package dev.rpgsolo.ui

import dev.rpgsolo.engine.SkillTreeSkill
import dev.rpgsolo.rules.SkillTreeCatalogLoader
import dev.rpgsolo.rules.SkillTreeCatalogResult
import org.json.JSONObject
import java.text.Normalizer

/**
 * Dados da criação de personagem por abas (CG): reinos, cidades, taxonomia de classes e profissões e árvores de habilidades.
 * Tudo vem de arquivos empacotados no módulo `rules`; nada aqui cria regra mecânica.
 */

/** Cidade de um reino (`dados/cidade.txt`). */
data class CriacaoCidade(val id: String, val nome: String)

/** Povo nativo de um reino: raça do catálogo, nota editorial (linhagem local) e cidades onde vive. */
data class CriacaoPovo(val racaId: String, val nota: String, val cidades: List<CriacaoCidade>)

data class CriacaoReino(
    val id: String,
    val nome: String,
    val tema: String,
    val povos: List<CriacaoPovo>,
    val cidades: List<CriacaoCidade>,
)

object CriacaoReinos {
    private val pastas =
        listOf(
            "01-aeltharyn-reino-das-florestas",
            "02-coroas-de-pedra-reino-das-montanhas",
            "03-veu-dos-malditos",
            "04-vale-palancar",
            "05-azeroth",
        )

    /** Os cinco reinos de Mythralis, na ordem editorial; lista vazia se os arquivos não estiverem no pacote. */
    val reinos: List<CriacaoReino> by lazy { pastas.mapNotNull { carregar(it) } }

    private fun ler(
        pasta: String,
        arquivo: String,
    ): String? =
        runCatching {
            CriacaoReinos::class.java.classLoader
                ?.getResourceAsStream("mundos/mythralis/reinos/$pasta/dados/$arquivo")
                ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
        }.getOrNull()

    private fun carregar(pasta: String): CriacaoReino? {
        val base = parseReino(ler(pasta, "reino.txt") ?: return null) ?: return null
        val cidades = parseCidades(ler(pasta, "cidade.txt").orEmpty())
        val povos = parsePovos(ler(pasta, "povo_no_reino.txt").orEmpty(), cidades)
        return base.copy(povos = povos, cidades = cidades)
    }

    /** Linhas de dados já separadas por "|": ignora vazias e comentários e descarta o cabeçalho. */
    internal fun linhas(texto: String): List<List<String>> =
        texto.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .map { it.split("|") }
            .toList()
            .drop(1)

    internal fun parseReino(texto: String): CriacaoReino? {
        val c = linhas(texto).firstOrNull() ?: return null
        if (c.size < 4) return null
        return CriacaoReino(c[0].trim(), c[1].trim(), c[3].substringBefore(",").trim(), emptyList(), emptyList())
    }

    internal fun parseCidades(texto: String): List<CriacaoCidade> =
        linhas(texto).filter { it.size >= 3 }.map { CriacaoCidade(it[0].trim(), it[2].substringBefore(" — ").trim()) }

    internal fun parsePovos(
        texto: String,
        cidades: List<CriacaoCidade>,
    ): List<CriacaoPovo> {
        val porId = cidades.associateBy { it.id }
        return linhas(texto)
            .filter { it.size >= 4 && it[2].trim().equals("originário", ignoreCase = true) }
            .map { c ->
                CriacaoPovo(
                    racaId = c[0].trim(),
                    nota = c.getOrElse(4) { "" }.trim(),
                    cidades = c[3].split(";").mapNotNull { porId[it.trim()] },
                )
            }
    }

    /** Reinos onde a raça é nativa, com o povo correspondente. */
    fun nativosDe(racaId: String): List<Pair<CriacaoReino, CriacaoPovo>> =
        reinos.flatMap { reino -> reino.povos.filter { it.racaId == racaId }.map { reino to it } }

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")

    /**
     * Sub-raças da raça que combinam com a linhagem local do povo (a nota editorial cita o complemento do nome,
     * por exemplo "das Matas"); se nenhuma combinar, todas as sub-raças da raça.
     */
    fun subracasDoPovo(
        povo: CriacaoPovo,
        subracas: List<CharacterCatalogEntry>,
    ): List<CharacterCatalogEntry> {
        val todas = subracas.filter { it.parentRaceId == povo.racaId }
        val nota = normalizar(povo.nota)
        val locais =
            todas.filter { s ->
                val sufixo = normalizar(s.name).split(" ").drop(1).joinToString(" ")
                sufixo.isNotBlank() && nota.contains(sufixo)
            }
        return locais.ifEmpty { todas }
    }
}

