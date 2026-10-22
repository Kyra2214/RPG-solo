package dev.rpgsolo.ai

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Cancelamento cooperativo de chamadas de rede bloqueantes. Quem chama cria um token, executa o trabalho dentro de
 * [bind] e chama [cancel] de outra thread: a conexão aberta é fechada e esperas de retry/fila terminam na hora,
 * com [CancellationException] (que não é [AiError] e, portanto, não é repetida).
 */
class CancelToken {
    private val latch = CountDownLatch(1)
    private var onCancel: (() -> Unit)? = null

    val isCancelled: Boolean get() = latch.count == 0L

    fun cancel() {
        val action = synchronized(this) {
            latch.countDown()
            onCancel.also { onCancel = null }
        }
        action?.invoke()
    }

    /** Registra o que fechar ao cancelar; se já cancelado, executa na hora. Passe null para soltar. */
    internal fun attach(action: (() -> Unit)?) {
        val runNow = synchronized(this) {
            if (action != null && isCancelled) true else { onCancel = action; false }
        }
        if (runNow) action?.invoke()
    }

    fun throwIfCancelled() {
        if (isCancelled) throw CancellationException("operação cancelada")
    }

    /** Espera até [ms] ou até o cancelamento (nesse caso lança [CancellationException]). */
    fun sleep(ms: Long) {
        if (latch.await(ms, TimeUnit.MILLISECONDS)) throw CancellationException("operação cancelada")
    }

    companion object {
        private val current = ThreadLocal<CancelToken?>()

        /** Token da thread atual, se houver um [bind] ativo. */
        fun current(): CancelToken? = current.get()

        /** Associa [token] à thread atual durante [block]. */
        fun <T> bind(token: CancelToken, block: () -> T): T {
            val previous = current.get()
            current.set(token)
            try {
                return block()
            } finally {
                current.set(previous)
            }
        }
    }
}

data class HttpReply(val status: Int, val body: String, val headers: Map<String, String> = emptyMap())

/** Separa a rede do resto: nos testes entra uma implementação falsa, sem internet. */
interface HttpTransport {
    /** Lança [AiError.NoInternet] se não conseguir falar com o servidor. */
    fun post(url: String, headers: Map<String, String>, body: String): HttpReply

    /** Lê uma resposta progressivamente por linhas (SSE). Implementações falsas herdam o fallback completo. */
    fun postStream(url: String, headers: Map<String, String>, body: String, onLine: (String) -> Unit): HttpReply {
        val reply = post(url, headers, body)
        reply.body.lineSequence().forEach(onLine)
        return reply
    }
}

class JavaNetTransport(
    private val connectTimeoutMs: Int = 15_000,
    private val readTimeoutMs: Int = 60_000
) : HttpTransport {
    override fun postStream(url: String, headers: Map<String, String>, body: String, onLine: (String) -> Unit): HttpReply {
        val token = CancelToken.current()
        token?.throwIfCancelled()
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            token?.attach { conn.disconnect() }
            try {
                conn.requestMethod = "POST"
                conn.connectTimeout = connectTimeoutMs
                conn.readTimeout = readTimeoutMs
                conn.doOutput = true
                headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val status = conn.responseCode
                val stream = if (status in 200..299) conn.inputStream else conn.errorStream
                val captured = StringBuilder()
                stream?.bufferedReader(Charsets.UTF_8)?.useLines { lines ->
                    lines.forEach { line -> captured.append(line).append('\n'); onLine(line) }
                }
                val hdrs = conn.headerFields.filterKeys { it != null }
                    .mapKeys { it.key.lowercase() }.mapValues { it.value.firstOrNull().orEmpty() }
                return HttpReply(status, captured.toString(), hdrs)
            } finally { token?.attach(null); conn.disconnect() }
        } catch (e: IOException) {
            token?.throwIfCancelled()
            throw AiError.NoInternet(e.javaClass.simpleName)
        }
    }

    override fun post(url: String, headers: Map<String, String>, body: String): HttpReply {
        val token = CancelToken.current()
        token?.throwIfCancelled()
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            token?.attach { conn.disconnect() }
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
                token?.attach(null)
                conn.disconnect()
            }
        } catch (e: IOException) {
            token?.throwIfCancelled()
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
