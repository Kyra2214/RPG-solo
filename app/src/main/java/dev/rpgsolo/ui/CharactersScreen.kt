package dev.rpgsolo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.rpgsolo.R
import dev.rpgsolo.rules.CharacterDraft
import dev.rpgsolo.rules.FichaBuild
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.TraitDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Aba Personagens (10.2 e 5.0): criação guiada pelo pacote, com validação do motor e biblioteca de fichas.
 * Fluxo: arquétipo (ou ficha pronta) → atributos → cardápio do arquétipo → ajuste fino → criar.
 */
@Composable
fun CharactersScreen(model: AppModel) {
    val packageVersion = model.packageVersion

    @Suppress("UNUSED_VARIABLE")
    val observePackageVersion = packageVersion
    val reg = model.content.registry
    val ids = reg.installed().map { it.id }
    var selected by remember { mutableStateOf(ids.firstOrNull()) }
    val pkg = selected?.let { reg.get(it) }

    ScreenColumn {
        if (pkg == null) {
            AppCard { Text(stringResource(R.string.char_no_package)) }
            return@ScreenColumn
        }
        if (ids.size > 1) {
            SectionTitle(stringResource(R.string.char_system))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ids.forEach { id -> FilterChip(selected = id == selected, onClick = { selected = id }, label = { Text(id) }) }
            }
        }
        Creator(model, pkg, selected!!)
    }
}

