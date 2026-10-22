package dev.rpgsolo.ai

import java.util.concurrent.locks.ReentrantLock

/**
 * Exclusividade de turno: uma jogada por vez sobre a mesma sessão. Diferente da trava interna de `CampaignSession`,
 * pode ficar presa durante chamadas de rede, pois protege sequências `mark()` → trabalho → `rollbackTo()`.
 * Reentrante: fluxos compostos podem chamar outros fluxos na mesma thread. Não chamar da thread principal.
 */
class TurnGate {
    @PublishedApi internal val lock = ReentrantLock()

    inline fun <T> exclusive(block: () -> T): T {
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    /** true se alguma thread está dentro de um turno. */
    val busy: Boolean get() = lock.isLocked
}
