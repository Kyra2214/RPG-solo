package dev.rpgsolo.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dev.rpgsolo.ai.AiProvider
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

/**
 * Estado do app. O ViewModel sobrevive à recriação da Activity; campanha e log são persistidos no Room.
 * A configuração do provedor é restaurada e a chave fica cifrada pelo Android Keystore.
 */
class AppModel(application: Application) : AndroidViewModel(application) {
    val usage = UsageTracker()
    val content: Content = BundledContent.load()
    private val secureSettings = SecureSettings(application)
    private var storedSettings: StoredProviderSettings? = secureSettings.load()

    /** Biblioteca de fichas reutilizáveis, persistida separadamente das campanhas. */
    val fichas = mutableStateListOf<Ficha>()
    val campaigns = mutableStateListOf<CampaignRecord>()
    var chapters = ChapterBook()
        private set
    var quests = QuestLog()
        private set

    /** Sobe a cada mudança de capítulo/missão para a tela de campanhas redesenhar. */
    var campaignVersion by mutableIntStateOf(0)
        private set
    private var roles: AiRoles? = null
    private val database = RpgDatabase.build(application)
    private val store = RoomEventStore(database)
    private val activeCampaignPrefs = application.getSharedPreferences("rpgsolo_session", Application.MODE_PRIVATE)
    private var session: CampaignSession = openOrCreateCampaign()

    /** null enquanto não houver provedor configurado. */
    var controller: PlayController? by mutableStateOf(null)
        private set

    var configError: String? by mutableStateOf(null)
        private set

