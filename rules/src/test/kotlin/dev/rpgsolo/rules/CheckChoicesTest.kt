package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CheckChoicesTest {
    private fun files(name: String): Map<String, String> =
        listOf("src/test/resources/packages/$name", "rules/src/test/resources/packages/$name").map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun basico(): RulePackage {
        val r = PackageRegistry(null)
        assertTrue(r.install(files("teste-basico")) is InstallResult.Success)
        return r.get("teste-basico")!!
    }

    @Test fun dificuldadePadraoEMediaQuandoExiste() {
        assertEquals(12, basico().defaultDifficulty())
    }

    @Test fun ofereceTodosOsTestesDoPacoteComModificadorDoPersonagem() {
        val p = basico()
        val choices = p.checkChoices(mapOf("forca" to 3))
        assertEquals(p.data.checks.map { it.id }, choices.map { it.id })
        val ataque = choices.first { it.id == "ataque" }
        assertEquals(3, ataque.spec.modifier)
        assertEquals(12, ataque.spec.difficulty)
        assertTrue("usa" in ataque.description)
    }

    @Test fun dificuldadeExplicitaSobrepoeAPadrao() {
        assertEquals(16, basico().checkChoices(emptyMap(), 16).first().spec.difficulty)
    }

    @Test fun semDificuldadesNoPacoteNaoHaTestesAOferecer() {
        val p = RulePackage(basico().data.copy(difficulties = emptyList()))
        assertNull(p.defaultDifficulty())
        assertTrue(p.checkChoices(emptyMap()).isEmpty())
    }
}
