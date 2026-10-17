package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rpgsolo.ai.NewCampaignFlow
import dev.rpgsolo.engine.Ficha
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Nova campanha (9.1): descrição livre → perguntas do motor → revisão com avisos → ficha e nome → começar.
 * Toda a lógica está em [NewCampaignFlow] (testada em Kotlin puro); aqui só se desenha o passo atual.
 */
@Composable
fun NewCampaignSection(model: AppModel) {
    val flow = remember(model.controller != null) { model.newCampaignFlow() }
    Text("Nova campanha", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    if (flow == null) {
        Muted("Configure o provedor de IA na aba Config para criar uma campanha.")
        return
    }
    val scope = rememberCoroutineScope()
    var step by remember(flow) { mutableStateOf(flow.step) }
    var text by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var chosen by remember { mutableStateOf<Ficha?>(null) }
    var busy by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (val s = step) {
            NewCampaignFlow.Step.Describe -> {
                OutlinedTextField(
                    text, { text = it }, Modifier.fillMaxWidth(), enabled = !busy,
                    label = { Text("Descreva a campanha (sistema, cenário, tom, dificuldade…)") }
                )
                Button(modifier = Modifier.fillMaxWidth(), enabled = !busy && text.isNotBlank(), onClick = {
                    busy = true
                    scope.launch {
                        // Chamada à IA: fora da thread principal.
                        withContext(Dispatchers.IO) { flow.submitDescription(text) }
                        step = flow.step
                        busy = false
                    }
                }) { Text(if (busy) "Interpretando…" else "Interpretar") }
            }

            is NewCampaignFlow.Step.Asking -> {
                Text(s.ask.question, style = MaterialTheme.typography.bodyLarge)
                s.ask.options.forEach { o ->
                    OutlinedButton(onClick = { flow.answer(o.id); step = flow.step }, modifier = Modifier.fillMaxWidth()) { Text(o.label) }
                }
                OutlinedButton(onClick = { flow.reset(); step = flow.step }) { Text("Recomeçar") }
            }

            is NewCampaignFlow.Step.Review -> {
                val r = s.ready.resolved
                Text("${r.edition.name} — ${r.edition.edition}", style = MaterialTheme.typography.titleSmall)
                s.ready.draft.concept?.let { Text("Personagem pedido: $it", style = MaterialTheme.typography.bodySmall) }
                r.notes.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                if (s.ready.draft.ignored.isNotEmpty()) {
                    Text("Ignorei termos que não existem no catálogo: ${s.ready.draft.ignored.joinToString()}", style = MaterialTheme.typography.bodySmall)
                }
                val fichas = flow.compatible(model.fichas.toList())
                if (fichas.isEmpty()) {
                    Text("Nenhuma ficha desta edição. Crie uma na aba Personagens e volte aqui.", style = MaterialTheme.typography.bodySmall)
                }
                fichas.forEach { f ->
                    FilterChip(selected = chosen?.id == f.id, onClick = { chosen = f }, label = { Text(f.name) })
                }
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Nome da campanha") }, singleLine = true)
                s.errors.forEach { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Button(modifier = Modifier.fillMaxWidth(), enabled = chosen != null && name.isNotBlank(), onClick = {
                    val result = flow.start(chosen!!, name, Random.nextLong())
                    step = result
                    if (result is NewCampaignFlow.Step.Started) model.activate(result.result)
                }) { Text("Começar") }
                OutlinedButton(onClick = { flow.reset(); step = flow.step }) { Text("Cancelar") }
            }

            is NewCampaignFlow.Step.Problem -> {
                s.reasons.forEach { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                OutlinedButton(onClick = { flow.reset(); step = flow.step }) { Text("Tentar de novo") }
            }

            is NewCampaignFlow.Step.Started -> {
                Text("Campanha iniciada. Vá para a aba Jogar.", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { flow.reset(); step = flow.step; text = ""; name = ""; chosen = null }) { Text("Criar outra") }
            }
        }
    }
}
