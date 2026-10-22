package dev.rpgsolo.engine

import org.junit.Assert.*
import org.junit.Test

class AgendaTest {
    private fun session() =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 3L, EngineConfig())).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)),
                    CreateEntity(Entity("ana", "Ana", EntityKind.NPC)),
                    CreateEntity(Entity("rui", "Rui", EntityKind.NPC)),
                ),
            )
        }

    @Test fun relacaoMudaELimita() {
        val s = session()
        s.executeAll(listOf(ChangeRelation("ana", "hero", 30, "ele a salvou")))
        assertEquals(30, s.state.relation("ana", "hero"))
        assertEquals(0, s.state.relation("hero", "ana"))
        s.executeAll(listOf(ChangeRelation("ana", "hero", 200, "grande favor")))
        assertEquals(100, s.state.relation("ana", "hero"))
        assertTrue(s.executeAll(listOf(ChangeRelation("ana", "hero", 5, "mais um"))) is ExecResult.Rejected)
        s.executeAll(listOf(ChangeRelation("ana", "rui", -40, "traição")))
        assertEquals(-40, s.state.relation("ana", "rui"))
    }

    @Test fun relacaoRecusaInvalidos() {
        val s = session()
        val before = s.state.eventCount
        listOf(
            ChangeRelation("ana", "ana", 5, "x"),
            ChangeRelation("ana", "nada", 5, "x"),
            ChangeRelation("ana", "hero", 0, "x"),
            ChangeRelation("ana", "hero", 5, " "),
        ).forEach { assertTrue(it.toString(), s.executeAll(listOf(it)) is ExecResult.Rejected) }
        assertEquals(before, s.state.eventCount)
    }

    @Test fun relogioAvancaAteCompletar() {
        val s = session()
        s.executeAll(listOf(CreateClock("culto", "O culto invoca o demônio", 4)))
        s.executeAll(listOf(AdvanceClock("culto")))
        assertEquals(1, s.state.clocks.getValue("culto").filled)
        assertTrue(s.state.fullClocks().isEmpty())
        s.executeAll(listOf(AdvanceClock("culto", 10)))
        assertEquals(4, s.state.clocks.getValue("culto").filled)
        assertEquals(listOf("culto"), s.state.fullClocks().map { it.id })
        assertTrue(s.executeAll(listOf(AdvanceClock("culto"))) is ExecResult.Rejected)
    }

    @Test fun relogioValidaECombinaComDesfazer() {
        val s = session()
        assertTrue(s.executeAll(listOf(CreateClock("a", "A", 1))) is ExecResult.Rejected)
        assertTrue(s.executeAll(listOf(CreateClock("invalid-combat-tick", "A", 3, combatTick = 4))) is ExecResult.Rejected)
        assertTrue(s.executeAll(listOf(CreateClock("invalid-quest-tick", "A", 3, questTick = 4))) is ExecResult.Rejected)
        assertTrue(s.executeAll(listOf(AdvanceClock("nada"))) is ExecResult.Rejected)
        s.executeAll(listOf(CreateClock("a", "A", 3)))
        assertTrue(s.executeAll(listOf(CreateClock("a", "Outra", 3))) is ExecResult.Rejected)
    }

    @Test fun viagemAvancaSomenteRelogiosConfiguradosEIncompletos() {
        val s = session()
        s.executeAll(
            listOf(
                CreateClock("threat", "Ameaça", 4, moveTick = 3),
                CreateClock("parado", "Plano parado", 4),
                CreateClock("completo", "Plano concluído", 2, moveTick = 2),
                AdvanceClock("completo", 2),
                AddPlace(Place("inicio", "Início")),
                AddPlace(Place("destino", "Destino")),
                ConnectPlaces("inicio", "destino"),
                DiscoverPlace("inicio"),
                DiscoverPlace("destino"),
            ),
        )

        val eventMark = s.engine.events.size
        val result = s.execute(MoveToPlace("destino"))

        assertTrue(result is ExecResult.Accepted)
        assertEquals(3, s.state.clocks.getValue("threat").filled)
        assertEquals(0, s.state.clocks.getValue("parado").filled)
        assertEquals(2, s.state.clocks.getValue("completo").filled)
        assertEquals(listOf("threat"), s.engine.events.drop(eventMark).filterIsInstance<ClockAdvanced>().map { it.id })
    }

    @Test fun fimDeCombateAvancaSomenteRelogiosConfiguradosEIncompletos() {
        val s = session()
        s.executeAll(
            listOf(
                CreateClock("threat", "Ameaça", 4, combatTick = 3),
                CreateClock("parado", "Plano parado", 4),
                CreateClock("completo", "Plano concluído", 2, combatTick = 1),
                AdvanceClock("completo", 2),
                StartCombat(listOf("hero", "ana")),
            ),
        )
        val eventMark = s.engine.events.size

        val result = s.execute(EndCombat)

        assertTrue(result is ExecResult.Accepted)
        assertNull(s.state.combat)
        assertEquals(3, s.state.clocks.getValue("threat").filled)
        assertEquals(0, s.state.clocks.getValue("parado").filled)
        assertEquals(2, s.state.clocks.getValue("completo").filled)
        val emitted = s.engine.events.drop(eventMark)
        assertTrue(emitted.any { it == CombatEnded })
        assertEquals(listOf("threat"), emitted.filterIsInstance<ClockAdvanced>().map { it.id })
    }

    @Test fun derrotaQueEncerraCombateAvancaRelogioConfigurado() {
        val s =
            CampaignSession.create(
                InMemoryEventStore(),
                CampaignRecord("combat", "Combat", 1L, EngineConfig()),
            )
        s.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(10, 10)))),
                CreateEntity(Entity("foe", "Inimigo", EntityKind.NPC, resources = mapOf("hp" to Resource(5, 5)))),
                CreateClock("threat", "Ameaça", 4, combatTick = 1),
                StartCombat(listOf("hero", "foe")),
            ),
        )

        val result = s.execute(Damage("foe", "hp", 5))

        assertTrue(result is ExecResult.Accepted)
        assertNull(s.state.combat)
        assertEquals(1, s.state.clocks.getValue("threat").filled)
        assertEquals(1, s.engine.events.filterIsInstance<ClockAdvanced>().count { it.id == "threat" })
    }

    @Test fun encerramentoDeMissaoAvancaSomenteRelogiosConfiguradosEIncompletos() {
        val s = session()
        s.executeAll(
            listOf(
                CreateClock("threat", "Ameaça", 4, questTick = 2),
                CreateClock("parado", "Plano parado", 4),
                CreateClock("completo", "Plano concluído", 2, questTick = 1),
                AdvanceClock("completo", 2),
            ),
        )
        val eventMark = s.engine.events.size

        val result = s.advanceClocksAfterQuestClosure()

        assertTrue(result is ExecResult.Accepted)
        assertEquals(2, s.state.clocks.getValue("threat").filled)
        assertEquals(0, s.state.clocks.getValue("parado").filled)
        assertEquals(2, s.state.clocks.getValue("completo").filled)
        assertEquals(listOf("threat"), s.engine.events.drop(eventMark).filterIsInstance<ClockAdvanced>().map { it.id })
    }

    @Test fun encerramentoDeCapituloAvancaSomenteRelogiosConfiguradosEIncompletos() {
        val s = session()
        s.executeAll(
            listOf(
                CreateClock("threat", "Ameaça", 4, chapterTick = 2),
                CreateClock("parado", "Plano parado", 4),
                CreateClock("completo", "Plano concluído", 2, chapterTick = 1),
                AdvanceClock("completo", 2),
            ),
        )
        val eventMark = s.engine.events.size

        val result = s.advanceClocksAfterChapterClosure()

        assertTrue(result is ExecResult.Accepted)
        assertEquals(2, s.state.clocks.getValue("threat").filled)
        assertEquals(0, s.state.clocks.getValue("parado").filled)
        assertEquals(2, s.state.clocks.getValue("completo").filled)
        assertEquals(listOf("threat"), s.engine.events.drop(eventMark).filterIsInstance<ClockAdvanced>().map { it.id })
    }

    @Test fun logReproduzOEstado() {
        val s = session()
        s.executeAll(listOf(ChangeRelation("ana", "hero", 10, "ajuda"), CreateClock("c", "C", 3, combatTick = 1), AdvanceClock("c", 2)))
        val again = replay(s.engine.events)
        assertEquals(s.state.relations, again.relations)
        assertEquals(s.state.clocks, again.clocks)
    }
}
