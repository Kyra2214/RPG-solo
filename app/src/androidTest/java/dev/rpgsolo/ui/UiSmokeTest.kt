package dev.rpgsolo.ui

import androidx.compose.ui.test.assertIsDisplayed
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

/** Q-03: fumaça visual mínima das três telas prioritárias e do cartão de dados. */
@RunWith(AndroidJUnit4::class)
class UiSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun telasPrioritarias_navegamEExibemConteudoPrincipal() {
        composeRule.waitForAppReady()
        composeRule.onNodeWithText("Preparar sua mesa").assertIsDisplayed()

        composeRule.onNodeWithText("Heróis").performClick()
        composeRule.onNodeWithText("Personagens").assertIsDisplayed()
        composeRule.onNodeWithText("Ponto de partida").assertIsDisplayed()
        composeRule.onNodeWithText("Nome").performTextInput("Herói UI")
        composeRule.onNodeWithText("Criar ficha").performScrollTo().performClick()
        composeRule.onNodeWithText("Ficha de Herói UI criada e salva.").assertIsDisplayed()

        composeRule.onNodeWithText("Campanhas").performClick()
        composeRule.onNodeWithText("Campanhas").assertIsDisplayed()
        composeRule.onNodeWithText("Nova campanha").assertIsDisplayed()
        composeRule.onNodeWithText("Descreva a campanha (sistema, cenário, tom, dificuldade…)").assertIsDisplayed()

        composeRule.onNodeWithText("Jogar").performClick()
        composeRule.onNodeWithText("A mesa").assertIsDisplayed()
    }
}
