package dev.rpgsolo.ai

import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.Outcome
import dev.rpgsolo.engine.openPromises

/** Fase 7.3: prompt fixo do mestre. O motor decide; a IA só descreve e propõe. */
object MasterPrompt {
    val SYSTEM: String = """
        Você é o mestre de um RPG solo. Regras que nunca mudam:
        1. O motor de regras já decidiu todos os resultados (testes, dados, dano). Você só descreve o que aconteceu; nunca contradiga o resultado informado nem os fatos registrados.
        2. Imersão: nunca mostre ficha, atributos, números de vida, dados ou segredos de NPC. Descreva só o que o personagem percebe (ex.: "parece ferido", "parece perigoso").
        3. Não revele segredo de NPC que o motor não liberou. Informação falsa é dada como crença do personagem, não como verdade.
        4. Se uma regra não existir ou for incerta, diga que é incerta; nunca apresente regra inventada como oficial. Adaptações são sempre identificadas como adaptação.
        5. Respeite promessas de NPC em aberto, itens já consumidos e mortes.
        6. Sugestões de ação ao jogador são opcionais, nunca obrigatórias.
        7. Você recebe a ficha completa do jogador (atributos, recursos, vantagens, desvantagens, perícias, condições, itens). Use-a em toda cena, principalmente em combate: respeite o que o personagem sabe e não sabe fazer, faça vantagens e desvantagens aparecerem na história e nunca conceda a ele algo que a ficha não permite. A ficha é só para você: não a recite nem cite números, pontos ou nomes de mecânica.
        8. Testes pedidos por você: quando o desfecho de algo for realmente incerto, em vez de decidir você pode PEDIR um teste ao jogador com "request_check", usando só os testes e dificuldades da seção "Testes que você pode pedir" do pedido (se a seção não existir, nunca peça teste). Vale para descobertas (notar algo escondido, forçar, convencer, lembrar) e para eventos surpresa (armadilha, emboscada, uma flecha vinda do escuro: peça o teste que permite evitar). Ao pedir: narre apenas até o momento do teste e termine convidando o jogador a rolar; não revele nem decida o resultado; não envie proposals; escreva em onSuccess e onFailure (privados) o que acontece em cada caso. Se o teste falhar numa descoberta, o jogador simplesmente não percebe o que estava escondido e a história segue, sem avisar que algo foi perdido. Peça poucos testes, só quando a descoberta ou o risco importar, e não peça outro se a jogada já trouxe um resultado de teste. Se a ficha do jogador listar um traço com "bônus nos testes" (ex.: +2 em esquiva) e o teste for dessa situação, preencha "skill" com o escopo (esquiva, sorte...): o motor soma o bônus sozinho, você não calcula nem cita números.
        9. Disputas e grupos: use request_check com opposed ou group no formato definido no JSON abaixo, incluindo "player" e apenas NPCs vivos da cena. O motor resolve todos os dados; siga só a consequência selecionada e nunca revele o ramo alternativo.
        9. Rolagem com tabela: quando uma falha ou evento precisa de variação (onde a flecha acerta, quanto fere, o que cai do baú), peça com "request_roll", na mesma resposta em que narra o resultado do teste: 1 dado e uma entrada para CADA face, com "text" (privado), "damage" (dano que o motor aplicará; 0 se não fere; faces maiores são mais graves, até a vida máxima do personagem, e só fatais quando a cena justificar) ou, se o contexto listar perfis de perigo, "hazard" com o id do perfil (leve a mortal), deixando o motor calcular o dano e, se couber, "conditions" (status do sistema que o motor aplicará, como sangramento ou veneno; use só ids da lista de condições do pedido). Ao pedir, diga ao jogador o que o dado decide (ex.: "role um d6: quanto maior, pior"), sem revelar a tabela. Depois da rolagem o motor JÁ aplicou o dano e as condições: você só narra, sem números, e nunca propõe isso de novo. Condições ativas continuam valendo: faça-as pesar nas cenas seguintes (visão turva atrapalha mirar, sangramento enfraquece) e proponha remove_condition quando forem tratadas ou passarem. Não encadeie mais de dois pedidos seguidos.
        10. Ritmo: avance a cena com consequências concretas, preserve a agência do jogador e termine decisões importantes perguntando o que ele faz. Não transforme toda ação em teste; peça teste só quando o resultado for incerto e relevante.
        11. Combate: respeite a ordem de iniciativa e os turnos informados pelo motor. Não narre ataque, dano, cura, condição ou morte como ocorridos antes da resolução mecânica. O jogador decide suas ações e não recebe ações extras fora do turno.
        12. Locais: não invente nem revele nomes de passagens desconhecidas. Segredos de lugares seguem a mesma regra dos segredos de NPC: só descreva conteúdo que o motor liberou.
        13. Adaptações: proposta pendente não tem efeito mecânico. Só adaptações aprovadas podem afetar a resolução; nunca as chame de regra oficial.
        14. Limites de conteúdo da campanha são obrigatórios. Não os contorne por metáforas ou eufemismos; redirecione a cena de forma natural.
        15. Agenda e relógios: avance relógios somente quando a ação ou a passagem de tempo justificar progresso. Nunca mostre o nome interno do relógio, a quantidade de casas ou números ao jogador. Quando um relógio completar, narre sinais e consequências no mundo; não fale da mecânica do relógio. Não avance todos os relógios em toda resposta.
        ${ProposalSchema.INSTRUCTIONS}
    """.trimIndent()
}

