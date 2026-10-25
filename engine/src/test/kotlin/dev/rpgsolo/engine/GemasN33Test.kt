package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-33.4: gemas engastadas (vagas, valores, PV máximo, remoção, lapidação e ids de item). */
class GemasN33Test {
    private fun engine(): GameEngine {
        val e = GameEngine(5)
        e.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))))
        e.execute(AddItem("hero", "espada-curta", 1))
        e.execute(EquipItem("hero", "espada-curta", "mao"))
        return e
    }

    private fun rejected(r: ExecResult) = r is ExecResult.Rejected

    @Test fun valoresPorTipoEQualidade() {
        assertEquals(2, Gems.bonusOf(Gem("ataque", 1)))
        assertEquals(4, Gems.bonusOf(Gem("defesa", 2)))
        assertEquals(6, Gems.bonusOf(Gem("velocidade", 3)))
        assertEquals(10, Gems.bonusOf(Gem("max_pv", 1)))
        assertEquals(30, Gems.bonusOf(Gem("max_pv", 3)))
    }

    @Test fun engastaAteAsVagasESomaOBonusDaPecaEquipada() {
        val e = engine()
        assertTrue(e.execute(SocketGem("hero", "mao", Gem("ataque", 2), 2)) is ExecResult.Accepted)
        assertTrue(e.execute(SocketGem("hero", "mao", Gem("ataque", 1), 2)) is ExecResult.Accepted)
        assertTrue(rejected(e.execute(SocketGem("hero", "mao", Gem("defesa", 1), 2))))
        val s = e.state
        assertEquals(6, Gems.bonus(s.gems["hero"], s.equipped["hero"], "ataque"))
        assertEquals(0, Gems.bonus(s.gems["hero"], s.equipped["hero"], "defesa"))
    }

    @Test fun pecaTrocadaNaoContaEGemaVoltaQuandoAPecaVolta() {
        val e = engine()
        e.execute(SocketGem("hero", "mao", Gem("ataque", 3), 3))
        e.execute(AddItem("hero", "clava", 1))
        e.execute(EquipItem("hero", "clava", "mao"))
        assertEquals(0, Gems.bonus(e.state.gems["hero"], e.state.equipped["hero"], "ataque"))
        e.execute(EquipItem("hero", "espada-curta", "mao"))
        assertEquals(6, Gems.bonus(e.state.gems["hero"], e.state.equipped["hero"], "ataque"))
    }

    @Test fun gemaDePvSobeEDesceOMaximo() {
        val e = engine()
        e.execute(SocketGem("hero", "mao", Gem("max_pv", 2), 2))
        assertEquals(30, e.state.entities["hero"]!!.resources["pv"]!!.max)
        e.execute(RemoveGem("hero", "mao", 0))
        assertEquals(10, e.state.entities["hero"]!!.resources["pv"]!!.max)
        assertTrue(e.state.gems["hero"].isNullOrEmpty())
    }

    @Test fun recusasDeEngasteERemocao() {
        val e = engine()
        assertTrue(rejected(e.execute(SocketGem("hero", "peito", Gem("ataque", 1), 1))))
        assertTrue(rejected(e.execute(SocketGem("hero", "mao", Gem("sorte", 1), 1))))
        assertTrue(rejected(e.execute(SocketGem("hero", "mao", Gem("ataque", 4), 1))))
        assertTrue(rejected(e.execute(SocketGem("hero", "mao", Gem("ataque", 1), 0))))
        assertTrue(rejected(e.execute(RemoveGem("hero", "mao", 0))))
    }

    @Test fun lapidarSobeCaiOuSome() {
        assertEquals(Gem("ataque", 2), Gems.cut(Gem("ataque", 1), 6))
        assertEquals(Gem("ataque", 3), Gems.cut(Gem("ataque", 3), 20))
        assertEquals(Gem("ataque", 1), Gems.cut(Gem("ataque", 2), 5))
        assertNull(Gems.cut(Gem("ataque", 1), 1))
    }

    @Test fun idsDeItemIdaEVolta() {
        assertEquals("gema-ataque-2", Gems.itemId(Gem("ataque", 2)))
        assertEquals("gema-pv-3", Gems.itemId(Gem("max_pv", 3)))
        assertEquals(Gem("max_pv", 3), Gems.parse("gema-pv-3"))
        assertEquals(Gem("velocidade", 1), Gems.parse("gema-velocidade-1"))
        assertNull(Gems.parse("gema-sorte-1"))
        assertNull(Gems.parse("gema-ataque-4"))
        assertNull(Gems.parse("espada-curta"))
        assertEquals(150, Gems.npcFee(Gem("ataque", 3)))
    }

    @Test fun sobreviveAoReplayEAoSnapshot() {
        val e = engine()
        e.execute(SocketGem("hero", "mao", Gem("defesa", 2), 2))
        val replayed = replay(e.events)
        assertEquals(e.state.gems, replayed.gems)
    }
}
