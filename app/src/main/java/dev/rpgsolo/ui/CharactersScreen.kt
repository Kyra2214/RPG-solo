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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.rpgsolo.rules.CharacterDraft
import dev.rpgsolo.rules.FichaBuild
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.TraitDef

/**
 * Aba Personagens (10.2 e 5.0): criação guiada pelo pacote, com links só de consulta, com validação do motor e biblioteca de fichas.
 * Fluxo: arquétipo (ou ficha pronta) → atributos → cardápio do arquétipo → ajuste fino → criar.
 */
@Composable
fun CharactersScreen(model: AppModel) {
    val packageVersion = model.packageVersion
    @Suppress("UNUSED_VARIABLE") val observePackageVersion = packageVersion
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
        ConsultLinks(model)
    }
}

/** Links de consulta (3DeT, D&D...) para o jogador olhar as regras oficiais ao criar o personagem. Só abre o navegador. */
@Composable
private fun ConsultLinks(model: AppModel) {
    val links = model.content.sources?.links.orEmpty()
    if (links.isEmpty()) return
    val uri = LocalUriHandler.current
    SectionTitle("Consultar regras oficiais", "Só para consulta: abre no navegador. As regras do jogo são as do pacote acima.")
    AppCard {
        links.forEach { link ->
            TextButton(onClick = { uri.openUri(link.url) }) {
                Text(link.title + if (link.verified) "" else " (link não verificado)")
            }
        }
    }
}

