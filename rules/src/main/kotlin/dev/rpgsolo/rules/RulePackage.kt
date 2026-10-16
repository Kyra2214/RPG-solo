package dev.rpgsolo.rules

import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.SystemRef
import java.text.Normalizer

data class CharacterReport(
    val errors: List<String>,
    val warnings: List<String>,
    val pointsSpent: Int,
    val pointsAvailable: Int
) {
    val valid: Boolean get() = errors.isEmpty()
}

sealed interface FichaBuild {
    data class Created(val ficha: Ficha, val warnings: List<String>) : FichaBuild
    data class Invalid(val errors: List<String>) : FichaBuild
}

data class RuleSnippet(val kind: String, val id: String, val title: String, val text: String, val score: Int)

/** Pacote já resolvido (herança aplicada) e validado. Tudo o que é específico de um sistema vem daqui. */
/** Teste que o pacote oferece para ações do jogador (7.1.2). O [spec] já traz modificador e dificuldade. */
data class CheckChoice(val id: String, val description: String, val spec: CheckSpec)

class RulePackage internal constructor(val data: PackageData) {
    val manifest: Manifest get() = data.manifest

    private val statById = data.stats.associateBy { it.id }
    private val traitById = data.traits.associateBy { it.id }
    private val checkById = data.checks.associateBy { it.id }
    private val difficultyById = data.difficulties.associateBy { it.id }
    private val uncertainById = data.uncertain.associateBy { it.id }

    val creationPoints: Int get() = data.creation["pontos"]?.toIntOrNull() ?: 0
    val statCost: Int get() = data.creation["custo_atributo"]?.toIntOrNull() ?: 1

    fun stat(id: String): StatDef? = statById[id]
    fun trait(id: String): TraitDef? = traitById[id]
    fun difficultyValue(id: String): Int? = difficultyById[id]?.value
    fun isUncertain(id: String): Boolean = id in uncertainById

    /** Configuração para o motor: o que mata e os níveis. Sem isso no pacote, o motor não mata nem sobe de nível. */
    fun engineConfig(): EngineConfig =
        EngineConfig(deathResource = manifest.deathResource, levelThresholds = data.progression ?: emptyList())

    /** Recursos derivados dos atributos (vida, mana...), cheios. */
    fun resourcesFor(stats: Map<String, Int>): Map<String, Resource> =
        data.resources.associate { r ->
            val v = r.stat?.let { stats[it] ?: statById[it]?.default ?: 0 } ?: 0
            val max = maxOf(0, r.base + r.mult * v)
            r.id to Resource(max, max)
        }

    /** Valida atributos e traços pelas regras de criação do pacote. Atributos ausentes usam o padrão. */
    fun validateCharacter(stats: Map<String, Int>, traits: List<String>): CharacterReport {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        stats.keys.filter { it !in statById }.forEach { errors += "atributo desconhecido: $it" }
        val full = data.stats.associate { it.id to (stats[it.id] ?: it.default) }
        var spent = 0
        for (def in data.stats) {
            val v = full.getValue(def.id)
            if (v !in def.min..def.max) errors += "${def.name} fora do limite ${def.min}..${def.max}: $v"
            spent += (v - def.min) * statCost
            if (isUncertain(def.id) && stats.containsKey(def.id)) warnings += "regra incerta em ${def.name}: ${uncertainById.getValue(def.id).note}"
        }
        traits.groupBy { it }.filter { it.value.size > 1 }.keys.forEach { errors += "traço repetido: $it" }
        for (tid in traits.distinct()) {
            val t = traitById[tid]
            if (t == null) { errors += "traço desconhecido: $tid"; continue }
            spent += t.cost
            for (r in t.requires) when (r) {
                is Requirement.MinStat ->
                    if ((full[r.stat] ?: Int.MIN_VALUE) < r.min) errors += "${t.name} exige ${statById[r.stat]?.name ?: r.stat} >= ${r.min}"
                is Requirement.HasTrait ->
                    if (r.traitId !in traits) errors += "${t.name} exige o traço ${traitById[r.traitId]?.name ?: r.traitId}"
                is Requirement.Excludes ->
                    if (r.traitId in traits) errors += "${t.name} não pode ser combinado com ${traitById[r.traitId]?.name ?: r.traitId}"
            }
            if (isUncertain(tid)) warnings += "regra incerta em ${t.name}: ${uncertainById.getValue(tid).note}"
        }
        val negativos = -traits.distinct().mapNotNull { traitById[it] }.filter { it.type == "desvantagem" }.sumOf { it.cost }
        data.creation["limite_desvantagens"]?.toIntOrNull()?.let { lim ->
            if (negativos > lim) errors += "desvantagens somam $negativos pontos; o limite é $lim"
        }
        if (spent > creationPoints) errors += "pontos gastos ($spent) passam do limite ($creationPoints)"
        for (r in data.resources) {
            val v = r.stat?.let { full[it] ?: 0 } ?: 0
            if (r.base + r.mult * v < 0) errors += "recurso ${r.name} ficaria negativo"
        }
        return CharacterReport(errors, warnings, spent, creationPoints)
    }

    /** Cria a ficha pelas regras da edição: valida, deriva recursos e marca o sistema/edição. */
    fun buildFicha(id: String, name: String, concept: String, stats: Map<String, Int>, traits: List<String>): FichaBuild {
        val report = validateCharacter(stats, traits)
        if (!report.valid) return FichaBuild.Invalid(report.errors)
        val full = data.stats.associate { it.id to (stats[it.id] ?: it.default) }
        val ficha = Ficha(id, name, SystemRef(manifest.system, manifest.edition), concept, full, resourcesFor(full), traits)
        return FichaBuild.Created(ficha, report.warnings)
    }

