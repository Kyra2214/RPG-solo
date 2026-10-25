package dev.rpgsolo.rules

import dev.rpgsolo.engine.SkillActionKind
import dev.rpgsolo.engine.SkillCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-22 (pendência): Curandeiro e Médico de Campo curam fora de combate no posto 6. */
class CuraClassesN22Test {
    private val result =
        SkillTreeCatalogLoader.load(
            requireNotNull(javaClass.classLoader?.getResourceAsStream("catalogs/sistema-proprio/habilidades-arvore.txt"))
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() },
        )

    @Test fun postoSeisCuraForaDeCombate() {
        assertTrue(result.errors.joinToString("; "), result.valid)
        listOf("curandeiro", "medico-de-campo").forEach { id ->
            val s = result.tree(id).single { it.scrollRank == 6 }
            assertEquals(SkillActionKind.OUT_OF_COMBAT, s.actionKind)
            assertEquals(SkillCategory.SUPPORT, s.category)
            assertEquals("cura", s.effects.single().target)
            assertEquals("pm", s.actionResource)
            assertEquals(1, result.tree(id).count { it.actionKind == SkillActionKind.OUT_OF_COMBAT })
        }
    }
}
