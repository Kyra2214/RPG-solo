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
    fun apply(
        targetId: String,
        curseId: String,
        sourceId: String,
        resistanceSucceeded: Boolean? = null
    ): String? = try {
        val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
        val curse = catalog.firstOrNull { it.id == curseId } ?: throw IllegalArgumentException("maldição não existe: $curseId")
        val target = session().state.entities[targetId] ?: throw IllegalArgumentException("alvo inexistente: $targetId")
        require(pkg.checkChoices(target.stats, skills = target.skills).any { it.id == curse.resistanceCheckId } || curse.resistanceCheckId == null) {
            "o pacote ativo não define o teste de resistência '\${curse.resistanceCheckId}'"
        }
        if (curse.resistanceCheckId != null && resistanceSucceeded == null) {
            return "resolva o teste '\${curse.resistanceCheckId}' contra dificuldade \${curse.resistanceDifficulty} antes de aplicar a maldição"
        }
        if (resistanceSucceeded == true) return "o alvo resistiu à maldição"
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
            is dev.rpgsolo.engine.ExecResult.Accepted -> { onChanged(); null }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
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
