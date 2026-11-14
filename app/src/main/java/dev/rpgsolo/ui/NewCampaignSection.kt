package dev.rpgsolo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import dev.rpgsolo.ai.CampaignForm
import dev.rpgsolo.ai.NewCampaignFlow
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.rules.WorldOption
import dev.rpgsolo.rules.WorldOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Nova campanha (9.1): descrição livre → perguntas do motor → revisão com avisos → ficha e nome → começar.
 * Toda a lógica está em [NewCampaignFlow] (testada em Kotlin puro); aqui só se desenha o passo atual.
 */
@Composable
fun NewCampaignSection(model: AppModel) {
    val flow = remember(model.controller != null, model.packageVersion) { model.newCampaignFlow() }
    Text(
        stringResource(R.string.campaign_new_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
    if (flow == null) {
        Muted(stringResource(R.string.campaign_provider_required))
        return
    }
    val scope = rememberCoroutineScope()
    var step by remember(flow) { mutableStateOf(flow.step) }
    var text by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var chosen by remember { mutableStateOf<Ficha?>(null) }
    // MP-08: id do mundo escolhido (null = criar o próprio, a descrição livre acima).
    var worldId by remember(flow) { mutableStateOf<String?>(null) }
    var worldOptions by remember { mutableStateOf<List<WorldOption>>(emptyList()) }
    // MP-07: mundos de campanhas anteriores (replay do log) e o salto de tempo até a nova campanha.
    var previousWorlds by remember { mutableStateOf<List<dev.rpgsolo.rules.PreviousWorld>>(emptyList()) }
    var jumpDays by remember { mutableStateOf("30") }
    var bringLegends by remember { mutableStateOf(false) }
    // MP-08: mundo próprio proposto pela IA e validado pelo motor; o aviso mostra recusa ou falha ao instalar.
    var customDraft by remember { mutableStateOf<dev.rpgsolo.rules.CustomWorldDraft?>(null) }
    var customWorldMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        // O molde de Mythralis lê arquivos e o replay das campanhas também: carga fora da thread principal.
        withContext(Dispatchers.IO) {
            previousWorlds = model.previousWorlds()
            worldOptions = WorldOptions.list(previousWorlds)
        }
    }
    var busy by remember { mutableStateOf(false) }
    var quickFailed by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var genre by remember { mutableStateOf("") }
    var era by remember { mutableStateOf("") }
    var tone by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    var themes by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var contentLimits by remember { mutableStateOf("") }
    val startFailureText = stringResource(R.string.campaign_start_failure)

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = { showForm = !showForm }) {
            Text(stringResource(if (showForm) R.string.campaign_hide_form else R.string.campaign_show_form))
        }
        if (showForm) {
            Text(stringResource(R.string.campaign_form_optional), style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(genre, {
                genre = it
            }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.campaign_genre)) }, singleLine = true)
            OutlinedTextField(
                era,
                { era = it },
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campaign_era)) },
                singleLine = true,
            )
            OutlinedTextField(
                tone,
                { tone = it },
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campaign_tone)) },
                singleLine = true,
            )
            OutlinedTextField(difficulty, {
                difficulty = it
            }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.campaign_difficulty)) }, singleLine = true)
            OutlinedTextField(themes, { themes = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.campaign_themes)) })
            OutlinedTextField(duration, {
                duration = it
            }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.campaign_duration)) }, singleLine = true)
            OutlinedTextField(
                contentLimits,
                { contentLimits = it },
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campaign_content_limits)) },
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    text =
                        CampaignForm(
                            genre = genre, era = era, tone = tone, difficulty = difficulty,
                            themes = themes.split(',').map { it.trim() }.filter { it.isNotEmpty() },
                            duration = duration, contentLimits = contentLimits, freeText = text,
                        ).toDescription()
                },
            ) { Text(stringResource(R.string.campaign_add_fields)) }
        }
        when (val s = step) {
            NewCampaignFlow.Step.Describe -> {
                OutlinedTextField(
                    text,
                    { text = it },
                    Modifier.fillMaxWidth(),
                    enabled = !busy,
                    label = { Text(stringResource(R.string.campaign_describe)) },
                )
                Button(modifier = Modifier.fillMaxWidth(), enabled = !busy && text.isNotBlank(), onClick = {
                    busy = true
                    scope.launch {
                        // Chamada à IA: fora da thread principal.
                        cancellableIo { flow.submitDescription(text) }
                        step = flow.step
                        busy = false
                    }
                }) { Text(stringResource(if (busy) R.string.campaign_interpreting else R.string.campaign_interpret)) }
            }

            is NewCampaignFlow.Step.Asking -> {
                Text(s.ask.question, style = MaterialTheme.typography.bodyLarge)
                s.ask.options.forEach { o ->
                    OutlinedButton(onClick = {
                        flow.answer(o.id)
                        step = flow.step
                    }, modifier = Modifier.fillMaxWidth()) { Text(o.label) }
                }
                OutlinedButton(onClick = {
                    flow.reset()
                    step = flow.step
                }) { Text(stringResource(R.string.campaign_restart)) }
            }

            is NewCampaignFlow.Step.Review -> {
                val r = s.ready.resolved
                Text(
                    stringResource(R.string.campaign_edition, r.edition.name, r.edition.edition),
                    style = MaterialTheme.typography.titleSmall,
                )
                s.ready.draft.concept?.let {
                    Text(
                        stringResource(R.string.campaign_character_requested, it),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                r.notes.forEach { Text(stringResource(R.string.campaign_note, it), style = MaterialTheme.typography.bodySmall) }
                Text(stringResource(R.string.campaign_world_title), style = MaterialTheme.typography.titleSmall)
                worldOptions.forEach { option ->
                    CartaoMundo(option, worldId == option.id && option.available) { worldId = option.id }
                }
                if (worldId == null) {
                    OutlinedButton(
                        enabled = !busy && text.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            busy = true
                            scope.launch {
                                val proposal =
                                    try {
                                        withContext(Dispatchers.IO) { model.proposeCustomWorld(text) }
                                    } finally {
                                        busy = false
                                    }
                                when (proposal) {
                                    is dev.rpgsolo.ai.CustomWorldProposal.Valid -> {
                                        customDraft = proposal.draft
                                        customWorldMessage = null
                                    }
                                    is dev.rpgsolo.ai.CustomWorldProposal.Invalid -> {
                                        customDraft = null
                                        customWorldMessage = proposal.errors.joinToString("; ")
                                    }
                                    is dev.rpgsolo.ai.CustomWorldProposal.Failed -> {
                                        customDraft = null
                                        customWorldMessage = proposal.reason
                                    }
                                }
                            }
                        },
                    ) { Text(stringResource(R.string.campaign_custom_world_propose)) }
                    customDraft?.let { Muted(stringResource(R.string.campaign_custom_world_ready, it.name, it.places.size)) }
                    customWorldMessage?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                if (worldId == WorldOptions.PREVIOUS) {
                    OutlinedTextField(
                        jumpDays,
                        { v -> jumpDays = v.filter { it.isDigit() }.take(4) },
                        Modifier.fillMaxWidth(),
                        label = { Text("Dias desde o fim da campanha anterior") },
                        singleLine = true,
                    )
                }
                // LG-05: interruptor "trazer lendas" (heróis de campanhas anteriores viram rumores e o motor controla o estágio).
                androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    androidx.compose.material3.Switch(checked = bringLegends, onCheckedChange = { bringLegends = it })
                    Text(stringResource(R.string.campaign_bring_legends), Modifier.padding(start = 8.dp))
                }
                if (s.ready.draft.ignored.isNotEmpty()) {
                    Text(
                        stringResource(R.string.campaign_ignored_terms, s.ready.draft.ignored.joinToString()),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                val fichas = flow.compatible(model.fichas.toList())
                if (fichas.isEmpty()) {
                    Text(stringResource(R.string.campaign_no_sheet), style = MaterialTheme.typography.bodySmall)
                }
                fichas.forEach { f ->
                    FilterChip(selected = chosen?.id == f.id, onClick = { chosen = f }, label = { Text(f.name) })
                }
                val heroisProntos = remember { HeroisProntos.mythralis }
                if (heroisProntos.isNotEmpty()) {
                    Text(stringResource(R.string.campaign_quick_title), style = MaterialTheme.typography.titleSmall)
                    Muted(stringResource(R.string.campaign_quick_help))
                    heroisProntos.forEach { h ->
                        OutlinedButton(
                            enabled = !busy,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                busy = true
                                scope.launch {
                                    val ficha =
                                        try {
                                            withContext(Dispatchers.IO) { model.criarFichaDeHeroi(h, r.edition.id) }
                                        } catch (e: Exception) {
                                            null
                                        } finally {
                                            busy = false
                                        }
                                    if (ficha != null) {
                                        chosen = ficha
                                        worldOptions.firstOrNull { it.id == dev.rpgsolo.world.WorldTemplates.MYTHRALIS && it.available }
                                            ?.let { worldId = it.id }
                                        if (name.isBlank()) name = ficha.name
                                    } else {
                                        quickFailed = true
                                    }
                                }
                            },
                        ) { Text(h.titulo) }
                    }
                    if (quickFailed) {
                        Text(
                            stringResource(R.string.campaign_quick_failed),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                OutlinedTextField(
                    name,
                    { name = it },
                    Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.campaign_name)) },
                    singleLine = true,
                )
                s.errors.forEach { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Button(modifier = Modifier.fillMaxWidth(), enabled = chosen != null && name.isNotBlank() && !busy, onClick = {
                    val ficha = chosen!!
                    val chosenWorldId = worldId
                    // MP-07: por ora continua o mundo da campanha mais recente; escolher entre várias vem com a tela de mundos.
                    val chosenChronicle = if (chosenWorldId == WorldOptions.PREVIOUS) previousWorlds.lastOrNull()?.chronicle else null
                    val chosenJump = jumpDays.toIntOrNull()?.coerceIn(0, 3650) ?: 0
                    val chosenCustom = if (chosenWorldId == null) customDraft else null
                    val campaignName = name
                    busy = true
                    scope.launch {
                        // Grava no banco e troca a sessão: fora da thread principal, com exclusividade de turno (R-02).
                        val result =
                            try {
                                withContext(Dispatchers.IO) {
                                    model.exclusiveTurn {
                                        flow.start(
                                            ficha,
                                            campaignName,
                                            Random.nextLong(),
                                            worldId = chosenWorldId,
                                            chronicle = chosenChronicle,
                                            jumpDays = chosenJump,
                                        ).also {
                                            if (it is NewCampaignFlow.Step.Started) model.activate(it.result)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                NewCampaignFlow.Step.Problem(listOf(e.message ?: startFailureText))
                            } finally {
                                busy = false
                            }
                        step = result
                        if (result is NewCampaignFlow.Step.Started && chosenCustom != null) {
                            // MP-08: o mundo próprio só entra depois da campanha criada; o motor valida de novo ao instalar.
                            customWorldMessage = withContext(Dispatchers.IO) { model.exclusiveTurn { model.installCustomWorld(chosenCustom) } }
                        }
                        if (result is NewCampaignFlow.Step.Started && bringLegends) {
                            // LG-05: as lendas entram depois da campanha criada; o motor valida cada comando.
                            withContext(Dispatchers.IO) { model.exclusiveTurn { model.seedLegends() } }?.let { customWorldMessage = it }
                        }
                        if (result is NewCampaignFlow.Step.Started) {
                            model.registrarConquista(Conquista.PRIMEIRA_CAMPANHA)
                            if (chosenWorldId == dev.rpgsolo.world.WorldTemplates.MYTHRALIS) model.registrarConquista(Conquista.MUNDO_VIVO)
                        }
                    }
                }) { Text(stringResource(R.string.campaign_start)) }
                OutlinedButton(onClick = {
                    flow.reset()
                    step = flow.step
                }) { Text(stringResource(R.string.campaign_cancel)) }
            }

            is NewCampaignFlow.Step.Problem -> {
                s.reasons.forEach { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                OutlinedButton(onClick = {
                    flow.reset()
                    step = flow.step
                }) { Text(stringResource(R.string.campaign_try_again)) }
            }

            is NewCampaignFlow.Step.Started -> {
                Text(stringResource(R.string.campaign_started), style = MaterialTheme.typography.bodyMedium)
                customWorldMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                OutlinedButton(onClick = {
                    flow.reset()
                    step = flow.step
                    text = ""
                    name = ""
                    chosen = null
                    worldId = null
                    customDraft = null
                    customWorldMessage = null
                }) { Text(stringResource(R.string.campaign_create_another)) }
            }
        }
    }
}

/** Cartão de mundo (CG-6.1): medalhão com o monograma (lugar da imagem do M12), nome e resumo; indisponível mostra o motivo. */
@Composable
private fun CartaoMundo(
    option: WorldOption,
    selecionado: Boolean,
    aoClicar: () -> Unit,
) {
    val estado = stringResource(if (selecionado) R.string.campaign_world_selected else R.string.campaign_world_not_selected)
    AppCard(
        modifier =
            Modifier
                .clickable(enabled = option.available, role = Role.Button, onClick = aoClicar)
                .semantics { stateDescription = estado },
        accent = if (selecionado) MaterialTheme.colorScheme.primary else null,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoG)) {
            Surface(
                shape = CircleShape,
                color = if (selecionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.size(CriacaoTokens.MedalhaoTamanho),
            ) {
                Box(contentAlignment = Alignment.Center) { Text(CriacaoNomes.monograma(option.name)) }
            }
            Column(Modifier.weight(1f)) {
                Text(option.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    if (option.available) option.summary else option.unavailableReason.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
