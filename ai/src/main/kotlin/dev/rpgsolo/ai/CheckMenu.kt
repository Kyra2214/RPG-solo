package dev.rpgsolo.ai

import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.NpcProposal
import dev.rpgsolo.rules.NpcResult
import dev.rpgsolo.rules.NpcFactory
import dev.rpgsolo.engine.RegraAdicional

/**
 * Pedido de teste feito pelo MESTRE no meio da história (ex.: "role Percepção", "role Esquiva contra a flecha").
 * A IA só escolhe o teste e a dificuldade entre os que o pacote oferece; quem rola e decide é o motor.
 * [onSuccess] e [onFailure] são notas PRIVADAS do mestre: o que acontece em cada caso. Só a nota que
 * corresponde ao resultado volta para a IA, para o que estava escondido não vazar numa falha.
 */
/** Opção de vantagem/desvantagem paga, definida pelo pacote e nunca escolhida pela IA. */
data class FortuneSpendOption(
    val resource: String,
    val resourceName: String,
    val cost: Int,
    val mode: dev.rpgsolo.engine.FortuneMode
)

data class OpposedCheckRequest(
    val entityId: String,
    val checkId: String,
    val difficultyId: String? = null,
    val label: String? = null
)

data class GroupCheckRequest(
    val participantIds: List<String>,
    val requiredSuccesses: Int,
    val onSuccess: String,
    val onFailure: String
)

data class CheckRequest(
    val checkId: String,
    val difficultyId: String? = null,
    /** Nome mostrado ao jogador (ex.: "Percepção"). Se vazio, usa o nome do teste do pacote. */
    val label: String? = null,
    val onSuccess: String,
    val onFailure: String,
    val onPartial: String? = null,
    /** Situação do teste (ex.: "esquiva", "sorte"): ativa bônus de traços do personagem com esse escopo. */
    val skill: String? = null,
    /** Revelação de um segredo específico só após o resultado do teste. */
    val targetNpcId: String? = null,
    val revealSecretId: String? = null,
    val targetPlaceId: String? = null,
    val revealPlaceSecret: Boolean = false,
    val requiredStat: String? = null,
    val opposed: OpposedCheckRequest? = null,
    val group: GroupCheckRequest? = null
)

/**
 * O que uma face do dado significa: [text] é nota PRIVADA do mestre; [damage] é aplicado pelo motor (0 = não fere);
 * [conditions] são status (ids do pacote, ex.: "sangramento") que o motor também aplica ao personagem.
 */
data class RollEntry(
    val text: String,
    val damage: Int = 0,
    val conditions: List<String> = emptyList(),
    /** M-03.6: id de perfil de perigo do pacote; o motor calcula o dano. Exclusivo com [damage] > 0. */
    val hazard: String? = null
)

/**
 * Rolagem com tabela pedida pelo mestre (ex.: d6 para saber onde a flecha acerta: 1 = raspa no ombro, 6 = perto do coração).
 * Não tem dificuldade nem sucesso/falha: a face decide. [table] tem uma entrada para CADA face de 1 a [sides].
 */
data class RollRequest(val sides: Int, val label: String?, val table: Map<Int, RollEntry>)

/** Algo que o mestre pediu e o jogador ainda não rolou. O jogo fica parado até o jogador rolar. */
sealed interface Pending {
    /** Nome mostrado no botão e no cartão do dado. */
    val label: String
    /** Ação do jogador que levou ao pedido. */
    val playerText: String
    /** O que o motor vai rolar. Em [PendingRoll] é só o vaso dos dados: dificuldade e resultado não valem. */
    val spec: CheckSpec
}

data class LuckOption(val resource: String, val cost: Int, val kinds: Set<dev.rpgsolo.engine.LuckKind>)

data class PendingCheck(
    val request: CheckRequest,
    override val spec: CheckSpec,
    override val playerText: String,
    val fortuneSpendOption: FortuneSpendOption? = null,
    /** Nota privada escolhida pelo resultado de uma disputa ou teste em grupo. */
    val resultNote: String? = null
) : Pending {
    override val label: String get() = spec.label
}

data class PendingRoll(val request: RollRequest, override val spec: CheckSpec, override val playerText: String) : Pending {
    override val label: String get() = spec.label
}

/**
 * Os testes, dificuldades e rolagens que o pacote ativo deixa o mestre pedir. Sem pacote não há menu e o mestre
 * não pode pedir nada (o motor não inventa mecânica).
 */
class CheckMenu(private val pkg: RulePackage) {
    private val checks get() = pkg.data.checks
    private val difficulties get() = pkg.data.difficulties