@Composable
private fun Creator(
    model: AppModel,
    pkg: RulePackage,
    key: String,
) {
    // Um rascunho por pacote. Depois de salvar, gira o ID para que a próxima criação
    // não sobrescreva a ficha recém-criada.
    var draftId by remember(key) { mutableStateOf(model.draftFichaId()) }
    val draft = remember(key, draftId) { CharacterDraft(pkg, draftId) }
    var version by remember(key) { mutableIntStateOf(0) }
    var message by remember(key) { mutableStateOf<String?>(null) }
    var libraryMessage by remember(key) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val unexpectedError = stringResource(R.string.char_action_failed)
    val context = LocalContext.current
    val notSelected = stringResource(R.string.char_not_selected)
    val idNotFound = stringResource(R.string.char_id_not_found)
    val noneLabel = stringResource(R.string.char_none_fem)
    val noneMasc = stringResource(R.string.char_none_masc)
    val decreaseLabel: (String) -> String = { context.getString(R.string.char_decrease, it) }
    val increaseLabel: (String) -> String = { context.getString(R.string.char_increase, it) }
    var dbBusy by remember(key) { mutableStateOf(false) }

    /** Gravações no banco rodam em IO e desabilitam os botões enquanto duram (R-02). */
    fun io(
        work: () -> String?,
        done: (String?) -> Unit,
    ) {
        if (dbBusy) return
        dbBusy = true
        scope.launch {
            val result =
                try {
                    withContext(Dispatchers.IO) { work() }
                } catch (e: Exception) {
                    e.message ?: unexpectedError
                } finally {
                    dbBusy = false
                }
            done(result)
            version++
        }
    }
    var fichaImportText by remember(key) { mutableStateOf("") }
    var expandedFichaId by remember(key) { mutableStateOf<String?>(null) }
    var editingChoicesFichaId by remember(key) { mutableStateOf<String?>(null) }
    val loreDrafts = remember(key) { androidx.compose.runtime.mutableStateMapOf<String, String>() }
    val loreAppearance = remember(key) { androidx.compose.runtime.mutableStateMapOf<String, String>() }
    val loreIdea = remember(key) { androidx.compose.runtime.mutableStateMapOf<String, String>() }
    var portraitTargetId by remember(key) { mutableStateOf<String?>(null) }
    var portraitVersion by remember(key) { mutableStateOf(0) }
    val portraitPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val target = portraitTargetId
            portraitTargetId = null
            if (uri != null && target != null) {
                io({ model.setPortrait(target, uri) }) {
                    portraitVersion++
                    libraryMessage = it ?: context.getString(R.string.char_portrait_saved)
                }
            }
        }
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
    var elementalAffinity by remember(key) { mutableStateOf("") }
    var cursedClassBranchId by remember(key) { mutableStateOf("") }
    var additionalClassIds by remember(key) { mutableStateOf(emptyList<String>()) }
    val choices =
        CharacterChoices(raceId, subraceId, classId, professionId, curseIds.toList(), hybridId, elementalAffinity, cursedClassBranchId, additionalClassIds)
    val choiceErrors = (CharacterCreationCatalog.validateChoices(choices) + CharacterCreationCatalog.validateEffectScopes(pkg, choices)).distinct()
    @Suppress("UNUSED_EXPRESSION")
    version

    fun toggle(id: String) {
        if (id in draft.traits()) {
            draft.removeTrait(id)
            message = null
        } else {
            val why = draft.whyNot(id)
            if (why.isEmpty()) {
                draft.addTrait(id)
                message = null
            } else {
                message = why.joinToString("; ")
            }
        }
        version++
    }

    // ---------- 1. Ponto de partida ----------
    SectionTitle(
        stringResource(R.string.char_start_title),
        stringResource(R.string.char_start_help),
    )
    OutlinedTextField(
        archQuery,
        { archQuery = it },
        Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.char_archetype_search)) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
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
                label = { Text(a.name) },
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
                OutlinedButton(onClick = {
                    draft.loadExample(ex.id)
                    archId = null
                    version++
                }) { Text(ex.title) }
            }
        }
    }

    // ---------- 2. Origem e identidade de aventura ----------
    SectionTitle(
        stringResource(R.string.char_origin_title),
        stringResource(R.string.char_origin_help),
    )
    val catalogErrors = CharacterCreationCatalog.validationErrors()
    if (catalogErrors.isNotEmpty()) {
        AppCard {
            Text(stringResource(R.string.char_catalog_invalid), color = MaterialTheme.colorScheme.error)
            catalogErrors.take(5).forEach { Muted(it) }
        }
    }
    if (choiceErrors.isNotEmpty()) {
        AppCard {
            Text(stringResource(R.string.char_review_choices), color = MaterialTheme.colorScheme.error)
            choiceErrors.forEach { Muted(it) }
        }
    }
    val catalogRaces = CharacterCreationCatalog.races
    val catalogSubraces = CharacterCreationCatalog.subraces.filter { it.parentRaceId == raceId }
    CatalogPicker(stringResource(R.string.char_race), catalogRaces, raceId, { selected ->
        raceId = selected
        if (selected.isNotBlank()) hybridId = ""
        if (subraceId.isNotBlank() && CharacterCreationCatalog.subraces.none { it.id == subraceId && it.parentRaceId == selected }) subraceId = ""
        version++
    })
    if (raceId.isNotBlank()) {
        CatalogPicker(stringResource(R.string.char_subrace), catalogSubraces, subraceId, {
            subraceId = it
            version++
        }, allowNone = true)
    }
    CatalogPicker(stringResource(R.string.char_hybrid), CharacterCreationCatalog.hybrids, hybridId, { selected ->
        hybridId = selected
        if (selected.isNotBlank()) {
            raceId = ""
            subraceId = ""
        }
        version++
    }, allowNone = true)
    hybridId.takeIf { it.isNotBlank() }?.let { selectedId ->
        CharacterCreationCatalog.find(selectedId)?.let { entry ->
            AppCard {
                Text(entry.name, style = MaterialTheme.typography.titleSmall)
                Muted(
                    stringResource(
                        R.string.char_ancestry,
                        entry.hybridRaceIds.mapNotNull { CharacterCreationCatalog.find(it)?.name }.joinToString(" + "),
                    ),
                )
                if (entry.lore.isNotBlank()) Muted(entry.lore)
            }
        }
    }
    CatalogPicker(stringResource(R.string.char_main_class), CharacterCreationCatalog.classes, classId, {
        classId = it
        additionalClassIds = additionalClassIds - it
        version++
    }, allowNone = true)
    SectionTitle(
        stringResource(R.string.char_extra_classes_title),
        stringResource(R.string.char_extra_classes_help),
    )
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CharacterCreationCatalog.classes.filter { it.id != classId }.forEach { option ->
            FilterChip(
                selected = option.id in additionalClassIds,
                onClick = {
                    additionalClassIds =
                        if (option.id in additionalClassIds) {
                            additionalClassIds - option.id
                        } else if (additionalClassIds.size < 3) {
                            additionalClassIds + option.id
                        } else {
                            additionalClassIds
                        }
                    version++
                },
                label = { Text(option.name) },
            )
        }
    }
    val totalClasses = (if (classId.isBlank()) 0 else 1) + additionalClassIds.size
    val hybridAppearance =
        when (totalClasses) {
            0, 1 -> stringResource(R.string.char_appearance_normal)
            2 -> stringResource(R.string.char_appearance_visible)
            3 -> stringResource(R.string.char_appearance_monstrous)
            else -> stringResource(R.string.char_appearance_extreme)
        }
    Muted(stringResource(R.string.char_combined_classes, totalClasses, hybridAppearance))
    SectionTitle(
        stringResource(R.string.char_element_title),
        stringResource(R.string.char_element_help),
    )
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            "WATER" to stringResource(R.string.char_element_water),
            "FIRE" to stringResource(R.string.char_element_fire),
            "WIND" to stringResource(R.string.char_element_wind),
            "EARTH" to stringResource(R.string.char_element_earth),
            "METAL" to stringResource(R.string.char_element_metal),
            "YIN" to stringResource(R.string.char_element_yin),
            "YANG" to stringResource(R.string.char_element_yang),
        ).forEach {
                (id, label) ->
            FilterChip(selected = elementalAffinity == id, onClick = {
                elementalAffinity = if (elementalAffinity == id) "" else id
                version++
            }, label = { Text(label) })
        }
    }
    CatalogPicker(stringResource(R.string.char_profession), CharacterCreationCatalog.professions, professionId, {
        professionId = it
        version++
    }, allowNone = true)

    SectionTitle(stringResource(R.string.char_cursed_title), stringResource(R.string.char_cursed_help))
    CatalogPicker(stringResource(R.string.char_cursed_branch), CharacterCreationCatalog.cursedClassBranches, cursedClassBranchId, {
        cursedClassBranchId = it
        version++
    }, allowNone = true)

    val chosenCatalogEntries =
        (listOf(raceId, subraceId, hybridId, cursedClassBranchId, classId, professionId) + additionalClassIds + curseIds.toList())
            .filter { it.isNotBlank() }.mapNotNull { CharacterCreationCatalog.find(it) }
    if (chosenCatalogEntries.isNotEmpty()) {
        AppCard {
            Text(stringResource(R.string.char_summary_title), style = MaterialTheme.typography.titleSmall)
            chosenCatalogEntries.forEach { entry ->
                Text(entry.name, style = MaterialTheme.typography.bodyMedium)
                if (entry.summary.isNotBlank()) Muted(entry.summary)
                if (entry.lore.isNotBlank() && entry.lore != entry.summary) Muted(stringResource(R.string.char_lore, entry.lore))
                if (entry.benefits.isNotEmpty()) {
                    entry.benefits.forEach { Muted(stringResource(R.string.char_benefit_summary, it)) }
                } else if (entry.benefit.isNotBlank()) {
                    Muted(stringResource(R.string.char_benefit_summary, entry.benefit))
                }
                if (entry.drawbacks.isNotEmpty()) {
                    entry.drawbacks.forEach { Muted(stringResource(R.string.char_drawback_summary, it)) }
                } else if (entry.drawback.isNotBlank()) {
                    Muted(stringResource(R.string.char_drawback_summary, entry.drawback))
                }
                entry.mechanicalEffects.forEach {
                    Muted(
                        stringResource(
                            R.string.char_effect_declared,
                            entry.name,
                            it.scope,
                            (if (it.bonus >= 0) "+" else "") + it.bonus,
                        ),
                    )
                }
            }
        }
    }

    editingChoicesFichaId?.let { fichaId ->
        AppCard {
            Text(
                stringResource(R.string.char_editing_choices, model.fichas.firstOrNull { it.id == fichaId }?.name ?: fichaId),
                style = MaterialTheme.typography.titleSmall,
            )
            Muted(stringResource(R.string.char_editing_help))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = choiceErrors.isEmpty() && !dbBusy, onClick = {
                    io({
                        model.saveCharacterChoices(fichaId, choices)
                        null
                    }) {
                        editingChoicesFichaId = null
                        libraryMessage = context.getString(R.string.char_choices_updated)
                    }
                }) { Text(stringResource(R.string.char_save_choices)) }
                TextButton(onClick = { editingChoicesFichaId = null }) { Text(stringResource(R.string.char_cancel)) }
            }
        }
    }

    // ---------- 2. Nome ----------
    OutlinedTextField(draft.name, {
        draft.name = it
        version++
    }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.char_name)) }, singleLine = true)
    OutlinedTextField(draft.concept, {
        draft.concept = it
        version++
    }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.char_concept)) })

    // ---------- 3. Atributos ----------
    SectionTitle(stringResource(R.string.char_attributes))
    AppCard {
        pkg.data.stats.forEach { st ->
            val v = draft.stats()[st.id] ?: st.default
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(st.name, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                OutlinedButton(enabled = v > st.min, onClick = {
                    draft.setStat(st.id, v - 1)
                    version++
                }, modifier = Modifier.semantics { contentDescription = decreaseLabel(st.name) }) { Text("−") }
                Text(v.toString(), Modifier.width(28.dp), style = MaterialTheme.typography.titleMedium)
                OutlinedButton(enabled = v < st.max, onClick = {
                    draft.setStat(st.id, v + 1)
                    version++
                }, modifier = Modifier.semantics { contentDescription = increaseLabel(st.name) }) { Text("+") }
            }
        }
    }

    // ---------- 4. Pontos de perícia ----------
    val skills = pkg.data.traits.filter { it.type == "pericia" }
    if (skills.isNotEmpty()) {
        SectionTitle(stringResource(R.string.char_skills_title), stringResource(R.string.char_skills_help))
        AppCard {
            skills.forEach { skill ->
                val value = draft.skills()[skill.id] ?: 0
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(skill.name, style = MaterialTheme.typography.titleSmall)
                        Muted(skill.description)
                    }
                    OutlinedButton(enabled = value > 0, onClick = {
                        draft.setSkill(skill.id, value - 1)
                        version++
                    }, modifier = Modifier.semantics { contentDescription = decreaseLabel(skill.name) }) { Text("−") }
                    Text(value.toString(), Modifier.width(28.dp), style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(enabled = value < 5 && draft.pointsLeft() > 0, onClick = {
                        draft.setSkill(skill.id, value + 1)
                        version++
                    }, modifier = Modifier.semantics { contentDescription = increaseLabel(skill.name) }) { Text("+") }
                }
            }
        }
    }

    // ---------- 5. Contas da ficha ----------
    val report = draft.report()
    AppCard(accent = if (report.valid) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.char_points), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.char_points_left, draft.pointsLeft(), pkg.creationPoints),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        LinearProgressIndicator(
            progress = { (report.pointsSpent.toFloat() / pkg.creationPoints.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        if (pkg.balanceTable.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.char_balance), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                val ok = report.disadvantages >= report.disadvantagesRequired
                Pill(
                    stringResource(R.string.char_balance_pill, report.advantages, report.disadvantages, report.disadvantagesRequired),
                    if (ok) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                )
            }
            Muted(stringResource(R.string.char_balance_help))
        }
        report.errors.forEach { ErrorText(it) }
        report.warnings.forEach { Muted(it) }
    }

    // ---------- 5. Cardápio do arquétipo ----------
    val menu = archId?.let { pkg.archetypeOptions(it) }
    if (menu != null) {
        SectionTitle(stringResource(R.string.char_menu_title), stringResource(R.string.char_menu_help))
        Text(
            stringResource(R.string.char_advantages),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
        menu.advantages.mapNotNull { pkg.trait(it) }.forEach { TraitRow(it, it.id in draft.traits()) { toggle(it.id) } }
        Text(
            stringResource(R.string.char_disadvantages),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error,
        )
        menu.disadvantages.mapNotNull { pkg.trait(it) }.forEach { TraitRow(it, it.id in draft.traits()) { toggle(it.id) } }
    }

    // ---------- 6. Escolhidos ----------
    if (draft.traits().isNotEmpty()) {
        SectionTitle(stringResource(R.string.char_on_sheet))
        draft.traits().mapNotNull { pkg.trait(it) }.forEach { TraitRow(it, true) { toggle(it.id) } }
    }

    // ---------- 7. Todos os traços ----------
    SectionTitle(stringResource(R.string.char_all_traits_title), stringResource(R.string.char_all_traits_help))
    OutlinedTextField(
        traitQuery,
        { traitQuery = it },
        Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.char_trait_search)) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
    )
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            "todos" to stringResource(R.string.char_filter_all),
            "vantagem" to stringResource(R.string.char_advantages),
            "desvantagem" to stringResource(R.string.char_disadvantages),
            "pericia" to stringResource(R.string.char_skills_title),
            "poder" to stringResource(R.string.char_filter_powers),
        )
            .forEach { (id, label) -> FilterChip(selected = typeFilter == id, onClick = { typeFilter = id }, label = { Text(label) }) }
    }
    val allowed = remember(version, key) { draft.allowedTraits() }
    val q = traitQuery.trim().lowercase()
    val shown =
        allowed.filter {
            (typeFilter == "todos" || it.type == typeFilter) &&
                (q.isEmpty() || it.name.lowercase().contains(q) || it.description.lowercase().contains(q))
        }
    Muted(
        stringResource(R.string.char_traits_available, shown.size) +
            if (shown.size > 40) stringResource(R.string.char_traits_showing_40) else "",
    )
    shown.take(40).forEach { TraitRow(it, false) { toggle(it.id) } }

    // ---------- 8. Revisão final ----------
    SectionTitle(
        stringResource(R.string.char_final_title),
        stringResource(R.string.char_final_help),
    )
    AppCard {
        Text(draft.name.ifBlank { stringResource(R.string.char_unnamed) }, style = MaterialTheme.typography.titleMedium)
        Muted(stringResource(R.string.char_final_race, CharacterCreationCatalog.find(raceId)?.name ?: notSelected))
        if (subraceId.isNotBlank()) {
            Muted(
                stringResource(R.string.char_final_subrace, CharacterCreationCatalog.find(subraceId)?.name ?: idNotFound),
            )
        }
        if (hybridId.isNotBlank()) {
            val hybrid = CharacterCreationCatalog.find(hybridId)
            Muted(stringResource(R.string.char_final_hybrid, hybrid?.name ?: idNotFound))
            if (hybrid != null) {
                Muted(
                    stringResource(
                        R.string.char_final_ancestries,
                        hybrid.hybridRaceIds.mapNotNull { CharacterCreationCatalog.find(it)?.name }.joinToString(" + "),
                    ),
                )
            }
        }
        Muted(stringResource(R.string.char_final_class, CharacterCreationCatalog.find(classId)?.name ?: notSelected))
        Muted(stringResource(R.string.char_final_profession, CharacterCreationCatalog.find(professionId)?.name ?: notSelected))
        Muted(stringResource(R.string.char_final_cursed, CharacterCreationCatalog.find(cursedClassBranchId)?.name ?: noneLabel))
        Muted(
            stringResource(
                R.string.char_final_hybrid_classes,
                (listOf(classId).filter { it.isNotBlank() } + additionalClassIds)
                    .mapNotNull { CharacterCreationCatalog.find(it)?.name }.ifEmpty { listOf(noneLabel) }.joinToString(" + "),
            ),
        )
        Muted(stringResource(R.string.char_final_curses_note))
        Muted(
            stringResource(
                R.string.char_final_traits,
                draft.traits().mapNotNull { pkg.trait(it)?.name }.ifEmpty { listOf(noneMasc) }.joinToString(", "),
            ),
        )
        Muted(
            stringResource(
                R.string.char_final_stats,
                draft.stats().entries.joinToString(", ") { (id, value) -> "${pkg.stat(id)?.name ?: id}: $value" },
            ),
        )
    }

    // ---------- 9. Criar ----------
    Button(modifier = Modifier.fillMaxWidth(), enabled = editingChoicesFichaId == null && choiceErrors.isEmpty() && !dbBusy, onClick = {
        when (val b = draft.build()) {
            is FichaBuild.Created -> {
                var newId = draftId
                io({
                    model.saveFicha(b.ficha)
                    model.saveCharacterChoices(b.ficha.id, choices)
                    newId = model.newFichaId()
                    null
                }) { failure ->
                    if (failure == null) {
                        draftId = newId
                        message = context.getString(R.string.char_created, b.ficha.name)
                    } else {
                        message = failure
                    }
                }
            }
            is FichaBuild.Invalid -> message = b.errors.joinToString("; ")
        }
    }) { Text(stringResource(R.string.char_create)) }
    message?.let { Muted(it) }

    SectionTitle(stringResource(R.string.char_import_title), stringResource(R.string.char_import_help))
    AppCard {
        OutlinedTextField(
            value = fichaImportText,
            onValueChange = { fichaImportText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.char_import_label)) },
            placeholder = { Text(stringResource(R.string.char_import_placeholder)) },
            minLines = 3,
            maxLines = 8,
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !dbBusy,
            onClick = {
                val raw = fichaImportText
                io({ model.importFicha(raw) }) {
                    libraryMessage = it
                    if (it?.startsWith("Ficha ") == true && it.orEmpty().contains("importada")) fichaImportText = ""
                }
            },
        ) { Text(stringResource(R.string.char_import_button)) }
    }

    if (model.fichas.isNotEmpty()) {
        SectionTitle(stringResource(R.string.char_library))
        model.fichas.forEach { f ->
            AppCard {
                Text(f.name, style = MaterialTheme.typography.titleSmall)
                Muted("${f.concept.ifBlank { stringResource(R.string.char_no_concept) }} · ${f.system.editionId}")
                val savedChoices = model.characterChoices(f.id)
                val savedNames =
                    (listOf(savedChoices.raceId, savedChoices.subraceId, savedChoices.hybridId, savedChoices.classId, savedChoices.professionId) + savedChoices.curseIds)
                        .filter { it.isNotBlank() }.mapNotNull { CharacterCreationCatalog.find(it)?.name }
                if (savedNames.isNotEmpty()) Muted(stringResource(R.string.char_origin, savedNames.joinToString(" · ")))
                TextButton(onClick = { expandedFichaId = if (expandedFichaId == f.id) null else f.id }) {
                    Text(stringResource(if (expandedFichaId == f.id) R.string.char_hide_sheet else R.string.char_show_sheet))
                }
                if (expandedFichaId == f.id) {
                    val fichaPkg = dev.rpgsolo.rules.CampaignStarter.packageFor(model.content.registry, f.system.editionId)
                    Muted(
                        stringResource(
                            R.string.char_lib_stats,
                            f.stats.entries.joinToString(", ") { (id, value) -> "${fichaPkg?.stat(id)?.name ?: id}: $value" },
                        ),
                    )
                    if (f.skills.isNotEmpty()) {
                        Muted(
                            stringResource(
                                R.string.char_lib_skills,
                                f.skills.entries.joinToString(", ") { (id, value) -> "${fichaPkg?.trait(id)?.name ?: id}: $value" },
                            ),
                        )
                    }
                    if (f.traits.isNotEmpty()) {
                        Muted(
                            stringResource(
                                R.string.char_lib_traits,
                                f.traits.joinToString(", ") { id -> fichaPkg?.trait(id)?.let { traitTag(it, context) } ?: id },
                            ),
                        )
                    }
                    if (f.resources.isNotEmpty()) {
                        Muted(
                            stringResource(
                                R.string.char_lib_resources,
                                f.resources.entries.joinToString(", ") { (id, resource) -> "$id: ${resource.current}/${resource.max}" },
                            ),
                        )
                    }
                    if (f.adaptations.isNotEmpty()) Muted(stringResource(R.string.char_lib_adaptations, f.adaptations.joinToString(", ")))
                    SectionTitle(stringResource(R.string.char_portrait_title))
                    val portrait =
                        remember(f.portraitRef, portraitVersion) {
                            model.portraitFile(f.portraitRef)?.let { file ->
                                android.graphics.BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                            }
                        }
                    if (portrait != null) {
                        Image(
                            bitmap = portrait,
                            contentDescription = stringResource(R.string.char_portrait_description, f.name),
                            modifier = Modifier.size(160.dp),
                        )
                    } else {
                        Muted(stringResource(R.string.char_portrait_none))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(enabled = !dbBusy, onClick = {
                            portraitTargetId = f.id
                            portraitPicker.launch("image/*")
                        }) { Text(stringResource(if (portrait == null) R.string.char_portrait_choose else R.string.char_portrait_change)) }
                        if (f.portraitRef.isNotBlank()) {
                            TextButton(enabled = !dbBusy, onClick = {
                                io({ model.clearPortrait(f.id) }) {
                                    portraitVersion++
                                    libraryMessage = it ?: context.getString(R.string.char_portrait_removed)
                                }
                            }) { Text(stringResource(R.string.char_portrait_remove)) }
                        }
                    }
                    SectionTitle(stringResource(R.string.char_lore_title))
                    if (f.approvedLore.isNotBlank()) Muted(f.approvedLore) else Muted(stringResource(R.string.char_lore_none))
                    val draftText = loreDrafts[f.id]
                    if (draftText == null) {
                        OutlinedTextField(
                            value = loreAppearance[f.id].orEmpty(),
                            onValueChange = { loreAppearance[f.id] = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.char_lore_appearance)) },
                            maxLines = 3,
                        )
                        OutlinedTextField(
                            value = loreIdea[f.id].orEmpty(),
                            onValueChange = { loreIdea[f.id] = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.char_lore_idea)) },
                            maxLines = 3,
                        )
                        Muted(stringResource(R.string.char_lore_privacy))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TextButton(enabled = !dbBusy, onClick = {
                                val appearance = loreAppearance[f.id].orEmpty()
                                val idea = loreIdea[f.id].orEmpty()
                                io({
                                    val r = model.generateLore(f.id, appearance, idea)
                                    r.text?.let { loreDrafts[f.id] = it }
                                    r.error
                                }) { libraryMessage = it }
                            }) {
                                Text(
                                    stringResource(
                                        if (f.approvedLore.isBlank()) R.string.char_lore_generate else R.string.char_lore_regenerate,
                                    ),
                                )
                            }
                            if (f.approvedLore.isNotBlank()) {
                                TextButton(enabled = !dbBusy, onClick = {
                                    io(
                                        { model.approveLore(f.id, "") },
                                    ) { libraryMessage = it ?: context.getString(R.string.char_lore_cleared) }
                                }) { Text(stringResource(R.string.char_lore_clear)) }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = draftText,
                            onValueChange = { loreDrafts[f.id] = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.char_lore_draft)) },
                            minLines = 4,
                            maxLines = 12,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TextButton(enabled = !dbBusy, onClick = {
                                io({ model.approveLore(f.id, draftText) }) {
                                    if (it == null) loreDrafts.remove(f.id)
                                    libraryMessage = it ?: context.getString(R.string.char_lore_saved)
                                }
                            }) { Text(stringResource(R.string.char_lore_approve)) }
                            TextButton(onClick = { loreDrafts.remove(f.id) }) { Text(stringResource(R.string.char_lore_discard)) }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = {
                        val saved = model.characterChoices(f.id)
                        raceId = saved.raceId
                        subraceId = saved.subraceId
                        hybridId = saved.hybridId
                        classId = saved.classId
                        professionId = saved.professionId
                        curseIds = saved.curseIds.toSet()
                        editingChoicesFichaId = f.id
                        message = null
                        version++
                    }) { Text(stringResource(R.string.char_edit_choices)) }
                    TextButton(enabled = !dbBusy, onClick = {
                        io({ model.duplicateFicha(f.id) }) { libraryMessage = it ?: context.getString(R.string.char_duplicated) }
                    }) { Text(stringResource(R.string.char_duplicate)) }
                    TextButton(enabled = !dbBusy, onClick = {
                        io({ model.archiveFicha(f.id, true) }) { libraryMessage = it ?: context.getString(R.string.char_archived) }
                    }) { Text(stringResource(R.string.char_archive)) }
                    TextButton(onClick = {
                        val raw = model.exportFicha(f.id)
                        if (raw != null) {
                            clipboard.setText(AnnotatedString(raw))
                            libraryMessage = context.getString(R.string.char_json_copied)
                        } else {
                            libraryMessage = context.getString(R.string.char_export_failed)
                        }
                    }) { Text(stringResource(R.string.char_copy_json)) }
                }
            }
        }
    }
    if (model.archivedFichas.isNotEmpty()) {
        SectionTitle(stringResource(R.string.char_archived_title))
        model.archivedFichas.forEach { f ->
            AppCard {
                Text(f.name, style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(enabled = !dbBusy, onClick = {
                        io({ model.archiveFicha(f.id, false) }) { libraryMessage = it ?: context.getString(R.string.char_restored) }
                    }) { Text(stringResource(R.string.char_restore)) }
                    TextButton(enabled = !dbBusy, onClick = {
                        io({ model.deleteFicha(f.id) }) { libraryMessage = it ?: context.getString(R.string.char_deleted) }
                    }) { Text(stringResource(R.string.char_delete)) }
                }
            }
        }
    }
    libraryMessage?.let { Muted(it) }
}

