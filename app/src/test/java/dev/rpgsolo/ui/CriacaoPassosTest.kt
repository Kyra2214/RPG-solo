package dev.rpgsolo.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Regra dos sete passos da criação por abas (CG-4.1) e mensagens de ordem (CG-4.9). */
class CriacaoPassosTest {
    private val vazio =
        ProgressoCriacao(
            racaCompleta = false,
            classeEscolhida = false,
            profissaoEscolhida = false,
            statusSemSobra = false,
            vantagensValidasComTracos = false,
            informacoesOk = false,
        )

    @Test
    fun semEscolhas_nenhumPassoConcluido_eOPrimeiroPendenteERaca() {
        assertTrue(CriacaoPassos.concluidos(vazio).isEmpty())
        assertEquals(PassoCriacao.RACA, CriacaoPassos.primeiroPendente(emptySet()))
    }

    @Test
    fun habilidades_dependemDeRacaEClasse() {
        assertFalse(PassoCriacao.HABILIDADES in CriacaoPassos.concluidos(vazio.copy(racaCompleta = true)))
        assertFalse(PassoCriacao.HABILIDADES in CriacaoPassos.concluidos(vazio.copy(classeEscolhida = true)))
        assertTrue(PassoCriacao.HABILIDADES in CriacaoPassos.concluidos(vazio.copy(racaCompleta = true, classeEscolhida = true)))
    }

    @Test
    fun tudoPronto_concluiOsSetePassos_eOPendenteEOUltimo() {
        val tudo = ProgressoCriacao(true, true, true, true, true, true)
        val feitos = CriacaoPassos.concluidos(tudo)
        assertEquals(PassoCriacao.entries.toSet(), feitos)
        assertEquals(PassoCriacao.INFORMACOES, CriacaoPassos.primeiroPendente(feitos))
    }

    @Test
    fun primeiroPendente_pulaOsFeitos() {
        val feitos = CriacaoPassos.concluidos(vazio.copy(racaCompleta = true, classeEscolhida = true))
        assertEquals(PassoCriacao.PROFISSAO, CriacaoPassos.primeiroPendente(feitos))
    }

    @Test
    fun proximoEAnterior_naoPassamDosLimites() {
        assertEquals(PassoCriacao.CLASSE, CriacaoPassos.proximo(PassoCriacao.RACA))
        assertEquals(PassoCriacao.INFORMACOES, CriacaoPassos.proximo(PassoCriacao.INFORMACOES))
        assertEquals(PassoCriacao.RACA, CriacaoPassos.anterior(PassoCriacao.RACA))
        assertEquals(PassoCriacao.VANTAGENS, CriacaoPassos.anterior(PassoCriacao.INFORMACOES))
    }

    @Test
    fun habilidade_naoEDistribuidaNaCriacao_masOsDemaisAtributosSao() {
        assertTrue(AtributosDaCriacao.ehOculto("habilidade"))
        listOf("poder", "resistencia", "inteligencia", "agilidade", "destreza", "sorte").forEach {
            assertFalse(it, AtributosDaCriacao.ehOculto(it))
        }
    }
}
