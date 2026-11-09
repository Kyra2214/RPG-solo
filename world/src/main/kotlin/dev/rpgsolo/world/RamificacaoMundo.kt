package dev.rpgsolo.world

/** Modos de persistência do mundo (MP-07, D-I4): o eixo da persistência, independente da fidelidade à fonte. */
enum class ModoMundo {
    /** Cadeia canônica: a campanha entra no mesmo mundo e o que acontece nele vale para as outras campanhas. */
    CANONE_COMPARTILHADO,

    /** Ramificação isolada a partir de um ponto (bloco) de um mundo existente: herda o estado e depois diverge. */
    LINHA_ALTERNATIVA,

    /** Cópia independente do molde, sem estado simulado: começa do zero e nunca toca o cânone. */
    COPIA_INDEPENDENTE,
}

class ResultadoRamificacao(val modo: ModoMundo, val mundo: MundoRegistro, val vinculo: VinculoMundo)

/**
 * Ramificação de mundo (MI-12). Só [ModoMundo.CANONE_COMPARTILHADO] compartilha dados; os outros dois criam um mundo
 * novo, então duas campanhas neles não se alteram. Uma alternativa nunca altera o cânone em silêncio: o mundo de
 * origem só é lido.
 * 🔶 ASSUMINDO: o modo não tem coluna própria (sem nova migração); quem chama grava `mundo.modo` nos fatos da campanha.
 */
object RamificacaoMundo {
    fun idDaCampanha(campanhaId: String): String = "mundo-$campanhaId"

    fun canoneCompartilhado(
        store: MundoStore,
        campanhaId: String,
        mundoId: String,
        agora: Long,
    ): ResultadoRamificacao {
        val mundo = checkNotNull(store.mundo(mundoId)) { "mundo inexistente: $mundoId" }
        val vinculo = VinculoMundo(campanhaId, mundo.id, mundo.moldeId, agora)
        store.vincular(vinculo)
        return ResultadoRamificacao(ModoMundo.CANONE_COMPARTILHADO, mundo, vinculo)
    }

    /** [bloco] nulo = o último bloco guardado do mundo de origem. */
    fun linhaAlternativa(
        store: MundoStore,
        campanhaId: String,
        origemId: String,
        agora: Long,
        bloco: Int? = null,
    ): ResultadoRamificacao {
        val origem = checkNotNull(store.mundo(origemId)) { "mundo inexistente: $origemId" }
        val novo = store.ramificar(origemId, idDaCampanha(campanhaId), bloco ?: origem.ultimoBloco, agora)
        val vinculo = VinculoMundo(campanhaId, novo.id, novo.moldeId, agora)
        store.vincular(vinculo)
        return ResultadoRamificacao(ModoMundo.LINHA_ALTERNATIVA, novo, vinculo)
    }

    fun copiaIndependente(
        store: MundoStore,
        campanhaId: String,
        moldeId: String,
        moldeHash: String,
        dinamicaHash: String,
        semente: Long,
        agora: Long,
    ): ResultadoRamificacao {
        val novo = MundoRegistro(idDaCampanha(campanhaId), moldeId, moldeHash, dinamicaHash, semente, agora, agora)
        store.criarMundo(novo)
        val vinculo = VinculoMundo(campanhaId, novo.id, moldeId, agora)
        store.vincular(vinculo)
        return ResultadoRamificacao(ModoMundo.COPIA_INDEPENDENTE, novo, vinculo)
    }
}
