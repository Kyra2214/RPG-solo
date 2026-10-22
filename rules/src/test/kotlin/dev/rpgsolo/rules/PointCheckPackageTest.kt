package dev.rpgsolo.rules

import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.REAL_DICE
import dev.rpgsolo.engine.RollResult
import dev.rpgsolo.engine.resolveCheck
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
        assertEquals(listOf(5, 6), winningFaces(p.check("esquiva", mapOf("agilidade" to 2), media(p))))
        assertEquals(listOf(6), winningFaces(p.check("esquiva", mapOf("agilidade" to 0), media(p))))
    }

    @Test fun mestreEmEsquivaSomaDoisPontos() {
        val p = pacote()
        val spec = p.check("esquiva", mapOf("agilidade" to 1), media(p))
        val r = p.adjustCheck(spec, emptyList(), listOf("esquivador"), "esquiva")
        assertEquals(3, r.modifier)
        assertEquals(listOf(4, 5, 6), winningFaces(r))
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
        assertEquals(listOf(5, 6), winningFaces(spec))
    }

    @Test fun condicaoMudaPontosENaoOsDados() {
        val p = pacote()
        val spec = p.check("esquiva", mapOf("agilidade" to 3), media(p))
        val r = p.adjustCheck(spec, listOf("visao-turva")) // Perda em agilidade
        assertEquals(1, r.diceCount)
        assertEquals(2, r.modifier)
        assertEquals(listOf(5, 6), winningFaces(r))
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

    @Test fun periciasSaoDePontosEAtaqueEhD20() {
        val p = proprio()
        val stats = mapOf("habilidade" to 2, "poder" to 3)
        assertEquals(Comparison.POINTS, p.check("esquiva", stats, dif(p, "media")).comparison)
        assertEquals(Comparison.POINTS, p.check("pericia", stats, dif(p, "media")).comparison)
        val ataque = p.check("ataque", stats, dif(p, "media"))
        assertEquals(1, ataque.diceCount)
        assertEquals(20, ataque.diceSides)
        assertEquals(3, ataque.modifier)
        assertEquals(20, p.check("sorte", stats, dif(p, "media")).diceSides)
    }

    @Test fun dificuldadeSobeADadoReal() {
        val p = proprio()
        val stats = mapOf("habilidade" to 3)
        val dados = listOf("facil", "media", "dificil", "heroica").map { p.check("esquiva", stats, dif(p, it)).diceSides }
        assertEquals(listOf(4, 6, 8, 10), dados)
        assertTrue(dados.all { it in REAL_DICE })
    }

    @Test fun comTresPontosAChanceCaiComADificuldade() {
        val p = proprio()
        val stats = mapOf("habilidade" to 3)

        fun chance(id: String): Pair<Int, Int> {
            val spec = p.check("esquiva", stats, dif(p, id))
            return winningFaces(spec).size to spec.diceSides
        }
        assertEquals(3 to 4, chance("facil")) // 2–4 no d4 (75%)
        assertEquals(3 to 6, chance("media")) // 4–6 no d6 (50%)
        assertEquals(3 to 8, chance("dificil")) // 6–8 no d8
        assertEquals(3 to 10, chance("heroica")) // 8–10 no d10 (30%)
    }

    @Test fun nuncaPassaDeLadosMenosUmNemCaiDeUm() {
        val p = proprio()
        assertEquals(3, winningFaces(p.check("esquiva", mapOf("habilidade" to 5), dif(p, "facil"))).size) // d4: até 3 faces
        assertEquals(1, winningFaces(p.check("esquiva", mapOf("habilidade" to 0), dif(p, "heroica"))).size)
    }

    @Test fun dificuldadesEstaoNaEscalaDoD20() {
        val p = proprio()
        assertEquals(listOf(8, 12, 15, 18), listOf("facil", "media", "dificil", "heroica").map { p.difficultyValue(it) })
    }
}
