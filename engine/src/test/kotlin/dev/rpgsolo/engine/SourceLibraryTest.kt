package dev.rpgsolo.engine

import org.junit.Assert.*
import org.junit.Test

class SourceLibraryTest {
    private val txt = "Era uma vez um dragão antigo.".toByteArray()

    @Test fun importaSemDuplicarMesmoHash() {
        val (l1, e1) = SourceLibrary().addImport("a.txt", txt, 1, "id1")
        val (l2, e2) = l1.addImport("copia.txt", txt, 2, "id2")
        assertEquals(1, l2.entries.size)
        assertEquals(e1.id, e2.id)
        assertEquals(SourceStatus.READY, e1.status)
    }

    @Test fun falhaViraEntradaComMotivo() {
        val (l, e) = SourceLibrary().addImport("a.doc", txt, 1, "id1")
        assertEquals(SourceStatus.FAILED, e.status)
        assertNotNull(e.error)
        assertNull(l.link("c1", "id1", 2))
    }

    @Test fun removerNaoApagaVinculosNemCampanhas() {
        val (l, e) = SourceLibrary().addImport("a.txt", txt, 1, "id1")
        val linked = l.link("c1", e.id, 2)!!
        val removed = linked.remove(e.id)
        assertEquals(SourceStatus.REMOVED, removed.get(e.id)!!.status)
        assertEquals(listOf("c1"), removed.dependents(e.id))
        assertEquals(1, removed.forCampaign("c1").size)
        assertNull(removed.link("c2", e.id, 3))
    }

    @Test fun hashPorCampanhaEJson() {
        val (l, e) = SourceLibrary().addImport("a.txt", txt, 1, "id1")
        val linked = l.link("c1", e.id, 2)!!
        assertTrue(linked.matchesLink("c1", e.id, txt))
        assertFalse(linked.matchesLink("c1", e.id, "outro".toByteArray()))
        assertEquals(linked, SourceLibrary.fromJson(linked.toJson()))
        assertEquals(linked, linked.link("c1", e.id, 9))
    }
}
