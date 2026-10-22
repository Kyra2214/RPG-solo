package dev.rpgsolo.ai

import dev.rpgsolo.engine.Quest
import dev.rpgsolo.engine.QuestStatus

/**
 * CP-03: briefing de retomada, montado só de dados oficiais já salvos (missões, linha do tempo derivada do log e
 * resumos de capítulos). Vai ao mestre na primeira jogada depois de abrir a campanha, quando ainda não há cenas
 * recentes na memória. O estado mecânico (PV, condições, fatos, relações) continua vindo do estado oficial; este
 * texto nunca o substitui nem carrega números de regra.
 */
object ResumeBriefing {
    const val HEADER =
        "Retomada da campanha. O estado oficial (personagens, PV, condições, fatos, relações) vale sobre qualquer resumo; " +
            "este briefing é só memória narrativa e nunca substitui dados mecânicos."

    fun build(
        quests: List<Quest>,
        timeline: List<Pair<Int, String>>,
        chapterSummaries: List<String>,
        maxTimeline: Int = 8
    ): String? {
        val active = quests.filter { it.status == QuestStatus.ACTIVE }
        val activeText = active.takeIf { it.isNotEmpty() }?.let { list ->
            "Missões em andamento:\n" + list.joinToString("\n") { q ->
                val pending = q.objectives.filterNot { it.done }.map { it.text }
                "- ${q.title}" + if (pending.isEmpty()) "" else " (falta: ${pending.joinToString("; ")})"
            }
        }
        val chaptersText = chapterSummaries.takeLast(3).takeIf { it.isNotEmpty() }
            ?.let { "Capítulos anteriores:\n" + it.joinToString("\n") }
        val recentText = timeline.takeLast(maxTimeline).takeIf { it.isNotEmpty() }
            ?.let { "Acontecimentos mais recentes:\n" + it.joinToString("\n") { (_, text) -> "- $text" } }
        val sections = listOfNotNull(activeText, chaptersText, recentText)
        if (sections.isEmpty()) return null
        return (listOf(HEADER) + sections).joinToString("\n")
    }
}
