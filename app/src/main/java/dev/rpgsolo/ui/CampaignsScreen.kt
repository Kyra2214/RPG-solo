package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Aba Campanhas (10.4): capítulos e resumos, missões, diário (só o descoberto). */
@Composable
fun CampaignsScreen(model: AppModel) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var quest by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var backup by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val v = model.campaignVersion
    val chapters = remember(v) { model.chapters.chapters }
    val open = remember(v) { model.chapters.current() }
    val active = remember(v) { model.quests.active() }
    val journal = remember(v, model.controller?.lines?.size) { model.journal() }

    ScreenColumn {
        AppCard(accent = MaterialTheme.colorScheme.primary) { NewCampaignSection(model) }

        SectionTitle("Campanhas salvas")
        model.campaigns.forEach { campaign ->
            AppCard(accent = if (campaign.id == model.activeCampaignId()) MaterialTheme.colorScheme.tertiary else null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(campaign.name, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (campaign.id == model.activeCampaignId()) Pill("aberta", MaterialTheme.colorScheme.tertiary)
                }
                Muted("${campaign.id} · seed ${campaign.seed}")
            }
        }
        OutlinedTextField(newName, { newName = it }, Modifier.fillMaxWidth(), label = { Text("Novo nome da campanha ativa") }, singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), onClick = { message = model.renameCampaign(newName); if (message == null) newName = "" }) { Text("Renomear") }
            Button(modifier = Modifier.weight(1f), onClick = { message = model.duplicateCampaign() }) { Text("Duplicar") }
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { message = model.deleteCurrentCampaign() }) { Text("Apagar campanha ativa") }

        SectionTitle("Backup local")
        Muted("Exporte o texto para um arquivo seguro. A restauração cria uma cópia e não sobrescreve campanhas.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), onClick = { backup = model.exportBackup(); message = null }) { Text("Exportar") }
            Button(modifier = Modifier.weight(1f), enabled = backup.isNotBlank(), onClick = { message = model.importBackup(backup) }) { Text("Restaurar") }
        }
        OutlinedTextField(backup, { backup = it }, Modifier.fillMaxWidth(), label = { Text("Texto do backup") }, minLines = 3)

        SectionTitle("Capítulos")
        if (chapters.isEmpty()) Muted("Nenhum capítulo ainda.")
        chapters.forEach { c ->
            AppCard(accent = if (c.isOpen) MaterialTheme.colorScheme.tertiary else null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${c.number}. ${c.title}", Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (c.isOpen) Pill("em andamento", MaterialTheme.colorScheme.tertiary)
                }
                c.summary?.let { Muted(it) }
            }
        }
        if (open == null) {
            OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Título do novo capítulo") }, singleLine = true)
            Button(modifier = Modifier.fillMaxWidth(), onClick = { message = model.openChapter(title); if (message == null) title = "" }) { Text("Abrir capítulo") }
        } else {
            Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                busy = true
                scope.launch {
                    message = withContext(Dispatchers.IO) { model.closeChapter() }
                    busy = false
                }
            }) { Text(if (busy) "Gerando resumo…" else "Encerrar capítulo com resumo") }
        }
        message?.let { ErrorText(it) }

        SectionTitle("Missões")
        if (active.isEmpty()) Muted("Nenhuma missão ativa.")
        active.forEach { AppCard(accent = MaterialTheme.colorScheme.secondary) { Text(it.title, style = MaterialTheme.typography.bodyLarge) } }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(quest, { quest = it }, Modifier.weight(1f), label = { Text("Nova missão") }, singleLine = true)
            Button(onClick = { message = model.addQuest(quest); if (message == null) quest = "" }) { Text("Adicionar") }
        }

        SectionTitle("Diário", "Só o que você já descobriu.")
        if (journal.isEmpty()) Muted("Você ainda não descobriu nada.")
        journal.forEach { AppCard { Text(it, style = MaterialTheme.typography.bodyMedium) } }
    }
}
