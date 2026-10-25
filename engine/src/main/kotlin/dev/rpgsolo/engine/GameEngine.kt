package dev.rpgsolo.engine

sealed interface ExecResult {
    data class Accepted(val events: List<Event>) : ExecResult

    /** Nada foi alterado. [commandIndex] indica qual comando do lote falhou. */
    data class Rejected(val reason: String, val commandIndex: Int = 0) : ExecResult
}

/**
 * Funil único de mudanças. A IA só PROPÕE comandos; o motor valida, aplica e registra.
 * Lotes são atômicos: se um comando falhar, nenhum é aplicado.
 */
class GameEngine private constructor(
    val seed: Long,
    val config: EngineConfig,
    private val dice: Dice,
    private val log: MutableList<Event>,
    state: GameState,
) {
    constructor(seed: Long, config: EngineConfig = EngineConfig()) :
        this(seed, config, Dice(seed), mutableListOf(), GameState())

    var state: GameState = state
        private set

    val events: List<Event> get() = log.toList()
    val eventCount: Int get() = log.size

    fun execute(command: Command): ExecResult = executeAll(listOf(command))

    fun executeAll(commands: List<Command>): ExecResult {
        var tmp = state
        val produced = mutableListOf<Event>()
        commands.forEachIndexed { i, c ->
            when (val d = decide(tmp, config, c)) {
                is Decision.No -> return ExecResult.Rejected(d.reason, i)
                is Decision.Ok -> {
                    d.events.forEach { tmp = tmp.apply(it) }
                    produced += d.events
                }
            }
        }
        log += produced
        state = tmp
        return ExecResult.Accepted(produced)
    }

    /** Rola o teste, registra no log e devolve o resultado definitivo. */
    fun check(original: CheckSpec): CheckResult {
        if (original.fortuneMode != FortuneMode.NORMAL) {
            return checkWithFortune(
                original.copy(fortuneMode = FortuneMode.NORMAL),
                original.fortuneMode,
            ).chosen
        }
        val spec = original.resolvedPool()
        val roll = dice.roll(spec.diceCount, spec.diceSides, spec.modifier, spec.explodeOnMax)
        val result = resolveCheck(spec, roll)
        recordRoll(spec.label, roll)
        return result
    }

    /**
     * Resolve um ataque inteiramente no motor. A IA pode narrar a consequência,
     * mas não escolhe a rolagem, a defesa nem o dano.
     *
     * N-09: caminho antigo (d20 + atributo contra defesa base), sem uso no app; o jogo usa [unifiedAttack].
     */
    @Deprecated("N-09: use unifiedAttack/unifiedAttackBetween")
    fun attack(spec: AttackSpec): AttackResult {
        val combat = state.combat ?: return AttackResult.Rejected("não há combate ativo")
        if (combat.current != spec.attackerId) {
            return AttackResult.Rejected("não é o turno de ${spec.attackerId}")
        }
        val attacker =
            state.entities[spec.attackerId]
                ?: return AttackResult.Rejected("entidade inexistente: ${spec.attackerId}")
        val target =
            state.entities[spec.targetId]
                ?: return AttackResult.Rejected("entidade inexistente: ${spec.targetId}")
        if (attacker.id == target.id) return AttackResult.Rejected("ataque contra si mesmo")
        if (!attacker.alive) return AttackResult.Rejected("${attacker.name} está morto")
        if (attacker.downed) return AttackResult.Rejected("${attacker.name} está caído")
        if (!target.alive) return AttackResult.Rejected("${target.name} está morto")
        if (spec.targetId !in combat.order) return AttackResult.Rejected("alvo fora do combate")
        if (target.resources[spec.damageResource] == null) {
            return AttackResult.Rejected("recurso inexistente: ${spec.damageResource}")
        }

        val attackModifier = (attacker.stats[spec.attackStat] ?: 0) + spec.attackBonus
        val defending = target.id in combat.defending
        val stanceBonus = if (defending) config.defendBonus else 0
        val targetDefenseModifier = (target.stats[spec.defenseStat] ?: 0) + spec.defenseBonus + stanceBonus
        val defense =
            maxOf(
                0,
                when (spec.attackComparison) {
                    Comparison.ROLL_OVER -> spec.defenseDefault + targetDefenseModifier
                    Comparison.ROLL_UNDER -> spec.defenseDefault + attackModifier - targetDefenseModifier
                    Comparison.POINTS -> spec.defenseDefault + targetDefenseModifier
                },
            )
        val mark = mark()
        val roll =
            when (spec.attackComparison) {
                Comparison.POINTS, Comparison.ROLL_UNDER -> dice.roll(spec.attackDiceCount, spec.attackDiceSides)
                Comparison.ROLL_OVER -> dice.roll(spec.attackDiceCount, spec.attackDiceSides, attackModifier)
            }
        recordRoll("ataque:${spec.attackerId}->${spec.targetId}", roll)
        val hit =
            when (spec.attackComparison) {
                Comparison.ROLL_OVER -> roll.total >= defense
                Comparison.ROLL_UNDER -> roll.total <= defense
                Comparison.POINTS -> {
                    val faces = pointFaces(attackModifier - targetDefenseModifier, spec.attackDiceSides)
                    val lowestSuccess = spec.attackDiceSides + 1 - faces
                    roll.dice.single() >= lowestSuccess
                }
            }
        var damageRoll: RollResult? = null
        var resolvedDamage = 0
        if (hit) {
            damageRoll =
                if (spec.damageDiceCount > 0) {
                    dice.roll(
                        spec.damageDiceCount,
                        spec.damageDiceSides,
                        (spec.damageStat?.let { attacker.stats[it] } ?: 0) + spec.damageBonus,
                    ).also { recordRoll("dano:${spec.attackerId}->${spec.targetId}", it) }
                } else {
                    null
                }
            val rolledDamage = (damageRoll?.total ?: spec.damage) + if (damageRoll == null) spec.damageBonus else 0
            resolvedDamage = maxOf(0, rolledDamage - spec.damageReduction)
        }
        val actualDamage = if (hit) resolvedDamage else 0
        recordEvent(AttackResolved(spec.attackerId, spec.targetId, defense, hit, actualDamage))
        if (defending) recordEvent(CombatantDefenseConsumed(target.id))
        if (hit) {
            when (val result = execute(Damage(spec.targetId, spec.damageResource, resolvedDamage, sourceId = spec.attackerId))) {
                is ExecResult.Accepted -> return AttackResult.Resolved(roll, true, defense, actualDamage, eventsSince(mark), damageRoll)
                is ExecResult.Rejected -> {
                    rollbackTo(mark)
                    return AttackResult.Rejected(result.reason)
                }
            }
        }
        return AttackResult.Resolved(roll, false, defense, 0, eventsSince(mark), null)
    }

    /**
     * MU-02.4: confronto do motor unificado (valor efetivo → dados, crítico, ignorar defesa, defesa rolada).
     * Mesma validação e mesmo caminho para jogador, NPC, aliado e chefe: rolagens e o confronto vão ao log,
     * o dano passa pelo comando [Damage] e qualquer recusa desfaz tudo. A IA só propõe ação e alvo.
     */
    fun unifiedAttack(spec: UnifiedAttackSpec): UnifiedAttackResult {
        val combat = state.combat ?: return UnifiedAttackResult.Rejected("não há combate ativo")
        if (combat.current != spec.attackerId) return UnifiedAttackResult.Rejected("não é o turno de ${spec.attackerId}")
        val attacker = state.entities[spec.attackerId] ?: return UnifiedAttackResult.Rejected("entidade inexistente: ${spec.attackerId}")
        val target = state.entities[spec.targetId] ?: return UnifiedAttackResult.Rejected("entidade inexistente: ${spec.targetId}")
        if (attacker.id == target.id) return UnifiedAttackResult.Rejected("ataque contra si mesmo")
        if (!attacker.alive) return UnifiedAttackResult.Rejected("${attacker.name} está morto")
        if (attacker.downed) return UnifiedAttackResult.Rejected("${attacker.name} está caído")
        if (!target.alive) return UnifiedAttackResult.Rejected("${target.name} está morto")
        if (spec.targetId !in combat.order) return UnifiedAttackResult.Rejected("alvo fora do combate")
        if (target.resources[spec.damageResource] == null) {
            return UnifiedAttackResult.Rejected(
                "recurso inexistente: ${spec.damageResource}",
            )
        }

        val mark = mark()
        // MU-02.5: em recarga, o "ignorar defesa" não é tentado (nem rola o d10); o ataque segue normal.
        val ignoreSpec = spec.ignoreDefense?.takeIf { (state.ignoreCooldowns[spec.attackerId] ?: 0) <= 0 }
        val defending = target.id in combat.defending
        val raw =
            UnifiedCombat.resolve(
                spec.attack,
                spec.defense,
                dice,
                spec.critModifiers,
                spec.guaranteedCrit,
                spec.canCrit,
                ignoreSpec,
                spec.hit,
                spec.luck,
                spec.luckBonus,
            )
        val elemental = if (spec.elementPercent != 100) raw.copy(damage = ElementChart.apply(raw.damage, spec.elementPercent)) else raw
        val clash = if (spec.damageReduction > 0) elemental.copy(damage = maxOf(0, elemental.damage - spec.damageReduction)) else elemental
        if (defending) recordEvent(CombatantDefenseConsumed(target.id))
        val label = "${spec.attackerId}->${spec.targetId}"
        clash.hit?.let { recordRoll("acerto:$label", it.roll) }
        clash.crit?.roll?.let { recordRoll("critico:$label", it) }
        clash.ignore?.roll?.let { recordRoll("ignora-defesa:$label", it) }
        clash.attackRoll?.let { recordRoll("ataque:$label", it) }
        clash.defenseRoll?.let { recordRoll("defesa:$label", it) }

        fun sources(b: EffectiveBreakdown) =
            listOf("base ${b.base}") + b.applied.map { "${it.label} ${if (it.value >= 0) "+" else ""}${it.value}" }
        recordEvent(
            ClashRecorded(
                spec.attackerId, spec.targetId,
                spec.attack.value, spec.attack.expression.toString(), clash.attackTotal,
                spec.defense.value, spec.defense.expression.toString(), clash.defenseTotal,
                clash.critical, clash.ignoredDefense, clash.damage,
                sources(spec.attack), sources(spec.defense),
                (spec.attack.rejected + spec.defense.rejected).map { "${it.modifier.label}: ${it.reason}" },
                clash.attackRoll?.dice.orEmpty(), clash.defenseRoll?.dice.orEmpty(),
                clash.crit?.threshold, clash.crit?.roll?.total, clash.crit?.guaranteed == true,
                clash.ignore?.threshold, clash.ignore?.roll?.total, clash.ignore?.guaranteed == true,
                clash.hit?.chance, clash.hit?.roll?.total, clash.missed,
            ),
        )
        recordEvent(AttackResolved(spec.attackerId, spec.targetId, clash.defenseTotal, clash.damage > 0, clash.damage))
        if (clash.ignoredDefense && ignoreSpec != null && ignoreSpec.cooldownRounds > 0) {
            recordEvent(IgnoreDefenseCooldownStarted(spec.attackerId, ignoreSpec.cooldownRounds))
        }
        if (clash.damage > 0) {
            when (
                val result =
                    execute(
                        Damage(
                            spec.targetId,
                            spec.damageResource,
                            clash.damage,
                            bypassShield = clash.ignoredDefense,
                            sourceId = spec.attackerId,
                        ),
                    )
            ) {
                is ExecResult.Accepted -> Unit
                is ExecResult.Rejected -> {
                    rollbackTo(mark)
                    return UnifiedAttackResult.Rejected(result.reason)
                }
            }
        }
        return UnifiedAttackResult.Resolved(clash, eventsSince(mark))
    }

    /**
     * N-16: tenta aplicar [condition] em [targetId] com [chancePercent] (d100 em silêncio, aplica com dado <= chance; 100 não rola).
     * A rolagem vai ao log; recusa do comando desfaz a rolagem. Devolve true quando a condição foi aplicada.
     */
    fun applyConditionByChance(
        targetId: String,
        condition: String,
        chancePercent: Int,
        durationRounds: Int? = null,
        damagePerRound: Int = 0,
        damageResource: String = "hp",
        damagePercent: Int = 0,
        /** N-17: percentual do dano da condição (peito encantado = 50). */
        damageFactorPercent: Int = 100,
    ): Boolean {
        require(chancePercent in 0..100) { "chance fora de 0..100" }
        require(damageFactorPercent in 0..100) { "fator de dano fora de 0..100" }
        val mark = mark()
        if (chancePercent < 100) {
            val roll = dice.roll(1, 100)
            recordRoll("condicao:$targetId:$condition", roll)
            if (roll.total > chancePercent) return false
        }
        val perRound = damagePerRound * damageFactorPercent / 100
        val percent = damagePercent * damageFactorPercent / 100
        return when (val result = execute(AddCondition(targetId, condition, durationRounds, perRound, damageResource, percent))) {
            is ExecResult.Accepted -> true
            is ExecResult.Rejected -> {
                rollbackTo(mark)
                false
            }
        }
    }

    /**
     * N-20: ação genérica de ofício (criar, preparar, consertar, identificar). Rola d100 em silêncio (100 não rola) com [chancePercent].
     * Sucesso: consome todas as [inputs] e entrega as [outputs]. Falha: perde metade (arredondada para baixo) das entradas fora de [keepOnFailure].
     * Nada muda se faltar algum item ou o motor recusar um passo (a rolagem é desfeita).
     */
    fun attemptCraft(
        ownerId: String,
        kind: CraftKind,
        inputs: Map<String, Int>,
        outputs: Map<String, Int>,
        chancePercent: Int,
        keepOnFailure: Set<String> = emptySet(),
    ): CraftResult {
        require(chancePercent in 0..100) { "chance fora de 0..100" }
        val ent = state.entities[ownerId] ?: return CraftResult.Refused("entidade inexistente: $ownerId")
        if (!ent.alive) return CraftResult.Refused("${ent.name} está morto")
        if (state.combat != null) return CraftResult.Refused("ofício não vale em combate")
        if (outputs.isEmpty() || outputs.values.any { it <= 0 }) {
            return CraftResult.Refused(
                "a receita precisa de saída com quantidade positiva",
            )
        }
        if (inputs.values.any { it <= 0 }) return CraftResult.Refused("quantidade de entrada inválida")
        if (kind != CraftKind.CRAFT && inputs.isEmpty()) return CraftResult.Refused("a receita precisa de entrada")
        inputs.forEach { (id, qty) ->
            val have = state.inventory[ownerId]?.get(id) ?: 0
            if (have < qty) return CraftResult.Refused("item ausente ou insuficiente: $id ($have/$qty)")
            if (state.equipped[ownerId].orEmpty().values.contains(id)) return CraftResult.Refused("desequipe $id antes")
        }
        val mark = mark()
        var roll: Int? = null
        if (chancePercent < 100) {
            val r = dice.roll(1, 100)
            recordRoll("oficio:${kind.id}:$ownerId", r)
            roll = r.total
        }
        val success = roll == null || roll <= chancePercent
        val consumed =
            if (success) {
                inputs
            } else {
                inputs.mapValues {
                        (id, qty) ->
                    if (id in keepOnFailure) {
                        0
                    } else if (kind == CraftKind.REPAIR) {
                        qty
                    } else {
                        qty / 2
                    }
                }.filterValues { it > 0 }
            }
        val produced = if (success) outputs else emptyMap()
        val steps: List<Command> =
            consumed.map {
                    (id, qty) ->
                ConsumeItem(ownerId, id, qty)
            } + produced.map { (id, qty) -> AddItem(ownerId, id, qty) }
        for (step in steps) {
            val result = execute(step)
            if (result is ExecResult.Rejected) {
                rollbackTo(mark)
                return CraftResult.Refused(result.reason)
            }
        }
        return CraftResult.Done(success, chancePercent, roll, consumed, produced)
    }

    /**
     * N-20: afanar [qty] de [itemId] do inventário de [targetId] (item não equipado). Chance = acerto de Destreza do ladrão × Agilidade do alvo
     * ([dexterity] e [agility] já efetivas, como no acerto do combate). Falha: o alvo percebe e a relação dele com o ladrão cai em
     * [PILFER_CAUGHT_RELATION]. Fora de combate.
     */
    fun attemptPilfer(
        thiefId: String,
        targetId: String,
        itemId: String,
        dexterity: Int,
        agility: Int,
        qty: Int = 1,
    ): CraftResult {
        val thief = state.entities[thiefId] ?: return CraftResult.Refused("entidade inexistente: $thiefId")
        val target = state.entities[targetId] ?: return CraftResult.Refused("entidade inexistente: $targetId")
        if (thief.id == target.id) return CraftResult.Refused("não é possível afanar de si mesmo")
        if (!thief.alive) return CraftResult.Refused("${thief.name} está morto")
        if (state.combat != null) return CraftResult.Refused("afanar não vale em combate")
        if (qty <= 0) return CraftResult.Refused("quantidade inválida")
        val have = state.inventory[targetId]?.get(itemId) ?: 0
        if (have < qty) return CraftResult.Refused("${target.name} não tem $itemId ($have/$qty)")
        if (state.equipped[targetId].orEmpty().values.contains(itemId)) return CraftResult.Refused("item equipado não pode ser afanado")
        val chance = UnifiedCombat.hitChance(dexterity, agility)
        val mark = mark()
        val r = dice.roll(1, 100)
        recordRoll("afanar:$thiefId:$targetId", r)
        val success = r.total <= chance
        val steps: List<Command> =
            if (success) {
                listOf(ConsumeItem(targetId, itemId, qty), AddItem(thiefId, itemId, qty))
            } else {
                listOf(ChangeRelation(targetId, thiefId, PILFER_CAUGHT_RELATION, "flagrou ${thief.name} afanando"))
            }
        for (step in steps) {
            val result = execute(step)
            if (result is ExecResult.Rejected && success) {
                rollbackTo(mark)
                return CraftResult.Refused(result.reason)
            }
        }
        return CraftResult.Done(success, chance, r.total, emptyMap(), if (success) mapOf(itemId to qty) else emptyMap(), caught = !success)
    }

    /**
     * MU-05.1: concede o XP de uma missão concluída ao personagem, uma única vez. A recompensa é proposta pelo
     * mestre ou pelo jogador, mas o motor exige missão DONE, valor positivo e dentro do teto do nível do alvo.
     */
    fun awardQuestXp(
        quests: QuestLog,
        questId: String,
        entityId: String,
    ): ExecResult {
        val quest = quests.get(questId) ?: return ExecResult.Rejected("missão inexistente: $questId")
        if (quest.status != QuestStatus.DONE) return ExecResult.Rejected("a missão não foi concluída")
        if (quest.rewarded) return ExecResult.Rejected("a recompensa desta missão já foi concedida")
        if (quest.xpReward <= 0) return ExecResult.Rejected("a missão não tem recompensa de XP")
        val entity = state.entities[entityId] ?: return ExecResult.Rejected("entidade inexistente: $entityId")
        val cap = config.maxQuestXp(entity.level)
        if (quest.xpReward > cap) {
            return ExecResult.Rejected(
                "XP da missão (${quest.xpReward}) acima do teto do nível ${entity.level}: $cap",
            )
        }
        val result = execute(GainXp(entityId, quest.xpReward))
        if (result is ExecResult.Accepted) quests.markRewarded(questId)
        return result
    }

    /** MU-07.3: concede a magia de [Quest.spellRewardId] ao concluir a missão, uma única vez e se houver slot livre. */
    fun awardQuestSpell(
        quests: QuestLog,
        questId: String,
        entityId: String,
    ): ExecResult {
        val quest = quests.get(questId) ?: return ExecResult.Rejected("missão inexistente: $questId")
        if (quest.status != QuestStatus.DONE) return ExecResult.Rejected("a missão não foi concluída")
        val spellId = quest.spellRewardId ?: return ExecResult.Rejected("a missão não concede magia")
        if (quest.spellGranted) return ExecResult.Rejected("a magia desta missão já foi concedida")
        val result = execute(LearnSpell(entityId, spellId))
        if (result is ExecResult.Accepted) quests.markSpellGranted(questId)
        return result
    }

    /** MU-07.2: slots de magia da entidade (total pelo nível, usados pelas magias aprendidas); null se não existe. */
    fun spellSlots(entityId: String): SpellSlots? =
        state.entities[entityId]?.let { SpellSlots(config.spellSlotsFor(it.level), it.spells.size) }

    /** MU-06.2: libera o nível 2 de [Quest.masterySkillId] ao concluir a missão, uma única vez. */
    fun awardQuestMastery(
        quests: QuestLog,
        questId: String,
        entityId: String,
    ): ExecResult {
        val quest = quests.get(questId) ?: return ExecResult.Rejected("missão inexistente: $questId")
        if (quest.status != QuestStatus.DONE) return ExecResult.Rejected("a missão não foi concluída")
        val skillId = quest.masterySkillId ?: return ExecResult.Rejected("a missão não libera nível 2 de perícia")
        if (quest.masteryGranted) return ExecResult.Rejected("o nível 2 desta missão já foi liberado")
        val result = execute(MasterSkill(entityId, skillId))
        if (result is ExecResult.Accepted) quests.markMasteryGranted(questId)
        return result
    }

    /**
     * MU-03.2: ataque unificado montado a partir do estado. Atacante e alvo passam pelo mesmo cálculo
     * (stat-base + fontes), sem ramificar por jogador/NPC/chefe. Bônus `crit` e `ignoreDef` vêm das fontes.
     */
    fun unifiedAttackBetween(
        attackerId: String,
        targetId: String,
        providers: List<ModifierProvider>,
        attackStat: String = "attack",
        defenseStat: String = "defense",
        guaranteedCrit: Boolean = false,
        canCrit: Boolean = true,
        ignoreDefense: IgnoreDefenseSpec? = null,
        damageResource: String = "hp",
        hitStat: String? = null,
        dodgeStat: String? = null,
        luckStat: String? = null,
        damageReduction: Int = 0,
        luckItemBonus: Int = 0,
        elementPercent: Int = 100,
    ): UnifiedAttackResult {
        val itemLuck = luckItemBonus.coerceAtLeast(0)
        val luck = luckStat?.let { CombatantValues.breakdown(state, attackerId, it, providers)?.value }?.let { maxOf(0, it - itemLuck) } ?: 0
        val hitSpec =
            if (hitStat != null && dodgeStat != null) {
                HitSpec(
                    CombatantValues.breakdown(state, attackerId, hitStat, providers)
                        ?: return UnifiedAttackResult.Rejected("entidade inexistente: $attackerId"),
                    CombatantValues.breakdown(state, targetId, dodgeStat, providers)
                        ?: return UnifiedAttackResult.Rejected("entidade inexistente: $targetId"),
                )
            } else {
                null
            }
        val attack =
            CombatantValues.breakdown(state, attackerId, attackStat, providers)
                ?: return UnifiedAttackResult.Rejected("entidade inexistente: $attackerId")
        val defense =
            CombatantValues.breakdown(state, targetId, defenseStat, providers)
                ?: return UnifiedAttackResult.Rejected("entidade inexistente: $targetId")
        val ignore =
            ignoreDefense?.let {
                it.copy(modifiers = it.modifiers + CombatantValues.modifiers(state, attackerId, UnifiedCombat.IGNORE_TARGET, providers))
            }
        return unifiedAttack(
            UnifiedAttackSpec(
                attackerId, targetId, attack, defense,
                CombatantValues.modifiers(state, attackerId, UnifiedCombat.CRIT_TARGET, providers),
                guaranteedCrit, canCrit, ignore, damageResource, hitSpec, luck, itemLuck, damageReduction, elementPercent,
            ),
        )
    }

    /**
     * Ativa um poder no motor: valida usuário e alvo, gasta o recurso, rola o teste (se houver) e aplica o efeito
     * tipado. Qualquer recusa desfaz tudo, inclusive o custo. A IA não escolhe custo, dado nem efeito.
     */
    fun activatePower(spec: PowerSpec): PowerResult {
        val user = state.entities[spec.userId] ?: return PowerResult.Rejected("entidade inexistente: ${spec.userId}")
        if (!user.alive) return PowerResult.Rejected("${user.name} está morto")
        if (user.downed) return PowerResult.Rejected("${user.name} está caído")
        val targetId = spec.targetId ?: spec.userId
        val target = state.entities[targetId] ?: return PowerResult.Rejected("entidade inexistente: $targetId")
        when (val effect = spec.effect) {
            is PowerEffect.Cure, is PowerEffect.Cleanse, is PowerEffect.Shield ->
                if (targetId != spec.userId) return PowerResult.Rejected("este poder só atua em quem o usa")
            is PowerEffect.Strike -> {
                if (targetId == spec.userId) return PowerResult.Rejected("este poder exige um alvo diferente do usuário")
                if (!target.alive) return PowerResult.Rejected("${target.name} está morto")
                if (target.resources[effect.resource] == null) return PowerResult.Rejected("recurso inexistente: ${effect.resource}")
            }
            is PowerEffect.Inflict ->
                if (targetId == spec.userId) return PowerResult.Rejected("este poder exige um alvo diferente do usuário")
        }
        val mark = mark()

        fun fail(reason: String): PowerResult {
            rollbackTo(mark)
            return PowerResult.Rejected(reason)
        }
        if (spec.cost > 0) {
            val spent = execute(SpendResource(spec.userId, spec.resource, spec.cost))
            if (spent is ExecResult.Rejected) return fail(spent.reason)
        }
        val checkResult = spec.check?.let { check(it) }
        val applies = checkResult?.outcome?.isSuccess ?: true
        var damageRoll: RollResult? = null
        var delta = 0
        if (applies) {
            val before = state.entities[targetId]?.resources
            val command: Command =
                when (val effect = spec.effect) {
                    is PowerEffect.Cure -> Heal(targetId, effect.resource, effect.amount)
                    is PowerEffect.Strike -> {
                        val roll = dice.roll(effect.diceCount, effect.diceSides, effect.bonus)
                        recordRoll("poder:${spec.powerId}->$targetId", roll)
                        damageRoll = roll
                        Damage(targetId, effect.resource, maxOf(0, roll.total - effect.reduction), sourceId = spec.userId)
                    }
                    is PowerEffect.Inflict ->
                        AddCondition(
                            targetId,
                            effect.condition,
                            effect.durationRounds,
                            effect.damagePerRound,
                            effect.damageResource,
                            effect.damagePercent,
                        )
                    is PowerEffect.Cleanse -> RemoveCondition(targetId, effect.condition)
                    is PowerEffect.Shield -> GrantShield(targetId, effect.amount)
                }
            val applied = execute(command)
            if (applied is ExecResult.Rejected) return fail(applied.reason)
            val resourceName =
                when (val effect = spec.effect) {
                    is PowerEffect.Cure -> effect.resource
                    is PowerEffect.Strike -> effect.resource
                    else -> null
                }
            if (resourceName != null) {
                delta = (state.entities[targetId]?.resources?.get(resourceName)?.current ?: 0) -
                    (before?.get(resourceName)?.current ?: 0)
            }
        }
        return PowerResult.Activated(spec.cost, checkResult, applies, damageRoll, delta, eventsSince(mark))
    }

    /** MU-06.3: sorteia 1d6 de rodadas de duração e grava a rolagem no log. */
    internal fun rollDuration(label: String): RolledDuration {
        val rolled = EffectDuration.rollRounds(dice)
        recordRoll(label, rolled.roll)
        return rolled
    }

    private fun recordRoll(
        label: String,
        roll: RollResult,
    ) {
        val ev = RollRecorded(label, roll.expression, roll.dice, roll.modifier, roll.total)
        recordEvent(ev)
    }

    private fun recordEvent(event: Event) {
        log += event
        state = state.apply(event)
    }

    private fun eventsSince(mark: Int): List<Event> = log.drop(mark)

    /**
     * Rola a iniciativa de cada participante (dados do motor, gravados no log) e inicia o combate.
     * Maior total age primeiro; empate resolve por id (determinístico). [statKey] = atributo somado, se houver.
     * Se o combate for recusado, as rolagens são desfeitas.
     */
    fun rollInitiative(
        ids: List<String>,
        diceCount: Int = 1,
        diceSides: Int = 20,
        statKey: String? = null,
        highestFirst: Boolean = true,
    ): ExecResult {
        ids.firstOrNull { !state.entities.containsKey(it) }
            ?.let { return ExecResult.Rejected("entidade inexistente: $it") }
        val m = mark()
        val totals =
            ids.map { id ->
                val mod = statKey?.let { state.entities.getValue(id).stats[it] } ?: 0
                val roll = dice.roll(diceCount, diceSides, mod)
                recordRoll("iniciativa:$id", roll)
                id to roll.total
            }
        val order =
            totals
                .sortedWith(
                    (
                        if (highestFirst) {
                            compareByDescending<Pair<String, Int>> { it.second }
                        } else {
                            compareBy<Pair<String, Int>> { it.second }
                        }
                    ).thenBy { it.first },
                )
                .map { it.first }
        val result = execute(StartCombat(order))
        if (result is ExecResult.Rejected) rollbackTo(m)
        return result
    }

    /** Marca um ponto do log (ex.: início de uma jogada) para desfazer depois. */

    /** N-32.4: rola 1d[sides] (d20 do animal, d100 da ordem) e registra a rolagem no log; quem chama usa o total. */
    fun rollPlain(
        label: String,
        sides: Int,
    ): Int {
        require(sides in 2..1000) { "lados fora de 2..1000" }
        val r = dice.roll(1, sides)
        recordRoll(label, r)
        return r.total
    }

    fun mark(): Int = log.size

    /** Desfaz tudo depois de [mark], inclusive rolagens (os mesmos dados voltam a sair). */
    fun rollbackTo(mark: Int) {
        require(mark in 0..log.size) { "marca inválida" }
        while (log.size > mark) log.removeAt(log.size - 1)
        state = replay(log)
        dice.seek(log.count { it is RollRecorded }.toLong())
    }

    companion object {
        /** Retoma uma campanha a partir do log salvo (Fase 3). */
        fun restore(
            seed: Long,
            config: EngineConfig,
            events: List<Event>,
            snapshot: GameState? = null,
        ): GameEngine {
            val dice = Dice(seed, events.count { it is RollRecorded }.toLong())
            // M-08.3: um snapshot válido (eventCount dentro do log) evita refazer o início; só a cauda é aplicada.
            val state =
                if (snapshot != null && snapshot.eventCount in 0..events.size) {
                    replay(events.subList(snapshot.eventCount, events.size), snapshot)
                } else {
                    replay(events)
                }
            return GameEngine(seed, config, dice, events.toMutableList(), state)
        }
    }
}

