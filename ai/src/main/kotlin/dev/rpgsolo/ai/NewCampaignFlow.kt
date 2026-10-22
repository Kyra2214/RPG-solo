package dev.rpgsolo.ai

import dev.rpgsolo.engine.EventStore
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.rules.CampaignStarter
import dev.rpgsolo.rules.Catalog
import dev.rpgsolo.rules.PackageRegistry
import dev.rpgsolo.rules.StartResult

/**
 * Fluxo "Nova campanha" (9.1 + 5.1.4) em Kotlin puro, para a tela só desenhar o passo atual:
 * descrição livre → perguntas do motor → revisão (avisos, ficha, nome) → campanha iniciada.
 * Nada é gravado antes de [start] ser aceito.
 */
class NewCampaignFlow(
    private val interpreter: CampaignInterpreter,
    private val catalog: Catalog,
    private val registry: PackageRegistry,
    private val store: EventStore,
    private val newId: () -> String = { "c" + System.nanoTime() },
) {
    sealed interface Step {
        data object Describe : Step

        data class Asking(val ask: Interpretation.Ask) : Step

        /** Tudo resolvido; o jogador vê [Interpretation.Ready.resolved] (avisos), escolhe a ficha e dá o nome. */
        data class Review(val ready: Interpretation.Ready, val errors: List<String> = emptyList()) : Step

        data class Started(val result: StartResult.Started) : Step

        /** Erro que não avança o fluxo (rede, bloqueio). O jogador pode corrigir a descrição e tentar de novo. */
        data class Problem(val reasons: List<String>) : Step
    }

    var step: Step = Step.Describe
        private set

    /** Chamada de rede (IA): rode fora da thread principal. */
    fun submitDescription(text: String): Step = move(interpreter.interpret(text, catalog))

    fun answer(optionId: String): Step {
        val s = step as? Step.Asking ?: return step
        return move(interpreter.answer(s.ask, optionId, catalog))
    }

    /** Fichas que podem entrar nesta campanha: só as da mesma edição (nunca mistura sistemas). */
    fun compatible(fichas: List<Ficha>): List<Ficha> {
        val r = (step as? Step.Review)?.ready ?: return emptyList()
        return fichas.filter { it.system.editionId == r.resolved.edition.id }
    }

    fun start(
        ficha: Ficha,
        name: String,
        seed: Long,
    ): Step {
        val review = step as? Step.Review ?: return step
        val result =
            CampaignStarter.start(
                store, registry, catalog, review.ready.resolved, review.ready.config, ficha,
                newId(), name, seed, review.ready.draft.customSetting,
            )
        step =
            when (result) {
                is StartResult.Started -> Step.Started(result)
                is StartResult.Refused -> review.copy(errors = result.reasons)
            }
        return step
    }

    fun reset() {
        step = Step.Describe
    }

    private fun move(i: Interpretation): Step {
        step =
            when (i) {
                is Interpretation.Ready -> Step.Review(i)
                is Interpretation.Ask -> Step.Asking(i)
                is Interpretation.Invalid -> Step.Problem(i.errors)
                is Interpretation.Failed -> Step.Problem(listOf(i.reason))
            }
        return step
    }
}
