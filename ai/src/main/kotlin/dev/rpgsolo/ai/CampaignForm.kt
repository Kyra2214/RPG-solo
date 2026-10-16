package dev.rpgsolo.ai

/**
 * Fase 9.1.2: formulário auxiliar. Todos os campos são opcionais e a descrição livre continua valendo.
 * O formulário só vira texto para o [CampaignInterpreter]; quem decide sistema, edição e validade
 * continua sendo o catálogo e o motor.
 */
data class CampaignForm(
    val genre: String = "",
    val era: String = "",
    val tone: String = "",
    val difficulty: String = "",
    val themes: List<String> = emptyList(),
    val duration: String = "",
    val freeText: String = ""
) {
    fun isEmpty() = toDescription().isBlank()

    fun toDescription(): String {
        val lines = buildList {
            if (genre.isNotBlank()) add("Gênero: ${genre.trim()}")
            if (era.isNotBlank()) add("Época: ${era.trim()}")
            if (tone.isNotBlank()) add("Tom: ${tone.trim()}")
            if (difficulty.isNotBlank()) add("Dificuldade: ${difficulty.trim()}")
            val t = themes.map { it.trim() }.filter { it.isNotEmpty() }
            if (t.isNotEmpty()) add("Temas: ${t.joinToString(", ")}")
            if (duration.isNotBlank()) add("Duração: ${duration.trim()}")
        }
        val free = freeText.trim()
        return when {
            free.isEmpty() -> lines.joinToString("\n")
            lines.isEmpty() -> free
            else -> free + "\n" + lines.joinToString("\n")
        }
    }
}
