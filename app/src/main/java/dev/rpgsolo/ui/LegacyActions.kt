package dev.rpgsolo.ui

import dev.rpgsolo.engine.AssumeNpc
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.EndCampaign
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.UseRebirthItem
import dev.rpgsolo.engine.npcSheet
import dev.rpgsolo.rules.RulePackage

/** M-04: um NPC vivo que o jogador pode assumir; [known] é só o que o jogador já descobriu dele (nunca a ficha). */
data class LegacyNpcOption(val id: String, val name: String, val known: List<String>)

/**
 * M-04: o que o jogador pode fazer quando o herói morre. [rebirthItemName] != null = o item de renascimento vale primeiro;
 * sem item, [npcs] lista quem pode ser assumido; sem nenhum dos dois só resta [canEnd]. [endedReason] != null = campanha encerrada.
 */
data class LegacyView(
    val heroName: String,
    val rebirthItemName: String?,
    val npcs: List<LegacyNpcOption>,
    val canEnd: Boolean,
    val endedReason: String?,
)

/** M-04 (legado do próximo herói): renascer com item, assumir um NPC ou encerrar a campanha; o motor valida tudo. */
class LegacyActions(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val playerId: () -> String,
    private val setPlayer: (String) -> Unit,
    private val onChanged: () -> Unit,
) {
    private fun rebirthItem(heroId: String): String? {
        val ids = activePackage()?.rebirthItemIds.orEmpty()
        return session().state.inventory[heroId].orEmpty().filter { (id, qty) -> qty > 0 && id in ids }.keys.minOrNull()
    }

    /** Null enquanto o herói está vivo (o painel some). */
    fun view(): LegacyView? {
        val state = session().state
        val hero = state.entities[playerId()]?.takeIf { it.kind == EntityKind.PLAYER } ?: return null
        if (state.endedReason == null && hero.alive) return null
        val pkg = activePackage()
        val item = rebirthItem(hero.id)
        val npcs =
            state.entities.values.filter { it.kind == EntityKind.NPC && it.alive }.sortedBy { it.name }.mapNotNull { npc ->
                val sheet = state.npcSheet(npc.id) ?: return@mapNotNull null
                LegacyNpcOption(npc.id, npc.name, sheet.discovered)
            }
        return LegacyView(hero.name, item?.let { pkg?.item(it)?.name ?: it }, npcs, item == null && npcs.isEmpty(), state.endedReason)
    }

    private fun run(vararg commands: dev.rpgsolo.engine.Command): String? =
        when (val r = session().executeAll(commands.toList())) {
            is ExecResult.Accepted -> {
                onChanged()
                null
            }
            is ExecResult.Rejected -> r.reason
        }

    /** Usa o item de renascimento do herói morto (consome 1; volta com a fração de PV do pacote). */
    fun useRebirth(): String? = run(UseRebirthItem(playerId()))

    /** O jogador passa a controlar [npcId]; o herói morto vira corpo. O app troca o `playerId` se o motor aceitar. */
    fun assume(npcId: String): String? {
        val refusal = run(AssumeNpc(npcId))
        if (refusal == null) setPlayer(npcId)
        return refusal
    }

    /** Game over: só sem item e sem NPC vivo (o motor recusa o resto). */
    fun endCampaign(): String? = run(EndCampaign("O herói morreu e não há quem continue a jornada."))
}
