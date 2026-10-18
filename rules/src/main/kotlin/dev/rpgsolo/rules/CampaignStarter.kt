package dev.rpgsolo.rules

import dev.rpgsolo.engine.Ambientacao
import dev.rpgsolo.engine.Campanha
import dev.rpgsolo.engine.CampaignRecord
import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.engine.CampaignStyle
import dev.rpgsolo.engine.CreateEntity
import dev.rpgsolo.engine.EngineConfig
import dev.rpgsolo.engine.EventStore
import dev.rpgsolo.engine.ExecResult
import dev.rpgsolo.engine.Ficha

sealed interface StartResult {
    data class Started(
        val session: CampaignSession,
        val record: CampaignRecord,
        val campanha: Campanha,
        /** null = a edição não tem pacote instalado: ficha e NPCs ficam "não validados pelo motor" (5.0.4). */
        val pkg: RulePackage?,
        val playerId: String,
        /** Avisos que o jogador deve ver antes de jogar. */
        val notes: List<String>
    ) : StartResult

    /** Recusado na validação: nada foi gravado. */
    data class Refused(val reasons: List<String>) : StartResult
}

/**
 * Liga as camadas (Fase 5.3 / 9.1): configuração resolvida + ficha aprovada → campanha pronta para jogar.
 * Nunca mistura sistemas em silêncio: a ficha precisa ser da mesma edição da campanha.
 */
object CampaignStarter {

    /** Pacote da edição escolhida. Se houver mais de um, o último instalado vence (ex.: variante que herda do base). */
    fun packageFor(registry: PackageRegistry, editionId: String): RulePackage? =
        registry.installed().lastOrNull { it.edition == editionId }?.let { registry.get(it.id) }

    fun start(
        store: EventStore,
        registry: PackageRegistry,
        catalog: Catalog?,
        ready: ConfigResult.Ready,
        config: CampaignConfig,
        ficha: Ficha,
        campaignId: String,
        name: String,
        seed: Long,
        /** Ambientação em texto livre (9.1/4A.1.3); se nula, usa o nome da primeira ambientação do catálogo. */
        customSetting: String? = null
    ): StartResult {
        val edition = ready.edition
        val problems = mutableListOf<String>()
        if (name.isBlank()) problems += "dê um nome à campanha"
        if (store.campaign(campaignId) != null) problems += "já existe uma campanha com o id $campaignId"
        if (ficha.system.editionId != edition.id) {
            problems += "a ficha é de ${ficha.system.editionId}, mas a campanha usa ${edition.id}; fichas de sistemas diferentes não se misturam"
        }
        val pkg = packageFor(registry, edition.id)
        if (pkg != null && ficha.system.editionId == edition.id) {
            val report = pkg.validateCharacter(ficha.stats, ficha.traits, skills = ficha.skills)
            if (!report.valid) problems += report.errors.map { "ficha inválida pelo pacote: $it" }
        }
        if (problems.isNotEmpty()) return StartResult.Refused(problems)

        val settingName = customSetting?.trim()?.takeIf { it.isNotEmpty() }
            ?: config.settingIds.firstNotNullOfOrNull { catalog?.entry(it)?.name }
            ?: "Ambientação livre"
        val tone = config.toneIds.mapNotNull { catalog?.entry(it)?.name }.joinToString(", ")
        val campanha = Campanha(
            id = campaignId,
            fichaId = ficha.id,
            system = ficha.system,
            ambientacao = Ambientacao(id = "amb-$campaignId", campaignId = campaignId, name = settingName, tone = tone),
            style = CampaignStyle(
                settingIds = config.settingIds,
                structureIds = config.structureIds,
                philosophyIds = config.philosophyIds,
                toneIds = config.toneIds,
                difficultyId = config.difficulty.levelId,
                difficultyParams = config.difficulty.params,
                progressionId = config.progressionId,
                contentLimits = config.contentLimits
            )
        )

        val record = CampaignRecord(campaignId, name.trim(), seed, pkg?.engineConfig() ?: EngineConfig())
        val session = CampaignSession.create(store, record)
        when (val r = session.execute(CreateEntity(ficha.toEntity().copy(rulesValidated = pkg != null))) {
            is ExecResult.Accepted -> {}
            is ExecResult.Rejected -> {
                // Defesa: em campanha recém-criada isso não deveria ocorrer; zera o log para não ficar estado pela metade.
                store.truncate(campaignId, 0)
                return StartResult.Refused(listOf("não foi possível criar o personagem: ${r.reason}"))
            }
        }

        val notes = ready.notes.toMutableList()
        if (pkg == null) notes += "não há pacote de regras instalado para ${edition.id}: a ficha e os NPCs não são validados pelo motor"
        return StartResult.Started(session, record, campanha, pkg, ficha.id, notes)
    }
}
