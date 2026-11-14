package dev.rpgsolo.ui

/**
 * Modo de jogo da campanha (CG-6.2, M6, D-CG-05): História guiada (o mestre protege o cânone) ou Criativo livre
 * (o jogador pode propor fatos do mundo). Não muda regras nem dados; só a instrução enviada ao mestre.
 */
enum class ModoJogo(val id: String, val instrucao: String) {
    HISTORIA(
        "historia",
        "Modo de jogo: História guiada. Você conduz a trama e protege o cânone do mundo; fatos novos propostos pelo jogador " +
            "só entram se forem coerentes com o cânone e com o que já foi estabelecido.",
    ),
    CRIATIVO(
        "criativo",
        "Modo de jogo: Criativo livre. O jogador pode propor fatos, lugares e personagens do mundo; aceite-os sempre que não " +
            "contradigam o cânone fixo nem as regras mecânicas, incorporando-os à narração.",
    ),
    ;

    companion object {
        fun deId(id: String?): ModoJogo = entries.firstOrNull { it.id == id } ?: HISTORIA
    }
}