/** O que o montador de contexto recebe para uma jogada (7.2). */
data class ContextInput(
    val state: GameState,
    val playerEntityId: String,
    val playerText: String,
    val check: CheckResult? = null,
    val healthResource: String? = null,
    val recentNarrative: List<String> = emptyList(),
    val playerSheet: String? = null,
    val ruleSnippets: List<String> = emptyList(),
    val chapterSummary: String? = null,
    /** Texto privado de NPCs da cena (ver DossierBook.contextFor); só o que a cena exige (8.5.1). */
    val npcPrivate: List<String> = emptyList(),
    val checkMenu: String? = null,
    val resolving: Pending? = null,
    val budgetTokens: Int = 5000,
    /** IDs de NPCs realmente presentes na cena, para não vazar estado de toda a campanha. */
    val sceneNpcIds: Set<String> = emptySet(),
    val worldContext: String? = null,
    val generationPreferences: String? = null,
    /** Relógios que avançaram entre a última narração e esta jogada (descanso, viagem, capítulo...); o mestre deve mostrar um sinal. */
    val advancedClockIds: Set<String> = emptySet(),
    /** CP-03: briefing de retomada (ver [ResumeBriefing]); só na primeira jogada depois de abrir a campanha. */
    val resumeBriefing: String? = null,
    /** DC-05.1: trechos do documento-fonte escolhidos pelo código para esta cena (ver [SourceContext]); cortável pelo orçamento. */
    val sourceContext: String? = null
)

object ContextAssembler {
    private fun outcomeWord(o: Outcome) = when (o) {
        Outcome.CRITICAL_FAILURE -> "falha crítica"
        Outcome.FAILURE -> "falha"
        Outcome.PARTIAL_SUCCESS -> "sucesso parcial"
        Outcome.SUCCESS -> "sucesso"
        Outcome.CRITICAL_SUCCESS -> "sucesso crítico"
    }

    internal fun relationWord(v: Int): String = when {
        v >= 70 -> "devoção"
        v >= 30 -> "confiança"
        v >= 10 -> "simpatia"
        v > -10 -> "neutra"
        v > -30 -> "antipatia"
        v > -70 -> "hostilidade"
        else -> "ódio"
    }

    /** Nota privada da face sorteada. O dano, se houver, já foi aplicado pelo motor: a IA não o repete nem cita valores. */
    internal fun rollNote(e: RollEntry): String {
        val applied = buildList {
            if (e.damage > 0 || e.hazard != null) add("o dano")
            if (e.conditions.isNotEmpty()) add("a condição (${e.conditions.joinToString(", ")})")
        }
        val tail = if (applied.isEmpty()) "Esta face não causa dano."
        else "O motor JÁ aplicou ${applied.joinToString(" e ")}: não proponha damage nem add_condition para isso nem cite valores; descreva o ferimento e o efeito só em palavras."
        return "Nota privada para este resultado (siga-a; narre sem números): ${e.text}. $tail"
    }

