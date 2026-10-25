package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-14: escudo dá Resistência; arma de duas mãos bloqueia o escudo e rende ataque ×1,5. */
class EscudoDuasMaosN14Test {
    private fun files(): MutableMap<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(f: Map<String, String> = files()): RulePackage = (PackageRegistry().install(f) as InstallResult.Success).pkg

    @Test fun escudoDaResistenciaEPvEMaosPadraoEhUma() {
        val p = pkg()
        assertEquals(1, p.equipmentBonus(listOf("escudo-leve"), "resistencia"))
        assertEquals(5, p.equipmentBonus(listOf("escudo-leve"), "max_pv"))
        assertEquals(0, p.equipmentBonus(listOf("escudo-leve"), "defesa"))
        assertEquals(1, p.item("espada-longa")!!.hands)
        assertEquals(2, p.item("montante")!!.hands)
    }

    @Test fun duasMaosMultiplicaAtaqueEArredondaParaCima() {
        val p = pkg()
        assertEquals(1, p.equipmentBonus(listOf("espada-longa"), "ataque"))
        assertEquals(2, p.equipmentBonus(listOf("montante"), "ataque"))
        assertEquals(2, p.equipmentBonus(listOf("montante"), "dano"))
        assertEquals(3, p.equipmentBonus(listOf("montante#otima"), "ataque"))
    }

    @Test fun duasMaosBloqueiaEscudoNosDoisSentidos() {
        val p = pkg()
        assertNotNull(p.handsConflict("montante", mapOf("outra-mao" to "escudo-leve")))
        assertNotNull(p.handsConflict("escudo-leve", mapOf("mao" to "montante")))
        assertNull(p.handsConflict("escudo-leve", mapOf("mao" to "espada-longa")))
        assertNull(p.handsConflict("montante", emptyMap()))
        assertNull(p.handsConflict("espada-longa", mapOf("outra-mao" to "escudo-pesado")))
    }

    @Test fun duasMaosInvalidoOuForaDaArmaERecusado() {
        val a = files()
        a["itens.txt"] = a.getValue("itens.txt") + "\ny1|Y1|equipamento|mao|x|ataque:+1|5||-|-|3\n"
        assertTrue(PackageRegistry().install(a) is InstallResult.Failure)
        val b = files()
        b["itens.txt"] = b.getValue("itens.txt") + "\ny2|Y2|equipamento|peito|x|max_pv:+1|5||-|-|2\n"
        assertTrue(PackageRegistry().install(b) is InstallResult.Failure)
    }
}
