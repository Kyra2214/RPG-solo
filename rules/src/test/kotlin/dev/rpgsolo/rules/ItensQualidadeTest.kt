package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Catálogo de itens do sistema próprio (poções, pergaminhos, pedras, materiais), qualidade e renascimento. */
class ItensQualidadeTest {
    private fun dir(path: String): File = listOf("src/$path", "rules/src/$path").map(::File).first { it.exists() }

    private fun proprio(): MutableMap<String, String> =
        dir("main/resources/packages/sistema-proprio").listFiles()!!.filter { it.name.endsWith(".txt") }
            .associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(files: Map<String, String> = proprio()): RulePackage =
        when (val result = PackageRegistry().install(files)) {
            is InstallResult.Success -> result.pkg
            is InstallResult.Failure -> throw AssertionError("pacote inválido: ${result.errors.joinToString("; ")}")
        }

    @Test fun catalogoNovoCarregaSemErros() {
        val p = pkg()
        listOf("antidoto", "elixir-anti-sono", "pergaminho-cura", "pedra-fogo", "erva-curativa", "pena-de-fenix")
            .forEach { assertNotNull("item $it", p.item(it)) }
        assertTrue(p.data.items.size >= 60)
        assertEquals(p.data.items.size, p.data.items.map { it.id }.distinct().size)
    }

    @Test fun antidotoEAntiSonoRemovemCondicoesQueExistem() {
        val p = pkg()
        assertEquals("remover-condicao", p.data.creation["item_antidoto_acao"])
        assertEquals("envenenado", p.data.creation["item_antidoto_condicao"])
        assertEquals("sono", p.data.creation["item_elixir-anti-sono_condicao"])
        assertTrue(p.data.conditions.any { it.id == "sono" })
    }

    @Test fun idComQualidadeResolveParaOItemBase() {
        val p = pkg()
        assertEquals("Poção de cura", p.item("pocao-cura#rara")?.name)
        assertEquals(ItemQuality.RARA, ItemRef.quality("pocao-cura#rara"))
        assertEquals("pocao-cura", ItemRef.base("pocao-cura#rara"))
        assertEquals("pocao-cura#boa", ItemRef.of("pocao-cura", ItemQuality.BOA))
        assertEquals("pocao-cura", ItemRef.of("pocao-cura", ItemQuality.NORMAL))
        assertNull(p.item("pocao-cura#mitica"))
        assertNull(ItemRef.quality("pocao-cura#mitica"))
    }

    @Test fun qualidadeMultiplicaOEfeitoPositivoEPreservaPenalidades() {
        val p = pkg()
        assertEquals(5, p.scaleByQuality(5, "pocao-cura"))
        assertEquals(6, p.scaleByQuality(5, "pocao-cura#boa"))
        assertEquals(8, p.scaleByQuality(5, "pocao-cura#rara"))
        assertEquals(10, p.scaleByQuality(5, "pocao-cura#lendaria"))
        assertEquals(2, p.scaleByQuality(1, "espada-curta#lendaria"))
        assertEquals(-1, p.scaleByQuality(-1, "machado#lendaria"))
        assertEquals(1, p.equipmentBonus(listOf("espada-curta"), "dano"))
        assertEquals(2, p.equipmentBonus(listOf("espada-curta#lendaria"), "dano"))
    }

    @Test fun renascimentoVemDoPacoteEInclusiveComQualidade() {
        val cfg = pkg().engineConfig()
        assertEquals(50, cfg.rebirthPercent)
        assertTrue("pena-de-fenix" in cfg.rebirthItems)
        assertTrue("pena-de-fenix#rara" in cfg.rebirthItems)
        assertTrue("pergaminho-salvaguarda" in cfg.rebirthItems)
    }

    @Test fun percentuaisInvalidosOuForaDeOrdemSaoRecusados() {
        val files = proprio()
        files["criacao.txt"] = files.getValue("criacao.txt") + "qualidade_rara_percent|90\n"
        assertTrue(PackageRegistry().install(files) is InstallResult.Failure)
        val files2 = proprio()
        files2["criacao.txt"] = files2.getValue("criacao.txt") + "item_antidoto_condicao|inexistente\n"
        assertTrue(PackageRegistry().install(files2) is InstallResult.Failure)
    }
}
