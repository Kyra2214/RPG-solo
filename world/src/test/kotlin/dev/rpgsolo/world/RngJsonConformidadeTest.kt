package dev.rpgsolo.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigInteger

/** MI-03: RNG, JSON canônico e hash encadeado batem com os vetores gerados pelo Python (`vetores-rng-json.txt`). */
class RngJsonConformidadeTest {
    private val vetores = Conformidade.vetores("vetores-rng-json.txt")

    @Test
    fun rngIgualAoPython() {
        val geradores = HashMap<String, PythonRandom>()
        var conferidos = 0
        for (c in vetores.filter { it[0] == "RNG" }) {
            val op = c[2]
            val r = geradores.getOrPut(c[1] + "|" + op) { PythonRandom(BigInteger(c[1])) }
            val esperado = c[4].split(',')
            val rotulo = "RNG semente=${c[1]} op=$op args=${c[3]}"
            when (op) {
                "random" -> esperado.forEachIndexed { i, x -> assertEquals("$rotulo #$i", x.toDouble().toRawBits(), r.random().toRawBits()) }
                "bits" -> assertEquals(rotulo, esperado, List(esperado.size) { r.getrandbits(c[3].toInt()).toString() })
                "randrange" -> assertEquals(rotulo, esperado, List(esperado.size) { r.randrange(c[3].toInt()).toString() })
                "randint" -> {
                    val (a, b) = c[3].split(',').map { it.toInt() }
                    assertEquals(rotulo, esperado, List(esperado.size) { r.randint(a, b).toString() })
                }
                "choice" -> {
                    val lista = List(c[3].toInt()) { it }
                    assertEquals(rotulo, esperado, List(esperado.size) { r.choice(lista).toString() })
                }
                else -> throw AssertionError("operação desconhecida: $op")
            }
            conferidos++
        }
        assertTrue("poucos vetores de RNG: $conferidos", conferidos >= 200)
    }

    @Test
    fun sementeDoDiaIgualAoPython() {
        val dias = vetores.filter { it[0] == "DIA" }
        assertTrue(dias.isNotEmpty())
        for (c in dias) {
            val rotulo = "DIA ${c[1]}|${c[2]}|${c[3]}"
            assertEquals(rotulo, c[4], PythonRandom.sementeDoDia(c[1], c[2], c[3].toInt()).toString())
            val r = PythonRandom.doDia(c[1], c[2], c[3].toInt())
            assertEquals(rotulo, c[5].split(','), List(5) { r.randrange(1000).toString() })
        }
    }

    @Test
    fun jsonCanonicoEHashIguaisAoPython() {
        val casos = vetores.filter { it[0] == "JSON" }
        assertTrue(casos.size >= 15)
        for (c in casos) {
            val canon = JsonCanonico.canon(JsonLeitor.valor(c[1]))
            assertEquals("JSON ${c[1]}", c[2], canon)
            assertEquals("sha256 ${c[1]}", c[3], Hash.sha256(canon))
        }
    }

    @Test
    fun cadeiaDeHashIgualAoPython() {
        val cadeia = vetores.filter { it[0] == "CADEIA" }
        val inicial = cadeia.first { it.size == 3 }
        val h0 = Hash.sha256(JsonCanonico.canon(mapOf("cabecalho" to mapOf("semente" to 1, "molde" to "x"))))
        assertEquals(inicial[2], h0)
        for (c in cadeia.filter { it.size == 4 }) {
            assertEquals("CADEIA ${c[2]}", c[3], Hash.encadear(c[1], JsonLeitor.valor(c[2])))
        }
    }
}
