package dev.rpgsolo.engine

/** Como o sistema compara o total com a dificuldade. */
enum class Comparison {
    /** Sucesso se total >= dificuldade (ex.: d20 + mod contra CD). */
    ROLL_OVER,
    /** Sucesso se total <= dificuldade (ex.: 3d6 contra valor da perícia). */
    ROLL_UNDER
}

enum class Outcome {
    CRITICAL_FAILURE, FAILURE, PARTIAL_SUCCESS, SUCCESS, CRITICAL_SUCCESS;

    /** Sucesso pleno (não conta o parcial). */
    val isSuccess: Boolean get() = this == SUCCESS || this == CRITICAL_SUCCESS
    val isFailure: Boolean get() = this == FAILURE || this == CRITICAL_FAILURE
}

/**
 * Descrição de um teste, vinda do pacote de regras. O motor não conhece nenhum sistema:
 * tudo o que é específico (parcial, críticos) é opcional e só existe se o sistema declarar.
 */
data class CheckSpec(
    val label: String,
    val diceCount: Int,
    val diceSides: Int,
    val modifier: Int = 0,
    val difficulty: Int,
    val comparison: Comparison = Comparison.ROLL_OVER,
    /** null = o sistema não tem sucesso parcial. Senão, falhar por até N pontos é parcial. */
    val partialMargin: Int? = null,
    /** Soma natural dos dados nesta faixa = crítico. null = o sistema não tem crítico. */
    val criticalSuccessRange: IntRange? = null,
    val criticalFailureRange: IntRange? = null
)

data class CheckResult(
    val spec: CheckSpec,
    val roll: RollResult,
    /** Positivo ou zero = passou; negativo = faltou esse tanto. */
    val margin: Int,
    val outcome: Outcome
)

/** Resolve um teste de forma pura: o resultado é decidido aqui, a IA só narra. */
fun resolveCheck(spec: CheckSpec, roll: RollResult): CheckResult {
    val margin = when (spec.comparison) {
        Comparison.ROLL_OVER -> roll.total - spec.difficulty
        Comparison.ROLL_UNDER -> spec.difficulty - roll.total
    }
    val outcome = when {
        spec.criticalFailureRange?.contains(roll.natural) == true -> Outcome.CRITICAL_FAILURE
        spec.criticalSuccessRange?.contains(roll.natural) == true -> Outcome.CRITICAL_SUCCESS
        margin >= 0 -> Outcome.SUCCESS
        spec.partialMargin != null && margin >= -spec.partialMargin -> Outcome.PARTIAL_SUCCESS
        else -> Outcome.FAILURE
    }
    return CheckResult(spec, roll, margin, outcome)
}
