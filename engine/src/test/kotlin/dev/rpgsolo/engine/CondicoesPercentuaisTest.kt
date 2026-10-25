package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-08: dano por rodada de condição em % do máximo do recurso, arredondado para cima, mínimo 1. */
class CondicoesPercentuaisTest {
    private fun session(max: Int) =
        CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "pv")),
        ).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(max, max)))),
                    CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(30, 30)))),
                ),
            )
        }

    private fun pv(s: CampaignSession) = s.state.entities.getValue("hero").resources.getValue("pv").current

    private fun rodada(s: CampaignSession) {
        s.execute(NextTurn)
        s.execute(NextTurn)
    }

    @Test fun tickDamageArredondaParaCimaComMinimoUm() {
        assertEquals(3, ConditionState(3, damagePercent = 10).tickDamage(30))
        assertEquals(2, ConditionState(3, damagePercent = 5).tickDamage(30))
        assertEquals(5, ConditionState(2, damagePercent = 15).tickDamage(30))
        assertEquals(1, ConditionState(3, damagePercent = 5).tickDamage(5))
        assertEquals(4, ConditionState(3, damagePerRound = 4).tickDamage(100))
    }

    @Test fun percentualEhAplicadoNoFimDaRodada() {
        val s = session(30)
        assertTrue(s.execute(AddCondition("hero", "envenenado", 3, 0, "pv", 5)) is ExecResult.Accepted)
        s.execute(StartCombat(listOf("hero", "orc")))
        rodada(s)
        assertEquals(28, pv(s))
        rodada(s)
        assertEquals(26, pv(s))
    }

    @Test fun maximoPequenoCausaPeloMenosUm() {
        val s = session(5)
        s.execute(AddCondition("hero", "envenenado", 3, 0, "pv", 5))
        s.execute(StartCombat(listOf("hero", "orc")))
        rodada(s)
        assertEquals(4, pv(s))
    }

    @Test fun percentualInvalidoERecusado() {
        val s = session(30)
        assertTrue(s.execute(AddCondition("hero", "envenenado", 3, 0, "pv", 101)) is ExecResult.Rejected)
        assertTrue(s.execute(AddCondition("hero", "envenenado", 3, 0, "pv", -1)) is ExecResult.Rejected)
    }

    @Test fun danoFixoAntigoContinuaFuncionando() {
        val s = session(30)
        s.execute(AddCondition("hero", "envenenado", 3, 2, "pv"))
        s.execute(StartCombat(listOf("hero", "orc")))
        rodada(s)
        assertEquals(28, pv(s))
    }
}
