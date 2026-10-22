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
    // A pista só é publicada na transição de incompleto para completo.
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
                if (clock != null &&
                    previousFill < clock.midpoint &&
                    event.filled >= clock.midpoint &&
                    event.filled < clock.segments &&
                    clock.midClue.isNotBlank()
                ) {
                    AutomaticTimelineEntry(index, "pista", clock.midClue)
                } else if (clock != null &&
                    previousFill < clock.segments &&
                    event.filled == clock.segments &&
                    clock.publicClue.isNotBlank()
                ) {
                    AutomaticTimelineEntry(index, "pista", clock.publicClue)
                } else null
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
            is KnowledgeGained ->
                AutomaticTimelineEntry(index, "descoberta",
                    if (event.believed) "Você passou a acreditar: ${event.text}" else "Você descobriu: ${event.text}")
            is PromiseMade -> finalState.entities[event.npcId]?.let { npc ->
                AutomaticTimelineEntry(index, "promessa", "${npc.name} prometeu: ${event.text}")
            }
            is PromiseKept -> finalState.promises[event.id]?.let { promise ->
                finalState.entities[promise.npcId]?.let { npc ->
                    AutomaticTimelineEntry(index, "promessa", "A promessa de ${npc.name} foi cumprida.")
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
                AutomaticTimelineEntry(index, "inventário", "$action $amount unidade(s) do item ${event.itemId}.")
            } else null
            is EquipmentChanged -> if (event.ownerId == playerId) {
                if (event.itemId == null) {
                    AutomaticTimelineEntry(index, "equipamento", "Você desequipou o item do espaço ${event.slot}.")
                } else {
                    AutomaticTimelineEntry(index, "equipamento", "Você equipou ${event.itemId} no espaço ${event.slot}.")
                }
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
            is XpGained -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você recebeu experiência.")
            } else null
            is LevelGained -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você avançou para o nível ${event.level}.")
            } else null
            is AdvancementAllocated -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você investiu pontos de evolução na ficha.")
            } else null
            is SpellLearned -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você aprendeu uma magia.")
            } else null
            is AbilityLearned -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você aprendeu uma habilidade.")
            } else null
            is AbilityActivated -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "habilidade", "Você ativou uma habilidade.")
            } else null
            is SkillMastered -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você alcançou o nível 2 de uma perícia.")
            } else null
            is StatPointsAllocated -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você investiu pontos de status na ficha.")
            } else null
            is ConditionTicked -> if (event.entityId == playerId && event.damage > 0) {
                AutomaticTimelineEntry(index, "condição", "A condição ${event.condition} causou ${event.damage} de dano em você.")
            } else null
            is ConditionAdded -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "condição", "Você sofreu a condição: ${event.condition}.")
            } else null
            is ConditionRemoved -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "condição", "A condição ${event.condition} deixou de afetar você.")
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
