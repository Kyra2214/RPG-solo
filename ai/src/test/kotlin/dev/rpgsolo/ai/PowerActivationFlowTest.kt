package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
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
import org.junit.Assert.assertTrue
import org.junit.Test

/** F2.3: tradução PowerDef → PowerSpec (PowerMenu) e ativação pelo PlayController, com desfazer. */
class PowerActivationFlowTest {
    private val powers = """
        cura-rapida|Cura rápida|Fecha ferimentos.|arcano|mana|1|self|cura|2
        raio|Raio|Descarga contra um inimigo.|arcano|mana|1|inimigo|dano|arma-leve|ataque
        veneno|Veneno|Envenena um inimigo.|arcano|mana|1|inimigo|condicao|envenenado
        purgar|Purgar|Remove veneno.|arcano|mana|0|self|remover-condicao|envenenado
    """.trimIndent()

    private fun pkg(): dev.rpgsolo.rules.RulePackage {
        val root = listOf(
            "rules/src/test/resources/packages/teste-basico",
            "../rules/src/test/resources/packages/teste-basico",
            "src/test/resources/packages/teste-basico"
        ).map(::File).first { it.exists() }
        val files = root.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()
        files["poderes.txt"] = powers
        val installed = PackageRegistry().install(files)
        assertTrue("pacote deveria instalar: $installed", installed is InstallResult.Success)
        return (installed as InstallResult.Success).pkg
    }

    private fun session() = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 11L, EngineConfig(deathResource = "hp"))).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, stats = mapOf("forca" to 2, "mente" to 3),
                resources = mapOf("hp" to Resource(4, 10), "mana" to Resource(4, 4)))),
            CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(30, 30))))
        ))
    }

    private val traits = listOf("arcano")

    @Test fun disponiveisDependemDoTracoDaFicha() {
        val menu = PowerMenu(pkg())
        assertEquals(4, menu.available(traits).size)
        assertTrue(menu.available(emptyList()).isEmpty())
    }

    @Test fun traducaoUsaSoValoresDoPacote() {
        val s = session()
        val menu = PowerMenu(pkg())
        val raio = menu.build("raio", "hero", "orc", s.state, traits) as PowerBuild.Ready
        val strike = raio.spec.effect as dev.rpgsolo.engine.PowerEffect.Strike
        assertEquals("hp", strike.resource)
        assertEquals(1, strike.diceCount); assertEquals(4, strike.diceSides)
        assertEquals(2, strike.bonus)
        assertEquals("mana", raio.spec.resource); assertEquals(1, raio.spec.cost)
        assertEquals("ataque", raio.spec.check?.checkId)
        val cura = menu.build("cura-rapida", "hero", null, s.state, traits) as PowerBuild.Ready
        assertEquals(dev.rpgsolo.engine.PowerEffect.Cure("hp", 2), cura.spec.effect)
    }

    @Test fun pedidosInvalidosSaoRecusados() {
        val s = session()
        val menu = PowerMenu(pkg())
        assertTrue(menu.build("inexistente", "hero", null, s.state, traits) is PowerBuild.Invalid)
        assertTrue(menu.build("raio", "hero", "orc", s.state, emptyList()) is PowerBuild.Invalid)
        assertTrue(menu.build("raio", "hero", null, s.state, traits) is PowerBuild.Invalid)
        assertTrue(menu.build("raio", "hero", "hero", s.state, traits) is PowerBuild.Invalid)
    }

    @Test fun controladorAtivaMostraNoChatEDesfaz() {
        val s = session()
        val menu = PowerMenu(pkg())
        val c = PlayController(s, GameLoop(ScriptedProvider(emptyList())), "hero", "hp")
        val ready = menu.build("cura-rapida", "hero", null, s.state, traits) as PowerBuild.Ready
        val before = s.eventCount
        val r = c.activatePower(ready.spec, ready.def.name, "Herói", null)
        assertTrue(r is PlayResult.Played)
        assertEquals(6, s.state.entities.getValue("hero").resources.getValue("hp").current)
        assertEquals(3, s.state.entities.getValue("hero").resources.getValue("mana").current)
        assertTrue(c.lines.any { it.author == Author.SYSTEM && "Cura rápida" in it.text })
        assertTrue(c.undo())
        assertEquals(before, s.eventCount)
        assertEquals(4, s.state.entities.getValue("hero").resources.getValue("hp").current)
        assertTrue(c.lines.isEmpty())
    }

    @Test fun recusaDoMotorNaoAlteraEstado() {
        val s = session()
        val menu = PowerMenu(pkg())
        val c = PlayController(s, GameLoop(ScriptedProvider(emptyList())), "hero", "hp")
        val ready = menu.build("purgar", "hero", null, s.state, traits) as PowerBuild.Ready
        val before = s.eventCount
        val r = c.activatePower(ready.spec, ready.def.name, "Herói", null)
        assertTrue(r is PlayResult.Failed)
        assertEquals(before, s.eventCount)
        assertFalse(c.undo())
    }
}