    /** Monta o teste para o motor. O resultado é decidido pelo motor; aqui só se descreve o teste. */
    fun check(checkId: String, stats: Map<String, Int>, difficulty: Int): CheckSpec {
        val c = requireNotNull(checkById[checkId]) { "teste inexistente: $checkId" }
        val mod = c.stat?.let { (stats[it] ?: statById[it]?.default ?: 0) * c.mult } ?: 0
        return CheckSpec(
            label = c.name, diceCount = c.diceCount, diceSides = c.diceSides, modifier = mod, difficulty = difficulty,
            comparison = c.comparison, partialMargin = c.partialMargin,
            criticalSuccessRange = c.critSuccess, criticalFailureRange = c.critFailure
        )
    }

    /**
     * Dificuldade usada quando a ação não diz qual: "media" se o pacote tiver, senão a do meio (por valor).
     * null se o pacote não define nenhuma; então não há testes a oferecer (o motor não inventa dificuldade).
     */
    fun defaultDifficulty(): Int? {
        difficultyById["media"]?.let { return it.value }
        val sorted = data.difficulties.map { it.value }.sorted()
        return sorted.getOrNull(sorted.size / 2)
    }

    /** Todos os testes do pacote, prontos para o classificador escolher um (7.1.2). [stats] = atributos do personagem. */
    fun checkChoices(stats: Map<String, Int>, difficulty: Int? = defaultDifficulty()): List<CheckChoice> {
        val d = difficulty ?: return emptyList()
        return data.checks.map { c ->
            val using = c.stat?.let { statById[it]?.name ?: it }
            CheckChoice(c.id, c.name + (using?.let { " (usa $it)" } ?: ""), check(c.id, stats, d))
        }
    }

    /** Busca por trecho de regra (4.2.3): só o necessário vai para a IA. Ignora acentos e maiúsculas. */
    fun search(query: String, limit: Int = 5): List<RuleSnippet> {
        val tokens = norm(query).split(Regex("[^a-z0-9]+")).filter { it.length >= 3 }.distinct()
        if (tokens.isEmpty()) return emptyList()
        fun score(title: String, text: String): Int {
            val t = norm(title)
            val x = norm(text)
            return tokens.sumOf { (if (t.contains(it)) 2 else 0) + (if (x.contains(it)) 1 else 0) }
        }
        val all = mutableListOf<RuleSnippet>()
        data.traits.forEach { all += RuleSnippet("traco", it.id, it.name, it.description, score(it.name, it.description)) }
        data.conditions.forEach { all += RuleSnippet("condicao", it.id, it.name, it.description, score(it.name, it.description)) }
        data.tutorial.forEach { all += RuleSnippet("tutorial", it.order.toString(), it.title, it.text, score(it.title, it.text)) }
        data.checks.forEach {
            val d = "${it.diceCount}d${it.diceSides}"
            all += RuleSnippet("teste", it.id, it.name, d, score(it.name, d))
        }
        return all.filter { it.score > 0 }.sortedWith(compareByDescending<RuleSnippet> { it.score }.thenBy { it.title }).take(limit)
    }

    private fun norm(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").lowercase()
}

/** Confere a coerência interna de um pacote já resolvido. Lista vazia = pacote válido. */
object PackageValidator {
    fun validate(d: PackageData): List<String> {
        val p = mutableListOf<String>()
        val stats = d.stats.map { it.id }.toSet()
        val traits = d.traits.map { it.id }.toSet()
        if (d.stats.isEmpty()) p += "pacote sem atributos"
        d.resources.filter { it.stat != null && it.stat !in stats }.forEach { p += "recurso ${it.id}: atributo inexistente ${it.stat}" }
        d.checks.filter { it.stat != null && it.stat !in stats }.forEach { p += "teste ${it.id}: atributo inexistente ${it.stat}" }
        d.damage.filter { it.bonusStat != null && it.bonusStat !in stats }.forEach { p += "dano ${it.id}: atributo inexistente ${it.bonusStat}" }
        for (t in d.traits) for (r in t.requires) when (r) {
            is Requirement.MinStat -> if (r.stat !in stats) p += "traço ${t.id}: requisito de atributo inexistente ${r.stat}"
            is Requirement.HasTrait -> if (r.traitId !in traits) p += "traço ${t.id}: requisito de traço inexistente ${r.traitId}"
            is Requirement.Excludes -> if (r.traitId !in traits) p += "traço ${t.id}: exclusão de traço inexistente ${r.traitId}"
        }
        d.manifest.deathResource?.let { dr ->
            if (d.resources.none { it.id == dr }) p += "morte aponta para recurso inexistente: $dr"
        }
        val pontos = d.creation["pontos"]?.toIntOrNull()
        if (pontos == null || pontos < 0) p += "criacao.txt: 'pontos' ausente ou inválido"
        d.creation["limite_desvantagens"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'limite_desvantagens' inválido" }
        d.creation["custo_atributo"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'custo_atributo' inválido" }
        val everyId = stats + traits + d.resources.map { it.id } + d.checks.map { it.id } +
            d.difficulties.map { it.id } + d.conditions.map { it.id } + d.damage.map { it.id }
        d.uncertain.filter { it.id !in everyId }.forEach { p += "incerto.txt: id inexistente ${it.id}" }
        if (p.isNotEmpty()) return p
        // Exemplos do próprio pacote precisam passar nas regras dele (autoteste do formato).
        val pkg = RulePackage(d)
        for (ex in d.examples) {
            val r = pkg.validateCharacter(ex.stats, ex.traits)
            r.errors.forEach { p += "exemplo ${ex.id}: $it" }
        }
        return p
    }
}
