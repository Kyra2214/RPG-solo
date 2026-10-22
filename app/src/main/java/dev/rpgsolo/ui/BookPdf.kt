package dev.rpgsolo.ui

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import dev.rpgsolo.engine.AdventureBook
import java.io.ByteArrayOutputStream

/** CP-04.1: desenha o [AdventureBook] em PDF (A4) com a API do Android; sem dependências novas. Só lê o livro. */
object BookPdf {
    private const val W = 595
    private const val H = 842
    private const val MARGIN = 48f

    fun render(book: AdventureBook): ByteArray {
        val title = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 26f; typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD) }
        val sub = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 14f; typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC) }
        val head = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 18f; typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD) }
        val body = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 12f; typeface = Typeface.SERIF }
        val pdf = PdfDocument()
        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var y = 0f

        fun newPage() {
            page?.let { pdf.finishPage(it) }
            pageNumber++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(W, H, pageNumber).create())
            y = MARGIN
        }

        fun line(text: String, paint: Paint, gap: Float = 4f) {
            val height = paint.fontSpacing
            if (page == null || y + height > H - MARGIN) newPage()
            y += height
            page!!.canvas.drawText(text, MARGIN, y - paint.descent(), paint)
            y += gap
        }

        fun paragraph(text: String, paint: Paint) {
            text.split('\n').forEach { raw ->
                wrap(raw, paint, W - 2 * MARGIN).forEach { line(it, paint, 2f) }
            }
            y += 6f
        }

        newPage()
        wrap(book.title, title, W - 2 * MARGIN).forEach { line(it, title, 6f) }
        line(book.subtitle, sub, 18f)
        book.sections.forEach { section ->
            y += 8f
            line(section.heading, head, 8f)
            section.lines.forEach { paragraph(it, body) }
        }
        page?.let { pdf.finishPage(it) }
        return ByteArrayOutputStream().also { out -> pdf.writeTo(out); pdf.close() }.toByteArray()
    }

    /** Quebra [text] em linhas que cabem em [width]; palavras maiores que a linha são partidas. */
    internal fun wrap(text: String, paint: Paint, width: Float): List<String> {
        if (text.isBlank()) return listOf("")
        val lines = mutableListOf<String>()
        var current = ""
        for (word in text.trim().split(Regex("\\s+"))) {
            var rest = word
            while (paint.measureText(rest) > width) {
                if (current.isNotEmpty()) { lines += current; current = "" }
                val fit = paint.breakText(rest, true, width, null).coerceAtLeast(1)
                lines += rest.substring(0, fit)
                rest = rest.substring(fit)
            }
            val candidate = if (current.isEmpty()) rest else "$current $rest"
            if (paint.measureText(candidate) <= width) current = candidate
            else { lines += current; current = rest }
        }
        if (current.isNotEmpty()) lines += current
        return lines
    }
}
