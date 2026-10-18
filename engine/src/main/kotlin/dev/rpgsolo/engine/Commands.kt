package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** Pedidos de mudança (do app ou propostos pela IA). Nada vira estado sem passar por [decide]. */
sealed interface Command

data class CreateEntity(val entity: Entity) : Command
/** Dano: reduz o recurso (nunca abaixo de 0) e pode matar. */
data class Damage(val entityId: String, val resource: String, val amount: Int) : Command
/** Cura: aumenta o recurso (nunca acima do máximo). Não ressuscita. */
data class Heal(val entityId: String, val resource: String, val amount: Int) : Command
/** Gasto de recurso (mana, munição...). Falha se não houver o suficiente. */
data class SpendResource(val entityId: String, val resource: String, val cost: Int) : Command
data class AddItem(val ownerId: String, val itemId: String, val qty: Int = 1) : Command
data class ConsumeItem(val ownerId: String, val itemId: String, val qty: Int = 1) : Command
data class EquipItem(val ownerId: String, val itemId: String, val slot: String) : Command
data class UnequipItem(val ownerId: String, val slot: String) : Command
data class GainXp(val entityId: String, val amount: Int) : Command
/** Ressurreição exige justificativa explícita. */
data class Revive(val entityId: String, val reason: String) : Command
data class RecordFact(val key: String, val value: String) : Command
/** Jogador descobriu algo. [believed] = informação falsa/incerta, guardada como "acreditada". */
data class LearnKnowledge(val key: String, val text: String, val source: String, val believed: Boolean = false) : Command
data class MakePromise(val id: String, val npcId: String, val text: String) : Command
data class KeepPromise(val id: String) : Command
/** 8.2.2: muda a relação de [fromId] com [toId] (quanto o primeiro gosta/confia no segundo), de -100 a 100. */
data class ChangeRelation(val fromId: String, val toId: String, val delta: Int, val reason: String) : Command
/** 8.2.3: relógio de agenda (ameaça ou plano de NPC) com [segments] casas. */
data class CreateClock(val id: String, val name: String, val segments: Int) : Command
data class AdvanceClock(val id: String, val by: Int = 1) : Command
data class AddCondition(
    val entityId: String,
    val condition: String,
    val durationRounds: Int? = null,
    val damagePerRound: Int = 0,
    val damageResource: String = "hp"
) : Command
data class RemoveCondition(val entityId: String, val condition: String) : Command
/** [order] = ordem de iniciativa já decidida (ver GameEngine.rollInitiative). */
data class StartCombat(val order: List<String>) : Command
object NextTurn : Command
object EndCombat : Command

/** Configurações vindas do pacote de regras. */
@Serializable
data class EngineConfig(
    /** Recurso que, ao chegar a 0, mata (ex.: "pv"). null = o sistema não mata automaticamente. */
    val deathResource: String? = "hp",
    /** XP total necessário para o nível 2, 3, 4... (crescente). Vazio = o sistema não sobe de nível por XP. */
    val levelThresholds: List<Int> = emptyList()
) {
    init {
        require(levelThresholds.zipWithNext().all { (a, b) -> a < b } && levelThresholds.all { it > 0 }) {
            "levelThresholds deve ser positivo e estritamente crescente"
        }
    }

    /** Nível correspondente a um total de XP. */
    fun levelFor(xp: Int): Int = 1 + levelThresholds.count { xp >= it }
}

sealed interface Decision {
    data class Ok(val events: List<Event>) : Decision
    data class No(val reason: String) : Decision
}

private fun no(reason: String) = Decision.No(reason)

