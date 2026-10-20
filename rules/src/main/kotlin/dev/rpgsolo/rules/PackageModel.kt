package dev.rpgsolo.rules

import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.FortuneMode

/** TESTE = mecânica própria para validar o formato. REAL = sistema de verdade (exige licença verificada ou importação do usuário). */
enum class PackageKind { TESTE, REAL }

const val USER_IMPORT_SOURCE = "importacao-usuario"

/** Formato de criacao.txt: recompensa_recurso|mana=2,pv=1 (quantidade por nível). */
internal fun parseResourceRewards(raw: String?): Map<String, Int> {
    if (raw.isNullOrBlank() || raw.trim() == "-") return emptyMap()
    val result = linkedMapOf<String, Int>()
    for (entry in raw.split(",")) {
        val pair = entry.split("=", limit = 2)
        require(pair.size == 2) { "recompensa_recurso inválida: '${entry}' (use recurso=quantidade)" }
        val id = pair[0].trim()
        require(Regex("[a-z0-9][a-z0-9_-]*").matches(id)) { "recurso de recompensa inválido: '${id}'" }
        val amount = pair[1].trim().toIntOrNull()
            ?: throw IllegalArgumentException("quantidade de recompensa inválida: '${pair[1]}'")
        require(amount > 0) { "recompensa de recurso deve ser positiva: '${id}'" }
        require(result.put(id, amount) == null) { "recurso de recompensa repetido: '${id}'" }
    }
    return result
}

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
/** Bônus mecânico tipado. Escopos de teste: id do teste, atributo ou perícia; combate: ataque, dano, defesa, resistencia_dano. */
data class TraitBonus(val scope: String, val bonus: Int)

/** Bônus tipado externo ao pacote, com origem rastreável (ex.: raça, classe ou maldição). */
data class ExternalBonus(
    val scope: String,
    val bonus: Int,
    val source: String,
    /** Identidade estável da declaração; vazio significa que não há deduplicação segura por identidade. */
    val effectId: String = ""
)

/**
 * Remove somente repetições da mesma declaração explicitamente identificada.
 * Bônus diferentes que coincidem em escopo/valor continuam acumulando; não inferimos equivalência.
 */
fun distinctExternalBonuses(bonuses: Collection<ExternalBonus>): List<ExternalBonus> {
    val seen = mutableSetOf<String>()
    return bonuses.mapIndexedNotNull { index, bonus ->
        val identity = bonus.effectId.takeIf { it.isNotBlank() } ?: "#sem-id:$index"
        if (seen.add(identity)) bonus else null
    }
}

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
    val skill: String? = null,
    /** Política opcional de rolagem; pacote antigo mantém NORMAL. */
    val fortuneMode: FortuneMode = FortuneMode.NORMAL,
    /** Explosão opcional: cada face máxima gera outro dado. */
    val explodeOnMax: Boolean = false,
    /** Recurso opcional que o jogador pode gastar para usar a política de fortuna. */
    val fortuneResource: String? = null,
    /** Custo positivo do recurso para ativar vantagem/desvantagem. */
    val fortuneCost: Int = 0,
    /** Política ativada pelo gasto opcional; independente da política automática do teste. */
    val fortuneSpendMode: FortuneMode? = null
)

data class DifficultyDef(val id: String, val name: String, val value: Int)
/**
 * Efeito automático de uma condição nos testes: [dice] = +1 (Ganho, um dado extra) ou -1 (Perda, um dado a menos).
 * [stats] = atributos afetados; null = todos os testes.
 */
data class ConditionEffect(val dice: Int, val stats: Set<String>?)
data class ConditionDef(
    val id: String,
    val name: String,
    val description: String,
    val effect: ConditionEffect? = null,
    /** null = duração narrativa/indefinida; valor positivo = rodadas restantes. */
    val durationRounds: Int? = null,
    val damagePerRound: Int = 0,
    val damageResource: String = "hp"
)
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
