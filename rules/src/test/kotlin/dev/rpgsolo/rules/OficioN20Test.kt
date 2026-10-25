package dev.rpgsolo.rules

import dev.rpgsolo.engine.CraftKind
import dev.rpgsolo.engine.CraftResult
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-20: receitas de ofício no pacote (`receitas.txt`), chance por atributos e nível. */
class OficioN20Test {
    private fun files(): Map<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun pkg(files: Map<String, String> = files()): RulePackage = (PackageRegistry().install(files) as InstallResult.Success).pkg

    @Test fun pacoteEmbutidoTemReceitas() {
        val p = pkg()
        assertTrue(p.recipes().size >= 4)
        assertTrue(p.recipes(CraftKind.PREPARE).size >= 2)
        val r = p.recipe("forjar-barra-ferro")!!
        assertEquals(mapOf("minerio-ferro" to 2), r.inputs)
        assertEquals(mapOf("barra-de-ferro" to 1), r.outputs)
        assertNull(p.recipe("receita-inventada"))
    }

    @Test fun chanceUsaHabilidadeSorteEDificuldade() {
        val p = pkg()
        val hero = Entity("h", "H", EntityKind.PLAYER, stats = mapOf("habilidade" to 2, "sorte" to 3))
        val r = p.recipe("preparar-pocao-cura-menor")!!
        assertEquals(61, p.craftChance(r, hero))
        assertEquals(66, p.craftChance(r, hero, tierBonus = 1))
        assertEquals(100, p.craftChance(r, hero, guaranteed = true))
        assertNotNull(p.craftChance(r, hero))
    }

    @Test fun nivelMinimoBloqueia() {
        val f = files().toMutableMap()
        f["receitas.txt"] = "alto|Receita alta|criar|minerio-ferro:1|barra-de-ferro:1|5|0|\n"
        val p = pkg(f)
        assertNull(p.craftChance(p.recipe("alto")!!, Entity("h", "H", EntityKind.PLAYER, level = 4)))
        assertNotNull(p.craftChance(p.recipe("alto")!!, Entity("h", "H", EntityKind.PLAYER, level = 5)))
    }

    @Test fun parserRecusaReceitasInvalidas() {
        fun fail(line: String) {
            val f = files().toMutableMap()
            f["receitas.txt"] = line
            assertTrue(line, PackageParser.parse(f) is ParseResult.Fail)
        }
        fail("a|A|criar|minerio-ferro:1|item-que-nao-existe:1|1|0|")
        fail("a|A|forjar|minerio-ferro:1|barra-de-ferro:1|1|0|")
        fail("a|A|consertar||barra-de-ferro:1|1|0|")
        fail("a|A|criar|minerio-ferro:0|barra-de-ferro:1|1|0|")
        fail("a|A|criar|minerio-ferro:1|barra-de-ferro:1|1|41|")
        fail("a|A|criar|minerio-ferro:1|barra-de-ferro:1|1|0|couro-cru")
        fail("a|A|criar|minerio-ferro:1||1|0|")
        fail("a|A|criar|minerio-ferro:1|barra-de-ferro:1|1|0|\na|B|criar|minerio-ferro:1|barra-de-ferro:1|1|0|")
    }

    @Test fun executeRecipeNaSessao() {
        val p = pkg()
        val store = dev.rpgsolo.engine.InMemoryEventStore()
        val session =
            dev.rpgsolo.engine.CampaignSession.create(
                store,
                dev.rpgsolo.engine.CampaignRecord("c1", "Teste", 3L, dev.rpgsolo.engine.EngineConfig()),
            )
        session.executeAll(
            listOf(
                dev.rpgsolo.engine.CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)),
                dev.rpgsolo.engine.AddItem("hero", "minerio-ferro", 2),
            ),
        )
        val r = p.executeRecipe(session, "hero", "forjar-barra-ferro", guaranteed = true) as CraftResult.Done
        assertTrue(r.success)
        assertEquals(1, session.state.inventory["hero"]?.get("barra-de-ferro"))
        assertTrue(p.executeRecipe(session, "hero", "nao-existe") is CraftResult.Refused)
        assertTrue(p.executeRecipe(session, "hero", "forjar-barra-ferro", guaranteed = true) is CraftResult.Refused)
    }
}
