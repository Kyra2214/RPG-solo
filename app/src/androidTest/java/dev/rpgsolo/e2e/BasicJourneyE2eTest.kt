package dev.rpgsolo.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.MainActivity
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
        composeRule.onNodeWithText("A mesa ainda está vazia").assertIsDisplayed()

        composeRule.onNodeWithText("Heróis").performClick()
        composeRule.onNodeWithText("Personagens").assertIsDisplayed()
        composeRule.onNodeWithText("Ponto de partida").assertIsDisplayed()
        composeRule.onNodeWithText("Nome").performTextInput("Herói E2E")
        composeRule.onNodeWithText("Criar ficha").performScrollTo().performClick()
        composeRule.onNodeWithText("Ficha de Herói E2E criada e salva.").assertIsDisplayed()

        composeRule.onNodeWithText("Campanhas").performClick()
        composeRule.onNodeWithText("Campanhas salvas").assertIsDisplayed()
        composeRule.onNodeWithText("Gerar texto").performClick()
        composeRule.onNode(hasSetTextAction()).assertExists()

        composeRule.onNodeWithText("Regras").performClick()
        composeRule.onNodeWithText("Livro de regras").assertIsDisplayed()
        composeRule.onNodeWithText("Sistemas").performClick()
        composeRule.onNodeWithText("Config").performClick()
        composeRule.onNodeWithText("Configuração").assertIsDisplayed()
        composeRule.onNodeWithText("Jogar").performClick()
        composeRule.onNodeWithText("A mesa ainda está vazia").assertIsDisplayed()
    }
}
