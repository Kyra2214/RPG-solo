package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rpgsolo.rules.CharacterDraft
import dev.rpgsolo.rules.FichaBuild

/** Aba Personagens (10.2 e 5.0): criação guiada pelo pacote, sem PDF, com validação do motor e biblioteca de fichas. */
@Composable
fun CharactersScreen(model: AppModel) {
    val reg = model.content.registry
    val ids = reg.installed().map { it.id }
    var selected by remember { mutableStateOf(ids.firstOrNull()) }
    val pkg = selected?.let { reg.get(it) }

    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (pkg == null) {
            Text("Instale um pacote de regras na aba Sistemas para criar personagens.")
            return@Column
        }
        Text("Sistema", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ids.forEach { id -> FilterChip(selected = id == selected, onClick = { selected = id }, label = { Text(id) }) }
        }

        // Um rascunho por pacote; "version" força redesenhar depois de mudar o rascunho (ele não é observável).
        val draft = remember(selected) { CharacterDraft(pkg, "f-${model.fichas.size + 1}") }
        var version by remember(selected) { mutableIntStateOf(0) }
        var message by remember(selected) { mutableStateOf<String?>(null) }
        @Suppress("UNUSED_EXPRESSION") version

        if (pkg.data.examples.isNotEmpty()) {
            Text("Fichas prontas", style = MaterialTheme.typography.titleSmall)
            pkg.data.examples.forEach { ex ->
                OutlinedButton(onClick = { draft.loadExample(ex.id); version++ }) { Text(ex.title) }
            }
        }

        OutlinedTextField(draft.name, { draft.name = it; version++ }, Modifier.fillMaxWidth(), label = { Text("Nome") }, singleLine = true)
        OutlinedTextField(draft.concept, { draft.concept = it; version++ }, Modifier.fillMaxWidth(), label = { Text("Conceito (livre)") })

        Text("Atributos", style = MaterialTheme.typography.titleSmall)
        pkg.data.stats.forEach { st ->
            val v = draft.stats()[st.id] ?: st.default
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${st.name}: $v", Modifier.weight(1f))
                OutlinedButton(enabled = v > st.min, onClick = { draft.setStat(st.id, v - 1); version++ }) { Text("−") }
                OutlinedButton(enabled = v < st.max, onClick = { draft.setStat(st.id, v + 1); version++ }) { Text("+") }
            }
        }

        val report = draft.report()
        Text("Pontos restantes: ${draft.pointsLeft()} de ${pkg.creationPoints}", style = MaterialTheme.typography.bodyMedium)
        report.errors.forEach { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        report.warnings.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }

        if (draft.traits().isNotEmpty()) {
            Text("Escolhidos", style = MaterialTheme.typography.titleSmall)
            draft.traits().forEach { t ->
                OutlinedButton(onClick = { draft.removeTrait(t); version++ }) { Text("Remover ${pkg.trait(t)?.name ?: t}") }
            }
        }
        val allowed = draft.allowedTraits()
        if (allowed.isNotEmpty()) {
            Text("Disponíveis agora", style = MaterialTheme.typography.titleSmall)
            allowed.take(30).forEach { t ->
                OutlinedButton(onClick = { draft.addTrait(t.id); version++ }) { Text("${t.name} (${t.cost})") }
            }
        }

        Button(onClick = {
            message = when (val b = draft.build()) {
                is FichaBuild.Created -> { model.fichas += b.ficha; "Ficha de ${b.ficha.name} criada." }
                is FichaBuild.Invalid -> b.errors.joinToString("; ")
            }
        }) { Text("Criar ficha") }
        message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

        if (model.fichas.isNotEmpty()) {
            Text("Biblioteca", style = MaterialTheme.typography.titleMedium)
            model.fichas.forEach { f ->
                Text("${f.name} — ${f.concept.ifBlank { "sem conceito" }} (${f.system.editionId})", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
