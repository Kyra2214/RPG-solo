package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rpgsolo.engine.SkillCategory
import dev.rpgsolo.engine.SkillEffectRecipient
import dev.rpgsolo.engine.SkillActionKind

@Composable
fun SkillTreePanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit
) {
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedTargets by remember { mutableStateOf(emptyMap<String, String>()) }
    var initialInputs by remember { mutableStateOf(emptyMap<String, String>()) }
    var initialSkillPoints by remember { mutableStateOf("0") }
    val initial = model.initialBuildState()
    val initialValuesValid = initialInputs.values.all { it.toIntOrNull()?.let { value -> value >= 0 } == true } &&
        initialSkillPoints.toIntOrNull()?.let { it >= 0 } == true
    val initialTotal = initialInputs.values.sumOf { it.toIntOrNull()?.toLong() ?: 0L } +
        (initialSkillPoints.toIntOrNull()?.toLong() ?: 0L)
    val actions = remember(model, version, model.campaignVersion, model.packageVersion) { model.skillTreeActions() }
    val entries = remember(version, model.campaignVersion, model.packageVersion) { actions.view() }
    val targets = remember(version, model.campaignVersion) { actions.targets() }
    val revivableTargets = remember(version, model.campaignVersion) { actions.revivableTargets() }
    val state = model.statPointsState()

    AppCard {
        SectionTitle("Árvore de habilidades", "Pontos disponíveis: ${state.abilityPoints}. Habilidades básicas de raça/sub-raça são gratuitas; evolução custa pontos.")
        if (!initial.allocated) {
            SectionTitle("Distribuir os 12 pontos iniciais", "Divida livremente entre atributos e pontos de habilidade. A distribuição só pode ser feita uma vez.")
            initial.stats.forEach { stat ->
                OutlinedTextField(
                    value = initialInputs[stat.id] ?: "0",
                    onValueChange = { value -> initialInputs = initialInputs + (stat.id to value.filter { it.isDigit() }) },
                    label = { Text("${stat.name} (atual: ${stat.current})") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = initialSkillPoints,
                onValueChange = { initialSkillPoints = it.filter { ch -> ch.isDigit() } },
                label = { Text("Pontos para habilidades") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Muted("Distribuídos: $initialTotal/12")
            Button(
                enabled = enabled && initialValuesValid && initialTotal == 12L && initial.stats.isNotEmpty(),
                onClick = {
                    val statIncreases = initial.stats.associate { stat -> stat.id to (initialInputs[stat.id]?.toIntOrNull() ?: 0) }
                        .filterValues { it > 0 }
                    act({ model.allocateInitialBuild(statIncreases, initialSkillPoints.toIntOrNull() ?: 0) }) { result ->
                        message = result ?: "Os 12 pontos iniciais foram distribuídos."
                        version++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Confirmar distribuição") }
        } else {
            Muted("Distribuição inicial concluída.")
        }
        if (entries.isEmpty()) {
            Muted("Escolha uma raça/sub-raça, classe ou profissão na ficha ativa para ver suas árvores.")
        } else {
            entries.forEach { entry ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(entry.skill.name, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                    Muted("${categoryLabel(entry.skill.category)} · nível ${entry.currentLevel} · ${entry.skill.element.takeUnless { it.name == "NONE" } ?: "sem elemento fixo"}")
                    entry.reason?.let { Muted("Requisitos: $it") }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        if (entry.skill.freeBaseSkill && entry.currentLevel == 0) {
                            Button(
                                enabled = enabled && entry.canUse,
                                onClick = {
                                    act({ actions.grantFreeBaseSkill(entry.skill.id) }) { result ->
                                        message = result ?: "Habilidade básica concedida."
                                        version++
                                    }
                                }
                            ) { Text("Receber grátis") }
                        } else if (entry.currentLevel == 0) {
                            OutlinedButton(
                                enabled = enabled && entry.canUse && !entry.hasScroll,
                                onClick = {
                                    act({ actions.buyScroll(entry.skill.id) }) { result ->
                                        message = result ?: "Pergaminho comprado."
                                        version++
                                    }
                                }
                            ) { Text("Pergaminho: ${entry.scrollPriceSilver ?: "—"} prata") }
                            Button(
                                enabled = enabled && entry.canUse && entry.hasScroll && state.abilityPoints >= entry.pointCost,
                                onClick = {
                                    act({ actions.unlockOrUpgrade(entry.skill.id) }) { result ->
                                        message = result ?: "Habilidade desbloqueada."
                                        version++
                                    }
                                }
                            ) { Text("Desbloquear (${entry.pointCost} ponto)") }
                        } else {
                            Button(
                                enabled = enabled && entry.canUse && state.abilityPoints >= entry.pointCost,
                                onClick = {
                                    act({ actions.unlockOrUpgrade(entry.skill.id) }) { result ->
                                        message = result ?: "Habilidade evoluída."
                                        version++
                                    }
                                }
                            ) { Text("Evoluir (${entry.pointCost} ponto)") }
                            if (entry.skill.effects.any { it.recipient == SkillEffectRecipient.TARGET } || entry.skill.actionKind == SkillActionKind.REVIVE) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                    (if (entry.skill.actionKind == SkillActionKind.REVIVE) revivableTargets else targets).forEach { (targetId, targetName) ->
                                        FilterChip(
                                            selected = selectedTargets[entry.skill.id] == targetId,
                                            onClick = { selectedTargets = selectedTargets + (entry.skill.id to targetId) },
                                            label = { Text(targetName) },
                                            enabled = enabled
                                        )
                                    }
                                }
                            }
                            if (entry.skill.category != SkillCategory.PASSIVE) {
                                OutlinedButton(
                                    enabled = enabled && (entry.skill.effects.none { it.recipient == SkillEffectRecipient.TARGET } && entry.skill.actionKind != SkillActionKind.REVIVE || selectedTargets[entry.skill.id] != null),
                                    onClick = {
                                        act({ actions.activate(entry.skill.id, selectedTargets[entry.skill.id]) }) { result ->
                                            message = result ?: "Habilidade ativada."
                                            version++
                                        }
                                    }
                                ) { Text("Ativar") }
                            }
                        }
                    }
                }
            }
            OutlinedButton(
                enabled = enabled,
                onClick = {
                    act({ actions.reset() }) { result ->
                        message = result ?: "Pontos redefinidos. Habilidades básicas gratuitas foram preservadas."
                        version++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Resetar habilidades — 25.000 prata") }
        }
        message?.let { Muted(it) }
    }
}

private fun categoryLabel(category: SkillCategory): String = when (category) {
    SkillCategory.OFFENSE -> "Ofensiva"
    SkillCategory.DEFENSE -> "Defensiva"
    SkillCategory.BUFF -> "Buff"
    SkillCategory.SUPPORT -> "Suporte"
    SkillCategory.PASSIVE -> "Passiva"
    SkillCategory.UTILITY -> "Utilitária"
    SkillCategory.CONTROL -> "Controle"
}
