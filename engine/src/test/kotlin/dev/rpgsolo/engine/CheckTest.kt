package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckTest {
    private fun spec(
        diff: Int,
        cmp: Comparison = Comparison.ROLL_OVER,
        partial: Int? = null,
        cs: IntRange? = null,
        cf: IntRange? = null,
    ) = CheckSpec("t", 1, 20, 0, diff, cmp, partial, cs, cf)

    private fun roll(
        vararg dice: Int,
        mod: Int = 0,
    ) = RollResult("x", dice.toList(), mod)

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

class PointCheckTest {
    private fun pool(
        points: Int,
        sides: Int = 6,
    ) = CheckSpec("esquiva", 1, sides, points, 0, Comparison.POINTS)

    private fun roll(
        face: Int,
        sides: Int = 6,
    ) = RollResult("1d$sides", listOf(face), 0)

    private fun wins(
        points: Int,
        sides: Int = 6,
    ) = (1..sides).filter { resolveCheck(pool(points, sides), roll(it, sides)).outcome.isSuccess }

    @Test fun semPontosSoAcertaComAFaceMaisAlta() {
        assertEquals(listOf(6), wins(0))
        assertEquals(listOf(6), wins(-3))
        assertEquals(listOf(10), wins(0, 10))
    }

    @Test fun cadaPontoEhUmaFaceContandoDoTopo() {
        assertEquals(listOf(5, 6), wins(2))
        assertEquals(listOf(4, 5, 6), wins(3))
        assertEquals(listOf(3, 4, 5, 6), wins(4))
        assertEquals(listOf(7, 8), wins(2, 8))
    }

    @Test fun nuncaPassaDeLadosMenosUmEntaoSempreFalhaComUm() {
        assertEquals(listOf(2, 3, 4, 5, 6), wins(5))
        assertEquals(listOf(2, 3, 4, 5, 6), wins(9))
        assertEquals(listOf(2, 3, 4), wins(9, 4))
        assertEquals(19, wins(30, 20).size)
        assertEquals(Outcome.FAILURE, resolveCheck(pool(9), roll(1)).outcome)
        assertEquals(Outcome.FAILURE, resolveCheck(pool(99, 20), roll(1, 20)).outcome)
    }

    @Test fun talentoSomaPontos() {
        // 1 ponto + talento (+2) = 3 pontos → 4, 5 e 6
        assertEquals(listOf(4, 5, 6), wins(1 + 2))
    }

    @Test fun cartaoMostraODadoSemModificadorEADificuldadeDaFace() {
        val r = resolveCheck(pool(2, 8), roll(7, 8))
        assertEquals(1, r.spec.diceCount)
        assertEquals(8, r.spec.diceSides)
        assertEquals(0, r.spec.modifier)
        assertEquals(7, r.spec.difficulty)
        assertEquals(Comparison.ROLL_OVER, r.spec.comparison)
        assertTrue(r.spec.label.contains("d8") && r.spec.label.contains("7–8"))
        assertTrue(r.outcome.isSuccess)
    }

    @Test fun motorRolaSemSomarPontosAoDado() {
        val e = GameEngine(1L)
        val r = e.check(pool(3, 10))
        assertEquals(0, r.roll.modifier)
        assertEquals(10, r.spec.diceSides)
        assertEquals(r.roll.dice.single(), r.roll.total)
        assertEquals(r.roll.total >= 8, r.outcome.isSuccess)
    }

    @Test fun escadaDeDadosReais() {
        assertEquals(listOf(4, 6, 8, 10, 12, 20), REAL_DICE)
    }
}
