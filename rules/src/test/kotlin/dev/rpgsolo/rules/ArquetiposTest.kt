package dev.rpgsolo.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** `arquetipos.txt`: formato, validação pelo pacote e ordem alfabética sem acento. */
class ArquetiposTest {
    private fun dir(path: String): File = listOf("src/$path", "rules/src/$path").map(::File).first { it.exists() }

    private fun files(path: String): MutableMap<String, String> =
        dir(path).listFiles()!!.filter { it.name.endsWith(".txt") }.associate { it.name to it.readText() }.toMutableMap()

    private fun proprio(): MutableMap<String, String> = files("main/resources/packages/sistema-proprio")

    private fun install(f: Map<String, String>): InstallResult = PackageRegistry().install(f)

    private fun pkg(): RulePackage =
        when (val result = install(proprio())) {
            is InstallResult.Success -> result.pkg
            is InstallResult.Failure -> throw AssertionError("pacote próprio inválido: ${result.errors.joinToString("; ")}")
        }

    @Test fun pacoteProprioCarregaComArquetipos() {
        val p = pkg()
        assertTrue(p.data.archetypes.size >= 50)
        assertTrue(p.data.archetypes.all { it.description.isNotBlank() })
    }

    @Test fun todoArquetipoPassaNasRegrasDoPacote() {
        val p = pkg()
        p.data.archetypes.forEach {
            val r = p.validateCharacter(it.stats, it.traits)
            assertTrue("${it.id}: ${r.errors}", r.valid)
        }
    }

    @Test fun ordemAlfabeticaIgnoraAcentoEMaiusculas() {
        val nomes = pkg().archetypesSorted().map { it.name }
        val i = nomes.indexOf("Órfão das ruas")
        assertTrue(i > nomes.indexOf("Mercenário"))
        assertTrue(i < nomes.indexOf("Viajante"))
        assertEquals("Acrobata", nomes.first())
        assertEquals(nomes.size, nomes.toSet().size)
    }

    @Test fun temEspeciesEOficiosPedidos() {
        val ids = pkg().data.archetypes.map { it.id }.toSet()
        listOf("anao", "lobisomem", "vampiro", "elfo", "elfo-negro", "ciborgue", "orc", "androide", "golem")
            .forEach { assertTrue("falta $it", it in ids) }
    }

    @Test fun cadaArquetipoTemTracosEDescricaoPropria() {
        val p = pkg()
        p.data.archetypes.forEach {
            assertTrue("${it.id}: sem traços", it.traits.isNotEmpty())
            assertTrue("${it.id}: descrição curta", it.description.length >= 30)
        }
        assertEquals(p.data.archetypes.size, p.data.archetypes.map { it.name }.toSet().size)
    }

    @Test fun buscaIgnoraAcentoEMaiusculas() {
        val p = pkg()
        assertTrue(p.archetypesSorted("GUARDIAO").map { it.id }.contains("guardiao"))
        assertEquals("guardiao", p.archetypesSorted("GUARDIAO").first().id)
        assertEquals(listOf("orfao-das-ruas"), p.archetypesSorted("orfão").map { it.id })
        assertTrue(p.archetypesSorted("zzzz").isEmpty())
    }

    @Test fun rascunhoCarregaArquetipoEPodeSerEditado() {
        val p = pkg()
        val d = CharacterDraft(p, "pj1")
        assertTrue(d.loadArchetype("guardiao"))
        assertEquals(2, d.stat("poder"))
        assertTrue(d.traits().contains("escudo-vivo"))
        assertTrue(d.report().valid)
        assertTrue(d.setStat("poder", 3))
        assertFalse(d.loadArchetype("nao-existe"))
    }

    @Test fun arquetipoQueQuebraPontosImpedeInstalacao() {
        val f = proprio()
        f["arquetipos.txt"] = f.getValue("arquetipos.txt") + "\nexagerado|Exagerado|Quer tudo.|poder=5,habilidade=5,resistencia=5|"
        val r = install(f) as InstallResult.Failure
        assertTrue(r.errors.any { it.contains("arquétipo exagerado") })
    }

    @Test fun arquetipoComRequisitoNaoCumpridoImpedeInstalacao() {
        val f = proprio()
        f["arquetipos.txt"] = f.getValue("arquetipos.txt") + "\nfraco|Fraco|Sem força.|poder=0,habilidade=0,resistencia=0|golpe-forte"
        val r = install(f) as InstallResult.Failure
        assertTrue(r.errors.any { it.contains("arquétipo fraco") && it.contains("exige") })
    }

    @Test fun formatoRuimEIdDuplicadoSaoRecusadosComArquivoELinha() {
        val f = proprio()
        f["arquetipos.txt"] = "a|So tres campos|x\ndup|Um|d|poder=1|\ndup|Dois|d|poder=1|"
        val r = (PackageParser.parse(f) as ParseResult.Fail).errors
        assertTrue(r.any { it.contains("arquetipos.txt linha 1") && it.contains("esperado 5 campos") })
        assertTrue(r.any { it.contains("arquetipos.txt: id duplicado: dup") })
    }

    @Test fun tracoDesconhecidoNoArquetipoEhRecusado() {
        val f = proprio()
        f["arquetipos.txt"] = "x|X|d|poder=1|traco-que-nao-existe"
        val r = install(f) as InstallResult.Failure
        assertTrue(r.errors.any { it.contains("arquétipo x") && it.contains("desconhecido") })
    }

    @Test fun pacoteSemArquetiposContinuaValido() {
        val f = proprio()
        f.remove("arquetipos.txt")
        f.remove("opcoes-arquetipo.txt")
        val r = install(f) as InstallResult.Success
        assertNull(r.pkg.archetype("guardiao"))
        assertTrue(r.pkg.archetypesSorted().isEmpty())
    }

    @Test fun filhoSobrescreveArquetipoDoPaiPorId() {
        val base = proprio()
        val filho =
            mutableMapOf(
                "manifest.txt" to "id|proprio-ext\nsistema|proprio\nedicao|sistema-proprio\nversao|0.1.0\norigem|autoral\nlicenca|x\nlicenca_status|VERIFICADA\ntipo|TESTE\nestende|sistema-proprio\n",
                "arquetipos.txt" to "guardiao|Guardião Veterano|Mais calejado.|poder=2,habilidade=1,resistencia=3|escudo-vivo,dever-de-proteger",
            )
        val reg = PackageRegistry()
        assertTrue(reg.install(base) is InstallResult.Success)
        assertTrue(reg.install(filho) is InstallResult.Success)
        assertEquals("Guardião Veterano", reg.get("proprio-ext")!!.archetype("guardiao")!!.name)
        assertEquals("Guardião", reg.get("sistema-proprio")!!.archetype("guardiao")!!.name)
    }
}
