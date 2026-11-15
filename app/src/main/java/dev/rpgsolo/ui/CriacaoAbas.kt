package dev.rpgsolo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.rpgsolo.R
import dev.rpgsolo.engine.SkillCategory
import dev.rpgsolo.engine.SkillTreeSkill
import dev.rpgsolo.rules.CharacterDraft
import dev.rpgsolo.rules.ItemWeight
import dev.rpgsolo.rules.RulePackage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Criação de personagem por abas (CG-4): Raça (por reino) → Classe → Profissão → Status → Habilidades → Vantagens → Informações.
 * A tela só organiza escolhas: o rascunho ([CharacterDraft]) e o motor continuam validando pontos, requisitos e equilíbrio.
 */
private const val IDADE_MAXIMA = 1000
private const val BLOCOS_POR_PAGINA_TRACOS = 24
private const val RACAS_POR_PAGINA = 25

internal enum class PassoCriacao { RACA, CLASSE, PROFISSAO, STATUS, HABILIDADES, VANTAGENS, INFORMACOES }

/** Mensagem curta do passo (CG-4.9): o que fazer enquanto o passo não está pronto, ou a confirmação quando está. */
private fun PassoCriacao.mensagem(pronto: Boolean): Int? =
    when (this) {
        PassoCriacao.RACA -> if (pronto) R.string.wiz_msg_race_done else R.string.wiz_msg_race
        PassoCriacao.CLASSE -> if (pronto) R.string.wiz_msg_class_done else R.string.wiz_msg_class
        PassoCriacao.PROFISSAO -> if (pronto) R.string.wiz_msg_profession_done else R.string.wiz_msg_profession
        PassoCriacao.STATUS -> if (pronto) R.string.wiz_msg_stats_done else R.string.wiz_msg_stats
        PassoCriacao.HABILIDADES -> R.string.wiz_msg_skills
        PassoCriacao.VANTAGENS -> if (pronto) R.string.wiz_msg_traits_done else R.string.wiz_msg_traits
        PassoCriacao.INFORMACOES -> null
    }

private fun PassoCriacao.rotulo(): Int =
    when (this) {
        PassoCriacao.RACA -> R.string.wiz_tab_race
        PassoCriacao.CLASSE -> R.string.wiz_tab_class
        PassoCriacao.PROFISSAO -> R.string.wiz_tab_profession
        PassoCriacao.STATUS -> R.string.wiz_tab_status
        PassoCriacao.HABILIDADES -> R.string.wiz_tab_skills
        PassoCriacao.VANTAGENS -> R.string.wiz_tab_traits
        PassoCriacao.INFORMACOES -> R.string.wiz_tab_info
    }

/** Estado das escolhas da criação por abas; um por rascunho (a chave é o id do rascunho). */
@Stable
internal class EstadoCriacao {
    var passo by mutableStateOf(PassoCriacao.RACA)

    /** Muda a cada alteração no rascunho (que não é observável) para forçar a recomposição. */
    var tick by mutableIntStateOf(0)
    var modoReino by mutableIntStateOf(0)
    var forasteiro by mutableStateOf(true)
    var pagina by mutableIntStateOf(0)
    var raceId by mutableStateOf("")
    var subraceId by mutableStateOf("")
    var hybridId by mutableStateOf("")
    var originKind by mutableStateOf("")
    var originKingdomId by mutableStateOf("")
    var originCity by mutableStateOf("")
    var classId by mutableStateOf("")
    var professionId by mutableStateOf("")
    var catClasse by mutableIntStateOf(0)
    var catProfissao by mutableIntStateOf(0)
    var habilidadeSel by mutableStateOf("")

    /** Raça escolhida para comparação com a raça atual (CG-2.6); vazio = sem comparação. */
    var comparaId by mutableStateOf("")
    var tracoTipo by mutableStateOf("vantagem")
    var tracoSel by mutableStateOf("")
    var tracoBusca by mutableStateOf("")
    var paginaTraco by mutableIntStateOf(0)
    var sexo by mutableStateOf("")
    var idadeTxt by mutableStateOf("")
    var aparencia by mutableStateOf("")
    var personalidade by mutableStateOf("")
    var historia by mutableStateOf("")

    /** Evoluções das habilidades gratuitas feitas na criação (id da habilidade -> evoluções). */
    val evolucoes = mutableStateMapOf<String, Int>()

    /** Classes adicionais do herói híbrido (CG-3.5): até 3, além da principal; despertam nos níveis 10, 20 e 30. */
    val classesExtras = mutableStateListOf<String>()

    fun alternarExtra(id: String) {
        if (id == classId) return
        if (id in classesExtras) {
            classesExtras.remove(id)
        } else if (classesExtras.size < MAX_CLASSES_EXTRAS) {
            classesExtras.add(id)
        }
    }

    fun extrasValidas(): List<String> = classesExtras.filter { it != classId }

    /** Mestiço livre: até duas raças escolhidas (CG-4.12). */
    val mistas = mutableStateListOf<String>()

    fun alternarMista(id: String) {
        if (hybridId.isNotBlank() || raceId.isNotBlank()) limparRaca()
        originKind = "mixed"
        if (id in mistas) {
            mistas.remove(id)
        } else {
            if (mistas.size >= 2) mistas.removeAt(0)
            mistas.add(id)
        }
        originKingdomId = ""
        originCity = ""
    }

    fun evolucoesValidas(): Map<String, Int> {
        val livres =
            (listOf(raceId, subraceId, hybridId, classId, professionId) + mistas + extrasValidas())
                .filter { it.isNotBlank() }
                .flatMap { CriacaoHabilidades.arvore(it) }
                .filter { it.freeBaseSkill }
                .map { it.id }
                .toSet()
        return evolucoes.filter { it.key in livres && it.value > 0 }
    }

    fun evolucoesGastas(): Int = evolucoesValidas().values.sum()

    fun limparRaca() {
        raceId = ""
        subraceId = ""
        hybridId = ""
        mistas.clear()
        originKind = ""
        originKingdomId = ""
        originCity = ""
    }

    fun trocarModo(indice: Int) {
        if (modoReino != indice) {
            modoReino = indice
            limparRaca()
            pagina = 0
        }
    }

    fun trocarOutros(ehForasteiro: Boolean) {
        if (forasteiro != ehForasteiro) {
            forasteiro = ehForasteiro
            limparRaca()
            pagina = 0
        }
    }

    fun escolherNativo(
        racaId: String,
        reinoId: String,
    ) {
        if (raceId != racaId || originKind != "native" || originKingdomId != reinoId) {
            limparRaca()
            raceId = racaId
            originKind = "native"
            originKingdomId = reinoId
        }
    }

    fun escolherForasteiro(racaId: String) {
        if (raceId != racaId) {
            limparRaca()
            raceId = racaId
            originKind = "foreigner"
        }
    }

    fun escolherHibrido(id: String) {
        if (hybridId != id) {
            limparRaca()
            hybridId = id
            originKind = "mixed"
        }
    }

    /** Preenche raça, origem, classe, profissão, idade e sexo com um herói pronto (CG-5.2); o nome fica para o jogador. */
    fun aplicarHeroi(
        h: HeroiPronto,
        indiceReino: Int,
    ) {
        val c = h.escolhas
        limparRaca()
        modoReino = indiceReino.coerceAtLeast(0)
        raceId = c.raceId
        subraceId = c.subraceId
        hybridId = c.hybridId
        originKind = c.originKind
        originKingdomId = c.originKingdomId
        originCity = c.originCity
        classId = c.classId
        professionId = c.professionId
        classesExtras.clear()
        classesExtras.addAll(c.additionalClassIds)
        evolucoes.clear()
        sexo = c.sex
        idadeTxt = if (c.age > 0) c.age.toString() else ""
        pagina = 0
        tick++
    }

