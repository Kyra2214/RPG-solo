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
    var completedQuestId by remember { mutableStateOf("") }
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
        if (initial.allocated && entries.any { it.currentLevel == 0 && it.skill.scrollRank != null }) {
            OutlinedTextField(
                value = completedQuestId,
                onValueChange = { completedQuestId = it.trim() },
                label = { Text("ID da missão concluída para resgatar pergaminho") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (entries.isEmpty()) {
            Muted("Escolha uma raça/sub-raça, classe ou profissão na ficha ativa para ver suas árvores.")
        } else {
            entries.forEach { entry ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(entry.displayName, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                    Muted("${categoryLabel(entry.skill.category)} · nível ${entry.currentLevel} · ${if (entry.skill.requiredElement != null) "exige ${elementLabelUi(entry.skill.requiredElement)}" else if (entry.skill.affinityBound) "afinidade ${elementLabelUi(entry.skill.element)}" else "neutra"}")
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
                            if (entry.skill.effects.any { it.recipient == SkillEffectRecipient.TARGET } || entry.skill.actionKind == SkillActionKind.REVIVE || entry.skill.actionKind == SkillActionKind.MULTI_HIT || entry.skill.actionKind == SkillActionKind.POWER_STRIKE) {
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
                                val needsCombat = entry.skill.actionKind == SkillActionKind.POWER_STRIKE ||
                                    entry.skill.actionKind == SkillActionKind.MULTI_HIT ||
                                    entry.skill.effects.any { it.durationRounds != null }
                                OutlinedButton(
                                    enabled = enabled && (!needsCombat || model.hasActiveCombat()) && ((entry.skill.effects.none { it.recipient == SkillEffectRecipient.TARGET } && entry.skill.actionKind != SkillActionKind.REVIVE && entry.skill.actionKind != SkillActionKind.MULTI_HIT && entry.skill.actionKind != SkillActionKind.POWER_STRIKE) || selectedTargets[entry.skill.id] != null),
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
                    if (entry.currentLevel == 0 && !entry.skill.freeBaseSkill && entry.skill.scrollRank != null) {
                        OutlinedButton(
                            enabled = enabled && completedQuestId.isNotBlank(),
                            onClick = {
                                act({ actions.rewardScrollFromQuest(entry.skill.id, completedQuestId) }) { result ->
                                    message = result ?: "Pergaminho resgatado como recompensa da missão."
                                    version++
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Resgatar pergaminho por missão") }
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

private fun elementLabelUi(element: dev.rpgsolo.engine.ElementPath): String = when (element) {
    dev.rpgsolo.engine.ElementPath.WATER -> "Água"
    dev.rpgsolo.engine.ElementPath.FIRE -> "Fogo"
    dev.rpgsolo.engine.ElementPath.WIND -> "Vento"
    dev.rpgsolo.engine.ElementPath.EARTH -> "Terra"
    dev.rpgsolo.engine.ElementPath.METAL -> "Metal"
    dev.rpgsolo.engine.ElementPath.YIN -> "Yin"
    dev.rpgsolo.engine.ElementPath.YANG -> "Yang"
    dev.rpgsolo.engine.ElementPath.NONE -> "não definida"
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
