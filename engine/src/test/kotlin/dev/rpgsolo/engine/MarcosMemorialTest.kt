package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** MP-01 (marcos) e LG-06 (memorial): consulta, certeza e contexto. Escritos, não executados. */
class MarcosMemorialTest {
    private fun marco(
        id: String,
        day: Int,
        knowledge: Map<String, Certainty>,
        participants: List<String> = listOf("hero", "npc"),
        consequence: String = "",
    ) = Milestone(id, MilestoneType.MERCY, participants, "poupou $id", day = day, consequence = consequence, knowledge = knowledge)

    private fun state(vararg ms: Milestone) = ms.fold(GameState()) { s, m -> s.apply(MilestoneRecorded(m)) }

    @Test fun certezaSoDegradaDeConfirmadoParaRumor() {
        assertEquals(Certainty.RUMOR, Certainty.CONFIRMED.degrade())
        assertEquals(Certainty.SUSPECTED, Certainty.SUSPECTED.degrade())
        assertEquals(Certainty.RUMOR, Certainty.RUMOR.degrade())
        assertEquals(Certainty.UNKNOWN, Certainty.UNKNOWN.degrade())
    }

    @Test fun npcSoConheceOQueViveuOuDescobriuEUnknownFicaDeFora() {
        val s =
            state(
                marco("a", 2, mapOf("npc" to Certainty.CONFIRMED)),
                marco("b", 1, mapOf("npc" to Certainty.RUMOR)),
                marco("c", 3, mapOf("npc" to Certainty.UNKNOWN)),
                marco("d", 4, mapOf("outro" to Certainty.CONFIRMED)),
            )
        val known = s.milestonesKnownBy("npc")
        assertEquals(listOf("b", "a"), known.map { it.first.id })
        assertEquals(Certainty.RUMOR, known.first().second)
    }

    @Test fun marcosDeUmaPessoaSaoOsDosParticipantesEmOrdemDeDia() {
        val s = state(marco("a", 5, emptyMap(), participants = listOf("hero")), marco("b", 1, emptyMap(), participants = listOf("hero", "x")), marco("c", 2, emptyMap(), participants = listOf("x")))
        assertEquals(listOf("b", "a"), s.milestonesOf("hero").map { it.id })
    }

    @Test fun contextoDoNpcFicaNuloSemMarcosELimitaAosUltimos() {
        assertNull(MilestoneContext.forNpc(GameState(), "npc"))
        val ms = (1..8).map { marco("m$it", it, mapOf("npc" to Certainty.CONFIRMED)) }
        val text = checkNotNull(MilestoneContext.forNpc(state(*ms.toTypedArray()), "npc", max = 3))
        assertEquals(3, Regex("\n- ").findAll(text).count())
        assertTrue(text.contains("poupou m8"))
        assertTrue(!text.contains("poupou m1"))
    }

    @Test fun contextoDiferenciaRumorDeFato() {
        val text = checkNotNull(MilestoneContext.forNpc(state(marco("a", 1, mapOf("npc" to Certainty.RUMOR), consequence = "ficou em dívida")), "npc"))
        assertTrue(text.contains("só ouviu rumor"))
        assertTrue(text.contains("ficou em dívida"))
    }

    private fun legacy(
        name: String,
        day: Int,
    ) = HeroLegacy(heroId = name.lowercase(), heroName = name, level = 5, diedOnDay = day, kind = DeathKind.UNKNOWN)

    @Test fun memorialOrdenaPeloMaisRecenteEDesempataPeloNome() {
        val cards = Memorial.cards(listOf(legacy("Zed", 3), legacy("Ana", 3), legacy("Bia", 9)))
        assertEquals(listOf("Bia", "Ana", "Zed"), cards.map { it.heroName })
        assertTrue(cards.all { it.line.isNotBlank() })
        assertNotNull(cards.first().level)
        assertTrue(Memorial.cards(emptyList()).isEmpty())
    }
}