    /** Sem os arquivos dos reinos no pacote, a origem (reino e cidade) deixa de ser exigida. */
    fun racaCompleta(exigeOrigem: Boolean): Boolean =
        (raceId.isNotBlank() || hybridId.isNotBlank() || mistas.size == 2) &&
            (!exigeOrigem || (originCity.isNotBlank() && originKingdomId.isNotBlank()))

    fun idade(): Int = idadeTxt.toIntOrNull() ?: 0

    fun escolhas(): CharacterChoices =
        CharacterChoices(
            raceId = raceId,
            subraceId = subraceId,
            classId = classId,
            professionId = professionId,
            hybridId = hybridId,
            additionalClassIds = extrasValidas(),
            mixedRaceIds = if (mistas.size == 2) mistas.toList() else emptyList(),
            age = idade(),
            sex = sexo,
            originKind = originKind,
            originKingdomId = originKingdomId,
            originCity = originCity,
            initialSkillUpgrades = evolucoesValidas(),
        )
}

/** Catálogo e dados de mundo lidos uma vez e reaproveitados pelas abas. */
internal class DadosCriacao(
    val reinos: List<CriacaoReino>,
    val racas: List<CharacterCatalogEntry>,
    val subracas: List<CharacterCatalogEntry>,
    val hibridos: List<CharacterCatalogEntry>,
    val classes: List<CharacterCatalogEntry>,
    val profissoes: List<CharacterCatalogEntry>,
) {
    val catsClasses: List<CriacaoCategoria> = CriacaoTaxonomia.agrupar("classes", classes)
    val catsProfissoes: List<CriacaoCategoria> = CriacaoTaxonomia.agrupar("profissoes", profissoes)
    private val porId: Map<String, CharacterCatalogEntry> = (racas + subracas + hibridos + classes + profissoes).associateBy { it.id }

    fun entrada(id: String): CharacterCatalogEntry? = porId[id]

    companion object {
        fun carregar(): DadosCriacao =
            DadosCriacao(
                reinos = CriacaoReinos.reinos,
                racas = CharacterCreationCatalog.races,
                subracas = CharacterCreationCatalog.subraces,
                hibridos = CharacterCreationCatalog.hybrids,
                classes = CharacterCreationCatalog.classes,
                profissoes = CharacterCreationCatalog.professions,
            )
    }
}

/**
 * Tela de criação por abas. [aoCriar] recebe as escolhas validadas e o texto de história (aparência, personalidade e
 * história juntas, vazio se nada foi escrito); quem chama grava a ficha, as escolhas e a história.
 */
@Composable
internal fun CriacaoPorAbas(
    pkg: RulePackage,
    draft: CharacterDraft,
    ocupado: Boolean,
    mensagem: String?,
    aoProgresso: (Int) -> Unit = {},
    rascunhoSalvo: String? = null,
    aoRascunho: (String?) -> Unit = {},
    aoCriar: (CharacterChoices, String) -> Unit,
) {
    val est =
        remember(draft.id) {
            EstadoCriacao().also { novo ->
                if (rascunhoSalvo != null) CriacaoRascunho.restaurar(rascunhoSalvo, pkg.manifest.id, novo, draft)
            }
        }
    val dados = remember { DadosCriacao.carregar() }

    @Suppress("UNUSED_VARIABLE")
    val observaTick = est.tick
    val exigeOrigem = dados.reinos.isNotEmpty()
    val relatorio = remember(est.tick, draft.id) { draft.report() }
    val statSobra = draft.statPointsLeft(relatorio)
    val infoOk = draft.name.isNotBlank() && est.sexo.isNotBlank() && est.idade() in 1..IDADE_MAXIMA
    val feitos =
        CriacaoPassos.concluidos(
            ProgressoCriacao(
                racaCompleta = est.racaCompleta(exigeOrigem),
                classeEscolhida = est.classId.isNotBlank(),
                profissaoEscolhida = est.professionId.isNotBlank(),
                statusSemSobra = statSobra <= 0,
                vantagensValidasComTracos = relatorio.valid && draft.traits().isNotEmpty(),
                informacoesOk = infoOk,
            ),
        )

    SideEffect { aoProgresso(feitos.size) }
    val aoRascunhoAtual by rememberUpdatedState(aoRascunho)
    LaunchedEffect(est, draft) {
        snapshotFlow {
            est.tick
            if (CriacaoRascunho.temConteudo(est, draft)) CriacaoRascunho.serializar(pkg.manifest.id, est, draft) else null
        }.distinctUntilChanged().collectLatest { json ->
            kotlinx.coroutines.delay(800)
            aoRascunhoAtual(json)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoXg)) {
        SectionTitle(stringResource(R.string.wiz_title), stringResource(R.string.wiz_help))
        Pill(
            stringResource(R.string.wiz_progress, feitos.size, PassoCriacao.entries.size),
            if (feitos.size == PassoCriacao.entries.size) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        )
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
            PassoCriacao.entries.forEach { passo ->
                val marca = if (passo in feitos) "✓ " else ""
                FilterChip(
                    selected = est.passo == passo,
                    onClick = { est.passo = passo },
                    label = { Text(marca + stringResource(passo.rotulo())) },
                )
            }
        }
        est.passo.mensagem(est.passo in feitos)?.let { Muted(stringResource(it)) }
        if (est.passo != PassoCriacao.INFORMACOES && est.passo !in feitos) {
            val pendente = CriacaoPassos.primeiroPendente(feitos)
            if (pendente != est.passo) {
                TextButton(onClick = { est.passo = pendente }) {
                    Text(stringResource(R.string.wiz_go_pending, stringResource(pendente.rotulo())))
                }
            }
        }
        when (est.passo) {
            PassoCriacao.RACA -> PassoRaca(est, dados, pkg, draft, exigeOrigem) { est.passo = PassoCriacao.CLASSE }
            PassoCriacao.CLASSE -> {
                PassoGrupos(
                    tipo = "classes",
                    categorias = dados.catsClasses,
                    catIdx = est.catClasse,
                    aoCategoria = { est.catClasse = it },
                    selecionadoId = est.classId,
                    aoSelecionar = { est.classId = it },
                    colunas = 5,
                    abreviar = true,
                    textoContinuar = stringResource(R.string.wiz_continue_profession),
                    aoContinuar = { est.passo = PassoCriacao.PROFISSAO },
                    aoPular = null,
                ) { entrada -> ExtraClasse(entrada) }
                if (est.classId.isNotBlank()) ClassesAdicionais(est, dados)
            }
            PassoCriacao.PROFISSAO ->
                PassoGrupos(
                    tipo = "profissoes",
                    categorias = dados.catsProfissoes,
                    catIdx = est.catProfissao,
                    aoCategoria = { est.catProfissao = it },
                    selecionadoId = est.professionId,
                    aoSelecionar = { est.professionId = it },
                    colunas = 4,
                    abreviar = false,
                    textoContinuar = stringResource(R.string.wiz_continue_status),
                    aoContinuar = { est.passo = PassoCriacao.STATUS },
                    aoPular = {
                        est.professionId = ""
                        est.passo = PassoCriacao.STATUS
                    },
                ) { entrada -> ExtraProfissao(entrada) }
            PassoCriacao.STATUS -> PassoStatus(est, pkg, draft) { est.passo = PassoCriacao.HABILIDADES }
            PassoCriacao.HABILIDADES ->
                PassoHabilidades(
                    est,
                    dados,
                    pkg.engineConfig().initialAbilityPoints,
                ) { est.passo = PassoCriacao.VANTAGENS }
            PassoCriacao.VANTAGENS -> PassoVantagens(est, pkg, draft) { est.passo = PassoCriacao.INFORMACOES }
            PassoCriacao.INFORMACOES -> PassoInformacoes(est, dados, pkg, draft, relatorio.valid, statSobra, exigeOrigem, ocupado, mensagem, aoCriar)
        }
    }
}

