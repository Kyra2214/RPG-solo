package dev.rpgsolo.ui

import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.GameState
import dev.rpgsolo.engine.MarkHeroDead
import dev.rpgsolo.engine.RecordFact
import dev.rpgsolo.world.Dinamica
import dev.rpgsolo.world.EstadoHeroi
import dev.rpgsolo.world.MotorHeroi
import dev.rpgsolo.world.ResultadoHeroi

/**
 * Idade do herói (MI-26): nascimento no dia do mundo, classe de longevidade e saúde ficam como fatos da campanha
 * (`heroi.nasc_dia`, `heroi.longevidade`, `heroi.saude`), e a morte de velhice como `heroi.morte` (`<dia>:<causa>`).
 * 🔶 ASSUMINDO: fatos em vez de campo novo na entidade (o motor do jogo não guarda idade e os fatos já são desfeitos
 * pelo mesmo lote); a classe vem de quem chama (povo da ficha → `Dinamica.classeDe`); o herói só é simulado se tem
 * `heroi.nasc_dia`; a morte aqui só é registrada (a parada da tela e o legado são o MI-27).
 */
object IdadeHeroi {
    const val NASC = "heroi.nasc_dia"
    const val LONGEVIDADE = "heroi.longevidade"
    const val SAUDE = "heroi.saude"
    const val MORTE = "heroi.morte"

    /** Fatos que fixam a idade de [anos] no dia do mundo [diaMundo] (campo de idade da ficha e da criação). */
    fun definir(
        anos: Int,
        diaMundo: Int,
        classe: String,
    ): List<Command> {
        require(anos >= 0) { "idade negativa" }
        return listOf(
            RecordFact(NASC, (diaMundo - anos * MotorHeroi.DIAS_ANO).toString()),
            RecordFact(LONGEVIDADE, classe),
            RecordFact(SAUDE, "sao"),
        )
    }

    fun estado(state: GameState): EstadoHeroi? {
        val nasc = state.facts[NASC]?.toIntOrNull() ?: return null
        if (MORTE in state.facts) return null
        return EstadoHeroi(nasc, state.facts[LONGEVIDADE] ?: "humana", state.facts[SAUDE] == "doente")
    }

    fun idade(
        state: GameState,
        diaMundo: Int,
    ): Int? = state.facts[NASC]?.toIntOrNull()?.let { MotorHeroi.idade(it, diaMundo) }

    /** Fatos que gravam o resultado do período (saúde mudada e, se morreu, `heroi.morte`). */
    fun comandos(
        state: GameState,
        r: ResultadoHeroi,
    ): List<Command> {
        val out = ArrayList<Command>()
        val saude = if (r.doente) "doente" else "sao"
        if ((state.facts[SAUDE] ?: "sao") != saude) out += RecordFact(SAUDE, saude)
        if (r.morteDia != null) {
            state.entities.values.filter { it.kind == EntityKind.PLAYER && it.alive }.minByOrNull { it.id }
                ?.let { out += MarkHeroDead(it.id, r.causa) }
            out += RecordFact(MORTE, "${r.morteDia}:${r.causa}")
        }
        return out
    }

    /** MI-27: se o herói morre de velhice em (diaInicio, diaFim], devolve o dia e a causa; `null` = sobrevive ou não é simulado. */
    fun previsaoDeMorte(
        state: GameState,
        din: Dinamica,
        semente: Long,
        diaInicio: Int,
        diaFim: Int,
    ): MorteHeroi? {
        val h = estado(state) ?: return null
        val r = MotorHeroi.simular(din, h, semente, diaInicio, diaFim)
        return r.morteDia?.let { MorteHeroi(it, r.causa ?: "morte-natural") }
    }

    /** Texto do resumo da morte para a tela (idade e causa). */
    fun resumoDaMorte(
        state: GameState,
        morte: MorteHeroi,
    ): String {
        val idade = idade(state, morte.dia)?.let { ", aos $it anos" } ?: ""
        val causa = if (morte.causa.endsWith("-doente")) "velhice, depois de adoecer" else "velhice"
        return "Seu herói morreu de $causa$idade, em ${Periodos.data(morte.dia)}."
    }
}
