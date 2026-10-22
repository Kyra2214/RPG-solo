package dev.rpgsolo.ai

import dev.rpgsolo.engine.AddCondition
import dev.rpgsolo.engine.AddItem
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.CreateClock
import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.StartCombat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentAndLocalTest {
    private val furtar = CheckOption("furtividade", "esconder-se, passar sem ser visto", CheckSpec("furtividade", 1, 20, 3, 12))
    private val atacar = CheckOption("ataque", "atacar um inimigo", CheckSpec("ataque", 1, 20, 2, 10))
    private val options = listOf(furtar, atacar)

    private fun session() =
        CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "pv")),
        ).also {
            it.executeAll(
                listOf(
                    CreateEntity(
                        Entity(
                            "hero",
                            "Herói",
                            EntityKind.PLAYER,
                            stats = mapOf("forca" to 3),
                            resources = mapOf("pv" to Resource(10, 10)),
                        ),
                    ),
                    CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(6, 6)))),
                ),
            )
        }

    // ---- 7.1.2 classificador ----

    @Test fun escolheUmTesteDaLista() {
        val ai = ScriptedProvider(listOf("""{"check":"furtividade"}"""))
        val i = IntentClassifier(ai).classify("passo escondido pelo guarda", options)
        assertEquals(Intent.Test(furtar), i)
        // Pedido barato: JSON, sem criatividade, poucos tokens.
        val req = ai.requests.single()
        assertTrue(req.jsonMode)
        assertEquals(0.0, req.temperature!!, 0.0)
        assertTrue(req.maxTokens <= 100)
        assertTrue("furtividade: esconder-se" in req.messages.last().content)
    }

    @Test fun nullSignificaSemTeste() {
        assertEquals(
            Intent.NoCheck,
            IntentClassifier(ScriptedProvider(listOf("""{"check":null}"""))).classify("converso com o taverneiro", options),
        )
    }

    @Test fun idInventadoPelaIaViraSemTeste() {
        val i = IntentClassifier(ScriptedProvider(listOf("""{"check":"acertar-tudo"}"""))).classify("ataco", options)
        assertEquals(Intent.NoCheck, i)
    }

    @Test fun aceitaJsonDentroDeCercaDeCodigo() {
        val i = IntentClassifier(ScriptedProvider(listOf("```json\n{\"check\":\"ataque\"}\n```"))).classify("ataco", options)
        assertEquals(Intent.Test(atacar), i)
    }

    @Test fun respostaIlegivelEFalhaDeRedeSaoIndisponivel() {
        assertTrue(IntentClassifier(ScriptedProvider(listOf("não sei"))).classify("ataco", options) is Intent.Unavailable)
        assertTrue(IntentClassifier(ScriptedProvider(listOf("""{"outro":1}"""))).classify("ataco", options) is Intent.Unavailable)
        assertTrue(IntentClassifier(ScriptedProvider(listOf(AiError.NoInternet()))).classify("ataco", options) is Intent.Unavailable)
    }

    @Test fun semOpcoesOuTextoVazioNaoChamaAIA() {
        val ai = ScriptedProvider(emptyList())
        val c = IntentClassifier(ai)
        assertEquals(Intent.NoCheck, c.classify("ataco", emptyList()))
        assertEquals(Intent.NoCheck, c.classify("   ", options))
        assertTrue(ai.requests.isEmpty())
    }

    @Test fun asCheckForAvisaQuandoIndisponivelESegueSemTeste() {
        val avisos = mutableListOf<String>()
        val f = IntentClassifier(ScriptedProvider(listOf(AiError.InvalidKey()))).asCheckFor(options) { avisos += it }
        assertNull(f("ataco"))
        assertEquals(1, avisos.size)
    }

    // ---- 6.4.3 ações locais ----

    @Test fun reconheceSoFrasesCurtasEExatas() {
        assertEquals(LocalAction.SHEET, LocalActions.match("Ver ficha"))
        assertEquals(LocalAction.INVENTORY, LocalActions.match("  Inventário! "))
        assertEquals(LocalAction.REST, LocalActions.match("descansar"))
        assertNull(LocalActions.match("descanso na taverna e converso com o dono"))
        assertNull(LocalActions.match("abro a ficha do prisioneiro"))
    }

    @Test fun fichaMostraSoOPersonagemDoJogador() {
        val s = session()
        s.execute(AddCondition("hero", "cansado"))
        val r = LocalActions.run(LocalAction.SHEET, s, "hero") as LocalResult.Done
        assertTrue("Herói" in r.text && "forca 3" in r.text && "pv 10/10" in r.text && "cansado" in r.text)
        assertFalse("Orc" in r.text)
        assertFalse(r.changedState)
    }

    @Test fun inventarioListaOsItens() {
        val s = session()
        assertEquals("Inventário vazio.", (LocalActions.run(LocalAction.INVENTORY, s, "hero") as LocalResult.Done).text)
        s.execute(AddItem("hero", "corda", 2))
        assertTrue("- corda x2" in (LocalActions.run(LocalAction.INVENTORY, s, "hero") as LocalResult.Done).text)
    }

    @Test fun descansarRecuperaSoOQueOPacoteDefine() {
        val s = session()
        s.execute(Damage("hero", "pv", 4))
        val r = LocalActions.run(LocalAction.REST, s, "hero", setOf("pv")) as LocalResult.Done
        assertTrue(r.changedState)
        assertEquals(10, s.state.entities["hero"]!!.resources["pv"]!!.current)
        // Sem definição do sistema, o motor não inventa regra de descanso.
        s.execute(Damage("hero", "pv", 4))
        assertTrue(LocalActions.run(LocalAction.REST, s, "hero") is LocalResult.Refused)
        assertEquals(6, s.state.entities["hero"]!!.resources["pv"]!!.current)
    }

    @Test fun descansoAvancaRelogioERevelaPistasSemExporContagemOuNome() {
        val s = session()
        s.execute(CreateClock(
            "culto-secreto", "Culto secreto", 4,
            publicClue = "A cerimônia começa ao longe.", restTick = 2,
            midClue = "Sinos ecoam pela cidade."
        ))

        val halfway = LocalActions.run(LocalAction.REST, s, "hero", setOf("pv")) as LocalResult.Done

        assertEquals(2, s.state.clocks.getValue("culto-secreto").filled)
        assertTrue("Sinos ecoam pela cidade." in halfway.text)
        assertFalse("A cerimônia começa ao longe." in halfway.text)
        assertFalse("Culto secreto" in halfway.text || "culto-secreto" in halfway.text || "2/4" in halfway.text)

        val completed = LocalActions.run(LocalAction.REST, s, "hero", setOf("pv")) as LocalResult.Done

        assertEquals(4, s.state.clocks.getValue("culto-secreto").filled)
        assertTrue("A cerimônia começa ao longe." in completed.text)
        assertFalse("Culto secreto" in completed.text || "culto-secreto" in completed.text || "4/4" in completed.text)
    }

    @Test fun descansarEmCombateOuJaDescansadoOuMortoNaoMudaNada() {
        val s = session()
        assertEquals("Você já está descansado.", (LocalActions.run(LocalAction.REST, s, "hero", setOf("pv")) as LocalResult.Done).text)
        s.execute(Damage("hero", "pv", 2))
        s.execute(StartCombat(listOf("hero", "orc")))
        val before = s.mark()
        assertTrue(LocalActions.run(LocalAction.REST, s, "hero", setOf("pv")) is LocalResult.Refused)
        assertEquals(before, s.mark())
    }

    // ---- PlayController: local + sugestões ----

    private fun ctrl(
        s: CampaignSession,
        ai: ScriptedProvider,
    ) = PlayController(s, GameLoop(ai), "hero", "pv", { null })

    @Test fun acaoLocalNaoChamaIANemRola() {
        val ai = ScriptedProvider(emptyList())
        val s = session()
        val c = ctrl(s, ai)
        val before = s.mark()
        assertTrue(c.submit("ver ficha") is PlayResult.Played)
        assertTrue(ai.requests.isEmpty())
        assertEquals(before, s.mark())
        assertEquals(listOf(Author.PLAYER, Author.SYSTEM), c.lines.map { it.author })
    }

    @Test fun descansoLocalPodeSerDesfeito() {
        val s = session()
        s.execute(Damage("hero", "pv", 5))
        val c = ctrl(s, ScriptedProvider(emptyList()))
        c.submit("descansar")
        assertEquals(10, s.state.entities["hero"]!!.resources["pv"]!!.current)
        assertTrue(c.undo())
        assertEquals(5, s.state.entities["hero"]!!.resources["pv"]!!.current)
        assertTrue(c.lines.isEmpty())
    }

    @Test fun desfazerRespeitaAOrdemEntreLocalEMestre() {
        val s = session()
        val c = ctrl(s, ScriptedProvider(listOf("""{"narrative":"A porta range."}""")))
        c.submit("abro a porta")
        c.submit("ver inventário")
        assertTrue(c.undo()) // desfaz o inventário, não a narração
        assertEquals(listOf(Author.PLAYER, Author.MASTER), c.lines.map { it.author })
        assertTrue(c.undo())
        assertTrue(c.lines.isEmpty())
    }

    @Test fun refazDepoisDeAcaoLocalAvisaEDeixaTudoComoEstava() {
        val s = session()
        val c = ctrl(s, ScriptedProvider(listOf("""{"narrative":"A porta range."}""")))
        c.submit("abro a porta")
        c.submit("ver ficha")
        val n = c.lines.size
        assertTrue(c.regenerate() is PlayResult.Failed)
        assertEquals(n + 1, c.lines.size) // só o aviso
        assertTrue(c.lines.any { it.author == Author.MASTER })
    }

    @Test fun sugestoesVemDaNarracaoSaoLimitadasEOpcionais() {
        val ai =
            ScriptedProvider(
                listOf(
                    """{"narrative":"Um corredor escuro.","suggestions":["Acender a tocha","Escutar na porta","Voltar","Gritar","Acender a tocha",7]}""",
                    """{"narrative":"Silêncio."}""",
                ),
            )
        val c = ctrl(session(), ai)
        c.submit("entro")
        assertEquals(listOf("Acender a tocha", "Escutar na porta", "Voltar"), c.suggestions())
        c.submit("espero")
        assertTrue(c.suggestions().isEmpty())
        assertTrue(c.undo())
        assertEquals(3, c.suggestions().size)
        c.submit("ver ficha")
        assertTrue(c.suggestions().isEmpty())
    }

    @Test fun sugestoesComFormatoErradoNaoInvalidamAJogada() {
        val r = ProposalParser.parse("""{"narrative":"Ok.","suggestions":"não é lista"}""")
        assertTrue(r is ParseResult.Ok)
        assertTrue((r as ParseResult.Ok).output.suggestions.isEmpty())
    }

    @Test fun sugestaoNuncaViraEstado() {
        val s = session()
        val before = s.mark()
        val c = ctrl(s, ScriptedProvider(listOf("""{"narrative":"Ok.","suggestions":["Matar o orc"]}""")))
        c.submit("olho")
        assertEquals(before, s.mark())
        assertEquals(6, s.state.entities["orc"]!!.resources["pv"]!!.current)
    }
}
