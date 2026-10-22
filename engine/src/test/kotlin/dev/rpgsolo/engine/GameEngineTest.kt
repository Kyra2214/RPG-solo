package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    private fun hero() =
        Entity(
            "hero",
            "Herói",
            EntityKind.PLAYER,
            resources = mapOf("hp" to Resource(10, 10), "mana" to Resource(3, 5)),
        )

    private fun orc() = Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6)))

    private fun engine(): GameEngine {
        val e = GameEngine(seed = 1)
        assertTrue(e.executeAll(listOf(CreateEntity(hero()), CreateEntity(orc()))) is ExecResult.Accepted)
        return e
    }

    private fun rejected(r: ExecResult) = r as ExecResult.Rejected

    @Test fun danoReduzEPodeMatar() {
        val e = engine()
        e.execute(Damage("orc", "hp", 4))
        assertEquals(2, e.state.entities.getValue("orc").resources.getValue("hp").current)
        e.execute(Damage("orc", "hp", 99))
        val orc = e.state.entities.getValue("orc")
        assertEquals(0, orc.resources.getValue("hp").current)
        assertFalse(orc.alive)
    }

    @Test fun mortoNaoRecebeCuraNemDano() {
        val e = engine()
        e.execute(Damage("orc", "hp", 99))
        assertTrue(e.execute(Heal("orc", "hp", 5)) is ExecResult.Rejected)
        assertTrue(e.execute(Damage("orc", "hp", 1)) is ExecResult.Rejected)
        assertFalse(e.state.entities.getValue("orc").alive)
    }

    @Test fun maldicaoExigeResistenciaResolvidaExpiraEPermaneceNoReplay() {
        val e = engine()
        assertTrue(e.execute(ApplyCurse("orc", "curse-test", "hero", 2, true, null)) is ExecResult.Rejected)
        assertTrue(e.execute(ApplyCurse("orc", "curse-test", "hero", 2, true, true)) is ExecResult.Rejected)
        assertTrue(e.execute(ApplyCurse("orc", "curse-test", "hero", 2, true, false)) is ExecResult.Accepted)
        assertEquals(2, e.state.entities.getValue("orc").appliedCurses.getValue("curse-test").remainingRounds)
        assertTrue(e.execute(StartCombat(listOf("hero", "orc"))) is ExecResult.Accepted)
        e.execute(NextTurn)
        e.execute(NextTurn)
        assertEquals(1, e.state.entities.getValue("orc").appliedCurses.getValue("curse-test").remainingRounds)
        e.execute(NextTurn)
        e.execute(NextTurn)
        assertFalse(e.state.entities.getValue("orc").appliedCurses.containsKey("curse-test"))
        assertEquals(e.state, replay(e.events))
    }

    @Test fun maldicaoPodeSerRemovidaComMotivoExplicito() {
        val e = engine()
        assertTrue(e.execute(ApplyCurse("orc", "curse-test", "ritual:purificacao")) is ExecResult.Accepted)
        assertTrue(e.execute(RemoveCurse("orc", "curse-test", "purificação concluída")) is ExecResult.Accepted)
        assertFalse(e.state.entities.getValue("orc").appliedCurses.containsKey("curse-test"))
        assertEquals(e.state, replay(e.events))
    }

    @Test fun curaNaoPassaDoMaximo() {
        val e = engine()
        e.execute(Damage("hero", "hp", 3))
        e.execute(Heal("hero", "hp", 100))
        assertEquals(10, e.state.entities.getValue("hero").resources.getValue("hp").current)
    }

    @Test fun gastoInsuficienteEhRejeitadoESemEfeito() {
        val e = engine()
        val before = e.state
        val r = rejected(e.execute(SpendResource("hero", "mana", 4)))
        assertTrue(r.reason.contains("insuficiente"))
        assertEquals(before, e.state)
    }

    @Test fun itemConsumidoNaoReaparece() {
        val e = engine()
        e.execute(AddItem("hero", "pocao", 1))
        assertTrue(e.execute(ConsumeItem("hero", "pocao")) is ExecResult.Accepted)
        assertTrue(e.execute(ConsumeItem("hero", "pocao")) is ExecResult.Rejected)
        assertNull(e.state.inventory["hero"]?.get("pocao"))
    }

    @Test fun ressurreicaoExigeJustificativa() {
        val e = engine()
        e.execute(Damage("orc", "hp", 99))
        assertTrue(e.execute(Revive("orc", "  ")) is ExecResult.Rejected)
        assertTrue(e.execute(Revive("orc", "magia de ressurreição")) is ExecResult.Accepted)
        val orc = e.state.entities.getValue("orc")
        assertTrue(orc.alive)
        assertEquals(1, orc.resources.getValue("hp").current)
    }

    @Test fun loteEhAtomico() {
        val e = engine()
        val before = e.state
        val count = e.eventCount
        val r =
            rejected(
                e.executeAll(
                    listOf(
                        Damage("orc", "hp", 2),
                        ConsumeItem("hero", "inexistente"),
                        Damage("orc", "hp", 2),
                    ),
                ),
            )
        assertEquals(1, r.commandIndex)
        assertEquals(before, e.state)
        assertEquals(count, e.eventCount)
    }

    @Test fun entidadeDuplicadaERejeitada() {
        assertTrue(engine().execute(CreateEntity(hero())) is ExecResult.Rejected)
    }

    @Test fun loadoutRejeitaMaisDeQuatroClassesHibridas() {
        val e = engine()
        val classes = listOf("guerreiro", "mago", "ladino", "clerigo", "druida")
        val result = e.execute(SetSkillTreeLoadout("hero", classes.toSet(), classes, "FIRE"))
        assertTrue(result is ExecResult.Rejected)
    }

    @Test fun efeitoDirecionadoExigeCombateETurnoDoUsuario() {
        val e = GameEngine(seed = 2)
        val hero = hero().copy(skillTreeLevels = mapOf("skill-test" to 1))
        assertTrue(e.executeAll(listOf(CreateEntity(hero), CreateEntity(orc()))) is ExecResult.Accepted)
        val effect = ActiveEffect("habilidade:skill-test", "Teste", "defesa", -3, 2)
        assertTrue(e.execute(ApplyTargetSkillEffects("hero", "orc", "skill-test", listOf(effect))) is ExecResult.Rejected)
        assertTrue(e.execute(StartCombat(listOf("hero", "orc"))) is ExecResult.Accepted)
        assertTrue(e.execute(ApplyTargetSkillEffects("hero", "orc", "skill-test", listOf(effect))) is ExecResult.Accepted)
        assertTrue(e.execute(NextTurn) is ExecResult.Accepted)
        assertTrue(e.execute(ApplyTargetSkillEffects("hero", "orc", "skill-test", listOf(effect))) is ExecResult.Rejected)
        assertEquals(e.state, replay(e.events))
    }

    @Test fun semRecursoDeMorteNaoMorreAutomaticamente() {
        val e = GameEngine(1, EngineConfig(deathResource = null))
        e.execute(CreateEntity(orc()))
        e.execute(Damage("orc", "hp", 99))
        assertTrue(e.state.entities.getValue("orc").alive)
    }

    @Test fun replayDoLogReproduzOEstado() {
        val e = engine()
        e.execute(AddItem("hero", "espada"))
        e.execute(Damage("hero", "hp", 4))
        e.execute(GainXp("hero", 50))
        e.execute(RecordFact("promessa", "levar a carta ao mestre"))
        e.check(CheckSpec("ataque", 1, 20, 3, 12))
        assertEquals(e.state, replay(e.events))
        assertTrue(validateState(e.state).isEmpty())
    }

    @Test fun restaurarDoLogMantemEstadoEDados() {
        val e = engine()
        val spec = CheckSpec("t", 2, 6, 0, 7)
        e.check(spec)
        e.check(spec)
        val restored = GameEngine.restore(1, EngineConfig(), e.events)
        assertEquals(e.state, restored.state)
        assertEquals(e.check(spec).roll, restored.check(spec).roll)
    }

    @Test fun desfazerVoltaEstadoEGeraOsMesmosDados() {
        val e = engine()
        val spec = CheckSpec("atk", 1, 20, 0, 10)
        val mark = e.mark()
        val first = e.check(spec)
        e.execute(Damage("orc", "hp", 3))
        e.rollbackTo(mark)
        assertEquals(6, e.state.entities.getValue("orc").resources.getValue("hp").current)
        assertEquals(first.roll, e.check(spec).roll)
    }

    @Test fun rolagemFicaRegistradaNoLog() {
        val e = engine()
        e.check(CheckSpec("furtividade", 2, 6, 1, 8))
        val ev = e.events.last() as RollRecorded
        assertEquals("furtividade", ev.label)
        assertEquals(ev.dice.sum() + 1, ev.total)
    }

    @Test fun lutaCompletaAtaqueDanoEMorteEncerramCombate() {
        val e = engine()
        assertTrue(e.execute(StartCombat(listOf("hero", "orc"))) is ExecResult.Accepted)
        val target = e.state.entities.getValue("orc")
        val result = e.attack(AttackSpec("hero", "orc", defenseDefault = 0, damage = target.resources.getValue("hp").max))
        val resolved = result as AttackResult.Resolved
        assertTrue(resolved.hit)
        assertEquals(target.resources.getValue("hp").max, resolved.damage)
        assertFalse(e.state.entities.getValue("orc").alive)
        assertNull(e.state.combat)
        assertTrue(e.events.any { it is AttackResolved })
        assertTrue(validateState(e.state).isEmpty())
    }

    @Test fun ataqueContraDefesaAltaNaoCausaDano() {
        val e = engine()
        assertTrue(e.execute(StartCombat(listOf("hero", "orc"))) is ExecResult.Accepted)
        val before = e.state.entities.getValue("orc").resources.getValue("hp").current
        val result = e.attack(AttackSpec("hero", "orc", defenseDefault = 21, damage = 3))
        assertTrue(result is AttackResult.Resolved)
        assertFalse((result as AttackResult.Resolved).hit)
        assertEquals(before, e.state.entities.getValue("orc").resources.getValue("hp").current)
    }

    @Test fun condicaoComDanoExpiraAoAvancarRodadas() {
        val e = engine()
        assertTrue(e.execute(AddCondition("hero", "envenenado", durationRounds = 2, damagePerRound = 1)) is ExecResult.Accepted)
        assertTrue(e.execute(StartCombat(listOf("hero", "orc"))) is ExecResult.Accepted)
        e.execute(NextTurn)
        e.execute(NextTurn)
        assertEquals(9, e.state.entities.getValue("hero").resources.getValue("hp").current)
        assertEquals(1, e.state.conditionStates.getValue("hero").getValue("envenenado").remainingRounds)
        e.execute(NextTurn)
        e.execute(NextTurn)
        assertEquals(8, e.state.entities.getValue("hero").resources.getValue("hp").current)
        assertTrue("envenenado" !in e.state.conditions["hero"].orEmpty())
        assertTrue(validateState(e.state).isEmpty())
    }

    @Test fun promessaRegistradaPermanece() {
        val e = engine()
        e.execute(RecordFact("npc.ana.promessa", "pagar 10 moedas"))
        e.execute(Damage("hero", "hp", 1))
        assertEquals("pagar 10 moedas", e.state.facts["npc.ana.promessa"])
    }

    @Test fun resetDevolveSomentePontosInvestidosEPreservaHabilidadeBasicaGratuita() {
        val e = GameEngine(seed = 1)
        assertTrue(e.execute(CreateEntity(hero().copy(abilityPoints = 5))) is ExecResult.Accepted)
        assertTrue(e.execute(ProgressSkillTree("hero", "race-basic", pointCost = 0, freeBase = true)) is ExecResult.Accepted)
        assertTrue(e.execute(ProgressSkillTree("hero", "race-basic", pointCost = 1)) is ExecResult.Accepted)
        assertTrue(e.execute(ProgressSkillTree("hero", "class-skill", pointCost = 1)) is ExecResult.Accepted)

        val beforeReset = e.state.entities.getValue("hero")
        assertEquals(3, beforeReset.abilityPoints)
        assertEquals(2, beforeReset.skillTreeInvestments.values.sum())

        assertTrue(e.execute(ResetSkillTreeProgress("hero")) is ExecResult.Accepted)
        val afterReset = e.state.entities.getValue("hero")
        assertEquals(5, afterReset.abilityPoints)
        assertEquals(mapOf("race-basic" to 1), afterReset.skillTreeLevels)
        assertEquals(emptyMap<String, Int>(), afterReset.skillTreeInvestments)
        assertEquals(setOf("race-basic"), afterReset.freeSkillIds)
        assertTrue(validateState(e.state).isEmpty())
    }
}
