package dev.rpgsolo.engine

/** MU-01.1: expressão de dados derivada de um valor efetivo. Não é o valor efetivo nem o resultado da rolagem. */
data class DiceExpression(val count: Int, val sides: Int, val modifier: Int) {
    fun roll(dice: Dice): RollResult = dice.roll(count, sides, modifier)

    override fun toString(): String =
        "${count}d$sides${if (modifier == 0) {
            ""
        } else if (modifier > 0) {
            "+$modifier"
        } else {
            "$modifier"
        }}"
}

object EffectiveValue {
    /** Faixa descrita na regra original; fora dela vale a extensão do MU-01.2. */
    const val MIN = 3
    const val MAX = 12

    /** Limite de dados de [Dice.roll]; acima dele o excedente vira bônus. */
    const val MAX_DICE = 100

    /**
     * Valor >= 6: dados = valor / 6 (inteiro), bônus = resto. Reproduz a regra original
     * (6 -> 1d6, 7..11 -> 1d6+1..+5, 12 -> 2d6) e estende: 13 -> 2d6+1, 18 -> 3d6.
     * Valor < 6: 1d6 − (6 − valor), como 3..5 da regra original; valores < 3 são extensão
     * (ASSUMINDO), e a rolagem pode dar resultado negativo, que o dano limita a 0 depois.
     */
    fun expressionFor(value: Int): DiceExpression =
        if (value >= 6) {
            val count = (value / 6).coerceAtMost(MAX_DICE)
            DiceExpression(count, 6, value - count * 6)
        } else {
            DiceExpression(1, 6, value - 6)
        }
}
