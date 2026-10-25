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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.Switch
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rpgsolo.R
import dev.rpgsolo.ai.Author
import dev.rpgsolo.ai.ChatLine
import dev.rpgsolo.ai.PanelState
import dev.rpgsolo.ai.PlayController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Aba Jogar (10.1): chat com o mestre, rolagem animada de dados e painel rápido do personagem. */
@Composable
fun PlayScreen(
    model: AppModel,
    onNavigate: (Tab) -> Unit = {},
) {
    val setup = model.setupStatus()
    if (!setup.complete) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            AppCard {
                Text("🎲", fontSize = 40.sp)
                Text(stringResource(R.string.play_setup_title), style = MaterialTheme.typography.titleLarge)
                Muted(stringResource(R.string.play_setup_help))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (setup.providerReady) {
                        Muted(stringResource(R.string.play_step1_done))
                    } else {
                        Muted(stringResource(R.string.play_step1))
                        OutlinedButton(onClick = { onNavigate(Tab.CONFIG) }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.play_step1_button))
                        }
                    }
                    if (setup.heroReady) {
                        Muted(stringResource(R.string.play_step2_done))
                    } else {
                        Muted(stringResource(R.string.play_step2))
                        OutlinedButton(onClick = { onNavigate(Tab.PERSONAGENS) }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.play_step2_button))
                        }
                    }
                    if (setup.campaignReady) {
                        Muted(stringResource(R.string.play_step3_done))
                    } else {
                        Muted(stringResource(R.string.play_step3))
                        Button(onClick = { onNavigate(Tab.CAMPANHAS) }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.play_step3_button))
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
                Text(stringResource(R.string.play_master_failed), style = MaterialTheme.typography.titleLarge)
                Muted(model.configError ?: stringResource(R.string.play_master_failed_help))
                OutlinedButton(onClick = { onNavigate(Tab.CONFIG) }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.play_open_config))
                }
            }
        }
        return
    }
    PlayContent(c, model, model.classifierWarning)
}

