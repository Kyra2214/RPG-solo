package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Fórmula única de chance dos testes (15/11/2026): 50% + 5% por nível acima do 1 + 5% a cada 2 do atributo + 5% por Sorte − dificuldade. */
class TesteUnicoPorChanceTest {
    @Test fun formulaBase() {
        assertEquals(50, TestChance.percent(attribute = 0))
        assertEquals(55, TestChance.percent(attribute = 2))
        assertEquals(55, TestChance.percent(attribute = 3)) // 3 pontos valem o mesmo que 2
        assertEquals(60, TestChance.percent(attribute = 4))
        assertEquals(55, TestChance.percent(attribute = 0, luck = 1))
        assertEquals(60, TestChance.percent(attribute = 0, level = 3))
        assertEquals(45, TestChance.percent(attribute = 0, level = 0))
        assertEquals(40, TestChance.percent(attribute = 0, penalty = 10))
    }

    @Test fun atributoOpostoDescontaNaMesmaEscala() {
        assertEquals(50, TestChance.percent(attribute = 4, opposing = 4))
        assertEquals(60, TestChance.percent(attribute = 6, opposing = 2))
        assertEquals(95, TestChance.percent(attribute = 50))
        assertEquals(5, TestChance.percent(attribute = 0, opposing = 50))
    }

    @Test fun oficioUsaAMesmaFormula() {
        assertEquals(
            TestChance.percent(attribute = 4, luck = 2, level = 3, penalty = 5),
            CraftChance.percent(4, 2, tierBonus = 2, difficulty = 5),
        )
    }

    @Test fun testePorChanceViraD100ContraAChanceFinal() {
        // chance bruta 60%, dificuldade desconta 10% -> 50%
        val spec = CheckSpec("t", 1, 100, modifier = 60, difficulty = 10, comparison = Comparison.PERCENT)
        val pool = spec.resolvedPool()
        assertEquals(Comparison.ROLL_UNDER, pool.comparison)
        assertEquals(100, pool.diceSides)
        assertEquals(50, pool.difficulty)
        assertTrue(pool.label.contains("chance 50%"))
        assertTrue(resolveCheck(spec, RollResult("1d100", listOf(50), 0)).outcome.isSuccess)
        assertFalse(resolveCheck(spec, RollResult("1d100", listOf(51), 0)).outcome.isSuccess)
    }

    @Test fun testePorChanceRespeitaOsLimitesDeCincoENoventaECinco() {
        val alto = CheckSpec("t", 1, 100, modifier = 200, difficulty = 0, comparison = Comparison.PERCENT).resolvedPool()
        val baixo = CheckSpec("t", 1, 100, modifier = 10, difficulty = 30, comparison = Comparison.PERCENT).resolvedPool()
        assertEquals(95, alto.difficulty)
        assertEquals(5, baixo.difficulty)
    }

    @Test fun bonusEmPontosValeCincoPorCentoCadaNoTestePorChance() {
        val spec = CheckSpec("t", 1, 100, modifier = 50, difficulty = 0, comparison = Comparison.PERCENT)
        assertEquals(60, spec.plusPoints(2).modifier)
        assertEquals(40, spec.plusPoints(-2).modifier)
        val under = CheckSpec("t", 1, 100, difficulty = 50, comparison = Comparison.ROLL_UNDER)
        assertEquals(52, under.plusPoints(2).difficulty)
    }
}
