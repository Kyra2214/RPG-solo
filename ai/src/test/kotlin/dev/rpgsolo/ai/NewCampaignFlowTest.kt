package dev.rpgsolo.ai

import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.SystemRef
import dev.rpgsolo.rules.CatalogLoader
import dev.rpgsolo.rules.FichaBuild
import dev.rpgsolo.rules.InstallResult
import dev.rpgsolo.rules.LoadResult
import dev.rpgsolo.rules.PackageRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NewCampaignFlowTest {
    private fun res(path: String) = NewCampaignFlowTest::class.java.classLoader!!.getResourceAsStream(path)!!.bufferedReader().readText()

    // Sistema próprio embutido + sistemas fictícios de teste.
    private val sistemas =
        res("catalog/sistemas.txt") + "\n" +
            """
            alfa-1|alfa|Sistema Alfa|1|generico|PENDENTE|PENDENTE|teste
            alfa-2|alfa|Sistema Alfa|2|generico|PENDENTE|PENDENTE|teste
            beta-1|beta|Sistema Beta|1|generico|PENDENTE|PENDENTE|teste
            """.trimIndent()

    private val catalog =
        (
            CatalogLoader.load(
                res("catalog/estilos.txt"),
                sistemas,
                res("catalog/compat.txt"),
            ) as LoadResult.Success
        ).catalog

    private val registry =
        PackageRegistry(catalog).also {
            val files =
                listOf("manifest", "atributos", "recursos", "tracos", "testes", "dificuldades", "condicoes", "dano", "itens", "criacao", "progressao", "incerto", "tutorial", "exemplos", "arquetipos", "opcoes-arquetipo")
                    .mapNotNull { f -> runCatching { "$f.txt" to res("packages/sistema-proprio/$f.txt") }.getOrNull() }.toMap()
            val installed = it.install(files)
            assertTrue("pacote sistema-proprio deve instalar: $installed", installed is InstallResult.Success)
        }

    private val store = InMemoryEventStore()

    private fun flow(vararg replies: Any) =
        NewCampaignFlow(CampaignInterpreter(ScriptedProvider(replies.toList())), catalog, registry, store) { "camp1" }

    private fun fichaPadrao(): Ficha {
        val p = registry.get("sistema-proprio")!!
        return (
            p.buildFicha(
                "pj1",
                "Brokk",
                "anão minerador",
                p.data.stats.associate {
                    it.id to it.default
                },
                emptyList(),
            ) as FichaBuild.Created
        ).ficha
    }

    private val descricaoPropria = """{"system":"proprio","edition":"sistema-proprio","settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}"""

    @Test fun caminhoFelizDescricaoRevisaoInicio() {
        val f = flow(descricaoPropria)
        assertTrue(f.step is NewCampaignFlow.Step.Describe)
        assertTrue(f.submitDescription("Sistema próprio cyberpunk normal") is NewCampaignFlow.Step.Review)
        assertEquals(listOf("pj1"), f.compatible(listOf(fichaPadrao())).map { it.id })
        val s = f.start(fichaPadrao(), "Minas de neon", 5L) as NewCampaignFlow.Step.Started
        assertEquals("camp1", s.result.record.id)
        assertEquals("Minas de neon", store.campaign("camp1")!!.name)
    }

    @Test fun perguntasDoMotorViramPassosENaoGravamNada() {
        val f = flow("""{"system":"alfa","difficulty":"difficulty.normal"}""")
        val step = f.submitDescription("Sistema Alfa") as NewCampaignFlow.Step.Asking
        assertEquals(DraftField.EDITION, step.ask.field)
        assertNull(store.campaign("camp1"))
        assertTrue(f.answer("alfa-2") is NewCampaignFlow.Step.Review)
    }

    @Test fun soFichasDaMesmaEdicaoSaoOferecidas() {
        val f = flow(descricaoPropria)
        f.submitDescription("x")
        val outra = Ficha("pj9", "Outro", SystemRef("alfa", "alfa-2"))
        assertEquals(listOf("pj1"), f.compatible(listOf(outra, fichaPadrao())).map { it.id })
    }

    @Test fun fichaErradaVoltaParaRevisaoComErrosESemGravar() {
        val f = flow(descricaoPropria)
        f.submitDescription("x")
        val outra = Ficha("pj9", "Outro", SystemRef("alfa", "alfa-2"))
        val r = f.start(outra, "X", 1L) as NewCampaignFlow.Step.Review
        assertTrue(r.errors.any { "não se misturam" in it })
        assertNull(store.campaign("camp1"))
        // Corrige: mesma revisão, ficha certa.
        assertTrue(f.start(fichaPadrao(), "X", 1L) is NewCampaignFlow.Step.Started)
    }

    @Test fun falhaDeIaViraProblemaEPodeRecomecar() {
        val f = flow(AiError.NoInternet())
        val p = f.submitDescription("x") as NewCampaignFlow.Step.Problem
        assertTrue(p.reasons.isNotEmpty())
        f.reset()
        assertTrue(f.step is NewCampaignFlow.Step.Describe)
    }

    @Test fun answerEStartForaDoPassoCertoNaoFazemNada() {
        val f = flow()
        assertTrue(f.answer("x") is NewCampaignFlow.Step.Describe)
        assertTrue(f.start(fichaPadrao(), "X", 1L) is NewCampaignFlow.Step.Describe)
        assertNull(store.campaign("camp1"))
    }
}
