package dev.rpgsolo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.ai.RollView
import dev.rpgsolo.engine.Outcome
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Q-03 / U-04: o resultado só aparece depois de rolar, e o estado "revelada" vem de quem usa o cartão. */
@RunWith(AndroidJUnit4::class)
class RollCardRevealTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val view =
        RollView(
            label = "Perceber a passagem",
            sides = 6,
            dice = listOf(5),
            modifier = 0,
            total = 5,
            difficulty = 4,
            outcome = Outcome.SUCCESS,
            outcomeWord = "sucesso por pouco",
            margin = 1,
        )

    @Test
    fun rolagemNaoReveladaEscondeOResultado() {
        composeRule.setContent { RpgTheme { RollCard(view = view, revealed = false, onRevealed = {}) } }

        composeRule.onNodeWithText("Perceber a passagem").assertIsDisplayed()
        composeRule.onNodeWithText("Rolar dados", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("SUCESSO POR POUCO").assertDoesNotExist()
        composeRule.onNodeWithText("passou por 1").assertDoesNotExist()
    }

    @Test
    fun tocarEmRolarDadosRevelaOResultadoEEscondeOBotao() {
        var revealed by mutableStateOf(false)
        composeRule.setContent { RpgTheme { RollCard(view = view, revealed = revealed, onRevealed = { revealed = true }) } }

        composeRule.onNodeWithText("Rolar dados", substring = true).performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) { revealed }

        composeRule.onNodeWithText("SUCESSO POR POUCO").assertIsDisplayed()
        composeRule.onNodeWithText("passou por 1").assertIsDisplayed()
        composeRule.onNodeWithText("Rolar dados", substring = true).assertDoesNotExist()
    }

    @Test
    fun rolagemJaReveladaMostraOResultadoSemBotao() {
        composeRule.setContent { RpgTheme { RollCard(view = view, revealed = true, onRevealed = {}) } }

        composeRule.onNodeWithText("SUCESSO POR POUCO").assertIsDisplayed()
        composeRule.onNodeWithText("Rolar dados", substring = true).assertDoesNotExist()
    }
}
