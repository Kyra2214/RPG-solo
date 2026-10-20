package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.rules.InstallResult
import dev.rpgsolo.rules.PackageRegistry
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayControllerTest {
    private fun session() = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 9L, EngineConfig(deathResource = "pv"))).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
            CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(6, 6))))
        ))
    }
    private fun ctrl(s: CampaignSession, script: List<Any>, check: ((String) -> CheckSpec?) = { CheckSpec("ataque", 1, 20, 2, 10) }) =
        PlayController(s, GameLoop(ScriptedProvider(script)), "hero", "pv", check)
    private fun ok(n: String) = """{"narrative":"$n"}"""

    private fun fortuneMenu(): CheckMenu {
        val root = listOf(
            "rules/src/test/resources/packages/teste-basico",
            "../rules/src/test/resources/packages/teste-basico",
            "src/test/resources/packages/teste-basico"
        ).map(::File).first { it.exists() }
        val files = root.listFiles()!!.filter { it.name.endsWith(".txt") }
            .associate { it.name to it.readText() }.toMutableMap()
        files["testes.txt"] = files.getValue("testes.txt").lines().joinToString("\n") { line ->
            if (line.isBlank() || line.startsWith("#")) line
            else {
                val fields = line.split("|").toMutableList()
                if (fields.firstOrNull() == "ataque") {
                    while (fields.size < 16) fields += "-"
                    fields[11] = "NORMAL"
                    fields[12] = "false"
                    fields[13] = "mana"
                    fields[14] = "1"
                    fields[15] = "ADVANTAGE"
                    fields.joinToString("|")
                } else line
            }
        }
        val installed = PackageRegistry().install(files)
        assertTrue("pacote deveria instalar: $installed", installed is InstallResult.Success)
        return CheckMenu((installed as InstallResult.Success).pkg)
    }

    @Test fun jogadaMostraRolagemENarracao() {
        val c = ctrl(session(), listOf(ok("Você golpeia.")))
        assertTrue(c.submit("ataco o orc") is PlayResult.Played)
        assertEquals(listOf(Author.PLAYER, Author.ROLL, Author.MASTER), c.lines.map { it.author })
        assertTrue(c.lines[1].text.startsWith("ataque: 1d20 ["))
        assertTrue(" +2 = " in c.lines[1].text)
    }

    @Test fun vantagemMostraAlternativaDescartadaEResultadoEscolhido() {
        val c = ctrl(
            session(),
            listOf(ok("Você consegue passar.")),
            check = {
                CheckSpec("furtividade", 1, 20, 2, 10, fortuneMode = dev.rpgsolo.engine.FortuneMode.ADVANTAGE)
            }
        )

        assertTrue(c.submit("passo escondido") is PlayResult.Played)
        assertEquals(listOf(Author.PLAYER, Author.ROLL, Author.ROLL, Author.MASTER), c.lines.map { it.author })
        assertTrue(c.lines[1].text.startsWith("Não escolhido · "))
        assertTrue(c.lines[2].text.startsWith("furtividade:"))
        assertEquals(2, c.lines.count { it.roll != null })
    }

    @Test fun jogadorPodeEscolherGastarRecursoParaVantagemNoTestePendente() {
        val s = CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("paid-fortune", "Teste", 42L, EngineConfig(deathResource = "hp"))
        )
        s.execute(CreateEntity(Entity(
            id = "hero", name = "Herói", kind = EntityKind.PLAYER,
            stats = mapOf("forca" to 2, "agilidade" to 1, "mente" to 1),
            resources = mapOf("hp" to Resource(10, 10), "mana" to Resource(2, 2))
        )))
        val provider = ScriptedProvider(listOf(
            """{"narrative":"A fechadura resiste.","request_check":{"check":"ataque","difficulty":"media","onSuccess":"abre","onFailure":"falha"}}""",
            ok("A fechadura cede.")
        ))
        val c = PlayController(
            s, GameLoop(provider, menu = fortuneMenu()), "hero", "hp",
            checkFor = { null }
        )

        assertTrue(c.submit("forço a fechadura") is PlayResult.Played)
        assertEquals(1, c.pendingFortuneSpendOption()!!.cost)
        assertEquals("mana", c.pendingFortuneSpendOption()!!.resource)
        assertTrue(c.rollPending(useFortuneResource = true) is PlayResult.Played)
        assertEquals(1, s.state.entities.getValue("hero").resources.getValue("mana").current)
        assertEquals(2, c.lines.count { it.author == Author.ROLL })
        assertTrue(c.lines.any { it.text.startsWith("Não escolhido · ") })
    }

    @Test fun rolagemNormalNaoGastaRecursoMesmoQuandoExisteOpcaoPaga() {
        val s = CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("normal-fortune", "Teste", 43L, EngineConfig(deathResource = "hp"))
        )
        s.execute(CreateEntity(Entity(
            id = "hero", name = "Herói", kind = EntityKind.PLAYER,
            stats = mapOf("forca" to 2, "agilidade" to 1, "mente" to 1),
            resources = mapOf("hp" to Resource(10, 10), "mana" to Resource(2, 2))
        )))
        val provider = ScriptedProvider(listOf(
            """{"narrative":"A fechadura resiste.","request_check":{"check":"ataque","difficulty":"media","onSuccess":"abre","onFailure":"falha"}}""",
            ok("A fechadura resiste.")
        ))
        val c = PlayController(
            s, GameLoop(provider, menu = fortuneMenu()), "hero", "hp",
            checkFor = { null }
        )

        assertTrue(c.submit("forço a fechadura") is PlayResult.Played)
        assertTrue(c.pendingFortuneSpendOption() != null)
        assertTrue(c.rollPending() is PlayResult.Played)
        assertEquals(2, s.state.entities.getValue("hero").resources.getValue("mana").current)
        assertEquals(1, c.lines.count { it.author == Author.ROLL })
    }

    @Test fun linhaDeRolagemTrazOsDadosReaisDoMotorParaAnimar() {
        val c = ctrl(session(), listOf(ok("Você golpeia.")))
        c.submit("ataco o orc")
        val v = c.lines[1].roll!!
        assertEquals("ataque", v.label)
        assertEquals(20, v.sides)
        assertEquals(1, v.dice.size)
        assertTrue(v.dice.single() in 1..20)
        assertEquals(2, v.modifier)
        assertEquals(v.dice.sum() + 2, v.total)
        assertEquals(10, v.difficulty)
        assertEquals(v.total - v.difficulty, v.margin)
        assertTrue("[${v.dice.single()}]" in c.lines[1].text)
        assertNull(c.lines[0].roll)
        assertNull(c.lines[2].roll)
    }

    @Test fun rolagemReveladaFicaNoControlador() {
        val c = ctrl(session(), listOf(ok("Você golpeia.")))
        c.submit("ataco o orc")
        assertFalse(c.isRollRevealed(1))
        assertTrue(c.revealRoll(1))
        assertTrue(c.isRollRevealed(1))
        assertFalse(c.revealRoll(1))
        assertTrue(c.undo())
        assertFalse(c.isRollRevealed(1))
    }

    @Test fun semTesteNaoHaLinhaDeRolagem() {
        val c = ctrl(session(), listOf(ok("Nada acontece.")), check = { null })
        c.submit("olho em volta")
        assertFalse(c.lines.any { it.author == Author.ROLL })
    }

    @Test fun painelSoMostraOPersonagemDoJogador() {
        val p = ctrl(session(), emptyList()).panel()!!
        assertEquals("Herói", p.name)
        assertEquals(mapOf("pv" to (10 to 10)), p.resources)
    }

    @Test fun falhaMostraAvisoENaoGravaNada() {
        val s = session()
        val before = s.mark()
        val c = ctrl(s, listOf(AiError.InvalidKey()))
        assertTrue(c.submit("ataco") is PlayResult.Failed)
        assertEquals(Author.SYSTEM, c.lines.last().author)
        assertEquals(before, s.mark())
    }

    @Test fun desfazerLimpaOChatEOEstado() {
        val s = session()
        val c = ctrl(s, listOf(ok("a"), ok("b")))
        c.submit("um")
        c.submit("dois")
        assertTrue(c.undo())
        assertEquals(listOf("um", "a"), c.lines.filter { it.author == Author.PLAYER || it.author == Author.MASTER }.map { it.text })
        assertTrue(c.undo())
        assertTrue(c.lines.isEmpty())
        assertFalse(c.undo())
    }

    @Test fun refazTrocaSoANarracao() {
        val c = ctrl(session(), listOf(ok("primeira"), ok("segunda")))
        c.submit("ataco")
        val roll = c.lines.first { it.author == Author.ROLL }
        c.regenerate()
        assertEquals(roll, c.lines.first { it.author == Author.ROLL })
        assertEquals("segunda", c.lines.last { it.author == Author.MASTER }.text)
        assertEquals(1, c.lines.count { it.author == Author.MASTER })
    }

    @Test fun textoVazioERecusado() {
        val c = ctrl(session(), emptyList())
        assertTrue(c.submit("  ") is PlayResult.Failed)
        assertTrue(c.lines.isEmpty())
        assertNull(null)
    }
}
