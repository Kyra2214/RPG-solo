package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** MU-08.2: confronto ataque × defesa, crítico, ignorar defesa, registro no log, paridade e exceções. */
class UnifiedCombatTest {
    private fun bd(
        target: String,
        base: Int,
        vararg mods: Modifier,
    ) = ModifierAggregator.aggregate(target, base, mods.toList())

    private fun mod(
        source: String,
        target: String,
        value: Int,
    ) = Modifier(source, source, target, value)

    @Test fun danoEhAtaqueMenosDefesaSemMinimo() {
        assertEquals(6, UnifiedCombat.damageOf(10, 4))
        assertEquals(0, UnifiedCombat.damageOf(3, 5))
        assertEquals(0, UnifiedCombat.damageOf(5, 5))
    }

    @Test fun invarianteDeDanoEmVariasSeeds() {
        repeat(200) { seed ->
            val r = UnifiedCombat.resolve(bd("attack", 9), bd("defense", 7), Dice(seed.toLong()))
            assertTrue(r.damage >= 0)
            if (r.critical) {
                assertNull(r.attackRoll)
                assertEquals(9, r.attackTotal)
            } else {
                assertNotNull(r.attackRoll)
            }
            assertEquals(maxOf(0, r.attackTotal - r.defenseTotal), r.damage)
            r.crit?.roll?.let { assertTrue(it.total in 1..6) }
        }
    }

    @Test fun confrontoEhReproduzivelPelaSeed() {
        val a = UnifiedCombat.resolve(bd("attack", 8), bd("defense", 6), Dice(11))
        val b = UnifiedCombat.resolve(bd("attack", 8), bd("defense", 6), Dice(11))
        assertEquals(a, b)
    }

    @Test fun criticoGarantidoPulaARolagemEUsaValorCheio() {
        val r = UnifiedCombat.resolve(bd("attack", 10), bd("defense", 6), Dice(3), guaranteedCrit = true)
        assertTrue(r.critical)
        assertTrue(r.crit!!.guaranteed)
        assertNull(r.crit!!.roll)
        assertNull(r.attackRoll)
        assertEquals(maxOf(0, 10 - r.defenseTotal), r.damage)
    }

    @Test fun semPermissaoDeCriticoNaoHaCriticoEHaRolagemDeAtaque() {
        val r = UnifiedCombat.resolve(bd("attack", 9), bd("defense", 6), Dice(5), guaranteedCrit = true, canCrit = false)
        assertNull(r.crit)
        assertFalse(r.critical)
        assertNotNull(r.attackRoll)
    }

    @Test fun limiarDeCriticoCaiComBonusAteUm() {
        assertEquals(6, UnifiedCombat.checkCrit(emptyList(), false, Dice(1)).threshold)
        assertEquals(5, UnifiedCombat.checkCrit(listOf(mod("a", "crit", 1)), false, Dice(1)).threshold)
        assertEquals(3, UnifiedCombat.checkCrit(listOf(mod("a", "crit", 1), mod("b", "crit", 2)), false, Dice(1)).threshold)
        val floor = UnifiedCombat.checkCrit(listOf(mod("a", "crit", 20)), false, Dice(1))
        assertEquals(1, floor.threshold)
        assertTrue(floor.critical)
    }

    @Test fun bonusDeCriticoDaMesmaFonteNaoRepete() {
        val c = UnifiedCombat.checkCrit(listOf(mod("a", "crit", 1), mod("a", "crit", 1)), false, Dice(1))
        assertEquals(5, c.threshold)
    }

    @Test fun ignorarDefesaGarantidoCausaCentoECinquentaPorCentoSemDados() {
        val spec = IgnoreDefenseSpec(guaranteed = true, cooldownRounds = 3)
        val r = UnifiedCombat.resolve(bd("attack", 10), bd("defense", 8), Dice(2), ignoreDefense = spec)
        assertTrue(r.ignoredDefense)
        assertNull(r.attackRoll)
        assertNull(r.defenseRoll)
        assertEquals(15, r.damage)
        assertEquals(3, r.ignore!!.cooldownRounds)
        assertNull(r.ignore!!.roll)
    }

    @Test fun ignorarDefesaTemPrioridadeSobreCritico() {
        val spec = IgnoreDefenseSpec(guaranteed = true)
        val r = UnifiedCombat.resolve(bd("attack", 10), bd("defense", 8), Dice(2), guaranteedCrit = true, ignoreDefense = spec)
        assertEquals(15, r.damage)
        assertNull(r.defenseRoll)
    }

