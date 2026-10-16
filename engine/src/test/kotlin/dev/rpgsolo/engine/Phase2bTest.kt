package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase2bTest {
    private fun ent(id: String, hp: Int = 10, stats: Map<String, Int> = emptyMap()) =
        Entity(id, id, if (id == "hero") EntityKind.PLAYER else EntityKind.NPC,
            stats = stats, resources = mapOf("hp" to Resource(hp, hp)))

    private fun engine(vararg ids: String, seed: Long = 7): GameEngine {
        val e = GameEngine(seed)
        assertTrue(e.executeAll(ids.map { CreateEntity(ent(it)) }) is ExecResult.Accepted)
        return e
    }

    private fun ok(r: ExecResult) = assertTrue(r.toString(), r is ExecResult.Accepted)
    private fun bad(r: ExecResult) = assertTrue(r.toString(), r is ExecResult.Rejected)

    // ---- conhecimento (2.2.3, 11.1.4) ----
    @Test fun conhecimentoExigeFonteEChave() {
        val e = engine("hero")
        bad(e.execute(LearnKnowledge("", "x", "pista")))
        bad(e.execute(LearnKnowledge("k", "x", " ")))
        ok(e.execute(LearnKnowledge("k", "x", "pista")))
    }

    @Test fun fatoConfirmadoNaoVoltaAserSoAcreditado() {
        val e = engine("hero")
        ok(e.execute(LearnKnowledge("traidor", "é o ferreiro", "conversa", believed = true)))
        ok(e.execute(LearnKnowledge("traidor", "é o capitão", "teste", believed = false)))
        bad(e.execute(LearnKnowledge("traidor", "é o ferreiro", "boato", believed = true)))
        assertFalse(e.state.knowledge.getValue("traidor").believed)
    }

    @Test fun segredoDescobertoSobreviveAoReplay() {
        val e = engine("hero")
        e.execute(LearnKnowledge("segredo", "porta oculta", "teste"))
        val restored = GameEngine.restore(7, EngineConfig(), e.events)
        assertEquals(e.state, restored.state)
        assertEquals("porta oculta", restored.state.knowledge.getValue("segredo").text)
    }

    // ---- promessas (2.2.4, 11.1.1) ----
    @Test fun promessaFicaAbertaAteSerCumprida() {
        val e = engine("hero", "rei")
        ok(e.execute(MakePromise("p1", "rei", "dar 100 moedas")))
        assertEquals(1, e.state.openPromises("rei").size)
        bad(e.execute(MakePromise("p1", "rei", "outra")))
        ok(e.execute(KeepPromise("p1")))
        assertTrue(e.state.openPromises().isEmpty())
        bad(e.execute(KeepPromise("p1")))
        bad(e.execute(KeepPromise("nada")))
    }

    @Test fun npcMortoNaoFazPromessa() {
        val e = engine("hero", "rei")
        e.execute(Damage("rei", "hp", 99))
        bad(e.execute(MakePromise("p1", "rei", "x")))
    }

    // ---- condições ----
    @Test fun condicoesNaoDuplicamEPrecisamExistirParaSair() {
        val e = engine("hero")
        ok(e.execute(AddCondition("hero", "envenenado")))
        bad(e.execute(AddCondition("hero", "envenenado")))
        bad(e.execute(RemoveCondition("hero", "cego")))
        ok(e.execute(RemoveCondition("hero", "envenenado")))
        assertNull(e.state.conditions["hero"])
    }

    // ---- combate (2.3.2) ----
    @Test fun turnosGiramEContamRodadas() {
        val e = engine("hero", "orc", "elfo")
        ok(e.execute(StartCombat(listOf("orc", "hero", "elfo"))))
        assertEquals("orc", e.state.combat!!.current)
        e.execute(NextTurn); assertEquals("hero", e.state.combat!!.current)
        e.execute(NextTurn); assertEquals("elfo", e.state.combat!!.current)
        e.execute(NextTurn)
        assertEquals("orc", e.state.combat!!.current)
        assertEquals(2, e.state.combat!!.round)
    }

    @Test fun turnoPulaMortos() {
        val e = engine("hero", "orc", "elfo")
        e.execute(StartCombat(listOf("hero", "orc", "elfo")))
        e.execute(Damage("orc", "hp", 99))
        e.execute(NextTurn)
        assertEquals("elfo", e.state.combat!!.current)
    }

    @Test fun combateRecusaMortoRepetidoEDuplo() {
        val e = engine("hero", "orc")
        e.execute(Damage("orc", "hp", 99))
        bad(e.execute(StartCombat(listOf("hero", "orc"))))
        bad(e.execute(StartCombat(listOf("hero", "hero"))))
        bad(e.execute(StartCombat(emptyList())))
        bad(e.execute(StartCombat(listOf("fantasma"))))
        ok(e.execute(StartCombat(listOf("hero"))))
        bad(e.execute(StartCombat(listOf("hero"))))
        ok(e.execute(EndCombat))
        assertNull(e.state.combat)
        bad(e.execute(NextTurn))
        bad(e.execute(EndCombat))
    }

    @Test fun iniciativaEhDeterministicaEFicaNoLog() {
        fun run(): List<String> {
            val e = engine("hero", "orc", "elfo", seed = 42)
            ok(e.rollInitiative(listOf("hero", "orc", "elfo")))
            assertEquals(3, e.events.count { it is RollRecorded })
            return e.state.combat!!.order
        }
        assertEquals(run(), run())
    }

    @Test fun iniciativaSomaAtributoEOrdenaPorTotal() {
        val e = GameEngine(1)
        e.executeAll(listOf(
            CreateEntity(ent("lento", stats = mapOf("agi" to 0))),
            CreateEntity(ent("rapido", stats = mapOf("agi" to 1000)))))
        ok(e.rollInitiative(listOf("lento", "rapido"), statKey = "agi"))
        assertEquals(listOf("rapido", "lento"), e.state.combat!!.order)
    }

    @Test fun iniciativaRecusadaDesfazAsRolagens() {
        val e = engine("hero", "orc")
        e.execute(Damage("orc", "hp", 99))
        val before = e.eventCount
        bad(e.rollInitiative(listOf("hero", "orc")))
        assertEquals(before, e.eventCount)
        bad(e.rollInitiative(listOf("hero", "fantasma")))
        assertEquals(before, e.eventCount)
    }

    @Test fun desfazerCombateRestauraEstadoEDados() {
        val e = engine("hero", "orc", seed = 5)
        val m = e.mark()
        ok(e.rollInitiative(listOf("hero", "orc")))
        val first = e.state.combat!!.order
        e.rollbackTo(m)
        assertNull(e.state.combat)
        ok(e.rollInitiative(listOf("hero", "orc")))
        assertEquals(first, e.state.combat!!.order)
    }

    @Test fun estadoNovoContinuaCoerente() {
        val e = engine("hero", "orc")
        e.execute(StartCombat(listOf("hero", "orc")))
        e.execute(AddCondition("hero", "atordoado"))
        e.execute(MakePromise("p", "orc", "poupar"))
        assertTrue(validateState(e.state).isEmpty())
        assertEquals(e.state, GameEngine.restore(7, EngineConfig(), e.events).state)
    }

    // ---- camadas (2.2.1b, 9.4.5, 11.1.9, 11.1.10) ----
    private val sys = SystemRef("teste", "1e")
    private fun ficha() = Ficha("f1", "Brokk", sys, concept = "anão minerador de máquinas de combate",
        stats = mapOf("forca" to 3), resources = mapOf("hp" to Resource(8, 8)), traits = listOf("Máquinas"))
    private fun camp(amb: String) = Campanha("c1", "f1", sys, Ambientacao("a-$amb", "c1", amb))

    @Test fun trocarAmbientacaoNaoMudaAFicha() {
        val f = ficha()
        val c = camp("cyberpunk")
        val c2 = c.withAmbientacao(Ambientacao("a-medieval", "outra", "Japão medieval"))
        assertEquals(f, ficha())
        assertEquals("Japão medieval", c2.ambientacao.name)
        assertEquals("c1", c2.ambientacao.campaignId)
        assertTrue(c2.validate(f).isEmpty())
    }

    @Test fun campanhaDetectaFichaDeOutroSistema() {
        val outra = ficha().copy(system = SystemRef("outro", "2e"))
        assertTrue(camp("x").validate(outra).isNotEmpty())
        assertTrue(camp("x").validate(ficha().copy(id = "f9")).isNotEmpty())
    }

    @Test fun regraAdicionalNuncaEhOficialEComecaSemAprovacao() {
        val r = RegraAdicional("r1", "Mineração pesada", "usa britadeira como arma", mapOf("forca" to 1))
        assertEquals(RuleOrigin.ADAPTACAO, r.origin)
        assertFalse(r.approved)
        val c = camp("x").copy(additionalRules = listOf(r))
        assertEquals(1, c.pendingApproval().size)
        assertEquals(0, c.copy(additionalRules = listOf(r.copy(approved = true))).pendingApproval().size)
    }

    @Test fun fichaViraEntidadeSemPerderDados() {
        val ent = ficha().toEntity()
        assertEquals(EntityKind.PLAYER, ent.kind)
        assertEquals(mapOf("forca" to 3), ent.stats)
        assertEquals(8, ent.resources.getValue("hp").max)
    }
}

