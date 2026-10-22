package dev.rpgsolo.ui

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import dev.rpgsolo.rules.SystemModels

/** Aba Sistemas: pacotes instalados (edição e versão) e importação de pacotes de regras no formato do app. */
@Composable
fun SystemsScreen(model: AppModel) {
    val c = model.content
    val packageVersion = model.packageVersion
    @Suppress("UNUSED_VARIABLE") val observePackageVersion = packageVersion
    var importStatus by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { selected ->
        if (selected != null) {
            importStatus = "Importando…"
            scope.launch {
                importStatus = try {
                    withContext(Dispatchers.IO) { model.importPackage(selected) }
                } catch (e: Exception) {
                    e.message ?: "Falha ao importar o pacote."
                }
            }
        }
    }
    val models = SystemModels.list(c.registry, c.catalog)
    ScreenColumn {
        SectionTitle("Importar pacote de regras", "Selecione um ZIP contendo manifest.txt e os arquivos .txt do pacote.")
        TextButton(onClick = { importer.launch(arrayOf("application/zip", "application/octet-stream")) }) {
            Text("Importar ZIP")
        }
        importStatus?.let { Text(it, color = if (it.startsWith("Pacote ") && !it.startsWith("Pacote recusado")) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error) }
        SectionTitle("Sistemas instalados")
        c.errors.forEach { ErrorText(it) }
        if (models.isEmpty()) AppCard { Text("Nenhum pacote de regras instalado.") }
        models.forEach { m ->
            val manifest = c.registry.get(m.packageId)!!.manifest
            AppCard(accent = MaterialTheme.colorScheme.primary) {
                Text(m.title, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Pill("v${manifest.version}")
                    Pill(manifest.kind.name, MaterialTheme.colorScheme.secondary)
                    if (model.activePackageId() == manifest.id) Pill("Ativo nesta campanha", MaterialTheme.colorScheme.tertiary)
                }
                Muted("Pacote ${manifest.id}")
                Muted("Origem: ${manifest.source} · Licença: ${manifest.license}")
                Muted("Estado da licença: ${manifest.licenseStatus.name}")
            }
        }
    }
}
