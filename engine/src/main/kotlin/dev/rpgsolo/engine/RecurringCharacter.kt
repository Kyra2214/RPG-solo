package dev.rpgsolo.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.Normalizer

/** MP-02: heróis mortos e NPCs recorrentes compartilham o mesmo armazenamento. */
@Serializable
enum class RecurringKind { HERO, NPC }

/**
 * MP-02: personagem que pode reaparecer em campanhas futuras do mesmo mundo, mesmo com outro protagonista.
 * Guarda só a parte pública e o que o motor já registrou (epitáfio, marcos de escopo PESSOA, atitude);
 * nunca a ficha completa nem segredos do dossiê. [identityKey] evita duplicar o mesmo personagem ao recuperar.
 */
@Serializable
data class RecurringCharacter(
    val identityKey: String,
    val name: String,
    val kind: RecurringKind,
    val worldId: String = "",
    val originCampaignId: String = "",
    val concept: String = "",
    val alive: Boolean = true,
    /** Epitáfio público (heróis mortos); vazio para NPCs vivos. */
    val epitaph: String = "",
    val personality: String = "",
    val goals: List<String> = emptyList(),
    /** Último dia de campanha em que o motor registrou algo sobre ele. */
    val lastDay: Int = 0,
    val milestones: List<Milestone> = emptyList(),
    /** Como terminou o último combate vivo (RV-01), se terminou. */
    val defeat: DefeatOutcome? = null,
    val defeatAttitude: DefeatAttitude? = null,
    /** Quantas campanhas já registraram este personagem (base do orçamento de aparições, MP-04). */
    val campaigns: Int = 1,
    /** MP-03: mudanças da vida fora de cena; nulo = nada mudou. Dados antigos decodificam com nulo. */
    val offscreen: OffscreenLife? = null,
    /** RV-02: nome de quem o derrotou sem matar (para o rival saber a quem procurar); vazio = desconhecido ou dados antigos. */
    val defeatedBy: String = "",
    /**
     * MP-04: marcos que entraram na última campanha que registrou o personagem (base do bônus do orçamento de aparições).
     * Dados antigos decodificam com 0.
     */
    val newMilestones: Int = 0,
) {
    fun toJson(): String = JSON.encodeToString(this)

    companion object {
        private val JSON = Json { ignoreUnknownKeys = true }

        fun fromJson(text: String): RecurringCharacter = JSON.decodeFromString(text)
    }
}

object RecurringIdentity {
    /** Nome sem acentos, minúsculo e com espaços simples. */
    fun normalize(name: String): String =
        Normalizer.normalize(name.trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()
            .replace(Regex("\\s+"), " ")

    /** Chave de identidade: tipo + nome normalizado (o mundo é parte da chave de armazenamento, não desta). */
    fun key(
        kind: RecurringKind,
        name: String,
    ): String = "${kind.name.lowercase()}:${normalize(name)}"

    /**
     * Anti-duplicação ao recuperar: une [old] (já guardado) e [new] (da campanha atual) com a mesma chave.
     * Marcos se unem por id (o novo vence); o estado mais recente vence; nada é apagado.
     */
    fun merge(
        old: RecurringCharacter,
        new: RecurringCharacter,
    ): RecurringCharacter {
        require(old.identityKey == new.identityKey) { "identidades diferentes: ${old.identityKey} x ${new.identityKey}" }
        val newer = if (new.lastDay >= old.lastDay || new.originCampaignId != old.originCampaignId) new else old
        val milestones = (old.milestones.associateBy { it.id } + new.milestones.associateBy { it.id }).values.sortedBy { it.day }
        // MP-04: marcos ainda desconhecidos do registro antigo; na mesma campanha acumula, em outra recomeça a contagem.
        val added = new.milestones.count { n -> old.milestones.none { it.id == n.id } }
        val sameCampaign = old.originCampaignId == new.originCampaignId
        return newer.copy(
            name = new.name.ifBlank { old.name },
            concept = new.concept.ifBlank { old.concept },
            epitaph = new.epitaph.ifBlank { old.epitaph },
            personality = new.personality.ifBlank { old.personality },
            goals = (new.goals + old.goals).distinct().take(GOALS_MAX),
            milestones = milestones.takeLast(MILESTONES_PER_CHARACTER_MAX),
            alive = if (!old.alive || !new.alive) newer.alive else true,
            lastDay = maxOf(old.lastDay, new.lastDay),
            offscreen = new.offscreen ?: old.offscreen,
            defeatedBy = new.defeatedBy.ifBlank { old.defeatedBy },
            newMilestones = if (sameCampaign) old.newMilestones + added else added,
            campaigns = if (old.originCampaignId == new.originCampaignId) maxOf(old.campaigns, new.campaigns) else old.campaigns + 1,
        )
    }

    const val GOALS_MAX = 5
    const val MILESTONES_PER_CHARACTER_MAX = 50
}

/** MP-02: extrai do estado da campanha o que merece persistir entre campanhas (só escopo PESSOA nos marcos). */
object RecurringExtractor {
    fun from(
        state: GameState,
        worldId: String,
        campaignId: String,
    ): List<RecurringCharacter> {
        fun marks(id: String) = state.milestonesOf(id).filter { it.scope == MilestoneScope.PERSON }
        val heroes =
            state.legacies.values.map { l ->
                RecurringCharacter(
                    identityKey = RecurringIdentity.key(RecurringKind.HERO, l.heroName),
                    name = l.heroName,
                    kind = RecurringKind.HERO,
                    worldId = worldId,
                    originCampaignId = campaignId,
                    concept = l.concept,
                    alive = false,
                    epitaph = LegacyMemory.fact(l),
                    lastDay = l.diedOnDay,
                    milestones = marks(l.heroId),
                )
            }
        val npcs =
            state.defeats.values.mapNotNull { d ->
                val ent = state.entities[d.npcId] ?: return@mapNotNull null
                val dossier = state.npcDossiers[d.npcId]
                RecurringCharacter(
                    identityKey = RecurringIdentity.key(RecurringKind.NPC, d.npcName),
                    name = d.npcName,
                    kind = RecurringKind.NPC,
                    worldId = worldId,
                    originCampaignId = campaignId,
                    concept = ent.concept,
                    alive = ent.alive,
                    personality = dossier?.personality.orEmpty(),
                    goals = dossier?.goals.orEmpty().take(RecurringIdentity.GOALS_MAX),
                    lastDay = d.day,
                    milestones = marks(d.npcId),
                    defeat = d.outcome,
                    defeatAttitude = d.attitude,
                    defeatedBy = d.byId?.let { state.entities[it]?.name }.orEmpty(),
                )
            }
        return heroes + npcs
    }
}
