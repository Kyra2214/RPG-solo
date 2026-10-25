package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ElementosN16Test {
    private fun pure(
        a: ElementPath,
        d: ElementPath,
    ) = ElementChart.percent(setOf(a), d)

    @Test
    fun cicloDosCinco() {
        assertEquals(150, pure(ElementPath.WATER, ElementPath.FIRE))
        assertEquals(150, pure(ElementPath.FIRE, ElementPath.METAL))
        assertEquals(150, pure(ElementPath.METAL, ElementPath.WIND))
        assertEquals(150, pure(ElementPath.WIND, ElementPath.EARTH))
        assertEquals(150, pure(ElementPath.EARTH, ElementPath.WATER))
        assertEquals(0, pure(ElementPath.FIRE, ElementPath.WATER))
        assertEquals(100, pure(ElementPath.FIRE, ElementPath.FIRE))
        assertEquals(100, pure(ElementPath.FIRE, ElementPath.WIND))
        assertEquals(100, pure(ElementPath.NONE, ElementPath.FIRE))
    }

    @Test
    fun luzETrevas() {
        assertEquals(150, pure(ElementPath.YANG, ElementPath.FIRE))
        assertEquals(150, pure(ElementPath.FIRE, ElementPath.YIN))
        assertEquals(200, pure(ElementPath.YANG, ElementPath.YIN))
        assertEquals(200, pure(ElementPath.YIN, ElementPath.YANG))
        assertEquals(100, pure(ElementPath.YANG, ElementPath.YANG))
    }

    @Test
    fun magiaCombinada() {
        val fogoVento = setOf(ElementPath.FIRE, ElementPath.WIND)
        assertEquals(150, ElementChart.percent(fogoVento, ElementPath.NONE.let { ElementPath.YANG }))
        assertEquals(150, ElementChart.percent(fogoVento, ElementPath.FIRE))
        assertEquals(200, ElementChart.percent(fogoVento, ElementPath.EARTH))
        assertEquals(50, ElementChart.percent(fogoVento, ElementPath.WATER))
        assertEquals(50, ElementChart.percent(fogoVento, ElementPath.METAL))
        assertThrows(IllegalArgumentException::class.java) {
            ElementChart.percent(setOf(ElementPath.FIRE, ElementPath.YANG), ElementPath.FIRE)
        }
    }

    @Test
    fun danoArredondaParaCima() {
        assertEquals(8, ElementChart.apply(5, 150))
        assertEquals(0, ElementChart.apply(9, 0))
        assertEquals(3, ElementChart.apply(5, 50))
        assertEquals(7, ElementChart.apply(7, 100))
    }

    @Test
    fun parseAceitaAliases() {
        assertEquals(ElementPath.METAL, ElementChart.parse("ferro"))
        assertEquals(ElementPath.WATER, ElementChart.parse("água"))
        assertEquals(ElementPath.YANG, ElementChart.parse("Luz"))
        assertEquals(ElementPath.YIN, ElementChart.parse("trevas"))
        assertEquals(ElementPath.NONE, ElementChart.parse("-"))
        assertThrows(IllegalArgumentException::class.java) { ElementChart.parse("gelo") }
    }

    @Test
    fun chanceDeCondicao() {
        assertEquals(24, ConditionChance.chance(20, 2))
        assertEquals(90, ConditionChance.chance(80, 20))
        assertEquals(100, ConditionChance.chance(80, 20, extra = 15))
        assertEquals(20, ConditionChance.chance(20, -3))
    }

    @Test
    fun peitoDeLuzETrevasIgnoraOOutro() {
        assertEquals(0, ElementChart.armorPercent(setOf(ElementPath.YIN), ElementPath.YANG))
        assertEquals(0, ElementChart.armorPercent(setOf(ElementPath.YANG), ElementPath.YIN))
        assertEquals(0, ElementChart.armorPercent(setOf(ElementPath.FIRE), ElementPath.WATER))
        assertEquals(100, ElementChart.armorPercent(setOf(ElementPath.FIRE), ElementPath.WIND))
    }
}
