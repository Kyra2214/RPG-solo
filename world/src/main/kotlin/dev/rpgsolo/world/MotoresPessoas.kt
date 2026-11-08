package dev.rpgsolo.world

import java.util.Locale

private fun Map<String, Any?>.str(k: String): String = this[k].toString()

private fun Map<String, Any?>.int(k: String): Int = Valores.numero(this[k]).toInt()

/** Demografia de pessoas: idade por classe de longevidade, doença e morte natural. Anual (porta de `demografia.py`). */
class Demografia(private val din: Dinamica) : Motor {
    override val nome = "demografia"
    override val frequencia = Frequencia.ANUAL

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        if (din.vazia) return emptyList()
        val out = ArrayList<DeltaRascunho>()
        val e = ctx.estado
        val fd = din.lim["fator_mortalidade_doente"] ?: 4
        for (i in e.ent.keys.filter { it.startsWith("npc.") }.sortedWith(Ordem.CODIGO)) {
            val n = e.ent.getValue(i)
            if (n["vivo"] != true || n["nasc_dia"] == null) continue
            val idade = Math.floorDiv(ctx.diaMundo - n.int("nasc_dia"), 365)
            var p = din.mortePm(n.str("classe"), idade)
            val doente = n["saude"] == "doente"
            if (doente) p = minOf(1000, p * fd)
            val q = listOf(n.str("reino"))
            if (ctx.rng.randrange(1000) < p) {
                out.add(
                    DeltaRascunho(
                        "campo", i, "vivo", false,
                        causa(listOf(i), "morte-natural" + (if (doente) "-doente" else "")), quemSabe = q,
                    ),
                )
                continue
            }
            if (doente) {
                if (ctx.rng.randrange(1000) < (din.lim["recuperacao_pm"] ?: 350)) {
                    out.add(DeltaRascunho("campo", i, "saude", "sao", causa(listOf(i), "recuperacao"), quemSabe = q))
                }
            } else if (din.eIdoso(n.str("classe"), idade) && ctx.rng.randrange(1000) < (din.lim["doenca_idosa_pm"] ?: 40)) {
                out.add(DeltaRascunho("campo", i, "saude", "doente", causa(listOf(i), "doenca-da-idade"), quemSabe = q))
            }
        }
        return out
    }
}

/** Relógios executáveis. Lunar: avanço por pressões, recuo por travas e homeostase; estouro quando completo (`relogios.py`). */
class Relogios(private val din: Dinamica) : Motor {
    override val nome = "relogios"
    override val frequencia = Frequencia.LUNAR

    private fun ativa(ctx: Ctx, i: String): Boolean {
        val e = ctx.estado
        if (i.startsWith("npc.")) return e.vivo(i)
        if (i.startsWith("faction.")) {
            val limite = (din.lim["limiar_disputa_aberta"] ?: 85) - 20
            return e.ent.any { (k, p) -> k.startsWith("par.") && (i == p["a"] || i == p["b"]) && Valores.numero(p["tensao"]) >= limite }
        }
        return true
    }

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        if (din.vazia) return emptyList()
        val out = ArrayList<DeltaRascunho>()
        val e = ctx.estado
        val l = din.lim
        for (i in e.ent.keys.filter { it.startsWith("clock.") }.sortedWith(Ordem.CODIGO)) {
            val c = e.ent.getValue(i)
            if (c["estourado"] == true) continue
            val reg = din.relogios[i] ?: RegraRelogio(taxa = 20, pressoes = emptyList(), travas = emptyList(), eventos = emptyList(), inacao = 12)
            val pres = reg.pressoes.count { ativa(ctx, it) }
            val trav = reg.travas.count { ativa(ctx, it) }
            val pressaoPm = l["relogio_pressao_pm"] ?: 4
            val travaPm = l["relogio_trava_pm"] ?: 4
            val avanco = maxOf(0, minOf(l["relogio_max_pm"] ?: 300, reg.taxa + reg.inacao + pressaoPm * pres - travaPm * trav))
            val recuo = (l["relogio_recuo_base_pm"] ?: 20) + travaPm * trav
            val ids = listOf(i) + (reg.pressoes + reg.travas).filter { it in e.ent }.take(3)
            val q = listOf(c.str("reino"))
            val progresso = c.int("progresso")
            if (progresso >= c.int("segmentos")) {
                out.add(DeltaRascunho("campo", i, "estourado", true, causa(listOf(i), "relogio-completo"), quemSabe = q))
                continue
            }
            val r = ctx.rng.randrange(1000)
            if (r < avanco) {
                out.add(DeltaRascunho("campo", i, "progresso", progresso + 1, causa(ids, "relogio-avanco"), quemSabe = q))
            } else if (progresso > 0 && r < avanco + recuo) {
                out.add(DeltaRascunho("campo", i, "progresso", progresso - 1, causa(ids, "relogio-recuo"), quemSabe = q))
            }
        }
        return out
    }
}

