package dev.rpgsolo.ui

import dev.rpgsolo.engine.ApplyCurse
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.RemoveCurse
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

    fun targets(): List<Pair<String, String>> = session().state.entities.values
        .filter { it.alive }
        .map { it.id to (if (it.id == playerId()) "Você" else it.name) }
        .sortedBy { it.second }

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
            var resistanceSummary = ""
            var resistanceSucceeded: Boolean? = null
            if (curse.resistanceCheckId != null) {
                val difficulty = requireNotNull(curse.resistanceDifficulty)
                val base = pkg.check(curse.resistanceCheckId, target.stats, difficulty, target.skills)
                val adjusted = base.copy(
                    modifier = base.modifier + pkg.traitBonus(target.traits, curse.resistanceCheckId) +
                        (base.stat?.let { pkg.traitBonus(target.traits, it) } ?: 0)
                )
                val result = session().check(adjusted)
                resistanceSucceeded = result.outcome.isSuccess
                resistanceSummary = "Teste ${result.spec.label}: ${result.roll.expression} [${result.roll.dice.joinToString(", ")}] = ${result.roll.total}; dificuldade ${result.spec.difficulty}. "
                if (resistanceSucceeded == true) {
                    return@atomically "O alvo resistiu à maldição. $resistanceSummary"
                }
            }
            when (val result = session().execute(
                ApplyCurse(
                    targetId = targetId,
                    curseId = curse.id,
                    sourceId = sourceId,
                    durationRounds = curse.durationRounds,
                    requiresResistance = curse.resistanceCheckId != null,
                    resistanceSucceeded = resistanceSucceeded
                )
            )) {
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
        require(method in curse.removalMethods) { "método de remoção não é válido para esta maldição" }
        when (val result = session().execute(RemoveCurse(targetId, curseId, method))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> { onChanged(); null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    } catch (e: IllegalArgumentException) { e.message }
      catch (e: IllegalStateException) { e.message }
}
