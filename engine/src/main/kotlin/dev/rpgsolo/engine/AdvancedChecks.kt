package dev.rpgsolo.engine

/**
 * Política de rolagem para sistemas que permitem rolar duas vezes e escolher um resultado.
 * A regra do pacote decide quando aplicar esta política; o motor continua decidindo os dados.
 */
enum class FortuneMode { NORMAL, ADVANTAGE, DISADVANTAGE }

data class FortuneCheckResult(
    val mode: FortuneMode,
    val first: CheckResult,
    val second: CheckResult?,
    val chosen: CheckResult
)

data class OpposedCheckResult(
    val challenger: CheckResult,
    val defender: CheckResult,
    /** Empate favorece o defensor para que a ação contestada não tenha sucesso por padrão. */
    val challengerWins: Boolean
)

data class GroupCheckResult(
    val individual: List<CheckResult>,
    val successes: Int,
    val failures: Int,
    val requiredSuccesses: Int,
    val passed: Boolean
)

/**
 * Executa uma rolagem com política explícita. Os dois resultados são registrados no log;
 * só [FortuneCheckResult.chosen] deve ser usado para decidir o efeito da ação.
 *
 * Esta API não deve ser acionada automaticamente pela IA: a política vem do pacote ou de
 * uma regra de situação validada pelo motor.
 */
fun GameEngine.checkWithFortune(spec: CheckSpec, mode: FortuneMode): FortuneCheckResult {
    val first = check(spec.copy(label = "${spec.label} [1]"))
    if (mode == FortuneMode.NORMAL) {
        return FortuneCheckResult(mode, first, null, first)
    }

    val second = check(spec.copy(label = "${spec.label} [2]"))
    val firstScore = first.outcome.ordinal to first.margin
    val secondScore = second.outcome.ordinal to second.margin
    val chooseFirst = when (mode) {
        FortuneMode.NORMAL -> true
        FortuneMode.ADVANTAGE -> firstScore >= secondScore
        FortuneMode.DISADVANTAGE -> firstScore <= secondScore
    }
    return FortuneCheckResult(mode, first, second, if (chooseFirst) first else second)
}

/**
 * Teste oposto. Cada lado usa sua própria especificação, podendo declarar perícia, atributo,
 * dificuldade e tipo de dado próprios. Em empate, o defensor mantém a situação.
 */
fun GameEngine.checkOpposed(
    challenger: CheckSpec,
    defender: CheckSpec
): OpposedCheckResult {
    val left = check(challenger.copy(label = "${challenger.label} [oposto]"))
    val right = check(defender.copy(label = "${defender.label} [oposto]"))
    val leftScore = left.outcome.ordinal to left.margin
    val rightScore = right.outcome.ordinal to right.margin
    return OpposedCheckResult(left, right, leftScore > rightScore)
}

/**
 * Teste de grupo. A quantidade de sucessos necessária é explícita e não inferida pela IA.
 * A função só resolve as rolagens; o chamador deve aplicar consequências em comandos validados.
 */
fun GameEngine.checkGroup(
    checks: List<CheckSpec>,
    requiredSuccesses: Int
): GroupCheckResult {
    require(checks.isNotEmpty()) { "teste de grupo exige ao menos um participante" }
    require(requiredSuccesses in 1..checks.size) {
        "sucessos necessários devem ficar entre 1 e o número de participantes"
    }
    val results = checks.mapIndexed { index, spec ->
        check(spec.copy(label = "${spec.label} [grupo ${index + 1}]"))
    }
    val successes = results.count { it.outcome == Outcome.SUCCESS || it.outcome == Outcome.CRITICAL_SUCCESS }
    val failures = results.size - successes
    return GroupCheckResult(results, successes, failures, requiredSuccesses, successes >= requiredSuccesses)
}
