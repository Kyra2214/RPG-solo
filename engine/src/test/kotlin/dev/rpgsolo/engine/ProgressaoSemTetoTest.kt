package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Test

/** N-04: curva 5 × N sem teto, teto de missão por fórmula e XP de batalha por nível. */
class ProgressaoSemTetoTest {
    private val cfg = EngineConfig(levelThresholds = listOf(10, 25, 45, 70, 100), levelCostFactor = 5, victoryXpPercent = 8)

    private fun total(n: Int) = 5 * (n * (n + 1) / 2 - 1)

    @Test fun niveisDaTabelaInalterados() {
        assertEquals(1, cfg.levelFor(9))
        assertEquals(2, cfg.levelFor(10))
        assertEquals(6, cfg.levelFor(100))
    }

    @Test fun curvaContinuaSemTeto() {
        listOf(7, 10, 20, 30, 50, 100).forEach { n ->
            assertEquals("nível $n", n, cfg.levelFor(total(n)))
            assertEquals("antes do nível $n", n - 1, cfg.levelFor(total(n) - 1))
        }
        assertEquals(270, total(10))
        assertEquals(25245, total(100))
    }

    @Test fun semFatorATabelaEOTeto() {
        val old = cfg.copy(levelCostFactor = 0)
        assertEquals(6, old.levelFor(100000))
    }

    @Test fun tetoDeMissaoEoCustoDoNivelAtual() {
        assertEquals(10, cfg.maxQuestXp(1))
        assertEquals(30, cfg.maxQuestXp(5))
        assertEquals(35, cfg.maxQuestXp(6))
        assertEquals(155, cfg.maxQuestXp(30))
        assertEquals(30, cfg.copy(levelCostFactor = 0).maxQuestXp(40))
    }

    @Test fun xpDeBatalhaEscalaComONivel() {
        assertEquals(1, cfg.battleXp(1, 1))
        assertEquals(12, cfg.battleXp(30, 30))
        assertEquals(14, cfg.battleXp(30, 31))
        assertEquals(0, cfg.battleXp(30, 25))
        assertEquals(0, cfg.copy(victoryXpPercent = 0).battleXp(30, 30))
    }
}
