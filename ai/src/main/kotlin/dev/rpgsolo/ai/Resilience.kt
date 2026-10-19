package dev.rpgsolo.ai

import kotlin.math.pow

/** Espera injetável: nos testes não dorme de verdade. */
fun interface Sleeper {
    fun sleep(ms: Long)
}

object ThreadSleeper : Sleeper {
    override fun sleep(ms: Long) {
        Thread.sleep(ms)
    }
}

/** Retentativas com espera crescente (6.2.2): 1s, 2s, 4s... até [maxDelayMs]. */
data class RetryPolicy(
    val maxAttempts: Int = 3,
    val baseDelayMs: Long = 1_000,
    val factor: Double = 2.0,
    val maxDelayMs: Long = 30_000
) {
    init {
        require(maxAttempts >= 1) { "maxAttempts deve ser >= 1" }
        require(baseDelayMs >= 0 && factor >= 1.0 && maxDelayMs >= baseDelayMs) { "política de espera inválida" }
    }

    /** Espera antes da retentativa [retryNumber] (1 = primeira retentativa). */
    fun delayBefore(retryNumber: Int): Long =
        minOf(maxDelayMs, (baseDelayMs * factor.pow(retryNumber - 1)).toLong())
}

/** Repete só erros que valem a pena repetir (sem internet, limite, falha do servidor). Chave inválida falha na hora. */
class RetryingProvider(
    private val inner: AiProvider,
    private val policy: RetryPolicy = RetryPolicy(),
    private val sleeper: Sleeper = ThreadSleeper
) : AiProvider {
    override val name: String get() = inner.name

    override fun complete(request: AiRequest): AiResponse = retry { inner.complete(request) }

    override fun stream(request: AiRequest, onChunk: (String) -> Unit): AiResponse {
        var attempt = 1
        var emitted = false
        while (true) {
            try {
                return inner.stream(request) { chunk ->
                    emitted = true
                    onChunk(chunk)
                }
            } catch (e: AiError) {
                if (!e.retryable || emitted || attempt >= policy.maxAttempts) throw e
                val serverWait = (e as? AiError.RateLimited)?.retryAfterMs
                if (serverWait != null && serverWait > policy.maxDelayMs) throw e
                sleeper.sleep(serverWait ?: policy.delayBefore(attempt))
                attempt++
            }
        }
    }

    private fun retry(operation: () -> AiResponse): AiResponse {
        var attempt = 1
        while (true) {
            try {
                return operation()
            } catch (e: AiError) {
                if (!e.retryable || attempt >= policy.maxAttempts) throw e
                val serverWait = (e as? AiError.RateLimited)?.retryAfterMs
                if (serverWait != null && serverWait > policy.maxDelayMs) throw e
                sleeper.sleep(serverWait ?: policy.delayBefore(attempt))
                attempt++
            }
        }
    }
}

/** No máximo [maxRequests] a cada [windowMs]; o excedente espera na fila (6.2.1). */
class RateLimiter(
    private val maxRequests: Int,
    private val windowMs: Long,
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val sleeper: Sleeper = ThreadSleeper
) {
    init {
        require(maxRequests >= 1 && windowMs > 0) { "limite inválido" }
    }

    private val stamps = ArrayDeque<Long>()

    fun acquire() {
        while (true) {
            val wait = synchronized(this) {
                val now = clock()
                while (stamps.isNotEmpty() && now - stamps.first() >= windowMs) stamps.removeFirst()
                if (stamps.size < maxRequests) {
                    stamps.addLast(now)
                    0L
                } else {
                    windowMs - (now - stamps.first())
                }
            }
            if (wait <= 0L) return
            sleeper.sleep(wait)
        }
    }
}

class RateLimitedProvider(private val inner: AiProvider, private val limiter: RateLimiter) : AiProvider {
    override val name: String get() = inner.name

    override fun complete(request: AiRequest): AiResponse {
        limiter.acquire()
        return inner.complete(request)
    }

    override fun stream(request: AiRequest, onChunk: (String) -> Unit): AiResponse {
        limiter.acquire()
        return inner.stream(request, onChunk)
    }
}

/** Soma de uso (6.2.4). Usa os números do provedor; se faltarem, estima pelo tamanho do texto. */
class UsageTracker {
    var calls = 0
        private set
    /** Total de entrada, incluindo tokens lidos/criados no cache. */
    var inputTokens = 0L
        private set
    var outputTokens = 0L
        private set
    var cacheReadInputTokens = 0L
        private set
    var cacheCreationInputTokens = 0L
        private set
    private var uncachedInputTokens = 0L

    @Synchronized
    fun record(usage: AiUsage?, request: AiRequest, response: AiResponse) {
        calls++
        if (usage == null) {
            val estimated = TokenEstimator.estimate(request).toLong()
            uncachedInputTokens += estimated
            inputTokens += estimated
        } else {
            val uncached = usage.inputTokens.coerceAtLeast(0)
            val cachedRead = usage.cacheReadInputTokens.coerceAtLeast(0)
            val cachedCreation = usage.cacheCreationInputTokens.coerceAtLeast(0)
            uncachedInputTokens += uncached
            cacheReadInputTokens += cachedRead
            cacheCreationInputTokens += cachedCreation
            inputTokens += uncached.toLong() + cachedRead.toLong() + cachedCreation.toLong()
        }
        outputTokens += (usage?.outputTokens ?: TokenEstimator.estimate(response.text)).coerceAtLeast(0)
    }

    /** Estimativa conservadora: sem preços específicos, tokens em cache usam o preço normal de entrada. */
    fun estimateCost(inputPerMillion: Double, outputPerMillion: Double): Double =
        estimateCost(inputPerMillion, outputPerMillion, inputPerMillion, inputPerMillion)

    /** Preços por milhão informados pelo usuário; cache read e cache creation podem ter tarifas diferentes. */
    fun estimateCost(
        inputPerMillion: Double,
        outputPerMillion: Double,
        cacheReadPerMillion: Double,
        cacheCreationPerMillion: Double
    ): Double =
        uncachedInputTokens / 1_000_000.0 * inputPerMillion +
            cacheReadInputTokens / 1_000_000.0 * cacheReadPerMillion +
            cacheCreationInputTokens / 1_000_000.0 * cacheCreationPerMillion +
            outputTokens / 1_000_000.0 * outputPerMillion
}

class UsageTrackingProvider(private val inner: AiProvider, private val tracker: UsageTracker) : AiProvider {
    override val name: String get() = inner.name

    override fun complete(request: AiRequest): AiResponse =
        inner.complete(request).also { tracker.record(it.usage, request, it) }

    override fun stream(request: AiRequest, onChunk: (String) -> Unit): AiResponse =
        inner.stream(request, onChunk).also { tracker.record(it.usage, request, it) }
}
