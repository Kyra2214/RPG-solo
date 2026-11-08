package dev.rpgsolo.world

/**
 * Só o que o validador do mundo lê da dinâmica (W-09): as classes de longevidade, a classe de cada raça e sub-raça
 * e os relógios com regra. A configuração completa (`Dinamica` do Python) entra em MI-05. Arquivo ausente = vazio.
 */
class DinamicaMinima(
    val vazia: Boolean,
    val classes: Set<String>,
    val longevidade: Map<String, String>,
    val relogios: Set<String>,
) {
    companion object {
        fun carregar(fonte: MoldeFonte): DinamicaMinima {
            fun tabela(nome: String): List<Linha> = fonte.texto("dinamica/$nome")?.let { TabelaMolde.ler(it).linhas } ?: emptyList()

            return DinamicaMinima(
                vazia = fonte.bytes("dinamica/classes_longevidade.txt") == null,
                classes = tabela("classes_longevidade.txt").map { it.getValue("classe") }.toSet(),
                longevidade = tabela("longevidade.txt").associate { it.getValue("id") to it.getValue("classe") },
                relogios = tabela("relogios_dinamica.txt").map { it.getValue("relogio") }.toSet(),
            )
        }
    }
}
