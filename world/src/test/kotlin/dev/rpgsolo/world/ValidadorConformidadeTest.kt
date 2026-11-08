package dev.rpgsolo.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** MI-02: carregador e validador do molde reproduzem `Mythralis/saidas/validacao-mundo.txt` (0 erro, 20 avisos). */
class ValidadorConformidadeTest {
    @Test
    fun moldeCarregaSemErrosDeCarga() {
        assertTrue("erros de carga: ${Conformidade.mundo.erros.take(3).map { it.mensagem }}", Conformidade.mundo.erros.isEmpty())
        assertEquals(Conformidade.manifesto.moldeSha256, Conformidade.mundo.hashMolde)
    }

    @Test
    fun relatorioIgualAoDoPython() {
        val esperado = File("../Mythralis/saidas/validacao-mundo.txt")
        assertTrue("relatório do Python ausente: ${esperado.absolutePath}", esperado.isFile)
        val w = Conformidade.mundo
        val achados = ValidadorMundo.validar(w, DinamicaMinima.carregar(Conformidade.fonte))
        val (erros, avisos) = ValidadorMundo.resumo(achados)
        assertEquals(0, erros)
        assertEquals(20, avisos)
        assertEquals(esperado.readText(Charsets.UTF_8).replace("\r\n", "\n"), ValidadorMundo.relatorio(w, achados))
    }
}
