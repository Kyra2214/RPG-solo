package dev.rpgsolo.ai

import dev.rpgsolo.engine.AddCondition
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.StartCombat
import dev.rpgsolo.rules.FichaBuild
import dev.rpgsolo.rules.InstallResult
import dev.rpgsolo.rules.PackageRegistry
import dev.rpgsolo.rules.RulePackage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PlayerSheetTest {
    private fun pkg(): RulePackage {
        val dir =
            listOf("../rules/src/test/resources/packages/teste-basico", "rules/src/test/resources/packages/teste-basico")
                .map(::File).first { it.exists() }
        val files = dir.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        val r = PackageRegistry()
        assertTrue(r.install(files) is InstallResult.Success)
        return r.get("teste-basico")!!
    }

    private fun ficha(p: RulePackage): Ficha {
        val b =
            p.buildFicha(
                "hero",
                "Brokk",
                "guerreiro cansado",
                mapOf("forca" to 3, "agilidade" to 1, "mente" to 0),
                listOf("lutador", "fragil", "espadas"),
            )
        return (b as FichaBuild.Created).ficha
    }

    private fun session(f: Ficha) =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "hp"))).also {
            it.executeAll(
                listOf(
                    CreateEntity(f.toEntity()),
                    CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6)))),
                ),
            )
        }

    @Test fun fichaMostraAtributosRecursosVantagensEDesvantagens() {
        val p = pkg()
        val f = ficha(p)
        val text = PlayerSheet(p, f).render(session(f).state, "hero")!!
        assertTrue("Brokk" in text && "guerreiro cansado" in text)
        assertTrue("Força 3" in text)
        assertTrue("Vida 10/10" in text)
        assertTrue("Vantagens" in text && "Lutador: Treino de combate corpo a corpo." in text)
        assertTrue("Desvantagens" in text && "Frágil: Corpo delicado" in text)
        assertTrue("Perícias" in text && "Espadas" in text)
    }

    @Test fun fichaAcompanhaOEstadoAtual() {
        val p = pkg()
        val f = ficha(p)
        val s = session(f)
        s.executeAll(listOf(Damage("hero", "hp", 4), AddCondition("hero", "envenenado")))
        val text = PlayerSheet(p, f).render(s.state, "hero")!!
        assertTrue("Vida 6/10" in text)
        assertTrue("Envenenado (Perde vida" in text)
    }

    @Test fun naoMostraFichaDeNpc() {
        val p = pkg()
        val f = ficha(p)
        val text = PlayerSheet(p, f).render(session(f).state, "hero")!!
        assertFalse("Orc" in text)
    }

    @Test fun semPacoteNemFichaAindaMostraOQueOEstadoSabe() {
        val p = pkg()
        val s = session(ficha(p))
        val text = PlayerSheet().render(s.state, "hero")!!
        assertTrue("forca 3" in text && "hp 10/10" in text)
        assertFalse("Vantagens" in text)
    }

    @Test fun jogadorInexistenteNaoGeraFicha() {
        val p = pkg()
        val f = ficha(p)
        assertNull(PlayerSheet(p, f).render(session(f).state, "ninguem"))
    }

    @Test fun contextoLevaAFichaAntesDaAcaoEMesmoComOrcamentoApertado() {
        val p = pkg()
        val f = ficha(p)
        val s = session(f)
        val sheet = PlayerSheet(p, f).render(s.state, "hero")
        val req =
            ContextAssembler.build(
                ContextInput(
                    s.state,
                    "hero",
                    "ataco o orc",
                    playerSheet = sheet,
                    budgetTokens = TokenEstimator.estimate(MasterPrompt.SYSTEM) + 50,
                ),
            )
        val user = req.messages.last().content
        assertTrue("Lutador" in user && "Frágil" in user)
        assertTrue(user.indexOf("Lutador") < user.indexOf("Ação do jogador"))
        assertTrue("ficha completa do jogador" in req.messages.first().content)
    }

    @Test fun semFichaOContextoContinuaIgual() {
        val p = pkg()
        val s = session(ficha(p))
        val user = ContextAssembler.build(ContextInput(s.state, "hero", "olho")).messages.last().content
        assertFalse("Ficha do jogador" in user)
    }

    @Test fun controladorEnviaAFichaAoMestreNaJogada() {
        val p = pkg()
        val f = ficha(p)
        val s = session(f)
        val provider = ScriptedProvider(listOf("""{"narrative":"Você golpeia."}"""))
        val c = PlayController(s, GameLoop(provider), "hero", "hp", sheet = PlayerSheet(p, f))
        assertTrue(c.submit("ataco o orc") is PlayResult.Played)
        val user = provider.requests.single().messages.last().content
        assertNotNull(user)
        // Fora de combate o controlador envia a ficha em nível BRIEF; o status detalhado
        // (incluindo Vida) só entra no início do combate ou durante uma rodada de combate.
        assertTrue("Lutador" in user && "Frágil" in user)
        assertFalse("Vida 10/10" in user)
        assertEquals(1, provider.requests.size)
    }

    // ---- níveis da ficha: completa só no início do combate ----
    @Test fun resumoForaDeCombateSoTemNomesDosTracos() {
        val p = pkg()
        val f = ficha(p)
        val text = PlayerSheet(p, f).render(session(f).state, "hero", SheetLevel.BRIEF)!!
        assertTrue("Brokk" in text && "Vantagens: Lutador" in text && "Desvantagens: Frágil" in text)
        assertFalse("Treino de combate" in text)
        assertFalse("10/10" in text)
    }

    @Test fun statusDaRodadaTemRecursosAtuaisSemDescricoes() {
        val p = pkg()
        val f = ficha(p)
        val s = session(f)
        s.execute(Damage("hero", "hp", 4))
        val text = PlayerSheet(p, f).render(s.state, "hero", SheetLevel.ROUND)!!
        assertTrue("Vida 6/10" in text && "Lutador" in text)
        assertFalse("Treino de combate" in text)
    }

    private fun combatController(
        p: RulePackage,
        f: Ficha,
        s: CampaignSession,
        provider: ScriptedProvider,
    ) = PlayController(s, GameLoop(provider), "hero", "hp", sheet = PlayerSheet(p, f))

    private fun ok(n: String) = """{"narrative":"$n"}"""

    @Test fun fichaCompletaSoNaPrimeiraJogadaDoCombate() {
        val p = pkg()
        val f = ficha(p)
        val s = session(f)
        val provider = ScriptedProvider(listOf(ok("a"), ok("b"), ok("c")))
        val c = combatController(p, f, s, provider)
        c.submit("olho em volta") // fora de combate: resumo
        s.execute(StartCombat(listOf("hero", "orc")))
        c.submit("ataco o orc") // início do combate: ficha completa
        c.submit("ataco de novo") // rodadas seguintes: só status
        val users = provider.requests.map { it.messages.last().content }
        assertFalse("Treino de combate" in users[0])
        assertTrue("Treino de combate" in users[1] && "Corpo delicado" in users[1])
        assertFalse("Treino de combate" in users[2])
        assertTrue("Status do jogador nesta rodada" in users[2] && "Vida 10/10" in users[2])
    }

    @Test fun desfazerAPrimeiraJogadaDoCombateVoltaAMandarAFichaCompleta() {
        val p = pkg()
        val f = ficha(p)
        val s = session(f)
        val provider = ScriptedProvider(listOf(ok("a"), ok("b")))
        val c = combatController(p, f, s, provider)
        s.execute(StartCombat(listOf("hero", "orc")))
        c.submit("ataco")
        assertTrue(c.undo())
        c.submit("ataco")
        val users = provider.requests.map { it.messages.last().content }
        assertTrue("Treino de combate" in users[0] && "Treino de combate" in users[1])
    }
}
