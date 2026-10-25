package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Test

/** N-36: nomes dos conjuntos por peso e mapeamento das 50 classes (13 pesadas, 18 médias, 19 leves). */
class ConjuntosPesoN36Test {
    @Test fun nomesPorPeso() {
        assertEquals(listOf("Baluarte", "Couraça", "Titã"), ItemWeight.PESADO.setNames)
        assertEquals(listOf("Patrulha", "Malha", "Andarilho"), ItemWeight.MEDIO.setNames)
        assertEquals(listOf("Véu", "Manto", "Sombra"), ItemWeight.LEVE.setNames)
    }
}
