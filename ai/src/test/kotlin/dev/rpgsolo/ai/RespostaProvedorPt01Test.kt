package dev.rpgsolo.ai

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/** PT-01: leitura robusta da resposta do provedor (`extractChoiceText` e `OpenAiCompatibleProvider`). */
class RespostaProvedorPt01Test {
    private val cfg = ProviderConfig("https://api.exemplo.com/v1/chat/completions", "vendor/modelo-x:free", "sk-teste-1234")
    private val req = AiRequest(listOf(AiMessage(Role.USER, "oi")))

    private fun choice(json: String): JsonObject = Json.parseToJsonElement(json).jsonObject

    private fun badResponse(block: () -> Unit): AiError.BadResponse =
        try {
            block()
            fail("deveria falhar")
            throw IllegalStateException()
        } catch (e: AiError.BadResponse) {
            e
        }

    private fun responder(body: String): AiResponse =
        OpenAiCompatibleProvider(
            cfg,
            FakeTransport(listOf(HttpReply(200, body))),
        ).complete(req)

    @Test fun contentTextoSimples() {
        assertEquals("olá", extractChoiceText(choice("""{"message":{"content":"olá"}}"""), "m"))
    }

    @Test fun contentListaDePartesJuntaSoTexto() {
        val c =
            choice(
                """{"message":{"content":[{"type":"text","text":"Olá, "},{"type":"image_url","url":"x"},{"type":"output_text","text":"mundo"}]}}""",
            )
        assertEquals("Olá, mundo", extractChoiceText(c, "m"))
    }

    @Test fun contentNuloComReasoningComTerminoNormalUsaORaciocinio() {
        val c = choice("""{"message":{"content":null,"reasoning":"pensei e respondo OK"},"finish_reason":"stop"}""")
        assertEquals("pensei e respondo OK", extractChoiceText(c, "m"))
        val d = choice("""{"message":{"content":"","reasoning_content":"OK"}}""")
        assertEquals("OK", extractChoiceText(d, "m"))
    }

    @Test fun contentTemPrioridadeSobreOReasoning() {
        val c = choice("""{"message":{"content":"resposta","reasoning":"rascunho"},"finish_reason":"stop"}""")
        assertEquals("resposta", extractChoiceText(c, "m"))
    }

    @Test fun finishLengthSemTextoViraErroComOModelo() {
        val c = choice("""{"message":{"content":null,"reasoning":"ainda pensando"},"finish_reason":"length"}""")
        val e = badResponse { extractChoiceText(c, "vendor/raciocinio") }
        assertTrue("vendor/raciocinio" in e.message.orEmpty())
        assertTrue("limite de tokens" in e.message.orEmpty())
    }

    @Test fun recusaViraErroComMotivo() {
        val c = choice("""{"message":{"content":null,"refusal":"não posso ajudar"},"finish_reason":"stop"}""")
        val e = badResponse { extractChoiceText(c, "vendor/x") }
        assertTrue("recusou" in e.message.orEmpty() && "não posso ajudar" in e.message.orEmpty())
    }

    @Test fun semTextoNemRaciocinioViraRespostaVazia() {
        val e = badResponse { extractChoiceText(choice("""{"message":{"content":null},"finish_reason":"stop"}"""), "vendor/x") }
        assertTrue("vendor/x" in e.message.orEmpty())
        badResponse { extractChoiceText(choice("""{"message":{"content":[]}}"""), "m") }
    }

    @Test fun raciocinioComFinishDiferenteDeStopNaoServe() {
        badResponse { extractChoiceText(choice("""{"message":{"content":null,"reasoning":"x"},"finish_reason":"content_filter"}"""), "m") }
    }

    @Test fun semMessageOuEscolhaViraErro() {
        badResponse { extractChoiceText(null, "m") }
        badResponse { extractChoiceText(choice("""{"finish_reason":"stop"}"""), "m") }
    }

    @Test fun providerLeContentEmListaEUso() {
        val r =
            responder(
                """{"choices":[{"message":{"content":[{"type":"text","text":"ok"}]}}],"usage":{"prompt_tokens":3,"completion_tokens":1}}""",
            )
        assertEquals("ok", r.text)
        assertEquals(AiUsage(3, 1), r.usage)
    }

    @Test fun providerErroComStatus200NomeiaOModelo() {
        val e = badResponse { responder("""{"error":{"message":"modelo indisponível","code":502}}""") }
        assertTrue("vendor/modelo-x:free" in e.message.orEmpty() && "modelo indisponível" in e.message.orEmpty())
    }

    @Test fun providerSemChoicesNaoQuebra() {
        badResponse { responder("""{"choices":[]}""") }
        badResponse { responder("""{"choices":"x"}""") }
    }
}
