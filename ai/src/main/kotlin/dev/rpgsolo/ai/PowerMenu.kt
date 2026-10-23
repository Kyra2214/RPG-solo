package dev.rpgsolo.ai

import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.PowerEffect
import dev.rpgsolo.engine.PowerResult
import dev.rpgsolo.engine.PowerSpec
import dev.rpgsolo.rules.PowerDef
import dev.rpgsolo.rules.PowerEffectKind
import dev.rpgsolo.rules.PowerTarget
import dev.rpgsolo.rules.RulePackage

sealed interface PowerBuild {
    data class Ready(val def: PowerDef, val spec: PowerSpec) : PowerBuild

    data class Invalid(val reason: String) : PowerBuild
}

/** Poder usado na jogada a pedido da IA, com o resultado já resolvido pelo motor. */
data class PowerUse(
    val powerName: String,
    val userName: String,
    val targetName: String?,
    val spec: PowerSpec,
    val result: PowerResult.Activated,
)

/** Poderes que o mestre pode pedir nesta jogada; [traitIds] lê os traços atuais do personagem do jogador. */
class PowerContext(val menu: PowerMenu, val traitIds: () -> List<String>)

/** Executor do ataque pedido pela IA ([execute] recebe o id do alvo e devolve o motivo da recusa, ou null se resolveu). */
class AttackContext(val execute: (targetId: String) -> String?)

/** Executor da missão pedida pela IA ([execute] recebe o pedido e devolve o motivo da recusa, ou null se registrou). */
class QuestContext(val execute: (QuestRequest) -> String?)

/**
 * Traduz um [PowerDef] do pacote em [PowerSpec] para o motor. Custo, recurso, dado, condição e teste vêm só do pacote;
 * a IA e a interface escolhem apenas qual poder usar e o alvo.
 * ASSUMINDO: o dano de poder soma o atributo de bônus do perfil e os bônus de traço do escopo `dano`; equipamento não entra.
 */
