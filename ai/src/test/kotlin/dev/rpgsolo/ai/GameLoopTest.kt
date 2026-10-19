package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.RecordFact
import dev.rpgsolo.engine.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLoopTest {
    private fun session() = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 7L, EngineConfig(deathResource = "pv"))).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
            CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(3, 6))))
        ))
    }
    private val teste = CheckSpec("furtividade", 1, 20, 0, 10)
    private val input = PlayInput("hero", "tento passar escondido", teste, healthResource = "pv")
    private fun dano(n: Int) = """{"narrative":"ok $n","proposals":[{"type":"damage","entityId":"orc","resource":"pv","amount":$n}]}"""
    private fun orcPv(s: CampaignSession) = s.state.entities.getValue("orc").resources.getValue("pv").current

    @Test fun contextoDescreveSaudeSemNumerosEPoeResultadoDoMotor() {
        val s = session()
        val check = s.check(teste)
        val req = ContextAssembler.build(ContextInput(s.state, "hero", "ando", check, "pv"))
        val user = req.messages.last().content
        assertTrue("ileso" in user && "gravemente ferido" in user)
        assertFalse("10/10" in user || "3/6" in user)
        assertTrue("Narre exatamente esse resultado" in user)
        assertTrue("nunca mostre ficha" in req.messages.first().content.lowercase())
    }

    @Test fun orcamentoCortaOpcionaisMasMantemAcaoEPrompt() {
        val s = session()
        s.execute(RecordFact("local", "taverna ".repeat(500)))
        val req = ContextAssembler.build(ContextInput(s.state, "hero", "olho ao redor", budgetTokens = TokenEstimator.estimate(MasterPrompt.SYSTEM) + 200))
        val user = req.messages.last().content
        assertTrue("olho ao redor" in user)
        assertFalse("taverna taverna" in user)
    }

    @Test fun jogadaAplicaEGuardaRegistro() {
        val s = session()
        val loop = GameLoop(ScriptedProvider(listOf(dano(1))))
        val r = loop.play(s, input) as PlayResult.Played
        assertEquals("ok 1", r.record.narrative)
        assertEquals(2, orcPv(s))
        assertTrue(r.record.check != null)
        assertEquals(1, loop.turns.size)
    }

    @Test fun politicaDeVantagemDoTesteRolaDuasVezesEUsaUmResultadoEscolhido() {
        val s = session()
        val loop = GameLoop(ScriptedProvider(listOf(dano(1))))
        val spec = teste.copy(fortuneMode = dev.rpgsolo.engine.FortuneMode.ADVANTAGE)

        val result = loop.play(s, input.copy(check = spec)) as PlayResult.Played

        assertEquals(dev.rpgsolo.engine.FortuneMode.ADVANTAGE, result.record.check!!.spec.fortuneMode)
        assertEquals(1, result.record.fortuneAlternatives.size)
        assertTrue(result.record.fortuneAlternatives.single().spec.label.contains("["))
        assertEquals(2, result.record.markAfterRoll - result.record.markBefore)
        assertTrue(s.mark() >= result.record.markAfterRoll) // ambas as rolagens persistidas antes da narração
    }

    @Test fun falhaDesfazAteARolagem() {
        val s = session()
        val before = s.mark()
        val r = GameLoop(ScriptedProvider(listOf(AiError.InvalidKey()))).play(s, input)
        assertTrue(r is PlayResult.Failed)
        assertEquals(before, s.mark())
    }

    @Test fun desfazVoltaEstadoEOsMesmosDadosSaemDeNovo() {
        val s = session()
        val loop = GameLoop(ScriptedProvider(listOf(dano(1), dano(1))))
        val a = loop.play(s, input) as PlayResult.Played
        assertTrue(loop.undo(s))
        assertEquals(3, orcPv(s))
        val b = loop.play(s, input) as PlayResult.Played
        assertEquals(a.record.check!!.roll.total, b.record.check!!.roll.total)
        assertFalse(GameLoop(ScriptedProvider(emptyList())).undo(s))
    }

    @Test fun refazNarracaoMantemOsDados() {
        val s = session()
        val loop = GameLoop(ScriptedProvider(listOf(dano(1), dano(2))))
        val a = loop.play(s, input) as PlayResult.Played
        val b = loop.regenerate(s) as PlayResult.Played
        assertEquals(a.record.check!!.roll.total, b.record.check!!.roll.total)
        assertEquals("ok 2", b.record.narrative)
        assertEquals(1, orcPv(s)) // só a segunda proposta vale: 3 - 2
        assertEquals(1, loop.turns.size)
    }

    @Test fun refazQueFalhaDesfazAJogadaInteira() {
        val s = session()
        val before = s.mark()
        val loop = GameLoop(ScriptedProvider(listOf(dano(1), AiError.NoInternet())))
        loop.play(s, input)
        assertTrue(loop.regenerate(s) is PlayResult.Failed)
        assertEquals(before, s.mark())
        assertTrue(loop.turns.isEmpty())
    }
}
