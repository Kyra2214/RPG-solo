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
