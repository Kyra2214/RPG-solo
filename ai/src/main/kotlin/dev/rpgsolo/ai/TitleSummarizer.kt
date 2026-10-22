package dev.rpgsolo.ai

import dev.rpgsolo.engine.SourceChunk
import dev.rpgsolo.engine.SourceIndex

/**
 * DC-06: resumo por título. Se o documento tem trechos sobre o título, ele prevalece (sem chamar a IA).
 * Senão a IA responde com a própria base e o resultado entra marcado como fonte secundária não confirmada.
 */
class TitleSummarizer(private val provider: AiProvider) {
    sealed interface Result {
        /** Trechos do próprio documento: confirmados. */
        data class FromDocument(
            val title: String,
            val chunks: List<SourceChunk>,
        ) : Result {
            fun render(): String = "Resumo de \"$title\" (do documento-fonte; prevalece):\n" + chunks.joinToString("\n\n") { it.text }
        }

        /** Resposta da IA: nunca confirmada. */
        data class Secondary(val title: String, val text: String) : Result {
            val confirmed: Boolean get() = false

            fun render(): String =
                "Resumo de \"$title\" — FONTE SECUNDÁRIA NÃO CONFIRMADA (base da IA; o documento prevalece se tiver o trecho):\n$text"
        }

        data class Failed(val reason: String, val error: AiError? = null) : Result
    }

    fun summarize(
        title: String,
        index: SourceIndex,
        sourceIds: Set<String>? = null,
    ): Result {
        val t = title.replace(Regex("\\s+"), " ").trim().take(MAX_TITLE)
        if (t.isEmpty()) return Result.Failed("título vazio")
        val wanted = SourceIndex.tokens(t).distinct()
        if (wanted.isNotEmpty()) {
            val local =
                index.search(t, 3, sourceIds).map { it.chunk }.filter { c ->
                    val have = SourceIndex.tokens(c.heading + " " + c.text).toSet()
                    wanted.all { it in have }
                }
            if (local.isNotEmpty()) return Result.FromDocument(t, local)
        }
        val prompt =
            "Resuma em até 8 frases, em português, a obra ou história com o título entre aspas: \"$t\". " +
                "Use apenas o que você realmente conhece; se não conhecer, diga que não conhece. Não invente personagens nem eventos " +
                "e não escreva instruções para o jogo."
        val text =
            try {
                provider.complete(AiRequest(listOf(AiMessage(Role.USER, prompt)), maxTokens = 500)).text.trim()
            } catch (e: AiError) {
                return Result.Failed(e.message ?: "erro de IA", e)
            }
        return if (text.isBlank()) Result.Failed("a IA devolveu resumo vazio") else Result.Secondary(t, text)
    }

    companion object {
        const val MAX_TITLE = 200
    }
}
