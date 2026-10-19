package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Outcome
import dev.rpgsolo.engine.QuestLog

enum class Author { PLAYER, ROLL, MASTER, SYSTEM }

/**
 * Rolagem pronta para a tela animar. Os números já foram decididos pelo motor; a animação é só visual
 * e sempre termina exatamente em [dice].
 */
data class RollView(
    val label: String,
    val sides: Int,
    val dice: List<Int>,
    val modifier: Int,
    val total: Int,
    val difficulty: Int,
    val outcome: Outcome,
    val outcomeWord: String,
    /** Margem positiva = passou; negativa = faltou. O motor continua sendo a fonte deste valor. */
    val margin: Int = 0,
    /** Rolagem simples (sem dificuldade nem sucesso/falha): a tela mostra só o dado. */
    val plain: Boolean = false
)

/** [roll] só vem preenchido nas linhas ROLL; [text] continua com a versão em texto da mesma rolagem. */
data class ChatLine(val author: Author, val text: String, val roll: RollView? = null)

/** Painel rápido (10.1.3): só o personagem do jogador. Nada de vida ou atributos de NPC (10.1.5). */
data class PanelState(
    val name: String,
    val resources: Map<String, Pair<Int, Int>>,
    val conditions: List<String>,
    val quest: String?,
    val inCombat: Boolean,
    val level: Int = 1,
    val xp: Int = 0
)

/**
 * Lógica da aba Jogar (Fase 10.1), em Kotlin puro para ser testável sem Android.
 * A tela só desenha [lines] e [panel] e chama [submit], [undo] e [regenerate] fora da thread principal.
 */
