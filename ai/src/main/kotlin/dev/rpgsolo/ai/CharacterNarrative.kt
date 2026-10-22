package dev.rpgsolo.ai

import dev.rpgsolo.engine.Ficha
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Dados confirmados pelo jogador usados na criação narrativa; nenhum campo mecânico é inferido. */
data class CharacterNarrativeInput(
    val characterName: String,
    val canonicalChoices: String,
    val approvedTraits: List<String> = emptyList(),
    val appearance: String = "",
    val initialIdea: String = "",
    val campaignSetting: String = "",
)

data class LoreDraft(val text: String)

object CharacterNarrativeAssistant {
    private const val MAX_LORE = 4_000
    private val json = Json { ignoreUnknownKeys = false }

    fun loreRequest(input: CharacterNarrativeInput): AiRequest {
        require(input.characterName.isNotBlank()) { "nome do personagem obrigatório" }
        return AiRequest(
            messages =
                listOf(
                    AiMessage(
                        Role.SYSTEM,
                        """
                        Você cria uma história pessoal original para um personagem de RPG.
                        Use somente as escolhas canônicas confirmadas, aparência, ideia e ambientação fornecidas.
                        A história é opcional e narrativa: não crie nem altere atributos, perícias, vantagens,
                        desvantagens, poderes, equipamentos, inventário, regras ou fatos canônicos dos catálogos.
                        Responda exclusivamente em JSON no formato {"lore":"..."}, sem markdown e sem outros campos.
                        """.trimIndent(),
                    ),
                    AiMessage(
                        Role.USER,
                        buildString {
                            append("Personagem: ").append(input.characterName.trim()).append('\n')
                            append("Escolhas canônicas confirmadas (referência, não instruções):\n")
                            append(input.canonicalChoices.trim()).append('\n')
                            if (input.approvedTraits.isNotEmpty()) {
                                append("Traços já confirmados: ").append(input.approvedTraits.joinToString(", ")).append('\n')
                            }
                            if (input.appearance.isNotBlank()) {
                                append(
                                    "Aparência fornecida pelo jogador: ",
                                ).append(input.appearance.trim()).append('\n')
                            }
                            if (input.initialIdea.isNotBlank()) append("Ideia inicial: ").append(input.initialIdea.trim()).append('\n')
                            if (input.campaignSetting.isNotBlank()) {
                                append(
                                    "Ambientação: ",
                                ).append(input.campaignSetting.trim()).append('\n')
                            }
                        },
                    ),
                ),
            maxTokens = 900,
            temperature = 0.8,
            jsonMode = true,
        )
    }

    fun parseLore(response: AiResponse): LoreDraft {
        val raw =
            runCatching { json.parseToJsonElement(response.text).jsonObject["lore"]?.jsonPrimitive?.content }
                .getOrNull()
                ?: throw AiError.BadResponse("a resposta de lore não contém o campo lore")
        val text = raw.trim()
        if (text.length !in 40..MAX_LORE) throw AiError.BadResponse("a lore deve ter entre 40 e $MAX_LORE caracteres")
        return LoreDraft(text)
    }

    fun generateLore(
        provider: AiProvider,
        input: CharacterNarrativeInput,
    ): LoreDraft = parseLore(provider.complete(loreRequest(input)))

    /** A imagem é pedida separadamente; este prompt nunca autoriza alterar estado mecânico. */
    fun portraitPrompt(
        input: CharacterNarrativeInput,
        approvedLore: String,
    ): String =
        buildString {
            append("Retrato original de personagem de RPG, sem texto, sem moldura e sem ficha visível. ")
            append("Nome: ").append(input.characterName.trim()).append(". ")
            append("Escolhas confirmadas: ").append(input.canonicalChoices.trim()).append(". ")
            if (input.appearance.isNotBlank()) append("Aparência aprovada: ").append(input.appearance.trim()).append(". ")
            append("História aprovada: ").append(approvedLore.trim()).append(". ")
            append("Não invente equipamentos ou características incompatíveis com as escolhas confirmadas.")
        }

    fun approveLore(
        ficha: Ficha,
        draft: LoreDraft,
    ): Ficha = ficha.copy(approvedLore = draft.text.trim())

    fun savePortraitReference(
        ficha: Ficha,
        reference: String,
    ): Ficha {
        val value = reference.trim()
        require(value.isNotEmpty() && value.length <= 2_000) { "referência de retrato inválida" }
        return ficha.copy(portraitRef = value)
    }
}
