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
    val setup = model.setupStatus()
    if (!setup.complete) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            AppCard {
                Text("🎲", fontSize = 40.sp)
                Text("Preparar sua mesa", style = MaterialTheme.typography.titleLarge)
                Muted("Complete as três etapas para começar uma campanha. O progresso acompanha sua configuração real.")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (setup.providerReady) {
                        Muted("✓ 1. Provedor de IA configurado.")
                    } else {
                        Muted("1. Configure e teste o provedor de IA.")
                        OutlinedButton(onClick = { onNavigate(Tab.CONFIG) }, modifier = Modifier.fillMaxWidth()) {
                            Text("1 · Configurar IA")
                        }
                    }
                    if (setup.heroReady) {
                        Muted("✓ 2. Herói disponível na biblioteca.")
                    } else {
                        Muted("2. Crie ou escolha uma ficha de herói.")
                        OutlinedButton(onClick = { onNavigate(Tab.PERSONAGENS) }, modifier = Modifier.fillMaxWidth()) {
                            Text("2 · Criar ou escolher herói")
                        }
                    }
                    if (setup.campaignReady) {
                        Muted("✓ 3. Campanha criada.")
                    } else {
                        Muted("3. Descreva a aventura e inicie a campanha com o herói escolhido.")
                        Button(onClick = { onNavigate(Tab.CAMPANHAS) }, modifier = Modifier.fillMaxWidth()) {
                            Text("3 · Criar campanha")
                        }
                    }
                }
            }
        }
        return
    }
    val c = model.controller
    if (c == null) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            AppCard {
                Text("Não foi possível iniciar o mestre", style = MaterialTheme.typography.titleLarge)
                Muted(model.configError ?: "Confira o provedor e tente novamente na aba Config.")
                OutlinedButton(onClick = { onNavigate(Tab.CONFIG) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Abrir Config")
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
    val clashes = remember(version) { c.clashReports() }
    // Teste pedido pelo mestre no meio da história: o jogo espera o jogador rolar.
    val pendingCheck = remember(version) { c.pending()?.label }
    val fortuneSpendOption = remember(version) { c.pendingFortuneSpendOption() }
    // luckOffer() usa a trava de turno: só consulta com a tela livre, nunca durante a narração.
    val luckOffer = remember(version, busy) { if (busy) null else c.luckOffer() }
    val listState = rememberLazyListState()

    val visible = lines.withIndex().filter { (i, line) ->
        val hasHiddenRollBeforeNarrative = if (line.author == Author.MASTER) {
            var cursor = i - 1
            var hidden = false
            while (cursor >= 0 && lines[cursor].roll != null) {
                if (!c.isRollRevealed(cursor)) hidden = true
                cursor--
            }
            hidden
        } else false
        !hasHiddenRollBeforeNarrative
    }
    val pendingRoll = lines.withIndex().any { (i, l) -> l.roll != null && !c.isRollRevealed(i) }

    LaunchedEffect(visible.size, pendingRoll, streamingText.length, busy) {
        val lastIndex = visible.lastIndex + if (busy && streamingText.isNotBlank()) 1 else 0
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    // A chamada à IA é de rede: sempre fora da thread principal.
    fun run(block: suspend () -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            try {
                cancellableIoSuspend { block() }
            } catch (e: Exception) {
                playError = e.message ?: "A ação falhou inesperadamente. Você pode tentar de novo."
                streamingText = ""
            } finally {
                busy = false
                version++
            }
        }
    }

    // Ações locais de painéis: mesma trava de turno e mesmo despacho em IO que a narração.
    fun act(block: () -> String?, onResult: (String?) -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            var result: String? = null
            try {
                result = withContext(Dispatchers.IO) { model.exclusiveTurn(block) }
            } catch (e: Exception) {
                result = e.message ?: "A ação falhou inesperadamente. Você pode tentar de novo."
            } finally {
                busy = false
                version++
            }
            onResult(result)
        }
    }

    Column(Modifier.fillMaxSize()) {
        model.chapterNotice?.let { notice ->
            Muted(notice)
        }
        panel?.let { StatusPanel(it) }
        InventoryPanel(model, enabled = !busy, act = ::act)
        ShopPanel(model, enabled = !busy, act = ::act)
        PowersPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
        SkillTreePanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
        CursePanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
        TraitUsesPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
        ConditionRemovalPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
        SurvivalPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
        luckOffer?.let { offer ->
            LuckPanel(
                offer = offer,
                resourceName = model.resourceName(offer.resource),
                enabled = !busy && !pendingRoll && pendingCheck == null,
                onSpend = { kind ->
                    run {
                        val result = c.spendLuck(kind) { partial ->
                            scope.launch(Dispatchers.Main) { streamingText = partial; version++ }
                        }
                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                            streamingText = ""
                            playError = (result as? dev.rpgsolo.ai.PlayResult.Failed)?.reason
                        }
                    }
                }
            )
        }
        AdditionalRulesPanel(model, enabled = !busy, act = ::act)
        PlacesPanel(model, enabled = !busy, act = ::act)
        QuickActions(
            enabled = !busy && !pendingRoll && pendingCheck == null,
            onSheet = { run { c.submit("ver ficha") } },
            onInventory = { run { c.submit("inventário") } },
            onRest = { run { c.submit("descansar") } }
        )
        AdvancedCheckControls(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
        CombatControls(model, enabled = !busy, act = ::act)

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
            itemsIndexed(clashes, key = { index, _ -> "clash-$index" }) { _, report ->
                ClashCard(report)
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
            busy = busy, pendingRoll = pendingRoll, pendingCheck = pendingCheck,
            fortuneSpendLabel = fortuneSpendOption?.let {
                "${it.cost} ${it.resourceName} para " + when (it.mode) {
                    dev.rpgsolo.engine.FortuneMode.ADVANTAGE -> "vantagem"
                    dev.rpgsolo.engine.FortuneMode.DISADVANTAGE -> "desvantagem"
                    dev.rpgsolo.engine.FortuneMode.NORMAL -> "fortuna"
                }
            }, text = text,
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
            onRollFortune = { run {
                val result = c.rollPending(useFortuneResource = true) { partial ->
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

/** F3.4: sorte/destino gasto depois da rolagem do último teste; custo e formas vêm do pacote. */
@Composable
private fun LuckPanel(
    offer: dev.rpgsolo.ai.LuckOption,
    resourceName: String,
    enabled: Boolean,
    onSpend: (dev.rpgsolo.engine.LuckKind) -> Unit
) {
    AppCard {
        Text("Sorte", style = MaterialTheme.typography.titleSmall)
        Muted("Custo: ${offer.cost} de $resourceName. Vale uma vez para o último teste e o mestre narra o novo resultado.")
        if (dev.rpgsolo.engine.LuckKind.REROLL in offer.kinds) {
            OutlinedButton(enabled = enabled, onClick = { onSpend(dev.rpgsolo.engine.LuckKind.REROLL) }, modifier = Modifier.fillMaxWidth()) {
                Text("Repetir a rolagem")
            }
        }
        if (dev.rpgsolo.engine.LuckKind.ADD_FACE in offer.kinds) {
            OutlinedButton(enabled = enabled, onClick = { onSpend(dev.rpgsolo.engine.LuckKind.ADD_FACE) }, modifier = Modifier.fillMaxWidth()) {
                Text("Somar uma face")
            }
        }
    }
}

@Composable
private fun InventoryPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
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
                        TextButton(enabled = enabled, onClick = { act({ model.unequipItem(item.slot) }) { message = it } }) { Text("Desequipar") }
                    item.kind == "equipamento" && item.slot != null ->
                        TextButton(enabled = enabled, onClick = { act({ model.equipItem(item.id) }) { message = it } }) { Text("Equipar") }
                    item.kind == "consumivel" ->
                        TextButton(enabled = enabled, onClick = { act({ model.useItem(item.id) }) { message = it } }) { Text("Usar") }
                }
                if (item.price != null && !item.equipped) {
                    TextButton(enabled = enabled, onClick = { act({ model.sellItem(item.id) }) { message = it } }) { Text("Vender (${item.price})") }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun ShopPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
    @Suppress("UNUSED_VARIABLE") val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val shop = model.shopView() ?: return
    val spellShop = model.spellShopView()
    if (shop.entries.isEmpty() && spellShop.isEmpty()) return
    AppCard {
        Text("Loja", style = MaterialTheme.typography.titleSmall)
        Muted("Saldo: ${shop.balance} (${shop.currencyName})")
        shop.goldName?.let { goldName ->
            Muted("${shop.goldHeld} de $goldName e ${shop.silverHeld} de ${shop.currencyName} · 1 $goldName = ${shop.silverPerGold} ${shop.currencyName}")
            Row {
                TextButton(enabled = enabled && shop.silverHeld >= shop.silverPerGold, onClick = { act({ model.convertCoins(toGold = true) }) { message = it } }) { Text("Trocar ${shop.silverPerGold} ${shop.currencyName} por 1 $goldName") }
                TextButton(enabled = enabled && shop.goldHeld >= 1, onClick = { act({ model.convertCoins(toGold = false) }) { message = it } }) { Text("Trocar 1 $goldName por ${shop.silverPerGold} ${shop.currencyName}") }
            }
        }
        shop.entries.forEach { entry ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("${entry.name} · ${entry.price}")
                    Muted(entry.description)
                }
                TextButton(enabled = enabled && shop.balance >= entry.price, onClick = { act({ model.buyItem(entry.id) }) { message = it } }) { Text("Comprar") }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun PowersPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
    val campaignVersion = model.campaignVersion
    @Suppress("UNUSED_VARIABLE") val observeVersion = campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    var targetId by remember { mutableStateOf<String?>(null) }
    val powers = model.powersView()
    if (powers.isEmpty()) return
    val targets = model.powerTargets()
    AppCard {
        Text("Poderes", style = MaterialTheme.typography.titleSmall)
        powers.forEach { power ->
            Column(Modifier.fillMaxWidth()) {
                Text(power.name)
                Muted(power.description)
                Muted("custo: ${power.cost} de ${power.resourceName}" + if (power.needsTarget) " · exige alvo" else "")
                if (power.needsTarget) {
                    if (targets.isEmpty()) Muted("Nenhum adversário disponível.")
                    targets.forEach { t ->
                        TextButton(enabled = enabled, onClick = { targetId = t.id }) {
                            Text((if (targetId == t.id) "● " else "○ ") + t.name)
                        }
                    }
                }
                TextButton(
                    enabled = enabled && (!power.needsTarget || targetId in targets.map { it.id }),
                    onClick = {
                        val id = power.id
                        val target = if (power.needsTarget) targetId else null
                        act({ model.activatePower(id, target) }) { message = it }
                    }
                ) { Text("Usar ${power.name}") }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun TraitUsesPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
    @Suppress("UNUSED_VARIABLE") val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val traits = model.traitUsesView()
    if (traits.isEmpty()) return
    AppCard {
        Text("Traços com usos", style = MaterialTheme.typography.titleSmall)
        traits.forEach { t ->
            Column(Modifier.fillMaxWidth()) {
                Text(t.name)
                if (t.description.isNotBlank()) Muted(t.description)
                Muted("usos: ${t.used}/${t.limit} · voltam ao descansar")
                TextButton(
                    enabled = enabled && t.used < t.limit,
                    onClick = { val id = t.id; act({ model.spendTraitUse(id) }) { message = it } }
                ) { Text("Usar ${t.name}") }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun ConditionRemovalPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
    @Suppress("UNUSED_VARIABLE") val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val list = model.removableConditions()
    if (list.isEmpty()) return
    AppCard {
        Text("Condições tratáveis", style = MaterialTheme.typography.titleSmall)
        list.forEach { (id, name) ->
            TextButton(enabled = enabled, onClick = { act({ model.removeCondition(id) }) { message = it } }) {
                Text("Tentar remover: $name")
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun SurvivalPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
    @Suppress("UNUSED_VARIABLE") val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val limit = model.survivalLimit() ?: return
    AppCard {
        Text("Você está caído", style = MaterialTheme.typography.titleSmall)
        Muted("falhas: ${model.survivalFailures()}/$limit · novo dano mata")
        TextButton(enabled = enabled, onClick = { act({ model.survivalTest() }) { message = it } }) {
            Text("Teste de sobrevivência")
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun PlacesPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
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
                            OutlinedButton(enabled = enabled, onClick = { act({ model.moveToPlace(id) }) { message = it } }) { Text("Ir para $name") }
                        }
                    }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun AdditionalRulesPanel(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
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
                        Button(enabled = enabled, onClick = { act({ model.approveAdditionalRule(rule.id) }) { message = it } }) { Text("Aprovar") }
                        TextButton(enabled = enabled, onClick = { act({ model.rejectAdditionalRule(rule.id) }) { message = it } }) { Text("Descartar") }
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
private fun AdvancedCheckControls(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
    val version = model.campaignVersion
    val options = remember(version) { model.advancedCheckOptions() }
    val targets = remember(version) { model.advancedCheckTargets() }
    var selectedCheck by remember(version) { mutableStateOf(options.firstOrNull()?.id.orEmpty()) }
    var selectedTarget by remember(version) { mutableStateOf(targets.firstOrNull()?.id.orEmpty()) }
    var selectedGroupTargets by remember(version) { mutableStateOf(emptySet<String>()) }
    var requiredGroupSuccesses by remember(version) { mutableIntStateOf(1) }
    var message by remember { mutableStateOf<String?>(null) }

    if (options.isEmpty() || targets.isEmpty()) return
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Disputa de testes", style = MaterialTheme.typography.labelLarge)
            Muted("Escolha o mesmo teste para seu personagem e o NPC. Os resultados são decididos pelo motor.")
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { option ->
                    AssistChip(
                        onClick = { selectedCheck = option.id },
                        enabled = enabled,
                        label = { Text(option.id) },
                        leadingIcon = if (selectedCheck == option.id) ({ Text("✓") }) else null
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                targets.forEach { target ->
                    AssistChip(
                        onClick = { selectedTarget = target.id },
                        enabled = enabled,
                        label = { Text(target.name + if (target.rulesValidated) "" else " · não validado") },
                        leadingIcon = if (selectedTarget == target.id) ({ Text("✓") }) else null
                    )
                }
            }
            Button(
                onClick = { val t = selectedTarget; val c = selectedCheck; act({ model.resolveOpposedCheck(t, c) }) { message = it } },
                enabled = enabled && selectedCheck in options.map { it.id } && selectedTarget in targets.map { it.id },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resolver disputa")
            }
            Text("Teste de grupo", style = MaterialTheme.typography.labelLarge)
            Muted("O herói participa automaticamente. Marque os NPCs que participam da tarefa.")
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                targets.forEach { target ->
                    val chosen = target.id in selectedGroupTargets
                    AssistChip(
                        onClick = {
                            selectedGroupTargets = if (chosen) selectedGroupTargets - target.id
                            else selectedGroupTargets + target.id
                            val count = selectedGroupTargets.size + 1
                            if (requiredGroupSuccesses > count) requiredGroupSuccesses = count
                        },
                        enabled = enabled,
                        label = { Text(target.name) },
                        leadingIcon = if (chosen) ({ Text("✓") }) else null
                    )
                }
            }
            val participantCount = selectedGroupTargets.size + 1
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..participantCount).forEach { threshold ->
                    AssistChip(
                        onClick = { requiredGroupSuccesses = threshold },
                        enabled = enabled,
                        label = { Text("$threshold sucesso(s)") },
                        leadingIcon = if (requiredGroupSuccesses == threshold) ({ Text("✓") }) else null
                    )
                }
            }
            Button(
                onClick = { val g = selectedGroupTargets.toList(); val c = selectedCheck; val n = requiredGroupSuccesses; act({ model.resolveGroupCheck(g, c, n) }) { message = it } },
                enabled = enabled && selectedCheck in options.map { it.id } && requiredGroupSuccesses in 1..participantCount,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resolver teste de grupo")
            }
            message?.let { Muted(it) }
        }
    }
}

@Composable
private fun CombatControls(model: AppModel, enabled: Boolean, act: (() -> String?, (String?) -> Unit) -> Unit) {
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
                        val name = npcName
                        act({ model.addNpc(name) }) { r ->
                            message = r
                            if (r?.contains("adicionado") == true) npcName = ""
                        }
                    }, enabled = enabled && npcName.isNotBlank()) { Text("Adicionar") }
                }
                Button(enabled = enabled, onClick = { act({ model.startCombat() }) { message = it } }, modifier = Modifier.fillMaxWidth()) {
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
                                Button(enabled = enabled, onClick = { val id = target.id; act({ model.attack(id) }) { message = it } }) {
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
                        enabled = enabled,
                        onClick = { act({ model.defend() }) { message = it } },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Defender — bônus contra o próximo ataque") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (model.isPlayerCombatTurn()) {
                        Button(enabled = enabled, onClick = { act({ model.nextCombatTurn() }) { message = it } }, modifier = Modifier.weight(1f)) { Text("Passar turno") }
                        OutlinedButton(enabled = enabled, onClick = { act({ model.fleeCombat() }) { message = it } }, modifier = Modifier.weight(1f)) { Text("Fugir") }
                    } else {
                        Button(enabled = enabled, onClick = { act({ model.resolveNpcTurn() }) { message = it } }, modifier = Modifier.weight(1f)) { Text("Resolver turno inimigo") }
                    }
                    TextButton(enabled = enabled, onClick = { act({ model.endCombat() }) { message = it } }) { Text("Encerrar") }
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
    busy: Boolean, pendingRoll: Boolean, pendingCheck: String?, fortuneSpendLabel: String?, text: String, onText: (String) -> Unit,
    suggestions: List<String>, warning: String?,
    onSend: () -> Unit, onRollCheck: () -> Unit, onRollFortune: () -> Unit, onUndo: () -> Unit, onRegenerate: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 3.dp) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            warning?.let { ErrorText("Não consegui decidir se a ação exige teste ($it). Seguindo sem teste.") }
            if (busy) Thinking()
            if (pendingRoll && !busy) Text("🎲 Role os dados para continuar.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            // O mestre pediu um teste: só resta rolar (ou desfazer). O motor rola e o mestre narra o desfecho.
            if (pendingCheck != null && !busy && !pendingRoll) {
                Button(onClick = onRollCheck, modifier = Modifier.fillMaxWidth()) { Text("🎲 Rolar $pendingCheck") }
                if (fortuneSpendLabel != null) {
                    OutlinedButton(onClick = onRollFortune, modifier = Modifier.fillMaxWidth()) {
                        Text("🎲 Gastar $fortuneSpendLabel")
                    }
                }
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
