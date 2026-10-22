package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Event
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.rules.NpcProposal
import dev.rpgsolo.rules.NpcResult

sealed interface TurnOutcome {
    /** Narrativa liberada e propostas aplicadas pelo motor (lista vazia = só narração). */
    data class Applied(
        val narrative: String,
        val events: List<Event>,
        val suggestions: List<String> = emptyList(),
        /** O mestre pediu um teste: nada foi aplicado ainda; o jogo espera o jogador rolar. */
        val checkRequest: CheckRequest? = null,
        /** O mestre pediu uma rolagem com tabela: nada foi aplicado ainda; o jogo espera o jogador rolar. */
        val rollRequest: RollRequest? = null,
        /** Poder pedido pela IA e já resolvido pelo motor nesta jogada. */
        val powerUse: PowerUse? = null,
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
    private val maxCorrections: Int = 1,
    /** Permite criar NPCs sem pacote, desde que o app forneça uma edição e marque a ficha como não validada. */
    private val npcCreator: ((NpcProposal) -> NpcResult)? = null,
) {
    init {
        require(maxCorrections >= 0) { "maxCorrections deve ser >= 0" }
    }

    private sealed interface Attempt {
        data class Done(val outcome: TurnOutcome.Applied) : Attempt

        data class Retry(val problem: String, val fromEngine: Boolean) : Attempt
    }

    /** [menu] = testes que o mestre pode pedir neste jogo (null = o mestre não pode pedir teste). */
    fun run(
        session: CampaignSession,
        request: AiRequest,
        menu: CheckMenu? = null,
        canRequest: Boolean = true,
        playerEntityId: String? = null,
        sceneNpcIds: Set<String> = emptySet(),
        onNarrativeChunk: (String) -> Unit = {},
        powers: PowerContext? = null,
    ): TurnOutcome {
        val mark = session.mark()
        var messages = request.messages
        var lastProblem = ""
        var lastFromEngine = false

        for (i in 0..maxCorrections) {
            val response =
                try {
                    val decoder = NarrativeJsonStream()
                    var emitted = ""
                    provider.stream(request.copy(messages = messages, jsonMode = true)) { chunk ->
                        val narrative = decoder.accept(chunk)
                        if (narrative.length > emitted.length) {
                            emitted = narrative
                            runCatching { onNarrativeChunk(narrative) }
                        }
                    }
                } catch (e: AiError) {
                    return TurnOutcome.Failed(e.message ?: "erro de IA", e)
                }
            when (val a = attempt(session, response.text, menu, canRequest, playerEntityId, sceneNpcIds, powers)) {
                is Attempt.Done -> return a.outcome
                is Attempt.Retry -> {
                    lastProblem = a.problem
                    lastFromEngine = a.fromEngine
                    messages = messages +
                        AiMessage(Role.ASSISTANT, response.text) +
                        AiMessage(
                            Role.USER,
                            "Sua resposta foi recusada: ${a.problem}. " +
                                "Responda de novo apenas com o JSON no formato pedido, corrigindo o problema.",
                        )
                }
            }
        }
        // Defesa extra: lote recusado não grava nada, mas garantimos o ponto de partida.
        if (session.eventCount != mark) session.rollbackTo(mark)
        return if (lastFromEngine) TurnOutcome.Refused(lastProblem) else TurnOutcome.Failed(lastProblem)
    }

    /** Extrai incrementalmente apenas o campo narrative do JSON; nunca mostra o restante do payload. */
    private class NarrativeJsonStream {
        private val raw = StringBuilder()

        fun accept(chunk: String): String {
            raw.append(chunk)
            val source = raw.toString()
            val keyText = "\"narrative\""
            val key = source.indexOf(keyText)
            if (key < 0) return ""
            var i = key + keyText.length
            while (i < source.length && source[i].isWhitespace()) i++
            if (i >= source.length || source[i++] != ':') return ""
            while (i < source.length && source[i].isWhitespace()) i++
            if (i >= source.length || source[i++] != '"') return ""
            val out = StringBuilder()
            while (i < source.length) {
                val ch = source[i++]
                if (ch == '"') break
                if (ch != '\\') {
                    out.append(ch)
                    continue
                }
                if (i >= source.length) break
                when (val esc = source[i++]) {
                    '"' -> out.append('"')
                    '\\' -> out.append('\\')
                    '/' -> out.append('/')
                    'b' -> out.append('\b')
                    'f' -> out.append('\u000c')
                    'n' -> out.append('\n')
                    'r' -> out.append('\r')
                    't' -> out.append('\t')
                    'u' -> {
                        if (i + 4 > source.length) break
                        val code = source.substring(i, i + 4).toIntOrNull(16) ?: break
                        out.append(code.toChar())
                        i += 4
                    }
                    else -> out.append(esc)
                }
            }
            return out.toString()
        }
    }

    /** request_power: valida pelo pacote e deixa o motor gastar custo, rolar e aplicar o efeito. Recusa volta à IA. */
    private fun attemptPower(
        session: CampaignSession,
        out: TurnOutput,
        powers: PowerContext?,
        playerEntityId: String?,
    ): Attempt {
        val request = out.powerRequest!!
        val context = powers ?: return Attempt.Retry("este jogo não oferece poderes; não use request_power", false)
        val playerId = playerEntityId ?: return Attempt.Retry("o jogador não foi identificado para usar o poder", false)
        val targetTraits = request.targetId?.let { session.state.entities[it]?.traits.orEmpty() }.orEmpty()
        val built = context.menu.build(request.powerId, playerId, request.targetId, session.state, context.traitIds(), targetTraits)
        val ready =
            when (built) {
                is PowerBuild.Invalid -> return Attempt.Retry("poder recusado: ${built.reason}", false)
                is PowerBuild.Ready -> built
            }
        val mark = session.mark()
        return when (val r = session.activatePower(ready.spec)) {
            is dev.rpgsolo.engine.PowerResult.Rejected -> {
                if (session.eventCount != mark) session.rollbackTo(mark)
                Attempt.Retry("o motor recusou o poder: ${r.reason}", true)
            }
            is dev.rpgsolo.engine.PowerResult.Activated ->
                Attempt.Done(
                    TurnOutcome.Applied(
                        out.narrative,
                        session.events.drop(mark),
                        out.suggestions,
                        powerUse =
                            PowerUse(
                                ready.def.name,
                                session.state.entities[playerId]?.name ?: playerId,
                                ready.spec.targetId?.let { session.state.entities[it]?.name },
                                ready.spec,
                                r,
                            ),
                    ),
                )
        }
    }

    private fun attempt(
        session: CampaignSession,
        text: String,
        menu: CheckMenu?,
        canRequest: Boolean,
        playerEntityId: String?,
        sceneNpcIds: Set<String>,
        powers: PowerContext?,
    ): Attempt {
        return when (val parsed = ProposalParser.parse(text)) {
            is ParseResult.Invalid ->
                Attempt.Retry("formato inválido: " + parsed.errors.joinToString("; "), false)
            is ParseResult.Ok -> {
                val out = parsed.output
                if (out.powerRequest != null) return attemptPower(session, out, powers, playerEntityId)
                val req = out.checkRequest
                val roll = out.rollRequest
                if (req != null || roll != null) {
                    // Teste ou rolagem pedidos: valida contra o pacote e só guarda o pedido. Mudanças de estado esperam o resultado.
                    val invalid =
                        when {
                            menu == null -> "este jogo não oferece testes nem rolagens"
                            req != null -> menu.validate(req)
                            roll != null -> menu.validateRoll(roll, session.state)
                            else -> null
                        }
                    val secretInvalid = req?.let { menu?.validateSecretRequest(it, session.state) }
                    val advancedInvalid =
                        req?.let { request ->
                            val playerId = playerEntityId ?: return@let "o jogador não foi identificado para o teste avançado"
                            menu?.validateAdvancedRequest(request, session.state, playerId, sceneNpcIds)
                        }
                    val problem: String? =
                        when {
                            menu == null -> "este jogo não oferece testes nem rolagens; não use request_check nem request_roll"
                            !canRequest -> "você já pediu testes demais em sequência; narre o desfecho agora, sem request_check nem request_roll"
                            invalid != null -> "pedido inválido: $invalid"
                            secretInvalid != null -> "pedido de segredo inválido: $secretInvalid"
                            advancedInvalid != null -> "pedido de teste avançado inválido: $advancedInvalid"
                            out.proposals.isNotEmpty() || out.npcProposal != null -> "com request_check/request_roll não envie proposals nem create_npc; proponha mudanças só depois do resultado"
                            else -> null
                        }
                    if (problem != null) {
                        Attempt.Retry(problem, false)
                    } else {
                        Attempt.Done(TurnOutcome.Applied(out.narrative, emptyList(), emptyList(), req, roll))
                    }
                } else if (out.proposals.isEmpty() && out.npcProposal == null) {
                    Attempt.Done(TurnOutcome.Applied(out.narrative, emptyList(), out.suggestions))
                } else {
                    // O motor não aceita dano/cura/custo de recurso inventado para o personagem do jogador.
                    // Itens e condições também precisam existir no pacote ativo; seus efeitos vêm do pacote.
                    if (out.requestCombat && (menu == null || playerEntityId == null)) {
                        return Attempt.Retry("request_combat exige personagem do jogador e pacote ativo", false)
                    }
                    val transactionMark = session.mark()
                    val normalized = mutableListOf<dev.rpgsolo.engine.Command>()
                    for (command in out.proposals) {
                        val playerTarget =
                            when (command) {
                                is dev.rpgsolo.engine.Damage -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                                is dev.rpgsolo.engine.Heal -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                                is dev.rpgsolo.engine.SpendResource -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                                is dev.rpgsolo.engine.GainXp -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                                is dev.rpgsolo.engine.ConsumeItem -> session.state.entities[command.ownerId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                                is dev.rpgsolo.engine.AddCondition -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                                is dev.rpgsolo.engine.RemoveCondition -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                                else -> false
                            }
                        val combatantId =
                            when (command) {
                                is dev.rpgsolo.engine.Damage -> command.entityId
                                is dev.rpgsolo.engine.Heal -> command.entityId
                                is dev.rpgsolo.engine.SpendResource -> command.entityId
                                is dev.rpgsolo.engine.AddCondition -> command.entityId
                                is dev.rpgsolo.engine.RemoveCondition -> command.entityId
                                else -> null
                            }
                        if (combatantId != null && session.state.combat?.order?.contains(combatantId) == true) {
                            return Attempt.Retry(
                                "durante o combate PV, custos e condições de quem luta só mudam por ações do motor (ataque, poder, condição do pacote); proponha apenas a intenção e narre o resultado",
                                false,
                            )
                        }
                        if (playerTarget) {
                            return Attempt.Retry(
                                "esta proposta alteraria diretamente recursos, XP, inventário ou condições do jogador; use uma ação local ou uma rolagem mecânica do motor",
                                false,
                            )
                        }
                        when (command) {
                            is dev.rpgsolo.engine.AddCondition -> {
                                val conditionMenu =
                                    menu
                                        ?: return Attempt.Retry("não há pacote ativo para validar condições mecânicas", false)
                                val configured =
                                    conditionMenu.conditionCommand(command.entityId, command.condition)
                                        ?: return Attempt.Retry("condição '${command.condition}' não existe no pacote ativo", false)
                                val targetTraits = session.state.entities[command.entityId]?.traits.orEmpty()
                                if (conditionMenu.isImmune(command.entityId, command.condition, targetTraits)) {
                                    return Attempt.Retry("o alvo é imune à condição '${command.condition}'", false)
                                }
                                normalized += configured
                            }
                            is dev.rpgsolo.engine.AddItem, is dev.rpgsolo.engine.ConsumeItem -> {
                                val itemMenu =
                                    menu
                                        ?: return Attempt.Retry("não há pacote ativo para validar itens", false)
                                val itemId =
                                    when (command) {
                                        is dev.rpgsolo.engine.AddItem -> command.itemId
                                        is dev.rpgsolo.engine.ConsumeItem -> command.itemId
                                        else -> error("comando não é item")
                                    }
                                if (!itemMenu.hasItem(itemId)) {
                                    return Attempt.Retry("item '$itemId' não existe no pacote ativo", false)
                                }
                                normalized += command
                            }
                            is dev.rpgsolo.engine.ProposeRuleAddition -> {
                                val ruleMenu =
                                    menu
                                        ?: return Attempt.Retry("adaptação exige um pacote de regras ativo", false)
                                val invalid = ruleMenu.validateRuleAddition(command.rule)
                                if (invalid != null) return Attempt.Retry("adaptação recusada: $invalid", false)
                                normalized += command
                            }
                            else -> normalized += command
                        }
                    }
                    var combatNpcId: String? = null
                    out.npcProposal?.let { proposal ->
                        val npcMenu = menu
                        if (npcMenu == null && npcCreator == null) {
                            return Attempt.Retry("não há pacote nem contexto de sistema para criar a ficha de NPC", false)
                        }
                        val npcId =
                            if (proposal.id == "pending") {
                                "npc-$transactionMark-${proposal.name.trim().lowercase().hashCode()}"
                            } else {
                                if (proposal.id in session.state.entities || proposal.id == playerEntityId || proposal.id == "player") {
                                    return Attempt.Retry("o id '${proposal.id}' do NPC já está em uso; escolha outro", false)
                                }
                                proposal.id
                            }
                        combatNpcId = npcId
                        val candidate = proposal.copy(id = npcId)
                        val result =
                            npcCreator?.invoke(candidate)
                                ?: npcMenu?.createNpc(candidate)
                                ?: return Attempt.Retry("não há pacote ativo para validar a ficha de NPC", false)
                        val sheet =
                            when (result) {
                                is NpcResult.Created -> result.sheet
                                is NpcResult.Rejected ->
                                    return Attempt.Retry("ficha de NPC recusada: ${result.errors.joinToString("; ")}", false)
                            }
                        normalized += dev.rpgsolo.engine.CreateEntity(sheet.toEntity())
                        sheet.dossier?.let { normalized += dev.rpgsolo.engine.SetNpcDossier(it) }
                    }
                    when (val r = session.executeAll(normalized)) {
                        is ExecResult.Accepted -> {
                            if (out.requestCombat) {
                                val npcId =
                                    combatNpcId
                                        ?: run {
                                            session.rollbackTo(transactionMark)
                                            return Attempt.Retry("request_combat não produziu um adversário", false)
                                        }
                                val activeMenu =
                                    menu
                                        ?: run {
                                            session.rollbackTo(transactionMark)
                                            return Attempt.Retry("request_combat exige pacote de regras ativo", false)
                                        }
                                val startError = activeMenu.startCombat(session, playerEntityId!!, npcId)
                                if (startError != null) {
                                    session.rollbackTo(transactionMark)
                                    return Attempt.Retry("não foi possível iniciar o combate: $startError", true)
                                }
                                Attempt.Done(
                                    TurnOutcome.Applied(
                                        out.narrative,
                                        session.events.drop(transactionMark),
                                        out.suggestions,
                                    ),
                                )
                            } else {
                                Attempt.Done(TurnOutcome.Applied(out.narrative, r.events, out.suggestions))
                            }
                        }
                        is ExecResult.Rejected ->
                            Attempt.Retry("o motor recusou a proposta ${r.commandIndex + 1}: ${r.reason}", true)
                    }
                }
            }
        }
    }
}
