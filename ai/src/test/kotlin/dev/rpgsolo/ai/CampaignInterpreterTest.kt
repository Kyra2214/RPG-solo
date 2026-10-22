package dev.rpgsolo.ai

import dev.rpgsolo.rules.CatalogLoader
import dev.rpgsolo.rules.Catalog
import dev.rpgsolo.rules.Compatibility
import dev.rpgsolo.rules.LoadResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CampaignInterpreterTest {
    private fun res(name: String) =
        CampaignInterpreterTest::class.java.classLoader!!.getResourceAsStream("catalog/$name")!!.bufferedReader().readText()

    // Sistema próprio embutido + sistemas fictícios de teste (uma família com duas edições e outra com uma).
    private val sistemas: String = res("sistemas.txt") + "\n" + """
        alfa-1|alfa|Sistema Alfa|1|generico|PENDENTE|PENDENTE|teste
        alfa-2|alfa|Sistema Alfa|2|generico|PENDENTE|PENDENTE|teste
        beta-1|beta|Sistema Beta|1|generico|PENDENTE|PENDENTE|teste
    """.trimIndent()

    private fun catalog(compat: String = res("compat.txt")): Catalog =
        (CatalogLoader.load(res("estilos.txt"), sistemas, compat) as LoadResult.Success).catalog

    private fun interp(vararg replies: Any) = CampaignInterpreter(ScriptedProvider(replies.toList()))

    private val completo = """{"system":"alfa","edition":"alfa-2","settings":["setting.cyberpunk"],"tones":["tone.sombrio"],
        "difficulty":"difficulty.normal","limits":["sem violência gráfica"],"concept":"anão minerador de máquinas de combate"}"""

    @Test fun descricaoCompletaViraConfiguracaoPronta() {
        val r = interp(completo).interpret("Sistema Alfa 2, cyberpunk sombrio, normal, anão minerador", catalog()) as Interpretation.Ready
        assertEquals("alfa", r.config.systemFamily)
        assertEquals("alfa-2", r.resolved.edition.id)
        assertEquals(listOf("setting.cyberpunk"), r.config.settingIds)
        assertEquals("anão minerador de máquinas de combate", r.draft.concept)
        assertEquals(listOf("sem violência gráfica"), r.config.contentLimits)
        // Sem regra registrada, a combinação é "não verificada" e o jogador vê o aviso.
        assertEquals(Compatibility.UNVERIFIED, r.resolved.compatibility)
        assertTrue(r.resolved.notes.any { "não foi verificada" in it })
    }

    @Test fun edicaoAmbiguaGeraPerguntaERespostaConclui() {
        val c = catalog()
        val i = interp("""{"system":"alfa","settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}""")
        val ask = i.interpret("Sistema Alfa cyberpunk normal", c) as Interpretation.Ask
        assertEquals(DraftField.EDITION, ask.field)
        assertEquals(setOf("alfa-1", "alfa-2"), ask.options.map { it.id }.toSet())
        val ready = i.answer(ask, "alfa-2", c) as Interpretation.Ready
        assertEquals("alfa-2", ready.resolved.edition.id)
    }

    @Test fun semSistemaPerguntaESemChutar() {
        val c = catalog()
        val i = interp("""{"system":null,"settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}""")
        val ask = i.interpret("quero cyberpunk", c) as Interpretation.Ask
        assertEquals(DraftField.SYSTEM, ask.field)
        assertTrue(ask.options.any { it.id == "alfa" })
        val next = i.answer(ask, "beta", c)
        assertTrue(next is Interpretation.Ready)
    }

    @Test fun semDificuldadePergunta() {
        val c = catalog()
        val ask = interp("""{"system":"beta"}""").interpret("cyberpunk", c) as Interpretation.Ask
        assertEquals(DraftField.DIFFICULTY, ask.field)
        assertEquals(5, ask.options.size)
    }

    @Test fun idsInventadosSaoIgnoradosEAmbientacaoLivreFicaGuardada() {
        val c = catalog()
        val i = interp("""{"system":"alfa","edition":"alfa-2","difficulty":"difficulty.normal",
            "settings":["setting.inventado","setting.cyberpunk"],"tones":["setting.cyberpunk"],
            "custom_setting":"cyberpunk com mineração em asteroides"}""")
        val r = i.interpret("x", c) as Interpretation.Ready
        assertEquals(listOf("setting.cyberpunk"), r.config.settingIds)
        assertTrue("setting.inventado" in r.draft.ignored)
        assertTrue("setting.cyberpunk" in r.draft.ignored) // id de ambientação usado como tom
        assertTrue(r.config.toneIds.isEmpty())
        assertEquals("cyberpunk com mineração em asteroides", r.draft.customSetting)
    }

    @Test fun sistemaOuEdicaoInexistenteViraPerguntaNaoErro() {
        val c = catalog()
        val ask = interp("""{"system":"sistema-fantasma","difficulty":"difficulty.normal"}""").interpret("x", c) as Interpretation.Ask
        assertEquals(DraftField.SYSTEM, ask.field)
        // edição de outra família é descartada, o motor pergunta a edição certa
        val ask2 = interp("""{"system":"alfa","edition":"beta-1","difficulty":"difficulty.normal"}""").interpret("x", c) as Interpretation.Ask
        assertEquals(DraftField.EDITION, ask2.field)
    }

    @Test fun combinacaoBloqueadaImpedeIniciar() {
        val c = catalog("beta-1|setting.cyberpunk|BLOCKED|teste de bloqueio")
        val r = interp("""{"system":"beta","edition":"beta-1","settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}""")
            .interpret("x", c)
        assertTrue(r is Interpretation.Invalid)
        assertTrue((r as Interpretation.Invalid).errors.any { "bloqueada" in it })
    }

    @Test fun respostaForaDasOpcoesERecusada() {
        val c = catalog()
        val i = interp("""{"system":"alfa","difficulty":"difficulty.normal"}""")
        val ask = i.interpret("x", c) as Interpretation.Ask
        assertTrue(i.answer(ask, "beta-1", c) is Interpretation.Invalid)
    }

    @Test fun trocarDeSistemaLimpaAEdicaoAnterior() {
        val c = catalog()
        val i = CampaignInterpreter(ScriptedProvider(emptyList()))
        val ask = Interpretation.Ask(
            DraftField.SYSTEM, "?", listOf(Interpretation.Option("alfa", "Alfa")),
            CampaignDraft("x", systemFamily = null, editionId = "beta-1", difficultyId = "difficulty.normal")
        )
        val r = i.answer(ask, "alfa", c)
        assertTrue(r is Interpretation.Ask && r.field == DraftField.EDITION)
    }

    @Test fun falhaDeIaOuRespostaIlegivelNaoCriamNada() {
        val c = catalog()
        assertTrue(interp(AiError.NoInternet()).interpret("x", c) is Interpretation.Failed)
        assertTrue(interp("não sei").interpret("x", c) is Interpretation.Failed)
        assertTrue(interp().interpret("   ", c) is Interpretation.Failed)
    }

    @Test fun pedidoUsaModeloEmJsonComCatalogoNoPrompt() {
        val ai = ScriptedProvider(listOf(completo))
        CampaignInterpreter(ai).interpret("x", catalog())
        val req = ai.requests.single()
        assertTrue(req.jsonMode)
        val sys = req.messages.first().content
        assertTrue("setting.cyberpunk" in sys && "alfa-2" in sys && "difficulty.normal" in sys)
        assertNull(req.messages.firstOrNull { it.role == Role.ASSISTANT })
    }
}
