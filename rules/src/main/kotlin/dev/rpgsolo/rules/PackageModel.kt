package dev.rpgsolo.rules

import dev.rpgsolo.engine.Comparison

/** TESTE = mecânica própria para validar o formato. REAL = sistema de verdade (exige licença verificada ou importação do usuário). */
enum class PackageKind { TESTE, REAL }

const val USER_IMPORT_SOURCE = "importacao-usuario"

data class Manifest(
    val id: String,
    /** Família do sistema (ex.: "dnd"). Herança só vale dentro da mesma família. */
    val system: String,
    /** Edição/variante exata (id do catálogo, para pacotes REAL). */
    val edition: String,
    val version: String,
    val source: String,
    val license: String,
    val licenseStatus: LicenseStatus,
    val kind: PackageKind,
    val extends: String? = null,
    /** Recurso que mata ao chegar a 0. null = o sistema não mata automaticamente. */
    val deathResource: String? = null
)

data class StatDef(val id: String, val name: String, val min: Int, val max: Int, val default: Int)

/** Recurso derivado: máximo = base + mult * atributo (stat nulo = só a base). */
data class ResourceDef(val id: String, val name: String, val base: Int, val stat: String?, val mult: Int)

sealed interface Requirement {
    data class MinStat(val stat: String, val min: Int) : Requirement
    data class HasTrait(val traitId: String) : Requirement
    /** O traço não pode ser combinado com o traço indicado (vale nos dois sentidos). */
    data class Excludes(val traitId: String) : Requirement
}

/** Vantagem, desvantagem, perícia ou poder. Custo negativo devolve pontos. */
data class TraitDef(
    val id: String,
    val type: String,
    val name: String,
    val cost: Int,
    val requires: List<Requirement>,
    val description: String
)

/** Modelo de teste. modificador = atributo * mult. Parcial e críticos só existem se o pacote declarar. */
data class CheckDef(
    val id: String,
    val name: String,
    val diceCount: Int,
    val diceSides: Int,
    val stat: String?,
    val mult: Int,
    val comparison: Comparison,
    val partialMargin: Int?,
    val critSuccess: IntRange?,
    val critFailure: IntRange?
)

data class DifficultyDef(val id: String, val name: String, val value: Int)
data class ConditionDef(val id: String, val name: String, val description: String)
data class DamageDef(val id: String, val name: String, val diceCount: Int, val diceSides: Int, val bonusStat: String?)

/** Regra ambígua: nunca apresentada como oficial (4.1.6). */
data class UncertainRule(val id: String, val note: String)
data class TutorialStep(val order: Int, val title: String, val text: String)
data class ExampleCharacter(val id: String, val title: String, val stats: Map<String, Int>, val traits: List<String>)

/** Conteúdo lido de um pacote, antes de resolver herança. */
data class PackageData(
    val manifest: Manifest,
    val stats: List<StatDef> = emptyList(),
    val resources: List<ResourceDef> = emptyList(),
    val traits: List<TraitDef> = emptyList(),
    val checks: List<CheckDef> = emptyList(),
    val difficulties: List<DifficultyDef> = emptyList(),
    val conditions: List<ConditionDef> = emptyList(),
    val damage: List<DamageDef> = emptyList(),
    val creation: Map<String, String> = emptyMap(),
    /** XP total para o nível 2, 3, 4... null = o pacote não define progressão. */
    val progression: List<Int>? = null,
    val uncertain: List<UncertainRule> = emptyList(),
    val tutorial: List<TutorialStep> = emptyList(),
    val examples: List<ExampleCharacter> = emptyList()
)
