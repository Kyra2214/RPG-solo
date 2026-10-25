package dev.rpgsolo.ai

import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.Kennel
import dev.rpgsolo.engine.KennelWeekSettled
import dev.rpgsolo.engine.SettleKennel

/**
 * N-33.2c: liquidação do criadouro quando o tempo passa por ação do app (descanso, trazer animal). O motor não mexe em
 * prata: aqui se calcula, com o estado já avançado [days] dias, quanto as semanas viradas custam e monta o lote de pagamento.
 */
object KennelSettlement {
    /** Lote a rodar depois do avanço de dias: [commands] (pagamento e [SettleKennel]), [silver] cobrado e [weeks] liquidadas. */
    data class Result(val commands: List<Command>, val silver: Int, val weeks: List<KennelWeekSettled>)

    /**
     * [budget]: saldo em prata do herói; [reserved]: parte dele já comprometida com outra cobrança do mesmo lote
     * (ex.: taxa de trazer o animal), que também entra em [pay]. [pay] devolve os comandos que consomem o total
     * (null se o saldo não cobre). Devolve `null` se nenhuma semana virou ou se o pagamento não é possível.
     */
    fun after(
        state: GameState,
        days: Int,
        budget: Int,
        reserved: Int = 0,
        breederDegree: Int = 0,
        pay: (Int) -> List<Command>?,
    ): Result? {
        val free = maxOf(0, budget - reserved)
        val plan = Kennel.plan(state.copy(day = state.day + days), free, breederDegree) ?: return null
        val payment = pay(reserved + plan.totalSilver) ?: return null
        return Result(payment + SettleKennel(free, breederDegree), plan.totalSilver, plan.events)
    }
}
