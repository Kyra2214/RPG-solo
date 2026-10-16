package dev.rpgsolo.ai

import dev.rpgsolo.engine.AddCondition
import dev.rpgsolo.engine.AddItem
import dev.rpgsolo.engine.AdvanceClock
import dev.rpgsolo.engine.ChangeRelation
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.CreateClock
import dev.rpgsolo.engine.ConsumeItem
import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.GainXp
import dev.rpgsolo.engine.Heal
import dev.rpgsolo.engine.KeepPromise
import dev.rpgsolo.engine.LearnKnowledge
import dev.rpgsolo.engine.MakePromise
import dev.rpgsolo.engine.RecordFact
import dev.rpgsolo.engine.RemoveCondition
import dev.rpgsolo.engine.Revive
import dev.rpgsolo.engine.SpendResource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull

/** Fase 6.3.1: a IA devolve narrativa + PROPOSTAS. Propostas nunca viram estado sem passar pelo motor. */
data class TurnOutput(
    val narrative: String,
    val proposals: List<Command>,
    /** 7.3.4: sugestões de ação, opcionais. Nunca obrigatórias e nunca viram estado. */
    val suggestions: List<String> = emptyList()
)

sealed interface ParseResult {
    data class Ok(val output: TurnOutput) : ParseResult
    data class Invalid(val errors: List<String>) : ParseResult
}

/** Texto do formato esperado, para o prompt fixo do mestre (Fase 7.3). */
object ProposalSchema {
    val INSTRUCTIONS: String = """
        Responda SOMENTE com um objeto JSON, sem texto fora dele:
        {"narrative": "texto que o jogador vai ler", "proposals": [ ... ], "suggestions": ["até 3 ideias curtas de ação"]}
        "suggestions" é opcional: no máximo ${ProposalParser.MAX_SUGGESTIONS} ideias curtas do que o personagem poderia fazer; o jogador pode ignorá-las.
        "proposals" é a lista (pode ser vazia) de mudanças que você PROPÕE. O motor valida cada uma e pode recusar.
        Você nunca rola dados nem decide o resultado de testes: o motor já decidiu; você só descreve.
        Tipos permitidos (campos entre parênteses):
        damage(entityId, resource, amount) · heal(entityId, resource, amount) · spend_resource(entityId, resource, cost)
        add_item(ownerId, itemId, qty) · consume_item(ownerId, itemId, qty) · gain_xp(entityId, amount)
        revive(entityId, reason) · record_fact(key, value) · learn(key, text, source, believed)
        make_promise(id, npcId, text) · keep_promise(id) · add_condition(entityId, condition) · remove_condition(entityId, condition)
        change_relation(fromId, toId, delta, reason) · create_clock(id, name, segments) · advance_clock(id, by)
    """.trimIndent()

    val TYPES = listOf(
        "damage", "heal", "spend_resource", "add_item", "consume_item", "gain_xp", "revive",
        "record_fact", "learn", "make_promise", "keep_promise", "add_condition", "remove_condition",
        "change_relation", "create_clock", "advance_clock"
    )
}

private class Bad(message: String) : Exception(message)

object ProposalParser {
    const val MAX_SUGGESTIONS = 3

