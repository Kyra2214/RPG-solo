package dev.rpgsolo.ai

import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.Damage
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.Entity
import dev.rpgsolo.engine.EntityKind
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** N-33.5: descanso comum recupera 50%, o acampamento soma +10% por grau de Construção e a estalagem recupera 100%. */
class AcampamentoN33Test {
    private val rules =
        RestRules(partial = setOf("pv"), campGrade = {
                e ->
            (1..5).lastOrNull { (e.skillTreeLevels["profissao-carpinteiro-$it"] ?: 0) > 0 } ?: 0
        })

    private fun session(grade: Int = 0): CampaignSession {
        val levels = if (grade > 0) mapOf("profissao-carpinteiro-$grade" to 1) else emptyMap()
        return CampaignSession.create(InMemoryEventStore(), CampaignRecord("c", "T", 5L, EngineConfig(deathResource = "pv"))).also {
            it.execute(
                CreateEntity(
                    Entity("hero", "Herói", EntityKind.PLAYER, resources = mapOf("pv" to Resource(100, 100)), skillTreeLevels = levels),
                ),
            )
            it.execute(Damage("hero", "pv", 90))
        }
    }

    private fun pv(s: CampaignSession) = s.state.entities.getValue("hero").resources.getValue("pv").current

    @Test fun semAcampamentoRecuperaMetade() {
        val s = session()
        LocalActions.run(LocalAction.REST, s, "hero", setOf("pv"), rules)
        assertEquals(60, pv(s))
    }

    @Test fun acampamentoSomaDezPorCentoPorGrau() {
        val s = session(3)
        val r = LocalActions.run(LocalAction.REST, s, "hero", setOf("pv"), rules) as LocalResult.Done
        assertEquals(90, pv(s))
        assertTrue(r.text.contains("acampamento"))
        val s5 = session(5)
        LocalActions.run(LocalAction.REST, s5, "hero", setOf("pv"), rules)
        assertEquals(100, pv(s5))
    }

    @Test fun estalagemRecuperaTudoESemRegraTambem() {
        val s = session()
        LocalActions.run(LocalAction.INN_REST, s, "hero", setOf("pv"), rules)
        assertEquals(100, pv(s))
        val legado = session()
        LocalActions.run(LocalAction.REST, legado, "hero", setOf("pv"))
        assertEquals(100, pv(legado))
        assertEquals(LocalAction.INN_REST, LocalActions.match("dormir na estalagem"))
    }
}
