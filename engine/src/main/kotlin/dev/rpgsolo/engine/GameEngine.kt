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
    state: GameState
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
                original.fortuneMode
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
     */
    fun attack(spec: AttackSpec): AttackResult {
        val combat = state.combat ?: return AttackResult.Rejected("não há combate ativo")
        if (combat.current != spec.attackerId) {
            return AttackResult.Rejected("não é o turno de ${spec.attackerId}")
        }
        val attacker = state.entities[spec.attackerId]
            ?: return AttackResult.Rejected("entidade inexistente: ${spec.attackerId}")
        val target = state.entities[spec.targetId]
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
        val defense = maxOf(0, when (spec.attackComparison) {
            Comparison.ROLL_OVER -> spec.defenseDefault + targetDefenseModifier
            Comparison.ROLL_UNDER -> spec.defenseDefault + attackModifier - targetDefenseModifier
            Comparison.POINTS -> spec.defenseDefault + targetDefenseModifier
        })
        val mark = mark()
        val roll = when (spec.attackComparison) {
            Comparison.POINTS, Comparison.ROLL_UNDER -> dice.roll(spec.attackDiceCount, spec.attackDiceSides)
            Comparison.ROLL_OVER -> dice.roll(spec.attackDiceCount, spec.attackDiceSides, attackModifier)
        }
        recordRoll("ataque:${spec.attackerId}->${spec.targetId}", roll)
        val hit = when (spec.attackComparison) {
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
            damageRoll = if (spec.damageDiceCount > 0) {
                dice.roll(
                    spec.damageDiceCount,
                    spec.damageDiceSides,
                    (spec.damageStat?.let { attacker.stats[it] } ?: 0) + spec.damageBonus
                ).also { recordRoll("dano:${spec.attackerId}->${spec.targetId}", it) }
            } else null
            val rolledDamage = (damageRoll?.total ?: spec.damage) + if (damageRoll == null) spec.damageBonus else 0
            resolvedDamage = maxOf(0, rolledDamage - spec.damageReduction)
        }
        val actualDamage = if (hit) resolvedDamage else 0
        recordEvent(AttackResolved(spec.attackerId, spec.targetId, defense, hit, actualDamage))
        if (defending) recordEvent(CombatantDefenseConsumed(target.id))
        if (hit) {
            when (val result = execute(Damage(spec.targetId, spec.damageResource, resolvedDamage))) {
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
        if (target.resources[spec.damageResource] == null) return UnifiedAttackResult.Rejected("recurso inexistente: ${spec.damageResource}")

        val mark = mark()
        val clash = UnifiedCombat.resolve(
            spec.attack, spec.defense, dice, spec.critModifiers, spec.guaranteedCrit, spec.canCrit, spec.ignoreDefense
        )
        val label = "${spec.attackerId}->${spec.targetId}"
        clash.crit?.roll?.let { recordRoll("critico:$label", it) }
        clash.ignore?.roll?.let { recordRoll("ignora-defesa:$label", it) }
        clash.attackRoll?.let { recordRoll("ataque:$label", it) }
        clash.defenseRoll?.let { recordRoll("defesa:$label", it) }
        fun sources(b: EffectiveBreakdown) = listOf("base ${b.base}") + b.applied.map { "${it.label} ${if (it.value >= 0) "+" else ""}${it.value}" }
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
                clash.ignore?.threshold, clash.ignore?.roll?.total, clash.ignore?.guaranteed == true
            )
        )
        recordEvent(AttackResolved(spec.attackerId, spec.targetId, clash.defenseTotal, clash.damage > 0, clash.damage))
        if (clash.damage > 0) {
            when (val result = execute(Damage(spec.targetId, spec.damageResource, clash.damage))) {
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
     * MU-05.1: concede o XP de uma missão concluída ao personagem, uma única vez. A recompensa é proposta pelo
     * mestre ou pelo jogador, mas o motor exige missão DONE, valor positivo e dentro do teto do nível do alvo.
     */
    fun awardQuestXp(quests: QuestLog, questId: String, entityId: String): ExecResult {
        val quest = quests.get(questId) ?: return ExecResult.Rejected("missão inexistente: $questId")
        if (quest.status != QuestStatus.DONE) return ExecResult.Rejected("a missão não foi concluída")
        if (quest.rewarded) return ExecResult.Rejected("a recompensa desta missão já foi concedida")
        if (quest.xpReward <= 0) return ExecResult.Rejected("a missão não tem recompensa de XP")
        val entity = state.entities[entityId] ?: return ExecResult.Rejected("entidade inexistente: $entityId")
        val cap = config.maxQuestXp(entity.level)
        if (quest.xpReward > cap) return ExecResult.Rejected("XP da missão (${quest.xpReward}) acima do teto do nível ${entity.level}: $cap")
        val result = execute(GainXp(entityId, quest.xpReward))
        if (result is ExecResult.Accepted) quests.markRewarded(questId)
        return result
    }

    /** MU-07.3: concede a magia de [Quest.spellRewardId] ao concluir a missão, uma única vez e se houver slot livre. */
    fun awardQuestSpell(quests: QuestLog, questId: String, entityId: String): ExecResult {
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
    fun awardQuestMastery(quests: QuestLog, questId: String, entityId: String): ExecResult {
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
        damageResource: String = "hp"
    ): UnifiedAttackResult {
        val attack = CombatantValues.breakdown(state, attackerId, attackStat, providers)
            ?: return UnifiedAttackResult.Rejected("entidade inexistente: $attackerId")
        val defense = CombatantValues.breakdown(state, targetId, defenseStat, providers)
            ?: return UnifiedAttackResult.Rejected("entidade inexistente: $targetId")
        val ignore = ignoreDefense?.let {
            it.copy(modifiers = it.modifiers + CombatantValues.modifiers(state, attackerId, UnifiedCombat.IGNORE_TARGET, providers))
        }
        return unifiedAttack(
            UnifiedAttackSpec(
                attackerId, targetId, attack, defense,
                CombatantValues.modifiers(state, attackerId, UnifiedCombat.CRIT_TARGET, providers),
                guaranteedCrit, canCrit, ignore, damageResource
            )
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
            is PowerEffect.Cure, is PowerEffect.Cleanse ->
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
        fun fail(reason: String): PowerResult { rollbackTo(mark); return PowerResult.Rejected(reason) }
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
            val command: Command = when (val effect = spec.effect) {
                is PowerEffect.Cure -> Heal(targetId, effect.resource, effect.amount)
                is PowerEffect.Strike -> {
                    val roll = dice.roll(effect.diceCount, effect.diceSides, effect.bonus)
                    recordRoll("poder:${spec.powerId}->$targetId", roll)
                    damageRoll = roll
                    Damage(targetId, effect.resource, maxOf(0, roll.total - effect.reduction))
                }
                is PowerEffect.Inflict -> AddCondition(targetId, effect.condition, effect.durationRounds, effect.damagePerRound, effect.damageResource)
                is PowerEffect.Cleanse -> RemoveCondition(targetId, effect.condition)
            }
            val applied = execute(command)
            if (applied is ExecResult.Rejected) return fail(applied.reason)
            val resourceName = when (val effect = spec.effect) {
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

    private fun recordRoll(label: String, roll: RollResult) {
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
        highestFirst: Boolean = true
    ): ExecResult {
        ids.firstOrNull { !state.entities.containsKey(it) }
            ?.let { return ExecResult.Rejected("entidade inexistente: $it") }
        val m = mark()
        val totals = ids.map { id ->
            val mod = statKey?.let { state.entities.getValue(id).stats[it] } ?: 0
            val roll = dice.roll(diceCount, diceSides, mod)
            recordRoll("iniciativa:$id", roll)
            id to roll.total
        }
        val order = totals
            .sortedWith(
                (if (highestFirst) compareByDescending<Pair<String, Int>> { it.second }
                else compareBy<Pair<String, Int>> { it.second }).thenBy { it.first }
            )
            .map { it.first }
        val result = execute(StartCombat(order))
        if (result is ExecResult.Rejected) rollbackTo(m)
        return result
    }

    /** Marca um ponto do log (ex.: início de uma jogada) para desfazer depois. */
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
        fun restore(seed: Long, config: EngineConfig, events: List<Event>, snapshot: GameState? = null): GameEngine {
            val dice = Dice(seed, events.count { it is RollRecorded }.toLong())
            // M-08.3: um snapshot válido (eventCount dentro do log) evita refazer o início; só a cauda é aplicada.
            val state = if (snapshot != null && snapshot.eventCount in 0..events.size) {
                replay(events.subList(snapshot.eventCount, events.size), snapshot)
            } else replay(events)
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
            if (effect.damagePerRound > 0 && entity?.resources?.containsKey(effect.damageResource) != true) {
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
            ids.size == 2 && (ids[0] !in state.entities || ids[1] !in state.entities)) {
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
fun GameState.relation(fromId: String, toId: String): Int = relations[relationKey(fromId, toId)] ?: 0

/** Relógios que já se completaram (a IA deve narrar a consequência; quem completa é o motor). */
fun GameState.fullClocks(): List<Clock> = clocks.values.filter { it.full }

/** Relógios com `moveTick` que ainda não completaram: avançam quando o jogador viaja (M-07). */
fun GameState.travelClocks(): List<Clock> = clocks.values.filter { it.moveTick > 0 && !it.full }

/** Relógios com `chapterTick` que ainda não completaram: avançam quando um capítulo é encerrado (M-07). */
fun GameState.chapterClocks(): List<Clock> = clocks.values.filter { it.chapterTick > 0 && !it.full }
