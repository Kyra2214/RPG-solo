package dev.rpgsolo.rules

import dev.rpgsolo.engine.SystemRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CriacaoDeFichaENpcTest {
    private fun registry(): PackageRegistry {
        val dir = listOf("src/main/resources/packages/teste-basico", "rules/src/main/resources/packages/teste-basico").map(::File).first { it.exists() }
        val files = dir.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        val r = PackageRegistry()
        assertTrue(r.install(files) is InstallResult.Success)
        return r
    }

    private fun pkg() = registry().get("teste-basico")!!

    private fun docs() = DocumentIndex().also {
        it.add(ImportedDocument("meu", "Meu livro", null, listOf("O arqueiro élfico tem pontaria precisa e ataca de longe com arco longo e bônus de furtividade.")))
    }

    // ---- criação de ficha guiada, sem PDF ----
    @Test fun criaFichaPeloModeloSemNenhumPdf() {
        val d = CharacterDraft(pkg(), "f1")
        assertTrue(d.loadExample("guerreiro"))
        d.name = "Brokk"
        assertTrue(d.report().valid)
        val b = d.build() as FichaBuild.Created
        assertEquals("Brokk", b.ficha.name)
        assertEquals(10, b.ficha.resources.getValue("hp").max) // 4 + 2 * forca 3
    }

    @Test fun semNomeNaoConstroi() {
        assertTrue(CharacterDraft(pkg(), "f1").build() is FichaBuild.Invalid)
    }

    @Test fun traitsPermitidosMudamComOsAtributos() {
        val d = CharacterDraft(pkg(), "f1")
        assertFalse(d.allowedTraits().any { it.id == "lutador" })
        assertTrue(d.whyNot("lutador").any { it.contains("exige") })
        d.setStat("forca", 2)
        assertTrue(d.allowedTraits().any { it.id == "lutador" })
        assertTrue(d.whyNot("lutador").isEmpty())
    }

    @Test fun pontosAcabamEOTracoSaiDaLista() {
        val d = CharacterDraft(pkg(), "f1")
        d.setStat("forca", 5); d.setStat("agilidade", 3)
        assertTrue(d.pointsLeft() <= 0)
        assertTrue(d.allowedTraits().none { it.cost > 0 })
    }

    @Test fun dicasFuncionamSemPdfEComPdf() {
        val d = CharacterDraft(pkg(), "f1")
        assertTrue(d.hints("lutador").all { it.source == RuleHint.Source.PACOTE })
        val com = d.hints("arqueiro pontaria", docs())
        assertTrue(com.any { it.source == RuleHint.Source.PDF_DO_USUARIO && it.page == 1 })
    }

    @Test fun abaDeCriacaoListaOsModelosComSuasFichas() {
        val m = SystemModels.list(registry()).first { it.packageId == "teste-basico" }
        assertEquals(8, m.creationPoints)
        assertEquals(2, m.examples.size)
    }

    // ---- NPC: consulta em cadeia ----
    @Test fun consultaVaiDoPacoteAoPdfEDepoisARede() {
        assertEquals(RuleSource.PACOTE, RuleLookupChain.lookup("lutador", pkg(), null).source)
        assertEquals(RuleSource.PDF_DO_USUARIO, RuleLookupChain.lookup("arqueiro elfico", pkg(), docs()).source)
        val web = RuleLookupChain.lookup("arqueiro elfico", pkg(), null)
        assertEquals(RuleSource.PESQUISA_NA_REDE, web.source)
        assertTrue(web.needsWebSearch)
    }

    // ---- NPC: ficha privada e validada pelo motor ----
    @Test fun npcValidoFicaPrivadoEOJogadorSoVeIdentidade() {
        val p = NpcProposal("n1", "Vael", "vilão arqueiro élfico", mapOf("forca" to 2, "agilidade" to 3, "mente" to 0),
            listOf("lutador"), ruleSource = RuleSource.PESQUISA_NA_REDE)
        val sheet = (NpcFactory.create(p, pkg()) as NpcResult.Created).sheet
        assertTrue(sheet.validated && sheet.isPrivate)
        assertEquals("Vael" to "vilão arqueiro élfico", sheet.playerView())
        assertEquals(dev.rpgsolo.engine.EntityKind.NPC, sheet.toEntity().kind)
        assertTrue(sheet.ficha.traits.contains("lutador")) // a mecânica existe e vai para o combate
    }

    @Test fun npcInvalidoEhRecusadoParaAIaTentarDeNovo() {
        val p = NpcProposal("n2", "Vael", "arqueiro", mapOf("forca" to 9), listOf("arcano"), ruleSource = RuleSource.PACOTE)
        val r = NpcFactory.create(p, pkg()) as NpcResult.Rejected
        assertTrue(r.errors.isNotEmpty())
    }

    @Test fun npcSemPacoteEhAceitoMasMarcadoNaoValidado() {
        val p = NpcProposal("n3", "Vael", "arqueiro élfico", mapOf("des" to 18), listOf("Tiro Preciso"),
            resources = mapOf("pv" to 22), ruleSource = RuleSource.PDF_DO_USUARIO)
        assertTrue(NpcFactory.create(p, null) is NpcResult.Rejected) // precisa saber o sistema
        val sheet = (NpcFactory.create(p, null, SystemRef("dnd", "dnd-5-2-srd")) as NpcResult.Created).sheet
        assertFalse(sheet.validated)
        assertEquals(22, sheet.ficha.resources.getValue("pv").max)
        assertTrue(sheet.warnings.isNotEmpty())
    }
}
