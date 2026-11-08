package dev.rpgsolo.world

/** Uma linha do manifesto do molde exportado: tipo (`molde`, `dinamica` ou `derivado`), caminho, tamanho e sha256 do arquivo. */
data class ArquivoMolde(val tipo: String, val caminho: String, val bytes: Long, val sha256: String)

/**
 * Contrato de dados do molde de Mythralis exportado por `Mythralis/motor/exportar_molde.py` (MI-01).
 *
 * Os arquivos ficam em `rules/src/main/resources/mundos/mythralis/` e espelham os caminhos de `Mythralis/`.
 * [arquivos] vem na ordem exata do hash do molde (pasta por pasta, caminhos ordenados); [moldeSha256] é o hash
 * encadeado de caminho + bytes dessa lista, igual ao do `saidas/validacao-mundo.txt` do Python (D-I2).
 * Aqui só se lê o manifesto; o sha256 e o carregador do molde entram em MI-02 a MI-04.
 */
data class MoldeManifesto(
    val esquema: Int,
    val moldeSha256: String,
    val dinamicaSha256: String,
    val arquivos: List<ArquivoMolde>,
) {
    val molde: List<ArquivoMolde> get() = arquivos.filter { it.tipo == TIPO_MOLDE }
    val dinamica: List<ArquivoMolde> get() = arquivos.filter { it.tipo == TIPO_DINAMICA }
    val derivados: List<ArquivoMolde> get() = arquivos.filter { it.tipo == TIPO_DERIVADO }

    companion object {
        const val RAIZ_RECURSOS = "mundos/mythralis"
        const val ARQUIVO = "$RAIZ_RECURSOS/manifesto.txt"
        const val TIPO_MOLDE = "molde"
        const val TIPO_DINAMICA = "dinamica"
        const val TIPO_DERIVADO = "derivado"
        const val ESQUEMA = 1
        private const val CABECALHO = "tipo|caminho|bytes|sha256"

        /** Lê o texto do manifesto; lança [IllegalArgumentException] se o formato ou as contagens não baterem. */
        fun ler(texto: String): MoldeManifesto {
            val meta = HashMap<String, String>()
            val arquivos = ArrayList<ArquivoMolde>()
            var cabecalhoVisto = false
            for (linha in texto.split('\n')) {
                if (linha.isEmpty()) continue
                if (linha.startsWith("#")) {
                    val corpo = linha.removePrefix("#").trim()
                    val i = corpo.indexOf('=')
                    if (i > 0) meta[corpo.substring(0, i).trim()] = corpo.substring(i + 1).trim()
                    continue
                }
                if (!cabecalhoVisto) {
                    require(linha == CABECALHO) { "cabeçalho inesperado no manifesto: $linha" }
                    cabecalhoVisto = true
                    continue
                }
                val c = linha.split('|')
                require(c.size == 4) { "linha do manifesto com ${c.size} campos (esperado 4): $linha" }
                require(c[0] == TIPO_MOLDE || c[0] == TIPO_DINAMICA || c[0] == TIPO_DERIVADO) { "tipo desconhecido no manifesto: ${c[0]}" }
                val bytes = requireNotNull(c[2].toLongOrNull()) { "tamanho inválido no manifesto: $linha" }
                arquivos.add(ArquivoMolde(c[0], c[1], bytes, c[3]))
            }
            require(cabecalhoVisto) { "manifesto sem cabeçalho" }
            require(meta["esquema"] == ESQUEMA.toString()) { "esquema do manifesto não suportado: ${meta["esquema"]}" }
            val manifesto = MoldeManifesto(
                esquema = ESQUEMA,
                moldeSha256 = requireNotNull(meta["molde_sha256"]) { "manifesto sem molde_sha256" },
                dinamicaSha256 = requireNotNull(meta["dinamica_sha256"]) { "manifesto sem dinamica_sha256" },
                arquivos = arquivos,
            )
            require(meta["arquivos_molde"] == manifesto.molde.size.toString()) { "contagem de arquivos do molde não confere" }
            require(meta["arquivos_dinamica"] == manifesto.dinamica.size.toString()) { "contagem de arquivos da dinâmica não confere" }
            require(meta["arquivos_derivados"] == manifesto.derivados.size.toString()) { "contagem de arquivos derivados não confere" }
            return manifesto
        }
    }
}