/** M-01.8: nome do traço com a marcação do que o motor aplica ("efeito: ...") ou "narrativo" (só vantagem/desvantagem). */
private fun traitTag(
    t: TraitDef,
    context: android.content.Context,
): String =
    when {
        t.bonuses.isNotEmpty() ->
            t.name +
                context.getString(
                    R.string.char_tag_effect,
                    t.bonuses.joinToString("; ") { "${it.scope} ${if (it.bonus > 0) "+" else ""}${it.bonus}" },
                )
        t.type == "vantagem" || t.type == "desvantagem" -> t.name + context.getString(R.string.char_tag_narrative)
        else -> t.name
    }

@Composable
private fun TraitRow(
    t: TraitDef,
    chosen: Boolean,
    onToggle: () -> Unit,
) {
    val (label, color) =
        when (t.type) {
            "vantagem" -> stringResource(R.string.char_type_advantage) to MaterialTheme.colorScheme.tertiary
            "poder" -> stringResource(R.string.char_type_power) to MaterialTheme.colorScheme.tertiary
            "desvantagem" -> stringResource(R.string.char_type_disadvantage) to MaterialTheme.colorScheme.error
            else -> stringResource(R.string.char_type_skill) to MaterialTheme.colorScheme.secondary
        }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(t.name, style = MaterialTheme.typography.titleSmall)
                    Pill(label, color)
                    if (t.cost != 0) Pill(stringResource(R.string.char_cost, t.cost), MaterialTheme.colorScheme.outline)
                    // M-01: distingue o que o motor aplica do que é só texto para o mestre e o jogador.
                    if (t.bonuses.isNotEmpty()) {
                        Pill(
                            stringResource(
                                R.string.char_pill_effect,
                                t.bonuses.joinToString("; ") { "${it.scope} ${if (it.bonus > 0) "+" else ""}${it.bonus}" },
                            ),
                            MaterialTheme.colorScheme.primary,
                        )
                    } else if (t.type == "vantagem" || t.type == "desvantagem") {
                        Pill(stringResource(R.string.char_narrative), MaterialTheme.colorScheme.outline)
                    }
                }
                Muted(t.description)
            }
            OutlinedButton(onClick = onToggle) { Text(stringResource(if (chosen) R.string.char_take_off else R.string.char_take)) }
        }
    }
}

