package dev.rpgsolo.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MI-08: lote de 20 sementes (1 a 20) x 500 anos, passo 10, 12 motores, comparado com `vetores-lote.txt` (gerado por
 * `exportar_vetores.py --lote`): resultado, deltas, notificações, hash da crônica e do estado e as métricas da seção 14
 * derivadas dos deltas (`lote.estatisticas`). As métricas saem da crônica em fluxo (sem guardar os deltas na memória).
 */
class LoteConformidadeTest {
    private class Metricas(private val mundo: MundoMolde) : CronicaSaida {
        private val ano0 = CalendarioReino.ANO_BASE
        private val reinos = ReinosMapa(mundo, emptyList())
        private var primeiroId: String? = null
        private var fimEra1: Int? = null
        private var incidente: Int? = null
        private var guerra: Int? = null
        private val caidos = HashSet<String>()
        private var resolvidos = 0
        private val estourados = LinkedHashMap<String, Int>()
        private var pactosFormados = 0
        private var pactosEncerrados = 0

        @Suppress("UNCHECKED_CAST")
        override fun linha(texto: String) {
            if (!texto.startsWith("{\"delta\"")) return
            val d = JsonLeitor.objeto(texto)["delta"] as Map<String, Any?>
            reinos.registrar(d)
            val tipo = d["tipo"].toString()
            val entidade = d["entidade"].toString()
            val ano = CalendarioReino.anoBase(Valores.numero(d["dia_mundo"]).toInt())
            if (primeiroId == null) primeiroId = d["id"].toString()
            when (tipo) {
                "era" -> if (fimEra1 == null && d["id"] != primeiroId) fimEra1 = ano - ano0
                "incidente_fronteira" -> if (incidente == null) incidente = ano - ano0
                "guerra" -> if (guerra == null) guerra = ano - ano0
                "queda_de_coroa" -> caidos.add(reinos.de(entidade))
                "tentativa_de_resolver_misterio" -> if ((d["estado_novo"] as? Map<String, Any?>)?.get("resolvido") == true) resolvidos++
                "alianca_de_povos" -> pactosFormados++
                "fim_de_alianca_de_povos" -> pactosEncerrados++
                "campo" -> if (d["campo"] == "estourado" && d["estado_novo"] == true && entidade.startsWith("clock.")) estourados.putIfAbsent(entidade, ano)
            }
        }

        /** Campos de métrica na ordem de `vetores-lote.txt`. */
        fun campos(): List<String> = listOf(
            fimEra1?.toString() ?: "",
            incidente?.toString() ?: "",
            guerra?.toString() ?: "",
            caidos.filter { it != "—" }.sortedWith(Ordem.CODIGO).joinToString(",").ifEmpty { "—" },
            resolvidos.toString(),
            estourados.size.toString(),
            estourados.values.count { it < ano0 + 100 }.toString(),
            pactosFormados.toString(),
            pactosEncerrados.toString(),
        )
    }

    @Test
    fun loteDeVinteSementesIgualAoPython() {
        val vetores = Conformidade.vetores("vetores-lote.txt")
        val hdr = vetores.single { it[0] == "HDR" }
        assertEquals("motores", hdr[1], Conformidade.nomes(Motores.todos(Dinamica(Conformidade.fonte))))
        assertEquals("hash do molde", hdr[2], Conformidade.mundo.hashMolde)
        assertEquals("hash da dinâmica", hdr[3], Conformidade.hashDinamica)
        val lote = vetores.filter { it[0] == "LOTE" }
        assertEquals(20, lote.size)
        for (c in lote) {
            val semente = c[1].toLong()
            val metricas = Metricas(Conformidade.mundo)
            val sim = Conformidade.simulador(semente, Motores::todos, 10, guardarDeltas = false, saida = metricas)
            val res = sim.rodar(500)
            val obtido = listOf(res.resultado, sim.crono.total.toString(), sim.nNotificacoes.toString(), sim.crono.hash, sim.estado.hash(), sim.estado.ent.size.toString()) + metricas.campos()
            assertEquals("semente $semente", c.drop(2), obtido)
            assertTrue("invariantes violadas na semente $semente", sim.violTotal.isEmpty())
        }
    }
}
