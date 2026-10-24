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
        composeRule.waitForText("Ficha de Herói UI criada e salva.")
        composeRule.onNodeWithText("Ficha de Herói UI criada e salva.").assertIsDisplayed()

        // A tela Nova campanha exige provedor configurado; usa endpoint fictício sem rede externa.
        composeRule.openTab("Config")
        composeRule.onNodeWithText("Endpoint (https)").performScrollTo().performTextInput("https://fake.local/v1/chat/completions")
        composeRule.onNodeWithText("Modelo").performScrollTo().performTextInput("fake-model")
        composeRule.onNodeWithText("Chave de API").performScrollTo().performTextInput("chave-falsa")
        composeRule.onNodeWithText("Salvar e usar").performScrollTo().performClick()
        composeRule.onNodeWithText("Provedor ativo", substring = true).performScrollTo().assertIsDisplayed()

        composeRule.openTab("Campanhas")
        // O título "Campanhas" também é o rótulo da aba (dois nós iguais): confere o conteúdo da tela.
        composeRule.onNodeWithText("Nova campanha").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Descreva a campanha (sistema, cenário, tom, dificuldade…)").performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithText("Jogar").performClick()
        composeRule.onNodeWithText("A mesa").assertIsDisplayed()
    }
}