@Composable
private fun PlayContent(
    c: PlayController,
    model: AppModel,
    classifierWarning: String?,
) {
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

    val visible =
        lines.withIndex().filter { (i, line) ->
            val hasHiddenRollBeforeNarrative =
                if (line.author == Author.MASTER) {
                    var cursor = i - 1
                    var hidden = false
                    while (cursor >= 0 && lines[cursor].roll != null) {
                        if (!c.isRollRevealed(cursor)) hidden = true
                        cursor--
                    }
                    hidden
                } else {
                    false
                }
            !hasHiddenRollBeforeNarrative
        }
    val pendingRoll = lines.withIndex().any { (i, l) -> l.roll != null && !c.isRollRevealed(i) }
    val showsStreaming = busy && streamingText.isNotBlank()
    val feedItemCount =
        13 + (if (luckOffer != null) 1 else 0) + 1 + (if (lines.isEmpty()) 1 else visible.size) + clashes.size +
            (if (showsStreaming) 1 else 0)

    LaunchedEffect(feedItemCount, pendingRoll, streamingText.length, busy) {
        if (visible.isEmpty() && !showsStreaming) return@LaunchedEffect
        val itemCount = snapshotFlow { listState.layoutInfo.totalItemsCount }.first { it >= feedItemCount }
        listState.animateScrollToItem(itemCount - 1)
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
    fun act(
        block: () -> String?,
        onResult: (String?) -> Unit,
    ) {
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
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "inventario") { InventoryPanel(model, enabled = !busy, act = ::act) }
            item(key = "loja") { ShopPanel(model, enabled = !busy, act = ::act) }
            item(key = "poderes") { PowersPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "arvore-pericias") { SkillTreePanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "maldições") { CursePanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "oficios") { RecipesPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "marcas") { TattooPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "gemas") { GemsPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "companheiro") { CompanionPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "usos-tracos") { TraitUsesPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "remover-condicoes") {
                ConditionRemovalPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act)
            }
            item(key = "legado") { LegacyPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            item(key = "sobrevivencia") { SurvivalPanel(model, enabled = !busy && !pendingRoll && pendingCheck == null, act = ::act) }
            luckOffer?.let { offer ->
                item(key = "fortuna") {
                    LuckPanel(
                        offer = offer,
                        resourceName = model.resourceName(offer.resource),
                        enabled = !busy && !pendingRoll && pendingCheck == null,
                        onSpend = { kind ->
                            run {
                                val result =
                                    c.spendLuck(kind) { partial ->
                                        scope.launch(Dispatchers.Main) {
                                            streamingText = partial
                                            version++
                                        }
                                    }
                                withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    streamingText = ""
                                    playError = (result as? dev.rpgsolo.ai.PlayResult.Failed)?.reason
                                }
                            }
                        },
                    )
                }
            }
            item(key = "regras-adicionais") { AdditionalRulesPanel(model, enabled = !busy, act = ::act) }
            item(key = "locais") { PlacesPanel(model, enabled = !busy, act = ::act) }
            item(key = "acoes-rapidas") {
                QuickActions(
                    enabled = !busy && !pendingRoll && pendingCheck == null,
                    onSheet = { run { c.submit("ver ficha") } },
                    onInventory = { run { c.submit("inventário") } },
                    onRest = { run { c.submit("descansar") } },
                )
            }
            item(key = "testes-avancados") {
                AdvancedCheckControls(
                    model,
                    enabled = !busy && !pendingRoll && pendingCheck == null,
                    act = ::act,
                )
            }
            item(key = "combate") { CombatControls(model, enabled = !busy, act = ::act) }
            if (lines.isEmpty()) {
                item(key = "vazio") {
                    Muted(
                        "Descreva o que seu personagem faz. O mestre responde, e quando a ação exigir um teste, é você quem rola os dados.",
                    )
                }
            }
            item(key = "historico-rolagens") {
                RollHistory(lines.withIndex().filter { (i, l) -> l.roll != null && c.isRollRevealed(i) }.map { it.value.roll!! })
            }
            itemsIndexed(visible, key = { _, iv -> iv.index }) { _, iv ->
                ChatItem(
                    iv.value,
                    revealed = c.isRollRevealed(iv.index),
                    onRevealed = {
                        c.revealRoll(iv.index)
                        version++
                    },
                )
            }
            itemsIndexed(clashes, key = { index, _ -> "clash-$index" }) { _, report ->
                ClashCard(report, c.combatantPublicName(report.attackerId), c.combatantPublicName(report.targetId))
            }
            if (busy && streamingText.isNotBlank()) {
                item(key = "streaming-narration") {
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(modifier = Modifier.width(28.dp))
                            Text(stringResource(R.string.chat_narrating), style = MaterialTheme.typography.labelMedium)
                        }
                        Text(streamingText)
                    }
                }
            }
        }

        playError?.let { ErrorText(it) }
        BottomBar(
            busy = busy, pendingRoll = pendingRoll, pendingCheck = pendingCheck,
            fortuneSpendLabel =
                fortuneSpendOption?.let {
                    "${it.cost} ${it.resourceName} para " +
                        when (it.mode) {
                            dev.rpgsolo.engine.FortuneMode.ADVANTAGE -> "vantagem"
                            dev.rpgsolo.engine.FortuneMode.DISADVANTAGE -> "desvantagem"
                            dev.rpgsolo.engine.FortuneMode.NORMAL -> "fortuna"
                        }
                },
            text = text,
            onText = {
                text = it
                playError = null
            },
            suggestions = suggestions, warning = classifierWarning,
            onSend = {
                val submitted = text
                run {
                    val result =
                        c.submit(submitted) { partial ->
                            scope.launch(Dispatchers.Main) {
                                streamingText = partial
                                version++
                            }
                        }
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        streamingText = ""
                        if (result is dev.rpgsolo.ai.PlayResult.Failed) {
                            playError = result.reason
                        } else {
                            text = ""
                            playError = null
                        }
                    }
                    model.autoCloseChapterIfNeeded()
                }
            },
            onRollCheck = {
                run {
                    val result =
                        c.rollPending { partial ->
                            scope.launch(Dispatchers.Main) {
                                streamingText = partial
                                version++
                            }
                        }
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        streamingText = ""
                        playError = (result as? dev.rpgsolo.ai.PlayResult.Failed)?.reason
                    }
                    model.autoCloseChapterIfNeeded()
                }
            },
            onRollFortune = {
                run {
                    val result =
                        c.rollPending(useFortuneResource = true) { partial ->
                            scope.launch(Dispatchers.Main) {
                                streamingText = partial
                                version++
                            }
                        }
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        streamingText = ""
                        playError = (result as? dev.rpgsolo.ai.PlayResult.Failed)?.reason
                    }
                    model.autoCloseChapterIfNeeded()
                }
            },
            onUndo = {
                run {
                    val undone = c.undo()
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        playError = if (undone) null else "Não há jogada para desfazer."
                    }
                }
            },
            onRegenerate = {
                run {
                    val result = c.regenerate()
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        playError = (result as? dev.rpgsolo.ai.PlayResult.Failed)?.reason
                    }
                }
            },
        )
    }
}

/** F3.4: sorte/destino gasto depois da rolagem do último teste; custo e formas vêm do pacote. */
@Composable
private fun LuckPanel(
    offer: dev.rpgsolo.ai.LuckOption,
    resourceName: String,
    enabled: Boolean,
    onSpend: (dev.rpgsolo.engine.LuckKind) -> Unit,
) {
    AppCard {
        Text(stringResource(R.string.luck_title), style = MaterialTheme.typography.titleSmall)
        Muted(stringResource(R.string.luck_help, offer.cost, resourceName))
        if (dev.rpgsolo.engine.LuckKind.REROLL in offer.kinds) {
            OutlinedButton(
                enabled = enabled,
                onClick = { onSpend(dev.rpgsolo.engine.LuckKind.REROLL) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.luck_reroll))
            }
        }
        if (dev.rpgsolo.engine.LuckKind.ADD_FACE in offer.kinds) {
            OutlinedButton(
                enabled = enabled,
                onClick = { onSpend(dev.rpgsolo.engine.LuckKind.ADD_FACE) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.luck_add_face))
            }
        }
    }
}

