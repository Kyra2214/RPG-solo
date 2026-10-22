package dev.rpgsolo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rpgsolo.ai.RollView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/** Duração da rolagem de cada dado: o primeiro assenta antes, os outros em sequência. */
private fun spinMs(index: Int) = 950 + index * 230

/** Tempo total até todos os dados assentarem (com folga para o "pulo" final). */
fun rollDurationMs(diceCount: Int) = (spinMs((diceCount - 1).coerceAtLeast(0)) + 650).toLong()

private val PIPS: Map<Int, List<Pair<Float, Float>>> = run {
    val tl = 0.27f to 0.27f; val tr = 0.73f to 0.27f
    val ml = 0.27f to 0.5f; val mr = 0.73f to 0.5f; val c = 0.5f to 0.5f
    val bl = 0.27f to 0.73f; val br = 0.73f to 0.73f
    mapOf(
        1 to listOf(c), 2 to listOf(tl, br), 3 to listOf(tl, c, br), 4 to listOf(tl, tr, bl, br),
        5 to listOf(tl, tr, c, bl, br), 6 to listOf(tl, tr, ml, mr, bl, br)
    )
}

/**
 * Um dado. Enquanto [rolling], gira, quica e troca de número; ao fim assenta em [final] com um pulinho.
 * Se [done] já começa como true (histórico), mostra o valor direto, sem animação.
 * O valor final vem do motor: a animação só enfeita, nunca decide.
 */
@Composable
private fun Die(sides: Int, final: Int, index: Int, rolling: Boolean, done: Boolean, tint: Color) {
    var shown by remember { mutableIntStateOf(if (done) final else 0) }
    val rot = remember { Animatable(0f) }
    val lift = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }

    LaunchedEffect(rolling) {
        if (!rolling) return@LaunchedEffect
        val spin = spinMs(index)
        coroutineScope {
            launch { rot.animateTo(if (index % 2 == 0) 1080f else -1080f, tween(spin, easing = FastOutSlowInEasing)) }
            launch {
                for (h in listOf(-46f, -28f, -14f)) {
                    lift.animateTo(h, tween(150, easing = FastOutSlowInEasing))
                    lift.animateTo(0f, tween(130, easing = FastOutLinearInEasing))
                }
            }
            launch {
                var t = 0
                while (t < spin - 150) {
                    shown = Random.nextInt(1, sides + 1)
                    delay(60)
                    t += 60
                }
            }
        }
        rot.snapTo(0f)
        shown = final
        pop.animateTo(1.28f, tween(90))
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    val face = MaterialTheme.colorScheme.surfaceContainerHighest
    val ink = MaterialTheme.colorScheme.onSurface
    Surface(
        modifier = Modifier.size(58.dp).semantics {
            contentDescription = when {
                rolling -> "Dado de $sides lados, rolando"
                !done -> "Dado de $sides lados, resultado oculto"
                else -> "Dado de $sides lados, resultado $final"
            }
        }.graphicsLayer {
            rotationZ = rot.value
            translationY = lift.value
            scaleX = pop.value
            scaleY = pop.value
        },
        shape = RoundedCornerShape(14.dp),
        color = face,
        border = BorderStroke(2.dp, if (done || shown != 0) tint else MaterialTheme.colorScheme.outline),
        shadowElevation = 6.dp
    ) {
        val pips = if (sides == 6) PIPS[shown] else null
        if (pips != null) {
            Canvas(Modifier.fillMaxSize().padding(2.dp)) {
                pips.forEach { (x, y) ->
                    drawCircle(ink, radius = size.minDimension * 0.085f, center = Offset(size.width * x, size.height * y))
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize().padding(5.dp)) {
                    val polygonSides = when (sides) {
                        4 -> 3
                        20 -> 6
                        else -> sides.coerceIn(4, 12)
                    }
                    val radius = size.minDimension * 0.43f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val path = Path()
                    repeat(polygonSides) { n ->
                        val angle = -PI / 2.0 + (2.0 * PI * n / polygonSides)
                        val point = Offset(
                            center.x + radius * cos(angle).toFloat(),
                            center.y + radius * sin(angle).toFloat()
                        )
                        if (n == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                    }
                    path.close()
                    drawPath(path, color = face, style = androidx.compose.ui.graphics.drawscope.Fill)
                    drawPath(
                        path, color = tint,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )
                }
                Text(
                    if (shown == 0) "?" else shown.toString(),
                    fontSize = if (shown >= 100) 18.sp else 24.sp, fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif, color = ink
                )
            }
        }
    }
}

/**
 * Cartão da rolagem no chat. O jogador toca em "Rolar dados": os dados giram, quicam e caem nos números que o
 * motor já tinha decidido; só então aparecem o total, o resultado e (na tela) a narração do mestre.
 * [revealed] vem de fora para a rolagem não ser refeita ao rolar a lista nem ao reabrir a aba.
 */
@Composable
fun RollCard(view: RollView, revealed: Boolean, onRevealed: () -> Unit) {
    var rolling by remember { mutableStateOf(false) }
    val tint = if (revealed && !view.plain) outcomeColor(view.outcome) else MaterialTheme.colorScheme.primary

    LaunchedEffect(rolling) {
        if (rolling) {
            delay(rollDurationMs(view.dice.size))
            onRevealed()
            rolling = false
        }
    }

    AppCard(accent = tint) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(view.label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Muted(
                when {
                    view.plain -> "rolagem"
                    view.label.contains("acerta com") -> "faces de sucesso"
                    else -> "d${view.sides}${if (view.modifier == 0) "" else " ${if (view.modifier > 0) "+" else "−"}${kotlin.math.abs(view.modifier)}"} contra ${view.difficulty}"
                }
            )
        }
        view.chance?.let { Muted("chance de sucesso: ${kotlin.math.round(it * 100).toInt()}%") }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 52.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            view.dice.forEachIndexed { i, v ->
                Die(view.sides, v, i, rolling = rolling, done = revealed, tint = tint)
            }
        }
        if (!revealed) {
            Button(onClick = { rolling = true }, enabled = !rolling, modifier = Modifier.fillMaxWidth()) {
                Text(if (rolling) "Rolando…" else "🎲  Rolar dados")
            }
        }
        AnimatedVisibility(visible = revealed, enter = fadeIn(tween(300)) + expandVertically(tween(300))) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val mod = when {
                    view.modifier > 0 -> " + ${view.modifier}"
                    view.modifier < 0 -> " − ${-view.modifier}"
                    else -> ""
                }
                Text(
                    if (view.plain) "${view.total}" else "${view.dice.joinToString(" + ")}$mod = ${view.total}",
                    style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f)
                )
                if (!view.plain) Pill(view.outcomeWord.uppercase(), tint)
            }
            if (!view.plain) {
                val marginText = when {
                    view.margin > 0 -> "passou por ${view.margin}"
                    view.margin < 0 -> "faltou ${-view.margin}"
                    else -> "no limite"
                }
                Muted(marginText)
            }
        }
    }
}
