package dev.rpgsolo.rules

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.SystemRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CampaignStarterTest {
    private fun res(name: String) = listOf("src/main/resources/$name", "rules/src/main/resources/$name").map(::File).first { it.exists() }

    private fun catalog() = (CatalogLoader.load(
        res("catalog/estilos.txt").readText(), res("catalog/sistemas.txt").readText(), res("catalog/compat.txt").readText()
    ) as LoadResult.Success).catalog

    private fun registry(cat: Catalog): PackageRegistry {
        val r = PackageRegistry(cat)
        for (n in listOf("fate-condensed")) {
            val files = res("packages/$n").listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
            assertTrue(r.install(files) is InstallResult.Success)
        }
        return r
    }

    private val diff = DifficultySetting("difficulty.normal")
    private fun config(system: String, edition: String) =
        CampaignConfig(system, edition, settingIds = listOf("setting.cyberpunk"), toneIds = listOf("tone.sombrio"), difficulty = diff)

    private fun ready(cat: Catalog, system: String, edition: String): Pair<CampaignConfig, ConfigResult.Ready> {
        val c = config(system, edition)
        return c to (resolveConfig(cat, c) as ConfigResult.Ready)
    }

    private fun fate(cat: Catalog, reg: PackageRegistry): Ficha {
        val p = packageFor(reg)
        val b = p.buildFicha("pj1", "Brokk", "anão minerador", p.data.stats.associate { it.id to it.default }, emptyList())
        return (b as FichaBuild.Created).ficha
    }

    private fun packageFor(reg: PackageRegistry) = CampaignStarter.packageFor(reg, "fate-condensed")!!

    @Test fun achaOPacotePelaEdicao() {
        val reg = registry(catalog())
        assertEquals("fate-condensed", packageFor(reg).manifest.id)
        assertNull(CampaignStarter.packageFor(reg, "3det-victory"))
    }

    @Test fun iniciaCampanhaComFichaPacoteEAmbientacao() {
        val cat = catalog(); val reg = registry(cat); val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "fate", "fate-condensed")
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, fate(cat, reg), "c1", "Minas de neon", 7L) as StartResult.Started
        assertNotNull(r.pkg)
        assertEquals("pj1", r.playerId)
        assertEquals(EntityKind.PLAYER, r.session.state.entities.getValue("pj1").kind)
        assertEquals("Cyberpunk", r.campanha.ambientacao.name)
        assertEquals("Sombrio", r.campanha.ambientacao.tone)
        assertTrue(r.campanha.validate(fate(cat, reg)).isEmpty())
        // Já está salva: reabre com o mesmo estado.
        val reopened = CampaignSession.open(store, "c1")
        assertTrue("pj1" in reopened.state.entities)
    }

    @Test fun ambientacaoLivreTemPrioridadeSobreOCatalogo() {
        val cat = catalog(); val reg = registry(cat)
        val (cfg, rd) = ready(cat, "fate", "fate-condensed")
        val r = CampaignStarter.start(InMemoryEventStore(), reg, cat, rd, cfg, fate(cat, reg), "c1", "X", 1L, "cyberpunk com mineração em asteroides")
        assertEquals("cyberpunk com mineração em asteroides", (r as StartResult.Started).campanha.ambientacao.name)
    }

    @Test fun fichaDeOutraEdicaoNaoEntraEmSilencio() {
        val cat = catalog(); val reg = registry(cat); val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "fate", "fate-condensed")
        val estranha = Ficha("pj2", "Intruso", SystemRef("3det", "3det-victory"), stats = mapOf("forca" to 3))
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, estranha, "c1", "X", 1L) as StartResult.Refused
        assertTrue(r.reasons.any { "não se misturam" in it })
        assertNull(store.campaign("c1")) // nada foi criado
    }

    @Test fun fichaInvalidaPeloPacoteERecusada() {
        val cat = catalog(); val reg = registry(cat); val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "fate", "fate-condensed")
        val ruim = fate(cat, reg).copy(stats = mapOf("inexistente" to 99), traits = listOf("tracoInexistente"))
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, ruim, "c1", "X", 1L)
        assertTrue(r is StartResult.Refused)
        assertNull(store.campaign("c1"))
    }

    @Test fun semPacoteIniciaMasAvisaQueNaoHaValidacao() {
        val cat = catalog(); val reg = registry(cat); val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "3det", "3det-victory")
        val livre = Ficha("pj3", "Livre", SystemRef("3det", "3det-victory"), stats = mapOf("forca" to 2))
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, livre, "c1", "X", 1L) as StartResult.Started
        assertNull(r.pkg)
        assertTrue(r.notes.any { "não são validados pelo motor" in it })
        assertTrue(r.notes.any { "não foi verificada" in it })
    }

    @Test fun nomeVazioEIdRepetidoSaoRecusados() {
        val cat = catalog(); val reg = registry(cat); val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "fate", "fate-condensed")
        val f = fate(cat, reg)
        assertTrue(CampaignStarter.start(store, reg, cat, rd, cfg, f, "c1", "  ", 1L) is StartResult.Refused)
        assertTrue(CampaignStarter.start(store, reg, cat, rd, cfg, f, "c1", "Ok", 1L) is StartResult.Started)
        val dup = CampaignStarter.start(store, reg, cat, rd, cfg, f, "c1", "Ok", 1L) as StartResult.Refused
        assertTrue(dup.reasons.any { "já existe" in it })
        assertFalse(dup.reasons.isEmpty())
    }
}
