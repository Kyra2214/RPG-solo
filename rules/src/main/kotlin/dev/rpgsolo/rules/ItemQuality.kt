package dev.rpgsolo.rules

/** Qualidade de um item: multiplica o efeito (percentual por pacote, ver [RulePackage.qualityPercent]). */
enum class ItemQuality(val id: String, val label: String, val defaultPercent: Int) {
    NORMAL("normal", "Normal", 100),
    BOA("boa", "Boa", 125),
    RARA("rara", "Rara", 150),
    LENDARIA("lendaria", "Lendária", 200),
    ;

    companion object {
        fun fromId(id: String): ItemQuality? = entries.firstOrNull { it.id == id }
    }
}

/**
 * Id de item com qualidade: `base#qualidade` (ex.: `pocao-cura#rara`); sem sufixo = [ItemQuality.NORMAL].
 * A qualidade fica no próprio id para o inventário, o log e os comandos do motor continuarem iguais.
 */
object ItemRef {
    const val SEPARATOR = '#'

    fun base(id: String): String = id.substringBefore(SEPARATOR)

    /** Qualidade do id; `null` se o sufixo não for uma qualidade conhecida (id inválido). */
    fun quality(id: String): ItemQuality? =
        if (SEPARATOR !in id) ItemQuality.NORMAL else ItemQuality.fromId(id.substringAfter(SEPARATOR))

    fun of(
        baseId: String,
        quality: ItemQuality,
    ): String = if (quality == ItemQuality.NORMAL) baseId else "$baseId$SEPARATOR${quality.id}"
}
