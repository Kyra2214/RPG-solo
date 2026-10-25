package dev.rpgsolo.rules

import dev.rpgsolo.engine.CraftKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-23 (pendência): coleta de materiais da Exploração como receitas sem entrada. */
class ColetaN23Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun cadaOficioDeExploracaoTemUmaColeta() {
        val coletas = pkg().recipes(CraftKind.CRAFT).filter { it.id.startsWith("col-") }
        assertTrue(coletas.all { it.inputs.isEmpty() && it.outputs.isNotEmpty() })
        val arvores = coletas.map { it.skillId!!.removePrefix("profissao-").substringBeforeLast('-') }.toSet()
        assertEquals(RulePackage.HAZARD_TREES.size, arvores.size)
        assertEquals(RulePackage.HAZARD_TREES.toSet(), arvores)
        val coletasBase = coletas.filterNot { it.id.startsWith("col-extrair-gema-") }
        assertTrue(coletasBase.all { it.skillId!!.endsWith("-2") && it.guaranteedBy!!.endsWith("-4") })
        val extracoesDeGemas = coletas.filter { it.id.startsWith("col-extrair-gema-") }
        assertTrue(extracoesDeGemas.all { it.skillId!!.endsWith("-3") && it.guaranteedBy!!.endsWith("-5") })
    }
}
