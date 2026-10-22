package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** Pedidos de mudança (do app ou propostos pela IA). Nada vira estado sem passar por [decide]. */
sealed interface Command

data class CreateEntity(val entity: Entity) : Command
/** Dano: reduz o recurso (nunca abaixo de 0) e pode matar. */
data class Damage(val entityId: String, val resource: String, val amount: Int) : Command
/** Cura: aumenta o recurso (nunca acima do máximo). Não ressuscita. */
data class Heal(val entityId: String, val resource: String, val amount: Int) : Command
/** Gasto de recurso (mana, munição...). Falha se não houver o suficiente. */
data class SpendResource(val entityId: String, val resource: String, val cost: Int) : Command
/** Gasta um uso de um traço com usos limitados; [limit] é o máximo declarado por quem chama (pacote, em etapa futura). */
data class SpendTraitUse(val entityId: String, val traitId: String, val limit: Int) : Command
/** Devolve os usos de [traitIds] (vazio = todos os traços da entidade). */
data class ResetTraitUses(val entityId: String, val traitIds: List<String> = emptyList()) : Command
data class AddItem(val ownerId: String, val itemId: String, val qty: Int = 1) : Command
data class ConsumeItem(val ownerId: String, val itemId: String, val qty: Int = 1) : Command
data class EquipItem(val ownerId: String, val itemId: String, val slot: String) : Command
data class UnequipItem(val ownerId: String, val slot: String) : Command
data class GainXp(val entityId: String, val amount: Int) : Command
/** Gasta pontos de evolução em aumentos de atributo/perícia autorizados pelo pacote. */
data class AllocateAdvancement(
    val entityId: String,
    val statIncreases: Map<String, Int> = emptyMap(),
    val skillIncreases: Map<String, Int> = emptyMap()
) : Command
/** MU-05.2: gasta pontos de status (1 ponto = +1), sem teto, em atributos que a entidade já tem. */
data class AllocateStatPoints(val entityId: String, val increases: Map<String, Int>) : Command
/** MU-06.2: libera o nível 2 de uma perícia que a entidade já tem; limitado pelos níveis-chave alcançados. */
data class MasterSkill(val entityId: String, val skillId: String) : Command
/** MU-07.3: aprende uma magia, ocupando um slot livre (quem chama garante a origem: missão ou compra). */
data class LearnSpell(val entityId: String, val spellId: String) : Command
/** MU-06.3: aprende uma habilidade gastando [cost] pontos de habilidade. */
data class LearnAbility(val entityId: String, val abilityId: String, val cost: Int) : Command
/** Distribui uma única vez 12 pontos iniciais entre aumentos de atributo e pontos de habilidade. */
data class AllocateInitialBuild(
    val entityId: String,
    val statIncreases: Map<String, Int>,
    val skillPoints: Int
) : Command
/** Evolui uma habilidade da árvore; o catálogo valida origem, afinidade e pré-requisitos antes da chamada. */
data class ProgressSkillTree(
    val entityId: String,
    val skillId: String,
    val pointCost: Int,
    val freeBase: Boolean = false
) : Command
/** Aplica efeitos de uma habilidade desbloqueada a outro alvo, validando ambos no motor. */
data class ApplyTargetSkillEffects(
    val actorId: String,
    val targetId: String,
    val skillId: String,
    val effects: List<ActiveEffect>
) : Command
/** Reseta investimentos da árvore sem criar pontos extras. */
data class ResetSkillTreeProgress(val entityId: String) : Command
/** MU-06.3: ativa uma habilidade aprendida; [effects] já trazem a duração (fixa ou sorteada). */
data class ActivateAbility(
    val entityId: String,
    val abilityId: String,
    val declaredTrigger: AbilityTrigger,
    val trigger: AbilityTrigger,
    val effects: List<ActiveEffect>
) : Command
/** MU-06.3: fim de rodada; desconta 1 de cada efeito temporário e remove os expirados. */
object EndEffectsRound : Command
/** Ressurreição exige justificativa explícita. */
/** M-04: registra uma falha de sobrevivência do caído; ao atingir [limit] falhas, a entidade morre. */
data class FailSurvival(val entityId: String, val limit: Int) : Command
data class Revive(val entityId: String, val reason: String) : Command
data class RecordFact(val key: String, val value: String) : Command
/** I-02.1: guarda uma nota curta sobre um NPC ou lugar existente. */
data class NoteMemory(val subjectId: String, val text: String) : Command
/** Jogador descobriu algo. [believed] = informação falsa/incerta, guardada como "acreditada". */
data class LearnKnowledge(val key: String, val text: String, val source: String, val believed: Boolean = false) : Command
data class MakePromise(val id: String, val npcId: String, val text: String) : Command
data class KeepPromise(val id: String) : Command
/** 8.2.2: muda a relação de [fromId] com [toId] (quanto o primeiro gosta/confia no segundo), de -100 a 100. */
data class ChangeRelation(val fromId: String, val toId: String, val delta: Int, val reason: String) : Command
/** 8.2.3: relógio de agenda (ameaça ou plano de NPC) com [segments] casas. */
data class CreateClock(val id: String, val name: String, val segments: Int, val publicClue: String = "", val restTick: Int = 0, val moveTick: Int = 0, val chapterTick: Int = 0, val midClue: String = "", val consequence: String = "") : Command
data class AdvanceClock(val id: String, val by: Int = 1) : Command
data class AddCondition(
    val entityId: String,
    val condition: String,
    val durationRounds: Int? = null,
    val damagePerRound: Int = 0,
    val damageResource: String = "hp"
) : Command
data class RemoveCondition(val entityId: String, val condition: String) : Command
/** Cria/atualiza o dossiê privado de um NPC; a ficha mecânica é validada pelo pacote antes do comando. */
data class SetNpcDossier(val dossier: NpcDossier) : Command
/** Adaptação proposta: efeitos ficam inertes até ApproveRuleAddition. */
data class ProposeRuleAddition(val rule: RegraAdicional) : Command
data class ApproveRuleAddition(val id: String) : Command
data class RejectRuleAddition(val id: String) : Command
data class SetAmbientacao(val ambientacao: Ambientacao) : Command
data class SetCampaignStyle(val style: CampaignStyle) : Command
/** DC-05.2: define o modo do documento-fonte; recusa repetir o modo atual. */
data class SetSourceMode(val mode: SourceMode) : Command
data class AddPlace(val place: Place) : Command
data class ConnectPlaces(val fromId: String, val toId: String) : Command
data class DiscoverPlace(val id: String) : Command
/** Só o jogador pode mover-se; a IA não move a miniatura automaticamente. */
data class MoveToPlace(val id: String) : Command
/** [order] = ordem de iniciativa já decidida (ver GameEngine.rollInitiative). */
data class StartCombat(val order: List<String>) : Command
data class Defend(val entityId: String) : Command
object NextTurn : Command
object EndCombat : Command

