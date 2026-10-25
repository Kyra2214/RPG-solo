package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-20: ações genéricas de ofício (criar, preparar, consertar, identificar) e afanar. */
class OficioN20Test {
    private fun engine(seed: Long = 7): GameEngine {
        val e = GameEngine(seed)
        e.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                CreateEntity(Entity("guarda", "Guarda", EntityKind.NPC, resources = mapOf("pv" to Resource(10, 10)))),
                AddItem("hero", "minerio", 4),
                AddItem("hero", "espada-quebrada"),
                AddItem("guarda", "chave", 1),
            ),
        )
        return e
    }

    private fun qty(
        e: GameEngine,
        owner: String,
        item: String,
    ) = e.state.inventory[owner]?.get(item) ?: 0

    @Test fun chanceSegueAFormula() {
        assertEquals(50, CraftChance.percent(0, 0))
        assertEquals(66, CraftChance.percent(2, 3))
        assertEquals(95, CraftChance.percent(20, 20))
        assertEquals(5, CraftChance.percent(0, 0, difficulty = 40))
        assertEquals(60, CraftChance.percent(0, 0, tierBonus = 2))
    }

    @Test fun sucessoGarantidoConsomeEEntrega() {
        val e = engine()
        val r = e.attemptCraft("hero", CraftKind.CRAFT, mapOf("minerio" to 2), mapOf("barra" to 1), 100) as CraftResult.Done
        assertTrue(r.success)
        assertNull(r.roll)
        assertEquals(2, qty(e, "hero", "minerio"))
        assertEquals(1, qty(e, "hero", "barra"))
    }

    @Test fun falhaDeConsertoPerdeTodoOMaterialEManteOQuebrado() {
        val e = engine()
        val r =
            e.attemptCraft(
                "hero",
                CraftKind.REPAIR,
                mapOf("espada-quebrada" to 1, "minerio" to 3),
                mapOf("espada" to 1),
                0,
                keepOnFailure = setOf("espada-quebrada"),
            ) as CraftResult.Done
        assertFalse(r.success)
        assertTrue(r.roll != null)
        // N-35: conserto que falha gasta todo o material; o item quebrado continua.
        assertEquals(mapOf("minerio" to 3), r.consumed)
        assertEquals(0, qty(e, "hero", "minerio"))
        assertEquals(1, qty(e, "hero", "espada-quebrada"))
        assertEquals(0, qty(e, "hero", "espada"))
    }

    @Test fun recusasNaoAlteramNada() {
        val e = engine()
        val before = e.state
        assertTrue(e.attemptCraft("hero", CraftKind.CRAFT, mapOf("minerio" to 9), mapOf("barra" to 1), 100) is CraftResult.Refused)
        assertTrue(e.attemptCraft("hero", CraftKind.REPAIR, emptyMap(), mapOf("x" to 1), 100) is CraftResult.Refused)
        assertTrue(e.attemptCraft("hero", CraftKind.CRAFT, mapOf("minerio" to 1), emptyMap(), 100) is CraftResult.Refused)
        assertTrue(e.attemptCraft("nao-existe", CraftKind.CRAFT, mapOf("minerio" to 1), mapOf("x" to 1), 100) is CraftResult.Refused)
        assertEquals(before, e.state)
    }

    @Test fun identificarTrocaAEntrada() {
        val e = engine()
        val r =
            e.attemptCraft(
                "hero",
                CraftKind.IDENTIFY,
                mapOf("espada-quebrada" to 1),
                mapOf("espada-real" to 1),
                100,
            ) as CraftResult.Done
        assertTrue(r.success)
        assertEquals(1, qty(e, "hero", "espada-real"))
        assertEquals(0, qty(e, "hero", "espada-quebrada"))
    }

    @Test fun afanarTransfereSoNoSucessoEFlagraNaFalha() {
        for (seed in 1L..20L) {
            val e = engine(seed)
            val r = e.attemptPilfer("hero", "guarda", "chave", dexterity = 50, agility = 0) as CraftResult.Done
            assertEquals(95, r.chance)
            assertEquals(r.success, r.roll!! <= 95)
            assertEquals(!r.success, r.caught)
            if (r.success) {
                assertEquals(1, qty(e, "hero", "chave"))
                assertEquals(0, qty(e, "guarda", "chave"))
                assertNull(e.state.relations[relationKey("guarda", "hero")])
            } else {
                assertEquals(0, qty(e, "hero", "chave"))
                assertEquals(1, qty(e, "guarda", "chave"))
                assertEquals(PILFER_CAUGHT_RELATION, e.state.relations[relationKey("guarda", "hero")])
            }
        }
    }

    @Test fun afanarRecusas() {
        val e = engine()
        assertTrue(e.attemptPilfer("hero", "hero", "chave", 1, 1) is CraftResult.Refused)
        assertTrue(e.attemptPilfer("hero", "guarda", "ouro", 1, 1) is CraftResult.Refused)
        e.execute(EquipItem("guarda", "chave", "mao"))
        assertTrue(e.attemptPilfer("hero", "guarda", "chave", 1, 1) is CraftResult.Refused)
    }

    @Test fun replayIgual() {
        val e = engine()
        e.attemptCraft("hero", CraftKind.CRAFT, mapOf("minerio" to 2), mapOf("barra" to 1), 50)
        e.attemptPilfer("hero", "guarda", "chave", 3, 3)
        assertEquals(e.state, GameEngine.restore(7, EngineConfig(), e.events).state)
    }
}