/** Eventos. Lunar: estouro de relógio, evento ocorre, a cadeia do molde vira passos em ordem (`eventos.py`). */
class Eventos(private val din: Dinamica) : Motor {
    override val nome = "eventos"
    override val frequencia = Frequencia.LUNAR

    private var idx: Pair<Map<String, Linha>, Map<String, List<Linha>>>? = null

    private fun prep(w: MundoMolde): Pair<Map<String, Linha>, Map<String, List<Linha>>> = idx ?: run {
        val ev = LinkedHashMap<String, Linha>()
        for (r in w.t.getValue("evento")) ev[r.getValue("id")] = r
        val sem = LinkedHashMap<String, MutableList<Linha>>()
        for (s in w.t.getValue("semente")) {
            for (x in TabelaMolde.toks(s.getValue("relogio_evento"))) if (x.startsWith("event.")) sem.getOrPut(x) { ArrayList() }.add(s)
        }
        (ev to sem).also { idx = it }
    }

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        if (din.vazia) return emptyList()
        val (ev, sem) = prep(ctx.mundo)
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        val atraso = 30 * (din.lim["atraso_cadeia_luas"] ?: 2)
        for (i in e.ent.keys.filter { it.startsWith("event.") }.sortedWith(Ordem.CODIGO)) {
            val s = e.ent.getValue(i)
            val r = ev.getValue(i)
            val q = listOf(s.str("reino")) + TabelaMolde.toks(r.getValue("cidades_e_assentamentos")).filter { it in e.ent }.take(3)
            if (s["estado"] == "latente") {
                val rel = r.getValue("relogio")
                if (e.ent[rel]?.get("estourado") == true) {
                    val cz = causa(listOf(rel, i), "estouro-de-relogio")
                    out.add(DeltaRascunho("evento_ocorrido", i, causa = cz, quemSabe = q, estadoNovo = linkedMapOf("relogio" to rel, "nome" to r.getValue("nome"))))
                    out.add(DeltaRascunho("campo", i, "estado", "em_cadeia", cz, quemSabe = q))
                    out.add(DeltaRascunho("campo", i, "inicio_dia", ctx.diaMundo, cz, quemSabe = q))
                }
            } else if (s["estado"] == "em_cadeia" && ctx.diaMundo >= s.int("inicio_dia") + atraso * s.int("passo")) {
                val passos = r.getValue("cadeia").split(" > ").map { it.trim() }.filter { it.isNotEmpty() }
                val n = s.int("passo")
                val ags = TabelaMolde.toks(r.getValue("pessoas_e_faccoes")).filter { it.startsWith("npc.") && e.vivo(it) }
                val cz = causa(listOf(i), "cadeia-do-evento")
                out.add(
                    DeltaRascunho(
                        "passo_de_cadeia", i, causa = cz, quemSabe = q, agentes = ags,
                        estadoNovo = linkedMapOf("passo" to n + 1, "de" to passos.size, "texto" to passos[minOf(n, passos.size - 1)]),
                    ),
                )
                out.add(DeltaRascunho("campo", i, "passo", n + 1, cz, quemSabe = q))
                if (n + 1 >= s.int("total")) {
                    out.add(DeltaRascunho("campo", i, "estado", "concluido", cz, quemSabe = q))
                    out.add(DeltaRascunho("fim_de_cadeia", i, causa = cz, quemSabe = q, estadoNovo = linkedMapOf("nome" to r.getValue("nome"))))
                    for (sd in sem[i] ?: emptyList()) {
                        out.add(
                            DeltaRascunho(
                                "consequencia_sem_agente", sd.getValue("id"), quemSabe = q,
                                causa = causa(listOf(i, sd.getValue("id")), "se-ninguem-age"), certeza = "alegado",
                                estadoNovo = linkedMapOf("texto" to sd.getValue("se_ninguem_age")),
                            ),
                        )
                    }
                }
            }
        }
        return out
    }
}

