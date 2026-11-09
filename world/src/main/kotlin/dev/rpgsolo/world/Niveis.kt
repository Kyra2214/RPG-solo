package dev.rpgsolo.world

/** Reino de qualquer id de delta: molde, entidade criada pela simulação, econ, par, posto e Eco (porta de `Reinos` de `relatorio.py`). */
class ReinosMapa(private val w: MundoMolde, deltas: List<Map<String, Any?>>) {
    val m = HashMap<String, String>()
    val eco = HashMap<String, String>()

    init {
        for (d in deltas) registrar(d)
    }

    /** Aprende o reino de uma entidade criada (`criacao` com `reino` no estado novo). */
    fun registrar(d: Map<String, Any?>) {
        if (d["tipo"] == "criacao") {
            val n = d["estado_novo"] as? Map<*, *>
            val r = n?.get("reino")
            if (r != null && r != "") m[d["entidade"].toString()] = r.toString()
        }
    }

    fun de(
        i: String,
        n: Int = 0,
    ): String {
        m[i]?.let { return it }
        if (i.startsWith("crown.")) return "kingdom." + i.substring(6)
        if (i.startsWith("front.")) return "kingdom." + i.substring(6).split('+')[0]
        if (i.startsWith("eco.")) return eco[i] ?: "—"
        if (i.startsWith("laco.")) {
            m[i] = w.reinoDe(i.substring(5).split('@')[0]) ?: "—"
            return m.getValue(i)
        }
        val r: String? =
            if (i.startsWith("econ.") || i.startsWith("posto.") || i.startsWith("par.")) {
                val base = if (i.startsWith("econ.")) i.split('@').last() else i.split('.', limit = 2)[1].split('+')[0]
                if (n < 3) de(base, n + 1) else null
            } else {
                w.reinoDe(i)
            }
        m[i] = if (r.isNullOrEmpty()) "—" else r
        return m.getValue(i)
    }
}

