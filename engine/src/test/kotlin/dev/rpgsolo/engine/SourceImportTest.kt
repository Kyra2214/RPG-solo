package dev.rpgsolo.engine

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SourceImportTest {
    private fun ok(
        name: String,
        bytes: ByteArray,
    ) = (SourceImporter.import(name, bytes) as SourceImportResult.Ok).source

    private fun failed(
        name: String,
        bytes: ByteArray,
    ) = (SourceImporter.import(name, bytes) as SourceImportResult.Failed).reason

    private fun docx(xml: String): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { z ->
            z.putNextEntry(ZipEntry("word/document.xml"))
            z.write(xml.toByteArray(Charsets.UTF_8))
            z.closeEntry()
        }
        return out.toByteArray()
    }

    private fun pdf(
        content: String,
        flate: Boolean = false,
    ): ByteArray {
        var data = content.toByteArray(Charsets.ISO_8859_1)
        var filter = ""
        if (flate) {
            val d = Deflater()
            d.setInput(data)
            d.finish()
            val buf = ByteArray(4096)
            val n = d.deflate(buf)
            d.end()
            data = buf.copyOf(n)
            filter = " /Filter /FlateDecode"
        }
        val head = "%PDF-1.4\n1 0 obj\n<< /Length ${data.size}$filter >>\nstream\n".toByteArray(Charsets.ISO_8859_1)
        return head + data + "\nendstream\nendobj\n%%EOF".toByteArray(Charsets.ISO_8859_1)
    }

    @Test fun txtComAcentosNormalizaQuebras() {
        val s = ok("historia.txt", "Olá, mundo!\r\n\r\n\r\n\r\nAção   \r\nfim".toByteArray())
        assertEquals("Olá, mundo!\n\nAção\nfim", s.text)
        assertEquals("historia", s.title)
        assertEquals(SourceFormat.TXT, s.format)
    }

    @Test fun utf8ComBomEIso88591() {
        val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) + "Ação".toByteArray()
        assertEquals("Ação", ok("a.txt", bom).text)
        assertEquals("Ação", ok("b.txt", "Ação".toByteArray(Charsets.ISO_8859_1)).text)
    }

    @Test fun markdownSeparaSecoesEIgnoraBlocoDeCodigo() {
        val s = ok("lore.md", "# Título\nintro\n## Sub\ntexto\n```\n# não é título\n```\n".toByteArray())
        assertEquals("Título", s.title)
        assertEquals(2, s.sections.size)
        assertEquals(2, s.sections[1].level)
        assertTrue(s.sections[1].text.contains("# não é título"))
    }

    @Test fun recusasComMotivo() {
        assertTrue(failed("a.doc", "x".toByteArray()).contains("não suportado"))
        assertTrue(failed("a.txt", ByteArray(0)).contains("vazio"))
        assertTrue(failed("a.txt", ByteArray(SourceImporter.MAX_BYTES + 1) { 'a'.code.toByte() }).contains("MiB"))
        assertTrue(failed("a.txt", byteArrayOf(65, 0, 66)).contains("texto"))
        assertTrue(failed("a.txt", "  \n \n".toByteArray()).contains("sem texto"))
    }

    @Test fun hashEstavelEDiferenteParaOutroConteudo() {
        val a = ok("a.txt", "um".toByteArray())
        val b = ok("b.txt", "um".toByteArray())
        val c = ok("c.txt", "dois".toByteArray())
        assertEquals(a.sha256, b.sha256)
        assertNotEquals(a.sha256, c.sha256)
        assertEquals(64, a.sha256.length)
    }

    @Test fun docxTitulosEntidadesETabulacao() {
        val xml = """<w:document><w:body><w:p><w:pPr><w:pStyle w:val="Heading1"/></w:pPr><w:r><w:t>Capítulo Um</w:t></w:r></w:p>
            <w:p><w:r><w:t xml:space="preserve">Texto &amp; mais</w:t></w:r><w:r><w:tab/></w:r><w:r><w:t>fim</w:t></w:r></w:p></w:body></w:document>"""
        val s = ok("livro.docx", docx(xml))
        assertEquals("Capítulo Um", s.title)
        assertEquals(1, s.sections.size)
        assertTrue(s.text.startsWith("# Capítulo Um"))
        assertTrue(s.text.contains("Texto & mais\tfim"))
    }

    @Test fun docxInvalidoFalha() {
        assertTrue(failed("x.docx", "não é zip".toByteArray()).contains("DOCX"))
    }

    @Test fun pdfComTextoSemFiltroEComFlate() {
        val content = "BT /F1 12 Tf 72 700 Td (Texto de teste do documento fonte com Ação) Tj ET"
        assertTrue(ok("a.pdf", pdf(content)).text.contains("Ação"))
        assertTrue(ok("b.pdf", pdf(content, flate = true)).text.contains("documento fonte"))
    }

    @Test fun pdfSemTextoOuProtegidoFalha() {
        assertTrue(failed("a.pdf", pdf("0 0 m 10 10 l S")).contains("PDF"))
        assertTrue(failed("b.pdf", "%PDF-1.4 /Encrypt".toByteArray()).contains("PDF"))
    }
}
