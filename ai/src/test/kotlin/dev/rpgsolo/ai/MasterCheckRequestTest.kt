package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.InMemoryEventStore
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

/** O mestre pede um teste no meio da história; o jogador rola; o mestre narra conforme o resultado. */
class MasterCheckRequestTest {
    private fun pkg(): RulePackage {
        val dir = listOf("../rules/src/test/resources/packages/teste-basico", "rules/src/test/resources/packages/teste-basico")
            .map(::File).first { it.exists() }
        val files = dir.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        val r = PackageRegistry()
        assertTrue(r.install(files) is InstallResult.Success)
        return r.get("teste-basico")!!
    }

    private fun ficha(p: RulePackage): Ficha =
        (p.buildFicha("hero", "Brokk", "", mapOf("forca" to 3, "agilidade" to 1, "mente" to 0), listOf("lutador")) as FichaBuild.Created).ficha

    private fun session(f: Ficha) = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 11L, EngineConfig(deathResource = "hp"))).also {
        it.executeAll(listOf(CreateEntity(f.toEntity()), CreateEntity(Entity("orc", "Orc", EntityKind.NPC))))
    }

    private val pedido = """{"narrative":"Você caminha pela estrada. Algo parece estranho; role um teste de Percepção.",
        "request_check":{"check":"esquiva","difficulty":"facil","label":"Percepção",
        "onSuccess":"percebe o mato balançando e uma criança caída","onFailure":"segue direto pela estrada"}}"""
    private fun fim(n: String) = """{"narrative":"$n"}"""

    private fun ctrl(provider: ScriptedProvider, withMenu: Boolean = true): PlayController {
        val p = pkg(); val f = ficha(p)
        return PlayController(session(f), GameLoop(provider, menu = if (withMenu) CheckMenu(p) else null), "hero", "hp", sheet = PlayerSheet(p, f))
    }

    // ---- formato ----
    @Test fun parserLeOPedidoDeTeste() {
        val r = ProposalParser.parse(pedido) as ParseResult.Ok
        val req = r.output.checkRequest!!
        assertEquals("esquiva", req.checkId)
        assertEquals("facil", req.difficultyId)
        assertEquals("Percepção", req.label)
        assertTrue("criança" in req.onSuccess && "segue direto" in req.onFailure)
        assertNull(req.onPartial)
    }

    @Test fun disputaPedidaPelaIaRolaJogadorENpcEPassaSoODesfechoEscolhido() {
        val opposed = """{"narrative":"O guarda cruza a lança. Role para passar.",
            "request_check":{"check":"esquiva","difficulty":"facil","onSuccess":"você rompe a guarda","onFailure":"o guarda mantém a passagem",
            "opposed":{"entityId":"orc","check":"esquiva","difficulty":"facil","label":"Guarda"}}}"""
        val provider = ScriptedProvider(listOf(opposed, fim("Você continua pelo caminho.")))
        val c = ctrl(provider)
        assertTrue(c.submit("tento passar pelo guarda") is PlayResult.Played)
        assertNotNull(c.pending())
        val result = c.rollPending()
        assertTrue(result is PlayResult.Played)
        assertNull(c.pending())
        assertEquals(2, c.lines.count { it.author == Author.ROLL })
        assertTrue(c.lines.any { it.text.contains("Orc") || it.text.contains("Guarda") })
        val context = provider.requests.last().messages.last().content
        assertTrue("Resultado mecânico" in context)
        assertFalse("o guarda mantém a passagem" in context && "você rompe a guarda" in context)
    }

    @Test fun grupoPedidoPelaIaRolaTodosOsParticipantes() {
        val group = """{"narrative":"Vocês tentam passar a porta pesada juntos.",
            "request_check":{"check":"esquiva","difficulty":"facil","onSuccess":"vocês conseguem passar","onFailure":"a porta continua fechada",
            "group":{"participantIds":["player","orc"],"requiredSuccesses":1,"onSuccess":"o grupo abre a porta","onFailure":"o grupo não consegue abrir a porta"}}}"""
        val provider = ScriptedProvider(listOf(group, fim("A porta finalmente se move.")))
        val c = ctrl(provider)
        assertTrue(c.submit("ajudo o orc a abrir a porta") is PlayResult.Played)
        assertNotNull(c.pending())
        assertTrue(c.rollPending() is PlayResult.Played)
        assertNull(c.pending())
        assertEquals(2, c.lines.count { it.author == Author.ROLL })
        assertTrue(provider.requests.last().messages.last().content.contains("Resultado mecânico"))
    }

    @Test fun testeAvancadoRecusaNpcForaDaCenaQuandoAListaEstaDisponivel() {
        val p = pkg()
        val f = ficha(p)
        val s = session(f)
        val menu = CheckMenu(p)
        val raw = """{"narrative":"O guarda bloqueia a passagem.",
            "request_check":{"check":"esquiva","difficulty":"facil","onSuccess":"passa","onFailure":"não passa",
            "opposed":{"entityId":"orc","check":"esquiva","difficulty":"facil"}}}"""
        val req = (ProposalParser.parse(raw) as ParseResult.Ok).output.checkRequest!!

        assertTrue(menu.validateAdvancedRequest(req, s.state, "hero", setOf("outro-npc"))!!.contains("não está presente na cena"))
        assertNull(menu.validateAdvancedRequest(req, s.state, "hero", setOf("orc")))
    }

    @Test fun pedidoSemAsDuasNotasEInvalido() {
        val raw = """{"narrative":"x","request_check":{"check":"esquiva","onSuccess":"a"}}"""
        val r = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(r.errors.any { "onFailure" in it })
    }

    @Test fun parserLePedidoDeDisputaEGrupo() {
        val opposed = """{"narrative":"O guarda bloqueia a passagem; role para vencê-lo.",
            "request_check":{"check":"esquiva","difficulty":"facil","onSuccess":"você passa","onFailure":"o guarda impede a passagem",
            "opposed":{"entityId":"orc","check":"esquiva","difficulty":"facil","label":"Guarda"}}}"""
        val parsedOpposed = ProposalParser.parse(opposed) as ParseResult.Ok
        assertEquals("orc", parsedOpposed.output.checkRequest!!.opposed!!.entityId)
        assertEquals("esquiva", parsedOpposed.output.checkRequest!!.opposed!!.checkId)

        val group = """{"narrative":"Vocês tentam erguer a porta juntos.",
            "request_check":{"check":"forca","difficulty":"facil","onSuccess":"a porta abre","onFailure":"a porta não cede",
            "group":{"participantIds":["player","orc"],"requiredSuccesses":1,"onSuccess":"a porta abre","onFailure":"a porta continua fechada"}}}"""
        val parsedGroup = ProposalParser.parse(group) as ParseResult.Ok
        assertEquals(listOf("player", "orc"), parsedGroup.output.checkRequest!!.group!!.participantIds)
        assertEquals(1, parsedGroup.output.checkRequest!!.group!!.requiredSuccesses)
    }

    @Test fun pedidoDeGrupoRejeitaIdsDuplicados() {
        val raw = """{"narrative":"x","request_check":{"check":"esquiva","onSuccess":"a","onFailure":"b",
            "group":{"participantIds":["player","orc","orc"],"requiredSuccesses":1,"onSuccess":"a","onFailure":"b"}}}"""
        val parsed = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(parsed.errors.any { "não pode repetir participantes" in it })
    }

    @Test fun pedidoDeGrupoExigePersonagemDoJogador() {
        val raw = """{"narrative":"x","request_check":{"check":"esquiva","onSuccess":"a","onFailure":"b",
            "group":{"participantIds":["orc","guard"],"requiredSuccesses":1,"onSuccess":"a","onFailure":"b"}}}"""
        val parsed = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(parsed.errors.any { "deve incluir 'player'" in it })
    }

    @Test fun pedidoDeGrupoRejeitaLimiarImpossivel() {
        val raw = """{"narrative":"x","request_check":{"check":"esquiva","onSuccess":"a","onFailure":"b",
            "group":{"participantIds":["player","orc"],"requiredSuccesses":3,"onSuccess":"a","onFailure":"b"}}}"""
        val parsed = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(parsed.errors.any { "entre 1 e o número de participantes" in it })
    }

    @Test fun pedidoAvancadoNaoPodeMisturarDisputaEGrupo() {
        val raw = """{"narrative":"x","request_check":{"check":"esquiva","onSuccess":"a","onFailure":"b",
            "opposed":{"entityId":"orc","check":"esquiva"},
            "group":{"participantIds":["player","orc"],"requiredSuccesses":1,"onSuccess":"a","onFailure":"b"}}}"""
        val parsed = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(parsed.errors.any { "opposed ou group" in it })
    }

    @Test fun semRequestCheckNadaMudaNoParser() {
        assertNull((ProposalParser.parse(fim("ok")) as ParseResult.Ok).output.checkRequest)
    }

    // ---- fluxo em duas etapas ----
    @Test fun mestrePedeTesteEJogoEsperaOJogadorRolar() {
        val provider = ScriptedProvider(listOf(pedido))
        val c = ctrl(provider)
        assertTrue(c.submit("caminho pela estrada em direção à cidade") is PlayResult.Played)
        assertEquals("Percepção", c.pending()!!.label)
        // narra até o teste, sem rolar nada e sem sugestões
        assertEquals(listOf(Author.PLAYER, Author.MASTER), c.lines.map { it.author })
        assertTrue(c.suggestions().isEmpty())
        // enquanto o teste está pendente, não dá para seguir com outra ação
        assertTrue(c.submit("ando mais rápido") is PlayResult.Failed)
        assertEquals(1, provider.requests.size)
        // o menu de testes foi oferecido ao mestre
        val first = provider.requests[0].messages.last().content
        assertTrue("Testes que você pode pedir" in first && "esquiva" in first && "facil" in first)
    }

    @Test fun aoRolarOMestreSoRecebeANotaDoResultado() {
        val provider = ScriptedProvider(listOf(pedido, fim("E a história segue.")))
        val c = ctrl(provider)
        c.submit("caminho pela estrada")
        assertTrue(c.rollPending() is PlayResult.Played)
        assertNull(c.pending())
        assertEquals(listOf(Author.PLAYER, Author.MASTER, Author.ROLL, Author.MASTER), c.lines.map { it.author })
        val roll = c.lines.first { it.author == Author.ROLL }.roll!!
        assertEquals("Percepção", roll.label)
        val user = provider.requests[1].messages.last().content
        assertTrue("caminho pela estrada" in user)
        if (roll.outcome.isSuccess) {
            assertTrue("criança caída" in user)
            assertFalse("segue direto" in user)
        } else {
            assertTrue("segue direto" in user)
            assertFalse("criança caída" in user)   // o que estava escondido não vaza numa falha
        }
    }

    @Test fun desfazerVoltaAoTesteDepoisAoInicio() {
        val c = ctrl(ScriptedProvider(listOf(pedido, fim("fim"))))
        c.submit("caminho")
        c.rollPending()
        assertTrue(c.undo())                       // desfaz a rolagem e o desfecho
        assertNotNull(c.pending())
        assertEquals(listOf(Author.PLAYER, Author.MASTER), c.lines.map { it.author })
        assertTrue(c.undo())                       // desfaz o pedido
        assertNull(c.pending())
        assertTrue(c.lines.isEmpty())
    }

    @Test fun refazerONarracaoMantemARolagem() {
        val c = ctrl(ScriptedProvider(listOf(pedido, fim("um"), fim("dois"))))
        c.submit("caminho")
        c.rollPending()
        val roll = c.lines.first { it.author == Author.ROLL }
        c.regenerate()
        assertEquals(roll, c.lines.first { it.author == Author.ROLL })
        assertEquals("dois", c.lines.last { it.author == Author.MASTER }.text)
    }

    // ---- validação ----
    @Test fun testeInexistenteVoltaParaIaCorrigir() {
        val ruim = pedido.replace("\"esquiva\"", "\"percepcao\"")
        val provider = ScriptedProvider(listOf(ruim, pedido))
        val c = ctrl(provider)
        assertTrue(c.submit("caminho") is PlayResult.Played)
        assertEquals(2, provider.requests.size)
        assertTrue("percepcao" in provider.requests[1].messages.last().content)
        assertNotNull(c.pending())
    }

    @Test fun pedidoComProposalsEhRecusado() {
        val ruim = """{"narrative":"x","proposals":[{"type":"damage","entityId":"hero","resource":"hp","amount":1}],
            "request_check":{"check":"esquiva","onSuccess":"a","onFailure":"b"}}"""
        val provider = ScriptedProvider(listOf(ruim, pedido))
        val c = ctrl(provider)
        assertTrue(c.submit("caminho") is PlayResult.Played)
        assertTrue("proposals" in provider.requests[1].messages.last().content)
    }

    @Test fun semMenuOMestreNaoPodePedirTeste() {
        val provider = ScriptedProvider(listOf(pedido, pedido))
        val c = ctrl(provider, withMenu = false)
        assertTrue(c.submit("caminho") is PlayResult.Failed)
        assertNull(c.pending())
        assertFalse("Testes que você pode pedir" in provider.requests[0].messages.last().content)
    }

    // ---- rolagem com tabela: teste de esquiva -> d6 de dano ----
    private fun tabela(maxDamage: Int = 6) = """{"narrative":"A flecha acerta! Role um d6: quanto maior, pior.",
        "request_roll":{"sides":6,"label":"Dano da flecha","table":{
        "1":{"text":"raspa no ombro","damage":1},"2":{"text":"corta o braço","damage":2},
        "3":{"text":"fura a coxa","damage":3},"4":{"text":"atinge as costelas","damage":4},
        "5":{"text":"perfura o peito","damage":5},"6":{"text":"atinge perto do coração","damage":$maxDamage}}}}"""
    private val textos = mapOf(
        1 to "raspa no ombro", 2 to "corta o braço", 3 to "fura a coxa",
        4 to "atinge as costelas", 5 to "perfura o peito", 6 to "atinge perto do coração"
    )
    private fun hp(c: PlayController) = c.panel()!!.resources.getValue("hp")

    @Test fun parserRecusaTabelaComFaceFaltando() {
        val raw = """{"narrative":"x","request_roll":{"sides":6,"table":{"1":{"text":"a"},"2":{"text":"b"}}}}"""
        val r = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(r.errors.any { "cada face" in it })
    }

    @Test fun naoPodePedirTesteERolagemJuntos() {
        val raw = """{"narrative":"x",
            "request_check":{"check":"esquiva","onSuccess":"a","onFailure":"b"},
            "request_roll":{"sides":2,"table":{"1":{"text":"a"},"2":{"text":"b"}}}}"""
        val r = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(r.errors.any { "um por vez" in it })
    }

    @Test fun esquivaFalhaDepoisD6DeDanoAplicadoPeloMotor() {
        val provider = ScriptedProvider(listOf(pedido, tabela(), fim("O ferimento arde.")))
        val c = ctrl(provider)
        c.submit("ando pelo corredor escuro")
        assertEquals(10 to 10, hp(c))
        c.rollPending()                               // teste de esquiva; o mestre responde pedindo o d6
        assertEquals("Dano da flecha (d6)", c.pending()!!.label)
        assertEquals(10 to 10, hp(c))                 // nada de dano antes de o jogador rolar o d6
        c.rollPending()                               // d6: o motor aplica o dano da face
        assertNull(c.pending())

        val roll = c.lines.last { it.author == Author.ROLL }.roll!!
        assertTrue(roll.plain)
        assertEquals(6, roll.sides)
        val face = roll.dice.single()
        assertEquals(10 - face to 10, hp(c))           // nesta tabela o dano é igual à face

        // o mestre recebe só a nota da face sorteada e a informação de que o dano já foi aplicado
        val user = provider.requests[2].messages.last().content
        assertTrue(textos.getValue(face) in user)
        assertTrue(textos.filterKeys { it != face }.values.none { it in user })
        assertTrue("JÁ aplicou o dano" in user)
    }

    @Test fun desfazerOD6DevolveAVidaEOPedido() {
        val c = ctrl(ScriptedProvider(listOf(pedido, tabela(), fim("fim"))))
        c.submit("ando")
        c.rollPending()
        c.rollPending()
        assertTrue(c.undo())
        assertEquals(10 to 10, hp(c))
        assertEquals("Dano da flecha (d6)", c.pending()!!.label)
    }

    @Test fun danoAcimaDaVidaMaximaVoltaParaIaCorrigir() {
        val provider = ScriptedProvider(listOf(pedido, tabela(maxDamage = 99), tabela(), fim("fim")))
        val c = ctrl(provider)
        c.submit("ando")
        c.rollPending()                                // 2ª tentativa do mestre (99 foi recusado)
        assertTrue("vida máxima" in provider.requests[2].messages.last().content)
        assertNotNull(c.pending())
    }

    @Test fun encadeamentoDePedidosTemLimite() {
        // pedido -> d6 -> novo teste (permitido) -> o mestre tenta pedir de novo e é barrado
        val provider = ScriptedProvider(listOf(pedido, tabela(), pedido, pedido, fim("segue")))
        val c = ctrl(provider)
        c.submit("ando")
        c.rollPending()
        c.rollPending()
        assertNotNull(c.pending())
        assertTrue(c.rollPending() is PlayResult.Played)
        assertNull(c.pending())
        assertTrue("demais" in provider.requests.last().messages.last().content)
    }

    // ---- status (condições) aplicados por uma face do dado ----
    private fun tabelaComStatus(condicao: String = "sangramento") = """{"narrative":"A flecha acerta! Role um d6: quanto maior, pior.",
        "request_roll":{"sides":6,"label":"Dano da flecha","table":{
        "1":{"text":"raspa no ombro","damage":1,"conditions":["$condicao"]},"2":{"text":"corta o braço","damage":2,"conditions":["$condicao"]},
        "3":{"text":"fura a coxa","damage":3,"conditions":["$condicao"]},"4":{"text":"atinge as costelas","damage":4,"conditions":["$condicao"]},
        "5":{"text":"perfura o peito","damage":5,"conditions":["$condicao"]},"6":{"text":"atinge perto do coração","damage":6,"conditions":["$condicao"]}}}}"""

    @Test fun parserLeAsCondicoesDeCadaFace() {
        val r = ProposalParser.parse(tabelaComStatus()) as ParseResult.Ok
        assertEquals(listOf("sangramento"), r.output.rollRequest!!.table.getValue(3).conditions)
        val semStatus = ProposalParser.parse(tabela()) as ParseResult.Ok
        assertTrue(semStatus.output.rollRequest!!.table.values.all { it.conditions.isEmpty() })
    }

    @Test fun condicoesForaDeListaDeTextoSaoRecusadas() {
        val raw = """{"narrative":"x","request_roll":{"sides":2,"table":{"1":{"text":"a","conditions":"sangramento"},"2":{"text":"b"}}}}"""
        val r = ProposalParser.parse(raw) as ParseResult.Invalid
        assertTrue(r.errors.any { "conditions" in it })
    }

    @Test fun faceAplicaDanoEStatusPeloMotor() {
        val provider = ScriptedProvider(listOf(pedido, tabelaComStatus(), fim("O sangue escorre.")))
        val c = ctrl(provider)
        c.submit("ando pelo corredor escuro")
        c.rollPending()                                // esquiva
        assertTrue(c.panel()!!.conditions.isEmpty())   // nenhum status antes do d6
        c.rollPending()                                // d6: dano e status
        assertEquals(listOf("sangramento"), c.panel()!!.conditions)
        assertTrue("sangramento" in provider.requests[2].messages.last().content)
        assertTrue("JÁ aplicou o dano e a condição" in provider.requests[2].messages.last().content)
    }

    @Test fun desfazerOD6RemoveOStatus() {
        val c = ctrl(ScriptedProvider(listOf(pedido, tabelaComStatus(), fim("fim"))))
        c.submit("ando")
        c.rollPending()
        c.rollPending()
        assertTrue(c.undo())
        assertTrue(c.panel()!!.conditions.isEmpty())
        assertEquals(10 to 10, hp(c))
    }

    @Test fun condicaoInexistenteNoPacoteVoltaParaIaCorrigir() {
        val provider = ScriptedProvider(listOf(pedido, tabelaComStatus("fantasma"), tabelaComStatus(), fim("fim")))
        val c = ctrl(provider)
        c.submit("ando")
        c.rollPending()                                // 2ª tentativa do mestre ("fantasma" foi recusado)
        val reenvio = provider.requests[2].messages.last().content
        assertTrue("condição desconhecida" in reenvio && "sangramento" in reenvio)
        assertNotNull(c.pending())
    }

    @Test fun menuListaAsCondicoesDoPacote() {
        val d = CheckMenu(pkg()).describe()!!
        assertTrue("sangramento (Sangramento)" in d && "visao-turva" in d)
    }

    @Test fun statusDaFaceValeComoPerdaNoProximoTeste() {
        // sorte = 2d6 sem atributo; amaldiçoado (perda em tudo) deixa o teste com 1d6
        val sorte = """{"narrative":"Role a sorte.","request_check":{"check":"sorte","difficulty":"facil","label":"Sorte","onSuccess":"a","onFailure":"b"}}"""
        val provider = ScriptedProvider(listOf(pedido, tabelaComStatus("amaldicoado"), sorte, fim("fim")))
        val c = ctrl(provider)
        c.submit("ando")
        c.rollPending()                                // esquiva
        c.rollPending()                                // d6: dano + amaldiçoado
        assertEquals(listOf("amaldicoado"), c.panel()!!.conditions)
        c.rollPending()                                // sorte, já com Perda
        assertEquals(1, c.lines.last { it.author == Author.ROLL }.roll!!.dice.size)
    }

    // ---- bônus de traços ----
    @Test fun parserLeASkillDoPedidoENormaliza() {
        val raw = """{"narrative":"x","request_check":{"check":"esquiva","skill":"Esquiva Rápida","onSuccess":"a","onFailure":"b"}}"""
        assertEquals("esquiva-rapida", (ProposalParser.parse(raw) as ParseResult.Ok).output.checkRequest!!.skill)
        assertNull((ProposalParser.parse(pedido) as ParseResult.Ok).output.checkRequest!!.skill)
    }

    @Test fun vantagemSomaBonusNoTesteDoMestre() {
        val p = pkg()
        val f = (p.buildFicha("hero", "Brokk", "", mapOf("forca" to 3, "agilidade" to 1, "mente" to 0), listOf("lutador", "esquivador")) as FichaBuild.Created).ficha
        val provider = ScriptedProvider(listOf(pedido, fim("desvia")))
        val c = PlayController(session(f), GameLoop(provider, menu = CheckMenu(p), playerTraits = { f.traits }), "hero", "hp", sheet = PlayerSheet(p, f))
        c.submit("ando")
        c.rollPending()
        val roll = c.lines.last { it.author == Author.ROLL }.roll!!
        val base = p.check("esquiva", mapOf("agilidade" to 1), 0).modifier
        assertEquals(base + 2, roll.modifier)      // o pedido não declara skill, mas o escopo "esquiva" é o id do teste
        assertTrue(roll.label.contains("+2"))
    }

    @Test fun semOTracoNaoHaBonus() {
        val p = pkg(); val f = ficha(p)
        val provider = ScriptedProvider(listOf(pedido, fim("x")))
        val c = PlayController(session(f), GameLoop(provider, menu = CheckMenu(p), playerTraits = { f.traits }), "hero", "hp", sheet = PlayerSheet(p, f))
        c.submit("ando"); c.rollPending()
        val roll = c.lines.last { it.author == Author.ROLL }.roll!!
        assertEquals(p.check("esquiva", mapOf("agilidade" to 1), 0).modifier, roll.modifier)
    }

    @Test fun fichaMostraOBonusAoMestre() {
        val p = pkg()
        val f = (p.buildFicha("hero", "Brokk", "", mapOf("forca" to 3, "agilidade" to 1, "mente" to 0), listOf("lutador", "esquivador")) as FichaBuild.Created).ficha
        assertTrue("+2 em esquiva" in PlayerSheet(p, f).render(session(f).state, "hero")!!)
    }

    @Test fun controllerExibeDisputaOpostaEPermiteDesfazer() {
        val p = pkg()
        val f = ficha(p)
        val c = PlayController(session(f), GameLoop(ScriptedProvider(emptyList()), menu = CheckMenu(p)), "hero", "hp")
        val spec = p.check("esquiva", mapOf("agilidade" to 1), 0)
        assertTrue(c.resolveOpposed(spec.copy(label = "Herói"), spec.copy(label = "Guarda")) is PlayResult.Played)
        assertEquals(2, c.lines.count { it.author == Author.ROLL })
        assertTrue(c.undo())
        assertTrue(c.lines.isEmpty())
    }


    @Test fun controllerExibeTesteDeGrupoEPermiteDesfazer() {
        val p = pkg()
        val f = ficha(p)
        val c = PlayController(session(f), GameLoop(ScriptedProvider(emptyList()), menu = CheckMenu(p)), "hero", "hp")
        val spec = p.check("esquiva", mapOf("agilidade" to 1), 0)
        assertTrue(c.resolveGroup(
            listOf(spec.copy(label = "Batedor"), spec.copy(label = "Arqueiro")),
            requiredSuccesses = 1,
            participantNames = listOf("Batedor", "Arqueiro")
        ) is PlayResult.Played)
        assertEquals(2, c.lines.count { it.author == Author.ROLL })
        assertTrue(c.lines.first().text.contains("Teste de grupo"))
        assertTrue(c.undo())
        assertTrue(c.lines.isEmpty())
    }

}
