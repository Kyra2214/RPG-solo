package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import dev.rpgsolo.R
import dev.rpgsolo.engine.ClashReport

/**
 * MU-04.2: cartão do confronto do motor unificado. Só apresenta o [ClashReport] (gerado do evento persistido);
 * não recalcula valores, dados nem dano. Sem animação: vale igual para jogador e IA e para o histórico.
 */
@Composable
fun ClashCard(report: ClashReport, attackerName: String = report.attackerId, targetName: String = report.targetId) {
    val lines = report.lines(attackerName, targetName)
    val tint = when {
        report.damage > 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }
    AppCard(modifier = Modifier.semantics { contentDescription = lines.joinToString(". ") }, accent = tint) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(lines.first(), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            when {
                report.ignoredDefense -> Pill(stringResource(R.string.clash_ignored_defense), tint)
                report.critical -> Pill(stringResource(R.string.clash_critical), tint)
            }
        }
        lines.drop(1).dropLast(1).forEach { Muted(it) }
        Text(stringResource(R.string.clash_final_damage, report.damage), style = MaterialTheme.typography.titleMedium, color = tint)
    }
}
