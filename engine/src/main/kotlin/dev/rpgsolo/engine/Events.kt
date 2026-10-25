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

/** N-18: [days] dias passam; pergaminhos vencidos caem. */
@Serializable
data class DaysAdvanced(val days: Int) : Event

/** N-18: pergaminho de elemento aplicado à peça [itemId] no [slot] de [ownerId] por [days] dias. */
@Serializable
data class ScrollEnchantApplied(val ownerId: String, val slot: String, val itemId: String, val element: ElementPath, val days: Int) : Event

/** N-36: marca de Tatuador aplicada (nova ou cobrindo a de [coverIndex]); [maxDelta] ajusta o máximo de PV. */
@Serializable
data class TattooApplied(
    val ownerId: String,
    val scope: String,
    val grade: Int,
    val coverIndex: Int? = null,
    val maxDelta: Map<String, Int> = emptyMap(),
) : Event

/** N-33.4: gema engastada em [itemId] (peça equipada em [slot]); [maxDelta] ajusta o máximo de PV. */
@Serializable
data class GemSocketed(
    val ownerId: String,
    val slot: String,
    val itemId: String,
    val gem: Gem,
    val maxDelta: Map<String, Int> = emptyMap(),
) : Event

/** N-33.4: gema da posição [index] removida da peça; a gema não se destrói. */
@Serializable
data class GemRemoved(
    val ownerId: String,
    val slot: String,
    val itemId: String,
    val index: Int,
    val maxDelta: Map<String, Int> = emptyMap(),
) : Event

/**
 * [killerId]/[cause] (LG-01): quem ou o que causou a morte; ambos null = ambiente ou origem desconhecida.
 * [cause] só é preenchida quando não há um autor (ex.: `condicao:veneno`). Logs antigos decodificam com null.
 */
@Serializable
data class EntityDied(val entityId: String, val killerId: String? = null, val cause: String? = null) : Event

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

/**
 * M-04: o jogador assumiu [npcId] depois da morte de [fallenHeroId]. O NPC passa a ser o PLAYER (ficha mantida);
 * o herói morto vira entidade NPC (corpo), com inventário e equipamento intactos, para a recuperação de itens.
 */
@Serializable
data class NpcAssumed(val npcId: String, val fallenHeroId: String) : Event

/** M-04: a campanha terminou (game over); o app a arquiva. */
@Serializable
data class CampaignEnded(val reason: String) : Event

@Serializable
data class ItemChanged(val ownerId: String, val itemId: String, val delta: Int) : Event

@Serializable
data class EquipmentChanged(
    val ownerId: String,
    val slot: String,
    val itemId: String?,
    val maxDelta: Map<String, Int> = emptyMap(),
) : Event

@Serializable
data class XpGained(val entityId: String, val amount: Int) : Event

@Serializable
data class LevelGained(
    val entityId: String,
    val level: Int,
    val pointsGranted: Int = 0,
    val statPointsGranted: Int = 0,
    val abilityPointsGranted: Int = 0,
) : Event

/** MU-07.3: magia aprendida. */
@Serializable
data class SpellLearned(val entityId: String, val spellId: String) : Event

/** MU-06.3: habilidade aprendida. */
@Serializable
data class AbilityLearned(val entityId: String, val abilityId: String, val cost: Int) : Event

/** Evolução da árvore; registra investimento real e origem gratuita. */
@Serializable
data class SkillTreeProgressed(
    val entityId: String,
    val skillId: String,
    val newLevel: Int,
    val pointsSpent: Int,
    val freeBase: Boolean,
) : Event

/** Distribuição inicial única (N-02): 10 pontos de status entre os atributos + 2 pontos de habilidade (valores do pacote). */
@Serializable
data class InitialBuildAllocated(
    val entityId: String,
    val statIncreases: Map<String, Int>,
    val skillPointsGranted: Int,
) : Event

/** Loadout de árvores e afinidade usado pela resolução mecânica da campanha. */
@Serializable
data class SkillTreeLoadoutSet(
    val entityId: String,
    val treeIds: Set<String>,
    val classOrder: List<String>,
    val elementalAffinity: String,
) : Event

/** Pergaminho de habilidade resgatado como recompensa de missão concluída. */
@Serializable
data class QuestScrollClaimed(
    val entityId: String,
    val questId: String,
    val skillId: String,
    val scrollId: String,
) : Event

