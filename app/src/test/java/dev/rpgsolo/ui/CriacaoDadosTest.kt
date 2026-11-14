package dev.rpgsolo.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Dados da criação por abas: reinos, cidades, taxonomia, nomes curtos e prévia das árvores de habilidade. */
class CriacaoDadosTest {
    @Test
    fun parser_ignoraComentariosECabecalhoELeApenasNativos() {
        val cidades =
            CriacaoReinos.parseCidades(
                "# esquema=1\nid|reino|nome|tipo\ncity.a|kingdom.x|Alfa — Cidade A|capital\ncity.b|kingdom.x|Beta|vila\n",
            )
        assertEquals(listOf("Alfa", "Beta"), cidades.map { it.nome })
        val povos =
            CriacaoReinos.parsePovos(
                "# esquema=1\npovo|reino|relacao|cidades|nota|fonte\nelfo|kingdom.x|originário|city.a;city.b|Elfos|doc\nanao|kingdom.x|aliado|city.a||doc\n",
                cidades,
            )
        assertEquals(1, povos.size)
        assertEquals("elfo", povos[0].racaId)
        assertEquals(listOf("Alfa", "Beta"), povos[0].cidades.map { it.nome })
    }

    @Test
    fun reinos_sao5_e_todo_povo_existe_no_catalogo() {
        val reinos = CriacaoReinos.reinos
        assertEquals(5, reinos.size)
        val racas = CharacterCreationCatalog.races.map { it.id }.toSet()
        reinos.forEach { reino ->
            assertTrue("${reino.nome} sem povos", reino.povos.isNotEmpty())
            assertTrue("${reino.nome} sem cidades", reino.cidades.isNotEmpty())
            reino.povos.forEach { assertTrue("povo desconhecido: ${it.racaId}", it.racaId in racas) }
        }
        assertEquals(55, reinos.sumOf { it.povos.size })
    }

    @Test
    fun toda_raca_e_nativa_de_ao_menos_um_reino() {
        CharacterCreationCatalog.races.forEach { raca ->
            assertTrue("${raca.name} sem reino nativo", CriacaoReinos.nativosDe(raca.id).isNotEmpty())
        }
    }

    @Test
    fun subracas_seguem_a_linhagem_local_do_povo() {
        val subracas = CharacterCreationCatalog.subraces
        val florestas = CriacaoReinos.reinos.first { it.id == "kingdom.aeltharyn" }
        val feras = CriacaoReinos.reinos.first { it.id == "kingdom.vale-palancar" }
        val orcFloresta = florestas.povos.first { it.racaId == "orcano" }
        val orcFeras = feras.povos.first { it.racaId == "orcano" }
        assertEquals(listOf("Orc das Matas"), CriacaoReinos.subracasDoPovo(orcFloresta, subracas).map { it.name })
        assertEquals(2, CriacaoReinos.subracasDoPovo(orcFeras, subracas).size)
    }

    @Test
    fun taxonomia_cobre_todas_as_classes_e_profissoes() {
        val classes = CharacterCreationCatalog.classes
        val profissoes = CharacterCreationCatalog.professions
        val catsClasses = CriacaoTaxonomia.agrupar("classes", classes)
        val catsProfissoes = CriacaoTaxonomia.agrupar("profissoes", profissoes)
        assertEquals(6, catsClasses.size)
        assertEquals(10, catsProfissoes.size)
        assertEquals(classes.size, catsClasses.sumOf { c -> c.subgrupos.sumOf { it.second.size } })
        assertEquals(profissoes.size, catsProfissoes.sumOf { c -> c.subgrupos.sumOf { it.second.size } })
        assertTrue(catsClasses.none { it.nome == "Outros" })
        assertTrue(catsProfissoes.none { it.nome == "Outros" })
    }

    @Test
    fun nomes_curtos_das_racas_nao_se_repetem() {
        val racas = CharacterCreationCatalog.races.map { CriacaoNomes.curto(it.name) }
        assertEquals(racas.size, racas.toSet().size)
        val classes = CharacterCreationCatalog.classes.map { CriacaoNomes.curto(it.name) }
        assertEquals(classes.size, classes.toSet().size)
    }

    @Test
    fun previa_das_arvores_tem_uma_gratuita_na_origem() {
        val arqueiro = CriacaoHabilidades.arvore("arqueiro")
        assertEquals(7, arqueiro.size)
        assertTrue(arqueiro.first().freeBaseSkill)
        val ferreiro = CriacaoHabilidades.arvore("ferreiro")
        assertTrue(ferreiro.isNotEmpty())
        assertTrue(ferreiro.first().freeBaseSkill)
        assertTrue(CriacaoHabilidades.arvore("").isEmpty())
    }

    @Test
    fun escolhas_guardam_e_leem_a_origem() {
        val original =
            CharacterChoices(
                raceId = "elfo",
                classId = "arqueiro",
                age = 120,
                sex = "feminino",
                originKind = "native",
                originKingdomId = "kingdom.aeltharyn",
                originCity = "Elarion",
            )
        val lido = CharacterChoices.fromJson(original.toJson())
        assertEquals(original, lido)
        assertEquals("nativo de Elarion, reino Aeltharyn", lido.origemNarrativa())
        assertEquals("", CharacterChoices().origemNarrativa())
        assertEquals(CharacterChoices(), CharacterChoices.fromJson("{\"raceId\":\"\"}"))
    }
}
