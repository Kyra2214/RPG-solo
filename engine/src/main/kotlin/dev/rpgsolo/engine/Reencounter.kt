package dev.rpgsolo.engine

import kotlin.random.Random

/**
 * MP-04: um NPC recorrente e a situação dele em relação à campanha atual. O chamador (app) preenche; o motor decide.
 * [campaignsAway] = campanhas do mundo desde a última aparição; [appearances] = quantas vezes já voltou;
 * [newMilestones] = marcos novos registrados sobre ele desde a última aparição.
 */
data class ReencounterCandidate(
    val character: RecurringCharacter,
    val campaignsAway: Int,
    val appearances: Int = 0,
    val newMilestones: Int = 0,
)

/** MP-04: o que o motor decidiu antes da cena. A IA só narra; nada aqui vem dela. */
data class ReencounterPlan(
    val character: RecurringCharacter,
    /** Por que o NPC procura o herói ou está ali. */
    val reason: String,
    /** Como soube do paradeiro: [Certainty.CONFIRMED] = fato, RUMOR = rumor, UNKNOWN = não sabe (encontro por acaso). */
    val knowledge: Certainty,
    val howHeKnew: String,
    /** Nível do NPC, escalonado pelo pacote (função recebida), nunca pela IA. */
    val level: Int,
    val resources: String,
    val campaignsAway: Int,
    /** O NPC reconhece o herói. */
    val recognizesHero: Boolean,
    /** O herói tem motivo para reconhecer o NPC (já o viu: há marco em comum ou ele foi derrotado/poupado). */
    val heroRecognizesNpc: Boolean,
    /** RV-04: papel do retorno de um rival (derrota sem morte); nulo para NPCs sem derrota registrada. */
    val role: ReturnRole? = null,
    /** RV-04: o herói que o derrotou já morreu; o rival procura o herdeiro (o protagonista atual). */
    val seeksHeir: Boolean = false,
)

object Reencounter {
    const val MIN_CAMPAIGNS_AWAY = 5
    const val FULL_WINDOW_CAMPAIGNS_AWAY = 10
    const val MAX_PER_CAMPAIGN = 1
    const val BASE_CHANCE = 60
    const val PENALTY_PER_APPEARANCE = 15
    const val BONUS_PER_NEW_MILESTONE = 10
    const val MIN_CHANCE = 5

    /**
     * Orçamento de aparições: a chance cai a cada aparição e só volta a subir com marco novo (nunca acima da base).
     * Depois de [FULL_WINDOW_CAMPAIGNS_AWAY] campanhas fora, a chance sobe 50% (teto 100).
     */
    fun chance(c: ReencounterCandidate): Int {
        val base = (BASE_CHANCE - PENALTY_PER_APPEARANCE * c.appearances + BONUS_PER_NEW_MILESTONE * c.newMilestones).coerceIn(MIN_CHANCE, BASE_CHANCE)
        return if (c.campaignsAway >= FULL_WINDOW_CAMPAIGNS_AWAY) (base * 3 / 2).coerceAtMost(100) else base
    }

