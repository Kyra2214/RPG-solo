package dev.rpgsolo.ui

import dev.rpgsolo.world.AcumuladorFeed
import dev.rpgsolo.world.BlocoSaude
import dev.rpgsolo.world.CalendarioReino
import dev.rpgsolo.world.CronicaGuardada
import dev.rpgsolo.world.CronicaSaida
import dev.rpgsolo.world.FeedSimulacao
import dev.rpgsolo.world.FiltroCronica
import dev.rpgsolo.world.JsonLeitor
import dev.rpgsolo.world.MundoStore
import dev.rpgsolo.world.Niveis
import dev.rpgsolo.world.RetomadaMundo
import dev.rpgsolo.world.Simulador
import dev.rpgsolo.world.Snapshot
import dev.rpgsolo.world.Valores
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Constrói o [Simulador] do mundo: do molde ([retomada] nula) ou de um snapshot. Quem monta os motores é o `app`. */
fun interface SimuladorFabrica {
    fun criar(
        retomada: Snapshot?,
        saida: CronicaSaida,
        aoFecharBloco: (BlocoSaude, Snapshot?) -> Unit,
    ): Simulador
}

/** Andamento do período: [diaAtual] é o próximo dia a simular; [blocosFeitos] conta só esta execução. */
data class ProgressoSimulacao(val diaInicio: Int, val diaAtual: Int, val diaFim: Int, val blocosFeitos: Int, val deltas: Int)

/** MI-27: o herói morreu de velhice no [dia] do mundo; a simulação para nesse dia. */
data class MorteHeroi(val dia: Int, val causa: String)

sealed interface EstadoSimulacao {
    data object Parado : EstadoSimulacao

    data class Executando(val progresso: ProgressoSimulacao) : EstadoSimulacao

    /** [emMarco] = parou sozinho num bloco com evento de nível 1 (D-I8); senão foi pedido pelo jogador. */
    data class Pausado(val progresso: ProgressoSimulacao, val emMarco: Boolean) : EstadoSimulacao

    data class Concluido(val progresso: ProgressoSimulacao, val morte: MorteHeroi? = null) : EstadoSimulacao

    /** O período foi aplicado à campanha (MI-17); só [TimeSimulationController.descartar] ainda o desfaz, junto com o lote da campanha. */
    data class Aplicado(val progresso: ProgressoSimulacao, val morte: MorteHeroi? = null) : EstadoSimulacao

    data object Cancelado : EstadoSimulacao

    data class Erro(val mensagem: String) : EstadoSimulacao
}

/**
 * Controlador da simulação de tempo (MI-13). Roda o [Simulador] em [dispatcher] (padrão `Dispatchers.Default`) um bloco
 * por vez (blocos de [Simulador.passo] anos alinhados ao calendário, o que não muda a crônica, I-22). Ao fim de cada
 * bloco sem falha grava snapshot e crônica filtrada numa transação (`guardarBloco`); pausa, cancelamento e parada em
 * marco só agem nessa fronteira. Uma FALHA de invariante interrompe a execução e o bloco com falha não é gravado.
 * 🔶 ASSUMINDO: cancelar (e [descartar]) volta ao bloco em que a execução começou, não só ao último bloco gravado: nada
 * do período fica no mundo antes de "aplicar" (MI-17). Marco = bloco que contém delta de nível 1.
 * MI-27: [morteDoHeroi] diz se o herói morre de velhice dentro do período; se morre, o período termina no dia da morte
 * (o último bloco é parcial, como em qualquer período que não fecha o bloco) e o estado final carrega a [MorteHeroi].
 */
