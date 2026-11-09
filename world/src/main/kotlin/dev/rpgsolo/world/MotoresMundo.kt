package dev.rpgsolo.world

private fun Map<String, Any?>.str(k: String): String = this[k].toString()

private fun Map<String, Any?>.int(k: String): Int = Valores.numero(this[k]).toInt()

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.lista(k: String): List<String> = (this[k] as List<Any?>).map { it.toString() }

private fun Estado.chaves(prefixo: String): List<String> = ent.keys.filter { it.startsWith(prefixo) }.sortedWith(Ordem.CODIGO)

private fun dr(
    tipo: String,
    entidade: String,
    campo: String? = null,
    novo: Any? = null,
    causa: Map<String, Any?>? = null,
    agentes: List<String> = emptyList(),
    quemSabe: List<String> = emptyList(),
    certeza: String = "confirmado",
) = DeltaRascunho(tipo, entidade, campo, novo, causa, agentes, quemSabe, certeza)

private fun mapa(vararg p: Pair<String, Any?>): Map<String, Any?> = linkedMapOf(*p)

/** Política de facções (porta de `politica.py`). Anual. */
class Politica(private val din: Dinamica) : Motor {
    override val nome = "politica"
    override val frequencia = Frequencia.ANUAL

    private fun base(rel: String): Int {
        val l = din.lim
        return when (rel) {
            "aliança" -> l["tensao_base_alianca"] ?: 20
            "rivalidade", "rompida" -> l["tensao_base_rivalidade"] ?: 50
            "tratado" -> l["tensao_base_tratado"] ?: 25
            "disputa_aberta" -> l["limiar_disputa_aberta"] ?: 85
            "ambigua" -> l["tensao_inicial_ambigua"] ?: 40
            else -> throw NoSuchElementException(rel)
        }
    }

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        if (din.vazia) return emptyList()
        val l = din.lim
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        val quase = LinkedHashMap<String, Map<String, Any?>>()
        for ((k, c) in e.ent) {
            if (k.startsWith("clock.") && c["estourado"] != true && c.int("progresso") >= c.int("segmentos") - 2) quase[k] = c
        }
        for (k in e.chaves("par.")) {
            val p = e.ent.getValue(k)
            val a = p.str("a")
            val b = p.str("b")
            val rel = p.str("relacao")
            val t = p.int("tensao")
            val rep = listOf(e.ent.getValue(a).str("lideranca"), e.ent.getValue(b).str("lideranca"))
            val ids = arrayListOf(k)
            val bs = base(rel)
            val deriva = l["deriva_anual"] ?: 6
            var dt = ctx.rng.randint(-deriva, deriva)
            if (bs != t) dt += Math.floorDiv((bs - t) * (l["decaimento_anual"] ?: 3), 20)
            for (ck in quase.keys) {
                val rg = din.relogios[ck]
                if (rg != null && a in rg.pressoes && b in rg.pressoes) {
                    dt += l["pressao_relogio"] ?: 3
                    ids.add(ck)
                }
            }
            val novoT = maxOf(0, minOf(l["tensao_maxima"] ?: 100, t + dt))
            val q = listOf(p.str("reino"))
            if (novoT != t) out.add(dr("campo", k, "tensao", novoT, causa(ids.take(3), "tensao-anual"), quemSabe = q))
            var novaRel: String? = null
            var tipoReg: String? = null
            var custo = 0
            if (rel == "aliança" && novoT >= (l["limiar_romper_alianca"] ?: 80)) {
                novaRel = "rompida"
                tipoReg = "ruptura"
                custo = 1
            } else if (rel == "rivalidade" && novoT >= (l["limiar_disputa_aberta"] ?: 85)) {
                novaRel = "disputa_aberta"
                tipoReg = "disputa_aberta"
                custo = 1
            } else if ((rel == "rivalidade" || rel == "ambigua") && novoT <= (l["limiar_tratado"] ?: 15)) {
                novaRel = "tratado"
                tipoReg = "tratado"
            } else if (rel == "disputa_aberta" && novoT <= (l["limiar_tregua"] ?: 55)) {
                novaRel = "rivalidade"
                tipoReg = "tregua"
            } else if (rel == "tratado" && novoT >= (l["limiar_tratado_quebra"] ?: 70)) {
                novaRel = "rivalidade"
                tipoReg = "ruptura"
                custo = 1
            } else if (rel == "rompida") {
                novaRel = "rivalidade"
                tipoReg = "tensao_pos_ruptura"
            }
            if (novaRel != null && tipoReg != null) {
                val cz = causa(listOf(k, a, b), "limiar-de-" + tipoReg.replace('_', '-'))
                out.add(
                    dr(
                        tipoReg,
                        k,
                        causa = cz,
                        quemSabe = q,
                        agentes =
                            rep.filter {
                                e.vivo(it)
                            },
                        novo = mapa("de" to rel, "para" to novaRel, "tensao" to novoT),
                    ),
                )
                out.add(dr("campo", k, "relacao", novaRel, cz, quemSabe = q))
                if (custo != 0) out.add(dr("campo", k, "tensao", maxOf(0, novoT - (l["custo_ruptura"] ?: 25)), cz, quemSabe = q))
            }
        }
        return out
    }
}

