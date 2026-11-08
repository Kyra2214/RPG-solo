package dev.rpgsolo.world

/** Destino das linhas da crônica (arquivo, Room ou nada). A crônica completa não precisa ficar em memória (D-I3). */
fun interface CronicaSaida {
    fun linha(texto: String)
}

class ErroCronica(mensagem: String) : Exception(mensagem)

/**
 * Crônica de Mythralis: registro append-only de deltas com cadeia de hash (porta de `cronica.py`).
 * Formato das linhas (jsonl): `{"cabecalho":{...}}` e depois `{"delta":{...},"h":"<sha256 encadeado>"}`.
 */
class Cronica private constructor(
    val cabecalho: Map<String, Any?>,
    private val saida: CronicaSaida?,
    private val guardar: Boolean,
) {
    val deltas = ArrayList<Map<String, Any?>>()
    var hash: String = Hash.sha256(JsonCanonico.canon(mapOf("cabecalho" to cabecalho)))
        private set

    /** Total de deltas anexados (também quando [guardar] é falso). */
    var total: Int = 0
        private set

    fun anexar(delta: Map<String, Any?>) {
        val h = Hash.encadear(hash, delta)
        saida?.linha(JsonCanonico.canon(mapOf("delta" to delta, "h" to h)))
        hash = h
        total++
        if (guardar) deltas.add(delta)
    }

    companion object {
        /** Cria uma crônica nova e escreve a linha do cabeçalho na saída. */
        fun criar(
            cabecalho: Map<String, Any?>,
            saida: CronicaSaida? = null,
            guardar: Boolean = true,
        ): Cronica {
            val c = Cronica(LinkedHashMap(cabecalho), saida, guardar)
            saida?.linha(JsonCanonico.canon(mapOf("cabecalho" to c.cabecalho)))
            return c
        }

        /** Continua uma crônica já conferida (retomada por snapshot): hash e contagem vêm do snapshot; não escreve o cabeçalho de novo. */
        fun continuar(
            cabecalho: Map<String, Any?>,
            hash: String,
            total: Int,
            saida: CronicaSaida? = null,
            guardar: Boolean = true,
        ): Cronica {
            val c = Cronica(LinkedHashMap(cabecalho), saida, guardar)
            c.hash = hash
            c.total = total
            return c
        }

        /** Reabre a partir do texto jsonl, conferindo a cadeia inteira (como `Cronica.abrir` do Python). */
        fun abrir(
            texto: String,
            saida: CronicaSaida? = null,
            guardar: Boolean = true,
        ): Cronica {
            val linhas = texto.split('\n').toMutableList()
            if (linhas.isNotEmpty() && linhas.last().isEmpty()) linhas.removeAt(linhas.size - 1)
            if (linhas.isEmpty()) throw ErroCronica("crônica vazia")
            val cab = JsonLeitor.objeto(linhas[0])
            if ("cabecalho" !in cab) throw ErroCronica("falta o cabeçalho na 1ª linha")
            @Suppress("UNCHECKED_CAST")
            val c = Cronica(cab.getValue("cabecalho") as Map<String, Any?>, saida, guardar)
            var h = c.hash
            for (n in 1 until linhas.size) {
                val r = JsonLeitor.objeto(linhas[n])

                @Suppress("UNCHECKED_CAST")
                val delta = r.getValue("delta") as Map<String, Any?>
                h = Hash.encadear(h, delta)
                if (r["h"] != h) throw ErroCronica("cadeia de hash quebrada na linha ${n + 1}")
                if (guardar) c.deltas.add(delta)
                c.total++
            }
            c.hash = h
            return c
        }
    }
}
