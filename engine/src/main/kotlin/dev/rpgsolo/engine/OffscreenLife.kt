package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlin.random.Random

/** MP-03: tipo do passo que o motor aplicou ao NPC entre campanhas. */
@Serializable
enum class OffscreenKind { PROMOTION, DEMOTION, RELOCATION, ALLIANCE, NONE }

@Serializable
data class OffscreenStep(val kind: OffscreenKind, val text: String)

/**
 * MP-03: o que mudou na vida do NPC fora de cena. [rank] é a faixa de cargo (0 a [OffscreenRules.MAX_RANK]), relativa
 * ao ponto de partida; [power] muda junto com o cargo. Só guarda os últimos [OffscreenRules.LOG_MAX] passos.
 */
@Serializable
data class OffscreenLife(
    val rank: Int = 0,
    val placeId: String? = null,
    val allies: List<String> = emptyList(),
    val steps: List<OffscreenStep> = emptyList(),
    val totalSteps: Int = 0,
)

object OffscreenRules {
    const val MAX_RANK = 5
    const val ALLIES_MAX = 3
    const val LOG_MAX = 5

    /** No máximo um passo por campanha passada, e no máximo isto por avanço: sem saltos de poder. */
    const val MAX_CAMPAIGNS_PER_ADVANCE = 10
    const val MAX_NET_RANK_GAIN = 2

    private val AMBITIOUS = listOf("ambici", "ganancios", "cobic", "sedent")
    private val LOYAL = listOf("leal", "fiel", "devot")
    private val PEACEFUL = listOf("pacific", "calm", "humilde")

    /**
     * Avança [campaignsElapsed] campanhas de vida fora de cena. Cada campanha sorteia (por semente) no máximo um passo
     * pequeno, limitado pelos recursos e pela personalidade. Relações, marcos e eventos nunca são tocados.
     * [places] = lugares para onde ele pode se mudar; [peers] = nomes de outros NPCs com quem pode se aliar.
     */
    fun advance(
        c: RecurringCharacter,
        campaignsElapsed: Int,
        seed: Long,
        places: List<String> = emptyList(),
        peers: List<String> = emptyList(),
    ): RecurringCharacter {
        if (c.kind != RecurringKind.NPC || !c.alive || campaignsElapsed <= 0) return c
        var life = c.offscreen ?: OffscreenLife()
        val startRank = life.rank
        val persona = c.personality.lowercase()
        val ambitious = AMBITIOUS.any { it in persona } || c.goals.isNotEmpty()
        val loyal = LOYAL.any { it in persona }
        val peaceful = PEACEFUL.any { it in persona }
        val weakened = c.defeat == DefeatOutcome.FLED || c.defeat == DefeatOutcome.HUMILIATED

        repeat(campaignsElapsed.coerceAtMost(MAX_CAMPAIGNS_PER_ADVANCE)) { i ->
            val rng = Random(seed xor c.identityKey.hashCode().toLong() xor (life.totalSteps * 31L + i))
            val canPromote = life.rank < MAX_RANK && life.rank - startRank < MAX_NET_RANK_GAIN
            val canDemote = life.rank > -MAX_RANK
            val canMove = places.any { it != life.placeId }
            val canAlly = life.allies.size < ALLIES_MAX && peers.any { it !in life.allies }
            // Pesos: personalidade empurra, não obriga. O que sobra é NONE (nada muda).
            val weights =
                listOf(
                    OffscreenKind.PROMOTION to if (canPromote) (if (ambitious) 20 else 8) - (if (weakened) 10 else 0) else 0,
                    OffscreenKind.DEMOTION to if (canDemote) (if (weakened) 18 else 6) else 0,
                    OffscreenKind.RELOCATION to
                        if (canMove) {
                            (
                                if (loyal) {
                                    5
                                } else if (peaceful) {
                                    8
                                } else {
                                    14
                                }
                            )
                        } else {
                            0
                        },
                    OffscreenKind.ALLIANCE to if (canAlly) (if (loyal) 20 else 10) else 0,
                    OffscreenKind.NONE to 40,
                ).map { it.first to it.second.coerceAtLeast(0) }
            var roll = rng.nextInt(weights.sumOf { it.second })
            val kind =
                weights.first { (_, w) ->
                    if (roll < w) {
                        true
                    } else {
                        roll -= w
                        false
                    }
                }.first
            val step =
                when (kind) {
                    OffscreenKind.PROMOTION -> {
                        life = life.copy(rank = life.rank + 1)
                        OffscreenStep(kind, "subiu uma faixa de cargo")
                    }
                    OffscreenKind.DEMOTION -> {
                        life = life.copy(rank = life.rank - 1)
                        OffscreenStep(kind, "perdeu poder: desceu uma faixa de cargo")
                    }
                    OffscreenKind.RELOCATION -> {
                        val to = places.filter { it != life.placeId }.let { it[rng.nextInt(it.size)] }
                        life = life.copy(placeId = to)
                        OffscreenStep(kind, "mudou-se para $to")
                    }
                    OffscreenKind.ALLIANCE -> {
                        val ally = peers.filter { it !in life.allies }.let { it[rng.nextInt(it.size)] }
                        life = life.copy(allies = life.allies + ally)
                        OffscreenStep(kind, "formou aliança com $ally")
                    }
                    OffscreenKind.NONE -> null
                }
            if (step != null) life = life.copy(steps = (life.steps + step).takeLast(LOG_MAX), totalSteps = life.totalSteps + 1)
        }
        return c.copy(offscreen = life)
    }

    /** Texto para o mestre explicar o que mudou; a IA não acrescenta poder nem eventos além dos listados. */
    fun masterContext(c: RecurringCharacter): String? {
        val life = c.offscreen?.takeIf { it.steps.isNotEmpty() } ?: return null
        return "Vida fora de cena de ${c.name} (decidida pelo motor; explique, sem inventar poder ou fatos além disto): " +
            life.steps.joinToString("; ") { it.text } + "."
    }
}
