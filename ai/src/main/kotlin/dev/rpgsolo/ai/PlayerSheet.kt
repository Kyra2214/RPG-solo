package dev.rpgsolo.ai

import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.Ficha
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.rules.RulePackage
import dev.rpgsolo.rules.TraitDef

/**
 * Quanto da ficha vai no pedido à IA. Cada pedido é independente (o provedor não guarda histórico),
 * então o que sai da ficha precisa ser decidido a cada jogada:
 *  - [FULL]: ficha completa, com a descrição de cada traço. Só no INÍCIO do combate.
 *  - [ROUND]: durante o combate, só o status da rodada (recursos e condições) + os nomes dos traços.
 *  - [BRIEF]: fora de combate, uma linha curta com nome, conceito e nomes dos traços.
 */
enum class SheetLevel { BRIEF, FULL, ROUND }

/**
 * Ficha do jogador em texto, para o mestre (IA) saber o que o personagem é e pode fazer.
 *
 * Os valores mutáveis (atributos, recursos, condições, itens) são lidos do estado ATUAL a cada jogada;
 * vantagens, desvantagens e perícias vêm da [ficha] e são explicadas pelo [pkg].
 * Sem [pkg] (edição sem pacote) ou sem [ficha], sai só o que o estado conhece: o texto fica menor, nunca inventado.
 */
