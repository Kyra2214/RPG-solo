package dev.rpgsolo.rules

import dev.rpgsolo.engine.Comparison
import dev.rpgsolo.engine.DeathMode
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
        require(pair.size == 2) { "recompensa_recurso inválida: '$entry' (use recurso=quantidade)" }
        val id = pair[0].trim()
        require(Regex("[a-z0-9][a-z0-9_-]*").matches(id)) { "recurso de recompensa inválido: '$id'" }
        val amount =
            pair[1].trim().toIntOrNull()
                ?: throw IllegalArgumentException("quantidade de recompensa inválida: '${pair[1]}'")
        require(amount > 0) { "recompensa de recurso deve ser positiva: '$id'" }
        require(result.put(id, amount) == null) { "recurso de recompensa repetido: '$id'" }
    }
    return result
}

data class Manifest(
    val id: String,
    /** Família do sistema (ex.: "proprio"). Herança só vale dentro da mesma família. */
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
    val deathResource: String? = null,
    /** M-04: PERMADEATH ou DOWNED (`modo_morte`). null = não declarado (herda do pai; sem pai, PERMADEATH). */
    val deathMode: DeathMode? = null,
    /** M-04.4: id do teste (`testes.txt`) usado para sobreviver caído (`teste_sobrevivencia`). null = não declarado (herda do pai). */
    val survivalCheck: String? = null,
    /** M-04.4: id da dificuldade do teste de sobrevivência (`dificuldade_sobrevivencia`). null = dificuldade padrão. */
    val survivalDifficulty: String? = null,
    /** M-04.4: falhas que matam o caído, 1–10 (`falhas_morte`). null = não declarado (herda do pai; sem pai, 3). */
    val survivalFailures: Int? = null,
)

data class StatDef(val id: String, val name: String, val min: Int, val max: Int, val default: Int) {
    /** Atributo sem teto (`max` = "-" em `atributos.txt`): guardado como [NO_CAP]. */
    val uncapped: Boolean get() = max == NO_CAP

    companion object {
        const val NO_CAP: Int = Int.MAX_VALUE
    }
}

/** Recurso derivado: máximo = max(min, base + mult * atributo) (stat nulo = só a base; min padrão 0, N-03). */
data class ResourceDef(val id: String, val name: String, val base: Int, val stat: String?, val mult: Int, val min: Int = 0)

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
    val effectId: String = "",
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

/** MU-06.1: bônus de perícia que só vale com o item [itemId] equipado (qualquer slot). */

/** MU-07.3: magia à venda; preço em moedas base (prata). Declarada por `magia_<id>_preco` em `criacao.txt`. */
data class SpellDef(val id: String, val name: String, val price: Int)

/** MU-07.1: pagamento em moedas: [silver] de prata e [gold] de ouro gastos, com [change] de troco em prata. */
data class CoinPayment(val silver: Int, val gold: Int, val change: Int)

data class ConditionalBonus(val scope: String, val bonus: Int, val itemId: String)

data class TraitDef(
    val id: String,
    val type: String,
    val name: String,
    val cost: Int,
    val requires: List<Requirement>,
    val description: String,
    val bonuses: List<TraitBonus> = emptyList(),
    /** Usos máximos entre dois descansos (M-01); 0 = sem controle de usos. */
    val uses: Int = 0,
    /** MU-06.1: bônus condicionais à posse equipada de um item; só perícias podem declará-los. */
    val conditionalBonuses: List<ConditionalBonus> = emptyList(),
    /** MU-06.2: bônus da perícia com o nível 2 liberado; só perícias. */
    val level2Bonuses: List<TraitBonus> = emptyList(),
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
    val fortuneSpendMode: FortuneMode? = null,
    /** Recurso de sorte/destino gasto DEPOIS da rolagem (F3); independente da fortuna paga antes de rolar. */
    val luckResource: String? = null,
    /** Custo positivo por uso da sorte pós-rolagem. */
    val luckCost: Int = 0,
    /** Formas permitidas: repetir a rolagem e/ou somar uma face. Vazio = o teste não aceita sorte pós-rolagem. */
    val luckKinds: Set<dev.rpgsolo.engine.LuckKind> = emptySet(),
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
    val damageResource: String = "hp",
    /** M-05: teste que remove a condição com sucesso (`remocao`, coluna 8: `teste` ou `teste:dificuldade`). */
    val removalCheck: String? = null,
    /** M-05: id da dificuldade do teste de remoção; null = dificuldade padrão do pacote. */
    val removalDifficulty: String? = null,
    /** N-08: dano por rodada em % do máximo do recurso (coluna 6 com sufixo `%`); 0 = usa [damagePerRound]. */
    val damagePercent: Int = 0,
)

data class DamageDef(val id: String, val name: String, val diceCount: Int, val diceSides: Int, val bonusStat: String?)

/** Item do catálogo: equipamento ocupa [slot]; itens narrativos não têm efeito mecânico. */
data class ItemEffect(val scope: String, val bonus: Int)

