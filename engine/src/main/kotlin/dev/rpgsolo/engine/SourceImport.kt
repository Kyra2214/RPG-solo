package dev.rpgsolo.engine

import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
import java.security.MessageDigest

enum class SourceFormat { TXT, MARKDOWN, DOCX, PDF }

/** Trecho de uma fonte: [heading] vazio para o texto anterior ao primeiro título. */
data class SourceSection(val heading: String, val level: Int, val text: String)

/** Fonte importada (DC-01.1): texto normalizado, seções, hash do arquivo original e tamanho. */
data class ImportedSource(
    val fileName: String,
    val format: SourceFormat,
    val title: String,
    val text: String,
    val sections: List<SourceSection>,
    val sha256: String,
    val byteSize: Int
)

sealed class SourceImportResult {
    data class Ok(val source: ImportedSource) : SourceImportResult()
    data class Failed(val reason: String) : SourceImportResult()
}

/** DC-01.1: importa TXT e Markdown. Puro: não grava nada nem altera estado de campanha. */
object SourceImporter {
    const val MAX_BYTES = 5 * 1024 * 1024

    private val headingRe = Regex("^ {0,3}(#{1,6})[ \\t]+(.+?)[ \\t#]*$")

    fun formatOf(fileName: String): SourceFormat? =
        when (fileName.substringAfterLast('.', "").lowercase()) {
            "txt" -> SourceFormat.TXT
            "md", "markdown" -> SourceFormat.MARKDOWN
            "docx" -> SourceFormat.DOCX
            "pdf" -> SourceFormat.PDF
            else -> null
        }

    fun import(fileName: String, bytes: ByteArray): SourceImportResult {
        val format = formatOf(fileName)
            ?: return SourceImportResult.Failed("Formato não suportado (use .txt, .md, .docx ou .pdf).")
        if (bytes.isEmpty()) return SourceImportResult.Failed("Arquivo vazio.")
        if (bytes.size > MAX_BYTES) return SourceImportResult.Failed("Arquivo maior que ${MAX_BYTES / (1024 * 1024)} MiB.")
        val raw = when (format) {
            SourceFormat.DOCX -> DocxText.extract(bytes) ?: return SourceImportResult.Failed("DOCX inválido ou sem document.xml.")
            SourceFormat.PDF -> PdfText.extract(bytes) ?: return SourceImportResult.Failed("PDF sem texto extraível (escaneado, protegido ou com fonte codificada).")
            else -> {
                if (bytes.any { it == 0.toByte() }) return SourceImportResult.Failed("Arquivo não parece ser texto.")
                decode(bytes)
            }
        }
        val text = normalize(raw)
        if (text.isBlank()) return SourceImportResult.Failed("Arquivo sem texto.")
        val sections = if (format == SourceFormat.MARKDOWN || format == SourceFormat.DOCX) markdownSections(text) else listOf(SourceSection("", 0, text))
        val title = sections.firstOrNull { it.heading.isNotEmpty() }?.heading
            ?: fileName.substringBeforeLast('.').trim().ifEmpty { fileName }
        return SourceImportResult.Ok(ImportedSource(fileName, format, title, text, sections, sha256(bytes), bytes.size))
    }

    fun sha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun decode(bytes: ByteArray): String {
        val start = if (bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) 3 else 0
        val decoder = Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
        return try {
            decoder.decode(ByteBuffer.wrap(bytes, start, bytes.size - start)).toString()
        } catch (_: CharacterCodingException) {
            String(bytes, start, bytes.size - start, Charsets.ISO_8859_1)
        }
    }

    private fun normalize(raw: String): String =
        raw.replace("\r\n", "\n").replace('\r', '\n').lines()
            .joinToString("\n") { it.trimEnd() }
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()

    private fun markdownSections(text: String): List<SourceSection> {
        val out = ArrayList<SourceSection>()
        var heading = ""
        var level = 0
        val body = StringBuilder()
        var fenced = false
        fun flush() {
            val t = body.toString().trim()
            if (heading.isNotEmpty() || t.isNotEmpty()) out.add(SourceSection(heading, level, t))
            body.clear()
        }
        for (line in text.split('\n')) {
            if (line.trimStart().startsWith("```") || line.trimStart().startsWith("~~~")) fenced = !fenced
            val m = if (fenced) null else headingRe.matchEntire(line)
            if (m != null) {
                flush()
                level = m.groupValues[1].length
                heading = m.groupValues[2].trim()
            } else {
                body.append(line).append('\n')
            }
        }
        flush()
        return out
    }
}
