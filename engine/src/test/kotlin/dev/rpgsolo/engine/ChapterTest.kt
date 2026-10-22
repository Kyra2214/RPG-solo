package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ChapterTest {
    private val store = InMemoryEventStore()

    private fun session() =
        CampaignSession.create(store, CampaignRecord("c", "T", 5L, EngineConfig())).also {
            it.executeAll(
                listOf(
                    CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(10, 10)))),
                    CreateEntity(Entity("npc", "Ana", EntityKind.NPC)),
                ),
            )
        }

    @Test fun abreEncerraEGuardaResumoUmaVez() {
        val s = session()
        val book = ChapterBook()
        book.open(s, "Início")
        s.executeAll(listOf(AddItem("hero", "chave"), MakePromise("p1", "npc", "ajudar"), GainXp("hero", 5)))
        val c = book.close(s, "Ele achou a chave.")
        assertEquals(mapOf("hero/chave" to 1), c.digest!!.itemDeltas)
        assertEquals(listOf("p1"), c.digest!!.promisesMade)
        assertEquals(mapOf("hero" to 5), c.digest!!.xpGained)
        try {
            book.close(s, "outra")
            fail()
        } catch (e: IllegalStateException) {
        }
        assertEquals("Ele achou a chave.", book.chapters.single().summary)
        assertEquals(listOf("Capítulo 1 (Início): Ele achou a chave."), book.summaries())
    }

    @Test fun naoAbreDoisCapitulosAoMesmoTempo() {
        val s = session()
        val book = ChapterBook()
        book.open(s, "A")
        try {
            book.open(s, "B")
            fail()
        } catch (e: IllegalStateException) {
        }
    }

    @Test fun tamanhoConfiguravelSugereEncerrar() {
        val s = session()
        val book = ChapterBook(maxEvents = 2)
        book.open(s, "A")
        assertFalse(book.shouldClose(s))
        s.execute(AddItem("hero", "x"))
        s.execute(AddItem("hero", "y"))
        assertTrue(book.shouldClose(s))
    }

    @Test fun desfazerAntesDoInicioReconcilia() {
        val s = session()
        val m = s.mark()
        s.execute(AddItem("hero", "x"))
        val book = ChapterBook()
        book.open(s, "A")
        s.rollbackTo(m)
        assertTrue(book.reconcile(s))
        assertEquals(m, book.current()!!.startMark)
    }

    @Test fun capituloSeguinteComecaNoEstadoCorretoDepoisDeReabrir() {
        val s = session()
        val book = ChapterBook()
        book.open(s, "1")
        s.executeAll(listOf(AddItem("hero", "poção"), MakePromise("p", "npc", "voltar"), ConsumeItem("hero", "poção")))
        book.close(s, "fim")
        val reaberta = CampaignSession.open(store, "c")
        val book2 = ChapterBook()
        val c2 = book2.open(reaberta, "2")
        assertEquals(reaberta.mark(), c2.startMark)
        assertNull(reaberta.state.inventory["hero"]?.get("poção")) // consumido não volta
        assertEquals(1, reaberta.state.openPromises("npc").size) // promessa lembrada
    }

    @Test fun missoesELinhaDoTempo() {
        val log = QuestLog()
        log.add(Quest("q1", "Achar a chave", listOf(Objective("entrar"), Objective("pegar"))))
        log.add(Quest("q2", "Ajudar a aldeia", listOf(Objective("levar remédios"))))
        log.completeObjective("q1", 0, chapter = 1)
        assertEquals(QuestStatus.ACTIVE, log.get("q1")!!.status)
        val afterFirstObjective = log.timeline()
        try { log.completeObjective("q1", 0, chapter = 1); fail() } catch (e: IllegalArgumentException) { }
        assertEquals(afterFirstObjective, log.timeline())
        log.completeObjective("q1", 1, chapter = 1)
        assertEquals(QuestStatus.DONE, log.get("q1")!!.status)
        assertEquals(listOf("q2"), log.active().map { it.id })
        try { log.finish("q1", QuestStatus.FAILED); fail() } catch (e: IllegalArgumentException) { }
        log.finish("q2", QuestStatus.FAILED, chapter = 1)
        log.note(1, "Chegou à vila")
        assertEquals(listOf(
            1 to "Objetivo concluído: Achar a chave — entrar",
            1 to "Objetivo concluído: Achar a chave — pegar",
            1 to "Missão concluída: Achar a chave",
            1 to "Missão encerrada sem sucesso: Ajudar a aldeia",
            1 to "Chegou à vila"
        ), log.timeline())
    }

    @Test fun numeroDeCapituloInvalidoNaoAlteraObjetivo() {
        val log = QuestLog().also { it.add(Quest("q1", "Achar a chave", listOf(Objective("entrar")))) }
        try { log.completeObjective("q1", 0, chapter = -1); fail() } catch (e: IllegalArgumentException) { }
        assertFalse(log.get("q1")!!.objectives.single().done)
        assertTrue(log.timeline().isEmpty())
    }

    @Test fun trocarAmbientacaoNaoMudaAFicha() {
        val sys = SystemRef("s", "e")
        val ficha = Ficha("f", "Dorn", sys, "anão minerador", mapOf("forca" to 3))
        val camp = Campanha("c", "f", sys, Ambientacao("a1", "c", "Fantasia"))
        val outra = camp.withAmbientacao(Ambientacao("a2", "x", "Cyberpunk"))
        assertEquals("Cyberpunk", outra.ambientacao.name)
        assertEquals("c", outra.ambientacao.campaignId)
        assertTrue(outra.validate(ficha).isEmpty())
        assertEquals(mapOf("forca" to 3), ficha.stats)
        assertNotNull(outra.fichaId)
    }
}