class PlayController(
    private val session: CampaignSession,
    private val loop: GameLoop,
    private val playerId: String,
    private val healthResource: String? = null,
    /** Escolhe o teste da ação (classificador de intenção, 7.1.2). Padrão: ação sem teste. */
    private val checkFor: (String) -> CheckSpec? = { null },
    private val quests: QuestLog? = null,
    /** Recursos que "descansar" recupera (6.4.3). Padrão: só o de saúde; vazio = descanso recusado. */
    private val restResources: Set<String> = healthResource?.let { setOf(it) } ?: emptySet(),
    /** Ficha do jogador para o mestre ver (vantagens, desvantagens, atributos...). null = o mestre só vê a saúde em palavras. */
    private val sheet: PlayerSheet? = null,
    /** id da condição -> nome para mostrar no painel (ex.: "visao-turva" -> "Visão turva"). Sem entrada, mostra o id. */
    private val conditionNames: Map<String, String> = emptyMap(),
    /** Busca só regras relevantes no pacote ativo (4.2.3/7.2.2). */
    private val ruleLookup: (String) -> List<String> = { emptyList() },
    /** Resumo do capítulo já encerrado, sem mandar todo o histórico à IA. */
    private val chapterSummary: () -> String? = { null },
    /** Fichas privadas dos NPCs que participam do combate atual; nunca renderizadas na interface do jogador. */
    private val npcContext: (String) -> List<String> = { emptyList() },
    /** NPCs presentes: todos os combatentes ou os mencionados explicitamente na ação atual. */
    private val sceneNpcIds: (String) -> Set<String> = { emptySet() },
    private val worldContext: () -> String? = { null },
    private val generationPreferences: () -> String? = { null }
) {
    /** Uma jogada no chat: onde começa e, se foi ação local que gravou estado, a marca para desfazer. */
    private data class Entry(val start: Int, val localMark: Int? = null, val viaLoop: Boolean = true)

    private val chat = mutableListOf<ChatLine>()
    private val entries = ArrayDeque<Entry>()
    /** Índices das rolagens que o jogador já abriu; pertence à sessão, não à composição da tela. */
    private val revealedRolls = mutableSetOf<Int>()

    val lines: List<ChatLine> get() = chat.toList()

    /** Linha do tempo derivada do log persistido; segredos e estado privado de NPC não são incluídos. */
    fun automaticTimeline() = dev.rpgsolo.engine.automaticTimeline(session.engine.events, playerId, session.state)

    fun isRollRevealed(index: Int): Boolean = index in revealedRolls

    /** Marca uma rolagem como vista depois que a animação visual terminou. */
    fun revealRoll(index: Int): Boolean {
        if (index !in chat.indices || chat[index].roll == null) return false
        return revealedRolls.add(index)
    }

    /** Sugestões de ação da última narração (7.3.4). Opcionais; vazias após ação local ou desfazer tudo. */
    fun suggestions(): List<String> =
        if (entries.lastOrNull()?.viaLoop == true) loop.turns.lastOrNull()?.suggestions.orEmpty() else emptyList()

    /** Teste ou rolagem que o mestre pediu e o jogador ainda não rolou. A tela mostra o botão de rolar enquanto houver um. */
    fun pending(): Pending? = loop.pending()

    /** Opção paga, definida pelo pacote, para ativar vantagem/desvantagem no teste pendente. */
    fun pendingFortuneSpendOption(): FortuneSpendOption? {
        val option = (loop.pending() as? PendingCheck)?.fortuneSpendOption ?: return null
        val current = session.state.entities[playerId]?.resources?.get(option.resource)?.current ?: return null
        return option.takeIf { current >= it.cost }
    }

    /**
     * Quanto da ficha enviar agora (cada pedido à IA é independente):
     *  - fora de combate: resumo curto;
     *  - primeira jogada do combate: ficha completa, com as descrições dos traços;
     *  - demais jogadas do combate: só o status da rodada.
     * Deriva do histórico, então desfazer ou refazer a jogada recalcula sozinho.
     */
    private fun sheetNow(): Pair<SheetLevel, String?>? {
        val s = sheet ?: return null
        val inCombat = session.state.combat != null
        val previous = loop.turns.lastOrNull()?.input?.sheetLevel
        val level = when {
            !inCombat -> SheetLevel.BRIEF
            previous == SheetLevel.FULL || previous == SheetLevel.ROUND -> SheetLevel.ROUND
            else -> SheetLevel.FULL
        }
        return level to s.render(session.state, playerId, level)
    }

    fun submit(text: String, onNarrativeChunk: (String) -> Unit = {}): PlayResult {
        if (text.isBlank()) return PlayResult.Failed("texto vazio")
        if (loop.pending() != null) return PlayResult.Failed("o mestre pediu uma rolagem: role-a antes de continuar")
        val start = chat.size
        chat += ChatLine(Author.PLAYER, text.trim())
        LocalActions.match(text)?.let { return submitLocal(it, start) }
        val sheetNow = sheetNow()
        val result = loop.play(
            session,
            PlayInput(
                playerId, text, checkFor(text), healthResource,
                ruleSnippets = ruleLookup(text).take(5),
                chapterSummary = chapterSummary(),
                npcPrivate = npcContext(text),
                sceneNpcIds = sceneNpcIds(text),
                worldContext = worldContext(),
                generationPreferences = generationPreferences(),
                playerSheet = sheetNow?.second,
                sheetLevel = sheetNow?.first
            ),
            onNarrativeChunk = onNarrativeChunk
        )
        when (result) {
            is PlayResult.Played -> {
                entries.addLast(Entry(start))
                showTurn(result)
            }
            is PlayResult.Failed -> chat += ChatLine(Author.SYSTEM, result.reason)
        }
        return result
    }

    /** O jogador rola o teste pedido pelo mestre: o motor rola e o mestre narra o desfecho. Chamada de rede. */
    fun rollPending(useFortuneResource: Boolean = false, onNarrativeChunk: (String) -> Unit = {}): PlayResult {
        val pending = loop.pending() ?: return PlayResult.Failed("não há teste pendente")
        val start = chat.size
        val sheetNow = sheetNow()
        val result = loop.resolvePending(
            session,
            PlayInput(
                playerId, pending.playerText, null, healthResource,
                ruleSnippets = ruleLookup(pending.playerText).take(5),
                chapterSummary = chapterSummary(),
                npcPrivate = npcContext(pending.playerText),
                sceneNpcIds = sceneNpcIds(pending.playerText),
                worldContext = worldContext(),
                generationPreferences = generationPreferences(),
                playerSheet = sheetNow?.second,
                sheetLevel = sheetNow?.first
            ),
            useFortuneResource = useFortuneResource,
            onNarrativeChunk = onNarrativeChunk
        )
        when (result) {
            is PlayResult.Played -> {
                entries.addLast(Entry(start))
                showTurn(result)
            }
            is PlayResult.Failed -> chat += ChatLine(Author.SYSTEM, result.reason)
        }
        return result
    }

    /** Ação simples sem IA (6.4.3). Não chama o provedor e não rola dados. */
    private fun submitLocal(action: LocalAction, start: Int): PlayResult {
        val mark = session.mark()
        return when (val r = LocalActions.run(action, session, playerId, restResources)) {
            is LocalResult.Done -> {
                entries.addLast(Entry(start, localMark = if (r.changedState) mark else null, viaLoop = false))
                chat += ChatLine(Author.SYSTEM, r.text)
                PlayResult.Played(TurnRecord(PlayInput(playerId, chat[start].text), mark, session.mark(), null, r.text))
            }
            is LocalResult.Refused -> {
                chat += ChatLine(Author.SYSTEM, r.reason)
                PlayResult.Failed(r.reason)
            }
        }
    }

    /** Desfaz a última jogada, inclusive no chat. */
    fun undo(): Boolean {
        val last = entries.lastOrNull() ?: return false
        if (last.viaLoop) {
            if (!loop.undo(session)) return false
        } else {
            last.localMark?.let { session.rollbackTo(it) }
        }
        truncateLastTurn()
        return true
    }

    /** Nova narração do mesmo resultado; a rolagem mostrada continua a mesma. */
    fun regenerate(): PlayResult {
        if (entries.lastOrNull()?.viaLoop == false) {
            val reason = "a última jogada foi uma ação local, sem narração para refazer"
            chat += ChatLine(Author.SYSTEM, reason)
            return PlayResult.Failed(reason)
        }
        val result = loop.regenerate(session)
        when (result) {
            is PlayResult.Played -> {
                val i = chat.indexOfLast { it.author == Author.MASTER }
                if (i >= 0) chat.removeAt(i)
                chat += ChatLine(Author.MASTER, result.record.narrative)
            }
            is PlayResult.Failed -> {
                if (entries.isNotEmpty()) truncateLastTurn()
                chat += ChatLine(Author.SYSTEM, result.reason)
            }
        }
        return result
    }

    fun panel(): PanelState? {
        val e = session.state.entities[playerId] ?: return null
        return PanelState(
            name = e.name,
            resources = e.resources.mapValues { (_, r) -> r.current to r.max },
            conditions = session.state.conditions[playerId].orEmpty().sorted().map { conditionNames[it] ?: it },
            quest = quests?.active()?.firstOrNull()?.title,
            inCombat = session.state.combat != null,
            level = e.level,
            xp = e.xp
        )
    }

    private fun truncateLastTurn() {
        val start = entries.removeLastOrNull()?.start ?: return
        while (chat.size > start) chat.removeAt(chat.size - 1)
        revealedRolls.removeAll { it >= chat.size }
    }

    private fun showTurn(r: PlayResult.Played) {
        // Rolagem com tabela (ex.: d6 do dano) não tem dificuldade nem sucesso/falha: o cartão mostra só o dado.
        val plain = r.record.input.resolving is PendingRoll
        // Mostra a rolagem descartada para transparência, mas deixa o resultado escolhido
        // como a única rolagem efetiva usada pela narração e pelas consequências.
        r.record.fortuneAlternatives.forEach { alternative ->
            chat += ChatLine(Author.ROLL, "Não escolhido · " + rollText(alternative, plain), rollView(alternative, plain))
        }
        r.record.check?.let { chat += ChatLine(Author.ROLL, rollText(it, plain), rollView(it, plain)) }
        chat += ChatLine(Author.MASTER, r.record.narrative)
    }

    companion object {
        private fun word(o: Outcome) = when (o) {
            Outcome.CRITICAL_FAILURE -> "falha crítica"
            Outcome.FAILURE -> "falha"
            Outcome.PARTIAL_SUCCESS -> "sucesso parcial"
            Outcome.SUCCESS -> "sucesso"
            Outcome.CRITICAL_SUCCESS -> "sucesso crítico"
        }

        /** Dados do motor para a animação da tela (10.1.4). */
        fun rollView(c: CheckResult, plain: Boolean = false) = RollView(
            label = c.spec.label, sides = c.spec.diceSides, dice = c.roll.dice, modifier = c.roll.modifier,
            total = c.roll.total, difficulty = c.spec.difficulty, outcome = c.outcome,
            outcomeWord = if (plain) "" else word(c.outcome), margin = c.margin, plain = plain
        )

        /** Rolagem do motor, clara (10.1.4): dados, modificador, total e resultado. */
        fun rollText(c: CheckResult, plain: Boolean = false): String {
            val r = c.roll
            if (plain) return "${c.spec.label}: ${r.expression} [${r.dice.joinToString(", ")}] = ${r.total}"
            val mod = if (r.modifier == 0) "" else if (r.modifier > 0) " +${r.modifier}" else " ${r.modifier}"
            return "${c.spec.label}: ${r.expression.takeWhile { ch -> ch != '+' && ch != '-' }} [${r.dice.joinToString(", ")}]$mod = ${r.total} → ${word(c.outcome)}"
        }
    }
}
