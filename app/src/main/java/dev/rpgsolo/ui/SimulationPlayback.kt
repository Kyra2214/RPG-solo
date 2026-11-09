package dev.rpgsolo.ui

import dev.rpgsolo.world.CalendarioReino

/** Velocidade de exibição (D-I7): 1x = 1 ano de mundo por segundo. Nunca entra no cálculo (I-02). */
enum class VelocidadeSimulacao(val rotulo: String, val fator: Int) {
    X1("1x", 1),
    X5("5x", 5),
    X10("10x", 10),
}

/** O que a tela mostra num instante: [dia] é o dia de mundo exibido (nunca passa do dia já calculado). */
data class QuadroExibicao(
    val dia: Int,
    val esperandoCalculo: Boolean,
    val aparelhoLento: Boolean,
    val terminou: Boolean,
)

/**
 * Exibição desacoplada do cálculo (MI-14). O [TimeSimulationController] calcula em blocos e não conhece a velocidade, então
 * a mesma semente e o mesmo período dão o mesmo resultado em 1x, 5x e 10x; esta classe só decide qual dia mostrar.
 * Sem relógio nem thread próprios: a tela chama [atualizar] a cada quadro com o instante e o dia já calculado
 * (`ProgressoSimulacao.diaAtual`). Se o cálculo não acompanha, a exibição espera nele; esperar mais de [LENTO_MS] liga
 * [QuadroExibicao.aparelhoLento].
 * 🔶 ASSUMINDO: "período curto" = até 91 dias (uma estação, 365 / 4): nesse caso nada é animado e o resultado aparece
 * de uma vez ao concluir; aviso de aparelho lento após 2 s esperando o cálculo.
 */
class ExibicaoSimulacao(
    private val diaInicio: Int,
    private val diaFim: Int,
    velocidade: VelocidadeSimulacao = VelocidadeSimulacao.X1,
) {
    var velocidade: VelocidadeSimulacao = velocidade
        private set

    private var diaExato = diaInicio.toDouble()
    private var ultimoMs = -1L
    private var pausado = false
    private var esperaDesdeMs = -1L
    private val imediato = diaFim - diaInicio <= DIAS_ESTACAO

    /** Troca a velocidade a partir do instante [agora], sem salto: o trecho anterior conta na velocidade antiga. */
    fun definirVelocidade(
        nova: VelocidadeSimulacao,
        agora: Long,
    ) {
        integrar(agora)
        velocidade = nova
    }

    fun pausar(agora: Long) {
        integrar(agora)
        pausado = true
    }

    fun retomar(agora: Long) {
        ultimoMs = agora
        pausado = false
    }

    fun atualizar(
        agora: Long,
        calculadoAte: Int,
        concluido: Boolean,
    ): QuadroExibicao {
        integrar(agora)
        val limite = minOf(calculadoAte, diaFim).toDouble()
        if (imediato) {
            if (concluido) diaExato = diaFim.toDouble()
            return QuadroExibicao(diaExato.toInt(), esperandoCalculo = false, aparelhoLento = false, terminou = concluido)
        }
        val esperando = !concluido && !pausado && diaExato >= limite
        if (diaExato > limite) diaExato = limite
        if (esperando) {
            if (esperaDesdeMs < 0) esperaDesdeMs = agora
        } else {
            esperaDesdeMs = -1L
        }
        val lento = esperando && agora - esperaDesdeMs >= LENTO_MS
        val dia = diaExato.toInt()
        return QuadroExibicao(dia, esperando, lento, terminou = concluido && dia >= diaFim)
    }

    private fun integrar(agora: Long) {
        if (ultimoMs >= 0 && !pausado && !imediato) {
            val dias = (agora - ultimoMs).coerceAtLeast(0) / 1000.0 * velocidade.fator * CalendarioReino.DIAS_ANO
            diaExato = minOf(diaExato + dias, diaFim.toDouble())
        }
        ultimoMs = agora
    }

    companion object {
        const val DIAS_ESTACAO = CalendarioReino.DIAS_ANO / 4
        const val LENTO_MS = 2000L
    }
}
