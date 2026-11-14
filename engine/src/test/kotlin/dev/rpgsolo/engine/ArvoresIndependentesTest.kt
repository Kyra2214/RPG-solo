package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Árvore de status e árvore de habilidades não dependem uma da outra. */
class ArvoresIndependentesTest {
    private fun sessao(): CampaignSession {
        val config =
            EngineConfig(
                levelThresholds = listOf(10, 20),
                statCaps = mapOf("forca" to Int.MAX_VALUE, "inteligencia" to Int.MAX_VALUE),
                statDefaults = mapOf("forca" to 0, "inteligencia" to 0),
            )
        val s = CampaignSession.create(InMemoryEventStore(), CampaignRecord("arv", "Árvores", 5L, config))
        s.execute(CreateEntity(Entity(id = "hero", name = "Herói", kind = EntityKind.PLAYER, stats = mapOf("forca" to 1))))
        return s
    }

    @Test fun habilidadesChegamSemDistribuirStatus() {
        val s = sessao()
        assertTrue(s.execute(GrantInitialAbilityPoints("hero")) is ExecResult.Accepted)
        val hero = s.state.entities.getValue("hero")
        assertEquals(2, hero.abilityPoints)
        assertEquals(false, hero.initialBuildAllocated)
        assertTrue(s.execute(GrantInitialAbilityPoints("hero")) is ExecResult.Rejected)
    }

    @Test fun statusDistribuemSemMexerNasHabilidades() {
        val s = sessao()
        assertTrue(s.execute(AllocateInitialStats("hero", mapOf("forca" to 9))) is ExecResult.Rejected)
        assertTrue(s.execute(AllocateInitialStats("hero", mapOf("forca" to 6, "inteligencia" to 4))) is ExecResult.Accepted)
        val hero = s.state.entities.getValue("hero")
        assertEquals(7, hero.stats.getValue("forca"))
        assertEquals(0, hero.abilityPoints)
        assertEquals(false, hero.initialAbilityGranted)
        assertTrue(s.execute(AllocateInitialStats("hero", mapOf("forca" to 10))) is ExecResult.Rejected)
    }
}
