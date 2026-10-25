package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-36: marcas do Tatuador (limite por grau, bônus por grau, PV +5, cobertura por grau maior, preço). */
class TatuadorN36Test {
    private fun engine(tattooGrade: Int = 0): GameEngine {
        val e = GameEngine(5)
        val levels = if (tattooGrade > 0) mapOf("${Tattoos.SKILL_PREFIX}$tattooGrade" to 1) else emptyMap()
        e.execute(
            CreateEntity(
                Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)), skillTreeLevels = levels),
            ),
        )
        return e
    }

    private fun rejected(r: ExecResult) = r is ExecResult.Rejected

    @Test fun bonusEPrecoPorGrau() {
        assertEquals(300, Tattoos.price(3))
        assertEquals(3, Tattoos.bonusOf(Tattoo("ataque", 3)))
        assertEquals(15, Tattoos.bonusOf(Tattoo("max_pv", 3)))
        assertEquals(0, Tattoos.grade(emptyMap()))
        assertEquals(3, Tattoos.grade(mapOf("profissao-tatuador-1" to 1, "profissao-tatuador-3" to 1)))
        assertEquals(1, Tattoos.limit(emptyMap()))
    }

    @Test fun marcaSomaBonusNoStatus() {
        val e = engine(2)
        assertTrue(e.execute(ApplyTattoo("hero", "ataque", 2)) is ExecResult.Accepted)
        assertTrue(e.execute(ApplyTattoo("hero", "ataque", 1)) is ExecResult.Accepted)
        assertEquals(3, Tattoos.bonus(e.state.tattoos["hero"], "ataque"))
        assertEquals(0, Tattoos.bonus(e.state.tattoos["hero"], "defesa"))
    }

    @Test fun limiteUmaMarcaPorGrau() {
        val e = engine(2)
        assertTrue(e.execute(ApplyTattoo("hero", "ataque", 1)) is ExecResult.Accepted)
        assertTrue(e.execute(ApplyTattoo("hero", "defesa", 1)) is ExecResult.Accepted)
        assertTrue(rejected(e.execute(ApplyTattoo("hero", "velocidade", 1))))
        assertEquals(2, e.state.tattoos.getValue("hero").size)
    }

    @Test fun semProfissaoTemUmaMarca() {
        val e = engine(0)
        assertTrue(e.execute(ApplyTattoo("hero", "ataque", 1)) is ExecResult.Accepted)
        assertTrue(rejected(e.execute(ApplyTattoo("hero", "defesa", 1))))
    }

    @Test fun coberturaExigeGrauMaior() {
        val e = engine(1)
        e.execute(ApplyTattoo("hero", "ataque", 2))
        assertTrue(rejected(e.execute(ApplyTattoo("hero", "defesa", 2, coverIndex = 0))))
        assertTrue(rejected(e.execute(ApplyTattoo("hero", "defesa", 3, coverIndex = 5))))
        assertTrue(e.execute(ApplyTattoo("hero", "defesa", 3, coverIndex = 0)) is ExecResult.Accepted)
        assertEquals(listOf(Tattoo("defesa", 3)), e.state.tattoos["hero"])
    }

    @Test fun pvSobeCincoPorGrauECobrirTiraOAntigo() {
        val e = engine(2)
        e.execute(ApplyTattoo("hero", "max_pv", 2))
        assertEquals(Resource(20, 20), e.state.entities.getValue("hero").resources.getValue("pv"))
        e.execute(ApplyTattoo("hero", "ataque", 3, coverIndex = 0))
        assertEquals(10, e.state.entities.getValue("hero").resources.getValue("pv").max)
    }

    @Test fun recusasBasicas() {
        val e = engine(1)
        assertTrue(rejected(e.execute(ApplyTattoo("hero", "sorte", 1))))
        assertTrue(rejected(e.execute(ApplyTattoo("hero", "ataque", 0))))
        assertTrue(rejected(e.execute(ApplyTattoo("hero", "ataque", 6))))
        assertTrue(rejected(e.execute(ApplyTattoo("nobody", "ataque", 1))))
    }
}
