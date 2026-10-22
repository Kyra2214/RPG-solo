package dev.rpgsolo.engine

/** Resultado de tentar remover uma condição por teste (M-05). */
sealed interface ConditionRemovalResult {
    data class Removed(val check: CheckResult) : ConditionRemovalResult
    data class Kept(val check: CheckResult) : ConditionRemovalResult
    data class Rejected(val reason: String) : ConditionRemovalResult
}

/**
 * Rola [spec] e, com sucesso, remove [condition] de [entityId]. Falha ou sucesso parcial mantêm a condição
 * (a rolagem fica no log). Atômico: recusa ou erro desfaz a rolagem.
 */
fun GameEngine.removeConditionByCheck(entityId: String, condition: String, spec: CheckSpec): ConditionRemovalResult {
    val ent = state.entities[entityId] ?: return ConditionRemovalResult.Rejected("entidade inexistente: $entityId")
    if (!ent.alive) return ConditionRemovalResult.Rejected("${ent.name} está morto")
    if (state.conditions[entityId]?.contains(condition) != true) return ConditionRemovalResult.Rejected("condição ausente: $condition")
    val start = mark()
    return try {
        val result = check(spec)
        if (!result.outcome.isSuccess) return ConditionRemovalResult.Kept(result)
        when (val r = executeAll(listOf(RemoveCondition(entityId, condition)))) {
            is ExecResult.Rejected -> { rollbackTo(start); ConditionRemovalResult.Rejected(r.reason) }
            else -> ConditionRemovalResult.Removed(result)
        }
    } catch (e: Exception) {
        rollbackTo(start)
        throw e
    }
}

/** Versão persistida: a rolagem e a remoção são gravadas juntas. */
fun CampaignSession.removeConditionByCheck(entityId: String, condition: String, spec: CheckSpec): ConditionRemovalResult = atomically {
    val start = mark()
    try {
        engine.removeConditionByCheck(entityId, condition, spec).also { save() }
    } catch (e: Exception) {
        engine.rollbackTo(start)
        throw e
    }
}