/** Promessas de NPC ainda não cumpridas (2.2.4). Use para impedir que a narração as esqueça. */
fun GameState.openPromises(npcId: String? = null): List<Promise> =
    promises.values.filter { !it.kept && (npcId == null || it.npcId == npcId) }

/** Confere invariantes do estado. Lista vazia = estado coerente. */
fun validateState(state: GameState): List<String> {
    val problems = mutableListOf<String>()
    for (e in state.entities.values) {
        if (e.advancementPoints < 0) problems += "${e.id} com pontos de evolução negativos"
        if (e.statPoints < 0) problems += "${e.id} com pontos de status negativos"
        if (e.abilityPoints < 0) problems += "${e.id} com pontos de habilidade negativos"
        for ((name, r) in e.resources) {
            if (r.current !in 0..r.max) problems += "${e.id}.$name fora do limite"
        }
    }
    for ((owner, items) in state.inventory) {
        if (!state.entities.containsKey(owner)) problems += "inventário de dono inexistente: $owner"
        for ((item, qty) in items) if (qty <= 0) problems += "$owner/$item com quantidade $qty"
    }
    for ((owner, slots) in state.equipped) {
        if (!state.entities.containsKey(owner)) problems += "equipamento de dono inexistente: $owner"
        for ((slot, item) in slots) {
            if (slot.isBlank() || item.isBlank()) problems += "$owner com equipamento inválido"
            if ((state.inventory[owner]?.get(item) ?: 0) <= 0) problems += "$owner/$slot equipado sem item no inventário"
        }
    }
    for (p in state.promises.values) {
        if (!state.entities.containsKey(p.npcId)) problems += "promessa ${p.id} de NPC inexistente: ${p.npcId}"
    }
    for ((id, activeConditions) in state.conditions) {
        if (!state.entities.containsKey(id)) problems += "condição em entidade inexistente: $id"
        for (condition in activeConditions) {
            if (state.conditionStates[id]?.containsKey(condition) != true) {
                problems += "condição sem estado mecânico: $id/$condition"
            }
        }
    }
    for ((id, states) in state.conditionStates) {
        val entity = state.entities[id]
        if (entity == null) problems += "estado de condição em entidade inexistente: $id"
        for ((condition, effect) in states) {
            if (condition !in state.conditions[id].orEmpty()) problems += "estado de condição órfão: $id/$condition"
            if (effect.remainingRounds != null && effect.remainingRounds <= 0) problems += "duração inválida: $id/$condition"
            if (effect.damagePerRound < 0) problems += "dano por rodada negativo: $id/$condition"
            if (effect.dealsDamage && entity?.resources?.containsKey(effect.damageResource) != true) {
                problems += "recurso de dano de condição inexistente: $id/$condition/${effect.damageResource}"
            }
        }
    }
    for ((from, destinations) in state.placeLinks) {
        if (from !in state.places) problems += "conexões de local inexistente: $from"
        for (to in destinations) {
            if (to !in state.places) problems += "destino de local inexistente: $to"
            if (from !in state.placeLinks[to].orEmpty()) problems += "conexão de local não recíproca: $from/$to"
        }
    }
    if (state.currentPlaceId != null && state.currentPlaceId !in state.knownPlaces) {
        problems += "local atual não está descoberto: ${state.currentPlaceId}"
    }
    if (state.knownPlaces.any { it !in state.places }) problems += "há local descoberto sem definição"
    for ((npcId, dossier) in state.npcDossiers) {
        if (state.entities[npcId]?.kind != EntityKind.NPC) problems += "dossiê sem NPC válido: $npcId"
        if (dossier.npcId != npcId) problems += "id do dossiê não corresponde à chave: $npcId"
    }
    for ((key, value) in state.relations) {
        if (value !in -100..100) problems += "relação fora do intervalo: $key"
        val ids = key.split('\u0000', limit = 2)
        if (ids.size != 2 || ids.any { it.isBlank() } ||
            ids.size == 2 && (ids[0] !in state.entities || ids[1] !in state.entities)
        ) {
            problems += "relação aponta para entidade inexistente: $key"
        }
    }
    for ((id, clock) in state.clocks) {
        if (id != clock.id) problems += "id de relógio inconsistente: $id"
        if (clock.filled !in 0..clock.segments) problems += "preenchimento de relógio inválido: $id"
    }
    state.combat?.let { c ->
        if (c.turnIndex !in c.order.indices) problems += "turno fora da ordem de iniciativa"
        for (id in c.order) if (!state.entities.containsKey(id)) problems += "combate com entidade inexistente: $id"
        for (id in c.defending) if (id !in c.order || state.entities[id]?.alive != true) {
            problems += "postura defensiva inválida no combate: $id"
        }
    }
    return problems
}

