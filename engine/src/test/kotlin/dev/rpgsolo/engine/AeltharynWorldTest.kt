package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AeltharynWorldTest {
    @Test
    fun catalogueHasTenConnectedCitiesAndElvenAdjacentPeople() {
        assertEquals(10, AeltharynWorld.cities.size)
        assertEquals(16, AeltharynWorld.people.size)
        assertEquals(emptyList<String>(), AeltharynWorld.validate())
    }

    @Test
    fun eachCityHasHeroGuardianAndBenefactor() {
        AeltharynWorld.cities.forEach { city ->
            val roles = AeltharynWorld.figures.filter { it.cityId == city.id }.map { it.role }.toSet()
            assertTrue("${city.id} missing HERO", "HERO" in roles)
            assertTrue("${city.id} missing GUARDIAN", "GUARDIAN" in roles)
            assertTrue("${city.id} missing BENEFACTOR", "BENEFACTOR" in roles)
        }
    }

    @Test
    fun routesAndClocksHaveValidReferencesAndRanges() {
        assertTrue(AeltharynWorld.routes.all { it.days > 0 })
        assertTrue(AeltharynWorld.clocks.all { it.initialProgress in 0..it.segments })
        assertEquals(emptyList<String>(), AeltharynWorld.validate())
    }
}
