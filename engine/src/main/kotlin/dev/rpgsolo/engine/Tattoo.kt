package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** N-36: marca de Tatuador. [scope] é o status reforçado e [grade] o grau (1 a 5) de quem tatuou. */
@Serializable
data class Tattoo(val scope: String, val grade: Int)

/** N-36: regras das marcas do Tatuador (limite, bônus, preço e cobertura). */
object Tattoos {
    /** Status que uma marca pode reforçar. */
    val SCOPES: List<String> = listOf("ataque", "defesa", "velocidade", "max_pv")
    const val MAX_GRADE = 5
    const val PRICE_PER_GRADE = 100
    const val PV_PER_GRADE = 5
    const val SKILL_PREFIX = "profissao-tatuador-"

    /** Maior grau (1 a 5) da profissão Tatuador nas habilidades da entidade; 0 sem a profissão. */
    fun grade(skillLevels: Map<String, Int>): Int = (1..MAX_GRADE).lastOrNull { (skillLevels["$SKILL_PREFIX$it"] ?: 0) > 0 } ?: 0

    /** 1 marca por grau do Tatuador do herói. ASSUMINDO: quem não tem a profissão ainda pode receber 1 marca. */
    fun limit(skillLevels: Map<String, Int>): Int = maxOf(1, grade(skillLevels))

    /** +1 por grau (PV +5 por grau). */
    fun bonusOf(tattoo: Tattoo): Int = tattoo.grade * (if (tattoo.scope == "max_pv") PV_PER_GRADE else 1)

    fun bonus(
        tattoos: List<Tattoo>?,
        scope: String,
    ): Int = tattoos.orEmpty().filter { it.scope == scope }.sumOf { bonusOf(it) }

    /** 100 prata × grau de quem tatua. */
    fun price(grade: Int): Int = PRICE_PER_GRADE * grade
}
