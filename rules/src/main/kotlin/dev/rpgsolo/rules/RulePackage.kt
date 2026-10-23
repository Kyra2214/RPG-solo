package dev.rpgsolo.rules

import dev.rpgsolo.engine.AdvancementRequirement
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.DeathMode
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
    val disadvantagesRequired: Int = 0,
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
internal const val DEFAULT_SURVIVAL_FAILURES = 3
private const val BALANCE_CAP = 1_000_000L

internal fun parseBalance(s: String?): List<Int> = s?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()

class RulePackage internal constructor(val data: PackageData) {
    /** MU-06.7: habilidade declarada em `habilidades.txt` como especificação do motor; null se o id não existe. */
    /** MU-06.8: exceções de `excecoes.txt` como registro do motor; null se o pacote não declara nenhuma. */
    fun exceptionRegistry(): dev.rpgsolo.engine.ExceptionRegistry? =
        data.exceptions.takeIf { it.isNotEmpty() }?.let { defs ->
            dev.rpgsolo.engine.ExceptionRegistry(
                defs.map { ex ->
                    dev.rpgsolo.engine.MechanicalException(
                        ex.id, ex.entityId, ex.description,
                        ex.modifiers.map { dev.rpgsolo.engine.Modifier("excecao:${ex.id}", ex.description, it.target, it.value) },
                        ex.immuneConditions.toSet(),
                    )
                },
            )
        }

    fun abilitySpec(id: String): dev.rpgsolo.engine.AbilitySpec? = data.abilities.firstOrNull { it.id == id }?.toSpec()

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

    /** Definição do item; ids com qualidade (`base#rara`) resolvem para o item base se a qualidade for válida. */
    fun item(id: String): ItemDef? = if (ItemRef.quality(id) == null) null else itemById[ItemRef.base(id)]

    /** Percentual de efeito da qualidade (`qualidade_<id>_percent` em criacao.txt; padrão em [ItemQuality.defaultPercent]). */
    fun qualityPercent(quality: ItemQuality): Int = data.creation["qualidade_${quality.id}_percent"]?.toIntOrNull() ?: quality.defaultPercent

    /** Escala [value] positivo pela qualidade do item [itemId] (arredonda para o mais próximo, mínimo 1); zero ou negativo não muda. */
    fun scaleByQuality(
        value: Int,
        itemId: String,
    ): Int {
        if (value <= 0) return value
        val percent = qualityPercent(ItemRef.quality(itemId) ?: ItemQuality.NORMAL)
        return maxOf(1, (value * percent + 50) / 100)
    }

    /** M-04: ids base de itens de renascimento (`itens_renascimento` em criacao.txt, separados por vírgula). O motor compara ids exatos, então cada qualidade vem listada abaixo. */
    val rebirthItemIds: Set<String>
        get() =
            data.creation["itens_renascimento"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() && it != "-" }
                ?.flatMap { base -> ItemQuality.entries.map { ItemRef.of(base, it) } }?.toSet() ?: emptySet()

    /** Item de moeda declarado por `moeda_item` em criacao.txt; null = pacote sem economia. */
    val currencyItemId: String? get() = data.creation["moeda_item"]

    /** MU-07.3: magias compráveis, declaradas por `magia_<id>_preco` (e `magia_<id>_nome`, opcional) em criacao.txt. */
    val spells: List<SpellDef> get() =
        data.creation.keys
            .filter { it.startsWith("magia_") && it.endsWith("_preco") }
            .map { it.removePrefix("magia_").removeSuffix("_preco") }
            .sorted()
            .mapNotNull { id ->
                val price = data.creation["magia_${id}_preco"]?.toIntOrNull() ?: return@mapNotNull null
                SpellDef(id, data.creation["magia_${id}_nome"]?.takeIf { it.isNotBlank() } ?: id, price)
            }

    fun spell(id: String): SpellDef? = spells.firstOrNull { it.id == id }

    /** MU-07.4: poder (`poderes.txt`) que a magia conjura, declarado por `magia_<id>_poder` em criacao.txt; null = sem efeito. */
    fun spellPowerId(spellId: String): String? = data.creation["magia_${spellId}_poder"]?.takeIf { it.isNotBlank() }

    /** MU-07.1: item de ouro (`moeda_ouro`); null = sem ouro (só a moeda base, a prata). */
    val goldItemId: String? get() = data.creation["moeda_ouro"]