@Composable
private fun InventoryPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    val campaignVersion = model.campaignVersion

    @Suppress("UNUSED_VARIABLE")
    val observeVersion = campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val inventory = model.inventoryView()
    if (inventory.isEmpty()) return
    AppCard {
        Text(stringResource(R.string.inventory_title), style = MaterialTheme.typography.titleSmall)
        inventory.forEach { item ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.inventory_item, item.name, item.quantity))
                    Muted(item.description)
                    if (item.kind == "equipamento" && item.slot != null) {
                        Muted(
                            stringResource(R.string.inventory_slot, item.slot) +
                                (if (item.equipped) stringResource(R.string.inventory_equipped_suffix) else "") +
                                (if (item.restricted) stringResource(R.string.inventory_restricted_suffix) else ""),
                        )
                    } else {
                        Muted(stringResource(R.string.inventory_kind, item.kind))
                    }
                }
                when {
                    item.kind == "equipamento" && item.slot != null && item.equipped ->
                        TextButton(
                            enabled = enabled,
                            onClick = { act({ model.unequipItem(item.slot) }) { message = it } },
                        ) { Text(stringResource(R.string.inventory_unequip)) }
                    item.kind == "equipamento" && item.slot != null ->
                        TextButton(enabled = enabled, onClick = {
                            act({ model.equipItem(item.id) }) { message = it }
                        }) { Text(stringResource(R.string.inventory_equip)) }
                    item.kind == "consumivel" ->
                        TextButton(enabled = enabled, onClick = {
                            act({ model.useItem(item.id) }) { message = it }
                        }) { Text(stringResource(R.string.inventory_use)) }
                }
                if (item.price != null && !item.equipped) {
                    TextButton(
                        enabled = enabled,
                        onClick = { act({ model.sellItem(item.id) }) { message = it } },
                    ) { Text(stringResource(R.string.inventory_sell, item.price)) }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun ShopPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val shop = model.shopView() ?: return
    val spellShop = model.spellShopView()
    if (shop.entries.isEmpty() && spellShop.isEmpty()) return
    AppCard {
        Text(stringResource(R.string.shop_title), style = MaterialTheme.typography.titleSmall)
        Muted(stringResource(R.string.shop_balance, shop.balance, shop.currencyName))
        shop.goldName?.let { goldName ->
            Muted(
                stringResource(R.string.shop_holdings, shop.goldHeld, goldName, shop.silverHeld, shop.currencyName, shop.silverPerGold),
            )
            Row {
                TextButton(enabled = enabled && shop.silverHeld >= shop.silverPerGold, onClick = {
                    act({ model.convertCoins(toGold = true) }) { message = it }
                }) { Text(stringResource(R.string.shop_to_gold, shop.silverPerGold, shop.currencyName, goldName)) }
                TextButton(enabled = enabled && shop.goldHeld >= 1, onClick = {
                    act({
                        model.convertCoins(toGold = false)
                    }) { message = it }
                }) { Text(stringResource(R.string.shop_to_silver, goldName, shop.silverPerGold, shop.currencyName)) }
            }
        }
        shop.entries.forEach { entry ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.shop_entry, entry.name, entry.price))
                    Muted(entry.description)
                }
                TextButton(enabled = enabled && shop.balance >= entry.price, onClick = {
                    act({ model.buyItem(entry.id) }) { message = it }
                }) { Text(stringResource(R.string.shop_buy)) }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun PowersPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    val campaignVersion = model.campaignVersion

    @Suppress("UNUSED_VARIABLE")
    val observeVersion = campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    var targetId by remember { mutableStateOf<String?>(null) }
    val powers = model.powersView()
    if (powers.isEmpty()) return
    val targets = model.powerTargets()
    AppCard {
        Text(stringResource(R.string.powers_title), style = MaterialTheme.typography.titleSmall)
        powers.forEach { power ->
            Column(Modifier.fillMaxWidth()) {
                Text(power.name)
                Muted(power.description)
                Muted(
                    stringResource(R.string.powers_cost, power.cost, power.resourceName) + if (power.needsTarget) stringResource(R.string.powers_needs_target) else "",
                )
                if (power.needsTarget) {
                    if (targets.isEmpty()) Muted(stringResource(R.string.powers_no_targets))
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
                    },
                ) { Text(stringResource(R.string.powers_use, power.name)) }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun RecipesPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    var open by remember { mutableStateOf(false) }
    val recipes = model.recipesView()
    if (recipes.isEmpty()) return
    AppCard {
        TextButton(onClick = { open = !open }) {
            Text(stringResource(R.string.recipes_title, recipes.size), style = MaterialTheme.typography.titleSmall)
        }
        if (open) {
            recipes.forEach { r ->
                Column(Modifier.fillMaxWidth()) {
                    Text(r.name)
                    Muted(
                        if (r.inputs.isBlank()) {
                            stringResource(R.string.recipes_gives, r.outputs)
                        } else {
                            stringResource(R.string.recipes_uses_gives, r.inputs, r.outputs)
                        },
                    )
                    TextButton(
                        enabled = enabled,
                        onClick = {
                            val id = r.id
                            act({ model.craftRecipe(id) }) { message = it }
                        },
                    ) { Text(stringResource(R.string.recipes_do, r.name)) }
                }
            }
        }
        message?.let { Muted(it) }
    }
}

@Composable
private fun CompanionPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    var open by remember { mutableStateOf(false) }
    var tameName by remember { mutableStateOf("") }
    val pets = model.companionViews()
    val options = model.companionTameOptions()
    val lostPets = model.companionLostViews()
    if (pets.isEmpty() && options.isEmpty() && lostPets.isEmpty()) return
    AppCard {
        TextButton(onClick = {
            open = !open
            model.dismissCompanionNotice()
        }) {
            Text(stringResource(R.string.companion_title, pets.size), style = MaterialTheme.typography.titleSmall)
        }
        model.companionNotice?.let { Muted(it) }
        if (open) {
            options.forEach { o ->
                Column(Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.companion_tame, o.itemName))
                    OutlinedTextField(
                        value = tameName,
                        onValueChange = { tameName = it },
                        label = { Text(stringResource(R.string.companion_name)) },
                    )
                    model.activePackageCompanionBodies().forEach { (bodyId, bodyName) ->
                        TextButton(
                            enabled = enabled,
                            onClick = { act({ model.tameCompanion(o.itemId, tameName, bodyId) }) { message = it } },
                        ) { Text(bodyName) }
                    }
                }
            }
            lostPets.forEach { l ->
                Column(Modifier.fillMaxWidth()) {
                    Text(l.name, style = MaterialTheme.typography.titleSmall)
                    Muted(stringResource(R.string.companion_lost, l.level, l.attemptsLeft))
                    if (l.options.isEmpty()) Muted(stringResource(R.string.companion_retrain_need_item))
                    l.options.forEach { o ->
                        TextButton(
                            enabled = enabled && !model.inCombatNow(),
                            onClick = { act({ model.companionRetrain(l.id, o.itemId) }) { message = it } },
                        ) {
                            Text(stringResource(R.string.companion_retrain, o.itemName, o.chance))
                        }
                    }
                }
            }
            pets.forEach { p ->
                Column(Modifier.fillMaxWidth()) {
                    Text(p.name, style = MaterialTheme.typography.titleSmall)
                    Muted(stringResource(R.string.companion_top, p.bodyName, p.level, p.hp, p.hpMax, p.loyalty))
                    if (p.stabled) {
                        Muted(stringResource(R.string.companion_stabled))
                        if (p.canVisit) {
                            TextButton(enabled = enabled, onClick = { act({ model.companionVisit(p.id) }) { message = it } }) {
                                Text(stringResource(R.string.companion_visit))
                            }
                        }
                        TextButton(
                            enabled = enabled && !model.inCombatNow(),
                            onClick = { act({ model.companionBring(p.id) }) { message = it } },
                        ) {
                            Text(stringResource(R.string.companion_bring, p.bringFee))
                        }
                    } else {
                        Muted(
                            stringResource(
                                R.string.companion_training,
                                p.trainingLevel,
                                p.trainingLevel * dev.rpgsolo.engine.Companions.TRAINING_BONUS_PER_LEVEL,
                            ),
                        )
                        p.stats.forEach { (id, v) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${model.companionStatLabel(id)}: $v", Modifier.weight(1f))
                                if (p.freeStatPoints > 0) {
                                    TextButton(
                                        enabled = enabled,
                                        onClick = { act({ model.companionAllocate(p.id, id) }) { message = it } },
                                    ) { Text("+") }
                                }
                            }
                        }
                        if (p.freeStatPoints > 0) Muted(stringResource(R.string.companion_free_points, p.freeStatPoints))
                        if (p.attackId == null) {
                            Muted(stringResource(R.string.companion_pick_attack))
                            p.attackOptions.forEach { (id, name) ->
                                TextButton(
                                    enabled = enabled,
                                    onClick = { act({ model.companionChooseAttack(p.id, id) }) { message = it } },
                                ) { Text(name) }
                            }
                        } else {
                            Text(p.attackLine)
                            if (p.abilityPoints > 0) {
                                TextButton(enabled = enabled, onClick = { act({ model.companionRaiseAttack(p.id) }) { message = it } }) {
                                    Text(stringResource(R.string.companion_raise_attack, p.abilityPoints))
                                }
                            }
                            if (p.isTurn) {
                                p.enemies.forEach { (id, name) ->
                                    TextButton(enabled = enabled, onClick = { act({ model.companionStrike(p.id, id) }) { message = it } }) {
                                        Text(stringResource(R.string.companion_strike, name))
                                    }
                                }
                            }
                        }
                        p.knownTricks.filter { !model.inCombatNow() || it.combat }.forEach { t ->
                            TextButton(enabled = enabled, onClick = { act({ model.companionOrderTrick(p.id, t.id) }) { message = it } }) {
                                Text(stringResource(R.string.companion_order, t.name, t.chancePercent))
                            }
                        }
                        if (!model.inCombatNow()) {
                            p.learnableTricks.forEach { t ->
                                TextButton(
                                    enabled = enabled,
                                    onClick = { act({ model.companionLearnTrick(p.id, t.id) }) { message = it } },
                                ) {
                                    Text(stringResource(R.string.companion_learn, t.name, t.chancePercent))
                                }
                            }
                            p.foods.forEach { (id, name) ->
                                TextButton(enabled = enabled, onClick = { act({ model.companionFeed(p.id, id) }) { message = it } }) {
                                    Text(stringResource(R.string.companion_feed, name))
                                }
                            }
                            if (p.canPet) {
                                TextButton(enabled = enabled, onClick = { act({ model.companionPet(p.id) }) { message = it } }) {
                                    Text(stringResource(R.string.companion_pet))
                                }
                            }
                        }
                    }
                }
            }
        }
        message?.let { Muted(it) }
    }
}