class TimeSimulationController(
    private val store: MundoStore,
    private val fabrica: SimuladorFabrica,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val relogio: () -> Long = System::currentTimeMillis,
    private val morteDoHeroi: (diaInicio: Int, diaFim: Int) -> MorteHeroi? = { _, _ -> null },
) {
    private val _estado = MutableStateFlow<EstadoSimulacao>(EstadoSimulacao.Parado)
    val estado: StateFlow<EstadoSimulacao> get() = _estado
    private val _feed = MutableStateFlow(FeedSimulacao.VAZIO)

    /** Marcos ao vivo, resumo por década e resumo do período, atualizados a cada bloco gravado (MI-16). */
    val feed: StateFlow<FeedSimulacao> get() = _feed

    @Volatile private var pausaPedida = false

    @Volatile private var cancelPedido = false

    @Volatile private var emMarco = false
    private var job: Job? = null
    private var mundoAtual: String? = null
    private var blocoInicial = -1

    private val ativo: Boolean get() = _estado.value.let { it is EstadoSimulacao.Executando || it is EstadoSimulacao.Pausado }

    /** Simula [dias] dias do mundo [mundoId] a partir do ponto de retomada; [pararEmMarco] pausa em evento de nível 1. */
    fun iniciar(
        mundoId: String,
        dias: Int,
        pararEmMarco: Boolean = false,
    ) {
        require(dias > 0) { "período deve ser positivo" }
        check(!ativo) { "já há uma simulação em andamento" }
        pausaPedida = false
        cancelPedido = false
        emMarco = false
        mundoAtual = mundoId
        blocoInicial = -1
        _estado.value = EstadoSimulacao.Executando(ProgressoSimulacao(0, 0, 0, 0, 0))
        job = scope.launch(dispatcher) { executar(mundoId, dias, pararEmMarco) }
    }

    fun pausar() {
        if (_estado.value is EstadoSimulacao.Executando) pausaPedida = true
    }

    fun retomar() {
        emMarco = false
        pausaPedida = false
    }

    fun cancelar() {
        if (ativo) cancelPedido = true
    }

    /** Marca o período concluído como aplicado à campanha (MI-17). */
    fun aplicado() {
        val e = _estado.value
        if (e is EstadoSimulacao.Concluido) _estado.value = EstadoSimulacao.Aplicado(e.progresso, e.morte)
    }

    /**
     * Depois de [EstadoSimulacao.Concluido], [EstadoSimulacao.Erro] ou [EstadoSimulacao.Aplicado]: joga fora o período e
     * volta ao bloco em que ele começou. Depois de aplicar, quem chama desfaz antes o lote da campanha.
     */
    fun descartar() {
        val id = mundoAtual ?: return
        if (_estado.value !is EstadoSimulacao.Concluido && _estado.value !is EstadoSimulacao.Erro && _estado.value !is EstadoSimulacao.Aplicado) return
        if (blocoInicial < 0) return
        store.voltarAoBloco(id, blocoInicial, relogio())
        _feed.value = FeedSimulacao.VAZIO
        _estado.value = EstadoSimulacao.Parado
    }

    /** Espera o fim da execução (para quem chama de fora, como os testes). */
    suspend fun aguardar() {
        job?.join()
    }

    private suspend fun executar(
        mundoId: String,
        dias: Int,
        pararEmMarco: Boolean,
    ) {
        try {
            val ponto = RetomadaMundo.retomar(store, mundoId, relogio())
            blocoInicial = ponto.bloco
            val linhas = ArrayList<String>()
            var capturado: Snapshot? = null
            val sim = fabrica.criar(ponto.snapshot, { linhas.add(it) }) { _, s -> capturado = s }
            val niveis = Niveis(sim.mundo, emptyList(), sim.estado)
            val acumulador = AcumuladorFeed(sim.mundo, niveis)
            _feed.value = FeedSimulacao.VAZIO
            val blocoDias = sim.passo * CalendarioReino.DIAS_ANO
            val inicio = sim.estado.proximoDia
            val morte = morteDoHeroi(inicio, inicio + dias)
            val fim = morte?.dia ?: (inicio + dias)
            var bloco = ponto.bloco
            var feitos = 0

            fun progresso() = ProgressoSimulacao(inicio, sim.estado.proximoDia, fim, feitos, sim.crono.total)
            _estado.value = EstadoSimulacao.Executando(progresso())
            while (sim.estado.proximoDia < fim) {
                if (cancelPedido) return cancelarExecucao(mundoId)
                val alvo = minOf(fim, (sim.estado.proximoDia / blocoDias + 1) * blocoDias)
                linhas.clear()
                capturado = null
                val r = sim.avancarAte(alvo)
                if (r.resultado == "FALHA") {
                    val f = sim.saude.lastOrNull()?.falhas?.firstOrNull()
                    _estado.value = EstadoSimulacao.Erro("invariante violada no bloco ${bloco + 1}: ${f?.inv} ${f?.msg}".trim())
                    return
                }
                val snap = checkNotNull(capturado) { "bloco sem snapshot" }
                var marco = false
                val deltas = ArrayList<Map<String, Any?>>()
                val brutas = ArrayList<String>()
                for (texto in linhas) {
                    val linha = JsonLeitor.objeto(texto)
                    @Suppress("UNCHECKED_CAST")
                    val d = linha["delta"] as? Map<String, Any?> ?: continue
                    niveis.aprender(d)
                    deltas.add(d)
                    brutas.add(texto)
                }
                val guardadas = ArrayList<CronicaGuardada>()
                for ((k, d) in deltas.withIndex()) {
                    val nivel = niveis.nivel(d)
                    if (nivel == 1) marco = true
                    acumulador.registrar(d, nivel)
                    val imp = Valores.numero(d["importancia"]).toInt()
                    if (!FiltroCronica.guarda(imp, nivel)) continue
                    val id = d["id"].toString()
                    guardadas.add(
                        CronicaGuardada(
                            id.drop(1).toInt(), id, Valores.numero(d["dia_mundo"]).toInt(), nivel, imp,
                            d["tipo"].toString(), d["entidade"].toString(), brutas[k],
                        ),
                    )
                }
                bloco++
                feitos++
                store.guardarBloco(mundoId, bloco, snap, guardadas, relogio())
                _feed.value = acumulador.feed()
                _estado.value = EstadoSimulacao.Executando(progresso())
                if (pararEmMarco && marco && sim.estado.proximoDia < fim) {
                    emMarco = true
                    pausaPedida = true
                }
                if (cancelPedido) return cancelarExecucao(mundoId)
                if (pausaPedida) {
                    _estado.value = EstadoSimulacao.Pausado(progresso(), emMarco)
                    while (pausaPedida && !cancelPedido) delay(100)
                    if (cancelPedido) return cancelarExecucao(mundoId)
                    _estado.value = EstadoSimulacao.Executando(progresso())
                }
            }
            _estado.value = EstadoSimulacao.Concluido(progresso(), morte)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _estado.value = EstadoSimulacao.Erro(e.message ?: e.javaClass.simpleName)
        }
    }

    private fun cancelarExecucao(mundoId: String) {
        store.voltarAoBloco(mundoId, blocoInicial, relogio())
        _feed.value = FeedSimulacao.VAZIO
        _estado.value = EstadoSimulacao.Cancelado
    }
}
