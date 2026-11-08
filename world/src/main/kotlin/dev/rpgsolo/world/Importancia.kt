package dev.rpgsolo.world

/** Importância dos deltas (0 a 4) por regra, nunca por sorteio (porta de `importancia.py`). 3 ou mais notifica. */
object Importancia {
    const val LIMIAR_NOTIFICACAO = 3

    private val TIPO_REGISTRO: Map<String, Int> =
        mapOf(
            "incidente_fronteira" to 3, "quebra_de_contrato" to 3, "tratado" to 3, "ruptura" to 3, "cisma" to 3, "fusao" to 3,
            "extincao" to 3, "bloqueio_de_rota" to 3, "revolta" to 3, "pista_de_misterio" to 3, "tentativa_de_resolver_misterio" to 3,
            "alianca_de_povos" to 3, "fim_de_alianca_de_povos" to 3, "laco_aliado" to 2, "laco_rompido" to 2, "laco_rival" to 2,
            "guerra" to 4, "queda_de_coroa" to 4, "fato_confirmado_alterado" to 4,
            "disputa_aberta" to 3, "evento_ocorrido" to 3, "fim_de_cadeia" to 3, "passo_de_cadeia" to 2,
            "consequencia_sem_agente" to 2, "rota_reaberta" to 2, "tregua" to 2, "tensao_pos_ruptura" to 1, "boato" to 1,
        )
    private val CAMPOS_DE_CARGO = setOf("governante", "lideranca", "titular")
    private val CARGOS_DE_PODER = setOf("governante", "general", "herdeiro")

    private fun papeis(e: Map<String, Any?>): List<String> = (e["papeis"]?.toString() ?: "").split(';')

    /** `d`: delta ainda sem importância; `estado`: Estado antes de aplicar o delta. */
    fun calcular(
        d: Map<String, Any?>,
        estado: Estado,
    ): Int {
        val t = d["tipo"] as String
        if (t == "era") return 4
        if (t == "criacao") {
            @Suppress("UNCHECKED_CAST")
            val n = (d["estado_novo"] as? Map<String, Any?>) ?: emptyMap()
            val poder = papeis(n).any { it in CARGOS_DE_PODER } || n["cargo"] in CAMPOS_DE_CARGO
            return if (poder) 3 else 1
        }
        if (t != "campo") return TIPO_REGISTRO[t] ?: 2
        val c = d["campo"]
        val entidade = d["entidade"].toString()
        val ent = estado.ent[entidade] ?: emptyMap()
        val novo = d["estado_novo"]
        if (c == "vivo" && novo == false) {
            if ("governante" in papeis(ent)) return 4
            return if (Valores.verdadeiro(ent["principal"])) 3 else 2
        }
        if (c in CAMPOS_DE_CARGO) return 3
        if (c == "vago_desde" && novo != null) return 3
        if (c == "relacao" && entidade.startsWith("par.")) return 3
        if (c == "saude" && novo == "doente") return if (Valores.verdadeiro(ent["principal"])) 3 else 2
        if (c == "estado" && entidade.startsWith("event.")) return if (novo == "em_cadeia") 3 else 2
        if (c == "escassez" && entidade.startsWith("econ.")) return if (Valores.numero(novo) >= 3) 2 else 1
        if (c == "difusao") return 0
        if (c == "tensao" && entidade.startsWith("par.")) return 1
        if (c == "estourado" && novo == true) return 3
        if (c == "progresso") return if (Valores.numero(novo) == Valores.numero(ent["segmentos"] ?: 0) - 1) 3 else 1
        if (c == "estado" && entidade.startsWith("fact.") && d["estado_anterior"] == "confirmado") return 4
        return 1
    }
}