/** Grupo de uma classe ou profissão na taxonomia (rascunho aprovado em D-CG-11). */
data class CriacaoGrupo(val categoria: String, val subgrupo: String)

/** Categoria com seus subgrupos, já na ordem do catálogo. */
data class CriacaoCategoria(
    val nome: String,
    val rotulo: String,
    val subgrupos: List<Pair<String, List<CharacterCatalogEntry>>>,
)

object CriacaoTaxonomia {
    private val raiz: JSONObject? by lazy {
        runCatching {
            CriacaoTaxonomia::class.java.classLoader
                ?.getResourceAsStream("packages/sistema-proprio/taxonomia-criacao.json")
                ?.bufferedReader(Charsets.UTF_8)?.use { JSONObject(it.readText()) }
        }.getOrNull()
    }

    /** [tipo] é "classes" ou "profissoes"; a chave é o id do catálogo. */
    fun grupo(
        tipo: String,
        id: String,
    ): CriacaoGrupo? {
        val item = raiz?.optJSONObject(tipo)?.optJSONObject(id) ?: return null
        val categoria = item.optString("categoria")
        if (categoria.isBlank()) return null
        return CriacaoGrupo(categoria, item.optString("subgrupo").ifBlank { categoria })
    }

    /** Agrupa as entradas por categoria e subgrupo, na ordem em que aparecem no catálogo; sem taxonomia, tudo vai para "Outros". */
    fun agrupar(
        tipo: String,
        itens: List<CharacterCatalogEntry>,
    ): List<CriacaoCategoria> {
        val categorias = linkedMapOf<String, LinkedHashMap<String, MutableList<CharacterCatalogEntry>>>()
        itens.forEach { entrada ->
            val grupo = grupo(tipo, entrada.id) ?: CriacaoGrupo("Outros", "Outros")
            val subgrupos = categorias.getOrPut(grupo.categoria) { linkedMapOf<String, MutableList<CharacterCatalogEntry>>() }
            subgrupos.getOrPut(grupo.subgrupo) { mutableListOf<CharacterCatalogEntry>() }.add(entrada)
        }
        return categorias.map { (nome, subgrupos) ->
            CriacaoCategoria(nome, CriacaoNomes.rotuloCategoria(nome), subgrupos.map { (sub, lista) -> sub to lista.toList() })
        }
    }
}

/** Nomes curtos para os blocos da grade (o nome completo aparece no painel) e glifos de apoio sem arte. */
object CriacaoNomes {
    private val curtos =
        mapOf(
            "Nascido do Crepúsculo" to "Crepúsculo",
            "Felídeo Errante" to "Felídeo",
            "Abissal Costeiro" to "Abissal",
            "Silvano Festivo" to "Silvano",
            "Goblin de Legião" to "Legião",
            "Ursino Sombrio" to "Sombrio",
            "Escamado Miúdo" to "Miúdo",
            "Nascido da Rocha" to "Rocha",
            "Nascido da Brasa" to "Brasa",
            "Nascido da Maré" to "Maré",
            "Nascido do Vento" to "Vento",
            "Construto Desperto" to "Construto",
            "Metamorfo Lupino" to "Metamorfo",
            "Felino das Nuvens" to "Nuvens",
            "Guardião do Código" to "Código",
            "Guardião Rúnico" to "Rúnico",
            "Guardião de Relíquias" to "Relíquias",
            "Atirador de Precisão" to "Atirador",
            "Tecelão de Sonhos" to "Tecelão",
            "Caçador de Monstros" to "Caçador",
            "Agente das Sombras" to "Agente",
            "Médico de Campo" to "Médico",
            "Artista Marcial" to "Marcial",
        )

