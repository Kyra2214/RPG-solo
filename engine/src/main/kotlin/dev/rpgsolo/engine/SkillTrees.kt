package dev.rpgsolo.engine

/**
 * Domain primitives for the user-authored skill-tree system.
 *
 * This layer deliberately does not calculate attack damage: combat remains owned by
 * UnifiedCombat and the active rule package. Skill effects contribute typed modifiers
 * or explicit actions to those existing resolvers.
 */
enum class SkillCategory { OFFENSE, DEFENSE, BUFF, SUPPORT, PASSIVE, UTILITY, CONTROL }
enum class ElementPath { WATER, FIRE, WIND, EARTH, METAL, YIN, YANG, NONE }
enum class SkillOrigin { RACE, SUBRACE, CLASS, PROFESSION, HYBRID, CURSED_CLASS, SHARED, CURSE }
enum class SkillEffectRecipient { SELF, TARGET }
enum class SkillActionKind { MODIFIER, REVIVE, POWER_STRIKE, MULTI_HIT }

data class SkillEffectSpec(
    val id: String,
    val target: String,
    val baseValue: Int,
    val perLevel: Int = 0,
    val stackingGroup: String = id,
    val durationRounds: Int? = null,
    val conditionId: String? = null,
    val recipient: SkillEffectRecipient = SkillEffectRecipient.SELF
) {
    init {
        require(id.isNotBlank()) { "efeito de habilidade exige id" }
        require(target.isNotBlank()) { "efeito de habilidade exige alvo" }
        require(stackingGroup.isNotBlank()) { "efeito de habilidade exige grupo de acúmulo" }
        require(durationRounds == null || durationRounds > 0) { "duração de habilidade inválida" }
        require(conditionId == null || conditionId.isNotBlank()) { "condição de habilidade inválida" }
    }

    /** Sem teto de nível: cada efeito usa uma curva explícita e falha em vez de estourar Int. */
    fun valueAt(level: Int): Int {
        require(level >= 1) { "nível de habilidade deve ser positivo" }
        val value = baseValue.toLong() + perLevel.toLong() * (level - 1L)
        require(value in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) { "efeito excede o intervalo numérico suportado" }
        return value.toInt()
    }
}

data class SkillTreeSkill(
    val id: String,
    val name: String,
    val treeId: String,
    val origin: SkillOrigin,
    val category: SkillCategory,
    val element: ElementPath = ElementPath.NONE,
    val requiredElement: ElementPath? = null,
    /** A habilidade mágica acompanha a afinidade escolhida pelo personagem. */
    val affinityBound: Boolean = false,
    val weaponTags: Set<String> = emptySet(),
    val requiredCharacterLevel: Int = 1,
    val prerequisiteSkillIds: Set<String> = emptySet(),
    val scrollRank: Int? = null,
    val unlockPointCost: Int = 1,
    val actionKind: SkillActionKind = SkillActionKind.MODIFIER,
    val actionCost: Int = 0,
    val actionResource: String? = null,
    val hitCount: Int = 1,
    val effects: List<SkillEffectSpec> = emptyList(),
    /** Só habilidades básicas de raça/sub-raça/Classe de Malditos devem marcar true. */
    val freeBaseSkill: Boolean = false
) {
    init {
        require(id.isNotBlank() && treeId.isNotBlank() && name.isNotBlank()) { "habilidade exige id, árvore e nome" }
        require(requiredCharacterLevel >= 1) { "nível mínimo inválido" }
        require(unlockPointCost >= 0) { "custo de desbloqueio negativo" }
        require(actionCost >= 0) { "custo de ação negativo" }
        require(actionResource == null || actionResource.isNotBlank()) { "recurso de ação inválido" }
        require(hitCount >= 1) { "quantidade de golpes inválida" }
        require(actionKind != SkillActionKind.REVIVE || actionResource != null) { "ressurreição exige recurso de custo" }
        require(actionKind != SkillActionKind.MULTI_HIT || hitCount >= 2) { "multiataque exige ao menos dois golpes" }
        require(scrollRank == null || scrollRank in 1..7) { "posição de pergaminho deve ficar entre 1 e 7" }
        require(!freeBaseSkill || unlockPointCost == 0) { "habilidade básica gratuita não pode cobrar desbloqueio" }
        require(prerequisiteSkillIds.none { it == id }) { "habilidade não pode depender de si mesma" }
    }
}

data class SkillTreeCharacterContext(
    val characterLevel: Int,
    val element: ElementPath,
    val weaponTags: Set<String>,
    val knownSkillLevels: Map<String, Int>
)

data class SkillTreeValidation(val allowed: Boolean, val reasons: List<String>)

