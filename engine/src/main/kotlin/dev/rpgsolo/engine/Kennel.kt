package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/**
 * N-33.2a: criadouro, manutenção semanal e perda do animal (decisões do proprietário em `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md`, §15.7).
 * Só 1 animal anda com o herói; os outros ficam guardados no criadouro ([CompanionState.stabled]), pagos por semana de jogo.
 * O motor não mexe em prata: [Kennel.plan] diz quanto custou a liquidação e quem chama consome esse valor no mesmo lote.
 */
object Kennel {
    /** Semana de jogo = 7 dias do relógio da campanha ([GameState.day]). */
    const val WEEK_DAYS = 7

    /** Manutenção por semana: 5 prata × nível do animal guardado. */
    const val ANIMAL_FEE_PER_LEVEL = 5

    /** Salário semanal do cuidador: 10 prata × quantidade de animais guardados (o aluguel está embutido nos custos). */
    const val KEEPER_FEE_PER_ANIMAL = 10

    /** Lealdade perdida por semana sem pagamento. */
    const val UNPAID_LOYALTY_LOSS = 30

    /** Lealdade perdida por semana, com o animal pago, sem visita. */
    const val UNVISITED_LOYALTY_LOSS = 10

    /** A visita rende 1d6 de lealdade (rolado por quem chama). */
    const val VISIT_DIE_SIDES = 6

    /** Trazer um animal do criadouro: 10 prata fixas e 1 dia para chegar; quem chama cobra a prata e avança o dia no mesmo lote. */
    const val BRING_FEE = 10
    const val BRING_DAYS = 1

    /** Proteção contra saltos absurdos de calendário em dados corrompidos. */
    const val MAX_SETTLE_WEEKS = 1_000

    fun weekOf(day: Int): Int = day / WEEK_DAYS

    fun animalFee(animalLevel: Int): Int = ANIMAL_FEE_PER_LEVEL * maxOf(1, animalLevel)

    fun keeperFee(stabledCount: Int): Int = KEEPER_FEE_PER_ANIMAL * stabledCount

    /** Resultado da liquidação: eventos (um por semana) e total de prata a consumir. */
    data class Plan(val events: List<KennelWeekSettled>, val totalSilver: Int)

    /**
     * Liquida todas as semanas que viraram desde a última liquidação até [GameState.day], com [silver] de saldo disponível
     * (em prata). Cada semana: paga primeiro o cuidador e depois os animais, do de menor nível para o de maior, até acabar a
     * prata (cada cobrança é inteira; ao primeira que não cabe, ela e as seguintes ficam sem pagamento). Sem pagamento: -30 de
     * lealdade; pago e sem visita na semana: -10; pago e visitado: 0. Lealdade em 0 perde o animal. Devolve `null` quando não
     * há semana a liquidar.
     *
     * N-33.3: [breederDegree] (grau de Criador de Animais, 0 a 5) soma +1 de lealdade por grau, por semana, a todos os
     * animais que o herói ainda tem (guardados ou não), depois das quedas da semana; quem chegou a 0 nela já está perdido e
     * não recebe. Teto 100.
     */
    fun plan(
        state: GameState,
        silver: Int,
        breederDegree: Int = 0,
    ): Plan? {
        val from = state.kennelWeek ?: return null
        val to = weekOf(state.day)
        if (to <= from || to - from > MAX_SETTLE_WEEKS) return null
        val loyalty = state.companions.mapValues { it.value.loyalty }.toMutableMap()
        val lost = state.companions.filterValues { it.lost }.keys.toMutableSet()
        var budget = maxOf(0, silver)
        var spent = 0
        val events = mutableListOf<KennelWeekSettled>()
        for (week in (from + 1)..to) {
            val guarded =
                state.companions
                    .filter { (id, c) -> c.stabled && id !in lost && state.entities[id]?.alive == true }
                    .keys
                    .sortedWith(compareBy({ state.entities.getValue(it).level }, { it }))
            val changes = mutableListOf<KennelLoyalty>()
            var weekSilver = 0
            var keeperPaid = guarded.isEmpty()
            if (guarded.isNotEmpty()) {
                var stillPaying = true
                val keeper = keeperFee(guarded.size)
                if (budget >= keeper) {
                    budget -= keeper
                    weekSilver += keeper
                    keeperPaid = true
                } else {
                    stillPaying = false
                }
                for (id in guarded) {
                    val fee = animalFee(state.entities.getValue(id).level)
                    val paid = stillPaying && budget >= fee
                    if (paid) {
                        budget -= fee
                        weekSilver += fee
                    } else {
                        stillPaying = false
                    }
                    val visited = state.companions.getValue(id).visitedWeek >= week - 1
                    val loss =
                        when {
                            !paid -> UNPAID_LOYALTY_LOSS
                            !visited -> UNVISITED_LOYALTY_LOSS
                            else -> 0
                        }
                    if (loss > 0) {
                        val now = maxOf(0, loyalty.getValue(id) - loss)
                        changes += KennelLoyalty(id, now - loyalty.getValue(id), now, paid)
                        loyalty[id] = now
                    }
                }
            }
            val lostNow = changes.filter { it.newValue == 0 }.map { it.animalId }
            if (breederDegree > 0) {
                val gain = breederDegree.coerceAtMost(Companions.TAMER_DEGREE_MAX)
                val candidates =
                    state.companions
                        .filter { (id, c) -> id !in lost && id !in lostNow && !c.lost && state.entities[id]?.alive == true }
                        .keys
                        .sorted()
                for (id in candidates) {
                    val before = loyalty.getValue(id)
                    val now = minOf(Companions.MAX_LOYALTY, before + gain)
                    if (now == before) continue
                    val idx = changes.indexOfFirst { it.animalId == id }
                    if (idx >= 0) {
                        val old = changes[idx]
                        changes[idx] = old.copy(delta = old.delta + (now - before), newValue = now)
                    } else {
                        changes += KennelLoyalty(id, now - before, now, true)
                    }
                    loyalty[id] = now
                }
            }
            lost += lostNow
            spent += weekSilver
            events += KennelWeekSettled(week, weekSilver, keeperPaid, changes, lostNow)
        }
        return Plan(events, spent)
    }
}

