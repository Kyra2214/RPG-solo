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
    val attackDiceCount: Int = 1,
    val attackDiceSides: Int = 20,
    val attackComparison: Comparison = Comparison.ROLL_OVER,
    val damageBonus: Int = 0,
    val defenseBonus: Int = 0,
    /** Redução do dano recebido; valor negativo representa vulnerabilidade. */
    val damageReduction: Int = 0,
    /** Se > 0, o dano é rolado pelo motor usando o dado definido no pacote. */
    val damageDiceCount: Int = 0,
    val damageDiceSides: Int = 0,
    val damageStat: String? = null
) {
    init {
        require(attackerId.isNotBlank()) { "atacante vazio" }
        require(targetId.isNotBlank()) { "alvo vazio" }
        require(attackStat.isNotBlank()) { "atributo de ataque vazio" }
        require(defenseStat.isNotBlank()) { "atributo de defesa vazio" }
        require(damageResource.isNotBlank()) { "recurso de dano vazio" }
        require(damage >= 0) { "dano negativo" }
        require(damageReduction in -100..100) { "resistência a dano fora de -100..100" }
        require(defenseDefault >= 0) { "defesa padrão negativa" }
        require(attackDiceCount in 1..100 && attackDiceSides >= 2) { "dados de ataque inválidos" }
        require(attackComparison != Comparison.POINTS || (attackDiceCount == 1 && attackDiceSides in REAL_DICE)) {
            "ataque por pontos exige um dado real e um único dado"
        }
        require(damageDiceCount >= 0 && damageDiceSides >= 0) { "configuração de dados de dano negativa" }
        require((damageDiceCount == 0) == (damageDiceSides == 0)) { "dados de dano exigem quantidade e lados" }
        require(damageDiceCount <= 0 || damageDiceSides >= 2) { "dado de dano precisa ter pelo menos 2 lados" }
    }
}

sealed interface AttackResult {
    data class Resolved(
        val roll: RollResult,
        val hit: Boolean,
        val defense: Int,
        val damage: Int,
        val events: List<Event>,
        val damageRoll: RollResult? = null
    ) : AttackResult

    data class Rejected(val reason: String) : AttackResult
}
