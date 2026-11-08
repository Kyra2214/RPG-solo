package dev.rpgsolo.world

import java.security.MessageDigest

/** Carregador único do molde de Mythralis (porta de `carregar` de `carregador.py`): lê os reinos num só [MundoMolde]. */
object CarregadorMolde {
    val TIPOS =
        listOf(
            "reino", "calendario", "cidade", "assentamento", "npc", "faccao", "rota", "relogio", "evento", "semente",
            "misterio", "fato", "reputacao", "canal", "cena", "resumo", "recurso", "relacao", "povo_no_reino",
            "excecoes", "ids_aposentados",
        )

    /** Tipos com ID e o prefixo que o ID deve ter, na ordem de `PREFIXO` do Python (a ordem decide o índice de IDs). */
    val PREFIXO =
        linkedMapOf(
            "reino" to "kingdom.", "calendario" to "calendario.", "cidade" to "city.", "assentamento" to "settlement.",
            "npc" to "npc.", "faccao" to "faction.", "rota" to "route.", "relogio" to "clock.", "evento" to "event.",
            "semente" to "seed.", "misterio" to "mist.", "fato" to "fact.", "reputacao" to "rep.", "canal" to "canal.",
            "cena" to "cena.",
        )

    private const val CATALOGO = "referencia/dados-brutos/personagem-catalogo.json"
    private const val DERIVADO_SUBRACAS = "derivados/subracas-em-prosa.txt"
    private val PASTA_DE_REINO = Regex("^reinos/(0[^/]*-[^/]*)/dados/")

    /** Carrega o molde descrito por [manifesto] a partir de [fonte]. Erros de formato vão para [MundoMolde.erros]. */
    fun carregar(
        fonte: MoldeFonte,
        manifesto: MoldeManifesto,
    ): MundoMolde {
        val pastas = manifesto.molde.mapNotNull { PASTA_DE_REINO.find(it.caminho)?.groupValues?.get(1) }.toSortedSet()
        val t = LinkedHashMap<String, MutableList<Linha>>()
        for (tipo in TIPOS) t[tipo] = ArrayList()
        val cab = LinkedHashMap<String, LinkedHashMap<String, List<String>>>()
        val erros = ArrayList<ErroCarga>()
        for (pasta in pastas) {
            for (tipo in TIPOS) {
                val texto = fonte.texto("reinos/$pasta/dados/$tipo.txt")
                if (texto == null) {
                    erros.add(ErroCarga(pasta, tipo, "arquivo ausente"))
                    continue
                }
                val tabela = TabelaMolde.ler(texto)
                cab.getOrPut(tipo) { LinkedHashMap() }[pasta] = tabela.cabecalho
                for (e in tabela.erros) erros.add(ErroCarga(pasta, tipo, e))
                for (linha in tabela.linhas) {
                    val completa = LinkedHashMap(linha)
                    completa["_reino_pasta"] = pasta
                    t.getValue(tipo).add(completa)
                }
            }
        }
        val reinos = LinkedHashMap<String, ReinoMolde>()
        for (r in t.getValue("reino")) reinos[r.getValue("id")] = ReinoMolde(r, r.getValue("_reino_pasta"))
        val indice = LinkedHashMap<String, Pair<String, Linha>>()
        for (tipo in PREFIXO.keys) {
            for (r in t.getValue(tipo)) indice[r.getValue("id")] = tipo to r
        }
        val catalogoTexto = checkNotNull(fonte.texto(CATALOGO)) { "molde sem $CATALOGO" }
        val hash = hashMolde(fonte, manifesto)
        if (hash != manifesto.moldeSha256) {
            erros.add(
                ErroCarga("mundo", "hash", "hash do molde (${hash.take(16)}) difere do manifesto (${manifesto.moldeSha256.take(16)})"),
            )
        }
        val emProsa =
            fonte.texto(DERIVADO_SUBRACAS)
                ?.let { TabelaMolde.ler(it).linhas.map { linha -> linha.getValue("subraca") }.toSet() }
                ?: emptySet()
        return MundoMolde(reinos, t, cab, erros, indice, CatalogoPersonagem.ler(catalogoTexto), emProsa, hash)
    }

    /** Carrega pelo classpath: lê o manifesto e o molde dos recursos do app. */
    fun carregar(fonte: MoldeFonte = MoldeFonteClasspath()): MundoMolde {
        val texto = checkNotNull(fonte.texto("manifesto.txt")) { "molde sem manifesto.txt" }
        return carregar(fonte, MoldeManifesto.ler(texto))
    }

    /** sha256 encadeado de caminho + bytes de cada arquivo do molde, na ordem do manifesto (igual a `hash_molde` do Python). */
    fun hashMolde(
        fonte: MoldeFonte,
        manifesto: MoldeManifesto,
    ): String {
        val md = MessageDigest.getInstance("SHA-256")
        for (arquivo in manifesto.molde) {
            md.update(arquivo.caminho.toByteArray(Charsets.UTF_8))
            md.update(checkNotNull(fonte.bytes(arquivo.caminho)) { "arquivo do manifesto ausente: ${arquivo.caminho}" })
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
