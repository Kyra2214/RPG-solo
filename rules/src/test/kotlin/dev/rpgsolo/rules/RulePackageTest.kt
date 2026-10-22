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
        listOf("src/test/resources/packages/$name", "rules/src/test/resources/packages/$name").map(::File).first { it.exists() }

    private fun files(name: String): Map<String, String> =
        dir(name).listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun registry(catalog: Catalog? = null): PackageRegistry {
        val r = PackageRegistry(catalog)
        assertTrue(r.install(files("teste-basico")) is InstallResult.Success)
        return r
    }

    private fun basico() = registry().get("teste-basico")!!
    private fun pacoteComPericias(): RulePackage {
        val content = files("teste-basico").toMutableMap()
        content["tracos.txt"] = content.getValue("tracos.txt") +
            "\npericia-corrida|pericia|Corrida|0||Correr e saltar.\npericia-furtividade|pericia|Furtividade|0|trait:pericia-corrida|Mover-se sem ser visto.\n"
        return (PackageRegistry().install(content) as InstallResult.Success).pkg
    }
    private fun failure(r: InstallResult) = (r as InstallResult.Failure).errors

    // ---- formato e carregamento (4.1, 4.2.1) ----
    @Test fun pacoteDeTesteCarregaESeusExemplosPassam() {
        val p = basico()
        assertEquals("teste", p.manifest.system)
        assertEquals(PackageKind.TESTE, p.manifest.kind)
        assertEquals(8, p.creationPoints)
        assertTrue(p.validateCharacter(mapOf("forca" to 3, "agilidade" to 1, "mente" to 0), listOf("lutador", "espadas")).valid)
    }

    @Test fun catalogoDistingueEquipamentoConsumivelENarrativo() {
        val f = files("teste-basico").toMutableMap()
        f["itens.txt"] = "espada|Espada|equipamento|mao|Arma\npocao|Poção|consumivel|-|Cura\n"
        val p = (PackageRegistry().install(f) as InstallResult.Success).pkg
        assertTrue(p.isEquipableItem("espada", "mao"))
        assertFalse(p.isEquipableItem("pocao", "mao"))
        assertEquals("Cura", p.item("pocao")!!.description)
    }

    @Test fun equipamentoSemSlotEItemComSlotInvalidoSaoRecusados() {
        val f = files("teste-basico").toMutableMap()
        f["itens.txt"] = "espada|Espada|equipamento|-|Arma\n"
        assertTrue(failure(PackageRegistry().install(f)).any { it.contains("equipamento exige slot") })
        f["itens.txt"] = "pocao|Poção|consumivel|mao|Cura\n"
        assertTrue(failure(PackageRegistry().install(f)).any { it.contains("apenas equipamento") })
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

    @Test fun pontosDePericiaEntramNoOrcamentoEValidamIdsELimites() {
        val p = pacoteComPericias()
        val stats = p.data.stats.associate { it.id to it.default }
        val base = p.validateCharacter(stats, emptyList())
        val report = p.validateCharacter(stats, emptyList(), skills = mapOf("pericia-corrida" to 2))
        assertTrue(report.valid)
        assertEquals(base.pointsSpent + 2, report.pointsSpent)

        val desconhecida = p.validateCharacter(stats, emptyList(), skills = mapOf("fantasma" to 1))
        assertTrue(desconhecida.errors.any { it.contains("perícia desconhecida") })
        val acimaDoLimite = p.validateCharacter(stats, emptyList(), skills = mapOf("pericia-corrida" to 6))
        assertTrue(acimaDoLimite.errors.any { it.contains("fora do limite 0..5") })
        val semPreRequisito = p.validateCharacter(stats, emptyList(), skills = mapOf("pericia-furtividade" to 1))
        assertTrue(semPreRequisito.errors.any { it.contains("exige o traço Corrida") })
        val nivelZero = p.validateCharacter(stats, emptyList(), skills = mapOf("pericia-furtividade" to 0))
        assertTrue(nivelZero.errors.none { it.contains("exige o traço Corrida") })
        val acimaDoOrcamento = p.validateCharacter(
            stats, listOf("pericia-corrida"), skills = mapOf("pericia-corrida" to 5, "pericia-furtividade" to 5)
        )
        assertTrue(acimaDoOrcamento.errors.any { it.contains("pontos gastos") })
        assertTrue(p.validateCharacter(stats, listOf("pericia-corrida"), skills = mapOf("pericia-furtividade" to 1)).valid)

        assertTrue(p.buildFicha("ruim", "Inválido", "", stats, emptyList(), mapOf("fantasma" to 1)) is FichaBuild.Invalid)
    }

    @Test fun rascunhoUsaOMesmoRelatorioParaPericiasECriaFichaValidada() {
        val p = pacoteComPericias()
        val draft = CharacterDraft(p, "skill-draft")
        draft.name = "Corredora"
        val initialPoints = draft.pointsLeft()
        assertFalse(draft.setSkill("pericia-corrida", 6))
        assertTrue(draft.setSkill("pericia-corrida", 2))
        assertEquals(initialPoints - 2, draft.pointsLeft())
        val created = draft.build() as FichaBuild.Created
        assertEquals(2, created.ficha.skills.getValue("pericia-corrida"))
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
        // Estilos reais do app com sistemas fictícios de teste: duas famílias, uma edição cada.
        val sistemas = """
            alfa-1|alfa|Sistema Alfa|1|generico|PENDENTE|PENDENTE|teste
            beta-1|beta|Sistema Beta|1|generico|PESQUISADO_PARCIAL|VERIFICADA|teste
        """.trimIndent()
        return (CatalogLoader.load(res("estilos.txt"), sistemas, "") as LoadResult.Success).catalog
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
        assertTrue(failure(PackageRegistry(cat).install(realManifest("nao-existe", "beta", "VERIFICADA", "autoral")))
            .any { it.contains("não existe no catálogo") })
        assertTrue(PackageRegistry(cat).install(realManifest("alfa-1", "alfa", "PENDENTE", "autoral")) is InstallResult.Success)
        assertTrue(failure(PackageRegistry(cat).install(realManifest("alfa-1", "beta", "VERIFICADA", "autoral")))
            .any { it.contains("família") })
        assertTrue(PackageRegistry(cat).install(realManifest("alfa-1", "alfa", "PENDENTE", USER_IMPORT_SOURCE)) is InstallResult.Success)
        assertTrue(PackageRegistry(cat).install(realManifest("beta-1", "beta", "VERIFICADA", "autoral")) is InstallResult.Success)
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

    // ---- condições que dão Ganho/Perda automáticos ----
    private fun sorte() = basico().check("sorte", emptyMap(), 7)   // 2d6, sem atributo

    @Test fun perdaTiraUmDadoEGanhoCancelaAPerda() {
        val p = basico()
        assertEquals(1, p.adjustCheck(sorte(), listOf("amaldicoado")).diceCount)
        assertEquals(2, p.adjustCheck(sorte(), listOf("amaldicoado", "abencoado")).diceCount)
        assertEquals(3, p.adjustCheck(sorte(), listOf("abencoado")).diceCount)
        assertEquals(2, p.adjustCheck(sorte(), emptyList()).diceCount)
    }

    @Test fun efeitoPorAtributoSoAfetaOsTestesDaquelesAtributos() {
        val p = basico()
        val esquiva = p.check("esquiva", mapOf("agilidade" to 2), 10).copy(diceCount = 2)
        assertEquals(1, p.adjustCheck(esquiva, listOf("visao-turva")).diceCount)
        assertEquals(2, p.adjustCheck(esquiva, listOf("sangramento")).diceCount)   // sangramento é de força
        assertEquals(2, p.adjustCheck(sorte(), listOf("visao-turva")).diceCount)   // sem atributo: só efeitos para todos
    }

    @Test fun nuncaFicaAbaixoDeUmDadoNemPassaDoLimite() {
        val p = basico()
        val um = sorte().copy(diceCount = 1)
        assertEquals(1, p.adjustCheck(um, listOf("amaldicoado", "visao-turva")).diceCount)
        val tres = sorte().copy(diceCount = 3)
        assertEquals(3, p.adjustCheck(tres, listOf("abencoado")).diceCount)
    }

    @Test fun cartaoDoDadoMostraGanhoOuPerda() {
        val p = basico()
        val base = sorte().copy(label = "Teste de sorte (2d6)")
        assertEquals("Teste de sorte (1d6) · Perda", p.adjustCheck(base, listOf("amaldicoado")).label)
        assertEquals("Teste de sorte (3d6) · Ganho", p.adjustCheck(base, listOf("abencoado")).label)
        assertEquals("Teste de sorte (2d6)", p.adjustCheck(base, listOf("abencoado", "amaldicoado")).label)
    }

    @Test fun condicaoSemEfeitoNaoMudaOTeste() {
        val p = basico()
        assertEquals(sorte(), p.adjustCheck(sorte(), listOf("envenenado", "atordoado", "desconhecida")))
    }

    @Test fun efeitoInvalidoOuComAtributoInexistenteEhRecusado() {
        val a = files("teste-basico").toMutableMap()
        a["condicoes.txt"] = "x|X|d|inventado:forca\n"
        assertTrue(failure(PackageRegistry().install(a)).any { it.contains("efeito inválido") })
        val b = files("teste-basico").toMutableMap()
        b["condicoes.txt"] = "x|X|d|perda:fantasma\n"
        assertTrue(failure(PackageRegistry().install(b)).any { it.contains("atributo inexistente no efeito") })
    }

    @Test fun efeitosExternosDeduplicamPorIdentidadeSemApagarAcumuloEntreFontes() {
        val race = ExternalBonus("sorte", 1, "raça: elfo", effectId = "race:elfo#mechanicalEffects#0")
        val sameDeclaration = race.copy()
        val classBonus = ExternalBonus("sorte", 1, "classe: arqueiro", effectId = "class:arqueiro#mechanicalEffects#0")
        assertEquals(
            listOf(race, classBonus),
            distinctExternalBonuses(listOf(race, sameDeclaration, classBonus))
        )
        // Sem identidade explícita, a equivalência não é inferida pelo mesmo número/escopo.
        assertEquals(
            2,
            distinctExternalBonuses(
                listOf(
                    ExternalBonus("sorte", 1, "fonte A"),
                    ExternalBonus("sorte", 1, "fonte B")
                )
            ).size
        )
    }

    @Test fun adjustCheckNaoAplicaDuasVezesAMesmaDeclaracaoExterna() {
        val p = basico()
        val spec = p.check("sorte", emptyMap(), 7)
        val declaration = ExternalBonus("sorte", 1, "raça: elfo", effectId = "race:elfo#0")
        val otherSource = ExternalBonus("sorte", 1, "classe: arqueiro", effectId = "class:arqueiro#0")
        val adjusted = p.adjustCheck(
            spec,
            emptyList(),
            additionalBonuses = listOf(declaration, declaration, otherSource)
        )
        assertEquals(spec.modifier + 2, adjusted.modifier)
    }

    // ---- bônus de traços (vantagens e perícias) nos testes ----
    @Test fun tracoDaBonusNaSituacaoCerta() {
        val p = basico()
        val esquiva = p.check("esquiva", mapOf("agilidade" to 1), 10)
        val r = p.adjustCheck(esquiva, emptyList(), listOf("esquivador"))
        assertEquals(esquiva.modifier + 2, r.modifier)
        assertTrue(r.label.endsWith("· +2"))
        assertEquals(esquiva, p.adjustCheck(esquiva, emptyList(), listOf("sortudo")))   // sortudo é de sorte
        assertEquals(esquiva, p.adjustCheck(esquiva, emptyList(), emptyList()))
    }

    @Test fun skillInformadaPeloMestreAtivaOBonus() {
        val p = basico()
        val forcaTeste = p.check("sorte", emptyMap(), 7)
        assertEquals(forcaTeste.modifier + 1, p.adjustCheck(forcaTeste, emptyList(), listOf("sortudo")).modifier)   // id do teste
        val esquiva = p.check("esquiva", emptyMap(), 10)
        assertEquals(esquiva, p.adjustCheck(esquiva, emptyList(), listOf("sortudo")))
        assertEquals(esquiva.modifier + 1, p.adjustCheck(esquiva, emptyList(), listOf("sortudo"), skill = "sorte").modifier)
    }

    @Test fun bonusPorAtributoEMultiplosTracosSomam() {
        val p = basico()
        val porForca = p.data.checks.firstOrNull { it.stat == "forca" }
        if (porForca != null) {
            val spec = p.check(porForca.id, mapOf("forca" to 2), 10)
            assertEquals(spec.modifier + 1, p.adjustCheck(spec, emptyList(), listOf("lutador-nato")).modifier)
        }
        val esquiva = p.check("esquiva", emptyMap(), 10)
        val dois = p.adjustCheck(esquiva, emptyList(), listOf("esquivador", "sortudo"), skill = "sorte")
        assertEquals(esquiva.modifier + 3, dois.modifier)
        assertEquals(esquiva.modifier + 2, p.adjustCheck(esquiva, emptyList(), listOf("esquivador", "esquivador")).modifier)  // não duplica
    }

    @Test fun bonusJuntoComPerdaMostraOsDois() {
        val p = basico()
        val s = sorte().copy(label = "Sorte (2d6)")
        val r = p.adjustCheck(s, listOf("amaldicoado"), listOf("sortudo"))
        assertEquals("Sorte (1d6) · Perda · +1", r.label)
        assertEquals(1, r.diceCount)
        assertEquals(s.modifier + 1, r.modifier)
    }

    @Test fun bonusMalFormadoEhRecusado() {
        val a = files("teste-basico").toMutableMap()
        a["tracos.txt"] = "x|vantagem|X|1||d|esquiva\n"
        assertTrue(failure(PackageRegistry().install(a)).any { it.contains("bônus inválido") })
        val b = files("teste-basico").toMutableMap()
        b["tracos.txt"] = "x|vantagem|X|1||d|esquiva:muito\n"
        assertTrue(failure(PackageRegistry().install(b)).isNotEmpty())
    }
}
