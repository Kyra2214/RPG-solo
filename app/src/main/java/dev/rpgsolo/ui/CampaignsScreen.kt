package dev.rpgsolo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

/** Aba Campanhas (10.4): capítulos e resumos, missões, diário (só o descoberto). */
@Composable
fun CampaignsScreen(model: AppModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var quest by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var backup by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var backupStatus by remember { mutableStateOf<String?>(null) }
    var campaignStatus by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    val exportFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) {
            backupStatus = "Exportação cancelada."
        } else {
            busy = true
            backupStatus = null
            scope.launch {
                backupStatus = runCatching {
                    withContext(Dispatchers.IO) {
                        val contents = model.exportBackup()
                        val output = context.contentResolver.openOutputStream(uri)
                            ?: error("não foi possível abrir o arquivo para gravação")
                        output.use { it.write(contents.toByteArray(StandardCharsets.UTF_8)) }
                    }
                    "Backup salvo no arquivo escolhido."
                }.getOrElse { "Falha ao exportar: ${it.message ?: "erro de gravação"}" }
                busy = false
            }
        }
    }

    val importFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            backupStatus = "Restauração cancelada."
        } else {
            busy = true
            backupStatus = null
            scope.launch {
                val importedText = runCatching {
                    withContext(Dispatchers.IO) {
                        val input = context.contentResolver.openInputStream(uri)
                            ?: error("não foi possível abrir o arquivo")
                        input.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
                    }
                }
                backupStatus = importedText.fold(
                    onSuccess = { contents ->
                        model.importBackup(contents)?.let { "Falha ao restaurar: $it" }
                            ?: "Backup restaurado como uma nova campanha."
                    },
                    onFailure = { "Falha ao ler o arquivo: ${it.message ?: "erro de leitura"}" }
                )
                busy = false
            }
        }
    }

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
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = campaign.id != model.activeCampaignId(),
                    onClick = {
                        campaignStatus = model.activateCampaign(campaign.id)
                            ?: "Campanha ${campaign.name} reaberta."
                    }
                ) { Text(if (campaign.id == model.activeCampaignId()) "Campanha aberta" else "Continuar campanha") }
            }
        }
        campaignStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }
        OutlinedTextField(newName, { newName = it }, Modifier.fillMaxWidth(), label = { Text("Novo nome da campanha ativa") }, singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), onClick = { message = model.renameCampaign(newName); if (message == null) newName = "" }) { Text("Renomear") }
            Button(modifier = Modifier.weight(1f), onClick = { message = model.duplicateCampaign() }) { Text("Duplicar") }
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { message = model.deleteCurrentCampaign() }) { Text("Apagar campanha ativa") }

        SectionTitle("Backup local")
        Muted("Inclui log de eventos, configuração do motor, capítulos, missões, linha do tempo e fichas salvas. A chave de API e preferências secretas nunca entram no arquivo. Restaurar cria uma nova campanha.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = { backup = model.exportBackup(); message = null }) { Text("Gerar texto") }
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = { exportFile.launch("rpg-solo-backup.json") }) { Text("Salvar arquivo") }
        }
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            importFile.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
        }) { Text("Restaurar de arquivo") }
        backupStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = backup.isNotBlank() && !busy, onClick = { message = model.importBackup(backup) }) { Text("Restaurar texto") }
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
