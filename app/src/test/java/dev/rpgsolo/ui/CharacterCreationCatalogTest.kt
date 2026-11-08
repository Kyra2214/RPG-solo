package dev.rpgsolo.ui

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterCreationCatalogTest {
    @Test
    fun catalogoAtualValidaComEsquemasEspecificosPorCategoria() {
        val errors = CharacterCreationCatalog.validationErrors()
        assertTrue("O catálogo embutido deve ser válido, erros: ${errors.joinToString()}", errors.isEmpty())
    }

    @Test
    fun catalogoDisponibilizaClassesEProfissoes() {
        assertTrue("O catálogo deve carregar classes", CharacterCreationCatalog.classes.isNotEmpty())
        assertTrue("O catálogo deve carregar profissões", CharacterCreationCatalog.professions.isNotEmpty())
    }

    @Test
    fun efeitosMecanicosDeTodasAsCategoriasUsamEscoposDoPacote() {
        val pkg = BundledContent.load().registry.get("sistema-proprio")
        assertNotNull("O pacote próprio deve estar instalado para validar escopos", pkg)
        val choices =
            CharacterCreationCatalog.races.map { CharacterChoices(raceId = it.id) } +
                CharacterCreationCatalog.subraces.map { CharacterChoices(subraceId = it.id) } +
                CharacterCreationCatalog.hybrids.map { CharacterChoices(hybridId = it.id) } +
                CharacterCreationCatalog.classes.map { CharacterChoices(classId = it.id) } +
                CharacterCreationCatalog.professions.map { CharacterChoices(professionId = it.id) }
        val errors = choices.flatMap { CharacterCreationCatalog.validateEffectScopes(pkg!!, it) }.distinct()
        assertTrue("Nenhum efeito deve apontar para escopo inexistente: ${errors.joinToString()}", errors.isEmpty())
    }
}
