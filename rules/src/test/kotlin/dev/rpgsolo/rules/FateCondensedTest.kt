package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Pacote embutido do Fate Condensed (CC-BY 3.0). Instala com o catálogo real, que exige licença VERIFICADA. */
class FateCondensedTest {
    private fun res(path: String): File = listOf("src/main/resources/$path", "rules/src/main/resources/$path").map(::File).first { it.exists() }

    private fun pkg(): RulePackage {
        val cat = (CatalogLoader.load(
            res("catalog/estilos.txt").readText(), res("catalog/sistemas.txt").readText(), res("catalog/compat.txt").readText()
        ) as LoadResult.Success).catalog
        val files = res("packages/fate-condensed").listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        val r = PackageRegistry(cat).install(files)
        assertTrue("falhou: ${(r as? InstallResult.Failure)?.errors}", r is InstallResult.Success)
        return (r as InstallResult.Success).pkg
    }

    @Test fun instalaComLicencaVerificadaEAtribuicao() {
        val p = pkg()
        assertEquals("fate", p.manifest.system)
        assertEquals(LicenseStatus.VERIFICADA, p.manifest.licenseStatus)
        assertTrue(p.manifest.license.contains("Evil Hat Productions"))
        assertTrue(p.manifest.license.contains("Creative Commons Attribution 3.0"))
        assertEquals(20, p.creationPoints)
    }

    @Test fun piramideDoExemploFecha20Pontos() {
        val p = pkg()
        val rep = p.validateCharacter(
            mapOf("investigate" to 4, "notice" to 3, "empathy" to 3, "contacts" to 2, "deceive" to 2, "will" to 2,
                "athletics" to 1, "rapport" to 1, "academics" to 1, "drive" to 1), emptyList())
        assertTrue(rep.errors.toString(), rep.valid)
        assertEquals(20, rep.pointsSpent)
    }

    @Test fun passarDe20PontosOuDeMais4EhRecusado() {
        val p = pkg()
        assertTrue(p.validateCharacter(mapOf("fight" to 4, "will" to 4, "notice" to 4, "shoot" to 4, "stealth" to 4, "drive" to 1), emptyList()).errors.isNotEmpty())
        assertTrue(p.validateCharacter(mapOf("fight" to 5), emptyList()).errors.any { it.contains("fora do limite") })
    }

    @Test fun testeUsa4d3ComDificuldadeSomadaDe8() {
        val p = pkg()
        val spec = p.check("fight", mapOf("fight" to 3), p.difficultyValue("fair")!!)
        assertEquals(4, spec.diceCount); assertEquals(3, spec.diceSides)
        assertEquals(3, spec.modifier)
        assertEquals(10, spec.difficulty) // Fair (+2) + 8
    }

    @Test fun recursoDeDestinoComecaEm3SemMorteAutomatica() {
        val p = pkg()
        assertEquals(3, p.resourcesFor(emptyMap()).getValue("pd").max)
        assertEquals(null, p.engineConfig().deathResource)
    }
}
