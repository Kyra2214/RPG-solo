package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** RV-01: como um NPC elegível terminou o combate vivo. */
@Serializable
enum class DefeatOutcome { SPARED, FLED, HUMILIATED }

/** RV-01: atitude inicial do NPC para com quem o derrotou (base do rival, RV-02). */
@Serializable
enum class DefeatAttitude { RESENTMENT, RESPECT, DEBT }

/** RV-01: registro durável de uma derrota sem morte; chave em [GameState.defeats] = id do NPC. */
@Serializable
data class DefeatRecord(
    val npcId: String,
    val npcName: String,
    val outcome: DefeatOutcome,
    val byId: String? = null,
    val attitude: DefeatAttitude,
    val day: Int = 0,
    /** Item que caiu na fuga (já entregue ao espólio); null = nada caiu. */
    val droppedItem: String? = null,
)

/** RV-01: categorias de ameaça (espelham `ThreatCategory` do pacote, guardadas por nome em [Entity.threat]). */
object ThreatTier {
    const val CAPANGA = "CAPANGA"
    const val COMUM = "COMUM"
    private val relevant = setOf("ELITE", "CHEFE", "CHEFE_FINAL")

    /** Só ELITE, CHEFE e CHEFE_FINAL mostram o botão Poupar. */
    fun isRelevant(threat: String): Boolean = threat in relevant
}

/** RV-01: limiar padrão da fuga no d6 (5 ou 6 = 1/3). Configurável em [EngineConfig.fleeThreshold]. */
const val DEFAULT_FLEE_THRESHOLD = 5

/** RV-01: elegível = NPC vivo, com dossiê, que não é capanga nem companheiro (capangas e animais continuam morrendo). */
fun GameState.defeatEligible(id: String): Boolean {
    val ent = entities[id] ?: return false
    return ent.kind == EntityKind.NPC && ent.alive &&
        ent.threat != ThreatTier.CAPANGA &&
        npcDossiers.containsKey(id) &&
        companions.values.none { it.entityId == id }
}

/** RV-01: Poupar só para NPC relevante (ELITE, CHEFE, CHEFE_FINAL) e elegível. */
fun GameState.spareAllowed(id: String): Boolean = defeatEligible(id) && ThreatTier.isRelevant(entities.getValue(id).threat)

/** RV-01: XP de quem escapou, foi poupado ou humilhado = 50% do XP da derrota (mínimo 1 se a derrota pagaria algo). */
fun defeatXpHalf(fullXp: Int): Int = if (fullXp <= 0) 0 else maxOf(1, fullXp / 2)

/** RV-01: quem recebe o espólio da fuga: o PLAYER vivo (o primeiro por id). */
internal fun GameState.spoilsOwner(): String? =
    entities.values.filter { it.kind == EntityKind.PLAYER && it.alive }.minByOrNull { it.id }?.id

/** RV-01: tira [id] da ordem do combate ajustando o turno. */
internal fun CombatState.without(id: String): CombatState {
    val idx = order.indexOf(id)
    if (idx < 0) return this
    val next = order - id
    if (next.isEmpty()) return copy(order = next, turnIndex = 0, defending = defending - id)
    var turn = turnIndex
    if (idx < turn) turn -= 1
    if (turn >= next.size) turn = 0
    return copy(order = next, turnIndex = turn, defending = defending - id)
}
