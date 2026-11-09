package dev.rpgsolo.world

class ErroEstado(mensagem: String) : Exception(mensagem)

/** O que o [Estado] lê da dinâmica ao nascer do molde (a configuração completa e o carregador são MI-05). */
interface DinamicaEstado {
    val vazia: Boolean
    val lim: Map<String, Int>
    val econ: Map<String, Int>
    val mist: Map<String, Int>
    val front: Map<String, Int>
    val povos: Map<String, Int>

    fun classeDe(
        povo: String,
        subraca: String,
    ): String

    fun nascDia(
        npcId: String,
        idadeTxt: String,
        classe: String,
    ): Int?
}

/**
 * Estado mutável de Mythralis (porta de `estado.py`). Nunca é a verdade: é o que sobra de aplicar a crônica ao molde.
 * Tipos mecânicos de delta: `campo`, `criacao` e `era`; qualquer outro tipo é só registro.
 */
class Estado {
    var proximoDia = 0
    val ent = LinkedHashMap<String, MutableMap<String, Any?>>()
    val eras = LinkedHashMap<String, MutableMap<String, Any?>>()
    var mundo: MutableMap<String, Any?> = linkedMapOf("era" to null, "regime" to null)
    val ecosAtivos = LinkedHashSet<String>()
    var nCriados = 0

    fun existe(i: String) = i in ent

    fun vivo(i: String): Boolean = ent[i]?.let { (if (it.containsKey("vivo")) it["vivo"] else true) == true } ?: false

    fun morto(i: String): Boolean = ent[i]?.let { it.containsKey("vivo") && it["vivo"] == false } ?: false

    fun aplicar(d: Map<String, Any?>) {
        val t = d["tipo"]
        when (t) {
            "campo" -> {
                val id = d["entidade"].toString()
                val e = ent[id] ?: throw ErroEstado("entidade inexistente: $id")
                val campo = d["campo"].toString()
                val atual = e[campo]
                if (!Valores.iguais(atual, d["estado_anterior"])) {
                    throw ErroEstado("estado_anterior não bate em $id.$campo: esperado ${d["estado_anterior"]}, atual $atual")
                }
                e[campo] = d["estado_novo"]
            }
            "criacao" -> {
                val id = d["entidade"].toString()
                if (id in ent) throw ErroEstado("entidade já existe: $id")

                @Suppress("UNCHECKED_CAST")
                ent[id] = Valores.copiar(d["estado_novo"]) as MutableMap<String, Any?>
                nCriados++
            }
            "era" -> {
                @Suppress("UNCHECKED_CAST")
                val n = d["estado_novo"] as Map<String, Any?>
                if (!Valores.iguais(linkedMapOf("era" to mundo["era"], "regime" to mundo["regime"]), d["estado_anterior"])) {
                    throw ErroEstado("estado_anterior da era não bate")
                }
                eras[n["era"].toString()] = linkedMapOf("nome" to n["nome"], "regime" to n["regime"], "dia_inicio" to d["dia_mundo"])
                mundo = linkedMapOf("era" to n["era"], "regime" to n["regime"])
            }
        }
        val entidade = d["entidade"].toString()
        if (t == "campo" && entidade.startsWith("eco.") && d["campo"] == "difusao") {
            @Suppress("UNCHECKED_CAST")
            val ativo = (d["estado_novo"] as Map<String, Any?>)["ativo"] == true
            if (ativo) ecosAtivos.add(entidade) else ecosAtivos.remove(entidade)
        } else if (((d["importancia"] as? Number)?.toInt() ?: 0) >= 3 && t != "era" && !entidade.startsWith("eco.")) {
            nasceEco(d)
        }
    }

    private fun lugar(
        i: String,
        n: Int = 0,
    ): String? {
        val e = ent[i]
        if (i.startsWith("city.") || i.startsWith("settlement.")) return i
        if (e == null || n > 3) return null
        for (c in listOf("localidade", "de", "cidade")) {
            val v = e[c]?.toString() ?: ""
            if (v.startsWith("city.") || v.startsWith("settlement.")) return v
        }
        val alvo = e["alvo"]
        if (alvo != null && alvo != "") return lugar(alvo.toString(), n + 1)
        val reino = e["reino"]
        return ent.keys.sortedWith(Ordem.CODIGO).firstOrNull { it.startsWith("city.") && Valores.iguais(ent.getValue(it)["reino"], reino) }
    }

