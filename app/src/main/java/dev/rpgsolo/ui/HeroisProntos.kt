package dev.rpgsolo.ui

import org.json.JSONObject

/** Herói pronto de um mundo (M5): título, conceito, reino e as escolhas da criação. Sem nome próprio; o jogador batiza. */
data class HeroiPronto(
    val id: String,
    val titulo: String,
    val conceito: String,
    val reinoId: String,
    val escolhas: CharacterChoices,
)

object HeroisProntos {
    private val raiz: JSONObject? by lazy {
        runCatching {
            HeroisProntos::class.java.classLoader
                ?.getResourceAsStream("mundos/mythralis/herois-prontos.json")
                ?.bufferedReader(Charsets.UTF_8)?.use { JSONObject(it.readText()) }
        }.getOrNull()
    }

    /** Os heróis prontos de Mythralis, na ordem do arquivo; lista vazia se o recurso faltar ou estiver malformado. */
    val mythralis: List<HeroiPronto> by lazy { parse(raiz) }

    internal fun parse(obj: JSONObject?): List<HeroiPronto> {
        val lista = obj?.optJSONArray("herois") ?: return emptyList()
        return (0 until lista.length()).mapNotNull { i ->
            val h = lista.optJSONObject(i) ?: return@mapNotNull null
            val id = h.optString("id")
            val escolhas = h.optJSONObject("escolhas")?.toString()?.let { CharacterChoices.fromJson(it) }
            if (id.isBlank() || escolhas == null || escolhas.raceId.isBlank()) return@mapNotNull null
            HeroiPronto(id, h.optString("titulo"), h.optString("conceito"), h.optString("reinoId"), escolhas)
        }
    }

    /** Mensagens de erro de cada herói contra o catálogo ativo; lista vazia = todos válidos. */
    fun validar(
        herois: List<HeroiPronto>,
        validador: (CharacterChoices) -> List<String>,
    ): List<String> = herois.flatMap { h -> validador(h.escolhas).map { "${h.id}: $it" } }
}
