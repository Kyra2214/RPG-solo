package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun everyElvenAdjacentPeopleHasHeroGuardianAndBenefactor() {
        AeltharynWorld.people.forEach { person ->
            val roles = AeltharynWorld.figures.filter { it.peopleId == person.id }.map { it.role }.toSet()
            assertTrue("${person.id} missing HERO", "HERO" in roles)
            assertTrue("${person.id} missing GUARDIAN", "GUARDIAN" in roles)
            assertTrue("${person.id} missing BENEFACTOR", "BENEFACTOR" in roles)
        }
    }

    @Test
    fun routesAndClocksHaveValidReferencesAndRanges() {
        assertTrue(AeltharynWorld.routes.all { it.days > 0 })
        assertTrue(AeltharynWorld.clocks.all { it.initialProgress in 0..it.segments })
        assertEquals(emptyList<String>(), AeltharynWorld.validate())
    }

    @Test
    fun sceneContextLimitsCityAndSecretInformation() {
        val city = AeltharynWorld.cities.first { it.id == AeltharynWorld.CAPITAL_CITY_ID }
        val context = AeltharynWorld.sceneContext(city.id, setOf(city.id))

        assertTrue(context!!.contains(city.name))
        assertFalse(context.contains(city.activeProblem))
        city.dependsOn.filter { it != city.id }.forEach { dependencyId ->
            assertFalse(context.contains(AeltharynWorld.cities.first { it.id == dependencyId }.name))
        }
        assertEquals(null, AeltharynWorld.sceneContext("city.unknown", emptySet()))
    }

    @Test
    fun installSeedsConnectedMapAndInitialClockProgressAtomically() {
        val store = InMemoryEventStore()
        val session =
            CampaignSession.create(
                store,
                CampaignRecord("aeltharyn-test", "Aeltharyn", 42L, EngineConfig()),
            )

        assertTrue(AeltharynWorld.install(session) is ExecResult.Accepted)
        assertEquals(10, session.state.places.size)
        assertEquals(setOf(AeltharynWorld.CAPITAL_CITY_ID), session.state.knownPlaces)
        assertEquals(AeltharynWorld.CAPITAL_CITY_ID, session.state.currentPlaceId)
        assertEquals(7, session.state.clocks.size)
        val activeFigures = AeltharynWorld.figures.filter { it.aliveAtStart }
        assertEquals(activeFigures.size, session.state.entities.values.count { it.kind == EntityKind.NPC })
        assertEquals(activeFigures.map { it.id }.toSet(), session.state.npcDossiers.keys)
        assertEquals(2, session.state.clocks.getValue("clock.heart-trees").filled)
        assertEquals(3, session.state.clocks.getValue("clock.dikes").filled)
        assertFalse(
            session.state.places.getValue(AeltharynWorld.CAPITAL_CITY_ID).description.contains("Documentos sobre a Guerra"),
        )
        assertTrue(
            session.state.places.getValue(AeltharynWorld.CAPITAL_CITY_ID).secret.contains("Documentos sobre a Guerra"),
        )

        val reopened = CampaignSession.open(store, "aeltharyn-test")
        assertEquals(10, reopened.state.places.size)
        assertEquals(7, reopened.state.clocks.size)
    }
}