/** Maldição aplicada a uma entidade por personagem, criatura, ritual ou artefato. */
@Serializable
data class CurseApplied(
    val targetId: String,
    val curseId: String,
    val sourceId: String,
    val remainingRounds: Int? = null,
) : Event

@Serializable
data class CurseRemoved(val targetId: String, val curseId: String, val reason: String) : Event

/** Efeito de habilidade aplicado pelo usuário da habilidade a outro combatente. */
@Serializable
data class TargetSkillEffectsApplied(
    val actorId: String,
    val targetId: String,
    val skillId: String,
    val effects: List<ActiveEffect>,
) : Event

/** N-29: efeitos de uma habilidade de apoio aplicados a todos os [targetIds] (aliados em combate) de uma vez. */
@Serializable
data class AllySkillEffectsApplied(
    val actorId: String,
    val targetIds: List<String>,
    val skillId: String,
    val effects: List<ActiveEffect>,
) : Event

/** Reset de árvore: devolve exatamente os pontos investidos e mantém habilidades básicas no nível 1. */
@Serializable
data class SkillTreeProgressReset(val entityId: String, val pointsRefunded: Int) : Event

/** MU-06.3: habilidade ativada; substitui os efeitos anteriores da mesma habilidade. */
@Serializable
data class AbilityActivated(val entityId: String, val abilityId: String, val effects: List<ActiveEffect>) : Event

/** MU-02.6: escudo de [entityId] passa a valer [newValue] (ganho ou absorção de dano). */
@Serializable
data class ShieldChanged(val entityId: String, val newValue: Int) : Event

/** MU-02.5: o "ignorar defesa" de [entityId] foi usado e fica em recarga por [rounds] rodadas. */
@Serializable
data class IgnoreDefenseCooldownStarted(val entityId: String, val rounds: Int) : Event

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
    val pointsSpent: Int,
) : Event

@Serializable
data class FactRecorded(val key: String, val value: String) : Event

/** I-02.1: nota curta de "o que importa lembrar" sobre um NPC ou lugar. */
@Serializable
data class MemoryNoted(val subjectId: String, val text: String) : Event

/** I-02.4: substitui as notas antigas por um resumo extrativo e as notas recentes. */
@Serializable
data class MemoryRolledUp(val subjectId: String, val notes: List<String>) : Event

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
    val damageResource: String = "hp",
    val damagePercent: Int = 0,
) : Event

@Serializable
data class RelationChanged(val fromId: String, val toId: String, val delta: Int, val value: Int, val reason: String) : Event

@Serializable
data class ClockCreated(
    val id: String,
    val name: String,
    val segments: Int,
    val publicClue: String = "",
    val restTick: Int = 0,
    val moveTick: Int = 0,
    val chapterTick: Int = 0,
    val midClue: String = "",
    val consequence: String = "",
    val combatTick: Int = 0,
    val questTick: Int = 0,
) : Event

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
    val damage: Int = 0,
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
    val ignoreGuaranteed: Boolean = false,
    /** N-06: chance (%) e dado d100 do teste de acerto; nulos quando o confronto não teve teste. */
    val hitChance: Int? = null,
    val hitRoll: Int? = null,
    val missed: Boolean = false,
) : Event

@Serializable
data class AttackResolved(
    val attackerId: String,
    val targetId: String,
    val defense: Int,
    val hit: Boolean,
    val damage: Int,
) : Event

private fun GameState.entity(id: String): Entity = requireNotNull(entities[id]) { "evento aponta para entidade inexistente: $id" }

/** Reducer: estado novo = estado antigo + evento. Assume evento já validado por [decide]. */

/** N-33.4: soma [delta] ao máximo dos recursos de [ownerId] (sobe o atual junto; ao descer, o atual não passa do máximo e não zera quem está vivo). */
private fun withMaxDelta(
    entities: Map<String, Entity>,
    ownerId: String,
    delta: Map<String, Int>,
): Map<String, Entity> {
    val ent = entities[ownerId] ?: return entities
    if (delta.isEmpty()) return entities
    val resources =
        ent.resources.mapValues { (id, r) ->
            val d = delta[id] ?: 0
            if (d == 0) {
                r
            } else {
                val max = (r.max + d).coerceAtLeast(0)
                val cur = (if (d > 0) r.current + d else r.current).coerceIn(0, max)
                Resource(if (r.current > 0) cur.coerceAtLeast(1).coerceAtMost(max) else cur, max)
            }
        }
    return entities + (ent.id to ent.copy(resources = resources))
}