    /** Recurso de saúde do pacote (o que mata ao chegar a 0). null = o sistema não tem. */
    fun healthResource(): String? = pkg.manifest.deathResource

    /** Sorte/destino gasto depois de rolar (F3): recurso, custo e formas vêm só do pacote. null = o teste não oferece. */
    fun luckOption(checkId: String?): LuckOption? {
        val check = checks.firstOrNull { it.id == checkId } ?: return null
        val resource = check.luckResource ?: return null
        if (check.luckCost <= 0 || check.luckKinds.isEmpty()) return null
        return LuckOption(resource, check.luckCost, check.luckKinds)
    }

    /** Custo opcional por teste para o jogador escolher gastar recurso ao rolar com fortuna. */
    fun fortuneSpendOption(checkId: String): FortuneSpendOption? {
        val check = checks.firstOrNull { it.id == checkId } ?: return null
        val resource = check.fortuneResource ?: return null
        val paidMode = check.fortuneSpendMode ?: return null
        if (check.fortuneCost <= 0 || paidMode == dev.rpgsolo.engine.FortuneMode.NORMAL) return null
        val resourceName = pkg.data.resources.firstOrNull { it.id == resource }?.name ?: resource
        return FortuneSpendOption(resource, resourceName, check.fortuneCost, paidMode)
    }

    /** Itens válidos no pacote ativo; a IA não pode inventar ids de inventário. */
    fun hasItem(itemId: String): Boolean = pkg.item(itemId) != null

    /** Valida e inicia combate entre o herói e um NPC recém-criado usando só o perfil do pacote. */
    fun startCombat(session: CampaignSession, playerId: String, npcId: String): String? {
        if (session.state.combat != null) return "já existe um combate ativo"
        val player = session.state.entities[playerId] ?: return "personagem inexistente: $playerId"
        if (player.kind != EntityKind.PLAYER || !player.alive) return "o combatente do jogador não está disponível"
        val npc = session.state.entities[npcId] ?: return "NPC inexistente: $npcId"
        if (npc.kind != EntityKind.NPC || !npc.alive) return "o adversário não é um NPC vivo"
        val creation = pkg.data.creation
        val stat = creation["combate_atributo_iniciativa"]
            ?: return "o pacote não define o atributo de iniciativa"
        val count = creation["combate_dados_iniciativa"]?.toIntOrNull()
            ?: return "o pacote não define combate_dados_iniciativa"
        val sides = creation["combate_lados_iniciativa"]?.toIntOrNull()
            ?: return "o pacote não define combate_lados_iniciativa"
        val highestFirst = when (creation["combate_ordem_iniciativa"]) {
            "MAIOR" -> true
            "MENOR" -> false
            else -> return "o pacote não define combate_ordem_iniciativa válida"
        }
        return when (val result = session.rollInitiative(
            ids = listOf(playerId, npcId),
            diceCount = count,
            diceSides = sides,
            statKey = stat,
            highestFirst = highestFirst
        )) {
            is ExecResult.Accepted -> null
            is ExecResult.Rejected -> result.reason
        }
    }

    /** Valida a ficha mecânica proposta para um NPC pelo pacote ativo. */
    fun createNpc(proposal: NpcProposal): NpcResult = NpcFactory.create(proposal, pkg)

    /** Só aceita efeitos explícitos que o motor da campanha sabe aplicar. */
    fun validateRuleAddition(rule: RegraAdicional): String? {
        if (rule.name.isBlank() || rule.description.isBlank() || rule.limit.isBlank()) {
            return "adaptação exige nome, descrição e limite"
        }
        if (rule.limit.trim().lowercase() !in setOf("passivo", "passiva", "sempre", "permanente")) {
            return "este motor só aplica efeitos passivos; declare limite 'passivo' ou 'sempre' até existir controle mecânico de cargas"
        }
        if (rule.origin != dev.rpgsolo.engine.RuleOrigin.ADAPTACAO) return "a origem deve ser ADAPTACAO"
        if (rule.effects.isEmpty()) return "adaptação sem efeitos mecânicos"
        val allowed = pkg.data.stats.map { it.id }.toSet() + setOf("ataque", "dano", "defesa")
        val unknown = rule.effects.keys.filter { it !in allowed }
        if (unknown.isNotEmpty()) return "escopo de efeito não suportado: ${unknown.joinToString(", ")}"
        if (rule.effects.values.any { it !in -5..5 }) return "efeitos devem ficar entre -5 e +5"
        return null
    }

