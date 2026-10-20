package dev.rpgsolo.ui

import org.json.JSONObject

/** Entrada editorial do catálogo de criação; efeitos mecânicos só vêm do motor de regras. */
data class CharacterCatalogEntry(
    val id: String,
    val name: String,
    val summary: String,
    val parentRaceId: String? = null,
    val lore: String = "",
    val benefit: String = "",
    val drawback: String = "",
    val hybridRaceIds: List<String> = emptyList()
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
                hybridRaceIds = item.optJSONArray("raceIds")?.let { arr -> (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() } }.orEmpty()
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

    /** Valida IDs únicos e referências de linhagem antes de expor o catálogo à criação. */
    fun validationErrors(): List<String> {
        val all = races + subraces + hybrids + classes + professions + curses
        val errors = mutableListOf<String>()
        all.groupBy { it.id }.filterValues { it.size > 1 }.keys.forEach { errors += "ID duplicado no catálogo: $it" }
        val raceIds = races.map { it.id }.toSet()
        subraces.filter { it.parentRaceId == null || it.parentRaceId !in raceIds }.forEach { errors += "Sub-raça ${it.id} referencia raça inexistente" }
        hybrids.forEach { hybrid ->
            if (hybrid.hybridRaceIds.size != 2 || hybrid.hybridRaceIds.toSet().size != 2) errors += "Híbrido ${hybrid.id} precisa referenciar duas raças distintas"
            else if (hybrid.hybridRaceIds.any { it !in raceIds }) errors += "Híbrido ${hybrid.id} referencia raça inexistente"
        }
        return errors
    }
}
