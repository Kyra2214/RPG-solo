package dev.rpgsolo.ui

import dev.rpgsolo.ai.CancelToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Executa [block] bloqueante em `Dispatchers.IO`. Se a coroutine for cancelada (por exemplo, ao sair da tela),
 * o [CancelToken] fecha a conexão HTTP aberta e interrompe esperas de retry (R-05).
 */
suspend fun <T> cancellableIo(block: () -> T): T =
    withContext(Dispatchers.IO) {
        val token = CancelToken()
        val watcher =
            launch {
                try {
                    awaitCancellation()
                } finally {
                    token.cancel()
                }
            }
        try {
            CancelToken.bind(token, block)
        } finally {
            watcher.cancel()
        }
    }

/** Como [cancellableIo], para blocos que também suspendem (por exemplo, `withContext(Dispatchers.Main)` para streaming). */
suspend fun cancellableIoSuspend(block: suspend () -> Unit) = cancellableIo { kotlinx.coroutines.runBlocking { block() } }