    /**
     * Lê a resposta da IA. Aceita JSON puro ou dentro de cerca de código. Erros claros e em lista, para a IA
     * poder corrigir na nova tentativa (6.3.3). Uma proposta inválida invalida o lote inteiro (tudo ou nada).
     */
    fun parse(raw: String): ParseResult {
        val text = extractJson(raw) ?: return ParseResult.Invalid(listOf("a resposta não contém um objeto JSON"))
        val root = try {
            Json.parseToJsonElement(text)
        } catch (e: Exception) {
            return ParseResult.Invalid(listOf("JSON inválido"))
        }
        val obj = root as? JsonObject ?: return ParseResult.Invalid(listOf("a resposta deve ser um objeto JSON"))

        val errors = mutableListOf<String>()
        val narrative = obj.str("narrative")
        if (narrative.isNullOrBlank()) errors += "campo 'narrative' ausente ou vazio"

        val list: JsonArray = when (val p = obj["proposals"]) {
            null -> JsonArray(emptyList())
            is JsonArray -> p
            else -> {
                errors += "'proposals' deve ser uma lista"
                JsonArray(emptyList())
            }
        }
        val commands = mutableListOf<Command>()
        list.forEachIndexed { i, el ->
            val o = el as? JsonObject
            if (o == null) {
                errors += "proposals[$i]: deve ser um objeto"
            } else {
                try {
                    commands += toCommand(o)
                } catch (e: Bad) {
                    errors += "proposals[$i]: ${e.message}"
                }
            }
        }
        // Sugestões são opcionais: formato errado nelas não invalida a jogada, só são descartadas.
        val suggestions = (obj["suggestions"] as? JsonArray).orEmpty()
            .mapNotNull { (it as? JsonPrimitive)?.takeIf { p -> p.isString }?.content?.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .take(MAX_SUGGESTIONS)
        return if (errors.isEmpty()) ParseResult.Ok(TurnOutput(narrative!!.trim(), commands, suggestions))
        else ParseResult.Invalid(errors)
    }

    internal fun extractJson(raw: String): String? {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        return if (start in 0 until end) raw.substring(start, end + 1) else null
    }

    private fun JsonObject.str(key: String): String? =
        (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content

    private fun JsonObject.int(key: String): Int? =
        (this[key] as? JsonPrimitive)?.takeIf { !it.isString }?.intOrNull

    private fun JsonObject.bool(key: String): Boolean? =
        (this[key] as? JsonPrimitive)?.takeIf { !it.isString }?.booleanOrNull

    private fun toCommand(o: JsonObject): Command {
        val type = o.str("type") ?: throw Bad("campo 'type' ausente")
        fun s(k: String): String = o.str(k)?.takeIf { it.isNotBlank() } ?: throw Bad("$type: campo '$k' ausente ou vazio")
        fun n(k: String): Int = o.int(k) ?: throw Bad("$type: campo '$k' ausente ou não numérico")
        fun nOr(k: String, default: Int): Int = if (o.containsKey(k)) n(k) else default
        return when (type) {
            "damage" -> Damage(s("entityId"), s("resource"), n("amount"))
            "heal" -> Heal(s("entityId"), s("resource"), n("amount"))
            "spend_resource" -> SpendResource(s("entityId"), s("resource"), n("cost"))
            "add_item" -> AddItem(s("ownerId"), s("itemId"), nOr("qty", 1))
            "consume_item" -> ConsumeItem(s("ownerId"), s("itemId"), nOr("qty", 1))
            "gain_xp" -> GainXp(s("entityId"), n("amount"))
            "revive" -> Revive(s("entityId"), s("reason"))
            "record_fact" -> RecordFact(s("key"), o.str("value") ?: throw Bad("record_fact: campo 'value' ausente"))
            "learn" -> LearnKnowledge(s("key"), s("text"), s("source"), o.bool("believed") ?: false)
            "make_promise" -> MakePromise(s("id"), s("npcId"), s("text"))
            "keep_promise" -> KeepPromise(s("id"))
            "add_condition" -> AddCondition(s("entityId"), s("condition"))
            "remove_condition" -> RemoveCondition(s("entityId"), s("condition"))
            "change_relation" -> ChangeRelation(s("fromId"), s("toId"), n("delta"), s("reason"))
            "create_clock" -> CreateClock(s("id"), s("name"), n("segments"))
            "advance_clock" -> AdvanceClock(s("id"), nOr("by", 1))
            "roll", "dice", "check", "set_hp", "set_resource", "create_entity", "start_combat", "end_combat", "next_turn" ->
                throw Bad("'$type' não é permitido à IA: dados, testes e combate são decididos pelo motor")
            else -> throw Bad("tipo desconhecido '$type' (permitidos: ${ProposalSchema.TYPES.joinToString(", ")})")
        }
    }
}
