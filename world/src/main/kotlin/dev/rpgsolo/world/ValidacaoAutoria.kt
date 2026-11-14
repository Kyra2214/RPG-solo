package dev.rpgsolo.world

import java.io.File
import kotlin.system.exitProcess

/**
 * WD-01 (D-09): consumidor do [ValidadorAutoria]. Carrega o molde de Mythralis (classpath), soma os erros de carga
 * aos achados de autoria e devolve o relatório. Erro de carga ou achado `E` = falha (código de saída 1, o que
 * quebra o build quando a tarefa Gradle `:world:validarAutoria` roda dentro de `check`); aviso `A` só é listado.
 *
 * Uso: `ValidacaoAutoria [--relatorio=<arquivo>]`.
 */
object ValidacaoAutoria {
    class Resultado(val relatorio: String, val erros: Int, val avisos: Int) {
        val ok: Boolean get() = erros == 0
    }

    fun validar(fonte: MoldeFonte = MoldeFonteClasspath()): Resultado {
        val mundo = CarregadorMolde.carregar(fonte)
        val achados = ArrayList<Achado>()
        for (e in mundo.erros) achados.add(Achado("A-00", "E", "${e.pasta}/${e.tipo}", "erro de carga: ${e.mensagem}"))
        achados.addAll(ValidadorAutoria.validar(mundo))
        val (erros, avisos) = ValidadorMundo.resumo(achados)
        return Resultado(ValidadorAutoria.relatorio(achados), erros, avisos)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val resultado = validar()
        print(resultado.relatorio)
        args.firstOrNull { it.startsWith("--relatorio=") }?.substringAfter('=')?.takeIf { it.isNotBlank() }?.let { caminho ->
            val arquivo = File(caminho)
            arquivo.absoluteFile.parentFile?.mkdirs()
            arquivo.writeText(resultado.relatorio, Charsets.UTF_8)
        }
        if (!resultado.ok) exitProcess(1)
    }
}
