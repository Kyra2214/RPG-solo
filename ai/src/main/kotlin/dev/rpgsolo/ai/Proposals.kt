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
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull

/** Fase 6.3.1: a IA devolve narrativa + PROPOSTAS. Propostas nunca viram estado sem passar pelo motor. */
data class TurnOutput(
    val narrative: String,
    val proposals: List<Command>,
    /** 7.3.4: sugestões de ação, opcionais. Nunca obrigatórias e nunca viram estado. */
    val suggestions: List<String> = emptyList(),
    /** Teste que o mestre pede ao jogador no meio da história. Quando existe, a narração para antes do resultado. */
    val checkRequest: CheckRequest? = null,
    /** Rolagem com tabela (ex.: d6 para onde a flecha acerta). Também para a narração antes do resultado. */
    val rollRequest: RollRequest? = null
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
        Campo opcional "request_roll" (regra 9), uma entrada para CADA face: {"sides": 6, "label": "Onde a flecha acerta", "table": {"1": {"text": "raspa no ombro", "damage": 1}, "2": {...}, "4": {"text": "fura a coxa", "damage": 4, "conditions": ["sangramento"]}, "6": {"text": "atinge perto do coração", "damage": 6}}}
        "conditions" (opcional em cada face) é uma lista de ids de condição do sistema que o motor aplica ao personagem junto com o dano.
        Só um pedido por resposta: request_check OU request_roll.
        Campo opcional "request_check" (veja a regra 8): {"check": "id", "difficulty": "id", "label": "nome curto, ex.: Percepção", "onSuccess": "o que acontece se passar", "onFailure": "o que acontece se falhar", "onPartial": "opcional", "skill": "opcional: tipo da situação, ex.: esquiva ou sorte"}
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
        val checkRequest = when (val rc = obj["request_check"]) {
            null, is JsonNull -> null
            is JsonObject -> try {
                toCheckRequest(rc)
            } catch (e: Bad) {
                errors += "request_check: ${e.message}"
                null
            }
            else -> {
                errors += "'request_check' deve ser um objeto"
                null
            }
        }
        val rollRequest = when (val rr = obj["request_roll"]) {
            null, is JsonNull -> null
            is JsonObject -> try {
                toRollRequest(rr)
            } catch (e: Bad) {
                errors += "request_roll: ${e.message}"
                null
            }
            else -> {
                errors += "'request_roll' deve ser um objeto"
                null
            }
        }
        if (checkRequest != null && rollRequest != null) errors += "peça só um por vez: request_check ou request_roll"
        return if (errors.isEmpty()) ParseResult.Ok(TurnOutput(narrative!!.trim(), commands, suggestions, checkRequest, rollRequest))
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

    private fun toRollRequest(o: JsonObject): RollRequest {
        val sides = o.int("sides") ?: throw Bad("campo 'sides' ausente ou não numérico")
        if (sides !in 2..20) throw Bad("'sides' deve estar entre 2 e 20")
        val tableObj = o["table"] as? JsonObject ?: throw Bad("campo 'table' ausente (objeto face -> {text, damage})")
        val table = mutableMapOf<Int, RollEntry>()
        for ((key, value) in tableObj) {
            val face = key.trim().toIntOrNull() ?: throw Bad("table: a face '$key' não é um número")
            val e = value as? JsonObject ?: throw Bad("table[$key] deve ser um objeto {text, damage}")
            val text = e.str("text")?.trim()?.takeIf { it.isNotEmpty() } ?: throw Bad("table[$key]: campo 'text' ausente ou vazio")
            val damage = if (!e.containsKey("damage")) 0 else (e.int("damage") ?: throw Bad("table[$key]: 'damage' deve ser número"))
            if (damage < 0) throw Bad("table[$key]: 'damage' negativo")
            val conditions = when (val c = e["conditions"]) {
                null, is JsonNull -> emptyList()
                is JsonArray -> c.map { el ->
                    (el as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }
                        ?: throw Bad("table[$key]: 'conditions' deve ser uma lista de ids em texto")
                }.distinct()
                else -> throw Bad("table[$key]: 'conditions' deve ser uma lista de ids em texto")
            }
            table[face] = RollEntry(text, damage, conditions)
        }
        val missing = (1..sides).filter { it !in table }
        if (missing.isNotEmpty() || table.keys.any { it !in 1..sides }) {
            throw Bad("table deve ter exatamente uma entrada para cada face de 1 a $sides (faltam: ${missing.joinToString()})")
        }
        return RollRequest(sides, o.str("label")?.trim()?.takeIf { it.isNotEmpty() }, table)
    }

    /** "Esquiva Rápida" -> "esquiva-rapida" (mesmo formato dos escopos de bônus do pacote). */
    private fun slug(s: String): String =
        java.text.Normalizer.normalize(s.trim().lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "").replace(Regex("[^a-z0-9]+"), "-").trim('-')

    private fun toCheckRequest(o: JsonObject): CheckRequest {
        fun req(k: String): String = o.str(k)?.takeIf { it.isNotBlank() } ?: throw Bad("campo '$k' ausente ou vazio")
        fun opt(k: String): String? = o.str(k)?.trim()?.takeIf { it.isNotEmpty() }
        return CheckRequest(
            checkId = req("check").trim(),
            difficultyId = opt("difficulty"),
            label = opt("label"),
            onSuccess = req("onSuccess").trim(),
            onFailure = req("onFailure").trim(),
            onPartial = opt("onPartial"),
            skill = opt("skill")?.let(::slug)
        )
    }

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