// ---------- Peças visuais ----------

/** Bloco da grade: medalhão com o glifo, nome e estado. Sem arte, o glifo vem do subgrupo ou é a inicial do nome (kit visual CG-0.5). */
@Composable
private fun Bloco(
    glifo: String,
    nome: String,
    selecionado: Boolean,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    selo: String? = null,
    apagado: Boolean = false,
) {
    val cores = MaterialTheme.colorScheme
    val formato = MaterialTheme.shapes.small
    val gratuita = stringResource(R.string.wiz_free_short)
    val descricao = if (selo == null) nome else nome + ", " + (if (selo == "✓") gratuita else selo)
    val conteudo =
        when {
            selecionado -> cores.primary
            apagado -> cores.onSurfaceVariant.copy(alpha = 0.5f)
            else -> cores.onSurfaceVariant
        }
    Box(
        modifier
            .heightIn(min = CriacaoTokens.BlocoAlturaMin)
            .clip(formato)
            .background(if (selecionado) cores.surfaceContainerHighest else cores.surfaceContainerLow)
            .border(CriacaoTokens.Borda, if (selecionado) cores.primary else cores.outlineVariant, formato)
            .semantics(mergeDescendants = true) {
                selected = selecionado
                contentDescription = descricao
            }
            .clickable(role = Role.Button, onClick = aoClicar)
            .padding(horizontal = CriacaoTokens.EspacoXs, vertical = CriacaoTokens.EspacoM),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoXs)) {
            Box(
                Modifier
                    .size(CriacaoTokens.MedalhaoTamanho)
                    .clip(CircleShape)
                    .background(if (selecionado) cores.primaryContainer else cores.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Text(glifo, fontSize = CriacaoTokens.GlifoTamanho, color = conteudo)
            }
            Text(
                nome,
                style = MaterialTheme.typography.labelSmall,
                color = conteudo,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (selo != null) {
            Text(selo, Modifier.align(Alignment.TopEnd), style = MaterialTheme.typography.labelSmall, color = conteudo)
        }
    }
}

/** Grade simples com [colunas] colunas (sem lista preguiçosa: a tela já rola). */
@Composable
private fun <T> GradeBlocos(
    itens: List<T>,
    colunas: Int,
    conteudo: @Composable (T, Modifier) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoS)) {
        itens.chunked(colunas).forEach { linha ->
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoS)) {
                linha.forEach { item -> conteudo(item, Modifier.weight(1f).fillMaxHeight()) }
                repeat(colunas - linha.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/** Opções (id, rótulo) em chips com rolagem lateral; o id vazio é a opção "nenhuma". */
@Composable
private fun LinhaOpcoes(
    opcoes: List<Pair<String, String>>,
    atual: String,
    aoEscolher: (String) -> Unit,
) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
        opcoes.forEach { (id, rotulo) ->
            FilterChip(selected = atual == id, onClick = { aoEscolher(id) }, label = { Text(rotulo) })
        }
    }
}

@Composable
private fun Paginador(
    pagina: Int,
    total: Int,
    aoMudar: (Int) -> Unit,
) {
    val anterior = stringResource(R.string.wiz_page_prev)
    val proxima = stringResource(R.string.wiz_page_next)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        TextButton(
            onClick = { aoMudar((pagina - 1 + total) % total) },
            modifier = Modifier.semantics { contentDescription = anterior },
        ) { Text("‹") }
        Text(stringResource(R.string.wiz_page, pagina + 1, total), style = MaterialTheme.typography.bodySmall)
        TextButton(
            onClick = { aoMudar((pagina + 1) % total) },
            modifier = Modifier.semantics { contentDescription = proxima },
        ) { Text("›") }
    }
}

@Composable
private fun PainelEntrada(
    entrada: CharacterCatalogEntry,
    legenda: String,
    extra: @Composable () -> Unit = {},
) {
    AppCard {
        Text(entrada.name, style = MaterialTheme.typography.titleMedium)
        Muted(legenda)
        if (entrada.summary.isNotBlank()) Text(entrada.summary, style = MaterialTheme.typography.bodyMedium)
        entrada.benefits.take(2).forEach { Muted(stringResource(R.string.char_benefit_summary, it)) }
        entrada.drawbacks.take(2).forEach { Muted(stringResource(R.string.char_drawback_summary, it)) }
        extra()
        val temMais = entrada.lore.isNotBlank() || entrada.benefits.size > 2 || entrada.drawbacks.size > 2 || entrada.mechanicalEffects.isNotEmpty()
        var aberto by remember(entrada.id) { mutableStateOf(false) }
        if (temMais) {
            TextButton(onClick = { aberto = !aberto }) {
                Text(stringResource(if (aberto) R.string.wiz_detail_less else R.string.wiz_detail_more))
            }
        }
        if (aberto) {
            if (entrada.lore.isNotBlank()) Text(entrada.lore, style = MaterialTheme.typography.bodyMedium)
            entrada.benefits.drop(2).forEach { Muted(stringResource(R.string.char_benefit_summary, it)) }
            entrada.drawbacks.drop(2).forEach { Muted(stringResource(R.string.char_drawback_summary, it)) }
            if (entrada.mechanicalEffects.isNotEmpty()) {
                Muted(stringResource(R.string.wiz_detail_effects, resumoEfeitos(entrada.mechanicalEffects)))
            }
        }
    }
}

/** \"ataque +1, defesa +2\": soma por escopo, em ordem alfabética. */
private fun resumoEfeitos(efeitos: List<CharacterCatalogEffect>): String =
    efeitos.groupBy { it.scope }
        .mapValues { (_, lista) -> lista.sumOf { it.bonus } }
        .filterValues { it != 0 }
        .entries.sortedBy { it.key }
        .joinToString(", ") { (escopo, total) -> escopo.replace('_', ' ') + " " + (if (total > 0) "+" else "") + total }

@Composable
private fun BotaoContinuar(
    texto: String,
    habilitado: Boolean,
    dica: String?,
    aoContinuar: () -> Unit,
) {
    Button(modifier = Modifier.fillMaxWidth(), enabled = habilitado, onClick = aoContinuar) { Text(texto) }
    if (!habilitado && dica != null) Muted(dica)
}

// ---------- 1. Raça, por reino ----------

@Composable
private fun PassoRaca(
    est: EstadoCriacao,
    dados: DadosCriacao,
    pkg: RulePackage,
    draft: CharacterDraft,
    exigeOrigem: Boolean,
    aoContinuar: () -> Unit,
) {
    val reinos = dados.reinos
    val outros = reinos.size
    val modo = est.modoReino.coerceIn(0, outros)
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
        reinos.forEachIndexed { i, reino ->
            FilterChip(selected = modo == i, onClick = { est.trocarModo(i) }, label = { Text(reino.nome) })
        }
        FilterChip(selected = modo == outros, onClick = { est.trocarModo(outros) }, label = { Text(stringResource(R.string.wiz_others)) })
    }
    HeroisProntosCartao(est, dados, reinos, pkg, draft)
    if (modo < outros) PassoReino(est, dados, reinos[modo]) else PassoOutros(est, dados)
    ComparadorRacas(est, dados)
    BotaoContinuar(
        texto = stringResource(R.string.wiz_continue_class),
        habilitado = est.racaCompleta(exigeOrigem),
        dica = stringResource(R.string.wiz_hint_race),
        aoContinuar = aoContinuar,
    )
}

