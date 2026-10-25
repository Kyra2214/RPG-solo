package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-32.3: ataque, truques, subir de nível e turno do animal. */
class CompanheiroCombateN32Test {
    private fun engine(roll: Int = 10): GameEngine {
        val e = GameEngine(5)
        e.executeAll(
            listOf(
                CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(20, 20)))),
                TameCompanion(
                    "hero",
                    Entity("pet", "Lobo", EntityKind.NPC, resources = mapOf("pv" to Resource(10, 10))),
                    "quadrupede",
                    roll,
                ),
            ),
        )
        return e
    }

    @Test fun contasDeDanoChanceEOrdem() {
        assertEquals(8, Companions.attackDamage(10, 80))
        assertEquals(10, Companions.attackDamage(10, 100))
        assertEquals(13, Companions.attackDamage(10, 130))
        assertEquals(35, Companions.effectChance(30, 1))
        assertEquals(90, Companions.effectChance(40, 12))
        assertEquals(80, Companions.trickD20Percent(8, 1))
        assertEquals(100, Companions.orderChance(80, 100))
        assertEquals(88, Companions.orderChance(80, 40))
        assertTrue(Companions.d20Success(20, 99, 0))
        assertFalse(Companions.d20Success(1, 1, 50))
    }

    @Test fun escolherAtaqueUmaVezESubirComPontoDeHabilidade() {
        val e = engine()
        assertTrue(e.execute(RaiseCompanionAttack("pet")) is ExecResult.Rejected)
        assertTrue(e.execute(ChooseCompanionAttack("pet", "quad-garra")) is ExecResult.Accepted)
        assertTrue(e.execute(ChooseCompanionAttack("pet", "quad-mordida")) is ExecResult.Rejected)
        assertTrue(e.execute(RaiseCompanionAttack("pet")) is ExecResult.Rejected)
        e.execute(LevelUpCompanion("pet"))
        assertEquals(0, e.state.entities.getValue("pet").abilityPoints)
        e.execute(LevelUpCompanion("pet"))
        assertEquals(1, e.state.entities.getValue("pet").abilityPoints)
        assertTrue(e.execute(RaiseCompanionAttack("pet")) is ExecResult.Accepted)
        assertEquals(2, e.state.companions.getValue("pet").attackLevel)
        assertEquals(0, e.state.entities.getValue("pet").abilityPoints)
    }

    @Test fun subirDeNivelSomaCincoDeLealdadeComTeto() {
        val e = engine(roll = 18)
        e.execute(LevelUpCompanion("pet"))
        assertEquals(2, e.state.entities.getValue("pet").level)
        assertEquals(23, e.state.companions.getValue("pet").loyalty)
        repeat(20) { e.execute(LevelUpCompanion("pet")) }
        assertEquals(100, e.state.companions.getValue("pet").loyalty)
    }

    @Test fun aprenderTruqueUmaVezPorDescansoEExigeLealdade() {
        val e = engine(roll = 12)
        assertTrue(e.execute(LearnCompanionTrick("pet", "buscar", 8, 25, 10)) is ExecResult.Rejected)
        val ok = e.execute(LearnCompanionTrick("pet", "vir", 5, 10, 10))
        assertTrue(ok is ExecResult.Accepted)
        assertTrue("vir" in e.state.companions.getValue("pet").tricks)
        assertTrue(e.execute(LearnCompanionTrick("pet", "alertar", 5, 10, 20)) is ExecResult.Rejected)
        e.execute(AdvanceDays(1))
        assertTrue(e.execute(LearnCompanionTrick("pet", "alertar", 5, 10, 20)) is ExecResult.Accepted)
        e.execute(AdvanceDays(1))
        e.execute(LearnCompanionTrick("pet", "sentar-e-ficar", 20, 10, 1))
        assertFalse("sentar-e-ficar" in e.state.companions.getValue("pet").tricks)
    }

    @Test fun ordemExigeTruqueAprendidoEEmCombateSoDeCombate() {
        val e = engine(roll = 12)
        assertTrue(e.execute(OrderCompanionTrick("pet", "vir", 5, 1, false)) is ExecResult.Rejected)
        e.execute(LearnCompanionTrick("pet", "vir", 5, 10, 20))
        val r = e.execute(OrderCompanionTrick("pet", "vir", 5, 1, false)) as ExecResult.Accepted
        assertTrue((r.events.single() as CompanionTrickOrdered).success)
        e.executeAll(listOf(StartCombat(listOf("hero", "pet", "orc"))))
        assertTrue(e.execute(OrderCompanionTrick("pet", "vir", 5, 1, false)) is ExecResult.Rejected)
        assertTrue(e.execute(OrderCompanionTrick("pet", "vir", 5, 1, true)) is ExecResult.Accepted)
    }

    @Test fun turnoDoAnimalCausaDanoAplicaEfeitoEPassaOTurno() {
        val e = engine()
        e.execute(StartCombat(listOf("pet", "hero", "orc")))
        assertTrue(e.companionStrike("pet", "hero", "pv", 3) is ExecResult.Rejected)
        val r = e.companionStrike("pet", "orc", "pv", 8, "sangramento", 3, effectChance = 35, effectRoll = 20)
        assertTrue(r is ExecResult.Accepted)
        assertEquals(12, e.state.entities.getValue("orc").resources.getValue("pv").current)
        assertTrue("sangramento" in e.state.conditions["orc"].orEmpty())
        assertEquals("hero", e.state.combat!!.current)
        assertTrue(e.companionStrike("pet", "orc", "pv", 8) is ExecResult.Rejected)
    }
}
