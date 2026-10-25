package dev.rpgsolo.ui

import dev.rpgsolo.ai.KennelSettlement
import dev.rpgsolo.engine.AddItem
import dev.rpgsolo.engine.AdvanceDays
import dev.rpgsolo.engine.AllocateStatPoints
import dev.rpgsolo.engine.BringCompanion
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.ChooseCompanionAttack
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.CompanionRetrained
import dev.rpgsolo.engine.CompanionState
import dev.rpgsolo.engine.Companions
import dev.rpgsolo.engine.ConsumeItem
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.FeedCompanion
import dev.rpgsolo.engine.Kennel
import dev.rpgsolo.engine.LearnCompanionTrick
import dev.rpgsolo.engine.OrderCompanionTrick
import dev.rpgsolo.engine.PetCompanion
import dev.rpgsolo.engine.RaiseCompanionAttack
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.RetrainCompanion
import dev.rpgsolo.engine.SettleKennel
import dev.rpgsolo.engine.SyncTrainerBonus
import dev.rpgsolo.engine.TameCompanion
import dev.rpgsolo.engine.VisitCompanion
import dev.rpgsolo.engine.allyIdsInCombat
import dev.rpgsolo.engine.companionStrike
import dev.rpgsolo.rules.CompanionEffectKind
import dev.rpgsolo.rules.RulePackage

/** N-32.4: tela do companheiro adestrado; números vêm do motor e do pacote, nunca da IA. */
data class CompanionView(
    val id: String,
    val name: String,
    val bodyName: String,
    val bodyType: String,
    val level: Int,
    val hp: Int,
    val hpMax: Int,
    val loyalty: Int,
    val trainingLevel: Int,
    val stats: Map<String, Int>,
    val freeStatPoints: Int,
    val attackId: String?,
    val attackLine: String,
    val attackOptions: List<Pair<String, String>>,
    val abilityPoints: Int,
    val knownTricks: List<TrickView>,
    val learnableTricks: List<TrickView>,
    val foods: List<Pair<String, String>>,
    val canPet: Boolean,
    val isTurn: Boolean,
    val enemies: List<Pair<String, String>>,
    /** N-33.2c: guardado no criadouro (só visitar ou trazer; sem painel de combate). */
    val stabled: Boolean = false,
    /** Taxa em prata para trazer (só quando guardado). */
    val bringFee: Int = Kennel.BRING_FEE,
    val canVisit: Boolean = false,
)

data class TrickView(val id: String, val name: String, val description: String, val chancePercent: Int, val combat: Boolean)

/** N-33.2d: marca de adestramento que paga uma tentativa de readestrar; [chance] já tem a penalidade de 30 pontos. */
data class RetrainOption(val itemId: String, val itemName: String, val chance: Int)

/** N-33.2d: animal perdido (lealdade 0) que ainda pode ser readestrado; [attemptsLeft] vai de 2 a 1. */
data class LostCompanionView(val id: String, val name: String, val level: Int, val attemptsLeft: Int, val options: List<RetrainOption>)

/** Marcas de adestramento (itens de resultado do Adestrador) que ainda podem virar um companheiro. */
data class TameOption(val itemId: String, val itemName: String, val hp: Int)

