package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.ClashRecorded
import dev.rpgsolo.engine.ClashReport
import dev.rpgsolo.engine.FortuneMode
import dev.rpgsolo.engine.Outcome
import dev.rpgsolo.engine.QuestLog
import dev.rpgsolo.engine.checkGroup
import dev.rpgsolo.engine.checkOpposed
import dev.rpgsolo.engine.removeConditionByCheck
import dev.rpgsolo.engine.successChance
import dev.rpgsolo.engine.survivalTest

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
    val plain: Boolean = false,
    /** Chance exata de sucesso pleno calculada pelo motor; null em rolagem simples, vantagem/desvantagem ou dados explosivos. */
    val chance: Double? = null,
    /** Por que o teste não é padrão (vantagem, desvantagem, dados explosivos, bônus ou penalidade), só do que o motor registrou no teste. */
    val chips: List<String> = emptyList(),
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
    val xp: Int = 0,
    /** MU-02.8: escudo atual do personagem (0 = sem escudo). */
    val shield: Int = 0,
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
    private val generationPreferences: () -> String? = { null },
    /** CP-03: briefing de retomada (ver [ResumeBriefing]); enviado só até a primeira narração depois de abrir a campanha. */
    private val resumeBriefing: () -> String? = { null },
    /** DC-05.1: trechos do documento-fonte para o texto da jogada (ver [SourceContext]). */
    private val sourceContext: (String) -> String? = { null },
) {
    /** Uma jogada no chat: onde começa e, se foi ação local que gravou estado, a marca para desfazer. */
    private data class Entry(val start: Int, val localMark: Int? = null, val viaLoop: Boolean = true)

    /** `chat` (lista sincronizada) é também o monitor de `entries` e `revealedRolls`: leituras da interface não esperam o turno. */
    private val chat: MutableList<ChatLine> = java.util.Collections.synchronizedList(mutableListOf<ChatLine>())
    private val entries = ArrayDeque<Entry>()

    /** Índices das rolagens que o jogador já abriu; pertence à sessão, não à composição da tela. */
    private val revealedRolls = mutableSetOf<Int>()

    val lines: List<ChatLine> get() = synchronized(chat) { chat.toList() }

    /** Executa uma ação local da interface (combate, inventário, lugares) com exclusividade de turno. Chamar fora da thread principal. */
    fun <T> exclusiveTurn(block: () -> T): T = loop.turnGate.exclusive(block)

    /** true enquanto uma jogada ou ação exclusiva está em andamento. */
    val busy: Boolean get() = loop.turnGate.busy

    /** Linha do tempo derivada do log persistido; segredos e estado privado de NPC não são incluídos. */
    fun automaticTimeline() = dev.rpgsolo.engine.automaticTimeline(session.events, playerId, session.state)

    /** Cartões derivados somente dos eventos persistidos; a UI nunca recalcula um confronto. */
    fun clashReports(): List<ClashReport> =
        session.events
            .filterIsInstance<ClashRecorded>()
            .takeLast(20)
            .map(ClashReport::of)

    /** Nome público do combatente; o ID só permanece no evento canônico. */
    fun combatantPublicName(entityId: String): String = session.state.entities[entityId]?.name ?: "combatente"

    fun isRollRevealed(index: Int): Boolean = synchronized(chat) { index in revealedRolls }

    /** Marca uma rolagem como vista depois que a animação visual terminou. */
    fun revealRoll(index: Int): Boolean {
        synchronized(chat) {
            if (index !in chat.indices || chat[index].roll == null) return false
            return revealedRolls.add(index)
        }
    }

    /** Sugestões de ação da última narração (7.3.4). Opcionais; vazias após ação local ou desfazer tudo. */
    fun suggestions(): List<String> =
        if (synchronized(chat) { entries.lastOrNull()?.viaLoop } == true) loop.turns.lastOrNull()?.suggestions.orEmpty() else emptyList()

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
        val level =
            when {
                !inCombat -> SheetLevel.BRIEF
                previous == SheetLevel.FULL || previous == SheetLevel.ROUND -> SheetLevel.ROUND
                else -> SheetLevel.FULL
            }
        return level to s.render(session.state, playerId, level)
    }

    fun submit(
        text: String,
        onNarrativeChunk: (String) -> Unit = {},
    ): PlayResult = loop.turnGate.exclusive { submitLocked(text, onNarrativeChunk) }

    private fun submitLocked(
        text: String,
        onNarrativeChunk: (String) -> Unit,
    ): PlayResult {
        if (text.isBlank()) return PlayResult.Failed("texto vazio")
        if (loop.pending() != null) return PlayResult.Failed("o mestre pediu uma rolagem: role-a antes de continuar")
        val start = chat.size
        chat += ChatLine(Author.PLAYER, text.trim())
        LocalActions.match(text)?.let { return submitLocal(it, start) }
        val sheetNow = sheetNow()
        val result =
            loop.play(
                session,
                PlayInput(
                    playerId, text, checkFor(text), healthResource,
                    ruleSnippets = ruleLookup(text).take(5),
                    chapterSummary = chapterSummary(),
                    npcPrivate = npcContext(text),
                    sceneNpcIds = sceneNpcIds(text),
                    sourceContext = sourceContext(text),
                    worldContext = worldContext(),
                    resumeBriefing = resumeBriefing(),
                    generationPreferences = generationPreferences(),
                    playerSheet = sheetNow?.second,
                    sheetLevel = sheetNow?.first,
                ),
                onNarrativeChunk = onNarrativeChunk,
            )
        when (result) {
            is PlayResult.Played -> {
                synchronized(chat) { entries.addLast(Entry(start)) }
                showTurn(result)
            }
            is PlayResult.Failed -> chat += ChatLine(Author.SYSTEM, result.reason)
        }
        return result
    }

    /** O jogador rola o teste pedido pelo mestre: o motor rola e o mestre narra o desfecho. Chamada de rede. */
    fun rollPending(
        useFortuneResource: Boolean = false,
        onNarrativeChunk: (String) -> Unit = {},
    ): PlayResult = loop.turnGate.exclusive { rollPendingLocked(useFortuneResource, onNarrativeChunk) }

    private fun rollPendingLocked(
        useFortuneResource: Boolean,
        onNarrativeChunk: (String) -> Unit,
    ): PlayResult {
        val pending = loop.pending() ?: return PlayResult.Failed("não há teste pendente")
        val start = chat.size
        val sheetNow = sheetNow()
        val result =
            loop.resolvePending(
                session,
                PlayInput(
                    playerId, pending.playerText, null, healthResource,
                    ruleSnippets = ruleLookup(pending.playerText).take(5),
                    chapterSummary = chapterSummary(),
                    npcPrivate = npcContext(pending.playerText),
                    sceneNpcIds = sceneNpcIds(pending.playerText),
                    sourceContext = sourceContext(pending.playerText),
                    worldContext = worldContext(),
                    resumeBriefing = resumeBriefing(),
                    generationPreferences = generationPreferences(),
                    playerSheet = sheetNow?.second,
                    sheetLevel = sheetNow?.first,
                ),
                useFortuneResource = useFortuneResource,
                onNarrativeChunk = onNarrativeChunk,
            )
        when (result) {
            is PlayResult.Played -> {
                synchronized(chat) { entries.addLast(Entry(start)) }
                showTurn(result)
            }
            is PlayResult.Failed -> chat += ChatLine(Author.SYSTEM, result.reason)
        }
        return result
    }

    /** Sorte pós-rolagem disponível para o último teste (F3), ou null. */
    fun luckOffer(): LuckOption? = loop.luckOffer(session, playerId)

    /** Gasta sorte no último teste e narra de novo o novo resultado. O chat troca as rolagens e a narração dessa jogada. Chamada de rede. */
    fun spendLuck(
        kind: dev.rpgsolo.engine.LuckKind,
        onNarrativeChunk: (String) -> Unit = {},
    ): PlayResult = loop.turnGate.exclusive { spendLuckLocked(kind, onNarrativeChunk) }

    private fun spendLuckLocked(
        kind: dev.rpgsolo.engine.LuckKind,
        onNarrativeChunk: (String) -> Unit,
    ): PlayResult {
        if (synchronized(chat) { entries.lastOrNull()?.viaLoop } != true) {
            val reason = "a última jogada não tem teste para alterar"
            chat += ChatLine(Author.SYSTEM, reason)
            return PlayResult.Failed(reason)
        }
        val result = loop.spendLuck(session, playerId, kind, onNarrativeChunk)
        when (result) {
            is PlayResult.Played -> {
                synchronized(chat) {
                    var i = entries.last().start
                    if (i < chat.size && chat[i].author == Author.PLAYER) i++
                    while (chat.size > i) chat.removeAt(chat.size - 1)
                    revealedRolls.removeAll { it >= chat.size }
                }
                showTurn(result)
            }
            is PlayResult.Failed -> {
                if (loop.turns.size < synchronized(chat) { entries.count { it.viaLoop } }) truncateLastTurn()
                chat += ChatLine(Author.SYSTEM, result.reason)
            }
        }
        return result
    }

    /** Ação simples sem IA (6.4.3). Não chama o provedor e não rola dados. */
    private fun submitLocal(
        action: LocalAction,
        start: Int,
    ): PlayResult {
        val mark = session.mark()
        return when (val r = LocalActions.run(action, session, playerId, restResources)) {
            is LocalResult.Done -> {
                synchronized(chat) { entries.addLast(Entry(start, localMark = if (r.changedState) mark else null, viaLoop = false)) }
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
    fun undo(): Boolean = loop.turnGate.exclusive { undoLocked() }

    private fun undoLocked(): Boolean {
        val last = synchronized(chat) { entries.lastOrNull() } ?: return false
        if (last.viaLoop) {
            if (!loop.undo(session)) return false
        } else {
            last.localMark?.let { session.rollbackTo(it) }
        }
        truncateLastTurn()
        return true
    }

    /** Nova narração do mesmo resultado; a rolagem mostrada continua a mesma. */
    fun regenerate(): PlayResult = loop.turnGate.exclusive { regenerateLocked() }

    private fun regenerateLocked(): PlayResult {
        if (synchronized(chat) { entries.lastOrNull()?.viaLoop } == false) {
            val reason = "a última jogada foi uma ação local, sem narração para refazer"
            chat += ChatLine(Author.SYSTEM, reason)
            return PlayResult.Failed(reason)
        }
        val result = loop.regenerate(session)
        when (result) {
            is PlayResult.Played -> {
                synchronized(chat) {
                    val i = chat.indexOfLast { it.author == Author.MASTER }
                    if (i >= 0) chat.removeAt(i)
                    chat += ChatLine(Author.MASTER, result.record.narrative)
                }
            }
            is PlayResult.Failed -> {
                truncateLastTurn()
                chat += ChatLine(Author.SYSTEM, result.reason)
            }
        }
        return result
    }

    /**
     * Resolve uma disputa com especificações mecânicas explícitas do pacote.
     * Registra ambas as rolagens; o motor determina o vencedor e o empate favorece o defensor.
     */
    fun resolveOpposed(
        challenger: CheckSpec,
        defender: CheckSpec,
        challengerName: String = challenger.label,
        defenderName: String = defender.label,
        challengerEntityId: String = playerId,
        defenderEntityId: String? = null,
    ): PlayResult =
        loop.turnGate.exclusive {
            resolveOpposedLocked(challenger, defender, challengerName, defenderName, challengerEntityId, defenderEntityId)
        }

    private fun resolveOpposedLocked(
        challenger: CheckSpec,
        defender: CheckSpec,
        challengerName: String,
        defenderName: String,
        challengerEntityId: String,
        defenderEntityId: String?,
    ): PlayResult {
        if (loop.pending() != null) return PlayResult.Failed("resolva o pedido pendente antes da disputa")
        if (challengerName.isBlank() || defenderName.isBlank()) {
            return PlayResult.Failed("cada participante da disputa precisa de um nome não vazio")
        }
        if (defenderEntityId != null && challengerEntityId == defenderEntityId) {
            return PlayResult.Failed("uma disputa exige dois participantes diferentes")
        }
        val challengerEntity =
            session.state.entities[challengerEntityId]
                ?: return PlayResult.Failed("participante desafiante inexistente: $challengerEntityId")
        if (!challengerEntity.alive) return PlayResult.Failed("o participante desafiante está fora de combate")
        if (defenderEntityId != null) {
            val defenderEntity =
                session.state.entities[defenderEntityId]
                    ?: return PlayResult.Failed("participante defensor inexistente: $defenderEntityId")
            if (defenderEntity.kind != dev.rpgsolo.engine.EntityKind.NPC || !defenderEntity.alive) {
                return PlayResult.Failed("o defensor deve ser um NPC vivo")
            }
        }
        val start = chat.size
        val mark = session.mark()
        return try {
            val preparedChallenger =
                loop.prepareAdvancedCheck(session, challenger, challengerEntityId)
                    ?: return PlayResult.Failed("participante desafiante inexistente: $challengerEntityId")
            val preparedDefender =
                defenderEntityId?.let { id ->
                    loop.prepareAdvancedCheck(session, defender, id)
                        ?: return PlayResult.Failed("participante defensor inexistente: $id")
                } ?: defender
            val result = session.checkOpposed(preparedChallenger, preparedDefender)
            synchronized(chat) { entries.addLast(Entry(start, localMark = mark, viaLoop = false)) }
            chat += ChatLine(Author.SYSTEM, "Disputa: $challengerName contra $defenderName.")
            result.challengerAlternatives.forEach {
                    alternative ->
                chat += ChatLine(Author.ROLL, "Não escolhido · $challengerName · " + rollText(alternative), rollView(alternative))
            }
            chat += ChatLine(Author.ROLL, "$challengerName · " + rollText(result.challenger), rollView(result.challenger))
            result.defenderAlternatives.forEach {
                    alternative ->
                chat += ChatLine(Author.ROLL, "Não escolhido · $defenderName · " + rollText(alternative), rollView(alternative))
            }
            chat += ChatLine(Author.ROLL, "$defenderName · " + rollText(result.defender), rollView(result.defender))
            chat +=
                ChatLine(
                    Author.MASTER,
                    if (result.challengerWins) {
                        "$challengerName vence a disputa."
                    } else {
                        "$defenderName vence ou mantém a disputa; empate favorece o defensor."
                    },
                )
            PlayResult.Played(
                TurnRecord(
                    PlayInput(playerId, "disputa: $challengerName vs. $defenderName"),
                    mark,
                    session.mark(),
                    result.challenger,
                    if (result.challengerWins) "$challengerName vence a disputa." else "$defenderName vence ou mantém a disputa.",
                    advancedChecks =
                        listOf(
                            challengerName to result.challenger,
                            defenderName to result.defender,
                        ),
                ),
            )
        } catch (e: Exception) {
            runCatching { session.rollbackTo(mark) }
            PlayResult.Failed(e.message ?: "falha ao resolver a disputa")
        }
    }

    /**
     * Resolve uma tarefa em grupo com limiar explícito. Cada participante respeita a política
     * de fortuna de sua especificação; o motor conta sucessos e decide se o grupo passou.
     */
    fun resolveGroup(
        checks: List<CheckSpec>,
        requiredSuccesses: Int,
        participantNames: List<String> = checks.map { it.label },
        participantEntityIds: List<String?> = listOf(playerId) + List((checks.size - 1).coerceAtLeast(0)) { null },
    ): PlayResult =
        loop.turnGate.exclusive {
            resolveGroupLocked(checks, requiredSuccesses, participantNames, participantEntityIds)
        }

    private fun resolveGroupLocked(
        checks: List<CheckSpec>,
        requiredSuccesses: Int,
        participantNames: List<String>,
        participantEntityIds: List<String?>,
    ): PlayResult {
        if (loop.pending() != null) return PlayResult.Failed("resolva o pedido pendente antes do teste de grupo")
        if (checks.size < 2) {
            return PlayResult.Failed("teste de grupo exige pelo menos dois participantes")
        }
        if (requiredSuccesses !in 1..checks.size) {
            return PlayResult.Failed("sucessos necessários devem ficar entre 1 e o número de participantes")
        }
        if (participantNames.size != checks.size || participantEntityIds.size != checks.size) {
            return PlayResult.Failed("cada participante precisa de nome e referência correspondentes")
        }
        if (participantNames.any { it.isBlank() }) {
            return PlayResult.Failed("cada participante precisa de um nome não vazio")
        }
        val referencedIds = participantEntityIds.filterNotNull()
        if (referencedIds.distinct().size != referencedIds.size) {
            return PlayResult.Failed("um participante não pode ser incluído mais de uma vez")
        }
        if (playerId !in referencedIds) {
            return PlayResult.Failed("o teste de grupo precisa incluir o personagem do jogador")
        }
        for (entityId in referencedIds) {
            val entity =
                session.state.entities[entityId]
                    ?: return PlayResult.Failed("participante inexistente: $entityId")
            if (!entity.alive) return PlayResult.Failed("participante fora de combate: $entityId")
            if (entityId != playerId && entity.kind != dev.rpgsolo.engine.EntityKind.NPC) {
                return PlayResult.Failed("participante de grupo deve ser o jogador ou um NPC: $entityId")
            }
        }
        val start = chat.size
        val mark = session.mark()
        return try {
            val preparedChecks =
                checks.mapIndexed { index, spec ->
                    val entityId = participantEntityIds[index]
                    if (entityId == null) {
                        spec
                    } else {
                        loop.prepareAdvancedCheck(session, spec, entityId)
                            ?: return PlayResult.Failed("participante inexistente: $entityId")
                    }
                }
            val result = session.checkGroup(preparedChecks, requiredSuccesses)
            synchronized(chat) { entries.addLast(Entry(start, localMark = mark, viaLoop = false)) }
            chat += ChatLine(Author.SYSTEM, "Teste de grupo: ${checks.size} participantes; necessários ${result.requiredSuccesses} sucessos.")
            result.individual.forEachIndexed { index, check ->
                result.alternatives.getOrNull(index).orEmpty().forEach { alternative ->
                    chat += ChatLine(Author.ROLL, "Não escolhido · " + participantNames[index] + " · " + rollText(alternative), rollView(alternative))
                }
                chat += ChatLine(Author.ROLL, participantNames[index] + " · " + rollText(check), rollView(check))
            }
            chat +=
                ChatLine(
                    Author.MASTER,
                    if (result.passed) {
                        "O grupo alcançou o limiar de sucesso."
                    } else {
                        "O grupo não alcançou o limiar de sucesso."
                    },
                )
            PlayResult.Played(
                TurnRecord(
                    PlayInput(playerId, "teste de grupo (${checks.size} participantes)"),
                    mark,
                    session.mark(),
                    result.individual[participantEntityIds.indexOf(playerId)],
                    if (result.passed) "grupo passou" else "grupo falhou",
                    advancedChecks =
                        result.individual.mapIndexed { index, check ->
                            participantNames[index] to check
                        },
                ),
            )
        } catch (e: Exception) {
            runCatching { session.rollbackTo(mark) }
            PlayResult.Failed(e.message ?: "falha ao resolver o teste de grupo")
        }
    }

    /**
     * Ativa um poder já traduzido pelo pacote ([PowerMenu]). Custo, teste e efeito são do motor; o chat mostra o
     * resultado e a jogada pode ser desfeita como as outras ações locais.
     */
    fun activatePower(
        spec: dev.rpgsolo.engine.PowerSpec,
        powerName: String,
        userName: String,
        targetName: String?,
    ): PlayResult = loop.turnGate.exclusive { activatePowerLocked(spec, powerName, userName, targetName) }

    private fun activatePowerLocked(
        spec: dev.rpgsolo.engine.PowerSpec,
        powerName: String,
        userName: String,
        targetName: String?,
    ): PlayResult {
        if (loop.pending() != null) return PlayResult.Failed("resolva o pedido pendente antes de usar um poder")
        val start = chat.size
        val mark = session.mark()
        return try {
            when (val result = session.activatePower(spec)) {
                is dev.rpgsolo.engine.PowerResult.Rejected -> {
                    chat += ChatLine(Author.SYSTEM, result.reason)
                    PlayResult.Failed(result.reason)
                }
                is dev.rpgsolo.engine.PowerResult.Activated -> {
                    synchronized(chat) { entries.addLast(Entry(start, localMark = mark, viaLoop = false)) }
                    val summary = addPowerLines(powerName, userName, targetName, spec, result)
                    PlayResult.Played(TurnRecord(PlayInput(playerId, "poder: $powerName"), mark, session.mark(), result.check, summary))
                }
            }
        } catch (e: Exception) {
            runCatching { session.rollbackTo(mark) }
            PlayResult.Failed(e.message ?: "falha ao ativar o poder")
        }
    }

    /**
     * Gasta um uso de um traço com usos limitados ([limit] vem do pacote). Só o motor decide se há uso disponível;
     * desfazer reverte como as outras ações locais.
     */
    fun spendTraitUse(
        traitId: String,
        traitName: String,
        limit: Int,
        userName: String,
    ): PlayResult = loop.turnGate.exclusive { spendTraitUseLocked(traitId, traitName, limit, userName) }

    private fun spendTraitUseLocked(
        traitId: String,
        traitName: String,
        limit: Int,
        userName: String,
    ): PlayResult {
        if (loop.pending() != null) return PlayResult.Failed("resolva o pedido pendente antes de usar um traço")
        val start = chat.size
        val mark = session.mark()
        return try {
            when (val r = session.execute(dev.rpgsolo.engine.SpendTraitUse(playerId, traitId, limit))) {
                is dev.rpgsolo.engine.ExecResult.Rejected -> {
                    chat += ChatLine(Author.SYSTEM, r.reason)
                    PlayResult.Failed(r.reason)
                }
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    synchronized(chat) { entries.addLast(Entry(start, localMark = mark, viaLoop = false)) }
                    val used = session.state.traitUses[playerId]?.get(traitId) ?: 0
                    val summary = "$userName usa $traitName ($used/$limit)."
                    chat += ChatLine(Author.SYSTEM, summary)
                    PlayResult.Played(TurnRecord(PlayInput(playerId, "traço: $traitName"), mark, session.mark(), null, summary))
                }
            }
        } catch (e: Exception) {
            runCatching { session.rollbackTo(mark) }
            PlayResult.Failed(e.message ?: "falha ao usar o traço")
        }
    }

    /**
     * Teste de sobrevivência do herói caído (M-04.5). O motor rola e aplica (reergue, mantém ou conta falha/morte);
     * chat e desfazer como as outras ações locais. Sem chamada de IA.
     */
    fun survivalTest(
        spec: dev.rpgsolo.engine.CheckSpec,
        failuresToDie: Int,
        userName: String,
    ): PlayResult = loop.turnGate.exclusive { survivalTestLocked(spec, failuresToDie, userName) }

    private fun survivalTestLocked(
        spec: dev.rpgsolo.engine.CheckSpec,
        failuresToDie: Int,
        userName: String,
    ): PlayResult {
        if (loop.pending() != null) return PlayResult.Failed("resolva o pedido pendente antes do teste de sobrevivência")
        val start = chat.size
        val mark = session.mark()
        return try {
            when (val r = session.survivalTest(playerId, spec, failuresToDie)) {
                is dev.rpgsolo.engine.SurvivalResult.Rejected -> {
                    chat += ChatLine(Author.SYSTEM, r.reason)
                    PlayResult.Failed(r.reason)
                }
                else -> {
                    synchronized(chat) { entries.addLast(Entry(start, localMark = mark, viaLoop = false)) }
                    val summary =
                        when (r) {
                            is dev.rpgsolo.engine.SurvivalResult.Recovered -> "$userName passa no teste de sobrevivência e se reergue."
                            is dev.rpgsolo.engine.SurvivalResult.Stable -> "$userName se mantém caído, mas estável."
                            is dev.rpgsolo.engine.SurvivalResult.Failed ->
                                if (r.died) {
                                    "$userName falha no teste de sobrevivência (${r.failures}/$failuresToDie) e morre."
                                } else {
                                    "$userName falha no teste de sobrevivência (${r.failures}/$failuresToDie)."
                                }
                            is dev.rpgsolo.engine.SurvivalResult.Rejected -> r.reason
                        }
                    chat += ChatLine(Author.SYSTEM, summary)
                    PlayResult.Played(TurnRecord(PlayInput(playerId, "sobrevivência"), mark, session.mark(), null, summary))
                }
            }
        } catch (e: Exception) {
            runCatching { session.rollbackTo(mark) }
            PlayResult.Failed(e.message ?: "falha no teste de sobrevivência")
        }
    }

    /** Tenta remover uma condição do herói por teste do pacote (M-05); chat e desfazer como as ações locais. */
    fun removeConditionByCheck(
        condition: String,
        conditionName: String,
        spec: dev.rpgsolo.engine.CheckSpec,
        userName: String,
    ): PlayResult = loop.turnGate.exclusive { removeConditionLocked(condition, conditionName, spec, userName) }

    private fun removeConditionLocked(
        condition: String,
        conditionName: String,
        spec: dev.rpgsolo.engine.CheckSpec,
        userName: String,
    ): PlayResult {
        if (loop.pending() != null) return PlayResult.Failed("resolva o pedido pendente antes de tentar remover a condição")
        val start = chat.size
        val mark = session.mark()
        return try {
            when (val r = session.removeConditionByCheck(playerId, condition, spec)) {
                is dev.rpgsolo.engine.ConditionRemovalResult.Rejected -> {
                    chat += ChatLine(Author.SYSTEM, r.reason)
                    PlayResult.Failed(r.reason)
                }
                else -> {
                    synchronized(chat) { entries.addLast(Entry(start, localMark = mark, viaLoop = false)) }
                    val summary =
                        if (r is dev.rpgsolo.engine.ConditionRemovalResult.Removed) {
                            "$userName se livra de $conditionName."
                        } else {
                            "$userName não consegue se livrar de $conditionName."
                        }
                    chat += ChatLine(Author.SYSTEM, summary)
                    PlayResult.Played(TurnRecord(PlayInput(playerId, "remover: $conditionName"), mark, session.mark(), null, summary))
                }
            }
        } catch (e: Exception) {
            runCatching { session.rollbackTo(mark) }
            PlayResult.Failed(e.message ?: "falha ao tentar remover a condição")
        }
    }

    /** Mostra no chat o uso do poder (custo, teste, efeito) e devolve o resumo do efeito. */
    private fun addPowerLines(
        powerName: String,
        userName: String,
        targetName: String?,
        spec: dev.rpgsolo.engine.PowerSpec,
        result: dev.rpgsolo.engine.PowerResult.Activated,
    ): String {
        val costText = if (result.spent > 0) " (custo ${result.spent} de ${spec.resource})" else ""
        chat += ChatLine(Author.SYSTEM, "$userName usa $powerName" + (targetName?.let { " em $it" } ?: "") + costText + ".")
        result.check?.let { chat += ChatLine(Author.ROLL, rollText(it), rollView(it)) }
        val summary =
            when {
                !result.effectApplied -> "$powerName falha: o custo foi gasto, sem efeito."
                result.damageRoll != null -> {
                    val r = result.damageRoll!!
                    "Dano: ${r.expression} [${r.dice.joinToString(", ")}] = ${r.total}; ${-result.resourceDelta} aplicado."
                }
                result.resourceDelta > 0 -> "$powerName cura ${result.resourceDelta}."
                else -> "$powerName faz efeito."
            }
        chat += ChatLine(Author.SYSTEM, summary)
        return summary
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
            xp = e.xp,
            shield = e.shield,
        )
    }

    private fun truncateLastTurn() {
        synchronized(chat) {
            val start = entries.removeLastOrNull()?.start ?: return
            while (chat.size > start) chat.removeAt(chat.size - 1)
            revealedRolls.removeAll { it >= chat.size }
        }
    }

    private fun showTurn(r: PlayResult.Played) {
        // Rolagem com tabela (ex.: d6 do dano) não tem dificuldade nem sucesso/falha: o cartão mostra só o dado.
        val plain = r.record.input.resolving is PendingRoll
        // Mostra a rolagem descartada para transparência, mas deixa o resultado escolhido
        // como a única rolagem efetiva usada pela narração e pelas consequências.
        r.record.fortuneAlternatives.forEach { alternative ->
            chat += ChatLine(Author.ROLL, "Não escolhido · " + rollText(alternative, plain), rollView(alternative, plain))
        }
        if (r.record.advancedChecks.isNotEmpty()) {
            r.record.advancedChecks.forEach { (participant, result) ->
                chat += ChatLine(Author.ROLL, participant + " · " + rollText(result), rollView(result))
            }
        } else {
            r.record.check?.let { chat += ChatLine(Author.ROLL, rollText(it, plain), rollView(it, plain)) }
        }
        r.record.powerUse?.let { addPowerLines(it.powerName, it.userName, it.targetName, it.spec, it.result) }
        chat += ChatLine(Author.MASTER, r.record.narrative)
    }

    companion object {
        private fun word(o: Outcome) =
            when (o) {
                Outcome.CRITICAL_FAILURE -> "falha crítica"
                Outcome.FAILURE -> "falha"
                Outcome.PARTIAL_SUCCESS -> "sucesso parcial"
                Outcome.SUCCESS -> "sucesso"
                Outcome.CRITICAL_SUCCESS -> "sucesso crítico"
            }

        /** Dados do motor para a animação da tela (10.1.4). */
        fun rollView(
            c: CheckResult,
            plain: Boolean = false,
        ) = RollView(
            label = c.spec.label, sides = c.spec.diceSides, dice = c.roll.dice, modifier = c.roll.modifier,
            total = c.roll.total, difficulty = c.spec.difficulty, outcome = c.outcome,
            outcomeWord = if (plain) "" else word(c.outcome), margin = c.margin, plain = plain,
            chance = if (plain || c.spec.fortuneMode != FortuneMode.NORMAL || c.spec.explodeOnMax) null else c.spec.successChance(),
            chips = if (plain) emptyList() else rollChips(c.spec),
        )

        /** Causas visíveis da mudança no teste, a partir do próprio [CheckSpec]; a origem do bônus (traço, condição) não é registrada no teste. */
        fun rollChips(spec: CheckSpec): List<String> =
            buildList {
                when (spec.fortuneMode) {
                    FortuneMode.ADVANTAGE -> add("vantagem")
                    FortuneMode.DISADVANTAGE -> add("desvantagem")
                    FortuneMode.NORMAL -> Unit
                }
                if (spec.explodeOnMax) add("dados explosivos")
                if (spec.modifier > 0) add("bônus +${spec.modifier}")
                if (spec.modifier < 0) add("penalidade ${spec.modifier}")
            }

        /** Rolagem do motor, clara (10.1.4): dados, modificador, total e resultado. */
        fun rollText(
            c: CheckResult,
            plain: Boolean = false,
        ): String {
            val r = c.roll
            if (plain) return "${c.spec.label}: ${r.expression} [${r.dice.joinToString(", ")}] = ${r.total}"
            val mod =
                if (r.modifier == 0) {
                    ""
                } else if (r.modifier > 0) {
                    " +${r.modifier}"
                } else {
                    " ${r.modifier}"
                }
            return "${c.spec.label}: ${r.expression.takeWhile {
                    ch ->
                ch != '+' && ch != '-'
            }} [${r.dice.joinToString(", ")}]$mod = ${r.total} → ${word(c.outcome)}"
        }
    }
}