    /** Converte um id de condição em comando com duração e dano definidos pelo pacote, nunca pela IA. */
    fun conditionCommand(entityId: String, conditionId: String): dev.rpgsolo.engine.AddCondition? {
        val definition = pkg.data.conditions.firstOrNull { it.id == conditionId } ?: return null
        return dev.rpgsolo.engine.AddCondition(
            entityId = entityId,
            condition = definition.id,
            durationRounds = definition.durationRounds,
            damagePerRound = definition.damagePerRound,
            damageResource = definition.damageResource
        )
    }

    /** M-01.7: o alvo tem traço de imunidade à condição (definido pelo pacote). */
    fun isImmune(entityId: String, conditionId: String, traitIds: Collection<String>): Boolean =
        pkg.isImmuneToCondition(traitIds, conditionId)

    /** Mensagem de erro (para a IA corrigir) ou null se o pedido é válido. */
    fun validate(req: CheckRequest): String? {
        if (checks.none { it.id == req.checkId }) {
            return "teste '${req.checkId}' não existe (use um destes: ${checks.joinToString(", ") { it.id }})"
        }
        req.difficultyId?.let { d ->
            if (pkg.difficultyValue(d) == null) {
                return "dificuldade '$d' não existe (use uma destas: ${difficulties.joinToString(", ") { it.id }})"
            }
        }
        if (req.difficultyId == null && pkg.defaultDifficulty() == null) {
            return "este sistema não define dificuldades, então não há como pedir teste"
        }
        return null
    }

    /** Valida uma descoberta de segredo de NPC ou local sem entregar o texto antes do resultado. */
    fun validateSecretRequest(req: CheckRequest, state: GameState, allowedNpcIds: Set<String>? = null): String? {
        val requested = req.targetNpcId != null || req.revealSecretId != null ||
            req.targetPlaceId != null || req.revealPlaceSecret || req.requiredStat != null
        if (!requested) return null
        if (req.targetNpcId != null && req.targetPlaceId != null) return "um teste só pode investigar um NPC ou um local"
        val requiredStat = req.requiredStat ?: return "revelação exige requiredStat"
        val skill = pkg.trait(requiredStat)
        if (pkg.stat(requiredStat) == null && skill?.type != "pericia") {
            return "requiredStat '$requiredStat' não é atributo nem perícia do pacote"
        }
        val check = pkg.data.checks.firstOrNull { it.id == req.checkId }
            ?: return "teste de revelação não existe no pacote"
        if (pkg.stat(requiredStat) != null && check.stat != requiredStat) {
            return "o teste deve usar o atributo '$requiredStat'"
        }
        if (skill?.type == "pericia" && check.skill != requiredStat) {
            return "o teste deve usar a perícia '$requiredStat'"
        }
        if (req.targetNpcId != null || req.revealSecretId != null) {
            val npcId = req.targetNpcId ?: return "revelação de NPC exige targetNpc"
            val secretId = req.revealSecretId ?: return "revelação de NPC exige revealSecret"
            val npc = state.entities[npcId] ?: return "NPC inexistente: $npcId"
            if (npc.kind != EntityKind.NPC) return "targetNpc não é um NPC"
            if (allowedNpcIds != null && npcId !in allowedNpcIds) {
                return "só é possível investigar um NPC presente ou explicitamente mencionado na cena atual"
            }
            val dossier = state.npcDossiers[npcId] ?: return "NPC não tem dossiê privado"
            if (dossier.secrets.none { it.id == secretId }) return "segredo '$secretId' não existe no dossiê de $npcId"
            if (state.knowledge.containsKey("npc-secret:$npcId:$secretId")) return "esse segredo já foi descoberto"
        }
        if (req.targetPlaceId != null || req.revealPlaceSecret) {
            val placeId = req.targetPlaceId ?: return "revelação de local exige targetPlace"
            val place = state.places[placeId] ?: return "local inexistente: $placeId"
            if (placeId !in state.knownPlaces || state.currentPlaceId != placeId) {
                return "só é possível investigar o segredo do local atual e descoberto"
            }
            if (place.secret.isBlank()) return "este local não tem segredo mecânico registrado"
            if (state.knowledge.containsKey("place-secret:$placeId")) return "o segredo deste local já foi descoberto"
        }
        return null
    }

