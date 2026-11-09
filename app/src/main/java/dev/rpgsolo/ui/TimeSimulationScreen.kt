package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.rpgsolo.world.MundoRegistro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Tela cheia "Passar o tempo" (§4, MI-15): período, velocidade, calendário, progresso por bloco, pausar, retomar,
 * cancelar, parar no próximo marco, estados de erro, feed por nível e resumo final (MI-16). "Aplicar à campanha" grava
 * o lote na campanha e "Desfazer" o reverte junto com o mundo (MI-17). Nada entra na campanha antes de aplicar.
 */
@Composable
fun TimeSimulationScreen(
    model: AppModel,
    mundo: MundoRegistro,
    onClose: () -> Unit,
) {
    val scope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    val controller =
        remember(mundo.id) {
            TimeSimulationController(
                model.worldStoreForSimulation(),
                MythralisRuntime.fabrica(mundo.semente),
                scope,
                morteDoHeroi = { a, b -> model.heroDeathIn(a, b) },
            )
        }
    DisposableEffect(controller) {
        onDispose {
            controller.cancelar()
            scope.launch {
                controller.aguardar()
                scope.cancel()
            }
        }
    }
    val estado by controller.estado.collectAsState()
    val feed by controller.feed.collectAsState()

    var periodo by remember { mutableStateOf(PeriodoSimulacao.UM_ANO) }
    var livre by remember { mutableStateOf("") }
    var velocidade by remember { mutableStateOf(VelocidadeSimulacao.X1) }
    var pararEmMarco by remember { mutableStateOf(false) }
    var agora by remember { mutableStateOf(System.currentTimeMillis()) }
    var exibicao by remember { mutableStateOf<ExibicaoSimulacao?>(null) }
    var lote by remember { mutableStateOf<LoteAplicado?>(null) }
    var aplicando by remember { mutableStateOf(false) }
    var falhaAplicar by remember { mutableStateOf<String?>(null) }
    var escolheuLegado by remember { mutableStateOf(false) }

    val livreResultado = if (livre.isBlank()) null else Periodos.livre(livre)
    val dias =
        when (livreResultado) {
            is Periodos.Livre.Valido -> livreResultado.dias
            is Periodos.Livre.Invalido -> null
            null -> periodo.dias
        }
    val ocupado = estado is EstadoSimulacao.Executando || estado is EstadoSimulacao.Pausado
    val atual = mundo.proximoDia

    LaunchedEffect(ocupado) {
        while (ocupado) {
            agora = System.currentTimeMillis()
            delay(100)
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Passar o tempo", style = MaterialTheme.typography.titleLarge)
        Muted(
            "Mundo ${mundo.moldeId.removePrefix(
                "world.",
            ).replaceFirstChar { it.uppercase() }}. Nada entra na campanha antes de você aplicar o resultado.",
        )

        if (!ocupado && estado !is EstadoSimulacao.Concluido && estado !is EstadoSimulacao.Aplicado) {
            SectionTitle("Quanto tempo passa")
            Row(Modifier.fillMaxWidth().semantics { contentDescription = "Períodos" }, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PeriodoSimulacao.values().toList().chunked(3).forEach { linha ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            linha.forEach { p ->
                                FilterChip(selected = livre.isBlank() && periodo == p, onClick = {
                                    periodo = p
                                    livre = ""
                                }, label = { Text(p.rotulo) })
                            }
                        }
                    }
                }
            }
            OutlinedTextField(
                value = livre,
                onValueChange = { livre = it },
                label = { Text("Ou digite: N dias / N anos") },
                singleLine = true,
                isError = livreResultado is Periodos.Livre.Invalido,
                modifier = Modifier.fillMaxWidth(),
            )
            if (livreResultado is Periodos.Livre.Invalido) ErrorText(livreResultado.motivo)
            if (dias != null) Muted(Periodos.resumo(atual, dias))

            SectionTitle("Velocidade", "Só muda a exibição, nunca o resultado. Até 1 estação o resultado aparece de uma vez.")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                VelocidadeSimulacao.values().forEach { v ->
                    FilterChip(selected = velocidade == v, onClick = { velocidade = v }, label = { Text(v.rotulo) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = pararEmMarco, onClick = { pararEmMarco = !pararEmMarco }, label = { Text("Parar no próximo marco") })
            }
            Button(
                enabled = dias != null,
                onClick = {
                    val d = checkNotNull(dias)
                    exibicao = ExibicaoSimulacao(atual, atual + d, velocidade)
                    controller.iniciar(mundo.id, d, pararEmMarco)
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Passar o tempo") }
        }

        val progresso =
            when (val e = estado) {
                is EstadoSimulacao.Executando -> e.progresso
                is EstadoSimulacao.Pausado -> e.progresso
                is EstadoSimulacao.Concluido -> e.progresso
                is EstadoSimulacao.Aplicado -> e.progresso
                else -> null
            }
        if (progresso != null) {
            val ex = exibicao
            val quadro =
                ex?.atualizar(agora, progresso.diaAtual, estado is EstadoSimulacao.Concluido || estado is EstadoSimulacao.Aplicado)
            val morte = (estado as? EstadoSimulacao.Concluido)?.morte ?: (estado as? EstadoSimulacao.Aplicado)?.morte
            val diaVisto = if (morte != null) progresso.diaAtual else (quadro?.dia ?: progresso.diaAtual)
            val pct = Periodos.porcentagem(progresso)
            AppCard {
                Text(Periodos.data(diaVisto), style = MaterialTheme.typography.titleMedium)
                Muted("Blocos de 10 anos concluídos: ${progresso.blocosFeitos}")
                LinearProgressIndicator(
                    progress = { pct / 100f },
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Progresso da simulação: $pct por cento" },
                )
                Text(
                    when (val e = estado) {
                        is EstadoSimulacao.Pausado -> if (e.emMarco) "Parou num marco." else "Pausado."
                        is EstadoSimulacao.Concluido -> "Concluído."
                        is EstadoSimulacao.Aplicado -> "Aplicado à campanha."
                        else -> if (quadro?.esperandoCalculo == true) "Calculando…" else "Simulando…"
                    },
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
                if (quadro?.aparelhoLento == true) {
                    ErrorText(
                        "O aparelho está mais lento que a velocidade escolhida; a exibição espera o cálculo.",
                    )
                }
            }
            val marcosVistos = feed.marcos.filter { it.dia < diaVisto }
            if (marcosVistos.isNotEmpty()) {
                AppCard {
                    Text("Marcos", style = MaterialTheme.typography.titleMedium)
                    marcosVistos.takeLast(15).reversed().forEach { m ->
                        Text("Ano ${m.ano}: ${m.texto}", style = MaterialTheme.typography.bodyMedium)
                        Muted(m.reinos + (m.cidade?.let { " / $it" } ?: ""))
                    }
                }
            }
            if (feed.decadas.isNotEmpty() || feed.resumo.eventosNivel3 > 0) {
                AppCard {
                    Text("Resumo por década", style = MaterialTheme.typography.titleMedium)
                    feed.decadas.takeLast(
                        6,
                    ).forEach { Muted("Anos ${it.decada * 10} a ${it.decada * 10 + 9}, ${it.reino}: ${it.eventos} eventos") }
                    Muted("${feed.resumo.eventosNivel3} eventos menores (nível 3), só contados.")
                }
            }
            if (estado is EstadoSimulacao.Concluido || estado is EstadoSimulacao.Aplicado) {
                val r = feed.resumo
                AppCard {
                    Text("Resumo do período", style = MaterialTheme.typography.titleMedium)
                    Text("Marcos: ${r.marcos}")
                    Text("Reinos caídos: ${if (r.reinosCaidos.isEmpty()) "nenhum" else r.reinosCaidos.joinToString(", ")}")
                    Text("Pactos novos: ${r.pactosNovos} (desfeitos: ${r.pactosDesfeitos})")
                    Text("NPCs principais mortos: ${r.npcsMortos}; empossados: ${r.npcsEmpossados}")
                    if (morte != null) Text("A simulação parou no dia da morte do herói; o mundo pode continuar depois, sem ele.")
                }
            }
        }

        when (val e = estado) {
            is EstadoSimulacao.Executando ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        controller.pausar()
                        exibicao?.pausar(System.currentTimeMillis())
                    }) { Text("Pausar") }
                    OutlinedButton(onClick = { controller.cancelar() }) { Text("Cancelar") }
                }
            is EstadoSimulacao.Pausado ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        controller.retomar()
                        exibicao?.retomar(System.currentTimeMillis())
                    }) { Text("Retomar") }
                    OutlinedButton(onClick = { controller.cancelar() }) { Text("Cancelar") }
                }
            is EstadoSimulacao.Concluido -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        enabled = !aplicando,
                        onClick = {
                            aplicando = true
                            falhaAplicar = null
                            val p = e.progresso
                            scope.launch {
                                val r = model.applySimulation(p.diaInicio, p.diaAtual, feed)
                                when (r) {
                                    is ResultadoAplicacao.Aplicado -> {
                                        lote = r.lote
                                        controller.aplicado()
                                    }
                                    is ResultadoAplicacao.Recusado -> falhaAplicar = r.motivo
                                }
                                aplicando = false
                            }
                        },
                    ) { Text("Aplicar à campanha") }
                    OutlinedButton(enabled = !aplicando, onClick = { controller.descartar() }) { Text("Descartar") }
                }
                falhaAplicar?.let { ErrorText("A campanha recusou o lote e nada foi alterado: $it") }
            }
            is EstadoSimulacao.Aplicado -> {
                Muted(
                    "Aplicado: a campanha avançou ${e.progresso.diaAtual - e.progresso.diaInicio} dias e os marcos entraram nos fatos do mundo.",
                )
                e.morte?.let { MorteHeroiCard(model, it, scope) { escolheuLegado = true } }
                if (escolheuLegado) Muted("Depois de escolher o que vem depois da morte, desfazer a simulação não vale mais.")
                OutlinedButton(
                    enabled = !aplicando && !escolheuLegado,
                    onClick = {
                        aplicando = true
                        scope.launch {
                            lote?.let { model.undoSimulation(it) }
                            lote = null
                            controller.descartar()
                            aplicando = false
                        }
                    },
                ) { Text("Desfazer") }
            }
            is EstadoSimulacao.Cancelado -> Muted("Cancelado: o mundo voltou ao ponto em que estava antes deste período.")
            is EstadoSimulacao.Erro -> {
                ErrorText(
                    "A simulação parou: ${e.mensagem}. O bloco com problema não foi gravado; descarte este período para voltar ao ponto de partida.",
                )
                OutlinedButton(onClick = { controller.descartar() }) { Text("Descartar") }
            }
            EstadoSimulacao.Parado -> Unit
        }
        OutlinedButton(onClick = onClose, enabled = !ocupado, modifier = Modifier.fillMaxWidth()) { Text("Voltar à mesa") }
    }
}

