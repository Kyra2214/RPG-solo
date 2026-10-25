package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-33.4: vagas de gema por qualidade da peça e os 12 itens de gema do pacote próprio. */
class GemasSlotsN33Test {
    private fun dir(path: String): File = listOf("src/$path", "rules/src/$path").map(::File).first { it.exists() }

    private fun pkg(): RulePackage {
        val files =
            dir("main/resources/packages/sistema-proprio").listFiles()!!.filter { it.name.endsWith(".txt") }
                .associate { it.name to it.readText() }
        return when (val result = PackageRegistry().install(files)) {
            is InstallResult.Success -> result.pkg
            is InstallResult.Failure -> throw AssertionError("pacote inválido: ${result.errors.joinToString("; ")}")
        }
    }

    @Test fun vagasPorQualidade() {
        assertEquals(1, ItemQuality.NORMAL.gemSockets)
        assertEquals(1, ItemQuality.OTIMA.gemSockets)
        assertEquals(2, ItemQuality.RARA.gemSockets)
        assertEquals(2, ItemQuality.EPICA.gemSockets)
        assertEquals(3, ItemQuality.LENDARIA.gemSockets)
        assertEquals(3, ItemRef.quality("espada-curta#lendaria")?.gemSockets)
    }

    @Test fun pacoteTemOsDozeItensDeGema() {
        val p = pkg()
        val ids = p.data.items.map { it.id }.filter { it.startsWith("gema-") }.toSet()
        val expected = listOf("ataque", "defesa", "velocidade", "pv").flatMap { t -> (1..3).map { "gema-$t-$it" } }.toSet()
        assertEquals(expected, ids)
        assertTrue(ids.all { dev.rpgsolo.engine.Gems.parse(it) != null })
    }

    @Test fun mineradorEGeologoExtraemGemasBrutas() {
        val p = pkg()
        val ids = listOf("ataque", "defesa", "velocidade", "pv").map { "col-extrair-gema-$it" }
        ids.forEach { id ->
            val r = requireNotNull(p.data.recipes.firstOrNull { it.id == id }) { "receita $id" }
            assertTrue(r.inputs.isEmpty())
            val out = r.outputs.keys.single()
            assertTrue(dev.rpgsolo.engine.Gems.parse(out)?.quality == 1)
        }
    }
}