/** Economia e rotas (porta de `economia.py`). Sazonal. */
class Economia(private val din: Dinamica) : Motor {
    override val nome = "economia"
    override val frequencia = Frequencia.SAZONAL

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        val ec = din.econ
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        if (din.vazia || ec.isEmpty()) return out
        val inverno = CalendarioReino.estacaoIdx(ctx.diaDoAno) == 0
        val rotas = LinkedHashMap<String, Pair<Map<String, Any?>, String>>()
        for (k in e.chaves("route.")) {
            val r = e.ent.getValue(k)
            val atual = r.str("estado")
            var novo = atual
            val q = listOf(r.str("reino"))
            val cz = listOf(k, r.str("de"), r.str("para"))
            val x = ctx.rng.randrange(1000)
            if (atual == "aberta") {
                var pm = if (inverno) ec["rota_bloqueio_inverno_pm"] ?: 150 else ec["rota_bloqueio_pm"] ?: 40
                if (r["evidencia"] != "documentada") pm += ec["rota_bloqueio_incerta_pm"] ?: 30
                if (x < pm) {
                    novo = "bloqueada"
                    out.add(
                        dr(
                            "bloqueio_de_rota",
                            k,
                            novo = mapa("de" to atual, "para" to novo),
                            quemSabe = q,
                            causa = causa(cz, "estacao-bloqueio-rota"),
                        ),
                    )
                }
            } else if (x < (ec["rota_reabertura_pm"] ?: 450)) {
                novo = "aberta"
                out.add(
                    dr(
                        "rota_reaberta",
                        k,
                        novo = mapa("de" to atual, "para" to novo),
                        quemSabe = q,
                        causa = causa(cz, "estacao-reabertura-rota"),
                    ),
                )
            }
            if (novo != atual) out.add(dr("campo", k, "estado", novo, causa(cz, "estacao-rota"), quemSabe = q))
            rotas[k] = r to novo
        }
        val adj = HashMap<String, MutableSet<String>>()
        val adj0 = HashMap<String, MutableSet<String>>()
        for ((r, est) in rotas.values) {
            val de = r.str("de")
            val pa = r.str("para")
            adj0.getOrPut(de) { HashSet() }.add(pa)
            adj0.getOrPut(pa) { HashSet() }.add(de)
            if (est == "aberta") {
                adj.getOrPut(de) { HashSet() }.add(pa)
                adj.getOrPut(pa) { HashSet() }.add(de)
            }
        }
        val econ = e.chaves("econ.")
        val prod = LinkedHashMap<Pair<String, String>, MutableList<Pair<String, Int>>>()
        for (k in econ) {
            val c = e.ent.getValue(k)
            if (c["papel"] != "produtor") continue
            val q = listOf(c.str("reino"))
            var pa = c.int("producao_atual")
            val x = ctx.rng.randrange(1000)
            var regra: String? = null
            if (pa == c.int("producao") && pa > 0 && x < (ec["econ_quebra_safra_pm"] ?: 80)) {
                pa -= 1
                regra = "quebra-de-safra"
            } else if (pa < c.int("producao") && x < (ec["econ_recuperacao_pm"] ?: 500)) {
                pa += 1
                regra = "recuperacao-de-safra"
            }
            if (regra != null) out.add(dr("campo", k, "producao_atual", pa, causa(listOf(k, c.str("localidade")), regra), quemSabe = q))
            prod.getOrPut(c.str("reino") to c.str("recurso")) { ArrayList() }.add(c.str("localidade") to pa)
        }
        val alcance = ec["econ_alcance_passos"] ?: 3
        val cache = HashMap<Pair<String, Boolean>, Set<String>>()

