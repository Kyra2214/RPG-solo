package dev.rpgsolo.rules

import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.SkillActionKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-22: Onda 1b (Preparos, Cuidados, Furtividade): 5 degraus, cura fora de combate, receitas de preparo e afanar. */
class OficioN22Test {
    private val preparos =
        listOf("alquimista-de-oficina", "herbalista", "cozinheiro", "padeiro", "cervejeiro", "especialista-em-toxinas", "perfumista")
    private val cuidados = listOf("curandeiro-comunitario", "enfermeiro", "cirurgiao")
    private val furtividade = listOf("espiao", "mensageiro", "perito-em-armadilhas", "detetive", "cacador-de-recompensas", "carcereiro")

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

    @Test fun dezesseisProfissoesTemCincoDegraus() {
        val c = catalog()
        assertTrue(c.errors.joinToString("; "), c.valid)
        (preparos + cuidados + furtividade).forEach { t ->
            val s = c.tree(t)
            assertEquals(t, listOf(1, 7, 13, 21, 30), s.map { it.requiredCharacterLevel })
            assertEquals(listOf(1, 2, 3, 4, 5), s.map { it.scrollRank })
            assertEquals(1, s.count { it.freeBaseSkill })
        }
    }

    @Test fun cuidadosCuramForaDeCombate() {
        val c = catalog()
        cuidados.forEach { t ->
            val s = c.tree(t)
            assertEquals(SkillActionKind.OUT_OF_COMBAT, s[0].actionKind)
            assertTrue(s[0].freeBaseSkill)
            assertEquals(listOf(true, true, false, true, true), s.map { it.actionKind == SkillActionKind.OUT_OF_COMBAT })
            val heals = s.filter { it.actionKind == SkillActionKind.OUT_OF_COMBAT }.map { it.effects.single().baseValue }
            assertEquals(heals.sorted(), heals)
            assertTrue(
                s.filter {
                    it.actionKind == SkillActionKind.OUT_OF_COMBAT
                }.all { it.effects.single().target == "cura" && it.actionResource == "pm" },
            )
        }
    }

    @Test fun acaoForaDeCombateExigeCuraECusto() {
        val ok = "s|t|Skill|PROFESSION|SUPPORT|-|-|false|-|1|-|1|0|true|OUT_OF_COMBAT|1|pm|1|SELF|fx,cura,3,1,g,-,-"
        assertTrue(SkillTreeCatalogLoader.load(ok).valid)
        assertFalse(SkillTreeCatalogLoader.load(ok.replace("fx,cura", "fx,dano")).valid)
        assertFalse(SkillTreeCatalogLoader.load(ok.replace("|1|pm|", "|0|-|")).valid)
        assertFalse(SkillTreeCatalogLoader.load(ok.replace("SELF|fx,cura,3,1,g,-,-", "SELF|-")).valid)
    }

    @Test fun preparosECuidadosTemReceitaPorDegrau() {
        val p = pkg()
        (preparos + listOf("curandeiro-comunitario", "enfermeiro", "cirurgiao")).forEach { t ->
            assertTrue(t, p.recipes(dev.rpgsolo.engine.CraftKind.PREPARE).any { it.skillId?.startsWith("profissao-$t-") == true })
        }
    }

    @Test fun afanarSoParaQuemSabe() {
        val p = pkg()

        fun e(vararg s: String) = Entity("h", "H", EntityKind.PLAYER, skillTreeLevels = s.associateWith { 1 })
        assertFalse(p.canPilfer(e()))
        assertFalse(p.canPilfer(e("classe-guerreiro-1")))
        assertTrue(p.canPilfer(e("classe-ladino-1")))
        assertTrue(p.canPilfer(e("profissao-espiao-1")))
        val c = catalog()
        RulePackage.PILFER_SKILLS.forEach { assertTrue(it, c.skill(it) != null) }
    }
}
