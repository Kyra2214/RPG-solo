package dev.rpgsolo.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rpgsolo.ai.Author
import dev.rpgsolo.ai.ChatLine
import dev.rpgsolo.ai.PanelState
import dev.rpgsolo.ai.PlayController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Aba Jogar (10.1): chat com o mestre, rolagem animada de dados e painel rápido do personagem. */
@Composable
fun PlayScreen(model: AppModel, onNavigate: (Tab) -> Unit = {}) {
    val c = model.controller
    if (c == null) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            AppCard {
                Text("🎲", fontSize = 40.sp)
                Text("A mesa ainda está vazia", style = MaterialTheme.typography.titleLarge)
                Muted("Vamos preparar sua mesa. Configure o provedor, crie ou escolha um herói e depois descreva a campanha.")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Muted("1. Configure e teste o provedor de IA.")
                    OutlinedButton(onClick = { onNavigate(Tab.CONFIG) }, modifier = Modifier.fillMaxWidth()) {
                        Text("1 · Configurar IA")
                    }
                    Muted("2. Crie ou escolha uma ficha de herói.")
                    OutlinedButton(onClick = { onNavigate(Tab.PERSONAGENS) }, modifier = Modifier.fillMaxWidth()) {
                        Text("2 · Criar ou escolher herói")
                    }
                    Muted("3. Descreva a aventura e comece a jogar.")
                    Button(onClick = { onNavigate(Tab.CAMPANHAS) }, modifier = Modifier.fillMaxWidth()) {
                        Text("3 · Criar campanha")
                    }
                }
            }
        }
        return
    }
    PlayContent(c, model, model.classifierWarning)
}

@Composable
private fun PlayContent(c: PlayController, model: AppModel, classifierWarning: String?) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var playError by remember { mutableStateOf<String?>(null) }
    var streamingText by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var version by remember { mutableIntStateOf(0) }
    val lines = remember(version) { c.lines }
    val panel = remember(version) { c.panel() }
    val suggestions = remember(version) { c.suggestions() }
    // Teste pedido pelo mestre no meio da história: o jogo espera o jogador rolar.
    val pendingCheck = remember(version) { c.pending()?.label }
    val listState = rememberLazyListState()

    val visible = lines.withIndex().filter { (i, l) ->
        !(l.author == Author.MASTER && i > 0 && lines[i - 1].roll != null && !c.isRollRevealed(i - 1))
    }
    val pendingRoll = lines.withIndex().any { (i, l) -> l.roll != null && !c.isRollRevealed(i) }

    LaunchedEffect(visible.size, pendingRoll, streamingText.length, busy) {
        val lastIndex = visible.lastIndex + if (busy && streamingText.isNotBlank()) 1 else 0
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    // A chamada à IA é de rede: sempre fora da thread principal.
    fun run(block: () -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            try {
                withContext(Dispatchers.IO) { block() }
            } catch (e: Exception) {
                playError = e.message ?: "A ação falhou inesperadamente. Você pode tentar de novo."
                streamingText = ""
            } finally {
                busy = false
                version++
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        model.chapterNotice?.let { notice ->
            Muted(notice)
        }
        panel?.let { StatusPanel(it) }
        InventoryPanel(model)
        AdditionalRulesPanel(model)
        PlacesPanel(model)
        QuickActions(
            enabled = !busy && !pendingRoll && pendingCheck == null,
            onSheet = { run { c.submit("ver ficha") } },
            onInventory = { run { c.submit("inventário") } },
            onRest = { run { c.submit("descansar") } }
        )
        CombatControls(model)

        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (lines.isEmpty()) {
                item(key = "vazio") {
                    Muted("Descreva o que seu personagem faz. O mestre responde, e quando a ação exigir um teste, é você quem rola os dados.")
                }
            }
            itemsIndexed(visible, key = { _, iv -> iv.index }) { _, iv ->
                ChatItem(
                    iv.value,
                    revealed = c.isRollRevealed(iv.index),
                    onRevealed = { c.revealRoll(iv.index); version++ }
                )
            }
            if (busy && streamingText.isNotBlank()) {
                item(key = "streaming-narration") {
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(modifier = Modifier.width(28.dp))
                            Text("Mestre narrando…", style = MaterialTheme.typography.labelMedium)
                        }
                        Text(streamingText)
                    }
                }
            }
        }

        playError?.let { ErrorText(it) }
        BottomBar(
            busy = busy, pendingRoll = pendingRoll, pendingCheck = pendingCheck, text = text,
            onText = { text = it; playError = null },
            suggestions = suggestions, warning = classifierWarning,
            onSend = {
                val submitted = text
                run {
                    val result = c.submit(submitted) { partial ->
                        scope.launch(Dispatchers.Main) { streamingText = partial; version++ }
                    }
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        streamingText = ""
                        if (result is dev.rpgsolo.ai.PlayResult.Failed) playError = result.reason
                        else {
                            text = ""
                            playError = null
                        }
                    }
                    model.autoCloseChapterIfNeeded()
                }
            },
            onRollCheck = { run {
                val result = c.rollPending { partial ->
                    scope.launch(Dispatchers.Main) { streamingText = partial; version++ }
                }
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    streamingText = ""
                    playError = (result as? dev.rpgsolo.ai.PlayResult.Failed)?.reason
                }
                model.autoCloseChapterIfNeeded()
            } },
            onUndo = { run {
                val undone = c.undo()
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    playError = if (undone) null else "Não há jogada para desfazer."
                }
            } },
            onRegenerate = { run {
                val result = c.regenerate()
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    playError = (result as? dev.rpgsolo.ai.PlayResult.Failed)?.reason
                }
            } }
        )
    }
}

