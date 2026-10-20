package dev.rpgsolo.ui

import android.app.Application
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dev.rpgsolo.ai.AiProvider
import dev.rpgsolo.ai.AiError
import dev.rpgsolo.ai.AiMessage
import dev.rpgsolo.ai.AiRequest
import dev.rpgsolo.ai.AiRoles
import dev.rpgsolo.ai.CampaignInterpreter
import dev.rpgsolo.ai.NewCampaignFlow
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.StartResult
import dev.rpgsolo.ai.TurnRunner
import dev.rpgsolo.ai.CheckOption
import dev.rpgsolo.ai.IntentClassifier
import dev.rpgsolo.ai.Author
import dev.rpgsolo.ai.ChapterSummarizer
import dev.rpgsolo.ai.GameLoop
import dev.rpgsolo.ai.PlayController
import dev.rpgsolo.ai.CheckMenu
import dev.rpgsolo.ai.PlayerSheet
import dev.rpgsolo.ai.ProviderConfig
import dev.rpgsolo.ai.ProviderFactory
import dev.rpgsolo.ai.ProviderKind
import dev.rpgsolo.ai.Role
import dev.rpgsolo.ai.UsageTracker
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.ChapterBook
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.Quest
import dev.rpgsolo.engine.QuestLog
import dev.rpgsolo.engine.journal
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.data.RpgDatabase
import dev.rpgsolo.data.RoomEventStore
import dev.rpgsolo.data.CampanhaMetaRow
import dev.rpgsolo.data.FichaRow
import dev.rpgsolo.rules.CampaignStarter

data class ConnectionTestResult(val success: Boolean, val message: String)
data class CombatTarget(val id: String, val name: String, val rulesValidated: Boolean = true, val condition: String = "")
data class PlaceView(val id: String, val name: String, val description: String, val current: Boolean, val exits: List<Pair<String, String>>)
data class RelationshipView(val npcName: String, val attitude: String)
data class EquipmentView(val id: String, val name: String, val quantity: Int, val slot: String, val equipped: Boolean)
data class InventoryView(
    val id: String, val name: String, val quantity: Int, val kind: String,
    val slot: String?, val equipped: Boolean, val description: String
)
data class AdvancementChoice(val id: String, val name: String, val current: Int, val max: Int, val skill: Boolean)
data class AdvancementState(val pointsAvailable: Int, val choices: List<AdvancementChoice>)
data class SetupStatus(val providerReady: Boolean, val heroReady: Boolean, val campaignReady: Boolean) {
    val complete: Boolean get() = providerReady && heroReady && campaignReady
}


/**
 * Estado do app. O ViewModel sobrevive à recriação da Activity; campanha e log são persistidos no Room.
 * A configuração do provedor é restaurada e a chave fica cifrada pelo Android Keystore.
 */
class AppModel(application: Application) : AndroidViewModel(application) {
    val usage = UsageTracker()
    val content: Content = BundledContent.load(application)
    private val secureSettings = SecureSettings(application)
    private var storedSettings: StoredProviderSettings? = secureSettings.load()
    private val narrationPrefs = application.getSharedPreferences("rpg_narration_prefs", 0)

    /** Metadados editoriais da nova criação, separados da ficha mecânica e persistidos por ID. */
    private val characterChoicesPrefs = application.getSharedPreferences("rpg_character_choices", 0)

