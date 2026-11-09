package dev.rpgsolo.world

/**
 * Leitura por níveis dos deltas (porta de `gerar_niveis` e de `texto` de `relatorio.py`, F8.1/D-M14). [gerar] devolve o
 * texto de `niveis.md`; as funções de apoio servem também ao feed e ao resumo do MI-16.
 */
object ResumoNiveis {
    private val SUC_REGRAS =
        setOf(
            "sucessao-herdeiro", "sucessao-vice", "sucessao-candidato", "diarquia-reduzida", "nomeacao-sem-candidato",
            "nomeacao-de-simulado", "vacancia", "interino-substituido",
        )

    fun importancia(d: Map<String, Any?>): Int = (d["importancia"] as? Number)?.toInt() ?: 0

    fun dia(d: Map<String, Any?>): Int = (d["dia_mundo"] as? Number)?.toInt() ?: 0

    /** Chave de contagem (F9.1): os deltas de uma sucessão no mesmo dia e posto contam como um evento só. */
    fun evento(d: Map<String, Any?>): String {
        val c = d["causa"] as? Map<*, *>
        if (c?.get("regra") !in SUC_REGRAS) return d["id"].toString()
        val e = d["entidade"].toString()
        val ids = (c?.get("ids") as? List<*>).orEmpty()
        val posto =
            when {
                e.startsWith("posto.") -> e
                e.startsWith("city.") || e.startsWith("faction.") -> "posto.$e"
                d["tipo"] == "criacao" && ids.isNotEmpty() -> "posto." + ids[0].toString()
                ids.isNotEmpty() && ids[0].toString().startsWith("posto.") -> ids[0].toString()
                else -> return d["id"].toString()
            }
        return "suc:${dia(d)}:$posto"
    }

    /** Entidades citadas como causa de deltas de importância 2+ e nível 1 ou 2: o nível 3 "necessário para a história". */
    fun citados(
        deltas: List<Map<String, Any?>>,
        nivelDe: (Map<String, Any?>) -> Int,
    ): Set<String> {
        val s = HashSet<String>()
        for (d in deltas) {
            if (importancia(d) >= 2 && nivelDe(d) <= 2) ((d["causa"] as? Map<*, *>)?.get("ids") as? List<*>)?.forEach { s.add(it.toString()) }
        }
        return s
    }

    /** Texto de um delta (`texto` de `relatorio.py`). */
    fun texto(d: Map<String, Any?>): String {
        val t = d["tipo"].toString()
        val n = d["estado_novo"]
        val m = n as? Map<*, *>
        return when {
            t == "era" -> "nova era: ${m?.get("nome")} (${m?.get("regime")})"
            t == "campo" && d["campo"] == "vivo" -> "morte de ${d["entidade"]}"
            t == "campo" -> "${d["entidade"]}.${d["campo"]}: ${PyTexto.str(d["estado_anterior"])} -> ${PyTexto.str(n)}"
            t == "criacao" -> "${d["entidade"]} criada"
            t in Niveis.TIPOS_LACO && m != null -> "$t: ${m["a"]} e ${m["b"]} em ${m["cidade"]}"
            (t == "alianca_de_povos" || t == "fim_de_alianca_de_povos") && m != null ->
                "$t: ${m["a"]} e ${m["b"]}" + ((m["cidades"] as? List<*>)?.takeIf { it.isNotEmpty() }?.let { " (" + it.joinToString(", ") + ")" } ?: "")
            else -> "$t: ${d["entidade"]}"
        }
    }

    /** Contagem ordenada como `Counter.most_common`: mais frequente primeiro, empate na ordem da primeira aparição. */
    fun <K> contar(itens: Iterable<K>): List<Pair<K, Int>> {
        val m = LinkedHashMap<K, Int>()
        for (i in itens) m[i] = (m[i] ?: 0) + 1
        return m.entries.map { it.key to it.value }.sortedByDescending { it.second }
    }

