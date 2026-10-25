package dev.rpgsolo.rules

import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.NpcDossier
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.SystemRef

/** De onde veio o conhecimento de regra usado para criar um NPC. */
enum class RuleSource { PACOTE, PESQUISA_NA_REDE, IA_NAO_VERIFICADA }

data class RuleLookup(val source: RuleSource, val snippets: List<RuleHint>) {
    /** Verdadeiro quando o pacote não respondeu: a camada de IA deve pesquisar na rede. */
    val needsWebSearch: Boolean get() = source == RuleSource.PESQUISA_NA_REDE
}

/**
 * Ordem de consulta para o mestre/IA saber vantagens, desvantagens e regras de um conceito (ex.: "vilão arqueiro élfico"):
 * 1) pacote do sistema; 2) pesquisa na rede pela IA.
 */
object RuleLookupChain {
    fun lookup(
        query: String,
        pkg: RulePackage?,
        minPackageHits: Int = 1,
    ): RuleLookup {
        val fromPkg = pkg?.search(query)?.map { RuleHint(RuleHint.Source.PACOTE, it.title, it.text) }.orEmpty()
        if (fromPkg.size >= minPackageHits) return RuleLookup(RuleSource.PACOTE, fromPkg)
        return RuleLookup(RuleSource.PESQUISA_NA_REDE, emptyList())
    }
}

/** N-27: categoria de ameaça; [percent] multiplica o PV (5 × Resistência) do NPC ou monstro. */
enum class ThreatCategory(val id: String, val percent: Int) {
    CAPANGA("capanga", 50),
    COMUM("comum", 100),
    ELITE("elite", 150),
    CHEFE("chefe", 200),
    CHEFE_FINAL("chefe-final", 300),
    ;

    /** PV de [base] sob esta categoria (arredonda para cima; mínimo 1 se [base] > 0). */
    fun scale(
        base: Int,
        percentOverride: Int? = null,
    ): Int = if (base <= 0) base else maxOf(1, ((base.toLong() * (percentOverride ?: percent) + 99) / 100).toInt())

    companion object {
        fun parse(text: String?): ThreatCategory? = entries.firstOrNull { it.id == text?.trim()?.lowercase()?.replace('_', '-') }

        /** Teto de PV máximo sem pacote: 5 × (nível + 2) × multiplicador da categoria. */
        fun healthCap(
            level: Int,
            category: ThreatCategory,
            percentOverride: Int? = null,
        ): Int = category.scale(5 * (maxOf(level, 1) + 2), percentOverride)

        /** N-34: faixa do multiplicador de PV do chefe, a critério do mestre (200 = ×2 a 300 = ×3). */
        const val BOSS_MIN_PERCENT = 200
        const val BOSS_MAX_PERCENT = 300
    }
}

/** O que a IA propõe para um NPC. O motor valida; a IA não decide mecânica sozinha. */
data class NpcProposal(
    val id: String,
    val name: String,
    val concept: String,
    val stats: Map<String, Int>,
    val traits: List<String>,
    /** Só usado quando não há pacote para validar (sistema sem pacote instalado): máximos dos recursos. */
    val resources: Map<String, Int> = emptyMap(),
    val ruleSource: RuleSource,
    val skills: Map<String, Int> = emptyMap(),
    val dossier: NpcDossier? = null,
    /** N-27: categoria de ameaça (multiplica o PV) e nível usado no teto de PV sem pacote. */
    val threat: ThreatCategory = ThreatCategory.COMUM,
    val level: Int = 1,
    /** N-34: multiplicador de PV do chefe em % (200 a 300) escolhido pelo mestre; só vale para chefe e chefe-final; null = o da categoria. */
    val bossPercent: Int? = null,
)

/**
 * Ficha de NPC. Privada: o jogador não vê vantagens, desvantagens nem atributos. Eles só entram em ação no combate,
 * calculados pelo motor. `validated` = passou nas regras de um pacote instalado.
 */
