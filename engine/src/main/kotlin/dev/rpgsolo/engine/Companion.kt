package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/**
 * N-32.1: companheiro adestrado (estado, lealdade e cuidados). O animal é uma entidade NPC comum (ver [Entity]) com
 * relação ao dono de pelo menos [ALLY_RELATION_MIN], então segue a mesma lógica de aliado do motor; os dados próprios
 * do adestramento ficam em [CompanionState]. Ataque, truques e turno entram nos módulos seguintes.
 */
object Companions {
    /** Tipos de corpo (ids); cada um oferece 3 opções de ataque, a definir no pacote (N-32.2). */
    val BODY_TYPES: Set<String> = setOf("quadrupede", "alado", "rastejante", "aquatico", "humanoide", "inseto")

    const val MAX_LOYALTY = 100

    /** A lealdade inicial é um d20 rolado pelo chamador ao adestrar. */
    const val INITIAL_LOYALTY_MIN = 1
    const val INITIAL_LOYALTY_MAX = 20

    /** Ganho base do carinho (uma vez por descanso). */
    const val PET_BASE_GAIN = 3

    /** N-33.2d: readestrar um animal perdido tira 30 pontos percentuais da chance; há no máximo 2 tentativas. */
    const val RETRAIN_PENALTY = 30
    const val RETRAIN_MAX_ATTEMPTS = 2

    /**
     * N-33.3: pontos de status do Treinador (1 por grau, 1..5) para o animal que anda com o herói; a distribuição segue o tipo de
     * corpo (ASSUMINDO: ordem de prioridade abaixo, definida por mim e ajustável; o primeiro ponto vai para o status principal).
     */
    private val TRAINER_ORDER: Map<String, List<String>> =
        mapOf(
            "quadrupede" to listOf("ataque", "resistencia", "velocidade", "ataque", "resistencia"),
            "alado" to listOf("velocidade", "destreza", "ataque", "velocidade", "destreza"),
            "rastejante" to listOf("destreza", "ataque", "resistencia", "destreza", "ataque"),
            "aquatico" to listOf("resistencia", "velocidade", "ataque", "resistencia", "velocidade"),
            "humanoide" to listOf("destreza", "ataque", "resistencia", "destreza", "ataque"),
            "inseto" to listOf("destreza", "velocidade", "ataque", "destreza", "velocidade"),
        )

    fun trainerPoints(
        bodyType: String,
        degree: Int,
    ): Map<String, Int> = (TRAINER_ORDER[bodyType] ?: emptyList()).take(degree.coerceIn(0, TAMER_DEGREE_MAX)).groupingBy { it }.eachCount()

    /** Chance (%) de readestrar: a chance normal da receita de adestrar menos [RETRAIN_PENALTY], entre 5% e 95% (como as receitas). */
    fun retrainChance(baseChance: Int): Int = (baseChance - RETRAIN_PENALTY).coerceIn(5, 95)

    /** Graus (nível) da profissão Adestrador. */
    const val TAMER_DEGREE_MIN = 1
    const val TAMER_DEGREE_MAX = 5

    /** Maior ganho base aceito por comida (rara = 10; a preferida da espécie dobra). */
    const val MAX_BASE_GAIN = 20

    /** N-32.3: +3 no d20 (+15%) por nível de adestramento; lealdade +5 por nível do animal; 1 ponto de habilidade a cada 3 níveis. */
    const val TRAINING_BONUS_PER_LEVEL = 3
    const val LEVEL_UP_LOYALTY = 5
    const val ABILITY_POINT_EVERY = 3
    const val EFFECT_CHANCE_PER_LEVEL = 5
    const val EFFECT_CHANCE_CAP = 90

    /** Dano do ataque: status Ataque do animal × fator da opção (leve 80, normal 100, alto 130), arredondado para cima. */
    fun attackDamage(
        attackStat: Int,
        damagePercent: Int,
    ): Int = maxOf(0, (attackStat * damagePercent + 99) / 100)

    /** Chance (d100) de efeito do ataque: base + 5% por nível da habilidade, teto 90%; o animal não usa Sorte. */
    fun effectChance(
        baseChance: Int,
        attackLevel: Int,
    ): Int = minOf(EFFECT_CHANCE_CAP, baseChance + EFFECT_CHANCE_PER_LEVEL * attackLevel)

