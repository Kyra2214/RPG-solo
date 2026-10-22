package dev.rpgsolo.rules

import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.RegraAdicional

data class EffectLine(val stat: String, val before: Int, val after: Int, val outOfRange: Boolean)

/** O que uma regra adicional faria, em números, antes de o jogador aprovar (4.4.2). */
data class AdaptationPreview(
    val rule: RegraAdicional,
    val lines: List<EffectLine>,
    /** Alvos que o pacote não conhece. Nunca se importa mecânica de outro sistema sem declarar (4.4.5). */
    val unknownTargets: List<String>,
    val requiresApproval: Boolean,
    val reasons: List<String>,
)

sealed interface AdaptationResult {
    data class Applied(val ficha: Ficha) : AdaptationResult

    data class Refused(val reasons: List<String>) : AdaptationResult
}

/** Mudança total (soma dos valores absolutos) a partir da qual o jogador precisa aprovar (4.4.3). */
const val DEFAULT_APPROVAL_THRESHOLD = 2

fun RulePackage.previewAdaptation(
    ficha: Ficha,
    rule: RegraAdicional,
    approvalThreshold: Int = DEFAULT_APPROVAL_THRESHOLD,
): AdaptationPreview {
    val unknown = rule.effects.keys.filter { stat(it) == null }.sorted()
    val lines =
        rule.effects.filter { stat(it.key) != null }.map { (id, delta) ->
            val def = stat(id)!!
            val before = ficha.stats[id] ?: def.default
            val after = before + delta
            EffectLine(id, before, after, after !in def.min..def.max)
        }.sortedBy { it.stat }
    val total = rule.effects.values.sumOf { kotlin.math.abs(it) }
    val reasons = mutableListOf<String>()
    if (total >= approvalThreshold) reasons += "altera $total ponto(s) no total (limite sem aprovação: ${approvalThreshold - 1})"
    if (lines.any { it.outOfRange }) reasons += "leva atributo para fora dos limites do pacote"
    if (unknown.isNotEmpty()) reasons += "alvo desconhecido no pacote: ${unknown.joinToString()}"
    return AdaptationPreview(rule, lines, unknown, reasons.isNotEmpty(), reasons)
}

/**
 * Aplica a adaptação à ficha. Recusa se: alvo desconhecido, atributo fora dos limites, regra já aplicada,
 * ou aprovação obrigatória sem aprovação. A estrutura da ficha não muda; ela ganha o selo de adaptação (4.4.4).
 */
fun RulePackage.applyAdaptation(
    ficha: Ficha,
    rule: RegraAdicional,
    approvalThreshold: Int = DEFAULT_APPROVAL_THRESHOLD,
): AdaptationResult {
    val preview = previewAdaptation(ficha, rule, approvalThreshold)
    val refusals = mutableListOf<String>()
    if (rule.id in ficha.adaptations) refusals += "regra adicional já aplicada: ${rule.id}"
    if (preview.unknownTargets.isNotEmpty()) refusals += "alvo desconhecido no pacote: ${preview.unknownTargets.joinToString()}"
    if (preview.lines.any { it.outOfRange }) refusals += "efeito leva atributo para fora dos limites do pacote"
    val needsApproval = preview.requiresApproval && preview.unknownTargets.isEmpty() && preview.lines.none { it.outOfRange }
    if (needsApproval && !rule.approved) refusals += "aprovação do jogador obrigatória: ${preview.reasons.joinToString("; ")}"
    if (refusals.isNotEmpty()) return AdaptationResult.Refused(refusals)
    val newStats = ficha.stats + preview.lines.associate { it.stat to it.after }
    return AdaptationResult.Applied(
        ficha.copy(stats = newStats, resources = resourcesFor(newStats, ficha.traits), adaptations = ficha.adaptations + rule.id),
    )
}
