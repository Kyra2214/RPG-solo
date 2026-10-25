package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-18: contador de dias da campanha e pergaminho de elemento temporário. */
class DiasPergaminhoN18Test {
    private fun engine(): GameEngine {
        val e = GameEngine(5)
        e.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                AddItem("hero", "espada-curta"),
                EquipItem("hero", "espada-curta", "mao"),
            ),
        )
        return e
    }

    @Test fun pergaminhoVenceNoDiaSeguinte() {
        val e = engine()
        assertTrue(e.execute(ApplyScrollEnchant("hero", "mao", "espada-curta", ElementPath.FIRE, 1)) is ExecResult.Accepted)
        assertEquals(ElementPath.FIRE, e.state.scrollEnchants.getValue("hero").getValue("mao").element)
        assertEquals(1, e.state.scrollEnchants.getValue("hero").getValue("mao").expiresDay)
        assertTrue(e.execute(AdvanceDays(1)) is ExecResult.Accepted)
        assertEquals(1, e.state.day)
        assertTrue(e.state.scrollEnchants.isEmpty())
    }

    @Test fun pergaminhoDeTresDiasDura() {
        val e = engine()
        e.execute(ApplyScrollEnchant("hero", "mao", "espada-curta", ElementPath.WATER, 3))
        e.execute(AdvanceDays(2))
        assertEquals(1, e.state.scrollEnchants.size)
        e.execute(AdvanceDays(1))
        assertTrue(e.state.scrollEnchants.isEmpty())
    }

    @Test fun recusas() {
        val e = engine()
        assertTrue(e.execute(AdvanceDays(0)) is ExecResult.Rejected)
        assertTrue(e.execute(ApplyScrollEnchant("hero", "mao", "outra", ElementPath.FIRE, 1)) is ExecResult.Rejected)
        assertTrue(e.execute(ApplyScrollEnchant("hero", "elmo", "espada-curta", ElementPath.FIRE, 1)) is ExecResult.Rejected)
        assertTrue(e.execute(ApplyScrollEnchant("hero", "mao", "espada-curta", ElementPath.NONE, 1)) is ExecResult.Rejected)
        e.execute(ApplyScrollEnchant("hero", "mao", "espada-curta", ElementPath.FIRE, 1))
        assertTrue(e.execute(ApplyScrollEnchant("hero", "mao", "espada-curta", ElementPath.WIND, 1)) is ExecResult.Rejected)
    }

    @Test fun replayIgual() {
        val e = engine()
        e.execute(ApplyScrollEnchant("hero", "mao", "espada-curta", ElementPath.EARTH, 2))
        e.execute(AdvanceDays(1))
        assertEquals(e.state, GameEngine.restore(5, EngineConfig(), e.events).state)
    }
}
