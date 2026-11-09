package dev.rpgsolo.world

/** Marco (nível 1) do feed: [ano] é o ano base; [reinos] e [cidade] já vêm com o nome para mostrar. */
data class EventoFeed(val dia: Int, val ano: Int, val reinos: String, val cidade: String?, val texto: String)

/** Resumo de nível 2 por década e reino ([decada] conta a partir do ano 0 do mundo; ano = [decada] × 10). */
data class ResumoDecada(val decada: Int, val reino: String, val eventos: Int)

/** Resumo final do período (MI-16). Mortos e empossados contam NPCs de nível 1 ou 2, sem repetir o mesmo NPC. */
data class ResumoPeriodo(
    val marcos: Int,
    val reinosCaidos: List<String>,
    val pactosNovos: Int,
    val pactosDesfeitos: Int,
    val npcsMortos: Int,
    val npcsEmpossados: Int,
    val eventosNivel3: Int,
)

data class FeedSimulacao(val marcos: List<EventoFeed>, val decadas: List<ResumoDecada>, val resumo: ResumoPeriodo) {
    companion object {
        val VAZIO = FeedSimulacao(emptyList(), emptyList(), ResumoPeriodo(0, emptyList(), 0, 0, 0, 0, 0))
    }
}

/**
 * Acumula o feed e o resumo de um período a partir dos deltas, bloco a bloco (MI-16): nível 1 vira marco ao vivo,
 * nível 2 vira contagem por década e reino, nível 3 só conta (D-M14, §4). Usa as mesmas regras de `gerar_niveis`
 * ([ResumoNiveis]): marco = nível 1, importância 3+ e entidade que não seja `mundo`; nível 2 e 3 = importância 2+.
 * 🔶 ASSUMINDO: "pacto novo" = delta `alianca_de_povos` de entidade `pacto.*`; "reino caído" = `queda_de_coroa`.
 */
class AcumuladorFeed(private val w: MundoMolde, private val niveis: Niveis) {
    private val nomeReino = w.reinos.mapValues { it.value.linha.getValue("nome") }
    private val nomeCidade = w.t.getValue("cidade").associate { it.getValue("id") to it.getValue("nome").split(" — ")[0] }
    private val marcos = ArrayList<EventoFeed>()
    private val porDecada = LinkedHashMap<Pair<Int, String>, MutableSet<String>>()
    private val caidos = ArrayList<String>()
    private val mortos = HashSet<String>()
    private val empossados = HashSet<String>()
    private var pactosNovos = 0
    private var pactosDesfeitos = 0
    private var nivel3 = 0

    fun registrar(
        d: Map<String, Any?>,
        nivel: Int,
    ) {
        val imp = ResumoNiveis.importancia(d)
        val dia = ResumoNiveis.dia(d)
        val ano = CalendarioReino.anoBase(dia)
        val tipo = d["tipo"].toString()
        val e = d["entidade"].toString()
        if (nivel == 1 && imp >= 3 && e != "mundo") {
            val rs = niveis.reinos(d)
            marcos +=
                EventoFeed(
                    dia, ano,
                    rs.joinToString(", ") {
                        nomeReino[it] ?: it
                    },
                    niveis.cidade(d)?.let { nomeCidade[it] ?: it }, ResumoNiveis.texto(d),
                )
        }
        if (nivel == 2 && imp >= 2) {
            val r = niveis.reinos(d).first()
            val dec = Math.floorDiv(ano - CalendarioReino.ANO_BASE, 10)
            porDecada.getOrPut(dec to r) { LinkedHashSet() }.add(ResumoNiveis.evento(d))
        }
        if (nivel == 3 && imp >= 2) nivel3++
        when {
            tipo == "queda_de_coroa" -> niveis.reinos(d).first().let { caidos += nomeReino[it] ?: it }
            tipo == "alianca_de_povos" && e.startsWith("pacto.") -> pactosNovos++
            tipo == "fim_de_alianca_de_povos" && e.startsWith("pacto.") -> pactosDesfeitos++
            tipo == "campo" && d["campo"] == "vivo" && e.startsWith("npc.") && nivel <= 2 -> mortos += e
            tipo == "campo" && d["campo"] in POSSE && nivel <= 2 -> (d["agentes"] as? List<*>)?.forEach { empossados += it.toString() }
        }
    }

    fun feed(): FeedSimulacao =
        FeedSimulacao(
            marcos.toList(),
            porDecada.entries.sortedWith(
                compareBy({
                    it.key.first
                }, { it.key.second }),
            ).map { ResumoDecada(it.key.first, nomeReino[it.key.second] ?: it.key.second, it.value.size) },
            ResumoPeriodo(marcos.size, caidos.toList(), pactosNovos, pactosDesfeitos, mortos.size, empossados.size, nivel3),
        )

    private companion object {
        val POSSE = setOf("titular", "governante", "lideranca")
    }
}
