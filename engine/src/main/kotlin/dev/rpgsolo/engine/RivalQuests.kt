package dev.rpgsolo.engine

/** RV-05: oferta de cena e quest para o retorno de um rival; o mestre só pode propor a quest descrita aqui. */
data class RivalQuestOffer(
    val identityKey: String,
    val name: String,
    val role: ReturnRole,
    val day: Int,
    val seeksHeir: Boolean = false,
)

/**
 * RV-05: cena e quests do mestre para o retorno do rival (vilão, chefão, aliado, favor, redenção). O motor decide se há
 * oferta (papel do RV-04, uma quest por rival e papel, nenhuma enquanto outra de rival estiver ativa); a IA só
 * escreve título e objetivos dentro do tema. O XP segue o teto do motor.
 */
object RivalQuests {
    const val ID_PREFIX = "rival:"

    fun questId(offer: RivalQuestOffer): String = "$ID_PREFIX${offer.identityKey}:${offer.role.name}:${offer.day}"

    fun isRivalQuest(q: Quest): Boolean = q.id.startsWith(ID_PREFIX)

    /** Chave de identidade e papel de uma quest de rival; null se o id não segue o formato. */
    fun parseId(id: String): Pair<String, ReturnRole>? {
        if (!id.startsWith(ID_PREFIX)) return null
        val parts = id.removePrefix(ID_PREFIX).split(':')
        if (parts.size < 3 || parts.last().toIntOrNull() == null) return null
        val role = runCatching { ReturnRole.valueOf(parts[parts.size - 2]) }.getOrNull() ?: return null
        return parts.dropLast(2).joinToString(":") to role
    }

    /** Oferta para o reencontro planejado, ou null (sem papel, quest de rival ativa ou papel já oferecido a este rival). */
    fun offer(
        plan: ReencounterPlan?,
        quests: List<Quest>,
        day: Int,
    ): RivalQuestOffer? {
        val role = plan?.role ?: return null
        val rivalQuests = quests.filter { isRivalQuest(it) }
        if (rivalQuests.any { it.status == QuestStatus.ACTIVE }) return null
        val key = plan.character.identityKey
        if (rivalQuests.any { parseId(it.id) == key to role }) return null
        return RivalQuestOffer(key, plan.character.name, role, day, plan.seeksHeir)
    }

    /** XP máximo aceito: teto inteiro para vilão e chefão (ameaça real); metade para aliado, favor e redenção. */
    fun xpCap(
        offer: RivalQuestOffer,
        questXpCap: Int,
    ): Int = if (offer.role == ReturnRole.VILLAIN || offer.role == ReturnRole.BOSS) questXpCap else questXpCap / 2

    /** Texto para o contexto do mestre: o que propor via `request_quest` com `rival:true` e o que nunca decidir. */
    fun masterInstruction(offer: RivalQuestOffer): String {
        val tema =
            when (offer.role) {
                ReturnRole.VILLAIN -> "a revanche de ${offer.name}: uma ameaça pessoal, com saída por confronto, acordo ou fuga"
                ReturnRole.BOSS -> "a ameaça maior que ${offer.name} lidera depois de subir de posição, com etapas antes do confronto final"
                ReturnRole.ALLY -> "uma ajuda de ${offer.name}, que respeita o herói, sem obrigação de lealdade total"
                ReturnRole.FAVOR -> "o favor que ${offer.name} cobra ou oferece, proporcional à dívida registrada"
                ReturnRole.REDEMPTION -> "a redenção de ${offer.name}: reparar o que deve a quem o poupou, sem perdão automático"
            }
        val herdeiro = if (offer.seeksHeir) " Ele procura o herdeiro de quem o derrotou." else ""
        return "Quest de rival disponível (papel ${offer.role.name.lowercase()}): se a cena permitir, ofereça $tema usando " +
            "request_quest com \"rival\":true.$herdeiro É no máximo uma; não invente poder, aliados nem recompensa fora do pacote."
    }
}
