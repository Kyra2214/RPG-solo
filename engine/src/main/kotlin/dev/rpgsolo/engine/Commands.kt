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
data class AddItem(val ownerId: String, val itemId: String, val qty: Int = 1) : Command
data class ConsumeItem(val ownerId: String, val itemId: String, val qty: Int = 1) : Command
data class EquipItem(val ownerId: String, val itemId: String, val slot: String) : Command
data class UnequipItem(val ownerId: String, val slot: String) : Command
data class GainXp(val entityId: String, val amount: Int) : Command
/** Ressurreição exige justificativa explícita. */
data class Revive(val entityId: String, val reason: String) : Command
data class RecordFact(val key: String, val value: String) : Command
/** Jogador descobriu algo. [believed] = informação falsa/incerta, guardada como "acreditada". */
data class LearnKnowledge(val key: String, val text: String, val source: String, val believed: Boolean = false) : Command
data class MakePromise(val id: String, val npcId: String, val text: String) : Command
data class KeepPromise(val id: String) : Command
/** 8.2.2: muda a relação de [fromId] com [toId] (quanto o primeiro gosta/confia no segundo), de -100 a 100. */
data class ChangeRelation(val fromId: String, val toId: String, val delta: Int, val reason: String) : Command
/** 8.2.3: relógio de agenda (ameaça ou plano de NPC) com [segments] casas. */
data class CreateClock(val id: String, val name: String, val segments: Int) : Command
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
data class AddPlace(val place: Place) : Command
data class ConnectPlaces(val fromId: String, val toId: String) : Command
data class DiscoverPlace(val id: String) : Command
/** Só o jogador pode mover-se; a IA não move a miniatura automaticamente. */
data class MoveToPlace(val id: String) : Command
/** [order] = ordem de iniciativa já decidida (ver GameEngine.rollInitiative). */
data class StartCombat(val order: List<String>) : Command
object NextTurn : Command
object EndCombat : Command

/** Configurações vindas do pacote de regras. */
@Serializable
data class EngineConfig(
    /** Recurso que, ao chegar a 0, mata (ex.: "pv"). null = o sistema não mata automaticamente. */
    val deathResource: String? = "hp",
    /** XP total necessário para o nível 2, 3, 4... (crescente). Vazio = o sistema não sobe de nível por XP. */
    val levelThresholds: List<Int> = emptyList()
) {
    init {
        require(levelThresholds.zipWithNext().all { (a, b) -> a < b } && levelThresholds.all { it > 0 }) {
            "levelThresholds deve ser positivo e estritamente crescente"
        }
    }

    /** Nível correspondente a um total de XP. */
    fun levelFor(xp: Int): Int = 1 + levelThresholds.count { xp >= it }
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
            if (newValue == 0 && config.deathResource == cmd.resource) events += EntityDied(ent.id)
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
            return Decision.Ok(listOf(ResourceChanged(ent.id, cmd.resource, newValue, newValue - res.current)))
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
            val events = mutableListOf<Event>(XpGained(ent.id, cmd.amount))
            val newLevel = config.levelFor(ent.xp + cmd.amount)
            if (newLevel > ent.level) events += LevelGained(ent.id, newLevel)
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
            if (cmd.id.isBlank() || cmd.name.isBlank()) return no("relógio exige id e nome")
            if (cmd.segments !in 2..12) return no("relógio precisa de 2 a 12 casas")
            if (state.clocks.containsKey(cmd.id)) return no("relógio já existe: ${cmd.id}")
            return Decision.Ok(listOf(ClockCreated(cmd.id, cmd.name.trim(), cmd.segments)))
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
                                val resource = ent.resources[effect.damageResource] ?: continue
                                val newValue = maxOf(0, resource.current - effect.damagePerRound)
                                val remaining = effect.remainingRounds?.let { maxOf(0, it - 1) }
                                val tick = ConditionTicked(
                                    entityId, condition, remaining, effect.damageResource,
                                    newValue, resource.current - newValue
                                )
                                events += tick
                                tickState = tickState.apply(tick)
                                if (newValue == 0 && config.deathResource == effect.damageResource) {
                                    val death = EntityDied(entityId)
                                    events += death
                                    tickState = tickState.apply(death)
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
                            if (combatKinds.size >= 2 && aliveKinds.size < 2) events += CombatEnded
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
            val npc = entity(cmd.dossier.npcId) ?: return no("NPC inexistente: ${cmd.dossier.npcId}")
            if (npc.kind != EntityKind.NPC) return no("dossiê privado só pode pertencer a NPC")
            if (cmd.dossier.npcId.isBlank()) return no("dossiê sem id de NPC")
            if (cmd.dossier.secrets.any { it.id.isBlank() || it.text.isBlank() }) return no("segredo sem id ou texto")
            if (cmd.dossier.secrets.map { it.id }.distinct().size != cmd.dossier.secrets.size) return no("id de segredo repetido")
            return Decision.Ok(listOf(NpcDossierStored(cmd.dossier)))
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
            if (rule.id.isBlank() || rule.name.isBlank() || rule.description.isBlank()) return no("adaptação sem id, nome ou descrição")
            if (rule.origin != RuleOrigin.ADAPTACAO) return no("regra adicional deve ser marcada como adaptação")
            if (rule.approved) return no("a IA não pode propor uma adaptação já aprovada")
            if (state.additionalRules.containsKey(rule.id)) return no("adaptação já existe: ${rule.id}")
            if (rule.effects.isEmpty()) return no("adaptação sem efeito mecânico explícito")
            if (rule.effects.values.any { it !in -5..5 }) return no("efeitos da adaptação devem ficar entre -5 e +5")
            if (rule.limit.isBlank()) return no("adaptação precisa declarar custo ou limite")
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
    }
}
