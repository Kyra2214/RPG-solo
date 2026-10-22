package dev.rpgsolo.engine

/**
 * MU-03.3: exceção mecânica explícita (chefe, inimigo especial, fase). Vem do pacote ou da regra da campanha,
 * nunca da narração da IA. Precisa de ID, entidade existente e descrição documentada; seus bônus entram no
 * mesmo agregador, com origem `excecao:<id>`, e por isso aparecem no detalhamento como qualquer outro.
 */
data class MechanicalException(
    val id: String,
    val entityId: String,
    val description: String,
    val modifiers: List<Modifier> = emptyList(),
    val immuneConditions: Set<String> = emptySet(),
)

class ExceptionRegistry(val exceptions: List<MechanicalException>) : ModifierProvider {
    /** Erros de validação; lista vazia = registro válido. Exceção sem descrição ou com ID repetido é inválida. */
    fun validate(state: GameState): List<String> {
        val errors = mutableListOf<String>()
        val seen = mutableSetOf<String>()
        for (e in exceptions) {
            if (e.id.isBlank()) {
                errors += "exceção sem id"
            } else if (!seen.add(e.id)) {
                errors += "exceção repetida: ${e.id}"
            }
            if (e.description.isBlank()) errors += "exceção '${e.id}' sem descrição documentada"
            if (e.entityId !in state.entities) errors += "exceção '${e.id}' aponta para entidade inexistente: ${e.entityId}"
            if (e.modifiers.isEmpty() && e.immuneConditions.isEmpty()) errors += "exceção '${e.id}' não declara efeito"
        }
        return errors
    }

    /** Só exceções declaradas contribuem; o ID da origem é forçado para `excecao:<id>` (estável e auditável). */
    override fun modifiers(
        state: GameState,
        entity: Entity,
        target: String,
    ): List<Modifier> =
        exceptions.filter { it.entityId == entity.id }.flatMap { ex ->
            ex.modifiers.filter { it.target == target }.mapIndexed { i, m ->
                m.copy(sourceId = "excecao:${ex.id}:$i", label = "${ex.description}: ${m.label}")
            }
        }

    fun isImmune(
        entityId: String,
        condition: String,
    ): Boolean = exceptions.any { it.entityId == entityId && condition in it.immuneConditions }
}
