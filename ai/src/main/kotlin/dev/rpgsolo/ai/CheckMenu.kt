package dev.rpgsolo.ai

import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.GameState
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
    val requiredStat: String? = null
)

/**
 * O que uma face do dado significa: [text] é nota PRIVADA do mestre; [damage] é aplicado pelo motor (0 = não fere);
 * [conditions] são status (ids do pacote, ex.: "sangramento") que o motor também aplica ao personagem.
 */
data class RollEntry(val text: String, val damage: Int = 0, val conditions: List<String> = emptyList())

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

data class PendingCheck(val request: CheckRequest, override val spec: CheckSpec, override val playerText: String) : Pending {
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

    /** Itens válidos no pacote ativo; a IA não pode inventar ids de inventário. */
    fun hasItem(itemId: String): Boolean = pkg.item(itemId) != null

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

    /** Valida o vínculo de uma rolagem a um segredo privado, sem entregar o texto antes do resultado. */
    fun validateSecretRequest(req: CheckRequest, state: GameState): String? {
        if (req.revealSecretId == null && req.targetNpcId == null && req.requiredStat == null) return null
        val npcId = req.targetNpcId ?: return "revelação exige targetNpc"
        val secretId = req.revealSecretId ?: return "revelação exige revealSecret"
        val requiredStat = req.requiredStat ?: return "revelação exige requiredStat"
        val skill = pkg.trait(requiredStat)
        if (pkg.stat(requiredStat) == null && skill?.type != "pericia") {
            return "requiredStat '$requiredStat' não é atributo nem perícia do pacote"
        }
        val npc = state.entities[npcId] ?: return "NPC inexistente: $npcId"
        if (npc.kind != EntityKind.NPC) return "targetNpc não é um NPC"
        val dossier = state.npcDossiers[npcId] ?: return "NPC não tem dossiê privado"
        if (dossier.secrets.none { it.id == secretId }) return "segredo '$secretId' não existe no dossiê de $npcId"
        if (state.knowledge.containsKey("npc-secret:$npcId:$secretId")) return "esse segredo já foi descoberto"
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
        val maxDamage = req.table.values.maxOf { it.damage }
        if (maxDamage <= 0) return null
        val resource = healthResource()
            ?: return "este sistema não tem recurso de saúde, então a rolagem não pode causar dano (use damage 0)"
        val player = state.entities.values.firstOrNull { it.kind == EntityKind.PLAYER } ?: return null
        val max = player.resources[resource]?.max ?: return "o personagem não tem o recurso '$resource'"
        if (maxDamage > max) return "dano $maxDamage passa da vida máxima do personagem ($max); use no máximo $max"
        return null
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
                    append("\n- ").append(c.id).append(": ").append(c.name).append(using)
                }
                append("\nDificuldades (campo difficulty = id, opcional): ")
                append(difficulties.joinToString(", ") { "${it.id} (${it.name})" })
            }
        }
        parts += buildString {
            append("Rolagem com tabela (request_roll): 1 dado de 2 a 20 faces; você define o que cada face significa (text)")
            if (healthResource() != null) append(" e o dano (damage, de 0 até a vida máxima do personagem; o motor aplica)")
            append(". Faces maiores = mais graves.")
            if (pkg.data.conditions.isNotEmpty()) append(" Cada face também pode aplicar status ao personagem (campo conditions, lista de ids; o motor aplica).")
        }
        if (pkg.data.conditions.isNotEmpty()) {
            parts += "Condições que existem neste sistema (ids para conditions e add_condition/remove_condition): " +
                pkg.data.conditions.joinToString(", ") { condition ->
                    buildString {
                        append(condition.id).append(" (").append(condition.name)
                        condition.durationRounds?.let { append("; duração: ").append(it).append(" rodadas") }
                        if (condition.damagePerRound > 0) {
                            append("; dano por rodada: ").append(condition.damagePerRound)
                                .append(" ").append(condition.damageResource)
                        }
                        condition.effect?.let {
                            append("; efeito nos testes: ").append(if (it.dice > 0) "Ganho" else "Perda")
                        }
                        append(")")
                    }
                }
        }
        return parts.joinToString("\n")
    }
}