class ProgressionTest {
    private val cfg = EngineConfig(levelThresholds = listOf(10, 30, 60))
    private fun engine(config: EngineConfig = cfg): GameEngine {
        val e = GameEngine(1, config)
        e.execute(CreateEntity(Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("hp" to Resource(5, 5)))))
        return e
    }
    private fun level(e: GameEngine) = e.state.entities.getValue("hero").level

    @Test fun xpAbaixoDoLimiteNaoSobe() {
        val e = engine(); e.execute(GainXp("hero", 9))
        assertEquals(1, level(e))
    }

    @Test fun xpNoLimiteSobeUmNivel() {
        val e = engine(); e.execute(GainXp("hero", 10))
        assertEquals(2, level(e))
    }

    @Test fun xpGrandeSobeVariosNiveisDeUmaVez() {
        val e = engine(); e.execute(GainXp("hero", 100))
        assertEquals(4, level(e))
        assertEquals(1, e.events.count { it is LevelGained })
    }

    @Test fun xpAcumuladoEmPartesSobeNaHora() {
        val e = engine()
        e.execute(GainXp("hero", 6)); assertEquals(1, level(e))
        e.execute(GainXp("hero", 6)); assertEquals(2, level(e))
        e.execute(GainXp("hero", 6)); assertEquals(2, level(e))
    }

    @Test fun semLimitesNaoSobeDeNivel() {
        val e = engine(EngineConfig()); e.execute(GainXp("hero", 9999))
        assertEquals(1, level(e))
    }

    @Test fun nivelSobreviveAoReplayEDesfazVolta() {
        val e = engine()
        val m = e.mark()
        e.execute(GainXp("hero", 35))
        assertEquals(3, level(GameEngine.restore(1, cfg, e.events)))
        e.rollbackTo(m)
        assertEquals(1, level(e))
    }

    @Test(expected = IllegalArgumentException::class)
    fun limitesForaDeOrdemSaoInvalidos() { EngineConfig(levelThresholds = listOf(30, 10)) }
}
