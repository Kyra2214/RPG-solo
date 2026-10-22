package dev.rpgsolo.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CursePanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedCurseId by remember { mutableStateOf("maldicao-sangue-frio") }
    var selectedTargetId by remember { mutableStateOf<String?>(null) }
    var selectedSourceId by remember { mutableStateOf<String?>(null) }
    var sourceIdText by remember { mutableStateOf("") }
    val actions = remember(model, version, model.campaignVersion, model.packageVersion) { model.curseActions() }
    val curses = remember(version) { actions.all() }
    val targets = remember(version, model.campaignVersion) { actions.targets() }
    val sources = remember(version, model.campaignVersion) { actions.sources() }
    val targetId = selectedTargetId ?: actions.defaultTargetId().takeIf { id -> targets.any { it.first == id } } ?: targets.firstOrNull()?.first
    val selectedCurse = curses.firstOrNull { it.id == selectedCurseId } ?: curses.firstOrNull()
    val active = targetId?.let(actions::applied).orEmpty()

    AppCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SectionTitle("Maldições lançadas", "Catálogo separado da Classe de Malditos")
            TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Ocultar" else "Abrir (${curses.size})") }
        }
        if (expanded) {
            if (targets.isEmpty() || sources.isEmpty() || selectedCurse == null) {
                Muted("Não há alvos ou maldições disponíveis.")
            } else {
                Text("Quem lança", style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    sources.forEach { (id, name) ->
                        FilterChip(
                            selected = (selectedSourceId ?: actions.defaultTargetId()) == id,
                            onClick = { selectedSourceId = id; sourceIdText = "personagem:$id" },
                            label = { Text(name) },
                            enabled = enabled
                        )
                    }
                }
                androidx.compose.material3.OutlinedTextField(
                    value = sourceIdText,
                    onValueChange = { sourceIdText = it.trim() },
                    label = { Text("Origem (personagem:id, criatura:id, artefato:id ou ritual:id)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Alvo", style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    targets.forEach { (id, name) ->
                        FilterChip(
                            selected = targetId == id,
                            onClick = { selectedTargetId = id },
                            label = { Text(name) },
                            enabled = enabled
                        )
                    }
                }
                Text("Maldição", style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    curses.forEach { curse ->
                        FilterChip(
                            selected = selectedCurse.id == curse.id,
                            onClick = { selectedCurseId = curse.id },
                            label = { Text(curse.name) },
                            enabled = enabled
                        )
                    }
                }
                Muted(selectedCurse.description)
                Muted("Origem: ${selectedCurse.sourceType}. Duração: ${selectedCurse.durationRounds?.let { "${it} rodadas" } ?: "até ser removida"}.")
                selectedCurse.effects.forEach { effect ->
                    Muted("${effect.target}: ${if (effect.baseValue > 0) "+" else ""}${effect.baseValue} (${effect.stackingGroup})")
                }
                if (selectedCurse.resistanceCheckId != null) {
                    Muted("Exige teste '${selectedCurse.resistanceCheckId}' contra dificuldade ${selectedCurse.resistanceDifficulty}. O motor rola o teste automaticamente e só aplica a maldição se o alvo não resistir.")
                }
                Button(
                    enabled = enabled && targetId != null && (selectedCurse.durationRounds == null || model.hasActiveCombat()),
                    onClick = {
                        act({ actions.apply(requireNotNull(targetId), selectedCurse.id, sourceIdText.ifBlank { "personagem:" + (selectedSourceId ?: actions.defaultTargetId()) }) }) { result ->
                            message = result ?: "Maldição aplicada."
                            version++
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Aplicar maldição") }

                Text("Maldições ativas no alvo", style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                if (active.isEmpty()) Muted("Nenhuma maldição aplicada.")
                active.forEach { applied ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(applied.curse.name, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                        Muted("Origem: ${applied.sourceId}; duração: ${applied.remainingRounds?.let { "${it} rodadas" } ?: "permanente"}.")
                        val method = applied.curse.removalMethods.firstOrNull()
                        if (method != null) {
                            OutlinedButton(
                                enabled = enabled,
                                onClick = {
                                    act({ actions.remove(requireNotNull(targetId), applied.curse.id, method) }) { result ->
                                        message = result ?: "Maldição removida por: $method."
                                        version++
                                    }
                                }
                            ) { Text("Remover: $method") }
                        }
                    }
                }
            }
        }
        message?.let { Muted(it) }
    }
}
