package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** LG-01: prefixo de [EntityDied.cause] quando a morte veio de uma condição (ex.: `condicao:veneno`). */
const val CAUSE_CONDITION_PREFIX = "condicao:"

/** LG-01: relação mínima (em módulo) para um NPC entrar nos vínculos do legado. */
const val LEGACY_BOND_MIN = 30

/** LG-01: máximo de vínculos guardados por legado. */
const val LEGACY_BONDS_MAX = 5

@Serializable
enum class DeathKind {
    /** Morto por uma entidade ([HeroLegacy.killerId] preenchido). */
    SLAIN,

    /** Morto por uma condição (veneno, sangramento…); sem autor rastreável. */
    CONDITION,

    /** Ambiente (armadilha, queda…) ou origem desconhecida; nunca gera vingança. */
    UNKNOWN,
}

/** LG-01: NPC que tinha um laço forte com o herói; [value] é a relação (−100..100), positiva = afeto. */
@Serializable
data class LegacyBond(val npcId: String, val npcName: String, val value: Int)

/**
 * LG-01: o que fica do herói quando ele morre. Gravado pelo motor ao aplicar [EntityDied] de um PLAYER,
 * só com o que já está no estado (nada vem da IA). Não guarda ficha: é a parte pública da memória.
 * Chave em [GameState.legacies] = [heroId]; renascer ([EntityRevived]) apaga o legado.
 */
@Serializable
data class HeroLegacy(
    val heroId: String,
    val heroName: String,
    val level: Int,
    val concept: String = "",
    val diedOnDay: Int,
    val kind: DeathKind,
    val killerId: String? = null,
    val killerName: String? = null,
    val cause: String? = null,
    val bonds: List<LegacyBond> = emptyList(),
)

/** LG-01: situação do assassino hoje, para decidir o desfecho da vingança (LG-03). */
enum class KillerStatus {
    /** Sem assassino (ambiente, condição ou desconhecido): não há vingança. */
    NONE,

    /** Vivo e hostil ou neutro: a vingança vale. */
    ALIVE,

    /** Já morreu (a morte foi paga): sem vingança. */
    DEAD,

    /** Vivo, mas aliado de quem joga agora: dilema moral. */
    ALLY,

    /** O jogador controla o assassino (assumiu o NPC que matou o herói). */
    PLAYER_CONTROLLED,
}

/** Constrói o legado de [hero] a partir do estado *antes* da morte. */
internal fun GameState.legacyOf(
    hero: Entity,
    killerId: String?,
    cause: String?,
): HeroLegacy {
    val killer = killerId?.takeIf { it != hero.id }?.let { entities[it] }
    val kind =
        when {
            killer != null -> DeathKind.SLAIN
            cause?.startsWith(CAUSE_CONDITION_PREFIX) == true -> DeathKind.CONDITION
            else -> DeathKind.UNKNOWN
        }
    val bonds =
        entities.values
            .asSequence()
            .filter { it.kind == EntityKind.NPC && it.alive && it.id != hero.id }
            .mapNotNull { npc ->
                val toHero = relations[relationKey(npc.id, hero.id)] ?: 0
                val fromHero = relations[relationKey(hero.id, npc.id)] ?: 0
                val value = if (kotlin.math.abs(toHero) >= kotlin.math.abs(fromHero)) toHero else fromHero
                if (kotlin.math.abs(value) >= LEGACY_BOND_MIN) LegacyBond(npc.id, npc.name, value) else null
            }
            .sortedWith(compareByDescending<LegacyBond> { kotlin.math.abs(it.value) }.thenBy { it.npcId })
            .take(LEGACY_BONDS_MAX)
            .toList()
    return HeroLegacy(
        heroId = hero.id,
        heroName = hero.name,
        level = hero.level,
        concept = hero.concept,
        diedOnDay = day,
        kind = kind,
        killerId = killer?.id,
        killerName = killer?.name,
        cause = if (kind == DeathKind.CONDITION) cause else null,
        bonds = bonds,
    )
}

/** LG-01: o legado do herói (vivo ou não), ou null se ele nunca morreu / renasceu. */
fun GameState.legacyOfHero(heroId: String): HeroLegacy? = legacies[heroId]

/**
 * LG-01: situação do assassino de [this] em [state]; [playerId] é quem o jogador controla agora.
 * Só olha o estado; não decide a quest (isso é do LG-03).
 */
fun HeroLegacy.killerStatus(
    state: GameState,
    playerId: String?,
): KillerStatus {
    val id = killerId ?: return KillerStatus.NONE
    val killer = state.entities[id] ?: return KillerStatus.NONE
    return when {
        id == playerId -> KillerStatus.PLAYER_CONTROLLED
        !killer.alive -> KillerStatus.DEAD
        playerId != null &&
            maxOf(
                state.relations[relationKey(id, playerId)] ?: 0,
                state.relations[relationKey(playerId, id)] ?: 0,
            ) >= ALLY_RELATION_MIN -> KillerStatus.ALLY
        else -> KillerStatus.ALIVE
    }
}