/** Pré-requisitos tipados para evolução de uma perícia; independem do formato textual do pacote. */
@Serializable
data class AdvancementRequirement(
    val minStats: Map<String, Int> = emptyMap(),
    val requiredTraits: Set<String> = emptySet(),
    val excludedTraits: Set<String> = emptySet()
)

/** Configurações vindas do pacote de regras. */
@Serializable
data class EngineConfig(
    /** Recurso que, ao chegar a 0, mata (ex.: "pv"). null = o sistema não mata automaticamente. */
    val deathResource: String? = "hp",
    /** M-04: o que acontece quando [deathResource] chega a 0. */
    val deathMode: DeathMode = DeathMode.PERMADEATH,
    /** XP total necessário para o nível 2, 3, 4... (crescente). Vazio = o sistema não sobe de nível por XP. */
    val levelThresholds: List<Int> = emptyList(),
    /** Bônus de defesa da ação Defend; 0 desativa a ação. */
    val defendBonus: Int = 0,
    /** Pontos concedidos por cada nível novo, conforme declarado pelo pacote. */
    val advancementPointsPerLevel: Int = 0,
    /** MU-05.2: pontos de status concedidos por nível novo (reserva própria). */
    val statPointsPerLevel: Int = 0,
    /** MU-05.2: pontos de habilidade concedidos por nível novo (reserva própria). */
    val abilityPointsPerLevel: Int = 0,
    /** MU-06.2: níveis-chave; cada um permite liberar o nível 2 de uma perícia (por missão). */
    val masteryLevels: List<Int> = emptyList(),
    /** MU-07.2: níveis-chave que concedem 1 slot de magia cada (`niveis_slot_magia`). Vazio = sem magia. */
    val spellSlotLevels: List<Int> = emptyList(),
    /** Quantidade de cada recurso restaurada por nível novo, limitada ao máximo atual. */
    val levelResourceRewards: Map<String, Int> = emptyMap(),
    /** Tetos de atributos e perícias do pacote, usados ao gastar pontos de evolução. */
    val statCaps: Map<String, Int> = emptyMap(),
    val statDefaults: Map<String, Int> = emptyMap(),
    val skillCaps: Map<String, Int> = emptyMap(),
    val skillRequirements: Map<String, AdvancementRequirement> = emptyMap()
) {
    /** MU-07.2: total de slots de magia no [level] (um por nível-chave alcançado). */
    fun spellSlotsFor(level: Int): Int = spellSlotLevels.count { it <= level }

    init {
        require(defendBonus in 0..10) { "defendBonus deve ficar entre 0 e 10" }
        require(advancementPointsPerLevel in 0..100) { "advancementPointsPerLevel deve ficar entre 0 e 100" }
        require(masteryLevels.all { it > 0 } && masteryLevels.zipWithNext().all { (a, b) -> a < b }) {
            "masteryLevels deve ser positivo e estritamente crescente"
        }
        require(spellSlotLevels.all { it > 0 } && spellSlotLevels.zipWithNext().all { (a, b) -> a < b }) {
            "spellSlotLevels deve ser positivo e estritamente crescente"
        }
        require(statPointsPerLevel in 0..100 && abilityPointsPerLevel in 0..100) {
            "statPointsPerLevel e abilityPointsPerLevel devem ficar entre 0 e 100"
        }
        require(levelResourceRewards.values.all { it > 0 } && levelResourceRewards.keys.all { it.isNotBlank() }) {
            "recompensas de recurso por nível devem usar ids não vazios e quantidades positivas"
        }
        require(statCaps.values.all { it >= 0 } && skillCaps.values.all { it in 0..100 }) {
            "limites de evolução inválidos"
        }
        require(statDefaults.keys.all { it in statCaps } &&
            statDefaults.all { (id, value) -> value <= (statCaps[id] ?: Int.MIN_VALUE) }) {
            "valores padrão de atributos inválidos para evolução"
        }
        require(skillRequirements.keys.all { it in skillCaps }) {
            "pré-requisitos de evolução inválidos"
        }
        require(skillRequirements.values.all { requirement ->
            requirement.minStats.keys.all { it in statCaps }
        }) {
            "pré-requisitos de evolução referenciam atributo inexistente"
        }
        require(levelThresholds.zipWithNext().all { (a, b) -> a < b } && levelThresholds.all { it > 0 }) {
            "levelThresholds deve ser positivo e estritamente crescente"
        }
    }

    /** Nível correspondente a um total de XP. */
    fun levelFor(xp: Int): Int = 1 + levelThresholds.count { xp >= it }

    /**
     * MU-05.1: teto de XP por missão no nível [level] = XP necessário para subir um nível (no máximo um nível
     * por missão). Acima do último limiar vale a última faixa. Sem limiares não há teto (XP não gera nível).
     */
    fun maxQuestXp(level: Int): Int {
        if (levelThresholds.isEmpty()) return Int.MAX_VALUE
        val i = (level - 1).coerceIn(0, levelThresholds.lastIndex)
        return levelThresholds[i] - if (i == 0) 0 else levelThresholds[i - 1]
    }

    /**
     * Evento ao zerar o recurso de morte de [ent] por [amount] de dano (M-04).
     * PERMADEATH e NPCs: morte. DOWNED em PLAYER: cai; já caído e com dano > 0: morre; já caído com dano 0: nada.
     */
    internal fun zeroEvent(ent: Entity, amount: Int): Event? = when {
        deathMode != DeathMode.DOWNED || ent.kind != EntityKind.PLAYER -> EntityDied(ent.id)
        !ent.downed -> EntityDowned(ent.id)
        amount > 0 -> EntityDied(ent.id)
        else -> null
    }
}

