package dev.rpgsolo.rules

import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-23: Onda 2 (Exploração, Comércio, Mecanismos): 5 degraus, redução de perigo, preço de comércio e receitas de Mecanismos. */
class OficioN23Test {
    private val exploracao = RulePackage.HAZARD_TREES
    private val comercio =
        listOf(
            "comerciante", "contador", "estalajadeiro", "taverneiro", "diplomata-de-corte", "mediador", "advogado",
            "organizador-de-eventos", "mestre-de-cerimonias", "escolta-profissional",
        )
    private val mecanismos = listOf("mecanico", "mecanico-de-artefatos", "inventor", "relojoeiro")

    private fun catalog() =
        SkillTreeCatalogLoader.load(
            requireNotNull(
                javaClass.classLoader?.getResourceAsStream("catalogs/sistema-proprio/habilidades-arvore.txt"),
            ).bufferedReader().readText(),
        )

    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    private fun hero(vararg skills: String) = Entity("h", "H", EntityKind.PLAYER, skillTreeLevels = skills.associateWith { 1 })

    @Test fun vinteETresProfissoesTemCincoDegraus() {
        val c = catalog()
        assertTrue(c.errors.joinToString("; "), c.valid)
        assertEquals(23, (exploracao + comercio + mecanismos).size)
        (exploracao + comercio + mecanismos).forEach { t ->
            val s = c.tree(t)
            assertEquals(t, listOf(1, 7, 13, 21, 30), s.map { it.requiredCharacterLevel })
            assertEquals(listOf(1, 2, 3, 4, 5), s.map { it.scrollRank })
            assertEquals(1, s.count { it.freeBaseSkill })
            assertTrue(s[0].freeBaseSkill)
        }
    }

    @Test fun reducaoDePerigoPorDegrauDeExploracao() {
        val p = pkg()
        assertEquals(0, p.hazardReductionPercent(hero()))
        assertEquals(0, p.hazardReductionPercent(hero("profissao-minerador-1", "profissao-minerador-2")))
        assertEquals(10, p.hazardReductionPercent(hero("profissao-guia-de-montanha-3")))
        assertEquals(20, p.hazardReductionPercent(hero("profissao-geologo-4")))
        assertEquals(30, p.hazardReductionPercent(hero("profissao-cacador-5", "profissao-geologo-3")))
        assertEquals(0, p.hazardReductionPercent(hero("profissao-comerciante-5")))
    }

    @Test fun perigoReduzidoNuncaFicaAbaixoDeUm() {
        val p = pkg()
        assertEquals(15, p.hazardDamage("leve", 100))
        assertEquals(11, p.hazardDamage("leve", 100, 30))
        assertEquals(60, p.hazardDamage("grave", 100))
        assertEquals(42, p.hazardDamage("grave", 100, 30))
        assertEquals(1, p.hazardDamage("leve", 1, 30))
        assertNull(p.hazardDamage("inexistente", 100, 30))
    }

    @Test fun precoDeComercioPorDegrau() {
        val p = pkg()
        assertEquals(0, p.tradePercent(hero()))
        assertEquals(0, p.tradePercent(hero("profissao-comerciante-1")))
        assertEquals(5, p.tradePercent(hero("profissao-contador-2")))
        assertEquals(20, p.tradePercent(hero("profissao-comerciante-5")))
        assertEquals(0, p.tradePercent(hero("profissao-mediador-5")))
        assertEquals(80, p.discountedPrice(100, 20))
        assertEquals(1, p.discountedPrice(1, 20))
        assertEquals(0, p.discountedPrice(0, 20))
        assertEquals(120, p.boostedSale(100, 20))
        assertEquals(9, p.boostedSale(9, 5))
    }

    @Test fun mecanismosTemReceitasLigadasAosDegraus() {
        val p = pkg()
        val c = catalog()
        mecanismos.forEach { t ->
            assertTrue(t, p.recipes().any { it.skillId?.startsWith("profissao-$t-") == true })
        }
        p.recipes().filter { it.id.startsWith("mec-") }.forEach { r ->
            assertNotNull(r.id, c.skill(requireNotNull(r.skillId)))
            r.outputs.keys.forEach { assertNotNull(it, p.item(it)) }
        }
        val h = hero("profissao-relojoeiro-2")
        assertTrue(p.availableRecipes(h).any { it.id == "mec-forjar-engrenagem" })
        assertTrue(p.availableRecipes(h).none { it.id == "mec-montar-relogio-de-bolso" })
    }
}
