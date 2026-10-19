package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancementTest {
    private fun config() = EngineConfig(
        levelThresholds = listOf(10, 20),
        advancementPointsPerLevel = 2,
        statCaps = mapOf("forca" to 5),
        skillCaps = mapOf("esquiva" to 5),
        skillRequirements = mapOf("esquiva" to AdvancementRequirement(minStats = mapOf("forca" to 2)))
    )

    @Test fun levelGrantsPointsAndPlayerAllocationIsPersisted() {
        val store = InMemoryEventStore()
        val session = CampaignSession.create(store, CampaignRecord("adv", "Evolução", 7L, config()))
        session.execute(CreateEntity(Entity(
            id = "hero",
            name = "Herói",
            kind = EntityKind.PLAYER,
            stats = mapOf("forca" to 2),
            skills = mapOf("esquiva" to 1)
        )))
        session.execute(GainXp("hero", 10))
        assertEquals(2, session.state.entities.getValue("hero").advancementPoints)
        session.execute(AllocateAdvancement("hero", statIncreases = mapOf("forca" to 1), skillIncreases = mapOf("esquiva" to 1)))
        val evolved = session.state.entities.getValue("hero")
        assertEquals(3, evolved.stats["forca"])
        assertEquals(2, evolved.skills["esquiva"])
        assertTrue("esquiva" in evolved.traits)
        assertTrue(automaticTimeline(session.engine.events, "hero", session.state).any { it.text.contains("investiu pontos de evolução") })
        assertEquals(0, evolved.advancementPoints)

        val reopened = CampaignSession.open(store, "adv")
        assertEquals(evolved, reopened.state.entities.getValue("hero"))
    }

    @Test fun levelRewardsRestoreConfiguredResourcesWithoutExceedingMaximum() {
        val store = InMemoryEventStore()
        val cfg = config().copy(levelResourceRewards = mapOf("mana" to 3))
        val session = CampaignSession.create(store, CampaignRecord("level-reward", "Recompensa", 17L, cfg))
        session.execute(CreateEntity(Entity(
            id = "hero", name = "Herói", kind = EntityKind.PLAYER,
            resources = mapOf("mana" to Resource(2, 5))
        )))

        session.execute(GainXp("hero", 10))
        assertEquals(5, session.state.entities.getValue("hero").resources.getValue("mana").current)
        session.execute(GainXp("hero", 10))
        assertEquals(5, session.state.entities.getValue("hero").resources.getValue("mana").current)
        val reopened = CampaignSession.open(store, "level-reward")
        assertEquals(5, reopened.state.entities.getValue("hero").resources.getValue("mana").current)
    }

    @Test fun allocationRejectsCapsAndDoesNotPartiallyApply() {
        val engine = GameEngine(3L, config())
        engine.execute(CreateEntity(Entity(
            id = "hero",
            name = "Herói",
            kind = EntityKind.PLAYER,
            stats = mapOf("forca" to 5),
            skills = mapOf("esquiva" to 1),
            advancementPoints = 3
        )))
        val before = engine.events.size
        val result = engine.execute(AllocateAdvancement("hero", statIncreases = mapOf("forca" to 1)))
        assertTrue(result is ExecResult.Rejected)
        assertEquals(before, engine.events.size)
        assertEquals(5, engine.state.entities.getValue("hero").stats["forca"])
        assertEquals(3, engine.state.entities.getValue("hero").advancementPoints)
    }
    @Test fun allocationRejectsSkillWhosePrerequisiteIsMissing() {
        val engine = GameEngine(5L, config())
        engine.execute(CreateEntity(Entity(
            id = "hero",
            name = "Herói",
            kind = EntityKind.PLAYER,
            stats = mapOf("forca" to 1),
            skills = emptyMap(),
            advancementPoints = 2
        )))
        val before = engine.events.size
        val result = engine.execute(AllocateAdvancement("hero", skillIncreases = mapOf("esquiva" to 1)))
        assertTrue(result is ExecResult.Rejected)
        assertEquals(before, engine.events.size)
        assertEquals(2, engine.state.entities.getValue("hero").advancementPoints)
    }

    @Test fun configRejectsPrerequisiteForUnknownStat() {
        val error = runCatching {
            EngineConfig(
                statCaps = mapOf("forca" to 5),
                skillCaps = mapOf("esquiva" to 5),
                skillRequirements = mapOf(
                    "esquiva" to AdvancementRequirement(minStats = mapOf("atributo-inexistente" to 1))
                )
            )
        }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
    }

}
