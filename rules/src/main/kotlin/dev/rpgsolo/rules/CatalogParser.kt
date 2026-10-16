package dev.rpgsolo.rules

sealed interface LoadResult {
    data class Success(val catalog: Catalog) : LoadResult
    /** Nada foi carregado; o catálogo anterior continua valendo. */
    data class Failure(val errors: List<String>) : LoadResult
}

/** Lê os arquivos de texto do catálogo (campos separados por `|`, linhas com `#` são comentários). */
object CatalogLoader {
    private fun rows(text: String): List<Pair<Int, List<String>>> =
        text.lines().mapIndexed { i, l -> (i + 1) to l.trim() }
            .filter { it.second.isNotEmpty() && !it.second.startsWith("#") }
            .map { it.first to it.second.split("|").map(String::trim) }

    fun load(estilos: String, sistemas: String, compat: String): LoadResult {
        val errors = mutableListOf<String>()
        val entries = rows(estilos).mapNotNull { (n, f) ->
            if (f.size != 4 || f.any { it.isEmpty() }) { errors += "estilos linha $n: esperado 4 campos"; null }
            else CatalogEntry(f[0], f[1], f[2], f[3])
        }
        val systems = rows(sistemas).mapNotNull { (n, f) ->
            if (f.size != 8) { errors += "sistemas linha $n: esperado 8 campos"; return@mapNotNull null }
            val research = ResearchStatus.values().firstOrNull { it.name == f[5] }
            val lic = LicenseStatus.values().firstOrNull { it.name == f[6] }
            if (research == null || lic == null) { errors += "sistemas linha $n: status inválido"; null }
            else SystemEdition(f[0], f[1], f[2], f[3], f[4], research, lic, f[7])
        }
        val rules = rows(compat).mapNotNull { (n, f) ->
            val st = Compatibility.values().firstOrNull { it.name == f.getOrNull(2) }
            if (f.size != 4 || st == null) { errors += "compat linha $n: formato inválido"; null }
            else CompatibilityRule(f[0], f[1], st, f[3])
        }
        if (errors.isNotEmpty()) return LoadResult.Failure(errors)
        val catalog = Catalog(entries, systems, rules)
        val problems = catalog.validate()
        return if (problems.isEmpty()) LoadResult.Success(catalog) else LoadResult.Failure(problems)
    }
}

/** Guarda o catálogo atual. Uma carga com erro NÃO substitui o que já estava carregado. */
class CatalogHolder(initial: Catalog? = null) {
    var current: Catalog? = initial
        private set

    fun reload(estilos: String, sistemas: String, compat: String): LoadResult {
        val r = CatalogLoader.load(estilos, sistemas, compat)
        if (r is LoadResult.Success) current = r.catalog
        return r
    }
}
