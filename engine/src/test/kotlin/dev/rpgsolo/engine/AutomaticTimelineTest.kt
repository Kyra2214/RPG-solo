package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutomaticTimelineTest {
    @Test fun includesPlayerFacingEventsAndSkipsPrivateNpcState() {
        val player = Entity("hero", "Hero", EntityKind.PLAYER)
        val npc = Entity("npc", "Hidden NPC", EntityKind.NPC)
        val place = Place(id = "village", name = "Aldeia", description = "Praça")
        val events = listOf(
            EntityCreated(player),
            EntityCreated(npc),
            KnowledgeGained("rumor", "O ferreiro mente", "conversa", believed = true),
            PlaceAdded(place),
            PlaceDiscovered("village"),
            XpGained("hero", 10),
            LevelGained("hero", 2),
            ClockCreated("secret-plan", "Plano secreto", 4),
            ClockAdvanced("secret-plan", 1, 1),
            NpcDossierStored(NpcDossier("npc", secrets = listOf(Secret("secret", "Identidade secreta"))))
        )
        val state = replay(events)
        val timeline = automaticTimeline(events, "hero", state)
        assertTrue(timeline.any { it.text.contains("passou a acreditar") })
        assertTrue(timeline.any { it.text.contains("Aldeia") })
        assertTrue(timeline.any { it.text.contains("experiência") })
        assertTrue(timeline.any { it.text.contains("nível 2") })
        assertFalse(timeline.any { it.text.contains("Plano secreto") })
        assertFalse(timeline.any { it.text.contains("Identidade secreta") })
        assertFalse(timeline.any { it.text.contains("Hidden NPC") })
    }

    @Test fun removingPlayerConditionIsRecorded() {
        val events = listOf(
            EntityCreated(Entity("hero", "Hero", EntityKind.PLAYER)),
            ConditionAdded("hero", "envenenado"),
            ConditionRemoved("hero", "envenenado")
        )
        val timeline = automaticTimeline(events, "hero", replay(events))
        assertEquals(1, timeline.count { it.category == "condição" && it.text.contains("deixou de afetar") })
    }
    @Test fun combatTimelineOnlyIncludesCombatsInvolvingPlayer() {
        val player = Entity("hero", "Hero", EntityKind.PLAYER)
        val enemy = Entity("enemy", "Bandido", EntityKind.NPC)
        val events = listOf(
            EntityCreated(player),
            EntityCreated(enemy),
            CombatStarted(listOf("hero", "enemy")),
            AttackResolved("hero", "enemy", defense = 10, hit = true, damage = 2),
            CombatEnded,
            CombatStarted(listOf("enemy")),
            CombatEnded
        )
        val timeline = automaticTimeline(events, "hero", replay(events))
        assertTrue(timeline.any { it.text == "Um confronto começou." })
        assertTrue(timeline.any { it.text == "Você atingiu Bandido." })
        assertTrue(timeline.any { it.text == "O confronto terminou." })
        assertEquals(3, timeline.count { it.category == "combate" })
    }

    @Test fun agendaClueAppearsOnlyWhenClockCompletesWithoutExposingClockName() {
        val events = listOf(
            EntityCreated(Entity("hero", "Hero", EntityKind.PLAYER)),
            ClockCreated("hidden-clock", "Plano secreto", 2, publicClue = "As portas da cidade se fecharam."),
            ClockAdvanced("hidden-clock", 1, 1),
            ClockAdvanced("hidden-clock", 2, 1)
        )
        val state = replay(events)
        val timeline = automaticTimeline(events, "hero", state)
        assertEquals(1, timeline.count { it.text == "As portas da cidade se fecharam." })
        assertFalse(timeline.any { it.text.contains("Plano secreto") })
        assertFalse(timeline.any { it.text.contains("hidden-clock") })
    }

}
