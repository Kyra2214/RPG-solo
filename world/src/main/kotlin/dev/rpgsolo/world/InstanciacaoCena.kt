package dev.rpgsolo.world

class LocalCena(val id: String, val nome: String, val descricao: String)

class NpcCena(val id: String, val nome: String, val conceito: String)

class FaccaoCena(val id: String, val nome: String, val resumo: String)

/** O que uma cena numa cidade precisa que exista na campanha: o local, poucos NPCs vivos e as facções presentes. */
class CenaInstanciavel(
    val cidadeId: String,
    val local: LocalCena,
    val npcs: List<NpcCena>,
    val faccoes: List<FaccaoCena>,
    /** Rumores que já chegaram à cidade pelo grafo de Ecos (MI-19). */
    val rumores: List<Rumor> = emptyList(),
)

/**
 * Instanciação sob demanda (MI-18): a campanha só ganha uma cidade, NPCs e facções do mundo quando a cena toca neles.
 * Lê o molde e, se houver, o estado do último snapshot do mundo (quem está vivo, onde mora, quem governa hoje).
 * 🔶 ASSUMINDO: "o necessário" = o governante atual da cidade, os NPCs vivos principais e os de papel de nível 2
 * (herói, guardião, benfeitor, general), até [MAX_NPCS], e até [MAX_FACCOES] facções com presença na cidade (as lideradas
 * por NPCs escolhidos primeiro). Facções não são entidades do motor: entram como fatos; a ficha mecânica dos NPCs é
 * vazia (`rulesValidated = false` no `app`).
 */
object InstanciacaoCena {
    const val MAX_NPCS = 5
    const val MAX_FACCOES = 3

    @Suppress("UNCHECKED_CAST")
    fun cena(
        w: MundoMolde,
        estado: Map<String, Any?>?,
        cidadeId: String,
        maxNpcs: Int = MAX_NPCS,
        maxFaccoes: Int = MAX_FACCOES,
    ): CenaInstanciavel? {
        val cidade = w.t.getValue("cidade").firstOrNull { it["id"] == cidadeId } ?: return null
        val ent = (estado?.get("ent") as? Map<String, Map<String, Any?>>).orEmpty()
        val npcs = w.t.getValue("npc").associateBy { it.getValue("id") }

        fun cidadeDe(id: String): String? = ent[id]?.get("cidade")?.toString() ?: npcs[id]?.get("cidade")

        fun vivo(id: String): Boolean = ent[id]?.get("vivo")?.let { it == true } ?: (npcs[id]?.get("vivo") == "sim")

        fun papeis(id: String): Set<String> {
            val p = (ent[id]?.get("papeis")?.toString() ?: npcs[id]?.get("papeis") ?: "").split(';').filter { it.isNotBlank() }.toSet()
            val principal = ent[id]?.get("principal")?.let { it == true } ?: (npcs[id]?.get("principal") == "sim")
            return p + (if (principal) setOf("principal") else emptySet())
        }

        val aqui = (npcs.keys + ent.keys.filter { it.startsWith("npc.") }).distinct().filter { cidadeDe(it) == cidadeId && vivo(it) }.sorted()
        val governante = ent[cidadeId]?.get("governante")?.toString() ?: cidade["governante"]
        val escolhidos =
            (
                listOfNotNull(governante).filter { it in aqui } +
                    aqui.filter { "principal" in papeis(it) } +
                    aqui.filter { papeis(it).any { p -> p in Niveis.PAPEIS_N1 || p in Niveis.PAPEIS_N2 } }
            ).distinct().take(maxNpcs)

        val faccoes =
            w.t.getValue("faccao").filter { cidadeId in it.getValue("presenca_cidades").split(';').map { c -> c.trim() } }
                .sortedBy { if (it["lideranca"] in escolhidos) 0 else 1 }.take(maxFaccoes)

        val descricao = listOf(cidade["tipo"].orEmpty(), cidade["localizacao"].orEmpty(), cidade["governo"]?.let { "Governo: $it" }.orEmpty()).filter { it.isNotBlank() }.joinToString(" ")
        return CenaInstanciavel(
            cidadeId,
            LocalCena(cidadeId, cidade.getValue("nome").split(" — ")[0], descricao.take(2000)),
            escolhidos.map { id ->
                val r = npcs[id]
                val nome = ent[id]?.get("nome")?.toString() ?: r?.get("nome") ?: id
                val conceito =
                    if (r != null) {
                        listOf(r["cargo"], r["personalidade"], r["objetivo"]?.let { "Objetivo: $it" }).filter { !it.isNullOrBlank() && it != "—" }.joinToString(". ")
                    } else {
                        (ent[id]?.get("cargo")?.toString() ?: "").takeIf { it != "—" }.orEmpty()
                    }
                NpcCena(id, nome, conceito.take(600))
            },
            faccoes.map { FaccaoCena(it.getValue("id"), it.getValue("nome"), "${it.getValue("nome")}: ${it["objetivo"].orEmpty()}".take(500)) },
            RumoresEcos.doLugar(w, estado, cidadeId),
        )
    }
}
