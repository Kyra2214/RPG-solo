package dev.rpgsolo.engine

/**
 * LG-06: cartão do memorial do herói desta campanha. Só a parte pública, derivada do legado que o motor já gravou
 * (LG-01); nunca ficha, dossiê nem segredo, e nada vem da IA.
 */
data class MemorialCard(
    val heroName: String,
    val level: Int,
    val diedOnDay: Int,
    /** Fato público do legado ([LegacyMemory.fact]), já limitado em tamanho. */
    val line: String,
)

object Memorial {
    /** Cartões dos heróis mortos da campanha: o mais recente primeiro, no empate pelo nome. */
    fun cards(legacies: Collection<HeroLegacy>): List<MemorialCard> =
        legacies
            .sortedWith(compareByDescending<HeroLegacy> { it.diedOnDay }.thenBy { it.heroName })
            .map { MemorialCard(it.heroName, it.level, it.diedOnDay, LegacyMemory.fact(it)) }
}
