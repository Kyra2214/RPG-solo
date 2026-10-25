package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-33.3: Criador de Animais (lealdade semanal) e Treinador (pontos de status do animal ativo). */
class CriadorTreinadorN33Test {
    private fun animal(
        id: String,
        level: Int = 1,
    ) = Entity(
        id,
        id,
        EntityKind.NPC,
        resources = mapOf("pv" to Resource(10, 10)),
        level = level,
        stats = mapOf("ataque" to 1, "resistencia" to 1, "velocidade" to 1, "destreza" to 1),
    )

    private fun engine(vararg animals: String): GameEngine {
        val e = GameEngine(5)
        e.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))))
        animals.forEach { assertTrue(e.execute(TameCompanion("hero", animal(it), "quadrupede", 10)) is ExecResult.Accepted) }
        return e
    }

    @Test fun criadorSomaPorGrauATodosOsAnimaisInclusiveOAtivo() {
        val e = engine("a", "b") // a guardado, b com o herói
        e.execute(AdvanceDays(7))
        assertTrue(e.execute(SettleKennel(1000, breederDegree = 3)) is ExecResult.Accepted)
        // a: pago e visitado (guardado nesta semana) = 0 de queda, +3; b: +3
        assertEquals(13, e.state.companions.getValue("a").loyalty)
        assertEquals(13, e.state.companions.getValue("b").loyalty)
    }

    @Test fun criadorSomaComAQuedaPorFaltaDePagamento() {
        val e = engine("a", "b")
        e.state.companions.getValue("a")
        e.execute(AdvanceDays(14))
        e.execute(SettleKennel(0, breederDegree = 2)) // duas semanas sem pagar: a perde 30 e 30, mas tem só 10
        val a = e.state.companions.getValue("a")
        assertTrue(a.lost) // já chegou a 0 na primeira semana; o Criador não salva
        assertEquals(0, a.loyalty)
        assertEquals(14, e.state.companions.getValue("b").loyalty) // 10 + 2 + 2
    }

    @Test fun criadorRespeitaOTetoENaoAjudaPerdido() {
        val e = engine("a")
        e.execute(PetCompanion("a", 5))
        e.execute(AdvanceDays(7))
        e.execute(SettleKennel(1000, breederDegree = 5))
        assertTrue(e.state.companions.getValue("a").loyalty <= Companions.MAX_LOYALTY)
        assertTrue(e.execute(SettleKennel(0, breederDegree = 6)) is ExecResult.Rejected)
    }

    @Test fun semCriadorNadaMuda() {
        val e = engine("a")
        e.execute(AdvanceDays(7))
        e.execute(SettleKennel(1000))
        assertEquals(10, e.state.companions.getValue("a").loyalty)
    }

    @Test fun treinadorSegueOTipoDeCorpoEOGrau() {
        val e = engine("a")
        assertEquals(mapOf("ataque" to 2, "resistencia" to 2, "velocidade" to 1), Companions.trainerPoints("quadrupede", 5))
        assertTrue(e.execute(SyncTrainerBonus("hero", 3)) is ExecResult.Accepted)
        val ent = e.state.entities.getValue("a")
        assertEquals(2, ent.stats["ataque"])
        assertEquals(2, ent.stats["resistencia"])
        assertEquals(2, ent.stats["velocidade"])
        assertEquals(1, ent.stats["destreza"])
        // grau maior ajusta só a diferença, sem somar de novo
        e.execute(SyncTrainerBonus("hero", 5))
        assertEquals(3, e.state.entities.getValue("a").stats["ataque"])
        // repetir não muda nada
        val before = e.events.size
        e.execute(SyncTrainerBonus("hero", 5))
        assertEquals(before, e.events.size)
        // grau 0 retira
        e.execute(SyncTrainerBonus("hero", 0))
        assertEquals(1, e.state.entities.getValue("a").stats["ataque"])
    }

    @Test fun bonusPertenceAoHeroiEAcompanhaATroca() {
        val e = engine("a", "b") // b ativo
        e.execute(SyncTrainerBonus("hero", 2))
        assertEquals(2, e.state.entities.getValue("b").stats["ataque"])
        assertEquals(1, e.state.entities.getValue("a").stats["ataque"])
        e.execute(BringCompanion("a"))
        e.execute(SyncTrainerBonus("hero", 2))
        assertEquals(2, e.state.entities.getValue("a").stats["ataque"])
        assertEquals(1, e.state.entities.getValue("b").stats["ataque"]) // guardado perde
        assertFalse(e.state.companions.getValue("b").trainerBonus.isNotEmpty())
        assertTrue(e.execute(SyncTrainerBonus("hero", 6)) is ExecResult.Rejected)
    }
}
