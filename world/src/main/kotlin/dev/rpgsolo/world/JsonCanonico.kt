package dev.rpgsolo.world

import java.security.MessageDigest

/** `sha256` em hexadecimal minúsculo sobre UTF-8 (igual a `cronica.sha`). */
object Hash {
    fun sha256(s: String): String = sha256(s.toByteArray(Charsets.UTF_8))

    fun sha256(b: ByteArray): String = MessageDigest.getInstance("SHA-256").digest(b).joinToString("") { "%02x".format(it) }

    /** Elo da cadeia: `sha(anterior + canon(delta))`. */
    fun encadear(anterior: String, delta: Any?): String = sha256(anterior + JsonCanonico.canon(delta))
}

/**
 * JSON canônico idêntico a `json.dumps(o, ensure_ascii=False, sort_keys=True, separators=(',', ':'))` do Python.
 * Aceita null, Boolean, Int, Long, String, Map (chaves String ou Int/Long, como o Python converte), Iterable e Array.
 * Não aceita ponto flutuante: o estado do mundo só tem inteiros.
 */
object JsonCanonico {
    fun canon(o: Any?): String = StringBuilder().also { escrever(it, o) }.toString()

    private fun escrever(sb: StringBuilder, o: Any?) {
        when (o) {
            null -> sb.append("null")
            is Boolean -> sb.append(if (o) "true" else "false")
            is Int, is Long, is Short, is Byte -> sb.append(o.toString())
            is java.math.BigInteger -> sb.append(o.toString())
            is CharSequence -> texto(sb, o.toString())
            is Map<*, *> -> mapa(sb, o)
            is Iterable<*> -> lista(sb, o)
            is Array<*> -> lista(sb, o.asList())
            is IntArray -> lista(sb, o.asList())
            is LongArray -> lista(sb, o.asList())
            is Float, is Double -> throw IllegalArgumentException("ponto flutuante não suportado no JSON canônico")
            else -> throw IllegalArgumentException("tipo não suportado: ${o::class.java.name}")
        }
    }

    private fun lista(sb: StringBuilder, it: Iterable<*>) {
        sb.append('[')
        var primeiro = true
        for (e in it) {
            if (!primeiro) sb.append(',')
            primeiro = false
            escrever(sb, e)
        }
        sb.append(']')
    }

    private fun mapa(sb: StringBuilder, m: Map<*, *>) {
        val chaves = m.entries.map { chaveTexto(it.key) to it.value }
        val ordenadas = chaves.sortedWith { a, b -> compararPontosDeCodigo(a.first, b.first) }
        for (i in 1 until ordenadas.size) {
            require(ordenadas[i - 1].first != ordenadas[i].first) { "chave duplicada: ${ordenadas[i].first}" }
        }
        sb.append('{')
        var primeiro = true
        for ((k, v) in ordenadas) {
            if (!primeiro) sb.append(',')
            primeiro = false
            texto(sb, k)
            sb.append(':')
            escrever(sb, v)
        }
        sb.append('}')
    }

    private fun chaveTexto(k: Any?): String = when (k) {
        is String -> k
        is Int, is Long -> k.toString()
        else -> throw IllegalArgumentException("chave não suportada: $k")
    }

    private fun texto(sb: StringBuilder, s: String) {
        sb.append('"')
        for (c in s) {
            when {
                c == '"' -> sb.append("\\\"")
                c == '\\' -> sb.append("\\\\")
                c == '\n' -> sb.append("\\n")
                c == '\r' -> sb.append("\\r")
                c == '\t' -> sb.append("\\t")
                c == '\b' -> sb.append("\\b")
                c == '\u000c' -> sb.append("\\f")
                c < ' ' -> sb.append("\\u").append("%04x".format(c.code))
                else -> sb.append(c)
            }
        }
        sb.append('"')
    }

    /** Ordem por ponto de código Unicode, como `str` do Python (e não por unidade UTF-16). */
    fun compararPontosDeCodigo(a: String, b: String): Int {
        var i = 0
        var j = 0
        while (i < a.length && j < b.length) {
            val ca = a.codePointAt(i)
            val cb = b.codePointAt(j)
            if (ca != cb) return ca.compareTo(cb)
            i += Character.charCount(ca)
            j += Character.charCount(cb)
        }
        return (a.length - i).compareTo(b.length - j)
    }
}
