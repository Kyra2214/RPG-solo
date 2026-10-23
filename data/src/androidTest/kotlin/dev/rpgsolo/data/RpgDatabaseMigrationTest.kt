package dev.rpgsolo.data

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.rpgsolo.engine.Chapter
import dev.rpgsolo.engine.Codec
import dev.rpgsolo.engine.Quest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

/**
 * Q-04: teste instrumentado das migrações Room. Só os esquemas 2 e 3 estão versionados em `data/schemas`;
 * os testes que dependem do esquema da versão atual são ignorados até que `10.json` seja commitado
 * (o CI publica os esquemas gerados no artefato `rpgsolo-room-schemas`).
 */
@RunWith(AndroidJUnit4::class)
class RpgDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), RpgDatabase::class.java)

    private val targetContext get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val backupDbName = "migracao-backup-test.db"

    @After
    fun limpaBancosDeBackup() {
        targetContext.deleteDatabase(backupDbName)
        File(targetContext.getDatabasePath(backupDbName).parentFile, "$backupDbName.antes-v${RpgDatabase.VERSION}.bak").delete()
    }

    private fun migration(from: Int): Migration = RpgDatabase.MIGRATIONS.first { it.startVersion == from }

    private fun schemaExported(version: Int): Boolean =
        try {
            InstrumentationRegistry.getInstrumentation().context.assets.open("dev.rpgsolo.data.RpgDatabase/$version.json").close()
            true
        } catch (e: IOException) {
            false
        }

    private fun seedVersion2(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT INTO campanhas (id, nome, seed, configJson) VALUES ('c1', 'Campanha antiga', 7, '{}')")
        db.execSQL("INSERT INTO eventos (campanhaId, seq, payload) VALUES ('c1', 0, '{}')")
        db.execSQL("INSERT INTO fichas (id, nome, json, arquivada) VALUES ('f1', 'Brokk', '{}', 0)")
        db.execSQL(
            "INSERT INTO campanha_meta (campanhaId, chaptersJson, questsJson, timelineJson) VALUES (?, ?, ?, ?)",
            arrayOf<Any>(
                "c1",
                Codec.encodeChapters(listOf(Chapter(number = 1, title = "Início", startMark = 0))),
                Codec.encodeQuests(listOf(Quest(id = "q1", title = "Achar a passagem"))),
                Codec.encodeTimeline(listOf(3 to "Chegou à vila")),
            ),
        )
    }

    private fun count(
        db: SupportSQLiteDatabase,
        sql: String,
    ): Int = db.query(sql).use { c -> if (c.moveToFirst()) c.getInt(0) else -1 }

    @Test
    fun cadeiaDeMigracoesEhContinuaAteAVersaoAtual() {
        val starts = RpgDatabase.MIGRATIONS.map { it.startVersion }
        assertEquals((1 until RpgDatabase.VERSION).toList(), starts)
        assertTrue(RpgDatabase.MIGRATIONS.all { it.endVersion == it.startVersion + 1 })
    }

    @Test
    fun migracao2Para3_preservaMetadadosEAdicionaColunasNulas() {
        helper.createDatabase("migracao-2-3.db", 2).use { db ->
            db.execSQL("INSERT INTO campanhas (id, nome, seed, configJson) VALUES ('c1', 'Antiga', 1, '{}')")
            db.execSQL("INSERT INTO campanha_meta (campanhaId, chaptersJson, questsJson, timelineJson) VALUES ('c1', '[]', '[]', '[]')")
        }
        val migrated = helper.runMigrationsAndValidate("migracao-2-3.db", 3, true, migration(2))
        migrated.query("SELECT chaptersJson, playerId, fichaId, packageId, systemId, editionId FROM campanha_meta WHERE campanhaId = 'c1'").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("[]", c.getString(0))
            for (i in 1..5) assertTrue("coluna $i deveria ser nula", c.isNull(i))
        }
    }

    @Test
    fun banco2AbreNaVersaoAtualPeloBuilderDoApp_eMigraJsonsParaTabelas() {
        helper.createDatabase("migracao-completa.db", 2).use { seedVersion2(it) }
        val db =
            Room.databaseBuilder(targetContext, RpgDatabase::class.java, "migracao-completa.db")
                .addMigrations(*RpgDatabase.MIGRATIONS)
                .build()
        try {
            // A abertura aplica 2→atual e o Room valida o esquema final contra o compilado.
            val sqlite = db.openHelper.writableDatabase
            assertEquals(RpgDatabase.VERSION, sqlite.version)
            assertEquals(1, count(sqlite, "SELECT COUNT(*) FROM eventos WHERE campanhaId = 'c1'"))
            assertEquals(1, count(sqlite, "SELECT COUNT(*) FROM fichas WHERE id = 'f1'"))
            assertEquals(1, count(sqlite, "SELECT COUNT(*) FROM capitulos WHERE campanhaId = 'c1' AND numero = 1"))
            assertEquals(1, count(sqlite, "SELECT COUNT(*) FROM missoes WHERE campanhaId = 'c1' AND missaoId = 'q1' AND ordem = 0"))
            assertEquals(1, count(sqlite, "SELECT COUNT(*) FROM linha_tempo WHERE campanhaId = 'c1' AND eventoIndice = 3 AND texto = 'Chegou à vila'"))
            assertEquals(1, count(sqlite, "SELECT COUNT(*) FROM campanhas WHERE id = 'c1' AND estado = 'ATIVA' AND criadaEm = 0 AND atualizadaEm = 0"))
            // O JSON antigo continua intacto (rollback do app depende dele).
            assertEquals(1, count(sqlite, "SELECT COUNT(*) FROM campanha_meta WHERE campanhaId = 'c1' AND chaptersJson <> '[]'"))
            for (tabela in listOf("fontes", "fonte_vinculos", "fonte_pipeline", "fatos_canonicos", "snapshots")) {
                assertEquals("tabela $tabela deveria existir vazia", 0, count(sqlite, "SELECT COUNT(*) FROM $tabela"))
            }
        } finally {
            db.close()
        }
    }

    @Test
    fun migracaoCompleta_validaContraEsquemaExportadoDaVersaoAtual() {
        assumeTrue(
            "data/schemas/dev.rpgsolo.data.RpgDatabase/${RpgDatabase.VERSION}.json ainda não foi commitado",
            schemaExported(RpgDatabase.VERSION),
        )
        helper.createDatabase("migracao-validada.db", 2).use { seedVersion2(it) }
        val migrated = helper.runMigrationsAndValidate("migracao-validada.db", RpgDatabase.VERSION, true, *RpgDatabase.MIGRATIONS)
        assertEquals(1, count(migrated, "SELECT COUNT(*) FROM capitulos WHERE campanhaId = 'c1'"))
    }

    @Test
    fun backupAntesDeMigrar_copiaBancoAntigoEMantemSoAPrimeiraCopia() {
        val file = targetContext.getDatabasePath(backupDbName)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use {
            it.execSQL("CREATE TABLE marca (x INTEGER)")
            it.version = 2
        }
        val copy = RpgDatabase.backupBeforeMigration(targetContext, backupDbName)
        assertNotNull(copy)
        assertEquals("$backupDbName.antes-v${RpgDatabase.VERSION}.bak", copy!!.name)
        assertTrue(copy.exists())
        // Segunda chamada devolve a mesma cópia, sem sobrescrever.
        assertEquals(copy.path, RpgDatabase.backupBeforeMigration(targetContext, backupDbName)?.path)
    }

    @Test
    fun backupAntesDeMigrar_naoCopiaQuandoJaEstaNaVersaoAtualOuNaoHaBanco() {
        assertNull(RpgDatabase.backupBeforeMigration(targetContext, backupDbName))
        val file = targetContext.getDatabasePath(backupDbName)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { it.version = RpgDatabase.VERSION }
        assertNull(RpgDatabase.backupBeforeMigration(targetContext, backupDbName))
    }
}
