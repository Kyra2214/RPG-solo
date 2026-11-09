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
    fun renomear(
        id: String,
        nome: String,
    )

    @Query("DELETE FROM campanhas WHERE id = :id")
    fun apagar(id: String)

    @Query("UPDATE campanhas SET estado = :estado, atualizadaEm = :agora WHERE id = :id")
    fun definirEstado(
        id: String,
        estado: String,
        agora: Long,
    ): Int

    @Query("UPDATE campanhas SET atualizadaEm = :agora WHERE id = :id")
    fun tocar(
        id: String,
        agora: Long,
    )
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
    fun apagarDepoisDe(
        id: String,
        aPartirDe: Int,
    )
}

@Dao
interface FichaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: FichaRow)

    /** Atualiza o conteúdo sem substituir a linha e sem desarquivar a ficha. */
    @Query("UPDATE fichas SET nome = :nome, json = :json WHERE id = :id")
    fun atualizarConteudo(
        id: String,
        nome: String,
        json: String,
    ): Int

    @Query("SELECT * FROM fichas WHERE id = :id")
    fun buscar(id: String): FichaRow?

    @Query("SELECT * FROM fichas WHERE arquivada = 0 ORDER BY nome")
    fun listar(): List<FichaRow>

    @Query("SELECT * FROM fichas WHERE arquivada = 1 ORDER BY nome")
    fun listarArquivadas(): List<FichaRow>

    @Query("UPDATE fichas SET arquivada = :arquivada WHERE id = :id")
    fun definirArquivada(
        id: String,
        arquivada: Boolean,
    ): Int

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
    fun apagarDepoisDe(
        id: String,
        aPartirDe: Int,
    )

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

@Dao
interface CanonFactsDao {
    @Query("SELECT * FROM fatos_canonicos WHERE campaignId = :id")
    fun buscar(id: String): CanonFactsRow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: CanonFactsRow)

    @Query("DELETE FROM fatos_canonicos WHERE campaignId = :id")
    fun apagar(id: String)
}

@Dao
interface MundoDao {
    @Query("SELECT * FROM mundos WHERE id = :id")
    fun buscar(id: String): MundoRow?

    @Query("SELECT * FROM mundos ORDER BY criadoEm, id")
    fun listar(): List<MundoRow>

    /** ABORT (o padrão): id repetido falha em vez de substituir o mundo e apagar o que depende dele. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserir(row: MundoRow)

    @androidx.room.Update
    fun atualizar(row: MundoRow)

    @Query("DELETE FROM mundos WHERE id = :id")
    fun apagar(id: String)
}

@Dao
interface MundoSnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: MundoSnapshotRow)

    @Query("SELECT * FROM mundo_snapshots WHERE mundoId = :mundoId ORDER BY bloco DESC LIMIT 1")
    fun ultimo(mundoId: String): MundoSnapshotRow?

    @Query("SELECT * FROM mundo_snapshots WHERE mundoId = :mundoId AND bloco = :bloco")
    fun buscar(
        mundoId: String,
        bloco: Int,
    ): MundoSnapshotRow?

    @Query(
        "SELECT bloco, dia, hashEstado, hashCronica, deltas, bytes, criadoEm FROM mundo_snapshots " +
            "WHERE mundoId = :mundoId ORDER BY bloco",
    )
    fun listarInfo(mundoId: String): List<MundoSnapshotInfoRow>

    @Query("DELETE FROM mundo_snapshots WHERE mundoId = :mundoId AND bloco > :bloco")
    fun apagarDepoisDe(
        mundoId: String,
        bloco: Int,
    )

    @Query("SELECT COALESCE(SUM(bytes), 0) FROM mundo_snapshots WHERE mundoId = :mundoId")
    fun somaBytes(mundoId: String): Long

    /** Copia o snapshot [bloco] de um mundo para outro (ramificação, MI-12). */
    @Query(
        "INSERT INTO mundo_snapshots (mundoId, bloco, dia, hashEstado, hashCronica, deltas, bytes, criadoEm, snapshotJson) " +
            "SELECT :novoId, bloco, dia, hashEstado, hashCronica, deltas, bytes, :agora, snapshotJson " +
            "FROM mundo_snapshots WHERE mundoId = :origemId AND bloco = :bloco",
    )
    fun copiar(
        origemId: String,
        novoId: String,
        bloco: Int,
        agora: Long,
    )

    @Query("SELECT COUNT(*) FROM mundo_snapshots WHERE mundoId = :mundoId")
    fun contar(mundoId: String): Int

    /** Mantém os [manter] últimos snapshots (por número de bloco) e apaga os anteriores. */
    @Query(
        "DELETE FROM mundo_snapshots WHERE mundoId = :mundoId AND bloco <= " +
            "(SELECT MAX(bloco) FROM mundo_snapshots WHERE mundoId = :mundoId) - :manter",
    )
    fun apagarAntigos(
        mundoId: String,
        manter: Int,
    ): Int
}

@Dao
interface MundoCronicaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun anexar(rows: List<MundoCronicaRow>)

    @Query(
        "SELECT * FROM mundo_cronica WHERE mundoId = :mundoId AND dia >= :desdeDia AND dia <= :ateDia " +
            "AND nivel <= :nivelMaximo ORDER BY seq LIMIT :limite",
    )
    fun listar(
        mundoId: String,
        desdeDia: Int,
        ateDia: Int,
        nivelMaximo: Int,
        limite: Int,
    ): List<MundoCronicaRow>

    /** Descarta as linhas posteriores a um dia (usado ao voltar a um snapshot: `dia` é o `proximoDia` dele). */
    @Query("DELETE FROM mundo_cronica WHERE mundoId = :mundoId AND dia >= :dia")
    fun apagarDesdeDia(
        mundoId: String,
        dia: Int,
    )

    @Query("SELECT COALESCE(SUM(LENGTH(linhaJson)), 0) FROM mundo_cronica WHERE mundoId = :mundoId")
    fun somaBytes(mundoId: String): Long

    /** Copia a crônica anterior ao dia [antesDoDia] de um mundo para outro (ramificação, MI-12). */
    @Query(
        "INSERT INTO mundo_cronica (mundoId, seq, deltaId, dia, nivel, importancia, tipo, entidade, linhaJson) " +
            "SELECT :novoId, seq, deltaId, dia, nivel, importancia, tipo, entidade, linhaJson " +
            "FROM mundo_cronica WHERE mundoId = :origemId AND dia < :antesDoDia",
    )
    fun copiar(
        origemId: String,
        novoId: String,
        antesDoDia: Int,
    )

    @Query("SELECT COUNT(*) FROM mundo_cronica WHERE mundoId = :mundoId")
    fun contar(mundoId: String): Int

    @Query("DELETE FROM mundo_cronica WHERE mundoId = :mundoId AND dia < :dia")
    fun apagarAntesDoDia(
        mundoId: String,
        dia: Int,
    ): Int
}

@Dao
interface CampanhaMundoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salvar(row: CampanhaMundoRow)

    @Query("SELECT * FROM campanha_mundo WHERE campaignId = :campaignId")
    fun buscar(campaignId: String): CampanhaMundoRow?

    @Query("SELECT COUNT(*) FROM campanha_mundo WHERE mundoId = :mundoId")
    fun contarPorMundo(mundoId: String): Int

    @Query("DELETE FROM campanha_mundo WHERE campaignId = :campaignId")
    fun apagar(campaignId: String)
}
