package dev.rpgsolo.rules

import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.FortuneMode
import dev.rpgsolo.engine.REAL_DICE

object PackageFiles {
    const val MANIFEST = "manifest.txt"
    const val STATS = "atributos.txt"
    const val RESOURCES = "recursos.txt"
    const val TRAITS = "tracos.txt"
    const val CHECKS = "testes.txt"
    const val DIFFICULTIES = "dificuldades.txt"
    const val CONDITIONS = "condicoes.txt"
    const val DAMAGE = "dano.txt"
    const val ITEMS = "itens.txt"
    const val CREATION = "criacao.txt"
    const val PROGRESSION = "progressao.txt"
    const val UNCERTAIN = "incerto.txt"
    const val TUTORIAL = "tutorial.txt"
    const val EXAMPLES = "exemplos.txt"
    const val ARCHETYPES = "arquetipos.txt"
    const val ARCHETYPE_OPTIONS = "opcoes-arquetipo.txt"
    val ALL = setOf(MANIFEST, STATS, RESOURCES, TRAITS, CHECKS, DIFFICULTIES, CONDITIONS, DAMAGE, ITEMS,
        CREATION, PROGRESSION, UNCERTAIN, TUTORIAL, EXAMPLES, ARCHETYPES, ARCHETYPE_OPTIONS)
}

sealed interface ParseResult {
    data class Ok(val data: PackageData) : ParseResult
    /** Nada foi lido; a mensagem diz arquivo e linha. */
    data class Fail(val errors: List<String>) : ParseResult
}

/** Lê os arquivos de texto de um pacote (campos separados por `|`, linhas com `#` são comentários). */
object PackageParser {
    private val ID = Regex("[a-z0-9][a-z0-9_-]*")
    private val MIN_STAT = Regex("([a-z0-9_-]+)>=(-?\\d+)")
    val TRAIT_TYPES = setOf("vantagem", "desvantagem", "pericia", "poder")

    private fun bad(msg: String): Nothing = throw IllegalArgumentException(msg)
    private fun int(s: String, what: String): Int = s.toIntOrNull() ?: bad("$what inválido: '$s'")
    private fun id(s: String, what: String): String {
        if (!ID.matches(s)) bad("$what inválido: '$s' (use a-z, 0-9, - e _)")
        return s
    }
    private fun text(s: String, what: String): String = if (s.isEmpty()) bad("$what vazio") else s
    private fun blank(s: String) = s.isEmpty() || s == "-"
    private fun optInt(s: String, what: String): Int? = if (blank(s)) null else int(s, what)
    /** "esquiva:+2;sorte:+1" ou "-" / vazio (sem bônus). Escopo = id de teste, de atributo ou tipo de situação. */
    private fun traitBonuses(s: String?): List<TraitBonus> {
        val t = s?.trim().orEmpty()
        if (t.isEmpty() || t == "-") return emptyList()
        return t.split(";").map { part ->
            val kv = part.split(":", limit = 2)
            if (kv.size != 2) bad("bônus inválido: '$part' (use escopo:+N, ex.: esquiva:+2)")
            TraitBonus(id(kv[0].trim(), "escopo do bônus"), int(kv[1].trim(), "valor do bônus"))
        }
    }

    /** "ataque:+1;dano:+1;defesa:+1" ou "-" (sem efeito mecânico). */
    private fun itemEffects(s: String?): List<ItemEffect> {
        val t = s?.trim().orEmpty()
        if (t.isEmpty() || t == "-") return emptyList()
        return t.split(";").map { part ->
            val kv = part.split(":", limit = 2)
            if (kv.size != 2) bad("efeito de item inválido: '$part' (use escopo:+N)")
            val scope = id(kv[0].trim(), "escopo do efeito de item")
            if (scope !in setOf("ataque", "dano", "defesa")) bad("escopo de item não suportado: '$scope' (use ataque, dano ou defesa)")
            ItemEffect(scope, int(kv[1].trim(), "bônus do item"))
        }
    }

    /** "ganho:poder,habilidade", "perda:*" ou "-" (sem efeito). */
    private fun conditionEffect(s: String): ConditionEffect? {
        val t = s.trim()
        if (t.isEmpty() || t == "-") return null
        val parts = t.split(":", limit = 2)
        val dice = when (parts[0].trim()) {
            "ganho" -> 1
            "perda" -> -1
            else -> bad("efeito inválido: '$t' (use ganho:<atributos>, perda:<atributos> ou -)")
        }
        val scope = parts.getOrNull(1)?.trim().orEmpty()
        if (scope.isEmpty()) bad("efeito sem atributos: '$t' (use * para todos)")
        val stats = if (scope == "*") null else scope.split(",").map { id(it.trim(), "atributo do efeito") }.toSet()
        return ConditionEffect(dice, stats)
    }

