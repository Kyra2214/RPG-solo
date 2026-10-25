package dev.rpgsolo.rules

import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.SkillCategory
import dev.rpgsolo.engine.SkillTreeModifierProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-35: o posto 6 de 48 classes virou utilidade fora de combate (passiva com escopo de teste); curandeiro e médico mantêm a cura. */
class UtilidadesClassesN35Test {
    private val result =
        SkillTreeCatalogLoader.load(
            requireNotNull(javaClass.classLoader?.getResourceAsStream("catalogs/sistema-proprio/habilidades-arvore.txt"))
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() },
        )

    @Test fun quarentaEOitoClassesTemUtilidade() {
        assertTrue(result.errors.joinToString("; "), result.valid)
        val sixth = result.skills.filter { it.origin == dev.rpgsolo.engine.SkillOrigin.CLASS && it.scrollRank == 6 }
        assertEquals(50, sixth.size)
        val utilities = sixth.filter { it.category == SkillCategory.PASSIVE }
        assertEquals(48, utilities.size)
        assertTrue(utilities.all { it.actionCost == 0 && it.effects.size == 1 && it.effects.single().target !in setOf("dano", "defesa") })
    }

    @Test fun ladinoGanhaBonusEmFechaduras() {
        val ladino6 = result.skills.single { it.id == "classe-ladino-6" }
        val hero = Entity("h", "H", EntityKind.PLAYER, skillTreeLevels = mapOf(ladino6.id to 1))
        val mods = SkillTreeModifierProvider(result.skills).modifiers(GameState(), hero, "fechaduras")
        assertEquals(1, mods.size)
        assertTrue(mods.single().value > 0)
        assertTrue(SkillTreeModifierProvider(result.skills).modifiers(GameState(), hero, "rastrear").isEmpty())
    }
}