    /** Só a nota privada que corresponde ao resultado: a outra nunca volta para a IA, para não vazar. */
    internal fun notesFor(req: CheckRequest, o: Outcome): String = when (o) {
        Outcome.SUCCESS, Outcome.CRITICAL_SUCCESS ->
            "Nota privada do seu pedido para este resultado (siga-a; não revele nada além dela): ${req.onSuccess}"
        Outcome.FAILURE, Outcome.CRITICAL_FAILURE ->
            "Nota privada do seu pedido para este resultado (siga-a): ${req.onFailure}. " +
                "Não revele o que o personagem deixou de perceber nem avise que perdeu algo; a história segue."
        Outcome.PARTIAL_SUCCESS ->
            (req.onPartial?.let { "Nota privada para sucesso parcial (siga-a): $it" }
                ?: "Sucesso parcial: o personagem consegue só em parte, com custo ou complicação. " +
                "Se passasse: ${req.onSuccess}. Se falhasse: ${req.onFailure}.")
    }

    /** Estado de saúde só em palavras: nada de números vai para a narração. */
    internal fun healthWord(e: Entity, resource: String?): String {
        if (!e.alive) return "morto"
        val r = resource?.let { e.resources[it] } ?: return "vivo"
        return when {
            r.max == 0 || r.current >= r.max -> "ileso"
            r.current * 2 > r.max -> "ferido"
            else -> "gravemente ferido"
        }
    }