        fun alcancados(
            loc: String,
            g: Map<String, Set<String>>,
            aberto: Boolean,
        ): Set<String> =
            cache.getOrPut(loc to aberto) {
                val vis = hashSetOf(loc)
                var fila = listOf(loc)
                repeat(alcance) {
                    fila = fila.flatMap { x -> (g[x] ?: emptySet()).sortedWith(Ordem.CODIGO) }.filter { it !in vis }
                    vis.addAll(fila)
                }
                vis
            }
        val escMax = ec["econ_escassez_max"] ?: 3
        val impMax = ec["econ_import_max"] ?: 2
        for (k in econ) {
            val c = e.ent.getValue(k)
            if (c.int("demanda") <= 0) continue
            val loc = c.str("localidade")
            val ps = prod[c.str("reino") to c.str("recurso")] ?: emptyList()
            val propria = ps.filter { it.first == loc }.sumOf { it.second }
            val viz = alcancados(loc, adj, true)
            val recebida = minOf(impMax, ps.filter { it.first != loc && it.first in viz }.sumOf { it.second })
            val kp = "econ.${c.str("recurso")}@$loc"
            val pcheia = e.ent[kp]?.takeIf { it["papel"] == "produtor" }?.int("producao") ?: 0
            val vz0 = alcancados(loc, adj0, false)
            val rcheia =
                minOf(
                    impMax,
                    econ.map { e.ent.getValue(it) }.filter {
                        it["recurso"] == c["recurso"] && it["reino"] == c["reino"] && it["papel"] == "produtor" && it["localidade"] != loc && it.str("localidade") in vz0
                    }.sumOf { it.int("producao") },
                )
            val esc = maxOf(0, minOf(escMax, (pcheia + rcheia) - (propria + recebida)))
            val q = listOf(c.str("reino"))
            val cr = c.int("estacoes_criticas")
            val ids = listOf(k, loc)
            if (esc != c.int("escassez")) out.add(dr("campo", k, "escassez", esc, causa(ids, "balanco-sazonal"), quemSabe = q))
            var ncr = if (esc >= escMax) cr + 1 else 0
            if (ncr >= (ec["econ_revolta_estacoes"] ?: 4)) {
                val gov = e.ent[loc]?.get("governante")?.toString() ?: ""
                out.add(
                    dr(
                        "revolta",
                        loc,
                        quemSabe = q,
                        novo = mapa("recurso" to c["recurso"], "escassez" to esc),
                        agentes = gov.split(';').filter { it.isNotEmpty() && e.vivo(it) },
                        causa = causa(ids, "escassez-prolongada"),
                    ),
                )
                ncr = 0
            }
            if (ncr != cr) out.add(dr("campo", k, "estacoes_criticas", ncr, causa(ids, "escassez-prolongada"), quemSabe = q))
        }
        return out
    }
}

/** Ecos (porta de `ecos.py`). Diária. */
class Ecos(private val din: Dinamica) : Motor {
    override val nome = "ecos"
    override val frequencia = Frequencia.DIARIA

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        val e = ctx.estado
        val ec = din.ecos
        if (din.vazia || ec.isEmpty() || e.ecosAtivos.isEmpty()) return emptyList()
        val atraso = ec["eco_atraso_dias"] ?: 12

        @Suppress("UNCHECKED_CAST")
        fun dif(k: String) = e.ent.getValue(k)["difusao"] as Map<String, Any?>
        val devidos =
            e.ecosAtivos.sortedWith(Ordem.CODIGO).filter {
                ctx.diaMundo == e.ent.getValue(it).int("inicio") + (dif(it).int("passo") + 1) * atraso
            }
        if (devidos.isEmpty()) return emptyList()
        val adj = HashMap<String, MutableList<Pair<String, String>>>()
        for (k in e.chaves("route.")) {
            val r = e.ent.getValue(k)
            if (r["estado"] == "aberta") {
                adj.getOrPut(r.str("de")) { ArrayList() }.add(r.str("para") to k)
                adj.getOrPut(r.str("para")) { ArrayList() }.add(r.str("de") to k)
            }
        }
        val out = ArrayList<DeltaRascunho>()
        for (k in devidos) {
            val c = e.ent.getValue(k)
            val df = dif(k)
            val p = df.int("passo") + 1
            val pm = ec["eco_prob_${c.str("camada")}_pm"] ?: 450
            val sabem = df.lista("lugares").toSet()
            val novos = ArrayList<String>()
            var via: String? = null
            for (l in sabem.sortedWith(Ordem.CODIGO)) {
                for ((viz, rota) in (adj[l] ?: emptyList<Pair<String, String>>()).sortedWith(Ordem.PARES)) {
                    if (viz !in sabem && viz !in novos && ctx.rng.randrange(1000) < pm) {
                        novos.add(viz)
                        if (via == null) via = rota
                    }
                }
            }
            val ordem = listOf("confirmado", "alegado", "rumor")
            val base = ordem.indexOf(df.str("certeza")).let { if (it >= 0) it else ordem.size - 1 }
            val cert = ordem[minOf(ordem.size - 1, base + p / maxOf(1, ec["eco_passos_por_degrau"] ?: 2))]
            val todos = (sabem + novos).sortedWith(Ordem.CODIGO)
            val fronteira = todos.any { l -> (adj[l] ?: emptyList<Pair<String, String>>()).any { it.first !in todos } }
            val ativo = p < (ec["eco_max_passos"] ?: 8) && fronteira
            out.add(
                dr(
                    "campo",
                    k,
                    "difusao",
                    mapa("passo" to p, "lugares" to todos.toMutableList(), "certeza" to cert, "ativo" to ativo),
                    causa(listOf(k) + listOfNotNull(via) + c.str("origem_delta"), "difusao-de-eco"),
                    quemSabe = if (novos.isNotEmpty()) novos else listOf(c.str("reino")),
                    certeza = cert,
                ),
            )
        }
        return out
    }
}

