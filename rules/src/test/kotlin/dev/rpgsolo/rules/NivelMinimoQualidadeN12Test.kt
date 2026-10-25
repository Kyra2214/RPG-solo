package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-12: qualidades normal/ótima/rara/épica/lendária e nível mínimo de equipamento. */
class NivelMinimoQualidadeN12Test {
    private fun files(): MutableMap<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(f: Map<String, String> = files()): RulePackage = (PackageRegistry().install(f) as InstallResult.Success).pkg

    @Test fun percentuaisDasCincoQualidades() {
        val p = pkg()
        assertEquals(
            listOf(100, 125, 150, 200, 250),
            listOf(
                ItemQuality.NORMAL,
                ItemQuality.OTIMA,
                ItemQuality.RARA,
                ItemQuality.EPICA,
                ItemQuality.LENDARIA,
            ).map { p.qualityPercent(it) },
        )
        assertEquals(ItemQuality.OTIMA, ItemRef.quality("adaga#boa"))
    }

    @Test fun epicaELendariaExigemNivelDezMesmoEmItemDeNivelUm() {
        val p = pkg()
        assertEquals(1, p.requiredLevel("adaga"))
        assertEquals(1, p.requiredLevel("adaga#rara"))
        assertEquals(10, p.requiredLevel("adaga#epica"))
        assertEquals(10, p.requiredLevel("adaga#lendaria"))
        assertEquals(10, p.requiredLevel("espada-longa#epica"))
    }

    @Test fun itemTemNivelMinimoDoPacoteEPadraoUm() {
        val p = pkg()
        assertEquals(5, p.item("espada-longa")!!.minLevel)
        assertEquals(5, p.requiredLevel("cota-de-malha#otima"))
        assertEquals(1, p.item("espada-curta")!!.minLevel)
    }

    @Test fun nivelMinimoInvalidoOuEmNaoEquipamentoEhRecusado() {
        val f = files()
        f["itens.txt"] = f.getValue("itens.txt") + "\nfaca-x|Faca X|equipamento|mao|x|ataque:+1|5|0\n"
        assertTrue(PackageRegistry().install(f) is InstallResult.Failure)
        val g = files()
        g["itens.txt"] = g.getValue("itens.txt") + "\nerva-x|Erva X|narrativo|-|x|-|-|5\n"
        assertTrue(PackageRegistry().install(g) is InstallResult.Failure)
    }
}
