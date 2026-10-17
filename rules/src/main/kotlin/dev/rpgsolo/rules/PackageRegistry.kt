package dev.rpgsolo.rules

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipInputStream

sealed interface InstallResult {
    data class Success(val pkg: RulePackage) : InstallResult
    /** Nada foi instalado nem alterado. */
    data class Failure(val errors: List<String>) : InstallResult
}

/**
 * Pacotes instalados. Instalar é atômico: se o novo pacote (ou qualquer pacote que dependa dele) ficar
 * inválido, o conjunto instalado não muda. Cada pacote é independente: regras nunca se misturam.
 */
class PackageRegistry(private val catalog: Catalog? = null) {
    private val raw = linkedMapOf<String, PackageData>()
    private val built = linkedMapOf<String, RulePackage>()

    fun get(id: String): RulePackage? = built[id]
    fun installed(): List<Manifest> = built.values.map { it.manifest }

    fun install(files: Map<String, String>, replace: Boolean = false): InstallResult {
        val data = when (val parsed = PackageParser.parse(files)) {
            is ParseResult.Fail -> return InstallResult.Failure(parsed.errors)
            is ParseResult.Ok -> parsed.data
        }
        val id = data.manifest.id
        if (raw.containsKey(id) && !replace) return InstallResult.Failure(listOf("pacote já instalado: $id"))

        val tentative = LinkedHashMap(raw)
        tentative[id] = data
        val errors = mutableListOf<String>()
        val newBuilt = linkedMapOf<String, RulePackage>()
        for (pid in tentative.keys) {
            val resolved = try {
                resolve(pid, tentative, emptySet())
            } catch (e: IllegalArgumentException) {
                errors += "$pid: ${e.message}"
                continue
            }
            val problems = PackageValidator.validate(resolved) + catalogProblems(resolved.manifest)
            if (problems.isEmpty()) newBuilt[pid] = RulePackage(resolved) else errors += problems.map { "$pid: $it" }
        }
        if (errors.isNotEmpty()) return InstallResult.Failure(errors)
        raw.clear(); raw.putAll(tentative)
        built.clear(); built.putAll(newBuilt)
        return InstallResult.Success(newBuilt.getValue(id))
    }

    /** Importa um pacote de um .zip (4.2.2). Só lê arquivos .txt, ignora pastas e limita o tamanho. */
    fun installZip(input: InputStream, replace: Boolean = false): InstallResult {
        val files = linkedMapOf<String, String>()
        var total = 0L
        try {
            ZipInputStream(input).use { zis ->
                while (true) {
                    val e = zis.nextEntry ?: break
                    if (e.isDirectory) continue
                    val name = e.name.substringAfterLast('/').substringAfterLast('\\')
                    if (!name.endsWith(".txt")) continue
                    if (files.size >= MAX_FILES) return InstallResult.Failure(listOf("zip com arquivos demais"))
                    val buf = ByteArrayOutputStream()
                    val tmp = ByteArray(8192)
                    while (true) {
                        val n = zis.read(tmp)
                        if (n < 0) break
                        total += n
                        if (total > MAX_BYTES) return InstallResult.Failure(listOf("pacote grande demais"))
                        buf.write(tmp, 0, n)
                    }
                    if (files.put(name, buf.toString("UTF-8")) != null)
                        return InstallResult.Failure(listOf("arquivo repetido no zip: $name"))
                }
            }
        } catch (e: IOException) {
            return InstallResult.Failure(listOf("zip ilegível: ${e.message}"))
        }
        return install(files, replace)
    }

    private fun catalogProblems(m: Manifest): List<String> {
        val c = catalog ?: return emptyList()
        if (m.kind == PackageKind.TESTE) return emptyList()
        val p = mutableListOf<String>()
        val ed = c.system(m.edition)
        if (ed == null) p += "edição ${m.edition} não existe no catálogo"
        else if (ed.family != m.system) p += "edição ${m.edition} é da família ${ed.family}, não ${m.system}"
        // Licença é só informação (licenca_status): não bloqueia. O app usa PDFs como guia e o usuário traz os dele.
        return p
    }

    private fun resolve(id: String, all: Map<String, PackageData>, seen: Set<String>): PackageData {
        val d = all.getValue(id)
        val parentId = d.manifest.extends ?: return d
        require(parentId != id && parentId !in seen) { "herança circular em $id" }
        if (!all.containsKey(parentId)) throw IllegalArgumentException("pacote-base não instalado: $parentId")
        val base = resolve(parentId, all, seen + id)
        require(base.manifest.system == d.manifest.system) {
            "herança só vale dentro da mesma família (${base.manifest.system} != ${d.manifest.system})"
        }
        return merge(base, d)
    }

    private fun <T> byId(base: List<T>, child: List<T>, key: (T) -> String): List<T> {
        val m = LinkedHashMap<String, T>()
        base.forEach { m[key(it)] = it }
        child.forEach { m[key(it)] = it }
        return m.values.toList()
    }

    /** Filho sobrescreve o pai por id. Remover entradas do pai não é suportado. */
    private fun merge(b: PackageData, c: PackageData) = PackageData(
        manifest = c.manifest.copy(deathResource = c.manifest.deathResource ?: b.manifest.deathResource),
        stats = byId(b.stats, c.stats) { it.id },
        resources = byId(b.resources, c.resources) { it.id },
        traits = byId(b.traits, c.traits) { it.id },
        checks = byId(b.checks, c.checks) { it.id },
        difficulties = byId(b.difficulties, c.difficulties) { it.id },
        conditions = byId(b.conditions, c.conditions) { it.id },
        damage = byId(b.damage, c.damage) { it.id },
        creation = b.creation + c.creation,
        progression = c.progression ?: b.progression,
        uncertain = byId(b.uncertain, c.uncertain) { it.id },
        tutorial = byId(b.tutorial, c.tutorial) { it.order.toString() }.sortedBy { it.order },
        examples = byId(b.examples, c.examples) { it.id },
        archetypes = byId(b.archetypes, c.archetypes) { it.id },
        archetypeOptions = byId(b.archetypeOptions, c.archetypeOptions) { it.archetype }
    )

    companion object {
        const val MAX_FILES = 50
        const val MAX_BYTES = 2L * 1024 * 1024
    }
}