    private fun nomeCidade(w: MundoMolde): Map<String, String> = w.t.getValue("cidade").associate { it.getValue("id") to it.getValue("nome").split(" — ")[0] }

    private fun nomeReino(w: MundoMolde): Map<String, String> = w.reinos.mapValues { it.value.linha.getValue("nome") }

    /** `niveis.md` de [deltas] (cabeçalho [cab] da crônica), idêntico ao de `gerar_niveis`. */
    fun gerar(
        w: MundoMolde,
        deltas: List<Map<String, Any?>>,
        cab: Map<String, Any?>,
    ): String {
        val n = Niveis(w, deltas)
        val ano0 = (cab["ano_base"] as? Number)?.toInt() ?: CalendarioReino.ANO_BASE
        val niv = deltas.associate { it["id"].toString() to n.nivel(it) }
        fun nv(d: Map<String, Any?>) = niv.getValue(d["id"].toString())
        val cit = citados(deltas, ::nv)
        fun dec(d: Map<String, Any?>) = Math.floorDiv(CalendarioReino.anoBase(dia(d)) - ano0, 10)
        val n1 = deltas.filter { nv(it) == 1 && importancia(it) >= 3 && it["entidade"] != "mundo" }
        val n2 = deltas.filter { nv(it) == 2 && importancia(it) >= 2 }
        val n3 = deltas.filter { nv(it) == 3 && importancia(it) >= 2 }
        val eras = deltas.filter { it["tipo"] == "era" }.map { (it["estado_novo"] as Map<*, *>) to dia(it) }
        val ult = deltas.maxOfOrNull { dia(it) } ?: 0
        val nomeCid = nomeCidade(w)
        val nomeRei = nomeReino(w)
        val l = ArrayList<String>()
        l += "# Relatório por níveis de relevância"
        l += ""
        l += "Semente ${cab["semente"]}, anos $ano0 a ${CalendarioReino.anoBase(ult)}. Leitura derivada dos deltas (D-M14); não entra nos hashes."
        l += ""
        l +=
            "**Nível 1:** alianças e rupturas entre nações, raças e sub-raças, e reis (cada evento, abaixo). **Nível 2:** heróis, NPCs secundários, " +
            "facções e laços com mediador (um resumo por década e reino). **Nível 3:** o resto (só entra se for causa citada; senão, contagem por era)."
        l += ""
        for ((i, par) in eras.withIndex()) {
            val (era, ini) = par
            val fim = if (i + 1 < eras.size) eras[i + 1].second else ult + 1
            l += "## ${era["era"]} — ${era["nome"]} (regime ${era["regime"]})"
            l += ""
            l += "### Nível 1 — marcos"
            l += ""
            val ds = n1.filter { dia(it) in ini until fim }
            for (d in ds) {
                val rs = n.reinos(d)
                val c = n.cidade(d)
                val cidade = if (c != null) " / " + (nomeCid[c] ?: c) else ""
                l += "- ano ${CalendarioReino.anoBase(dia(d)) - ano0} [${rs.joinToString(", ") { nomeRei[it] ?: it }}$cidade]: ${texto(d)}"
            }
            if (ds.isEmpty()) l += "- (nenhum)"
            l += ""
            l += "### Nível 2 — resumo por década"
            l += ""
            val porRei = HashMap<Pair<Int, String>, MutableList<Map<String, Any?>>>()
            for (d in n2.filter { dia(it) in ini until fim }) n.reinos(d).take(1).forEach { r -> porRei.getOrPut(dec(d) to r) { ArrayList() }.add(d) }
            for (chave in porRei.keys.sortedWith(compareBy({ it.first }, { it.second }))) {
                val (k, r) = chave
                val unicos = LinkedHashMap<String, Map<String, Any?>>()
                for (d in porRei.getValue(chave).reversed()) unicos[evento(d)] = d
                val ds2 = unicos.values.toList()
                val cs = contar(ds2.mapNotNull { d -> n.cidade(d)?.let { nomeCid[it] ?: "—" } })
                val ts =
                    contar(
                        ds2.map { d ->
                            when {
                                d["tipo"] in Niveis.TIPOS_LACO -> "laços de povos com mediador"
                                d["tipo"] == "campo" && d["campo"] == "vivo" -> "mortes"
                                evento(d).startsWith("suc:") -> "sucessões"
                                d["tipo"] == "criacao" -> "criações"
                                else -> "outros"
                            }
                        },
                    )
                val cidades = cs.take(3).joinToString(", ") { "${it.first} ${it.second}" }.ifEmpty { "—" }
                l += "- anos ${k * 10} a ${k * 10 + 9}, ${nomeRei[r] ?: r}: ${ds2.size} eventos (${ts.joinToString(", ") { "${it.first} ${it.second}" }}); cidades: $cidades."
            }
            l += ""
            l += "### Nível 3 — resumo da virada de era"
            l += ""
            val d3 = n3.filter { dia(it) in ini until fim }
            val tc = contar(d3.map { if (it["tipo"] != "campo") it["tipo"].toString() else "campo:" + PyTexto.str(it["campo"]) })
            l += "${d3.size} eventos de nível 3 (importância 2+): ${tc.take(8).joinToString(", ") { "${it.first} ${it.second}" }.ifEmpty { "nenhum" }}."
            val cd = d3.filter { it["entidade"].toString() in cit }
            l += "Citados como causa de nível 1 ou 2: ${cd.size}."
            for (d in cd.take(20)) l += "- ano ${CalendarioReino.anoBase(dia(d)) - ano0}: ${texto(d)}"
            l += ""
        }
        l += "## Total por cidade"
        l += ""
        l += "| cidade | reino | nível 1 | nível 2 | nível 3 |"
        l += "|---|---|---|---|---|"
        val porC = HashMap<String, IntArray>()
        for ((lista, ix) in listOf(n1 to 0, n2 to 1, n3 to 2)) {
            for (d in lista) n.cidade(d)?.let { c -> porC.getOrPut(c) { IntArray(3) }[ix]++ }
        }
        for (c in w.t.getValue("cidade").map { it.getValue("id") }.sorted()) {
            val v = porC[c] ?: IntArray(3)
            l += "| ${nomeCid.getValue(c)} | ${nomeRei[w.reinoDe(c)] ?: w.reinoDe(c) ?: "—"} | ${v[0]} | ${v[1]} | ${v[2]} |"
        }
        return l.joinToString("\n") + "\n"
    }
}

/** `str()` e `repr()` do Python para os valores que aparecem nos deltas (JSON). */
object PyTexto {
    fun str(v: Any?): String = if (v is String) v else repr(v)

    fun repr(v: Any?): String =
        when (v) {
            null -> "None"
            true -> "True"
            false -> "False"
            is String -> reprTexto(v)
            is Map<*, *> -> v.entries.joinToString(", ", "{", "}") { repr(it.key) + ": " + repr(it.value) }
            is List<*> -> v.joinToString(", ", "[", "]") { repr(it) }
            is Double -> if (v == Math.floor(v) && !v.isInfinite() && Math.abs(v) < 1e16) "${v.toLong()}.0" else v.toString()
            else -> v.toString()
        }

    private fun reprTexto(s: String): String {
        val aspa = if ('\'' in s && '"' !in s) '"' else '\''
        val sb = StringBuilder().append(aspa)
        for (c in s) {
            when {
                c == aspa || c == '\\' -> sb.append('\\').append(c)
                c == '\n' -> sb.append("\\n")
                c == '\t' -> sb.append("\\t")
                c == '\r' -> sb.append("\\r")
                else -> sb.append(c)
            }
        }
        return sb.append(aspa).toString()
    }
}