@Composable
private fun InventoryPanel(model: AppModel) {
    val campaignVersion = model.campaignVersion
    @Suppress("UNUSED_VARIABLE") val observeVersion = campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val inventory = model.inventoryView()
    if (inventory.isEmpty()) return
    AppCard {
        Text("Inventário", style = MaterialTheme.typography.titleSmall)
        inventory.forEach { item ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("${item.name} x${item.quantity}")
                    Muted(item.description)
                    if (item.kind == "equipamento" && item.slot != null) {
                        Muted("slot: ${item.slot}${if (item.equipped) " · equipado" else ""}")
                    } else {
                        Muted("tipo: ${item.kind}")
                    }
                }
                when {
                    item.kind == "equipamento" && item.slot != null && item.equipped ->
                        TextButton(onClick = { message = model.unequipItem(item.slot) }) { Text("Desequipar") }
                    item.kind == "equipamento" && item.slot != null ->
                        TextButton(onClick = { message = model.equipItem(item.id) }) { Text("Equipar") }
                    item.kind == "consumivel" ->
                        TextButton(onClick = { message = model.useItem(item.id) }) { Text("Usar") }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun PlacesPanel(model: AppModel) {
    val places = model.placesView()
    var message by remember { mutableStateOf<String?>(null) }
    if (places.isEmpty()) return
    AppCard {
        Text("Mapa textual", style = MaterialTheme.typography.titleSmall)
        places.forEach { place ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(place.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (place.current) Pill("Você está aqui", MaterialTheme.colorScheme.tertiary)
                }
                if (place.description.isNotBlank()) Text(place.description)
                if (place.current && place.exits.isNotEmpty()) {
                    Muted("Saídas conhecidas:")
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        place.exits.forEach { (id, name) ->
                            OutlinedButton(onClick = { message = model.moveToPlace(id) }) { Text("Ir para $name") }
                        }
                    }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun AdditionalRulesPanel(model: AppModel) {
    val campaignVersion = model.campaignVersion
    @Suppress("UNUSED_VARIABLE") val observeVersion = campaignVersion
    val rules = model.additionalRulesView()
    var message by remember { mutableStateOf<String?>(null) }
    if (rules.isEmpty()) return
    AppCard {
        Text("Adaptações da campanha", style = MaterialTheme.typography.titleSmall)
        Muted("Regras propostas pela IA não alteram a mecânica até você aprová-las.")
        rules.forEach { rule ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(rule.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (rule.approved) Pill("Aprovada", MaterialTheme.colorScheme.tertiary)
                    else Pill("Pendente", MaterialTheme.colorScheme.error)
                }
                Text(rule.description)
                Muted("Efeitos: " + rule.effects.entries.joinToString(", ") { (key, value) ->
                    "${key} ${if (value >= 0) "+" else ""}${value}"
                })
                Muted("Limite/custo: ${rule.limit}")
                if (!rule.approved) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { message = model.approveAdditionalRule(rule.id) }) { Text("Aprovar") }
                        TextButton(onClick = { message = model.rejectAdditionalRule(rule.id) }) { Text("Descartar") }
                    }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

/** Atalhos de baixo custo: o controlador reconhece estas frases como ações locais, sem chamar a IA. */
@Composable
private fun QuickActions(enabled: Boolean, onSheet: () -> Unit, onInventory: () -> Unit, onRest: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(onClick = onSheet, enabled = enabled, label = { Text("Ver ficha") })
            AssistChip(onClick = onInventory, enabled = enabled, label = { Text("Inventário") })
            AssistChip(onClick = onRest, enabled = enabled, label = { Text("Descansar") })
        }
    }
}

@Composable
private fun CombatControls(model: AppModel) {
    val campaignVersion = model.campaignVersion
    @Suppress("UNUSED_VARIABLE") val observeVersion = campaignVersion
    var message by remember(campaignVersion) { mutableStateOf<String?>(null) }
    var npcName by remember { mutableStateOf("") }
    val combat = model.combatState()
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (combat == null) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = npcName,
                        onValueChange = { npcName = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Nome do adversário") },
                        singleLine = true
                    )
                    Button(onClick = {
                        message = model.addNpc(npcName)
                        if (message?.contains("adicionado") == true) npcName = ""
                    }, enabled = npcName.isNotBlank()) { Text("Adicionar") }
                }
                Button(onClick = { message = model.startCombat() }, modifier = Modifier.fillMaxWidth()) {
                    Text("⚔ Iniciar combate")
                }
            } else {
                Text("Rodada ${combat.round} · turno ${combat.turnIndex + 1}/${combat.order.size}: ${model.combatantName(combat.current)}", style = MaterialTheme.typography.labelLarge)
                model.combatOrderDescription()?.let { order ->
                    Muted("Ordem de iniciativa: $order")
                }
                if (model.isPlayerCombatTurn()) {
                    val targets = model.combatTargets()
                    if (targets.isEmpty()) {
                        Muted("Não há alvos vivos.")
                    } else {
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            targets.forEach { target ->
                                Button(onClick = { message = model.attack(target.id) }) {
                                    Text("Atacar ${target.name}${target.condition.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty()}${if (target.rulesValidated) "" else " (não validado)"}")
                                }
                            }
                        }
                    }
                } else {
                    Muted("Aguarde o turno de ${model.combatantName(combat.current)}.")
                }
                if (model.isPlayerCombatTurn() && model.canDefend()) {
                    OutlinedButton(
                        onClick = { message = model.defend() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Defender — bônus contra o próximo ataque") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (model.isPlayerCombatTurn()) {
                        Button(onClick = { message = model.nextCombatTurn() }, modifier = Modifier.weight(1f)) { Text("Passar turno") }
                        OutlinedButton(onClick = { message = model.fleeCombat() }, modifier = Modifier.weight(1f)) { Text("Fugir") }
                    } else {
                        Button(onClick = { message = model.resolveNpcTurn() }, modifier = Modifier.weight(1f)) { Text("Resolver turno inimigo") }
                    }
                    TextButton(onClick = { message = model.endCombat() }) { Text("Encerrar") }
                }
            }
            message?.let { ErrorText(it) }
        }
    }
}

@Composable
private fun StatusPanel(p: PanelState) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 2.dp) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(p.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                if (p.inCombat) Pill("⚔ em combate", MaterialTheme.colorScheme.error)
            }
            Muted("Nível ${p.level} · ${p.xp} XP")
            p.resources.forEach { (id, v) -> ResourceBar(id, v.first, v.second) }
            if (p.conditions.isNotEmpty()) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    p.conditions.forEach { Pill(it, MaterialTheme.colorScheme.tertiary) }
                }
            }
            p.quest?.let { Muted("📜 $it") }
        }
    }
}

