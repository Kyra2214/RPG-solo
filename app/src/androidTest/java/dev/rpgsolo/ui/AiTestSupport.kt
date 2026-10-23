package dev.rpgsolo.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.rpgsolo.ai.HttpReply
import dev.rpgsolo.ai.HttpTransport
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

/** Abre uma aba pelo nome: direto na barra/trilho ou, no layout estreito, pelo menu "Mais" (U-06.2). */
fun ComposeTestRule.openTab(label: String) {
    if (onAllNodesWithText(label).fetchSemanticsNodes().isEmpty()) onNodeWithText("Mais").performClick()
    onNodeWithText(label).performClick()
}

/** Aguarda o bootstrap assíncrono do AppModel antes de interagir com as telas. */
fun ComposeTestRule.waitForAppReady(timeoutMillis: Long = 15_000) {
    waitUntil(timeoutMillis) {
        onAllNodesWithText("Preparar sua mesa").fetchSemanticsNodes().isNotEmpty() ||
            onAllNodesWithText("Não foi possível abrir os dados:", substring = true).fetchSemanticsNodes().isNotEmpty()
    }
    check(onAllNodesWithText("Preparar sua mesa").fetchSemanticsNodes().isNotEmpty()) {
        "O app exibiu erro de inicialização; consulte a exceção AppModel no logcat do E2E."
    }
}

/** Resposta no formato OpenAI com o texto dado (o JSON é montado por org.json, sem escapes manuais). */
fun openAiReply(content: String): HttpReply =
    HttpReply(
        200,
        JSONObject()
            .put("choices", JSONArray().put(JSONObject().put("message", JSONObject().put("content", content))))
            .put("usage", JSONObject().put("prompt_tokens", 3).put("completion_tokens", 1))
            .toString(),
    )

/** Transporte falso por papel: interpretador de campanha, classificador de intenção e narrador, reconhecidos pelo prompt. */
class ScriptedTransport(
    private val classifiedCheck: String? = "sorte",
    private val narrations: List<String> = listOf("A porta range e cede.", "A pedra rola e o caminho se abre."),
) : HttpTransport {
    val bodies = CopyOnWriteArrayList<String>()
    private var narrationIndex = 0

    override fun post(
        url: String,
        headers: Map<String, String>,
        body: String,
    ): HttpReply {
        bodies += body
        return when {
            body.contains("Você converte a descrição de uma campanha") ->
                openAiReply(
                    JSONObject()
                        .put("system", "proprio")
                        .put("edition", "sistema-proprio")
                        .put("difficulty", "difficulty.normal")
                        .toString(),
                )
            body.contains("Você classifica ações de um jogador") ->
                openAiReply(JSONObject().put("check", classifiedCheck ?: JSONObject.NULL).toString())
            else -> {
                val text = narrations[minOf(synchronized(this) { narrationIndex++ }, narrations.lastIndex)]
                openAiReply(
                    JSONObject()
                        .put("narrative", text)
                        .put("proposals", JSONArray())
                        .put("suggestions", JSONArray())
                        .toString(),
                )
            }
        }
    }
}
