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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R

@Composable
fun CursePanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
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
    val targetId =
        selectedTargetId ?: actions.defaultTargetId().takeIf {
                id ->
            targets.any { it.first == id }
        } ?: targets.firstOrNull()?.first
    val selectedCurse = curses.firstOrNull { it.id == selectedCurseId } ?: curses.firstOrNull()
    val active = targetId?.let(actions::applied).orEmpty()

    AppCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SectionTitle(stringResource(R.string.curse_title), stringResource(R.string.curse_subtitle))
            TextButton(onClick = {
                expanded = !expanded
            }) { Text(if (expanded) stringResource(R.string.curse_hide) else stringResource(R.string.curse_open, curses.size)) }
        }
        if (expanded) {
            if (targets.isEmpty() || sources.isEmpty() || selectedCurse == null) {
                Muted(stringResource(R.string.curse_none_available))
            } else {
                Text(stringResource(R.string.curse_caster), style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    sources.forEach { (id, name) ->
                        FilterChip(
                            selected = (selectedSourceId ?: actions.defaultTargetId()) == id,
                            onClick = {
                                selectedSourceId = id
                                sourceIdText = "personagem:$id"
                            },
                            label = { Text(name) },
                            enabled = enabled,
                        )
                    }
                }
                androidx.compose.material3.OutlinedTextField(
                    value = sourceIdText,
                    onValueChange = { sourceIdText = it.trim() },
                    label = { Text(stringResource(R.string.curse_source_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(stringResource(R.string.curse_target), style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    targets.forEach { (id, name) ->
                        FilterChip(
                            selected = targetId == id,
                            onClick = { selectedTargetId = id },
                            label = { Text(name) },
                            enabled = enabled,
                        )
                    }
                }
                Text(stringResource(R.string.curse_curse), style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    curses.forEach { curse ->
                        FilterChip(
                            selected = selectedCurse.id == curse.id,
                            onClick = { selectedCurseId = curse.id },
                            label = { Text(curse.name) },
                            enabled = enabled,
                        )
                    }
                }
                Muted(selectedCurse.description)
                Muted(
                    stringResource(
                        R.string.curse_source_duration,
                        selectedCurse.sourceType,
                        selectedCurse.durationRounds?.let { stringResource(R.string.curse_rounds, it) } ?: stringResource(R.string.curse_until_removed),
                    ),
                )
                selectedCurse.effects.forEach { effect ->
                    Muted("${effect.target}: ${if (effect.baseValue > 0) "+" else ""}${effect.baseValue} (${effect.stackingGroup})")
                }
                if (selectedCurse.resistanceCheckId != null) {
                    Muted(
                        stringResource(
                            R.string.curse_resistance,
                            selectedCurse.resistanceCheckId.orEmpty(),
                            selectedCurse.resistanceDifficulty.toString(),
                        ),
                    )
                }
                Button(
                    enabled = enabled && targetId != null && (selectedCurse.durationRounds == null || model.hasActiveCombat()),
                    onClick = {
                        act({
                            actions.apply(
                                requireNotNull(targetId),
                                selectedCurse.id,
                                sourceIdText.ifBlank {
                                    "personagem:" + (selectedSourceId ?: actions.defaultTargetId())
                                },
                            )
                        }) { result ->
                            message = result ?: context.getString(R.string.curse_applied)
                            version++
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.curse_apply)) }

                Text(stringResource(R.string.curse_active), style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                if (active.isEmpty()) Muted(stringResource(R.string.curse_none_applied))
                active.forEach { applied ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(applied.curse.name, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                        Muted(
                            stringResource(
                                R.string.curse_applied_info,
                                applied.sourceId,
                                applied.remainingRounds?.let { stringResource(R.string.curse_rounds, it) } ?: stringResource(R.string.curse_permanent),
                            ),
                        )
                        val method = applied.curse.removalMethods.firstOrNull()
                        if (method != null) {
                            OutlinedButton(
                                enabled = enabled,
                                onClick = {
                                    act({ actions.remove(requireNotNull(targetId), applied.curse.id, method) }) { result ->
                                        message = result ?: context.getString(R.string.curse_removed, method)
                                        version++
                                    }
                                },
                            ) { Text(stringResource(R.string.curse_remove, method)) }
                        }
                    }
                }
            }
        }
        message?.let { Muted(it) }
    }
}
