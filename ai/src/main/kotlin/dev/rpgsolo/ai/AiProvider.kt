package dev.rpgsolo.ai

/** Fase 6.1.1: contrato único para qualquer provedor de IA. O motor nunca sabe qual provedor está por trás. */
enum class Role(val wire: String) { SYSTEM("system"), USER("user"), ASSISTANT("assistant") }

data class AiMessage(val role: Role, val content: String)

data class AiRequest(
    val messages: List<AiMessage>,
    val maxTokens: Int = 1000,
    val temperature: Double? = null,
    /** Pede saída em JSON, quando o provedor suporta. A validação do formato é sempre feita por nós. */
    val jsonMode: Boolean = false
)

data class AiUsage(val inputTokens: Int, val outputTokens: Int)

data class AiResponse(val text: String, val usage: AiUsage? = null)

/** Erros claros para o jogador (6.2.3). [retryable] = vale tentar de novo sem mudar nada. */
sealed class AiError(message: String, val retryable: Boolean) : Exception(message) {
    class NoInternet(detail: String = "") :
        AiError("Sem conexão com a internet" + if (detail.isBlank()) "." else " ($detail).", true)

    class InvalidKey : AiError("Chave de API inválida ou sem permissão. Confira a chave nas configurações.", false)

    class RateLimited(val retryAfterMs: Long? = null) :
        AiError("Limite de requisições do provedor excedido. Aguarde um pouco.", true)

    class ServerError(val status: Int) : AiError("O provedor de IA falhou (HTTP $status).", true)

    class BadRequest(detail: String) : AiError("O provedor recusou o pedido: $detail", false)

    class BadResponse(detail: String) : AiError("Resposta do provedor ilegível: $detail", false)
}

interface AiProvider {
    val name: String

    /** Bloqueia até ter a resposta. Lança [AiError] em qualquer falha; nunca altera o estado do jogo. */
    fun complete(request: AiRequest): AiResponse

    /** Streaming (6.1.1). Implementação padrão: entrega a resposta inteira de uma vez. */
    fun stream(request: AiRequest, onChunk: (String) -> Unit): AiResponse =
        complete(request).also { onChunk(it.text) }
}

/** Estimativa grosseira (~4 caracteres por token) para orçamento e custo (6.2.4, 7.2.4). */
object TokenEstimator {
    fun estimate(text: String): Int = (text.length + 3) / 4
    fun estimate(request: AiRequest): Int = request.messages.sumOf { estimate(it.content) }
}
