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

    private fun catalog(compat: String = res("compat.txt")): Catalog =
        (CatalogLoader.load(res("estilos.txt"), res("sistemas.txt"), compat) as LoadResult.Success).catalog

    private fun interp(vararg replies: Any) = CampaignInterpreter(ScriptedProvider(replies.toList()))

    private val completo = """{"system":"3det","edition":"3det-victory","settings":["setting.cyberpunk"],"tones":["tone.sombrio"],
        "difficulty":"difficulty.normal","limits":["sem violência gráfica"],"concept":"anão minerador de máquinas de combate"}"""

    @Test fun descricaoCompletaViraConfiguracaoPronta() {
        val r = interp(completo).interpret("3D&T Victory, cyberpunk sombrio, normal, anão minerador", catalog()) as Interpretation.Ready
        assertEquals("3det", r.config.systemFamily)
        assertEquals("3det-victory", r.resolved.edition.id)
        assertEquals(listOf("setting.cyberpunk"), r.config.settingIds)
        assertEquals("anão minerador de máquinas de combate", r.draft.concept)
        assertEquals(listOf("sem violência gráfica"), r.config.contentLimits)
        // Sem regra registrada, a combinação é "não verificada" e o jogador vê o aviso.
        assertEquals(Compatibility.UNVERIFIED, r.resolved.compatibility)
        assertTrue(r.resolved.notes.any { "não foi verificada" in it })
    }

    @Test fun edicaoAmbiguaGeraPerguntaERespostaConclui() {
        val c = catalog()
        val i = interp("""{"system":"3det","settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}""")
        val ask = i.interpret("3D&T cyberpunk normal", c) as Interpretation.Ask
        assertEquals(DraftField.EDITION, ask.field)
        assertEquals(setOf("3det-alpha", "3det-victory"), ask.options.map { it.id }.toSet())
        val ready = i.answer(ask, "3det-victory", c) as Interpretation.Ready
        assertEquals("3det-victory", ready.resolved.edition.id)
    }

    @Test fun semSistemaPerguntaESemChutar() {
        val c = catalog()
        val i = interp("""{"system":null,"settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}""")
        val ask = i.interpret("quero cyberpunk", c) as Interpretation.Ask
        assertEquals(DraftField.SYSTEM, ask.field)
        assertTrue(ask.options.any { it.id == "3det" })
        val next = i.answer(ask, "cyberpunk-red", c)
        assertTrue(next is Interpretation.Ready)
    }

    @Test fun semDificuldadePergunta() {
        val c = catalog()
        val ask = interp("""{"system":"cyberpunk-red"}""").interpret("cyberpunk", c) as Interpretation.Ask
        assertEquals(DraftField.DIFFICULTY, ask.field)
        assertEquals(5, ask.options.size)
    }

    @Test fun idsInventadosSaoIgnoradosEAmbientacaoLivreFicaGuardada() {
        val c = catalog()
        val i = interp("""{"system":"3det","edition":"3det-victory","difficulty":"difficulty.normal",
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
        val ask2 = interp("""{"system":"3det","edition":"dnd-5-2-srd","difficulty":"difficulty.normal"}""").interpret("x", c) as Interpretation.Ask
        assertEquals(DraftField.EDITION, ask2.field)
    }

    @Test fun combinacaoBloqueadaImpedeIniciar() {
        val c = catalog("dnd-5-2-srd|setting.cyberpunk|BLOCKED|teste de bloqueio")
        val r = interp("""{"system":"dnd","edition":"dnd-5-2-srd","settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}""")
            .interpret("x", c)
        assertTrue(r is Interpretation.Invalid)
        assertTrue((r as Interpretation.Invalid).errors.any { "bloqueada" in it })
    }

    @Test fun respostaForaDasOpcoesERecusada() {
        val c = catalog()
        val i = interp("""{"system":"3det","difficulty":"difficulty.normal"}""")
        val ask = i.interpret("x", c) as Interpretation.Ask
        assertTrue(i.answer(ask, "dnd-5-2-srd", c) is Interpretation.Invalid)
    }

    @Test fun trocarDeSistemaLimpaAEdicaoAnterior() {
        val c = catalog()
        val i = CampaignInterpreter(ScriptedProvider(emptyList()))
        val ask = Interpretation.Ask(
            DraftField.SYSTEM, "?", listOf(Interpretation.Option("fate", "Fate")),
            CampaignDraft("x", systemFamily = null, editionId = "3det-victory", difficultyId = "difficulty.normal")
        )
        val r = i.answer(ask, "fate", c)
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
        assertTrue("setting.cyberpunk" in sys && "3det-victory" in sys && "difficulty.normal" in sys)
        assertNull(req.messages.firstOrNull { it.role == Role.ASSISTANT })
    }
}
