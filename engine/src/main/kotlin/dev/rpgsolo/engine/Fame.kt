package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlin.random.Random

/** MP-07: o rumor reflete o fato com exatidão, o exagera ou é falso. Sorteio do motor; a IA só narra a distorção. */
@Serializable
enum class RumorTruth { EXACT, EXAGGERATED, FALSE }

/** MP-07: por onde a notícia viaja; define a força inicial no lugar de origem. */
@Serializable
enum class RumorChannel(val startStrength: Int) {
    WITNESS(70),
    MERCHANT(60),
    BARD(80),
    POSTER(90),
}

/**
 * MP-07: um rumor. [strength] mapeia lugar -> força (0 a 100) com que a notícia chegou lá; some quando chega a 0.
 * O rumor nunca vira fato: [truth] só diz ao motor e ao mestre o quanto ele distorce.
 */
@Serializable
data class Rumor(
    val id: String,
    val text: String,
    val originPlaceId: String,
    val startedOnDay: Int,
    val truth: RumorTruth,
    val channel: RumorChannel,
    val strength: Map<String, Int> = emptyMap(),
)

/** MP-07: fama por região ou facção. [reach] = quão longe a história chegou (0 a 100); [disposition] = como é vista (-100 a 100). */
@Serializable
data class RegionFame(val reach: Int = 0, val disposition: Int = 0)

const val RUMORS_MAX = 100
const val FAME_KEYS_MAX = 100
const val RUMOR_TEXT_MAX = 200
const val FAME_JUMP_DAYS_MAX = 3650

/** Perda de força do rumor por dia parado e por passagem percorrida (MP-07). */
const val RUMOR_DAILY_DECAY = 5
const val RUMOR_HOP_DECAY = 20

/**
 * MP-07: instantâneo do mundo no fim de uma campanha, herdado pela próxima (mapa, dia, fama e rumores). Nada é
 * compartilhado em tempo real: cada campanha ramifica a partir daqui.
 * ASSUMINDO: NPCs recorrentes já vivem em `personagem_recorrente` (MP-02) e relíquias ainda não existem no motor;
 * por isso o instantâneo não os repete.
 */
@Serializable
data class WorldChronicle(
    val places: List<Place>,
    val links: Map<String, Set<String>>,
    val day: Int,
    val endPlaceId: String?,
    val fame: Map<String, RegionFame> = emptyMap(),
    val rumors: List<Rumor> = emptyList(),
) {
    /** Lugar onde a nova campanha começa: um vizinho de onde a anterior terminou (ou o próprio, se isolado). */
    fun startPlaceId(): String? {
        val end = endPlaceId?.takeIf { e -> places.any { it.id == e } } ?: places.firstOrNull()?.id ?: return null
        return links[end].orEmpty().sorted().firstOrNull() ?: end
    }

    companion object {
        fun from(state: GameState): WorldChronicle =
            WorldChronicle(
                places = state.places.values.toList(),
                links = state.placeLinks,
                day = state.day,
                endPlaceId = state.currentPlaceId,
                fame = state.fame,
                rumors = state.rumors.values.toList(),
            )
    }
}

/** Efeitos mecânicos da fama num lugar. Calculados pelo motor, nunca pela IA. */
data class FameEffects(
    /** Alguém no lugar reconhece o herói (algum rumor chegou forte ou a fama alcança o lugar). */
    val recognized: Boolean,
    /** Ajuste de preço em %, de -10 a +10 (fama boa barateia; má fama encarece). */
    val priceModPercent: Int,
    /** Bônus na relação inicial dos NPCs do lugar, de -25 a +25. */
    val initialRelationBonus: Int,
)

object Fame {
    const val RECOGNITION_THRESHOLD = 40

    /** Fama que vale num lugar: a da própria região, ou a da facção [factionKey] se esta for mais forte. */
    fun effects(
        state: GameState,
        placeId: String,
        factionKey: String? = null,
    ): FameEffects {
        val fame = listOfNotNull(state.fame[placeId], factionKey?.let { state.fame[it] }).maxByOrNull { it.reach } ?: RegionFame()
        val rumorStrength = state.rumors.values.maxOfOrNull { it.strength[placeId] ?: 0 } ?: 0
        val weight = maxOf(fame.reach, rumorStrength)
        return FameEffects(
            recognized = weight >= RECOGNITION_THRESHOLD,
            priceModPercent = (-fame.disposition * fame.reach / 1000).coerceIn(-10, 10),
            initialRelationBonus = (fame.disposition * fame.reach / 400).coerceIn(-25, 25),
        )
    }

    /** Sorteio determinístico da veracidade (60% exato, 25% exagerado, 15% falso); mesma semente, mesmo resultado no replay. */
    fun drawTruth(seed: String): RumorTruth {
        val roll = Random(seed.hashCode().toLong()).nextInt(100)
        return when {
            roll < 60 -> RumorTruth.EXACT
            roll < 85 -> RumorTruth.EXAGGERATED
            else -> RumorTruth.FALSE
        }
    }

    /**
     * Passa [days] dias: cada dia a força cai [RUMOR_DAILY_DECAY] e a notícia passa aos vizinhos com
     * [RUMOR_HOP_DECAY] a menos. Força que chega a 0 some do mapa.
     */
    fun spread(
        rumor: Rumor,
        links: Map<String, Set<String>>,
        days: Int,
    ): Rumor {
        var s = rumor.strength
        repeat(days.coerceAtLeast(0)) {
            val next = mutableMapOf<String, Int>()
            for ((place, v) in s) {
                val own = v - RUMOR_DAILY_DECAY
                if (own > 0) next.merge(place, own, ::maxOf)
                val hop = v - RUMOR_HOP_DECAY
                if (hop > 0) links[place].orEmpty().forEach { n -> next.merge(n, hop, ::maxOf) }
            }
            s = next
        }
        return rumor.copy(strength = s)
    }

    /** Só o que o mestre pode citar do rumor; nunca a veracidade, que ele não conhece. */
    fun rumorsAt(
        state: GameState,
        placeId: String,
        limit: Int = 3,
    ): List<Rumor> =
        state.rumors.values.filter { (it.strength[placeId] ?: 0) > 0 }
            .sortedByDescending { it.strength[placeId] }.take(limit)
}

/** MP-07: um rumor nasce no lugar [originPlaceId]. [truth] nulo = sorteio determinístico do motor. */
data class StartRumor(
    val id: String,
    val text: String,
    val originPlaceId: String,
    val channel: RumorChannel = RumorChannel.WITNESS,
    val truth: RumorTruth? = null,
) : Command

/** MP-07: o tempo passa e os rumores viajam pelas passagens do mapa. */
data class SpreadRumors(val days: Int = 1) : Command

/** MP-07: muda a fama em [key] (id de lugar ou de facção). Alcance 0 a 100, disposição -100 a 100. */
data class ChangeFame(val key: String, val reachDelta: Int = 0, val dispositionDelta: Int = 0) : Command

/** MP-07: a campanha nova herda o mundo da anterior e salta [jumpDays] dias (a vida fora de cena é do MP-03). */
data class InheritChronicle(val chronicle: WorldChronicle, val jumpDays: Int = 0) : Command

@Serializable
data class RumorStarted(val rumor: Rumor) : Event

@Serializable
data class RumorsSpread(val days: Int) : Event

/** Valores absolutos já limitados; o replay não recalcula nada. */
@Serializable
data class FameChanged(val key: String, val fame: RegionFame) : Event

@Serializable
data class ChronicleInherited(val chronicle: WorldChronicle, val jumpDays: Int) : Event