/** Mistérios (porta de `misterios.py`). Anual. */
class Misterios(private val din: Dinamica) : Motor {
    override val nome = "misterios"
    override val frequencia = Frequencia.ANUAL

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        val m0 = din.mist
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        if (din.vazia || m0.isEmpty()) return out
        for (k in e.chaves("mist.")) {
            val m = e.ent.getValue(k)
            if (m["estado"] != "aberto" || m.int("pistas") >= (m0["mist_max_pistas"] ?: 6)) continue
            if (ctx.rng.randrange(1000) >= (m0["mist_pista_pm"] ?: 70)) continue
            val hip = (ctx.mundo.linha(k)?.get("hipoteses_abertas") ?: "").split(';').map { it.trim() }.filter { it.isNotEmpty() }
            val hi = if (hip.isNotEmpty()) ctx.rng.randrange(hip.size) else null
            val n = m.int("pistas") + 1
            val cz = causa(listOf(k), "pista-sem-agente")
            val q = listOf(m.str("reino"))
            out.add(
                dr(
                    "pista_de_misterio",
                    k,
                    certeza = "suspeita",
                    quemSabe = q,
                    causa = cz,
                    novo = mapa("pista" to n, "em_torno_de" to (if (hi != null) hip[hi] else "?")),
                ),
            )
            out.add(dr("campo", k, "pistas", n, cz, quemSabe = q))
        }
        return out
    }
}

private fun trocaRegra(
    c: Map<String, Any?>?,
    regra: String,
): Map<String, Any?> =
    LinkedHashMap(c ?: emptyMap()).also {
        it["regra"] = regra
    }

/** Diplomacia (porta de `diplomacia.py`). Anual; só fora do regime selado. */
class Diplomacia(private val din: Dinamica) : Motor {
    override val nome = "diplomacia"
    override val frequencia = Frequencia.ANUAL

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        val d = din.dipl
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        if (din.vazia || d.isEmpty() || e.mundo["regime"] == "selado") return out
        val dia = ctx.diaMundo
        val tmax = din.front["front_tensao_max"] ?: 400
        for (k in e.chaves("front.")) {
            val f = e.ent.getValue(k)
            val qs = f.lista("reinos")
            val cz = causa(listOf(k), "diplomacia-de-fronteira")

            fun campos(
                rel: String,
                tensao: Int,
                pend: Boolean = false,
            ): List<DeltaRascunho> {
                val r =
                    arrayListOf(
                        dr("campo", k, "relacao", rel, cz, quemSabe = qs),
                        dr("campo", k, "relacao_dia", dia, cz, quemSabe = qs),
                    )
                if (tensao != f.int("tensao")) r.add(dr("campo", k, "tensao", tensao, cz, quemSabe = qs))
                if (pend) r.add(dr("campo", k, "pendente", false, cz, quemSabe = qs))
                return r
            }
            val rel = f.str("relacao")
            if (rel == "guerra") continue
            if (rel == "tratado" && dia >= f.int("relacao_dia") + (d["dipl_tratado_anos"] ?: 30) * 365) {
                out.addAll(campos("neutra", f.int("tensao")))
                continue
            }
            if (rel == "ruptura" && dia >= f.int("relacao_dia") + (d["dipl_ruptura_anos"] ?: 12) * 365) {
                out.addAll(campos("neutra", f.int("tensao")))
                continue
            }
            if (rel == "tratado" && ctx.rng.randrange(1000) < (d["dipl_quebra_pm"] ?: 25)) {
                out.add(
                    dr("quebra_de_contrato", k, quemSabe = qs, causa = trocaRegra(cz, "tratado-quebrado"), novo = mapa("de" to "tratado")),
                )
                out.addAll(campos("ruptura", minOf(tmax, f.int("tensao") + (d["dipl_ruptura_soma"] ?: 60))))
                continue
            }
            if (f["pendente"] != true) continue
            if (dia - f.int("ultimo_dia") > (d["dipl_janela_anos"] ?: 5) * 365) {
                out.add(dr("campo", k, "pendente", false, trocaRegra(cz, "incidente-sem-resposta"), quemSabe = qs))
                continue
            }
            val r = ctx.rng.randrange(1000)
            val pt = d["dipl_tratado_pm"] ?: 350
            if (r < pt) {
                val ags = qs.mapNotNull { agente(e, it) }
                out.add(
                    dr(
                        "tratado",
                        k,
                        quemSabe = qs,
                        agentes = ags,
                        causa = trocaRegra(cz, "tratado-de-fronteira"),
                        novo = mapa("duracao_anos" to (d["dipl_tratado_anos"] ?: 30)),
                    ),
                )
                out.addAll(campos("tratado", maxOf(0, f.int("tensao") - (d["dipl_tratado_reduz"] ?: 120)), true))
            } else if (r < pt + (d["dipl_ruptura_pm"] ?: 200)) {
                out.add(
                    dr("ruptura", k, quemSabe = qs, causa = trocaRegra(cz, "ruptura-de-fronteira"), novo = mapa("tensao" to f["tensao"])),
                )
                out.addAll(campos("ruptura", minOf(tmax, f.int("tensao") + (d["dipl_ruptura_soma"] ?: 60)), true))
            }
        }
        return out
    }

    companion object {
        /** Governante vivo de uma cidade do reino (porta de `Diplomacia._agente`). */
        fun agente(
            e: Estado,
            reino: String,
            excluir: Set<String> = emptySet(),
        ): String? {
            for (k in e.chaves("city.").filter { e.ent.getValue(it)["reino"] == reino }) {
                for (g0 in (e.ent.getValue(k)["governante"]?.toString() ?: "").replace(',', ';').split(';')) {
                    val g = g0.trim()
                    if (g !in excluir && g in e.ent && e.vivo(g)) return g
                }
            }
            return null
        }
    }
}

