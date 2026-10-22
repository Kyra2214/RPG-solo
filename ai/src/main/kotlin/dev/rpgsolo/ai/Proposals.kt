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
import dev.rpgsolo.engine.NoteMemory
import dev.rpgsolo.engine.RecordFact
import dev.rpgsolo.engine.RemoveCondition
import dev.rpgsolo.engine.Revive
import dev.rpgsolo.engine.SpendResource
import dev.rpgsolo.engine.ProposeRuleAddition
import dev.rpgsolo.engine.AddPlace
import dev.rpgsolo.engine.ConnectPlaces
import dev.rpgsolo.engine.DiscoverPlace
import dev.rpgsolo.engine.Place
import dev.rpgsolo.engine.RegraAdicional
import dev.rpgsolo.engine.RuleOrigin
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
    val npcProposal: NpcProposal? = null,
    /** Quando true, cria o NPC validado e inicia combate com iniciativa rolada pelo motor. */
    val requestCombat: Boolean = false,
    /** Poder do pacote que o personagem do jogador usa nesta jogada; custo, teste e efeito são do motor. */
    val powerRequest: PowerRequest? = null,
    /** I-01: o NPC veio de `request_npc` (pedido isolado), não de `create_npc` junto a outras propostas. */
    val requestNpc: Boolean = false
)

/** Pedido da IA para o personagem do jogador usar um poder do pacote ([powerId]); [targetId] só para poderes ofensivos. */
data class PowerRequest(val powerId: String, val targetId: String? = null)

sealed interface ParseResult {
    data class Ok(val output: TurnOutput) : ParseResult
    data class Invalid(val errors: List<String>) : ParseResult
}

