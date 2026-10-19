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

private fun compareQuality(a: CheckResult, b: CheckResult): Int {
    val outcomeComparison = a.outcome.ordinal.compareTo(b.outcome.ordinal)
    return if (outcomeComparison != 0) outcomeComparison else a.margin.compareTo(b.margin)
}

/**
 * Executa uma rolagem com política explícita. Os dois resultados são registrados no log;
 * só [FortuneCheckResult.chosen] deve ser usado para decidir o efeito da ação.
 *
 * Esta API não deve ser acionada automaticamente pela IA: a política vem do pacote ou de
 * uma regra de situação validada pelo motor.
 */
private inline fun <T> GameEngine.atomicAdvancedCheck(block: GameEngine.() -> T): T {
    val mark = mark()
    return try {
        block()
    } catch (e: Exception) {
        rollbackTo(mark)
        throw e
    }
}

/**
 * Executa uma rolagem com política explícita. Os dois resultados são registrados no log;
 * só [FortuneCheckResult.chosen] deve ser usado para decidir o efeito da ação.
 *
 * Esta API não deve ser acionada automaticamente pela IA: a política vem do pacote ou de
 * uma regra de situação validada pelo motor. Falha no meio do fluxo restaura log e dado.
 */
fun GameEngine.checkWithFortune(spec: CheckSpec, mode: FortuneMode): FortuneCheckResult =
    atomicAdvancedCheck {
        if (mode == FortuneMode.NORMAL) {
            val single = check(spec.copy(fortuneMode = FortuneMode.NORMAL))
            return@atomicAdvancedCheck FortuneCheckResult(mode, single, null, single)
        }
        val first = check(spec.copy(label = "${spec.label} [1]", fortuneMode = FortuneMode.NORMAL))
        val second = check(spec.copy(label = "${spec.label} [2]", fortuneMode = FortuneMode.NORMAL))
        val quality = compareQuality(first, second)
        val chooseFirst = when (mode) {
            FortuneMode.NORMAL -> true
            FortuneMode.ADVANTAGE -> quality >= 0
            FortuneMode.DISADVANTAGE -> quality <= 0
        }
        val selected = if (chooseFirst) first else second
        val cleanLabel = selected.spec.label.replace(Regex(" \\[([12])\\](?= · d|$)"), "")
        val chosen = selected.copy(spec = selected.spec.copy(label = cleanLabel, fortuneMode = mode))
        FortuneCheckResult(mode, first, second, chosen)
    }

/**
 * Teste oposto. Cada lado usa sua própria especificação, podendo declarar perícia, atributo,
 * dificuldade e tipo de dado próprios. Em empate, o defensor mantém a situação.
 * As duas rolagens são atômicas: erro em um dos lados não deixa rolagem órfã no log.
 */
fun GameEngine.checkOpposed(
    challenger: CheckSpec,
    defender: CheckSpec
): OpposedCheckResult = atomicAdvancedCheck {
    val left = check(challenger.copy(label = "${challenger.label} [oposto]"))
    val right = check(defender.copy(label = "${defender.label} [oposto]"))
    // Margens de testes diferentes não são diretamente comparáveis: primeiro vence quem obteve o melhor resultado; em igualdade, compara-se a margem. Empate absoluto favorece o defensor.
    OpposedCheckResult(left, right, compareQuality(left, right) > 0)
}

/**
 * Teste de grupo. A quantidade de sucessos necessária é explícita e não inferida pela IA.
 * A função só resolve as rolagens; o chamador deve aplicar consequências em comandos validados.
 */
fun GameEngine.checkGroup(
    checks: List<CheckSpec>,
    requiredSuccesses: Int
): GroupCheckResult = atomicAdvancedCheck {
    require(checks.isNotEmpty()) { "teste de grupo exige ao menos um participante" }
    require(requiredSuccesses in 1..checks.size) {
        "sucessos necessários devem ficar entre 1 e o número de participantes"
    }
    val results = checks.mapIndexed { index, spec ->
        check(spec.copy(label = "${spec.label} [grupo ${index + 1}]"))
    }
    val successes = results.count { it.outcome == Outcome.SUCCESS || it.outcome == Outcome.CRITICAL_SUCCESS }
    val failures = results.size - successes
    GroupCheckResult(results, successes, failures, requiredSuccesses, successes >= requiredSuccesses)
}

/** Wrappers para campanhas persistidas: as rolagens dos testes avançados também são salvas no EventStore. */
fun CampaignSession.checkWithFortune(spec: CheckSpec, mode: FortuneMode): FortuneCheckResult =
    engine.checkWithFortune(spec, mode).also { save() }

fun CampaignSession.checkOpposed(challenger: CheckSpec, defender: CheckSpec): OpposedCheckResult =
    engine.checkOpposed(challenger, defender).also { save() }

fun CampaignSession.checkGroup(checks: List<CheckSpec>, requiredSuccesses: Int): GroupCheckResult =
    engine.checkGroup(checks, requiredSuccesses).also { save() }

/** Resultado de um teste de sorte que consome um recurso persistido da campanha. */
sealed interface FortuneSpendResult {
    data class Resolved(val result: FortuneCheckResult) : FortuneSpendResult
    data class Rejected(val reason: String) : FortuneSpendResult
}

/**
 * Usa um recurso da ficha (por exemplo, PA/sorte) para habilitar vantagem ou desvantagem.
 * O gasto e as rolagens formam uma única operação lógica: se a resolução falhar, o log volta
 * ao ponto inicial. O nome do recurso e o custo vêm das regras/situação, nunca da IA.
 */
fun CampaignSession.checkWithFortuneSpendingResource(
    entityId: String,
    resource: String,
    cost: Int,
    spec: CheckSpec,
    mode: FortuneMode
): FortuneSpendResult {
    if (cost <= 0) return FortuneSpendResult.Rejected("o custo do recurso deve ser positivo")
    if (mode == FortuneMode.NORMAL) {
        return FortuneSpendResult.Rejected("gastar recurso exige vantagem ou desvantagem")
    }
    val start = mark()
    return when (val spent = execute(SpendResource(entityId, resource, cost))) {
        is ExecResult.Rejected -> FortuneSpendResult.Rejected(spent.reason)
        is ExecResult.Accepted -> try {
            val result = engine.checkWithFortune(spec, mode)
            save()
            FortuneSpendResult.Resolved(result)
        } catch (e: Exception) {
            rollbackTo(start)
            FortuneSpendResult.Rejected(e.message ?: "falha ao resolver teste com recurso")
        }
    }
}
