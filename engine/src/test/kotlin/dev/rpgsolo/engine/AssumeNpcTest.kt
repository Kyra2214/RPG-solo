package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AssumeNpcTest {
    private fun session(): CampaignSession =
        CampaignSession.create(
            InMemoryEventStore(),
            CampaignRecord("c", "T", 3L, EngineConfig(deathResource = "hp", levelThresholds = listOf(10))),
        ).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(5, 5)))),
                    CreateEntity(
                        Entity(
                            "aldric",
                            "Aldric",
                            EntityKind.NPC,
                            stats = mapOf("forca" to 3),
                            skills = mapOf("luta" to 2),
                            resources = mapOf("hp" to Resource(8, 8)),
                            traits = listOf("leal"),
                            concept = "mercenário que esconde dívidas",
                            level = 4,
                        ),
                    ),
                    CreateEntity(Entity("morto", "Corvo", EntityKind.NPC, resources = mapOf("hp" to Resource(1, 1)))),
                    AddItem("hero", "adaga"),
                    AddItem("aldric", "espada"),
                    EquipItem("aldric", "espada", "mao"),
                    Damage("morto", "hp", 5),
                ),
            )
        }

    private fun killHero(s: CampaignSession) {
        assertTrue(s.execute(Damage("hero", "hp", 99)) is ExecResult.Accepted)
        assertFalse(s.state.entities.getValue("hero").alive)
    }

    @Test fun recusaEnquantoOHeroiEstaVivo() {
        val s = session()
        val before = s.mark()
        assertTrue(s.execute(AssumeNpc("aldric")) is ExecResult.Rejected)
        assertEquals(before, s.mark())
    }

    @Test fun recusaNpcMortoInexistenteOuQueJaEhPlayer() {
        val s = session()
        killHero(s)
        assertTrue(s.execute(AssumeNpc("morto")) is ExecResult.Rejected)
        assertTrue(s.execute(AssumeNpc("ninguem")) is ExecResult.Rejected)
        assertTrue(s.execute(AssumeNpc("hero")) is ExecResult.Rejected)
    }

    @Test fun recusaDuranteOCombate() {
        val s = session()
        s.execute(CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6)))))
        s.execute(StartCombat(listOf("aldric", "orc")))
        killHero(s)
        assertTrue(s.execute(AssumeNpc("aldric")) is ExecResult.Rejected)
    }

    @Test fun assumirTrocaOPlayerMantendoAFichaEOCorpoComSeusItens() {
        val s = session()
        killHero(s)
        assertTrue(s.execute(AssumeNpc("aldric")) is ExecResult.Accepted)
        val st = s.state
        val aldric = st.entities.getValue("aldric")
        assertEquals(EntityKind.PLAYER, aldric.kind)
        assertEquals(3, aldric.stats["forca"])
        assertEquals(listOf("leal"), aldric.traits)
        assertEquals(1, st.inventory["aldric"]?.get("espada"))
        assertEquals("espada", st.equipped["aldric"]?.get("mao"))
        // O herói morto vira corpo (NPC), com o inventário no lugar para a recuperação de itens.
        assertEquals(EntityKind.NPC, st.entities.getValue("hero").kind)
        assertFalse(st.entities.getValue("hero").alive)
        assertEquals(1, st.inventory["hero"]?.get("adaga"))
        assertEquals(1, st.entities.values.count { it.kind == EntityKind.PLAYER })
    }

    @Test fun naoHaSegundaTroca() {
        val s = session()
        killHero(s)
        s.execute(AssumeNpc("aldric"))
        s.execute(CreateEntity(Entity("outro", "Outro", EntityKind.NPC)))
        assertTrue(s.execute(AssumeNpc("outro")) is ExecResult.Rejected)
    }

    @Test fun xpSoParaOPlayerEPorIssoSoDepoisDeAssumir() {
        val s = session()
        assertTrue(s.execute(GainXp("aldric", 5)) is ExecResult.Rejected)
        killHero(s)
        s.execute(AssumeNpc("aldric"))
        assertTrue(s.execute(GainXp("aldric", 10)) is ExecResult.Accepted)
        assertEquals(10, s.state.entities.getValue("aldric").xp)
        assertEquals(2, s.state.entities.getValue("aldric").level)
    }

    @Test fun fichaOcultaAteAssumirECompletaDepois() {
        val s = session()
        s.execute(LearnKnowledge("analise:aldric:1", "parece forte", "teste:analise"))
        val antes = s.state.npcSheet("aldric")!!
        assertFalse(antes.assumed)
        assertEquals(listOf("parece forte"), antes.discovered)
        assertNull(antes.stats)
        assertNull(antes.skills)
        assertNull(antes.resources)
        assertNull(antes.traits)
        assertNull(antes.abilities)
        assertNull(antes.concept)
        assertNull(antes.inventory)
        assertNull(antes.equipped)
        assertNull(antes.level)
        assertNull(antes.xp)

        killHero(s)
        s.execute(AssumeNpc("aldric"))
        val depois = s.state.npcSheet("aldric")!!
        assertTrue(depois.assumed)
        assertEquals(mapOf("forca" to 3), depois.stats)
        assertEquals(mapOf("luta" to 2), depois.skills)
        assertEquals(listOf("leal"), depois.traits)
        assertEquals("mercenário que esconde dívidas", depois.concept)
        assertEquals(mapOf("espada" to 1), depois.inventory)
        assertEquals(mapOf("mao" to "espada"), depois.equipped)
        assertEquals(4, depois.level)
        assertNotNull(depois.resources)
        assertNull(s.state.npcSheet("inexistente"))
    }

    @Test fun replayReproduzATroca() {
        val s = session()
        killHero(s)
        s.execute(AssumeNpc("aldric"))
        val reaberto = replay(s.events)
        assertEquals(EntityKind.PLAYER, reaberto.entities.getValue("aldric").kind)
        assertEquals(EntityKind.NPC, reaberto.entities.getValue("hero").kind)
    }
}

