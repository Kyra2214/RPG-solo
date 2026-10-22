package dev.rpgsolo.ai

/** Provedor de mentira: devolve respostas (ou erros) na ordem e guarda os pedidos recebidos. */
class ScriptedProvider(private val script: List<Any>) : AiProvider {
    override val name = "scripted"
    val requests = mutableListOf<AiRequest>()
    private var next = 0

    override fun complete(request: AiRequest): AiResponse {
        requests += request
        val item = script.getOrElse(next++) { error("roteiro acabou") }
        return when (item) {
            is AiError -> throw item
            is String -> AiResponse(item)
            is AiResponse -> item
            else -> error("item inválido no roteiro")
        }
    }
}

class FakeTransport(private val replies: List<HttpReply>) : HttpTransport {
    var lastUrl = ""
    var lastHeaders: Map<String, String> = emptyMap()
    var lastBody = ""
    private var next = 0

    override fun post(
        url: String,
        headers: Map<String, String>,
        body: String,
    ): HttpReply {
        lastUrl = url
        lastHeaders = headers
        lastBody = body
        return replies[next++]
    }
}

class RecordingSleeper : Sleeper {
    val waits = mutableListOf<Long>()

    override fun sleep(ms: Long) {
        waits += ms
    }
}
