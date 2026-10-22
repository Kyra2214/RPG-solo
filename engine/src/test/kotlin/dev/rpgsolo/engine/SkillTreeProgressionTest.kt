package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SkillTreeProgressionTest {
    private fun skill(
        id: String = "skill-a",
        origin: SkillOrigin = SkillOrigin.CLASS,
        category: SkillCategory = SkillCategory.PASSIVE,
        free: Boolean = false,
        unlockCost: Int = 1,
        actionCost: Int = 0,
        actionResource: String? = null
    ) = SkillTreeSkill(
        id = id,
        name = id,
        treeId = "tree-a",
        origin = origin,
        category = category,
        freeBaseSkill = free,
        unlockPointCost = unlockCost,
        actionCost = actionCost,
        actionResource = actionResource
    )

    @Test fun desbloqueioEUpgradeTemCustosSeparadosEProgressaoSemTetoArbitrario() {
        assertEquals(1, skill().unlockPointCost)
        assertEquals(1, SkillTreeProgression.upgradeCost(1))
        assertEquals(Int.MAX_VALUE, SkillTreeProgression.nextLevel(Int.MAX_VALUE - 1))
        assertTrue(runCatching { SkillTreeProgression.nextLevel(Int.MAX_VALUE) }.isFailure)
    }

    @Test fun habilidadeBasicaGratuitaSoPodeVirDeOrigemPermitida() {
        assertTrue(runCatching {
            skill(free = true, unlockCost = 1)
        }.isFailure)
        assertTrue(runCatching {
            skill(origin = SkillOrigin.PROFESSION, free = true, unlockCost = 0)
        }.isFailure)
        skill(origin = SkillOrigin.RACE, free = true, unlockCost = 0)
    }

    @Test fun custoDeAcaoExigeRecursoExplicito() {
        assertTrue(runCatching { skill(actionCost = 2) }.isFailure)
        skill(actionCost = 2, actionResource = "pm")
    }

    @Test fun efeitosEquivalentesNaoAcumulamEConservamOMaisForte() {
        val weaker = SkillEffectSpec("weak", "defesa", 2, stackingGroup = "guard")
        val stronger = SkillEffectSpec("strong", "defesa", 5, stackingGroup = "guard")
        val different = SkillEffectSpec("speed", "velocidade", 3, stackingGroup = "speed")
        val selected = SkillTreeProgression.strongestEquivalentEffects(
            listOf(weaker to 2, stronger to 5, different to 3)
        )
        assertEquals(2, selected.size)
        assertEquals(setOf(5, 3), selected.map { it.second }.toSet())
    }

    @Test fun misturaDeClassesAplicaPenalidadeSocialCrescente() {
        assertEquals(0, HybridClassRules.socialPenalty(0))
        assertEquals(0, HybridClassRules.socialPenalty(1))
        assertEquals(-1, HybridClassRules.socialPenalty(2))
        assertEquals(-2, HybridClassRules.socialPenalty(3))
        assertEquals(-3, HybridClassRules.socialPenalty(4))
        assertTrue(runCatching { HybridClassRules.socialPenalty(5) }.isFailure)
    }

    @Test fun penalidadeHibridaCobreEscoposSociaisDasPericiasECatalogo() {
        assertTrue(HybridClassRules.socialPenaltyScopes.containsAll(
            setOf(
                "persuasao", "intimidacao", "lideranca",
                "atuacao", "enganacao", "diplomacia",
                "argumentacao", "mediacao", "comerciante"
            )
        ))
        assertTrue("penalidade social não deve contaminar escopos de combate", setOf(
            "ataque", "dano", "defesa", "resistencia_dano", "velocidade", "precisao"
        ).none { it in HybridClassRules.socialPenaltyScopes })
    }

    @Test fun classesSecundariasSoContamQuandoDesbloqueadasPeloNivel() {
        val classes = listOf("guerreiro", "mago", "ladino", "clerigo")
        assertEquals(1, HybridClassRules.unlockedClassCount(classes, 1))
        assertEquals(1, HybridClassRules.unlockedClassCount(classes, 9))
        assertEquals(2, HybridClassRules.unlockedClassCount(classes, 10))
        assertEquals(3, HybridClassRules.unlockedClassCount(classes, 20))
        assertEquals(4, HybridClassRules.unlockedClassCount(classes, 30))
        assertTrue(runCatching { HybridClassRules.unlockedClassCount(classes + "druida", 40) }.isFailure)
    }

    @Test fun economiaDePergaminhosSegueTabelaDefinida() {
        assertEquals(listOf(200L, 2_000L, 10_000L, 15_000L, 20_000L, 30_000L, 50_000L), SkillTreeEconomy.scrollPricesSilver)
        assertEquals(25_000L, SkillTreeEconomy.RESET_PRICE_SILVER)
        assertEquals(100L, SkillTreeEconomy.SILVER_PER_GOLD)
    }
}
