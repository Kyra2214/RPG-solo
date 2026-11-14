package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** RV-02, RV-03 e MP-04: registro de rivais, panteão e orçamento de reencontro. Escritos, não executados. */
class PanteaoRivaisReencontroTest {
    private fun npc(
        key: String,
        defeat: DefeatOutcome? = null,
        attitude: DefeatAttitude? = null,
        by: String = "",
        alive: Boolean = true,
        day: Int = 0,
    ) = RecurringCharacter(key, key, RecurringKind.NPC, alive = alive, defeat = defeat, defeatAttitude = attitude, defeatedBy = by, lastDay = day)

    private fun deadHero(
        name: String,
        epitaph: String,
        day: Int = 0,
    ) = RecurringCharacter(RecurringIdentity.normalize(name), name, RecurringKind.HERO, alive = false, epitaph = epitaph, lastDay = day)

    @Test fun rivaisSoVemDeNpcVivoComDerrotaEOrdenamRessentimentoPrimeiro() {
        val list =
            listOf(
                npc("calmo", DefeatOutcome.SPARED, DefeatAttitude.RESPECT),
                npc("furioso", DefeatOutcome.HUMILIATED, DefeatAttitude.RESENTMENT),
                npc("morto", DefeatOutcome.FLED, DefeatAttitude.RESENTMENT, alive = false),
                npc("semDerrota"),
            )
        assertEquals(listOf("furioso", "calmo"), Rivals.register(list).map { it.identityKey })
    }

    @Test fun rivalProcuraOHerdeiroQuandoQuemODerrotouMorreu() {
        val rivals =
            Rivals.register(
                listOf(
                    deadHero("Mira Valen", "Caiu na ponte."),
                    npc("grak", DefeatOutcome.FLED, DefeatAttitude.RESENTMENT, by = "Mira Valen"),
                ),
            )
        assertTrue(rivals.single().targetDead)
    }

    @Test fun panteaoJuntaHeroiMortoComEpitafioERivalSemFichaBranca() {
        val entries =
            Pantheon.entries(
                listOf(
                    deadHero("Mira", "Caiu na ponte.", day = 5),
                    deadHero("Sem Epitafio", "", day = 9),
                    npc("grak", DefeatOutcome.SPARED, DefeatAttitude.DEBT, day = 5),
                ),
            )
        assertEquals(2, entries.size)
        assertEquals(PantheonKind.RIVAL, entries.first().kind)
        assertTrue(entries.all { it.line.length <= Pantheon.LINE_MAX })
    }

    @Test fun contextoDoPanteaoLimitaATresEFicaNuloQuandoVazio() {
        assertNull(Pantheon.masterContext(emptyList()))
        val many = (1..5).map { PantheonEntry("h$it", "H$it", PantheonKind.FALLEN_HERO, "linha $it", it) }
        val text = checkNotNull(Pantheon.masterContext(many))
        assertEquals(Pantheon.CONTEXT_MAX, Regex("\n- ").findAll(text).count())
    }

    @Test fun orcamentoDeAparicoesCaiESoSobeComMarcoNovoSemPassarDaBase() {
        fun c(
            appearances: Int,
            milestones: Int = 0,
            away: Int = 5,
        ) = Reencounter.chance(ReencounterCandidate(npc("x"), away, appearances, milestones))
        assertEquals(60, c(0))
        assertEquals(30, c(2))
        assertEquals(40, c(2, milestones = 1))
        assertEquals(60, c(0, milestones = 5))
        assertEquals(5, c(10))
        assertEquals(90, c(0, away = 10))
    }

    @Test fun planoRecusaQuemEstaForaDaJanelaOuNaoEhNpcVivo() {
        val state = GameState()
        fun plan(vararg candidates: ReencounterCandidate) = Reencounter.plan(state, "hero", candidates.toList(), 42L) { 5 }
        assertNull(plan(ReencounterCandidate(npc("perto"), campaignsAway = 4)))
        assertNull(plan(ReencounterCandidate(npc("morto", alive = false), campaignsAway = 20)))
        assertNull(plan(ReencounterCandidate(deadHero("Heroi", "x"), campaignsAway = 20)))
        assertNull(plan())
    }

    @Test fun planoEhDeterministicoPelaSemente() {
        val candidates = listOf(ReencounterCandidate(npc("a"), 12), ReencounterCandidate(npc("b"), 12))
        val one = Reencounter.plan(GameState(), "hero", candidates, 7L) { 5 }
        val two = Reencounter.plan(GameState(), "hero", candidates, 7L) { 5 }
        assertEquals(one?.character?.identityKey, two?.character?.identityKey)
        one?.let {
            assertNotNull(it.reason)
            assertFalse(it.recognizesHero)
            assertEquals(5, it.level)
        }
    }
}