    /** Sucesso de um d20: 20 natural sempre passa, 1 natural sempre falha; senão d20 + [bonus] >= [difficulty]. */
    fun d20Success(
        roll: Int,
        difficulty: Int,
        bonus: Int,
    ): Boolean = roll == 20 || (roll != 1 && roll + bonus >= difficulty)

    /** Chance (%) do d20 de um truque, com o bônus de adestramento (+3 por nível). */
    fun trickD20Percent(
        difficulty: Int,
        trainingLevel: Int,
    ): Int = 5 * (1..20).count { d20Success(it, difficulty, TRAINING_BONUS_PER_LEVEL * trainingLevel) }

    /** Chance de executar a ordem: chance do d20 + (100 − chance do d20) × lealdade ÷ 100 (arredonda para baixo); lealdade 100 = 100%. */
    fun orderChance(
        d20Percent: Int,
        loyalty: Int,
    ): Int = if (loyalty >= MAX_LOYALTY) 100 else d20Percent + (100 - d20Percent) * loyalty / 100

    /** Aprender truque: d20 + lealdade ÷ 10 (para baixo) + 3 por nível de adestramento contra a dificuldade. */
    fun learnSuccess(
        roll: Int,
        difficulty: Int,
        loyalty: Int,
        trainingLevel: Int,
    ): Boolean = d20Success(roll, difficulty, loyalty / 10 + TRAINING_BONUS_PER_LEVEL * trainingLevel)

    /** Fator, em %, pelo nível do animal: quanto mais alto, mais difícil ganhar lealdade. */
    fun animalTierPercent(animalLevel: Int): Int =
        when {
            animalLevel <= 5 -> 100
            animalLevel <= 10 -> 75
            animalLevel <= 20 -> 50
            else -> 25
        }

    /** Fator, em %, pelo grau do Adestrador: 100 no grau 1, 200 no grau 5. */
    fun tamerPercent(tamerDegree: Int): Int = 100 + 25 * (tamerDegree - 1)

    /**
     * Ganho de lealdade: base × fator do animal × fator do Adestrador, arredondado para cima uma só vez, mínimo 1.
     * Ex.: comida boa (+5) em animal nível 35 rende 2 com Adestrador 1 e 3 com Adestrador 5.
     */
    fun loyaltyGain(
        baseGain: Int,
        animalLevel: Int,
        tamerDegree: Int,
    ): Int {
        val scaled = baseGain.toLong() * animalTierPercent(animalLevel) * tamerPercent(tamerDegree)
        return maxOf(1, ((scaled + 9_999) / 10_000).toInt())
    }
}