@Composable
private fun Creator(model: AppModel, pkg: RulePackage, key: String) {
    // Um rascunho por pacote; "version" força redesenhar depois de mudar o rascunho (ele não é observável).
    val draft = remember(key) { CharacterDraft(pkg, "f-${model.fichas.size + 1}") }
    var version by remember(key) { mutableIntStateOf(0) }
    var message by remember(key) { mutableStateOf<String?>(null) }
    var libraryMessage by remember(key) { mutableStateOf<String?>(null) }
    var fichaImportText by remember(key) { mutableStateOf("") }
    var expandedFichaId by remember(key) { mutableStateOf<String?>(null) }
    val clipboard = LocalClipboardManager.current
    var archId by remember(key) { mutableStateOf<String?>(null) }
    var archQuery by remember(key) { mutableStateOf("") }
    var traitQuery by remember(key) { mutableStateOf("") }
    var typeFilter by remember(key) { mutableStateOf("todos") }
    var raceId by remember(key) { mutableStateOf("") }
    var subraceId by remember(key) { mutableStateOf("") }
    var classId by remember(key) { mutableStateOf("") }
    var professionId by remember(key) { mutableStateOf("") }
    var hybridId by remember(key) { mutableStateOf("") }
    var curseIds by remember(key) { mutableStateOf(setOf<String>()) }
    val choices = CharacterChoices(raceId, subraceId, classId, professionId, curseIds.toList(), hybridId)
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

    // ---------- 2. Origem e identidade de aventura ----------
    SectionTitle("Raça, linhagem, classe e profissão", "Escolhas editoriais do catálogo. Híbridos são combinações catalogadas, não sub-raças. Nenhuma opção aplica bônus automaticamente.")
    val catalogErrors = CharacterCreationCatalog.validationErrors()
    if (catalogErrors.isNotEmpty()) AppCard { Text("Catálogo com referências inválidas", color = MaterialTheme.colorScheme.error); catalogErrors.take(5).forEach { Muted(it) } }
    val catalogRaces = CharacterCreationCatalog.races
    val catalogSubraces = CharacterCreationCatalog.subraces.filter { it.parentRaceId == raceId }
    CatalogPicker("Raça", catalogRaces, raceId, { selected ->
        raceId = selected
        if (selected.isNotBlank()) hybridId = ""
        if (subraceId.isNotBlank() && CharacterCreationCatalog.subraces.none { it.id == subraceId && it.parentRaceId == selected }) subraceId = ""
        version++
    })
    if (raceId.isNotBlank()) {
        CatalogPicker("Sub-raça / variante (opcional)", catalogSubraces, subraceId, { subraceId = it; version++ }, allowNone = true)
    }
    CatalogPicker("Híbrido / ascendência mista (opcional)", CharacterCreationCatalog.hybrids, hybridId, { selected ->
        hybridId = selected
        if (selected.isNotBlank()) { raceId = ""; subraceId = "" }
        version++
    }, allowNone = true)
    hybridId.takeIf { it.isNotBlank() }?.let { selectedId ->
        CharacterCreationCatalog.find(selectedId)?.let { entry ->
            AppCard {
                Text(entry.name, style = MaterialTheme.typography.titleSmall)
                Muted("Ascendência: " + entry.hybridRaceIds.mapNotNull { CharacterCreationCatalog.find(it)?.name }.joinToString(" + "))
                if (entry.lore.isNotBlank()) Muted(entry.lore)
            }
        }
    }
    CatalogPicker("Classe", CharacterCreationCatalog.classes, classId, { classId = it; version++ }, allowNone = true)
    CatalogPicker("Profissão", CharacterCreationCatalog.professions, professionId, { professionId = it; version++ }, allowNone = true)

    SectionTitle("Maldições", "Podem trazer benefícios e custos narrativos. Os efeitos ainda não são aplicados pelo motor.")
    CatalogMultiPicker("Escolha as maldições", CharacterCreationCatalog.curses, curseIds, { id ->
        curseIds = if (id in curseIds) curseIds - id else curseIds + id
        version++
    })
    val chosenCatalogEntries = (listOf(raceId, subraceId, hybridId, classId, professionId) + curseIds.toList())
        .filter { it.isNotBlank() }.mapNotNull { CharacterCreationCatalog.find(it) }
    if (chosenCatalogEntries.isNotEmpty()) {
        AppCard {
            Text("Resumo das escolhas", style = MaterialTheme.typography.titleSmall)
            chosenCatalogEntries.forEach { entry ->
                Text(entry.name, style = MaterialTheme.typography.bodyMedium)
                if (entry.summary.isNotBlank()) Muted(entry.summary)
                if (entry.lore.isNotBlank() && entry.lore != entry.summary) Muted("Lore: " + entry.lore)
                if (entry.benefit.isNotBlank()) Muted("Benefício descrito (não aplicado): " + entry.benefit)
                if (entry.drawback.isNotBlank()) Muted("Custo descrito (não aplicado): " + entry.drawback)
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

    // ---------- 4. Pontos de perícia ----------
    val skills = pkg.data.traits.filter { it.type == "pericia" }
    if (skills.isNotEmpty()) {
        SectionTitle("Perícias", "Distribua de 0 a 5 pontos. Cada ponto consome 1 ponto de criação.")
        AppCard {
            skills.forEach { skill ->
                val value = draft.skills()[skill.id] ?: 0
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(skill.name, style = MaterialTheme.typography.titleSmall)
                        Muted(skill.description)
                    }
                    OutlinedButton(enabled = value > 0, onClick = { draft.setSkill(skill.id, value - 1); version++ }) { Text("−") }
                    Text(value.toString(), Modifier.width(28.dp), style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(enabled = value < 5 && draft.pointsLeft() > 0, onClick = { draft.setSkill(skill.id, value + 1); version++ }) { Text("+") }
                }
            }
        }
    }

    // ---------- 5. Contas da ficha ----------
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
            is FichaBuild.Created -> { model.saveFicha(b.ficha); model.saveCharacterChoices(b.ficha.id, choices); "Ficha de ${b.ficha.name} criada e salva." }
            is FichaBuild.Invalid -> b.errors.joinToString("; ")
        }
        version++
    }) { Text("Criar ficha") }
    message?.let { Muted(it) }

    SectionTitle("Importar ficha por JSON", "Aceita exportações novas com escolhas editoriais e JSONs antigos contendo apenas a ficha.")
    AppCard {
        OutlinedTextField(
            value = fichaImportText,
            onValueChange = { fichaImportText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("JSON da ficha") },
            placeholder = { Text("Cole aqui o JSON exportado") },
            minLines = 3,
            maxLines = 8
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                libraryMessage = model.importFicha(fichaImportText)
                if (libraryMessage?.startsWith("Ficha ") == true && libraryMessage?.contains("importada") == true) fichaImportText = ""
                version++
            }
        ) { Text("Importar ficha") }
    }

    if (model.fichas.isNotEmpty()) {
        SectionTitle("Biblioteca")
        model.fichas.forEach { f ->
            AppCard {
                Text(f.name, style = MaterialTheme.typography.titleSmall)
                Muted("${f.concept.ifBlank { "sem conceito" }} · ${f.system.editionId}")
                val savedChoices = model.characterChoices(f.id)
                val savedNames = (listOf(savedChoices.raceId, savedChoices.subraceId, savedChoices.hybridId, savedChoices.classId, savedChoices.professionId) + savedChoices.curseIds)
                    .filter { it.isNotBlank() }.mapNotNull { CharacterCreationCatalog.find(it)?.name }
                if (savedNames.isNotEmpty()) Muted("Origem: " + savedNames.joinToString(" · "))
                TextButton(onClick = { expandedFichaId = if (expandedFichaId == f.id) null else f.id }) {
                    Text(if (expandedFichaId == f.id) "Ocultar ficha" else "Ver ficha")
                }
                if (expandedFichaId == f.id) {
                    val fichaPkg = dev.rpgsolo.rules.CampaignStarter.packageFor(reg, f.system.editionId)
                    Muted("Atributos: " + f.stats.entries.joinToString(", ") { (id, value) ->
                        "${fichaPkg?.stat(id)?.name ?: id}: $value"
                    })
                    if (f.skills.isNotEmpty()) Muted("Perícias: " + f.skills.entries.joinToString(", ") { (id, value) ->
                        "${fichaPkg?.trait(id)?.name ?: id}: $value"
                    })
                    if (f.traits.isNotEmpty()) Muted("Traços: " + f.traits.joinToString(", ") { id -> fichaPkg?.trait(id)?.name ?: id })
                    if (f.resources.isNotEmpty()) Muted("Recursos: " + f.resources.entries.joinToString(", ") { (id, resource) ->
                        "${id}: ${resource.current}/${resource.max}"
                    })
                    if (f.adaptations.isNotEmpty()) Muted("Adaptações: " + f.adaptations.joinToString(", "))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { libraryMessage = model.duplicateFicha(f.id) ?: "Ficha duplicada." }) { Text("Duplicar") }
                    TextButton(onClick = { libraryMessage = model.archiveFicha(f.id, true) ?: "Ficha arquivada." }) { Text("Arquivar") }
                    TextButton(onClick = {
                        val raw = model.exportFicha(f.id)
                        if (raw != null) {
                            clipboard.setText(AnnotatedString(raw))
                            libraryMessage = "JSON da ficha copiado."
                        } else libraryMessage = "Não foi possível exportar a ficha."
                    }) { Text("Copiar JSON") }
                }
            }
        }
    }
    if (model.archivedFichas.isNotEmpty()) {
        SectionTitle("Fichas arquivadas")
        model.archivedFichas.forEach { f ->
            AppCard {
                Text(f.name, style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { libraryMessage = model.archiveFicha(f.id, false) ?: "Ficha restaurada." }) { Text("Restaurar") }
                    TextButton(onClick = { libraryMessage = model.deleteFicha(f.id) ?: "Ficha excluída." }) { Text("Excluir") }
                }
            }
        }
    }
    libraryMessage?.let { Muted(it) }
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

