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

/**
 * LG-04: recuperar os itens de um corpo. Em combate exige o turno, faz um teste de destreza e gasta o turno (sem ataque);
 * se falhar, o adversário acerta o jogador. Depois da batalha é só pegar.
 */
@Composable
fun BodyLootPanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit,
) {
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    @Suppress("UNUSED_EXPRESSION")
    version
    val bodies = model.bodies()
    if (bodies.isEmpty() && message == null) return
    val combat = model.inCombatNow()
    AppCard {
        SectionTitle(
            stringResource(R.string.bodyloot_title),
            stringResource(if (combat) R.string.bodyloot_help_combat else R.string.bodyloot_help_after),
        )
        bodies.forEach { body ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.bodyloot_row, body.name, body.itemCount), Modifier.weight(1f))
                Button(
                    enabled = enabled && (!combat || model.isPlayerCombatTurn()),
                    onClick = {
                        act({ model.recoverBody(body.id) }) { result ->
                            message = result
                            version++
                        }
                    },
                ) { Text(stringResource(if (combat) R.string.bodyloot_try else R.string.bodyloot_take)) }
            }
        }
        message?.let { Muted(it) }
    }
}
