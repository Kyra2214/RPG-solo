package dev.rpgsolo.ai

import kotlin.math.roundToInt

import dev.rpgsolo.engine.AddCondition
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.Reveal
import dev.rpgsolo.engine.checkWithFortune
import dev.rpgsolo.engine.checkWithFortuneSpendingResource
import dev.rpgsolo.engine.checkOpposed
import dev.rpgsolo.engine.checkGroup

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
    val budgetTokens: Int = 2000,
    /** IDs dos NPCs presentes nesta cena; evita enviar todos os NPCs da campanha. */
    val sceneNpcIds: Set<String> = emptySet(),
    val worldContext: String? = null,
    val generationPreferences: String? = null
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
    val pending: Pending? = null,
    /** Rolagens de vantagem/desvantagem não escolhidas; o resultado efetivo continua em [check]. */
    val fortuneAlternatives: List<CheckResult> = emptyList(),
    /** Resultados de todos os participantes em uma disputa ou teste de grupo pedido pela IA. */
    val advancedChecks: List<Pair<String, CheckResult>> = emptyList()
)

private data class CheckResolution(
    val chosen: CheckResult,
    val alternatives: List<CheckResult> = emptyList()
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
    private val playerTraits: () -> List<String> = { emptyList() },
    /** Bônus mecânicos de adaptações aprovadas, consultados por escopo (teste, atributo ou perícia). */
    private val additionalBonus: (String) -> Int = { 0 }
) {
    private val history = mutableListOf<TurnRecord>()
    private val narrations = mutableListOf<String>()

    val turns: List<TurnRecord> get() = history.toList()

    fun play(session: CampaignSession, input: PlayInput, onNarrativeChunk: (String) -> Unit = {}): PlayResult {
        val markBefore = session.mark()
        val resolution = try {
            input.check?.let { resolveCheck(session, adjusted(it, session, input.playerEntityId)) }
        } catch (e: Exception) {
            runCatching { session.rollbackTo(markBefore) }
            return PlayResult.Failed(e.message ?: "falha ao resolver o teste")
        }
        val check = resolution?.chosen
        val markAfterRoll = session.mark()
        return narrate(session, input, check, markBefore, markAfterRoll, chainDepth(history), onNarrativeChunk,
            fortuneAlternatives = resolution?.alternatives.orEmpty())
            .also { if (it is PlayResult.Played) history += it.record }
    }

    /**
     * Respeita a política de fortuna declarada pelo pacote no próprio CheckSpec.
     * O motor grava ambas as rolagens, mas apenas o resultado escolhido afeta o fluxo do jogo.
     */
    private fun resolveCheck(session: CampaignSession, spec: CheckSpec): CheckResolution =
        if (spec.fortuneMode == dev.rpgsolo.engine.FortuneMode.NORMAL) {
            CheckResolution(session.check(spec))
        } else {
            val result = session.checkWithFortune(spec, spec.fortuneMode)
            // A alternativa fica disponível à interface, mas não é enviada como resultado efetivo à IA.
            val unchosen = if (result.chosen.roll === result.first.roll) result.second else result.first
            CheckResolution(result.chosen, listOfNotNull(unchosen))
        }

    private fun adjusted(spec: CheckSpec, session: CampaignSession, playerId: String, skill: String? = null, traitIds: List<String> = playerTraits()): CheckSpec {
        val adjusted = menu?.adjust(spec, session.state.conditions[playerId].orEmpty(), traitIds, skill) ?: spec
        val scopes = listOfNotNull(spec.checkId, spec.stat, skill).distinct()
        val bonus = scopes.sumOf(additionalBonus)
        val withAdaptations = if (bonus == 0) adjusted else {
            val suffix = if (bonus > 0) " · adaptação +$bonus" else " · adaptação $bonus"
            if (adjusted.comparison == dev.rpgsolo.engine.Comparison.ROLL_UNDER) {
                adjusted.copy(difficulty = adjusted.difficulty + bonus, label = adjusted.label + suffix)
            } else {
                adjusted.copy(modifier = adjusted.modifier + bonus, label = adjusted.label + suffix)
            }
        }
        val danger = session.state.campaignStyle?.difficultyParams?.get("danger_level")?.roundToInt()?.coerceIn(0, 10) ?: 5
        val shift = danger - 5
        if (shift == 0) return withAdaptations
        val suffix = " · perigo ${if (shift > 0) "+" else ""}$shift"
        return when (withAdaptations.comparison) {
            dev.rpgsolo.engine.Comparison.ROLL_OVER ->
                withAdaptations.copy(modifier = withAdaptations.modifier - shift, label = withAdaptations.label + suffix)
            dev.rpgsolo.engine.Comparison.ROLL_UNDER ->
                withAdaptations.copy(difficulty = (withAdaptations.difficulty - shift).coerceAtLeast(1), label = withAdaptations.label + suffix)
            dev.rpgsolo.engine.Comparison.POINTS ->
                withAdaptations.copy(modifier = (withAdaptations.modifier - shift).coerceAtLeast(0), label = withAdaptations.label + suffix)
        }
    }

    /**
     * Prepara uma especificação para um participante de teste avançado, aplicando as condições,
     * traços e adaptações mecânicas da entidade real antes de rolar.
     */
    fun prepareAdvancedCheck(
        session: CampaignSession,
        spec: CheckSpec,
        entityId: String,
        skill: String? = null
    ): CheckSpec? {
        val entity = session.state.entities[entityId] ?: return null
        val traits = entity.traits.ifEmpty {
            if (entity.kind == dev.rpgsolo.engine.EntityKind.PLAYER) playerTraits() else emptyList()
        }
        return adjusted(spec, session, entityId, skill, traits)
    }

    /** Quantas resoluções de pedido do mestre vieram em sequência no fim do histórico (limita o encadeamento). */
    private fun chainDepth(records: List<TurnRecord>): Int = records.takeLastWhile { it.input.resolving != null }.size

    /** Teste ou rolagem pedidos pelo mestre que esperam o jogador. Derivado do histórico: desfazer a jogada o apaga sozinho. */
    fun pending(): Pending? = history.lastOrNull()?.pending

    /**
     * O jogador rola o que o mestre pediu: o motor rola e grava. Numa rolagem com tabela, o motor também aplica o dano da
     * face sorteada (a IA não decide o número). Depois o mestre narra o desfecho com a nota privada do resultado.
     * [template] traz jogador, ficha e recurso de saúde. Falha desfaz tudo, inclusive a rolagem.
     */
    fun resolvePending(
        session: CampaignSession,
        template: PlayInput,
        useFortuneResource: Boolean = false,
        onNarrativeChunk: (String) -> Unit = {}
    ): PlayResult {
        val pending = pending() ?: return PlayResult.Failed("não há teste pendente")
        var resolvedPending: Pending = pending
        var advancedChecks: List<Pair<String, CheckResult>> = emptyList()
        val markBefore = session.mark()
        if (pending is PendingCheck && (pending.request.revealSecretId != null || pending.request.revealPlaceSecret)) {
            val player = session.state.entities[template.playerEntityId]
                ?: return PlayResult.Failed("personagem inexistente")
            val required = pending.request.requiredStat ?: return PlayResult.Failed("pedido de segredo sem requiredStat")
            if (required !in player.stats && required !in player.skills) {
                return PlayResult.Failed("o personagem não tem o atributo/perícia exigido para revelar esse segredo")
            }
        }
        // Teste do mestre leva Ganho/Perda das condições; rolagem com tabela (d6 de dano) nunca.
        val pendingSpec = if (pending is PendingCheck) {
            adjusted(pending.spec, session, template.playerEntityId, pending.request.skill)
        } else pending.spec
        val resolution = try {
            when {
                pending is PendingCheck && pending.request.opposed != null -> {
                    if (useFortuneResource) return PlayResult.Failed("gasto de fortuna não é suportado em disputa")
                    val req = pending.request
                    val opposed = req.opposed
                    val target = session.state.entities[opposed.entityId]
                        ?: return PlayResult.Failed("participante oposto inexistente: ${opposed.entityId}")
                    val targetRequest = req.copy(
                        checkId = opposed.checkId,
                        difficultyId = opposed.difficultyId ?: req.difficultyId,
                        label = opposed.label ?: target.name,
                        opposed = null, group = null,
                        targetNpcId = null, revealSecretId = null, targetPlaceId = null,
                        revealPlaceSecret = false, requiredStat = null
                    )
                    val targetSpec = menu?.build(targetRequest, target.stats, target.skills)
                        ?: return PlayResult.Failed("não foi possível montar o teste do oponente")
                    val challenger = prepareAdvancedCheck(session, pending.spec, template.playerEntityId, req.skill)
                        ?: return PlayResult.Failed("personagem do jogador inexistente")
                    val defender = prepareAdvancedCheck(session, targetSpec, target.id, req.skill)
                        ?: return PlayResult.Failed("participante oposto inexistente")
                    val result = session.checkOpposed(challenger, defender)
                    advancedChecks = listOf(
                        (session.state.entities[template.playerEntityId]?.name ?: "Jogador") to result.challenger,
                        target.name to result.defender
                    )
                    val playerWon = result.challengerWins
                    val note = if (playerWon) {
                        "Resultado mecânico: o jogador venceu a disputa. Nota privada deste ramo: ${req.onSuccess}. Não revele o ramo alternativo."
                    } else {
                        "Resultado mecânico: o NPC venceu ou manteve a disputa. Nota privada deste ramo: ${req.onFailure}. Não revele o ramo alternativo."
                    }
                    resolvedPending = pending.copy(resultNote = note)
                    CheckResolution(result.challenger)
                }
                pending is PendingCheck && pending.request.group != null -> {
                    if (useFortuneResource) return PlayResult.Failed("gasto de fortuna não é suportado em teste de grupo")
                    val req = pending.request
                    val group = req.group
                    val ids = group.participantIds.map { if (it == "player") template.playerEntityId else it }
                    if (ids.size < 2) return PlayResult.Failed("teste de grupo exige pelo menos dois participantes")
                    if (ids.distinct().size != ids.size) return PlayResult.Failed("teste de grupo não pode repetir participantes")
                    if (template.playerEntityId !in ids) return PlayResult.Failed("teste de grupo precisa incluir o personagem do jogador")
                    if (group.requiredSuccesses !in 1..ids.size) {
                        return PlayResult.Failed("limiar de sucessos inválido para o tamanho do grupo")
                    }
                    val names = mutableListOf<String>()
                    val specs = ids.mapIndexed { index, id ->
                        val entity = session.state.entities[id]
                            ?: return PlayResult.Failed("participante de grupo inexistente: $id")
                        names += entity.name
                        val base = if (id == template.playerEntityId) pending.spec else {
                            val participantRequest = req.copy(
                                opposed = null, group = null, targetNpcId = null, revealSecretId = null,
                                targetPlaceId = null, revealPlaceSecret = false, requiredStat = null,
                                label = req.label ?: entity.name
                            )
                            menu?.build(participantRequest, entity.stats, entity.skills)
                                ?: return PlayResult.Failed("não foi possível montar o teste de ${entity.name}")
                        }
                        prepareAdvancedCheck(session, base, id, req.skill)
                            ?: return PlayResult.Failed("participante de grupo inexistente: $id")
                    }
                    val result = session.checkGroup(specs, group.requiredSuccesses)
                    advancedChecks = result.individual.mapIndexed { index, check -> names[index] to check }
                    val playerIndex = ids.indexOf(template.playerEntityId)
                    val note = if (result.passed) {
                        "Resultado mecânico: o grupo atingiu o limiar de sucesso. Nota privada deste ramo: ${group.onSuccess}. Não revele o ramo alternativo."
                    } else {
                        "Resultado mecânico: o grupo não atingiu o limiar de sucesso. Nota privada deste ramo: ${group.onFailure}. Não revele o ramo alternativo."
                    }
                    resolvedPending = pending.copy(resultNote = note)
                    CheckResolution(result.individual[playerIndex])
                }
                !useFortuneResource -> resolveCheck(session, pendingSpec)
                else -> {
                    val pendingCheck = pending as? PendingCheck
                        ?: return PlayResult.Failed("gasto de fortuna só pode ser usado em um teste")
                    val option = pendingCheck.fortuneSpendOption
                        ?: return PlayResult.Failed("este teste não oferece gasto de recurso para fortuna")
                    when (val spent = session.checkWithFortuneSpendingResource(
                        template.playerEntityId, option.resource, option.cost, pendingSpec, option.mode
                    )) {
                        is dev.rpgsolo.engine.FortuneSpendResult.Rejected -> {
                            runCatching { session.rollbackTo(markBefore) }
                            return PlayResult.Failed(spent.reason)
                        }
                        is dev.rpgsolo.engine.FortuneSpendResult.Resolved -> {
                            val result = spent.result
                            val unchosen = result.second?.let { second ->
                                if (result.chosen.roll === result.first.roll) second else result.first
                            }
                            CheckResolution(result.chosen, listOfNotNull(unchosen))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            runCatching { session.rollbackTo(markBefore) }
            return PlayResult.Failed(e.message ?: "falha ao resolver o teste pendente")
        }
        val input = template.copy(text = pending.playerText, check = null, resolving = resolvedPending)
        val check = resolution.chosen
        if (pending is PendingCheck && pending.request.revealSecretId != null) {
            val npcId = pending.request.targetNpcId ?: run {
                session.rollbackTo(markBefore)
                return PlayResult.Failed("pedido de segredo sem targetNpc")
            }
            val secretId = pending.request.revealSecretId ?: run {
                session.rollbackTo(markBefore)
                return PlayResult.Failed("pedido de segredo sem revealSecret")
            }
            when (val revealed = Reveal.discoverSecret(session, template.playerEntityId, npcId, secretId, check)) {
                null, is ExecResult.Accepted -> Unit
                is ExecResult.Rejected -> {
                    session.rollbackTo(markBefore)
                    return PlayResult.Failed("não foi possível registrar a descoberta: ${revealed.reason}")
                }
            }
        }
        if (pending is PendingCheck && pending.request.revealPlaceSecret) {
            val placeId = pending.request.targetPlaceId ?: run {
                session.rollbackTo(markBefore)
                return PlayResult.Failed("pedido de segredo de local sem targetPlace")
            }
            when (val revealed = Reveal.discoverPlaceSecret(session, template.playerEntityId, placeId, check)) {
                null, is ExecResult.Accepted -> Unit
                is ExecResult.Rejected -> {
                    session.rollbackTo(markBefore)
                    return PlayResult.Failed("não foi possível registrar a descoberta: ${revealed.reason}")
                }
            }
        }
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
                val configured = menu?.conditionCommand(player.id, condition)
                    ?: run {
                        session.rollbackTo(markBefore)
                        return PlayResult.Failed("condição '$condition' não existe no pacote ativo")
                    }
                val r = session.execute(configured)
                if (r is ExecResult.Rejected) {
                    session.rollbackTo(markBefore)
                    return PlayResult.Failed("o motor recusou a condição da rolagem: ${r.reason}")
                }
            }
        }
        val markAfterRoll = session.mark()
        return narrate(session, input, check, markBefore, markAfterRoll, chainDepth(history), onNarrativeChunk,
            fortuneAlternatives = resolution.alternatives, advancedChecks = advancedChecks)
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
        return when (val r = narrate(session, last.input, last.check, last.markBefore, last.markAfterRoll, chainDepth(history.dropLast(1)),
            fortuneAlternatives = last.fortuneAlternatives)) {
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
        session: CampaignSession, input: PlayInput, check: CheckResult?, markBefore: Int, markAfterRoll: Int, depth: Int,
        onNarrativeChunk: (String) -> Unit = {},
        fortuneAlternatives: List<CheckResult> = emptyList(),
        advancedChecks: List<Pair<String, CheckResult>> = emptyList()
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
                sceneNpcIds = input.sceneNpcIds,
                worldContext = input.worldContext,
                generationPreferences = input.generationPreferences,
                playerSheet = input.playerSheet,
                checkMenu = menu?.describe(),
                resolving = input.resolving,
                recentNarrative = narrations.toList(),
                budgetTokens = input.budgetTokens
            )
        )
        val chosen = if (premiumRunner != null && Importance.of(check) == NarrationTier.PREMIUM) premiumRunner else runner
        // No máximo duas resoluções seguidas pedindo coisa nova: evita o mestre prender o jogador em pedidos.
        return when (val o = chosen.run(
            session, request, menu, canRequest = depth < 2, playerEntityId = input.playerEntityId,
            onNarrativeChunk = onNarrativeChunk, sceneNpcIds = input.sceneNpcIds
        )) {
            is TurnOutcome.Applied -> {
                narrations += o.narrative
                val stats = session.state.entities[input.playerEntityId]?.stats.orEmpty()
                val skills = session.state.entities[input.playerEntityId]?.skills.orEmpty()
                val baseText = input.resolving?.playerText ?: input.text
                val checkReq = o.checkRequest
                val rollReq = o.rollRequest
                if (checkReq != null) {
                    val validationError = menu?.validateSecretRequest(
                        checkReq, session.state, allowedNpcIds = input.sceneNpcIds
                    )
                    if (validationError != null) {
                        return fail(session, markBefore, "pedido de revelação inválido: $validationError", null)
                    }
                }
                val pending: Pending? = when {
                    checkReq != null -> menu?.build(checkReq, stats, skills)?.let {
                        PendingCheck(checkReq, it, baseText, menu.fortuneSpendOption(checkReq.checkId))
                    }
                    rollReq != null -> menu?.let { PendingRoll(rollReq, it.buildRoll(rollReq), baseText) }
                    else -> null
                }
                PlayResult.Played(TurnRecord(input, markBefore, markAfterRoll, check, o.narrative, o.suggestions, pending, fortuneAlternatives, advancedChecks))
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
