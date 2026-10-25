package dev.rpgsolo.rules

import dev.rpgsolo.engine.CraftKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-24 (pendência): pesca, mergulho e apresentações que rendem prata, como receitas sem entrada. */
class MarArtesN24Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun marEArtesTemReceitasSemEntrada() {
        val r = pkg().recipes(CraftKind.CRAFT)
        val mar = r.filter { it.id.startsWith("mar-") }
        val artes = r.filter { it.id.startsWith("art-") }
        assertEquals(5, mar.size)
        assertEquals(4, artes.size)
        assertTrue((mar + artes).all { it.inputs.isEmpty() && it.skillId!!.endsWith("-2") && it.guaranteedBy!!.endsWith("-4") })
        assertTrue(artes.all { it.outputs == mapOf("moeda" to 5) })
    }
}
