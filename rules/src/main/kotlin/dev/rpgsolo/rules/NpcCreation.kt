package dev.rpgsolo.rules

import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.SystemRef

/** De onde veio o conhecimento de regra usado para criar um NPC. */
enum class RuleSource { PACOTE, PDF_DO_USUARIO, PESQUISA_NA_REDE }

data class RuleLookup(val source: RuleSource, val snippets: List<RuleHint>) {
    /** Verdadeiro quando nem o pacote nem os PDFs responderam: a camada de IA deve pesquisar na rede. */
    val needsWebSearch: Boolean get() = source == RuleSource.PESQUISA_NA_REDE
}

/**
 * Ordem de consulta para o mestre/IA saber vantagens, desvantagens e regras de um conceito (ex.: "vilão arqueiro élfico"):
 * 1) pacote do sistema; 2) PDFs que o usuário inseriu (se houver); 3) pesquisa na rede pela IA.
 * Nada aqui exige PDF.
 */
object RuleLookupChain {
    fun lookup(query: String, pkg: RulePackage?, docs: DocumentIndex?, minPackageHits: Int = 1): RuleLookup {
        val fromPkg = pkg?.search(query)?.map { RuleHint(RuleHint.Source.PACOTE, it.title, it.text) }.orEmpty()
        if (fromPkg.size >= minPackageHits) return RuleLookup(RuleSource.PACOTE, fromPkg)
        val fromPdf = docs?.search(query, 5, pkg?.manifest?.edition).orEmpty()
            .map { RuleHint(RuleHint.Source.PDF_DO_USUARIO, it.title, it.text, it.page) }
        if (fromPdf.isNotEmpty()) return RuleLookup(RuleSource.PDF_DO_USUARIO, fromPdf)
        return RuleLookup(RuleSource.PESQUISA_NA_REDE, emptyList())
    }
}

/** O que a IA propõe para um NPC. O motor valida; a IA não decide mecânica sozinha. */
data class NpcProposal(
    val id: String,
    val name: String,
    val concept: String,
    val stats: Map<String, Int>,
    val traits: List<String>,
    /** Só usado quando não há pacote para validar (sistema só com PDF/rede): máximos dos recursos. */
    val resources: Map<String, Int> = emptyMap(),
    val ruleSource: RuleSource
)

/**
 * Ficha de NPC. Privada: o jogador não vê vantagens, desvantagens nem atributos. Eles só entram em ação no combate,
 * calculados pelo motor. `validated` = passou nas regras de um pacote instalado.
 */
data class NpcSheet(
    val ficha: Ficha,
    val validated: Boolean,
    val ruleSource: RuleSource,
    val warnings: List<String> = emptyList()
) {
    val isPrivate: Boolean get() = true
    fun toEntity() = ficha.toEntity(EntityKind.NPC)

    /** O que a interface pode mostrar ao jogador por padrão: só a identidade, nunca a mecânica. */
    fun playerView(): Pair<String, String> = ficha.name to ficha.concept
}

sealed interface NpcResult {
    data class Created(val sheet: NpcSheet) : NpcResult
    /** Proposta inválida pelas regras do pacote: nada é criado; a IA recebe os erros e tenta de novo. */
    data class Rejected(val errors: List<String>) : NpcResult
}

object NpcFactory {
    /** Com pacote: valida pelas regras do sistema e deriva os recursos. Sem pacote: aceita, marcando como não validado. */
    fun create(proposal: NpcProposal, pkg: RulePackage?, systemRef: SystemRef? = null): NpcResult {
        if (proposal.name.isBlank()) return NpcResult.Rejected(listOf("NPC sem nome"))
        if (pkg != null) {
            return when (val b = pkg.buildFicha(proposal.id, proposal.name.trim(), proposal.concept.trim(), proposal.stats, proposal.traits)) {
                is FichaBuild.Invalid -> NpcResult.Rejected(b.errors)
                is FichaBuild.Created -> NpcResult.Created(NpcSheet(b.ficha, true, proposal.ruleSource, b.warnings))
            }
        }
        val ref = systemRef ?: return NpcResult.Rejected(listOf("sem pacote: informe o sistema/edição do NPC"))
        val res = proposal.resources.mapValues { (_, max) -> Resource(max, max) }
        if (proposal.resources.values.any { it < 0 }) return NpcResult.Rejected(listOf("recurso negativo"))
        val ficha = Ficha(
            id = proposal.id,
            name = proposal.name.trim(),
            system = ref,
            concept = proposal.concept.trim(),
            stats = proposal.stats,
            resources = res,
            traits = proposal.traits
        )
        return NpcResult.Created(NpcSheet(ficha, false, proposal.ruleSource, listOf("sistema sem pacote instalado: ficha não validada pelo motor")))
    }
}
