package dev.rpgsolo.engine

/**
 * Chance exata de sucesso pleno de um teste, calculada pelo motor (sem rolar).
 * Usa a mesma resolução de [resolveCheck]. Ignora vantagem/desvantagem e dados explosivos
 * ([CheckSpec.fortuneMode], [CheckSpec.explodeOnMax]); devolve 0.0 para dados inválidos.
 */
fun CheckSpec.successChance(): Double {
    val pool = resolvedPool()
    if (pool.diceCount < 1 || pool.diceSides < 2 || pool.diceCount > MAX_ODDS_DICE) return 0.0
    var dist = mapOf(0 to 1.0)
    repeat(pool.diceCount) {
        val next = HashMap<Int, Double>()
        for ((sum, p) in dist) for (face in 1..pool.diceSides) {
            next.merge(sum + face, p / pool.diceSides, Double::plus)
        }
        dist = next
    }
    return dist.entries.sumOf { (sum, p) ->
        if (resolveCheck(this, RollResult("odds", listOf(sum), pool.modifier, 1)).outcome.isSuccess) p else 0.0
    }
}

private const val MAX_ODDS_DICE = 10
