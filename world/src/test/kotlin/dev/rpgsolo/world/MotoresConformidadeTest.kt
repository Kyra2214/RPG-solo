package dev.rpgsolo.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** MI-05 e MI-06: motores de pessoas (10 anos) e os 12 motores (100 anos) com hash da crônica e do estado iguais ao Python. */
class MotoresConformidadeTest {
    @Test
    fun motoresDePessoasIguaisAoPython() {
        val vetores = Conformidade.vetores("vetores-motores-pessoas.txt")
        val hdr = vetores.filter { it[0] == "HDR" }
        assertEquals(3, hdr.size)
        for (h in hdr) {
            assertEquals("motores", h[2], Conformidade.nomes(Motores.pessoas(Dinamica(Conformidade.fonte))))
            assertEquals("hash do molde", h[3], Conformidade.mundo.hashMolde)
            assertEquals("hash da dinâmica", h[4], Conformidade.hashDinamica)
        }
        val anos = vetores.filter { it[0] == "ANO" }
        assertEquals(30, anos.size)
        for (c in anos) {
            val ano = c[2].toInt()
            val s = Conformidade.simulador(c[1].toLong(), Motores::pessoas, ano)
            s.rodar(ano)
            assertEquals("semente ${c[1]}, ano $ano", c.drop(3).joinToString("|"), Conformidade.ponto(s))
        }
    }

    @Test
    fun motoresDoMundoIguaisAoPython() {
        val vetores = Conformidade.vetores("vetores-motores-mundo.txt")
        val hdr = vetores.filter { it[0] == "HDR" }
        assertEquals(3, hdr.size)
        for (h in hdr) {
            val semente = h[1].toLong()
            assertEquals("motores", h[2], Conformidade.nomes(Motores.todos(Dinamica(Conformidade.fonte))))
            assertEquals("hash do molde", h[3], Conformidade.mundo.hashMolde)
            assertEquals("hash da dinâmica", h[4], Conformidade.hashDinamica)
            val obtido = ArrayList<String>()
            lateinit var sim: Simulador
            sim = Conformidade.simulador(semente, Motores::todos, 10) { _, _ ->
                obtido.add("ANO|$semente|${obtido.size * 10 + 10}|${Conformidade.ponto(sim)}|${sim.estado.mundo["regime"]}|${sim.estado.eras.size}")
            }
            sim.rodar(100)
            val esperado = vetores.filter { it[0] == "ANO" && it[1] == h[1] }.map { it.joinToString("|") }
            assertTrue("sem pontos para a semente $semente", esperado.isNotEmpty())
            assertEquals("semente $semente", esperado, obtido)
        }
    }
}
