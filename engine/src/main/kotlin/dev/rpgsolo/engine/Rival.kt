package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/**
 * RV-02: um rival registrado. Nasce de um NPC recorrente vivo que terminou um combate sem morrer (RV-01: poupado, fugiu
 * ou humilhado). Só reúne o que o motor já gravou; nenhum fato novo e nada vem da IA. O papel do retorno (vilão,
 * chefão, aliado, favor, redenção) é decidido nos módulos seguintes (RV-04 e RV-05), não aqui.
 *
 * [targetName] é quem o derrotou; [targetDead] = esse herói já morreu, então o rival procura o herdeiro (liga com o LG-03).
 */
@Serializable
data class Rival(
    val identityKey: String,
    val name: String,
    val outcome: DefeatOutcome,
    val attitude: DefeatAttitude,
    val targetName: String,
    val targetDead: Boolean,
    val worldId: String = "",
    val originCampaignId: String = "",
    /** Ordem de prioridade do registro: menor = mais urgente (ressentimento antes de dívida e respeito). */
    val priority: Int = 0,
)

object Rivals {
    /**
     * Registro de rivais de um mundo: NPCs recorrentes vivos com derrota sem morte. Ordena por prioridade e depois por
     * identidade, de forma determinística. Não exige [RecurringCharacter.defeatedBy]: sem ele o alvo fica em branco
     * (dados anteriores ao RV-02) e o rival continua válido.
     */
    fun register(characters: List<RecurringCharacter>): List<Rival> {
        val deadHeroes =
            characters.filter { it.kind == RecurringKind.HERO && !it.alive }.map {
                RecurringIdentity.normalize(
                    it.name,
                )
            }.toSet()
        return characters
            .filter { it.kind == RecurringKind.NPC && it.alive && it.defeat != null }
            .map { c ->
                val outcome = checkNotNull(c.defeat)
                val attitude = c.defeatAttitude ?: DefeatAttitude.RESENTMENT
                Rival(
                    identityKey = c.identityKey,
                    name = c.name,
                    outcome = outcome,
                    attitude = attitude,
                    targetName = c.defeatedBy,
                    targetDead = c.defeatedBy.isNotBlank() && RecurringIdentity.normalize(c.defeatedBy) in deadHeroes,
                    worldId = c.worldId,
                    originCampaignId = c.originCampaignId,
                    priority = priority(outcome, attitude),
                )
            }.sortedWith(compareBy({ it.priority }, { it.identityKey }))
    }

    /** Ressentimento primeiro; humilhado antes de fugiu e de poupado. */
    internal fun priority(
        outcome: DefeatOutcome,
        attitude: DefeatAttitude,
    ): Int =
        when (attitude) {
            DefeatAttitude.RESENTMENT -> 0
            DefeatAttitude.DEBT -> 10
            DefeatAttitude.RESPECT -> 20
        } +
            when (outcome) {
                DefeatOutcome.HUMILIATED -> 0
                DefeatOutcome.FLED -> 1
                DefeatOutcome.SPARED -> 2
            }
}
