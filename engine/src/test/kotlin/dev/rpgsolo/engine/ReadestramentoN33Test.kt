package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-33.2d: readestramento do animal perdido (-30 pontos, no máximo 2 tentativas, mantém nível, ataque e truques). */
class ReadestramentoN33Test {
    private fun animal(
        id: String,
        level: Int = 1,
    ) = Entity(id, id, EntityKind.NPC, resources = mapOf("pv" to Resource(10, 10)), level = level)

    /** "a" (guardado, nível 4) e "b" (com o herói); "a" perde a lealdade sem pagamento e fica perdido. */
    private fun lostEngine(): GameEngine {
        val e = GameEngine(5)
        e.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))))
        e.execute(TameCompanion("hero", animal("a", 4), "quadrupede", 20))
        e.execute(ChooseCompanionAttack("a", "mordida"))
        e.execute(TameCompanion("hero", animal("b"), "quadrupede", 20))
        e.execute(AdvanceDays(7))
        e.execute(SettleKennel(0))
        e.execute(AdvanceDays(7))
        e.execute(SettleKennel(0))
        assertTrue(e.state.companions.getValue("a").lost)
        return e
    }

    @Test fun penalidadeDeTrintaPontosComPiso() {
        assertEquals(45, Companions.retrainChance(75))
        assertEquals(5, Companions.retrainChance(20))
        assertEquals(65, Companions.retrainChance(95))
    }

    @Test fun sucessoDevolveOAnimalMantendoNivelEAtaque() {
        val e = lostEngine()
        val r = e.execute(RetrainCompanion("a", baseChance = 75, percentRoll = 45, loyaltyRoll = 12))
        assertTrue(r.toString(), r is ExecResult.Accepted)
        val a = e.state.companions.getValue("a")
        assertFalse(a.lost)
        assertFalse(a.stabled)
        assertEquals(12, a.loyalty)
        assertEquals("mordida", a.attackId)
        assertEquals(4, e.state.entities.getValue("a").level)
        assertEquals(ALLY_RELATION_MIN, e.state.relations[relationKey("a", "hero")])
        // quem andava com o herói vai para o criadouro
        assertTrue(e.state.companions.getValue("b").stabled)
        val event = e.events.filterIsInstance<CompanionRetrained>().last()
        assertTrue(event.success)
        assertEquals(45, event.chance)
        assertEquals(1, event.attempt)
    }

    @Test fun rolagemAcimaDaChanceFalhaEGastaUmaTentativa() {
        val e = lostEngine()
        assertTrue(e.execute(RetrainCompanion("a", 75, 46, 12)) is ExecResult.Accepted)
        val a = e.state.companions.getValue("a")
        assertTrue(a.lost)
        assertFalse(a.abandoned)
        assertEquals(1, a.retrainAttempts)
        assertEquals(0, a.loyalty)
        // ainda dá para tentar a segunda vez
        assertTrue(e.execute(RetrainCompanion("a", 75, 10, 8)) is ExecResult.Accepted)
        assertFalse(e.state.companions.getValue("a").lost)
    }

    @Test fun segundaFalhaPerdeParaSempre() {
        val e = lostEngine()
        e.execute(RetrainCompanion("a", 75, 99, 12))
        e.execute(RetrainCompanion("a", 75, 99, 12))
        val a = e.state.companions.getValue("a")
        assertTrue(a.abandoned)
        assertTrue(a.lost)
        assertEquals(2, a.retrainAttempts)
        assertTrue(e.execute(RetrainCompanion("a", 100, 1, 12)) is ExecResult.Rejected)
        assertTrue(e.execute(BringCompanion("a")) is ExecResult.Rejected)
        assertEquals("o animal foi perdido para sempre", a.awayReason())
    }

    @Test fun recusas() {
        val e = lostEngine()
        assertTrue(e.execute(RetrainCompanion("b", 75, 1, 12)) is ExecResult.Rejected) // não está perdido
        assertTrue(e.execute(RetrainCompanion("x", 75, 1, 12)) is ExecResult.Rejected)
        assertTrue(e.execute(RetrainCompanion("a", 75, 0, 12)) is ExecResult.Rejected)
        assertTrue(e.execute(RetrainCompanion("a", 75, 101, 12)) is ExecResult.Rejected)
        assertTrue(e.execute(RetrainCompanion("a", 75, 10, 21)) is ExecResult.Rejected)
        assertTrue(e.execute(RetrainCompanion("a", 101, 10, 12)) is ExecResult.Rejected)
        // limite: com 1 animal ativo e limite 1, o sucesso é recusado; a falha não depende do limite
        assertTrue(e.execute(RetrainCompanion("a", 75, 10, 12, maxAnimals = 1)) is ExecResult.Rejected)
        assertTrue(e.execute(RetrainCompanion("a", 75, 99, 12, maxAnimals = 1)) is ExecResult.Accepted)
    }

    @Test fun readestradoVoltaAPagarManutencaoQuandoGuardado() {
        val e = lostEngine()
        e.execute(RetrainCompanion("a", 75, 10, 12))
        e.execute(StableCompanion("a"))
        e.execute(AdvanceDays(7))
        val plan = Kennel.plan(e.state, 1000)!!
        assertTrue(plan.totalSilver > 0)
    }
}