    private fun nasceEco(d: Map<String, Any?>) {
        val entidade = d["entidade"].toString()
        val lugar = lugar(entidade) ?: return
        val tipo = d["tipo"].toString()
        val importancia = (d["importancia"] as Number).toInt()
        val camada =
            when {
                tipo == "pista_de_misterio" || tipo == "tentativa_de_resolver_misterio" -> "restrito"
                importancia >= 4 || tipo in PUBLICOS -> "publico"
                else -> "local"
            }
        val k = "eco.${d["id"]}"

        fun reinoDe(x: String): Any? = ent[x]?.get("reino")?.takeIf { it != "" }
        ent[k] =
            linkedMapOf(
                "reino" to (reinoDe(entidade) ?: reinoDe(lugar)),
                "origem_delta" to d["id"],
                "tipo" to tipo,
                "entidade" to entidade,
                "camada" to camada,
                "inicio" to d["dia_mundo"],
                "difusao" to linkedMapOf("passo" to 0, "lugares" to mutableListOf<Any?>(lugar), "certeza" to d["certeza"], "ativo" to true),
            )
        ecosAtivos.add(k)
    }

    fun estadoAnteriorDe(
        tipo: String?,
        entidade: String?,
        campo: String?,
    ): Any? =
        when (tipo) {
            "campo" -> ent[entidade]?.get(campo)
            "era" -> linkedMapOf("era" to mundo["era"], "regime" to mundo["regime"])
            else -> null
        }

    fun comoMapa(): Map<String, Any?> =
        linkedMapOf(
            "proximo_dia" to proximoDia,
            "mundo" to mundo,
            "eras" to eras,
            "n_criados" to nCriados,
            "ent" to ent,
        )

    /** Hash do estado, sem `proximo_dia` (o dia avança mesmo sem delta; o estado é o que mudou). */
    fun hash(): String = Hash.sha256(JsonCanonico.canon(comoMapa().filterKeys { it != "proximo_dia" }))

