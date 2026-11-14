package dev.rpgsolo.ai

import dev.rpgsolo.rules.CustomClockDraft
import dev.rpgsolo.rules.CustomPlaceDraft
import dev.rpgsolo.rules.CustomWorldDraft
import dev.rpgsolo.rules.CustomWorldLimits
import dev.rpgsolo.rules.CustomWorldValidator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

/** MP-08: resultado da proposta de mundo próprio. */
sealed interface CustomWorldProposal {
    /** Rascunho que passou pelo validador do motor; pode ser mostrado ao jogador e gravado. */
    data class Valid(val draft: CustomWorldDraft) : CustomWorldProposal

    /** A IA respondeu, mas o motor recusou: [errors] vão para o jogador, nada é gravado. */
    data class Invalid(val draft: CustomWorldDraft?, val errors: List<String>) : CustomWorldProposal

    /** Rede, chave ou resposta ilegível. */
    data class Failed(val reason: String, val error: AiError? = null) : CustomWorldProposal
}

/** MP-08: a IA propõe o mundo a partir da descrição livre; o motor valida limites e nomes únicos antes de qualquer gravação. */
class CustomWorldProposer(private val provider: AiProvider, private val maxTokens: Int = 1500) {
    fun propose(description: String): CustomWorldProposal {
        if (description.isBlank()) return CustomWorldProposal.Failed("descreva o mundo que você quer jogar")
        val raw =
            try {
                provider.complete(
                    AiRequest(
                        listOf(AiMessage(Role.SYSTEM, systemPrompt()), AiMessage(Role.USER, description.trim())),
                        maxTokens = maxTokens,
                        temperature = 0.3,
                        jsonMode = true,
                    ),
                ).text
            } catch (e: AiError) {
                return CustomWorldProposal.Failed(e.message ?: "erro de IA", e)
            }
        val draft = parse(raw) ?: return CustomWorldProposal.Failed("não entendi a resposta da IA; tente descrever de novo")
        val errors = CustomWorldValidator.validate(draft)
        return if (errors.isEmpty()) CustomWorldProposal.Valid(draft) else CustomWorldProposal.Invalid(draft, errors)
    }

    internal fun systemPrompt(): String {
        val l = CustomWorldLimits
        return """
            Você propõe o mundo de uma campanha de RPG solo. Responda SOMENTE um objeto JSON, em português, com os campos:
            "name" (texto), "tone" (texto), "technology" (texto), "start" (id do lugar inicial),
            "places" (lista de {"id","name","description","secret","connections":[ids]}),
            "factions" (lista de nomes), "conflicts" (lista de textos),
            "clocks" (lista de {"id","name","segments","consequence"}), "gear" (lista de textos).
            Limites: ${l.MIN_PLACES} a ${l.MAX_PLACES} lugares, no máximo ${l.MAX_FACTIONS} facções, ${l.MAX_CONFLICTS} conflitos e ${l.MAX_CLOCKS} relógios;
            relógios com ${l.MIN_CLOCK_SEGMENTS} a ${l.MAX_CLOCK_SEGMENTS} casas; ids em minúsculas (letras, números, ponto, hífen); nomes únicos;
            o mapa deve ser conexo. Você não decide resultados de dados nem regras: só descreve o mundo.
            """.trimIndent()
    }

    internal fun parse(raw: String): CustomWorldDraft? {
        val json = ProposalParser.extractJson(raw) ?: return null
        val o =
            try {
                Json.parseToJsonElement(json) as? JsonObject
            } catch (e: Exception) {
                null
            } ?: return null

        fun text(
            obj: JsonObject,
            k: String,
        ): String = (obj[k] as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim().orEmpty()

        fun list(k: String): List<String> =
            (o[k] as? JsonArray).orEmpty().mapNotNull { (it as? JsonPrimitive)?.takeIf { p -> p.isString }?.content?.trim() }

        val places =
            (o["places"] as? JsonArray).orEmpty().mapNotNull { el ->
                val p = el as? JsonObject ?: return@mapNotNull null
                CustomPlaceDraft(
                    id = text(p, "id"),
                    name = text(p, "name"),
                    description = text(p, "description"),
                    secret = text(p, "secret"),
                    connections =
                        (p["connections"] as? JsonArray).orEmpty()
                            .mapNotNull { (it as? JsonPrimitive)?.takeIf { x -> x.isString }?.content?.trim() },
                )
            }
        val clocks =
            (o["clocks"] as? JsonArray).orEmpty().mapNotNull { el ->
                val c = el as? JsonObject ?: return@mapNotNull null
                CustomClockDraft(
                    id = text(c, "id"),
                    name = text(c, "name"),
                    // Valor ausente ou não numérico vira 0 e o validador recusa; nada é "corrigido" em silêncio.
                    segments = (c["segments"] as? JsonPrimitive)?.intOrNull ?: 0,
                    consequence = text(c, "consequence"),
                )
            }
        return CustomWorldDraft(
            name = text(o, "name"),
            tone = text(o, "tone"),
            technology = text(o, "technology"),
            places = places,
            factions = list("factions"),
            conflicts = list("conflicts"),
            clocks = clocks,
            fictionalGear = list("gear"),
            startPlaceId = text(o, "start").ifEmpty { null },
        )
    }
}
