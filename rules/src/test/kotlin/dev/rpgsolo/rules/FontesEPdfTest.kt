package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File

class FontesEPdfTest {
    private fun res(path: String): String =
        listOf("src/main/resources/$path", "rules/src/main/resources/$path").map(::File).first { it.exists() }.readText()

    private fun catalog() = (CatalogLoader.load(res("catalog/estilos.txt"), res("catalog/sistemas.txt"), res("catalog/compat.txt")) as LoadResult.Success).catalog

    // ---- biblioteca de links ----
    @Test fun fontesDoProjetoCarregamComCatalogoReal() {
        val r = SourceLibraryParser.parse(res("catalog/fontes.txt"), catalog())
        assertTrue(r.toString(), r is SourcesResult.Ok)
        val lib = SourceLibrary((r as SourcesResult.Ok).links)
        assertTrue(lib.forSystem("3det-victory").isNotEmpty())
        assertTrue(lib.links.all { it.url.startsWith("https://") })
    }

    @Test fun fonteInvalidaEhRecusadaComMensagemClara() {
        val ruim = """
            a|-|Titulo|REGRAS|http://sem-https.com|pt|SIM
            b|nao-existe|Titulo|REGRAS|https://x.com|pt|SIM
            c|-|Titulo|REGRAS|https://x.com|pt|TALVEZ
            c|-|Titulo|REGRAS|https://x.com|pt|SIM
            d|-|so quatro campos
        """.trimIndent()
        val e = (SourceLibraryParser.parse(ruim, catalog()) as SourcesResult.Fail).errors
        assertTrue(e.any { it.contains("https://") })
        assertTrue(e.any { it.contains("inexistente no catálogo") })
        assertTrue(e.any { it.contains("SIM ou NAO") })
        assertTrue(e.any { it.contains("esperado 7 campos") })
    }

    // ---- importação de PDF do usuário ----
    private val fake = object : PdfTextExtractor {
        override fun extractPages(input: java.io.InputStream): List<String> =
            input.readBytes().toString(Charsets.UTF_8).split("\u000c")
    }

    private fun pdf(vararg pages: String) = ByteArrayInputStream(pages.joinToString("\u000c").toByteArray())

    @Test fun importaEBuscaIgnorandoAcentoComPaginaCorreta() {
        val idx = DocumentIndex()
        val r = idx.importPdf("meu-dnd", "Meu livro de D&D", "dnd-5-2-srd", pdf(
            "Introdução ao jogo. Este texto fala de aventuras e de como começar a jogar com os amigos.",
            "Combate: na sua vez você pode se mover e realizar uma ação. Ataques usam o bônus de proficiência."
        ), fake)
        assertTrue(r is ImportDocResult.Ok)
        val s = idx.search("PROFICIENCIA ataque")
        assertEquals(1, s.size)
        assertEquals(2, s.first().page)
        assertEquals("meu-dnd", s.first().docId)
    }

    @Test fun buscaFiltraPorSistemaMasDocumentoSemSistemaSempreEntra() {
        val idx = DocumentIndex()
        idx.add(ImportedDocument("a", "A", "dnd-5-2-srd", listOf("Regras de iniciativa e ordem de turno no combate do jogo base.")))
        idx.add(ImportedDocument("b", "B", "sistema-proprio", listOf("Iniciativa não existe aqui; a ordem é decidida pela mesa em cada cena.")))
        idx.add(ImportedDocument("c", "C", null, listOf("Dica geral sobre iniciativa em qualquer sistema de mesa que você usar.")))
        val ids = idx.search("iniciativa", systemId = "sistema-proprio").map { it.docId }.toSet()
        assertEquals(setOf("b", "c"), ids)
    }

    @Test fun pdfSemTextoEhRecusadoERemoverApagaDoIndice() {
        val idx = DocumentIndex()
        assertTrue(idx.add(ImportedDocument("x", "X", null, listOf("", "  "))) is ImportDocResult.Fail)
        idx.add(ImportedDocument("y", "Y", null, listOf("Texto suficiente para ser indexado com folga neste teste de remoção.")))
        assertTrue(idx.search("indexado").isNotEmpty())
        idx.remove("y")
        assertTrue(idx.search("indexado").isEmpty())
        assertNull(idx.get("y"))
    }

    @Test fun paginaGrandeViraVariosTrechos() {
        val idx = DocumentIndex(maxChunkChars = 100)
        val longo = (1..30).joinToString("\n\n") { "paragrafo numero $it com palavra-chave dragao" }
        idx.add(ImportedDocument("l", "L", null, listOf(longo)))
        assertTrue(idx.search("dragao", limit = 50).size > 5)
        assertTrue(idx.search("dragao", limit = 3).size == 3)
    }

    @Test fun mesmoIdNaoSobrescreveSemPedir() {
        val idx = DocumentIndex()
        val d = ImportedDocument("z", "Z", null, listOf("Conteúdo longo o bastante para passar na checagem de texto mínimo do índice."))
        assertTrue(idx.add(d) is ImportDocResult.Ok)
        assertTrue(idx.add(d) is ImportDocResult.Fail)
        assertTrue(idx.add(d, replace = true) is ImportDocResult.Ok)
    }
}
