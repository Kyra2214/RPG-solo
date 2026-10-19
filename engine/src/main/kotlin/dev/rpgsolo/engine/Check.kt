package dev.rpgsolo.engine

/** Como o sistema compara o total com a dificuldade. */
enum class Comparison {
    /** Sucesso se total >= dificuldade (ex.: d20 + mod contra CD). */
    ROLL_OVER,
    /** Sucesso se total <= dificuldade (ex.: 3d6 contra valor da perícia). */
    ROLL_UNDER,
    /**
     * Teste de pontos: um dado só (dos dados reais, [REAL_DICE]); o modificador do teste são os pontos da habilidade
     * (já somados talentos e condições). Zero pontos mantém só a chance mínima (face mais alta); cada ponto positivo acrescenta uma face.
     * Sempre entre [MIN_POINT_FACES] e (lados − 1) faces; o 1 sempre falha.
     */
    POINTS
}

/** Dados que existem na mesa; a dificuldade de um teste de pontos escolhe um deles. */
val REAL_DICE = listOf(4, 6, 8, 10, 12, 20)
const val MIN_POINT_FACES = 1

/** Zero pontos = uma face mínima; cada ponto positivo acrescenta uma face, até lados − 1. */
fun pointFaces(points: Int, sides: Int): Int =
    minOf(points.coerceAtLeast(0), maxOf(0, sides - 2)) + MIN_POINT_FACES

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
    val criticalFailureRange: IntRange? = null,
    /** Atributo que o teste usa (id do pacote), se houver. Serve para condições que dão Ganho/Perda por atributo. */
    val stat: String? = null,
    /** Id do teste no pacote, se veio de um. Serve para bônus de traços ligados a um teste. */
    val checkId: String? = null,
    /** Política de rolagem opcional declarada pela regra; padrão preserva o comportamento atual. */
    val fortuneMode: FortuneMode = FortuneMode.NORMAL
)

data class CheckResult(
    val spec: CheckSpec,
    val roll: RollResult,
    /** Positivo ou zero = passou; negativo = faltou esse tanto. */
    val margin: Int,
    val outcome: Outcome
)

/**
 * Teste de pontos ([Comparison.POINTS]) vira um 1dN comum, sem modificador, contra a menor face que acerta
 * (N + 1 - faces). Assim o log, a animação e o cartão do dado mostram exatamente o que vale. Outros testes voltam intactos.
 */
fun CheckSpec.resolvedPool(): CheckSpec {
    if (comparison != Comparison.POINTS) return this
    val faces = pointFaces(modifier, diceSides)
    val lowest = diceSides + 1 - faces
    return copy(
        label = "$label · d$diceSides · acerta com ${if (faces == 1) "$diceSides" else "$lowest–$diceSides"}",
        diceCount = 1, modifier = 0, difficulty = lowest, comparison = Comparison.ROLL_OVER
    )
}

/** Resolve um teste de forma pura: o resultado é decidido aqui, a IA só narra. */
fun resolveCheck(original: CheckSpec, roll: RollResult): CheckResult {
    val spec = original.resolvedPool()
    val margin = when (spec.comparison) {
        Comparison.ROLL_OVER, Comparison.POINTS -> roll.total - spec.difficulty
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
