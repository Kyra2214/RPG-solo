package dev.rpgsolo.engine

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** LG-01: registro do legado do herói (quem matou, vínculos) e situação do assassino. */
class LegadoLg01Test {
    private fun hero() =
        Entity(
            "hero",
            "Mira",
            EntityKind.PLAYER,
            resources = mapOf("hp" to Resource(10, 10)),
            level = 7,
            concept = "caçadora de recompensas",
        )

    private fun npc(
        id: String,
        name: String,
    ) = Entity(id, name, EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6)))

    private fun base(): GameState =
        listOf(hero(), npc("goblin", "Grak"), npc("bia", "Bia"), npc("tio", "Tio Ren"))
            .fold(GameState()) { s, e -> s.apply(EntityCreated(e)) }

    private fun relate(
        s: GameState,
        from: String,
        to: String,
        value: Int,
    ) = s.apply(RelationChanged(from, to, value, value, "teste"))

    @Test fun mortePorEntidadeGuardaOAssassino() {
        val s = base().apply(EntityDied("hero", killerId = "goblin"))
        val legacy = s.legacies.getValue("hero")
        assertEquals(DeathKind.SLAIN, legacy.kind)
        assertEquals("goblin", legacy.killerId)
        assertEquals("Grak", legacy.killerName)
        assertEquals("Mira", legacy.heroName)
        assertEquals(7, legacy.level)
        assertEquals("caçadora de recompensas", legacy.concept)
        assertNull(legacy.cause)
    }

    @Test fun morteSemAutorEhDesconhecidaENuncaTemAssassino() {
        val legacy = base().apply(EntityDied("hero")).legacies.getValue("hero")
        assertEquals(DeathKind.UNKNOWN, legacy.kind)
        assertNull(legacy.killerId)
        assertNull(legacy.killerName)
    }

    @Test fun morteDeCondicaoGuardaACausaSemAssassino() {
        val legacy = base().apply(EntityDied("hero", cause = "${CAUSE_CONDITION_PREFIX}veneno")).legacies.getValue("hero")
        assertEquals(DeathKind.CONDITION, legacy.kind)
        assertEquals("condicao:veneno", legacy.cause)
        assertNull(legacy.killerId)
    }

    @Test fun autorInexistenteOuOProprioHeroiNaoViraAssassino() {
        assertEquals(DeathKind.UNKNOWN, base().apply(EntityDied("hero", killerId = "fantasma")).legacies.getValue("hero").kind)
        assertEquals(DeathKind.UNKNOWN, base().apply(EntityDied("hero", killerId = "hero")).legacies.getValue("hero").kind)
    }

    @Test fun morteDeNpcNaoGeraLegado() {
        val s = base().apply(EntityDied("goblin", killerId = "hero"))
        assertTrue(s.legacies.isEmpty())
        assertFalse(s.entities.getValue("goblin").alive)
    }

    @Test fun vinculosFortesEntramOrdenadosEFracosFicamDeFora() {
        var s = base()
        s = relate(s, "bia", "hero", 80)
        s = relate(s, "hero", "tio", -45)
        s = relate(s, "goblin", "hero", 10)
        val bonds = s.apply(EntityDied("hero", killerId = "goblin")).legacies.getValue("hero").bonds
        assertEquals(listOf("bia", "tio"), bonds.map { it.npcId })
        assertEquals(listOf(80, -45), bonds.map { it.value })
        assertEquals("Bia", bonds.first().npcName)
    }

    @Test fun vinculoComNpcMortoNaoEntra() {
        var s = relate(base(), "bia", "hero", 90)
        s = s.apply(EntityDied("bia"))
        assertTrue(s.apply(EntityDied("hero")).legacies.getValue("hero").bonds.isEmpty())
    }

    @Test fun vinculosSaoLimitados() {
        var s = base()
        val extra = (1..8).map { npc("n$it", "N$it") }
        s = extra.fold(s) { acc, e -> relate(acc.apply(EntityCreated(e)), e.id, "hero", 30 + num(e)) }
        val bonds = s.apply(EntityDied("hero")).legacies.getValue("hero").bonds
        assertEquals(LEGACY_BONDS_MAX, bonds.size)
        assertEquals("n8", bonds.first().npcId)
    }

    private fun num(e: Entity) = e.id.removePrefix("n").toInt()

    @Test fun renascerApagaOLegado() {
        val dead = base().apply(EntityDied("hero", killerId = "goblin"))
        assertTrue(dead.legacies.containsKey("hero"))
        val back = dead.apply(EntityRevived("hero", "teste"))
        assertTrue(back.legacies.isEmpty())
        assertTrue(back.entities.getValue("hero").alive)
    }

    @Test fun legadoSobreviveATrocaDeHeroi() {
        val dead = base().apply(EntityDied("hero", killerId = "goblin"))
        val assumed = dead.apply(NpcAssumed("bia", "hero"))
        assertEquals(EntityKind.PLAYER, assumed.entities.getValue("bia").kind)
        assertEquals("goblin", assumed.legacies.getValue("hero").killerId)
    }

    @Test fun segundoHeroiTambemGanhaLegadoSemApagarOPrimeiro() {
        val s =
            base().apply(
                EntityDied("hero", killerId = "goblin"),
            ).apply(NpcAssumed("bia", "hero")).apply(EntityDied("bia", cause = "condicao:fome"))
        assertEquals(setOf("hero", "bia"), s.legacies.keys)
        assertEquals(DeathKind.CONDITION, s.legacies.getValue("bia").kind)
    }

    @Test fun danoComSourceIdMataEGuardaOAssassino() {
        val e = GameEngine(seed = 1)
        e.executeAll(listOf(CreateEntity(hero()), CreateEntity(npc("goblin", "Grak"))))
        assertTrue(e.execute(Damage("hero", "hp", 99, sourceId = "goblin")) is ExecResult.Accepted)
        assertEquals("goblin", e.state.legacies.getValue("hero").killerId)
    }

    @Test fun danoSemSourceIdNaoInventaAssassino() {
        val e = GameEngine(seed = 1)
        e.executeAll(listOf(CreateEntity(hero()), CreateEntity(npc("goblin", "Grak"))))
        e.execute(Damage("hero", "hp", 99))
        val legacy = e.state.legacies.getValue("hero")
        assertEquals(DeathKind.UNKNOWN, legacy.kind)
        assertNull(legacy.killerId)
    }

    @Test fun ataqueDoMotorRegistraOAtacanteComoAssassino() {
        val e = GameEngine(seed = 1)
        e.executeAll(listOf(CreateEntity(hero()), CreateEntity(npc("goblin", "Grak"))))
        assertTrue(e.execute(StartCombat(listOf("goblin", "hero"))) is ExecResult.Accepted)
        val result = e.attack(AttackSpec("goblin", "hero", defenseDefault = 0, damage = 99))
        assertTrue(result is AttackResult.Resolved)
        assertFalse(e.state.entities.getValue("hero").alive)
        assertEquals("goblin", e.state.legacies.getValue("hero").killerId)
    }

    @Test fun eventoAntigoSemCamposNovosAindaDecodifica() {
        val json = Json { classDiscriminator = "t" }
        val old = json.decodeFromString(Event.serializer(), """{"t":"dev.rpgsolo.engine.EntityDied","entityId":"hero"}""")
        assertEquals(EntityDied("hero"), old)
        val round = json.decodeFromString(Event.serializer(), json.encodeToString(Event.serializer(), EntityDied("hero", "goblin", null)))
        assertEquals(EntityDied("hero", "goblin", null), round)
    }

    private fun killer(
        id: String,
        playerId: String? = "bia",
        mutate: (GameState) -> GameState = { it },
    ): KillerStatus {
        val s = mutate(base().apply(EntityDied("hero", killerId = id)))
        return s.legacies.getValue("hero").killerStatus(s, playerId)
    }

    @Test fun statusSemAssassinoEhNone() {
        val s = base().apply(EntityDied("hero"))
        assertEquals(KillerStatus.NONE, s.legacies.getValue("hero").killerStatus(s, "bia"))
    }

    @Test fun statusAssassinoVivoEhAlive() {
        assertEquals(KillerStatus.ALIVE, killer("goblin"))
    }

    @Test fun statusAssassinoMortoEhDead() {
        assertEquals(KillerStatus.DEAD, killer("goblin") { it.apply(EntityDied("goblin")) })
    }

    @Test fun statusAssassinoAliadoEhAlly() {
        assertEquals(KillerStatus.ALLY, killer("goblin") { s -> relate(s, "goblin", "bia", 40) })
    }

    @Test fun statusJogadorControlaOAssassinoEhPlayerControlled() {
        assertEquals(KillerStatus.PLAYER_CONTROLLED, killer("bia", playerId = "bia"))
    }
}
