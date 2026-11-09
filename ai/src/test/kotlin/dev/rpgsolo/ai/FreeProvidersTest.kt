package dev.rpgsolo.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FreeProvidersTest {
    private val openRouterBody =
        """
        {"data":[
          {"id":"meta-llama/llama-3.3-70b-instruct:free","context_length":65536,
           "pricing":{"prompt":"0","completion":"0"},"architecture":{"output_modalities":["text"]},
           "supported_parameters":["temperature","response_format"]},
          {"id":"google/gemma-3-27b-it:free","context_length":96000,
           "pricing":{"prompt":"0","completion":"0"},"architecture":{"output_modalities":["text"]},
           "supported_parameters":["temperature"]},
          {"id":"vendor/modelo-zero-sem-sufixo","context_length":8000,
           "pricing":{"prompt":"0","completion":"0"},"architecture":{"output_modalities":["text"]},
           "supported_parameters":["response_format"]},
          {"id":"vendor/pago","context_length":128000,
           "pricing":{"prompt":"0.000002","completion":"0.000006"},"architecture":{"output_modalities":["text"]}},
          {"id":"vendor/imagem:free","pricing":{"prompt":"0","completion":"0"},
           "architecture":{"output_modalities":["image","text"]}},
          {"id":"meta-llama/llama-guard-3-8b:free","pricing":{"prompt":"0","completion":"0"},
           "architecture":{"output_modalities":["text"]}},
          {"id":"deepseek/deepseek-r1:free","context_length":163000,
           "pricing":{"prompt":"0","completion":"0"},"architecture":{"output_modalities":["text"]},
           "supported_parameters":["response_format"]}
        ]}
        """.trimIndent()

    @Test fun openRouterFiltraSoGratuitosDeTextoERankeia() {
        val ids = ModelDiscovery.parse(ProviderPreset.OPENROUTER, openRouterBody).map { it.id }
        assertEquals(
            listOf(
                "meta-llama/llama-3.3-70b-instruct:free",
                "vendor/modelo-zero-sem-sufixo",
                "deepseek/deepseek-r1:free",
                "google/gemma-3-27b-it:free",
            ),
            ids,
        )
    }

    @Test fun groqIgnoraAudioGuardaEInativos() {
        val body =
            """
            {"data":[
              {"id":"llama-3.3-70b-versatile","active":true,"context_window":131072},
              {"id":"llama-3.1-8b-instant","active":true,"context_window":131072},
              {"id":"groq/compound","active":true,"context_window":131072},
              {"id":"whisper-large-v3","active":true,"context_window":448},
              {"id":"meta-llama/llama-guard-4-12b","active":true,"context_window":131072},
              {"id":"modelo-velho-70b","active":false,"context_window":8192}
            ]}
            """.trimIndent()
        val ids = ModelDiscovery.parse(ProviderPreset.GROQ, body).map { it.id }
        assertEquals(listOf("llama-3.3-70b-versatile", "llama-3.1-8b-instant", "groq/compound"), ids)
    }

    @Test fun listaIlegivelViraBadResponse() {
        try {
            ModelDiscovery.parse(ProviderPreset.GROQ, "não é json")
            fail("deveria falhar")
        } catch (e: AiError.BadResponse) {
            assertTrue(e.message.orEmpty().contains("ilegível"))
        }
    }

    @Test fun fetchChamaOEndpointDeModelosComAChave() {
        val transport = GetTransport(HttpReply(200, openRouterBody))
        val models = ModelDiscovery.fetch(ProviderPreset.OPENROUTER, " sk-or-abc ", transport)
        assertEquals("https://openrouter.ai/api/v1/models", transport.lastGetUrl)
        assertEquals("Bearer sk-or-abc", transport.lastGetHeaders["Authorization"])
        assertFalse(models.isEmpty())
    }

    @Test fun fetchComChaveRecusadaViraInvalidKey() {
        val transport = GetTransport(HttpReply(401, "{}"))
        try {
            ModelDiscovery.fetch(ProviderPreset.GROQ, "x", transport)
            fail("deveria falhar")
        } catch (e: AiError.InvalidKey) {
            // esperado
        }
    }

    @Test fun modeloEsgotadoPassaParaOProximoEFicaDeMolho() {
        val a = ScriptedProvider(listOf(AiError.RateLimited(), AiError.RateLimited()))
        val b = ScriptedProvider(listOf("resposta-b", "resposta-b2"))
        var now = 0L
        val provider = FallbackProvider(listOf("a" to a, "b" to b), clock = { now })

        assertEquals("resposta-b", provider.complete(simpleRequest()).text)
        assertEquals("b", provider.lastModel)
        assertEquals(1, a.requests.size)

        now += 60_000L
        assertEquals("resposta-b2", provider.complete(simpleRequest()).text)
        assertEquals("o modelo esgotado não deve ser chamado de novo antes do tempo", 1, a.requests.size)
    }

    @Test fun modeloVoltaDepoisDoTempoDeEspera() {
        val a = ScriptedProvider(listOf(AiError.ServerError(503), "resposta-a"))
        val b = ScriptedProvider(listOf("resposta-b"))
        var now = 0L
        val provider = FallbackProvider(listOf("a" to a, "b" to b), clock = { now }, serverCooldownMs = 1_000L)

        assertEquals("resposta-b", provider.complete(simpleRequest()).text)
        now += 2_000L
        assertEquals("resposta-a", provider.complete(simpleRequest()).text)
        assertEquals("a", provider.lastModel)
    }

    @Test fun modeloIndisponivelOuRecusaPassaParaOProximo() {
        val a = ScriptedProvider(listOf(AiError.BadRequest("HTTP 404 model not found")))
        val b = ScriptedProvider(listOf("ok"))
        val provider = FallbackProvider(listOf("a" to a, "b" to b))
        assertEquals("ok", provider.complete(simpleRequest()).text)
    }

    @Test fun chaveInvalidaNaoTrocaDeModelo() {
        val a = ScriptedProvider(listOf(AiError.InvalidKey()))
        val b = ScriptedProvider(listOf("não deve ser usado"))
        val provider = FallbackProvider(listOf("a" to a, "b" to b))
        try {
            provider.complete(simpleRequest())
            fail("deveria falhar")
        } catch (e: AiError.InvalidKey) {
            assertTrue(b.requests.isEmpty())
        }
    }

    @Test fun semInternetNaoTrocaDeModelo() {
        val a = ScriptedProvider(listOf(AiError.NoInternet()))
        val b = ScriptedProvider(listOf("não deve ser usado"))
        val provider = FallbackProvider(listOf("a" to a, "b" to b))
        try {
            provider.complete(simpleRequest())
            fail("deveria falhar")
        } catch (e: AiError.NoInternet) {
            assertTrue(b.requests.isEmpty())
        }
    }

    @Test fun todosEsgotadosAvisaLimiteSemGastarRequisicoes() {
        val a = ScriptedProvider(listOf(AiError.RateLimited()))
        val b = ScriptedProvider(listOf(AiError.RateLimited()))
        var now = 0L
        val provider = FallbackProvider(listOf("a" to a, "b" to b), clock = { now })
        try {
            provider.complete(simpleRequest())
            fail("deveria falhar")
        } catch (e: AiError.RateLimited) {
            // todos falharam na primeira volta
        }
        now += 1_000L
        try {
            provider.complete(simpleRequest())
            fail("deveria falhar")
        } catch (e: AiError.RateLimited) {
            assertEquals(1, a.requests.size)
            assertEquals(1, b.requests.size)
        }
    }

    @Test fun streamSoTrocaDeModeloAntesDeComecarATexto() {
        val failsBefore = ScriptedProvider(listOf(AiError.RateLimited()))
        val ok = ScriptedProvider(listOf("texto completo"))
        val chunks = mutableListOf<String>()
        val provider = FallbackProvider(listOf("a" to failsBefore, "b" to ok))
        assertEquals("texto completo", provider.stream(simpleRequest()) { chunks += it }.text)
        assertEquals(listOf("texto completo"), chunks)

        val partial = PartialThenErrorProvider()
        val other = ScriptedProvider(listOf("não deve duplicar"))
        val guarded = FallbackProvider(listOf("a" to partial, "b" to other))
        try {
            guarded.stream(simpleRequest()) { }
            fail("deveria falhar")
        } catch (e: AiError.ServerError) {
            assertTrue(other.requests.isEmpty())
        }
    }

    @Test fun fabricaGratuitaContinuaNoProximoModeloQuandoOPrimeiroEsgota() {
        val cfg = ProviderConfig("https://openrouter.ai/api/v1/chat/completions", "modelo-a:free", "sk-or-teste")
        val transport =
            FakeTransport(
                listOf(
                    HttpReply(429, "{}"),
                    HttpReply(200, """{"choices":[{"message":{"content":"narração"}}]}"""),
                ),
            )
        val roles = ProviderFactory.buildFreeFallback(cfg, listOf("modelo-b:free", "modelo-a:free"), transport = transport)
        assertEquals("narração", roles.narrator.complete(simpleRequest()).text)
        assertTrue(transport.lastBody.contains("modelo-b:free"))
        assertFalse(roles.hasPremium)
    }

    @Test fun limitadorContaCadaTentativaNaCadeiaDeFallback() {
        var now = 0L
        val limiter = RateLimiter(1, 60_000L, clock = { now }, sleeper = Sleeper { delay -> now += delay })
        val cfg = ProviderConfig("https://openrouter.ai/api/v1/chat/completions", "modelo-a:free", "sk-or-teste")
        val transport =
            FakeTransport(
                listOf(
                    HttpReply(429, "{}"),
                    HttpReply(200, """{"choices":[{"message":{"content":"narração"}}]}"""),
                ),
            )

        val roles = ProviderFactory.buildFreeFallback(cfg, listOf("modelo-b:free"), transport = transport, limiter = limiter)

        assertEquals("narração", roles.narrator.complete(simpleRequest()).text)
        assertEquals(60_000L, now)
    }

    private fun simpleRequest() = AiRequest(messages = listOf(AiMessage(Role.USER, "oi")), maxTokens = 16)

    private class GetTransport(private val reply: HttpReply) : HttpTransport {
        var lastGetUrl = ""
        var lastGetHeaders: Map<String, String> = emptyMap()

        override fun post(
            url: String,
            headers: Map<String, String>,
            body: String,
        ): HttpReply = error("não usado")

        override fun get(
            url: String,
            headers: Map<String, String>,
        ): HttpReply {
            lastGetUrl = url
            lastGetHeaders = headers
            return reply
        }
    }

    private class PartialThenErrorProvider : AiProvider {
        override val name = "parcial"

        override fun complete(request: AiRequest): AiResponse = throw AiError.ServerError(500)

        override fun stream(
            request: AiRequest,
            onChunk: (String) -> Unit,
        ): AiResponse {
            onChunk("começo")
            throw AiError.ServerError(500)
        }
    }
}
