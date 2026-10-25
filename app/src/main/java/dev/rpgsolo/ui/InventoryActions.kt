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
    private val wearer: () -> dev.rpgsolo.rules.WearerProfile? = { null },
) {
    fun inventoryView(): List<InventoryView> {
        val pkg = activePackage()
        val equipped = session().state.equipped[playerId()].orEmpty()
        return session().state.inventory[playerId()].orEmpty().map { (id, quantity) ->
            val item = pkg?.item(id)
            InventoryView(
                id = id,
                name = item?.let { it.name + qualitySuffix(id) } ?: id,
                quantity = quantity,
                kind = item?.kind ?: "desconhecido",
                slot = item?.slot,
                equipped = item?.slot?.let { equipped[it] == id } == true,
                description = item?.description ?: "Item não existe no pacote ativo.",
                price = item?.price,
                restricted = item != null && item.kind == "equipamento" && pkg.isRestricted(id, wearer()),
            )
        }.filter { it.quantity > 0 }.sortedWith(compareBy<InventoryView>({ it.kind }, { it.name }))
    }

    private fun qualitySuffix(id: String): String {
        val quality = dev.rpgsolo.rules.ItemRef.quality(id) ?: return ""
        return if (quality == dev.rpgsolo.rules.ItemQuality.NORMAL) "" else " (${quality.label})"
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
            val baseId = dev.rpgsolo.rules.ItemRef.base(itemId)
            val action =
                pkg.data.creation["item_${baseId}_acao"]
                    ?: return "o pacote não definiu o efeito mecânico de ${item.name}"
            val inCombat = session().state.combat != null
            if (inCombat && !isPlayerCombatTurn()) return "só é possível usar item no seu turno"
            val commands: List<dev.rpgsolo.engine.Command> =
                when (action) {
                    "cura" -> {
                        val resource =
                            pkg.data.creation["item_${baseId}_recurso"]
                                ?: return "o pacote não definiu o recurso restaurado por ${item.name}"
                        val base =
                            pkg.data.creation["item_${baseId}_quantidade"]?.toIntOrNull()
                                ?: return "o pacote não definiu a quantidade restaurada por ${item.name}"
                        require(base > 0) { "quantidade de cura deve ser positiva" }
                        val current =
                            session().state.entities[playerId()]?.resources?.get(resource)
                                ?: return "o personagem não possui o recurso '$resource'"
                        val amount = pkg.healAmount(itemId, current.max) ?: pkg.scaleByQuality(base, itemId)
                        require(current.current < current.max) { "o recurso já está cheio; o consumível não foi gasto" }
                        listOf(
                            dev.rpgsolo.engine.Heal(playerId(), resource, amount),
                            dev.rpgsolo.engine.ConsumeItem(playerId(), itemId, 1),
                        )
                    }
                    "remover-condicao" -> {
                        val condition =
                            pkg.data.creation["item_${baseId}_condicao"]
                                ?: return "o pacote não definiu a condição removida por ${item.name}"
                        require(session().state.conditions[playerId()]?.contains(condition) == true) {
                            "o personagem não está com essa condição; o consumível não foi gasto"
                        }
                        listOf(
                            dev.rpgsolo.engine.RemoveCondition(playerId(), condition),
                            dev.rpgsolo.engine.ConsumeItem(playerId(), itemId, 1),
                        )
                    }
                    "aplicar-condicao" -> {
                        val condition =
                            pkg.data.creation["item_${baseId}_condicao"]
                                ?: return "o pacote não definiu a condição aplicada por ${item.name}"
                        val definition =
                            pkg.data.conditions.firstOrNull { it.id == condition }
                                ?: return "condição inexistente no pacote: $condition"
                        require(session().state.conditions[playerId()]?.contains(condition) != true) {
                            "o personagem já está com essa condição; o consumível não foi gasto"
                        }
                        val baseDuration =
                            pkg.data.creation["item_${baseId}_duracao"]?.toIntOrNull() ?: definition.durationRounds
                        listOf(
                            dev.rpgsolo.engine.AddCondition(
                                playerId(),
                                definition.id,
                                baseDuration?.let { pkg.scaleByQuality(it, itemId) },
                                definition.damagePerRound,
                                definition.damageResource,
                                definition.damagePercent,
                            ),
                            dev.rpgsolo.engine.ConsumeItem(playerId(), itemId, 1),
                        )
                    }
                    "encantar" -> {
                        val element =
                            dev.rpgsolo.engine.ElementChart.parse(
                                pkg.data.creation["item_${baseId}_elemento"] ?: return "o pacote não definiu o elemento de ${item.name}",
                            )
                        val days = pkg.data.creation["item_${baseId}_dias"]?.toIntOrNull() ?: return "o pacote não definiu a duração de ${item.name}"
                        require(!inCombat) { "não é possível usar pergaminho de elemento durante o combate" }
                        val equipped = session().state.equipped[playerId()].orEmpty()
                        // Peito primeiro; sem peito elegível, a arma. O pergaminho não é gasto se nenhuma peça aceitar.
                        val target =
                            listOf(dev.rpgsolo.rules.CHEST_SLOT, dev.rpgsolo.rules.WEAPON_SLOT)
                                .firstOrNull { slot -> equipped[slot]?.let { pkg.scrollBlock(it) == null } == true }
                                ?: return "nenhuma peça equipada (arma ou peito) aceita o pergaminho; o consumível não foi gasto"
                        listOf(
                            dev.rpgsolo.engine.ApplyScrollEnchant(playerId(), target, equipped.getValue(target), element, days),
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

    /** N-11: variação do máximo dos recursos (`max_<recurso>` do item) ao equipar (+1) ou desequipar (-1). */
    private fun maxDelta(
        itemId: String,
        sign: Int,
    ): Map<String, Int> {
        val pkg = activePackage() ?: return emptyMap()
        return pkg.data.resources.associate { it.id to sign * pkg.equipmentBonus(listOf(itemId), "max_" + it.id, wearer()) }
            .filterValues { it != 0 }
    }

    fun equipItem(itemId: String): String? =
        try {
            require(session().state.combat == null) { "não é possível trocar equipamento durante o combate" }
            val item = requireNotNull(activePackage()?.item(itemId)) { "item não existe no catálogo: $itemId" }
            val slot = item.slot
            require(item.kind == "equipamento" && slot != null) { "item não é equipável" }
            activePackage()?.handsConflict(itemId, session().state.equipped[playerId()].orEmpty())?.let { return it }
            when (
                val result =
                    session().execute(
                        dev.rpgsolo.engine.EquipItem(
                            playerId(),
                            itemId,
                            slot,
                            maxDelta(itemId, 1),
                            activePackage()?.requiredLevel(itemId) ?: 1,
                        ),
                    )
            ) {
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
        val worn = session().state.equipped[playerId()]?.get(slot)
        val delta = if (worn != null) maxDelta(worn, -1) else emptyMap()
        return when (val result = session().execute(dev.rpgsolo.engine.UnequipItem(playerId(), slot, delta))) {
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

    /** N-17: ferreiro NPC encanta [itemId] (peito ou arma, não equipado, um por peça) com [element]; paga 5 × preço × qualidade, troca atômica. */
    fun enchantItem(
        itemId: String,
        element: dev.rpgsolo.engine.ElementPath,
    ): String? =
        try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            require(element != dev.rpgsolo.engine.ElementPath.NONE) { "escolha um elemento" }
            pkg.enchantBlock(itemId)?.let { return it }
            require(session().state.combat == null) { "não é possível encantar durante o combate" }
            require(session().state.equipped[playerId()].orEmpty().values.none { it == itemId }) { "desequipe a peça antes de encantar" }
            val currencyId = requireNotNull(pkg.currencyItemId) { "o pacote não define moeda" }
            val price = requireNotNull(pkg.enchantPrice(itemId)) { "item sem preço" }
            val wallet = session().state.inventory[playerId()]
            val payment =
                pkg.planPayment(wallet?.get(currencyId) ?: 0, pkg.goldItemId?.let { wallet?.get(it) } ?: 0, price)
                    ?: return "saldo insuficiente (encantamento custa $price)"
            val quality = dev.rpgsolo.rules.ItemRef.quality(itemId) ?: dev.rpgsolo.rules.ItemQuality.NORMAL
            val enchanted = dev.rpgsolo.rules.ItemRef.of(dev.rpgsolo.rules.ItemRef.base(itemId), quality, element)
            val commands: List<dev.rpgsolo.engine.Command> =
                buildList {
                    if (payment.silver > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), currencyId, payment.silver))
                    if (payment.gold > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
                    if (payment.change > 0) add(dev.rpgsolo.engine.AddItem(playerId(), currencyId, payment.change))
                    add(dev.rpgsolo.engine.ConsumeItem(playerId(), itemId, 1))
                    add(dev.rpgsolo.engine.AddItem(playerId(), enchanted, 1))
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

    /**
     * N-36: tatua o herói no status [scope] com o [tattooerGrade] de quem tatua (1 a 5; jogador ou NPC). Paga 100 prata × grau.
     * [coverIndex] cobre a marca daquela posição (precisa ser de grau menor e paga o preço integral da nova).
     */
    fun tattooHero(
        scope: String,
        tattooerGrade: Int,
        coverIndex: Int? = null,
    ): String? =
        try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            require(session().state.combat == null) { "não é possível tatuar durante o combate" }
            require(tattooerGrade in 1..dev.rpgsolo.engine.Tattoos.MAX_GRADE) { "grau do Tatuador fora de 1..5" }
            val currencyId = requireNotNull(pkg.currencyItemId) { "o pacote não define moeda" }
            val price = dev.rpgsolo.engine.Tattoos.price(tattooerGrade)
            val wallet = session().state.inventory[playerId()]
            val payment =
                pkg.planPayment(wallet?.get(currencyId) ?: 0, pkg.goldItemId?.let { wallet?.get(it) } ?: 0, price)
                    ?: return "saldo insuficiente (a marca custa $price)"
            val commands: List<dev.rpgsolo.engine.Command> =
                buildList {
                    if (payment.silver > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), currencyId, payment.silver))
                    if (payment.gold > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
                    if (payment.change > 0) add(dev.rpgsolo.engine.AddItem(playerId(), currencyId, payment.change))
                    add(dev.rpgsolo.engine.ApplyTattoo(playerId(), scope, tattooerGrade, coverIndex))
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

    /** N-36: marcas atuais do herói, limite e grau dele como Tatuador (0 se não tem a profissão). */
    fun tattooView(): TattooView {
        val hero = session().state.entities[playerId()]
        val levels = hero?.skillTreeLevels.orEmpty()
        val marks = session().state.tattoos[playerId()].orEmpty().map { TattooMarkView(it.scope, it.grade) }
        return TattooView(marks, dev.rpgsolo.engine.Tattoos.limit(levels), dev.rpgsolo.engine.Tattoos.grade(levels))
    }

    /** N-33.4: peças equipadas com vagas de gema, gemas no inventário e o que o herói pode fazer (sem chamar a IA). */
    fun gemsView(): GemsView {
        val pkg = activePackage()
        val state = session().state
        val hero = state.entities[playerId()]
        val equipped = state.equipped[playerId()].orEmpty()
        val socketed = state.gems[playerId()].orEmpty()

        fun gemName(gem: dev.rpgsolo.engine.Gem): String =
            pkg?.item(dev.rpgsolo.engine.Gems.itemId(gem))?.name ?: dev.rpgsolo.engine.Gems.itemId(gem)
        val slots =
            equipped.mapNotNull { (slot, itemId) ->
                val sockets = dev.rpgsolo.rules.ItemRef.quality(itemId)?.gemSockets ?: 0
                val gems = socketed[slot]?.takeIf { it.itemId == itemId }?.gems.orEmpty()
                if (sockets == 0 && gems.isEmpty()) return@mapNotNull null
                GemSlotView(slot, pkg?.item(itemId)?.name?.let { it + qualitySuffix(itemId) } ?: itemId, sockets, gems.map(::gemName))
            }.sortedBy { it.slot }
        val cutter = hero?.let { dev.rpgsolo.engine.Gems.cutterGrade(it.skillTreeLevels) > 0 } == true
        val stock =
            state.inventory[playerId()].orEmpty().mapNotNull { (id, qty) ->
                val gem = dev.rpgsolo.engine.Gems.parse(id) ?: return@mapNotNull null
                if (qty <= 0) return@mapNotNull null
                GemStockView(
                    id,
                    gemName(gem),
                    qty,
                    cutter && gem.quality < dev.rpgsolo.engine.Gems.MAX_QUALITY,
                    dev.rpgsolo.engine.Gems.npcFee(gem),
                )
            }.sortedBy { it.name }
        val canSet = hero?.let { dev.rpgsolo.engine.Gems.setterGrade(it.skillTreeLevels) > 0 } == true
        return GemsView(slots, stock, canSet)
    }

    /** N-33.4: comandos que pagam [price] em prata/ouro do herói; `null` se o saldo não cobre. */
    private fun paymentCommands(price: Int): List<dev.rpgsolo.engine.Command>? {
        val pkg = activePackage() ?: return null
        val currencyId = pkg.currencyItemId ?: return null
        val wallet = session().state.inventory[playerId()]
        val payment = pkg.planPayment(wallet?.get(currencyId) ?: 0, pkg.goldItemId?.let { wallet?.get(it) } ?: 0, price) ?: return null
        return buildList {
            if (payment.silver > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), currencyId, payment.silver))
            if (payment.gold > 0) add(dev.rpgsolo.engine.ConsumeItem(playerId(), requireNotNull(pkg.goldItemId), payment.gold))
            if (payment.change > 0) add(dev.rpgsolo.engine.AddItem(playerId(), currencyId, payment.change))
        }
    }

    /** N-33.4: vagas de gema da peça equipada em [slot] (pela qualidade do id); 0 sem peça. */
    fun gemSockets(slot: String): Int {
        val itemId = session().state.equipped[playerId()]?.get(slot) ?: return 0
        return dev.rpgsolo.rules.ItemRef.quality(itemId)?.gemSockets ?: 0
    }

    private fun runGemCommands(commands: List<dev.rpgsolo.engine.Command>): String? =
        when (val result = session().executeAll(commands)) {
            is dev.rpgsolo.engine.ExecResult.Accepted -> {
                onChanged()
                null
            }
            is dev.rpgsolo.engine.ExecResult.Rejected -> result.reason
        }

    /**
     * N-33.4: engasta a gema [gemItemId] (do inventário) na peça equipada em [slot]. O Montador de Gemas (herói) engasta sem custo;
     * com [viaNpc] o ferreiro NPC cobra 50 prata × qualidade.
     */
    fun socketGem(
        slot: String,
        gemItemId: String,
        viaNpc: Boolean = false,
    ): String? =
        try {
            require(session().state.combat == null) { "não é possível engastar durante o combate" }
            val gem = requireNotNull(dev.rpgsolo.engine.Gems.parse(gemItemId)) { "não é uma gema válida: $gemItemId" }
            val hero = requireNotNull(session().state.entities[playerId()]) { "herói inexistente" }
            val pay: List<dev.rpgsolo.engine.Command> =
                if (viaNpc) {
                    paymentCommands(dev.rpgsolo.engine.Gems.npcFee(gem)) ?: return "saldo insuficiente (o engaste custa ${dev.rpgsolo.engine.Gems.npcFee(gem)})"
                } else {
                    require(
                        dev.rpgsolo.engine.Gems.setterGrade(hero.skillTreeLevels) > 0,
                    ) { "só o Montador de Gemas engasta sem o ferreiro" }
                    emptyList()
                }
            runGemCommands(
                pay + dev.rpgsolo.engine.ConsumeItem(playerId(), gemItemId, 1) +
                    dev.rpgsolo.engine.SocketGem(playerId(), slot, gem, gemSockets(slot)),
            )
        } catch (e: IllegalArgumentException) {
            e.message
        }

    /** N-33.4: tira a gema da posição [index] da peça em [slot] e a devolve ao inventário (a gema não se destrói). */
    fun removeGem(
        slot: String,
        index: Int,
        viaNpc: Boolean = false,
    ): String? =
        try {
            require(session().state.combat == null) { "não é possível remover durante o combate" }
            val itemId = requireNotNull(session().state.equipped[playerId()]?.get(slot)) { "não há peça equipada em $slot" }
            val gem =
                requireNotNull(
                    session().state.gems[playerId()]?.get(slot)?.takeIf {
                        it.itemId == itemId
                    }?.gems?.getOrNull(index),
                ) { "não há gema na posição $index" }
            val hero = requireNotNull(session().state.entities[playerId()]) { "herói inexistente" }
            val pay: List<dev.rpgsolo.engine.Command> =
                if (viaNpc) {
                    paymentCommands(dev.rpgsolo.engine.Gems.npcFee(gem)) ?: return "saldo insuficiente (a remoção custa ${dev.rpgsolo.engine.Gems.npcFee(gem)})"
                } else {
                    require(
                        dev.rpgsolo.engine.Gems.setterGrade(hero.skillTreeLevels) > 0,
                    ) { "só o Montador de Gemas remove sem o ferreiro" }
                    emptyList()
                }
            runGemCommands(
                pay + dev.rpgsolo.engine.RemoveGem(playerId(), slot, index) +
                    dev.rpgsolo.engine.AddItem(playerId(), dev.rpgsolo.engine.Gems.itemId(gem), 1),
            )
        } catch (e: IllegalArgumentException) {
            e.message
        }

    /**
     * N-33.4: o Lapidador tenta subir a gema [gemItemId] em 1 nível (d20; 1 a 5 falha, 25%). Na falha a gema cai 1 nível e a de qualidade 1 some.
     * Devolve o resultado em texto (ou a recusa). Qualidade 3 é o teto.
     */
    fun cutGem(gemItemId: String): String =
        try {
            require(session().state.combat == null) { "não é possível lapidar durante o combate" }
            val gem = requireNotNull(dev.rpgsolo.engine.Gems.parse(gemItemId)) { "não é uma gema válida: $gemItemId" }
            val hero = requireNotNull(session().state.entities[playerId()]) { "herói inexistente" }
            require(dev.rpgsolo.engine.Gems.cutterGrade(hero.skillTreeLevels) > 0) { "só o Lapidador lapida gemas" }
            require(gem.quality < dev.rpgsolo.engine.Gems.MAX_QUALITY) { "a gema já está na qualidade máxima" }
            require((session().state.inventory[playerId()]?.get(gemItemId) ?: 0) > 0) { "o herói não tem essa gema" }
            val roll = session().rollPlain("lapidar:$gemItemId", dev.rpgsolo.engine.Gems.CUT_DIE_SIDES)
            val result = dev.rpgsolo.engine.Gems.cut(gem, roll)
            val commands: List<dev.rpgsolo.engine.Command> =
                buildList {
                    add(dev.rpgsolo.engine.ConsumeItem(playerId(), gemItemId, 1))
                    if (result != null) add(dev.rpgsolo.engine.AddItem(playerId(), dev.rpgsolo.engine.Gems.itemId(result), 1))
                }
            runGemCommands(commands) ?: when {
                result == null -> "Falhou (d20 = $roll): a gema se perdeu."
                result.quality > gem.quality -> "Sucesso (d20 = $roll): a gema subiu para a qualidade ${result.quality}."
                else -> "Falhou (d20 = $roll): a gema caiu para a qualidade ${result.quality}."
            }
        } catch (e: IllegalArgumentException) {
            e.message ?: "recusado"
        }

    /** N-21: receitas de ofício que o herói já pode usar (habilidade de profissão e nível). */
    fun availableRecipes(): List<dev.rpgsolo.rules.RecipeDef> {
        val hero = session().state.entities[playerId()] ?: return emptyList()
        return activePackage()?.availableRecipes(hero).orEmpty()
    }

    /** N-20/N-21: faz [recipeId] com o herói; devolve o resultado em texto (sucesso, falha ou recusa). Fora de combate. */
    fun craftRecipe(recipeId: String): String {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        return when (val r = pkg.executeRecipe(session(), playerId(), recipeId)) {
            is dev.rpgsolo.engine.CraftResult.Refused -> r.reason
            is dev.rpgsolo.engine.CraftResult.Done -> {
                onChanged()
                val name = pkg.recipe(recipeId)?.name ?: recipeId
                val roll = r.roll?.let { " (dado $it, precisava de ${r.chance} ou menos)" }.orEmpty()
                if (r.success) {
                    "$name: sucesso$roll"
                } else if (pkg.recipe(recipeId)?.kind == dev.rpgsolo.engine.CraftKind.REPAIR) {
                    "$name: falhou$roll; o material se perdeu e o item continua quebrado"
                } else {
                    "$name: falhou$roll; parte dos materiais se perdeu"
                }
            }
        }
    }

    /**
     * N-22: o herói afana [itemId] de [targetId] (item não equipado, fora de combate). Só quem tem Ladino, Assassino, Agente das Sombras ou Espião.
     * ASSUMINDO: usa Destreza do herói e Agilidade do alvo base da ficha (sem equipamento nem bônus ativos).
     */
    fun pilfer(
        targetId: String,
        itemId: String,
    ): String {
        val pkg = activePackage() ?: return "pacote de regras não está ativo"
        val state = session().state
        val hero = state.entities[playerId()] ?: return "personagem inexistente"
        val target = state.entities[targetId] ?: return "alvo inexistente"
        if (!pkg.canPilfer(hero)) return "${hero.name} não sabe afanar"
        return when (
            val r =
                session().attemptPilfer(
                    hero.id,
                    targetId,
                    itemId,
                    hero.stats["destreza"] ?: 0,
                    target.stats["agilidade"] ?: 0,
                )
        ) {
            is dev.rpgsolo.engine.CraftResult.Refused -> r.reason
            is dev.rpgsolo.engine.CraftResult.Done -> {
                onChanged()
                val roll = " (dado ${r.roll}, precisava de ${r.chance} ou menos)"
                if (r.success) "${hero.name} afanou $itemId de ${target.name}$roll" else "${target.name} flagrou ${hero.name}$roll"
            }
        }
    }

    /** N-21: Ferreiro jogador (degrau 5) encanta [itemId] com [element], pagando só 1 pedra do elemento; troca atômica, fora de combate. */
    fun smithEnchantItem(
        itemId: String,
        element: dev.rpgsolo.engine.ElementPath,
    ): String? =
        try {
            val pkg = requireNotNull(activePackage()) { "pacote de regras não está ativo" }
            val hero = requireNotNull(session().state.entities[playerId()]) { "personagem inexistente" }
            require(element != dev.rpgsolo.engine.ElementPath.NONE) { "escolha um elemento" }
            pkg.smithEnchantBlock(hero, itemId)?.let { return it }
            require(session().state.combat == null) { "não é possível encantar durante o combate" }
            require(session().state.equipped[playerId()].orEmpty().values.none { it == itemId }) { "desequipe a peça antes de encantar" }
            val stone = requireNotNull(pkg.enchantMaterial(element)) { "o pacote não tem material para este elemento" }
            val quality = dev.rpgsolo.rules.ItemRef.quality(itemId) ?: dev.rpgsolo.rules.ItemQuality.NORMAL
            val enchanted = dev.rpgsolo.rules.ItemRef.of(dev.rpgsolo.rules.ItemRef.base(itemId), quality, element)
            val commands =
                listOf<dev.rpgsolo.engine.Command>(
                    dev.rpgsolo.engine.ConsumeItem(playerId(), stone, 1),
                    dev.rpgsolo.engine.ConsumeItem(playerId(), itemId, 1),
                    dev.rpgsolo.engine.AddItem(playerId(), enchanted, 1),
                )
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
            val base = Math.multiplyExact(price, qty)
            val tradePercent = session().state.entities[playerId()]?.let { pkg.tradePercent(it) } ?: 0
            val total = if (buying) pkg.discountedPrice(base, tradePercent) else pkg.boostedSale(base, tradePercent)
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

/** N-33.4: peça equipada com vagas de gema ([sockets]) e os nomes das gemas já engastadas, na ordem. */
data class GemSlotView(val slot: String, val itemName: String, val sockets: Int, val gems: List<String>)

/** N-33.4: gema no inventário; [canCut] se o herói é Lapidador e a gema ainda sobe; [npcFee] o preço do ferreiro NPC. */
data class GemStockView(val itemId: String, val name: String, val quantity: Int, val canCut: Boolean, val npcFee: Int)

/** N-36: [scope] é o status reforçado e [grade] o grau de quem tatuou. */
data class TattooMarkView(val scope: String, val grade: Int)

data class TattooView(val marks: List<TattooMarkView>, val limit: Int, val heroGrade: Int) {
    val isFull: Boolean get() = marks.size >= limit
}

/** N-33.4: [canSet] = o herói é Montador de Gemas (engasta e remove sem pagar o ferreiro NPC). */
data class GemsView(val slots: List<GemSlotView>, val stock: List<GemStockView>, val canSet: Boolean) {
    val isEmpty: Boolean get() = slots.all { it.gems.isEmpty() } && stock.isEmpty()
}
