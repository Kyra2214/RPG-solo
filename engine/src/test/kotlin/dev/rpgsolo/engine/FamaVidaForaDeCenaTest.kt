package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** MP-07 (fama e rumores) e MP-03 (vida fora de cena): contratos puros do motor. Escritos, não executados. */
class FamaVidaForaDeCenaTest {
    private fun rumor(
        id: String,
        strength: Map<String, Int>,
    ) = Rumor(id, "dizem algo", strength.keys.first(), 1, RumorTruth.EXACT, RumorChannel.BARD, strength)

    @Test fun semFamaNinguemReconhece() {
        val fx = Fame.effects(GameState(), "vila")
        assertFalse(fx.recognized)
        assertEquals(0, fx.priceModPercent)
        assertEquals(0, fx.initialRelationBonus)
    }

    @Test fun famaBoaBarateiaEMelhoraARelacaoEMaFamaFazOContrario() {
        val boa = GameState().apply(FameChanged("vila", RegionFame(reach = 50, disposition = 80)))
        val fxBoa = Fame.effects(boa, "vila")
        assertTrue(fxBoa.recognized)
        assertEquals(-4, fxBoa.priceModPercent)
        assertEquals(10, fxBoa.initialRelationBonus)
        val ma = GameState().apply(FameChanged("vila", RegionFame(reach = 50, disposition = -80)))
        val fxMa = Fame.effects(ma, "vila")
        assertEquals(4, fxMa.priceModPercent)
        assertEquals(-10, fxMa.initialRelationBonus)
    }

    @Test fun rumorForteNoLugarBastaParaReconhecer() {
        val s = GameState().apply(RumorStarted(rumor("r", mapOf("vila" to 70))))
        assertTrue(Fame.effects(s, "vila").recognized)
        assertFalse(Fame.effects(s, "outro").recognized)
    }

    @Test fun veracidadeEhDeterministicaPelaSemente() {
        assertEquals(Fame.drawTruth("r|3"), Fame.drawTruth("r|3"))
    }

    @Test fun rumorPerdeForcaPorDiaEPorPassagemESomeComOTempo() {
        val links = mapOf("a" to setOf("b"), "b" to setOf("a"))
        val one = Fame.spread(rumor("r", mapOf("a" to 70)), links, 1)
        assertEquals(65, one.strength["a"])
        assertEquals(50, one.strength["b"])
        assertTrue(Fame.spread(rumor("r", mapOf("a" to 70)), links, 20).strength.isEmpty())
        assertEquals(70, Fame.spread(rumor("r", mapOf("a" to 70)), links, 0).strength["a"])
    }

    @Test fun rumoresDoLugarVemDoMaisForteELimitados() {
        var s = GameState()
        for ((i, v) in listOf(30, 90, 60, 10, 0).withIndex()) s = s.apply(RumorStarted(rumor("r$i", mapOf("vila" to v))))
        val ids = Fame.rumorsAt(s, "vila").map { it.id }
        assertEquals(listOf("r1", "r2", "r0"), ids)
    }

    private fun npc(
        personality: String = "ambicioso",
        offscreen: OffscreenLife? = null,
    ) = RecurringCharacter(
        "npc",
        "Npc",
        RecurringKind.NPC,
        personality = personality,
        goals = listOf("tomar o trono"),
        offscreen = offscreen,
        milestones = emptyList(),
    )

    @Test fun vidaForaDeCenaNaoMexeEmHeroiMortoOuSemTempo() {
        val hero = RecurringCharacter("h", "H", RecurringKind.HERO)
        assertSame(hero, OffscreenRules.advance(hero, 5, 1L))
        val dead = npc().copy(alive = false)
        assertSame(dead, OffscreenRules.advance(dead, 5, 1L))
        val n = npc()
        assertSame(n, OffscreenRules.advance(n, 0, 1L))
    }

    @Test fun avancoEhDeterministicoELimitado() {
        val a = OffscreenRules.advance(npc(), 100, 99L, listOf("Vila", "Porto"), listOf("Bia"))
        val b = OffscreenRules.advance(npc(), 100, 99L, listOf("Vila", "Porto"), listOf("Bia"))
        assertEquals(a, b)
        val life = checkNotNull(a.offscreen)
        assertTrue(life.totalSteps <= OffscreenRules.MAX_CAMPAIGNS_PER_ADVANCE)
        assertTrue(life.rank <= OffscreenRules.MAX_NET_RANK_GAIN)
        assertTrue(life.rank >= -OffscreenRules.MAX_RANK)
        assertTrue(life.allies.size <= OffscreenRules.ALLIES_MAX)
        assertTrue(life.steps.size <= OffscreenRules.LOG_MAX)
    }

    @Test fun avancoNuncaTocaEmMarcosNemEmIdentidade() {
        val before = npc()
        val after = OffscreenRules.advance(before, 10, 5L, listOf("Vila"), listOf("Bia"))
        assertEquals(before.milestones, after.milestones)
        assertEquals(before.identityKey, after.identityKey)
        assertEquals(before.name, after.name)
    }
}
