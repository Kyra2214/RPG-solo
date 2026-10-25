package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

/** N-18: pergaminhos de elemento (1 dia) em arma e peito; peça encantada ou com elemento próprio não aceita. */
class PergaminhoElementoN18Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun pergaminhosDeclarados() {
        val p = pkg()
        for (e in listOf("agua", "fogo", "ferro", "vento", "terra", "luz", "trevas")) {
            assertNotNull(p.item("pergaminho-elemento-$e"))
            assertEquals("encantar", p.data.creation["item_pergaminho-elemento-${e}_acao"])
            assertEquals(e, p.data.creation["item_pergaminho-elemento-${e}_elemento"])
            assertEquals("1", p.data.creation["item_pergaminho-elemento-${e}_dias"])
        }
    }

    @Test fun restricoes() {
        val p = pkg()
        assertNull(p.scrollBlock("espada-curta"))
        assertNull(p.scrollBlock("couro-batido"))
        assertNotNull(p.scrollBlock("espada-curta@fogo"))
        assertNotNull(p.scrollBlock("espada-de-brasa"))
        assertNotNull(p.scrollBlock("escudo-leve"))
    }
}
