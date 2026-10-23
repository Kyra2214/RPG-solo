package dev.rpgsolo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
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
    var questXp by remember { mutableStateOf("") }
    var questSkill by remember { mutableStateOf("") }
    var questSpell by remember { mutableStateOf("") }
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
                    e.message ?: context.getString(R.string.sources_action_failed)
                } finally {
                    busy = false
                }
            done(result)
        }
    }

    val exportFile =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri == null) {
                backupStatus = context.getString(R.string.backup_export_cancelled)
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
                                        ?: error(context.getString(R.string.error_open_for_write))
                                output.use { it.write(contents.toByteArray(StandardCharsets.UTF_8)) }
                            }
                            context.getString(R.string.backup_saved)
                        }.getOrElse { context.getString(R.string.backup_export_failed, it.message ?: context.getString(R.string.error_write)) }
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
                pdfStatus = context.getString(R.string.pdf_export_cancelled)
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
                                        ?: error(context.getString(R.string.error_open_for_write))
                                output.use { it.write(bytes) }
                            }
                            context.getString(R.string.pdf_saved)
                        }.getOrElse { context.getString(R.string.pdf_save_failed, it.message ?: context.getString(R.string.error_write)) }
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
                }.getOrElse { context.getString(R.string.pdf_share_failed, it.message ?: context.getString(R.string.error_unexpected)) }
            busy = false
        }
    }

    val importFile =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                backupStatus = context.getString(R.string.backup_restore_cancelled)
            } else {
                busy = true
                backupStatus = null
                scope.launch {
                    val importedText =
                        runCatching {
                            withContext(Dispatchers.IO) {
                                val input =
                                    context.contentResolver.openInputStream(uri)
                                        ?: error(context.getString(R.string.error_open_file))
                                input.use { stream ->
                                    val output = ByteArrayOutputStream()
                                    val buffer = ByteArray(8192)
                                    var total = 0
                                    while (true) {
                                        val count = stream.read(buffer)
                                        if (count < 0) break
                                        total += count
                                        require(total <= dev.rpgsolo.engine.Backup.MAX_BACKUP_CHARS) {
                                            context.getString(R.string.backup_too_large)
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
                                        e.message ?: context.getString(R.string.error_unexpected)
                                    }
                                failure?.let { context.getString(R.string.backup_restore_failed, it) } ?: context.getString(R.string.backup_restored)
                            },
                            onFailure = { context.getString(R.string.backup_read_failed, it.message ?: context.getString(R.string.error_read)) },
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
                    stringResource(R.string.campaign_unsaved, failure),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                    io({ model.retrySave() }) { message = it ?: context.getString(R.string.campaign_save_confirmed) }
                }) { Text(stringResource(R.string.campaign_retry_save)) }
            }
        }
        SectionTitle(stringResource(R.string.campaign_saved_title))
        val dateFormat = remember { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR")) }

        fun date(ms: Long) = if (ms <= 0L) "—" else dateFormat.format(java.util.Date(ms))
        model.campaignInfos.forEach { campaign ->
            val isActive = campaign.id == model.activeCampaignId()
            AppCard(accent = if (isActive) MaterialTheme.colorScheme.tertiary else null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(campaign.nome, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (isActive) Pill(stringResource(R.string.campaign_open_pill), MaterialTheme.colorScheme.tertiary)
                    Pill(
                        when (campaign.estado) {
                            dev.rpgsolo.data.EstadoCampanha.ATIVA -> stringResource(R.string.campaign_state_active)
                            dev.rpgsolo.data.EstadoCampanha.PAUSADA -> stringResource(R.string.campaign_state_paused)
                            dev.rpgsolo.data.EstadoCampanha.CONCLUIDA -> stringResource(R.string.campaign_state_done)
                        },
                        MaterialTheme.colorScheme.secondary,
                    )
                }
                Muted(stringResource(R.string.campaign_dates, date(campaign.criadaEm), date(campaign.atualizadaEm), campaign.eventos))
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
                        }) { campaignStatus = it ?: context.getString(R.string.campaign_msg_reopened, campaign.nome) }
                    },
                ) { Text(stringResource(if (isActive) R.string.campaign_is_open else R.string.campaign_continue)) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (campaign.estado) {
                        dev.rpgsolo.data.EstadoCampanha.ATIVA -> {
                            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                                io({
                                    model.setCampaignState(campaign.id, dev.rpgsolo.data.EstadoCampanha.PAUSADA)
                                }) { campaignStatus = it ?: context.getString(R.string.campaign_msg_paused, campaign.nome) }
                            }) { Text(stringResource(R.string.campaign_pause)) }
                            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                                io({
                                    model.setCampaignState(campaign.id, dev.rpgsolo.data.EstadoCampanha.CONCLUIDA)
                                }) { campaignStatus = it ?: context.getString(R.string.campaign_msg_done, campaign.nome) }
                            }) { Text(stringResource(R.string.campaign_finish)) }
                        }
                        else ->
                            Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                                io({
                                    model.setCampaignState(campaign.id, dev.rpgsolo.data.EstadoCampanha.ATIVA)
                                }) { campaignStatus = it ?: context.getString(R.string.campaign_msg_reactivated, campaign.nome) }
                            }) { Text(stringResource(R.string.campaign_reactivate)) }
                    }
                }
            }
        }
        SectionTitle(stringResource(R.string.campaign_mode_title))
        Muted(
            stringResource(R.string.campaign_mode_help),
        )
        listOf(
            dev.rpgsolo.engine.SourceMode.FAITHFUL to (stringResource(R.string.campaign_mode_faithful) to stringResource(R.string.campaign_mode_faithful_desc)),
            dev.rpgsolo.engine.SourceMode.ALTERNATE to (stringResource(R.string.campaign_mode_alternate) to stringResource(R.string.campaign_mode_alternate_desc)),
            dev.rpgsolo.engine.SourceMode.INSPIRED to (stringResource(R.string.campaign_mode_inspired) to stringResource(R.string.campaign_mode_inspired_desc)),
        ).forEach { (mode, labels) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = sourceMode == mode,
                    enabled = !busy,
                    onClick = {
                        if (sourceMode != mode) {
                            io({ model.setSourceMode(mode) }) {
                                campaignStatus = it ?: context.getString(R.string.campaign_mode_registered, labels.first)
                            }
                        }
                    },
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(labels.first, style = MaterialTheme.typography.bodyMedium)
                    Muted(labels.second)
                }
            }
        }
        SectionTitle(stringResource(R.string.source_privacy_title))
        Muted(stringResource(R.string.source_privacy_body))
        val sourceConsent = remember(v) { model.sourcePrivacyConsent() }
        var askConsent by remember { mutableStateOf(false) }
        val switchDesc = stringResource(R.string.source_privacy_switch_desc)
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Switch(
                modifier = Modifier.semantics { contentDescription = switchDesc },
                checked = sourceConsent,
                enabled = !busy,
                onCheckedChange = { if (it) askConsent = true else model.setSourcePrivacyConsent(false) },
            )
            Text(
                stringResource(if (sourceConsent) R.string.source_privacy_on else R.string.source_privacy_off),
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (askConsent) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { askConsent = false },
                title = { Text(stringResource(R.string.source_privacy_dialog_title)) },
                text = { Text(stringResource(R.string.source_privacy_dialog_body)) },
                confirmButton = {
                    TextButton(onClick = {
                        askConsent = false
                        model.setSourcePrivacyConsent(true)
                    }) { Text(stringResource(R.string.source_privacy_allow)) }
                },
                dismissButton = {
                    TextButton(onClick = { askConsent = false }) { Text(stringResource(R.string.source_privacy_cancel)) }
                },
            )
        }
        SourcesSection(model)
        campaignStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }
        OutlinedTextField(
            newName,
            { newName = it },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.campaign_new_name)) },
            singleLine = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                io({ model.renameCampaign(newName) }) {
                    message = it
                    if (it == null) newName = ""
                }
            }) { Text(stringResource(R.string.campaign_rename)) }
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                io({ model.duplicateCampaign() }) { message = it }
            }) { Text(stringResource(R.string.campaign_duplicate)) }
        }
        var confirmDelete by remember { mutableStateOf(false) }
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            confirmDelete = true
        }) { Text(stringResource(R.string.campaign_delete_active)) }
        if (confirmDelete) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { confirmDelete = false },
                title = { Text(stringResource(R.string.campaign_delete_title)) },
                text = {
                    Text(stringResource(R.string.campaign_delete_body))
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        confirmDelete = false
                        io({ model.deleteCurrentCampaign() }) { message = it }
                    }) { Text(stringResource(R.string.campaign_delete_confirm)) }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { confirmDelete = false },
                    ) { Text(stringResource(R.string.campaign_cancel)) }
                },
            )
        }

        SectionTitle(stringResource(R.string.pdf_title))
        Muted(stringResource(R.string.pdf_help))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                pdfComplete = false
                savePdf.launch(PdfShare.fileName(model.adventureBook().title, "livro"))
            }) { Text(stringResource(R.string.pdf_save_book)) }
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                pdfComplete = true
                savePdf.launch(PdfShare.fileName(model.adventureBook().title, "arquivo"))
            }) { Text(stringResource(R.string.pdf_save_full)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                modifier = Modifier.weight(1f),
                enabled = !busy,
                onClick = { sharePdf(false) },
            ) { Text(stringResource(R.string.pdf_share_book)) }
            Button(
                modifier = Modifier.weight(1f),
                enabled = !busy,
                onClick = { sharePdf(true) },
            ) { Text(stringResource(R.string.pdf_share_full)) }
        }
        pdfStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }

        SectionTitle(stringResource(R.string.backup_title))
        Muted(stringResource(R.string.backup_help))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = !busy, onClick = {
                message = null
                busy = true
                scope.launch {
                    backup =
                        try {
                            withContext(Dispatchers.IO) { model.exportBackup() }
                        } catch (e: Exception) {
                            message = context.getString(R.string.backup_generate_failed, e.message ?: context.getString(R.string.error_unexpected))
                            backup
                        }
                    busy = false
                }
            }) { Text(stringResource(R.string.backup_generate_text)) }
            Button(
                modifier = Modifier.weight(1f),
                enabled = !busy,
                onClick = { exportFile.launch("rpg-solo-backup.json") },
            ) { Text(stringResource(R.string.backup_save_file)) }
        }
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            importFile.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
        }) { Text(stringResource(R.string.backup_restore_file)) }
        backupStatus?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(modifier = Modifier.weight(1f), enabled = backup.isNotBlank() && !busy, onClick = {
                io({ model.importBackup(backup) }) { message = it }
            }) { Text(stringResource(R.string.backup_restore_text)) }
        }
        OutlinedTextField(backup, {
            backup = it
        }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.backup_text_label)) }, minLines = 3)

        SectionTitle(stringResource(R.string.chapters_title))
        if (chapters.isEmpty()) Muted(stringResource(R.string.chapters_empty))
        chapters.forEach { c ->
            AppCard(accent = if (c.isOpen) MaterialTheme.colorScheme.tertiary else null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${c.number}. ${c.title}", Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (c.isOpen) Pill(stringResource(R.string.chapter_in_progress), MaterialTheme.colorScheme.tertiary)
                }
                c.summary?.let { Muted(it) }
            }
        }
        if (open == null) {
            OutlinedTextField(
                title,
                { title = it },
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.chapter_new_title)) },
                singleLine = true,
            )
            Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                io({ model.openChapter(title) }) {
                    message = it
                    if (it == null) title = ""
                }
            }) { Text(stringResource(R.string.chapter_open)) }
        } else {
            Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
                busy = true
                scope.launch {
                    message = cancellableIo { model.closeChapter() }
                    busy = false
                }
            }) { Text(stringResource(if (busy) R.string.chapter_summarizing else R.string.chapter_close)) }
        }
        message?.let { ErrorText(it) }

        SectionTitle(stringResource(R.string.relations_title))
        if (relationships.isEmpty()) Muted(stringResource(R.string.relations_empty))
        relationships.forEach { relation ->
            AppCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(relation.npcName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Pill(relation.attitude, MaterialTheme.colorScheme.secondary)
                }
            }
        }

        SectionTitle(stringResource(R.string.progression_title))
        if (progressionHistory.isEmpty()) Muted(stringResource(R.string.progression_empty))
        progressionHistory.forEach { entry -> AppCard { Text(entry, style = MaterialTheme.typography.bodyMedium) } }

        SectionTitle(stringResource(R.string.advancement_title))
        Muted(stringResource(R.string.advancement_available, advancement.pointsAvailable))
        if (advancement.pointsAvailable <= 0) {
            Muted(stringResource(R.string.advancement_none))
        } else if (advancement.choices.isEmpty()) {
            Muted(stringResource(R.string.advancement_no_options))
        } else {
            advancement.choices.forEach { choice ->
                val increaseDesc = stringResource(R.string.advancement_increase_desc, choice.name)
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(choice.name, style = MaterialTheme.typography.bodyMedium)
                            Muted(
                                stringResource(
                                    if (choice.skill) R.string.advancement_choice_skill else R.string.advancement_choice_stat,
                                    choice.current,
                                    choice.max,
                                ),
                            )
                        }
                        Button(
                            modifier =
                                Modifier.semantics {
                                    contentDescription = increaseDesc
                                },
                            enabled = advancement.pointsAvailable > 0 && choice.current < choice.max && !busy,
                            onClick = { io({ model.increaseAdvancement(choice.id, choice.skill) }) { message = it } },
                        ) { Text("+1") }
                    }
                }
            }
        }

        spellSlots?.let { slots ->
            SectionTitle(stringResource(R.string.spell_slots_title))
            Muted(stringResource(R.string.spell_slots_help, slots.used, slots.total))
        }

        if (statPoints.statPoints > 0 || statPoints.abilityPoints > 0) {
            SectionTitle(stringResource(R.string.statpoints_title))
            Muted(stringResource(R.string.statpoints_help, statPoints.statPoints, statPoints.abilityPoints))
            if (statPoints.statPoints > 0) {
                statPoints.choices.forEach { choice ->
                    val statDesc = stringResource(R.string.statpoints_increase_desc, choice.name)
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(choice.name, style = MaterialTheme.typography.bodyMedium)
                                Muted(stringResource(R.string.statpoints_choice, choice.current))
                            }
                            Button(
                                modifier = Modifier.semantics { contentDescription = statDesc },
                                enabled = !busy,
                                onClick = { io({ model.increaseStatPoint(choice.id) }) { message = it } },
                            ) { Text("+1") }
                        }
                    }
                }
            }
            if (statPoints.abilityPoints > 0) Muted(stringResource(R.string.statpoints_ability_note))
        }

        SectionTitle(stringResource(R.string.quests_title))
        if (active.isEmpty()) Muted(stringResource(R.string.quests_empty))
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
                                Text(stringResource(R.string.campaign_finish))
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(enabled = !busy, onClick = {
                        io({ model.finishQuest(currentQuest.id, dev.rpgsolo.engine.QuestStatus.DONE) }) { message = it }
                    }) {
                        Text(stringResource(R.string.quest_finish_done))
                    }
                    TextButton(enabled = !busy, onClick = {
                        io({ model.finishQuest(currentQuest.id, dev.rpgsolo.engine.QuestStatus.FAILED) }) { message = it }
                    }) {
                        Text(stringResource(R.string.quest_finish_failed))
                    }
                }
            }
        }
        val finishedQuests = allQuests.filter { it.status != dev.rpgsolo.engine.QuestStatus.ACTIVE }
        if (finishedQuests.isNotEmpty()) {
            SectionTitle(stringResource(R.string.quests_finished_title))
            finishedQuests.forEach { finished ->
                AppCard {
                    Text(finished.title, style = MaterialTheme.typography.bodyLarge)
                    Pill(
                        stringResource(
                            if (finished.status == dev.rpgsolo.engine.QuestStatus.DONE) R.string.campaign_state_done else R.string.quest_failed_pill,
                        ),
                        if (finished.status == dev.rpgsolo.engine.QuestStatus.DONE) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                    )
                    finished.objectives.forEach { objective -> Muted((if (objective.done) "✓ " else "○ ") + objective.text) }
                }
            }
        }
        OutlinedTextField(
            quest,
            { quest = it },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.quest_new)) },
            singleLine = true,
        )
        OutlinedTextField(
            questObjective,
            { questObjective = it },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.quest_objective)) },
            singleLine = true,
        )
        OutlinedTextField(
            questXp,
            { questXp = it.filter(Char::isDigit).take(6) },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.quest_xp)) },
            supportingText = { Text(stringResource(R.string.quest_xp_help)) },
            keyboardOptions =
                androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                ),
            singleLine = true,
        )
        OutlinedTextField(
            questSkill,
            { questSkill = it.trim().take(40) },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.quest_skill)) },
            supportingText = { Text(stringResource(R.string.quest_skill_help)) },
            singleLine = true,
        )
        OutlinedTextField(
            questSpell,
            { questSpell = it.trim().take(40) },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.quest_spell)) },
            supportingText = { Text(stringResource(R.string.quest_spell_help)) },
            singleLine = true,
        )
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            val q = quest
            val o = questObjective
            val xp = questXp.toIntOrNull() ?: 0
            val skill = questSkill
            val spell = questSpell
            io({ model.addQuest(q, listOf(o), xp, skill, spell) }) {
                message = it
                if (it == null) {
                    quest = ""
                    questObjective = ""
                    questXp = ""
                    questSkill = ""
                    questSpell = ""
                }
            }
        }) { Text(stringResource(R.string.quest_add)) }

        SectionTitle(stringResource(R.string.timeline_title))
        if (timeline.isEmpty()) Muted(stringResource(R.string.timeline_empty))
        timeline.forEach { (chapterNumber, text) ->
            AppCard {
                Muted(stringResource(R.string.timeline_chapter, chapterNumber))
                Text(text)
            }
        }
        OutlinedTextField(
            timelineNote,
            { timelineNote = it },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.timeline_note)) },
            minLines = 2,
            maxLines = 4,
        )
        Button(modifier = Modifier.fillMaxWidth(), enabled = !busy, onClick = {
            val note = timelineNote
            io({ model.addTimelineNote(note) }) {
                message = it
                if (it == null) timelineNote = ""
            }
        }) { Text(stringResource(R.string.timeline_register)) }

        SectionTitle(stringResource(R.string.auto_timeline_title))
        if (automaticTimeline.isEmpty()) {
            Muted(stringResource(R.string.auto_timeline_empty))
        }
        automaticTimeline.forEach { entry ->
            AppCard {
                Muted(stringResource(R.string.auto_timeline_entry, entry.eventIndex + 1, entry.category.toString()))
                Text(entry.text, style = MaterialTheme.typography.bodyMedium)
            }
        }

        SectionTitle(stringResource(R.string.journal_title), stringResource(R.string.journal_subtitle))
        if (journal.isEmpty()) Muted(stringResource(R.string.journal_empty))
        journal.forEach { AppCard { Text(it, style = MaterialTheme.typography.bodyMedium) } }
    }
}
