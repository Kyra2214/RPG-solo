package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Outcome
import dev.rpgsolo.engine.QuestLog

enum class Author { PLAYER, ROLL, MASTER, SYSTEM }

data class ChatLine(val author: Author, val text: String)

/** Painel rápido (10.1.3): só o personagem do jogador. Nada de vida ou atributos de NPC (10.1.5). */
data class PanelState(
    val name: String,
    val resources: Map<String, Pair<Int, Int>>,
    val conditions: List<String>,
    val quest: String?,
    val inCombat: Boolean
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
    private val restResources: Set<String> = healthResource?.let { setOf(it) } ?: emptySet()
) {
    /** Uma jogada no chat: onde começa e, se foi ação local que gravou estado, a marca para desfazer. */
    private data class Entry(val start: Int, val localMark: Int? = null, val viaLoop: Boolean = true)

    private val chat = mutableListOf<ChatLine>()
    private val entries = ArrayDeque<Entry>()

    val lines: List<ChatLine> get() = chat.toList()

    /** Sugestões de ação da última narração (7.3.4). Opcionais; vazias após ação local ou desfazer tudo. */
    fun suggestions(): List<String> =
        if (entries.lastOrNull()?.viaLoop == true) loop.turns.lastOrNull()?.suggestions.orEmpty() else emptyList()

    fun submit(text: String): PlayResult {
        if (text.isBlank()) return PlayResult.Failed("texto vazio")
        val start = chat.size
        chat += ChatLine(Author.PLAYER, text.trim())
        LocalActions.match(text)?.let { return submitLocal(it, start) }
        val result = loop.play(session, PlayInput(playerId, text, checkFor(text), healthResource))
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
            conditions = session.state.conditions[playerId].orEmpty().sorted(),
            quest = quests?.active()?.firstOrNull()?.title,
            inCombat = session.state.combat != null
        )
    }

    private fun truncateLastTurn() {
        val start = entries.removeLastOrNull()?.start ?: return
        while (chat.size > start) chat.removeAt(chat.size - 1)
    }

    private fun showTurn(r: PlayResult.Played) {
        r.record.check?.let { chat += ChatLine(Author.ROLL, rollText(it)) }
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

        /** Rolagem do motor, clara (10.1.4): dados, modificador, total e resultado. */
        fun rollText(c: CheckResult): String {
            val r = c.roll
            val mod = if (r.modifier == 0) "" else if (r.modifier > 0) " +${r.modifier}" else " ${r.modifier}"
            return "${c.spec.label}: ${r.expression.takeWhile { ch -> ch != '+' && ch != '-' }} [${r.dice.joinToString(", ")}]$mod = ${r.total} → ${word(c.outcome)}"
        }
    }
}
