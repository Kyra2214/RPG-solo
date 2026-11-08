package dev.rpgsolo.world

/** Data local de um dia de mundo (porta de `CalendarioReino.data` de `calendario.py`). */
data class DataLocal(val ano: Int, val diaDoAno: Int, val estacao: String, val lua: Int)

/** Calendário local de um reino (porta de `CalendarioReino` de `calendario.py`). */
class CalendarioReino(linha: Linha) {
    val id: String = linha.getValue("id")
    private val meses = linha.getValue("meses").trim().toInt()
    private val diasMes = linha.getValue("dias_por_mes").trim().toInt()
    private val transicao = linha.getValue("dias_de_transicao").trim().toInt()
    private val estacoes: List<String> = linha.getValue("estacoes").split(';').map { it.trim() }
    private val anoInicial: Int? = anoLocal(linha.getValue("ano_inicial"))
    private val estacaoInicial = linha.getValue("estacao_inicial")

    fun valido(): Boolean =
        meses * diasMes + transicao == DIAS_ANO && estacoes.size == 4 && estacaoInicial == estacoes[0] && anoInicial != null

    fun data(diaMundo: Int): DataLocal {
        val doy = diaDoAno(diaMundo)
        return DataLocal(
            ano = requireNotNull(anoInicial) { "ano inicial inválido" } + Math.floorDiv(diaMundo, DIAS_ANO),
            diaDoAno = doy,
            estacao = estacoes[estacaoIdx(doy)],
            lua = lua(doy),
        )
    }

    companion object {
        /** Ano de Aeltharyn do dia de mundo 0 (M-02 §1, D-14). */
        const val ANO_BASE = 742
        const val DIAS_ANO = 365

        /** Dia do ano de 1 a 365 (o `%` do Python é módulo de piso). */
        fun diaDoAno(diaMundo: Int): Int = Math.floorMod(diaMundo, DIAS_ANO) + 1

        fun anoBase(diaMundo: Int): Int = ANO_BASE + Math.floorDiv(diaMundo, DIAS_ANO)

        /** 0..3 (inverno, primavera, verão, outono); os dias 364 e 365 ficam no outono (M-02 §3). */
        fun estacaoIdx(doy: Int): Int =
            when {
                doy <= 91 -> 0
                doy <= 182 -> 1
                doy <= 273 -> 2
                else -> 3
            }

        /** Lua 1..12, ou 0 para dia de transição. */
        fun lua(doy: Int): Int = if (doy in setOf(91, 182, 273, 364, 365)) 0 else ((doy - 1) / 91) * 3 + ((doy - 1) % 91) / 30 + 1

        /** Calendário de cada reino do molde (porta de `construir`); `null` se a linha do calendário não existe. */
        fun construir(w: MundoMolde): Map<String, CalendarioReino?> {
            val cals = w.t.getValue("calendario").associate { it.getValue("id") to CalendarioReino(it) }
            return w.reinos.mapValues { (_, r) -> cals[r.linha.getValue("calendario")] }
        }

        private val ANO_LOCAL = Regex("^\\s*(\\d+)")

        fun anoLocal(texto: String): Int? = ANO_LOCAL.find(texto)?.groupValues?.get(1)?.toIntOrNull()
    }
}