    private fun optId(s: String, what: String): String? = if (blank(s)) null else id(s, what)

    private fun range(s: String, what: String): IntRange? {
        if (blank(s)) return null
        val p = s.split("..")
        if (p.size != 2) bad("$what inválido: '$s' (use a..b)")
        val a = int(p[0].trim(), what)
        val b = int(p[1].trim(), what)
        if (a > b) bad("$what com início maior que o fim")
        return a..b
    }

    private fun requirements(s: String): List<Requirement> {
        if (blank(s)) return emptyList()
        return s.split(";").map { it.trim() }.map { r ->
            if (r.startsWith("trait:")) Requirement.HasTrait(id(r.removePrefix("trait:"), "requisito"))
            else if (r.startsWith("not:")) Requirement.Excludes(id(r.removePrefix("not:"), "requisito"))
            else MIN_STAT.matchEntire(r)?.let { Requirement.MinStat(it.groupValues[1], it.groupValues[2].toInt()) }
                ?: bad("requisito inválido: '$r' (use atributo>=N, trait:id ou not:id)")
        }
    }

    private fun statMap(s: String): Map<String, Int> {
        if (blank(s)) return emptyMap()
        val m = linkedMapOf<String, Int>()
        for (pair in s.split(",")) {
            val kv = pair.split("=")
            if (kv.size != 2) bad("atributo mal formado: '$pair' (use id=valor)")
            if (m.put(id(kv[0].trim(), "atributo"), int(kv[1].trim(), "valor")) != null) bad("atributo repetido: ${kv[0]}")
        }
        return m
    }

