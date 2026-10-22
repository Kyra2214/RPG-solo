package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.Normalizer
import kotlin.math.ln

/** Trecho de uma fonte (DC-03). */
@Serializable
data class SourceChunk(val id: String, val sourceId: String, val ordinal: Int, val heading: String, val text: String)

data class SearchHit(val chunk: SourceChunk, val score: Double)

/** DC-03: divide a fonte em trechos por seção e parágrafo. */
object SourceChunker {
    fun chunk(sourceId: String, source: ImportedSource, maxChars: Int = 1200): List<SourceChunk> {
        val out = ArrayList<SourceChunk>()
        fun emit(heading: String, text: String) {
            val t = text.trim()
            if (t.isNotEmpty()) out.add(SourceChunk("$sourceId#${out.size}", sourceId, out.size, heading, t))
        }
        for (sec in source.sections) {
            val buf = StringBuilder()
            for (p in sec.text.split("\n\n")) {
                for (piece in hardSplit(p.trim(), maxChars)) {
                    if (buf.isNotEmpty() && buf.length + piece.length + 2 > maxChars) {
                        emit(sec.heading, buf.toString()); buf.clear()
                    }
                    if (buf.isNotEmpty()) buf.append("\n\n")
                    buf.append(piece)
                }
            }
            emit(sec.heading, buf.toString())
        }
        return out
    }

    private fun hardSplit(p: String, max: Int): List<String> {
        if (p.length <= max) return listOf(p).filter { it.isNotEmpty() }
        val parts = ArrayList<String>()
        var rest = p
        while (rest.length > max) {
            val cut = rest.lastIndexOf(' ', max).let { if (it < max / 2) max else it }
            parts.add(rest.substring(0, cut).trim())
            rest = rest.substring(cut).trim()
        }
        if (rest.isNotEmpty()) parts.add(rest)
        return parts
    }
}

/** DC-03: índice invertido local (TF-IDF simples, sem acentos nem embeddings). Reconstruível a partir dos trechos. */
class SourceIndex {
    private val chunks = ArrayList<SourceChunk>()
    private val ids = HashSet<String>()
    private val postings = HashMap<String, HashMap<Int, Int>>()

    val size: Int get() = chunks.size

    fun add(chunk: SourceChunk) {
        if (!ids.add(chunk.id)) return
        val idx = chunks.size
        chunks.add(chunk)
        for (t in tokens(chunk.heading + " " + chunk.text)) postings.getOrPut(t) { HashMap() }.merge(idx, 1, Int::plus)
    }

    fun addAll(list: List<SourceChunk>) = list.forEach { add(it) }

    fun remove(sourceId: String) {
        val keep = chunks.filter { it.sourceId != sourceId }
        chunks.clear(); ids.clear(); postings.clear()
        addAll(keep)
    }

    fun get(chunkId: String): SourceChunk? = chunks.firstOrNull { it.id == chunkId }

    fun search(query: String, limit: Int = 5, sourceIds: Set<String>? = null): List<SearchHit> {
        val terms = tokens(query).distinct()
        if (terms.isEmpty() || chunks.isEmpty()) return emptyList()
        val scores = HashMap<Int, Double>()
        for (t in terms) {
            val post = postings[t] ?: continue
            val idf = ln(1.0 + chunks.size.toDouble() / post.size)
            for ((i, tf) in post) scores.merge(i, (1.0 + ln(tf.toDouble())) * idf, Double::plus)
        }
        return scores.entries
            .filter { sourceIds == null || chunks[it.key].sourceId in sourceIds }
            .sortedWith(compareByDescending<Map.Entry<Int, Double>> { it.value }.thenBy { it.key })
            .take(limit).map { SearchHit(chunks[it.key], it.value) }
    }

    companion object {
        private val stop = setOf(
            "que", "uma", "uns", "umas", "com", "para", "por", "dos", "das", "nos", "nas", "mas", "como", "mais", "seu", "sua",
            "seus", "suas", "ele", "ela", "eles", "elas", "este", "esta", "esse", "essa", "isso", "ao", "aos", "pelo", "pela",
            "the", "and", "for", "with", "was", "his", "her", "that", "this", "are", "you"
        )

        fun tokens(text: String): List<String> =
            Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
                .replace(Regex("\\p{M}+"), "")
                .split(Regex("[^\\p{L}\\p{N}]+"))
                .filter { it.length >= 3 && it !in stop }
    }
}

@Serializable
enum class PipelineStage { IMPORTED, CHUNKED, INDEXED, FAILED }

/** Estado persistível da pipeline: permite retomar do ponto em que parou. */
@Serializable
data class PipelineState(
    val sourceId: String,
    val stage: PipelineStage = PipelineStage.IMPORTED,
    val chunks: List<SourceChunk> = emptyList(),
    val indexedCount: Int = 0,
    val error: String? = null
) {
    val progress: Float
        get() = when (stage) {
            PipelineStage.IMPORTED -> 0f
            PipelineStage.CHUNKED -> if (chunks.isEmpty()) 0f else 0.1f + 0.9f * indexedCount / chunks.size
            PipelineStage.INDEXED -> 1f
            PipelineStage.FAILED -> 0f
        }
    val done: Boolean get() = stage == PipelineStage.INDEXED || stage == PipelineStage.FAILED
}

/** DC-03: importada → em trechos → indexada, em lotes e retomável. */
object SourcePipeline {
    private val JSON = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun start(sourceId: String) = PipelineState(sourceId)

    /** Representação persistível da etapa, trechos, progresso e falha atual. */
    fun encode(state: PipelineState): String = JSON.encodeToString(state)

    /** Decodifica estados salvos por versões anteriores sem falhar por campos adicionais. */
    fun decode(json: String): PipelineState = JSON.decodeFromString(json)

    /** Reconstrói o índice em memória com o que a pipeline já indexou. */
    fun resume(state: PipelineState): SourceIndex =
        SourceIndex().also { it.addAll(state.chunks.take(state.indexedCount)) }

    fun step(state: PipelineState, source: ImportedSource?, index: SourceIndex, batch: Int = 50): PipelineState = try {
        when (state.stage) {
            PipelineStage.IMPORTED -> {
                val src = source ?: return state.copy(stage = PipelineStage.FAILED, error = "Fonte indisponível.")
                val chunks = SourceChunker.chunk(state.sourceId, src)
                if (chunks.isEmpty()) state.copy(stage = PipelineStage.FAILED, error = "Fonte sem trechos.")
                else state.copy(stage = PipelineStage.CHUNKED, chunks = chunks, indexedCount = 0)
            }
            PipelineStage.CHUNKED -> {
                val end = minOf(state.chunks.size, state.indexedCount + batch.coerceAtLeast(1))
                index.addAll(state.chunks.subList(state.indexedCount, end))
                state.copy(indexedCount = end, stage = if (end >= state.chunks.size) PipelineStage.INDEXED else PipelineStage.CHUNKED)
            }
            else -> state
        }
    } catch (e: Exception) {
        state.copy(stage = PipelineStage.FAILED, error = e.message ?: "Falha ao indexar.")
    }

    fun run(
        state: PipelineState,
        source: ImportedSource?,
        index: SourceIndex,
        shouldStop: () -> Boolean = { false },
        onProgress: (PipelineState) -> Unit = {}
    ): PipelineState {
        var s = state
        while (!s.done && !shouldStop()) {
            s = step(s, source, index)
            onProgress(s)
        }
        return s
    }
}
