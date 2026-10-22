package dev.rpgsolo.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PersistenceTest {
    private val rec = CampaignRecord("c1", "Teste", 99L, EngineConfig(levelThresholds = listOf(10)))
    private fun hero() = Entity("hero", "Herói", EntityKind.PLAYER,
        stats = mapOf("forca" to 2), resources = mapOf("hp" to Resource(10, 10)))
    private fun orc() = Entity("orc", "Orc", EntityKind.NPC, resources = mapOf("hp" to Resource(6, 6)))
    private fun newSession(store: EventStore = InMemoryEventStore()): Pair<CampaignSession, EventStore> {
        val s = CampaignSession.create(store, rec)
        s.executeAll(listOf(CreateEntity(hero()), CreateEntity(orc())))
        return s to store
    }

    @Test fun todoEventoTemCodecIdaEVolta() {
        val samples = listOf<Event>(
            EntityCreated(hero()), ResourceChanged("hero", "hp", 3, -7), EntityDied("orc"),
            EntityRevived("orc", "milagre"), ItemChanged("hero", "pocao", 2), XpGained("hero", 5),
            LevelGained("hero", 2), FactRecorded("k", "v"),
            KnowledgeGained("s", "texto", "pista", true), PromiseMade("p", "orc", "poupar"), PromiseKept("p"),
            ConditionAdded("hero", "cego"), ConditionRemoved("hero", "cego"), CombatStarted(listOf("hero", "orc")),
            TurnAdvanced(1, 2), CombatEnded, RollRecorded("t", "2d6+1", listOf(3, 4), 1, 8)
        )
        for (e in samples) assertEquals(e, Codec.decodeEvent(Codec.encode(e)))
    }

    @Test fun eventoAntigoDeRelogioSemCombatTickContinuaDecodificavel() {
        val encoded = Codec.encode(ClockCreated("old", "Relógio antigo", 4, publicClue = "Sinal público"))
        val legacy = encoded.replace(Regex(",\\\"combatTick\\\":\\d+"), "")

        assertEquals(ClockCreated("old", "Relógio antigo", 4, publicClue = "Sinal público"), Codec.decodeEvent(legacy))
    }

    @Test fun configEFichaTambemVoltamIguais() {
        assertEquals(rec.config, Codec.decodeConfig(Codec.encode(rec.config)))
        val f = Ficha("f", "Brokk", SystemRef("s", "1e"), "anão", mapOf("forca" to 3),
            mapOf("hp" to Resource(8, 8)), listOf("Máquinas"))
        assertEquals(f, Codec.decodeFicha(Codec.encode(f)))
    }

    @Test fun escolhasEditoriaisPersistemNoJsonCanonicoDaFicha() {
        val choices = """{"raceId":"elfo","subraceId":"elfo-da-lua","classId":"guerreiro","professionId":"ferreiro","curseIds":["marca-do-vazio"],"hybridId":""}"""
        val ficha = Ficha("f-editorial", "Ari", SystemRef("s", "1e"), editorialChoicesJson = choices)
        assertEquals(choices, Codec.decodeFicha(Codec.encode(ficha)).editorialChoicesJson)
    }

    @Test fun fichaAntigaSemCampoEditorialContinuaDecodificavel() {
        val ficha = Ficha("f-antiga", "Brokk", SystemRef("s", "1e"))
        val oldJson = Codec.encode(ficha).replace(",\"editorialChoicesJson\":\"\"", "")
        assertTrue(oldJson.contains("\"id\":\"f-antiga\""))
        assertEquals("", Codec.decodeFicha(oldJson).editorialChoicesJson)
    }

    @Test fun backupPreservaEstadoArquivadoELeBackupsAntigos() {
        val (_, store) = newSession()
        val metadata = CampaignBackupMetadata(archivedFichaIds = listOf("f-arquivada"))
        val current = Backup.export(store, "c1", metadata)
        assertEquals(listOf("f-arquivada"), Backup.decode(current).metadata?.archivedFichaIds)

        // Backups anteriores ao campo opcional não devem falhar na leitura.
        val oldBackup = Backup.export(store, "c1", CampaignBackupMetadata())
            .replace(",\"archivedFichaIds\":[]", "")
        assertTrue(oldBackup.contains("\"metadata\""))
        assertEquals(emptyList<String>(), Backup.decode(oldBackup).metadata?.archivedFichaIds)
    }

    @Test fun autosaveReabreComOMesmoEstado() {
        val (s, store) = newSession()
        s.execute(Damage("orc", "hp", 4))
        s.execute(AddItem("hero", "pocao", 1))
        s.check(CheckSpec("ataque", 1, 20, 2, 10))
        s.execute(GainXp("hero", 12))
        val reopened = CampaignSession.open(store, "c1")
        assertEquals(s.state, reopened.state)
        assertEquals(2, reopened.state.entities.getValue("hero").level)
        assertEquals(s.engine.eventCount, store.size("c1"))
    }

    @Test fun comandoRecusadoNaoGravaNada() {
        val (s, store) = newSession()
        val before = store.size("c1")
        assertTrue(s.execute(Heal("ninguem", "hp", 1)) is ExecResult.Rejected)
        assertEquals(before, store.size("c1"))
    }

    @Test fun loteRecusadoNaoGravaParte() {
        val (s, store) = newSession()
        val before = store.size("c1")
        s.executeAll(listOf(Damage("orc", "hp", 1), Damage("fantasma", "hp", 1)))
        assertEquals(before, store.size("c1"))
    }

    @Test fun desfazerApagaDoBancoEOsMesmosDadosVoltam() {
        val (s, store) = newSession()
        val m = s.mark()
        val r1 = s.check(CheckSpec("t", 1, 20, 0, 10))
        s.execute(Damage("orc", "hp", 2))
        s.rollbackTo(m)
        assertEquals(m, store.size("c1"))
        assertEquals(r1.roll.dice, s.check(CheckSpec("t", 1, 20, 0, 10)).roll.dice)
        assertEquals(s.state, CampaignSession.open(store, "c1").state)
    }

    @Test fun combatePersisteELogoContinuaDeOndeParou() {
        val (s, store) = newSession()
        assertTrue(s.rollInitiative(listOf("hero", "orc")) is ExecResult.Accepted)
        s.execute(NextTurn)
        val reopened = CampaignSession.open(store, "c1")
        assertEquals(s.state.combat, reopened.state.combat)
        assertNotNull(reopened.state.combat)
        // continua jogando depois de reabrir, e os dados seguem a mesma sequência
        assertEquals(
            s.check(CheckSpec("x", 1, 20, 0, 10)).roll.dice,
            reopened.check(CheckSpec("x", 1, 20, 0, 10)).roll.dice
        )
    }

    @Test fun logIlegivelDaErroClaro() {
        val store = InMemoryEventStore()
        store.createCampaign(rec)
        store.append("c1", 0, listOf("isto não é json"))
        val ex = runCatching { CampaignSession.open(store, "c1") }.exceptionOrNull()
        assertTrue(ex is IllegalStateException && ex.message!!.contains("evento 0"))
    }

    @Test fun anexarComTamanhoDesatualizadoEhRecusado() {
        val (_, store) = newSession()
        val r = runCatching { store.append("c1", 0, listOf(Codec.encode(CombatEnded))) }
        assertTrue(r.isFailure)
    }
}
