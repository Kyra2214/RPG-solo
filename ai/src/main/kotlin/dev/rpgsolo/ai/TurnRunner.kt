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
        val rollRequest: RollRequest? = null
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
    private val npcCreator: ((NpcProposal) -> NpcResult)? = null
) {
    init {
        require(maxCorrections >= 0) { "maxCorrections deve ser >= 0" }
    }

    private sealed interface Attempt {
        data class Done(val outcome: TurnOutcome.Applied) : Attempt
        data class Retry(val problem: String, val fromEngine: Boolean) : Attempt
    }

    /** [menu] = testes que o mestre pode pedir neste jogo (null = o mestre não pode pedir teste). */
    fun run(session: CampaignSession, request: AiRequest, menu: CheckMenu? = null, canRequest: Boolean = true): TurnOutcome {
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
            when (val a = attempt(session, response.text, menu, canRequest)) {
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

    private fun attempt(session: CampaignSession, text: String, menu: CheckMenu?, canRequest: Boolean): Attempt =
        when (val parsed = ProposalParser.parse(text)) {
            is ParseResult.Invalid ->
                Attempt.Retry("formato inválido: " + parsed.errors.joinToString("; "), false)
            is ParseResult.Ok -> {
                val out = parsed.output
                val req = out.checkRequest
                val roll = out.rollRequest
                if (req != null || roll != null) {
                    // Teste ou rolagem pedidos: valida contra o pacote e só guarda o pedido. Mudanças de estado esperam o resultado.
                    val invalid = req?.let { request ->
                        menu?.validate(request) ?: "este jogo não oferece testes"
                    } ?: roll?.let { menu?.validateRoll(it, session.state) }
                    val secretInvalid = req?.let { menu?.validateSecretRequest(it, session.state) }
                    val problem: String? = when {
                        menu == null -> "este jogo não oferece testes nem rolagens; não use request_check nem request_roll"
                        !canRequest -> "você já pediu testes demais em sequência; narre o desfecho agora, sem request_check nem request_roll"
                        invalid != null -> "pedido inválido: $invalid"
                        secretInvalid != null -> "pedido de segredo inválido: $secretInvalid"
                        out.proposals.isNotEmpty() || out.npcProposal != null -> "com request_check/request_roll não envie proposals nem create_npc; proponha mudanças só depois do resultado"
                        else -> null
                    }
                    if (problem != null) Attempt.Retry(problem, false)
                    else Attempt.Done(TurnOutcome.Applied(out.narrative, emptyList(), emptyList(), req, roll))
                } else if (out.proposals.isEmpty() && out.npcProposal == null) {
                    Attempt.Done(TurnOutcome.Applied(out.narrative, emptyList(), out.suggestions))
                } else {
                    // O motor não aceita dano/cura/custo de recurso inventado para o personagem do jogador.
                    // Itens e condições também precisam existir no pacote ativo; seus efeitos vêm do pacote.
                    val normalized = mutableListOf<dev.rpgsolo.engine.Command>()
                    for (command in out.proposals) {
                        val playerTarget = when (command) {
                            is dev.rpgsolo.engine.Damage -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                            is dev.rpgsolo.engine.Heal -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                            is dev.rpgsolo.engine.SpendResource -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                            is dev.rpgsolo.engine.GainXp -> session.state.entities[command.entityId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                            is dev.rpgsolo.engine.ConsumeItem -> session.state.entities[command.ownerId]?.kind == dev.rpgsolo.engine.EntityKind.PLAYER
                            else -> false
                        }
                        if (playerTarget) {
                            return Attempt.Retry(
                                "esta proposta alteraria diretamente recursos, XP ou inventário do jogador; use uma ação local ou uma rolagem mecânica do motor",
                                false
                            )
                        }
                        when (command) {
                            is dev.rpgsolo.engine.AddCondition -> {
                                val conditionMenu = menu
                                    ?: return Attempt.Retry("não há pacote ativo para validar condições mecânicas", false)
                                val configured = conditionMenu.conditionCommand(command.entityId, command.condition)
                                    ?: return Attempt.Retry("condição '${command.condition}' não existe no pacote ativo", false)
                                normalized += configured
                            }
                            is dev.rpgsolo.engine.AddItem, is dev.rpgsolo.engine.ConsumeItem -> {
                                val itemMenu = menu
                                    ?: return Attempt.Retry("não há pacote ativo para validar itens", false)
                                val itemId = when (command) {
                                    is dev.rpgsolo.engine.AddItem -> command.itemId
                                    is dev.rpgsolo.engine.ConsumeItem -> command.itemId
                                    else -> error("comando não é item")
                                }
                                if (!itemMenu.hasItem(itemId)) {
                                    return Attempt.Retry("item '$itemId' não existe no pacote ativo", false)
                                }
                                normalized += command
                            }
                            else -> normalized += command
                        }
                    }
                    out.npcProposal?.let { proposal ->
                        val npcMenu = menu
                        if (npcMenu == null && npcCreator == null) {
                            return Attempt.Retry("não há pacote nem contexto de sistema para criar a ficha de NPC", false)
                        }
                        val npcId = "npc-${session.mark()}-${proposal.name.trim().lowercase().hashCode()}"
                        val candidate = proposal.copy(id = npcId)
                        val result = npcCreator?.invoke(candidate) ?: npcMenu.createNpc(candidate)
                        val sheet = when (result) {
                            is NpcResult.Created -> result.sheet
                            is NpcResult.Rejected ->
                                return Attempt.Retry("ficha de NPC recusada: ${result.errors.joinToString("; ")}", false)
                        }
                        normalized += dev.rpgsolo.engine.CreateEntity(sheet.toEntity())
                        sheet.dossier?.let { normalized += dev.rpgsolo.engine.SetNpcDossier(it) }
                    }
                    when (val r = session.executeAll(normalized)) {
                        is ExecResult.Accepted -> Attempt.Done(TurnOutcome.Applied(out.narrative, r.events, out.suggestions))
                        is ExecResult.Rejected ->
                            Attempt.Retry("o motor recusou a proposta ${r.commandIndex + 1}: ${r.reason}", true)
                    }
                }
            }
        }
}
