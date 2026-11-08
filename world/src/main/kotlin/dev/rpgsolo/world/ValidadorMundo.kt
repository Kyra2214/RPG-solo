package dev.rpgsolo.world

/** Uma constatação do validador: código (W-00 a W-09), gravidade (`E` erro, `A` aviso), entidade e mensagem. */
data class Achado(val codigo: String, val gravidade: String, val entidade: String, val mensagem: String)

/**
 * Validador do mundo (porta de `validador.py` e `validar_mundo.py`): confere o que só aparece com os cinco reinos
 * carregados. Mesmas regras, mesmos textos e mesma ordem do Python, para o relatório sair idêntico ao
 * `saidas/validacao-mundo.txt`.
 */
object ValidadorMundo {
    private val REFERENCIA = Regex("^(?:npc|city|faction|settlement|clock|event|route|kingdom)\\.[a-z0-9.-]+$")
    private val REFERENCIA_RELACAO = Regex("^(?:npc|city|faction|settlement)\\.")

    fun validar(
        w: MundoMolde,
        din: DinamicaMinima? = null,
    ): List<Achado> {
        val f = ArrayList<Achado>()

        fun achar(
            codigo: String,
            gravidade: String,
            entidade: String,
            mensagem: String,
        ) {
            f.add(Achado(codigo, gravidade, entidade, mensagem))
        }

        // W-00 carga
        for (e in w.erros) achar("W-00", "E", "${e.pasta}/${e.tipo}", e.mensagem)
        if (w.reinos.size != 5) achar("W-00", "E", "mundo", "esperados 5 reinos, carregados ${w.reinos.size}")

        // W-01 esquema único
        for ((tipo, por) in w.cab) {
            if (por.values.toSet().size > 1) {
                val campos = por.entries.sortedBy { it.key }.joinToString(", ") { "${it.key.take(2)}=${it.value.size} campos" }
                achar("W-01", "E", tipo, "cabeçalhos diferentes entre reinos: $campos")
            }
        }

        // W-02 IDs únicos no mundo
        for ((tipo, prefixo) in CarregadorMolde.PREFIXO) {
            val contagem = LinkedHashMap<String, Int>()
            for (r in w.t.getValue(tipo)) contagem.merge(r.getValue("id"), 1, Int::plus)
            for ((id, n) in contagem) {
                if (n > 1) achar("W-02", "E", id, "ID duplicado no mundo ($tipo, $n vezes)")
            }
            for (r in w.t.getValue(tipo)) {
                if (!r.getValue("id").startsWith(prefixo)) achar("W-02", "E", r.getValue("id"), "prefixo errado para $tipo")
            }
        }

        // W-03 calendários
        val calendarios = LinkedHashMap<String, CalendarioReino>()
        for (c in w.t.getValue("calendario")) calendarios[c.getValue("id")] = CalendarioReino(c)
        val porReino = w.reinos.mapValues { (_, reino) -> calendarios[reino.linha.getValue("calendario")] }
        for ((reinoId, c) in porReino) {
            if (c == null) {
                achar("W-03", "E", reinoId, "reino sem calendário carregado")
            } else if (!c.valido()) {
                achar("W-03", "E", c.id, "calendário inválido (dias, estações ou ano inicial)")
            }
        }
        if (porReino.values.filterNotNull().map { it.id }.toSet().size < w.reinos.size) {
            achar("W-03", "A", "mundo", "reinos compartilham calendário")
        }

        // W-04 catálogo de personagem
        val cat = w.catalogo
        val racas = cat.racas.toSet()
        val excecoes = w.t.getValue("excecoes").map { it.getValue("regra") to it.getValue("entidade") }.toSet()
        for (r in w.t.getValue("npc")) {
            val id = r.getValue("id")
            if (("V-12" to id) in excecoes) continue
            val povo = r.getValue("povo")
            if (povo !in racas) achar("W-04", "E", id, "povo fora do catálogo: $povo")
            val subraca = r.getValue("subraca")
            if (subraca != "—") {
                val mae = cat.subracas[subraca]
                if (mae == null) {
                    achar("W-04", "E", id, "sub-raça fora do catálogo: $subraca")
                } else if (mae != povo) {
                    achar("W-04", "E", id, "sub-raça não pertence ao povo")
                }
            }
            val classe = r.getValue("classe")
            if (classe !in cat.classes && !(classe == "—" && r.getValue("vivo") == "não")) {
                achar("W-04", "E", id, "classe fora do catálogo: $classe")
            }
            val profissao = r.getValue("profissao")
            if (profissao !in cat.profissoes && profissao != "—") achar("W-04", "E", id, "profissão fora do catálogo: $profissao")
        }

        // W-05 cobertura de povos e sub-raças
        val nativo = LinkedHashMap<String, MutableSet<String>>()
        for (r in w.t.getValue("povo_no_reino")) nativo.getOrPut(r.getValue("povo")) { LinkedHashSet() }.add(r.getValue("reino"))
        for (p in racas.filter { it !in nativo }.sorted()) achar("W-05", "E", p, "raça sem lugar em nenhum reino (povo_no_reino)")
        for (p in nativo.keys) {
            if (p !in racas) achar("W-05", "E", p, "povo_no_reino aponta para raça fora do catálogo")
        }
        val blob = StringBuilder()
        for (tipo in listOf("cidade", "npc", "faccao", "resumo", "fato", "povo_no_reino")) {
            for (r in w.t.getValue(tipo)) blob.append(r.values.joinToString(" ")).append(' ')
        }
        val usadas = cat.subracas.keys.filter { palavraEm(it, blob) }.toSet()
        for (s in (cat.subracas.keys - usadas).sorted()) {
            val sufixo = if (s in w.subracasEmProsa) "; aparece só em prosa dos reinos" else "; não aparece em nenhum reino"
            achar("W-05", "A", s, "sub-raça sem NPC nem cidade que a use$sufixo")
        }

        // W-06 referências no mundo todo (cruzar reinos é permitido; ID inexistente não)
        val ids = w.indice.keys

        fun refs(
            campo: String,
            entidade: String,
            valor: String,
        ) {
            for (t in TabelaMolde.toks(valor)) {
                if (REFERENCIA.matches(t) && t !in ids) {
                    achar("W-06", if (("V-10" to entidade) in excecoes) "A" else "E", entidade, "$campo aponta para ID inexistente: $t")
                }
            }
        }

        for (r in w.t.getValue("relacao")) {
            for (k in listOf("origem", "destino")) {
                val ponta = r.getValue(k)
                if (REFERENCIA_RELACAO.containsMatchIn(ponta) && ponta !in ids) {
                    achar("W-06", "E", r.getValue("origem"), "relação com $k inexistente: $ponta")
                }
            }
        }
        for (r in w.t.getValue("relogio")) refs("relogio.entidades", r.getValue("id"), r.getValue("entidades"))
        for (r in w.t.getValue("evento")) {
            for (k in listOf("relogio", "cidades_e_assentamentos", "pessoas_e_faccoes", "rotas")) {
                refs("evento.$k", r.getValue("id"), r.getValue(k))
            }
        }
        val cruzam =
            w.t.getValue("relacao").count { r ->
                val de = w.reinoDe(r.getValue("origem"))
                val para = w.reinoDe(r.getValue("destino"))
                r.getValue("destino") in ids && de != null && para != null && de != para
            }
        if (cruzam > 0) achar("W-06", "A", "mundo", "$cruzam relações cruzam reinos (entram só pelos contratos de fronteira na Era I)")

        // W-07 pronto para simular
        val npcs = LinkedHashMap<String, Linha>()
        for (r in w.t.getValue("npc")) npcs[r.getValue("id")] = r
        for (r in w.t.getValue("cidade")) {
            val g = r.getValue("governante")
            if (g in npcs && npcs.getValue(g).getValue("vivo") != "sim") achar("W-07", "E", r.getValue("id"), "governante morto: $g")
        }
        for (r in w.t.getValue("faccao")) {
            val lider = r.getValue("lideranca")
            if (lider in npcs && npcs.getValue(lider).getValue("vivo") != "sim") {
                achar(
                    "W-07",
                    "E",
                    r.getValue("id"),
                    "liderança morta: $lider",
                )
            }
        }
        val semLugar =
            w.t.getValue("npc")
                .filter { it.getValue("vivo") == "sim" && it.getValue("cidade") == "—" && it.getValue("faccao") == "—" }
                .map { it.getValue("id") }
        if (semLugar.isNotEmpty()) {
            val lista = semLugar.take(6).joinToString(", ") + if (semLugar.size > 6) "…" else ""
            achar("W-07", "A", "mundo", "${semLugar.size} NPCs vivos sem cidade nem facção (o motor os coloca \"em trânsito\"): $lista")
        }
        for (r in w.t.getValue("relogio")) {
            try {
                val progresso = r.getValue("progresso").trim().toInt()
                val segmentos = r.getValue("segmentos").trim().toInt()
                if (progresso > segmentos || segmentos < 1) achar("W-07", "E", r.getValue("id"), "segmentos/progresso incoerentes")
            } catch (e: NumberFormatException) {
                achar("W-07", "E", r.getValue("id"), "segmentos/progresso não numéricos")
            }
        }
        for ((reinoId, reino) in w.reinos) {
            val capital = reino.linha.getValue("capital")
            if (capital != "—" && capital != "?" && capital !in ids) achar("W-07", "E", reinoId, "capital inexistente: $capital")
            val governa =
                w.t.getValue("npc").any {
                    it.getValue("reino") == reinoId && it.getValue("vivo") == "sim" && "governante" in TabelaMolde.toks(it.getValue("papeis"))
                }
            if (!governa) achar("W-07", "E", reinoId, "reino sem NPC governante vivo")
        }

        // W-08 rotas: pontas existentes e grafo por reino
        for (r in w.t.getValue("rota")) {
            for (k in listOf("de", "para")) {
                if (r.getValue(k) !in ids) achar("W-08", "E", r.getValue("id"), "ponta inexistente: ${r.getValue(k)}")
            }
        }
        for (reinoId in w.reinos.keys) {
            val locais = w.t.getValue("cidade").filter { it.getValue("reino") == reinoId }.map { it.getValue("id") }
            val vizinhos = HashMap<String, MutableSet<String>>()
            for (r in w.t.getValue("rota")) {
                if (r.getValue("reino") != reinoId) continue
                vizinhos.getOrPut(r.getValue("de")) { HashSet() }.add(r.getValue("para"))
                vizinhos.getOrPut(r.getValue("para")) { HashSet() }.add(r.getValue("de"))
            }
            if (locais.isNotEmpty()) {
                val vistos = HashSet<String>()
                val pilha = arrayListOf(locais[0])
                while (pilha.isNotEmpty()) {
                    val x = pilha.removeAt(pilha.lastIndex)
                    if (!vistos.add(x)) continue
                    for (y in vizinhos[x].orEmpty()) {
                        if (y !in vistos) pilha.add(y)
                    }
                }
                val fora = locais.filter { it !in vistos }
                if (fora.isNotEmpty()) {
                    val gravidade = if (("V-23" to reinoId) in excecoes) "A" else "E"
                    achar("W-08", gravidade, reinoId, "cidades fora do grafo de rotas: " + fora.joinToString(", "))
                }
            }
        }
        val rotas = w.t.getValue("rota")
        val comDuracao = rotas.count { it.getValue("estado") != "desconhecida" }
        val entreReinos = rotas.count { w.reinoDe(it.getValue("de")) != w.reinoDe(it.getValue("para")) }
        achar("W-08", "A", "mundo", "rotas com duração: $comDuracao de ${rotas.size}; rotas ENTRE reinos: $entreReinos (depende de M-07)")

        // W-09 dinâmica: o que o motor lê além do molde
        if (din != null && !din.vazia) {
            for (x in cat.racas + cat.subracas.keys) {
                if (x !in din.longevidade) achar("W-09", "E", x, "raça ou sub-raça sem classe de longevidade")
            }
            for (c in din.longevidade.values.toSet() - din.classes) achar("W-09", "E", c, "classe de longevidade sem parâmetros")
            for (r in w.t.getValue("relogio")) {
                if (r.getValue("id") !in din.relogios) {
                    achar("W-09", "A", r.getValue("id"), "relógio sem regra em relogios_dinamica.txt (o motor usa o padrão)")
                }
            }
            val relogioIds = w.t.getValue("relogio").map { it.getValue("id") }.toSet()
            val semRelogio = w.t.getValue("evento").count { it.getValue("relogio") !in relogioIds }
            if (semRelogio > 0) achar("W-09", "A", "mundo", "$semRelogio eventos sem relógio (gatilho só em prosa): ficam latentes na F4")
        }
        return f.sortedWith(compareBy<Achado>({ it.gravidade != "E" }, { it.codigo }, { it.entidade }))
    }