/** Item do catálogo: efeitos tipados aplicados pelo motor, nunca inferidos da descrição. */
data class ItemDef(
    val id: String,
    val name: String,
    val kind: String,
    val slot: String?,
    val description: String,
    val effects: List<ItemEffect> = emptyList(),
    /** Preço em unidades do item de moeda (`moeda_item` em criacao.txt); null = não negociável. */
    val price: Int? = null,
    /** N-12: nível mínimo do portador para equipar (1, 5, 10, 20, 30...); qualidades épica e lendária exigem ao menos 10. */
    val minLevel: Int = 1,
    /** N-13: peso da peça de armadura (coluna 9 `peso`); null = sem peso, nunca sofre penalidade de classe. */
    val weight: ItemWeight? = null,
    /** N-13: tipo da arma (coluna 10 `tipo`, ex.: espada, arco, cajado); null = sem tipo, nunca sofre penalidade de classe. */
    val weaponType: String? = null,
    /** N-14: mãos que a arma ocupa (coluna 11 `maos`, 1 ou 2); duas mãos bloqueia o escudo e rende ataque ×1,5. */
    val hands: Int = 1,
    /** N-16: elemento da arma (coluna 12 `elemento`); NONE = sem elemento. */
    val element: dev.rpgsolo.engine.ElementPath = dev.rpgsolo.engine.ElementPath.NONE,
    /** N-16: condição aplicada ao acertar (coluna 13 `condicao`); null = nenhuma. */
    val conditionId: String? = null,
    /** N-16: chance base em % (coluna 14 `chance`, 1..100; d100 + 2% por Sorte, teto 90% sem itens/buffs). */
    val conditionChance: Int = 0,
    /** Escala por nível (coluna 15 `escala`): escopo que ganha ⌈nível mínimo ÷ 10⌉ (×5 em `max_pv`) quando o item não declara efeito nesse escopo; null = sem escala. */
    val scaleScope: String? = null,
)

/** N-13: peso de equipamento, de acordo com a classe (pesado, médio ou leve). */
enum class ItemWeight(val id: String, val setNames: List<String>) {
    PESADO("pesado", listOf("Baluarte", "Couraça", "Titã")),
    MEDIO("medio", listOf("Patrulha", "Malha", "Andarilho")),
    LEVE("leve", listOf("Véu", "Manto", "Sombra")),
    ;

    companion object {
        fun fromId(id: String): ItemWeight? = entries.firstOrNull { it.id == id }
    }
}

/** Quem recebe o efeito de um poder. */
enum class PowerTarget { SELF, ENEMY }

/** Efeito tipado de um poder; o motor aplica, nunca a descrição. */
enum class PowerEffectKind { HEAL, DAMAGE, APPLY_CONDITION, REMOVE_CONDITION, SHIELD }

/**
 * Poder ativável. Exige o traço [traitId] (do tipo `poder`) na ficha, gasta [cost] de [resource] e, se houver [checkId],
 * só produz efeito com sucesso nesse teste. [param]: HEAL = quantidade (inteiro positivo, recurso de saúde do pacote);
 * DAMAGE = id de perfil de dano; APPLY_CONDITION/REMOVE_CONDITION = id de condição.
 */
data class PowerDef(
    val id: String,
    val name: String,
    val description: String,
    val traitId: String,
    val resource: String,
    val cost: Int,
    val target: PowerTarget,
    val effect: PowerEffectKind,
    val param: String,
    val checkId: String? = null,
)

/**
 * Perfil de perigo (leve, médio, grave, mortal...): a IA escolhe o id, o motor calcula o dano como [percent] % da vida
 * máxima do personagem (arredondado para cima, mínimo 1). [percent] de 1 a 100.
 */
data class HazardDef(val id: String, val name: String, val description: String, val percent: Int)

/**
 * MU-06.3/06.4: habilidade declarada pelo pacote. [duration]: "N" rodadas fixas, "sorteio" (1d6 rolado pelo motor),
 * "combate" ou "cena". [bonuses] positivos; [drawbacks] negativos (vazio = sem desvantagem).
 */
data class AbilityDef(
    val id: String,
    val name: String,
    val cost: Int,
    val trigger: dev.rpgsolo.engine.AbilityTrigger,
    val bonuses: List<dev.rpgsolo.engine.AbilityBonus>,
    val duration: String,
    val drawbacks: List<dev.rpgsolo.engine.AbilityBonus> = emptyList(),
) {
    fun toSpec(): dev.rpgsolo.engine.AbilitySpec =
        dev.rpgsolo.engine.AbilitySpec(
            id, name, cost, trigger, bonuses,
            fixedRounds = duration.toIntOrNull(),
            drawbacks = drawbacks,
            untilCombatEnd = duration == "combate",
            untilSceneEnd = duration == "cena",
        )
}

/**
 * MU-03.3/MU-06.8: exceção mecânica declarada pelo pacote para uma entidade ([entityId], ex.: slug de `request_npc`).
 * [modifiers] positivos ou negativos; [immuneConditions] ids de condição. Deve declarar ao menos um efeito.
 */
