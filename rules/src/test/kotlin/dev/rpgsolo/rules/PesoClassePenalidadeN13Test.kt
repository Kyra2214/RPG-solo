package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-13: peso de equipamento, tipo de arma, penalidade de classe e vantagens Mestre. */
class PesoClassePenalidadeN13Test {
    private fun dir(): File =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }

    private fun files(): MutableMap<String, String> =
        dir().listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(f: Map<String, String> = files()): RulePackage = (PackageRegistry().install(f) as InstallResult.Success).pkg

    private val leve = WearerProfile(setOf(ItemWeight.LEVE), setOf("cajado", "adaga"))

    @Test fun itensDeclaramPesoETipo() {
        val p = pkg()
        assertEquals(ItemWeight.PESADO, p.item("cota-de-malha")!!.weight)
        assertEquals(ItemWeight.LEVE, p.item("roupa-reforcada")!!.weight)
        assertEquals(ItemWeight.MEDIO, p.item("capacete")!!.weight)
        assertEquals("espada", p.item("espada-longa")!!.weaponType)
        assertEquals("cajado", p.item("cajado")!!.weaponType)
        assertNull(p.item("pocao-cura")!!.weight)
        assertNull(p.item("escudo-leve")!!.weaponType)
    }

    @Test fun pecaDePesoErradoVaiMetadeArredondadaParaCima() {
        val p = pkg()
        assertEquals(6, p.equipmentBonus(listOf("cota-de-malha"), "max_pv"))
        assertEquals(3, p.equipmentBonus(listOf("cota-de-malha"), "max_pv", leve))
        assertEquals(2, p.equipmentBonus(listOf("roupa-reforcada"), "max_pv"))
        assertEquals(2, p.equipmentBonus(listOf("roupa-reforcada"), "max_pv", leve))
        assertEquals(1, p.equipmentBonus(listOf("capacete"), "sorte", leve))
    }

    @Test fun metadeVemDepoisDaQualidade() {
        val p = pkg()
        assertEquals(8, p.equipmentBonus(listOf("cota-de-malha#otima"), "max_pv"))
        assertEquals(4, p.equipmentBonus(listOf("cota-de-malha#otima"), "max_pv", leve))
    }

    @Test fun penalidadesDaPecaContinuamInteiras() {
        val p = pkg()
        assertEquals(-1, p.equipmentBonus(listOf("cota-de-malha"), "ataque", leve))
        assertEquals(-1, p.equipmentBonus(listOf("machado"), "ataque", leve))
        assertEquals(1, p.equipmentBonus(listOf("machado"), "dano", leve))
    }

    @Test fun armaForaDoTipoDaClasseVaiMetade() {
        val p = pkg()
        assertEquals(2, p.equipmentBonus(listOf("espada-longa"), "dano"))
        assertEquals(1, p.equipmentBonus(listOf("espada-longa"), "dano", leve))
        assertEquals(1, p.equipmentBonus(listOf("cajado"), "inteligencia", leve))
        assertTrue(p.isRestricted("espada-longa", leve))
        assertFalse(p.isRestricted("adaga", leve))
    }

    @Test fun semPerfilOuSemClasseNaoHaPenalidade() {
        val p = pkg()
        assertFalse(p.isRestricted("cota-de-malha", null))
        assertFalse(p.isRestricted("cota-de-malha", WearerProfile()))
        assertEquals(6, p.equipmentBonus(listOf("cota-de-malha"), "max_pv", WearerProfile()))
    }

    @Test fun itemSemPesoNemTipoNuncaSofrePenalidade() {
        val p = pkg()
        assertFalse(p.isRestricted("escudo-leve", leve))
        assertEquals(1, p.equipmentBonus(listOf("escudo-leve"), "resistencia", leve))
    }

    @Test fun mestreDasArmasAnulaSoAPenalidadeDeArma() {
        val p = pkg()
        val perfil = p.wearerProfile(setOf(ItemWeight.LEVE), setOf("cajado"), listOf("mestre-das-armas"))
        assertTrue(perfil.masterWeapons)
        assertFalse(perfil.masterGear)
        assertEquals(2, p.equipmentBonus(listOf("espada-longa"), "dano", perfil))
        assertEquals(3, p.equipmentBonus(listOf("cota-de-malha"), "max_pv", perfil))
    }

    @Test fun mestreDosEquipamentosAnulaSoAPenalidadeDePeso() {
        val p = pkg()
        val perfil = p.wearerProfile(setOf(ItemWeight.LEVE), setOf("cajado"), listOf("mestre-dos-equipamentos"))
        assertTrue(perfil.masterGear)
        assertEquals(6, p.equipmentBonus(listOf("cota-de-malha"), "max_pv", perfil))
        assertEquals(1, p.equipmentBonus(listOf("espada-longa"), "dano", perfil))
    }

    @Test fun vantagensMestreExistemSemDesvantagemObrigatoria() {
        val p = pkg()
        listOf("mestre-das-armas", "mestre-dos-equipamentos").forEach { id ->
            val t = p.trait(id)!!
            assertEquals("vantagem", t.type)
            assertTrue(t.requires.none { it is Requirement.HasTrait })
        }
        val stats = p.data.stats.associate { it.id to it.default }
        assertTrue(p.validateCharacter(stats, listOf("mestre-das-armas", "juramento-sagrado")).errors.none { it.contains("Mestre") })
    }

    @Test fun pesoOuTipoInvalidosOuForaDeEquipamentoSaoRecusados() {
        val a = files()
        a["itens.txt"] = a.getValue("itens.txt") + "\nx1|X1|equipamento|peito|x|max_pv:+1|5||enorme|-\n"
        assertTrue(PackageRegistry().install(a) is InstallResult.Failure)
        val b = files()
        b["itens.txt"] = b.getValue("itens.txt") + "\nx2|X2|consumivel|-|x|-|5||leve|-\n"
        assertTrue(PackageRegistry().install(b) is InstallResult.Failure)
        val c = files()
        c["itens.txt"] = c.getValue("itens.txt") + "\nx3|X3|equipamento|mao|x|ataque:+1|5||leve|espada\n"
        assertTrue(PackageRegistry().install(c) is InstallResult.Failure)
    }

    @Test fun catalogoDeClassesTemTrezePesadasDezoitoMediasEDezenoveLeves() {
        val raw = File(dir(), "personagem-catalogo.json").readText()
        val pesos = Regex("\"armorWeight\"\\s*:\\s*\"(\\w+)\"").findAll(raw).map { it.groupValues[1] }.toList()
        assertEquals(50, pesos.size)
        assertEquals(13, pesos.count { it == "pesado" })
        assertEquals(18, pesos.count { it == "medio" })
        assertEquals(19, pesos.count { it == "leve" })
        val listas = Regex("\"weaponTypes\"\\s*:\\s*\\[([^\\]]*)]").findAll(raw).map { it.groupValues[1] }.toList()
        assertEquals(50, listas.size)
        val tipos = pkg().data.items.mapNotNull { it.weaponType }.toSet()
        listas.forEach { lista ->
            val tiposDaClasse = Regex("\"(\\w+)\"").findAll(lista).map { it.groupValues[1] }.toList()
            assertTrue(tiposDaClasse.isNotEmpty())
            assertTrue(tiposDaClasse.any { it in tipos })
        }
    }
}
