package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** N-33.4: gema engastada. [type] é o status reforçado (ver [Gems.TYPES]) e [quality] vai de 1 a 3. */
@Serializable
data class Gem(val type: String, val quality: Int)

/** N-33.4: peça com gemas engastadas; vale só enquanto [itemId] estiver equipado no slot. */
@Serializable
data class SocketedGems(val itemId: String, val gems: List<Gem>)

/** N-33.4: regras das gemas (tipos, valores, lapidação e preço do engaste). */
object Gems {
    /** Um tipo por status de combate; reaproveita os escopos das marcas do Tatuador (`max_pv` é o PV máximo). */
    val TYPES: List<String> = Tattoos.SCOPES
    const val MIN_QUALITY = 1
    const val MAX_QUALITY = 3
    const val CUT_DIE_SIDES = 20
    const val CUT_FAIL_MAX = 5
    const val NPC_FEE_PER_QUALITY = 50
    const val ITEM_PREFIX = "gema-"
    const val CUTTER_PREFIX = "profissao-lapidador-"
    const val SETTER_PREFIX = "profissao-montador-de-gemas-"

    /** +2, +4 e +6 em Ataque, Defesa e Velocidade; PV máximo +10, +20 e +30. */
    fun bonusOf(gem: Gem): Int = gem.quality * 2 * (if (gem.type == "max_pv") 5 else 1)

    fun bonus(
        socketed: Map<String, SocketedGems>?,
        equipped: Map<String, String>?,
        scope: String,
    ): Int =
        socketed.orEmpty().filter { (slot, s) -> equipped?.get(slot) == s.itemId }
            .values.sumOf { s -> s.gems.filter { it.type == scope }.sumOf { bonusOf(it) } }

    /** Id do item de inventário da gema (ex.: `gema-ataque-2`; PV usa `gema-pv-N`). */
    fun itemId(gem: Gem): String = "$ITEM_PREFIX${if (gem.type == "max_pv") "pv" else gem.type}-${gem.quality}"

    /** Inverso de [itemId]; `null` se o id não for de uma gema válida. */
    fun parse(itemId: String): Gem? {
        if (!itemId.startsWith(ITEM_PREFIX)) return null
        val body = itemId.removePrefix(ITEM_PREFIX)
        val quality = body.substringAfterLast('-').toIntOrNull() ?: return null
        val raw = body.substringBeforeLast('-')
        val type = if (raw == "pv") "max_pv" else raw
        return Gem(type, quality).takeIf { type in TYPES && quality in MIN_QUALITY..MAX_QUALITY }
    }

    /** Lapidar: d20 de 1 a 5 falha (25%). Sucesso sobe 1 nível (teto 3); falha desce 1 e a de qualidade 1 some (`null`). */
    fun cut(
        gem: Gem,
        roll: Int,
    ): Gem? =
        if (roll > CUT_FAIL_MAX) {
            gem.copy(quality = minOf(MAX_QUALITY, gem.quality + 1))
        } else if (gem.quality > MIN_QUALITY) {
            gem.copy(quality = gem.quality - 1)
        } else {
            null
        }

    /** 50 prata × qualidade da gema por engaste ou remoção feitos pelo ferreiro NPC. */
    fun npcFee(gem: Gem): Int = NPC_FEE_PER_QUALITY * gem.quality

    private fun maxGrade(
        skillLevels: Map<String, Int>,
        prefix: String,
    ): Int = (1..5).lastOrNull { (skillLevels["$prefix$it"] ?: 0) > 0 } ?: 0

    fun cutterGrade(skillLevels: Map<String, Int>): Int = maxGrade(skillLevels, CUTTER_PREFIX)

    fun setterGrade(skillLevels: Map<String, Int>): Int = maxGrade(skillLevels, SETTER_PREFIX)
}
