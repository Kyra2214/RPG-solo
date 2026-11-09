package dev.rpgsolo.rules

import dev.rpgsolo.engine.AeltharynWorld
import dev.rpgsolo.world.OrigemMundo
import dev.rpgsolo.world.WorldTemplates

/**
 * Uma escolha de mundo na criação da campanha (MP-08). [id] é o `worldId` que [CampaignStarter.start] recebe;
 * `null` significa mundo próprio (ambientação livre, o comportamento de sempre).
 */
data class WorldOption(
    val id: String?,
    val name: String,
    val summary: String,
    val origin: OrigemMundo,
    val available: Boolean = true,
    /** Por que a opção não pode ser escolhida agora (só quando [available] é falso). */
    val unavailableReason: String? = null,
)

/** As três vias do MP-08: mundo anterior, mundo pré-configurado e mundo próprio. */
object WorldOptions {
    /** Id da opção "mundo anterior"; ela só fica disponível quando houver mundos persistidos (MI-10 e MI-12). */
    const val PREVIOUS = "world.previous"

    /**
     * Opções na ordem da tela. A carga do molde de Mythralis lê arquivos: chame fora da thread principal.
     * Se o molde não carregar, a opção aparece indisponível com o motivo, sem derrubar a lista.
     */
    fun list(): List<WorldOption> {
        val mythralis =
            runCatching { WorldTemplates.mythralis }.fold(
                onSuccess = { t ->
                    WorldOption(
                        id = t.id,
                        name = "${t.nome} — mundo vivo",
                        summary =
                            "${t.reinos.size} reinos, ${t.cidades} cidades, ${t.npcs} NPCs e ${t.faccoes} facções que evoluem com o tempo. " +
                                "A campanha fica vinculada ao molde; cidades e NPCs entram conforme as cenas os tocam.",
                        origin = OrigemMundo.PRECONFIGURADO,
                    )
                },
                onFailure = { e ->
                    WorldOption(
                        id = WorldTemplates.MYTHRALIS,
                        name = "${WorldTemplates.NOME_MYTHRALIS} — mundo vivo",
                        summary = "Molde indisponível.",
                        origin = OrigemMundo.PRECONFIGURADO,
                        available = false,
                        unavailableReason = e.message ?: "falha ao carregar o molde",
                    )
                },
            )
        return listOf(
            WorldOption(
                id = null,
                name = "Criar o meu mundo",
                summary = "Descreva livremente; a ambientação que você escrever vale para a campanha.",
                origin = OrigemMundo.PROPRIO,
            ),
            WorldOption(
                id = AeltharynWorld.ID,
                name = AeltharynWorld.NAME,
                summary = "Dez cidades, mapa conectado, sete relógios e NPCs com dossiês; só Elarion começa descoberta.",
                origin = OrigemMundo.PRECONFIGURADO,
            ),
            mythralis,
            WorldOption(
                id = PREVIOUS,
                name = "Mundo anterior",
                summary = "Continuar o mundo de uma campanha que já existe.",
                origin = OrigemMundo.ANTERIOR,
                available = false,
                unavailableReason = "ainda não há mundos guardados; chega com a persistência de mundos (MI-10 e MI-12)",
            ),
        )
    }
}
