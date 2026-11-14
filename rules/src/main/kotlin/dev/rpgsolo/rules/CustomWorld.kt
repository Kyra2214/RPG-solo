package dev.rpgsolo.rules

import dev.rpgsolo.engine.AddPlace
import dev.rpgsolo.engine.Ambientacao
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.ConnectPlaces
import dev.rpgsolo.engine.CreateClock
import dev.rpgsolo.engine.DiscoverPlace
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.Place
import dev.rpgsolo.engine.SetAmbientacao
import java.text.Normalizer

/** MP-08: um lugar proposto para o mundo próprio. [connections] são ids de outros lugares do mesmo rascunho. */
data class CustomPlaceDraft(
    val id: String,
    val name: String,
    val description: String = "",
    val secret: String = "",
    val connections: List<String> = emptyList(),
)

/** MP-08: relógio de agenda proposto (ameaça ou plano de facção). */
data class CustomClockDraft(
    val id: String,
    val name: String,
    val segments: Int,
    val consequence: String = "",
)

/**
 * MP-08: mundo próprio proposto pela IA a partir da descrição do jogador. É só proposta até passar por
 * [CustomWorldValidator]; nada entra na campanha sem validação.
 */
data class CustomWorldDraft(
    val name: String,
    val tone: String = "",
    val technology: String = "",
    val places: List<CustomPlaceDraft> = emptyList(),
    val factions: List<String> = emptyList(),
    val conflicts: List<String> = emptyList(),
    val clocks: List<CustomClockDraft> = emptyList(),
    val fictionalGear: List<String> = emptyList(),
    /** Id do lugar onde a campanha começa; se nulo, o primeiro lugar da lista. */
    val startPlaceId: String? = null,
)

/** Limites do mundo próprio (MP-08). Existem para o replay e o contexto da IA não incharem. */
object CustomWorldLimits {
    const val MIN_PLACES = 1
    const val MAX_PLACES = 30
    const val MAX_FACTIONS = 12
    const val MAX_CONFLICTS = 12
    const val MAX_CLOCKS = 8
    const val MIN_CLOCK_SEGMENTS = 2
    const val MAX_CLOCK_SEGMENTS = 12
    const val MAX_NAME = 80
    const val MAX_TEXT = 500
    val ID_PATTERN = Regex("^[a-z0-9][a-z0-9._-]{0,39}$")
}

