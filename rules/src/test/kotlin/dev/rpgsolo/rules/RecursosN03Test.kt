package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-03: PV = 5 × Resistência (mín. 1), PM = 2 × Inteligência, PA = 1 + Habilidade; `pv` ligado à morte e às condições. */
class RecursosN03Test {
    private fun files(): MutableMap<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(f: Map<String, String> = files()): RulePackage = (PackageRegistry().install(f) as InstallResult.Success).pkg

    private fun max(
        p: RulePackage,
        stats: Map<String, Int>,
        id: String,
    ): Int = p.resourcesFor(stats).getValue(id).max

    @Test fun pvCincoPorResistenciaMinimoUm() {
        val p = pkg()
        assertEquals(1, max(p, emptyMap(), "pv"))
        assertEquals(5, max(p, mapOf("resistencia" to 1), "pv"))
        assertEquals(15, max(p, mapOf("resistencia" to 3), "pv"))
    }

    @Test fun pmVemDaInteligenciaEPaDaHabilidade() {
        val p = pkg()
        assertEquals(0, max(p, mapOf("habilidade" to 5, "poder" to 5), "pm"))
        assertEquals(6, max(p, mapOf("inteligencia" to 3), "pm"))
        assertEquals(1, max(p, mapOf("poder" to 9), "pa"))
        assertEquals(4, max(p, mapOf("habilidade" to 3), "pa"))
    }

    @Test fun mortePvECondicoesUsamOMesmoRecurso() {
        val p = pkg()
        assertEquals("pv", p.manifest.deathResource)
        p.data.conditions.forEach { assertTrue(it.id, p.data.resources.any { r -> r.id == it.damageResource }) }
    }

    @Test fun condicaoSemRecursoUsaORecursoDeMorteDoManifesto() {
        val f = files()
        f["condicoes.txt"] = f.getValue("condicoes.txt") + "\nsangrando-n03|Sangrando N03|teste||2|1\n"
        val c = pkg(f).data.conditions.first { it.id == "sangrando-n03" }
        assertEquals("pv", c.damageResource)
    }

    @Test fun minimoNegativoERecusado() {
        val f = files()
        f["recursos.txt"] = "pv|Vida|0|resistencia|5|-1\n"
        assertTrue(PackageRegistry().install(f) !is InstallResult.Success)
    }
}

/** N-08: condições do sistema próprio em % do máximo de PV. */
class CondicoesPercentuaisPacoteTest {
    private fun pkg(): RulePackage =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
            .let { (PackageRegistry().install(it) as InstallResult.Success).pkg }

    @Test fun sangramentoEnvenenadoEQueimandoSaoPercentuais() {
        val c = pkg().data.conditions.associateBy { it.id }
        listOf("sangramento" to (10 to 3), "envenenado" to (5 to 3), "queimando" to (15 to 2)).forEach { (id, v) ->
            assertEquals(id, v.first, c.getValue(id).damagePercent)
            assertEquals(id, 0, c.getValue(id).damagePerRound)
            assertEquals(id, v.second, c.getValue(id).durationRounds)
            assertEquals(id, "pv", c.getValue(id).damageResource)
        }
    }
}
