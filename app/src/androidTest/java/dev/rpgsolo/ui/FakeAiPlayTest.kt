package dev.rpgsolo.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.MainActivity
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Q-03.3: jogada completa pela interface com IA falsa: provedor, ficha, campanha interpretada, ação do jogador,
 * teste escolhido pelo classificador, rolagem e narração do desfecho. Nenhuma chamada sai do aparelho.
 */
@RunWith(AndroidJUnit4::class)
class FakeAiPlayTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun jogadaCompletaComRolagemEnarracao() {
        composeRule.waitForAppReady()
        // Provedor
        composeRule.openTab("Config")
        composeRule.onNodeWithText("Endpoint (https)").performScrollTo().performTextReplacement("https://fake.local/v1/chat/completions")
        composeRule.onNodeWithText("Modelo").performScrollTo().performTextReplacement("fake-model")
        composeRule.onNodeWithText("Chave de API").performScrollTo().performTextReplacement("chave-falsa")
        composeRule.onNodeWithText("Salvar e usar").performScrollTo().performClick()
        composeRule.onNodeWithText("Provedor ativo", substring = true).performScrollTo().assertIsDisplayed()

        // Ficha: usar um exemplo válido do pacote para que o motor aceite a ficha ao iniciar a campanha.
        composeRule.onNodeWithText("Heróis").performClick()
        composeRule.onNodeWithText("Guerreiro de exemplo").performScrollTo().performClick()
        composeRule.onNodeWithText("Nome").performTextReplacement("Herói IA")
        composeRule.onNodeWithText("Criar ficha").performScrollTo().performClick()
        composeRule.waitForText("Ficha de Herói IA criada e salva.")
        composeRule.onNodeWithText("Ficha de Herói IA criada e salva.").assertIsDisplayed()

        // Campanha
        composeRule.onNodeWithText("Campanhas").performClick()
        composeRule.onNodeWithText("Descreva a campanha (sistema, cenário, tom, dificuldade…)")
            .performScrollTo()
            .performTextInput("aventura de teste no sistema próprio")
        composeRule.onNodeWithText("Interpretar").performScrollTo().performClick()
        composeRule.waitForText("Herói IA", timeoutMillis = 25_000)
        composeRule.onNodeWithText("Herói IA").performScrollTo().performClick()
        composeRule.onNodeWithText("Nome da campanha").performScrollTo().performTextInput("Campanha IA")
        composeRule.onNodeWithText("Começar").performScrollTo().performClick()
        // O início ativa a sessão e navega direto para a tela de jogo; Loja é um elemento já visível.
        composeRule.waitUntil(timeoutMillis = 25_000) {
            composeRule.onAllNodesWithText("Loja").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Loja").assertIsDisplayed()

        // Jogada: ação -> teste classificado -> rolagem -> narração
        composeRule.onNodeWithText("Jogar").performClick()
        composeRule.onNodeWithText("O que você faz?").performTextInput("tento arrombar a porta")
        composeRule.onNodeWithText("Enviar").performClick()
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText("A porta range e cede.", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue("o narrador falso deve ter sido chamado", transport.bodies.any { it.contains("tento arrombar a porta") })
    }

    companion object {
        private val transport = ScriptedTransport()

        @BeforeClass
        @JvmStatic
        fun instalarTransporteFalso() {
            AiTestHooks.transport = transport
        }

        @AfterClass
        @JvmStatic
        fun removerTransporteFalso() {
            AiTestHooks.transport = null
        }
    }
}
