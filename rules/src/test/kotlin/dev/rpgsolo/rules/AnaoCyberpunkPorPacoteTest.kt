package dev.rpgsolo.rules

import dev.rpgsolo.engine.Ambientacao
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.Campanha
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.InMemoryEventStore
import dev.rpgsolo.engine.RegraAdicional
import dev.rpgsolo.engine.RuleOrigin
import dev.rpgsolo.engine.SystemRef
import dev.rpgsolo.engine.validateState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Fase 11.1.8: o exemplo obrigatório ("anão minerador de máquinas de combate num cenário cyberpunk") passa
 * pelos passos do plano com CADA pacote instalado, para provar que o motor não depende de um sistema.
 * Os passos de narração (6) dependem da IA e ficam fora; aqui valem os passos mecânicos.
 */
class AnaoCyberpunkPorPacoteTest {
    private fun dir(path: String): File? = listOf(path, "rules/$path", "../$path").map(::File).firstOrNull { it.exists() }

    private fun files(d: File) = d.listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }

    private fun packages(): List<RulePackage> {
        val reg = PackageRegistry()
        listOf("teste-basico", "teste-avancado", "sistema-proprio").forEach { n ->
            val base = if (n == "sistema-proprio") "main" else "test"
            val d = requireNotNull(dir("src/$base/resources/packages/$n")) { "pacote ausente: $n" }
            val r = reg.install(files(d))
            assertTrue("$n: ${(r as? InstallResult.Failure)?.errors}", r is InstallResult.Success)
        }
        return reg.installed().map { reg.get(it.id)!! }
    }

    @Test fun todosOsPacotesPassamNoRoteiro() {
        val all = packages()
        assertTrue(all.size >= 3)
        all.forEach { roteiro(it) }
    }

    private fun roteiro(p: RulePackage) {
        val id = p.manifest.id
        val concept = "anão minerador especializado em máquinas de combate"

        // 1. identifica sistema e edição exata
        assertTrue("$id: sistema/edição", p.manifest.system.isNotBlank() && p.manifest.edition.isNotBlank())

        // 2. cria a ficha pelas regras da edição (a partir de um modelo do próprio pacote)
        val ex = requireNotNull(p.data.examples.firstOrNull()) { "$id: pacote sem ficha de exemplo" }
        val draft = CharacterDraft(p, "dorn")
        assertTrue(draft.loadExample(ex.id))
        draft.name = "Dorn"
        draft.concept = concept
        val built = draft.build()
        assertTrue("$id: ${(built as? FichaBuild.Invalid)?.errors}", built is FichaBuild.Created)
        val ficha = (built as FichaBuild.Created).ficha

        // 3. preserva atributos, recursos, vantagens/desvantagens do sistema
        assertEquals("$id: sistema da ficha", SystemRef(p.manifest.system, p.manifest.edition), ficha.system)
        assertEquals("$id: traços", ex.traits, ficha.traits)
        assertTrue("$id: atributos", p.data.stats.all { ficha.stats.containsKey(it.id) })

        // 4. "minerador" e "máquinas de combate" são conceito: não viram atributo nem traço inventado
        assertEquals(concept, ficha.concept)
        assertTrue("$id: conceito não vira traço", ficha.traits.none { it.contains("minerador") })

        // 5. adapta o cenário para cyberpunk sem trocar a estrutura da ficha
        val camp = Campanha("camp", ficha.id, ficha.system, Ambientacao("fantasia", "camp", "Fantasia medieval"))
        val cyber = camp.withAmbientacao(Ambientacao("cyber", "camp", "Cyberpunk"))
        assertTrue("$id: campanha coerente", cyber.validate(ficha).isEmpty())
        assertEquals("$id: ficha intacta", ficha.copy(), ficha)

        // 7. capacidade que o sistema não tem vira regra adicional IDENTIFICADA, nunca oficial
        val rule = RegraAdicional("maq", "Máquinas de combate", "opera exoesqueletos", effects = mapOf("maquinas_de_combate" to 1))
        val preview = p.previewAdaptation(ficha, rule)
        assertEquals(RuleOrigin.ADAPTACAO, rule.origin)
        assertTrue(
            "$id: alvo desconhecido exige aprovação",
            preview.requiresApproval && preview.unknownTargets.contains("maquinas_de_combate"),
        )
        assertTrue("$id: recusa sem alvo conhecido", p.applyAdaptation(ficha, rule.copy(approved = true)) is AdaptationResult.Refused)

        // 8. valida pelo motor e salva o estado
        val session = CampaignSession.create(InMemoryEventStore(), CampaignRecord("c-$id", "T", 1L, p.engineConfig()))
        assertTrue("$id: entidade", session.execute(CreateEntity(ficha.toEntity())) is dev.rpgsolo.engine.ExecResult.Accepted)
        p.data.checks.firstOrNull()?.let { c ->
            val diff = p.data.difficulties.firstOrNull()?.value ?: 10
            session.check(p.check(c.id, ficha.stats, diff))
        }
        assertTrue("$id: estado coerente", validateState(session.state).isEmpty())
        val reaberta =
            CampaignSession.open(
                InMemoryEventStore().also { st ->
                    // copia o log para outro armazenamento, como um backup/restauração
                    val text = dev.rpgsolo.engine.Backup.export(storeOf(session), session.campaignId)
                    dev.rpgsolo.engine.Backup.import(st, text)
                },
                session.campaignId,
            )
        assertEquals("$id: estado após reabrir", session.state, reaberta.state)
    }

    // O session não expõe o store; recria um com o mesmo log para exportar.
    private fun storeOf(s: CampaignSession): InMemoryEventStore {
        val st = InMemoryEventStore()
        st.createCampaign(CampaignRecord(s.campaignId, "T", s.engine.seed, s.engine.config))
        val payloads = s.engine.events.map { dev.rpgsolo.engine.Codec.encode(it) }
        if (payloads.isNotEmpty()) st.append(s.campaignId, 0, payloads)
        return st
    }
}
