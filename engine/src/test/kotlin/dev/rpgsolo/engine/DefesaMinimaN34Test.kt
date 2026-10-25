package dev.rpgsolo.engine

import org.junit.Assert.assertTrue
import org.junit.Test

/** N-34: a defesa rolada nunca fica abaixo de 1 (valor baixo vira 1d6 − N, que podia dar 0 ou negativo). */
class DefesaMinimaN34Test {
    @Test fun defesaRoladaTemMinimoUm() {
        val attack = ModifierAggregator.aggregate("ataque", 10, emptyList())
        val defense = ModifierAggregator.aggregate("defesa", 1, emptyList())
        repeat(200) { seed ->
            val r = UnifiedCombat.resolve(attack, defense, Dice(seed.toLong()), canCrit = false)
            assertTrue(r.defenseTotal >= 1)
        }
    }
}
