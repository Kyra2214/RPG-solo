package dev.rpgsolo.ui

import dev.rpgsolo.engine.*
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.SkillTreeCatalogLoader

data class SkillTreeEntryView(
    val skill: SkillTreeSkill,
    val currentLevel: Int,
    val pointCost: Int,
    val scrollPriceSilver: Long?,
    val hasScroll: Boolean,
    val canUse: Boolean,
    val reason: String?
)

/** Operações transacionais de compra, desbloqueio, evolução, ativação e reset da árvore. */
class SkillTreeActions(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val playerId: () -> String,
    private val selectedTreeIds: () -> Set<String>,
    private val element: () -> ElementPath,
    private val weaponTags: () -> Set<String>,
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
        return definitions.filter { it.treeId in selectedTreeIds() }.map { skill ->
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
            ?: return "saldo insuficiente para o pergaminho (\${price} prata)"
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

    fun activate(skillId: String): String? = try {
        val skill = requireSkill(skillId)
        require(skill.treeId in selectedTreeIds()) { "esta árvore não pertence às escolhas atuais do personagem" }
        require(skill.category != SkillCategory.PASSIVE) { "habilidades passivas são aplicadas automaticamente" }
        require(skill.effects.isNotEmpty()) { "esta habilidade ainda não tem efeito mecânico executável" }
        val hero = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
        val level = hero.skillTreeLevels[skill.id] ?: 0
        require(level > 0) { "habilidade ainda não foi desbloqueada" }
        val validation = SkillTreeProgression.validate(skill, context(hero.skillTreeLevels))
        require(validation.allowed) { validation.reasons.joinToString("; ") }
        val durations = skill.effects.mapNotNull { it.durationRounds }.distinct()
        require(durations.size <= 1) { "os efeitos da habilidade exigem durações incompatíveis" }
        require(skill.effects.all { it.conditionId == null }) { "esta habilidade exige uma ação especial ainda não suportada por esta ativação" }
        val spec = AbilitySpec(
            id = skill.id, name = skill.name, cost = 0, trigger = AbilityTrigger.MANUAL,
            bonuses = skill.effects.map { AbilityBonus(it.target, it.valueAt(level)) },
            fixedRounds = durations.singleOrNull()
        )
        when (val result = session().activateAbility(playerId(), spec, AbilityTrigger.MANUAL)) {
            is AbilityResult.Activated -> { onChanged(); null }
            is AbilityResult.Rejected -> result.reason
        }
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
        definitions.firstOrNull { it.id == id } ?: throw IllegalArgumentException("habilidade não existe no catálogo: $id")

    private fun scrollItemId(skillId: String) = "pergaminho-habilidade-$skillId"

    private fun execute(commands: List<Command>): String? =
        when (val result = session().executeAll(commands)) {
            is ExecResult.Accepted -> { onChanged(); null }
            is ExecResult.Rejected -> result.reason
        }
}
