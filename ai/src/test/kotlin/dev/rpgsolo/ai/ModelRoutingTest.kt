package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Outcome
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.RollResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelRoutingTest {
    private val cfg = ProviderConfig("https://api.exemplo.com/v1/chat/completions", "principal", "sk-chave-1234")
    private val reply = HttpReply(200, """{"choices":[{"message":{"content":"{}"}}]}""")

    @Test fun semModelosExtrasTodosOsPapeisSaoOMesmo() {
        val r = ProviderFactory.buildRoles(ProviderKind.OPENAI_COMPATIBLE, cfg, " ", null, transport = FakeTransport(emptyList()))
        assertSame(r.narrator, r.cheap)
        assertSame(r.narrator, r.premium)
        assertFalse(r.hasPremium)
    }

    @Test fun modeloIgualAoPrincipalNaoCriaOutroProvedor() {
        val r =
            ProviderFactory.buildRoles(
                ProviderKind.OPENAI_COMPATIBLE,
                cfg,
                "principal",
                "principal",
                transport = FakeTransport(emptyList()),
            )
        assertSame(r.narrator, r.cheap)
        assertFalse(r.hasPremium)
    }

    @Test fun cadaPapelUsaSeuModeloNoMesmoEndpointEChave() {
        val t = FakeTransport(listOf(reply, reply, reply))
        val r = ProviderFactory.buildRoles(ProviderKind.OPENAI_COMPATIBLE, cfg, "barato", "premium", transport = t)
        assertTrue(r.hasPremium)
        val q = AiRequest(listOf(AiMessage(Role.USER, "oi")))
        r.cheap.complete(q)
        assertTrue("\"model\":\"barato\"" in t.lastBody)
        r.narrator.complete(q)
        assertTrue("\"model\":\"principal\"" in t.lastBody)
        r.premium.complete(q)
        assertTrue("\"model\":\"premium\"" in t.lastBody)
        assertEquals("Bearer sk-chave-1234", t.lastHeaders["Authorization"])
    }

    @Test fun soCriticoEImportante() {
        fun res(o: Outcome) = CheckResult(CheckSpec("t", 1, 20, 0, 10), RollResult("1d20", listOf(10), 0), 0, o)
        assertEquals(NarrationTier.PREMIUM, Importance.of(res(Outcome.CRITICAL_SUCCESS)))
        assertEquals(NarrationTier.PREMIUM, Importance.of(res(Outcome.CRITICAL_FAILURE)))
        assertEquals(NarrationTier.STANDARD, Importance.of(res(Outcome.SUCCESS)))
        assertEquals(NarrationTier.STANDARD, Importance.of(res(Outcome.FAILURE)))
        assertEquals(NarrationTier.STANDARD, Importance.of(null))
    }

    private fun session() =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 3L, EngineConfig())).also {
            it.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))))
        }

    private fun ok(n: String) = """{"narrative":"$n"}"""

    @Test fun jogadaSemCriticoUsaONarradorPadrao() {
        val padrao = ScriptedProvider(listOf(ok("normal")))
        val premium = ScriptedProvider(emptyList())
        // Dificuldade impossível de falhar com crítico: sem faixas críticas, o resultado é só sucesso/falha.
        val loop = GameLoop(padrao, premiumRunner = TurnRunner(premium))
        val r = loop.play(session(), PlayInput("hero", "ando", CheckSpec("t", 1, 20, 0, 1)))
        assertTrue(r is PlayResult.Played)
        assertEquals(1, padrao.requests.size)
        assertTrue(premium.requests.isEmpty())
    }

    @Test fun criticoVaiParaONarradorPremiumEDesfazerContinuaIntacto() {
        val padrao = ScriptedProvider(emptyList())
        val premium = ScriptedProvider(listOf(ok("épico")))
        val loop = GameLoop(padrao, premiumRunner = TurnRunner(premium))
        // Toda soma 1..20 é "crítico de sucesso": o teste sempre cai na faixa.
        val spec = CheckSpec("t", 1, 20, 0, 1, criticalSuccessRange = 1..40)
        val s = session()
        val before = s.mark()
        val r = loop.play(s, PlayInput("hero", "salto o abismo", spec)) as PlayResult.Played
        assertEquals("épico", r.record.narrative)
        assertTrue(padrao.requests.isEmpty())
        assertEquals(1, premium.requests.size)
        assertTrue(loop.undo(s))
        assertEquals(before, s.mark())
    }

    @Test fun semPremiumConfiguradoCriticoUsaOPadrao() {
        val padrao = ScriptedProvider(listOf(ok("normal")))
        val spec = CheckSpec("t", 1, 20, 0, 1, criticalSuccessRange = 1..40)
        assertTrue(GameLoop(padrao).play(session(), PlayInput("hero", "salto", spec)) is PlayResult.Played)
        assertEquals(1, padrao.requests.size)
    }
}
