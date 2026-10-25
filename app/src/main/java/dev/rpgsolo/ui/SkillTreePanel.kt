package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import dev.rpgsolo.engine.SkillActionKind
import dev.rpgsolo.engine.SkillCategory
import dev.rpgsolo.engine.SkillEffectRecipient

@Composable
fun SkillTreePanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedTargets by remember { mutableStateOf(emptyMap<String, String>()) }
    var completedQuestId by remember { mutableStateOf("") }
    var initialInputs by remember { mutableStateOf(emptyMap<String, String>()) }
    val initial = model.initialBuildState()
    val initialValuesValid = initialInputs.values.all { it.toIntOrNull()?.let { value -> value >= 0 } == true }
    val initialTotal = initialInputs.values.sumOf { it.toIntOrNull()?.toLong() ?: 0L }
    val actions = remember(model, version, model.campaignVersion, model.packageVersion) { model.skillTreeActions() }
    val entries = remember(version, model.campaignVersion, model.packageVersion) { actions.view() }
    val targets = remember(version, model.campaignVersion) { actions.targets() }
    val revivableTargets = remember(version, model.campaignVersion) { actions.revivableTargets() }
    val state = model.statPointsState()

    AppCard {
        SectionTitle(
            stringResource(R.string.skilltree_title),
            stringResource(R.string.skilltree_points_help, state.abilityPoints),
        )
        if (!initial.allocated) {
            SectionTitle(
                stringResource(R.string.skilltree_initial_title, initial.statPoints, initial.abilityPoints),
                stringResource(R.string.skilltree_initial_help, initial.statPoints, initial.abilityPoints),
            )
            initial.stats.forEach { stat ->
                OutlinedTextField(
                    value = initialInputs[stat.id] ?: "0",
                    onValueChange = { value -> initialInputs = initialInputs + (stat.id to value.filter { it.isDigit() }) },
                    label = { Text(stringResource(R.string.skilltree_stat_label, stat.name, stat.current)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Muted(stringResource(R.string.skilltree_distributed, initialTotal, initial.statPoints))
            Button(
                enabled = enabled && initialValuesValid && initialTotal == initial.statPoints.toLong() && initial.stats.isNotEmpty(),
                onClick = {
                    val statIncreases =
                        initial.stats.associate { stat -> stat.id to (initialInputs[stat.id]?.toIntOrNull() ?: 0) }
                            .filterValues { it > 0 }
                    act({ model.allocateInitialBuild(statIncreases, initial.abilityPoints) }) { result ->
                        message = result ?: context.getString(R.string.skilltree_initial_done)
                        version++
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.skilltree_confirm)) }
        } else {
            Muted(stringResource(R.string.skilltree_initial_complete))
        }
        if (initial.allocated && entries.any { it.currentLevel == 0 && it.skill.scrollRank != null }) {
            OutlinedTextField(
                value = completedQuestId,
                onValueChange = { completedQuestId = it.trim() },
                label = { Text(stringResource(R.string.skilltree_quest_id)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (entries.isEmpty()) {
            Muted(stringResource(R.string.skilltree_empty))
        } else {
            entries.forEach { entry ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(entry.displayName, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                    val requiredElement = entry.skill.requiredElement
                    Muted(
                        stringResource(
                            R.string.skilltree_entry_info,
                            categoryLabel(entry.skill.category),
                            entry.currentLevel,
                            if (requiredElement != null) {
                                stringResource(R.string.skilltree_requires, elementLabelUi(requiredElement))
                            } else if (entry.skill.affinityBound) {
                                stringResource(R.string.skilltree_affinity, elementLabelUi(entry.skill.element))
                            } else {
                                stringResource(R.string.skilltree_neutral)
                            },
                        ),
                    )
                    entry.reason?.let { Muted(stringResource(R.string.skilltree_requirements, it)) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        if (entry.skill.freeBaseSkill && entry.currentLevel == 0) {
                            Button(
                                enabled = enabled && entry.canUse,
                                onClick = {
                                    act({ actions.grantFreeBaseSkill(entry.skill.id) }) { result ->
                                        message = result ?: context.getString(R.string.skilltree_msg_free)
                                        version++
                                    }
                                },
                            ) { Text(stringResource(R.string.skilltree_get_free)) }
                        } else if (entry.currentLevel == 0) {
                            OutlinedButton(
                                enabled = enabled && entry.canUse && !entry.hasScroll,
                                onClick = {
                                    act({ actions.buyScroll(entry.skill.id) }) { result ->
                                        message = result ?: context.getString(R.string.skilltree_msg_bought)
                                        version++
                                    }
                                },
                            ) { Text(stringResource(R.string.skilltree_scroll_price, entry.scrollPriceSilver?.toString() ?: "—")) }
                            Button(
                                enabled = enabled && entry.canUse && entry.hasScroll && state.abilityPoints >= entry.pointCost,
                                onClick = {
                                    act({ actions.unlockOrUpgrade(entry.skill.id) }) { result ->
                                        message = result ?: context.getString(R.string.skilltree_msg_unlocked)
                                        version++
                                    }
                                },
                            ) { Text(stringResource(R.string.skilltree_unlock, entry.pointCost)) }
                        } else {
                            Button(
                                enabled = enabled && entry.canUse && state.abilityPoints >= entry.pointCost,
                                onClick = {
                                    act({ actions.unlockOrUpgrade(entry.skill.id) }) { result ->
                                        message = result ?: context.getString(R.string.skilltree_msg_evolved)
                                        version++
                                    }
                                },
                            ) { Text(stringResource(R.string.skilltree_evolve, entry.pointCost)) }
                            if (entry.skill.effects.any {
                                    it.recipient == SkillEffectRecipient.TARGET
                                } || entry.skill.actionKind == SkillActionKind.REVIVE || entry.skill.actionKind == SkillActionKind.MULTI_HIT || entry.skill.actionKind == SkillActionKind.POWER_STRIKE
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                    (if (entry.skill.actionKind == SkillActionKind.REVIVE) revivableTargets else targets).forEach {
                                            (targetId, targetName) ->
                                        FilterChip(
                                            selected = selectedTargets[entry.skill.id] == targetId,
                                            onClick = { selectedTargets = selectedTargets + (entry.skill.id to targetId) },
                                            label = { Text(targetName) },
                                            enabled = enabled,
                                        )
                                    }
                                }
                            }
                            if (entry.skill.category != SkillCategory.PASSIVE) {
                                val needsCombat =
                                    entry.skill.actionKind == SkillActionKind.POWER_STRIKE ||
                                        entry.skill.actionKind == SkillActionKind.MULTI_HIT ||
                                        entry.skill.effects.any { it.durationRounds != null }
                                OutlinedButton(
                                    enabled =
                                        enabled && (!needsCombat || model.hasActiveCombat()) && (
                                            (
                                                entry.skill.effects.none {
                                                    it.recipient == SkillEffectRecipient.TARGET
                                                } && entry.skill.actionKind != SkillActionKind.REVIVE && entry.skill.actionKind != SkillActionKind.MULTI_HIT && entry.skill.actionKind != SkillActionKind.POWER_STRIKE
                                            ) || selectedTargets[entry.skill.id] != null
                                        ),
                                    onClick = {
                                        act({ actions.activate(entry.skill.id, selectedTargets[entry.skill.id]) }) { result ->
                                            message = result ?: context.getString(R.string.skilltree_msg_activated)
                                            version++
                                        }
                                    },
                                ) { Text(stringResource(R.string.skilltree_activate)) }
                            }
                        }
                    }
                    if (entry.currentLevel == 0 && !entry.skill.freeBaseSkill && entry.skill.scrollRank != null) {
                        OutlinedButton(
                            enabled = enabled && completedQuestId.isNotBlank(),
                            onClick = {
                                act({ actions.rewardScrollFromQuest(entry.skill.id, completedQuestId) }) { result ->
                                    message = result ?: context.getString(R.string.skilltree_msg_scroll_quest)
                                    version++
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(stringResource(R.string.skilltree_scroll_quest)) }
                    }
                }
            }
            OutlinedButton(
                enabled = enabled,
                onClick = {
                    act({ actions.reset() }) { result ->
                        message = result ?: context.getString(R.string.skilltree_msg_reset)
                        version++
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.skilltree_reset)) }
        }
        message?.let { Muted(it) }
    }
}

@Composable
private fun elementLabelUi(element: dev.rpgsolo.engine.ElementPath): String =
    when (element) {
        dev.rpgsolo.engine.ElementPath.WATER -> stringResource(R.string.skilltree_element_water)
        dev.rpgsolo.engine.ElementPath.FIRE -> stringResource(R.string.skilltree_element_fire)
        dev.rpgsolo.engine.ElementPath.WIND -> stringResource(R.string.skilltree_element_wind)
        dev.rpgsolo.engine.ElementPath.EARTH -> stringResource(R.string.skilltree_element_earth)
        dev.rpgsolo.engine.ElementPath.METAL -> stringResource(R.string.skilltree_element_metal)
        dev.rpgsolo.engine.ElementPath.YIN -> stringResource(R.string.skilltree_element_yin)
        dev.rpgsolo.engine.ElementPath.YANG -> stringResource(R.string.skilltree_element_yang)
        dev.rpgsolo.engine.ElementPath.NONE -> stringResource(R.string.skilltree_element_none)
    }

@Composable
private fun categoryLabel(category: SkillCategory): String =
    when (category) {
        SkillCategory.OFFENSE -> stringResource(R.string.skilltree_category_offense)
        SkillCategory.DEFENSE -> stringResource(R.string.skilltree_category_defense)
        SkillCategory.BUFF -> stringResource(R.string.skilltree_category_buff)
        SkillCategory.SUPPORT -> stringResource(R.string.skilltree_category_support)
        SkillCategory.PASSIVE -> stringResource(R.string.skilltree_category_passive)
        SkillCategory.UTILITY -> stringResource(R.string.skilltree_category_utility)
        SkillCategory.CONTROL -> stringResource(R.string.skilltree_category_control)
    }