class PlayerSheet(
    private val pkg: RulePackage? = null,
    private val ficha: Ficha? = null,
    private val characterChoices: String = "",
) {
    /** Ordem e rótulo dos tipos de traço; tipos que o pacote inventar caem no fim com o próprio nome. */
    private val typeLabels =
        linkedMapOf(
            "vantagem" to "Vantagens",
            "poder" to "Poderes",
            "desvantagem" to "Desvantagens",
            "pericia" to "Perícias",
        )

    fun render(
        state: GameState,
        playerId: String,
        level: SheetLevel = SheetLevel.FULL,
    ): String? {
        val e = state.entities[playerId] ?: return null
        return when (level) {
            SheetLevel.FULL -> full(e, state, playerId)
            SheetLevel.BRIEF -> brief(e)
            SheetLevel.ROUND -> round(e, state, playerId)
        }
    }

    private fun full(
        e: Entity,
        state: GameState,
        playerId: String,
    ): String =
        buildString {
            append("Ficha do jogador (PRIVADA: use para decidir o que é possível e para conduzir o combate; ")
            append("nunca recite números, pontos ou nomes de mecânica ao jogador):")
            append("\n- Nome: ").append(e.name)
            ficha?.concept?.takeIf { it.isNotBlank() }?.let { append("\n- Conceito: ").append(it) }
            characterChoices.takeIf {
                it.isNotBlank()
            }?.let { append("\n- Raça/linhagem, classe, profissão e maldições (lore do catálogo; sem regras inventadas):\n").append(it) }
            append("\n- Nível ").append(e.level)
            if (!e.alive) append(" (MORTO)")
            if (e.stats.isNotEmpty()) {
                append("\n- Atributos: ").append(e.stats.entries.joinToString(", ") { (id, v) -> "${statName(id)} $v" })
            }
            liveStatus(e, state, playerId)?.let { append("\n- ").append(it) }
            val items = state.inventory[playerId].orEmpty().filterValues { it > 0 }
            if (items.isNotEmpty()) {
                append("\n- Itens: ").append(items.entries.joinToString(", ") { (k, n) -> if (n > 1) "$k x$n" else k })
            }
            traitLines().forEach { append("\n").append(it) }
        }

    private fun brief(e: Entity): String =
        buildString {
            append("Personagem do jogador (resumo privado; não recite): ").append(e.name).append(", nível ").append(e.level)
            ficha?.concept?.takeIf { it.isNotBlank() }?.let { append(" — ").append(it) }
            characterChoices.takeIf { it.isNotBlank() }?.let { append("\n").append(it) }
            if (!e.alive) append(" (MORTO)")
            traitNames().forEach { append("\n").append(it) }
        }

    private fun round(
        e: Entity,
        state: GameState,
        playerId: String,
    ): String =
        buildString {
            append("Status do jogador nesta rodada (privado): ")
            append(liveStatus(e, state, playerId) ?: "sem recursos nem condições")
            append("\n").append(brief(e))
        }

    /** "Recursos agora: Vida 6/10 · Condições: Envenenado (...)" ou null se não há nenhum dos dois. */
    private fun liveStatus(
        e: Entity,
        state: GameState,
        playerId: String,
    ): String? {
        val parts = mutableListOf<String>()
        if (e.resources.isNotEmpty()) {
            parts += "Recursos agora: " + e.resources.entries.joinToString(", ") { (id, r) -> "${resourceName(id)} ${r.current}/${r.max}" }
        }
        val conds = state.conditions[playerId].orEmpty().sorted()
        if (conds.isNotEmpty()) parts += "Condições ativas: " + conds.joinToString("; ") { conditionText(it) }
        return parts.takeIf { it.isNotEmpty() }?.joinToString(" · ")
    }

    private fun groupedTraits(): List<Pair<String, List<String>>> {
        val ids = ficha?.traits.orEmpty()
        if (ids.isEmpty()) return emptyList()
        val known = ids.mapNotNull { id -> pkg?.trait(id) }
        val byType = known.groupBy { it.type }
        val order = typeLabels.keys + (byType.keys - typeLabels.keys)
        val out = mutableListOf<Pair<String, List<String>>>()
        for (type in order) {
            val list = byType[type] ?: continue
            out += (typeLabels[type] ?: type.replaceFirstChar { it.uppercase() }) to list.map { traitText(it) }
        }
        return out
    }

    private fun unknownTraits(): List<String> = ficha?.traits.orEmpty().filter { id -> pkg?.trait(id) == null }

    /** Traços com descrição (usado só na ficha completa). */
    private fun traitLines(): List<String> {
        val out = mutableListOf<String>()
        groupedTraits().forEach { (label, items) ->
            out += "- $label:"
            items.forEach { out += "  • $it" }
        }
        // Traço sem definição no pacote: o mestre ainda sabe que existe, sem inventar o efeito.
        unknownTraits().takeIf { it.isNotEmpty() }?.let { out += "- Outros traços (sem descrição no pacote): ${it.joinToString(", ")}" }
        return out
    }

    /** Só os nomes dos traços, uma linha por tipo (usado no resumo e no status da rodada). */
    private fun traitNames(): List<String> {
        val ids = ficha?.traits.orEmpty()
        if (ids.isEmpty()) return emptyList()
        val byType = ids.mapNotNull { pkg?.trait(it) }.groupBy { it.type }
        val order = typeLabels.keys + (byType.keys - typeLabels.keys)
        val out = mutableListOf<String>()
        for (type in order) {
            val list = byType[type] ?: continue
            out += (typeLabels[type] ?: type.replaceFirstChar { it.uppercase() }) + ": " + list.joinToString(", ") { it.name }
        }
        unknownTraits().takeIf { it.isNotEmpty() }?.let { out += "Outros traços: ${it.joinToString(", ")}" }
        return out
    }

    private fun traitText(t: TraitDef): String {
        val base = if (t.description.isBlank()) t.name else "${t.name}: ${t.description}"
        if (t.bonuses.isEmpty()) return base
        // O motor soma isso sozinho; o mestre só informa a situação em "skill" quando couber.
        return "$base [bônus nos testes: " + t.bonuses.joinToString(", ") { "${if (it.bonus > 0) "+" else ""}${it.bonus} em ${it.scope}" } + "]"
    }

    private fun statName(id: String) = pkg?.stat(id)?.name ?: id

    private fun resourceName(id: String) = pkg?.data?.resources?.firstOrNull { it.id == id }?.name ?: id

    private fun conditionText(id: String): String {
        val c = pkg?.data?.conditions?.firstOrNull { it.id == id } ?: return id
        return if (c.description.isBlank()) c.name else "${c.name} (${c.description})"
    }
}
