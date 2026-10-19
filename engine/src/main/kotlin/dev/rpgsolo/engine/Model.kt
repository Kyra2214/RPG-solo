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
    val skills: Map<String, Int> = emptyMap(),
    /** Conceito e traços de NPCs são privados para a interface; usados pelo mestre e pelo motor. */
    val concept: String = "",
    val traits: List<String> = emptyList(),
    /** false = ficha criada sem pacote de regras para validação. */
    val rulesValidated: Boolean = true
)

/** Estado canônico da campanha. Só muda aplicando eventos (ver [apply]). */
data class GameState(
    val entities: Map<String, Entity> = emptyMap(),
    /** dono -> (item -> quantidade) */
    val inventory: Map<String, Map<String, Int>> = emptyMap(),
    /** dono -> (slot -> item); equipar não remove o item do inventário. */
    val equipped: Map<String, Map<String, String>> = emptyMap(),
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
    /** entidade -> condição -> duração e efeitos mecânicos. */
    val conditionStates: Map<String, Map<String, ConditionState>> = emptyMap(),
    /** null = fora de combate. */
    val combat: CombatState? = null,
    val eventCount: Int = 0,
    /** Dossiês privados e event-sourced de NPCs; a interface do jogador nunca os exibe diretamente. */
    val npcDossiers: Map<String, NpcDossier> = emptyMap(),
    /** Regras adicionais propostas pela IA; efeitos só entram na mecânica após aprovação explícita. */
    val additionalRules: Map<String, RegraAdicional> = emptyMap(),
    /** Ambientação é independente da ficha e persistida no log da campanha. */
    val ambientacao: Ambientacao? = null,
    /** Mapa textual event-sourced: segredos ficam no estado privado, não na visão do jogador. */
    val places: Map<String, Place> = emptyMap(),
    val placeLinks: Map<String, Set<String>> = emptyMap(),
    val knownPlaces: Set<String> = emptySet(),
    val currentPlaceId: String? = null,
    /** Preferências e limites de conteúdo persistidos separadamente das regras mecânicas. */
    val campaignStyle: CampaignStyle? = null
)

data class Promise(val id: String, val npcId: String, val text: String, val kept: Boolean = false)

/** Efeito mecânico de uma condição ativa; null em [remainingRounds] significa duração indefinida. */
@Serializable
data class ConditionState(
    val remainingRounds: Int?,
    val damagePerRound: Int = 0,
    val damageResource: String = "hp"
) {
    init {
        require(remainingRounds == null || remainingRounds > 0) { "duração de condição inválida" }
        require(damagePerRound >= 0) { "dano de condição negativo" }
        require(damageResource.isNotBlank()) { "recurso de condição vazio" }
    }
}

/** [order] já vem decidido (iniciativa rolada pelo motor). */
data class CombatState(
    val order: List<String>,
    val round: Int = 1,
    val turnIndex: Int = 0,
    /** Entidades protegidas contra o próximo ataque recebido. */
    val defending: Set<String> = emptySet()
) {
    val current: String get() = order[turnIndex]
}


/** Relógio de agenda com preenchimento validado pelo motor. */
@Serializable
data class Clock(
    val id: String,
    val name: String,
    val segments: Int,
    val filled: Int = 0,
    /** Pista pública opcional, revelada somente quando o relógio termina; nome e contagem seguem privados. */
    val publicClue: String = ""
) {
    init {
        require(id.isNotBlank() && id.length <= 80) { "id de relógio inválido" }
        require(name.isNotBlank() && name.length <= 120) { "nome de relógio inválido" }
        require(segments in 2..12) { "relógio precisa de 2 a 12 casas" }
        require(filled in 0..segments) { "preenchimento de relógio fora dos limites" }
        require(publicClue.length <= 500) { "pista pública excede 500 caracteres" }
    }

    val full: Boolean get() = filled >= segments
}

/** Chave não ambígua para a relação direcional entre duas entidades. */
internal fun relationKey(fromId: String, toId: String): String = "$fromId\u0000$toId"