@Composable
private fun TattooPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    var open by remember { mutableStateOf(false) }
    var viaNpc by remember { mutableStateOf(false) }
    var npcGrade by remember { mutableStateOf(1) }
    var cover by remember { mutableStateOf<Int?>(null) }
    val view = model.tattooView()
    if (view.heroGrade == 0 && view.marks.isEmpty()) return
    val grade = if (viaNpc || view.heroGrade == 0) npcGrade else view.heroGrade
    val free = enabled && !model.inCombatNow()
    AppCard {
        TextButton(onClick = { open = !open }) {
            Text(stringResource(R.string.tattoo_title, view.marks.size, view.limit), style = MaterialTheme.typography.titleSmall)
        }
        if (open) {
            view.marks.forEachIndexed { i, m ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.tattoo_mark, model.tattooScopeLabel(m.scope), m.grade), Modifier.weight(1f))
                    if (view.isFull && m.grade < grade) {
                        TextButton(enabled = free, onClick = { cover = if (cover == i) null else i }) {
                            Text(stringResource(if (cover == i) R.string.tattoo_cover_selected else R.string.tattoo_cover))
                        }
                    }
                }
            }
            if (view.heroGrade > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = viaNpc, onCheckedChange = { viaNpc = it })
                    Text(stringResource(R.string.tattoo_via_npc), Modifier.padding(start = 8.dp))
                }
            }
            if (viaNpc || view.heroGrade == 0) {
                Row {
                    (1..dev.rpgsolo.engine.Tattoos.MAX_GRADE).forEach { g ->
                        TextButton(onClick = { npcGrade = g }) { Text(if (g == npcGrade) "[$g]" else "$g") }
                    }
                }
            }
            val coverIndex = cover
            if (!view.isFull || coverIndex != null) {
                Muted(stringResource(R.string.tattoo_price, dev.rpgsolo.engine.Tattoos.price(grade)))
                dev.rpgsolo.engine.Tattoos.SCOPES.forEach { scope ->
                    TextButton(enabled = free, onClick = {
                        act({ model.tattooHero(scope, grade, coverIndex) }) {
                            message = it ?: ""
                            cover = null
                        }
                    }) {
                        Text(stringResource(R.string.tattoo_apply, model.tattooScopeLabel(scope), grade))
                    }
                }
            } else {
                Muted(stringResource(R.string.tattoo_full))
            }
        }
        message?.takeIf { it.isNotEmpty() }?.let { Muted(it) }
    }
}

