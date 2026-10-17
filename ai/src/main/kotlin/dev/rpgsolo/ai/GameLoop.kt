package dev.rpgsolo.ai

import dev.rpgsolo.engine.AddCondition
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.ExecResult

data class PlayInput(
    val playerEntityId: String,
    val text: String,
    /** Teste escolhido pelo motor/pacote para essa ação (7.1.3). null = ação sem teste. */
    val check: CheckSpec? = null,
    val healthResource: String? = null,
    val ruleSnippets: List<String> = emptyList(),
    val chapterSummary: String? = null,
    val npcPrivate: List<String> = emptyList(),
    /** Ficha do jogador em texto, montada a cada jogada com o estado atual (ver [PlayerSheet]). */
    val playerSheet: String? = null,
    /** Quanto da ficha foi enviado nesta jogada. Serve para o controlador saber se o combate já recebeu a ficha completa. */
    val sheetLevel: SheetLevel? = null,
    /** Preenchido só na jogada que resolve um teste ou rolagem pedidos pelo mestre. */
    val resolving: Pending? = null,
    val budgetTokens: Int = 2000
)

/** Marcas do log permitem desfazer a jogada inteira ou só refazer a narração mantendo os dados (7.4). */
data class TurnRecord(
    val input: PlayInput,
    val markBefore: Int,
    val markAfterRoll: Int,
    val check: CheckResult?,
    val narrative: String,
    val suggestions: List<String> = emptyList(),
    /** Teste ou rolagem que o mestre pediu nesta jogada e que o jogador ainda não rolou. */
    val pending: Pending? = null
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
    private val premiumRunner: TurnRunner? = null,
    /** Testes que o mestre pode pedir (do pacote ativo). null = o mestre não pode pedir teste. */
    private val menu: CheckMenu? = null,
    /** Ids dos traços do personagem do jogador (vantagens e perícias que dão bônus nos testes). */
    private val playerTraits: () -> List<String> = { emptyList() }
) {
    private val history = mutableListOf<TurnRecord>()
    private val narrations = mutableListOf<String>()

    val turns: List<TurnRecord> get() = history.toList()

    fun play(session: CampaignSession, input: PlayInput): PlayResult {
        val markBefore = session.mark()
        val check = input.check?.let { session.check(adjusted(it, session, input.playerEntityId)) }
        val markAfterRoll = session.mark()
        return narrate(session, input, check, markBefore, markAfterRoll, chainDepth(history))
            .also { if (it is PlayResult.Played) history += it.record }
    }

    private fun adjusted(spec: CheckSpec, session: CampaignSession, playerId: String, skill: String? = null): CheckSpec =
        menu?.adjust(spec, session.state.conditions[playerId].orEmpty(), playerTraits(), skill) ?: spec

    /** Quantas resoluções de pedido do mestre vieram em sequência no fim do histórico (limita o encadeamento). */
    private fun chainDepth(records: List<TurnRecord>): Int = records.takeLastWhile { it.input.resolving != null }.size

    /** Teste ou rolagem pedidos pelo mestre que esperam o jogador. Derivado do histórico: desfazer a jogada o apaga sozinho. */
    fun pending(): Pending? = history.lastOrNull()?.pending

    /**
     * O jogador rola o que o mestre pediu: o motor rola e grava. Numa rolagem com tabela, o motor também aplica o dano da
     * face sorteada (a IA não decide o número). Depois o mestre narra o desfecho com a nota privada do resultado.
     * [template] traz jogador, ficha e recurso de saúde. Falha desfaz tudo, inclusive a rolagem.
     */
    fun resolvePending(session: CampaignSession, template: PlayInput): PlayResult {
        val pending = pending() ?: return PlayResult.Failed("não há teste pendente")
        val input = template.copy(text = pending.playerText, check = null, resolving = pending)
        val markBefore = session.mark()
        // Teste do mestre leva Ganho/Perda das condições; rolagem com tabela (d6 de dano) nunca.
        val check = session.check(if (pending is PendingCheck) adjusted(pending.spec, session, template.playerEntityId, pending.request.skill) else pending.spec)
        if (pending is PendingRoll) {
            val face = check.roll.dice.single()
            val entry = pending.request.table[face]
            val damage = entry?.damage ?: 0
            val resource = menu?.healthResource()
            val alive = session.state.entities[template.playerEntityId]?.alive == true
            if (damage > 0 && resource != null && alive) {
                val r = session.execute(Damage(template.playerEntityId, resource, damage))
                if (r is ExecResult.Rejected) {
                    session.rollbackTo(markBefore)
                    return PlayResult.Failed("o motor recusou o dano da rolagem: ${r.reason}")
                }
            }
            // Status da face (ex.: sangramento): aplicados pelo motor, só em quem ainda está vivo e ainda não os tem.
            for (condition in entry?.conditions.orEmpty()) {
                val player = session.state.entities[template.playerEntityId] ?: break
                if (!player.alive) break
                if (session.state.conditions[player.id]?.contains(condition) == true) continue
                val r = session.execute(AddCondition(player.id, condition))
                if (r is ExecResult.Rejected) {
                    session.rollbackTo(markBefore)
                    return PlayResult.Failed("o motor recusou a condição da rolagem: ${r.reason}")
                }
            }
        }
        val markAfterRoll = session.mark()
        return narrate(session, input, check, markBefore, markAfterRoll, chainDepth(history))
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
        return when (val r = narrate(session, last.input, last.check, last.markBefore, last.markAfterRoll, chainDepth(history.dropLast(1)))) {
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
        session: CampaignSession, input: PlayInput, check: CheckResult?, markBefore: Int, markAfterRoll: Int, depth: Int
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
                playerSheet = input.playerSheet,
                checkMenu = menu?.describe(),
                resolving = input.resolving,
                recentNarrative = narrations.toList(),
                budgetTokens = input.budgetTokens
            )
        )
        val chosen = if (premiumRunner != null && Importance.of(check) == NarrationTier.PREMIUM) premiumRunner else runner
        // No máximo duas resoluções seguidas pedindo coisa nova: evita o mestre prender o jogador em pedidos.
        return when (val o = chosen.run(session, request, menu, canRequest = depth < 2)) {
            is TurnOutcome.Applied -> {
                narrations += o.narrative
                val stats = session.state.entities[input.playerEntityId]?.stats.orEmpty()
                val skills = session.state.entities[input.playerEntityId]?.skills.orEmpty()
                val baseText = input.resolving?.playerText ?: input.text
                val checkReq = o.checkRequest
                val rollReq = o.rollRequest
                val pending: Pending? = when {
                    checkReq != null -> menu?.build(checkReq, stats, skills)?.let { PendingCheck(checkReq, it, baseText) }
                    rollReq != null -> menu?.let { PendingRoll(rollReq, it.buildRoll(rollReq), baseText) }
                    else -> null
                }
                PlayResult.Played(TurnRecord(input, markBefore, markAfterRoll, check, o.narrative, o.suggestions, pending))
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
