package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-11: equipar/desequipar com `maxDelta` altera o máximo do recurso (peito com `max_pv`). */
class EquipamentoMaxRecursoTest {
    private fun engine(): GameEngine {
        val e = GameEngine(3)
        e.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 15)))),
                AddItem("hero", "couro"),
            ),
        )
        return e
    }

    private fun pv(e: GameEngine) = e.state.entities.getValue("hero").resources.getValue("pv")

    @Test fun equiparAumentaMaximoEAtualDesequiparReduz() {
        val e = engine()
        assertTrue(e.execute(EquipItem("hero", "couro", "peito", mapOf("pv" to 4))) is ExecResult.Accepted)
        assertEquals(Resource(14, 19), pv(e))
        assertTrue(e.execute(UnequipItem("hero", "peito", mapOf("pv" to -4))) is ExecResult.Accepted)
        assertEquals(Resource(14, 15), pv(e))
    }

    @Test fun desequiparNuncaMataEReplayEhIgual() {
        val e = engine()
        e.execute(EquipItem("hero", "couro", "peito", mapOf("pv" to 4)))
        e.execute(UnequipItem("hero", "peito", mapOf("pv" to -30)))
        assertEquals(0, pv(e).max)
        assertTrue(pv(e).current <= pv(e).max)
        assertEquals(e.state, GameEngine.restore(3, EngineConfig(), e.events).state)
    }

    @Test fun recursoInexistenteEhRecusadoEEventoAntigoAindaAbre() {
        val e = engine()
        assertTrue(e.execute(EquipItem("hero", "couro", "peito", mapOf("mana" to 1))) is ExecResult.Rejected)
        assertEquals(emptyMap<String, Int>(), EquipmentChanged("hero", "mao", "x").maxDelta)
    }
}

/** N-12: o motor recusa equipar abaixo do nível mínimo. */
class EquipamentoNivelMinimoTest {
    @Test fun recusaAbaixoDoNivelEAceitaNoNivel() {
        val e = GameEngine(3)
        e.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                AddItem("hero", "espada"),
            ),
        )
        assertTrue(e.execute(EquipItem("hero", "espada", "mao", minLevel = 2)) is ExecResult.Rejected)
        assertTrue(e.execute(EquipItem("hero", "espada", "mao", minLevel = 1)) is ExecResult.Accepted)
    }
}
