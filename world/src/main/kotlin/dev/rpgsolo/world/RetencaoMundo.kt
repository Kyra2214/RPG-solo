package dev.rpgsolo.world

/**
 * Janela de retenção do detalhe (D-I3, MI-11).
 * 🔶 ASSUMINDO: crônica filtrada do último século e os 5 últimos snapshots (50 anos de retomada); ajustáveis.
 */
data class PoliticaRetencao(
    val janelaCronicaDias: Int = 100 * CalendarioReino.DIAS_ANO,
    val snapshotsMantidos: Int = 5,
) {
    init {
        require(janelaCronicaDias >= 0) { "janela da crônica negativa" }
        require(snapshotsMantidos >= 1) { "manter ao menos 1 snapshot" }
    }
}

class ResultadoLimpeza(val snapshotsApagados: Int, val linhasApagadas: Int, val bytesAntes: Long, val bytesDepois: Long)

/** Ponto de onde o mundo continua: [snapshot] nulo = ainda no molde (bloco 0). [corrigido] = o progresso foi realinhado. */
class PontoRetomada(val bloco: Int, val snapshot: Snapshot?, val corrigido: Boolean)

object RetencaoMundo {
    /** Aplica a política ao mundo: poda snapshots antigos e a crônica fora da janela (sempre contada a partir de `proximoDia`). */
    fun limpar(
        store: MundoStore,
        mundoId: String,
        politica: PoliticaRetencao = PoliticaRetencao(),
    ): ResultadoLimpeza {
        val mundo = checkNotNull(store.mundo(mundoId)) { "mundo inexistente: $mundoId" }
        val antes = store.bytesEmDisco(mundoId)
        val snaps = store.podarSnapshots(mundoId, politica.snapshotsMantidos)
        val corte = mundo.proximoDia - politica.janelaCronicaDias
        val linhas = if (corte > 0) store.podarCronica(mundoId, corte) else 0
        return ResultadoLimpeza(snaps, linhas, antes, store.bytesEmDisco(mundoId))
    }

    /** Apaga os mundos sem campanha; devolve os ids apagados. */
    fun limparOrfaos(store: MundoStore): List<String> =
        store.mundos().map { it.id }.filter { store.campanhasDoMundo(it) == 0 && store.apagarMundo(it) }
}

object RetomadaMundo {
    /**
     * Ponto de retomada do mundo: o último snapshot guardado (cada bloco é gravado numa transação, então um app
     * encerrado no meio de um bloco deixa o banco no bloco anterior). Se o progresso do mundo ou a crônica estiverem
     * à frente desse snapshot, realinha por [MundoStore.voltarAoBloco].
     */
    fun retomar(
        store: MundoStore,
        mundoId: String,
        agora: Long,
    ): PontoRetomada {
        val mundo = checkNotNull(store.mundo(mundoId)) { "mundo inexistente: $mundoId" }
        val ultimo = store.ultimoSnapshot(mundoId)
        val bloco = ultimo?.info?.bloco ?: 0
        val dia = ultimo?.snapshot?.dia ?: 0
        val deltas = ultimo?.snapshot?.deltas ?: 0
        val hash = ultimo?.snapshot?.hashCronica ?: ""
        val desalinhado =
            mundo.ultimoBloco != bloco || mundo.proximoDia != dia || mundo.totalDeltas != deltas || mundo.hashCronica != hash ||
                store.cronica(mundoId, desdeDia = dia, nivelMaximo = Int.MAX_VALUE, limite = 1).isNotEmpty()
        if (desalinhado) store.voltarAoBloco(mundoId, bloco, agora)
        return PontoRetomada(bloco, ultimo?.snapshot, desalinhado)
    }
}