@Composable
private fun CatalogPicker(
    title: String,
    entries: List<CharacterCatalogEntry>,
    selectedId: String,
    onSelected: (String) -> Unit,
    allowNone: Boolean = false
) {
    var query by remember(title) { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar $title") },
            singleLine = true
        )
        if (allowNone && selectedId.isNotBlank()) {
            TextButton(onClick = { onSelected("") }) { Text("Limpar seleção") }
        }
        val q = query.trim().lowercase()
        val visible = entries.filter { q.isEmpty() || it.name.lowercase().contains(q) || it.summary.lowercase().contains(q) }.take(30)
        if (visible.isEmpty()) Muted("Nenhuma opção encontrada.")
        visible.forEach { entry ->
            FilterChip(
                selected = selectedId == entry.id,
                onClick = { onSelected(if (selectedId == entry.id && allowNone) "" else entry.id) },
                label = { Text(entry.name) }
            )
            if (selectedId == entry.id && entry.summary.isNotBlank()) Muted(entry.summary)
        }
        if (entries.size > visible.size) Muted("Mostrando até 30 opções; use a busca para localizar as demais.")
    }
}

@Composable
private fun CatalogMultiPicker(
    title: String,
    entries: List<CharacterCatalogEntry>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit
) {
    var query by remember(title) { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar maldição") },
            singleLine = true
        )
        Text("${selectedIds.size} selecionada(s)", style = MaterialTheme.typography.labelMedium)
        val q = query.trim().lowercase()
        val visible = entries.filter { q.isEmpty() || it.name.lowercase().contains(q) || it.summary.lowercase().contains(q) }.take(30)
        if (visible.isEmpty()) Muted("Nenhuma maldição encontrada.")
        visible.forEach { entry ->
            FilterChip(
                selected = entry.id in selectedIds,
                onClick = { onToggle(entry.id) },
                label = { Text(entry.name) }
            )
            if (entry.id in selectedIds && entry.summary.isNotBlank()) Muted(entry.summary)
        }
        if (entries.size > visible.size) Muted("Catálogo com ${entries.size} opções; busque pelo nome para encontrar outras.")
    }
}