    /** Valida participantes e limites de um teste oposto ou em grupo solicitado pela IA. */
    fun validateAdvancedRequest(req: CheckRequest, state: GameState, playerEntityId: String, allowedNpcIds: Set<String> = emptySet()): String? {
        if (req.opposed != null && req.group != null) return "um teste não pode ser oposto e em grupo ao mesmo tempo"
        req.opposed?.let { opposed ->
            val target = state.entities[opposed.entityId] ?: return "participante oposto inexistente: ${opposed.entityId}"
            if (target.id == playerEntityId || target.kind != EntityKind.NPC || !target.alive) {
                return "o participante oposto deve ser um NPC vivo diferente do jogador"
            }
            if (allowedNpcIds.isNotEmpty() && target.id !in allowedNpcIds) {
                return "o participante oposto não está presente na cena"
            }
            val targetRequest = req.copy(
                checkId = opposed.checkId,
                difficultyId = opposed.difficultyId ?: req.difficultyId,
                label = opposed.label,
                opposed = null,
                group = null,
                targetNpcId = null,
                revealSecretId = null,
                targetPlaceId = null,
                revealPlaceSecret = false,
                requiredStat = null
            )
            validate(targetRequest)?.let { return "teste do oponente inválido: $it" }
        }
        req.group?.let { group ->
            if (group.participantIds.size < 2) return "teste em grupo exige ao menos dois participantes"
            if (group.participantIds.distinct().size != group.participantIds.size) return "participantes repetidos no teste em grupo"
            if ("player" !in group.participantIds) return "o teste em grupo deve incluir 'player'"
            if (group.requiredSuccesses !in 1..group.participantIds.size) {
                return "requiredSuccesses deve ficar entre 1 e o número de participantes"
            }
            for (id in group.participantIds.filter { it != "player" }) {
                val entity = state.entities[id] ?: return "participante de grupo inexistente: $id"
                if (entity.kind != EntityKind.NPC || !entity.alive) return "participante de grupo deve ser um NPC vivo: $id"
                if (allowedNpcIds.isNotEmpty() && id !in allowedNpcIds) {
                    return "participante de grupo não está presente na cena: $id"
                }
            }
        }
        return null
    }

    /** Monta o teste com os atributos do jogador. null se o pedido é inválido. */
    fun build(req: CheckRequest, stats: Map<String, Int>, skills: Map<String, Int> = emptyMap()): CheckSpec? {
        if (validate(req) != null) return null
        val value = req.difficultyId?.let { pkg.difficultyValue(it) } ?: pkg.defaultDifficulty() ?: return null
        val spec = pkg.check(req.checkId, stats, value, skills)
        val label = req.label?.trim()?.take(40)?.takeIf { it.isNotEmpty() }
        return if (label != null) spec.copy(label = label) else spec
    }

    /**
     * Rolagem com tabela: o dano de cada face precisa caber no personagem (até a vida máxima, que pode matar)
     * e só existe se o sistema tiver recurso de saúde. Erro em texto para a IA corrigir, ou null.
     */
    fun validateRoll(req: RollRequest, state: GameState): String? {
        val known = pkg.data.conditions.map { it.id }
        val unknown = req.table.values.flatMap { it.conditions }.filter { it !in known }.distinct()
        if (unknown.isNotEmpty()) {
            val allowed = if (known.isEmpty()) "este sistema não define condições, então use conditions vazio" else "use só estas: ${known.joinToString(", ")}"
            return "condição desconhecida: ${unknown.joinToString(", ")} ($allowed)"
        }
        val hazards = pkg.data.hazards.map { it.id }
        val usedHazards = req.table.values.mapNotNull { it.hazard }.distinct()
        if (usedHazards.isNotEmpty()) {
            val unknownHazards = usedHazards.filter { it !in hazards }
            if (unknownHazards.isNotEmpty()) {
                val allowed = if (hazards.isEmpty()) "este sistema não define perfis de perigo, então use damage" else "use só estes: ${hazards.joinToString(", ")}"
                return "perfil de perigo desconhecido: ${unknownHazards.joinToString(", ")} ($allowed)"
            }
            if (healthResource() == null) return "este sistema não tem recurso de saúde, então a rolagem não pode causar dano (não use hazard)"
        }
        val maxDamage = req.table.values.maxOf { it.damage }
        if (maxDamage <= 0) return null
        val resource = healthResource()
            ?: return "este sistema não tem recurso de saúde, então a rolagem não pode causar dano (use damage 0)"
        val player = state.entities.values.firstOrNull { it.kind == EntityKind.PLAYER } ?: return null
        val max = player.resources[resource]?.max ?: return "o personagem não tem o recurso '$resource'"
        if (maxDamage > max) return "dano $maxDamage passa da vida máxima do personagem ($max); use no máximo $max"
        return null
    }

    /** Dano que o motor aplica por esta face: o perfil de perigo (do pacote, sobre a vida máxima de [playerId]) ou o `damage` direto. */
    fun rollDamage(entry: RollEntry, state: GameState, playerId: String): Int {
        val hazard = entry.hazard ?: return entry.damage
        val max = healthResource()?.let { state.entities[playerId]?.resources?.get(it)?.max } ?: return 0
        return pkg.hazardDamage(hazard, max) ?: 0
    }

