package dev.rpgsolo.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Q-03 / U-01: cada destino da barra de navegação tem descrição para TalkBack (R.string.navigation_label). */
@RunWith(AndroidJUnit4::class)
class NavigationAccessibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun cadaAbaTemDescricaoDeNavegacao() {
        for (aba in listOf("Jogar", "Heróis", "Campanhas", "Mais")) {
            composeRule.onNodeWithContentDescription("Navegação: $aba", useUnmergedTree = true).assertIsDisplayed()
        }
    }
}
