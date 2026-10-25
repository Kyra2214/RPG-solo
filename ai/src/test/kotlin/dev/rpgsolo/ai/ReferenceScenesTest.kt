package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.NoteMemory
import dev.rpgsolo.engine.RollResult
import dev.rpgsolo.engine.resolveCheck
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** I-07: cenas mínimas gravadas para detectar regressões na estrutura do contexto do mestre. */
class ReferenceScenesTest {
    private data class ReferenceScene(
        val name: String,
        val input: ContextInput,
        val required: List<String>,
        val forbidden: List<String> = emptyList(),
    )

    private fun session(withNpc: Boolean = false): CampaignSession =
        CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("reference", "Cenas de referência", 1L, EngineConfig()),
        ).also { session ->
            session.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)))
            if (withNpc) session.execute(CreateEntity(Entity("ana", "Ana", EntityKind.NPC)))
        }

    private fun request(scene: ReferenceScene): String = ContextAssembler.build(scene.input).messages.last().content

    private fun scenes(): List<ReferenceScene> {
        val simple = session()
        val check = session()
        val checkResult =
            resolveCheck(
                CheckSpec("Perceber a passagem", 1, 6, difficulty = 4, comparison = Comparison.ROLL_OVER),
                RollResult("1d6", listOf(5), 0, 5),
            )
        val memory =
            session(withNpc = true).also {
                it.executeAll(
                    listOf(
                        NoteMemory("ana", "prefere negociar antes de lutar"),
                        CreateEntity(Entity("longe", "Pessoa distante", EntityKind.NPC)),
                        NoteMemory("longe", "segredo que não pertence à cena"),
                    ),
                )
            }
        return listOf(
            ReferenceScene(
                name = "ação simples",
                input = ContextInput(simple.state, "hero", "abro a porta"),
                required = listOf("Ação do jogador: abro a porta", "Personagens e estado:"),
                forbidden = listOf("Resultado decidido pelo motor"),
            ),
            ReferenceScene(
                name = "teste com desfecho narrável",
                input = ContextInput(check.state, "hero", "examino a parede", check = checkResult),
                required = listOf("Resultado decidido pelo motor", "sucesso por pouco", "sem mencionar dados"),
                forbidden = listOf("= 5", "margem: 1"),
            ),
            ReferenceScene(
                name = "memória limitada à cena",
                input = ContextInput(memory.state, "hero", "converso com Ana", sceneNpcIds = setOf("ana")),
                required = listOf("Memórias relevantes da cena", "Ana: prefere negociar antes de lutar"),
                forbidden = listOf("segredo que não pertence à cena", "Pessoa distante"),
            ),
        )
    }

    @Test
    fun cenasDeReferenciaMantemEstruturaObrigatoria() {
        scenes().forEach { scene ->
            val content = request(scene)
            scene.required.forEach { expected ->
                assertTrue("cena '${scene.name}' deveria conter '$expected'", expected in content)
            }
            scene.forbidden.forEach { unexpected ->
                assertFalse("cena '${scene.name}' não deveria conter '$unexpected'", unexpected in content)
            }
        }
    }

    @Test
    fun parserDePropostaMantemFormatoNarrativo() {
        val parsed =
            ProposalParser.parse(
                """{"narrative":"A porta cede.","suggestions":["entrar"],"proposals":[]}""",
            )
        assertTrue(parsed is ParseResult.Ok)
        val output = (parsed as ParseResult.Ok).output
        assertTrue(output.narrative.isNotBlank())
        assertTrue(output.suggestions.single() == "entrar")
        assertTrue(output.proposals.isEmpty())
    }

    @Test
    fun promptDoMestreMantemRegrasEPedidosDoEsquema() {
        val prompt = MasterPrompt.SYSTEM
        listOf(
            "Imersão",
            "Ciclo de cena e turno",
            "Oportunidades de ofício",
            "request_check",
            "request_roll",
            "request_npc",
            "request_combat",
            "request_power",
            "request_attack",
            "request_quest",
            "create_clock",
            "record_memory",
        ).forEach { expected ->
            assertTrue("o prompt do mestre deveria conter '$expected'", expected in prompt)
        }
    }

    @Test
    fun pedidoDeMissaoValidoViraQuestRequest() {
        val parsed =
            ProposalParser.parse(
                """{"narrative":"O ferreiro pede ajuda.","request_quest":{"title":"Ferro perdido","objectives":["achar a mina","voltar"],"xpReward":30}}""",
            )
        assertTrue(parsed is ParseResult.Ok)
        val quest = (parsed as ParseResult.Ok).output.questRequest
        assertTrue(quest != null && quest.title == "Ferro perdido" && quest.objectives.size == 2 && quest.xpReward == 30)
    }

    @Test
    fun pedidoDeMissaoDeveVirSozinhoEComTituloEXpValidos() {
        val withProposals =
            ProposalParser.parse(
                """{"narrative":"x","proposals":[{"type":"advance_clock","id":"a","by":1}],"request_quest":{"title":"T"}}""",
            )
        val noTitle = ProposalParser.parse("""{"narrative":"x","request_quest":{"objectives":["a"]}}""")
        val badXp = ProposalParser.parse("""{"narrative":"x","request_quest":{"title":"T","xpReward":"muito"}}""")
        listOf(withProposals, noTitle, badXp).forEach { assertTrue(it is ParseResult.Invalid) }
    }

    @Test
    fun lembreteDeOficioSoApareceAteOMestreRegistrarOFato() {
        val base = dev.rpgsolo.engine.GameState()

        fun text(state: dev.rpgsolo.engine.GameState): String =
            ContextAssembler.build(ContextInput(state, "hero", "olho ao redor", craftOpportunity = true)).messages.last().content
        assertTrue("regra 18" in text(base))
        assertTrue("regra 18" !in text(base.copy(facts = mapOf(CRAFT_OFFER_FACT to "sim"))))
        assertTrue("regra 18" !in ContextAssembler.build(ContextInput(base, "hero", "olho ao redor")).messages.last().content)
    }
}
