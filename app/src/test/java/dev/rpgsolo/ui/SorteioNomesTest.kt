package dev.rpgsolo.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Sorteio de nomes por reino (CG-4.5). */
class SorteioNomesTest {
    private val reinos = listOf("kingdom.aeltharyn", "kingdom.coroas-de-pedra", "kingdom.veu-dos-malditos", "kingdom.vale-palancar", "kingdom.azeroth", null, "")

    @Test
    fun mesmaSemente_mesmoNome() {
        reinos.forEach { assertEquals(SorteioNomes.sortear(it, Random(7)), SorteioNomes.sortear(it, Random(7))) }
    }

    @Test
    fun nuncaDevolveVazioNemPrimeiroNomeReservado() {
        reinos.forEach { reino ->
            val aleatorio = Random(1)
            repeat(500) {
                val nome = SorteioNomes.sortear(reino, aleatorio)
                assertTrue(nome.isNotBlank())
                assertFalse("reservado: $nome", SorteioNomes.reservado(nome))
                assertTrue("começa em maiúscula: $nome", nome.first().isUpperCase())
            }
        }
    }

    @Test
    fun reservado_ignoraMaiusculasEEpiteto() {
        assertTrue(SorteioNomes.reservado("tik"))
        assertTrue(SorteioNomes.reservado("Saelith Arco-de-Chuva"))
        assertTrue(SorteioNomes.reservado("Azeroth"))
        assertFalse(SorteioNomes.reservado("Zorvalen"))
    }

    @Test
    fun evitaONomeAtual() {
        val aleatorio = Random(3)
        val atual = SorteioNomes.sortear("kingdom.aeltharyn", Random(3))
        repeat(200) { assertNotEquals(atual.lowercase(), SorteioNomes.sortear("kingdom.aeltharyn", aleatorio, atual).lowercase()) }
    }

    @Test
    fun variaEntreSorteios() {
        val aleatorio = Random(9)
        val nomes = (1..50).map { SorteioNomes.sortear("kingdom.coroas-de-pedra", aleatorio) }.toSet()
        assertTrue(nomes.size > 10)
    }
}
