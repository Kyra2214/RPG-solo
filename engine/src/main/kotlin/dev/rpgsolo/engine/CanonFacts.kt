package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
enum class FactStatus { ORIGINAL, DIVERGENCE, UNKNOWN, AI_CREATED }

/** DC-04: fato canônico com origem, status e marca "o protagonista já sabe". */
@Serializable
data class CanonFact(
    val id: String,
    val campaignId: String,
    val text: String,
    val status: FactStatus,
    val sourceId: String? = null,
    val chunkId: String? = null,
    val protagonistKnows: Boolean = false,
    val createdAt: Long = 0
)

/** Conjunto imutável de fatos canônicos. Operações inválidas devolvem null e não alteram nada. */
@Serializable
data class CanonFacts(val facts: List<CanonFact> = emptyList()) {

    fun add(f: CanonFact): CanonFacts? =
        if (valid(f) && facts.none { it.id == f.id }) copy(facts = facts + f) else null

    fun setStatus(id: String, status: FactStatus): CanonFacts? {
        val f = get(id) ?: return null
        val n = f.copy(status = status)
        return if (valid(n)) replace(n) else null
    }

    fun markKnown(id: String, known: Boolean = true): CanonFacts? = get(id)?.let { replace(it.copy(protagonistKnows = known)) }

    fun remove(id: String): CanonFacts = copy(facts = facts.filterNot { it.id == id })

    fun get(id: String): CanonFact? = facts.firstOrNull { it.id == id }
    fun forCampaign(campaignId: String): List<CanonFact> = facts.filter { it.campaignId == campaignId }
    fun forChunks(campaignId: String, chunkIds: Set<String>): List<CanonFact> =
        forCampaign(campaignId).filter { it.chunkId != null && it.chunkId in chunkIds }

    fun toJson(): String = JSON.encodeToString(this)

    private fun replace(n: CanonFact) = copy(facts = facts.map { if (it.id == n.id) n else it })

    private fun valid(f: CanonFact): Boolean {
        if (f.id.isBlank() || f.campaignId.isBlank() || f.text.isBlank() || f.text.length > MAX_TEXT) return false
        return when (f.status) {
            FactStatus.ORIGINAL, FactStatus.DIVERGENCE -> !f.sourceId.isNullOrBlank() && !f.chunkId.isNullOrBlank()
            FactStatus.AI_CREATED -> f.sourceId == null && f.chunkId == null
            FactStatus.UNKNOWN -> true
        }
    }

    companion object {
        const val MAX_TEXT = 500
        private val JSON = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        fun fromJson(text: String): CanonFacts = JSON.decodeFromString(text)
    }
}
