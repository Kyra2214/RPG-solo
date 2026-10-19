package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancedChecksTest {
    private fun spec(label: String, difficulty: Int = 10) =
        CheckSpec(label, diceCount = 1, diceSides = 20, difficulty = difficulty)

    private fun compareQuality(a: CheckResult, b: CheckResult): Int =
        a.outcome.ordinal.compareTo(b.outcome.ordinal).takeIf { it != 0 } ?: a.margin.compareTo(b.margin)

    @Test fun advantageSelectsBestAndRecordsBothRolls() {
        val engine = GameEngine(seed = 123L)
        val result = engine.checkWithFortune(spec("escalar"), FortuneMode.ADVANTAGE)
        assertEquals(2, listOf(result.first, result.second).filterNotNull().size)
        assertTrue(compareQuality(result.chosen, requireNotNull(result.second)) >= 0)
        assertEquals(2, engine.events.count { it is RollRecorded })
    }

    @Test fun disadvantageSelectsWorst() {
        val engine = GameEngine(seed = 456L)
        val result = engine.checkWithFortune(spec("furtividade"), FortuneMode.DISADVANTAGE)
        assertTrue(compareQuality(result.chosen, requireNotNull(result.second)) <= 0)
    }

    @Test fun normalRollsOnlyOnce() {
        val engine = GameEngine(seed = 789L)
        val result = engine.checkWithFortune(spec("percepcao"), FortuneMode.NORMAL)
        assertEquals(null, result.second)
        assertEquals(result.first, result.chosen)
        assertEquals(1, engine.events.count { it is RollRecorded })
    }

    @Test fun opposedTieGoesToDefender() {
        val engine = GameEngine(seed = 1L)
        val result = engine.checkOpposed(
            spec("arrombamento", difficulty = 10),
            spec("resistencia-da-fechadura", difficulty = 10)
        )
        val expected = compareQuality(result.challenger, result.defender) > 0
        assertEquals(expected, result.challengerWins)
    }

    @Test fun groupUsesExplicitSuccessThreshold() {
        val engine = GameEngine(seed = 42L)
        val result = engine.checkGroup(
            listOf(spec("vigia-1", 1), spec("vigia-2", 1), spec("vigia-3", 1)),
            requiredSuccesses = 2
        )
        assertEquals(3, result.individual.size)
        assertEquals(3, result.successes + result.failures)
        assertEquals(result.successes >= 2, result.passed)
    }
    @Test fun checkSpecCanDeclareAdvantageWithoutCallingSpecialApi() {
        val engine = GameEngine(seed = 321L)
        val result = engine.check(spec("atletismo").copy(fortuneMode = FortuneMode.ADVANTAGE))
        assertTrue(result.spec.label.contains("atletismo"))
        assertEquals(2, engine.events.count { it is RollRecorded })
    }

}
