package dev.rpgsolo.world

/** Um fato do cânone do mundo (MI-21), ligado ao delta da crônica que o estabeleceu (`deltaId`; `null` = vem do molde). */
class FatoCanonMundo(
    val chave: String,
    val valor: String,
    val texto: String,
    val deltaId: String?,
)

/**
 * Cânone do mundo para a cena (MI-21). Fatos objetivos que a IA não pode contradizer, lidos do último snapshot e da
 * crônica filtrada: se um NPC da cidade está vivo ou morto (`canon.<npc>.vivo` = `sim`/`nao`) e quem governa a cidade
 * (`canon.<cidade>.governante` = id). Cada fato aponta para o delta que o estabeleceu. Nada é gravado aqui.
 * 🔶 ASSUMINDO: só vida/morte de NPCs da cidade e governante; a contradição é por chave com valor diferente (sem
 * análise de texto livre); as chaves `canon.*` e `mundo.*` são reservadas ao app e a IA não pode gravá-las.
 */
object CanonMundo {
    const val PREFIXO = "canon."
    private const val RESERVADA_MUNDO = "mundo."

    fun chaveVivo(npcId: String) = "$PREFIXO$npcId.vivo"

    fun chaveGovernante(cidadeId: String) = "$PREFIXO$cidadeId.governante"

    @Suppress("UNCHECKED_CAST")
    fun fatos(
        w: MundoMolde,
        estado: Map<String, Any?>?,
        cena: CenaInstanciavel,
        cronica: List<CronicaGuardada> = emptyList(),
    ): List<FatoCanonMundo> {
        val ent = (estado?.get("ent") as? Map<String, Map<String, Any?>>).orEmpty()
        val npcs = w.t.getValue("npc").associateBy { it.getValue("id") }
        val cidadeId = cena.cidadeId

        fun deltaDe(
            entidade: String,
            campo: String,
        ): String? = cronica.lastOrNull { it.entidade == entidade && it.linhaJson.contains("\"campo\":\"$campo\"") }?.deltaId

        fun cidadeDe(id: String) = ent[id]?.get("cidade")?.toString() ?: npcs[id]?.get("cidade")

        fun vivo(id: String) = ent[id]?.get("vivo")?.let { it == true } ?: (npcs[id]?.get("vivo") == "sim")

        fun nome(id: String) = ent[id]?.get("nome")?.toString() ?: npcs[id]?.get("nome")?.split(" — ")?.get(0) ?: id
        val out = ArrayList<FatoCanonMundo>()
        val ids = (npcs.keys + ent.keys.filter { it.startsWith("npc.") }).distinct().filter { cidadeDe(it) == cidadeId }.sorted()
        for (id in ids) {
            val v = vivo(id)
            out += FatoCanonMundo(chaveVivo(id), if (v) "sim" else "nao", "${nome(id)} " + if (v) "está vivo(a)." else "está morto(a).", deltaDe(id, "vivo"))
        }
        val gov = ent[cidadeId]?.get("governante")?.toString() ?: w.t.getValue("cidade").firstOrNull { it["id"] == cidadeId }?.get("governante")
        if (!gov.isNullOrBlank() && gov != "—") {
            out += FatoCanonMundo(chaveGovernante(cidadeId), gov, "${nome(gov)} governa a cidade hoje.", deltaDe(cidadeId, "governante"))
        }
        return out
    }

    /** Motivo da recusa se gravar [chave] = [valor] contradiz o cânone ou invade chave reservada; `null` se pode. */
    fun recusa(
        fatos: List<FatoCanonMundo>,
        chave: String,
        valor: String,
    ): String? {
        val existente = fatos.firstOrNull { it.chave == chave }
        if (existente != null) {
            return if (valor == existente.valor) null else "contradiz o cânone do mundo: ${existente.texto}"
        }
        if (chave.startsWith(PREFIXO) || chave.startsWith(RESERVADA_MUNDO)) return "a chave '$chave' é reservada ao mundo"
        return null
    }

    /** Motivo da recusa se criar o NPC [id] contradiz o cânone (morto no mundo); `null` se pode. */
    fun recusaNpc(
        fatos: List<FatoCanonMundo>,
        id: String,
    ): String? = fatos.firstOrNull { it.chave == chaveVivo(id) && it.valor == "nao" }?.let { "contradiz o cânone do mundo: ${it.texto}" }

    fun linhas(fatos: List<FatoCanonMundo>): String? = fatos.takeIf { it.isNotEmpty() }?.joinToString("\n") { "- ${it.texto}" }
}
