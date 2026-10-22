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
import dev.rpgsolo.engine.spendLuck

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
    val generationPreferences: String? = null,
    /** CP-03: briefing de retomada; só é enviado enquanto não há cenas recentes na memória do loop. */
    val resumeBriefing: String? = null,
    /** DC-05.1: trechos do documento-fonte para esta cena. */
    val sourceContext: String? = null,
    /** Notas mecânicas privadas persistidas para regenerar a mesma narração sem perder imunidades aplicadas. */
    val mechanicalNotes: List<String> = emptyList()
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
    val advancedChecks: List<Pair<String, CheckResult>> = emptyList(),
    /** Poder pedido pela IA nesta jogada e resolvido pelo motor. */
    val powerUse: PowerUse? = null,
    /** true depois que o jogador gastou sorte pós-rolagem neste teste (um uso por teste). */
    val luckUsed: Boolean = false
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
    private val additionalBonus: (String) -> Int = { 0 },
    /** Efeitos tipados de outras fontes, preservando a origem no registro do teste. */
    private val externalBonuses: (Collection<String>) -> List<dev.rpgsolo.rules.ExternalBonus> = { emptyList() },
    /** Origem individual das adaptações aprovadas; o total mecânico continua vindo de additionalBonus. */
    private val additionalBonusSources: (Collection<String>) -> List<dev.rpgsolo.rules.ExternalBonus> = { emptyList() },
    /** Poderes que o mestre pode pedir por request_power (do pacote ativo). null = não pode. */
    private val powerMenu: PowerMenu? = null
) {
    /** Uma jogada por vez: protege mark() → rede → rollbackTo() (R-01). Compartilhado com o PlayController. */
    val turnGate = TurnGate()

    private val history: MutableList<TurnRecord> = java.util.Collections.synchronizedList(mutableListOf<TurnRecord>())
    private val narrations = mutableListOf<String>()

    /** Tamanho do log ao fim da última narração aceita; o que entrou depois foi tempo passando fora da IA (M-07). */
    /** -1 = ainda sem narração nesta sessão (ex.: campanha reaberta): sem marcação, para não tratar o histórico todo como novo. */
    private var narratedUpTo = -1

    val turns: List<TurnRecord> get() = history.toList()

    fun play(session: CampaignSession, input: PlayInput, onNarrativeChunk: (String) -> Unit = {}): PlayResult =
        turnGate.exclusive { playLocked(session, input, onNarrativeChunk) }

    private fun playLocked(session: CampaignSession, input: PlayInput, onNarrativeChunk: (String) -> Unit): PlayResult {
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
        // Adaptações aprovadas alteram a ficha do jogador; não devem virar bônus globais de NPC.
        val isPlayerEntity = session.state.entities[playerId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
        val bonus = if (isPlayerEntity) scopes.sumOf(additionalBonus) else 0
        val adaptationSources = if (isPlayerEntity) additionalBonusSources(scopes)
            .filter { it.scope in scopes && it.bonus != 0 } else emptyList()
        val withAdaptations = if (bonus == 0 && adaptationSources.isEmpty()) adjusted else {
            val sourceLabel = if (adaptationSources.isEmpty()) "" else " · adaptações: " +
                adaptationSources.joinToString(", ") { effect ->
                    "${effect.source} (${effect.scope} ${if (effect.bonus > 0) "+" else ""}${effect.bonus})"
                }
            val totalLabel = if (bonus == 0) "" else
                " · adaptação " + if (bonus > 0) "+$bonus" else bonus.toString()
            val label = adjusted.label + sourceLabel + totalLabel
            if (bonus == 0) adjusted.copy(label = label)
            else if (adjusted.comparison == dev.rpgsolo.engine.Comparison.ROLL_UNDER) {
                adjusted.copy(difficulty = adjusted.difficulty + bonus, label = label)
            } else {
                adjusted.copy(modifier = adjusted.modifier + bonus, label = label)
            }
        }
        // Efeitos da ficha escolhida pertencem somente ao jogador; nunca a NPCs ou adversários.
        val selectedExternal = if (isPlayerEntity) {
            dev.rpgsolo.rules.distinctExternalBonuses(
                externalBonuses(scopes).filter { it.scope in scopes && it.bonus != 0 }
            )
        } else emptyList()
        val externalTotal = selectedExternal.sumOf { it.bonus }
        val withExternal = if (selectedExternal.isEmpty()) withAdaptations else {
            // Mostra cada origem/escopo mesmo quando bônus diferentes se anulam no total.
            val details = selectedExternal.joinToString(", ") { effect ->
                "${effect.source} (${effect.scope} ${if (effect.bonus > 0) "+" else ""}${effect.bonus})"
            }
            val totalSuffix = if (externalTotal == 0) "" else
                " · efeito externo " + if (externalTotal > 0) "+$externalTotal" else externalTotal.toString()
            val label = withAdaptations.label + " · efeitos: $details" + totalSuffix
            if (externalTotal == 0) {
                withAdaptations.copy(label = label)
            } else if (withAdaptations.comparison == dev.rpgsolo.engine.Comparison.ROLL_UNDER) {
                withAdaptations.copy(difficulty = withAdaptations.difficulty + externalTotal, label = label)
            } else {
                withAdaptations.copy(modifier = withAdaptations.modifier + externalTotal, label = label)
            }
        }
        val danger = session.state.campaignStyle?.difficultyParams?.get("danger_level")?.roundToInt()?.coerceIn(0, 10) ?: 5
        val shift = danger - 5
        if (shift == 0) return withExternal
        val suffix = " · perigo ${if (shift > 0) "+" else ""}$shift"
        return when (withExternal.comparison) {
            dev.rpgsolo.engine.Comparison.ROLL_OVER ->
                withExternal.copy(modifier = withExternal.modifier - shift, label = withExternal.label + suffix)
            dev.rpgsolo.engine.Comparison.ROLL_UNDER ->
                withExternal.copy(difficulty = (withExternal.difficulty - shift).coerceAtLeast(1), label = withExternal.label + suffix)
            dev.rpgsolo.engine.Comparison.POINTS ->
                withExternal.copy(modifier = (withExternal.modifier - shift).coerceAtLeast(0), label = withExternal.label + suffix)
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
    ): PlayResult = turnGate.exclusive { resolvePendingLocked(session, template, useFortuneResource, onNarrativeChunk) }

    private fun resolvePendingLocked(
        session: CampaignSession,
        template: PlayInput,
        useFortuneResource: Boolean,
        onNarrativeChunk: (String) -> Unit
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
                    if (target.kind != dev.rpgsolo.engine.EntityKind.NPC || !target.alive) {
                        return PlayResult.Failed("o participante oposto deve ser um NPC vivo")
                    }
                    if (template.sceneNpcIds.isNotEmpty() && opposed.entityId !in template.sceneNpcIds) {
                        return PlayResult.Failed("o participante oposto não está presente na cena")
                    }
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
                    CheckResolution(result.challenger, result.challengerAlternatives + result.defenderAlternatives)
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
                    for (id in ids) {
                        val participant = session.state.entities[id]
                            ?: return PlayResult.Failed("participante de grupo inexistente: $id")
                        if (!participant.alive) return PlayResult.Failed("participante de grupo fora de combate: $id")
                        if (id != template.playerEntityId) {
                            if (participant.kind != dev.rpgsolo.engine.EntityKind.NPC) {
                                return PlayResult.Failed("participante de grupo deve ser um NPC: $id")
                            }
                            if (template.sceneNpcIds.isNotEmpty() && id !in template.sceneNpcIds) {
                                return PlayResult.Failed("participante de grupo fora da cena: $id")
                            }
                        }
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
                    CheckResolution(result.individual[playerIndex], result.alternatives.flatten())
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
        var input = template.copy(text = pending.playerText, check = null, resolving = resolvedPending)
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
        val mechanicalNotes = mutableListOf<String>()
        if (pending is PendingRoll) {
            val face = check.roll.dice.single()
            val entry = pending.request.table[face]
            val damage = if (entry == null) 0 else menu?.rollDamage(entry, session.state, template.playerEntityId) ?: entry.damage
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
                if (menu?.isImmune(player.id, condition, player.traits) == true) {
                    val name = menu.conditionName(condition) ?: condition
                    mechanicalNotes += "A condição $name foi bloqueada pela imunidade de ${player.name}; não a aplique nem descreva como instalada."
                    continue
                }
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
        input = input.copy(mechanicalNotes = mechanicalNotes)
        val markAfterRoll = session.mark()
        return narrate(session, input, check, markBefore, markAfterRoll, chainDepth(history), onNarrativeChunk,
            fortuneAlternatives = resolution.alternatives, advancedChecks = advancedChecks)
            .also { if (it is PlayResult.Played) history += it.record }
    }

    /**
     * Sorte pós-rolagem (F3) oferecida para o último teste, ou null. Só vale para teste direto da jogada (sem pedido do mestre,
     * disputa, grupo ou revelação, que gravam efeitos dependentes do dado), uma vez por teste, com recurso suficiente.
     */
    fun luckOffer(session: CampaignSession, playerEntityId: String): LuckOption? = turnGate.exclusive {
        val last = history.lastOrNull() ?: return@exclusive null
        luckOfferFor(session, last, playerEntityId)
    }

    private fun luckOfferFor(session: CampaignSession, last: TurnRecord, playerEntityId: String): LuckOption? {
        val check = last.check ?: return null
        if (last.luckUsed || last.pending != null || last.input.resolving != null || last.advancedChecks.isNotEmpty()) return null
        val option = menu?.luckOption(check.spec.checkId) ?: return null
        val available = session.state.entities[playerEntityId]?.resources?.get(option.resource)?.current ?: return null
        return option.takeIf { available >= it.cost }
    }

    /**
     * Gasta sorte no último teste: volta ao ponto logo após a rolagem, o motor gasta o recurso e repete a rolagem ou soma uma face,
     * e o mestre narra de novo o novo resultado. Falha ao narrar desfaz a jogada inteira, como em [regenerate].
     */
    fun spendLuck(
        session: CampaignSession, playerEntityId: String, kind: dev.rpgsolo.engine.LuckKind, onNarrativeChunk: (String) -> Unit = {}
    ): PlayResult = turnGate.exclusive {
        val last = history.lastOrNull() ?: return@exclusive PlayResult.Failed("não há jogada com teste")
        val option = luckOfferFor(session, last, playerEntityId)
            ?: return@exclusive PlayResult.Failed("este teste não aceita sorte agora")
        if (kind !in option.kinds) return@exclusive PlayResult.Failed("este teste não aceita essa forma de sorte")
        val original = last.check!!
        session.rollbackTo(last.markAfterRoll)
        val spent = session.spendLuck(playerEntityId, option.resource, option.cost, original, kind)
        if (spent is dev.rpgsolo.engine.LuckResult.Rejected) {
            history.removeLast()
            narrations.removeLastOrNull()
            session.rollbackTo(last.markBefore)
            return@exclusive PlayResult.Failed(spent.reason)
        }
        val resolved = spent as dev.rpgsolo.engine.LuckResult.Resolved
        narrations.removeLastOrNull()
        when (val r = narrate(
            session, last.input, resolved.chosen, last.markBefore, session.mark(), chainDepth(history.dropLast(1)), onNarrativeChunk,
            fortuneAlternatives = last.fortuneAlternatives + resolved.original
        )) {
            is PlayResult.Played -> {
                history[history.lastIndex] = r.record.copy(luckUsed = true)
                PlayResult.Played(history.last())
            }
            is PlayResult.Failed -> {
                history.removeLast()
                session.rollbackTo(last.markBefore)
                r
            }
        }
    }

    /** 7.4.1: reverte a última jogada (eventos e rolagens). false se não há o que desfazer. */
    fun undo(session: CampaignSession): Boolean = turnGate.exclusive { undoLocked(session) }

    private fun undoLocked(session: CampaignSession): Boolean {
        val last = history.removeLastOrNull() ?: return false
        session.rollbackTo(last.markBefore)
        narrations.removeLastOrNull()
        return true
    }

    /** 7.4.2: nova narração do MESMO resultado. Mantém os dados rolados; refaz as propostas da IA. */
    fun regenerate(session: CampaignSession): PlayResult = turnGate.exclusive { regenerateLocked(session) }

    private fun regenerateLocked(session: CampaignSession): PlayResult {
        val last = history.lastOrNull() ?: return PlayResult.Failed("não há jogada para refazer")
        session.rollbackTo(last.markAfterRoll)
        narrations.removeLastOrNull()
        return when (val r = narrate(session, last.input, last.check, last.markBefore, last.markAfterRoll, chainDepth(history.dropLast(1)),
            fortuneAlternatives = last.fortuneAlternatives, advancedChecks = last.advancedChecks)) {
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
        val recentClockEvents = if (narratedUpTo < 0) emptyList() else session.events
            .let { all -> all.subList(minOf(narratedUpTo, markBefore).coerceIn(0, all.size), markBefore.coerceIn(0, all.size)) }
        val clockAdvances = recentClockEvents.filterIsInstance<dev.rpgsolo.engine.ClockAdvanced>()
        val advancedClocks = clockAdvances.map { it.id }.toSet()
        val midpointClocks = clockAdvances.filter { event ->
            val clock = session.state.clocks[event.id] ?: return@filter false
            clock.midClue.isNotBlank() &&
                event.filled - event.delta < clock.midpoint &&
                event.filled >= clock.midpoint &&
                event.filled < clock.segments
        }.map { it.id }.toSet()
        val request = ContextAssembler.build(
            ContextInput(
                advancedClockIds = advancedClocks,
                midpointClockIds = midpointClocks,
                mechanicalNotes = input.mechanicalNotes,
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
                checkMenu = listOfNotNull(menu?.describe(), powerMenu?.describe(playerTraits())).joinToString("\n\n").ifBlank { null },
                resolving = input.resolving,
                recentNarrative = narrations.toList(),
                resumeBriefing = if (narrations.isEmpty()) input.resumeBriefing else null,
                sourceContext = input.sourceContext,
                budgetTokens = input.budgetTokens
            )
        )
        val chosen = if (premiumRunner != null && Importance.of(check) == NarrationTier.PREMIUM) premiumRunner else runner
        // No máximo duas resoluções seguidas pedindo coisa nova: evita o mestre prender o jogador em pedidos.
        return when (val o = chosen.run(
            session, request, menu, canRequest = depth < 2, playerEntityId = input.playerEntityId,
            onNarrativeChunk = onNarrativeChunk, sceneNpcIds = input.sceneNpcIds,
            powers = powerMenu?.let { PowerContext(it, playerTraits) }
        )) {
            is TurnOutcome.Applied -> {
                narrations += o.narrative
                narratedUpTo = session.mark()
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
                PlayResult.Played(TurnRecord(input, markBefore, markAfterRoll, check, o.narrative, o.suggestions, pending, fortuneAlternatives, advancedChecks, o.powerUse))
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
