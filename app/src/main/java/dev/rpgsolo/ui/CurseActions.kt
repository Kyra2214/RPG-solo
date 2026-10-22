package dev.rpgsolo.ui

import dev.rpgsolo.engine.ApplyCurse
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.RemoveCurse
import dev.rpgsolo.engine.isSuccess
import dev.rpgsolo.rules.CurseCatalogLoader
import dev.rpgsolo.rules.CurseDefinition
import dev.rpgsolo.rules.RulePackage

data class AppliedCurseView(
    val curse: CurseDefinition,
    val sourceId: String,
    val remainingRounds: Int?
)

/** Aplicação e remoção de maldições durante a aventura, separadas da Classe de Malditos. */
class CurseActions(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val playerId: () -> String,
    private val checkBonuses: (entityId: String, scopes: Set<String>) -> List<dev.rpgsolo.rules.ExternalBonus>,
    private val onChanged: () -> Unit
) {
    private val catalog: List<CurseDefinition> by lazy {
        val stream = CurseActions::class.java.classLoader
            ?.getResourceAsStream("packages/sistema-proprio/maldicoes-lancadas.txt")
            ?: error("catálogo de maldições lançadas não encontrado")
        val raw = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val parsed = CurseCatalogLoader.load(raw)
        require(parsed.valid) { parsed.errors.joinToString("; ") }
        parsed.curses
    }

    fun all(): List<CurseDefinition> = catalog
    fun defaultTargetId(): String = playerId()
    fun defaultSourceId(): String = "personagem:" + playerId()

    fun sources(): List<Pair<String, String>> {
        val state = session().state
        val combatOrder = state.combat?.order
        return state.entities.values.filter { it.alive && !it.downed && (combatOrder == null || it.id in combatOrder) }
            .map { it.id to (if (it.id == playerId()) "Você" else it.name) }
            .sortedBy { it.second }
    }

    fun targets(): List<Pair<String, String>> {
        val state = session().state
        val combatOrder = state.combat?.order
        return state.entities.values.filter { it.alive && (combatOrder == null || it.id in combatOrder) }
            .map { it.id to (if (it.id == playerId()) "Você" else it.name) }
            .sortedBy { it.second }
    }

    fun applied(targetId: String): List<AppliedCurseView> {
        val entity = session().state.entities[targetId] ?: return emptyList()
        val definitions = catalog.associateBy { it.id }
        return entity.appliedCurses.values.mapNotNull { applied ->
            definitions[applied.curseId]?.let { AppliedCurseView(it, applied.sourceId, applied.remainingRounds) }
        }.sortedBy { it.curse.name }
    }

    /**
     * If a curse declares resistance, caller must provide the result of the package's real
     * 'resistir' test. true means resisted and no curse is applied; null is rejected.
     */
    fun apply(targetId: String, curseId: String, sourceId: String): String? = try {
        val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
        session().atomically {
            val curse = catalog.firstOrNull { it.id == curseId } ?: throw IllegalArgumentException("maldição não existe: $curseId")
            val target = session().state.entities[targetId] ?: throw IllegalArgumentException("alvo inexistente: $targetId")
            require(target.alive) { "não é possível amaldiçoar uma entidade morta" }
            require(sourceId.isNotBlank()) { "a maldição precisa identificar sua origem" }
            require(curse.id !in target.appliedCurses) { "esta maldição já está aplicada" }
            val combat = session().state.combat
            if (combat == null) require(curse.durationRounds == null) { "maldições com duração em rodadas só podem ser lançadas durante combate" }
            val sourceEntityId = when {
                sourceId.startsWith("personagem:") -> sourceId.removePrefix("personagem:")
                sourceId.startsWith("criatura:") -> sourceId.removePrefix("criatura:")
                else -> null
            }
            if (sourceEntityId != null) require(sourceEntityId in session().state.entities) { "a origem da maldição não existe" }
            if (combat != null) {
                require(targetId in combat.order) { "o alvo está fora do combate" }
                require(sourceEntityId != null && sourceEntityId in session().state.entities) {
                    "durante o combate, a origem da maldição precisa ser um personagem na iniciativa"
                }
                require(combat.current == sourceEntityId) { "não é o turno de quem lança a maldição" }
            }
            var resistanceSummary = ""
            var resistanceSucceeded: Boolean? = null
            if (curse.resistanceCheckId != null) {
                val difficulty = requireNotNull(curse.resistanceDifficulty)
                val base = pkg.check(curse.resistanceCheckId, target.stats, difficulty, target.skills)
                val adjusted = pkg.adjustCheck(
                    base,
                    conditions = session().state.conditions[targetId].orEmpty(),
                    traits = target.traits,
                    skill = curse.resistanceCheckId,
                    additionalBonuses = checkBonuses(targetId, setOf(curse.resistanceCheckId, base.stat ?: "").filter { it.isNotBlank() }.toSet())
                )
                val result = session().check(adjusted)
                resistanceSucceeded = result.outcome.isSuccess
                resistanceSummary = "Teste ${result.spec.label}: ${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}; dificuldade ${result.spec.difficulty}. "
                if (resistanceSucceeded == true) {
                    if (combat != null) {
                        when (val advance = session().execute(dev.rpgsolo.engine.NextTurn)) {
                            is dev.rpgsolo.engine.ExecResult.Accepted -> { onChanged(); Unit }
                            is dev.rpgsolo.engine.ExecResult.Rejected -> return@atomically advance.reason
                        }
                    }
                    return@atomically "O alvo resistiu à maldição. $resistanceSummary"
                }
            }
            val commands = buildList<dev.rpgsolo.engine.Command> {
                add(ApplyCurse(
                    targetId = targetId,
                    curseId = curse.id,
                    sourceId = sourceId,
                    durationRounds = curse.durationRounds,
                    requiresResistance = curse.resistanceCheckId != null,
                    resistanceSucceeded = resistanceSucceeded
                ))
                if (combat != null) add(dev.rpgsolo.engine.NextTurn)
            }
            when (val result = session().executeAll(commands)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    onChanged()
                    "$resistanceSummary Maldição '${curse.name}' aplicada."
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }

    fun remove(targetId: String, curseId: String, method: String): String? = try {
        val curse = catalog.firstOrNull { it.id == curseId } ?: throw IllegalArgumentException("maldição não existe: $curseId")
        require(session().state.combat == null) { "a remoção por ritual, item ou missão precisa ocorrer fora de combate" }
        require(method in curse.removalMethods) { "método de remoção não é válido para esta maldição" }
        when (val result = session().execute(RemoveCurse(targetId, curseId, method))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { onChanged(); null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }
}
