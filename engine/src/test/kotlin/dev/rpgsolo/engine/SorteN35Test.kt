package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Test

/** N-35: Sorte dá +1 nos testes a cada 2 pontos e +1% de drop por ponto. */
class SorteN35Test {
    @Test fun bonusDeTeste() {
        assertEquals(0, LuckRules.testBonus(0))
        assertEquals(0, LuckRules.testBonus(1))
        assertEquals(1, LuckRules.testBonus(2))
        assertEquals(2, LuckRules.testBonus(5))
        assertEquals(10, LuckRules.testBonus(20))
        assertEquals(0, LuckRules.testBonus(-4))
    }

    @Test fun bonusDeDrop() {
        assertEquals(7, LuckRules.dropPercentBonus(7))
        assertEquals(0, LuckRules.dropPercentBonus(-1))
    }
}
