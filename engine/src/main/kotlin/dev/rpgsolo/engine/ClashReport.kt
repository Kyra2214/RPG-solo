package dev.rpgsolo.engine

/**
 * MU-04.1: detalhamento de um confronto, gerado só a partir do [ClashRecorded] persistido (o que o motor de fato
 * usou). A interface e o histórico apresentam isto e nunca recalculam. Serve igual para jogador e IA.
 */
data class ClashReport(
    val attackerId: String,
    val targetId: String,
    val attackSources: List<String>,
    val attackValue: Int,
    val attackExpression: String,
    /** Nulo quando a rolagem de ataque foi pulada (crítico ou defesa ignorada). */
    val attackDice: List<Int>?,
    val attackTotal: Int,
    val defenseSources: List<String>,
    val defenseValue: Int,
    val defenseExpression: String,
    val defenseDice: List<Int>?,
    val defenseTotal: Int,
    val critical: Boolean,
    val ignoredDefense: Boolean,
    val damage: Int,
    val rejectedSources: List<String>,
    val critLine: String?,
    val ignoreLine: String?
) {
    /** Linhas em português para o cartão/histórico (strings localizadas ficam para a interface, U-03). */
    fun lines(): List<String> = buildList {
        add("Ataque de $attackerId em $targetId")
        addAll(attackSources.map { "• $it" })
        add("Valor efetivo: $attackValue · expressão $attackExpression")
        critLine?.let { add(it) }
        ignoreLine?.let { add(it) }
        add(
            when {
                ignoredDefense -> "Dano sem dado (defesa ignorada): $attackTotal"
                critical -> "Dano cheio sem dado (crítico): $attackTotal"
                else -> "Resultado do ataque: ${attackDice.orEmpty().joinToString("+")} → $attackTotal"
            }
        )
        if (!ignoredDefense) {
            addAll(defenseSources.map { "• $it" })
            add("Defesa: valor $defenseValue · $defenseExpression → ${defenseDice.orEmpty().joinToString("+")} = $defenseTotal")
        } else add("Defesa do alvo: ignorada")
        addAll(rejectedSources.map { "Não aplicado: $it" })
        add("Dano final: $damage")
    }

    companion object {
        fun of(e: ClashRecorded): ClashReport = ClashReport(
            e.attackerId, e.targetId,
            e.attackSources, e.attackValue, e.attackExpression, e.attackDice.takeIf { it.isNotEmpty() }, e.attackTotal,
            e.defenseSources, e.defenseValue, e.defenseExpression, e.defenseDice.takeIf { it.isNotEmpty() }, e.defenseTotal,
            e.critical, e.ignoredDefense, e.damage, e.rejectedSources,
            e.critThreshold?.let { t ->
                when {
                    e.critGuaranteed -> "Crítico garantido (100%)"
                    else -> "Chance de crítico: dado ${e.critRoll} (precisa de $t+)${if (e.critical) " → crítico" else ""}"
                }
            },
            e.ignoreThreshold?.let { t ->
                when {
                    e.ignoreGuaranteed -> "Ignorar defesa garantido (100%)"
                    else -> "Ignorar defesa: dado ${e.ignoreRoll} (precisa de $t+)${if (e.ignoredDefense) " → ignorou" else ""}"
                }
            }
        )
    }
}
