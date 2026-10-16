package dev.rpgsolo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.Codec
import dev.rpgsolo.engine.EventStore

/**
 * Versão 1. Ao mudar tabelas: aumente [version], escreva uma `Migration` e registre em [MIGRATIONS];
 * os esquemas exportados ficam em `data/schemas/` (3.1.2). Nunca use fallbackToDestructiveMigration:
 * apagaria as campanhas do jogador.
 */
@Database(
    entities = [CampanhaRow::class, EventoRow::class, FichaRow::class],
    version = 1,
    exportSchema = true
)
abstract class RpgDatabase : RoomDatabase() {
    abstract fun campanhas(): CampanhaDao
    abstract fun eventos(): EventoDao
    abstract fun fichas(): FichaDao

    companion object {
        val MIGRATIONS: Array<androidx.room.migration.Migration> = emptyArray()

        fun build(context: Context, name: String = "rpgsolo.db"): RpgDatabase =
            Room.databaseBuilder(context.applicationContext, RpgDatabase::class.java, name)
                .addMigrations(*MIGRATIONS)
                .build()

        /** Banco em memória para testes instrumentados. */
        fun inMemory(context: Context): RpgDatabase =
            Room.inMemoryDatabaseBuilder(context, RpgDatabase::class.java).build()
    }
}

/**
 * [EventStore] sobre Room. Cada operação roda em uma transação (tudo ou nada).
 * Chame fora da thread principal (Room bloqueia a main thread por padrão).
 */
class RoomEventStore(private val db: RpgDatabase) : EventStore {
    override fun createCampaign(record: CampaignRecord) {
        db.campanhas().inserir(CampanhaRow(record.id, record.name, record.seed, Codec.encode(record.config)))
    }

    override fun campaign(id: String): CampaignRecord? =
        db.campanhas().buscar(id)?.let { CampaignRecord(it.id, it.nome, it.seed, Codec.decodeConfig(it.configJson)) }

    override fun size(campaignId: String) = db.eventos().contar(campaignId)

    override fun load(campaignId: String) = db.eventos().carregar(campaignId)

    override fun append(campaignId: String, expectedSize: Int, payloads: List<String>) {
        db.runInTransaction {
            val atual = db.eventos().contar(campaignId)
            check(atual == expectedSize) { "log mudou: esperado $expectedSize, atual $atual" }
            db.eventos().inserir(payloads.mapIndexed { i, p -> EventoRow(campaignId, expectedSize + i, p) })
        }
    }

    override fun truncate(campaignId: String, newSize: Int) {
        db.eventos().apagarDepoisDe(campaignId, newSize)
    }
}
