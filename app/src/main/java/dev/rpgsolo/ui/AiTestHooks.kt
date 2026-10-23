package dev.rpgsolo.ui

import dev.rpgsolo.ai.HttpTransport
import dev.rpgsolo.ai.JavaNetTransport

/** Q-03: ponto de injeção de um transporte falso nos testes instrumentados. Em produção fica nulo (rede real). */
internal object AiTestHooks {
    @Volatile
    var transport: HttpTransport? = null

    fun transportOrDefault(): HttpTransport = transport ?: JavaNetTransport()
}
