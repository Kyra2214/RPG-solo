package dev.rpgsolo.engine

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/** Texto <-> objetos para salvar. Os nomes das classes entram no JSON: renomear um evento exige migração. */
object Codec {
    private val json =
        Json {
            classDiscriminator = "t"
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

    fun encode(e: Event): String = json.encodeToString(Event.serializer(), e)

    fun decodeEvent(s: String): Event = json.decodeFromString(Event.serializer(), s)

    fun encode(c: EngineConfig): String = json.encodeToString(EngineConfig.serializer(), c)

    fun decodeConfig(s: String): EngineConfig = json.decodeFromString(EngineConfig.serializer(), s)

    fun encode(s: GameState): String = json.encodeToString(GameState.serializer(), s)

    fun decodeState(s: String): GameState = json.decodeFromString(GameState.serializer(), s)

    fun encode(f: Ficha): String = json.encodeToString(Ficha.serializer(), f)

    fun decodeFicha(s: String): Ficha = json.decodeFromString(Ficha.serializer(), s)

    fun encodeChapters(value: List<Chapter>): String = json.encodeToString(ListSerializer(Chapter.serializer()), value)

    fun decodeChapters(value: String): List<Chapter> = json.decodeFromString(ListSerializer(Chapter.serializer()), value)

    fun encode(value: Chapter): String = json.encodeToString(Chapter.serializer(), value)

    fun decodeChapter(value: String): Chapter = json.decodeFromString(Chapter.serializer(), value)

    fun encode(value: Quest): String = json.encodeToString(Quest.serializer(), value)

    fun decodeQuest(value: String): Quest = json.decodeFromString(Quest.serializer(), value)

    fun encodeQuests(value: List<Quest>): String = json.encodeToString(ListSerializer(Quest.serializer()), value)

    fun decodeQuests(value: String): List<Quest> = json.decodeFromString(ListSerializer(Quest.serializer()), value)

    fun encodeTimeline(value: List<Pair<Int, String>>): String =
        json.encodeToString(ListSerializer(String.serializer()), value.map { "${it.first}\u0000${it.second}" })

    fun decodeTimeline(value: String): List<Pair<Int, String>> =
        json.decodeFromString(ListSerializer(String.serializer()), value).map {
            val split = it.split('\u0000', limit = 2)
            split.first().toInt() to split.getOrElse(1) { "" }
        }
}

/** Dados que identificam uma campanha salva (a seed e a config são parte do que reproduz o estado). */
data class CampaignRecord(val id: String, val name: String, val seed: Long, val config: EngineConfig)

/**
 * Armazenamento do log de eventos de UMA ou várias campanhas. O log é a verdade; o estado é recalculado.
 * Implementações devem tornar cada chamada atômica (tudo ou nada). Room: uma transação por chamada.
 */
interface EventStore {
    fun createCampaign(record: CampaignRecord)

    fun campaign(id: String): CampaignRecord?

    /** Quantos eventos estão salvos. */
    fun size(campaignId: String): Int

    /** Eventos em ordem. */
    fun load(campaignId: String): List<String>

    /** Acrescenta [payloads] depois dos [size] eventos já salvos. Recusa se [expectedSize] != size atual. */
    fun append(
        campaignId: String,
        expectedSize: Int,
        payloads: List<String>,
    )

    /** Mantém só os primeiros [newSize] eventos (desfazer). */
    fun truncate(
        campaignId: String,
        newSize: Int,
    )

    /** Último snapshot do estado: (quantidade de eventos já aplicados, JSON do estado). Padrão: sem snapshots. */
    fun snapshot(campaignId: String): Pair<Int, String>? = null

    /** Guarda [stateJson] como o snapshot do estado após [eventCount] eventos, substituindo o anterior. Padrão: ignora. */
    fun saveSnapshot(
        campaignId: String,
        eventCount: Int,
        stateJson: String,
    ) {}
}

/** Implementação em memória: testes e uso sem banco. */
class InMemoryEventStore : EventStore {
    private val campaigns = mutableMapOf<String, CampaignRecord>()
    private val logs = mutableMapOf<String, MutableList<String>>()

    @Synchronized
    override fun createCampaign(record: CampaignRecord) {
        require(!campaigns.containsKey(record.id)) { "campanha já existe: ${record.id}" }
        campaigns[record.id] = record
        logs[record.id] = mutableListOf()
    }

    @Synchronized
    override fun campaign(id: String) = campaigns[id]

    @Synchronized
    override fun size(campaignId: String) = logs[campaignId]?.size ?: 0

    @Synchronized
    override fun load(campaignId: String) = logs[campaignId]?.toList() ?: emptyList()

    @Synchronized
    override fun append(
        campaignId: String,
        expectedSize: Int,
        payloads: List<String>,
    ) {
        val log = requireNotNull(logs[campaignId]) { "campanha inexistente: $campaignId" }
        check(log.size == expectedSize) { "log mudou: esperado $expectedSize, atual ${log.size}" }
        log += payloads
    }

