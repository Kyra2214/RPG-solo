package dev.rpgsolo.world

import java.util.Locale

/** Resultado do check de saúde de um bloco (uma linha de `saude.txt`). */
class BlocoSaude(
    val numero: Int,
    val anoIni: Int,
    val anoFim: Int,
    val diaIni: Int,
    val diaFim: Int,
    val parcial: Boolean,
    val resultado: String,
    val deltas: Int,
    val notificacoes: Int,
    val alertas: List<VBloco>,
    val falhas: List<VBloco>,
    val hashCronica: String,
) {
    fun linha(): String = listOf(
        numero, anoIni, anoFim, diaIni, diaFim, if (parcial) "sim" else "não", resultado, deltas, notificacoes, alertas.size, falhas.size,
        hashCronica.take(16),
    ).joinToString("|")
}

/**
 * Ponto de retomada (D-I3): estado do mundo ao fim de um bloco sem falha, com o hash e a contagem da crônica.
 * O JSON usa o mesmo formato canônico do estado; a persistência em Room é do MI-10.
 */
class Snapshot(
    val dia: Int,
    val hashCronica: String,
    val deltas: Int,
    val notificacoes: Int,
    val ultimoDia: Int,
    val hashEstado: String,
    val estado: Map<String, Any?>,
) {
    fun paraJson(): String = JsonCanonico.canon(
        linkedMapOf(
            "dia" to dia, "hash_cronica" to hashCronica, "deltas" to deltas, "notificacoes" to notificacoes, "ultimo_dia" to ultimoDia,
            "hash_estado" to hashEstado, "estado" to estado,
        ),
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun deJson(texto: String): Snapshot {
            val m = JsonLeitor.objeto(texto)
            return Snapshot(
                Valores.numero(m["dia"]).toInt(), m["hash_cronica"].toString(), Valores.numero(m["deltas"]).toInt(),
                Valores.numero(m["notificacoes"]).toInt(), Valores.numero(m["ultimo_dia"]).toInt(), m["hash_estado"].toString(),
                m["estado"] as Map<String, Any?>,
            )
        }
    }
}

class ResultadoRodada(val resultado: String, val dia: Int, val deltas: Int, val notificacoes: Int, val blocos: Int, val hashCronica: String)

/**
 * Simulador (porta de `Simulador` de `simulador.py`): laço por dia, frequências, emissão de deltas, notificações por contador,
 * blocos de [passo] anos com check de saúde (invariantes por delta, por estado e por bloco) e retomada por [Snapshot].
 * O laço é sempre por dia: o bloco é só ponto de parada e conferência, então o passo não muda a crônica (I-22).
 */
