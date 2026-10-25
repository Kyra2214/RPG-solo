package dev.rpgsolo.engine

/** N-20: tipos das ações genéricas de ofício; a receita (e o tipo de item) vem do pacote, nunca da IA. */
enum class CraftKind(val id: String) {
    CRAFT("criar"),
    PREPARE("preparar"),
    REPAIR("consertar"),
    IDENTIFY("identificar"),
    ;

    companion object {
        fun fromId(id: String): CraftKind? = entries.firstOrNull { it.id == id }
    }
}

/**
 * N-20: chance (d100, acerta com dado <= chance) de consertar, criar, preparar e identificar.
 * ASSUMINDO: 50% + 5% por ponto de Habilidade + 2% por ponto de Sorte + 5% por degrau de ofício acima do primeiro − dificuldade da receita; entre 5% e 95%.
 */
object CraftChance {
    const val BASE = 50
    const val PER_ABILITY = 5
    const val PER_LUCK = 2
    const val PER_TIER = 5

    fun percent(
        ability: Int,
        luck: Int,
        tierBonus: Int = 0,
        difficulty: Int = 0,
    ): Int = (BASE + PER_ABILITY * ability + PER_LUCK * luck + PER_TIER * tierBonus - difficulty).coerceIn(5, 95)
}

/** N-20: resultado de uma ação de ofício ou de afanar; [Refused] não altera nada. */
sealed interface CraftResult {
    data class Done(
        val success: Boolean,
        val chance: Int,
        val roll: Int?,
        val consumed: Map<String, Int>,
        val produced: Map<String, Int>,
        /** Só ao afanar: o alvo notou o furto (relação do alvo com o ladrão cai). */
        val caught: Boolean = false,
    ) : CraftResult

    data class Refused(val reason: String) : CraftResult
}

/** N-20: relação do alvo com o ladrão ao ser flagrado afanando. */
const val PILFER_CAUGHT_RELATION = -10
