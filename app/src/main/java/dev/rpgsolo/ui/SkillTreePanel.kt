package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import dev.rpgsolo.engine.SkillCategory

@Composable
fun SkillTreePanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit
) {
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    val actions = remember(model, version) { model.skillTreeActions() }
    val entries = remember(version, model.campaignVersion) { actions.view() }
    val state = model.statPointsState()

    AppCard {
        SectionTitle("Árvore de habilidades", "Pontos disponíveis: \${state.abilityPoints}. Habilidades básicas de raça/sub-raça são gratuitas; evolução custa pontos.")
        if (entries.isEmpty()) {
            Muted("Escolha uma raça/sub-raça, classe ou profissão na ficha ativa para ver suas árvores.")
        } else {
            entries.forEach { entry ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(entry.skill.name, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                    Muted("\${categoryLabel(entry.skill.category)} · nível \${entry.currentLevel} · \${entry.skill.element.takeUnless { it.name == "NONE" } ?: "sem elemento fixo"}")
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
                            ) { Text("Pergaminho: \${entry.scrollPriceSilver ?: "—"} prata") }
                            Button(
                                enabled = enabled && entry.canUse && entry.hasScroll && state.abilityPoints >= entry.pointCost,
                                onClick = {
                                    act({ actions.unlockOrUpgrade(entry.skill.id) }) { result ->
                                        message = result ?: "Habilidade desbloqueada."
                                        version++
                                    }
                                }
                            ) { Text("Desbloquear (\${entry.pointCost} ponto)") }
                        } else {
                            Button(
                                enabled = enabled && entry.canUse && state.abilityPoints >= entry.pointCost,
                                onClick = {
                                    act({ actions.unlockOrUpgrade(entry.skill.id) }) { result ->
                                        message = result ?: "Habilidade evoluída."
                                        version++
                                    }
                                }
                            ) { Text("Evoluir (\${entry.pointCost} ponto)") }
                            if (entry.skill.category != SkillCategory.PASSIVE) {
                                OutlinedButton(
                                    enabled = enabled,
                                    onClick = {
                                        act({ actions.activate(entry.skill.id) }) { result ->
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
