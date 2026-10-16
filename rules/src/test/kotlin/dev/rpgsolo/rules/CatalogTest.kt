package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class CatalogTest {
    // Os arquivos reais do catálogo (src/main/resources/catalog). No Gradle, o diretório de trabalho é o módulo.
    private fun res(name: String): String {
        val f = listOf("src/main/resources/catalog/$name", "rules/src/main/resources/catalog/$name").map(::File).first { it.exists() }
        return f.readText()
    }
    private fun real(): Catalog =
        (CatalogLoader.load(res("estilos.txt"), res("sistemas.txt"), res("compat.txt")) as LoadResult.Success).catalog

    private val smallStyles = """
        setting.cyberpunk|setting|scifi|Cyberpunk
        setting.wuxia|setting|cultivo|Wuxia
        difficulty.hard|difficulty|dificuldade|Hard
        difficulty.facil|difficulty|dificuldade|Fácil
        tone.sombrio|tone|tom|Sombrio
    """.trimIndent()
    private val smallSystems = """
        a-1|famA|Sistema A|ed1|x|PENDENTE|PENDENTE|n
        a-2|famA|Sistema A|ed2|x|PENDENTE|PENDENTE|n
        b-1|famB|Sistema B|unica|x|PESQUISADO_PARCIAL|VERIFICADA|n
    """.trimIndent()
    private fun small(compat: String = "") =
        (CatalogLoader.load(smallStyles, smallSystems, compat) as LoadResult.Success).catalog
    private val hard = DifficultySetting("difficulty.hard")

    @Test fun catalogoRealCarregaSemProblemas() {
        assertTrue(real().validate().isEmpty())
    }

    @Test fun contagensPorDimensaoBatemComODocumento() {
        val byDim = real().entries.groupingBy { it.dimension }.eachCount()
        assertEquals(85, byDim["setting"]); assertEquals(24, byDim["structure"]); assertEquals(14, byDim["philosophy"])
        assertEquals(13, byDim["tone"]); assertEquals(5, byDim["difficulty"]); assertEquals(12, byDim["progression"])
        assertEquals(153, real().entries.size)
    }

    @Test fun idsSaoUnicos() {
        val ids = real().entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test fun wuxiaXianxiaXuanhuanSaoEntradasSeparadas() {
        val c = real()
        assertNotNull(c.entry("setting.wuxia")); assertNotNull(c.entry("setting.xianxia")); assertNotNull(c.entry("setting.xuanhuan"))
    }

    @Test fun familiasDeSistemaSaoSeparadas() {
        val c = real()
        assertTrue(c.families().containsAll(setOf("3det", "tormenta", "dnd", "pathfinder")))
        assertTrue(c.editionsOf("3det").none { it.family != "3det" })
        assertEquals(setOf("3det-alpha", "3det-victory"), c.editionsOf("3det").map { it.id }.toSet())
    }

    @Test fun naoHaStatusVerificadoSemNotaDeEvidencia() {
        real().systems.filter { it.licenseStatus == LicenseStatus.VERIFICADA }.forEach {
            assertTrue("${it.id} sem nota", it.licenseNote.isNotBlank())
        }
    }

    @Test fun combinacaoSemRegraEhNaoVerificada() {
        assertEquals(Compatibility.UNVERIFIED, real().compatibility("3det-alpha", "setting.cyberpunk").status)
    }

    @Test fun referenciaInvalidaFazACargaFalharSemEfeito() {
        val holder = CatalogHolder(small())
        val before = holder.current
        val r = holder.reload(smallStyles, smallSystems, "a-1|setting.nao-existe|NATIVE|x")
        assertTrue(r is LoadResult.Failure)
        assertTrue(holder.current === before)
    }

    @Test fun linhaMalFormadaFazACargaFalhar() {
        assertTrue(CatalogLoader.load("setting.x|setting|c", smallSystems, "") is LoadResult.Failure)
    }

    @Test fun idDuplicadoEhDetectado() {
        val dup = smallStyles + "\nsetting.wuxia|setting|cultivo|Wuxia de novo"
        assertTrue(CatalogLoader.load(dup, smallSystems, "") is LoadResult.Failure)
    }

    @Test fun edicaoAmbiguaGeraPergunta() {
        val r = resolveConfig(small(), CampaignConfig("famA", difficulty = hard))
        assertTrue(r is ConfigResult.NeedsQuestion)
        assertEquals(listOf("a-1", "a-2"), (r as ConfigResult.NeedsQuestion).options)
    }

    @Test fun edicaoUnicaEhEscolhidaSozinha() {
        val r = resolveConfig(small(), CampaignConfig("famB", difficulty = hard)) as ConfigResult.Ready
        assertEquals("b-1", r.edition.id)
    }

    @Test fun edicaoDeOutraFamiliaEhRejeitada() {
        val r = resolveConfig(small(), CampaignConfig("famA", editionId = "b-1", difficulty = hard))
        assertTrue(r is ConfigResult.Invalid)
    }

    @Test fun combinacaoNaoVerificadaAvisaOJogador() {
        val r = resolveConfig(small(), CampaignConfig("famB", settingIds = listOf("setting.cyberpunk"), difficulty = hard)) as ConfigResult.Ready
        assertEquals(Compatibility.UNVERIFIED, r.compatibility)
        assertTrue(r.notes.any { it.contains("não foi verificada") })
    }

    @Test fun combinacaoNativaNaoGeraAviso() {
        val c = small("b-1|setting.cyberpunk|NATIVE|ok")
        val r = resolveConfig(c, CampaignConfig("famB", settingIds = listOf("setting.cyberpunk"), difficulty = hard)) as ConfigResult.Ready
        assertEquals(Compatibility.NATIVE, r.compatibility)
        assertTrue(r.notes.none { it.contains("verificada com") })
    }

    @Test fun combinacaoBloqueadaEhInvalida() {
        val c = small("b-1|setting.wuxia|BLOCKED|sem regras para cultivo")
        val r = resolveConfig(c, CampaignConfig("famB", settingIds = listOf("setting.wuxia"), difficulty = hard))
        assertTrue(r is ConfigResult.Invalid)
    }

    @Test fun dificuldadeNaoPodeAlterarAtributosDeNpc() {
        val bad = DifficultySetting("difficulty.hard", mapOf("npc_attributes" to 2.0))
        assertTrue(resolveConfig(small(), CampaignConfig("famB", difficulty = bad)) is ConfigResult.Invalid)
        val ok = DifficultySetting("difficulty.hard", mapOf("resource_scarcity" to 0.5, "enemy_tactics" to 1.0))
        assertTrue(resolveConfig(small(), CampaignConfig("famB", difficulty = ok)) is ConfigResult.Ready)
    }

    @Test fun idDeDimensaoErradaEhRejeitado() {
        val r = resolveConfig(small(), CampaignConfig("famB", toneIds = listOf("setting.wuxia"), difficulty = hard))
        assertTrue(r is ConfigResult.Invalid)
    }

    @Test fun sistemaDesconhecidoEhInvalido() {
        assertTrue(resolveConfig(small(), CampaignConfig("nao-existe", difficulty = hard)) is ConfigResult.Invalid)
    }

    @Test fun limitesDeConteudoSaoIndependentes() {
        val c = CampaignConfig("famB", difficulty = hard, contentLimits = listOf("sem violência gráfica"))
        assertTrue(resolveConfig(small(), c) is ConfigResult.Ready)
    }

    @Test fun exemploAnaoMineradorCyberpunk3detExigeEdicaoEAvisa() {
        val c = real()
        val semEdicao = CampaignConfig("3det", settingIds = listOf("setting.cyberpunk"),
            structureIds = listOf("structure.combate-tatico"), difficulty = DifficultySetting("difficulty.dificil-hard"))
        assertTrue(resolveConfig(c, semEdicao) is ConfigResult.NeedsQuestion)
        val r = resolveConfig(c, semEdicao.copy(editionId = "3det-alpha")) as ConfigResult.Ready
        assertEquals("3det-alpha", r.edition.id)
        assertEquals(Compatibility.UNVERIFIED, r.compatibility)
        assertTrue(r.notes.isNotEmpty())
    }

    @Test fun tormenta20NaoCompartilhaEdicoesCom3det() {
        val c = real()
        val r = resolveConfig(c, CampaignConfig("tormenta", editionId = "3det-alpha", difficulty = DifficultySetting("difficulty.normal")))
        assertTrue(r is ConfigResult.Invalid)
    }
}
