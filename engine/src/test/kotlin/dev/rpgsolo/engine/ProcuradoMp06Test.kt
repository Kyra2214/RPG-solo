package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** MP-06: alcance, decaimento, escalada e agentes do procurado (funções puras). Escritos, não executados. */
class ProcuradoMp06Test {
    private val rich = IssuerResources(gold = 1000, men = 50, influence = 100)

    private fun record(
        rung: ReachRung = ReachRung.RUMOR,
        resources: IssuerResources = rich,
        witnesses: List<String> = listOf("bia"),
        issuerLevel: Int = 10,
        status: WantedStatus = WantedStatus.ACTIVE,
        deadline: Int? = null,
        issued: Int = 0,
    ) = WantedRecord(
        id = "w1",
        targetId = "hero",
        issuerName = "Barão Voss",
        issuerLevel = issuerLevel,
        bounty = 1000,
        resources = resources,
        originPlaceId = "a",
        witnessIds = witnesses,
        rung = rung,
        issuedOnDay = issued,
        lastRenewedDay = 0,
        deadlineDay = deadline,
        status = status,
    )

    @Test fun recompensaDecaiUmPorCentoPorDiaAteZero() {
        assertEquals(1000, Wanted.effectiveBounty(record(), 0))
        assertEquals(900, Wanted.effectiveBounty(record(), 10))
        assertEquals(0, Wanted.effectiveBounty(record(), 150))
    }

    @Test fun procuradoVivoExigiAtivoRecompensaEPrazo() {
        assertTrue(Wanted.isLive(record(), 10))
        assertFalse(Wanted.isLive(record(status = WantedStatus.PAID), 10))
        assertFalse(Wanted.isLive(record(deadline = 5), 10))
        assertFalse(Wanted.isLive(record(), 150))
    }

    @Test fun noticiaViajaUmaPassagemPorDegrau() {
        val links = mapOf("a" to setOf("b"), "b" to setOf("a", "c"), "c" to setOf("b", "d"), "d" to setOf("c"))
        assertEquals(setOf("a", "b"), Wanted.reach(record(ReachRung.RUMOR), links))
        assertEquals(setOf("a", "b", "c"), Wanted.reach(record(ReachRung.POSTER), links))
    }

    @Test fun semTestemunhaOuRecursosNaoSobe() {
        assertNotNull(Wanted.cannotEscalate(record(witnesses = emptyList()), 10, 0))
        assertNotNull(Wanted.cannotEscalate(record(resources = IssuerResources()), 10, 0))
        assertNull(Wanted.cannotEscalate(record(), 10, 0))
    }

    @Test fun bossSoVemEmPessoaComGrupoAAltura() {
        val emissary = record(rung = ReachRung.EMISSARY, issuerLevel = 30)
        assertNotNull(Wanted.cannotEscalate(emissary, groupLevel = 5, today = 0))
        assertNull(Wanted.cannotEscalate(emissary, groupLevel = 25, today = 0))
        assertNotNull(Wanted.cannotEscalate(record(rung = ReachRung.BOSS), 30, 0))
    }

    @Test fun agentesSeguemOGrupoNuncaOBoss() {
        assertTrue(Wanted.agents(record(ReachRung.RUMOR), 8, 1L).isEmpty())
        assertTrue(Wanted.agents(record(ReachRung.POSTER), 8, 1L).isEmpty())
        assertEquals(8, Wanted.agents(record(ReachRung.HUNTERS), 8, 1L).single().level)
        val boss = Wanted.agents(record(ReachRung.BOSS, issuerLevel = 99), 8, 1L).single()
        assertEquals(13, boss.level)
        assertTrue(boss.kind.contains("Barão Voss"))
        assertEquals(Wanted.agents(record(ReachRung.ELITE), 8, 7L), Wanted.agents(record(ReachRung.ELITE), 8, 7L))
    }

    @Test fun escaladasDevidasSoQuandoPassouOPrazoDoDegrau() {
        val cedo = GameState().apply(WantedIssued(record(issued = 0)))
        assertTrue(Wanted.dueEscalations(cedo, "hero", 10).isEmpty())
        val tarde = GameState().apply(WantedIssued(record(issued = -10)))
        assertEquals(listOf("w1"), Wanted.dueEscalations(tarde, "hero", 10).map { it.id })
        assertTrue(Wanted.dueEscalations(tarde, "outro", 10).isEmpty())
    }
}
