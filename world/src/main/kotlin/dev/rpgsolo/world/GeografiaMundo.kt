package dev.rpgsolo.world

import java.text.Normalizer

/** WD-02: elevação relativa de uma feição. A ordem do enum é a ordem de altura. */
enum class Elevacao(val rotulo: String) {
    NIVEL_MAR("nivel_mar"),
    BAIXA("baixa"),
    MEDIA("media"),
    ALTA("alta"),
    ;

    companion object {
        fun de(texto: String): Elevacao? = values().firstOrNull { it.rotulo == texto.trim() }
    }
}

/** WD-02: feição geográfica (cordilheira, rio, mar, ilha, vulcão, floresta, deserto...). Só posição relativa. */
data class Feicao(
    val id: String,
    val nome: String,
    val tipo: String,
    val reino: String?,
    val elevacao: Elevacao?,
    /** Rios: id da feição onde nasce; `null` = não se aplica; [DESCONHECIDO] = o cânone não fixou. */
    val nasceEm: String?,
    val desembocaEm: String?,
    val vizinhos: List<String>,
    val fonte: String,
)

data class GeografiaMundo(val feicoes: List<Feicao>, val erros: List<String>) {
    val porId: Map<String, Feicao> by lazy { feicoes.associateBy { it.id } }

    companion object {
        /** Fora de `mundo/`: o exportador do molde recria essa pasta e o hash do molde incluiria o arquivo. */
        const val ARQUIVO = "geografia/geografia.txt"
        const val DESCONHECIDO = "?"

        /** Lê `mundo/geografia.txt` da [fonte]. Arquivo ausente vira erro, não exceção. */
        fun carregar(fonte: MoldeFonte): GeografiaMundo {
            val texto = fonte.texto(ARQUIVO) ?: return GeografiaMundo(emptyList(), listOf("falta $ARQUIVO"))
            val t = TabelaMolde.ler(texto)

            fun vazio(s: String?) = s?.trim()?.takeIf { it.isNotEmpty() && it != "-" }
            val feicoes =
                t.linhas.map { l ->
                    Feicao(
                        id = l["id"].orEmpty().trim(),
                        nome = l["nome"].orEmpty().trim(),
                        tipo = l["tipo"].orEmpty().trim(),
                        reino = vazio(l["reino"]),
                        elevacao = vazio(l["elevacao"])?.let { Elevacao.de(it) },
                        nasceEm = vazio(l["nasce_em"]),
                        desembocaEm = vazio(l["desemboca_em"]),
                        vizinhos = vazio(l["vizinhos"])?.split(',')?.map { it.trim() }.orEmpty(),
                        fonte = l["fonte"].orEmpty().trim(),
                    )
                }
            return GeografiaMundo(feicoes, t.erros)
        }
    }
}

/**
 * WD-02: regras de geografia que o WD-01 deixou de fora ("rio que não desce de área elevada" e "fortaleza fora de
 * posição plausível"). Sem coordenadas: usa elevação relativa e vizinhança, como o M-04 manda. Códigos G-01 a G-07.
 */
object ValidadorGeografia {
    private val MARCAS_FORTALEZA =
        listOf("fronteira", "limite", "passagem", "morro", "planalto", "alto", "serra", "espinha", "montanha", "colina", "rio", "costa", "porto")

    private fun normalizar(s: String): String =
        Normalizer.normalize(s.trim(), Normalizer.Form.NFD).replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()

    fun validar(
        g: GeografiaMundo,
        w: MundoMolde,
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
        g.erros.forEach { achar("G-00", "E", "geografia", it) }
        val ids = g.feicoes.map { it.id }
        ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.forEach { achar("G-01", "E", it, "id de feição duplicado") }
        val reinos = w.t["reino"].orEmpty().mapNotNull { it["id"] }.toSet()
        val porId = g.porId

        for (e in g.feicoes) {
            if (e.reino != null && e.reino !in reinos) achar("G-02", "E", e.id, "reino inexistente: ${e.reino}")
            if (e.elevacao == null) achar("G-02", "E", e.id, "elevação ausente ou inválida")
            for (v in e.vizinhos) {
                val outro = porId[v]
                if (outro == null) {
                    achar("G-05", "E", e.id, "vizinho inexistente: $v")
                } else if (e.id !in outro.vizinhos) {
                    achar("G-05", "A", e.id, "vizinhança não é recíproca com $v")
                }
            }
            if (e.tipo != "rio") continue
            // G-03/G-04: rio nasce em área elevada e desce até algo mais baixo (mar, outro rio ou terra mais baixa).
            val origem = e.nasceEm
            val foz = e.desembocaEm
            if (origem == null || origem == GeografiaMundo.DESCONHECIDO || foz == null || foz == GeografiaMundo.DESCONHECIDO) {
                achar("G-03", "A", e.id, "rio sem nascente ou foz fixadas pelo cânone")
                continue
            }
            val a = porId[origem]
            val b = porId[foz]
            if (a == null) achar("G-03", "E", e.id, "nascente inexistente: $origem")
            if (b == null) achar("G-03", "E", e.id, "foz inexistente: $foz")
            if (a?.elevacao != null && b?.elevacao != null) {
                if (a.elevacao < Elevacao.MEDIA) achar("G-04", "E", e.id, "o rio nasce em ${a.nome}, que não é área elevada")
                if (a.elevacao <= b.elevacao) achar("G-04", "E", e.id, "o rio não desce: ${a.nome} não é mais alta que ${b.nome}")
            }
        }

        // G-06: fortaleza em posição plausível (aviso: o texto de localização é livre).
        for (c in w.t["cidade"].orEmpty()) {
            val tipo = normalizar(c["tipo"].orEmpty())
            if ("fortaleza" !in tipo && "fortificad" !in tipo) continue
            val lugar = normalizar(c["localizacao"].orEmpty())
            if (MARCAS_FORTALEZA.none {
                    it in lugar
                }
            ) {
                achar(
                    "G-06",
                    "A",
                    c["id"].orEmpty(),
                    "fortaleza sem posição defensável descrita (passagem, fronteira, morro, planalto...)",
                )
            }
        }

        // G-07: todo reino do mundo tem ao menos uma feição própria registrada.
        for (r in reinos) if (g.feicoes.none { it.reino == r }) achar("G-07", "A", r, "reino sem feição geográfica registrada")
        return f
    }

    fun relatorio(achados: List<Achado>): String {
        val (erros, avisos) = ValidadorMundo.resumo(achados)
        return buildString {
            append("Validação de geografia (WD-02): $erros erro(s), $avisos aviso(s)\n")
            achados.sortedWith(compareBy({ it.gravidade != "E" }, { it.codigo }, { it.entidade })).forEach {
                append("[${it.gravidade}] ${it.codigo} ${it.entidade}: ${it.mensagem}\n")
            }
        }
    }
}
