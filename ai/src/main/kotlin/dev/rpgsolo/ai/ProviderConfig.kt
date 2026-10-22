package dev.rpgsolo.ai

/**
 * Endpoint e modelo configuráveis (6.1.4). A chave fica só no aparelho (guardada pelo app em
 * cifrada com Android Keystore, nunca no APK) e nunca aparece em logs nem na tela (6.1.5).
 */
data class ProviderConfig(
    val endpoint: String,
    val model: String,
    val apiKey: String,
    val extraHeaders: Map<String, String> = emptyMap(),
    /** Cache real do prefixo system na API oficial Anthropic; endpoints compatíveis ficam opt-in. */
    val cacheSystemPrompt: Boolean = endpoint.startsWith("https://api.anthropic.com/"),
) {
    init {
        require(endpoint.startsWith("https://")) { "o endpoint deve usar https://" }
        require(model.isNotBlank()) { "modelo vazio" }
    }

    /** A chave nunca vai para texto de log. */
    override fun toString(): String =
        "ProviderConfig(endpoint=$endpoint, model=$model, apiKey=${maskKey(
            apiKey,
        )}, cacheSystemPrompt=$cacheSystemPrompt)"
}

/** Mostra só o final da chave: `••••••••wxyz`. Chave curta demais some inteira. */
fun maskKey(key: String): String = if (key.length <= 8) "••••••••" else "••••••••" + key.takeLast(4)
