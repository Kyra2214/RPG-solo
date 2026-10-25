package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Sistema próprio: equilíbrio vantagens x desvantagens por quantidade (1→1, 2→3, 3→5, depois dobra)
 * e cardápio de pelo menos 3 vantagens e 3 desvantagens por arquétipo (específicas + gerais).
 */
class EquilibrioEOpcoesTest {
    private fun files(): MutableMap<String, String> =
        listOf("src/main/resources/packages/sistema-proprio", "rules/src/main/resources/packages/sistema-proprio")
            .map(::File).first { it.exists() }
            .listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun pkg(f: Map<String, String> = files()): RulePackage = (PackageRegistry().install(f) as InstallResult.Success).pkg

    private val forte = mapOf("poder" to 3, "habilidade" to 3, "resistencia" to 3)

    // Vantagens e desvantagens gerais sem exclusões entre si, para montar fichas de qualquer tamanho.
    private val vantagens = listOf("golpe-forte", "visao-noturna", "pele-dura", "aliado", "sortudo")
    private val desvantagens =
        listOf(
            "atrapalhado", "audicao-ruim", "esquecido", "gago", "preguicoso", "guloso", "ganancioso", "mentiroso-compulsivo",
            "claustrofobico", "medo-de-altura", "medo-de-fogo", "medo-de-bichos", "sono-pesado", "insonia", "estomago-fraco",
            "alergico", "manco", "antipatico", "marcante", "procurado",
        )

    private fun ficha(
        adv: Int,
        dis: Int,
    ) = vantagens.take(adv) + desvantagens.take(dis)

    @Test fun tabelaDeEquilibrio_1x1_2x3_3x5_e_depoisDobra() {
        val p = pkg()
        assertEquals(listOf(1, 3, 5), p.balanceTable)
        val esperado = listOf(0 to 0, 1 to 1, 2 to 3, 3 to 5, 4 to 10, 5 to 20, 6 to 40, 7 to 80)
        esperado.forEach { (v, d) -> assertEquals("$v vantagens", d, p.requiredDisadvantages(v)) }
    }

    @Test fun semDobrarMantemOUltimoValor() {
        val f = files()
        f["criacao.txt"] = f.getValue("criacao.txt").replace("equilibrio_apos|dobrar", "equilibrio_apos|manter")
        val p = pkg(f)
        assertEquals(5, p.requiredDisadvantages(4))
        assertEquals(5, p.requiredDisadvantages(9))
    }

    @Test fun contaGigante_naoEstouraEFicaLimitada() {
        val p = pkg()
        assertTrue(p.requiredDisadvantages(500) in 1..1_000_000)
    }

    @Test fun umaVantagemExigeUmaDesvantagem() {
        val p = pkg()
        val sem = p.validateCharacter(forte, ficha(1, 0))
        assertFalse(sem.valid)
        assertTrue(sem.errors.any { it.contains("1 vantagem exige pelo menos 1 desvantagem; a ficha tem 0") })
        assertTrue(p.validateCharacter(forte, ficha(1, 1)).valid)
    }

    @Test fun duasVantagensExigemTresDesvantagens() {
        val p = pkg()
        assertFalse(p.validateCharacter(forte, ficha(2, 2)).valid)
        assertTrue(p.validateCharacter(forte, ficha(2, 3)).valid)
    }

    @Test fun tresVantagensExigemCincoDesvantagens() {
        val p = pkg()
        val r = p.validateCharacter(forte, ficha(3, 4))
        assertTrue(r.errors.any { it.contains("3 vantagens exigem pelo menos 5 desvantagens") && it.contains("faltam 1") })
        assertTrue(p.validateCharacter(forte, ficha(3, 5)).valid)
    }

    @Test fun deDepoisDaTerceiraDobra_quatroExigemDez_cincoExigemVinte() {
        val p = pkg()
        assertFalse(p.validateCharacter(forte, ficha(4, 9)).valid)
        assertTrue(p.validateCharacter(forte, ficha(4, 10)).valid)
        assertFalse(p.validateCharacter(forte, ficha(5, 19)).valid)
        val cinco = p.validateCharacter(forte, ficha(5, 20))
        assertTrue(cinco.errors.toString(), cinco.valid)
        assertEquals(Triple(5, 20, 20), Triple(cinco.advantages, cinco.disadvantages, cinco.disadvantagesRequired))
    }

    @Test fun modoDificil_maisDesvantagensQueOMinimoEhPermitido_eSemVantagemTambem() {
        val p = pkg()
        assertTrue(p.validateCharacter(forte, ficha(1, 6)).valid)
        assertTrue(p.validateCharacter(forte, ficha(0, 4)).valid)
        assertTrue(p.validateCharacter(forte, emptyList()).valid)
    }