    private val categorias =
        mapOf(
            "Combate corpo a corpo" to "Combate",
            "Furtivos e distância" to "Furtivos",
            "Magia e mistérios" to "Magia",
            "Fé, cura e espírito" to "Fé e cura",
            "Natureza, viagem e mar" to "Natureza",
            "Intelecto, liderança e ofício" to "Intelecto",
            "Produção e ofícios" to "Produção",
            "Criação e arte" to "Arte",
            "Comida e campo" to "Comida",
            "Natureza e animais" to "Natureza",
            "Mar e estrada" to "Mar e estrada",
            "Saber e estudo" to "Saber",
            "Engenharia e construção" to "Engenharia",
            "Cura e saúde" to "Cura",
            "Comércio e serviços" to "Comércio",
            "Lei, investigação e proteção" to "Lei",
        )

    private val glifos =
        mapOf(
            "Linha de frente" to "⚔",
            "Defensores e juramentados" to "✚",
            "Lâmina e artes marciais" to "✦",
            "Furtivos" to "☾",
            "Arco e tiro" to "◎",
            "Arcanistas" to "✧",
            "Mente e ilusão" to "☯",
            "Cura" to "✚",
            "Fé e visão" to "☀",
            "Exploração" to "◈",
            "Feras e criaturas" to "❋",
            "Mar" to "⚓",
            "Liderança e diplomacia" to "⚖",
            "Estudo e investigação" to "✎",
            "Engenho e arte" to "⚙",
            "Metal, minas e armas" to "⚒",
            "Madeira, pedra e barro" to "⌂",
            "Tecido e couro" to "✂",
            "Joias e gemas" to "◆",
            "Artes e espetáculo" to "♪",
            "Preparados e fragrâncias" to "⚗",
            "Cozinha e bebida" to "❖",
            "Lavoura e criação" to "✿",
            "Guias e caça" to "◈",
            "Animais" to "❋",
            "Ciências da natureza" to "✧",
            "Mar e pesca" to "⚓",
            "Estrada e correio" to "➤",
            "Letras e ensino" to "✎",
            "Passado e relíquias" to "♞",
            "Construção" to "⌂",
            "Engenho e mecânica" to "⚙",
            "Cuidados" to "✚",
            "Comércio e contas" to "⚖",
            "Hospedagem e cuidados pessoais" to "☾",
            "Lei e diplomacia" to "⚖",
            "Investigação e segredos" to "◎",
            "Proteção e captura" to "✦",
        )

    fun curto(nome: String): String = curtos[nome] ?: nome

    fun rotuloCategoria(nome: String): String = categorias[nome] ?: nome

    /** Glifo do subgrupo; sem mapeamento, a inicial do nome (o app funciona sem arte, como pede o plano CG). */
    fun glifo(
        nome: String,
        subgrupo: String? = null,
    ): String = subgrupo?.let { glifos[it] } ?: monograma(nome)

    fun monograma(nome: String): String = nome.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "◆"
}

/** Árvores de habilidade para a prévia da criação; o motor continua sendo a autoridade sobre desbloqueios. */
object CriacaoHabilidades {
    val catalogo: SkillTreeCatalogResult by lazy {
        val bruto =
            CriacaoHabilidades::class.java.classLoader
                ?.getResourceAsStream("catalogs/sistema-proprio/habilidades-arvore.txt")
                ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
        SkillTreeCatalogLoader.load(bruto)
    }

    fun arvore(id: String): List<SkillTreeSkill> = if (id.isBlank()) emptyList() else catalogo.tree(id)

    /** "Arqueiro — Disparo de Abertura" vira "Disparo de Abertura". */
    fun nomeCurto(habilidade: SkillTreeSkill): String = habilidade.name.substringAfter(" — ")
}

/** Texto narrativo da origem escolhida na criação por abas; só contexto, sem efeito mecânico. */
fun CharacterChoices.origemNarrativa(): String {
    if (originCity.isBlank()) return ""
    val reino = CriacaoReinos.reinos.firstOrNull { it.id == originKingdomId }?.nome
    val lugar = if (reino != null) "$originCity, reino $reino" else originCity
    return when (originKind) {
        "native" -> "nativo de $lugar"
        "foreigner" -> "forasteiro vivendo em $lugar"
        "mixed" -> "mestiço, nascido em $lugar"
        else -> lugar
    }
}

/** Busca por nome nas listas de classes e profissões da criação por abas (sem acento nem maiúsculas). */
object CriacaoBusca {
    fun normalizar(texto: String): String =
        java.text.Normalizer.normalize(texto.trim().lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
}
