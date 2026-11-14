package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
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
import dev.rpgsolo.engine.DefeatAttitude
import dev.rpgsolo.engine.DefeatOutcome

/**
 * RV-06: interface do rival. Em combate, o botão Poupar para NPC relevante no limite da vida (RV-01); sempre, a lista
 * dos rivais registrados no mundo (RV-02). Só mostra o que o motor já decidiu; nada aqui vem da IA.
 */
@Composable
fun RivalPanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit,
) {
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }

    @Suppress("UNUSED_EXPRESSION")
    version
    val spare = model.spareOptions()
    val rivals = model.rivals()
    if (spare.isEmpty() && rivals.isEmpty() && message == null) return
    AppCard {
        SectionTitle(
            stringResource(R.string.rival_title),
            stringResource(if (spare.isNotEmpty()) R.string.rival_help_spare else R.string.rival_help_list),
        )
        spare.forEach { opt ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.rival_spare_row, opt.name, opt.current, opt.max), Modifier.weight(1f))
                Button(
                    enabled = enabled,
                    onClick = {
                        act({ model.spare(opt.id) }) { result ->
                            message = result
                            version++
                        }
                    },
                ) { Text(stringResource(R.string.rival_spare)) }
            }
        }
        rivals.forEach { r ->
            val outcome =
                stringResource(
                    when (r.outcome) {
                        DefeatOutcome.SPARED -> R.string.rival_outcome_spared
                        DefeatOutcome.FLED -> R.string.rival_outcome_fled
                        DefeatOutcome.HUMILIATED -> R.string.rival_outcome_humiliated
                    },
                )
            val attitude =
                stringResource(
                    when (r.attitude) {
                        DefeatAttitude.RESENTMENT -> R.string.rival_attitude_resentment
                        DefeatAttitude.RESPECT -> R.string.rival_attitude_respect
                        DefeatAttitude.DEBT -> R.string.rival_attitude_debt
                    },
                )
            Muted(stringResource(R.string.rival_row, r.name, outcome, attitude))
        }
        message?.let { Muted(it) }
    }
}
