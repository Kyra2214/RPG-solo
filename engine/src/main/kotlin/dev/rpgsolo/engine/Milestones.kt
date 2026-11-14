package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** MP-01: tipo do marco (o que aconteceu entre personagens). */
@Serializable
enum class MilestoneType {
    HUMILIATION,
    DEFEAT,
    MERCY,
    BETRAYAL,
    HELP,
    DEBT_PAID,
    DECEIT,
    PROMISE,
    INJUSTICE,
    HEROIC_ACT,

    /** Morte de um herói (LG-01): primeiro tipo de marco. */
    DEATH,
}

/** MP-01: quanto se sabe do fato. UNKNOWN nunca é contado como fato a quem pergunta. */
@Serializable
enum class Certainty { CONFIRMED, SUSPECTED, RUMOR, UNKNOWN }

/** MP-01: PERSON acompanha o protagonista entre campanhas; WORLD fica na campanha de origem. */
@Serializable
enum class MilestoneScope { PERSON, WORLD }

/**
 * MP-01: fato estruturado da história. [participantIds] viveram o fato; [knownBy] sabem dele (participantes mais
 * quem soube depois, cada um com a sua [certainty] em [knowledge]). O NPC só sabe o que viveu ou descobriu.
 */
@Serializable
data class Milestone(
    val id: String,
    val type: MilestoneType,
    val participantIds: List<String>,
    val what: String,
    val placeId: String? = null,
    val day: Int = 0,
    val consequence: String = "",
    /** Certeza do fato em si (a verdade registrada pelo motor). */
    val certainty: Certainty = Certainty.CONFIRMED,
    val scope: MilestoneScope = MilestoneScope.PERSON,
    /** Quem sabe do marco -> o que essa pessoa acredita (CONFIRMED para quem viveu). */
    val knowledge: Map<String, Certainty> = emptyMap(),
) {
    val knownBy: Set<String> get() = knowledge.keys
}

const val MILESTONE_TEXT_MAX = 200
const val MILESTONES_MAX = 300

/** MP-01: certeza que alguém ganha ao ouvir falar do marco; nunca sobe acima da dele. */
fun Certainty.degrade(): Certainty =
    when (this) {
        Certainty.CONFIRMED -> Certainty.RUMOR
        else -> this
    }

/** MP-01: marcos que [npcId] conhece, com o que ele acredita (UNKNOWN é omitido). */
fun GameState.milestonesKnownBy(npcId: String): List<Pair<Milestone, Certainty>> =
    milestones.values
        .mapNotNull { m -> m.knowledge[npcId]?.takeIf { it != Certainty.UNKNOWN }?.let { m to it } }
        .sortedBy { it.first.day }

/** MP-01: marcos que envolvem [personId] como participante (para consulta entre campanhas). */
fun GameState.milestonesOf(personId: String): List<Milestone> =
    milestones.values.filter { personId in it.participantIds }.sortedBy { it.day }

/** MP-01: texto de contexto do mestre sobre o que o NPC sabe, sem revelar o que ele não viveu nem descobriu. */
object MilestoneContext {
    fun forNpc(
        state: GameState,
        npcId: String,
        max: Int = 5,
    ): String? {
        val lines =
            state.milestonesKnownBy(npcId).takeLast(max).map { (m, c) ->
                val how =
                    when (c) {
                        Certainty.CONFIRMED -> "sabe (viveu ou confirmou)"
                        Certainty.SUSPECTED -> "suspeita"
                        Certainty.RUMOR -> "só ouviu rumor"
                        Certainty.UNKNOWN -> ""
                    }
                "- (dia ${m.day}) $how: ${m.what}" + if (m.consequence.isNotBlank()) " Consequência: ${m.consequence}" else ""
            }
        if (lines.isEmpty()) return null
        return "Marcos que este NPC conhece (não cite nada além disto como lembrança dele):\n" + lines.joinToString("\n")
    }
}

/** Marco automático de uma morte de herói (LG-01). */
internal fun GameState.deathMilestone(legacy: HeroLegacy): Milestone {
    val parts = listOfNotNull(legacy.heroId, legacy.killerId)
    val know = (parts + legacy.bonds.map { it.npcId }).associateWith { Certainty.CONFIRMED }
    return Milestone(
        id = "morte:${legacy.heroId}",
        type = MilestoneType.DEATH,
        participantIds = parts,
        what = "${legacy.heroName} morreu" + (legacy.killerName?.let { ", morto por $it" } ?: "") + ".",
        day = legacy.diedOnDay,
        certainty = Certainty.CONFIRMED,
        knowledge = know,
    )
}

/** Marco automático de uma derrota sem morte (RV-01). */
internal fun GameState.defeatMilestone(
    e: NpcDefeated,
    name: String,
    byName: String?,
): Milestone {
    val type =
        when (e.outcome) {
            DefeatOutcome.SPARED -> MilestoneType.MERCY
            DefeatOutcome.FLED -> MilestoneType.DEFEAT
            DefeatOutcome.HUMILIATED -> MilestoneType.HUMILIATION
        }
    val who = byName ?: "alguém"
    val what =
        when (e.outcome) {
            DefeatOutcome.SPARED -> "$who poupou $name."
            DefeatOutcome.FLED -> "$name fugiu de $who."
            DefeatOutcome.HUMILIATED -> "$who humilhou $name."
        }
    val parts = listOfNotNull(e.entityId, e.byId)
    return Milestone(
        id = "derrota:${e.entityId}:$day",
        type = type,
        participantIds = parts,
        what = what,
        day = day,
        knowledge = parts.associateWith { Certainty.CONFIRMED },
    )
}
