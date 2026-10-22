package dev.rpgsolo.engine

/** Entrada derivada do log canônico. Não é persistida separadamente: replay do log recompõe a mesma linha. */
data class AutomaticTimelineEntry(
    val eventIndex: Int,
    val category: String,
    val text: String
)

/**
 * Cria uma linha do tempo só com acontecimentos que o personagem poderia conhecer.
 * Não inclui relógios em si, dossiês privados, mudanças ocultas de NPC nem texto de segredos não descobertos; só pode incluir a pista pública definida para um relógio concluído e a pista intermediária ao cruzar a metade das casas.
 * O índice é a posição do evento no log, não uma data civil.
 */
fun automaticTimeline(
    events: List<Event>,
    playerId: String,
    finalState: GameState
): List<AutomaticTimelineEntry> {
    require(playerId.isNotBlank()) { "playerId vazio" }
    val result = mutableListOf<AutomaticTimelineEntry>()
    var combatIncludesPlayer = false
    var combatants = emptySet<String>()
    // Pistas são publicadas somente na transição que cruza a metade ou completa o relógio.
    // Guardar o preenchimento visto no replay evita duplicação se um evento
    // posterior reportar novamente um relógio já cheio.
    val clockFills = mutableMapOf<String, Int>()
    events.forEachIndexed { index, event ->
        val entry = when (event) {
            is ClockCreated -> {
                clockFills[event.id] = 0
                null
            }
            is ClockAdvanced -> {
                val previousFill = clockFills[event.id] ?: 0
                clockFills[event.id] = event.filled
                val clock = finalState.clocks[event.id]
                val crossedMidpoint = clock != null &&
                    previousFill < clock.midpoint &&
                    event.filled >= clock.midpoint &&
                    clock.midClue.isNotBlank()
                val completed = clock != null &&
                    previousFill < clock.segments &&
                    event.filled == clock.segments &&
                    clock.publicClue.isNotBlank()
                when {
                    clock == null -> null
                    crossedMidpoint && completed ->
                        AutomaticTimelineEntry(index, "pista", "${clock.midClue}\n${clock.publicClue}")
                    crossedMidpoint -> AutomaticTimelineEntry(index, "pista", clock.midClue)
                    completed -> AutomaticTimelineEntry(index, "pista", clock.publicClue)
                    else -> null
                }
            }
            is CombatStarted -> {
                combatants = event.order.toSet()
                combatIncludesPlayer = playerId in combatants
                if (combatIncludesPlayer) AutomaticTimelineEntry(index, "combate", "Um confronto começou.")
                else null
            }
            CombatEnded -> {
                val visible = combatIncludesPlayer
                combatIncludesPlayer = false
                combatants = emptySet()
                if (visible) AutomaticTimelineEntry(index, "combate", "O confronto terminou.") else null
            }
            is AttackResolved -> if (combatIncludesPlayer && (event.attackerId == playerId || event.targetId == playerId)) {
                when {
                    event.attackerId == playerId -> finalState.entities[event.targetId]?.let {
                        AutomaticTimelineEntry(
                            index, "combate",
                            if (event.hit) "Você atingiu ${it.name} e causou ${event.damage} de dano." else "Você não causou dano a ${it.name}."
                        )
                    }
                    else -> finalState.entities[event.attackerId]?.let {
                        AutomaticTimelineEntry(
                            index, "combate",
                            if (event.hit) "${it.name} atingiu você e causou ${event.damage} de dano." else "${it.name} não causou dano a você."
                        )
                    }
                }
            } else null
            is EntityDied -> if (combatIncludesPlayer && event.entityId in combatants && event.entityId != playerId) {
                finalState.entities[event.entityId]?.let {
                    AutomaticTimelineEntry(index, "combate", "${it.name} foi derrotado.")
                }
            } else if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "consequência", "Seu personagem morreu.")
            } else null
            is SurvivalFailed -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "sobrevivência", "Você falhou em um teste de sobrevivência e ficou mais perto de uma consequência grave.")
            } else null
            is TraitUseSpent -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "habilidade", "Você gastou um uso de uma habilidade limitada.")
            } else null
            is TraitUsesReset -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "habilidade", "Os usos limitados de suas habilidades foram recuperados.")
            } else null
            is ResourceChanged -> if (event.entityId == playerId && event.delta > 0) {
                AutomaticTimelineEntry(index, "recuperação", "Você recuperou ${event.delta} ponto(s) de um recurso.")
            } else null
            is KnowledgeGained ->
                AutomaticTimelineEntry(index, "descoberta",
                    if (event.believed) "Você passou a acreditar: ${event.text}" else "Você descobriu: ${event.text}")
            is RelationChanged -> {
                val otherId = when (playerId) {
                    event.fromId -> event.toId
                    event.toId -> event.fromId
                    else -> null
                }
                otherId?.let { id ->
                    finalState.entities[id]?.takeIf { it.kind == EntityKind.NPC }?.let { npc ->
                        AutomaticTimelineEntry(index, "relação", "Sua relação com ${npc.name} mudou.")
                    }
                }
            }
            is PlaceDiscovered -> finalState.places[event.id]
                ?.takeIf { event.id in finalState.knownPlaces }
                ?.let { AutomaticTimelineEntry(index, "local", "Local descoberto: ${it.name}") }
            is PlayerMovedToPlace -> finalState.places[event.id]
                ?.takeIf { event.id in finalState.knownPlaces }
                ?.let { AutomaticTimelineEntry(index, "viagem", "Você chegou a ${it.name}.") }
            is ItemChanged -> if (event.ownerId == playerId && event.delta != 0) {
                val action = if (event.delta > 0) "Você recebeu" else "Você perdeu"
                val amount = kotlin.math.abs(event.delta)
                AutomaticTimelineEntry(index, "inventário", "$action $amount unidade(s) de um item.")
            } else null
            is EquipmentChanged -> if (event.ownerId == playerId) {
                if (event.itemId == null) {
                    AutomaticTimelineEntry(index, "equipamento", "Você desequipou um item.")
                } else {
                    AutomaticTimelineEntry(index, "equipamento", "Você equipou um item.")
                }
            } else null
            is XpGained -> if (event.entityId == playerId && event.amount > 0) {
                AutomaticTimelineEntry(index, "progressão", "Você ganhou ${event.amount} ponto(s) de experiência.")
            } else null
            is LevelGained -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você alcançou o nível ${event.level}.")
            } else null
            is SpellLearned -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você aprendeu uma magia.")
            } else null
            is AbilityLearned -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você aprendeu uma habilidade.")
            } else null
            is SkillMastered -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você dominou uma perícia.")
            } else null
            is StatPointsAllocated -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você distribuiu pontos de atributo.")
            } else null
            is AdvancementAllocated -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você investiu pontos de evolução em atributos e perícias.")
            } else null
            is SkillTreeProgressed -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você avançou uma habilidade da sua árvore de habilidades.")
            } else null
            is SkillTreeProgressReset -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você redefiniu sua árvore de habilidades e recuperou ${event.pointsRefunded} ponto(s) investido(s).")
            } else null
            is InitialBuildAllocated -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você concluiu a distribuição inicial dos pontos da ficha.")
            } else null
            is AbilityActivated -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "habilidade", "Você ativou uma habilidade.")
            } else null
            is ConditionTicked -> if (event.entityId == playerId && event.damage > 0) {
                AutomaticTimelineEntry(index, "condição", "Uma condição causou ${event.damage} de dano em você.")
            } else null
            is ConditionAdded -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "condição", "Você sofreu uma condição.")
            } else null
            is ConditionRemoved -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "condição", "Uma condição deixou de afetar você.")
            } else null
            is EntityDowned -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "consequência", "Seu personagem caiu.")
            } else null
            is EntityRecovered -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "consequência", "Seu personagem se reergueu.")
            } else null
            is EntityRevived -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "consequência", "Seu personagem voltou à vida.")
            } else null
            // A criação de NPC é estado interno; só fatos explicitamente públicos entram na timeline.
            is EntityCreated -> null
            else -> null
        }
        if (entry != null) result += entry
    }
    return result
}
