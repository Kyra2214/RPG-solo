package dev.rpgsolo.ui

import dev.rpgsolo.rules.CharacterDraft
import dev.rpgsolo.rules.RulePackage
import kotlin.random.Random

/**
 * Jogo rápido (CG-5.3, M5): parte de um herói pronto e deixa a ficha completa para criar com um toque na aba Informações.
 * ASSUMINDO: pontos de status distribuídos em rodízio entre os atributos até acabar o saldo ou o teto; uma vantagem
 * escolhida ao acaso entre as que deixam a ficha válida; nome sorteado pelo reino. O jogador pode mudar tudo depois.
 */
internal object CriacaoJogoRapido {
    fun preparar(
        heroi: HeroiPronto,
        indiceReino: Int,
        est: EstadoCriacao,
        draft: CharacterDraft,
        pkg: RulePackage,
        aleatorio: Random = Random.Default,
    ) {
        est.aplicarHeroi(heroi, indiceReino)
        if (draft.name.isBlank()) draft.name = SorteioNomes.sortear(heroi.escolhas.originKingdomId.ifBlank { null }, aleatorio)
        distribuirStatus(draft, pkg)
        escolherVantagem(draft, aleatorio)
        est.passo = PassoCriacao.INFORMACOES
        est.tick++
    }

    internal fun distribuirStatus(
        draft: CharacterDraft,
        pkg: RulePackage,
    ) {
        val atributos = pkg.data.stats.filterNot { AtributosDaCriacao.ehOculto(it.id) }
        if (atributos.isEmpty()) return
        var semAvanco = 0
        var i = 0
        while (draft.statPointsLeft() > 0 && semAvanco < atributos.size) {
            val a = atributos[i % atributos.size]
            val atual = draft.stats()[a.id] ?: a.default
            val ok = atual < a.max && draft.setStat(a.id, atual + 1) && draft.statPointsLeft() >= 0
            if (ok) {
                semAvanco = 0
            } else {
                if (atual < a.max) draft.setStat(a.id, atual)
                semAvanco++
            }
            i++
        }
    }

    internal fun escolherVantagem(
        draft: CharacterDraft,
        aleatorio: Random,
    ) {
        if (draft.traits().isNotEmpty()) return
        draft.allowedTraits().filter { it.type == "vantagem" }.shuffled(aleatorio).forEach { t ->
            if (draft.addTrait(t.id)) {
                if (draft.report().valid) return
                draft.removeTrait(t.id)
            }
        }
    }
}
