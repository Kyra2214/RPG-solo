package dev.rpgsolo.engine

import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/** DC-01.2: texto de DOCX (`word/document.xml`); títulos do Word viram títulos Markdown. */
object DocxText {
    const val MAX_XML_BYTES = 20 * 1024 * 1024

    private val para = Regex("<w:p[ >].*?</w:p>", RegexOption.DOT_MATCHES_ALL)
    private val token = Regex("<w:t(?: [^>]*)?>(.*?)</w:t>|<w:tab/>|<w:br/>|<w:pStyle w:val=\"([^\"]*)\"", RegexOption.DOT_MATCHES_ALL)
    private val headingStyle = Regex("(?i)^(?:heading|t[ií]tulo)\\s*([1-6])$")

    fun extract(bytes: ByteArray): String? {
        val xml = readDocumentXml(bytes) ?: return null
        val out = ArrayList<String>()
        for (p in para.findAll(xml)) {
            var level = 0
            val sb = StringBuilder()
            for (t in token.findAll(p.value)) {
                val v = t.value
                when {
                    t.groups[2] != null -> {
                        val style = t.groupValues[2]
                        level = headingStyle.matchEntire(style)?.groupValues?.get(1)?.toInt()
                            ?: if (style.equals("Title", true) || style.equals("Titulo", true)) 1 else 0
                    }
                    v == "<w:tab/>" -> sb.append('\t')
                    v == "<w:br/>" -> sb.append('\n')
                    else -> sb.append(unescape(t.groupValues[1]))
                }
            }
            val line = sb.toString().trim()
            if (line.isNotEmpty()) out.add(if (level > 0) "#".repeat(level) + " " + line else line)
        }
        return out.joinToString("\n\n")
    }

    private fun readDocumentXml(bytes: ByteArray): String? = try {
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var result: String? = null
            while (true) {
                val e = zip.nextEntry ?: break
                if (e.name == "word/document.xml") {
                    val buf = java.io.ByteArrayOutputStream()
                    val chunk = ByteArray(8192)
                    var total = 0
                    while (true) {
                        val n = zip.read(chunk)
                        if (n < 0) break
                        total += n
                        if (total > MAX_XML_BYTES) return null
                        buf.write(chunk, 0, n)
                    }
                    result = buf.toString(Charsets.UTF_8.name())
                    break
                }
            }
            result
        }
    } catch (_: Exception) {
        null
    }

    private fun unescape(s: String): String =
        Regex("&(#x?[0-9A-Fa-f]+|amp|lt|gt|quot|apos);").replace(s) { m ->
            when (val k = m.groupValues[1]) {
                "amp" -> "&"; "lt" -> "<"; "gt" -> ">"; "quot" -> "\""; "apos" -> "'"
                else -> {
                    val code = if (k[1] == 'x' || k[1] == 'X') k.drop(2).toIntOrNull(16) else k.drop(1).toIntOrNull()
                    if (code != null && code in 1..0x10FFFF) String(Character.toChars(code)) else m.value
                }
            }
        }
}