    companion object {
        /** Restaura um estado de um snapshot (`comoMapa`, vindo de `JsonLeitor`): D-I3, retomada sem refazer a crônica. */
        @Suppress("UNCHECKED_CAST")
        fun doSnapshot(m: Map<String, Any?>): Estado {
            val e = Estado()
            e.proximoDia = Valores.numero(m["proximo_dia"]).toInt()
            e.mundo = Valores.copiar(m["mundo"]) as MutableMap<String, Any?>
            for ((k, v) in m["eras"] as Map<String, Any?>) e.eras[k] = Valores.copiar(v) as MutableMap<String, Any?>
            e.nCriados = Valores.numero(m["n_criados"]).toInt()
            for ((k, v) in m["ent"] as Map<String, Any?>) e.ent[k] = Valores.copiar(v) as MutableMap<String, Any?>
            for ((k, v) in e.ent) {
                if (k.startsWith("eco.") && (v["difusao"] as? Map<String, Any?>)?.get("ativo") == true) e.ecosAtivos.add(k)
            }
            return e
        }

        const val ERA_INICIAL_ID = "era.001"
        val ERA_INICIAL = Triple(ERA_INICIAL_ID, "Fronteiras Seladas", "selado")

        /** Anel de M-04 (G-02), sentido horário; vizinhos são pares consecutivos. */
        val ANEL =
            listOf(
                "kingdom.coroas-de-pedra",
                "kingdom.aeltharyn",
                "kingdom.azeroth",
                "kingdom.veu-dos-malditos",
                "kingdom.vale-palancar",
            )
        val PUBLICOS =
            setOf(
                "alianca_de_povos", "fim_de_alianca_de_povos", "revolta", "bloqueio_de_rota", "guerra", "queda_de_coroa", "tratado",
                "ruptura", "disputa_aberta", "evento_ocorrido", "fim_de_cadeia", "incidente_fronteira", "quebra_de_contrato",
            )

        private fun sim(v: String) = v.trim().lowercase() == "sim"

        private fun lugarValido(x: String) = x.startsWith("city.") || x.startsWith("settlement.")

        /** Estado inicial = molde (+ dinâmica, só para a idade dos NPCs vivos). Nada aqui é editado no molde. */
        fun doMolde(
            w: MundoMolde,
            din: DinamicaEstado? = null,
        ): Estado {
            val s = Estado()
            val lim = { k: String, d: Int -> din?.lim?.get(k) ?: d }
            val comDin = din != null && !din.vazia
            for (r in w.t.getValue("npc")) {
                val e =
                    linkedMapOf<String, Any?>(
                        "vivo" to sim(r.getValue("vivo")), "reino" to r.getValue("reino"), "cidade" to r.getValue("cidade"),
                        "faccao" to r.getValue("faccao"), "cargo" to r.getValue("cargo"), "papeis" to r.getValue("papeis"),
                        "principal" to sim(r.getValue("principal")), "povo" to r.getValue("povo"), "subraca" to r.getValue("subraca"),
                        "nome" to r.getValue("nome"), "saude" to "sao",
                    )
                if (din != null && comDin) {
                    val classe = din.classeDe(r.getValue("povo"), r.getValue("subraca"))
                    e["classe"] = classe
                    e["nasc_dia"] = if (e["vivo"] == true) din.nascDia(r.getValue("id"), r.getValue("idade"), classe) else null
                }
                s.ent[r.getValue("id")] = e
            }
            for (r in w.t.getValue("relogio")) {
                s.ent[r.getValue("id")] =
                    linkedMapOf(
                        "reino" to r.getValue("reino"), "segmentos" to r.getValue("segmentos").trim().toInt(),
                        "progresso" to r.getValue("progresso").trim().toInt(), "estourado" to false,
                    )
            }
            for (r in w.t.getValue(
                "faccao",
            )) s.ent[r.getValue("id")] = linkedMapOf("reino" to r.getValue("reino"), "lideranca" to r.getValue("lideranca"))
            for (r in w.t.getValue(
                "cidade",
            )) s.ent[r.getValue("id")] = linkedMapOf("reino" to r.getValue("reino"), "governante" to r.getValue("governante"))
            s.postos(w)
            s.pares(w, lim)
            if (din != null && comDin && din.econ.isNotEmpty()) s.economia(w, din.econ)
            if (din != null && comDin && din.mist.isNotEmpty()) {
                for (r in w.t.getValue("misterio")) {
                    val loc = TabelaMolde.toks(r.getValue("entidades")).firstOrNull { lugarValido(it) }
                    val m = linkedMapOf<String, Any?>("reino" to r.getValue("reino"), "estado" to "aberto", "pistas" to 0)
                    if (loc != null) m["localidade"] = loc
                    s.ent[r.getValue("id")] = m
                }
            }
            for (r in w.t.getValue("evento")) {
                val total = maxOf(1, r.getValue("cadeia").split(" > ").count { it.isNotBlank() })
                s.ent[r.getValue("id")] =
                    linkedMapOf(
                        "reino" to r.getValue("reino"), "estado" to "latente", "passo" to 0, "inicio_dia" to null, "total" to total,
                    )
            }
            if (din != null && comDin && (din.front["front_tensao_limiar"] ?: 0) != 0) s.fronteiras(w)
            if (din != null && comDin && din.povos["povos_tensao_base"] != null) s.povos(w, din.povos)
            return s
        }

        /** Povos (raças e sub-raças) que convivem em cada cidade, derivados do molde. */
        fun povosPorCidade(w: MundoMolde): Pair<Map<String, MutableSet<String>>, Map<String, String>> {
            val racas = w.catalogo.racas.toSet()
            val sub = w.catalogo.subracas
            val tok = LinkedHashMap<String, MutableSet<String>>()
            for (c in w.t.getValue(
                "cidade",
            )) tok[c.getValue("id")] = TabelaMolde.toks(c.getValue("povos")).filter { it in racas }.toMutableSet()
            for (n in w.t.getValue("npc")) {
                val povos = tok[n.getValue("cidade")] ?: continue
                if (n.getValue("povo") in racas) povos.add(n.getValue("povo"))
                if (n.getValue("subraca") in sub) povos.add(n.getValue("subraca"))
            }
            return tok to sub
        }
    }