/**
 * Sucessão: posto com titular morto vai para herdeiro, vice (vínculo de cargo), candidato da facção ou cidade e,
 * por fim, vacância com prazo e interino; NPC simulado quando o prazo acaba. Anual, depois da demografia (`sucessao.py`).
 */
class Sucessao(private val din: Dinamica) : Motor {
    override val nome = "sucessao"
    override val frequencia = Frequencia.ANUAL

    private var rel: List<Pair<String, String>>? = null
    private var novos = LinkedHashMap<String, MutableMap<String, Any?>>()

    private fun vinculos(w: MundoMolde): List<Pair<String, String>> = rel ?: w.t.getValue("relacao")
        .filter { it.getValue("tipo") == "vínculo de cargo" && it.getValue("destino").startsWith("npc.") }
        .map { it.getValue("origem") to it.getValue("destino") }.also { rel = it }

    private fun titulares(p: Map<String, Any?>): List<String> = p.str("titular").split(';').filter { it.startsWith("npc.") }

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        if (din.vazia) return emptyList()
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        novos = LinkedHashMap()
        val titulares = HashSet<String>()
        for ((k, p) in e.ent) if (k.startsWith("posto.")) titulares.addAll(titulares(p))
        for (k in e.ent.keys.filter { it.startsWith("posto.") }.sortedWith(Ordem.CODIGO)) {
            val p = e.ent.getValue(k)
            val tit = titulares(p)
            val vivos = tit.filter { e.vivo(it) }
            if (tit.isNotEmpty() && vivos.size == tit.size) continue
            if (tit.isNotEmpty() && vivos.isNotEmpty()) {
                out.addAll(mudarTitular(k, p, vivos.joinToString(";"), tit, "diarquia-reduzida"))
                continue
            }
            val morto: String? = if (tit.isNotEmpty()) tit.last() else p["anterior"]?.toString()?.takeIf { it != "—" }
            val ref: Map<String, Any?> = if (morto != null) LinkedHashMap(e.ent[morto] ?: emptyMap()).also { it["_id"] = morto } else emptyMap()
            var (novo, regra) = escolher(ctx, p, morto, ref, titulares)
            if (novo == null && p["vago_desde"] != null && ctx.diaMundo >= p.int("prazo")) {
                novo = criar(ctx, p, ref, out)
                regra = "nomeacao-de-simulado"
            }
            if (novo != null) {
                out.addAll(posse(ctx, k, p, novo, tit, ref, regra!!))
                titulares.add(novo)
            } else if (p["vago_desde"] != null) {
                if (!e.vivo(p.str("interino"))) {
                    out.add(DeltaRascunho("campo", k, "interino", interino(ctx, p, titulares), causa(listOf(k), "interino-substituido"), quemSabe = listOf(p.str("reino"))))
                }
            } else {
                val inter = interino(ctx, p, titulares)
                val prazo = ctx.diaMundo + 365 * (din.lim["prazo_vacancia_anos"] ?: 3)
                val campos = listOf<Pair<String, Any?>>("anterior" to (morto ?: "—"), "titular" to "—", "vago_desde" to ctx.diaMundo, "prazo" to prazo, "interino" to inter)
                for ((campo, v) in campos) {
                    out.add(DeltaRascunho("campo", k, campo, v, causa(listOf(morto ?: k), "vacancia"), quemSabe = listOf(p.str("reino"))))
                }
            }
        }
        return out
    }

    private fun livres(ctx: Ctx, reino: String, titulares: Set<String>): List<String> {
        val e = ctx.estado
        return e.ent.keys.sortedWith(Ordem.CODIGO).filter { it.startsWith("npc.") && e.vivo(it) && e.ent.getValue(it)["reino"] == reino && it !in titulares }
    }

    private fun ordem(ctx: Ctx, ids: Collection<String>): List<String> {
        val e = ctx.estado
        return ids.sortedWith(compareBy<String>({ if (e.ent.getValue(it)["principal"] == true) 0 else 1 }).thenComparing(Ordem.CODIGO))
    }

    private fun afin(heir: String, morto: String): Int {
        val strip = ".,;()`"
        val pal = heir.trim().split(Regex("\\s+")).filter { it.isNotEmpty() && it[0].isUpperCase() && it.length > 3 && it != "Coroa" }
            .map { it.trim { c -> c in strip } }.toSet()
        return pal.count { it in morto }
    }

    private fun escolher(ctx: Ctx, p: Map<String, Any?>, morto: String?, ref: Map<String, Any?>, titulares: Set<String>): Pair<String?, String?> {
        val e = ctx.estado
        val livres = livres(ctx, p.str("reino"), titulares)
        if (livres.isEmpty()) return null to null
        if ("governante" in p.str("papel") || "governante" in (ref["papeis"]?.toString() ?: "")) {
            val hs = livres.filter { "herdeiro" in e.ent.getValue(it).str("papeis").split(';') }
            if (hs.isNotEmpty()) {
                val cargoMorto = ref["cargo"]?.toString() ?: ""
                val melhor = hs.maxOf { afin(e.ent.getValue(it).str("cargo"), cargoMorto) }
                if (melhor > 0 || hs.size == 1) {
                    val h = hs.filter { afin(e.ent.getValue(it).str("cargo"), cargoMorto) == melhor }
                    return ordem(ctx, h)[0] to "sucessao-herdeiro"
                }
            }
        }
        if (morto != null) {
            val v = vinculos(ctx.mundo)
            var vs = v.filter { it.second == morto }.map { it.first }.toSet() intersect livres.toSet()
            if (vs.isEmpty()) vs = v.filter { it.first == morto }.map { it.second }.toSet() intersect livres.toSet()
            if (vs.isNotEmpty()) return ordem(ctx, vs)[0] to "sucessao-vice"
        }
        val cand = membros(ctx, p, ref, livres)
        if (cand.isNotEmpty()) return ordem(ctx, cand)[0] to "sucessao-candidato"
        return null to null
    }

    private fun membros(ctx: Ctx, p: Map<String, Any?>, ref: Map<String, Any?>, livres: List<String>): List<String> {
        val e = ctx.estado
        if (p["tipo"] == "faccao") return livres.filter { e.ent.getValue(it)["faccao"] == p["alvo"] }
        if (p["tipo"] == "cidade") return livres.filter { e.ent.getValue(it)["cidade"] == p["alvo"] }
        val c = ref["cidade"]?.toString() ?: "—"
        val f = ref["faccao"]?.toString() ?: "—"
        return livres.filter { (c != "—" && e.ent.getValue(it)["cidade"] == c) || (f != "—" && e.ent.getValue(it)["faccao"] == f) }
    }

    /** Responsável interino: membro livre, senão qualquer livre do reino ou vivo do reino, senão vivo do mundo; "—" se ninguém. */
    private fun interino(ctx: Ctx, p: Map<String, Any?>, titulares: Set<String>): String {
        val e = ctx.estado
        val livres = livres(ctx, p.str("reino"), titulares)
        val c = membros(ctx, p, emptyMap(), livres).ifEmpty { livres }
        if (c.isNotEmpty()) return ordem(ctx, c)[0]
        val chaves = e.ent.keys.sortedWith(Ordem.CODIGO)
        var vivos = chaves.filter { it.startsWith("npc.") && e.vivo(it) && e.ent.getValue(it)["reino"] == p["reino"] }
        if (vivos.isEmpty()) vivos = chaves.filter { it.startsWith("npc.") && e.vivo(it) }
        return if (vivos.isNotEmpty()) ordem(ctx, vivos)[0] else "—"
    }

    private fun mudarTitular(k: String, p: Map<String, Any?>, novo: String, antigos: List<String>, regra: String): List<DeltaRascunho> {
        val q = listOf(p.str("reino"))
        val out = arrayListOf(DeltaRascunho("campo", k, "titular", novo, causa(listOf(antigos.last()), regra), quemSabe = q))
        when (p["tipo"]) {
            "cidade" -> out.add(DeltaRascunho("campo", p.str("alvo"), "governante", novo, causa(listOf(k), regra), quemSabe = q))
            "faccao" -> out.add(DeltaRascunho("campo", p.str("alvo"), "lideranca", novo, causa(listOf(k), regra), quemSabe = q))
        }
        return out
    }

    private fun posse(ctx: Ctx, k: String, p: Map<String, Any?>, novo: String, antigos: List<String>, ref: Map<String, Any?>, regra: String): List<DeltaRascunho> {
        val q = listOf(p.str("reino"))
        val out = ArrayList<DeltaRascunho>()
        if (p["vago_desde"] != null) {
            for ((campo, v) in listOf<Pair<String, Any?>>("vago_desde" to null, "prazo" to null, "interino" to "—", "anterior" to "—")) {
                out.add(DeltaRascunho("campo", k, campo, v, causa(listOf(k), regra), quemSabe = q))
            }
        }
        out.addAll(mudarTitular(k, p, novo, antigos.ifEmpty { listOf(ref["_id"]?.toString() ?: k) }, regra))
        val comAgente = out.map {
            DeltaRascunho(
                it.tipo, it.entidade, it.campo, it.estadoNovo, it.causa,
                agentes = if (it.campo in listOf("titular", "governante", "lideranca")) listOf(novo) else emptyList(), quemSabe = it.quemSabe,
            )
        }.toMutableList()
        val n: Map<String, Any?> = ctx.estado.ent[novo] ?: novos.getValue(novo)
        val base = n.str("papeis").split(';').toSet() - setOf("—", "?", "")
        val add = (ref["papeis"]?.toString() ?: "").split(';').toSet() intersect setOf("governante", "general")
        val papeis = (base + add).sortedWith(Ordem.CODIGO).joinToString(";").ifEmpty { "—" }
        val cargo = ref["cargo"]?.toString()?.takeIf { it.isNotEmpty() } ?: n.str("cargo")
        for ((campo, v) in listOf("cargo" to cargo, "papeis" to papeis)) {
            if (!Valores.iguais(n[campo], v) && novo !in novos) {
                comAgente.add(DeltaRascunho("campo", novo, campo, v, causa(listOf(k), regra), quemSabe = q))
            }
        }
        if (novo in novos) {
            novos.getValue(novo)["cargo"] = cargo
            novos.getValue(novo)["papeis"] = papeis
        }
        return comAgente
    }

    /** NPC simulado (D-M5): só quando o prazo da vacância acaba e ninguém serve. Nome por M-10 (`Nomes`). */
    private fun criar(ctx: Ctx, p: Map<String, Any?>, ref: Map<String, Any?>, out: MutableList<DeltaRascunho>): String {
        val e = ctx.estado
        val rng = ctx.rng
        val w = ctx.mundo
        val racas = w.catalogo.racas.toSet()
        val povos = (w.t.getValue("povo_no_reino").filter { it.getValue("reino") == p["reino"] }.map { it.getValue("povo") }.toSet() intersect racas)
            .sortedWith(Ordem.CODIGO)
        val povo = if (povos.isNotEmpty()) rng.choice(povos) else "humano"
        val subs = w.catalogo.subracas.filter { it.value == povo }.keys.sortedWith(Ordem.CODIGO)
        val sub = if (subs.isNotEmpty()) rng.choice(subs) else "—"
        var cidade = if (p["tipo"] == "cidade") p.str("alvo") else (ref["cidade"]?.toString() ?: "—")
        if (cidade == "—" && p["tipo"] == "faccao") {
            val fr = w.t.getValue("faccao").filter { it.getValue("id") == p["alvo"] }
            val cs = if (fr.isNotEmpty()) TabelaMolde.toks(fr[0].getValue("presenca_cidades")) else emptyList()
            cidade = cs.firstOrNull() ?: "—"
        }
        val classe = din.classeDe(povo, sub)
        val k = din.classes[classe] ?: din.classes.getValue("humana")
        val idade = k.mat + rng.randrange(maxOf(1, k.mad - k.mat)) + k.mat
        val nasc: Int? = if (k.max != 0) ctx.diaMundo - 365 * idade else null
        val nid = "npc.sim-%05d".format(Locale.ROOT, e.nCriados + novos.size + 1)
        val cargo = ref["cargo"]?.toString()?.takeIf { it.isNotEmpty() } ?: "—"
        val papeis = ((ref["papeis"]?.toString() ?: "").split(';').toSet() intersect setOf("governante", "general")).sortedWith(Ordem.CODIGO)
            .joinToString(";").ifEmpty { "—" }
        val novo = linkedMapOf<String, Any?>(
            "vivo" to true, "reino" to p["reino"], "cidade" to cidade, "faccao" to (if (p["tipo"] == "faccao") p["alvo"] else "—"),
            "cargo" to cargo, "papeis" to papeis, "principal" to false, "povo" to povo, "subraca" to sub,
            "nome" to Nomes.gerar(rng, e, novos.values.map { it.str("nome") }),
            "saude" to "sao", "classe" to classe, "nasc_dia" to nasc, "origem_npc" to "nomeacao",
        )
        novos[nid] = novo
        out.add(
            DeltaRascunho(
                "criacao", nid, causa = causa(listOf(p.str("alvo")), "nomeacao-sem-candidato"), quemSabe = listOf(p.str("reino")),
                estadoNovo = LinkedHashMap(novo),
            ),
        )
        return nid
    }
}