    /** Quantos erros e quantos avisos há em [achados]. */
    fun resumo(achados: List<Achado>): Pair<Int, Int> = achados.count { it.gravidade == "E" } to achados.count { it.gravidade == "A" }

    /** O texto de `saidas/validacao-mundo.txt`: cabeçalho, uma linha por constatação e a linha de resumo. */
    fun relatorio(
        w: MundoMolde,
        achados: List<Achado>,
    ): String {
        val (erros, avisos) = resumo(achados)
        val linhas = ArrayList<String>()
        linhas.add("codigo|gravidade|entidade|mensagem")
        for (a in achados) linhas.add("${a.codigo}|${a.gravidade}|${a.entidade}|${a.mensagem}")
        val contagem = listOf("cidade", "npc", "faccao", "relogio").map { w.t.getValue(it).size }
        linhas.add(
            "# resumo: $erros erros, $avisos avisos; ${w.reinos.size} reinos, ${contagem[0]} cidades, ${contagem[1]} NPCs, " +
                "${contagem[2]} facções, ${contagem[3]} relógios; molde sha256=${w.hashMolde.take(16)}",
        )
        return linhas.joinToString("\n") + "\n"
    }

    /** `id` como palavra inteira em [texto]: sem letra, número, `_` ou `-` colado antes ou depois (como `(?<![\w-])…(?![\w-])`). */
    private fun palavraEm(
        id: String,
        texto: CharSequence,
    ): Boolean = Regex("(?<![\\p{L}\\p{N}_-])" + Regex.escape(id) + "(?![\\p{L}\\p{N}_-])").containsMatchIn(texto)
}
