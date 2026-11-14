package dev.rpgsolo.engine

/** LG-02: prefixo de uma nota fixada (o [MemoryRollup] nunca a compacta nem a empurra para fora). */
const val PINNED_NOTE_PREFIX = "[fixa] "

/** LG-02: máximo de notas fixadas por assunto; as mais recentes valem. */
const val PINNED_NOTES_MAX = 2

/** LG-02: máximo de feitos do herói citados no contexto. */
const val LEGACY_DEEDS_MAX = 3

/**
 * LG-02: memória do herói morto para o mestre. Só monta texto a partir do que o motor já guarda
 * ([HeroLegacy], capítulos encerrados); nada vem da IA e nada altera o estado.
 */
object LegacyMemory {
    /** Instrução ao mestre sobre como usar o legado. */
    const val RULE =
        "Cite o herói anterior de vez em quando, com naturalidade; não repita a mesma lembrança em cenas seguidas " +
            "nem invente feitos além dos listados."

    fun isPinned(note: String): Boolean = note.startsWith(PINNED_NOTE_PREFIX)

    /** Fato fixo do legado, dentro do limite de [CanonFacts.MAX_TEXT] caracteres. */
    fun fact(
        legacy: HeroLegacy,
        deeds: List<String> = emptyList(),
    ): String {
        val how =
            when (legacy.kind) {
                DeathKind.SLAIN -> "morto por ${legacy.killerName ?: "alguém"}"
                DeathKind.CONDITION -> "morreu de ${legacy.cause?.removePrefix(CAUSE_CONDITION_PREFIX) ?: "uma condição"}"
                DeathKind.UNKNOWN -> "morreu em circunstâncias desconhecidas"
            }
        val head = "${legacy.heroName} (nível ${legacy.level}${if (legacy.concept.isNotBlank()) ", ${legacy.concept}" else ""}) $how no dia ${legacy.diedOnDay}."
        val feitos = if (deeds.isEmpty()) "" else " Feitos: ${deeds.take(LEGACY_DEEDS_MAX).joinToString("; ")}."
        return (head + feitos).take(CanonFacts.MAX_TEXT).trimEnd()
    }

    /** Nota fixada do NPC com vínculo ao herói; dentro de [MEMORY_NOTE_MAX_CHARS]. */
    fun pinnedNote(
        legacy: HeroLegacy,
        bond: LegacyBond,
    ): String {
        val tom = if (bond.value >= 0) "tinha afeto por" else "tinha rancor contra"
        return (PINNED_NOTE_PREFIX + "${bond.npcName} $tom ${legacy.heroName}, morto no dia ${legacy.diedOnDay}.")
            .take(MEMORY_NOTE_MAX_CHARS).trimEnd()
    }

    /** Até [LEGACY_DEEDS_MAX] feitos tirados dos resumos de capítulos já encerrados (os mais recentes), curtos. */
    fun deeds(chapters: List<Chapter>): List<String> =
        chapters.filter { !it.summary.isNullOrBlank() }
            .takeLast(LEGACY_DEEDS_MAX)
            .map { c -> c.summary.orEmpty().trim().lineSequence().first().take(DEED_MAX_CHARS).trimEnd() }

    /** Notas fixadas que ainda faltam nos NPCs vivos com vínculo ([NoteMemory] válido para o motor). */
    fun pendingPins(
        legacy: HeroLegacy,
        state: GameState,
    ): List<NoteMemory> =
        legacy.bonds.mapNotNull { b ->
            val npc = state.entities[b.npcId]?.takeIf { it.alive && it.kind == EntityKind.NPC } ?: return@mapNotNull null
            val note = pinnedNote(legacy, b)
            val notes = state.memories[b.npcId].orEmpty()
            // Com o teto de fixas já ocupado, emitir de novo faria o MemoryRollup descartar uma e repetir a emissão a cada turno.
            if (note in notes || notes.count { isPinned(it) } >= PINNED_NOTES_MAX) null else NoteMemory(npc.id, note)
        }

    /** Bloco de contexto do mestre: o legado como fato fixo, mais a regra de uso. Vazio se não há legados. */
    fun masterContext(
        legacies: Collection<HeroLegacy>,
        chapters: List<Chapter> = emptyList(),
    ): String? {
        if (legacies.isEmpty()) return null
        val feitos = deeds(chapters)
        return buildString {
            append("Heróis anteriores (fato fixo):\n")
            legacies.sortedBy { it.diedOnDay }.forEach { l ->
                append("- ").append(fact(l, feitos)).append('\n')
                if (l.bonds.isNotEmpty()) {
                    append("  Vínculos: ")
                        .append(l.bonds.joinToString(", ") { "${it.npcName} (${if (it.value >= 0) "afeto" else "rancor"})" })
                        .append('\n')
                }
            }
            append(RULE)
        }
    }

    private const val DEED_MAX_CHARS = 90
}