    init {
        restoreFichas()
        refreshCampaigns()
        restoreMetadata()
        storedSettings?.let { saved ->
            runCatching {
                applySettings(
                    ProviderKind.valueOf(saved.kind), saved.endpoint, saved.model, saved.apiKey,
                    saved.cheapModel, saved.premiumModel
                )
            }
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
        fichas.addAll(store.fichas().mapNotNull { row ->
            runCatching { dev.rpgsolo.engine.Codec.decodeFicha(row.json) }.getOrNull()
        })
    }

    /** Salva uma ficha na biblioteca e atualiza a cópia observável em memória. */
    fun saveFicha(ficha: Ficha) {
        store.saveFicha(FichaRow(ficha.id, ficha.name, dev.rpgsolo.engine.Codec.encode(ficha)))
        val index = fichas.indexOfFirst { it.id == ficha.id }
        if (index >= 0) fichas[index] = ficha else fichas.add(ficha)
    }

    fun activeCampaignId(): String = session.campaignId

    fun exportBackup(): String = dev.rpgsolo.engine.Backup.export(store, session.campaignId)

    fun importBackup(text: String): String? = try {
        val id = dev.rpgsolo.engine.Backup.import(store, text.trim(), "import-${System.nanoTime()}")
        session = CampaignSession.open(store, id)
        activeCampaignPrefs.edit().putString("active_campaign", id).apply()
        chapters = ChapterBook(); quests = QuestLog(); restoreMetadata()
        controller = roles?.let { buildController(it) }
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }

    private fun restoreMetadata() {
        val row = store.metadata(session.campaignId) ?: return
        runCatching {
            chapters.restore(dev.rpgsolo.engine.Codec.decodeChapters(row.chaptersJson))
            quests.restore(dev.rpgsolo.engine.Codec.decodeQuests(row.questsJson), dev.rpgsolo.engine.Codec.decodeTimeline(row.timelineJson))
        }
    }

    private fun saveMetadata() {
        store.saveMetadata(
            CampanhaMetaRow(
                session.campaignId,
                dev.rpgsolo.engine.Codec.encodeChapters(chapters.chapters),
                dev.rpgsolo.engine.Codec.encodeQuests(quests.all()),
                dev.rpgsolo.engine.Codec.encodeTimeline(quests.timeline())
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
        session = CampaignSession.open(store, next.id)
        activeCampaignPrefs.edit().putString("active_campaign", next.id).apply()
        chapters = ChapterBook()
        quests = QuestLog()
        restoreMetadata()
        controller = roles?.let { buildController(it) }
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }

    /** Valida e aplica a configuração. Erro claro na tela se o endpoint ou o modelo forem inválidos. */
    fun applySettings(
        kind: ProviderKind, endpoint: String, model: String, apiKey: String,
        cheapModel: String = "", premiumModel: String = ""
    ): Boolean =
        try {
            require(apiKey.isNotBlank()) { "informe a chave de API" }
            val r = ProviderFactory.buildRoles(
                kind, ProviderConfig(endpoint.trim(), model.trim(), apiKey.trim()), cheapModel, premiumModel, usage
            )
            roles = r
            controller = buildController(r)
            storedSettings = StoredProviderSettings(
                kind = kind.name,
                endpoint = endpoint.trim(),
                model = model.trim(),
                cheapModel = cheapModel.trim(),
                premiumModel = premiumModel.trim(),
                apiKey = apiKey.trim()
            ).also(secureSettings::save)
            configError = null
            true
        } catch (e: IllegalArgumentException) {
            configError = e.message
            false
        }

    fun savedSettings(): StoredProviderSettings? = storedSettings

    /** Remove a chave cifrada e desativa o provedor atual. */
    fun forgetProvider() {
        secureSettings.clear()
        storedSettings = null
        roles = null
        controller = null
        configError = null
    }

    /**
     * 7.1.2: testes do pacote ativo (demo: sistema-proprio) com os atributos do herói. Sem pacote, sem testes:
     * toda ação segue sem teste e o mestre só narra. Falha do classificador vira aviso no chat, não erro.
     */
    private fun checkFor(provider: AiProvider): (String) -> dev.rpgsolo.engine.CheckSpec? {
        val pkg = activePackage ?: return { null }
        val stats = session.state.entities[playerId]?.stats.orEmpty()
        val options = pkg.checkChoices(stats).map { CheckOption(it.id, it.description, it.spec) }
        return IntentClassifier(provider).asCheckFor(options) { classifierWarning = it }
    }

    /** Personagem do jogador e pacote da campanha ativa. A demonstração usa o herói e o `sistema-proprio`. */
    private var playerId = "hero"
    private var activePackage: RulePackage? = content.registry.get("sistema-proprio")

    /** Ficha da campanha ativa (vantagens/desvantagens). A demonstração não tem ficha: o mestre vê só atributos e recursos. */
    private var activeFicha: Ficha? = null

    private fun buildController(r: AiRoles): PlayController =
        PlayController(
            session,
            GameLoop(
                r.narrator,
                premiumRunner = if (r.hasPremium) TurnRunner(r.premium) else null,
                // O mestre só pode pedir os testes que o pacote da campanha oferece.
                menu = activePackage?.let { CheckMenu(it) },
                playerTraits = { activeFicha?.traits.orEmpty() }
            ),
            playerId,
            healthResource = activePackage?.manifest?.deathResource ?: "pv",
            checkFor = checkFor(r.cheap), quests = quests,
            sheet = PlayerSheet(activePackage, activeFicha),
            conditionNames = activePackage?.data?.conditions?.associate { it.id to it.name }.orEmpty()
        )

    /** Fluxo "Nova campanha" (9.1). null até haver provedor de IA configurado. */
    fun newCampaignFlow(): NewCampaignFlow? {
        val r = roles ?: return null
        val catalog = content.catalog ?: return null
        return NewCampaignFlow(CampaignInterpreter(r.cheap), catalog, content.registry, store)
    }

    /** Troca a campanha ativa pela recém-criada: sessão, personagem, pacote, capítulos e missões novos. */
    fun activate(started: StartResult.Started) {
        session = started.session
        playerId = started.playerId
        activePackage = started.pkg
        activeFicha = fichas.firstOrNull { it.id == started.campanha.fichaId }
        activeCampaignPrefs.edit().putString("active_campaign", session.campaignId).apply()
        chapters = ChapterBook()
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

    /** Encerra o capítulo com resumo da IA. Chamada de rede: rode fora da thread principal. Devolve o erro, ou null. */
    fun closeChapter(): String? {
        val p = roles?.cheap ?: return "configure o provedor de IA na aba Config"
        val narrations = controller?.lines.orEmpty().filter { it.author == Author.MASTER }.map { it.text }
        return when (val r = ChapterSummarizer(p).closeWithSummary(session, chapters, narrations)) {
            is ChapterSummarizer.Result.Closed -> { saveMetadata(); campaignVersion++; null }
            is ChapterSummarizer.Result.Failed -> r.reason
        }
    }

    fun addQuest(title: String): String? =
        try {
            require(title.isNotBlank()) { "dê um título à missão" }
            quests.add(Quest("q${quests.active().size + 1}-${title.hashCode()}", title.trim()))
            saveMetadata()
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }
}
