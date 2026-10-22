package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/** Garante que várias threads usando a mesma [CampaignSession] não corrompem log, estado nem o log salvo. */
class SessionConcurrencyTest {
    private val rec = CampaignRecord("c1", "Teste", 7L, EngineConfig())

    private fun newSession(store: EventStore = InMemoryEventStore()): Pair<CampaignSession, EventStore> =
        CampaignSession.create(store, rec) to store

    private fun runAll(
        threads: List<Thread>,
        errors: ConcurrentLinkedQueue<Throwable>,
    ) {
        threads.forEach { it.join(30_000) }
        assertTrue("alguma thread não terminou", threads.none { it.isAlive })
        assertTrue("erros nas threads: ${errors.map { it.toString() }}", errors.isEmpty())
    }

    @Test fun escritasConcorrentesMantemLogEstadoELogSalvoCoerentes() {
        val (session, store) = newSession()
        val errors = ConcurrentLinkedQueue<Throwable>()
        val workers = 8
        val rounds = 40
        val threads =
            (0 until workers).map { t ->
                thread(start = false) {
                    try {
                        repeat(rounds) { i ->
                            val r = session.execute(RecordFact("k-$t-$i", "v"))
                            check(r is ExecResult.Accepted) { "recusado: $r" }
                            session.check(CheckSpec("teste-$t-$i", 1, 20, difficulty = 10))
                        }
                    } catch (e: Throwable) {
                        errors += e
                    }
                }
            }
        threads.forEach { it.start() }
        runAll(threads, errors)

        assertEquals(workers * rounds, session.state.facts.size)
        assertEquals(session.eventCount, store.size("c1"))
        // O log salvo reproduz exatamente o estado em memória.
        assertEquals(session.state, CampaignSession.open(store, "c1").state)
    }

    @Test fun leitoresNuncaVeemListaEmMudancaPeloMeio() {
        val (session, _) = newSession()
        val errors = ConcurrentLinkedQueue<Throwable>()
        val stop = java.util.concurrent.atomic.AtomicBoolean(false)
        val reader =
            thread(start = false) {
                try {
                    while (!stop.get()) {
                        val n = session.eventCount
                        val snapshot = session.events
                        check(snapshot.size >= n) { "cópia menor que a contagem anterior" }
                        session.state.facts.size
                    }
                } catch (e: Throwable) {
                    errors += e
                }
            }
        val writers =
            (0 until 4).map { t ->
                thread(start = false) {
                    try {
                        repeat(60) { i -> session.execute(RecordFact("w-$t-$i", "v")) }
                    } catch (e: Throwable) {
                        errors += e
                    }
                }
            }
        reader.start()
        writers.forEach { it.start() }
        writers.forEach { it.join(30_000) }
        stop.set(true)
        reader.join(30_000)
        assertTrue("erros: ${errors.map { it.toString() }}", errors.isEmpty())
        assertEquals(4 * 60, session.state.facts.size)
    }

    @Test fun transacaoAtomicaNaoApagaEventoDeOutraThread() {
        val (session, store) = newSession()
        val errors = ConcurrentLinkedQueue<Throwable>()
        val insideTransaction = CountDownLatch(1)

        val tx =
            thread(start = false) {
                try {
                    session.atomically {
                        val mark = session.mark()
                        session.execute(RecordFact("transitorio", "1"))
                        insideTransaction.countDown()
                        Thread.sleep(150) // dá tempo da outra thread tentar gravar
                        session.rollbackTo(mark)
                    }
                } catch (e: Throwable) {
                    errors += e
                }
            }
        val other =
            thread(start = false) {
                try {
                    check(insideTransaction.await(10, TimeUnit.SECONDS)) { "transação não começou" }
                    session.execute(RecordFact("de-outra-thread", "1"))
                } catch (e: Throwable) {
                    errors += e
                }
            }
        tx.start()
        other.start()
        runAll(listOf(tx, other), errors)

        assertEquals(mapOf("de-outra-thread" to "1"), session.state.facts)
        assertEquals(1, session.eventCount)
        assertEquals(1, store.size("c1"))
    }

    @Test fun operacoesCompostasFicamAtomicas() {
        // checkWithFortuneSpendingResource gasta recurso e rola em um único passo, mesmo com outra thread escrevendo.
        val (session, store) = newSession()
        session.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pa" to Resource(50, 50)))))
        val errors = ConcurrentLinkedQueue<Throwable>()
        val spenders =
            (0 until 4).map {
                thread(start = false) {
                    try {
                        repeat(10) {
                            val r =
                                session.checkWithFortuneSpendingResource(
                                    "hero",
                                    "pa",
                                    1,
                                    CheckSpec("sorte", 1, 20, difficulty = 10),
                                    FortuneMode.ADVANTAGE,
                                )
                            check(r is FortuneSpendResult.Resolved) { "recusado: $r" }
                        }
                    } catch (e: Throwable) {
                        errors += e
                    }
                }
            }
        val noise =
            thread(start = false) {
                try {
                    repeat(40) { i -> session.execute(RecordFact("ruido-$i", "v")) }
                } catch (e: Throwable) {
                    errors += e
                }
            }
        (spenders + noise).forEach { it.start() }
        runAll(spenders + noise, errors)

        assertEquals(10, session.state.entities.getValue("hero").resources.getValue("pa").current)
        assertEquals(40, session.state.facts.size)
        assertEquals(session.eventCount, store.size("c1"))
        assertEquals(session.state, CampaignSession.open(store, "c1").state)
    }
}
