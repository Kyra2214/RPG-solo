package dev.rpgsolo.engine

/**
 * Chance de crítico (MU-02.2): d6 rolado em silêncio; crítico quando o dado >= [threshold].
 * O limiar base é 6 e cada ponto de bônus de crítico (habilidade, item, vantagem) o reduz em 1, até 1.
 * [roll] é nulo quando a chance é garantida (100%, vinda de habilidade) e a rolagem é pulada.
 */
data class CritCheck(
    val bonus: EffectiveBreakdown,
    val threshold: Int,
    val guaranteed: Boolean,
    val roll: RollResult?,
    val critical: Boolean,
)

/** Habilidade que pode ignorar defesa (MU-02.3). [percent] é o dano em % do valor efetivo cheio. */
data class IgnoreDefenseSpec(
    val modifiers: List<Modifier> = emptyList(),
    val guaranteed: Boolean = false,
    val cooldownRounds: Int = 0,
    val percent: Int = 150,
) {
    init {
        require(cooldownRounds >= 0) { "recarga negativa" }
        require(percent > 0) { "percentual inválido" }
    }
}

/** d10 em silêncio: acerta quando o dado >= [threshold] (base 10, reduzido por bônus `ignoreDef`); nulo em [roll] se garantida. */
data class IgnoreCheck(
    val bonus: EffectiveBreakdown,
    val threshold: Int,
    val guaranteed: Boolean,
    val roll: RollResult?,
    val success: Boolean,
    val cooldownRounds: Int,
)

/**
 * MU-02.1/02.2/02.3: confronto ataque × defesa do motor unificado. Vale para qualquer entidade (jogador, NPC,
 * aliado ou chefe): quem chama só entrega os valores já agregados; a IA nunca fornece número.
 */
data class ClashResult(
    val attack: EffectiveBreakdown,
    val defense: EffectiveBreakdown,
    /** Nulo no crítico: o dano é o valor efetivo cheio, sem rolar dado. */
    val attackRoll: RollResult?,
    /** Nulo quando a defesa é ignorada. */
    val defenseRoll: RollResult?,
    val crit: CritCheck?,
    val ignore: IgnoreCheck? = null,
    /** Defesa aplicada exatamente uma vez: dano = max(0, ataque − defesa). */
    val damage: Int,
) {
    val ignoredDefense: Boolean get() = ignore?.success == true
    val attackTotal: Int get() = attackRoll?.total ?: attack.value
    val defenseTotal: Int get() = defenseRoll?.total ?: 0
    val critical: Boolean get() = crit?.critical == true
}

object UnifiedCombat {
    const val CRIT_TARGET = "crit"
    const val CRIT_BASE_THRESHOLD = 6
    const val CRIT_DIE_SIDES = 6
    const val IGNORE_TARGET = "ignoreDef"
    const val IGNORE_BASE_THRESHOLD = 10
    const val IGNORE_DIE_SIDES = 10

    /**
     * Ordem das rolagens: crítico (se não garantido), ignorar defesa (se a habilidade tiver; d10), ataque (se não crítico), defesa (se não ignorada).
     * Crítico: o ataque vale o valor efetivo cheio (ex.: 10 → 10), sem dado. ASSUMINDO: a defesa rolada
     * ainda é subtraída. [critModifiers] são os modificadores de qualquer origem com target [CRIT_TARGET].
     */
    fun resolve(
        attack: EffectiveBreakdown,
        defense: EffectiveBreakdown,
        dice: Dice,
        critModifiers: List<Modifier> = emptyList(),
        guaranteedCrit: Boolean = false,
        canCrit: Boolean = true,
        ignoreDefense: IgnoreDefenseSpec? = null,
    ): ClashResult {
        val crit = if (canCrit) checkCrit(critModifiers, guaranteedCrit, dice) else null
        val ignore = ignoreDefense?.let { checkIgnore(it, dice) }
        if (ignore?.success == true) {
            // Ignora defesa (e escudo, quando existir): valor cheio × percentual, sem dado e sem defesa.
            val damage = attack.value * ignoreDefense.percent / 100
            return ClashResult(attack, defense, null, null, crit, ignore, maxOf(0, damage))
        }
        val attackRoll = if (crit?.critical == true) null else attack.expression.roll(dice)
        val defenseRoll = defense.expression.roll(dice)
        val attackTotal = attackRoll?.total ?: attack.value
        return ClashResult(attack, defense, attackRoll, defenseRoll, crit, ignore, damageOf(attackTotal, defenseRoll.total))
    }

