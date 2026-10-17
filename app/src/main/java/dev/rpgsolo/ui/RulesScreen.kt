package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/** Aba Tutorial e regras (10.3): passos do pacote, regras marcadas como incertas e busca por trecho (4.2.3). */
@Composable
fun RulesScreen(model: AppModel) {
    val reg = model.content.registry
    val ids = reg.installed().map { it.id }
    var selected by remember { mutableStateOf(ids.firstOrNull()) }
    var query by remember { mutableStateOf("") }
    val pkg = selected?.let { reg.get(it) }

    ScreenColumn {
        if (pkg == null) {
            AppCard { Text("Nenhum pacote de regras instalado.") }
            return@ScreenColumn
        }
        if (ids.size > 1) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ids.forEach { id -> FilterChip(selected = id == selected, onClick = { selected = id }, label = { Text(id) }) }
            }
        }
        OutlinedTextField(
            query, { query = it }, Modifier.fillMaxWidth(), placeholder = { Text("Buscar regra") },
            singleLine = true, shape = MaterialTheme.shapes.large
        )
        if (query.isNotBlank()) {
            val hits = pkg.search(query, 5)
            if (hits.isEmpty()) Muted("Nada encontrado neste pacote.")
            hits.forEach {
                AppCard {
                    Text(it.title, style = MaterialTheme.typography.titleSmall)
                    Text(it.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            SectionTitle("Tutorial")
            if (pkg.data.tutorial.isEmpty()) Muted("Este pacote não traz tutorial.")
            pkg.data.tutorial.forEach {
                AppCard(accent = MaterialTheme.colorScheme.primary) {
                    Text("${it.order}. ${it.title}", style = MaterialTheme.typography.titleSmall)
                    Text(it.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (pkg.data.uncertain.isNotEmpty()) {
                SectionTitle("Regras incertas", "Nunca tratadas como oficiais.")
                AppCard(accent = MaterialTheme.colorScheme.error) {
                    pkg.data.uncertain.forEach { Muted("• ${it.note}") }
                }
            }
        }
    }
}
