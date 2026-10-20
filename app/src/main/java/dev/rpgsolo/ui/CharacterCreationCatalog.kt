package dev.rpgsolo.ui

import org.json.JSONObject

/** Entrada editorial do catálogo de criação; efeitos mecânicos só vêm do motor de regras. */
data class CharacterCatalogEntry(
    val id: String,
    val name: String,
    val summary: String,
    val parentRaceId: String? = null
)

data class CharacterChoices(
    val raceId: String = "",
    val subraceId: String = "",
    val classId: String = "",
    val professionId: String = "",
    val curseIds: List<String> = emptyList()
) {
    fun toJson(): String = JSONObject()
        .put("raceId", raceId)
        .put("subraceId", subraceId)
        .put("classId", classId)
        .put("professionId", professionId)
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
            CharacterCatalogEntry(id, item.optString("name", id), item.optString("summary"), item.optString("parentRaceId").takeIf { it.isNotBlank() && it != "null" })
        }
    }

    val races: List<CharacterCatalogEntry> get() = entries("races")
    val subraces: List<CharacterCatalogEntry> get() = entries("subraces")
    val classes: List<CharacterCatalogEntry> get() = entries("classes")
    val professions: List<CharacterCatalogEntry> get() = entries("professions")
    val curses: List<CharacterCatalogEntry> get() = entries("curses")

    fun find(id: String): CharacterCatalogEntry? =
        (races + subraces + classes + professions + curses).firstOrNull { it.id == id }
}
