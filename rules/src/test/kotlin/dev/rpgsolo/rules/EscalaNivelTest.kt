package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/** Bônus base por faixa de nível (coluna `escala` de itens.txt): ⌈nível ÷ 10⌉, ×5 em max_pv; a qualidade multiplica. */
class EscalaNivelTest {
    private fun pkg(): RulePackage {
        val f =
            listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
                .map(::File).first { it.exists() }
                .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()
        f["itens.txt"] = f.getValue("itens.txt").trimEnd('\n') + "\n" +
            "elmo-de-prata|Elmo de prata|equipamento|elmo|Elmo de prata batida.||40|20|||||||sorte\n" +
            "peito-de-prata|Peitoral de prata|equipamento|peito|Peitoral de prata batida.||90|30|||||||max_pv\n"
        return (PackageRegistry().install(f) as InstallResult.Success).pkg
    }

    @Test fun bonusBasePorFaixaDeNivel() {
        val p = pkg()
        assertEquals(2, p.equipmentBonus(listOf("elmo-de-prata"), "sorte"))
        assertEquals(15, p.equipmentBonus(listOf("peito-de-prata"), "max_pv"))
        assertEquals(0, p.equipmentBonus(listOf("elmo-de-prata"), "destreza"))
    }

    @Test fun qualidadeMultiplicaOBonusDeNivel() {
        val p = pkg()
        assertEquals(4, p.equipmentBonus(listOf("elmo-de-prata#epica"), "sorte"))
        assertEquals(30, p.equipmentBonus(listOf("peito-de-prata#epica"), "max_pv"))
    }

    @Test fun itemComEfeitoDeclaradoNaoEscala() {
        assertEquals(1, pkg().equipmentBonus(listOf("capacete"), "sorte"))
    }
}
