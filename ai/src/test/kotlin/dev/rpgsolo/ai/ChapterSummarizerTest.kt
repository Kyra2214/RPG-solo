package dev.rpgsolo.ai

import dev.rpgsolo.engine.AddItem
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.ChapterBook
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChapterSummarizerTest {
    private fun setup(): Pair<CampaignSession, ChapterBook> {
        val s = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 1L, EngineConfig()))
        s.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER)))
        val b = ChapterBook()
        b.open(s, "Vila")
        s.execute(AddItem("hero", "mapa"))
        return s to b
    }

    @Test fun geraEGuardaOResumo() {
        val (s, b) = setup()
        val ai = ScriptedProvider(listOf("  Ele chegou à vila e achou um mapa.  "))
        val r = ChapterSummarizer(ai).closeWithSummary(s, b, listOf("A chuva cai."))
        assertTrue(r is ChapterSummarizer.Result.Closed)
        assertEquals("Ele chegou à vila e achou um mapa.", b.chapters.single().summary)
        assertTrue("hero/mapa" in ai.requests.single().messages.single().content)
    }

    @Test fun falhaDeIaDeixaCapituloAberto() {
        val (s, b) = setup()
        val r = ChapterSummarizer(ScriptedProvider(listOf(AiError.NoInternet()))).closeWithSummary(s, b, emptyList())
        assertTrue(r is ChapterSummarizer.Result.Failed)
        assertNotNull(b.current())
    }

    @Test fun resumoVazioNaoFechaCapitulo() {
        val (s, b) = setup()
        assertTrue(
            ChapterSummarizer(ScriptedProvider(listOf("   "))).closeWithSummary(s, b, emptyList()) is ChapterSummarizer.Result.Failed,
        )
        assertNotNull(b.current())
    }
}
