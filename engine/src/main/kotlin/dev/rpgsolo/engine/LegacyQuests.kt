package dev.rpgsolo.engine

import kotlin.random.Random

/** LG-03: tipo da quest de legado (decidido pelo motor, nunca pela IA). */
enum class LegacyQuestKind {
    /** Favor ou lembrança ligada a um vínculo ou a um lugar do herói. */
    MEMORY,

    /** Descobrir quem matou o herói (o nome não é revelado antes da descoberta). */
    INVESTIGATION,

    /** Assassino vivo e descoberto: vingança ou perdão (o jogador escolhe). */
    REVENGE,

    /** Assassino vivo e aliado de quem joga: dilema moral. */
    DILEMMA,

    /** O jogador controla o assassino: história pessoal. */
    PERSONAL,
}

/** LG-03: posição na cadeia de até [LegacyQuests.CHAIN_MAX] quests (favor, descoberta, desfecho). */
enum class LegacyQuestStep { FAVOR, DISCOVERY, OUTCOME }

/** LG-03: o que disparou a oferta. */
enum class LegacyTrigger { TIME, PLACE }

/** LG-03: oferta de quest de legado; o mestre só pode propor a quest descrita aqui. */
data class LegacyQuestOffer(
    val heroId: String,
    val heroName: String,
    val step: LegacyQuestStep,
    val kind: LegacyQuestKind,
    val trigger: LegacyTrigger,
    val day: Int,
    /** Nome do assassino; só preenchido quando já foi descoberto (ou é o próprio jogador). */
    val killerName: String? = null,
    val bondNames: List<String> = emptyList(),
)

/**
 * LG-03: gatilho, cadeia e limites das quests de legado. Só lê o estado e o registro de missões;
 * o sorteio é determinístico (semente, herói, dia e lugar). O XP segue o teto do motor ([EngineConfig.maxQuestXp]).
 */
object LegacyQuests {
    const val ID_PREFIX = "legado:"

    /** Máximo de quests por herói morto (favor, descoberta, desfecho). */
    const val CHAIN_MAX = 3

    /** Dias mínimos desde a morte para o gatilho por tempo. */
    const val MIN_DAYS_AFTER_DEATH = 5

    /** Dias mínimos entre duas ofertas (cooldown). */
    const val COOLDOWN_DAYS = 7

    /** Chance (%) por dia do gatilho por tempo e ao chegar em lugar visitado pelo herói. */
    const val TIME_CHANCE_PERCENT = 30
    const val PLACE_CHANCE_PERCENT = 60

    fun questId(
        heroId: String,
        step: LegacyQuestStep,
        day: Int,
    ): String = "$ID_PREFIX$heroId:${step.name}:$day"

    fun isLegacyQuest(q: Quest): Boolean = q.id.startsWith(ID_PREFIX)

    /** Id de herói e dia de oferta de uma quest de legado; null se o id não segue o formato. */
    fun parseId(id: String): Triple<String, LegacyQuestStep, Int>? {
        if (!id.startsWith(ID_PREFIX)) return null
        val parts = id.removePrefix(ID_PREFIX).split(':')
        if (parts.size < 3) return null
        val day = parts.last().toIntOrNull() ?: return null
        val step = runCatching { LegacyQuestStep.valueOf(parts[parts.size - 2]) }.getOrNull() ?: return null
        return Triple(parts.dropLast(2).joinToString(":"), step, day)
    }

    /** O assassino já foi descoberto pelo jogador (há análise ou segredo registrado sobre ele)? */
    fun killerDiscovered(
        legacy: HeroLegacy,
        state: GameState,
    ): Boolean = legacy.killerId?.let { id -> state.npcSheet(id)?.discovered?.isNotEmpty() } ?: false

    /** Cadeia prevista para o herói: LG-03 (favor; descoberta só se o assassino ainda é desconhecido; desfecho). */
    fun plan(
        status: KillerStatus,
        discovered: Boolean,
    ): List<Pair<LegacyQuestStep, LegacyQuestKind>> {
        val chain = mutableListOf(LegacyQuestStep.FAVOR to LegacyQuestKind.MEMORY)
        when (status) {
            KillerStatus.ALIVE, KillerStatus.ALLY -> {
                if (!discovered) chain += LegacyQuestStep.DISCOVERY to LegacyQuestKind.INVESTIGATION
                chain += LegacyQuestStep.OUTCOME to
                    (if (status == KillerStatus.ALIVE) LegacyQuestKind.REVENGE else LegacyQuestKind.DILEMMA)
            }
            KillerStatus.PLAYER_CONTROLLED -> chain += LegacyQuestStep.OUTCOME to LegacyQuestKind.PERSONAL
            KillerStatus.NONE, KillerStatus.DEAD -> Unit
        }
        return chain.take(CHAIN_MAX)
    }

