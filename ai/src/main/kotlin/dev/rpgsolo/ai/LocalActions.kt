package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.Heal
import java.text.Normalizer

enum class LocalAction { SHEET, INVENTORY, REST }

sealed interface LocalResult {
    /** Texto para o chat. [changedState] = true se algo foi gravado (então a ação é desfazível). */
    data class Done(val text: String, val changedState: Boolean = false) : LocalResult
    data class Refused(val reason: String) : LocalResult
}

/**
 * Fase 6.4.3: ações simples resolvidas sem IA (ver ficha, inventário, descansar). Só frases curtas e exatas
 * contam; "descanso na taverna e falo com o dono" é narrativa e segue para o mestre.
 * Os números mostrados são só do personagem do jogador (10.1.5).
 */
object LocalActions {
    private val phrases: Map<LocalAction, Set<String>> = mapOf(
        LocalAction.SHEET to setOf("ficha", "ver ficha", "ver a ficha", "minha ficha", "abrir ficha", "status", "ver status"),
        LocalAction.INVENTORY to setOf(
            "inventario", "ver inventario", "ver o inventario", "meu inventario", "abrir inventario", "mochila", "ver mochila"
        ),
        LocalAction.REST to setOf("descansar", "descanso", "descansar um pouco", "fazer um descanso")
    )

    private fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    fun match(text: String): LocalAction? {
        val n = normalize(text)
        return phrases.entries.firstOrNull { n in it.value }?.key
    }

    /**
     * Executa a ação. [restResources] diz o que o descanso recupera; o motor não inventa essa regra:
     * vem do pacote/configuração (padrão do controlador: só o recurso de saúde).
     */
    fun run(
        action: LocalAction,
        session: CampaignSession,
        playerId: String,
        restResources: Set<String> = emptySet()
    ): LocalResult {
        val state = session.state
        val me = state.entities[playerId] ?: return LocalResult.Refused("personagem não encontrado")
        return when (action) {
            LocalAction.SHEET -> LocalResult.Done(buildString {
                append(me.name).append(" — nível ").append(me.level).append(", ").append(me.xp).append(" XP")
                if (!me.alive) append(" (morto)")
                if (me.stats.isNotEmpty()) append("\nAtributos: ").append(me.stats.entries.joinToString { "${it.key} ${it.value}" })
                if (me.resources.isNotEmpty()) {
                    append("\nRecursos: ").append(me.resources.entries.joinToString { "${it.key} ${it.value.current}/${it.value.max}" })
                }
                val conds = state.conditions[playerId].orEmpty()
                if (conds.isNotEmpty()) append("\nCondições: ").append(conds.sorted().joinToString())
            })

            LocalAction.INVENTORY -> {
                val items = state.inventory[playerId].orEmpty().filterValues { it > 0 }
                LocalResult.Done(
                    if (items.isEmpty()) "Inventário vazio."
                    else "Inventário:\n" + items.entries.sortedBy { it.key }.joinToString("\n") { "- ${it.key} x${it.value}" }
                )
            }

            LocalAction.REST -> rest(session, playerId, restResources)
        }
    }

    private fun rest(session: CampaignSession, playerId: String, restResources: Set<String>): LocalResult {
        val state = session.state
        val me = state.entities[playerId] ?: return LocalResult.Refused("personagem não encontrado")
        if (!me.alive) return LocalResult.Refused("um personagem morto não descansa")
        if (state.combat != null) return LocalResult.Refused("não dá para descansar durante o combate")
        if (restResources.isEmpty()) return LocalResult.Refused("este sistema não definiu o que o descanso recupera")
        val heals: List<Command> = restResources.sorted().mapNotNull { res ->
            val r = me.resources[res] ?: return@mapNotNull null
            if (r.current < r.max) Heal(playerId, res, r.max - r.current) else null
        }
        if (heals.isEmpty()) return LocalResult.Done("Você já está descansado.")
        return when (val r = session.executeAll(heals)) {
            is ExecResult.Accepted -> LocalResult.Done("Você descansa e recupera as forças.", changedState = true)
            is ExecResult.Rejected -> LocalResult.Refused(r.reason)
        }
    }
}
