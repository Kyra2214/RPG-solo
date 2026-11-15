package dev.rpgsolo.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** PT-04: aparência e personalidade por chips e sorteio, sem texto livre na criação. */
class CriacaoChipsPt04Test {
    @Test
    fun personalidade_sorteioTemUmaFrasePorCategoria() {
        repeat(20) { semente ->
            val texto = CriacaoPersonalidade.sortear(Random(semente))
            val frases = CriacaoAparencia.frases(texto)
            assertEquals(CriacaoPersonalidade.categorias.size, frases.size)
            CriacaoPersonalidade.categorias.zip(frases).forEach { (cat, frase) -> assertTrue(frase in cat.second) }
        }
    }

    @Test
    fun personalidade_mesmaSementeMesmoSorteio() {
        assertEquals(CriacaoPersonalidade.sortear(Random(5)), CriacaoPersonalidade.sortear(Random(5)))
    }

    @Test
    fun personalidade_alternarColocaETira() {
        val com = CriacaoPersonalidade.alternar("", "calmo")
        assertTrue(CriacaoPersonalidade.contem(com, "calmo"))
        val dois = CriacaoPersonalidade.alternar(com, "leal")
        assertEquals("calmo, leal", dois)
        val sem = CriacaoPersonalidade.alternar(dois, "CALMO")
        assertEquals("leal", sem)
        assertFalse(CriacaoPersonalidade.contem(sem, "calmo"))
    }

    @Test
    fun aparencia_sorteioTemUmaFrasePorCategoria() {
        val frases = CriacaoAparencia.frases(CriacaoAparencia.sortear(Random(3)))
        assertEquals(CriacaoAparencia.categorias.size, frases.size)
    }

    @Test
    fun chips_semFrasesRepetidasNemVazias() {
        val todas = (CriacaoAparencia.categorias + CriacaoPersonalidade.categorias).flatMap { it.second }
        assertTrue(todas.all { it.isNotBlank() })
        assertEquals(todas.size, todas.map { it.lowercase() }.toSet().size)
    }
}