    @Test fun percentualDeIgnorarDefesaEhConfiguravel() {
        val spec = IgnoreDefenseSpec(guaranteed = true, percent = 100)
        assertEquals(10, UnifiedCombat.resolve(bd("attack", 10), bd("defense", 8), Dice(2), ignoreDefense = spec).damage)
    }

    @Test fun limiarDeIgnorarDefesaCaiComBonusAteUm() {
        assertEquals(10, UnifiedCombat.checkIgnore(IgnoreDefenseSpec(), Dice(1)).threshold)
        assertEquals(7, UnifiedCombat.checkIgnore(IgnoreDefenseSpec(modifiers = listOf(mod("a", "ignoreDef", 3))), Dice(1)).threshold)
        val floor = UnifiedCombat.checkIgnore(IgnoreDefenseSpec(modifiers = listOf(mod("a", "ignoreDef", 30))), Dice(1))
        assertEquals(1, floor.threshold)
        assertTrue(floor.success)
    }

    @Test fun ignorarDefesaSemGarantiaSoAcertaNoLimiar() {
        repeat(100) { seed ->
            val r =
                UnifiedCombat.resolve(
                    bd("attack", 10),
                    bd("defense", 8),
                    Dice(seed.toLong()),
                    canCrit = false,
                    ignoreDefense = IgnoreDefenseSpec(),
                )
            val face = r.ignore!!.roll!!.total
            assertTrue(face in 1..10)
            assertEquals(face >= 10, r.ignoredDefense)
            if (!r.ignoredDefense) assertNotNull(r.defenseRoll)
        }
    }

    @Test fun especificacaoDeIgnorarDefesaRecusaValoresInvalidos() {
        assertTrue(runCatching { IgnoreDefenseSpec(cooldownRounds = -1) }.isFailure)
        assertTrue(runCatching { IgnoreDefenseSpec(percent = 0) }.isFailure)
    }

    // ---- no motor (estado, log e paridade) ----
    private fun fighter(
        id: String,
        kind: EntityKind,
        attack: Int = 5,
        defense: Int = 2,
        hp: Int = 30,
    ) = Entity(id, id, kind, stats = mapOf("attack" to attack, "defense" to defense), resources = mapOf("hp" to Resource(hp, hp)))

    private fun duel(seed: Long = 7): GameEngine {
        val e = GameEngine(seed)
        assertTrue(
            e.executeAll(
                listOf(
                    CreateEntity(fighter("hero", EntityKind.PLAYER)),
                    CreateEntity(fighter("orc", EntityKind.NPC)),
                    CreateEntity(fighter("elfo", EntityKind.NPC)),
                ),
            ) is ExecResult.Accepted,
        )
        assertTrue(e.execute(StartCombat(listOf("hero", "orc"))) is ExecResult.Accepted)
        return e
    }

    private fun spec(
        attacker: String,
        target: String,
    ) = UnifiedAttackSpec(attacker, target, bd("attack", 10), bd("defense", 3))

    @Test fun ataqueUnificadoGravaConfrontoEAplicaODano() {
        val e = duel()
        val result = e.unifiedAttack(spec("hero", "orc")) as UnifiedAttackResult.Resolved
        val recorded = e.events.filterIsInstance<ClashRecorded>().single()
        assertEquals(result.clash.damage, recorded.damage)
        assertEquals(30 - result.clash.damage, e.state.entities.getValue("orc").resources.getValue("hp").current)
        assertEquals(e.state, GameEngine.restore(7, EngineConfig(), e.events).state)
    }

    @Test fun ataqueUnificadoRecusaSituacoesInvalidas() {
        val e = duel()
        assertTrue(e.unifiedAttack(spec("orc", "hero")) is UnifiedAttackResult.Rejected)
        assertTrue(e.unifiedAttack(spec("hero", "hero")) is UnifiedAttackResult.Rejected)
        assertTrue(e.unifiedAttack(spec("hero", "elfo")) is UnifiedAttackResult.Rejected)
        assertTrue(e.unifiedAttack(spec("hero", "fantasma")) is UnifiedAttackResult.Rejected)
        assertTrue(e.unifiedAttack(spec("hero", "orc").copy(damageResource = "mana")) is UnifiedAttackResult.Rejected)
        assertTrue(e.events.none { it is ClashRecorded })
        e.execute(EndCombat)
        assertTrue(e.unifiedAttack(spec("hero", "orc")) is UnifiedAttackResult.Rejected)
    }

