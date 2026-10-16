package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Aba Tutorial e regras (10.3): passos do pacote, regras marcadas como incertas e busca por trecho (4.2.3). */
@Composable
fun RulesScreen(model: AppModel) {
    val reg = model.content.registry
    val ids = reg.installed().map { it.id }
    var selected by remember { mutableStateOf(ids.firstOrNull()) }
    var query by remember { mutableStateOf("") }
    val pkg = selected?.let { reg.get(it) }

    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (pkg == null) {
            Text("Nenhum pacote de regras instalado.")
            return@Column
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ids.forEach { id -> FilterChip(selected = id == selected, onClick = { selected = id }, label = { Text(id) }) }
        }
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text("Buscar regra") }, singleLine = true)
        if (query.isNotBlank()) {
            val hits = pkg.search(query, 5)
            if (hits.isEmpty()) Text("Nada encontrado neste pacote.", style = MaterialTheme.typography.bodySmall)
            hits.forEach {
                Text(it.title, style = MaterialTheme.typography.titleSmall)
                Text(it.text, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            Text("Tutorial", style = MaterialTheme.typography.titleMedium)
            if (pkg.data.tutorial.isEmpty()) Text("Este pacote não traz tutorial.", style = MaterialTheme.typography.bodySmall)
            pkg.data.tutorial.forEach {
                Text("${it.order}. ${it.title}", style = MaterialTheme.typography.titleSmall)
                Text(it.text, style = MaterialTheme.typography.bodyMedium)
            }
            if (pkg.data.uncertain.isNotEmpty()) {
                Text("Regras incertas (nunca tratadas como oficiais)", style = MaterialTheme.typography.titleSmall)
                pkg.data.uncertain.forEach { Text("• ${it.note}", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}
