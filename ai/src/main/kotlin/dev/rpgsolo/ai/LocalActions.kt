package dev.rpgsolo.ai

import dev.rpgsolo.engine.AdvanceClock
import dev.rpgsolo.engine.AdvanceDays
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.Heal
import dev.rpgsolo.engine.ResetTraitUses
import java.text.Normalizer

enum class LocalAction { SHEET, INVENTORY, REST, INN_REST }

/**
 * N-33.5: descanso comum recupera [basePercent]% do máximo de [partial] (PV e PM); o acampamento soma [campPercentPerGrade]%
 * por grau de Construção ([campGrade], 0 a 5; sem grau 1 não há acampamento). Estalagem/cidade ([LocalAction.INN_REST]) recupera 100%.
 * Sem [RestRules] o descanso segue integral (sistemas que não definem a regra).
 */
data class RestRules(
    val partial: Set<String>,
    val basePercent: Int = 50,
    val campPercentPerGrade: Int = 10,
    val campGrade: (Entity) -> Int = { 0 },
    /** N-33.2c: saldo do herói em prata e pagamento (comandos que consomem o total; null = sem saldo) da manutenção do criadouro. */
    val kennelBudget: () -> Int = { 0 },
    val kennelPay: (Int) -> List<Command>? = { if (it == 0) emptyList() else null },
    /** N-33.3: grau de Criador de Animais do herói (0 a 5): lealdade semanal de todos os animais. */
    val breederDegree: () -> Int = { 0 },
) {
    fun percent(entity: Entity): Int = (basePercent + campPercentPerGrade * campGrade(entity).coerceIn(0, 5)).coerceIn(0, 100)
}

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
    private val phrases: Map<LocalAction, Set<String>> =
        mapOf(
            LocalAction.SHEET to setOf("ficha", "ver ficha", "ver a ficha", "minha ficha", "abrir ficha", "status", "ver status"),
            LocalAction.INVENTORY to
                setOf(
                    "inventario", "ver inventario", "ver o inventario", "meu inventario", "abrir inventario", "mochila", "ver mochila",
                ),
            LocalAction.REST to setOf("descansar", "descanso", "descansar um pouco", "fazer um descanso"),
            LocalAction.INN_REST to setOf("dormir na estalagem", "descansar na estalagem", "pousar", "dormir na pousada", "descansar na pousada"),
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
        restResources: Set<String> = emptySet(),
        restRules: RestRules? = null,
    ): LocalResult {
        val state = session.state
        val me = state.entities[playerId] ?: return LocalResult.Refused("personagem não encontrado")
        return when (action) {
            LocalAction.SHEET ->
                LocalResult.Done(
                    buildString {
                        append(me.name).append(" — nível ").append(me.level).append(", ").append(me.xp).append(" XP")
                        if (!me.alive) append(" (morto)")
                        if (me.stats.isNotEmpty()) append("\nAtributos: ").append(me.stats.entries.joinToString { "${it.key} ${it.value}" })
                        if (me.resources.isNotEmpty()) {
                            append(
                                "\nRecursos: ",
                            ).append(me.resources.entries.joinToString { "${it.key} ${it.value.current}/${it.value.max}" })
                        }
                        val conds = state.conditions[playerId].orEmpty()
                        if (conds.isNotEmpty()) append("\nCondições: ").append(conds.sorted().joinToString())
                    },
                )

            LocalAction.INVENTORY -> {
                val items = state.inventory[playerId].orEmpty().filterValues { it > 0 }
                LocalResult.Done(
                    if (items.isEmpty()) {
                        "Inventário vazio."
                    } else {
                        "Inventário:\n" + items.entries.sortedBy { it.key }.joinToString("\n") { "- ${it.key} x${it.value}" }
                    },
                )
            }

            LocalAction.REST -> rest(session, playerId, restResources, restRules, inn = false)
            LocalAction.INN_REST -> rest(session, playerId, restResources, restRules, inn = true)
        }
    }

    /** Linhas de chat sobre a cobrança do criadouro (só o que o jogador precisa saber). */
    private fun kennelNotes(
        state: dev.rpgsolo.engine.GameState,
        kennel: KennelSettlement.Result?,
    ): List<String> {
        if (kennel == null) return emptyList()
        val notes = mutableListOf<String>()
        if (kennel.silver > 0) notes += "Criadouro: ${kennel.silver} de prata pagos pela manutenção dos animais guardados."
        val unpaid = kennel.weeks.flatMap { it.changes }.filter { !it.paid }.map { state.entities[it.animalId]?.name ?: it.animalId }.distinct()
        if (unpaid.isNotEmpty()) notes += "Sem prata para a manutenção: ${unpaid.joinToString()} perde(m) lealdade."
        val lost = kennel.weeks.flatMap { it.lost }.map { state.entities[it]?.name ?: it }.distinct()
        if (lost.isNotEmpty()) notes += "Animal perdido: ${lost.joinToString()} deixou de ser seu companheiro."
        return notes
    }

    private fun rest(
        session: CampaignSession,
        playerId: String,
        restResources: Set<String>,
        restRules: RestRules?,
        inn: Boolean,
    ): LocalResult {
        val state = session.state
        val me = state.entities[playerId] ?: return LocalResult.Refused("personagem não encontrado")
        if (!me.alive) return LocalResult.Refused("um personagem morto não descansa")
        if (state.combat != null) return LocalResult.Refused("não dá para descansar durante o combate")
        if (restResources.isEmpty()) return LocalResult.Refused("este sistema não definiu o que o descanso recupera")
        val percent = if (inn || restRules == null) 100 else restRules.percent(me)
        val heals: List<Command> =
            restResources.sorted().mapNotNull { res ->
                val r = me.resources[res] ?: return@mapNotNull null
                val missing = r.max - r.current
                val amount =
                    if (percent >= 100 || restRules == null || res !in restRules.partial) {
                        missing
                    } else {
                        minOf(missing, (r.max * percent + 99) / 100)
                    }
                if (amount > 0) Heal(playerId, res, amount) else null
            }
        // M-07: o tempo passa no descanso; relógios com restTick avançam pelo motor, sem passar pela IA.
        val ticks = state.clocks.values.filter { it.restTick > 0 && !it.full }
        val advances: List<Command> = ticks.map { AdvanceClock(it.id, minOf(it.restTick, it.segments - it.filled)) }
        // M-01: o descanso devolve os usos gastos de traços com usos limitados.
        val resets: List<Command> = if (state.traitUses[playerId].orEmpty().isNotEmpty()) listOf(ResetTraitUses(playerId)) else emptyList()
        if (heals.isEmpty() && advances.isEmpty() && resets.isEmpty()) return LocalResult.Done("Você já está descansado.")
        // N-33.2c: se uma semana vira, o criadouro cobra (prata do herói) e a lealdade dos animais guardados muda.
        val kennel =
            if (restRules != null && state.companions.isNotEmpty()) {
                KennelSettlement.after(
                    state,
                    1,
                    restRules.kennelBudget(),
                    breederDegree = restRules.breederDegree(),
                    pay = restRules.kennelPay,
                )
            } else {
                null
            }
        // N-18: descanso completo avança 1 dia (pergaminhos de elemento vencem).
        return when (val r = session.executeAll(heals + advances + resets + AdvanceDays(1) + kennel?.commands.orEmpty())) {
            is ExecResult.Accepted -> {
                val kennelNotes = kennelNotes(state, kennel)
                // Só a pista pública de quem completou aparece; nome interno e contagem nunca.
                val clues =
                    ticks.mapNotNull {
                        val now = minOf(it.segments, it.filled + it.restTick)
                        when {
                            now >= it.segments -> it.publicClue.takeIf { c -> c.isNotBlank() }
                            it.filled < it.midpoint && now >= it.midpoint -> it.midClue.takeIf { c -> c.isNotBlank() }
                            else -> null
                        }
                    }
                val camp = restRules != null && !inn && restRules.campGrade(me) > 0
                val base =
                    when {
                        heals.isEmpty() -> "Você descansa; o tempo passa."
                        camp -> "Você monta acampamento, descansa e recupera $percent% das forças."
                        restRules != null && percent < 100 -> "Você descansa ao relento e recupera $percent% das forças."
                        else -> "Você descansa e recupera as forças."
                    }
                LocalResult.Done((listOf(base) + clues + kennelNotes).joinToString("\n"), changedState = true)
            }
            is ExecResult.Rejected -> LocalResult.Refused(r.reason)
        }
    }
}