    /**
     * Salva a escolha editorial dentro da ficha canônica e mantém o espelho legado
     * por compatibilidade com backups/metadados de versões antigas.
     */
    fun saveCharacterChoices(fichaId: String, choices: CharacterChoices) {
        if (fichaId.isBlank()) return
        val json = choices.toJson()
        characterChoicesPrefs.edit().putString(fichaId, json).apply()
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == fichaId } ?: return
        if (ficha.editorialChoicesJson != json) {
            val updated = ficha.copy(editorialChoicesJson = json)
            store.saveFicha(FichaRow(updated.id, updated.name, dev.rpgsolo.engine.Codec.encode(updated)))
            val activeIndex = fichas.indexOfFirst { it.id == fichaId }
            if (activeIndex >= 0) fichas[activeIndex] = updated
            val archivedIndex = archivedFichas.indexOfFirst { it.id == fichaId }
            if (archivedIndex >= 0) archivedFichas[archivedIndex] = updated
            if (activeFichaId == fichaId) activeFicha = updated
        }
        campaignVersion++
    }

    /** O campo canônico tem precedência; SharedPreferences só é fallback para fichas antigas. */
    fun characterChoices(fichaId: String): CharacterChoices {
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == fichaId }
        val canonical = ficha?.editorialChoicesJson.orEmpty()
        if (canonical.isNotBlank()) return CharacterChoices.fromJson(canonical)
        return CharacterChoices.fromJson(characterChoicesPrefs.getString(fichaId, null))
    }

    /**
     * Efeitos explícitos do catálogo para a ficha ativa. Só entram quando as escolhas e
     * os escopos são válidos para o pacote ativo; não altera Ficha.traits nem persiste bônus derivados.
     */
    private fun selectedCatalogBonuses(pkg: RulePackage): List<dev.rpgsolo.rules.ExternalBonus> {
        val fichaId = activeFichaId ?: activeFicha?.id ?: return emptyList()
        val choices = characterChoices(fichaId)
        if (CharacterCreationCatalog.validateChoices(choices).isNotEmpty()) return emptyList()
        if (CharacterCreationCatalog.validateEffectScopes(pkg, choices).isNotEmpty()) return emptyList()
        return CharacterCreationCatalog.selectedEffects(choices).map {
            dev.rpgsolo.rules.ExternalBonus(scope = it.scope, bonus = it.bonus, source = it.source)
        }
    }

    private fun selectedCatalogBonus(pkg: RulePackage, scope: String): Int =
        selectedCatalogBonuses(pkg).filter { it.scope == scope }.sumOf { it.bonus }

    /** Explica os efeitos por escopo, sem somar bônus que modificam cálculos diferentes. */
    private fun selectedCatalogBonusSummary(pkg: RulePackage, scopes: Collection<String>): String {
        val applied = selectedCatalogBonuses(pkg).filter { it.scope in scopes && it.bonus != 0 }
        if (applied.isEmpty()) return ""
        val details = applied.groupBy { it.scope }.entries.joinToString("; ") { (scope, effects) ->
            val total = effects.sumOf { it.bonus }
            val sources = effects.map { it.source }.distinct().joinToString(", ")
            scope + " " + (if (total > 0) "+" else "") + total + " [" + sources + "]"
        }
        return " Efeitos de personagem: $details."
    }
    /** Lore canônica selecionada para contexto narrativo; nunca é convertida em regra mecânica. */
    fun characterChoicesForAi(fichaId: String): String {
        val choices = characterChoices(fichaId)
        val selectedIds = listOf(choices.raceId, choices.subraceId, choices.hybridId, choices.classId, choices.professionId) + choices.curseIds
        val selected = selectedIds.filter { it.isNotBlank() }.distinct().mapNotNull(CharacterCreationCatalog::find)
        val ancestryIds = selected.filter { it.id == choices.hybridId }.flatMap { it.hybridRaceIds }
        val ancestry = ancestryIds.mapNotNull(CharacterCreationCatalog::find)
        val entries = (selected + ancestry).distinctBy { it.id }
        return buildString {
            append("INSTRUÇÕES DE INTEGRIDADE: este bloco contém escolhas e descrições editoriais, não regras mecânicas. ")
            append("Não conceda bônus, penalidades, poderes, resistências ou alterações de atributos a partir dele. ")
            append("Use apenas como contexto narrativo; os efeitos mecânicos válidos vêm da ficha e do pacote de regras.\n")
            entries.forEach { entry ->
                append("- ").append(entry.name)
                if (entry.id == choices.hybridId && ancestry.isNotEmpty()) {
                    append(" (híbrido de ").append(ancestry.filter { it.id in entry.hybridRaceIds }.joinToString(" + ") { it.name }).append(")")
                }
                if (entry.lore.isNotBlank()) append("\n  Lore: ").append(entry.lore)
                if (entry.summary.isNotBlank()) append("\n  Resumo: ").append(entry.summary)
                if (entry.benefits.isNotEmpty()) append("\n  Benefícios descritos (narrativos, não aplicar mecanicamente): ").append(entry.benefits.joinToString("; "))
                if (entry.drawbacks.isNotEmpty()) append("\n  Custos descritos (narrativos, não aplicar mecanicamente): ").append(entry.drawbacks.joinToString("; "))
                if (entry.benefits.isEmpty() && entry.benefit.isNotBlank()) append("\n  Benefício descrito no catálogo (não aplicar mecanicamente): ").append(entry.benefit)
                if (entry.drawbacks.isEmpty() && entry.drawback.isNotBlank()) append("\n  Custo descrito no catálogo (não aplicar mecanicamente): ").append(entry.drawback)
            }
        }
    }

    var narrationLength by mutableStateOf(narrationPrefs.getString("length", "equilibrada") ?: "equilibrada")
        private set
    var narrationStyle by mutableStateOf(narrationPrefs.getString("style", "cinematográfica") ?: "cinematográfica")
        private set
    var contentLines by mutableStateOf(narrationPrefs.getString("content_lines", "").orEmpty())
        private set
    var contentVeils by mutableStateOf(narrationPrefs.getString("content_veils", "").orEmpty())
        private set
    var chapterEventLimit by mutableStateOf(narrationPrefs.getInt("chapter_events", 200))
        private set
    var fontScale by mutableStateOf(narrationPrefs.getFloat("font_scale", 1.0f).coerceIn(0.85f, 1.4f))
        private set

    fun setFontScale(value: Float) {
        if (!value.isFinite()) return
        fontScale = value.coerceIn(0.85f, 1.4f)
        narrationPrefs.edit().putFloat("font_scale", fontScale).apply()
    }

    fun setChapterEventLimit(value: Int) {
        if (value !in 50..2000) return
        chapterEventLimit = value
        narrationPrefs.edit().putInt("chapter_events", value).apply()
        chapters.configureMaxEvents(value)
        campaignVersion++
    }

    fun setContentBoundaries(lines: String, veils: String) {
        contentLines = lines.take(4000)
        contentVeils = veils.take(4000)
        narrationPrefs.edit()
            .putString("content_lines", contentLines)
            .putString("content_veils", contentVeils)
            .apply()
        campaignVersion++
    }

    fun setNarrationPreferences(length: String, style: String) {
        val validLengths = setOf("curta", "equilibrada", "detalhada")
        val validStyles = setOf("cinematográfica", "literária", "direta")
        if (length !in validLengths || style !in validStyles) return
        narrationLength = length
        narrationStyle = style
        narrationPrefs.edit().putString("length", length).putString("style", style).apply()
        campaignVersion++
    }

    /** Biblioteca de fichas reutilizáveis, persistida separadamente das campanhas. */
    val fichas = mutableStateListOf<Ficha>()
    val archivedFichas = mutableStateListOf<Ficha>()
    val campaigns = mutableStateListOf<CampaignRecord>()
    var chapters = ChapterBook(chapterEventLimit)
        private set
    var quests = QuestLog()
        private set

    /** Sobe a cada mudança de capítulo/missão para a tela de campanhas redesenhar. */
    var campaignVersion by mutableIntStateOf(0)
        private set
    var packageVersion by mutableIntStateOf(0)
        private set
    var chapterNotice by mutableStateOf<String?>(null)
        private set
    private var roles: AiRoles? = null
    private val database = RpgDatabase.build(application)
    private val store = RoomEventStore(database)
    private val activeCampaignPrefs = application.getSharedPreferences("rpgsolo_session", Application.MODE_PRIVATE)
    private var playerId = "hero"
    private var activePackage: RulePackage? = content.registry.get("sistema-proprio")
    /** NPCs adicionados manualmente para o próximo encontro; NPCs de outras cenas não entram automaticamente. */
    private val pendingCombatNpcIds = linkedSetOf<String>()
    private var activeFicha: Ficha? = null
    private var activeFichaId: String? = null
    private var activeSystemId: String? = null
    private var activeEditionId: String? = null
    private var session: CampaignSession = openOrCreateCampaign()

    /** null enquanto não houver provedor configurado. */
    var controller: PlayController? by mutableStateOf(null)
        private set

    var configError: String? by mutableStateOf(null)
        private set

    init {
        restoreImportedPackages()
        restoreFichas()
        refreshCampaigns()
        restoreMetadata()
        restorePendingCombatNpcIds()
        if (chapters.chapters.isEmpty()) openChapter("Capítulo 1")
        storedSettings?.let { saved ->
            runCatching {
                applySettings(
                    ProviderKind.valueOf(saved.kind), saved.endpoint, saved.model, saved.apiKey,
                    saved.cheapModel, saved.premiumModel, saved.cacheSystemPrompt,
                    saved.inputPricePerMillion, saved.outputPricePerMillion,
                    saved.cacheReadPricePerMillion, saved.cacheCreationPricePerMillion
                )
            }
        }
    }

    /** Reinstala no registro em memória os pacotes ZIP guardados no armazenamento privado do app. */
    private fun restoreImportedPackages() {
        val dir = File(getApplication<Application>().filesDir, "packages-import")
        val pending = dir.listFiles { file -> file.isFile && file.extension.equals("zip", ignoreCase = true) }
            .orEmpty().sortedBy { it.name }.toMutableList()
        // A package may extend another user package. Retry unresolved archives after each successful install.
        while (pending.isNotEmpty()) {
            var installedAny = false
            val iterator = pending.iterator()
            while (iterator.hasNext()) {
                val archive = iterator.next()
                val result = runCatching {
                    archive.inputStream().use { content.registry.installZip(it, replace = false) }
                }.getOrNull()
                if (result is dev.rpgsolo.rules.InstallResult.Success) {
                    iterator.remove()
                    installedAny = true
                }
            }
            if (!installedAny) break // Invalid archives and dependency cycles remain inactive, never partially installed.
        }
    }

    private fun openOrCreateCampaign(): CampaignSession {
        val savedId = activeCampaignPrefs.getString("active_campaign", null)
        val saved = savedId?.let { store.campaign(it) } ?: store.campaigns().firstOrNull()
        if (saved != null) {
            activeCampaignPrefs.edit().putString("active_campaign", saved.id).apply()
            return CampaignSession.open(store, saved.id)
        }
        val record = CampaignRecord("demo", "Aventura de teste", 42L, EngineConfig(deathResource = "pv"))
        return CampaignSession.create(store, record).also {
            it.executeAll(listOf(
                CreateEntity(Entity("hero", "Aventureiro", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                CreateEntity(Entity("guarda", "Guarda do portão", EntityKind.NPC, resources = mapOf("pv" to Resource(8, 8))))
            ))
            activeCampaignPrefs.edit().putString("active_campaign", record.id).apply()
        }
    }

    private fun refreshCampaigns() {
        campaigns.clear()
        campaigns.addAll(store.campaigns())
    }

    private fun restoreFichas() {
        fichas.clear()
        archivedFichas.clear()
        fichas.addAll(store.fichas().mapNotNull { row ->
            runCatching { dev.rpgsolo.engine.Codec.decodeFicha(row.json) }.getOrNull()
        })
        archivedFichas.addAll(store.fichasArquivadas().mapNotNull { row ->
            runCatching { dev.rpgsolo.engine.Codec.decodeFicha(row.json) }.getOrNull()
        })
    }

    /** Salva uma ficha na biblioteca e atualiza a cópia observável em memória. */
    fun saveFicha(ficha: Ficha) {
        // Migração sob demanda: qualquer gravação de uma ficha antiga incorpora as escolhas
        // legadas antes de persistir, sem apagar o registro de origem.
        val legacyChoices = characterChoicesPrefs.getString(ficha.id, null)
        val canonical = ficha.editorialChoicesJson.ifBlank { legacyChoices.orEmpty() }
        val persisted = if (canonical == ficha.editorialChoicesJson) ficha else ficha.copy(editorialChoicesJson = canonical)
        store.saveFicha(FichaRow(persisted.id, persisted.name, dev.rpgsolo.engine.Codec.encode(persisted)))
        val index = fichas.indexOfFirst { it.id == persisted.id }
        if (index >= 0) fichas[index] = persisted else fichas.add(persisted)
        val archivedIndex = archivedFichas.indexOfFirst { it.id == persisted.id }
        if (archivedIndex >= 0) archivedFichas[archivedIndex] = persisted
        if (activeFichaId == persisted.id) activeFicha = persisted
    }

    fun duplicateFicha(id: String): String? = try {
        val original = requireNotNull(fichas.firstOrNull { it.id == id }) { "ficha não encontrada na biblioteca" }
        val copy = original.copy(
            id = "f-${System.nanoTime()}",
            name = "${original.name} (cópia)",
            editorialChoicesJson = characterChoices(id).toJson()
        )
        saveFicha(copy)
        characterChoicesPrefs.edit().putString(copy.id, copy.editorialChoicesJson).apply()
        campaignVersion++
        null
    } catch (e: IllegalArgumentException) { e.message }

    fun archiveFicha(id: String, archived: Boolean): String? = try {
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == id }
            ?: return "ficha não encontrada na biblioteca"
        require(store.definirFichaArquivada(id, archived)) { "não foi possível atualizar a ficha" }
        if (archived) {
            fichas.removeAll { it.id == id }
            if (archivedFichas.none { it.id == id }) archivedFichas.add(ficha)
        } else {
            archivedFichas.removeAll { it.id == id }
            if (fichas.none { it.id == id }) fichas.add(ficha)
        }
        campaignVersion++
        null
    } catch (e: IllegalArgumentException) { e.message }

    fun deleteFicha(id: String): String? = try {
        require(store.apagarFicha(id)) { "ficha não encontrada na biblioteca" }
        characterChoicesPrefs.edit().remove(id).apply()
        fichas.removeAll { it.id == id }
        archivedFichas.removeAll { it.id == id }
        campaignVersion++
        null
    } catch (e: IllegalArgumentException) { e.message }

    /** Exportação individual versionada: ficha mecânica e escolhas editoriais viajam juntas. */
    fun exportFicha(id: String): String? =
        (fichas + archivedFichas).firstOrNull { it.id == id }?.let { ficha ->
            org.json.JSONObject()
                .put("format", "rpgsolo-character-export")
                .put("schemaVersion", 1)
                .put("ficha", org.json.JSONObject(dev.rpgsolo.engine.Codec.encode(ficha)))
                .put("characterChoices", org.json.JSONObject(characterChoices(ficha.id).toJson()))
                .toString(2)
        }

    /** Importa o formato versionado atual e também JSONs antigos que continham somente a ficha. */
    fun importFicha(raw: String): String? = try {
        require(raw.isNotBlank()) { "cole o JSON da ficha" }
        val envelope = org.json.JSONObject(raw)
        val format = envelope.optString("format")
        val fichaJson: String
        val choices: CharacterChoices
        if (format == "rpgsolo-character-export") {
            require(envelope.optInt("schemaVersion", -1) == 1) { "versão de exportação de ficha não suportada" }
            fichaJson = requireNotNull(envelope.optJSONObject("ficha")) { "exportação sem objeto ficha" }.toString()
            choices = CharacterChoices.fromJson(envelope.optJSONObject("characterChoices")?.toString())
        } else {
            require(format.isBlank()) { "formato de exportação desconhecido: $format" }
            fichaJson = raw
            choices = CharacterChoices()
        }
        val decoded = dev.rpgsolo.engine.Codec.decodeFicha(fichaJson)
        val exists = (fichas + archivedFichas).any { it.id == decoded.id }
        val importedId = if (exists) "f-${System.nanoTime()}" else decoded.id
        val importedChoices = when {
            choices != CharacterChoices() -> choices
            decoded.editorialChoicesJson.isNotBlank() -> CharacterChoices.fromJson(decoded.editorialChoicesJson)
            else -> CharacterChoices()
        }
        val imported = decoded.copy(
            id = importedId,
            name = if (exists) "${decoded.name} (importada)" else decoded.name,
            editorialChoicesJson = if (importedChoices != CharacterChoices()) importedChoices.toJson() else decoded.editorialChoicesJson
        )
        saveFicha(imported)
        if (importedChoices != CharacterChoices()) saveCharacterChoices(imported.id, importedChoices)
        val choiceWarnings = if (choices != CharacterChoices()) CharacterCreationCatalog.validateChoices(choices) else emptyList()
        buildString {
            append("Ficha ").append(imported.name).append(" importada")
            if (choices != CharacterChoices()) append(" com escolhas de raça/linhagem, classe, profissão e maldições")
            append(".")
            if (choiceWarnings.isNotEmpty()) {
                append("\nAtenção: as escolhas foram preservadas, mas precisam ser revisadas porque algumas referências não são válidas neste catálogo:")
                choiceWarnings.forEach { append("\n- ").append(it) }
                append("\nAbra “Editar escolhas” na biblioteca para corrigir.")
            }
        }
    } catch (e: Exception) {
        e.message ?: "JSON de ficha inválido"
    }

    fun activeCampaignId(): String = session.campaignId

    fun activePackageId(): String? = activePackage?.manifest?.id

    /** Importa um ZIP de pacote de regras, valida-o antes de mantê-lo e persiste-o no armazenamento privado. */
    fun importPackage(uri: Uri): String {
        val dir = File(getApplication<Application>().filesDir, "packages-import")
        if (!dir.exists() && !dir.mkdirs()) return "não foi possível criar a pasta de pacotes"
        val archive = File(dir, "package-${System.nanoTime()}.zip")
        return try {
            val input = getApplication<Application>().contentResolver.openInputStream(uri)
                ?: run { archive.delete(); return "não foi possível abrir o arquivo selecionado" }
            input.use { source ->
                FileOutputStream(archive).use { target ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    while (true) {
                        val count = source.read(buffer)
                        if (count < 0) break
                        total += count
                        if (total > 20L * 1024 * 1024) {
                            archive.delete()
                            return "arquivo ZIP excede o limite de 20 MiB"
                        }
                        target.write(buffer, 0, count)
                    }
                }
            }
            when (val result = archive.inputStream().use { stream -> content.registry.installZip(stream) }) {
                is dev.rpgsolo.rules.InstallResult.Success -> {
                    packageVersion++
                    "Pacote ${result.pkg.manifest.id} instalado (edição ${result.pkg.manifest.edition}, v${result.pkg.manifest.version})."
                }
                is dev.rpgsolo.rules.InstallResult.Failure -> {
                    archive.delete()
                    "Pacote recusado:\n" + result.errors.joinToString("\n")
                }
            }
        } catch (e: Exception) {
            archive.delete()
            "falha ao importar pacote: ${e.message ?: "erro de leitura"}"
        }
    }

    /** Reabre uma campanha salva e restaura o pacote/ficha associados sem reutilizar o contexto da campanha anterior. */
    fun activateCampaign(campaignId: String): String? = try {
        if (campaignId != session.campaignId) {
            switchCampaign(campaignId)
            refreshCampaigns()
            campaignVersion++
        }
        null
    } catch (e: IllegalArgumentException) {
        e.message
    } catch (e: IllegalStateException) {
        e.message
    }

    /** Cria um NPC inicial usando apenas atributos e recursos definidos pelo pacote ativo. */
    fun addNpc(name: String): String? = try {
        require(name.isNotBlank()) { "dê um nome ao adversário" }
        val pkg = requireNotNull(activePackage) { "instale/ative um pacote de regras antes de criar adversários" }
        val stats = pkg.data.stats.associate { it.id to it.default }
        val entity = Entity(
            id = "npc-${System.nanoTime()}",
            name = name.trim(),
            kind = EntityKind.NPC,
            stats = stats,
            resources = pkg.resourcesFor(stats)
        )
        when (val result = session.execute(CreateEntity(entity))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                pendingCombatNpcIds += entity.id
                savePendingCombatNpcIds()
                campaignVersion++
                "${entity.name} foi adicionado ao próximo encontro."
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    /** Inicia a iniciativa do jogador e dos NPCs preparados para este encontro; os números vêm do motor. */
    fun startCombat(): String? = try {
        val pkg = requireNotNull(activePackage) { "pacote de regras não está ativo" }
        val initiativeStat = pkg.data.creation["combate_atributo_iniciativa"]
            ?: return "o pacote não define o atributo de iniciativa"
        val initiativeDiceCount = pkg.data.creation["combate_dados_iniciativa"]?.toIntOrNull()
            ?: return "o pacote não define combate_dados_iniciativa"
        val initiativeDiceSides = pkg.data.creation["combate_lados_iniciativa"]?.toIntOrNull()
            ?: return "o pacote não define combate_lados_iniciativa"
        val initiativeOrder = pkg.data.creation["combate_ordem_iniciativa"]
            ?: return "o pacote não define combate_ordem_iniciativa"
        val participants = (listOf(playerId) + pendingCombatNpcIds).distinct().filter { id ->
            session.state.entities[id]?.alive == true
        }
        require(participants.size >= 2) { "adicione pelo menos um adversário para este encontro" }
        when (val result = session.rollInitiative(
            participants,
            diceCount = initiativeDiceCount,
            diceSides = initiativeDiceSides,
            statKey = initiativeStat,
            highestFirst = initiativeOrder == "MAIOR"
        )) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                pendingCombatNpcIds.clear()
                savePendingCombatNpcIds()
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }

    /** Permite passar o turno apenas ao jogador; turnos de NPC são resolvidos pela ação mecânica correspondente. */
    fun nextCombatTurn(): String? {
        if (!isPlayerCombatTurn()) return "só é possível passar o turno durante o turno do jogador"
        val combatantsBefore = session.state.combat?.order.orEmpty()
        return when (val result = session.execute(dev.rpgsolo.engine.NextTurn)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                awardCombatVictoryXp(combatantsBefore)
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** Resolve o ataque do inimigo atual e só então avança o turno; nenhum número vem da IA. */
    fun resolveNpcTurn(): String? {
        return try {
        val combat = requireNotNull(session.state.combat) { "não há combate ativo" }
        val combatantsBefore = combat.order
        require(combat.current != playerId) { "é o turno do jogador" }
        val pkg = requireNotNull(activePackage) { "pacote de regras não está ativo" }
        val resource = pkg.manifest.deathResource
            ?: return "o pacote não define o recurso de vida usado no combate"
        val attackStat = pkg.data.creation["combate_atributo_ataque"]
            ?: return "o pacote não define o atributo de ataque"
        val defenseStat = pkg.data.creation["combate_atributo_defesa"]
            ?: return "o pacote não define o atributo de defesa"
        val attackDiceCount = pkg.data.creation["combate_dados_ataque"]?.toIntOrNull()
            ?: return "o pacote não define combate_dados_ataque"
        val attackDiceSides = pkg.data.creation["combate_lados_ataque"]?.toIntOrNull()
            ?: return "o pacote não define combate_lados_ataque"
        val attackComparison = pkg.data.creation["combate_comparacao_ataque"]?.let {
            runCatching { dev.rpgsolo.engine.Comparison.valueOf(it) }.getOrNull()
        } ?: return "o pacote não define uma comparação de ataque válida"
        val baseDamage = pkg.data.creation["combate_dano_base"]?.toIntOrNull()
            ?: return "o pacote não define combate_dano_base"
        val baseDefense = pkg.data.creation["combate_defesa_base"]?.toIntOrNull()
            ?: return "o pacote não define combate_defesa_base"
        val damageDef = pkg.data.creation["combate_dano"]?.let { id ->
            pkg.data.damage.firstOrNull { it.id == id }
                ?: return "o pacote referencia um perfil de dano inexistente: $id"
        }
        val attacker = combat.current
        val attackerEquipment = session.state.equipped[attacker].orEmpty().values
        val targetEquipment = session.state.equipped[playerId].orEmpty().values
        val attackConditionShift = conditionDiceShift(attacker, attackStat)
        val targetConditionShift = conditionDiceShift(playerId, defenseStat)
        // Ganho/Perda altera o bônus/limiar conforme a comparação, nunca a quantidade de dados.
        val effectiveAttackDice = attackDiceCount
        val npcAttackMark = session.mark()
        val result = session.attack(
            dev.rpgsolo.engine.AttackSpec(
                attackerId = attacker,
                targetId = playerId,
                attackStat = attackStat,
                defenseStat = defenseStat,
                attackDiceCount = effectiveAttackDice,
                attackDiceSides = attackDiceSides,
                attackComparison = attackComparison,
                damageResource = resource,
                damage = baseDamage,
                defenseDefault = baseDefense,
                attackBonus = pkg.equipmentBonus(attackerEquipment, "ataque") +
                    pkg.traitBonus(combatTraitIds(attacker), "ataque") +
                    attackConditionShift +
                    ((session.state.campaignStyle?.difficultyParams?.get("enemy_tactics") ?: 5.0)
                        .roundToInt().coerceIn(0, 10) - 5),
                damageBonus = pkg.equipmentBonus(attackerEquipment, "dano") +
                    pkg.traitBonus(combatTraitIds(attacker), "dano"),
                defenseBonus = pkg.equipmentBonus(targetEquipment, "defesa") +
                    pkg.traitBonus(combatTraitIds(playerId), "defesa") +
                    selectedCatalogBonus(pkg, "defesa") +
                    approvedRuleBonus("defesa") + approvedRuleBonus(defenseStat) + targetConditionShift,
                damageReduction = pkg.traitBonus(combatTraitIds(playerId), "resistencia_dano") +
                    selectedCatalogBonus(pkg, "resistencia_dano") +
                    approvedRuleBonus("resistencia_dano"),
                damageDiceCount = damageDef?.diceCount ?: 0,
                damageDiceSides = damageDef?.diceSides ?: 0,
                damageStat = damageDef?.bonusStat
            )
        )
        val text = when (result) {
            is dev.rpgsolo.engine.AttackResult.Resolved -> {
                val outcome = if (result.hit) {
                    "${session.state.entities.getValue(attacker).name} acertou você."
                } else {
                    "${session.state.entities.getValue(attacker).name} errou o ataque."
                }
                outcome +
                    selectedCatalogBonusSummary(pkg, if (result.hit) listOf("defesa", "resistencia_dano") else listOf("defesa")) +
                    approvedRuleBonusSummary(if (result.hit) listOf("defesa", defenseStat, "resistencia_dano") else listOf("defesa", defenseStat))
            }
            is dev.rpgsolo.engine.AttackResult.Rejected -> return result.reason
        }
        if (session.state.combat != null) {
            when (val advance = session.execute(dev.rpgsolo.engine.NextTurn)) {
                is dev.rpgsolo.engine.ExecResult.Rejected -> {
                    session.rollbackTo(npcAttackMark)
                    return advance.reason
                }
                is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            }
        }
        val victoryXp = awardCombatVictoryXp(combatantsBefore)
        campaignVersion++
        if (victoryXp > 0) "$text Você venceu e recebeu $victoryXp XP." else text
        } catch (e: IllegalArgumentException) { e.message }
          catch (e: IllegalStateException) { e.message }
    }

    fun endCombat(): String? = when (val result = session.execute(dev.rpgsolo.engine.EndCombat)) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    fun combatState(): dev.rpgsolo.engine.CombatState? = session.state.combat

    fun combatantName(id: String): String = session.state.entities[id]?.name ?: id

    /** Ordem de iniciativa em palavras; nunca expõe atributos ou recursos dos NPCs. */
    fun combatOrderDescription(): String? {
        val combat = session.state.combat ?: return null
        return combat.order.mapNotNull { id ->
            val entity = session.state.entities[id]?.takeIf { it.alive } ?: return@mapNotNull null
            if (id == playerId) "Você" else entity.name
        }.joinToString(" → ").ifBlank { null }
    }

    fun combatTargets(): List<CombatTarget> = session.state.combat?.order.orEmpty()
        .filter { it != playerId }
        .mapNotNull { id ->
            session.state.entities[id]?.takeIf { it.alive }?.let { entity ->
                val resourceId = activePackage?.manifest?.deathResource
                val resource = resourceId?.let { entity.resources[it] }
                val condition = when {
                    resource == null || resource.max <= 0 -> ""
                    resource.current * 4 <= resource.max -> "gravemente ferido"
                    resource.current * 4 <= resource.max * 3 -> "ferido"
                    else -> "ileso"
                }
                CombatTarget(entity.id, entity.name, entity.rulesValidated, condition)
            }
        }

    fun isPlayerCombatTurn(): Boolean =
        session.state.combat?.current == playerId

    fun canDefend(): Boolean = (activePackage?.engineConfig()?.defendBonus ?: 0) > 0 && session.state.entities[playerId]?.alive == true

    /** Só recompensa quando o combate terminou e todos os NPCs que participaram foram derrotados. */
    private fun awardCombatVictoryXp(combatantIds: Collection<String>): Int {
        if (session.state.combat != null || session.state.entities[playerId]?.alive != true) return 0
        val npcIds = combatantIds.filter { session.state.entities[it]?.kind == EntityKind.NPC }
        if (npcIds.isEmpty() || npcIds.any { session.state.entities[it]?.alive == true }) return 0
        val xp = activePackage?.data?.creation?.get("combate_xp_vitoria")?.toIntOrNull() ?: 0
        if (xp <= 0) return 0
        return when (session.execute(dev.rpgsolo.engine.GainXp(playerId, xp))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> xp
            is dev.rpgsolo.engine.ExecResult.Rejected -> 0
        }
    }

    fun additionalRulesView(): List<dev.rpgsolo.engine.RegraAdicional> =
        session.state.additionalRules.values.sortedWith(compareBy({ it.approved }, { it.name }))

    /** Relações direcionadas ao jogador; mostra atitude em palavras, nunca os valores numéricos internos. */
    fun relationshipsView(): List<RelationshipView> =
        session.state.relations.mapNotNull { (key, value) ->
            val ids = key.split('\u0000', limit = 2)
            if (ids.size != 2 || ids[1] != playerId) return@mapNotNull null
            val npc = session.state.entities[ids[0]]?.takeIf { it.kind == EntityKind.NPC } ?: return@mapNotNull null
            val attitude = when {
                value >= 60 -> "confia em você"
                value >= 20 -> "amistoso"
                value <= -60 -> "hostil"
                value <= -20 -> "desconfiado"
                else -> "neutro"
            }
            RelationshipView(npc.name, attitude)
        }.sortedBy { it.npcName }

    fun placesView(): List<PlaceView> {
        val state = session.state
        return state.knownPlaces.mapNotNull { id ->
            val place = state.places[id] ?: return@mapNotNull null
            val exits = state.placeLinks[id].orEmpty().filter { it in state.knownPlaces }
                .mapNotNull { nextId -> state.places[nextId]?.let { nextId to it.name } }
            PlaceView(id, place.name, place.description, state.currentPlaceId == id, exits)
        }.sortedWith(compareByDescending<PlaceView> { it.current }.thenBy { it.name })
    }

    fun moveToPlace(id: String): String? = when (
        val result = session.execute(dev.rpgsolo.engine.MoveToPlace(id))
    ) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    private fun approvedRuleBonus(scope: String): Int =
        session.state.additionalRules.values.filter { it.approved }.sumOf { it.effects[scope] ?: 0 }

    private fun approvedRuleEffects(scopes: Collection<String>): List<dev.rpgsolo.rules.ExternalBonus> =
        session.state.additionalRules.values.filter { it.approved }.flatMap { rule ->
            scopes.distinct().mapNotNull { scope ->
                rule.effects[scope]?.takeIf { it != 0 }?.let { bonus ->
                    dev.rpgsolo.rules.ExternalBonus(scope, bonus, "adaptação ${rule.id}")
                }
            }
        }

    /** Explica quais adaptações aprovadas contribuíram para os escopos usados neste cálculo. */
    private fun approvedRuleBonusSummary(scopes: Collection<String>): String {
        val details = session.state.additionalRules.values
            .filter { it.approved }
            .flatMap { rule ->
                scopes.distinct().mapNotNull { scope ->
                    rule.effects[scope]?.takeIf { it != 0 }?.let { bonus ->
                        "${rule.id} ($scope ${if (bonus > 0) "+" else ""}$bonus)"
                    }
                }
            }
        return if (details.isEmpty()) "" else " · adaptações: ${details.joinToString(", ")}"
    }

    fun approveAdditionalRule(id: String): String? = when (
        val result = session.execute(dev.rpgsolo.engine.ApproveRuleAddition(id))
    ) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    fun rejectAdditionalRule(id: String): String? = when (
        val result = session.execute(dev.rpgsolo.engine.RejectRuleAddition(id))
    ) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    /** Ganho/Perda das condições para um teste que usa o atributo informado. */
    private fun conditionDiceShift(entityId: String, stat: String): Int =
        session.state.conditions[entityId].orEmpty().sumOf { conditionId ->
            val effect = activePackage?.data?.conditions?.firstOrNull { it.id == conditionId }?.effect
            if (effect != null && (effect.stats == null || stat in effect.stats)) effect.dice else 0
        }

    /** Traços mecânicos do combatente; a ficha salva cobre campanhas antigas cujo Entity ainda não os continha. */
    private fun combatTraitIds(entityId: String): List<String> =
        (session.state.entities[entityId]?.traits.orEmpty() +
            if (entityId == playerId) activeFicha?.traits.orEmpty() else emptyList()).distinct()

    fun inventoryView(): List<InventoryView> {
        val pkg = activePackage
        val equipped = session.state.equipped[playerId].orEmpty()
        return session.state.inventory[playerId].orEmpty().map { (id, quantity) ->
            val item = pkg?.item(id)
            InventoryView(
                id = id,
                name = item?.name ?: id,
                quantity = quantity,
                kind = item?.kind ?: "desconhecido",
                slot = item?.slot,
                equipped = item?.slot?.let { equipped[it] == id } == true,
                description = item?.description ?: "Item não existe no pacote ativo."
            )
        }.filter { it.quantity > 0 }.sortedWith(compareBy<InventoryView>({ it.kind }, { it.name }))
    }

    /** Usa consumíveis apenas quando o pacote declara uma ação mecânica explícita. */
    fun useItem(itemId: String): String? = try {
        val pkg = requireNotNull(activePackage) { "pacote de regras não está ativo" }
        val item = requireNotNull(pkg.item(itemId)) { "item não existe no pacote ativo: $itemId" }
        require(item.kind == "consumivel") { "este item não é consumível" }
        val action = pkg.data.creation["item_${itemId}_acao"]
            ?: return "o pacote não definiu o efeito mecânico de $itemId"
        val inCombat = session.state.combat != null
        if (inCombat && !isPlayerCombatTurn()) return "só é possível usar item no seu turno"
        val commands: List<dev.rpgsolo.engine.Command> = when (action) {
            "cura" -> {
                val resource = pkg.data.creation["item_${itemId}_recurso"]
                    ?: return "o pacote não definiu o recurso restaurado por $itemId"
                val amount = pkg.data.creation["item_${itemId}_quantidade"]?.toIntOrNull()
                    ?: return "o pacote não definiu a quantidade restaurada por $itemId"
                require(amount > 0) { "quantidade de cura deve ser positiva" }
                val current = session.state.entities[playerId]?.resources?.get(resource)
                    ?: return "o personagem não possui o recurso '$resource'"
                require(current.current < current.max) { "o recurso já está cheio; o consumível não foi gasto" }
                listOf(
                    dev.rpgsolo.engine.Heal(playerId, resource, amount),
                    dev.rpgsolo.engine.ConsumeItem(playerId, itemId, 1)
                )
            }
            else -> return "ação de consumível não suportada pelo app: $action"
        }
        val actionCommands = if (inCombat) commands + dev.rpgsolo.engine.NextTurn else commands
        when (val result = session.executeAll(actionCommands)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                "${item.name} usado."
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun equipItem(itemId: String): String? = try {
        require(session.state.combat == null) { "não é possível trocar equipamento durante o combate" }
        val item = requireNotNull(activePackage?.item(itemId)) { "item não existe no catálogo: $itemId" }
        val slot = item.slot
        require(item.kind == "equipamento" && slot != null) { "item não é equipável" }
        when (val result = session.execute(dev.rpgsolo.engine.EquipItem(playerId, itemId, slot))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }

    fun unequipItem(slot: String): String? {
        if (session.state.combat != null) return "não é possível trocar equipamento durante o combate"
        return when (val result = session.execute(dev.rpgsolo.engine.UnequipItem(playerId, slot))) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** Ataque local: atributos e bônus vêm do pacote e do equipamento; a IA não escolhe números. */
    fun attack(targetId: String): String? = try {
        require(isPlayerCombatTurn()) { "não é o turno do jogador" }
        val pkg = requireNotNull(activePackage) { "pacote de regras não está ativo" }
        val resource = pkg.manifest.deathResource
            ?: return "o pacote não define o recurso de vida usado no combate"
        val attackStat = pkg.data.creation["combate_atributo_ataque"]
            ?: return "o pacote não define o atributo de ataque"
        val defenseStat = pkg.data.creation["combate_atributo_defesa"]
            ?: return "o pacote não define o atributo de defesa"
        val attackDiceCount = pkg.data.creation["combate_dados_ataque"]?.toIntOrNull()
            ?: return "o pacote não define combate_dados_ataque"
        val attackDiceSides = pkg.data.creation["combate_lados_ataque"]?.toIntOrNull()
            ?: return "o pacote não define combate_lados_ataque"
        val attackComparison = pkg.data.creation["combate_comparacao_ataque"]?.let {
            runCatching { dev.rpgsolo.engine.Comparison.valueOf(it) }.getOrNull()
        } ?: return "o pacote não define uma comparação de ataque válida"
        val baseDamage = pkg.data.creation["combate_dano_base"]?.toIntOrNull()
            ?: return "o pacote não define combate_dano_base"
        val baseDefense = pkg.data.creation["combate_defesa_base"]?.toIntOrNull()
            ?: return "o pacote não define combate_defesa_base"
        val damageDef = pkg.data.creation["combate_dano"]?.let { id ->
            pkg.data.damage.firstOrNull { it.id == id }
                ?: return "o pacote referencia um perfil de dano inexistente: $id"
        }
        val attackerEquipment = session.state.equipped[playerId].orEmpty().values
        val targetEquipment = session.state.equipped[targetId].orEmpty().values
        val attackConditionShift = conditionDiceShift(playerId, attackStat)
        val targetConditionShift = conditionDiceShift(targetId, defenseStat)
        // Condições modificam bônus/limiar; a quantidade de dados vem exclusivamente do pacote.
        val effectiveAttackDice = attackDiceCount
        val combatantsBefore = session.state.combat?.order.orEmpty()
        val attackMark = session.mark()
        when (val result = session.attack(
            dev.rpgsolo.engine.AttackSpec(
                attackerId = playerId,
                targetId = targetId,
                attackStat = attackStat,
                defenseStat = defenseStat,
                attackDiceCount = effectiveAttackDice,
                attackDiceSides = attackDiceSides,
                attackComparison = attackComparison,
                damageResource = resource,
                damage = baseDamage,
                defenseDefault = baseDefense,
                attackBonus = pkg.equipmentBonus(attackerEquipment, "ataque") + pkg.traitBonus(combatTraitIds(playerId), "ataque") +
                    selectedCatalogBonus(pkg, "ataque") + approvedRuleBonus("ataque") + approvedRuleBonus(attackStat) +
                    attackConditionShift,
                damageBonus = pkg.equipmentBonus(attackerEquipment, "dano") + pkg.traitBonus(combatTraitIds(playerId), "dano") +
                    selectedCatalogBonus(pkg, "dano") + approvedRuleBonus("dano") + (damageDef?.bonusStat?.let(::approvedRuleBonus) ?: 0),
                defenseBonus = pkg.equipmentBonus(targetEquipment, "defesa") +
                    pkg.traitBonus(combatTraitIds(targetId), "defesa") + targetConditionShift,
                damageReduction = pkg.traitBonus(combatTraitIds(targetId), "resistencia_dano"),
                damageDiceCount = damageDef?.diceCount ?: 0,
                damageDiceSides = damageDef?.diceSides ?: 0,
                damageStat = damageDef?.bonusStat
            )
        )) {
            is dev.rpgsolo.engine.AttackResult.Resolved -> {
                if (session.state.combat != null) {
                    when (val advance = session.execute(dev.rpgsolo.engine.NextTurn)) {
                        is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
                        is dev.rpgsolo.engine.ExecResult.Rejected -> {
                            session.rollbackTo(attackMark)
                            return advance.reason
                        }
                    }
                }
                val victoryXp = awardCombatVictoryXp(combatantsBefore)
                campaignVersion++
                val attackText = "${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}"
                val outcome = if (result.hit) {
                    val damageText = result.damageRoll?.let {
                        " Dano: ${it.expression} [${it.dice.joinToString(", ")}] = ${it.total}."
                    }.orEmpty()
                    "Ataque: $attackText. Acertou e causou ${result.damage} de dano.$damageText"
                } else "Ataque: $attackText. Errou."
                val withSources = outcome +
                    selectedCatalogBonusSummary(pkg, if (result.hit) listOf("ataque", "dano") else listOf("ataque")) +
                    approvedRuleBonusSummary(
                        if (result.hit) listOf("ataque", attackStat, "dano", damageDef?.bonusStat ?: "") else listOf("ataque", attackStat)
                    )
                if (victoryXp > 0) "$withSources Vitória! Você recebeu $victoryXp XP." else withSources
            }
            is dev.rpgsolo.engine.AttackResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    /** A postura defensiva concede o bônus do pacote contra o próximo ataque recebido e consome o turno. */
    fun defend(): String? {
        if (!isPlayerCombatTurn()) return "só é possível defender no seu turno"
        val combatantsBefore = session.state.combat?.order.orEmpty()
        return when (val result = session.executeAll(listOf(
            dev.rpgsolo.engine.Defend(playerId),
            dev.rpgsolo.engine.NextTurn
        ))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                awardCombatVictoryXp(combatantsBefore)
                campaignVersion++
                "Postura defensiva ativa até o próximo ataque recebido ou o retorno do seu turno."
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** Fuga usa um teste real do pacote; sucesso encerra o combate, falha cede a próxima ação ao adversário. */
    fun fleeCombat(): String? = try {
        require(isPlayerCombatTurn()) { "só é possível fugir no seu turno" }
        val pkg = requireNotNull(activePackage) { "pacote de regras não está ativo" }
        val checkId = listOf("fuga", "esquiva", "furtividade").firstOrNull { id ->
            pkg.data.checks.any { it.id == id }
        } ?: return "o pacote não define um teste de fuga, esquiva ou furtividade"
        val difficulty = pkg.defaultDifficulty()
            ?: return "o pacote não define dificuldade para o teste de fuga"
        val player = requireNotNull(session.state.entities[playerId]) { "personagem inexistente" }
        val combatantsBefore = session.state.combat?.order.orEmpty()
        val mark = session.mark()
        val base = pkg.check(checkId, player.stats, difficulty, player.skills)
        val adjusted = pkg.adjustCheck(
            base,
            session.state.conditions[playerId].orEmpty(),
            combatTraitIds(playerId),
            checkId,
            additionalBonuses = selectedCatalogBonuses(pkg)
        )
        val adaptation = listOfNotNull(adjusted.checkId, adjusted.stat, checkId).distinct().sumOf(::approvedRuleBonus)
        val danger = session.state.campaignStyle?.difficultyParams?.get("danger_level")?.roundToInt()?.coerceIn(0, 10) ?: 5
        val shift = danger - 5
        val spec = when (adjusted.comparison) {
            dev.rpgsolo.engine.Comparison.ROLL_OVER -> adjusted.copy(modifier = adjusted.modifier + adaptation - shift)
            dev.rpgsolo.engine.Comparison.ROLL_UNDER -> adjusted.copy(
                difficulty = (adjusted.difficulty + adaptation - shift).coerceAtLeast(1)
            )
            dev.rpgsolo.engine.Comparison.POINTS -> adjusted.copy(
                modifier = (adjusted.modifier + adaptation - shift).coerceAtLeast(0)
            )
        }
        val result = session.check(spec)
        val roll = "${result.spec.label}: ${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}." +
            approvedRuleBonusSummary(listOfNotNull(adjusted.checkId, adjusted.stat, checkId))
        if (result.outcome in setOf(
                dev.rpgsolo.engine.Outcome.SUCCESS,
                dev.rpgsolo.engine.Outcome.CRITICAL_SUCCESS
            )
        ) {
            when (val ended = session.execute(dev.rpgsolo.engine.EndCombat)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    campaignVersion++
                    "$roll Você conseguiu fugir e encerrou o combate."
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> {
                    session.rollbackTo(mark)
                    ended.reason
                }
            }
        } else {
            when (val advanced = session.execute(dev.rpgsolo.engine.NextTurn)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    awardCombatVictoryXp(combatantsBefore)
                    campaignVersion++
                    "$roll Você não conseguiu escapar; o adversário age agora."
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> {
                    session.rollbackTo(mark)
                    advanced.reason
                }
            }
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun exportBackup(): String = dev.rpgsolo.engine.Backup.export(
        store,
        session.campaignId,
        dev.rpgsolo.engine.CampaignBackupMetadata(
            chaptersJson = dev.rpgsolo.engine.Codec.encodeChapters(chapters.chapters),
            questsJson = dev.rpgsolo.engine.Codec.encodeQuests(quests.all()),
            timelineJson = dev.rpgsolo.engine.Codec.encodeTimeline(quests.timeline()),
            playerId = playerId,
            fichaId = activeFichaId ?: activeFicha?.id,
            packageId = activePackage?.manifest?.id,
            systemId = activeSystemId ?: activeFicha?.system?.systemId,
            editionId = activeEditionId ?: activeFicha?.system?.editionId,
            fichas = fichas.map { dev.rpgsolo.engine.Codec.encode(it) },
            characterChoices = fichas.associate { ficha ->
                ficha.id to characterChoicesPrefs.getString(ficha.id, null).orEmpty()
            }.filterValues { it.isNotBlank() }
        )
    )

    fun importBackup(text: String): String? = try {
        val raw = text.trim()
        val file = dev.rpgsolo.engine.Backup.decode(raw)
        val id = dev.rpgsolo.engine.Backup.import(store, raw, "import-${System.nanoTime()}")
        file.metadata?.let { meta ->
            store.saveMetadata(
                CampanhaMetaRow(
                    campanhaId = id,
                    chaptersJson = meta.chaptersJson,
                    questsJson = meta.questsJson,
                    timelineJson = meta.timelineJson,
                    playerId = meta.playerId,
                    fichaId = meta.fichaId,
                    packageId = meta.packageId,
                    systemId = meta.systemId,
                    editionId = meta.editionId
                )
            )
            meta.fichas.forEach { rawFicha ->
                runCatching {
                    val ficha = dev.rpgsolo.engine.Codec.decodeFicha(rawFicha)
                    store.saveFicha(FichaRow(ficha.id, ficha.name, rawFicha))
                }
            }
            meta.characterChoices.forEach { (fichaId, rawChoices) ->
                if (fichaId.isNotBlank() && rawChoices.isNotBlank()) {
                    val choices = CharacterChoices.fromJson(rawChoices)
                    characterChoicesPrefs.edit().putString(fichaId, choices.toJson()).apply()
                }
            }
        }
        restoreFichas()
        switchCampaign(id)
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }
      catch (e: Exception) { "falha ao restaurar backup: ${e.message ?: "erro inesperado"}" }

    private fun restoreMetadata() {
        chapters = ChapterBook(chapterEventLimit)
        quests = QuestLog()
        val row = store.metadata(session.campaignId)
        if (row != null) {
            runCatching {
                chapters.restore(dev.rpgsolo.engine.Codec.decodeChapters(row.chaptersJson))
                quests.restore(dev.rpgsolo.engine.Codec.decodeQuests(row.questsJson), dev.rpgsolo.engine.Codec.decodeTimeline(row.timelineJson))
            }
        }

        val firstPlayerId = session.state.entities.values.firstOrNull { it.kind == EntityKind.PLAYER }?.id
        playerId = row?.playerId?.takeIf { session.state.entities.containsKey(it) } ?: firstPlayerId ?: "hero"
        activeFichaId = row?.fichaId ?: playerId
        activeFicha = activeFichaId?.let { id -> fichas.firstOrNull { it.id == id } }
        activeSystemId = row?.systemId ?: activeFicha?.system?.systemId
        activeEditionId = row?.editionId ?: activeFicha?.system?.editionId
        activePackage = when {
            row?.editionId != null -> row.packageId?.let(content.registry::get)
            activeFicha != null -> CampaignStarter.packageFor(content.registry, activeFicha!!.system.editionId)
            session.campaignId == "demo" -> content.registry.get("sistema-proprio")
            else -> null
        }
    }

    private fun pendingCombatKey(): String = "pending_combat_${session.campaignId}"

    private fun savePendingCombatNpcIds() {
        activeCampaignPrefs.edit().putStringSet(pendingCombatKey(), pendingCombatNpcIds.toSet()).apply()
    }

    private fun restorePendingCombatNpcIds() {
        pendingCombatNpcIds.clear()
        pendingCombatNpcIds += activeCampaignPrefs.getStringSet(pendingCombatKey(), emptySet()).orEmpty()
            .filter { session.state.entities[it]?.let { entity -> entity.kind == EntityKind.NPC && entity.alive } == true }
        savePendingCombatNpcIds()
    }

    /** Troca primeiro para uma sessão já validada; depois restaura contexto e controlador da campanha selecionada. */
    private fun switchCampaign(campaignId: String) {
        val next = CampaignSession.open(store, campaignId)
        pendingCombatNpcIds.clear()
        session = next
        activeCampaignPrefs.edit().putString("active_campaign", campaignId).apply()
        restoreMetadata()
        restorePendingCombatNpcIds()
        if (chapters.chapters.isEmpty()) openChapter("Capítulo 1")
        controller = roles?.let { buildController(it) }
    }

    private fun saveMetadata() {
        store.saveMetadata(
            CampanhaMetaRow(
                session.campaignId,
                dev.rpgsolo.engine.Codec.encodeChapters(chapters.chapters),
                dev.rpgsolo.engine.Codec.encodeQuests(quests.all()),
                dev.rpgsolo.engine.Codec.encodeTimeline(quests.timeline()),
                playerId = playerId,
                fichaId = activeFichaId ?: activeFicha?.id,
                packageId = activePackage?.manifest?.id,
                systemId = activeSystemId ?: activeFicha?.system?.systemId,
                editionId = activeEditionId ?: activeFicha?.system?.editionId
            )
        )
    }

    fun renameCampaign(name: String): String? = try {
        store.rename(session.campaignId, name)
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }

    fun duplicateCampaign(): String? = try {
        val source = requireNotNull(store.campaign(session.campaignId))
        store.duplicate(source.id, "${source.id}-copy-${System.nanoTime()}", "${source.name} (cópia)")
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }

    fun deleteCurrentCampaign(): String? = try {
        require(campaigns.size > 1) { "mantenha pelo menos uma campanha" }
        store.delete(session.campaignId)
        val next = store.campaigns().first()
        switchCampaign(next.id)
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }

    /** Valida e aplica a configuração. Erro claro na tela se o endpoint ou o modelo forem inválidos. */
    fun applySettings(
        kind: ProviderKind, endpoint: String, model: String, apiKey: String,
        cheapModel: String = "", premiumModel: String = "",
        cacheSystemPrompt: Boolean = endpoint.trim().startsWith("https://api.anthropic.com/"),
        inputPricePerMillion: Double = 0.0,
        outputPricePerMillion: Double = 0.0,
        cacheReadPricePerMillion: Double = inputPricePerMillion,
        cacheCreationPricePerMillion: Double = inputPricePerMillion
    ): Boolean =
        try {
            require(apiKey.isNotBlank()) { "informe a chave de API" }
            require(listOf(inputPricePerMillion, outputPricePerMillion, cacheReadPricePerMillion, cacheCreationPricePerMillion)
                .all { it.isFinite() && it >= 0.0 }) { "preços por milhão devem ser números finitos e não negativos" }
            val r = ProviderFactory.buildRoles(
                kind, ProviderConfig(endpoint.trim(), model.trim(), apiKey.trim(), cacheSystemPrompt = cacheSystemPrompt), cheapModel, premiumModel, usage
            )
            roles = r
            controller = buildController(r)
            storedSettings = StoredProviderSettings(
                kind = kind.name,
                endpoint = endpoint.trim(),
                model = model.trim(),
                cheapModel = cheapModel.trim(),
                premiumModel = premiumModel.trim(),
                apiKey = apiKey.trim(),
                cacheSystemPrompt = cacheSystemPrompt,
                inputPricePerMillion = inputPricePerMillion,
                outputPricePerMillion = outputPricePerMillion,
                cacheReadPricePerMillion = cacheReadPricePerMillion,
                cacheCreationPricePerMillion = cacheCreationPricePerMillion
            ).also(secureSettings::save)
            configError = null
            true
        } catch (e: IllegalArgumentException) {
            configError = e.message
            false
        }

    fun savedSettings(): StoredProviderSettings? = storedSettings

    /** Progresso real do primeiro uso, derivado do provedor, ficha ativa e campanha ativa. */
    fun setupStatus(): SetupStatus = SetupStatus(
        providerReady = controller != null && roles != null,
        heroReady = activeFicha != null || fichas.isNotEmpty(),
        campaignReady = session.campaignId != "demo" && activeFicha != null
    )

    /** Faz uma chamada mínima ao provedor informado, sem persistir a configuração nem tocar no jogo. */
    fun testConnection(
        kind: ProviderKind, endpoint: String, model: String, apiKey: String,
        cacheSystemPrompt: Boolean = endpoint.trim().startsWith("https://api.anthropic.com/")
    ): ConnectionTestResult = try {
        require(endpoint.trim().startsWith("http://") || endpoint.trim().startsWith("https://")) {
            "o endpoint deve começar com http:// ou https://"
        }
        require(model.isNotBlank()) { "informe o modelo" }
        require(apiKey.isNotBlank()) { "informe a chave de API" }
        val provider = ProviderFactory.build(
            kind,
            ProviderConfig(endpoint.trim(), model.trim(), apiKey.trim(), cacheSystemPrompt = cacheSystemPrompt),
            usage = usage,
            maxRequestsPerMinute = 20
        )
        val reply = provider.complete(
            AiRequest(
                messages = listOf(AiMessage(Role.USER, "Responda apenas OK.")),
                maxTokens = 8,
                temperature = 0.0
            )
        )
        if (reply.text.isBlank()) ConnectionTestResult(false, "o provedor respondeu sem texto")
        else ConnectionTestResult(true, "conexão funcionando (${provider.name})")
    } catch (e: AiError) {
        ConnectionTestResult(false, e.message ?: "falha na conexão")
    } catch (e: IllegalArgumentException) {
        ConnectionTestResult(false, e.message ?: "configuração inválida")
    } catch (e: Exception) {
        ConnectionTestResult(false, "não foi possível testar a conexão: ${e.message ?: "erro desconhecido"}")
    }

    /** Remove a chave cifrada e desativa o provedor atual. */
    fun forgetProvider() {
        secureSettings.clear()
        storedSettings = null
        roles = null
        controller = null
        configError = null
    }

    /** Testes disponíveis para os fluxos avançados; derivados do pacote e ficha ativos. */
    fun advancedCheckOptions(): List<CheckOption> {
        val pkg = activePackage ?: return emptyList()
        val player = session.state.entities[playerId] ?: return emptyList()
        return pkg.checkChoices(player.stats, skills = player.skills)
            .map { CheckOption(it.id, it.description, it.spec) }
    }

    /** NPCs vivos que podem participar de uma disputa; não expõe fichas privadas na interface. */
    fun advancedCheckTargets(): List<CombatTarget> =
        session.state.entities.values
            .filter { it.kind == EntityKind.NPC && it.alive }
            .map { npc ->
                CombatTarget(
                    id = npc.id,
                    name = npc.name,
                    rulesValidated = npc.rulesValidated
                )
            }
            .sortedBy { it.name }

    /** Resolve disputa jogador-versus-NPC usando os mesmos testes do pacote para ambos os lados. */
    fun resolveOpposedCheck(targetId: String, checkId: String): String {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val player = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val target = session.state.entities[targetId]?.takeIf { it.kind == EntityKind.NPC && it.alive }
            ?: return "O adversário não existe ou não está disponível."
        val playerChoice = pkg.checkChoices(player.stats, skills = player.skills).firstOrNull { it.id == checkId }
            ?: return "O teste selecionado não existe no pacote ativo."
        val targetChoice = pkg.checkChoices(target.stats, skills = target.skills).firstOrNull { it.id == checkId }
            ?: return "O adversário não tem dados suficientes para esse teste."
        val result = controller?.resolveOpposed(
            playerChoice.spec, targetChoice.spec, player.name, target.name, player.id, target.id
        ) ?: return "O controlador da campanha não está disponível."
        campaignVersion++
        return when (result) {
            is dev.rpgsolo.ai.PlayResult.Played -> "Disputa resolvida. Revele as rolagens no chat para ver o desfecho."
            is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
        }
    }

    /** Resolve teste de grupo com participantes selecionados e limiar explícito na interface. */
    fun resolveGroupCheck(targetIds: List<String>, checkId: String, requiredSuccesses: Int): String {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val player = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val uniqueTargets = targetIds.distinct()
        if (uniqueTargets.size != targetIds.size) return "A lista de participantes contém duplicatas."
        val targets = uniqueTargets.map { id ->
            session.state.entities[id]?.takeIf { it.kind == EntityKind.NPC && it.alive }
                ?: return "Participante indisponível: " + id
        }
        val participants = listOf(player) + targets
        if (requiredSuccesses !in 1..participants.size) return "O limiar de sucessos deve ficar entre 1 e o total de participantes."
        val checks = participants.map { participant ->
            pkg.checkChoices(participant.stats, skills = participant.skills).firstOrNull { it.id == checkId }?.spec
                ?: return "O teste '" + checkId + "' não pode ser usado por " + participant.name + "."
        }
        val result = controller?.resolveGroup(
            checks = checks,
            requiredSuccesses = requiredSuccesses,
            participantNames = participants.map { it.name },
            participantEntityIds = participants.map { it.id }
        ) ?: return "O controlador da campanha não está disponível."
        campaignVersion++
        return when (result) {
            is dev.rpgsolo.ai.PlayResult.Played -> "Teste de grupo resolvido. Revele as rolagens no chat para ver o resultado."
            is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
        }
    }

    /**
     * 7.1.2: testes do pacote ativo (demo: sistema-proprio) com os atributos do herói. Sem pacote, sem testes:
     * toda ação segue sem teste e o mestre só narra. Falha do classificador vira aviso no chat, não erro.
     */
    private fun checkFor(provider: AiProvider): (String) -> dev.rpgsolo.engine.CheckSpec? {
        val pkg = activePackage ?: return { null }
        val stats = session.state.entities[playerId]?.stats.orEmpty()
        val skills = session.state.entities[playerId]?.skills.orEmpty()
        val options = pkg.checkChoices(stats, skills = skills).map { CheckOption(it.id, it.description, it.spec) }
        return IntentClassifier(provider).asCheckFor(options) { classifierWarning = it }
    }

    private fun buildController(r: AiRoles): PlayController {
        val npcCreator: (dev.rpgsolo.rules.NpcProposal) -> dev.rpgsolo.rules.NpcResult = { proposal ->
            val pkg = activePackage
            val systemRef = activeFicha?.system ?: if (activeSystemId != null && activeEditionId != null) {
                dev.rpgsolo.engine.SystemRef(activeSystemId!!, activeEditionId!!)
            } else null
            dev.rpgsolo.rules.NpcFactory.create(
                proposal.copy(
                    ruleSource = if (pkg != null) dev.rpgsolo.rules.RuleSource.PACOTE
                    else dev.rpgsolo.rules.RuleSource.IA_NAO_VERIFICADA
                ),
                pkg,
                systemRef
            )
        }
        val loop = GameLoop(
            r.narrator,
            runner = TurnRunner(r.narrator, npcCreator = npcCreator),
            premiumRunner = if (r.hasPremium) TurnRunner(r.premium, npcCreator = npcCreator) else null,
            menu = activePackage?.let { CheckMenu(it) },
            playerTraits = { activeFicha?.traits.orEmpty() },
            additionalBonus = { scope -> approvedRuleBonus(scope) },
            externalBonuses = { scopes ->
                activePackage?.let { pkg -> selectedCatalogBonuses(pkg).filter { it.scope in scopes } }.orEmpty()
            },
            additionalBonusSources = { scopes -> approvedRuleEffects(scopes) }
        )
        return PlayController(
            session,
            loop,
            playerId,
            healthResource = activePackage?.manifest?.deathResource
                ?: session.state.entities[playerId]?.resources?.keys?.firstOrNull()
                ?: "pv",
            checkFor = checkFor(r.cheap),
            quests = quests,
            restResources = activePackage?.data?.resources?.map { it.id }?.toSet()
                ?: session.state.entities[playerId]?.resources?.keys.orEmpty(),
            sheet = PlayerSheet(activePackage, activeFicha, characterChoicesForAi(activeFicha?.id.orEmpty())),
            conditionNames = activePackage?.data?.conditions?.associate { it.id to it.name }.orEmpty(),
            ruleLookup = { query ->
                val packageRules = activePackage?.search(query, 5)?.map { "${it.kind}: ${it.title} — ${it.text}" }.orEmpty()
                val adaptationRules = session.state.additionalRules.values.filter {
                    query.isBlank() || listOf(it.name, it.description, it.effects.toString()).any { value ->
                        value.contains(query, ignoreCase = true)
                    }
                }.map { rule ->
                    "ADAPTAÇÃO ${if (rule.approved) "APROVADA" else "PENDENTE"}: ${rule.name} — ${rule.description}; efeitos ${rule.effects}; limite ${rule.limit}"
                }
                (packageRules + adaptationRules).take(5)
            },
            chapterSummary = { chapters.summaries().takeLast(3).joinToString("\n").ifBlank { null } },
            npcContext = { text -> privateNpcContext(text) },
            sceneNpcIds = { text -> npcIdsForScene(text) },
            generationPreferences = {
                buildString {
                    append("Preferências de narração do jogador: extensão ${narrationLength}; estilo ${narrationStyle}. Preserve as regras, fatos e resultados do motor.")
                    contentLines.trim().takeIf { it.isNotEmpty() }?.let {
                        append("\nLINHAS VERMELHAS (proibido narrar ou contornar): ").append(it)
                    }
                    contentVeils.trim().takeIf { it.isNotEmpty() }?.let {
                        append("\nVÉUS (não descrever em detalhe; cortar ou resumir): ").append(it)
                    }
                }
            },
            worldContext = {
                buildString {
                    session.state.campaignStyle?.let { style ->
                        fun labels(ids: List<String>): String = ids.mapNotNull { id ->
                            content.catalog?.entry(id)?.name ?: id
                        }.joinToString(", ")
                        style.settingIds.takeIf { it.isNotEmpty() }?.let { append("Estilos de ambientação: ").append(labels(it)).append(".\n") }
                        style.structureIds.takeIf { it.isNotEmpty() }?.let { append("Estrutura da campanha: ").append(labels(it)).append(".\n") }
                        style.philosophyIds.takeIf { it.isNotEmpty() }?.let { append("Filosofia de jogo: ").append(labels(it)).append(".\n") }
                        style.toneIds.takeIf { it.isNotEmpty() }?.let { append("Tom: ").append(labels(it)).append(".\n") }
                        style.difficultyId?.let { id -> append("Dificuldade escolhida: ").append(content.catalog?.entry(id)?.name ?: id).append(".\n") }
                        style.difficultyParams.takeIf { it.isNotEmpty() }?.let { params ->
                            append("Parâmetros de dificuldade: ").append(params.entries.joinToString(", ") { "${it.key}=${it.value}" }).append(".\n")
                            params["resource_scarcity"]?.let { value ->
                                append("Escassez de recursos (0 abundante, 10 escasso): ").append(value)
                                    .append(". Ajuste a frequência narrativa de recursos e saques a esta escala, sem inventar itens mecânicos fora do pacote.\n")
                            }
                            params["defeat_consequence"]?.let { value ->
                                append("Consequências de derrota (0 leves, 10 severas): ").append(value)
                                    .append(". Use contratempos ficcionais proporcionais, sem contrariar o estado ou os resultados do motor.\n")
                            }
                        }
                        style.progressionId?.let { id -> append("Progressão: ").append(content.catalog?.entry(id)?.name ?: id).append(".\n") }
                        style.contentLimits.takeIf { it.isNotEmpty() }?.let { limits ->
                            append("LIMITES DE CONTEÚDO OBRIGATÓRIOS DO JOGADOR: ").append(labels(limits)).append(". Nunca ultrapasse nem tente contornar estes limites.\n")
                        }
                    }
                    session.state.ambientacao?.let { setting ->
                        append("Ambientação ficcional da campanha: ").append(setting.name)
                        setting.tone.takeIf { it.isNotBlank() }?.let { append("; tom: ").append(it) }
                        setting.technology.takeIf { it.isNotBlank() }?.let { append("; tecnologia: ").append(it) }
                        setting.organizations.takeIf { it.isNotEmpty() }?.let { append("; organizações: ").append(it.joinToString(", ")) }
                        setting.conflicts.takeIf { it.isNotEmpty() }?.let { append("; conflitos: ").append(it.joinToString(", ")) }
                        setting.fictionalGear.takeIf { it.isNotEmpty() }?.let { append("; equipamentos ficcionais: ").append(it.joinToString(", ")) }
                        append(". Isto não altera regras mecânicas do sistema/edição.\n")
                    }
                    session.state.currentPlaceId?.let { id ->
                        val place = session.state.places[id]
                        if (place != null && id in session.state.knownPlaces) {
                            append("Local atual: ").append(place.name).append(". ").append(place.description)
                            append(" (id interno: ").append(id).append(")")
                            if (place.secret.isNotBlank() && !session.state.knowledge.containsKey("place-secret:$id")) {
                                append("\nHá um detalhe oculto neste local; não o revele nem invente seu conteúdo.")
                            }
                            val links = session.state.placeLinks[id].orEmpty()
                            val exits = links.filter { it in session.state.knownPlaces }
                                .mapNotNull { session.state.places[it]?.name }
                            append("\nSaídas conhecidas: ").append(exits.joinToString(", ").ifEmpty { "nenhuma" })
                            val unknownExits = links.count { it !in session.state.knownPlaces }
                            if (unknownExits > 0) append("; há ").append(unknownExits).append(" passagem(ns) ainda desconhecida(s), não revele seus nomes.")
                        }
                    }
                }.toString().ifBlank { null }
            }
        )
    }

    /** A cena inclui NPCs do combate e os nomes/ids mencionados explicitamente pelo jogador. */
    private fun npcIdsForScene(text: String): Set<String> {
        val query = text.trim().lowercase()
        val inCombat = session.state.combat?.order.orEmpty().filter { it != playerId }
        val mentioned = session.state.entities.values.filter { npc ->
            npc.kind == EntityKind.NPC && (
                npc.name.isNotBlank() && query.contains(npc.name.lowercase()) ||
                    query.contains(npc.id.lowercase())
            )
        }.map { it.id }
        return (inCombat + mentioned).toSet()
    }

    /** Ficha mecânica privada enviada apenas para NPCs desta cena; textos de segredos ocultos nunca são enviados. */
    private fun privateNpcContext(text: String): List<String> =
        npcIdsForScene(text).mapNotNull { id ->
            val npc = session.state.entities[id] ?: return@mapNotNull null
            val traits = npc.traits.mapNotNull { traitId ->
                activePackage?.trait(traitId)?.let { "${it.name}: ${it.description}" }
            }
            val dossier = session.state.npcDossiers[id]
            buildString {
                append("Ficha privada de NPC — ").append(npc.name)
                if (!npc.rulesValidated) append("; ficha NÃO validada por pacote")
                if (npc.concept.isNotBlank()) append("; conceito: ").append(npc.concept)
                if (npc.stats.isNotEmpty()) append("; atributos mecânicos: ").append(npc.stats.entries.joinToString(", ") { "${it.key}=${it.value}" })
                if (traits.isNotEmpty()) append("; traços: ").append(traits.joinToString(" | "))
                if (npc.skills.isNotEmpty()) append("; perícias: ").append(npc.skills.entries.joinToString(", ") { "${it.key}=${it.value}" })
                dossier?.personality?.takeIf { it.isNotBlank() }?.let { append("; personalidade: ").append(it) }
                dossier?.goals?.takeIf { it.isNotEmpty() }?.let { append("; objetivos: ").append(it.joinToString(", ")) }
                dossier?.motivations?.takeIf { it.isNotEmpty() }?.let { append("; motivações: ").append(it.joinToString(", ")) }
                dossier?.relations?.takeIf { it.isNotEmpty() }?.let { append("; relações privadas: ").append(it.entries.joinToString(", ") { pair -> "${pair.key}: ${pair.value}" }) }
                if (dossier?.secrets?.isNotEmpty() == true) {
                    val undiscovered = dossier.secrets.filterNot { secret ->
                        session.state.knowledge.containsKey("npc-secret:$id:${secret.id}")
                    }
                    if (undiscovered.isNotEmpty()) {
                        append("; ids de segredos ainda ocultos: ").append(undiscovered.joinToString(", ") { it.id })
                        append("; seus textos não foram enviados; só podem ser revelados após teste autorizado")
                    }
                }
                append(". Nunca exponha estes números nem nomes de mecânica ao jogador.")
            }
        }

    /** Fluxo "Nova campanha" (9.1). null até haver provedor de IA configurado. */
    fun newCampaignFlow(): NewCampaignFlow? {
        val r = roles ?: return null
        val catalog = content.catalog ?: return null
        return NewCampaignFlow(CampaignInterpreter(r.cheap), catalog, content.registry, store)
    }

    /** Troca a campanha ativa pela recém-criada: sessão, personagem, pacote, capítulos e missões novos. */
    fun activate(started: StartResult.Started) {
        pendingCombatNpcIds.clear()
        session = started.session
        savePendingCombatNpcIds()
        playerId = started.playerId
        activePackage = started.pkg
        activeFicha = fichas.firstOrNull { it.id == started.campanha.fichaId }
        activeFichaId = started.campanha.fichaId
        activeSystemId = started.campanha.system.systemId
        activeEditionId = started.campanha.system.editionId
        when (val setting = session.execute(dev.rpgsolo.engine.SetAmbientacao(started.campanha.ambientacao))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            is dev.rpgsolo.engine.ExecResult.Rejected -> configError = "ambientação não salva: ${setting.reason}"
        }
        when (val style = session.execute(dev.rpgsolo.engine.SetCampaignStyle(started.campanha.style))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            is dev.rpgsolo.engine.ExecResult.Rejected -> configError = "configuração da campanha não salva: ${style.reason}"
        }
        activeCampaignPrefs.edit().putString("active_campaign", session.campaignId).apply()
        chapters = ChapterBook()
        chapters.open(session, "Capítulo 1")
        quests = QuestLog()
        saveMetadata()
        controller = roles?.let { buildController(it) }
        refreshCampaigns()
        campaignVersion++
    }

    /** Último aviso do classificador; a tela pode mostrá-lo uma vez. */
    var classifierWarning: String? by mutableStateOf(null)

    fun journal(): List<String> = journal(session.state)

    fun openChapter(title: String): String? =
        try {
            chapters.open(session, title)
            saveMetadata()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }

    /**
     * Fecha automaticamente um capítulo quando o log passa do limite configurado e abre o próximo.
     * Chamado depois de uma jogada, em thread de trabalho. Se a síntese falhar, preserva o capítulo aberto.
     */
    fun autoCloseChapterIfNeeded(): String? {
        if (!chapters.shouldClose(session)) return null
        val nextNumber = chapters.chapters.size + 1
        val error = closeChapter()
        if (error != null) {
            chapterNotice = "Não foi possível resumir o capítulo automaticamente: $error"
            return error
        }
        val openError = openChapter("Capítulo $nextNumber")
        chapterNotice = if (openError == null) "Capítulo resumido; Capítulo $nextNumber iniciado." else openError
        return openError
    }

    /** Encerra o capítulo com resumo da IA. Chamada de rede: rode fora da thread principal. Devolve o erro, ou null. */
    fun closeChapter(): String? {
        val p = roles?.cheap ?: return "configure o provedor de IA na aba Config"
        val narrations = controller?.lines.orEmpty().filter { it.author == Author.MASTER }.map { it.text }
        return when (val r = ChapterSummarizer(p).closeWithSummary(session, chapters, narrations)) {
            is ChapterSummarizer.Result.Closed -> { saveMetadata(); campaignVersion++; null }
            is ChapterSummarizer.Result.Failed -> r.reason
        }
    }

    /** Estado de evolução com limites vindos do pacote ativo; nunca inventa opções para sistema sem pacote. */
    fun advancementState(): AdvancementState {
        val player = session.state.entities[playerId]
            ?: return AdvancementState(0, emptyList())
        val pkg = activePackage ?: return AdvancementState(player.advancementPoints, emptyList())
        val stats = pkg.data.stats.map { stat ->
            AdvancementChoice(stat.id, stat.name, player.stats[stat.id] ?: session.engine.config.statDefaults[stat.id] ?: stat.default, session.engine.config.statCaps[stat.id] ?: 0, skill = false)
        }
        val skills = pkg.data.traits.filter { skill ->
            skill.type == "pericia" &&
                ((player.skills[skill.id] ?: 0) > 0 || pkg.canImproveSkill(skill.id, player.stats, player.traits))
        }.map { skill ->
            AdvancementChoice(skill.id, skill.name, player.skills[skill.id] ?: 0, session.engine.config.skillCaps[skill.id] ?: 0, skill = true)
        }
        return AdvancementState(player.advancementPoints, stats + skills)
    }

    /** Gasta exatamente um ponto; motor valida saldo e teto antes de registrar qualquer alteração. */
    fun increaseAdvancement(choiceId: String, skill: Boolean): String? {
        val command = if (skill) {
            dev.rpgsolo.engine.AllocateAdvancement(playerId, skillIncreases = mapOf(choiceId to 1))
        } else {
            dev.rpgsolo.engine.AllocateAdvancement(playerId, statIncreases = mapOf(choiceId to 1))
        }
        return when (val result = session.execute(command)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** Histórico de evolução derivado do log canônico; não duplica dados nem perde eventos ao reabrir campanha. */
    fun progressionHistory(): List<String> {
        val events = session.engine.events
        return events.mapNotNull { event ->
            when (event) {
                is dev.rpgsolo.engine.XpGained -> if (event.entityId == playerId) {
                    val name = session.state.entities[event.entityId]?.name ?: event.entityId
                    "$name recebeu +${event.amount} XP"
                } else null
                is dev.rpgsolo.engine.LevelGained -> if (event.entityId == playerId) {
                    val name = session.state.entities[event.entityId]?.name ?: event.entityId
                    "$name alcançou o nível ${event.level}" +
                        if (event.pointsGranted > 0) " e recebeu ${event.pointsGranted} ponto(s) de evolução" else ""
                } else null
                is dev.rpgsolo.engine.AdvancementAllocated -> if (event.entityId == playerId) {
                    val statNames = event.statIncreases.keys.map { id ->
                        activePackage?.data?.stats?.firstOrNull { it.id == id }?.name ?: id
                    }
                    val skillNames = event.skillIncreases.keys.map { id ->
                        activePackage?.data?.traits?.firstOrNull { it.id == id }?.name ?: id
                    }
                    val choices = statNames + skillNames
                    "Você investiu pontos de evolução em ${choices.joinToString(", ")}"
                } else null
                else -> null
            }
        }.takeLast(50)
    }

    fun addTimelineNote(text: String): String? =
        try {
            require(text.isNotBlank()) { "escreva o acontecimento" }
            require(text.length <= 1000) { "acontecimento deve ter no máximo 1000 caracteres" }
            quests.note(chapters.chapters.size, text)
            saveMetadata()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) { e.message }

    fun addQuest(title: String, objectives: List<String> = emptyList()): String? =
        try {
            require(title.isNotBlank()) { "dê um título à missão" }
            val cleanObjectives = objectives.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
            require(cleanObjectives.size <= 20 && cleanObjectives.all { it.length <= 300 }) {
                "a missão aceita até 20 objetivos, cada um com no máximo 300 caracteres"
            }
            val id = "q${quests.all().size + 1}-${title.hashCode()}"
            quests.add(Quest(
                id = id,
                title = title.trim(),
                objectives = cleanObjectives.map { dev.rpgsolo.engine.Objective(it) }
            ))
            saveMetadata()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }

    fun finishQuest(questId: String, status: dev.rpgsolo.engine.QuestStatus): String? =
        try {
            require(status != dev.rpgsolo.engine.QuestStatus.ACTIVE) { "escolha concluída ou falhou" }
            quests.finish(questId, status)
            saveMetadata()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }

    fun completeQuestObjective(questId: String, index: Int): String? =
        try {
            quests.completeObjective(questId, index)
            saveMetadata()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }
}
