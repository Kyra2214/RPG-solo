package dev.rpgsolo.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.MainActivity
import dev.rpgsolo.ai.RollView
import dev.rpgsolo.engine.Outcome
import org.junit.Assert.assertTrue
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
        composeRule.onNodeWithText("A mesa ainda está vazia").assertIsDisplayed()

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

    @Test
    fun cartaoDeRolagem_temDescricaoAcessivelEPodeSerCapturado() {
        val view =
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
                chance = 0.5,
            )
        composeRule.setContent {
            RpgTheme {
                RollCard(view = view, revealed = true, onRevealed = {})
            }
        }

        composeRule.onNodeWithText("Perceber a passagem").assertIsDisplayed()
        composeRule.onNodeWithText("sucesso por pouco").assertIsDisplayed()
        composeRule.onNodeWithText("chance de sucesso: 50%").assertIsDisplayed()
        val image = composeRule.onNodeWithText("Perceber a passagem").captureToImage()
        assertTrue("a captura do cartão deve ter dimensões válidas", image.width > 0 && image.height > 0)
    }
}
