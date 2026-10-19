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
}