/** Texto do formato esperado, para o prompt fixo do mestre (Fase 7.3). */
object ProposalSchema {
    val INSTRUCTIONS: String = """
        Responda SOMENTE com um objeto JSON, sem texto fora dele:
        {"narrative": "texto que o jogador vai ler", "proposals": [ ... ], "suggestions": ["até 3 ideias curtas de ação"], "create_npc": {"name":"...", "concept":"...", "stats":{"poder":1}, "traits":[], "skills":{}, "personality":"...", "goals":[], "motivations":[], "relations":{}, "secrets":[{"id":"...", "text":"...", "falseInfo":false}]}, "request_combat": false}
        "create_npc" é opcional: quando criar um NPC relevante, proponha nome, conceito, atributos, traços e perícias. O pacote valida tudo; não invente ids nem regras. Não inclua create_npc ao pedir teste/rolagem.
        "request_npc" é opcional e usa os mesmos campos de create_npc, mais "id" (opcional, slug minúsculo com letras, números e hífen, 2 a 40 caracteres, ainda não usado) para você citar o NPC depois. Use quando a cena introduz um NPC relevante sem iniciar luta; o pacote valida a ficha e o motor cria o NPC. Envie sozinho, sem create_npc, proposals, request_combat, request_power, request_check ou request_roll.
        "request_combat" é opcional e booleano. Use true somente quando a cena realmente iniciar uma luta; nesse caso, envie também create_npc com um único adversário e deixe proposals, request_check e request_roll vazios/ausentes. O app valida a ficha pelo pacote, cria o NPC e o motor rola a iniciativa. Não use request_combat para ameaças ainda não hostis.
        "request_power" é opcional: {"power":"id-do-poder","target":"id-do-npc opcional"}. Use somente quando o jogador declarar que o personagem usa um dos poderes listados no contexto; o app valida traço, recurso e alvo, e o motor gasta o custo, rola o teste e aplica o efeito. Descreva só a tentativa; não invente o resultado nem números. Envie sozinho, sem proposals, create_npc, request_combat, request_check ou request_roll.
        "suggestions" é opcional: no máximo ${ProposalParser.MAX_SUGGESTIONS} ideias curtas do que o personagem poderia fazer; o jogador pode ignorá-las.
        "proposals" é a lista (pode ser vazia) de mudanças que você PROPÕE. O motor valida cada uma e pode recusar.
        Você nunca rola dados nem decide o resultado de testes: o motor já decidiu; você só descreve.
        Durante um combate não use damage, heal, spend_resource, add_condition nem remove_condition em quem está lutando: ataques, defesa, crítico e dano são calculados pelo motor com as mesmas regras para o jogador e para os NPCs. Você escolhe a ação e o alvo e narra o resultado que o motor devolver; nunca informe números de dano, rolagem ou acerto.
        Tipos permitidos (campos entre parênteses):
        damage(entityId, resource, amount) · heal(entityId, resource, amount) · spend_resource(entityId, resource, cost)
        add_item(ownerId, itemId, qty) · consume_item(ownerId, itemId, qty) · gain_xp(entityId, amount)
        revive(entityId, reason) · record_fact(key, value) · record_memory(subjectId, text: nota curta sobre um NPC ou lugar) · learn(key, text, source, believed)
        make_promise(id, npcId, text) · keep_promise(id) · add_condition(entityId, condition) · remove_condition(entityId, condition)
        change_relation(fromId, toId, delta, reason) · create_clock(id, name, segments, publicClue opcional, midClue opcional, consequence opcional, restTick opcional, moveTick opcional, chapterTick opcional, combatTick opcional, questTick opcional) · advance_clock(id, by)
        Em create_clock, publicClue é opcional e só aparece ao jogador quando o relógio completa. Use apenas consequência/pista pública; nunca coloque nela o nome interno do relógio, a contagem ou segredo privado. Quando houver base narrativa, proponha publicClue e midClue como sinais observáveis, concretos e contextualizados nos fatos já estabelecidos na cena/campanha. Não invente um acontecimento como já ocorrido, não explique a causa secreta nem revele planos, nomes internos ou contagens; se não houver uma pista segura e coerente, omita-a. midClue deve sinalizar uma mudança perceptível no meio do progresso; publicClue, um efeito observável quando ele se completa. restTick (inteiro, opcional, padrão 0) é quantas casas o relógio avança sozinho a cada descanso do jogador; moveTick, a cada viagem validada para outro local; chapterTick, a cada capítulo encerrado; combatTick, no fim de cada combate; questTick, quando uma missão é encerrada como concluída ou falha. Cada valor é número de casas de 0 até segments (padrão 0); o motor aplica automaticamente o avanço configurado, sem você propor outra ação. midClue (opcional) é uma pista pública curta, só de mundo, revelada quando o relógio chega à metade das casas; as mesmas restrições de publicClue valem. consequence (opcional) é uma nota PRIVADA para você: o que acontece no mundo quando o relógio completar; o jogador nunca a vê.
        propose_rule(id, name, description, effects, limit): adaptação explícita, nunca regra oficial; fica pendente até aprovação do jogador.
        add_place(id, name, description, secret) · connect_places(fromId, toId) · discover_place(id); não use move_to_place, pois só o jogador se desloca.
        Se ainda não houver local conhecido, crie o ponto inicial e descubra-o; segredos de local só são revelados pelo motor após teste apropriado.
        Campo opcional "request_roll" (regra 9), uma entrada para CADA face: {"sides": 6, "label": "Onde a flecha acerta", "table": {"1": {"text": "raspa no ombro", "damage": 1}, "2": {...}, "4": {"text": "fura a coxa", "damage": 4, "conditions": ["sangramento"]}, "6": {"text": "atinge perto do coração", "damage": 6}}}
        "hazard" (opcional em cada face, no lugar de damage) é o id de um perfil de perigo listado no contexto (leve, médio, grave, mortal...): o motor calcula o dano pela vida máxima do personagem.
        "conditions" (opcional em cada face) é uma lista de ids de condição do sistema que o motor aplica ao personagem junto com o dano.
        Só um pedido por resposta: request_check OU request_roll.
        Campo opcional "request_check": {"check":"id","difficulty":"id opcional","onSuccess":"...","onFailure":"...","skill":"escopo de bônus opcional"}. Para disputa, inclua "opposed":{"entityId":"id-do-npc","check":"id-do-teste-do-npc","difficulty":"id-opcional","label":"nome opcional"}; o jogador é o desafiante e o NPC é o defensor (empate favorece o defensor). Para tarefa cooperativa, inclua "group":{"participantIds":["player","id-npc-1","id-npc-2"],"requiredSuccesses":2,"onSuccess":"...","onFailure":"..."}. "participantIds" deve conter "player" exatamente uma vez e apenas IDs de NPCs vivos presentes na cena, sem repetições; "requiredSuccesses" deve ser de 1 até o total de participantes. Use "group" só quando todos estiverem colaborando no mesmo objetivo, não para uma disputa entre lados. Escolha check/difficulty válidos para cada teste. Não combine opposed/group com revelação de segredo; nunca envie proposals ou outro pedido junto ao request_check. O motor resolve os resultados e só revela a consequência escolhida.
    """.trimIndent()

