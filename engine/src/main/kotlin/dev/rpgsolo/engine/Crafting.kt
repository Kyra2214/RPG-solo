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
 * Usa a fórmula única [TestChance]: o degrau 1 do ofício vale 50% e cada degrau acima soma [TestChance.PER_LEVEL];
 * Destreza e Sorte entram como nos demais testes; [difficulty] é o desconto da receita. Habilidade não é atributo e não entra.
 */
object CraftChance {
    fun percent(
        dexterity: Int,
        luck: Int,
        tierBonus: Int = 0,
        difficulty: Int = 0,
    ): Int = TestChance.percent(attribute = dexterity, luck = luck, level = 1 + tierBonus, penalty = difficulty)
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
