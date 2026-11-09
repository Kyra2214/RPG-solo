package dev.rpgsolo.ui

import dev.rpgsolo.world.BlocoSaude
import dev.rpgsolo.world.CarregadorMolde
import dev.rpgsolo.world.CronicaSaida
import dev.rpgsolo.world.Dinamica
import dev.rpgsolo.world.MoldeFonteClasspath
import dev.rpgsolo.world.MoldeManifesto
import dev.rpgsolo.world.Motores
import dev.rpgsolo.world.MundoMolde
import dev.rpgsolo.world.Simulador
import dev.rpgsolo.world.Snapshot
import dev.rpgsolo.world.texto

/**
 * Molde de Mythralis, dinâmica e motores carregados do classpath (APK), uma vez por processo (cerca de 130 arquivos:
 * rode fora da thread principal). É a [SimuladorFabrica] concreta do MI-15.
 */
object MythralisRuntime {
    private val fonte by lazy { MoldeFonteClasspath() }
    private val manifesto: MoldeManifesto by lazy {
        MoldeManifesto.ler(checkNotNull(fonte.texto("manifesto.txt")) { "molde sem manifesto.txt" })
    }
    val mundo: MundoMolde by lazy {
        CarregadorMolde.carregar(fonte, manifesto).also { m ->
            check(m.erros.isEmpty()) { "molde de Mythralis com ${m.erros.size} erro(s) de carga" }
        }
    }

    /** Dinâmica (tabelas de longevidade etc.) para o motor do herói (MI-26). */
    fun dinamica(): Dinamica = Dinamica(fonte)

    private val hashDinamica: String by lazy { Dinamica.hash(fonte, manifesto) }

    /** Fábrica do mundo de [semente]; não guarda deltas em memória (a crônica filtrada vai para o Room, D-I3). */
    fun fabrica(semente: Long): SimuladorFabrica =
        SimuladorFabrica { retomada: Snapshot?, saida: CronicaSaida, aoFecharBloco: (BlocoSaude, Snapshot?) -> Unit ->
            val din = Dinamica(fonte)
            Simulador(
                mundo = mundo,
                din = din,
                semente = semente,
                motores = Motores.todos(din),
                hashDinamica = hashDinamica,
                saida = saida,
                guardarDeltas = false,
                retomada = retomada,
                aoFecharBloco = aoFecharBloco,
            )
        }
}
