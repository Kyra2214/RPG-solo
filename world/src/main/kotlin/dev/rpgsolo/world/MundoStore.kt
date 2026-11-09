package dev.rpgsolo.world

/**
 * Um mundo guardado no aparelho (D-I3, D-I4): a instância que evolui, criada a partir de um molde ([moldeId], [moldeHash]).
 * Os campos de progresso resumem o último ponto aceito: [proximoDia], [totalDeltas] e [hashCronica] vêm do último snapshot.
 */
data class MundoRegistro(
    val id: String,
    val moldeId: String,
    val moldeHash: String,
    val dinamicaHash: String,
    val semente: Long,
    val criadoEm: Long,
    val atualizadoEm: Long,
    val proximoDia: Int = 0,
    val totalDeltas: Int = 0,
    val hashCronica: String = "",
    /** Número do último snapshot guardado; 0 = o mundo ainda está no molde, sem nenhum bloco simulado. */
    val ultimoBloco: Int = 0,
)

/** Resumo de um snapshot, sem o estado (para listar, medir o tamanho em disco e escolher o que reter). */
data class SnapshotInfo(
    val bloco: Int,
    val dia: Int,
    val hashEstado: String,
    val hashCronica: String,
    val deltas: Int,
    val bytes: Int,
    val criadoEm: Long,
)

/** Um snapshot guardado: o ponto de retomada de um bloco (D-I3). */
class SnapshotGuardado(val info: SnapshotInfo, val snapshot: Snapshot)

/** Uma linha da crônica filtrada: os campos de busca mais a linha jsonl completa (`{"delta":...,"h":...}`). */
data class CronicaGuardada(
    val seq: Int,
    val deltaId: String,
    val dia: Int,
    val nivel: Int,
    val importancia: Int,
    val tipo: String,
    val entidade: String,
    val linhaJson: String,
)

/** Vínculo de uma campanha com o mundo em que ela acontece. */
data class VinculoMundo(
    val campanhaId: String,
    val mundoId: String,
    val moldeId: String,
    val vinculadoEm: Long,
)

/**
 * Quais deltas da crônica vão para o aparelho (D-I3): a crônica completa não é guardada (cerca de 240 mil deltas em
 * 500 anos); o hash encadeado é calculado em fluxo e confere o que foi descartado.
 * 🔶 ASSUMINDO: guarda o delta de nível 1 ou 2 **e** importância 2 ou mais; os dois limites são ajustáveis.
 */
object FiltroCronica {
    const val IMPORTANCIA_MINIMA = 2
    const val NIVEL_MAXIMO = 2

    fun guarda(
        importancia: Int,
        nivel: Int,
    ): Boolean = importancia >= IMPORTANCIA_MINIMA && nivel <= NIVEL_MAXIMO
}

/**
 * Armazenamento do mundo (MI-10). A implementação em Room fica no módulo `data`; o simulador e os controladores só
 * conhecem esta interface. Chamadas bloqueantes: use fora da thread principal.
 */
interface MundoStore {
    /** Cria o mundo; falha se o id já existe (nunca substitui: substituir apagaria snapshots, crônica e vínculos). */
    fun criarMundo(mundo: MundoRegistro)

    fun mundo(id: String): MundoRegistro?

    fun mundos(): List<MundoRegistro>

    /** Liga a campanha ao mundo (uma campanha tem um mundo); ligar de novo troca o mundo. O mundo precisa existir. */
    fun vincular(vinculo: VinculoMundo)

    fun vinculo(campanhaId: String): VinculoMundo?

    fun mundoDaCampanha(campanhaId: String): MundoRegistro?

    /** Quantas campanhas usam o mundo (um mundo sem campanha pode ser limpo, MI-11). */
    fun campanhasDoMundo(mundoId: String): Int

    /**
     * Guarda o snapshot de um bloco e atualiza o progresso do mundo, numa transação só. [linhas] são as linhas da
     * crônica filtrada produzidas desde o snapshot anterior (já passadas por [FiltroCronica]).
     */
    fun guardarBloco(
        mundoId: String,
        bloco: Int,
        snapshot: Snapshot,
        linhas: List<CronicaGuardada>,
        agora: Long,
    )

    /** Último snapshot do mundo, ou `null` se ainda não há nenhum. */
    fun ultimoSnapshot(mundoId: String): SnapshotGuardado?

    fun snapshots(mundoId: String): List<SnapshotInfo>

    /** Linhas da crônica filtrada no intervalo de dias, de nível até [nivelMaximo], em ordem de sequência. */
    fun cronica(
        mundoId: String,
        desdeDia: Int = 0,
        ateDia: Int = Int.MAX_VALUE,
        nivelMaximo: Int = FiltroCronica.NIVEL_MAXIMO,
        limite: Int = 500,
    ): List<CronicaGuardada>

    /** Descarta tudo o que veio depois do [bloco] (cancelar e voltar ao último snapshot aceito) e reajusta o progresso. */
    fun voltarAoBloco(
        mundoId: String,
        bloco: Int,
        agora: Long,
    )

    /** Tamanho aproximado, em bytes, do que o mundo ocupa (snapshots e crônica filtrada). */
    fun bytesEmDisco(mundoId: String): Long

    /** Detalhe do espaço ocupado pelo mundo (MI-11). */
    fun espaco(mundoId: String): EspacoMundo

    /** Apaga os snapshots mais antigos e mantém os [manter] últimos (mínimo 1); devolve quantos apagou. */
    fun podarSnapshots(
        mundoId: String,
        manter: Int,
    ): Int

    /** Apaga as linhas da crônica filtrada anteriores a [antesDoDia]; devolve quantas apagou. */
    fun podarCronica(
        mundoId: String,
        antesDoDia: Int,
    ): Int

    /**
     * Cria o mundo [novoId] como cópia independente do [origemId] no [bloco] (0 = o molde, sem estado): copia só aquele
     * snapshot (mesmo número de bloco) e a crônica filtrada anterior a ele; depois disso os dois mundos não se afetam (MI-12).
     */
    fun ramificar(
        origemId: String,
        novoId: String,
        bloco: Int,
        agora: Long,
    ): MundoRegistro

    /** Apaga o mundo e tudo o que depende dele, só se nenhuma campanha o usa; devolve se apagou. */
    fun apagarMundo(mundoId: String): Boolean
}

/** Espaço ocupado por um mundo: snapshots e crônica filtrada (MI-11). */
data class EspacoMundo(
    val snapshots: Int,
    val bytesSnapshots: Long,
    val linhasCronica: Int,
    val bytesCronica: Long,
) {
    val total: Long get() = bytesSnapshots + bytesCronica
}
