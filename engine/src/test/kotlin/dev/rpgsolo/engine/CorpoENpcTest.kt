package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** LG-04 (itens do corpo) e identidade de NPC: o jogador só vê o público até assumir o NPC. */
class CorpoENpcTest {
    private fun sessao(): CampaignSession {
        val s = CampaignSession.create(InMemoryEventStore(), CampaignRecord("cn", "Corpo", 7L, EngineConfig()))
        s.execute(CreateEntity(Entity(id = "hero", name = "Herói", kind = EntityKind.PLAYER)))
        s.execute(CreateEntity(Entity(id = "corpo", name = "Velho herói", kind = EntityKind.NPC)))
        return s
    }

    @Test fun corpoSoPassaItensDeQuemEstaMorto() {
        val s = sessao()
        s.execute(AddItem("corpo", "espada", 1))
        assertTrue(s.execute(TransferBodyItems("corpo", "hero")) is ExecResult.Rejected) // ainda vivo
        s.execute(MarkNpcDead("corpo", "batalha"))
        assertTrue(s.execute(TransferBodyItems("corpo", "hero")) is ExecResult.Accepted)
        assertEquals(1, s.state.inventory["hero"]?.get("espada"))
        assertTrue(s.execute(TransferBodyItems("corpo", "hero")) is ExecResult.Rejected) // corpo já vazio
    }

    @Test fun dossieGuardaIdentidadeEIdadeDoNpc() {
        val s = sessao()
        val d = NpcDossier("corpo", personality = "calado", identity = "Ferreiro de Arvel", age = 34, sex = "masculino", publicInfo = listOf("Mãos calejadas"))
        assertTrue(s.execute(SetNpcDossier(d)) is ExecResult.Accepted)
        val antes = s.state.npcSheet("corpo")!!
        assertEquals(listOf("Mãos calejadas"), antes.publicInfo)
        assertNull(antes.identity)
        assertNull(antes.age)
        assertTrue(s.execute(SetNpcDossier(d.copy(age = 5000))) is ExecResult.Rejected)
    }
}
