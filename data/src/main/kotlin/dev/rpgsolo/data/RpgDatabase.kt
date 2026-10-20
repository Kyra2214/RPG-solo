package dev.rpgsolo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.Codec
import dev.rpgsolo.engine.EventStore

/**
 * Versão 3. Ao mudar tabelas: aumente [version], escreva uma `Migration` e registre em [MIGRATIONS];
 * os esquemas exportados ficam em `data/schemas/` (3.1.2). Nunca use fallbackToDestructiveMigration:
 * apagaria as campanhas do jogador.
 */
@Database(
    entities = [CampanhaRow::class, EventoRow::class, FichaRow::class, CampanhaMetaRow::class],
    version = 3,
    exportSchema = true
)
abstract class RpgDatabase : RoomDatabase() {
    abstract fun campanhas(): CampanhaDao
    abstract fun eventos(): EventoDao
    abstract fun fichas(): FichaDao
    abstract fun meta(): CampanhaMetaDao

    companion object {
        val MIGRATIONS = arrayOf(
            object : androidx.room.migration.Migration(1, 2) {
                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS campanha_meta (campanhaId TEXT NOT NULL PRIMARY KEY, chaptersJson TEXT NOT NULL, questsJson TEXT NOT NULL, timelineJson TEXT NOT NULL)")
                }
            },
            object : androidx.room.migration.Migration(2, 3) {
                override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE campanha_meta ADD COLUMN playerId TEXT")
                    db.execSQL("ALTER TABLE campanha_meta ADD COLUMN fichaId TEXT")
                    db.execSQL("ALTER TABLE campanha_meta ADD COLUMN packageId TEXT")
                    db.execSQL("ALTER TABLE campanha_meta ADD COLUMN systemId TEXT")
                    db.execSQL("ALTER TABLE campanha_meta ADD COLUMN editionId TEXT")
                }
            }
        )

        fun build(context: Context, name: String = "rpgsolo.db"): RpgDatabase =
            Room.databaseBuilder(context.applicationContext, RpgDatabase::class.java, name)
                .addMigrations(*MIGRATIONS)
                // O AppModel expõe uma API síncrona para o motor; a Etapa 1 migra as chamadas
                // de rede/DB para Dispatchers.IO. Até lá, evita bloquear a UI com estado parcial.
                .allowMainThreadQueries()
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

    fun campaigns(): List<CampaignRecord> = db.campanhas().listar().map { row ->
        CampaignRecord(row.id, row.nome, row.seed, Codec.decodeConfig(row.configJson))
    }

    fun rename(campaignId: String, name: String) {
        require(name.isNotBlank()) { "dê um nome à campanha" }
        db.campanhas().renomear(campaignId, name.trim())
    }

    fun delete(campaignId: String) {
        db.campanhas().apagar(campaignId)
        db.meta().apagar(campaignId)
    }

    fun metadata(campaignId: String): CampanhaMetaRow? = db.meta().buscar(campaignId)
    fun saveMetadata(row: CampanhaMetaRow) = db.meta().salvar(row)

    fun fichas(): List<FichaRow> = db.fichas().listar()
    fun fichasArquivadas(): List<FichaRow> = db.fichas().listarArquivadas()
    fun definirFichaArquivada(id: String, arquivada: Boolean): Boolean = db.fichas().definirArquivada(id, arquivada) > 0
    fun apagarFicha(id: String): Boolean = db.fichas().apagar(id) > 0

    /** Atualiza fichas existentes preservando o campo `arquivada`; só insere quando não existem. */
    fun saveFicha(row: FichaRow) {
        val updated = db.fichas().atualizarConteudo(row.id, row.nome, row.json)
        if (updated == 0) db.fichas().salvar(row)
    }

    fun duplicate(sourceId: String, newId: String, newName: String): CampaignRecord {
        val source = requireNotNull(campaign(sourceId)) { "campanha não encontrada: $sourceId" }
        val copy = source.copy(id = newId, name = newName.trim())
        require(copy.name.isNotBlank()) { "dê um nome à campanha duplicada" }
        db.runInTransaction {
            createCampaign(copy)
            val events = load(sourceId)
            if (events.isNotEmpty()) db.eventos().inserir(events.mapIndexed { i, payload -> EventoRow(newId, i, payload) })
            metadata(sourceId)?.let { meta -> db.meta().salvar(meta.copy(campanhaId = newId)) }
        }
        return copy
    }
}
