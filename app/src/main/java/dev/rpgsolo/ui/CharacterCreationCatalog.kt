package dev.rpgsolo.ui

import org.json.JSONObject
import dev.rpgsolo.rules.RulePackage

/** Efeito tipado declarado no catálogo. Declarar não significa aplicar ao motor. */
data class CharacterCatalogEffect(
    val scope: String,
    val bonus: Int,
    val source: String = "entry",
    /** Caminho estável até a declaração no JSON; não depende do texto de lore. */
    val effectId: String = ""
)

/** Entrada editorial do catálogo de criação; efeitos mecânicos só vêm do motor de regras. */
data class CharacterCatalogEntry(
    val id: String,
    val name: String,
    val summary: String,
    val parentRaceId: String? = null,
    val lore: String = "",
    val benefit: String = "",
    val drawback: String = "",
    val hybridRaceIds: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val drawbacks: List<String> = emptyList(),
    val mechanicalEffects: List<CharacterCatalogEffect> = emptyList()
)

data class CharacterChoices(
    val raceId: String = "",
    val subraceId: String = "",
    val classId: String = "",
    val professionId: String = "",
    val curseIds: List<String> = emptyList(),
    val hybridId: String = ""
) {
    fun toJson(): String = JSONObject()
        .put("raceId", raceId)
        .put("subraceId", subraceId)
        .put("classId", classId)
        .put("professionId", professionId)
        .put("hybridId", hybridId)
        .put("curseIds", org.json.JSONArray(curseIds))
        .toString()

    companion object {
        fun fromJson(raw: String?): CharacterChoices {
            if (raw.isNullOrBlank()) return CharacterChoices()
            return runCatching {
                val obj = JSONObject(raw)
                val curses = obj.optJSONArray("curseIds")
                CharacterChoices(
                    raceId = obj.optString("raceId"),
                    subraceId = obj.optString("subraceId"),
                    classId = obj.optString("classId"),
                    professionId = obj.optString("professionId"),
                    hybridId = obj.optString("hybridId"),
                    curseIds = (0 until (curses?.length() ?: 0)).map { curses!!.optString(it) }.filter { it.isNotBlank() }
                )
            }.getOrDefault(CharacterChoices())
        }
    }
}

/** Lê o catálogo versionado empacotado no módulo rules, sem criar regras mecânicas novas. */
object CharacterCreationCatalog {
    private val catalog: JSONObject? by lazy {
        runCatching {
            CharacterCreationCatalog::class.java.classLoader
                ?.getResourceAsStream("packages/sistema-proprio/personagem-catalogo.json")
                ?.bufferedReader(Charsets.UTF_8)?.use { JSONObject(it.readText()) }
        }.getOrNull()
    }

    private fun parseEffects(array: org.json.JSONArray?, source: String): List<CharacterCatalogEffect> {
        if (array == null) return emptyList()
        return (0 until array.length()).mapNotNull { index ->
            val effect = array.optJSONObject(index) ?: return@mapNotNull null
            val scope = effect.optString("scope").trim()
            val rawBonus = effect.opt("bonus")
            val bonus = if (rawBonus is Number && rawBonus.toDouble() % 1.0 == 0.0) rawBonus.toInt() else null
            if (scope.isBlank() || bonus == null) null else CharacterCatalogEffect(
                scope = scope,
                bonus = bonus,
                source = source,
                effectId = "$source#$index"
            )
        }
    }

