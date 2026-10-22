package dev.rpgsolo.engine

/** Bloco de texto do livro: [heading] e linhas já formatadas (CP-04.1). */
data class BookSection(val heading: String, val lines: List<String>)

/** Conteúdo do "Livro da aventura", independente de formato (o PDF é desenhado pelo `app`). */
data class AdventureBook(val title: String, val subtitle: String, val sections: List<BookSection>)

/**
 * CP-04.1: monta o livro só do que o jogador já pode ver: resumos de capítulos, missões e linha do tempo.
 * Não lê o log nem o estado: não há segredos de NPC, relógios, fatos privados nem números de regra, e nada é alterado.
 */
object AdventureBookBuilder {
    fun build(
        campaignName: String,
        playerName: String?,
        chapters: List<Chapter>,
        quests: List<Quest>,
        timeline: List<Pair<Int, String>>
    ): AdventureBook {
        val chapterLines = chapters.sortedBy { it.number }.map { c ->
            val body = c.summary?.trim()?.takeIf { it.isNotEmpty() }
                ?: if (c.isOpen) "Capítulo em andamento." else "Sem resumo registrado."
            "Capítulo ${c.number} — ${c.title.trim()}\n$body"
        }
        val questLines = quests.map { q ->
            val status = when (q.status) {
                QuestStatus.ACTIVE -> "em andamento"
                QuestStatus.DONE -> "concluída"
                QuestStatus.FAILED -> "fracassada"
            }
            val goals = q.objectives.joinToString("\n") { (if (it.done) "[x] " else "[ ] ") + it.text.trim() }
            "${q.title.trim()} ($status)" + if (goals.isEmpty()) "" else "\n$goals"
        }
        val sections = listOf(
            BookSection("Capítulos", chapterLines),
            BookSection("Missões", questLines),
            BookSection("Diário da aventura", timeline.map { it.second.trim() }.filter { it.isNotEmpty() })
        ).filter { it.lines.isNotEmpty() }
        val name = campaignName.trim().ifEmpty { "Aventura" }
        return AdventureBook(name, playerName?.trim()?.takeIf { it.isNotEmpty() }?.let { "A história de $it" } ?: "Livro da aventura", sections)
    }
}