sealed interface Decision {
    data class Ok(val events: List<Event>) : Decision
    data class No(val reason: String) : Decision
}

private fun no(reason: String) = Decision.No(reason)

fun decide(state: GameState, config: EngineConfig, cmd: Command): Decision {
    fun entity(id: String) = state.entities[id]
    when (cmd) {
        is CreateEntity ->
            return if (state.entities.containsKey(cmd.entity.id)) no("entidade já existe: ${cmd.entity.id}")
            else Decision.Ok(listOf(EntityCreated(cmd.entity)))

        is Damage -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} já está morto")
            val res = ent.resources[cmd.resource] ?: return no("recurso inexistente: ${cmd.resource}")
            if (cmd.amount < 0) return no("dano negativo")
            val newValue = maxOf(0, res.current - cmd.amount)
            val events = mutableListOf<Event>(ResourceChanged(ent.id, cmd.resource, newValue, newValue - res.current))
            if (newValue == 0 && config.deathResource == cmd.resource) config.zeroEvent(ent, cmd.amount)?.let { events += it }
            if (events.any { it is EntityDied } && state.combat != null) {
                val afterDamage = events.fold(state) { current, event -> current.apply(event) }
                val aliveKinds = afterDamage.combat!!.order
                    .mapNotNull { afterDamage.entities[it]?.takeIf(Entity::alive)?.kind }
                    .distinct()
                val combatKinds = state.combat.order
                    .mapNotNull { state.entities[it]?.kind }
                    .distinct()
                if (combatKinds.size >= 2 && aliveKinds.size < 2) events += CombatEnded
            }
            return Decision.Ok(events)
        }

        is Heal -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto; cura não ressuscita (use Revive com justificativa)")
            val res = ent.resources[cmd.resource] ?: return no("recurso inexistente: ${cmd.resource}")
            if (cmd.amount < 0) return no("cura negativa")
            val newValue = minOf(res.max, res.current + cmd.amount)
            val healEvents = mutableListOf<Event>(ResourceChanged(ent.id, cmd.resource, newValue, newValue - res.current))
            if (ent.downed && config.deathResource == cmd.resource && newValue > 0) healEvents += EntityRecovered(ent.id)
            return Decision.Ok(healEvents)
        }

        is SpendResource -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            val res = ent.resources[cmd.resource] ?: return no("recurso inexistente: ${cmd.resource}")
            if (cmd.cost < 0) return no("custo negativo")
            if (res.current < cmd.cost) return no("recurso insuficiente: ${cmd.resource} ${res.current}/${cmd.cost}")
            return Decision.Ok(listOf(ResourceChanged(ent.id, cmd.resource, res.current - cmd.cost, -cmd.cost)))
        }

        is AddItem -> {
            if (entity(cmd.ownerId) == null) return no("dono inexistente: ${cmd.ownerId}")
            if (cmd.qty <= 0) return no("quantidade inválida")
            return Decision.Ok(listOf(ItemChanged(cmd.ownerId, cmd.itemId, cmd.qty)))
        }

        is SpendTraitUse -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.limit <= 0) return no("limite de usos inválido")
            if (cmd.traitId !in ent.traits) return no("${ent.name} não possui o traço ${cmd.traitId}")
            val used = state.traitUses[cmd.entityId]?.get(cmd.traitId) ?: 0
            if (used >= cmd.limit) return no("usos esgotados: ${cmd.traitId} ($used/${cmd.limit})")
            return Decision.Ok(listOf(TraitUseSpent(cmd.entityId, cmd.traitId, used + 1)))
        }

        is ResetTraitUses -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            val spent = state.traitUses[cmd.entityId].orEmpty()
            val targets: List<String> = if (cmd.traitIds.isEmpty()) spent.keys.toList() else cmd.traitIds.filter { it in spent }
            if (targets.isEmpty()) return no("${ent.name} não tem usos gastos para devolver")
            return Decision.Ok(listOf(TraitUsesReset(cmd.entityId, if (cmd.traitIds.isEmpty()) emptyList() else targets)))
        }

        is ConsumeItem -> {
            val ent = entity(cmd.ownerId) ?: return no("dono inexistente: ${cmd.ownerId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.qty <= 0) return no("quantidade inválida")
            val have = state.inventory[cmd.ownerId]?.get(cmd.itemId) ?: 0
            if (have < cmd.qty) return no("item ausente ou insuficiente: ${cmd.itemId} ($have/${cmd.qty})")
            if (state.equipped[cmd.ownerId].orEmpty().values.contains(cmd.itemId)) {
                return no("não é possível consumir/vender o item equipado; desequipe-o primeiro")
            }
            return Decision.Ok(listOf(ItemChanged(cmd.ownerId, cmd.itemId, -cmd.qty)))
        }

        is EquipItem -> {
            val ent = entity(cmd.ownerId) ?: return no("dono inexistente: ${cmd.ownerId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.itemId.isBlank() || cmd.slot.isBlank()) return no("item e slot são obrigatórios")
            val have = state.inventory[cmd.ownerId]?.get(cmd.itemId) ?: 0
            if (have <= 0) return no("item ausente: ${cmd.itemId}")
            if (state.equipped[cmd.ownerId]?.containsKey(cmd.slot) == true) return no("slot ocupado: ${cmd.slot}")
            return Decision.Ok(listOf(EquipmentChanged(cmd.ownerId, cmd.slot, cmd.itemId)))
        }

        is UnequipItem -> {
            if (entity(cmd.ownerId) == null) return no("dono inexistente: ${cmd.ownerId}")
            if (cmd.slot.isBlank()) return no("slot vazio")
            if (state.equipped[cmd.ownerId]?.containsKey(cmd.slot) != true) return no("slot vazio: ${cmd.slot}")
            return Decision.Ok(listOf(EquipmentChanged(cmd.ownerId, cmd.slot, null)))
        }

        is GainXp -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.amount <= 0) return no("XP inválido")
            val totalXp = ent.xp.toLong() + cmd.amount.toLong()
            if (totalXp > Int.MAX_VALUE) return no("XP excede o limite suportado")
            val events = mutableListOf<Event>(XpGained(ent.id, cmd.amount))
            val newLevel = config.levelFor(totalXp.toInt())
            if (newLevel > ent.level) {
                val gainedLevels = newLevel - ent.level
                val pointsGranted = gainedLevels.toLong() * config.advancementPointsPerLevel.toLong()
                if (pointsGranted > Int.MAX_VALUE) return no("pontos de evolução excedem o limite suportado")
                val statGranted = gainedLevels.toLong() * config.statPointsPerLevel.toLong()
                val abilityGranted = gainedLevels.toLong() * config.abilityPointsPerLevel.toLong()
                if (ent.statPoints + statGranted > Int.MAX_VALUE || ent.abilityPoints + abilityGranted > Int.MAX_VALUE) {
                    return no("pontos de status ou habilidade excedem o limite suportado")
                }
                events += LevelGained(ent.id, newLevel, pointsGranted.toInt(), statGranted.toInt(), abilityGranted.toInt())
                for ((resource, perLevel) in config.levelResourceRewards) {
                    val current = ent.resources[resource] ?: return no("recompensa por nível referencia recurso ausente na ficha: $resource")
                    val reward = perLevel.toLong() * gainedLevels.toLong()
                    val newValue = minOf(current.max.toLong(), current.current.toLong() + reward).toInt()
                    if (newValue != current.current) {
                        events += ResourceChanged(ent.id, resource, newValue, newValue - current.current)
                    }
                }
            }
            return Decision.Ok(events)
        }

        is AllocateAdvancement -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.statIncreases.isEmpty() && cmd.skillIncreases.isEmpty()) return no("escolha ao menos um aumento")
            if (cmd.statIncreases.values.any { it <= 0 } || cmd.skillIncreases.values.any { it <= 0 }) {
                return no("aumentos devem ser inteiros positivos")
            }
            for ((stat, increase) in cmd.statIncreases) {
                val current = ent.stats[stat] ?: config.statDefaults[stat] ?: return no("atributo inexistente na ficha: $stat")
                val cap = config.statCaps[stat] ?: return no("o pacote não define limite de evolução para o atributo: $stat")
                if (current > cap || increase > cap - current) return no("aumento ultrapassa o limite de $stat ($current/$cap)")
            }
            val projectedStats = ent.stats + cmd.statIncreases.mapValues { (key, increase) -> (ent.stats[key] ?: config.statDefaults[key] ?: 0) + increase }
            val projectedTraits = (ent.traits + cmd.skillIncreases.keys).distinct()
            for ((skill, increase) in cmd.skillIncreases) {
                val cap = config.skillCaps[skill] ?: return no("perícia não permitida pelo pacote: $skill")
                val current = ent.skills[skill] ?: 0
                if (current > cap || increase > cap - current) return no("aumento ultrapassa o limite de $skill ($current/$cap)")
                val requirements = config.skillRequirements[skill]
                if (requirements != null) {
                    for ((stat, minimum) in requirements.minStats) {
                        if ((projectedStats[stat] ?: 0) < minimum) return no("perícia $skill exige $stat >= $minimum")
                    }
                    val missingTraits = requirements.requiredTraits - projectedTraits.toSet()
                    if (missingTraits.isNotEmpty()) return no("perícia $skill exige os traços: ${missingTraits.joinToString()}")
                    val excluded = requirements.excludedTraits.intersect(projectedTraits.toSet())
                    if (excluded.isNotEmpty()) return no("perícia $skill não combina com: ${excluded.joinToString()}")
                }
            }
            val costLong = cmd.statIncreases.values.sumOf { it.toLong() } +
                cmd.skillIncreases.values.sumOf { it.toLong() }
            if (costLong > Int.MAX_VALUE) return no("custo de evolução excede o limite suportado")
            val cost = costLong.toInt()
            if (cost > ent.advancementPoints) return no("pontos de evolução insuficientes: ${ent.advancementPoints}/$cost")
            return Decision.Ok(listOf(AdvancementAllocated(ent.id, cmd.statIncreases, cmd.skillIncreases, cost)))
        }

        is AllocateStatPoints -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.increases.isEmpty()) return no("escolha ao menos um aumento")
            if (cmd.increases.values.any { it <= 0 }) return no("aumentos devem ser inteiros positivos")
            for ((stat, increase) in cmd.increases) {
                val current = ent.stats[stat] ?: return no("atributo inexistente na ficha: $stat")
                if (current.toLong() + increase.toLong() > Int.MAX_VALUE) return no("aumento excede o limite suportado em $stat")
            }
            val cost = cmd.increases.values.sumOf { it.toLong() }
            if (cost > ent.statPoints) return no("pontos de status insuficientes: ${ent.statPoints}/$cost")
            return Decision.Ok(listOf(StatPointsAllocated(ent.id, cmd.increases, cost.toInt())))
        }

        is MasterSkill -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if ((ent.skills[cmd.skillId] ?: 0) <= 0) return no("a entidade não tem a perícia: ${cmd.skillId}")
            if (cmd.skillId in ent.masteredSkills) return no("nível 2 já liberado: ${cmd.skillId}")
            val slots = config.masteryLevels.count { it <= ent.level }
            if (ent.masteredSkills.size >= slots) return no("sem nível-chave disponível para liberar o nível 2 (${ent.masteredSkills.size}/$slots)")
            return Decision.Ok(listOf(SkillMastered(ent.id, cmd.skillId)))
        }

        is LearnSpell -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.spellId.isBlank()) return no("magia sem id")
            if (cmd.spellId in ent.spells) return no("magia já aprendida: ${cmd.spellId}")
            val slots = config.spellSlotsFor(ent.level)
            if (ent.spells.size >= slots) return no("sem slot de magia livre (${ent.spells.size}/$slots)")
            return Decision.Ok(listOf(SpellLearned(ent.id, cmd.spellId)))
        }

        is LearnAbility -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.abilityId.isBlank()) return no("habilidade sem id")
            if (cmd.cost <= 0) return no("custo da habilidade deve ser positivo")
            if (cmd.abilityId in ent.abilities) return no("habilidade já aprendida: ${cmd.abilityId}")
            if (cmd.cost > ent.abilityPoints) return no("pontos de habilidade insuficientes: ${ent.abilityPoints}/${cmd.cost}")
            return Decision.Ok(listOf(AbilityLearned(ent.id, cmd.abilityId, cmd.cost)))
        }

        is AllocateInitialBuild -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (ent.initialBuildAllocated) return no("os 12 pontos iniciais já foram distribuídos")
            if (cmd.skillPoints < 0 || cmd.statIncreases.values.any { it < 0 }) return no("a distribuição não pode conter valores negativos")
            if (cmd.statIncreases.keys.any { it !in ent.stats }) return no("atributo inexistente na ficha")
            val statTotal = cmd.statIncreases.values.sumOf { it.toLong() }
            if (statTotal + cmd.skillPoints.toLong() != 12L) return no("distribua exatamente 12 pontos entre atributos e habilidades")
            if (cmd.statIncreases.any { (key, value) -> (ent.stats[key] ?: 0).toLong() + value.toLong() > Int.MAX_VALUE }) {
                return no("aumento inicial excede o limite numérico suportado")
            }
            if (ent.abilityPoints.toLong() + cmd.skillPoints.toLong() > Int.MAX_VALUE) return no("pontos de habilidade excedem o limite suportado")
            return Decision.Ok(listOf(InitialBuildAllocated(ent.id, cmd.statIncreases.filterValues { it > 0 }, cmd.skillPoints)))
        }

        is ProgressSkillTree -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.skillId.isBlank()) return no("habilidade sem id")
            if (cmd.pointCost < 0) return no("custo de pontos negativo")
            val currentLevel = ent.skillTreeLevels[cmd.skillId] ?: 0
            if (cmd.freeBase) {
                if (currentLevel != 0) return no("habilidade básica já foi concedida: ${cmd.skillId}")
                if (cmd.pointCost != 0) return no("habilidade básica deve ser gratuita")
            } else {
                if (cmd.pointCost <= 0) return no("desbloqueio/evolução deve custar ao menos 1 ponto")
                if (cmd.pointCost > ent.abilityPoints) return no("pontos de habilidade insuficientes: ${ent.abilityPoints}/${cmd.pointCost}")
                if (currentLevel == 0 && cmd.pointCost != 1) return no("desbloquear uma habilidade custa 1 ponto")
                if (currentLevel > 0 && cmd.pointCost != SkillTreeProgression.upgradeCost(currentLevel)) {
                    return no("custo de evolução inválido")
                }
            }
            val next = try { SkillTreeProgression.nextLevel(currentLevel) }
                catch (e: IllegalArgumentException) { return no(e.message ?: "nível de habilidade inválido") }
            return Decision.Ok(listOf(SkillTreeProgressed(ent.id, cmd.skillId, next, cmd.pointCost, cmd.freeBase)))
        }

        is ApplyTargetSkillEffects -> {
            val actor = entity(cmd.actorId) ?: return no("usuário inexistente: ${cmd.actorId}")
            val target = entity(cmd.targetId) ?: return no("alvo inexistente: ${cmd.targetId}")
            if (!actor.alive || actor.downed) return no("usuário da habilidade está fora de combate")
            if (!target.alive || target.downed) return no("alvo está fora de combate")
            if (actor.id == target.id) return no("efeito de alvo não pode apontar para o próprio usuário")
            if ((actor.skillTreeLevels[cmd.skillId] ?: 0) <= 0 && cmd.skillId !in actor.abilities) return no("habilidade não aprendida: ${cmd.skillId}")
            if (cmd.effects.isEmpty()) return no("habilidade sem efeitos para o alvo")
            if (cmd.effects.any { it.remainingRounds <= 0 || it.target.isBlank() || it.value == 0 || !it.sourceId.startsWith("habilidade:") }) {
                return no("efeito de alvo inválido")
            }
            return Decision.Ok(listOf(TargetSkillEffectsApplied(actor.id, target.id, cmd.skillId, cmd.effects)))
        }

        is ResetSkillTreeProgress -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            val invested = ent.skillTreeInvestments.values.sumOf { it.toLong() }
            if (invested <= 0L) return no("não há pontos investidos na árvore para devolver")
            if (invested > Int.MAX_VALUE.toLong()) return no("investimento excede o limite numérico suportado")
            if (ent.abilityPoints.toLong() + invested > Int.MAX_VALUE.toLong()) return no("reset excederia o limite de pontos suportado")
            return Decision.Ok(listOf(SkillTreeProgressReset(ent.id, invested.toInt())))
        }

        is ActivateAbility -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.abilityId !in ent.abilities && (ent.skillTreeLevels[cmd.abilityId] ?: 0) <= 0) return no("habilidade não aprendida: ${cmd.abilityId}")
            if (cmd.declaredTrigger != cmd.trigger) return no("gatilho incorreto para ${cmd.abilityId}: exige ${cmd.declaredTrigger}")
            if (cmd.effects.isEmpty()) return no("habilidade sem bônus")
            if (cmd.effects.any { it.remainingRounds <= 0 || it.target.isBlank() || it.value == 0 }) return no("efeito de habilidade inválido")
            return Decision.Ok(listOf(AbilityActivated(ent.id, cmd.abilityId, cmd.effects)))
        }

        is EndEffectsRound -> {
            if (state.activeEffects.values.all { it.isEmpty() }) return no("não há efeitos temporários ativos")
            return Decision.Ok(listOf(EffectsRoundEnded))
        }

        is FailSurvival -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive || !ent.downed) return no("${ent.name} não está caído")
            if (cmd.limit !in 1..10) return no("limite de falhas deve ficar entre 1 e 10")
            val failures = ent.deathFailures + 1
            val events = mutableListOf<Event>(SurvivalFailed(ent.id, failures))
            if (failures >= cmd.limit) events += EntityDied(ent.id)
            return Decision.Ok(events)
        }

        is Revive -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (ent.alive) return no("${ent.name} não está morto")
            if (cmd.reason.isBlank()) return no("ressurreição exige justificativa")
            val events = mutableListOf<Event>(EntityRevived(ent.id, cmd.reason))
            val dr = config.deathResource
            if (dr != null && ent.resources.containsKey(dr)) {
                val cur = ent.resources.getValue(dr).current
                events += ResourceChanged(ent.id, dr, 1, 1 - cur)
            }
            return Decision.Ok(events)
        }

        is LearnKnowledge -> {
            if (cmd.key.isBlank()) return no("chave vazia")
            if (cmd.source.isBlank()) return no("conhecimento exige fonte (pista, teste, conversa)")
            val old = state.knowledge[cmd.key]
            if (old != null && !old.believed && cmd.believed)
                return no("${cmd.key} já é fato confirmado; não pode voltar a ser só acreditado")
            return Decision.Ok(listOf(KnowledgeGained(cmd.key, cmd.text, cmd.source, cmd.believed)))
        }

        is MakePromise -> {
            val npc = entity(cmd.npcId) ?: return no("entidade inexistente: ${cmd.npcId}")
            if (!npc.alive) return no("${npc.name} está morto")
            if (cmd.id.isBlank() || cmd.text.isBlank()) return no("promessa exige id e texto")
            if (state.promises.containsKey(cmd.id)) return no("promessa já existe: ${cmd.id}")
            return Decision.Ok(listOf(PromiseMade(cmd.id, cmd.npcId, cmd.text)))
        }

        is KeepPromise -> {
            val p = state.promises[cmd.id] ?: return no("promessa inexistente: ${cmd.id}")
            if (p.kept) return no("promessa já cumprida: ${cmd.id}")
            return Decision.Ok(listOf(PromiseKept(cmd.id)))
        }

        is ChangeRelation -> {
            val a = entity(cmd.fromId) ?: return no("entidade inexistente: ${cmd.fromId}")
            val b = entity(cmd.toId) ?: return no("entidade inexistente: ${cmd.toId}")
            if (a.id == b.id) return no("relação consigo mesmo não existe")
            if (!a.alive) return no("${a.name} está morto")
            if (cmd.delta == 0) return no("mudança de relação zerada")
            if (cmd.reason.isBlank()) return no("mudança de relação exige motivo")
            val old = state.relations[relationKey(a.id, b.id)] ?: 0
            val now = (old + cmd.delta).coerceIn(-100, 100)
            if (now == old) return no("relação já está no limite")
            return Decision.Ok(listOf(RelationChanged(a.id, b.id, now - old, now, cmd.reason.trim())))
        }

        is CreateClock -> {
            if (cmd.id.isBlank() || cmd.id.length > 80 || cmd.name.isBlank() || cmd.name.length > 120) {
                return no("relógio exige id e nome dentro dos limites")
            }
            if (cmd.segments !in 2..12) return no("relógio precisa de 2 a 12 casas")
            if (cmd.publicClue.length > 500) return no("pista pública excede 500 caracteres")
            if (cmd.midClue.length > 500) return no("pista intermediária excede 500 caracteres")
            if (cmd.consequence.length > 500) return no("consequência excede 500 caracteres")
            if (cmd.restTick !in 0..cmd.segments) return no("avanço por descanso deve ficar entre 0 e o número de casas")
            if (cmd.moveTick !in 0..cmd.segments) return no("avanço por viagem deve ficar entre 0 e o número de casas")
            if (cmd.chapterTick !in 0..cmd.segments) return no("avanço por capítulo deve ficar entre 0 e o número de casas")
            if (state.clocks.containsKey(cmd.id)) return no("relógio já existe: ${cmd.id}")
            return Decision.Ok(listOf(ClockCreated(cmd.id, cmd.name.trim(), cmd.segments, cmd.publicClue.trim(), cmd.restTick, cmd.moveTick, cmd.chapterTick, cmd.midClue.trim(), cmd.consequence.trim())))
        }

        is AdvanceClock -> {
            val c = state.clocks[cmd.id] ?: return no("relógio inexistente: ${cmd.id}")
            if (cmd.by <= 0) return no("avanço inválido")
            if (c.full) return no("relógio já completo: ${c.name}")
            val now = minOf(c.segments, c.filled + cmd.by)
            return Decision.Ok(listOf(ClockAdvanced(c.id, now, now - c.filled)))
        }

        is AddCondition -> {
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.condition.isBlank()) return no("condição vazia")
            if (state.conditions[ent.id]?.contains(cmd.condition) == true) return no("${ent.name} já tem a condição ${cmd.condition}")
            if (cmd.durationRounds != null && cmd.durationRounds <= 0) return no("duração de condição inválida")
            if (cmd.damagePerRound < 0) return no("dano de condição negativo")
            if (cmd.damagePerRound > 0 && ent.resources[cmd.damageResource] == null) {
                return no("recurso inexistente: ${cmd.damageResource}")
            }
            return Decision.Ok(listOf(ConditionAdded(
                ent.id, cmd.condition, cmd.durationRounds, cmd.damagePerRound, cmd.damageResource
            )))
        }

        is RemoveCondition -> {
            if (entity(cmd.entityId) == null) return no("entidade inexistente: ${cmd.entityId}")
            if (state.conditions[cmd.entityId]?.contains(cmd.condition) != true) return no("condição ausente: ${cmd.condition}")
            return Decision.Ok(listOf(ConditionRemoved(cmd.entityId, cmd.condition)))
        }

        is Defend -> {
            val combat = state.combat ?: return no("não há combate ativo")
            if (combat.current != cmd.entityId) return no("não é o turno de ${cmd.entityId}")
            val ent = entity(cmd.entityId) ?: return no("entidade inexistente: ${cmd.entityId}")
            if (!ent.alive) return no("${ent.name} está morto")
            if (cmd.entityId in combat.defending) return no("${ent.name} já está em postura defensiva")
            if (config.defendBonus <= 0) return no("o pacote não define bônus positivo para defender")
            return Decision.Ok(listOf(CombatantDefended(cmd.entityId)))
        }

        is StartCombat -> {
            if (state.combat != null) return no("já existe um combate ativo")
            if (cmd.order.isEmpty()) return no("combate sem participantes")
            if (cmd.order.toSet().size != cmd.order.size) return no("participante repetido na iniciativa")
            for (id in cmd.order) {
                val ent = entity(id) ?: return no("entidade inexistente: $id")
                if (!ent.alive) return no("${ent.name} está morto e não pode entrar no combate")
            }
            return Decision.Ok(listOf(CombatStarted(cmd.order)))
        }

        NextTurn -> {
            val c = state.combat ?: return no("não há combate ativo")
            val n = c.order.size
            for (step in 1..n) {
                val idx = (c.turnIndex + step) % n
                if (entity(c.order[idx])?.alive == true) {
                    val wrapped = c.turnIndex + step >= n
                    val events = mutableListOf<Event>(TurnAdvanced(idx, if (wrapped) c.round + 1 else c.round))
                    if (wrapped) {
                        var tickState = state
                        for ((entityId, conditions) in state.conditionStates) {
                            for ((condition, effect) in conditions) {
                                val ent = tickState.entities[entityId] ?: continue
                                if (!ent.alive) continue
                                val resource = ent.resources[effect.damageResource]
                                if (resource == null && effect.damagePerRound > 0) continue
                                val newValue = resource?.let { maxOf(0, it.current - effect.damagePerRound) }
                                val remaining = effect.remainingRounds?.let { maxOf(0, it - 1) }
                                val tick = ConditionTicked(
                                    entityId, condition, remaining,
                                    effect.damageResource.takeIf { resource != null },
                                    newValue,
                                    if (resource == null || newValue == null) 0 else resource.current - newValue
                                )
                                events += tick
                                tickState = tickState.apply(tick)
                                if (resource != null && newValue == 0 && config.deathResource == effect.damageResource) {
                                    config.zeroEvent(ent, effect.damagePerRound)?.let { death ->
                                        events += death
                                        tickState = tickState.apply(death)
                                    }
                                }
                            }
                        }
                        val activeCombat = tickState.combat
                        if (activeCombat != null) {
                            val aliveKinds = activeCombat.order
                                .mapNotNull { tickState.entities[it]?.takeIf(Entity::alive)?.kind }
                                .distinct()
                            val combatKinds = activeCombat.order
                                .mapNotNull { state.entities[it]?.kind }
                                .distinct()
                            if (combatKinds.size >= 2 && aliveKinds.size < 2) {
                                events += CombatEnded
                            } else if (tickState.entities[activeCombat.current]?.alive != true) {
                                // Uma condição pode derrubar o combatente cujo turno acabou de começar.
                                // Avança para o próximo vivo sem abrir outra rodada nem deixar o combate preso.
                                val next = (1..activeCombat.order.size)
                                    .map { step -> (activeCombat.turnIndex + step) % activeCombat.order.size }
                                    .firstOrNull { tickState.entities[activeCombat.order[it]]?.alive == true }
                                if (next != null) {
                                    val skipDeadTurn = TurnAdvanced(next, activeCombat.round)
                                    events += skipDeadTurn
                                    tickState = tickState.apply(skipDeadTurn)
                                } else {
                                    events += CombatEnded
                                }
                            }
                        }
                    }
                    return Decision.Ok(events)
                }
            }
            return no("ninguém vivo no combate")
        }

        EndCombat ->
            return if (state.combat == null) no("não há combate ativo") else Decision.Ok(listOf(CombatEnded))

        is SetNpcDossier -> {
            val dossier = cmd.dossier
            val npc = entity(dossier.npcId) ?: return no("NPC inexistente: ${dossier.npcId}")
            if (npc.kind != EntityKind.NPC) return no("dossiê privado só pode pertencer a NPC")
            if (dossier.npcId.isBlank() || dossier.npcId.length > 80) return no("dossiê exige id de NPC válido")
            if (dossier.personality.length > 2000) return no("personalidade do NPC excede 2000 caracteres")
            if (dossier.goals.size > 30 || dossier.motivations.size > 30 ||
                (dossier.goals + dossier.motivations).any { it.isBlank() || it.length > 300 }) {
                return no("objetivos e motivações aceitam até 30 itens de até 300 caracteres")
            }
            if (dossier.relations.size > 50 || dossier.relations.any { (id, relation) ->
                    id.isBlank() || id.length > 80 || relation.isBlank() || relation.length > 300
                }) return no("relações do NPC inválidas")
            if (dossier.secrets.size > 50 || dossier.secrets.any {
                    it.id.isBlank() || it.id.length > 80 || it.text.isBlank() || it.text.length > 1000
                }) return no("segredos aceitam até 50 itens, id de até 80 e texto de até 1000 caracteres")
            if (dossier.secrets.map { it.id }.distinct().size != dossier.secrets.size) return no("id de segredo repetido")
            return Decision.Ok(listOf(NpcDossierStored(dossier)))
        }

        is SetAmbientacao -> {
            val a = cmd.ambientacao
            if (a.id.isBlank() || a.campaignId.isBlank() || a.name.isBlank()) return no("ambientação exige id, campanha e nome")
            return Decision.Ok(listOf(AmbientacaoSet(a)))
        }
        is SetCampaignStyle -> {
            if (cmd.style.contentLimits.size > 50 || cmd.style.contentLimits.any { it.isBlank() || it.length > 300 }) {
                return no("limites de conteúdo inválidos")
            }
            if (cmd.style.difficultyParams.values.any { !it.isFinite() || it !in 0.0..10.0 }) {
                return no("parâmetros de dificuldade fora do intervalo 0..10")
            }
            return Decision.Ok(listOf(CampaignStyleSet(cmd.style)))
        }
        is SetSourceMode -> {
            if (state.sourceMode == cmd.mode) return no("a campanha já está nesse modo de fonte")
            return Decision.Ok(listOf(SourceModeSet(cmd.mode)))
        }
        is AddPlace -> {
            val p = cmd.place
            if (p.id.isBlank() || p.id.length > 80 || p.name.isBlank()) return no("local exige id válido e nome")
            if (p.name.length > 120) return no("nome do local excede 120 caracteres")
            if (p.description.length > 2000) return no("descrição do local excede 2000 caracteres")
            if (p.secret.length > 2000) return no("segredo do local excede 2000 caracteres")
            if (state.places.size >= 200) return no("limite de 200 locais por campanha atingido")
            if (state.places.containsKey(p.id)) return no("local já existe: ${p.id}")
            return Decision.Ok(listOf(PlaceAdded(p)))
        }
        is ConnectPlaces -> {
            if (cmd.fromId == cmd.toId) return no("um local não pode ligar a si mesmo")
            if (cmd.fromId !in state.places) return no("local inexistente: ${cmd.fromId}")
            if (cmd.toId !in state.places) return no("local inexistente: ${cmd.toId}")
            if (cmd.toId in state.placeLinks[cmd.fromId].orEmpty()) return no("passagem já existe")
            if (state.placeLinks[cmd.fromId].orEmpty().size >= 20 || state.placeLinks[cmd.toId].orEmpty().size >= 20) {
                return no("cada local pode ter no máximo 20 passagens")
            }
            return Decision.Ok(listOf(PlacesConnected(cmd.fromId, cmd.toId)))
        }
        is DiscoverPlace -> {
            if (cmd.id !in state.places) return no("local inexistente: ${cmd.id}")
            if (cmd.id in state.knownPlaces) return no("local já descoberto: ${cmd.id}")
            if (state.currentPlaceId != null) {
                val reachableFromKnown = state.knownPlaces.any { known ->
                    cmd.id in state.placeLinks[known].orEmpty()
                }
                if (!reachableFromKnown) return no("local não conectado a nenhuma área já descoberta")
            }
            return Decision.Ok(listOf(PlaceDiscovered(cmd.id)))
        }
        is MoveToPlace -> {
            if (cmd.id !in state.knownPlaces) return no("não é possível viajar para um local desconhecido")
            val current = state.currentPlaceId ?: return no("a campanha ainda não tem local atual")
            if (cmd.id !in state.placeLinks[current].orEmpty()) return no("não existe passagem conhecida direta para esse local")
            return Decision.Ok(listOf(PlayerMovedToPlace(cmd.id)))
        }
        is ProposeRuleAddition -> {
            val rule = cmd.rule
            if (rule.id.isBlank() || rule.id.length > 80 || rule.name.isBlank() || rule.name.length > 120 ||
                rule.description.isBlank() || rule.description.length > 2000) {
                return no("adaptação exige id, nome e descrição dentro dos limites")
            }
            if (rule.origin != RuleOrigin.ADAPTACAO) return no("regra adicional deve ser marcada como adaptação")
            if (rule.approved) return no("a IA não pode propor uma adaptação já aprovada")
            if (state.additionalRules.containsKey(rule.id)) return no("adaptação já existe: ${rule.id}")
            if (rule.effects.isEmpty()) return no("adaptação sem efeito mecânico explícito")
            if (rule.effects.size > 30 || rule.effects.keys.any { it.isBlank() || it.length > 80 } ||
                rule.effects.values.any { it !in -5..5 }) {
                return no("efeitos da adaptação inválidos; máximo 30 escopos e bônus de -5 a +5")
            }
            if (rule.limit.isBlank() || rule.limit.length > 500) return no("adaptação precisa declarar custo ou limite de até 500 caracteres")
            return Decision.Ok(listOf(AdditionalRuleProposed(rule)))
        }
        is ApproveRuleAddition -> {
            val rule = state.additionalRules[cmd.id] ?: return no("adaptação inexistente: ${cmd.id}")
            if (rule.approved) return no("adaptação já aprovada: ${cmd.id}")
            return Decision.Ok(listOf(AdditionalRuleApproved(cmd.id)))
        }
        is RejectRuleAddition -> {
            val rule = state.additionalRules[cmd.id] ?: return no("adaptação inexistente: ${cmd.id}")
            if (rule.approved) return no("adaptação aprovada não pode ser descartada como pendente")
            return Decision.Ok(listOf(AdditionalRuleRejected(cmd.id)))
        }

        is RecordFact ->
            return if (cmd.key.isBlank()) no("chave vazia") else Decision.Ok(listOf(FactRecorded(cmd.key, cmd.value)))

        is NoteMemory -> {
            val text = cmd.text.trim()
            if (text.isEmpty()) return no("nota vazia")
            if (text.length > MEMORY_NOTE_MAX_CHARS) return no("nota acima de $MEMORY_NOTE_MAX_CHARS caracteres")
            val isNpc = state.entities[cmd.subjectId]?.kind == EntityKind.NPC
            if (!isNpc && cmd.subjectId !in state.places) return no("assunto inexistente (só NPC ou lugar): ${cmd.subjectId}")
            if (state.memories[cmd.subjectId]?.lastOrNull() == text) return no("nota repetida")
            return Decision.Ok(listOf(MemoryNoted(cmd.subjectId, text)))
        }
    }
}
