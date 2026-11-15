package dev.rpgsolo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import kotlinx.coroutines.launch

/**
 * PT-05: importar e exportar fichas em JSON, em Configurações. Mesma validação e mesmo formato de
 * [AppModel.importFicha] e [AppModel.exportFicha]; nada do formato ou do modelo muda.
 */
@Composable
fun FichasJsonSection(model: AppModel) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var importText by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    fun finishImport(result: String?) {
        message = result
        if (result?.startsWith("Ficha ") == true && result.contains("importada")) importText = ""
    }

    fun runImport(raw: String) {
        busy = true
        scope.launch {
            val result =
                try {
                    cancellableIo { model.importFicha(raw) }
                } catch (e: Exception) {
                    e.message ?: context.getString(R.string.char_export_failed)
                } finally {
                    busy = false
                }
            finishImport(result)
        }
    }

    val openFile =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                busy = true
                scope.launch {
                    val raw =
                        try {
                            cancellableIo {
                                context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                            }
                        } catch (e: Exception) {
                            null
                        } finally {
                            busy = false
                        }
                    if (raw.isNullOrBlank()) message = context.getString(R.string.cfg_json_read_failed) else runImport(raw)
                }
            }
        }
    val saveFile =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            val id = selectedId
            val raw = id?.let { model.exportFicha(it) }
            if (uri != null && raw != null) {
                busy = true
                scope.launch {
                    val ok =
                        try {
                            cancellableIo {
                                context.contentResolver.openOutputStream(uri)?.use { it.write(raw.toByteArray(Charsets.UTF_8)) } != null
                            }
                        } catch (e: Exception) {
                            false
                        } finally {
                            busy = false
                        }
                    message = context.getString(if (ok) R.string.cfg_json_saved else R.string.char_export_failed)
                }
            }
        }

    AppCard {
        Text(stringResource(R.string.cfg_json_title), style = MaterialTheme.typography.titleSmall)
        Muted(stringResource(R.string.char_import_help))
        OutlinedTextField(
            value = importText,
            onValueChange = { importText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.char_import_label)) },
            placeholder = { Text(stringResource(R.string.char_import_placeholder)) },
            minLines = 3,
            maxLines = 8,
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !busy && importText.isNotBlank(),
            onClick = { runImport(importText) },
        ) { Text(stringResource(R.string.char_import_button)) }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !busy,
            onClick = { openFile.launch(arrayOf("application/json", "text/plain", "*/*")) },
        ) { Text(stringResource(R.string.cfg_json_import_file)) }

        Text(stringResource(R.string.cfg_json_export_title), style = MaterialTheme.typography.titleSmall)
        val fichas = model.fichas.toList()
        if (fichas.isEmpty()) {
            Muted(stringResource(R.string.cfg_json_export_none))
        } else {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fichas.forEach { f ->
                    FilterChip(selected = selectedId == f.id, onClick = { selectedId = f.id }, label = { Text(f.name) })
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !busy && selectedId != null,
                onClick = {
                    val raw = selectedId?.let { model.exportFicha(it) }
                    if (raw != null) {
                        clipboard.setText(AnnotatedString(raw))
                        message = context.getString(R.string.char_json_copied)
                    } else {
                        message = context.getString(R.string.char_export_failed)
                    }
                },
            ) { Text(stringResource(R.string.char_copy_json)) }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !busy && selectedId != null,
                onClick = {
                    val name = fichas.firstOrNull { it.id == selectedId }?.name.orEmpty()
                    saveFile.launch("ficha-" + name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "heroi" } + ".json")
                },
            ) { Text(stringResource(R.string.cfg_json_save_file)) }
        }
        message?.let { Muted(it) }
    }
}
