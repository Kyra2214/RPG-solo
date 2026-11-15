package dev.rpgsolo.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dev.rpgsolo.ai.AiError
import dev.rpgsolo.ai.AiMessage
import dev.rpgsolo.ai.AiProvider
import dev.rpgsolo.ai.AiRequest
import dev.rpgsolo.ai.AiRoles
import dev.rpgsolo.ai.Author
import dev.rpgsolo.ai.CampaignInterpreter
import dev.rpgsolo.ai.ChapterSummarizer
import dev.rpgsolo.ai.CheckMenu
import dev.rpgsolo.ai.CheckOption
import dev.rpgsolo.ai.GameLoop
import dev.rpgsolo.ai.IntentClassifier
import dev.rpgsolo.ai.ModelDiscovery
import dev.rpgsolo.ai.ModelProbe
import dev.rpgsolo.ai.NewCampaignFlow
import dev.rpgsolo.ai.PlayController
import dev.rpgsolo.ai.PlayerSheet
import dev.rpgsolo.ai.ProviderConfig
import dev.rpgsolo.ai.ProviderFactory
import dev.rpgsolo.ai.ProviderKind
import dev.rpgsolo.ai.ProviderPreset
import dev.rpgsolo.ai.Role
import dev.rpgsolo.ai.SourceContext
import dev.rpgsolo.ai.TurnRunner
import dev.rpgsolo.ai.UsageTracker
import dev.rpgsolo.data.CampanhaMetaRow
import dev.rpgsolo.data.FichaRow
import dev.rpgsolo.data.RoomEventStore
import dev.rpgsolo.data.RoomMundoStore
import dev.rpgsolo.data.RpgDatabase
import dev.rpgsolo.data.SourceDocumentStore
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CanonFacts
import dev.rpgsolo.engine.ChapterBook
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.PipelineState
import dev.rpgsolo.engine.Quest
import dev.rpgsolo.engine.QuestLog
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.SetSkillTreeLoadout
import dev.rpgsolo.engine.SourceEntry
import dev.rpgsolo.engine.SourceIndex
import dev.rpgsolo.engine.SourceLibrary
import dev.rpgsolo.engine.SourceStatus
import dev.rpgsolo.engine.SpellSlots
import dev.rpgsolo.engine.advanceClocksAfterChapterClosure
import dev.rpgsolo.engine.advanceClocksAfterQuestClosure
import dev.rpgsolo.engine.journal
import dev.rpgsolo.rules.CampaignStarter
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.StartResult
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/** Tamanho da cadeia de modelos gratuitos (ASSUMINDO 6; ver PLANO_CORRECOES_POS_TESTE.md) e prazos de renovação. */
/** Cadeia de modelos gratuitos: todos os que respondem texto (decisão do autor em 14/11/2026), sem teto de quantidade nem de sondas. */
private const val FREE_CHAIN_SIZE = Int.MAX_VALUE
private const val MODELS_MAX_AGE_MS = 24L * 60L * 60L * 1000L
private const val MODELS_RETRY_GAP_MS = 10L * 60L * 1000L

data class ConnectionTestResult(val success: Boolean, val message: String)

/** Resultado da busca de modelos gratuitos de um provedor pré-configurado (ids do mais indicado ao menos). */
data class FreeModelsResult(val success: Boolean, val models: List<String>, val message: String)

data class SourceDocumentImportOutcome(
    val entry: SourceEntry?,
    val error: String? = null,
)

data class CombatTarget(
    val id: String,
    val name: String,
    val rulesValidated: Boolean = true,
    val condition: String = "",
)

data class PlaceView(val id: String, val name: String, val description: String, val current: Boolean, val exits: List<Pair<String, String>>)

data class RelationshipView(val npcName: String, val attitude: String)

data class EquipmentView(val id: String, val name: String, val quantity: Int, val slot: String, val equipped: Boolean)

/** N-20/N-21: receita liberada ao herói, com entradas e saídas já em texto para a tela. */
data class RecipeView(val id: String, val name: String, val inputs: String, val outputs: String)

data class TraitUseView(val id: String, val name: String, val description: String, val used: Int, val limit: Int)

data class PowerView(
    val id: String,
    val name: String,
    val description: String,
    val resourceName: String,
    val cost: Int,
    val needsTarget: Boolean,
)

data class InventoryView(
    val id: String,
    val name: String,
    val quantity: Int,
    val kind: String,
    val slot: String?,
    val equipped: Boolean,
    val description: String,
    val price: Int? = null,
    /** N-13: peso ou tipo de arma fora da classe do herói (bônus positivos pela metade). */
    val restricted: Boolean = false,
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
    val silverPerGold: Int = 0,
)

data class AdvancementChoice(val id: String, val name: String, val current: Int, val max: Int, val skill: Boolean)

data class AdvancementState(val pointsAvailable: Int, val choices: List<AdvancementChoice>)

data class SkillTrialRow(val id: String, val name: String, val value: Int, val max: Int, val description: String = "")

data class SkillTrialState(
    val every: Int,
    val pendingLevels: List<Int>,
    val skills: List<SkillTrialRow>,
)

data class StatPointsState(val statPoints: Int, val abilityPoints: Int, val choices: List<AdvancementChoice>)

data class InitialBuildState(
    val allocated: Boolean,
    val stats: List<AdvancementChoice>,
    /** Pontos de status a distribuir entre os atributos (N-02: 10). */
    val statPoints: Int = 10,
    /** Pontos de habilidade concedidos de uma vez (N-02: 2). */
    val abilityPoints: Int = 2,
    /** Árvore de habilidades: os pontos iniciais já foram concedidos (independente do status). */
    val abilityGranted: Boolean = false,
)

data class SetupStatus(val providerReady: Boolean, val heroReady: Boolean, val campaignReady: Boolean) {
    val complete: Boolean get() = providerReady && heroReady && campaignReady
}

private data class CampaignSourceContext(
    val index: SourceIndex,
    val facts: CanonFacts,
    val sourceIds: Set<String>?,
)

/**
 * Estado do app. O ViewModel sobrevive à recriação da Activity; campanha e log são persistidos no Room.
 * A configuração do provedor é restaurada e a chave fica cifrada pelo Android Keystore.
 */
class AppModel(application: Application) : AndroidViewModel(application) {
    private val sourceDocuments = SourceDocumentStore(File(application.filesDir, "source-documents"))
    val usage = UsageTracker()
    lateinit var content: Content
        private set
    private val secureSettings = SecureSettings(application)
    private var storedSettings: StoredProviderSettings? = secureSettings.load()
    private val narrationPrefs = application.getSharedPreferences("rpg_narration_prefs", 0)

    /** Metadados editoriais da nova criação, separados da ficha mecânica e persistidos por ID. */
    private val characterChoicesPrefs = application.getSharedPreferences("rpg_character_choices", 0)

