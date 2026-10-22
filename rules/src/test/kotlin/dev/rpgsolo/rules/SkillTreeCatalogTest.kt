package dev.rpgsolo.rules

import dev.rpgsolo.engine.SkillOrigin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SkillTreeCatalogTest {
    private fun catalogText(): String =
        requireNotNull(javaClass.classLoader?.getResourceAsStream("catalogs/sistema-proprio/habilidades-arvore.txt")) {
            "catálogo de habilidades não encontrado nos recursos de teste"
        }.bufferedReader(Charsets.UTF_8).use { it.readText() }

    @Test fun catalogoCompletoCarregaSemErros() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        assertTrue(result.skills.isNotEmpty())
        assertEquals(result.skills.size, result.skills.map { it.id }.distinct().size)
    }

    @Test fun classesProfissoesEClassesAmaldicoadasTemSeteHabilidadesCada() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        val byTree = result.skills.groupBy { it.treeId }
        listOf(SkillOrigin.CLASS, SkillOrigin.PROFESSION, SkillOrigin.CURSED_CLASS).forEach { origin ->
            val trees = result.skills.filter { it.origin == origin }.map { it.treeId }.distinct()
            val minimumTrees = if (origin == SkillOrigin.CURSED_CLASS) 40 else 50
            assertTrue("quantidade insuficiente de árvores para $origin: ${trees.size}", trees.size >= minimumTrees)
            trees.forEach { treeId ->
                val skills = byTree.getValue(treeId)
                val ranked = skills.filter { it.scrollRank != null }
                assertEquals("árvore $treeId deveria ter sete habilidades com pergaminho", 7, ranked.size)
                assertEquals("árvore $treeId deveria ter as posições 1..7", (1..7).toList(), ranked.mapNotNull { it.scrollRank })
                if (origin == SkillOrigin.CURSED_CLASS) {
                    assertEquals("árvore amaldiçoada $treeId deveria ter uma habilidade básica gratuita", 1, skills.count { it.freeBaseSkill })
                } else {
                    assertEquals("árvore $treeId não deve ter habilidade básica extra", 0, skills.count { it.freeBaseSkill })
                }
            }
        }
    }

    @Test fun raçasESubraçasPossuemHabilidadeBasicaGratuita() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.valid)
        listOf(SkillOrigin.RACE, SkillOrigin.SUBRACE).forEach { origin ->
            val entries = result.skills.filter { it.origin == origin }
            assertTrue("esperadas ao menos 50 habilidades de origem $origin", entries.size >= 50)
            assertTrue("toda habilidade racial deve ser gratuita", entries.all { it.freeBaseSkill && it.unlockPointCost == 0 })
        }
    }

    @Test fun nenhumaHabilidadeDeClasseTemAutoDependencia() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.valid)
        assertFalse(result.skills.any { it.id in it.prerequisiteSkillIds })
    }
}
