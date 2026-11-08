package dev.rpgsolo.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** MI-07: 500 anos de 3 sementes (blocos, saúde, hashes, invariantes) e retomada por snapshot iguais ao Python. */
class SimuladorConformidadeTest {
    private val vetores = Conformidade.vetores("vetores-simulador.txt")

    @Test
    fun quinhentosAnosIguaisAoPython() {
        val hdr = vetores.filter { it[0] == "HDR" }
        assertEquals(3, hdr.size)
        for (h in hdr) {
            val semente = h[1].toLong()
            assertEquals("motores", h[2], Conformidade.nomes(Motores.todos(Dinamica(Conformidade.fonte))))
            assertEquals("hash do molde", h[3], Conformidade.mundo.hashMolde)
            assertEquals("hash da dinâmica", h[4], Conformidade.hashDinamica)
            val pontos = ArrayList<String>()
            var blocos = 0
            lateinit var sim: Simulador
            sim =
                Conformidade.simulador(semente, Motores::todos, 10) { _, _ ->
                    blocos++
                    if (blocos % 5 == 0) pontos.add("PONTO|$semente|${blocos * 10}|${Conformidade.ponto(sim)}")
                }
            val res = sim.rodar(500)
            val doPython = vetores.filter { it[1] == h[1] }
            assertEquals(
                "BLOCO semente $semente",
                doPython.filter {
                    it[0] == "BLOCO"
                }.map { it.joinToString("|") },
                sim.saude.map { "BLOCO|$semente|${it.linha()}" },
            )
            assertEquals("PONTO semente $semente", doPython.filter { it[0] == "PONTO" }.map { it.joinToString("|") }, pontos)
            val inv = doPython.single { it[0] == "INV" }
            assertEquals("hash de invariantes.txt, semente $semente", inv[2], Hash.sha256(sim.relatorioInvariantes()))
            assertEquals("resultado final, semente $semente", inv[3], res.resultado)
            assertTrue("invariantes violadas na semente $semente: ${sim.violTotal.take(3).map { it.msg }}", sim.violTotal.isEmpty())
        }
    }

    @Test
    fun retomadaPorSnapshotIgualARodadaContinua() {
        val semente = 1L
        val direta = Conformidade.simulador(semente, Motores::todos, 10)
        direta.rodar(100)
        val ponto100 = vetores.single { it[0] == "PONTO" && it[1] == semente.toString() && it[2] == "100" }
        assertEquals("100 anos contra o Python", ponto100.drop(3).joinToString("|"), Conformidade.ponto(direta))

        var blocos = 0
        var json = ""
        val primeira =
            Conformidade.simulador(semente, Motores::todos, 10) { _, snap ->
                blocos++
                if (blocos == 5) json = checkNotNull(snap).paraJson()
            }
        primeira.rodar(50)
        assertTrue("snapshot do bloco 5 não gerado", json.isNotEmpty())

        val retomada = Conformidade.simulador(semente, Motores::todos, 10, retomada = Snapshot.deJson(json))
        assertEquals(18250, retomada.estado.proximoDia)
        retomada.rodar(50)
        assertEquals("retomada x rodada contínua", Conformidade.ponto(direta), Conformidade.ponto(retomada))
        assertTrue(retomada.violTotal.isEmpty())
    }
}