/** Dados do adestramento de um animal; o animal em si é a entidade [entityId]. */
@Serializable
data class CompanionState(
    val entityId: String,
    val ownerId: String,
    /** Um de [Companions.BODY_TYPES]. */
    val bodyType: String,
    /** 0..[Companions.MAX_LOYALTY]; com 100 a ordem é sempre executada. */
    val loyalty: Int,
    /** Nível de adestramento: +3 no d20 dos truques por nível (+15%). */
    val trainingLevel: Int = 1,
    /** Dia ([GameState.day]) do último carinho; o carinho vale uma vez por descanso (dia). */
    val lastPetDay: Int = -1,
    /** N-32.3: id do único ataque escolhido (validado contra o pacote por quem chama); null = ainda não escolheu. */
    val attackId: String? = null,
    /** N-32.3: nível da habilidade de ataque (sobe com pontos de habilidade do animal). */
    val attackLevel: Int = 1,
    /** N-32.3: ids dos truques aprendidos. */
    val tricks: Set<String> = emptySet(),
    /** N-32.3: dia ([GameState.day]) da última tentativa de aprender truque (1 por descanso). */
    val lastLearnDay: Int = -1,
    /** N-33.2: guardado no criadouro (não anda com o herói); só 1 animal por dono fica fora do criadouro. */
    val stabled: Boolean = false,
    /** N-33.2: semana ([Kennel.weekOf]) da última visita; deixar o animal no criadouro conta como visita. */
    val visitedWeek: Int = -1,
    /** N-33.2: lealdade chegou a 0 e o dono perdeu o animal; a entidade fica no estado para o readestramento (N-33.2d). */
    val lost: Boolean = false,
    /** N-33.2d: tentativas de readestramento já feitas (0..[Companions.RETRAIN_MAX_ATTEMPTS]). */
    val retrainAttempts: Int = 0,
    /** N-33.2d: as tentativas acabaram sem sucesso; o animal está perdido para sempre. */
    val abandoned: Boolean = false,
    /** N-33.3: pontos de status do Treinador somados hoje ao animal (só o que anda com o herói tem). */
    val trainerBonus: Map<String, Int> = emptyMap(),
) {
    init {
        require(entityId.isNotBlank()) { "companheiro exige entidade" }
        require(ownerId.isNotBlank()) { "companheiro exige dono" }
        require(bodyType in Companions.BODY_TYPES) { "tipo de corpo inválido: $bodyType" }
        require(loyalty in 0..Companions.MAX_LOYALTY) { "lealdade fora de 0..100" }
        require(trainingLevel >= 1) { "nível de adestramento inválido" }
        require(attackLevel >= 1) { "nível de ataque inválido" }
        require(retrainAttempts in 0..Companions.RETRAIN_MAX_ATTEMPTS) {
            "tentativas de readestramento fora de 0..${Companions.RETRAIN_MAX_ATTEMPTS}"
        }
    }
}

/**
 * Adestra [animal] (entidade NPC nova) para [ownerId]; [loyaltyRoll] é o d20 já rolado pelo chamador (1..20). O novo
 * animal passa a andar com o herói e o que andava antes vai para o criadouro (N-33.2).
 */
data class TameCompanion(
    val ownerId: String,
    val animal: Entity,
    val bodyType: String,
    val loyaltyRoll: Int,
    /** N-33.2: limite de animais do dono (1 por grau de Adestrador); null = sem limite. Animais perdidos não contam. */
    val maxAnimals: Int? = null,
) : Command

/** Comida dada ao animal: [baseGain] já vem da qualidade (e do dobro da preferida) e [tamerDegree] é o grau do Adestrador (1..5). */
data class FeedCompanion(val animalId: String, val baseGain: Int, val tamerDegree: Int) : Command

/** Carinho: ganho base [Companions.PET_BASE_GAIN], uma vez por descanso. */
data class PetCompanion(val animalId: String, val tamerDegree: Int) : Command

/** O animal foi adestrado: entra a entidade, o estado de adestramento e a relação de aliado com o dono. */
@Serializable
data class CompanionTamed(
    val entity: Entity,
    val companion: CompanionState,
    /** N-33.2: animais que andavam com o herói e vão para o criadouro; dados antigos assumem vazio. */
    val stabledIds: List<String> = emptyList(),
) : Event

/** A lealdade do animal mudou para [newValue]; [petDay] (se houver) registra o dia do carinho. */
@Serializable
data class CompanionLoyaltyChanged(val animalId: String, val newValue: Int, val delta: Int, val petDay: Int? = null) : Event

/**
 * N-33.1: XP do animal por combate vencido (a única fonte de XP do animal). O nível sai da mesma tabela do herói
 * ([EngineConfig.levelFor]); cada nível ganho soma [Companions.LEVEL_UP_LOYALTY] de lealdade (teto 100) e 1 ponto de
 * habilidade a cada [Companions.ABILITY_POINT_EVERY] níveis.
 */
data class GainCompanionXp(val animalId: String, val amount: Int) : Command

@Serializable
data class CompanionXpGained(
    val animalId: String,
    val amount: Int,
    val newLevel: Int,
    val newLoyalty: Int,
    val abilityPointsGained: Int,
) : Event

/** N-32.3: escolhe o único ataque do animal (uma vez); [attackId] vem do pacote e é validado por quem chama. */
data class ChooseCompanionAttack(val animalId: String, val attackId: String) : Command

/** N-32.3: gasta 1 ponto de habilidade do animal para subir o nível do ataque escolhido. */
data class RaiseCompanionAttack(val animalId: String) : Command

