package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import dev.rpgsolo.ai.CampaignForm
import dev.rpgsolo.ai.NewCampaignFlow
import dev.rpgsolo.engine.Ficha
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
    var useAeltharyn by remember(flow) { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
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
                Text("Mundo inicial (opcional)", style = MaterialTheme.typography.titleSmall)
                FilterChip(
                    selected = useAeltharyn,
                    onClick = { useAeltharyn = !useAeltharyn },
                    label = { Text("Aeltharyn — Reino das Florestas") },
                )
                if (useAeltharyn) {
                    Text(
                        "Instala as dez cidades, rotas e crises iniciais de Aeltharyn. Só Elarion começa descoberta.",
                        style = MaterialTheme.typography.bodySmall,
                    )
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
                    val campaignName = name
                    busy = true
                    scope.launch {
                        // Grava no banco e troca a sessão: fora da thread principal, com exclusividade de turno (R-02).
                        val result =
                            try {
                                withContext(Dispatchers.IO) {
                                    model.exclusiveTurn {
                                        flow.start(ficha, campaignName, Random.nextLong(), if (useAeltharyn) "kingdom.aeltharyn" else null).also {
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
                OutlinedButton(onClick = {
                    flow.reset()
                    step = flow.step
                    text = ""
                    name = ""
                    chosen = null
                    useAeltharyn = false
                }) { Text(stringResource(R.string.campaign_create_another)) }
            }
        }
    }
}
