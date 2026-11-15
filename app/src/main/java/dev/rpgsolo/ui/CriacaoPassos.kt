package dev.rpgsolo.ui

/** Estado de conclusão dos sete passos da criação por abas; Kotlin puro, sem Compose. */
internal data class ProgressoCriacao(
    val racaCompleta: Boolean,
    val classeEscolhida: Boolean,
    val profissaoEscolhida: Boolean,
    val statusSemSobra: Boolean,
    val vantagensValidasComTracos: Boolean,
    val informacoesOk: Boolean,
)

internal object CriacaoPassos {
    fun concluidos(p: ProgressoCriacao): Set<PassoCriacao> =
        buildSet {
            if (p.racaCompleta) add(PassoCriacao.RACA)
            if (p.classeEscolhida) add(PassoCriacao.CLASSE)
            if (p.profissaoEscolhida) add(PassoCriacao.PROFISSAO)
            if (p.statusSemSobra) add(PassoCriacao.STATUS)
            if (p.classeEscolhida && p.racaCompleta) add(PassoCriacao.HABILIDADES)
            if (p.vantagensValidasComTracos) add(PassoCriacao.VANTAGENS)
            if (p.informacoesOk) add(PassoCriacao.INFORMACOES)
        }

    fun proximo(passo: PassoCriacao): PassoCriacao = PassoCriacao.entries.getOrElse(passo.ordinal + 1) { passo }

    fun anterior(passo: PassoCriacao): PassoCriacao = PassoCriacao.entries.getOrElse(passo.ordinal - 1) { passo }

    /** Primeiro passo ainda não concluído, ou o último se todos estiverem prontos. */
    fun primeiroPendente(feitos: Set<PassoCriacao>): PassoCriacao =
        PassoCriacao.entries.firstOrNull { it !in feitos } ?: PassoCriacao.INFORMACOES
}

/**
 * Atributos que continuam existindo na ficha (o motor e o pacote seguem usando), mas não são distribuídos
 * nas telas de criação: "Habilidade" evolui só pelos pontos de habilidade e de perícia, nunca pelos pontos de status.
 */
internal object AtributosDaCriacao {
    private val ocultos: Set<String> = setOf("habilidade")

    fun ehOculto(id: String): Boolean = id in ocultos
}
