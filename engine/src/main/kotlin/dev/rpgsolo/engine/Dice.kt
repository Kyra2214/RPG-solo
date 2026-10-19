package dev.rpgsolo.engine

import kotlin.random.Random

/** Resultado de uma rolagem, com cada dado para o log de eventos. */
data class RollResult(val expression: String, val dice: List<Int>, val modifier: Int) {
    val total: Int get() = dice.sum() + modifier
    /** Soma dos dados sem modificador (usada para críticos). */
    val natural: Int get() = dice.sum()
}

/**
 * Rolagem determinística e "rebobinável": a rolagem número N depende só de (seed, N).
 * Assim, desfazer uma jogada e jogar de novo gera os mesmos dados (sem "rolar de novo até dar certo").
 */
class Dice(val seed: Long, startAt: Long = 0) {
    /** Quantas rolagens já foram feitas. */
    var position: Long = startAt
        private set

    fun seek(newPosition: Long) {
        require(newPosition >= 0) { "posição inválida" }
        position = newPosition
    }

    fun roll(count: Int, sides: Int, modifier: Int = 0, explodeOnMax: Boolean = false): RollResult {
        require(count in 1..100) { "count fora do limite" }
        require(sides >= 2) { "sides inválido" }
        val random = Random(seed xor (position * -7046029254386353131L))
        position++
        val rolled = mutableListOf<Int>()
        repeat(count) {
            var face = random.nextInt(1, sides + 1)
            rolled += face
            // Limite por dado evita explosão infinita em dados pequenos (ex.: d2).
            var explosions = 0
            while (explodeOnMax && face == sides && explosions < 100) {
                face = random.nextInt(1, sides + 1)
                rolled += face
                explosions++
            }
        }
        val mod = if (modifier == 0) "" else if (modifier > 0) "+$modifier" else "$modifier"
        val expression = "${count}d$sides${if (explodeOnMax) "!" else ""}$mod"
        return RollResult(expression, rolled, modifier)
    }
}
