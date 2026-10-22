package dev.rpgsolo.ui

import dev.rpgsolo.engine.CampaignSession
import dev.rpgsolo.rules.RulePackage

/**
 * Inventário e equipamento do herói (R-03): extraído do `AppModel` sem mudar regras nem mensagens.
 * Não guarda estado: lê sessão, pacote e herói ativos por funções e avisa a interface em [onChanged].
 */
class InventoryActions(
    private val session: () -> CampaignSession,
    private val activePackage: () -> RulePackage?,
    private val playerId: () -> String,
    private val isPlayerCombatTurn: () -> Boolean,
    private val onChanged: () -> Unit,
) {
    fun inventoryView(): List<InventoryView> {
        val pkg = activePackage()
        val equipped = session().state.equipped[playerId()].orEmpty()
        return session().state.inventory[playerId()].orEmpty().map { (id, quantity) ->
            val item = pkg?.item(id)
            InventoryView(
                id = id,
                name = item?.name ?: id,
                quantity = quantity,
                kind = item?.kind ?: "desconhecido",
                slot = item?.slot,
                equipped = item?.slot?.let { equipped[it] == id } == true,
                description = item?.description ?: "Item não existe no pacote ativo.",
                price = item?.price,
            )
        }.filter { it.quantity > 0 }.sortedWith(compareBy<InventoryView>({ it.kind }, { it.name }))
    }

    /** Loja do pacote: saldo de moeda e itens com preço; null se o pacote não define moeda. */
    fun shopView(): ShopView? {
        val pkg = activePackage() ?: return null
        val currencyId = pkg.currencyItemId ?: return null
        val wallet = session().state.inventory[playerId()]
        val silver = wallet?.get(currencyId) ?: 0
        val goldId = pkg.goldItemId
        val gold = goldId?.let { wallet?.get(it) } ?: 0
        val balance = silver + gold * pkg.silverPerGold
        val entries =
            pkg.data.items.filter { it.price != null && it.id != currencyId }
                .map { ShopEntry(it.id, it.name, it.price!!, it.description) }
                .sortedWith(compareBy<ShopEntry>({ it.price }, { it.name }))
        return ShopView(
            pkg.item(currencyId)?.name ?: currencyId,
            balance,
            entries,
            goldId?.let { pkg.item(it)?.name ?: it },
            gold,
            silver,
            if (goldId != null) pkg.silverPerGold else 0,
        )
    }

    /** Usa consumíveis apenas quando o pacote declara uma ação mecânica explícita. */
    fun useItem(itemId: String): String? {
        return try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val item = requireNotNull(pkg.item(itemId)) { "item não existe no pacote ativo: $itemId" }
            require(item.kind == "consumivel") { "este item não é consumível" }
            val action =
                pkg.data.creation["item_${itemId}_acao"]
                    ?: return "o pacote não definiu o efeito mecânico de $itemId"
            val inCombat = session().state.combat != null
            if (inCombat && !isPlayerCombatTurn()) return "só é possível usar item no seu turno"
            val commands: List<dev.rpgsolo.engine.Command> =
                when (action) {
                    "cura" -> {
                        val resource =
                            pkg.data.creation["item_${itemId}_recurso"]
                                ?: return "o pacote não definiu o recurso restaurado por $itemId"
                        val amount =
                            pkg.data.creation["item_${itemId}_quantidade"]?.toIntOrNull()
                                ?: return "o pacote não definiu a quantidade restaurada por $itemId"
                        require(amount > 0) { "quantidade de cura deve ser positiva" }
                        val current =
                            session().state.entities[playerId()]?.resources?.get(resource)
                                ?: return "o personagem não possui o recurso '$resource'"
                        require(current.current < current.max) { "o recurso já está cheio; o consumível não foi gasto" }
                        listOf(
                            dev.rpgsolo.engine.Heal(playerId(), resource, amount),
                            dev.rpgsolo.engine.ConsumeItem(playerId(), itemId, 1),
                        )
                    }
                    else -> return "ação de consumível não suportada pelo app: $action"
                }
            val actionCommands = if (inCombat) commands + dev.rpgsolo.engine.NextTurn else commands
            when (val result = session().executeAll(actionCommands)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    onChanged()
                    "${item.name} usado."
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalStateException) {
            e.message
        }
    }

    fun equipItem(itemId: String): String? =
        try {
            require(session().state.combat == null) { "não é possível trocar equipamento durante o combate" }
            val item = requireNotNull(activePackage()?.item(itemId)) { "item não existe no catálogo: $itemId" }
            val slot = item.slot
            require(item.kind == "equipamento" && slot != null) { "item não é equipável" }
            when (val result = session().execute(dev.rpgsolo.engine.EquipItem(playerId(), itemId, slot))) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    onChanged()
                    null
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        } catch (e: IllegalArgumentException) {
            e.message
        }

    fun unequipItem(slot: String): String? {
        if (session().state.combat != null) return "não é possível trocar equipamento durante o combate"
        return when (val result = session().execute(dev.rpgsolo.engine.UnequipItem(playerId(), slot))) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                onChanged()
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }
    }

    /** MU-07.3: magias à venda do pacote ativo, com a marca de já aprendida pelo herói. */
    fun spellShopView(): List<SpellShopEntry> {
        val pkg = activePackage() ?: return emptyList()
        if (pkg.currencyItemId == null) return emptyList()
        val known = session().state.entities[playerId()]?.spells.orEmpty()
        return pkg.spells.map { SpellShopEntry(it.id, it.name, it.price, it.id in known) }.sortedWith(compareBy({ it.price }, { it.name }))
    }

    /** MU-07.3: compra uma magia (paga em moedas e ocupa um slot livre) em uma única troca atômica. */
    fun buySpell(spellId: String): String? =
        try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val currencyId = requireNotNull(pkg.currencyItemId) { "o pacote não define moeda" }
            val spell = requireNotNull(pkg.spell(spellId)) { "magia não existe no pacote ativo: $spellId" }
            require(session().state.combat == null) { "não é possível comprar magia durante o combate" }
            val wallet = session().state.inventory[playerId()]
            val payment =
                pkg.planPayment(wallet?.get(currencyId) ?: 0, pkg.goldItemId?.let { wallet?.get(it) } ?: 0, spell.price)
                    ?: return "saldo insuficiente"
            val commands: List<dev.rpgsolo.engine.Command> =
                buildList {
                    if (payment.silver > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), currencyId, payment.silver))
                    if (payment.gold > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
                    if (payment.change > 0) add(dev.rpgsolo.engine.AddItem(playerId(), currencyId, payment.change))
                    add(dev.rpgsolo.engine.LearnSpell(playerId(), spell.id))
                }
            when (val result = session().executeAll(commands)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    onChanged()
                    null
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        } catch (e: IllegalArgumentException) {
            e.message
        }

    /** MU-07.1: troca [goldQty] de ouro por prata (`toGold` = false) ou prata por [goldQty] de ouro (`toGold` = true), de forma atômica. */
    fun convertCoins(
        toGold: Boolean,
        goldQty: Int = 1,
    ): String? =
        try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val silverId = requireNotNull(pkg.currencyItemId) { "o pacote não define moeda" }
            val goldId = requireNotNull(pkg.goldItemId) { "o pacote não define ouro" }
            require(goldQty in 1..999) { "quantidade inválida" }
            require(session().state.combat == null) { "não é possível trocar moedas durante o combate" }
            val silver = Math.multiplyExact(goldQty, pkg.silverPerGold)
            val commands: List<dev.rpgsolo.engine.Command> =
                if (toGold) {
                    listOf(
                        dev.rpgsolo.engine.ConsumeItem(playerId(), silverId, silver),
                        dev.rpgsolo.engine.AddItem(playerId(), goldId, goldQty),
                    )
                } else {
                    listOf(
                        dev.rpgsolo.engine.ConsumeItem(playerId(), goldId, goldQty),
                        dev.rpgsolo.engine.AddItem(playerId(), silverId, silver),
                    )
                }
            when (val result = session().executeAll(commands)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    onChanged()
                    null
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: ArithmeticException) {
            "valor total excede o limite"
        }

    /** Compra [qty] de [itemId] pelo preço do pacote, em troca atômica de moeda por item. */
    fun buyItem(
        itemId: String,
        qty: Int = 1,
    ): String? = trade(itemId, qty, buying = true)

    /** Vende [qty] de [itemId] pelo preço do pacote (preço único), em troca atômica de item por moeda. */
    fun sellItem(
        itemId: String,
        qty: Int = 1,
    ): String? = trade(itemId, qty, buying = false)

    private fun trade(
        itemId: String,
        qty: Int,
        buying: Boolean,
    ): String? =
        try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val currencyId = requireNotNull(pkg.currencyItemId) { "o pacote não define moeda" }
            val item = requireNotNull(pkg.item(itemId)) { "item não existe no pacote ativo: $itemId" }
            val price = requireNotNull(item.price) { "${item.name} não é negociável" }
            require(itemId != currencyId) { "a moeda não pode ser comprada nem vendida" }
            require(qty in 1..999) { "quantidade inválida" }
            require(session().state.combat == null) { "não é possível negociar durante o combate" }
            val total = Math.multiplyExact(price, qty)
            val wallet = session().state.inventory[playerId()]
            val payment =
                if (buying) {
                    pkg.planPayment(wallet?.get(currencyId) ?: 0, pkg.goldItemId?.let { wallet?.get(it) } ?: 0, total)
                        ?: return "saldo insuficiente"
                } else {
                    null
                }
            val commands: List<dev.rpgsolo.engine.Command> =
                if (payment != null) {
                    buildList {
                        if (payment.silver > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), currencyId, payment.silver))
                        if (payment.gold > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
                        if (payment.change > 0) add(dev.rpgsolo.engine.AddItem(playerId(), currencyId, payment.change))
                        add(dev.rpgsolo.engine.AddItem(playerId(), itemId, qty))
                    }
                } else {
                    listOf(
                        dev.rpgsolo.engine.ConsumeItem(playerId(), itemId, qty),
                        dev.rpgsolo.engine.AddItem(playerId(), currencyId, total),
                    )
                }
            when (val result = session().executeAll(commands)) {
                is dev.rpgsolo.engine.ExecResult.Accepted -> {
                    onChanged()
                    null
                }
                is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
            }
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: ArithmeticException) {
            "valor total excede o limite"
        }
}
