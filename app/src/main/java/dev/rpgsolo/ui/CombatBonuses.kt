package dev.rpgsolo.ui

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.rules.RulePackage

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
            ?.getResourceAsStream("packages/sistema-proprio/habilidades-arvore.txt")
            ?: return@lazy emptyList()
        val text = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val loaded = dev.rpgsolo.rules.SkillTreeCatalogLoader.load(text)
        require(loaded.valid) { loaded.errors.joinToString("; ") }
        loaded.skills
    }

    private val curseDefinitions: List<dev.rpgsolo.rules.CurseDefinition> by lazy {
        val stream = CombatBonuses::class.java.classLoader
            ?.getResourceAsStream("packages/sistema-proprio/maldicoes-lancadas.txt")
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
        return dev.rpgsolo.rules.distinctExternalBonuses(passive + active + curseEffects)
    }

    /** Bônus passivos da árvore + buffs temporários já validados pelo motor. */
    fun skillTreeAndActiveBonus(entityId: String, scope: String): Int =
        externalSkillAndCurseBonuses(entityId, setOf(scope)).sumOf { it.bonus }

    /** Bônus equivalentes do catálogo de raça/classe/profissão não acumulam: prevalece o maior efeito por escopo. */
    fun selectedCatalogBonuses(pkg: RulePackage): List<dev.rpgsolo.rules.ExternalBonus> =
        catalogBonuses(pkg).groupBy { it.scope }.mapNotNull { (_, effects) ->
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
