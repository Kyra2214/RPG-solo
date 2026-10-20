package dev.rpgsolo.rules

import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.AdvancementRequirement
import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.REAL_DICE
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.SystemRef
import java.text.Normalizer

data class CharacterReport(
    val errors: List<String>,
    val warnings: List<String>,
    val pointsSpent: Int,
    val pointsAvailable: Int,
    /** Equilíbrio vantagens x desvantagens (só tem efeito se o pacote declara `equilibrio` em criacao.txt). */
    val advantages: Int = 0,
    val disadvantages: Int = 0,
    val disadvantagesRequired: Int = 0
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

/** Tipos de traço que contam como "vantagem" no equilíbrio vantagens x desvantagens. */
internal val ADVANTAGE_TYPES = setOf("vantagem", "poder")
internal const val MAX_SKILL_POINTS = 5
private const val BALANCE_CAP = 1_000_000L
internal fun parseBalance(s: String?): List<Int> = s?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()

class RulePackage internal constructor(val data: PackageData) {
    val manifest: Manifest get() = data.manifest

    private val statById = data.stats.associateBy { it.id }
    private val traitById = data.traits.associateBy { it.id }
    private val checkById = data.checks.associateBy { it.id }
    private val difficultyById = data.difficulties.associateBy { it.id }
    private val uncertainById = data.uncertain.associateBy { it.id }
    private val itemById = data.items.associateBy { it.id }

    val creationPoints: Int get() = data.creation["pontos"]?.toIntOrNull() ?: 0
    val statCost: Int get() = data.creation["custo_atributo"]?.toIntOrNull() ?: 1

    /**
     * Tabela de equilíbrio (`equilibrio|1,3,5`): com N vantagens (N = 1, 2, 3...) a ficha precisa de pelo menos
     * tabela[N-1] desvantagens. Vazia = o pacote não usa equilíbrio por quantidade.
     */
    val balanceTable: List<Int> get() = parseBalance(data.creation["equilibrio"])

    /** Desvantagens mínimas para [advantages] vantagens. Passando do fim da tabela, `equilibrio_apos|dobrar` dobra a cada vantagem (4→10, 5→20...). */
    fun requiredDisadvantages(advantages: Int): Int {
        val table = balanceTable
        if (advantages <= 0 || table.isEmpty()) return 0
        if (advantages <= table.size) return table[advantages - 1]
        var v = table.last().toLong()
        if (data.creation["equilibrio_apos"] == "dobrar") {
            repeat(advantages - table.size) { v = minOf(v * 2, BALANCE_CAP) }
        }
        return v.toInt()
    }

    /** Vantagens (e poderes) contam do lado das vantagens; perícias não entram na conta. */
    fun isAdvantage(t: TraitDef): Boolean = t.type in ADVANTAGE_TYPES

    fun stat(id: String): StatDef? = statById[id]
    fun trait(id: String): TraitDef? = traitById[id]
    fun difficultyValue(id: String): Int? = difficultyById[id]?.value
    fun isUncertain(id: String): Boolean = id in uncertainById
    fun item(id: String): ItemDef? = itemById[id]
    /** Soma os efeitos dos equipamentos atualmente equipados para um escopo mecânico. */
    fun equipmentBonus(itemIds: Collection<String>, scope: String): Int =
        itemIds.sumOf { id -> itemById[id]?.effects.orEmpty().filter { it.scope == scope }.sumOf { it.bonus } }

    /** Bônus mecânico de traços para testes e combate (ataque, dano, defesa e resistência/vulnerabilidade a dano). */
    fun traitBonus(traitIds: Collection<String>, scope: String): Int =
        traitIds.distinct().sumOf { id -> traitById[id]?.bonuses.orEmpty().filter { it.scope == scope }.sumOf { it.bonus } }

    fun isEquipableItem(id: String, slot: String): Boolean =
        itemById[id]?.kind == "equipamento" && itemById[id]?.slot == slot

    /** Regras de elegibilidade para comprar novos pontos de uma perícia fora da criação inicial. */
    fun canImproveSkill(skillId: String, stats: Map<String, Int>, traitIds: Collection<String>): Boolean {
        val skill = traitById[skillId]?.takeIf { it.type == "pericia" } ?: return false
        val fullStats = data.stats.associate { it.id to (stats[it.id] ?: it.default) }
        val projectedTraits = (traitIds + skillId).toSet()
        return skill.requires.all { requirement ->
            when (requirement) {
                is Requirement.MinStat -> (fullStats[requirement.stat] ?: Int.MIN_VALUE) >= requirement.min
                is Requirement.HasTrait -> requirement.traitId in projectedTraits
                is Requirement.Excludes -> requirement.traitId !in projectedTraits
            }
        }
    }

    /** Configuração para o motor: o que mata e os níveis. Sem isso no pacote, o motor não mata nem sobe de nível. */
    fun engineConfig(): EngineConfig =
        EngineConfig(
            deathResource = manifest.deathResource,
            levelThresholds = data.progression ?: emptyList(),
            defendBonus = data.creation["combate_bonus_defender"]?.toIntOrNull() ?: 0,
            advancementPointsPerLevel = data.creation["pontos_por_nivel"]?.toIntOrNull() ?: 0,
            levelResourceRewards = parseResourceRewards(data.creation["recompensa_recurso"]),
            statCaps = data.stats.associate { it.id to it.max },
            statDefaults = data.stats.associate { it.id to it.default },
            skillCaps = data.traits.filter { it.type == "pericia" }.associate { it.id to MAX_SKILL_POINTS },
            skillRequirements = data.traits.filter { it.type == "pericia" }.associate { skill ->
                skill.id to AdvancementRequirement(
                    minStats = skill.requires.filterIsInstance<Requirement.MinStat>().associate { it.stat to it.min },
                    requiredTraits = skill.requires.filterIsInstance<Requirement.HasTrait>().map { it.traitId }.toSet(),
                    excludedTraits = skill.requires.filterIsInstance<Requirement.Excludes>().map { it.traitId }.toSet()
                )
            }
        )

    /** Recursos derivados dos atributos (vida, mana...), cheios. */
    fun resourcesFor(stats: Map<String, Int>): Map<String, Resource> =
        data.resources.associate { r ->
            val v = r.stat?.let { stats[it] ?: statById[it]?.default ?: 0 } ?: 0
            val max = maxOf(0, r.base + r.mult * v)
            r.id to Resource(max, max)
        }

    /**
     * Valida atributos e traços pelas regras de criação do pacote. Atributos ausentes usam o padrão.
     * [checkBalance] = false ignora só o mínimo de desvantagens (a tela usa para ofertar traços: o equilíbrio é uma conta corrente, não um requisito do traço).
     */
    fun validateCharacter(
        stats: Map<String, Int>,
        traits: List<String>,
        checkBalance: Boolean = true,
        skills: Map<String, Int> = emptyMap()
    ): CharacterReport {
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
        for ((skillId, value) in skills) {
            val skill = traitById[skillId]
            if (skill?.type != "pericia") errors += "perícia desconhecida: $skillId"
            if (value !in 0..MAX_SKILL_POINTS) {
                errors += "pontos da perícia ${skill?.name ?: skillId} fora do limite 0..$MAX_SKILL_POINTS: $value"
            }
            // Um nível positivo ativa os requisitos da perícia; 0 equivale a não tê-la selecionado.
            if (skill?.type == "pericia" && value > 0 && skillId !in traits) {
                for (requirement in skill.requires) when (requirement) {
                    is Requirement.MinStat ->
                        if ((full[requirement.stat] ?: Int.MIN_VALUE) < requirement.min) {
                            errors += "${skill.name} exige ${statById[requirement.stat]?.name ?: requirement.stat} >= ${requirement.min}"
                        }
                    is Requirement.HasTrait ->
                        if (requirement.traitId !in traits) {
                            errors += "${skill.name} exige o traço ${traitById[requirement.traitId]?.name ?: requirement.traitId}"
                        }
                    is Requirement.Excludes ->
                        if (requirement.traitId in traits) {
                            errors += "${skill.name} não pode ser combinado com ${traitById[requirement.traitId]?.name ?: requirement.traitId}"
                        }
                }
            }
            spent = (spent.toLong() + value.coerceAtLeast(0).toLong())
                .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        }
        val known = traits.distinct().mapNotNull { traitById[it] }
        val advCount = known.count { isAdvantage(it) }
        val disCount = known.count { it.type == "desvantagem" }
        val disNeeded = requiredDisadvantages(advCount)
        if (checkBalance && disCount < disNeeded) {
            val v = if (advCount == 1) "1 vantagem exige" else "$advCount vantagens exigem"
            errors += "$v pelo menos $disNeeded ${if (disNeeded == 1) "desvantagem" else "desvantagens"}; a ficha tem $disCount (faltam ${disNeeded - disCount})"
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
        return CharacterReport(errors, warnings, spent, creationPoints, advCount, disCount, disNeeded)
    }

    /** Cria a ficha pelas regras da edição: valida, deriva recursos e marca o sistema/edição. */
    fun buildFicha(id: String, name: String, concept: String, stats: Map<String, Int>, traits: List<String>, skills: Map<String, Int> = emptyMap()): FichaBuild {
        val report = validateCharacter(stats, traits, skills = skills)
        if (!report.valid) return FichaBuild.Invalid(report.errors)
        val full = data.stats.associate { it.id to (stats[it.id] ?: it.default) }
        val ficha = Ficha(
            id = id,
            name = name,
            system = SystemRef(manifest.system, manifest.edition),
            concept = concept,
            stats = full,
            resources = resourcesFor(full),
            traits = traits,
            skills = skills
        )
        return FichaBuild.Created(ficha, report.warnings)
    }

    fun archetype(id: String): Archetype? = data.archetypes.firstOrNull { it.id == id }

    /** Cardápio de vantagens e desvantagens do arquétipo (null se o pacote não define). */
    fun archetypeOptions(id: String): ArchetypeOptions? = data.archetypeOptions.firstOrNull { it.archetype == id }

    /** Arquétipos em ordem alfabética, sem considerar acento nem maiúsculas ("Órfão" vem entre "M" e "V"). */
    fun archetypesSorted(query: String = ""): List<Archetype> {
        val q = norm(query).trim()
        return data.archetypes
            .filter { q.isEmpty() || norm(it.name).contains(q) || norm(it.description).contains(q) }
            .sortedWith(compareBy({ norm(it.name) }, { it.id }))
    }

    /**
     * Dado de um teste de pontos na dificuldade escolhida: o dado do teste ([base], na "média") sobe ou desce na escada
     * dos dados reais (d4, d6, d8, d10, d12, d20) conforme a posição da dificuldade em relação à média.
     * Dificuldade fora da lista não muda o dado; a escada nunca passa do d4 nem do d20.
     */
    private fun poolDie(base: Int, difficulty: Int): Int {
        val sorted = data.difficulties.map { it.value }.distinct().sorted()
        val i = sorted.indexOf(difficulty)
        val ref = defaultDifficulty()?.let { sorted.indexOf(it) } ?: -1
        val at = REAL_DICE.indexOf(base)
        if (i < 0 || ref < 0 || at < 0) return base
        return REAL_DICE[(at + i - ref).coerceIn(0, REAL_DICE.lastIndex)]
    }

    /** Monta o teste para o motor. O resultado é decidido pelo motor; aqui só se descreve o teste. */
    fun check(checkId: String, stats: Map<String, Int>, difficulty: Int, skills: Map<String, Int> = emptyMap()): CheckSpec {
        val c = requireNotNull(checkById[checkId]) { "teste inexistente: $checkId" }
        val base = c.skill?.let { skills[it] ?: 0 } ?: c.stat?.let { (stats[it] ?: statById[it]?.default ?: 0) * c.mult } ?: 0
        // Teste de pontos não tem CD: a dificuldade escolhida troca o tamanho do dado (fácil d4, média d6, difícil d8...).
        val sides = if (c.comparison == Comparison.POINTS) poolDie(c.diceSides, difficulty) else c.diceSides
        return CheckSpec(
            label = c.name, diceCount = c.diceCount, diceSides = sides, modifier = base, difficulty = difficulty,
            comparison = c.comparison, partialMargin = c.partialMargin,
            criticalSuccessRange = c.critSuccess, criticalFailureRange = c.critFailure,
            stat = c.stat, checkId = c.id, fortuneMode = c.fortuneMode, explodeOnMax = c.explodeOnMax
        )
    }

    /**
     * Ajustes automáticos do teste na hora de rolar.
     * Condições: cada uma ativa com efeito sobre o atributo do teste soma +1 (Ganho) ou -1 (Perda) dado; se anulam;
     * nunca abaixo de 1 dado nem acima de 3 (ou dos dados que o teste já tinha, se mais).
     * Traços: cada traço do personagem com bônus cujo escopo bate com o id do teste, com o atributo dele ou com a
     * situação ([skill], ex.: "esquiva") soma esse valor ao total. Sem nada aplicável, o teste volta intacto.
     * Teste de pontos (POINTS): o bônus do traço soma pontos (talento "mestre em esquiva" = +2) e Ganho/Perda
     * somam +1/-1 ponto, em vez de mexer nos dados; as faces de sucesso ficam sempre entre 1 e (lados − 1).
     */
    fun adjustCheck(
        spec: CheckSpec, conditions: Collection<String>,
        traits: Collection<String> = emptyList(), skill: String? = null,
        additionalBonuses: Collection<ExternalBonus> = emptyList()
    ): CheckSpec {
        val stat: String? = spec.stat
        val shift = conditions.sumOf { id ->
            val e = data.conditions.firstOrNull { it.id == id }?.effect
            val scope: Set<String>? = e?.stats
            if (e != null && (scope == null || (stat != null && stat in scope))) e.dice else 0
        }
        // Teste de pontos: Ganho/Perda não mexem nos dados (é sempre 1 dado), mudam os pontos em +1/-1.
        val pool = spec.comparison == Comparison.POINTS
        val count = if (pool) spec.diceCount else (spec.diceCount + shift).coerceIn(1, maxOf(spec.diceCount, MAX_CONDITION_DICE))
        val checkId: String? = spec.checkId
        val bonus = traits.distinct().sumOf { tid ->
            traitById[tid]?.bonuses.orEmpty()
                .filter { b -> b.scope == checkId || (stat != null && b.scope == stat) || (skill != null && b.scope == skill) }
                .map { it.scope }.distinct().sumOf { sc -> traitById[tid]!!.bonuses.filter { it.scope == sc }.sumOf { it.bonus } }
        }
        // Efeitos tipados externos (ex.: raça/classe/maldição) usam o mesmo cálculo por escopo,
        // mas permanecem separados dos traços canônicos para evitar inserir cópias em Ficha.traits.
        val externalBonus = additionalBonuses
            .filter { b -> b.scope == checkId || (stat != null && b.scope == stat) || (skill != null && b.scope == skill) }
            .map { it.scope }.distinct()
            .sumOf { scope -> additionalBonuses.filter { it.scope == scope }.sumOf { it.bonus } }
        val totalBonus = bonus + externalBonus
        val pointShift = if (pool) shift else 0
        // O cartão do dado mostra por que mudou: "(2d6)" vira "(1d6) · Perda"; o bônus combinado aparece como "· +2".
        var label = spec.label
        if (count != spec.diceCount) {
            label = label.replace(Regex("\\(\\d+d\\d+\\)"), "(${count}d${spec.diceSides})") +
                if (count > spec.diceCount) " · Ganho" else " · Perda"
        }
        if (pointShift != 0) label += if (pointShift > 0) " · Ganho" else " · Perda"
        if (totalBonus != 0) label += if (totalBonus > 0) " · +$totalBonus" else " · $totalBonus"
        val matchingExternal = additionalBonuses.filter { b ->
            b.bonus != 0 && (b.scope == checkId || (stat != null && b.scope == stat) || (skill != null && b.scope == skill))
        }
        if (matchingExternal.isNotEmpty()) {
            val effectDetails = matchingExternal.joinToString(", ") { effect ->
                "${effect.source} (${effect.scope} ${if (effect.bonus > 0) "+" else ""}${effect.bonus})"
            }
            label += " · efeitos: $effectDetails"
        }
        if (count == spec.diceCount && totalBonus == 0 && pointShift == 0 && matchingExternal.isEmpty()) return spec
        return if (spec.comparison == Comparison.ROLL_UNDER) {
            spec.copy(diceCount = count, difficulty = spec.difficulty + totalBonus, modifier = spec.modifier + pointShift, label = label)
        } else {
            spec.copy(diceCount = count, modifier = spec.modifier + totalBonus + pointShift, label = label)
        }
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
    fun checkChoices(stats: Map<String, Int>, difficulty: Int? = defaultDifficulty(), skills: Map<String, Int> = emptyMap()): List<CheckChoice> {
        val d = difficulty ?: return emptyList()
        return data.checks.map { c ->
            val using = c.skill ?: c.stat?.let { statById[it]?.name ?: it }
            CheckChoice(c.id, c.name + (using?.let { " (usa $it)" } ?: ""), check(c.id, stats, d, skills))
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
        data.conditions.forEach {
            val mechanics = buildString {
                append(it.description)
                it.effect?.let { effect -> append(" Efeito nos testes: ").append(if (effect.dice > 0) "Ganho" else "Perda") }
                it.durationRounds?.let { rounds -> append(" Duração: ").append(rounds).append(" rodadas.") }
                if (it.damagePerRound > 0) append(" Dano por rodada: ").append(it.damagePerRound).append(" ").append(it.damageResource).append(".")
            }
            all += RuleSnippet("condicao", it.id, it.name, mechanics, score(it.name, mechanics))
        }
        data.stats.forEach { all += RuleSnippet("atributo", it.id, it.name, "Limites ${it.min}..${it.max}; padrão ${it.default}.", score(it.name, it.id)) }
        data.resources.forEach {
            val text = "Recurso ${it.name}; base ${it.base}; atributo ${it.stat ?: "nenhum"}; multiplicador ${it.mult}."
            all += RuleSnippet("recurso", it.id, it.name, text, score(it.name, text))
        }
        data.items.forEach {
            val mechanics = it.description + if (it.effects.isEmpty()) "" else
                " Efeitos: " + it.effects.joinToString(", ") { e -> "${e.scope} ${if (e.bonus >= 0) "+" else ""}${e.bonus}" }
            all += RuleSnippet("item", it.id, it.name, mechanics, score(it.name, mechanics))
        }
        data.damage.forEach {
            val mechanics = "${it.diceCount}d${it.diceSides}" + (it.bonusStat?.let { stat -> " + $stat" } ?: "")
            all += RuleSnippet("dano", it.id, it.name, mechanics, score(it.name, mechanics))
        }
        data.difficulties.forEach {
            all += RuleSnippet("dificuldade", it.id, it.name, "Valor ${it.value}.", score(it.name, it.id))
        }
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
        val idGroups = listOf(
            "atributo" to d.stats.map { it.id },
            "recurso" to d.resources.map { it.id },
            "traço" to d.traits.map { it.id },
            "teste" to d.checks.map { it.id },
            "dificuldade" to d.difficulties.map { it.id },
            "condição" to d.conditions.map { it.id },
            "perfil de dano" to d.damage.map { it.id },
            "item" to d.items.map { it.id }
        )
        idGroups.forEach { (kind, ids) ->
            ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.forEach { id ->
                p += "$kind duplicado: $id"
            }
            ids.filter { it.isBlank() }.forEach { p += "$kind com id vazio" }
        }
        if (d.stats.isEmpty()) p += "pacote sem atributos"
        d.resources.filter { it.stat != null && it.stat !in stats }.forEach { p += "recurso ${it.id}: atributo inexistente ${it.stat}" }
        val levelRewards = try {
            parseResourceRewards(d.creation["recompensa_recurso"])
        } catch (e: IllegalArgumentException) {
            p += "criacao.txt: ${e.message}"
            emptyMap()
        }
        levelRewards.keys.filter { reward -> d.resources.none { it.id == reward } }
            .forEach { p += "criacao.txt: recompensa por nível referencia recurso inexistente $it" }
        if (levelRewards.isNotEmpty() && d.progression.isNullOrEmpty()) {
            p += "criacao.txt: recompensa_recurso exige progressao.txt com limiares de XP"
        }
        d.checks.filter { it.stat != null && it.stat !in stats }.forEach { p += "teste ${it.id}: atributo inexistente ${it.stat}" }
        d.checks.filter { it.skill != null && d.traits.none { trait -> trait.id == it.skill && trait.type == "pericia" } }
            .forEach { p += "teste ${it.id}: perícia inexistente ou não classificada como perícia: ${it.skill}" }
        d.checks.forEach { check ->
            if (check.fortuneResource != null && d.resources.none { it.id == check.fortuneResource }) {
                p += "teste ${check.id}: recurso de fortuna inexistente ${check.fortuneResource}"
            }
            if ((check.fortuneResource == null) != (check.fortuneCost == 0) || check.fortuneCost < 0) {
                p += "teste ${check.id}: recurso e custo de fortuna inválidos"
            }
            if (check.fortuneResource != null && check.fortuneCost <= 0) {
                p += "teste ${check.id}: custo de fortuna deve ser positivo"
            }
            if (check.fortuneResource != null && check.fortuneSpendMode == null) {
                p += "teste ${check.id}: custo de fortuna exige modo pago"
            }
            if (check.fortuneResource == null && check.fortuneSpendMode != null) {
                p += "teste ${check.id}: modo pago exige recurso e custo de fortuna"
            }
            if (check.fortuneResource != null && check.fortuneMode != dev.rpgsolo.engine.FortuneMode.NORMAL) {
                p += "teste ${check.id}: política automática conflita com fortuna paga"
            }
            if (check.fortuneSpendMode == dev.rpgsolo.engine.FortuneMode.NORMAL) {
                p += "teste ${check.id}: modo de fortuna paga não pode ser NORMAL"
            }
        }
        d.conditions.forEach { c -> c.effect?.stats?.filter { it !in stats }?.forEach { p += "condição ${c.id}: atributo inexistente no efeito $it" } }
        d.conditions.forEach { c ->
            if (c.durationRounds != null && c.durationRounds <= 0) p += "condição ${c.id}: duração deve ser positiva"
            if (c.damageResource.isBlank()) p += "condição ${c.id}: recurso de dano vazio"
            if (c.damagePerRound < 0) p += "condição ${c.id}: dano por rodada negativo"
            if (c.damagePerRound > 0 && d.resources.none { it.id == c.damageResource }) {
                p += "condição ${c.id}: recurso de dano inexistente ${c.damageResource}"
            }
        }
        d.damage.filter { it.bonusStat != null && it.bonusStat !in stats }.forEach { p += "dano ${it.id}: atributo inexistente ${it.bonusStat}" }
        d.items.filter { it.kind !in setOf("equipamento", "consumivel", "narrativo") }
            .forEach { p += "item ${it.id}: tipo desconhecido '${it.kind}'" }
        d.items.filter { it.kind == "equipamento" && it.slot.isNullOrBlank() }
            .forEach { p += "item ${it.id}: equipamento exige slot" }
        d.items.filter { it.effects.isNotEmpty() && (it.kind != "equipamento" || it.slot.isNullOrBlank()) }
            .forEach { p += "item ${it.id}: efeitos mecânicos exigem equipamento com slot" }
        d.items.forEach { item ->
            item.effects.filter { it.scope.isBlank() || it.scope.length > 80 || it.bonus !in -100..100 }
                .forEach { p += "item ${item.id}: escopo ou bônus de efeito inválido" }
        }
        val validItemScopes = setOf("ataque", "dano", "defesa")
        d.items.forEach { item ->
            item.effects.filter { it.scope !in validItemScopes }
                .forEach { p += "item ${item.id}: escopo de efeito sem regra mecânica: ${it.scope}" }
        }
        d.traits.forEach { trait ->
            trait.bonuses.filter { it.scope.isBlank() || it.scope.length > 80 || it.bonus !in -100..100 }
                .forEach { p += "traço ${trait.id}: escopo ou bônus inválido" }
        }
        val validTraitScopes = stats + d.checks.map { it.id } + traits + setOf("ataque", "dano", "defesa", "resistencia_dano")
        d.traits.forEach { trait ->
            trait.bonuses.filter { it.scope !in validTraitScopes }.forEach { p += "traço " + trait.id + ": escopo de bônus sem regra mecânica: " + it.scope }
        }
        d.conditions.forEach { condition ->
            condition.effect?.let { effect ->
                if (effect.dice !in -10..10) p += "condição ${condition.id}: efeito de dados fora do limite -10..10"
            }
        }
        for (t in d.traits) for (r in t.requires) when (r) {
            is Requirement.MinStat -> if (r.stat !in stats) p += "traço ${t.id}: requisito de atributo inexistente ${r.stat}"
            is Requirement.HasTrait -> if (r.traitId !in traits) p += "traço ${t.id}: requisito de traço inexistente ${r.traitId}"
            is Requirement.Excludes -> if (r.traitId !in traits) p += "traço ${t.id}: exclusão de traço inexistente ${r.traitId}"
        }
        d.manifest.deathResource?.let { dr ->
            if (d.resources.none { it.id == dr }) p += "morte aponta para recurso inexistente: $dr"
        }
        val combatAttack = d.creation["combate_atributo_ataque"]
        val combatDefense = d.creation["combate_atributo_defesa"]
        val combatInitiative = d.creation["combate_atributo_iniciativa"]
        val attackDiceCount = d.creation["combate_dados_ataque"]
        val attackDiceSides = d.creation["combate_lados_ataque"]
        val attackComparison = d.creation["combate_comparacao_ataque"]
        val initiativeDiceCount = d.creation["combate_dados_iniciativa"]
        val initiativeDiceSides = d.creation["combate_lados_iniciativa"]
        val initiativeOrder = d.creation["combate_ordem_iniciativa"]
        val hasCombatProfile = combatAttack != null || combatDefense != null || combatInitiative != null
        if (listOf(combatAttack, combatDefense, combatInitiative).any { it != null } &&
            listOf(combatAttack, combatDefense, combatInitiative).any { it == null }) {
            p += "criacao.txt: perfil de combate incompleto; ataque, defesa e iniciativa devem ser definidos juntos"
        }
        listOf(
            "combate_atributo_ataque" to combatAttack,
            "combate_atributo_defesa" to combatDefense,
            "combate_atributo_iniciativa" to combatInitiative
        ).filter { it.second != null && it.second !in stats }
            .forEach { p += "criacao.txt: ${it.first} referencia atributo inexistente ${it.second}" }
        if (hasCombatProfile) {
            if (attackDiceCount == null || (attackDiceCount.toIntOrNull() ?: 0) !in 1..100) {
                p += "criacao.txt: combate_dados_ataque ausente ou inválido"
            }
            if (attackDiceSides == null || (attackDiceSides.toIntOrNull() ?: 0) < 2) {
                p += "criacao.txt: combate_lados_ataque ausente ou inválido"
            }
            if (attackComparison == null || attackComparison !in setOf("ROLL_OVER", "ROLL_UNDER", "POINTS")) {
                p += "criacao.txt: combate_comparacao_ataque ausente ou inválido"
            } else if (attackComparison == "POINTS") {
                if (attackDiceCount?.toIntOrNull() != 1 || attackDiceSides?.toIntOrNull() !in REAL_DICE) {
                    p += "criacao.txt: ataque POINTS exige combate_dados_ataque=1 e um dado real em combate_lados_ataque"
                }
            }
            if (initiativeDiceCount == null || (initiativeDiceCount.toIntOrNull() ?: 0) !in 1..100) {
                p += "criacao.txt: combate_dados_iniciativa ausente ou inválido"
            }
            if (initiativeDiceSides == null || (initiativeDiceSides.toIntOrNull() ?: 0) < 2) {
                p += "criacao.txt: combate_lados_iniciativa ausente ou inválido"
            }
            if (initiativeOrder !in setOf("MAIOR", "MENOR")) {
                p += "criacao.txt: combate_ordem_iniciativa ausente ou inválido (MAIOR ou MENOR)"
            }
            if ((d.creation["combate_defesa_base"]?.toIntOrNull() ?: -1) < 0) {
                p += "criacao.txt: combate_defesa_base ausente ou inválido"
            }
            if ((d.creation["combate_dano_base"]?.toIntOrNull() ?: -1) < 0) {
                p += "criacao.txt: combate_dano_base ausente ou inválido"
            }
        }
        listOf(
            "combate_atributo_ataque" to combatAttack,
            "combate_atributo_defesa" to combatDefense,
            "combate_atributo_iniciativa" to combatInitiative
        ).forEach { (key, value) ->
            if (value != null && value !in stats) p += "criacao.txt: '$key' referencia atributo inexistente: $value"
        }
        d.creation["combate_defesa_base"]?.let {
            if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'combate_defesa_base' inválido"
        }
        d.creation["combate_dano_base"]?.let {
            if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'combate_dano_base' inválido"
        }
        d.creation["combate_xp_vitoria"]?.let {
            if ((it.toIntOrNull() ?: -1) !in 0..100000) p += "criacao.txt: 'combate_xp_vitoria' deve ficar entre 0 e 100000"
        }
        d.creation["combate_bonus_defender"]?.let {
            if ((it.toIntOrNull() ?: -1) !in 0..10) p += "criacao.txt: 'combate_bonus_defender' deve ficar entre 0 e 10"
        }
        d.creation["combate_dano"]?.let { id ->
            if (d.damage.none { it.id == id }) p += "criacao.txt: perfil de dano inexistente: $id"
        }
        for ((key, value) in d.creation) {
            if (!key.startsWith("item_")) continue
            val suffix = key.removePrefix("item_")
            val itemId = suffix.substringBeforeLast('_', "")
            val property = suffix.substringAfterLast('_', "")
            val item = d.items.firstOrNull { it.id == itemId }
            if (item == null) {
                p += "criacao.txt: configuração referencia item inexistente: $itemId"
                continue
            }
            if (item.kind != "consumivel") p += "criacao.txt: só consumíveis podem ter configuração de uso: $itemId"
            when (property) {
                "acao" -> if (value !in setOf("cura")) p += "criacao.txt: ação de item não suportada: $value"
                "recurso" -> if (d.resources.none { it.id == value }) p += "criacao.txt: recurso de item inexistente: $value"
                "quantidade" -> if ((value.toIntOrNull() ?: 0) <= 0) p += "criacao.txt: quantidade de item inválida: $value"
                else -> p += "criacao.txt: propriedade de item desconhecida: $key"
            }
        }
        d.items.filter { it.kind == "consumivel" }.forEach { item ->
            val prefix = "item_${item.id}_"
            val action = d.creation[prefix + "acao"]
            val resource = d.creation[prefix + "recurso"]
            val amount = d.creation[prefix + "quantidade"]
            if (listOf(action, resource, amount).any { it != null } && action == null) {
                p += "criacao.txt: consumível ${item.id} define recurso/quantidade sem ação"
            }
            if (action != null) {
                if (resource == null) p += "criacao.txt: consumível ${item.id} sem recurso"
                if (amount == null) p += "criacao.txt: consumível ${item.id} sem quantidade"
            }
        }
        val pontos = d.creation["pontos"]?.toIntOrNull()
        if (pontos == null || pontos < 0) p += "criacao.txt: 'pontos' ausente ou inválido"
        d.creation["limite_desvantagens"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'limite_desvantagens' inválido" }
        d.creation["custo_atributo"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'custo_atributo' inválido" }
        d.creation["equilibrio"]?.let { raw ->
            val parts = raw.split(",").map { it.trim() }
            val nums = parts.mapNotNull { it.toIntOrNull() }
            if (nums.size != parts.size || nums.isEmpty() || nums.any { it < 1 } || nums.zipWithNext().any { (a, b) -> b < a })
                p += "criacao.txt: 'equilibrio' inválido (use números crescentes, ex.: 1,3,5)"
        }
        d.creation["equilibrio_apos"]?.let { if (it != "dobrar" && it != "manter") p += "criacao.txt: 'equilibrio_apos' inválido (dobrar ou manter)" }
        if (d.creation.containsKey("equilibrio_apos") && !d.creation.containsKey("equilibrio")) p += "criacao.txt: 'equilibrio_apos' sem 'equilibrio'"
        d.creation["minimo_opcoes"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'minimo_opcoes' inválido" }
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
        // Arquétipos também: nenhum pode nascer quebrando pontos, limites ou requisitos.
        for (a in d.archetypes) {
            val r = pkg.validateCharacter(a.stats, a.traits)
            r.errors.forEach { p += "arquétipo ${a.id}: $it" }
        }
        // Cardápio dos arquétipos (opcoes-arquetipo.txt).
        val traitById = d.traits.associateBy { it.id }
        val archById = d.archetypes.associateBy { it.id }
        for (o in d.archetypeOptions) {
            val a = archById[o.archetype]
            if (a == null) { p += "opções: arquétipo inexistente ${o.archetype}"; continue }
            val chosen = o.advantages + o.disadvantages
            chosen.groupBy { it }.filter { it.value.size > 1 }.keys.forEach { p += "opções de ${o.archetype}: traço repetido $it" }
            for (id in o.advantages) {
                val t = traitById[id]
                if (t == null) { p += "opções de ${o.archetype}: traço inexistente $id"; continue }
                if (t.type !in ADVANTAGE_TYPES) p += "opções de ${o.archetype}: $id não é vantagem (é ${t.type})"
            }
            for (id in o.disadvantages) {
                val t = traitById[id]
                if (t == null) { p += "opções de ${o.archetype}: traço inexistente $id"; continue }
                if (t.type != "desvantagem") p += "opções de ${o.archetype}: $id não é desvantagem (é ${t.type})"
            }
            // Todo item do cardápio precisa ser escolhível por quem tem os atributos do arquétipo.
            for (id in chosen) {
                val t = traitById[id] ?: continue
                for (r in t.requires) when (r) {
                    is Requirement.MinStat ->
                        if ((a.stats[r.stat] ?: d.stats.firstOrNull { it.id == r.stat }?.default ?: 0) < r.min)
                            p += "opções de ${o.archetype}: $id exige ${r.stat} >= ${r.min}, mais do que o arquétipo tem"
                    is Requirement.HasTrait ->
                        if (r.traitId !in chosen) p += "opções de ${o.archetype}: $id exige ${r.traitId}, que não está no cardápio"
                    is Requirement.Excludes ->
                        if (r.traitId in chosen) p += "opções de ${o.archetype}: $id não combina com ${r.traitId}, também no cardápio"
                }
            }
        }
        d.creation["minimo_opcoes"]?.toIntOrNull()?.let { min ->
            val byArch = d.archetypeOptions.associateBy { it.archetype }
            for (a in d.archetypes) {
                val o = byArch[a.id]
                val v = o?.advantages?.size ?: 0
                val ds = o?.disadvantages?.size ?: 0
                if (v < min) p += "arquétipo ${a.id}: precisa de pelo menos $min vantagens no cardápio (tem $v)"
                if (ds < min) p += "arquétipo ${a.id}: precisa de pelo menos $min desvantagens no cardápio (tem $ds)"
            }
        }
        return p
    }
}

/** Limite de dados de um teste ao somar Ganhos (3D&T). */
private const val MAX_CONDITION_DICE = 3
