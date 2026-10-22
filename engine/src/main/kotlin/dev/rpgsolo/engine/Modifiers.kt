package dev.rpgsolo.engine

/**
 * MU-01.3: modificador de qualquer origem (atributo, perícia, habilidade, item, buff, bênção, condição).
 * [sourceId] é um ID estável da origem (não o nome exibido); [target] é a característica afetada.
 * Nulo em [remainingRounds] e [usesLeft] = sem limite; 0 ou menos = expirado.
 * [stackable] só deve ser true quando uma regra explícita autoriza repetir a mesma origem.
 */
data class Modifier(
    val sourceId: String,
    val label: String,
    val target: String,
    val value: Int,
    val remainingRounds: Int? = null,
    val usesLeft: Int? = null,
    val stackable: Boolean = false,
) {
    val expired: Boolean get() = (remainingRounds != null && remainingRounds <= 0) || (usesLeft != null && usesLeft <= 0)
}

enum class RejectReason { EXPIRED, DUPLICATE_SOURCE }

data class RejectedModifier(val modifier: Modifier, val reason: RejectReason)

/** Detalhamento do cálculo: a interface e o histórico leem isto, nunca recalculam. */
data class EffectiveBreakdown(
    val target: String,
    val base: Int,
    val applied: List<Modifier>,
    val rejected: List<RejectedModifier>,
) {
    val value: Int get() = base + applied.sumOf { it.value }
    val expression: DiceExpression get() = EffectiveValue.expressionFor(value)
}

object ModifierAggregator {
    /**
     * valorEfetivo = base + soma(modificadores ativos). Expirados são excluídos; fontes diferentes
     * acumulam (inclusive com valores iguais); a mesma fonte não repete na mesma característica,
     * salvo [Modifier.stackable]. A ordem de entrada decide qual duplicata fica (a primeira).
     */
    fun aggregate(
        target: String,
        base: Int,
        modifiers: List<Modifier>,
    ): EffectiveBreakdown {
        val applied = mutableListOf<Modifier>()
        val rejected = mutableListOf<RejectedModifier>()
        val seen = mutableSetOf<String>()
        for (m in modifiers) {
            if (m.target != target) continue
            when {
                m.expired -> rejected += RejectedModifier(m, RejectReason.EXPIRED)
                !m.stackable && !seen.add(m.sourceId) -> rejected += RejectedModifier(m, RejectReason.DUPLICATE_SOURCE)
                else -> applied += m
            }
        }
        return EffectiveBreakdown(target, base, applied, rejected)
    }
}

/** MU-01.4: duração sorteada (1d6 de rodadas), sem animação; o valor sorteado fica gravado no efeito. */
data class RolledDuration(val rounds: Int, val roll: RollResult)

object EffectDuration {
    const val ROLLED_SIDES = 6

    /** Rola 1d6 de rodadas com o [Dice] determinístico da sessão (mesma regra para jogador, NPC e inimigo). */
    fun rollRounds(dice: Dice): RolledDuration {
        val roll = dice.roll(1, ROLLED_SIDES)
        return RolledDuration(roll.total, roll)
    }

    /** Cria o modificador já com a duração sorteada gravada em [Modifier.remainingRounds]. */
    fun withRolledDuration(
        modifier: Modifier,
        dice: Dice,
    ): Pair<Modifier, RolledDuration> {
        val rolled = rollRounds(dice)
        return modifier.copy(remainingRounds = rolled.rounds) to rolled
    }

    /**
     * Fim de rodada: desconta 1 de cada duração por rodadas e remove os que chegam a 0
     * (ASSUMINDO: o efeito vale até o fim da última rodada sorteada). Sem limite de rodadas = permanece.
     */
    fun endOfRound(modifiers: List<Modifier>): List<Modifier> =
        modifiers.mapNotNull { m ->
            val left = m.remainingRounds?.minus(1)
            val next = if (left != null) m.copy(remainingRounds = left) else m
            if (next.expired) null else next
        }

    /** Consome um uso de efeitos por usos; remove os esgotados. Chamar uma vez por uso real do efeito. */
    fun consumeUse(
        modifiers: List<Modifier>,
        sourceId: String,
    ): List<Modifier> =
        modifiers.mapNotNull { m ->
            if (m.sourceId != sourceId || m.usesLeft == null) {
                m
            } else {
                m.copy(usesLeft = m.usesLeft - 1).takeUnless { it.expired }
            }
        }
}
