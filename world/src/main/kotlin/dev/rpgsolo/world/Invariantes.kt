package dev.rpgsolo.world

/** Violação de um delta: invariante e mensagem (porta de `(inv, msg)` de `invariantes.py`). */
class Viol(val inv: String, val msg: String)

/** Violação de estado ou de bloco: invariante, entidade e mensagem. */
class VBloco(val inv: String, val entidade: String, val msg: String)

/** Invariantes de continuidade (porta de `invariantes.py`): por delta, por estado ao fim do bloco e por bloco. */
object Invariantes {
    /** Código → (gravidade `E` erro ou `A` alerta, nome). */
    val IMPLEMENTADOS: Map<String, Pair<String, String>> = linkedMapOf(
        "I-01" to ("E" to "molde intacto (hash dos dados/, mundo/ e catálogo; hash da dinâmica)"),
        "I-02" to ("E" to "determinismo (mesma semente, mesma crônica)"),
        "I-03" to ("E" to "morto não age"),
        "I-04" to ("E" to "cargo ocupado ou vacância válida (prazo e interino)"),
        "I-05" to ("E" to "referências válidas"),
        "I-06" to ("E" to "toda mudança tem causa"),
        "I-07" to ("E" to "origem marcada (nada novo é ORIGINAL)"),
        "I-08" to ("E" to "mistério sem evidência não se resolve (exige agente, evidência listada e verdade não vedada)"),
        "I-09" to ("E" to "camada respeitada (Eco restrito ou secreto só avança por rota registrada)"),
        "I-10" to ("E" to "contrato de fronteira na Era I (selado): sem rota nova, sem Mistério D resolvido, sem agente de outro reino, sem troca de dono de Torvelume)"),
        "I-11" to ("E" to "aliado e rival ao mesmo tempo é proibido (par só muda por transição permitida)"),
        "I-12" to ("E" to "calendário coerente (a mesma data do mundo dá o mesmo dia do ano em todos os reinos)"),
        "I-13" to ("E" to "ordem causal"),
        "I-14" to ("E" to "linhagem de NPC simulado"),
        "I-15" to ("A" to "NPC simulado vivo sem lugar"),
        "I-16" to ("A" to "mundo não morre (todo reino mantém NPCs vivos)"),
        "I-17" to ("A" to "mundo não congela (mínimo de eventos importantes por década e reino)"),
        "I-18" to ("A" to "sem explosão (teto de eventos importantes por ano e reino)"),
        "I-19" to ("E" to "retomada coerente"),
        "I-20" to ("E" to "legibilidade (toda causa remonta ao molde ou a um delta anterior)"),
        "I-21" to ("E" to "notificação completa (importância 3+)"),
        "I-22" to ("E" to "passo neutro"),
        "I-23" to ("E" to "transições permitidas da relação entre reinos vizinhos"),
        "I-24" to ("E" to "guerra válida e queda de coroa irreversível durante guerra"),
        "I-25" to ("E" to "mudança de era/regime com gatilho e causa correspondentes"),
        "I-26" to ("E" to "laço entre povos: transições permitidas e aliança só com governante vivo da cidade como agente"),
        "I-27" to ("E" to "pacto entre povos coerente com os laços aliados do par"),
        "I-28" to ("E" to "níveis completos: todo delta de importância 3+ tem nível e cidade ou reino"),
    )

    /** Invariantes que só se comparam entre rodadas (não rodam dentro de uma). */
    val ENTRE_RODADAS = setOf("I-02", "I-19", "I-22")

    val CERTEZAS = setOf("confirmado", "alegado", "suspeita", "rumor", "inferência", "desconhecido")

