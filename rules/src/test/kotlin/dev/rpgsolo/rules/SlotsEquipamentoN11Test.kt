package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-11: seis peças (elmo, luvas, botas, calça, peito, arma) e efeitos de atributo e de PV máximo. */
class SlotsEquipamentoN11Test {
    private fun files(): MutableMap<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(f: Map<String, String> = files()): RulePackage = (PackageRegistry().install(f) as InstallResult.Success).pkg

    @Test fun pacoteTemUmaPecaParaCadaSlotEAntigosSlotsSumiram() {
        val slots = pkg().data.items.filter { it.kind == "equipamento" }.mapNotNull { it.slot }.toSet()
        assertEquals(setOf("mao", "outra-mao", "elmo", "luvas", "botas", "calca", "peito"), slots)
    }

    @Test fun cadaPecaDaOEfeitoDoSeuSlot() {
        val p = pkg()

        fun scopes(id: String) = p.item(id)!!.effects.map { it.scope }.toSet()
        assertTrue("sorte" in scopes("capacete"))
        assertTrue("destreza" in scopes("luvas-de-couro"))
        assertTrue("agilidade" in scopes("botas-de-couro"))
        assertTrue("defesa" in scopes("calca-reforcada"))
        assertTrue("max_pv" in scopes("couro-batido"))
        assertTrue("inteligencia" in scopes("cajado"))
        assertEquals(4, p.equipmentBonus(listOf("couro-batido"), "max_pv"))
        assertEquals(1, p.equipmentBonus(listOf("botas-de-couro", "luvas-de-couro"), "agilidade"))
    }

    @Test fun escopoDeItemInexistenteEhRecusado() {
        val f = files()
        f["itens.txt"] = f.getValue("itens.txt") + "\nbotas-ruins|Botas ruins|equipamento|botas|x|velocidade:+1|5\n"
        assertTrue(PackageRegistry().install(f) is InstallResult.Failure)
        val g = files()
        g["itens.txt"] = g.getValue("itens.txt") + "\nbotas-ruins|Botas ruins|equipamento|botas|x|max_mana:+1|5\n"
        assertTrue(PackageRegistry().install(g) is InstallResult.Failure)
    }
}
