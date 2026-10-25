package dev.rpgsolo.rules

import dev.rpgsolo.engine.CraftKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-22 (pendência): rastrear, colher pistas, desarmar armadilha e abrir fechadura como receitas sem entrada. */
class FurtividadeN22Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun furtividadeTemQuatroAcoesSemEntrada() {
        val r = pkg().recipes(CraftKind.CRAFT).filter { it.id.startsWith("fur-") }
        assertEquals(4, r.size)
        assertTrue(r.all { it.inputs.isEmpty() && it.skillId!!.endsWith("-2") && it.guaranteedBy!!.endsWith("-4") })
    }
}