/** N-32.3: o animal sobe 1 nível: lealdade +5 (teto 100) e 1 ponto de habilidade a cada 3 níveis. */
data class LevelUpCompanion(val animalId: String) : Command

/** N-32.3: tenta aprender um truque (1 por descanso); [roll] é o d20 já rolado, [difficulty] e [minLoyalty] vêm do pacote. */
data class LearnCompanionTrick(
    val animalId: String,
    val trickId: String,
    val difficulty: Int,
    val minLoyalty: Int,
    val roll: Int,
) : Command

/** N-32.3: ordena um truque aprendido; [percentRoll] é o d100 já rolado (ignorado com lealdade 100); em combate só vale [combatTrick]. */
data class OrderCompanionTrick(
    val animalId: String,
    val trickId: String,
    val difficulty: Int,
    val percentRoll: Int,
    val combatTrick: Boolean,
) : Command

@Serializable
data class CompanionAttackChosen(val animalId: String, val attackId: String) : Event

@Serializable
data class CompanionAttackRaised(val animalId: String, val newLevel: Int) : Event

@Serializable
data class CompanionLeveledUp(val animalId: String, val newLevel: Int, val newLoyalty: Int, val abilityPointGained: Boolean) : Event

@Serializable
data class CompanionTrickLearnAttempted(
    val animalId: String,
    val trickId: String,
    val roll: Int,
    val success: Boolean,
    val day: Int,
) : Event

@Serializable
data class CompanionTrickOrdered(val animalId: String, val trickId: String, val chance: Int, val success: Boolean) : Event

/**
 * N-32.3: turno do animal no combate. Só no turno dele: aplica [damage] em [resource] do alvo, tenta o efeito do ataque
 * ([effectRoll] é o d100 já rolado; vale se <= [effectChance]) e passa o turno. Sem teste de acerto (ASSUMINDO: o
 * ataque do animal sempre acerta). Retorna o resultado do lote; recusa desfaz tudo.
 */
fun GameEngine.companionStrike(
    animalId: String,
    targetId: String,
    resource: String,
    damage: Int,
    condition: String? = null,
    conditionRounds: Int? = null,
    effectChance: Int = 0,
    effectRoll: Int = 100,
): ExecResult {
    val combat = state.combat ?: return ExecResult.Rejected("não há combate ativo")
    val self = state.companions[animalId] ?: return ExecResult.Rejected("não é um companheiro: $animalId")
    self.awayReason()?.let { return ExecResult.Rejected(it) }
    if (combat.current != animalId) return ExecResult.Rejected("não é o turno de $animalId")
    if (targetId == animalId || targetId == state.companions.getValue(animalId).ownerId || targetId in state.allyIdsInCombat(animalId)) {
        return ExecResult.Rejected("o animal não ataca aliados nem o dono")
    }
    val commands = mutableListOf<Command>(Damage(targetId, resource, damage, sourceId = animalId))
    if (condition != null && effectRoll <= effectChance) commands += AddCondition(targetId, condition, conditionRounds)
    commands += NextTurn
    return executeAll(commands)
}

