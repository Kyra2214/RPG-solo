package dev.rpgsolo.engine

/**
 * N-16: ciclo elemental. Água vence fogo, fogo vence ferro, ferro vence vento, vento vence terra, terra vence água.
 * ASSUMINDO: [ElementPath.METAL] é o ferro, [ElementPath.YANG] é a Luz e [ElementPath.YIN] são as Trevas (nomes do enum preservados).
 */
object ElementChart {
    val CYCLE: List<ElementPath> = listOf(ElementPath.WATER, ElementPath.FIRE, ElementPath.METAL, ElementPath.WIND, ElementPath.EARTH)
    private val SPECIAL = setOf(ElementPath.YIN, ElementPath.YANG)

    fun isSpecial(e: ElementPath): Boolean = e in SPECIAL

    /** Elemento que [e] vence no ciclo (nulo para Luz, Trevas e sem elemento). */
    fun beats(e: ElementPath): ElementPath? = CYCLE.indexOf(e).takeIf { it >= 0 }?.let { CYCLE[(it + 1) % CYCLE.size] }

    /** Elemento que vence [e] no ciclo. */
    fun beatenBy(e: ElementPath): ElementPath? = CYCLE.indexOf(e).takeIf { it >= 0 }?.let { CYCLE[(it + CYCLE.size - 1) % CYCLE.size] }

    /**
     * Percentual do dano (100 = normal, 0 = nenhum) de um ataque com [attack] (um elemento, ou dois para magia combinada) contra [defender].
     * Sem elemento no ataque ou no alvo: 100.
     * Ataque puro: o elemento que vence o seu zera o dano; vantagem +50%; Luz e Trevas dão e recebem +50% dos demais e dobram entre si.
     * Magia combinada: +50% contra tudo, exceto contra o elemento que vence algum dos dois, onde fica em 50% (a desvantagem prevalece sobre a vantagem).
     * O bônus de combinação soma com o de vantagem (+50% +50% = 200%).
     */
    fun percent(
        attack: Set<ElementPath>,
        defender: ElementPath,
    ): Int {
        val atk = attack.filter { it != ElementPath.NONE }.toSet()
        require(atk.size <= 2) { "no máximo dois elementos por ataque" }
        if (atk.isEmpty() || defender == ElementPath.NONE) return 100
        if (atk.size == 2) {
            require(atk.none(::isSpecial)) { "magia combinada exige dois elementos do ciclo" }
            if (atk.any { beatenBy(it) == defender }) return 50
            return 150 + if (atk.any { beats(it) == defender }) 50 else 0
        }
        val a = atk.single()
        if (isSpecial(a)) {
            return when {
                isSpecial(defender) -> if (a == defender) 100 else 200
                else -> 150
            }
        }
        if (isSpecial(defender)) return 150
        return when {
            beatenBy(a) == defender -> 0
            beats(a) == defender -> 150
            else -> 100
        }
    }

    /** N-17: percentual contra um peito encantado ([armor]); Luz e Trevas ignoram o outro do par, o resto segue [percent]. */
    fun armorPercent(
        attack: Set<ElementPath>,
        armor: ElementPath,
    ): Int {
        val atk = attack.filter { it != ElementPath.NONE }
        if (isSpecial(armor) && atk.size == 1 && isSpecial(atk.single()) && atk.single() != armor) return 0
        return percent(attack, armor)
    }

    /** Dano com o percentual do elemento aplicado, arredondado para cima. */
    fun apply(
        damage: Int,
        percent: Int,
    ): Int = if (percent == 100) damage else maxOf(0, (damage * percent + 99) / 100)

    /** Aceita ids em português ou o nome do enum (`ferro`, `luz`, `trevas`, `agua`, `metal`...); vazio ou `-` = sem elemento. */
    fun parse(raw: String): ElementPath {
        val key = raw.trim().lowercase().replace("á", "a").replace("é", "e")
        if (key.isEmpty() || key == "-") return ElementPath.NONE
        return when (key) {
            "agua", "water" -> ElementPath.WATER
            "fogo", "fire" -> ElementPath.FIRE
            "ferro", "metal" -> ElementPath.METAL
            "vento", "wind" -> ElementPath.WIND
            "terra", "earth" -> ElementPath.EARTH
            "luz", "yang" -> ElementPath.YANG
            "trevas", "yin" -> ElementPath.YIN
            "none", "nenhum" -> ElementPath.NONE
            else -> throw IllegalArgumentException("elemento inválido: '$raw'")
        }
    }
}

/**
 * N-16: chance de aplicar condição (d100). Base declarada na arma ou habilidade + 2% por ponto de Sorte, com teto de 90% pela soma
 * de base e atributos; itens, buffs e pergaminhos ([extra]) somam depois do teto, até 100%.
 */
object ConditionChance {
    const val PER_LUCK = 2
    const val ATTRIBUTE_CAP = 90

    fun chance(
        base: Int,
        luck: Int,
        extra: Int = 0,
    ): Int {
        require(base in 0..100) { "chance base fora de 0..100" }
        return (minOf(ATTRIBUTE_CAP, base + PER_LUCK * luck.coerceAtLeast(0)) + extra.coerceAtLeast(0)).coerceIn(0, 100)
    }
}
