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
    private fun res(path: String) =
        NewCampaignFlowTest::class.java.classLoader!!.getResourceAsStream(path)!!.bufferedReader().readText()

    private val catalog = (CatalogLoader.load(res("catalog/estilos.txt"), res("catalog/sistemas.txt"), res("catalog/compat.txt")) as LoadResult.Success).catalog

    private val registry = PackageRegistry(catalog).also {
        val files = listOf("manifest", "atributos", "recursos", "tracos", "testes", "dificuldades", "condicoes", "dano", "criacao", "progressao", "incerto", "tutorial", "exemplos")
            .mapNotNull { f -> runCatching { "$f.txt" to res("packages/sistema-proprio/$f.txt") }.getOrNull() }.toMap()
        assertTrue(it.install(files) is InstallResult.Success)
    }

    private val store = InMemoryEventStore()
    private fun flow(vararg replies: Any) =
        NewCampaignFlow(CampaignInterpreter(ScriptedProvider(replies.toList())), catalog, registry, store) { "camp1" }

    private fun fateFicha(): Ficha {
        val p = registry.get("sistema-proprio")!!
        return (p.buildFicha("pj1", "Brokk", "anão minerador", p.data.stats.associate { it.id to it.default }, emptyList()) as FichaBuild.Created).ficha
    }

    private val fate = """{"system":"proprio","edition":"sistema-proprio","settings":["setting.cyberpunk"],"difficulty":"difficulty.normal"}"""

    @Test fun caminhoFelizDescricaoRevisaoInicio() {
        val f = flow(fate)
        assertTrue(f.step is NewCampaignFlow.Step.Describe)
        assertTrue(f.submitDescription("Fate Condensed cyberpunk normal") is NewCampaignFlow.Step.Review)
        assertEquals(listOf("pj1"), f.compatible(listOf(fateFicha())).map { it.id })
        val s = f.start(fateFicha(), "Minas de neon", 5L) as NewCampaignFlow.Step.Started
        assertEquals("camp1", s.result.record.id)
        assertEquals("Minas de neon", store.campaign("camp1")!!.name)
    }

    @Test fun perguntasDoMotorViramPassosENaoGravamNada() {
        val f = flow("""{"system":"3det","difficulty":"difficulty.normal"}""")
        val step = f.submitDescription("3D&T") as NewCampaignFlow.Step.Asking
        assertEquals(DraftField.EDITION, step.ask.field)
        assertNull(store.campaign("camp1"))
        assertTrue(f.answer("3det-victory") is NewCampaignFlow.Step.Review)
    }

    @Test fun soFichasDaMesmaEdicaoSaoOferecidas() {
        val f = flow(fate)
        f.submitDescription("x")
        val outra = Ficha("pj9", "Outro", SystemRef("3det", "3det-victory"))
        assertEquals(listOf("pj1"), f.compatible(listOf(outra, fateFicha())).map { it.id })
    }

    @Test fun fichaErradaVoltaParaRevisaoComErrosESemGravar() {
        val f = flow(fate)
        f.submitDescription("x")
        val outra = Ficha("pj9", "Outro", SystemRef("3det", "3det-victory"))
        val r = f.start(outra, "X", 1L) as NewCampaignFlow.Step.Review
        assertTrue(r.errors.any { "não se misturam" in it })
        assertNull(store.campaign("camp1"))
        // Corrige: mesma revisão, ficha certa.
        assertTrue(f.start(fateFicha(), "X", 1L) is NewCampaignFlow.Step.Started)
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
        assertTrue(f.start(fateFicha(), "X", 1L) is NewCampaignFlow.Step.Describe)
        assertNull(store.campaign("camp1"))
    }
}