    @Test fun periciasNaoContamNoEquilibrio_epoderesContamComoVantagem() {
        val p = pkg()
        val medio = mapOf("poder" to 2, "habilidade" to 2, "resistencia" to 2, "agilidade" to 2)
        assertTrue(p.validateCharacter(medio, listOf("atletismo", "furtividade", "percepcao")).valid)
        val poder = p.validateCharacter(medio, listOf("voo"))
        assertEquals(1, poder.advantages)
        assertFalse(poder.valid)
    }

    @Test fun vantagensEDesvantagensNaoGastamPontos() {
        val p = pkg()
        val r = p.validateCharacter(forte, ficha(3, 5))
        assertEquals(9, r.pointsSpent)
    }

    @Test fun tabelaInvalidaImpedeInstalacao() {
        val f = files()
        f["criacao.txt"] = f.getValue("criacao.txt").replace("equilibrio|1,3,5", "equilibrio|3,1")
        assertTrue(PackageRegistry().install(f) is InstallResult.Failure)
        val g = files()
        g["criacao.txt"] = g.getValue("criacao.txt").replace("equilibrio_apos|dobrar", "equilibrio_apos|triplicar")
        assertTrue(PackageRegistry().install(g) is InstallResult.Failure)
    }

    // ---------- cardápio dos arquétipos ----------

    @Test fun todoArquetipoTemPeloMenosTresVantagensETresDesvantagens() {
        val p = pkg()
        assertEquals(72, p.data.archetypes.size)
        p.data.archetypes.forEach { a ->
            val o = p.archetypeOptions(a.id)!!
            assertTrue("${a.id}: vantagens ${o.advantages.size}", o.advantages.size >= 3)
            assertTrue("${a.id}: desvantagens ${o.disadvantages.size}", o.disadvantages.size >= 3)
        }
    }

    /** Traço específico de classe: a descrição começa com "Nome do arquétipo:" (ou Clérigo/Ladino, nomes dos pares antigos). */
    private fun nomes(p: RulePackage): Set<String> = p.data.archetypes.map { it.name }.toSet() + setOf("Clérigo", "Ladino")

    private class Prefixo(val nomes: Set<String>) {
        fun containsMatchIn(d: String) = d.substringBefore(":", "") in nomes
    }

    private fun prefixo(p: RulePackage) = Prefixo(nomes(p))

    @Test fun cardapioMisturaDesvantagemEspecificaEGeral() {
        val p = pkg()
        p.data.archetypes.forEach { a ->
            val ds = p.archetypeOptions(a.id)!!.disadvantages.map { p.trait(it)!! }
            assertTrue("${a.id}: sem específica", ds.any { prefixo(p).containsMatchIn(it.description) })
            assertTrue("${a.id}: sem geral", ds.any { !prefixo(p).containsMatchIn(it.description) })
        }
    }

    @Test fun vantagensDoCardapioSaoQuaseTodasUnicas_naoRepete() {
        val p = pkg()
        val todas = p.data.archetypes.flatMap { p.archetypeOptions(it.id)!!.advantages }
        assertTrue("distintas: ${todas.toSet().size}", todas.toSet().size >= todas.size - 3)
        val listas = p.data.archetypes.map { p.archetypeOptions(it.id)!!.advantages.toSet() }
        assertEquals(listas.size, listas.toSet().size)
        // vantagem específica de arquétipo leva o nome dele na descrição
        assertTrue(p.trait("pocoes-rapidas")!!.description.startsWith("Alquimista:"))
    }

    @Test fun desvantagensGeraisPedidasExistem() {
        val p = pkg()
        listOf("atrapalhado", "desatento", "ma-visao").forEach {
            val t = p.trait(it)!!
            assertEquals("desvantagem", t.type)
            assertFalse(prefixo(p).containsMatchIn(t.description))
        }
        val gerais = p.data.traits.filter { it.type == "desvantagem" && !prefixo(p).containsMatchIn(it.description) }
        assertTrue("gerais: ${gerais.size}", gerais.size >= 40)
    }

    @Test fun exclusoesDasGeraisValem() {
        val p = pkg()
        val r = p.validateCharacter(forte, listOf("sortudo", "azarado"))
        assertTrue(r.errors.any { it.contains("não pode ser combinado") })
        assertTrue(
            p.validateCharacter(forte, listOf("sentidos-agucados", "desatento", "aliado", "esquecido")).errors
                .any { it.contains("não pode ser combinado") },
        )
    }