data class NpcSheet(
    val ficha: Ficha,
    val validated: Boolean,
    val ruleSource: RuleSource,
    val warnings: List<String> = emptyList(),
    val dossier: NpcDossier? = null,
) {
    val isPrivate: Boolean get() = true

    fun toEntity() = ficha.toEntity(EntityKind.NPC).copy(rulesValidated = validated)

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
    fun create(
        proposal: NpcProposal,
        pkg: RulePackage?,
        systemRef: SystemRef? = null,
    ): NpcResult {
        if (proposal.name.isBlank()) return NpcResult.Rejected(listOf("NPC sem nome"))
        proposal.bossPercent?.let {
            if (proposal.threat != ThreatCategory.CHEFE && proposal.threat != ThreatCategory.CHEFE_FINAL) {
                return NpcResult.Rejected(listOf("multiplicador de chefe só vale para a categoria chefe"))
            }
            if (it !in ThreatCategory.BOSS_MIN_PERCENT..ThreatCategory.BOSS_MAX_PERCENT) {
                return NpcResult.Rejected(listOf("multiplicador de chefe fora de ×2 a ×3 (200 a 300)"))
            }
        }
        if (pkg != null) {
            return when (
                val b =
                    pkg.buildFicha(
                        proposal.id,
                        proposal.name.trim(),
                        proposal.concept.trim(),
                        proposal.stats,
                        proposal.traits,
                        proposal.skills,
                    )
            ) {
                is FichaBuild.Invalid -> NpcResult.Rejected(b.errors)
                is FichaBuild.Created -> {
                    val hp = pkg.manifest.deathResource
                    val base = hp?.let { b.ficha.resources[it] }
                    val scaled =
                        if (hp != null && base != null && proposal.threat != ThreatCategory.COMUM) {
                            val max = proposal.threat.scale(base.max, proposal.bossPercent)
                            b.ficha.copy(resources = b.ficha.resources + (hp to Resource(max, max)))
                        } else {
                            b.ficha
                        }
                    NpcResult.Created(
                        NpcSheet(
                            scaled,
                            true,
                            proposal.ruleSource,
                            b.warnings,
                            proposal.dossier?.copy(npcId = proposal.id),
                        ),
                    )
                }
            }
        }
        val ref = systemRef ?: return NpcResult.Rejected(listOf("sem pacote: informe o sistema/edição do NPC"))
        if (proposal.stats.values.any { it < 0 }) return NpcResult.Rejected(listOf("atributo negativo"))
        if (proposal.skills.values.any { it !in 0..5 }) return NpcResult.Rejected(listOf("perícia fora de 0..5"))
        if (proposal.traits.any { it.isBlank() }) return NpcResult.Rejected(listOf("traço vazio"))
        if (proposal.resources.values.any { it < 0 }) return NpcResult.Rejected(listOf("recurso negativo"))
        val warnings = mutableListOf("sistema sem pacote instalado: ficha não validada pelo motor")
        val cap = ThreatCategory.healthCap(proposal.level, proposal.threat, proposal.bossPercent)
        val capped =
            proposal.resources.mapValues { (id, max) ->
                if (id == "pv" && max > cap) {
                    warnings += "pv $max passa do teto $cap (nível ${proposal.level}, ${proposal.threat.id}); recortado"
                    cap
                } else {
                    max
                }
            }
        val res = capped.mapValues { (_, max) -> Resource(max, max) }
        val ficha =
            Ficha(
                id = proposal.id,
                name = proposal.name.trim(),
                system = ref,
                concept = proposal.concept.trim(),
                stats = proposal.stats,
                resources = res,
                traits = proposal.traits,
                skills = proposal.skills,
            )
        return NpcResult.Created(
            NpcSheet(
                ficha,
                false,
                proposal.ruleSource,
                warnings,
                proposal.dossier?.copy(npcId = proposal.id),
            ),
        )
    }
}
