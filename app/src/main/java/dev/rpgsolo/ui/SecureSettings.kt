package dev.rpgsolo.ui

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Configuração persistida; a chave fica cifrada pelo Android Keystore e nunca em texto puro. */
data class StoredProviderSettings(
    val kind: String,
    val endpoint: String,
    val model: String,
    val cheapModel: String,
    val premiumModel: String,
    val apiKey: String,
    val cacheSystemPrompt: Boolean = false,
    val inputPricePerMillion: Double = 0.0,
    val outputPricePerMillion: Double = 0.0,
    val cacheReadPricePerMillion: Double = 0.0,
    val cacheCreationPricePerMillion: Double = 0.0
)

class SecureSettings(context: Context) {
    private val preferences = context.getSharedPreferences("rpgsolo_provider", Context.MODE_PRIVATE)

    fun load(): StoredProviderSettings? {
        val kind = preferences.getString("kind", null) ?: return null
        val endpoint = preferences.getString("endpoint", null) ?: return null
        val model = preferences.getString("model", null) ?: return null
        val encrypted = preferences.getString("api_key", null) ?: return null
        val apiKey = decrypt(encrypted) ?: return null
        return StoredProviderSettings(
            kind = kind,
            endpoint = endpoint,
            model = model,
            cheapModel = preferences.getString("cheap_model", "").orEmpty(),
            premiumModel = preferences.getString("premium_model", "").orEmpty(),
            apiKey = apiKey,
            cacheSystemPrompt = preferences.getBoolean(
                "cache_system_prompt",
                endpoint.startsWith("https://api.anthropic.com/")
            ),
            inputPricePerMillion = preferences.getString("price_input_per_million", "0")?.toDoubleOrNull() ?: 0.0,
            outputPricePerMillion = preferences.getString("price_output_per_million", "0")?.toDoubleOrNull() ?: 0.0,
            cacheReadPricePerMillion = preferences.getString("price_cache_read_per_million", "0")?.toDoubleOrNull() ?: 0.0,
            cacheCreationPricePerMillion = preferences.getString("price_cache_creation_per_million", "0")?.toDoubleOrNull() ?: 0.0
        )
    }

    fun save(settings: StoredProviderSettings) {
        preferences.edit()
            .putString("kind", settings.kind)
            .putString("endpoint", settings.endpoint)
            .putString("model", settings.model)
            .putString("cheap_model", settings.cheapModel)
            .putString("premium_model", settings.premiumModel)
            .putBoolean("cache_system_prompt", settings.cacheSystemPrompt)
            .putString("price_input_per_million", settings.inputPricePerMillion.toString())
            .putString("price_output_per_million", settings.outputPricePerMillion.toString())
            .putString("price_cache_read_per_million", settings.cacheReadPricePerMillion.toString())
            .putString("price_cache_creation_per_million", settings.cacheCreationPricePerMillion.toString())
            .putString("api_key", encrypt(settings.apiKey))
            .apply()
    }

    fun clear() = preferences.edit().clear().apply()

    private fun secretKey(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!store.containsAlias(KEY_ALIAS)) {
            val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            generator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            generator.generateKey()
        }
        return store.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val body = Base64.encodeToString(cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8)), Base64.NO_WRAP)
        return "$iv:$body"
    }

    private fun decrypt(value: String): String? = try {
        val parts = value.split(':', limit = 2)
        require(parts.size == 2)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey(),
            GCMParameterSpec(128, Base64.decode(parts[0], Base64.NO_WRAP))
        )
        String(cipher.doFinal(Base64.decode(parts[1], Base64.NO_WRAP)), StandardCharsets.UTF_8)
    } catch (_: Exception) {
        null
    }

    private companion object {
        const val KEY_ALIAS = "rpgsolo_provider_key_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
