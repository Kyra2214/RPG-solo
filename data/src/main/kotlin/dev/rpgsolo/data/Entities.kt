package dev.rpgsolo.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** Campanha salva: seed e config reproduzem o estado junto com o log. */
@Entity(tableName = "campanhas")
data class CampanhaRow(
    @androidx.room.PrimaryKey val id: String,
    val nome: String,
    val seed: Long,
    val configJson: String,
    /** CP-02: `ATIVA`, `PAUSADA` ou `CONCLUIDA` (ver [EstadoCampanha]). */
    @androidx.room.ColumnInfo(defaultValue = "'ATIVA'") val estado: String = "ATIVA",
    @androidx.room.ColumnInfo(defaultValue = "0") val criadaEm: Long = 0,
    @androidx.room.ColumnInfo(defaultValue = "0") val atualizadaEm: Long = 0
)

enum class EstadoCampanha { ATIVA, PAUSADA, CONCLUIDA }

/** Resumo de uma campanha para a lista e o histórico (CP-02). Datas em milissegundos; 0 = desconhecida (campanha antiga). */
data class CampanhaInfo(
    val id: String,
    val nome: String,
    val estado: EstadoCampanha,
    val criadaEm: Long,
    val atualizadaEm: Long,
    val eventos: Int
)

/** Um evento do log append-only. [seq] começa em 0 e não tem buracos. */
@Entity(
    tableName = "eventos",
    primaryKeys = ["campanhaId", "seq"],
    foreignKeys = [ForeignKey(entity = CampanhaRow::class, parentColumns = ["id"], childColumns = ["campanhaId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("campanhaId")]
)
data class EventoRow(val campanhaId: String, val seq: Int, val payload: String)

/** Ficha reutilizável entre campanhas (a instância por campanha vem na Fase 5). */
@Entity(tableName = "fichas")
data class FichaRow(
    @androidx.room.PrimaryKey val id: String,
    val nome: String,
    val json: String,
    val arquivada: Boolean = false
)

@Entity(tableName = "campanha_meta")
data class CampanhaMetaRow(
    @androidx.room.PrimaryKey val campanhaId: String,
    val chaptersJson: String,
    val questsJson: String,
    val timelineJson: String,
    val playerId: String? = null,
    val fichaId: String? = null,
    val packageId: String? = null,
    val systemId: String? = null,
    val editionId: String? = null
)

/** Capítulo de uma campanha (M-08). [json] é um `Chapter` codificado; [numero] ordena e identifica. */
@Entity(
    tableName = "capitulos",
    primaryKeys = ["campanhaId", "numero"],
    foreignKeys = [ForeignKey(entity = CampanhaRow::class, parentColumns = ["id"], childColumns = ["campanhaId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("campanhaId")]
)
data class CapituloRow(val campanhaId: String, val numero: Int, val json: String)

/** Missão de uma campanha (M-08). [json] é um `Quest` codificado; [ordem] preserva a ordem de criação. */
@Entity(
    tableName = "missoes",
    primaryKeys = ["campanhaId", "missaoId"],
    foreignKeys = [ForeignKey(entity = CampanhaRow::class, parentColumns = ["id"], childColumns = ["campanhaId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("campanhaId")]
)
data class MissaoRow(val campanhaId: String, val missaoId: String, val ordem: Int, val json: String)

/** Último snapshot do estado de uma campanha (M-08.3): estado após [eventCount] eventos; reabrir aplica só a cauda do log. */
@Entity(
    tableName = "snapshots",
    foreignKeys = [ForeignKey(entity = CampanhaRow::class, parentColumns = ["id"], childColumns = ["campanhaId"], onDelete = ForeignKey.CASCADE)]
)
data class SnapshotRow(@androidx.room.PrimaryKey val campanhaId: String, val eventCount: Int, val stateJson: String)

/** Linha do tempo derivada do log (CP-01.2): [eventoIndice] é o índice do evento de origem, [ordem] preserva a ordem. */
@Entity(
    tableName = "linha_tempo",
    primaryKeys = ["campanhaId", "ordem"],
    foreignKeys = [ForeignKey(entity = CampanhaRow::class, parentColumns = ["id"], childColumns = ["campanhaId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("campanhaId")]
)
data class LinhaTempoRow(val campanhaId: String, val ordem: Int, val eventoIndice: Int, val texto: String)

/** Metadados de um documento-fonte importado (DC-02); o conteúdo binário é armazenado separadamente. */
@Entity(tableName = "fontes")
data class FonteRow(
    @androidx.room.PrimaryKey val id: String,
    val fileName: String,
    val format: String?,
    val title: String,
    val sha256: String,
    val byteSize: Int,
    val status: String,
    val error: String?,
    val importedAt: Long
)

/** Vínculo fonte-campanha; a FK apaga somente o vínculo ao excluir a campanha, nunca a fonte. */
@Entity(
    tableName = "fonte_vinculos",
    primaryKeys = ["campaignId", "sourceId"],
    foreignKeys = [ForeignKey(entity = CampanhaRow::class, parentColumns = ["id"], childColumns = ["campaignId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("campaignId")]
)
data class FonteVinculoRow(
    val campaignId: String,
    val sourceId: String,
    val sha256: String,
    val fileName: String,
    val linkedAt: Long
)
