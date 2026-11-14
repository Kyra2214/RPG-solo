package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Perícia só ganha ponto pela prova dos níveis múltiplos de 5; mobs e XP comum não dão ponto. */
class ProvaDePericiaTest {
    private fun sessao(): CampaignSession {
        val config =
            EngineConfig(
                levelThresholds = (2..12).map { it to (it - 1) * 10 }.map { it.second },
                skillCaps = mapOf("atletismo" to 5),
                skillTrialEvery = 5,
            )
        val s = CampaignSession.create(InMemoryEventStore(), CampaignRecord("pr", "Prova", 9L, config))
        s.execute(CreateEntity(Entity(id = "hero", name = "Herói", kind = EntityKind.PLAYER)))
        return s
    }

    @Test fun xpENivelNaoDaoPontoDePericia() {
        val s = sessao()
        s.execute(GainXp("hero", 60))
        assertEquals(0, s.state.entities.getValue("hero").skills["atletismo"] ?: 0)
    }

    @Test fun provaSoNosMultiplosDeCincoEUmaVezPorNivel() {
        val s = sessao()
        assertTrue(s.execute(AdvanceSkillByTrial("hero", "atletismo", 5)) is ExecResult.Rejected) // ainda nível 1
        s.execute(GainXp("hero", 1000))
        assertTrue(s.execute(AdvanceSkillByTrial("hero", "atletismo", 3)) is ExecResult.Rejected)
        assertTrue(s.execute(AdvanceSkillByTrial("hero", "atletismo", 5)) is ExecResult.Accepted)
        assertEquals(1, s.state.entities.getValue("hero").skills.getValue("atletismo"))
        assertTrue(s.execute(AdvanceSkillByTrial("hero", "atletismo", 5)) is ExecResult.Rejected)
    }
}