/** Níveis de relevância (porta de `niveis.py`, D-M14): leitura derivada dos deltas, nada vai para a crônica nem para os hashes. */
class Niveis(
    private val w: MundoMolde,
    deltas: List<Map<String, Any?>> = emptyList(),
    private val estado: Estado? = null,
    reinos: ReinosMapa? = null,
) {
    private val r: ReinosMapa = reinos ?: ReinosMapa(w, deltas)
    private val capitais: Set<String> = w.t.getValue("reino").map { it.getValue("capital") }.toSet()
    private val papeis = HashMap<String, Set<String>>()
    private val npcCidade = HashMap<String, String?>()

    init {
        for (n in w.t.getValue("npc")) {
            papeis[n.getValue("id")] = n.getValue("papeis").split(';').toSet() + (if (n.getValue("principal") == "sim") setOf("principal") else emptySet())
            npcCidade[n.getValue("id")] = n.getValue("cidade")
        }
        for (d in deltas) ver(d)
    }

    /** Aprende, para a crônica que chega aos poucos (MI-16), o reino e o papel das entidades criadas em [d]. */
    fun aprender(d: Map<String, Any?>) {
        r.registrar(d)
        ver(d)
    }

    /** Aprende NPCs simulados (`criacao`) à medida que a crônica avança. */
    fun ver(d: Map<String, Any?>) {
        val e = d["entidade"].toString()
        val n = d["estado_novo"] as? Map<*, *>
        if (d["tipo"] == "criacao" && e.startsWith("npc.") && n != null) {
            papeis[e] = (n["papeis"]?.toString() ?: "").split(';').toSet() + (if (Valores.verdadeiro(n["principal"]) || (n["principal"] != null && n["principal"] != false && n["principal"] != "")) setOf("principal") else emptySet())
            npcCidade[e] = (n["cidade"] ?: "—").toString()
        }
    }

    private fun npc(e: String): Pair<Set<String>, String?> {
        papeis[e]?.let { return it to npcCidade[e] }
        val x = estado?.ent?.get(e) ?: emptyMap()
        val p = (x["papeis"]?.toString() ?: "").split(';').toSet() + (if (x["principal"] != null && x["principal"] != false && x["principal"] != "") setOf("principal") else emptySet())
        return p to x["cidade"]?.toString()
    }

    fun cidade(d: Map<String, Any?>): String? {
        val e = d["entidade"].toString()
        if (e.startsWith("laco.")) return e.substring(5).split('@')[0]
        if (e.startsWith("city.")) return e
        if (e.startsWith("posto.city.")) return e.substring(6)
        if (e.startsWith("npc.")) return npc(e).second?.takeIf { it.startsWith("city.") }
        val n = d["estado_novo"] as? Map<*, *>
        if (e.startsWith("pacto.") && n != null && (n["cidades"] as? List<*>)?.isNotEmpty() == true) return (n["cidades"] as List<*>)[0].toString()
        if (e.startsWith("econ.") && "@city." in e) return e.split('@')[1]
        return null
    }

    fun reinos(d: Map<String, Any?>): List<String> {
        val e = d["entidade"].toString()
        val qs = (d["quem_sabe"] as? List<*>)?.map { it.toString() }?.filter { it in w.reinos } ?: emptyList()
        if (e in w.reinos) return listOf(e)
        if (e.startsWith("pacto.")) return qs.ifEmpty { listOf("—") }
        val c = cidade(d)
        val rr = (if (c != null) w.reinoDe(c) else null) ?: r.de(e)
        if (rr.isNotEmpty() && rr != "—") return listOf(rr)
        return if (!(e.startsWith("front.") || e.startsWith("crown."))) listOf("—") else qs.ifEmpty { listOf("—") }
    }

    private fun mediador(d: Map<String, Any?>): Boolean =
        (d["agentes"] as? List<*>)?.any { a -> npc(a.toString()).first.any { it in PAPEIS_N2 } } == true

    fun nivel(d: Map<String, Any?>): Int {
        val t = d["tipo"].toString()
        val e = d["entidade"].toString()
        val c = d["campo"]
        if (t in TIPOS_N1 || e.startsWith("front.") || e.startsWith("crown.") || e.startsWith("pacto.")) return 1
        if (t in TIPOS_LACO || e.startsWith("laco.")) return if (mediador(d)) 2 else 3
        if (e.startsWith("npc.")) {
            val (p, cn) = npc(e)
            if (p.any { it in PAPEIS_N1 } && cn != null && cn in capitais) return 1
            if (GOVERNANTE_DE_CIDADE_N1 && "governante" in p && !cn.isNullOrEmpty()) return 1
            return if (p.any { it in PAPEIS_N1 || it in PAPEIS_N2 }) 2 else 3
        }
        val cid = cidade(d)
        if ((e.startsWith("city.") || e.startsWith("posto.city.")) && c in setOf("governante", "titular", "vago_desde", "interino")) {
            return if ((cid != null && cid in capitais) || GOVERNANTE_DE_CIDADE_N1) 1 else 2
        }
        if ((e.startsWith("faction.") || e.startsWith("posto.faction.")) && c in setOf("lideranca", "titular", "vago_desde", "interino")) return 2
        if (e.startsWith("par.") && t in setOf("tratado", "ruptura", "disputa_aberta", "campo")) return 2
        return 3
    }

    companion object {
        val TIPOS_N1 = setOf("era", "guerra", "queda_de_coroa", "fato_confirmado_alterado", "alianca_de_povos", "fim_de_alianca_de_povos")
        val TIPOS_LACO = setOf("laco_aliado", "laco_rompido", "laco_rival")
        val PAPEIS_N1 = setOf("governante", "herdeiro")
        val PAPEIS_N2 = setOf("heroi", "guardiao", "benfeitor", "general", "principal")
        const val GOVERNANTE_DE_CIDADE_N1 = false
    }
}
