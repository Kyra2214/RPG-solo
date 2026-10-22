package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** Fatos já validados e confirmados. O log de eventos só cresce (append-only). */
@Serializable
sealed interface Event

@Serializable
data class EntityCreated(val entity: Entity) : Event
@Serializable
data class ResourceChanged(val entityId: String, val resource: String, val newValue: Int, val delta: Int) : Event
/** [uses] é o total já gasto do traço depois deste uso (M-01: traços com usos limitados por cena/descanso). */
@Serializable
data class TraitUseSpent(val entityId: String, val traitId: String, val uses: Int) : Event
/** Zera os usos gastos de [traitIds] (vazio = todos os traços da entidade), por exemplo ao descansar ou trocar de cena. */
@Serializable
data class TraitUsesReset(val entityId: String, val traitIds: List<String> = emptyList()) : Event
@Serializable
data class EntityDied(val entityId: String) : Event
/** M-04: a entidade caiu (continua viva). */
@Serializable
data class EntityDowned(val entityId: String) : Event
/** M-04: falha em teste de sobrevivência; [failures] é o total acumulado desde a queda. */
@Serializable
data class SurvivalFailed(val entityId: String, val failures: Int) : Event
/** M-04: a entidade caída voltou a ter o recurso de morte acima de 0. */
@Serializable
data class EntityRecovered(val entityId: String) : Event
@Serializable
data class EntityRevived(val entityId: String, val reason: String) : Event
@Serializable
data class ItemChanged(val ownerId: String, val itemId: String, val delta: Int) : Event
@Serializable
data class EquipmentChanged(val ownerId: String, val slot: String, val itemId: String?) : Event
@Serializable
data class XpGained(val entityId: String, val amount: Int) : Event
@Serializable
data class LevelGained(
    val entityId: String,
    val level: Int,
    val pointsGranted: Int = 0,
    val statPointsGranted: Int = 0,
    val abilityPointsGranted: Int = 0
) : Event
/** MU-07.3: magia aprendida. */
@Serializable
data class SpellLearned(val entityId: String, val spellId: String) : Event
/** MU-06.3: habilidade aprendida. */
@Serializable
data class AbilityLearned(val entityId: String, val abilityId: String, val cost: Int) : Event
/** MU-06.3: habilidade ativada; substitui os efeitos anteriores da mesma habilidade. */
@Serializable
data class AbilityActivated(val entityId: String, val abilityId: String, val effects: List<ActiveEffect>) : Event
/** MU-06.3: fim de rodada dos efeitos temporários. */
@Serializable
object EffectsRoundEnded : Event
/** MU-06.2: nível 2 de uma perícia liberado. */
@Serializable
data class SkillMastered(val entityId: String, val skillId: String) : Event
/** MU-05.2: pontos de status gastos em atributos, sem teto. */
@Serializable
data class StatPointsAllocated(val entityId: String, val increases: Map<String, Int>, val pointsSpent: Int) : Event
@Serializable
data class AdvancementAllocated(
    val entityId: String,
    val statIncreases: Map<String, Int>,
    val skillIncreases: Map<String, Int>,
    val pointsSpent: Int
) : Event
@Serializable
data class FactRecorded(val key: String, val value: String) : Event
/** I-02.1: nota curta de "o que importa lembrar" sobre um NPC ou lugar. */
@Serializable
data class MemoryNoted(val subjectId: String, val text: String) : Event
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
data class ClockCreated(val id: String, val name: String, val segments: Int, val publicClue: String = "", val restTick: Int = 0, val moveTick: Int = 0, val chapterTick: Int = 0, val midClue: String = "", val consequence: String = "") : Event
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
/** DC-05.2: modo de uso do documento-fonte da campanha. */
@Serializable
data class SourceModeSet(val mode: SourceMode) : Event
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
/** MU-02.4: registro auditável de um confronto do motor unificado (origens, expressões, rolagens, defesa, dano). */
@Serializable
data class ClashRecorded(
    val attackerId: String,
    val targetId: String,
    val attackValue: Int,
    val attackExpression: String,
    val attackTotal: Int,
    val defenseValue: Int,
    val defenseExpression: String,
    val defenseTotal: Int,
    val critical: Boolean,
    val ignoredDefense: Boolean,
    val damage: Int,
    val attackSources: List<String>,
    val defenseSources: List<String>,
    val rejectedSources: List<String>,
    /** Faces dos dados realmente rolados (vazio quando a rolagem foi pulada). */
    val attackDice: List<Int> = emptyList(),
    val defenseDice: List<Int> = emptyList(),
    val critThreshold: Int? = null,
    val critRoll: Int? = null,
    val critGuaranteed: Boolean = false,
    val ignoreThreshold: Int? = null,
    val ignoreRoll: Int? = null,
    val ignoreGuaranteed: Boolean = false
) : Event
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
            entities = entities + (e.entityId to entity(e.entityId).copy(alive = false, downed = false, deathFailures = 0)),
            combat = combat?.copy(defending = combat.defending - e.entityId)
        )
        is EntityDowned -> copy(
            entities = entities + (e.entityId to entity(e.entityId).copy(downed = true, deathFailures = 0)),
            combat = combat?.copy(defending = combat.defending - e.entityId)
        )
        is SurvivalFailed -> copy(entities = entities + (e.entityId to entity(e.entityId).copy(deathFailures = e.failures)))
        is EntityRecovered -> copy(entities = entities + (e.entityId to entity(e.entityId).copy(downed = false, deathFailures = 0)))
        is EntityRevived -> copy(entities = entities + (e.entityId to entity(e.entityId).copy(alive = true, downed = false, deathFailures = 0)))
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
        is LevelGained -> {
            val current = entity(e.entityId)
            copy(entities = entities + (e.entityId to current.copy(
                level = e.level,
                advancementPoints = current.advancementPoints + e.pointsGranted,
                statPoints = current.statPoints + e.statPointsGranted,
                abilityPoints = current.abilityPoints + e.abilityPointsGranted
            )))
        }
        is SpellLearned -> {
            val current = entity(e.entityId)
            copy(entities = entities + (e.entityId to current.copy(spells = current.spells + e.spellId)))
        }
        is AbilityLearned -> {
            val current = entity(e.entityId)
            copy(entities = entities + (e.entityId to current.copy(
                abilities = current.abilities + e.abilityId,
                abilityPoints = current.abilityPoints - e.cost
            )))
        }
        is AbilityActivated -> {
            val source = "habilidade:${e.abilityId}"
            val kept = activeEffects[e.entityId].orEmpty().filter { it.sourceId != source && it.sourceId != "$source:desvantagem" }
            copy(activeEffects = activeEffects + (e.entityId to (kept + e.effects)))
        }
        is EffectsRoundEnded -> copy(activeEffects = activeEffects.mapValues { (_, list) ->
            list.map { it.copy(remainingRounds = it.remainingRounds - 1) }.filter { it.remainingRounds > 0 }
        }.filterValues { it.isNotEmpty() })
        is SkillMastered -> {
            val current = entity(e.entityId)
            copy(entities = entities + (e.entityId to current.copy(masteredSkills = current.masteredSkills + e.skillId)))
        }
        is StatPointsAllocated -> {
            val current = entity(e.entityId)
            copy(entities = entities + (e.entityId to current.copy(
                stats = current.stats + e.increases.mapValues { (key, increase) -> (current.stats[key] ?: 0) + increase },
                statPoints = current.statPoints - e.pointsSpent
            )))
        }
        is AdvancementAllocated -> {
            val current = entity(e.entityId)
            copy(entities = entities + (e.entityId to current.copy(
                stats = current.stats + e.statIncreases.mapValues { (key, increase) -> (current.stats[key] ?: 0) + increase },
                skills = current.skills + e.skillIncreases.mapValues { (key, increase) -> (current.skills[key] ?: 0) + increase },
                traits = (current.traits + e.skillIncreases.keys).distinct(),
                advancementPoints = current.advancementPoints - e.pointsSpent
            )))
        }
        is TraitUseSpent -> copy(traitUses = traitUses + (e.entityId to ((traitUses[e.entityId] ?: emptyMap()) + (e.traitId to e.uses))))
        is TraitUsesReset -> {
            val current = traitUses[e.entityId].orEmpty()
            val kept = if (e.traitIds.isEmpty()) emptyMap() else current - e.traitIds.toSet()
            copy(traitUses = if (kept.isEmpty()) traitUses - e.entityId else traitUses + (e.entityId to kept))
        }
        is FactRecorded -> copy(facts = facts + (e.key to e.value))
        is MemoryNoted -> copy(memories = memories + (e.subjectId to (memories[e.subjectId].orEmpty() + e.text).takeLast(MEMORY_NOTES_PER_SUBJECT)))
        is KnowledgeGained -> copy(knowledge = knowledge + (e.key to Knowledge(e.key, e.text, e.source, e.believed)))
        is PromiseMade -> copy(promises = promises + (e.id to Promise(e.id, e.npcId, e.text)))
        is PromiseKept -> {
            val p = requireNotNull(promises[e.id]) { "promessa inexistente: ${e.id}" }
            copy(promises = promises + (e.id to p.copy(kept = true)))
        }
        is RelationChanged -> copy(relations = relations + (relationKey(e.fromId, e.toId) to e.value))
        is ClockCreated -> copy(clocks = clocks + (e.id to Clock(e.id, e.name, e.segments, publicClue = e.publicClue, restTick = e.restTick, moveTick = e.moveTick, chapterTick = e.chapterTick, midClue = e.midClue, consequence = e.consequence)))
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
        is SourceModeSet -> copy(sourceMode = e.mode)
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
        is ClashRecorded -> this
    }
    return next.copy(eventCount = eventCount + 1)
}

/** Reconstrói o estado a partir do log. */
fun replay(events: List<Event>, initial: GameState = GameState()): GameState =
    events.fold(initial) { s, e -> s.apply(e) }