@Composable
private fun HeroisProntosCartao(
    est: EstadoCriacao,
    dados: DadosCriacao,
    reinos: List<CriacaoReino>,
    pkg: RulePackage,
    draft: CharacterDraft,
) {
    val herois = remember { HeroisProntos.mythralis.filter { h -> dados.racas.any { it.id == h.escolhas.raceId } } }
    if (herois.isEmpty()) return
    var aberto by remember { mutableStateOf(false) }
    AppCard {
        Text(stringResource(R.string.wiz_heroes_title), style = MaterialTheme.typography.titleSmall)
        Muted(stringResource(R.string.wiz_heroes_help))
        TextButton(onClick = { aberto = !aberto }) {
            Text(stringResource(if (aberto) R.string.wiz_heroes_hide else R.string.wiz_heroes_show))
        }
        if (aberto) {
            herois.forEach { h ->
                val reino = reinos.firstOrNull { it.id == h.reinoId }
                val raca = dados.racas.firstOrNull { it.id == h.escolhas.raceId }?.name.orEmpty()
                val classe = dados.classes.firstOrNull { it.id == h.escolhas.classId }?.name.orEmpty()
                val profissao = dados.profissoes.firstOrNull { it.id == h.escolhas.professionId }?.name.orEmpty()
                Text(h.titulo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Muted(h.conceito)
                Muted(stringResource(R.string.wiz_heroes_line, reino?.nome.orEmpty(), raca, classe, profissao))
                OutlinedButton(onClick = {
                    est.aplicarHeroi(h, reinos.indexOfFirst { it.id == h.reinoId })
                    aberto = false
                }) { Text(stringResource(R.string.wiz_heroes_use)) }
                Button(onClick = {
                    CriacaoJogoRapido.preparar(h, reinos.indexOfFirst { it.id == h.reinoId }, est, draft, pkg)
                    aberto = false
                }) { Text(stringResource(R.string.wiz_heroes_quick)) }
            }
        }
    }
}

@Composable
private fun ComparadorRacas(
    est: EstadoCriacao,
    dados: DadosCriacao,
) {
    val atual = dados.racas.firstOrNull { it.id == est.raceId } ?: return
    AppCard {
        Text(stringResource(R.string.wiz_compare_title), style = MaterialTheme.typography.titleSmall)
        LinhaOpcoes(
            listOf("" to stringResource(R.string.wiz_compare_none)) + dados.racas.filter { it.id != atual.id }.map { it.id to it.name },
            est.comparaId,
        ) { est.comparaId = it }
        val outra = dados.racas.firstOrNull { it.id == est.comparaId } ?: return@AppCard
        listOf(atual, outra).forEach { raca ->
            Text(raca.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            val bonus = resumoEfeitos(raca.mechanicalEffects)
            Muted(stringResource(R.string.wiz_detail_effects, bonus.ifBlank { stringResource(R.string.wiz_compare_no_bonus) }))
            raca.benefits.take(3).forEach { Muted(stringResource(R.string.char_benefit_summary, it)) }
            raca.drawbacks.take(3).forEach { Muted(stringResource(R.string.char_drawback_summary, it)) }
        }
    }
}

@Composable
private fun PassoReino(
    est: EstadoCriacao,
    dados: DadosCriacao,
    reino: CriacaoReino,
) {
    Muted(stringResource(R.string.wiz_kingdom_line, reino.nome, reino.tema))
    val cartoes = reino.povos.mapNotNull { povo -> dados.entrada(povo.racaId)?.let { entrada -> povo to entrada } }
    GradeBlocos(cartoes, 4) { (_, entrada), modificador ->
        Bloco(
            glifo = CriacaoNomes.monograma(entrada.name),
            nome = entrada.name,
            selecionado = est.raceId == entrada.id && est.originKind == "native" && est.originKingdomId == reino.id,
            aoClicar = { est.escolherNativo(entrada.id, reino.id) },
            modifier = modificador,
        )
    }
    val escolhido = cartoes.firstOrNull { it.second.id == est.raceId && est.originKind == "native" && est.originKingdomId == reino.id }
    if (escolhido != null) {
        val povo = escolhido.first
        val entrada = escolhido.second
        PainelEntrada(entrada, stringResource(R.string.wiz_native_of, reino.nome))
        val subs = CriacaoReinos.subracasDoPovo(povo, dados.subracas)
        if (subs.isNotEmpty()) {
            Muted(stringResource(R.string.wiz_subrace_optional))
            LinhaOpcoes(listOf("" to stringResource(R.string.wiz_race_only)) + subs.map { it.id to it.name }, est.subraceId) {
                est.subraceId = it
            }
        }
        Muted(stringResource(R.string.wiz_birth_city))
        LinhaOpcoes(povo.cidades.map { it.nome to it.nome }, est.originCity) { est.originCity = it }
    }
}

@Composable
private fun PassoOutros(
    est: EstadoCriacao,
    dados: DadosCriacao,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
        FilterChip(
            selected = est.forasteiro,
            onClick = { est.trocarOutros(true) },
            label = { Text(stringResource(R.string.wiz_foreigner)) },
        )
        FilterChip(
            selected = !est.forasteiro,
            onClick = { est.trocarOutros(false) },
            label = { Text(stringResource(R.string.wiz_mixed)) },
        )
    }
    if (est.forasteiro) PassoForasteiro(est, dados) else PassoMestico(est, dados)
}

@Composable
private fun PassoForasteiro(
    est: EstadoCriacao,
    dados: DadosCriacao,
) {
    val paginas = ((dados.racas.size + RACAS_POR_PAGINA - 1) / RACAS_POR_PAGINA).coerceAtLeast(1)
    val pagina = est.pagina.coerceIn(0, paginas - 1)
    Muted(stringResource(R.string.wiz_foreigner_help))
    GradeBlocos(dados.racas.drop(pagina * RACAS_POR_PAGINA).take(RACAS_POR_PAGINA), 5) { entrada, modificador ->
        Bloco(
            glifo = CriacaoNomes.monograma(entrada.name),
            nome = CriacaoNomes.curto(entrada.name),
            selecionado = est.raceId == entrada.id,
            aoClicar = { est.escolherForasteiro(entrada.id) },
            modifier = modificador,
        )
    }
    if (paginas > 1) Paginador(pagina, paginas) { est.pagina = it }
    val entrada = dados.racas.firstOrNull { it.id == est.raceId } ?: return
    val nativos = CriacaoReinos.nativosDe(entrada.id)
    PainelEntrada(entrada, stringResource(R.string.wiz_foreigner_of, nativos.joinToString(" / ") { it.first.nome }))
    val subs = dados.subracas.filter { it.parentRaceId == entrada.id }
    if (subs.isNotEmpty()) {
        Muted(stringResource(R.string.wiz_subrace_optional))
        LinhaOpcoes(listOf("" to stringResource(R.string.wiz_race_only)) + subs.map { it.id to it.name }, est.subraceId) {
            est.subraceId = it
        }
    }
    val disponiveis = dados.reinos.filter { reino -> nativos.none { it.first.id == reino.id } }
    if (disponiveis.isNotEmpty()) {
        Muted(stringResource(R.string.wiz_lives_in))
        LinhaOpcoes(disponiveis.map { it.id to it.nome }, est.originKingdomId) { id ->
            if (id != est.originKingdomId) {
                est.originKingdomId = id
                est.originCity = ""
            }
        }
    }
    val reino = dados.reinos.firstOrNull { it.id == est.originKingdomId }
    if (reino != null) {
        Muted(stringResource(R.string.wiz_city))
        LinhaOpcoes(reino.cidades.map { it.nome to it.nome }, est.originCity) { est.originCity = it }
    }
}

@Composable
private fun PassoMestico(
    est: EstadoCriacao,
    dados: DadosCriacao,
) {
    Muted(stringResource(R.string.wiz_mixed_help))
    GradeBlocos(dados.hibridos, 4) { entrada, modificador ->
        Bloco(
            glifo = CriacaoNomes.monograma(entrada.name),
            nome = entrada.name,
            selecionado = est.hybridId == entrada.id,
            aoClicar = { est.escolherHibrido(entrada.id) },
            modifier = modificador,
        )
    }
    Text(stringResource(R.string.wiz_mixed_free), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
    Muted(stringResource(R.string.wiz_mixed_free_help, CharacterCreationCatalog.MIXED_SCOPE_CAP))
    GradeBlocos(dados.racas, 4) { entrada, modificador ->
        Bloco(
            glifo = CriacaoNomes.monograma(entrada.name),
            nome = entrada.name,
            selecionado = entrada.id in est.mistas,
            aoClicar = { est.alternarMista(entrada.id) },
            modifier = modificador,
        )
    }
    if (est.mistas.size == 2) {
        val paisLivres = est.mistas.mapNotNull { dados.entrada(it) }
        Muted(stringResource(R.string.char_ancestry, paisLivres.joinToString(" + ") { it.name }))
        val combinados = resumoEfeitos(CharacterCreationCatalog.mixedRaceEffects(est.mistas.toList()))
        if (combinados.isNotBlank()) {
            Muted(stringResource(R.string.wiz_mixed_preview, combinados, CharacterCreationCatalog.MIXED_SCOPE_CAP))
        }
        val opcoesLivres =
            est.mistas
                .flatMap { id -> CriacaoReinos.nativosDe(id).flatMap { (reino, povo) -> povo.cidades.map { cidade -> reino to cidade } } }
                .distinctBy { it.first.id + "|" + it.second.id }
        if (opcoesLivres.isNotEmpty()) {
            Muted(stringResource(R.string.wiz_birth_city_parent))
            LinhaOpcoes(
                opcoesLivres.map { (reino, cidade) -> (reino.id + "|" + cidade.nome) to (cidade.nome + " (" + reino.nome + ")") },
                est.originKingdomId + "|" + est.originCity,
            ) { valor ->
                est.originKingdomId = valor.substringBefore("|")
                est.originCity = valor.substringAfter("|")
            }
        }
    }
    val hibrido = dados.hibridos.firstOrNull { it.id == est.hybridId } ?: return
    val pais = hibrido.hybridRaceIds.mapNotNull { dados.entrada(it) }
    PainelEntrada(hibrido, stringResource(R.string.char_ancestry, pais.joinToString(" + ") { it.name }))
    val opcoes =
        hibrido.hybridRaceIds
            .flatMap { id -> CriacaoReinos.nativosDe(id).flatMap { (reino, povo) -> povo.cidades.map { cidade -> reino to cidade } } }
            .distinctBy { it.first.id + "|" + it.second.id }
    if (opcoes.isNotEmpty()) {
        Muted(stringResource(R.string.wiz_birth_city_parent))
        LinhaOpcoes(
            opcoes.map { (reino, cidade) -> (reino.id + "|" + cidade.nome) to (cidade.nome + " (" + reino.nome + ")") },
            est.originKingdomId + "|" + est.originCity,
        ) { valor ->
            est.originKingdomId = valor.substringBefore("|")
            est.originCity = valor.substringAfter("|")
        }
    }
    Muted(stringResource(R.string.wiz_mixed_note))
}

// ---------- 2 e 3. Classe e profissão ----------

@Composable
private fun PassoGrupos(
    tipo: String,
    categorias: List<CriacaoCategoria>,
    catIdx: Int,
    aoCategoria: (Int) -> Unit,
    selecionadoId: String,
    aoSelecionar: (String) -> Unit,
    colunas: Int,
    abreviar: Boolean,
    textoContinuar: String,
    aoContinuar: () -> Unit,
    aoPular: (() -> Unit)?,
    extra: @Composable (CharacterCatalogEntry) -> Unit,
) {
    if (categorias.isEmpty()) {
        Muted(stringResource(R.string.wiz_empty_catalog))
        return
    }
    var busca by remember(tipo) { mutableStateOf("") }
    OutlinedTextField(
        value = busca,
        onValueChange = { busca = it },
        label = { Text(stringResource(R.string.wiz_search)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    val termo = CriacaoBusca.normalizar(busca)
    if (termo.isNotEmpty()) {
        val achados =
            categorias.flatMap { it.subgrupos }.flatMap { (subgrupo, itens) -> itens.map { subgrupo to it } }
                .filter { CriacaoBusca.normalizar(it.second.name).contains(termo) }
        if (achados.isEmpty()) {
            Muted(stringResource(R.string.wiz_search_empty))
        } else {
            GradeBlocos(achados, colunas) { (subgrupo, entrada), modificador ->
                Bloco(
                    glifo = CriacaoNomes.glifo(entrada.name, subgrupo),
                    nome = if (abreviar) CriacaoNomes.curto(entrada.name) else entrada.name,
                    selecionado = entrada.id == selecionadoId,
                    aoClicar = { aoSelecionar(entrada.id) },
                    modifier = modificador,
                )
            }
        }
        val escolhidaBusca = achados.map { it.second }.firstOrNull { it.id == selecionadoId }
        if (escolhidaBusca != null) {
            val grupoBusca = CriacaoTaxonomia.grupo(tipo, escolhidaBusca.id)
            PainelEntrada(escolhidaBusca, grupoBusca?.let { it.categoria + " · " + it.subgrupo }.orEmpty()) { extra(escolhidaBusca) }
        }
        BotaoContinuar(textoContinuar, escolhidaBusca != null, stringResource(R.string.wiz_hint_pick), aoContinuar)
        return
    }
    val atual = categorias[catIdx.coerceIn(0, categorias.lastIndex)]
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
        categorias.forEachIndexed { i, categoria ->
            FilterChip(selected = categoria === atual, onClick = { aoCategoria(i) }, label = { Text(categoria.rotulo) })
        }
    }
    Muted(atual.nome)
    atual.subgrupos.forEach { (subgrupo, itens) ->
        Text(subgrupo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        GradeBlocos(itens, colunas) { entrada, modificador ->
            Bloco(
                glifo = CriacaoNomes.glifo(entrada.name, subgrupo),
                nome = if (abreviar) CriacaoNomes.curto(entrada.name) else entrada.name,
                selecionado = entrada.id == selecionadoId,
                aoClicar = { aoSelecionar(entrada.id) },
                modifier = modificador,
            )
        }
    }
    val escolhida = categorias.flatMap { it.subgrupos }.flatMap { it.second }.firstOrNull { it.id == selecionadoId }
    if (escolhida != null) {
        val grupo = CriacaoTaxonomia.grupo(tipo, escolhida.id)
        PainelEntrada(escolhida, grupo?.let { it.categoria + " · " + it.subgrupo }.orEmpty()) { extra(escolhida) }
    }
    BotaoContinuar(textoContinuar, escolhida != null, stringResource(R.string.wiz_hint_pick), aoContinuar)
    if (aoPular != null) {
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = aoPular) { Text(stringResource(R.string.wiz_skip_profession)) }
    }
}

@Composable
private fun ExtraClasse(entrada: CharacterCatalogEntry) {
    val peso = CharacterCreationCatalog.classArmorWeight(entrada.id)
    val armas = CharacterCreationCatalog.classWeaponTypes(entrada.id)
    if (peso != null) {
        val nomePeso =
            when (peso) {
                ItemWeight.PESADO -> stringResource(R.string.wiz_armor_heavy)
                ItemWeight.MEDIO -> stringResource(R.string.wiz_armor_medium)
                ItemWeight.LEVE -> stringResource(R.string.wiz_armor_light)
            }
        Muted(stringResource(R.string.wiz_armor, nomePeso))
    }
    if (armas.isNotEmpty()) {
        val nomesArmas = armas.map { it.replace("lanca", "lança") }
        Muted(stringResource(R.string.wiz_weapon_main, nomesArmas.first()))
        if (nomesArmas.size > 1) Muted(stringResource(R.string.wiz_weapon_others, nomesArmas.drop(1).joinToString(", ")))
    }
}

@Composable
private fun ClassesAdicionais(
    est: EstadoCriacao,
    dados: DadosCriacao,
) {
    var aberto by remember(est.classId) { mutableStateOf(est.classesExtras.isNotEmpty()) }
    AppCard {
        Text(stringResource(R.string.wiz_extra_title), style = MaterialTheme.typography.titleSmall)
        Muted(stringResource(R.string.wiz_extra_help, MAX_CLASSES_EXTRAS))
        TextButton(onClick = { aberto = !aberto }) {
            Text(stringResource(if (aberto) R.string.wiz_extra_hide else R.string.wiz_extra_show, est.extrasValidas().size))
        }
        if (aberto) {
            val todas = dados.catsClasses.flatMap { it.subgrupos }.flatMap { it.second }.filter { it.id != est.classId }.distinctBy { it.id }
            GradeBlocos(todas, 5) { entrada, modificador ->
                Bloco(
                    glifo = CriacaoNomes.monograma(entrada.name),
                    nome = CriacaoNomes.curto(entrada.name),
                    selecionado = entrada.id in est.classesExtras,
                    aoClicar = { est.alternarExtra(entrada.id) },
                    modifier = modificador,
                )
            }
            if (est.extrasValidas().size >= MAX_CLASSES_EXTRAS) Muted(stringResource(R.string.wiz_extra_limit))
        }
        if (est.extrasValidas().isNotEmpty()) Muted(stringResource(R.string.wiz_extra_note))
    }
}

private const val MAX_CLASSES_EXTRAS = 3

@Composable
private fun ExtraProfissao(entrada: CharacterCatalogEntry) {
    val livre = CriacaoHabilidades.arvore(entrada.id).firstOrNull { it.freeBaseSkill }
    if (livre != null) Muted(stringResource(R.string.wiz_free_skill, CriacaoHabilidades.nomeCurto(livre)))
}

// ---------- 4. Status ----------

private fun descricaoAtributo(id: String): Int? =
    when (id) {
        "poder" -> R.string.wiz_stat_poder
        "habilidade" -> R.string.wiz_stat_habilidade
        "resistencia" -> R.string.wiz_stat_resistencia
        "inteligencia" -> R.string.wiz_stat_inteligencia
        "agilidade" -> R.string.wiz_stat_agilidade
        "destreza" -> R.string.wiz_stat_destreza
        "sorte" -> R.string.wiz_stat_sorte
        else -> null
    }

@Composable
private fun PassoStatus(
    est: EstadoCriacao,
    pkg: RulePackage,
    draft: CharacterDraft,
    aoContinuar: () -> Unit,
) {
    // O rascunho não é estado do Compose: ler o contador aqui inscreve o passo na mudança e o redesenha a cada +/−
    // (sem isso, com a recomposição inteligente, o passo era pulado e os pontos não apareciam).
    @Suppress("UNUSED_VARIABLE")
    val observaTick = est.tick
    val relatorio = draft.report()
    val sobra = draft.statPointsLeft(relatorio)
    val stats = draft.stats()
    AppCard(accent = if (sobra < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.char_stat_points), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.char_points_left, sobra, relatorio.statPointsAvailable),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        LinearProgressIndicator(
            progress = { (relatorio.statPointsSpent.toFloat() / relatorio.statPointsAvailable.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        if (sobra <= 0) Muted(stringResource(R.string.char_stat_points_locked))
    }
    AppCard {
        pkg.data.stats.forEach { atributo ->
            val valor = stats[atributo.id] ?: atributo.default
            val menos = stringResource(R.string.char_decrease, atributo.name)
            val mais = stringResource(R.string.char_increase, atributo.name)
            val descricao = descricaoAtributo(atributo.id)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoG)) {
                Column(Modifier.weight(1f)) {
                    Text(atributo.name, style = MaterialTheme.typography.titleSmall)
                    if (descricao != null) Muted(stringResource(descricao))
                }
                OutlinedButton(
                    enabled = valor > atributo.min,
                    onClick = {
                        draft.setStat(atributo.id, valor - 1)
                        est.tick++
                    },
                    modifier = Modifier.semantics { contentDescription = menos },
                ) { Text("−") }
                Text(valor.toString(), Modifier.width(CriacaoTokens.ValorLargura), style = MaterialTheme.typography.titleMedium)
                OutlinedButton(
                    enabled = valor < atributo.max && sobra > 0,
                    onClick = {
                        draft.setStat(atributo.id, valor + 1)
                        est.tick++
                    },
                    modifier = Modifier.semantics { contentDescription = mais },
                ) { Text("+") }
            }
        }
    }
    val recursos = pkg.resourcesFor(stats, draft.traits())
    if (pkg.data.resources.isNotEmpty()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
            pkg.data.resources.forEach { recurso ->
                AppCard(Modifier.weight(1f)) {
                    Muted(recurso.name)
                    Text((recursos[recurso.id]?.max ?: 0).toString(), style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        Muted(stringResource(R.string.wiz_stat_note))
    }
    BotaoContinuar(stringResource(R.string.wiz_continue_skills), sobra <= 0, stringResource(R.string.wiz_hint_stats, sobra), aoContinuar)
}

// ---------- 5. Habilidades (prévia das árvores) ----------

private fun glifoCategoria(categoria: SkillCategory): String =
    when (categoria) {
        SkillCategory.OFFENSE -> "⚔"
        SkillCategory.DEFENSE -> "▣"
        SkillCategory.BUFF -> "▲"
        SkillCategory.SUPPORT -> "✚"
        SkillCategory.PASSIVE -> "∞"
        SkillCategory.UTILITY -> "⚙"
        SkillCategory.CONTROL -> "◉"
    }

private fun rotuloCategoria(categoria: SkillCategory): Int =
    when (categoria) {
        SkillCategory.OFFENSE -> R.string.wiz_cat_offense
        SkillCategory.DEFENSE -> R.string.wiz_cat_defense
        SkillCategory.BUFF -> R.string.wiz_cat_buff
        SkillCategory.SUPPORT -> R.string.wiz_cat_support
        SkillCategory.PASSIVE -> R.string.wiz_cat_passive
        SkillCategory.UTILITY -> R.string.wiz_cat_utility
        SkillCategory.CONTROL -> R.string.wiz_cat_control
    }

@Composable
private fun PassoHabilidades(
    est: EstadoCriacao,
    dados: DadosCriacao,
    pontosIniciais: Int,
    aoContinuar: () -> Unit,
) {
    Muted(stringResource(R.string.wiz_skills_note))
    val gastos = est.evolucoesGastas()
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.wiz_skill_points), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.char_points_left, pontosIniciais - gastos, pontosIniciais),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    val secoes =
        (
            listOf(
                R.string.wiz_tree_race to est.raceId,
                R.string.wiz_tree_subrace to est.subraceId,
                R.string.wiz_tree_hybrid to est.hybridId,
                R.string.wiz_tree_race to est.mistas.getOrNull(0).orEmpty(),
                R.string.wiz_tree_race to est.mistas.getOrNull(1).orEmpty(),
                R.string.wiz_tree_class to est.classId,
                R.string.wiz_tree_profession to est.professionId,
            ) + est.extrasValidas().map { R.string.wiz_tree_class to it }
        ).filter { it.second.isNotBlank() }
    val arvores = secoes.map { (rotulo, id) -> Triple(rotulo, id, CriacaoHabilidades.arvore(id)) }.filter { it.third.isNotEmpty() }
    if (arvores.isEmpty()) {
        Muted(stringResource(R.string.wiz_skills_empty))
    }
    arvores.forEach { (rotulo, id, arvore) ->
        Text(
            stringResource(rotulo) + ": " + (dados.entrada(id)?.name ?: id),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        GradeBlocos(arvore, 4) { habilidade, modificador ->
            Bloco(
                glifo = glifoCategoria(habilidade.category),
                nome = CriacaoHabilidades.nomeCurto(habilidade),
                selecionado = est.habilidadeSel == habilidade.id,
                aoClicar = { est.habilidadeSel = habilidade.id },
                modifier = modificador,
                selo = if (habilidade.freeBaseSkill) "✓" else stringResource(R.string.wiz_level_short, habilidade.requiredCharacterLevel),
                apagado = !habilidade.freeBaseSkill,
            )
        }
    }
    val escolhida: SkillTreeSkill? = arvores.flatMap { it.third }.firstOrNull { it.id == est.habilidadeSel }
    if (escolhida != null) {
        AppCard {
            Text(escolhida.name, style = MaterialTheme.typography.titleMedium)
            Muted(stringResource(rotuloCategoria(escolhida.category)))
            escolhida.effects.forEach { efeito ->
                if (efeito.perLevel != 0) {
                    Muted(stringResource(R.string.wiz_effect_level, efeito.target, efeito.baseValue, efeito.perLevel))
                } else {
                    Muted(stringResource(R.string.wiz_effect, efeito.target, efeito.baseValue))
                }
            }
            if (escolhida.category == SkillCategory.PASSIVE) {
                Muted(stringResource(R.string.wiz_use_passive))
            } else if (escolhida.actionCost > 0) {
                Muted(stringResource(R.string.wiz_use_cost, escolhida.actionCost, escolhida.actionResource.orEmpty()))
            }
            Muted(stringResource(R.string.wiz_required_level, escolhida.requiredCharacterLevel))
            if (escolhida.freeBaseSkill) {
                val nivelEvol = est.evolucoes[escolhida.id] ?: 0
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoG)) {
                    Text(
                        stringResource(R.string.wiz_skill_level, 1 + nivelEvol),
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    OutlinedButton(onClick = { est.evolucoes[escolhida.id] = (nivelEvol - 1).coerceAtLeast(0) }, enabled = nivelEvol > 0) {
                        Text(stringResource(R.string.wiz_skill_downgrade))
                    }
                    Button(onClick = { est.evolucoes[escolhida.id] = nivelEvol + 1 }, enabled = gastos < pontosIniciais) {
                        Text(stringResource(R.string.wiz_skill_upgrade))
                    }
                }
            }
            Muted(
                if (escolhida.freeBaseSkill) {
                    stringResource(R.string.wiz_unlock_free)
                } else {
                    stringResource(R.string.wiz_unlock_points, escolhida.unlockPointCost)
                },
            )
        }
    }
    BotaoContinuar(stringResource(R.string.wiz_continue_traits), true, null, aoContinuar)
}

// ---------- 6. Vantagens e desvantagens ----------

@Composable
private fun PassoVantagens(
    est: EstadoCriacao,
    pkg: RulePackage,
    draft: CharacterDraft,
    aoContinuar: () -> Unit,
) {
    val relatorio = draft.report()
    val escolhidos = draft.traits()
    val corBalanco = if (relatorio.valid) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    AppCard(accent = corBalanco) {
        if (pkg.balanceTable.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.char_balance), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                Pill(
                    stringResource(
                        R.string.char_balance_pill,
                        relatorio.advantages,
                        relatorio.disadvantages,
                        relatorio.disadvantagesRequired,
                    ),
                    corBalanco,
                )
            }
            Muted(stringResource(R.string.char_balance_help))
        }
        relatorio.errors.forEach { ErrorText(it) }
    }
    if (escolhidos.isNotEmpty()) {
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
            escolhidos.forEach { id ->
                FilterChip(
                    selected = true,
                    onClick = {
                        draft.removeTrait(id)
                        est.tick++
                    },
                    label = { Text(pkg.trait(id)?.name ?: id) },
                )
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
        FilterChip(
            selected = est.tracoTipo == "vantagem",
            onClick = {
                est.tracoTipo = "vantagem"
                est.paginaTraco = 0
            },
            label = { Text(stringResource(R.string.char_advantages)) },
        )
        FilterChip(
            selected = est.tracoTipo == "desvantagem",
            onClick = {
                est.tracoTipo = "desvantagem"
                est.paginaTraco = 0
            },
            label = { Text(stringResource(R.string.char_disadvantages)) },
        )
    }
    OutlinedTextField(
        est.tracoBusca,
        {
            est.tracoBusca = it
            est.paginaTraco = 0
        },
        Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.char_trait_search)) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
    )
    val permitidos = remember(est.tick, draft.id) { draft.allowedTraits().map { it.id }.toSet() }
    val busca = est.tracoBusca.trim().lowercase()
    val lista =
        pkg.data.traits.filter { traco ->
            val tipoOk = if (est.tracoTipo == "vantagem") traco.type == "vantagem" || traco.type == "poder" else traco.type == "desvantagem"
            tipoOk && (busca.isEmpty() || traco.name.lowercase().contains(busca) || traco.description.lowercase().contains(busca))
        }
    val paginas = ((lista.size + BLOCOS_POR_PAGINA_TRACOS - 1) / BLOCOS_POR_PAGINA_TRACOS).coerceAtLeast(1)
    val pagina = est.paginaTraco.coerceIn(0, paginas - 1)
    Muted(stringResource(R.string.wiz_traits_count, lista.size))
    GradeBlocos(lista.drop(pagina * BLOCOS_POR_PAGINA_TRACOS).take(BLOCOS_POR_PAGINA_TRACOS), 4) { traco, modificador ->
        val ativo = traco.id in escolhidos
        Bloco(
            glifo =
                if (traco.type == "desvantagem") {
                    "▼"
                } else if (traco.type == "poder") {
                    "✦"
                } else {
                    "▲"
                },
            nome = traco.name,
            selecionado = ativo,
            aoClicar = { est.tracoSel = traco.id },
            modifier = modificador,
            selo = if (ativo) "✓" else null,
            apagado = !ativo && traco.id !in permitidos,
        )
    }
    if (paginas > 1) Paginador(pagina, paginas) { est.paginaTraco = it }
    val traco = pkg.trait(est.tracoSel)
    if (traco != null) {
        val ativo = traco.id in escolhidos
        val porque = if (ativo) emptyList() else draft.whyNot(traco.id)
        AppCard {
            Text(traco.name, style = MaterialTheme.typography.titleMedium)
            Muted(
                stringResource(
                    when (traco.type) {
                        "desvantagem" -> R.string.char_disadvantages
                        "poder" -> R.string.char_filter_powers
                        else -> R.string.char_advantages
                    },
                ),
            )
            if (traco.description.isNotBlank()) Text(traco.description, style = MaterialTheme.typography.bodyMedium)
            if (porque.isNotEmpty()) ErrorText(porque.joinToString("; "))
            if (ativo || porque.isEmpty()) {
                OutlinedButton(onClick = {
                    if (ativo) draft.removeTrait(traco.id) else draft.addTrait(traco.id)
                    est.tick++
                }) { Text(stringResource(if (ativo) R.string.wiz_remove else R.string.wiz_choose)) }
            }
        }
    }
    BotaoContinuar(stringResource(R.string.wiz_continue_info), relatorio.valid, stringResource(R.string.wiz_hint_balance), aoContinuar)
}

// ---------- 7. Informações e criação ----------

@Composable
private fun PassoInformacoes(
    est: EstadoCriacao,
    dados: DadosCriacao,
    pkg: RulePackage,
    draft: CharacterDraft,
    relatorioValido: Boolean,
    statSobra: Int,
    exigeOrigem: Boolean,
    ocupado: Boolean,
    mensagem: String?,
    aoCriar: (CharacterChoices, String) -> Unit,
) {
    // CharacterDraft é mutável fora do Compose; manter o nome também em State garante atualização imediata do formulário.
    var nome by remember(draft.id) { mutableStateOf(draft.name) }
    val escolhas = est.escolhas()
    val errosCatalogo =
        remember(
            escolhas,
        ) { CharacterCreationCatalog.validateChoices(escolhas) + CharacterCreationCatalog.validateEffectScopes(pkg, escolhas) }
    val rotAparencia = stringResource(R.string.wiz_appearance)
    val rotPersonalidade = stringResource(R.string.wiz_personality)
    // PT-04: sem história escrita à mão. Aparência e personalidade viram a base do pedido à IA, feito após criar a ficha.
    val lore =
        listOf(rotAparencia to est.aparencia, rotPersonalidade to est.personalidade)
            .filter { it.second.isNotBlank() }
            .joinToString("\n") { it.first + ": " + it.second.trim() }
    val nomes =
        (listOf(est.raceId, est.subraceId, est.hybridId, est.classId, est.professionId) + est.mistas + est.extrasValidas())
            .filter { it.isNotBlank() }
            .mapNotNull { dados.entrada(it)?.name }
    val origem = escolhas.origemNarrativa()
    val bonusTotais =
        remember(escolhas) {
            if (errosCatalogo.isNotEmpty()) {
                emptyList()
            } else {
                CharacterCreationCatalog.selectedEffects(escolhas)
                    .groupBy { it.scope }
                    .mapValues { (_, lista) -> lista.sumOf { it.bonus } }
                    .filterValues { it != 0 }
                    .entries.sortedBy { it.key }
                    .map { (escopo, total) -> escopo.replace('_', ' ') + " " + (if (total > 0) "+" else "") + total }
            }
        }

    AppCard {
        Text(stringResource(R.string.char_summary_title), style = MaterialTheme.typography.titleSmall)
        Muted(if (nomes.isEmpty()) stringResource(R.string.char_not_selected) else nomes.joinToString(" · "))
        if (origem.isNotBlank()) Muted(origem)
        if (bonusTotais.isNotEmpty()) Muted(stringResource(R.string.wiz_review_bonuses, bonusTotais.joinToString(", ")))
    }
    OutlinedTextField(
        nome,
        {
            nome = it
            draft.name = it
            est.tick++
        },
        Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.char_name)) },
        singleLine = true,
    )
    OutlinedButton(
        onClick = {
            nome = SorteioNomes.sortear(est.originKingdomId.ifBlank { null }, evitar = nome)
            draft.name = nome
            est.tick++
        },
    ) { Text(stringResource(R.string.wiz_name_draw)) }
    Row(horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
        listOf("masculino" to R.string.char_sex_male, "feminino" to R.string.char_sex_female).forEach { (id, rotulo) ->
            FilterChip(selected = est.sexo == id, onClick = { est.sexo = id }, label = { Text(stringResource(rotulo)) })
        }
    }
    OutlinedTextField(
        value = est.idadeTxt,
        onValueChange = { est.idadeTxt = it.filter(Char::isDigit).take(4) },
        label = { Text(stringResource(R.string.char_age_field)) },
        supportingText = { Text(stringResource(R.string.char_age_required, IDADE_MAXIMA)) },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    CriacaoAparencia.categorias.forEach { (categoria, frases) ->
        Muted(categoria)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
            frases.forEach { frase ->
                FilterChip(
                    selected = CriacaoAparencia.contem(est.aparencia, frase),
                    onClick = { est.aparencia = CriacaoAparencia.alternar(est.aparencia, frase) },
                    label = { Text(frase) },
                )
            }
        }
    }
    OutlinedButton(onClick = { est.aparencia = CriacaoAparencia.sortear() }) { Text(stringResource(R.string.wiz_appearance_draw)) }
    if (est.aparencia.isNotBlank()) Muted(rotAparencia + ": " + est.aparencia)
    Muted(rotPersonalidade)
    CriacaoPersonalidade.categorias.forEach { (categoria, frases) ->
        Muted(categoria)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(CriacaoTokens.EspacoM)) {
            frases.forEach { frase ->
                FilterChip(
                    selected = CriacaoPersonalidade.contem(est.personalidade, frase),
                    onClick = { est.personalidade = CriacaoPersonalidade.alternar(est.personalidade, frase) },
                    label = { Text(frase) },
                )
            }
        }
    }
    OutlinedButton(onClick = { est.personalidade = CriacaoPersonalidade.sortear() }) { Text(stringResource(R.string.wiz_personality_draw)) }
    Muted(stringResource(R.string.wiz_story_by_ai_hint))

    val faltas =
        buildList {
            if (!est.racaCompleta(exigeOrigem)) add(stringResource(R.string.wiz_missing_race))
            if (est.classId.isBlank()) add(stringResource(R.string.wiz_missing_class))
            if (statSobra > 0) add(stringResource(R.string.wiz_hint_stats, statSobra))
            if (!relatorioValido) add(stringResource(R.string.wiz_hint_balance))
            if (nome.isBlank()) add(stringResource(R.string.wiz_missing_name))
            if (est.sexo.isBlank()) add(stringResource(R.string.char_sex_required))
            if (est.idade() !in 1..IDADE_MAXIMA) add(stringResource(R.string.char_age_required, IDADE_MAXIMA))
            addAll(errosCatalogo)
        }
    val conceito = (nomes + listOf(origem)).filter { it.isNotBlank() }.joinToString(" · ")
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = faltas.isEmpty() && !ocupado,
        onClick = {
            if (draft.concept.isBlank()) draft.concept = conceito
            aoCriar(escolhas, lore)
        },
    ) { Text(stringResource(R.string.char_create)) }
    faltas.forEach { Muted(it) }
    mensagem?.let { Muted(it) }
}
