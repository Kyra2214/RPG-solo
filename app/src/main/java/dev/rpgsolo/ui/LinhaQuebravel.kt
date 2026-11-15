package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PT-06: linha de botões que quebra para a linha seguinte quando falta largura (tela de 360 dp), em vez de espremer
 * os rótulos até quebrarem letra por letra.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LinhaQuebravel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) { content() }
}
