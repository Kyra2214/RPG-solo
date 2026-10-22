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
    fun selectedCatalogBonuses(pkg: RulePackage): List<dev.rpgsolo.rules.ExternalBonus> = catalogBonuses(pkg)

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
        catalogBonuses(pkg).filter { it.scope == scope }.sumOf { it.bonus }

    /** Explica os efeitos por escopo, sem somar bônus que modificam cálculos diferentes. */
    fun selectedCatalogBonusSummary(pkg: RulePackage, scopes: Collection<String>): String {
        val applied = catalogBonuses(pkg).filter { it.scope in scopes && it.bonus != 0 }
        if (applied.isEmpty()) return ""
        val details = applied.groupBy { it.scope }.entries.joinToString("; ") { (scope, effects) ->
            val total = effects.sumOf { it.bonus }
            val sources = effects.map { it.source }.distinct().joinToString(", ")
            scope + " " + (if (total > 0) "+" else "") + total + " [" + sources + "]"
        }
        return " Efeitos de personagem: $details."
    }
}