    /**
     * Oferta de quest de legado para hoje, ou null. [quests] é o registro de missões da campanha;
     * [playerId] é a entidade que o jogador controla.
     */
    fun evaluate(
        state: GameState,
        playerId: String?,
        quests: List<Quest>,
        seed: Long,
    ): LegacyQuestOffer? {
        if (state.endedReason != null || state.legacies.isEmpty()) return null
        val legacyQuests = quests.filter { isLegacyQuest(it) }
        if (legacyQuests.any { it.status == QuestStatus.ACTIVE }) return null
        val lastOffer = legacyQuests.mapNotNull { parseId(it.id)?.third }.maxOrNull()
        if (lastOffer != null && state.day - lastOffer < COOLDOWN_DAYS) return null
        val place = state.currentPlaceId
        return state.legacies.values
            .filter { it.heroId != playerId }
            .sortedByDescending { it.diedOnDay }
            .firstNotNullOfOrNull { legacy -> offerFor(state, playerId, legacy, legacyQuests, seed, place) }
    }

    private fun offerFor(
        state: GameState,
        playerId: String?,
        legacy: HeroLegacy,
        legacyQuests: List<Quest>,
        seed: Long,
        place: String?,
    ): LegacyQuestOffer? {
        val byPlace = place != null && place in legacy.visitedPlaceIds
        val byTime = state.day - legacy.diedOnDay >= MIN_DAYS_AFTER_DEATH
        if (!byPlace && !byTime) return null
        val used = legacyQuests.count { parseId(it.id)?.first == legacy.heroId }
        val status = legacy.killerStatus(state, playerId)
        val discovered = killerDiscovered(legacy, state)
        val (step, kind) = plan(status, discovered).getOrNull(used) ?: return null
        val chance = if (byPlace) PLACE_CHANCE_PERCENT else TIME_CHANCE_PERCENT
        val key = seed xor (legacy.heroId.hashCode().toLong() shl 20) xor (state.day.toLong() shl 4) xor (place?.hashCode()?.toLong() ?: 0L)
        if (Random(key).nextInt(100) >= chance) return null
        val showKiller = discovered || status == KillerStatus.PLAYER_CONTROLLED
        return LegacyQuestOffer(
            heroId = legacy.heroId,
            heroName = legacy.heroName,
            step = step,
            kind = kind,
            trigger = if (byPlace) LegacyTrigger.PLACE else LegacyTrigger.TIME,
            day = state.day,
            killerName = if (showKiller) legacy.killerName else null,
            bondNames = legacy.bonds.mapNotNull { b -> b.npcName.takeIf { state.entities[b.npcId]?.alive == true } },
        )
    }

    /** XP máximo aceito para a quest: metade do teto em favor e descoberta, teto inteiro no desfecho. */
    fun xpCap(
        offer: LegacyQuestOffer,
        questXpCap: Int,
    ): Int = if (offer.step == LegacyQuestStep.OUTCOME) questXpCap else questXpCap / 2

    /** Texto para o contexto do mestre: o que propor via `request_quest` com `legacy:true` e o que nunca revelar. */
    fun masterInstruction(offer: LegacyQuestOffer): String {
        val tema =
            when (offer.kind) {
                LegacyQuestKind.MEMORY ->
                    "um favor ou uma lembrança de ${offer.heroName}" +
                        (if (offer.bondNames.isNotEmpty()) ", pedido por alguém como ${offer.bondNames.joinToString(", ")}" else "") +
                        (if (offer.trigger == LegacyTrigger.PLACE) ", ligado a este lugar" else "")
                LegacyQuestKind.INVESTIGATION ->
                    "uma investigação sobre quem matou ${offer.heroName}; NÃO revele nem sugira o nome do assassino"
                LegacyQuestKind.REVENGE ->
                    "a vingança contra ${offer.killerName ?: "o assassino"}, que ainda vive, deixando claro que perdoar também é uma saída"
                LegacyQuestKind.DILEMMA ->
                    "um dilema moral: o assassino de ${offer.heroName}, ${offer.killerName ?: "agora"}, é aliado de quem joga"
                LegacyQuestKind.PERSONAL ->
                    "uma história pessoal: quem joga controla quem matou ${offer.heroName}"
            }
        return "Quest de legado disponível (etapa ${offer.step.name.lowercase()}): se a cena permitir, ofereça $tema usando " +
            "request_quest com \"legacy\":true. É no máximo uma; não invente outro culpado nem recompensa fora do pacote."
    }
}