@Composable
private fun GemsPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    var open by remember { mutableStateOf(false) }
    var viaNpc by remember { mutableStateOf(false) }
    val view = model.gemsView()
    if (view.isEmpty) return
    val free = enabled && !model.inCombatNow()
    AppCard {
        TextButton(onClick = { open = !open }) {
            Text(stringResource(R.string.gems_title, view.stock.sumOf { it.quantity }), style = MaterialTheme.typography.titleSmall)
        }
        if (open) {
            if (!view.canSet) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = viaNpc, onCheckedChange = { viaNpc = it })
                    Text(stringResource(R.string.gems_via_npc), Modifier.padding(start = 8.dp))
                }
                if (!viaNpc) Muted(stringResource(R.string.gems_need_setter))
            }
            val useNpc = viaNpc && !view.canSet
            val allowed = free && (view.canSet || useNpc)
            view.slots.forEach { slot ->
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.gems_slot, slot.itemName, slot.gems.size, slot.sockets),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    slot.gems.forEachIndexed { index, name ->
                        TextButton(enabled = allowed, onClick = { act({ model.removeGem(slot.slot, index, useNpc) }) { message = it } }) {
                            Text(stringResource(R.string.gems_remove, name))
                        }
                    }
                    if (slot.gems.size < slot.sockets) {
                        view.stock.forEach { g ->
                            TextButton(
                                enabled = allowed,
                                onClick = { act({ model.socketGem(slot.slot, g.itemId, useNpc) }) { message = it } },
                            ) {
                                Text(stringResource(R.string.gems_socket, g.name, if (useNpc) g.npcFee else 0))
                            }
                        }
                    }
                }
            }
            view.stock.filter { it.canCut }.forEach { g ->
                TextButton(enabled = free, onClick = { act({ model.cutGem(g.itemId) }) { message = it } }) {
                    Text(stringResource(R.string.gems_cut, g.name, g.quantity))
                }
            }
            Muted(stringResource(R.string.gems_rules))
        }
        message?.let { Muted(it) }
    }
}

