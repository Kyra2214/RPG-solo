package dev.rpgsolo.engine

/** MP-05: eixos da relação de um NPC com o herói. Cada eixo vai de 0 a 100; um NPC pode ter vários ao mesmo tempo. */
enum class RelationAxis(val label: String) {
    RESENTMENT("ressentimento"),
    RESPECT("respeito"),
    GRATITUDE("gratidão"),
    DISTRUST("desconfiança"),
    DEBT("dívida"),
    FEAR("medo"),
    PARTIAL_FORGIVENESS("perdão parcial"),
    FRIENDLY_RIVALRY("rivalidade amistosa"),
}

/** MP-05: caminho que o motor recomenda ao mestre; a IA não força luta quando outro caminho faz mais sentido. */
enum class RelationApproach(val guidance: String) {
    NONE("Sem posição forte; reaja pela personalidade."),
    NEGOTIATE("Prefira negociar ou cobrar um preço a lutar."),
    DEMAND_PRICE("Respeita o herói, mas guarda ressentimento: pode exigir reparação ou uma humilhação equivalente em troca de ajuda."),
    RECONCILE("Há espaço para reconciliação; o perdão é parcial e precisa de gesto concreto."),
    HELP("Sente-se em dívida ou grato; tende a ajudar, dentro dos próprios recursos."),
    FIGHT("O conflito direto é plausível: há rancor forte, sem medo nem respeito que o contenham."),
}

/** MP-05: relação em eixos, derivada da relação numérica e dos marcos que o NPC conhece. Não é estado novo: nada é gravado. */
data class RelationProfile(
    val npcId: String,
    val heroId: String,
    val axes: Map<RelationAxis, Int>,
    val approach: RelationApproach,
) {
    operator fun get(axis: RelationAxis): Int = axes[axis] ?: 0

    /** Até [max] eixos relevantes (>= [MIN_RELEVANT]), do mais forte ao mais fraco. */
    fun dominant(max: Int = 3): List<Pair<RelationAxis, Int>> =
        axes.entries.filter { it.value >= MIN_RELEVANT }.sortedByDescending { it.value }.take(max).map { it.key to it.value }

    companion object {
        const val MIN_RELEVANT = 15
    }
}

/**
 * MP-05. Convenção dos marcos: o primeiro participante é quem sofreu ou recebeu o ato (como em [defeatMilestone]);
 * os demais são os autores. O peso depende da certeza do NPC: viveu = 100%, suspeita = 50%, rumor = 25%.
 */
