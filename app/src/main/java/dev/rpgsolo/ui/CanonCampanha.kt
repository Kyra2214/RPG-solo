package dev.rpgsolo.ui

import dev.rpgsolo.engine.CanonFact
import dev.rpgsolo.engine.FactStatus
import dev.rpgsolo.world.FatoCanonMundo

/**
 * Liga o cânone do mundo (MI-21) aos `CanonFact` da campanha: cada fato vira ORIGINAL com `sourceId` = `mundo:<mundo>` e
 * `chunkId` = o delta da crônica que o estabeleceu (ou `molde`). Idempotente (mesmo id não duplica).
 * 🔶 ASSUMINDO: DIVERGENCE só por [divergir] (ninguém o chama sozinho ainda) e AI_CREATED não é gravado nesta etapa.
 */
object CanonCampanha {
    fun idFato(chave: String) = "mundo:$chave"

    fun sincronizar(
        model: AppModel,
        campaignId: String,
        mundoId: String,
        fatos: List<FatoCanonMundo>,
    ) {
        var atual = model.canonFacts(campaignId)
        var mudou = false
        for (f in fatos) {
            val id = idFato(f.chave)
            if (atual.get(id) != null) continue
            val novo =
                CanonFact(
                    id,
                    campaignId,
                    f.texto,
                    FactStatus.ORIGINAL,
                    sourceId = "mundo:$mundoId",
                    chunkId = f.deltaId ?: "molde",
                    protagonistKnows = false,
                )
            atual.add(novo)?.let {
                atual = it
                mudou = true
            }
        }
        if (mudou) model.saveCanonFacts(campaignId, atual)
    }

    /** Marca o fato de [chave] como divergência (a campanha deixou de seguir o mundo); `null` se o fato não existe. */
    fun divergir(
        model: AppModel,
        campaignId: String,
        chave: String,
    ): String? {
        val novo = model.canonFacts(campaignId).setStatus(idFato(chave), FactStatus.DIVERGENCE) ?: return "fato de cânone inexistente: $chave"
        return model.saveCanonFacts(campaignId, novo)
    }
}
