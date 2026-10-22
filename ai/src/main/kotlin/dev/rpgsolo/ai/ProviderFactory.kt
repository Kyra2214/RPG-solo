package dev.rpgsolo.ai

enum class ProviderKind(val label: String) {
    OPENAI_COMPATIBLE("OpenAI-compatível"),
    ANTHROPIC("Anthropic"),
}

/** Monta o provedor pronto para uso: fila de limite + retentativas + contagem de uso (6.1.4, 6.2). */
object ProviderFactory {
    fun build(
        kind: ProviderKind,
        config: ProviderConfig,
        usage: UsageTracker? = null,
        transport: HttpTransport = JavaNetTransport(),
        maxRequestsPerMinute: Int = 20,
        limiter: RateLimiter? = null,
    ): AiProvider {
        val base: AiProvider =
            when (kind) {
                ProviderKind.OPENAI_COMPATIBLE -> OpenAiCompatibleProvider(config, transport)
                ProviderKind.ANTHROPIC -> AnthropicProvider(config, transport)
            }
        val tracked = if (usage != null) UsageTrackingProvider(base, usage) else base
        val limited = RateLimitedProvider(tracked, limiter ?: RateLimiter(maxRequestsPerMinute, 60_000))
        return RetryingProvider(limited)
    }

    /**
     * 6.4.1/6.4.2: três papéis sobre o mesmo endpoint e chave, mudando só o modelo. Modelo em branco = usa o principal.
     * Fila, retentativas e contagem de uso valem para os três.
     */
    fun buildRoles(
        kind: ProviderKind,
        config: ProviderConfig,
        cheapModel: String? = null,
        premiumModel: String? = null,
        usage: UsageTracker? = null,
        transport: HttpTransport = JavaNetTransport(),
        maxRequestsPerMinute: Int = 20,
    ): AiRoles {
        val endpointLimiter = RateLimiter(maxRequestsPerMinute, 60_000)

        fun make(model: String?) =
            build(
                kind,
                if (model.isNullOrBlank() || model.trim() == config.model) config else config.copy(model = model.trim()),
                usage,
                transport,
                maxRequestsPerMinute,
                endpointLimiter,
            )
        val narrator = make(null)
        val cheap = if (cheapModel.isNullOrBlank() || cheapModel.trim() == config.model) narrator else make(cheapModel)
        val premium = if (premiumModel.isNullOrBlank() || premiumModel.trim() == config.model) narrator else make(premiumModel)
        return AiRoles(cheap, narrator, premium)
    }
}
