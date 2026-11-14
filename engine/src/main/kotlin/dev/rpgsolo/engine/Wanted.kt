package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlin.random.Random

/** MP-06: degraus do alcance. Um Boss muito acima do grupo age por intermediários, nunca em pessoa cedo. */
@Serializable
enum class ReachRung(val level: Int, val label: String, val costGold: Int, val costMen: Int, val costInfluence: Int) {
    RUMOR(1, "rumor", 0, 0, 1),
    POSTER(2, "cartaz de procurado", 20, 0, 2),
    HUNTERS(3, "caçadores de recompensa", 60, 2, 3),
    ELITE(4, "grupo de elite ou lacaios", 150, 6, 5),
    EMISSARY(5, "emissário que negocia ou ameaça", 100, 2, 8),
    BOSS(6, "o próprio Boss", 0, 0, 20),
}

@Serializable
enum class WantedStatus { ACTIVE, HIDDEN, PAID, PROVEN_FALSE, EXPIRED }

/** MP-06: respostas do jogador ao procurado. */
@Serializable
enum class WantedResponse { PAY, BRIBE, DISGUISE, KILL_MESSENGER, PROVE_FALSE, RECRUIT }

/** MP-06: recursos do emissor; cada degrau os gasta. */
@Serializable
data class IssuerResources(val gold: Int = 0, val men: Int = 0, val influence: Int = 0)

/**
 * MP-06: registro de procurado. A pessoa leva o histórico; o mundo leva o que é dele (cartaz, caçadores, relógio).
 * [bounty] decai [Wanted.DECAY_PER_DAY_PERCENT]% por dia desde [lastRenewedDay] e só se renova com marco novo.
 */
@Serializable
data class WantedRecord(
    val id: String,
    val targetId: String,
    val issuerName: String,
    val issuerLevel: Int,
    val bounty: Int,
    val resources: IssuerResources,
    val originPlaceId: String,
    val witnessIds: List<String>,
    val rung: ReachRung = ReachRung.RUMOR,
    val issuedOnDay: Int,
    val lastRenewedDay: Int,
    val deadlineDay: Int? = null,
    val status: WantedStatus = WantedStatus.ACTIVE,
    val usedMilestones: List<String> = emptyList(),
)

/** MP-06: relíquia persistente com histórico de donos (ex.: a espada que grita ao ser roubada). */
@Serializable
data class RelicOwner(val ownerName: String, val sinceDay: Int, val how: String)

@Serializable
data class Relic(
    val id: String,
    val name: String,
    val trait: String = "",
    val owners: List<RelicOwner> = emptyList(),
) {
    val currentOwner: String? get() = owners.lastOrNull()?.ownerName
}

/** Agente enviado pelo emissor; [level] segue o grupo atual, nunca o do Boss. */
data class WantedAgent(val kind: String, val level: Int, val count: Int)

object Wanted {
    const val DECAY_PER_DAY_PERCENT = 1
    const val WANTED_MAX = 30
    const val RELICS_MAX = 50
    const val TEXT_MAX = 80

    fun effectiveBounty(
        r: WantedRecord,
        today: Int,
    ): Int = (r.bounty * (100 - DECAY_PER_DAY_PERCENT * (today - r.lastRenewedDay).coerceAtLeast(0)) / 100).coerceAtLeast(0)

    fun isLive(
        r: WantedRecord,
        today: Int,
    ): Boolean = r.status == WantedStatus.ACTIVE && effectiveBounty(r, today) > 0 && (r.deadlineDay?.let { today <= it } ?: true)

    /** Lugares que a notícia já alcançou: [rung] passagens a partir da origem (a notícia viaja pelos `placeLinks`). */
    fun reach(
        r: WantedRecord,
        links: Map<String, Set<String>>,
    ): Set<String> {
        var frontier = setOf(r.originPlaceId)
        val seen = frontier.toMutableSet()
        repeat(r.rung.level) {
            frontier = frontier.flatMap { links[it].orEmpty() }.filter { seen.add(it) }.toSet()
        }
        return seen
    }

    /** Próximo degrau e por que não pode subir (null = pode). O degrau do Boss exige grupo à altura e é raro. */
    fun cannotEscalate(
        r: WantedRecord,
        groupLevel: Int,
        today: Int,
    ): String? {
        if (!isLive(r, today)) return "procurado inativo"
        val next = ReachRung.values().firstOrNull { it.level == r.rung.level + 1 } ?: return "já está no degrau máximo"
        if (r.witnessIds.isEmpty()) return "sem testemunha a notícia não avança"
        if (next == ReachRung.BOSS && groupLevel + 5 < r.issuerLevel) return "o Boss só vem em pessoa quando o grupo está à altura"
        val c = r.resources
        if (c.gold < next.costGold || c.men < next.costMen || c.influence < next.costInfluence) return "o emissor não tem recursos para ${next.label}"
        return null
    }

