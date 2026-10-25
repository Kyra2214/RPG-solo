package dev.rpgsolo.engine

/**
 * Chance de crítico (MU-02.2, N-07): d10 rolado em silêncio; crítico quando o dado >= [threshold].
 * O limiar base é 10 (10%); cada ponto de Sorte (até 6, teto de 70% pelos atributos) e de bônus de crítico (habilidade, item, vantagem) o reduz em 1, até 1 (100%).
 * [roll] é nulo quando a chance é garantida (100%, vinda de habilidade) e a rolagem é pulada.
 */
data class CritCheck(
    val bonus: EffectiveBreakdown,
    val threshold: Int,
    val guaranteed: Boolean,
    val roll: RollResult?,
    val critical: Boolean,
)

/** N-06: valores fixos (sem dado) de Destreza do atacante e Agilidade do defensor. */
data class HitSpec(val dexterity: EffectiveBreakdown, val agility: EffectiveBreakdown)

/** N-06: acerto automático. Chance = 50% + 5% × (Destreza − Agilidade), entre 5% e 95%; d100 em silêncio, acerta com dado ≤ chance. */
data class HitCheck(val dexterity: Int, val agility: Int, val chance: Int, val roll: RollResult, val hit: Boolean)

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
    /** Defesa aplicada exatamente uma vez: dano = max(0, ceil(ataque − 0,65 × defesa)) (N-05). */
    val damage: Int,
    /** N-06: nulo quando o confronto não usa teste de acerto (pacote sem Destreza/Agilidade). */
    val hit: HitCheck? = null,
) {
    val missed: Boolean get() = hit?.hit == false
    val ignoredDefense: Boolean get() = ignore?.success == true
    val attackTotal: Int get() = attackRoll?.total ?: attack.value
    val defenseTotal: Int get() = defenseRoll?.total?.coerceAtLeast(UnifiedCombat.DEFENSE_ROLL_MIN) ?: 0
    val critical: Boolean get() = crit?.critical == true
}

object UnifiedCombat {
    const val CRIT_TARGET = "crit"

    /** N-07/N-15: d10, base 10% (limite 10); cada ponto de Sorte (até [CRIT_MAX_LUCK]) e de bônus `crit` reduz o limite em 1. */
    const val CRIT_BASE_THRESHOLD = 10
    const val CRIT_DIE_SIDES = 10
    const val CRIT_MAX_LUCK = 6

    /** N-34: defesa rolada nunca fica abaixo de 1 (aplicado ao valor rolado, antes da fórmula do dano). */
    const val DEFENSE_ROLL_MIN = 1
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
        hitSpec: HitSpec? = null,
        luck: Int = 0,
        luckBonus: Int = 0,
    ): ClashResult {
        val hitCheck = hitSpec?.let { checkHit(it, dice) }
        if (hitCheck?.hit == false) return ClashResult(attack, defense, null, null, null, null, 0, hitCheck)
        val crit = if (canCrit) checkCrit(critModifiers, guaranteedCrit, dice, luck, luckBonus) else null
        val ignore = ignoreDefense?.let { checkIgnore(it, dice) }
        if (ignore?.success == true) {
            // Ignora defesa (e escudo, quando existir): valor cheio × percentual, sem dado e sem defesa.
            val damage = attack.value * ignoreDefense.percent / 100
            return ClashResult(attack, defense, null, null, crit, ignore, maxOf(0, damage), hitCheck)
        }
        val attackRoll = if (crit?.critical == true) null else attack.expression.roll(dice)
        val defenseRoll = defense.expression.roll(dice)
        val attackTotal = attackRoll?.total ?: attack.value
        return ClashResult(
            attack,
            defense,
            attackRoll,
            defenseRoll,
            crit,
            ignore,
            damageOf(attackTotal, defenseRoll.total.coerceAtLeast(DEFENSE_ROLL_MIN)),
            hitCheck,
        )
    }

    const val HIT_BASE_CHANCE = 50
    const val HIT_PER_POINT = 5

    fun hitChance(
        dexterity: Int,
        agility: Int,
    ): Int = (HIT_BASE_CHANCE + HIT_PER_POINT * (dexterity - agility)).coerceIn(5, 95)

    fun checkHit(
        spec: HitSpec,
        dice: Dice,
    ): HitCheck {
        val chance = hitChance(spec.dexterity.value, spec.agility.value)
        val roll = dice.roll(1, 100)
        return HitCheck(spec.dexterity.value, spec.agility.value, chance, roll, roll.total <= chance)
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
        luck: Int = 0,
        luckBonus: Int = 0,
    ): CritCheck {
        val bonus = ModifierAggregator.aggregate(CRIT_TARGET, 0, critModifiers)
        val threshold =
            (CRIT_BASE_THRESHOLD - luck.coerceIn(0, CRIT_MAX_LUCK) - luckBonus.coerceAtLeast(0) - bonus.value).coerceIn(
                1,
                CRIT_BASE_THRESHOLD,
            )
        if (guaranteed) return CritCheck(bonus, threshold, true, null, true)
        val roll = dice.roll(1, CRIT_DIE_SIDES)
        return CritCheck(bonus, threshold, false, roll, roll.total >= threshold)
    }

    /** N-05: defesa conta [DEFENSE_PERCENT]%. */
    const val DEFENSE_PERCENT = 65

    /**
     * Única fórmula de dano (N-05): ataque − 0,65 × defesa, arredondado para cima, mínimo 0 (sem dano mínimo de 1).
     * A defesa rolada nunca é negativa. ceil(a − 0,65 d) = a − floor(65 d / 100).
     */
    fun damageOf(
        attackTotal: Int,
        defenseTotal: Int,
    ): Int = maxOf(0, attackTotal - defenseTotal.coerceAtLeast(0) * DEFENSE_PERCENT / 100)
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
    val hit: HitSpec? = null,
    val luck: Int = 0,
    /** N-15: Sorte de itens e encantamentos; passa do teto de [UnifiedCombat.CRIT_MAX_LUCK] (+10% por ponto, até 100%). */
    val luckBonus: Int = 0,
    /** N-09: redução fixa do dano final (resistência a dano/elemental do alvo). */
    val damageReduction: Int = 0,
    /** N-16: percentual do dano pelo elemento ([ElementChart.percent]); 100 = normal, 0 = sem dano. Aplicado antes da redução. */
    val elementPercent: Int = 100,
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
