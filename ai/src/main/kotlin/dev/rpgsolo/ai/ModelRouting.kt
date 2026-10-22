package dev.rpgsolo.ai

import dev.rpgsolo.engine.CheckResult
import dev.rpgsolo.engine.Outcome

/**
 * Fase 6.4: um provedor por papel. [cheap] classifica intenção e resume (6.4.1); [narrator] narra o dia a dia;
 * [premium] narra só os momentos importantes (6.4.2). Sem modelo extra configurado, todos são o mesmo.
 */
data class AiRoles(
    val cheap: AiProvider,
    val narrator: AiProvider,
    val premium: AiProvider = narrator,
) {
    val hasPremium: Boolean get() = premium !== narrator
}

enum class NarrationTier { STANDARD, PREMIUM }

/**
 * Quem decide o que é "importante" é o motor, nunca a IA: por ora, só crítico (sucesso ou falha) no teste rolado.
 * Regra fechada e previsível, para o custo não variar por capricho do modelo.
 */
object Importance {
    fun of(check: CheckResult?): NarrationTier =
        when (check?.outcome) {
            Outcome.CRITICAL_SUCCESS, Outcome.CRITICAL_FAILURE -> NarrationTier.PREMIUM
            else -> NarrationTier.STANDARD
        }
}
