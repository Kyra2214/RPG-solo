package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Aba Campanhas (10.4): capítulos e resumos, missões, diário (só o descoberto). */
@Composable
fun CampaignsScreen(model: AppModel) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var quest by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val v = model.campaignVersion
    val chapters = remember(v) { model.chapters.chapters }
    val open = remember(v) { model.chapters.current() }
    val active = remember(v) { model.quests.active() }
    val journal = remember(v, model.controller?.lines?.size) { model.journal() }

    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NewCampaignSection(model)

        Text("Capítulos", style = MaterialTheme.typography.titleMedium)
        if (chapters.isEmpty()) Text("Nenhum capítulo ainda.", style = MaterialTheme.typography.bodySmall)
        chapters.forEach { c ->
            Text("${c.number}. ${c.title}${if (c.isOpen) " (em andamento)" else ""}", style = MaterialTheme.typography.titleSmall)
            c.summary?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
        if (open == null) {
            OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Título do novo capítulo") }, singleLine = true)
            Button(onClick = { message = model.openChapter(title); if (message == null) title = "" }) { Text("Abrir capítulo") }
        } else {
            Button(enabled = !busy, onClick = {
                busy = true
                scope.launch {
                    message = withContext(Dispatchers.IO) { model.closeChapter() }
                    busy = false
                }
            }) { Text(if (busy) "Gerando resumo…" else "Encerrar capítulo com resumo") }
        }
        message?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

        Text("Missões", style = MaterialTheme.typography.titleMedium)
        if (active.isEmpty()) Text("Nenhuma missão ativa.", style = MaterialTheme.typography.bodySmall)
        active.forEach { Text("• ${it.title}", style = MaterialTheme.typography.bodyMedium) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(quest, { quest = it }, Modifier.weight(1f), label = { Text("Nova missão") }, singleLine = true)
            Button(onClick = { message = model.addQuest(quest); if (message == null) quest = "" }) { Text("Adicionar") }
        }

        Text("Diário", style = MaterialTheme.typography.titleMedium)
        if (journal.isEmpty()) Text("Você ainda não descobriu nada.", style = MaterialTheme.typography.bodySmall)
        journal.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
    }
}
