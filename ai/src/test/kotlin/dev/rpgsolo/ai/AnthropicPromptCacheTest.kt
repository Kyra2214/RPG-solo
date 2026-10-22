package dev.rpgsolo.ai

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnthropicPromptCacheTest {
    private class CapturingTransport : HttpTransport {
        var body: String = ""

        override fun post(
            url: String,
            headers: Map<String, String>,
            body: String,
        ): HttpReply {
            this.body = body
            return HttpReply(
                200,
                """{"content":[{"type":"text","text":"ok"}],"usage":{"input_tokens":10,"output_tokens":1,"cache_read_input_tokens":5,"cache_creation_input_tokens":2}}""",
            )
        }
    }

    private val request =
        AiRequest(
            messages =
                listOf(
                    AiMessage(Role.SYSTEM, "Prompt fixo do mestre"),
                    AiMessage(Role.USER, "Ação atual"),
                ),
        )

    @Test fun officialAnthropicEndpointUsesEphemeralCacheForSystemPrefix() {
        val transport = CapturingTransport()
        val config = ProviderConfig("https://api.anthropic.com/v1/messages", "modelo", "secret")
        assertTrue(config.cacheSystemPrompt)
        val response = AnthropicProvider(config, transport).complete(request)
        assertEquals(5, response.usage?.cacheReadInputTokens)
        assertEquals(2, response.usage?.cacheCreationInputTokens)
        val root = Json.parseToJsonElement(transport.body).jsonObject
        val system = root["system"]!!.jsonArray.single().jsonObject
        assertEquals("Prompt fixo do mestre", system["text"]!!.jsonPrimitive.content)
        assertEquals("ephemeral", system["cache_control"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    }

    @Test fun compatibleAnthropicEndpointDoesNotAssumeCacheSupport() {
        val transport = CapturingTransport()
        val config = ProviderConfig("https://proxy.example/v1/messages", "modelo", "secret")
        assertFalse(config.cacheSystemPrompt)
        AnthropicProvider(config, transport).complete(request)
        val root = Json.parseToJsonElement(transport.body).jsonObject
        assertEquals("Prompt fixo do mestre", root["system"]!!.jsonPrimitive.content)
    }
}
