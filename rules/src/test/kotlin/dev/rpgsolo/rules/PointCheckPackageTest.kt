package dev.rpgsolo.rules

import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.RollResult
import dev.rpgsolo.engine.resolveCheck
import dev.rpgsolo.engine.resolvedPool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

private fun winningFaces(spec: CheckSpec): List<Int> =
    (1..spec.diceSides).filter { resolveCheck(spec, RollResult("1d${spec.diceSides}", listOf(it), 0)).outcome.isSuccess }

/** Teste de pontos ligado ao pacote: talento soma pontos, condições dão +1/-1 ponto, dificuldade troca o dado. */
class PointCheckPackageTest {
    private fun files(): Map<String, String> {
        val dir =
            listOf("src/test/resources/packages/teste-basico", "rules/src/test/resources/packages/teste-basico")
                .map(::File).first { it.exists() }
        return dir.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
    }

    /** Pacote de teste com a esquiva trocada por um teste de pontos (dado base d6, pontos = agilidade). */
    private fun pacote(): RulePackage {
        val f = files().toMutableMap()
        f["testes.txt"] =
            f.getValue("testes.txt").lines()
                .joinToString("\n") { if (it.startsWith("esquiva|")) "esquiva|Esquiva|1|6|agilidade|1|POINTS|-|-|-" else it }
        val r = PackageRegistry()
        assertTrue(r.install(f) is InstallResult.Success)
        return r.get("teste-basico")!!
    }

    private fun media(p: RulePackage) = p.difficultyValue("media")!!

    @Test fun pontosDaFichaViramFacesDeSucesso() {
        val p = pacote()
        assertEquals(listOf(4, 5, 6), winningFaces(p.check("esquiva", mapOf("agilidade" to 2), media(p))))
        assertEquals(listOf(5, 6), winningFaces(p.check("esquiva", mapOf("agilidade" to 1), media(p))))
        assertEquals(listOf(6), winningFaces(p.check("esquiva", mapOf("agilidade" to 0), media(p))))
    }

    @Test fun mestreEmEsquivaSomaDoisPontos() {
        val p = pacote()
        val spec = p.check("esquiva", mapOf("agilidade" to 1), media(p))
        val r = p.adjustCheck(spec, emptyList(), listOf("esquivador"), "esquiva")
        assertEquals(3, r.modifier)
        assertEquals(listOf(3, 4, 5, 6), winningFaces(r))
    }

    @Test fun testePodeUsarPontosDePericiaEmVezDoAtributo() {
        val f = files().toMutableMap()
        f["testes.txt"] =
            f.getValue("testes.txt").lines()
                .joinToString("\n") {
                    if (!it.startsWith("esquiva|")) {
                        it
                    } else {
                        it.split("|").toMutableList().also { fields ->
                            fields[3] = "6"
                            fields[6] = "POINTS"
                            fields += "furtividade"
                        }.joinToString("|")
                    }
                }
        val r = PackageRegistry()
        assertTrue(r.install(f) is InstallResult.Success)
        val p = r.get("teste-basico")!!
        val spec = p.check("esquiva", mapOf("agilidade" to 5), media(p), mapOf("furtividade" to 2))
        assertEquals(2, spec.modifier)
        assertEquals(listOf(4, 5, 6), winningFaces(spec))
    }

    @Test fun condicaoMudaPontosENaoOsDados() {
        val p = pacote()
        val spec = p.check("esquiva", mapOf("agilidade" to 3), media(p))
        val r = p.adjustCheck(spec, listOf("visao-turva")) // Perda em agilidade
        assertEquals(1, r.diceCount)
        assertEquals(2, r.modifier)
        assertEquals(listOf(4, 5, 6), winningFaces(r))
    }

    @Test fun dificuldadeTrocaOTamanhoDoDado() {
        val p = pacote() // dificuldades: facil, media, dificil → d4, d6, d8
        val stats = mapOf("agilidade" to 3)
        assertEquals(4, p.check("esquiva", stats, p.difficultyValue("facil")!!).diceSides)
        assertEquals(6, p.check("esquiva", stats, media(p)).diceSides)
        assertEquals(8, p.check("esquiva", stats, p.difficultyValue("dificil")!!).diceSides)
    }

    @Test fun semAjusteOTesteVoltaIntacto() {
        val p = pacote()
        val spec = p.check("esquiva", mapOf("agilidade" to 2), media(p))
        assertEquals(spec, p.adjustCheck(spec, emptyList(), emptyList()))
    }

