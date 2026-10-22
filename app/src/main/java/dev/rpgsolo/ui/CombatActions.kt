package dev.rpgsolo.ui

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.EntityKind
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
    private val bonuses: CombatBonuses
) {
    /** Inicia a iniciativa do jogador e dos NPCs preparados para este encontro; os números vêm do motor. */
    fun startCombat(): String? = try {
        val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
        val initiativeStat = pkg.data.creation["combate_atributo_iniciativa"]
            ?: return "o pacote não define o atributo de iniciativa"
        val initiativeDiceCount = pkg.data.creation["combate_dados_iniciativa"]?.toIntOrNull()
            ?: return "o pacote não define combate_dados_iniciativa"
        val initiativeDiceSides = pkg.data.creation["combate_lados_iniciativa"]?.toIntOrNull()
            ?: return "o pacote não define combate_lados_iniciativa"
        val initiativeOrder = pkg.data.creation["combate_ordem_iniciativa"]
            ?: return "o pacote não define combate_ordem_iniciativa"
        val participants = (listOf(playerId()) + pendingCombatNpcIds).distinct().filter { id ->
            session().state.entities[id]?.alive == true
        }
        require(participants.size >= 2) { "adicione pelo menos um adversário para este encontro" }
        when (val result = session().rollInitiative(
            participants,
            diceCount = initiativeDiceCount,
            diceSides = initiativeDiceSides,
            statKey = initiativeStat,
            highestFirst = initiativeOrder == "MAIOR"
        )) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                pendingCombatNpcIds.clear()
                savePending()
                onChanged()
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }

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

    fun endCombat(): String? = when (val result = session().execute(dev.rpgsolo.engine.EndCombat)) {
        is dev.rpgsolo.engine.ExecResult.Accepted -> { onChanged(); null }
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

    fun combatTargets(): List<CombatTarget> = session().state.combat?.order.orEmpty()
        .filter { it != playerId() }
        .mapNotNull { id ->
            session().state.entities[id]?.takeIf { it.alive }?.let { entity ->
                val resourceId = activePackage()?.manifest?.deathResource
                val resource = resourceId?.let { entity.resources[it] }
                val condition = when {
                    resource == null || resource.max <= 0 -> ""
                    resource.current * 4 <= resource.max -> "gravemente ferido"
                    resource.current * 4 <= resource.max * 3 -> "ferido"
                    else -> "ileso"
                }
                CombatTarget(entity.id, entity.name, entity.rulesValidated, condition)
            }
        }

    fun isPlayerCombatTurn(): Boolean =
        session().state.combat?.current == playerId()

    fun canDefend(): Boolean = (activePackage()?.engineConfig()?.defendBonus ?: 0) > 0 && session().state.entities[playerId()]?.alive == true

    /** Só recompensa quando o combate terminou e todos os NPCs que participaram foram derrotados. */
    fun awardCombatVictoryXp(combatantIds: Collection<String>): Int {
        if (session().state.combat != null || session().state.entities[playerId()]?.alive != true) return 0
        val npcIds = combatantIds.filter { session().state.entities[it]?.kind == EntityKind.NPC }
        if (npcIds.isEmpty() || npcIds.any { session().state.entities[it]?.alive == true }) return 0
        val xp = activePackage()?.data?.creation?.get("combate_xp_vitoria")?.toIntOrNull() ?: 0
        if (xp <= 0) return 0
        return when (session().execute(dev.rpgsolo.engine.GainXp(playerId(), xp))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> xp
            is dev.rpgsolo.engine.ExecResult.Rejected -> 0
        }
    }

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
        val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
        val skill = bonuses.bestAvailableAttackSkill(attacker.id)
        val skillLevel = skill?.second ?: 0
        val skillDamageBonus = skill?.first?.effects?.firstOrNull { it.target == "dano" }?.valueAt(skillLevel) ?: 0
        val strikeCount = skill?.first?.hitCount?.coerceAtLeast(1) ?: 1
        val elementalScope = if (skill?.first?.affinityBound == true) {
            when (attacker.skillTreeElement) {
                "WATER" -> "agua"
                "FIRE" -> "fogo"
                "WIND" -> "vento"
                "EARTH" -> "terra"
                "METAL" -> "metal"
                "YIN" -> "yin"
                "YANG" -> "yang"
                else -> null
            }
        } else null
        val attackStat = pkg.data.rules.firstOrNull { it.id == "combate_atributo_ataque" }?.value ?: "poder"
        val defenseStat = pkg.data.rules.firstOrNull { it.id == "combate_atributo_defesa" }?.value ?: "defesa"
        val attackConditionShift = bonuses.conditionDiceShift(attacker.id, attackStat)
        val targetConditionShift = bonuses.conditionDiceShift(targetId, defenseStat)
        val attackMark = session().mark()
        val spec = AttackSpec(
            attackerId = attacker.id,
            targetId = targetId,
            attackStat = attackStat,
            attackBonus = pkg.traitBonus(bonuses.combatTraitIds(attacker.id), "ataque") +
                bonuses.skillTreeAndActiveBonus(attacker.id, "ataque") +
                bonuses.skillTreeAndActiveBonus(attacker.id, "precisao") +
                bonuses.skillTreeAndActiveBonus(attacker.id, "velocidade") +
                attackConditionShift +
                pkg.traitBonus(bonuses.combatTraitIds(attacker.id), attackStat),
            defenseStat = defenseStat,
            defenseBonus = pkg.traitBonus(bonuses.combatTraitIds(targetId), "defesa") +
                bonuses.skillTreeAndActiveBonus(targetId, "defesa") +
                bonuses.skillTreeAndActiveBonus(targetId, "velocidade") +
                bonuses.approvedRuleBonus("defesa") +
                targetConditionShift +
                pkg.traitBonus(bonuses.combatTraitIds(targetId), defenseStat),
            damageDef = pkg.data.damage.firstOrNull { it.id == "corte" }
                ?: pkg.data.damage.firstOrNull() ?: return "O pacote não possui perfil de dano.",
            damageStat = pkg.data.rules.firstOrNull { it.id == "combate_atributo_dano" }?.value ?: attackStat,
            damageBonus = pkg.traitBonus(bonuses.combatTraitIds(attacker.id), "dano") +
                bonuses.skillTreeAndActiveBonus(attacker.id, "dano") +
                skillDamageBonus +
                bonuses.approvedRuleBonus("dano") +
                pkg.traitBonus(bonuses.combatTraitIds(attacker.id), pkg.data.rules.firstOrNull { it.id == "combate_atributo_dano" }?.value ?: attackStat),
            damageReduction = pkg.traitBonus(bonuses.combatTraitIds(targetId), "resistencia_dano") +
                bonuses.skillTreeAndActiveBonus(targetId, "resistencia_dano") +
                (elementalScope?.let { scope ->
                    pkg.traitBonus(bonuses.combatTraitIds(targetId), "resistencia_$scope") +
                        bonuses.skillTreeAndActiveBonus(targetId, "resistencia_$scope")
                } ?: 0),
            attackDiceCount = (pkg.data.rules.firstOrNull { it.id == "combate_dados_ataque" }?.value?.toIntOrNull() ?: 1) + attackConditionShift,
            defenseDiceCount = (pkg.data.rules.firstOrNull { it.id == "combate_dados_defesa" }?.value?.toIntOrNull() ?: 1) + targetConditionShift
        )
        val results = mutableListOf<dev.rpgsolo.engine.AttackResult.Resolved>()
        var rejection: String? = null
        repeat(strikeCount) {
            if (rejection != null || session().state.entities[targetId]?.alive != true) return@repeat
            when (val attack = session().attack(spec)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> results += attack.value
                is dev.rpgsolo.engine.ExecResult.Rejected -> rejection = attack.reason
            }
        }
        if (rejection != null) {
            session().rollbackTo(attackMark)
            return rejection
        }
        if (results.isEmpty()) return "O ataque foi cancelado porque o alvo não pode mais lutar."
        when (val advance = session().execute(NextTurn)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> Unit
            is dev.rpgsolo.engine.ExecResult.Rejected -> {
                session().rollbackTo(attackMark)
                return advance.reason
            }
        }
        onChanged()
        val skillPrefix = skill?.first?.name?.let { "$it — " }.orEmpty()
        val strikeSummary = results.mapIndexed { index, result ->
            "Golpe ${index + 1}: ${result.outcome}; dano ${result.damageRoll.total} - defesa ${result.defenseRoll.total} = ${result.damage}."
        }.joinToString(" ")
        val elementPrefix = elementalScope?.let { "Ataque elemental de $it. " }.orEmpty()
        return "$skillPrefix$elementPrefix${attacker.name} ataca ${target.name}. $strikeSummary"
    }

    /** Ataque local: atributos e bônus vêm do pacote e do equipamento; a IA não escolhe números. */
    fun attack(targetId: String, strikeCount: Int = 1, extraDamageBonus: Int = 0, elementalResistanceScope: String? = null): String? {
        return try {
            require(isPlayerCombatTurn()) { "não é o turno do jogador" }
            require(strikeCount in 1..20) { "quantidade de golpes inválida" }
            if (strikeCount > 1) require(session().state.combat != null) { "multiataque só pode ser usado durante combate" }
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val resource = pkg.manifest.deathResource
                ?: return "o pacote não define o recurso de vida usado no combate"
            val attackStat = pkg.data.creation["combate_atributo_ataque"]
                ?: return "o pacote não define o atributo de ataque"
            val defenseStat = pkg.data.creation["combate_atributo_defesa"]
                ?: return "o pacote não define o atributo de defesa"
            val attackDiceCount = pkg.data.creation["combate_dados_ataque"]?.toIntOrNull()
                ?: return "o pacote não define combate_dados_ataque"
            val attackDiceSides = pkg.data.creation["combate_lados_ataque"]?.toIntOrNull()
                ?: return "o pacote não define combate_lados_ataque"
            val attackComparison = pkg.data.creation["combate_comparacao_ataque"]?.let {
                runCatching { dev.rpgsolo.engine.Comparison.valueOf(it) }.getOrNull()
            } ?: return "o pacote não define uma comparação de ataque válida"
            val baseDamage = pkg.data.creation["combate_dano_base"]?.toIntOrNull()
                ?: return "o pacote não define combate_dano_base"
            val baseDefense = pkg.data.creation["combate_defesa_base"]?.toIntOrNull()
                ?: return "o pacote não define combate_defesa_base"
            val damageDef = pkg.data.creation["combate_dano"]?.let { id ->
                pkg.data.damage.firstOrNull { it.id == id }
                    ?: return "o pacote referencia um perfil de dano inexistente: $id"
            }
            val attackerEquipment = session().state.equipped[playerId()].orEmpty().values
            val targetEquipment = session().state.equipped[targetId].orEmpty().values
            val attackConditionShift = bonuses.conditionDiceShift(playerId(), attackStat)
            val targetConditionShift = bonuses.conditionDiceShift(targetId, defenseStat)
            val combatantsBefore = session().state.combat?.order.orEmpty()
            val attackMark = session().mark()
            val spec = dev.rpgsolo.engine.AttackSpec(
                attackerId = playerId(),
                targetId = targetId,
                attackStat = attackStat,
                defenseStat = defenseStat,
                attackDiceCount = attackDiceCount,
                attackDiceSides = attackDiceSides,
                attackComparison = attackComparison,
                damageResource = resource,
                damage = baseDamage,
                defenseDefault = baseDefense,
                attackBonus = pkg.equipmentBonus(attackerEquipment, "ataque") +
                    pkg.traitBonus(bonuses.combatTraitIds(playerId()), "ataque") +
                    bonuses.skillTreeAndActiveBonus(playerId(), "ataque") +
                    bonuses.skillTreeAndActiveBonus(playerId(), "precisao") +
                    bonuses.skillTreeAndActiveBonus(playerId(), "velocidade") +
                    bonuses.approvedRuleBonus("ataque") +
                    bonuses.approvedRuleBonus(attackStat) + attackConditionShift,
                damageBonus = pkg.equipmentBonus(attackerEquipment, "dano") +
                    pkg.traitBonus(bonuses.combatTraitIds(playerId()), "dano") +
                    bonuses.skillTreeAndActiveBonus(playerId(), "dano") +
                    bonuses.approvedRuleBonus("dano") +
                    (damageDef?.bonusStat?.let(bonuses::approvedRuleBonus) ?: 0) + extraDamageBonus,
                defenseBonus = pkg.equipmentBonus(targetEquipment, "defesa") +
                    pkg.traitBonus(bonuses.combatTraitIds(targetId), "defesa") +
                    bonuses.skillTreeAndActiveBonus(targetId, "defesa") +
                    bonuses.skillTreeAndActiveBonus(targetId, "velocidade") + targetConditionShift,
                damageReduction = pkg.traitBonus(bonuses.combatTraitIds(targetId), "resistencia_dano") +
                    bonuses.skillTreeAndActiveBonus(targetId, "resistencia_dano") +
                    (elementalResistanceScope?.let { scope ->
                        pkg.traitBonus(bonuses.combatTraitIds(targetId), "resistencia_$scope") +
                            bonuses.skillTreeAndActiveBonus(targetId, "resistencia_$scope")
                    } ?: 0),
                damageDiceCount = damageDef?.diceCount ?: 0,
                damageDiceSides = damageDef?.diceSides ?: 0,
                damageStat = damageDef?.bonusStat
            )
            val results = mutableListOf<dev.rpgsolo.engine.AttackResult.Resolved>()
            repeat(strikeCount) {
                if (session().state.entities[targetId]?.alive != true) return@repeat
                when (val result = session().attack(spec)) {
                    is dev.rpgsolo.engine.AttackResult.Resolved -> results += result
                    is dev.rpgsolo.engine.AttackResult.Rejected -> {
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
            val outcome = results.mapIndexed { index, result ->
                val attackText = "${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}"
                if (result.hit) {
                    val damageText = result.damageRoll?.let {
                        " Dano rolado: ${it.expression} [${it.dice.joinToString(", ")}] = ${it.total}."
                    }.orEmpty()
                    "Golpe ${index + 1}: acertou e causou ${result.damage} de dano.$damageText"
                } else "Golpe ${index + 1}: errou (ataque $attackText)."
            }.joinToString(" ")
            val withSources = outcome +
                bonuses.selectedCatalogBonusSummary(pkg, listOf("ataque", "dano")) +
                bonuses.approvedRuleBonusSummary(listOf("ataque", attackStat, "dano", damageDef?.bonusStat ?: ""))
            if (victoryXp > 0) "$withSources Vitória! Você recebeu $victoryXp XP." else withSources
        } catch (e: IllegalArgumentException) { e.message }
          catch (e: IllegalStateException) { e.message }
    }

    /** A postura defensiva concede o bônus do pacote contra o próximo ataque recebido e consome o turno. */
    fun defend(): String? {
        if (!isPlayerCombatTurn()) return "só é possível defender no seu turno"
        val combatantsBefore = session().state.combat?.order.orEmpty()
        return when (val result = session().executeAll(listOf(
            dev.rpgsolo.engine.Defend(playerId()),
            dev.rpgsolo.engine.NextTurn
        ))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                awardCombatVictoryXp(combatantsBefore)
                onChanged()
                "Postura defensiva ativa até o próximo ataque recebido ou o retorno do seu turno."
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** Fuga usa um teste real do pacote; sucesso encerra o combate, falha cede a próxima ação ao adversário. */
    fun fleeCombat(): String? = try {
        require(isPlayerCombatTurn()) { "só é possível fugir no seu turno" }
        val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
        val checkId = listOf("fuga", "esquiva", "furtividade").firstOrNull { id ->
            pkg.data.checks.any { it.id == id }
        } ?: return "o pacote não define um teste de fuga, esquiva ou furtividade"
        val difficulty = pkg.defaultDifficulty()
            ?: return "o pacote não define dificuldade para o teste de fuga"
        val player = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
        val combatantsBefore = session().state.combat?.order.orEmpty()
        val mark = session().mark()
        val base = pkg.check(checkId, player.stats, difficulty, player.skills)
        val adjusted = pkg.adjustCheck(
            base,
            session().state.conditions[playerId()].orEmpty(),
            bonuses.combatTraitIds(playerId()),
            checkId,
            additionalBonuses = bonuses.selectedCatalogBonuses(pkg)
        )
        val adaptation = listOfNotNull(adjusted.checkId, adjusted.stat, checkId).distinct().sumOf(bonuses::approvedRuleBonus)
        val danger = session().state.campaignStyle?.difficultyParams?.get("danger_level")?.roundToInt()?.coerceIn(0, 10) ?: 5
        val shift = danger - 5
        val spec = when (adjusted.comparison) {
            dev.rpgsolo.engine.Comparison.ROLL_OVER -> adjusted.copy(modifier = adjusted.modifier + adaptation - shift)
            dev.rpgsolo.engine.Comparison.ROLL_UNDER -> adjusted.copy(
                difficulty = (adjusted.difficulty + adaptation - shift).coerceAtLeast(1)
            )
            dev.rpgsolo.engine.Comparison.POINTS -> adjusted.copy(
                modifier = (adjusted.modifier + adaptation - shift).coerceAtLeast(0)
            )
        }
        val result = session().check(spec)
        val roll = "${result.spec.label}: ${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}." +
            bonuses.approvedRuleBonusSummary(listOfNotNull(adjusted.checkId, adjusted.stat, checkId))
        if (result.outcome in setOf(
                dev.rpgsolo.engine.Outcome.SUCCESS,
                dev.rpgsolo.engine.Outcome.CRITICAL_SUCCESS
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
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }
}
