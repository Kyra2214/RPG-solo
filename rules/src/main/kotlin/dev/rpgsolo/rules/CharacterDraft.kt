package dev.rpgsolo.rules


/** Dica de regra para a criação: vem do pacote do sistema instalado. */
data class RuleHint(val source: Source, val title: String, val text: String) {
    enum class Source { PACOTE }
}

/**
 * Rascunho de ficha guiado pelo pacote do sistema escolhido.
 * O pacote valida tudo (pontos, requisitos, exclusões, limites); o rascunho só organiza as escolhas.
 */
class CharacterDraft(val pkg: RulePackage, val id: String) {
    var name: String = ""
    var concept: String = ""
    private val stats = linkedMapOf<String, Int>().also { m -> pkg.data.stats.forEach { m[it.id] = it.default } }
    private val skills = linkedMapOf<String, Int>()
    private val traits = linkedSetOf<String>()

    fun stats(): Map<String, Int> = stats.toMap()
    fun skills(): Map<String, Int> = skills.toMap()
    fun traits(): List<String> = traits.toList()
    fun stat(id: String): Int = stats[id] ?: 0

    fun setStat(statId: String, value: Int): Boolean {
        if (pkg.stat(statId) == null) return false
        stats[statId] = value
        return true
    }

    /** Define os pontos da perícia; o pacote continua sendo a autoridade final sobre limites e custo. */
    fun setSkill(skillId: String, value: Int): Boolean {
        if (pkg.trait(skillId)?.type != "pericia" || value !in 0..MAX_SKILL_POINTS) return false
        skills[skillId] = value
        return true
    }

    fun addTrait(traitId: String): Boolean = pkg.trait(traitId) != null && traits.add(traitId)
    fun removeTrait(traitId: String): Boolean = traits.remove(traitId)

    /** Carrega uma ficha de exemplo do modelo (do pacote) como ponto de partida. */
    fun loadExample(exampleId: String): Boolean {
        val ex = pkg.data.examples.firstOrNull { it.id == exampleId } ?: return false
        pkg.data.stats.forEach { stats[it.id] = ex.stats[it.id] ?: it.default }
        traits.clear(); traits.addAll(ex.traits)
        if (concept.isBlank()) concept = ex.title
        return true
    }

    /** Carrega um arquétipo do pacote como ponto de partida; o jogador pode editar tudo depois. */
    fun loadArchetype(archetypeId: String): Boolean {
        val a = pkg.archetype(archetypeId) ?: return false
        pkg.data.stats.forEach { stats[it.id] = a.stats[it.id] ?: it.default }
        traits.clear(); traits.addAll(a.traits)
        return true
    }

    fun report(): CharacterReport = pkg.validateCharacter(stats, traits.toList(), skills = skills.toMap())
    fun pointsLeft(): Int = pkg.creationPoints - report().pointsSpent

    /** Traços que ainda cabem: não escolhidos e que não gerariam erro se adicionados agora. */
    fun allowedTraits(): List<TraitDef> =
        pkg.data.traits.filter {
            it.id !in traits && pkg.validateCharacter(stats, traits.toList() + it.id, checkBalance = false, skills = skills.toMap()).valid
        }

    /** Por que um traço não pode ser adicionado agora (lista vazia = pode). */
    fun whyNot(traitId: String): List<String> {
        if (pkg.trait(traitId) == null) return listOf("traço desconhecido: $traitId")
        if (traitId in traits) return listOf("traço já escolhido")
        return pkg.validateCharacter(stats, traits.toList() + traitId, checkBalance = false, skills = skills.toMap()).errors
    }

    /** Busca regras do pacote para ajudar na criação. */
    fun hints(query: String, limit: Int = 5): List<RuleHint> =
        pkg.search(query, limit).map { RuleHint(RuleHint.Source.PACOTE, it.title, it.text) }

    fun build(): FichaBuild {
        if (name.isBlank()) return FichaBuild.Invalid(listOf("dê um nome ao personagem"))
        if (pointsLeft() < 0) return FichaBuild.Invalid(listOf("pontos gastos passam do limite (${pkg.creationPoints})"))
        return pkg.buildFicha(id, name.trim(), concept.trim(), stats, traits.toList(), skills)
    }
}

/** Card de um modelo (sistema instalado) na aba de criação: o que ele é e as fichas prontas dele. */
data class SystemModel(
    val packageId: String,
    val system: String,
    val edition: String,
    val title: String,
    val creationPoints: Int,
    val statNames: List<String>,
    val examples: List<ExampleCharacter>
)

object SystemModels {
    fun list(registry: PackageRegistry, catalog: Catalog? = null): List<SystemModel> =
        registry.installed().mapNotNull { m ->
            val p = registry.get(m.id) ?: return@mapNotNull null
            val ed = catalog?.system(m.edition)
            SystemModel(
                m.id, m.system, m.edition,
                ed?.let { "${it.name} ${it.edition}" } ?: m.edition,
                p.creationPoints, p.data.stats.map { it.name }, p.data.examples
            )
        }
}
