package dev.rpgsolo.rules

/**
 * Dificuldade configurável. Só parâmetros documentados da campanha são aceitos.
 * Nenhum parâmetro permite alterar atributos de NPC durante a luta.
 */
data class DifficultySetting(val levelId: String, val params: Map<String, Double> = emptyMap())

val ALLOWED_DIFFICULTY_PARAMS = setOf("resource_scarcity", "danger_level", "enemy_tactics", "defeat_consequence")

/** Configuração escolhida pelo jogador (livre ou por formulário). */
data class CampaignConfig(
    val systemFamily: String,
    val editionId: String? = null,
    val settingIds: List<String> = emptyList(),
    val structureIds: List<String> = emptyList(),
    val philosophyIds: List<String> = emptyList(),
    val toneIds: List<String> = emptyList(),
    val difficulty: DifficultySetting,
    val progressionId: String? = null,
    /** Limites de conteúdo: independentes de gênero, dificuldade e regras. */
    val contentLimits: List<String> = emptyList()
)

sealed interface ConfigResult {
    data class Ready(
        val edition: SystemEdition,
        val compatibility: Compatibility,
        /** Notas que o jogador deve ver antes de começar (adaptações, combinações não verificadas). */
        val notes: List<String>
    ) : ConfigResult
    /** Falta uma informação que muda as regras: perguntar ao jogador. */
    data class NeedsQuestion(val question: String, val options: List<String>) : ConfigResult
    data class Invalid(val errors: List<String>) : ConfigResult
}

fun resolveConfig(catalog: Catalog, c: CampaignConfig): ConfigResult {
    val errors = mutableListOf<String>()
    fun check(ids: List<String>, dim: String) = ids.forEach {
        val e = catalog.entry(it)
        if (e == null) errors += "id inexistente: $it" else if (e.dimension != dim) errors += "$it não é da dimensão $dim"
    }
    check(c.settingIds, "setting"); check(c.structureIds, "structure"); check(c.philosophyIds, "philosophy")
    check(c.toneIds, "tone"); check(listOf(c.difficulty.levelId), "difficulty")
    c.progressionId?.let { check(listOf(it), "progression") }
    c.difficulty.params.keys.filter { it !in ALLOWED_DIFFICULTY_PARAMS }
        .forEach { errors += "parâmetro de dificuldade não permitido: $it" }
    c.difficulty.params.forEach { (key, value) ->
        if (!value.isFinite() || value !in 0.0..10.0) {
            errors += "parâmetro de dificuldade '$key' deve ser um número finito entre 0 e 10"
        }
    }
    if (c.contentLimits.size > 30 || c.contentLimits.any { it.isBlank() || it.length > 200 }) {
        errors += "limites de conteúdo: máximo 30 itens, cada um com 1 a 200 caracteres"
    }

    val editions = catalog.editionsOf(c.systemFamily)
    if (editions.isEmpty()) errors += "sistema desconhecido: ${c.systemFamily}"
    if (errors.isNotEmpty()) return ConfigResult.Invalid(errors)

    val edition = when {
        c.editionId != null -> editions.firstOrNull { it.id == c.editionId }
            ?: return ConfigResult.Invalid(listOf("a edição ${c.editionId} não pertence ao sistema ${c.systemFamily}"))
        editions.size == 1 -> editions.first()
        else -> return ConfigResult.NeedsQuestion(
            "Qual edição de ${editions.first().name} você quer usar? As regras mudam entre elas.",
            editions.map { it.id }
        )
    }

    val notes = mutableListOf<String>()
    var worst = Compatibility.NATIVE
    val settings = if (c.settingIds.isEmpty()) emptyList() else c.settingIds
    for (s in settings) {
        val rule = catalog.compatibility(edition.id, s)
        if (rule.status.severity > worst.severity) worst = rule.status
        when (rule.status) {
            Compatibility.BLOCKED -> errors += "combinação bloqueada: ${edition.id} + $s (${rule.note})"
            Compatibility.ADAPTED -> notes += "$s exige adaptação: ${rule.note}"
            Compatibility.UNVERIFIED -> notes += "$s não foi verificada com ${edition.id}; regras adicionais serão propostas e precisam de aprovação"
            Compatibility.NATIVE -> {}
        }
    }
    if (errors.isNotEmpty()) return ConfigResult.Invalid(errors)
    if (edition.licenseStatus == LicenseStatus.PENDENTE) notes += "licença de ${edition.id} ainda não verificada"
    return ConfigResult.Ready(edition, worst, notes)
}
