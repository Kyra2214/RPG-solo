package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** Um local da campanha. [secret] guarda o que o jogador ainda não sabe e nunca sai em [PlaceMap.playerView]. */
@Serializable
data class Place(val id: String, val name: String, val description: String = "", val secret: String = "")

/**
 * Fase 9.3.3: mapa textual de locais. É um grafo de passagens (sempre de mão dupla) com o conjunto
 * dos locais que o personagem já descobriu. A visão do jogador mostra só o descoberto.
 */
class PlaceMap {
    private val places = linkedMapOf<String, Place>()
    private val links = linkedMapOf<String, MutableSet<String>>()
    private val known = linkedSetOf<String>()

    fun add(p: Place) {
        require(p.id.isNotBlank()) { "local sem id" }
        require(p.name.isNotBlank()) { "local sem nome" }
        require(!places.containsKey(p.id)) { "local já existe: ${p.id}" }
        places[p.id] = p
        links[p.id] = linkedSetOf()
    }

    fun connect(
        a: String,
        b: String,
    ) {
        require(a != b) { "um local não liga a si mesmo" }
        require(places.containsKey(a)) { "local inexistente: $a" }
        require(places.containsKey(b)) { "local inexistente: $b" }
        links.getValue(a) += b
        links.getValue(b) += a
    }

    /** O personagem passa a conhecer o local. */
    fun discover(id: String) {
        require(places.containsKey(id)) { "local inexistente: $id" }
        known += id
    }

    fun isKnown(id: String) = id in known

    /** Caminho mais curto passando só por locais descobertos; null se não houver. */
    fun route(
        from: String,
        to: String,
    ): List<String>? {
        if (from !in known || to !in known) return null
        val prev = mutableMapOf<String, String?>(from to null)
        val queue = ArrayDeque(listOf(from))
        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            if (cur == to) break
            for (n in links.getValue(cur)) if (n in known && n !in prev) {
                prev[n] = cur
                queue += n
            }
        }
        if (to !in prev) return null
        return generateSequence(to) { prev[it] }.toList().reversed()
    }

    /** Texto para o jogador: só locais descobertos e só passagens entre eles. Nunca mostra [Place.secret]. */
    fun playerView(current: String): String {
        require(current in known) { "local atual não descoberto: $current" }
        return buildString {
            val here = places.getValue(current)
            append("Você está em ${here.name}.")
            if (here.description.isNotBlank()) append(" ${here.description}")
            val exits = links.getValue(current).filter { it in known }.map { places.getValue(it).name }
            append(if (exits.isEmpty()) "\nNenhuma saída conhecida." else "\nSaídas conhecidas: ${exits.joinToString(", ")}.")
            val others = known.filter { it != current && it !in links.getValue(current) }.map { places.getValue(it).name }
            if (others.isNotEmpty()) append("\nOutros lugares que você conhece: ${others.joinToString(", ")}.")
        }
    }

    /** Para o contexto da IA: o local atual completo (com segredo) e só os nomes das saídas. */
    fun sceneContext(current: String): String {
        val here = requireNotNull(places[current]) { "local inexistente: $current" }
        val exits = links.getValue(current).map { places.getValue(it).name }
        return "Local: ${here.name}. ${here.description}".trim() +
            (if (here.secret.isNotBlank()) "\nSegredo do local (não revelar sem o motor liberar): ${here.secret}" else "") +
            "\nSaídas: ${exits.joinToString(", ").ifEmpty { "nenhuma" }}."
    }
}
