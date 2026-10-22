package dev.rpgsolo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.Chapter
import dev.rpgsolo.engine.Codec
import dev.rpgsolo.engine.EventStore
import dev.rpgsolo.engine.Quest
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

/**
 * Versão 7. Ao mudar tabelas: aumente [version], escreva uma `Migration` e registre em [MIGRATIONS];
 * os esquemas exportados ficam em `data/schemas/` (3.1.2). Nunca use fallbackToDestructiveMigration:
 * apagaria as campanhas do jogador.
 */
@Database(
    entities = [CampanhaRow::class, EventoRow::class, FichaRow::class, CampanhaMetaRow::class, CapituloRow::class, MissaoRow::class, SnapshotRow::class, LinhaTempoRow::class],
    version = RpgDatabase.VERSION,
    exportSchema = true,
)
abstract class RpgDatabase : RoomDatabase() {
    abstract fun campanhas(): CampanhaDao

    abstract fun eventos(): EventoDao

    abstract fun fichas(): FichaDao

    abstract fun meta(): CampanhaMetaDao

    abstract fun capitulos(): CapituloDao

    abstract fun missoes(): MissaoDao

    abstract fun snapshots(): SnapshotDao

    abstract fun linhaTempo(): LinhaTempoDao

    companion object {
        /** Versão atual do esquema; a mesma da anotação [Database]. */
        const val VERSION = 7

        val MIGRATIONS =
            arrayOf(
                object : androidx.room.migration.Migration(1, 2) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS campanha_meta (campanhaId TEXT NOT NULL PRIMARY KEY, chaptersJson TEXT NOT NULL, questsJson TEXT NOT NULL, timelineJson TEXT NOT NULL)",
                        )
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
                },
                object : androidx.room.migration.Migration(3, 4) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `capitulos` (`campanhaId` TEXT NOT NULL, `numero` INTEGER NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY(`campanhaId`, `numero`), FOREIGN KEY(`campanhaId`) REFERENCES `campanhas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_capitulos_campanhaId` ON `capitulos` (`campanhaId`)")
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `missoes` (`campanhaId` TEXT NOT NULL, `missaoId` TEXT NOT NULL, `ordem` INTEGER NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY(`campanhaId`, `missaoId`), FOREIGN KEY(`campanhaId`) REFERENCES `campanhas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_missoes_campanhaId` ON `missoes` (`campanhaId`)")
                        // Copia o que estava nos JSONs de campanha_meta. Linha ilegível é ignorada: o JSON antigo continua intacto.
                        db.query("SELECT campanhaId, chaptersJson, questsJson FROM campanha_meta").use { c ->
                            while (c.moveToNext()) {
                                val id = c.getString(0)
                                runCatching { Codec.decodeChapters(c.getString(1)) }.getOrNull()?.forEach { ch ->
                                    db.execSQL(
                                        "INSERT OR REPLACE INTO capitulos (campanhaId, numero, json) VALUES (?, ?, ?)",
                                        arrayOf<Any>(id, ch.number, Codec.encode(ch)),
                                    )
                                }
                                runCatching { Codec.decodeQuests(c.getString(2)) }.getOrNull()?.forEachIndexed { i, q ->
                                    db.execSQL(
                                        "INSERT OR REPLACE INTO missoes (campanhaId, missaoId, ordem, json) VALUES (?, ?, ?, ?)",
                                        arrayOf<Any>(id, q.id, i, Codec.encode(q)),
                                    )
                                }
                            }
                        }
                    }
                },
                object : androidx.room.migration.Migration(4, 5) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `snapshots` (`campanhaId` TEXT NOT NULL, `eventCount` INTEGER NOT NULL, `stateJson` TEXT NOT NULL, PRIMARY KEY(`campanhaId`), FOREIGN KEY(`campanhaId`) REFERENCES `campanhas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                    }
                },
                object : androidx.room.migration.Migration(5, 6) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `linha_tempo` (`campanhaId` TEXT NOT NULL, `ordem` INTEGER NOT NULL, `eventoIndice` INTEGER NOT NULL, `texto` TEXT NOT NULL, PRIMARY KEY(`campanhaId`, `ordem`), FOREIGN KEY(`campanhaId`) REFERENCES `campanhas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_linha_tempo_campanhaId` ON `linha_tempo` (`campanhaId`)")
                        // Copia a linha do tempo do JSON antigo. Linha ilegível é ignorada: o JSON continua intacto.
                        db.query(
                            "SELECT campanhaId, timelineJson FROM campanha_meta INNER JOIN campanhas ON campanhas.id = campanha_meta.campanhaId",
                        ).use {
                                c ->
                            while (c.moveToNext()) {
                                val id = c.getString(0)
                                runCatching { Codec.decodeTimeline(c.getString(1)) }.getOrNull()?.forEachIndexed { i, (index, text) ->
                                    db.execSQL(
                                        "INSERT OR REPLACE INTO linha_tempo (campanhaId, ordem, eventoIndice, texto) VALUES (?, ?, ?, ?)",
                                        arrayOf<Any>(id, i, index, text),
                                    )
                                }
                            }
                        }
                    }
                },
                object : androidx.room.migration.Migration(6, 7) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL("ALTER TABLE campanhas ADD COLUMN estado TEXT NOT NULL DEFAULT 'ATIVA'")
                        db.execSQL("ALTER TABLE campanhas ADD COLUMN criadaEm INTEGER NOT NULL DEFAULT 0")
                        db.execSQL("ALTER TABLE campanhas ADD COLUMN atualizadaEm INTEGER NOT NULL DEFAULT 0")
                    }
                },
            )

        fun build(
            context: Context,
            name: String = "rpgsolo.db",
        ): RpgDatabase {
            backupBeforeMigration(context.applicationContext, name)
            return Room.databaseBuilder(context.applicationContext, RpgDatabase::class.java, name)
                .addMigrations(*MIGRATIONS)
                .build()
        }

        /**
         * CP-01.4: antes de migrar um banco mais antigo, guarda uma cópia do arquivo (`<nome>.antes-v<versão>.bak`,
         * com o `-wal` se existir). A migração do Room já roda numa transação (falhou, o banco antigo fica intacto);
         * a cópia protege contra defeito na própria migração. Só a primeira cópia de cada versão é mantida; falha ao
         * copiar nunca impede abrir o app. Devolve o arquivo da cópia ou `null` se não havia o que copiar.
         */
        internal fun backupBeforeMigration(
            context: Context,
            name: String,
            targetVersion: Int = VERSION,
        ): java.io.File? {
            val file = context.getDatabasePath(name)
            if (!file.exists()) return null
            val current =
                runCatching {
                    android.database.sqlite.SQLiteDatabase.openDatabase(file.path, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY)
                        .use { it.version }
                }.getOrNull() ?: return null
            if (current <= 0 || current >= targetVersion) return null
            val copy = java.io.File(file.parentFile, "$name.antes-v$targetVersion.bak")
            if (copy.exists()) return copy
            return runCatching {
                file.copyTo(copy)
                val wal = java.io.File(file.path + "-wal")
                if (wal.exists()) wal.copyTo(java.io.File(copy.path + "-wal"), overwrite = true)
                copy
            }.getOrElse {
                copy.delete()
                null
            }
        }

        /** Banco em memória para testes instrumentados. */
        fun inMemory(context: Context): RpgDatabase = Room.inMemoryDatabaseBuilder(context, RpgDatabase::class.java).build()
    }
}

/**
 * [EventStore] sobre Room. Cada operação roda em uma transação (tudo ou nada).
 * O Room não permite consultas na thread principal: chamadas vindas dela são executadas em uma thread de IO
 * ([io]) enquanto quem chamou espera. Isso evita `IllegalStateException`, mas ainda bloqueia a thread principal
 * pelo tempo da consulta; prefira chamar a partir de `Dispatchers.IO`, onde [io] executa direto.
 */
class RoomEventStore(private val db: RpgDatabase) : EventStore {
    private fun onMainThread(): Boolean = android.os.Looper.getMainLooper()?.thread === Thread.currentThread()

    private fun <T> io(block: () -> T): T {
        if (!onMainThread()) return block()
        try {
            return IO.submit(Callable { block() }).get()
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    /** Agrupa várias operações do store em uma única transação do Room (fora da thread principal). */
    fun <T> inTransaction(block: () -> T): T = io { db.runInTransaction(Callable { block() }) }

    override fun createCampaign(record: CampaignRecord) =
        io {
            val agora = System.currentTimeMillis()
            db.campanhas().inserir(
                CampanhaRow(record.id, record.name, record.seed, Codec.encode(record.config), criadaEm = agora, atualizadaEm = agora),
            )
        }

    override fun campaign(id: String): CampaignRecord? =
        io {
            db.campanhas().buscar(id)?.let { CampaignRecord(it.id, it.nome, it.seed, Codec.decodeConfig(it.configJson)) }
        }

    override fun size(campaignId: String) = io { db.eventos().contar(campaignId) }

    override fun load(campaignId: String) = io { db.eventos().carregar(campaignId) }

    override fun append(
        campaignId: String,
        expectedSize: Int,
        payloads: List<String>,
    ) = io {
        db.runInTransaction {
            val atual = db.eventos().contar(campaignId)
            check(atual == expectedSize) { "log mudou: esperado $expectedSize, atual $atual" }
            db.eventos().inserir(payloads.mapIndexed { i, p -> EventoRow(campaignId, expectedSize + i, p) })
        }
    }

    override fun truncate(
        campaignId: String,
        newSize: Int,
    ) = io {
        db.runInTransaction {
            db.eventos().apagarDepoisDe(campaignId, newSize)
            db.snapshots().apagarDepoisDe(campaignId, newSize)
        }
    }

    override fun snapshot(campaignId: String): Pair<Int, String>? =
        io {
            db.snapshots().buscar(campaignId)?.let { it.eventCount to it.stateJson }
        }

    override fun saveSnapshot(
        campaignId: String,
        eventCount: Int,
        stateJson: String,
    ) = io {
        db.snapshots().salvar(SnapshotRow(campaignId, eventCount, stateJson))
    }

    fun campaigns(): List<CampaignRecord> =
        io {
            db.campanhas().listar().map { row ->
                CampaignRecord(row.id, row.nome, row.seed, Codec.decodeConfig(row.configJson))
            }
        }

    /** Lista para a tela de campanhas (CP-02), da mais recente para a mais antiga; sem data, por nome. */
    fun infos(): List<CampanhaInfo> =
        io {
            db.campanhas().listar().map { row ->
                CampanhaInfo(
                    row.id,
                    row.nome,
                    EstadoCampanha.values().firstOrNull { it.name == row.estado } ?: EstadoCampanha.ATIVA,
                    row.criadaEm,
                    row.atualizadaEm,
                    db.eventos().contar(row.id),
                )
            }.sortedWith(compareByDescending<CampanhaInfo> { it.atualizadaEm }.thenBy { it.nome })
        }

    /** Pausa, conclui ou reativa. Devolve `false` se a campanha não existe. */
    fun setState(
        campaignId: String,
        state: EstadoCampanha,
    ): Boolean =
        io {
            db.campanhas().definirEstado(campaignId, state.name, System.currentTimeMillis()) > 0
        }

    /** Marca a última atividade da campanha (CP-02, histórico). */
    fun touch(campaignId: String) = io { db.campanhas().tocar(campaignId, System.currentTimeMillis()) }

    fun rename(
        campaignId: String,
        name: String,
    ) {
        require(name.isNotBlank()) { "dê um nome à campanha" }
        io { db.campanhas().renomear(campaignId, name.trim()) }
    }

    fun delete(campaignId: String) =
        io {
            db.runInTransaction {
                db.campanhas().apagar(campaignId)
                db.meta().apagar(campaignId)
                db.capitulos().apagar(campaignId)
                db.missoes().apagar(campaignId)
                db.snapshots().apagar(campaignId)
                db.linhaTempo().apagar(campaignId)
            }
        }

    /** Capítulos em ordem de número (M-08). Linha ilegível lança erro claro. */
    fun chapters(campaignId: String): List<Chapter> =
        io {
            db.capitulos().listar(campaignId).map {
                try {
                    Codec.decodeChapter(it.json)
                } catch (e: Exception) {
                    throw IllegalStateException("capítulo ${it.numero} da campanha $campaignId ilegível", e)
                }
            }
        }

    /** Substitui todos os capítulos da campanha numa transação. */
    fun saveChapters(
        campaignId: String,
        value: List<Chapter>,
    ) = io {
        db.runInTransaction {
            db.capitulos().apagar(campaignId)
            db.capitulos().salvar(value.map { CapituloRow(campaignId, it.number, Codec.encode(it)) })
        }
    }

    /** Missões na ordem de criação (M-08). */
    fun quests(campaignId: String): List<Quest> =
        io {
            db.missoes().listar(campaignId).map {
                try {
                    Codec.decodeQuest(it.json)
                } catch (e: Exception) {
                    throw IllegalStateException("missão ${it.missaoId} da campanha $campaignId ilegível", e)
                }
            }
        }

    /** Substitui todas as missões da campanha numa transação, preservando a ordem recebida. */
    fun saveQuests(
        campaignId: String,
        value: List<Quest>,
    ) = io {
        db.runInTransaction {
            db.missoes().apagar(campaignId)
            db.missoes().salvar(value.mapIndexed { i, q -> MissaoRow(campaignId, q.id, i, Codec.encode(q)) })
        }
    }

    /** Linha do tempo (índice do evento, texto) em ordem (CP-01.2). */
    fun timeline(campaignId: String): List<Pair<Int, String>> =
        io {
            db.linhaTempo().listar(campaignId).map { it.eventoIndice to it.texto }
        }

    /** Substitui a linha do tempo da campanha numa transação. */
    fun saveTimeline(
        campaignId: String,
        value: List<Pair<Int, String>>,
    ) = io {
        db.runInTransaction {
            db.linhaTempo().apagar(campaignId)
            db.linhaTempo().salvar(value.mapIndexed { i, (index, text) -> LinhaTempoRow(campaignId, i, index, text) })
        }
    }

    fun metadata(campaignId: String): CampanhaMetaRow? = io { db.meta().buscar(campaignId) }

    fun saveMetadata(row: CampanhaMetaRow) = io { db.meta().salvar(row) }

    fun fichas(): List<FichaRow> = io { db.fichas().listar() }

    fun fichasArquivadas(): List<FichaRow> = io { db.fichas().listarArquivadas() }

    /** Consulta a chave primária sem depender de a ficha ter sido decodificada em memória. */
    fun fichaExiste(id: String): Boolean = io { db.fichas().buscar(id) != null }

    fun definirFichaArquivada(
        id: String,
        arquivada: Boolean,
    ): Boolean = io { db.fichas().definirArquivada(id, arquivada) > 0 }

    fun apagarFicha(id: String): Boolean = io { db.fichas().apagar(id) > 0 }

    /** Atualiza fichas existentes preservando o campo `arquivada`; só insere quando não existem. */
    fun saveFicha(row: FichaRow) =
        io {
            db.runInTransaction {
                val updated = db.fichas().atualizarConteudo(row.id, row.nome, row.json)
                if (updated == 0) db.fichas().salvar(row)
            }
        }

    fun duplicate(
        sourceId: String,
        newId: String,
        newName: String,
    ): CampaignRecord {
        val source = requireNotNull(campaign(sourceId)) { "campanha não encontrada: $sourceId" }
        val copy = source.copy(id = newId, name = newName.trim())
        require(copy.name.isNotBlank()) { "dê um nome à campanha duplicada" }
        io {
            db.runInTransaction {
                createCampaign(copy)
                val events = load(sourceId)
                if (events.isNotEmpty()) db.eventos().inserir(events.mapIndexed { i, payload -> EventoRow(newId, i, payload) })
                metadata(sourceId)?.let { meta -> db.meta().salvar(meta.copy(campanhaId = newId)) }
                db.capitulos().salvar(db.capitulos().listar(sourceId).map { it.copy(campanhaId = newId) })
                db.missoes().salvar(db.missoes().listar(sourceId).map { it.copy(campanhaId = newId) })
                db.linhaTempo().salvar(db.linhaTempo().listar(sourceId).map { it.copy(campanhaId = newId) })
            }
        }
        return copy
    }

    private companion object {
        val IO = Executors.newCachedThreadPool { r -> Thread(r, "rpgsolo-db").apply { isDaemon = true } }
    }
}