@Composable
private fun CatalogPicker(
    title: String,
    entries: List<CharacterCatalogEntry>,
    selectedId: String,
    onSelected: (String) -> Unit,
    allowNone: Boolean = false,
) {
    var query by remember(title) { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.char_search_title, title)) },
            singleLine = true,
        )
        if (allowNone && selectedId.isNotBlank()) {
            TextButton(onClick = { onSelected("") }) { Text(stringResource(R.string.char_clear_selection)) }
        }
        val q = query.trim().lowercase()
        val visible = entries.filter { q.isEmpty() || it.name.lowercase().contains(q) || it.summary.lowercase().contains(q) }.take(30)
        if (visible.isEmpty()) Muted(stringResource(R.string.char_no_options))
        visible.forEach { entry ->
            FilterChip(
                selected = selectedId == entry.id,
                onClick = { onSelected(if (selectedId == entry.id && allowNone) "" else entry.id) },
                label = { Text(entry.name) },
            )
            if (selectedId == entry.id) {
                if (entry.summary.isNotBlank()) Muted(entry.summary)
                if (entry.lore.isNotBlank()) Muted(stringResource(R.string.char_lore, entry.lore))
                entry.benefits.forEach { Muted(stringResource(R.string.char_benefit_not_auto, it)) }
                entry.drawbacks.forEach { Muted(stringResource(R.string.char_drawback_not_auto, it)) }
                entry.mechanicalEffects.forEach {
                    Muted(
                        stringResource(
                            R.string.char_effect_applied,
                            entry.name,
                            it.scope,
                            (if (it.bonus >= 0) "+" else "") + it.bonus,
                        ),
                    )
                }
            }
        }
        if (entries.size > visible.size) Muted(stringResource(R.string.char_showing_30))
    }
}

