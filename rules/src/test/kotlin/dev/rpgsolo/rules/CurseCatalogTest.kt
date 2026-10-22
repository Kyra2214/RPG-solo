package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurseCatalogTest {
    private fun catalogText(): String =
        requireNotNull(javaClass.classLoader?.getResourceAsStream("catalogs/sistema-proprio/maldicoes-lancadas.txt")) {
            "catálogo de maldições lançadas não encontrado nos recursos de teste"
        }.bufferedReader(Charsets.UTF_8).use { it.readText() }

    @Test fun catalogoDeMaldicoesAplicadasCarregaSemErros() {
        val result = CurseCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        assertTrue("catálogo deve conter maldições reais, não apenas conceitos", result.curses.size >= 100)
        assertEquals(100, result.curses.size)
        assertEquals(result.curses.size, result.curses.map { it.id }.distinct().size)
    }

    @Test fun duracaoDosEfeitosEstaAlinhadaComADuracaoDaMaldicao() {
        val result = CurseCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        result.curses.forEach { curse ->
            curse.effects.forEach { effect ->
                assertEquals(
                    "efeito ${effect.id} da maldição ${curse.id} deve acompanhar a duração da maldição",
                    curse.durationRounds,
                    effect.durationRounds
                )
            }
            if (curse.durationRounds != null) {
                assertTrue("${curse.id} temporária precisa declarar resistência", curse.resistanceCheckId != null)
            }
        }
    }

    @Test fun efeitosDeMaldicaoNaoMisturamSinaisNoMesmoGrupoDeAcumulo() {
        val result = CurseCatalogLoader.load(catalogText())
        assertTrue(result.errors.joinToString("; "), result.valid)
        result.curses.forEach { curse ->
            curse.effects.groupBy { it.target to it.stackingGroup }.forEach { (key, effects) ->
                assertFalse(
                    "maldição ${curse.id} mistura bônus e penalidade no mesmo alvo/grupo: $key",
                    effects.any { it.baseValue > 0 } && effects.any { it.baseValue < 0 }
                )
            }
        }
    }

    @Test fun cadaMaldicaoDeclaraBeneficioEContrapartida() {
        val result = CurseCatalogLoader.load(catalogText())
        assertTrue(result.valid)
        result.curses.forEach { curse ->
            assertTrue("${curse.id} sem efeito positivo", curse.effects.any { it.baseValue > 0 })
            assertTrue("${curse.id} sem efeito negativo", curse.effects.any { it.baseValue < 0 })
            assertTrue("${curse.id} sem métodos de remoção", curse.removalMethods.isNotEmpty())
        }
    }
}
