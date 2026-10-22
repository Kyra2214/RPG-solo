package dev.rpgsolo.engine

/** Resultado de um teste de sobrevivência do caído (M-04). */
sealed interface SurvivalResult {
    /** Sucesso pleno: o recurso de morte sobe a 1 e a entidade se reergue. */
    data class Recovered(val check: CheckResult) : SurvivalResult

    /** Sucesso parcial: nada muda. */
    data class Stable(val check: CheckResult) : SurvivalResult

    /** Falha: [failures] acumuladas; [died] se atingiu o limite. */
    data class Failed(val check: CheckResult, val failures: Int, val died: Boolean) : SurvivalResult

    data class Rejected(val reason: String) : SurvivalResult
}

/**
 * Teste de sobrevivência de um personagem caído. Rola [spec], e então recupera (sucesso pleno), mantém (parcial)
 * ou registra falha (morre ao chegar a [failuresToDie]). Atômico: qualquer recusa ou erro desfaz a rolagem.
 */
fun GameEngine.survivalTest(
    entityId: String,
    spec: CheckSpec,
    failuresToDie: Int,
): SurvivalResult {
    val ent = state.entities[entityId] ?: return SurvivalResult.Rejected("entidade inexistente: $entityId")
    if (!ent.alive || !ent.downed) return SurvivalResult.Rejected("${ent.name} não está caído")
    val resource = config.deathResource ?: return SurvivalResult.Rejected("o sistema não define recurso de morte")
    if (failuresToDie !in 1..10) return SurvivalResult.Rejected("limite de falhas deve ficar entre 1 e 10")
    val start = mark()
    return try {
        val result = check(spec)
        val outcome = result.outcome
        when {
            outcome.isSuccess ->
                when (val r = executeAll(listOf(Heal(entityId, resource, 1)))) {
                    is ExecResult.Rejected -> {
                        rollbackTo(start)
                        SurvivalResult.Rejected(r.reason)
                    }
                    else -> SurvivalResult.Recovered(result)
                }
            outcome.isFailure ->
                when (val r = executeAll(listOf(FailSurvival(entityId, failuresToDie)))) {
                    is ExecResult.Rejected -> {
                        rollbackTo(start)
                        SurvivalResult.Rejected(r.reason)
                    }
                    else -> {
                        val after = state.entities.getValue(entityId)
                        SurvivalResult.Failed(result, after.deathFailures, !after.alive)
                    }
                }
            else -> SurvivalResult.Stable(result)
        }
    } catch (e: Exception) {
        rollbackTo(start)
        throw e
    }
}

/** Versão persistida: a rolagem e o efeito são gravados juntos. */
fun CampaignSession.survivalTest(
    entityId: String,
    spec: CheckSpec,
    failuresToDie: Int,
): SurvivalResult =
    atomically {
        val start = mark()
        try {
            engine.survivalTest(entityId, spec, failuresToDie).also { save() }
        } catch (e: Exception) {
            engine.rollbackTo(start)
            throw e
        }
    }
