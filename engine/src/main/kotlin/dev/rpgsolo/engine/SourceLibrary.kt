package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
enum class SourceStatus { READY, FAILED, REMOVED }

/** Metadados de uma fonte da biblioteca (DC-02). O texto não é guardado aqui (DC-03). */
@Serializable
data class SourceEntry(
    val id: String,
    val fileName: String,
    val format: String? = null,
    val title: String,
    val sha256: String,
    val byteSize: Int,
    val status: SourceStatus,
    val error: String? = null,
    val importedAt: Long
)

/** Vínculo campanha × fonte, com o hash do arquivo no momento do vínculo. */
@Serializable
data class SourceLink(
    val campaignId: String,
    val sourceId: String,
    val sha256: String,
    val fileName: String,
    val linkedAt: Long
)

/** DC-02: biblioteca de fontes, imutável e serializável. Remover uma fonte nunca apaga campanhas nem vínculos. */
@Serializable
data class SourceLibrary(val entries: List<SourceEntry> = emptyList(), val links: List<SourceLink> = emptyList()) {

    fun addImport(
        fileName: String,
        bytes: ByteArray,
        now: Long,
        id: String = UUID.randomUUID().toString()
    ): Pair<SourceLibrary, SourceEntry> {
        val hash = SourceImporter.sha256(bytes)
        entries.firstOrNull { it.sha256 == hash && it.status == SourceStatus.READY }?.let { return this to it }
        val entry = when (val r = SourceImporter.import(fileName, bytes)) {
            is SourceImportResult.Ok -> SourceEntry(id, fileName, r.source.format.name, r.source.title, hash, bytes.size, SourceStatus.READY, null, now)
            is SourceImportResult.Failed -> SourceEntry(id, fileName, SourceImporter.formatOf(fileName)?.name, fileName, hash, bytes.size, SourceStatus.FAILED, r.reason, now)
        }
        return copy(entries = entries + entry) to entry
    }

    fun get(sourceId: String): SourceEntry? = entries.firstOrNull { it.id == sourceId }

    /** Vincula uma fonte pronta à campanha; recusa fonte inexistente, com falha ou removida. */
    fun link(campaignId: String, sourceId: String, now: Long): SourceLibrary? {
        val e = get(sourceId)?.takeIf { it.status == SourceStatus.READY } ?: return null
        if (links.any { it.campaignId == campaignId && it.sourceId == sourceId }) return this
        return copy(links = links + SourceLink(campaignId, sourceId, e.sha256, e.fileName, now))
    }

    fun unlink(campaignId: String, sourceId: String): SourceLibrary =
        copy(links = links.filterNot { it.campaignId == campaignId && it.sourceId == sourceId })

    /** Marca como removida; vínculos e campanhas dependentes permanecem. */
    fun remove(sourceId: String): SourceLibrary =
        copy(entries = entries.map { if (it.id == sourceId) it.copy(status = SourceStatus.REMOVED) else it })

    fun dependents(sourceId: String): List<String> = links.filter { it.sourceId == sourceId }.map { it.campaignId }.distinct()

    fun forCampaign(campaignId: String): List<SourceLink> = links.filter { it.campaignId == campaignId }

    /** Confere se o arquivo apresentado é o mesmo que foi vinculado à campanha. */
    fun matchesLink(campaignId: String, sourceId: String, bytes: ByteArray): Boolean =
        links.firstOrNull { it.campaignId == campaignId && it.sourceId == sourceId }?.sha256 == SourceImporter.sha256(bytes)

    fun toJson(): String = JSON.encodeToString(this)

    companion object {
        private val JSON = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        fun fromJson(text: String): SourceLibrary = JSON.decodeFromString(text)
    }
}
