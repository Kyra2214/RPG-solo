package dev.rpgsolo.ui

import dev.rpgsolo.engine.AddPlace
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.ConnectPlaces
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.DiscoverPlace
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.Place
import dev.rpgsolo.engine.RecordFact
import dev.rpgsolo.world.CenaInstanciavel

/**
 * Leva para a campanha o que uma cena numa cidade do mundo precisa (MI-18): local, NPCs, facções e os rumores que já
 * chegaram à cidade (MI-19, fatos `mundo.rumor.<eco>@<cidade>`), num lote só.
 * Idempotente: o que já existe na campanha é pulado, então tocar a mesma cidade de novo não cria nada. Facções entram
 * como fatos `mundo.faccao.<id>`; NPCs entram sem ficha mecânica (`rulesValidated = false`).
 */
object InstanciacaoCampanha {
    const val PREFIXO_FACCAO = "mundo.faccao."
    const val PREFIXO_RUMOR = "mundo.rumor."

    /** Chave do fato de um rumor numa cidade: o mesmo Eco pode chegar a cidades diferentes. */
    fun chaveRumor(
        ecoId: String,
        cidadeId: String,
    ) = "$PREFIXO_RUMOR$ecoId@$cidadeId"

    fun comandos(
        cena: CenaInstanciavel,
        state: GameState,
    ): List<Command> {
        val cmds = ArrayList<Command>()
        val id = cena.local.id
        if (id !in state.places) cmds += AddPlace(Place(id, cena.local.nome, cena.local.descricao))
        val atual = state.currentPlaceId
        if (atual != null && atual != id && id !in state.placeLinks[atual].orEmpty()) cmds += ConnectPlaces(atual, id)
        if (id !in state.knownPlaces) cmds += DiscoverPlace(id)
        for (n in cena.npcs) {
            if (n.id !in state.entities) cmds += CreateEntity(Entity(n.id, n.nome, EntityKind.NPC, concept = n.conceito, rulesValidated = false))
        }
        for (f in cena.faccoes) {
            if ((PREFIXO_FACCAO + f.id) !in state.facts) cmds += RecordFact(PREFIXO_FACCAO + f.id, f.resumo)
        }
        for (r in cena.rumores) {
            val chave = chaveRumor(r.ecoId, cena.cidadeId)
            if (chave !in state.facts) cmds += RecordFact(chave, "[versão ${r.versao.rotulo}] ${r.texto}")
        }
        return cmds
    }

    /** Devolve `null` se deu certo (ou se nada faltava) ou o motivo da recusa; a campanha não muda se o lote for recusado. */
    fun instanciar(
        session: CampaignSession,
        cena: CenaInstanciavel,
    ): String? {
        val cmds = comandos(cena, session.state)
        if (cmds.isEmpty()) return null
        return when (val r = session.executeAll(cmds)) {
            is ExecResult.Accepted -> null
            is ExecResult.Rejected -> r.reason
        }
    }
}