    @Test fun pontosExigemUmDadoReal() {
        val f = files().toMutableMap()
        val base = f.getValue("testes.txt")
        f["testes.txt"] = base + "\nruim|Ruim|2|6|agilidade|1|POINTS|-|-|-\n"
        assertTrue(PackageRegistry().install(f) is InstallResult.Failure)
        f["testes.txt"] = base + "\nruim|Ruim|1|15|agilidade|1|POINTS|-|-|-\n"
        assertTrue(PackageRegistry().install(f) is InstallResult.Failure)
        f["testes.txt"] = base + "\nbom|Bom|1|12|agilidade|1|POINTS|-|-|-\n"
        assertTrue(PackageRegistry().install(f) is InstallResult.Success)
    }

    @Test fun enumTemOModoNovo() {
        assertTrue(Comparison.values().any { it.name == "POINTS" })
    }
}

/** Sistema próprio: perícias com dado que a dificuldade troca (d4 a d10), ataque/resistir/sorte em d20 contra CD. */
class SistemaProprioDadosTest {
    private fun proprio(): RulePackage {
        val dir =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
        val f = dir.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        val r = PackageRegistry()
        assertTrue(r.install(f) is InstallResult.Success)
        return r.get("sistema-proprio")!!
    }

    private fun dif(
        p: RulePackage,
        id: String,
    ) = p.difficultyValue(id)!!

    @Test fun todosOsTestesDoSistemaProprioUsamAFormulaUnica() {
        val p = proprio()
        listOf("ataque", "forca", "resistir", "inteligencia", "sorte", "esquiva", "destreza", "pericia").forEach {
            assertEquals(it, Comparison.PERCENT, p.check(it, emptyMap(), dif(p, "media")).comparison)
        }
    }

    private fun chance(
        p: RulePackage,
        teste: String,
        stats: Map<String, Int>,
        dificuldade: String = "facil",
    ) = p.check(teste, stats, dif(p, dificuldade)).resolvedPool().difficulty

    @Test fun cadaTesteUsaOAtributoCerto() {
        val p = proprio()
        // atributo 4 = +10% sobre os 50% base; dificuldade fácil não desconta
        assertEquals(60, chance(p, "forca", mapOf("poder" to 4)))
        assertEquals(60, chance(p, "esquiva", mapOf("agilidade" to 4)))
        assertEquals(60, chance(p, "resistir", mapOf("resistencia" to 4)))
        assertEquals(60, chance(p, "inteligencia", mapOf("inteligencia" to 4)))
        assertEquals(60, chance(p, "destreza", mapOf("destreza" to 4)))
        assertEquals(60, chance(p, "pericia", mapOf("destreza" to 4)))
        assertEquals(60, chance(p, "ataque", mapOf("destreza" to 4)))
        // um atributo de outro teste não conta
        assertEquals(50, chance(p, "esquiva", mapOf("destreza" to 4, "poder" to 4)))
    }

    @Test fun sorteSomaCincoPorCentoPorPontoEmTodosOsTestes() {
        val p = proprio()
        assertEquals(65, chance(p, "esquiva", mapOf("agilidade" to 2, "sorte" to 2)))
        assertEquals(60, chance(p, "sorte", mapOf("sorte" to 2)))
    }

    @Test fun dificuldadeDescontaDaChance() {
        val p = proprio()
        val chances = listOf("facil", "media", "dificil", "heroica").map { chance(p, "esquiva", emptyMap(), it) }
        assertEquals(listOf(50, 40, 30, 20), chances)
    }

    @Test fun chanceNuncaPassaDeNoventaECinco() {
        val p = proprio()
        assertEquals(95, chance(p, "forca", mapOf("poder" to 100)))
    }

    @Test fun ganhoEPerdaEBonusValemCincoPorCentoCada() {
        val p = proprio()
        val base = p.check("esquiva", mapOf("agilidade" to 2), dif(p, "media"))
        assertEquals(50 + 5, base.modifier)
        val comBonus = p.adjustCheck(base, emptyList(), emptyList(), "esquiva", listOf(ExternalBonus("esquiva", 2, "teste", "teste:1")))
        assertEquals(base.modifier + 10, comBonus.modifier)
    }

    @Test fun dificuldadesEstaoNaEscalaDoD20() {
        val p = proprio()
        assertEquals(listOf(8, 12, 15, 18), listOf("facil", "media", "dificil", "heroica").map { p.difficultyValue(it) })
    }
}
