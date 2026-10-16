package dev.rpgsolo.rules

import java.io.InputStream
import java.text.Normalizer

/**
 * Extrai o texto de um PDF, uma string por página. A implementação real fica no Android (ex.: PdfBox-Android);
 * aqui só existe o contrato, para o núcleo continuar em Kotlin puro e testável.
 */
interface PdfTextExtractor {
    fun extractPages(input: InputStream): List<String>
}

/** PDF (ou texto) que o próprio usuário inseriu. Fica só no aparelho. `systemId` liga o documento a uma edição, se ele quiser. */
data class ImportedDocument(val id: String, val title: String, val systemId: String?, val pages: List<String>)

data class DocSnippet(val docId: String, val title: String, val page: Int, val text: String, val score: Int)

sealed interface ImportDocResult {
    data class Ok(val doc: ImportedDocument) : ImportDocResult
    data class Fail(val error: String) : ImportDocResult
}

/**
 * Biblioteca de documentos do usuário com busca por trecho: só o necessário vai para a IA (plano 4.2.3).
 * O texto não é copiado para o projeto; vive no aparelho do jogador.
 */
class DocumentIndex(private val maxChunkChars: Int = 900) {
    private class Chunk(val docId: String, val title: String, val systemId: String?, val page: Int, val text: String, val norm: String)

    private val docs = linkedMapOf<String, ImportedDocument>()
    private val chunks = mutableListOf<Chunk>()

    fun documents(): List<ImportedDocument> = docs.values.toList()
    fun get(id: String): ImportedDocument? = docs[id]

    /** Lê o PDF pelo extrator e indexa. PDF sem texto (digitalizado) é recusado com aviso claro. */
    fun importPdf(id: String, title: String, systemId: String?, input: InputStream, extractor: PdfTextExtractor): ImportDocResult {
        val pages = try { extractor.extractPages(input) } catch (e: Exception) { return ImportDocResult.Fail("não consegui ler o PDF: ${e.message}") }
        return add(ImportedDocument(id, title, systemId, pages))
    }

    fun add(doc: ImportedDocument, replace: Boolean = false): ImportDocResult {
        if (doc.id.isBlank()) return ImportDocResult.Fail("id vazio")
        if (docs.containsKey(doc.id) && !replace) return ImportDocResult.Fail("documento já importado: ${doc.id}")
        if (doc.pages.sumOf { it.trim().length } < 40)
            return ImportDocResult.Fail("o PDF quase não tem texto (talvez seja digitalizado); a leitura por imagem ainda não existe")
        remove(doc.id)
        docs[doc.id] = doc
        doc.pages.forEachIndexed { i, page -> split(page).forEach { chunks += Chunk(doc.id, doc.title, doc.systemId, i + 1, it, norm(it)) } }
        return ImportDocResult.Ok(doc)
    }

    fun remove(id: String) {
        docs.remove(id)
        chunks.removeAll { it.docId == id }
    }

    /** Busca ignorando acentos e maiúsculas. `systemId` filtra por edição; documentos sem sistema sempre entram. */
    fun search(query: String, limit: Int = 5, systemId: String? = null): List<DocSnippet> {
        val tokens = norm(query).split(Regex("[^a-z0-9]+")).filter { it.length >= 3 }.distinct()
        if (tokens.isEmpty()) return emptyList()
        return chunks.asSequence()
            .filter { systemId == null || it.systemId == null || it.systemId == systemId }
            .map { c -> c to tokens.sumOf { t -> countOf(c.norm, t) } }
            .filter { it.second > 0 }
            .sortedWith(compareByDescending<Pair<Chunk, Int>> { it.second }.thenBy { it.first.title }.thenBy { it.first.page })
            .take(limit)
            .map { (c, s) -> DocSnippet(c.docId, c.title, c.page, c.text, s) }
            .toList()
    }

    private fun countOf(text: String, token: String): Int {
        var n = 0; var i = text.indexOf(token)
        while (i >= 0) { n++; i = text.indexOf(token, i + token.length) }
        return n
    }

    /** Quebra por parágrafo e junta até o tamanho máximo; parágrafo gigante é cortado. */
    private fun split(page: String): List<String> {
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        fun flush() { if (cur.isNotBlank()) out += cur.toString().trim(); cur.clear() }
        for (para in page.split(Regex("\\n\\s*\\n")).map { it.trim() }.filter { it.isNotEmpty() }) {
            if (para.length > maxChunkChars) { flush(); para.chunked(maxChunkChars).forEach { out += it }; continue }
            if (cur.length + para.length + 2 > maxChunkChars) flush()
            if (cur.isNotEmpty()) cur.append("\n\n")
            cur.append(para)
        }
        flush()
        return out
    }

    private fun norm(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").lowercase()
}
