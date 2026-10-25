package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-02: atributo sem teto e ficha antiga que ainda não tem os atributos acrescentados depois. */
class AtributosSemTetoTest {
    private val semTeto = Int.MAX_VALUE

    private fun config() =
        EngineConfig(
            levelThresholds = listOf(10, 20),
            statPointsPerLevel = 1,
            statCaps = mapOf("forca" to semTeto, "inteligencia" to semTeto),
            statDefaults = mapOf("forca" to 0, "inteligencia" to 0),
        )

    private fun sessao(stats: Map<String, Int>): CampaignSession {
        val session = CampaignSession.create(InMemoryEventStore(), CampaignRecord("n02", "Sem teto", 3L, config()))
        session.execute(CreateEntity(Entity(id = "hero", name = "Herói", kind = EntityKind.PLAYER, stats = stats)))
        return session
    }

    @Test fun configAceitaAtributoSemTeto() {
        assertEquals(semTeto, config().statCaps.getValue("forca"))
    }

    @Test fun fichaAntigaGastaPontoEmAtributoQueNaoTinha() {
        val session = sessao(mapOf("forca" to 2))
        session.execute(GainXp("hero", 10))
        assertEquals(1, session.state.entities.getValue("hero").statPoints)
        val result = session.execute(AllocateStatPoints("hero", mapOf("inteligencia" to 1)))
        assertTrue(result.toString(), result is ExecResult.Accepted)
        val hero = session.state.entities.getValue("hero")
        assertEquals(1, hero.stats["inteligencia"])
        assertEquals(2, hero.stats["forca"])
        assertEquals(0, hero.statPoints)
    }

    @Test fun evolucaoPorPontoDeAtributoNaoBatenoTeto() {
        val session = sessao(mapOf("forca" to 50, "inteligencia" to 0))
        session.execute(GainXp("hero", 10))
        val result = session.execute(AllocateStatPoints("hero", mapOf("forca" to 1)))
        assertTrue(result.toString(), result is ExecResult.Accepted)
        assertEquals(51, session.state.entities.getValue("hero").stats["forca"])
    }

    @Test fun distribuicaoInicialExige10DeStatusE2DeHabilidade() {
        val session = sessao(mapOf("forca" to 1))
        // 12 todos em status, 8 + 4 e 10 + 0 são recusados: o total vem do pacote (10 + 2), sem divisão livre.
        assertTrue(session.execute(AllocateInitialBuild("hero", mapOf("forca" to 12), 0)) is ExecResult.Rejected)
        assertTrue(session.execute(AllocateInitialBuild("hero", mapOf("forca" to 8), 4)) is ExecResult.Rejected)
        assertTrue(session.execute(AllocateInitialBuild("hero", mapOf("forca" to 10), 0)) is ExecResult.Rejected)
        assertTrue(session.execute(AllocateInitialBuild("hero", mapOf("forca" to 9), 2)) is ExecResult.Rejected)
        assertEquals(false, session.state.entities.getValue("hero").initialBuildAllocated)

        val ok = session.execute(AllocateInitialBuild("hero", mapOf("forca" to 6, "inteligencia" to 4), 2))
        assertTrue(ok.toString(), ok is ExecResult.Accepted)
        val hero = session.state.entities.getValue("hero")
        assertEquals(7, hero.stats["forca"])
        assertEquals(4, hero.stats["inteligencia"])
        assertEquals(2, hero.abilityPoints)
        assertTrue(hero.initialBuildAllocated)
        // Só uma vez.
        assertTrue(session.execute(AllocateInitialBuild("hero", mapOf("forca" to 10), 2)) is ExecResult.Rejected)
    }

    @Test fun totaisIniciaisVemDaConfiguracaoDoPacote() {
        val config =
            EngineConfig(
                initialStatPoints = 4,
                initialAbilityPoints = 1,
                statCaps = mapOf("forca" to semTeto),
                statDefaults = mapOf("forca" to 0),
            )
        val session = CampaignSession.create(InMemoryEventStore(), CampaignRecord("n02b", "Config", 5L, config))
        session.execute(CreateEntity(Entity(id = "hero", name = "Herói", kind = EntityKind.PLAYER, stats = mapOf("forca" to 0))))
        assertTrue(session.execute(AllocateInitialBuild("hero", mapOf("forca" to 10), 2)) is ExecResult.Rejected)
        assertTrue(session.execute(AllocateInitialBuild("hero", mapOf("forca" to 4), 1)) is ExecResult.Accepted)
    }
}
