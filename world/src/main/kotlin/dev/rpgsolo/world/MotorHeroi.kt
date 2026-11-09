package dev.rpgsolo.world

/** Estado do herói que o motor precisa: nascimento no dia do mundo, classe de longevidade do povo e saúde. */
class EstadoHeroi(
    val nascDia: Int,
    val classe: String,
    val doente: Boolean = false,
)

/** O que o motor achou no período: saúde ao fim, dia da doença (se adoeceu) e dia e causa da morte (se morreu). */
class ResultadoHeroi(
    val doente: Boolean,
    val doencaDia: Int?,
    val morteDia: Int?,
    val causa: String?,
) {
    val morreu: Boolean get() = morteDia != null
}

/**
 * Motor `heroi` (MI-26, D-I5): o herói envelhece, adoece pela idade e pode morrer de velhice, pela longevidade do povo
 * (mesmas tabelas e fórmulas do motor de demografia). Gerador próprio e separado do mundo: **não entra no estado nem
 * nos hashes do mundo**, então a igualdade com o Python (D-I2) não muda. Anual, nos marcos de ano do mundo (dia múltiplo
 * de 365); o sorteio de cada ano depende só da semente e do número do ano, então a mesma semente dá o mesmo resultado
 * mesmo se o período for aplicado em partes.
 * 🔶 ASSUMINDO: o herói é avaliado a cada marco de ano dentro de (diaInicio, diaFim]; classe sem idade (`sem_idade`) não
 * envelhece; a semente do sorteio é `semente * 1000003 + ano * 7919 + 4845`.
 */
object MotorHeroi {
    const val DIAS_ANO = 365

    fun idade(
        nascDia: Int,
        dia: Int,
    ): Int = Math.floorDiv(dia - nascDia, DIAS_ANO)

    fun simular(
        din: Dinamica,
        heroi: EstadoHeroi,
        semente: Long,
        diaInicio: Int,
        diaFim: Int,
    ): ResultadoHeroi {
        var doente = heroi.doente
        var doencaDia: Int? = null
        if (din.vazia || diaFim <= diaInicio) return ResultadoHeroi(doente, null, null, null)
        val fd = din.lim["fator_mortalidade_doente"] ?: 4
        var ano = Math.floorDiv(diaInicio, DIAS_ANO) + 1
        while (ano.toLong() * DIAS_ANO <= diaFim) {
            val dia = ano * DIAS_ANO
            val rng = PythonRandom(semente * 1_000_003L + ano * 7919L + 4845L)
            val idade = idade(heroi.nascDia, dia)
            var p = din.mortePm(heroi.classe, idade)
            if (doente) p = minOf(1000, p * fd)
            if (rng.randrange(1000) < p) {
                return ResultadoHeroi(doente, doencaDia, dia, "morte-natural" + if (doente) "-doente" else "")
            }
            if (doente) {
                if (rng.randrange(1000) < (din.lim["recuperacao_pm"] ?: 350)) doente = false
            } else if (din.eIdoso(heroi.classe, idade) && rng.randrange(1000) < (din.lim["doenca_idosa_pm"] ?: 40)) {
                doente = true
                doencaDia = dia
            }
            ano++
        }
        return ResultadoHeroi(doente, doencaDia, null, null)
    }
}