    /** MU-07.1: quantas moedas base (prata) valem 1 ouro (`prata_por_ouro`, padrão 10). */
    val silverPerGold: Int get() = data.creation["prata_por_ouro"]?.toIntOrNull() ?: 10

    /**
     * MU-07.1: plano de pagamento de [total] moedas base, gastando prata primeiro e quebrando ouro só no que faltar
     * (o troco volta em prata). Null se o saldo não cobre o total.
     */
    fun planPayment(
        silverHeld: Int,
        goldHeld: Int,
        total: Int,
    ): CoinPayment? {
        require(total >= 0) { "total negativo" }
        val silverPaid = minOf(silverHeld, total)
        val missing = total - silverPaid
        if (missing == 0) return CoinPayment(silverPaid, 0, 0)
        if (goldItemId == null) return null
        val ratio = silverPerGold
        val gold = (missing + ratio - 1) / ratio
        if (gold > goldHeld) return null
        return CoinPayment(silverPaid, gold, gold * ratio - missing)
    }

    /** Soma os efeitos dos equipamentos atualmente equipados para um escopo mecânico. */
    fun equipmentBonus(
        itemIds: Collection<String>,
        scope: String,
    ): Int =
        itemIds.sumOf { id ->
            item(id)?.effects.orEmpty().filter { it.scope == scope }.sumOf { scaleByQuality(it.bonus, id) }
        }

    /** Bônus mecânico de traços para testes e combate (ataque, dano, defesa e resistência/vulnerabilidade a dano). */
    fun traitBonus(
        traitIds: Collection<String>,
        scope: String,
    ): Int = traitIds.distinct().sumOf { id -> traitById[id]?.bonuses.orEmpty().filter { it.scope == scope }.sumOf { it.bonus } }

    /** M-01.7: imunidade a condição por traço com escopo `imune_<condição>` e bônus positivo. */
    fun isImmuneToCondition(
        traitIds: Collection<String>,
        conditionId: String,
    ): Boolean = traitBonus(traitIds, "imune_" + conditionId) > 0

    /** M-03.6: dano do perfil de perigo [hazardId] para quem tem [maxHealth] de vida máxima; null = perfil inexistente. */
    fun hazardDamage(
        hazardId: String,
        maxHealth: Int,
    ): Int? {
        val hazard = data.hazards.firstOrNull { it.id == hazardId } ?: return null
        if (maxHealth <= 0) return 0
        return maxOf(1, (maxHealth * hazard.percent + 99) / 100).coerceAtMost(maxHealth)
    }

    fun isEquipableItem(
        id: String,
        slot: String,
    ): Boolean = itemById[id]?.kind == "equipamento" && itemById[id]?.slot == slot

