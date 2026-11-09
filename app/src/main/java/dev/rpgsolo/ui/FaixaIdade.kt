package dev.rpgsolo.ui

/**
 * Faixa de idade do herói (MI-26), escolhida na criação da ficha: cada faixa vale uma porcentagem da longevidade do
 * povo (`vida_max` da classe de longevidade). Decidido pelo autor: jovem 20%, adulto 40%, veterano 70%.
 * 🔶 ASSUMINDO: faixa vazia na ficha = adulto; povo sem idade (`vida_max` 0) não recebe idade.
 */
enum class FaixaIdade(val id: String, val percentual: Int) {
    JOVEM("jovem", 20),
    ADULTO("adulto", 40),
    VETERANO("veterano", 70),
    ;

    fun anos(vidaMax: Int): Int = vidaMax * percentual / 100

    companion object {
        fun de(id: String): FaixaIdade = entries.firstOrNull { it.id == id } ?: ADULTO
    }
}
