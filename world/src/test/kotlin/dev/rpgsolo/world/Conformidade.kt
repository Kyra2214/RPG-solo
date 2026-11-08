package dev.rpgsolo.world

/** Apoio dos testes de conformidade Kotlin × Python (MI-08): vetores em `src/test/resources/conformidade/` e o molde do classpath. */
object Conformidade {
    val fonte: MoldeFonte = MoldeFonteClasspath()

    val manifesto: MoldeManifesto by lazy { MoldeManifesto.ler(checkNotNull(fonte.texto("manifesto.txt")) { "molde sem manifesto.txt" }) }

    val mundo: MundoMolde by lazy { CarregadorMolde.carregar(fonte, manifesto) }

    val hashDinamica: String by lazy { Dinamica.hash(fonte, manifesto) }

    /** Linhas do arquivo de vetores, cada uma separada em campos por `|`. */
    fun vetores(nome: String): List<List<String>> {
        val bytes =
            checkNotNull(Conformidade::class.java.getResourceAsStream("/conformidade/$nome")) {
                "vetores ausentes: $nome"
            }.use { it.readBytes() }
        return bytes.toString(Charsets.UTF_8).split('\n').filter { it.isNotEmpty() }.map { it.split('|') }
    }

    fun simulador(
        semente: Long,
        motores: (Dinamica) -> List<Motor>,
        passo: Int,
        retomada: Snapshot? = null,
        guardarDeltas: Boolean = true,
        saida: CronicaSaida? = null,
        aoFecharBloco: ((BlocoSaude, Snapshot?) -> Unit)? = null,
    ): Simulador {
        val din = Dinamica(fonte)
        return Simulador(
            mundo = mundo,
            din = din,
            semente = semente,
            motores = motores(din),
            hashDinamica = hashDinamica,
            saida = saida,
            guardarDeltas = guardarDeltas,
            passo = passo,
            retomada = retomada,
            aoFecharBloco = aoFecharBloco,
        )
    }

    fun nomes(motores: List<Motor>): String = motores.map { it.nome }.sortedWith(Ordem.CODIGO).joinToString(",")

    /** Ponto de conferência: dia_fim|deltas|notificações|hash da crônica|hash do estado|entidades. */
    fun ponto(s: Simulador): String =
        listOf(s.estado.proximoDia, s.crono.total, s.nNotificacoes, s.crono.hash, s.estado.hash(), s.estado.ent.size).joinToString("|")
}
