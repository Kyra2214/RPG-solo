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
/** Bônus fixo no total de um teste. [scope] = id de teste, id de atributo ou tipo de situação (ex.: "esquiva", "sorte"). */
data class TraitBonus(val scope: String, val bonus: Int)

data class TraitDef(
    val id: String,
    val type: String,
    val name: String,
    val cost: Int,
    val requires: List<Requirement>,
    val description: String,
    val bonuses: List<TraitBonus> = emptyList()
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
    val critFailure: IntRange?,
    /** Id opcional de perícia; quando presente, seus pontos são a base do teste. */
    val skill: String? = null
)

data class DifficultyDef(val id: String, val name: String, val value: Int)
/**
 * Efeito automático de uma condição nos testes: [dice] = +1 (Ganho, um dado extra) ou -1 (Perda, um dado a menos).
 * [stats] = atributos afetados; null = todos os testes.
 */
data class ConditionEffect(val dice: Int, val stats: Set<String>?)
data class ConditionDef(val id: String, val name: String, val description: String, val effect: ConditionEffect? = null)
data class DamageDef(val id: String, val name: String, val diceCount: Int, val diceSides: Int, val bonusStat: String?)

/** Item do catálogo: equipamento ocupa [slot]; itens narrativos não têm efeito mecânico. */
data class ItemEffect(val scope: String, val bonus: Int)

/** Item do catálogo: efeitos tipados aplicados pelo motor, nunca inferidos da descrição. */
data class ItemDef(
    val id: String, val name: String, val kind: String, val slot: String?, val description: String,
    val effects: List<ItemEffect> = emptyList()
)

/** Regra ambígua: nunca apresentada como oficial (4.1.6). */
data class UncertainRule(val id: String, val note: String)
data class TutorialStep(val order: Int, val title: String, val text: String)
data class ExampleCharacter(val id: String, val title: String, val stats: Map<String, Int>, val traits: List<String>)

/** Arquétipo: ponto de partida da criação de ficha (atributos e traços iniciais que o jogador pode editar). */
data class Archetype(
    val id: String, val name: String, val description: String,
    val stats: Map<String, Int>, val traits: List<String>
)

/**
 * Cardápio de um arquétipo: vantagens e desvantagens que combinam com ele (específicas e gerais misturadas).
 * É só sugestão: o jogador pode escolher qualquer traço do pacote, desde que passe nas regras de criação.
 */
data class ArchetypeOptions(val archetype: String, val advantages: List<String>, val disadvantages: List<String>)

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
    val items: List<ItemDef> = emptyList(),
    val creation: Map<String, String> = emptyMap(),
    /** XP total para o nível 2, 3, 4... null = o pacote não define progressão. */
    val progression: List<Int>? = null,
    val uncertain: List<UncertainRule> = emptyList(),
    val tutorial: List<TutorialStep> = emptyList(),
    val examples: List<ExampleCharacter> = emptyList(),
    val archetypes: List<Archetype> = emptyList(),
    val archetypeOptions: List<ArchetypeOptions> = emptyList()
)
