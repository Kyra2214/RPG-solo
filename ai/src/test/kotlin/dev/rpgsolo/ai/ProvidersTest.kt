package dev.rpgsolo.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ProvidersTest {
    private val cfg = ProviderConfig("https://api.exemplo.com/v1/chat/completions", "modelo-x", "sk-segredo-1234")
    private val req = AiRequest(listOf(AiMessage(Role.SYSTEM, "regras"), AiMessage(Role.USER, "oi")), jsonMode = true)

    private fun error(block: () -> Unit): AiError =
        try { block(); fail("deveria falhar"); throw IllegalStateException() } catch (e: AiError) { e }

    @Test fun chaveMascaradaNaConfig() {
        assertFalse("segredo" in cfg.toString())
        assertTrue(cfg.toString().contains("apiKey=••••••••1234"))
        assertTrue(cfg.toString().endsWith("cacheSystemPrompt=false)"))
        assertEquals("••••••••", maskKey("curta"))
    }

    @Test fun endpointExigeHttps() {
        try { ProviderConfig("http://x", "m", "k"); fail() } catch (e: IllegalArgumentException) { }
    }

    @Test fun openAiMontaPedidoELeResposta() {
        val t = FakeTransport(listOf(HttpReply(200,
            """{"choices":[{"message":{"content":"olá"}}],"usage":{"prompt_tokens":10,"completion_tokens":2}}""")))
        val r = OpenAiCompatibleProvider(cfg, t).complete(req)
        assertEquals("olá", r.text)
        assertEquals(AiUsage(10, 2), r.usage)
        assertEquals("Bearer sk-segredo-1234", t.lastHeaders["Authorization"])
        assertTrue("\"model\":\"modelo-x\"" in t.lastBody)
        assertTrue("json_object" in t.lastBody)
    }

    @Test fun anthropicSeparaSystemELeBlocosDeTexto() {
        val t = FakeTransport(listOf(HttpReply(200,
            """{"content":[{"type":"text","text":"a"},{"type":"text","text":"b"}],"usage":{"input_tokens":5,"output_tokens":1}}""")))
        val r = AnthropicProvider(cfg, t).complete(req)
        assertEquals("ab", r.text)
        assertEquals("sk-segredo-1234", t.lastHeaders["x-api-key"])
        assertTrue("\"system\":\"regras\"" in t.lastBody)
        assertFalse("\"role\":\"system\"" in t.lastBody)
    }

    @Test fun statusViraErroClaro() {
        fun p(status: Int, headers: Map<String, String> = emptyMap()) =
            OpenAiCompatibleProvider(cfg, FakeTransport(listOf(HttpReply(status, "{}", headers))))
        assertTrue(error { p(401).complete(req) } is AiError.InvalidKey)
        val limit = error { p(429, mapOf("retry-after" to "3")).complete(req) } as AiError.RateLimited
        assertEquals(3000L, limit.retryAfterMs)
        assertTrue(error { p(503).complete(req) } is AiError.ServerError)
        assertTrue(error { p(400).complete(req) } is AiError.BadRequest)
    }

    @Test fun respostaIlegivelNaoQuebra() {
        val p = OpenAiCompatibleProvider(cfg, FakeTransport(listOf(HttpReply(200, "não é json"))))
        assertTrue(error { p.complete(req) } is AiError.BadResponse)
        val q = OpenAiCompatibleProvider(cfg, FakeTransport(listOf(HttpReply(200, """{"choices":[]}"""))))
        assertTrue(error { q.complete(req) } is AiError.BadResponse)
    }
}
