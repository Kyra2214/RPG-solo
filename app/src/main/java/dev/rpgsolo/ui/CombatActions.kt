package dev.rpgsolo.ui

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.NextTurn
import dev.rpgsolo.rules.RulePackage
import kotlin.math.roundToInt

/**
 * Início, passagem e fim do combate, visões da ordem e XP de vitória (R-03): extraído do `AppModel` sem mudar regras nem mensagens.
 * Também resolve ataque, defesa, fuga e turno de NPC; os bônus vêm de [CombatBonuses].
 * [pendingNpcIds] é o conjunto de adversários preparados, compartilhado com o `AppModel`; [savePending] o persiste.
 */
class CombatActions(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val playerId: () -> String,
    private val pendingCombatNpcIds: MutableSet<String>,
    private val savePending: () -> Unit,
    private val onChanged: () -> Unit,
    private val bonuses: CombatBonuses,
    /** N-33.2c: avisa quando o companheiro sobe de nível por XP de combate (nome do animal, novo nível). */
    private val onCompanionLevelUp: (String, Int) -> Unit = { _, _ -> },
) {
    /** Inicia a iniciativa do jogador e dos NPCs preparados para este encontro; os números vêm do motor. */
    fun startCombat(): String? =
        try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val initiativeStat =
                pkg.data.creation["combate_atributo_iniciativa"]
                    ?: return "o pacote não define o atributo de iniciativa"
            val initiativeDiceCount =
                pkg.data.creation["combate_dados_iniciativa"]?.toIntOrNull()
                    ?: return "o pacote não define combate_dados_iniciativa"
            val initiativeDiceSides =
                pkg.data.creation["combate_lados_iniciativa"]?.toIntOrNull()
                    ?: return "o pacote não define combate_lados_iniciativa"
            val initiativeOrder =
                pkg.data.creation["combate_ordem_iniciativa"]
                    ?: return "o pacote não define combate_ordem_iniciativa"
            val participants =
                (listOf(playerId()) + pendingCombatNpcIds).distinct().filter { id ->
                    session().state.entities[id]?.alive == true
                }
            require(participants.size >= 2) { "adicione pelo menos um adversário para este encontro" }
            when (
                val result =
                    session().rollInitiative(
                        participants,
                        diceCount = initiativeDiceCount,
                        diceSides = initiativeDiceSides,
                        statKey = initiativeStat,
                        highestFirst = initiativeOrder == "MAIOR",
                    )
            ) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    pendingCombatNpcIds.clear()
                    savePending()
                    onChanged()
                    null
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        } catch (e: IllegalArgumentException) {
            e.message
        }

    /** Permite passar o turno apenas ao jogador; turnos de NPC são resolvidos pela ação mecânica correspondente. */
    fun nextCombatTurn(): String? {
        if (!isPlayerCombatTurn()) return "só é possível passar o turno durante o turno do jogador"
        val combatantsBefore = session().state.combat?.order.orEmpty()
        return when (val result = session().execute(dev.rpgsolo.engine.NextTurn)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                awardCombatVictoryXp(combatantsBefore)
                onChanged()
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    fun endCombat(): String? =
        when (val result = session().execute(dev.rpgsolo.engine.EndCombat)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                onChanged()
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    fun combatState(): dev.rpgsolo.engine.CombatState? = session().state.combat

    fun combatantName(id: String): String = session().state.entities[id]?.name ?: id

    /** Ordem de iniciativa em palavras; nunca expõe atributos ou recursos dos NPCs. */
    fun combatOrderDescription(): String? {
        val combat = session().state.combat ?: return null
        return combat.order.mapNotNull { id ->
            val entity = session().state.entities[id]?.takeIf { it.alive } ?: return@mapNotNull null
            if (id == playerId()) "Você" else entity.name
        }.joinToString(" → ").ifBlank { null }
    }

    fun combatTargets(): List<CombatTarget> =
        session().state.combat?.order.orEmpty()
            .filter { it != playerId() }
            .mapNotNull { id ->
                session().state.entities[id]?.takeIf { it.alive }?.let { entity ->
                    val resourceId = activePackage()?.manifest?.deathResource
                    val resource = resourceId?.let { entity.resources[it] }
                    val condition =
                        when {
                            resource == null || resource.max <= 0 -> ""
                            resource.current * 4 <= resource.max -> "gravemente ferido"
                            resource.current * 4 <= resource.max * 3 -> "ferido"
                            else -> "ileso"
                        }
                    CombatTarget(entity.id, entity.name, entity.rulesValidated, condition)
                }
            }

    fun isPlayerCombatTurn(): Boolean = session().state.combat?.current == playerId()

    fun canDefend(): Boolean =
        (activePackage()?.engineConfig()?.defendBonus ?: 0) > 0 && session().state.entities[playerId()]?.alive == true

    /** Só recompensa quando o combate terminou e todos os NPCs que participaram foram derrotados. */
    fun awardCombatVictoryXp(combatantIds: Collection<String>): Int {
        if (session().state.combat != null || session().state.entities[playerId()]?.alive != true) return 0
        val companionIds = session().state.companions.values.filter { it.ownerId == playerId() }.map { it.entityId }.toSet()
        val npcIds = combatantIds.filter { session().state.entities[it]?.kind == EntityKind.NPC && it !in companionIds }
        if (npcIds.isEmpty() || npcIds.any { session().state.entities[it]?.alive == true }) return 0
        val config = activePackage()?.engineConfig()
        val fixed = activePackage()?.data?.creation?.get("combate_xp_vitoria")?.toIntOrNull() ?: 0

        fun xpFor(level: Int): Int =
            if ((config?.victoryXpPercent ?: 0) > 0) {
                npcIds.sumOf { config!!.battleXp(level, session().state.entities[it]?.level ?: 1) }
            } else {
                fixed
            }
        // N-33.1: o companheiro vivo que participou ganha XP pelo nível dele (única fonte de XP do animal).
        for (animalId in combatantIds.filter { it in companionIds }) {
            val animal = session().state.entities[animalId]?.takeIf { it.alive } ?: continue
            val animalXp = xpFor(animal.level)
            if (animalXp > 0) {
                val before = animal.level
                session().execute(dev.rpgsolo.engine.GainCompanionXp(animalId, animalXp))
                val after = session().state.entities[animalId]?.level ?: before
                if (after > before) onCompanionLevelUp(animal.name, after)
            }
        }
        val xp = xpFor(session().state.entities[playerId()]?.level ?: 1)
        if (xp <= 0) return 0
        return when (session().execute(dev.rpgsolo.engine.GainXp(playerId(), xp))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> xp
            is dev.rpgsolo.engine.ExecResult.Rejected -> 0
        }
    }

    /** Ataque manual pelo pipeline unificado; o resultado persistido alimenta o ClashCard sem recálculo. */
    fun unifiedAttack(
        targetId: String,
        attackerId: String = playerId(),
    ): String? {
        val combat = session().state.combat ?: return "Não há combate ativo."
        if (combat.current != attackerId) return "não é o turno de $attackerId"
        val mark = session().mark()
        return when (val result = strike(attackerId, targetId)) {
            is dev.rpgsolo.engine.UnifiedAttackResult.Rejected -> result.reason
            is dev.rpgsolo.engine.UnifiedAttackResult.Resolved -> {
                val targetName = session().state.entities[targetId]?.name ?: targetId
                when (val next = session().execute(NextTurn)) {
                    is dev.rpgsolo.engine.ExecResult.Accepted -> {
                        if (attackerId == playerId()) awardCombatVictoryXp(combat.order)
                        onChanged()
                        val attackerName = session().state.entities[attackerId]?.name ?: attackerId
                        if (result.clash.missed) "$attackerName errou o ataque em $targetName." else "$attackerName atacou $targetName."
                    }
                    is dev.rpgsolo.engine.ExecResult.Rejected -> {
                        session().rollbackTo(mark)
                        next.reason
                    }
                }
            }
        }
    }

    /**
     * N-09: único golpe do jogo (jogador, NPC, aliado, chefe, habilidades): pipeline unificado de acerto, crítico, ataque e defesa.
     * [extraAttack] soma ao ataque (habilidades); [elementalScope] soma resistência elementar do alvo. Não avança o turno.
     */
    private fun strike(
        attackerId: String,
        targetId: String,
        extraAttack: Int = 0,
        elementalScope: String? = null,
    ): dev.rpgsolo.engine.UnifiedAttackResult {
        fun rejected(reason: String) = dev.rpgsolo.engine.UnifiedAttackResult.Rejected(reason)
        val pkg = activePackage() ?: return rejected("pacote de regras não está ativo")
        val resource = pkg.manifest.deathResource ?: return rejected("o pacote não define o recurso de vida usado no combate")
        val attackStat = pkg.data.creation["combate_atributo_ataque"] ?: return rejected("o pacote não define o atributo de ataque")
        val defenseStat = pkg.data.creation["combate_atributo_defesa"] ?: return rejected("o pacote não define o atributo de defesa")
        val hitStat = pkg.data.creation["combate_atributo_acerto"]
        val dodgeStat = pkg.data.creation["combate_atributo_esquiva"]
        val luckStat = pkg.data.creation["combate_atributo_sorte"]
        val defendBonus = pkg.engineConfig().defendBonus
        val combatBonuses =
            dev.rpgsolo.engine.ModifierProvider { state, entity, target ->
                val scopes =
                    when (target) {
                        attackStat -> listOf("ataque", "dano")
                        defenseStat -> listOf("defesa")
                        else -> listOf(target)
                    }
                val external =
                    bonuses.externalSkillAndCurseBonuses(entity.id, scopes.toSet() + target)
                        .filter { it.scope in scopes || it.scope == target }
                        .mapIndexed { index, effect ->
                            dev.rpgsolo.engine.Modifier(
                                sourceId = "unified:${entity.id}:${effect.source}:$index",
                                label = effect.source,
                                target = target,
                                value = effect.bonus,
                            )
                        }
                val total =
                    scopes.sumOf { scope ->
                        pkg.equipmentBonus(state.equipped[entity.id].orEmpty().values, scope, bonuses.wearerProfile(entity.id)) +
                            dev.rpgsolo.engine.Tattoos.bonus(state.tattoos[entity.id], scope) +
                            dev.rpgsolo.engine.Gems.bonus(state.gems[entity.id], state.equipped[entity.id], scope) +
                            pkg.traitBonus(bonuses.combatTraitIds(entity.id), scope) +
                            bonuses.approvedRuleBonus(scope)
                    }
                val extras = mutableListOf<dev.rpgsolo.engine.Modifier>()
                if (total != 0) {
                    extras += dev.rpgsolo.engine.Modifier("unified:${entity.id}:package:$target", "fontes do pacote", target, total)
                }
                if (target == attackStat || target == defenseStat) {
                    val shift = bonuses.conditionDiceShift(entity.id, target)
                    if (shift != 0) extras += dev.rpgsolo.engine.Modifier("unified:${entity.id}:conditions:$target", "condições", target, shift)
                }
                if (target == attackStat && entity.id == attackerId && extraAttack != 0) {
                    extras += dev.rpgsolo.engine.Modifier("unified:${entity.id}:skill-extra", "habilidade", target, extraAttack)
                }
                if (target == defenseStat && defendBonus > 0 && state.combat?.defending?.contains(entity.id) == true) {
                    extras += dev.rpgsolo.engine.Modifier("unified:${entity.id}:defend", "postura defensiva", target, defendBonus)
                }
                external + extras
            }
        val reduction =
            pkg.traitBonus(bonuses.combatTraitIds(targetId), "resistencia_dano") +
                bonuses.skillTreeAndActiveBonus(targetId, "resistencia_dano") +
                (
                    elementalScope?.let { scope ->
                        pkg.traitBonus(bonuses.combatTraitIds(targetId), "resistencia_$scope") +
                            bonuses.skillTreeAndActiveBonus(targetId, "resistencia_$scope")
                    } ?: 0
                )
        val weaponId = session().state.equipped[attackerId]?.get(dev.rpgsolo.rules.WEAPON_SLOT)
        val weapon = weaponId?.let { pkg.item(it) }
        val none = dev.rpgsolo.engine.ElementPath.NONE
        val attackElement =
            elementalScope?.let { dev.rpgsolo.engine.ElementChart.parse(it) } ?: weaponId?.let { id ->
                pkg.effectiveElement(id).takeIf { it != none } ?: scrollElement(attackerId, dev.rpgsolo.rules.WEAPON_SLOT, id)
            } ?: none
        val chestId = session().state.equipped[targetId]?.get(dev.rpgsolo.rules.CHEST_SLOT)
        val chestElement =
            chestId?.let { id ->
                dev.rpgsolo.rules.ItemRef.enchantment(id).takeIf { it != none } ?: scrollElement(targetId, dev.rpgsolo.rules.CHEST_SLOT, id)
            } ?: none
        val affinity =
            session().state.entities[targetId]?.skillTreeElement?.takeIf { it.isNotBlank() }
                ?.let { dev.rpgsolo.engine.ElementChart.parse(it) } ?: none
        val elementPercent =
            if (chestElement != none) {
                dev.rpgsolo.engine.ElementChart.armorPercent(setOf(attackElement), chestElement)
            } else {
                dev.rpgsolo.engine.ElementChart.percent(setOf(attackElement), affinity)
            }
        val result =
            session().unifiedAttackBetween(
                attackerId,
                targetId,
                listOfNotNull(combatBonuses, pkg.exceptionRegistry()),
                attackStat,
                defenseStat,
                damageResource = resource,
                hitStat = hitStat,
                dodgeStat = dodgeStat,
                luckStat = luckStat,
                damageReduction = reduction.coerceAtLeast(0),
                luckItemBonus =
                    luckStat?.let {
                        pkg.equipmentBonus(session().state.equipped[attackerId].orEmpty().values, it, bonuses.wearerProfile(attackerId))
                    } ?: 0,
                elementPercent = elementPercent,
            )
        val conditionId = weapon?.conditionId
        if (weapon != null && conditionId != null && elementalScope == null && result is dev.rpgsolo.engine.UnifiedAttackResult.Resolved &&
            !result.clash.missed && result.clash.damage > 0
        ) {
            val luck = luckStat?.let { session().state.entities[attackerId]?.stats?.get(it) } ?: 0
            val def = pkg.data.conditions.firstOrNull { it.id == conditionId }
            session().applyConditionByChance(
                targetId,
                conditionId,
                dev.rpgsolo.engine.ConditionChance.chance(weapon.conditionChance, luck),
                def?.durationRounds,
                def?.damagePerRound ?: 0,
                def?.damageResource ?: "hp",
                def?.damagePercent ?: 0,
                if (chestElement != none) 50 else 100,
            )
        }
        return result
    }

    /** N-18: elemento do pergaminho ativo na peça [itemId] equipada no [slot] de [ownerId]; NONE se não houver ou for de outra peça. */
    private fun scrollElement(
        ownerId: String,
        slot: String,
        itemId: String,
    ): dev.rpgsolo.engine.ElementPath =
        session().state.scrollEnchants[ownerId]?.get(slot)?.takeIf { it.itemId == itemId }?.element ?: dev.rpgsolo.engine.ElementPath.NONE

    /** Resolve o ataque do inimigo atual e só então avança o turno; nenhum número vem da IA. */
    fun resolveNpcTurn(): String? {
        val combat = session().state.combat ?: return "Não há combate ativo."
        if (combat.current == playerId()) return null
        val attacker = session().state.entities[combat.current] ?: return "Atacante não encontrado."
        if (!attacker.alive || attacker.downed) {
            session().execute(NextTurn)
            onChanged()
            return "Turno pulado: ${attacker.name} não pode agir."
        }
        val targetId = playerId()
        val target = session().state.entities[targetId] ?: return "Alvo não encontrado."
        if (!target.alive || target.downed) {
            session().execute(NextTurn)
            onChanged()
            return "Turno pulado: alvo não pode continuar lutando."
        }
        // Jogador e NPC usam o mesmo serviço; a IA escolhe apenas a ação e o alvo.
        return unifiedAttack(targetId, combat.current)
    }

    /** Ataque local (inclui multiataque e habilidades com custo): cada golpe passa pelo pipeline unificado; a IA não escolhe números. */
    fun attack(
        targetId: String,
        strikeCount: Int = 1,
        extraDamageBonus: Int = 0,
        elementalResistanceScope: String? = null,
        /** Habilidade com custo: recurso e quantidade gastos no mesmo lote do ataque (recusa ou falha desfaz tudo). */
        spendResource: String? = null,
        spendCost: Int = 0,
    ): String? {
        return try {
            require(isPlayerCombatTurn()) { "não é o turno do jogador" }
            require(strikeCount in 1..20) { "quantidade de golpes inválida" }
            if (strikeCount > 1) require(session().state.combat != null) { "multiataque só pode ser usado durante combate" }
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val damageDef = pkg.data.creation["combate_dano"]?.let { id -> pkg.data.damage.firstOrNull { it.id == id } }
            val combatantsBefore = session().state.combat?.order.orEmpty()
            val attackMark = session().mark()
            if (spendResource != null && spendCost > 0) {
                when (val spent = session().execute(dev.rpgsolo.engine.SpendResource(playerId(), spendResource, spendCost))) {
                    is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
                    is dev.rpgsolo.engine.ExecResult.Rejected -> return spent.reason
                }
            }
            val results = mutableListOf<dev.rpgsolo.engine.ClashResult>()
            repeat(strikeCount) {
                if (session().state.entities[targetId]?.alive != true) return@repeat
                when (val result = strike(playerId(), targetId, extraDamageBonus, elementalResistanceScope)) {
                    is dev.rpgsolo.engine.UnifiedAttackResult.Resolved -> results += result.clash
                    is dev.rpgsolo.engine.UnifiedAttackResult.Rejected -> {
                        session().rollbackTo(attackMark)
                        return result.reason
                    }
                }
            }
            if (results.isEmpty()) {
                session().rollbackTo(attackMark)
                return "nenhum golpe foi resolvido"
            }
            if (session().state.combat != null) {
                when (val advance = session().execute(dev.rpgsolo.engine.NextTurn)) {
                    is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
                    is dev.rpgsolo.engine.ExecResult.Rejected -> {
                        session().rollbackTo(attackMark)
                        return advance.reason
                    }
                }
            }
            val victoryXp = awardCombatVictoryXp(combatantsBefore)
            onChanged()
            val outcome =
                results.mapIndexed { index, clash ->
                    when {
                        clash.missed -> "Golpe ${index + 1}: errou (acerto ${clash.hit?.roll?.total} contra ${clash.hit?.chance}%)."
                        clash.damage > 0 ->
                            "Golpe ${index + 1}: acertou${if (clash.critical) " (crítico)" else ""} e causou ${clash.damage} de dano."
                        else -> "Golpe ${index + 1}: acertou, mas a defesa absorveu o dano."
                    }
                }.joinToString(" ")
            val attackStat = pkg.data.creation["combate_atributo_ataque"].orEmpty()
            val withSources =
                outcome +
                    bonuses.selectedCatalogBonusSummary(pkg, listOf("ataque", "dano")) +
                    bonuses.approvedRuleBonusSummary(listOf("ataque", attackStat, "dano", damageDef?.bonusStat ?: ""))
            if (victoryXp > 0) "$withSources Vitória! Você recebeu $victoryXp XP." else withSources
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }
    }

    /** A postura defensiva concede o bônus do pacote contra o próximo ataque recebido e consome o turno. */
    fun defend(): String? {
        if (!isPlayerCombatTurn()) return "só é possível defender no seu turno"
        val combatantsBefore = session().state.combat?.order.orEmpty()
        return when (
            val result =
                session().executeAll(
                    listOf(
                        dev.rpgsolo.engine.Defend(playerId()),
                        dev.rpgsolo.engine.NextTurn,
                    ),
                )
        ) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                awardCombatVictoryXp(combatantsBefore)
                onChanged()
                "Postura defensiva ativa até o próximo ataque recebido ou o retorno do seu turno."
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** Fuga usa um teste real do pacote; sucesso encerra o combate, falha cede a próxima ação ao adversário. */
    fun fleeCombat(): String? =
        try {
            require(isPlayerCombatTurn()) { "só é possível fugir no seu turno" }
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val checkId =
                listOf("fuga", "esquiva", "furtividade").firstOrNull { id ->
                    pkg.data.checks.any { it.id == id }
                } ?: return "o pacote não define um teste de fuga, esquiva ou furtividade"
            val difficulty =
                pkg.defaultDifficulty()
                    ?: return "o pacote não define dificuldade para o teste de fuga"
            val player = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
            val combatantsBefore = session().state.combat?.order.orEmpty()
            val mark = session().mark()
            val base = pkg.check(checkId, player.stats, difficulty, player.skills)
            val adjusted =
                pkg.adjustCheck(
                    base,
                    session().state.conditions[playerId()].orEmpty(),
                    bonuses.combatTraitIds(playerId()),
                    checkId,
                    additionalBonuses = bonuses.selectedCatalogBonuses(pkg),
                )
            val adaptation = listOfNotNull(adjusted.checkId, adjusted.stat, checkId).distinct().sumOf(bonuses::approvedRuleBonus)
            val danger = session().state.campaignStyle?.difficultyParams?.get("danger_level")?.roundToInt()?.coerceIn(0, 10) ?: 5
            val shift = danger - 5
            val spec =
                when (adjusted.comparison) {
                    dev.rpgsolo.engine.Comparison.ROLL_OVER -> adjusted.copy(modifier = adjusted.modifier + adaptation - shift)
                    dev.rpgsolo.engine.Comparison.ROLL_UNDER ->
                        adjusted.copy(
                            difficulty = (adjusted.difficulty + adaptation - shift).coerceAtLeast(1),
                        )
                    dev.rpgsolo.engine.Comparison.POINTS ->
                        adjusted.copy(
                            modifier = (adjusted.modifier + adaptation - shift).coerceAtLeast(0),
                        )
                }
            val result = session().check(spec)
            val roll =
                "${result.spec.label}: ${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}." +
                    bonuses.approvedRuleBonusSummary(listOfNotNull(adjusted.checkId, adjusted.stat, checkId))
            if (result.outcome in
                setOf(
                    dev.rpgsolo.engine.Outcome.SUCCESS,
                    dev.rpgsolo.engine.Outcome.CRITICAL_SUCCESS,
                )
            ) {
                when (val ended = session().execute(dev.rpgsolo.engine.EndCombat)) {
                    is dev.rpgsolo.engine.ExecResult.Accepted -> {
                        onChanged()
                        "$roll Você conseguiu fugir e encerrou o combate."
                    }
                    is dev.rpgsolo.engine.ExecResult.Rejected -> {
                        session().rollbackTo(mark)
                        ended.reason
                    }
                }
            } else {
                when (val advanced = session().execute(dev.rpgsolo.engine.NextTurn)) {
                    is dev.rpgsolo.engine.ExecResult.Accepted -> {
                        awardCombatVictoryXp(combatantsBefore)
                        onChanged()
                        "$roll Você não conseguiu escapar; o adversário age agora."
                    }
                    is dev.rpgsolo.engine.ExecResult.Rejected -> {
                        session().rollbackTo(mark)
                        advanced.reason
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }
}
