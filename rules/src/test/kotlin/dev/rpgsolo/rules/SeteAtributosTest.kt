package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-02: sistema próprio com sete atributos e sem teto (`max` "-" em `atributos.txt`). */
class SeteAtributosTest {
    private fun files(): MutableMap<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(f: Map<String, String> = files()): RulePackage = (PackageRegistry().install(f) as InstallResult.Success).pkg

    private val ids = listOf("poder", "habilidade", "resistencia", "inteligencia", "agilidade", "destreza", "sorte")

    @Test fun pacoteProprioTemOsSeteAtributosSemTetoEPadraoZero() {
        val stats = pkg().data.stats
        assertEquals(ids, stats.map { it.id })
        stats.forEach {
            assertTrue("${it.id} sem teto", it.uncapped)
            assertEquals("${it.id} mínimo", 0, it.min)
            assertEquals("${it.id} padrão", 0, it.default)
        }
    }

    @Test fun valorAltoNaoViolaLimiteMasContaNoOrcamentoDePontos() {
        val p = pkg()
        val alto = p.validateCharacter(mapOf("poder" to 9, "sorte" to 1), emptyList())
        assertFalse(alto.errors.toString(), alto.errors.any { it.contains("fora do limite") })
        assertEquals(10, alto.pointsSpent)
        // 10 pontos na criação: um ponto além do orçamento continua sendo recusado, mas por orçamento, não por teto de atributo.
        val alemDoOrcamento = p.validateCharacter(mapOf("poder" to 11), emptyList())
        assertFalse(alemDoOrcamento.valid)
        assertFalse(alemDoOrcamento.errors.any { it.contains("fora do limite") })
    }

    @Test fun atributosNovosPodemReceberPontosNaCriacao() {
        val p = pkg()
        val report = p.validateCharacter(mapOf("inteligencia" to 2, "agilidade" to 3, "destreza" to 2, "sorte" to 3), emptyList())
        assertFalse(report.errors.toString(), report.errors.any { it.contains("fora do limite") })
        assertEquals(10, report.pointsSpent)
    }

    @Test fun fichaAntigaSemOsAtributosNovosUsaPadraoZero() {
        val p = pkg()
        val antiga = p.validateCharacter(mapOf("poder" to 2, "habilidade" to 1, "resistencia" to 1), emptyList())
        assertFalse(antiga.errors.toString(), antiga.errors.any { it.contains("fora do limite") || it.contains("desconhecido") })
        assertEquals(4, antiga.pointsSpent)
        val ficha = (p.buildFicha("f-antiga", "Antiga", "", mapOf("poder" to 2), emptyList()) as FichaBuild.Created).ficha
        assertEquals(ids, ficha.stats.keys.toList())
        assertEquals(0, ficha.stats.getValue("sorte"))
    }

    @Test fun configDoMotorNaoLimitaOsAtributos() {
        val cfg = pkg().engineConfig()
        assertEquals(ids.toSet(), cfg.statCaps.keys)
        assertTrue(cfg.statCaps.values.all { it == StatDef.NO_CAP })
        assertTrue(cfg.statDefaults.values.all { it == 0 })
        // Distribuição inicial: 10 de status + 2 de habilidade/profissão (N-01/N-02).
        assertEquals(10, cfg.initialStatPoints)
        assertEquals(2, cfg.initialAbilityPoints)
    }

    @Test fun parserAindaAceitaTetoNumericoEMarcaSemTetoComTraco() {
        val comTeto = files().also { it["atributos.txt"] = it.getValue("atributos.txt").replace("poder|Poder|0|-|0", "poder|Poder|0|5|0") }
        val parsed = PackageParser.parse(comTeto) as ParseResult.Ok
        assertEquals(5, parsed.data.stats.first { it.id == "poder" }.max)
        assertFalse(parsed.data.stats.first { it.id == "poder" }.uncapped)
        assertTrue(parsed.data.stats.first { it.id == "sorte" }.uncapped)

        val invalido = files().also { it["atributos.txt"] = it.getValue("atributos.txt").replace("poder|Poder|0|-|0", "poder|Poder|0|x|0") }
        val falha = PackageParser.parse(invalido)
        assertTrue(falha is ParseResult.Fail)
        assertTrue((falha as ParseResult.Fail).errors.any { it.contains("atributos.txt") })
    }

    @Test fun trechoDeRegraDoAtributoDizSemTeto() {
        val trecho = pkg().search("Inteligência", 10).first { it.id == "inteligencia" }
        assertTrue(trecho.text, trecho.text.contains("sem teto"))
    }
}
