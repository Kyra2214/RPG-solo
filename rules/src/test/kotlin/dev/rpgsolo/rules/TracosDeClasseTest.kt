package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Pares de classe do sistema próprio: toda vantagem de classe exige a sua desvantagem equivalente. */
class TracosDeClasseTest {
    private fun files(): Map<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun pkg(): RulePackage = (PackageRegistry().install(files()) as InstallResult.Success).pkg

    /** Vantagens que exigem uma desvantagem: id da vantagem -> id da desvantagem exigida. */
    private fun pares(p: RulePackage): Map<String, String> =
        p.data.traits.filter { it.type == "vantagem" }.mapNotNull { v ->
            val d = v.requires.filterIsInstance<Requirement.HasTrait>().map { it.traitId }
                .firstOrNull { p.trait(it)?.type == "desvantagem" }
            d?.let { v.id to it }
        }.toMap()

    @Test fun temPeloMenosCinquentaParesDeClasse() {
        assertTrue(pares(pkg()).size >= 50)
    }

    @Test fun cadaDesvantagemDeParTemUmaUnicaVantagemDona() {
        val ds = pares(pkg()).values
        assertEquals(ds.size, ds.toSet().size)
    }

    @Test fun barbaroEPaladinoExistemComSuasDesvantagens() {
        val p = pkg()
        val par = pares(p)
        assertEquals("nao-foge-de-luta", par["furia-guerreira"])
        assertEquals("juramento-sagrado", par["bencao-da-fe"])
        assertTrue(p.trait("furia-guerreira")!!.description.contains("+1d6"))
        assertTrue(p.trait("juramento-sagrado")!!.description.contains("maldade"))
    }

    @Test fun vantagemSemASuaDesvantagemEhRecusada() {
        val p = pkg()
        val stats = mapOf("poder" to 3, "habilidade" to 1, "resistencia" to 2)
        val sem = p.validateCharacter(stats, listOf("furia-guerreira"))
        assertFalse(sem.valid)
        assertTrue(sem.errors.any { it.contains("exige o traço Não foge de luta") })
        assertTrue(p.validateCharacter(stats, listOf("furia-guerreira", "nao-foge-de-luta")).valid)
    }

    @Test fun vantagemDeClasseRespeitaOAtributoMinimo() {
        val p = pkg()
        val fraco = mapOf("poder" to 1, "habilidade" to 1, "resistencia" to 1)
        val r = p.validateCharacter(fraco, listOf("furia-guerreira", "nao-foge-de-luta"))
        assertTrue(r.errors.any { it.contains("exige Poder >= 2") })
    }

    @Test fun vantagensEDesvantagensNaoCustamPontos_soAtributosEPericias() {
        val p = pkg()
        val stats = mapOf("poder" to 3, "habilidade" to 3, "resistencia" to 3)
        // custo liquido do par = 0: so os 9 pontos de atributo contam
        val um = p.validateCharacter(stats, listOf("furia-guerreira", "nao-foge-de-luta"))
        assertEquals(9, um.pointsSpent)
        // pericias continuam custando 1 ponto cada
        val comPericia = p.validateCharacter(stats, listOf("furia-guerreira", "nao-foge-de-luta", "atletismo"))
        assertEquals(10, comPericia.pointsSpent)
    }

    @Test fun classesPedidasTemArquetipoQueUsaOPar() {
        val p = pkg()
        val barbaro = p.archetype("barbaro")!!
        assertTrue(barbaro.traits.containsAll(listOf("furia-guerreira", "nao-foge-de-luta")))
        val paladino = p.archetype("paladino")!!
        assertTrue(paladino.traits.containsAll(listOf("bencao-da-fe", "juramento-sagrado")))
        assertTrue(p.data.archetypes.size >= 70)
    }
}