    @Test fun cardapioInteiroMaisGeraisFechaNaConta_paraOsSetentaEDoisArquetipos() {
        val p = pkg()
        val gerais = desvantagens
        p.data.archetypes.forEach { a ->
            val o = p.archetypeOptions(a.id)!!
            val pericias = a.traits.filter { p.trait(it)?.type == "pericia" }
            val base = o.advantages + o.disadvantages
            val falta = p.requiredDisadvantages(o.advantages.size) - o.disadvantages.size
            val extra = gerais.filter { it !in base }.take(maxOf(0, falta))
            val r = p.validateCharacter(a.stats, pericias + base + extra)
            assertTrue("${a.id}: ${r.errors}", r.valid)
        }
    }

    @Test fun arquetipoComecaComUmaVantagemEUmaDesvantagem_conjuradorasComDuasETres() {
        val p = pkg()
        p.data.archetypes.forEach { a ->
            val r = p.validateCharacter(a.stats, a.traits)
            assertTrue("${a.id}: ${r.errors}", r.valid)
            if ("talento-arcano" in a.traits) {
                assertEquals(a.id, Triple(2, 3, 3), Triple(r.advantages, r.disadvantages, r.disadvantagesRequired))
            } else {
                assertEquals(a.id, Triple(1, 1, 1), Triple(r.advantages, r.disadvantages, r.disadvantagesRequired))
            }
        }
    }

    @Test fun cardapioComMenosDeTresImpedeInstalacao() {
        val f = files()
        f["opcoes-arquetipo.txt"] =
            f.getValue("opcoes-arquetipo.txt").lines().joinToString("\n") { l ->
                if (l.startsWith("alquimista|")) "alquimista|pocoes-rapidas,bomba-de-fumaca|efeitos-colaterais,nariz-queimado,alergico" else l
            }
        val r = PackageRegistry().install(f) as InstallResult.Failure
        assertTrue(r.errors.any { it.contains("alquimista") && it.contains("pelo menos 3 vantagens") })
    }

    @Test fun cardapioComTracoQueOArquetipoNaoAlcancaImpedeInstalacao() {
        val f = files()
        f["opcoes-arquetipo.txt"] =
            f.getValue("opcoes-arquetipo.txt").lines().joinToString("\n") { l ->
                if (l.startsWith("batedor|")) "batedor|olho-de-aguia,golpe-forte,aviso-a-tempo|inquietacao-de-trilha,avesso-a-multidoes,solitario" else l
            }
        val r = PackageRegistry().install(f) as InstallResult.Failure
        assertTrue(r.errors.any { it.contains("batedor") && it.contains("golpe-forte") && it.contains("poder >= 2") })
    }

    @Test fun cardapioComTipoErradoOuTracoInexistenteImpedeInstalacao() {
        val f = files()
        f["opcoes-arquetipo.txt"] =
            f.getValue("opcoes-arquetipo.txt").lines().joinToString("\n") { l ->
                if (l.startsWith("ninja|")) "ninja|passo-de-sombra,mao-de-shuriken,escalada-silenciosa|lealdade-ao-cla,mao-de-shuriken,fantasma" else l
            }
        val r = PackageRegistry().install(f) as InstallResult.Failure
        assertTrue(r.errors.any { it.contains("ninja") && it.contains("não é desvantagem") })
        assertTrue(
            r.errors.any { it.contains("ninja") && it.contains("inexistente fantasma") || it.contains("traço inexistente fantasma") },
        )
    }

    @Test fun arquetipoSemCardapioImpedeInstalacao() {
        val f = files()
        f["opcoes-arquetipo.txt"] = f.getValue("opcoes-arquetipo.txt").lines().filterNot { it.startsWith("monge|") }.joinToString("\n")
        val r = PackageRegistry().install(f) as InstallResult.Failure
        assertTrue(r.errors.any { it.contains("arquétipo monge") })
    }

    @Test fun rascunhoOfereVantagensMesmoSemDesvantagem_oEquilibrioSoApareceNoRelatorio() {
        val p = pkg()
        val d = CharacterDraft(p, "x")
        assertTrue(d.loadArchetype("guerreiro"))
        d.removeTrait("mestre-de-armas")
        d.removeTrait("mao-pesada")
        assertTrue(d.allowedTraits().any { it.id == "golpe-forte" })
        assertTrue(d.whyNot("golpe-forte").isEmpty())
        d.addTrait("golpe-forte")
        assertFalse(d.report().valid)
        assertEquals(1, d.report().disadvantagesRequired)
        d.addTrait("atrapalhado")
        assertTrue(d.report().valid)
    }
}
