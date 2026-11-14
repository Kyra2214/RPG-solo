package dev.rpgsolo.ui

/** Conquistas locais (CG-10.1, M10): marcos simples guardados só neste aparelho, sem conta nem rede. */
enum class Conquista(val titulo: String, val descricao: String) {
    PRIMEIRO_HEROI("Primeiro herói", "Criou a primeira ficha."),
    HEROI_PRONTO("Atalho de lenda", "Começou de um herói pronto."),
    HEROI_MESTICO("Sangue misturado", "Criou um herói mestiço."),
    PRIMEIRA_CAMPANHA("Primeira crônica", "Iniciou a primeira campanha."),
    MUNDO_VIVO("Mundo vivo", "Iniciou uma campanha em Mythralis."),
}

/** Armazenamento das conquistas já desbloqueadas; [ler] e [gravar] isolam o SharedPreferences para teste. */
class ConquistasLocais(
    private val ler: () -> Set<String>,
    private val gravar: (Set<String>) -> Unit,
) {
    fun desbloqueadas(): Set<Conquista> = ler().mapNotNull { id -> Conquista.entries.firstOrNull { it.name == id } }.toSet()

    /** Devolve `true` só na primeira vez que a conquista é registrada. */
    fun registrar(c: Conquista): Boolean {
        val atual = ler()
        if (c.name in atual) return false
        gravar(atual + c.name)
        return true
    }
}
