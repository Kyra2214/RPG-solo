package dev.rpgsolo.ui

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.rules.RulePackage

/**
 * Preserva camadas distintas. As passivas já chegam deduplicadas pelo grupo tipado da habilidade,
 * e o catálogo aplica sua própria regra de equivalência. Não colapsar tudo por escopo/sinal:
 * isso apagaria efeitos compatíveis de origens diferentes só porque afetam o mesmo teste.
 */
internal fun mergePassiveAndCatalogBonuses(
    passive: Collection<dev.rpgsolo.rules.ExternalBonus>,
    catalog: Collection<dev.rpgsolo.rules.ExternalBonus>
): List<dev.rpgsolo.rules.ExternalBonus> =
    dev.rpgsolo.rules.distinctExternalBonuses(passive + catalog)

/**
 * Bônus e ajustes que o combate e os testes somam (R-03): regras adicionais aprovadas, condições, traços e efeitos do catálogo.
 * Extraído do `AppModel` sem mudar cálculo nem texto. [catalogBonuses] devolve os efeitos do catálogo da ficha ativa.
 */
class CombatBonuses(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val activeFicha: () -> Ficha?,
    private val playerId: () -> String,
    private val catalogBonuses: (RulePackage) -> List<dev.rpgsolo.rules.ExternalBonus>
) {
    private val skillTreeDefinitions: List<dev.rpgsolo.engine.SkillTreeSkill> by lazy {
        val stream = CombatBonuses::class.java.classLoader
            ?.getResourceAsStream("catalogs/sistema-proprio/habilidades-arvore.txt")
            ?: return@lazy emptyList()
        val text = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val loaded = dev.rpgsolo.rules.SkillTreeCatalogLoader.load(text)
        require(loaded.valid) { loaded.errors.joinToString("; ") }
        loaded.skills
    }

    private val curseDefinitions: List<dev.rpgsolo.rules.CurseDefinition> by lazy {
        val stream = CombatBonuses::class.java.classLoader
            ?.getResourceAsStream("catalogs/sistema-proprio/maldicoes-lancadas.txt")
            ?: return@lazy emptyList()
        val raw = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val loaded = dev.rpgsolo.rules.CurseCatalogLoader.load(raw)
        require(loaded.valid) { loaded.errors.joinToString("; ") }
        loaded.curses
    }

    /** Efeitos permanentes/temporários de maldições realmente aplicadas à entidade. */
    fun appliedCurseBonus(entityId: String, scope: String): Int {
        val entity = session().state.entities[entityId] ?: return 0
        val definitions = curseDefinitions.associateBy { it.id }
        val effects = entity.appliedCurses.values.flatMap { applied ->
            val curse = definitions[applied.curseId] ?: return@flatMap emptyList()
            curse.effects.filter { it.target == scope }.map { effect -> effect to effect.baseValue }
        }
        return effects.groupBy { it.first.stackingGroup }
            .values
            .mapNotNull { group -> group.maxWithOrNull(compareBy<Pair<dev.rpgsolo.engine.SkillEffectSpec, Int>>({ kotlin.math.abs(it.second) }, { it.second })) }
            .sumOf { it.second }
    }

    /** Melhor golpe ativo desbloqueado por um NPC; usa os mesmos requisitos e níveis da árvore do jogador. */
    fun bestAvailableAttackSkill(entityId: String): Pair<dev.rpgsolo.engine.SkillTreeSkill, Int>? {
        val entity = session().state.entities[entityId] ?: return null
        val element = entity.skillTreeElement.takeIf { it.isNotBlank() }?.let {
            runCatching { dev.rpgsolo.engine.ElementPath.valueOf(it) }.getOrDefault(dev.rpgsolo.engine.ElementPath.NONE)
        } ?: dev.rpgsolo.engine.ElementPath.NONE
        return entity.skillTreeLevels.mapNotNull { (skillId, level) ->
            val raw = skillTreeDefinitions.firstOrNull { it.id == skillId } ?: return@mapNotNull null
            if (level <= 0 || raw.category != dev.rpgsolo.engine.SkillCategory.OFFENSE ||
                raw.actionKind !in setOf(dev.rpgsolo.engine.SkillActionKind.POWER_STRIKE, dev.rpgsolo.engine.SkillActionKind.MULTI_HIT)
            ) return@mapNotNull null
            if (entity.skillTreeTreeIds.isNotEmpty() && raw.treeId !in entity.skillTreeTreeIds) return@mapNotNull null
            val slotLevel = when (entity.skillTreeClassOrder.indexOf(raw.treeId)) {
                0 -> 1
                1 -> 10
                2 -> 20
                3 -> 30
                else -> 1
            }
            val skill = raw.copy(
                requiredCharacterLevel = maxOf(raw.requiredCharacterLevel, if (raw.origin == dev.rpgsolo.engine.SkillOrigin.CLASS) slotLevel else 1),
                element = if (raw.affinityBound && element != dev.rpgsolo.engine.ElementPath.NONE) element else raw.element
            )
            val context = dev.rpgsolo.engine.SkillTreeCharacterContext(entity.level, element, emptySet(), entity.skillTreeLevels)
            val valid = dev.rpgsolo.engine.SkillTreeProgression.validate(skill, context)
            if (!valid.allowed) return@mapNotNull null
            skill to level
        }.maxWithOrNull(compareBy<Pair<dev.rpgsolo.engine.SkillTreeSkill, Int>>(
            { it.first.scrollRank ?: 0 },
            { it.first.effects.maxOfOrNull { effect -> kotlin.math.abs(effect.valueAt(it.second)) } ?: 0 },
            { it.second }
        ))
    }

    /** Efeitos de passivas, buffs ativos e maldições como bônus externos para o mesmo pipeline de testes do pacote. */
    fun externalSkillAndCurseBonuses(entityId: String, scopes: Set<String>): List<dev.rpgsolo.rules.ExternalBonus> {
        if (scopes.isEmpty()) return emptyList()
        val state = session().state
        val entity = state.entities[entityId] ?: return emptyList()
        val passive = scopes.flatMap { scope ->
            dev.rpgsolo.engine.SkillTreeModifierProvider(skillTreeDefinitions).modifiers(state, entity, scope)
        }.map { modifier ->
            dev.rpgsolo.rules.ExternalBonus(modifier.target, modifier.value, modifier.label, modifier.sourceId)
        }
        val active = state.activeEffects[entityId].orEmpty().filter { it.target in scopes }.mapIndexed { index, effect ->
            dev.rpgsolo.rules.ExternalBonus(
                effect.target, effect.value, effect.label,
                "${effect.sourceId}:${effect.target}:$index"
            )
        }
        val definitions = curseDefinitions.associateBy { it.id }
        val curseEffects = entity.appliedCurses.values.flatMap { applied ->
            val curse = definitions[applied.curseId] ?: return@flatMap emptyList()
            curse.effects.filter { it.target in scopes }.map { curse to it }
        }.groupBy { (curse, effect) -> "${effect.target}:${effect.stackingGroup}" }
            .mapNotNull { (groupId, group) ->
                val (curse, effect) = group.maxWithOrNull(
                    compareBy<Pair<dev.rpgsolo.rules.CurseDefinition, dev.rpgsolo.engine.SkillEffectSpec>>(
                        { kotlin.math.abs(it.second.baseValue) }, { it.second.baseValue }
                    )
                ) ?: return@mapNotNull null
                dev.rpgsolo.rules.ExternalBonus(
                    effect.target, effect.baseValue, "Maldição: ${curse.name}", "curse-group:$groupId"
                )
            }
        val catalog = if (entityId == playerId()) {
            activePackage()?.let { selectedCatalogBonuses(it) }.orEmpty().filter { it.scope in scopes }
        } else emptyList()
        val unlockedClassCount = dev.rpgsolo.engine.HybridClassRules.unlockedClassCount(
            entity.skillTreeClassOrder, entity.level
        )
        val hybridPenalty = dev.rpgsolo.engine.HybridClassRules.socialPenalty(unlockedClassCount)
        val hybridAppearance = if (hybridPenalty == 0) emptyList() else scopes
            .filter { it in dev.rpgsolo.engine.HybridClassRules.socialPenaltyScopes }
            .map { scope ->
                dev.rpgsolo.rules.ExternalBonus(
                    scope,
                    hybridPenalty,
                    "aparência híbrida ($unlockedClassCount classes desbloqueadas)",
                    "hybrid-appearance:$entityId:$scope"
                )
            }
        val passiveAndCatalog = mergePassiveAndCatalogBonuses(passive, catalog)
        // Efeitos declarados em itens só contam quando o item está realmente equipado.
        // A mesma regra vale para jogador e NPC; a descrição narrativa do item nunca gera bônus.
        val equipment = activePackage()?.let { pkg ->
            state.equipped[entityId].orEmpty().values.distinct().flatMap { itemId ->
                val item = pkg.item(itemId) ?: return@flatMap emptyList()
                item.effects.filter { it.scope in scopes }.map { effect ->
                    dev.rpgsolo.rules.ExternalBonus(
                        effect.scope,
                        effect.bonus,
                        "Equipamento: ${item.name}",
                        "equipment:$entityId:$itemId:${effect.scope}"
                    )
                }
            }
        }.orEmpty()
        return dev.rpgsolo.rules.distinctExternalBonuses(passiveAndCatalog + active + curseEffects + hybridAppearance + equipment)
    }

    /** Bônus passivos da árvore + buffs temporários já validados pelo motor. */
    fun skillTreeAndActiveBonus(entityId: String, scope: String): Int =
        externalSkillAndCurseBonuses(entityId, setOf(scope)).sumOf { it.bonus }

    /** Bônus equivalentes do catálogo de raça/classe/profissão não acumulam: prevalece o maior efeito por escopo. */
    fun selectedCatalogBonuses(pkg: RulePackage): List<dev.rpgsolo.rules.ExternalBonus> =
        catalogBonuses(pkg).groupBy { it.scope to (it.bonus > 0) }.mapNotNull { (_, effects) ->
            effects.maxWithOrNull(compareBy<dev.rpgsolo.rules.ExternalBonus>({ kotlin.math.abs(it.bonus) }, { it.bonus }))
        }

    fun approvedRuleBonus(scope: String): Int =
        session().state.additionalRules.values.filter { it.approved }.sumOf { it.effects[scope] ?: 0 }

    fun approvedRuleEffects(scopes: Collection<String>): List<dev.rpgsolo.rules.ExternalBonus> =
        session().state.additionalRules.values.filter { it.approved }.flatMap { rule ->
            scopes.distinct().mapNotNull { scope ->
                rule.effects[scope]?.takeIf { it != 0 }?.let { bonus ->
                    dev.rpgsolo.rules.ExternalBonus(scope, bonus, "adaptação ${rule.id}")
                }
            }
        }

    /** Explica quais adaptações aprovadas contribuíram para os escopos usados neste cálculo. */
    fun approvedRuleBonusSummary(scopes: Collection<String>): String {
        val details = session().state.additionalRules.values
            .filter { it.approved }
            .flatMap { rule ->
                scopes.distinct().mapNotNull { scope ->
                    rule.effects[scope]?.takeIf { it != 0 }?.let { bonus ->
                        "${rule.id} ($scope ${if (bonus > 0) "+" else ""}$bonus)"
                    }
                }
            }
        return if (details.isEmpty()) "" else " · adaptações: ${details.joinToString(", ")}"
    }

    /** Ganho/Perda das condições para um teste que usa o atributo informado. */
    fun conditionDiceShift(entityId: String, stat: String): Int =
        session().state.conditions[entityId].orEmpty().sumOf { conditionId ->
            val effect = activePackage()?.data?.conditions?.firstOrNull { it.id == conditionId }?.effect
            val effectStats = effect?.stats
            if (effect != null && (effectStats == null || stat in effectStats)) effect.dice else 0
        }

    /** Traços mecânicos do combatente; a ficha salva cobre campanhas antigas cujo Entity ainda não os continha. */
    fun combatTraitIds(entityId: String): List<String> =
        (session().state.entities[entityId]?.traits.orEmpty() +
            if (entityId == playerId()) activeFicha()?.traits.orEmpty() else emptyList()).distinct()

    fun selectedCatalogBonus(pkg: RulePackage, scope: String): Int =
        selectedCatalogBonuses(pkg).filter { it.scope == scope }.sumOf { it.bonus }

    /** Explica os efeitos por escopo, sem somar bônus que modificam cálculos diferentes. */
    fun selectedCatalogBonusSummary(pkg: RulePackage, scopes: Collection<String>): String {
        val applied = selectedCatalogBonuses(pkg).filter { it.scope in scopes && it.bonus != 0 }
        if (applied.isEmpty()) return ""
        val details = applied.groupBy { it.scope }.entries.joinToString("; ") { (scope, effects) ->
            val total = effects.sumOf { it.bonus }
            val sources = effects.map { it.source }.distinct().joinToString(", ")
            scope + " " + (if (total > 0) "+" else "") + total + " [" + sources + "]"
        }
        return " Efeitos de personagem: $details."
    }
}
