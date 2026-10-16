package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Chapter
import dev.rpgsolo.engine.ChapterBook
import dev.rpgsolo.engine.digestOf

/** Fase 9.2.2: o resumo é gerado uma vez. Se a IA falhar, o capítulo continua aberto e nada se perde. */
class ChapterSummarizer(private val provider: AiProvider) {
    sealed interface Result {
        data class Closed(val chapter: Chapter) : Result
        data class Failed(val reason: String, val error: AiError? = null) : Result
    }

    fun closeWithSummary(session: CampaignSession, book: ChapterBook, narrations: List<String>): Result {
        val cur = book.current() ?: return Result.Failed("não há capítulo aberto")
        val d = digestOf(book.eventsOfCurrent(session))
        val prompt = buildString {
            append("Resuma em até 6 frases, em português, o capítulo '${cur.title}' do ponto de vista do personagem. ")
            append("Não inclua números de ficha, vida ou dados, nem segredos que o jogador não descobriu.\n")
            if (narrations.isNotEmpty()) append("Cenas:\n").append(narrations.joinToString("\n")).append('\n')
            if (d.itemDeltas.isNotEmpty()) append("Itens: ").append(d.itemDeltas.entries.joinToString { "${it.key} ${it.value}" }).append('\n')
            if (d.deaths.isNotEmpty()) append("Mortes: ").append(d.deaths.joinToString()).append('\n')
            if (d.promisesMade.isNotEmpty()) append("Promessas feitas: ").append(d.promisesMade.joinToString()).append('\n')
            if (d.promisesKept.isNotEmpty()) append("Promessas cumpridas: ").append(d.promisesKept.joinToString()).append('\n')
        }
        val text = try {
            provider.complete(AiRequest(listOf(AiMessage(Role.USER, prompt)), maxTokens = 400)).text.trim()
        } catch (e: AiError) {
            return Result.Failed(e.message ?: "erro de IA", e)
        }
        if (text.isBlank()) return Result.Failed("a IA devolveu resumo vazio")
        return Result.Closed(book.close(session, text))
    }
}
