package dev.rpgsolo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rpgsolo.rules.CharacterDraft
import dev.rpgsolo.rules.FichaBuild
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.TraitDef

/**
 * Aba Personagens (10.2 e 5.0): criação guiada pelo pacote, sem PDF, com validação do motor e biblioteca de fichas.
 * Fluxo: arquétipo (ou ficha pronta) → atributos → cardápio do arquétipo → ajuste fino → criar.
 */
@Composable
fun CharactersScreen(model: AppModel) {
    val reg = model.content.registry
    val ids = reg.installed().map { it.id }
    var selected by remember { mutableStateOf(ids.firstOrNull()) }
    val pkg = selected?.let { reg.get(it) }

    ScreenColumn {
        if (pkg == null) {
            AppCard { Text("Instale um pacote de regras na aba Sistemas para criar personagens.") }
            return@ScreenColumn
        }
        if (ids.size > 1) {
            SectionTitle("Sistema")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ids.forEach { id -> FilterChip(selected = id == selected, onClick = { selected = id }, label = { Text(id) }) }
            }
        }
        Creator(model, pkg, selected!!)
    }
}

@Composable
private fun Creator(model: AppModel, pkg: RulePackage, key: String) {
    // Um rascunho por pacote; "version" força redesenhar depois de mudar o rascunho (ele não é observável).
    val draft = remember(key) { CharacterDraft(pkg, "f-${model.fichas.size + 1}") }
    var version by remember(key) { mutableIntStateOf(0) }
    var message by remember(key) { mutableStateOf<String?>(null) }
    var archId by remember(key) { mutableStateOf<String?>(null) }
    var archQuery by remember(key) { mutableStateOf("") }
    var traitQuery by remember(key) { mutableStateOf("") }
    var typeFilter by remember(key) { mutableStateOf("todos") }
    @Suppress("UNUSED_EXPRESSION") version

    fun toggle(id: String) {
        if (id in draft.traits()) { draft.removeTrait(id); message = null }
        else {
            val why = draft.whyNot(id)
            if (why.isEmpty()) { draft.addTrait(id); message = null } else message = why.joinToString("; ")
        }
        version++
    }

    // ---------- 1. Ponto de partida ----------
    SectionTitle("Ponto de partida", "Escolha um arquétipo: ele já traz atributos, perícias e um par de vantagem e desvantagem. Você muda o que quiser.")
    OutlinedTextField(
        archQuery, { archQuery = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar arquétipo (elfo, ladrão, mago…)") }, singleLine = true, shape = MaterialTheme.shapes.large
    )
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        pkg.archetypesSorted(archQuery).forEach { a ->
            FilterChip(
                selected = archId == a.id,
                onClick = {
                    if (draft.loadArchetype(a.id)) {
                        archId = a.id
                        if (draft.concept.isBlank()) draft.concept = a.name
                        message = null
                        version++
                    }
                },
                label = { Text(a.name) }
            )
        }
    }
    archId?.let { pkg.archetype(it) }?.let { a ->
        AppCard(accent = MaterialTheme.colorScheme.primary) {
            Text(a.name, style = MaterialTheme.typography.titleMedium)
            Muted(a.description)
        }
    }
    if (pkg.data.examples.isNotEmpty()) {
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pkg.data.examples.forEach { ex ->
                OutlinedButton(onClick = { draft.loadExample(ex.id); archId = null; version++ }) { Text(ex.title) }
            }
        }
    }

    // ---------- 2. Nome ----------
    OutlinedTextField(draft.name, { draft.name = it; version++ }, Modifier.fillMaxWidth(), label = { Text("Nome") }, singleLine = true)
    OutlinedTextField(draft.concept, { draft.concept = it; version++ }, Modifier.fillMaxWidth(), label = { Text("Conceito (livre)") })

    // ---------- 3. Atributos ----------
    SectionTitle("Atributos")
    AppCard {
        pkg.data.stats.forEach { st ->
            val v = draft.stats()[st.id] ?: st.default
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(st.name, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                OutlinedButton(enabled = v > st.min, onClick = { draft.setStat(st.id, v - 1); version++ }) { Text("−") }
                Text(v.toString(), Modifier.width(28.dp), style = MaterialTheme.typography.titleMedium)
                OutlinedButton(enabled = v < st.max, onClick = { draft.setStat(st.id, v + 1); version++ }) { Text("+") }
            }
        }
    }

    // ---------- 4. Contas da ficha ----------
    val report = draft.report()
    AppCard(accent = if (report.valid) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pontos", Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Text("${draft.pointsLeft()} sobrando de ${pkg.creationPoints}", style = MaterialTheme.typography.bodyMedium)
        }
        LinearProgressIndicator(
            progress = { (report.pointsSpent.toFloat() / pkg.creationPoints.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(), trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
        if (pkg.balanceTable.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vantagens x desvantagens", Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                val ok = report.disadvantages >= report.disadvantagesRequired
                Pill(
                    "${report.advantages} vant. · ${report.disadvantages}/${report.disadvantagesRequired} desv.",
                    if (ok) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
            }
            Muted("1 vantagem pede 1 desvantagem, 2 pedem 3, 3 pedem 5 e, da quarta em diante, o mínimo dobra a cada vantagem.")
        }
        report.errors.forEach { ErrorText(it) }
        report.warnings.forEach { Muted(it) }
    }

    // ---------- 5. Cardápio do arquétipo ----------
    val menu = archId?.let { pkg.archetypeOptions(it) }
    if (menu != null) {
        SectionTitle("Cardápio do arquétipo", "Sugestões que combinam com a classe. Misture à vontade com as gerais.")
        Text("Vantagens", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
        menu.advantages.mapNotNull { pkg.trait(it) }.forEach { TraitRow(it, it.id in draft.traits()) { toggle(it.id) } }
        Text("Desvantagens", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
        menu.disadvantages.mapNotNull { pkg.trait(it) }.forEach { TraitRow(it, it.id in draft.traits()) { toggle(it.id) } }
    }

    // ---------- 6. Escolhidos ----------
    if (draft.traits().isNotEmpty()) {
        SectionTitle("Na ficha agora")
        draft.traits().mapNotNull { pkg.trait(it) }.forEach { TraitRow(it, true) { toggle(it.id) } }
    }

    // ---------- 7. Todos os traços ----------
    SectionTitle("Todos os traços", "Qualquer traço do sistema, filtrado pelo que a ficha atual aceita.")
    OutlinedTextField(
        traitQuery, { traitQuery = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar traço (atrapalhado, visão, magia…)") }, singleLine = true, shape = MaterialTheme.shapes.large
    )
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("todos" to "Todos", "vantagem" to "Vantagens", "desvantagem" to "Desvantagens", "pericia" to "Perícias", "poder" to "Poderes")
            .forEach { (id, label) -> FilterChip(selected = typeFilter == id, onClick = { typeFilter = id }, label = { Text(label) }) }
    }
    val allowed = remember(version, key) { draft.allowedTraits() }
    val q = traitQuery.trim().lowercase()
    val shown = allowed.filter {
        (typeFilter == "todos" || it.type == typeFilter) &&
            (q.isEmpty() || it.name.lowercase().contains(q) || it.description.lowercase().contains(q))
    }
    Muted("${shown.size} traços disponíveis" + if (shown.size > 40) " (mostrando 40; use a busca)" else "")
    shown.take(40).forEach { TraitRow(it, false) { toggle(it.id) } }

    // ---------- 8. Criar ----------
    Button(modifier = Modifier.fillMaxWidth(), onClick = {
        message = when (val b = draft.build()) {
            is FichaBuild.Created -> { model.saveFicha(b.ficha); "Ficha de ${b.ficha.name} criada e salva." }
            is FichaBuild.Invalid -> b.errors.joinToString("; ")
        }
        version++
    }) { Text("Criar ficha") }
    message?.let { Muted(it) }

    if (model.fichas.isNotEmpty()) {
        SectionTitle("Biblioteca")
        model.fichas.forEach { f ->
            AppCard {
                Text(f.name, style = MaterialTheme.typography.titleSmall)
                Muted("${f.concept.ifBlank { "sem conceito" }} · ${f.system.editionId}")
            }
        }
    }
}

@Composable
private fun TraitRow(t: TraitDef, chosen: Boolean, onToggle: () -> Unit) {
    val (label, color) = when (t.type) {
        "vantagem" -> "Vantagem" to MaterialTheme.colorScheme.tertiary
        "poder" -> "Poder" to MaterialTheme.colorScheme.tertiary
        "desvantagem" -> "Desvantagem" to MaterialTheme.colorScheme.error
        else -> "Perícia" to MaterialTheme.colorScheme.secondary
    }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(t.name, style = MaterialTheme.typography.titleSmall)
                    Pill(label, color)
                    if (t.cost != 0) Pill("custo ${t.cost}", MaterialTheme.colorScheme.outline)
                }
                Muted(t.description)
            }
            OutlinedButton(onClick = onToggle) { Text(if (chosen) "Tirar" else "Pegar") }
        }
    }
}