/** Fronteira (porta de `fronteira.py`). Anual. */
class Fronteira(private val din: Dinamica) : Motor {
    override val nome = "fronteira"
    override val frequencia = Frequencia.ANUAL

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        val fr = din.front
        val d = din.dipl
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        if (din.vazia || fr.isEmpty()) return out
        var primeiro: String? = null
        for (k in e.chaves("front.")) {
            val f = e.ent.getValue(k)
            if (f["relacao"] == "guerra") continue
            val reinos = f.lista("reinos")
            val ids =
                reinos.flatMap {
                        r ->
                    e.chaves("clock.").filter { e.ent.getValue(it)["reino"] == r && e.ent.getValue(it)["estourado"] == true }
                }
            val g = (fr["front_tensao_base"] ?: 1) + (fr["front_tensao_por_relogio"] ?: 1) * ids.size + ctx.rng.randrange((fr["front_tensao_jitter"] ?: 3) + 1)
            val fator =
                when (f["relacao"]) {
                    "tratado" -> d["dipl_tratado_fator_pm"] ?: 1000
                    "ruptura" -> d["dipl_ruptura_fator_pm"] ?: 1000
                    else -> 1000
                }
            val t = minOf(f.int("tensao") + Math.floorDiv(g * fator, 1000), fr["front_tensao_max"] ?: 400)
            val cz = causa(listOf(k) + ids.take(3), "tensao-de-fronteira")
            if (t >= (fr["front_tensao_limiar"] ?: 300)) {
                out.add(
                    dr(
                        "incidente_fronteira",
                        k,
                        quemSabe = reinos,
                        causa = cz,
                        novo = mapa("tensao" to t, "relogios_estourados" to ids.size),
                    ),
                )
                out.add(dr("campo", k, "tensao", fr["front_tensao_pos_incidente"] ?: 150, cz, quemSabe = reinos))
                out.add(dr("campo", k, "incidentes", f.int("incidentes") + 1, cz, quemSabe = reinos))
                out.add(dr("campo", k, "ultimo_dia", ctx.diaMundo, cz, quemSabe = reinos))
                if (f["pendente"] != true) out.add(dr("campo", k, "pendente", true, cz, quemSabe = reinos))
                if (primeiro == null) primeiro = k
            } else {
                out.add(dr("campo", k, "tensao", t, cz, quemSabe = reinos))
            }
        }
        val lim = fr["fronteira_limite_anos"]
        if (e.mundo["regime"] != "selado") return out
        val cz: Map<String, Any?> =
            if (primeiro != null) {
                causa(listOf(primeiro), "primeiro-incidente-de-fronteira")
            } else if (lim != null && lim != 0 && ctx.diaMundo >= lim * 365) {
                causa(listOf("mundo.mythralis"), "limite-de-anos-sem-gatilho")
            } else {
                return out
            }
        out.add(
            dr(
                "era",
                "mundo",
                quemSabe = listOf("mundo.mythralis"),
                causa = cz,
                novo = mapa("era" to "era.002", "nome" to "Fronteiras Porosas", "regime" to "poroso"),
            ),
        )
        return out
    }
}

