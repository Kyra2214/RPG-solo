package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import dev.rpgsolo.R
import dev.rpgsolo.ai.ProviderKind
import dev.rpgsolo.ai.maskKey
import kotlinx.coroutines.launch

/** Aba Config (10.6.1/10.6.4): provedor, endpoint, modelo, chave mascarada e uso estimado. */
@Composable
fun ConfigScreen(model: AppModel) {
    val stored = model.savedSettings()
    var kind by remember {
        mutableStateOf(stored?.kind?.let { runCatching { ProviderKind.valueOf(it) }.getOrNull() } ?: ProviderKind.OPENAI_COMPATIBLE)
    }
    var endpoint by remember { mutableStateOf(stored?.endpoint ?: "https://") }
    var modelName by remember { mutableStateOf(stored?.model.orEmpty()) }
    var key by remember { mutableStateOf(stored?.apiKey.orEmpty()) }
    var cheapModel by remember { mutableStateOf(stored?.cheapModel.orEmpty()) }
    var premiumModel by remember { mutableStateOf(stored?.premiumModel.orEmpty()) }
    var cacheSystemPrompt by remember { mutableStateOf(stored?.cacheSystemPrompt ?: (stored?.endpoint ?: endpoint).startsWith("https://api.anthropic.com/")) }
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
    ScreenColumn {
        SectionTitle(stringResource(R.string.config_provider_title), stringResource(R.string.config_provider_supporting))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProviderKind.values().forEach { k ->
                FilterChip(selected = kind == k, onClick = { kind = k }, label = { Text(k.label) })
            }
        }
        OutlinedTextField(endpoint, { endpoint = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_endpoint_label)) }, singleLine = true)
        if (endpoint.trim().startsWith("http://", ignoreCase = true)) {
            ErrorText(stringResource(R.string.config_http_error))
        }
        OutlinedTextField(modelName, { modelName = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_model_label)) }, singleLine = true)
        OutlinedTextField(cheapModel, { cheapModel = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_cheap_model_label)) }, singleLine = true)
        OutlinedTextField(premiumModel, { premiumModel = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_premium_model_label)) }, singleLine = true)
        OutlinedTextField(
            key, { key = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_api_key_label)) }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (kind == ProviderKind.ANTHROPIC) {
            FilterChip(
                selected = cacheSystemPrompt,
                onClick = { cacheSystemPrompt = !cacheSystemPrompt },
                label = { Text(stringResource(if (cacheSystemPrompt) R.string.config_cache_enabled else R.string.config_cache_disabled)) }
            )
            Muted(stringResource(R.string.config_cache_help))
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !testing,
            onClick = {
                testing = true
                testResult = null
                scope.launch {
                    val result = cancellableIo {
                        model.testConnection(kind, endpoint, modelName, key, cacheSystemPrompt)
                    }
                    testResult = result
                    testing = false
                }
            }
        ) { Text(stringResource(if (testing) R.string.config_testing_connection else R.string.config_test_connection)) }
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            fun parsePrice(value: String): Double? =
                if (value.isBlank()) null else value.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }
            val input = parsePrice(inputPrice)
            val output = parsePrice(outputPrice)
            val cacheRead = parsePrice(cacheReadPrice)
            val cacheCreation = parsePrice(cacheCreationPrice)
            if ((!inputPrice.isBlank() && input == null) || (!outputPrice.isBlank() && output == null) ||
                (!cacheReadPrice.isBlank() && cacheRead == null) || (!cacheCreationPrice.isBlank() && cacheCreation == null)) {
                priceError = configPriceError
                saved = false
            } else {
                priceError = null
                val baseInput = input ?: 0.0
                saved = model.applySettings(
                    kind, endpoint, modelName, key, cheapModel, premiumModel, cacheSystemPrompt,
                    baseInput, output ?: 0.0, cacheRead ?: baseInput, cacheCreation ?: baseInput
                )
            }
        }) { Text(stringResource(R.string.config_save_and_use)) }
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
        if (saved && model.configError == null) AppCard(accent = MaterialTheme.colorScheme.tertiary) { Text(stringResource(R.string.config_provider_active, maskKey(key))) }
        AppCard {
            Text(stringResource(R.string.config_accessibility_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.config_font_scale, (model.fontScale * 100).toInt()))
            Slider(
                value = model.fontScale,
                onValueChange = model::setFontScale,
                valueRange = 0.85f..1.4f,
                steps = 10
            )
            Muted(stringResource(R.string.config_font_scale_help))
        }
        AppCard {
            Text(stringResource(R.string.config_generation_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.config_narration_length))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("curta" to R.string.config_length_short, "equilibrada" to R.string.config_length_balanced, "detalhada" to R.string.config_length_detailed).forEach { (option, labelRes) ->
                    FilterChip(
                        selected = narrationLength == option,
                        onClick = {
                            narrationLength = option
                            model.setNarrationPreferences(narrationLength, narrationStyle)
                        },
                        label = { Text(stringResource(labelRes)) }
                    )
                }
            }
            Text(stringResource(R.string.config_style))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("cinematográfica" to R.string.config_style_cinematic, "literária" to R.string.config_style_literary, "direta" to R.string.config_style_direct).forEach { (option, labelRes) ->
                    FilterChip(
                        selected = narrationStyle == option,
                        onClick = {
                            narrationStyle = option
                            model.setNarrationPreferences(narrationLength, narrationStyle)
                        },
                        label = { Text(stringResource(labelRes)) }
                    )
                }
            }
            Text(stringResource(R.string.config_chapter_size))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(100, 200, 300, 500, 1000).forEach { limit ->
                    FilterChip(
                        selected = model.chapterEventLimit == limit,
                        onClick = { model.setChapterEventLimit(limit) },
                        label = { Text(stringResource(R.string.config_chapter_events, limit)) }
                    )
                }
            }
            Muted(stringResource(R.string.config_chapter_help))
            Text(stringResource(R.string.config_red_lines))
            OutlinedTextField(
                value = contentLines,
                onValueChange = { contentLines = it; model.setContentBoundaries(contentLines, contentVeils) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.config_forbidden_topics)) },
                minLines = 2,
                maxLines = 5
            )
            Text(stringResource(R.string.config_veils))
            OutlinedTextField(
                value = contentVeils,
                onValueChange = { contentVeils = it; model.setContentBoundaries(contentLines, contentVeils) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.config_veiled_topics)) },
                minLines = 2,
                maxLines = 5
            )
            Muted(stringResource(R.string.config_content_help))
        }
        AppCard {
            Text(stringResource(R.string.config_prices_title), style = MaterialTheme.typography.titleSmall)
            Muted(stringResource(R.string.config_prices_help))
            OutlinedTextField(inputPrice, { inputPrice = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_input_price)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(outputPrice, { outputPrice = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_output_price)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(cacheReadPrice, { cacheReadPrice = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_cache_read_price)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(cacheCreationPrice, { cacheCreationPrice = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.config_cache_creation_price)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            priceError?.let { ErrorText(it) }
            val input = inputPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
            val output = outputPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
            val cacheRead = cacheReadPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: input
            val cacheCreation = cacheCreationPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: input
            if (inputPrice.isBlank() || outputPrice.isBlank()) {
                Muted(stringResource(R.string.config_cost_missing_prices))
            } else {
                Text(stringResource(R.string.config_cost_estimate, "%.6f".format(model.usage.estimateCost(input, output, cacheRead, cacheCreation))), style = MaterialTheme.typography.bodyMedium)
            }
        }
        AppCard {
            Text(stringResource(R.string.config_privacy_title), style = MaterialTheme.typography.titleSmall)
            Muted(stringResource(R.string.config_privacy_key))
            Muted(stringResource(R.string.config_usage, model.usage.calls, model.usage.inputTokens, model.usage.outputTokens))
            Muted(stringResource(R.string.config_cache_usage, model.usage.cacheReadInputTokens, model.usage.cacheCreationInputTokens))
        }
    }
}
