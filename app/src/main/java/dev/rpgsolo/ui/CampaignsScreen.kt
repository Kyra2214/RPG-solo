package dev.rpgsolo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

/** Aba Campanhas (10.4): capítulos e resumos, missões, diário (só o descoberto). */
@Composable
fun CampaignsScreen(model: AppModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var quest by remember { mutableStateOf("") }
    var questObjective by remember { mutableStateOf("") }
    var timelineNote by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var backup by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var backupStatus by remember { mutableStateOf<String?>(null) }
    var campaignStatus by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    /** Ações que gravam no banco/sessão rodam em IO, com exclusividade de turno, e desabilitam os botões enquanto duram (R-02). */
    fun io(
        work: () -> String?,
        done: (String?) -> Unit,
    ) {
        if (busy) return
        busy = true
        scope.launch {
            val result =
                try {
                    withContext(Dispatchers.IO) { model.exclusiveTurn(work) }
                } catch (e: Exception) {
                    e.message ?: "A ação falhou inesperadamente."
                } finally {
                    busy = false
                }
            done(result)
        }
    }

    val exportFile =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri == null) {
                backupStatus = "Exportação cancelada."
            } else {
                busy = true
                backupStatus = null
                scope.launch {
                    backupStatus =
                        runCatching {
                            withContext(Dispatchers.IO) {
                                val contents = model.exportBackup()
                                val output =
                                    context.contentResolver.openOutputStream(uri)
                                        ?: error("não foi possível abrir o arquivo para gravação")
                                output.use { it.write(contents.toByteArray(StandardCharsets.UTF_8)) }
                            }
                            "Backup salvo no arquivo escolhido."
                        }.getOrElse { "Falha ao exportar: ${it.message ?: "erro de gravação"}" }
                    busy = false
                }
            }
        }

    // CP-04.3: PDF do livro (false) ou do arquivo completo (true); gerado só depois de o jogador escolher onde salvar.
    var pdfComplete by remember { mutableStateOf(false) }
    var pdfStatus by remember { mutableStateOf<String?>(null) }
    val savePdf =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
            if (uri == null) {
                pdfStatus = "Exportação do PDF cancelada."
            } else {
                busy = true
                pdfStatus = null
                val complete = pdfComplete
                scope.launch {
                    pdfStatus =
                        runCatching {
                            withContext(Dispatchers.IO) {
                                val bytes = if (complete) model.campaignArchivePdf() else model.adventureBookPdf()
                                val output =
                                    context.contentResolver.openOutputStream(uri)
                                        ?: error("não foi possível abrir o arquivo para gravação")
                                output.use { it.write(bytes) }
                            }
                            "PDF salvo no arquivo escolhido."
                        }.getOrElse { "Falha ao salvar o PDF: ${it.message ?: "erro de gravação"}" }
                    busy = false
                }
            }
        }

    fun sharePdf(complete: Boolean) {
        busy = true
        pdfStatus = null
        scope.launch {
            pdfStatus =
                runCatching {
                    val (bytes, title) =
                        withContext(Dispatchers.IO) {
                            (if (complete) model.campaignArchivePdf() else model.adventureBookPdf()) to model.adventureBook().title
                        }
                    val name = PdfShare.fileName(title, if (complete) "arquivo" else "livro")
                    context.startActivity(PdfShare.chooser(context, bytes, name, title))
                    null
                }.getOrElse { "Falha ao compartilhar o PDF: ${it.message ?: "erro inesperado"}" }
            busy = false
        }
    }

    val importFile =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                backupStatus = "Restauração cancelada."
            } else {
                busy = true
                backupStatus = null
                scope.launch {
                    val importedText =
                        runCatching {
                            withContext(Dispatchers.IO) {
                                val input =
                                    context.contentResolver.openInputStream(uri)
                                        ?: error("não foi possível abrir o arquivo")
                                input.use { stream ->
                                    val output = ByteArrayOutputStream()
                                    val buffer = ByteArray(8192)
                                    var total = 0
                                    while (true) {
                                        val count = stream.read(buffer)
                                        if (count < 0) break
                                        total += count
                                        require(total <= dev.rpgsolo.engine.Backup.MAX_BACKUP_CHARS) {
                                            "backup excede o limite de 50 MiB; arquivo recusado durante a leitura"
                                        }
                                        output.write(buffer, 0, count)
                                    }
                                    String(output.toByteArray(), StandardCharsets.UTF_8)
                                }
                            }
                        }
                    backupStatus =
                        importedText.fold(
                            onSuccess = { contents ->
                                val failure =
                                    try {
                                        withContext(Dispatchers.IO) { model.exclusiveTurn { model.importBackup(contents) } }
                                    } catch (e: Exception) {
                                        e.message ?: "erro inesperado"
                                    }
                                failure?.let { "Falha ao restaurar: $it" } ?: "Backup restaurado como uma nova campanha."
                            },
                            onFailure = { "Falha ao ler o arquivo: ${it.message ?: "erro de leitura"}" },
                        )
                    busy = false
                }
            }
        }

    val v = model.campaignVersion
    val sourceMode = model.sourceMode()
    val chapters = remember(v) { model.chapters.chapters }
    val open = remember(v) { model.chapters.current() }
    val active = remember(v) { model.quests.active() }
    val allQuests = remember(v) { model.quests.all() }
    val timeline = remember(v) { model.quests.timeline() }
    val automaticTimeline = remember(v, model.controller?.lines?.size) { model.controller?.automaticTimeline().orEmpty() }
    val journal = remember(v, model.controller?.lines?.size) { model.journal() }
    val progressionHistory = remember(v) { model.progressionHistory() }
    val advancement = remember(v) { model.advancementState() }
    val statPoints = remember(v) { model.statPointsState() }
    val spellSlots = remember(v) { model.spellSlotsView() }
    val relationships = remember(v) { model.relationshipsView() }

    ScreenColumn {
        AppCard(accent = MaterialTheme.colorScheme.primary) { NewCampaignSection(model) }

        model.saveError?.let { failure ->
            AppCard(accent = MaterialTheme.colorScheme.error) {
                Text(
                    "Alterações ainda não gravadas: $failure",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                    io({ model.retrySave() }) { message = it ?: "Gravação confirmada." }
                }) { Text("Tentar gravar de novo") }
            }
        }
        SectionTitle("Campanhas salvas")
        val dateFormat = remember { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR")) }

        fun date(ms: Long) = if (ms <= 0L) "—" else dateFormat.format(java.util.Date(ms))
        model.campaignInfos.forEach { campaign ->
            val isActive = campaign.id == model.activeCampaignId()
            AppCard(accent = if (isActive) MaterialTheme.colorScheme.tertiary else null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(campaign.nome, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (isActive) Pill("aberta", MaterialTheme.colorScheme.tertiary)
                    Pill(
                        when (campaign.estado) {
                            dev.rpgsolo.data.EstadoCampanha.ATIVA -> "em andamento"
                            dev.rpgsolo.data.EstadoCampanha.PAUSADA -> "pausada"
                            dev.rpgsolo.data.EstadoCampanha.CONCLUIDA -> "concluída"
                        },
                        MaterialTheme.colorScheme.secondary,
                    )
                }
                Muted("Criada em ${date(campaign.criadaEm)} · última jogada ${date(campaign.atualizadaEm)} · ${campaign.eventos} eventos")
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isActive && !busy && campaign.estado != dev.rpgsolo.data.EstadoCampanha.CONCLUIDA,
                    onClick = {
                        io({
                            model.activateCampaign(campaign.id)
                                ?: if (campaign.estado == dev.rpgsolo.data.EstadoCampanha.PAUSADA) {
                                    model.setCampaignState(campaign.id, dev.rpgsolo.data.EstadoCampanha.ATIVA)
                                } else {
                                    null
                                }
                        }) { campaignStatus = it ?: "Campanha ${campaign.nome} reaberta." }
                    },
                ) { Text(if (isActive) "Campanha aberta" else "Continuar campanha") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (campaign.estado) {
                        dev.rpgsolo.data.EstadoCampanha.ATIVA -> {
                            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                                io({
                                    model.setCampaignState(campaign.id, dev.rpgsolo.data.EstadoCampanha.PAUSADA)
                                }) { campaignStatus = it ?: "Campanha ${campaign.nome} pausada." }
                            }) { Text("Pausar") }
                            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                                io({
                                    model.setCampaignState(campaign.id, dev.rpgsolo.data.EstadoCampanha.CONCLUIDA)
                                }) { campaignStatus = it ?: "Campanha ${campaign.nome} concluída." }
                            }) { Text("Concluir") }
                        }
                        else ->
                            Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                                io({
                                    model.setCampaignState(campaign.id, dev.rpgsolo.data.EstadoCampanha.ATIVA)
                                }) { campaignStatus = it ?: "Campanha ${campaign.nome} reativada." }
                            }) { Text("Reativar") }
                    }
                }
            }
        }
        SectionTitle("Modo de fidelidade das fontes")
        Muted("Define como a campanha usa documentos-fonte quando eles estiverem vinculados. A escolha fica registrada no histórico da campanha.")
        listOf(
            dev.rpgsolo.engine.SourceMode.FAITHFUL to ("História fiel" to "Preserve os fatos centrais do documento."),
            dev.rpgsolo.engine.SourceMode.ALTERNATE to ("História alternativa" to "Permita divergências causadas pelas ações do jogador."),
            dev.rpgsolo.engine.SourceMode.INSPIRED to ("Universo inspirado" to "Use o documento como inspiração, sem exigir fidelidade ao enredo.")
        ).forEach { (mode, labels) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = sourceMode == mode,
                    enabled = !busy,
                    onClick = {
                        if (sourceMode != mode) {
                            io({ model.setSourceMode(mode) }) {
                                campaignStatus = it ?: "Modo ${labels.first} registrado na campanha."
                            }
                        }
                    }
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(labels.first, style = MaterialTheme.typography.bodyMedium)
                    Muted(labels.second)
                }
            }
        }
        campaignStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }
        OutlinedTextField(
            newName,
            { newName = it },
            Modifier.fillMaxWidth(),
            label = { Text("Novo nome da campanha ativa") },
            singleLine = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                io({ model.renameCampaign(newName) }) {
                    message = it
                    if (it == null) newName = ""
                }
            }) { Text("Renomear") }
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                io({ model.duplicateCampaign() }) { message = it }
            }) { Text("Duplicar") }
        }
        var confirmDelete by remember { mutableStateOf(false) }
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = { confirmDelete = true }) { Text("Apagar campanha ativa") }
        if (confirmDelete) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { confirmDelete = false },
                title = { Text("Apagar campanha?") },
                text = {
                    Text(
                        "A campanha aberta, com todo o histórico, capítulos, missões e linha do tempo, será apagada e não poderá ser recuperada. Faça um backup antes se quiser guardá-la.",
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        confirmDelete = false
                        io({ model.deleteCurrentCampaign() }) { message = it }
                    }) { Text("Apagar") }
                },
                dismissButton = { androidx.compose.material3.TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } },
            )
        }

        SectionTitle("Exportar em PDF")
        Muted(
            "O livro traz capítulos, missões e diário. O arquivo completo soma ficha, inventário, descobertas, lugares e relações. Nenhum dos dois traz segredos do mestre nem serve como backup; para restaurar use o backup abaixo.",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                pdfComplete = false
                savePdf.launch(PdfShare.fileName(model.adventureBook().title, "livro"))
            }) { Text("Salvar livro") }
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                pdfComplete = true
                savePdf.launch(PdfShare.fileName(model.adventureBook().title, "arquivo"))
            }) { Text("Salvar arquivo completo") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = { sharePdf(false) }) { Text("Compartilhar livro") }
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = { sharePdf(true) }) { Text("Compartilhar arquivo") }
        }
        pdfStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }

        SectionTitle("Backup local")
        Muted(
            "Inclui log de eventos, configuração do motor, capítulos, missões, linha do tempo e fichas salvas. A chave de API e preferências secretas nunca entram no arquivo. Restaurar cria uma nova campanha.",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                message = null
                busy = true
                scope.launch {
                    backup =
                        try {
                            withContext(Dispatchers.IO) { model.exportBackup() }
                        } catch (e: Exception) {
                            message = "Falha ao gerar o backup: ${e.message ?: "erro inesperado"}"
                            backup
                        }
                    busy = false
                }
            }) { Text("Gerar texto") }
            Button(
                modifier = Modifier.weight(1f),
                enabled = !busy,
                onClick = { exportFile.launch("rpg-solo-backup.json") },
            ) { Text("Salvar arquivo") }
        }
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            importFile.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
        }) { Text("Restaurar de arquivo") }
        backupStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = backup.isNotBlank() && !busy, onClick = {
                io({ model.importBackup(backup) }) { message = it }
            }) { Text("Restaurar texto") }
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
            OutlinedTextField(
                title,
                { title = it },
                Modifier.fillMaxWidth(),
                label = { Text("Título do novo capítulo") },
                singleLine = true,
            )
            Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                io({ model.openChapter(title) }) {
                    message = it
                    if (it == null) title = ""
                }
            }) { Text("Abrir capítulo") }
        } else {
            Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                busy = true
                scope.launch {
                    message = cancellableIo { model.closeChapter() }
                    busy = false
                }
            }) { Text(if (busy) "Gerando resumo…" else "Encerrar capítulo com resumo") }
        }
        message?.let { ErrorText(it) }

        SectionTitle("Relações")
        if (relationships.isEmpty()) Muted("As atitudes dos NPCs ainda não foram registradas.")
        relationships.forEach { relation ->
            AppCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(relation.npcName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Pill(relation.attitude, MaterialTheme.colorScheme.secondary)
                }
            }
        }

        SectionTitle("Evolução do personagem")
        if (progressionHistory.isEmpty()) Muted("Nenhum ganho de XP ou nível registrado ainda.")
        progressionHistory.forEach { entry -> AppCard { Text(entry, style = MaterialTheme.typography.bodyMedium) } }

        SectionTitle("Pontos de evolução")
        Muted("Disponíveis: ${advancement.pointsAvailable}. Cada aumento custa 1 ponto e respeita os limites do pacote ativo.")
        if (advancement.pointsAvailable <= 0) {
            Muted("Você não tem pontos de evolução para gastar.")
        } else if (advancement.choices.isEmpty()) {
            Muted("O pacote ativo não define opções de evolução para esta ficha.")
        } else {
            advancement.choices.forEach { choice ->
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(choice.name, style = MaterialTheme.typography.bodyMedium)
                            Muted("${choice.current}/${choice.max}" + if (choice.skill) " · perícia" else " · atributo")
                        }
                        Button(
                            modifier =
                                Modifier.semantics {
                                    contentDescription = "Aumentar ${choice.name} em 1 ponto"
                                },
                            enabled = advancement.pointsAvailable > 0 && choice.current < choice.max && !busy,
                            onClick = { io({ model.increaseAdvancement(choice.id, choice.skill) }) { message = it } },
                        ) { Text("+1") }
                    }
                }
            }
        }

        spellSlots?.let { slots ->
            SectionTitle("Slots de magia")
            Muted(
                "Usados: ${slots.used} de ${slots.total}. Um novo slot surge em cada nível-chave do pacote; magias novas entram só por missão ou compra.",
            )
        }

        if (statPoints.statPoints > 0 || statPoints.abilityPoints > 0) {
            SectionTitle("Pontos de status e habilidade")
            Muted(
                "Status: ${statPoints.statPoints} · Habilidade: ${statPoints.abilityPoints}. Cada aumento de status custa 1 ponto e não tem teto.",
            )
            if (statPoints.statPoints > 0) {
                statPoints.choices.forEach { choice ->
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(choice.name, style = MaterialTheme.typography.bodyMedium)
                                Muted("${choice.current} · status")
                            }
                            Button(
                                modifier = Modifier.semantics { contentDescription = "Aumentar ${choice.name} em 1 ponto de status" },
                                enabled = !busy,
                                onClick = { io({ model.increaseStatPoint(choice.id) }) { message = it } },
                            ) { Text("+1") }
                        }
                    }
                }
            }
            if (statPoints.abilityPoints > 0) Muted("Pontos de habilidade ficam guardados até as habilidades existirem (MU-06).")
        }

        SectionTitle("Missões")
        if (active.isEmpty()) Muted("Nenhuma missão ativa.")
        active.forEach { currentQuest ->
            AppCard(accent = MaterialTheme.colorScheme.secondary) {
                Text(currentQuest.title, style = MaterialTheme.typography.bodyLarge)
                currentQuest.objectives.forEachIndexed { index, objective ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            (if (objective.done) "✓ " else "○ ") + objective.text,
                            modifier = Modifier.weight(1f),
                            color = if (objective.done) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                        )
                        if (!objective.done) {
                            TextButton(
                                enabled = !busy,
                                onClick = { io({ model.completeQuestObjective(currentQuest.id, index) }) { message = it } },
                            ) {
                                Text("Concluir")
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(enabled = !busy, onClick = {
                        io({ model.finishQuest(currentQuest.id, dev.rpgsolo.engine.QuestStatus.DONE) }) { message = it }
                    }) {
                        Text("Encerrar como concluída")
                    }
                    TextButton(enabled = !busy, onClick = {
                        io({ model.finishQuest(currentQuest.id, dev.rpgsolo.engine.QuestStatus.FAILED) }) { message = it }
                    }) {
                        Text("Marcar como falhou")
                    }
                }
            }
        }
        val finishedQuests = allQuests.filter { it.status != dev.rpgsolo.engine.QuestStatus.ACTIVE }
        if (finishedQuests.isNotEmpty()) {
            SectionTitle("Missões encerradas")
            finishedQuests.forEach { finished ->
                AppCard {
                    Text(finished.title, style = MaterialTheme.typography.bodyLarge)
                    Pill(
                        if (finished.status == dev.rpgsolo.engine.QuestStatus.DONE) "concluída" else "falhou",
                        if (finished.status == dev.rpgsolo.engine.QuestStatus.DONE) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                    )
                    finished.objectives.forEach { objective -> Muted((if (objective.done) "✓ " else "○ ") + objective.text) }
                }
            }
        }
        OutlinedTextField(quest, { quest = it }, Modifier.fillMaxWidth(), label = { Text("Nova missão") }, singleLine = true)
        OutlinedTextField(
            questObjective,
            { questObjective = it },
            Modifier.fillMaxWidth(),
            label = { Text("Objetivo inicial (opcional)") },
            singleLine = true,
        )
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            val q = quest
            val o = questObjective
            io({ model.addQuest(q, listOf(o)) }) {
                message = it
                if (it == null) {
                    quest = ""
                    questObjective = ""
                }
            }
        }) { Text("Adicionar missão") }

        SectionTitle("Linha do tempo")
        if (timeline.isEmpty()) Muted("Nenhum acontecimento importante registrado.")
        timeline.forEach { (chapterNumber, text) ->
            AppCard {
                Muted("Capítulo $chapterNumber")
                Text(text)
            }
        }
        OutlinedTextField(
            timelineNote,
            { timelineNote = it },
            Modifier.fillMaxWidth(),
            label = { Text("Acontecimento importante") },
            minLines = 2,
            maxLines = 4,
        )
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            val note = timelineNote
            io({ model.addTimelineNote(note) }) {
                message = it
                if (it == null) timelineNote = ""
            }
        }) { Text("Registrar acontecimento") }

        SectionTitle("Acontecimentos automáticos")
        if (automaticTimeline.isEmpty()) {
            Muted("Os acontecimentos compatíveis aparecerão aqui conforme forem registrados no log.")
        }
        automaticTimeline.forEach { entry ->
            AppCard {
                Muted("Registro ${entry.eventIndex + 1} · ${entry.category}")
                Text(entry.text, style = MaterialTheme.typography.bodyMedium)
            }
        }

        SectionTitle("Diário", "Só o que você já descobriu.")
        if (journal.isEmpty()) Muted("Você ainda não descobriu nada.")
        journal.forEach { AppCard { Text(it, style = MaterialTheme.typography.bodyMedium) } }
    }
}
