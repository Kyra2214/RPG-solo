package dev.rpgsolo.ui

import kotlin.random.Random

/**
 * Aparência por chips e sorteio (CG-8.1, M8). Cada frase é um traço curto; o texto livre continua valendo e as frases entram nele.
 * ASSUMINDO: lista editorial própria e neutra (sem raça, sexo ou reino); alternar a frase a coloca ou tira do texto, separada por vírgula.
 */
internal object CriacaoAparencia {
    val categorias: List<Pair<String, List<String>>> =
        listOf(
            "Cabelo" to listOf("cabelo preto", "cabelo castanho", "cabelo ruivo", "cabelo loiro", "cabelo branco", "cabeça raspada", "tranças longas"),
            "Olhos" to listOf("olhos castanhos", "olhos verdes", "olhos azuis", "olhos cinzentos", "olhos dourados"),
            "Porte" to listOf("porte esguio", "porte robusto", "porte alto", "porte baixo", "postura ereta", "postura curvada"),
            "Marca" to listOf("cicatriz no rosto", "tatuagem no braço", "mãos calejadas", "sorriso fácil", "olhar desconfiado", "roupas gastas pela estrada"),
        )

    private const val SEPARADOR = ", "

    fun frases(texto: String): List<String> = texto.split(',').map { it.trim() }.filter { it.isNotEmpty() }

    fun contem(
        texto: String,
        frase: String,
    ): Boolean = frases(texto).any { it.equals(frase, ignoreCase = true) }

    /** Coloca a frase no texto se faltar; tira se já estiver. O restante do texto é preservado. */
    fun alternar(
        texto: String,
        frase: String,
    ): String {
        val atuais = frases(texto)
        val novas = if (contem(texto, frase)) atuais.filterNot { it.equals(frase, ignoreCase = true) } else atuais + frase
        return novas.joinToString(SEPARADOR)
    }

    /** Uma frase de cada categoria, em texto novo; não usa o que já foi escrito. */
    fun sortear(aleatorio: Random = Random.Default): String = categorias.joinToString(SEPARADOR) { it.second.random(aleatorio) }
}

/** Personalidade por escolha (PT-04): mesmas regras de alternar/sortear da aparência, sem texto livre. */
internal object CriacaoPersonalidade {
    val categorias: List<Pair<String, List<String>>> =
        listOf(
            "Temperamento" to listOf("calmo", "impulsivo", "curioso", "cauteloso", "ousado", "melancólico"),
            "Jeito" to listOf("bem-humorado", "sério", "leal", "desconfiado", "generoso", "orgulhoso"),
            "Motivação" to listOf("busca fama", "busca vingança", "busca conhecimento", "protege os fracos", "foge do passado", "busca riqueza"),
        )

    fun contem(
        texto: String,
        frase: String,
    ): Boolean = CriacaoAparencia.contem(texto, frase)

    fun alternar(
        texto: String,
        frase: String,
    ): String = CriacaoAparencia.alternar(texto, frase)

    fun sortear(aleatorio: Random = Random.Default): String = categorias.joinToString(", ") { it.second.random(aleatorio) }
}
