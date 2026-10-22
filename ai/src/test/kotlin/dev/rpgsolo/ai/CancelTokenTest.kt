package dev.rpgsolo.ai

import java.net.ServerSocket
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/** R-05: cancelar interrompe esperas e conexões abertas com [CancellationException], sem retentativa. */
class CancelTokenTest {
    private val req = AiRequest(listOf(AiMessage(Role.USER, "oi")))

    /** Executa [work] numa thread com [token] ligado e devolve o que ela lançou (ou null). */
    private fun runBound(token: CancelToken, started: CountDownLatch? = null, work: () -> Unit): Pair<Thread, AtomicReference<Throwable?>> {
        val error = AtomicReference<Throwable?>(null)
        val t = thread {
            try {
                CancelToken.bind(token) { started?.countDown(); work() }
            } catch (e: Throwable) {
                error.set(e)
            }
        }
        return t to error
    }

    @Test fun esperaLongaTerminaNoCancelamento() {
        val token = CancelToken()
        val started = CountDownLatch(1)
        val (t, error) = runBound(token, started) { ThreadSleeper.sleep(60_000) }
        assertTrue(started.await(5, TimeUnit.SECONDS))
        token.cancel()
        t.join(5_000)
        assertTrue("espera não terminou", !t.isAlive)
        assertTrue(error.get() is CancellationException)
    }

    @Test fun tokenJaCanceladoRecusaTrabalhoEExecutaAcaoNaHora() {
        val token = CancelToken()
        token.cancel()
        var closed = false
        token.attach { closed = true }
        assertTrue(closed)
        try { token.throwIfCancelled(); fail() } catch (e: CancellationException) { }
        try { token.sleep(10); fail() } catch (e: CancellationException) { }
    }

    @Test fun retryNaoRepeteDepoisDeCancelar() {
        val token = CancelToken()
        val started = CountDownLatch(1)
        var calls = 0
        val inner = object : AiProvider {
            override val name = "falha"
            override fun complete(request: AiRequest): AiResponse {
                calls++
                started.countDown()
                throw AiError.NoInternet()
            }
        }
        val provider = RetryingProvider(inner, RetryPolicy(maxAttempts = 3, baseDelayMs = 60_000, maxDelayMs = 60_000))
        val (t, error) = runBound(token) { provider.complete(req) }
        assertTrue(started.await(5, TimeUnit.SECONDS))
        Thread.sleep(100)
        token.cancel()
        t.join(5_000)
        assertTrue("retry não terminou", !t.isAlive)
        assertTrue(error.get() is CancellationException)
        assertEquals(1, calls)
    }

    @Test fun conexaoPenduradaEFechadaNoCancelamento() {
        ServerSocket(0).use { server ->
            val accepted = CountDownLatch(1)
            val acceptor = thread { try { server.accept().use { accepted.countDown(); Thread.sleep(30_000) } } catch (_: Exception) { } }
            val token = CancelToken()
            val transport = JavaNetTransport(connectTimeoutMs = 5_000, readTimeoutMs = 60_000)
            val (t, error) = runBound(token) { transport.post("http://127.0.0.1:${server.localPort}/x", emptyMap(), "{}") }
            assertTrue(accepted.await(5, TimeUnit.SECONDS))
            Thread.sleep(200)
            token.cancel()
            t.join(5_000)
            assertTrue("requisição não foi interrompida", !t.isAlive)
            assertTrue("esperado CancellationException, veio ${error.get()}", error.get() is CancellationException)
            acceptor.interrupt()
        }
    }
}
