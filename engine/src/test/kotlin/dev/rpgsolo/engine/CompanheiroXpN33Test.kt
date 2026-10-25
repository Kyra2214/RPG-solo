package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-33.1: XP do animal por combate, nível, lealdade e ponto de habilidade. */
class CompanheiroXpN33Test {
    private fun engine(roll: Int = 10): GameEngine {
        val e = GameEngine(5, EngineConfig(levelThresholds = listOf(10, 25, 45, 70)))
        e.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                TameCompanion(
                    "hero",
                    Entity("pet", "Lobo", EntityKind.NPC, resources = mapOf("pv" to Resource(10, 10))),
                    "quadrupede",
                    roll,
                ),
            ),
        )
        return e
    }

    @Test fun xpSemSubirDeNivelNaoMudaLealdade() {
        val e = engine()
        assertTrue(e.execute(GainCompanionXp("pet", 9)) is ExecResult.Accepted)
        val pet = e.state.entities.getValue("pet")
        assertEquals(9, pet.xp)
        assertEquals(1, pet.level)
        assertEquals(10, e.state.companions.getValue("pet").loyalty)
    }

    @Test fun subirDeNivelSomaLealdadeEPontoACadaTresNiveis() {
        val e = engine()
        e.execute(GainCompanionXp("pet", 10))
        assertEquals(2, e.state.entities.getValue("pet").level)
        assertEquals(15, e.state.companions.getValue("pet").loyalty)
        assertEquals(0, e.state.entities.getValue("pet").abilityPoints)
        e.execute(GainCompanionXp("pet", 15))
        assertEquals(3, e.state.entities.getValue("pet").level)
        assertEquals(20, e.state.companions.getValue("pet").loyalty)
        assertEquals(1, e.state.entities.getValue("pet").abilityPoints)
    }

    @Test fun variosNiveisDeUmaVezContamTodosOsPontos() {
        val e = engine(roll = 20)
        e.execute(GainCompanionXp("pet", 70))
        val pet = e.state.entities.getValue("pet")
        assertEquals(5, pet.level)
        assertEquals(1, pet.abilityPoints)
        assertEquals(40, e.state.companions.getValue("pet").loyalty)
    }

    @Test fun recusasDoXp() {
        val e = engine()
        assertTrue(e.execute(GainCompanionXp("pet", 0)) is ExecResult.Rejected)
        assertTrue(e.execute(GainCompanionXp("hero", 5)) is ExecResult.Rejected)
        assertTrue(e.execute(GainXp("pet", 5)) is ExecResult.Rejected)
    }
}
