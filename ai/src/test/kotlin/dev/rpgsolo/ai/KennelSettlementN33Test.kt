package dev.rpgsolo.ai

import dev.rpgsolo.engine.AddItem
import dev.rpgsolo.engine.AdvanceDays
import dev.rpgsolo.engine.Command
import dev.rpgsolo.engine.ConsumeItem
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.GameEngine
import dev.rpgsolo.engine.Resource
import dev.rpgsolo.engine.SettleKennel
import dev.rpgsolo.engine.TameCompanion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-33.2c: lote de pagamento + liquidação do criadouro calculado antes do dia avançar. */
class KennelSettlementN33Test {
    private fun engine(silver: Int): GameEngine {
        val e = GameEngine(5)
        e.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(10, 10)))))
        for (id in listOf("a", "b")) {
            e.execute(TameCompanion("hero", Entity(id, id, EntityKind.NPC, resources = mapOf("pv" to Resource(10, 10))), "quadrupede", 20))
        }
        if (silver > 0) e.execute(AddItem("hero", "prata", silver))
        return e
    }

    private val pay: (Int) -> List<Command>? = { if (it == 0) emptyList() else listOf(ConsumeItem("hero", "prata", it)) }

    @Test fun semSemanaViradaNaoFazNada() {
        val e = engine(100)
        assertNull(KennelSettlement.after(e.state, 1, 100, pay = pay))
    }

    @Test fun semanaViradaCobraEMudaOEstadoNoMesmoLote() {
        val e = engine(100)
        val result = requireNotNull(KennelSettlement.after(e.state, 7, 100, pay = pay))
        assertEquals(15, result.silver)
        assertTrue(result.commands.last() is SettleKennel)
        val ok = e.executeAll(listOf<Command>(AdvanceDays(7)) + result.commands)
        assertTrue(ok.toString(), ok is ExecResult.Accepted)
        assertEquals(85, e.state.inventory["hero"]?.get("prata"))
        assertEquals(1, e.state.kennelWeek)
    }

    @Test fun taxaReservadaEntraNoPagamentoEReduzOSaldoLivre() {
        val e = engine(30)
        val result = requireNotNull(KennelSettlement.after(e.state, 7, 30, reserved = 10, pay = pay))
        assertEquals(15, result.silver)
        assertEquals(listOf<Command>(ConsumeItem("hero", "prata", 25), SettleKennel(20)), result.commands)
    }

    @Test fun saldoCurtoDeixaOsAnimaisSemPagamento() {
        val e = engine(5)
        val result = requireNotNull(KennelSettlement.after(e.state, 7, 5, pay = pay))
        assertEquals(0, result.silver)
        assertEquals(false, result.weeks.single().keeperPaid)
    }
}
