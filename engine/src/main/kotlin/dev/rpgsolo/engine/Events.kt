package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** Fatos já validados e confirmados. O log de eventos só cresce (append-only). */
@Serializable
sealed interface Event

@Serializable
data class EntityCreated(val entity: Entity) : Event
@Serializable
data class ResourceChanged(val entityId: String, val resource: String, val newValue: Int, val delta: Int) : Event
@Serializable
data class EntityDied(val entityId: String) : Event
@Serializable
data class EntityRevived(val entityId: String, val reason: String) : Event
@Serializable
data class ItemChanged(val ownerId: String, val itemId: String, val delta: Int) : Event
@Serializable
data class EquipmentChanged(val ownerId: String, val slot: String, val itemId: String?) : Event
@Serializable
data class XpGained(val entityId: String, val amount: Int) : Event
@Serializable
data class LevelGained(val entityId: String, val level: Int) : Event
@Serializable
data class FactRecorded(val key: String, val value: String) : Event
@Serializable
data class KnowledgeGained(val key: String, val text: String, val source: String, val believed: Boolean) : Event
@Serializable
data class PromiseMade(val id: String, val npcId: String, val text: String) : Event
@Serializable
data class PromiseKept(val id: String) : Event
@Serializable
data class ConditionAdded(
    val entityId: String,
    val condition: String,
    val durationRounds: Int? = null,
    val damagePerRound: Int = 0,
    val damageResource: String = "hp"
) : Event
@Serializable
data class RelationChanged(val fromId: String, val toId: String, val delta: Int, val value: Int, val reason: String) : Event
@Serializable
data class ClockCreated(val id: String, val name: String, val segments: Int, val publicClue: String = "") : Event
@Serializable
data class ClockAdvanced(val id: String, val filled: Int, val delta: Int) : Event
@Serializable
data class ConditionRemoved(val entityId: String, val condition: String) : Event
@Serializable
data class NpcDossierStored(val dossier: NpcDossier) : Event
@Serializable
data class AdditionalRuleProposed(val rule: RegraAdicional) : Event
@Serializable
data class AdditionalRuleApproved(val id: String) : Event
@Serializable
data class AdditionalRuleRejected(val id: String) : Event
@Serializable
data class AmbientacaoSet(val ambientacao: Ambientacao) : Event
@Serializable
data class CampaignStyleSet(val style: CampaignStyle) : Event
@Serializable
data class PlaceAdded(val place: Place) : Event
@Serializable
data class PlacesConnected(val fromId: String, val toId: String) : Event
@Serializable
data class PlaceDiscovered(val id: String) : Event
@Serializable
data class PlayerMovedToPlace(val id: String) : Event
@Serializable
data class ConditionTicked(
    val entityId: String,
    val condition: String,
    val remainingRounds: Int?,
    val damageResource: String? = null,
    val newResourceValue: Int? = null,
    val damage: Int = 0
) : Event
@Serializable
data class CombatStarted(val order: List<String>) : Event
@Serializable
data class TurnAdvanced(val turnIndex: Int, val round: Int) : Event
@Serializable
data class CombatantDefended(val entityId: String) : Event
@Serializable
data class CombatantDefenseConsumed(val entityId: String) : Event
@Serializable
object CombatEnded : Event
@Serializable
data class RollRecorded(val label: String, val expression: String, val dice: List<Int>, val modifier: Int, val total: Int) : Event
@Serializable
data class AttackResolved(
    val attackerId: String,
    val targetId: String,
    val defense: Int,
    val hit: Boolean,
    val damage: Int
) : Event

private fun GameState.entity(id: String): Entity =
    requireNotNull(entities[id]) { "evento aponta para entidade inexistente: $id" }

