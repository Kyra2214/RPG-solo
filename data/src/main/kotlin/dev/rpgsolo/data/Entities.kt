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
    val configJson: String
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
    val timelineJson: String
)