    /** Aplica ao teste, na hora de rolar, o Ganho/Perda das condições ativas e os bônus dos traços do jogador. */
    fun adjust(spec: CheckSpec, conditions: Collection<String>, traits: Collection<String> = emptyList(), skill: String? = null): CheckSpec =
        pkg.adjustCheck(spec, conditions, traits, skill)

    /** Os dados que o motor vai rolar. Só vale a face; dificuldade e resultado do vaso são ignorados. */
    fun buildRoll(req: RollRequest): CheckSpec {
        val name = req.label?.trim()?.take(32)?.takeIf { it.isNotEmpty() }
        return CheckSpec(
            label = if (name != null) "$name (d${req.sides})" else "d${req.sides}",
            diceCount = 1, diceSides = req.sides, modifier = 0, difficulty = 0
        )
    }

    /** Seção do pedido à IA. null se o pacote não oferece nada. */
    fun describe(): String? {
        val parts = mutableListOf<String>()
        if (checks.isNotEmpty() && difficulties.isNotEmpty()) {
            parts += buildString {
                append("Testes que você pode pedir (campo check = id; o jogador rola, o motor decide):")
                checks.forEach { c ->
                    val using = c.stat?.let { " (usa ${pkg.stat(it)?.name ?: it})" } ?: ""
                    val fortune = when (c.fortuneMode) {
                        dev.rpgsolo.engine.FortuneMode.NORMAL -> ""
                        dev.rpgsolo.engine.FortuneMode.ADVANTAGE -> " · vantagem automática"
                        dev.rpgsolo.engine.FortuneMode.DISADVANTAGE -> " · desvantagem automática"
                    }
                    val exploding = if (c.explodeOnMax) " · dados explosivos" else ""
                    val paid = if (c.fortuneResource != null && c.fortuneCost > 0 && c.fortuneSpendMode != null) {
                        val resourceName = pkg.data.resources.firstOrNull { it.id == c.fortuneResource }?.name ?: c.fortuneResource
                        val modeName = if (c.fortuneSpendMode == dev.rpgsolo.engine.FortuneMode.ADVANTAGE) "vantagem" else "desvantagem"
                        " · jogador pode gastar ${c.fortuneCost} ${resourceName} para ${modeName}"
                    } else ""
                    append("\n- ").append(c.id).append(": ").append(c.name).append(using)
                        .append(fortune).append(exploding).append(paid)
                }
                append("\nDificuldades (campo difficulty = id, opcional): ")
                append(difficulties.joinToString(", ") { "${it.id} (${it.name})" })
            }
        }
        parts += buildString {
            append("Rolagem com tabela (request_roll): 1 dado de 2 a 20 faces; você define o que cada face significa (text)")
            if (healthResource() != null) append(" e o dano (damage, de 0 até a vida máxima do personagem; o motor aplica)")
            if (healthResource() != null && pkg.data.hazards.isNotEmpty()) {
                append(" Em vez de damage você pode usar hazard (id do perfil de perigo): o motor calcula o dano pela vida máxima, sem você escolher números.")
            }
            append(". Faces maiores = mais graves.")
            if (pkg.data.conditions.isNotEmpty()) append(" Cada face também pode aplicar status ao personagem (campo conditions, lista de ids; o motor aplica).")
        }
        if (healthResource() != null && pkg.data.hazards.isNotEmpty()) {
            parts += "Perfis de perigo (campo hazard em cada face; não combine com damage maior que 0): " +
                pkg.data.hazards.joinToString(", ") { "${it.id} (${it.name}: ${it.description})" }
        }
        if (pkg.data.conditions.isNotEmpty()) {
            parts += "Condições que existem neste sistema (ids para conditions e add_condition/remove_condition): " +
                pkg.data.conditions.joinToString(", ") { condition ->
                    buildString {
                        append(condition.id).append(" (").append(condition.name).append(")")
                        val details = buildList {
                            condition.durationRounds?.let { add("duração: " + it + " rodadas") }
                            if (condition.damagePerRound > 0) {
                                add("dano por rodada: " + condition.damagePerRound + " " + condition.damageResource)
                            }
                            condition.effect?.let {
                                add("efeito nos testes: " + if (it.dice > 0) "Ganho" else "Perda")
                            }
                        }
                        if (details.isNotEmpty()) append(" — ").append(details.joinToString("; "))
                    }
                }
        }
        return parts.joinToString("\n")
    }
}
