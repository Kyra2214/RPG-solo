package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/** N-35: cada receita define o próprio tempo (coluna 11 de receitas.txt, em dias). */
class OficioTempoN35Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun tempoVemDaReceita() {
        val p = pkg()
        assertEquals(2, p.recipe("forjar-cota-de-malha")?.days)
        assertEquals(1, p.recipe("forjar-espada-longa")?.days)
        assertEquals(0, p.recipe("forjar-barra-ferro")?.days)
    }
}
