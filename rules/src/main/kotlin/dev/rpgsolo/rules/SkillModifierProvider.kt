package dev.rpgsolo.rules

import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.Modifier
import dev.rpgsolo.engine.ModifierProvider

/**
 * MU-06.1: fonte de modificadores das perícias com bônus condicional (ex.: espada de duas mãos equipada).
 * Vale para qualquer entidade que tenha a perícia (pontos > 0) e o item equipado em algum slot;
 * jogador, NPC, aliado e chefe passam pelo mesmo cálculo.
 */
class SkillModifierProvider(private val traits: Collection<TraitDef>) : ModifierProvider {
    private val skills = traits.filter { it.type == "pericia" && it.conditionalBonuses.isNotEmpty() }

    constructor(pkg: RulePackage) : this(pkg.data.traits)

    override fun modifiers(state: GameState, entity: Entity, target: String): List<Modifier> {
        val equipped = state.equipped[entity.id]?.values?.toSet().orEmpty()
        val owned = skills.filter { (entity.skills[it.id] ?: 0) > 0 }
        val conditional = owned.flatMap { skill ->
            skill.conditionalBonuses
                .filter { it.scope == target && it.itemId in equipped }
                .map { Modifier("pericia:${skill.id}:${it.itemId}", "${skill.name} (${it.itemId} equipado)", target, it.bonus) }
        }
        val level2 = traits.filter { it.type == "pericia" && it.id in entity.masteredSkills && (entity.skills[it.id] ?: 0) > 0 }
            .flatMap { skill ->
                skill.level2Bonuses.filter { it.scope == target }
                    .map { Modifier("pericia:${skill.id}:nivel2", "${skill.name} (nível 2)", target, it.bonus) }
            }
        return conditional + level2
    }
}
