package dev.rpgsolo.ai

import dev.rpgsolo.rules.Catalog
import dev.rpgsolo.rules.CampaignConfig
import dev.rpgsolo.rules.ConfigResult
import dev.rpgsolo.rules.DifficultySetting
import dev.rpgsolo.rules.resolveConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Fase 9.1: rascunho de campanha montado a partir da descrição livre do jogador.
 * Tudo aqui é proposta até passar por [CampaignInterpreter.resolve], que usa o catálogo e `resolveConfig`.
 */
data class CampaignDraft(
    val description: String,
    val systemFamily: String? = null,
    val editionId: String? = null,
    val settingIds: List<String> = emptyList(),
    val structureIds: List<String> = emptyList(),
    val philosophyIds: List<String> = emptyList(),
    val toneIds: List<String> = emptyList(),
    val difficultyId: String? = null,
    val progressionId: String? = null,
    val contentLimits: List<String> = emptyList(),
    /** Ambientação ou mistura que o catálogo não cobre (4A.1.3): guardada como texto livre, nunca descartada. */
    val customSetting: String? = null,
    /** Conceito do personagem, se o jogador descreveu (ex.: "anão minerador de máquinas de combate"). */
    val concept: String? = null,
    /** Ids que a IA citou e não existem no catálogo. Mostrados ao jogador, nunca inventados. */
    val ignored: List<String> = emptyList()
)

enum class DraftField { SYSTEM, EDITION, DIFFICULTY }

sealed interface Interpretation {
    /** Tudo resolvido pelo catálogo; [notes] (adaptações, combinação não verificada) o jogador vê antes de começar. */
    data class Ready(val draft: CampaignDraft, val config: CampaignConfig, val resolved: ConfigResult.Ready) : Interpretation

    /** Falta uma escolha que muda as regras: o jogador responde e chama [CampaignInterpreter.answer]. */
    data class Ask(val field: DraftField, val question: String, val options: List<Option>, val draft: CampaignDraft) : Interpretation

    /** Combinação bloqueada ou dados inválidos. Nada foi criado. */
    data class Invalid(val errors: List<String>, val draft: CampaignDraft) : Interpretation

    /** Rede, chave ou resposta ilegível. Nada foi criado. */
    data class Failed(val reason: String, val error: AiError? = null) : Interpretation

    data class Option(val id: String, val label: String)
}

class CampaignInterpreter(private val provider: AiProvider, private val maxTokens: Int = 600) {

    /** 9.1.3: a IA só mapeia a descrição para ids do catálogo; o motor confirma e pergunta o que faltar. */
    fun interpret(description: String, catalog: Catalog): Interpretation {
        if (description.isBlank()) return Interpretation.Failed("descreva a campanha que você quer jogar")
        val raw = try {
            provider.complete(
                AiRequest(
                    listOf(AiMessage(Role.SYSTEM, systemPrompt(catalog)), AiMessage(Role.USER, description.trim())),
                    maxTokens = maxTokens, temperature = 0.0, jsonMode = true
                )
            ).text
        } catch (e: AiError) {
            return Interpretation.Failed(e.message ?: "erro de IA", e)
        }
        val draft = parse(raw, description.trim(), catalog)
            ?: return Interpretation.Failed("não entendi a resposta da IA; tente descrever de novo")
        return resolve(draft, catalog)
    }

    /** Resposta do jogador a uma pergunta. Determinístico, sem IA. Opção fora da lista é recusada. */
    fun answer(ask: Interpretation.Ask, optionId: String, catalog: Catalog): Interpretation {
        if (ask.options.none { it.id == optionId }) {
            return Interpretation.Invalid(listOf("opção inválida: $optionId"), ask.draft)
        }
        val d = ask.draft
        val next = when (ask.field) {
            // Trocar de sistema invalida a edição anterior.
            DraftField.SYSTEM -> d.copy(systemFamily = optionId, editionId = null)
            DraftField.EDITION -> d.copy(editionId = optionId)
            DraftField.DIFFICULTY -> d.copy(difficultyId = optionId)
        }
        return resolve(next, catalog)
    }

