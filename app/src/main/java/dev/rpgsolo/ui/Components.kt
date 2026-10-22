package dev.rpgsolo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Tela rolável padrão: margens e espaçamento iguais em todas as abas. */
@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content,
    )
}

@Composable
fun SectionTitle(
    text: String,
    hint: String? = null,
) {
    Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        if (hint != null) Text(hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Cartão padrão: fundo elevado, borda fina e, se quiser, uma faixa de cor à esquerda. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            if (accent != null) Column(Modifier.width(4.dp).fillMaxHeight().background(accent)) {}
            Column(Modifier.weight(1f).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
        }
    }
}

/** Etiqueta pequena (condição, estado, resultado). */
@Composable
fun Pill(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(shape = CircleShape, color = color.copy(alpha = 0.16f), border = BorderStroke(1.dp, color.copy(alpha = 0.5f))) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
fun Muted(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
fun ErrorText(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
}
