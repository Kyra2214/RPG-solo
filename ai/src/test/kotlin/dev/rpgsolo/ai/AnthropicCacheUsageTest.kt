package dev.rpgsolo.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class AnthropicCacheUsageTest {
    private class SseTransport : HttpTransport {
        override fun post(url: String, headers: Map<String, String>, body: String): HttpReply = HttpReply(
            200,
            listOf(
                """event: message_start""",
                """data: {"type":"message_start","message":{"usage":{"input_tokens":20,"output_tokens":1,"cache_read_input_tokens":500,"cache_creation_input_tokens":50}}}""",
                """event: content_block_delta""",
                """data: {"type":"content_block_delta","delta":{"type":"text_delta","text":"hello"}}""",
                """event: message_delta""",
                """data: {"type":"message_delta","usage":{"output_tokens":12}}""",
                """event: message_stop""",
                """data: {"type":"message_stop"}"""
            ).joinToString("\n")
        )
    }

    @Test fun streamingCapturesCacheAndFinalOutputUsage() {
        val provider = AnthropicProvider(
            ProviderConfig("https://api.anthropic.com/v1/messages", "model", "secret"),
            SseTransport()
        )
        val response = provider.stream(
            AiRequest(listOf(AiMessage(Role.SYSTEM, "static"), AiMessage(Role.USER, "dynamic"))),
            {}
        )
        assertEquals("hello", response.text)
        assertEquals(20, response.usage?.inputTokens)
        assertEquals(12, response.usage?.outputTokens)
        assertEquals(500, response.usage?.cacheReadInputTokens)
        assertEquals(50, response.usage?.cacheCreationInputTokens)
    }

    @Test fun trackerSeparatesCacheTokensForPricing() {
        val tracker = UsageTracker()
        tracker.record(
            AiUsage(inputTokens = 100, outputTokens = 20, cacheReadInputTokens = 500, cacheCreationInputTokens = 50),
            AiRequest(listOf(AiMessage(Role.USER, "tiny"))),
            AiResponse("ok")
        )
        assertEquals(650L, tracker.inputTokens)
        assertEquals(500L, tracker.cacheReadInputTokens)
        assertEquals(50L, tracker.cacheCreationInputTokens)
        assertEquals(0.00215, tracker.estimateCost(10.0, 20.0, 1.0, 5.0), 0.000000001)
    }
}
