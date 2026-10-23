package dev.rpgsolo.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rpgsolo.engine.Outcome

/** Taverna à luz de vela: fundo quase preto quente, dourado de brasa e cobre. O claro é pergaminho. */
private val Dark =
    darkColorScheme(
        primary = Color(0xFFE0A84B), onPrimary = Color(0xFF2B1B00),
        primaryContainer = Color(0xFF4A3410), onPrimaryContainer = Color(0xFFFFDDA8),
        secondary = Color(0xFFC48A66), onSecondary = Color(0xFF2E1405),
        secondaryContainer = Color(0xFF45291A), onSecondaryContainer = Color(0xFFFFDBC8),
        tertiary = Color(0xFF7FB7A3), onTertiary = Color(0xFF00382C),
        tertiaryContainer = Color(0xFF1F4F42), onTertiaryContainer = Color(0xFFB9F0DD),
        background = Color(0xFF14110F), onBackground = Color(0xFFEDE3D3),
        surface = Color(0xFF1B1714), onSurface = Color(0xFFEDE3D3),
        surfaceVariant = Color(0xFF2B2520), onSurfaceVariant = Color(0xFFCFC2AF),
        surfaceTint = Color(0xFFE0A84B),
        surfaceDim = Color(0xFF14110F), surfaceBright = Color(0xFF3A322B),
        surfaceContainerLowest = Color(0xFF100D0B), surfaceContainerLow = Color(0xFF191512),
        surfaceContainer = Color(0xFF211C18), surfaceContainerHigh = Color(0xFF2A2420), surfaceContainerHighest = Color(0xFF342D27),
        outline = Color(0xFF8A7B6C), outlineVariant = Color(0xFF3D342C),
        error = Color(0xFFFF8A7A), onError = Color(0xFF3B0A04),
        errorContainer = Color(0xFF5C1A12), onErrorContainer = Color(0xFFFFDAD4),
    )

private val Light =
    lightColorScheme(
        primary = Color(0xFF8A4B14), onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDCC0), onPrimaryContainer = Color(0xFF2E1500),
        secondary = Color(0xFF5E6B4A), onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFDFEBC8), onSecondaryContainer = Color(0xFF1B2A0A),
        tertiary = Color(0xFF2F6F62), onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFB9F0DD), onTertiaryContainer = Color(0xFF00201A),
        background = Color(0xFFF6EFE2), onBackground = Color(0xFF2A2118),
        surface = Color(0xFFFFFAF0), onSurface = Color(0xFF2A2118),
        surfaceVariant = Color(0xFFEADFCB), onSurfaceVariant = Color(0xFF4D4234),
        surfaceTint = Color(0xFF8A4B14),
        surfaceDim = Color(0xFFE4DAC7), surfaceBright = Color(0xFFFFFAF0),
        surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFFBF4E6),
        surfaceContainer = Color(0xFFF3EAD8), surfaceContainerHigh = Color(0xFFEDE2CE), surfaceContainerHighest = Color(0xFFE7DBC5),
        outline = Color(0xFF8C7B66), outlineVariant = Color(0xFFD6C8B0),
        error = Color(0xFFB3261E), onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC), onErrorContainer = Color(0xFF410E0B),
    )

private val base = Typography()
private val Serif = FontFamily.Serif

/** Títulos com serifa (cara de livro de regras); texto corrido continua sem serifa para ler bem em tela pequena. */
private val AppTypography =
    base.copy(
        headlineMedium = base.headlineMedium.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
        headlineSmall = base.headlineSmall.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
        titleSmall = base.titleSmall.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
        bodyLarge = base.bodyLarge.copy(lineHeight = 24.sp),
        bodyMedium = base.bodyMedium.copy(lineHeight = 21.sp),
    )

private val AppShapes =
    Shapes(
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(24.dp),
        extraLarge = RoundedCornerShape(32.dp),
    )

@Composable
fun RpgTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}

@Composable
private fun isDark(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

/** Cor do resultado de um teste: dourado crítico, verde sucesso, âmbar parcial, vermelho falha. */
@Composable
fun outcomeColor(o: Outcome): Color {
    val dark = isDark()
    return when (o) {
        Outcome.CRITICAL_SUCCESS -> if (dark) Color(0xFFFFC857) else Color(0xFF9A6A00)
        Outcome.SUCCESS -> if (dark) Color(0xFF6FCF97) else Color(0xFF2E7D4F)
        Outcome.PARTIAL_SUCCESS -> if (dark) Color(0xFFF2A03D) else Color(0xFFB36B00)
        Outcome.FAILURE -> if (dark) Color(0xFFE57368) else Color(0xFFB3261E)
        Outcome.CRITICAL_FAILURE -> if (dark) Color(0xFFFF5252) else Color(0xFF8E0000)
    }
}

/** Cor da barra de cada recurso (PV vermelho, PM azul, PA dourado). */
@Composable
fun resourceColor(id: String): Color =
    when (id.lowercase()) {
        "pv", "hp" -> Color(0xFFD95D50)
        "pm", "mp" -> Color(0xFF5B8DEF)
        "pa", "ap" -> Color(0xFFE0A84B)
        else -> MaterialTheme.colorScheme.primary
    }
