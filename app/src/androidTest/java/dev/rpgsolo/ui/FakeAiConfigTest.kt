package dev.rpgsolo.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.rpgsolo.MainActivity
import dev.rpgsolo.ai.HttpReply
import dev.rpgsolo.ai.HttpTransport
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList

/** Transporte falso: nenhuma chamada sai do aparelho; guarda os corpos para conferência e responde no formato OpenAI. */
private class FakeTransport : HttpTransport {
    val bodies = CopyOnWriteArrayList<String>()

    override fun post(
        url: String,
        headers: Map<String, String>,
        body: String,
    ): HttpReply {
        bodies += body
        return HttpReply(200, """{"choices":[{"message":{"content":"OK"}}],"usage":{"prompt_tokens":3,"completion_tokens":1}}""")
    }
}

/**
 * Q-03: provedor de IA falso no app. Configura o provedor pela tela Config, testa a conexão e confirma que a chamada
 * passou pelo transporte falso (sem rede). Base para a jogada completa (campanha + rolagem), ainda pendente.
 */
@RunWith(AndroidJUnit4::class)
class FakeAiConfigTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun configurarProvedorEtestarConexaoUsaOTransporteFalso() {
        composeRule.openTab("Config")
        composeRule.onNodeWithText("Endpoint (https)").performScrollTo().performTextReplacement("https://fake.local/v1/chat/completions")
        composeRule.onNodeWithText("Modelo").performScrollTo().performTextReplacement("fake-model")
        composeRule.onNodeWithText("Chave de API").performScrollTo().performTextReplacement("chave-falsa")
        composeRule.onNodeWithText("Testar conexão").performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 10_000) { fake.bodies.isNotEmpty() }
        assertTrue("o corpo enviado deve citar o modelo falso", fake.bodies.first().contains("fake-model"))

        composeRule.onNodeWithText("Salvar e usar").performScrollTo().performClick()
        composeRule.onNodeWithText("Provedor ativo", substring = true).performScrollTo().assertIsDisplayed()
    }

    companion object {
        private val fake = FakeTransport()

        @BeforeClass
        @JvmStatic
        fun instalarTransporteFalso() {
            AiTestHooks.transport = fake
        }

        @AfterClass
        @JvmStatic
        fun removerTransporteFalso() {
            AiTestHooks.transport = null
        }
    }
}
