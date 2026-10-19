package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceTest {
    @Test fun mesmaSeedGeraMesmoResultado() {
        assertEquals(Dice(42).roll(3, 6, 2), Dice(42).roll(3, 6, 2))
    }

    @Test fun valoresDentroDoLimite() {
        val d = Dice(7)
        repeat(500) { assertTrue(d.roll(2, 6).dice.all { it in 1..6 }) }
    }

    @Test fun dadosExplosivosSaoDeterministicosERespeitamOLimite() {
        val a = Dice(1).roll(100, 2, explodeOnMax = true)
        val b = Dice(1).roll(100, 2, explodeOnMax = true)
        assertEquals(a, b)
        assertTrue(a.expression.contains("!"))
        assertTrue(a.dice.size >= 100)
        assertTrue(a.dice.all { it in 1..2 })
        assertTrue(a.dice.size <= 100 * 101)
    }

    @Test fun dadosNormaisNaoMudamAExpressao() {
        val result = Dice(42).roll(2, 6, 1)
        assertEquals("2d6+1", result.expression)
        assertEquals(2, result.dice.size)
    }

    @Test fun totalSomaModificador() {
        assertEquals(12, RollResult("2d6+3", listOf(4, 5), 3).total)
    }

    @Test fun rebobinarRepeteAsMesmasRolagens() {
        val d = Dice(99)
        val a = d.roll(2, 20)
        val b = d.roll(2, 20)
        d.seek(0)
        assertEquals(a, d.roll(2, 20))
        assertEquals(b, d.roll(2, 20))
    }

    @Test fun rolagensSeguidasSaoDiferentes() {
        val d = Dice(5)
        val seq = List(20) { d.roll(1, 1000).total }
        assertTrue(seq.toSet().size > 1)
    }
}
