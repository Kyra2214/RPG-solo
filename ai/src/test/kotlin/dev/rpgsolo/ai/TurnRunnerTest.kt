package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TurnRunnerTest {
    private val store = InMemoryEventStore()
    private val session = CampaignSession.create(store, CampaignRecord("c1", "Teste", 1L, EngineConfig())).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(10, 10)))),
            CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6))))
        ))
    }
    private val request = AiRequest(listOf(AiMessage(Role.USER, "ataco o orc")))
    private fun orcHp() = session.state.entities.getValue("orc").resources.getValue("hp").current

    private val dano3 = """{"narrative":"A lâmina acerta.","proposals":[{"type":"damage","entityId":"orc","resource":"hp","amount":3}]}"""

    @Test fun propostaValidaEAplicadaESalva() {
        val saved = store.size("c1")
        val out = TurnRunner(ScriptedProvider(listOf(dano3))).run(session, request) as TurnOutcome.Applied
        assertEquals("A lâmina acerta.", out.narrative)
        assertEquals(3, orcHp())
        assertTrue(store.size("c1") > saved)
    }

    @Test fun formatoRuimGeraUmaCorrecaoEDepoisFunciona() {
        val ai = ScriptedProvider(listOf("desculpe, não sei", dano3))
        val out = TurnRunner(ai).run(session, request)
        assertTrue(out is TurnOutcome.Applied)
        assertEquals(2, ai.requests.size)
        assertTrue(ai.requests[1].messages.last().content.contains("recusada"))
        assertEquals(3, orcHp())
    }

    @Test fun formatoRuimDuasVezesFalhaSemMexerNoEstado() {
        val before = session.engine.eventCount
        val out = TurnRunner(ScriptedProvider(listOf("x", "y"))).run(session, request)
        assertTrue(out is TurnOutcome.Failed)
        assertEquals(before, session.engine.eventCount)
    }

    @Test fun motorRecusaEIaCorrige() {
        val morto = """{"narrative":"x","proposals":[{"type":"heal","entityId":"ghost","resource":"hp","amount":2}]}"""
        val ai = ScriptedProvider(listOf(morto, dano3))
        assertTrue(TurnRunner(ai).run(session, request) is TurnOutcome.Applied)
        assertTrue(ai.requests[1].messages.last().content.contains("motor recusou"))
    }

    @Test fun motorRecusaDuasVezesNadaMuda() {
        val ruim = """{"narrative":"x","proposals":[{"type":"damage","entityId":"orc","resource":"hp","amount":1},{"type":"heal","entityId":"ghost","resource":"hp","amount":2}]}"""
        val before = session.engine.eventCount
        val out = TurnRunner(ScriptedProvider(listOf(ruim, ruim))).run(session, request)
        assertTrue(out is TurnOutcome.Refused)
        assertEquals(before, session.engine.eventCount)
        assertEquals(6, orcHp()) // lote atômico: o primeiro dano também não valeu
    }

    @Test fun falhaDeApiNaoCorrompeEstado() {
        val ai = ScriptedProvider(listOf(AiError.InvalidKey()))
        val before = session.engine.eventCount
        val out = TurnRunner(ai).run(session, request) as TurnOutcome.Failed
        assertTrue(out.error is AiError.InvalidKey)
        assertEquals(before, session.engine.eventCount)
        assertEquals(1, ai.requests.size)
    }

    @Test fun iaNaoConsegueMudarResultadoDeDado() {
        val rola = """{"narrative":"x","proposals":[{"type":"roll","expr":"1d20"}]}"""
        val before = session.engine.eventCount
        val out = TurnRunner(ScriptedProvider(listOf(rola, rola))).run(session, request)
        assertFalse(out is TurnOutcome.Applied)
        assertEquals(before, session.engine.eventCount)
    }

    @Test fun soNarracaoNaoGravaEventos() {
        val before = session.engine.eventCount
        val out = TurnRunner(ScriptedProvider(listOf("""{"narrative":"O vento sopra."}"""))).run(session, request) as TurnOutcome.Applied
        assertTrue(out.events.isEmpty())
        assertEquals(before, session.engine.eventCount)
    }

    @Test fun pedidoSempreSaiEmModoJson() {
        val ai = ScriptedProvider(listOf("""{"narrative":"ok"}"""))
        TurnRunner(ai).run(session, request)
        assertTrue(ai.requests.single().jsonMode)
    }
}
