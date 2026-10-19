package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AdvancementPackageTest {
    @Test fun packageConfiguresResourceRewardsPerLevel() {
        val root = listOf("src/test/resources/packages/teste-basico", "rules/src/test/resources/packages/teste-basico")
            .map(::File).first { it.exists() }
        val files = root.listFiles()!!.filter { it.name.endsWith(".txt") }
            .associate { it.name to it.readText() }.toMutableMap()
        files["criacao.txt"] = files.getValue("criacao.txt").trimEnd() + "\nrecompensa_recurso|mana=2,hp=1\n"
        val parsed = PackageParser.parse(files)
        assertTrue("pacote deveria ser válido: $parsed", parsed is ParseResult.Ok)
        val pkg = RulePackage((parsed as ParseResult.Ok).data)
        assertEquals(mapOf("mana" to 2, "hp" to 1), pkg.engineConfig().levelResourceRewards)
        val installed = PackageRegistry().install(files)
        assertTrue("recompensas devem apontar a recursos existentes: $installed", installed is InstallResult.Success)
    }

    @Test fun packageControlsPointBudgetAndCaps() {
        val root = listOf("src/test/resources/packages/teste-basico", "rules/src/test/resources/packages/teste-basico")
            .map(::File).first { it.exists() }
        val files = root.listFiles()!!.filter { it.name.endsWith(".txt") }
            .associate { it.name to it.readText() }.toMutableMap()
        files["criacao.txt"] = files.getValue("criacao.txt").trimEnd() + "\npontos_por_nivel|2\n"
        val parsed = PackageParser.parse(files)
        assertTrue("pacote deveria ser válido: $parsed", parsed is ParseResult.Ok)
        val pkg = RulePackage((parsed as ParseResult.Ok).data)
        val config = pkg.engineConfig()
        assertEquals(2, config.advancementPointsPerLevel)
        assertTrue(config.statCaps.isNotEmpty())
        assertEquals(config.statCaps.keys, config.statDefaults.keys)
    }
}