class PowerMenu(
    private val pkg: RulePackage,
    /** MU-03.3: exceções mecânicas declaradas; imunidade a condição vale também para poderes. */
    private val exceptions: dev.rpgsolo.engine.ExceptionRegistry? = null,
) {
    /** Poderes cujo traço exigido está na ficha. */
    fun available(traitIds: Collection<String>): List<PowerDef> = pkg.data.powers.filter { it.traitId in traitIds }

    /** Texto para o contexto do mestre: poderes que ele pode pedir por request_power. null se não há nenhum. */
    fun describe(
        traitIds: Collection<String>,
        learnedSpells: Collection<String> = emptyList(),
    ): String? {
        // MU-07.6: magias aprendidas com efeito de conjuração entram como "magia:<id>".
        val entries =
            available(traitIds).map { Triple(it.id, it.name, it) } +
                learnedSpells.sorted().mapNotNull { spellId ->
                    val def =
                        pkg.spellPowerId(spellId)?.let {
                                pid ->
                            pkg.data.powers.firstOrNull { it.id == pid }
                        } ?: return@mapNotNull null
                    Triple("magia:$spellId", pkg.spell(spellId)?.name ?: spellId, def)
                }
        if (entries.isEmpty()) return null
        return buildString {
            append("Poderes do personagem (request_power.power = id; só quando o jogador declarar o uso):")
            entries.forEach { (id, name, def) ->
                val resource = pkg.data.resources.firstOrNull { it.id == def.resource }?.name ?: def.resource
                val target = if (def.target == PowerTarget.ENEMY) "alvo: adversário (request_power.target = id do NPC)" else "em si mesmo"
                append("\n- ").append(id).append(": ").append(name)
                    .append(" (custo ").append(def.cost).append(' ').append(resource).append("; ").append(target).append(')')
            }
        }
    }

    /**
     * MU-07.4: conjura uma magia aprendida pelo efeito do poder ligado em `magia_<id>_poder`. Custo, teste e efeito
     * são os do poder; o traço do poder não é exigido (a magia aprendida o substitui). Sem slot extra: o slot já foi
     * gasto ao aprender (MU-07.3). Jogador, NPC e aliado usam o mesmo caminho.
     */
    fun buildSpell(
        spellId: String,
        userId: String,
        targetId: String?,
        state: GameState,
        traitIds: Collection<String>,
        targetTraitIds: Collection<String> = emptyList(),
    ): PowerBuild {
        val user = state.entities[userId] ?: return PowerBuild.Invalid("usuário inexistente: $userId")
        if (spellId !in user.spells) return PowerBuild.Invalid("a magia $spellId não foi aprendida")
        val powerId = pkg.spellPowerId(spellId) ?: return PowerBuild.Invalid("a magia $spellId não tem efeito de conjuração")
        val def = pkg.data.powers.firstOrNull { it.id == powerId } ?: return PowerBuild.Invalid("poder inexistente: $powerId")
        return build(powerId, userId, targetId, state, traitIds + def.traitId, targetTraitIds)
    }

    fun build(
        powerId: String,
        userId: String,
        targetId: String?,
        state: GameState,
        traitIds: Collection<String>,
        targetTraitIds: Collection<String> = emptyList(),
    ): PowerBuild {
        val def =
            pkg.data.powers.firstOrNull { it.id == powerId }
                ?: return PowerBuild.Invalid("poder inexistente: $powerId")
        // Defesa adicional no ponto de execução: pacotes normalmente chegam validados,
        // mas um PowerMenu não deve converter uma definição incoerente em efeito de motor.
        val expectedTarget =
            when (def.effect) {
                PowerEffectKind.HEAL, PowerEffectKind.REMOVE_CONDITION, PowerEffectKind.SHIELD -> PowerTarget.SELF
                PowerEffectKind.DAMAGE, PowerEffectKind.APPLY_CONDITION -> PowerTarget.ENEMY
            }
        if (def.target != expectedTarget) {
            return PowerBuild.Invalid(
                "poder ${def.name} inválido: ${def.effect.name} exige alvo ${expectedTarget.name}",
            )
        }
        if (def.traitId !in traitIds) return PowerBuild.Invalid("o poder ${def.name} exige o traço ${def.traitId}")
        val user = state.entities[userId] ?: return PowerBuild.Invalid("usuário inexistente: $userId")
        if (!user.alive) return PowerBuild.Invalid("${user.name} está fora de combate")
        val resolvedTarget: String? =
            when (def.target) {
                PowerTarget.SELF -> null
                PowerTarget.ENEMY -> {
                    val id = targetId ?: return PowerBuild.Invalid("escolha um adversário para ${def.name}")
                    val target =
                        state.entities[id]?.takeIf { it.kind == EntityKind.NPC && it.alive }
                            ?: return PowerBuild.Invalid("o alvo não é um NPC vivo")
                    target.id
                }
            }
        val health = pkg.manifest.deathResource
        val effect: PowerEffect =
            when (def.effect) {
                PowerEffectKind.HEAL -> {
                    val resource = health ?: return PowerBuild.Invalid("o pacote não define recurso de vida")
                    val amount = def.param.toIntOrNull()?.takeIf { it > 0 } ?: return PowerBuild.Invalid("quantidade de cura inválida")
                    PowerEffect.Cure(resource, amount)
                }
                PowerEffectKind.DAMAGE -> {
                    val resource = health ?: return PowerBuild.Invalid("o pacote não define recurso de vida")
                    val profile =
                        pkg.data.damage.firstOrNull { it.id == def.param }
                            ?: return PowerBuild.Invalid("perfil de dano inexistente: ${def.param}")
                    PowerEffect.Strike(
                        resource = resource,
                        diceCount = profile.diceCount,
                        diceSides = profile.diceSides,
                        bonus = (profile.bonusStat?.let { user.stats[it] } ?: 0) + pkg.traitBonus(traitIds, "dano"),
                        reduction = pkg.traitBonus(targetTraitIds, "resistencia_dano"),
                    )
                }
                PowerEffectKind.APPLY_CONDITION -> {
                    val c =
                        pkg.data.conditions.firstOrNull { it.id == def.param }
                            ?: return PowerBuild.Invalid("condição inexistente: ${def.param}")
                    val victimTraits = if (resolvedTarget == null) traitIds else targetTraitIds
                    if (pkg.isImmuneToCondition(victimTraits, c.id) || exceptions?.isImmune(resolvedTarget ?: userId, c.id) == true) {
                        return PowerBuild.Invalid("o alvo é imune a ${c.name}")
                    }
                    PowerEffect.Inflict(c.id, c.durationRounds, c.damagePerRound, c.damageResource)
                }
                PowerEffectKind.REMOVE_CONDITION -> PowerEffect.Cleanse(def.param)
                PowerEffectKind.SHIELD -> {
                    val amount = def.param.toIntOrNull()?.takeIf { it > 0 } ?: return PowerBuild.Invalid("quantidade de escudo inválida")
                    PowerEffect.Shield(amount)
                }
            }
        val check =
            def.checkId?.let { id ->
                val difficulty = pkg.defaultDifficulty() ?: return PowerBuild.Invalid("o pacote não define dificuldades para o teste do poder")
                val base = pkg.check(id, user.stats, difficulty, user.skills)
                pkg.adjustCheck(base, state.conditions[userId].orEmpty(), traitIds)
            }
        val spec =
            try {
                PowerSpec(def.id, userId, resolvedTarget, def.resource, def.cost, effect, check)
            } catch (e: IllegalArgumentException) {
                return PowerBuild.Invalid(e.message ?: "poder inválido")
            }
        return PowerBuild.Ready(def, spec)
    }
}
