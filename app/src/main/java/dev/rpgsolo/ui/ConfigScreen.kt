package dev.rpgsolo.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import dev.rpgsolo.ai.ProviderKind
import dev.rpgsolo.ai.ProviderPreset
import dev.rpgsolo.ai.maskKey
import kotlinx.coroutines.launch

/** Aba Config (10.6.1/10.6.4): provedor, endpoint, modelo, chave mascarada e uso estimado. */
@Composable
fun ConfigScreen(model: AppModel) {
    val stored = model.savedSettings()
    var kind by remember {
        mutableStateOf(stored?.kind?.let { runCatching { ProviderKind.valueOf(it) }.getOrNull() } ?: ProviderKind.OPENAI_COMPATIBLE)
    }
    // Primeira vez: já abre num provedor gratuito pré-configurado. Quem salvou um endpoint próprio continua em Personalizado.
    var preset by remember { mutableStateOf(if (stored == null) ProviderPreset.OPENROUTER else ProviderPreset.fromName(stored.preset)) }
    var freeModels by remember {
        mutableStateOf(if (stored != null && stored.preset.isNotBlank()) listOf(stored.model) + stored.fallbackModels else emptyList())
    }
    var discovering by remember { mutableStateOf(false) }
    var discoverResult by remember { mutableStateOf<FreeModelsResult?>(null) }
    var endpoint by remember { mutableStateOf(stored?.endpoint ?: "https://") }
    var modelName by remember { mutableStateOf(stored?.model.orEmpty()) }
    var key by remember { mutableStateOf(stored?.apiKey.orEmpty()) }
    var cheapModel by remember { mutableStateOf(stored?.cheapModel.orEmpty()) }
    var premiumModel by remember { mutableStateOf(stored?.premiumModel.orEmpty()) }
    var cacheSystemPrompt by remember {
        mutableStateOf(stored?.cacheSystemPrompt ?: (stored?.endpoint ?: endpoint).startsWith("https://api.anthropic.com/"))
    }
    var inputPrice by remember { mutableStateOf(stored?.inputPricePerMillion?.takeIf { it > 0.0 }?.toString().orEmpty()) }
    var outputPrice by remember { mutableStateOf(stored?.outputPricePerMillion?.takeIf { it > 0.0 }?.toString().orEmpty()) }
    var cacheReadPrice by remember { mutableStateOf(stored?.cacheReadPricePerMillion?.takeIf { it > 0.0 }?.toString().orEmpty()) }
    var cacheCreationPrice by remember { mutableStateOf(stored?.cacheCreationPricePerMillion?.takeIf { it > 0.0 }?.toString().orEmpty()) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var narrationLength by remember { mutableStateOf(model.narrationLength) }
    var narrationStyle by remember { mutableStateOf(model.narrationStyle) }
    var contentLines by remember { mutableStateOf(model.contentLines) }
    var contentVeils by remember { mutableStateOf(model.contentVeils) }
    var saved by remember { mutableStateOf(false) }
    var testing by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<ConnectionTestResult?>(null) }
    val scope = rememberCoroutineScope()
    val configPriceError = stringResource(R.string.config_price_error)
    val effKind = if (preset != null) ProviderKind.OPENAI_COMPATIBLE else kind
    val effEndpoint = preset?.chatEndpoint ?: endpoint
    val effCheap = if (preset != null) "" else cheapModel
    val effPremium = if (preset != null) "" else premiumModel
    val effCache = if (preset != null) false else cacheSystemPrompt
    ScreenColumn {
        SectionTitle(stringResource(R.string.config_provider_title), stringResource(R.string.config_provider_supporting))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProviderPreset.values().forEach { p ->
                FilterChip(
                    selected = preset == p,
                    enabled = !discovering && !testing,
                    onClick = {
                        if (preset != p) {
                            preset = p
                            freeModels = emptyList()
                            modelName = ""
                            key = stored?.takeIf { it.preset == p.name }?.apiKey.orEmpty()
                            discoverResult = null
                            testResult = null
                            saved = false
                        }
                    },
                    label = { Text(p.label) },
                )
            }
            FilterChip(
                selected = preset == null,
                enabled = !discovering && !testing,
                onClick = {
                    if (preset != null) {
                        preset = null
                        freeModels = emptyList()
                        modelName = stored?.takeIf { it.preset.isBlank() }?.model.orEmpty()
                        key = stored?.takeIf { it.preset.isBlank() }?.apiKey.orEmpty()
                        discoverResult = null
                        testResult = null
                        saved = false
                    }
                },
                label = { Text(stringResource(R.string.config_preset_custom)) },
            )
        }
        preset?.let { p ->
            Muted(stringResource(R.string.config_preset_key_help, p.label))
            ApiKeyField(key, enabled = !discovering && !testing) { nextKey ->
                if (nextKey != key) {
                    key = nextKey
                    freeModels = emptyList()
                    modelName = ""
                    discoverResult = null
                    testResult = null
                    saved = false
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !discovering && !testing,
                onClick = {
                    val requestedKey = key.trim()
                    discovering = true
                    discoverResult = null
                    freeModels = emptyList()
                    modelName = ""
                    saved = false
                    scope.launch {
                        val result = cancellableIo { model.discoverFreeModels(p, requestedKey) }
                        if (preset == p && key.trim() == requestedKey) {
                            discoverResult = result
                            if (result.success) {
                                freeModels = result.models
                                if (modelName !in result.models) modelName = result.models.first()
                            }
                        }
                        discovering = false
                    }
                },
            ) { Text(stringResource(if (discovering) R.string.config_finding_free_models else R.string.config_find_free_models)) }
            discoverResult?.let { result ->
                if (result.success) {
                    AppCard(accent = MaterialTheme.colorScheme.tertiary) { Text(result.message) }
                } else {
                    ErrorText(result.message)
                }
            }
            if (freeModels.isNotEmpty()) {
                Text(stringResource(R.string.config_free_models_title), style = MaterialTheme.typography.titleSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    freeModels.take(12).forEach { id ->
                        FilterChip(
                            selected = modelName == id,
                            enabled = !discovering && !testing,
                            onClick = {
                                modelName = id
                                testResult = null
                                saved = false
                            },
                            label = { Text(id) },
                        )
                    }
                }
                Muted(stringResource(R.string.config_free_models_auto_help))
            }
            AppCard {
                Text(stringResource(R.string.config_free_key_steps_title), style = MaterialTheme.typography.titleSmall)
                Muted(
                    stringResource(
                        if (p == ProviderPreset.OPENROUTER) R.string.config_free_key_steps_openrouter else R.string.config_free_key_steps_groq,
                    ),
                )
            }
        }
        if (preset == null) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProviderKind.values().forEach { k ->
                    FilterChip(selected = kind == k, onClick = { kind = k }, label = { Text(k.label) })
                }
            }
            OutlinedTextField(endpoint, {
                endpoint = it
            }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_endpoint_label)) }, singleLine = true)
            if (endpoint.trim().startsWith("http://", ignoreCase = true)) {
                ErrorText(stringResource(R.string.config_http_error))
            }
            OutlinedTextField(modelName, {
                modelName = it
            }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_model_label)) }, singleLine = true)
            OutlinedTextField(cheapModel, {
                cheapModel = it
            }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_cheap_model_label)) }, singleLine = true)
            OutlinedTextField(premiumModel, {
                premiumModel = it
            }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_premium_model_label)) }, singleLine = true)
            OutlinedTextField(
                key,
                { key = it },
                Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.config_api_key_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
        }
        if (preset == null && kind == ProviderKind.ANTHROPIC) {
            FilterChip(
                selected = cacheSystemPrompt,
                onClick = { cacheSystemPrompt = !cacheSystemPrompt },
                label = { Text(stringResource(if (cacheSystemPrompt) R.string.config_cache_enabled else R.string.config_cache_disabled)) },
            )
            Muted(stringResource(R.string.config_cache_help))
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled =
                !testing && !discovering &&
                    (preset == null || (modelName.isNotBlank() && modelName in freeModels.take(12))),
            onClick = {
                testing = true
                testResult = null
                scope.launch {
                    val result =
                        cancellableIo {
                            model.testConnection(effKind, effEndpoint, modelName, key, effCache)
                        }
                    testResult = result
                    testing = false
                }
            },
        ) { Text(stringResource(if (testing) R.string.config_testing_connection else R.string.config_test_connection)) }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled =
                !discovering && !testing &&
                    (preset == null || (modelName.isNotBlank() && modelName in freeModels.take(12))),
            onClick = {
                fun parsePrice(value: String): Double? =
                    if (value.isBlank()) null else value.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }
                val input = parsePrice(inputPrice)
                val output = parsePrice(outputPrice)
                val cacheRead = parsePrice(cacheReadPrice)
                val cacheCreation = parsePrice(cacheCreationPrice)
                if ((!inputPrice.isBlank() && input == null) || (!outputPrice.isBlank() && output == null) ||
                    (!cacheReadPrice.isBlank() && cacheRead == null) || (!cacheCreationPrice.isBlank() && cacheCreation == null)
                ) {
                    priceError = configPriceError
                    saved = false
                } else {
                    priceError = null
                    val baseInput = input ?: 0.0
                    saved =
                        model.applySettings(
                            effKind, effEndpoint, modelName, key, effCheap, effPremium, effCache,
                            baseInput, output ?: 0.0, cacheRead ?: baseInput, cacheCreation ?: baseInput,
                            preset?.name.orEmpty(), freeModels.take(12).filter { it != modelName },
                        )
                }
            },
        ) { Text(stringResource(R.string.config_save_and_use)) }
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            model.forgetProvider()
            key = ""
            saved = false
        }) { Text(stringResource(R.string.config_forget_provider)) }
        model.configError?.let { ErrorText(it) }
        testResult?.let { result ->
            if (result.success) {
                AppCard(accent = MaterialTheme.colorScheme.tertiary) { Text(result.message) }
            } else {
                ErrorText(result.message)
            }
        }
        if (saved && model.configError == null) {
            AppCard(accent = MaterialTheme.colorScheme.tertiary) {
                Text(stringResource(R.string.config_provider_active, maskKey(key)))
            }
        }
        AppCard {
            Text(stringResource(R.string.config_accessibility_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.config_font_scale, (model.fontScale * 100).toInt()))
            val scaleDesc = stringResource(R.string.config_font_scale_desc, (model.fontScale * 100).toInt())
            Slider(
                modifier = Modifier.semantics { contentDescription = scaleDesc },
                value = model.fontScale,
                onValueChange = model::setFontScale,
                valueRange = 0.85f..1.4f,
                steps = 10,
            )
            Muted(stringResource(R.string.config_font_scale_help))
        }
        AppCard {
            Text(stringResource(R.string.config_generation_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.config_narration_length))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "curta" to R.string.config_length_short,
                    "equilibrada" to R.string.config_length_balanced,
                    "detalhada" to R.string.config_length_detailed,
                ).forEach {
                        (option, labelRes) ->
                    FilterChip(
                        selected = narrationLength == option,
                        onClick = {
                            narrationLength = option
                            model.setNarrationPreferences(narrationLength, narrationStyle)
                        },
                        label = { Text(stringResource(labelRes)) },
                    )
                }
            }
            Text(stringResource(R.string.config_style))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "cinematográfica" to R.string.config_style_cinematic,
                    "literária" to R.string.config_style_literary,
                    "direta" to R.string.config_style_direct,
                ).forEach {
                        (option, labelRes) ->
                    FilterChip(
                        selected = narrationStyle == option,
                        onClick = {
                            narrationStyle = option
                            model.setNarrationPreferences(narrationLength, narrationStyle)
                        },
                        label = { Text(stringResource(labelRes)) },
                    )
                }
            }
            Text(stringResource(R.string.config_chapter_size))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(100, 200, 300, 500, 1000).forEach { limit ->
                    FilterChip(
                        selected = model.chapterEventLimit == limit,
                        onClick = { model.setChapterEventLimit(limit) },
                        label = { Text(stringResource(R.string.config_chapter_events, limit)) },
                    )
                }
            }
            Muted(stringResource(R.string.config_chapter_help))
            Text(stringResource(R.string.config_red_lines))
            OutlinedTextField(
                value = contentLines,
                onValueChange = {
                    contentLines = it
                    model.setContentBoundaries(contentLines, contentVeils)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.config_forbidden_topics)) },
                minLines = 2,
                maxLines = 5,
            )
            Text(stringResource(R.string.config_veils))
            OutlinedTextField(
                value = contentVeils,
                onValueChange = {
                    contentVeils = it
                    model.setContentBoundaries(contentLines, contentVeils)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.config_veiled_topics)) },
                minLines = 2,
                maxLines = 5,
            )
            Muted(stringResource(R.string.config_content_help))
        }
        AppCard {
            Text(stringResource(R.string.config_prices_title), style = MaterialTheme.typography.titleSmall)
            Muted(stringResource(R.string.config_prices_help))
            OutlinedTextField(inputPrice, {
                inputPrice = it
            }, Modifier.fillMaxWidth(), label = {
                Text(stringResource(R.string.config_input_price))
            }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(outputPrice, {
                outputPrice = it
            }, Modifier.fillMaxWidth(), label = {
                Text(stringResource(R.string.config_output_price))
            }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(cacheReadPrice, {
                cacheReadPrice = it
            }, Modifier.fillMaxWidth(), label = {
                Text(stringResource(R.string.config_cache_read_price))
            }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(cacheCreationPrice, {
                cacheCreationPrice = it
            }, Modifier.fillMaxWidth(), label = {
                Text(stringResource(R.string.config_cache_creation_price))
            }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            priceError?.let { ErrorText(it) }
            val input = inputPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
            val output = outputPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
            val cacheRead = cacheReadPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: input
            val cacheCreation = cacheCreationPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: input
            if (inputPrice.isBlank() || outputPrice.isBlank()) {
                Muted(stringResource(R.string.config_cost_missing_prices))
            } else {
                Text(
                    stringResource(
                        R.string.config_cost_estimate,
                        "%.6f".format(model.usage.estimateCost(input, output, cacheRead, cacheCreation)),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        AppCard {
            Text(stringResource(R.string.config_privacy_title), style = MaterialTheme.typography.titleSmall)
            Muted(stringResource(R.string.config_privacy_key))
            Muted(stringResource(R.string.config_usage, model.usage.calls, model.usage.inputTokens, model.usage.outputTokens))
            Muted(stringResource(R.string.config_cache_usage, model.usage.cacheReadInputTokens, model.usage.cacheCreationInputTokens))
        }
        val diagContext = LocalContext.current
        var hasCrash by remember { mutableStateOf(Diagnostics.lastCrash(diagContext) != null) }
        AppCard {
            Text(stringResource(R.string.diag_title), style = MaterialTheme.typography.titleSmall)
            Muted(stringResource(R.string.diag_help))
            Muted(stringResource(if (hasCrash) R.string.diag_crash_recorded else R.string.diag_no_crash))
            Button(onClick = { diagContext.startActivity(Diagnostics.chooser(diagContext)) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.diag_export))
            }
            if (hasCrash) {
                TextButton(onClick = {
                    Diagnostics.clear(diagContext)
                    hasCrash = false
                }) { Text(stringResource(R.string.diag_clear)) }
            }
        }
    }
}

@Composable
private fun ApiKeyField(
    value: String,
    enabled: Boolean = true,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value,
        onChange,
        Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.config_api_key_label)) },
        singleLine = true,
        enabled = enabled,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    )
}
