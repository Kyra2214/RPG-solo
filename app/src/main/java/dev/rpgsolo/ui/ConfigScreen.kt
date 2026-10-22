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
                priceError = "Use preços numéricos não negativos por milhão de tokens."
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
            Text("Preferências de geração", style = MaterialTheme.typography.titleSmall)
            Text("Extensão da narração")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("curta", "equilibrada", "detalhada").forEach { option ->
                    FilterChip(
                        selected = narrationLength == option,
                        onClick = {
                            narrationLength = option
                            model.setNarrationPreferences(narrationLength, narrationStyle)
                        },
                        label = { Text(option.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            Text("Estilo")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("cinematográfica", "literária", "direta").forEach { option ->
                    FilterChip(
                        selected = narrationStyle == option,
                        onClick = {
                            narrationStyle = option
                            model.setNarrationPreferences(narrationLength, narrationStyle)
                        },
                        label = { Text(option.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            Text("Tamanho de capítulo")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(100, 200, 300, 500, 1000).forEach { limit ->
                    FilterChip(
                        selected = model.chapterEventLimit == limit,
                        onClick = { model.setChapterEventLimit(limit) },
                        label = { Text("$limit eventos") }
                    )
                }
            }
            Muted("Ao atingir esse número de eventos confirmados, o app gera um resumo e abre o próximo capítulo automaticamente.")
            Text("Linhas vermelhas — não incluir")
            OutlinedTextField(
                value = contentLines,
                onValueChange = { contentLines = it; model.setContentBoundaries(contentLines, contentVeils) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Temas proibidos (um por linha)") },
                minLines = 2,
                maxLines = 5
            )
            Text("Véus — mencionar sem detalhar")
            OutlinedTextField(
                value = contentVeils,
                onValueChange = { contentVeils = it; model.setContentBoundaries(contentLines, contentVeils) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Temas a resumir ou cortar (um por linha)") },
                minLines = 2,
                maxLines = 5
            )
            Muted("Limites são enviados ao mestre a cada jogada e não alteram os resultados do motor nem as regras do sistema.")
        }
        AppCard {
            Text("Preços do modelo", style = MaterialTheme.typography.titleSmall)
            Muted("Informe o preço por 1 milhão de tokens conforme a tabela atual do provedor. Campos de cache vazios usam o preço normal de entrada para evitar estimativa zero.")
            OutlinedTextField(inputPrice, { inputPrice = it }, Modifier.fillMaxWidth(), label = { Text("Entrada normal / 1M") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(outputPrice, { outputPrice = it }, Modifier.fillMaxWidth(), label = { Text("Saída / 1M") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(cacheReadPrice, { cacheReadPrice = it }, Modifier.fillMaxWidth(), label = { Text("Leitura de cache / 1M (opcional)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(cacheCreationPrice, { cacheCreationPrice = it }, Modifier.fillMaxWidth(), label = { Text("Criação de cache / 1M (opcional)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            priceError?.let { ErrorText(it) }
            val input = inputPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
            val output = outputPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
            val cacheRead = cacheReadPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: input
            val cacheCreation = cacheCreationPrice.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 } ?: input
            if (inputPrice.isBlank() || outputPrice.isBlank()) {
                Muted("Informe os preços de entrada e saída para calcular o custo. A moeda usada será a mesma da tabela que você informar.")
            } else {
                Text("Custo estimado nesta sessão (moeda informada): " + "%.6f".format(model.usage.estimateCost(input, output, cacheRead, cacheCreation)), style = MaterialTheme.typography.bodyMedium)
            }
        }
        AppCard {
            Text("Privacidade e uso", style = MaterialTheme.typography.titleSmall)
            Muted("A chave é cifrada pelo Android Keystore e não entra em backups nem logs. A jogada que você escreve é enviada ao provedor escolhido.")
            Muted("Uso estimado nesta sessão: ${model.usage.calls} chamadas, ${model.usage.inputTokens} tokens de entrada, ${model.usage.outputTokens} de saída.")
            Muted("Cache do provedor: ${model.usage.cacheReadInputTokens} tokens reutilizados e ${model.usage.cacheCreationInputTokens} tokens gravados.")
        }
    }
}
