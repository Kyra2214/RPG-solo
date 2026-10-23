package dev.rpgsolo.engine

/** Efeito tipado de um poder ativável. Os valores vêm do pacote de regras; o motor só valida e aplica. */
sealed interface PowerEffect {
    /** Cura [amount] no recurso [resource] do alvo. */
    data class Cure(val resource: String, val amount: Int) : PowerEffect

    /** Dano rolado pelo motor: [diceCount]d[diceSides] + [bonus] - [reduction], aplicado em [resource] do alvo. */
    data class Strike(
        val resource: String,
        val diceCount: Int,
        val diceSides: Int,
        val bonus: Int = 0,
        val reduction: Int = 0,
    ) : PowerEffect

    data class Inflict(
        val condition: String,
        val durationRounds: Int? = null,
        val damagePerRound: Int = 0,
        val damageResource: String = "hp",
    ) : PowerEffect

    data class Cleanse(val condition: String) : PowerEffect

    /** MU-02.7: soma [amount] ao escudo do usuário. */
    data class Shield(val amount: Int) : PowerEffect
}

/**
 * Ativação de um poder: gasta [cost] de [resource] do usuário e, se houver [check], só aplica o efeito com sucesso
 * (o custo é gasto de qualquer forma). [targetId] = null significa o próprio usuário.
 */
data class PowerSpec(
    val powerId: String,
    val userId: String,
    val targetId: String? = null,
    val resource: String,
    val cost: Int,
    val effect: PowerEffect,
    val check: CheckSpec? = null,
) {
    init {
        require(powerId.isNotBlank()) { "poder sem id" }
        require(cost >= 0) { "custo negativo" }
        if (effect is PowerEffect.Strike) {
            require(effect.diceCount in 1..100 && effect.diceSides >= 2) { "dado de dano inválido" }
        }
    }
}

sealed interface PowerResult {
    data class Activated(
        val spent: Int,
        val check: CheckResult?,
        /** false se o teste falhou: custo gasto, sem efeito. */
        val effectApplied: Boolean,
        val damageRoll: RollResult?,
        /** Variação real do recurso do alvo (cura positiva, dano negativo); 0 sem efeito de recurso. */
        val resourceDelta: Int,
        val events: List<Event>,
    ) : PowerResult

    data class Rejected(val reason: String) : PowerResult
}
