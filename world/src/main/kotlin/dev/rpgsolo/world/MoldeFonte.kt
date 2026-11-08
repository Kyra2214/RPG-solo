package dev.rpgsolo.world

/** De onde vêm os bytes do molde exportado. Os caminhos são relativos à raiz do molde e usam `/`. */
interface MoldeFonte {
    /** Bytes do arquivo, ou `null` se ele não existir. */
    fun bytes(caminho: String): ByteArray?
}

/** Texto UTF-8 do arquivo com fim de linha normalizado para `\n` (como a leitura em modo texto do Python). */
fun MoldeFonte.texto(caminho: String): String? = bytes(caminho)?.toString(Charsets.UTF_8)?.replace("\r\n", "\n")?.replace('\r', '\n')

/** Fonte em memória, para testes e para ferramentas. */
class MoldeFonteMapa(private val arquivos: Map<String, ByteArray>) : MoldeFonte {
    override fun bytes(caminho: String): ByteArray? = arquivos[caminho]
}

/** Fonte nos recursos do classpath (`rules/src/main/resources/mundos/mythralis/`), que no app vão dentro do APK. */
class MoldeFonteClasspath(private val loader: ClassLoader) : MoldeFonte {
    constructor() : this(requireNotNull(MoldeFonteClasspath::class.java.classLoader) { "sem ClassLoader" })

    override fun bytes(caminho: String): ByteArray? =
        loader.getResourceAsStream(MoldeManifesto.RAIZ_RECURSOS + "/" + caminho)?.use { it.readBytes() }
}