data class ExceptionDef(
    val id: String,
    val entityId: String,
    val description: String,
    val modifiers: List<dev.rpgsolo.engine.AbilityBonus> = emptyList(),
    val immuneConditions: List<String> = emptyList(),
)

/** Regra ambígua: nunca apresentada como oficial (4.1.6). */
data class UncertainRule(val id: String, val note: String)

data class TutorialStep(val order: Int, val title: String, val text: String)

data class ExampleCharacter(val id: String, val title: String, val stats: Map<String, Int>, val traits: List<String>)

/** Arquétipo: ponto de partida da criação de ficha (atributos e traços iniciais que o jogador pode editar). */
data class Archetype(
    val id: String,
    val name: String,
    val description: String,
    val stats: Map<String, Int>,
    val traits: List<String>,
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
    val archetypeOptions: List<ArchetypeOptions> = emptyList(),
    val powers: List<PowerDef> = emptyList(),
    val hazards: List<HazardDef> = emptyList(),
    val abilities: List<AbilityDef> = emptyList(),
    val exceptions: List<ExceptionDef> = emptyList(),
    /** N-20: receitas de ofício de `receitas.txt`. */
    val recipes: List<RecipeDef> = emptyList(),
    /** N-32.2: tipos de corpo, ataques, truques e comidas do companheiro de `companheiros.txt`. */
    val companions: CompanionData = CompanionData(),
)

/**
 * N-20: receita de ofício (criar, preparar, consertar ou identificar). [inputs] e [outputs] são ids de item (aceitam qualidade) com quantidade;
 * [keepOnFailure] são entradas que não se perdem na falha (ex.: a peça a consertar). [minLevel] é o nível mínimo do personagem;
 * [difficulty] (0–40) reduz a chance em pontos percentuais.
 */
data class RecipeDef(
    val id: String,
    val name: String,
    val kind: dev.rpgsolo.engine.CraftKind,
    val inputs: Map<String, Int>,
    val outputs: Map<String, Int>,
    val minLevel: Int = 1,
    val difficulty: Int = 0,
    val keepOnFailure: Set<String> = emptySet(),
    /** N-21: degrau de profissão (`profissao-<arvore>-<1..5>`) que libera a receita; null = qualquer herói. */
    val skillId: String? = null,
    /** N-21: degrau que tira a chance de falha (100%); null = nenhum. */
    val guaranteedBy: String? = null,
    /** N-35: dias que a receita leva (avançam o relógio da campanha ao fazer, com sucesso ou não); 0 = instantânea. */
    val days: Int = 0,
)

/** N-21: nível máximo de equipamento que cada degrau (1 a 5) de uma profissão de ofício pode criar. */
val CRAFT_TIER_LEVEL_CAP: List<Int> = listOf(1, 5, 10, 20, 30)

/** N-32.2: dano do ataque do animal em relação ao status Ataque dele: leve ×0,8, normal ×1, alto ×1,3. */
enum class CompanionDamage(val id: String, val percent: Int) {
    LIGHT("leve", 80),
    NORMAL("normal", 100),
    HIGH("alto", 130),
    ;

    companion object {
        fun fromId(id: String): CompanionDamage? = entries.firstOrNull { it.id == id }
    }
}

/** N-32.2: efeito extra de um ataque do animal: nenhum, condição (d100), elemento ou à distância. */
enum class CompanionEffectKind { NONE, CONDITION, ELEMENT, RANGED }

data class CompanionBodyDef(val id: String, val name: String)

/**
 * N-32.2: opção de ataque de um tipo de corpo. [effectRef] é o id da condição (CONDITION) ou do elemento (ELEMENT);
 * [baseChance] (1–100) é a chance base de efeito em d100 (só CONDITION; +5% por nível da habilidade, teto 90%, calculado no N-32.3).
 */
data class CompanionAttackDef(
    val id: String,
    val bodyType: String,
    val name: String,
    val damage: CompanionDamage,
    val effectKind: CompanionEffectKind = CompanionEffectKind.NONE,
    val effectRef: String? = null,
    val baseChance: Int = 0,
)

/** N-32.2: truque em d20: [difficulty] (1–20) e [minLoyalty] (0–100) para aprender; [combat] = pode ser ordenado em combate. */
data class CompanionTrickDef(
    val id: String,
    val name: String,
    val difficulty: Int,
    val minLoyalty: Int,
    val combat: Boolean,
    val description: String,
)

/** N-32.2: comida aceita por um tipo de corpo; [baseGain] (comum 2, boa 5, rara 10) dobra se [preferred]. */
data class CompanionFoodDef(val bodyType: String, val itemId: String, val baseGain: Int, val preferred: Boolean)

data class CompanionData(
    val bodies: List<CompanionBodyDef> = emptyList(),
    val attacks: List<CompanionAttackDef> = emptyList(),
    val tricks: List<CompanionTrickDef> = emptyList(),
    val foods: List<CompanionFoodDef> = emptyList(),
) {
    val isEmpty: Boolean get() = bodies.isEmpty() && attacks.isEmpty() && tricks.isEmpty() && foods.isEmpty()
}
