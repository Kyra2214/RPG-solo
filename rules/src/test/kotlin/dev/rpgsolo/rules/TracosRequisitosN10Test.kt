package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-10: requisitos de traços coerentes com os sete atributos, sem traço inalcançável. */
class TracosRequisitosN10Test {
    private fun files(): MutableMap<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(): RulePackage = (PackageRegistry().install(files()) as InstallResult.Success).pkg

    private fun closure(
        p: RulePackage,
        id: String,
        seen: MutableSet<String> = mutableSetOf(),
    ): Set<String> {
        if (!seen.add(id)) return seen
        p.trait(id)!!.requires.filterIsInstance<Requirement.HasTrait>().forEach { closure(p, it.traitId, seen) }
        return seen
    }

    @Test fun todoRequisitoDeAtributoUsaUmAtributoDoPacote() {
        val p = pkg()
        val ids = p.data.stats.map { it.id }.toSet()
        p.data.traits.forEach { t ->
            t.requires.filterIsInstance<Requirement.MinStat>().forEach {
                assertTrue("${t.id}: ${it.stat}", it.stat in ids)
            }
        }
    }

    @Test fun nenhumTracoEhInalcancavelNaCriacao() {
        val p = pkg()
        p.data.traits.forEach { t ->
            val ids = closure(p, t.id)
            val stats = mutableMapOf<String, Int>()
            ids.forEach { id ->
                p.trait(id)!!.requires.filterIsInstance<Requirement.MinStat>().forEach {
                    stats[it.stat] = maxOf(stats[it.stat] ?: 0, it.min)
                }
            }
            val r = p.validateCharacter(stats, ids.toList(), checkBalance = false)
            assertTrue("${t.id}: ${r.errors}", r.valid)
        }
    }

    @Test fun magiaEConhecimentoUsamInteligenciaEMovimentoEPrecisaoUsamAgilidadeEDestreza() {
        val p = pkg()

        fun stat(id: String) = p.trait(id)!!.requires.filterIsInstance<Requirement.MinStat>().map { it.stat }
        listOf("talento-arcano", "grimorio-vasto", "contramagia", "biblioteca-mental").forEach {
            assertTrue(it, "inteligencia" in stat(it))
            assertTrue(it, "habilidade" !in stat(it))
        }
        listOf("reflexos", "voo", "pe-leve").forEach { assertTrue(it, "agilidade" in stat(it)) }
        listOf("golpe-furtivo", "tiro-certeiro", "iaijutsu").forEach { assertTrue(it, "destreza" in stat(it)) }
        assertEquals(listOf("sorte"), stat("sorte-do-mar"))
        assertTrue("habilidade" in stat("engenhoca"))
    }

    @Test fun arquetiposAlcancamSeusTracosECardapiosComOsAtributosNovos() {
        val p = pkg()
        p.data.archetypes.forEach { a ->
            val o = p.archetypeOptions(a.id)!!
            val r = p.validateCharacter(a.stats, a.traits + o.advantages + o.disadvantages, checkBalance = false)
            assertTrue("${a.id}: ${r.errors}", r.errors.none { it.contains("exige") && it.contains(">=") })
        }
        val elfo = p.archetype("elfo")!!
        assertTrue((elfo.stats["inteligencia"] ?: 0) >= 2)
        assertTrue((elfo.stats["agilidade"] ?: 0) >= 2)
    }

    @Test fun exemploExploradorUsaAgilidadeParaOsReflexos() {
        val p = pkg()
        val ex = p.data.examples.first { it.id == "explorador" }
        assertTrue((ex.stats["agilidade"] ?: 0) >= 2)
        assertTrue(p.validateCharacter(ex.stats, ex.traits).valid)
    }
}