@Composable
private fun ResourceBar(id: String, current: Int, max: Int) {
    val color = resourceColor(id)
    val frac = if (max <= 0) 0f else (current.toFloat() / max).coerceIn(0f, 1f)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(id.uppercase(), Modifier.width(30.dp), style = MaterialTheme.typography.labelLarge, color = color)
        LinearProgressIndicator(
            progress = { frac },
            modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
        Text("$current/$max", Modifier.widthIn(min = 44.dp), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ChatItem(line: ChatLine, revealed: Boolean, onRevealed: () -> Unit) {
    when (line.author) {
        Author.PLAYER -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                Text(
                    line.text, Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Author.ROLL -> {
            val view = line.roll
            if (view != null) RollCard(view, revealed, onRevealed)
            else Pill("🎲 ${line.text}", MaterialTheme.colorScheme.secondary)
        }
        Author.MASTER -> Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                Modifier.width(3.dp).fillMaxHeight().clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            )
            Spacer(Modifier.width(12.dp))
            Text(line.text, style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif, lineHeight = 27.sp, fontSize = 17.sp))
        }
        Author.SYSTEM -> ErrorText(line.text)
    }
}

@Composable
private fun Thinking() {
    val t = rememberInfiniteTransition(label = "pensando")
    val a by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "alfa")
    Text("O mestre está pensando…", Modifier.alpha(a), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun BottomBar(
    busy: Boolean, pendingRoll: Boolean, pendingCheck: String?, text: String, onText: (String) -> Unit,
    suggestions: List<String>, warning: String?,
    onSend: () -> Unit, onRollCheck: () -> Unit, onUndo: () -> Unit, onRegenerate: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 3.dp) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            warning?.let { ErrorText("Não consegui decidir se a ação exige teste ($it). Seguindo sem teste.") }
            if (busy) Thinking()
            if (pendingRoll && !busy) Text("🎲 Role os dados para continuar.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            // O mestre pediu um teste: só resta rolar (ou desfazer). O motor rola e o mestre narra o desfecho.
            if (pendingCheck != null && !busy && !pendingRoll) {
                Button(onClick = onRollCheck, modifier = Modifier.fillMaxWidth()) { Text("🎲 Rolar $pendingCheck") }
            }
            // 10.1.2: sugestões opcionais. Tocar só preenche o campo; o jogador pode editar ou ignorar.
            if (suggestions.isNotEmpty() && !busy && !pendingRoll) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    suggestions.forEach { sug -> AssistChip(onClick = { onText(sug) }, label = { Text(sug) }) }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = text, onValueChange = onText, modifier = Modifier.weight(1f),
                    placeholder = { Text(if (pendingCheck != null) "Role o teste para continuar" else "O que você faz?") },
                    enabled = !busy && !pendingRoll && pendingCheck == null,
                    shape = MaterialTheme.shapes.large, maxLines = 4
                )
                Button(enabled = !busy && !pendingRoll && pendingCheck == null && text.isNotBlank(), onClick = onSend) { Text("Enviar") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(enabled = !busy, onClick = onUndo) { Text("↩ Desfazer") }
                TextButton(enabled = !busy && !pendingRoll, onClick = onRegenerate) { Text("🔄 Narrar de novo") }
            }
        }
    }
}
