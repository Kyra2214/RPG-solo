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
    val challengerWins: Boolean,
    /** Rolagens descartadas pela política de fortuna, agrupadas por participante. */
    val challengerAlternatives: List<CheckResult> = emptyList(),
    val defenderAlternatives: List<CheckResult> = emptyList()
)

data class GroupCheckResult(
    val individual: List<CheckResult>,
    val successes: Int,
    val failures: Int,
    val requiredSuccesses: Int,
    val passed: Boolean,
    /** Uma lista por participante; contém apenas as rolagens descartadas pela fortuna. */
    val alternatives: List<List<CheckResult>> = emptyList()
)

private fun compareQuality(a: CheckResult, b: CheckResult): Int {
    val outcomeComparison = a.outcome.ordinal.compareTo(b.outcome.ordinal)
    if (outcomeComparison != 0) return outcomeComparison

    // Margens só podem desempatar quando ambos os resultados usam a mesma escala mecânica.
    // Comparar margem de d20 contra CD com margem de 3d6 abaixo do atributo produz um vencedor artificial.
    val sameScale = a.spec.comparison == b.spec.comparison &&
        a.spec.diceCount == b.spec.diceCount &&
        a.spec.diceSides == b.spec.diceSides &&
        a.spec.difficulty == b.spec.difficulty
    return if (sameScale) a.margin.compareTo(b.margin) else 0
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
    val leftSpec = challenger.copy(label = "${challenger.label} [oposto]")
    val rightSpec = defender.copy(label = "${defender.label} [oposto]")
    // Cada lado aplica sua própria política de fortuna, sem misturar a rolagem descartada na disputa.
    val leftResolution = checkWithFortune(leftSpec, leftSpec.fortuneMode)
    val rightResolution = checkWithFortune(rightSpec, rightSpec.fortuneMode)
    val left = leftResolution.chosen
    val right = rightResolution.chosen
    // Margens de testes diferentes não são diretamente comparáveis: primeiro vence quem obteve o melhor resultado; em igualdade, compara-se a margem. Empate absoluto favorece o defensor.
    val leftAlternative = listOfNotNull(leftResolution.first, leftResolution.second)
        .filter { it.roll !== leftResolution.chosen.roll }
    val rightAlternative = listOfNotNull(rightResolution.first, rightResolution.second)
        .filter { it.roll !== rightResolution.chosen.roll }
    OpposedCheckResult(left, right, compareQuality(left, right) > 0, leftAlternative, rightAlternative)
}

/**
 * Teste de grupo. A quantidade de sucessos necessária é explícita e não inferida pela IA.
 * A função só resolve as rolagens; o chamador deve aplicar consequências em comandos validados.
 */
fun GameEngine.checkGroup(
    checks: List<CheckSpec>,
    requiredSuccesses: Int
): GroupCheckResult = atomicAdvancedCheck {
    require(checks.size >= 2) { "teste de grupo exige ao menos dois participantes" }
    require(requiredSuccesses in 1..checks.size) {
        "sucessos necessários devem ficar entre 1 e o número de participantes"
    }
    val resolutions = checks.mapIndexed { index, spec ->
        val participantSpec = spec.copy(label = "${spec.label} [grupo ${index + 1}]")
        checkWithFortune(participantSpec, participantSpec.fortuneMode)
    }
    val results = resolutions.map { it.chosen }
    val alternatives = resolutions.map { resolution ->
        listOfNotNull(resolution.first, resolution.second).filter { it.roll !== resolution.chosen.roll }
    }
    val successes = results.count { it.outcome == Outcome.SUCCESS || it.outcome == Outcome.CRITICAL_SUCCESS }
    val failures = results.size - successes
    GroupCheckResult(results, successes, failures, requiredSuccesses, successes >= requiredSuccesses, alternatives)
}

/**
 * Persiste uma resolução avançada como uma única unidade lógica. Se a resolução ou o append falhar,
 * restaura o estado em memória; EventStore exige que append seja atômico (tudo ou nada).
 */
private inline fun <T> CampaignSession.persistAdvancedCheck(block: GameEngine.() -> T): T {
    val start = mark()
    return try {
        val result = engine.block()
        save()
        result
    } catch (e: Exception) {
        engine.rollbackTo(start)
        throw e
    }
}

/** Wrappers para campanhas persistidas: a resolução e a gravação formam uma operação lógica. */
fun CampaignSession.checkWithFortune(spec: CheckSpec, mode: FortuneMode): FortuneCheckResult =
    persistAdvancedCheck { checkWithFortune(spec, mode) }

fun CampaignSession.checkOpposed(challenger: CheckSpec, defender: CheckSpec): OpposedCheckResult =
    persistAdvancedCheck { checkOpposed(challenger, defender) }

fun CampaignSession.checkGroup(checks: List<CheckSpec>, requiredSuccesses: Int): GroupCheckResult =
    persistAdvancedCheck { checkGroup(checks, requiredSuccesses) }

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
    return try {
        when (val spent = engine.execute(SpendResource(entityId, resource, cost))) {
            is ExecResult.Rejected -> {
                engine.rollbackTo(start)
                FortuneSpendResult.Rejected(spent.reason)
            }
            is ExecResult.Accepted -> {
                val result = engine.checkWithFortune(spec, mode)
                save() // grava gasto + rolagens em um único append
                FortuneSpendResult.Resolved(result)
            }
        }
    } catch (e: Exception) {
        // Nenhum save ocorreu antes da resolução; append do EventStore é atômico.
        engine.rollbackTo(start)
        FortuneSpendResult.Rejected(e.message ?: "falha ao resolver teste com recurso")
    }
}
