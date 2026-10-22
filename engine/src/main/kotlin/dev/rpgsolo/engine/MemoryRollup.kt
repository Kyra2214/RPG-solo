package dev.rpgsolo.engine

/**
 * I-02.4: mantém a memória útil sem depender do encerramento de capítulos ou de outra chamada à IA.
 * O resumo é extrativo: só concatena notas já aprovadas pelo motor, sem inventar fatos.
 */
object MemoryRollup {
    private const val SUMMARY_PREFIX = "Resumo rolante: "
    private const val SEPARATOR = " • "

    /** Devolve no máximo uma nota-resumo e as quatro notas mais recentes. */
    fun compact(notes: List<String>): List<String> {
        if (notes.size <= MEMORY_NOTES_PER_SUBJECT) return notes
        val recent = notes.takeLast(MEMORY_NOTES_PER_SUBJECT - 1)
        val older = notes.dropLast(MEMORY_NOTES_PER_SUBJECT - 1)
            .map { it.removePrefix(SUMMARY_PREFIX) }
            .distinct()
        val summary = (SUMMARY_PREFIX + older.joinToString(SEPARATOR))
            .take(MEMORY_NOTE_MAX_CHARS)
            .trimEnd()
        return listOf(summary) + recent
    }
}