    private fun entries(key: String): List<CharacterCatalogEntry> {
        val array = catalog?.optJSONArray(key) ?: return emptyList()
        return (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val id = item.optString("id").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            CharacterCatalogEntry(
                id = id,
                name = item.optString("name", id),
                summary = item.optString("summary"),
                parentRaceId = item.optString("parentRaceId").takeIf { it.isNotBlank() && it != "null" },
                lore = item.optString("lore"),
                benefit = item.optJSONArray("benefits")?.optJSONObject(0)?.optString("description").orEmpty(),
                drawback = item.optJSONArray("drawbacks")?.optJSONObject(0)?.optString("description").orEmpty(),
                hybridRaceIds = item.optJSONArray("raceIds")?.let { arr -> (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() } }.orEmpty(),
                benefits = item.optJSONArray("benefits")?.let { arr ->
                    (0 until arr.length()).mapNotNull { arr.optJSONObject(it)?.optString("description")?.takeIf { text -> text.isNotBlank() } }
                }.orEmpty(),
                drawbacks = item.optJSONArray("drawbacks")?.let { arr ->
                    (0 until arr.length()).mapNotNull { arr.optJSONObject(it)?.optString("description")?.takeIf { text -> text.isNotBlank() } }
                }.orEmpty(),
                mechanicalEffects = parseEffects(item.optJSONArray("mechanicalEffects"), "entrada:" + id).toMutableList().apply {
                    listOf("benefits", "drawbacks").forEach { field ->
                        val details = item.optJSONArray(field)
                        if (details != null) {
                            for (detailIndex in 0 until details.length()) {
                                val detail = details.optJSONObject(detailIndex) ?: continue
                                val description = detail.optString("description", field).takeIf { it.isNotBlank() } ?: field
                                addAll(parseEffects(detail.optJSONArray("mechanicalEffects"), "$field:" + id + ": $description"))
                            }
                        }
                    }
                }
            )
        }
    }

    val races: List<CharacterCatalogEntry> get() = entries("races")
    val subraces: List<CharacterCatalogEntry> get() = entries("subraces")
    val hybrids: List<CharacterCatalogEntry> get() = entries("hybrids")
    val classes: List<CharacterCatalogEntry> get() = entries("classes")
    val professions: List<CharacterCatalogEntry> get() = entries("professions")
    val curses: List<CharacterCatalogEntry> get() = entries("curses")

    fun find(id: String): CharacterCatalogEntry? =
        (races + subraces + hybrids + classes + professions + curses).firstOrNull { it.id == id }

    /**
     * Retorna somente efeitos explicitamente declarados nas escolhas atuais.
     * Não infere herança, não converte lore em regra e não deduplica efeitos de fontes distintas:
     * a política de acúmulo deve ser aplicada pelo resolvedor mecânico, não pelo catálogo.
     */
    fun selectedEffects(choices: CharacterChoices): List<CharacterCatalogEffect> {
        val selectedIds = listOf(
            choices.raceId,
            choices.subraceId,
            choices.hybridId,
            choices.classId,
            choices.professionId
        ) + choices.curseIds
        return selectedIds
            .filter { it.isNotBlank() }
            .distinct()
            .mapNotNull(::find)
            .flatMap { entry ->
                entry.mechanicalEffects.map { effect ->
                    effect.copy(
                        source = entry.id + "/" + effect.source,
                        effectId = entry.id + "/" + effect.effectId
                    )
                }
            }
    }

    /** Valida escolhas do jogador contra categorias e referências do catálogo, sem inferir regras mecânicas. */
    fun validateChoices(choices: CharacterChoices): List<String> {
        val errors = validationErrors().toMutableList()
        if (choices.raceId.isNotBlank() && races.none { it.id == choices.raceId }) errors += "Raça selecionada não existe no catálogo"
        val subrace = choices.subraceId.takeIf { it.isNotBlank() }?.let { id -> subraces.firstOrNull { it.id == id } }
        if (choices.subraceId.isNotBlank() && subrace == null) errors += "Sub-raça selecionada não existe no catálogo"
        else if (subrace != null && subrace.parentRaceId != choices.raceId) errors += "A sub-raça não pertence à raça selecionada"
        if (choices.hybridId.isNotBlank() && hybrids.none { it.id == choices.hybridId }) errors += "Híbrido selecionado não existe no catálogo"
        if (choices.hybridId.isNotBlank() && (choices.raceId.isNotBlank() || choices.subraceId.isNotBlank())) errors += "Escolha raça/sub-raça ou híbrido, não ambos"
        if (choices.classId.isNotBlank() && classes.none { it.id == choices.classId }) errors += "Classe selecionada não existe no catálogo"
        if (choices.professionId.isNotBlank() && professions.none { it.id == choices.professionId }) errors += "Profissão selecionada não existe no catálogo"
        choices.curseIds.distinct().filter { id -> curses.none { it.id == id } }.forEach { errors += "Maldição selecionada não existe no catálogo: $it" }
        if (choices.curseIds.size != choices.curseIds.distinct().size) errors += "Há maldições duplicadas na seleção"
        return errors.distinct()
    }

