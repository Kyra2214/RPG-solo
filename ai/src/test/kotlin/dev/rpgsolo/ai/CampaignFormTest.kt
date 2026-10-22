package dev.rpgsolo.ai

import org.junit.Assert.*
import org.junit.Test

class CampaignFormTest {
    @Test fun vazioNaoGeraDescricao() {
        assertTrue(CampaignForm().isEmpty())
        assertTrue(CampaignForm(themes = listOf(" ", "")).isEmpty())
    }

    @Test fun textoLivreVemPrimeiroEOsCamposDepois() {
        val d =
            CampaignForm(
                genre = "cyberpunk",
                tone = "sombrio",
                themes = listOf("corporações", " ", "vingança"),
                freeText = "Um anão minerador de máquinas de combate",
            ).toDescription()
        assertEquals("Um anão minerador de máquinas de combate\nGênero: cyberpunk\nTom: sombrio\nTemas: corporações, vingança", d)
    }

    @Test fun soFormularioFunciona() {
        assertEquals("Época: medieval\nDuração: curta", CampaignForm(era = "medieval", duration = "curta").toDescription())
    }

    @Test fun soTextoLivreFunciona() {
        assertEquals("quero jogar fantasia sombria", CampaignForm(freeText = "  quero jogar fantasia sombria ").toDescription())
    }
}
