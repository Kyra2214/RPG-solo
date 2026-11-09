package dev.rpgsolo.ai

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/** Provedores com camada gratuita já configurados: o usuário só cola a chave. Ambos falam o formato OpenAI. */
enum class ProviderPreset(
    val label: String,
    val chatEndpoint: String,
    val modelsEndpoint: String,
    val keySite: String,
    val keyPrefix: String,
) {
    OPENROUTER(
        "OpenRouter (grátis)",
        "https://openrouter.ai/api/v1/chat/completions",
        "https://openrouter.ai/api/v1/models",
        "openrouter.ai/keys",
        "sk-or-",
    ),
    GROQ(
        "Groq (grátis)",
        "https://api.groq.com/openai/v1/chat/completions",
        "https://api.groq.com/openai/v1/models",
        "console.groq.com/keys",
        "gsk_",
    ),
    ;

    companion object {
        fun fromName(name: String?): ProviderPreset? = values().firstOrNull { it.name == name }
    }
}

/** Modelo de chat gratuito encontrado na lista do provedor. */
data class DiscoveredModel(
    val id: String,
    val contextLength: Int? = null,
    val supportsJson: Boolean = false,
)

/** Descobre os modelos gratuitos de texto e devolve do mais indicado para o menos indicado. */
object ModelDiscovery {
    private val notChat = listOf("whisper", "tts", "playai", "guard", "embed", "orpheus", "transcri", "moderation")
    private val reasoning = listOf("r1", "reason", "thinking", "qwq", "think")
    private val sizeRegex = Regex("""(?<![a-z0-9])(\d+(?:\.\d+)?)b(?![a-z])""")

    fun fetch(
        preset: ProviderPreset,
        apiKey: String,
        transport: HttpTransport,
    ): List<DiscoveredModel> {
        val reply =
            transport.get(
                preset.modelsEndpoint,
                mapOf("Authorization" to "Bearer ${apiKey.trim()}", "Accept" to "application/json"),
            )
        checkHttp(reply)
        return parse(preset, reply.body)
    }

    fun parse(
        preset: ProviderPreset,
        body: String,
    ): List<DiscoveredModel> {
        val data: JsonArray =
            try {
                Json.parseToJsonElement(body).jsonObject["data"]?.jsonArray
            } catch (e: Exception) {
                null
            } ?: throw AiError.BadResponse("lista de modelos ilegível")
        val found =
            data.mapNotNull { element ->
                val obj = element as? JsonObject ?: return@mapNotNull null
                when (preset) {
                    ProviderPreset.OPENROUTER -> openRouterModel(obj)
                    ProviderPreset.GROQ -> groqModel(obj)
                }
            }
        return found.distinctBy { it.id }.sortedWith(
            compareByDescending<DiscoveredModel> { it.supportsJson }
                .thenBy { isReasoning(it.id) }
                .thenByDescending { paramsBillions(it.id) }
                .thenByDescending { it.contextLength ?: 0 }
                .thenBy { it.id },
        )
    }

    private fun isChatId(id: String): Boolean {
        val lower = id.lowercase()
        return notChat.none { lower.contains(it) }
    }

    private fun isReasoning(id: String): Boolean {
        val lower = id.lowercase()
        return reasoning.any { lower.contains(it) }
    }

    private fun paramsBillions(id: String): Double = sizeRegex.find(id.lowercase())?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0

    private fun isZeroPrice(value: JsonPrimitive?): Boolean = value?.contentOrNull?.toDoubleOrNull() == 0.0

