package dev.rpgsolo.rules

/** N-14: slot da arma e do escudo no sistema próprio. */
const val WEAPON_SLOT = "mao"
const val CHEST_SLOT = "peito"
const val SHIELD_SLOT = "outra-mao"

/**
 * N-13: o que o portador veste sem penalidade. [armorWeights] e [weaponTypes] vêm das classes (vazio = sem restrição);
 * [masterWeapons] (Mestre das Armas) anula a penalidade de arma e [masterGear] (Mestre dos Equipamentos), a de peso.
 */
data class WearerProfile(
    val armorWeights: Set<ItemWeight> = emptySet(),
    val weaponTypes: Set<String> = emptySet(),
    val masterWeapons: Boolean = false,
    val masterGear: Boolean = false,
)
