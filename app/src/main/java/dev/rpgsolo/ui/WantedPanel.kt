package dev.rpgsolo.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.OutlinedButton
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
import dev.rpgsolo.engine.WantedResponse
import dev.rpgsolo.engine.WantedStatus

/**
 * MP-06: tela de respostas ao procurado. Mostra só o que o motor registrou (emissor, degrau, recompensa) e deixa o
 * jogador responder (pagar, subornar, disfarçar, matar o mensageiro, provar que é falso, recrutar). O motor valida.
 */
@Composable
fun WantedPanel(
    model: AppModel,
    enabled: Boolean,
    act: (block: () -> String?, onResult: (String?) -> Unit) -> Unit,
) {
    var version by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }

    @Suppress("UNUSED_EXPRESSION")
    version
    val list = model.wantedList()
    if (list.isEmpty() && message == null) return
    AppCard {
        SectionTitle(stringResource(R.string.wanted_title), stringResource(R.string.wanted_help))
        list.forEach { r ->
            if (r.status == WantedStatus.HIDDEN) {
                Muted(stringResource(R.string.wanted_row_hidden, r.issuerName))
            } else {
                Muted(stringResource(R.string.wanted_row, r.issuerName, r.rung.label, model.wantedBounty(r)))
            }
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    WantedResponse.PAY to R.string.wanted_pay,
                    WantedResponse.BRIBE to R.string.wanted_bribe,
                    WantedResponse.DISGUISE to R.string.wanted_disguise,
                    WantedResponse.KILL_MESSENGER to R.string.wanted_kill_messenger,
                    WantedResponse.PROVE_FALSE to R.string.wanted_prove_false,
                    WantedResponse.RECRUIT to R.string.wanted_recruit,
                ).forEach { (response, label) ->
                    OutlinedButton(
                        enabled = enabled,
                        onClick = {
                            act({ model.respondWanted(r.id, response) }) { result ->
                                message = result
                                version++
                            }
                        },
                    ) { Text(stringResource(label)) }
                }
            }
        }
        message?.let { Muted(it) }
    }
}
