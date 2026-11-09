package dev.rpgsolo.ui

import dev.rpgsolo.world.CalendarioReino

/**
 * Períodos de um toque da tela Simulação de tempo (§4).
 * 🔶 ASSUMINDO: o molde não declara o fim da era; "1 era" usa o teto da simulação, 120 anos (o mesmo do campo livre).
 */
enum class PeriodoSimulacao(val rotulo: String, val dias: Int) {
    UM_DIA("1 dia", 1),
    UMA_SEMANA("1 semana", 7),
    UMA_LUA("1 lua (30 dias)", 30),
    UMA_ESTACAO("1 estação", CalendarioReino.DIAS_ANO / 4),
    UM_ANO("1 ano", CalendarioReino.DIAS_ANO),
    DEZ_ANOS("10 anos", 10 * CalendarioReino.DIAS_ANO),
    UM_SECULO("1 século", 100 * CalendarioReino.DIAS_ANO),
    UMA_ERA("1 era", Periodos.MAXIMO_DIAS),
}

object Periodos {
    const val MAXIMO_ANOS = 120
    const val MAXIMO_DIAS = MAXIMO_ANOS * CalendarioReino.DIAS_ANO

    private val LIVRE = Regex("^\\s*(\\d{1,6})\\s*(dias?|d|anos?|a)?\\s*$", RegexOption.IGNORE_CASE)

    sealed interface Livre {
        data class Valido(val dias: Int) : Livre

        data class Invalido(val motivo: String) : Livre
    }

    /** Campo livre "N dias" ou "N anos" (sem unidade = dias): de 1 dia até [MAXIMO_ANOS] anos. */
    fun livre(texto: String): Livre {
        val m = LIVRE.matchEntire(texto) ?: return Livre.Invalido("Use, por exemplo, \"45 dias\" ou \"20 anos\".")
        val n = m.groupValues[1].toLong()
        val anos = m.groupValues[2].lowercase().startsWith("a")
        val dias = if (anos) n * CalendarioReino.DIAS_ANO else n
        return when {
            dias < 1 -> Livre.Invalido("O período precisa ter ao menos 1 dia.")
            dias > MAXIMO_DIAS -> Livre.Invalido("O máximo é $MAXIMO_ANOS anos.")
            else -> Livre.Valido(dias.toInt())
        }
    }

    private val ESTACOES = listOf("inverno", "primavera", "verão", "outono")

    /** Data de um dia de mundo na contagem base: ano, estação e lua (0 = dia de transição). */
    fun data(dia: Int): String {
        val doy = CalendarioReino.diaDoAno(dia)
        val lua = CalendarioReino.lua(doy)
        val estacao = ESTACOES[CalendarioReino.estacaoIdx(doy)]
        return "ano ${CalendarioReino.anoBase(dia)}, $estacao, " + if (lua == 0) "dia de transição" else "lua $lua"
    }

    /** "Partida → chegada prevista" para o texto do seletor. */
    fun resumo(
        diaPartida: Int,
        dias: Int,
    ): String = "De ${data(diaPartida)} até ${data(diaPartida + dias)}."

    /** Rótulo falado do progresso (TalkBack): inteiro de 0 a 100. */
    fun porcentagem(
        progresso: ProgressoSimulacao,
    ): Int {
        val total = progresso.diaFim - progresso.diaInicio
        return if (total <= 0) 0 else ((progresso.diaAtual - progresso.diaInicio) * 100L / total).toInt().coerceIn(0, 100)
    }
}
