package dev.rpgsolo.ai

import dev.rpgsolo.engine.*
import org.junit.Assert.*
import org.junit.Test

class AgendaProposalTest {
    private fun ok(raw: String) = (ProposalParser.parse(raw) as ParseResult.Ok).output
    private fun session() = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 1L, EngineConfig())).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)),
            CreateEntity(Entity("ana", "Ana", EntityKind.NPC))
        ))
    }

    @Test fun iaPodePropor() {
        val o = ok("""{"narrative":"x","proposals":[
            {"type":"change_relation","fromId":"ana","toId":"hero","delta":20,"reason":"ajuda"},
            {"type":"create_clock","id":"c","name":"Culto","segments":4},
            {"type":"advance_clock","id":"c"}]}""")
        assertEquals(listOf(ChangeRelation("ana", "hero", 20, "ajuda"), CreateClock("c", "Culto", 4), AdvanceClock("c", 1)), o.proposals)
    }

    @Test fun camposFaltandoSaoInvalidos() {
        assertTrue(ProposalParser.parse("""{"narrative":"x","proposals":[{"type":"change_relation","fromId":"a","toId":"b","delta":1}]}""") is ParseResult.Invalid)
    }

    @Test fun contextoMostraRelacaoEmPalavrasERelogioPrivado() {
        val s = session()
        s.executeAll(listOf(ChangeRelation("ana", "hero", 35, "ajuda"), CreateClock("c", "Culto invoca", 2), AdvanceClock("c", 2)))
        val user = ContextAssembler.build(ContextInput(s.state, "hero", "olho")).messages.last().content
        assertTrue("Ana em relação a Herói: confiança" in user)
        assertFalse("35" in user)
        assertTrue("Culto invoca: 2/2 — COMPLETO" in user)
    }

    @Test fun semRelacoesNemRelogiosNadaEntra() {
        val user = ContextAssembler.build(ContextInput(session().state, "hero", "olho")).messages.last().content
        assertFalse("Relações" in user || "Relógios" in user)
    }
}