fun decide(state: GameState, config: EngineConfig, cmd: Command): Decision {
    fun entity(id: String) = state.entities[id]
    when (cmd) {
        is CreateEntity ->
            return if (state.entities.containsKey(cmd.entity.id)) no("entidade já existe: ${cmd.entity.id}")
            else Decision.Ok(listOf(EntityCreated(cmd.entity)))

        is Damage -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} já está morto")
            val res = ent.resources[cmd.resource] ?: return no("recurso inexistente: ${cmd.resource}")
            if (cmd.amount < 0) return no("dano negativo")
            val newValue = maxOf(0, res.current - cmd.amount)
            val events = mutableListOf<Event>(ResourceChanged(ent.id, cmd.resource, newValue, newValue - res.current))
            if (newValue == 0 && config.deathResource == cmd.resource) events += EntityDied(ent.id)
            if (events.any { it is EntityDied } && state.combat != null) {
                val afterDamage = events.fold(state) { current, event -> current.apply(event) }
                val aliveKinds = afterDamage.combat!!.order
                    .mapNotNull { afterDamage.entities[it]?.takeIf(Entity::alive)?.kind }
                    .distinct()
                val combatKinds = state.combat.order
                    .mapNotNull { state.entities[it]?.kind }
                    .distinct()
                if (combatKinds.size >= 2 && aliveKinds.size < 2) events += CombatEnded
            }
            return Decision.Ok(events)
        }

        is Heal -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto; cura não ressuscita (use Revive com justificativa)")
            val res = ent.resources[cmd.resource] ?: return no("recurso inexistente: ${cmd.resource}")
            if (cmd.amount < 0) return no("cura negativa")
            val newValue = minOf(res.max, res.current + cmd.amount)
            return Decision.Ok(listOf(ResourceChanged(ent.id, cmd.resource, newValue, newValue - res.current)))
        }

        is SpendResource -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            val res = ent.resources[cmd.resource] ?: return no("recurso inexistente: ${cmd.resource}")
            if (cmd.cost < 0) return no("custo negativo")
            if (res.current < cmd.cost) return no("recurso insuficiente: ${cmd.resource} ${res.current}/${cmd.cost}")
            return Decision.Ok(listOf(ResourceChanged(ent.id, cmd.resource, res.current - cmd.cost, -cmd.cost)))
        }

        is AddItem -> {
            if (entity(cmd.ownerId) == null) return no("dono inexistente: ${cmd.ownerId}")
            if (cmd.qty <= 0) return no("quantidade inválida")
            return Decision.Ok(listOf(ItemChanged(cmd.ownerId, cmd.itemId, cmd.qty)))
        }

        is ConsumeItem -> {
            val ent = entity(cmd.ownerId) ?: return no("dono inexistente: ${cmd.ownerId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.qty <= 0) return no("quantidade inválida")
            val have = state.inventory[cmd.ownerId]?.get(cmd.itemId) ?: 0
            if (have < cmd.qty) return no("item ausente ou insuficiente: ${cmd.itemId} ($have/${cmd.qty})")
            return Decision.Ok(listOf(ItemChanged(cmd.ownerId, cmd.itemId, -cmd.qty)))
        }

        is EquipItem -> {
            val ent = entity(cmd.ownerId) ?: return no("dono inexistente: ${cmd.ownerId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.itemId.isBlank() || cmd.slot.isBlank()) return no("item e slot são obrigatórios")
            val have = state.inventory[cmd.ownerId]?.get(cmd.itemId) ?: 0
            if (have <= 0) return no("item ausente: ${cmd.itemId}")
            if (state.equipped[cmd.ownerId]?.containsKey(cmd.slot) == true) return no("slot ocupado: ${cmd.slot}")
            return Decision.Ok(listOf(EquipmentChanged(cmd.ownerId, cmd.slot, cmd.itemId)))
        }

        is UnequipItem -> {
            if (entity(cmd.ownerId) == null) return no("dono inexistente: ${cmd.ownerId}")
            if (cmd.slot.isBlank()) return no("slot vazio")
            if (state.equipped[cmd.ownerId]?.containsKey(cmd.slot) != true) return no("slot vazio: ${cmd.slot}")
            return Decision.Ok(listOf(EquipmentChanged(cmd.ownerId, cmd.slot, null)))
        }

        is GainXp -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.amount <= 0) return no("XP inválido")
            val events = mutableListOf<Event>(XpGained(ent.id, cmd.amount))
            val newLevel = config.levelFor(ent.xp + cmd.amount)
            if (newLevel > ent.level) events += LevelGained(ent.id, newLevel)
            return Decision.Ok(events)
        }

        is Revive -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (ent.alive) return no("${ent.name} não está morto")
            if (cmd.reason.isBlank()) return no("ressurreição exige justificativa")
            val events = mutableListOf<Event>(EntityRevived(ent.id, cmd.reason))
            val dr = config.deathResource
            if (dr != null && ent.resources.containsKey(dr)) {
                val cur = ent.resources.getValue(dr).current
                events += ResourceChanged(ent.id, dr, 1, 1 - cur)
            }
            return Decision.Ok(events)
        }

        is LearnKnowledge -> {
            if (cmd.key.isBlank()) return no("chave vazia")
            if (cmd.source.isBlank()) return no("conhecimento exige fonte (pista, teste, conversa)")
            val old = state.knowledge[cmd.key]
            if (old != null && !old.believed && cmd.believed)
                return no("${cmd.key} já é fato confirmado; não pode voltar a ser só acreditado")
            return Decision.Ok(listOf(KnowledgeGained(cmd.key, cmd.text, cmd.source, cmd.believed)))
        }

        is MakePromise -> {
            val npc = entity(cmd.npcId) ?: return no("entidade inexistente: ${cmd.npcId}")
            if (!npc.alive) return no("${npc.name} está morto")
            if (cmd.id.isBlank() || cmd.text.isBlank()) return no("promessa exige id e texto")
            if (state.promises.containsKey(cmd.id)) return no("promessa já existe: ${cmd.id}")
            return Decision.Ok(listOf(PromiseMade(cmd.id, cmd.npcId, cmd.text)))
        }

        is KeepPromise -> {
            val p = state.promises[cmd.id] ?: return no("promessa inexistente: ${cmd.id}")
            if (p.kept) return no("promessa já cumprida: ${cmd.id}")
            return Decision.Ok(listOf(PromiseKept(cmd.id)))
        }

        is ChangeRelation -> {
            val a = entity(cmd.fromId) ?: return no("entidade inexistente: ${cmd.fromId}")
            val b = entity(cmd.toId) ?: return no("entidade inexistente: ${cmd.toId}")
            if (a.id == b.id) return no("relação consigo mesmo não existe")
            if (!a.alive) return no("${a.name} está morto")
            if (cmd.delta == 0) return no("mudança de relação zerada")
            if (cmd.reason.isBlank()) return no("mudança de relação exige motivo")
            val old = state.relations[relationKey(a.id, b.id)] ?: 0
            val now = (old + cmd.delta).coerceIn(-100, 100)
            if (now == old) return no("relação já está no limite")
            return Decision.Ok(listOf(RelationChanged(a.id, b.id, now - old, now, cmd.reason.trim())))
        }

        is CreateClock -> {
            if (cmd.id.isBlank() || cmd.name.isBlank()) return no("relógio exige id e nome")
            if (cmd.segments !in 2..12) return no("relógio precisa de 2 a 12 casas")
            if (state.clocks.containsKey(cmd.id)) return no("relógio já existe: ${cmd.id}")
            return Decision.Ok(listOf(ClockCreated(cmd.id, cmd.name.trim(), cmd.segments)))
        }

        is AdvanceClock -> {
            val c = state.clocks[cmd.id] ?: return no("relógio inexistente: ${cmd.id}")
            if (cmd.by <= 0) return no("avanço inválido")
            if (c.full) return no("relógio já completo: ${c.name}")
            val now = minOf(c.segments, c.filled + cmd.by)
            return Decision.Ok(listOf(ClockAdvanced(c.id, now, now - c.filled)))
        }

        is AddCondition -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.condition.isBlank()) return no("condição vazia")
            if (state.conditions[ent.id]?.contains(cmd.condition) == true) return no("${ent.name} já tem a condição ${cmd.condition}")
            if (cmd.durationRounds != null && cmd.durationRounds <= 0) return no("duração de condição inválida")
            if (cmd.damagePerRound < 0) return no("dano de condição negativo")
            if (cmd.damagePerRound > 0 && ent.resources[cmd.damageResource] == null) {
                return no("recurso inexistente: ${cmd.damageResource}")
            }
            return Decision.Ok(listOf(ConditionAdded(
                ent.id, cmd.condition, cmd.durationRounds, cmd.damagePerRound, cmd.damageResource
            )))
        }

        is RemoveCondition -> {
            if (entity(cmd.entityId) == null) return no("entidade inexistente: ${cmd.entityId}")
            if (state.conditions[cmd.entityId]?.contains(cmd.condition) != true) return no("condição ausente: ${cmd.condition}")
            return Decision.Ok(listOf(ConditionRemoved(cmd.entityId, cmd.condition)))
        }

        is StartCombat -> {
            if (state.combat != null) return no("já existe um combate ativo")
            if (cmd.order.isEmpty()) return no("combate sem participantes")
            if (cmd.order.toSet().size != cmd.order.size) return no("participante repetido na iniciativa")
            for (id in cmd.order) {
                val ent = entity(id) ?: return no("entidade inexistente: $id")
                if (!ent.alive) return no("${ent.name} está morto e não pode entrar no combate")
            }
            return Decision.Ok(listOf(CombatStarted(cmd.order)))
        }

        NextTurn -> {
            val c = state.combat ?: return no("não há combate ativo")
            val n = c.order.size
            for (step in 1..n) {
                val idx = (c.turnIndex + step) % n
                if (entity(c.order[idx])?.alive == true) {
                    val wrapped = c.turnIndex + step >= n
                    val events = mutableListOf<Event>(TurnAdvanced(idx, if (wrapped) c.round + 1 else c.round))
                    if (wrapped) {
                        var tickState = state
                        for ((entityId, conditions) in state.conditionStates) {
                            for ((condition, effect) in conditions) {
                                val ent = tickState.entities[entityId] ?: continue
                                if (!ent.alive) continue
                                val resource = ent.resources[effect.damageResource] ?: continue
                                val newValue = maxOf(0, resource.current - effect.damagePerRound)
                                val remaining = effect.remainingRounds?.let { maxOf(0, it - 1) }
                                val tick = ConditionTicked(
                                    entityId, condition, remaining, effect.damageResource,
                                    newValue, resource.current - newValue
                                )
                                events += tick
                                tickState = tickState.apply(tick)
                                if (newValue == 0 && config.deathResource == effect.damageResource) {
                                    val death = EntityDied(entityId)
                                    events += death
                                    tickState = tickState.apply(death)
                                }
                            }
                        }
                    }
                    return Decision.Ok(events)
                }
            }
            return no("ninguém vivo no combate")
        }

        EndCombat ->
            return if (state.combat == null) no("não há combate ativo") else Decision.Ok(listOf(CombatEnded))

        is RecordFact ->
            return if (cmd.key.isBlank()) no("chave vazia") else Decision.Ok(listOf(FactRecorded(cmd.key, cmd.value)))
    }
}
