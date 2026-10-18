package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/**
 * Camadas separadas (plano, "Princípio central"): Ficha, Sistema+edição, Ambientação,
 * Regra adicional e Campanha. Trocar a ambientação nunca mexe na estrutura da ficha.
 */

/** Sistema + edição exata. Os IDs vêm do catálogo (módulo rules); o motor não interpreta. */
@Serializable
data class SystemRef(val systemId: String, val editionId: String)

/** Regras adicionais nunca são "oficiais": só existe a origem ADAPTACAO (plano 4.4.1 / 11.1.9). */
@Serializable
enum class RuleOrigin { ADAPTACAO }

/** Identidade mecânica do personagem. Reutilizável entre campanhas e sem referência a cenário. */
@Serializable
data class Ficha(
    val id: String,
    val name: String,
    val system: SystemRef,
    /** Conceito livre ("anão minerador de máquinas de combate"); não há lista fechada. */
    val concept: String = "",
    val stats: Map<String, Int> = emptyMap(),
    val resources: Map<String, Resource> = emptyMap(),
    /** Vantagens, desvantagens, perícias, poderes: nomes definidos pelo pacote de regras. */
    val traits: List<String> = emptyList(),
    /** Ids das regras adicionais aplicadas: o "selo" de adaptação na ficha (4.4.4). */
    val adaptations: List<String> = emptyList(),
    /** Pontos por perícia, separados dos atributos e dos traços narrativos. */
    val skills: Map<String, Int> = emptyMap()
) {
    fun toEntity(kind: EntityKind = EntityKind.PLAYER): Entity =
        Entity(
            id = id, name = name, kind = kind, stats = stats, resources = resources,
            skills = skills, concept = concept, traits = traits
        )
}

/** Universo ficcional. Referencia a campanha, nunca a ficha. */
@Serializable
data class Ambientacao(
    val id: String,
    val campaignId: String,
    val name: String,
    val tone: String = "",
    val technology: String = "",
    val organizations: List<String> = emptyList(),
    val conflicts: List<String> = emptyList(),
    val fictionalGear: List<String> = emptyList()
)

/** Adaptação explícita para algo que o sistema não cobre. */
@Serializable
data class RegraAdicional(
    val id: String,
    val name: String,
    val description: String,
    /** Efeitos mecânicos em números (ex.: "forca" a +1), para o jogador ver antes de aprovar. */
    val effects: Map<String, Int> = emptyMap(),
    val limit: String = "",
    val origin: RuleOrigin = RuleOrigin.ADAPTACAO,
    val approved: Boolean = false
)

/** Estado da aventura: liga ficha, sistema, ambientação e regras adicionais. */
data class Campanha(
    val id: String,
    val fichaId: String,
    val system: SystemRef,
    val ambientacao: Ambientacao,
    val additionalRules: List<RegraAdicional> = emptyList()
) {
    /** Troca o cenário sem tocar na ficha (9.4.4). */
    fun withAmbientacao(a: Ambientacao): Campanha = copy(ambientacao = a.copy(campaignId = id))

    /** Adaptações ainda sem aprovação do jogador. */
    fun pendingApproval(): List<RegraAdicional> = additionalRules.filter { !it.approved }

    /** Problemas ao usar [ficha] nesta campanha. Lista vazia = coerente. */
    fun validate(ficha: Ficha): List<String> {
        val problems = mutableListOf<String>()
        if (ficha.id != fichaId) problems += "ficha ${ficha.id} não é a da campanha ($fichaId)"
        if (ficha.system != system) problems += "ficha de ${ficha.system} em campanha de $system"
        if (ambientacao.campaignId != id) problems += "ambientação aponta para outra campanha"
        return problems
    }
}
