package dev.rpgsolo.rules

import dev.rpgsolo.engine.SkillEffectRecipient
import dev.rpgsolo.engine.SkillEffectSpec

data class CurseDefinition(
    val id: String,
    val name: String,
    val description: String,
    val sourceType: String,
    val resistanceCheckId: String?,
    val resistanceDifficulty: Int?,
    val durationRounds: Int?,
    val removalMethods: List<String>,
    val effects: List<SkillEffectSpec>
)

data class CurseCatalogResult(val curses: List<CurseDefinition>, val errors: List<String>) {
    val valid: Boolean get() = errors.isEmpty()
    fun find(id: String): CurseDefinition? = curses.firstOrNull { it.id == id }
}

object CurseCatalogLoader {
    private val idPattern = Regex("[a-z0-9][a-z0-9_-]*")

    /**
     * id|name|description|sourceType|resistanceCheckId|resistanceDifficulty|durationRounds|removalMethods|effects
     * effects: effectId,target,baseValue,perLevel,stackingGroup,durationRounds,conditionId;...
     */
    fun load(text: String): CurseCatalogResult {
        val errors = mutableListOf<String>()
        val curses = mutableListOf<CurseDefinition>()
        text.lines().forEachIndexed { index, raw ->
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) return@forEachIndexed
            val fields = line.split("|").map(String::trim)
            val lineNo = index + 1
            if (fields.size != 9) {
                errors += "maldições linha $lineNo: esperado 9 campos, recebido \${fields.size}"
                return@forEachIndexed
            }
            try {
                require(idPattern.matches(fields[0])) { "id inválido" }
                require(fields[1].isNotBlank() && fields[2].isNotBlank() && fields[3].isNotBlank()) { "nome, descrição e origem são obrigatórios" }
                val resistanceId = fields[4].takeUnless { it.isBlank() || it == "-" }
                val difficulty = fields[5].takeUnless { it.isBlank() || it == "-" }?.toInt()
                val duration = fields[6].takeUnless { it.isBlank() || it == "-" }?.toInt()
                require((resistanceId == null) == (difficulty == null)) { "teste de resistência e dificuldade devem ser declarados juntos" }
                require(difficulty == null || difficulty > 0) { "dificuldade de resistência inválida" }
                require(duration == null || duration > 0) { "duração inválida" }
                val removal = fields[7].split(",").map(String::trim).filter { it.isNotBlank() && it != "-" }
                val effects = fields[8].takeUnless { it.isBlank() || it == "-" }?.split(";")?.map { rawEffect ->
                    val parts = rawEffect.split(",").map(String::trim)
                    require(parts.size == 7) { "efeito '$rawEffect' precisa de 7 partes" }
                    require(idPattern.matches(parts[0]) && idPattern.matches(parts[1]) && idPattern.matches(parts[4])) { "id de efeito, alvo ou grupo inválido" }
                    val effectDuration = parts[5].takeUnless { it.isBlank() || it == "-" }?.toInt()
                    SkillEffectSpec(
                        id = parts[0],
                        target = parts[1],
                        baseValue = parts[2].toInt(),
                        perLevel = parts[3].toInt(),
                        stackingGroup = parts[4],
                        durationRounds = effectDuration,
                        conditionId = parts[6].takeUnless { it.isBlank() || it == "-" },
                        recipient = SkillEffectRecipient.SELF
                    )
                }.orEmpty()
                require(effects.size >= 2) { "maldição precisa declarar benefício e contrapartida em efeitos tipados" }
                require(effects.any { it.baseValue > 0 } && effects.any { it.baseValue < 0 }) { "maldição precisa de pelo menos um benefício e uma desvantagem mecânica" }
                curses += CurseDefinition(fields[0], fields[1], fields[2], fields[3], resistanceId, difficulty, duration, removal, effects)
            } catch (e: Exception) {
                errors += "maldições linha $lineNo: \${e.message ?: "formato inválido"}"
            }
        }
        curses.groupBy { it.id }.filterValues { it.size > 1 }.keys.forEach { errors += "id de maldição duplicado: $it" }
        return CurseCatalogResult(curses, errors.distinct())
    }
}
