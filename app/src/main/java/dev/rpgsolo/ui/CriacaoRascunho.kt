package dev.rpgsolo.ui

import dev.rpgsolo.rules.CharacterDraft
import org.json.JSONArray
import org.json.JSONObject

/**
 * Rascunho salvo da criação por abas (CG-4.8, D-CG-09): um só, na tabela `criacao_rascunho`, em JSON.
 * Guarda o passo, as escolhas das abas e o `CharacterDraft` (status, perícias, vantagens, nome e conceito).
 * ASSUMINDO: um rascunho único por instalação, descartado se for de outro pacote de regras (`pacote`).
 */
internal object CriacaoRascunho {
    private const val VERSAO = 1

    fun serializar(
        pacote: String,
        est: EstadoCriacao,
        draft: CharacterDraft,
    ): String =
        JSONObject()
            .put("v", VERSAO)
            .put("pacote", pacote)
            .put("passo", est.passo.name)
            .put("modoReino", est.modoReino)
            .put("forasteiro", est.forasteiro)
            .put("raceId", est.raceId)
            .put("subraceId", est.subraceId)
            .put("hybridId", est.hybridId)
            .put("originKind", est.originKind)
            .put("originKingdomId", est.originKingdomId)
            .put("originCity", est.originCity)
            .put("classId", est.classId)
            .put("professionId", est.professionId)
            .put("sexo", est.sexo)
            .put("idadeTxt", est.idadeTxt)
            .put("aparencia", est.aparencia)
            .put("personalidade", est.personalidade)
            .put("historia", est.historia)
            .put("mistas", JSONArray(est.mistas.toList()))
            .put("extras", JSONArray(est.classesExtras.toList()))
            .put("evolucoes", JSONObject(est.evolucoes.toMap<String, Any>()))
            .put("nome", draft.name)
            .put("conceito", draft.concept)
            .put("stats", JSONObject(draft.stats().toMap<String, Any>()))
            .put("skills", JSONObject(draft.skills().toMap<String, Any>()))
            .put("traits", JSONArray(draft.traits()))
            .toString()

    /** Rascunho com alguma escolha feita; o estado inicial não vale a pena gravar. */
    fun temConteudo(
        est: EstadoCriacao,
        draft: CharacterDraft,
    ): Boolean =
        est.raceId.isNotBlank() || est.hybridId.isNotBlank() || est.mistas.isNotEmpty() || est.classId.isNotBlank() ||
            est.professionId.isNotBlank() || draft.name.isNotBlank() || draft.traits().isNotEmpty() ||
            est.sexo.isNotBlank() || est.idadeTxt.isNotBlank() || est.aparencia.isNotBlank() ||
            est.personalidade.isNotBlank() || est.historia.isNotBlank()

    /** Devolve `true` se o JSON era legível e do mesmo pacote; as escolhas inválidas ficam para a validação normal. */
    fun restaurar(
        json: String,
        pacote: String,
        est: EstadoCriacao,
        draft: CharacterDraft,
    ): Boolean =
        runCatching {
            val o = JSONObject(json)
            if (o.optInt("v") != VERSAO || o.optString("pacote") != pacote) return false
            est.passo = PassoCriacao.entries.firstOrNull { it.name == o.optString("passo") } ?: PassoCriacao.RACA
            est.modoReino = o.optInt("modoReino")
            est.forasteiro = o.optBoolean("forasteiro", true)
            est.raceId = o.optString("raceId")
            est.subraceId = o.optString("subraceId")
            est.hybridId = o.optString("hybridId")
            est.originKind = o.optString("originKind")
            est.originKingdomId = o.optString("originKingdomId")
            est.originCity = o.optString("originCity")
            est.classId = o.optString("classId")
            est.professionId = o.optString("professionId")
            est.sexo = o.optString("sexo")
            est.idadeTxt = o.optString("idadeTxt")
            est.aparencia = o.optString("aparencia")
            est.personalidade = o.optString("personalidade")
            est.historia = o.optString("historia")
            est.mistas.clear()
            est.mistas.addAll(strings(o.optJSONArray("mistas")))
            est.classesExtras.clear()
            est.classesExtras.addAll(strings(o.optJSONArray("extras")))
            est.evolucoes.clear()
            o.optJSONObject("evolucoes")?.let { e -> e.keys().forEach { k -> est.evolucoes[k] = e.optInt(k) } }
            draft.name = o.optString("nome")
            draft.concept = o.optString("conceito")
            o.optJSONObject("stats")?.let { s -> s.keys().forEach { k -> draft.setStat(k, s.optInt(k)) } }
            o.optJSONObject("skills")?.let { s -> s.keys().forEach { k -> draft.setSkill(k, s.optInt(k)) } }
            strings(o.optJSONArray("traits")).forEach { draft.addTrait(it) }
            est.tick++
            true
        }.getOrDefault(false)

    private fun strings(a: JSONArray?): List<String> = if (a == null) emptyList() else (0 until a.length()).map { a.getString(it) }
}
