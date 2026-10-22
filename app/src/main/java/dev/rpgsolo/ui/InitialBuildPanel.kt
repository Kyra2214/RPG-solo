package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Interface obrigatória para distribuir os 12 pontos iniciais entre atributos e habilidades. */
@Composable
fun InitialBuildPanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit
) {
    val state = remember(model, model.campaignVersion) { model.initialBuildState() }
    var increases by remember(model, model.campaignVersion, state.allocated) {
        mutableStateOf(state.stats.associate { it.id to 0 })
    }
    var skillPoints by remember(model, model.campaignVersion, state.allocated) { mutableIntStateOf(0) }
    var message by remember(model, model.campaignVersion) { mutableStateOf<String?>(null) }
    val used = increases.values.sum() + skillPoints
    val remaining = 12 - used

    if (state.allocated) return

    AppCard {
        SectionTitle("Distribuição inicial", "Distribua exatamente 12 pontos entre atributos e habilidades")
        Text("Pontos restantes: $remaining")
        state.stats.forEach { stat ->
            val increase = increases[stat.id] ?: 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stat.name)
                    Muted("${stat.current} → ${stat.current + increase} (máximo ${stat.max})")
                }
                OutlinedButton(
                    enabled = enabled && increase > 0,
                    onClick = { increases = increases + (stat.id to increase - 1) }
                ) { Text("−") }
                Button(
                    enabled = enabled && remaining > 0 && stat.current.toLong() + increase.toLong() < stat.max.toLong(),
                    onClick = { increases = increases + (stat.id to increase + 1) }
                ) { Text("+") }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Pontos de habilidade")
                Muted("Investidos: $skillPoints")
            }
            OutlinedButton(
                enabled = enabled && skillPoints > 0,
                onClick = { skillPoints-- }
            ) { Text("−") }
            Button(
                enabled = enabled && remaining > 0,
                onClick = { skillPoints++ }
            ) { Text("+") }
        }
        Button(
            enabled = enabled && used == 12,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                act({
                    model.allocateInitialBuild(increases.filterValues { it > 0 }, skillPoints)
                }) { result ->
                    message = result ?: "Distribuição inicial salva."
                }
            }
        ) { Text("Confirmar distribuição") }
        message?.let { Muted(it) }
    }
}
