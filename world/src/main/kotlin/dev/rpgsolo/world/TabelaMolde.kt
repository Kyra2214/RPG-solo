package dev.rpgsolo.world

/** Uma linha de tabela do molde: nome da coluna para o valor, na ordem do cabeçalho. */
typealias Linha = Map<String, String>

/** Resultado da leitura de uma tabela `|` do molde: o cabeçalho, as linhas válidas e os erros de formato. */
class TabelaLida(val cabecalho: List<String>, val linhas: List<Linha>, val erros: List<String>)

/** Leitor das tabelas `|` do molde (porta de `ler_tabela`, `_split` e `toks` de `carregador.py`). */
object TabelaMolde {
    /** Divide por `|` respeitando `\|` e `\\`; a barra com o caractere seguinte passa inteira e só depois é desfeita. */
    fun dividir(linha: String): List<String> {
        val saida = ArrayList<String>()
        val atual = StringBuilder()
        var i = 0
        while (i < linha.length) {
            val c = linha[i]
            if (c == '\\' && i + 1 < linha.length) {
                atual.append(c).append(linha[i + 1])
                i += 2
                continue
            }
            if (c == '|') {
                saida.add(atual.toString())
                atual.setLength(0)
            } else {
                atual.append(c)
            }
            i++
        }
        saida.add(atual.toString())
        return saida.map { it.replace("\\|", "|").replace("\\\\", "\\") }
    }

    fun ler(texto: String): TabelaLida {
        val erros = ArrayList<String>()
        val todas = texto.split('\n')
        if (todas[0].trim() != "# esquema=1") erros.add("falta \"# esquema=1\" na 1ª linha")
        val uteis = todas.filter { it.isNotEmpty() && !it.startsWith("#") }
        if (uteis.isEmpty()) return TabelaLida(emptyList(), emptyList(), erros + "arquivo vazio")
        val cabecalho = dividir(uteis[0])
        val linhas = ArrayList<Linha>()
        for (n in 1 until uteis.size) {
            val celulas = dividir(uteis[n])
            if (celulas.size != cabecalho.size) {
                erros.add("linha ${n + 1} com ${celulas.size} campos (esperado ${cabecalho.size})")
                continue
            }
            val linha = LinkedHashMap<String, String>()
            for (k in cabecalho.indices) linha[cabecalho[k]] = celulas[k]
            linhas.add(linha)
        }
        return TabelaLida(cabecalho, linhas, erros)
    }

    /** Campo-lista: itens separados por `;`, sem os marcadores `?`, `—` e `--`. */
    fun toks(valor: String): List<String> =
        valor.split(';').map { it.trim() }.filter { it.isNotEmpty() && it != "?" && it != "—" && it != "--" }
}
