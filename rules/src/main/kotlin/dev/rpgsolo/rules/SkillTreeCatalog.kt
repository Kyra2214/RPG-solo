package dev.rpgsolo.rules

import dev.rpgsolo.engine.ElementPath
import dev.rpgsolo.engine.SkillCategory
import dev.rpgsolo.engine.SkillEffectSpec
import dev.rpgsolo.engine.SkillOrigin
import dev.rpgsolo.engine.SkillTreeSkill

data class SkillTreeCatalogResult(
    val skills: List<SkillTreeSkill>,
    val errors: List<String>
) {
    val valid: Boolean get() = errors.isEmpty()
    fun skill(id: String): SkillTreeSkill? = skills.firstOrNull { it.id == id }
    fun tree(id: String): List<SkillTreeSkill> = skills.filter { it.treeId == id }.sortedWith(
        compareBy<SkillTreeSkill>({ it.scrollRank ?: 99 }, { it.name })
    )
}

/**
 * Parser for the original skill-tree catalog. Format is pipe-delimited and deliberately separate
 * from the rules package's attack/damage files so the existing combat formula is not redefined.
 *
 * Columns:
 * id|treeId|name|origin|category|element|requiredElement|affinityBound|weaponTags|level|prerequisites|scrollRank|unlockCost|freeBase|effects
 *
 * Effects are semicolon-delimited tuples:
 * id,target,baseValue,perLevel,stackingGroup,durationRounds,conditionId
 * Optional values use '-'. Text/lore never becomes an effect automatically.
 */
object SkillTreeCatalogLoader {
    private val ID = Regex("[a-z0-9][a-z0-9_-]*")

    fun load(text: String): SkillTreeCatalogResult {
        val errors = mutableListOf<String>()
        val skills = mutableListOf<SkillTreeSkill>()
        text.lines().forEachIndexed { index, raw ->
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) return@forEachIndexed
            val fields = line.split("|").map(String::trim)
            val lineNo = index + 1
            if (fields.size != 15) {
                errors += "habilidades-arvore linha $lineNo: esperado 15 campos, recebido \${fields.size}"
                return@forEachIndexed
            }
            try {
                fun id(value: String, label: String, optional: Boolean = false): String {
                    if (optional && (value.isEmpty() || value == "-")) return ""
                    require(ID.matches(value)) { "$label inválido: '$value'" }
                    return value
                }
                fun opt(value: String) = value.takeUnless { it.isEmpty() || it == "-" }
                val skillId = id(fields[0], "id da habilidade")
                val treeId = id(fields[1], "id da árvore")
                val origin = SkillOrigin.valueOf(fields[3].uppercase())
                val category = SkillCategory.valueOf(fields[4].uppercase())
                val element = if (fields[5] == "-" || fields[5].isEmpty()) ElementPath.NONE else ElementPath.valueOf(fields[5].uppercase())
                val requiredElement = opt(fields[6])?.let { ElementPath.valueOf(it.uppercase()) }
                val affinityBound = fields[7].toBooleanStrict()
                val weaponTags = opt(fields[8])?.split(",")?.map { id(it, "tag de arma") }?.toSet().orEmpty()
                val level = fields[9].toInt().also { require(it >= 1) { "nível mínimo inválido" } }
                val prerequisites = opt(fields[10])?.split(",")?.map { id(it, "pré-requisito") }?.toSet().orEmpty()
                val rank = opt(fields[11])?.toInt()?.also { require(it in 1..7) { "posição de pergaminho fora de 1..7" } }
                val unlockCost = fields[12].toInt().also { require(it >= 0) { "custo de desbloqueio negativo" } }
                val freeBase = fields[13].toBooleanStrict()
                val effects = opt(fields[14])?.split(";")?.map { rawEffect ->
                    val parts = rawEffect.split(",").map(String::trim)
                    require(parts.size == 7) { "efeito '$rawEffect' precisa de 7 partes" }
                    SkillEffectSpec(
                        id(parts[0], "id do efeito"),
                        id(parts[1], "alvo do efeito"),
                        parts[2].toInt(),
                        parts[3].toInt(),
                        id(parts[4], "grupo de acúmulo"),
                        opt(parts[5])?.toInt(),
                        opt(parts[6])
                    )
                }.orEmpty()
                skills += SkillTreeSkill(
                    id = skillId,
                    name = fields[2].also { require(it.isNotBlank()) { "nome vazio" } },
                    treeId = treeId,
                    origin = origin,
                    category = category,
                    element = element,
                    requiredElement = requiredElement,
                    affinityBound = affinityBound,
                    weaponTags = weaponTags,
                    requiredCharacterLevel = level,
                    prerequisiteSkillIds = prerequisites,
                    scrollRank = rank,
                    unlockPointCost = unlockCost,
                    effects = effects,
                    freeBaseSkill = freeBase
                )
            } catch (e: Exception) {
                errors += "habilidades-arvore linha $lineNo: \${e.message ?: "formato inválido"}"
            }
        }

        skills.groupBy { it.id }.filterValues { it.size > 1 }.keys.forEach { errors += "id de habilidade duplicado: $it" }
        skills.groupBy { it.treeId }.forEach { (treeId, entries) ->
            val ranks = entries.mapNotNull { it.scrollRank }
            if (ranks.size != ranks.distinct().size) errors += "árvore $treeId tem posições de pergaminho duplicadas"
            if (ranks.isNotEmpty() && ranks.sorted() != ranks) errors += "árvore $treeId deve declarar posições de pergaminho em ordem"
            entries.filter { it.freeBaseSkill && it.origin !in setOf(SkillOrigin.RACE, SkillOrigin.SUBRACE, SkillOrigin.CURSED_CLASS) }
                .forEach { errors += "habilidade básica gratuita \${it.id} tem origem inválida" }
            entries.filter { it.prerequisiteSkillIds.any { req -> entries.none { candidate -> candidate.id == req } } }
                .forEach { errors += "pré-requisito ausente na árvore $treeId: \${it.id}" }
        }
        return SkillTreeCatalogResult(skills, errors.distinct())
    }
}
