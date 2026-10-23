package dev.rpgsolo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import dev.rpgsolo.rules.SystemModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Aba Sistemas: pacotes instalados (edição e versão) e importação de pacotes de regras no formato do app. */
@Composable
fun SystemsScreen(model: AppModel) {
    val c = model.content
    val packageVersion = model.packageVersion

    @Suppress("UNUSED_VARIABLE")
    val observePackageVersion = packageVersion
    var importStatus by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val importingText = stringResource(R.string.systems_importing)
    val importFailureText = stringResource(R.string.systems_import_failure)
    val importer =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { selected ->
            if (selected != null) {
                importStatus = importingText
                scope.launch {
                    importStatus =
                        try {
                            withContext(Dispatchers.IO) { model.importPackage(selected) }
                        } catch (e: Exception) {
                            e.message ?: importFailureText
                        }
                }
            }
        }
    val models = SystemModels.list(c.registry, c.catalog)
    val installedEditions = c.registry.installed().map { it.edition }.toSet()
    val comingSoon = c.catalog?.systems.orEmpty().filter { it.id !in installedEditions }
    ScreenColumn {
        SectionTitle(stringResource(R.string.systems_import_title), stringResource(R.string.systems_import_help))
        TextButton(onClick = { importer.launch(arrayOf("application/zip", "application/octet-stream")) }) {
            Text(stringResource(R.string.systems_import_zip))
        }
        importStatus?.let {
            Text(
                it,
                color = if (it.startsWith("Pacote ") && !it.startsWith("Pacote recusado")) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
            )
        }
        SectionTitle(stringResource(R.string.systems_installed_title), stringResource(R.string.systems_installed_help))
        c.errors.forEach { ErrorText(it) }
        if (models.isEmpty()) AppCard { Text(stringResource(R.string.systems_empty)) }
        models.forEach { m ->
            val manifest = c.registry.get(m.packageId)!!.manifest
            AppCard(accent = MaterialTheme.colorScheme.primary) {
                Text(m.title, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Pill("v${manifest.version}")
                    Pill(manifest.kind.name, MaterialTheme.colorScheme.secondary)
                    if (model.activePackageId() == manifest.id) {
                        Pill(
                            stringResource(R.string.systems_active),
                            MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Muted(stringResource(R.string.systems_package_id, manifest.id))
                Muted(stringResource(R.string.systems_origin_license, manifest.source, manifest.license))
                Muted(stringResource(R.string.systems_license_status, manifest.licenseStatus.name))
            }
        }
        if (comingSoon.isNotEmpty()) {
            SectionTitle(stringResource(R.string.systems_soon_title), stringResource(R.string.systems_soon_help))
            comingSoon.forEach { e ->
                AppCard {
                    Text("${e.name} ${e.edition}", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Pill(stringResource(R.string.systems_soon_pill), MaterialTheme.colorScheme.secondary)
                    }
                    Muted(stringResource(R.string.systems_license_status, e.licenseStatus.name))
                }
            }
        }
    }
}
