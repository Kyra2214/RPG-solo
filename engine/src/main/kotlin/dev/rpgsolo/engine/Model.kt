package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

@Serializable
enum class EntityKind { PLAYER, NPC }

/** Modo de morte da campanha (M-04). PERMADEATH: recurso de morte em 0 mata. DOWNED: o jogador cai antes de morrer. */
@Serializable
enum class DeathMode { PERMADEATH, DOWNED }

@Serializable
data class Resource(val current: Int, val max: Int) {
    init {
        require(max >= 0) { "max negativo" }
        require(current in 0..max) { "current fora de 0..max" }
    }
}

@Serializable
data class AppliedCurseState(
    val curseId: String,
    val sourceId: String,
    val remainingRounds: Int? = null
)

/** Entidade genérica (jogador ou NPC). Os nomes de atributos/recursos vêm do pacote de regras. */
@Serializable
data class Entity(
    val id: String,
    val name: String,
    val kind: EntityKind,
    val stats: Map<String, Int> = emptyMap(),
    val resources: Map<String, Resource> = emptyMap(),
    val alive: Boolean = true,
    /** M-04: caído (vivo, com o recurso de morte em 0). Só ocorre com [DeathMode.DOWNED] e em PLAYER. */
    val downed: Boolean = false,
    /** M-04: falhas em testes de sobrevivência desde que caiu. */
    val deathFailures: Int = 0,
    val xp: Int = 0,
    val level: Int = 1,
    /** Pontos por perícia; ids e limites vêm do pacote de regras. */
    val skills: Map<String, Int> = emptyMap(),
    /** Conceito e traços de NPCs são privados para a interface; usados pelo mestre e pelo motor. */
    val concept: String = "",
    val traits: List<String> = emptyList(),
    /** false = ficha criada sem pacote de regras para validação. */
    val rulesValidated: Boolean = true,
    /** Pontos de evolução não gastos, concedidos pelo pacote ao subir de nível. */
    val advancementPoints: Int = 0,
    /** MU-05.2: reserva de pontos de status (sem teto), separada da de habilidade. */
    val statPoints: Int = 0,
    /** MU-05.2: reserva de pontos de habilidade; o gasto entra em MU-06.3. */
    val abilityPoints: Int = 0,
    /** MU-06.2: perícias com o nível 2 liberado (por missão, em níveis-chave). */
    val masteredSkills: Set<String> = emptySet(),
    /** MU-06.3: habilidades aprendidas com pontos de habilidade. */
    val abilities: Set<String> = emptySet(),
    /** MU-07.2: magias aprendidas; cada uma ocupa um slot (aquisição em MU-07.3). */
    val spells: Set<String> = emptySet(),
    /** IDs e níveis das habilidades da nova árvore de progressão, persistidos no estado canônico. */
    val skillTreeLevels: Map<String, Int> = emptyMap(),
    /** Pontos efetivamente investidos por habilidade; usados para um reset sem gerar pontos extras. */
    val skillTreeInvestments: Map<String, Int> = emptyMap(),
    /** Habilidades básicas gratuitas de origem, preservadas em nível 1 durante o reset. */
    val freeSkillIds: Set<String> = emptySet(),
    /** Marca a distribuição única dos 12 pontos iniciais do novo sistema. */
    val initialBuildAllocated: Boolean = false,
    /** Maldições aplicadas durante a aventura, distintas da identidade Classe de Malditos. */
    val appliedCurses: Map<String, AppliedCurseState> = emptyMap()
)

/** MU-07.2: slots de magia de uma entidade: [total] pelos níveis-chave alcançados, [used] por magias aprendidas. */
data class SpellSlots(val total: Int, val used: Int) {
    val free: Int get() = (total - used).coerceAtLeast(0)
}

/** Estado canônico da campanha. Só muda aplicando eventos (ver [apply]). */
@Serializable
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
    val campaignStyle: CampaignStyle? = null,
    /** DC-05.2: modo do documento-fonte (null = campanha sem documento). */
    val sourceMode: SourceMode? = null,
    /** entidade -> traço -> usos já gastos desde o último zeramento (M-01). Sem entrada = nenhum uso. */
    val traitUses: Map<String, Map<String, Int>> = emptyMap(),
    /** I-02.1: NPC/lugar -> notas do que importa lembrar (as mais recentes, até [MEMORY_NOTES_PER_SUBJECT]). */
    val memories: Map<String, List<String>> = emptyMap(),
    /** MU-06.3: entidade -> efeitos temporários de habilidades, com duração em rodadas gravada. */
    val activeEffects: Map<String, List<ActiveEffect>> = emptyMap()
)

const val MEMORY_NOTES_PER_SUBJECT = 5
const val MEMORY_NOTE_MAX_CHARS = 200

@Serializable
data class Promise(val id: String, val npcId: String, val text: String, val kept: Boolean = false)

/** Conhecimento descoberto pelo jogador; crenças podem ser falsas e preservam sua fonte. */
@Serializable
data class Knowledge(val key: String, val text: String, val source: String, val believed: Boolean = false)

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
@Serializable
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
    val publicClue: String = "",
    /** Casas que o relógio avança sozinho a cada descanso do jogador (M-07); 0 = só avança por proposta validada. */
    val restTick: Int = 0,
    /** Casas que o relógio avança sozinhas a cada viagem do jogador para outro local (M-07); 0 = não avança. */
    val moveTick: Int = 0,
    /** Casas que o relógio avança sozinhas ao encerrar um capítulo (M-07); 0 = não avança. */
    val chapterTick: Int = 0,
    /** Pista pública opcional de meio caminho (M-07): revelada ao preenchimento cruzar a metade das casas; vazia = sem pista. */
    val midClue: String = "",
    /** Consequência privada (M-07): orientação ao mestre para quando o relógio completar; nunca vai ao jogador pelo app. */
    val consequence: String = ""
) {
    init {
        require(id.isNotBlank() && id.length <= 80) { "id de relógio inválido" }
        require(name.isNotBlank() && name.length <= 120) { "nome de relógio inválido" }
        require(segments in 2..12) { "relógio precisa de 2 a 12 casas" }
        require(filled in 0..segments) { "preenchimento de relógio fora dos limites" }
        require(publicClue.length <= 500) { "pista pública excede 500 caracteres" }
        require(midClue.length <= 500) { "pista intermediária excede 500 caracteres" }
        require(consequence.length <= 500) { "consequência excede 500 caracteres" }
        require(restTick in 0..segments) { "avanço por descanso fora dos limites" }
        require(moveTick in 0..segments) { "avanço por viagem fora dos limites" }
        require(chapterTick in 0..segments) { "avanço por capítulo fora dos limites" }
    }

    val full: Boolean get() = filled >= segments

    /** Preenchimento a partir do qual a pista intermediária é revelada (metade das casas, arredondada para cima). */
    val midpoint: Int get() = (segments + 1) / 2
}

/**
 * Conhecimento do jogador, com a fonte (pista, teste, conversa).
 * [believed] = true guarda informação falsa/incerta como "acreditada", nunca como verdade.
 */


/** Chave não ambígua para a relação direcional entre duas entidades. */
internal fun relationKey(fromId: String, toId: String): String = "$fromId\u0000$toId"
