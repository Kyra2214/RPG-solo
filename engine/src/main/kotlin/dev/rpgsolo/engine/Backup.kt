package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CampaignBackupMetadata(
    val chaptersJson: String = "[]",
    val questsJson: String = "[]",
    val timelineJson: String = "[]",
    val playerId: String? = null,
    val fichaId: String? = null,
    val packageId: String? = null,
    val systemId: String? = null,
    val editionId: String? = null,
    /** Fichas reutilizáveis em JSON. Chaves de API e preferências secretas nunca entram no backup. */
    val fichas: List<String> = emptyList(),
    /** Escolhas editoriais associadas às fichas; ausente em backups antigos. */
    val characterChoices: Map<String, String> = emptyMap(),
    /** IDs das fichas arquivadas; vazio em backups antigos. */
    val archivedFichaIds: List<String> = emptyList()
)

@Serializable
data class BackupFile(
    val version: Int,
    val campaignId: String,
    val name: String,
    val seed: Long,
    val config: EngineConfig,
    /** Eventos já em JSON (o mesmo formato salvo no banco). */
    val events: List<String>,
    /** Metadados opcionais; backups antigos continuam importáveis. */
    val metadata: CampaignBackupMetadata? = null
)

/**
 * Fase 11.3.1: exportar e importar uma campanha como um único arquivo de texto, sem nuvem.
 * O seed e a config vão junto: sem eles o log não reproduz os mesmos dados.
 */
object Backup {
    const val VERSION = 1
    const val MAX_BACKUP_CHARS = 50 * 1024 * 1024
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    fun export(
        store: EventStore,
        campaignId: String,
        metadata: CampaignBackupMetadata? = null
    ): String {
        val rec = requireNotNull(store.campaign(campaignId)) { "campanha não encontrada: $campaignId" }
        val file = BackupFile(VERSION, rec.id, rec.name, rec.seed, rec.config, store.load(campaignId), metadata)
        return json.encodeToString(BackupFile.serializer(), file)
    }

    /** Lê os metadados para o app restaurar capítulos, missões e fichas sem incluir segredos do provedor. */
    fun decode(text: String): BackupFile {
        require(text.length <= MAX_BACKUP_CHARS) {
            "backup excede o limite de 50 MiB; arquivo recusado antes de decodificar"
        }
        return try {
            json.decodeFromString(BackupFile.serializer(), text)
        } catch (e: Exception) {
            throw IllegalArgumentException("backup ilegível")
        }
    }

    /**
     * Importa para [store]. Valida tudo antes de gravar: backup ilegível, versão desconhecida, evento
     * corrompido ou id já existente falham sem criar nada. Devolve o id da campanha criada.
     */
    fun import(store: EventStore, text: String, newId: String? = null): String {
        val f = decode(text)
        require(f.version == VERSION) { "versão de backup não suportada: ${f.version}" }
        val id = newId ?: f.campaignId
        require(id.isNotBlank()) { "id de campanha vazio" }
        require(f.name.isNotBlank()) { "nome de campanha vazio" }
        require(store.campaign(id) == null) { "já existe uma campanha com o id $id" }
        val decoded = f.events.mapIndexed { i, raw ->
            try {
                Codec.decodeEvent(raw)
            } catch (e: Exception) {
                throw IllegalArgumentException("evento $i do backup ilegível")
            }
        }
        val restoredState = try {
            replay(decoded)
        } catch (e: Exception) {
            throw IllegalArgumentException("sequência de eventos do backup é inconsistente")
        }
        val stateProblems = validateState(restoredState)
        require(stateProblems.isEmpty()) {
            "estado do backup inconsistente: ${stateProblems.take(5).joinToString("; ")}"
        }
        store.createCampaign(CampaignRecord(id, f.name, f.seed, f.config))
        if (f.events.isNotEmpty()) store.append(id, 0, f.events)
        return id
    }
}
