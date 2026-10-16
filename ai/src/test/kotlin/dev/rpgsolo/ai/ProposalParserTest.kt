package dev.rpgsolo.ai

import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.LearnKnowledge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalParserTest {
    private fun ok(raw: String) = (ProposalParser.parse(raw) as ParseResult.Ok).output
    private fun errors(raw: String) = (ProposalParser.parse(raw) as ParseResult.Invalid).errors

    @Test fun jsonPuroComPropostas() {
        val o = ok("""{"narrative":"O orc recua.","proposals":[{"type":"damage","entityId":"orc","resource":"hp","amount":3}]}""")
        assertEquals("O orc recua.", o.narrative)
        assertEquals(listOf(Damage("orc", "hp", 3)), o.proposals)
    }

    @Test fun aceitaCercaDeCodigo() {
        val o = ok("```json\n{\"narrative\":\"ok\"}\n```")
        assertEquals("ok", o.narrative)
        assertTrue(o.proposals.isEmpty())
    }

    @Test fun aprenderTemPadraoNaoAcreditado() {
        val o = ok("""{"narrative":"x","proposals":[{"type":"learn","key":"k","text":"t","source":"conversa"}]}""")
        assertEquals(LearnKnowledge("k", "t", "conversa", false), o.proposals.single())
    }

    @Test fun semJsonOuSemNarrativaInvalida() {
        assertTrue(errors("só texto").isNotEmpty())
        assertTrue(errors("""{"proposals":[]}""").any { "narrative" in it })
    }

    @Test fun iaNaoPodeRolarDadosNemCriarEntidade() {
        val e = errors("""{"narrative":"x","proposals":[{"type":"roll","expr":"1d20"},{"type":"set_hp","amount":99}]}""")
        assertEquals(2, e.size)
        assertTrue(e.all { "motor" in it })
    }

    @Test fun campoFaltandoOuTextoNoLugarDeNumero() {
        assertTrue(errors("""{"narrative":"x","proposals":[{"type":"damage","entityId":"orc","resource":"hp"}]}""").isNotEmpty())
        assertTrue(errors("""{"narrative":"x","proposals":[{"type":"damage","entityId":"orc","resource":"hp","amount":"3"}]}""").isNotEmpty())
    }

    @Test fun umaPropostaRuimInvalidaOLoteInteiro() {
        val e = errors("""{"narrative":"x","proposals":[{"type":"damage","entityId":"a","resource":"hp","amount":1},{"type":"xyz"}]}""")
        assertEquals(1, e.size)
        assertTrue("proposals[1]" in e.single())
    }
}
