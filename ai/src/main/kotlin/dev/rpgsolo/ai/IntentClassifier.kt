package dev.rpgsolo.ai

import dev.rpgsolo.engine.CheckSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Fase 7.1.2 / 6.4.1: um teste que o pacote de regras oferece para ações do jogador.
 * O [spec] já vem montado pelo motor/pacote (dados, modificador, dificuldade). A IA só escolhe o [id].
 */
data class CheckOption(val id: String, val description: String, val spec: CheckSpec) {
    init {
        require(id.isNotBlank()) { "id do teste vazio" }
    }
}

sealed interface Intent {
    /** A ação não exige teste (conversar, observar, andar). */
    data object NoCheck : Intent

    /** A ação exige este teste, escolhido entre as opções do pacote. */
    data class Test(val option: CheckOption) : Intent

    /** Não foi possível classificar (rede, chave, resposta ilegível). O jogador é avisado; nada é inventado. */
    data class Unavailable(val reason: String) : Intent
}

/**
 * Interpreta a intenção do jogador com um modelo barato (6.4.1). Ele nunca cria teste, dificuldade nem dado:
 * só aponta um id da lista que o pacote ofereceu. Qualquer id desconhecido vira "sem teste".
 */
class IntentClassifier(private val provider: AiProvider, private val maxTokens: Int = 60) {
    fun classify(
        text: String,
        options: List<CheckOption>,
    ): Intent {
        if (text.isBlank() || options.isEmpty()) return Intent.NoCheck
        val request =
            AiRequest(
                messages =
                    listOf(
                        AiMessage(Role.SYSTEM, PROMPT),
                        AiMessage(
                            Role.USER,
                            buildString {
                                append("Testes disponíveis:\n")
                                options.forEach { append("- ").append(it.id).append(": ").append(it.description).append('\n') }
                                append("\nAção do jogador: ").append(text.trim())
                            },
                        ),
                    ),
                maxTokens = maxTokens,
                temperature = 0.0,
                jsonMode = true,
            )
        val raw =
            try {
                provider.complete(request).text
            } catch (e: AiError) {
                return Intent.Unavailable(e.message ?: "erro de IA")
            }
        return parse(raw, options)
    }

    /** Expõe o classificador como o `checkFor` do [PlayController]: sem classificação, a ação segue sem teste. */
    fun asCheckFor(
        options: List<CheckOption>,
        onUnavailable: (String) -> Unit = {},
    ): (String) -> CheckSpec? =
        { text ->
            when (val i = classify(text, options)) {
                is Intent.Test -> i.option.spec
                Intent.NoCheck -> null
                is Intent.Unavailable -> {
                    onUnavailable(i.reason)
                    null
                }
            }
        }

    internal fun parse(
        raw: String,
        options: List<CheckOption>,
    ): Intent {
        val json = ProposalParser.extractJson(raw) ?: return Intent.Unavailable("resposta sem JSON")
        val obj =
            try {
                Json.parseToJsonElement(json) as? JsonObject
            } catch (e: Exception) {
                null
            } ?: return Intent.Unavailable("JSON inválido")
        val value = obj["check"] ?: return Intent.Unavailable("campo 'check' ausente")
        if (value is JsonNull) return Intent.NoCheck
        val id =
            (value as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()
                ?: return Intent.Unavailable("campo 'check' inválido")
        if (id.isEmpty() || id.equals("null", ignoreCase = true) || id.equals("none", ignoreCase = true)) return Intent.NoCheck
        val option = options.firstOrNull { it.id == id } ?: return Intent.NoCheck
        return Intent.Test(option)
    }

    companion object {
        val PROMPT: String =
            """
            Você classifica ações de um jogador de RPG. Escolha no máximo UM dos testes listados que a ação realmente exige,
            ou nenhum, se a ação é simples, só conversa, observa ou anda sem risco nem incerteza.
            Responda SOMENTE com JSON: {"check": "<id do teste>"} ou {"check": null}.
            Use apenas ids da lista. Nunca invente teste, dificuldade ou resultado.
            """.trimIndent()
    }
}
