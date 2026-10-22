package dev.rpgsolo.engine

import org.junit.Assert.*
import org.junit.Test

class SourcePipelineTest {
    private fun source(): ImportedSource {
        val paras = (1..4).joinToString("\n\n") { i -> "marcador$i ".repeat(90).trim() }
        val text = "# Lore\nO dragão vive na montanha.\n\n$paras"
        return (SourceImporter.import("lore.md", text.toByteArray()) as SourceImportResult.Ok).source
    }

    @Test fun chunkEBuscaSemAcentos() {
        val chunks = SourceChunker.chunk("s1", source())
        assertTrue(chunks.size >= 4)
        assertTrue(chunks.all { it.text.length <= 1200 })
        val idx = SourceIndex().also { it.addAll(chunks) }
        assertEquals(chunks.first().id, idx.search("DRAGAO montanha").first().chunk.id)
        assertTrue(idx.search("inexistente").isEmpty())
        assertEquals(chunks[2].id, idx.search("marcador3").first().chunk.id)
    }

    @Test fun pipelineCompletaComProgresso() {
        val idx = SourceIndex()
        val seen = ArrayList<Float>()
        val end = SourcePipeline.run(SourcePipeline.start("s1"), source(), idx, onProgress = { seen += it.progress })
        assertEquals(PipelineStage.INDEXED, end.stage)
        assertEquals(1f, end.progress, 0f)
        assertEquals(end.chunks.size, idx.size)
        assertTrue(seen == seen.sorted())
    }

    @Test fun retomaDeOndeParou() {
        val idx = SourceIndex()
        var s = SourcePipeline.step(SourcePipeline.start("s1"), source(), idx)
        s = SourcePipeline.step(s, null, idx, batch = 1)
        assertEquals(1, s.indexedCount)
        assertEquals(PipelineStage.CHUNKED, s.stage)
        val idx2 = SourcePipeline.resume(s)
        assertEquals(1, idx2.size)
        val end = SourcePipeline.run(s, null, idx2)
        assertEquals(PipelineStage.INDEXED, end.stage)
        assertEquals(end.chunks.size, idx2.size)
    }

    @Test fun pararNoMeioEFalhaSemFonte() {
        var calls = 0
        val s = SourcePipeline.run(SourcePipeline.start("s1"), source(), SourceIndex(), shouldStop = { calls++ >= 1 })
        assertFalse(s.done)
        assertEquals(PipelineStage.FAILED, SourcePipeline.step(SourcePipeline.start("x"), null, SourceIndex()).stage)
    }

    @Test fun estadoSerializaEIndiceRemoveFonte() {
        val idx = SourceIndex()
        val end = SourcePipeline.run(SourcePipeline.start("s1"), source(), idx)
        idx.remove("s1")
        assertEquals(0, idx.size)
        assertEquals(end.chunks.size, SourcePipeline.resume(end).size)
    }
}
