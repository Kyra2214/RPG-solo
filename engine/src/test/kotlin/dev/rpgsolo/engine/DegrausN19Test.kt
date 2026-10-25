package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-19: degraus 1, 7, 13, 21 e 30 pelo nível do personagem; degrau 1 de classe e profissão gratuito; ação fora de combate. */
class DegrausN19Test {
    @Test fun degrausPeloNivelDoPersonagem() {
        assertEquals(listOf(1, 7, 13, 21, 30), SkillTreeProgression.TIER_LEVELS)
        assertEquals(1, SkillTreeProgression.unlockedTier(1))
        assertEquals(1, SkillTreeProgression.unlockedTier(6))
        assertEquals(2, SkillTreeProgression.unlockedTier(7))
        assertEquals(4, SkillTreeProgression.unlockedTier(21))
        assertEquals(5, SkillTreeProgression.unlockedTier(99))
        assertEquals(3, SkillTreeProgression.tierOf(13))
        assertNull(SkillTreeProgression.tierOf(15))
    }

    private fun skill(
        origin: SkillOrigin,
        free: Boolean,
    ) = SkillTreeSkill(
        id = "s",
        name = "S",
        treeId = "t",
        origin = origin,
        category = SkillCategory.UTILITY,
        unlockPointCost = if (free) 0 else 1,
        freeBaseSkill = free,
        actionKind = SkillActionKind.OUT_OF_COMBAT,
    )

    @Test fun degrauUmGratuitoDeClasseEProfissao() {
        assertTrue(skill(SkillOrigin.CLASS, true).freeBaseSkill)
        assertTrue(skill(SkillOrigin.PROFESSION, true).freeBaseSkill)
        val result = runCatching { skill(SkillOrigin.SHARED, true) }
        assertTrue(result.isFailure)
    }
}
