package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import dev.rpgsolo.R
import dev.rpgsolo.engine.PantheonKind

/**
 * LG-06: cartão do memorial (heróis mortos desta campanha) e lista recolhível do panteão do mundo (RV-03). Só leitura:
 * mostra o que o motor já gravou; nada aqui vem da IA nem altera o estado.
 */
@Composable
fun MemorialPanel(model: AppModel) {
    val version = model.campaignVersion
    var open by remember { mutableStateOf(false) }
    val cards = remember(version) { model.memorialCards() }
    val pantheon = remember(version, open) { if (open) model.pantheon() else emptyList() }
    val hasWorldEntries = remember(version) { model.pantheon().isNotEmpty() }
    if (cards.isEmpty() && !hasWorldEntries) return
    AppCard {
        SectionTitle(stringResource(R.string.memorial_title), stringResource(R.string.memorial_help))
        cards.forEach { c ->
            Column {
                Text(stringResource(R.string.memorial_card_head, c.heroName, c.level, c.diedOnDay))
                Muted(c.line)
            }
        }
        if (hasWorldEntries) {
            TextButton(onClick = { open = !open }) {
                Text(stringResource(if (open) R.string.memorial_pantheon_hide else R.string.memorial_pantheon_show))
            }
            if (open) {
                pantheon.forEach { e ->
                    val kind =
                        stringResource(
                            if (e.kind == PantheonKind.RIVAL) R.string.memorial_kind_rival else R.string.memorial_kind_hero,
                        )
                    Muted(stringResource(R.string.memorial_pantheon_row, e.name, kind, e.line))
                }
            }
        }
    }
}
