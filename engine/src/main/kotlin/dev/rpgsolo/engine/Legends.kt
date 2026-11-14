package dev.rpgsolo.engine

/**
 * LG-05: o motor avança em um estágio uma lenda trazida do panteão (só se ela existe e ainda não chegou ao último).
 * No máximo um passo por dia de campanha; a IA só propõe, nunca grava o estágio.
 */
data class AdvanceLegend(val id: String) : Command

/**
 * LG-05: lendas entre campanhas. Heróis mortos do panteão (MP-02/RV-03) entram na campanha nova como rumores de
 * bardo e como fatos do motor (`lenda.*`), sem tabela nova nem migração: o estágio vive em `GameState.facts`, que o
 * replay do log já reconstrói. Só o epitáfio público é usado (nunca ficha nem dossiê).
 *
 * Estágios: 0 ouviu falar; 1 achou quem o conheceu; 2 achou a relíquia; 3 achou o herdeiro (fim da trilha).
 * ASSUMINDO: o estágio 3 é "herdeiro" e o 2 é "relíquia", pois o plano lista só três marcos para os quatro números.
 */
object Legends {
    const val MAX = 3
    const val STAGE_MAX = 3
    const val ENABLED_FACT = "lendas.ligado"
    private const val STAGE_PREFIX = "lenda.estagio."
    private const val NAME_PREFIX = "lenda.nome."
    private const val DAY_PREFIX = "lenda.dia."

    private val STAGES =
        listOf(
            "só ouviu falar dele",
            "achou quem o conheceu",
            "achou a relíquia que ele deixou",
            "achou o herdeiro ou sucessor dele",
        )

    fun stageKey(id: String): String = STAGE_PREFIX + id

    fun rumorId(id: String): String = "lenda.$id"

    /** Até [MAX] heróis mortos do panteão, do mais recente ao mais antigo. */
    fun pick(entries: List<PantheonEntry>): List<PantheonEntry> = entries.filter { it.kind == PantheonKind.FALLEN_HERO }.take(MAX)

    /**
     * Comandos que plantam as lendas escolhidas numa campanha nova: fato de ligado, estágio 0 e nome de cada uma e,
     * se há lugar atual, o rumor de bardo. Idempotente: lenda já plantada não é repetida.
     */
    fun seed(
        state: GameState,
        chosen: List<PantheonEntry>,
    ): List<Command> {
        val out = ArrayList<Command>()
        if (chosen.isEmpty()) return out
        out.add(RecordFact(ENABLED_FACT, "1"))
        val place = state.currentPlaceId?.takeIf { it in state.places }
        for (e in chosen) {
            if (state.facts.containsKey(stageKey(e.identityKey))) continue
            out.add(RecordFact(stageKey(e.identityKey), "0"))
            out.add(RecordFact(NAME_PREFIX + e.identityKey, e.name.take(60)))
            if (place != null && !state.rumors.containsKey(rumorId(e.identityKey))) {
                out.add(StartRumor(rumorId(e.identityKey), "Dizem que ${e.line}".take(RUMOR_TEXT_MAX), place, RumorChannel.BARD))
            }
        }
        return out
    }

    fun stage(
        state: GameState,
        id: String,
    ): Int? = state.facts[stageKey(id)]?.toIntOrNull()?.coerceIn(0, STAGE_MAX)

    /** Validação do [AdvanceLegend]: eventos do passo ou o motivo da recusa. */
    fun advance(
        state: GameState,
        id: String,
    ): Pair<List<Event>, String?> {
        val current = stage(state, id) ?: return emptyList<Event>() to "lenda inexistente: $id"
        if (current >= STAGE_MAX) return emptyList<Event>() to "a lenda já chegou ao último estágio: $id"
        if (state.facts[DAY_PREFIX + id]?.toIntOrNull() == state.day) return emptyList<Event>() to "a lenda já avançou hoje: $id"
        return listOf<Event>(FactRecorded(stageKey(id), (current + 1).toString()), FactRecorded(DAY_PREFIX + id, state.day.toString())) to null
    }

    /** Contexto do mestre sobre as lendas da campanha; nulo se não há nenhuma. */
    fun masterContext(state: GameState): String? {
        val legends =
            state.facts.keys.filter { it.startsWith(STAGE_PREFIX) }.map { it.removePrefix(STAGE_PREFIX) }.sorted()
        if (legends.isEmpty()) return null
        return buildString {
            append("LENDAS DESTE MUNDO (fato fixo; o estágio é decidido pelo motor):\n")
            for (id in legends) {
                val s = stage(state, id) ?: continue
                val name = state.facts[NAME_PREFIX + id] ?: id
                append("- ").append(name).append(" (id ").append(id).append("): o herói ").append(STAGES[s]).append(".\n")
            }
            append("Um NPC pode perguntar \"você conheceu fulano?\" e contar a lenda como rumor. ")
            append("Só proponha advance_legend quando a cena mostrar de fato o próximo passo (alguém que o conheceu, a relíquia, o herdeiro); ")
            append("não invente feitos além do que o rumor diz.")
        }
    }
}
