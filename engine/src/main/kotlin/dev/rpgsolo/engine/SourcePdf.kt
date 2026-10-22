package dev.rpgsolo.engine

import java.io.ByteArrayOutputStream
import java.util.zip.Inflater

/**
 * DC-01.3: extração best-effort de texto de PDF com texto, sem biblioteca externa.
 * Lê fluxos de conteúdo (sem filtro ou FlateDecode) e os operadores Tj, TJ, ' e ". Fontes sem codificação
 * simples (CID/Identity-H) e PDFs escaneados não rendem texto e resultam em falha (sem OCR).
 */
object PdfText {
    const val MAX_STREAM_BYTES = 20 * 1024 * 1024

    private val win1252 = charset("windows-1252")
    private val tok = Regex("\\((?:\\\\.|[^\\\\()])*\\)|<[0-9A-Fa-f\\s]*>|\\[|\\]|-?\\d+\\.?\\d*|T\\*|TD|Td|TJ|Tj|ET|BT|'|\"", RegexOption.DOT_MATCHES_ALL)

    fun extract(bytes: ByteArray): String? {
        if (bytes.size < 8 || String(bytes, 0, 5, Charsets.ISO_8859_1) != "%PDF-") return null
        val raw = String(bytes, Charsets.ISO_8859_1)
        if (raw.contains("/Encrypt")) return null
        val lines = ArrayList<String>()
        var pos = 0
        while (true) {
            val s = raw.indexOf("stream", pos)
            if (s < 0) break
            if (s >= 3 && raw.startsWith("end", s - 3)) { pos = s + 6; continue }
            var start = s + 6
            if (raw.startsWith("\r\n", start)) start += 2 else if (start < raw.length && (raw[start] == '\n' || raw[start] == '\r')) start += 1
            val end = raw.indexOf("endstream", start)
            if (end < 0) break
            pos = end + 9
            val dict = raw.substring(maxOf(0, raw.lastIndexOf("<<", s).let { if (it < 0) s else it }), s)
            if (dict.contains("/Image") || dict.contains("/FontFile") || dict.contains("/XRef") || dict.contains("/ObjStm")) continue
            val data = bytes.copyOfRange(start, end)
            val content = when {
                dict.contains("FlateDecode") -> inflate(data) ?: continue
                dict.contains("/Filter") -> continue
                else -> data
            }
            val text = String(content, Charsets.ISO_8859_1)
            if (!text.contains("BT")) continue
            textOf(text, lines)
        }
        val result = lines.joinToString("\n").trim()
        val letters = result.count { it.isLetter() }
        return if (letters < 20 || letters * 2 < result.length / 2) null else result
    }

    private fun textOf(c: String, out: MutableList<String>) {
        val line = StringBuilder()
        val pending = ArrayList<String>()
        var inArray = false
        fun flushLine() {
            val t = line.toString().trim()
            if (t.isNotEmpty()) out.add(t)
            line.clear()
        }
        for (m in tok.findAll(c)) {
            val v = m.value
            when {
                v.startsWith("(") -> pending.add(literal(v))
                v.startsWith("<") -> pending.add(hex(v))
                v == "[" -> inArray = true
                v == "]" -> inArray = false
                v == "Tj" || v == "TJ" || v == "'" || v == "\"" -> {
                    if (v == "'" || v == "\"") flushLine()
                    pending.forEach { line.append(it) }
                    pending.clear()
                }
                v == "Td" || v == "TD" || v == "T*" || v == "ET" -> { pending.clear(); flushLine() }
                v == "BT" -> pending.clear()
                else -> {
                    val n = v.toDoubleOrNull()
                    if (n != null && inArray && n < -200 && pending.isNotEmpty()) pending.add(" ")
                }
            }
        }
        flushLine()
    }

    private fun literal(v: String): String {
        val b = ByteArrayOutputStream()
        val s = v.substring(1, v.length - 1)
        var i = 0
        while (i < s.length) {
            val ch = s[i]
            if (ch != '\\') { b.write(ch.code and 0xFF); i++; continue }
            i++
            if (i >= s.length) break
            when (val e = s[i]) {
                'n' -> { b.write(10); i++ }
                'r' -> { b.write(13); i++ }
                't' -> { b.write(9); i++ }
                'b' -> { b.write(8); i++ }
                'f' -> { b.write(12); i++ }
                '\n' -> i++
                '\r' -> { i++; if (i < s.length && s[i] == '\n') i++ }
                in '0'..'7' -> {
                    var n = 0
                    var k = 0
                    while (k < 3 && i < s.length && s[i] in '0'..'7') { n = n * 8 + (s[i] - '0'); i++; k++ }
                    b.write(n and 0xFF)
                }
                else -> { b.write(e.code and 0xFF); i++ }
            }
        }
        return decodeBytes(b.toByteArray())
    }

    private fun hex(v: String): String {
        val h = v.substring(1, v.length - 1).filter { !it.isWhitespace() }.let { if (it.length % 2 == 1) it + "0" else it }
        val bytes = ByteArray(h.length / 2) { h.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
        return decodeBytes(bytes)
    }

    private fun decodeBytes(b: ByteArray): String {
        if (b.size >= 2 && b[0] == 0xFE.toByte() && b[1] == 0xFF.toByte()) return String(b, 2, b.size - 2, Charsets.UTF_16BE)
        if (b.any { it == 0.toByte() }) return ""
        return String(b, win1252).filter { !it.isISOControl() || it == '\t' }
    }

    private fun inflate(data: ByteArray): ByteArray? {
        val inf = Inflater()
        return try {
            inf.setInput(data)
            val out = ByteArrayOutputStream()
            val buf = ByteArray(8192)
            while (!inf.finished()) {
                val n = inf.inflate(buf)
                if (n == 0 && (inf.needsInput() || inf.needsDictionary())) break
                out.write(buf, 0, n)
                if (out.size() > MAX_STREAM_BYTES) return null
            }
            out.toByteArray()
        } catch (_: Exception) {
            null
        } finally {
            inf.end()
        }
    }
}
