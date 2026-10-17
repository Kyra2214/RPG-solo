package dev.rpgsolo.engine

import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

/** Texto <-> objetos para salvar. Os nomes das classes entram no JSON: renomear um evento exige migração. */
object Codec {
    private val json = Json {
        classDiscriminator = "t"
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun encode(e: Event): String = json.encodeToString(Event.serializer(), e)
    fun decodeEvent(s: String): Event = json.decodeFromString(Event.serializer(), s)

    fun encode(c: EngineConfig): String = json.encodeToString(EngineConfig.serializer(), c)
    fun decodeConfig(s: String): EngineConfig = json.decodeFromString(EngineConfig.serializer(), s)

    fun encode(f: Ficha): String = json.encodeToString(Ficha.serializer(), f)
    fun decodeFicha(s: String): Ficha = json.decodeFromString(Ficha.serializer(), s)

    fun encodeChapters(value: List<Chapter>): String = json.encodeToString(ListSerializer(Chapter.serializer()), value)
    fun decodeChapters(value: String): List<Chapter> = json.decodeFromString(ListSerializer(Chapter.serializer()), value)
    fun encodeQuests(value: List<Quest>): String = json.encodeToString(ListSerializer(Quest.serializer()), value)
    fun decodeQuests(value: String): List<Quest> = json.decodeFromString(ListSerializer(Quest.serializer()), value)
    fun encodeTimeline(value: List<Pair<Int, String>>): String =
        json.encodeToString(ListSerializer(String.serializer()), value.map { "${it.first}\u0000${it.second}" })
    fun decodeTimeline(value: String): List<Pair<Int, String>> = json.decodeFromString(ListSerializer(String.serializer()), value).map {
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
    fun append(campaignId: String, expectedSize: Int, payloads: List<String>)
    /** Mantém só os primeiros [newSize] eventos (desfazer). */
    fun truncate(campaignId: String, newSize: Int)
}

/** Implementação em memória: testes e uso sem banco. */
class InMemoryEventStore : EventStore {
    private val campaigns = mutableMapOf<String, CampaignRecord>()
    private val logs = mutableMapOf<String, MutableList<String>>()

    override fun createCampaign(record: CampaignRecord) {
        require(!campaigns.containsKey(record.id)) { "campanha já existe: ${record.id}" }
        campaigns[record.id] = record
        logs[record.id] = mutableListOf()
    }

    override fun campaign(id: String) = campaigns[id]
    override fun size(campaignId: String) = logs[campaignId]?.size ?: 0
    override fun load(campaignId: String) = logs[campaignId]?.toList() ?: emptyList()

    override fun append(campaignId: String, expectedSize: Int, payloads: List<String>) {
        val log = requireNotNull(logs[campaignId]) { "campanha inexistente: $campaignId" }
        check(log.size == expectedSize) { "log mudou: esperado $expectedSize, atual ${log.size}" }
        log += payloads
    }

    override fun truncate(campaignId: String, newSize: Int) {
        val log = requireNotNull(logs[campaignId]) { "campanha inexistente: $campaignId" }
        require(newSize in 0..log.size) { "tamanho inválido" }
        while (log.size > newSize) log.removeAt(log.size - 1)
    }
}

/**
 * Campanha com salvamento automático (3.2.1): depois de cada operação o log salvo é igualado ao do motor
 * (acrescenta o que falta ou apara o que foi desfeito). Só eventos confirmados entram no log, então
 * fechar o app no meio de uma jogada deixa o último estado coerente (3.2.2).
 */
class CampaignSession private constructor(
    val campaignId: String,
    val engine: GameEngine,
    private val store: EventStore
) {
    val state: GameState get() = engine.state

    fun execute(command: Command): ExecResult = executeAll(listOf(command))

    fun executeAll(commands: List<Command>): ExecResult = engine.executeAll(commands).also { save() }

    fun check(spec: CheckSpec): CheckResult = engine.check(spec).also { save() }

    fun rollInitiative(ids: List<String>, diceCount: Int = 1, diceSides: Int = 20, statKey: String? = null): ExecResult =
        engine.rollInitiative(ids, diceCount, diceSides, statKey).also { save() }

    fun mark(): Int = engine.mark()

    fun rollbackTo(mark: Int) {
        engine.rollbackTo(mark)
        save()
    }

    /** Sincroniza o log salvo com o do motor. Se falhar, a próxima operação tenta de novo. */
    fun save() {
        val saved = store.size(campaignId)
        val events = engine.events
        if (events.size < saved) store.truncate(campaignId, events.size)
        else if (events.size > saved) {
            store.append(campaignId, saved, events.subList(saved, events.size).map { Codec.encode(it) })
        }
    }

    companion object {
        fun create(store: EventStore, record: CampaignRecord): CampaignSession {
            store.createCampaign(record)
            return CampaignSession(record.id, GameEngine(record.seed, record.config), store)
        }

        /** Reabre uma campanha salva. Erro claro se o registro ou o log estiverem ilegíveis. */
        fun open(store: EventStore, campaignId: String): CampaignSession {
            val record = requireNotNull(store.campaign(campaignId)) { "campanha não encontrada: $campaignId" }
            val events = store.load(campaignId).mapIndexed { i, raw ->
                try {
                    Codec.decodeEvent(raw)
                } catch (e: Exception) {
                    throw IllegalStateException("evento $i da campanha $campaignId ilegível", e)
                }
            }
            return CampaignSession(campaignId, GameEngine.restore(record.seed, record.config, events), store)
        }
    }
}
