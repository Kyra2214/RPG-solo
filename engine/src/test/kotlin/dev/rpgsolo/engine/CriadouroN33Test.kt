package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-33.2a: criadouro, manutenção semanal, visita e perda do animal. */
class CriadouroN33Test {
    private fun animal(
        id: String,
        level: Int = 1,
    ) = Entity(id, id, EntityKind.NPC, resources = mapOf("pv" to Resource(10, 10)), level = level)

    private fun engine(vararg animals: Pair<String, Int>): GameEngine {
        val e = GameEngine(5)
        e.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))))
        animals.forEach {
                (id, level) ->
            assertTrue(e.execute(TameCompanion("hero", animal(id, level), "quadrupede", 20)) is ExecResult.Accepted)
        }
        return e
    }

    private fun settle(
        e: GameEngine,
        silver: Int,
    ): KennelWeekSettled? {
        val before = e.events.size
        val result = e.execute(SettleKennel(silver))
        assertTrue(result.toString(), result is ExecResult.Accepted)
        return e.events.drop(before).filterIsInstance<KennelWeekSettled>().lastOrNull()
    }

    @Test fun adestrarOSegundoGuardaOPrimeiroNoCriadouro() {
        val e = engine("a" to 1, "b" to 1)
        assertTrue(e.state.companions.getValue("a").stabled)
        assertFalse(e.state.companions.getValue("b").stabled)
        assertEquals(0, e.state.companions.getValue("a").visitedWeek)
        assertEquals(0, e.state.kennelWeek)
    }

    @Test fun limiteDeAnimaisPorGrau() {
        val e = engine("a" to 1)
        assertTrue(e.execute(TameCompanion("hero", animal("b"), "quadrupede", 10, maxAnimals = 1)) is ExecResult.Rejected)
        assertTrue(e.execute(TameCompanion("hero", animal("b"), "quadrupede", 10, maxAnimals = 2)) is ExecResult.Accepted)
        assertEquals(2, e.state.companions.size)
    }

    @Test fun guardarEPrimeiroAnimalAtivoSoUmaVez() {
        val e = engine("a" to 1)
        assertTrue(e.execute(StableCompanion("a")) is ExecResult.Accepted)
        assertTrue(e.state.companions.getValue("a").stabled)
        assertTrue(e.execute(StableCompanion("a")) is ExecResult.Rejected)
        assertTrue(e.execute(FeedCompanion("a", 5, 1)) is ExecResult.Rejected)
        assertTrue(e.execute(PetCompanion("a", 1)) is ExecResult.Rejected)
    }

    @Test fun semanaPagaEVisitadaNaoMudaLealdade() {
        val e = engine("a" to 1, "b" to 3)
        e.execute(AdvanceDays(7))
        val week = settle(e, 100)!!
        assertEquals(1, week.week)
        assertEquals(15, week.silverPaid) // cuidador 10 × 1 animal + 5 × nível 1
        assertTrue(week.keeperPaid)
        assertTrue(week.changes.isEmpty())
        assertEquals(20, e.state.companions.getValue("a").loyalty)
        assertEquals(1, e.state.kennelWeek)
    }

    @Test fun semanaSemVisitaTiraDez() {
        val e = engine("a" to 1, "b" to 1)
        e.execute(AdvanceDays(14))
        val plan = requireNotNull(Kennel.plan(e.state, 1000))
        assertEquals(2, plan.events.size)
        assertEquals(30, plan.totalSilver)
        assertTrue(e.execute(SettleKennel(1000)) is ExecResult.Accepted)
        // semana 1 ainda vale como visitada (guardado na semana 0); a semana 2 não
        assertEquals(10, e.state.companions.getValue("a").loyalty)
        assertEquals(2, e.state.kennelWeek)
    }

    @Test fun visitaEvitaAQuedaEContaComoCarinho() {
        val e = engine("a" to 1, "b" to 1)
        e.execute(AdvanceDays(7))
        assertTrue(e.execute(VisitCompanion("a", 4)) is ExecResult.Accepted)
        assertEquals(24, e.state.companions.getValue("a").loyalty)
        assertEquals(1, e.state.companions.getValue("a").visitedWeek)
        assertTrue(e.execute(VisitCompanion("a", 2)) is ExecResult.Rejected) // 1 por dia
        e.execute(AdvanceDays(7))
        assertTrue(e.execute(SettleKennel(1000)) is ExecResult.Accepted)
        assertEquals(24, e.state.companions.getValue("a").loyalty)
    }

    @Test fun visitaRespeitaTetoERecusas() {
        val e = engine("a" to 1, "b" to 1)
        assertTrue(e.execute(VisitCompanion("a", 7)) is ExecResult.Rejected)
        assertTrue(e.execute(VisitCompanion("a", 0)) is ExecResult.Rejected)
        assertTrue(e.execute(VisitCompanion("b", 3)) is ExecResult.Rejected) // anda com o herói
        assertTrue(e.execute(VisitCompanion("hero", 3)) is ExecResult.Rejected)
        e.execute(FeedCompanion("b", 20, 5))
        e.execute(AdvanceDays(1))
        e.execute(LevelUpCompanion("a"))
        e.execute(LevelUpCompanion("a"))
        e.execute(LevelUpCompanion("a"))
        e.execute(LevelUpCompanion("a"))
        e.execute(LevelUpCompanion("a"))
        assertEquals(45, e.state.companions.getValue("a").loyalty)
        repeat(10) {
            e.execute(AdvanceDays(1))
            e.execute(VisitCompanion("a", 6))
        }
        assertEquals(Companions.MAX_LOYALTY, e.state.companions.getValue("a").loyalty)
    }

    @Test fun cuidadorPrimeiroDepoisOsAnimaisDoMenorNivelAteAcabarAPrata() {
        val e = engine("a" to 1, "b" to 3, "c" to 2)
        // guardados: a (nível 1) e b (nível 3); c anda com o herói
        e.execute(AdvanceDays(7))
        val week = settle(e, 25)!! // cuidador 20, a 5, b (15) não cabe
        assertEquals(25, week.silverPaid)
        assertTrue(week.keeperPaid)
        assertEquals(listOf(KennelLoyalty("b", -20, 0, false)), week.changes)
        assertEquals(listOf("b"), week.lost)
        assertEquals(20, e.state.companions.getValue("a").loyalty)
    }

    @Test fun semPrataTodosFicamSemPagamentoETiramTrinta() {
        val e = engine("a" to 1, "b" to 1)
        e.execute(AdvanceDays(7))
        val week = settle(e, 0)!!
        assertEquals(0, week.silverPaid)
        assertFalse(week.keeperPaid)
        assertEquals(listOf(KennelLoyalty("a", -20, 0, false)), week.changes)
    }

    @Test fun lealdadeZeroPerdeOAnimalERemoveAAliança() {
        val e = engine("a" to 1, "b" to 1)
        assertEquals(ALLY_RELATION_MIN, e.state.relations[relationKey("a", "hero")])
        e.execute(AdvanceDays(7))
        settle(e, 0)
        val a = e.state.companions.getValue("a")
        assertTrue(a.lost)
        assertEquals(0, a.loyalty)
        assertNull(e.state.relations[relationKey("a", "hero")])
        assertTrue(e.state.entities.containsKey("a")) // fica para o readestramento
        assertTrue(e.execute(VisitCompanion("a", 3)) is ExecResult.Rejected)
        assertTrue(e.execute(StableCompanion("a")) is ExecResult.Rejected)
        assertTrue(e.execute(FeedCompanion("a", 5, 1)) is ExecResult.Rejected)
        // a semana seguinte não cobra o animal perdido
        e.execute(AdvanceDays(7))
        val next = settle(e, 100)!!
        assertEquals(0, next.silverPaid)
        assertTrue(next.changes.isEmpty())
    }

    @Test fun animalPerdidoNaoContaNoLimite() {
        val e = engine("a" to 1, "b" to 1)
        e.execute(AdvanceDays(7))
        settle(e, 0)
        assertTrue(e.execute(TameCompanion("hero", animal("c"), "quadrupede", 10, maxAnimals = 2)) is ExecResult.Accepted)
    }

    @Test fun recusasDaLiquidacao() {
        val none = GameEngine(5)
        none.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)))
        assertTrue(none.execute(SettleKennel(10)) is ExecResult.Rejected) // sem animais
        val e = engine("a" to 1)
        assertTrue(e.execute(SettleKennel(10)) is ExecResult.Rejected) // nenhuma semana virou
        e.execute(AdvanceDays(7))
        assertTrue(e.execute(SettleKennel(-1)) is ExecResult.Rejected)
        assertTrue(e.execute(SettleKennel(10)) is ExecResult.Accepted)
        assertTrue(e.execute(SettleKennel(10)) is ExecResult.Rejected) // já liquidada
    }

    @Test fun semAnimalGuardadoNaoPagaNada() {
        val e = engine("a" to 5)
        e.execute(AdvanceDays(21))
        val plan = requireNotNull(Kennel.plan(e.state, 500))
        assertEquals(3, plan.events.size)
        assertEquals(0, plan.totalSilver)
    }

    @Test fun trazerTrocaOAnimalAtivoComOGuardado() {
        val e = engine("a" to 1, "b" to 2) // a guardado, b com o herói
        e.execute(AdvanceDays(Kennel.BRING_DAYS))
        assertTrue(e.execute(BringCompanion("a")) is ExecResult.Accepted)
        assertFalse(e.state.companions.getValue("a").stabled)
        assertTrue(e.state.companions.getValue("b").stabled)
        assertEquals(0, e.state.companions.getValue("b").visitedWeek)
        assertTrue(e.execute(BringCompanion("a")) is ExecResult.Rejected) // já anda com o herói
        assertTrue(e.execute(BringCompanion("hero")) is ExecResult.Rejected)
    }

    @Test fun trazerPerdidoOuMortoRecusa() {
        val e = engine("a" to 1, "b" to 1)
        e.execute(AdvanceDays(7))
        settle(e, 0) // a perdido
        assertTrue(e.execute(BringCompanion("a")) is ExecResult.Rejected)
    }

    @Test fun animalTrazidoNaoPagaManutencao() {
        val e = engine("a" to 1, "b" to 1)
        e.execute(AdvanceDays(1))
        e.execute(BringCompanion("a"))
        e.execute(AdvanceDays(6))
        val week = settle(e, 100)!!
        // agora b está guardado (visitado na semana 0): paga cuidador 10 + 5, sem queda
        assertEquals(15, week.silverPaid)
        assertTrue(week.changes.isEmpty())
    }

    @Test fun replayIgual() {
        val e = engine("a" to 2, "b" to 1)
        e.execute(AdvanceDays(7))
        e.execute(VisitCompanion("a", 3))
        e.execute(AdvanceDays(14))
        e.execute(SettleKennel(7))
        assertEquals(e.state, GameEngine.restore(5, EngineConfig(), e.events).state)
    }
}
