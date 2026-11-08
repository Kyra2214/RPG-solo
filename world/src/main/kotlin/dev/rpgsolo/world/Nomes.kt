package dev.rpgsolo.world

import java.util.Locale

/**
 * Nomes de NPCs simulados, pelo padrão de M-10 §1 (porta de `nomes.py`): nome curto de 1 a 3 sílabas + epíteto com hífen.
 * Nunca repete nome completo nem primeiro nome do molde (M-10 §2.1).
 */
object Nomes {
    private val SILABAS =
        listOf(
            "ba", "ce", "da", "ei", "fi", "ga", "ha", "il", "ju", "ka", "le", "ma", "ne", "or", "pa", "re", "sa", "ta", "ul", "ve", "zo",
        )
    private val ESPACOS = Regex("\\s+")

    private fun nomes(estado: Estado): List<String> =
        estado.ent.values.mapNotNull { (it["nome"] as? String)?.takeIf { n -> n.isNotEmpty() } }

    private fun primeiro(n: String): String = n.trim().split(ESPACOS).first()

    private fun epitetos(estado: Estado): List<String> =
        nomes(estado).filter { ' ' in it }.map { it.substringAfter(' ') }.toSet().sortedWith(Ordem.CODIGO)

    private fun capitalizar(s: String): String = s.take(1).uppercase() + s.drop(1).lowercase()

    fun gerar(
        rng: PythonRandom,
        estado: Estado,
        extras: List<String> = emptyList(),
    ): String {
        val comp = nomes(estado).toMutableSet().apply { addAll(extras) }
        val prim = (nomes(estado).map { primeiro(it) } + extras.map { primeiro(it) }).toSet()
        val ep = epitetos(estado)
        repeat(200) {
            val n = rng.randint(1, 3)
            val pnome = capitalizar((0 until n).joinToString("") { rng.choice(SILABAS) })
            val a = rng.choice(ep)
            val b = rng.choice(ep)
            val epNovo = if ('-' in a && '-' in b) a.substringBefore('-') + "-" + b.substringAfterLast('-') else a
            val nome = "$pnome $epNovo"
            if (pnome !in prim && nome !in comp) return nome
        }
        return "Anônimo %05d".format(Locale.ROOT, comp.size + 1)
    }
}
