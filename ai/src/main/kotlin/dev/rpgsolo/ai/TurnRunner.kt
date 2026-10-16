package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Event
import dev.rpgsolo.engine.ExecResult

sealed interface TurnOutcome {
    /** Narrativa liberada e propostas aplicadas pelo motor (lista vazia = só narração). */
    data class Applied(
        val narrative: String,
        val events: List<Event>,
        val suggestions: List<String> = emptyList()
    ) : TurnOutcome

    /** A IA respondeu, mas o motor recusou as propostas mesmo após a correção. Nada foi alterado. */
    data class Refused(val reason: String) : TurnOutcome

    /** Falha de rede/chave/limite ou resposta fora do formato. Nada foi alterado. */
    data class Failed(val reason: String, val error: AiError? = null) : TurnOutcome
}

/**
 * Fases 6.2.5 e 6.3: pede a jogada à IA, valida o formato, passa as propostas pelo motor (lote atômico) e,
 * se algo falhar, devolve o erro à IA para UMA nova tentativa. Em qualquer falha final o estado fica como estava.
 */
class TurnRunner(
    private val provider: AiProvider,
    private val maxCorrections: Int = 1
) {
    init {
        require(maxCorrections >= 0) { "maxCorrections deve ser >= 0" }
    }

    private sealed interface Attempt {
        data class Done(val outcome: TurnOutcome.Applied) : Attempt
        data class Retry(val problem: String, val fromEngine: Boolean) : Attempt
    }

    fun run(session: CampaignSession, request: AiRequest): TurnOutcome {
        val mark = session.mark()
        var messages = request.messages
        var lastProblem = ""
        var lastFromEngine = false

        for (i in 0..maxCorrections) {
            val response = try {
                provider.complete(request.copy(messages = messages, jsonMode = true))
            } catch (e: AiError) {
                return TurnOutcome.Failed(e.message ?: "erro de IA", e)
            }
            when (val a = attempt(session, response.text)) {
                is Attempt.Done -> return a.outcome
                is Attempt.Retry -> {
                    lastProblem = a.problem
                    lastFromEngine = a.fromEngine
                    messages = messages +
                        AiMessage(Role.ASSISTANT, response.text) +
                        AiMessage(
                            Role.USER,
                            "Sua resposta foi recusada: ${a.problem}. " +
                                "Responda de novo apenas com o JSON no formato pedido, corrigindo o problema."
                        )
                }
            }
        }
        // Defesa extra: lote recusado não grava nada, mas garantimos o ponto de partida.
        if (session.engine.eventCount != mark) session.rollbackTo(mark)
        return if (lastFromEngine) TurnOutcome.Refused(lastProblem) else TurnOutcome.Failed(lastProblem)
    }

    private fun attempt(session: CampaignSession, text: String): Attempt =
        when (val parsed = ProposalParser.parse(text)) {
            is ParseResult.Invalid ->
                Attempt.Retry("formato inválido: " + parsed.errors.joinToString("; "), false)
            is ParseResult.Ok -> {
                val out = parsed.output
                if (out.proposals.isEmpty()) {
                    Attempt.Done(TurnOutcome.Applied(out.narrative, emptyList(), out.suggestions))
                } else {
                    when (val r = session.executeAll(out.proposals)) {
                        is ExecResult.Accepted -> Attempt.Done(TurnOutcome.Applied(out.narrative, r.events, out.suggestions))
                        is ExecResult.Rejected ->
                            Attempt.Retry("o motor recusou a proposta ${r.commandIndex + 1}: ${r.reason}", true)
                    }
                }
            }
        }
}
