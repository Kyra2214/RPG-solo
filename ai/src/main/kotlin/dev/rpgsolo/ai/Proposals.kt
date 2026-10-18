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
import dev.rpgsolo.rules.NpcProposal
import dev.rpgsolo.rules.RuleSource
import dev.rpgsolo.engine.NpcDossier
import dev.rpgsolo.engine.Secret
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
    val rollRequest: RollRequest? = null,
    /** NPC proposto pela IA; a ficha só é criada após validação pelo pacote ativo. */
    val npcProposal: NpcProposal? = null
)

sealed interface ParseResult {
    data class Ok(val output: TurnOutput) : ParseResult
    data class Invalid(val errors: List<String>) : ParseResult
}

/** Texto do formato esperado, para o prompt fixo do mestre (Fase 7.3). */
object ProposalSchema {
    val INSTRUCTIONS: String = """
        Responda SOMENTE com um objeto JSON, sem texto fora dele:
        {"narrative": "texto que o jogador vai ler", "proposals": [ ... ], "suggestions": ["até 3 ideias curtas de ação"], "create_npc": {"name":"...", "concept":"...", "stats":{"poder":1}, "traits":[], "skills":{}, "personality":"...", "goals":[], "motivations":[], "relations":{}, "secrets":[{"id":"...", "text":"...", "falseInfo":false}]}}
        "create_npc" é opcional: quando criar um NPC relevante, proponha nome, conceito, atributos, traços e perícias. O pacote valida tudo; não invente ids nem regras. Não inclua create_npc ao pedir teste/rolagem.
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
        Campo opcional "request_check" (veja a regra 8): {"check": "id", "difficulty": "id", "label": "nome curto, ex.: Percepção", "onSuccess": "o que acontece se passar", "onFailure": "o que acontece se falhar", "onPartial": "opcional", "skill": "opcional: tipo da situação, ex.: esquiva ou sorte"}. Para descobrir segredo guardado, acrescente "targetNpc": "id do NPC", "revealSecret": "id do segredo" e "requiredStat": "atributo/perícia exigido"; o texto do segredo só é liberado pelo motor em sucesso parcial ou melhor.
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
        val npcProposal = when (val rawNpc = obj["create_npc"]) {
            null, is JsonNull -> null
            is JsonObject -> try { toNpcProposal(rawNpc) }
                catch (e: Bad) { errors += "create_npc: ${e.message}"; null }
            else -> { errors += "'create_npc' deve ser um objeto"; null }
        }
        if (npcProposal != null && (checkRequest != null || rollRequest != null)) {
            errors += "não combine create_npc com request_check/request_roll; crie o NPC em outra jogada"
        }
        return if (errors.isEmpty()) ParseResult.Ok(TurnOutput(narrative!!.trim(), commands, suggestions, checkRequest, rollRequest, npcProposal))
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

    private fun toNpcProposal(o: JsonObject): NpcProposal {
        fun requiredText(key: String): String =
            o.str(key)?.trim()?.takeIf { it.isNotEmpty() } ?: throw Bad("campo '$key' ausente ou vazio")
        fun intMap(key: String, optional: Boolean = false): Map<String, Int> {
            val raw = o[key]
            if (raw == null && optional) return emptyMap()
            val obj = raw as? JsonObject ?: throw Bad("campo '$key' deve ser um objeto id -> número")
            return obj.mapValues { (id, value) ->
                val primitive = value as? JsonPrimitive
                if (primitive == null || primitive.isString) throw Bad("campo '$key.$id' deve ser inteiro")
                primitive.intOrNull ?: throw Bad("campo '$key.$id' deve ser inteiro")
            }
        }
        val rawTraits = o["traits"]
        val traits = when (rawTraits) {
            null, is JsonNull -> emptyList()
            is JsonArray -> rawTraits.map { value ->
                (value as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("campo 'traits' deve ser uma lista de ids em texto")
            }
            else -> throw Bad("campo 'traits' deve ser uma lista de ids em texto")
        }
        val stats = intMap("stats")
        if (stats.isEmpty()) throw Bad("informe os atributos do NPC em 'stats'")
        fun textList(key: String): List<String> = when (val raw = o[key]) {
            null, is JsonNull -> emptyList()
            is JsonArray -> raw.map { value ->
                (value as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("campo '$key' deve ser uma lista de textos")
            }
            else -> throw Bad("campo '$key' deve ser uma lista de textos")
        }
        val relations = when (val raw = o["relations"]) {
            null, is JsonNull -> emptyMap()
            is JsonObject -> raw.mapValues { (key, value) ->
                (value as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("campo 'relations.$key' deve ser texto")
            }
            else -> throw Bad("campo 'relations' deve ser um objeto")
        }
        val secrets = when (val raw = o["secrets"]) {
            null, is JsonNull -> emptyList()
            is JsonArray -> raw.mapIndexed { index, value ->
                val secret = value as? JsonObject ?: throw Bad("secrets[$index] deve ser um objeto")
                val id = secret.str("id")?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("secrets[$index].id ausente")
                val text = secret.str("text")?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("secrets[$index].text ausente")
                Secret(id, text, secret.bool("falseInfo") ?: false)
            }
            else -> throw Bad("campo 'secrets' deve ser uma lista de objetos")
        }
        val personality = o.str("personality")?.trim().orEmpty()
        val goals = textList("goals")
        val motivations = textList("motivations")
        val dossier = if (personality.isBlank() && goals.isEmpty() && motivations.isEmpty() && relations.isEmpty() && secrets.isEmpty()) {
            null
        } else NpcDossier("pending", personality, goals, motivations, relations, secrets)
        return NpcProposal(
            id = "pending",
            name = requiredText("name"),
            concept = o.str("concept")?.trim().orEmpty(),
            stats = stats,
            traits = traits.distinct(),
            ruleSource = RuleSource.PACOTE,
            skills = intMap("skills", optional = true),
            dossier = dossier,
            resources = intMap("resources", optional = true)
        )
    }

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
            skill = opt("skill")?.let(::slug),
            targetNpcId = opt("targetNpc"),
            revealSecretId = opt("revealSecret"),
            requiredStat = opt("requiredStat")
        ).also {
            if ((it.targetNpcId == null) != (it.revealSecretId == null)) {
                throw Bad("use targetNpc e revealSecret juntos")
            }
            if (it.revealSecretId != null && it.requiredStat.isNullOrBlank()) {
                throw Bad("revelar segredo exige requiredStat")
            }
        }
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