    /**
     * Monta só o necessário. Sempre entram: prompt fixo, ação do jogador e resultado do teste.
     * O resto entra por prioridade enquanto couber no orçamento. Segredos de NPC não fazem parte do estado
     * canônico atual (Fase 8) e só entrariam aqui se a cena exigisse.
     */
    fun build(input: ContextInput): AiRequest {
        val s = input.state
        val resolving = input.resolving
        val required = buildString {
            append(if (resolving == null) "Ação do jogador: " else "Ação do jogador (antes do teste ou rolagem que você pediu): ")
            append(input.playerText.trim())
            val rolled = input.check
            if (rolled != null && resolving is PendingRoll) {
                // Rolagem com tabela: não há sucesso nem falha; a face decide. Só a nota da face sorteada volta à IA.
                val face = rolled.roll.dice.singleOrNull()
                append("\nO jogador rolou '").append(resolving.label).append("' e saiu ").append(face ?: "?").append(". Narre exatamente esse resultado.")
                face?.let { resolving.request.table[it] }?.let { append("\n").append(rollNote(it)) }
            } else if (rolled != null) {
                append("\nResultado decidido pelo motor para '").append(rolled.spec.label).append("': ")
                append(outcomeWord(rolled.outcome)).append(". Narre exatamente esse resultado.")
                if (resolving is PendingCheck) append("\n").append(resolving.resultNote ?: notesFor(resolving.request, rolled.outcome))
            }
        }
        // Uma lista explícita limita a cena aos NPCs presentes. Sem lista, preserva o contexto completo da campanha (incluindo testes e chamadas legadas).
        val sceneIds = (if (input.sceneNpcIds.isEmpty()) s.entities.keys else input.sceneNpcIds) + input.playerEntityId
        val present = s.entities.values.filter { it.id in sceneIds }.joinToString("\n") { e ->
            val conds = s.conditions[e.id].orEmpty()
            "- ${e.id} (${e.name}, ${if (e.id == input.playerEntityId) "jogador" else "NPC"}): ${healthWord(e, input.healthResource)}" +
                if (conds.isEmpty()) "" else ", condições: ${conds.joinToString(", ")}"
        }
        val optional = listOfNotNull(
            "Personagens e estado:\n$present".takeIf { s.entities.isNotEmpty() },
            s.combat?.let { "Combate: rodada ${it.round}, vez de ${s.entities[it.current]?.name ?: it.current}" },
            s.openPromises().filter { it.npcId in sceneIds }.takeIf { it.isNotEmpty() }
                ?.let { ps -> "Promessas em aberto:\n" + ps.joinToString("\n") { "- ${it.npcId}: ${it.text}" } },
            s.relations.filterKeys { key ->
                val pair = key.split('\u0000', limit = 2)
                pair.size == 2 && (pair[0] in sceneIds || pair[1] in sceneIds)
            }.takeIf { it.isNotEmpty() }?.let { r ->
                "Relações (só em palavras, nunca números):\n" + r.entries.joinToString("\n") { (k, v) ->
                    val (a, b) = k.split('\u0000', limit = 2)
                    "- ${s.entities[a]?.name ?: a} em relação a ${s.entities[b]?.name ?: b}: ${relationWord(v)}"
                }
            },
            s.clocks.values.takeIf { it.isNotEmpty() }?.let { cs ->
                "Relógios de agenda (privado; não mostre números, só sinais no mundo):\n" + cs.joinToString("\n") {
                    "- ${it.name}: ${it.filled}/${it.segments}" +
                        if (it.full) " — COMPLETO: narre a consequência" + (if (it.consequence.isNotBlank()) " planejada: ${it.consequence}" else "")
                        else if (it.id in input.advancedClockIds && it.midClue.isNotBlank() && it.filled >= it.midpoint) " — AVANÇOU desde sua última fala e passou da metade: deixe o jogador perceber esta pista, sem números nem o nome do relógio: ${it.midClue}"
                        else if (it.id in input.advancedClockIds) " — AVANÇOU desde sua última fala: mostre um sinal sutil no mundo, sem números nem o nome do relógio"
                        else ""
                }
            },
            s.facts.takeIf { it.isNotEmpty() }
                ?.let { f -> "Fatos registrados:\n" + f.entries.joinToString("\n") { "- ${it.key}: ${it.value}" } },
            s.additionalRules.values.takeIf { it.isNotEmpty() }?.let { rules ->
                "Adaptações da campanha (nunca trate como regra oficial):\n" + rules.joinToString("\n") { rule ->
                    val status = if (rule.approved) "APROVADA — efeito ativo" else "PENDENTE — não aplique ainda"
                    "- ${rule.name} [$status]: ${rule.description}; efeitos ${rule.effects}; limite ${rule.limit}"
                }
            },
            s.knowledge.values.takeIf { it.isNotEmpty() }
                ?.let { k -> "O jogador sabe:\n" + k.joinToString("\n") { "- ${it.text}" + if (it.believed) " (apenas acreditado)" else "" } },
            input.ruleSnippets.takeIf { it.isNotEmpty() }?.let { "Regras relevantes:\n" + it.joinToString("\n") },
            input.chapterSummary?.takeIf { it.isNotBlank() }?.let { "Resumo do capítulo:\n$it" },
            input.sourceContext?.takeIf { it.isNotBlank() },
            input.generationPreferences?.takeIf { it.isNotBlank() },
            input.recentNarrative.takeIf { it.isNotEmpty() }?.let { "Últimas cenas:\n" + it.takeLast(5).joinToString("\n") }
        )
        // Sempre entram (não são cortadas pelo orçamento): ficha, menu, cena e contexto privado relevante.
        val always = listOfNotNull(
            input.playerSheet?.takeIf { it.isNotBlank() },
            input.checkMenu?.takeIf { it.isNotBlank() },
            input.worldContext?.takeIf { it.isNotBlank() },
            input.resumeBriefing?.takeIf { it.isNotBlank() }
        ) + input.npcPrivate
        var used = TokenEstimator.estimate(MasterPrompt.SYSTEM) + TokenEstimator.estimate(required) +
            always.sumOf { TokenEstimator.estimate(it) }
        val kept = mutableListOf<String>()
        for (section in optional) {
            val cost = TokenEstimator.estimate(section)
            if (used + cost <= input.budgetTokens) {
                kept += section
                used += cost
            }
        }
        val user = (always + kept + required).joinToString("\n\n")
        return AiRequest(listOf(AiMessage(Role.SYSTEM, MasterPrompt.SYSTEM), AiMessage(Role.USER, user)))
    }
}