    /**
     * Salva a escolha editorial dentro da ficha canônica e mantém o espelho legado
     * por compatibilidade com backups/metadados de versões antigas.
     */
    fun saveCharacterChoices(
        fichaId: String,
        choices: CharacterChoices,
    ) {
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
                    effectId = it.effectId,
                )
            },
        )
    }

    private val bonuses =
        CombatBonuses(
            session = { session },
            activePackage = { activePackage },
            activeFicha = { activeFicha },
            playerId = { playerId },
            catalogBonuses = { pkg -> selectedCatalogBonuses(pkg) },
            wearerClassIds = { heroClassIds() },
        )

    /** N-13: classes do herói ativo (ordem salva na entidade, ou as escolhas da ficha) para peso e tipo de equipamento. */
    private fun heroClassIds(): List<String> {
        val saved = session.state.entities[playerId]?.skillTreeClassOrder.orEmpty()
        if (saved.isNotEmpty()) return saved
        val choices = characterChoices(activeFichaId ?: activeFicha?.id.orEmpty())
        return (listOf(choices.classId) + choices.additionalClassIds).filter { it.isNotBlank() }.distinct()
    }

    private fun selectedCatalogBonus(
        pkg: RulePackage,
        scope: String,
    ): Int = bonuses.selectedCatalogBonus(pkg, scope)

    private fun selectedCatalogBonusSummary(
        pkg: RulePackage,
        scopes: Collection<String>,
    ): String = bonuses.selectedCatalogBonusSummary(pkg, scopes)

    /** Lore canônica selecionada para contexto narrativo; nunca é convertida em regra mecânica. */
    fun characterChoicesForAi(fichaId: String): String {
        val editorialChoices = characterChoices(fichaId)
        val storedLoadout = runCatching { session.state.entities[playerId] }.getOrNull()
        val loadoutIds = storedLoadout?.skillTreeTreeIds.orEmpty()
        val classOrder = storedLoadout?.skillTreeClassOrder.orEmpty()
        val choices =
            if (loadoutIds.isEmpty()) {
                editorialChoices
            } else {
                editorialChoices.copy(
                    raceId =
                        if (editorialChoices.mixedRaceIds.isNotEmpty()) {
                            ""
                        } else {
                            loadoutIds.firstOrNull { id -> CharacterCreationCatalog.races.any { it.id == id } }.orEmpty()
                        },
                    subraceId = loadoutIds.firstOrNull { id -> CharacterCreationCatalog.subraces.any { it.id == id } }.orEmpty(),
                    classId = classOrder.firstOrNull().orEmpty(),
                    professionId = loadoutIds.firstOrNull { id -> CharacterCreationCatalog.professions.any { it.id == id } }.orEmpty(),
                    hybridId = loadoutIds.firstOrNull { id -> CharacterCreationCatalog.hybrids.any { it.id == id } }.orEmpty(),
                    cursedClassBranchId =
                        loadoutIds.firstOrNull {
                                id ->
                            CharacterCreationCatalog.cursedClassBranches.any { it.id == id }
                        }.orEmpty(),
                    additionalClassIds = classOrder.drop(1),
                    elementalAffinity = storedLoadout?.skillTreeElement.orEmpty(),
                )
            }
        val selectedIds = listOf(choices.raceId, choices.subraceId, choices.hybridId, choices.cursedClassBranchId, choices.classId, choices.professionId) + choices.additionalClassIds + choices.mixedRaceIds
        val selected = selectedIds.filter { it.isNotBlank() }.distinct().mapNotNull(CharacterCreationCatalog::find)
        val ancestryIds = selected.filter { it.id == choices.hybridId }.flatMap { it.hybridRaceIds }
        val ancestry = ancestryIds.mapNotNull(CharacterCreationCatalog::find)
        val entries = (selected + ancestry).distinctBy { it.id }
        val orderedClasses = (listOf(choices.classId).filter { it.isNotBlank() } + choices.additionalClassIds).distinct()
        val appearance =
            when (orderedClasses.size) {
                0, 1 -> "aparência normal para sua raça"
                2 -> "traços híbridos visíveis; possíveis reações sociais negativas"
                3 -> "aparência monstruosa evidente; desvantagem social significativa"
                else -> "aparência extremamente monstruosa; forte desvantagem social"
            }
        val affinityName =
            mapOf(
                "WATER" to "Água", "FIRE" to "Fogo", "WIND" to "Vento", "EARTH" to "Terra",
                "METAL" to "Ferro", "YIN" to "Trevas", "YANG" to "Luz",
            )[choices.elementalAffinity] ?: "não definida"
        val unlockedSkills =
            runCatching {
                skillTreeActions().view().filter { it.currentLevel > 0 }
                    .map { "${it.displayName} nível ${it.currentLevel}" }
            }.getOrDefault(emptyList())
        val activeCurses =
            runCatching {
                val definitions = curseActions().all().associateBy { it.id }
                session.state.entities.values.flatMap { entity ->
                    entity.appliedCurses.values.mapNotNull { applied ->
                        definitions[applied.curseId]?.let { curse ->
                            "${entity.name}: ${curse.name} (${curse.effects.joinToString {
                                    effect ->
                                "${effect.target} ${effect.baseValue}"
                            }})"
                        }
                    }
                }
            }.getOrDefault(emptyList())
        return buildString {
            append("INSTRUÇÕES DE INTEGRIDADE: este bloco contém escolhas e descrições editoriais, não regras mecânicas. ")
            append("Não conceda bônus, penalidades, poderes, resistências ou alterações de atributos a partir dele. ")
            append(
                "Use apenas como contexto narrativo; os efeitos mecânicos válidos vêm da ficha, do catálogo de habilidades e do pacote de regras. ",
            )
            append("Não invente dano, custo, duração ou bônus; use o motor para resolver ações.\n")
            append("Classe principal: ").append(CharacterCreationCatalog.find(choices.classId)?.name ?: "não definida").append(". ")
            append("Classes adicionais, nesta ordem: ").append(
                choices.additionalClassIds.mapNotNull {
                    CharacterCreationCatalog.find(it)
                }.joinToString(" + ") { it.name }.ifBlank { "nenhuma" },
            ).append(". ")
            append("Aparência híbrida: ").append(appearance).append(". ")
            append("Afinidade elemental: ").append(affinityName).append(". Habilidades mágicas devem respeitar essa afinidade. ")
            append("Despertar das classes: principal nível 1, segunda nível 10, terceira nível 20, quarta nível 30.\n")
            append(
                "Habilidades de árvore desbloqueadas: ",
            ).append(unlockedSkills.ifEmpty { listOf("nenhuma") }.joinToString("; ")).append(".\n")
            append("Maldições atualmente aplicadas: ").append(activeCurses.ifEmpty { listOf("nenhuma") }.joinToString("; ")).append(".\n")
            choices.origemNarrativa().takeIf { it.isNotBlank() }?.let {
                append("Origem narrativa do herói: ").append(it).append(" (contexto, sem efeito mecânico).\n")
            }
            entries.forEach { entry ->
                append("- ").append(entry.name)
                if (entry.id == choices.hybridId && ancestry.isNotEmpty()) {
                    append(
                        " (híbrido de ",
                    ).append(ancestry.filter { it.id in entry.hybridRaceIds }.joinToString(" + ") { it.name }).append(")")
                }
                if (entry.lore.isNotBlank()) append("\n  Lore: ").append(entry.lore)
                if (entry.summary.isNotBlank()) append("\n  Resumo: ").append(entry.summary)
                if (entry.benefits.isNotEmpty()) {
                    append(
                        "\n  Benefícios descritos (narrativos, não aplicar mecanicamente): ",
                    ).append(entry.benefits.joinToString("; "))
                }
                if (entry.drawbacks.isNotEmpty()) {
                    append(
                        "\n  Custos descritos (narrativos, não aplicar mecanicamente): ",
                    ).append(entry.drawbacks.joinToString("; "))
                }
                if (entry.benefits.isEmpty() && entry.benefit.isNotBlank()) {
                    append(
                        "\n  Benefício descrito no catálogo (não aplicar mecanicamente): ",
                    ).append(entry.benefit)
                }
                if (entry.drawbacks.isEmpty() && entry.drawback.isNotBlank()) {
                    append(
                        "\n  Custo descrito no catálogo (não aplicar mecanicamente): ",
                    ).append(entry.drawback)
                }
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

    /** CG-6.3: tema do app: "sistema" (padrão), "claro" ou "escuro". */
    var themeMode by mutableStateOf(narrationPrefs.getString("theme_mode", "sistema").orEmpty().let { if (it in TEMAS) it else "sistema" })
        private set

    @JvmName("persistThemeMode")
    fun setThemeMode(value: String) {
        if (value !in TEMAS) return
        themeMode = value
        narrationPrefs.edit().putString("theme_mode", value).apply()
    }

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

    fun setContentBoundaries(
        lines: String,
        veils: String,
    ) {
        contentLines = lines.take(4000)
        contentVeils = veils.take(4000)
        narrationPrefs.edit()
            .putString("content_lines", contentLines)
            .putString("content_veils", contentVeils)
            .apply()
        campaignVersion++
    }

    fun setNarrationPreferences(
        length: String,
        style: String,
    ) {
        val validLengths = setOf("curta", "equilibrada", "detalhada")
        val validStyles = setOf("cinematográfica", "literária", "direta")
        if (length !in validLengths || style !in validStyles) return
        narrationLength = length
        narrationStyle = style
        narrationPrefs.edit().putString("length", length).putString("style", style).apply()
        campaignVersion++
    }

    /** Rascunho da criação por abas (CG-4.8); chamar fora da thread principal. Falha de leitura vira "sem rascunho". */
    fun creationDraft(): String? = runCatching { store.creationDraft() }.getOrNull()

    /** Grava (ou, com nulo, apaga) o rascunho da criação por abas; falha não interrompe a criação. */
    fun saveCreationDraft(json: String?) {
        runCatching { if (json == null) store.clearCreationDraft() else store.saveCreationDraft(json) }
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

    /** N-33.2c: aviso do card do Companheiro (subiu de nível); some quando o jogador abre o card. */
    var companionNotice by mutableStateOf<String?>(null)
        private set

    fun dismissCompanionNotice() {
        companionNotice = null
    }

    var sourceLibraryVersion by mutableIntStateOf(0)
        private set
    var canonFactsVersion by mutableIntStateOf(0)
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
    private val worldStore = RoomMundoStore(database)
    private val activeCampaignPrefs = application.getSharedPreferences("rpgsolo_session", Application.MODE_PRIVATE)
    private var playerId = "hero"
    private var activePackage: RulePackage? = null

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

    private val conquistasPrefs = application.getSharedPreferences("rpg_conquistas", 0)
    private val conquistas =
        ConquistasLocais(
            { conquistasPrefs.getStringSet("ids", emptySet()).orEmpty().toSet() },
            { conquistasPrefs.edit().putStringSet("ids", it).apply() },
        )

    /** Conquistas desbloqueadas (observável: muda a cada [registrarConquista] nova). */
    var conquistasVersao by mutableIntStateOf(0)
        private set

    fun conquistasDesbloqueadas(): Set<Conquista> = conquistas.desbloqueadas()

    fun registrarConquista(c: Conquista) {
        if (conquistas.registrar(c)) conquistasVersao++
    }

    /** CG-6.2: modo de jogo por campanha (padrão: História guiada). */
    private val gameModePrefs = application.getSharedPreferences("rpg_game_mode", 0)

    fun gameMode(campaignId: String = session.campaignId): ModoJogo = ModoJogo.deId(gameModePrefs.getString("mode_$campaignId", null))

    fun setGameMode(
        mode: ModoJogo,
        campaignId: String = session.campaignId,
    ) {
        if (campaignId.isNotBlank()) gameModePrefs.edit().putString("mode_$campaignId", mode.id).apply()
    }

    /** Q-09: consentimento por campanha para enviar trechos de fontes ao provedor de IA. */
    private val sourcePrivacyPrefs = application.getSharedPreferences("rpg_source_privacy", 0)

    fun sourcePrivacyConsent(campaignId: String = session.campaignId): Boolean =
        campaignId.isNotBlank() && sourcePrivacyPrefs.getBoolean("consent_$campaignId", false)

    /** Registra ou revoga o consentimento; a revogação remove o contexto documental em memória. */
    fun setSourcePrivacyConsent(
        granted: Boolean,
        campaignId: String = session.campaignId,
    ) {
        if (campaignId.isBlank()) return
        sourcePrivacyPrefs.edit().apply {
            if (granted) putBoolean("consent_$campaignId", true) else remove("consent_$campaignId")
        }.apply()
        if (!granted) clearCampaignSourceContext(campaignId)
        campaignVersion++
        if (granted && campaignId == session.campaignId) {
            startupScope.launch { refreshSourceContext() }
        }
    }

    /** DC-05.1: só mantém índice em memória depois de confirmação explícita de envio à IA remota. */
    private val sourceContextLock = Any()
    private val campaignSourceContexts = mutableMapOf<String, CampaignSourceContext>()

    /**
     * Disponibiliza fontes e fatos de uma campanha ao contexto narrativo. A tela chamadora deve
     * apresentar o aviso de privacidade e passar [privacyConfirmed] como true, ou o consentimento
     * da campanha já deve estar registrado (Q-09); sem nenhum dos dois, a chamada revoga o contexto
     * e impede o envio de trechos à IA.
     */
    fun configureCampaignSourceContext(
        campaignId: String,
        index: SourceIndex,
        facts: CanonFacts = CanonFacts(),
        sourceIds: Set<String>? = null,
        privacyConfirmed: Boolean = false,
    ) {
        if (campaignId.isBlank()) return
        val canConfigure = (privacyConfirmed || sourcePrivacyConsent(campaignId)) && index.size > 0
        val contextFacts =
            if (canConfigure) {
                val persisted = store.canonFacts(campaignId).forCampaign(campaignId)
                CanonFacts((facts.forCampaign(campaignId) + persisted).distinctBy { it.id })
            } else {
                CanonFacts()
            }
        synchronized(sourceContextLock) {
            if (!canConfigure) {
                campaignSourceContexts.remove(campaignId)
            } else {
                campaignSourceContexts[campaignId] = CampaignSourceContext(index, contextFacts, sourceIds?.toSet())
            }
        }
    }

    /** Revoga a autorização de contexto documental para a campanha. */
    fun clearCampaignSourceContext(campaignId: String) {
        synchronized(sourceContextLock) { campaignSourceContexts.remove(campaignId) }
    }

    private fun sourceContextForScene(sceneText: String): String? {
        val campaignId = session.campaignId
        val mode = session.state.sourceMode
        return synchronized(sourceContextLock) {
            val configured = campaignSourceContexts[campaignId] ?: return@synchronized null
            SourceContext.build(
                index = configured.index,
                sceneText = sceneText,
                facts = configured.facts.forCampaign(campaignId),
                sourceIds = configured.sourceIds,
                mode = mode,
            )
        }
    }

    /** Roda uma ação de combate/inventário/lugar com exclusividade de turno (R-01). Chamar fora da thread principal. */
    fun <T> exclusiveTurn(block: () -> T): T = controller?.exclusiveTurn(block) ?: block()

    var configError: String? by mutableStateOf(null)
        private set

    /** Reinstala no registro em memória os pacotes ZIP guardados no armazenamento privado do app. */
    private fun restoreImportedPackages() {
        val dir = File(getApplication<Application>().filesDir, "packages-import")
        val pending =
            dir.listFiles { file -> file.isFile && file.extension.equals("zip", ignoreCase = true) }
                .orEmpty().sortedBy { it.name }.toMutableList()
        // A package may extend another user package. Retry unresolved archives after each successful install.
        while (pending.isNotEmpty()) {
            var installedAny = false
            val iterator = pending.iterator()
            while (iterator.hasNext()) {
                val archive = iterator.next()
                val result =
                    runCatching {
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
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Aventureiro", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                    CreateEntity(Entity("guarda", "Guarda do portão", EntityKind.NPC, resources = mapOf("pv" to Resource(8, 8)))),
                ),
            )
            activeCampaignPrefs.edit().putString("active_campaign", record.id).apply()
        }
    }

    /**
     * MP-08: a IA propõe o mundo próprio a partir da descrição; o motor valida antes de qualquer gravação
     * ([dev.rpgsolo.ai.CustomWorldProposer]). Chamada de rede: chame fora da thread principal.
     */
    fun proposeCustomWorld(description: String): dev.rpgsolo.ai.CustomWorldProposal {
        val provider = roles?.narrator ?: return dev.rpgsolo.ai.CustomWorldProposal.Failed("configure o provedor de IA na aba Config")
        return dev.rpgsolo.ai.CustomWorldProposer(provider).propose(description)
    }

    /** MP-08: instala o mundo próprio validado na campanha recém-criada (sem mapa). Devolve `null` se aceitou ou o motivo da recusa. */
    fun installCustomWorld(draft: dev.rpgsolo.rules.CustomWorldDraft): String? =
        when (val result = dev.rpgsolo.rules.CustomWorld.install(session, draft, session.campaignId)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    /**
     * MP-07: campanhas cujo mundo pode ser continuado. O instantâneo sai do replay do log (sem tabela nova); campanhas
     * sem mapa ficam de fora. Chame fora da thread principal.
     */
    fun previousWorlds(): List<dev.rpgsolo.rules.PreviousWorld> =
        store.campaigns().mapNotNull { rec ->
            runCatching {
                val state = CampaignSession.open(store, rec.id).state
                if (state.places.isEmpty()) {
                    null
                } else {
                    dev.rpgsolo.rules.PreviousWorld(
                        campaignId = rec.id,
                        campaignName = rec.name,
                        chronicle = dev.rpgsolo.engine.WorldChronicle.from(state),
                    )
                }
            }.getOrNull()
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

        fun decodeAndMigrate(row: FichaRow): Ficha? =
            runCatching {
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

    fun duplicateFicha(id: String): String? =
        try {
            val original = requireNotNull(fichas.firstOrNull { it.id == id }) { "ficha não encontrada na biblioteca" }
            val copy =
                original.copy(
                    id = newFichaId(),
                    name = "${original.name} (cópia)",
                    editorialChoicesJson = characterChoices(id).toJson(),
                )
            saveFicha(copy)
            characterChoicesPrefs.edit().putString(copy.id, copy.editorialChoicesJson).apply()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }

    fun archiveFicha(
        id: String,
        archived: Boolean,
    ): String? =
        try {
            val ficha =
                (fichas + archivedFichas).firstOrNull { it.id == id }
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
        } catch (e: IllegalArgumentException) {
            e.message
        }

    private val portraitsDir get() = File(getApplication<Application>().filesDir, "portraits")

    /** I-09.2: arquivo do retrato escolhido pelo jogador (ou null se a referência não aponta para um arquivo existente). */
    fun portraitFile(ref: String): File? =
        ref.takeIf { it.startsWith("portraits/") && !it.contains("..") }
            ?.let { File(getApplication<Application>().filesDir, it) }
            ?.takeIf { it.isFile }

    /** I-09.2: copia a foto escolhida (reduzida a no máximo 512 px, JPEG) para o armazenamento privado e grava só a referência. Executar fora da thread principal. */
    fun setPortrait(
        fichaId: String,
        uri: Uri,
    ): String? {
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == fichaId } ?: return "ficha não encontrada"
        val resolver = getApplication<Application>().contentResolver
        return try {
            val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { android.graphics.BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return "não foi possível ler a imagem"
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= PORTRAIT_MAX_SIDE) sample *= 2
            val options = android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }
            val decoded =
                resolver.openInputStream(uri)?.use { android.graphics.BitmapFactory.decodeStream(it, null, options) }
                    ?: return "não foi possível ler a imagem"
            val scale = PORTRAIT_MAX_SIDE.toFloat() / maxOf(decoded.width, decoded.height)
            val bitmap =
                if (scale < 1f) {
                    android.graphics.Bitmap.createScaledBitmap(
                        decoded,
                        (decoded.width * scale).toInt().coerceAtLeast(1),
                        (decoded.height * scale).toInt().coerceAtLeast(1),
                        true,
                    )
                } else {
                    decoded
                }
            portraitsDir.mkdirs()
            val name = "${ficha.id.filter { it.isLetterOrDigit() || it == '-' || it == '_' }.take(40)}-${System.currentTimeMillis()}.jpg"
            File(portraitsDir, name).outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 88, it) }
            val previous = ficha.portraitRef
            saveFicha(dev.rpgsolo.ai.CharacterNarrativeAssistant.savePortraitReference(ficha, "portraits/$name"))
            deletePortraitFileIfUnused(previous)
            null
        } catch (e: java.io.IOException) {
            "falha ao salvar o retrato"
        } catch (e: SecurityException) {
            "sem permissão para ler a imagem"
        } catch (e: OutOfMemoryError) {
            "imagem grande demais"
        }
    }

    fun clearPortrait(fichaId: String): String? {
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == fichaId } ?: return "ficha não encontrada"
        val previous = ficha.portraitRef
        saveFicha(ficha.copy(portraitRef = ""))
        deletePortraitFileIfUnused(previous)
        return null
    }

    /** Fichas duplicadas compartilham o arquivo: ele só some quando nenhuma ficha o referencia. */
    private fun deletePortraitFileIfUnused(ref: String) {
        if (ref.isBlank() || (fichas + archivedFichas).any { it.portraitRef == ref }) return
        portraitFile(ref)?.delete()
    }

    fun deleteFicha(id: String): String? =
        try {
            val portrait = (fichas + archivedFichas).firstOrNull { it.id == id }?.portraitRef.orEmpty()
            require(store.apagarFicha(id)) { "ficha não encontrada na biblioteca" }
            characterChoicesPrefs.edit().remove(id).apply()
            fichas.removeAll { it.id == id }
            archivedFichas.removeAll { it.id == id }
            deletePortraitFileIfUnused(portrait)
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }

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

    /** Rascunho de história (I-09.1): [text] ou [error]. Nada é gravado até [approveLore]. */
    data class LoreOutcome(val text: String? = null, val error: String? = null)

    /**
     * Pede à IA uma história opcional só com as escolhas já confirmadas da ficha, aparência e ideia do jogador.
     * Chamada de rede: rode fora da thread principal. Não altera a ficha.
     */
    fun generateLore(
        fichaId: String,
        appearance: String,
        idea: String,
    ): LoreOutcome {
        val provider = roles?.narrator ?: return LoreOutcome(error = "configure o provedor de IA na aba Config")
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == fichaId } ?: return LoreOutcome(error = "ficha não encontrada")
        val pkg = dev.rpgsolo.rules.CampaignStarter.packageFor(content.registry, ficha.system.editionId)
        val input =
            dev.rpgsolo.ai.CharacterNarrativeInput(
                characterName = ficha.name,
                canonicalChoices = characterChoices(ficha.id).toJson(),
                approvedTraits = ficha.traits.map { id -> pkg?.trait(id)?.name ?: id },
                appearance = appearance,
                initialIdea = idea,
            )
        return try {
            LoreOutcome(text = dev.rpgsolo.ai.CharacterNarrativeAssistant.generateLore(provider, input).text)
        } catch (e: Exception) {
            LoreOutcome(error = e.message ?: "falha ao gerar a história")
        }
    }

    /** Grava a história aprovada (texto vazio apaga). Só altera `approvedLore`; Executar fora da thread principal. */
    fun approveLore(
        fichaId: String,
        text: String,
    ): String? {
        val ficha = (fichas + archivedFichas).firstOrNull { it.id == fichaId } ?: return "ficha não encontrada"
        val clean = text.trim()
        if (clean.isNotEmpty() && clean.length !in 40..4_000) return "a história deve ter entre 40 e 4000 caracteres"
        saveFicha(ficha.copy(approvedLore = clean))
        return null
    }

    /** Importa o formato versionado atual e também JSONs antigos que continham somente a ficha. */
    fun importFicha(raw: String): String? =
        try {
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
            val importedChoices =
                when {
                    choices != CharacterChoices() -> choices
                    decoded.editorialChoicesJson.isNotBlank() -> CharacterChoices.fromJson(decoded.editorialChoicesJson)
                    else -> CharacterChoices()
                }
            val imported =
                decoded.copy(
                    id = importedId,
                    name = if (exists) "${decoded.name} (importada)" else decoded.name,
                    editorialChoicesJson = if (importedChoices != CharacterChoices()) importedChoices.toJson() else decoded.editorialChoicesJson,
                )
            saveFicha(imported)
            if (importedChoices != CharacterChoices()) saveCharacterChoices(imported.id, importedChoices)
            val choiceWarnings =
                if (importedChoices != CharacterChoices()) {
                    CharacterCreationCatalog.validateChoices(
                        importedChoices,
                    )
                } else {
                    emptyList()
                }
            buildString {
                append("Ficha ").append(imported.name).append(" importada")
                if (importedChoices != CharacterChoices()) append(" com escolhas de raça/linhagem, classe, profissão e maldições")
                append(".")
                if (choiceWarnings.isNotEmpty()) {
                    append(
                        "\nAtenção: as escolhas foram preservadas, mas precisam ser revisadas porque algumas referências não são válidas neste catálogo:",
                    )
                    choiceWarnings.forEach { append("\n- ").append(it) }
                    CharacterCreationCatalog.suggestReplacements(importedChoices).forEach { append("\n- ").append(it) }
                    append("\nAbra “Editar escolhas” na biblioteca para corrigir.")
                }
            }
        } catch (e: Exception) {
            e.message ?: "JSON de ficha inválido"
        }

    fun activeCampaignId(): String = session.campaignId

    /** DC-02: biblioteca persistida no Room; o texto dos arquivos não é mantido nesta estrutura. */
    fun sourceLibrary(): SourceLibrary = store.sourceLibrary()

    /** Importa metadados e guarda bytes originais. Executar fora da thread principal. */
    fun importSourceDocument(
        fileName: String,
        bytes: ByteArray,
    ): SourceDocumentImportOutcome =
        try {
            val current = store.sourceLibrary()
            val (updated, entry) = current.addImport(fileName, bytes, System.currentTimeMillis())
            val alreadyRegistered = current.get(entry.id) != null
            if (entry.status == SourceStatus.READY) {
                sourceDocuments.save(entry.id, bytes, entry.sha256)
            }
            try {
                store.saveSourceLibrary(updated)
            } catch (e: Exception) {
                if (!alreadyRegistered && entry.status == SourceStatus.READY) {
                    sourceDocuments.delete(entry.id)
                }
                throw e
            }
            sourceLibraryVersion++
            SourceDocumentImportOutcome(entry)
        } catch (e: Exception) {
            SourceDocumentImportOutcome(null, e.message ?: "falha ao importar documento-fonte")
        }

    /** Lê somente fonte pronta e verifica tamanho e SHA-256 antes de devolver os bytes; executar em IO. */
    fun sourceDocumentBytes(sourceId: String): ByteArray? {
        val entry = store.sourceLibrary().get(sourceId) ?: return null
        if (entry.status != SourceStatus.READY) return null
        return sourceDocuments.read(entry.id, entry.sha256, entry.byteSize)
    }

    /** Grava a biblioteca e os vínculos como uma unidade transacional. */
    fun saveSourceLibrary(library: SourceLibrary): String? =
        try {
            val previousIds = store.sourceLibrary().entries.map { it.id }.toSet()
            store.saveSourceLibrary(library)
            val retainedIds = library.entries.map { it.id }.toSet()
            (previousIds - retainedIds).forEach { sourceDocuments.delete(it) }
            sourceLibraryVersion++
            null
        } catch (e: Exception) {
            e.message ?: "falha ao salvar a biblioteca de fontes"
        }

    /** DC-03: estados e progresso da indexação persistidos no Room. */
    fun sourcePipelineStates(): List<PipelineState> = store.sourcePipelineStates()

    /** DC-04: lê os fatos narrativos da campanha ativa ou indicada. */
    fun canonFacts(campaignId: String): CanonFacts = store.canonFacts(campaignId)

    fun saveCanonFacts(
        campaignId: String,
        facts: CanonFacts,
    ): String? =
        try {
            store.saveCanonFacts(campaignId, facts)
            canonFactsVersion++
            null
        } catch (e: Exception) {
            e.message ?: "falha ao salvar fatos canônicos"
        }

    fun saveSourcePipelineState(state: PipelineState): String? =
        try {
            store.saveSourcePipelineState(state)
            sourceLibraryVersion++
            null
        } catch (e: Exception) {
            e.message ?: "falha ao salvar o progresso de indexação"
        }

    /**
     * DC-03: retoma ao abrir o app a indexação de fontes prontas sem estado ou paradas em
     * IMPORTED/CHUNKED. Estados concluídos ou FAILED não são repetidos. Executar fora da thread principal.
     */
    fun resumeSourcePipelines() {
        val states = store.sourcePipelineStates().associateBy { it.sourceId }
        store.sourceLibrary().entries
            .filter { it.status == dev.rpgsolo.engine.SourceStatus.READY }
            .forEach { entry -> indexSource(entry, states[entry.id]) }
        refreshSourceContext()
    }

    /** Executa a pipeline de uma fonte pronta até o fim, gravando o progresso por lote. */
    private fun indexSource(
        entry: SourceEntry,
        savedState: PipelineState?,
    ) {
        val saved = savedState ?: dev.rpgsolo.engine.SourcePipeline.start(entry.id)
        if (saved.done) return
        val bytes = sourceDocuments.read(entry.id, entry.sha256, entry.byteSize)
        val source =
            bytes?.let {
                (dev.rpgsolo.engine.SourceImporter.import(entry.fileName, it) as? dev.rpgsolo.engine.SourceImportResult.Ok)?.source
            }
        val result =
            dev.rpgsolo.engine.SourcePipeline.run(
                state = saved,
                source = source,
                index = dev.rpgsolo.engine.SourcePipeline.resume(saved),
                onProgress = { saveSourcePipelineState(it) },
            )
        saveSourcePipelineState(result)
    }

    /** Fase 14: importa o arquivo, guarda os bytes e indexa na hora. Executar em IO. Devolve o erro, ou null. */
    fun importAndIndexSource(
        fileName: String,
        bytes: ByteArray,
    ): String? {
        val outcome = importSourceDocument(fileName, bytes)
        val entry = outcome.entry ?: return outcome.error ?: "falha ao importar documento-fonte"
        if (entry.status != dev.rpgsolo.engine.SourceStatus.READY) return entry.error ?: "documento recusado"
        indexSource(entry, store.sourcePipelineStates().firstOrNull { it.sourceId == entry.id })
        refreshSourceContext()
        return null
    }

    /** Vincula ou desvincula uma fonte pronta da campanha ativa. */
    fun setSourceLinked(
        sourceId: String,
        linked: Boolean,
    ): String? {
        val campaignId = session.campaignId
        val library = store.sourceLibrary()
        val updated =
            if (linked) {
                library.link(campaignId, sourceId, System.currentTimeMillis()) ?: return "só fontes prontas podem ser vinculadas"
            } else {
                library.unlink(campaignId, sourceId)
            }
        return saveSourceLibrary(updated) ?: run {
            refreshSourceContext()
            null
        }
    }

    /** Marca a fonte como removida (vínculos e campanhas permanecem) e apaga o arquivo original. */
    fun removeSource(sourceId: String): String? {
        val error = saveSourceLibrary(store.sourceLibrary().remove(sourceId))
        if (error == null) {
            sourceDocuments.delete(sourceId)
            refreshSourceContext()
        }
        return error
    }

    /** Reaplica o contexto documental da campanha ativa; só envia à IA com consentimento Q-09. */
    fun refreshSourceContext() {
        val campaignId = session.campaignId
        if (campaignId.isBlank()) return
        runCatching {
            if (sourcePrivacyConsent(campaignId)) {
                configureCampaignSourceContext(campaignId, sourceIndexForCampaign(campaignId))
            } else {
                clearCampaignSourceContext(campaignId)
            }
        }
    }

    /** Adiciona um fato manual (status desconhecido, sem fonte) à campanha ativa. */
    fun addManualFact(text: String): String? {
        val campaignId = session.campaignId
        val fact =
            dev.rpgsolo.engine.CanonFact(
                id = java.util.UUID.randomUUID().toString(),
                campaignId = campaignId,
                text = text.trim(),
                status = dev.rpgsolo.engine.FactStatus.UNKNOWN,
                createdAt = System.currentTimeMillis(),
            )
        val updated = store.canonFacts(campaignId).add(fact) ?: return "fato vazio ou acima de ${dev.rpgsolo.engine.CanonFacts.MAX_TEXT} caracteres"
        return saveCanonFacts(campaignId, updated) ?: run {
            refreshSourceContext()
            null
        }
    }

    fun setFactKnown(
        factId: String,
        known: Boolean,
    ): String? {
        val campaignId = session.campaignId
        val updated = store.canonFacts(campaignId).markKnown(factId, known) ?: return "fato inexistente"
        return saveCanonFacts(campaignId, updated) ?: run {
            refreshSourceContext()
            null
        }
    }

    fun removeFact(factId: String): String? {
        val campaignId = session.campaignId
        return saveCanonFacts(campaignId, store.canonFacts(campaignId).remove(factId)) ?: run {
            refreshSourceContext()
            null
        }
    }

    /** Reconstrói o índice apenas das fontes prontas vinculadas à campanha e dos trechos já indexados. */
    fun sourceIndexForCampaign(campaignId: String): SourceIndex {
        val library = store.sourceLibrary()
        val linkedIds = library.forCampaign(campaignId).map { it.sourceId }.toSet()
        val readyIds =
            library.entries
                .filter { it.id in linkedIds && it.status == dev.rpgsolo.engine.SourceStatus.READY }
                .map { it.id }
                .toSet()
        return SourceIndex().also { index ->
            store.sourcePipelineStates().filter { it.sourceId in readyIds }.forEach { state ->
                index.addAll(state.chunks.take(state.indexedCount))
            }
        }
    }

    /** DC-05.2: modo de fidelidade documental da campanha ativa, derivado do log do motor. */
    fun sourceMode(): dev.rpgsolo.engine.SourceMode? = session.state.sourceMode

    /** Define o modo por comando/evento canônico; o próprio motor recusa repetir o modo atual. */
    fun setSourceMode(mode: dev.rpgsolo.engine.SourceMode): String? =
        when (val result = session.execute(dev.rpgsolo.engine.SetSourceMode(mode))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    fun activePackageId(): String? = activePackage?.manifest?.id

    /** Importa um ZIP de pacote de regras, valida-o antes de mantê-lo e persiste-o no armazenamento privado. */
    fun importPackage(uri: Uri): String {
        val dir = File(getApplication<Application>().filesDir, "packages-import")
        if (!dir.exists() && !dir.mkdirs()) return "não foi possível criar a pasta de pacotes"
        val archive = File(dir, "package-${System.nanoTime()}.zip")
        return try {
            val input =
                getApplication<Application>().contentResolver.openInputStream(uri)
                    ?: run {
                        archive.delete()
                        return "não foi possível abrir o arquivo selecionado"
                    }
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
    fun activateCampaign(campaignId: String): String? =
        try {
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
    fun addNpc(name: String): String? =
        try {
            require(name.isNotBlank()) { "dê um nome ao adversário" }
            val pkg = requireNotNull(activePackage) { "instale/ative um pacote de regras antes de criar adversários" }
            val stats = pkg.data.stats.associate { it.id to it.default }
            val entity =
                Entity(
                    id = "npc-${System.nanoTime()}",
                    name = name.trim(),
                    kind = EntityKind.NPC,
                    stats = stats,
                    resources = pkg.resourcesFor(stats),
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
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }

    private val combatActions =
        CombatActions(
            session = { session },
            activePackage = { activePackage },
            playerId = { playerId },
            pendingCombatNpcIds = pendingCombatNpcIds,
            savePending = { savePendingCombatNpcIds() },
            onChanged = { campaignVersion++ },
            bonuses = bonuses,
            onCompanionLevelUp = { name, level -> companionNotice = "$name subiu para o nível $level." },
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

    /** Ataque manual usando o pipeline unificado e o evento ClashRecorded. */
    fun unifiedAttack(targetId: String): String? = combatActions.unifiedAttack(targetId)

    private fun awardCombatVictoryXp(combatantIds: Collection<String>): Int = combatActions.awardCombatVictoryXp(combatantIds)

    fun additionalRulesView(): List<dev.rpgsolo.engine.RegraAdicional> =
        session.state.additionalRules.values.sortedWith(compareBy({ it.approved }, { it.name }))

    /** Relações direcionadas ao jogador; mostra atitude em palavras, nunca os valores numéricos internos. */
    fun relationshipsView(): List<RelationshipView> =
        session.state.relations.mapNotNull { (key, value) ->
            val ids = key.split('\u0000', limit = 2)
            if (ids.size != 2 || ids[1] != playerId) return@mapNotNull null
            val npc = session.state.entities[ids[0]]?.takeIf { it.kind == EntityKind.NPC } ?: return@mapNotNull null
            val attitude =
                when {
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
            val exits =
                state.placeLinks[id].orEmpty().filter { it in state.knownPlaces }
                    .mapNotNull { nextId -> state.places[nextId]?.let { nextId to it.name } }
            PlaceView(id, place.name, place.description, state.currentPlaceId == id, exits)
        }.sortedWith(compareByDescending<PlaceView> { it.current }.thenBy { it.name })
    }

    /** A viagem e o avanço dos relógios `moveTick` são validados atomicamente pelo motor. */
    fun moveToPlace(id: String): String? =
        when (
            val result = session.execute(dev.rpgsolo.engine.MoveToPlace(id))
        ) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    private fun approvedRuleBonus(scope: String): Int = bonuses.approvedRuleBonus(scope)

    /** N-33.5: grau (0 a 5) de Construção do herói: o maior posto aprendido entre as profissões de construção, limitado a 5. */
    private fun constructionGrade(entity: dev.rpgsolo.engine.Entity): Int =
        listOf(
            "carpinteiro",
            "marceneiro",
            "pedreiro",
            "arquiteto",
            "construtor-de-barcos",
            "engenheiro-de-pontes",
            "construtor-de-fortificacoes",
        ).maxOf { tree -> (1..5).lastOrNull { (entity.skillTreeLevels["profissao-$tree-$it"] ?: 0) > 0 } ?: 0 }

    /** N-35: +1 em todos os testes a cada 2 pontos de Sorte (atributo + itens equipados). */
    private fun luckTestBonusOf(entityId: String): Int {
        val pkg = activePackage ?: return 0
        val luckStat = pkg.data.creation["combate_atributo_sorte"] ?: return 0
        val state = session.state
        val points =
            (state.entities[entityId]?.stats?.get(luckStat) ?: 0) +
                pkg.equipmentBonus(state.equipped[entityId].orEmpty().values, luckStat, bonuses.wearerProfile(entityId))
        return dev.rpgsolo.engine.LuckRules.testBonus(points)
    }

    private fun approvedRuleEffects(scopes: Collection<String>): List<dev.rpgsolo.rules.ExternalBonus> = bonuses.approvedRuleEffects(scopes)

    private fun approvedRuleBonusSummary(scopes: Collection<String>): String = bonuses.approvedRuleBonusSummary(scopes)

    fun approveAdditionalRule(id: String): String? =
        when (
            val result = session.execute(dev.rpgsolo.engine.ApproveRuleAddition(id))
        ) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    fun rejectAdditionalRule(id: String): String? =
        when (
            val result = session.execute(dev.rpgsolo.engine.RejectRuleAddition(id))
        ) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    private fun conditionDiceShift(
        entityId: String,
        stat: String,
    ): Int = bonuses.conditionDiceShift(entityId, stat)

    private fun combatTraitIds(entityId: String): List<String> = bonuses.combatTraitIds(entityId)

    /** Poderes do pacote cujo traço está na ficha ativa; sem pacote ou sem traços, lista vazia. */
    fun powersView(): List<PowerView> {
        val pkg = activePackage ?: return emptyList()

        fun view(
            id: String,
            name: String,
            def: dev.rpgsolo.rules.PowerDef,
        ) = PowerView(
            id = id,
            name = name,
            description = def.description,
            resourceName = pkg.data.resources.firstOrNull { it.id == def.resource }?.name ?: def.resource,
            cost = def.cost,
            needsTarget = def.target == dev.rpgsolo.rules.PowerTarget.ENEMY,
        )
        val powers = dev.rpgsolo.ai.PowerMenu(pkg).available(combatTraitIds(playerId)).map { view(it.id, it.name, it) }
        // MU-07.4: magias aprendidas com efeito de conjuração aparecem como "magia:<id>".
        val spells =
            session.state.entities[playerId]?.spells.orEmpty().sorted().mapNotNull { spellId ->
                val def = pkg.spellPowerId(spellId)?.let { pid -> pkg.data.powers.firstOrNull { it.id == pid } } ?: return@mapNotNull null
                view("magia:$spellId", pkg.spell(spellId)?.name ?: spellId, def)
            }
        return powers + spells
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
        return when (
            val result =
                controller?.spendTraitUse(def.id, def.name, def.uses, user.name)
                    ?: return "O controlador da campanha não está disponível."
        ) {
            is dev.rpgsolo.ai.PlayResult.Played -> {
                campaignVersion++
                null
            }
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
        return when (
            val result =
                controller?.removeConditionByCheck(conditionId, name, spec, me.name)
                    ?: return "O controlador da campanha não está disponível."
        ) {
            is dev.rpgsolo.ai.PlayResult.Played -> {
                campaignVersion++
                null
            }
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
        return when (
            val result =
                controller?.survivalTest(spec, pkg.survivalFailureLimit, me.name)
                    ?: return "O controlador da campanha não está disponível."
        ) {
            is dev.rpgsolo.ai.PlayResult.Played -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
        }
    }

    /** Nome de exibição de um recurso do pacote ativo; o próprio id se não houver. */
    fun resourceName(id: String): String = activePackage?.data?.resources?.firstOrNull { it.id == id }?.name ?: id

    /** NPCs vivos que podem ser alvo de um poder ofensivo. */
    fun powerTargets(): List<CombatTarget> =
        session.state.entities.values
            .filter { it.kind == EntityKind.NPC && it.alive }
            .map { CombatTarget(it.id, it.name) }

    /**
     * Ativa um poder pelo motor. ASSUMINDO: usar um poder não consome o turno de combate; custo, teste e efeito
     * vêm do pacote e são resolvidos pelo motor. Chamar fora da thread principal (via `act`).
     */
    fun activatePower(
        powerId: String,
        targetId: String?,
    ): String? {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val user = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val menu = dev.rpgsolo.ai.PowerMenu(pkg)
        val spellId = powerId.takeIf { it.startsWith("magia:") }?.removePrefix("magia:")
        val traits = combatTraitIds(playerId)
        val targetTraits = targetId?.let(::combatTraitIds).orEmpty()
        return when (
            val built =
                if (spellId != null) {
                    menu.buildSpell(spellId, playerId, targetId, session.state, traits, targetTraits)
                } else {
                    menu.build(powerId, playerId, targetId, session.state, traits, targetTraits)
                }
        ) {
            is dev.rpgsolo.ai.PowerBuild.Invalid -> built.reason
            is dev.rpgsolo.ai.PowerBuild.Ready -> {
                val targetName = built.spec.targetId?.let { session.state.entities[it]?.name }
                val spec =
                    when (val effect = built.spec.effect) {
                        is dev.rpgsolo.engine.PowerEffect.Cure -> {
                            val receiverId = built.spec.targetId ?: built.spec.userId
                            val recovery = bonuses.skillTreeAndActiveBonus(receiverId, "recuperacao")
                            built.spec.copy(effect = effect.copy(amount = (effect.amount + recovery).coerceAtLeast(0)))
                        }
                        else -> built.spec
                    }
                when (
                    val result =
                        controller?.activatePower(spec, spellId?.let { pkg.spell(it)?.name } ?: built.def.name, user.name, targetName)
                            ?: return "O controlador da campanha não está disponível."
                ) {
                    is dev.rpgsolo.ai.PlayResult.Played -> {
                        campaignVersion++
                        null
                    }
                    is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
                }
            }
        }
    }

    private val inventory =
        InventoryActions(
            session = { session },
            activePackage = { activePackage },
            playerId = { playerId },
            isPlayerCombatTurn = { isPlayerCombatTurn() },
            onChanged = { campaignVersion++ },
            wearer = { bonuses.wearerProfile(playerId) },
        )

    private val legacy =
        LegacyActions(
            session = { session },
            activePackage = { activePackage },
            playerId = { playerId },
            setPlayer = { playerId = it },
            onChanged = { campaignVersion++ },
            restartAge = { anos, classe -> restartHeroAge(anos, classe) },
        )

    /** Vida nova (renascer ou assumir NPC): a idade recomeça e a morte anterior deixa de valer na simulação. */
    private fun restartHeroAge(
        anos: Int,
        classe: String?,
    ): String? {
        val c = classe ?: session.state.facts[IdadeHeroi.LONGEVIDADE] ?: return null
        val diaMundo = session.state.facts[AplicacaoSimulacao.CHAVE_DIA]?.toIntOrNull() ?: activeWorld()?.proximoDia ?: 0
        val r = session.executeAll(IdadeHeroi.recomecar(anos, diaMundo, c))
        return (r as? dev.rpgsolo.engine.ExecResult.Rejected)?.reason
    }

    /** M-04: opções quando o herói morre (null com o herói vivo). */
    fun legacyView(): LegacyView? = legacy.view()

    fun legacyRebirth(): String? = legacy.useRebirth()

    /** Assume o NPC e grava o novo herói nos metadados da campanha (senão a reabertura voltaria ao herói morto). */
    fun legacyAssume(npcId: String): String? = legacy.assume(npcId).also { if (it == null) saveMetadata() }

    fun legacyEnd(): String? = legacy.endCampaign()

    private val companionActions =
        CompanionActions(
            session = { session },
            activePackage = { activePackage },
            playerId = { playerId },
            onChanged = { campaignVersion++ },
        )

    /** N-32.4: companheiros adestrados do herói e marcas de adestramento no inventário. */
    fun companionViews(): List<CompanionView> = companionActions.views()

    /** Tipos de corpo do pacote ativo (id, nome) para a escolha ao adestrar. */
    fun activePackageCompanionBodies(): List<Pair<String, String>> = activePackage?.companions?.bodies.orEmpty().map { it.id to it.name }

    fun inCombatNow(): Boolean = session.state.combat != null

    fun companionTameOptions(): List<TameOption> = companionActions.tameOptions()

    /** N-33.2d: animais perdidos que ainda podem ser readestrados. */
    fun companionLostViews(): List<LostCompanionView> = companionActions.lostViews()

    fun companionRetrain(
        animalId: String,
        itemId: String,
    ): String? = companionActions.retrain(animalId, itemId)

    fun companionStatLabel(id: String): String = companionActions.statLabel(id)

    fun tameCompanion(
        itemId: String,
        name: String,
        bodyType: String,
    ): String? = companionActions.tame(itemId, name, bodyType)

    fun companionChooseAttack(
        animalId: String,
        attackId: String,
    ): String? = companionActions.chooseAttack(animalId, attackId)

    fun companionAllocate(
        animalId: String,
        stat: String,
    ): String? = companionActions.allocate(animalId, stat)

    fun companionRaiseAttack(animalId: String): String? = companionActions.raiseAttack(animalId)

    fun companionLearnTrick(
        animalId: String,
        trickId: String,
    ): String? = companionActions.learnTrick(animalId, trickId)

    fun companionOrderTrick(
        animalId: String,
        trickId: String,
    ): String? = companionActions.orderTrick(animalId, trickId)

    fun companionFeed(
        animalId: String,
        itemId: String,
    ): String? = companionActions.feed(animalId, itemId)

    fun companionPet(animalId: String): String? = companionActions.pet(animalId)

    /** N-33.2c: visita ao animal guardado no criadouro (1d6 de lealdade). */
    fun companionVisit(animalId: String): String? = companionActions.visit(animalId)

    /** N-33.2c: traz o animal guardado (10 prata, 1 dia) e guarda o que andava com o herói. */
    fun companionBring(animalId: String): String? = companionActions.bring(animalId)

    fun companionStrike(
        animalId: String,
        targetId: String,
    ): String? = companionActions.strike(animalId, targetId)

    fun inventoryView(): List<InventoryView> = inventory.inventoryView()

    /** Usa consumíveis apenas quando o pacote declara uma ação mecânica explícita. */
    fun useItem(itemId: String): String? = inventory.useItem(itemId)

    fun equipItem(itemId: String): String? = inventory.equipItem(itemId)

    fun shopView(): ShopView? = inventory.shopView()

    fun buyItem(
        itemId: String,
        qty: Int = 1,
    ): String? = inventory.buyItem(itemId, qty)

    fun enchantItem(
        itemId: String,
        element: dev.rpgsolo.engine.ElementPath,
    ): String? = inventory.enchantItem(itemId, element)

    fun availableRecipes(): List<dev.rpgsolo.rules.RecipeDef> = inventory.availableRecipes()

    fun craftRecipe(recipeId: String): String = inventory.craftRecipe(recipeId)

    /** Receitas que o herói pode usar agora, com nomes de item do pacote ativo. */
    fun recipesView(): List<RecipeView> {
        val pkg = activePackage ?: return emptyList()

        fun line(m: Map<String, Int>): String = m.entries.joinToString(", ") { (id, qty) -> "$qty× ${pkg.item(id)?.name ?: id}" }
        return inventory.availableRecipes().map { RecipeView(it.id, it.name, line(it.inputs), line(it.outputs)) }
    }

    fun pilfer(
        targetId: String,
        itemId: String,
    ): String = inventory.pilfer(targetId, itemId)

    fun gemsView(): GemsView = inventory.gemsView()

    /** N-33.4: gemas (ver `InventoryActions.socketGem`, `removeGem` e `cutGem`). */
    fun socketGem(
        slot: String,
        gemItemId: String,
        viaNpc: Boolean = false,
    ): String? = inventory.socketGem(slot, gemItemId, viaNpc)

    fun removeGem(
        slot: String,
        index: Int,
        viaNpc: Boolean = false,
    ): String? = inventory.removeGem(slot, index, viaNpc)

    fun cutGem(gemItemId: String): String = inventory.cutGem(gemItemId)

    fun tattooView(): TattooView = inventory.tattooView()

    fun tattooScopeLabel(scope: String): String =
        when (scope) {
            "ataque" -> "Ataque"
            "defesa" -> "Defesa"
            "velocidade" -> "Velocidade"
            "max_pv" -> "PV máximo"
            else -> scope
        }

    /** N-36: tatua o herói (ver `InventoryActions.tattooHero`). */
    fun tattooHero(
        scope: String,
        tattooerGrade: Int,
        coverIndex: Int? = null,
    ): String? = inventory.tattooHero(scope, tattooerGrade, coverIndex)

    fun smithEnchantItem(
        itemId: String,
        element: dev.rpgsolo.engine.ElementPath,
    ): String? = inventory.smithEnchantItem(itemId, element)

    fun sellItem(
        itemId: String,
        qty: Int = 1,
    ): String? = inventory.sellItem(itemId, qty)

    fun spellShopView(): List<SpellShopEntry> = inventory.spellShopView()

    fun buySpell(spellId: String): String? = inventory.buySpell(spellId)

    fun convertCoins(
        toGold: Boolean,
        goldQty: Int = 1,
    ): String? = inventory.convertCoins(toGold, goldQty)

    fun unequipItem(slot: String): String? = inventory.unequipItem(slot)

    /** Ataque local: atributos e bônus vêm do pacote e do equipamento; a IA não escolhe números. */
    fun attack(targetId: String): String? = combatActions.attack(targetId)

    /** A postura defensiva concede o bônus do pacote contra o próximo ataque recebido e consome o turno. */
    fun defend(): String? = combatActions.defend()

    /** Fuga usa um teste real do pacote; sucesso encerra o combate, falha cede a próxima ação ao adversário. */
    fun fleeCombat(): String? = combatActions.fleeCombat()

    /** RV-06: NPCs relevantes que podem ser poupados agora (turno do jogador, vida no limite). */
    fun spareOptions(): List<CombatActions.SpareOption> = combatActions.spareOptions()

    /** RV-06: poupar o NPC; o motor grava o rival e o XP é metade da derrota. */
    fun spare(npcId: String): String? = combatActions.spare(npcId)

    /** RV-06: rivais registrados no mundo desta campanha (RV-02), por prioridade. */
    fun rivals(): List<dev.rpgsolo.engine.Rival> = dev.rpgsolo.engine.Rivals.register(recurringCharacters())

    /** LG-04: corpos com itens para recuperar. */
    fun bodies(): List<CombatActions.BodyOption> = combatActions.bodies()

    /** LG-04: em combate, teste de destreza e turno gasto; fora de combate, só pegar. */
    fun recoverBody(bodyId: String): String? = combatActions.recoverBody(bodyId)

    fun exportBackup(): String =
        dev.rpgsolo.engine.Backup.export(
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
                characterChoices =
                    (fichas + archivedFichas).associate { ficha ->
                        ficha.id to characterChoices(ficha.id).toJson()
                    }.filterValues { raw ->
                        CharacterChoices.fromJson(raw) != CharacterChoices()
                    },
                archivedFichaIds = archivedFichas.map { it.id },
            ),
        )

    fun importBackup(text: String): String? =
        try {
            val raw = text.trim()
            val file = dev.rpgsolo.engine.Backup.decode(raw)
            val meta = file.metadata
            // Validar todas as fichas antes de criar a campanha: não ignorar JSONs inválidos
            // nem deixar um backup parcialmente restaurado por causa de uma ficha corrompida.
            val decodedFichas =
                meta?.fichas.orEmpty().map { rawFicha ->
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
                    do {
                        mappedId = newFichaId()
                    } while (mappedId in usedIds)
                }
                usedIds += mappedId
                fichaIdMap[ficha.id] = mappedId
            }

            var importedCampaignId = ""
            store.inTransaction {
                importedCampaignId =
                    dev.rpgsolo.engine.Backup.import(
                        store, raw, "import-${java.util.UUID.randomUUID()}",
                    )
                if (meta != null) {
                    store.saveMetadata(
                        CampanhaMetaRow(
                            campanhaId = importedCampaignId,
                            chaptersJson = meta.chaptersJson,
                            questsJson = meta.questsJson,
                            timelineJson = meta.timelineJson,
                            playerId = meta.playerId,
                            fichaId =
                                meta.fichaId?.let { fichaId ->
                                    fichaIdMap[fichaId] ?: if (store.fichaExiste(fichaId)) null else fichaId
                                },
                            packageId = meta.packageId,
                            systemId = meta.systemId,
                            editionId = meta.editionId,
                        ),
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
            refreshCampaigns()
            campaignVersion++
            null
        } catch (
            e: IllegalArgumentException,
        ) {
            e.message
        } catch (
            e: IllegalStateException,
        ) {
            e.message
        } catch (e: Exception) {
            "falha ao restaurar backup: ${e.message ?: "erro inesperado"}"
        }

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
                    tableTimeline.ifEmpty { dev.rpgsolo.engine.Codec.decodeTimeline(row.timelineJson) },
                )
            }
        }

        val firstPlayerId = session.state.entities.values.firstOrNull { it.kind == EntityKind.PLAYER }?.id
        playerId = row?.playerId?.takeIf { session.state.entities.containsKey(it) } ?: firstPlayerId ?: "hero"
        activeFichaId = row?.fichaId ?: playerId
        activeFicha = activeFichaId?.let { id -> fichas.firstOrNull { it.id == id } }
        activeSystemId = row?.systemId ?: activeFicha?.system?.systemId
        activeEditionId = row?.editionId ?: activeFicha?.system?.editionId
        activePackage =
            when {
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
        pendingCombatNpcIds +=
            activeCampaignPrefs.getStringSet(pendingCombatKey(), emptySet()).orEmpty()
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
        refreshSourceContext()
    }

    /** Grava metadados numa transação; falha fica em [saveError] (nunca mostra "salvo" sem confirmação) e não derruba a jogada. */
    private fun saveMetadata() {
        saveError =
            try {
                writeMetadata()
                syncRecurring()
                store.touch(session.campaignId)
                session.saveError
            } catch (e: Exception) {
                e.message ?: e::class.simpleName ?: "falha ao gravar"
            }
    }

    /**
     * MP-02: grava heróis mortos e NPCs com derrota viva como personagens recorrentes do mundo da campanha
     * (anti-duplicação pela identidade). Falha aqui nunca impede salvar a campanha.
     */
    private fun syncRecurring() {
        runCatching {
            val state = session.state
            if (state.legacies.isEmpty() && state.defeats.isEmpty()) return
            val worldId = worldStore.vinculo(session.campaignId)?.mundoId ?: return
            store.saveRecurring(worldId, dev.rpgsolo.engine.RecurringExtractor.from(state, worldId, session.campaignId))
        }
    }

    /** MP-02: personagens recorrentes do mundo da campanha aberta, também de campanhas anteriores e outros protagonistas. */

    /** RV-03: panteão unificado (heróis mortos e rivais) do mundo desta campanha; vazio sem mundo vinculado. */
    fun pantheon(): List<dev.rpgsolo.engine.PantheonEntry> = dev.rpgsolo.engine.Pantheon.entries(recurringCharacters())

    /** LG-06: cartões do memorial dos heróis mortos da campanha aberta. */
    fun memorialCards(): List<dev.rpgsolo.engine.MemorialCard> = dev.rpgsolo.engine.Memorial.cards(session.state.legacies.values)

    /**
     * LG-05: planta na campanha recém-criada até 3 lendas (heróis mortos do panteão de qualquer mundo já jogado, exceto
     * o próprio mundo novo se ele está vazio) como rumores de bardo e fatos `lenda.*`. Devolve `null` se aceitou (ou se
     * não havia lenda) ou o motivo da recusa. ASSUMINDO: o panteão vem de todos os mundos das campanhas salvas, pois o
     * mundo novo ainda não tem recorrentes.
     */
    fun seedLegends(): String? =
        runCatching {
            val worldIds = store.campaigns().mapNotNull { worldStore.vinculo(it.id)?.mundoId }.distinct()
            val all = worldIds.flatMap { store.recurringCharacters(it) }.distinctBy { it.identityKey }
            val chosen = dev.rpgsolo.engine.Legends.pick(dev.rpgsolo.engine.Pantheon.entries(all))
            val commands = dev.rpgsolo.engine.Legends.seed(session.state, chosen)
            if (commands.isEmpty()) return@runCatching null
            when (val result = session.executeAll(commands)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    campaignVersion++
                    null
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        }.getOrElse { it.message ?: "lendas não trazidas" }

    /** LG-05: lendas da campanha aberta para o contexto do mestre. */
    private fun legendsContextForMaster(): String? = dev.rpgsolo.engine.Legends.masterContext(session.state)

    fun recurringCharacters(): List<dev.rpgsolo.engine.RecurringCharacter> =
        runCatching {
            worldStore.vinculo(session.campaignId)?.mundoId?.let { store.recurringCharacters(it) }.orEmpty()
        }.getOrDefault(emptyList())

    /** Tenta gravar de novo log e metadados pendentes. Devolve `null` se confirmou ou o motivo da falha. */
    fun retrySave(): String? {
        val logFailure = session.recover()
        saveMetadata()
        return logFailure ?: saveError
    }

    private fun writeMetadata() =
        store.inTransaction {
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
                    editionId = activeEditionId ?: activeFicha?.system?.editionId,
                ),
            )
        }

    fun renameCampaign(name: String): String? =
        try {
            store.rename(session.campaignId, name)
            refreshCampaigns()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }

    fun duplicateCampaign(): String? =
        try {
            val source = requireNotNull(store.campaign(session.campaignId))
            store.duplicate(source.id, "${source.id}-copy-${System.nanoTime()}", "${source.name} (cópia)")
            refreshCampaigns()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }

    /** CP-04.1: conteúdo do "Livro da aventura" da campanha aberta (só leitura; sem segredos). */
    fun adventureBook(): dev.rpgsolo.engine.AdventureBook =
        dev.rpgsolo.engine.AdventureBookBuilder.build(
            campaigns.firstOrNull { it.id == session.campaignId }?.name ?: "Aventura",
            session.state.entities[playerId]?.name,
            chapters.chapters,
            quests.all(),
            quests.timeline(),
        )

    /** CP-04.2: "Arquivo completo" da campanha aberta, só leitura e sem segredos. */
    fun campaignArchive(): dev.rpgsolo.engine.AdventureBook =
        dev.rpgsolo.engine.CampaignArchiveBuilder.build(
            adventureBook(),
            session.state,
            playerId,
            itemName = { id -> activePackage?.item(id)?.name ?: id },
        )

    fun campaignArchivePdf(): ByteArray = BookPdf.render(campaignArchive())

    /** CP-04.1: bytes do PDF do livro da aventura. Salvar e compartilhar entram no CP-04.3. */
    fun adventureBookPdf(): ByteArray = BookPdf.render(adventureBook())

    /** CP-02: pausar, concluir ou reativar qualquer campanha da lista. */
    fun setCampaignState(
        campaignId: String,
        state: dev.rpgsolo.data.EstadoCampanha,
    ): String? =
        if (store.setState(campaignId, state)) {
            refreshCampaigns()
            campaignVersion++
            null
        } else {
            "campanha não encontrada"
        }

    fun deleteCurrentCampaign(): String? =
        try {
            require(campaigns.size > 1) { "mantenha pelo menos uma campanha" }
            val removedId = session.campaignId
            store.delete(removedId)
            setSourcePrivacyConsent(false, removedId)
            val next = store.campaigns().first()
            switchCampaign(next.id)
            refreshCampaigns()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }

    /** Valida e aplica a configuração. Erro claro na tela se o endpoint ou o modelo forem inválidos. */
    fun applySettings(
        kind: ProviderKind,
        endpoint: String,
        model: String,
        apiKey: String,
        cheapModel: String = "",
        premiumModel: String = "",
        cacheSystemPrompt: Boolean = endpoint.trim().startsWith("https://api.anthropic.com/"),
        inputPricePerMillion: Double = 0.0,
        outputPricePerMillion: Double = 0.0,
        cacheReadPricePerMillion: Double = inputPricePerMillion,
        cacheCreationPricePerMillion: Double = inputPricePerMillion,
        preset: String = "",
        fallbackModels: List<String> = emptyList(),
        modelsRefreshedAt: Long = storedSettings?.modelsRefreshedAt ?: 0L,
    ): Boolean =
        try {
            require(apiKey.isNotBlank()) { "informe a chave de API" }
            require(model.isNotBlank() || preset.isBlank()) { "toque em Buscar modelos gratuitos antes de salvar" }
            require(
                listOf(inputPricePerMillion, outputPricePerMillion, cacheReadPricePerMillion, cacheCreationPricePerMillion)
                    .all { it.isFinite() && it >= 0.0 },
            ) { "preços por milhão devem ser números finitos e não negativos" }
            val r =
                if (preset.isNotBlank()) {
                    ProviderFactory.buildFreeFallback(
                        ProviderConfig(endpoint.trim(), model.trim(), apiKey.trim(), cacheSystemPrompt = false),
                        fallbackModels,
                        usage,
                        AiTestHooks.transportOrDefault(),
                        onAllFailed = ::requestModelsRefreshAfterFailure,
                    )
                } else {
                    ProviderFactory.buildRoles(
                        kind,
                        ProviderConfig(endpoint.trim(), model.trim(), apiKey.trim(), cacheSystemPrompt = cacheSystemPrompt),
                        cheapModel,
                        premiumModel,
                        usage,
                        AiTestHooks.transportOrDefault(),
                    )
                }
            roles = r
            controller = buildController(r)
            storedSettings =
                StoredProviderSettings(
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
                    cacheCreationPricePerMillion = cacheCreationPricePerMillion,
                    preset = preset,
                    fallbackModels = fallbackModels.map { it.trim() }.filter { it.isNotEmpty() },
                    modelsRefreshedAt = modelsRefreshedAt,
                ).also(secureSettings::save)
            configError = null
            true
        } catch (e: IllegalArgumentException) {
            configError = e.message
            false
        }

    fun savedSettings(): StoredProviderSettings? = storedSettings

    /** Progresso real do primeiro uso, derivado do provedor, ficha ativa e campanha ativa. */
    fun setupStatus(): SetupStatus =
        SetupStatus(
            providerReady = controller != null && roles != null,
            heroReady = activeFicha != null || fichas.isNotEmpty(),
            campaignReady = session.campaignId != "demo" && activeFicha != null,
        )

    /** Faz uma chamada mínima ao provedor informado, sem persistir a configuração nem tocar no jogo. */
    fun testConnection(
        kind: ProviderKind,
        endpoint: String,
        model: String,
        apiKey: String,
        cacheSystemPrompt: Boolean = endpoint.trim().startsWith("https://api.anthropic.com/"),
    ): ConnectionTestResult =
        try {
            require(endpoint.trim().startsWith("https://")) {
                "o endpoint deve começar com https:// (http:// não é aceito: a chave trafegaria sem criptografia)"
            }
            require(model.isNotBlank()) { "informe o modelo" }
            require(apiKey.isNotBlank()) { "informe a chave de API" }
            val provider =
                ProviderFactory.build(
                    kind,
                    ProviderConfig(endpoint.trim(), model.trim(), apiKey.trim(), cacheSystemPrompt = cacheSystemPrompt),
                    usage = usage,
                    transport = AiTestHooks.transportOrDefault(),
                    maxRequestsPerMinute = 20,
                )
            val reply =
                provider.complete(
                    AiRequest(
                        messages = listOf(AiMessage(Role.USER, "Responda apenas OK.")),
                        // Modelos de raciocínio gastam tokens pensando antes de responder; 8 não bastava para eles.
                        maxTokens = 256,
                        temperature = 0.0,
                    ),
                )
            if (reply.text.isBlank()) {
                ConnectionTestResult(false, "o modelo ${model.trim()} respondeu sem texto")
            } else {
                ConnectionTestResult(true, "conexão funcionando (${provider.name}, modelo ${model.trim()})")
            }
        } catch (e: AiError) {
            val reason = e.message ?: "falha na conexão"
            // Os erros de leitura já citam o modelo; os de rede e de HTTP ainda não.
            ConnectionTestResult(false, if (model.isNotBlank() && model.trim() !in reason) "modelo ${model.trim()}: $reason" else reason)
        } catch (e: IllegalArgumentException) {
            ConnectionTestResult(false, e.message ?: "configuração inválida")
        } catch (e: Exception) {
            ConnectionTestResult(false, "não foi possível testar a conexão: ${e.message ?: "erro desconhecido"}")
        }

    /**
     * Busca os modelos gratuitos de texto do provedor pré-configurado, do mais indicado ao menos. Também confirma
     * que a chave é aceita. Não persiste nada.
     */
    fun discoverFreeModels(
        preset: ProviderPreset,
        apiKey: String,
    ): FreeModelsResult =
        try {
            require(apiKey.isNotBlank()) { "informe a chave de API" }
            val found = ModelDiscovery.fetch(preset, apiKey, AiTestHooks.transportOrDefault())
            if (found.isEmpty()) {
                FreeModelsResult(false, emptyList(), "nenhum modelo gratuito de texto disponível agora; tente de novo mais tarde")
            } else {
                FreeModelsResult(true, found.map { it.id }, "${found.size} modelos gratuitos encontrados")
            }
        } catch (e: AiError) {
            FreeModelsResult(false, emptyList(), e.message ?: "falha ao buscar os modelos")
        } catch (e: IllegalArgumentException) {
            FreeModelsResult(false, emptyList(), e.message ?: "configuração inválida")
        } catch (e: Exception) {
            FreeModelsResult(false, emptyList(), "não foi possível buscar os modelos: ${e.message ?: "erro desconhecido"}")
        }

    /** Estado da montagem automática da cadeia gratuita (para a tela de Configurações). */
    var freeModelsBusy by mutableStateOf(false)
        private set

    @Volatile
    private var lastAutoRefreshAttemptAt = 0L

    /** Chamado pela cadeia quando nenhum modelo respondeu: pede nova busca, no máximo uma a cada 10 minutos. */
    private fun requestModelsRefreshAfterFailure() {
        val now = System.currentTimeMillis()
        if (storedSettings?.preset.isNullOrBlank() || now - lastAutoRefreshAttemptAt < MODELS_RETRY_GAP_MS) return
        startupScope.launch { refreshFreeModels(force = true) }
    }

    /**
     * Monta sozinho a cadeia de modelos gratuitos: busca a lista do provedor, sonda os candidatos (só texto→texto) e
     * grava o principal e as reservas. Sem [force], não faz nada se a cadeia atual tem menos de 24 h.
     * [FreeModelsResult.models] traz a cadeia pronta (principal primeiro). Bloqueante: chamar fora da thread principal.
     */
    fun refreshFreeModels(
        force: Boolean = false,
        presetOverride: ProviderPreset? = null,
        keyOverride: String? = null,
    ): FreeModelsResult {
        val saved = storedSettings
        val preset = presetOverride ?: ProviderPreset.fromName(saved?.preset)
        val key = (keyOverride ?: saved?.apiKey).orEmpty().trim()
        if (preset == null) return FreeModelsResult(false, emptyList(), "escolha um provedor gratuito")
        if (key.isBlank()) return FreeModelsResult(false, emptyList(), "informe a chave de API")
        val sameChain = saved != null && saved.preset == preset.name && saved.apiKey == key
        if (!force && sameChain && saved != null && saved.modelsRefreshedAt > 0L &&
            System.currentTimeMillis() - saved.modelsRefreshedAt <= MODELS_MAX_AGE_MS
        ) {
            return FreeModelsResult(true, listOf(saved.model) + saved.fallbackModels, "${1 + saved.fallbackModels.size} modelos prontos")
        }
        synchronized(this) {
            if (freeModelsBusy) return FreeModelsResult(false, emptyList(), "já estou atualizando os modelos")
            freeModelsBusy = true
        }
        lastAutoRefreshAttemptAt = System.currentTimeMillis()
        return try {
            val transport = AiTestHooks.transportOrDefault()
            val found = ModelDiscovery.fetch(preset, key, transport)
            if (found.isEmpty()) {
                FreeModelsResult(false, emptyList(), "nenhum modelo gratuito de texto disponível agora; tente de novo mais tarde")
            } else {
                val chain = ModelProbe.select(preset.chatEndpoint, key, found.map { it.id }, transport, want = FREE_CHAIN_SIZE, maxProbes = FREE_CHAIN_SIZE)
                if (chain.isEmpty()) {
                    FreeModelsResult(false, emptyList(), "nenhum modelo gratuito respondeu agora; tente de novo em alguns minutos")
                } else {
                    val applied =
                        applySettings(
                            ProviderKind.OPENAI_COMPATIBLE, preset.chatEndpoint, chain.first(), key,
                            preset = preset.name, fallbackModels = chain.drop(1),
                            modelsRefreshedAt = System.currentTimeMillis(),
                        )
                    if (applied) {
                        FreeModelsResult(true, chain, "${chain.size} modelos prontos")
                    } else {
                        FreeModelsResult(false, emptyList(), configError ?: "não foi possível salvar a configuração")
                    }
                }
            }
        } catch (e: AiError) {
            FreeModelsResult(false, emptyList(), e.message ?: "falha ao buscar os modelos")
        } catch (e: Exception) {
            FreeModelsResult(false, emptyList(), "não foi possível buscar os modelos: ${e.message ?: "erro desconhecido"}")
        } finally {
            freeModelsBusy = false
        }
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
                    rulesValidated = npc.rulesValidated,
                )
            }
            .sortedBy { it.name }

    /** Resolve disputa jogador-versus-NPC usando os mesmos testes do pacote para ambos os lados. */
    fun resolveOpposedCheck(
        targetId: String,
        checkId: String,
    ): String {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val player = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val target =
            session.state.entities[targetId]?.takeIf { it.kind == EntityKind.NPC && it.alive }
                ?: return "O adversário não existe ou não está disponível."
        val playerChoice =
            pkg.checkChoices(player.stats, skills = player.skills).firstOrNull { it.id == checkId }
                ?: return "O teste selecionado não existe no pacote ativo."
        val targetChoice =
            pkg.checkChoices(target.stats, skills = target.skills).firstOrNull { it.id == checkId }
                ?: return "O adversário não tem dados suficientes para esse teste."
        val result =
            controller?.resolveOpposed(
                playerChoice.spec, targetChoice.spec, player.name, target.name, player.id, target.id,
            ) ?: return "O controlador da campanha não está disponível."
        campaignVersion++
        return when (result) {
            is dev.rpgsolo.ai.PlayResult.Played -> "Disputa resolvida. Revele as rolagens no chat para ver o desfecho."
            is dev.rpgsolo.ai.PlayResult.Failed -> result.reason
        }
    }

    /** Resolve teste de grupo com participantes selecionados e limiar explícito na interface. */
    fun resolveGroupCheck(
        targetIds: List<String>,
        checkId: String,
        requiredSuccesses: Int,
    ): String {
        val pkg = activePackage ?: return "Não há pacote de regras ativo."
        val player = session.state.entities[playerId] ?: return "Personagem do jogador inexistente."
        val uniqueTargets = targetIds.distinct()
        if (uniqueTargets.size != targetIds.size) return "A lista de participantes contém duplicatas."
        val targets =
            uniqueTargets.map { id ->
                session.state.entities[id]?.takeIf { it.kind == EntityKind.NPC && it.alive }
                    ?: return "Participante indisponível: " + id
            }
        val participants = listOf(player) + targets
        if (requiredSuccesses !in 1..participants.size) return "O limiar de sucessos deve ficar entre 1 e o total de participantes."
        val checks =
            participants.map { participant ->
                pkg.checkChoices(participant.stats, skills = participant.skills).firstOrNull { it.id == checkId }?.spec
                    ?: return "O teste '" + checkId + "' não pode ser usado por " + participant.name + "."
            }
        val result =
            controller?.resolveGroup(
                checks = checks,
                requiredSuccesses = requiredSuccesses,
                participantNames = participants.map { it.name },
                participantEntityIds = participants.map { it.id },
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
            val systemRef =
                activeFicha?.system ?: if (activeSystemId != null && activeEditionId != null) {
                    dev.rpgsolo.engine.SystemRef(activeSystemId!!, activeEditionId!!)
                } else {
                    null
                }
            dev.rpgsolo.rules.NpcFactory.create(
                proposal.copy(
                    ruleSource =
                        if (pkg != null) {
                            dev.rpgsolo.rules.RuleSource.PACOTE
                        } else {
                            dev.rpgsolo.rules.RuleSource.IA_NAO_VERIFICADA
                        },
                ),
                pkg,
                systemRef,
            )
        }
        val loop =
            GameLoop(
                r.narrator,
                runner = TurnRunner(r.narrator, npcCreator = npcCreator, canonGuard = canonGuard),
                premiumRunner = if (r.hasPremium) TurnRunner(r.premium, npcCreator = npcCreator, canonGuard = canonGuard) else null,
                menu = activePackage?.let { CheckMenu(it, it.exceptionRegistry()) },
                powerMenu = activePackage?.let { dev.rpgsolo.ai.PowerMenu(it, it.exceptionRegistry()) },
                attackExecutor = { targetId -> combatActions.unifiedAttack(targetId) },
                questExecutor = {
                        q ->
                    if (q.legacy) {
                        addLegacyQuest(q)
                    } else if (q.rival) {
                        addRivalQuest(q)
                    } else {
                        addQuest(q.title, q.objectives, q.xpReward, q.masterySkillId, q.spellRewardId)
                    }
                },
                playerTraits = { activeFicha?.traits.orEmpty() },
                additionalBonus = { scope -> approvedRuleBonus(scope) },
                luckTestBonus = { id -> luckTestBonusOf(id) },
                externalBonuses = { scopes ->
                    bonuses.externalSkillAndCurseBonuses(playerId, scopes.toSet())
                },
                additionalBonusSources = { scopes -> approvedRuleEffects(scopes) },
            )
        return PlayController(
            session,
            loop,
            playerId,
            healthResource =
                activePackage?.manifest?.deathResource
                    ?: session.state.entities[playerId]?.resources?.keys?.firstOrNull()
                    ?: "pv",
            checkFor = checkFor(r.cheap),
            quests = quests,
            restResources =
                activePackage?.data?.resources?.map { it.id }?.toSet()
                    ?: session.state.entities[playerId]?.resources?.keys.orEmpty(),
            restRules =
                dev.rpgsolo.ai.RestRules(
                    partial = setOf("pv", "pm"),
                    campGrade = { e -> constructionGrade(e) },
                    kennelBudget = { companionActions.kennelBudget() },
                    kennelPay = { companionActions.payCommands(it) },
                    breederDegree = { companionActions.breederDegree() },
                ),
            sheet =
                PlayerSheet(activePackage, activeFicha, characterChoicesForAi(activeFicha?.id.orEmpty()), utilities = {
                    bonuses.utilityHints(it.id)
                }),
            conditionNames = activePackage?.data?.conditions?.associate { it.id to it.name }.orEmpty(),
            ruleLookup = { query ->
                val packageRules = activePackage?.search(query, 5)?.map { "${it.kind}: ${it.title} — ${it.text}" }.orEmpty()
                val adaptationRules =
                    session.state.additionalRules.values.filter {
                        query.isBlank() ||
                            listOf(it.name, it.description, it.effects.toString()).any { value ->
                                value.contains(query, ignoreCase = true)
                            }
                    }.map { rule ->
                        "ADAPTAÇÃO ${if (rule.approved) "APROVADA" else "PENDENTE"}: ${rule.name} — ${rule.description}; efeitos ${rule.effects}; limite ${rule.limit}"
                    }
                (packageRules + adaptationRules).take(5)
            },
            chapterSummary = { chapters.summaries().takeLast(3).joinToString("\n").ifBlank { null } },
            resumeBriefing = { dev.rpgsolo.ai.ResumeBriefing.build(quests.all(), quests.timeline(), chapters.summaries()) },
            craftOpportunity = { activePackage?.recipes()?.isNotEmpty() == true },
            sourceContext = { text -> sourceContextForScene(text) },
            mundoContext = {
                listOfNotNull(gameMode().instrucao, legacyContextForMaster(), bodyLootContextForMaster(), reencounterContextForMaster(), legendsContextForMaster(), fameContextForMaster(), wantedContextForMaster(), progressionContextForMaster(), mundoContextForScene()).joinToString(
                    "\n\n",
                ).ifBlank {
                    null
                }
            },
            npcContext = { text -> privateNpcContext(text) },
            sceneNpcIds = { text -> npcIdsForScene(text) },
            generationPreferences = {
                buildString {
                    append(
                        "Preferências de narração do jogador: extensão $narrationLength; estilo $narrationStyle. Preserve as regras, fatos e resultados do motor.",
                    )
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
                        fun labels(ids: List<String>): String =
                            ids.mapNotNull { id ->
                                content.catalog?.entry(id)?.name ?: id
                            }.joinToString(", ")
                        style.settingIds.takeIf { it.isNotEmpty() }?.let {
                            append(
                                "Estilos de ambientação: ",
                            ).append(labels(it)).append(".\n")
                        }
                        style.structureIds.takeIf { it.isNotEmpty() }?.let {
                            append(
                                "Estrutura da campanha: ",
                            ).append(labels(it)).append(".\n")
                        }
                        style.philosophyIds.takeIf { it.isNotEmpty() }?.let {
                            append(
                                "Filosofia de jogo: ",
                            ).append(labels(it)).append(".\n")
                        }
                        style.toneIds.takeIf { it.isNotEmpty() }?.let { append("Tom: ").append(labels(it)).append(".\n") }
                        style.difficultyId?.let {
                                id ->
                            append("Dificuldade escolhida: ").append(content.catalog?.entry(id)?.name ?: id).append(".\n")
                        }
                        style.difficultyParams.takeIf { it.isNotEmpty() }?.let { params ->
                            append(
                                "Parâmetros de dificuldade: ",
                            ).append(params.entries.joinToString(", ") { "${it.key}=${it.value}" }).append(".\n")
                            params["resource_scarcity"]?.let { value ->
                                append("Escassez de recursos (0 abundante, 10 escasso): ").append(value)
                                    .append(
                                        ". Ajuste a frequência narrativa de recursos e saques a esta escala, sem inventar itens mecânicos fora do pacote.\n",
                                    )
                            }
                            params["defeat_consequence"]?.let { value ->
                                append("Consequências de derrota (0 leves, 10 severas): ").append(value)
                                    .append(
                                        ". Use contratempos ficcionais proporcionais, sem contrariar o estado ou os resultados do motor.\n",
                                    )
                            }
                        }
                        style.progressionId?.let {
                                id ->
                            append("Progressão: ").append(content.catalog?.entry(id)?.name ?: id).append(".\n")
                        }
                        style.contentLimits.takeIf { it.isNotEmpty() }?.let { limits ->
                            append(
                                "LIMITES DE CONTEÚDO OBRIGATÓRIOS DO JOGADOR: ",
                            ).append(labels(limits)).append(". Nunca ultrapasse nem tente contornar estes limites.\n")
                        }
                    }
                    session.state.ambientacao?.let { setting ->
                        append("Ambientação ficcional da campanha: ").append(setting.name)
                        setting.tone.takeIf { it.isNotBlank() }?.let { append("; tom: ").append(it) }
                        setting.technology.takeIf { it.isNotBlank() }?.let { append("; tecnologia: ").append(it) }
                        setting.organizations.takeIf { it.isNotEmpty() }?.let { append("; organizações: ").append(it.joinToString(", ")) }
                        setting.conflicts.takeIf { it.isNotEmpty() }?.let { append("; conflitos: ").append(it.joinToString(", ")) }
                        setting.fictionalGear.takeIf { it.isNotEmpty() }?.let {
                            append(
                                "; equipamentos ficcionais: ",
                            ).append(it.joinToString(", "))
                        }
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
                            val exits =
                                links.filter { it in session.state.knownPlaces }
                                    .mapNotNull { session.state.places[it]?.name }
                            append("\nSaídas conhecidas: ").append(exits.joinToString(", ").ifEmpty { "nenhuma" })
                            val unknownExits = links.count { it !in session.state.knownPlaces }
                            if (unknownExits > 0) {
                                append(
                                    "; há ",
                                ).append(unknownExits).append(" passagem(ns) ainda desconhecida(s), não revele seus nomes.")
                            }
                        }
                    }
                }.toString().ifBlank { null }
            },
        )
    }

    /** A cena inclui NPCs do combate e os nomes/ids mencionados explicitamente pelo jogador. */
    private fun npcIdsForScene(text: String): Set<String> {
        val query = text.trim().lowercase()
        val inCombat = session.state.combat?.order.orEmpty().filter { it != playerId }
        val mentioned =
            session.state.entities.values.filter { npc ->
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
            val traits =
                npc.traits.mapNotNull { traitId ->
                    activePackage?.trait(traitId)?.let { "${it.name}: ${it.description}" }
                }
            val dossier = session.state.npcDossiers[id]
            buildString {
                append("Ficha privada de NPC — ").append(npc.name)
                if (!npc.rulesValidated) append("; ficha NÃO validada por pacote")
                if (npc.concept.isNotBlank()) append("; conceito: ").append(npc.concept)
                if (npc.stats.isNotEmpty()) {
                    append(
                        "; atributos mecânicos: ",
                    ).append(npc.stats.entries.joinToString(", ") { "${it.key}=${it.value}" })
                }
                if (traits.isNotEmpty()) append("; traços: ").append(traits.joinToString(" | "))
                if (npc.skills.isNotEmpty()) {
                    append(
                        "; perícias: ",
                    ).append(npc.skills.entries.joinToString(", ") { "${it.key}=${it.value}" })
                }
                dossier?.personality?.takeIf { it.isNotBlank() }?.let { append("; personalidade: ").append(it) }
                dossier?.goals?.takeIf { it.isNotEmpty() }?.let { append("; objetivos: ").append(it.joinToString(", ")) }
                dossier?.motivations?.takeIf { it.isNotEmpty() }?.let { append("; motivações: ").append(it.joinToString(", ")) }
                dossier?.relations?.takeIf {
                    it.isNotEmpty()
                }?.let {
                    append(
                        "; relações privadas: ",
                    ).append(it.toSortedMap().entries.joinToString(", ") { pair -> "${pair.key}: ${pair.value}" })
                }
                if (dossier?.secrets?.isNotEmpty() == true) {
                    val undiscovered =
                        dossier.secrets.filterNot { secret ->
                            session.state.knowledge.containsKey("npc-secret:$id:${secret.id}")
                        }
                    if (undiscovered.isNotEmpty()) {
                        append("; ids de segredos ainda ocultos: ").append(undiscovered.joinToString(", ") { it.id })
                        append("; seus textos não foram enviados; só podem ser revelados após teste autorizado")
                    }
                }
                // MP-01: só o que este NPC viveu ou descobriu (o motor decide; UNKNOWN não entra).
                dev.rpgsolo.engine.MilestoneContext.forNpc(session.state, id)?.let { append("; ").append(it.replace('\n', ' ')) }
                append(". Nunca exponha estes números nem nomes de mecânica ao jogador.")
            }
        }

    /**
     * CG-5.4: cria e salva a ficha de um herói pronto (Jogo rápido, sem a tela de criação) no pacote [pacoteId].
     * Devolve a ficha ou `null` se o pacote não existir ou a ficha não for válida. Chamada bloqueante.
     */
    fun criarFichaDeHeroi(
        heroi: HeroiPronto,
        pacoteId: String,
    ): Ficha? {
        val pkg = content.registry.get(pacoteId) ?: return null
        val draft = dev.rpgsolo.rules.CharacterDraft(pkg, newFichaId())
        val est = EstadoCriacao()
        CriacaoJogoRapido.preparar(heroi, 0, est, draft, pkg)
        val b = draft.build() as? dev.rpgsolo.rules.FichaBuild.Created ?: return null
        saveFicha(b.ficha)
        saveCharacterChoices(b.ficha.id, est.escolhas())
        registrarConquista(Conquista.PRIMEIRO_HEROI)
        registrarConquista(Conquista.HEROI_PRONTO)
        return b.ficha
    }

    /** Fluxo "Nova campanha" (9.1). null até haver provedor de IA configurado. */
    fun newCampaignFlow(): NewCampaignFlow? {
        val r = roles ?: return null
        val catalog = content.catalog ?: return null
        return NewCampaignFlow(CampaignInterpreter(r.cheap), catalog, content.registry, store)
    }

    /** Mundo ligado à campanha ativa, ou `null` (campanha sem mundo persistente ou ainda sem campanha). Chamada bloqueante. */
    fun activeWorld(): dev.rpgsolo.world.MundoRegistro? = runCatching { worldStore.mundoDaCampanha(session.campaignId) }.getOrNull()

    /** MI-17: aplica o período simulado à campanha ativa (um lote só). Chamada bloqueante. */
    fun applySimulation(
        diaInicio: Int,
        diaFim: Int,
        feed: dev.rpgsolo.world.FeedSimulacao,
    ): ResultadoAplicacao {
        val world = activeWorld()
        val estado = world?.let { runCatching { worldStore.ultimoSnapshot(it.id)?.snapshot?.estado }.getOrNull() }
        val heroi =
            IdadeHeroi.estado(session.state)?.let {
                dev.rpgsolo.world.MotorHeroi.simular(MythralisRuntime.dinamica(), it, world?.semente ?: 0L, diaInicio, diaFim)
            }
        return AplicacaoSimulacao.aplicar(session, diaInicio, diaFim, feed, estado, heroi)
    }

    /**
     * MI-26: ao iniciar a campanha com mundo, fixa a idade em anos informada na ficha no dia do mundo (limitada à
     * `vida_max` da classe de longevidade do povo: sub-raça, senão raça) e registra o sexo. Sem mundo, sem raça ou povo
     * sem idade, não faz nada. Falha só vira aviso.
     */
    private fun applyHeroAgeFromFicha(choices: CharacterChoices) {
        // O sexo informado na criação vira fato da campanha (não depende de mundo).
        if (choices.sex.isNotBlank()) session.execute(dev.rpgsolo.engine.RecordFact("heroi.sexo", choices.sex))
        if (choices.raceId.isBlank() || activeWorld() == null) return
        runCatching {
            val din = MythralisRuntime.dinamica()
            val classe = din.classeDe(choices.raceId, choices.subraceId)
            val vidaMax = din.classes[classe]?.max ?: 0
            if (vidaMax <= 0) return
            // Idade informada pelo jogador; ficha antiga sem idade usa adulto. Acima da longevidade do povo, ajusta e avisa.
            val anos =
                if (choices.age > 0) choices.age.coerceAtMost(vidaMax - 1) else FaixaIdade.ADULTO.anos(vidaMax)
            if (choices.age > anos) configError = "idade ajustada para $anos anos (longevidade do povo: $vidaMax)"
            if (anos > 0) setHeroAge(anos, classe)?.let { configError = "idade do herói não definida: $it" }
        }.onFailure { configError = "idade do herói não definida: ${it.message}" }
    }

    /** MI-27: o herói morre de velhice em (diaInicio, diaFim]? Mesma conta do Aplicar (semente do mundo). Chamada bloqueante. */
    fun heroDeathIn(
        diaInicio: Int,
        diaFim: Int,
    ): MorteHeroi? = IdadeHeroi.previsaoDeMorte(session.state, MythralisRuntime.dinamica(), activeWorld()?.semente ?: 0L, diaInicio, diaFim)

    /** MI-27: texto do resumo da morte do herói (idade e causa). */
    fun heroDeathSummary(morte: MorteHeroi): String = IdadeHeroi.resumoDaMorte(session.state, morte)

    /** MI-26: fixa a idade do herói (em anos) no dia atual do mundo e a classe de longevidade do povo. `null` = deu certo. */
    fun setHeroAge(
        anos: Int,
        classe: String,
    ): String? {
        val diaMundo = session.state.facts[AplicacaoSimulacao.CHAVE_DIA]?.toIntOrNull() ?: activeWorld()?.proximoDia ?: 0
        val r = session.executeAll(IdadeHeroi.definir(anos, diaMundo, classe))
        return (r as? dev.rpgsolo.engine.ExecResult.Rejected)?.reason
    }

    /** MI-17: desfaz o lote aplicado (dia e fatos da campanha). Chamada bloqueante. */
    fun undoSimulation(lote: LoteAplicado) = AplicacaoSimulacao.desfazer(session, lote)

    /**
     * MI-18: leva a cidade [cityId] do mundo para a campanha ativa (local, NPCs vivos necessários e facções presentes),
     * lendo o último snapshot do mundo. `null` = deu certo ou já estava lá. Chamada bloqueante (carrega o molde).
     */
    fun instantiateCity(cityId: String): String? {
        val world = activeWorld() ?: return "a campanha não tem mundo persistente"
        val estado = worldStore.ultimoSnapshot(world.id)?.snapshot?.estado
        val cena = dev.rpgsolo.world.InstanciacaoCena.cena(MythralisRuntime.mundo, estado, cityId) ?: return "cidade inexistente no mundo: $cityId"
        val erro = InstanciacaoCampanha.instanciar(session, cena)
        val vida = VidaForaDeCena.comandos(session.state, estado)
        val erroVida =
            if (erro == null && vida.isNotEmpty()) {
                (
                    session.executeAll(
                        vida,
                    ) as? dev.rpgsolo.engine.ExecResult.Rejected
                )?.reason
            } else {
                null
            }
        if (erro == null && erroVida == null) campaignVersion++
        return erro ?: erroVida
    }

    /**
     * Progressão contada na história: o Mestre costura os ganhos (perícia, status, habilidade) na narrativa, no momento
     * oportuno, sem tratar como missão formal. Perícia só sobe por skill_trial, validado pelo motor.
     */

    /** LG-02: legados dos heróis mortos como fato fixo para o mestre (nada vem da IA). */
    private fun legacyContextForMaster(): String? =
        runCatching {
            emitLegacyPins()
            listOfNotNull(
                dev.rpgsolo.engine.LegacyMemory.masterContext(session.state.legacies.values, chapters.chapters),
                legacyQuestOffer()?.let { dev.rpgsolo.engine.LegacyQuests.masterInstruction(it) },
            ).joinToString("\n\n").ifBlank { null }
        }.getOrNull()

    /**
     * LG-02: grava pelo motor as notas fixadas dos NPCs vivos com vínculo ao herói morto ([dev.rpgsolo.engine.LegacyMemory.pendingPins]).
     * Idempotente: depois de gravadas não há pendência. ASSUMINDO: chamada ao montar o contexto do mestre (a cada jogada); cada
     * nota é executada isoladamente, então uma recusa não impede as demais; falha nunca interrompe a jogada.
     */
    private fun emitLegacyPins() {
        val pins =
            session.state.legacies.values.flatMap { dev.rpgsolo.engine.LegacyMemory.pendingPins(it, session.state) }
        if (pins.isEmpty()) return
        var gravou = false
        pins.forEach { pin ->
            if (session.execute(pin) is dev.rpgsolo.engine.ExecResult.Accepted) gravou = true
        }
        if (gravou) campaignVersion++
    }

    /**
     * LG-04: o mestre só narra a recuperação de itens de corpos. Quem pega os itens é o motor (`TransferBodyItems`, com
     * teste de destreza e turno gasto em combate); a IA não entrega os itens do corpo por `add_item` nem inventa o
     * resultado do teste. Lista só os corpos com itens ainda não recolhidos.
     */
    private fun bodyLootContextForMaster(): String? =
        runCatching {
            val bodies = combatActions.bodies().filter { it.itemCount > 0 }
            if (bodies.isEmpty()) return@runCatching null
            val emCombate = session.state.combat != null
            "ITENS EM CORPOS (estado do motor): " + bodies.joinToString("; ") { "${it.name} (${it.itemCount} item(ns))" } + ". " +
                "Só o jogador recupera estes itens, pelo painel do app" +
                (if (emCombate) " (em combate gasta o turno e exige teste de destreza; se falhar, o adversário acerta)" else " (fora de combate é só pegar)") +
                ". Narre o que o motor decidir; não entregue estes itens com add_item e não decida o resultado do teste."
        }.getOrNull()

    /**
     * MP-07: os rumores viajam pelas passagens com o passar dos dias. O dia da última propagação fica no fato
     * `rumores.dia` (estado do motor, sem migração), gravado no mesmo lote de [dev.rpgsolo.engine.SpreadRumors].
     * ASSUMINDO: sem o fato, conta a partir do rumor mais antigo; rumor iniciado depois da última propagação também
     * envelhece pelos dias desde ela (aproximação aceita).
     */
    private fun advanceRumors() {
        val state = session.state
        if (state.rumors.isEmpty()) return
        val last = state.facts[RUMOR_DAY_FACT]?.toIntOrNull() ?: state.rumors.values.minOf { it.startedOnDay }
        val days = state.day - last
        if (days < 1) return
        val capped = days.coerceAtMost(dev.rpgsolo.engine.FAME_JUMP_DAYS_MAX)
        val result =
            session.executeAll(
                listOf(dev.rpgsolo.engine.SpreadRumors(capped), dev.rpgsolo.engine.RecordFact(RUMOR_DAY_FACT, state.day.toString())),
            )
        if (result is dev.rpgsolo.engine.ExecResult.Accepted) campaignVersion++
    }

    /**
     * MP-07: o que o lugar atual sabe do herói, decidido pelo motor ([dev.rpgsolo.engine.Fame.effects] e rumores com a força
     * que chegou ali). A veracidade do rumor não vai ao mestre; os números não são citados ao jogador.
     * ASSUMINDO: preço e relação inicial entram como orientação ao mestre (a loja do app não aplica o ajuste de preço).
     */
    private fun fameContextForMaster(): String? =
        runCatching {
            advanceRumors()
            val place = session.state.currentPlaceId ?: return@runCatching null
            val fx = dev.rpgsolo.engine.Fame.effects(session.state, place)
            val rumors = dev.rpgsolo.engine.Fame.rumorsAt(session.state, place)
            if (!fx.recognized && rumors.isEmpty() && fx.priceModPercent == 0 && fx.initialRelationBonus == 0) return@runCatching null
            buildString {
                append("FAMA NESTE LUGAR (decidida pelo motor): ")
                append(if (fx.recognized) "o herói costuma ser reconhecido aqui. " else "ninguém o reconhece de imediato. ")
                if (fx.initialRelationBonus > 0) append("Os moradores tendem a recebê-lo bem. ")
                if (fx.initialRelationBonus < 0) append("Os moradores tendem a desconfiar dele. ")
                if (fx.priceModPercent > 0) append("O comércio local cobra um pouco mais dele. ")
                if (fx.priceModPercent < 0) append("O comércio local lhe faz um preço melhor. ")
                if (rumors.isNotEmpty()) {
                    append("Rumores que circulam aqui (podem estar exagerados ou errados; nunca os trate como fato): ")
                    append(rumors.joinToString("; ") { it.text })
                    append(". ")
                }
                append("Não invente fama além disto nem cite números.")
            }
        }.getOrNull()

    /**
     * MP-06: o motor sobe os degraus do procurado contra o herói quando o prazo passa ([dev.rpgsolo.engine.Wanted.dueEscalations]);
     * a IA não escala. Chamada ao montar o contexto do mestre; idempotente. ASSUMINDO: grupo = o herói (nível dele).
     */
    private fun advanceWanted() {
        val level = session.state.entities[playerId]?.level ?: 1
        val due = dev.rpgsolo.engine.Wanted.dueEscalations(session.state, playerId, level)
        var subiu = false
        due.forEach { if (session.execute(it) is dev.rpgsolo.engine.ExecResult.Accepted) subiu = true }
        if (subiu) campaignVersion++
    }

    /** MP-06: procurados contra o herói que ainda importam (ativos ou escondidos), para a tela de respostas. */
    fun wantedList(): List<dev.rpgsolo.engine.WantedRecord> =
        session.state.wanted.values
            .filter {
                it.targetId == playerId && (dev.rpgsolo.engine.Wanted.isLive(it, session.state.day) || it.status == dev.rpgsolo.engine.WantedStatus.HIDDEN)
            }
            .sortedBy { it.id }

    /** MP-06: recompensa já com o decaimento do dia. */
    fun wantedBounty(r: dev.rpgsolo.engine.WantedRecord): Int = dev.rpgsolo.engine.Wanted.effectiveBounty(r, session.state.day)

    /**
     * MP-06: resposta do jogador ao procurado. Pagar custa a recompensa atual e subornar a metade, em moedas do pacote,
     * descontadas no mesmo lote da resposta (recusa do motor desfaz tudo). Devolve `null` se aceitou ou o motivo da recusa.
     */
    fun respondWanted(
        id: String,
        response: dev.rpgsolo.engine.WantedResponse,
    ): String? {
        val r = session.state.wanted[id] ?: return "procurado inexistente"
        val bounty = dev.rpgsolo.engine.Wanted.effectiveBounty(r, session.state.day)
        val amount =
            when (response) {
                dev.rpgsolo.engine.WantedResponse.PAY -> bounty
                dev.rpgsolo.engine.WantedResponse.BRIBE -> bounty / 2
                else -> 0
            }
        val commands = mutableListOf<dev.rpgsolo.engine.Command>()
        if (amount > 0) {
            val currency = activePackage?.currencyItemId ?: return "o pacote não define moeda"
            val have = session.state.inventory[playerId]?.get(currency) ?: 0
            if (have < amount) return "moedas insuficientes ($have de $amount)"
            commands += dev.rpgsolo.engine.ConsumeItem(playerId, currency, amount)
        }
        commands += dev.rpgsolo.engine.RespondWanted(id, response, amount)
        return when (val result = session.executeAll(commands)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** MP-06: procurados ativos contra o herói, com agentes escalonados pelo nível atual do herói (nunca pelo do Boss). */
    private fun wantedContextForMaster(): String? =
        runCatching {
            advanceWanted()
            dev.rpgsolo.engine.Wanted.masterContext(session.state, playerId, session.state.entities[playerId]?.level ?: 1)
        }.getOrNull()

    /**
     * MP-04: reencontro decidido pelo motor, no máximo um NPC recorrente por campanha (com a vida fora de cena do MP-03 aplicada, WD-10). ASSUMINDO: campanhas fora =
     * campanhas do mundo menos as que já registraram o NPC; aparições = registros menos 1; marcos novos = `RecurringCharacter.newMilestones` (MP-04). O nível usa o nível do herói (a escala do pacote entra quando o pacote expuser a função).
     */
    private fun reencounterPlan(): dev.rpgsolo.engine.ReencounterPlan? =
        runCatching {
            val worldId = worldStore.vinculo(session.campaignId)?.mundoId ?: return@runCatching null
            val total = worldStore.campanhasDoMundo(worldId)
            val heroLevel = session.state.entities[playerId]?.level ?: 1
            val recurring = store.recurringCharacters(worldId)
            // WD-10: vida fora de cena (MP-03) calculada ao montar o contexto, de forma determinística (semente do motor),
            // com os lugares do mundo desta campanha e os outros recorrentes como opções. Não é gravada: o mesmo avanço
            // é recalculado igual a cada chamada, então nunca soma passos duas vezes.
            val places = session.state.places.values.map { it.name }
            val candidates =
                recurring.map { c ->
                    val away = (total - c.campaigns).coerceAtLeast(0)
                    val peers = recurring.filter { it.identityKey != c.identityKey }.map { it.name }
                    val advanced = dev.rpgsolo.engine.OffscreenRules.advance(c, away, session.engine.seed, places, peers)
                    dev.rpgsolo.engine.ReencounterCandidate(
                        advanced,
                        campaignsAway = total - c.campaigns,
                        appearances = (c.campaigns - 1).coerceAtLeast(0),
                        newMilestones = c.newMilestones,
                    )
                }
            dev.rpgsolo.engine.Reencounter.plan(session.state, playerId, candidates, session.engine.seed) {
                dev.rpgsolo.engine.ReencounterLevel.forCharacter(it, heroLevel)
            }
        }.getOrNull()

    private fun reencounterContextForMaster(): String? =
        runCatching {
            val plan = reencounterPlan() ?: return@runCatching null
            listOfNotNull(
                dev.rpgsolo.engine.Reencounter.masterContext(plan),
                dev.rpgsolo.engine.OffscreenRules.masterContext(plan.character),
                dev.rpgsolo.engine.RelationAxes.contextOf(plan.character, playerId),
                rivalQuestOffer(plan)?.let { dev.rpgsolo.engine.RivalQuests.masterInstruction(it) },
            ).joinToString("\n")
        }.getOrNull()

    /** RV-05: oferta de quest de rival para o reencontro planejado (gatilho e papel do motor, RV-04). */
    private fun rivalQuestOffer(plan: dev.rpgsolo.engine.ReencounterPlan? = reencounterPlan()): dev.rpgsolo.engine.RivalQuestOffer? =
        runCatching { dev.rpgsolo.engine.RivalQuests.offer(plan, quests.all(), session.state.day) }.getOrNull()

    /** RV-05: registra a quest de rival oferecida; recusa se não há oferta e limita o XP pelo teto do motor. */
    private fun addRivalQuest(q: dev.rpgsolo.ai.QuestRequest): String? {
        val offer = rivalQuestOffer() ?: return "não há quest de rival oferecida agora; não use \"rival\":true"
        val player = session.state.entities[playerId]
        val cap = dev.rpgsolo.engine.RivalQuests.xpCap(offer, session.engine.config.maxQuestXp(player?.level ?: 1))
        return addQuest(
            q.title,
            q.objectives,
            q.xpReward.coerceIn(0, cap),
            q.masterySkillId,
            q.spellRewardId,
            idOverride = dev.rpgsolo.engine.RivalQuests.questId(offer),
        )
    }

    /** LG-03: oferta de quest de legado para hoje (gatilho do motor; a IA só propõe o que foi oferecido). */
    private fun legacyQuestOffer(): dev.rpgsolo.engine.LegacyQuestOffer? =
        dev.rpgsolo.engine.LegacyQuests.evaluate(session.state, playerId, quests.all(), session.engine.seed)

    /** LG-03: registra a quest de legado oferecida; recusa se não há oferta e limita o XP pelo teto do motor. */
    private fun addLegacyQuest(q: dev.rpgsolo.ai.QuestRequest): String? {
        val offer = legacyQuestOffer() ?: return "não há quest de legado oferecida agora; não use \"legacy\":true"
        val player = session.state.entities[playerId]
        val cap = dev.rpgsolo.engine.LegacyQuests.xpCap(offer, session.engine.config.maxQuestXp(player?.level ?: 1))
        return addQuest(
            q.title,
            q.objectives,
            q.xpReward.coerceIn(0, cap),
            q.masterySkillId,
            q.spellRewardId,
            idOverride = dev.rpgsolo.engine.LegacyQuests.questId(offer.heroId, offer.step, offer.day),
        )
    }

    private fun progressionContextForMaster(): String? {
        val player = session.state.entities[playerId] ?: return null
        val trial = skillTrialState()
        val linhas = mutableListOf<String>()
        val nivel = trial.pendingLevels.firstOrNull()
        if (nivel != null) {
            val dificuldade = nivel / trial.every
            val opcoes =
                trial.skills.filter { it.value < it.max }
                    .joinToString("; ") { "${it.id} (${it.name}, nível ${it.value}/${it.max}: ${it.description.take(120)})" }
            linhas +=
                "Perícia pendente (nível $nivel, dificuldade $dificuldade): quando a cena der espaço, sem pressa e sem chamar de prova ou missão, " +
                "crie um acontecimento da própria história (armadilha, travessia, duelo, fuga...) que exija PELO MENOS 3 testes seguidos de UMA perícia que combine com a cena, " +
                "usando request_check com dificuldade proporcional a $dificuldade. Só conta se o jogador passar na maioria desses testes: nesse caso, ao final use a proposta " +
                "skill_trial(entityId=\"$playerId\", skillId, level=$nivel) e anuncie na narrativa que a perícia subiu de nível, descrevendo os novos bônus. " +
                "Se ele falhar, não use skill_trial: a oportunidade continua para mais tarde. Perícias possíveis: $opcoes."
        }
        val status = statPointsState().statPoints
        val habilidade = session.state.entities[playerId]?.abilityPoints ?: 0
        if (status > 0 || habilidade > 0) {
            linhas +=
                "O personagem tem pontos novos para distribuir (status: $status, habilidade: $habilidade). Quando for natural, " +
                "mostre na narrativa que ele evoluiu (mais forte, mais ágil, um insight novo) e lembre que pode distribuir os pontos nos painéis; não é missão."
        }
        return if (linhas.isEmpty()) null else "PROGRESSÃO (integre à história):\n" + linhas.joinToString("\n")
    }

    @Volatile private var mundoContextCache: Pair<String, String?>? = null

    /** MI-21: cânone do mundo da cena atual (atualizado com o contexto do mestre a cada turno). */
    @Volatile private var canonMundoAtual: List<dev.rpgsolo.world.FatoCanonMundo> = emptyList()

    private val canonGuard =
        object : dev.rpgsolo.ai.CanonGuard {
            override fun recusaFato(
                chave: String,
                valor: String,
            ) = dev.rpgsolo.world.CanonMundo.recusa(canonMundoAtual, chave, valor)

            override fun recusaNpc(id: String) = dev.rpgsolo.world.CanonMundo.recusaNpc(canonMundoAtual, id)
        }

    /**
     * MI-20: contexto do mundo só do lugar atual (se for cidade do molde), lendo o último snapshot; guarda o texto
     * enquanto mundo, bloco simulado e lugar não mudam. Chamada bloqueante (fora da thread principal, como o turno).
     */
    private fun mundoContextForScene(): String? {
        val place = session.state.currentPlaceId ?: return null
        val world = activeWorld() ?: return null.also { canonMundoAtual = emptyList() }
        val chave = "${world.id}|${world.ultimoBloco}|$place"
        mundoContextCache?.let { if (it.first == chave) return it.second }
        val estado = runCatching { worldStore.ultimoSnapshot(world.id)?.snapshot?.estado }.getOrNull()
        val molde = MythralisRuntime.mundo
        val cena = dev.rpgsolo.world.InstanciacaoCena.cena(molde, estado, place)
        val cronica = runCatching { worldStore.cronica(world.id) }.getOrDefault(emptyList())
        val canon = cena?.let { dev.rpgsolo.world.CanonMundo.fatos(molde, estado, it, cronica) }.orEmpty()
        canonMundoAtual = canon
        runCatching { CanonCampanha.sincronizar(this, session.campaignId, world.id, canon) }
        val texto = cena?.let { dev.rpgsolo.world.ContextoMestre.cena(molde, it, canon = canon) }
        mundoContextCache = chave to texto
        return texto
    }

    /** Armazenamento do mundo para a tela Simulação de tempo (MI-15). */
    fun worldStoreForSimulation(): dev.rpgsolo.world.MundoStore = worldStore

    /**
     * MI-10: se a campanha nasceu vinculada a um molde de mundo (fatos `mundo.*` gravados pelo `CampaignStarter`),
     * cria o mundo da campanha no Room e liga os dois. Um mundo por campanha; o compartilhamento é o MI-12.
     */
    private fun linkWorld(started: StartResult.Started) {
        val facts = started.session.state.facts
        val moldeId = facts["mundo.id"] ?: return
        val now = System.currentTimeMillis()
        val worldId = "mundo-${started.campanha.id}"
        runCatching {
            worldStore.criarMundo(
                dev.rpgsolo.world.MundoRegistro(
                    id = worldId,
                    moldeId = moldeId,
                    moldeHash = facts["mundo.molde_hash"].orEmpty(),
                    dinamicaHash = facts["mundo.dinamica_hash"].orEmpty(),
                    semente = started.record.seed,
                    criadoEm = now,
                    atualizadoEm = now,
                ),
            )
            worldStore.vincular(dev.rpgsolo.world.VinculoMundo(started.campanha.id, worldId, moldeId, now))
        }.onFailure { configError = "mundo não vinculado à campanha: ${it.message}" }
    }

    /** Criação por abas: gasta os pontos iniciais de habilidade nas evoluções escolhidas (1 ponto cada, validado pelo motor). */
    private fun applyInitialSkillUpgrades(choices: CharacterChoices) {
        val upgrades = choices.initialSkillUpgrades.filterValues { it > 0 }
        if (upgrades.isEmpty()) return
        val limit = session.engine.config.initialAbilityPoints
        if (upgrades.values.sum() > limit) {
            configError = "evoluções iniciais acima do limite de $limit pontos"
            return
        }
        grantInitialAbilityPoints()?.let {
            configError = "pontos iniciais de habilidade não concedidos: $it"
            return
        }
        val actions = skillTreeActions()
        for ((skillId, times) in upgrades) {
            repeat(times) {
                actions.unlockOrUpgrade(skillId)?.let { configError = "evolução inicial não aplicada ($skillId): $it" }
            }
        }
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
        val loadoutTreeIds =
            (
                listOf(
                    loadoutChoices.raceId, loadoutChoices.subraceId, loadoutChoices.hybridId,
                    loadoutChoices.classId, loadoutChoices.professionId, loadoutChoices.cursedClassBranchId,
                ) + loadoutChoices.additionalClassIds + loadoutChoices.mixedRaceIds
            ).filter { it.isNotBlank() }.toSet()
        when (
            val loadoutResult =
                session.execute(
                    SetSkillTreeLoadout(
                        playerId,
                        loadoutTreeIds,
                        loadoutClassOrder,
                        loadoutChoices.elementalAffinity,
                    ),
                )
        ) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            is dev.rpgsolo.engine.ExecResult.Rejected -> configError = "loadout de habilidades inválido: ${loadoutResult.reason}"
        }
        runCatching { skillTreeActions().grantSelectedFreeBaseSkills() }
            .onFailure { configError = "falha ao conceder habilidades básicas: ${it.message}" }
            .getOrNull()?.let { configError = "habilidades básicas não concedidas: $it" }
        applyInitialSkillUpgrades(loadoutChoices)
        when (val setting = session.execute(dev.rpgsolo.engine.SetAmbientacao(started.campanha.ambientacao))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            is dev.rpgsolo.engine.ExecResult.Rejected -> configError = "ambientação não salva: ${setting.reason}"
        }
        when (val style = session.execute(dev.rpgsolo.engine.SetCampaignStyle(started.campanha.style))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            is dev.rpgsolo.engine.ExecResult.Rejected -> configError = "configuração da campanha não salva: ${style.reason}"
        }
        linkWorld(started)
        applyHeroAgeFromFicha(loadoutChoices)
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
                session.advanceClocksAfterChapterClosure()
                saveMetadata()
                campaignVersion++
                null
            }
            is ChapterSummarizer.Result.Failed -> r.reason
        }
    }

    /** Estado de evolução com limites vindos do pacote ativo; nunca inventa opções para sistema sem pacote. */
    fun advancementState(): AdvancementState {
        val player =
            session.state.entities[playerId]
                ?: return AdvancementState(0, emptyList())
        val pkg = activePackage ?: return AdvancementState(player.advancementPoints, emptyList())
        val stats =
            pkg.data.stats.map { stat ->
                AdvancementChoice(
                    stat.id,
                    stat.name,
                    player.stats[stat.id] ?: session.engine.config.statDefaults[stat.id] ?: stat.default,
                    session.engine.config.statCaps[stat.id] ?: 0,
                    skill = false,
                )
            }
        val skills =
            pkg.data.traits.filter { skill ->
                skill.type == "pericia" &&
                    ((player.skills[skill.id] ?: 0) > 0 || pkg.canImproveSkill(skill.id, player.stats, player.traits))
            }.map { skill ->
                AdvancementChoice(
                    skill.id,
                    skill.name,
                    player.skills[skill.id] ?: 0,
                    session.engine.config.skillCaps[skill.id] ?: 0,
                    skill = true,
                )
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
        val stats =
            pkg.data.stats.filter { it.id in player.stats }.map { stat ->
                AdvancementChoice(stat.id, stat.name, player.stats[stat.id] ?: stat.default, 0, skill = false)
            }
        return StatPointsState(player.statPoints, player.abilityPoints, stats)
    }

    /** Ações do catálogo de maldições lançadas durante a aventura. */
    fun curseActions(): CurseActions =
        CurseActions(
            session = { session },
            activePackage = { activePackage },
            playerId = { playerId },
            checkBonuses = { entityId, scopes -> bonuses.externalSkillAndCurseBonuses(entityId, scopes) },
            onChanged = { campaignVersion++ },
        )

    /** Estado da distribuição única de 12 pontos iniciais. */
    fun initialBuildState(): InitialBuildState {
        val player = session.state.entities[playerId] ?: return InitialBuildState(false, emptyList())
        val statCaps = activePackage?.engineConfig()?.statCaps.orEmpty()
        val stats =
            activePackage?.data?.stats.orEmpty().filter { it.id in player.stats }.map { stat ->
                AdvancementChoice(
                    stat.id,
                    stat.name,
                    player.stats[stat.id] ?: stat.default,
                    statCaps[stat.id] ?: (player.stats[stat.id] ?: stat.default),
                    skill = false,
                )
            }
        val config = session.engine.config
        return InitialBuildState(
            player.initialBuildAllocated,
            stats,
            config.initialStatPoints,
            config.initialAbilityPoints,
            player.initialAbilityGranted,
        )
    }

    /** O motor exige exatamente os pontos iniciais do pacote (10 de status + 2 de habilidade) e impede repetir a distribuição. */
    fun allocateInitialBuild(
        statIncreases: Map<String, Int>,
        skillPoints: Int,
    ): String? =
        when (val result = session.execute(dev.rpgsolo.engine.AllocateInitialBuild(playerId, statIncreases, skillPoints))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    /** Árvore de status: distribui só os pontos iniciais de status (não mexe nas habilidades). */
    fun allocateInitialStats(statIncreases: Map<String, Int>): String? =
        when (val result = session.execute(dev.rpgsolo.engine.AllocateInitialStats(playerId, statIncreases))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    /** Árvore de habilidades: concede os pontos iniciais de habilidade (não depende do status). */
    fun grantInitialAbilityPoints(): String? =
        when (val result = session.execute(dev.rpgsolo.engine.GrantInitialAbilityPoints(playerId))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    /** Ações da árvore vinculadas às escolhas salvas na ficha ativa. */
    fun skillTreeActions(): SkillTreeActions {
        val choices = characterChoices(activeFichaId ?: activeFicha?.id.orEmpty())
        val orderedClasses = (listOf(choices.classId).filter { it.isNotBlank() } + choices.additionalClassIds).distinct()
        val selected =
            (
                listOf(
                    choices.raceId, choices.subraceId, choices.classId, choices.professionId, choices.hybridId, choices.cursedClassBranchId,
                ) + choices.additionalClassIds + choices.mixedRaceIds
            ).filter { it.isNotBlank() }.toSet()
        val affinity =
            runCatching {
                if (choices.elementalAffinity.isBlank()) {
                    dev.rpgsolo.engine.ElementPath.NONE
                } else {
                    dev.rpgsolo.engine.ElementPath.valueOf(choices.elementalAffinity)
                }
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
                if (savedElement.isBlank()) {
                    affinity
                } else {
                    runCatching {
                        dev.rpgsolo.engine.ElementPath.valueOf(
                            savedElement,
                        )
                    }.getOrDefault(affinity)
                }
            },
            weaponTags = { weaponTags },
            questCompleted = { questId -> quests.get(questId)?.status == dev.rpgsolo.engine.QuestStatus.DONE },
            multiHitAction = {
                    targetId,
                    hitCount,
                    damageBonus,
                    elementalScope,
                    spendResource,
                    spendCost,
                ->
                combatActions.attack(targetId, hitCount, damageBonus, elementalScope, spendResource, spendCost)
            },
            onChanged = { campaignVersion++ },
        )
    }

    /** MU-05.3: gasta um ponto de status em um atributo; o motor valida saldo e atributo. */
    fun increaseStatPoint(statId: String): String? =
        when (val result = session.execute(dev.rpgsolo.engine.AllocateStatPoints(playerId, mapOf(statId to 1)))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    /** Gasta exatamente um ponto; motor valida saldo e teto antes de registrar qualquer alteração. */
    fun increaseAdvancement(
        choiceId: String,
        skill: Boolean,
    ): String? {
        val command =
            if (skill) {
                dev.rpgsolo.engine.AllocateAdvancement(playerId, skillIncreases = mapOf(choiceId to 1))
            } else {
                dev.rpgsolo.engine.AllocateAdvancement(playerId, statIncreases = mapOf(choiceId to 1))
            }
        return when (val result = session.execute(command)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                campaignVersion++
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** Histórico de evolução derivado do log canônico; não duplica dados nem perde eventos ao reabrir campanha. */
    fun progressionHistory(): List<String> {
        val events = session.events
        return events.mapNotNull { event ->
            when (event) {
                is dev.rpgsolo.engine.XpGained ->
                    if (event.entityId == playerId) {
                        val name = session.state.entities[event.entityId]?.name ?: event.entityId
                        "$name recebeu +${event.amount} XP"
                    } else {
                        null
                    }
                is dev.rpgsolo.engine.LevelGained ->
                    if (event.entityId == playerId) {
                        val name = session.state.entities[event.entityId]?.name ?: event.entityId
                        "$name alcançou o nível ${event.level}" +
                            (if (event.pointsGranted > 0) " e recebeu ${event.pointsGranted} ponto(s) de evolução" else "") +
                            (if (event.statPointsGranted > 0) ", ${event.statPointsGranted} de status" else "") +
                            (if (event.abilityPointsGranted > 0) ", ${event.abilityPointsGranted} de habilidade" else "")
                    } else {
                        null
                    }
                is dev.rpgsolo.engine.AdvancementAllocated ->
                    if (event.entityId == playerId) {
                        val statNames =
                            event.statIncreases.keys.map { id ->
                                activePackage?.data?.stats?.firstOrNull { it.id == id }?.name ?: id
                            }
                        val skillNames =
                            event.skillIncreases.keys.map { id ->
                                activePackage?.data?.traits?.firstOrNull { it.id == id }?.name ?: id
                            }
                        val choices = statNames + skillNames
                        "Você investiu pontos de evolução em ${choices.joinToString(", ")}"
                    } else {
                        null
                    }
                is dev.rpgsolo.engine.StatPointsAllocated ->
                    if (event.entityId == playerId) {
                        val names =
                            event.increases.keys.map { id ->
                                activePackage?.data?.stats?.firstOrNull { it.id == id }?.name ?: id
                            }
                        "Você investiu pontos de status em ${names.joinToString(", ")}"
                    } else {
                        null
                    }
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
        } catch (e: IllegalArgumentException) {
            e.message
        }

    /** Perícias e provas disponíveis. Perícia só ganha ponto pela prova dos níveis múltiplos de 5. */
    fun skillTrialState(): SkillTrialState {
        val player = session.state.entities[playerId] ?: return SkillTrialState(0, emptyList(), emptyList())
        val every = session.engine.config.skillTrialEvery
        val pkg = activePackage
        val caps = session.engine.config.skillCaps
        val skills =
            pkg?.data?.traits.orEmpty().filter { it.type == "pericia" }
                .map { SkillTrialRow(it.id, it.name, player.skills[it.id] ?: 0, caps[it.id] ?: 5, it.description) }
        val pending =
            if (every <= 0) {
                emptyList()
            } else {
                (every..player.level step every).filter { it !in player.skillTrialsClaimed }
            }
        return SkillTrialState(every, pending, skills)
    }

    fun addQuest(
        title: String,
        objectives: List<String> = emptyList(),
        xpReward: Int = 0,
        masterySkillId: String? = null,
        spellRewardId: String? = null,
        idOverride: String? = null,
    ): String? =
        try {
            require(title.isNotBlank()) { "dê um título à missão" }
            require(xpReward in 0..MAX_QUEST_XP_INPUT) { "o XP da missão deve ficar entre 0 e $MAX_QUEST_XP_INPUT" }
            val skillReward = masterySkillId?.trim()?.takeIf { it.isNotEmpty() }
            val spellReward = spellRewardId?.trim()?.takeIf { it.isNotEmpty() }
            if (skillReward != null) {
                require(activePackage?.trait(skillReward)?.type == "pericia") { "perícia inexistente no pacote ativo: $skillReward" }
            }
            if (spellReward != null) {
                require(activePackage?.spell(spellReward) != null) { "magia inexistente no pacote ativo: $spellReward" }
            }
            val cleanObjectives = objectives.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
            require(cleanObjectives.size <= 20 && cleanObjectives.all { it.length <= 300 }) {
                "a missão aceita até 20 objetivos, cada um com no máximo 300 caracteres"
            }
            val id = idOverride ?: "q${quests.all().size + 1}-${title.hashCode()}"
            quests.add(
                Quest(
                    id = id,
                    title = title.trim(),
                    objectives = cleanObjectives.map { dev.rpgsolo.engine.Objective(it) },
                    xpReward = xpReward,
                    masterySkillId = skillReward,
                    spellRewardId = spellReward,
                ),
            )
            saveMetadata()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }

    fun finishQuest(
        questId: String,
        status: dev.rpgsolo.engine.QuestStatus,
    ): String? =
        try {
            require(status != dev.rpgsolo.engine.QuestStatus.ACTIVE) { "escolha concluída ou falhou" }
            quests.finish(questId, status, chapter = chapters.chapters.size)
            var warning: String? = null
            if (status == dev.rpgsolo.engine.QuestStatus.DONE && (quests.get(questId)?.xpReward ?: 0) > 0) {
                val award = session.awardQuestXp(quests, questId, playerId)
                if (award is dev.rpgsolo.engine.ExecResult.Rejected) warning = award.reason
            }
            if (status == dev.rpgsolo.engine.QuestStatus.DONE) warning = awardQuestItems(questId) ?: warning
            val clockAdvance = session.advanceClocksAfterQuestClosure()
            if (clockAdvance is dev.rpgsolo.engine.ExecResult.Rejected && warning == null) warning = clockAdvance.reason
            saveMetadata()
            campaignVersion++
            warning
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }

    /** MU-06.2/MU-07.3: nível 2 de perícia e magia da missão concluída; devolve o motivo de recusa do motor, se houver. */
    private fun awardQuestItems(questId: String): String? {
        val quest = quests.get(questId) ?: return null
        var warning: String? = null
        if (quest.masterySkillId != null && !quest.masteryGranted) {
            val r = session.awardQuestMastery(quests, questId, playerId)
            if (r is dev.rpgsolo.engine.ExecResult.Rejected) warning = r.reason
        }
        if (quest.spellRewardId != null && !quest.spellGranted) {
            val r = session.awardQuestSpell(quests, questId, playerId)
            if (r is dev.rpgsolo.engine.ExecResult.Rejected && warning == null) warning = r.reason
        }
        return warning
    }

    fun completeQuestObjective(
        questId: String,
        index: Int,
    ): String? =
        try {
            quests.completeObjective(questId, index, chapter = chapters.chapters.size)
            var warning: String? = null
            val completed = quests.get(questId)?.status == dev.rpgsolo.engine.QuestStatus.DONE
            if (completed) {
                if ((quests.get(questId)?.xpReward ?: 0) > 0) {
                    val award = session.awardQuestXp(quests, questId, playerId)
                    if (award is dev.rpgsolo.engine.ExecResult.Rejected) warning = award.reason
                }
                warning = awardQuestItems(questId) ?: warning
                val clockAdvance = session.advanceClocksAfterQuestClosure()
                if (clockAdvance is dev.rpgsolo.engine.ExecResult.Rejected && warning == null) warning = clockAdvance.reason
            }
            saveMetadata()
            campaignVersion++
            warning
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }

    private fun startup() {
        content = BundledContent.load(getApplication<Application>())
        activePackage = content.registry.get("sistema-proprio")
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
                    saved.cacheReadPricePerMillion, saved.cacheCreationPricePerMillion,
                    saved.preset, saved.fallbackModels, saved.modelsRefreshedAt,
                )
            }
            if (saved.preset.isNotBlank() && System.currentTimeMillis() - saved.modelsRefreshedAt > MODELS_MAX_AGE_MS) {
                startupScope.launch { refreshFreeModels(force = true) }
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
                Log.e("AppModel", "Falha ao inicializar a campanha", e)
                startupError = e.message ?: "Falha ao abrir o banco de dados."
            }
            runCatching { resumeSourcePipelines() }
        }
    }
}

/** Limite de entrada do XP de missão; o teto real por nível continua no motor (`EngineConfig.maxQuestXp`). */

/** MP-07: fato do motor com o dia da última propagação de rumores. */
private const val RUMOR_DAY_FACT = "rumores.dia"
private const val MAX_QUEST_XP_INPUT = 100_000
private const val PORTRAIT_MAX_SIDE = 512

/** Temas aceitos em [AppModel.themeMode]. */
internal val TEMAS = listOf("sistema", "claro", "escuro")
