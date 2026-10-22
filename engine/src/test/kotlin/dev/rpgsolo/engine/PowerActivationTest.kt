package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PowerActivationTest {
    private fun session() = CampaignSession.create(
        InMemoryEventStore(), CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "pv"))
    ).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(5, 10), "pm" to Resource(3, 3)))),
            CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(30, 30))))
        ))
    }

    private fun res(s: CampaignSession, id: String, r: String) = s.state.entities.getValue(id).resources.getValue(r).current

    @Test fun curaGastaCustoEAumentaRecurso() {
        val s = session()
        val r = s.activatePower(PowerSpec("regen", "hero", null, "pm", 1, PowerEffect.Cure("pv", 2)))
        r as PowerResult.Activated
        assertTrue(r.effectApplied)
        assertEquals(2, r.resourceDelta)
        assertEquals(7, res(s, "hero", "pv"))
        assertEquals(2, res(s, "hero", "pm"))
    }

    @Test fun danoERoladoPeloMotorERegistrado() {
        val s = session()
        val r = s.activatePower(PowerSpec("rajada", "hero", "orc", "pm", 1, PowerEffect.Strike("pv", 1, 6, bonus = 1)))
        r as PowerResult.Activated
        val roll = r.damageRoll
        assertNotNull(roll)
        assertTrue(roll!!.total in 2..7)
        assertEquals(-roll.total, r.resourceDelta)
        assertEquals(30 - roll.total, res(s, "orc", "pv"))
        assertTrue(s.events.any { it is RollRecorded && it.label == "poder:rajada->orc" })
    }

    @Test fun testeFalhoGastaCustoSemEfeito() {
        val s = session()
        val check = CheckSpec("magia", 1, 20, 0, 99)
        val r = s.activatePower(PowerSpec("regen", "hero", null, "pm", 1, PowerEffect.Cure("pv", 2), check))
        r as PowerResult.Activated
        assertFalse(r.effectApplied)
        assertNotNull(r.check)
        assertEquals(0, r.resourceDelta)
        assertEquals(5, res(s, "hero", "pv"))
        assertEquals(2, res(s, "hero", "pm"))
    }

    @Test fun testeComSucessoAplicaEfeito() {
        val s = session()
        val check = CheckSpec("magia", 1, 20, 0, 1)
        val r = s.activatePower(PowerSpec("regen", "hero", null, "pm", 1, PowerEffect.Cure("pv", 2), check))
        assertTrue((r as PowerResult.Activated).effectApplied)
        assertEquals(7, res(s, "hero", "pv"))
    }

    @Test fun recursoInsuficienteRecusaSemAlterarNada() {
        val s = session()
        val before = s.eventCount
        val r = s.activatePower(PowerSpec("regen", "hero", null, "pm", 4, PowerEffect.Cure("pv", 2)))
        assertTrue(r is PowerResult.Rejected)
        assertEquals(before, s.eventCount)
        assertEquals(3, res(s, "hero", "pm"))
    }

    @Test fun efeitoRecusadoDevolveOCusto() {
        val s = session()
        val before = s.eventCount
        val r = s.activatePower(PowerSpec("regen", "hero", null, "pm", 1, PowerEffect.Cure("inexistente", 2)))
        assertTrue(r is PowerResult.Rejected)
        assertEquals(before, s.eventCount)
        assertEquals(3, res(s, "hero", "pm"))
    }

    @Test fun alvoInvalidoERecusado() {
        val s = session()
        assertTrue(s.activatePower(PowerSpec("r", "hero", null, "pm", 1, PowerEffect.Strike("pv", 1, 6))) is PowerResult.Rejected)
        assertTrue(s.activatePower(PowerSpec("c", "hero", "orc", "pm", 1, PowerEffect.Cure("pv", 1))) is PowerResult.Rejected)
        assertTrue(s.activatePower(PowerSpec("r", "hero", "ninguem", "pm", 1, PowerEffect.Strike("pv", 1, 6))) is PowerResult.Rejected)
        assertEquals(3, res(s, "hero", "pm"))
    }

    @Test fun condicaoAplicadaERemovida() {
        val s = session()
        val inflict = s.activatePower(PowerSpec("veneno", "hero", "orc", "pm", 1, PowerEffect.Inflict("envenenado", 3, 1, "pv")))
        assertTrue(inflict is PowerResult.Activated)
        assertTrue("envenenado" in s.state.conditions["orc"].orEmpty())
        s.execute(AddCondition("hero", "envenenado"))
        val cleanse = s.activatePower(PowerSpec("purificar", "hero", null, "pm", 0, PowerEffect.Cleanse("envenenado")))
        assertTrue(cleanse is PowerResult.Activated)
        assertFalse("envenenado" in s.state.conditions["hero"].orEmpty())
        assertTrue(s.activatePower(PowerSpec("purificar", "hero", null, "pm", 0, PowerEffect.Cleanse("envenenado"))) is PowerResult.Rejected)
    }

    @Test fun logSalvoAcompanhaOMotor() {
        val store = InMemoryEventStore()
        val s = CampaignSession.create(store, CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "pv")))
        s.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(5, 10), "pm" to Resource(3, 3)))),
            CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(30, 30))))
        ))
        s.activatePower(PowerSpec("rajada", "hero", "orc", "pm", 1, PowerEffect.Strike("pv", 1, 6)))
        assertEquals(s.eventCount, store.size("c"))
        val reopened = CampaignSession.open(store, "c")
        assertEquals(res(s, "orc", "pv"), res(reopened, "orc", "pv"))
        assertEquals(2, res(reopened, "hero", "pm"))
    }
}