@Composable
private fun TraitUsesPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val traits = model.traitUsesView()
    if (traits.isEmpty()) return
    AppCard {
        Text(stringResource(R.string.traituses_title), style = MaterialTheme.typography.titleSmall)
        traits.forEach { t ->
            Column(Modifier.fillMaxWidth()) {
                Text(t.name)
                if (t.description.isNotBlank()) Muted(t.description)
                Muted(stringResource(R.string.traituses_info, t.used, t.limit))
                TextButton(
                    enabled = enabled && t.used < t.limit,
                    onClick = {
                        val id = t.id
                        act({ model.spendTraitUse(id) }) { message = it }
                    },
                ) { Text(stringResource(R.string.powers_use, t.name)) }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun ConditionRemovalPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val list = model.removableConditions()
    if (list.isEmpty()) return
    AppCard {
        Text(stringResource(R.string.conditions_title), style = MaterialTheme.typography.titleSmall)
        list.forEach { (id, name) ->
            TextButton(enabled = enabled, onClick = { act({ model.removeCondition(id) }) { message = it } }) {
                Text(stringResource(R.string.conditions_try_remove, name))
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun LegacyPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val view = model.legacyView() ?: return
    AppCard {
        Text(stringResource(R.string.legacy_title, view.heroName), style = MaterialTheme.typography.titleSmall)
        if (view.endedReason != null) {
            Muted(stringResource(R.string.legacy_ended, view.endedReason))
        } else if (view.rebirthItemName != null) {
            Muted(stringResource(R.string.legacy_rebirth_hint))
            TextButton(enabled = enabled, onClick = { act({ model.legacyRebirth() }) { message = it } }) {
                Text(stringResource(R.string.legacy_rebirth, view.rebirthItemName))
            }
        } else if (view.npcs.isNotEmpty()) {
            Muted(stringResource(R.string.legacy_assume_hint))
            view.npcs.forEach { npc ->
                Column(Modifier.fillMaxWidth()) {
                    Text(npc.name, style = MaterialTheme.typography.titleSmall)
                    if (npc.known.isNotEmpty()) Muted(npc.known.joinToString("; "))
                    TextButton(enabled = enabled, onClick = { act({ model.legacyAssume(npc.id) }) { message = it } }) {
                        Text(stringResource(R.string.legacy_assume, npc.name))
                    }
                }
            }
        } else {
            Muted(stringResource(R.string.legacy_none))
            TextButton(enabled = enabled, onClick = { act({ model.legacyEnd() }) { message = it } }) {
                Text(stringResource(R.string.legacy_end))
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun SurvivalPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val observeVersion = model.campaignVersion
    var message by remember { mutableStateOf<String?>(null) }
    val limit = model.survivalLimit() ?: return
    AppCard {
        Text(stringResource(R.string.survival_title), style = MaterialTheme.typography.titleSmall)
        Muted(stringResource(R.string.survival_failures, model.survivalFailures(), limit))
        TextButton(enabled = enabled, onClick = { act({ model.survivalTest() }) { message = it } }) {
            Text(stringResource(R.string.survival_test))
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun PlacesPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    val places = model.placesView()
    var message by remember { mutableStateOf<String?>(null) }
    if (places.isEmpty()) return
    AppCard {
        Text(stringResource(R.string.places_title), style = MaterialTheme.typography.titleSmall)
        places.forEach { place ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(place.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (place.current) Pill(stringResource(R.string.places_here), MaterialTheme.colorScheme.tertiary)
                }
                if (place.description.isNotBlank()) Text(place.description)
                if (place.current && place.exits.isNotEmpty()) {
                    Muted(stringResource(R.string.places_exits))
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        place.exits.forEach { (id, name) ->
                            OutlinedButton(
                                enabled = enabled,
                                onClick = { act({ model.moveToPlace(id) }) { message = it } },
                            ) { Text(stringResource(R.string.places_go, name)) }
                        }
                    }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

@Composable
private fun AdditionalRulesPanel(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    val campaignVersion = model.campaignVersion

    @Suppress("UNUSED_VARIABLE")
    val observeVersion = campaignVersion
    val rules = model.additionalRulesView()
    var message by remember { mutableStateOf<String?>(null) }
    if (rules.isEmpty()) return
    AppCard {
        Text(stringResource(R.string.rules_title), style = MaterialTheme.typography.titleSmall)
        Muted(stringResource(R.string.rules_help))
        rules.forEach { rule ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(rule.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    if (rule.approved) {
                        Pill(stringResource(R.string.rules_approved), MaterialTheme.colorScheme.tertiary)
                    } else {
                        Pill(stringResource(R.string.rules_pending), MaterialTheme.colorScheme.error)
                    }
                }
                Text(rule.description)
                Muted(
                    stringResource(R.string.rules_effects_prefix) +
                        rule.effects.entries.joinToString(", ") { (key, value) ->
                            "$key ${if (value >= 0) "+" else ""}$value"
                        },
                )
                Muted(stringResource(R.string.rules_limit, rule.limit))
                if (!rule.approved) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            enabled = enabled,
                            onClick = { act({ model.approveAdditionalRule(rule.id) }) { message = it } },
                        ) { Text(stringResource(R.string.rules_approve)) }
                        TextButton(
                            enabled = enabled,
                            onClick = { act({ model.rejectAdditionalRule(rule.id) }) { message = it } },
                        ) { Text(stringResource(R.string.rules_discard)) }
                    }
                }
            }
        }
        message?.let { ErrorText(it) }
    }
}

/** Atalhos de baixo custo: o controlador reconhece estas frases como ações locais, sem chamar a IA. */
@Composable
private fun QuickActions(
    enabled: Boolean,
    onSheet: () -> Unit,
    onInventory: () -> Unit,
    onRest: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AssistChip(onClick = onSheet, enabled = enabled, label = { Text(stringResource(R.string.quick_sheet)) })
            AssistChip(onClick = onInventory, enabled = enabled, label = { Text(stringResource(R.string.quick_inventory)) })
            AssistChip(onClick = onRest, enabled = enabled, label = { Text(stringResource(R.string.quick_rest)) })
        }
    }
}

@Composable
private fun AdvancedCheckControls(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
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
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(stringResource(R.string.advcheck_opposed_title), style = MaterialTheme.typography.labelLarge)
            Muted(stringResource(R.string.advcheck_opposed_help))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                options.forEach { option ->
                    AssistChip(
                        onClick = { selectedCheck = option.id },
                        enabled = enabled,
                        label = { Text(option.id) },
                        leadingIcon = if (selectedCheck == option.id) ({ Text("✓") }) else null,
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                targets.forEach { target ->
                    AssistChip(
                        onClick = { selectedTarget = target.id },
                        enabled = enabled,
                        label = {
                            Text(
                                target.name +
                                    if (target.rulesValidated) "" else stringResource(R.string.advcheck_not_validated),
                            )
                        },
                        leadingIcon = if (selectedTarget == target.id) ({ Text("✓") }) else null,
                    )
                }
            }
            Button(
                onClick = {
                    val t = selectedTarget
                    val c = selectedCheck
                    act({ model.resolveOpposedCheck(t, c) }) { message = it }
                },
                enabled = enabled && selectedCheck in options.map { it.id } && selectedTarget in targets.map { it.id },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.advcheck_resolve_opposed))
            }
            Text(stringResource(R.string.advcheck_group_title), style = MaterialTheme.typography.labelLarge)
            Muted(stringResource(R.string.advcheck_group_help))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                targets.forEach { target ->
                    val chosen = target.id in selectedGroupTargets
                    AssistChip(
                        onClick = {
                            selectedGroupTargets =
                                if (chosen) {
                                    selectedGroupTargets - target.id
                                } else {
                                    selectedGroupTargets + target.id
                                }
                            val count = selectedGroupTargets.size + 1
                            if (requiredGroupSuccesses > count) requiredGroupSuccesses = count
                        },
                        enabled = enabled,
                        label = { Text(target.name) },
                        leadingIcon = if (chosen) ({ Text("✓") }) else null,
                    )
                }
            }
            val participantCount = selectedGroupTargets.size + 1
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                (1..participantCount).forEach { threshold ->
                    AssistChip(
                        onClick = { requiredGroupSuccesses = threshold },
                        enabled = enabled,
                        label = { Text(stringResource(R.string.advcheck_successes, threshold)) },
                        leadingIcon = if (requiredGroupSuccesses == threshold) ({ Text("✓") }) else null,
                    )
                }
            }
            Button(
                onClick = {
                    val g = selectedGroupTargets.toList()
                    val c = selectedCheck
                    val n = requiredGroupSuccesses
                    act({ model.resolveGroupCheck(g, c, n) }) { message = it }
                },
                enabled = enabled && selectedCheck in options.map { it.id } && requiredGroupSuccesses in 1..participantCount,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.advcheck_resolve_group))
            }
            message?.let { Muted(it) }
        }
    }
}

