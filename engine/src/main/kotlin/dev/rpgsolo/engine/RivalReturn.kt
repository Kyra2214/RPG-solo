package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** RV-04: papel do rival quando ele volta. Decidido pelo motor; a IA só narra. */
@Serializable
enum class ReturnRole { VILLAIN, BOSS, ALLY, FAVOR, REDEMPTION }

/**
 * RV-04: gatilho e papel do retorno de um rival. O gatilho (quando e se ele volta) é o do reencontro (MP-04: janela de
 * campanhas, orçamento de aparições, no máximo 1 por campanha); aqui o motor decide o papel a partir do que já gravou
 * (RV-01/RV-02) e da vida fora de cena (MP-03). Sem sorteio: as mesmas entradas dão sempre o mesmo papel.
 *
 * Regras: ressentimento volta como vilão e, se subiu duas faixas de cargo fora de cena, como chefão; respeito volta
 * como aliado; dívida volta cobrando ou pagando um favor e, se foi poupado, como redenção.
 */
object RivalReturns {
    const val BOSS_RANK = 2

    fun role(c: RecurringCharacter): ReturnRole? {
        val outcome = c.defeat ?: return null
        val rank = c.offscreen?.rank ?: 0
        return when (c.defeatAttitude ?: DefeatAttitude.RESENTMENT) {
            DefeatAttitude.RESENTMENT -> if (rank >= BOSS_RANK) ReturnRole.BOSS else ReturnRole.VILLAIN
            DefeatAttitude.RESPECT -> ReturnRole.ALLY
            DefeatAttitude.DEBT -> if (outcome == DefeatOutcome.SPARED) ReturnRole.REDEMPTION else ReturnRole.FAVOR
        }
    }

    /** Como o papel deve ser narrado; nunca concede poder além do que o motor registrou. */
    fun description(role: ReturnRole): String =
        when (role) {
            ReturnRole.VILLAIN -> "volta como vilão pessoal: busca revanche ou reparação, com os recursos que reuniu"
            ReturnRole.BOSS -> "volta como chefão: subiu de posição fora de cena e lidera uma ameaça maior, ainda dentro dos recursos registrados"
            ReturnRole.ALLY -> "volta como aliado: respeita o herói e pode ajudar, sem obrigação de lealdade total"
            ReturnRole.FAVOR -> "volta por um favor: cobra ou oferece uma retribuição proporcional à dívida"
            ReturnRole.REDEMPTION -> "volta em busca de redenção: quer reparar o que deve a quem o poupou"
        }

    /** O herói que o derrotou já morreu e o protagonista atual é quem herdou o assunto (liga com o LG-03). */
    fun seeksHeir(
        c: RecurringCharacter,
        state: GameState,
    ): Boolean {
        if (c.defeatedBy.isBlank()) return false
        val key = RecurringIdentity.normalize(c.defeatedBy)
        return state.legacies.values.any { RecurringIdentity.normalize(it.heroName) == key }
    }
}
