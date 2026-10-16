package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.rpgsolo.rules.SourceLink
import dev.rpgsolo.rules.SystemModels

/** Aba Sistemas (10.5.1 e 4B.1.2): pacotes instalados, edição, versão, origem, licença e links do material oficial. */
@Composable
fun SystemsScreen(model: AppModel) {
    val c = model.content
    val uri = LocalUriHandler.current
    val models = SystemModels.list(c.registry, c.catalog)
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Sistemas instalados", style = MaterialTheme.typography.titleMedium)
        c.errors.forEach { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        if (models.isEmpty()) Text("Nenhum pacote de regras instalado.")
        models.forEach { m ->
            val manifest = c.registry.get(m.packageId)!!.manifest
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(m.title, style = MaterialTheme.typography.titleSmall)
                    Text("Pacote ${manifest.id} v${manifest.version} • ${manifest.kind} • licença ${manifest.licenseStatus}", style = MaterialTheme.typography.bodySmall)
                    Text("Origem: ${manifest.source}", style = MaterialTheme.typography.bodySmall)
                    Text(manifest.license, style = MaterialTheme.typography.bodySmall)
                    c.sources?.forSystem(manifest.edition).orEmpty().forEach { link -> LinkRow(link) { uri.openUri(link.url) } }
                }
            }
        }
        c.sources?.general().orEmpty().takeIf { it.isNotEmpty() }?.let { general ->
            Text("Material geral", style = MaterialTheme.typography.titleSmall)
            general.forEach { link -> LinkRow(link) { uri.openUri(link.url) } }
        }
        Text(
            "O app não embute material oficial: abre o link e você pode inserir os seus próprios PDFs depois. PDF é sempre opcional.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LinkRow(link: SourceLink, open: () -> Unit) {
    TextButton(onClick = open) {
        Text(link.title + if (link.verified) "" else " (link não verificado)")
    }
}