/** Guerra (porta de `guerra.py`). Anual; só fora do regime selado. */
class Guerra(private val din: Dinamica) : Motor {
    override val nome = "guerra"
    override val frequencia = Frequencia.ANUAL

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        val g = din.guerra
        val d = din.dipl
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        if (din.vazia || g.isEmpty() || e.mundo["regime"] == "selado") return out
        val dia = ctx.diaMundo
        var regime = e.mundo["regime"]
        var nEra = e.eras.size
        val mortos = HashSet<String>()

        fun era(
            nome: String,
            regimeNovo: Any?,
            cz: Map<String, Any?>,
        ): DeltaRascunho {
            nEra++
            return dr(
                "era",
                "mundo",
                quemSabe = listOf("mundo.mythralis"),
                causa = cz,
                novo = mapa("era" to "era.%03d".format(java.util.Locale.ROOT, nEra), "nome" to nome, "regime" to regimeNovo),
            )
        }
        for (k in e.chaves("front.")) {
            val f = e.ent.getValue(k)
            val qs = f.lista("reinos")
            val cz = causa(listOf(k), "guerra-de-fronteira")
            val coroas = qs.map { "crown." + it.substring(8) }

            fun campos(
                rel: String,
                tensao: Int,
                pend: Boolean = false,
            ): List<DeltaRascunho> {
                val r =
                    arrayListOf(
                        dr("campo", k, "relacao", rel, cz, quemSabe = qs),
                        dr("campo", k, "relacao_dia", dia, cz, quemSabe = qs),
                    )
                if (tensao != f.int("tensao")) r.add(dr("campo", k, "tensao", tensao, cz, quemSabe = qs))
                if (pend) r.add(dr("campo", k, "pendente", false, cz, quemSabe = qs))
                return r
            }
            if (f["relacao"] == "guerra") {
                for (q in qs) {
                    val cands =
                        e.chaves("npc.").filter {
                            val v = e.ent.getValue(it)
                            it !in mortos && v["reino"] == q && v["vivo"] == true &&
                                (Valores.verdadeiro(v["principal"]) || "general" in (v["papeis"]?.toString() ?: "").split(';'))
                        }
                    if (cands.size >= (g["guerra_baixa_min_vivos"] ?: 4) && ctx.rng.randrange(1000) < (g["guerra_baixa_pm"] ?: 150)) {
                        val vitima = cands[ctx.rng.randrange(cands.size)]
                        mortos.add(vitima)
                        out.add(dr("campo", vitima, "vivo", false, trocaRegra(cz, "morte-em-guerra"), quemSabe = listOf(q)))
                    }
                }
                var caiu: Pair<String, String>? = null
                if (dia >= f.int("relacao_dia") + (g["queda_min_anos"] ?: 3) * 365) {
                    for ((q, c) in qs.zip(coroas)) {
                        if (c in e.ent && e.ent.getValue(c)["estado"] == "firme" && ctx.rng.randrange(1000) < (g["queda_pm"] ?: 20)) {
                            caiu = q to c
                            break
                        }
                    }
                }
                if (caiu != null) {
                    val (_, c) = caiu
                    val cc = causa(listOf(k, c), "queda-de-coroa-em-guerra")
                    out.add(dr("queda_de_coroa", c, quemSabe = qs, causa = cc, novo = mapa("guerra" to k)))
                    out.add(dr("campo", c, "estado", "caida", cc, quemSabe = qs))
                    out.add(dr("campo", c, "caida_dia", dia, cc, quemSabe = qs))
                    out.addAll(campos("neutra", g["guerra_pos_tensao"] ?: 150))
                    out.add(era("Queda da Coroa de ${ctx.mundo.reinos.getValue(caiu.first).linha.getValue("nome")}", regime, cc))
                } else if (dia >= f.int("relacao_dia") + (g["guerra_anos"] ?: 6) * 365) {
                    val ags = qs.mapNotNull { Diplomacia.agente(e, it, mortos) }
                    out.add(
                        dr(
                            "tratado",
                            k,
                            quemSabe = qs,
                            agentes = ags,
                            causa = trocaRegra(cz, "paz-apos-guerra"),
                            novo = mapa("paz" to true, "duracao_anos" to (d["dipl_tratado_anos"] ?: 30)),
                        ),
                    )
                    out.addAll(campos("tratado", g["guerra_pos_tensao"] ?: 150))
                }
                continue
            }
            if (f["relacao"] == "ruptura" && f.int("tensao") >= (g["guerra_tensao_min"] ?: 330) &&
                coroas.all { it in e.ent && e.ent.getValue(it)["estado"] == "firme" } &&
                ctx.rng.randrange(1000) < (g["guerra_pm"] ?: 80)
            ) {
                out.add(dr("guerra", k, quemSabe = qs, causa = cz, novo = mapa("tensao" to f["tensao"])))
                out.addAll(campos("guerra", f.int("tensao"), f["pendente"] == true))
                if (regime != "aberto") {
                    out.add(era("Fronteiras Abertas", "aberto", causa(listOf(k), "primeira-guerra")))
                    regime = "aberto"
                }
            }
        }
        return out
    }
}

