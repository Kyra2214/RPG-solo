package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
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
    @Test
    fun installSeedsConnectedMapAndInitialClockProgressAtomically() {
        val store = InMemoryEventStore()
        val session = CampaignSession.create(
            store,
            CampaignRecord("aeltharyn-test", "Aeltharyn", 42L, EngineConfig()),
        )

        assertTrue(AeltharynWorld.install(session) is ExecResult.Accepted)
        assertEquals(10, session.state.places.size)
        assertEquals(setOf(AeltharynWorld.capitalCityId), session.state.knownPlaces)
        assertEquals(AeltharynWorld.capitalCityId, session.state.currentPlaceId)
        assertEquals(7, session.state.clocks.size)
        assertEquals(2, session.state.clocks.getValue("clock.heart-trees").filled)
        assertEquals(3, session.state.clocks.getValue("clock.dikes").filled)
        assertFalse(session.state.places.getValue(AeltharynWorld.capitalCityId).description.contains("Documentos sobre a Guerra"))
        assertTrue(session.state.places.getValue(AeltharynWorld.capitalCityId).secret.contains("Documentos sobre a Guerra"))

        val reopened = CampaignSession.open(store, "aeltharyn-test")
        assertEquals(10, reopened.state.places.size)
        assertEquals(7, reopened.state.clocks.size)
    }

}