    private val TRANSICOES_PAR: Map<String, Set<String>> = mapOf(
        "aliança" to setOf("rompida"), "rompida" to setOf("rivalidade"), "rivalidade" to setOf("disputa_aberta", "tratado"),
        "disputa_aberta" to setOf("rivalidade"), "tratado" to setOf("rivalidade"), "ambigua" to setOf("rivalidade", "tratado"),
    )
    private val TRANSICOES_LACO: Map<String, Set<String>> = mapOf(
        "neutra" to setOf("alianca", "rivalidade"), "alianca" to setOf("neutra"), "rivalidade" to setOf("neutra"),
    )
    private val TRANSICOES_FRONT: Map<String, Set<String>> = mapOf(
        "neutra" to setOf("neutra", "tratado", "ruptura"),
        "tratado" to setOf("tratado", "neutra", "ruptura"),
        "ruptura" to setOf("ruptura", "neutra", "tratado", "guerra"),
        "guerra" to setOf("guerra", "neutra", "tratado"),
    )
    private val ORIGENS_NPC = setOf("nascimento", "chegada", "nomeacao")
    val PADRAO_DELTA = Regex("^d\\d{6}$")

    private fun s(x: Any?): String = when (x) {
        null -> "None"
        true -> "True"
        false -> "False"
        else -> x.toString()
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapa(x: Any?): Map<String, Any?>? = x as? Map<String, Any?>

    private fun lista(x: Any?): List<Any?> = (x as? List<Any?>) ?: emptyList()

    /** `i` é id de um delta já gravado? A crônica numera de 1 a [total] (`d%06d`). */
    fun idEmCronica(i: String, total: Int): Boolean {
        if (!i.startsWith("d") || i.length < 7 || !i.drop(1).all { it in '0'..'9' }) return false
        val n = i.drop(1).toLongOrNull() ?: return false
        return n in 1..total && i == "d%06d".format(java.util.Locale.ROOT, n)
    }

    /**
     * Violações de um delta. [estado] é o estado ANTES de aplicar; [total] é o número de deltas já na crônica;
     * [ultimoDia] o dia do último delta gravado.
     */
    fun checarDelta(d: Map<String, Any?>, estado: Estado, ultimoDia: Int, total: Int, world: MundoMolde, din: Dinamica?): List<Viol> {
        val v = ArrayList<Viol>()
        for (k in listOf("tipo", "entidade")) {
            val x = d[k]
            if (x !is String || x.isEmpty()) v.add(Viol("F-00", "delta sem $k"))
        }
        if (d["certeza"] !in CERTEZAS) v.add(Viol("F-00", "certeza fora do vocabulário: ${d["certeza"]}"))
        val tipo = d["tipo"]
        val campo = d["campo"]
        if (tipo == "campo" && (campo == null || campo == "")) v.add(Viol("F-00", "delta de campo sem campo"))
        val agentes = lista(d["agentes"]).map { s(it) }
        for (a in agentes) if (estado.morto(a)) v.add(Viol("I-03", "agente morto: $a"))
        val c0 = mapa(d["causa"])
        val causaOk = c0 != null && lista(c0["ids"]).isNotEmpty() && !(c0["regra"] == null || c0["regra"] == "")
        if (!causaOk) v.add(Viol("I-06", "delta sem causa (ids e regra)"))
        val c: Map<String, Any?> = if (causaOk) c0!! else linkedMapOf("ids" to emptyList<Any?>())
        val idsC: List<String> = lista(c["ids"]).map { s(it) }
        if (d["origem"] != "SIMULADO") v.add(Viol("I-07", "origem diferente de SIMULADO: ${d["origem"]}"))
        val dia = (d["dia_mundo"] as? Number)?.toInt() ?: 0
        if (dia < ultimoDia) v.add(Viol("I-13", "dia_mundo $dia anterior ao último registrado $ultimoDia"))
        for (i in idsC) if (PADRAO_DELTA.matches(i) && !idEmCronica(i, total)) v.add(Viol("I-13", "causa aponta para delta que não veio antes: $i"))

        fun existe(i: String) = i in estado.ent || i in world.indice || i == "mundo" || i == "mundo.mythralis" || idEmCronica(i, total)
        val entidade = d["entidade"]?.let { s(it) }
        if (tipo != "criacao" && !entidade.isNullOrEmpty() && !existe(entidade)) v.add(Viol("I-05", "entidade inexistente: $entidade"))
        for (i in idsC + agentes) if (!existe(i)) v.add(Viol("I-05", "referência inexistente: $i"))
        val ns = d["estado_novo"]
        val nsMapa = mapa(ns)
        val resolve = (tipo == "tentativa_de_resolver_misterio" && nsMapa != null && verdadeiro(nsMapa["resolvido"])) ||
            (tipo == "campo" && campo == "estado" && s(d["entidade"]).startsWith("mist.") && ns != "aberto")
        if (resolve) {
            val row = world.linha(s(d["entidade"])) ?: emptyMap()
            val ev = nsMapa?.get("evidencia")?.let { s(it) } ?: ""
            val vd = nsMapa?.get("verdade")?.let { s(it) } ?: ""
            if (agentes.isEmpty()) v.add(Viol("I-08", "mistério resolvido sem agente investigador: ${d["entidade"]}"))
            if (ev.isEmpty() || (row["evidencia_para_resolver"] ?: "").lowercase().contains(ev.lowercase()).not()) {
                v.add(Viol("I-08", "mistério resolvido sem a evidência listada no molde: ${d["entidade"]}"))
            }
            if (vd.isNotEmpty() && (row["a_verdade_nao_pode_ser"] ?: "").lowercase().contains(vd.lowercase())) {
                v.add(Viol("I-08", "verdade vedada pelo molde: ${d["entidade"]}"))
            }
        }
        if (tipo == "campo" && campo == "difusao") {
            val eco = estado.ent[s(d["entidade"])] ?: emptyMap()
            val camada = eco["camada"]
            if ((camada == "restrito" || camada == "secreto") && nsMapa != null &&
                lista(nsMapa["lugares"]).size > lista(mapa(eco["difusao"])?.get("lugares")).size &&
                idsC.none { it.startsWith("route.") }
            ) {
                v.add(Viol("I-09", "Eco $camada avançou sem rota registrada: ${d["entidade"]}"))
            }
        }
        if (estado.mundo["regime"] == "selado") {
            val ent = estado.ent[s(d["entidade"])] ?: emptyMap()
            val rk = ent["reino"]
            if (tipo == "criacao" && s(d["entidade"]).startsWith("route.")) v.add(Viol("I-10", "rota nova na Era I: ${d["entidade"]}"))
            if (d["entidade"] == "mist.d-mina-selada" && resolve) v.add(Viol("I-10", "Mistério D resolvido na Era I"))
            for (a in agentes) {
                val ra = estado.ent[a]?.get("reino")
                if (rk != null && rk != "" && ra != null && ra != "" && ra != rk) v.add(Viol("I-10", "agente de outro reino ($a) em ${d["entidade"]}"))
            }
            if (tipo == "campo" && d["entidade"] in setOf("city.torvelume", "posto.city.torvelume") && campo in setOf("governante", "titular")) {
                for (x in s(ns).split(';')) {
                    val rx = estado.ent[x]?.get("reino")
                    if (rx != null && rx != "" && rk != null && rk != "" && rx != rk) v.add(Viol("I-10", "Torvelume trocou de dono: $x"))
                }
            }
        }
        val ant = d["estado_anterior"]
        val antS = ant as? String ?: ""
        val nsS = ns as? String
        val entS = s(d["entidade"])
        if (tipo == "campo" && campo == "relacao" && entS.startsWith("par.")) {
            if (nsS !in (TRANSICOES_PAR[antS] ?: emptySet<String>())) v.add(Viol("I-11", "transição de par não permitida: ${s(ant)} -> ${s(ns)}"))
        }
        if (tipo == "campo" && campo == "relacao" && entS.startsWith("laco.")) {
            if (nsS !in (TRANSICOES_LACO[antS] ?: emptySet<String>())) {
                v.add(Viol("I-26", "transição de laço não permitida: ${s(ant)} -> ${s(ns)}"))
            } else if (ns == "alianca") {
                val cid = estado.ent[entS]?.get("cidade")
                val gov = (estado.ent[cid?.toString() ?: ""]?.get("governante")?.toString() ?: "").replace(',', ';').split(';').map { it.trim() }
                if (agentes.isEmpty() || agentes[0] !in gov || !estado.vivo(agentes[0])) {
                    v.add(Viol("I-26", "aliança de povos sem governante vivo da cidade como agente: $entS"))
                }
            }
        }
        if ((tipo == "alianca_de_povos" || tipo == "fim_de_alianca_de_povos") && entS in estado.ent && din != null) {
            val pc = estado.ent.getValue(entS)
            val minimo = din.povos["povos_pacto_min_cidades"] ?: 2
            val por = LinkedHashMap<String, Int>()
            for (cid in lista(pc["cidades"])) {
                val lk = "laco.$cid@${pc["a"]}+${pc["b"]}"
                if (estado.ent[lk]?.get("relacao") == "alianca") {
                    val r = s(estado.ent.getValue(s(cid))["reino"])
                    por[r] = (por[r] ?: 0) + 1
                }
            }
            val n = if (estado.mundo["regime"] == "selado") por.values.maxOrNull() ?: 0 else por.values.sum()
            if (tipo == "alianca_de_povos" && (pc["estado"] != "nenhum" || n < minimo)) {
                v.add(Viol("I-27", "aliança de povos sem laços aliados suficientes ($n < $minimo): $entS"))
            }
            if (tipo == "fim_de_alianca_de_povos" && (pc["estado"] != "alianca" || n >= minimo)) {
                v.add(Viol("I-27", "fim de aliança de povos com laços aliados ainda suficientes: $entS"))
            }
        }
        if (tipo == "criacao" && s(d["entidade"]).startsWith("npc.")) {
            val n = nsMapa ?: emptyMap()
            val cat = world.catalogo
            if (n["origem_npc"] !in ORIGENS_NPC) v.add(Viol("I-14", "NPC simulado sem origem (nascimento, chegada ou nomeação)"))
            if (n["povo"] !in cat.racas) v.add(Viol("I-14", "povo fora do catálogo: ${pyRepr(n["povo"])}"))
            val sub = n["subraca"] ?: "—"
            if (sub != "—" && cat.subracas[s(sub)] != n["povo"]) v.add(Viol("I-14", "sub-raça inválida para o povo: ${pyRepr(sub)}"))
        }
        if (tipo == "campo" && campo == "relacao" && entS.startsWith("front.")) {
            if (nsS !in (TRANSICOES_FRONT[antS] ?: emptySet<String>())) v.add(Viol("I-23", "transição de fronteira não permitida: ${s(ant)} -> ${s(ns)}"))
        }
        fun guerraValida(parId: String): Boolean {
            val par = estado.ent[parId] ?: emptyMap()
            val coroas = lista(par["reinos"]).map { s(it) }.filter { it.startsWith("kingdom.") }.map { "crown." + it.substring(8) }
            val minimo = din?.guerra?.get("guerra_tensao_min") ?: 0
            val tensao = par["tensao"]?.let { Valores.numero(it) } ?: -1L
            return coroas.size == 2 && par["relacao"] == "ruptura" && tensao >= minimo && estado.mundo["regime"] != "selado" &&
                coroas.all { it in estado.ent && estado.ent.getValue(it)["estado"] == "firme" }
        }
        val regra = c["regra"]
        val eGuerra = entS.startsWith("front.") && guerraValida(entS)
        if (tipo == "guerra" || (tipo == "campo" && campo == "relacao" && ns == "guerra" && entS.startsWith("front."))) {
            if (!eGuerra || regra != "guerra-de-fronteira") v.add(Viol("I-24", "guerra sem ruptura elegível, coroas firmes ou causa de fronteira"))
        }
        val queda = tipo == "queda_de_coroa" || (tipo == "campo" && entS.startsWith("crown.") && campo == "estado" && ns == "caida")
        if (queda) {
            val coroa = estado.ent[entS] ?: emptyMap()
            val reino = coroa["reino"]
            val pares = estado.ent.filter { (k, p) -> k.startsWith("front.") && p["relacao"] == "guerra" && lista(p["reinos"]).contains(reino) }.keys
            if (!entS.startsWith("crown.") || coroa["estado"] != "firme" || pares.isEmpty() || regra != "queda-de-coroa-em-guerra" || pares.none { it in idsC }) {
                v.add(Viol("I-24", "queda de coroa sem guerra ativa e causa correspondente"))
            }
        }
        if (tipo == "campo" && entS.startsWith("crown.") && campo == "estado" && estado.ent[entS]?.get("estado") == "caida" && ns != "caida") {
            v.add(Viol("I-24", "coroa caída não pode ser restaurada pela simulação"))
        }
        if (tipo == "era") {
            val novo = nsMapa ?: emptyMap()
            val antigo = estado.mundo["regime"]
            val proxima = "era.%03d".format(java.util.Locale.ROOT, estado.eras.size + 1)
            var ok = novo["era"] == proxima
            val nr = novo["regime"]
            if (antigo == null) {
                ok = ok && nr == "selado" && regra == "inicio-da-rodada"
            } else if (antigo == "selado" && nr == "poroso") {
                ok = ok && regra in setOf("primeiro-incidente-de-fronteira", "limite-de-anos-sem-gatilho")
                if (regra == "primeiro-incidente-de-fronteira") ok = ok && idsC.any { it.startsWith("front.") }
            } else if (antigo == "poroso" && nr == "aberto") {
                ok = ok && regra == "primeira-guerra" && idsC.any { it.startsWith("front.") && estado.ent[it]?.get("relacao") == "guerra" }
            } else if (antigo == "aberto" && nr == "aberto") {
                ok = ok && regra == "queda-de-coroa-em-guerra" && idsC.any { it.startsWith("crown.") && estado.ent[it]?.get("estado") == "caida" }
            } else {
                ok = false
            }
            if (!ok) v.add(Viol("I-25", "mudança de era/regime sem gatilho F7 válido: ${pyRepr(antigo)} -> ${pyRepr(nr)} (${s(regra)})"))
        }
        return v
    }

    /** `repr` do Python para os valores que aparecem nas mensagens. */
    private fun pyRepr(x: Any?): String = when (x) {
        null -> "None"
        is String -> "'$x'"
        is Boolean -> if (x) "True" else "False"
        else -> x.toString()
    }

    /** Conferência do estado ao fim de um bloco (I-04 só com o motor de sucessão na rodada; I-15). */
    fun checarEstado(estado: Estado, dia: Int, motores: Set<String>): List<VBloco> {
        val v = ArrayList<VBloco>()
        for (k in estado.ent.keys.sortedWith(Ordem.CODIGO)) {
            val e = estado.ent.getValue(k)
            if (k.startsWith("posto.") && "sucessao" in motores) {
                val tit = s(e["titular"]).split(';').filter { it.startsWith("npc.") }
                if (tit.isNotEmpty()) {
                    for (x in tit) if (!estado.vivo(x)) v.add(VBloco("I-04", k, "titular morto sem vacância registrada: $x"))
                } else if (e["vago_desde"] == null || e["prazo"] == null || !estado.vivo(s(e["interino"]))) {
                    v.add(VBloco("I-04", k, "vacância sem prazo ou sem interino vivo"))
                } else if (dia > Valores.numero(e["prazo"]) + 366) {
                    v.add(VBloco("I-04", k, "vacância além do prazo (${dia - Valores.numero(e["prazo"])} dias)"))
                }
            } else if (k.startsWith("npc.") && !(e["origem_npc"] == null || e["origem_npc"] == "") && e["vivo"] == true && (e["cidade"] == "—" || e["cidade"] == "?" || e["cidade"] == null)) {
                v.add(VBloco("I-15", k, "NPC simulado vivo sem cidade"))
            }
        }
        return v
    }

    /**
     * I-12, I-16, I-17, I-18, I-20 e I-28 ao fim de um bloco. [deltas] são os do bloco; [enraizados] é mantido pelo
     * simulador (id de delta fora do bloco e fora do conjunto vale como conferido).
     */
    fun checarBloco(
        deltas: List<Map<String, Any?>>,
        estado: Estado,
        diaFim: Int,
        parcial: Boolean,
        cals: Map<String, CalendarioReino?>,
        inv: Map<String, Int>,
        reinos: ReinosMapa,
        world: MundoMolde,
        enraizados: MutableSet<String>,
    ): List<VBloco> {
        val v = ArrayList<VBloco>()
        val doys = LinkedHashMap<String, Int>()
        for ((r, c) in cals) if (c != null && c.valido()) doys[r] = c.data(diaFim).diaDoAno
        if (doys.values.toSet().size > 1) v.add(VBloco("I-12", "mundo", "dia do ano diferente entre reinos no dia $diaFim: ${doys.entries.joinToString(", ", "{", "}") { "'${it.key}': ${it.value}" }}"))
        for (r in world.reinos.keys.sortedWith(Ordem.CODIGO)) {
            if (estado.ent.none { (k, e) -> k.startsWith("npc.") && e["vivo"] == true && e["reino"] == r }) v.add(VBloco("I-16", r, "reino sem NPC vivo"))
        }
        val ids = HashMap<String, Map<String, Any?>>()
        for (d in deltas) ids[s(d["id"])] = d
        for (d in deltas) {
            val pend = arrayListOf(s(d["id"]))
            var ok = false
            val vis = HashSet<String>()
            while (pend.isNotEmpty() && !ok) {
                val i = pend.removeAt(pend.size - 1)
                if (!vis.add(i)) continue
                val x = ids[i]
                if (x == null) {
                    ok = true
                    continue
                }
                if (i in enraizados) {
                    ok = true
                    continue
                }
                for (c in lista(mapa(x["causa"])?.get("ids")).map { s(it) }) {
                    if (c == i) continue
                    if (!PADRAO_DELTA.matches(c)) {
                        ok = true
                        break
                    }
                    pend.add(c)
                }
            }
            if (ok) enraizados.add(s(d["id"])) else v.add(VBloco("I-20", s(d["id"]), "causa não remonta ao molde nem a um delta anterior"))
        }
        val n = Niveis(world, deltas, estado, reinos)
        for (d in deltas) {
            if (imp(d) >= 3 && d["entidade"] != "mundo") {
                if (n.nivel(d) !in 1..3 || (n.cidade(d) == null && n.reinos(d) == listOf("—"))) {
                    v.add(VBloco("I-28", s(d["id"]), "delta importante sem nível ou sem cidade/reino: ${d["entidade"]}"))
                }
            }
        }
        val porAno = java.util.TreeMap<Pair<String, Int>, Int>(Ordem.PARES_INT)
        for (d in deltas) {
            if (imp(d) >= 3 && d["entidade"] != "mundo") {
                val r = reinos.de(s(d["entidade"]))
                if (r in world.reinos) {
                    val k = r to Math.floorDiv((d["dia_mundo"] as Number).toInt(), 365)
                    porAno[k] = (porAno[k] ?: 0) + 1
                }
            }
        }
        val teto = inv["inv_max_importantes_ano"]
        if (teto != null && teto != 0) {
            for ((k, c) in porAno) if (c > teto) v.add(VBloco("I-18", k.first, "$c eventos importantes no ano ${k.second} (teto $teto)"))
        }
        val minimo = inv["inv_min_importantes_decada"]
        if (minimo != null && minimo != 0 && !parcial && deltas.isNotEmpty()) {
            val anos = maxOf(1, Math.floorDiv((deltas.last()["dia_mundo"] as Number).toInt() - (deltas.first()["dia_mundo"] as Number).toInt(), 365) + 1)
            for (r in world.reinos.keys.sortedWith(Ordem.CODIGO)) {
                val c = porAno.filter { it.key.first == r }.values.sum()
                if (c * 10 < minimo * anos) v.add(VBloco("I-17", r, "só $c eventos importantes em $anos anos (mínimo $minimo por década)"))
            }
        }
        return v
    }

    private fun verdadeiro(x: Any?): Boolean = !(x == null || x == false || x == "" || (x is Number && x.toLong() == 0L) || (x is Collection<*> && x.isEmpty()) || (x is Map<*, *> && x.isEmpty()))

    private fun imp(d: Map<String, Any?>): Int = (d["importancia"] as? Number)?.toInt() ?: 0
}
