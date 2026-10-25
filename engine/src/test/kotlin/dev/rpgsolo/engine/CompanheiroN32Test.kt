package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-32.1: animal adestrado, lealdade inicial, comida, carinho e fatores de nível. */
class CompanheiroN32Test {
    private fun animal(level: Int = 1) = Entity("pet", "Lobo", EntityKind.NPC, resources = mapOf("pv" to Resource(10, 10)), level = level)

    private fun engine(
        roll: Int = 18,
        level: Int = 1,
    ): GameEngine {
        val e = GameEngine(5)
        e.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                TameCompanion("hero", animal(level), "quadrupede", roll),
            ),
        )
        return e
    }

    @Test fun ganhoPorNivelDoAnimalEDoAdestrador() {
        assertEquals(5, Companions.loyaltyGain(5, 1, 1))
        assertEquals(2, Companions.loyaltyGain(5, 35, 1))
        assertEquals(3, Companions.loyaltyGain(5, 35, 5))
        assertEquals(4, Companions.loyaltyGain(2, 1, 5))
        assertEquals(4, Companions.loyaltyGain(5, 8, 1))
        assertEquals(1, Companions.loyaltyGain(1, 35, 1))
    }

    @Test fun adestrarGuardaLealdadeDoD20ERelacaoDeAliado() {
        val e = engine(roll = 18)
        assertEquals(18, e.state.companions.getValue("pet").loyalty)
        assertEquals("pet", e.state.entities.getValue("pet").id)
        assertEquals(ALLY_RELATION_MIN, e.state.relations[relationKey("pet", "hero")])
    }

    @Test fun comidaSomaLealdadeComTeto() {
        val e = engine(roll = 18)
        assertTrue(e.execute(FeedCompanion("pet", 5, 1)) is ExecResult.Accepted)
        assertEquals(23, e.state.companions.getValue("pet").loyalty)
        e.execute(FeedCompanion("pet", 20, 5))
        e.execute(FeedCompanion("pet", 20, 5))
        assertEquals(Companions.MAX_LOYALTY, e.state.companions.getValue("pet").loyalty)
        assertTrue(e.execute(FeedCompanion("pet", 5, 1)) is ExecResult.Rejected)
    }

    @Test fun nivelAltoDificultaOGanho() {
        val e = engine(roll = 10, level = 35)
        e.execute(FeedCompanion("pet", 5, 1))
        assertEquals(12, e.state.companions.getValue("pet").loyalty)
    }

    @Test fun carinhoUmaVezPorDescanso() {
        val e = engine(roll = 10)
        assertTrue(e.execute(PetCompanion("pet", 1)) is ExecResult.Accepted)
        assertEquals(13, e.state.companions.getValue("pet").loyalty)
        assertTrue(e.execute(PetCompanion("pet", 1)) is ExecResult.Rejected)
        e.execute(AdvanceDays(1))
        assertTrue(e.execute(PetCompanion("pet", 1)) is ExecResult.Accepted)
        assertEquals(16, e.state.companions.getValue("pet").loyalty)
    }

    @Test fun recusas() {
        val e = engine()
        val fresh = GameEngine(5)
        fresh.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)))
        assertTrue(fresh.execute(TameCompanion("hero", animal(), "quadrupede", 0)) is ExecResult.Rejected)
        assertTrue(fresh.execute(TameCompanion("hero", animal(), "quadrupede", 21)) is ExecResult.Rejected)
        assertTrue(fresh.execute(TameCompanion("hero", animal(), "dragao", 10)) is ExecResult.Rejected)
        assertTrue(fresh.execute(TameCompanion("nao-existe", animal(), "quadrupede", 10)) is ExecResult.Rejected)
        assertTrue(e.execute(TameCompanion("hero", animal(), "quadrupede", 10)) is ExecResult.Rejected)
        assertTrue(e.execute(FeedCompanion("hero", 5, 1)) is ExecResult.Rejected)
        assertTrue(e.execute(FeedCompanion("pet", 0, 1)) is ExecResult.Rejected)
        assertTrue(e.execute(FeedCompanion("pet", 5, 6)) is ExecResult.Rejected)
    }

    @Test fun replayIgual() {
        val e = engine(roll = 7)
        e.execute(FeedCompanion("pet", 5, 3))
        e.execute(PetCompanion("pet", 3))
        assertEquals(e.state, GameEngine.restore(5, EngineConfig(), e.events).state)
    }
}
