package dev.rpgsolo.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ResilienceTest {
    private val req = AiRequest(listOf(AiMessage(Role.USER, "oi")))

    @Test fun esperaCresceEPassaTeto() {
        val p = RetryPolicy(maxAttempts = 5, baseDelayMs = 1000, maxDelayMs = 3000)
        assertEquals(listOf(1000L, 2000L, 3000L, 3000L), (1..4).map { p.delayBefore(it) })
    }

    @Test fun repeteErroTemporarioEDepoisConsegue() {
        val inner = ScriptedProvider(listOf(AiError.NoInternet(), AiError.ServerError(500), "ok"))
        val sleeper = RecordingSleeper()
        val r = RetryingProvider(inner, RetryPolicy(maxAttempts = 3), sleeper).complete(req)
        assertEquals("ok", r.text)
        assertEquals(listOf(1000L, 2000L), sleeper.waits)
        assertEquals(3, inner.requests.size)
    }

    @Test fun chaveInvalidaNaoRepete() {
        val inner = ScriptedProvider(listOf(AiError.InvalidKey(), "ok"))
        try {
            RetryingProvider(inner, sleeper = RecordingSleeper()).complete(req)
            fail()
        } catch (e: AiError.InvalidKey) {
        }
        assertEquals(1, inner.requests.size)
    }

    @Test fun desisteDepoisDoMaximo() {
        val inner = ScriptedProvider(listOf(AiError.NoInternet(), AiError.NoInternet()))
        try {
            RetryingProvider(inner, RetryPolicy(maxAttempts = 2), RecordingSleeper()).complete(req)
            fail()
        } catch (
            e: AiError.NoInternet,
        ) {
        }
        assertEquals(2, inner.requests.size)
    }

    @Test fun respeitaRetryAfterDoServidor() {
        val inner = ScriptedProvider(listOf(AiError.RateLimited(7000), "ok"))
        val sleeper = RecordingSleeper()
        RetryingProvider(inner, sleeper = sleeper).complete(req)
        assertEquals(listOf(7000L), sleeper.waits)
    }

    @Test fun limitadorFazExcedenteEsperar() {
        var now = 0L
        val sleeper = Sleeper { ms -> now += ms }
        val limiter = RateLimiter(2, 1000, clock = { now }, sleeper = sleeper)
        limiter.acquire()
        limiter.acquire()
        assertEquals(0L, now)
        limiter.acquire()
        assertTrue(now >= 1000L)
    }

    @Test fun usoSomaEEstimaQuandoFaltaNumero() {
        val tracker = UsageTracker()
        val inner = ScriptedProvider(listOf(AiResponse("abcd", AiUsage(100, 20)), "abcdefgh"))
        val p = UsageTrackingProvider(inner, tracker)
        p.complete(req)
        p.complete(req)
        assertEquals(2, tracker.calls)
        assertEquals(100L + TokenEstimator.estimate(req), tracker.inputTokens)
        assertEquals(22L, tracker.outputTokens)
        assertEquals(
            1.0,
            UsageTracker().also { it.record(AiUsage(1_000_000, 0), req, AiResponse("")) }
                .estimateCost(1.0, 5.0),
            1e-9,
        )
    }
}
