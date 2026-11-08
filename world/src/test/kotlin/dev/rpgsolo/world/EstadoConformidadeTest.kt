package dev.rpgsolo.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** MI-04: calendário, hash do estado inicial (sem e com dinâmica) e sequência de deltas com importância e hash. */
class EstadoConformidadeTest {
    private val vetores = Conformidade.vetores("vetores-estado.txt")

    @Test
    fun calendariosIguaisAoPython() {
        val cals = CalendarioReino.construir(Conformidade.mundo)
        val linhas = vetores.filter { it[0] == "CAL" }
        assertTrue(linhas.size >= 100)
        for (c in linhas) {
            val dia = c[2].toInt()
            val d = checkNotNull(cals[c[1]]) { "sem calendário de ${c[1]}" }.data(dia)
            assertEquals("CAL ${c[1]} $dia", c[3], "${d.ano},${d.diaDoAno},${d.estacao},${d.lua}")
            assertEquals("ano_base $dia", c[4].toInt(), CalendarioReino.anoBase(dia))
        }
    }

    @Test
    fun estadoInicialIgualAoPython() {
        for (c in vetores.filter { it[0] == "ESTADO" }) {
            val din = if (c[1] == "com_dinamica") Dinamica(Conformidade.fonte) else null
            val e = Estado.doMolde(Conformidade.mundo, din)
            assertEquals("hash do estado ${c[1]}", c[2], e.hash())
            assertEquals("entidades ${c[1]}", c[3].toInt(), e.ent.size)
        }
    }

    @Test
    fun sequenciaDeDeltasIgualAoPython() {
        val e = Estado.doMolde(Conformidade.mundo, null)
        val deltas = vetores.filter { it[0] == "DELTA" }
        assertTrue(deltas.size >= 24)
        for ((i, c) in deltas.withIndex()) {
            val d = JsonLeitor.objeto(c[1])
            val imp = Importancia.calcular(d, e)
            assertEquals("importância do delta #$i", c[2].toInt(), imp)
            d["importancia"] = imp
            e.aplicar(d)
            assertEquals("hash após o delta #$i", c[3], e.hash())
            assertEquals("entidades após o delta #$i", c[4].toInt(), e.ent.size)
            assertEquals("Ecos ativos após o delta #$i", c[5].toInt(), e.ecosAtivos.size)
        }
    }
}
