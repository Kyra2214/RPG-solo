package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-26: cura de PV em percentual do máximo, com o valor fixo como mínimo; PM e PA continuam fixos. */
class CuraPercentualN26Test {
    private fun files() =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun pkg() = (PackageRegistry().install(files()) as InstallResult.Success).pkg

    @Test fun pvCuraPercentualComMinimoFixo() {
        val p = pkg()
        assertEquals(5, p.healAmount("pocao-cura", 10))
        assertEquals(30, p.healAmount("pocao-cura", 100))
        assertEquals(10, p.healAmount("bandagem", 100))
        assertEquals(2, p.healAmount("bandagem", 4))
        assertEquals(50, p.healAmount("pocao-cura-maior", 100))
        assertEquals(100, p.healAmount("pocao-cura-suprema", 100))
        assertEquals(40, p.healAmount("pergaminho-cura", 100))
        assertEquals(70, p.healAmount("pergaminho-cura-maior", 100))
        assertEquals(21, p.healAmount("pocao-cura-menor", 101))
    }

    @Test fun pmEPaContinuamFixos() {
        val p = pkg()
        assertEquals(5, p.healAmount("pocao-mana", 500))
        assertEquals(2, p.healAmount("tonico-vigor", 500))
    }

    @Test fun qualidadeEscalaOPercentualAteCem() {
        val p = pkg()
        assertTrue(p.healAmount("pocao-cura#rara", 100)!! > 30)
        assertTrue(p.healAmount("pocao-cura-suprema#epica", 100)!! <= 100)
    }

    @Test fun percentualInvalidoERecusado() {
        val f = files().toMutableMap()
        f["criacao.txt"] = f.getValue("criacao.txt").replace("item_bandagem_percentual|10", "item_bandagem_percentual|0")
        assertTrue(PackageRegistry().install(f) !is InstallResult.Success)
    }
}