    private fun povos(
        w: MundoMolde,
        p: Map<String, Int>,
    ) {
        val (tok, sub) = povosPorCidade(w)
        val pares = LinkedHashMap<Pair<String, String>, MutableList<String>>()
        for (cid in tok.keys.sortedWith(Ordem.CODIGO)) {
            val reino = w.reinoDe(cid)
            val povos = tok.getValue(cid).sortedWith(Ordem.CODIGO)
            for (i in povos.indices) {
                for (j in i + 1 until povos.size) {
                    val a = povos[i]
                    val b = povos[j]
                    if ((sub[a] ?: a) == (sub[b] ?: b)) continue
                    ent["laco.$cid@$a+$b"] =
                        linkedMapOf(
                            "reino" to reino, "cidade" to cid, "a" to a, "b" to b, "relacao" to "neutra",
                            "tensao" to p.getValue("povos_tensao_base"), "desde" to null,
                        )
                    pares.getOrPut(a to b) { ArrayList() }.add(cid)
                }
            }
        }
        val ordem = pares.entries.sortedWith { x, y -> Ordem.PARES.compare(x.key, y.key) }
        for ((par, cs) in ordem) {
            if (cs.size < (p["povos_pacto_min_cidades"] ?: 2)) continue
            val reinos = cs.mapNotNull { w.reinoDe(it) }.toSet().sortedWith(Ordem.CODIGO)
            ent["pacto.${par.first}+${par.second}"] =
                linkedMapOf(
                    "reino" to (if (reinos.size == 1) reinos[0] else null), "reinos" to reinos.toMutableList(), "cidade" to cs[0],
                    "a" to par.first, "b" to par.second, "cidades" to cs.toMutableList(), "estado" to "nenhum", "desde" to null,
                )
        }
    }

    private fun fronteiras(w: MundoMolde) {
        for ((i, a) in ANEL.withIndex()) {
            val b = ANEL[(i + 1) % ANEL.size]
            if (a in w.reinos && b in w.reinos) {
                ent["crown." + a.substring(8)] = linkedMapOf("reino" to a, "estado" to "firme", "caida_dia" to null)
                ent["front.${a.substring(8)}+${b.substring(8)}"] =
                    linkedMapOf(
                        "reino" to a, "reinos" to mutableListOf<Any?>(a, b), "tensao" to 0, "incidentes" to 0, "ultimo_dia" to null,
                        "relacao" to "neutra", "relacao_dia" to null, "pendente" to false,
                    )
            }
        }
    }

    private fun economia(
        w: MundoMolde,
        e: Map<String, Int>,
    ) {
        val digito = Regex("^\\s*(\\d)")

        fun ordinal(
            txt: String,
            padrao: Int,
        ): Int = digito.find(txt)?.let { minOf(5, it.groupValues[1].toInt()) } ?: padrao
        for (r in w.t.getValue("rota")) {
            ent[r.getValue("id")] =
                linkedMapOf(
                    "reino" to r.getValue("reino"), "de" to r.getValue("de"), "para" to r.getValue("para"),
                    "evidencia" to r.getValue("estado"), "estado" to "aberta",
                )
        }
        for (r in w.t.getValue("recurso")) {
            val loc = r.getValue("localidade")
            if (!lugarValido(loc)) continue
            val k = "econ.${r.getValue("recurso")}@$loc"
            val reino = w.reinoDe(loc)
            if (k in ent || reino == null) continue
            val prod = r.getValue("papel") == "produtor"
            val p0 = if (prod) ordinal(r.getValue("producao"), e["econ_producao_padrao"] ?: 2) else 0
            val d0 =
                ordinal(
                    r.getValue("demanda"),
                    if (prod) e["econ_demanda_padrao_produtor"] ?: 0 else e["econ_demanda_padrao_consumidor"] ?: 2,
                )
            ent[k] =
                linkedMapOf(
                    "reino" to reino, "recurso" to r.getValue("recurso"), "localidade" to loc, "papel" to r.getValue("papel"),
                    "producao" to p0, "producao_atual" to p0, "demanda" to d0, "escassez" to 0, "estacoes_criticas" to 0,
                )
        }
    }