/** Mudança de lealdade de um animal guardado numa semana; [paid] diz se a manutenção dele foi paga. */
@Serializable
data class KennelLoyalty(val animalId: String, val delta: Int, val newValue: Int, val paid: Boolean)

/** Liquida as semanas pendentes do criadouro com [silver] de saldo (prata); o consumo de [KennelWeekSettled.silverPaid] é de quem chama. */
data class SettleKennel(val silver: Int, val breederDegree: Int = 0) : Command

/** Deixa o animal no criadouro (sai do lado do herói; a semana atual conta como visitada). Recusado em combate. */
data class StableCompanion(val animalId: String) : Command

/** Visita ao animal guardado: conta como carinho (1 por dia) e rende [roll] (1d6, já rolado) de lealdade, até 100. */
data class VisitCompanion(val animalId: String, val roll: Int) : Command

/**
 * Traz o animal guardado para andar com o herói e guarda no criadouro o que andava com ele (troca). Recusado em combate.
 * Quem chama (app) confere que o herói está numa cidade e, antes deste comando e no mesmo lote, consome [Kennel.BRING_FEE]
 * de prata e avança [Kennel.BRING_DAYS] dia ([AdvanceDays]), para a semana de visita do animal que sai ser a atual.
 */
data class BringCompanion(val animalId: String) : Command

/**
 * N-33.2d: o herói tenta adestrar de novo um animal perdido (lealdade 0). [baseChance] é a chance (%) normal da receita de
 * adestrar, calculada por quem chama; o motor tira [Companions.RETRAIN_PENALTY] pontos, compara com [percentRoll] (d100, acerta
 * com dado <= chance) e, no sucesso, o animal volta a andar com o herói com a lealdade [loyaltyRoll] (d20), mantendo nível,
 * ataque e truques. A falha gasta uma das [Companions.RETRAIN_MAX_ATTEMPTS] tentativas; na segunda falha o animal é perdido
 * para sempre. Quem chama consome o item de adestrar de cada tentativa no mesmo lote. [maxAnimals] como em [TameCompanion].
 */
data class RetrainCompanion(
    val animalId: String,
    val baseChance: Int,
    val percentRoll: Int,
    val loyaltyRoll: Int,
    val maxAnimals: Int? = null,
) : Command

@Serializable
data class CompanionRetrained(
    val animalId: String,
    val attempt: Int,
    val chance: Int,
    val roll: Int,
    val success: Boolean,
    val newLoyalty: Int,
    /** Animais que andavam com o herói e vão para o criadouro com a volta do readestrado. */
    val stabledIds: List<String> = emptyList(),
    val week: Int,
    /** Falhou na última tentativa: perdido para sempre. */
    val abandoned: Boolean = false,
) : Event

/**
 * N-33.3: acerta os pontos de status do Treinador ([degree], 0 a 5): o animal que anda com o herói (vivo, não guardado nem
 * perdido) recebe [Companions.trainerPoints]; os demais perdem o que tinham (o ponto pertence ao herói). Sem diferença,
 * não faz nada. Quem chama inclui no lote depois de adestrar, trazer, guardar ou readestrar e quando o grau mudar.
 */
data class SyncTrainerBonus(val ownerId: String, val degree: Int) : Command

/** Novo bônus do Treinador por animal (substitui o anterior; vazio remove). */
@Serializable
data class TrainerBonusSynced(val bonus: Map<String, Map<String, Int>>) : Event

@Serializable
data class CompanionBrought(val animalId: String, val stabledIds: List<String>, val week: Int) : Event

/** A [week] virou: [silverPaid] foi cobrado, [lost] são os animais cuja lealdade chegou a 0 (perdidos). */
@Serializable
data class KennelWeekSettled(
    val week: Int,
    val silverPaid: Int,
    val keeperPaid: Boolean,
    val changes: List<KennelLoyalty>,
    val lost: List<String> = emptyList(),
) : Event

@Serializable
data class CompanionStabled(val animalId: String, val week: Int) : Event

@Serializable
data class CompanionVisited(val animalId: String, val roll: Int, val newValue: Int, val delta: Int, val day: Int, val week: Int) : Event

/** Motivo de o animal não poder agir ou receber cuidado do herói agora; null = disponível. */
internal fun CompanionState.awayReason(): String? =
    when {
        abandoned -> "o animal foi perdido para sempre"
        lost -> "o animal foi perdido"
        stabled -> "o animal está no criadouro"
        else -> null
    }