    private fun validateEffects(array: org.json.JSONArray, location: String, errors: MutableList<String>) {
        val seen = mutableSetOf<String>()
        for (index in 0 until array.length()) {
            val effect = array.optJSONObject(index)
            if (effect == null) {
                errors += "$location[$index] deve ser um objeto"
                continue
            }
            val scope = effect.optString("scope").trim()
            if (!Regex("[a-z0-9][a-z0-9_-]*").matches(scope)) {
                errors += "$location[$index] possui scope inválido"
            } else if (!seen.add(scope)) {
                errors += "$location declara scope duplicado: $scope"
            }
            val rawBonus = effect.opt("bonus")
            val bonus = if (rawBonus is Number && rawBonus.toDouble().isFinite() && rawBonus.toDouble() % 1.0 == 0.0) rawBonus.toInt() else null
            if (bonus == null || bonus !in -100..100) errors += "$location[$index] deve declarar bonus inteiro entre -100 e 100"
        }
    }

    /**
     * Confere escopos tipados declarados contra o pacote realmente selecionado.
     * Isto só valida compatibilidade; não aplica os efeitos ao motor.
     */
    fun validateEffectScopes(pkg: RulePackage, choices: CharacterChoices): List<String> {
        val selectedIds = listOf(choices.raceId, choices.subraceId, choices.hybridId, choices.classId, choices.professionId) + choices.curseIds
        // O resolvedor de testes só consome escopos de atributo, teste, perícia e combate.
        // Um ID de vantagem/poder não é, por si só, um escopo executável.
        val validScopes = pkg.data.stats.map { it.id }.toSet() +
            pkg.data.checks.map { it.id } +
            pkg.data.traits.filter { it.type == "pericia" }.map { it.id } +
            setOf("ataque", "dano", "defesa", "resistencia_dano")
        return selectedIds.filter { it.isNotBlank() }.distinct()
            .mapNotNull(::find)
            .flatMap { entry ->
                entry.mechanicalEffects.filter { it.scope !in validScopes }.map {
                    "Efeito declarado em ${entry.name} usa escopo '${it.scope}', inexistente no pacote ${pkg.manifest.id}"
                }
            }.distinct()
    }

