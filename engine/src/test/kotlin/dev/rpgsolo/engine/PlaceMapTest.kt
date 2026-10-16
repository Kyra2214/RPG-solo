package dev.rpgsolo.engine

import org.junit.Assert.*
import org.junit.Test

class PlaceMapTest {
    private fun map() = PlaceMap().apply {
        add(Place("vila", "Vila", "Casas de pedra."))
        add(Place("mina", "Mina", "Escura.", secret = "tem um cofre"))
        add(Place("poco", "Poço"))
        add(Place("torre", "Torre"))
        connect("vila", "mina"); connect("mina", "poco"); connect("vila", "torre")
        discover("vila"); discover("mina"); discover("poco")
    }

    @Test fun jogadorVeSoODescoberto() {
        val v = map().playerView("vila")
        assertTrue("Mina" in v)
        assertFalse("Torre" in v)
        assertTrue("Poço" in v)
        assertFalse("cofre" in map().playerView("mina"))
    }

    @Test fun rotaSoPorLocaisConhecidos() {
        val m = map()
        assertEquals(listOf("vila", "mina", "poco"), m.route("vila", "poco"))
        assertNull(m.route("vila", "torre"))
        m.discover("torre")
        assertEquals(listOf("vila", "torre"), m.route("vila", "torre"))
        assertEquals(listOf("poco", "mina", "vila", "torre"), m.route("poco", "torre"))
    }

    @Test fun passagemEDeMaoDupla() {
        val m = map()
        assertTrue("Vila" in m.playerView("mina"))
    }

    @Test fun contextoDaIaTemSegredoDoLocalAtualMasNaoDosVizinhos() {
        val c = map().sceneContext("mina")
        assertTrue("cofre" in c)
        assertFalse("cofre" in map().sceneContext("vila"))
    }

    @Test fun validacoes() {
        val m = map()
        assertThrows(IllegalArgumentException::class.java) { m.add(Place("vila", "Outra")) }
        assertThrows(IllegalArgumentException::class.java) { m.connect("vila", "nada") }
        assertThrows(IllegalArgumentException::class.java) { m.connect("vila", "vila") }
        assertThrows(IllegalArgumentException::class.java) { m.discover("nada") }
        assertThrows(IllegalArgumentException::class.java) { m.playerView("torre") }
    }
}
