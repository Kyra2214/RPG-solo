package dev.rpgsolo.ui

import dev.rpgsolo.engine.AdvanceDays
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.RecordFact
import dev.rpgsolo.world.FeedSimulacao

/** O que "Aplicar à campanha" gravou: [marcaAntes] é a marca do log da campanha antes do lote (para desfazer). */
class LoteAplicado(val marcaAntes: Int, val dias: Int)

sealed interface ResultadoAplicacao {
    data class Aplicado(val lote: LoteAplicado) : ResultadoAplicacao

    data class Recusado(val motivo: String) : ResultadoAplicacao
}

/**
 * Aplica o período simulado à campanha (§4, MI-17): avança o dia da campanha e grava os efeitos como fatos, tudo num
 * lote só (`executeAll` recusa o lote inteiro se um comando falhar). Desfazer volta o log da campanha à marca de antes.
 * 🔶 ASSUMINDO: nesta etapa os efeitos são fatos (`mundo.dia`, `mundo.ultima_simulacao`, `mundo.marcos`); cidades, NPCs e
 * facções só entram na campanha sob demanda (MI-18), e o envelhecimento e a morte do herói ficam no MI-26 e MI-27.
 */
object AplicacaoSimulacao {
    const val CHAVE_DIA = "mundo.dia"
    const val CHAVE_ULTIMA = "mundo.ultima_simulacao"
    const val CHAVE_MARCOS = "mundo.marcos"
    const val MARCOS_GRAVADOS = 10

    fun comandos(
        diaInicio: Int,
        diaFim: Int,
        feed: FeedSimulacao,
    ): List<Command> {
        val dias = diaFim - diaInicio
        require(dias > 0) { "período vazio" }
        val avancos = ArrayList<Command>()
        var resta = dias
        while (resta > 0) {
            val n = minOf(resta, MAX_AVANCO)
            avancos += AdvanceDays(n)
            resta -= n
        }
        return avancos + listOf(RecordFact(CHAVE_DIA, diaFim.toString()), RecordFact(CHAVE_ULTIMA, resumo(diaInicio, diaFim, feed)), RecordFact(CHAVE_MARCOS, marcos(feed)))
    }

    fun resumo(
        diaInicio: Int,
        diaFim: Int,
        feed: FeedSimulacao,
    ): String {
        val r = feed.resumo
        val caidos = if (r.reinosCaidos.isEmpty()) "nenhum" else r.reinosCaidos.joinToString(", ")
        return "${Periodos.data(diaInicio)} até ${Periodos.data(diaFim)} (${diaFim - diaInicio} dias): marcos ${r.marcos}; reinos caídos: $caidos; " +
            "pactos novos ${r.pactosNovos}; NPCs principais mortos ${r.npcsMortos}; empossados ${r.npcsEmpossados}"
    }

    /** Os [MARCOS_GRAVADOS] marcos mais recentes, um por linha ("ano N: texto"). */
    fun marcos(feed: FeedSimulacao): String = feed.marcos.takeLast(MARCOS_GRAVADOS).joinToString("\n") { "ano ${it.ano}: ${it.texto}" }

    fun aplicar(
        session: CampaignSession,
        diaInicio: Int,
        diaFim: Int,
        feed: FeedSimulacao,
        estadoMundo: Map<String, Any?>? = null,
        heroi: dev.rpgsolo.world.ResultadoHeroi? = null,
    ): ResultadoAplicacao =
        session.atomically {
            val marca = session.mark()
            val lote = comandos(diaInicio, diaFim, feed) + VidaForaDeCena.comandos(session.state, estadoMundo) +
                    (heroi?.let { IdadeHeroi.comandos(session.state, it) } ?: emptyList())
            when (val r = session.executeAll(lote)) {
                is ExecResult.Accepted -> ResultadoAplicacao.Aplicado(LoteAplicado(marca, diaFim - diaInicio))
                is ExecResult.Rejected -> ResultadoAplicacao.Recusado(r.reason)
            }
        }

    /** Desfaz o lote (dia da campanha e fatos). Quem chama também devolve o mundo ao bloco de partida (`descartar`). */
    fun desfazer(
        session: CampaignSession,
        lote: LoteAplicado,
    ) {
        session.rollbackTo(lote.marcaAntes)
    }

    private const val MAX_AVANCO = 365
}