/** Valida o rascunho de mundo próprio. Lista vazia = pode gravar. Determinístico, sem IA. */
object CustomWorldValidator {
    fun validate(draft: CustomWorldDraft): List<String> {
        val errors = mutableListOf<String>()
        val l = CustomWorldLimits

        fun nome(s: String) = Normalizer.normalize(s.trim(), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").lowercase()

        fun texto(
            rotulo: String,
            s: String,
        ) {
            if (s.length > l.MAX_TEXT) errors += "$rotulo passa de ${l.MAX_TEXT} caracteres"
        }

        fun duplicados(
            rotulo: String,
            valores: List<String>,
        ) {
            valores.groupingBy { nome(it) }.eachCount().filterValues { it > 1 }.keys.forEach { errors += "$rotulo duplicado: $it" }
        }
        if (draft.name.isBlank()) errors += "o mundo precisa de um nome"
        if (draft.name.length > l.MAX_NAME) errors += "nome do mundo passa de ${l.MAX_NAME} caracteres"
        texto("tom", draft.tone)
        texto("tecnologia", draft.technology)

        if (draft.places.size < l.MIN_PLACES) errors += "o mundo precisa de ao menos ${l.MIN_PLACES} lugar"
        if (draft.places.size > l.MAX_PLACES) errors += "lugares demais: ${draft.places.size} (máximo ${l.MAX_PLACES})"
        if (draft.factions.size > l.MAX_FACTIONS) errors += "facções demais: ${draft.factions.size} (máximo ${l.MAX_FACTIONS})"
        if (draft.conflicts.size > l.MAX_CONFLICTS) errors += "conflitos demais: ${draft.conflicts.size} (máximo ${l.MAX_CONFLICTS})"
        if (draft.clocks.size > l.MAX_CLOCKS) errors += "relógios demais: ${draft.clocks.size} (máximo ${l.MAX_CLOCKS})"

        draft.places.forEach { p ->
            if (!l.ID_PATTERN.matches(p.id)) errors += "id de lugar inválido: '${p.id}'"
            if (p.name.isBlank()) errors += "lugar sem nome: ${p.id}"
            if (p.name.length > l.MAX_NAME) errors += "nome de lugar passa de ${l.MAX_NAME} caracteres: ${p.id}"
            texto("descrição de ${p.id}", p.description)
            texto("segredo de ${p.id}", p.secret)
        }
        draft.places.groupingBy { it.id }.eachCount().filterValues { it > 1 }.keys.forEach { errors += "id de lugar duplicado: $it" }
        duplicados("nome de lugar", draft.places.map { it.name }.filter { it.isNotBlank() })
        duplicados("facção", draft.factions.filter { it.isNotBlank() })
        draft.factions.filter { it.isBlank() || it.length > l.MAX_NAME }.forEach { errors += "facção com nome vazio ou longo demais" }
        draft.conflicts.filter { it.isBlank() || it.length > l.MAX_TEXT }.forEach { errors += "conflito vazio ou longo demais" }

        val ids = draft.places.map { it.id }.toSet()
        draft.places.forEach { p ->
            p.connections.forEach { to ->
                if (to !in ids) {
                    errors += "ligação de ${p.id} para lugar inexistente: $to"
                } else if (to == p.id) {
                    errors += "lugar ligado a si mesmo: ${p.id}"
                }
            }
        }
        if (draft.startPlaceId != null && draft.startPlaceId !in ids) errors += "lugar inicial inexistente: ${draft.startPlaceId}"
        if (errors.isEmpty() && draft.places.size > 1 && !conexo(draft)) {
            errors += "o mapa é desconexo: todo lugar precisa ser alcançável a partir do lugar inicial"
        }

        draft.clocks.forEach { c ->
            if (!l.ID_PATTERN.matches(c.id)) errors += "id de relógio inválido: '${c.id}'"
            if (c.name.isBlank() || c.name.length > l.MAX_NAME) errors += "relógio ${c.id} com nome vazio ou longo demais"
            if (c.segments !in l.MIN_CLOCK_SEGMENTS..l.MAX_CLOCK_SEGMENTS) {
                errors += "relógio ${c.id} com ${c.segments} casas (permitido ${l.MIN_CLOCK_SEGMENTS} a ${l.MAX_CLOCK_SEGMENTS})"
            }
            texto("consequência de ${c.id}", c.consequence)
        }
        draft.clocks.groupingBy { it.id }.eachCount().filterValues { it > 1 }.keys.forEach { errors += "id de relógio duplicado: $it" }
        duplicados("nome de relógio", draft.clocks.map { it.name }.filter { it.isNotBlank() })
        return errors
    }

    /** As ligações são de mão dupla (como em `PlaceMap`); a busca parte do lugar inicial. */
    private fun conexo(draft: CustomWorldDraft): Boolean {
        val vizinhos = mutableMapOf<String, MutableSet<String>>()
        draft.places.forEach { p ->
            p.connections.forEach { to ->
                vizinhos.getOrPut(p.id) { mutableSetOf() } += to
                vizinhos.getOrPut(to) { mutableSetOf() } += p.id
            }
        }
        val inicio = draft.startPlaceId ?: draft.places.first().id
        val visto = mutableSetOf(inicio)
        val fila = ArrayDeque(listOf(inicio))
        while (fila.isNotEmpty()) {
            vizinhos[fila.removeFirst()].orEmpty().filter { visto.add(it) }.forEach(fila::addLast)
        }
        return visto.size == draft.places.size
    }
}

/** Instala um [CustomWorldDraft] validado numa campanha sem mapa, num único lote atômico. */
object CustomWorld {
    /** Comandos de instalação. Só use com rascunho que passou em [CustomWorldValidator]. */
    fun commands(
        draft: CustomWorldDraft,
        campaignId: String,
    ): List<Command> {
        val cmds = mutableListOf<Command>()
        cmds +=
            SetAmbientacao(
                Ambientacao(
                    id = "ambientacao.$campaignId",
                    campaignId = campaignId,
                    name = draft.name.trim(),
                    tone = draft.tone.trim(),
                    technology = draft.technology.trim(),
                    organizations = draft.factions.map { it.trim() },
                    conflicts = draft.conflicts.map { it.trim() },
                    fictionalGear = draft.fictionalGear.map { it.trim() },
                ),
            )
        draft.places.forEach { cmds += AddPlace(Place(it.id, it.name.trim(), it.description.trim(), it.secret.trim())) }
        val feitas = mutableSetOf<Set<String>>()
        draft.places.forEach { p ->
            p.connections.forEach { to -> if (feitas.add(setOf(p.id, to))) cmds += ConnectPlaces(p.id, to) }
        }
        val inicio = draft.startPlaceId ?: draft.places.first().id
        // O primeiro lugar descoberto vira o lugar atual (mesmo caminho de Aeltharyn).
        cmds += DiscoverPlace(inicio)
        draft.clocks.forEach { cmds += CreateClock(it.id, it.name.trim(), it.segments, consequence = it.consequence.trim()) }
        return cmds
    }

    /** Valida e instala. Recusa campanha que já tem mapa ou relógios com os mesmos ids; nada é gravado se algo falhar. */
    fun install(
        session: CampaignSession,
        draft: CustomWorldDraft,
        campaignId: String,
    ): ExecResult {
        val problems = CustomWorldValidator.validate(draft)
        if (problems.isNotEmpty()) return ExecResult.Rejected("mundo próprio inválido: ${problems.joinToString("; ")}", 0)
        if (session.state.places.isNotEmpty() || session.state.currentPlaceId != null) {
            return ExecResult.Rejected("o mundo próprio só pode ser instalado numa campanha sem mapa prévio", 0)
        }
        val colisao = draft.clocks.map { it.id }.filter { it in session.state.clocks }
        if (colisao.isNotEmpty()) return ExecResult.Rejected("relógios já existentes: ${colisao.joinToString()}", 0)
        return session.executeAll(commands(draft, campaignId))
    }
}
