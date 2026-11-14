package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R

/**
 * Árvore de status: pontos de status e atributos. É independente da árvore de habilidades
 * (SkillTreePanel), que só cuida dos pontos de habilidade.
 */
@Composable
fun StatusTreePanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var initialInputs by remember { mutableStateOf(emptyMap<String, Int>()) }
    @Suppress("UNUSED_EXPRESSION")
    version
    val initial = model.initialBuildState()
    val state = model.statPointsState()
    val initialTotal = initialInputs.values.sumOf { it.toLong() }

    AppCard {
        SectionTitle(stringResource(R.string.statustree_title))
        if (!initial.allocated) {
            // Distribuição inicial: só status, travada quando bate o total (ex.: 10).
            Muted(stringResource(R.string.skilltree_initial_help, initial.statPoints))
            initial.stats.forEach { stat ->
                val added = initialInputs[stat.id] ?: 0
                val left = initial.statPoints.toLong() - initialTotal
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        stringResource(R.string.skilltree_stat_label, stat.name, stat.current),
                        Modifier.weight(1f),
                        style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                    )
                    OutlinedButton(enabled = added > 0, onClick = { initialInputs = initialInputs + (stat.id to added - 1) }) { Text("−") }
                    Text(added.toString(), Modifier.width(28.dp))
                    OutlinedButton(
                        enabled = left > 0 && stat.current.toLong() + added < stat.max,
                        onClick = { initialInputs = initialInputs + (stat.id to added + 1) },
                    ) { Text("+") }
                }
            }
            Muted(stringResource(R.string.skilltree_distributed, initialTotal, initial.statPoints))
            Button(
                enabled = enabled && initialTotal == initial.statPoints.toLong() && initial.stats.isNotEmpty(),
                onClick = {
                    val increases = initialInputs.filterValues { it > 0 }
                    act({ model.allocateInitialStats(increases) }) { result ->
                        message = result ?: context.getString(R.string.skilltree_initial_done)
                        version++
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.skilltree_confirm)) }
        } else {
            Muted(stringResource(R.string.statustree_points_left, state.statPoints))
            if (state.statPoints > 0) {
                state.choices.forEach { choice ->
                    val desc = stringResource(R.string.statpoints_increase_desc, choice.name)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            choice.name + " · " + stringResource(R.string.statpoints_choice, choice.current),
                            Modifier.weight(1f),
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                        )
                        Button(
                            enabled = enabled,
                            onClick = {
                                act({ model.increaseStatPoint(choice.id) }) { result ->
                                    message = result
                                    version++
                                }
                            },
                        ) { Text("+1") }
                    }
                }
            }
        }
        message?.let { Muted(it) }
    }
}
