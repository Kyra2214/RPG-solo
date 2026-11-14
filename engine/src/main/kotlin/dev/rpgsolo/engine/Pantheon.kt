package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** RV-03: o que a entrada do panteão representa. */
@Serializable
enum class PantheonKind { FALLEN_HERO, RIVAL }

/**
 * RV-03: uma entrada do panteão unificado do mundo. Só a parte pública: epitáfio do herói morto ou o que o motor
 * gravou da derrota do rival (RV-01/RV-02). Nunca ficha, dossiê nem segredo.
 */
@Serializable
data class PantheonEntry(
    val identityKey: String,
    val name: String,
    val kind: PantheonKind,
    /** Linha pública curta, já limitada a [Pantheon.LINE_MAX] caracteres. */
    val line: String,
    val lastDay: Int = 0,
)

/**
 * RV-03: panteão unificado. Junta os heróis mortos (MP-02/LG-05) e os rivais (RV-02) num só registro derivado dos
 * personagens recorrentes já persistidos; sem estado novo nem migração. Ordem determinística: o mais recente primeiro
 * e, no empate, rivais antes de heróis e depois pela identidade.
 */
object Pantheon {
    const val LINE_MAX = 200
    const val ENTRIES_MAX = 30
    const val CONTEXT_MAX = 3

    /** Instrução ao mestre sobre como usar o panteão. */
    const val RULE =
        "Use no máximo uma destas lembranças por cena, com naturalidade, só como rumor ou lenda; " +
            "não invente feitos além dos listados e não decida o retorno de ninguém (isso é do motor)."

    fun entries(characters: List<RecurringCharacter>): List<PantheonEntry> {
        val heroes =
            characters.filter { it.kind == RecurringKind.HERO && !it.alive && it.epitaph.isNotBlank() }.map {
                PantheonEntry(it.identityKey, it.name, PantheonKind.FALLEN_HERO, it.epitaph.take(LINE_MAX).trimEnd(), it.lastDay)
            }
        val byKey = characters.associateBy { it.identityKey }
        val rivals =
            Rivals.register(characters).map { r ->
                PantheonEntry(r.identityKey, r.name, PantheonKind.RIVAL, rivalLine(r).take(LINE_MAX).trimEnd(), byKey[r.identityKey]?.lastDay ?: 0)
            }
        return (heroes + rivals)
            .sortedWith(compareByDescending<PantheonEntry> { it.lastDay }.thenBy { if (it.kind == PantheonKind.RIVAL) 0 else 1 }.thenBy { it.identityKey })
            .take(ENTRIES_MAX)
    }

    private fun rivalLine(r: Rival): String {
        val how =
            when (r.outcome) {
                DefeatOutcome.SPARED -> "foi poupado"
                DefeatOutcome.FLED -> "escapou"
                DefeatOutcome.HUMILIATED -> "foi humilhado"
            }
        val by = if (r.targetName.isBlank()) "" else " por ${r.targetName}"
        val mood =
            when (r.attitude) {
                DefeatAttitude.RESENTMENT -> "guarda ressentimento"
                DefeatAttitude.RESPECT -> "guarda respeito"
                DefeatAttitude.DEBT -> "sente que deve algo"
            }
        val heir = if (r.targetDead) "; quem o derrotou já morreu" else ""
        return "${r.name} $how$by e $mood$heir."
    }

    /** Bloco de contexto do mestre: até [CONTEXT_MAX] entradas mais recentes; nulo se o panteão está vazio. */
    fun masterContext(entries: List<PantheonEntry>): String? {
        if (entries.isEmpty()) return null
        return buildString {
            append("Panteão do mundo (fato fixo, parte pública):\n")
            entries.take(CONTEXT_MAX).forEach { append("- ").append(it.line).append('\n') }
            append(RULE)
        }
    }
}