object RelationAxes {
    fun profile(
        state: GameState,
        npcId: String,
        heroId: String,
    ): RelationProfile {
        val raw = RelationAxis.values().associateWith { 0 }.toMutableMap()

        fun add(
            axis: RelationAxis,
            v: Int,
        ) {
            raw[axis] = ((raw[axis] ?: 0) + v).coerceIn(0, 100)
        }

        val r = state.relations[relationKey(npcId, heroId)] ?: 0
        if (r < 0) {
            add(RelationAxis.RESENTMENT, -r / 2)
            add(RelationAxis.DISTRUST, -r / 3)
        } else {
            add(RelationAxis.RESPECT, r / 3)
        }

        for ((m, certainty) in state.milestonesKnownBy(npcId)) {
            if (heroId !in m.participantIds || npcId !in m.participantIds) continue
            val w =
                when (certainty) {
                    Certainty.CONFIRMED -> 100
                    Certainty.SUSPECTED -> 50
                    Certainty.RUMOR -> 25
                    Certainty.UNKNOWN -> 0
                }

            fun a(
                axis: RelationAxis,
                v: Int,
            ) = add(axis, v * w / 100)
            val receiver = m.participantIds.firstOrNull() == npcId
            when (m.type) {
                MilestoneType.HUMILIATION ->
                    if (receiver) {
                        a(RelationAxis.RESENTMENT, 30)
                        a(RelationAxis.FEAR, 10)
                    }
                MilestoneType.DEFEAT ->
                    if (receiver) {
                        a(RelationAxis.RESPECT, 15)
                        a(RelationAxis.RESENTMENT, 15)
                        a(RelationAxis.FEAR, 20)
                    } else {
                        a(RelationAxis.RESPECT, 10)
                    }
                MilestoneType.MERCY ->
                    if (receiver) {
                        a(RelationAxis.GRATITUDE, 30)
                        a(RelationAxis.DEBT, 30)
                        a(RelationAxis.RESPECT, 10)
                    }
                MilestoneType.HELP ->
                    if (receiver) {
                        a(RelationAxis.GRATITUDE, 30)
                        a(RelationAxis.DEBT, 20)
                    }
                MilestoneType.DEBT_PAID -> if (receiver) a(RelationAxis.GRATITUDE, 10)
                MilestoneType.BETRAYAL ->
                    if (receiver) {
                        a(RelationAxis.RESENTMENT, 40)
                        a(RelationAxis.DISTRUST, 40)
                    }
                MilestoneType.DECEIT -> if (receiver) a(RelationAxis.DISTRUST, 30)
                MilestoneType.INJUSTICE -> if (receiver) a(RelationAxis.RESENTMENT, 25)
                MilestoneType.PROMISE -> a(RelationAxis.RESPECT, 10)
                MilestoneType.HEROIC_ACT -> a(RelationAxis.RESPECT, 25)
                MilestoneType.DEATH -> {}
            }
        }

        // Derivados: perdão parcial nasce de rancor com gratidão; rivalidade amistosa, de rancor leve com respeito e sem desconfiança.
        val resentment = raw.getValue(RelationAxis.RESENTMENT)
        val gratitude = raw.getValue(RelationAxis.GRATITUDE)
        val respect = raw.getValue(RelationAxis.RESPECT)
        val fear = raw.getValue(RelationAxis.FEAR)
        val distrust = raw.getValue(RelationAxis.DISTRUST)
        val forgiveness = minOf(resentment, gratitude) / 2
        raw[RelationAxis.PARTIAL_FORGIVENESS] = forgiveness
        raw[RelationAxis.FRIENDLY_RIVALRY] = if (respect >= 20 && resentment in 10..40 && distrust < 30) minOf(respect, resentment) else 0

        val approach =
            when {
                resentment >= 50 && respect >= 30 -> RelationApproach.DEMAND_PRICE
                resentment >= 40 && (gratitude >= 30 || forgiveness >= 20) -> RelationApproach.RECONCILE
                resentment >= 60 && fear < 30 && respect < 30 -> RelationApproach.FIGHT
                resentment >= 40 -> RelationApproach.NEGOTIATE
                gratitude + raw.getValue(RelationAxis.DEBT) >= 40 && resentment < 30 -> RelationApproach.HELP
                distrust >= 30 -> RelationApproach.NEGOTIATE
                else -> RelationApproach.NONE
            }
        return RelationProfile(npcId, heroId, raw, approach)
    }

    /**
     * MP-05 (adaptador, D-04): id que o NPC recorrente tinha na campanha de origem, deduzido dos marcos guardados
     * (participante, fora o herói atual, presente no maior número de marcos; empate desfeito por ordem alfabética,
     * para o resultado ser determinístico). Nulo sem marcos ou sem outro participante.
     */
    fun originNpcId(
        character: RecurringCharacter,
        heroId: String,
    ): String? =
        character.milestones
            .flatMap { it.participantIds.distinct() }
            .filter { it != heroId }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .firstOrNull()
            ?.key

    /**
     * MP-05 (adaptador, D-04): perfil em eixos de um NPC recorrente de campanha antiga, derivado só dos marcos que ele
     * guarda (a relação numérica é da campanha de origem e não é persistida, então conta 0). Nada é gravado.
     * ASSUMINDO: o herói de campanhas diferentes usa o mesmo id ([heroId]); marcos com outro herói não contam.
     */
    fun profileOf(
        character: RecurringCharacter,
        heroId: String,
    ): RelationProfile? {
        if (character.kind != RecurringKind.NPC) return null
        val npcId = originNpcId(character, heroId) ?: return null
        val synthetic = GameState(milestones = character.milestones.associateBy { it.id })
        return profile(synthetic, npcId, heroId)
    }

    /** Texto para o mestre sobre um NPC recorrente (campanha antiga); null quando nada é relevante. */
    fun contextOf(
        character: RecurringCharacter,
        heroId: String,
    ): String? {
        val p = profileOf(character, heroId) ?: return null
        val dom = p.dominant()
        if (dom.isEmpty()) return null
        return "Relação de ${character.name} com o herói: " + dom.joinToString(", ") { (ax, v) -> "${ax.label} $v" } +
            ". ${p.approach.guidance} Não force luta se negociar, cobrar um preço ou reconciliar fizer mais sentido."
    }

    /** Texto para o contexto do mestre; null quando nada é relevante. */
    fun context(
        state: GameState,
        npcId: String,
        heroId: String,
    ): String? {
        val p = profile(state, npcId, heroId)
        val dom = p.dominant()
        if (dom.isEmpty()) return null
        val name = state.entities[npcId]?.name ?: npcId
        return "Relação de $name com o herói: " + dom.joinToString(", ") { (ax, v) -> "${ax.label} $v" } +
            ". ${p.approach.guidance} Não force luta se negociar, cobrar um preço ou reconciliar fizer mais sentido."
    }
}
