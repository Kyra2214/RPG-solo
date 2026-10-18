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

/** Aba Sistemas (10.5.1): pacotes instalados (edição e versão) e links de consulta ao material oficial. Sem licença e sem PDF. */
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
                }
                Muted("Pacote ${manifest.id}")
                c.sources?.forSystem(manifest.edition).orEmpty().forEach { link -> LinkRow(link) { uri.openUri(link.url) } }
            }
        }
        c.sources?.let { lib ->
            SectionTitle("Consultar regras oficiais", "Só links: abrem no navegador. O app não copia nem guarda esse material.")
            AppCard { lib.links.forEach { link -> LinkRow(link) { uri.openUri(link.url) } } }
        }
    }
}

@Composable
private fun LinkRow(link: SourceLink, open: () -> Unit) {
    TextButton(onClick = open) {
        Text(link.title + if (link.verified) "" else " (link não verificado)")
    }
}
