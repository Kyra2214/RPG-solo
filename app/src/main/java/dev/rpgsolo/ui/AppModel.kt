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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
import dev.rpgsolo.engine.chapterClocks
import dev.rpgsolo.engine.travelClocks
import dev.rpgsolo.engine.Quest
import dev.rpgsolo.engine.QuestLog
import dev.rpgsolo.engine.journal
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.SetSkillTreeLoadout
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.SpellSlots
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
data class TraitUseView(val id: String, val name: String, val description: String, val used: Int, val limit: Int)

data class PowerView(
    val id: String, val name: String, val description: String,
    val resourceName: String, val cost: Int, val needsTarget: Boolean
)
data class InventoryView(
    val id: String, val name: String, val quantity: Int, val kind: String,
    val slot: String?, val equipped: Boolean, val description: String,
    val price: Int? = null
)
data class ShopEntry(val id: String, val name: String, val price: Int, val description: String)
data class SpellShopEntry(val id: String, val name: String, val price: Int, val learned: Boolean)
data class ShopView(
    val currencyName: String,
    val balance: Int,
    val entries: List<ShopEntry>,
    /** MU-07.1: ouro (null = pacote sem ouro); [balance] já soma o ouro convertido em moeda base. */
    val goldName: String? = null,
    val goldHeld: Int = 0,
    val silverHeld: Int = 0,
    val silverPerGold: Int = 0
)
data class AdvancementChoice(val id: String, val name: String, val current: Int, val max: Int, val skill: Boolean)
data class AdvancementState(val pointsAvailable: Int, val choices: List<AdvancementChoice>)
data class StatPointsState(val statPoints: Int, val abilityPoints: Int, val choices: List<AdvancementChoice>)
data class InitialBuildState(val allocated: Boolean, val stats: List<AdvancementChoice>)
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
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == fichaId }
        if (ficha == null) {
            // Compatibilidade para chamadas antigas que possam anteceder a gravação da ficha.
            characterChoicesPrefs.edit().putString(fichaId, json).apply()
            return
        }
        if (ficha.editorialChoicesJson != json) {
            val updated = ficha.copy(editorialChoicesJson = json)
            // Persistir primeiro no modelo canônico; se o banco falhar, não avançar o espelho.
            store.saveFicha(FichaRow(updated.id, updated.name, dev.rpgsolo.engine.Codec.encode(updated)))
            val activeIndex = fichas.indexOfFirst { it.id == fichaId }
            if (activeIndex >= 0) fichas[activeIndex] = updated
            val archivedIndex = archivedFichas.indexOfFirst { it.id == fichaId }
            if (archivedIndex >= 0) archivedFichas[archivedIndex] = updated
            if (activeFichaId == fichaId) activeFicha = updated
        }
        characterChoicesPrefs.edit().putString(fichaId, json).apply()
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
        return dev.rpgsolo.rules.distinctExternalBonuses(
            CharacterCreationCatalog.selectedEffects(choices).map {
                dev.rpgsolo.rules.ExternalBonus(
                    scope = it.scope,
                    bonus = it.bonus,
                    source = it.source,
                    effectId = it.effectId
                )
            }
        )
    }

    private val bonuses = CombatBonuses(
        session = { session },
        activePackage = { activePackage },
        activeFicha = { activeFicha },
        playerId = { playerId },
        catalogBonuses = { pkg -> selectedCatalogBonuses(pkg) }
    )

    private fun selectedCatalogBonus(pkg: RulePackage, scope: String): Int = bonuses.selectedCatalogBonus(pkg, scope)

    private fun selectedCatalogBonusSummary(pkg: RulePackage, scopes: Collection<String>): String =
        bonuses.selectedCatalogBonusSummary(pkg, scopes)

    /** Lore canônica selecionada para contexto narrativo; nunca é convertida em regra mecânica. */
    fun characterChoicesForAi(fichaId: String): String {
        val choices = characterChoices(fichaId)
        val selectedIds = listOf(choices.raceId, choices.subraceId, choices.hybridId, choices.cursedClassBranchId, choices.classId, choices.professionId) + choices.additionalClassIds
        val selected = selectedIds.filter { it.isNotBlank() }.distinct().mapNotNull(CharacterCreationCatalog::find)
        val ancestryIds = selected.filter { it.id == choices.hybridId }.flatMap { it.hybridRaceIds }
        val ancestry = ancestryIds.mapNotNull(CharacterCreationCatalog::find)
        val entries = (selected + ancestry).distinctBy { it.id }
        val orderedClasses = (listOf(choices.classId).filter { it.isNotBlank() } + choices.additionalClassIds).distinct()
        val appearance = when (orderedClasses.size) {
            0, 1 -> "aparência normal para sua raça"
            2 -> "traços híbridos visíveis; possíveis reações sociais negativas"
            3 -> "aparência monstruosa evidente; desvantagem social significativa"
            else -> "aparência extremamente monstruosa; forte desvantagem social"
        }
        val affinityName = mapOf(
            "WATER" to "Água", "FIRE" to "Fogo", "WIND" to "Vento", "EARTH" to "Terra",
            "METAL" to "Metal", "YIN" to "Yin", "YANG" to "Yang"
        )[choices.elementalAffinity] ?: "não definida"
        val unlockedSkills = runCatching {
            skillTreeActions().view().filter { it.currentLevel > 0 }
                .map { "${it.displayName} nível ${it.currentLevel}" }
        }.getOrDefault(emptyList())
        val activeCurses = runCatching {
            val definitions = curseActions().all().associateBy { it.id }
            session.state.entities.values.flatMap { entity ->
                entity.appliedCurses.values.mapNotNull { applied ->
                    definitions[applied.curseId]?.let { curse ->
                        "${entity.name}: ${curse.name} (${curse.effects.joinToString { effect -> "${effect.target} ${effect.baseValue}" }})"
                    }
                }
            }
        }.getOrDefault(emptyList())
        return buildString {
            append("INSTRUÇÕES DE INTEGRIDADE: este bloco contém escolhas e descrições editoriais, não regras mecânicas. ")
            append("Não conceda bônus, penalidades, poderes, resistências ou alterações de atributos a partir dele. ")
            append("Use apenas como contexto narrativo; os efeitos mecânicos válidos vêm da ficha, do catálogo de habilidades e do pacote de regras. ")
            append("Não invente dano, custo, duração ou bônus; use o motor para resolver ações.\n")
            append("Classe principal: ").append(CharacterCreationCatalog.find(choices.classId)?.name ?: "não definida").append(". ")
            append("Classes adicionais, nesta ordem: ").append(choices.additionalClassIds.mapNotNull { CharacterCreationCatalog.find(it) }.joinToString(" + ") { it.name }.ifBlank { "nenhuma" }).append(". ")
            append("Aparência híbrida: ").append(appearance).append(". ")
            append("Afinidade elemental: ").append(affinityName).append(". Habilidades mágicas devem respeitar essa afinidade. ")
            append("Despertar das classes: principal nível 1, segunda nível 10, terceira nível 20, quarta nível 30.\n")
            append("Habilidades de árvore desbloqueadas: ").append(unlockedSkills.ifEmpty { listOf("nenhuma") }.joinToString("; ")).append(".\n")
            append("Maldições atualmente aplicadas: ").append(activeCurses.ifEmpty { listOf("nenhuma") }.joinToString("; ")).append(".\n")
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

    @JvmName("persistFontScale")
    fun setFontScale(value: Float) {
        if (!value.isFinite()) return
        fontScale = value.coerceIn(0.85f, 1.4f)
        narrationPrefs.edit().putFloat("font_scale", fontScale).apply()
    }

    @JvmName("persistChapterEventLimit")
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

    /** ID de rascunho sem acesso ao banco (seguro na composição): UUID aleatório conferido só contra a memória. */
    fun draftFichaId(): String {
        var candidate: String
        do {
            candidate = "f-${java.util.UUID.randomUUID()}"
        } while ((fichas + archivedFichas).any { it.id == candidate })
        return candidate
    }

    /** ID novo e resistente a colisões entre fichas ativas, arquivadas e importadas. */
    fun newFichaId(): String {
        var candidate: String
        do {
            candidate = "f-${java.util.UUID.randomUUID()}"
        } while ((fichas + archivedFichas).any { it.id == candidate } || store.fichaExiste(candidate))
        return candidate
    }

    /** Biblioteca de fichas reutilizáveis, persistida separadamente das campanhas. */
    val fichas = mutableStateListOf<Ficha>()
    val archivedFichas = mutableStateListOf<Ficha>()
    val campaigns = mutableStateListOf<CampaignRecord>()
    /** CP-02: estado, datas e tamanho do log de cada campanha, para a lista e o histórico. */
    val campaignInfos = mutableStateListOf<dev.rpgsolo.data.CampanhaInfo>()
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
    /** CP-01.3: motivo da última gravação que falhou (log ou metadados); `null` = tudo confirmado. */
    var saveError by mutableStateOf<String?>(null)
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
    /** Definida na inicialização assíncrona (R-02); as telas só são desenhadas depois de [ready]. */
    private lateinit var session: CampaignSession

    /** false enquanto o banco e a campanha ativa são restaurados fora da thread principal. */
    var ready by mutableStateOf(false)
        private set
    var startupError: String? by mutableStateOf(null)
        private set
    private val startupScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO)

    override fun onCleared() {
        startupScope.cancel()
        super.onCleared()
    }

    /** null enquanto não houver provedor configurado. */
    var controller: PlayController? by mutableStateOf(null)
        private set

    /** Roda uma ação de combate/inventário/lugar com exclusividade de turno (R-01). Chamar fora da thread principal. */
    fun <T> exclusiveTurn(block: () -> T): T = controller?.exclusiveTurn(block) ?: block()

    var configError: String? by mutableStateOf(null)
        private set


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
        campaignInfos.clear()
        campaignInfos.addAll(store.infos())
    }

    private fun restoreFichas() {
        fichas.clear()
        archivedFichas.clear()

        fun decodeAndMigrate(row: FichaRow): Ficha? = runCatching {
            val decoded = dev.rpgsolo.engine.Codec.decodeFicha(row.json)
            val legacy = characterChoicesPrefs.getString(decoded.id, null).orEmpty()
            if (decoded.editorialChoicesJson.isNotBlank() || legacy.isBlank()) {
                decoded
            } else {
                val migrated = decoded.copy(editorialChoicesJson = legacy)
                // RoomEventStore atualiza o JSON sem alterar o sinalizador de arquivamento.
                store.saveFicha(FichaRow(migrated.id, migrated.name, dev.rpgsolo.engine.Codec.encode(migrated)))
                migrated
            }
        }.getOrNull()

        fichas.addAll(store.fichas().mapNotNull(::decodeAndMigrate))
        archivedFichas.addAll(store.fichasArquivadas().mapNotNull(::decodeAndMigrate))
    }

    /** Salva uma ficha na biblioteca e atualiza a cópia observável em memória. */
    fun saveFicha(ficha: Ficha) {
        // Migração sob demanda: qualquer gravação de uma ficha antiga incorpora as escolhas
        // legadas antes de persistir, sem apagar o registro de origem.
        val legacyChoices = characterChoicesPrefs.getString(ficha.id, null)
        val canonical = ficha.editorialChoicesJson.ifBlank { legacyChoices.orEmpty() }
        val persisted = if (canonical == ficha.editorialChoicesJson) ficha else ficha.copy(editorialChoicesJson = canonical)
        store.saveFicha(FichaRow(persisted.id, persisted.name, dev.rpgsolo.engine.Codec.encode(persisted)))
        val archivedIndex = archivedFichas.indexOfFirst { it.id == persisted.id }
        if (archivedIndex >= 0) {
            archivedFichas[archivedIndex] = persisted
            fichas.removeAll { it.id == persisted.id }
        } else {
            val index = fichas.indexOfFirst { it.id == persisted.id }
            if (index >= 0) fichas[index] = persisted else fichas.add(persisted)
        }
        if (activeFichaId == persisted.id) activeFicha = persisted
    }

    fun duplicateFicha(id: String): String? = try {
        val original = requireNotNull(fichas.firstOrNull { it.id == id }) { "ficha não encontrada na biblioteca" }
        val copy = original.copy(
            id = newFichaId(),
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
        require(decoded.id.isNotBlank()) { "a ficha importada precisa ter um ID" }
        val exists = store.fichaExiste(decoded.id) || (fichas + archivedFichas).any { it.id == decoded.id }
        val importedId = if (exists) newFichaId() else decoded.id
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
        val choiceWarnings = if (importedChoices != CharacterChoices()) CharacterCreationCatalog.validateChoices(importedChoices) else emptyList()
        buildString {
            append("Ficha ").append(imported.name).append(" importada")
            if (importedChoices != CharacterChoices()) append(" com escolhas de raça/linhagem, classe, profissão e maldições")
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

    private val combatActions = CombatActions(
        session = { session },
        activePackage = { activePackage },
        playerId = { playerId },
        pendingCombatNpcIds = pendingCombatNpcIds,
        savePending = { savePendingCombatNpcIds() },
        onChanged = { campaignVersion++ },
        bonuses = bonuses
    )

    /** Inicia a iniciativa do jogador e dos NPCs preparados para este encontro; os números vêm do motor. */
    fun startCombat(): String? = combatActions.startCombat()

    /** Permite passar o turno apenas ao jogador; turnos de NPC são resolvidos pela ação mecânica correspondente. */
    fun nextCombatTurn(): String? = combatActions.nextCombatTurn()

    /** Resolve o ataque do inimigo atual e só então avança o turno; nenhum número vem da IA. */
    fun resolveNpcTurn(): String? = combatActions.resolveNpcTurn()

    fun endCombat(): String? = combatActions.endCombat()

    fun combatState(): dev.rpgsolo.engine.CombatState? = combatActions.combatState()

    fun combatantName(id: String): String = combatActions.combatantName(id)

    /** Ordem de iniciativa em palavras; nunca expõe atributos ou recursos dos NPCs. */
    fun combatOrderDescription(): String? = combatActions.combatOrderDescription()

    fun combatTargets(): List<CombatTarget> = combatActions.combatTargets()

    fun isPlayerCombatTurn(): Boolean = combatActions.isPlayerCombatTurn()

    fun canDefend(): Boolean = combatActions.canDefend()

    private fun awardCombatVictoryXp(combatantIds: Collection<String>): Int = combatActions.awardCombatVictoryXp(combatantIds)

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

    /** A viagem também faz o tempo passar: relógios com `moveTick` avançam pelo motor, no mesmo lote do deslocamento. */
    fun moveToPlace(id: String): String? = when (
        val result = session.executeAll(
            listOf<dev.rpgsolo.engine.Command>(dev.rpgsolo.engine.MoveToPlace(id)) +
                session.state.travelClocks().map {
                    dev.rpgsolo.engine.AdvanceClock(it.id, minOf(it.moveTick, it.segments - it.filled))
                }
        )
    ) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    private fun approvedRuleBonus(scope: String): Int = bonuses.approvedRuleBonus(scope)

    private fun approvedRuleEffects(scopes: Collection<String>): List<dev.rpgsolo.rules.ExternalBonus> =
        bonuses.approvedRuleEffects(scopes)

    private fun approvedRuleBonusSummary(scopes: Collection<String>): String = bonuses.approvedRuleBonusSummary(scopes)

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

    private fun conditionDiceShift(entityId: String, stat: String): Int = bonuses.conditionDiceShift(entityId, stat)

    private fun combatTraitIds(entityId: String): List<String> = bonuses.combatTraitIds(entityId)

    /** Poderes do pacote cujo traço está na ficha ativa; sem pacote ou sem traços, lista vazia. */
    fun powersView(): List<PowerView> {
        val pkg = activePackage ?: return emptyList()
        return dev.rpgsolo.ai.PowerMenu(pkg).available(combatTraitIds(playerId)).map { def ->
            PowerView(
                id = def.id, name = def.name, description = def.description,
                resourceName = pkg.data.resources.firstOrNull { it.id == def.resource }?.name ?: def.resource,
                cost = def.cost, needsTarget = def.target == dev.rpgsolo.rules.PowerTarget.ENEMY
            )
        }
    }

    /** Traços da ficha ativa com usos limitados declarados pelo pacote (`usos` em `tracos.txt`). */
    fun traitUsesView(): List<TraitUseView> {
        val pkg = activePackage ?: return emptyList()
        val me = session.state.entities[playerId] ?: return emptyList()
        return me.traits.distinct().mapNotNull { id ->
            val def = pkg.trait(id)?.takeIf { it.uses > 0 } ?: return@mapNotNull null
            TraitUseView(def.id, def.name, def.description, session.state.traitUses[playerId]?.get(id) ?: 0, def.uses)
        }
    }

    /** Gasta um uso do traço pelo motor; limite vem do pacote. Chamar fora da thread principal (via `act`). */
    fun spendTraitUse(traitId: String): String? {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val user = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val def = pkg.trait(traitId)?.takeIf { it.uses > 0 } ?: return "Este traço não tem usos limitados."
        return when (val result = controller?.spendTraitUse(def.id, def.name, def.uses, user.name)
            ?: return "O controlador da campanha não está disponível.") {
            is dev.rpgsolo.ai.PlayResult.Played -> { campaignVersion++; null }
            is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
        }
    }

    /** Condições ativas do herói que o pacote deixa remover por teste: (id, nome). */
    fun removableConditions(): List<Pair<String, String>> {
        val pkg = activePackage ?: return emptyList()
        val me = session.state.entities[playerId]?.takeIf { it.alive } ?: return emptyList()
        return session.state.conditions[playerId].orEmpty().sorted().mapNotNull { id ->
            pkg.conditionRemovalSpec(id, me.stats, me.skills) ?: return@mapNotNull null
            id to (pkg.data.conditions.firstOrNull { it.id == id }?.name ?: id)
        }
    }

    /** Tenta remover a condição por teste, pelo motor. Chamar fora da thread principal (via `act`). */
    fun removeCondition(conditionId: String): String? {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val me = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val spec = pkg.conditionRemovalSpec(conditionId, me.stats, me.skills) ?: return "Esta condição não pode ser removida por teste."
        val name = pkg.data.conditions.firstOrNull { it.id == conditionId }?.name ?: conditionId
        return when (val result = controller?.removeConditionByCheck(conditionId, name, spec, me.name)
            ?: return "O controlador da campanha não está disponível.") {
            is dev.rpgsolo.ai.PlayResult.Played -> { campaignVersion++; null }
            is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
        }
    }

    /** Falhas que matam, se o herói está caído e o pacote declara teste de sobrevivência; senão null. */
    fun survivalLimit(): Int? {
        val pkg = activePackage ?: return null
        val me = session.state.entities[playerId] ?: return null
        if (!me.alive || !me.downed) return null
        pkg.survivalSpec(me.stats, me.skills) ?: return null
        return pkg.survivalFailureLimit
    }

    /** Falhas acumuladas do herói caído. */
    fun survivalFailures(): Int = session.state.entities[playerId]?.deathFailures ?: 0

    /** Teste de sobrevivência do herói caído, pelo motor. Chamar fora da thread principal (via `act`). */
    fun survivalTest(): String? {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val me = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val spec = pkg.survivalSpec(me.stats, me.skills) ?: return "O pacote não declara teste de sobrevivência."
        return when (val result = controller?.survivalTest(spec, pkg.survivalFailureLimit, me.name)
            ?: return "O controlador da campanha não está disponível.") {
            is dev.rpgsolo.ai.PlayResult.Played -> { campaignVersion++; null }
            is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
        }
    }

    /** Nome de exibição de um recurso do pacote ativo; o próprio id se não houver. */
    fun resourceName(id: String): String = activePackage?.data?.resources?.firstOrNull { it.id == id }?.name ?: id

    /** NPCs vivos que podem ser alvo de um poder ofensivo. */
    fun powerTargets(): List<CombatTarget> = session.state.entities.values
        .filter { it.kind == EntityKind.NPC && it.alive }
        .map { CombatTarget(it.id, it.name) }

    /**
     * Ativa um poder pelo motor. ASSUMINDO: usar um poder não consome o turno de combate; custo, teste e efeito
     * vêm do pacote e são resolvidos pelo motor. Chamar fora da thread principal (via `act`).
     */
    fun activatePower(powerId: String, targetId: String?): String? {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val user = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val menu = dev.rpgsolo.ai.PowerMenu(pkg)
        return when (val built = menu.build(
            powerId, playerId, targetId, session.state, combatTraitIds(playerId),
            targetId?.let(::combatTraitIds).orEmpty()
        )) {
            is dev.rpgsolo.ai.PowerBuild.Invalid -> built.reason
            is dev.rpgsolo.ai.PowerBuild.Ready -> {
                val targetName = built.spec.targetId?.let { session.state.entities[it]?.name }
                val spec = when (val effect = built.spec.effect) {
                    is dev.rpgsolo.engine.PowerEffect.Cure -> {
                        val receiverId = built.spec.targetId ?: built.spec.userId
                        val recovery = bonuses.skillTreeAndActiveBonus(receiverId, "recuperacao")
                        built.spec.copy(effect = effect.copy(amount = (effect.amount + recovery).coerceAtLeast(0)))
                    }
                    else -> built.spec
                }
                when (val result = controller?.activatePower(spec, built.def.name, user.name, targetName)
                    ?: return "O controlador da campanha não está disponível.") {
                    is dev.rpgsolo.ai.PlayResult.Played -> { campaignVersion++; null }
                    is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
                }
            }
        }
    }

    private val inventory = InventoryActions(
        session = { session },
        activePackage = { activePackage },
        playerId = { playerId },
        isPlayerCombatTurn = { isPlayerCombatTurn() },
        onChanged = { campaignVersion++ }
    )

    fun inventoryView(): List<InventoryView> = inventory.inventoryView()

    /** Usa consumíveis apenas quando o pacote declara uma ação mecânica explícita. */
    fun useItem(itemId: String): String? = inventory.useItem(itemId)

    fun equipItem(itemId: String): String? = inventory.equipItem(itemId)

    fun shopView(): ShopView? = inventory.shopView()

    fun buyItem(itemId: String, qty: Int = 1): String? = inventory.buyItem(itemId, qty)

    fun sellItem(itemId: String, qty: Int = 1): String? = inventory.sellItem(itemId, qty)

    fun spellShopView(): List<SpellShopEntry> = inventory.spellShopView()

    fun buySpell(spellId: String): String? = inventory.buySpell(spellId)

    fun convertCoins(toGold: Boolean, goldQty: Int = 1): String? = inventory.convertCoins(toGold, goldQty)

    fun unequipItem(slot: String): String? = inventory.unequipItem(slot)

    /** Ataque local: atributos e bônus vêm do pacote e do equipamento; a IA não escolhe números. */
    fun attack(targetId: String): String? = combatActions.attack(targetId)

    /** A postura defensiva concede o bônus do pacote contra o próximo ataque recebido e consome o turno. */
    fun defend(): String? = combatActions.defend()

    /** Fuga usa um teste real do pacote; sucesso encerra o combate, falha cede a próxima ação ao adversário. */
    fun fleeCombat(): String? = combatActions.fleeCombat()

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
            fichas = (fichas + archivedFichas).map { dev.rpgsolo.engine.Codec.encode(it) },
            characterChoices = (fichas + archivedFichas).associate { ficha ->
                ficha.id to characterChoices(ficha.id).toJson()
            }.filterValues { raw ->
                CharacterChoices.fromJson(raw) != CharacterChoices()
            },
            archivedFichaIds = archivedFichas.map { it.id }
        )
    )

    fun importBackup(text: String): String? = try {
        val raw = text.trim()
        val file = dev.rpgsolo.engine.Backup.decode(raw)
        val meta = file.metadata
        // Validar todas as fichas antes de criar a campanha: não ignorar JSONs inválidos
        // nem deixar um backup parcialmente restaurado por causa de uma ficha corrompida.
        val decodedFichas = meta?.fichas.orEmpty().map { rawFicha ->
            dev.rpgsolo.engine.Codec.decodeFicha(rawFicha)
        }
        require(decodedFichas.all { it.id.isNotBlank() }) {
            "backup contém ficha sem ID"
        }
        require(decodedFichas.map { it.id }.distinct().size == decodedFichas.size) {
            "backup contém IDs de ficha duplicados"
        }

        // Nunca sobrescrever uma ficha local só porque o backup reutiliza o mesmo ID.
        val localFichaIds = (fichas + archivedFichas).map { it.id }.toMutableSet()
        val usedIds = localFichaIds.toMutableSet()
        val fichaIdMap = linkedMapOf<String, String>()
        decodedFichas.forEach { ficha ->
            var mappedId = ficha.id
            if (mappedId in usedIds || store.fichaExiste(mappedId)) {
                do { mappedId = newFichaId() } while (mappedId in usedIds)
            }
            usedIds += mappedId
            fichaIdMap[ficha.id] = mappedId
        }

        var importedCampaignId = ""
        store.inTransaction {
            importedCampaignId = dev.rpgsolo.engine.Backup.import(
                store, raw, "import-${java.util.UUID.randomUUID()}"
            )
            if (meta != null) {
                store.saveMetadata(
                    CampanhaMetaRow(
                        campanhaId = importedCampaignId,
                        chaptersJson = meta.chaptersJson,
                        questsJson = meta.questsJson,
                        timelineJson = meta.timelineJson,
                        playerId = meta.playerId,
                        fichaId = meta.fichaId?.let { fichaId ->
                            fichaIdMap[fichaId] ?: if (store.fichaExiste(fichaId)) null else fichaId
                        },
                        packageId = meta.packageId,
                        systemId = meta.systemId,
                        editionId = meta.editionId
                    )
                )
                runCatching { dev.rpgsolo.engine.Codec.decodeChapters(meta.chaptersJson) }.getOrNull()
                    ?.let { store.saveChapters(importedCampaignId, it) }
                runCatching { dev.rpgsolo.engine.Codec.decodeQuests(meta.questsJson) }.getOrNull()
                    ?.let { store.saveQuests(importedCampaignId, it) }
                runCatching { dev.rpgsolo.engine.Codec.decodeTimeline(meta.timelineJson) }.getOrNull()
                    ?.let { store.saveTimeline(importedCampaignId, it) }
                decodedFichas.forEach { decoded ->
                    val mappedId = fichaIdMap.getValue(decoded.id)
                    val legacyChoices = meta.characterChoices[decoded.id].orEmpty()
                    val canonicalChoices = decoded.editorialChoicesJson.ifBlank { legacyChoices }
                    val ficha = decoded.copy(id = mappedId, editorialChoicesJson = canonicalChoices)
                    val archived = decoded.id in meta.archivedFichaIds
                    store.saveFicha(FichaRow(ficha.id, ficha.name, dev.rpgsolo.engine.Codec.encode(ficha), arquivada = archived))
                }
            }
        }

        // Preferências legadas só são atualizadas após o commit do banco; a ficha canônica
        // já contém as escolhas, e repetir a restauração não sobrescreve fichas locais.
        if (meta != null) {
            decodedFichas.forEach { decoded ->
                val mappedId = fichaIdMap.getValue(decoded.id)
                val choices = decoded.editorialChoicesJson.ifBlank { meta.characterChoices[decoded.id].orEmpty() }
                if (choices.isNotBlank()) characterChoicesPrefs.edit().putString(mappedId, choices).apply()
            }
            // Backups antigos podem ter escolhas sem a ficha serializada. Preservá-las como
            // legado apenas se o ID não pertence a uma ficha local, evitando sobrescrever dados.
            meta.characterChoices.forEach { (legacyId, rawChoices) ->
                if (legacyId.isNotBlank() &&
                    legacyId !in fichaIdMap.keys &&
                    legacyId !in localFichaIds &&
                    rawChoices.isNotBlank()
                ) {
                    characterChoicesPrefs.edit().putString(legacyId, rawChoices).apply()
                }
            }
        }
        restoreFichas()
        switchCampaign(importedCampaignId)
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }
      catch (e: Exception) { "falha ao restaurar backup: ${e.message ?: "erro inesperado"}" }

    private fun restoreMetadata() {
        chapters = ChapterBook(chapterEventLimit)
        quests = QuestLog()
        val row = store.metadata(session.campaignId)
        if (row != null) {
            // M-08: as tabelas dedicadas são a fonte; o JSON legado só vale enquanto elas estiverem vazias.
            runCatching {
                val tableChapters = store.chapters(session.campaignId)
                val tableQuests = store.quests(session.campaignId)
                val tableTimeline = store.timeline(session.campaignId)
                chapters.restore(tableChapters.ifEmpty { dev.rpgsolo.engine.Codec.decodeChapters(row.chaptersJson) })
                quests.restore(
                    tableQuests.ifEmpty { dev.rpgsolo.engine.Codec.decodeQuests(row.questsJson) },
                    tableTimeline.ifEmpty { dev.rpgsolo.engine.Codec.decodeTimeline(row.timelineJson) }
                )
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

    /** Grava metadados numa transação; falha fica em [saveError] (nunca mostra "salvo" sem confirmação) e não derruba a jogada. */
    private fun saveMetadata() {
        saveError = try {
            writeMetadata()
            store.touch(session.campaignId)
            session.saveError
        } catch (e: Exception) {
            e.message ?: e::class.simpleName ?: "falha ao gravar"
        }
    }

    /** Tenta gravar de novo log e metadados pendentes. Devolve `null` se confirmou ou o motivo da falha. */
    fun retrySave(): String? {
        val logFailure = session.recover()
        saveMetadata()
        return logFailure ?: saveError
    }

    private fun writeMetadata() = store.inTransaction {
        store.saveChapters(session.campaignId, chapters.chapters)
        store.saveQuests(session.campaignId, quests.all())
        store.saveTimeline(session.campaignId, quests.timeline())
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

    /** CP-04.1: conteúdo do "Livro da aventura" da campanha aberta (só leitura; sem segredos). */
    fun adventureBook(): dev.rpgsolo.engine.AdventureBook = dev.rpgsolo.engine.AdventureBookBuilder.build(
        campaigns.firstOrNull { it.id == session.campaignId }?.name ?: "Aventura",
        session.state.entities[playerId]?.name,
        chapters.chapters,
        quests.all(),
        quests.timeline()
    )

    /** CP-04.2: "Arquivo completo" da campanha aberta, só leitura e sem segredos. */
    fun campaignArchive(): dev.rpgsolo.engine.AdventureBook = dev.rpgsolo.engine.CampaignArchiveBuilder.build(
        adventureBook(),
        session.state,
        playerId,
        itemName = { id -> activePackage?.item(id)?.name ?: id }
    )

    fun campaignArchivePdf(): ByteArray = BookPdf.render(campaignArchive())

    /** CP-04.1: bytes do PDF do livro da aventura. Salvar e compartilhar entram no CP-04.3. */
    fun adventureBookPdf(): ByteArray = BookPdf.render(adventureBook())

    /** CP-02: pausar, concluir ou reativar qualquer campanha da lista. */
    fun setCampaignState(campaignId: String, state: dev.rpgsolo.data.EstadoCampanha): String? =
        if (store.setState(campaignId, state)) { refreshCampaigns(); campaignVersion++; null } else "campanha não encontrada"

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
        require(endpoint.trim().startsWith("https://")) {
            "o endpoint deve começar com https:// (http:// não é aceito: a chave trafegaria sem criptografia)"
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
            powerMenu = activePackage?.let { dev.rpgsolo.ai.PowerMenu(it) },
            playerTraits = { activeFicha?.traits.orEmpty() },
            additionalBonus = { scope -> approvedRuleBonus(scope) },
            externalBonuses = { scopes ->
                bonuses.externalSkillAndCurseBonuses(playerId, scopes.toSet())
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
            resumeBriefing = { dev.rpgsolo.ai.ResumeBriefing.build(quests.all(), quests.timeline(), chapters.summaries()) },
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
        val loadoutChoices = characterChoices(activeFichaId ?: activeFicha?.id.orEmpty())
        val loadoutClassOrder = (listOf(loadoutChoices.classId).filter { it.isNotBlank() } + loadoutChoices.additionalClassIds).distinct()
        val loadoutTreeIds = (listOf(
            loadoutChoices.raceId, loadoutChoices.subraceId, loadoutChoices.hybridId,
            loadoutChoices.classId, loadoutChoices.professionId, loadoutChoices.cursedClassBranchId
        ) + loadoutChoices.additionalClassIds).filter { it.isNotBlank() }.toSet()
        when (val loadoutResult = session.execute(SetSkillTreeLoadout(
            playerId, loadoutTreeIds, loadoutClassOrder, loadoutChoices.elementalAffinity
        ))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            is dev.rpgsolo.engine.ExecResult.Rejected -> configError = "loadout de habilidades inválido: ${loadoutResult.reason}"
        }
        runCatching { skillTreeActions().grantSelectedFreeBaseSkills() }
            .onFailure { configError = "falha ao conceder habilidades básicas: ${it.message}" }
            .getOrNull()?.let { configError = "habilidades básicas não concedidas: $it" }
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
            is ChapterSummarizer.Result.Closed -> {
                // M-07: encerrar um capítulo faz o tempo passar; relógios com chapterTick avançam pelo motor.
                val advances = session.state.chapterClocks().map {
                    dev.rpgsolo.engine.AdvanceClock(it.id, minOf(it.chapterTick, it.segments - it.filled))
                }
                if (advances.isNotEmpty()) session.executeAll(advances)
                saveMetadata(); campaignVersion++; null
            }
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

    /** MU-07.2: slots de magia do herói; null se o pacote não declara níveis de slot. */
    fun spellSlotsView(): SpellSlots? =
        session.engine.config.takeIf { it.spellSlotLevels.isNotEmpty() }?.let { session.engine.spellSlots(playerId) }

    /** MU-05.3: pontos de status e habilidade do herói; atributos do pacote ativo, sem teto (max = 0 indica ilimitado). */
    fun hasActiveCombat(): Boolean = session.state.combat != null

    fun statPointsState(): StatPointsState {
        val player = session.state.entities[playerId] ?: return StatPointsState(0, 0, emptyList())
        val pkg = activePackage ?: return StatPointsState(player.statPoints, player.abilityPoints, emptyList())
        val stats = pkg.data.stats.filter { it.id in player.stats }.map { stat ->
            AdvancementChoice(stat.id, stat.name, player.stats[stat.id] ?: stat.default, 0, skill = false)
        }
        return StatPointsState(player.statPoints, player.abilityPoints, stats)
    }

    /** Ações do catálogo de maldições lançadas durante a aventura. */
    fun curseActions(): CurseActions = CurseActions(
        session = { session },
        activePackage = { activePackage },
        playerId = { playerId },
        checkBonuses = { entityId, scopes -> bonuses.externalSkillAndCurseBonuses(entityId, scopes) },
        onChanged = { campaignVersion++ }
    )

    /** Estado da distribuição única de 12 pontos iniciais. */
    fun initialBuildState(): InitialBuildState {
        val player = session.state.entities[playerId] ?: return InitialBuildState(false, emptyList())
        val stats = activePackage?.data?.stats.orEmpty().filter { it.id in player.stats }.map { stat ->
            AdvancementChoice(stat.id, stat.name, player.stats[stat.id] ?: stat.default, 0, skill = false)
        }
        return InitialBuildState(player.initialBuildAllocated, stats)
    }

    /** O motor exige exatamente 12 pontos e impede repetir a distribuição inicial. */
    fun allocateInitialBuild(statIncreases: Map<String, Int>, skillPoints: Int): String? =
        when (val result = session.execute(dev.rpgsolo.engine.AllocateInitialBuild(playerId, statIncreases, skillPoints))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    /** Ações da árvore vinculadas às escolhas salvas na ficha ativa. */
    fun skillTreeActions(): SkillTreeActions {
        val choices = characterChoices(activeFichaId ?: activeFicha?.id.orEmpty())
        val orderedClasses = (listOf(choices.classId).filter { it.isNotBlank() } + choices.additionalClassIds).distinct()
        val selected = (listOf(
            choices.raceId, choices.subraceId, choices.classId, choices.professionId, choices.hybridId, choices.cursedClassBranchId
        ) + choices.additionalClassIds).filter { it.isNotBlank() }.toSet()
        val affinity = runCatching {
            if (choices.elementalAffinity.isBlank()) dev.rpgsolo.engine.ElementPath.NONE
            else dev.rpgsolo.engine.ElementPath.valueOf(choices.elementalAffinity)
        }.getOrDefault(dev.rpgsolo.engine.ElementPath.NONE)
        val equippedIds = session.state.equipped[playerId].orEmpty().values
        val weaponTags = equippedIds.flatMap { id -> listOf(id, id.substringBefore('-')) }.toSet()
        return SkillTreeActions(
            session = { session },
            activePackage = { activePackage },
            playerId = { playerId },
            selectedTreeIds = {
                session.state.entities[playerId]?.skillTreeTreeIds?.takeIf { it.isNotEmpty() } ?: selected
            },
            classTreeUnlockLevel = { treeId ->
                val classOrder = session.state.entities[playerId]?.skillTreeClassOrder?.takeIf { it.isNotEmpty() } ?: orderedClasses
                when (classOrder.indexOf(treeId)) {
                    0 -> 1
                    1 -> 10
                    2 -> 20
                    3 -> 30
                    else -> 1
                }
            },
            element = {
                val savedElement = session.state.entities[playerId]?.skillTreeElement.orEmpty()
                if (savedElement.isBlank()) affinity else runCatching { dev.rpgsolo.engine.ElementPath.valueOf(savedElement) }.getOrDefault(affinity)
            },
            weaponTags = { weaponTags },
            questCompleted = { questId -> quests.get(questId)?.status == dev.rpgsolo.engine.QuestStatus.DONE },
            multiHitAction = { targetId, hitCount, damageBonus, elementalScope -> combatActions.attack(targetId, hitCount, damageBonus, elementalScope) },
            onChanged = { campaignVersion++ }
        )
    }

    /** MU-05.3: gasta um ponto de status em um atributo; o motor valida saldo e atributo. */
    fun increaseStatPoint(statId: String): String? =
        when (val result = session.execute(dev.rpgsolo.engine.AllocateStatPoints(playerId, mapOf(statId to 1)))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
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
        val events = session.events
        return events.mapNotNull { event ->
            when (event) {
                is dev.rpgsolo.engine.XpGained -> if (event.entityId == playerId) {
                    val name = session.state.entities[event.entityId]?.name ?: event.entityId
                    "$name recebeu +${event.amount} XP"
                } else null
                is dev.rpgsolo.engine.LevelGained -> if (event.entityId == playerId) {
                    val name = session.state.entities[event.entityId]?.name ?: event.entityId
                    "$name alcançou o nível ${event.level}" +
                        (if (event.pointsGranted > 0) " e recebeu ${event.pointsGranted} ponto(s) de evolução" else "") +
                        (if (event.statPointsGranted > 0) ", ${event.statPointsGranted} de status" else "") +
                        (if (event.abilityPointsGranted > 0) ", ${event.abilityPointsGranted} de habilidade" else "")
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
                is dev.rpgsolo.engine.StatPointsAllocated -> if (event.entityId == playerId) {
                    val names = event.increases.keys.map { id ->
                        activePackage?.data?.stats?.firstOrNull { it.id == id }?.name ?: id
                    }
                    "Você investiu pontos de status em ${names.joinToString(", ")}"
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
            var warning: String? = null
            if (status == dev.rpgsolo.engine.QuestStatus.DONE && (quests.get(questId)?.xpReward ?: 0) > 0) {
                val award = session.awardQuestXp(quests, questId, playerId)
                if (award is dev.rpgsolo.engine.ExecResult.Rejected) warning = award.reason
            }
            saveMetadata()
            campaignVersion++
            warning
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

    private fun startup() {
        session = openOrCreateCampaign()
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

    init {
        // Último membro da classe: todas as propriedades acima já foram inicializadas quando a restauração começa.
        startupScope.launch {
            try {
                startup()
                ready = true
            } catch (e: Exception) {
                startupError = e.message ?: "Falha ao abrir o banco de dados."
            }
        }
    }
}
