package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** MP-08: validação do rascunho de mundo próprio. Escritos, não executados. */
class MundoProprioMp08Test {
    private fun place(
        id: String,
        vararg to: String,
        name: String = id.replaceFirstChar { it.uppercase() },
    ) = CustomPlaceDraft(id, name, connections = to.toList())

    private fun draft(
        places: List<CustomPlaceDraft> = listOf(place("vila", "porto"), place("porto", "vila")),
        factions: List<String> = emptyList(),
        clocks: List<CustomClockDraft> = emptyList(),
        start: String? = null,
        name: String = "Terra Nova",
    ) = CustomWorldDraft(name = name, places = places, factions = factions, clocks = clocks, startPlaceId = start)

    private fun errors(d: CustomWorldDraft) = CustomWorldValidator.validate(d)

    @Test fun rascunhoValidoNaoTemErros() {
        assertEquals(emptyList<String>(), errors(draft(factions = listOf("Guilda"), clocks = listOf(CustomClockDraft("fome", "Fome", 6)), start = "porto")))
    }

    @Test fun lugarUnicoEhValidoESemLugarNao() {
        assertTrue(errors(draft(places = listOf(place("vila")))).isEmpty())
        assertTrue(errors(draft(places = emptyList())).isNotEmpty())
    }

    @Test fun nomeVazioIdInvalidoEDuplicadosSaoRecusados() {
        assertTrue(errors(draft(name = " ")).isNotEmpty())
        assertTrue(errors(draft(places = listOf(place("Vila Grande")))).any { it.contains("id de lugar inválido") })
        assertTrue(errors(draft(places = listOf(place("a", "b"), place("b", "a"), place("a", "b")))).any { it.contains("id de lugar duplicado") })
        assertTrue(errors(draft(places = listOf(place("a", "b", name = "Vale"), place("b", "a", name = "vale")))).any { it.contains("nome de lugar duplicado") })
        assertTrue(errors(draft(factions = listOf("Guilda", "GUILDA"))).any { it.contains("facção duplicado") })
    }

    @Test fun ligacaoParaLugarInexistenteOuParaSiMesmoEhRecusada() {
        assertTrue(errors(draft(places = listOf(place("a", "x")))).any { it.contains("inexistente") })
        assertTrue(errors(draft(places = listOf(place("a", "a")))).any { it.contains("a si mesmo") })
        assertTrue(errors(draft(start = "nada")).any { it.contains("lugar inicial inexistente") })
    }

    @Test fun mapaDesconexoEhRecusado() {
        val isolado = draft(places = listOf(place("a", "b"), place("b", "a"), place("c")))
        assertTrue(errors(isolado).any { it.contains("desconexo") })
    }

    @Test fun limitesDeRelogiosELugaresValem() {
        val relogio = { n: Int, seg: Int -> CustomClockDraft("r$n", "Relogio $n", seg) }
        assertTrue(errors(draft(clocks = listOf(relogio(1, 1)))).any { it.contains("casas") })
        assertTrue(errors(draft(clocks = listOf(relogio(1, 13)))).any { it.contains("casas") })
        assertTrue(errors(draft(clocks = (1..9).map { relogio(it, 4) })).any { it.contains("relógios demais") })
        val muitos = (1..31).map { place("p$it") }
        assertTrue(errors(draft(places = muitos)).any { it.contains("lugares demais") })
        assertTrue(errors(draft(factions = (1..13).map { "F$it" })).any { it.contains("facções demais") })
    }
}
