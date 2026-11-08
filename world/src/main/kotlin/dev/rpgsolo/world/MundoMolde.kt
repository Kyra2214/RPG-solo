package dev.rpgsolo.world

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Um reino do molde: a linha de `reino.txt` e o nome da pasta de onde veio. */
class ReinoMolde(val linha: Linha, val pasta: String)

/** Erro de carga (formato ou arquivo ausente), na pasta `pasta`, no tipo de tabela `tipo`. */
class ErroCarga(val pasta: String, val tipo: String, val mensagem: String)

/** O que o validador usa do catálogo de personagem: raças, sub-raças (com a raça-mãe), classes e profissões. */
class CatalogoPersonagem(
    val racas: List<String>,
    val subracas: Map<String, String>,
    val classes: Set<String>,
    val profissoes: Set<String>,
) {
    companion object {
        fun ler(texto: String): CatalogoPersonagem {
            val raiz = Json.parseToJsonElement(texto).jsonObject

            fun itens(chave: String) = raiz.getValue(chave).jsonArray.map { it.jsonObject }

            fun id(item: JsonObject) = item.getValue("id").jsonPrimitive.content

            val subracas = LinkedHashMap<String, String>()
            for (item in itens("subraces")) subracas[id(item)] = item.getValue("parentRaceId").jsonPrimitive.content
            return CatalogoPersonagem(
                racas = itens("races").map { id(it) },
                subracas = subracas,
                classes = itens("classes").map { id(it) }.toSet(),
                profissoes = itens("professions").map { id(it) }.toSet(),
            )
        }
    }
}

/**
 * O mundo carregado do molde (porta de `World` de `carregador.py`), somente leitura: [t] guarda as tabelas por tipo
 * com todos os reinos juntos (cada linha ganha `_reino_pasta`), [cab] os cabeçalhos por tipo e pasta, [indice] o
 * tipo e a linha de cada ID, e [hashMolde] o sha256 encadeado do molde (D-I2).
 */
class MundoMolde(
    val reinos: Map<String, ReinoMolde>,
    val t: Map<String, List<Linha>>,
    val cab: Map<String, Map<String, List<String>>>,
    val erros: List<ErroCarga>,
    val indice: Map<String, Pair<String, Linha>>,
    val catalogo: CatalogoPersonagem,
    val subracasEmProsa: Set<String>,
    val hashMolde: String,
) {
    fun linha(id: String): Linha? = indice[id]?.second

    fun tipo(id: String): String? = indice[id]?.first

    /** Reino de um ID, ou `null` se o ID não existe ou a linha não tem reino. */
    fun reinoDe(id: String): String? = linha(id)?.get("reino")?.takeIf { it.isNotEmpty() }
}
