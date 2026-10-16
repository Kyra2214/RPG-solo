package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

/**
 * Pacote 3DeT Victory (demo) importado pelo usuário. Fica em `packages-import/3det-victory/`, fora do
 * repositório. Se a pasta não existir (ex.: CI), os testes são ignorados, não falham.
 */
class ImportedPackage3DetTest {
    private fun importDir(): File? =
        listOf("../packages-import/3det-victory", "packages-import/3det-victory").map(::File).firstOrNull { it.exists() }

    private fun catalog(): Catalog {
        fun res(name: String) = listOf("src/main/resources/catalog/$name", "rules/src/main/resources/catalog/$name")
            .map(::File).first { it.exists() }.readText()
        return (CatalogLoader.load(res("estilos.txt"), res("sistemas.txt"), res("compat.txt")) as LoadResult.Success).catalog
    }

    private fun installed(): RulePackage? {
        val dir = importDir() ?: return null
        val files = dir.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        val result = PackageRegistry(catalog()).install(files)
        assertTrue("falhou: ${(result as? InstallResult.Failure)?.errors}", result is InstallResult.Success)
        return (result as InstallResult.Success).pkg
    }

    @Test fun pacoteInstalaPorImportacaoEExemplosPassam() {
        val p = installed(); assumeTrue(p != null); p!!
        assertEquals("3det", p.manifest.system)
        assertEquals("3det-victory", p.manifest.edition)
        assertEquals(10, p.creationPoints)
    }

    @Test fun recursosSaemDosAtributos() {
        val p = installed(); assumeTrue(p != null); p!!
        val r = p.resourcesFor(mapOf("poder" to 2, "habilidade" to 3, "resistencia" to 2))
        assertEquals(2, r.getValue("pa").max)
        assertEquals(15, r.getValue("pm").max)
        assertEquals(10, r.getValue("pv").max)
    }

    @Test fun passarDosDezPontosEhRecusado() {
        val p = installed(); assumeTrue(p != null); p!!
        val rep = p.validateCharacter(mapOf("poder" to 5, "habilidade" to 5, "resistencia" to 1), emptyList())
        assertTrue(rep.errors.any { it.contains("pontos gastos") })
    }

    @Test fun maestriaExigeAPericia() {
        val p = installed(); assumeTrue(p != null); p!!
        val rep = p.validateCharacter(mapOf("poder" to 1), listOf("maestria-luta"))
        assertTrue(rep.errors.any { it.contains("exige") })
    }

    @Test fun testeMontadoParaOMotor() {
        val p = installed(); assumeTrue(p != null); p!!
        val spec = p.check("poder-2d", mapOf("poder" to 3), p.difficultyValue("dificil")!!)
        assertEquals(2, spec.diceCount); assertEquals(6, spec.diceSides)
        assertEquals(3, spec.modifier); assertEquals(12, spec.difficulty)
    }

    @Test fun exclusoesEmDoisSentidos() {
        val p = installed(); assumeTrue(p != null); p!!
        val a = p.validateCharacter(mapOf("poder" to 1), listOf("agil", "atrapalhado"))
        assertTrue(a.errors.any { it.contains("não pode ser combinado") })
        val b = p.validateCharacter(mapOf("poder" to 1), listOf("fracote", "forte"))
        assertTrue(b.errors.any { it.contains("não pode ser combinado") })
    }

    @Test fun tetoDeDesvantagensEhDois() {
        val p = installed(); assumeTrue(p != null); p!!
        val rep = p.validateCharacter(mapOf("poder" to 1), listOf("fracote", "tapado", "infame"))
        assertTrue(rep.errors.any { it.contains("limite é 2") })
    }
}
