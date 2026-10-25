package dev.rpgsolo.rules

import dev.rpgsolo.engine.SystemRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** N-27: categorias de ameaça (×0,5, ×1, ×1,5, ×2, ×3 sobre o PV) e teto de PV sem pacote. */
class AmeacaN27Test {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    private fun proposal(
        threat: ThreatCategory,
        resources: Map<String, Int> = emptyMap(),
        level: Int = 1,
    ) = NpcProposal(
        "m1", "Lobo", "fera",
        mapOf(
            "resistencia" to 4,
        ),
        emptyList(), resources, RuleSource.PACOTE, threat = threat, level = level,
    )

    @Test fun multiplicadoresDaCategoria() {
        assertEquals(listOf(10, 20, 30, 40, 60), ThreatCategory.entries.map { it.scale(20) })
        assertEquals(1, ThreatCategory.CAPANGA.scale(1))
        assertEquals(8, ThreatCategory.ELITE.scale(5))
        assertEquals(ThreatCategory.CHEFE_FINAL, ThreatCategory.parse("chefe_final"))
        assertNull(ThreatCategory.parse("lendario"))
    }

    @Test fun pacoteEscalaOPvPelaCategoria() {
        val p = pkg()
        val hp = requireNotNull(p.manifest.deathResource)

        fun max(c: ThreatCategory) = (NpcFactory.create(proposal(c), p) as NpcResult.Created).sheet.ficha.resources.getValue(hp)
        val comum = max(ThreatCategory.COMUM)
        assertEquals(comum.max, 5 * 4)
        assertEquals(comum.max * 3, max(ThreatCategory.CHEFE_FINAL).max)
        assertEquals(comum.max / 2, max(ThreatCategory.CAPANGA).max)
        assertEquals(max(ThreatCategory.CHEFE).max, max(ThreatCategory.CHEFE).current)
    }

    @Test fun semPacoteLimitaPvPorNivelECategoria() {
        val ref = SystemRef("externo", "externo-1")

        fun pv(
            level: Int,
            c: ThreatCategory,
            v: Int,
        ) = (NpcFactory.create(proposal(c, mapOf("pv" to v), level), null, ref) as NpcResult.Created).sheet
        assertEquals(15, ThreatCategory.healthCap(1, ThreatCategory.COMUM))
        assertEquals(45, ThreatCategory.healthCap(1, ThreatCategory.CHEFE_FINAL))
        val clipped = pv(1, ThreatCategory.COMUM, 100)
        assertEquals(15, clipped.ficha.resources.getValue("pv").max)
        assertTrue(clipped.warnings.any { "teto" in it })
        assertEquals(30, pv(5, ThreatCategory.COMUM, 30).ficha.resources.getValue("pv").max)
        assertEquals(35, pv(5, ThreatCategory.COMUM, 35).ficha.resources.getValue("pv").max)
    }

    @Test fun semPacoteRecusaRecursoNegativo() {
        val r = NpcFactory.create(proposal(ThreatCategory.COMUM, mapOf("pv" to -1)), null, SystemRef("externo", "externo-1"))
        assertTrue(r is NpcResult.Rejected)
    }
}