/** Economia fixa definida pelo usuário. Todos os preços canônicos são armazenados em prata. */
object SkillTreeEconomy {
    const val SILVER_PER_GOLD = 100L
    const val RESET_PRICE_SILVER = 25_000L
    val scrollPricesSilver: List<Long> = listOf(200L, 2_000L, 10_000L, 15_000L, 20_000L, 30_000L, 50_000L)

    fun scrollPriceSilver(rank: Int): Long {
        require(rank in 1..scrollPricesSilver.size) { "posição de pergaminho deve ficar entre 1 e 7" }
        return scrollPricesSilver[rank - 1]
    }

    fun silverToGold(silver: Long): Long {
        require(silver >= 0) { "quantidade de prata negativa" }
        return silver / SILVER_PER_GOLD
    }
}

/**
 * Shared rules for unlocking and upgrading. There is no fixed skill-level cap.
 * A free base skill starts at level 1; every later rank costs one skill point.
 * The caller must provide a catalog-validated definition before invoking the state command.
 */
object SkillTreeProgression {
    const val POINT_COST_PER_UPGRADE = 1

    fun validate(skill: SkillTreeSkill, context: SkillTreeCharacterContext): SkillTreeValidation {
        val reasons = mutableListOf<String>()
        if (context.characterLevel < skill.requiredCharacterLevel) {
            reasons += "exige nível de personagem ${skill.requiredCharacterLevel}"
        }
        if (skill.affinityBound && context.element == ElementPath.NONE) reasons += "exige escolher uma afinidade elemental"
        if (skill.requiredElement != null && skill.requiredElement != context.element) {
            reasons += "exige afinidade ${skill.requiredElement}"
        }
        if (skill.element != ElementPath.NONE && skill.element != context.element) {
            reasons += "incompatível com afinidade ${context.element}"
        }
        if (skill.weaponTags.isNotEmpty() && skill.weaponTags.intersect(context.weaponTags).isEmpty()) {
            reasons += "arma incompatível"
        }
        skill.prerequisiteSkillIds.forEach { prerequisite ->
            if ((context.knownSkillLevels[prerequisite] ?: 0) < 1) reasons += "exige habilidade $prerequisite"
        }
        return SkillTreeValidation(reasons.isEmpty(), reasons)
    }

    fun nextLevel(currentLevel: Int): Int {
        require(currentLevel >= 0) { "nível atual negativo" }
        require(currentLevel < Int.MAX_VALUE) { "nível máximo numérico atingido" }
        return currentLevel + 1
    }

    fun upgradeCost(currentLevel: Int): Int {
        require(currentLevel >= 1) { "a habilidade precisa ser desbloqueada antes de evoluir" }
        require(currentLevel < Int.MAX_VALUE) { "nível máximo numérico atingido" }
        return POINT_COST_PER_UPGRADE
    }

    /** Efeitos com o mesmo grupo não acumulam: conserva o valor mais forte, sem somar cópias. */
    fun strongestEquivalentEffects(effects: Collection<Pair<SkillEffectSpec, Int>>): List<Pair<SkillEffectSpec, Int>> =
        effects.groupBy { it.first.stackingGroup }
            .values
            .mapNotNull { group -> group.maxWithOrNull(compareBy<Pair<SkillEffectSpec, Int>>({ kotlin.math.abs(it.second) }, { it.second })) }
}

/**
 * Converts acquired PASSIVE tree skills into the existing modifier pipeline.
 * Equivalent effects share a stacking group and only the strongest magnitude survives.
 * Active skills are intentionally not treated as passive; their activation must be explicit.
 */
class SkillTreeModifierProvider(
    private val definitions: Collection<SkillTreeSkill>
) : ModifierProvider {
    override fun modifiers(state: GameState, entity: Entity, target: String): List<Modifier> {
        val definitionsById = definitions.associateBy { it.id }
        val candidates = entity.skillTreeLevels.mapNotNull { (skillId, level) ->
            val skill = definitionsById[skillId]?.takeIf { it.category == SkillCategory.PASSIVE } ?: return@mapNotNull null
            skill.effects.mapNotNull { effect ->
                if (effect.target != target) return@mapNotNull null
                val value = effect.valueAt(level)
                if (value == 0) return@mapNotNull null
                Modifier(
                    sourceId = "skill-tree-passive:${effect.target}:${effect.stackingGroup}",
                    label = skill.name,
                    target = effect.target,
                    value = value
                )
            }
        }.flatten()
        return candidates.groupBy { it.sourceId }
            .values
            .mapNotNull { group -> group.maxWithOrNull(compareBy<Modifier>({ kotlin.math.abs(it.value) }, { it.value })) }
    }
}
