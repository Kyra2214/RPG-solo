package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** Fase 8.1.3: segredo ou informação falsa de um NPC. [falseInfo] = o NPC acredita/diz algo que não é verdade. */
@Serializable
data class Secret(val id: String, val text: String, val falseInfo: Boolean = false)

/**
 * Fase 8.1: dossiê PRIVADO de NPC. Não existe tela para ele; o jogador só vê o que descobriu.
 * Fica fora do [GameState]: só vai para a IA quando a cena exige (8.5.1).
 */
@Serializable
data class NpcDossier(
    val npcId: String,
    val personality: String = "",
    val goals: List<String> = emptyList(),
    val motivations: List<String> = emptyList(),
    /** outro id -> natureza da relação (aliado, rival, deve favor...). */
    val relations: Map<String, String> = emptyMap(),
    val secrets: List<Secret> = emptyList()
)

class DossierBook {
    private val dossiers = mutableMapOf<String, NpcDossier>()

    fun put(d: NpcDossier) {
        dossiers[d.npcId] = d
    }

    fun get(npcId: String): NpcDossier? = dossiers[npcId]

    /**
     * Texto privado para a IA (8.5): só dos NPCs presentes na cena e só com os segredos que [exposed] pede
     * (cena que exige) ou que o motor liberou. Segredo não liberado ainda vai marcado como proibido de revelar.
     */
    fun contextFor(sceneNpcIds: Collection<String>, exposed: Set<String> = emptySet(), released: Set<String> = emptySet()): List<String> =
        sceneNpcIds.mapNotNull { dossiers[it] }.map { d ->
            buildString {
                append("NPC ${d.npcId}")
                if (d.personality.isNotBlank()) append(": ${d.personality}")
                if (d.goals.isNotEmpty()) append("; objetivos: ${d.goals.joinToString(", ")}")
                if (d.motivations.isNotEmpty()) append("; motivações: ${d.motivations.joinToString(", ")}")
                if (d.relations.isNotEmpty()) {
                    append("; relações conhecidas pelo mestre: ")
                    append(d.relations.toSortedMap().entries.joinToString("; ") { (otherId, relation) ->
                        "${otherId}: ${relation}"
                    })
                }
                d.secrets.filter { it.id in exposed || it.id in released }.forEach {
                    val label = if (it.id in released) "liberado ao jogador" else "NÃO revelar sem o motor liberar"
                    append("\n  segredo (${label})${if (it.falseInfo) " [informação falsa]" else ""}: ${it.text}")
                }
            }
        }
}

/** Níveis de informação que um teste de análise pode liberar (8.4.3), do mais vago ao mais detalhado. */
data class RevealPlan(
    val npcId: String,
    /** Atributo/perícia que o personagem precisa ter (8.4.1). */
    val requiredStat: String,
    /** Teste definido pelo pacote (8.4.2). */
    val check: CheckSpec,
    /** Textos por nível: estimativa, condição, detalhe... */
    val levels: List<String>
)

sealed interface RevealResult {
    data class Revealed(val outcome: Outcome, val levelsReleased: Int, val texts: List<String>) : RevealResult
    data class NotAllowed(val reason: String) : RevealResult
}

object Reveal {
    /**
     * Registra um segredo no diário somente depois de o motor resolver um teste como sucesso parcial ou melhor.
     * O texto nunca é retornado por esta função; passa ao contexto da IA pelo conhecimento já registrado.
     */
    fun discoverSecret(
        session: CampaignSession,
        playerId: String,
        npcId: String,
        secretId: String,
        result: CheckResult
    ): ExecResult? {
        if (result.outcome !in setOf(Outcome.PARTIAL_SUCCESS, Outcome.SUCCESS, Outcome.CRITICAL_SUCCESS)) return null
        val player = session.state.entities[playerId] ?: return ExecResult.Rejected("personagem inexistente: $playerId", 0)
        if (!player.alive) return ExecResult.Rejected("personagem está morto", 0)
        val dossier = session.state.npcDossiers[npcId] ?: return ExecResult.Rejected("dossiê inexistente: $npcId", 0)
        val secret = dossier.secrets.firstOrNull { it.id == secretId }
            ?: return ExecResult.Rejected("segredo inexistente: $secretId", 0)
        val key = "npc-secret:$npcId:$secretId"
        if (session.state.knowledge.containsKey(key)) return null
        return session.execute(LearnKnowledge(key, secret.text, "teste:${result.spec.label}", secret.falseInfo))
    }

