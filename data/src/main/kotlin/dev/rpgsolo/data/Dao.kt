package dev.rpgsolo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CampanhaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserir(row: CampanhaRow)

    @Query("SELECT * FROM campanhas WHERE id = :id")
    fun buscar(id: String): CampanhaRow?

    @Query("SELECT * FROM campanhas ORDER BY nome")
    fun listar(): List<CampanhaRow>

    @Query("UPDATE campanhas SET nome = :nome WHERE id = :id")
    fun renomear(id: String, nome: String)

    @Query("DELETE FROM campanhas WHERE id = :id")
    fun apagar(id: String)

    @Query("UPDATE campanhas SET estado = :estado, atualizadaEm = :agora WHERE id = :id")
    fun definirEstado(id: String, estado: String, agora: Long): Int

    @Query("UPDATE campanhas SET atualizadaEm = :agora WHERE id = :id")
    fun tocar(id: String, agora: Long)
}

@Dao
interface EventoDao {
    @Query("SELECT COUNT(*) FROM eventos WHERE campanhaId = :id")
    fun contar(id: String): Int

    @Query("SELECT payload FROM eventos WHERE campanhaId = :id ORDER BY seq")
    fun carregar(id: String): List<String>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserir(rows: List<EventoRow>)

    @Query("DELETE FROM eventos WHERE campanhaId = :id AND seq >= :aPartirDe")
    fun apagarDepoisDe(id: String, aPartirDe: Int)
}

@Dao
interface FichaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: FichaRow)

    /** Atualiza o conteúdo sem substituir a linha e sem desarquivar a ficha. */
    @Query("UPDATE fichas SET nome = :nome, json = :json WHERE id = :id")
    fun atualizarConteudo(id: String, nome: String, json: String): Int

    @Query("SELECT * FROM fichas WHERE id = :id")
    fun buscar(id: String): FichaRow?

    @Query("SELECT * FROM fichas WHERE arquivada = 0 ORDER BY nome")
    fun listar(): List<FichaRow>

    @Query("SELECT * FROM fichas WHERE arquivada = 1 ORDER BY nome")
    fun listarArquivadas(): List<FichaRow>

    @Query("UPDATE fichas SET arquivada = :arquivada WHERE id = :id")
    fun definirArquivada(id: String, arquivada: Boolean): Int

    @Query("DELETE FROM fichas WHERE id = :id")
    fun apagar(id: String): Int
}

@Dao
interface CampanhaMetaDao {
    @Query("SELECT * FROM campanha_meta WHERE campanhaId = :id")
    fun buscar(id: String): CampanhaMetaRow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: CampanhaMetaRow)

    @Query("DELETE FROM campanha_meta WHERE campanhaId = :id")
    fun apagar(id: String)
}

@Dao
interface CapituloDao {
    @Query("SELECT * FROM capitulos WHERE campanhaId = :id ORDER BY numero")
    fun listar(id: String): List<CapituloRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(rows: List<CapituloRow>)

    @Query("DELETE FROM capitulos WHERE campanhaId = :id")
    fun apagar(id: String)
}

@Dao
interface MissaoDao {
    @Query("SELECT * FROM missoes WHERE campanhaId = :id ORDER BY ordem")
    fun listar(id: String): List<MissaoRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(rows: List<MissaoRow>)

    @Query("DELETE FROM missoes WHERE campanhaId = :id")
    fun apagar(id: String)
}

@Dao
interface SnapshotDao {
    @Query("SELECT * FROM snapshots WHERE campanhaId = :id")
    fun buscar(id: String): SnapshotRow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: SnapshotRow)

    @Query("DELETE FROM snapshots WHERE campanhaId = :id AND eventCount > :aPartirDe")
    fun apagarDepoisDe(id: String, aPartirDe: Int)

    @Query("DELETE FROM snapshots WHERE campanhaId = :id")
    fun apagar(id: String)
}

@Dao
interface LinhaTempoDao {
    @Query("SELECT * FROM linha_tempo WHERE campanhaId = :id ORDER BY ordem")
    fun listar(id: String): List<LinhaTempoRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(rows: List<LinhaTempoRow>)

    @Query("DELETE FROM linha_tempo WHERE campanhaId = :id")
    fun apagar(id: String)
}

@Dao
interface FonteDao {
    @Query("SELECT * FROM fontes ORDER BY importedAt, id")
    fun listarFontes(): List<FonteRow>

    @Query("SELECT * FROM fonte_vinculos ORDER BY campaignId, linkedAt, sourceId")
    fun listarVinculos(): List<FonteVinculoRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvarFontes(rows: List<FonteRow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvarVinculos(rows: List<FonteVinculoRow>)

    @Query("DELETE FROM fonte_vinculos")
    fun apagarVinculos()

    @Query("DELETE FROM fontes")
    fun apagarFontes()

    @Query("DELETE FROM fontes WHERE id = :id")
    fun apagarFonte(id: String)
}

@Dao
interface FontePipelineDao {
    @Query("SELECT * FROM fonte_pipeline WHERE sourceId = :id")
    fun buscar(id: String): FontePipelineRow?

    @Query("SELECT * FROM fonte_pipeline ORDER BY sourceId")
    fun listar(): List<FontePipelineRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: FontePipelineRow)

    @Query("DELETE FROM fonte_pipeline WHERE sourceId = :id")
    fun apagar(id: String)
}
