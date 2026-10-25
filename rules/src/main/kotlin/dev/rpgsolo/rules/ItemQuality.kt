package dev.rpgsolo.rules

/** Qualidade de um item: multiplica o efeito (percentual por pacote, ver [RulePackage.qualityPercent]). */
enum class ItemQuality(val id: String, val label: String, val defaultPercent: Int, val minLevel: Int = 1) {
    NORMAL("normal", "Normal", 100),
    OTIMA("otima", "Ótima", 125),
    RARA("rara", "Rara", 150),
    EPICA("epica", "Épica", 200, minLevel = 10),
    LENDARIA("lendaria", "Lendária", 250, minLevel = 10),
    ;

    /**
     * N-33.4: vagas de gema da peça (comum 1, rara 2, lendária 3). ASSUMINDO: normal e ótima valem como comum;
     * rara e épica como rara; só a lendária tem 3.
     */
    val gemSockets: Int
        get() =
            when (this) {
                NORMAL, OTIMA -> 1
                RARA, EPICA -> 2
                LENDARIA -> 3
            }

    companion object {
        /** N-12: o id antigo `boa` continua valendo como [OTIMA]. */
        fun fromId(id: String): ItemQuality? = entries.firstOrNull { it.id == id } ?: if (id == "boa") OTIMA else null
    }
}

/**
 * Id de item: `base#qualidade@elemento` (ex.: `espada-curta#rara@fogo`); sem `#` = [ItemQuality.NORMAL], sem `@` = sem encantamento (N-17).
 * Qualidade e encantamento ficam no próprio id para o inventário, o log e os comandos do motor continuarem iguais.
 */
object ItemRef {
    const val SEPARATOR = '#'
    const val ENCHANT_SEPARATOR = '@'

    fun base(id: String): String = id.substringBefore(SEPARATOR).substringBefore(ENCHANT_SEPARATOR)

    /** Qualidade do id; `null` se o sufixo não for uma qualidade conhecida ou o encantamento for inválido (id inválido). */
    fun quality(id: String): ItemQuality? {
        val raw = enchantRaw(id)
        if (raw != null &&
            runCatching {
                dev.rpgsolo.engine.ElementChart.parse(raw)
            }.getOrNull().let { it == null || it == dev.rpgsolo.engine.ElementPath.NONE }
        ) {
            return null
        }
        if (SEPARATOR !in id.substringBefore(ENCHANT_SEPARATOR)) return ItemQuality.NORMAL
        return ItemQuality.fromId(id.substringBefore(ENCHANT_SEPARATOR).substringAfter(SEPARATOR))
    }

    private fun enchantRaw(id: String): String? = if (ENCHANT_SEPARATOR in id) id.substringAfter(ENCHANT_SEPARATOR) else null

    /** N-17: elemento do encantamento; [dev.rpgsolo.engine.ElementPath.NONE] sem encantamento. */
    fun enchantment(id: String): dev.rpgsolo.engine.ElementPath =
        enchantRaw(id)?.let { runCatching { dev.rpgsolo.engine.ElementChart.parse(it) }.getOrNull() } ?: dev.rpgsolo.engine.ElementPath.NONE

    fun of(
        baseId: String,
        quality: ItemQuality,
        enchantment: dev.rpgsolo.engine.ElementPath = dev.rpgsolo.engine.ElementPath.NONE,
    ): String {
        val q = if (quality == ItemQuality.NORMAL) baseId else "$baseId$SEPARATOR${quality.id}"
        return if (enchantment == dev.rpgsolo.engine.ElementPath.NONE) q else "$q$ENCHANT_SEPARATOR${enchantmentId(enchantment)}"
    }

    fun enchantmentId(e: dev.rpgsolo.engine.ElementPath): String =
        when (e) {
            dev.rpgsolo.engine.ElementPath.WATER -> "agua"
            dev.rpgsolo.engine.ElementPath.FIRE -> "fogo"
            dev.rpgsolo.engine.ElementPath.METAL -> "ferro"
            dev.rpgsolo.engine.ElementPath.WIND -> "vento"
            dev.rpgsolo.engine.ElementPath.EARTH -> "terra"
            dev.rpgsolo.engine.ElementPath.YANG -> "luz"
            dev.rpgsolo.engine.ElementPath.YIN -> "trevas"
            dev.rpgsolo.engine.ElementPath.NONE -> "-"
        }
}