    /** Descobre o segredo do local atual após sucesso parcial ou melhor; o texto vai ao diário do jogador. */
    fun discoverPlaceSecret(
        session: CampaignSession,
        playerId: String,
        placeId: String,
        result: CheckResult
    ): ExecResult? {
        if (result.outcome !in setOf(Outcome.PARTIAL_SUCCESS, Outcome.SUCCESS, Outcome.CRITICAL_SUCCESS)) return null
        val player = session.state.entities[playerId] ?: return ExecResult.Rejected("personagem inexistente: $playerId", 0)
        if (!player.alive) return ExecResult.Rejected("personagem está morto", 0)
        if (session.state.currentPlaceId != placeId || placeId !in session.state.knownPlaces) {
            return ExecResult.Rejected("o personagem não está neste local descoberto", 0)
        }
        val place = session.state.places[placeId] ?: return ExecResult.Rejected("local inexistente: $placeId", 0)
        if (place.secret.isBlank()) return ExecResult.Rejected("local sem segredo", 0)
        val key = "place-secret:$placeId"
        if (session.state.knowledge.containsKey(key)) return null
        return session.execute(LearnKnowledge(key, place.secret, "teste:${result.spec.label}", false))
    }

    private fun levelsFor(o: Outcome) = when (o) {
        Outcome.CRITICAL_FAILURE, Outcome.FAILURE -> 0
        Outcome.PARTIAL_SUCCESS -> 1
        Outcome.SUCCESS -> 2
        Outcome.CRITICAL_SUCCESS -> 3
    }

    /**
     * Verifica a habilidade, rola pelo motor e registra como conhecimento só o nível liberado.
     * A IA narra apenas o que sair em `texts` (8.4.4). Sem a habilidade, nada é rolado.
     */
    fun run(session: CampaignSession, playerId: String, plan: RevealPlan): RevealResult {
        val player = session.state.entities[playerId] ?: return RevealResult.NotAllowed("personagem inexistente: $playerId")
        if (!player.alive) return RevealResult.NotAllowed("${player.name} está morto")
        if (!plan.requiredStat.let { player.stats.containsKey(it) })
            return RevealResult.NotAllowed("${player.name} não tem ${plan.requiredStat}")
        val npc = session.state.entities[plan.npcId] ?: return RevealResult.NotAllowed("entidade inexistente: ${plan.npcId}")

        val result = session.check(plan.check)
        val n = minOf(levelsFor(result.outcome), plan.levels.size)
        val texts = plan.levels.take(n)
        if (texts.isNotEmpty()) {
            val cmds = texts.mapIndexed { i, t ->
                LearnKnowledge("analise:${npc.id}:${i + 1}", t, "teste:${plan.check.label}")
            }
            // Cada nível já descoberto antes é ignorado em vez de falhar o lote.
            val novos = cmds.filter { session.state.knowledge[it.key] == null }
            if (novos.isNotEmpty()) session.executeAll(novos)
        }
        return RevealResult.Revealed(result.outcome, n, texts)
    }
}

/** Fase 8.3.2: diário automático, só com o que o jogador descobriu. Informação falsa aparece como crença. */
fun journal(state: GameState): List<String> {
    val discoveries = state.knowledge.values.sortedBy { it.key }.map {
        if (it.believed) "${it.text} (acreditado; fonte: ${it.source})" else "${it.text} (fonte: ${it.source})"
    }
    val places = state.knownPlaces.mapNotNull { id ->
        state.places[id]?.let { place ->
            "Lugar descoberto: ${place.name}" +
                place.description.takeIf { it.isNotBlank() }?.let { " — $it" }.orEmpty()
        }
    }.sorted()
    return (discoveries + places).distinct()
}
