package dev.rpgsolo.rules

import dev.rpgsolo.engine.Companions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-32.2: tipos de corpo, 18 ataques, 12 truques e comidas do companheiro (`companheiros.txt`). */
class CompanheiroPacoteN32Test {
    private fun files(): Map<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun pkg(files: Map<String, String> = files()): RulePackage = (PackageRegistry().install(files) as InstallResult.Success).pkg

    private fun fail(line: String): String {
        val f = files().toMutableMap()
        f["companheiros.txt"] = f.getValue("companheiros.txt") + line + "\n"
        return (PackageRegistry().install(f) as InstallResult.Failure).errors.joinToString()
    }

    @Test fun pacoteEmbutidoTemCorposAtaquesTruquesEComidas() {
        val c = pkg().companions
        assertEquals(Companions.BODY_TYPES, c.bodies.map { it.id }.toSet())
        assertEquals(18, c.attacks.size)
        c.bodies.forEach { assertEquals(3, pkg().companionAttacks(it.id).size) }
        assertEquals(12, c.tricks.size)
        assertEquals(3, c.tricks.count { it.combat })
        assertTrue(c.foods.all { pkg().data.items.any { i -> i.id == it.itemId } })
    }

    @Test fun ataquesTemDanoEfeitoEChance() {
        val p = pkg()
        val investida = p.companionAttack("quad-investida")!!
        assertEquals(CompanionDamage.HIGH, investida.damage)
        assertEquals(CompanionEffectKind.CONDITION, investida.effectKind)
        assertEquals("caido", investida.effectRef)
        assertEquals(30, investida.baseChance)
        assertEquals(CompanionEffectKind.ELEMENT, p.companionAttack("aqu-jato-dagua")!!.effectKind)
        assertEquals(CompanionEffectKind.RANGED, p.companionAttack("hum-arremesso")!!.effectKind)
        assertEquals(CompanionEffectKind.NONE, p.companionAttack("quad-mordida")!!.effectKind)
        assertNull(p.companionAttack("ataque-inventado"))
    }

    @Test fun truquesSeguemOsQuatroNiveis() {
        val p = pkg()
        assertEquals(5 to 10, p.companionTrick("vir")!!.let { it.difficulty to it.minLoyalty })
        assertEquals(8 to 25, p.companionTrick("buscar")!!.let { it.difficulty to it.minLoyalty })
        assertEquals(11 to 40, p.companionTrick("esconder-se")!!.let { it.difficulty to it.minLoyalty })
        assertEquals(14 to 60, p.companionTrick("resgatar")!!.let { it.difficulty to it.minLoyalty })
        assertTrue(p.companionTrick("distrair")!!.combat)
    }

    @Test fun comidasPorTipoDeCorpo() {
        val p = pkg()
        assertEquals(5 to true, p.companionFood("quadrupede", "carne-fresca")!!.let { it.baseGain to it.preferred })
        assertEquals(2 to false, p.companionFood("aquatico", "alga-marinha")!!.let { it.baseGain to it.preferred })
        assertNotNull(p.companionFood("alado", "peixe-fresco#normal"))
        assertNull(p.companionFood("quadrupede", "alga-marinha"))
    }

    @Test fun linhasInvalidasSaoRecusadas() {
        assertTrue(fail("ataque|x|quadrupede|X|forte|-|-").contains("dano de ataque inválido"))
        assertTrue(fail("ataque|x|quadrupede|X|normal|condicao:caido|-").contains("chance de efeito"))
        assertTrue(fail("ataque|x|quadrupede|X|normal|condicao:nada|30").contains("condição inexistente"))
        assertTrue(fail("truque|x|X|21|10|nao|d").contains("fora de 1..20"))
        assertTrue(fail("comida|quadrupede|item-que-nao-existe|2|nao").contains("item inexistente"))
        assertTrue(fail("corpo|dragao|Dragões").contains("tipo de corpo desconhecido"))
        assertTrue(fail("ataque|x|quadrupede|X|normal|-|-").contains("deve ter 3 opções"))
    }
}
