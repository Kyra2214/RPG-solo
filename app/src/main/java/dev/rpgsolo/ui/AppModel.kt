package dev.rpgsolo.ui

import android.app.Application
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
data class CombatTarget(val id: String, val name: String)
data class EquipmentView(val id: String, val name: String, val quantity: Int, val slot: String, val equipped: Boolean)

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
    private var playerId = "hero"
    private var activePackage: RulePackage? = content.registry.get("sistema-proprio")
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

    /** Inicia a iniciativa dos participantes vivos da campanha; os números vêm exclusivamente do motor. */
    fun startCombat(): String? = try {
        val participants = session.state.entities.values.filter { it.alive }.map { it.id }
        require(participants.size >= 2) { "combate exige pelo menos duas entidades vivas" }
        when (val result = session.rollInitiative(participants, statKey = "habilidade")) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }

    /** Avança para o próximo participante vivo; a ordem e a rodada são decididas pelo motor. */
    fun nextCombatTurn(): String? = when (val result = session.execute(dev.rpgsolo.engine.NextTurn)) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    /** Resolve o ataque do inimigo atual e só então avança o turno; nenhum número vem da IA. */
    fun resolveNpcTurn(): String? {
        return try {
        val combat = requireNotNull(session.state.combat) { "não há combate ativo" }
        require(combat.current != playerId) { "é o turno do jogador" }
        val resource = activePackage?.manifest?.deathResource ?: "pv"
        val attacker = combat.current
        val result = session.attack(
            dev.rpgsolo.engine.AttackSpec(
                attackerId = attacker,
                targetId = playerId,
                attackStat = "habilidade",
                damageResource = resource,
                damage = 1,
                defenseDefault = 10
            )
        )
        val text = when (result) {
            is dev.rpgsolo.engine.AttackResult.Resolved -> if (result.hit) {
                "${session.state.entities.getValue(attacker).name} acertou você e causou ${result.damage} de dano."
            } else {
                "${session.state.entities.getValue(attacker).name} errou o ataque."
            }
            is dev.rpgsolo.engine.AttackResult.Rejected -> return result.reason
        }
        if (session.state.combat != null) {
            when (val advance = session.execute(dev.rpgsolo.engine.NextTurn)) {
                is dev.rpgsolo.engine.ExecResult.Rejected -> return advance.reason
                is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            }
        }
        campaignVersion++
        text
        } catch (e: IllegalArgumentException) { e.message }
          catch (e: IllegalStateException) { e.message }
    }

    fun endCombat(): String? = when (val result = session.execute(dev.rpgsolo.engine.EndCombat)) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    fun combatState(): dev.rpgsolo.engine.CombatState? = session.state.combat

    fun combatTargets(): List<CombatTarget> = session.state.combat?.order.orEmpty()
        .filter { it != playerId }
        .mapNotNull { id -> session.state.entities[id]?.takeIf { it.alive }?.let { CombatTarget(it.id, it.name) } }

    fun isPlayerCombatTurn(): Boolean = session.state.combat?.current == playerId

    fun equipmentView(): List<EquipmentView> {
        val equipped = session.state.equipped[playerId].orEmpty()
        return session.state.inventory[playerId].orEmpty().mapNotNull { (id, quantity) ->
            val item = activePackage?.item(id) ?: return@mapNotNull null
            val slot = item.slot ?: return@mapNotNull null
            if (item.kind != "equipamento" || quantity <= 0) return@mapNotNull null
            EquipmentView(id, item.name, quantity, slot, equipped[slot] == id)
        }.sortedBy { it.name }
    }

    fun equipItem(itemId: String): String? = try {
        val item = requireNotNull(activePackage?.item(itemId)) { "item não existe no catálogo: $itemId" }
        val slot = item.slot
        require(item.kind == "equipamento" && slot != null) { "item não é equipável" }
        when (val result = session.execute(dev.rpgsolo.engine.EquipItem(playerId, itemId, slot))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }

    fun unequipItem(slot: String): String? = when (val result = session.execute(dev.rpgsolo.engine.UnequipItem(playerId, slot))) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    /** Ataque local: alvo, rolagem, defesa e dano passam pelo motor; a IA não escolhe números. */
    fun attack(targetId: String): String? = try {
        require(isPlayerCombatTurn()) { "não é o turno do jogador" }
        val resource = activePackage?.manifest?.deathResource ?: "pv"
        when (val result = session.attack(
            dev.rpgsolo.engine.AttackSpec(
                attackerId = playerId,
                targetId = targetId,
                attackStat = "habilidade",
                damageResource = resource,
                damage = 1,
                defenseDefault = 10
            )
        )) {
            is dev.rpgsolo.engine.AttackResult.Resolved -> {
                campaignVersion++
                if (result.hit) "Ataque acertou e causou ${result.damage} de dano."
                else "Ataque errou (defesa ${result.defense})."
            }
            is dev.rpgsolo.engine.AttackResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun exportBackup(): String = dev.rpgsolo.engine.Backup.export(store, session.campaignId)

    fun importBackup(text: String): String? = try {
        val id = dev.rpgsolo.engine.Backup.import(store, text.trim(), "import-${System.nanoTime()}")
        switchCampaign(id)
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }

    private fun restoreMetadata() {
        chapters = ChapterBook()
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

    /** Troca primeiro para uma sessão já validada; depois restaura contexto e controlador da campanha selecionada. */
    private fun switchCampaign(campaignId: String) {
        val next = CampaignSession.open(store, campaignId)
        session = next
        activeCampaignPrefs.edit().putString("active_campaign", campaignId).apply()
        restoreMetadata()
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

    /** Faz uma chamada mínima ao provedor informado, sem persistir a configuração nem tocar no jogo. */
    fun testConnection(
        kind: ProviderKind, endpoint: String, model: String, apiKey: String
    ): ConnectionTestResult = try {
        require(endpoint.trim().startsWith("http://") || endpoint.trim().startsWith("https://")) {
            "o endpoint deve começar com http:// ou https://"
        }
        require(model.isNotBlank()) { "informe o modelo" }
        require(apiKey.isNotBlank()) { "informe a chave de API" }
        val provider = ProviderFactory.build(
            kind,
            ProviderConfig(endpoint.trim(), model.trim(), apiKey.trim()),
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
            healthResource = activePackage?.manifest?.deathResource
                ?: session.state.entities[playerId]?.resources?.keys?.firstOrNull()
                ?: "pv",
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
        activeFichaId = started.campanha.fichaId
        activeSystemId = started.campanha.system.systemId
        activeEditionId = started.campanha.system.editionId
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
