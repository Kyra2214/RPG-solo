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

    @Test fun classesTemSeisHabilidadesDistintivasAntesDaCulminancia() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        val starters = result.skills.filter { it.origin == SkillOrigin.CLASS && it.scrollRank != null && it.scrollRank in 1..6 }
        assertEquals(300, starters.size)
        assertEquals(starters.size, starters.map { it.name }.distinct().size)
        assertTrue(starters.filter { it.scrollRank == 1 }.all {
            it.actionKind in setOf(
                dev.rpgsolo.engine.SkillActionKind.POWER_STRIKE,
                dev.rpgsolo.engine.SkillActionKind.MULTI_HIT
            ) && it.effects.any { effect -> effect.target == "dano" }
        })
        val magicalTrees = setOf(
            "mago", "feiticeiro", "invocador", "druida", "oraculo",
            "xama", "ilusionista", "guardiao-runico", "psiquico", "tecelao-de-sonhos"
        )
        starters.filter { it.treeId in magicalTrees }.forEach {
            assertTrue("${it.id} deveria respeitar afinidade elemental", it.affinityBound)
        }
        assertTrue(starters.filter { it.scrollRank == 5 }.all {
            it.category == dev.rpgsolo.engine.SkillCategory.CONTROL &&
                it.effects.any { effect -> effect.recipient == dev.rpgsolo.engine.SkillEffectRecipient.TARGET && effect.baseValue < 0 }
        })
    }

    @Test fun habilidadesOfensivasDeClasseAmaldicoadasExecutamAtaqueReal() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        val cursedStarters = result.skills.filter {
            it.origin == SkillOrigin.CURSED_CLASS &&
                it.scrollRank == 1 &&
                it.category == dev.rpgsolo.engine.SkillCategory.OFFENSE
        }
        assertTrue("esperadas habilidades ofensivas iniciais de Classe de Malditos", cursedStarters.size >= 40)
        assertTrue("toda habilidade ofensiva inicial amaldiçoada deve executar golpe, não apenas buff", cursedStarters.all {
            it.actionKind in setOf(
                dev.rpgsolo.engine.SkillActionKind.POWER_STRIKE,
                dev.rpgsolo.engine.SkillActionKind.MULTI_HIT
            ) && it.effects.any { effect -> effect.target == "dano" }
        })
    }

    @Test fun habilidadesSupremasDeClasseSaoUnicasETipadas() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        val capstones = result.skills.filter { it.origin == SkillOrigin.CLASS && it.scrollRank == 7 }
        assertEquals(50, capstones.size)
        assertEquals(capstones.size, capstones.map { it.name }.distinct().size)
        assertTrue(capstones.none { it.name.startsWith("Técnica Suprema de") })
        assertTrue(capstones.map { it.actionKind }.toSet().containsAll(
            setOf(
                dev.rpgsolo.engine.SkillActionKind.POWER_STRIKE,
                dev.rpgsolo.engine.SkillActionKind.MULTI_HIT,
                dev.rpgsolo.engine.SkillActionKind.MODIFIER,
                dev.rpgsolo.engine.SkillActionKind.REVIVE
            )
        ))
        assertEquals(5, capstones.single { it.treeId == "espadachim" }.hitCount)
        assertEquals(dev.rpgsolo.engine.SkillActionKind.REVIVE, capstones.single { it.treeId == "sacerdote" }.actionKind)
        assertFalse(capstones.single { it.treeId == "arqueiro" }.affinityBound)
    }

    @Test fun culminanciasDaClasseDeMalditosTemNomesTematicos() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        val capstones = result.skills.filter { it.origin == SkillOrigin.CURSED_CLASS && it.scrollRank == 7 }
        assertTrue("esperadas pelo menos 40 culminâncias amaldiçoadas", capstones.size >= 40)
        assertEquals(capstones.size, capstones.map { it.name }.distinct().size)
        assertTrue("culminâncias não podem usar nome genérico de placeholder", capstones.none {
            it.name.startsWith("Técnica Suprema de") || it.name.contains("maldito-vampiro-") ||
                it.name.contains("maldito-lobisomem-") || it.name.contains("maldito-necromante-")
        })
        assertTrue("famílias de ataque múltiplo precisam declarar pelo menos dois golpes", capstones
            .filter { it.actionKind == dev.rpgsolo.engine.SkillActionKind.MULTI_HIT }
            .all { it.hitCount >= 2 && it.effects.any { effect -> effect.target == "dano" } })
        assertTrue("maldições de controle devem afetar o alvo e reduzir um escopo mecânico", capstones
            .filter { it.treeId.startsWith("maldito-possessao-") || it.treeId.startsWith("maldito-praga-") }
            .all { it.category == dev.rpgsolo.engine.SkillCategory.CONTROL &&
                it.effects.any { effect -> effect.recipient == dev.rpgsolo.engine.SkillEffectRecipient.TARGET && effect.baseValue < 0 } })
        assertTrue("magias amaldiçoadas devem respeitar a afinidade declarada", capstones
            .filter { it.treeId.startsWith("maldito-mago-negro-") || it.treeId.startsWith("maldito-lunar-") ||
                it.treeId.startsWith("maldito-corrupcao-elemental-") }
            .all { it.affinityBound })
    }

    @Test fun culminanciasDeProfissaoSaoUnicasENaoForcamTodoOficioACombater() {
        val result = SkillTreeCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        val capstones = result.skills.filter { it.origin == SkillOrigin.PROFESSION && it.scrollRank == 7 }
        assertEquals(103, capstones.size)
        assertEquals(capstones.size, capstones.map { it.name }.distinct().size)
        assertTrue(capstones.none { it.name.startsWith("Técnica Suprema de") })
        assertTrue("ofícios não combatentes precisam de culminâncias passivas", capstones.count { it.category == dev.rpgsolo.engine.SkillCategory.PASSIVE } >= 90)
        assertTrue("profissões de combate devem continuar usando ações ofensivas tipadas", capstones.any { it.actionKind == dev.rpgsolo.engine.SkillActionKind.POWER_STRIKE })
        val ferreiro = result.skills.filter { it.treeId == "ferreiro" }.sortedBy { it.scrollRank }
        assertEquals((1..7).toList(), ferreiro.map { it.effects.single().baseValue })
        assertEquals(1, ferreiro.map { it.effects.single().stackingGroup }.distinct().size)
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
