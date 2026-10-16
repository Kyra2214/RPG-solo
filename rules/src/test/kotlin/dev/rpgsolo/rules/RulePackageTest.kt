package dev.rpgsolo.rules

import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.GameEngine
import dev.rpgsolo.engine.GainXp
import dev.rpgsolo.engine.Outcome
import dev.rpgsolo.engine.RegraAdicional
import dev.rpgsolo.engine.RuleOrigin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class RulePackageTest {
    private fun dir(name: String): File =
        listOf("src/main/resources/packages/$name", "rules/src/main/resources/packages/$name").map(::File).first { it.exists() }

    private fun files(name: String): Map<String, String> =
        dir(name).listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun registry(catalog: Catalog? = null): PackageRegistry {
        val r = PackageRegistry(catalog)
        assertTrue(r.install(files("teste-basico")) is InstallResult.Success)
        return r
    }

    private fun basico() = registry().get("teste-basico")!!
    private fun failure(r: InstallResult) = (r as InstallResult.Failure).errors

    // ---- formato e carregamento (4.1, 4.2.1) ----
    @Test fun pacoteDeTesteCarregaESeusExemplosPassam() {
        val p = basico()
        assertEquals("teste", p.manifest.system)
        assertEquals(PackageKind.TESTE, p.manifest.kind)
        assertEquals(8, p.creationPoints)
        assertTrue(p.validateCharacter(mapOf("forca" to 3, "agilidade" to 1, "mente" to 0), listOf("lutador", "espadas")).valid)
    }

    @Test fun arquivoComErroDaMensagemComArquivoELinha() {
        val f = files("teste-basico").toMutableMap()
        f["atributos.txt"] = "forca|Força|0|5\n"
        val errs = failure(PackageRegistry().install(f))
        assertTrue(errs.any { it.contains("atributos.txt linha 1") })
    }

    @Test fun arquivoDesconhecidoEManifestIncompletoSaoRecusados() {
        val f = files("teste-basico").toMutableMap()
        f["lixo.txt"] = "x"
        f["manifest.txt"] = "id|teste-basico\n"
        val errs = failure(PackageRegistry().install(f))
        assertTrue(errs.any { it.contains("lixo.txt") })
        assertTrue(errs.any { it.contains("falta 'sistema'") })
    }

    @Test fun referenciasQuebradasSaoRecusadas() {
        val f = files("teste-basico").toMutableMap()
        f["recursos.txt"] = "hp|Vida|4|inexistente|2\n"
        assertTrue(failure(PackageRegistry().install(f)).any { it.contains("atributo inexistente") })
        val g = files("teste-basico").toMutableMap()
        g["tracos.txt"] = "x|poder|X|1|trait:fantasma|d\n"
        assertTrue(failure(PackageRegistry().install(g)).any { it.contains("traço inexistente") })
    }

    @Test fun exemploInvalidoDoProprioPacoteReprovaOPacote() {
        val f = files("teste-basico").toMutableMap()
        f["exemplos.txt"] = "ruim|Ruim|forca=5,agilidade=5,mente=5|\n"
        assertTrue(failure(PackageRegistry().install(f)).any { it.contains("exemplo ruim") })
    }

    @Test fun progressaoComBuracoOuForaDeOrdemEhRecusada() {
        val f = files("teste-basico").toMutableMap()
        f["progressao.txt"] = "2|10\n4|30\n"
        assertTrue(failure(PackageRegistry().install(f)).any { it.contains("sem buracos") })
        f["progressao.txt"] = "2|30\n3|10\n"
        assertTrue(failure(PackageRegistry().install(f)).any { it.contains("crescente") })
    }

    // ---- criação e validação de ficha (4.1.4) ----
    @Test fun validacaoDeFicha() {
        val p = basico()
        fun errs(stats: Map<String, Int>, traits: List<String>) = p.validateCharacter(stats, traits).errors
        assertTrue(errs(mapOf("forca" to 9), emptyList()).any { it.contains("fora do limite") })
        assertTrue(errs(mapOf("sorte" to 1), emptyList()).any { it.contains("atributo desconhecido") })
        assertTrue(errs(mapOf("forca" to 1), listOf("lutador")).any { it.contains("exige Força >= 2") })
        assertTrue(errs(emptyMap(), listOf("mestre-espadas")).any { it.contains("exige o traço Espadas") })
        assertTrue(errs(emptyMap(), listOf("espadas", "espadas")).any { it.contains("repetido") })
        assertTrue(errs(emptyMap(), listOf("fantasma")).any { it.contains("desconhecido") })
        assertTrue(errs(mapOf("forca" to 5, "agilidade" to 5, "mente" to 5), emptyList()).any { it.contains("pontos gastos") })
    }

    @Test fun desvantagemDevolvePontos() {
        val p = basico()
        val sem = p.validateCharacter(mapOf("forca" to 3, "agilidade" to 3, "mente" to 2), emptyList())
        val com = p.validateCharacter(mapOf("forca" to 3, "agilidade" to 3, "mente" to 2), listOf("fragil"))
        assertEquals(sem.pointsSpent - 1, com.pointsSpent)
    }

    @Test fun fichaCriadaTemRecursosDerivadosESistemaCerto() {
        val r = basico().buildFicha("f1", "Brokk", "anão minerador", mapOf("forca" to 3, "agilidade" to 1, "mente" to 0), listOf("lutador"))
        val f = (r as FichaBuild.Created).ficha
        assertEquals(10, f.resources.getValue("hp").max)
        assertEquals(0, f.resources.getValue("mana").max)
        assertEquals("teste-1", f.system.editionId)
        assertEquals("anão minerador", f.concept)
    }

    @Test fun fichaInvalidaNaoEhCriada() {
        val r = basico().buildFicha("f1", "X", "", mapOf("forca" to 1), listOf("lutador"))
        assertTrue(r is FichaBuild.Invalid)
    }

    @Test fun regraIncertaGeraAvisoNuncaSilencio() {
        val dir = files("teste-basico").toMutableMap()
        dir["incerto.txt"] = "lutador|Bônus de combate não está claro no material.\n"
        val p = (PackageRegistry().install(dir) as InstallResult.Success).pkg
        val r = p.buildFicha("f", "N", "", mapOf("forca" to 2), listOf("lutador")) as FichaBuild.Created
        assertTrue(r.warnings.any { it.contains("regra incerta em Lutador") })
    }

    // ---- ligação com o motor ----
    @Test fun testeDoPacoteVaiParaOMotorComModificadorECriticos() {
        val p = basico()
        val spec = p.check("ataque", mapOf("forca" to 3), p.difficultyValue("media")!!)
        assertEquals(3, spec.modifier)
        assertEquals(12, spec.difficulty)
        assertEquals(3, spec.partialMargin)
        assertEquals(20..20, spec.criticalSuccessRange)
        val e = GameEngine(1, p.engineConfig())
        assertTrue(e.check(spec).outcome in Outcome.values())
    }

    @Test fun testeSemParcialNemCriticoNaoInventaNada() {
        val spec = basico().check("esquiva", mapOf("agilidade" to 2), 10)
        assertNull(spec.partialMargin)
        assertNull(spec.criticalSuccessRange)
        assertNull(spec.criticalFailureRange)
    }

    @Test fun configDoMotorVemDoPacote() {
        val cfg = basico().engineConfig()
        assertEquals("hp", cfg.deathResource)
        assertEquals(listOf(10, 30, 60), cfg.levelThresholds)
        val f = (basico().buildFicha("h", "Herói", "", mapOf("forca" to 2), emptyList()) as FichaBuild.Created).ficha
        val e = GameEngine(1, cfg)
        e.execute(CreateEntity(f.toEntity()))
        e.execute(GainXp("h", 10))
        assertEquals(2, e.state.entities.getValue("h").level)
        e.execute(Damage("h", "hp", 99))
        assertFalse(e.state.entities.getValue("h").alive)
    }

    // ---- herança e isolamento (4.3.3) ----
    @Test fun edicaoFilhaHerdaESobrescreve() {
        val r = registry()
        assertTrue(r.install(files("teste-avancado")) is InstallResult.Success)
        val ad = r.get("teste-avancado")!!
        assertEquals(10, ad.creationPoints)
        assertEquals(13, ad.difficultyValue("media"))
        assertEquals(8, ad.difficultyValue("facil"))
        assertEquals(1, ad.statCost)
        assertTrue(ad.trait("lutador") != null && ad.trait("ambidestro") != null)
        assertEquals(listOf(10, 30, 60), ad.engineConfig().levelThresholds)
        assertEquals("hp", ad.engineConfig().deathResource)
        assertEquals(12, r.get("teste-basico")!!.difficultyValue("media"))
    }

    @Test fun trocarDePacoteNaoMisturaRegras() {
        val r = registry()
        r.install(files("teste-avancado"))
        val b = r.get("teste-basico")!!
        assertNull(b.trait("ambidestro"))
        assertTrue(b.validateCharacter(mapOf("agilidade" to 3), listOf("ambidestro")).errors.any { it.contains("desconhecido") })
        assertEquals(2, r.installed().size)
    }

    @Test fun heranciaSemBaseOuDeOutraFamiliaEhRecusada() {
        val orfao = files("teste-avancado")
        assertTrue(failure(PackageRegistry().install(orfao)).any { it.contains("pacote-base não instalado") })
        val r = registry()
        val outra = files("teste-avancado").toMutableMap()
        outra["manifest.txt"] = outra.getValue("manifest.txt").replace("sistema|teste", "sistema|outro")
        assertTrue(failure(r.install(outra)).any { it.contains("mesma família") })
        assertEquals(1, r.installed().size)
    }

    @Test fun instalacaoRuimNaoAlteraOQueJaEstavaInstalado() {
        val r = registry()
        val bad = files("teste-avancado").toMutableMap()
        bad["tracos.txt"] = "x|poder|X|1|fantasma>=1|d\n"
        assertTrue(r.install(bad) is InstallResult.Failure)
        assertEquals(listOf("teste-basico"), r.installed().map { it.id })
        assertNull(r.get("teste-avancado"))
    }

    @Test fun pacoteDuplicadoSoComReplaceEFilhoInvalidadoBarraAtualizacaoDaBase() {
        val r = registry()
        r.install(files("teste-avancado"))
        assertTrue(r.install(files("teste-basico")) is InstallResult.Failure)
        val quebraFilho = files("teste-basico").toMutableMap()
        quebraFilho["atributos.txt"] = "forca|Força|0|5|1\nagilidade|Agilidade|0|5|1\n"
        assertTrue(r.install(quebraFilho, replace = true) is InstallResult.Failure)
        assertTrue(r.get("teste-basico")!!.stat("mente") != null)
    }

    // ---- zip e licença (4.2.2, 4A.5) ----
    private fun zipOf(f: Map<String, String>, prefix: String = "pacote/"): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { z ->
            f.forEach { (n, t) -> z.putNextEntry(ZipEntry(prefix + n)); z.write(t.toByteArray()); z.closeEntry() }
        }
        return out.toByteArray()
    }

    @Test fun importaPorZipComPastaIgnorandoLixo() {
        val r = PackageRegistry()
        val z = zipOf(files("teste-basico") + ("LEIAME.md" to "x"))
        assertTrue(r.installZip(ByteArrayInputStream(z)) is InstallResult.Success)
    }

    @Test fun zipIlegivelOuRepetidoDaFalhaClara() {
        val r = PackageRegistry()
        assertTrue(r.installZip(ByteArrayInputStream(ByteArray(0))) is InstallResult.Failure)
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { z ->
            z.putNextEntry(ZipEntry("a/manifest.txt")); z.write("x".toByteArray()); z.closeEntry()
            z.putNextEntry(ZipEntry("b/manifest.txt")); z.write("y".toByteArray()); z.closeEntry()
        }
        assertTrue(failure(r.installZip(ByteArrayInputStream(out.toByteArray()))).any { it.contains("repetido") })
        assertTrue(r.installed().isEmpty())
    }

    private fun realCatalog(): Catalog {
        fun res(name: String) = listOf("src/main/resources/catalog/$name", "rules/src/main/resources/catalog/$name").map(::File).first { it.exists() }.readText()
        return (CatalogLoader.load(res("estilos.txt"), res("sistemas.txt"), res("compat.txt")) as LoadResult.Success).catalog
    }

    private fun realManifest(edition: String, system: String, status: String, source: String) =
        files("teste-basico").toMutableMap().also {
            it["manifest.txt"] = """
                id|real-x
                sistema|$system
                edicao|$edition
                versao|1
                origem|$source
                licenca|x
                licenca_status|$status
                tipo|REAL
            """.trimIndent()
        }

    @Test fun pacoteRealExigeEdicaoDoCatalogoELicencaNaoBloqueia() {
        val cat = realCatalog()
        assertTrue(failure(PackageRegistry(cat).install(realManifest("nao-existe", "dnd", "VERIFICADA", "autoral")))
            .any { it.contains("não existe no catálogo") })
        assertTrue(PackageRegistry(cat).install(realManifest("3det-alpha", "3det", "PENDENTE", "autoral")) is InstallResult.Success)
        assertTrue(failure(PackageRegistry(cat).install(realManifest("3det-alpha", "dnd", "VERIFICADA", "autoral")))
            .any { it.contains("família") })
        assertTrue(PackageRegistry(cat).install(realManifest("3det-alpha", "3det", "PENDENTE", USER_IMPORT_SOURCE)) is InstallResult.Success)
        assertTrue(PackageRegistry(cat).install(realManifest("dnd-5-2-srd", "dnd", "VERIFICADA", "autoral")) is InstallResult.Success)
    }

    // ---- busca (4.2.3) ----
    @Test fun buscaIgnoraAcentoERetornaSoOQueImporta() {
        val p = basico()
        val r = p.search("ENVENENADO perde vida")
        assertEquals("envenenado", r.first().id)
        assertEquals("traco", p.search("mestre das espadas").first().kind)
        assertTrue(p.search("zzzzzz").isEmpty())
        assertTrue(p.search("a b").isEmpty())
        assertTrue(p.search("atributo pontos", limit = 1).size == 1)
    }

    // ---- regras adicionais (4.4) ----
    private fun brokk(): Ficha =
        (basico().buildFicha("f1", "Brokk", "anão minerador", mapOf("forca" to 3, "agilidade" to 1, "mente" to 0), emptyList()) as FichaBuild.Created).ficha

    private fun regra(effects: Map<String, Int>, approved: Boolean = false) =
        RegraAdicional("r1", "Britadeira de combate", "usa ferramenta de mina como arma", effects, approved = approved)

    @Test fun previaMostraEfeitosEmNumeros() {
        val pv = basico().previewAdaptation(brokk(), regra(mapOf("forca" to 1)))
        assertEquals(listOf(EffectLine("forca", 3, 4, false)), pv.lines)
        assertFalse(pv.requiresApproval)
    }

    @Test fun mudancaGrandeExigeAprovacaoDoJogador() {
        val p = basico()
        val r = regra(mapOf("forca" to 1, "agilidade" to 1))
        assertTrue(p.previewAdaptation(brokk(), r).requiresApproval)
        assertTrue(p.applyAdaptation(brokk(), r) is AdaptationResult.Refused)
        val ok = p.applyAdaptation(brokk(), r.copy(approved = true)) as AdaptationResult.Applied
        assertEquals(4, ok.ficha.stats.getValue("forca"))
        assertEquals(2, ok.ficha.stats.getValue("agilidade"))
        assertEquals(12, ok.ficha.resources.getValue("hp").max)
    }

    @Test fun adaptacaoPequenaAplicaSemAprovacaoEGanhaSelo() {
        val ok = basico().applyAdaptation(brokk(), regra(mapOf("forca" to 1))) as AdaptationResult.Applied
        assertEquals(listOf("r1"), ok.ficha.adaptations)
        assertEquals(RuleOrigin.ADAPTACAO, regra(emptyMap()).origin)
    }

    @Test fun alvoDesconhecidoEForaDosLimitesSempreRecusados() {
        val p = basico()
        val desconhecido = p.applyAdaptation(brokk(), regra(mapOf("sanidade" to 1), approved = true))
        assertTrue((desconhecido as AdaptationResult.Refused).reasons.any { it.contains("alvo desconhecido") })
        val alto = p.applyAdaptation(brokk(), regra(mapOf("forca" to 3), approved = true))
        assertTrue((alto as AdaptationResult.Refused).reasons.any { it.contains("fora dos limites") })
    }

    @Test fun mesmaRegraNaoAplicaDuasVezesEEstruturaDaFichaNaoMuda() {
        val p = basico()
        val a = (p.applyAdaptation(brokk(), regra(mapOf("forca" to 1))) as AdaptationResult.Applied).ficha
        assertTrue(p.applyAdaptation(a, regra(mapOf("forca" to 1))) is AdaptationResult.Refused)
        assertEquals(brokk().stats.keys, a.stats.keys)
        assertEquals(brokk().resources.keys, a.resources.keys)
        assertEquals(brokk().system, a.system)
    }
}