@Composable
private fun CombatControls(
    model: AppModel,
    enabled: Boolean,
    act: (() -> String?, (String?) -> Unit) -> Unit,
) {
    val campaignVersion = model.campaignVersion

    @Suppress("UNUSED_VARIABLE")
    val observeVersion = campaignVersion
    var message by remember(campaignVersion) { mutableStateOf<String?>(null) }
    var npcName by remember { mutableStateOf("") }
    val combat = model.combatState()
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (combat == null) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = npcName,
                        onValueChange = { npcName = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(R.string.combat_enemy_name)) },
                        singleLine = true,
                    )
                    Button(onClick = {
                        val name = npcName
                        act({ model.addNpc(name) }) { r ->
                            message = r
                            if (r?.contains("adicionado") == true) npcName = ""
                        }
                    }, enabled = enabled && npcName.isNotBlank()) { Text(stringResource(R.string.combat_add)) }
                }
                Button(enabled = enabled, onClick = { act({ model.startCombat() }) { message = it } }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.combat_start))
                }
            } else {
                Text(
                    stringResource(
                        R.string.combat_turn,
                        combat.round,
                        combat.turnIndex + 1,
                        combat.order.size,
                        model.combatantName(combat.current),
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
                model.combatOrderDescription()?.let { order ->
                    Muted(stringResource(R.string.combat_initiative, order))
                }
                if (model.isPlayerCombatTurn()) {
                    val targets = model.combatTargets()
                    if (targets.isEmpty()) {
                        Muted(stringResource(R.string.combat_no_targets))
                    } else {
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            targets.forEach { target ->
                                Button(enabled = enabled, onClick = {
                                    val id = target.id
                                    act({ model.unifiedAttack(id) }) { message = it }
                                }) {
                                    Text(
                                        stringResource(R.string.combat_attack, target.name) +
                                            target.condition.takeIf { it.isNotBlank() }
                                                ?.let { stringResource(R.string.combat_condition_suffix, it) }.orEmpty() +
                                            if (target.rulesValidated) "" else stringResource(R.string.combat_not_validated),
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Muted(stringResource(R.string.combat_wait, model.combatantName(combat.current)))
                }
                if (model.isPlayerCombatTurn() && model.canDefend()) {
                    OutlinedButton(
                        enabled = enabled,
                        onClick = { act({ model.defend() }) { message = it } },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.combat_defend)) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (model.isPlayerCombatTurn()) {
                        Button(enabled = enabled, onClick = {
                            act({ model.nextCombatTurn() }) { message = it }
                        }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.combat_pass)) }
                        OutlinedButton(enabled = enabled, onClick = {
                            act({ model.fleeCombat() }) { message = it }
                        }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.combat_flee)) }
                    } else {
                        Button(enabled = enabled, onClick = {
                            act({ model.resolveNpcTurn() }) { message = it }
                        }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.combat_enemy_turn)) }
                    }
                    TextButton(
                        enabled = enabled,
                        onClick = { act({ model.endCombat() }) { message = it } },
                    ) { Text(stringResource(R.string.combat_end)) }
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
                if (p.inCombat) Pill(stringResource(R.string.status_in_combat), MaterialTheme.colorScheme.error)
                if (p.shield > 0) Pill(stringResource(R.string.status_shield, p.shield), MaterialTheme.colorScheme.primary)
            }
            Muted(stringResource(R.string.status_level_xp, p.level, p.xp))
            p.resources.forEach { (id, v) -> ResourceBar(id, v.first, v.second) }
            if (p.conditions.isNotEmpty()) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    p.conditions.forEach { Pill(it, MaterialTheme.colorScheme.tertiary) }
                }
            }
            p.quest?.let { Muted(stringResource(R.string.status_quest, it)) }
        }
    }
}

