package dev.rpgsolo.rules

import dev.rpgsolo.engine.CraftKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-23 (pendência): rumores, contratos, desarmar e reprogramar como receitas sem entrada. */
class ComercioMecanismosN23Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun rumoresContratosEMecanismosSemEntrada() {
        val r = pkg().recipes(CraftKind.CRAFT)
        val novas = r.filter { it.id.startsWith("com-") || it.id in setOf("mec-desarmar-engenhoca", "mec-reprogramar-artefato") }
        assertEquals(7, novas.size)
        assertTrue(novas.all { it.inputs.isEmpty() && it.skillId!!.endsWith("-2") && it.guaranteedBy!!.endsWith("-4") })
    }
}
