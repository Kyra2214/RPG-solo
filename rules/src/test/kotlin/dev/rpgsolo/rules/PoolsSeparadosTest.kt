package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Criação de ficha: os 10 pontos de status travam no limite e não se misturam com perícias nem com vantagens/desvantagens. */
class PoolsSeparadosTest {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun statusTravamNoLimiteDeDez() {
        val d = CharacterDraft(pkg(), "p1")
        assertTrue(d.setStat("poder", 6))
        assertTrue(d.setStat("agilidade", 4))
        assertEquals(0, d.statPointsLeft())
        assertFalse(d.setStat("sorte", 1))
        assertEquals(0, d.stat("sorte"))
    }

    @Test fun vantagensNaoGastamPontosDeStatus() {
        val p = pkg()
        val vantagem = p.data.traits.first { p.isAdvantage(it) }
        val d = CharacterDraft(p, "p3")
        d.setStat("poder", 10)
        d.addTrait(vantagem.id)
        assertEquals(0, d.statPointsLeft())
        assertTrue(d.report().errors.none { it.contains("pontos gastos") })
    }
}