    /** Regras de elegibilidade para comprar novos pontos de uma perícia fora da criação inicial. */
    fun canImproveSkill(
        skillId: String,
        stats: Map<String, Int>,
        traitIds: Collection<String>,
    ): Boolean {
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

    /** M-05: condição removível por teste (declarada em `condicoes.txt`), ou null. */
    fun conditionRemovalSpec(
        conditionId: String,
        stats: Map<String, Int>,
        skills: Map<String, Int> = emptyMap(),
    ): CheckSpec? {
        val def = data.conditions.firstOrNull { it.id == conditionId } ?: return null
        val id = def.removalCheck?.takeIf { it in checkById } ?: return null
        val difficulty = def.removalDifficulty?.let { difficultyValue(it) } ?: defaultDifficulty() ?: 0
        return check(id, stats, difficulty, skills)
    }

    /** M-04.4: falhas de sobrevivência que matam o caído (padrão 3). */
    val survivalFailureLimit: Int get() = manifest.survivalFailures ?: DEFAULT_SURVIVAL_FAILURES

    /** M-04.4: teste de sobrevivência declarado pelo pacote, ou null (sem declaração ou teste inexistente). */
    fun survivalSpec(
        stats: Map<String, Int>,
        skills: Map<String, Int> = emptyMap(),
    ): CheckSpec? {
        val id = manifest.survivalCheck?.takeIf { it in checkById } ?: return null
        val difficulty = manifest.survivalDifficulty?.let { difficultyValue(it) } ?: defaultDifficulty() ?: 0
        return check(id, stats, difficulty, skills)
    }

    /** Configuração para o motor: o que mata e os níveis. Sem isso no pacote, o motor não mata nem sobe de nível. */
    fun engineConfig(): EngineConfig =
        EngineConfig(
            deathResource = manifest.deathResource,
            deathMode = manifest.deathMode ?: DeathMode.PERMADEATH,
            rebirthItems = rebirthItemIds,
            rebirthPercent = data.creation["renascimento_percentual"]?.toIntOrNull() ?: 50,
            levelThresholds = data.progression ?: emptyList(),
            defendBonus = data.creation["combate_bonus_defender"]?.toIntOrNull() ?: 0,
            advancementPointsPerLevel = data.creation["pontos_por_nivel"]?.toIntOrNull() ?: 0,
            masteryLevels =
                data.creation["niveis_chave_pericia"]?.trim()
                    ?.takeIf { it.isNotEmpty() && it != "-" }?.split(",")?.map { it.trim().toInt() } ?: emptyList(),
            spellSlotLevels =
                data.creation["niveis_slot_magia"]?.trim()
                    ?.takeIf { it.isNotEmpty() && it != "-" }?.split(",")?.map { it.trim().toInt() } ?: emptyList(),
            statPointsPerLevel = data.creation["pontos_status_por_nivel"]?.toIntOrNull() ?: 0,
            abilityPointsPerLevel = data.creation["pontos_habilidade_por_nivel"]?.toIntOrNull() ?: 0,
            levelResourceRewards = parseResourceRewards(data.creation["recompensa_recurso"]),
            statCaps = data.stats.associate { it.id to it.max },
            statDefaults = data.stats.associate { it.id to it.default },
            skillCaps = data.traits.filter { it.type == "pericia" }.associate { it.id to MAX_SKILL_POINTS },
            skillRequirements =
                data.traits.filter { it.type == "pericia" }.associate { skill ->
                    skill.id to
                        AdvancementRequirement(
                            minStats = skill.requires.filterIsInstance<Requirement.MinStat>().associate { it.stat to it.min },
                            requiredTraits = skill.requires.filterIsInstance<Requirement.HasTrait>().map { it.traitId }.toSet(),
                            excludedTraits = skill.requires.filterIsInstance<Requirement.Excludes>().map { it.traitId }.toSet(),
                        )
                },
        )

    /** Recursos derivados dos atributos (vida, mana...), cheios. */
    fun resourcesFor(
        stats: Map<String, Int>,
        traitIds: Collection<String> = emptyList(),
    ): Map<String, Resource> =
        data.resources.associate { r ->
            val v = r.stat?.let { stats[it] ?: statById[it]?.default ?: 0 } ?: 0
            // M-01.6: traços com escopo `max_<recurso>` somam ao máximo derivado.
            val max = maxOf(0, r.base + r.mult * v + traitBonus(traitIds, "max_" + r.id))
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
        skills: Map<String, Int> = emptyMap(),
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
            if (t == null) {
                errors += "traço desconhecido: $tid"
                continue
            }
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
            spent =
                (spent.toLong() + value.coerceAtLeast(0).toLong())
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
    fun buildFicha(
        id: String,
        name: String,
        concept: String,
        stats: Map<String, Int>,
        traits: List<String>,
        skills: Map<String, Int> = emptyMap(),
    ): FichaBuild {
        val report = validateCharacter(stats, traits, skills = skills)
        if (!report.valid) return FichaBuild.Invalid(report.errors)
        val full = data.stats.associate { it.id to (stats[it.id] ?: it.default) }
        val ficha =
            Ficha(
                id = id,
                name = name,
                system = SystemRef(manifest.system, manifest.edition),
                concept = concept,
                stats = full,
                resources = resourcesFor(full, traits),
                traits = traits,
                skills = skills,
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
    private fun poolDie(
        base: Int,
        difficulty: Int,
    ): Int {
        val sorted = data.difficulties.map { it.value }.distinct().sorted()
        val i = sorted.indexOf(difficulty)
        val ref = defaultDifficulty()?.let { sorted.indexOf(it) } ?: -1
        val at = REAL_DICE.indexOf(base)
        if (i < 0 || ref < 0 || at < 0) return base
        return REAL_DICE[(at + i - ref).coerceIn(0, REAL_DICE.lastIndex)]
    }

    /** Monta o teste para o motor. O resultado é decidido pelo motor; aqui só se descreve o teste. */
    fun check(
        checkId: String,
        stats: Map<String, Int>,
        difficulty: Int,
        skills: Map<String, Int> = emptyMap(),
    ): CheckSpec {
        val c = requireNotNull(checkById[checkId]) { "teste inexistente: $checkId" }
        val base = c.skill?.let { skills[it] ?: 0 } ?: c.stat?.let { (stats[it] ?: statById[it]?.default ?: 0) * c.mult } ?: 0
        // Teste de pontos não tem CD: a dificuldade escolhida troca o tamanho do dado (fácil d4, média d6, difícil d8...).
        val sides = if (c.comparison == Comparison.POINTS) poolDie(c.diceSides, difficulty) else c.diceSides
        return CheckSpec(
            label = c.name, diceCount = c.diceCount, diceSides = sides, modifier = base, difficulty = difficulty,
            comparison = c.comparison, partialMargin = c.partialMargin,
            criticalSuccessRange = c.critSuccess, criticalFailureRange = c.critFailure,
            stat = c.stat, checkId = c.id, fortuneMode = c.fortuneMode, explodeOnMax = c.explodeOnMax,
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
        spec: CheckSpec,
        conditions: Collection<String>,
        traits: Collection<String> = emptyList(),
        skill: String? = null,
        additionalBonuses: Collection<ExternalBonus> = emptyList(),
    ): CheckSpec {
        val stat: String? = spec.stat
        val uniqueExternalBonuses = distinctExternalBonuses(additionalBonuses)
        val shift =
            conditions.sumOf { id ->
                val e = data.conditions.firstOrNull { it.id == id }?.effect
                val scope: Set<String>? = e?.stats
                if (e != null && (scope == null || (stat != null && stat in scope))) e.dice else 0
            }
        // Teste de pontos: Ganho/Perda não mexem nos dados (é sempre 1 dado), mudam os pontos em +1/-1.
        val pool = spec.comparison == Comparison.POINTS
        val count = if (pool) spec.diceCount else (spec.diceCount + shift).coerceIn(1, maxOf(spec.diceCount, MAX_CONDITION_DICE))
        val checkId: String? = spec.checkId
        val bonus =
            traits.distinct().sumOf { tid ->
                traitById[tid]?.bonuses.orEmpty()
                    .filter { b -> b.scope == checkId || (stat != null && b.scope == stat) || (skill != null && b.scope == skill) }
                    .map { it.scope }.distinct().sumOf { sc -> traitById[tid]!!.bonuses.filter { it.scope == sc }.sumOf { it.bonus } }
            }
        // Efeitos tipados externos (ex.: raça/classe/maldição) usam o mesmo cálculo por escopo,
        // mas permanecem separados dos traços canônicos para evitar inserir cópias em Ficha.traits.
        val externalBonus =
            uniqueExternalBonuses
                .filter { b -> b.scope == checkId || (stat != null && b.scope == stat) || (skill != null && b.scope == skill) }
                .map { it.scope }.distinct()
                .sumOf { scope -> uniqueExternalBonuses.filter { it.scope == scope }.sumOf { it.bonus } }
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
        val matchingExternal =
            uniqueExternalBonuses.filter { b ->
                b.bonus != 0 && (b.scope == checkId || (stat != null && b.scope == stat) || (skill != null && b.scope == skill))
            }
        if (matchingExternal.isNotEmpty()) {
            val effectDetails =
                matchingExternal.joinToString(", ") { effect ->
                    "${effect.source} (${effect.scope} ${if (effect.bonus > 0) "+" else ""}${effect.bonus})"
                }
            label += " · efeitos: $effectDetails"
        }
        if (count == spec.diceCount && totalBonus == 0 && pointShift == 0 && matchingExternal.isEmpty()) return spec
        return spec.copy(diceCount = count, modifier = spec.modifier + totalBonus + pointShift, label = label)
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
    fun checkChoices(
        stats: Map<String, Int>,
        difficulty: Int? = defaultDifficulty(),
        skills: Map<String, Int> = emptyMap(),
    ): List<CheckChoice> {
        val d = difficulty ?: return emptyList()
        return data.checks.map { c ->
            val using = c.skill ?: c.stat?.let { statById[it]?.name ?: it }
            CheckChoice(c.id, c.name + (using?.let { " (usa $it)" } ?: ""), check(c.id, stats, d, skills))
        }
    }

    /** Busca por trecho de regra (4.2.3): só o necessário vai para a IA. Ignora acentos e maiúsculas. */
    fun search(
        query: String,
        limit: Int = 5,
    ): List<RuleSnippet> {
        val tokens = norm(query).split(Regex("[^a-z0-9]+")).filter { it.length >= 3 }.distinct()
        if (tokens.isEmpty()) return emptyList()

        fun score(
            title: String,
            text: String,
        ): Int {
            val t = norm(title)
            val x = norm(text)
            return tokens.sumOf { (if (t.contains(it)) 2 else 0) + (if (x.contains(it)) 1 else 0) }
        }
        val all = mutableListOf<RuleSnippet>()
        data.traits.forEach { all += RuleSnippet("traco", it.id, it.name, it.description, score(it.name, it.description)) }
        data.conditions.forEach {
            val mechanics =
                buildString {
                    append(it.description)
                    it.effect?.let { effect -> append(" Efeito nos testes: ").append(if (effect.dice > 0) "Ganho" else "Perda") }
                    it.durationRounds?.let { rounds -> append(" Duração: ").append(rounds).append(" rodadas.") }
                    if (it.damagePerRound > 0) {
                        append(
                            " Dano por rodada: ",
                        ).append(it.damagePerRound).append(" ").append(it.damageResource).append(".")
                    }
                }
            all += RuleSnippet("condicao", it.id, it.name, mechanics, score(it.name, mechanics))
        }
        data.stats.forEach {
            all += RuleSnippet("atributo", it.id, it.name, "Limites ${it.min}..${it.max}; padrão ${it.default}.", score(it.name, it.id))
        }
        data.resources.forEach {
            val text = "Recurso ${it.name}; base ${it.base}; atributo ${it.stat ?: "nenhum"}; multiplicador ${it.mult}."
            all += RuleSnippet("recurso", it.id, it.name, text, score(it.name, text))
        }
        data.items.forEach {
            val mechanics =
                it.description +
                    if (it.effects.isEmpty()) {
                        ""
                    } else {
                        " Efeitos: " + it.effects.joinToString(", ") { e -> "${e.scope} ${if (e.bonus >= 0) "+" else ""}${e.bonus}" }
                    }
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

    private fun norm(s: String): String = Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").lowercase()
}

/** Confere a coerência interna de um pacote já resolvido. Lista vazia = pacote válido. */
object PackageValidator {
    fun validate(d: PackageData): List<String> {
        val p = mutableListOf<String>()
        val stats = d.stats.map { it.id }.toSet()
        val traits = d.traits.map { it.id }.toSet()
        val idGroups =
            listOf(
                "atributo" to d.stats.map { it.id },
                "recurso" to d.resources.map { it.id },
                "traço" to d.traits.map { it.id },
                "teste" to d.checks.map { it.id },
                "dificuldade" to d.difficulties.map { it.id },
                "condição" to d.conditions.map { it.id },
                "perfil de dano" to d.damage.map { it.id },
                "item" to d.items.map { it.id },
                "poder" to d.powers.map { it.id },
            )
        idGroups.forEach { (kind, ids) ->
            ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.forEach { id ->
                p += "$kind duplicado: $id"
            }
            ids.filter { it.isBlank() }.forEach { p += "$kind com id vazio" }
        }
        if (d.stats.isEmpty()) p += "pacote sem atributos"
        d.resources.filter { it.stat != null && it.stat !in stats }.forEach { p += "recurso ${it.id}: atributo inexistente ${it.stat}" }
        val globalLuckResource = d.creation["recurso_sorte"]?.trim()?.takeIf { it.isNotEmpty() && it != "-" }
        val globalLuckCostRaw = d.creation["custo_sorte"]?.trim()?.takeIf { it.isNotEmpty() && it != "-" }
        val globalLuckKindsRaw = d.creation["formas_sorte"]?.trim()?.takeIf { it.isNotEmpty() && it != "-" }
        if (listOf(globalLuckResource, globalLuckCostRaw, globalLuckKindsRaw).count { it != null } !in setOf(0, 3)) {
            p += "criacao.txt: recurso_sorte, custo_sorte e formas_sorte devem ser declarados juntos"
        }
        if (globalLuckResource != null && d.resources.none { it.id == globalLuckResource }) {
            p += "criacao.txt: recurso_sorte referencia recurso inexistente $globalLuckResource"
        }
        globalLuckCostRaw?.let { raw ->
            if (raw.toIntOrNull()?.let { it > 0 } != true) p += "criacao.txt: custo_sorte deve ser inteiro positivo"
        }
        globalLuckKindsRaw?.let { raw ->
            val kinds = raw.split('+').map { it.trim().uppercase() }
            val allowed = dev.rpgsolo.engine.LuckKind.values().map { it.name }.toSet()
            if (kinds.any { it !in allowed } || kinds.distinct().size != kinds.size) {
                p += "criacao.txt: formas_sorte inválidas (use REROLL, ADD_FACE ou REROLL+ADD_FACE, sem repetição)"
            }
        }
        val levelRewards =
            try {
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
            if (check.luckResource != null && d.resources.none { it.id == check.luckResource }) {
                p += "teste ${check.id}: recurso de sorte inexistente ${check.luckResource}"
            }
            if (check.luckResource != null && check.luckCost <= 0) {
                p += "teste ${check.id}: custo de sorte deve ser positivo"
            }
            if ((check.luckResource == null) != check.luckKinds.isEmpty() || (check.luckResource == null && check.luckCost != 0)) {
                p += "teste ${check.id}: recurso, custo e formas de sorte devem ser declarados juntos"
            }
        }
        d.conditions.forEach {
                c ->
            c.effect?.stats?.filter { it !in stats }?.forEach { p += "condição ${c.id}: atributo inexistente no efeito $it" }
        }
        d.conditions.forEach { c ->
            if (c.durationRounds != null && c.durationRounds <= 0) p += "condição ${c.id}: duração deve ser positiva"
            if (c.damageResource.isBlank()) p += "condição ${c.id}: recurso de dano vazio"
            if (c.damagePerRound < 0) p += "condição ${c.id}: dano por rodada negativo"
            if (c.damagePerRound > 0 && d.resources.none { it.id == c.damageResource }) {
                p += "condição ${c.id}: recurso de dano inexistente ${c.damageResource}"
            }
        }
        d.powers.forEach { power ->
            val trait = d.traits.firstOrNull { it.id == power.traitId }
            if (trait == null) {
                p += "poder ${power.id}: traço inexistente ${power.traitId}"
            } else if (trait.type != "poder") {
                p += "poder ${power.id}: traço ${trait.id} não é do tipo poder"
            }
            if (d.resources.none { it.id == power.resource }) p += "poder ${power.id}: recurso inexistente ${power.resource}"
            if (power.cost < 0) p += "poder ${power.id}: custo negativo"
            if (power.checkId != null && d.checks.none { it.id == power.checkId }) p += "poder ${power.id}: teste inexistente ${power.checkId}"
            when (power.effect) {
                PowerEffectKind.HEAL -> {
                    if (power.target != PowerTarget.SELF) p += "poder ${power.id}: cura só pode ter alvo self"
                    if ((power.param.toIntOrNull() ?: 0) <= 0) p += "poder ${power.id}: quantidade de cura inválida"
                    if (d.manifest.deathResource == null) p += "poder ${power.id}: cura exige recurso de saúde (morte) no pacote"
                }
                PowerEffectKind.DAMAGE -> {
                    if (power.target != PowerTarget.ENEMY) p += "poder ${power.id}: dano só pode ter alvo inimigo"
                    if (d.damage.none { it.id == power.param }) p += "poder ${power.id}: perfil de dano inexistente ${power.param}"
                }
                PowerEffectKind.APPLY_CONDITION -> {
                    if (power.target != PowerTarget.ENEMY) p += "poder ${power.id}: condicao só pode ter alvo inimigo"
                    if (d.conditions.none { it.id == power.param }) p += "poder ${power.id}: condição inexistente ${power.param}"
                }
                PowerEffectKind.SHIELD -> {
                    if (power.target != PowerTarget.SELF) p += "poder ${power.id}: escudo só pode ter alvo self"
                    if ((power.param.toIntOrNull() ?: 0) <= 0) p += "poder ${power.id}: quantidade de escudo inválida"
                }
                PowerEffectKind.REMOVE_CONDITION -> {
                    if (power.target != PowerTarget.SELF) p += "poder ${power.id}: remover-condicao só pode ter alvo self"
                    if (d.conditions.none { it.id == power.param }) p += "poder ${power.id}: condição inexistente ${power.param}"
                }
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
        val validTraitScopes =
            stats +
                d.checks.map {
                    it.id
                } + traits + setOf("ataque", "dano", "defesa", "resistencia_dano") +
                d.resources.map {
                    "max_" + it.id
                } + d.conditions.map { "imune_" + it.id }
        d.traits.forEach { trait ->
            trait.bonuses.filter {
                it.scope !in validTraitScopes
            }.forEach { p += "traço " + trait.id + ": escopo de bônus sem regra mecânica: " + it.scope }
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
            listOf(combatAttack, combatDefense, combatInitiative).any { it == null }
        ) {
            p += "criacao.txt: perfil de combate incompleto; ataque, defesa e iniciativa devem ser definidos juntos"
        }
        listOf(
            "combate_atributo_ataque" to combatAttack,
            "combate_atributo_defesa" to combatDefense,
            "combate_atributo_iniciativa" to combatInitiative,
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
            "combate_atributo_iniciativa" to combatInitiative,
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
        val itemActions = setOf("cura", "remover-condicao", "aplicar-condicao")
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
                "acao" -> if (value !in itemActions) p += "criacao.txt: ação de item não suportada: $value"
                "recurso" -> if (d.resources.none { it.id == value }) p += "criacao.txt: recurso de item inexistente: $value"
                "quantidade" -> if ((value.toIntOrNull() ?: 0) <= 0) p += "criacao.txt: quantidade de item inválida: $value"
                "condicao" -> if (d.conditions.none { it.id == value }) p += "criacao.txt: condição de item inexistente: $value"
                "duracao" -> if ((value.toIntOrNull() ?: 0) !in 1..100) p += "criacao.txt: duração de item inválida (1 a 100 rodadas): $value"
                else -> p += "criacao.txt: propriedade de item desconhecida: $key"
            }
        }
        d.items.filter { it.kind == "consumivel" }.forEach { item ->
            val prefix = "item_${item.id}_"
            val action = d.creation[prefix + "acao"]
            val others = listOf("recurso", "quantidade", "condicao", "duracao").filter { d.creation.containsKey(prefix + it) }
            if (action == null && others.isNotEmpty()) p += "criacao.txt: consumível ${item.id} define ${others.joinToString("/")} sem ação"
            when (action) {
                "cura" -> {
                    if (!d.creation.containsKey(prefix + "recurso")) p += "criacao.txt: consumível ${item.id} sem recurso"
                    if (!d.creation.containsKey(prefix + "quantidade")) p += "criacao.txt: consumível ${item.id} sem quantidade"
                }
                "remover-condicao", "aplicar-condicao" ->
                    if (!d.creation.containsKey(prefix + "condicao")) p += "criacao.txt: consumível ${item.id} sem condição"
            }
        }
        ItemQuality.entries.forEach { q ->
            d.creation["qualidade_${q.id}_percent"]?.let { raw ->
                if ((raw.toIntOrNull() ?: 0) !in 100..1000) p += "criacao.txt: qualidade_${q.id}_percent deve ficar entre 100 e 1000"
            }
        }
        val percents = ItemQuality.entries.map { qualityPercent(it) }
        if (percents.zipWithNext().any { (x, y) -> y < x }) p += "criacao.txt: os percentuais de qualidade devem ser crescentes (normal ≤ boa ≤ rara ≤ lendária)"
        d.creation["renascimento_percentual"]?.let { if ((it.toIntOrNull() ?: 0) !in 1..100) p += "criacao.txt: renascimento_percentual deve ficar entre 1 e 100" }
        d.creation["itens_renascimento"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() && it != "-" }?.forEach { id ->
            if (d.items.none { it.id == id }) p += "criacao.txt: itens_renascimento referencia item inexistente: $id"
        }
        d.items.filter { it.price != null && it.price !in 1..1_000_000 }
            .forEach { p += "item ${it.id}: preço fora de 1..1000000" }
        val currencyId = d.creation["moeda_item"]
        if (currencyId != null) {
            val currency = d.items.firstOrNull { it.id == currencyId }
            if (currency == null) {
                p += "criacao.txt: moeda_item referencia item inexistente: $currencyId"
            } else {
                if (currency.kind != "narrativo") p += "criacao.txt: moeda_item deve ser item narrativo: $currencyId"
                if (currency.price != null) p += "item $currencyId: a moeda não pode ter preço"
            }
        }
        d.creation["moeda_ouro"]?.let { goldId ->
            val gold = d.items.firstOrNull { it.id == goldId }
            when {
                currencyId == null -> p += "criacao.txt: moeda_ouro exige 'moeda_item'"
                goldId == currencyId -> p += "criacao.txt: moeda_ouro deve ser diferente de moeda_item"
                gold == null -> p += "criacao.txt: moeda_ouro referencia item inexistente: $goldId"
                else -> {
                    if (gold.kind != "narrativo") p += "criacao.txt: moeda_ouro deve ser item narrativo: $goldId"
                    if (gold.price != null) p += "item $goldId: a moeda não pode ter preço"
                }
            }
        }
        val spellKeys = d.creation.keys.filter { it.startsWith("magia_") && it.endsWith("_preco") }
        spellKeys.forEach { key ->
            val id = key.removePrefix("magia_").removeSuffix("_preco")
            if (!Regex("[a-z0-9][a-z0-9_-]*").matches(id)) p += "criacao.txt: id de magia inválido: '$id'"
            if (d.creation[key]?.toIntOrNull()?.let { it in 1..1_000_000 } != true) p += "criacao.txt: $key deve ser inteiro de 1 a 1000000"
        }
        d.creation.keys.filter { it.startsWith("magia_") && it.endsWith("_poder") }.forEach { key ->
            val id = key.removePrefix("magia_").removeSuffix("_poder")
            if ("magia_${id}_preco" !in d.creation) p += "criacao.txt: $key sem 'magia_${id}_preco'"
            if (d.powers.none { it.id == d.creation[key] }) p += "criacao.txt: $key referencia poder inexistente '${d.creation[key]}'"
        }
        if (spellKeys.isNotEmpty() && currencyId == null) p += "criacao.txt: magias à venda exigem 'moeda_item'"
        if (spellKeys.isNotEmpty() && d.creation["niveis_slot_magia"].isNullOrBlank()) p += "criacao.txt: magias à venda exigem 'niveis_slot_magia'"
        if (currencyId == null && d.items.any { it.price != null }) p += "criacao.txt: itens com preço exigem 'moeda_item'"
        val pontos = d.creation["pontos"]?.toIntOrNull()
        if (pontos == null || pontos < 0) p += "criacao.txt: 'pontos' ausente ou inválido"
        d.creation["limite_desvantagens"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'limite_desvantagens' inválido" }
        d.creation["custo_atributo"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'custo_atributo' inválido" }
        d.creation["equilibrio"]?.let { raw ->
            val parts = raw.split(",").map { it.trim() }
            val nums = parts.mapNotNull { it.toIntOrNull() }
            if (nums.size != parts.size || nums.isEmpty() || nums.any { it < 1 } || nums.zipWithNext().any { (a, b) -> b < a }) {
                p += "criacao.txt: 'equilibrio' inválido (use números crescentes, ex.: 1,3,5)"
            }
        }
        d.creation["equilibrio_apos"]?.let {
            if (it != "dobrar" && it != "manter") p += "criacao.txt: 'equilibrio_apos' inválido (dobrar ou manter)"
        }
        if (d.creation.containsKey("equilibrio_apos") && !d.creation.containsKey("equilibrio")) p += "criacao.txt: 'equilibrio_apos' sem 'equilibrio'"
        d.creation["minimo_opcoes"]?.let { if ((it.toIntOrNull() ?: -1) < 0) p += "criacao.txt: 'minimo_opcoes' inválido" }
        val everyId =
            stats + traits + d.resources.map { it.id } + d.checks.map { it.id } +
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
            if (a == null) {
                p += "opções: arquétipo inexistente ${o.archetype}"
                continue
            }
            val chosen = o.advantages + o.disadvantages
            chosen.groupBy { it }.filter { it.value.size > 1 }.keys.forEach { p += "opções de ${o.archetype}: traço repetido $it" }
            for (id in o.advantages) {
                val t = traitById[id]
                if (t == null) {
                    p += "opções de ${o.archetype}: traço inexistente $id"
                    continue
                }
                if (t.type !in ADVANTAGE_TYPES) p += "opções de ${o.archetype}: $id não é vantagem (é ${t.type})"
            }
            for (id in o.disadvantages) {
                val t = traitById[id]
                if (t == null) {
                    p += "opções de ${o.archetype}: traço inexistente $id"
                    continue
                }
                if (t.type != "desvantagem") p += "opções de ${o.archetype}: $id não é desvantagem (é ${t.type})"
            }
            // Todo item do cardápio precisa ser escolhível por quem tem os atributos do arquétipo.
            for (id in chosen) {
                val t = traitById[id] ?: continue
                for (r in t.requires) when (r) {
                    is Requirement.MinStat ->
                        if ((a.stats[r.stat] ?: d.stats.firstOrNull { it.id == r.stat }?.default ?: 0) < r.min) {
                            p += "opções de ${o.archetype}: $id exige ${r.stat} >= ${r.min}, mais do que o arquétipo tem"
                        }
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

/** Limite de dados de um teste ao somar Ganhos. */
private const val MAX_CONDITION_DICE = 3