    private fun openRouterModel(obj: JsonObject): DiscoveredModel? {
        val id = (obj["id"] as? JsonPrimitive)?.contentOrNull ?: return null
        val pricing = obj["pricing"] as? JsonObject
        val free =
            id.endsWith(":free") ||
                (isZeroPrice(pricing?.get("prompt") as? JsonPrimitive) && isZeroPrice(pricing?.get("completion") as? JsonPrimitive))
        if (!free || !isChatId(id)) return null
        val outputs =
            ((obj["architecture"] as? JsonObject)?.get("output_modalities") as? JsonArray)
                ?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
        if (outputs != null && outputs != listOf("text")) return null
        val params = (obj["supported_parameters"] as? JsonArray)?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }.orEmpty()
        return DiscoveredModel(
            id = id,
            contextLength = (obj["context_length"] as? JsonPrimitive)?.intOrNull,
            supportsJson = "response_format" in params || "structured_outputs" in params,
        )
    }

    private fun groqModel(obj: JsonObject): DiscoveredModel? {
        val id = (obj["id"] as? JsonPrimitive)?.contentOrNull ?: return null
        val active = (obj["active"] as? JsonPrimitive)?.booleanOrNull ?: true
        if (!active || !isChatId(id)) return null
        return DiscoveredModel(
            id = id,
            contextLength = (obj["context_window"] as? JsonPrimitive)?.intOrNull,
            supportsJson = true,
        )
    }
}

/**
 * Tenta os modelos na ordem e passa para o próximo quando um falha por limite, indisponibilidade ou erro do
 * servidor. O modelo que falhou fica de molho por um tempo, para as próximas jogadas já irem direto ao seguinte.
 * Chave inválida e falta de internet não trocam de modelo (não adiantaria). Depois que o texto começou a chegar
 * em streaming, não dá mais para trocar sem duplicar a narração, então o erro sobe.
 */
class FallbackProvider(
    private val candidates: List<Pair<String, AiProvider>>,
    private val clock: () -> Long = System::currentTimeMillis,
    private val rateLimitCooldownMs: Long = 5 * 60_000L,
    private val serverCooldownMs: Long = 2 * 60_000L,
    private val unavailableCooldownMs: Long = 10 * 60_000L,
) : AiProvider {
    init {
        require(candidates.isNotEmpty()) { "informe ao menos um modelo" }
    }

    override val name: String = "free-fallback"

    private val coolUntil = HashMap<String, Long>()

    /** Último modelo que respondeu com sucesso (para mostrar na tela). */
    @Volatile
    var lastModel: String? = null
        private set

    override fun complete(request: AiRequest): AiResponse = attempt({ true }) { it.complete(request) }

    override fun stream(
        request: AiRequest,
        onChunk: (String) -> Unit,
    ): AiResponse {
        var emitted = false
        return attempt({ !emitted }) { provider ->
            provider.stream(request) { chunk ->
                emitted = true
                onChunk(chunk)
            }
        }
    }

    private fun ready(): List<Pair<String, AiProvider>> {
        val now = clock()
        synchronized(coolUntil) {
            val ready = candidates.filter { (coolUntil[it.first] ?: 0L) <= now }
            if (ready.isNotEmpty()) return ready
            val wait = coolUntil.values.minOrNull()?.minus(now)?.coerceAtLeast(1_000L)
            throw AiError.RateLimited(wait)
        }
    }

    private fun cooldownFor(error: AiError): Long? =
        when (error) {
            is AiError.RateLimited -> error.retryAfterMs?.coerceAtLeast(30_000L) ?: rateLimitCooldownMs
            is AiError.ServerError -> serverCooldownMs
            is AiError.BadRequest, is AiError.BadResponse -> unavailableCooldownMs
            else -> null
        }

    private fun <T> attempt(
        canSwitch: () -> Boolean,
        call: (AiProvider) -> T,
    ): T {
        var last: AiError? = null
        for ((model, provider) in ready()) {
            try {
                return call(provider).also { lastModel = model }
            } catch (e: AiError) {
                val cooldown = cooldownFor(e)
                if (cooldown == null || !canSwitch()) throw e
                synchronized(coolUntil) { coolUntil[model] = clock() + cooldown }
                last = e
            }
        }
        throw last ?: AiError.BadRequest("nenhum modelo disponível")
    }
}
