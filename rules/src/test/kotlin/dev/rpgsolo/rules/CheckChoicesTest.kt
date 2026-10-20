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

    @Test fun pacoteRecusaEscopoDeBonusSemDefinicaoMecanica() {
        val files = files("teste-basico").toMutableMap()
        files["tracos.txt"] = files.getValue("tracos.txt").replace("esquiva:+2", "bonus-fantasma:+2")
        val result = PackageParser.parse(files)
        assertTrue(result is ParseResult.Fail)
        assertTrue((result as ParseResult.Fail).errors.any { "escopo de bônus desconhecido" in it })
    }

    @Test fun pacoteRecusaReferenciaDeRequisitoInexistente() {
        val files = files("teste-basico").toMutableMap()
        files["tracos.txt"] = files.getValue("tracos.txt").replace("forca>=2", "forca-inexistente>=2")
        val result = PackageParser.parse(files)
        assertTrue(result is ParseResult.Fail)
        assertTrue((result as ParseResult.Fail).errors.any { "atributo inexistente" in it })
    }


    @Test fun parserRejeitaEscopoDeBonusSemRegraMecanica() {
        val files = files("teste-basico").toMutableMap()
        val traits = files.getValue("tracos.txt")
        val line = traits.lineSequence().first { it.isNotBlank() && !it.trimStart().startsWith("#") && "esquiva:+2" in it }
        files["tracos.txt"] = traits.replace(line, line.replace("esquiva:+2", "bonus-fantasma:+2"))
        val result = PackageParser.parse(files)
        assertTrue(result is ParseResult.Fail)
        assertTrue((result as ParseResult.Fail).errors.any { "escopo de bônus desconhecido" in it })
    }

    @Test fun validadorConfereEscopoDepoisDaHeranca() {
        val registry = PackageRegistry(null)
        assertTrue(registry.install(files("teste-basico")) is InstallResult.Success)
        val child = files("teste-avancado").toMutableMap()
        val traits = child.getValue("tracos.txt")
        val line = traits.lineSequence().first { it.isNotBlank() && !it.trimStart().startsWith("#") && it.split("|").size >= 6 }
        child["tracos.txt"] = traits.replace(line, line + "|bonus-fantasma:+2")
        val result = registry.install(child)
        assertTrue(result is InstallResult.Failure)
        assertTrue((result as InstallResult.Failure).errors.any { "escopo de bônus sem regra mecânica" in it })
    }

    @Test fun validadorRejeitaEscopoDeEquipamentoSemRegraMesmoAposHeranca() {
        val data = basico().data.copy(
            items = listOf(
                ItemDef(
                    id = "teste-escopo",
                    name = "Equipamento de teste",
                    kind = "equipamento",
                    slot = "mao",
                    description = "Fixture de validação",
                    effects = listOf(ItemEffect("bonus-fantasma", 1))
                )
            )
        )
        val errors = PackageValidator.validate(data)
        assertTrue(errors.any { "escopo de efeito sem regra mecânica" in it })
    }

}
