package dev.rpgsolo.ui

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
}
