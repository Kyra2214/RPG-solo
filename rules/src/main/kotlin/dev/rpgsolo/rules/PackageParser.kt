package dev.rpgsolo.rules

import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.DeathMode
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
    const val POWERS = "poderes.txt"
    const val HAZARDS = "perigos.txt"
    const val ABILITIES = "habilidades.txt"
    const val EXCEPTIONS = "excecoes.txt"
    const val RECIPES = "receitas.txt"
    const val COMPANIONS = "companheiros.txt"
    const val CREATION = "criacao.txt"
    const val PROGRESSION = "progressao.txt"
    const val UNCERTAIN = "incerto.txt"
    const val TUTORIAL = "tutorial.txt"
    const val EXAMPLES = "exemplos.txt"
    const val ARCHETYPES = "arquetipos.txt"
    const val ARCHETYPE_OPTIONS = "opcoes-arquetipo.txt"
    val ALL =
        setOf(
            MANIFEST, STATS, RESOURCES, TRAITS, CHECKS, DIFFICULTIES, CONDITIONS, DAMAGE, ITEMS,
            CREATION, PROGRESSION, UNCERTAIN, TUTORIAL, EXAMPLES, ARCHETYPES, ARCHETYPE_OPTIONS, POWERS, HAZARDS, ABILITIES, EXCEPTIONS, RECIPES, COMPANIONS,
        )
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

    private fun int(
        s: String,
        what: String,
    ): Int = s.toIntOrNull() ?: bad("$what inválido: '$s'")

    private fun id(
        s: String,
        what: String,
    ): String {
        if (!ID.matches(s)) bad("$what inválido: '$s' (use a-z, 0-9, - e _)")
        return s
    }

    private fun text(
        s: String,
        what: String,
    ): String = if (s.isEmpty()) bad("$what vazio") else s

    private fun blank(s: String) = s.isEmpty() || s == "-"

    private fun optInt(
        s: String,
        what: String,
    ): Int? = if (blank(s)) null else int(s, what)

    /** "esquiva:+2;sorte:+1" ou "-" / vazio (sem bônus). Escopo = id de teste, de atributo ou tipo de situação. */
    private fun traitUses(s: String?): Int {
        val t = s?.trim().orEmpty()
        if (t.isEmpty() || t == "-") return 0
        val n = int(t, "usos")
        if (n !in 1..99) bad("usos deve ficar entre 1 e 99: '$t'")
        return n
    }

    private fun traitBonuses(s: String?): List<TraitBonus> {
        val t = s?.trim().orEmpty()
        if (t.isEmpty() || t == "-") return emptyList()
        return t.split(";").map { part ->
            val kv = part.split(":", limit = 2)
            if (kv.size != 2) bad("bônus inválido: '$part' (use escopo:+N, ex.: esquiva:+2)")
            TraitBonus(id(kv[0].trim(), "escopo do bônus"), int(kv[1].trim(), "valor do bônus"))
        }
    }

    /** MU-06.1: "escopo:+N@item;..." ou "-" (sem bônus condicional). */
    private fun traitConditionals(s: String?): List<ConditionalBonus> {
        val t = s?.trim().orEmpty()
        if (t.isEmpty() || t == "-") return emptyList()
        return t.split(";").map { part ->
            val at = part.split("@", limit = 2)
            if (at.size != 2) bad("bônus condicional inválido: '$part' (use escopo:+N@item)")
            val kv = at[0].split(":", limit = 2)
            if (kv.size != 2) bad("bônus condicional inválido: '$part' (use escopo:+N@item)")
            val value = int(kv[1].trim(), "valor do bônus condicional")
            if (value == 0) bad("bônus condicional não pode ser 0: '$part'")
            ConditionalBonus(id(kv[0].trim(), "escopo do bônus condicional"), value, id(at[1].trim(), "item do bônus condicional"))
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
            ItemEffect(scope, int(kv[1].trim(), "bônus do item"))
        }
    }

    /** "ganho:poder,habilidade", "perda:*" ou "-" (sem efeito). */
    private fun conditionEffect(s: String): ConditionEffect? {
        val t = s.trim()
        if (t.isEmpty() || t == "-") return null
        val parts = t.split(":", limit = 2)
        val dice =
            when (parts[0].trim()) {
                "ganho" -> 1
                "perda" -> -1
                else -> bad("efeito inválido: '$t' (use ganho:<atributos>, perda:<atributos> ou -)")
            }
        val scope = parts.getOrNull(1)?.trim().orEmpty()
        if (scope.isEmpty()) bad("efeito sem atributos: '$t' (use * para todos)")
        val stats = if (scope == "*") null else scope.split(",").map { id(it.trim(), "atributo do efeito") }.toSet()
        return ConditionEffect(dice, stats)
    }

    private fun optId(
        s: String,
        what: String,
    ): String? = if (blank(s)) null else id(s, what)

    private fun range(
        s: String,
        what: String,
    ): IntRange? {
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
            if (r.startsWith("trait:")) {
                Requirement.HasTrait(id(r.removePrefix("trait:"), "requisito"))
            } else if (r.startsWith("not:")) {
                Requirement.Excludes(id(r.removePrefix("not:"), "requisito"))
            } else {
                MIN_STAT.matchEntire(r)?.let { Requirement.MinStat(it.groupValues[1], it.groupValues[2].toInt()) }
                    ?: bad("requisito inválido: '$r' (use atributo>=N, trait:id ou not:id)")
            }
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

        fun <T> table(
            file: String,
            size: Int,
            maxSize: Int = size,
            build: (List<String>) -> T,
        ): List<T> =
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

        fun noDups(
            file: String,
            ids: List<String>,
        ) {
            ids.groupBy { it }.filter { it.value.size > 1 }.keys.forEach { errors += "$file: id duplicado: $it" }
        }

        // manifest
        val mf = mutableMapOf<String, String>()
        for ((n, f) in rows(PackageFiles.MANIFEST)) {
            if (f.size != 2) {
                errors += "${PackageFiles.MANIFEST} linha $n: esperado 2 campos"
            } else if (mf.put(f[0], f[1]) != null) {
                errors += "${PackageFiles.MANIFEST}: chave repetida: ${f[0]}"
            }
        }

        fun req(k: String): String {
            val v = mf[k]
            if (v.isNullOrBlank()) {
                errors += "${PackageFiles.MANIFEST}: falta '$k'"
                return ""
            }
            return v
        }
        val pid = req("id")
        if (pid.isNotEmpty() && !ID.matches(pid)) errors += "${PackageFiles.MANIFEST}: id inválido: '$pid'"
        val licenseStatus =
            req("licenca_status").let { s ->
                LicenseStatus.values().firstOrNull { it.name == s }
                    ?: run {
                        if (s.isNotEmpty()) errors += "${PackageFiles.MANIFEST}: licenca_status inválido: $s"
                        LicenseStatus.PENDENTE
                    }
            }
        val kind =
            req("tipo").let { s ->
                PackageKind.values().firstOrNull { it.name == s }
                    ?: run {
                        if (s.isNotEmpty()) errors += "${PackageFiles.MANIFEST}: tipo inválido: $s (TESTE ou REAL)"
                        PackageKind.TESTE
                    }
            }
        val manifest =
            Manifest(
                id = pid, system = req("sistema"), edition = req("edicao"), version = req("versao"),
                source = req("origem"), license = req("licenca"), licenseStatus = licenseStatus, kind = kind,
                extends = mf["estende"]?.takeIf { it.isNotBlank() },
                deathResource = mf["morte"]?.takeIf { it.isNotBlank() },
                deathMode =
                    mf["modo_morte"]?.trim()?.takeIf { it.isNotEmpty() }?.let { s ->
                        DeathMode.values().firstOrNull { it.name == s }
                            ?: run {
                                errors += "${PackageFiles.MANIFEST}: modo_morte inválido: $s (PERMADEATH ou DOWNED)"
                                null
                            }
                    },
                survivalCheck = mf["teste_sobrevivencia"]?.trim()?.takeIf { it.isNotEmpty() },
                survivalDifficulty = mf["dificuldade_sobrevivencia"]?.trim()?.takeIf { it.isNotEmpty() },
                survivalFailures =
                    mf["falhas_morte"]?.trim()?.takeIf { it.isNotEmpty() }?.let { s ->
                        s.toIntOrNull()?.takeIf { it in 1..10 }
                            ?: run {
                                errors += "${PackageFiles.MANIFEST}: falhas_morte inválido: $s (1 a 10)"
                                null
                            }
                    },
            )

        val stats =
            table(PackageFiles.STATS, 5) { f ->
                val max = if (f[3].trim() == "-") StatDef.NO_CAP else int(f[3], "max")
                val s = StatDef(id(f[0], "id"), text(f[1], "nome"), int(f[2], "min"), max, int(f[4], "padrao"))
                if (s.min > s.max) bad("min maior que max")
                if (s.default !in s.min..s.max) bad("padrao fora de min..max")
                s
            }
        val resources =
            table(PackageFiles.RESOURCES, 5, 6) { f ->
                val minimum = f.getOrNull(5)?.takeIf { !blank(it) }?.let { int(it, "mínimo") } ?: 0
                if (minimum < 0) bad("mínimo do recurso negativo")
                ResourceDef(id(f[0], "id"), text(f[1], "nome"), int(f[2], "base"), optId(f[3], "atributo"), int(f[4], "mult"), minimum)
            }
        val traits =
            table(PackageFiles.TRAITS, 6, 10) { f ->
                val type = f[1]
                if (type !in TRAIT_TYPES) bad("tipo inválido: '$type' (${TRAIT_TYPES.joinToString()})")
                TraitDef(
                    id(
                        f[0],
                        "id",
                    ),
                    type,
                    text(
                        f[2],
                        "nome",
                    ),
                    int(
                        f[3],
                        "custo",
                    ),
                    requirements(
                        f[4],
                    ),
                    f[5],
                    traitBonuses(
                        f.getOrNull(6),
                    ),
                    traitUses(f.getOrNull(7)), traitConditionals(f.getOrNull(8)), traitBonuses(f.getOrNull(9)),
                )
            }
        val checks =
            table(PackageFiles.CHECKS, 10, 19) { f ->
                val cmp =
                    Comparison.values().firstOrNull { it.name == f[6] }
                        ?: bad("comparacao inválida: '${f[6]}' (ROLL_OVER, ROLL_UNDER ou POINTS)")
                val count = int(f[2], "dados")
                val sides = int(f[3], "lados")
                if (count !in 1..100) bad("dados fora de 1..100")
                if (sides < 2) bad("lados menor que 2")
                if (cmp == Comparison.POINTS && (count != 1 || sides !in REAL_DICE)) {
                    bad(
                        "POINTS exige 1 dado real (dados=1, lados ${REAL_DICE.joinToString(", ")})",
                    )
                }
                val skill = f.getOrNull(10)?.takeIf { it.isNotBlank() && !blank(it) }?.let { id(it, "perícia") }
                val fortune =
                    f.getOrNull(11)?.takeIf { it.isNotBlank() && !blank(it) }?.let { value ->
                        FortuneMode.values().firstOrNull { it.name == value.uppercase() }
                            ?: bad("política de rolagem inválida: '$value' (NORMAL, ADVANTAGE ou DISADVANTAGE)")
                    } ?: FortuneMode.NORMAL
                val explode =
                    f.getOrNull(12)?.takeIf { it.isNotBlank() && !blank(it) }?.let { value ->
                        when (value.lowercase()) {
                            "true", "sim", "1" -> true
                            "false", "nao", "não", "0" -> false
                            else -> bad("explosão inválida: '$value' (true/false, sim/não ou 1/0)")
                        }
                    } ?: false
                val fortuneResource = f.getOrNull(13)?.takeIf { it.isNotBlank() && !blank(it) }?.let { id(it, "recurso de fortuna") }
                val fortuneCost = f.getOrNull(14)?.takeIf { it.isNotBlank() && !blank(it) }?.let { int(it, "custo de fortuna") } ?: 0
                if ((fortuneResource == null) != (fortuneCost == 0)) {
                    bad("recurso e custo de fortuna devem ser declarados juntos")
                }
                if (fortuneCost < 0) bad("custo de fortuna negativo")
                if (fortuneResource != null && fortuneCost <= 0) bad("custo de fortuna deve ser positivo")
                val fortuneSpendMode =
                    f.getOrNull(15)?.takeIf { it.isNotBlank() && !blank(it) }?.let { value ->
                        FortuneMode.values().firstOrNull { it.name == value.uppercase() && it != FortuneMode.NORMAL }
                            ?: bad("política paga inválida: '$value' (ADVANTAGE ou DISADVANTAGE)")
                    }
                if (fortuneResource != null && fortuneSpendMode == null) {
                    bad("recurso e custo de fortuna exigem modo_fortuna_pago (ADVANTAGE ou DISADVANTAGE)")
                }
                if (fortuneResource == null && fortuneSpendMode != null) {
                    bad("modo_fortuna_pago exige recurso_fortuna e custo_fortuna")
                }
                if (fortuneResource != null && fortune != FortuneMode.NORMAL) {
                    bad("um teste com fortuna paga não pode aplicar outra política de fortuna automaticamente")
                }
                val luckResource = f.getOrNull(16)?.takeIf { it.isNotBlank() && !blank(it) }?.let { id(it, "recurso de sorte") }
                val luckCost = f.getOrNull(17)?.takeIf { it.isNotBlank() && !blank(it) }?.let { int(it, "custo de sorte") } ?: 0
                val luckKinds =
                    f.getOrNull(18)?.takeIf { it.isNotBlank() && !blank(it) }?.split('+')?.map { value ->
                        dev.rpgsolo.engine.LuckKind.values().firstOrNull { it.name == value.trim().uppercase() }
                            ?: bad("forma de sorte inválida: '${value.trim()}' (REROLL, ADD_FACE ou REROLL+ADD_FACE)")
                    }?.toSet().orEmpty()
                if (luckResource != null && luckCost <= 0) bad("custo de sorte deve ser positivo")
                if (luckResource == null && luckCost != 0) bad("recurso_sorte, custo_sorte e formas_sorte devem ser declarados juntos")
                if ((luckResource == null) != luckKinds.isEmpty()) {
                    bad(
                        "recurso_sorte, custo_sorte e formas_sorte devem ser declarados juntos",
                    )
                }
                CheckDef(
                    id(f[0], "id"), text(f[1], "nome"), count, sides, optId(f[4], "atributo"), int(f[5], "mult"),
                    cmp, optInt(f[7], "parcial"), range(f[8], "crit_sucesso"), range(f[9], "crit_falha"),
                    skill, fortune, explode, fortuneResource, fortuneCost, fortuneSpendMode,
                    luckResource, luckCost, luckKinds,
                )
            }
        val difficulties =
            table(PackageFiles.DIFFICULTIES, 3) { f ->
                DifficultyDef(id(f[0], "id"), text(f[1], "nome"), int(f[2], "valor"))
            }
        val conditions =
            table(PackageFiles.CONDITIONS, 3, 8) { f ->
                val duration = f.getOrNull(4)?.takeIf { !blank(it) }?.let { int(it, "duração em rodadas") }
                if (duration != null && duration <= 0) bad("duração de condição deve ser positiva")
                val damageField = f.getOrNull(5)?.takeIf { !blank(it) }
                val damagePercent =
                    if (damageField?.endsWith("%") == true) int(damageField.dropLast(1), "dano por rodada em %") else 0
                if (damagePercent !in 0..100) bad("dano por rodada em % deve ficar entre 1 e 100")
                val damagePerRound =
                    if (damagePercent > 0) 0 else damageField?.let { int(it, "dano por rodada") } ?: 0
                if (damagePerRound < 0) bad("dano por rodada negativo")
                val resource =
                    f.getOrNull(6)?.takeIf {
                        !blank(it)
                    }?.let { id(it, "recurso de condição") } ?: (mf["morte"]?.takeIf { it.isNotBlank() } ?: "hp")
                val removal = f.getOrNull(7)?.takeIf { !blank(it) }?.split(":", limit = 2)
                ConditionDef(
                    id(f[0], "id"), text(f[1], "nome"), f[2],
                    f.getOrNull(3)?.let { conditionEffect(it) }, duration, damagePerRound, resource,
                    removalCheck = removal?.get(0)?.let { id(it, "teste de remoção") },
                    removalDifficulty = removal?.getOrNull(1)?.takeIf { it.isNotEmpty() }?.let { id(it, "dificuldade de remoção") },
                    damagePercent = damagePercent,
                )
            }
        val damage =
            table(PackageFiles.DAMAGE, 5) { f ->
                val count = int(f[2], "dados")
                val sides = int(f[3], "lados")
                if (count !in 1..100) bad("dados fora de 1..100")
                if (sides < 2) bad("lados menor que 2")
                DamageDef(id(f[0], "id"), text(f[1], "nome"), count, sides, optId(f[4], "atributo"))
            }
        val hazards =
            table(PackageFiles.HAZARDS, 4) { f ->
                val percent = int(f[3], "percentual")
                if (percent !in 1..100) bad("percentual de perigo deve ficar entre 1 e 100: '${f[3]}'")
                HazardDef(id(f[0], "id"), text(f[1], "nome"), f[2], percent)
            }
        val powers =
            table(PackageFiles.POWERS, 9, 10) { f ->
                val cost = int(f[5], "custo")
                if (cost < 0) bad("custo de poder negativo")
                val target =
                    when (f[6]) {
                        "self" -> PowerTarget.SELF
                        "inimigo" -> PowerTarget.ENEMY
                        else -> bad("alvo de poder inválido: '${f[6]}' (use self ou inimigo)")
                    }
                val effect =
                    when (f[7]) {
                        "cura" -> PowerEffectKind.HEAL
                        "dano" -> PowerEffectKind.DAMAGE
                        "condicao" -> PowerEffectKind.APPLY_CONDITION
                        "remover-condicao" -> PowerEffectKind.REMOVE_CONDITION
                        "escudo" -> PowerEffectKind.SHIELD
                        else -> bad("efeito de poder inválido: '${f[7]}' (use cura, dano, condicao, remover-condicao ou escudo)")
                    }
                val param =
                    if (effect == PowerEffectKind.HEAL) {
                        if (int(f[8], "quantidade de cura") <= 0) bad("quantidade de cura deve ser positiva")
                        f[8]
                    } else if (effect == PowerEffectKind.SHIELD) {
                        if (int(f[8], "quantidade de escudo") <= 0) bad("quantidade de escudo deve ser positiva")
                        f[8]
                    } else {
                        id(f[8], "parâmetro do efeito")
                    }
                if (effect == PowerEffectKind.HEAL && target != PowerTarget.SELF) bad("cura só pode ter alvo self")
                if (effect == PowerEffectKind.DAMAGE && target != PowerTarget.ENEMY) bad("dano só pode ter alvo inimigo")
                if (effect == PowerEffectKind.APPLY_CONDITION && target != PowerTarget.ENEMY) bad("condicao só pode ter alvo inimigo")
                if (effect == PowerEffectKind.SHIELD && target != PowerTarget.SELF) bad("escudo só pode ter alvo self")
                if (effect == PowerEffectKind.REMOVE_CONDITION && target != PowerTarget.SELF) bad("remover-condicao só pode ter alvo self")
                PowerDef(
                    id(f[0], "id"), text(f[1], "nome"), f[2], id(f[3], "traço"), id(f[4], "recurso"), cost,
                    target, effect, param, f.getOrNull(9)?.takeIf { !blank(it) }?.let { id(it, "teste") },
                )
            }
        val abilities =
            table(PackageFiles.ABILITIES, 6, 7) { f ->
                val cost = int(f[2], "custo")
                if (cost < 0) bad("custo de habilidade negativo")
                val trigger =
                    when (f[3]) {
                        "manual" -> dev.rpgsolo.engine.AbilityTrigger.MANUAL
                        "ao-atacar" -> dev.rpgsolo.engine.AbilityTrigger.ON_ATTACK
                        "ao-defender" -> dev.rpgsolo.engine.AbilityTrigger.ON_DEFEND
                        "ao-critar" -> dev.rpgsolo.engine.AbilityTrigger.ON_CRIT
                        "inicio-combate" -> dev.rpgsolo.engine.AbilityTrigger.ON_COMBAT_START
                        else ->
                            bad(
                                "gatilho de habilidade inválido: '${f[3]}' (use manual, ao-atacar, ao-defender, ao-critar ou inicio-combate)",
                            )
                    }

                fun effects(
                    raw: String,
                    positive: Boolean,
                    what: String,
                ): List<dev.rpgsolo.engine.AbilityBonus> =
                    raw.split(";").map { part ->
                        val pieces = part.trim().split(":", limit = 2)
                        if (pieces.size != 2) bad("$what inválido: '$part' (use alvo:+N)")
                        val value = int(pieces[1].trim().removePrefix("+"), "valor de $what")
                        if (positive && value <= 0) bad("$what deve ser positivo: '$part'")
                        if (!positive && value >= 0) bad("$what deve ser negativo: '$part'")
                        dev.rpgsolo.engine.AbilityBonus(id(pieces[0].trim(), "alvo de $what"), value)
                    }
                val bonuses = if (blank(f[4])) bad("habilidade exige ao menos um bônus") else effects(f[4], true, "bônus")
                val duration = f[5]
                val rounds = duration.toIntOrNull()
                if (duration !in setOf("sorteio", "combate", "cena") && (rounds == null || rounds <= 0)) {
                    bad("duração de habilidade inválida: '$duration' (use N > 0, sorteio, combate ou cena)")
                }
                if (trigger == dev.rpgsolo.engine.AbilityTrigger.ON_COMBAT_START && duration == "cena") {
                    bad(
                        "início de combate não combina com duração por cena",
                    )
                }
                val drawbacks = f.getOrNull(6)?.takeIf { !blank(it) }?.let { effects(it, false, "desvantagem") }.orEmpty()
                AbilityDef(id(f[0], "id"), text(f[1], "nome"), cost, trigger, bonuses, duration, drawbacks)
            }
        val exceptions =
            table(PackageFiles.EXCEPTIONS, 4, 5) { f ->
                val modifiers =
                    if (blank(f[3])) {
                        emptyList()
                    } else {
                        f[3].split(";").map { part ->
                            val pieces = part.trim().split(":", limit = 2)
                            if (pieces.size != 2) bad("efeito inválido: '$part' (use alvo:+N)")
                            val value = int(pieces[1].trim().removePrefix("+"), "valor de efeito")
                            if (value == 0) bad("efeito de exceção não pode ser 0: '$part'")
                            dev.rpgsolo.engine.AbilityBonus(id(pieces[0].trim(), "alvo de efeito"), value)
                        }
                    }
                val immune = f.getOrNull(4)?.takeIf { !blank(it) }?.split(";")?.map { id(it.trim(), "condição imune") }.orEmpty()
                if (modifiers.isEmpty() && immune.isEmpty()) bad("exceção deve declarar efeito ou imunidade")
                ExceptionDef(id(f[0], "id"), id(f[1], "entidade"), text(f[2], "descricao"), modifiers, immune)
            }
        val recipes =
            table(PackageFiles.RECIPES, 6, 11) { f ->
                val kind = dev.rpgsolo.engine.CraftKind.fromId(f[2]) ?: bad("tipo de receita inválido: '${f[2]}'")

                fun qtyMap(
                    s: String,
                    what: String,
                ): Map<String, Int> =
                    if (blank(s)) {
                        emptyMap()
                    } else {
                        s.split(",").map { part ->
                            val pieces = part.trim().split(":")
                            if (pieces.size != 2) bad("$what inválido: '$part' (use item:quantidade)")
                            val qty = int(pieces[1].trim(), "quantidade")
                            if (qty <= 0) bad("quantidade de $what deve ser positiva")
                            pieces[0].trim() to qty
                        }.toMap()
                    }
                val inputs = qtyMap(f[3], "entrada")
                val outputs = qtyMap(f[4], "saída")
                if (outputs.isEmpty()) bad("receita sem saída")
                if (kind != dev.rpgsolo.engine.CraftKind.CRAFT && inputs.isEmpty()) bad("receita de ${kind.id} exige entrada")
                val minLevel = int(f[5], "nível")
                if (minLevel < 1) bad("nível mínimo deve ser >= 1")
                val difficulty = f.getOrNull(6)?.takeIf { !blank(it) }?.let { int(it, "dificuldade") } ?: 0
                if (difficulty !in 0..40) bad("dificuldade fora de 0..40")
                val keep = f.getOrNull(7)?.takeIf { !blank(it) }?.split(",")?.map { it.trim() }?.toSet().orEmpty()
                if (!inputs.keys.containsAll(keep)) bad("manter_na_falha deve citar apenas entradas da receita")
                val skill = f.getOrNull(8)?.takeIf { !blank(it) }?.let { id(it, "habilidade") }
                val guaranteed = f.getOrNull(9)?.takeIf { !blank(it) }?.let { id(it, "garantida_por") }
                listOfNotNull(skill, guaranteed).forEach { s ->
                    val tier = s.substringAfterLast('-').toIntOrNull()
                    if (tier == null || tier !in 1..5 || !s.startsWith("profissao-")) {
                        bad(
                            "degrau inválido: '$s' (use profissao-<arvore>-<1..5>)",
                        )
                    }
                }
                if (guaranteed != null && skill == null) bad("garantida_por exige habilidade")
                val days = f.getOrNull(10)?.takeIf { !blank(it) }?.let { int(it, "tempo") } ?: 0
                if (days !in 0..30) bad("tempo fora de 0..30 dias")
                RecipeDef(id(f[0], "id"), text(f[1], "nome"), kind, inputs, outputs, minLevel, difficulty, keep, skill, guaranteed, days)
            }
        // N-32.2: companheiros.txt (corpo|id|nome, ataque|id|corpo|nome|dano|efeito|chance, truque|id|nome|dificuldade|lealdade_min|combate|descricao, comida|corpo|item|ganho|preferida)
        val companionBodies = mutableListOf<CompanionBodyDef>()
        val companionAttacks = mutableListOf<CompanionAttackDef>()
        val companionTricks = mutableListOf<CompanionTrickDef>()
        val companionFoods = mutableListOf<CompanionFoodDef>()
        for ((n, f) in rows(PackageFiles.COMPANIONS)) {
            try {
                when (f[0]) {
                    "corpo" -> {
                        if (f.size != 3) bad("corpo exige 3 campos (corpo|id|nome)")
                        val bodyId = id(f[1], "id")
                        if (bodyId !in dev.rpgsolo.engine.Companions.BODY_TYPES) {
                            bad(
                                "tipo de corpo desconhecido: '$bodyId' (use ${dev.rpgsolo.engine.Companions.BODY_TYPES.sorted().joinToString()})",
                            )
                        }
                        companionBodies += CompanionBodyDef(bodyId, text(f[2], "nome"))
                    }
                    "ataque" -> {
                        if (f.size != 7) bad("ataque exige 7 campos (ataque|id|corpo|nome|dano|efeito|chance)")
                        val damage = CompanionDamage.fromId(f[4]) ?: bad("dano de ataque inválido: '${f[4]}' (use leve, normal ou alto)")
                        val effect = f[5]
                        val chance = optInt(f[6], "chance")
                        var kind = CompanionEffectKind.NONE
                        var ref: String? = null
                        when {
                            blank(effect) -> Unit
                            effect == "distancia" -> kind = CompanionEffectKind.RANGED
                            effect.startsWith("condicao:") -> {
                                kind = CompanionEffectKind.CONDITION
                                ref = id(effect.removePrefix("condicao:"), "condição")
                            }
                            effect.startsWith("elemento:") -> {
                                kind = CompanionEffectKind.ELEMENT
                                val raw = effect.removePrefix("elemento:")
                                val el =
                                    runCatching { dev.rpgsolo.engine.ElementChart.parse(raw) }.getOrElse {
                                            e ->
                                        bad(e.message.orEmpty())
                                    }
                                if (el == dev.rpgsolo.engine.ElementPath.NONE) bad("elemento do ataque não pode ser nenhum")
                                ref = id(raw, "elemento")
                            }
                            else -> bad("efeito de ataque inválido: '$effect' (use -, condicao:<id>, elemento:<id> ou distancia)")
                        }
                        if (kind == CompanionEffectKind.CONDITION) {
                            if (chance == null || chance !in 1..100) bad("chance de efeito deve estar em 1..100")
                        } else if (chance != null) {
                            bad("chance só vale para efeito de condição")
                        }
                        companionAttacks +=
                            CompanionAttackDef(id(f[1], "id"), id(f[2], "corpo"), text(f[3], "nome"), damage, kind, ref, chance ?: 0)
                    }
                    "truque" -> {
                        if (f.size != 7) bad("truque exige 7 campos (truque|id|nome|dificuldade|lealdade_min|combate|descricao)")
                        val difficulty = int(f[3], "dificuldade")
                        if (difficulty !in 1..20) bad("dificuldade do truque fora de 1..20")
                        val minLoyalty = int(f[4], "lealdade mínima")
                        if (minLoyalty !in 0..dev.rpgsolo.engine.Companions.MAX_LOYALTY) bad("lealdade mínima fora de 0..100")
                        val combat =
                            when (f[5]) {
                                "sim" -> true
                                "nao" -> false
                                else -> bad("combate deve ser sim ou nao: '${f[5]}'")
                            }
                        companionTricks += CompanionTrickDef(id(f[1], "id"), text(f[2], "nome"), difficulty, minLoyalty, combat, text(f[6], "descricao"))
                    }
                    "comida" -> {
                        if (f.size != 5) bad("comida exige 5 campos (comida|corpo|item|ganho|preferida)")
                        val gain = int(f[3], "ganho")
                        if (gain !in 1..dev.rpgsolo.engine.Companions.MAX_BASE_GAIN) {
                            bad(
                                "ganho de comida fora de 1..${dev.rpgsolo.engine.Companions.MAX_BASE_GAIN}",
                            )
                        }
                        val preferred =
                            when (f[4]) {
                                "sim" -> true
                                "nao" -> false
                                else -> bad("preferida deve ser sim ou nao: '${f[4]}'")
                            }
                        companionFoods += CompanionFoodDef(id(f[1], "corpo"), id(f[2], "item"), gain, preferred)
                    }
                    else -> bad("tipo de linha desconhecido: '${f[0]}' (use corpo, ataque, truque ou comida)")
                }
            } catch (e: IllegalArgumentException) {
                errors += "${PackageFiles.COMPANIONS} linha $n: ${e.message}"
            }
        }
        val companions = CompanionData(companionBodies, companionAttacks, companionTricks, companionFoods)
        val items =
            table(PackageFiles.ITEMS, 5, 15) { f ->
                val kind = f[2]
                if (kind !in setOf("equipamento", "consumivel", "narrativo")) bad("tipo de item inválido: '$kind'")
                val slot = optId(f[3], "slot")
                if (kind == "equipamento" && slot == null) bad("equipamento exige slot")
                if (kind != "equipamento" && slot != null) bad("apenas equipamento pode ocupar slot")
                val effects = itemEffects(f.getOrNull(5))
                if (kind != "equipamento" && effects.isNotEmpty()) bad("apenas equipamento pode ter efeitos automáticos")
                val price = f.getOrNull(6)?.takeIf { !blank(it) }?.let { int(it, "preço") }
                if (price != null && price !in 1..1_000_000) bad("preço fora de 1..1000000")
                val minLevel = f.getOrNull(7)?.takeIf { !blank(it) && it.trim() != "-" }?.let { int(it, "nível mínimo") } ?: 1
                if (minLevel < 1) bad("nível mínimo do item deve ser 1 ou mais")
                if (kind != "equipamento" && minLevel != 1) bad("apenas equipamento pode ter nível mínimo")
                val weight =
                    f.getOrNull(8)?.takeIf { !blank(it) && it.trim() != "-" }?.let {
                        ItemWeight.fromId(it.trim()) ?: bad("peso de item inválido: '${it.trim()}' (use pesado, medio ou leve)")
                    }
                val weaponType = f.getOrNull(9)?.takeIf { !blank(it) && it.trim() != "-" }?.let { id(it.trim(), "tipo de arma") }
                if (kind != "equipamento" && (weight != null || weaponType != null)) bad("apenas equipamento pode ter peso ou tipo de arma")
                if (weight != null && weaponType != null) bad("item não pode ter peso e tipo de arma ao mesmo tempo")
                val hands = f.getOrNull(10)?.takeIf { !blank(it) && it.trim() != "-" }?.let { int(it, "mãos") } ?: 1
                if (hands !in 1..2) bad("mãos do item deve ser 1 ou 2")
                if (hands == 2 && (kind != "equipamento" || slot != WEAPON_SLOT)) {
                    bad(
                        "apenas arma no slot $WEAPON_SLOT pode ocupar duas mãos",
                    )
                }
                val element =
                    f.getOrNull(11)?.let {
                        runCatching { dev.rpgsolo.engine.ElementChart.parse(it) }.getOrElse {
                                e ->
                            bad(e.message.orEmpty())
                        }
                    } ?: dev.rpgsolo.engine.ElementPath.NONE
                val conditionId = f.getOrNull(12)?.takeIf { !blank(it) && it.trim() != "-" }?.let { id(it.trim(), "condição do item") }
                val conditionChance = f.getOrNull(13)?.takeIf { !blank(it) && it.trim() != "-" }?.let { int(it, "chance de condição") } ?: 0
                if ((element != dev.rpgsolo.engine.ElementPath.NONE || conditionId != null) && (kind != "equipamento" || slot != WEAPON_SLOT)) {
                    bad("apenas arma no slot $WEAPON_SLOT pode ter elemento ou condição")
                }
                if (conditionId != null && conditionChance !in 1..100) bad("chance de condição deve ficar em 1..100")
                if (conditionId == null && conditionChance != 0) bad("chance de condição exige a condição")
                val scaleScope = f.getOrNull(14)?.takeIf { !blank(it) && it.trim() != "-" }?.trim()
                if (scaleScope != null) {
                    if (kind != "equipamento") bad("apenas equipamento pode ter escala por nível")
                    if (itemEffects("$scaleScope:+1").single().scope != scaleScope) bad("escala inválida: '$scaleScope'")
                }
                ItemDef(
                    id(
                        f[0],
                        "id",
                    ),
                    text(
                        f[1],
                        "nome",
                    ),
                    kind, slot,
                    text(
                        f[4],
                        "descricao",
                    ),
                    effects, price, minLevel, weight, weaponType, hands, element, conditionId, conditionChance, scaleScope,
                )
            }
        val creationRows = table(PackageFiles.CREATION, 2) { f -> text(f[0], "chave") to f[1] }
        noDups(PackageFiles.CREATION, creationRows.map { it.first })
        val creationMap = creationRows.toMap()
        val globalLuckResource = creationMap["recurso_sorte"]?.takeIf { !blank(it) }
        val globalLuckCostRaw = creationMap["custo_sorte"]?.takeIf { !blank(it) }
        val globalLuckKindsRaw = creationMap["formas_sorte"]?.takeIf { !blank(it) }
        if (listOf(globalLuckResource, globalLuckCostRaw, globalLuckKindsRaw).count { it != null } !in setOf(0, 3)) {
            errors += "${PackageFiles.CREATION}: recurso_sorte, custo_sorte e formas_sorte devem ser declarados juntos"
        }
        globalLuckCostRaw?.let { raw ->
            val cost = raw.toIntOrNull()
            if (cost == null || cost <= 0) {
                errors += "${PackageFiles.CREATION}: custo_sorte deve ser inteiro positivo"
            }
        }
        globalLuckKindsRaw?.let { raw ->
            val kinds = raw.split('+').map { it.trim().uppercase() }
            val allowed = dev.rpgsolo.engine.LuckKind.values().map { it.name }.toSet()
            if (kinds.any { it !in allowed } || kinds.distinct().size != kinds.size) {
                errors += "${PackageFiles.CREATION}: formas_sorte inválidas (use REROLL, ADD_FACE ou REROLL+ADD_FACE, sem repetição)"
            }
        }
        try {
            parseResourceRewards(creationMap["recompensa_recurso"])
        } catch (e: IllegalArgumentException) {
            errors += "${PackageFiles.CREATION}: ${e.message}"
        }
        creationMap["niveis_slot_magia"]?.let { raw ->
            val levels = if (raw.trim() == "-") emptyList() else raw.split(",").map { it.trim().toIntOrNull() }
            if (levels.any { it == null || it <= 0 } || levels.filterNotNull().zipWithNext().any { (a, b) -> a >= b }) {
                errors += "${PackageFiles.CREATION}: niveis_slot_magia deve ser lista crescente de inteiros positivos (ex.: 3,6,9)"
            }
        }
        creationMap["niveis_chave_pericia"]?.let { raw ->
            val levels = if (raw.trim() == "-") emptyList() else raw.split(",").map { it.trim().toIntOrNull() }
            if (levels.any { it == null || it <= 0 } || levels.filterNotNull().zipWithNext().any { (a, b) -> a >= b }) {
                errors += "${PackageFiles.CREATION}: niveis_chave_pericia deve ser lista crescente de inteiros positivos (ex.: 5,10)"
            }
        }
        creationMap["prata_por_ouro"]?.let { raw ->
            val ratio = raw.toIntOrNull()
            if (ratio == null || ratio !in 2..1000) errors += "${PackageFiles.CREATION}: prata_por_ouro deve ser inteiro de 2 a 1000"
        }
        for (key in listOf("pontos_por_nivel", "pontos_status_por_nivel", "pontos_habilidade_por_nivel")) {
            creationMap[key]?.let { raw ->
                val points = raw.toIntOrNull()
                if (points == null || points !in 0..100) {
                    errors += "${PackageFiles.CREATION}: $key deve ser inteiro de 0 a 100"
                }
            }
        }
        val progressionRows = table(PackageFiles.PROGRESSION, 2) { f -> int(f[0], "nivel") to int(f[1], "xp") }
        var progression: List<Int>? = null
        if (files.containsKey(PackageFiles.PROGRESSION)) {
            val sorted = progressionRows.sortedBy { it.first }
            sorted.forEachIndexed { i, (lvl, _) ->
                if (lvl != i + 2) errors += "${PackageFiles.PROGRESSION}: níveis devem ser 2, 3, 4... sem buracos (achei $lvl na posição ${i + 1})"
            }
            val xps = sorted.map { it.second }
            if (xps.any { it <= 0 } || xps.zipWithNext().any { (a, b) -> a >= b }) {
                errors += "${PackageFiles.PROGRESSION}: XP deve ser positivo e estritamente crescente"
            }
            progression = xps
        }
        val uncertain = table(PackageFiles.UNCERTAIN, 2) { f -> UncertainRule(id(f[0], "id"), text(f[1], "nota")) }
        val tutorial = table(PackageFiles.TUTORIAL, 3) { f -> TutorialStep(int(f[0], "ordem"), text(f[1], "titulo"), text(f[2], "texto")) }
        val examples =
            table(PackageFiles.EXAMPLES, 4) { f ->
                val traitIds = if (blank(f[3])) emptyList() else f[3].split(",").map { id(it.trim(), "traço") }
                ExampleCharacter(id(f[0], "id"), text(f[1], "titulo"), statMap(f[2]), traitIds)
            }

        val archetypes =
            table(PackageFiles.ARCHETYPES, 5) { f ->
                val traitIds = if (blank(f[4])) emptyList() else f[4].split(",").map { id(it.trim(), "traço") }
                Archetype(id(f[0], "id"), text(f[1], "nome"), text(f[2], "descricao"), statMap(f[3]), traitIds)
            }

        fun idList(
            s: String,
            what: String,
        ): List<String> = if (blank(s)) emptyList() else s.split(",").map { id(it.trim(), what) }
        val archetypeOptions =
            table(PackageFiles.ARCHETYPE_OPTIONS, 3) { f ->
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
        noDups(PackageFiles.POWERS, powers.map { it.id })
        noDups(PackageFiles.HAZARDS, hazards.map { it.id })
        noDups(PackageFiles.ABILITIES, abilities.map { it.id })
        noDups(PackageFiles.EXCEPTIONS, exceptions.map { it.id })
        noDups(PackageFiles.RECIPES, recipes.map { it.id })
        noDups(
            PackageFiles.COMPANIONS,
            companions.bodies.map {
                "corpo:${it.id}"
            } +
                companions.attacks.map {
                    "ataque:${it.id}"
                } + companions.tricks.map { "truque:${it.id}" } + companions.foods.map { "comida:${it.bodyType}/${it.itemId}" },
        )
        exceptions.forEach { ex ->
            ex.immuneConditions.filter { c -> conditions.none { it.id == c } }.forEach {
                errors += "${PackageFiles.EXCEPTIONS}: exceção '${ex.id}' referencia condição inexistente '$it'"
            }
        }
        noDups(PackageFiles.UNCERTAIN, uncertain.map { it.id })
        noDups(PackageFiles.TUTORIAL, tutorial.map { it.order.toString() })
        noDups(PackageFiles.EXAMPLES, examples.map { it.id })
        noDups(PackageFiles.ARCHETYPES, archetypes.map { it.id })
        noDups(PackageFiles.ARCHETYPE_OPTIONS, archetypeOptions.map { it.archetype })

        // Referências mecânicas precisam apontar para definições existentes no próprio pacote.
        // Isso evita traços/poderes que parecem válidos na ficha, mas nunca podem produzir efeito.
        if (manifest.extends == null) {
            val statIds = stats.map { it.id }.toSet()
            val resourceIds = resources.map { it.id }.toSet()
            val traitById = traits.associateBy { it.id }
            val traitIds = traitById.keys
            val checkIds = checks.map { it.id }.toSet()
            val supportedTraitScopes =
                statIds + checkIds + traitIds +
                    setOf("ataque", "dano", "defesa", "resistencia_dano", "mestre_armas", "mestre_equipamentos") +
                    resourceIds.map { "max_$it" } + conditions.map { "imune_${it.id}" }

            resources.forEach { resource ->
                if (resource.stat != null && resource.stat !in statIds) {
                    errors += "${PackageFiles.RESOURCES}: recurso '${resource.id}' referencia atributo inexistente '${resource.stat}'"
                }
            }
            traits.forEach { trait ->
                trait.requires.forEach { requirement ->
                    when (requirement) {
                        is Requirement.MinStat ->
                            if (requirement.stat !in statIds) {
                                errors += "${PackageFiles.TRAITS}: traço '${trait.id}' referencia atributo inexistente '${requirement.stat}'"
                            }
                        is Requirement.HasTrait ->
                            if (requirement.traitId !in traitIds) {
                                errors += "${PackageFiles.TRAITS}: traço '${trait.id}' exige traço inexistente '${requirement.traitId}'"
                            }
                        is Requirement.Excludes ->
                            if (requirement.traitId !in traitIds) {
                                errors += "${PackageFiles.TRAITS}: traço '${trait.id}' exclui traço inexistente '${requirement.traitId}'"
                            }
                    }
                }
                if (trait.conditionalBonuses.isNotEmpty() && trait.type != "pericia") {
                    errors += "${PackageFiles.TRAITS}: traço '${trait.id}' só perícias aceitam bônus condicional"
                }
                if (trait.level2Bonuses.isNotEmpty() && trait.type != "pericia") {
                    errors += "${PackageFiles.TRAITS}: traço '${trait.id}' só perícias aceitam bônus de nível 2"
                }
                trait.conditionalBonuses.forEach { cond ->
                    if (items.none { it.id == cond.itemId }) {
                        errors += "${PackageFiles.TRAITS}: traço '${trait.id}' referencia item inexistente '${cond.itemId}' no bônus condicional"
                    }
                }
                trait.bonuses.forEach { bonus ->
                    if (bonus.scope !in supportedTraitScopes) {
                        errors += "${PackageFiles.TRAITS}: traço '${trait.id}' usa escopo de bônus desconhecido '${bonus.scope}'"
                    }
                }
            }
            creationMap["recurso_sorte"]?.takeIf { !blank(it) }?.let { resource ->
                if (resource !in resourceIds) {
                    errors += "${PackageFiles.CREATION}: recurso_sorte referencia recurso inexistente '$resource'"
                }
            }
            checks.forEach { check ->
                if (check.stat != null && check.stat !in statIds) {
                    errors += "${PackageFiles.CHECKS}: teste '${check.id}' referencia atributo inexistente '${check.stat}'"
                }
                // O campo de perícia pode ser um rótulo fornecido pelo mestre mesmo quando o pacote
                // não cadastra a perícia como traço; o valor recebido em RulePackage.check continua sendo opcional.
                if (check.fortuneResource != null && check.fortuneResource !in resourceIds) {
                    errors += "${PackageFiles.CHECKS}: teste '${check.id}' referencia recurso de fortuna inexistente '${check.fortuneResource}'"
                }
                if (check.luckResource != null && check.luckResource !in resourceIds) {
                    errors += "${PackageFiles.CHECKS}: teste '${check.id}' referencia recurso de sorte inexistente '${check.luckResource}'"
                }
            }
            if (manifest.deathMode == DeathMode.DOWNED && manifest.deathResource == null) {
                errors += "${PackageFiles.MANIFEST}: modo_morte DOWNED exige o recurso de morte (morte)"
            }
            manifest.survivalCheck?.let { id ->
                if (checks.none { it.id == id }) errors += "${PackageFiles.MANIFEST}: teste_sobrevivencia referencia teste inexistente '$id'"
            }
            manifest.survivalDifficulty?.let { id ->
                if (difficulties.none {
                        it.id == id
                    }
                ) {
                    errors += "${PackageFiles.MANIFEST}: dificuldade_sobrevivencia referencia dificuldade inexistente '$id'"
                }
            }
            if (manifest.deathMode == DeathMode.DOWNED && manifest.survivalCheck == null) {
                errors += "${PackageFiles.MANIFEST}: modo_morte DOWNED exige teste_sobrevivencia"
            }
            manifest.deathResource?.let { deathResource ->
                if (deathResource !in resourceIds) {
                    errors += "${PackageFiles.MANIFEST}: deathResource referencia recurso inexistente '$deathResource'"
                }
            }
            conditions.forEach { condition ->
                condition.removalCheck?.let { id ->
                    if (checks.none {
                            it.id == id
                        }
                    ) {
                        errors += "${PackageFiles.CONDITIONS}: condição '${condition.id}' referencia teste de remoção inexistente '$id'"
                    }
                }
                condition.removalDifficulty?.let { id ->
                    if (difficulties.none {
                            it.id == id
                        }
                    ) {
                        errors += "${PackageFiles.CONDITIONS}: condição '${condition.id}' referencia dificuldade de remoção inexistente '$id'"
                    }
                }
                condition.effect?.stats?.filter { it !in statIds }?.forEach { unknown ->
                    errors += "${PackageFiles.CONDITIONS}: condição '${condition.id}' referencia atributo inexistente no efeito '$unknown'"
                }
                if ((condition.damagePerRound > 0 || condition.damagePercent > 0) && condition.damageResource !in resourceIds) {
                    errors += "${PackageFiles.CONDITIONS}: condição '${condition.id}' causa dano em recurso inexistente '${condition.damageResource}'"
                }
            }
            powers.forEach { power ->
                val trait = traitById[power.traitId]
                if (trait == null) {
                    errors += "${PackageFiles.POWERS}: poder '${power.id}' referencia traço inexistente '${power.traitId}'"
                } else if (trait.type != "poder") {
                    errors += "${PackageFiles.POWERS}: poder '${power.id}' exige traço do tipo poder, mas '${trait.id}' é ${trait.type}"
                }
                if (power.resource !in resourceIds) errors += "${PackageFiles.POWERS}: poder '${power.id}' usa recurso inexistente '${power.resource}'"
                if (power.checkId != null && checks.none { it.id == power.checkId }) {
                    errors += "${PackageFiles.POWERS}: poder '${power.id}' referencia teste inexistente '${power.checkId}'"
                }
                when (power.effect) {
                    PowerEffectKind.DAMAGE ->
                        if (damage.none { it.id == power.param }) {
                            errors += "${PackageFiles.POWERS}: poder '${power.id}' referencia perfil de dano inexistente '${power.param}'"
                        }
                    PowerEffectKind.APPLY_CONDITION, PowerEffectKind.REMOVE_CONDITION ->
                        if (conditions.none { it.id == power.param }) {
                            errors += "${PackageFiles.POWERS}: poder '${power.id}' referencia condição inexistente '${power.param}'"
                        }
                    PowerEffectKind.SHIELD ->
                        if ((power.param.toIntOrNull() ?: 0) <= 0) {
                            errors += "${PackageFiles.POWERS}: poder '${power.id}' tem quantidade de escudo inválida"
                        }
                    PowerEffectKind.HEAL ->
                        if (manifest.deathResource == null) {
                            errors += "${PackageFiles.POWERS}: poder '${power.id}' cura, mas o pacote não define o recurso de saúde (morte)"
                        }
                }
            }
            val validItemScopes = setOf("ataque", "dano", "defesa") + statIds + resourceIds.map { "max_$it" }
            items.forEach { item ->
                if (item.conditionId != null && conditions.none { it.id == item.conditionId }) {
                    errors += "${PackageFiles.ITEMS}: item '${item.id}' referencia condição inexistente '${item.conditionId}'"
                }
                item.effects.forEach { effect ->
                    if (effect.scope !in validItemScopes) {
                        errors += "${PackageFiles.ITEMS}: item '${item.id}' usa escopo de efeito desconhecido '${effect.scope}'"
                    }
                }
            }
            recipes.forEach { recipe ->
                recipe.skillId?.let { skill ->
                    val cap = CRAFT_TIER_LEVEL_CAP[skill.substringAfterLast('-').toInt() - 1]
                    recipe.outputs.keys.forEach { out ->
                        val def = items.firstOrNull { it.id == ItemRef.base(out) }
                        if (def != null && def.kind == "equipamento") {
                            val required = maxOf(def.minLevel, (ItemRef.quality(out) ?: ItemQuality.NORMAL).minLevel)
                            if (required > cap) {
                                errors += "${PackageFiles.RECIPES}: receita '${recipe.id}' cria '$out' (nível $required) acima do limite $cap do degrau $skill"
                            }
                        }
                    }
                }
                (recipe.inputs.keys + recipe.outputs.keys).filter {
                        ref ->
                    ItemRef.quality(ref) == null || items.none { it.id == ItemRef.base(ref) }
                }.forEach { unknown ->
                    errors += "${PackageFiles.RECIPES}: receita '${recipe.id}' referencia item inexistente '$unknown'"
                }
            }
            companions.attacks.forEach { attack ->
                if (companions.bodies.none { it.id == attack.bodyType }) {
                    errors += "${PackageFiles.COMPANIONS}: ataque '${attack.id}' referencia tipo de corpo inexistente '${attack.bodyType}'"
                }
                if (attack.effectKind == CompanionEffectKind.CONDITION && conditions.none { it.id == attack.effectRef }) {
                    errors += "${PackageFiles.COMPANIONS}: ataque '${attack.id}' referencia condição inexistente '${attack.effectRef}'"
                }
            }
            companions.bodies.forEach { body ->
                val count = companions.attacks.count { it.bodyType == body.id }
                if (count != 3) errors += "${PackageFiles.COMPANIONS}: tipo de corpo '${body.id}' deve ter 3 opções de ataque (tem $count)"
            }
            companions.foods.forEach { food ->
                if (companions.bodies.none { it.id == food.bodyType }) {
                    errors += "${PackageFiles.COMPANIONS}: comida '${food.itemId}' referencia tipo de corpo inexistente '${food.bodyType}'"
                }
                if (items.none { it.id == food.itemId }) {
                    errors += "${PackageFiles.COMPANIONS}: comida referencia item inexistente '${food.itemId}'"
                }
            }
            examples.forEach { example ->
                (example.stats.keys - statIds).forEach { unknown ->
                    errors += "${PackageFiles.EXAMPLES}: exemplo '${example.id}' referencia atributo inexistente '$unknown'"
                }
                (example.traits - traitIds).forEach { unknown ->
                    errors += "${PackageFiles.EXAMPLES}: exemplo '${example.id}' referencia traço inexistente '$unknown'"
                }
            }
            archetypes.forEach { archetype ->
                (archetype.stats.keys - statIds).forEach { unknown ->
                    errors += "${PackageFiles.ARCHETYPES}: arquétipo '${archetype.id}' referencia atributo inexistente '$unknown'"
                }
                (archetype.traits - traitIds).forEach { unknown ->
                    errors += "${PackageFiles.ARCHETYPES}: arquétipo ${archetype.id}: traço desconhecido $unknown"
                }
            }
            archetypeOptions.forEach { options ->
                options.advantages.filter { it !in traitIds }.forEach { unknown ->
                    errors += "${PackageFiles.ARCHETYPE_OPTIONS}: arquétipo ${options.archetype}: traço inexistente $unknown"
                }
                options.disadvantages.filter { it !in traitIds }.forEach { unknown ->
                    errors += "${PackageFiles.ARCHETYPE_OPTIONS}: arquétipo ${options.archetype}: traço inexistente $unknown"
                }
                options.disadvantages.mapNotNull { traitById[it] }.filter { it.type != "desvantagem" }.forEach { trait ->
                    errors += "${PackageFiles.ARCHETYPE_OPTIONS}: arquétipo ${options.archetype}: ${trait.id} não é desvantagem (é ${trait.type})"
                }
            }
        } // Child packages may refer to definitions inherited from their parent.

        if (errors.isNotEmpty()) return ParseResult.Fail(errors)
        return ParseResult.Ok(
            PackageData(
                manifest, stats, resources, traits, checks, difficulties, conditions, damage, items,
                creationMap, progression, uncertain, tutorial, examples, archetypes, archetypeOptions, powers, hazards, abilities, exceptions, recipes, companions,
            ),
        )
    }
}
