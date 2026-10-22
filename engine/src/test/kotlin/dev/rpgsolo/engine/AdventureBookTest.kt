package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** CP-05: livro e arquivo completo (CP-04): conteúdo, acentos, textos longos e ausência de segredos. */
class AdventureBookTest {
    private val chapters =
        listOf(
            Chapter(2, "A Fortaleza", 10),
            Chapter(1, "Início", 0, endMark = 10, summary = "O herói partiu de Pôrto Ágape."),
        )
    private val quests =
        listOf(
            Quest("q1", "Resgatar a filha", listOf(Objective("Achar a trilha", true), Objective("Entrar na torre"))),
            Quest("q2", "Entregar a carta", status = QuestStatus.DONE),
            Quest("q3", "Salvar a ponte", status = QuestStatus.FAILED),
        )
    private val timeline = listOf(0 to "Você chegou à vila.", 5 to "   ", 9 to "O ferreiro prometeu ajuda.")

    private fun book() = AdventureBookBuilder.build("Crônica de Ébano", "Aria", chapters, quests, timeline)

    @Test fun livroOrdenaCapitulosEMostraEstados() {
        val b = book()
        assertEquals("Crônica de Ébano", b.title)
        assertEquals("A história de Aria", b.subtitle)
        assertEquals(listOf("Capítulos", "Missões", "Diário da aventura"), b.sections.map { it.heading })
        val cap = b.sections[0].lines
        assertTrue(cap[0].startsWith("Capítulo 1 — Início") && cap[0].contains("Pôrto Ágape"))
        assertTrue(cap[1].contains("Capítulo em andamento."))
        val q = b.sections[1].lines
        assertTrue(q[0].contains("(em andamento)") && q[0].contains("[x] Achar a trilha") && q[0].contains("[ ] Entrar na torre"))
        assertTrue(q[1].contains("(concluída)") && q[2].contains("(fracassada)"))
        assertEquals(2, b.sections[2].lines.size)
    }

    @Test fun livroVazioNaoTemSecoesENomePadrao() {
        val b = AdventureBookBuilder.build("  ", null, emptyList(), emptyList(), emptyList())
        assertEquals("Aventura", b.title)
        assertEquals("Livro da aventura", b.subtitle)
        assertTrue(b.sections.isEmpty())
    }

    @Test fun textoLongoEAcentosNaoSaoCortados() {
        val long = "Ação — " + "á".repeat(10_000)
        val b = AdventureBookBuilder.build("X", null, emptyList(), emptyList(), listOf(0 to long))
        assertEquals(long, b.sections.single().lines.single())
    }

    private fun state() =
        GameState(
            entities =
                mapOf(
                    "hero" to Entity("hero", "Aria", EntityKind.PLAYER, resources = mapOf("hp" to Resource(7, 10)), xp = 30, level = 2, skills = mapOf("furtividade" to 2)),
                    "orc" to Entity("orc", "Orc", EntityKind.NPC, concept = "SEGREDO-CONCEITO"),
                ),
            inventory = mapOf("hero" to mapOf("espada" to 1, "pocao" to 2), "orc" to mapOf("tesouro-oculto" to 1)),
            equipped = mapOf("hero" to mapOf("mao" to "espada")),
            facts = mapOf("chave-privada" to "SEGREDO-FATO"),
            knowledge = mapOf("k1" to Knowledge("k1", "O ferreiro esconde algo", "conversa", believed = true)),
            relations = mapOf("hero\u0000orc" to 45, "orc\u0000hero" to -80, "orc\u0000elfo" to 99),
            clocks = mapOf("rel" to Clock("rel", "RELOGIO-PRIVADO", 6)),
            places =
                mapOf(
                    "vila" to Place("vila", "Vila Velha", "Casas de pedra.", "SEGREDO-LUGAR"),
                    "cripta" to Place("cripta", "Cripta", "", "SEGREDO-CRIPTA"),
                ),
            knownPlaces = setOf("vila"),
            memories = mapOf("orc" to listOf("SEGREDO-MEMORIA")),
        )

    private fun archive(): AdventureBook =
        CampaignArchiveBuilder.build(book(), state(), "hero", itemName = { if (it == "espada") "Espada longa" else it })

    @Test fun arquivoCompletoTrazOQueOJogadorSabe() {
        val a = archive()
        val text = a.sections.flatMap { it.lines }.joinToString("\n")
        assertTrue(a.subtitle.startsWith("Arquivo completo"))
        assertEquals("Sobre este arquivo", a.sections.first().heading)
        assertTrue(text.contains("Aria — nível 2, 30 XP"))
        assertTrue(text.contains("hp 7/10") && text.contains("furtividade 2"))
        assertTrue(text.contains("Espada longa × 1 (equipado)") && text.contains("pocao × 2"))
        assertTrue(text.contains("O ferreiro esconde algo (apenas acreditado)"))
        assertTrue(text.contains("Vila Velha — Casas de pedra."))
        assertTrue(text.contains("Você em relação a Orc: confiança"))
        assertTrue(text.contains("Orc em relação a você: hostilidade"))
        assertTrue(text.contains("Capítulo 1 — Início"))
    }

    @Test fun arquivoCompletoNaoVazaSegredosNemNumerosDeRelacao() {
        val text = archive().sections.flatMap { it.lines }.joinToString("\n")
        listOf(
            "SEGREDO-CONCEITO",
            "SEGREDO-FATO",
            "SEGREDO-LUGAR",
            "SEGREDO-CRIPTA",
            "SEGREDO-MEMORIA",
            "RELOGIO-PRIVADO",
            "tesouro-oculto",
            "chave-privada",
        )
            .forEach { assertFalse("vazou $it", text.contains(it)) }
        assertFalse(text.contains("Cripta"))
        assertFalse(text.contains("elfo"))
        assertFalse(text.contains("45") || text.contains("-80") || text.contains("99"))
    }

    @Test fun gerarOsDocumentosNaoAlteraOEstado() {
        val s = state()
        CampaignArchiveBuilder.build(book(), s, "hero")
        assertEquals(state(), s)
    }
}
