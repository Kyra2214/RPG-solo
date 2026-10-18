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

    @Test fun jogadaMostraRolagemENarracao() {
        val c = ctrl(session(), listOf(ok("Você golpeia.")))
        assertTrue(c.submit("ataco o orc") is PlayResult.Played)
        assertEquals(listOf(Author.PLAYER, Author.ROLL, Author.MASTER), c.lines.map { it.author })
        assertTrue(c.lines[1].text.startsWith("ataque: 1d20 ["))
        assertTrue(" +2 = " in c.lines[1].text)
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
