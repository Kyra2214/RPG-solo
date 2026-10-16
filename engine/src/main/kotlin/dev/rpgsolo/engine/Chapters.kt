package dev.rpgsolo.engine

/** Fase 9.2.4: o que aconteceu num capítulo, calculado do log (nada é guardado em duplicidade). */
data class ChapterDigest(
    /** "dono/item" -> variação líquida. */
    val itemDeltas: Map<String, Int> = emptyMap(),
    val deaths: List<String> = emptyList(),
    val promisesMade: List<String> = emptyList(),
    val promisesKept: List<String> = emptyList(),
    /** Chaves do que o jogador descobriu. */
    val discoveries: List<String> = emptyList(),
    val facts: List<String> = emptyList(),
    val xpGained: Map<String, Int> = emptyMap()
)

fun digestOf(events: List<Event>): ChapterDigest {
    val items = linkedMapOf<String, Int>()
    val xp = linkedMapOf<String, Int>()
    val deaths = mutableListOf<String>()
    val made = mutableListOf<String>()
    val kept = mutableListOf<String>()
    val disc = mutableListOf<String>()
    val facts = mutableListOf<String>()
    for (e in events) when (e) {
        is ItemChanged -> items.merge("${e.ownerId}/${e.itemId}", e.delta, Int::plus)
        is XpGained -> xp.merge(e.entityId, e.amount, Int::plus)
        is EntityDied -> deaths += e.entityId
        is PromiseMade -> made += e.id
        is PromiseKept -> kept += e.id
        is KnowledgeGained -> disc += e.key
        is FactRecorded -> facts += e.key
        else -> Unit
    }
    return ChapterDigest(items.filterValues { it != 0 }, deaths, made, kept, disc, facts, xp)
}

data class Chapter(
    val number: Int,
    val title: String,
    /** Posição no log onde o capítulo começou. */
    val startMark: Int,
    val endMark: Int? = null,
    /** Resumo guardado uma só vez, ao encerrar (9.2.2). */
    val summary: String? = null,
    val digest: ChapterDigest? = null
) {
    val isOpen: Boolean get() = endMark == null
}

/**
 * Capítulos de uma campanha (9.2). Abre a partir do estado atual, encerra com resumo guardado uma vez e
 * tamanho configurável por [maxEvents]. Persistência no Room fica para depois; o log já é a verdade.
 */
class ChapterBook(private val maxEvents: Int = 200) {
    init {
        require(maxEvents > 0) { "maxEvents deve ser positivo" }
    }

    private val list = mutableListOf<Chapter>()
    val chapters: List<Chapter> get() = list.toList()
    fun current(): Chapter? = list.lastOrNull()?.takeIf { it.isOpen }

    fun open(session: CampaignSession, title: String): Chapter {
        require(title.isNotBlank()) { "capítulo sem título" }
        check(current() == null) { "já existe um capítulo aberto" }
        return Chapter(list.size + 1, title.trim(), session.mark()).also { list += it }
    }

    /** Verdadeiro quando o capítulo aberto já passou do tamanho configurado (9.2.3). */
    fun shouldClose(session: CampaignSession): Boolean =
        current()?.let { session.mark() - it.startMark >= maxEvents } ?: false

    /** Encerra o capítulo aberto. O resumo é gravado uma vez e nunca reescrito. */
    fun close(session: CampaignSession, summary: String): Chapter {
        val cur = checkNotNull(current()) { "não há capítulo aberto" }
        require(summary.isNotBlank()) { "resumo vazio" }
        val end = session.mark()
        check(end >= cur.startMark) { "o log voltou antes do início do capítulo; use reconcile" }
        val events = session.engine.events.subList(cur.startMark, end)
        val closed = cur.copy(endMark = end, summary = summary.trim(), digest = digestOf(events))
        list[list.lastIndex] = closed
        return closed
    }

    /** Eventos do capítulo aberto, para alimentar o resumo. */
    fun eventsOfCurrent(session: CampaignSession): List<Event> {
        val cur = checkNotNull(current()) { "não há capítulo aberto" }
        return session.engine.events.drop(cur.startMark)
    }

    /** Depois de desfazer, um capítulo aberto não pode começar depois do fim do log. Ajusta e devolve true se mudou. */
    fun reconcile(session: CampaignSession): Boolean {
        val cur = current() ?: return false
        if (cur.startMark <= session.mark()) return false
        list[list.lastIndex] = cur.copy(startMark = session.mark())
        return true
    }

    /** Resumos dos capítulos encerrados, do mais antigo ao mais novo (contexto da IA, 7.2.3). */
    fun summaries(): List<String> = list.mapNotNull { c -> c.summary?.let { "Capítulo ${c.number} (${c.title}): $it" } }
}

enum class QuestStatus { ACTIVE, DONE, FAILED }

data class Objective(val text: String, val done: Boolean = false)

data class Quest(val id: String, val title: String, val objectives: List<Objective> = emptyList(), val status: QuestStatus = QuestStatus.ACTIVE)

/** Fase 9.3: missões e linha do tempo. */
class QuestLog {
    private val quests = linkedMapOf<String, Quest>()
    private val line = mutableListOf<Pair<Int, String>>()

    fun add(q: Quest) {
        require(q.id.isNotBlank()) { "missão sem id" }
        require(!quests.containsKey(q.id)) { "missão já existe: ${q.id}" }
        quests[q.id] = q
    }

    fun get(id: String) = quests[id]
    fun active(): List<Quest> = quests.values.filter { it.status == QuestStatus.ACTIVE }

    fun completeObjective(id: String, index: Int) {
        val q = requireNotNull(quests[id]) { "missão inexistente: $id" }
        require(q.status == QuestStatus.ACTIVE) { "missão já encerrada: $id" }
        require(index in q.objectives.indices) { "objetivo inexistente: $index" }
        val objs = q.objectives.mapIndexed { i, o -> if (i == index) o.copy(done = true) else o }
        quests[id] = q.copy(objectives = objs, status = if (objs.all { it.done }) QuestStatus.DONE else q.status)
    }

    fun finish(id: String, status: QuestStatus) {
        val q = requireNotNull(quests[id]) { "missão inexistente: $id" }
        require(status != QuestStatus.ACTIVE) { "use DONE ou FAILED" }
        require(q.status == QuestStatus.ACTIVE) { "missão já encerrada: $id" }
        quests[id] = q.copy(status = status)
    }

    /** Evento importante, com o número do capítulo. */
    fun note(chapter: Int, text: String) {
        require(text.isNotBlank()) { "evento vazio" }
        line += chapter to text.trim()
    }

    fun timeline(): List<Pair<Int, String>> = line.toList()
}
