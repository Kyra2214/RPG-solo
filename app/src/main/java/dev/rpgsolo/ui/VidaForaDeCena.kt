package dev.rpgsolo.ui

import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.MarkNpcDead
import dev.rpgsolo.engine.RecordFact

/**
 * Vida fora de cena (MI-22): o NPC que já existe na campanha e morreu na simulação do mundo aparece morto na campanha.
 * Compara os NPCs `npc.*` vivos da campanha com o estado do mundo (`ent.<id>.vivo == false`) e gera `MarkNpcDead` mais o
 * fato `mundo.npc.<id>.morte`. Idempotente: quem já está morto na campanha é pulado.
 * 🔶 ASSUMINDO: só a morte de NPCs que já foram instanciados (o resto entra vivo ou morto pelo cânone, MI-21); a memória
 * entre campanhas (MP-03) e outras mudanças fora de cena (mudar de cidade, de cargo) ficam para depois.
 */
object VidaForaDeCena {
    const val PREFIXO_MORTE = "mundo.npc."

    @Suppress("UNCHECKED_CAST")
    fun comandos(
        state: GameState,
        estadoMundo: Map<String, Any?>?,
    ): List<Command> {
        val ent = (estadoMundo?.get("ent") as? Map<String, Map<String, Any?>>) ?: return emptyList()
        val out = ArrayList<Command>()
        for ((id, e) in state.entities.entries.sortedBy { it.key }) {
            if (e.kind != EntityKind.NPC || !e.alive || !id.startsWith("npc.")) continue
            if (ent[id]?.get("vivo") != false) continue
            out += MarkNpcDead(id, "mundo:simulação")
            out += RecordFact("$PREFIXO_MORTE$id.morte", "morreu fora de cena, na simulação do mundo")
        }
        return out
    }
}
