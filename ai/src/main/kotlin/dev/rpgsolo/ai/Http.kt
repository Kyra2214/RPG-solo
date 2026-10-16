package dev.rpgsolo.ai

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class HttpReply(val status: Int, val body: String, val headers: Map<String, String> = emptyMap())

/** Separa a rede do resto: nos testes entra uma implementação falsa, sem internet. */
interface HttpTransport {
    /** Lança [AiError.NoInternet] se não conseguir falar com o servidor. */
    fun post(url: String, headers: Map<String, String>, body: String): HttpReply
}

class JavaNetTransport(
    private val connectTimeoutMs: Int = 15_000,
    private val readTimeoutMs: Int = 60_000
) : HttpTransport {
    override fun post(url: String, headers: Map<String, String>, body: String): HttpReply {
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            try {
                conn.requestMethod = "POST"
                conn.connectTimeout = connectTimeoutMs
                conn.readTimeout = readTimeoutMs
                conn.doOutput = true
                headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val status = conn.responseCode
                val stream = if (status in 200..299) conn.inputStream else conn.errorStream
                val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                val hdrs = conn.headerFields
                    .filterKeys { it != null }
                    .mapKeys { it.key.lowercase() }
                    .mapValues { it.value.firstOrNull().orEmpty() }
                return HttpReply(status, text, hdrs)
            } finally {
                conn.disconnect()
            }
        } catch (e: IOException) {
            throw AiError.NoInternet(e.javaClass.simpleName)
        }
    }
}

/** Traduz o status HTTP em [AiError]. O corpo é cortado para nunca vazar texto enorme (nem a chave) na mensagem. */
internal fun checkHttp(reply: HttpReply) {
    val s = reply.status
    if (s in 200..299) return
    throw when (s) {
        401, 403 -> AiError.InvalidKey()
        429 -> AiError.RateLimited(reply.headers["retry-after"]?.toLongOrNull()?.let { it * 1000 })
        408 -> AiError.ServerError(s)
        in 500..599 -> AiError.ServerError(s)
        400, 404, 413, 422 -> AiError.BadRequest("HTTP $s " + reply.body.take(200))
        else -> AiError.BadRequest("HTTP $s")
    }
}