    /** Um posto por governante de cidade, líder de facção e NPC governante/general que não seja nenhum dos dois. */
    private fun postos(w: MundoMolde) {
        fun titulares(txt: String): List<String>? {
            val t = txt.split(';').filter { it.isNotBlank() }
            return if (t.isNotEmpty() && t.all { ent[it]?.get("vivo") == true }) t else null
        }
        val usados = HashSet<String>()
        for ((tipo, tab, campo, papel) in listOf(
            listOf("cidade", "cidade", "governante", "governante"),
            listOf("faccao", "faccao", "lideranca", "lideranca"),
        )) {
            for (r in w.t.getValue(tab)) {
                val t = titulares(r.getValue(campo)) ?: continue
                ent["posto." + r.getValue("id")] =
                    linkedMapOf(
                        "reino" to r.getValue("reino"), "tipo" to tipo, "alvo" to r.getValue("id"), "papel" to papel,
                        "titular" to t.joinToString(";"), "vago_desde" to null, "prazo" to null, "interino" to "—", "anterior" to "—",
                    )
                usados.addAll(t)
            }
        }
        for (r in w.t.getValue("npc")) {
            val p = r.getValue("papeis").split(';').toSet()
            if (r.getValue("id") !in usados && ent.getValue(r.getValue("id"))["vivo"] == true && ("governante" in p || "general" in p)) {
                ent["posto." + r.getValue("id")] =
                    linkedMapOf(
                        "reino" to r.getValue("reino"), "tipo" to "npc", "alvo" to r.getValue("id"),
                        "papel" to (if ("governante" in p) "governante" else "general"), "titular" to r.getValue("id"),
                        "vago_desde" to null, "prazo" to null, "interino" to "—", "anterior" to "—",
                    )
            }
        }
    }

    private fun pares(
        w: MundoMolde,
        lim: (String, Int) -> Int,
    ) {
        val al = HashSet<Pair<String, String>>()
        val ri = HashSet<Pair<String, String>>()

        fun par(
            a: String,
            b: String,
        ) = if (Ordem.comparar(a, b) <= 0) a to b else b to a
        for (f in w.t.getValue("faccao")) {
            for (x in TabelaMolde.toks(f.getValue("aliados"))) if (x in ent && x != f.getValue("id")) al.add(par(f.getValue("id"), x))
            for (x in TabelaMolde.toks(f.getValue("rivais"))) if (x in ent && x != f.getValue("id")) ri.add(par(f.getValue("id"), x))
        }
        val todos = (al + ri).sortedWith(Ordem.PARES)
        for ((a, b) in todos) {
            val rel =
                if ((a to b) in al && (a to b) in ri) {
                    "ambigua"
                } else if ((a to b) in al) {
                    "aliança"
                } else {
                    "rivalidade"
                }
            val t0 =
                when (rel) {
                    "aliança" -> lim("tensao_base_alianca", 20)
                    "rivalidade" -> lim("tensao_base_rivalidade", 50)
                    else -> lim("tensao_inicial_ambigua", 40)
                }
            ent["par.$a+$b"] = linkedMapOf("reino" to ent.getValue(a).getValue("reino"), "a" to a, "b" to b, "relacao" to rel, "tensao" to t0)
        }
    }
}

/** Ordem por ponto de código usada pelo `sorted` do Python. */
object Ordem {
    val CODIGO = Comparator<String> { a, b -> JsonCanonico.compararPontosDeCodigo(a, b) }

    fun comparar(
        a: String,
        b: String,
    ): Int = CODIGO.compare(a, b)

    val PARES_INT =
        Comparator<Pair<String, Int>> { x, y -> comparar(x.first, y.first).let { if (it != 0) it else x.second.compareTo(y.second) } }

    val PARES =
        Comparator<Pair<String, String>> { x, y -> comparar(x.first, y.first).let { if (it != 0) it else comparar(x.second, y.second) } }
}
