package dev.rpgsolo.ui

import dev.rpgsolo.engine.*
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.SkillTreeCatalogLoader
import dev.rpgsolo.engine.SkillActionKind

data class SkillTreeEntryView(
    val skill: SkillTreeSkill,
    val currentLevel: Int,
    val pointCost: Int,
    val scrollPriceSilver: Long?,
    val hasScroll: Boolean,
    val canUse: Boolean,
    val reason: String?
) {
    val displayName: String get() = if (skill.affinityBound && skill.element != ElementPath.NONE) "${skill.name} — ${elementLabel(skill.element)}" else skill.name
}

/** Operações transacionais de compra, desbloqueio, evolução, ativação e reset da árvore. */
class SkillTreeActions(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val playerId: () -> String,
    private val selectedTreeIds: () -> Set<String>,
    private val classTreeUnlockLevel: (treeId: String) -> Int,
    private val element: () -> ElementPath,
    private val weaponTags: () -> Set<String>,
    private val multiHitAction: (targetId: String, hitCount: Int, damageBonus: Int) -> String?,
    private val onChanged: () -> Unit
) {
    private val definitions: List<SkillTreeSkill> by lazy {
        val stream = SkillTreeActions::class.java.classLoader
            ?.getResourceAsStream("packages/sistema-proprio/habilidades-arvore.txt")
            ?: error("catálogo de habilidades não encontrado")
        val raw = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val parsed = SkillTreeCatalogLoader.load(raw)
        require(parsed.valid) { parsed.errors.joinToString("; ") }
        parsed.skills
    }

    fun all(): List<SkillTreeSkill> = definitions

    fun view(): List<SkillTreeEntryView> {
        val state = session().state
        val hero = state.entities[playerId()] ?: return emptyList()
        return definitions.filter { it.treeId in selectedTreeIds() }.map { rawSkill ->
            val skill = effectiveSkill(rawSkill)
            val level = hero.skillTreeLevels[skill.id] ?: 0
            val validation = SkillTreeProgression.validate(skill, context(hero.skillTreeLevels))
            val scrollId = scrollItemId(skill.id)
            SkillTreeEntryView(
                skill, level,
                if (level == 0) skill.unlockPointCost else SkillTreeProgression.upgradeCost(level),
                skill.scrollRank?.let(SkillTreeEconomy::scrollPriceSilver),
                (state.inventory[playerId()]?.get(scrollId) ?: 0) > 0,
                validation.allowed,
                validation.reasons.takeIf { it.isNotEmpty() }?.joinToString("; ")
            )
        }
    }

    /** Concede automaticamente todas as habilidades básicas gratuitas das origens selecionadas. */
    fun grantSelectedFreeBaseSkills(): String? = try {
        val hero = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
        val pending = definitions.filter {
            it.freeBaseSkill && it.treeId in selectedTreeIds() && it.id !in hero.skillTreeLevels
        }
        if (pending.isEmpty()) return null
        val context = context(hero.skillTreeLevels)
        pending.forEach { skill ->
            val validation = SkillTreeProgression.validate(skill, context)
            require(validation.allowed) { validation.reasons.joinToString("; ") }
        }
        execute(pending.map { ProgressSkillTree(playerId(), it.id, 0, freeBase = true) })
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun grantFreeBaseSkill(skillId: String): String? = try {
        val skill = requireSkill(skillId)
        require(skill.freeBaseSkill && skill.origin in setOf(SkillOrigin.RACE, SkillOrigin.SUBRACE, SkillOrigin.CURSED_CLASS)) {
            "esta habilidade não é uma habilidade básica gratuita"
        }
        require(skill.treeId in selectedTreeIds()) { "a origem desta habilidade não está selecionada na ficha" }
        val hero = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
        val validation = SkillTreeProgression.validate(skill, context(hero.skillTreeLevels))
        require(validation.allowed) { validation.reasons.joinToString("; ") }
        execute(listOf(ProgressSkillTree(playerId(), skill.id, 0, freeBase = true)))
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun buyScroll(skillId: String): String? = try {
        val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
        val skill = requireSkill(skillId)
        require(skill.treeId in selectedTreeIds()) { "esta árvore não pertence às escolhas atuais do personagem" }
        require(!skill.freeBaseSkill && skill.scrollRank != null) { "esta habilidade não usa pergaminho" }
        require(session().state.combat == null) { "não é possível comprar pergaminhos durante o combate" }
        val scrollId = scrollItemId(skill.id)
        require((session().state.inventory[playerId()]?.get(scrollId) ?: 0) == 0) { "o pergaminho desta habilidade já está no inventário" }
        val currencyId = requireNotNull(pkg.currencyItemId) { "o pacote não define moeda" }
        val price = SkillTreeEconomy.scrollPriceSilver(skill.scrollRank)
        val wallet = session().state.inventory[playerId()]
        val payment = pkg.planPayment(wallet?.get(currencyId) ?: 0, pkg.goldItemId?.let { wallet?.get(it) } ?: 0, price.toInt())
            ?: return "saldo insuficiente para o pergaminho (${price} prata)"
        val commands = buildList<Command> {
            if (payment.silver > 0) add(ConsumeItem(playerId(), currencyId, payment.silver))
            if (payment.gold > 0) add(ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
            if (payment.change > 0) add(AddItem(playerId(), currencyId, payment.change))
            add(AddItem(playerId(), scrollId, 1))
        }
        execute(commands)
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }
      catch (e: ArithmeticException) { "preço do pergaminho excede o limite suportado" }

    fun unlockOrUpgrade(skillId: String): String? = try {
        val skill = requireSkill(skillId)
        require(skill.treeId in selectedTreeIds()) { "esta árvore não pertence às escolhas atuais do personagem" }
        require(session().state.combat == null) { "não é possível evoluir habilidades durante o combate" }
        val hero = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
        val currentLevel = hero.skillTreeLevels[skill.id] ?: 0
        if (currentLevel == 0 && skill.freeBaseSkill) return "conceda primeiro a habilidade básica gratuita"
        val validation = SkillTreeProgression.validate(skill, context(hero.skillTreeLevels))
        require(validation.allowed) { validation.reasons.joinToString("; ") }
        if (currentLevel == 0) {
            val scrollId = scrollItemId(skill.id)
            require((session().state.inventory[playerId()]?.get(scrollId) ?: 0) > 0) { "compre ou obtenha o pergaminho desta habilidade primeiro" }
            execute(listOf(ConsumeItem(playerId(), scrollId, 1), ProgressSkillTree(playerId(), skill.id, skill.unlockPointCost)))
        } else {
            execute(listOf(ProgressSkillTree(playerId(), skill.id, SkillTreeProgression.upgradeCost(currentLevel))))
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun targets(): List<Pair<String, String>> {
        val state = session().state
        val combatOrder = state.combat?.order
        return state.entities.values.filter {
            it.id != playerId() && it.alive && !it.downed && (combatOrder == null || it.id in combatOrder)
        }.map { it.id to it.name }.sortedBy { it.second }
    }

    fun revivableTargets(): List<Pair<String, String>> {
        val state = session().state
        val combatOrder = state.combat?.order
        return state.entities.values.filter { !it.alive && (combatOrder == null || it.id in combatOrder) }
            .map { it.id to it.name }.sortedBy { it.second }
    }

    fun activate(skillId: String, targetId: String? = null): String? = try {
        val skill = requireSkill(skillId)
        require(skill.treeId in selectedTreeIds()) { "esta árvore não pertence às escolhas atuais do personagem" }
        require(skill.category != SkillCategory.PASSIVE) { "habilidades passivas são aplicadas automaticamente" }
        val hero = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
        require(hero.alive && !hero.downed) { "personagem não pode agir neste estado" }
        val level = hero.skillTreeLevels[skill.id] ?: 0
        require(level > 0) { "habilidade ainda não foi desbloqueada" }
        val validation = SkillTreeProgression.validate(skill, context(hero.skillTreeLevels))
        require(validation.allowed) { validation.reasons.joinToString("; ") }
        val combat = session().state.combat
        if (combat != null) require(combat.current == playerId()) { "não é o turno do jogador" }
        if (skill.actionKind == SkillActionKind.REVIVE) {
            val reviveTargetId = requireNotNull(targetId) { "selecione quem será ressuscitado" }
            require(reviveTargetId in revivableTargets().map { it.first }) { "o alvo não está morto ou não pode ser ressuscitado" }
            val resource = requireNotNull(skill.actionResource) { "a habilidade não define o recurso de custo" }
            val commands = buildList<Command> {
                if (skill.actionCost > 0) add(SpendResource(playerId(), resource, skill.actionCost))
                add(Revive(reviveTargetId, skill.name))
            }
            return execute(commands)
        }
        if (skill.actionKind == SkillActionKind.MULTI_HIT || skill.actionKind == SkillActionKind.POWER_STRIKE) {
            require(combat != null) { "ataques especiais só podem ser usados durante combate" }
            val selectedTarget = requireNotNull(targetId) { "selecione um alvo para o multiataque" }
            require(selectedTarget in targets().map { it.first } && selectedTarget in combat.order) { "alvo fora do combate" }
            require(skill.actionCost == 0) { "ataque especial com custo de recurso ainda não foi configurado" }
            val bonus = skill.effects.firstOrNull { it.target == "dano" }?.valueAt(level) ?: 0
            return multiHitAction(selectedTarget, skill.hitCount, bonus)
        }
        require(skill.effects.isNotEmpty()) { "esta habilidade ainda não tem efeito mecânico executável" }
        require(combat != null || skill.effects.none { it.durationRounds != null }) {
            "habilidades com duração em rodadas só podem ser ativadas durante combate"
        }
        val selfEffects = skill.effects.filter { it.recipient == SkillEffectRecipient.SELF }.map { effect ->
            ActiveEffect("habilidade:${skill.id}", skill.name, effect.target, effect.valueAt(level), effect.durationRounds ?: 1)
        }
        val targetSpecs = skill.effects.filter { it.recipient == SkillEffectRecipient.TARGET }
        if (targetSpecs.isNotEmpty()) {
            require(targetId != null) { "selecione um alvo para ${skill.name}" }
            require(targetId in targets().map { it.first }) { "alvo inválido ou indisponível" }
            if (combat != null) require(targetId in combat.order) { "alvo fora do combate" }
        }
        val targetEffects = targetSpecs.map { effect ->
            ActiveEffect("habilidade:${skill.id}", skill.name, effect.target, effect.valueAt(level), effect.durationRounds ?: 1)
        }
        val commands = buildList<Command> {
            if (selfEffects.isNotEmpty()) add(ActivateAbility(playerId(), skill.id, AbilityTrigger.MANUAL, AbilityTrigger.MANUAL, selfEffects))
            if (targetEffects.isNotEmpty()) add(ApplyTargetSkillEffects(playerId(), requireNotNull(targetId), skill.id, targetEffects))
        }
        require(commands.isNotEmpty()) { "habilidade sem efeito ativável" }
        execute(commands)
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun reset(): String? = try {
        val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
        require(session().state.combat == null) { "não é possível redefinir habilidades durante o combate" }
        val hero = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
        require(hero.skillTreeInvestments.values.sumOf { it.toLong() } > 0L) { "não há pontos investidos para redefinir" }
        val currencyId = requireNotNull(pkg.currencyItemId) { "o pacote não define moeda" }
        val wallet = session().state.inventory[playerId()]
        val payment = pkg.planPayment(
            wallet?.get(currencyId) ?: 0,
            pkg.goldItemId?.let { wallet?.get(it) } ?: 0,
            SkillTreeEconomy.RESET_PRICE_SILVER.toInt()
        ) ?: return "saldo insuficiente para redefinir habilidades (25.000 prata)"
        val commands = buildList<Command> {
            if (payment.silver > 0) add(ConsumeItem(playerId(), currencyId, payment.silver))
            if (payment.gold > 0) add(ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
            if (payment.change > 0) add(AddItem(playerId(), currencyId, payment.change))
            add(ResetSkillTreeProgress(playerId()))
        }
        execute(commands)
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }
      catch (e: ArithmeticException) { "preço do reset excede o limite suportado" }

    private fun context(levels: Map<String, Int>) = SkillTreeCharacterContext(
        characterLevel = session().state.entities[playerId()]?.level ?: 1,
        element = element(), weaponTags = weaponTags(), knownSkillLevels = levels
    )

    private fun requireSkill(id: String): SkillTreeSkill =
        effectiveSkill(definitions.firstOrNull { it.id == id } ?: throw IllegalArgumentException("habilidade não existe no catálogo: $id"))

    /** Classes secundárias despertam em níveis 10, 20 e 30, respectivamente. */
    private fun effectiveSkill(skill: SkillTreeSkill): SkillTreeSkill {
        var effective = skill
        if (skill.origin == SkillOrigin.CLASS) {
            effective = effective.copy(requiredCharacterLevel = maxOf(skill.requiredCharacterLevel, classTreeUnlockLevel(skill.treeId)))
        }
        if (skill.affinityBound && element() != ElementPath.NONE) effective = effective.copy(element = element())
        return effective
    }

    private fun elementLabel(element: ElementPath): String = when (element) {
        ElementPath.WATER -> "Água"
        ElementPath.FIRE -> "Fogo"
        ElementPath.WIND -> "Vento"
        ElementPath.EARTH -> "Terra"
        ElementPath.METAL -> "Metal"
        ElementPath.YIN -> "Yin"
        ElementPath.YANG -> "Yang"
        ElementPath.NONE -> "sem afinidade"
    }

    private fun scrollItemId(skillId: String) = "pergaminho-habilidade-$skillId"

    private fun execute(commands: List<Command>): String? =
        when (val result = session().executeAll(commands)) {
            is ExecResult.Accepted -> { onChanged(); null }
            is ExecResult.Rejected -> result.reason
        }
}
