package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** MU-06.3: momento em que uma habilidade pode ser ativada. */
enum class AbilityTrigger { MANUAL, ON_ATTACK, ON_DEFEND, ON_CRIT, ON_COMBAT_START }

data class AbilityBonus(val target: String, val value: Int)

/**
 * Habilidade comprada com pontos de habilidade. [bonuses] valem por rodadas: [fixedRounds] quando declarado;
 * sem valor fixo, o motor rola 1d6 de rodadas (sem animação) e grava o sorteio no efeito.
 */
data class AbilitySpec(
    val id: String,
    val name: String,
    val cost: Int,
    val trigger: AbilityTrigger,
    val bonuses: List<AbilityBonus>,
    val fixedRounds: Int? = null,
    /** MU-06.4: desvantagem opcional, só quando o autor declara; valores negativos, mesma duração dos bônus. */
    val drawbacks: List<AbilityBonus> = emptyList(),
)

/** Efeito temporário ativo; [sourceId] é `habilidade:<id>` (a mesma habilidade não repete na mesma característica). */
@Serializable
data class ActiveEffect(
    val sourceId: String,
    val label: String,
    val target: String,
    val value: Int,
    val remainingRounds: Int,
)

sealed interface AbilityResult {
    data class Activated(val rounds: Int, val effects: List<ActiveEffect>, val rolled: Boolean) : AbilityResult

    data class Rejected(val reason: String) : AbilityResult
}

/** Fonte de modificadores dos efeitos temporários ativos; mesma regra para jogador, NPC, aliado e chefe. */
object ActiveEffectsProvider : ModifierProvider {
    override fun modifiers(
        state: GameState,
        entity: Entity,
        target: String,
    ): List<Modifier> =
        state.activeEffects[entity.id].orEmpty().filter { it.target == target }
            .map { Modifier(it.sourceId, it.label, it.target, it.value, remainingRounds = it.remainingRounds) }
}

/**
 * Ativa uma habilidade aprendida no gatilho certo. Rola 1d6 de rodadas se não houver duração fixa; a rolagem
 * fica no log. Atômico: recusa desfaz tudo.
 */
fun GameEngine.activateAbility(
    entityId: String,
    spec: AbilitySpec,
    trigger: AbilityTrigger,
): AbilityResult {
    val start = mark()
    return try {
        val rounds: Int
        val rolled = spec.fixedRounds == null
        if (spec.fixedRounds != null) {
            rounds = spec.fixedRounds
        } else {
            rounds = rollDuration("duração: ${spec.name}").rounds
        }
        if (spec.drawbacks.any { it.value >= 0 }) {
            rollbackTo(start)
            return AbilityResult.Rejected("desvantagem de habilidade deve ter valor negativo")
        }
        val source = "habilidade:${spec.id}"
        val effects =
            spec.bonuses.map { ActiveEffect(source, spec.name, it.target, it.value, rounds) } +
                spec.drawbacks.map { ActiveEffect("$source:desvantagem", "${spec.name} (desvantagem)", it.target, it.value, rounds) }
        when (val r = execute(ActivateAbility(entityId, spec.id, spec.trigger, trigger, effects))) {
            is ExecResult.Rejected -> {
                rollbackTo(start)
                AbilityResult.Rejected(r.reason)
            }
            else -> AbilityResult.Activated(rounds, effects, rolled)
        }
    } catch (e: Exception) {
        rollbackTo(start)
        throw e
    }
}

/** Versão persistida: a rolagem e o efeito são gravados juntos. */
fun CampaignSession.activateAbility(
    entityId: String,
    spec: AbilitySpec,
    trigger: AbilityTrigger,
): AbilityResult =
    atomically {
        val start = mark()
        try {
            engine.activateAbility(entityId, spec, trigger).also { save() }
        } catch (e: Exception) {
            engine.rollbackTo(start)
            throw e
        }
    }
