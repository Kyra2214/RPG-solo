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

    private fun catalog() =
        (
            CatalogLoader.load(
                res("catalog/estilos.txt").readText(),
                res("catalog/sistemas.txt").readText(),
                res("catalog/compat.txt").readText(),
            ) as LoadResult.Success
        ).catalog

    // Catálogo embutido + uma edição fictícia sem pacote instalado (para o caso "sem pacote").
    private fun catalogComEdicaoSemPacote() =
        (
            CatalogLoader.load(
                res("catalog/estilos.txt").readText(),
                res("catalog/sistemas.txt").readText() + "\nsistema-sem-pacote|semPacote|Sistema sem pacote|v1|generico|PENDENTE|PENDENTE|teste\n",
                res("catalog/compat.txt").readText(),
            ) as LoadResult.Success
        ).catalog

    private fun registry(cat: Catalog): PackageRegistry {
        val r = PackageRegistry(cat)
        for (n in listOf("sistema-proprio")) {
            val files = res("packages/$n").listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
            assertTrue(r.install(files) is InstallResult.Success)
        }
        return r
    }

    private val diff = DifficultySetting("difficulty.normal")

    private fun config(
        system: String,
        edition: String,
    ) = CampaignConfig(system, edition, settingIds = listOf("setting.cyberpunk"), toneIds = listOf("tone.sombrio"), difficulty = diff)

    private fun ready(
        cat: Catalog,
        system: String,
        edition: String,
    ): Pair<CampaignConfig, ConfigResult.Ready> {
        val c = config(system, edition)
        return c to (resolveConfig(cat, c) as ConfigResult.Ready)
    }

    private fun fichaPadrao(
        cat: Catalog,
        reg: PackageRegistry,
    ): Ficha {
        val p = packageFor(reg)
        val b = p.buildFicha("pj1", "Brokk", "anão minerador", p.data.stats.associate { it.id to it.default }, emptyList())
        return (b as FichaBuild.Created).ficha
    }

    private fun packageFor(reg: PackageRegistry) = CampaignStarter.packageFor(reg, "sistema-proprio")!!

    @Test fun achaOPacotePelaEdicao() {
        val reg = registry(catalog())
        assertEquals("sistema-proprio", packageFor(reg).manifest.id)
        assertNull(CampaignStarter.packageFor(reg, "sistema-sem-pacote"))
    }

    @Test fun iniciaCampanhaComFichaPacoteEAmbientacao() {
        val cat = catalog()
        val reg = registry(cat)
        val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "proprio", "sistema-proprio")
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, fichaPadrao(cat, reg), "c1", "Minas de neon", 7L) as StartResult.Started
        assertNotNull(r.pkg)
        assertEquals("pj1", r.playerId)
        assertEquals(EntityKind.PLAYER, r.session.state.entities.getValue("pj1").kind)
        assertEquals("Cyberpunk", r.campanha.ambientacao.name)
        assertEquals("Sombrio", r.campanha.ambientacao.tone)
        assertTrue(r.campanha.validate(fichaPadrao(cat, reg)).isEmpty())
        // Já está salva: reabre com o mesmo estado.
        val reopened = CampaignSession.open(store, "c1")
        assertTrue("pj1" in reopened.state.entities)
    }

    @Test fun ambientacaoLivreTemPrioridadeSobreOCatalogo() {
        val cat = catalog()
        val reg = registry(cat)
        val (cfg, rd) = ready(cat, "proprio", "sistema-proprio")
        val r =
            CampaignStarter.start(
                InMemoryEventStore(), reg, cat, rd, cfg,
                fichaPadrao(
                    cat,
                    reg,
                ),
                "c1", "X", 1L, "cyberpunk com mineração em asteroides",
            )
        assertEquals("cyberpunk com mineração em asteroides", (r as StartResult.Started).campanha.ambientacao.name)
    }

    // Q-07 (b8): a ambientação escolhida não pode mexer na ficha do jogador.
    @Test fun ambientacaoNaoAlteraAFichaDoJogador() {
        val cat = catalog()
        val reg = registry(cat)
        val (cfg, rd) = ready(cat, "proprio", "sistema-proprio")
        val ficha = fichaPadrao(cat, reg)
        val doCatalogo = CampaignStarter.start(InMemoryEventStore(), reg, cat, rd, cfg, ficha, "c1", "X", 1L) as StartResult.Started
        val livre =
            CampaignStarter.start(
                InMemoryEventStore(),
                reg,
                cat,
                rd,
                cfg,
                ficha,
                "c2",
                "Y",
                1L,
                "fantasia sombria com piratas do deserto",
            ) as StartResult.Started
        assertEquals(
            doCatalogo.session.state.entities.getValue("pj1"),
            livre.session.state.entities.getValue("pj1"),
        )
        assertEquals(doCatalogo.session.state.entities.keys, livre.session.state.entities.keys)
        assertEquals(fichaPadrao(cat, reg), ficha)
    }

    @Test fun fichaDeOutraEdicaoNaoEntraEmSilencio() {
        val cat = catalog()
        val reg = registry(cat)
        val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "proprio", "sistema-proprio")
        val estranha = Ficha("pj2", "Intruso", SystemRef("externo", "externo-1"), stats = mapOf("forca" to 3))
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, estranha, "c1", "X", 1L) as StartResult.Refused
        assertTrue(r.reasons.any { "não se misturam" in it })
        assertNull(store.campaign("c1")) // nada foi criado
    }

    @Test fun fichaInvalidaPeloPacoteERecusada() {
        val cat = catalog()
        val reg = registry(cat)
        val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "proprio", "sistema-proprio")
        val ruim = fichaPadrao(cat, reg).copy(stats = mapOf("inexistente" to 99), traits = listOf("tracoInexistente"))
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, ruim, "c1", "X", 1L)
        assertTrue(r is StartResult.Refused)
        assertNull(store.campaign("c1"))
    }

    @Test fun fichaComPreRequisitoDePericiaPendenteERecusadaAntesDeCriarCampanha() {
        val cat = catalog()
        val reg = registry(cat)
        val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "proprio", "sistema-proprio")
        val ficha = fichaPadrao(cat, reg).copy(skills = mapOf("olhar-atento" to 2))
        val result = CampaignStarter.start(store, reg, cat, rd, cfg, ficha, "c1", "X", 1L)
        assertTrue(result is StartResult.Refused)
        assertTrue((result as StartResult.Refused).reasons.any { "exige o traço Percepção" in it })
        assertNull(store.campaign("c1"))
    }

    @Test fun semPacoteIniciaMasAvisaQueNaoHaValidacao() {
        val cat = catalogComEdicaoSemPacote()
        val reg = registry(cat)
        val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "semPacote", "sistema-sem-pacote")
        val livre = Ficha("pj3", "Livre", SystemRef("semPacote", "sistema-sem-pacote"), stats = mapOf("forca" to 2))
        val r = CampaignStarter.start(store, reg, cat, rd, cfg, livre, "c1", "X", 1L) as StartResult.Started
        assertNull(r.pkg)
        assertTrue(r.notes.any { "não são validados pelo motor" in it })
        assertTrue(r.notes.any { "não foi verificada" in it })
    }

    @Test fun nomeVazioEIdRepetidoSaoRecusados() {
        val cat = catalog()
        val reg = registry(cat)
        val store = InMemoryEventStore()
        val (cfg, rd) = ready(cat, "proprio", "sistema-proprio")
        val f = fichaPadrao(cat, reg)
        assertTrue(CampaignStarter.start(store, reg, cat, rd, cfg, f, "c1", "  ", 1L) is StartResult.Refused)
        assertTrue(CampaignStarter.start(store, reg, cat, rd, cfg, f, "c1", "Ok", 1L) is StartResult.Started)
        val dup = CampaignStarter.start(store, reg, cat, rd, cfg, f, "c1", "Ok", 1L) as StartResult.Refused
        assertTrue(dup.reasons.any { "já existe" in it })
        assertFalse(dup.reasons.isEmpty())
    }
}
