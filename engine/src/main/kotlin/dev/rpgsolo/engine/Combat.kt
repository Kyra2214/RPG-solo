package dev.rpgsolo.engine

/** Parâmetros mecânicos fornecidos pelo pacote de regras, nunca pela narração da IA. */
data class AttackSpec(
    val attackerId: String,
    val targetId: String,
    val attackStat: String = "attack",
    val defenseStat: String = "defense",
    val damageResource: String = "hp",
    val damage: Int = 1,
    val defenseDefault: Int = 10,
    val attackBonus: Int = 0,
    val damageBonus: Int = 0,
    val defenseBonus: Int = 0
) {
    init {
        require(attackerId.isNotBlank()) { "atacante vazio" }
        require(targetId.isNotBlank()) { "alvo vazio" }
        require(attackStat.isNotBlank()) { "atributo de ataque vazio" }
        require(defenseStat.isNotBlank()) { "atributo de defesa vazio" }
        require(damageResource.isNotBlank()) { "recurso de dano vazio" }
        require(damage >= 0) { "dano negativo" }
        require(defenseDefault >= 0) { "defesa padrão negativa" }
    }
}

sealed interface AttackResult {
    data class Resolved(
        val roll: RollResult,
        val hit: Boolean,
        val defense: Int,
        val damage: Int,
        val events: List<Event>
    ) : AttackResult

    data class Rejected(val reason: String) : AttackResult
}
