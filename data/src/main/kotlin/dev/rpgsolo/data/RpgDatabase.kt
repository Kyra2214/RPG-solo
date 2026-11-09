package dev.rpgsolo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CanonFacts
import dev.rpgsolo.engine.Chapter
import dev.rpgsolo.engine.Codec
import dev.rpgsolo.engine.EventStore
import dev.rpgsolo.engine.PipelineState
import dev.rpgsolo.engine.Quest
import dev.rpgsolo.engine.SourceEntry
import dev.rpgsolo.engine.SourceLibrary
import dev.rpgsolo.engine.SourceLink
import dev.rpgsolo.engine.SourcePipeline
import dev.rpgsolo.engine.SourceStatus
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

/**
 * Versão 11. Ao mudar tabelas: aumente [version], escreva uma `Migration` e registre em [MIGRATIONS];
 * os esquemas exportados ficam em `data/schemas/` (3.1.2). Nunca use fallbackToDestructiveMigration:
 * apagaria as campanhas do jogador.
 */
@Database(
    entities = [CampanhaRow::class, EventoRow::class, FichaRow::class, CampanhaMetaRow::class, CapituloRow::class, MissaoRow::class, SnapshotRow::class, LinhaTempoRow::class, FonteRow::class, FonteVinculoRow::class, FontePipelineRow::class, CanonFactsRow::class, MundoRow::class, MundoSnapshotRow::class, MundoCronicaRow::class, CampanhaMundoRow::class],
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

    abstract fun fontes(): FonteDao

    abstract fun fontePipeline(): FontePipelineDao

    abstract fun fatosCanonicos(): CanonFactsDao

    abstract fun mundos(): MundoDao

    abstract fun mundoSnapshots(): MundoSnapshotDao

    abstract fun mundoCronica(): MundoCronicaDao

    abstract fun campanhaMundo(): CampanhaMundoDao

    companion object {
        /** Versão atual do esquema; a mesma da anotação [Database]. */
        const val VERSION = 11

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
                object : androidx.room.migration.Migration(7, 8) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `fontes` (`id` TEXT NOT NULL, `fileName` TEXT NOT NULL, `format` TEXT, `title` TEXT NOT NULL, `sha256` TEXT NOT NULL, `byteSize` INTEGER NOT NULL, `status` TEXT NOT NULL, `error` TEXT, `importedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))",
                        )
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `fonte_vinculos` (`campaignId` TEXT NOT NULL, `sourceId` TEXT NOT NULL, `sha256` TEXT NOT NULL, `fileName` TEXT NOT NULL, `linkedAt` INTEGER NOT NULL, PRIMARY KEY(`campaignId`, `sourceId`), FOREIGN KEY(`campaignId`) REFERENCES `campanhas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_fonte_vinculos_campaignId` ON `fonte_vinculos` (`campaignId`)")
                    }
                },
                object : androidx.room.migration.Migration(8, 9) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `fonte_pipeline` (`sourceId` TEXT NOT NULL, `stateJson` TEXT NOT NULL, PRIMARY KEY(`sourceId`), FOREIGN KEY(`sourceId`) REFERENCES `fontes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                    }
                },
                object : androidx.room.migration.Migration(9, 10) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `fatos_canonicos` (`campaignId` TEXT NOT NULL, `factsJson` TEXT NOT NULL, PRIMARY KEY(`campaignId`), FOREIGN KEY(`campaignId`) REFERENCES `campanhas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                    }
                },
                object : androidx.room.migration.Migration(10, 11) {
                    // MI-10: só cria tabelas novas (mundo, snapshots, crônica filtrada e vínculo campanha → mundo);
                    // nenhuma tabela existente é alterada, então campanhas e logs ficam intactos.
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `mundos` (`id` TEXT NOT NULL, `moldeId` TEXT NOT NULL, `moldeHash` TEXT NOT NULL, `dinamicaHash` TEXT NOT NULL, `semente` INTEGER NOT NULL, `criadoEm` INTEGER NOT NULL, `atualizadoEm` INTEGER NOT NULL, `proximoDia` INTEGER NOT NULL, `totalDeltas` INTEGER NOT NULL, `hashCronica` TEXT NOT NULL, `ultimoBloco` INTEGER NOT NULL, PRIMARY KEY(`id`))",
                        )
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `mundo_snapshots` (`mundoId` TEXT NOT NULL, `bloco` INTEGER NOT NULL, `dia` INTEGER NOT NULL, `hashEstado` TEXT NOT NULL, `hashCronica` TEXT NOT NULL, `deltas` INTEGER NOT NULL, `bytes` INTEGER NOT NULL, `criadoEm` INTEGER NOT NULL, `snapshotJson` TEXT NOT NULL, PRIMARY KEY(`mundoId`, `bloco`), FOREIGN KEY(`mundoId`) REFERENCES `mundos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_mundo_snapshots_mundoId` ON `mundo_snapshots` (`mundoId`)")
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `mundo_cronica` (`mundoId` TEXT NOT NULL, `seq` INTEGER NOT NULL, `deltaId` TEXT NOT NULL, `dia` INTEGER NOT NULL, `nivel` INTEGER NOT NULL, `importancia` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `entidade` TEXT NOT NULL, `linhaJson` TEXT NOT NULL, PRIMARY KEY(`mundoId`, `seq`), FOREIGN KEY(`mundoId`) REFERENCES `mundos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        )
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_mundo_cronica_mundoId_dia` ON `mundo_cronica` (`mundoId`, `dia`)")
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `campanha_mundo` (`campaignId` TEXT NOT NULL, `mundoId` TEXT NOT NULL, `moldeId` TEXT NOT NULL, `vinculadoEm` INTEGER NOT NULL, PRIMARY KEY(`campaignId`), FOREIGN KEY(`campaignId`) REFERENCES `campanhas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`mundoId`) REFERENCES `mundos`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )",
                        )
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_campanha_mundo_mundoId` ON `campanha_mundo` (`mundoId`)")
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

    /** DC-02: biblioteca local de fontes; remover uma campanha apaga seus vínculos pela FK, não as fontes. */
    fun sourceLibrary(): SourceLibrary =
        io {
            val dao = db.fontes()
            SourceLibrary(
                entries =
                    dao.listarFontes().map { row ->
                        SourceEntry(
                            id = row.id,
                            fileName = row.fileName,
                            format = row.format,
                            title = row.title,
                            sha256 = row.sha256,
                            byteSize = row.byteSize,
                            status = SourceStatus.values().firstOrNull { it.name == row.status } ?: SourceStatus.FAILED,
                            error = row.error,
                            importedAt = row.importedAt,
                        )
                    },
                links =
                    dao.listarVinculos().map { row ->
                        SourceLink(row.campaignId, row.sourceId, row.sha256, row.fileName, row.linkedAt)
                    },
            )
        }

    /** Substitui a biblioteca inteira numa única transação; vínculos precisam referir campanhas existentes. */
    fun saveSourceLibrary(library: SourceLibrary) =
        io {
            db.runInTransaction {
                val dao = db.fontes()
                dao.apagarVinculos()
                val retainedIds = library.entries.map { it.id }.toSet()
                dao.listarFontes().filterNot { it.id in retainedIds }.forEach { dao.apagarFonte(it.id) }
                if (library.entries.isNotEmpty()) {
                    dao.salvarFontes(
                        library.entries.map { entry ->
                            FonteRow(
                                entry.id, entry.fileName, entry.format, entry.title, entry.sha256,
                                entry.byteSize, entry.status.name, entry.error, entry.importedAt,
                            )
                        },
                    )
                }
                if (library.links.isNotEmpty()) {
                    dao.salvarVinculos(
                        library.links.map { link ->
                            FonteVinculoRow(link.campaignId, link.sourceId, link.sha256, link.fileName, link.linkedAt)
                        },
                    )
                }
            }
        }

    /** Estados de pipeline persistidos, usados para retomar importação/indexação depois de reabrir o app. */
    fun sourcePipelineStates(): List<PipelineState> =
        io {
            db.fontePipeline().listar().map { row ->
                try {
                    SourcePipeline.decode(row.stateJson)
                } catch (e: Exception) {
                    throw IllegalStateException("estado de indexação da fonte ${row.sourceId} ilegível", e)
                }
            }
        }

    fun sourcePipelineState(sourceId: String): PipelineState? =
        io {
            db.fontePipeline().buscar(sourceId)?.let { row ->
                try {
                    SourcePipeline.decode(row.stateJson)
                } catch (e: Exception) {
                    throw IllegalStateException("estado de indexação da fonte ${row.sourceId} ilegível", e)
                }
            }
        }

    fun saveSourcePipelineState(state: PipelineState) =
        io {
            db.runInTransaction {
                require(db.fontes().listarFontes().any { it.id == state.sourceId }) { "fonte não encontrada: ${state.sourceId}" }
                db.fontePipeline().salvar(FontePipelineRow(state.sourceId, SourcePipeline.encode(state)))
            }
        }

    /** Fatos canônicos narrativos, independentes do log de comandos da campanha. */
    fun canonFacts(campaignId: String): CanonFacts =
        io {
            val row = db.fatosCanonicos().buscar(campaignId) ?: return@io CanonFacts()
            val facts =
                try {
                    CanonFacts.fromJson(row.factsJson)
                } catch (e: Exception) {
                    throw IllegalStateException("fatos canônicos da campanha $campaignId ilegíveis", e)
                }
            check(facts.facts.all { it.campaignId == campaignId }) {
                "fatos canônicos contêm campanha divergente: $campaignId"
            }
            facts
        }

    /** Valida IDs, conteúdo e campanha antes de substituir todo o conjunto de forma atômica. */
    fun saveCanonFacts(
        campaignId: String,
        facts: CanonFacts,
    ) = io {
        require(campaignId.isNotBlank()) { "campanha inválida" }
        var validated = CanonFacts()
        facts.facts.forEach { fact ->
            require(fact.campaignId == campaignId) { "fato ${fact.id} pertence a outra campanha" }
            validated = requireNotNull(validated.add(fact)) { "fato inválido ou duplicado: ${fact.id}" }
        }
        db.runInTransaction {
            require(db.campanhas().buscar(campaignId) != null) { "campanha não encontrada: $campaignId" }
            if (validated.facts.isEmpty()) {
                db.fatosCanonicos().apagar(campaignId)
            } else {
                db.fatosCanonicos().salvar(CanonFactsRow(campaignId, validated.toJson()))
            }
        }
    }

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
                val copiedFacts = canonFacts(sourceId).facts.map { it.copy(campaignId = newId) }
                if (copiedFacts.isNotEmpty()) {
                    db.fatosCanonicos().salvar(CanonFactsRow(newId, CanonFacts(copiedFacts).toJson()))
                }
                val copiedSourceLinks =
                    db.fontes().listarVinculos()
                        .filter { it.campaignId == sourceId }
                        .map { it.copy(campaignId = newId) }
                if (copiedSourceLinks.isNotEmpty()) db.fontes().salvarVinculos(copiedSourceLinks)
            }
        }
        return copy
    }

    private companion object {
        val IO = Executors.newCachedThreadPool { r -> Thread(r, "rpgsolo-db").apply { isDaemon = true } }
    }
}

/**
 * [MundoStore] sobre Room (MI-10). Mesma regra do [RoomEventStore]: cada operação numa transação e, se chamada da
 * thread principal, executada numa thread de IO enquanto quem chamou espera (prefira `Dispatchers.IO`).
 */
class RoomMundoStore(private val db: RpgDatabase) : dev.rpgsolo.world.MundoStore {
    private fun onMainThread(): Boolean = android.os.Looper.getMainLooper()?.thread === Thread.currentThread()

    private fun <T> io(block: () -> T): T {
        if (!onMainThread()) return block()
        try {
            return IO.submit(Callable { block() }).get()
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    private companion object {
        val IO = Executors.newCachedThreadPool { r -> Thread(r, "rpgsolo-db-mundo").apply { isDaemon = true } }
    }

    private fun MundoRow.paraRegistro() =
        dev.rpgsolo.world.MundoRegistro(
            id, moldeId, moldeHash, dinamicaHash, semente, criadoEm, atualizadoEm, proximoDia, totalDeltas, hashCronica, ultimoBloco,
        )

    private fun dev.rpgsolo.world.MundoRegistro.paraRow() =
        MundoRow(id, moldeId, moldeHash, dinamicaHash, semente, criadoEm, atualizadoEm, proximoDia, totalDeltas, hashCronica, ultimoBloco)

    private fun MundoSnapshotInfoRow.paraInfo() =
        dev.rpgsolo.world.SnapshotInfo(
            bloco,
            dia,
            hashEstado,
            hashCronica,
            deltas,
            bytes,
            criadoEm,
        )

    override fun criarMundo(mundo: dev.rpgsolo.world.MundoRegistro) = io { db.mundos().inserir(mundo.paraRow()) }

    override fun mundo(id: String) = io { db.mundos().buscar(id)?.paraRegistro() }

    override fun mundos() = io { db.mundos().listar().map { it.paraRegistro() } }

    override fun vincular(vinculo: dev.rpgsolo.world.VinculoMundo) =
        io {
            db.runInTransaction {
                check(db.mundos().buscar(vinculo.mundoId) != null) { "mundo inexistente: ${vinculo.mundoId}" }
                db.campanhaMundo().salvar(CampanhaMundoRow(vinculo.campanhaId, vinculo.mundoId, vinculo.moldeId, vinculo.vinculadoEm))
            }
        }

    override fun vinculo(campanhaId: String) =
        io {
            db.campanhaMundo().buscar(
                campanhaId,
            )?.let { dev.rpgsolo.world.VinculoMundo(it.campaignId, it.mundoId, it.moldeId, it.vinculadoEm) }
        }

    override fun mundoDaCampanha(campanhaId: String) =
        io {
            db.campanhaMundo().buscar(campanhaId)?.let { db.mundos().buscar(it.mundoId)?.paraRegistro() }
        }

    override fun campanhasDoMundo(mundoId: String) = io { db.campanhaMundo().contarPorMundo(mundoId) }

    override fun guardarBloco(
        mundoId: String,
        bloco: Int,
        snapshot: dev.rpgsolo.world.Snapshot,
        linhas: List<dev.rpgsolo.world.CronicaGuardada>,
        agora: Long,
    ) = io {
        db.runInTransaction {
            val mundo = checkNotNull(db.mundos().buscar(mundoId)) { "mundo inexistente: $mundoId" }
            val json = snapshot.paraJson()
            db.mundoSnapshots().salvar(
                MundoSnapshotRow(
                    mundoId, bloco, snapshot.dia, snapshot.hashEstado, snapshot.hashCronica, snapshot.deltas,
                    json.toByteArray(Charsets.UTF_8).size, agora, json,
                ),
            )
            if (linhas.isNotEmpty()) {
                db.mundoCronica().anexar(
                    linhas.map { MundoCronicaRow(mundoId, it.seq, it.deltaId, it.dia, it.nivel, it.importancia, it.tipo, it.entidade, it.linhaJson) },
                )
            }
            db.mundos().atualizar(
                mundo.copy(
                    atualizadoEm = agora,
                    proximoDia = snapshot.dia,
                    totalDeltas = snapshot.deltas,
                    hashCronica = snapshot.hashCronica,
                    ultimoBloco = bloco,
                ),
            )
        }
    }

    override fun ultimoSnapshot(mundoId: String) =
        io {
            db.mundoSnapshots().ultimo(mundoId)?.let { row ->
                dev.rpgsolo.world.SnapshotGuardado(
                    dev.rpgsolo.world.SnapshotInfo(
                        row.bloco,
                        row.dia,
                        row.hashEstado,
                        row.hashCronica,
                        row.deltas,
                        row.bytes,
                        row.criadoEm,
                    ),
                    dev.rpgsolo.world.Snapshot.deJson(row.snapshotJson),
                )
            }
        }

    override fun snapshots(mundoId: String) = io { db.mundoSnapshots().listarInfo(mundoId).map { it.paraInfo() } }

    override fun cronica(
        mundoId: String,
        desdeDia: Int,
        ateDia: Int,
        nivelMaximo: Int,
        limite: Int,
    ) = io {
        db.mundoCronica().listar(mundoId, desdeDia, ateDia, nivelMaximo, limite).map {
            dev.rpgsolo.world.CronicaGuardada(it.seq, it.deltaId, it.dia, it.nivel, it.importancia, it.tipo, it.entidade, it.linhaJson)
        }
    }

    override fun voltarAoBloco(
        mundoId: String,
        bloco: Int,
        agora: Long,
    ) = io {
        db.runInTransaction {
            val mundo = checkNotNull(db.mundos().buscar(mundoId)) { "mundo inexistente: $mundoId" }
            db.mundoSnapshots().apagarDepoisDe(mundoId, bloco)
            val alvo =
                if (bloco > 0) {
                    checkNotNull(
                        db.mundoSnapshots().buscar(mundoId, bloco),
                    ) { "bloco $bloco inexistente em $mundoId" }
                } else {
                    null
                }
            // Snapshot.dia é o próximo dia a simular: os deltas desse dia em diante pertencem aos blocos descartados.
            db.mundoCronica().apagarDesdeDia(mundoId, alvo?.dia ?: 0)
            db.mundos().atualizar(
                mundo.copy(
                    atualizadoEm = agora,
                    proximoDia = alvo?.dia ?: 0,
                    totalDeltas = alvo?.deltas ?: 0,
                    hashCronica = alvo?.hashCronica ?: "",
                    ultimoBloco = bloco,
                ),
            )
        }
    }

    override fun bytesEmDisco(mundoId: String) = io { db.mundoSnapshots().somaBytes(mundoId) + db.mundoCronica().somaBytes(mundoId) }

    override fun espaco(mundoId: String) =
        io {
            db.runInTransaction<dev.rpgsolo.world.EspacoMundo> {
                dev.rpgsolo.world.EspacoMundo(
                    db.mundoSnapshots().contar(mundoId),
                    db.mundoSnapshots().somaBytes(mundoId),
                    db.mundoCronica().contar(mundoId),
                    db.mundoCronica().somaBytes(mundoId),
                )
            }
        }

    override fun podarSnapshots(
        mundoId: String,
        manter: Int,
    ) = io {
        require(manter >= 1) { "manter ao menos 1 snapshot" }
        db.mundoSnapshots().apagarAntigos(mundoId, manter)
    }

    override fun podarCronica(
        mundoId: String,
        antesDoDia: Int,
    ) = io { db.mundoCronica().apagarAntesDoDia(mundoId, antesDoDia) }

    override fun ramificar(
        origemId: String,
        novoId: String,
        bloco: Int,
        agora: Long,
    ) = io {
        db.runInTransaction<dev.rpgsolo.world.MundoRegistro> {
            val origem = checkNotNull(db.mundos().buscar(origemId)) { "mundo inexistente: $origemId" }
            val alvo =
                if (bloco > 0) {
                    checkNotNull(
                        db.mundoSnapshots().buscar(origemId, bloco),
                    ) { "bloco $bloco inexistente em $origemId" }
                } else {
                    null
                }
            val novo =
                origem.copy(
                    id = novoId,
                    criadoEm = agora,
                    atualizadoEm = agora,
                    proximoDia = alvo?.dia ?: 0,
                    totalDeltas = alvo?.deltas ?: 0,
                    hashCronica = alvo?.hashCronica ?: "",
                    ultimoBloco = bloco,
                )
            db.mundos().inserir(novo)
            if (alvo != null) {
                db.mundoSnapshots().copiar(origemId, novoId, bloco, agora)
                db.mundoCronica().copiar(origemId, novoId, alvo.dia)
            }
            novo.paraRegistro()
        }
    }

    override fun apagarMundo(mundoId: String) =
        io {
            db.runInTransaction<Boolean> {
                if (db.campanhaMundo().contarPorMundo(mundoId) > 0) {
                    false
                } else {
                    db.mundos().apagar(mundoId)
                    true
                }
            }
        }
}
