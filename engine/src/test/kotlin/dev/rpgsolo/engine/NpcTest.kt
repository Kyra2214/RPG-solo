package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NpcTest {
    private fun session() =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 3L, EngineConfig())).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, stats = mapOf("analise" to 2))),
                    CreateEntity(Entity("sem", "Sem perícia", EntityKind.PLAYER)),
                    CreateEntity(Entity("guarda", "Guarda", EntityKind.NPC)),
                ),
            )
        }

    private val plan =
        RevealPlan(
            "guarda",
            "analise",
            CheckSpec("analise", 1, 20, 0, 1),
            listOf("parece forte", "está ferido na perna", "esconde uma chave"),
        )

    @Test fun semAHabilidadeNadaEhRoladoNemLiberado() {
        val s = session()
        val before = s.mark()
        assertTrue(Reveal.run(s, "sem", plan) is RevealResult.NotAllowed)
        assertEquals(before, s.mark())
        assertTrue(s.state.knowledge.isEmpty())
    }

    @Test fun testeLiberaSoOsNiveisDoResultado() {
        val s = session()
        val r = Reveal.run(s, "hero", plan) as RevealResult.Revealed
        assertEquals(r.levelsReleased, s.state.knowledge.size)
        assertEquals(r.texts, plan.levels.take(r.levelsReleased))
        assertTrue(s.state.knowledge.values.all { it.source == "teste:analise" })
    }

    @Test fun repetirNaoFalhaNemDuplica() {
        val s = session()
        Reveal.run(s, "hero", plan)
        val n = s.state.knowledge.size
        assertTrue(Reveal.run(s, "hero", plan) is RevealResult.Revealed)
        assertTrue(s.state.knowledge.size >= n)
    }

    @Test fun contextoSoComSegredoQueACenaExigeOuFoiLiberado() {
        val book = DossierBook()
        book.put(
            NpcDossier(
                "guarda",
                "desconfiado",
                goals = listOf("proteger o portão"),
                secrets = listOf(Secret("s1", "vende informações"), Secret("s2", "o rei morreu", falseInfo = true)),
            ),
        )
        val sem = book.contextFor(listOf("guarda")).single()
        assertFalse("vende" in sem)
        val com = book.contextFor(listOf("guarda"), exposed = setOf("s1")).single()
        assertTrue("NÃO revelar" in com && "vende informações" in com)
        assertFalse("rei morreu" in com)
        val lib = book.contextFor(listOf("guarda"), released = setOf("s2")).single()
        assertTrue("liberado ao jogador" in lib && "[informação falsa]" in lib)
        assertTrue(book.contextFor(listOf("outro")).isEmpty())
    }

    @Test fun diarioMostraCrencaComoCrenca() {
        val s = session()
        s.execute(LearnKnowledge("a", "há um túnel", "conversa", believed = true))
        s.execute(LearnKnowledge("b", "a porta é de ferro", "pista"))
        val j = journal(s.state)
        assertTrue(j[0].contains("acreditado") && !j[1].contains("acreditado"))
    }
}
