package dev.rpgsolo.engine

/**
 * CP-04.2: "Arquivo completo" da campanha, no mesmo formato do livro ([AdventureBook]) para reaproveitar o PDF.
 * Soma ao livro o que pertence ao jogador: ficha, inventário, o que ele descobriu, lugares conhecidos e relações em palavras.
 * Nunca inclui o que é privado do mestre: dossiês e memórias de NPC, relógios, fatos registrados, segredos de lugares,
 * adaptações pendentes, chaves e configurações. Só lê um [GameState] já copiado; não altera nada e não é backup restaurável.
 */
object CampaignArchiveBuilder {
    const val NOTE =
        "Arquivo de leitura da campanha. Não contém segredos do mestre, chaves nem configurações e não serve para restaurar dados; para isso use o backup."

    private fun relationWord(v: Int): String =
        when {
            v >= 70 -> "devoção"
            v >= 30 -> "confiança"
            v >= 10 -> "simpatia"
            v > -10 -> "neutra"
            v > -30 -> "antipatia"
            v > -90 -> "hostilidade"
            else -> "ódio"
        }

    fun build(
        book: AdventureBook,
        state: GameState,
        playerId: String,
        itemName: (String) -> String = { it },
        skillName: (String) -> String = { it },
    ): AdventureBook {
        val player = state.entities[playerId]
        val character =
            player?.let { p ->
                val head =
                    buildString {
                        append(p.name).append(" — nível ").append(p.level).append(", ").append(p.xp).append(" XP")
                        if (!p.alive) {
                            append(" (morto)")
                        } else if (p.downed) {
                            append(" (caído)")
                        }
                    }
                val resources = p.resources.entries.joinToString("; ") { (k, r) -> "$k ${r.current}/${r.max}" }
                val stats = p.stats.entries.joinToString("; ") { (k, v) -> "$k $v" }
                val skills = p.skills.filterValues { it > 0 }.entries.joinToString("; ") { (k, v) -> "${skillName(k)} $v" }
                val conditions = state.conditions[p.id].orEmpty().sorted().joinToString(", ")
                listOfNotNull(
                    head,
                    resources.takeIf { it.isNotEmpty() }?.let { "Recursos: $it" },
                    stats.takeIf { it.isNotEmpty() }?.let { "Atributos: $it" },
                    skills.takeIf { it.isNotEmpty() }?.let { "Perícias: $it" },
                    conditions.takeIf { it.isNotEmpty() }?.let { "Condições: $it" },
                ).joinToString("\n")
            }
        val equipped = state.equipped[playerId].orEmpty()
        val inventory =
            state.inventory[playerId].orEmpty().filterValues { it > 0 }.entries.map { (item, qty) ->
                "${itemName(item)} × $qty" + if (equipped.values.contains(item)) " (equipado)" else ""
            }
        val known =
            state.knowledge.values.map { it.text.trim() + if (it.believed) " (apenas acreditado)" else "" }
                .filter { it.isNotBlank() }
        val places =
            state.knownPlaces.mapNotNull { state.places[it] }.map { p ->
                p.name + if (p.description.isNotBlank()) " — ${p.description.trim()}" else ""
            }
        val relations =
            state.relations.entries.mapNotNull { (key, value) ->
                val pair = key.split('\u0000', limit = 2)
                if (pair.size != 2 || playerId !in pair) return@mapNotNull null
                val other = if (pair[0] == playerId) pair[1] else pair[0]
                val otherName = state.entities[other]?.name ?: return@mapNotNull null
                val direction = if (pair[0] == playerId) "Você em relação a $otherName" else "$otherName em relação a você"
                "$direction: ${relationWord(value)}"
            }.sorted()
        val extra =
            listOf(
                BookSection("Sobre este arquivo", listOf(NOTE)),
                BookSection("Personagem", listOfNotNull(character)),
                BookSection("Inventário", inventory),
                BookSection("O que você descobriu", known),
                BookSection("Lugares conhecidos", places),
                BookSection("Relações", relations),
            ).filter { it.lines.isNotEmpty() }
        return book.copy(subtitle = "Arquivo completo — " + book.subtitle, sections = extra + book.sections)
    }
}
