package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BackupFile(
    val version: Int,
    val campaignId: String,
    val name: String,
    val seed: Long,
    val config: EngineConfig,
    /** Eventos já em JSON (o mesmo formato salvo no banco). */
    val events: List<String>
)

/**
 * Fase 11.3.1: exportar e importar uma campanha como um único arquivo de texto, sem nuvem.
 * O seed e a config vão junto: sem eles o log não reproduz os mesmos dados.
 */
object Backup {
    const val VERSION = 1
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    fun export(store: EventStore, campaignId: String): String {
        val rec = requireNotNull(store.campaign(campaignId)) { "campanha não encontrada: $campaignId" }
        val file = BackupFile(VERSION, rec.id, rec.name, rec.seed, rec.config, store.load(campaignId))
        return json.encodeToString(BackupFile.serializer(), file)
    }

    /**
     * Importa para [store]. Valida tudo antes de gravar: backup ilegível, versão desconhecida, evento
     * corrompido ou id já existente falham sem criar nada. Devolve o id da campanha criada.
     */
    fun import(store: EventStore, text: String, newId: String? = null): String {
        val f = try {
            json.decodeFromString(BackupFile.serializer(), text)
        } catch (e: Exception) {
            throw IllegalArgumentException("backup ilegível")
        }
        require(f.version == VERSION) { "versão de backup não suportada: ${f.version}" }
        val id = newId ?: f.campaignId
        require(store.campaign(id) == null) { "já existe uma campanha com o id $id" }
        f.events.forEachIndexed { i, raw ->
            try {
                Codec.decodeEvent(raw)
            } catch (e: Exception) {
                throw IllegalArgumentException("evento $i do backup ilegível")
            }
        }
        store.createCampaign(CampaignRecord(id, f.name, f.seed, f.config))
        if (f.events.isNotEmpty()) store.append(id, 0, f.events)
        return id
    }
}