    fun checkIgnore(
        spec: IgnoreDefenseSpec,
        dice: Dice,
    ): IgnoreCheck {
        val bonus = ModifierAggregator.aggregate(IGNORE_TARGET, 0, spec.modifiers)
        val threshold = (IGNORE_BASE_THRESHOLD - bonus.value).coerceIn(1, IGNORE_BASE_THRESHOLD)
        if (spec.guaranteed) return IgnoreCheck(bonus, threshold, true, null, true, spec.cooldownRounds)
        val roll = dice.roll(1, IGNORE_DIE_SIDES)
        return IgnoreCheck(bonus, threshold, false, roll, roll.total >= threshold, spec.cooldownRounds)
    }

    fun checkCrit(
        critModifiers: List<Modifier>,
        guaranteed: Boolean,
        dice: Dice,
    ): CritCheck {
        val bonus = ModifierAggregator.aggregate(CRIT_TARGET, 0, critModifiers)
        val threshold = (CRIT_BASE_THRESHOLD - bonus.value).coerceIn(1, CRIT_BASE_THRESHOLD)
        if (guaranteed) return CritCheck(bonus, threshold, true, null, true)
        val roll = dice.roll(1, CRIT_DIE_SIDES)
        return CritCheck(bonus, threshold, false, roll, roll.total >= threshold)
    }

    /** Única fórmula de dano; o resultado nunca é negativo e não há dano mínimo de 1. */
    fun damageOf(
        attackTotal: Int,
        defenseTotal: Int,
    ): Int = maxOf(0, attackTotal - defenseTotal)
}

/** Entrada do confronto unificado. Os breakdowns vêm do motor/pacote (agregador), nunca da narração da IA. */
data class UnifiedAttackSpec(
    val attackerId: String,
    val targetId: String,
    val attack: EffectiveBreakdown,
    val defense: EffectiveBreakdown,
    val critModifiers: List<Modifier> = emptyList(),
    val guaranteedCrit: Boolean = false,
    val canCrit: Boolean = true,
    val ignoreDefense: IgnoreDefenseSpec? = null,
    val damageResource: String = "hp",
)

sealed interface UnifiedAttackResult {
    data class Resolved(val clash: ClashResult, val events: List<Event>) : UnifiedAttackResult

    data class Rejected(val reason: String) : UnifiedAttackResult
}

/**
 * MU-03.2: fonte de modificadores (traço, perícia, habilidade, item equipado, buff, bênção, condição).
 * Recebe a entidade e a característica e devolve modificadores com ID estável de origem. Não há
 * versão por tipo de entidade: jogador, NPC, aliado e chefe usam as mesmas fontes e o mesmo cálculo.
 */
fun interface ModifierProvider {
    fun modifiers(
        state: GameState,
        entity: Entity,
        target: String,
    ): List<Modifier>
}

object CombatantValues {
    /** Valor-base = `entity.stats[baseStat]` (0 se ausente) + todos os modificadores das fontes. Não ramifica por [EntityKind]. */
    fun breakdown(
        state: GameState,
        entityId: String,
        target: String,
        providers: List<ModifierProvider>,
        baseStat: String = target,
    ): EffectiveBreakdown? {
        val entity = state.entities[entityId] ?: return null
        val modifiers = providers.flatMap { it.modifiers(state, entity, target) }
        return ModifierAggregator.aggregate(target, entity.stats[baseStat] ?: 0, modifiers)
    }

    fun modifiers(
        state: GameState,
        entityId: String,
        target: String,
        providers: List<ModifierProvider>,
    ): List<Modifier> {
        val entity = state.entities[entityId] ?: return emptyList()
        return providers.flatMap { it.modifiers(state, entity, target) }.filter { it.target == target }
    }
}
