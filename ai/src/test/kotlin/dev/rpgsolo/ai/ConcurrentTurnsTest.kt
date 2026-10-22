package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CheckSpec
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/** R-01: jogadas concorrentes sobre a mesma sessão são serializadas e leituras da interface não esperam o turno. */
class ConcurrentTurnsTest {
    private class SlowProvider(private val entered: CountDownLatch? = null, private val release: CountDownLatch? = null) : AiProvider {
        override val name = "slow"
        val inFlight = AtomicInteger()
        val maxInFlight = AtomicInteger()
        val calls = AtomicInteger()

        override fun complete(request: AiRequest): AiResponse {
            val now = inFlight.incrementAndGet()
            maxInFlight.updateAndGet { maxOf(it, now) }
            calls.incrementAndGet()
            try {
                entered?.countDown()
                if (release != null) release.await(10, TimeUnit.SECONDS) else Thread.sleep(20)
            } finally {
                inFlight.decrementAndGet()
            }
            return AiResponse("""{"narrative":"ok"}""")
        }
    }

    private fun session() =
        CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 9L, EngineConfig(deathResource = "pv"))).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))),
                    CreateEntity(Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("pv" to Resource(6, 6)))),
                ),
            )
        }

    private fun controller(
        s: CampaignSession,
        p: AiProvider,
        loop: GameLoop = GameLoop(p),
    ) = PlayController(s, loop, "hero", "pv", { CheckSpec("ataque", 1, 20, 2, 10) })

    @Test fun duasJogadasConcorrentesSaoSerializadasEMantemLogEChatCoerentes() {
        val s = session()
        val provider = SlowProvider()
        val loop = GameLoop(provider)
        val c = controller(s, provider, loop)
        val baseEvents = s.eventCount
        val errors = ConcurrentLinkedQueue<Throwable>()
        val results = ConcurrentLinkedQueue<PlayResult>()
        val start = CountDownLatch(1)
        val threads =
            (1..2).map { n ->
                thread {
                    try {
                        start.await()
                        results += c.submit("ataco o orc $n")
                    } catch (e: Throwable) {
                        errors += e
                    }
                }
            }
        start.countDown()
        threads.forEach { it.join(30_000) }
        assertTrue("thread presa", threads.none { it.isAlive })
        assertTrue("erros: ${errors.map { it.toString() }}", errors.isEmpty())
        assertEquals(1, provider.maxInFlight.get())
        assertEquals(2, provider.calls.get())
        assertEquals(2, results.count { it is PlayResult.Played })
        assertEquals(2, c.lines.count { it.author == Author.PLAYER })
        assertEquals(2, c.lines.count { it.author == Author.MASTER })
        assertEquals(2, c.lines.count { it.author == Author.ROLL })
        assertEquals(2, loop.turns.size)
        assertEquals(s.events.size, s.eventCount)
        assertTrue(s.eventCount > baseEvents)
        assertTrue(c.undo())
        assertTrue(c.undo())
        assertEquals(baseEvents, s.eventCount)
        assertEquals(emptyList<ChatLine>(), c.lines)
    }

    @Test fun leiturasDaInterfaceNaoEsperamOTurnoEmAndamento() {
        val s = session()
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val c = controller(s, SlowProvider(entered, release))
        val errors = ConcurrentLinkedQueue<Throwable>()
        val worker =
            thread {
                try {
                    c.submit("ataco o orc")
                } catch (e: Throwable) {
                    errors += e
                }
            }
        assertTrue(entered.await(10, TimeUnit.SECONDS))
        val reader =
            thread {
                try {
                    assertTrue(c.busy)
                    c.lines
                    c.isRollRevealed(0)
                    c.revealRoll(0)
                    c.suggestions()
                    c.pending()
                    c.panel()
                } catch (e: Throwable) {
                    errors += e
                }
            }
        reader.join(5_000)
        val readerFinished = !reader.isAlive
        release.countDown()
        worker.join(30_000)
        assertTrue("leitura bloqueada pelo turno", readerFinished)
        assertTrue("erros: ${errors.map { it.toString() }}", errors.isEmpty())
        assertEquals(1, c.lines.count { it.author == Author.MASTER })
    }
}
