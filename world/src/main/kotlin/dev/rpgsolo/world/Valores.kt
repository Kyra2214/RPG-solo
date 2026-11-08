package dev.rpgsolo.world

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** Utilidades de valores dinâmicos do estado (null, Boolean, inteiros, String, List, Map), com a igualdade do JSON. */
object Valores {
    fun verdadeiro(v: Any?): Boolean = v == true

    fun numero(v: Any?): Long =
        when (v) {
            is Int -> v.toLong()
            is Long -> v
            else -> throw IllegalArgumentException("número esperado: $v")
        }

    /** Igualdade profunda; Int e Long com o mesmo valor são iguais; Boolean nunca é igual a número. */
    fun iguais(
        a: Any?,
        b: Any?,
    ): Boolean =
        when {
            a == null || b == null -> a == null && b == null
            (a is Int || a is Long) && (b is Int || b is Long) -> numero(a) == numero(b)
            a is Map<*, *> && b is Map<*, *> -> a.size == b.size && a.all { (k, v) -> k in b && iguais(v, b[k]) }
            a is List<*> && b is List<*> -> a.size == b.size && a.indices.all { iguais(a[it], b[it]) }
            else -> a == b
        }

    /** Cópia profunda mutável (equivale a `json.loads(json.dumps(x))`). */
    fun copiar(v: Any?): Any? =
        when (v) {
            is Map<*, *> -> LinkedHashMap<String, Any?>().also { m -> v.forEach { (k, x) -> m[k.toString()] = copiar(x) } }
            is Iterable<*> -> v.map { copiar(it) }.toMutableList()
            is Array<*> -> v.map { copiar(it) }.toMutableList()
            else -> v
        }
}

/** Leitura de JSON para o modelo dinâmico acima (só inteiros: número com ponto é recusado). */
object JsonLeitor {
    fun valor(texto: String): Any? = converter(Json.parseToJsonElement(texto))

    @Suppress("UNCHECKED_CAST")
    fun objeto(texto: String): MutableMap<String, Any?> =
        valor(texto) as? MutableMap<String, Any?>
            ?: throw ErroCronica("linha não é um objeto JSON")

    fun converter(e: JsonElement): Any? =
        when (e) {
            is JsonNull -> null
            is JsonObject -> LinkedHashMap<String, Any?>().also { m -> e.forEach { (k, v) -> m[k] = converter(v) } }
            is JsonArray -> e.map { converter(it) }.toMutableList()
            is JsonPrimitive ->
                when {
                    e.isString -> e.content
                    e.content == "true" -> true
                    e.content == "false" -> false
                    else -> e.content.toLongOrNull() ?: throw IllegalArgumentException("número não inteiro: ${e.content}")
                }
        }
}
