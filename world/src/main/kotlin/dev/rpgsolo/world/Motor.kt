package dev.rpgsolo.world

/** Frequências dos motores, na ordem em que o simulador as percorre em cada dia. */
enum class Frequencia { DIARIA, LUNAR, SAZONAL, ANUAL }

/** Contexto de um motor num dia (porta de `Ctx` de `simulador.py`). O estado é só leitura até `executar` devolver a lista. */
class Ctx(val diaMundo: Int, val estado: Estado, val rng: PythonRandom, val mundo: MundoMolde) {
    val diaDoAno: Int = CalendarioReino.diaDoAno(diaMundo)
    val ano: Int = CalendarioReino.anoBase(diaMundo)
    val era: Any? = estado.mundo["era"]
}

/** Delta proposto por um motor; o simulador preenche id, dia, era, estado anterior, origem, importância e notificação. */
class DeltaRascunho(
    val tipo: String,
    val entidade: String,
    val campo: String? = null,
    val estadoNovo: Any? = null,
    val causa: Map<String, Any?>? = null,
    val agentes: List<String> = emptyList(),
    val quemSabe: List<String> = emptyList(),
    val certeza: String = "confirmado",
    val origem: String = "SIMULADO",
)

/** Interface de um motor. `nome` é único: define a ordem alfabética de execução e a semente do gerador. */
interface Motor {
    val nome: String
    val frequencia: Frequencia

    fun executar(ctx: Ctx): List<DeltaRascunho>
}

/** `dict(ids=[...], regra='...')` dos motores. */
fun causa(
    ids: List<String>,
    regra: String,
): Map<String, Any?> = linkedMapOf("ids" to ids.toMutableList(), "regra" to regra)
