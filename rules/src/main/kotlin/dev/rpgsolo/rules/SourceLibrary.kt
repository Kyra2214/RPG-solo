package dev.rpgsolo.rules

/** Tipo de material linkado: regras, ficha, iniciante, conversão, outro. */
enum class SourceKind { REGRAS, FICHA, INICIANTE, CONVERSAO, OUTRO }

/**
 * Link para onde o material oficial está (o app não copia o PDF: abre o link e o usuário baixa/importa).
 * `verified` = o link foi aberto e conferido; `false` = confira antes de confiar.
 */
data class SourceLink(
    val id: String,
    val systemId: String?,
    val title: String,
    val kind: SourceKind,
    val url: String,
    val language: String,
    val verified: Boolean
)

sealed interface SourcesResult {
    data class Ok(val links: List<SourceLink>) : SourcesResult
    data class Fail(val errors: List<String>) : SourcesResult
}

/** Lê `fontes.txt`: `id|sistema|titulo|tipo|url|idioma|verificado(SIM/NAO)`. Sistema `-` = geral. */
object SourceLibraryParser {
    private val ID = Regex("[a-z0-9][a-z0-9_-]*")

    fun parse(text: String, catalog: Catalog? = null): SourcesResult {
        val errors = mutableListOf<String>()
        val out = mutableListOf<SourceLink>()
        text.lines().forEachIndexed { i, raw ->
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) return@forEachIndexed
            val n = i + 1
            val f = line.split("|").map { it.trim() }
            if (f.size != 7) { errors += "fontes.txt linha $n: esperado 7 campos, veio ${f.size}"; return@forEachIndexed }
            val kind = SourceKind.values().firstOrNull { it.name == f[3] }
            when {
                !ID.matches(f[0]) -> errors += "fontes.txt linha $n: id inválido '${f[0]}'"
                f[2].isEmpty() -> errors += "fontes.txt linha $n: título vazio"
                kind == null -> errors += "fontes.txt linha $n: tipo inválido '${f[3]}'"
                !f[4].startsWith("https://") -> errors += "fontes.txt linha $n: url precisa começar com https://"
                f[6] != "SIM" && f[6] != "NAO" -> errors += "fontes.txt linha $n: verificado deve ser SIM ou NAO"
                f[1] != "-" && catalog != null && catalog.system(f[1]) == null ->
                    errors += "fontes.txt linha $n: sistema inexistente no catálogo '${f[1]}'"
                else -> out += SourceLink(f[0], f[1].takeIf { it != "-" }, f[2], kind, f[4], f[5], f[6] == "SIM")
            }
        }
        out.groupBy { it.id }.filter { it.value.size > 1 }.keys.forEach { errors += "fontes.txt: id duplicado: $it" }
        return if (errors.isEmpty()) SourcesResult.Ok(out) else SourcesResult.Fail(errors)
    }
}

class SourceLibrary(val links: List<SourceLink>) {
    fun forSystem(systemId: String): List<SourceLink> = links.filter { it.systemId == systemId }
    fun general(): List<SourceLink> = links.filter { it.systemId == null }
}
