package dev.rpgsolo.ai

import dev.rpgsolo.engine.*
import org.junit.Assert.*
import org.junit.Test

class AgendaProposalTest {
    private fun ok(raw: String) = (ProposalParser.parse(raw) as ParseResult.Ok).output

    private fun session() =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 1L, EngineConfig())).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)),
                    CreateEntity(Entity("ana", "Ana", EntityKind.NPC)),
                ),
            )
        }

    @Test fun iaPodePropor() {
        val o =
            ok(
                """{"narrative":"x","proposals":[
            {"type":"change_relation","fromId":"ana","toId":"hero","delta":20,"reason":"ajuda"},
            {"type":"create_clock","id":"c","name":"Culto","segments":4,"combatTick":1,"questTick":2},
            {"type":"advance_clock","id":"c"}]}""")
        assertEquals(listOf(ChangeRelation("ana", "hero", 20, "ajuda"), CreateClock("c", "Culto", 4, combatTick = 1, questTick = 2), AdvanceClock("c", 1)), o.proposals)
    }

    @Test fun camposFaltandoSaoInvalidos() {
        assertTrue(
            ProposalParser.parse(
                """{"narrative":"x","proposals":[{"type":"change_relation","fromId":"a","toId":"b","delta":1}]}""",
            ) is ParseResult.Invalid,
        )
    }

    @Test fun instrucoesPedemPistasContextuaisSemInventarFatosOuVazarSegredos() {
        val instructions = ProposalSchema.INSTRUCTIONS
        assertTrue("sinais observáveis, concretos e contextualizados" in instructions)
        assertTrue("fatos já estabelecidos" in instructions)
        assertTrue("Não invente um acontecimento como já ocorrido" in instructions)
        assertTrue("se não houver uma pista segura e coerente, omita-a" in instructions)
        assertTrue("nunca coloque nela o nome interno do relógio, a contagem ou segredo privado" in instructions)
        assertTrue("questTick" in instructions)
        assertTrue("quando uma missão é encerrada como concluída ou falha" in instructions)
    }

    @Test fun contextoMostraRelacaoEmPalavrasERelogioPrivado() {
        val s = session()
        s.executeAll(listOf(ChangeRelation("ana", "hero", 35, "ajuda"), CreateClock("c", "Culto invoca", 2), AdvanceClock("c", 2)))
        val user =
            ContextAssembler.build(
                ContextInput(
                    s.state,
                    "hero",
                    "olho",
                    sceneNpcIds = setOf("ana"),
                    advancedClockIds = setOf("c"),
                ),
            ).messages.last().content
        assertTrue("Ana em relação a Herói: confiança" in user)
        assertFalse("35" in user)
        assertTrue("Culto invoca: 2/2 — COMPLETO nesta jogada" in user)
    }

    @Test fun semRelacoesNemRelogiosNadaEntra() {
        val user = ContextAssembler.build(ContextInput(session().state, "hero", "olho")).messages.last().content
        assertFalse("Relações" in user || "Relógios" in user)
    }

    @Test fun contextoIncluiApenasMemoriasDosNpcsDaCena() {
        val s = session()
        s.executeAll(
            listOf(
                NoteMemory("ana", "gosta de chá"),
                CreateEntity(Entity("fora", "Pessoa distante", EntityKind.NPC)),
                NoteMemory("fora", "segredo distante"),
            ),
        )
        val user =
            ContextAssembler.build(
                ContextInput(s.state, "hero", "conversar", sceneNpcIds = setOf("ana")),
            ).messages.last().content
        assertTrue("Ana: gosta de chá" in user)
        assertFalse("segredo distante" in user)
        assertFalse("Pessoa distante" in user)
    }

    @Test fun pistaPublicaDoRelogioSoEExpostaQuandoCompleta() {
        val o =
            ok(
                """{"narrative":"x","proposals":[
            {"type":"create_clock","id":"ameaca","name":"Plano oculto","segments":2,
             "publicClue":"As luzes da cidade se apagam.",
             "midClue":"Uma fumaça estranha cobre o céu.",
             "consequence":"O culto abre o portal para a entidade."}]}""")
        val create = o.proposals.single() as CreateClock
        assertEquals("As luzes da cidade se apagam.", create.publicClue)
        assertEquals("Uma fumaça estranha cobre o céu.", create.midClue)
        assertEquals("O culto abre o portal para a entidade.", create.consequence)
        val s = session()
        s.execute(create)
        s.execute(AdvanceClock("ameaca", 1))
        val midpointContext = ContextAssembler.build(ContextInput(
            s.state, "hero", "observar", advancedClockIds = setOf("ameaca"), midpointClockIds = setOf("ameaca")
        )).messages.last().content
        assertTrue("Uma fumaça estranha cobre o céu." in midpointContext)
        assertFalse("O culto abre o portal" in midpointContext)
        val midpointTimeline = automaticTimeline(s.engine.events, "hero", s.state)
        assertTrue(midpointTimeline.none { it.text.contains("luzes da cidade") })
        assertTrue(midpointTimeline.any { it.text == "Uma fumaça estranha cobre o céu." })
        s.execute(AdvanceClock("ameaca", 1))
        val completionContext = ContextAssembler.build(ContextInput(
            s.state, "hero", "observar", advancedClockIds = setOf("ameaca")
        )).messages.last().content
        assertTrue("O culto abre o portal para a entidade." in completionContext)
        val timeline = automaticTimeline(s.engine.events, "hero", s.state)
        assertTrue(timeline.any { it.text == "As luzes da cidade se apagam." })
        assertFalse(timeline.any {
            it.text.contains("Plano oculto") || it.text.contains("ameaca") ||
                it.text.contains("entidade") || it.text.contains("2/2")
        })
    }
}
