package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** MU-08.1: cálculo do valor efetivo, agregador de modificadores, duração rolada e rolagens reproduzíveis. */
class EffectiveValueTest {
    private fun expr(value: Int) = EffectiveValue.expressionFor(value)

    @Test fun valoresDaRegraOriginal() {
        assertEquals(DiceExpression(1, 6, -3), expr(3))
        assertEquals(DiceExpression(1, 6, -2), expr(4))
        assertEquals(DiceExpression(1, 6, -1), expr(5))
        assertEquals(DiceExpression(1, 6, 0), expr(6))
        assertEquals(DiceExpression(1, 6, 1), expr(7))
        assertEquals(DiceExpression(1, 6, 5), expr(11))
        assertEquals(DiceExpression(2, 6, 0), expr(12))
    }

    @Test fun extensaoForaDaFaixa() {
        assertEquals(DiceExpression(2, 6, 1), expr(13))
        assertEquals(DiceExpression(3, 6, 0), expr(18))
        assertEquals(DiceExpression(1, 6, -6), expr(0))
        assertEquals(DiceExpression(1, 6, -9), expr(-3))
        assertEquals(DiceExpression(100, 6, 0), expr(600))
        assertEquals(DiceExpression(100, 6, 5), expr(605))
    }

    @Test fun textoDaExpressao() {
        assertEquals("1d6", expr(6).toString())
        assertEquals("1d6+2", expr(8).toString())
        assertEquals("1d6-3", expr(3).toString())
        assertEquals("2d6", expr(12).toString())
    }

    @Test fun rolagemDaExpressaoEhReproduzivelEDentroDaFaixa() {
        val a = expr(8).roll(Dice(42))
        val b = expr(8).roll(Dice(42))
        assertEquals(a, b)
        assertTrue(a.total in 3..8)
        repeat(50) { seed ->
            val r = expr(12).roll(Dice(seed.toLong()))
            assertTrue(r.total in 2..12)
        }
    }

    @Test fun rolagemDependeSoDaSeedEDaPosicao() {
        val d1 = Dice(7)
        val first = d1.roll(1, 6)
        val second = d1.roll(1, 6)
        val d2 = Dice(7)
        assertEquals(first, d2.roll(1, 6))
        d2.seek(0)
        assertEquals(first, d2.roll(1, 6))
        assertEquals(second, d2.roll(1, 6))
    }

    private fun mod(source: String, target: String, value: Int, rounds: Int? = null, uses: Int? = null, stackable: Boolean = false) =
        Modifier(source, source, target, value, rounds, uses, stackable)

    @Test fun agregadorSomaFontesDiferentes() {
        val b = ModifierAggregator.aggregate("attack", 5, listOf(mod("a", "attack", 1), mod("b", "attack", 1), mod("c", "defense", 9)))
        assertEquals(7, b.value)
        assertEquals(2, b.applied.size)
        assertEquals(expr(7), b.expression)
    }

    @Test fun agregadorNaoRepeteMesmaFonte() {
        val b = ModifierAggregator.aggregate("attack", 5, listOf(mod("a", "attack", 2), mod("a", "attack", 2)))
        assertEquals(7, b.value)
        assertEquals(RejectReason.DUPLICATE_SOURCE, b.rejected.single().reason)
    }

    @Test fun agregadorAceitaRepeticaoSoSeEmpilhavel() {
        val b = ModifierAggregator.aggregate("attack", 5, listOf(mod("a", "attack", 1, stackable = true), mod("a", "attack", 1, stackable = true)))
        assertEquals(7, b.value)
    }

    @Test fun agregadorExcluiExpirados() {
        val b = ModifierAggregator.aggregate("attack", 5, listOf(mod("a", "attack", 3, rounds = 0), mod("b", "attack", 3, uses = 0), mod("c", "attack", 1, rounds = 2)))
        assertEquals(6, b.value)
        assertEquals(2, b.rejected.count { it.reason == RejectReason.EXPIRED })
    }

    @Test fun duracaoRoladaEhUmD6GravadoNoEfeito() {
        repeat(30) { seed ->
            val (m, rolled) = EffectDuration.withRolledDuration(mod("x", "attack", 1), Dice(seed.toLong()))
            assertTrue(rolled.rounds in 1..6)
            assertEquals(rolled.rounds, m.remainingRounds)
            assertEquals(rolled.rounds, rolled.roll.total)
        }
        val a = EffectDuration.rollRounds(Dice(9))
        assertEquals(a, EffectDuration.rollRounds(Dice(9)))
    }

    @Test fun fimDeRodadaDescontaERemoveExpirados() {
        val list = listOf(mod("a", "attack", 1, rounds = 2), mod("b", "attack", 1, rounds = 1), mod("c", "attack", 1))
        val next = EffectDuration.endOfRound(list)
        assertEquals(listOf("a", "c"), next.map { it.sourceId })
        assertEquals(1, next.first().remainingRounds)
        assertEquals(listOf("c"), EffectDuration.endOfRound(next).map { it.sourceId })
    }

    @Test fun consumoDeUsosRemoveEsgotados() {
        val list = listOf(mod("a", "attack", 1, uses = 2), mod("b", "attack", 1, uses = 1))
        val once = EffectDuration.consumeUse(list, "b")
        assertEquals(listOf("a"), once.map { it.sourceId })
        assertEquals(2, once.single().usesLeft)
        assertEquals(1, EffectDuration.consumeUse(once, "a").single().usesLeft)
    }
}
