package dev.rpgsolo.world

/** Quão fiel é o que chegou a um lugar: a certeza do Eco desce de degrau a cada [dois passos] do grafo (MP-07). */
enum class VersaoRumor(val rotulo: String) {
    EXATA("exata"),
    EXAGERADA("exagerada"),
    FALSA("falsa"),
}

class Rumor(
    val ecoId: String,
    val deltaId: String,
    val tipo: String,
    val entidade: String,
    val camada: String,
    val versao: VersaoRumor,
    val passo: Int,
    val inicio: Int,
    val texto: String,
)

/**
 * Ecos viram rumores (MI-19, MP-07). O motor de Ecos já espalha cada fato pelo grafo de rotas abertas (estado `eco.<delta>`:
 * `difusao.lugares`, `passo`, `certeza`); aqui só se lê esse estado: o rumor chega a um lugar se e só se o lugar está na lista
 * de quem já sabe (por isso chega a 1 passo e não a 4 quando a difusão parou em 1). Nada é gravado no mundo.
 * 🔶 ASSUMINDO: `confirmado` = versão exata, `alegado` = exagerada e `rumor` = falsa (o mestre deve tratá-la como não confiável);
 * pistas de mistério (`restrito`) e fatos `secreto` não viram boato; janela de [JANELA_DIAS] dias (1 ano) e até [MAX] rumores,
 * os mais recentes primeiro. O reconhecimento do herói depende de o herói existir no mundo (MI-26) e não entra aqui.
 */
object RumoresEcos {
    const val JANELA_DIAS = 365
    const val MAX = 5

    @Suppress("UNCHECKED_CAST")
    fun doLugar(
        w: MundoMolde,
        estado: Map<String, Any?>?,
        lugarId: String,
        janelaDias: Int = JANELA_DIAS,
        max: Int = MAX,
    ): List<Rumor> {
        val ent = (estado?.get("ent") as? Map<String, Map<String, Any?>>) ?: return emptyList()
        val hoje = (estado["proximo_dia"] as? Number)?.toInt() ?: 0

        fun nome(id: String) = w.linha(id)?.get("nome")?.split(" — ")?.get(0) ?: id
        return ent.filterKeys { it.startsWith("eco.") }.mapNotNull { (k, v) ->
            val dif = v["difusao"] as? Map<String, Any?> ?: return@mapNotNull null
            val camada = v["camada"].toString()
            val inicio = (v["inicio"] as? Number)?.toInt() ?: 0
            if (camada == "restrito" || camada == "secreto" || inicio < hoje - janelaDias) return@mapNotNull null
            if (lugarId !in (dif["lugares"] as? List<*>).orEmpty().map { it.toString() }) return@mapNotNull null
            val versao =
                when (dif["certeza"]) {
                    "confirmado" -> VersaoRumor.EXATA
                    "alegado" -> VersaoRumor.EXAGERADA
                    else -> VersaoRumor.FALSA
                }
            val tipo = v["tipo"].toString()
            val entidade = v["entidade"].toString()
            Rumor(
                k, v["origem_delta"].toString(), tipo, entidade, camada, versao, (dif["passo"] as? Number)?.toInt() ?: 0, inicio,
                "$tipo: ${nome(
                    entidade,
                )}",
            )
        }.sortedWith(compareByDescending<Rumor> { it.inicio }.thenBy { it.ecoId }).take(max)
    }
}
