package dev.rpgsolo.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** PT-03: a seleção por chips vira a descrição que segue para o `NewCampaignFlow` (formato montado em `NewCampaignSection`). */
class CampanhaPorSelecaoPt03Test {
    private fun selecao(
        extras: String = "",
        tom: String = "Sombrio",
        dificuldade: String = "Difícil",
    ) = CampaignForm(
        genre = "Fantasia sombria, terror",
        tone = tom,
        difficulty = dificuldade,
        themes = listOf("medo", "mistério", "sobrevivência"),
        freeText = extras,
    ).toDescription()

    @Test fun predefinicaoSemDigitarNadaGeraDescricaoCompleta() {
        assertEquals(
            "Gênero: Fantasia sombria, terror\nTom: Sombrio\nDificuldade: Difícil\nTemas: medo, mistério, sobrevivência",
            selecao(),
        )
    }

    @Test fun ritmoEModoEntramComoLinhasEOsDetalhesExtrasVemPrimeiro() {
        val extras = listOf("Ritmo: Calmo", "Modo: Narrativo", "  um anão em fuga ").joinToString("\n")
        val d = selecao(extras.trim())
        assertTrue(d.startsWith("Ritmo: Calmo\nModo: Narrativo\num anão em fuga\nGênero:"))
        assertTrue(d.endsWith("Temas: medo, mistério, sobrevivência"))
    }

    @Test fun tomEDificuldadeNaoEscolhidosSaemDaDescricao() {
        val d = selecao(tom = "", dificuldade = "")
        assertFalse("Tom:" in d)
        assertFalse("Dificuldade:" in d)
        assertTrue(d.startsWith("Gênero:"))
    }

    @Test fun descricaoNaoFicaVaziaComSoUmaPredefinicao() {
        assertFalse(CampaignForm(genre = "Fantasia, aventura clássica", themes = listOf("exploração")).isEmpty())
    }
}
