package dev.rpgsolo.engine

/** N-35: efeitos da Sorte além do crítico. */
object LuckRules {
    const val POINTS_PER_TEST_BONUS = 2

    /** +1 em todos os testes a cada 2 pontos de Sorte; sem teto (o teto de 70% vale só para o crítico). */
    fun testBonus(luckPoints: Int): Int = maxOf(0, luckPoints) / POINTS_PER_TEST_BONUS

    /** +1% de chance de drop por ponto de Sorte (itens e gemas contam); soma ao valor base da tabela de drop. */
    fun dropPercentBonus(luckPoints: Int): Int = maxOf(0, luckPoints)
}