@Composable
private fun ResourceBar(
    id: String,
    current: Int,
    max: Int,
) {
    val color = resourceColor(id)
    val frac = if (max <= 0) 0f else (current.toFloat() / max).coerceIn(0f, 1f)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(id.uppercase(), Modifier.width(30.dp), style = MaterialTheme.typography.labelLarge, color = color)
        LinearProgressIndicator(
            progress = { frac },
            modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        Text("$current/$max", Modifier.widthIn(min = 44.dp), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ChatItem(
    line: ChatLine,
    revealed: Boolean,
    onRevealed: () -> Unit,
) {
    when (line.author) {
        Author.PLAYER ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                    modifier = Modifier.widthIn(max = 320.dp),
                ) {
                    Text(
                        line.text,
                        Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        Author.ROLL -> {
            val view = line.roll
            if (view != null) {
                RollCard(view, revealed, onRevealed)
            } else {
                Pill(stringResource(R.string.chat_roll_line, line.text), MaterialTheme.colorScheme.secondary)
            }
        }
        Author.MASTER ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                Box(
                    Modifier.width(3.dp).fillMaxHeight().clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    line.text,
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif, lineHeight = 27.sp, fontSize = 17.sp),
                )
            }
        Author.SYSTEM -> ErrorText(line.text)
    }
}

@Composable
private fun Thinking() {
    val t = rememberInfiniteTransition(label = "pensando")
    val a by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "alfa")
    Text(
        stringResource(R.string.chat_thinking),
        Modifier.alpha(a),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun BottomBar(
    busy: Boolean,
    pendingRoll: Boolean,
    pendingCheck: String?,
    fortuneSpendLabel: String?,
    text: String,
    onText: (String) -> Unit,
    suggestions: List<String>,
    warning: String?,
    onSend: () -> Unit,
    onRollCheck: () -> Unit,
    onRollFortune: () -> Unit,
    onUndo: () -> Unit,
    onRegenerate: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 3.dp) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            warning?.let { ErrorText(stringResource(R.string.chat_check_warning, it)) }
            if (busy) Thinking()
            if (pendingRoll && !busy) {
                Text(
                    stringResource(R.string.chat_roll_to_continue),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            // O mestre pediu um teste: só resta rolar (ou desfazer). O motor rola e o mestre narra o desfecho.
            if (pendingCheck != null && !busy && !pendingRoll) {
                Button(
                    onClick = onRollCheck,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.chat_roll_check, pendingCheck)) }
                if (fortuneSpendLabel != null) {
                    OutlinedButton(onClick = onRollFortune, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.chat_spend_fortune, fortuneSpendLabel))
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
                    value = text,
                    onValueChange = onText,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(stringResource(if (pendingCheck != null) R.string.chat_placeholder_roll else R.string.chat_placeholder_action))
                    },
                    enabled = !busy && !pendingRoll && pendingCheck == null,
                    shape = MaterialTheme.shapes.large,
                    maxLines = 4,
                )
                Button(enabled = !busy && !pendingRoll && pendingCheck == null && text.isNotBlank(), onClick = onSend) {
                    Text(stringResource(R.string.chat_send))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(enabled = !busy, onClick = onUndo) { Text(stringResource(R.string.chat_undo)) }
                TextButton(enabled = !busy && !pendingRoll, onClick = onRegenerate) { Text(stringResource(R.string.chat_regenerate)) }
            }
        }
    }
}