class RebirthAndEndTest {
    private val cfg = EngineConfig(deathResource = "hp", rebirthItems = setOf("pergaminho"), rebirthPercent = 50)

    private fun session(withScroll: Boolean, withNpc: Boolean): CampaignSession =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 3L, cfg)).also {
            it.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(10, 10)))))
            if (withNpc) it.execute(CreateEntity(Entity("aldric", "Aldric", EntityKind.NPC)))
            if (withScroll) it.execute(AddItem("hero", "pergaminho"))
            it.execute(Damage("hero", "hp", 99))
        }

    @Test fun renasceComMetadeDosPvEConsomeOItem() {
        val s = session(withScroll = true, withNpc = true)
        assertTrue(s.execute(UseRebirthItem("hero")) is ExecResult.Accepted)
        val h = s.state.entities.getValue("hero")
        assertTrue(h.alive)
        assertEquals(5, h.resources.getValue("hp").current)
        assertNull(s.state.inventory["hero"]?.get("pergaminho"))
    }

    @Test fun semItemOuComHeroiVivoRecusa() {
        assertTrue(session(false, true).execute(UseRebirthItem("hero")) is ExecResult.Rejected)
        val s = session(true, true)
        s.execute(UseRebirthItem("hero"))
        assertTrue(s.execute(UseRebirthItem("hero")) is ExecResult.Rejected)
    }

    @Test fun itemValePrimeiroQueAssumirNpc() {
        val s = session(true, true)
        assertTrue(s.execute(AssumeNpc("aldric")) is ExecResult.Rejected)
    }

    @Test fun campanhaSoTerminaSemItemENpcEDepoisRecusaTudo() {
        assertTrue(session(true, false).execute(EndCampaign("fim")) is ExecResult.Rejected)
        assertTrue(session(false, true).execute(EndCampaign("fim")) is ExecResult.Rejected)
        val s = session(false, false)
        assertTrue(s.execute(EndCampaign("o herói caiu sem herdeiros")) is ExecResult.Accepted)
        assertEquals("o herói caiu sem herdeiros", s.state.endedReason)
        assertTrue(s.execute(RecordFact("k", "v")) is ExecResult.Rejected)
    }
}
