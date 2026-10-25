package dev.rpgsolo.rules

import dev.rpgsolo.engine.CraftKind
import dev.rpgsolo.engine.ElementPath
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.SkillOrigin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-21: Onda 1a (Forja e armas, Vestuário e couro): 5 degraus por profissão e receitas liberadas por degrau. */
class OficioN21Test {
    private val trees =
        listOf(
            "ferreiro", "armeiro", "armeiro-de-armaduras", "perito-em-arcos", "fabricante-de-arcos", "fabricante-de-flechas",
            "alfaiate", "tecelao", "curtidor", "sapateiro", "cordoeiro", "preparador-de-peles",
        )

    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    private fun hero(vararg skills: String) =
        Entity(
            "h",
            "H",
            EntityKind.PLAYER,
            level = 30,
            stats = mapOf("habilidade" to 2, "sorte" to 0),
            skillTreeLevels = skills.associateWith { 1 },
        )

    @Test fun cadaProfissaoDaOndaTemCincoDegraus() {
        val text =
            requireNotNull(
                javaClass.classLoader?.getResourceAsStream("catalogs/sistema-proprio/habilidades-arvore.txt"),
            ).bufferedReader().readText()
        val result = SkillTreeCatalogLoader.load(text)
        assertTrue(result.errors.joinToString("; "), result.valid)
        trees.forEach { t ->
            val s = result.tree(t)
            assertEquals(t, listOf(1, 7, 13, 21, 30), s.map { it.requiredCharacterLevel })
            assertTrue(s.all { it.origin == SkillOrigin.PROFESSION })
            assertEquals(1, s.count { it.freeBaseSkill })
        }
    }

    @Test fun cadaProfissaoTemReceitaLiberadaPeloDegrau() {
        val p = pkg()
        trees.forEach { t -> assertTrue(t, p.recipes().any { it.skillId?.startsWith("profissao-$t-") == true }) }
        p.recipes().mapNotNull { it.skillId }.forEach { assertNotNull(it, it.substringAfterLast('-').toIntOrNull()) }
    }

    @Test fun receitaExigeODegrau() {
        val p = pkg()
        val r = p.recipe("forjar-espada-curta")!!
        assertNotNull(p.recipeBlock(r, hero("profissao-ferreiro-1")))
        assertNull(p.recipeBlock(r, hero("profissao-ferreiro-1", "profissao-ferreiro-3")))
        assertTrue(p.availableRecipes(hero("profissao-ferreiro-3")).any { it.id == r.id })
        assertTrue(p.availableRecipes(hero()).none { it.id == r.id })
    }

    @Test fun degrauAltoGaranteOConserto() {
        val p = pkg()
        val r = p.recipe("consertar-espada-curta")!!
        assertEquals(CraftKind.REPAIR, r.kind)
        assertEquals(setOf("espada-curta-quebrada"), r.keepOnFailure)
        val h2 = hero("profissao-ferreiro-1", "profissao-ferreiro-2")
        assertEquals(50 + 10 + 5 * 1 - 5, p.craftChance(r, h2))
        assertEquals(100, p.craftChance(r, hero("profissao-ferreiro-2", "profissao-ferreiro-4")))
    }

    @Test fun limiteDeNivelDoDegrauNoParser() {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()
        f["receitas.txt"] = "x|X|criar|barra-de-ferro:1|espada-longa:1|1|0||profissao-ferreiro-1|\n"
        assertTrue(PackageParser.parse(f) is ParseResult.Fail)
        f["receitas.txt"] = "x|X|criar|barra-de-ferro:1|espada-longa:1|1|0||profissao-ferreiro-2|\n"
        assertTrue(PackageParser.parse(f) is ParseResult.Ok)
        f["receitas.txt"] = "x|X|criar|barra-de-ferro:1|espada-longa:1|1|0||ferreiro-2|\n"
        assertTrue(PackageParser.parse(f) is ParseResult.Fail)
    }

    @Test fun ferreiroDegrauCincoEncantaComPedra() {
        val p = pkg()
        assertNotNull(p.smithEnchantBlock(hero(), "espada-curta"))
        assertNull(p.smithEnchantBlock(hero(RulePackage.SMITH_ENCHANT_SKILL), "espada-curta"))
        assertNotNull(p.smithEnchantBlock(hero(RulePackage.SMITH_ENCHANT_SKILL), "espada-de-brasa"))
        assertEquals("pedra-fogo", p.enchantMaterial(ElementPath.FIRE))
        assertEquals("pedra-sombra", p.enchantMaterial(ElementPath.YIN))
        assertNull(p.enchantMaterial(ElementPath.NONE))
    }
}