    @Synchronized
    override fun truncate(
        campaignId: String,
        newSize: Int,
    ) {
        val log = requireNotNull(logs[campaignId]) { "campanha inexistente: $campaignId" }
        require(newSize in 0..log.size) { "tamanho inválido" }
        while (log.size > newSize) log.removeAt(log.size - 1)
    }
}

/**
 * Campanha com salvamento automático (3.2.1): depois de cada operação o log salvo é igualado ao do motor
 * (acrescenta o que falta ou apara o que foi desfeito). Só eventos confirmados entram no log, então
 * fechar o app no meio de uma jogada deixa o último estado coerente (3.2.2).
 *
 * Segurança entre threads: toda operação pública toma o mesmo trava reentrante, então estado, log do motor e
 * log salvo nunca ficam fora de sincronia. Uma sequência de passos que precisa ser indivisível
 * (por exemplo `mark()` → comandos → `rollbackTo()`) deve rodar dentro de [atomically]; do contrário, outra
 * thread pode gravar entre os passos e o `rollbackTo` apagaria o evento dela.
 * Não segure a trava durante chamadas lentas (rede, IA): [atomically] é só para trechos curtos e locais.
 */
class CampaignSession private constructor(
    val campaignId: String,
    /** Acesso direto ao motor NÃO é seguro entre threads: use [state], [events], [eventCount] e [atomically]. */
    val engine: GameEngine,
    private val store: EventStore,
) {
    private val lock = Any()

    /** Eventos cobertos pelo último snapshot salvo (M-08.3). */
    private var snapshotAt = 0

    /** Executa [block] segurando a trava da sessão. Reentrante: pode chamar as outras operações dentro dele. */
    fun <T> atomically(block: () -> T): T = synchronized(lock) { block() }

    val state: GameState get() = synchronized(lock) { engine.state }

    /** Cópia do log de eventos no momento da chamada (seguro entre threads, ao contrário de `engine.events`). */
    val events: List<Event> get() = synchronized(lock) { engine.events }

    val eventCount: Int get() = synchronized(lock) { engine.eventCount }

    fun execute(command: Command): ExecResult = executeAll(listOf(command))

    fun executeAll(commands: List<Command>): ExecResult = atomically { engine.executeAll(commands).also { save() } }

    fun check(spec: CheckSpec): CheckResult = atomically { engine.check(spec).also { save() } }

    fun rollInitiative(
        ids: List<String>,
        diceCount: Int = 1,
        diceSides: Int = 20,
        statKey: String? = null,
        highestFirst: Boolean = true,
    ): ExecResult = atomically { engine.rollInitiative(ids, diceCount, diceSides, statKey, highestFirst).also { save() } }

    /** Resolve um ataque no motor e salva as rolagens, o resultado e o dano confirmados. */
    @Suppress("DEPRECATION")
    fun attack(spec: AttackSpec): AttackResult = atomically { engine.attack(spec).also { save() } }

    /** Ativa um poder no motor e salva custo, rolagens e efeito como uma única operação. */
    fun activatePower(spec: PowerSpec): PowerResult = atomically { engine.activatePower(spec).also { save() } }

    /** MU-02.4: confronto unificado gravado no mesmo log persistido. */
    fun unifiedAttack(spec: UnifiedAttackSpec): UnifiedAttackResult = atomically { engine.unifiedAttack(spec).also { save() } }

    fun unifiedAttackBetween(
        attackerId: String,
        targetId: String,
        providers: List<ModifierProvider>,
        attackStat: String = "attack",
        defenseStat: String = "defense",
        guaranteedCrit: Boolean = false,
        canCrit: Boolean = true,
        ignoreDefense: IgnoreDefenseSpec? = null,
        damageResource: String = "hp",
        hitStat: String? = null,
        dodgeStat: String? = null,
        luckStat: String? = null,
        damageReduction: Int = 0,
        luckItemBonus: Int = 0,
        elementPercent: Int = 100,
    ): UnifiedAttackResult =
        atomically {
            engine.unifiedAttackBetween(
                attackerId, targetId, providers, attackStat, defenseStat, guaranteedCrit, canCrit, ignoreDefense, damageResource, hitStat, dodgeStat, luckStat, damageReduction, luckItemBonus, elementPercent,
            ).also {
                save()
            }
        }

    /** N-16: aplica a condição com chance (d100) no mesmo log persistido. */
    fun applyConditionByChance(
        targetId: String,
        condition: String,
        chancePercent: Int,
        durationRounds: Int? = null,
        damagePerRound: Int = 0,
        damageResource: String = "hp",
        damagePercent: Int = 0,
        damageFactorPercent: Int = 100,
    ): Boolean =
        atomically {
            engine.applyConditionByChance(
                targetId,
                condition,
                chancePercent,
                durationRounds,
                damagePerRound,
                damageResource,
                damagePercent,
                damageFactorPercent,
            ).also { save() }
        }

    /** N-20: ação de ofício no mesmo log persistido. */
    fun attemptCraft(
        ownerId: String,
        kind: CraftKind,
        inputs: Map<String, Int>,
        outputs: Map<String, Int>,
        chancePercent: Int,
        keepOnFailure: Set<String> = emptySet(),
    ): CraftResult = atomically { engine.attemptCraft(ownerId, kind, inputs, outputs, chancePercent, keepOnFailure).also { save() } }

    /** N-20: afanar no mesmo log persistido. */
    fun attemptPilfer(
        thiefId: String,
        targetId: String,
        itemId: String,
        dexterity: Int,
        agility: Int,
        qty: Int = 1,
    ): CraftResult = atomically { engine.attemptPilfer(thiefId, targetId, itemId, dexterity, agility, qty).also { save() } }

    /** N-32.4: rolagem simples registrada no log persistido. */
    fun rollPlain(
        label: String,
        sides: Int,
    ): Int = atomically { engine.rollPlain(label, sides).also { save() } }

    /** MU-05.1: XP de missão concluída, uma vez, com teto por nível. */
    fun awardQuestXp(
        quests: QuestLog,
        questId: String,
        entityId: String,
    ): ExecResult = atomically { engine.awardQuestXp(quests, questId, entityId).also { save() } }

    fun awardQuestMastery(
        quests: QuestLog,
        questId: String,
        entityId: String,
    ): ExecResult = atomically { engine.awardQuestMastery(quests, questId, entityId).also { save() } }

    fun awardQuestSpell(
        quests: QuestLog,
        questId: String,
        entityId: String,
    ): ExecResult = atomically { engine.awardQuestSpell(quests, questId, entityId).also { save() } }

    fun mark(): Int = synchronized(lock) { engine.mark() }

    fun rollbackTo(mark: Int) {
        atomically {
            engine.rollbackTo(mark)
            save()
        }
    }

    /** Motivo da última gravação que falhou; `null` quando a última tentativa foi confirmada (CP-01.3). */
    @Volatile private var saveFailure: String? = null
    val saveError: String? get() = saveFailure

    /** `true` só depois de uma gravação confirmada pelo armazenamento; falha deixa `false` até [recover] ou a próxima operação. */
    val isSaved: Boolean get() = saveFailure == null

    /** Tenta gravar de novo o que ficou pendente. Devolve `null` se confirmou ou o motivo da falha. */
    fun recover(): String? =
        atomically {
            try {
                save()
                null
            } catch (e: Exception) {
                e.message ?: e::class.simpleName ?: "falha ao gravar"
            }
        }

    /** Sincroniza o log salvo com o do motor. Se falhar, [saveError] fica preenchido e a próxima operação tenta de novo. */
    fun save() {
        atomically {
            try {
                val saved = store.size(campaignId)
                val events = engine.events
                if (events.size < saved) {
                    store.truncate(campaignId, events.size)
                } else if (events.size > saved) {
                    store.append(campaignId, saved, events.subList(saved, events.size).map { Codec.encode(it) })
                }
                saveFailure = null
                if (events.size < snapshotAt) snapshotAt = 0
                if (events.size - snapshotAt >= SNAPSHOT_INTERVAL) {
                    // Snapshot é só otimização: falhar aqui nunca pode perder a jogada já salva.
                    runCatching {
                        store.saveSnapshot(campaignId, engine.eventCount, Codec.encode(engine.state))
                        snapshotAt = engine.eventCount
                    }
                }
            } catch (e: Exception) {
                saveFailure = e.message ?: e::class.simpleName ?: "falha ao gravar"
                throw e
            }
        }
    }

    companion object {
        fun create(
            store: EventStore,
            record: CampaignRecord,
        ): CampaignSession {
            store.createCampaign(record)
            return CampaignSession(record.id, GameEngine(record.seed, record.config), store)
        }

        /** Reabre uma campanha salva. Erro claro se o registro ou o log estiverem ilegíveis. */
        fun open(
            store: EventStore,
            campaignId: String,
        ): CampaignSession {
            val record = requireNotNull(store.campaign(campaignId)) { "campanha não encontrada: $campaignId" }
            val events =
                store.load(campaignId).mapIndexed { i, raw ->
                    try {
                        Codec.decodeEvent(raw)
                    } catch (e: Exception) {
                        throw IllegalStateException("evento $i da campanha $campaignId ilegível", e)
                    }
                }
            val snap =
                runCatching { store.snapshot(campaignId) }.getOrNull()
                    ?.takeIf { it.first in 0..events.size }
                    ?.let { (count, raw) -> runCatching { Codec.decodeState(raw) }.getOrNull()?.takeIf { it.eventCount == count } }
            val engine = GameEngine.restore(record.seed, record.config, events, snap)
            return CampaignSession(campaignId, engine, store).also { it.snapshotAt = snap?.eventCount ?: 0 }
        }

        /** A cada quantos eventos novos um snapshot do estado é gravado. */
        const val SNAPSHOT_INTERVAL = 200
    }
}
