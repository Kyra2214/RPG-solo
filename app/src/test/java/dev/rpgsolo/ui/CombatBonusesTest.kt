package dev.rpgsolo.ui

import dev.rpgsolo.rules.ExternalBonus
import org.junit.Assert.assertEquals
import org.junit.Test

class CombatBonusesTest {
    @Test fun camadasCompativeisNoMesmoEscopoNaoSaoColapsadasPeloMaiorValor() {
        val passive = ExternalBonus("ataque", 2, "Passiva da árvore", "skill-tree-passive:ataque:precisao")
        val race = ExternalBonus("ataque", 1, "Traço racial", "race:combat#0")
        assertEquals(
            listOf(passive, race),
            mergePassiveAndCatalogBonuses(listOf(passive), listOf(race))
        )
    }

    @Test fun declaracaoDuplicadaComMesmoIdContinuaSendoAplicadaUmaVez() {
        val passive = ExternalBonus("defesa", 3, "Postura", "skill-tree-passive:defesa:guard")
        val duplicate = passive.copy(source = "cópia duplicada")
        val distinct = ExternalBonus("defesa", 1, "Traço racial", "race:defense#0")
        assertEquals(
            listOf(passive, distinct),
            mergePassiveAndCatalogBonuses(listOf(passive, duplicate), listOf(distinct))
        )
    }
}