/** MI-27: resumo da morte do herói e as opções do legado (M-04): renascer com item, assumir um NPC ou encerrar. O motor valida. */
@Composable
private fun MorteHeroiCard(
    model: AppModel,
    morte: MorteHeroi,
    scope: CoroutineScope,
    aoEscolher: () -> Unit,
) {
    var versao by remember { mutableStateOf(0) }
    var recusa by remember { mutableStateOf<String?>(null) }
    val resumo = remember(morte) { model.heroDeathSummary(morte) }
    val view = remember(versao) { model.legacyView() }

    fun escolher(acao: () -> String?) {
        scope.launch {
            recusa = acao()
            if (recusa == null) aoEscolher()
            versao++
        }
    }
    AppCard {
        Text("O herói morreu", style = MaterialTheme.typography.titleMedium)
        Text(resumo)
        when {
            view == null -> Muted("O herói não está mais morto.")
            view.endedReason != null -> Muted("Campanha encerrada: ${view.endedReason}")
            view.rebirthItemName != null ->
                Button(onClick = {
                    escolher { model.legacyRebirth() }
                }, modifier = Modifier.fillMaxWidth()) { Text("Renascer com ${view.rebirthItemName}") }
            view.npcs.isNotEmpty() -> {
                Muted("Quem você pode assumir:")
                view.npcs.forEach { n ->
                    OutlinedButton(
                        onClick = { escolher { model.legacyAssume(n.id) } },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Assumir ${n.name}") }
                }
            }
            view.canEnd ->
                Button(
                    onClick = { escolher { model.legacyEnd() } },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Encerrar a campanha") }
        }
        recusa?.let { ErrorText(it) }
    }
}
