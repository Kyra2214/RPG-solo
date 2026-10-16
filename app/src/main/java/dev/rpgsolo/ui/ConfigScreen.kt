package dev.rpgsolo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.rpgsolo.ai.ProviderKind
import dev.rpgsolo.ai.maskKey

/** Aba Config (10.6.1/10.6.4): provedor, endpoint, modelo, chave mascarada e uso estimado. */
@Composable
fun ConfigScreen(model: AppModel) {
    var kind by remember { mutableStateOf(ProviderKind.OPENAI_COMPATIBLE) }
    var endpoint by remember { mutableStateOf("https://") }
    var modelName by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var cheapModel by remember { mutableStateOf("") }
    var premiumModel by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Provedor de IA", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        Button(onClick = { saved = model.applySettings(kind, endpoint, modelName, key, cheapModel, premiumModel) }) { Text("Salvar e usar") }
        model.configError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (saved && model.configError == null) Text("Provedor ativo. Chave: ${maskKey(key)}")
        Text(
            "A chave fica só na memória do app nesta versão e é apagada ao fechar. A jogada que você escreve é enviada ao provedor escolhido.",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            "Uso estimado nesta sessão: ${model.usage.calls} chamadas, ${model.usage.inputTokens} tokens de entrada, ${model.usage.outputTokens} de saída.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