    /**
     * Escolhe no máximo [MAX_PER_CAMPAIGN] NPC para esta campanha. Só NPCs vivos, fora da janela mínima ficam de fora.
     * Determinístico: a mesma semente e os mesmos candidatos dão o mesmo resultado.
     */
    fun plan(
        state: GameState,
        heroId: String,
        candidates: List<ReencounterCandidate>,
        seed: Long,
        levelFor: (RecurringCharacter) -> Int,
    ): ReencounterPlan? {
        val winner =
            candidates
                .filter { it.character.kind == RecurringKind.NPC && it.character.alive && it.campaignsAway >= MIN_CAMPAIGNS_AWAY }
                .sortedBy { it.character.identityKey }
                .filter { Random(seed xor it.character.identityKey.hashCode().toLong()).nextInt(100) < chance(it) }
                .maxWithOrNull(compareBy({ chance(it) }, { it.character.milestones.size }, { it.campaignsAway }))
                ?: return null
        val ch = winner.character
        val here = state.currentPlaceId
        val fame = here?.let { Fame.effects(state, it) }
        val last = ch.milestones.lastOrNull()
        val withHero = ch.milestones.any { m -> heroId in m.participantIds }
        val knowledge =
            when {
                withHero -> Certainty.CONFIRMED
                fame?.recognized == true -> Certainty.RUMOR
                else -> Certainty.UNKNOWN
            }
        val howKnew =
            when (knowledge) {
                Certainty.CONFIRMED -> "viveu os fatos com o herói e sabe por experiência própria"
                Certainty.RUMOR -> "só ouviu rumores sobre o herói (fama no lugar); não cite detalhes que ele não presenciou"
                else -> "não sabe do paradeiro do herói: o encontro é por acaso"
            }
        val reason =
            when {
                knowledge == Certainty.UNKNOWN -> ch.goals.firstOrNull()?.let { "segue os próprios objetivos: $it" } ?: "segue a própria vida e cruza o caminho do herói"
                last?.type == MilestoneType.MERCY -> "quer retribuir a misericórdia que recebeu"
                last?.type == MilestoneType.HUMILIATION || last?.type == MilestoneType.DEFEAT -> "busca revanche ou reparação pelo que sofreu"
                last?.type == MilestoneType.HELP -> "quer devolver a ajuda ou pedir um favor em troca"
                last?.type == MilestoneType.BETRAYAL -> "quer cobrar uma traição"
                else -> ch.goals.firstOrNull()?.let { "persegue o objetivo: $it" } ?: "tem assunto pendente com o herói"
            }
        val resources =
            when (ch.defeat) {
                DefeatOutcome.FLED -> "reduzidos: fugiu e precisou recomeçar; pode ter reunido aliados nesse tempo"
                DefeatOutcome.HUMILIATED -> "o que reconstruiu desde a humilhação; a influência anterior foi abalada"
                DefeatOutcome.SPARED -> "os que tinha ao ser poupado, mais o que juntou desde então"
                null -> "os do seu cargo e personalidade; nada além do que a história já mostrou"
            }
        return ReencounterPlan(
            character = ch,
            reason = reason,
            knowledge = knowledge,
            howHeKnew = howKnew,
            level = levelFor(ch),
            resources = resources,
            campaignsAway = winner.campaignsAway,
            recognizesHero = knowledge != Certainty.UNKNOWN,
            heroRecognizesNpc = withHero || ch.defeat != null,
            role = RivalReturns.role(ch),
            seeksHeir = RivalReturns.seeksHeir(ch, state),
        )
    }

    /** Instrução ao mestre: o motor já respondeu o porquê; a IA narra sem alterar nível, recursos nem o que ele sabe. */
    fun masterContext(plan: ReencounterPlan): String =
        buildString {
            append("REENCONTRO (decidido pelo motor): ${plan.character.name} aparece nesta campanha, no máximo uma vez por campanha. ")
            append("Motivo: ${plan.reason}. Como soube: ${plan.howHeKnew}. ")
            append("Passaram-se cerca de ${plan.campaignsAway} campanhas. Recursos: ${plan.resources}. Nível ${plan.level} (fixado pelo motor a partir do grupo e do cargo). ")
            append(if (plan.recognizesHero) "Ele reconhece o herói. " else "Ele não reconhece o herói. ")
            append(if (plan.heroRecognizesNpc) "O herói o reconhece. " else "O herói não o conhece. ")
            plan.role?.let { append("Papel do retorno (decidido pelo motor): ${RivalReturns.description(it)}. ") }
            if (plan.seeksHeir) append("Quem o derrotou já morreu: ele procura o herdeiro, o herói atual. ")
            append("Não altere nível nem recursos e não invente fatos que ele não viveu nem ouviu.")
        }
}

/**
 * MP-04 (D-18, parte 2): nível do NPC que volta, decidido pelo motor e nunca pela IA. Parte do nível do grupo atual e
 * soma [PER_RANK] por faixa de cargo ganha ou perdida fora de cena (MP-03), limitado a ±[MAX_SHIFT]: quem subiu muito
 * fica um pouco acima do grupo (o Boss de verdade age por intermediários, MP-06), nunca à altura de um chefe fixo.
 */
object ReencounterLevel {
    const val PER_RANK = 2
    const val MAX_SHIFT = 4

    fun forCharacter(
        ch: RecurringCharacter,
        groupLevel: Int,
    ): Int = (groupLevel + ((ch.offscreen?.rank ?: 0) * PER_RANK).coerceIn(-MAX_SHIFT, MAX_SHIFT)).coerceAtLeast(1)
}