internal fun decideCompanion(
    state: GameState,
    cmd: Command,
    config: EngineConfig = EngineConfig(),
): Decision {
    fun no(reason: String) = Decision.No(reason)
    return when (cmd) {
        is TameCompanion -> {
            val owner = state.entities[cmd.ownerId] ?: return no("dono inexistente: ${cmd.ownerId}")
            if (owner.kind != EntityKind.PLAYER) return no("só o personagem do jogador adestra animais")
            if (!owner.alive) return no("${owner.name} está morto")
            if (cmd.animal.id.isBlank()) return no("animal sem id")
            if (state.entities.containsKey(cmd.animal.id)) return no("entidade já existe: ${cmd.animal.id}")
            if (cmd.animal.kind != EntityKind.NPC) return no("o animal adestrado é uma entidade NPC")
            if (!cmd.animal.alive) return no("o animal precisa estar vivo")
            if (cmd.bodyType !in Companions.BODY_TYPES) return no("tipo de corpo inválido: ${cmd.bodyType}")
            if (cmd.loyaltyRoll !in Companions.INITIAL_LOYALTY_MIN..Companions.INITIAL_LOYALTY_MAX) {
                return no("a lealdade inicial é um d20 (1..20)")
            }
            val mine = state.companions.values.filter { it.ownerId == cmd.ownerId && !it.lost }
            if (cmd.maxAnimals != null && mine.size >= cmd.maxAnimals) {
                return no(
                    "limite de animais atingido: ${mine.size}/${cmd.maxAnimals}",
                )
            }
            val toStable = mine.filter { !it.stabled }.map { it.entityId }.sorted()
            if (toStable.isNotEmpty() && state.combat != null) return no("não dá para trocar de animal durante o combate")
            Decision.Ok(
                listOf(CompanionTamed(cmd.animal, CompanionState(cmd.animal.id, cmd.ownerId, cmd.bodyType, cmd.loyaltyRoll), toStable)),
            )
        }

        is FeedCompanion -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            companion.awayReason()?.let { return no(it) }
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (!animal.alive) return no("${animal.name} está morto")
            if (cmd.baseGain !in 1..Companions.MAX_BASE_GAIN) return no("ganho base fora de 1..${Companions.MAX_BASE_GAIN}")
            if (cmd.tamerDegree !in Companions.TAMER_DEGREE_MIN..Companions.TAMER_DEGREE_MAX) return no("grau do Adestrador fora de 1..5")
            if (companion.loyalty >= Companions.MAX_LOYALTY) return no("a lealdade de ${animal.name} já está no máximo")
            val now = minOf(Companions.MAX_LOYALTY, companion.loyalty + Companions.loyaltyGain(cmd.baseGain, animal.level, cmd.tamerDegree))
            Decision.Ok(listOf(CompanionLoyaltyChanged(cmd.animalId, now, now - companion.loyalty)))
        }

        is PetCompanion -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            companion.awayReason()?.let { return no(it) }
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (!animal.alive) return no("${animal.name} está morto")
            if (cmd.tamerDegree !in Companions.TAMER_DEGREE_MIN..Companions.TAMER_DEGREE_MAX) return no("grau do Adestrador fora de 1..5")
            if (state.day <= companion.lastPetDay) return no("${animal.name} já recebeu carinho neste descanso")
            if (companion.loyalty >= Companions.MAX_LOYALTY) return no("a lealdade de ${animal.name} já está no máximo")
            val now =
                minOf(
                    Companions.MAX_LOYALTY,
                    companion.loyalty + Companions.loyaltyGain(Companions.PET_BASE_GAIN, animal.level, cmd.tamerDegree),
                )
            Decision.Ok(listOf(CompanionLoyaltyChanged(cmd.animalId, now, now - companion.loyalty, petDay = state.day)))
        }

        is GainCompanionXp -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            if (companion.lost) return no("o animal foi perdido")
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (!animal.alive) return no("${animal.name} está morto")
            if (cmd.amount <= 0) return no("XP inválido")
            val total = animal.xp.toLong() + cmd.amount.toLong()
            if (total > Int.MAX_VALUE) return no("XP excede o limite suportado")
            val newLevel = maxOf(animal.level, config.levelFor(total.toInt()))
            val gained = newLevel - animal.level
            val points = ((animal.level + 1)..newLevel).count { it % Companions.ABILITY_POINT_EVERY == 0 }
            val loyalty =
                minOf(
                    Companions.MAX_LOYALTY.toLong(),
                    companion.loyalty.toLong() + gained.toLong() * Companions.LEVEL_UP_LOYALTY,
                ).toInt()
            Decision.Ok(listOf(CompanionXpGained(cmd.animalId, cmd.amount, newLevel, loyalty, points)))
        }

        is ChooseCompanionAttack -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            if (companion.lost) return no("o animal foi perdido")
            if (cmd.attackId.isBlank()) return no("ataque vazio")
            if (companion.attackId != null) return no("o ataque já foi escolhido: ${companion.attackId}")
            Decision.Ok(listOf(CompanionAttackChosen(cmd.animalId, cmd.attackId)))
        }

        is RaiseCompanionAttack -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            if (companion.lost) return no("o animal foi perdido")
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (companion.attackId == null) return no("escolha o ataque antes de subir o nível dele")
            if (animal.abilityPoints <= 0) return no("${animal.name} não tem ponto de habilidade")
            Decision.Ok(listOf(CompanionAttackRaised(cmd.animalId, companion.attackLevel + 1)))
        }

        is LevelUpCompanion -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            if (companion.lost) return no("o animal foi perdido")
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (!animal.alive) return no("${animal.name} está morto")
            val level = animal.level + 1
            val loyalty = minOf(Companions.MAX_LOYALTY, companion.loyalty + Companions.LEVEL_UP_LOYALTY)
            Decision.Ok(listOf(CompanionLeveledUp(cmd.animalId, level, loyalty, level % Companions.ABILITY_POINT_EVERY == 0)))
        }

        is LearnCompanionTrick -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            companion.awayReason()?.let { return no(it) }
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (!animal.alive) return no("${animal.name} está morto")
            if (cmd.trickId.isBlank()) return no("truque vazio")
            if (cmd.trickId in companion.tricks) return no("${animal.name} já sabe ${cmd.trickId}")
            if (cmd.difficulty !in 1..20) return no("dificuldade fora de 1..20")
            if (cmd.roll !in 1..20) return no("o aprendizado usa um d20 (1..20)")
            if (cmd.minLoyalty !in 0..Companions.MAX_LOYALTY) return no("lealdade mínima fora de 0..100")
            if (companion.loyalty < cmd.minLoyalty) return no("lealdade insuficiente: ${companion.loyalty}/${cmd.minLoyalty}")
            if (state.day <= companion.lastLearnDay) return no("${animal.name} já tentou aprender um truque neste descanso")
            val ok = Companions.learnSuccess(cmd.roll, cmd.difficulty, companion.loyalty, companion.trainingLevel)
            Decision.Ok(listOf(CompanionTrickLearnAttempted(cmd.animalId, cmd.trickId, cmd.roll, ok, state.day)))
        }

        is OrderCompanionTrick -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            companion.awayReason()?.let { return no(it) }
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (!animal.alive || animal.downed) return no("${animal.name} não pode agir")
            if (cmd.trickId !in companion.tricks) return no("${animal.name} não sabe ${cmd.trickId}")
            if (cmd.difficulty !in 1..20) return no("dificuldade fora de 1..20")
            if (cmd.percentRoll !in 1..100) return no("a ordem usa um d100 (1..100)")
            if (state.combat != null && !cmd.combatTrick) return no("em combate só valem truques de combate")
            val chance = Companions.orderChance(Companions.trickD20Percent(cmd.difficulty, companion.trainingLevel), companion.loyalty)
            Decision.Ok(
                listOf(
                    CompanionTrickOrdered(
                        cmd.animalId,
                        cmd.trickId,
                        chance,
                        companion.loyalty >= Companions.MAX_LOYALTY || cmd.percentRoll <= chance,
                    ),
                ),
            )
        }

        is StableCompanion -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (companion.lost) return no("o animal foi perdido")
            if (companion.stabled) return no("${animal.name} já está no criadouro")
            if (!animal.alive) return no("${animal.name} está morto")
            if (state.combat != null) return no("não dá para guardar o animal durante o combate")
            Decision.Ok(listOf(CompanionStabled(cmd.animalId, Kennel.weekOf(state.day))))
        }

        is VisitCompanion -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (companion.lost) return no("o animal foi perdido")
            if (!companion.stabled) return no("${animal.name} anda com o herói; use carinho")
            if (!animal.alive) return no("${animal.name} está morto")
            if (cmd.roll !in 1..Kennel.VISIT_DIE_SIDES) return no("a visita usa um d${Kennel.VISIT_DIE_SIDES}")
            if (state.day <= companion.lastPetDay) return no("${animal.name} já recebeu visita ou carinho neste descanso")
            val now = minOf(Companions.MAX_LOYALTY, companion.loyalty + cmd.roll)
            Decision.Ok(listOf(CompanionVisited(cmd.animalId, cmd.roll, now, now - companion.loyalty, state.day, Kennel.weekOf(state.day))))
        }

        is BringCompanion -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            if (companion.lost) return no("o animal foi perdido")
            if (!companion.stabled) return no("${animal.name} já anda com o herói")
            if (!animal.alive) return no("${animal.name} está morto")
            if (state.combat != null) return no("não dá para trocar de animal durante o combate")
            val leaving = state.companions.values.filter { it.ownerId == companion.ownerId && !it.stabled && !it.lost }.map { it.entityId }.sorted()
            Decision.Ok(listOf(CompanionBrought(cmd.animalId, leaving, Kennel.weekOf(state.day))))
        }

        is RetrainCompanion -> {
            val companion = state.companions[cmd.animalId] ?: return no("não é um companheiro: ${cmd.animalId}")
            val animal = state.entities[cmd.animalId] ?: return no("animal inexistente: ${cmd.animalId}")
            val owner = state.entities[companion.ownerId] ?: return no("dono inexistente: ${companion.ownerId}")
            if (companion.abandoned) return no("${animal.name} está perdido para sempre")
            if (!companion.lost) return no("${animal.name} não foi perdido")
            if (!owner.alive) return no("${owner.name} está morto")
            if (!animal.alive) return no("${animal.name} está morto")
            if (companion.retrainAttempts >= Companions.RETRAIN_MAX_ATTEMPTS) {
                return no(
                    "as tentativas de readestrar ${animal.name} acabaram",
                )
            }
            if (state.combat != null) return no("não dá para readestrar durante o combate")
            if (cmd.baseChance !in 0..100) return no("chance base fora de 0..100")
            if (cmd.percentRoll !in 1..100) return no("o readestramento usa um d100 (1..100)")
            if (cmd.loyaltyRoll !in Companions.INITIAL_LOYALTY_MIN..Companions.INITIAL_LOYALTY_MAX) {
                return no(
                    "a lealdade inicial é um d20 (1..20)",
                )
            }
            val chance = Companions.retrainChance(cmd.baseChance)
            val success = cmd.percentRoll <= chance
            val attempt = companion.retrainAttempts + 1
            val mine = state.companions.values.filter { it.ownerId == companion.ownerId && !it.lost }
            if (success && cmd.maxAnimals != null && mine.size >= cmd.maxAnimals) {
                return no(
                    "limite de animais atingido: ${mine.size}/${cmd.maxAnimals}",
                )
            }
            val toStable = if (success) mine.filter { !it.stabled }.map { it.entityId }.sorted() else emptyList()
            Decision.Ok(
                listOf(
                    CompanionRetrained(
                        cmd.animalId, attempt, chance, cmd.percentRoll, success,
                        newLoyalty = if (success) cmd.loyaltyRoll else companion.loyalty,
                        stabledIds = toStable, week = Kennel.weekOf(state.day),
                        abandoned = !success && attempt >= Companions.RETRAIN_MAX_ATTEMPTS,
                    ),
                ),
            )
        }

        is SyncTrainerBonus -> {
            if (cmd.degree !in 0..Companions.TAMER_DEGREE_MAX) return no("grau do Treinador fora de 0..5")
            val changes =
                state.companions.values.filter { it.ownerId == cmd.ownerId }.mapNotNull { c ->
                    val alive = state.entities[c.entityId]?.alive == true
                    val wanted = if (alive && !c.lost && !c.stabled) Companions.trainerPoints(c.bodyType, cmd.degree) else emptyMap()
                    if (wanted == c.trainerBonus) null else c.entityId to wanted
                }.toMap()
            Decision.Ok(if (changes.isEmpty()) emptyList() else listOf(TrainerBonusSynced(changes)))
        }

        is SettleKennel -> {
            if (cmd.silver < 0) return no("saldo de prata negativo")
            if (cmd.breederDegree !in 0..Companions.TAMER_DEGREE_MAX) return no("grau do Criador fora de 0..5")
            val plan = Kennel.plan(state, cmd.silver, cmd.breederDegree) ?: return no("nenhuma semana do criadouro a liquidar")
            Decision.Ok(plan.events)
        }

        else -> no("comando de companheiro desconhecido")
    }
}
