package dev.rpgsolo.rules

import dev.rpgsolo.engine.SystemRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-34: o mestre escolhe o multiplicador de PV do chefe entre ×2 e ×3; o motor valida a faixa. */
class ChefeMultiplicadorN34Test {
    private val ref = SystemRef("externo", "externo-1")

    private fun proposal(
        threat: ThreatCategory,
        percent: Int?,
        pv: Int = 100,
    ) = NpcProposal(
        "b1", "Ogro", "chefe", mapOf("resistencia" to 4), emptyList(), mapOf("pv" to pv), RuleSource.PACOTE,
        threat = threat, level = 1, bossPercent = percent,
    )

    @Test fun escalaComPercentualEscolhido() {
        assertEquals(50, ThreatCategory.CHEFE.scale(20, 250))
        assertEquals(40, ThreatCategory.CHEFE.scale(20))
        assertEquals(38, ThreatCategory.healthCap(1, ThreatCategory.CHEFE, 250))
    }

    @Test fun faixaEValidada() {
        assertTrue(NpcFactory.create(proposal(ThreatCategory.CHEFE, 150), null, ref) is NpcResult.Rejected)
        assertTrue(NpcFactory.create(proposal(ThreatCategory.CHEFE, 400), null, ref) is NpcResult.Rejected)
        assertTrue(NpcFactory.create(proposal(ThreatCategory.ELITE, 250), null, ref) is NpcResult.Rejected)
        assertTrue(NpcFactory.create(proposal(ThreatCategory.CHEFE, 250), null, ref) is NpcResult.Created)
    }
}
