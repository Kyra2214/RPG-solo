package dev.rpgsolo.ui

import android.app.Application
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
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
data class EquipmentView(val id: String, val name: String, val quantity: Int, val slot: String, val equipped: Boolean)
data class InventoryView(
    val id: String, val name: String, val quantity: Int, val kind: String,
    val slot: String?, val equipped: Boolean, val description: String
)


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
        if (chapters.chapters.isEmpty()) openChapter("Capítulo 1")
        storedSettings?.let { saved ->
            runCatching {
                applySettings(
                    ProviderKind.valueOf(saved.kind), saved.endpoint, saved.model, saved.apiKey,
                    saved.cheapModel, saved.premiumModel
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
                campaignVersion++
                "${entity.name} foi adicionado ao próximo encontro."
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    /** Inicia a iniciativa dos participantes vivos da campanha; os números vêm exclusivamente do motor. */
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
    fun resolveNpcTurn(): String? = try {
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
        val effectiveAttackDice = if (attackComparison == dev.rpgsolo.engine.Comparison.POINTS) {
            attackDiceCount
        } else (attackDiceCount + attackConditionShift).coerceIn(1, maxOf(attackDiceCount, 3))
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
                    approvedRuleBonus("ataque") + approvedRuleBonus(attackStat) +
                    (if (attackComparison == dev.rpgsolo.engine.Comparison.POINTS) attackConditionShift else 0) +
                    ((session.state.campaignStyle?.difficultyParams?.get("enemy_tactics") ?: 5.0)
                        .toInt().coerceIn(0, 10) - 5),
                damageBonus = pkg.equipmentBonus(attackerEquipment, "dano") +
                    pkg.traitBonus(combatTraitIds(attacker), "dano") +
                    approvedRuleBonus("dano") + (damageDef?.bonusStat?.let(::approvedRuleBonus) ?: 0),
                defenseBonus = pkg.equipmentBonus(targetEquipment, "defesa") +
                    pkg.traitBonus(combatTraitIds(playerId), "defesa") +
                    approvedRuleBonus("defesa") + approvedRuleBonus(defenseStat) + targetConditionShift,
                damageDiceCount = damageDef?.diceCount ?: 0,
                damageDiceSides = damageDef?.diceSides ?: 0,
                damageStat = damageDef?.bonusStat
            )
        )
        val text = when (result) {
            is dev.rpgsolo.engine.AttackResult.Resolved -> if (result.hit) {
                "${session.state.entities.getValue(attacker).name} acertou você."
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
        val victoryXp = awardCombatVictoryXp(combatantsBefore)
        campaignVersion++
        if (victoryXp > 0) "$text Você venceu e recebeu $victoryXp XP." else text
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun endCombat(): String? = when (val result = session.execute(dev.rpgsolo.engine.EndCombat)) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { campaignVersion++; null }
        is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
    }

    fun combatState(): dev.rpgsolo.engine.CombatState? = session.state.combat

    fun combatantName(id: String): String = session.state.entities[id]?.name ?: id

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
        session.state.combat?.current == playerId && session.state.entities[playerId]?.alive == true

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
        require(item.kind == "equipamento" && item.slot != null) { "item não é equipável" }
        when (val result = session.execute(dev.rpgsolo.engine.EquipItem(playerId, itemId, item.slot))) {
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
        val effectiveAttackDice = if (attackComparison == dev.rpgsolo.engine.Comparison.POINTS) {
            attackDiceCount
        } else (attackDiceCount + attackConditionShift).coerceIn(1, maxOf(attackDiceCount, 3))
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
                    approvedRuleBonus("ataque") + approvedRuleBonus(attackStat) +
                    (if (attackComparison == dev.rpgsolo.engine.Comparison.POINTS) attackConditionShift else 0),
                damageBonus = pkg.equipmentBonus(attackerEquipment, "dano") + pkg.traitBonus(combatTraitIds(playerId), "dano") +
                    approvedRuleBonus("dano") + (damageDef?.bonusStat?.let(::approvedRuleBonus) ?: 0),
                defenseBonus = pkg.equipmentBonus(targetEquipment, "defesa") +
                    pkg.traitBonus(combatTraitIds(targetId), "defesa") +
                    approvedRuleBonus("defesa") + approvedRuleBonus(defenseStat) + targetConditionShift,
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
                if (victoryXp > 0) "$outcome Vitória! Você recebeu $victoryXp XP." else outcome
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
            checkId
        )
        val adaptation = listOfNotNull(adjusted.checkId, adjusted.stat, checkId).distinct().sumOf(::approvedRuleBonus)
        val danger = session.state.campaignStyle?.difficultyParams?.get("danger_level")?.toInt()?.coerceIn(0, 10) ?: 5
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
        val roll = "${result.spec.label}: ${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}."
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
            fichas = fichas.map { dev.rpgsolo.engine.Codec.encode(it) }
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
        }
        restoreFichas()
        switchCampaign(id)
        refreshCampaigns(); campaignVersion++; null
    } catch (e: IllegalArgumentException) { e.message }

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

    /** Troca primeiro para uma sessão já validada; depois restaura contexto e controlador da campanha selecionada. */
    private fun switchCampaign(campaignId: String) {
        val next = CampaignSession.open(store, campaignId)
        pendingCombatNpcIds.clear()
        session = next
        activeCampaignPrefs.edit().putString("active_campaign", campaignId).apply()
        restoreMetadata()
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
            additionalBonus = { scope -> approvedRuleBonus(scope) }
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
            sheet = PlayerSheet(activePackage, activeFicha),
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