    val TYPES = listOf(
        "damage", "heal", "spend_resource", "add_item", "consume_item", "gain_xp", "revive",
        "record_fact", "record_memory", "learn", "make_promise", "keep_promise", "add_condition", "remove_condition",
        "change_relation", "create_clock", "advance_clock", "propose_rule",
        "add_place", "connect_places", "discover_place"
    )
}

private class Bad(message: String) : Exception(message)

object ProposalParser {
    const val MAX_SUGGESTIONS = 3
    private val NPC_ID = Regex("^[a-z0-9][a-z0-9-]{1,39}$")

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
        val requestedNpc = when (val rawReq = obj["request_npc"]) {
            null, is JsonNull -> null
            is JsonObject -> try { toNpcProposal(rawReq) }
                catch (e: Bad) { errors += "request_npc: ${e.message}"; null }
            else -> { errors += "'request_npc' deve ser um objeto"; null }
        }
        if (requestedNpc != null && npcProposal != null) errors += "use create_npc ou request_npc, não os dois"
        if (requestedNpc != null && (commands.isNotEmpty() || checkRequest != null || rollRequest != null)) {
            errors += "request_npc deve vir sozinho, sem proposals nem pedidos de rolagem"
        }
        val requestCombat = when (val rawCombat = obj["request_combat"]) {
            null, is JsonNull -> false
            is JsonPrimitive -> rawCombat.booleanOrNull ?: run {
                errors += "'request_combat' deve ser booleano"
                false
            }
            else -> {
                errors += "'request_combat' deve ser booleano"
                false
            }
        }
        val powerRequest = when (val rawPower = obj["request_power"]) {
            null, is JsonNull -> null
            is JsonObject -> {
                val id = rawPower.str("power")?.trim()?.takeIf { it.isNotEmpty() }
                val rawTarget = rawPower["target"]
                val target = when (rawTarget) {
                    null, is JsonNull -> null
                    is JsonPrimitive -> rawTarget.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }
                    else -> null
                }
                if (id == null) errors += "request_power: campo 'power' ausente ou vazio"
                if (rawTarget != null && rawTarget !is JsonNull && target == null) errors += "request_power: 'target' deve ser um id em texto"
                id?.let { PowerRequest(it, target) }
            }
            else -> {
                errors += "'request_power' deve ser um objeto"
                null
            }
        }
        if (powerRequest != null && (commands.isNotEmpty() || checkRequest != null || rollRequest != null || npcProposal != null || requestedNpc != null || requestCombat)) {
            errors += "request_power deve vir sozinho, sem proposals, create_npc, request_npc, request_combat nem pedidos de rolagem"
        }
        if (requestedNpc != null && requestCombat) {
            errors += "request_npc não inicia combate; para lutar use create_npc com request_combat"
        }
        if (npcProposal != null && (checkRequest != null || rollRequest != null)) {
            errors += "não combine create_npc com request_check/request_roll; crie o NPC em outra jogada"
        }
        if (requestCombat && npcProposal == null) {
            errors += "request_combat exige create_npc com a ficha do adversário"
        }
        if (requestCombat && (commands.isNotEmpty() || checkRequest != null || rollRequest != null)) {
            errors += "request_combat deve vir sozinho, sem proposals nem pedidos de rolagem"
        }
        return if (errors.isEmpty()) ParseResult.Ok(
            TurnOutput(narrative!!.trim(), commands, suggestions, checkRequest, rollRequest, npcProposal ?: requestedNpc, requestCombat, powerRequest, requestedNpc != null)
        ) else ParseResult.Invalid(errors)
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
        val rawId = o["id"]
        val customId = when (rawId) {
            null, is JsonNull -> null
            is JsonPrimitive -> rawId.takeIf { it.isString }?.content?.trim()
                ?.takeIf { NPC_ID.matches(it) }
                ?: throw Bad("campo 'id' deve ser um slug minúsculo (letras, números e hífen, 2 a 40 caracteres)")
            else -> throw Bad("campo 'id' deve ser texto")
        }
        return NpcProposal(
            id = customId ?: "pending",
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
            val hazard = if (!e.containsKey("hazard") || e["hazard"] is JsonNull) null
                else slug(e.str("hazard")?.takeIf { it.isNotBlank() } ?: throw Bad("table[$key]: 'hazard' deve ser o id do perfil em texto"))
            if (hazard != null && damage > 0) throw Bad("table[$key]: use 'hazard' ou 'damage', não ambos")
            table[face] = RollEntry(text, damage, conditions, hazard)
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
        val opposed = when (val raw = o["opposed"]) {
            null, is JsonNull -> null
            is JsonObject -> {
                val entityId = raw.str("entityId")?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("opposed.entityId ausente")
                val checkId = raw.str("check")?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("opposed.check ausente")
                OpposedCheckRequest(entityId, checkId, raw.str("difficulty")?.trim()?.takeIf { it.isNotEmpty() },
                    raw.str("label")?.trim()?.takeIf { it.isNotEmpty() })
            }
            else -> throw Bad("'opposed' deve ser um objeto")
        }
        val group = when (val raw = o["group"]) {
            null, is JsonNull -> null
            is JsonObject -> {
                val ids = raw["participantIds"] as? JsonArray ?: throw Bad("group.participantIds deve ser uma lista")
                val participants = ids.map { value ->
                    (value as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }
                        ?: throw Bad("group.participantIds deve conter ids em texto")
                }
                if (participants.size < 2) {
                    throw Bad("group.participantIds deve conter pelo menos dois participantes")
                }
                if (participants.distinct().size != participants.size) {
                    throw Bad("group.participantIds não pode repetir participantes")
                }
                if ("player" !in participants) {
                    throw Bad("group.participantIds deve incluir 'player' para resolver o resultado do personagem do jogador")
                }
                val required = raw["requiredSuccesses"]?.let {
                    (it as? JsonPrimitive)?.takeIf { p -> !p.isString }?.intOrNull
                } ?: throw Bad("group.requiredSuccesses deve ser inteiro")
                if (required !in 1..participants.size) {
                    throw Bad("group.requiredSuccesses deve ficar entre 1 e o número de participantes")
                }
                val success = raw.str("onSuccess")?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("group.onSuccess ausente")
                val failure = raw.str("onFailure")?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw Bad("group.onFailure ausente")
                GroupCheckRequest(participants, required, success, failure)
            }
            else -> throw Bad("'group' deve ser um objeto")
        }
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
            targetPlaceId = opt("targetPlace"),
            revealPlaceSecret = o.bool("revealPlaceSecret") ?: false,
            requiredStat = opt("requiredStat"),
            opposed = opposed,
            group = group
        ).also {
            if ((it.targetNpcId == null) != (it.revealSecretId == null)) {
                throw Bad("use targetNpc e revealSecret juntos")
            }
            if ((it.targetPlaceId == null) == it.revealPlaceSecret) {
                throw Bad("use targetPlace junto com revealPlaceSecret:true")
            }
            if (it.targetNpcId != null && (it.targetPlaceId != null || it.revealPlaceSecret)) {
                throw Bad("não combine segredo de NPC e de local")
            }
            if ((it.revealSecretId != null || it.revealPlaceSecret) && it.requiredStat.isNullOrBlank()) {
                throw Bad("revelar segredo exige requiredStat")
            }
            if ((it.opposed != null || it.group != null) &&
                (it.targetNpcId != null || it.revealSecretId != null || it.targetPlaceId != null || it.revealPlaceSecret)) {
                throw Bad("não combine teste oposto/em grupo com revelação de segredo")
            }
            if (it.opposed != null && it.group != null) throw Bad("use opposed ou group, não ambos")
        }
    }

    private fun toCommand(o: JsonObject): Command {
        val type = o.str("type") ?: throw Bad("campo 'type' ausente")
        fun s(k: String): String = o.str(k)?.takeIf { it.isNotBlank() } ?: throw Bad("$type: campo '$k' ausente ou vazio")
        fun n(k: String): Int = o.int(k) ?: throw Bad("$type: campo '$k' ausente ou não numérico")
        fun nOr(k: String, default: Int): Int = if (o.containsKey(k)) n(k) else default
        fun opt(k: String): String? = o.str(k)?.trim()?.takeIf { it.isNotEmpty() }
        return when (type) {
            "damage" -> Damage(s("entityId"), s("resource"), n("amount"))
            "heal" -> Heal(s("entityId"), s("resource"), n("amount"))
            "spend_resource" -> SpendResource(s("entityId"), s("resource"), n("cost"))
            "add_item" -> AddItem(s("ownerId"), s("itemId"), nOr("qty", 1))
            "consume_item" -> ConsumeItem(s("ownerId"), s("itemId"), nOr("qty", 1))
            "gain_xp" -> GainXp(s("entityId"), n("amount"))
            "revive" -> Revive(s("entityId"), s("reason"))
            "record_fact" -> RecordFact(s("key"), o.str("value") ?: throw Bad("record_fact: campo 'value' ausente"))
            "record_memory" -> NoteMemory(s("subjectId"), s("text"))
            "learn" -> LearnKnowledge(s("key"), s("text"), s("source"), o.bool("believed") ?: false)
            "make_promise" -> MakePromise(s("id"), s("npcId"), s("text"))
            "keep_promise" -> KeepPromise(s("id"))
            "add_condition" -> AddCondition(s("entityId"), s("condition"))
            "remove_condition" -> RemoveCondition(s("entityId"), s("condition"))
            "change_relation" -> ChangeRelation(s("fromId"), s("toId"), n("delta"), s("reason"))
            "create_clock" -> CreateClock(s("id"), s("name"), n("segments"), o.str("publicClue")?.trim().orEmpty(), nOr("restTick", 0), nOr("moveTick", 0), nOr("chapterTick", 0), o.str("midClue")?.trim().orEmpty(), o.str("consequence")?.trim().orEmpty(), nOr("combatTick", 0), nOr("questTick", 0))
            "advance_clock" -> AdvanceClock(s("id"), nOr("by", 1))
            "add_place" -> AddPlace(
                Place(
                    id = slug(s("id")),
                    name = s("name"),
                    description = o.str("description")?.trim().orEmpty(),
                    secret = o.str("secret")?.trim().orEmpty()
                )
            )
            "connect_places" -> ConnectPlaces(slug(s("fromId")), slug(s("toId")))
            "discover_place" -> DiscoverPlace(slug(s("id")))
            "propose_rule" -> {
                val rawEffects = o["effects"] as? JsonObject ?: throw Bad("propose_rule: campo 'effects' deve ser um objeto escopo -> bônus")
                val effects = rawEffects.mapValues { (scope, value) ->
                    val primitive = value as? JsonPrimitive
                    if (primitive == null || primitive.isString) throw Bad("propose_rule: efeito '$scope' deve ser inteiro")
                    primitive.intOrNull ?: throw Bad("propose_rule: efeito '$scope' deve ser inteiro")
                }
                ProposeRuleAddition(
                    RegraAdicional(
                        id = slug(s("id")),
                        name = s("name"),
                        description = s("description"),
                        effects = effects,
                        limit = s("limit"),
                        origin = RuleOrigin.ADAPTACAO,
                        approved = false
                    )
                )
            }
            "roll", "dice", "check", "set_hp", "set_resource", "create_entity", "start_combat", "end_combat", "next_turn" ->
                throw Bad("'$type' não é permitido à IA: dados, testes e combate são decididos pelo motor")
            else -> throw Bad("tipo desconhecido '$type' (permitidos: ${ProposalSchema.TYPES.joinToString(", ")})")
        }
    }
}
