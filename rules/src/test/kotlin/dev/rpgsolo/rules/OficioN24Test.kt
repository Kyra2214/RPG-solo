package dev.rpgsolo.rules

import dev.rpgsolo.engine.CraftKind
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-24: Onda 3 (Mar, Animais, Artes, Identificação): 5 degraus e receitas de identificação, falsificação, mapas e relíquias. */
class OficioN24Test {
    private val mar = listOf("pescador", "pescador-de-profundidade", "mergulhador", "navegador", "marinheiro-de-carga")
    private val animais = listOf("criador-de-animais", "tratador-de-montarias", "tratador-de-aves-mensageiras", "treinador")
    private val artes = listOf("artista", "musico", "ator", "dancarino", "tatuador")
    private val identificacao =
        listOf(
            "avaliador-de-antiguidades",
            "avaliador-de-materiais",
            "arqueologo",
            "restaurador",
            "reparador-de-reliquias",
            "tradutor",
            "criptoanalista",
        )

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

    @Test fun vinteEUmaProfissoesTemCincoDegraus() {
        val c = catalog()
        assertTrue(c.errors.joinToString("; "), c.valid)
        assertEquals(21, (mar + animais + artes + identificacao).size)
        (mar + animais + artes + identificacao).forEach { t ->
            val s = c.tree(t)
            assertEquals(t, listOf(1, 7, 13, 21, 30), s.map { it.requiredCharacterLevel })
            assertEquals(listOf(1, 2, 3, 4, 5), s.map { it.scrollRank })
            assertEquals(1, s.count { it.freeBaseSkill })
        }
    }

    @Test fun identificacaoTemReceitasLigadasAosDegraus() {
        val p = pkg()
        val c = catalog()
        identificacao.forEach { t ->
            assertTrue(t, p.recipes().any { it.skillId?.startsWith("profissao-$t-") == true })
        }
        assertTrue(p.recipes(CraftKind.IDENTIFY).size >= 5)
        p.recipes().filter { it.id.startsWith("ide-") }.forEach { r ->
            assertNotNull(r.id, c.skill(requireNotNull(r.skillId)))
            r.outputs.keys.forEach { assertNotNull(it, p.item(it)) }
        }
        val h = Entity("h", "H", EntityKind.PLAYER, skillTreeLevels = mapOf("profissao-tradutor-3" to 1))
        assertTrue(p.availableRecipes(h).any { it.id == "ide-decifrar-mapa" })
        assertTrue(p.availableRecipes(h).none { it.id == "ide-restaurar-reliquia" })
    }
}
