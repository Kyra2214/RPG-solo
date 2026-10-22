package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** CP-05: isolamento entre campanhas, reabertura, textos com acento e longos e falha de gravação. */
class CampaignPersistenceTest {
    private class FailingStore(private val inner: InMemoryEventStore = InMemoryEventStore()) : EventStore by inner {
        var failAppend = false
        override fun append(campaignId: String, expectedSize: Int, payloads: List<String>) {
            if (failAppend) throw IllegalStateException("disco cheio")
            inner.append(campaignId, expectedSize, payloads)
        }
    }

    private fun hero(name: String = "Herói") =
        CreateEntity(Entity("hero", name, EntityKind.PLAYER, resources = mapOf("hp" to Resource(10, 10))))

    private fun create(store: EventStore, id: String) =
        CampaignSession.create(store, CampaignRecord(id, "Campanha $id", 1L, EngineConfig()))

    @Test fun campanhasNaoMisturamLogNemEstado() {
        val store = InMemoryEventStore()
        val a = create(store, "a"); val b = create(store, "b")
        a.execute(hero("Ana"))
        assertEquals(1, store.size("a"))
        assertEquals(0, store.size("b"))
        assertTrue(b.state.entities.isEmpty())
        b.execute(hero("Beto"))
        assertEquals("Ana", CampaignSession.open(store, "a").state.entities.getValue("hero").name)
        assertEquals("Beto", CampaignSession.open(store, "b").state.entities.getValue("hero").name)
    }

    @Test fun reabrirRestauraOMesmoEstado() {
        val store = InMemoryEventStore()
        val session = create(store, "c1")
        session.executeAll(listOf(hero(), CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6))))))
        session.execute(Damage("orc", "hp", 2))
        val reopened = CampaignSession.open(store, "c1")
        assertEquals(session.state, reopened.state)
        assertEquals(session.eventCount, reopened.eventCount)
    }

    @Test fun acentosETextosLongosSobrevivemAoSalvarEReabrir() {
        val store = InMemoryEventStore()
        val name = "Ação ÇÃO, ñ — “aspas” 日本 " + "ã".repeat(5000)
        create(store, "c1").execute(hero(name))
        assertEquals(name, CampaignSession.open(store, "c1").state.entities.getValue("hero").name)
    }

    @Test fun desfazerTambemApagaDoArmazenamento() {
        val store = InMemoryEventStore()
        val session = create(store, "c1")
        session.execute(hero())
        val mark = session.mark()
        session.execute(Damage("hero", "hp", 3))
        session.rollbackTo(mark)
        assertEquals(mark, store.size("c1"))
        assertEquals(10, CampaignSession.open(store, "c1").state.entities.getValue("hero").resources.getValue("hp").current)
    }

    @Test fun falhaDeGravacaoFicaRegistradaERecuperaDepois() {
        val store = FailingStore()
        val session = create(store, "c1")
        session.execute(hero())
        assertTrue(session.isSaved)
        store.failAppend = true
        assertTrue(runCatching { session.execute(Damage("hero", "hp", 3)) }.isFailure)
        assertFalse(session.isSaved)
        assertNotNull(session.saveError)
        assertNotNull(session.recover())
        assertEquals(1, store.size("c1"))
        store.failAppend = false
        assertNull(session.recover())
        assertTrue(session.isSaved)
        assertNull(session.saveError)
        assertEquals(session.eventCount, store.size("c1"))
        assertEquals(session.state, CampaignSession.open(store, "c1").state)
    }

    @Test fun sucessoDepoisDeFalhaLimpaOErro() {
        val store = FailingStore()
        val session = create(store, "c1")
        session.execute(hero())
        store.failAppend = true
        runCatching { session.execute(Damage("hero", "hp", 1)) }
        store.failAppend = false
        session.execute(Damage("hero", "hp", 1))
        assertTrue(session.isSaved)
        assertEquals(session.eventCount, store.size("c1"))
    }

    @Test fun maldicaoComDuracaoSobreviveASalvarReabrirEExpiraNoMomentoCorreto() {
        val store = InMemoryEventStore()
        val session = create(store, "curse-persistence")
        session.executeAll(listOf(
            hero(),
            CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6))))
        ))
        assertTrue(session.execute(ApplyCurse("orc", "curse-test", "hero", 2)) is ExecResult.Accepted)
        assertEquals(2, CampaignSession.open(store, "curse-persistence")
            .state.entities.getValue("orc").appliedCurses.getValue("curse-test").remainingRounds)

        val reopenedBeforeCombat = CampaignSession.open(store, "curse-persistence")
        assertTrue(reopenedBeforeCombat.execute(StartCombat(listOf("hero", "orc"))) is ExecResult.Accepted)
        reopenedBeforeCombat.execute(NextTurn)
        reopenedBeforeCombat.execute(NextTurn)

        val reopenedAfterFirstRound = CampaignSession.open(store, "curse-persistence")
        assertEquals(1, reopenedAfterFirstRound.state.entities.getValue("orc")
            .appliedCurses.getValue("curse-test").remainingRounds)
        reopenedAfterFirstRound.execute(NextTurn)
        reopenedAfterFirstRound.execute(NextTurn)

        val reopenedAfterExpiry = CampaignSession.open(store, "curse-persistence")
        assertFalse(reopenedAfterExpiry.state.entities.getValue("orc").appliedCurses.containsKey("curse-test"))
        assertEquals(reopenedAfterExpiry.state, GameEngine.restore(
            seed = 1L,
            config = EngineConfig(),
            events = reopenedAfterExpiry.engine.events
        ).state)
    }
}
