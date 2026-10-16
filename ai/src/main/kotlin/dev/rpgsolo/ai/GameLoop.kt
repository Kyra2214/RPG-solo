package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.CheckSpec

data class PlayInput(
    val playerEntityId: String,
    val text: String,
    /** Teste escolhido pelo motor/pacote para essa ação (7.1.3). null = ação sem teste. */
    val check: CheckSpec? = null,
    val healthResource: String? = null,
    val ruleSnippets: List<String> = emptyList(),
    val chapterSummary: String? = null,
    val npcPrivate: List<String> = emptyList(),
    val budgetTokens: Int = 2000
)

/** Marcas do log permitem desfazer a jogada inteira ou só refazer a narração mantendo os dados (7.4). */
data class TurnRecord(
    val input: PlayInput,
    val markBefore: Int,
    val markAfterRoll: Int,
    val check: CheckResult?,
    val narrative: String,
    val suggestions: List<String> = emptyList()
)

sealed interface PlayResult {
    data class Played(val record: TurnRecord) : PlayResult
    /** Nada ficou gravado, nem a rolagem (a mesma jogada, ao repetir, rola os mesmos dados). */
    data class Failed(val reason: String, val error: AiError? = null) : PlayResult
}

/**
 * Ciclo da jogada (7.1): motor rola → monta contexto → IA narra e propõe → motor valida e grava.
 * Falha em qualquer ponto desfaz a jogada inteira.
 */
class GameLoop(
    provider: AiProvider,
    private val runner: TurnRunner = TurnRunner(provider),
    /** 6.4.2: narrador melhor, usado só quando o motor marca a jogada como importante. null = sempre o padrão. */
    private val premiumRunner: TurnRunner? = null
) {
    private val history = mutableListOf<TurnRecord>()
    private val narrations = mutableListOf<String>()

    val turns: List<TurnRecord> get() = history.toList()

    fun play(session: CampaignSession, input: PlayInput): PlayResult {
        val markBefore = session.mark()
        val check = input.check?.let { session.check(it) }
        val markAfterRoll = session.mark()
        return narrate(session, input, check, markBefore, markAfterRoll)
            .also { if (it is PlayResult.Played) history += it.record }
    }

    /** 7.4.1: reverte a última jogada (eventos e rolagens). false se não há o que desfazer. */
    fun undo(session: CampaignSession): Boolean {
        val last = history.removeLastOrNull() ?: return false
        session.rollbackTo(last.markBefore)
        narrations.removeLastOrNull()
        return true
    }

    /** 7.4.2: nova narração do MESMO resultado. Mantém os dados rolados; refaz as propostas da IA. */
    fun regenerate(session: CampaignSession): PlayResult {
        val last = history.lastOrNull() ?: return PlayResult.Failed("não há jogada para refazer")
        session.rollbackTo(last.markAfterRoll)
        narrations.removeLastOrNull()
        return when (val r = narrate(session, last.input, last.check, last.markBefore, last.markAfterRoll)) {
            is PlayResult.Played -> {
                history[history.lastIndex] = r.record
                r
            }
            is PlayResult.Failed -> {
                // Não conseguiu refazer: volta ao estado da jogada original não é possível sem reaplicar,
                // então desfaz a jogada por inteiro para não deixar estado pela metade.
                history.removeLast()
                session.rollbackTo(last.markBefore)
                r
            }
        }
    }

    private fun narrate(
        session: CampaignSession, input: PlayInput, check: CheckResult?, markBefore: Int, markAfterRoll: Int
    ): PlayResult {
        val request = ContextAssembler.build(
            ContextInput(
                state = session.state,
                playerEntityId = input.playerEntityId,
                playerText = input.text,
                check = check,
                healthResource = input.healthResource,
                ruleSnippets = input.ruleSnippets,
                chapterSummary = input.chapterSummary,
                npcPrivate = input.npcPrivate,
                recentNarrative = narrations.toList(),
                budgetTokens = input.budgetTokens
            )
        )
        val chosen = if (premiumRunner != null && Importance.of(check) == NarrationTier.PREMIUM) premiumRunner else runner
        return when (val o = chosen.run(session, request)) {
            is TurnOutcome.Applied -> {
                narrations += o.narrative
                PlayResult.Played(TurnRecord(input, markBefore, markAfterRoll, check, o.narrative, o.suggestions))
            }
            is TurnOutcome.Refused -> fail(session, markBefore, o.reason, null)
            is TurnOutcome.Failed -> fail(session, markBefore, o.reason, o.error)
        }
    }

    private fun fail(session: CampaignSession, markBefore: Int, reason: String, error: AiError?): PlayResult {
        if (session.mark() != markBefore) session.rollbackTo(markBefore)
        return PlayResult.Failed(reason, error)
    }
}
