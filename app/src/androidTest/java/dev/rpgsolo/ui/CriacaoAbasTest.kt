package dev.rpgsolo.ui

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** CG-4.10: fumaça da criação por abas (aba padrão, navegação entre etapas, sorteio de nome e Modo avançado). */
@RunWith(AndroidJUnit4::class)
class CriacaoAbasTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun criacaoPorAbas_eAPadrao_ENavegaAteInformacoes() {
        composeRule.waitForAppReady()
        composeRule.onNodeWithText("Heróis").performClick()
        composeRule.onNodeWithText("Criação por abas").assertIsDisplayed()
        composeRule.onNodeWithText("Informações", substring = true).performClick()
        composeRule.onNodeWithText("Falta o nome do herói.").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun sortearNome_preencheONome() {
        composeRule.waitForAppReady()
        composeRule.onNodeWithText("Heróis").performClick()
        composeRule.onNodeWithText("Informações", substring = true).performClick()
        composeRule.onNodeWithText("Sortear nome").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Falta o nome do herói.").assertDoesNotExist()
    }

    @Test
    fun modoAvancado_continuaDisponivel() {
        composeRule.waitForAppReady()
        composeRule.onNodeWithText("Heróis").performClick()
        composeRule.onNodeWithText("Modo avançado").performClick()
        composeRule.onNodeWithText("Ponto de partida").assertIsDisplayed()
    }
}

