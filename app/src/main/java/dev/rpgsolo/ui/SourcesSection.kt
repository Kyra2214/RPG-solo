package dev.rpgsolo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import dev.rpgsolo.engine.FactStatus
import dev.rpgsolo.engine.PipelineStage
import dev.rpgsolo.engine.SourceImporter
import dev.rpgsolo.engine.SourceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/** Fase 14: importação, biblioteca, progresso da indexação e fatos canônicos da campanha ativa. */
@Composable
fun SourcesSection(model: AppModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    var newFact by remember { mutableStateOf("") }
    var askConsent by remember { mutableStateOf(false) }
    val libraryVersion = model.sourceLibraryVersion
    val factsVersion = model.canonFactsVersion
    val campaignVersion = model.campaignVersion
    val campaignId = remember(campaignVersion) { model.activeCampaignId() }
    val library = remember(libraryVersion, campaignVersion) { runCatching { model.sourceLibrary() }.getOrNull() }
    val states =
        remember(libraryVersion) { runCatching { model.sourcePipelineStates().associateBy { it.sourceId } }.getOrDefault(emptyMap()) }
    val facts =
        remember(
            factsVersion,
            campaignVersion,
        ) { runCatching { model.canonFacts(campaignId).forCampaign(campaignId) }.getOrDefault(emptyList()) }

    fun run(
        work: () -> String?,
        done: (String?) -> Unit = { status = it },
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

    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                status = context.getString(R.string.sources_import_cancelled)
            } else {
                busy = true
                status = context.getString(R.string.sources_importing)
                scope.launch {
                    val outcome =
                        try {
                            withContext(Dispatchers.IO) {
                                val name =
                                    context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
                                        ?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
                                        ?: uri.lastPathSegment.orEmpty()
                                val bytes =
                                    context.contentResolver.openInputStream(uri)?.use { stream ->
                                        val out = ByteArrayOutputStream()
                                        val buffer = ByteArray(8192)
                                        var total = 0
                                        while (true) {
                                            val n = stream.read(buffer)
                                            if (n < 0) break
                                            total += n
                                            require(total <= SourceImporter.MAX_BYTES) { "arquivo acima de 5 MiB" }
                                            out.write(buffer, 0, n)
                                        }
                                        out.toByteArray()
                                    } ?: error("não foi possível abrir o arquivo")
                                model.exclusiveTurn { model.importAndIndexSource(name, bytes) }
                            }
                        } catch (e: Exception) {
                            e.message ?: "A importação falhou inesperadamente."
                        } finally {
                            busy = false
                        }
                    status = outcome ?: context.getString(R.string.sources_imported)
                }
            }
        }

    SectionTitle(
        stringResource(R.string.sources_title),
        stringResource(R.string.sources_help),
    )

    fun launchPicker() =
        picker.launch(
            arrayOf(
                "text/plain",
                "text/markdown",
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/octet-stream",
            ),
        )
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !busy,
        onClick = { if (model.sourcePrivacyConsent(campaignId)) launchPicker() else askConsent = true },
    ) { Text(stringResource(R.string.sources_import)) }
    if (askConsent) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { askConsent = false },
            title = { Text(stringResource(R.string.source_privacy_dialog_title)) },
            text = {
                Text(
                    stringResource(R.string.source_privacy_dialog_body) + "\n\n" + stringResource(R.string.source_privacy_import_note),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    askConsent = false
                    model.setSourcePrivacyConsent(true, campaignId)
                    launchPicker()
                }) { Text(stringResource(R.string.source_privacy_allow_import)) }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        askConsent = false
                        launchPicker()
                    }) { Text(stringResource(R.string.source_privacy_import_only)) }
                    TextButton(onClick = { askConsent = false }) { Text(stringResource(R.string.source_privacy_cancel)) }
                }
            },
        )
    }
    status?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall) }

    val visible = library?.entries?.filter { it.status != SourceStatus.REMOVED }.orEmpty()
    if (visible.isEmpty()) Muted(stringResource(R.string.sources_empty))
    visible.forEach { entry ->
        val linked = library?.forCampaign(campaignId)?.any { it.sourceId == entry.id } == true
        val state = states[entry.id]
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(entry.title, style = MaterialTheme.typography.bodyMedium)
            Muted(stringResource(R.string.sources_file_info, entry.fileName, entry.format ?: "?", entry.byteSize / 1024))
            when {
                entry.status == SourceStatus.FAILED ->
                    ErrorText(
                        stringResource(R.string.sources_import_failed, entry.error ?: stringResource(R.string.sources_unknown_reason)),
                    )
                state == null -> Muted(stringResource(R.string.sources_index_pending))
                state.stage == PipelineStage.FAILED ->
                    ErrorText(
                        stringResource(R.string.sources_index_failed, state.error ?: stringResource(R.string.sources_unknown_reason)),
                    )
                state.stage == PipelineStage.INDEXED -> Muted(stringResource(R.string.sources_indexed, state.chunks.size))
                else -> {
                    Muted(stringResource(R.string.sources_indexing, (state.progress * 100).toInt()))
                    LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (entry.status == SourceStatus.READY) {
                    TextButton(enabled = !busy, onClick = { run({ model.setSourceLinked(entry.id, !linked) }) }) {
                        Text(stringResource(if (linked) R.string.sources_unlink else R.string.sources_link))
                    }
                }
                TextButton(
                    enabled = !busy,
                    onClick = { run({ model.removeSource(entry.id) }) },
                ) { Text(stringResource(R.string.sources_remove)) }
            }
        }
    }

    SectionTitle(stringResource(R.string.facts_title), stringResource(R.string.facts_help))
    if (facts.isEmpty()) Muted(stringResource(R.string.facts_empty))
    facts.forEach { fact ->
        val label =
            when (fact.status) {
                FactStatus.ORIGINAL -> stringResource(R.string.fact_status_original)
                FactStatus.DIVERGENCE -> stringResource(R.string.fact_status_divergence)
                FactStatus.UNKNOWN -> stringResource(R.string.fact_status_unknown)
                FactStatus.AI_CREATED -> stringResource(R.string.fact_status_ai)
            }
        Column(Modifier.fillMaxWidth()) {
            Text(fact.text, style = MaterialTheme.typography.bodyMedium)
            Muted(
                stringResource(
                    R.string.fact_line,
                    label,
                    stringResource(if (fact.protagonistKnows) R.string.fact_knows else R.string.fact_not_knows),
                ),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(enabled = !busy, onClick = { run({ model.setFactKnown(fact.id, !fact.protagonistKnows) }) }) {
                    Text(stringResource(if (fact.protagonistKnows) R.string.fact_mark_unknown else R.string.fact_mark_known))
                }
                TextButton(
                    enabled = !busy,
                    onClick = { run({ model.removeFact(fact.id) }) },
                ) { Text(stringResource(R.string.sources_remove)) }
            }
        }
    }
    OutlinedTextField(
        newFact,
        { newFact = it },
        Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.fact_new_label)) },
    )
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !busy && newFact.isNotBlank(),
        onClick = {
            run({ model.addManualFact(newFact) }) {
                status = it
                if (it == null) newFact = ""
            }
        },
    ) { Text(stringResource(R.string.fact_add)) }
}
