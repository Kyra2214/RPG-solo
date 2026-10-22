package dev.rpgsolo.engine

import org.junit.Assert.*
import org.junit.Test

class CanonFactsTest {
    private fun f(id: String = "f1", st: FactStatus = FactStatus.ORIGINAL, src: String? = "s1", ch: String? = "s1#0") =
        CanonFact(id, "c1", "O rei morreu.", st, src, ch)

    @Test fun validaOrigemPorStatus() {
        val e = CanonFacts()
        assertNotNull(e.add(f()))
        assertNull(e.add(f(src = null)))
        assertNull(e.add(f(st = FactStatus.AI_CREATED)))
        assertNotNull(e.add(f(st = FactStatus.AI_CREATED, src = null, ch = null)))
        assertNotNull(e.add(f(st = FactStatus.UNKNOWN, src = null, ch = null)))
        assertNull(e.add(f().copy(text = " ")))
        assertNull(e.add(f().copy(text = "x".repeat(501))))
    }

    @Test fun idDuplicadoEOperacoesInvalidas() {
        val c = CanonFacts().add(f())!!
        assertNull(c.add(f()))
        assertNull(c.setStatus("f1", FactStatus.AI_CREATED))
        assertNull(c.setStatus("nada", FactStatus.UNKNOWN))
        assertEquals(FactStatus.DIVERGENCE, c.setStatus("f1", FactStatus.DIVERGENCE)!!.get("f1")!!.status)
    }

    @Test fun protagonistaSabeEConsultas() {
        val c = CanonFacts().add(f())!!
        assertFalse(c.get("f1")!!.protagonistKnows)
        val k = c.markKnown("f1")!!
        assertTrue(k.get("f1")!!.protagonistKnows)
        assertEquals(1, k.forChunks("c1", setOf("s1#0")).size)
        assertTrue(k.forChunks("c2", setOf("s1#0")).isEmpty())
        assertEquals(k, CanonFacts.fromJson(k.toJson()))
        assertTrue(k.remove("f1").facts.isEmpty())
    }

    @Test fun modoDaFontePersisteNoLogERecusaRepetir() {
        val store = InMemoryEventStore()
        val s = CampaignSession.create(store, CampaignRecord("c", "T", 3L, EngineConfig()))
        assertNull(s.state.sourceMode)
        assertTrue(s.execute(SetSourceMode(SourceMode.ALTERNATE)) is ExecResult.Accepted)
        assertEquals(SourceMode.ALTERNATE, s.state.sourceMode)
        assertTrue(s.execute(SetSourceMode(SourceMode.ALTERNATE)) is ExecResult.Rejected)
        assertTrue(s.execute(SetSourceMode(SourceMode.INSPIRED)) is ExecResult.Accepted)
    }
}
