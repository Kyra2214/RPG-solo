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
        val spec = original.resolvedPool()
        val roll = dice.roll(spec.diceCount, spec.diceSides, spec.modifier)
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
        fun restore(seed: Long, config: EngineConfig, events: List<Event>): GameEngine {
            val dice = Dice(seed, events.count { it is RollRecorded }.toLong())
            return GameEngine(seed, config, dice, events.toMutableList(), replay(events))
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