    /** MP-06: dias, por nível do degrau atual, até o emissor poder subir o próximo degrau. */
    const val ESCALATE_EVERY_DAYS = 3

    /**
     * MP-06: escaladas devidas hoje para os procurados contra [targetId]: ativos, com [ESCALATE_EVERY_DAYS] dias por nível
     * do degrau atual desde a emissão e que o motor aceitaria ([cannotEscalate] nulo). Um degrau por chamada e por procurado,
     * em ordem de id; idempotente (depois de subir, o prazo do degrau seguinte é maior). A IA não decide a escalada.
     */
    fun dueEscalations(
        state: GameState,
        targetId: String,
        groupLevel: Int,
    ): List<EscalateWanted> =
        state.wanted.values
            .filter { it.targetId == targetId && state.day - it.issuedOnDay >= ESCALATE_EVERY_DAYS * it.rung.level }
            .filter { cannotEscalate(it, groupLevel, state.day) == null }
            .sortedBy { it.id }
            .map { EscalateWanted(it.id, groupLevel) }

    /** Agentes do degrau atual: nível escalonado pelo grupo atual (±2 por degrau alto), nunca pelo do Boss. */
    fun agents(
        r: WantedRecord,
        groupLevel: Int,
        seed: Long,
    ): List<WantedAgent> {
        val rng = Random(seed xor r.id.hashCode().toLong() xor r.rung.level.toLong())
        val lvl = { bonus: Int -> (groupLevel + bonus).coerceAtLeast(1) }
        return when (r.rung) {
            ReachRung.RUMOR, ReachRung.POSTER -> emptyList()
            ReachRung.HUNTERS -> listOf(WantedAgent("caçador de recompensa", lvl(0), 1 + rng.nextInt(3)))
            ReachRung.ELITE ->
                listOf(
                    WantedAgent("elite do emissor", lvl(2), 2 + rng.nextInt(2)),
                    WantedAgent("lacaio", lvl(-1), 2 + rng.nextInt(3)),
                )
            ReachRung.EMISSARY -> listOf(WantedAgent("emissário", lvl(0), 1))
            ReachRung.BOSS -> listOf(WantedAgent("${r.issuerName} em pessoa", lvl(5), 1))
        }
    }

    fun masterContext(
        state: GameState,
        targetId: String,
        groupLevel: Int,
    ): String? {
        val live = state.wanted.values.filter { it.targetId == targetId && isLive(it, state.day) }
        if (live.isEmpty()) return null
        return "PROCURADO (decidido pelo motor): " +
            live.joinToString(" ") { r ->
                val ags = agents(r, groupLevel, state.day.toLong()).joinToString { "${it.count}x ${it.kind} (nível ${it.level})" }
                "${r.issuerName} age por ${r.rung.label}; recompensa ${effectiveBounty(r, state.day)}" +
                    (if (ags.isNotEmpty()) "; agentes: $ags" else "") + "."
            } + " O Boss não é rebaixado e a notícia só chega onde o motor indica; não invente caçadores além dos listados."
    }
}

data class IssueWanted(
    val id: String,
    val targetId: String,
    val issuerName: String,
    val issuerLevel: Int,
    val bounty: Int,
    val resources: IssuerResources,
    val originPlaceId: String,
    val witnessIds: List<String>,
    val deadlineDay: Int? = null,
) : Command

/** Sobe um degrau, gastando os recursos do emissor. [groupLevel] vem do app (grupo atual). */
data class EscalateWanted(val id: String, val groupLevel: Int) : Command

/** Renova a recompensa; exige um marco novo ainda não usado por este procurado. */
data class RenewWanted(val id: String, val milestoneId: String) : Command

data class RespondWanted(val id: String, val response: WantedResponse, val amount: Int = 0) : Command

data class RegisterRelic(val id: String, val name: String, val trait: String = "", val ownerName: String, val how: String = "posse inicial") : Command

data class TransferRelic(val id: String, val newOwnerName: String, val how: String) : Command

@Serializable
data class WantedIssued(val record: WantedRecord) : Event

@Serializable
data class WantedUpdated(val record: WantedRecord) : Event

@Serializable
data class RelicSet(val relic: Relic) : Event
