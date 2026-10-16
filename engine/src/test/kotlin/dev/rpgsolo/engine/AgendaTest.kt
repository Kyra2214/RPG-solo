package dev.rpgsolo.engine

import org.junit.Assert.*
import org.junit.Test

class AgendaTest {
    private fun session() = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 3L, EngineConfig())).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)),
            CreateEntity(Entity("ana", "Ana", EntityKind.NPC)),
            CreateEntity(Entity("rui", "Rui", EntityKind.NPC))
        ))
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
            ChangeRelation("ana", "ana", 5, "x"), ChangeRelation("ana", "nada", 5, "x"),
            ChangeRelation("ana", "hero", 0, "x"), ChangeRelation("ana", "hero", 5, " ")
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
        assertTrue(s.executeAll(listOf(AdvanceClock("nada"))) is ExecResult.Rejected)
        s.executeAll(listOf(CreateClock("a", "A", 3)))
        assertTrue(s.executeAll(listOf(CreateClock("a", "Outra", 3))) is ExecResult.Rejected)
    }

    @Test fun logReproduzOEstado() {
        val s = session()
        s.executeAll(listOf(ChangeRelation("ana", "hero", 10, "ajuda"), CreateClock("c", "C", 3), AdvanceClock("c", 2)))
        val again = replay(s.engine.events)
        assertEquals(s.state.relations, again.relations)
        assertEquals(s.state.clocks, again.clocks)
    }
}
