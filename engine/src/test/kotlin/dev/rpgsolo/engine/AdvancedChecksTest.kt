package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancedChecksTest {
    private fun spec(label: String, difficulty: Int = 10) =
        CheckSpec(label, diceCount = 1, diceSides = 20, difficulty = difficulty)

    private fun compareQuality(a: CheckResult, b: CheckResult): Int {
        val outcome = a.outcome.ordinal.compareTo(b.outcome.ordinal)
        if (outcome != 0) return outcome
        val sameScale = a.spec.comparison == b.spec.comparison &&
            a.spec.diceCount == b.spec.diceCount &&
            a.spec.diceSides == b.spec.diceSides &&
            a.spec.difficulty == b.spec.difficulty
        return if (sameScale) a.margin.compareTo(b.margin) else 0
    }

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

    @Test fun opposedDifferentDifficultiesDoNotCompareIncompatibleMargins() {
        val engine = GameEngine(seed = 19L)
        val result = engine.checkOpposed(
            spec("ladrao", difficulty = 1).copy(modifier = 20),
            spec("guarda", difficulty = 2).copy(modifier = 20)
        )
        assertEquals(Outcome.SUCCESS, result.challenger.outcome)
        assertEquals(Outcome.SUCCESS, result.defender.outcome)
        assertEquals(false, result.challengerWins)
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

    @Test fun invalidMemberRollsBackWholeGroupCheck() {
        val engine = GameEngine(seed = 777L)
        val invalid = spec("invalid").copy(diceSides = 1)
        runCatching {
            engine.checkGroup(listOf(spec("valid"), invalid), requiredSuccesses = 1)
        }
        assertEquals(0, engine.events.count { it is RollRecorded })
    }

    @Test fun opposedChecksHonorEachParticipantsFortunePolicy() {
        val engine = GameEngine(seed = 987L)
        val result = engine.checkOpposed(
            spec("ladrao").copy(fortuneMode = FortuneMode.ADVANTAGE),
            spec("guarda").copy(fortuneMode = FortuneMode.DISADVANTAGE)
        )
        assertEquals(FortuneMode.ADVANTAGE, result.challenger.spec.fortuneMode)
        assertEquals(FortuneMode.DISADVANTAGE, result.defender.spec.fortuneMode)
        assertEquals(4, engine.events.count { it is RollRecorded })
    }

    @Test fun groupChecksHonorFortunePolicyPerParticipant() {
        val engine = GameEngine(seed = 654L)
        val result = engine.checkGroup(
            listOf(
                spec("vigia-1").copy(fortuneMode = FortuneMode.ADVANTAGE),
                spec("vigia-2").copy(fortuneMode = FortuneMode.DISADVANTAGE)
            ),
            requiredSuccesses = 1
        )
        assertEquals(2, result.individual.size)
        assertEquals(FortuneMode.ADVANTAGE, result.individual[0].spec.fortuneMode)
        assertEquals(FortuneMode.DISADVANTAGE, result.individual[1].spec.fortuneMode)
        assertEquals(4, engine.events.count { it is RollRecorded })
    }

    @Test fun fortuneCheckSpendsResourceAndPersistsRolls() {
        val store = InMemoryEventStore()
        val session = CampaignSession.create(
            store,
            CampaignRecord("fortune", "Sorte", 123L, EngineConfig())
        )
        session.execute(CreateEntity(Entity(
            id = "hero",
            name = "Herói",
            kind = EntityKind.PLAYER,
            resources = mapOf("pa" to Resource(2, 2))
        )))
        val result = session.checkWithFortuneSpendingResource(
            "hero", "pa", 1, spec("escalar"), FortuneMode.ADVANTAGE
        )
        assertTrue(result is FortuneSpendResult.Resolved)
        assertEquals(1, session.state.entities.getValue("hero").resources.getValue("pa").current)
        assertEquals(2, session.engine.events.count { it is RollRecorded })
        val reopened = CampaignSession.open(store, "fortune")
        assertEquals(1, reopened.state.entities.getValue("hero").resources.getValue("pa").current)
        assertEquals(2, reopened.engine.events.count { it is RollRecorded })
    }

    @Test fun fortuneCheckRejectsInsufficientResourceWithoutRolling() {
        val store = InMemoryEventStore()
        val session = CampaignSession.create(store, CampaignRecord("no-fortune", "Sem sorte", 321L, EngineConfig()))
        session.execute(CreateEntity(Entity(
            id = "hero",
            name = "Herói",
            kind = EntityKind.PLAYER,
            resources = mapOf("pa" to Resource(0, 2))
        )))
        val before = session.engine.eventCount
        val result = session.checkWithFortuneSpendingResource(
            "hero", "pa", 1, spec("escalar"), FortuneMode.ADVANTAGE
        )
        assertTrue(result is FortuneSpendResult.Rejected)
        assertEquals(before, session.engine.eventCount)
        assertEquals(0, session.state.entities.getValue("hero").resources.getValue("pa").current)
    }

    @Test fun fortuneResourceCannotBeSpentForNormalRoll() {
        val session = CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("normal-fortune", "Teste normal", 654L, EngineConfig())
        )
        session.execute(CreateEntity(Entity(
            id = "hero",
            name = "Herói",
            kind = EntityKind.PLAYER,
            resources = mapOf("pa" to Resource(2, 2))
        )))
        val before = session.engine.eventCount
        val result = session.checkWithFortuneSpendingResource(
            "hero", "pa", 1, spec("escalar"), FortuneMode.NORMAL
        )
        assertTrue(result is FortuneSpendResult.Rejected)
        assertEquals(before, session.engine.eventCount)
        assertEquals(2, session.state.entities.getValue("hero").resources.getValue("pa").current)
    }

}