    /** Valida pelo catálogo. Ordem das perguntas: sistema, dificuldade, edição (esta vem do `resolveConfig`). */
    fun resolve(draft: CampaignDraft, catalog: Catalog): Interpretation {
        val family = draft.systemFamily?.takeIf { it in catalog.families() }
        if (family == null) {
            val opts = catalog.families().sorted().map { f ->
                Interpretation.Option(f, catalog.editionsOf(f).first().name)
            }
            return Interpretation.Ask(DraftField.SYSTEM, "Qual sistema de regras você quer usar?", opts, draft.copy(systemFamily = null, editionId = null))
        }
        val difficulty = draft.difficultyId?.takeIf { catalog.entry(it)?.dimension == "difficulty" }
        if (difficulty == null) {
            val opts = catalog.entries.filter { it.dimension == "difficulty" }.map { Interpretation.Option(it.id, it.name) }
            return Interpretation.Ask(DraftField.DIFFICULTY, "Qual dificuldade?", opts, draft.copy(systemFamily = family, difficultyId = null))
        }
        val config = CampaignConfig(
            systemFamily = family,
            editionId = draft.editionId,
            settingIds = draft.settingIds,
            structureIds = draft.structureIds,
            philosophyIds = draft.philosophyIds,
            toneIds = draft.toneIds,
            difficulty = DifficultySetting(difficulty),
            progressionId = draft.progressionId,
            contentLimits = draft.contentLimits
        )
        val clean = draft.copy(systemFamily = family, difficultyId = difficulty)
        return when (val r = resolveConfig(catalog, config)) {
            is ConfigResult.Ready -> Interpretation.Ready(clean, config, r)
            is ConfigResult.NeedsQuestion -> Interpretation.Ask(
                DraftField.EDITION, r.question,
                r.options.map { id -> Interpretation.Option(id, catalog.system(id)?.let { "${it.name} — ${it.edition}" } ?: id) },
                clean
            )
            is ConfigResult.Invalid -> Interpretation.Invalid(r.errors, clean)
        }
    }

    internal fun parse(raw: String, description: String, catalog: Catalog): CampaignDraft? {
        val json = ProposalParser.extractJson(raw) ?: return null
        val o = try {
            Json.parseToJsonElement(json) as? JsonObject
        } catch (e: Exception) {
            null
        } ?: return null

        val ignored = mutableListOf<String>()
        fun text(k: String): String? = (o[k] as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }
        fun ids(k: String, dim: String): List<String> =
            ((o[k] as? JsonArray).orEmpty().mapNotNull { (it as? JsonPrimitive)?.takeIf { p -> p.isString }?.content?.trim() })
                .distinct()
                .filter { id ->
                    val ok = catalog.entry(id)?.dimension == dim
                    if (!ok) ignored += id
                    ok
                }

        val family = text("system")?.let { f -> if (f in catalog.families()) f else { ignored += f; null } }
        val edition = text("edition")?.let { e ->
            val ok = family != null && catalog.editionsOf(family).any { it.id == e }
            if (!ok) ignored += e
            e.takeIf { ok }
        }
        val difficulty = text("difficulty")?.let { d -> if (catalog.entry(d)?.dimension == "difficulty") d else { ignored += d; null } }
        val progression = text("progression")?.let { d -> if (catalog.entry(d)?.dimension == "progression") d else { ignored += d; null } }
        val limits = (o["limits"] as? JsonArray).orEmpty()
            .mapNotNull { (it as? JsonPrimitive)?.takeIf { p -> p.isString }?.content?.trim() }
            .filter { it.isNotEmpty() && it.length <= 120 }.distinct().take(8)

        return CampaignDraft(
            description = description,
            systemFamily = family,
            editionId = edition,
            settingIds = ids("settings", "setting"),
            structureIds = ids("structures", "structure"),
            philosophyIds = ids("philosophies", "philosophy"),
            toneIds = ids("tones", "tone"),
            difficultyId = difficulty,
            progressionId = progression,
            contentLimits = limits,
            customSetting = text("custom_setting"),
            concept = text("concept"),
            ignored = ignored.toList()
        )
    }

    private fun systemPrompt(catalog: Catalog): String = buildString {
        append("Você converte a descrição de uma campanha de RPG solo em uma configuração. ")
        append("Responda SOMENTE com JSON: {\"system\": familia|null, \"edition\": id|null, \"settings\": [ids], \"structures\": [ids], ")
        append("\"philosophies\": [ids], \"tones\": [ids], \"difficulty\": id|null, \"progression\": id|null, ")
        append("\"limits\": [\"limite de conteúdo em texto curto\"], \"custom_setting\": \"texto\"|null, \"concept\": \"texto\"|null}.\n")
        append("Use SOMENTE ids das listas abaixo. Se o jogador não disse o sistema, a edição ou a dificuldade, use null: nunca chute.\n")
        append("Se a ambientação pedida não estiver na lista (ou for uma mistura), deixe em 'custom_setting' com as palavras do jogador.\n")
        append("'concept' é o personagem que ele quer interpretar, se mencionou.\n\n")
        append("Sistemas (familia: edições):\n")
        catalog.families().sorted().forEach { f ->
            append("- ").append(f).append(": ")
            append(catalog.editionsOf(f).joinToString { "${it.id} (${it.edition})" }).append('\n')
        }
        listOf("setting" to "Ambientações", "structure" to "Estruturas", "philosophy" to "Filosofias", "tone" to "Tons",
            "difficulty" to "Dificuldades", "progression" to "Progressões").forEach { (dim, title) ->
            append('\n').append(title).append(":\n")
            catalog.entries.filter { it.dimension == dim }.forEach { append("- ").append(it.id).append(" = ").append(it.name).append('\n') }
        }
    }
}
