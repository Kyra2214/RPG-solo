package dev.rpgsolo.world

/** Classe de longevidade (porta de uma linha de `classes_longevidade.txt`). */
class ClasseLongevidade(val mat: Int, val mad: Int, val ido: Int, val max: Int, val base: Int, val mido: Int)

/** Regra de um relógio (porta de uma linha de `relogios_dinamica.txt`). */
class RegraRelogio(val taxa: Int, val pressoes: List<String>, val travas: List<String>, val eventos: List<String>, val inacao: Int)

/**
 * Camada de dinâmica (porta de `dinamica.py`): lê `dinamica/*.txt`. Só leitura; nunca volta para o molde.
 * Arquivo ausente = vazio, sem erro.
 */
class Dinamica(private val fonte: MoldeFonte) : DinamicaEstado {
    override val vazia: Boolean = fonte.bytes("dinamica/classes_longevidade.txt") == null

    private fun tabela(nome: String): List<Linha> = fonte.texto("dinamica/$nome")?.let { TabelaMolde.ler(it).linhas } ?: emptyList()

    private fun chaves(nome: String): Map<String, Int> =
        tabela(nome).associate { it.getValue("chave") to it.getValue("valor").trim().toInt() }

    val classes: Map<String, ClasseLongevidade> = tabela("classes_longevidade.txt").associate {
        it.getValue("classe") to ClasseLongevidade(
            mat = it.getValue("maturidade").trim().toInt(), mad = it.getValue("madura_ini").trim().toInt(),
            ido = it.getValue("idosa_ini").trim().toInt(), max = it.getValue("vida_max").trim().toInt(),
            base = it.getValue("mortalidade_base_pm").trim().toInt(), mido = it.getValue("mortalidade_idosa_pm").trim().toInt(),
        )
    }
    val longev: Map<String, String> = tabela("longevidade.txt").associate { it.getValue("id") to it.getValue("classe") }
    val faixas: Map<String, Pair<Int, Int>> = tabela("faixas_etarias.txt").associate {
        it.getValue("faixa") to (it.getValue("de_pm").trim().toInt() to it.getValue("ate_pm").trim().toInt())
    }
    override val lim: Map<String, Int> = chaves("limiares.txt")
    override val econ: Map<String, Int> = chaves("economia.txt")
    val ecos: Map<String, Int> = chaves("ecos.txt")
    override val mist: Map<String, Int> = chaves("misterios.txt")
    override val front: Map<String, Int> = chaves("fronteira.txt")
    val dipl: Map<String, Int> = chaves("diplomacia.txt")
    val guerra: Map<String, Int> = chaves("guerra.txt")
    override val povos: Map<String, Int> = chaves("povos.txt")
    val inv: Map<String, Int> = chaves("invariantes.txt")
    val relogios: Map<String, RegraRelogio> = tabela("relogios_dinamica.txt").associate {
        it.getValue("relogio") to RegraRelogio(
            taxa = it.getValue("taxa_base_pm").trim().toInt(), pressoes = TabelaMolde.toks(it.getValue("pressoes")),
            travas = TabelaMolde.toks(it.getValue("travas")), eventos = TabelaMolde.toks(it.getValue("evento_ao_completar")),
            inacao = it.getValue("inacao_pm").trim().toInt(),
        )
    }

    override fun classeDe(povo: String, subraca: String): String =
        longev[subraca]?.takeIf { it.isNotEmpty() } ?: longev[povo]?.takeIf { it.isNotEmpty() } ?: "humana"

    /** Idade em anos de um NPC do molde: número do texto, ou derivada da faixa (determinística pelo id). */
    fun idadeInicial(npcId: String, idadeTxt: String, classe: String): Int? {
        val k = classes[classe]
        if (k == null || k.max == 0) return null
        NUMERO.find(idadeTxt)?.let { return it.groupValues[1].toInt() }
        val faixa = FAIXA.find(idadeTxt)?.groupValues?.get(1)?.lowercase() ?: "?"
        val (de, ate) = faixas[faixa] ?: faixas["?"] ?: (250 to 650)
        val lo = maxOf(k.mat, k.max * de / 1000)
        val hi = maxOf(k.mat + 1, k.max * ate / 1000)
        val h = Hash.sha256("idade|$npcId").substring(0, 8).toLong(16)
        return (lo + h % maxOf(1, hi - lo)).toInt()
    }

    override fun nascDia(npcId: String, idadeTxt: String, classe: String): Int? {
        val a = idadeInicial(npcId, idadeTxt, classe) ?: return null
        return (-(a.toLong() * 365 + Hash.sha256("dia|$npcId").substring(0, 8).toLong(16) % 365)).toInt()
    }

    /** Chance de morte natural no ano, por mil. */
    fun mortePm(classe: String, idade: Int): Int {
        val k = classes[classe]
        if (k == null || k.max == 0) return 0
        if (idade < k.ido) return k.base
        val frac = minOf(1000L, (idade - k.ido) * 1000L / maxOf(1, k.max - k.ido))
        return minOf(1000L, k.mido + (1000L - k.mido) * frac * frac / 1000000L).toInt()
    }

    fun eIdoso(classe: String, idade: Int): Boolean {
        val k = classes[classe]
        return k != null && k.max != 0 && idade >= k.ido
    }

    companion object {
        private val NUMERO = Regex("^\\s*(?:cerca de\\s+)?(\\d+)\\s*$")
        private val FAIXA = Regex("^\\s*([a-zA-Zçã?]+)")

        /** Hash da dinâmica (igual a `hash_dinamica` do Python): nome + sha256 do texto de cada arquivo, em ordem. */
        fun hash(fonte: MoldeFonte, manifesto: MoldeManifesto): String {
            val sb = StringBuilder()
            for (a in manifesto.dinamica.sortedWith { x, y -> Ordem.comparar(x.caminho, y.caminho) }) {
                sb.append(a.caminho.substringAfterLast('/')).append(Hash.sha256(checkNotNull(fonte.texto(a.caminho)) { "falta ${a.caminho}" }))
            }
            return Hash.sha256(sb.toString())
        }
    }
}
