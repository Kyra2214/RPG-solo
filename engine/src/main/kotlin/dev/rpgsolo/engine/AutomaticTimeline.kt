package dev.rpgsolo.engine

/** Entrada derivada do log canônico. Não é persistida separadamente: replay do log recompõe a mesma linha. */
data class AutomaticTimelineEntry(
    val eventIndex: Int,
    val category: String,
    val text: String
)

/**
 * Cria uma linha do tempo só com acontecimentos que o personagem poderia conhecer.
 * Não inclui relógios, dossiês privados, mudanças ocultas de NPC nem texto de segredos não descobertos.
 * O índice é a posição do evento no log, não uma data civil.
 */
fun automaticTimeline(
    events: List<Event>,
    playerId: String,
    finalState: GameState
): List<AutomaticTimelineEntry> {
    require(playerId.isNotBlank()) { "playerId vazio" }
    val result = mutableListOf<AutomaticTimelineEntry>()
    events.forEachIndexed { index, event ->
        val entry = when (event) {
            is KnowledgeGained ->
                AutomaticTimelineEntry(index, "descoberta",
                    if (event.believed) "Você passou a acreditar: ${event.text}" else "Você descobriu: ${event.text}")
            is PlaceDiscovered -> finalState.places[event.id]
                ?.takeIf { event.id in finalState.knownPlaces }
                ?.let { AutomaticTimelineEntry(index, "local", "Local descoberto: ${it.name}") }
            is PlayerMovedToPlace -> finalState.places[event.id]
                ?.takeIf { event.id in finalState.knownPlaces }
                ?.let { AutomaticTimelineEntry(index, "viagem", "Você chegou a ${it.name}.") }
            is XpGained -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você recebeu experiência.")
            } else null
            is LevelGained -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "progressão", "Você avançou para o nível ${event.level}.")
            } else null
            is ConditionAdded -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "condição", "Você sofreu a condição: ${event.condition}.")
            } else null
            is ConditionRemoved -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "condição", "A condição ${event.condition} deixou de afetar você.")
            } else null
            is EntityDied -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "consequência", "Seu personagem morreu.")
            } else null
            is EntityRevived -> if (event.entityId == playerId) {
                AutomaticTimelineEntry(index, "consequência", "Seu personagem voltou à vida.")
            } else null
            else -> null
        }
        if (entry != null) result += entry
    }
    return result
}