class Simulador(
    val mundo: MundoMolde,
    val din: Dinamica,
    val semente: Long,
    motores: List<Motor>,
    hashDinamica: String,
    saida: CronicaSaida? = null,
    guardarDeltas: Boolean = true,
    passo: Int = 10,
    retomada: Snapshot? = null,
    private val conferirMolde: () -> List<VBloco> = { emptyList() },
    private val aoFecharBloco: ((BlocoSaude, Snapshot?) -> Unit)? = null,
) {
    val passo: Int = maxOf(1, passo)
    private val motores = motores.sortedWith { a, b -> Ordem.comparar(a.nome, b.nome) }
    private val nomesMotores: Set<String> = this.motores.map { it.nome }.toSet()
    private val porFreq: Map<Frequencia, List<Motor>> = Frequencia.values().toList().associateWith { f -> this.motores.filter { it.frequencia == f } }
    private val cals: Map<String, CalendarioReino?> = CalendarioReino.construir(mundo)
    val estado: Estado = if (retomada != null) Estado.doSnapshot(retomada.estado) else Estado.doMolde(mundo, din)
    private val cabecalho: Map<String, Any?> = linkedMapOf(
        "versao_motor" to VERSAO, "esquema_cronica" to 1, "mundo" to "mundo.mythralis", "semente" to semente,
        "hash_molde" to mundo.hashMolde, "hash_dinamica" to hashDinamica, "ano_base" to CalendarioReino.ANO_BASE,
    )
    val crono: Cronica = if (retomada != null) {
        Cronica.continuar(cabecalho, retomada.hashCronica, retomada.deltas, saida, guardarDeltas)
    } else {
        Cronica.criar(cabecalho, saida, guardarDeltas)
    }

    /** Notificações geradas (o contador define o id `nNNNNNN` gravado no delta). */
    var nNotificacoes = retomada?.notificacoes ?: 0
        private set

    /** Violações de formato/aplicação (`F-00`): delta recusado pelo estado. */
    val falhas = ArrayList<Triple<String, String, String>>()

    var resultado = "OK"
        private set
    var blocos = 0
        private set
    val violTotal = ArrayList<VBloco>()
    val alertTotal = ArrayList<VBloco>()
    val saude = ArrayList<BlocoSaude>()

    private var ultimoDia = retomada?.ultimoDia ?: 0
    private var blocoIni = 0
    private var nNotifIni = 0
    private var violBloco = ArrayList<VBloco>()
    private val deltasBloco = ArrayList<Map<String, Any?>>()
    private val enraizados = HashSet<String>()
    private val reinos = ReinosMapa(mundo, emptyList())

    init {
        if (retomada != null) {
            for ((k, e) in estado.ent) {
                if (k.startsWith("npc.") && e["origem_npc"] != null && e["reino"] != null && e["reino"] != "") reinos.m[k] = e["reino"].toString()
                if (k.startsWith("eco.")) reinos.eco[k] = e["reino"]?.toString()?.takeIf { it.isNotEmpty() } ?: "—"
            }
        }
    }

    private fun notificacaoValida(n: Any?): Boolean {
        val t = n as? String ?: return false
        val num = if (t.length >= 7 && t.startsWith("n")) t.drop(1).toIntOrNull() else null
        return num != null && num in 1..nNotificacoes && t == "n%06d".format(Locale.ROOT, num)
    }

    private fun emitir(dia: Int, dr: DeltaRascunho) {
        val e = estado
        val d = linkedMapOf<String, Any?>(
            "id" to "d%06d".format(Locale.ROOT, crono.total + 1), "dia_mundo" to dia, "era" to e.mundo["era"], "tipo" to dr.tipo,
            "entidade" to dr.entidade, "campo" to dr.campo, "estado_anterior" to e.estadoAnteriorDe(dr.tipo, dr.entidade, dr.campo),
            "estado_novo" to dr.estadoNovo, "causa" to dr.causa, "agentes" to dr.agentes.toMutableList(),
            "quem_sabe" to dr.quemSabe.toMutableList(), "certeza" to dr.certeza, "origem" to dr.origem,
        )
        val viol = Invariantes.checarDelta(d, e, ultimoDia, crono.total, mundo, din)
        d["importancia"] = if (dr.tipo.isNotEmpty() && dr.entidade.isNotEmpty()) Importancia.calcular(d, e) else 0
        try {
            e.aplicar(d)
        } catch (x: ErroEstado) {
            falhas.add(Triple("F-00", d["id"].toString(), x.message ?: ""))
            violBloco.add(VBloco("F-00", d["id"].toString(), x.message ?: ""))
            return
        }
        d["notificacao"] = if ((d["importancia"] as Int) >= Importancia.LIMIAR_NOTIFICACAO) "n%06d".format(Locale.ROOT, ++nNotificacoes) else null
        crono.anexar(d)
        deltasBloco.add(d)
        ultimoDia = dia
        for (v in viol) violBloco.add(VBloco(v.inv, d["id"].toString(), v.msg))
    }

    private fun dia(dia: Int) {
        for (f in frequencias(dia)) {
            for (m in porFreq.getValue(f)) {
                val rng = PythonRandom.doDia(semente.toString(), m.nome, dia)
                for (dr in m.executar(Ctx(dia, estado, rng, mundo))) emitir(dia, dr)
            }
        }
    }

    private fun abrirBloco(dia: Int) {
        blocoIni = dia
        deltasBloco.clear()
        nNotifIni = nNotificacoes
        violBloco = ArrayList()
    }

    private fun fecharBloco(diaFim: Int): String {
        val v = violBloco
        val bd: List<Map<String, Any?>> = deltasBloco
        for (d in bd) {
            if ((d["importancia"] as Int) >= Importancia.LIMIAR_NOTIFICACAO && !notificacaoValida(d["notificacao"])) {
                v.add(VBloco("I-21", d["id"].toString(), "delta de importância ${d["importancia"]} sem notificação"))
            }
        }
        v.addAll(conferirMolde())
        val al = ArrayList<VBloco>()
        fun separar(x: VBloco) = if (Invariantes.IMPLEMENTADOS[x.inv]?.first == "E") v.add(x) else al.add(x)
        for (x in Invariantes.checarEstado(estado, diaFim, nomesMotores)) separar(x)
        for (d in bd) {
            reinos.registrar(d)
            val ent = d["entidade"].toString()
            if (ent.startsWith("eco.") && ent in estado.ent) reinos.eco[ent] = estado.ent.getValue(ent)["reino"]?.toString()?.takeIf { it.isNotEmpty() } ?: "—"
        }
        val blocoDias = passo * CalendarioReino.DIAS_ANO
        val parcial = blocoIni % blocoDias != 0 || (diaFim + 1) % blocoDias != 0
        for (x in Invariantes.checarBloco(bd, estado, diaFim, parcial, cals, din.inv, reinos, mundo, enraizados)) separar(x)
        val res = if (v.isNotEmpty()) "FALHA" else if (al.isNotEmpty()) "ALERTA" else "OK"
        blocos++
        val b = BlocoSaude(
            blocoIni / blocoDias + 1, CalendarioReino.anoBase(blocoIni), CalendarioReino.anoBase(diaFim), blocoIni, diaFim, parcial, res,
            bd.size, nNotificacoes - nNotifIni, al, ArrayList(v), crono.hash,
        )
        saude.add(b)
        violTotal.addAll(v)
        alertTotal.addAll(al)
        resultado = res
        val cb = aoFecharBloco
        if (cb != null) {
            val snap = if (res != "FALHA") {
                @Suppress("UNCHECKED_CAST")
                Snapshot(
                    estado.proximoDia, crono.hash, crono.total, nNotificacoes, ultimoDia, estado.hash(),
                    Valores.copiar(estado.comoMapa()) as Map<String, Any?>,
                )
            } else {
                null
            }
            cb(b, snap)
        }
        return res
    }

    /** Avança do dia [estado.proximoDia] até [fim] (exclusive), em blocos; para no primeiro bloco com FALHA. */
    fun avancarAte(fim: Int): ResultadoRodada {
        val ini = estado.proximoDia
        val blocoDias = passo * CalendarioReino.DIAS_ANO
        abrirBloco(ini)
        if (crono.total == 0) {
            emitir(
                ini,
                DeltaRascunho(
                    "era", "mundo", estadoNovo = linkedMapOf("era" to Estado.ERA_INICIAL.first, "nome" to Estado.ERA_INICIAL.second, "regime" to Estado.ERA_INICIAL.third),
                    causa = causa(listOf("mundo.mythralis"), "inicio-da-rodada"),
                ),
            )
        }
        for (d in ini until fim) {
            dia(d)
            if ((d + 1) % blocoDias == 0 || d + 1 == fim) {
                estado.proximoDia = d + 1
                resultado = fecharBloco(d)
                if (resultado == "FALHA") break
                if (d + 1 < fim) abrirBloco(d + 1)
            }
        }
        return ResultadoRodada(resultado, estado.proximoDia, crono.total, nNotificacoes, blocos, crono.hash)
    }

    /** Simula [anos] anos a partir do dia atual (`int(round(anos * 365))` dias, arredondamento par como o Python). */
    fun rodar(anos: Double): ResultadoRodada = avancarAte(estado.proximoDia + Math.rint(anos * CalendarioReino.DIAS_ANO).toInt())

    fun rodar(anos: Int): ResultadoRodada = rodar(anos.toDouble())

    /** Texto de `invariantes.txt` da rodada (porta do começo de `_encerrar`). */
    fun relatorioInvariantes(): String {
        val linhas = arrayListOf("codigo|gravidade|situacao|detalhe")
        val por = LinkedHashMap<String, MutableList<String>>()
        for (x in violTotal + alertTotal) por.getOrPut(x.inv) { ArrayList() }.add("${x.entidade}: ${x.msg}")
        for ((cod, gn) in Invariantes.IMPLEMENTADOS.entries.sortedWith { a, b -> Ordem.comparar(a.key, b.key) }) {
            val (g, nome) = gn
            val p = por[cod]
            linhas.add(
                when {
                    cod in Invariantes.ENTRE_RODADAS -> "$cod|$g|fora-da-rodada|$nome; comparado entre rodadas em motor/testes/test_f3.py"
                    p != null -> "$cod|$g|${if (g == "E") "FALHOU" else "ALERTA"}|$nome (${p.size}); primeiro: ${p[0]}"
                    else -> "$cod|$g|ok|$nome"
                },
            )
        }
        for (f in por.keys.filter { it.startsWith("F-") }.sortedWith(Ordem.CODIGO)) {
            linhas.add("$f|E|FALHOU|formato/aplicação (${por.getValue(f).size}); primeiro: ${por.getValue(f)[0]}")
        }
        return linhas.joinToString("\n") + "\n"
    }

    companion object {
        const val VERSAO = "F8.2"

        /** Frequências que disparam num dia (porta de `frequencias`). */
        fun frequencias(dia: Int): List<Frequencia> {
            val f = arrayListOf(Frequencia.DIARIA)
            if (Math.floorMod(dia, 30) == 0) f.add(Frequencia.LUNAR)
            val doy = CalendarioReino.diaDoAno(dia)
            if (doy in setOf(1, 92, 183, 274)) f.add(Frequencia.SAZONAL)
            if (doy == 1) f.add(Frequencia.ANUAL)
            return f
        }
    }
}