    @Test fun mesmaSeedGeraOMesmoConfrontoNoLog() {
        val a = duel(21)
        val b = duel(21)
        a.unifiedAttack(spec("hero", "orc"))
        b.unifiedAttack(spec("hero", "orc"))
        assertEquals(a.events.filterIsInstance<ClashRecorded>(), b.events.filterIsInstance<ClashRecorded>())
    }

    @Test fun paridadeJogadorENpcUsamAMesmaFormula() {
        val bonus = ModifierProvider { _, _, target -> if (target == "attack") listOf(mod("fonte", "attack", 2)) else emptyList() }
        val e = duel()
        e.unifiedAttackBetween("hero", "orc", listOf(bonus))
        e.execute(NextTurn)
        e.unifiedAttackBetween("orc", "hero", listOf(bonus))
        val clashes = e.events.filterIsInstance<ClashRecorded>()
        assertEquals(2, clashes.size)
        assertEquals(7, clashes[0].attackValue)
        assertEquals(clashes[0].attackValue, clashes[1].attackValue)
        assertEquals(clashes[0].defenseValue, clashes[1].defenseValue)
        assertEquals(clashes[0].attackSources, clashes[1].attackSources)
    }

    @Test fun fontesDiferentesAcumulamNoAtaqueEmotivoApareceNoDetalhamento() {
        val e = duel()
        val providers =
            listOf(
                ModifierProvider { _, _, t -> if (t == "attack") listOf(mod("arma", "attack", 1)) else emptyList() },
                ModifierProvider {
                        _,
                        _,
                        t,
                    ->
                    if (t == "attack") listOf(mod("bencao", "attack", 1), mod("arma", "attack", 1)) else emptyList()
                },
            )
        e.unifiedAttackBetween("hero", "orc", providers)
        val recorded = e.events.filterIsInstance<ClashRecorded>().single()
        assertEquals(7, recorded.attackValue)
        assertTrue(recorded.rejectedSources.any { it.contains("DUPLICATE_SOURCE") })
        val lines = ClashReport.of(recorded).lines()
        assertTrue(lines.first().contains("hero") && lines.first().contains("orc"))
    }

    // ---- exceções mecânicas ----
    private fun stateWith(vararg ids: String) =
        GameEngine(1).also {
            it.executeAll(ids.map { id -> CreateEntity(fighter(id, EntityKind.NPC)) })
        }.state

    @Test fun excecaoValidaEntraNoAgregadorComOrigemEstavel() {
        val state = stateWith("boss", "hero")
        val ex = MechanicalException("couraca", "boss", "Couraça lendária", listOf(mod("x", "defense", 3)), setOf("veneno"))
        val registry = ExceptionRegistry(listOf(ex))
        assertTrue(registry.validate(state).isEmpty())
        val mods = registry.modifiers(state, state.entities.getValue("boss"), "defense")
        assertEquals("excecao:couraca:0", mods.single().sourceId)
        assertTrue(mods.single().label.contains("Couraça lendária"))
        assertTrue(registry.modifiers(state, state.entities.getValue("hero"), "defense").isEmpty())
        assertTrue(registry.isImmune("boss", "veneno"))
        assertFalse(registry.isImmune("hero", "veneno"))
    }

    @Test fun excecaoInvalidaEhRecusadaNaValidacao() {
        val state = stateWith("boss")
        val effect = listOf(mod("x", "defense", 1))
        assertEquals(1, ExceptionRegistry(listOf(MechanicalException("a", "boss", " ", effect))).validate(state).size)
        assertEquals(1, ExceptionRegistry(listOf(MechanicalException("a", "fantasma", "d", effect))).validate(state).size)
        assertEquals(1, ExceptionRegistry(listOf(MechanicalException("a", "boss", "d"))).validate(state).size)
        assertEquals(
            1,
            ExceptionRegistry(
                listOf(MechanicalException("a", "boss", "d", effect), MechanicalException("a", "boss", "d", effect)),
            ).validate(state).size,
        )
        assertEquals(1, ExceptionRegistry(listOf(MechanicalException("", "boss", "d", effect))).validate(state).size)
    }
}
