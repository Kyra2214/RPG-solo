package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
    var saved by remember { mutableStateOf(false) }
    var testing by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<ConnectionTestResult?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var backupMessage by remember { mutableStateOf<String?>(null) }
    val exportBackupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            scope.launch {
                backupMessage = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(model.exportBackup()) }
                            ?: error("não foi possível abrir o arquivo para escrita")
                        "Backup exportado. Guarde o arquivo em local seguro."
                    }.getOrElse { "Falha ao exportar backup: ${it.message ?: "erro desconhecido"}" }
                }
            }
        }
    }
    val importBackupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                backupMessage = withContext(Dispatchers.IO) {
                    runCatching {
                        val raw = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                            ?: error("não foi possível abrir o arquivo")
                        model.importBackup(raw) ?: "Backup importado. A campanha importada foi salva."
                    }.getOrElse { "Falha ao importar backup: ${it.message ?: "erro desconhecido"}" }
                }
            }
        }
    }

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
            Muted("Estas preferências mudam o texto, nunca os resultados do motor nem as regras do sistema.")
        }
        AppCard {
            Text("Backup da campanha", style = MaterialTheme.typography.titleSmall)
            Muted("Exporta eventos e metadados da campanha ativa para um arquivo JSON. A chave da API não faz parte do backup.")
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { exportBackupLauncher.launch("rpg-solo-backup.json") }
            ) { Text("Exportar backup") }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { importBackupLauncher.launch(arrayOf("application/json", "text/*")) }
            ) { Text("Importar backup") }
            backupMessage?.let { Text(it, color = if (it.startsWith("Falha")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) }
        }
        AppCard {
            Text("Privacidade e uso", style = MaterialTheme.typography.titleSmall)
            Muted("A chave é cifrada pelo Android Keystore e não entra em backups nem logs. A jogada que você escreve é enviada ao provedor escolhido.")
            Muted("Uso estimado nesta sessão: ${model.usage.calls} chamadas, ${model.usage.inputTokens} tokens de entrada, ${model.usage.outputTokens} de saída.")
        }
    }
}
