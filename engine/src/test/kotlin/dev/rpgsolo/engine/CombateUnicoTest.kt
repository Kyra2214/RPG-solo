package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-09: o pipeline unificado cobre redução de dano e postura defensiva (antes só no ataque antigo). */
class CombateUnicoTest {
    private fun session() =
        CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "pv", defendBonus = 2)),
        ).also {
            it.executeAll(
                listOf(
                    CreateEntity(
                        Entity(
                            "hero",
                            "Herói",
                            EntityKind.PLAYER,
                            stats = mapOf("poder" to 10, "resistencia" to 0),
                            resources = mapOf("pv" to Resource(30, 30)),
                        ),
                    ),
                    CreateEntity(
                        Entity(
                            "orc",
                            "Orc",
                            EntityKind.NPC,
                            stats = mapOf("poder" to 10, "resistencia" to 0),
                            resources = mapOf("pv" to Resource(30, 30)),
                        ),
                    ),
                ),
            )
            it.execute(StartCombat(listOf("hero", "orc")))
        }

    private fun pv(
        s: CampaignSession,
        id: String,
    ) = s.state.entities.getValue(id).resources.getValue("pv").current

    private fun hit(
        s: CampaignSession,
        reduction: Int,
    ) = s.unifiedAttackBetween(
        "hero",
        "orc",
        emptyList(),
        "poder",
        "resistencia",
        ignoreDefense = IgnoreDefenseSpec(guaranteed = true, percent = 100),
        damageResource = "pv",
        damageReduction = reduction,
    )

    @Test fun reducaoDeDanoSubtraiDoDanoFinal() {
        val s = session()
        assertTrue(hit(s, 3) is UnifiedAttackResult.Resolved)
        assertEquals(23, pv(s, "orc"))
    }

    @Test fun reducaoNuncaDeixaODanoNegativo() {
        val s = session()
        assertTrue(hit(s, 50) is UnifiedAttackResult.Resolved)
        assertEquals(30, pv(s, "orc"))
    }

    @Test fun posturaDefensivaEConsumidaPeloAtaqueUnificado() {
        val s = session()
        s.executeAll(listOf(Defend("hero"), NextTurn))
        assertTrue("hero" in s.state.combat!!.defending)
        val r = s.unifiedAttackBetween("orc", "hero", emptyList(), "poder", "resistencia", damageResource = "pv")
        assertTrue(r is UnifiedAttackResult.Resolved)
        assertFalse("hero" in s.state.combat!!.defending)
    }
}
