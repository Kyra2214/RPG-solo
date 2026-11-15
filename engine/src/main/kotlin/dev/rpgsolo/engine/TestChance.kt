package dev.rpgsolo.engine

/**
 * Fórmula única de chance dos testes (decidida pelo autor em 15/11/2026). Vale para esquiva, força, resistência, inteligência,
 * destreza, perícia, sorte, ofícios (criar, consertar, preparar, identificar), acerto e afanar.
 *
 * Chance (d100, sucesso com dado <= chance) = 50% no nível 1 + 5% por nível acima do primeiro
 * + 5% a cada 2 pontos do atributo do teste + 5% por ponto de Sorte + 5% por ponto de bônus
 * − 5% a cada 2 pontos do atributo oposto (acerto contra Agilidade, afanar) − desconto da dificuldade; sempre entre 5% e 95%.
 */
object TestChance {
    const val BASE = 50
    const val PER_LEVEL = 5
    const val ATTRIBUTE_STEP = 2
    const val PER_ATTRIBUTE_STEP = 5
    const val PER_LUCK = 5
    const val MIN = 5
    const val MAX = 95

    fun clamp(chance: Int): Int = chance.coerceIn(MIN, MAX)

    /** Bônus do atributo: 5% a cada 2 pontos (3 pontos valem o mesmo que 2). */
    fun attributeBonus(points: Int): Int = PER_ATTRIBUTE_STEP * (points.coerceAtLeast(0) / ATTRIBUTE_STEP)

    /** Chance sem desconto e sem limite: é o que o teste por chance ([Comparison.PERCENT]) guarda em `modifier`. */
    fun raw(
        attribute: Int,
        luck: Int = 0,
        level: Int = 1,
        opposing: Int = 0,
    ): Int = BASE + PER_LEVEL * (level - 1) + attributeBonus(attribute) + PER_LUCK * luck.coerceAtLeast(0) - attributeBonus(opposing)

    fun percent(
        attribute: Int,
        luck: Int = 0,
        level: Int = 1,
        bonusPoints: Int = 0,
        opposing: Int = 0,
        penalty: Int = 0,
    ): Int = clamp(raw(attribute, luck, level, opposing) + PER_LEVEL * bonusPoints - penalty)
}