/** Reducer: estado novo = estado antigo + evento. Assume evento já validado por [decide]. */
fun GameState.apply(e: Event): GameState {
    val next = when (e) {
        is EntityCreated -> copy(entities = entities + (e.entity.id to e.entity))
        is ResourceChanged -> {
            val ent = entity(e.entityId)
            val res = requireNotNull(ent.resources[e.resource]) { "recurso inexistente: ${e.resource}" }
            copy(entities = entities + (ent.id to ent.copy(resources = ent.resources + (e.resource to res.copy(current = e.newValue)))))
        }
        is EntityDied -> copy(
            entities = entities + (e.entityId to entity(e.entityId).copy(alive = false)),
            combat = combat?.copy(defending = combat.defending - e.entityId)
        )
        is EntityRevived -> copy(entities = entities + (e.entityId to entity(e.entityId).copy(alive = true)))
        is ItemChanged -> {
            val owner = inventory[e.ownerId] ?: emptyMap()
            val qty = (owner[e.itemId] ?: 0) + e.delta
            val newOwner = if (qty <= 0) owner - e.itemId else owner + (e.itemId to qty)
            copy(inventory = inventory + (e.ownerId to newOwner))
        }
        is EquipmentChanged -> {
            val owner = equipped[e.ownerId] ?: emptyMap()
            val nextOwner = if (e.itemId == null) owner - e.slot else owner + (e.slot to e.itemId)
            copy(equipped = if (nextOwner.isEmpty()) equipped - e.ownerId else equipped + (e.ownerId to nextOwner))
        }
        is XpGained -> {
            val ent = entity(e.entityId)
            copy(entities = entities + (ent.id to ent.copy(xp = ent.xp + e.amount)))
        }
        is LevelGained -> copy(entities = entities + (e.entityId to entity(e.entityId).copy(level = e.level)))
        is FactRecorded -> copy(facts = facts + (e.key to e.value))
        is KnowledgeGained -> copy(knowledge = knowledge + (e.key to Knowledge(e.key, e.text, e.source, e.believed)))
        is PromiseMade -> copy(promises = promises + (e.id to Promise(e.id, e.npcId, e.text)))
        is PromiseKept -> {
            val p = requireNotNull(promises[e.id]) { "promessa inexistente: ${e.id}" }
            copy(promises = promises + (e.id to p.copy(kept = true)))
        }
        is RelationChanged -> copy(relations = relations + (relationKey(e.fromId, e.toId) to e.value))
        is ClockCreated -> copy(clocks = clocks + (e.id to Clock(e.id, e.name, e.segments, publicClue = e.publicClue)))
        is ClockAdvanced -> {
            val c = requireNotNull(clocks[e.id]) { "relógio inexistente: ${e.id}" }
            copy(clocks = clocks + (e.id to c.copy(filled = e.filled)))
        }
        is ConditionAdded -> copy(
            conditions = conditions + (e.entityId to ((conditions[e.entityId] ?: emptySet()) + e.condition)),
            conditionStates = conditionStates + (e.entityId to ((conditionStates[e.entityId] ?: emptyMap()) +
                (e.condition to ConditionState(e.durationRounds, e.damagePerRound, e.damageResource))))
        )
        is NpcDossierStored -> copy(npcDossiers = npcDossiers + (e.dossier.npcId to e.dossier))
        is AdditionalRuleProposed -> copy(additionalRules = additionalRules + (e.rule.id to e.rule))
        is AdditionalRuleApproved -> {
            val rule = requireNotNull(additionalRules[e.id]) { "adaptação inexistente: ${e.id}" }
            copy(additionalRules = additionalRules + (e.id to rule.copy(approved = true)))
        }
        is AdditionalRuleRejected -> copy(additionalRules = additionalRules - e.id)
        is AmbientacaoSet -> copy(ambientacao = e.ambientacao)
        is CampaignStyleSet -> copy(campaignStyle = e.style)
        is PlaceAdded -> copy(
            places = places + (e.place.id to e.place),
            placeLinks = placeLinks + (e.place.id to emptySet())
        )
        is PlacesConnected -> copy(
            placeLinks = placeLinks +
                (e.fromId to (placeLinks[e.fromId].orEmpty() + e.toId)) +
                (e.toId to (placeLinks[e.toId].orEmpty() + e.fromId))
        )
        is PlaceDiscovered -> copy(
            knownPlaces = knownPlaces + e.id,
            currentPlaceId = currentPlaceId ?: e.id
        )
        is PlayerMovedToPlace -> copy(currentPlaceId = e.id)
        is ConditionRemoved -> {
            val left = (conditions[e.entityId] ?: emptySet()) - e.condition
            val states = (conditionStates[e.entityId] ?: emptyMap()) - e.condition
            copy(
                conditions = if (left.isEmpty()) conditions - e.entityId else conditions + (e.entityId to left),
                conditionStates = if (states.isEmpty()) conditionStates - e.entityId else conditionStates + (e.entityId to states)
            )
        }
        is ConditionTicked -> {
            val left = if (e.remainingRounds == 0) {
                (conditions[e.entityId] ?: emptySet()) - e.condition
            } else conditions[e.entityId] ?: emptySet()
            val oldStates = conditionStates[e.entityId] ?: emptyMap()
            val nextStates = if (e.remainingRounds == 0) oldStates - e.condition else {
                val old = requireNotNull(oldStates[e.condition])
                oldStates + (e.condition to old.copy(remainingRounds = e.remainingRounds))
            }
            val ent = entity(e.entityId)
            val changed = if (e.damageResource != null && e.newResourceValue != null) {
                val resource = requireNotNull(ent.resources[e.damageResource])
                ent.copy(resources = ent.resources + (e.damageResource to resource.copy(current = e.newResourceValue)))
            } else ent
            copy(
                entities = entities + (e.entityId to changed),
                conditions = if (left.isEmpty()) conditions - e.entityId else conditions + (e.entityId to left),
                conditionStates = if (nextStates.isEmpty()) conditionStates - e.entityId else conditionStates + (e.entityId to nextStates)
            )
        }
        is CombatStarted -> copy(combat = CombatState(e.order))
        is TurnAdvanced -> {
            val c = requireNotNull(combat) { "sem combate ativo" }
            val nextId = c.order[e.turnIndex]
            copy(combat = c.copy(
                turnIndex = e.turnIndex,
                round = e.round,
                defending = if (nextId in c.defending) c.defending - nextId else c.defending
            ))
        }
        is CombatantDefended -> {
            val c = requireNotNull(combat) { "sem combate ativo" }
            copy(combat = c.copy(defending = c.defending + e.entityId))
        }
        is CombatantDefenseConsumed -> {
            val c = requireNotNull(combat) { "sem combate ativo" }
            copy(combat = c.copy(defending = c.defending - e.entityId))
        }
        CombatEnded -> copy(combat = null)
        is RollRecorded -> this
        is AttackResolved -> this
    }
    return next.copy(eventCount = eventCount + 1)
}

/** Reconstrói o estado a partir do log. */
fun replay(events: List<Event>, initial: GameState = GameState()): GameState =
    events.fold(initial) { s, e -> s.apply(e) }
