package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

@Serializable
enum class EntityKind { PLAYER, NPC }

@Serializable
data class Resource(val current: Int, val max: Int) {
    init {
        require(max >= 0) { "max negativo" }
        require(current in 0..max) { "current fora de 0..max" }
    }
}

/** Entidade genérica (jogador ou NPC). Os nomes de atributos/recursos vêm do pacote de regras. */
@Serializable
data class Entity(
    val id: String,
    val name: String,
    val kind: EntityKind,
    val stats: Map<String, Int> = emptyMap(),
    val resources: Map<String, Resource> = emptyMap(),
    val alive: Boolean = true,
    val xp: Int = 0,
    val level: Int = 1,
    /** Pontos por perícia; ids e limites vêm do pacote de regras. */
    val skills: Map<String, Int> = emptyMap()
)

/** Estado canônico da campanha. Só muda aplicando eventos (ver [apply]). */
data class GameState(
    val entities: Map<String, Entity> = emptyMap(),
    /** dono -> (item -> quantidade) */
    val inventory: Map<String, Map<String, Int>> = emptyMap(),
    val facts: Map<String, String> = emptyMap(),
    /** O que o jogador descobriu (2.2.3). Chave -> conhecimento. */
    val knowledge: Map<String, Knowledge> = emptyMap(),
    /** Promessas registradas por NPCs (2.2.4). Id -> promessa. */
    val promises: Map<String, Promise> = emptyMap(),
    /** "de>para" -> relação de -100 a 100 (8.2.2). Sem entrada = 0 (neutra). */
    val relations: Map<String, Int> = emptyMap(),
    /** Relógios de agenda (8.2.3). */
    val clocks: Map<String, Clock> = emptyMap(),
    /** entidade -> condições ativas (ex.: "envenenado"). */
    val conditions: Map<String, Set<String>> = emptyMap(),
    /** null = fora de combate. */
    val combat: CombatState? = null,
    val eventCount: Int = 0
)

/**
 * Conhecimento do jogador, com a fonte (pista, teste, conversa).
 * [believed] = true guarda informação falsa/incerta como "acreditada", nunca como verdade.
 */
data class Knowledge(val key: String, val text: String, val source: String, val believed: Boolean = false)

fun relationKey(fromId: String, toId: String) = "$fromId>$toId"

/** Relógio de agenda: quando [filled] chega a [segments], a ameaça/plano se cumpre. */
data class Clock(val id: String, val name: String, val segments: Int, val filled: Int = 0) {
    val full: Boolean get() = filled >= segments
}

data class Promise(val id: String, val npcId: String, val text: String, val kept: Boolean = false)

/** [order] já vem decidido (iniciativa rolada pelo motor). */
data class CombatState(val order: List<String>, val round: Int = 1, val turnIndex: Int = 0) {
    val current: String get() = order[turnIndex]
}
