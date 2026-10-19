package dev.rpgsolo.rules

import dev.rpgsolo.engine.FortuneMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FortuneModePackageTest {
    private fun files(): MutableMap<String, String> {
        val root = listOf("src/test/resources/packages/teste-basico", "rules/src/test/resources/packages/teste-basico")
            .map(::File).first { it.exists() }
        return root.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()
    }

    @Test fun packageCanDefineOptionalResourceCostForFortune() {
        val files = files()
        val rows = files.getValue("testes.txt").lines().map { line ->
            if (line.isBlank() || line.startsWith("#")) line
            else {
                val fields = line.split("|").toMutableList()
                if (fields.firstOrNull() == "ataque") {
                    while (fields.size < 15) fields += "-"
                    fields[11] = "ADVANTAGE"
                    fields[12] = "false"
                    fields[13] = "mana"
                    fields[14] = "1"
                    fields.joinToString("|")
                } else line
            }
        }
        files["testes.txt"] = rows.joinToString("\n")
        val parsed = PackageParser.parse(files)
        assertTrue("pacote deveria ser aceito: $parsed", parsed is ParseResult.Ok)
        val data = (parsed as ParseResult.Ok).data
        val installed = PackageRegistry().install(files)
        assertTrue("pacote deveria instalar: $installed", installed is InstallResult.Success)
        val attack = RulePackage(data).data.checks.single { it.id == "ataque" }
        assertEquals("mana", attack.fortuneResource)
        assertEquals(1, attack.fortuneCost)
    }

    @Test fun packageCanDeclareExplodingDiceAfterFortuneMode() {
        val files = files()
        val rows = files.getValue("testes.txt").lines().map { line ->
            if (line.isBlank() || line.startsWith("#")) line
            else {
                val fields = line.split("|").toMutableList()
                if (fields.firstOrNull() == "ataque") {
                    while (fields.size < 12) fields += "-"
                    if (fields.size == 12) fields += "true" else fields[12] = "true"
                    fields.joinToString("|")
                } else line
            }
        }
        files["testes.txt"] = rows.joinToString("\n")
        val parsed = PackageParser.parse(files)
        assertTrue("pacote deveria ser aceito: $parsed", parsed is ParseResult.Ok)
        val data = (parsed as ParseResult.Ok).data
        val attack = RulePackage(data).check("ataque", emptyMap(), 12)
        assertTrue(attack.explodeOnMax)
    }

    @Test fun packageCanDeclareAdvantageWithoutBreakingOldRows() {
        val files = files()
        val rows = files.getValue("testes.txt").lines().map { line ->
            if (line.isBlank() || line.startsWith("#")) line
            else {
                val fields = line.split("|").toMutableList()
                if (fields.firstOrNull() == "ataque") {
                    while (fields.size < 11) fields += "-"
                    fields += "ADVANTAGE"
                    fields.joinToString("|")
                } else line
            }
        }
        files["testes.txt"] = rows.joinToString("\n")
        val parsed = PackageParser.parse(files)
        assertTrue("pacote deveria ser aceito: $parsed", parsed is ParseResult.Ok)
        val data = (parsed as ParseResult.Ok).data
        val attack = RulePackage(data).check("ataque", emptyMap(), 12)
        assertEquals(FortuneMode.ADVANTAGE, attack.fortuneMode)
    }
}
