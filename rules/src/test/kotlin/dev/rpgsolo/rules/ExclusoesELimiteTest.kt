package dev.rpgsolo.rules

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Formato: `not:id` nos requisitos e `limite_desvantagens` em criacao.txt (usa só o pacote de teste). */
class ExclusoesELimiteTest {
    private fun base(): MutableMap<String, String> {
        val dir = listOf("src/main/resources/packages/teste-basico", "rules/src/main/resources/packages/teste-basico")
            .map(::File).first { it.exists() }
        return dir.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()
    }

    private fun pkg(tracos: String, criacao: String): RulePackage {
        val f = base()
        f["tracos.txt"] = tracos
        f["criacao.txt"] = criacao
        f["exemplos.txt"] = ""
        val r = PackageRegistry().install(f)
        assertTrue("falhou: ${(r as? InstallResult.Failure)?.errors}", r is InstallResult.Success)
        return (r as InstallResult.Success).pkg
    }

    private val tracos = """
        agil|vantagem|Ágil|1|not:lento|Rápido.
        lento|desvantagem|Lento|-1|not:agil|Devagar.
        manco|desvantagem|Manco|-1||Perna ruim.
        surdo|desvantagem|Surdo|-1||Não ouve.
    """.trimIndent()

    @Test fun exclusaoVale() {
        val p = pkg(tracos, "pontos|8\ncusto_atributo|1\n")
        val rep = p.validateCharacter(mapOf("forca" to 1), listOf("agil", "lento"))
        assertTrue(rep.errors.any { it.contains("não pode ser combinado") })
    }

    @Test fun limiteDeDesvantagensVale() {
        val p = pkg(tracos, "pontos|8\ncusto_atributo|1\nlimite_desvantagens|1\n")
        val rep = p.validateCharacter(mapOf("forca" to 1), listOf("manco", "surdo"))
        assertTrue(rep.errors.any { it.contains("limite é 1") })
        assertTrue(p.validateCharacter(mapOf("forca" to 1), listOf("manco")).valid)
    }

    @Test fun exclusaoParaTracoInexistenteEhRecusada() {
        val f = base()
        f["tracos.txt"] = "agil|vantagem|Ágil|1|not:fantasma|Rápido."
        f["exemplos.txt"] = ""
        assertTrue(PackageRegistry().install(f) is InstallResult.Failure)
    }
}