    fun parse(files: Map<String, String>): ParseResult {
        val errors = mutableListOf<String>()
        (files.keys - PackageFiles.ALL).sorted().forEach { errors += "arquivo desconhecido no pacote: $it" }

        fun rows(file: String): List<Pair<Int, List<String>>> =
            files[file]?.lines()?.mapIndexed { i, l -> (i + 1) to l.trim() }
                ?.filter { it.second.isNotEmpty() && !it.second.startsWith("#") }
                ?.map { it.first to it.second.split("|").map(String::trim) }
                ?: emptyList()

        fun <T> table(file: String, size: Int, maxSize: Int = size, build: (List<String>) -> T): List<T> =
            rows(file).mapNotNull { (n, f) ->
                if (f.size !in size..maxSize) {
                    errors += "$file linha $n: esperado ${if (maxSize == size) "$size" else "$size a $maxSize"} campos, veio ${f.size}"
                    return@mapNotNull null
                }
                try {
                    build(f)
                } catch (e: IllegalArgumentException) {
                    errors += "$file linha $n: ${e.message}"
                    null
                }
            }

        fun noDups(file: String, ids: List<String>) {
            ids.groupBy { it }.filter { it.value.size > 1 }.keys.forEach { errors += "$file: id duplicado: $it" }
        }

        // manifest
        val mf = mutableMapOf<String, String>()
        for ((n, f) in rows(PackageFiles.MANIFEST)) {
            if (f.size != 2) errors += "${PackageFiles.MANIFEST} linha $n: esperado 2 campos"
            else if (mf.put(f[0], f[1]) != null) errors += "${PackageFiles.MANIFEST}: chave repetida: ${f[0]}"
        }
        fun req(k: String): String {
            val v = mf[k]
            if (v.isNullOrBlank()) { errors += "${PackageFiles.MANIFEST}: falta '$k'"; return "" }
            return v
        }
        val pid = req("id")
        if (pid.isNotEmpty() && !ID.matches(pid)) errors += "${PackageFiles.MANIFEST}: id inválido: '$pid'"
        val licenseStatus = req("licenca_status").let { s ->
            LicenseStatus.values().firstOrNull { it.name == s }
                ?: run { if (s.isNotEmpty()) errors += "${PackageFiles.MANIFEST}: licenca_status inválido: $s"; LicenseStatus.PENDENTE }
        }
        val kind = req("tipo").let { s ->
            PackageKind.values().firstOrNull { it.name == s }
                ?: run { if (s.isNotEmpty()) errors += "${PackageFiles.MANIFEST}: tipo inválido: $s (TESTE ou REAL)"; PackageKind.TESTE }
        }
        val manifest = Manifest(
            id = pid, system = req("sistema"), edition = req("edicao"), version = req("versao"),
            source = req("origem"), license = req("licenca"), licenseStatus = licenseStatus, kind = kind,
            extends = mf["estende"]?.takeIf { it.isNotBlank() },
            deathResource = mf["morte"]?.takeIf { it.isNotBlank() }
        )

        val stats = table(PackageFiles.STATS, 5) { f ->
            val s = StatDef(id(f[0], "id"), text(f[1], "nome"), int(f[2], "min"), int(f[3], "max"), int(f[4], "padrao"))
            if (s.min > s.max) bad("min maior que max")
            if (s.default !in s.min..s.max) bad("padrao fora de min..max")
            s
        }
        val resources = table(PackageFiles.RESOURCES, 5) { f ->
            ResourceDef(id(f[0], "id"), text(f[1], "nome"), int(f[2], "base"), optId(f[3], "atributo"), int(f[4], "mult"))
        }
        val traits = table(PackageFiles.TRAITS, 6, 7) { f ->
            val type = f[1]
            if (type !in TRAIT_TYPES) bad("tipo inválido: '$type' (${TRAIT_TYPES.joinToString()})")
            TraitDef(id(f[0], "id"), type, text(f[2], "nome"), int(f[3], "custo"), requirements(f[4]), f[5], traitBonuses(f.getOrNull(6)))
        }
        val checks = table(PackageFiles.CHECKS, 10, 12) { f ->
            val cmp = Comparison.values().firstOrNull { it.name == f[6] }
                ?: bad("comparacao inválida: '${f[6]}' (ROLL_OVER, ROLL_UNDER ou POINTS)")
            val count = int(f[2], "dados")
            val sides = int(f[3], "lados")
            if (count !in 1..100) bad("dados fora de 1..100")
            if (sides < 2) bad("lados menor que 2")
            if (cmp == Comparison.POINTS && (count != 1 || sides !in REAL_DICE)) bad("POINTS exige 1 dado real (dados=1, lados ${REAL_DICE.joinToString(", ")})")
            val skill = f.getOrNull(10)?.takeIf { it.isNotBlank() && !blank(it) }?.let { id(it, "perícia") }
            val fortune = f.getOrNull(11)?.takeIf { it.isNotBlank() && !blank(it) }?.let { value ->
                FortuneMode.values().firstOrNull { it.name == value.uppercase() }
                    ?: bad("política de rolagem inválida: '$value' (NORMAL, ADVANTAGE ou DISADVANTAGE)")
            } ?: FortuneMode.NORMAL
            CheckDef(id(f[0], "id"), text(f[1], "nome"), count, sides, optId(f[4], "atributo"), int(f[5], "mult"),
                cmp, optInt(f[7], "parcial"), range(f[8], "crit_sucesso"), range(f[9], "crit_falha"), skill, fortune)
        }
        val difficulties = table(PackageFiles.DIFFICULTIES, 3) { f ->
            DifficultyDef(id(f[0], "id"), text(f[1], "nome"), int(f[2], "valor"))
        }
        val conditions = table(PackageFiles.CONDITIONS, 3, 7) { f ->
            val duration = f.getOrNull(4)?.takeIf { !blank(it) }?.let { int(it, "duração em rodadas") }
            if (duration != null && duration <= 0) bad("duração de condição deve ser positiva")
            val damagePerRound = f.getOrNull(5)?.takeIf { !blank(it) }?.let { int(it, "dano por rodada") } ?: 0
            if (damagePerRound < 0) bad("dano por rodada negativo")
            val resource = f.getOrNull(6)?.takeIf { !blank(it) }?.let { id(it, "recurso de condição") } ?: "hp"
            ConditionDef(
                id(f[0], "id"), text(f[1], "nome"), f[2],
                f.getOrNull(3)?.let { conditionEffect(it) }, duration, damagePerRound, resource
            )
        }
        val damage = table(PackageFiles.DAMAGE, 5) { f ->
            val count = int(f[2], "dados")
            val sides = int(f[3], "lados")
            if (count !in 1..100) bad("dados fora de 1..100")
            if (sides < 2) bad("lados menor que 2")
            DamageDef(id(f[0], "id"), text(f[1], "nome"), count, sides, optId(f[4], "atributo"))
        }
        val items = table(PackageFiles.ITEMS, 5, 6) { f ->
            val kind = f[2]
            if (kind !in setOf("equipamento", "consumivel", "narrativo")) bad("tipo de item inválido: '$kind'")
            val slot = optId(f[3], "slot")
            if (kind == "equipamento" && slot == null) bad("equipamento exige slot")
            if (kind != "equipamento" && slot != null) bad("apenas equipamento pode ocupar slot")
            val effects = itemEffects(f.getOrNull(5))
            if (kind != "equipamento" && effects.isNotEmpty()) bad("apenas equipamento pode ter efeitos automáticos")
            ItemDef(id(f[0], "id"), text(f[1], "nome"), kind, slot, text(f[4], "descricao"), effects)
        }
        val creationRows = table(PackageFiles.CREATION, 2) { f -> text(f[0], "chave") to f[1] }
        noDups(PackageFiles.CREATION, creationRows.map { it.first })
        val progressionRows = table(PackageFiles.PROGRESSION, 2) { f -> int(f[0], "nivel") to int(f[1], "xp") }
        var progression: List<Int>? = null
        if (files.containsKey(PackageFiles.PROGRESSION)) {
            val sorted = progressionRows.sortedBy { it.first }
            sorted.forEachIndexed { i, (lvl, _) ->
                if (lvl != i + 2) errors += "${PackageFiles.PROGRESSION}: níveis devem ser 2, 3, 4... sem buracos (achei $lvl na posição ${i + 1})"
            }
            val xps = sorted.map { it.second }
            if (xps.any { it <= 0 } || xps.zipWithNext().any { (a, b) -> a >= b })
                errors += "${PackageFiles.PROGRESSION}: XP deve ser positivo e estritamente crescente"
            progression = xps
        }
        val uncertain = table(PackageFiles.UNCERTAIN, 2) { f -> UncertainRule(id(f[0], "id"), text(f[1], "nota")) }
        val tutorial = table(PackageFiles.TUTORIAL, 3) { f -> TutorialStep(int(f[0], "ordem"), text(f[1], "titulo"), text(f[2], "texto")) }
        val examples = table(PackageFiles.EXAMPLES, 4) { f ->
            val traitIds = if (blank(f[3])) emptyList() else f[3].split(",").map { id(it.trim(), "traço") }
            ExampleCharacter(id(f[0], "id"), text(f[1], "titulo"), statMap(f[2]), traitIds)
        }

        val archetypes = table(PackageFiles.ARCHETYPES, 5) { f ->
            val traitIds = if (blank(f[4])) emptyList() else f[4].split(",").map { id(it.trim(), "traço") }
            Archetype(id(f[0], "id"), text(f[1], "nome"), text(f[2], "descricao"), statMap(f[3]), traitIds)
        }

        fun idList(s: String, what: String): List<String> =
            if (blank(s)) emptyList() else s.split(",").map { id(it.trim(), what) }
        val archetypeOptions = table(PackageFiles.ARCHETYPE_OPTIONS, 3) { f ->
            ArchetypeOptions(id(f[0], "arquétipo"), idList(f[1], "vantagem"), idList(f[2], "desvantagem"))
        }

        noDups(PackageFiles.STATS, stats.map { it.id })
        noDups(PackageFiles.RESOURCES, resources.map { it.id })
        noDups(PackageFiles.TRAITS, traits.map { it.id })
        noDups(PackageFiles.CHECKS, checks.map { it.id })
        noDups(PackageFiles.DIFFICULTIES, difficulties.map { it.id })
        noDups(PackageFiles.CONDITIONS, conditions.map { it.id })
        noDups(PackageFiles.DAMAGE, damage.map { it.id })
        noDups(PackageFiles.ITEMS, items.map { it.id })
        noDups(PackageFiles.UNCERTAIN, uncertain.map { it.id })
        noDups(PackageFiles.TUTORIAL, tutorial.map { it.order.toString() })
        noDups(PackageFiles.EXAMPLES, examples.map { it.id })
        noDups(PackageFiles.ARCHETYPES, archetypes.map { it.id })
        noDups(PackageFiles.ARCHETYPE_OPTIONS, archetypeOptions.map { it.archetype })

        if (errors.isNotEmpty()) return ParseResult.Fail(errors)
        return ParseResult.Ok(PackageData(manifest, stats, resources, traits, checks, difficulties, conditions, damage, items,
            creationRows.toMap(), progression, uncertain, tutorial, examples, archetypes, archetypeOptions))
    }
}
