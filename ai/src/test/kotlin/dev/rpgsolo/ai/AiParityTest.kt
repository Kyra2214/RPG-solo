package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.StartCombat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** MU-08.3: em combate a IA propõe só intenção; jogador e NPC recebem o mesmo tratamento do motor. */
class AiParityTest {
    private val store = InMemoryEventStore()
    private val session =
        CampaignSession.create(store, CampaignRecord("c1", "Teste", 1L, EngineConfig())).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(10, 10)))),
                    CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6)))),
                    CreateEntity(Entity("elfo", "Elfo", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6)))),
                ),
            )
            it.execute(StartCombat(listOf("hero", "orc")))
        }
    private val request = AiRequest(listOf(AiMessage(Role.USER, "ataco")))

    private fun hp(id: String) = session.state.entities.getValue(id).resources.getValue("hp").current

    private fun proposal(json: String) = """{"narrative":"x","proposals":[$json]}"""

    private fun assertRefusedInCombat(json: String) {
        val ai = ScriptedProvider(listOf(proposal(json), proposal(json)))
        val before = session.engine.eventCount
        val out = TurnRunner(ai).run(session, request)
        assertFalse(out is TurnOutcome.Applied)
        assertEquals(before, session.engine.eventCount)
        assertTrue(ai.requests[1].messages.last().content.contains("só mudam por ações do motor"))
    }

    @Test fun danoDaIaEmNpcEmCombateERecusado() = assertRefusedInCombat("""{"type":"damage","entityId":"orc","resource":"hp","amount":3}""")

    @Test fun danoDaIaNoJogadorEmCombateERecusado() =
        assertRefusedInCombat(
            """{"type":"damage","entityId":"hero","resource":"hp","amount":3}""",
        )

    @Test fun curaDaIaNosDoisLadosEmCombateERecusada() {
        assertRefusedInCombat("""{"type":"heal","entityId":"orc","resource":"hp","amount":2}""")
        assertRefusedInCombat("""{"type":"heal","entityId":"hero","resource":"hp","amount":2}""")
    }

    @Test fun custoECondicaoDaIaNosDoisLadosEmCombateSaoRecusados() {
        assertRefusedInCombat("""{"type":"spend_resource","entityId":"orc","resource":"hp","cost":1}""")
        assertRefusedInCombat("""{"type":"spend_resource","entityId":"hero","resource":"hp","cost":1}""")
        assertRefusedInCombat("""{"type":"add_condition","entityId":"orc","condition":"veneno"}""")
        assertRefusedInCombat("""{"type":"remove_condition","entityId":"hero","condition":"veneno"}""")
    }

    @Test fun pontosDeVidaNaoMudamQuandoAIaTentaImporResultado() {
        val ruim = proposal("""{"type":"damage","entityId":"orc","resource":"hp","amount":6}""")
        TurnRunner(ScriptedProvider(listOf(ruim, ruim))).run(session, request)
        assertEquals(6, hp("orc"))
        assertEquals(10, hp("hero"))
    }

    @Test fun quemNaoEstaNaOrdemDoCombateSegueAsRegrasAnteriores() {
        val dano = proposal("""{"type":"damage","entityId":"elfo","resource":"hp","amount":2}""")
        val out = TurnRunner(ScriptedProvider(listOf(dano))).run(session, request)
        assertTrue(out is TurnOutcome.Applied)
        assertEquals(4, hp("elfo"))
    }

    @Test fun narracaoPuraContinuaPermitidaEmCombate() {
        val before = session.engine.eventCount
        val out = TurnRunner(ScriptedProvider(listOf("""{"narrative":"O orc rosna."}"""))).run(session, request)
        assertTrue(out is TurnOutcome.Applied)
        assertEquals(before, session.engine.eventCount)
    }
}
