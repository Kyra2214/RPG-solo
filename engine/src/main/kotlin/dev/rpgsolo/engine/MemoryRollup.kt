package dev.rpgsolo.engine

/**
 * I-02.4: mantém a memória útil sem depender do encerramento de capítulos ou de outra chamada à IA.
 * O resumo é extrativo: só concatena notas já aprovadas pelo motor, sem inventar fatos.
 */
object MemoryRollup {
    private const val SUMMARY_PREFIX = "Resumo rolante: "
    private const val SEPARATOR = " • "

    /**
     * Devolve no máximo uma nota-resumo e as mais recentes. LG-02: notas fixadas ([LegacyMemory.isPinned], até
     * [PINNED_NOTES_MAX]) ficam sempre, à frente, e ocupam vagas das notas comuns.
     */
    fun compact(notes: List<String>): List<String> {
        if (notes.size <= MEMORY_NOTES_PER_SUBJECT) return notes
        val pinned = notes.filter(LegacyMemory::isPinned).takeLast(PINNED_NOTES_MAX)
        if (pinned.isEmpty()) return compactPlain(notes, MEMORY_NOTES_PER_SUBJECT)
        val plain = notes.filterNot(LegacyMemory::isPinned)
        val room = MEMORY_NOTES_PER_SUBJECT - pinned.size
        return pinned + if (plain.size <= room) plain else compactPlain(plain, room)
    }

    /** Mantém as [limit] notas mais recentes nas notas, as antigas em uma só nota-resumo. */
    fun trim(
        notes: List<String>,
        limit: Int = MEMORY_NOTES_PER_SUBJECT,
    ): List<String> {
        val pinned = notes.filter(LegacyMemory::isPinned).takeLast(PINNED_NOTES_MAX)
        if (pinned.isEmpty()) return notes.takeLast(limit)
        return pinned + notes.filterNot(LegacyMemory::isPinned).takeLast((limit - pinned.size).coerceAtLeast(0))
    }

    private fun compactPlain(
        notes: List<String>,
        limit: Int,
    ): List<String> {
        if (notes.size <= limit) return notes
        val recent = notes.takeLast(limit - 1)
        val older =
            notes.dropLast(limit - 1)
                .map { it.removePrefix(SUMMARY_PREFIX) }
                .distinct()
        val summary =
            (SUMMARY_PREFIX + older.joinToString(SEPARATOR))
                .take(MEMORY_NOTE_MAX_CHARS)
                .trimEnd()
        return listOf(summary) + recent
    }
}
