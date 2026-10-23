package dev.rpgsolo.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.ai.RollView
import dev.rpgsolo.engine.Outcome
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Smoke visual isolado para o cartão de dados, sem inicializar a MainActivity. */
@RunWith(AndroidJUnit4::class)
class RollCardRenderingTest {
    @get:Rule
    val composeRule = createComposeRule()

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
        composeRule.onNodeWithText("SUCESSO POR POUCO").assertIsDisplayed()
        composeRule.onNodeWithText("chance de sucesso: 50%").assertIsDisplayed()
        val image = composeRule.onNodeWithText("Perceber a passagem").captureToImage()
        assertTrue("a captura do cartão deve ter dimensões válidas", image.width > 0 && image.height > 0)
    }
}
