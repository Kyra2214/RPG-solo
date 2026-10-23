package dev.rpgsolo.ui

import android.content.Context
import dev.rpgsolo.rules.Catalog
import dev.rpgsolo.rules.CatalogLoader
import dev.rpgsolo.rules.InstallResult
import dev.rpgsolo.rules.LoadResult
import dev.rpgsolo.rules.PackageRegistry

/** Catálogo e pacotes embutidos, lidos dos recursos do módulo `rules` (funciona no Android). */
class Content(
    val catalog: Catalog?,
    val registry: PackageRegistry,
    /** Problemas ao carregar; mostrados na aba Sistemas em vez de derrubar o app. */
    val errors: List<String>,
)

object BundledContent {
    private val FILES =
        listOf(
            "manifest", "atributos", "recursos", "tracos", "testes", "dificuldades", "condicoes",
            "dano", "itens", "criacao", "progressao", "incerto", "tutorial", "exemplos", "arquetipos", "opcoes-arquetipo", "poderes", "perigos",
        )

    // Único sistema embutido: o sistema próprio. Pacotes de teste ficam só nos testes.
    private val PACKAGES = listOf("sistema-proprio")

    fun empty(): Content = Content(null, PackageRegistry(), emptyList())

    private fun read(path: String): String? =
        BundledContent::class.java.classLoader?.getResourceAsStream(path)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }

    fun load(context: Context? = null): Content {
        val errors = mutableListOf<String>()
        val catalog =
            try {
                val e = read("catalog/estilos.txt")
                val s = read("catalog/sistemas.txt")
                val c = read("catalog/compat.txt")
                if (e == null || s == null || c == null) {
                    errors += "arquivos do catálogo ausentes"
                    null
                } else {
                    when (val r = CatalogLoader.load(e, s, c)) {
                        is LoadResult.Success -> r.catalog
                        is LoadResult.Failure -> {
                            errors += r.errors.map { "catálogo: $it" }
                            null
                        }
                    }
                }
            } catch (ex: Exception) {
                errors += "catálogo: ${ex.message}"
                null
            }

        val registry = PackageRegistry(catalog)
        for (name in PACKAGES) {
            val files = FILES.mapNotNull { f -> read("packages/$name/$f.txt")?.let { "$f.txt" to it } }.toMap()
            if (files.isEmpty()) {
                errors += "pacote $name ausente"
                continue
            }
            when (val r = registry.install(files)) {
                is InstallResult.Success -> {}
                is InstallResult.Failure -> errors += r.errors.map { "pacote $name: $it" }
            }
        }

        // Pacotes importados ficam no armazenamento privado do app e são reinstalados no próximo início.
        val importDir = context?.filesDir?.resolve("packages-import")
        if (importDir != null && importDir.exists()) {
            importDir.listFiles { file -> file.isFile && file.extension.equals("zip", ignoreCase = true) }
                .orEmpty().sortedBy { it.name }.forEach { archive ->
                    when (val installed = runCatching { archive.inputStream().use { stream -> registry.installZip(stream) } }.getOrNull()) {
                        is InstallResult.Success -> Unit
                        is InstallResult.Failure -> errors += installed.errors.map { "pacote importado ${archive.name}: $it" }
                        null -> errors += "pacote importado ilegível: ${archive.name}"
                    }
                }
        }

        return Content(catalog, registry, errors)
    }
}