/** Relação de [fromId] com [toId]; 0 se nunca mudou. */
fun GameState.relation(
    fromId: String,
    toId: String,
): Int = relations[relationKey(fromId, toId)] ?: 0

/** N-29: relação mínima do NPC com o personagem para contar como aliado (10 = simpatia, a faixa positiva mais baixa). */
const val ALLY_RELATION_MIN = 10

/**
 * N-29: aliados de [actorId] no combate atual: NPCs vivos e de pé que participam da iniciativa e cuja relação com
 * [actorId] é ao menos [ALLY_RELATION_MIN]. Sem combate, lista vazia. Não existe lado fixo no motor; o mestre
 * transforma um NPC em aliado mudando a relação dele com o personagem.
 */
fun GameState.allyIdsInCombat(actorId: String): List<String> {
    val order = combat?.order ?: return emptyList()
    return order.filter { id ->
        val e = entities[id]
        id != actorId && e != null && e.kind == EntityKind.NPC && e.alive && !e.downed && relation(id, actorId) >= ALLY_RELATION_MIN &&
            companions[id]?.awayReason() == null
    }
}

/** Relógios que já se completaram (a IA deve narrar a consequência; quem completa é o motor). */
fun GameState.fullClocks(): List<Clock> = clocks.values.filter { it.full }

/** Relógios com `moveTick` que ainda não completaram: avançam quando o jogador viaja (M-07). */
fun GameState.travelClocks(): List<Clock> = clocks.values.filter { it.moveTick > 0 && !it.full }

/** Relógios com `chapterTick` que ainda não completaram: avançam quando um capítulo é encerrado (M-07). */
fun GameState.chapterClocks(): List<Clock> = clocks.values.filter { it.chapterTick > 0 && !it.full }