/** Povos (porta de `povos.py`). Anual. */
class Povos(private val din: Dinamica) : Motor {
    override val nome = "povos"
    override val frequencia = Frequencia.ANUAL

    private fun governante(
        e: Estado,
        cid: String,
    ): String? {
        for (g0 in (e.ent.getValue(cid)["governante"]?.toString() ?: "").replace(',', ';').split(';')) {
            val g = g0.trim()
            if (g in e.ent && e.vivo(g)) return g
        }
        return null
    }

    private fun eh(
        n: Map<String, Any?>,
        povo: String,
    ) = n["povo"] == povo || n["subraca"] == povo

    override fun executar(ctx: Ctx): List<DeltaRascunho> {
        val p = din.povos
        val e = ctx.estado
        val out = ArrayList<DeltaRascunho>()
        if (din.vazia || p.isEmpty()) return out
        val dia = ctx.diaMundo
        val selado = e.mundo["regime"] == "selado"
        val ano = Math.floorDiv(dia, 365)
        val passo = maxOf(1, p["povos_passo_anos"] ?: 5)
        val emGuerra = HashSet<String>()
        for ((k, f) in e.ent) if (k.startsWith("front.") && f["relacao"] == "guerra") emGuerra.addAll(f.lista("reinos"))
        val escassez =
            e.ent.filter {
                    (k, x) ->
                k.startsWith("econ.") && x.int("escassez") >= 3
            }.values.map { it.str("localidade") }.toSet()
        val medP = setOf("heroi", "guardiao", "benfeitor")
        val mediadores = LinkedHashMap<String, MutableList<String>>()
        for (k in e.chaves("npc.")) {
            val n = e.ent.getValue(k)
            if (n["vivo"] == true &&
                (n["papeis"]?.toString() ?: "").split(';').any {
                    it in medP
                }
            ) {
                mediadores.getOrPut(n["cidade"].toString()) { ArrayList() }.add(k)
            }
        }
        val pactos = e.ent.filterKeys { it.startsWith("pacto.") }
        val lacos = e.chaves("laco.")
        for ((i, k) in lacos.withIndex()) {
            if ((ano + i) % passo != 0) continue
            val l = e.ent.getValue(k)
            val cid = l.str("cidade")
            val reino = l.str("reino")
            val pk = "pacto.${l.str("a")}+${l.str("b")}"
            val meds = (mediadores[cid] ?: emptyList<String>()).filter { eh(e.ent.getValue(it), l.str("a")) || eh(e.ent.getValue(it), l.str("b")) }
            val base = p.getValue("povos_tensao_base")
            var t = l.int("tensao")
            val dec = p.getValue("povos_decaimento")
            t += maxOf(-dec, minOf(dec, base - t))
            val dv = p.getValue("povos_deriva")
            t += ctx.rng.randint(-dv, dv)
            val posto = e.ent["posto.$cid"]
            if (posto != null && posto["vago_desde"] != null) t += p.getValue("povos_pressao_vacancia")
            if (cid in escassez) t += p.getValue("povos_pressao_escassez")
            if (reino in emGuerra) t += p.getValue("povos_pressao_guerra")
            t -= p.getValue("povos_alivio_mediador") * minOf(p.getValue("povos_alivio_mediador_max"), meds.size)
            if (pk in pactos && pactos.getValue(pk)["estado"] == "alianca") t -= p.getValue("povos_alivio_pacto")
            t = maxOf(0, minOf(100, t))
            val rel = l.str("relacao")
            var novo = rel
            var pos = t
            var reg: String? = null
            var ags: List<String> = emptyList()
            val qs = listOf(reino, cid)
            val cz = causa(listOf(k, cid), "laco-de-povos")
            if (rel == "neutra" && t <= p.getValue("povos_lim_aliar")) {
                val gov = governante(e, cid)
                if (gov != null) {
                    novo = "alianca"
                    pos = p.getValue("povos_pos_alianca")
                    reg = "laco_aliado"
                    ags = listOf(gov) + meds.take(2)
                }
            } else if (rel == "neutra" && t >= p.getValue("povos_lim_rival")) {
                novo = "rivalidade"
                reg = "laco_rival"
            } else if (rel == "alianca" && t >= p.getValue("povos_lim_romper")) {
                novo = "neutra"
                pos = p.getValue("povos_pos_ruptura")
                reg = "laco_rompido"
            } else if (rel == "rivalidade" && t <= p.getValue("povos_lim_rival_fim")) {
                novo = "neutra"
            }
            if (reg != null) {
                out.add(
                    dr(
                        reg,
                        k,
                        agentes = ags,
                        quemSabe = qs,
                        causa = cz,
                        novo = mapa("cidade" to cid, "a" to l["a"], "b" to l["b"], "de" to rel, "para" to novo),
                    ),
                )
            }
            if (novo != rel) {
                out.add(dr("campo", k, "relacao", novo, cz, agentes = ags, quemSabe = qs))
                out.add(dr("campo", k, "desde", dia, cz, quemSabe = qs))
            }
            if (pos != l.int("tensao")) out.add(dr("campo", k, "tensao", pos, cz, quemSabe = qs))
        }
        val mudou = HashMap<String, Any?>()
        for (d in out) if (d.tipo == "campo" && d.campo == "relacao") mudou[d.entidade] = d.estadoNovo
        val minCid = p["povos_pacto_min_cidades"] ?: 2
        for (pk in pactos.keys.sortedWith(Ordem.CODIGO)) {
            val pc = pactos.getValue(pk)
            val porReino = java.util.TreeMap<String, MutableList<String>>(Ordem.CODIGO)
            for (cid in pc.lista("cidades")) {
                val lk = "laco.$cid@${pc.str("a")}+${pc.str("b")}"
                val rel = if (lk in mudou) mudou[lk] else e.ent.getValue(lk)["relacao"]
                if (rel == "alianca") porReino.getOrPut(e.ent.getValue(cid).str("reino")) { ArrayList() }.add(cid)
            }
            val nTotal = porReino.values.sumOf { it.size }
            val n = if (selado) porReino.values.maxOfOrNull { it.size } ?: 0 else nTotal
            val qs = if (porReino.isNotEmpty()) porReino.keys.toList() else pc.lista("reinos")
            val cz = causa(listOf(pk) + porReino.values.flatten().sortedWith(Ordem.CODIGO).take(6), "pacto-entre-povos")
            if (pc["estado"] == "nenhum" && n >= minCid) {
                val alvo =
                    if (selado) {
                        porReino.keys.sortedWith {
                                x,
                                y,
                            ->
                            porReino.getValue(y).size.compareTo(porReino.getValue(x).size).let {
                                    c ->
                                if (c != 0) c else Ordem.comparar(x, y)
                            }
                        }.take(1)
                    } else {
                        porReino.keys.toList()
                    }
                val cids = alvo.flatMap { porReino.getValue(it) }.sortedWith(Ordem.CODIGO)
                val ags = ArrayList<String>()
                for (c in cids) {
                    val g = governante(e, c)
                    if (g != null && g !in ags) ags.add(g)
                    ags.addAll(
                        (mediadores[c] ?: emptyList<String>()).filter {
                                m ->
                            (eh(e.ent.getValue(m), pc.str("a")) || eh(e.ent.getValue(m), pc.str("b"))) && m !in ags
                        }.take(1),
                    )
                }
                val ag6 = ags.take(6)
                out.add(
                    dr(
                        "alianca_de_povos",
                        pk,
                        agentes = ag6,
                        quemSabe = alvo,
                        causa = cz,
                        novo = mapa("a" to pc["a"], "b" to pc["b"], "cidades" to cids.toMutableList()),
                    ),
                )
                out.add(dr("campo", pk, "estado", "alianca", cz, agentes = ag6, quemSabe = alvo))
                out.add(dr("campo", pk, "desde", dia, cz, quemSabe = alvo))
            } else if (pc["estado"] == "alianca" && n < minCid) {
                val rs = pc.lista("reinos")
                val c2 = causa(listOf(pk), "pacto-sem-lacos-suficientes")
                out.add(dr("fim_de_alianca_de_povos", pk, quemSabe = rs, causa = c2, novo = mapa("a" to pc["a"], "b" to pc["b"])))
                out.add(dr("campo", pk, "estado", "nenhum", c2, quemSabe = rs))
                out.add(dr("campo", pk, "desde", dia, c2, quemSabe = rs))
            }
        }
        return out
    }
}

/** Todos os motores, na ordem de `motores/__init__.py::criar`. */
object Motores {
    fun pessoas(din: Dinamica): List<Motor> = listOf(Demografia(din), Sucessao(din), Relogios(din), Eventos(din))

    fun todos(din: Dinamica): List<Motor> =
        pessoas(din) +
            listOf(
                Politica(din), Economia(din), Ecos(din), Misterios(din), Diplomacia(din), Fronteira(din), Guerra(din), Povos(din),
            )
}
