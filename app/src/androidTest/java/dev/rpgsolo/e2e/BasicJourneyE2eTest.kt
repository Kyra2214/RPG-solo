package dev.rpgsolo.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.MainActivity
import dev.rpgsolo.ui.openTab
import dev.rpgsolo.ui.waitForAppReady
import dev.rpgsolo.ui.waitForText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Fluxo sem rede que prova que o app abre, navega, cria ficha e gera backup. */
@RunWith(AndroidJUnit4::class)
class BasicJourneyE2eTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun jornadaBasica_navegaCriaFichaEgeraBackup() {
        composeRule.waitForAppReady()
        composeRule.onNodeWithText("Preparar sua mesa").assertIsDisplayed()

        composeRule.onNodeWithText("Heróis").performClick()
        composeRule.onNodeWithText("Personagens").assertIsDisplayed()
        composeRule.onNodeWithText("Ponto de partida").assertIsDisplayed()
        composeRule.onNodeWithText("Nome").performTextInput("Herói E2E")
        composeRule.onNodeWithText("Criar ficha").performScrollTo().performClick()
        composeRule.waitForText("Ficha de Herói E2E criada e salva.")
        composeRule.onNodeWithText("Ficha de Herói E2E criada e salva.").assertIsDisplayed()

        composeRule.openTab("Campanhas")
        composeRule.onNodeWithText("Campanhas salvas").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Gerar texto").performScrollTo().performClick()
        // O backup é gerado em IO: o botão "Restaurar texto" só habilita quando o campo recebe o JSON.
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasText("Restaurar texto") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
        }

        // Regras, Sistemas e Config ficam no menu "Mais" do layout estreito.
        composeRule.openTab("Regras")
        composeRule.onNodeWithText("Livro de regras").assertIsDisplayed()
        composeRule.openTab("Sistemas")
        composeRule.openTab("Config")
        composeRule.onNodeWithText("Configuração").assertIsDisplayed()
        composeRule.onNodeWithText("Jogar").performClick()
        composeRule.onNodeWithText("Preparar sua mesa").assertIsDisplayed()
    }
}