class CompanionActions(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val playerId: () -> String,
    private val onChanged: () -> Unit,
) {
    private companion object {
        val STATS = listOf("ataque" to "Ataque", "resistencia" to "Resistência", "velocidade" to "Velocidade", "destreza" to "Destreza")

        /** ASSUMINDO: PV inicial por porte do item de resultado do Adestrador. */
        val TAME_ITEMS =
            mapOf(
                "animal-pequeno-adestrado" to 10,
                "animal-medio-adestrado" to 20,
                "animal-grande-adestrado" to 30,
                "fera-adestrada" to 45,
                "criatura-colossal-adestrada" to 60,
            )
    }

    private fun tamerDegree(): Int {
        val hero = session().state.entities[playerId()] ?: return 1
        return (1..5).lastOrNull { (hero.skillTreeLevels["profissao-adestrador-$it"] ?: 0) > 0 } ?: 1
    }

    /** N-33.3: maior grau aprendido de Criador de Animais (0 se nenhum). */
    fun breederDegree(): Int {
        val hero = session().state.entities[playerId()] ?: return 0
        return (1..5).lastOrNull { (hero.skillTreeLevels["profissao-criador-de-animais-$it"] ?: 0) > 0 } ?: 0
    }

    /** N-33.3: maior grau aprendido de Treinador (0 se nenhum). */
    fun trainerDegree(): Int {
        val hero = session().state.entities[playerId()] ?: return 0
        return (1..5).lastOrNull { (hero.skillTreeLevels["profissao-treinador-$it"] ?: 0) > 0 } ?: 0
    }

    private fun healthResource(pkg: RulePackage): String = pkg.data.manifest.deathResource ?: "pv"

    fun tameOptions(): List<TameOption> {
        val pkg = activePackage() ?: return emptyList()
        if (pkg.companions.isEmpty) return emptyList()
        val inv = session().state.inventory[playerId()].orEmpty()
        return TAME_ITEMS.filterKeys { (inv[it] ?: 0) > 0 }.map { (id, hp) -> TameOption(id, pkg.item(id)?.name ?: id, hp) }
    }

    fun views(): List<CompanionView> {
        val pkg = activePackage() ?: return emptyList()
        val state = session().state
        val owner = playerId()
        val hpId = healthResource(pkg)
        val inv = state.inventory[owner].orEmpty()
        return state.companions.values.filter { it.ownerId == owner }.mapNotNull { c ->
            if (c.lost) return@mapNotNull null
            val animal = state.entities[c.entityId]?.takeIf { it.alive } ?: return@mapNotNull null
            val attack = c.attackId?.let { pkg.companionAttack(it) }
            val attackStat = animal.stats["ataque"] ?: 0
            val attackLine =
                attack?.let {
                    val damage = Companions.attackDamage(attackStat, it.damage.percent)
                    val effect =
                        when (it.effectKind) {
                            CompanionEffectKind.CONDITION ->
                                " · ${pkg.data.conditions.firstOrNull {
                                        cd ->
                                    cd.id == it.effectRef
                                }?.name ?: it.effectRef} (${Companions.effectChance(it.baseChance, c.attackLevel)}%)"
                            CompanionEffectKind.ELEMENT -> " · elemento ${it.effectRef}"
                            CompanionEffectKind.RANGED -> " · à distância"
                            CompanionEffectKind.NONE -> ""
                        }
                    "${it.name} nv ${c.attackLevel} · dano $damage$effect"
                }.orEmpty()

            fun trick(
                t: dev.rpgsolo.rules.CompanionTrickDef,
                learnChance: Boolean,
            ) = TrickView(
                t.id,
                t.name,
                t.description,
                if (learnChance) {
                    5 * (1..20).count { Companions.learnSuccess(it, t.difficulty, c.loyalty, c.trainingLevel) }
                } else {
                    Companions.orderChance(Companions.trickD20Percent(t.difficulty, c.trainingLevel), c.loyalty)
                },
                t.combat,
            )
            val known = pkg.companions.tricks.filter { it.id in c.tricks }.map { trick(it, false) }
            val learnable =
                pkg.companions.tricks.filter { it.id !in c.tricks && c.loyalty >= it.minLoyalty }.map { trick(it, true) }
            val foods =
                inv.filter { (id, q) -> q > 0 && pkg.companionFood(c.bodyType, id) != null }
                    .map { (id, _) -> id to (pkg.item(id)?.name ?: id) }
            val isTurn = state.combat?.current == c.entityId
            val enemies =
                state.combat?.order.orEmpty().filter { id ->
                    val e = state.entities[id]
                    e != null && e.alive && id != c.entityId && id != owner && id !in state.allyIdsInCombat(c.entityId)
                }.map { it to (state.entities[it]?.name ?: it) }
            CompanionView(
                c.entityId, animal.name, pkg.companions.bodies.firstOrNull { it.id == c.bodyType }?.name ?: c.bodyType, c.bodyType,
                animal.level, animal.resources[hpId]?.current ?: 0, animal.resources[hpId]?.max ?: 0, c.loyalty, c.trainingLevel,
                STATS.associate { (id, _) -> id to (animal.stats[id] ?: 0) }, animal.statPoints, c.attackId, attackLine,
                if (c.attackId == null) pkg.companionAttacks(c.bodyType).map { it.id to it.name } else emptyList(),
                animal.abilityPoints, known, learnable, foods, !c.stabled && state.day > c.lastPetDay && c.loyalty < Companions.MAX_LOYALTY, isTurn, enemies,
                stabled = c.stabled, canVisit = c.stabled && state.day > c.lastPetDay,
            )
        }
    }

    /**
     * N-33.2d: chance base (%) da receita que gera a marca [itemId] para o herói; null se o pacote não tem a receita ou o herói
     * ainda não pode fazê-la. ASSUMINDO: qualquer marca de adestramento do inventário serve ao readestramento.
     */
    private fun tameBaseChance(itemId: String): Int? {
        val pkg = activePackage() ?: return null
        val hero = session().state.entities[playerId()] ?: return null
        val recipe = pkg.data.recipes.firstOrNull { itemId in it.outputs } ?: return null
        return pkg.craftChance(recipe, hero)
    }

    fun lostViews(): List<LostCompanionView> {
        val pkg = activePackage() ?: return emptyList()
        if (pkg.companions.isEmpty) return emptyList()
        val state = session().state
        val owner = playerId()
        val inv = state.inventory[owner].orEmpty()
        return state.companions.values.filter { it.ownerId == owner && it.lost && !it.abandoned }.mapNotNull { c ->
            val animal = state.entities[c.entityId]?.takeIf { it.alive } ?: return@mapNotNull null
            val options =
                TAME_ITEMS.keys.filter { (inv[it] ?: 0) > 0 }.mapNotNull { id ->
                    tameBaseChance(id)?.let { RetrainOption(id, pkg.item(id)?.name ?: id, Companions.retrainChance(it)) }
                }
            LostCompanionView(c.entityId, animal.name, animal.level, Companions.RETRAIN_MAX_ATTEMPTS - c.retrainAttempts, options)
        }
    }

    /**
     * N-33.2d: tenta adestrar de novo o animal perdido gastando uma marca de adestramento (a tentativa consome o item mesmo na falha).
     * Devolve o resultado em texto (a interface mostra a mensagem); recusas voltam como texto também.
     */
    fun retrain(
        animalId: String,
        itemId: String,
    ): String? {
        val state = session().state
        val c = state.companions[animalId] ?: return "companheiro inexistente"
        if (!c.lost || c.abandoned) return "o animal não pode ser readestrado"
        if (itemId !in TAME_ITEMS) return "item sem marca de adestramento: $itemId"
        val base = tameBaseChance(itemId) ?: return "você ainda não sabe adestrar esse porte de animal"
        val percentRoll = session().rollPlain("readestrar:$animalId", 100)
        val loyaltyRoll = session().rollPlain("readestrar-lealdade:$animalId", 20)
        val refusal =
            run(ConsumeItem(playerId(), itemId, 1), RetrainCompanion(animalId, base, percentRoll, loyaltyRoll, maxAnimals = tamerDegree()))
        if (refusal != null) return refusal
        val e = session().events.filterIsInstance<CompanionRetrained>().lastOrNull { it.animalId == animalId } ?: return null
        val name = session().state.entities[animalId]?.name ?: animalId
        return when {
            e.success -> "$name aceitou o herói de novo (d100 ${e.roll} contra ${e.chance}%), com lealdade ${e.newLoyalty}."
            e.abandoned -> "$name recusou de novo (d100 ${e.roll} contra ${e.chance}%) e está perdido para sempre."
            else -> "$name recusou (d100 ${e.roll} contra ${e.chance}%). Resta mais uma tentativa."
        }
    }

    fun statLabel(id: String): String = STATS.firstOrNull { it.first == id }?.second ?: id

    /** Todo lote do painel termina com o ajuste dos pontos do Treinador (N-33.3): o bônus segue o animal que anda com o herói. */
    private fun run(vararg commands: Command): String? =
        when (val r = session().executeAll(commands.toList() + SyncTrainerBonus(playerId(), trainerDegree()))) {
            is ExecResult.Accepted -> {
                onChanged()
                null
            }
            is ExecResult.Rejected -> r.reason
        }

    /** Adestra um animal a partir da marca [itemId]; a lealdade inicial é um d20 do motor. ASSUMINDO: a captura já foi resolvida pela receita. */
    fun tame(
        itemId: String,
        name: String,
        bodyType: String,
    ): String? {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        val hp = TAME_ITEMS[itemId] ?: return "item sem marca de adestramento: $itemId"
        if (pkg.companions.bodies.none { it.id == bodyType }) return "tipo de corpo inexistente: $bodyType"
        val clean = name.trim()
        if (clean.isEmpty()) return "dê um nome ao animal"
        val animalId =
            "pet-" + (
                clean.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty {
                    "animal"
                }
            ) + "-" + (session().state.companions.size + 1)
        val roll = session().rollPlain("adestrar:$animalId", 20)
        val resource = healthResource(pkg)
        val animal =
            Entity(
                animalId,
                clean,
                EntityKind.NPC,
                stats = STATS.associate { it.first to 0 },
                resources = mapOf(resource to Resource(hp, hp)),
                statPoints = 3,
            )
        return run(ConsumeItem(playerId(), itemId, 1), TameCompanion(playerId(), animal, bodyType, roll, maxAnimals = tamerDegree()))
    }

    fun chooseAttack(
        animalId: String,
        attackId: String,
    ): String? {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        val c = session().state.companions[animalId] ?: return "companheiro inexistente"
        if (pkg.companionAttacks(c.bodyType).none { it.id == attackId }) return "ataque não pertence ao tipo de corpo"
        return run(ChooseCompanionAttack(animalId, attackId))
    }

    fun allocate(
        animalId: String,
        stat: String,
    ): String? = run(AllocateStatPoints(animalId, mapOf(stat to 1)))

    fun raiseAttack(animalId: String): String? = run(RaiseCompanionAttack(animalId))

    fun learnTrick(
        animalId: String,
        trickId: String,
    ): String? {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        val t = pkg.companionTrick(trickId) ?: return "truque inexistente: $trickId"
        val before = session().state.companions[animalId]?.tricks.orEmpty()
        val roll = session().rollPlain("truque-aprender:$animalId:$trickId", 20)
        val err = run(LearnCompanionTrick(animalId, trickId, t.difficulty, t.minLoyalty, roll))
        if (err != null) return err
        val learned = trickId in session().state.companions.getValue(animalId).tricks && trickId !in before
        return if (learned) "${t.name}: aprendeu (d20 $roll)" else "${t.name}: não aprendeu desta vez (d20 $roll)"
    }

    fun orderTrick(
        animalId: String,
        trickId: String,
    ): String? {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        val t = pkg.companionTrick(trickId) ?: return "truque inexistente: $trickId"
        val roll = session().rollPlain("truque-ordem:$animalId:$trickId", 100)
        val err = run(OrderCompanionTrick(animalId, trickId, t.difficulty, roll, t.combat))
        if (err != null) return err
        val chance =
            Companions.orderChance(
                Companions.trickD20Percent(t.difficulty, session().state.companions.getValue(animalId).trainingLevel),
                session().state.companions.getValue(animalId).loyalty,
            )
        val ok = session().state.companions.getValue(animalId).loyalty >= Companions.MAX_LOYALTY || roll <= chance
        return if (ok) "${t.name}: obedeceu (d100 $roll, chance $chance%)" else "${t.name}: ignorou a ordem (d100 $roll, chance $chance%)"
    }

    /** Alimenta com [itemId] do inventário: ganho base da comida (dobra se preferida) e grau do Adestrador. */
    fun feed(
        animalId: String,
        itemId: String,
    ): String? {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        val c = session().state.companions[animalId] ?: return "companheiro inexistente"
        val food = pkg.companionFood(c.bodyType, itemId) ?: return "o animal não aceita este alimento"
        val gain = minOf(Companions.MAX_BASE_GAIN, food.baseGain * if (food.preferred) 2 else 1)
        return run(ConsumeItem(playerId(), itemId, 1), FeedCompanion(animalId, gain, tamerDegree()))
    }

    /** N-33.2c: saldo do herói em prata (ouro convertido pela razão do pacote). */
    fun kennelBudget(): Int {
        val pkg = activePackage() ?: return 0
        val wallet = session().state.inventory[playerId()] ?: return 0
        val silver = pkg.currencyItemId?.let { wallet[it] } ?: 0
        val gold = pkg.goldItemId?.let { wallet[it] } ?: 0
        return silver + gold * pkg.silverPerGold
    }

    /** N-33.2c: comandos que consomem [price] em prata/ouro do herói; null se o saldo não cobre. */
    fun payCommands(price: Int): List<Command>? {
        if (price == 0) return emptyList()
        val pkg = activePackage() ?: return null
        val currencyId = pkg.currencyItemId ?: return null
        val wallet = session().state.inventory[playerId()]
        val payment = pkg.planPayment(wallet?.get(currencyId) ?: 0, pkg.goldItemId?.let { wallet?.get(it) } ?: 0, price) ?: return null
        return buildList {
            if (payment.silver > 0) add(ConsumeItem(playerId(), currencyId, payment.silver))
            if (payment.gold > 0) add(ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
            if (payment.change > 0) add(AddItem(playerId(), currencyId, payment.change))
        }
    }

    /** Visita ao animal guardado (conta como carinho): 1d6 de lealdade, rolado pelo motor. */
    fun visit(animalId: String): String? {
        val roll = session().rollPlain("visita:$animalId", Kennel.VISIT_DIE_SIDES)
        return run(VisitCompanion(animalId, roll))
    }

    /**
     * Traz o animal guardado e guarda o que andava com o herói: 10 prata, 1 dia (a semana pode virar e cobrar o criadouro).
     * ASSUMINDO: sem detecção de cidade; é o jogador quem só usa o botão numa cidade.
     */
    fun bring(animalId: String): String? {
        val state = session().state
        val c = state.companions[animalId] ?: return "companheiro inexistente"
        if (!c.stabled) return "o animal já anda com o herói"
        val budget = kennelBudget()
        if (budget < Kennel.BRING_FEE) return "faltam ${Kennel.BRING_FEE - budget} de prata para trazer o animal"
        val settle =
            KennelSettlement.after(
                state,
                Kennel.BRING_DAYS,
                budget,
                reserved = Kennel.BRING_FEE,
                breederDegree = breederDegree(),
                pay = ::payCommands,
            )
        // o pagamento vem primeiro, depois o dia, a liquidação (se uma semana virou) e a troca
        val pay = settle?.commands?.filter { it !is SettleKennel } ?: payCommands(Kennel.BRING_FEE) ?: return "saldo insuficiente"
        val settleCmd = settle?.commands?.filterIsInstance<SettleKennel>().orEmpty()
        return run(*(pay + AdvanceDays(Kennel.BRING_DAYS) + settleCmd + BringCompanion(animalId)).toTypedArray())
    }

    fun pet(animalId: String): String? = run(PetCompanion(animalId, tamerDegree()))

    /** Turno do animal: ataca [targetId] com o ataque escolhido (dano e chance de efeito do motor/pacote). */
    fun strike(
        animalId: String,
        targetId: String,
    ): String? {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        val c: CompanionState = session().state.companions[animalId] ?: return "companheiro inexistente"
        val animal = session().state.entities[animalId] ?: return "animal inexistente"
        val attack = c.attackId?.let { pkg.companionAttack(it) } ?: return "escolha o ataque do animal antes"
        val damage = Companions.attackDamage(animal.stats["ataque"] ?: 0, attack.damage.percent)
        val withCondition = attack.effectKind == CompanionEffectKind.CONDITION
        val chance = if (withCondition) Companions.effectChance(attack.baseChance, c.attackLevel) else 0
        val effectRoll = if (withCondition) session().rollPlain("efeito:$animalId", 100) else 100
        val sess = session()
        return when (
            val r =
                sess.atomically {
                    sess.engine.companionStrike(
                        animalId,
                        targetId,
                        healthResource(pkg),
                        damage,
                        if (withCondition) attack.effectRef else null,
                        null,
                        chance,
                        effectRoll,
                    ).also { if (it is ExecResult.Accepted) sess.save() }
                }
        ) {
            is ExecResult.Accepted -> {
                onChanged()
                null
            }
            is ExecResult.Rejected -> r.reason
        }
    }
}
