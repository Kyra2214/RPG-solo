package dev.rpgsolo.rules

import dev.rpgsolo.engine.ElementPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

/** N-17: encantamento permanente de peito e arma (sufixo `@elemento`, um por peça, preço 5 × item × qualidade). */
class EncantamentoN17Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun sufixoDeEncantamento() {
        assertEquals("espada-curta", ItemRef.base("espada-curta#rara@fogo"))
        assertEquals(ItemQuality.RARA, ItemRef.quality("espada-curta#rara@fogo"))
        assertEquals(ItemQuality.NORMAL, ItemRef.quality("espada-curta@agua"))
        assertEquals(ElementPath.FIRE, ItemRef.enchantment("espada-curta#rara@fogo"))
        assertEquals(ElementPath.NONE, ItemRef.enchantment("espada-curta#rara"))
        assertNull(ItemRef.quality("espada-curta@gelo"))
        assertEquals("espada-curta#rara@luz", ItemRef.of("espada-curta", ItemQuality.RARA, ElementPath.YANG))
    }

    @Test fun pacoteResolveItemEncantadoEPreco() {
        val p = pkg()
        assertNotNull(p.item("espada-curta@fogo"))
        assertNull(p.item("espada-curta@gelo"))
        assertEquals(ElementPath.FIRE, p.effectiveElement("espada-curta@fogo"))
        assertEquals(ElementPath.FIRE, p.effectiveElement("espada-de-brasa"))
        assertEquals(75, p.enchantPrice("espada-curta"))
        assertEquals(94, p.enchantPrice("espada-curta#otima"))
    }

    @Test fun restricoes() {
        val p = pkg()
        assertNull(p.enchantBlock("espada-curta"))
        assertNull(p.enchantBlock("couro-batido"))
        assertNotNull(p.enchantBlock("espada-curta@fogo"))
        assertNotNull(p.enchantBlock("espada-de-brasa"))
        assertNotNull(p.enchantBlock("escudo-leve"))
        assertNotNull(p.enchantBlock("pocao-cura"))
    }
}