@Composable
private fun CatalogMultiPicker(
    title: String,
    entries: List<CharacterCatalogEntry>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
) {
    var query by remember(title) { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.char_curse_search)) },
            singleLine = true,
        )
        Text(stringResource(R.string.char_selected_count, selectedIds.size), style = MaterialTheme.typography.labelMedium)
        val q = query.trim().lowercase()
        val visible = entries.filter { q.isEmpty() || it.name.lowercase().contains(q) || it.summary.lowercase().contains(q) }.take(30)
        if (visible.isEmpty()) Muted(stringResource(R.string.char_no_curses))
        visible.forEach { entry ->
            FilterChip(
                selected = entry.id in selectedIds,
                onClick = { onToggle(entry.id) },
                label = { Text(entry.name) },
            )
            if (entry.id in selectedIds) {
                if (entry.summary.isNotBlank()) Muted(entry.summary)
                if (entry.lore.isNotBlank() && entry.lore != entry.summary) Muted(stringResource(R.string.char_lore, entry.lore))
                entry.benefits.forEach { Muted(stringResource(R.string.char_benefit_not_auto, it)) }
                entry.drawbacks.forEach { Muted(stringResource(R.string.char_drawback_not_auto, it)) }
                entry.mechanicalEffects.forEach {
                    Muted(
                        stringResource(
                            R.string.char_effect_applied,
                            entry.name,
                            it.scope,
                            (if (it.bonus >= 0) "+" else "") + it.bonus,
                        ),
                    )
                }
            }
        }
        if (entries.size > visible.size) Muted(stringResource(R.string.char_catalog_size, entries.size))
    }
}
