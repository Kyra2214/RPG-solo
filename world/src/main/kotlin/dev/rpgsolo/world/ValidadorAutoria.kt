package dev.rpgsolo.world

import java.text.Normalizer

/**
 * WD-01: regras de autoria do mundo pré-configurado, somando-se ao [ValidadorMundo] (que não é alterado, pois o
 * relatório dele é conferido contra o Python). Códigos A-01 a A-06. Erro (`E`) = contradição estrutural;
 * aviso (`A`) = lacuna de conteúdo que os módulos WD-02 a WD-09 ainda vão preencher ou dado marcado como `?`.
 *
 * Fora do escopo por falta de dado no esquema: "rio que não desce de área elevada" e "fortaleza fora de posição
 * plausível" (as tabelas não trazem altitude nem coordenadas); passaram ao `ValidadorGeografia` (WD-02), que usa elevação relativa e vizinhança (sem coordenadas, como o M-04 manda).
 */
object ValidadorAutoria {
    const val CIDADES_MIN = 10
    const val NPCS_PRINCIPAIS_MIN = 8
    const val NPCS_PRINCIPAIS_MAX = 12
    const val HEROIS_MIN = 2
    const val GENERAIS_MIN = 2

    private val MEIOS_NAVEGAVEIS = setOf("rio", "misto", "mar", "maritima", "marítima", "navegavel", "navegável")
    private val SINAIS_MINERACAO = listOf("mineraç", "minerad", "minério", "minerio")
    private val RECURSOS_MINERAIS =
        setOf("minerio", "minério", "metal", "ferro", "cobre", "prata", "ouro", "pedra", "carvao", "carvão", "cristal", "gema")

    private fun normalizar(s: String): String =
        Normalizer.normalize(s.trim(), Normalizer.Form.NFD).replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()
            .replace(Regex("\\s+"), " ")