    /**
     * Valida o documento bruto antes de expor as opções.
     * Entradas malformadas não podem desaparecer silenciosamente durante o parsing.
     * Este validador verifica estrutura editorial; nunca interpreta lore como regra.
     */
    fun validationErrors(): List<String> {
        val root = catalog ?: return listOf("O catálogo de criação não foi carregado do pacote de regras")
        val errors = mutableListOf<String>()
        if (root.optInt("schemaVersion", -1) != 1) errors += "Versão de esquema do catálogo ausente ou incompatível"
        if (root.optString("catalogId").isBlank()) errors += "catalogId obrigatório ausente"
        if (root.optString("packageId") != "sistema-proprio") errors += "packageId do catálogo não corresponde a sistema-proprio"

        val categoryKeys = listOf("races", "subraces", "hybrids", "classes", "professions", "curses")
        val rawEntries = mutableMapOf<String, List<JSONObject>>()
        categoryKeys.forEach { key ->
            val array = root.optJSONArray(key)
            if (array == null) {
                errors += "Categoria obrigatória ausente ou inválida: $key"
                rawEntries[key] = emptyList()
            } else {
                val entries = mutableListOf<JSONObject>()
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index)
                    if (item == null) {
                        errors += "Entrada $index inválida na categoria $key"
                        continue
                    }
                    entries += item
                    val id = item.optString("id").takeIf { it.isNotBlank() && it != "null" }
                    if (id == null) errors += "Entrada $index em $key não possui ID"
                    if (item.optString("name").isBlank() || item.optString("name") == "null") {
                        errors += "Entrada ${id ?: index} em $key não possui nome"
                    }
                    if (item.optString("summary").isBlank() || item.optString("summary") == "null") {
                        errors += "Entrada ${id ?: index} em $key não possui resumo"
                    }
                    if (item.optString("lore").isBlank() || item.optString("lore") == "null") {
                        errors += "Entrada ${id ?: index} em $key não possui lore"
                    }
                    if (item.optJSONArray("traits") == null) {
                        errors += "Entrada ${id ?: index} em $key não declara traits como lista"
                    }
                    val effects = item.optJSONArray("mechanicalEffects")
                    if (effects == null) {
                        errors += "Entrada ${id ?: index} em $key não declara mechanicalEffects como lista"
                    } else {
                        validateEffects(effects, "mechanicalEffects da entrada ${id ?: index} em $key", errors)
                    }
                    listOf("benefits", "drawbacks").forEach { field ->
                        val value = item.opt(field)
                        if (value != null && value != JSONObject.NULL) {
                            val descriptions = item.optJSONArray(field)
                            if (descriptions == null) {
                                errors += "Campo $field da entrada ${id ?: index} em $key deve ser uma lista"
                            } else {
                                for (effectIndex in 0 until descriptions.length()) {
                                    val detail = descriptions.optJSONObject(effectIndex)
                                    val description = detail?.optString("description")
                                    if (description.isNullOrBlank() || description == "null") {
                                        errors += "Descrição ausente em $field[$effectIndex] da entrada ${id ?: index}"
                                    }
                                    if (detail != null && detail.has("mechanicalEffects") && !detail.isNull("mechanicalEffects")) {
                                        val nested = detail.optJSONArray("mechanicalEffects")
                                        if (nested == null) {
                                            errors += "mechanicalEffects de $field[$effectIndex] da entrada ${id ?: index} deve ser uma lista"
                                        } else {
                                            validateEffects(nested, "mechanicalEffects de $field[$effectIndex] da entrada ${id ?: index}", errors)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                rawEntries[key] = entries
            }
        }

        val allIds = rawEntries.values.flatten().mapNotNull { it.optString("id").takeIf(String::isNotBlank) }
        allIds.groupBy { it }.filterValues { it.size > 1 }.keys.forEach { errors += "ID duplicado no catálogo: $it" }

        val raceIds = rawEntries["races"].orEmpty().mapNotNull { it.optString("id").takeIf(String::isNotBlank) }.toSet()
        rawEntries["subraces"].orEmpty().forEach { item ->
            val id = item.optString("id", "(sem ID)")
            val parentId = item.optString("parentRaceId").takeIf { it.isNotBlank() && it != "null" }
            if (parentId == null || parentId !in raceIds) errors += "Sub-raça $id referencia raça inexistente"
        }
        rawEntries["hybrids"].orEmpty().forEach { item ->
            val id = item.optString("id", "(sem ID)")
            val ids = item.optJSONArray("raceIds")?.let { arr ->
                (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() && it != "null" }
            }.orEmpty()
            if (ids.size != 2 || ids.toSet().size != 2) errors += "Híbrido $id precisa referenciar duas raças distintas"
            else if (ids.any { it !in raceIds }) errors += "Híbrido $id referencia raça inexistente"
        }

        // Detecta divergência entre o documento bruto e as opções efetivamente carregadas.
        val expectedByCategory = rawEntries.mapValues { (_, entries) -> entries.size }
        val loadedByCategory = mapOf(
            "races" to races.size,
            "subraces" to subraces.size,
            "hybrids" to hybrids.size,
            "classes" to classes.size,
            "professions" to professions.size,
            "curses" to curses.size
        )
        expectedByCategory.forEach { (key, expected) ->
            val loaded = loadedByCategory[key] ?: 0
            if (loaded != expected) errors += "Categoria $key: $expected entradas no arquivo, mas $loaded carregadas"
        }
        return errors.distinct()
    }
}
