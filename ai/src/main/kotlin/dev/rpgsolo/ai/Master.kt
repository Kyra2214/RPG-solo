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
        ${ProposalSchema.INSTRUCTIONS}
    """.trimIndent()
}

/** O que o montador de contexto recebe para uma jogada (7.2). */
data class ContextInput(
    val state: GameState,
    val playerEntityId: String,
    val playerText: String,
    /** Resultado já rolado pelo motor, se a jogada exigiu teste. */
    val check: CheckResult? = null,
    /** Recurso que representa saúde (ex.: "pv"), para descrever estado sem números. */
    val healthResource: String? = null,
    val ruleSnippets: List<String> = emptyList(),
    val chapterSummary: String? = null,
    /** Texto privado de NPCs da cena (ver DossierBook.contextFor); só o que a cena exige (8.5.1). */
    val npcPrivate: List<String> = emptyList(),
    /** Últimas narrações, da mais antiga para a mais nova. */
    val recentNarrative: List<String> = emptyList(),
    /** Orçamento de tokens do pedido inteiro (7.2.4). */
    val budgetTokens: Int = 2000
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
        val required = buildString {
            append("Ação do jogador: ").append(input.playerText.trim())
            input.check?.let {
                append("\nResultado decidido pelo motor para '").append(it.spec.label).append("': ")
                append(outcomeWord(it.outcome)).append(". Narre exatamente esse resultado.")
            }
        }
        val present = s.entities.values.joinToString("\n") { e ->
            val conds = s.conditions[e.id].orEmpty()
            "- ${e.id} (${e.name}, ${if (e.id == input.playerEntityId) "jogador" else "NPC"}): ${healthWord(e, input.healthResource)}" +
                if (conds.isEmpty()) "" else ", condições: ${conds.joinToString(", ")}"
        }
        val optional = listOfNotNull(
            "Personagens e estado:\n$present".takeIf { s.entities.isNotEmpty() },
            s.combat?.let { "Combate: rodada ${it.round}, vez de ${it.current}" },
            s.openPromises().takeIf { it.isNotEmpty() }
                ?.let { ps -> "Promessas em aberto:\n" + ps.joinToString("\n") { "- ${it.npcId}: ${it.text}" } },
            s.relations.takeIf { it.isNotEmpty() }?.let { r ->
                "Relações (só em palavras, nunca números):\n" + r.entries.joinToString("\n") { (k, v) ->
                    val (a, b) = k.split(">", limit = 2)
                    "- ${s.entities[a]?.name ?: a} em relação a ${s.entities[b]?.name ?: b}: ${relationWord(v)}"
                }
            },
            s.clocks.values.takeIf { it.isNotEmpty() }?.let { cs ->
                "Relógios de agenda (privado; não mostre números, só sinais no mundo):\n" + cs.joinToString("\n") {
                    "- ${it.name}: ${it.filled}/${it.segments}" + if (it.full) " — COMPLETO: narre a consequência" else ""
                }
            },
            s.facts.takeIf { it.isNotEmpty() }
                ?.let { f -> "Fatos registrados:\n" + f.entries.joinToString("\n") { "- ${it.key}: ${it.value}" } },
            s.knowledge.values.takeIf { it.isNotEmpty() }
                ?.let { k -> "O jogador sabe:\n" + k.joinToString("\n") { "- ${it.text}" + if (it.believed) " (apenas acreditado)" else "" } },
            input.npcPrivate.takeIf { it.isNotEmpty() }?.let { "Notas privadas do mestre (nunca mostrar ao jogador):\n" + it.joinToString("\n") },
            input.ruleSnippets.takeIf { it.isNotEmpty() }?.let { "Regras relevantes:\n" + it.joinToString("\n") },
            input.chapterSummary?.takeIf { it.isNotBlank() }?.let { "Resumo do capítulo:\n$it" },
            input.recentNarrative.takeIf { it.isNotEmpty() }?.let { "Últimas cenas:\n" + it.takeLast(5).joinToString("\n") }
        )
        var used = TokenEstimator.estimate(MasterPrompt.SYSTEM) + TokenEstimator.estimate(required)
        val kept = mutableListOf<String>()
        for (section in optional) {
            val cost = TokenEstimator.estimate(section)
            if (used + cost <= input.budgetTokens) {
                kept += section
                used += cost
            }
        }
        val user = (kept + required).joinToString("\n\n")
        return AiRequest(listOf(AiMessage(Role.SYSTEM, MasterPrompt.SYSTEM), AiMessage(Role.USER, user)))
    }
}