    fun validar(w: MundoMolde): List<Achado> {
        val f = ArrayList<Achado>()

        fun achar(
            codigo: String,
            gravidade: String,
            entidade: String,
            mensagem: String,
        ) {
            f.add(Achado(codigo, gravidade, entidade, mensagem))
        }

        val cidades = w.t["cidade"].orEmpty()
        val npcs = w.t["npc"].orEmpty()
        val facoes = w.t["faccao"].orEmpty()
        val relacoes = w.t["relacao"].orEmpty()
        val rotas = w.t["rota"].orEmpty()
        val recursos = w.t["recurso"].orEmpty()

        // A-01 mínimos por reino (aviso: conteúdo ainda em escrita)
        for (reinoId in w.reinos.keys.sorted()) {
            val nCidades = cidades.count { it["reino"] == reinoId }
            if (nCidades < CIDADES_MIN) achar("A-01", "A", reinoId, "$nCidades cidades (mínimo $CIDADES_MIN)")
            val principais = npcs.filter { it["reino"] == reinoId && it["principal"] == "sim" }
            if (principais.size !in NPCS_PRINCIPAIS_MIN..NPCS_PRINCIPAIS_MAX) {
                achar("A-01", "A", reinoId, "${principais.size} NPCs principais (esperado $NPCS_PRINCIPAIS_MIN a $NPCS_PRINCIPAIS_MAX)")
            }
            val herois = principais.count { "heroi" in TabelaMolde.toks(it["papeis"].orEmpty()) }
            if (herois < HEROIS_MIN) achar("A-01", "A", reinoId, "$herois heróis entre os NPCs principais (mínimo $HEROIS_MIN)")
            val generais = principais.count { "general" in TabelaMolde.toks(it["papeis"].orEmpty()) }
            if (generais < GENERAIS_MIN) achar("A-01", "A", reinoId, "$generais generais entre os NPCs principais (mínimo $GENERAIS_MIN)")
        }

        // A-02 nomes duplicados dentro de cada tipo (erro)
        for ((tipo, linhas) in listOf("cidade" to cidades, "npc" to npcs, "faccao" to facoes)) {
            linhas.groupBy { normalizar(it["nome"].orEmpty()) }.filter { it.key.isNotEmpty() && it.value.size > 1 }.toSortedMap().forEach {
                    (nome, v) ->
                achar("A-02", "E", v.joinToString(", ") { it["id"].orEmpty() }, "nome duplicado em $tipo: $nome")
            }
        }

        // A-03 cidade sem reino ou com reino inexistente (erro)
        for (c in cidades) {
            val reino = c["reino"].orEmpty()
            if (reino.isEmpty()) {
                achar("A-03", "E", c["id"].orEmpty(), "cidade sem reino")
            } else if (reino !in w.reinos) {
                achar("A-03", "E", c["id"].orEmpty(), "cidade com reino inexistente: $reino")
            }
        }

        // A-04 relações contraditórias: aliança e rivalidade entre o mesmo par (erro)
        val tiposPorPar = LinkedHashMap<Set<String>, MutableSet<String>>()
        for (r in relacoes) {
            val par = setOf(r["origem"].orEmpty(), r["destino"].orEmpty())
            if (par.size < 2) continue
            val tipo = normalizar(r["tipo"].orEmpty()).substringBefore(' ')
            tiposPorPar.getOrPut(par) { LinkedHashSet() }.add(tipo)
        }
        for ((par, tipos) in tiposPorPar) {
            if ("alianca" in tipos && "rivalidade" in tipos) {
                achar(
                    "A-04",
                    "E",
                    par.sorted().joinToString(" x "),
                    "aliados e rivais ao mesmo tempo",
                )
            }
        }

        // A-05 porto sem acesso navegável
        val meiosPorCidade = HashMap<String, MutableSet<String>>()
        for (r in rotas) {
            val meio = normalizar(r["meio"].orEmpty())
            meiosPorCidade.getOrPut(r["de"].orEmpty()) { LinkedHashSet() }.add(meio)
            meiosPorCidade.getOrPut(r["para"].orEmpty()) { LinkedHashSet() }.add(meio)
        }
        for (c in cidades) {
            val id = c["id"].orEmpty()
            val tipo = normalizar(c["tipo"].orEmpty() + " " + c["funcao"].orEmpty())
            if (!tipo.contains("porto")) continue
            val meios = meiosPorCidade[id].orEmpty()
            val desconhecido = meios.any { it == "?" || it.isEmpty() }
            when {
                meios.isEmpty() -> achar("A-05", "E", id, "porto sem nenhuma rota")
                meios.any { it in MEIOS_NAVEGAVEIS } -> Unit
                desconhecido -> achar("A-05", "A", id, "porto com rotas de meio ainda não definido (?)")
                else -> achar("A-05", "E", id, "porto sem rota navegável (meios: ${meios.sorted().joinToString(", ")})")
            }
        }

        // A-06 cidade mineradora sem recurso mineral (aviso: o texto é livre)
        for (c in cidades) {
            val id = c["id"].orEmpty()
            val texto = normalizar(c["economia"].orEmpty() + " " + c["funcao"].orEmpty() + " " + c["tipo"].orEmpty())
            if (SINAIS_MINERACAO.none { it in texto }) continue
            val dela = recursos.filter { it["localidade"] == id }.map { normalizar(it["recurso"].orEmpty()) }
            if (dela.none {
                        r ->
                    RECURSOS_MINERAIS.any { it in r }
                }
            ) {
                achar("A-06", "A", id, "cidade mineradora sem recurso mineral registrado")
            }
        }

        return f
    }

    /** Relatório de texto no mesmo estilo de [ValidadorMundo.relatorio] (um achado por linha, erros primeiro). */
    fun relatorio(achados: List<Achado>): String {
        val (erros, avisos) = ValidadorMundo.resumo(achados)
        return buildString {
            append("Validação de autoria (WD-01): $erros erro(s), $avisos aviso(s)\n")
            achados.sortedWith(compareBy({ it.gravidade != "E" }, { it.codigo }, { it.entidade })).forEach {
                append("[${it.gravidade}] ${it.codigo} ${it.entidade}: ${it.mensagem}\n")
            }
        }
    }
}
