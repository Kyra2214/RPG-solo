package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckTest {
    private fun spec(diff: Int, cmp: Comparison = Comparison.ROLL_OVER, partial: Int? = null,
                     cs: IntRange? = null, cf: IntRange? = null) =
        CheckSpec("t", 1, 20, 0, diff, cmp, partial, cs, cf)

    private fun roll(vararg dice: Int, mod: Int = 0) = RollResult("x", dice.toList(), mod)

    @Test fun rollOverSucessoEFalha() {
        assertEquals(Outcome.SUCCESS, resolveCheck(spec(10), roll(10)).outcome)
        assertEquals(Outcome.FAILURE, resolveCheck(spec(10), roll(9)).outcome)
    }

    @Test fun rollUnderInverte() {
        val s = spec(10, Comparison.ROLL_UNDER)
        assertEquals(Outcome.SUCCESS, resolveCheck(s, roll(10)).outcome)
        assertEquals(Outcome.FAILURE, resolveCheck(s, roll(11)).outcome)
    }

    @Test fun modificadorEntraNoTotal() {
        assertEquals(Outcome.SUCCESS, resolveCheck(spec(15), roll(10, mod = 5)).outcome)
    }

    @Test fun semParcialNoSistemaNaoExisteParcial() {
        assertEquals(Outcome.FAILURE, resolveCheck(spec(10, partial = null), roll(8)).outcome)
    }

    @Test fun parcialQuandoOSistemaPermite() {
        val s = spec(10, partial = 2)
        assertEquals(Outcome.PARTIAL_SUCCESS, resolveCheck(s, roll(8)).outcome)
        assertEquals(Outcome.FAILURE, resolveCheck(s, roll(7)).outcome)
    }

    @Test fun semCriticoDeclaradoNuncaHaCritico() {
        assertEquals(Outcome.SUCCESS, resolveCheck(spec(2), roll(20)).outcome)
        assertEquals(Outcome.FAILURE, resolveCheck(spec(30), roll(1)).outcome)
    }

    @Test fun criticoSobrepoeMargem() {
        val s = spec(10, cs = 20..20, cf = 1..1)
        assertEquals(Outcome.CRITICAL_SUCCESS, resolveCheck(s, roll(20)).outcome)
        assertEquals(Outcome.CRITICAL_FAILURE, resolveCheck(s, roll(1, mod = 30)).outcome)
    }

    @Test fun margemEhCalculada() {
        assertEquals(-3, resolveCheck(spec(10), roll(7)).margin)
        assertEquals(4, resolveCheck(spec(10, Comparison.ROLL_UNDER), roll(6)).margin)
    }

    @Test fun parcialNaoContaComoSucessoPleno() {
        assertFalse(Outcome.PARTIAL_SUCCESS.isSuccess)
        assertTrue(Outcome.CRITICAL_SUCCESS.isSuccess)
    }
}