fun GameState.apply(e: Event): GameState {
    val next =
        when (e) {
            is EntityCreated -> copy(entities = entities + (e.entity.id to e.entity))
            is ResourceChanged -> {
                val ent = entity(e.entityId)
                val res = requireNotNull(ent.resources[e.resource]) { "recurso inexistente: ${e.resource}" }
                copy(entities = entities + (ent.id to ent.copy(resources = ent.resources + (e.resource to res.copy(current = e.newValue)))))
            }
            is EntityDied -> {
                val dying = entity(e.entityId)
                val died =
                    copy(
                        entities = entities + (e.entityId to dying.copy(alive = false, downed = false, deathFailures = 0)),
                        combat = combat?.copy(defending = combat.defending - e.entityId),
                    )
                if (dying.kind == EntityKind.PLAYER) {
                    died.copy(legacies = legacies + (dying.id to legacyOf(dying, e.killerId, e.cause)))
                } else {
                    died
                }
            }
            is EntityDowned ->
                copy(
                    entities = entities + (e.entityId to entity(e.entityId).copy(downed = true, deathFailures = 0)),
                    combat = combat?.copy(defending = combat.defending - e.entityId),
                )
            is SurvivalFailed -> copy(entities = entities + (e.entityId to entity(e.entityId).copy(deathFailures = e.failures)))
            is EntityRecovered -> copy(entities = entities + (e.entityId to entity(e.entityId).copy(downed = false, deathFailures = 0)))
            is EntityRevived ->
                copy(
                    entities = entities + (e.entityId to entity(e.entityId).copy(alive = true, downed = false, deathFailures = 0)),
                    legacies = legacies - e.entityId,
                )
            is NpcAssumed ->
                copy(
                    entities =
                        entities +
                            (e.npcId to entity(e.npcId).copy(kind = EntityKind.PLAYER)) +
                            (e.fallenHeroId to entity(e.fallenHeroId).copy(kind = EntityKind.NPC)),
                )
            is CampaignEnded -> copy(endedReason = e.reason)
            is ItemChanged -> {
                val owner = inventory[e.ownerId] ?: emptyMap()
                val qty = (owner[e.itemId] ?: 0) + e.delta
                val newOwner = if (qty <= 0) owner - e.itemId else owner + (e.itemId to qty)
                copy(inventory = inventory + (e.ownerId to newOwner))
            }
            is EquipmentChanged -> {
                val owner = equipped[e.ownerId] ?: emptyMap()
                val nextOwner = if (e.itemId == null) owner - e.slot else owner + (e.slot to e.itemId)
                val ent = entities[e.ownerId]
                val resources =
                    ent?.resources?.mapValues { (id, r) ->
                        val delta = e.maxDelta[id] ?: 0
                        if (delta == 0) {
                            r
                        } else {
                            val max = (r.max + delta).coerceAtLeast(0)
                            val cur = (if (delta > 0) r.current + delta else r.current).coerceIn(0, max)
                            Resource(if (r.current > 0) cur.coerceAtLeast(1).coerceAtMost(max) else cur, max)
                        }
                    }
                val nextEntities = if (ent != null && e.maxDelta.isNotEmpty()) entities + (ent.id to ent.copy(resources = resources!!)) else entities
                copy(
                    equipped = if (nextOwner.isEmpty()) equipped - e.ownerId else equipped + (e.ownerId to nextOwner),
                    entities = nextEntities,
                )
            }
            is XpGained -> {
                val ent = entity(e.entityId)
                copy(entities = entities + (ent.id to ent.copy(xp = ent.xp + e.amount)))
            }
            is LevelGained -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            e.entityId to
                                current.copy(
                                    level = e.level,
                                    advancementPoints = current.advancementPoints + e.pointsGranted,
                                    statPoints = current.statPoints + e.statPointsGranted,
                                    abilityPoints = current.abilityPoints + e.abilityPointsGranted,
                                )
                        ),
                )
            }
            is SpellLearned -> {
                val current = entity(e.entityId)
                copy(entities = entities + (e.entityId to current.copy(spells = current.spells + e.spellId)))
            }
            is AbilityLearned -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            e.entityId to
                                current.copy(
                                    abilities = current.abilities + e.abilityId,
                                    abilityPoints = current.abilityPoints - e.cost,
                                )
                        ),
                )
            }
            is SkillTreeProgressed -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            current.id to
                                current.copy(
                                    skillTreeLevels = current.skillTreeLevels + (e.skillId to e.newLevel),
                                    skillTreeInvestments =
                                        current.skillTreeInvestments +
                                            (e.skillId to ((current.skillTreeInvestments[e.skillId] ?: 0) + e.pointsSpent)),
                                    freeSkillIds = if (e.freeBase) current.freeSkillIds + e.skillId else current.freeSkillIds,
                                    abilityPoints = current.abilityPoints - e.pointsSpent,
                                )
                        ),
                )
            }
            is InitialBuildAllocated -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            current.id to
                                current.copy(
                                    stats =
                                        current.stats +
                                            e.statIncreases.mapValues {
                                                    (key, increase) ->
                                                (current.stats[key] ?: 0) + increase
                                            },
                                    abilityPoints = current.abilityPoints + e.skillPointsGranted,
                                    initialBuildAllocated = true,
                                )
                        ),
                )
            }
            is SkillTreeLoadoutSet -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            current.id to
                                current.copy(
                                    skillTreeTreeIds = e.treeIds,
                                    skillTreeClassOrder = e.classOrder,
                                    skillTreeElement = e.elementalAffinity,
                                )
                        ),
                )
            }
            is QuestScrollClaimed -> {
                val current = entity(e.entityId)
                val claim = "${e.questId}:${e.skillId}"
                copy(
                    entities = entities + (current.id to current.copy(claimedQuestRewards = current.claimedQuestRewards + claim)),
                    inventory = inventory + (current.id to (inventory[current.id].orEmpty() + (e.scrollId to ((inventory[current.id]?.get(e.scrollId) ?: 0) + 1)))),
                )
            }
            is CurseApplied -> {
                val current = entity(e.targetId)
                val curse = AppliedCurseState(e.curseId, e.sourceId, e.remainingRounds)
                copy(entities = entities + (current.id to current.copy(appliedCurses = current.appliedCurses + (e.curseId to curse))))
            }
            is CurseRemoved -> {
                val current = entity(e.targetId)
                copy(entities = entities + (current.id to current.copy(appliedCurses = current.appliedCurses - e.curseId)))
            }
            is TargetSkillEffectsApplied -> {
                val source = "habilidade:${e.skillId}"
                val kept = activeEffects[e.targetId].orEmpty().filter { it.sourceId != source && it.sourceId != "$source:desvantagem" }
                copy(activeEffects = activeEffects + (e.targetId to (kept + e.effects)))
            }
            is AllySkillEffectsApplied -> {
                val source = "habilidade:${e.skillId}"
                copy(
                    activeEffects =
                        activeEffects +
                            e.targetIds.associateWith { id ->
                                activeEffects[id].orEmpty().filter { it.sourceId != source && it.sourceId != "$source:desvantagem" } + e.effects
                            },
                )
            }
            is SkillTreeProgressReset -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            current.id to
                                current.copy(
                                    skillTreeLevels = current.freeSkillIds.associateWith { 1 },
                                    skillTreeInvestments = emptyMap(),
                                    abilityPoints = current.abilityPoints + e.pointsRefunded,
                                )
                        ),
                )
            }
            is AbilityActivated -> {
                val source = "habilidade:${e.abilityId}"
                val kept = activeEffects[e.entityId].orEmpty().filter { it.sourceId != source && it.sourceId != "$source:desvantagem" }
                copy(activeEffects = activeEffects + (e.entityId to (kept + e.effects)))
            }
            is ShieldChanged -> {
                val ent = entity(e.entityId)
                copy(entities = entities + (ent.id to ent.copy(shield = e.newValue)))
            }
            is IgnoreDefenseCooldownStarted -> copy(ignoreCooldowns = ignoreCooldowns + (e.entityId to e.rounds))
            is EffectsRoundEnded ->
                copy(
                    ignoreCooldowns = ignoreCooldowns.mapValues { it.value - 1 }.filterValues { it > 0 },
                    activeEffects =
                        activeEffects.mapValues { (_, list) ->
                            list.map { it.copy(remainingRounds = it.remainingRounds - 1) }.filter { it.remainingRounds > 0 }
                        }.filterValues { it.isNotEmpty() },
                    entities =
                        entities.mapValues { (_, current) ->
                            current.copy(
                                appliedCurses =
                                    current.appliedCurses.mapNotNull { (id, curse) ->
                                        val remaining = curse.remainingRounds
                                        when {
                                            remaining == null -> id to curse
                                            remaining > 1 -> id to curse.copy(remainingRounds = remaining - 1)
                                            else -> null
                                        }
                                    }.toMap(),
                            )
                        },
                )
            is SkillMastered -> {
                val current = entity(e.entityId)
                copy(entities = entities + (e.entityId to current.copy(masteredSkills = current.masteredSkills + e.skillId)))
            }
            is StatPointsAllocated -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            e.entityId to
                                current.copy(
                                    stats =
                                        current.stats +
                                            e.increases.mapValues {
                                                    (key, increase) ->
                                                (current.stats[key] ?: 0) + increase
                                            },
                                    statPoints = current.statPoints - e.pointsSpent,
                                )
                        ),
                )
            }
            is AdvancementAllocated -> {
                val current = entity(e.entityId)
                copy(
                    entities =
                        entities + (
                            e.entityId to
                                current.copy(
                                    stats =
                                        current.stats +
                                            e.statIncreases.mapValues {
                                                    (key, increase) ->
                                                (current.stats[key] ?: 0) + increase
                                            },
                                    skills =
                                        current.skills +
                                            e.skillIncreases.mapValues {
                                                    (key, increase) ->
                                                (current.skills[key] ?: 0) + increase
                                            },
                                    traits = (current.traits + e.skillIncreases.keys).distinct(),
                                    advancementPoints = current.advancementPoints - e.pointsSpent,
                                )
                        ),
                )
            }
            is TraitUseSpent ->
                copy(
                    traitUses = traitUses + (e.entityId to ((traitUses[e.entityId] ?: emptyMap()) + (e.traitId to e.uses))),
                )
            is TraitUsesReset -> {
                val current = traitUses[e.entityId].orEmpty()
                val kept = if (e.traitIds.isEmpty()) emptyMap() else current - e.traitIds.toSet()
                copy(traitUses = if (kept.isEmpty()) traitUses - e.entityId else traitUses + (e.entityId to kept))
            }
            is CompanionTamed ->
                copy(
                    entities = entities + (e.entity.id to e.entity),
                    companions =
                        companions.mapValues {
                                (id, c) ->
                            if (id in e.stabledIds) c.copy(stabled = true, visitedWeek = Kennel.weekOf(day)) else c
                        } +
                            (e.entity.id to e.companion),
                    kennelWeek = kennelWeek ?: Kennel.weekOf(day),
                    relations =
                        relations +
                            (
                                relationKey(e.entity.id, e.companion.ownerId) to
                                    maxOf(relations[relationKey(e.entity.id, e.companion.ownerId)] ?: 0, ALLY_RELATION_MIN)
                            ),
                )
            is CompanionXpGained -> {
                val animal = entities.getValue(e.animalId)
                copy(
                    entities = entities + (e.animalId to animal.copy(xp = animal.xp + e.amount, level = e.newLevel, abilityPoints = animal.abilityPoints + e.abilityPointsGained)),
                    companions = companions + (e.animalId to companions.getValue(e.animalId).copy(loyalty = e.newLoyalty)),
                )
            }
            is CompanionAttackChosen ->
                copy(companions = companions + (e.animalId to companions.getValue(e.animalId).copy(attackId = e.attackId)))
            is CompanionAttackRaised -> {
                val animal = entities.getValue(e.animalId)
                copy(
                    entities = entities + (e.animalId to animal.copy(abilityPoints = animal.abilityPoints - 1)),
                    companions = companions + (e.animalId to companions.getValue(e.animalId).copy(attackLevel = e.newLevel)),
                )
            }
            is CompanionLeveledUp -> {
                val animal = entities.getValue(e.animalId)
                copy(
                    entities = entities + (e.animalId to animal.copy(level = e.newLevel, abilityPoints = animal.abilityPoints + if (e.abilityPointGained) 1 else 0)),
                    companions = companions + (e.animalId to companions.getValue(e.animalId).copy(loyalty = e.newLoyalty)),
                )
            }
            is CompanionTrickLearnAttempted -> {
                val current = companions.getValue(e.animalId)
                copy(
                    companions =
                        companions +
                            (
                                e.animalId to
                                    current.copy(tricks = if (e.success) current.tricks + e.trickId else current.tricks, lastLearnDay = e.day)
                            ),
                )
            }
            is CompanionTrickOrdered -> this
            is CompanionStabled ->
                copy(companions = companions + (e.animalId to companions.getValue(e.animalId).copy(stabled = true, visitedWeek = e.week)))
            is CompanionBrought ->
                copy(
                    companions =
                        companions.mapValues { (id, c) ->
                            when {
                                id == e.animalId -> c.copy(stabled = false)
                                id in e.stabledIds -> c.copy(stabled = true, visitedWeek = e.week)
                                else -> c
                            }
                        },
                )
            is CompanionRetrained -> {
                val current = companions.getValue(e.animalId)
                if (e.success) {
                    copy(
                        companions =
                            companions.mapValues { (id, c) ->
                                when {
                                    id == e.animalId ->
                                        c.copy(
                                            lost = false,
                                            stabled = false,
                                            loyalty = e.newLoyalty,
                                            retrainAttempts = 0,
                                            visitedWeek = e.week,
                                        )
                                    id in e.stabledIds -> c.copy(stabled = true, visitedWeek = e.week)
                                    else -> c
                                }
                            },
                        relations =
                            relations +
                                (
                                    relationKey(e.animalId, current.ownerId) to
                                        maxOf(relations[relationKey(e.animalId, current.ownerId)] ?: 0, ALLY_RELATION_MIN)
                                ),
                    )
                } else {
                    copy(companions = companions + (e.animalId to current.copy(retrainAttempts = e.attempt, abandoned = e.abandoned)))
                }
            }
            is TrainerBonusSynced -> {
                var nextEntities = entities
                var nextCompanions = companions
                for ((id, wanted) in e.bonus) {
                    val c = nextCompanions[id] ?: continue
                    val ent = nextEntities[id]
                    if (ent != null) {
                        val stats = ent.stats.toMutableMap()
                        for (stat in c.trainerBonus.keys + wanted.keys) {
                            val delta = (wanted[stat] ?: 0) - (c.trainerBonus[stat] ?: 0)
                            if (delta != 0) stats[stat] = (stats[stat] ?: 0) + delta
                        }
                        nextEntities = nextEntities + (id to ent.copy(stats = stats))
                    }
                    nextCompanions = nextCompanions + (id to c.copy(trainerBonus = wanted))
                }
                copy(entities = nextEntities, companions = nextCompanions)
            }
            is CompanionVisited ->
                copy(
                    companions =
                        companions +
                            (e.animalId to companions.getValue(e.animalId).copy(loyalty = e.newValue, lastPetDay = e.day, visitedWeek = e.week)),
                )
            is KennelWeekSettled -> {
                var nextCompanions = companions
                var nextRelations = relations
                for (change in e.changes) {
                    val current = nextCompanions.getValue(change.animalId)
                    val gone = change.animalId in e.lost
                    nextCompanions = nextCompanions + (change.animalId to current.copy(loyalty = change.newValue, lost = current.lost || gone))
                    if (gone) nextRelations = nextRelations - relationKey(change.animalId, current.ownerId)
                }
                copy(companions = nextCompanions, relations = nextRelations, kennelWeek = e.week)
            }
            is CompanionLoyaltyChanged -> {
                val current = companions.getValue(e.animalId)
                copy(
                    companions = companions + (e.animalId to current.copy(loyalty = e.newValue, lastPetDay = e.petDay ?: current.lastPetDay)),
                )
            }
            is DaysAdvanced -> {
                val now = day + e.days
                val kept =
                    scrollEnchants.mapValues { (_, bySlot) -> bySlot.filterValues { it.expiresDay > now } }.filterValues { it.isNotEmpty() }
                copy(day = now, scrollEnchants = kept)
            }
            is ScrollEnchantApplied ->
                copy(
                    scrollEnchants =
                        scrollEnchants +
                            (e.ownerId to ((scrollEnchants[e.ownerId] ?: emptyMap()) + (e.slot to ScrollEnchant(e.itemId, e.element, day + e.days)))),
                )
            is TattooApplied -> {
                val marks = tattoos[e.ownerId].orEmpty()
                val mark = Tattoo(e.scope, e.grade)
                val nextMarks = if (e.coverIndex == null) marks + mark else marks.mapIndexed { i, m -> if (i == e.coverIndex) mark else m }
                val ent = entities[e.ownerId]
                val nextEntities =
                    if (ent != null && e.maxDelta.isNotEmpty()) {
                        val resources =
                            ent.resources.mapValues { (id, r) ->
                                val delta = e.maxDelta[id] ?: 0
                                if (delta == 0) {
                                    r
                                } else {
                                    val max = (r.max + delta).coerceAtLeast(0)
                                    val cur = (if (delta > 0) r.current + delta else r.current).coerceIn(0, max)
                                    Resource(if (r.current > 0) cur.coerceAtLeast(1).coerceAtMost(max) else cur, max)
                                }
                            }
                        entities + (ent.id to ent.copy(resources = resources))
                    } else {
                        entities
                    }
                copy(tattoos = tattoos + (e.ownerId to nextMarks), entities = nextEntities)
            }
            is GemSocketed -> {
                val bySlot = gems[e.ownerId].orEmpty()
                val old = bySlot[e.slot]?.takeIf { it.itemId == e.itemId }?.gems.orEmpty()
                val next = bySlot + (e.slot to SocketedGems(e.itemId, old + e.gem))
                copy(gems = gems + (e.ownerId to next), entities = withMaxDelta(entities, e.ownerId, e.maxDelta))
            }
            is GemRemoved -> {
                val bySlot = gems[e.ownerId].orEmpty()
                val old = bySlot[e.slot]?.takeIf { it.itemId == e.itemId }?.gems.orEmpty()
                val left = old.filterIndexed { i, _ -> i != e.index }
                val next = if (left.isEmpty()) bySlot - e.slot else bySlot + (e.slot to SocketedGems(e.itemId, left))
                copy(
                    gems = if (next.isEmpty()) gems - e.ownerId else gems + (e.ownerId to next),
                    entities = withMaxDelta(entities, e.ownerId, e.maxDelta),
                )
            }
            is FactRecorded -> copy(facts = facts + (e.key to e.value))
            is MemoryNoted ->
                copy(
                    memories = memories + (e.subjectId to (memories[e.subjectId].orEmpty() + e.text).takeLast(MEMORY_NOTES_PER_SUBJECT)),
                )
            is MemoryRolledUp -> copy(memories = memories + (e.subjectId to e.notes))
            is KnowledgeGained -> copy(knowledge = knowledge + (e.key to Knowledge(e.key, e.text, e.source, e.believed)))
            is PromiseMade -> copy(promises = promises + (e.id to Promise(e.id, e.npcId, e.text)))
            is PromiseKept -> {
                val p = requireNotNull(promises[e.id]) { "promessa inexistente: ${e.id}" }
                copy(promises = promises + (e.id to p.copy(kept = true)))
            }
            is RelationChanged -> copy(relations = relations + (relationKey(e.fromId, e.toId) to e.value))
            is ClockCreated ->
                copy(
                    clocks = clocks + (e.id to Clock(e.id, e.name, e.segments, publicClue = e.publicClue, restTick = e.restTick, moveTick = e.moveTick, chapterTick = e.chapterTick, midClue = e.midClue, consequence = e.consequence, combatTick = e.combatTick, questTick = e.questTick)),
                )
            is ClockAdvanced -> {
                val c = requireNotNull(clocks[e.id]) { "relógio inexistente: ${e.id}" }
                copy(clocks = clocks + (e.id to c.copy(filled = e.filled)))
            }
            is ConditionAdded ->
                copy(
                    conditions = conditions + (e.entityId to ((conditions[e.entityId] ?: emptySet()) + e.condition)),
                    conditionStates =
                        conditionStates + (
                            e.entityId to (
                                (conditionStates[e.entityId] ?: emptyMap()) +
                                    (e.condition to ConditionState(e.durationRounds, e.damagePerRound, e.damageResource, e.damagePercent))
                            )
                        ),
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
            is PlaceAdded ->
                copy(
                    places = places + (e.place.id to e.place),
                    placeLinks = placeLinks + (e.place.id to emptySet()),
                )
            is PlacesConnected ->
                copy(
                    placeLinks =
                        placeLinks +
                            (e.fromId to (placeLinks[e.fromId].orEmpty() + e.toId)) +
                            (e.toId to (placeLinks[e.toId].orEmpty() + e.fromId)),
                )
            is PlaceDiscovered ->
                copy(
                    knownPlaces = knownPlaces + e.id,
                    currentPlaceId = currentPlaceId ?: e.id,
                )
            is PlayerMovedToPlace ->
                copy(
                    currentPlaceId = e.id,
                    activeEffects =
                        activeEffects.mapValues { (_, list) -> list.filterNot { it.untilSceneEnd } }.filterValues { it.isNotEmpty() },
                )
            is ConditionRemoved -> {
                val left = (conditions[e.entityId] ?: emptySet()) - e.condition
                val states = (conditionStates[e.entityId] ?: emptyMap()) - e.condition
                copy(
                    conditions = if (left.isEmpty()) conditions - e.entityId else conditions + (e.entityId to left),
                    conditionStates = if (states.isEmpty()) conditionStates - e.entityId else conditionStates + (e.entityId to states),
                )
            }
            is ConditionTicked -> {
                val left =
                    if (e.remainingRounds == 0) {
                        (conditions[e.entityId] ?: emptySet()) - e.condition
                    } else {
                        conditions[e.entityId] ?: emptySet()
                    }
                val oldStates = conditionStates[e.entityId] ?: emptyMap()
                val nextStates =
                    if (e.remainingRounds == 0) {
                        oldStates - e.condition
                    } else {
                        val old = requireNotNull(oldStates[e.condition])
                        oldStates + (e.condition to old.copy(remainingRounds = e.remainingRounds))
                    }
                val ent = entity(e.entityId)
                val changed =
                    if (e.damageResource != null && e.newResourceValue != null) {
                        val resource = requireNotNull(ent.resources[e.damageResource])
                        ent.copy(resources = ent.resources + (e.damageResource to resource.copy(current = e.newResourceValue)))
                    } else {
                        ent
                    }
                copy(
                    entities = entities + (e.entityId to changed),
                    conditions = if (left.isEmpty()) conditions - e.entityId else conditions + (e.entityId to left),
                    conditionStates = if (nextStates.isEmpty()) conditionStates - e.entityId else conditionStates + (e.entityId to nextStates),
                )
            }
            is CombatStarted -> copy(combat = CombatState(e.order))
            is TurnAdvanced -> {
                val c = requireNotNull(combat) { "sem combate ativo" }
                val nextId = c.order[e.turnIndex]
                copy(
                    combat =
                        c.copy(
                            turnIndex = e.turnIndex,
                            round = e.round,
                            defending = if (nextId in c.defending) c.defending - nextId else c.defending,
                        ),
                )
            }
            is CombatantDefended -> {
                val c = requireNotNull(combat) { "sem combate ativo" }
                copy(combat = c.copy(defending = c.defending + e.entityId))
            }
            is CombatantDefenseConsumed -> {
                val c = requireNotNull(combat) { "sem combate ativo" }
                copy(combat = c.copy(defending = c.defending - e.entityId))
            }
            CombatEnded ->
                copy(
                    combat = null,
                    activeEffects =
                        activeEffects.mapValues { (_, list) -> list.filterNot { it.untilCombatEnd } }.filterValues { it.isNotEmpty() },
                )
            is RollRecorded -> this
            is AttackResolved -> this
            is ClashRecorded -> this
        }
    return next.copy(eventCount = eventCount + 1)
}

/** Reconstrói o estado a partir do log. */
fun replay(
    events: List<Event>,
    initial: GameState = GameState(),
): GameState = events.fold(initial) { s, e -> s.apply(e) }
