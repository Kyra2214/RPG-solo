package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rpgsolo.ai.Author
import dev.rpgsolo.ai.PlayController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Aba Jogar (10.1): chat com o mestre, rolagens do motor e painel rápido do personagem. */
@Composable
fun PlayScreen(model: AppModel) {
    val c = model.controller
    if (c == null) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text("Configure o provedor de IA na aba Config para começar a jogar.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    PlayContent(c, model.classifierWarning)
}

@Composable
private fun PlayContent(c: PlayController, classifierWarning: String?) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var version by remember { mutableIntStateOf(0) }
    val lines = remember(version) { c.lines }
    val panel = remember(version) { c.panel() }
    val suggestions = remember(version) { c.suggestions() }
    val listState = rememberLazyListState()

    LaunchedEffect(lines.size) { if (lines.isNotEmpty()) listState.animateScrollToItem(lines.lastIndex) }

    // A chamada à IA é de rede: sempre fora da thread principal.
    fun run(block: () -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            withContext(Dispatchers.IO) { block() }
            busy = false
            version++
        }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        panel?.let { p ->
            Column(Modifier.fillMaxWidth()) {
                Text(p.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    p.resources.entries.joinToString("   ") { "${it.key} ${it.value.first}/${it.value.second}" } +
                        (if (p.conditions.isEmpty()) "" else "   •   ${p.conditions.joinToString(", ")}") +
                        (if (p.inCombat) "   •   em combate" else ""),
                    style = MaterialTheme.typography.bodyMedium
                )
                p.quest?.let { Text("Missão: $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp), state = listState, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(lines) { line ->
                when (line.author) {
                    Author.PLAYER -> Text("Você: ${line.text}", style = MaterialTheme.typography.bodyLarge)
                    Author.ROLL -> Text("🎲 ${line.text}", style = MaterialTheme.typography.labelLarge)
                    Author.MASTER -> Text(line.text, style = MaterialTheme.typography.bodyLarge)
                    Author.SYSTEM -> Text(line.text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
        classifierWarning?.let {
            Text("Não consegui decidir se a ação exige teste ($it). Seguindo sem teste.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
        if (busy) Text("O mestre está pensando…", style = MaterialTheme.typography.bodySmall)
        // 10.1.2: sugestões opcionais. Tocar só preenche o campo; o jogador pode editar ou ignorar.
        if (suggestions.isNotEmpty() && !busy) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                suggestions.forEach { sug -> AssistChip(onClick = { text = sug }, label = { Text(sug) }) }
            }
        }
        OutlinedTextField(
            value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth(),
            label = { Text("O que você faz?") }, enabled = !busy
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(enabled = !busy && text.isNotBlank(), onClick = {
                val t = text
                text = ""
                run { c.submit(t) }
            }) { Text("Enviar") }
            OutlinedButton(enabled = !busy, onClick = { run { c.undo() } }) { Text("Desfazer") }
            OutlinedButton(enabled = !busy, onClick = { run { c.regenerate() } }) { Text("Narrar de novo") }
        }
    }
}
