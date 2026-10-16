package dev.rpgsolo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource

/**
 * Estado do app. Campanha de demonstração em memória (a ligação com o Room e com os pacotes vem depois).
 * A chave só vive na memória do processo; EncryptedSharedPreferences ainda não foi ligado.
 */
class AppModel {
    val usage = UsageTracker()
    val content: Content = BundledContent.load()

    /** Biblioteca de fichas criadas (5.2.1, só em memória por enquanto). */
    val fichas = mutableStateListOf<Ficha>()
    var chapters = ChapterBook()
        private set
    var quests = QuestLog()
        private set

    /** Sobe a cada mudança de capítulo/missão para a tela de campanhas redesenhar. */
    var campaignVersion by mutableIntStateOf(0)
        private set
    private var roles: AiRoles? = null
    private val store = InMemoryEventStore()
    private var session: CampaignSession = CampaignSession.create(
        store, CampaignRecord("demo", "Aventura de teste", 42L, EngineConfig(deathResource = "pv"))
    ).also {
        it.executeAll(listOf(
            CreateEntity(Entity("hero", "Aventureiro", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
            CreateEntity(Entity("guarda", "Guarda do portão", EntityKind.NPC, resources = mapOf("pv" to Resource(8, 8))))
        ))
    }

    /** null enquanto não houver provedor configurado. */
    var controller: PlayController? by mutableStateOf(null)
        private set

    var configError: String? by mutableStateOf(null)
        private set

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
            configError = null
            true
        } catch (e: IllegalArgumentException) {
            configError = e.message
            false
        }

    /**
     * 7.1.2: testes do pacote ativo (demo: teste-basico) com os atributos do herói. Sem pacote, sem testes:
     * toda ação segue sem teste e o mestre só narra. Falha do classificador vira aviso no chat, não erro.
     */
    private fun checkFor(provider: AiProvider): (String) -> dev.rpgsolo.engine.CheckSpec? {
        val pkg = activePackage ?: return { null }
        val stats = session.state.entities[playerId]?.stats.orEmpty()
        val options = pkg.checkChoices(stats).map { CheckOption(it.id, it.description, it.spec) }
        return IntentClassifier(provider).asCheckFor(options) { classifierWarning = it }
    }

    /** Personagem do jogador e pacote da campanha ativa. A demonstração usa o herói e o `teste-basico`. */
    private var playerId = "hero"
    private var activePackage: RulePackage? = content.registry.get("teste-basico")

    private fun buildController(r: AiRoles): PlayController =
        PlayController(
            session, GameLoop(r.narrator, premiumRunner = if (r.hasPremium) TurnRunner(r.premium) else null), playerId,
            healthResource = activePackage?.manifest?.deathResource ?: "pv",
            checkFor = checkFor(r.cheap), quests = quests
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
        chapters = ChapterBook()
        quests = QuestLog()
        controller = roles?.let { buildController(it) }
        campaignVersion++
    }

    /** Último aviso do classificador; a tela pode mostrá-lo uma vez. */
    var classifierWarning: String? by mutableStateOf(null)

    fun journal(): List<String> = journal(session.state)

    fun openChapter(title: String): String? =
        try {
            chapters.open(session, title)
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
            is ChapterSummarizer.Result.Closed -> { campaignVersion++; null }
            is ChapterSummarizer.Result.Failed -> r.reason
        }
    }

    fun addQuest(title: String): String? =
        try {
            require(title.isNotBlank()) { "dê um título à missão" }
            quests.add(Quest("q${quests.active().size + 1}-${title.hashCode()}", title.trim()))
            campaignVersion++
            null
        } catch (e: IllegalArgumentException) {
            e.message
        }
}
