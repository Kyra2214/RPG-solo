package dev.rpgsolo.rules

/** Entrada de estilo: ambientação, estrutura de campanha, filosofia, tom, dificuldade ou progressão. */
data class CatalogEntry(val id: String, val dimension: String, val category: String, val name: String)

enum class ResearchStatus { PESQUISADO_PARCIAL, PENDENTE }
enum class LicenseStatus { VERIFICADA, INDICADA, PENDENTE }

/** Uma edição de um sistema. Famílias diferentes (ex.: 3det e tormenta) nunca compartilham nada automaticamente. */
data class SystemEdition(
    val id: String,
    val family: String,
    val name: String,
    val edition: String,
    val category: String,
    val research: ResearchStatus,
    val licenseStatus: LicenseStatus,
    val licenseNote: String
)

enum class Compatibility(val severity: Int) {
    NATIVE(0), ADAPTED(1), UNVERIFIED(2), BLOCKED(3)
}

data class CompatibilityRule(val systemId: String, val settingId: String, val status: Compatibility, val note: String)

val DIMENSIONS = setOf("setting", "structure", "philosophy", "tone", "difficulty", "progression")

class Catalog(
    val entries: List<CatalogEntry>,
    val systems: List<SystemEdition>,
    val rules: List<CompatibilityRule>
) {
    private val entryById = entries.associateBy { it.id }
    private val systemById = systems.associateBy { it.id }

    fun entry(id: String): CatalogEntry? = entryById[id]
    fun system(id: String): SystemEdition? = systemById[id]
    fun editionsOf(family: String): List<SystemEdition> = systems.filter { it.family == family }
    fun families(): Set<String> = systems.map { it.family }.toSet()

    /** Sem regra registrada, a combinação é "não verificada": nunca presumimos compatibilidade. */
    fun compatibility(systemId: String, settingId: String): CompatibilityRule =
        rules.firstOrNull { it.systemId == systemId && it.settingId == settingId }
            ?: CompatibilityRule(systemId, settingId, Compatibility.UNVERIFIED, "sem regra registrada")

    /** Lista de problemas de integridade. Vazia = catálogo coerente. */
    fun validate(): List<String> {
        val p = mutableListOf<String>()
        entries.groupBy { it.id }.filter { it.value.size > 1 }.keys.forEach { p += "id de estilo duplicado: $it" }
        systems.groupBy { it.id }.filter { it.value.size > 1 }.keys.forEach { p += "id de sistema duplicado: $it" }
        entries.filter { it.dimension !in DIMENSIONS }.forEach { p += "dimensão inválida em ${it.id}: ${it.dimension}" }
        entries.filter { !it.id.startsWith(it.dimension + ".") }.forEach { p += "id ${it.id} não começa com a dimensão ${it.dimension}" }
        for (r in rules) {
            if (r.systemId !in systemById) p += "regra de compatibilidade aponta para sistema inexistente: ${r.systemId}"
            val s = entryById[r.settingId]
            if (s == null) p += "regra de compatibilidade aponta para ambientação inexistente: ${r.settingId}"
            else if (s.dimension != "setting") p += "regra de compatibilidade usa ${r.settingId}, que não é ambientação"
        }
        return p
    }
}
