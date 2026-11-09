package dev.rpgsolo.world

/** De onde vem o mundo escolhido na criação da campanha (MP-08). */
enum class OrigemMundo {
    /** Crônica de um mundo que já existe (MP-07): depende da persistência de mundos (MI-10 e MI-12). */
    ANTERIOR,

    /** Mundo que acompanha o app (Aeltharyn, Mythralis). */
    PRECONFIGURADO,

    /** O jogador descreve o próprio mundo (ambientação livre). */
    PROPRIO,
}

/** Um reino do molde, como o jogador o vê ao escolher o mundo. */
data class ReinoResumo(val id: String, val nome: String, val tema: String)

/**
 * Molde estático de um mundo (`WorldTemplate`, MP-08): o resumo que a tela de nova campanha mostra e o que o vínculo
 * campanha → mundo grava ([hashMolde] identifica a versão exata do molde). O conteúdo completo continua no [MundoMolde];
 * o molde nunca muda durante a campanha, só os deltas (D-I3, D-I4).
 */
data class WorldTemplate(
    val id: String,
    val nome: String,
    val descricao: String,
    val reinos: List<ReinoResumo>,
    val cidades: Int,
    val assentamentos: Int,
    val npcs: Int,
    val npcsPrincipais: Int,
    val faccoes: Int,
    val hashMolde: String,
    val hashDinamica: String,
) {
    val origem: OrigemMundo get() = OrigemMundo.PRECONFIGURADO
}

/** Moldes de mundo embutidos no app. Hoje só Mythralis; o molde sai de `rules/src/main/resources/mundos/mythralis/`. */
object WorldTemplates {
    const val MYTHRALIS = "world.mythralis"
    const val NOME_MYTHRALIS = "Mythralis"

    private const val DESCRICAO_MYTHRALIS =
        "Mundo vivo e persistente: cinco reinos, povos, cidades, NPCs e facções que continuam a história mesmo fora das cenas."

    /** Resumo carregado do classpath, uma única vez por processo (a carga lê cerca de 130 arquivos de texto: rode fora da thread principal). */
    val mythralis: WorldTemplate by lazy { carregar(MoldeFonteClasspath()) }

    /** Carrega o molde de [fonte] e o resume. Lança [IllegalStateException] se o manifesto, algum arquivo ou o formato estiver errado. */
    fun carregar(fonte: MoldeFonte): WorldTemplate {
        val texto = checkNotNull(fonte.texto("manifesto.txt")) { "molde de Mythralis sem manifesto.txt" }
        val manifesto = MoldeManifesto.ler(texto)
        val mundo = CarregadorMolde.carregar(fonte, manifesto)
        check(mundo.erros.isEmpty()) {
            "molde de Mythralis com ${mundo.erros.size} erro(s) de carga; primeiro: " +
                mundo.erros.first().let { "${it.pasta}/${it.tipo}: ${it.mensagem}" }
        }
        return resumir(mundo, manifesto)
    }

    /** Resumo de um molde já carregado. */
    fun resumir(
        mundo: MundoMolde,
        manifesto: MoldeManifesto,
    ): WorldTemplate {
        val reinos =
            mundo.reinos.values.map { r ->
                ReinoResumo(r.linha.getValue("id"), r.linha["nome"].orEmpty(), r.linha["tema"].orEmpty())
            }

        fun contar(tipo: String) = mundo.t[tipo].orEmpty().size
        return WorldTemplate(
            id = MYTHRALIS,
            nome = NOME_MYTHRALIS,
            descricao = DESCRICAO_MYTHRALIS,
            reinos = reinos,
            cidades = contar("cidade"),
            assentamentos = contar("assentamento"),
            npcs = contar("npc"),
            npcsPrincipais = mundo.t["npc"].orEmpty().count { it["principal"] == "sim" },
            faccoes = contar("faccao"),
            hashMolde = manifesto.moldeSha256,
            hashDinamica = manifesto.dinamicaSha256,
        )
    }
}
