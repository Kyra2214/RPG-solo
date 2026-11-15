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

/** A campanha começa com o mestre narrando a cena e já oferecendo escolhas; o pedido interno não aparece no chat. */
class AberturaCampanhaTest {
    private fun session() =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "pv"))).also {
            it.executeAll(
                listOf(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10))))),
            )
        }

    private fun ctrl(
        s: CampaignSession,
        ai: ScriptedProvider,
    ) = PlayController(s, GameLoop(ai), "hero", "pv", { null })

    @Test fun aberturaNarraSemLinhaDoJogadorEOferecePcoes() {
        val ai =
            ScriptedProvider(
                listOf("""{"narrative":"A praça acorda sob a névoa.","suggestions":["Seguir a voz","Ignorar","Observar a praça"]}"""),
            )
        val s = session()
        val c = ctrl(s, ai)
        assertTrue(c.needsOpening())
        assertTrue(c.openScene() is PlayResult.Played)
        assertEquals(listOf(Author.MASTER), c.lines.map { it.author })
        assertEquals(3, c.suggestions().size)
        assertFalse(c.needsOpening())
        assertEquals("sim", s.state.facts[PlayController.OPENING_FACT])
    }

    @Test fun naoAbreDeNovoDepoisDeJaAberta() {
        val ai = ScriptedProvider(listOf("""{"narrative":"Cena."}"""))
        val c = ctrl(session(), ai)
        c.openScene()
        assertTrue(c.openScene() is PlayResult.Failed)
    }

    @Test fun falhaNaAberturaNaoRepeteSozinha() {
        val ai = ScriptedProvider(listOf(AiError.BadResponse("vazio"), AiError.BadResponse("vazio"), AiError.BadResponse("vazio")))
        val c = ctrl(session(), ai)
        assertTrue(c.openScene() is PlayResult.Failed)
        assertFalse(c.needsOpening())
    }
}
