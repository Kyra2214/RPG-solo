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
    @androidx.room.ColumnInfo(defaultValue = "0") val atualizadaEm: Long = 0,
)

enum class EstadoCampanha { ATIVA, PAUSADA, CONCLUIDA }

/** Resumo de uma campanha para a lista e o histórico (CP-02). Datas em milissegundos; 0 = desconhecida (campanha antiga). */
data class CampanhaInfo(
    val id: String,
    val nome: String,
    val estado: EstadoCampanha,
    val criadaEm: Long,
    val atualizadaEm: Long,
    val eventos: Int,
)

/** Um evento do log append-only. [seq] começa em 0 e não tem buracos. */
@Entity(
    tableName = "eventos",
    primaryKeys = ["campanhaId", "seq"],
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campanhaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("campanhaId")],
)
data class EventoRow(val campanhaId: String, val seq: Int, val payload: String)

/** Ficha reutilizável entre campanhas (a instância por campanha vem na Fase 5). */
@Entity(tableName = "fichas")
data class FichaRow(
    @androidx.room.PrimaryKey val id: String,
    val nome: String,
    val json: String,
    val arquivada: Boolean = false,
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
    val editionId: String? = null,
)

/** Capítulo de uma campanha (M-08). [json] é um `Chapter` codificado; [numero] ordena e identifica. */
@Entity(
    tableName = "capitulos",
    primaryKeys = ["campanhaId", "numero"],
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campanhaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("campanhaId")],
)
data class CapituloRow(val campanhaId: String, val numero: Int, val json: String)

/** Missão de uma campanha (M-08). [json] é um `Quest` codificado; [ordem] preserva a ordem de criação. */
@Entity(
    tableName = "missoes",
    primaryKeys = ["campanhaId", "missaoId"],
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campanhaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("campanhaId")],
)
data class MissaoRow(val campanhaId: String, val missaoId: String, val ordem: Int, val json: String)

/** Último snapshot do estado de uma campanha (M-08.3): estado após [eventCount] eventos; reabrir aplica só a cauda do log. */
@Entity(
    tableName = "snapshots",
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campanhaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SnapshotRow(
    @androidx.room.PrimaryKey val campanhaId: String,
    val eventCount: Int,
    val stateJson: String,
)

/** Linha do tempo derivada do log (CP-01.2): [eventoIndice] é o índice do evento de origem, [ordem] preserva a ordem. */
@Entity(
    tableName = "linha_tempo",
    primaryKeys = ["campanhaId", "ordem"],
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campanhaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("campanhaId")],
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
    val importedAt: Long,
)

/** Vínculo fonte-campanha; a FK apaga somente o vínculo ao excluir a campanha, nunca a fonte. */
@Entity(
    tableName = "fonte_vinculos",
    primaryKeys = ["campaignId", "sourceId"],
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campaignId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("campaignId")],
)
data class FonteVinculoRow(
    val campaignId: String,
    val sourceId: String,
    val sha256: String,
    val fileName: String,
    val linkedAt: Long,
)

/** Estado retomável da indexação (DC-03), serializado para aceitar evolução do modelo. */
@Entity(
    tableName = "fonte_pipeline",
    foreignKeys = [
        ForeignKey(
            entity = FonteRow::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class FontePipelineRow(
    @androidx.room.PrimaryKey val sourceId: String,
    val stateJson: String,
)

/** Fatos canônicos narrativos de uma campanha; permanecem separados do log mecânico. */
@Entity(
    tableName = "fatos_canonicos",
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campaignId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class CanonFactsRow(
    @androidx.room.PrimaryKey val campaignId: String,
    val factsJson: String,
)

/**
 * Mundo guardado no aparelho (MI-10, D-I3 e D-I4): instância de um molde que evolui. O progresso resume o último
 * snapshot. Inserido com ABORT e alterado com UPDATE: um REPLACE apagaria, em cascata, snapshots, crônica e vínculos.
 */
@Entity(tableName = "mundos")
data class MundoRow(
    @androidx.room.PrimaryKey val id: String,
    val moldeId: String,
    val moldeHash: String,
    val dinamicaHash: String,
    val semente: Long,
    val criadoEm: Long,
    val atualizadoEm: Long,
    val proximoDia: Int,
    val totalDeltas: Int,
    val hashCronica: String,
    val ultimoBloco: Int,
)

/** Snapshot do estado do mundo ao fim de um bloco (D-I3): ponto de retomada. [snapshotJson] é o JSON canônico do `Snapshot`. */
@Entity(
    tableName = "mundo_snapshots",
    primaryKeys = ["mundoId", "bloco"],
    foreignKeys = [
        ForeignKey(
            entity = MundoRow::class,
            parentColumns = ["id"],
            childColumns = ["mundoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("mundoId")],
)
data class MundoSnapshotRow(
    val mundoId: String,
    val bloco: Int,
    val dia: Int,
    val hashEstado: String,
    val hashCronica: String,
    val deltas: Int,
    val bytes: Int,
    val criadoEm: Long,
    val snapshotJson: String,
)

/** Resumo de um snapshot sem o JSON do estado (consulta de listagem). */
data class MundoSnapshotInfoRow(
    val bloco: Int,
    val dia: Int,
    val hashEstado: String,
    val hashCronica: String,
    val deltas: Int,
    val bytes: Int,
    val criadoEm: Long,
)

/** Linha da crônica filtrada do mundo (D-I3). [seq] é a ordem do delta na crônica completa e pode ter buracos. */
@Entity(
    tableName = "mundo_cronica",
    primaryKeys = ["mundoId", "seq"],
    foreignKeys = [
        ForeignKey(
            entity = MundoRow::class,
            parentColumns = ["id"],
            childColumns = ["mundoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("mundoId", "dia")],
)
data class MundoCronicaRow(
    val mundoId: String,
    val seq: Int,
    val deltaId: String,
    val dia: Int,
    val nivel: Int,
    val importancia: Int,
    val tipo: String,
    val entidade: String,
    val linhaJson: String,
)

/**
 * Vínculo campanha → mundo (D-I4). Apagar a campanha apaga só o vínculo; o mundo só pode ser apagado sem campanhas
 * (RESTRICT), porque o cânone pode ser compartilhado (MP-07).
 */
@Entity(
    tableName = "campanha_mundo",
    foreignKeys = [
        ForeignKey(
            entity = CampanhaRow::class,
            parentColumns = ["id"],
            childColumns = ["campaignId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MundoRow::class,
            parentColumns = ["id"],
            childColumns = ["mundoId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("mundoId")],
)
data class CampanhaMundoRow(
    @androidx.room.PrimaryKey val campaignId: String,
    val mundoId: String,
    val moldeId: String,
    val vinculadoEm: Long,
)
