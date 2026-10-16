package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/** Fase 11.1 e 11.2/11.3: bateria automática de contradições, desempenho e backup. */
class ContinuityBatteryTest {
    private val store = InMemoryEventStore()
    private fun hero() = Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(10, 10), "mana" to Resource(2, 5)))
    private fun npc() = Entity("ana", "Ana", EntityKind.NPC, resources = mapOf("hp" to Resource(4, 4)))
    private fun session(id: String = "c", st: EventStore = store) =
        CampaignSession.create(st, CampaignRecord(id, "T", 11L, EngineConfig(deathResource = "hp"))).also {
            it.executeAll(listOf(CreateEntity(hero()), CreateEntity(npc())))
        }
    private fun reopen(id: String = "c", st: EventStore = store) = CampaignSession.open(st, id)

    @Test fun b1_npcLembraPromessaDepoisDeReabrir() {
        session().execute(MakePromise("p", "ana", "levar o amuleto"))
        assertEquals(listOf("p"), reopen().state.openPromises("ana").map { it.id })
    }

    @Test fun b2_itemConsumidoNaoReaparece() {
        val s = session()
        s.executeAll(listOf(AddItem("hero", "poção"), ConsumeItem("hero", "poção")))
        assertTrue(s.execute(ConsumeItem("hero", "poção")) is ExecResult.Rejected)
        assertEquals(0, reopen().state.inventory["hero"]?.get("poção") ?: 0)
    }

    @Test fun b3_morteNaoEIgnorada() {
        val s = session()
        s.execute(Damage("ana", "hp", 99))
        assertTrue(s.execute(Heal("ana", "hp", 1)) is ExecResult.Rejected)
        assertTrue(s.execute(MakePromise("p2", "ana", "x")) is ExecResult.Rejected)
        val r = reopen()
        assertFalse(r.state.entities.getValue("ana").alive)
        assertTrue(r.execute(Heal("ana", "hp", 1)) is ExecResult.Rejected)
    }

    @Test fun b4_segredoDescobertoPermanece() {
        val s = session()
        s.execute(LearnKnowledge("k", "o guarda é espião", "conversa"))
        val r = reopen()
        assertEquals("o guarda é espião", r.state.knowledge.getValue("k").text)
        assertTrue(r.execute(LearnKnowledge("k", "talvez", "boato", believed = true)) is ExecResult.Rejected)
    }

    @Test fun b5_habilidadeRespeitaCustoERejeitaSemRecurso() {
        val s = session()
        val before = s.mark()
        assertTrue(s.execute(SpendResource("hero", "mana", 3)) is ExecResult.Rejected)
        assertEquals(before, s.mark())
        assertTrue(s.execute(SpendResource("hero", "mana", 2)) is ExecResult.Accepted)
        assertTrue(s.execute(SpendResource("hero", "mana", 1)) is ExecResult.Rejected)
    }

    @Test fun b6_campanhaNaoAlteraAFichaDeOutra() {
        val sys = SystemRef("s", "e")
        val ficha = Ficha("f", "Dorn", sys, "anão", mapOf("forca" to 3), mapOf("hp" to Resource(10, 10)))
        val a = session("a", InMemoryEventStore()).also { it.execute(CreateEntity(ficha.toEntity().copy(id = "dorn"))) }
        val b = session("b", InMemoryEventStore()).also { it.execute(CreateEntity(ficha.toEntity().copy(id = "dorn"))) }
        a.execute(Damage("dorn", "hp", 7))
        assertEquals(3, a.state.entities.getValue("dorn").resources.getValue("hp").current)
        assertEquals(10, b.state.entities.getValue("dorn").resources.getValue("hp").current)
        assertEquals(10, ficha.resources.getValue("hp").current)
    }

    @Test fun b9_regraAdicionalNuncaEOficial() {
        assertEquals(listOf(RuleOrigin.ADAPTACAO), RuleOrigin.values().toList())
    }

    @Test fun estadoSempreCoerenteAposTudoIsso() {
        val s = session()
        s.executeAll(listOf(AddItem("hero", "x", 2), GainXp("hero", 3), AddCondition("hero", "cansado")))
        assertTrue(validateState(reopen().state).isEmpty())
    }

    // ---- 11.2.1 desempenho ----
    @Test fun campanhaLongaReabreSemLentidao() {
        val s = session()
        repeat(1500) { s.executeAll(listOf(AddItem("hero", "moeda"), ConsumeItem("hero", "moeda"), RecordFact("passo", "$it"))) }
        assertTrue(s.mark() > 4000)
        val t0 = System.nanoTime()
        val r = reopen()
        val ms = (System.nanoTime() - t0) / 1_000_000
        assertEquals("1499", r.state.facts["passo"])
        assertTrue("reabrir levou ${ms}ms", ms < 5_000)
    }

    // ---- 11.3.1 backup ----
    @Test fun backupExportaEImportaOMesmoEstadoEOsMesmosDados() {
        val s = session()
        s.execute(MakePromise("p", "ana", "voltar"))
        s.check(CheckSpec("t", 1, 20, 0, 10))
        val text = Backup.export(store, "c")
        val outro = InMemoryEventStore()
        val id = Backup.import(outro, text)
        val r = CampaignSession.open(outro, id)
        assertEquals(s.state, r.state)
        // seed preservada: a próxima rolagem é a mesma nas duas
        assertEquals(s.check(CheckSpec("t2", 1, 20, 0, 10)).roll.total, r.check(CheckSpec("t2", 1, 20, 0, 10)).roll.total)
    }

    @Test fun backupRuimNaoCriaNada() {
        val outro = InMemoryEventStore()
        session()
        val bom = Backup.export(store, "c")
        fun falha(text: String, id: String? = null) =
            try { Backup.import(outro, text, id); fail("deveria falhar") } catch (e: IllegalArgumentException) { }
        falha("isto não é backup")
        falha(bom.replace("\"version\":1", "\"version\":99"))
        falha(bom.replace("EntityCreated", "Inexistente"))
        assertEquals(null, outro.campaign("c"))
        Backup.import(outro, bom)
        falha(bom) // id já existe
        Backup.import(outro, bom, newId = "copia")
        assertEquals(store.size("c"), outro.size("copia"))
    }
}
