package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import dev.rpgsolo.rules.SourceLink
import dev.rpgsolo.rules.SystemModels

/** Aba Sistemas (10.5.1 e 4B.1.2): pacotes instalados, edição, versão, origem, licença e links do material oficial. */
@Composable
fun SystemsScreen(model: AppModel) {
    val c = model.content
    val uri = LocalUriHandler.current
    val models = SystemModels.list(c.registry, c.catalog)
    ScreenColumn {
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
                    Pill("licença ${manifest.licenseStatus}", MaterialTheme.colorScheme.tertiary)
                }
                Muted("Pacote ${manifest.id} · origem: ${manifest.source}")
                Muted(manifest.license)
                c.sources?.forSystem(manifest.edition).orEmpty().forEach { link -> LinkRow(link) { uri.openUri(link.url) } }
            }
        }
        c.sources?.general().orEmpty().takeIf { it.isNotEmpty() }?.let { general ->
            SectionTitle("Material geral")
            AppCard { general.forEach { link -> LinkRow(link) { uri.openUri(link.url) } } }
        }
        Muted("O app não embute material oficial: abre o link e você pode inserir os seus próprios PDFs depois. PDF é sempre opcional.")
    }
}

@Composable
private fun LinkRow(link: SourceLink, open: () -> Unit) {
    TextButton(onClick = open) {
        Text(link.title + if (link.verified) "" else " (link não verificado)")
    }
}
