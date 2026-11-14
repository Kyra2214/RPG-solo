package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** LG-05 (lendas) e MP-04 (escala de nível do reencontro): contratos puros do motor. Escritos, não executados. */
class LegendsReencounterTest {
    private fun hero(
        key: String,
        day: Int,
    ) = PantheonEntry(key, key.replaceFirstChar { it.uppercase() }, PantheonKind.FALLEN_HERO, "$key morreu defendendo a vila.", day)

    private fun rival(key: String) = PantheonEntry(key, key, PantheonKind.RIVAL, "$key foi poupado.", 99)

    private fun seeded(vararg keys: String): GameState {
        var s = GameState()
        for (c in Legends.seed(s, keys.map { hero(it, 1) })) {
            if (c is RecordFact) s = s.apply(FactRecorded(c.key, c.value))
        }
        return s
    }

    @Test fun pickTomaSoHeroisMortosEAteTres() {
        val entries = listOf(rival("r1"), hero("a", 5), hero("b", 4), hero("c", 3), hero("d", 2))
        assertEquals(listOf("a", "b", "c"), Legends.pick(entries).map { it.identityKey })
    }

    @Test fun seedSemLugarPlantaFatosENaoCriaRumor() {
        val commands = Legends.seed(GameState(), listOf(hero("mira", 1)))
        assertTrue(commands.any { it is RecordFact && it.key == Legends.ENABLED_FACT })
        assertTrue(commands.any { it is RecordFact && it.key == Legends.stageKey("mira") && it.value == "0" })
        assertTrue(commands.none { it is StartRumor })
        assertTrue(Legends.seed(GameState(), emptyList()).isEmpty())
    }

    @Test fun seedEhIdempotente() {
        val s = seeded("mira")
        assertTrue(Legends.seed(s, listOf(hero("mira", 1))).none { it is RecordFact && it.key == Legends.stageKey("mira") })
    }

    @Test fun avancaUmEstagioPorDiaERecusaOsInvalidos() {
        val s = seeded("mira")
        val (events, reason) = Legends.advance(s, "mira")
        assertNull(reason)
        val after = events.fold(s) { acc, e -> acc.apply(e) }
        assertEquals(1, Legends.stage(after, "mira"))
        assertNotNull(Legends.advance(after, "mira").second)
        assertNotNull(Legends.advance(s, "ninguem").second)
        val later = after.apply(FactRecorded("lenda.dia.mira", "-1"))
        assertEquals(2, Legends.stage(Legends.advance(later, "mira").first.fold(later) { acc, e -> acc.apply(e) }, "mira"))
    }

    @Test fun estagioFinalNaoPassaDeTres() {
        val s = seeded("mira").apply(FactRecorded(Legends.stageKey("mira"), "3"))
        assertNotNull(Legends.advance(s, "mira").second)
    }

    @Test fun contextoSoExisteComLendas() {
        assertNull(Legends.masterContext(GameState()))
        val text = Legends.masterContext(seeded("mira"))
        assertNotNull(text)
        assertTrue(text!!.contains("Mira"))
        assertTrue(text.contains("advance_legend"))
    }

    @Test fun nivelDoReencontroSegueGrupoECargoComTeto() {
        fun ch(rank: Int) = RecurringCharacter("npc", "Npc", RecurringKind.NPC, offscreen = OffscreenLife(rank = rank))
        assertEquals(10, ReencounterLevel.forCharacter(RecurringCharacter("x", "X", RecurringKind.NPC), 10))
        assertEquals(12, ReencounterLevel.forCharacter(ch(1), 10))
        assertEquals(14, ReencounterLevel.forCharacter(ch(5), 10))
        assertEquals(6, ReencounterLevel.forCharacter(ch(-5), 10))
        assertEquals(1, ReencounterLevel.forCharacter(ch(-5), 2))
    }
}
