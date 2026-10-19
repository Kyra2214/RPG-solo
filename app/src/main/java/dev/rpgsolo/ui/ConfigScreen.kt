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
import dev.rpgsolo.ai.ProviderKind
import dev.rpgsolo.ai.maskKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var narrationLength by remember { mutableStateOf(model.narrationLength) }
    var narrationStyle by remember { mutableStateOf(model.narrationStyle) }
    var contentLines by remember { mutableStateOf(model.contentLines) }
    var contentVeils by remember { mutableStateOf(model.contentVeils) }
    var saved by remember { mutableStateOf(false) }
    var testing by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<ConnectionTestResult?>(null) }
    val scope = rememberCoroutineScope()
    ScreenColumn {
        SectionTitle("Provedor de IA", "Quem faz o papel de mestre. O motor continua sendo quem rola os dados.")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProviderKind.values().forEach { k ->
                FilterChip(selected = kind == k, onClick = { kind = k }, label = { Text(k.label) })
            }
        }
        OutlinedTextField(endpoint, { endpoint = it }, Modifier.fillMaxWidth(), label = { Text("Endpoint (https)") }, singleLine = true)
        OutlinedTextField(modelName, { modelName = it }, Modifier.fillMaxWidth(), label = { Text("Modelo") }, singleLine = true)
        OutlinedTextField(cheapModel, { cheapModel = it }, Modifier.fillMaxWidth(), label = { Text("Modelo barato (opcional): classifica e resume") }, singleLine = true)
        OutlinedTextField(premiumModel, { premiumModel = it }, Modifier.fillMaxWidth(), label = { Text("Modelo melhor (opcional): só momentos importantes") }, singleLine = true)
        OutlinedTextField(
            key, { key = it }, Modifier.fillMaxWidth(), label = { Text("Chave de API") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !testing,
            onClick = {
                testing = true
                testResult = null
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        model.testConnection(kind, endpoint, modelName, key)
                    }
                    testResult = result
                    testing = false
                }
            }
        ) { Text(if (testing) "Testando conexão…" else "Testar conexão") }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { saved = model.applySettings(kind, endpoint, modelName, key, cheapModel, premiumModel) }) { Text("Salvar e usar") }
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            model.forgetProvider()
            key = ""
            saved = false
        }) { Text("Esquecer chave e desativar provedor") }
        model.configError?.let { ErrorText(it) }
        testResult?.let { result ->
            if (result.success) {
                AppCard(accent = MaterialTheme.colorScheme.tertiary) { Text(result.message) }
            } else {
                ErrorText(result.message)
            }
        }
        if (saved && model.configError == null) AppCard(accent = MaterialTheme.colorScheme.tertiary) { Text("Provedor ativo. Chave: ${maskKey(key)}") }
        AppCard {
            Text("Acessibilidade", style = MaterialTheme.typography.titleSmall)
            Text("Tamanho global da fonte: ${(model.fontScale * 100).toInt()}%")
            Slider(
                value = model.fontScale,
                onValueChange = model::setFontScale,
                valueRange = 0.85f..1.4f,
                steps = 10
            )
            Muted("Ajuste o texto de toda a interface; a preferência fica salva no aparelho.")
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
            Text("Privacidade e uso", style = MaterialTheme.typography.titleSmall)
            Muted("A chave é cifrada pelo Android Keystore e não entra em backups nem logs. A jogada que você escreve é enviada ao provedor escolhido.")
            Muted("Uso estimado nesta sessão: ${model.usage.calls} chamadas, ${model.usage.inputTokens} tokens de entrada, ${model.usage.outputTokens} de saída.")
            Muted("Cache do provedor: ${model.usage.cacheReadInputTokens} tokens reutilizados e ${model.usage.cacheCreationInputTokens} tokens gravados.")
        }
    }
}
