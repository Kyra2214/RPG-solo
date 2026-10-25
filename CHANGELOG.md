## 2026-10-25 — plano: bloco WD (mundo pré-configurado principal), só documentação

- `docs/MUNDO_PRECONFIGURADO.md`: proposta do autor guardada como referência (5 reinos, 50 cidades, 8 a 12 NPCs por reino, geografia, povos, facções, 3 modos de cânone).
- `PLANO_RPG_SOLO.md`: novo bloco **WD** (WD-01 a WD-10). Decisões: molde estático e imutável nos recursos + deltas no log da campanha com instanciação sob demanda; reaproveita o pipeline de documento-fonte (`SourceIndex`/`SourceContext`) e `CanonFacts` (ORIGINAL, DIVERGENCE, AI_CREATED); dois eixos independentes (fidelidade `SourceMode` × persistência: compartilhado, alternativa, cópia); validador de mundo com testes no CI. MP-08 passa a priorizar um mundo principal rico em vez de 3 a 5 rasos.
- Em aberto: concorrência no cânone compartilhado; nome, tom e conceito do mundo; formato dos dados; destino do Boss do cenário. Nenhum código alterado. Próximo: LG-02.

## 2026-10-25 — plano: fuga 1/3 com XP 50%, Poupar só para relevantes e escolha de mundo (MP-08), só documentação

- RV-01: chance de fuga **1/3**, **XP da fuga = 50%**; botão **Poupar** só para `ELITE`, `CHEFE` e `CHEFE_FINAL` com dossiê (evita repetição em toda batalha). Lacuna anotada: a `ThreatCategory` não fica gravada na entidade.
- **MP-08** (novo): na criação do personagem o jogador escolhe mundo anterior, mundo pré-configurado ou cria o próprio; `WorldTemplate` (molde) separado do Mundo/crônica (instância, MP-07). Ordem: MP-08 antes do MP-07.
- Em aberto: destino do Boss do cenário (morto ou derrotado). Nenhum código alterado. Próximo: LG-02.

## 2026-10-25 — plano: fuga por d6 (RV-01) e legado em ficha nova (MP-07), só documentação

- RV-01 decidido: NPC elegível que zera os PV rola 1d6 no motor; **5 ou 6 foge** vivo com 1 PV e deixa cair 1 item do próprio inventário (sem item, nada); 1 a 4 morre. A IA só narra. Limiar configurável no pacote. ASSUMINDO que "5/6" é resultado 5 ou 6 (1/3).
- MP-07: ficha nova no mundo anterior recebe histórias de legado e NPCs que conheceram o herói antigo (vínculos do LG-01 + marcos do MP-01).
- Em aberto: destino do Boss do cenário (morto ou derrotado). Nenhum código alterado. Próximo: LG-02.

## 2026-10-25 — plano: mundo persistente e fama (MP-07), só documentação

- `PLANO_RPG_SOLO.md`: novo **MP-07** (mundo/crônica herdado entre campanhas, fama com alcance e disposição por região, rumores exatos, exagerados ou falsos que viajam pelos `placeLinks`). A escolha **mundo anterior ou mundo novo** é feita na criação da campanha, com a mesma ficha ou outra.
- **Regra revisada:** o escopo do procurado deixou de ser "tudo do mundo fica para trás". Com a mesma ficha e o mesmo mundo, cartaz, reputação e rumores continuam; só em mundo novo o procurado entra como rumor de nível baixo. Ordem sugerida atualizada (MP-07 depois do MP-02).
- Nenhum código alterado. Decisões em aberto continuam: poupar (jogador ou motor) e o destino do Boss (morto ou derrotado). Próximo: LG-02.

## 2026-10-25 — plano: frente MP (memória entre campanhas, rivais e procurado), só documentação

- `PLANO_RPG_SOLO.md`: novo bloco **MP** depois do bloco LG, com RV-01 a RV-06 e MP-01 a MP-06: marcos estruturados com certeza e escopo (PESSOA ou MUNDO), estados de derrota (poupado, fugiu, humilhado), persistência entre campanhas, vida fora de cena limitada, reencontro com justificativa, relação em eixos, alcance e procurado (Boss que age por intermediários) e a regra de escopo do procurado (acompanha o protagonista com a ficha). O LG-05 (panteão) passa a ser parte do MP-02.
- `docs/NPCS_RECORRENTES_MEMORIA_ENTRE_CAMPANHAS.md`: proposta do autor guardada como referência.
- Decisões em aberto registradas: poupar por escolha do jogador ou fuga decidida pelo motor; no cenário do Boss nível 99, morto ou só derrotado. Nenhum código alterado. Próximo: LG-02.

## 2026-10-25 — engine: registro do legado e do assassino (LG-01, não compilado)

- `Damage.sourceId` (opcional) e `EntityDied.killerId/cause` (opcionais; logs antigos decodificam com `null`). Origem passada nos ataques do motor (`attack`, `unifiedAttack`), nos poderes `Strike` e no ataque do companheiro; morte por condição grava `cause = "condicao:<id>"`.
- `Legacy.kt`: `HeroLegacy`, `LegacyBond`, `DeathKind`, `KillerStatus` e `HeroLegacy.killerStatus(state, playerId)`. `GameState.legacies` (dados antigos assumem vazio): o `apply` de `EntityDied` de um PLAYER grava o legado (nome, nível, conceito, dia, assassino e até 5 vínculos com |relação| ≥ 30 de NPCs vivos); `EntityRevived` apaga. Trocar de herói (`NpcAssumed`) preserva o legado, e o segundo herói ganha o próprio.
- Plano: bloco **LG** no `PLANO_RPG_SOLO.md` com os módulos LG-01 a LG-06.
- `LegadoLg01Test` (20 testes) escrito. ASSUMINDO: só `Damage` com `sourceId` identifica o assassino; dano de proposta da IA, perigos do `GameLoop` e `FailSurvival` ficam como `UNKNOWN` (sem vingança). **Escrito, não compilado nem executado** (sem Gradle neste ambiente). Próximo: LG-02.

## 2026-10-25 — app: painel do herói morto (legado, M-04 (f) parcial, não compilado)

- `LegacyActions` (`app`) e `LegacyPanel` (aba Jogar, aparece só com o herói morto ou a campanha encerrada). Ordem do M-04: (1) item de renascimento vale primeiro (botão "Usar <item>", `UseRebirthItem`); (2) sem item, lista os NPCs vivos com **só o que o jogador já descobriu** (`npcSheet().discovered`) e o botão "Assumir <nome>" (`AssumeNpc`); (3) sem item e sem NPC, "Encerrar a campanha" (`EndCampaign`). O motor valida tudo; recusas aparecem no card.
- `AppModel.legacyView/legacyRebirth/legacyAssume/legacyEnd`: ao assumir, troca `playerId` para o NPC e chama `saveMetadata()` (senão a reabertura voltaria ao herói morto). 8 strings `legacy_*`.
- **Correção do plano:** o `request_spell` da IA já existia (MU-07.6: `request_power` com `power: "magia:<id>"`); a nota "segue pendente" estava desatualizada.
- ASSUMINDO: o painel só enxerga o herói atual (`playerId`); depois de encerrada a campanha o motor recusa tudo e o card só mostra o motivo (arquivar na biblioteca ainda não foi feito); a oferta do NPC é pela tela, não pela narração do mestre. **Ainda falta do legado:** (b) o mestre oferecer o NPC no fluxo e o contexto/prompt dele, (c) teste de recuperação dos itens do corpo, (d) arquivar a campanha encerrada, (e) aliados acompanharem o nível do herói. Os testes `AssumeNpcTest` e `RebirthAndEndTest` não rodam fora do Gradle (usam persistência/JSON stubada). Nada do `app` foi compilado.

## 2026-10-25 — app: painel de marcas do Tatuador na aba Jogar (N-36, não compilado)

- `TattooPanel` (`PlayScreen.kt`): card recolhível "Marcas do Tatuador (n de limite)" com as marcas atuais (status e grau), botões **Tatuar** por status (Ataque, Defesa, Velocidade, PV máximo) com o grau e o preço (100 prata × grau), interruptor para usar um Tatuador NPC (escolha do grau 1 a 5) e **Cobrir** para trocar uma marca de grau menor quando o limite está cheio. Desabilitado em combate e durante a narração.
- `InventoryActions.tattooView()` e `AppModel.tattooView()/tattooScopeLabel()` com `TattooView` e `TattooMarkView`; 8 strings `tattoo_*`. A ação de pagar e tatuar é a que já existia (`tattooHero`).
- ASSUMINDO: o painel só aparece para quem é Tatuador ou já tem marca (quem nunca tatuou e não tem a profissão não vê o card; a primeira marca de NPC vem pelo mestre ou por subir o grau); herói Tatuador usa o próprio grau por padrão, o interruptor troca para NPC; a cobertura exige grau da nova maior que o da marca coberta (o motor recusa o resto). Sem teste de interface. Nada compilado nem executado.

## 2026-10-25 — engine/ai/app: Criador de Animais e Treinador ligados ao companheiro (N-33.3, motor executado; ai/app não compilados)

- **Criador (lealdade semanal):** `Kennel.plan(state, silver, breederDegree)` e `SettleKennel(silver, breederDegree)` somam +1 de lealdade por grau (0 a 5) por semana a todos os animais que o herói ainda tem, guardados ou não, depois das quedas da semana (teto 100; quem chegou a 0 na semana já está perdido e não recebe). Entra no mesmo evento `KennelWeekSettled` (o `delta` é o líquido). `KennelSettlement.after`, `RestRules.breederDegree` e `CompanionActions.breederDegree` (maior degrau aprendido de `profissao-criador-de-animais-N`) levam o grau ao descanso e ao trazer animal.
- **Treinador (pontos de status):** `SyncTrainerBonus(ownerId, degree)` e evento `TrainerBonusSynced`: o animal que anda com o herói (vivo, não guardado nem perdido) recebe 1 ponto por grau (`Companions.trainerPoints`, distribuição pelo tipo de corpo); os outros perdem o que tinham, então o bônus pertence ao herói e acompanha a troca. Guardado em `CompanionState.trainerBonus` e somado a `Entity.stats` só pela diferença. Sem diferença não grava nada. Todo lote de `CompanionActions.run` termina com esse ajuste (`trainerDegree` lê `profissao-treinador-N`).
- **Testes:** `CriadorTreinadorN33Test` (6). **Validação:** `engine` compilado com o compilador Kotlin do npm (serialização stubada); 46 de 46 passam (`CriadorTreinadorN33Test`, `ReadestramentoN33Test`, `CriadouroN33Test`, `CompanheiroN32Test`, `CompanheiroXpN33Test`, `CompanheiroCombateN32Test`). `ai` e `app` (`KennelSettlement`, `LocalActions`, `CompanionActions`, `AppModel`) não foram compilados.
- ASSUMINDO: os pesos por tipo de corpo estão no código (`Companions.TRAINER_ORDER`: quadrúpede ataque/resistência/velocidade, alado velocidade/destreza/ataque, rastejante destreza/ataque/resistência, aquático resistência/velocidade/ataque, humanoide destreza/ataque/resistência, inseto destreza/velocidade/ataque; o 1º ponto vai ao status principal e a lista se repete até o 5º) e não em `companheiros.txt`, por serem meus e ajustáveis; o bônus só se acerta quando se usa o painel Companheiro (não no descanso), então após subir de grau vale a partir da próxima ação do painel; o Criador soma mesmo ao animal que anda com o herói e ao de lealdade 100 (sem efeito).

## 2026-10-25 — engine/app: readestramento do animal perdido (N-33.2d, motor executado; app não compilado)

- **Motor:** `RetrainCompanion(animalId, baseChance, percentRoll, loyaltyRoll, maxAnimals)` e evento `CompanionRetrained` (`Kennel.kt`, `Companion.kt`, `Events.kt`). A chance é a da receita de adestrar menos 30 pontos (`Companions.retrainChance`, piso 5% e teto 95%, como as receitas); d100 <= chance acerta. No sucesso o animal volta a andar com o herói com lealdade igual ao d20, mantém nível, ataque e truques, recupera a relação de aliado e o que andava com o herói vai para o criadouro. Na falha gasta 1 de 2 tentativas (`CompanionState.retrainAttempts`); a segunda falha marca `abandoned` (perdido para sempre, recusa tudo, inclusive trazer).
- Recusas: animal que não está perdido, abandonado, sem tentativas, herói ou animal morto, combate em andamento, dados fora de faixa e, só no sucesso, limite de animais por grau de Adestrador (`maxAnimals`).
- **App:** `CompanionActions.lostViews/retrain` e `AppModel.companionLostViews/companionRetrain`; o painel Companheiro lista o animal perdido com as tentativas que restam e um botão por marca de adestramento do inventário ("Readestrar com X (chance%)"). A marca é consumida na tentativa, mesmo na falha, no mesmo lote do comando (recusa desfaz tudo). O texto do resultado aparece no card. 3 strings novas.
- **Testes:** `ReadestramentoN33Test` (6). **Validação:** compilei o `engine` com o compilador Kotlin do npm (serialização stubada) e rodei de verdade `ReadestramentoN33Test`, `CriadouroN33Test`, `CompanheiroN32Test` e `CompanheiroXpN33Test`: 34 de 34 passam. `app` (`CompanionActions`, `AppModel`, `PlayScreen`, strings) **não foi compilado** (sem Android SDK nem Gradle).
- ASSUMINDO: qualquer marca de adestramento do inventário serve ao readestramento, e a chance usa a receita dessa marca (o herói precisa saber fazê-la, senão a opção não aparece); a lealdade volta como um d20 novo, como no primeiro adestramento; o animal readestrado fica com o herói, sem pagar manutenção; não há tela de recusa específica no mestre (a narração fica com ele). Falta N-33.2e (testes finais da fase).

## 2026-10-25 — ai/app: criadouro no app, Visitar, Trazer e aviso de nível (N-33.2c, não compilado)

- **Cobrança ligada ao tempo (`ai`):** `KennelSettlement.after` calcula, com o estado já avançado, a prata devida pelas semanas viradas e monta o lote `pagamento + SettleKennel`. O descanso (`LocalActions.rest`) usa isso quando há companheiros: se uma semana vira, cobra do saldo do herói (prata e, se faltar, ouro com troco) e o chat diz quanto foi pago, quem ficou sem pagamento e quem foi perdido. `RestRules` ganhou `kennelBudget` e `kennelPay`.
- **App:** `CompanionActions.visit` (1d6 rolado pelo motor), `bring` (10 prata + 1 dia + liquidação se uma semana virar + troca, tudo em um lote atômico), `kennelBudget`/`payCommands` (prata primeiro, ouro no que faltar); `tame` passa o limite de animais (1 por grau de Adestrador). A lista do Companheiro esconde animais perdidos.
- **Painel Companheiro (`PlayScreen`):** animal guardado mostra só nome, nível, lealdade e os botões **Visitar** (se ainda não visitou hoje) e **Trazer para o herói** (preço na etiqueta, desabilitado em combate). Aviso de nível: `CombatActions.awardCombatVictoryXp` avisa ("X subiu para o nível N") e o aviso aparece no card até o jogador abri-lo.
- **Testes:** `KennelSettlementN33Test` (4) executado fora do Gradle e passando. `LocalActions`, `CompanionActions`, `AppModel`, `PlayScreen` e strings **não foram compilados** (sem Android SDK nem Gradle); o compilador Kotlin só checou `engine`, `rules` e `ai` e não apontou erro nos arquivos tocados.
- ASSUMINDO: sem detecção de cidade (a etiqueta diz "só numa cidade" e quem decide é o jogador, como no ferreiro NPC); a taxa de 10 prata é paga antes da cobrança semanal, que usa só o saldo restante; o aviso de nível é só de memória (some ao fechar o app); sem teste de interface. Falta N-33.2d (readestramento do animal perdido) e N-33.2e (testes finais).

## 2026-10-25 — engine: trazer e trocar o animal (N-33.2b, validado por execução parcial)

- `BringCompanion(animalId)` (`Kennel.kt`): traz o animal guardado para andar com o herói e guarda o que andava com ele (evento `CompanionBrought`; o que sai conta como visitado na semana atual). Recusa animal perdido, morto, que já anda com o herói, e qualquer troca em combate. Constantes `Kennel.BRING_FEE` (10 prata) e `Kennel.BRING_DAYS` (1 dia).
- O motor não conhece cidade nem prata: o app (N-33.2c) confere que o herói está na cidade e monta o lote `consumir 10 prata → AdvanceDays(1) → BringCompanion`.
- Testes: 3 novos em `CriadouroN33Test` (17 no total); `CriadouroN33Test` + `CompanheiroN32Test` = 24 de 24 executados fora do Gradle (compilador Kotlin do Gradle local, serialização stubada). `rules`, `ai` e `app` seguem sem compilar.
- ASSUMINDO: o animal trazido não paga manutenção a partir do momento em que chega; a semana de chegada do que sai conta como visitada. Falta N-33.2c (app) e N-33.2d (readestramento).

## 2026-10-25 — engine: criadouro, manutenção semanal e perda do animal (N-33.2a, validado por execução parcial)

- `Kennel.kt` (motor): semana de jogo = 7 dias de `GameState.day`; `GameState.kennelWeek` guarda a última semana liquidada (começa ao adestrar o primeiro animal). `CompanionState` ganhou `stabled` (no criadouro), `visitedWeek` e `lost` (perdido).
- **Só 1 animal anda com o herói:** adestrar um novo guarda o anterior no criadouro (`CompanionTamed.stabledIds`); `TameCompanion.maxAnimals` (1 por grau de Adestrador, quem chama informa) recusa além do limite, sem contar animais perdidos. `StableCompanion` guarda o animal ativo (recusado em combate) e a semana em que ele é guardado conta como visitada.
- **Cobrança (`SettleKennel(silver)`):** liquida todas as semanas viradas. Por semana: cuidador primeiro (10 prata × animais guardados), depois cada animal do menor nível para o maior (5 prata × nível), até acabar a prata. Sem pagamento: -30 de lealdade; pago e sem visita na semana: -10; pago e visitado: 0. O motor não mexe em prata: `Kennel.plan(state, saldo)` informa o total (`KennelWeekSettled.silverPaid`) e quem chama consome no mesmo lote.
- **Visita (`VisitCompanion(animalId, roll)`):** só animal guardado; 1d6 de lealdade (teto 100), 1 por dia (conta como carinho), marca a semana como visitada.
- **Perda:** lealdade 0 marca `lost`, remove a relação de aliado e para a cobrança; a entidade continua no estado para o readestramento (N-33.2d). Animal guardado ou perdido não age, não é aliado em combate (`allyIdsInCombat`) nem recebe comida, carinho ou truque.
- Teste `CriadouroN33Test` (14). **Validação:** sem Gradle (sem rede), compilei `engine` com o compilador Kotlin 2.0.21 do Gradle local, serialização stubada, e rodei de verdade `CriadouroN33Test` (14) e `CompanheiroN32Test`: 21 de 21 passam. A suíte JVM inteira do motor tem o mesmo conjunto de falhas do ZIP original (as de persistência/JSON, por causa dos stubs), ou seja, nenhuma regressão nova. Módulos `rules`, `ai` e `app` não foram tocados e seguem sem compilar.
- ASSUMINDO: cada cobrança é inteira (não paga pela metade); se o cuidador não é pago, os animais também ficam sem pagamento (a regra "até acabar a prata" parando na primeira que não cabe); a visita não evita o -30 do não pago; a semana de manutenção é cobrada inteira mesmo no primeiro ou último dia de guarda; o animal que sai do criadouro (N-33.2b) deve zerar `stabled` e marcar `visitedWeek`; a lealdade semanal do Criador (N-33.3) entra depois na mesma liquidação. Falta N-33.2b (trazer/trocar na cidade, 10 prata e 1 dia), N-33.2c (botão Visitar, aviso de nível e ligação do `SettleKennel` ao app) e N-33.2d (readestramento).

## 2026-10-25 — rules: origem das gemas por loja e extração (N-33.4, não compilado)

- **Loja (NPCs das cidades):** já funciona sem código novo. `shopView` lista todo item com preço e as 12 gemas têm preço (40, 120 e 360 de prata por qualidade 1, 2 e 3).
- **Extração por profissão:** 4 receitas `col-extrair-gema-*` em `receitas.txt` (sem entrada, saída uma gema bruta de qualidade 1, dificuldade 20, liberadas no degrau 3 e garantidas no degrau 5): Minerador → Ataque e Defesa; Geólogo → Velocidade e Vida. A qualidade sobe depois com o Lapidador. Teste `mineradorEGeologoExtraemGemasBrutas` em `GemasSlotsN33Test`.
- **Drop em combate e exploração: não feito.** O projeto não tem tabela de drop nem espólio (`LuckRules.dropPercentBonus` segue sem chamador), e inventar as tabelas seria decidir por você: quais NPCs/lugares dropam o quê, com que chance base e quem concede (motor ou mestre). Falta decidir.
- ASSUMINDO: a divisão Minerador (Ataque/Defesa) e Geólogo (Velocidade/Vida) é minha e ajustável; só existe gema bruta por extração (sem receita de qualidade 2 ou 3, que vêm da lapidação e da loja). Nada compilado nem executado.

## 2026-10-25 — app: painel de gemas na aba Jogar (N-33.4, não compilado)

- `GemsPanel` (`PlayScreen.kt`): card recolhível "Gemas" (some se não há gema no inventário nem engastada). Lista as peças equipadas com vagas ("Espada (Rara): 1 de 2 vagas"), botões **Remover** por gema, **Engastar** (uma por gema do inventário, enquanto houver vaga) e **Lapidar** (só para o Lapidador, enquanto a gema não está na qualidade 3). Sem Montador de Gemas, um interruptor liga o ferreiro NPC, que cobra 50 prata × qualidade (o preço aparece no botão). Tudo desabilitado durante combate, narração, rolagem ou teste pendente; o resultado da lapidação (d20) aparece no próprio card.
- `InventoryActions.gemsView()` e `AppModel.gemsView()` com `GemsView`, `GemSlotView` e `GemStockView`; 8 strings novas (`gems_*`).
- ASSUMINDO: o botão de engastar mostra "custo: 0 prata" quando o herói é o Montador; sem pré-visualização do bônus da gema na peça; o painel não aparece sem gemas (a origem das gemas ainda não existe, então hoje só entram por item dado pela campanha). Sem teste de interface. Nada compilado nem executado.

## 2026-10-25 — engine/rules/app: gemas de engaste e Joias com 5 degraus (N-33.4/N-25, não compilado)

- **Motor:** `Gems.kt` (`Gem`, `SocketedGems`, `Gems`), comandos `SocketGem` e `RemoveGem`, eventos `GemSocketed` e `GemRemoved`, `GameState.gems` (dono → slot → peça + gemas; dados antigos assumem vazio). Quatro tipos (Ataque, Defesa, Velocidade, PV máximo), qualidade 1 a 3, valores +2/+4/+6 e PV +10/+20/+30. A gema só conta enquanto a peça engastada continua equipada no slot; a gema de PV sobe e desce o máximo de PV no mesmo evento. `Gems.cut`: d20, 1 a 5 falha (25%); sucesso sobe 1 nível (teto 3), falha desce 1 e a de qualidade 1 some. Ferreiro NPC cobra 50 prata × qualidade (`Gems.npcFee`).
- **Regras:** `ItemQuality.gemSockets` (comum 1, rara 2, lendária 3) e 12 itens `gema-<tipo>-<qualidade>` em `itens.txt` (PV usa `gema-pv-N`). As 7 profissões de Joias, vidro e cerâmica (Especialista em Joias, Ourives, Lapidador, Montador de Gemas, Joalheiro de Campo, Vidreiro, Oleiro) passaram a 5 degraus (1, 7, 13, 21, 30). **Agora as 104 árvores de profissão têm 5 degraus**: o N-35 fecha a conversão por ondas.
- **App:** `InventoryActions.socketGem/removeGem/cutGem` (e `AppModel`): o Montador de Gemas do herói engasta e remove sem custo; com `viaNpc` o ferreiro NPC cobra; só o Lapidador do herói lapida (d20 registrado no log). O bônus entra na soma de combate de `CombatActions` junto de equipamento e marcas.
- **Testes:** `GemasN33Test` (engine), `GemasSlotsN33Test` (rules); `SkillTreeCatalogTest` inclui as 7 árvores em `FIVE_STEP_TREES` e a verificação das passivas agora olha árvores de 5 postos (não restam de 7).
- ASSUMINDO: peça normal e ótima = 1 vaga, rara e épica = 2, lendária = 3; a gema fica gravada por slot e item (volta a valer se a mesma peça for reequipada); engastar e remover recusam durante o combate; a lapidação só cobre a gema do inventário (sem custo em prata e sem limite de tentativas); o ferreiro NPC é escolhido pelo parâmetro `viaNpc`, sem detecção do lugar. **Sem tela** (nem engaste, nem lapidação), sem origem das gemas (drop, loja e mineração ainda não geram gemas) e sem receita de Joias. Nada compilado nem executado.

## 2026-10-25 — rules: Conhecimento e Vida cotidiana com 5 degraus (N-35, não compilado)

- As 17 profissões sem habilidade nova (Escriba, Bibliotecário, Professor, Astrônomo, Paleontólogo, Botânico, Heraldista, Genealogista, Arquivista, Escrivão Judicial, Investigador de Incêndios, Agricultor, Apicultor, Jardineiro, Lavadeiro, Barbeiro, Saboeiro) foram realinhadas dos 7 postos antigos para **5 degraus nos níveis 1, 7, 13, 21 e 30**. Seguem com a passiva genérica no escopo da profissão (valor 1 a 5); degrau 1 gratuito.
- Os degraus 1 a 4 mantêm os nomes antigos; o degrau 5 herda o nome único da antiga culminância (ex.: Escriba → "Registro Impecável"). Os postos 5 e 6 antigos ("Execução de Mestre", "Mestria Profissional") foram descartados.
- Teste: as 17 árvores entraram em `FIVE_STEP_TREES` (`SkillTreeCatalogTest`). Agora 97 das 104 árvores de profissão têm 5 degraus; as 7 restantes (Joias, vidro e cerâmica) aguardam as gemas (N-33.4).
- ASSUMINDO: nenhuma mecânica nova para essas famílias (decisão da §12); personagens salvos com os postos 6 e 7 antigos dessas profissões perdem o vínculo com esses ids. Nada compilado nem executado.

## 2026-10-25 — rules: Construção com 5 degraus (N-35, não compilado)

- As 7 profissões de Construção (Carpinteiro, Marceneiro, Pedreiro, Arquiteto, Construtor de Barcos, Engenheiro de Pontes, Construtor de Fortificações) saíram do molde antigo de 7 postos (níveis 1, 5, 10…) e passaram a **5 degraus nos níveis 1, 7, 13, 21 e 30**, como as outras 73 já convertidas. Passiva no escopo da própria profissão, valor 1 a 5; o degrau 1 é gratuito. Nomes próprios por degrau (ex.: Pedreiro: Olho para Alvenaria → Reforçar Muros → Levantar Parede → Argamassa Perfeita → Obra de Pedra). Efeito direto no acampamento: o grau (1 a 5) lido por `AppModel`/`RestRules` agora coincide com o último degrau aprendido, sem os postos 6 e 7 que eram ignorados.
- Teste: `SkillTreeCatalogTest` inclui as 7 árvores em `FIVE_STEP_TREES` (as contagens de culminâncias derivam dessa lista).
- ASSUMINDO: só a passiva genérica por degrau; sem receitas de construção (barricada, ponte, barco) nem itens novos, que ficam para o próximo módulo. Os nomes dos degraus são meus e ajustáveis. Restam 24 árvores no molde antigo: Joias (7, aguardam o código das gemas, N-33.4) e Conhecimento/Vida cotidiana (17, sem habilidade nova; falta só realinhar os níveis). Nada compilado nem executado.

## 2026-10-24 — ai/app: descanso parcial e acampamento (N-33.5, não compilado)

- Decisão do proprietário (24/10/2026): o descanso comum recupera **50%** de PV e PM; o acampamento soma **+10% por grau** de Construção (1 a 5; grau 5 = 100%); a estalagem/cidade recupera 100%.
- `RestRules` (`LocalActions.kt`) e `LocalAction.INN_REST` ("dormir na estalagem", "pousar"…); `PlayController` e `AppModel` ligam a regra (PV e PM parciais, PA e o resto continuam integrais). O grau vem do maior posto aprendido (limite 5) entre carpinteiro, marceneiro, pedreiro, arquiteto, construtor de barcos, engenheiro de pontes e construtor de fortificações. Sem `RestRules` o descanso segue integral (testes antigos intactos). Teste `AcampamentoN33Test`.
- ASSUMINDO: o percentual é do máximo (não do que falta); o jogador escolhe a estalagem pela frase, sem botão; o descanso integral na cidade não detecta o lugar sozinho. Nada compilado nem executado.

## 2026-10-24 — ai/app: utilidades fora de combate na ficha do mestre (N-35, não compilado)

- `PlayerSheet` ganhou o parâmetro `utilities`; a linha "Utilidades fora de combate (…informe o escopo em "skill"): Mãos Leves → fechaduras; …" entra nos níveis FULL e BRIEF (e, por herdar o BRIEF, no ROUND). `CombatBonuses.utilityHints` lista as passivas aprendidas cujo efeito não é de combate; `AppModel` liga as duas.
- ASSUMINDO: vale também para passivas de profissão com escopo de teste. Teste em `PlayerSheetTest`. Pendente do N-35: conversão das 103 profissões por ondas. Nada compilado nem executado.

## 2026-10-24 — rules: utilidades fora de combate nas classes (N-35, não compilado)

- `habilidades-arvore.txt`: o posto 6 de 48 das 50 classes (suporte/buff de combate) virou **passiva de utilidade** com bônus em um escopo de teste (ex.: Ladino "Mãos Leves" → `fechaduras`; Batedor "Rastreador" → `rastrear`; Mago "Estudo Arcano" → `arcano`; Pirata "Rota dos Mares" → `navegacao`). Escopos usados: fechaduras, rastrear, furtividade, persuasao, intimidar, vigiar, sobrevivencia, arcano, natureza, animais, navegacao, mecanismos, identificar, investigar, conhecimento, espiritos, meditacao, estrategia. Curandeiro e Médico de Campo mantêm a cura fora de combate (N-22).
- Valor +3 (+1 por nível da habilidade) em todas, escolha minha. A passiva entra pela mesma via das outras (`SkillTreeModifierProvider`), quando o teste pedido tem esse escopo como perícia.
- ASSUMINDO: o mestre (IA) precisa pedir o teste com a perícia pelo nome do escopo; ainda não há lista desses escopos no prompt do mestre nem no menu de testes (próximo passo). A escolha da utilidade por classe é minha e ajustável. Teste `UtilidadesClassesN35Test`. Nada compilado nem executado.

## 2026-10-24 — engine/rules/app: consertar e tempo de criar (N-35, não compilado)

- **Consertar:** falha gasta todo o material (o item quebrado em `manter_na_falha` continua); antes perdia metade. Criar e preparar seguem perdendo metade. Sem material suficiente nem rola o teste (já era assim). Teste de `OficioN20Test` ajustado; mensagem do app atualizada.
- **Criar:** `receitas.txt` ganhou a coluna 11 opcional `tempo` (0 a 30 dias, `RecipeDef.days`); `executeRecipe` avança o relógio (`AdvanceDays`) ao tentar. ASSUMINDO: valores de exemplo 2 dias (cota de malha) e 1 (espada longa), meus; o tempo passa mesmo na falha. Teste `OficioTempoN35Test`.
- Afanar: o mestre narra a consequência quando o ladrão é pego (já havia `caught` e relação −10); nada novo no código. Pendentes do N-35: utilidades fora de combate das classes e conversão das 103 profissões por ondas. Nada compilado nem executado.

## 2026-10-24 — engine/ai/app: Sorte nos testes e no drop (N-35, não compilado)

- `LuckRules` (motor): `testBonus` = +1 a cada 2 pontos de Sorte, sem teto; `dropPercentBonus` = +1% por ponto. `GameLoop` ganhou `luckTestBonus` (só jogador) e o rótulo do teste mostra " · Sorte +N"; `AppModel.luckTestBonusOf` soma Sorte de atributo e de itens equipados. Teste `SorteN35Test`.
- Degraus 1/7/13/21/30 já estavam em código (N-19, inclusive Ferreiro): nada a realinhar.
- ASSUMINDO: o bônus entra nos testes narrados pelo `GameLoop`, não no confronto de combate (que já usa Sorte no crítico); não há sistema de drop no código, então `dropPercentBonus` fica pronto sem chamador. Pendentes do N-35: utilidades fora de combate das classes, consertar/afanar/criar ajustes e conversão das 103 profissões por ondas. Nada compilado nem executado.

## 2026-10-24 — engine: defesa rolada com mínimo 1 (N-34, não compilado)

- `UnifiedCombat.DEFENSE_ROLL_MIN = 1`: o valor rolado da defesa nunca fica abaixo de 1, antes da fórmula do dano; `ClashResult.defenseTotal` segue a mesma regra. Teste `DefesaMinimaN34Test`.
- Multiplicador de chefe: `NpcProposal.bossPercent` (campo `boss_percent` da proposta da IA, 200 a 300) só vale para chefe e chefe-final; fora da faixa ou em outra categoria a proposta é recusada; vale no PV com pacote e no teto sem pacote (`ThreatCategory.scale/healthCap`). Teste `ChefeMultiplicadorN34Test`.
- Já estavam em código: teste de acerto, Sorte d10 (`CRIT_DIE_SIDES`) e Sorte de itens até 100% (N-15). ASSUMINDO: sem `boss_percent`, chefe segue ×2 e chefe-final ×3. Nada compilado nem executado.

## 2026-10-24 — engine/rules/app: marcas do Tatuador e nomes dos conjuntos por peso (N-36, não compilado)

- Motor: `Tattoo.kt` (`Tattoo`, `Tattoos`), comando `ApplyTattoo`, evento `TattooApplied` e `GameState.tattoos`. Limite de 1 marca por grau do Tatuador do herói (`profissao-tatuador-N`); bônus +1 por grau no status (Ataque, Defesa, Velocidade) e PV máximo +5 por grau; cobertura por marca de grau maior, que substitui a anterior (PV máximo sobe ou desce junto).
- App: `InventoryActions.tattooHero` (paga 100 prata × grau de quem tatua, troca atômica) e `AppModel.tattooHero`; o bônus das marcas entra na soma de combate de `CombatActions` junto do equipamento. Sem tela: só a ação.
- Regras: `ItemWeight.setNames` (Baluarte/Couraça/Titã, Patrulha/Malha/Andarilho, Véu/Manto/Sombra). O mapeamento das 50 classes já estava no catálogo (N-13) e não mudou.
- ASSUMINDO: quem não tem a profissão Tatuador pode receber 1 marca; o grau de quem tatua é informado por quem chama (jogador ou NPC); cobrir paga o preço integral da nova; marcas repetidas no mesmo status somam; os três nomes de cada peso ainda não viraram peças nem faixas de nível (o texto da decisão não diz como se distribuem), então nenhum item novo foi criado.
- Testes: `TatuadorN36Test` (7) e `ConjuntosPesoN36Test` (1) escritos. **Validação:** nada compilado nem executado (sem rede para o Gradle); contas dos testes feitas à mão.

## 2026-10-24 — docs: peso das classes e Tatuador (N-36, só documentação)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.13: nomes dos conjuntos por peso (Baluarte/Couraça/Titã, Patrulha/Malha/Andarilho, Véu/Manto/Sombra), mapeamento das 50 classes e distribuição de bônus confirmados; Tatuador com 1 marca por grau, +1 em status de combate por grau (PV +5), cobertura por marca de grau maior e preço de 100 prata × grau.
- Nada implementado, compilado nem executado.

## 2026-10-24 — docs: Sorte, degraus de profissão e mecânicas fora de combate (N-35, só documentação)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.12: Sorte dá +1 nos testes a cada 2 pontos e +1% de drop por ponto (itens contam também); degraus de profissão em 1, 7, 13, 21 e 30 (o 1 grátis, os outros 1 ponto); 3 a 5 habilidades por profissão; classes com utilidades fora de combate; consertar (falha gasta o material), afanar (mestre narra), criar (tempo por receita); conversão das 103 profissões por famílias e ondas.
- Nada implementado, compilado nem executado.

## 2026-10-24 — docs: confirmação dos valores em aberto (N-34, só documentação)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.11: XP de batalha e missão e desconto por inimigo fraco confirmados como estão; defesa rolada com mínimo 1; fluxo de ataque (acerto, crítico, dano) confirmado; d10 para Sorte e crítico; Sorte de itens até 100% com +3; qualidades de itens mantidas; PV por categoria de ameaça (chefe ×2 a ×3 a critério do mestre).
- Nada implementado, compilado nem executado.

## 2026-10-24 — docs: decisão sobre o histórico do Git (R-04, só documentação)

- `PLANO_RPG_SOLO.md`: o proprietário decidiu **reescrever o histórico** para remover o conteúdo de terceiros. A execução (`git filter-repo` e force push) continua pendente e é feita por ele; invalida clones e PRs existentes.

## 2026-10-24 — docs: origem das gemas e acampamento (N-33.4 e N-33.5, só documentação)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.9: gemas vêm de drop, compra em NPCs e extração por profissão. §15.10: acampamento montado em qualquer lugar, com +10% por grau de Construção (1 a 5) de PV e PM recuperados no descanso.
- O N-25 deixa de estar bloqueado; faltam os módulos de implementação.
- Nada implementado, compilado nem executado.

## 2026-10-24 — docs: gemas como itens de engaste (N-33.4, só documentação)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.9: 4 tipos de gema (Ataque, Defesa, Velocidade, PV), qualidade 1 a 3 (+2/+4/+6; PV +10/+20/+30), vagas por qualidade da peça (1/2/3), engaste removível, lapidar para subir 1 nível (d20, falha em 1 a 5 derruba 1 nível) e engaste pelo Montador de Gemas ou ferreiro NPC (50 prata × qualidade).
- Destrava o N-25 quanto às gemas; acampamento e origem das gemas seguem em aberto.
- Nada implementado, compilado nem executado.

## 2026-10-24 — docs: Criador de Animais e Treinador ligados ao companheiro (N-33.3, só documentação)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.8: Criador de Animais dá +1 de lealdade por semana por grau (1 a 5) a todos os animais do herói; Treinador dá +1 ponto de status por grau (1 a 5) só ao animal que anda com o herói, distribuído conforme o tipo de corpo.
- Nada implementado, compilado nem executado.

## 2026-10-24 — docs: criadouro, manutenção e perda do animal (N-33.2, só documentação)

- Novo `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.7 com as decisões do proprietário: XP do animal igual ao do herói, só se vivo, aviso só ao subir de nível; limite de 1 animal por grau de Adestrador; só 1 anda com o herói e os outros ficam no criadouro; manutenção semanal (5 prata × nível do animal + cuidador NPC a 10 prata × animais guardados); quedas de lealdade (-30 sem pagar, -10 sem visita); botão "Visitar" (+1d6); troca só na cidade (10 prata, 1 dia); lealdade 0 perde o animal, com readestramento (-30 pontos, uma tentativa extra, mantém nível, ataque e truques).
- Renumeração: o antigo N-33.2 (ligar Criador de Animais e Treinador ao companheiro) virou N-33.3; as gemas (N-25) viraram N-33.4.
- Nada implementado, compilado nem executado.

## 2026-10-24 — engine/app: XP do animal só por combate (N-33.1, não compilado)

- Motor: comando `GainCompanionXp(animalId, amount)` e evento `CompanionXpGained`. O nível do animal sai da mesma tabela do herói (`EngineConfig.levelFor`); cada nível ganho soma +5 de lealdade (teto 100) e 1 ponto de habilidade a cada 3 níveis (conta todos os níveis cruzados de uma vez). `GainXp` continua só para o jogador. `decideCompanion` passou a receber a configuração do motor.
- App: `CombatActions.awardCombatVictoryXp` dá XP a cada companheiro vivo que participou da vitória, calculado pelo nível do animal (mesma fórmula `battleXp` ou o valor fixo `combate_xp_vitoria`), antes do XP do herói; o animal não conta como inimigo derrotado. Não há outra fonte de XP do animal (nem missão, nem botão).
- ASSUMINDO: o animal ganha o mesmo XP que o herói ganharia no nível dele (diferença de nível do inimigo conta pelo nível do animal); precisa estar vivo e ter entrado na iniciativa; o comando manual `LevelUpCompanion` fica no motor, sem botão; sem mensagem de interface sobre o XP ou o nível ganho pelo animal.
- Testes: `CompanheiroXpN33Test` (4) escrito. **Validação:** nada compilado nem executado; contas dos testes feitas à mão.

## 2026-10-24 — app: painel "Companheiro" na aba Jogar (N-32.4, não compilado)

- `CompanionActions.kt` (novo) + `AppModel`: `CompanionView`/`TrickView`/`TameOption`; adestrar a partir da marca do Adestrador (item `*-adestrado`/`fera-adestrada`, consumido; lealdade inicial = d20 do motor), escolha única do ataque entre as 3 opções do corpo, +1 de status (Ataque, Resistência, Velocidade, Destreza; 3 pontos iniciais), subir o ataque com ponto de habilidade, aprender truque (d20, 1 por descanso), ordenar truque (d100), alimentar (comida do inventário aceita pelo corpo; preferida dobra; grau do Adestrador pela árvore `profissao-adestrador-N`), carinho e ataque do animal no turno dele contra inimigos do combate. Tudo passa por `act` (trava de turno).
- `PlayScreen.CompanionPanel` (card recolhível "Companheiro (N)", só aparece com animal ou marca de adestramento) e 13 strings; em combate só aparecem Ordenar (truques de combate) e Atacar.
- Motor: `GameEngine.rollPlain` e `CampaignSession.rollPlain` (rolagem 1dN registrada no log) para o d20 e o d100 do animal.
- ASSUMINDO: a captura já foi resolvida pela receita (o painel só rola a lealdade inicial); PV inicial por porte 10/20/30/45/60 (valores meus, ajustáveis); sem limite de animais por dono; sem botão de subir de nível do animal (`LevelUpCompanion` fica sem gatilho de interface até haver regra de XP do animal); o animal não escolhe alvo sozinho (o jogador toca no inimigo); efeitos dos truques (Proteger, Distrair, Resgatar etc.) seguem narrativos: o painel só registra a obediência; sem tela separada para a escolha do ataque (aparece no próprio card logo após adestrar).
- **Validação:** nada compilado nem executado; sem teste de interface; conferência estática de imports e assinaturas por leitura.

## 2026-10-24 — engine: ataque, truques, nível e turno do animal (N-32.3, não compilado)

- `Companion.kt`: `CompanionState` ganhou `attackId`, `attackLevel`, `tricks` e `lastLearnDay` (padrões; dados antigos seguem válidos). `Companions`: `attackDamage` (Ataque × fator, para cima), `effectChance` (base + 5% por nível, teto 90%), `d20Success` (20 natural passa, 1 natural falha), `trickD20Percent` (+3 por nível de adestramento), `orderChance` (d20 + (100 − d20) × lealdade ÷ 100; 100 = 100%) e `learnSuccess` (d20 + lealdade ÷ 10 + 3 por nível).
- Comandos e eventos: `ChooseCompanionAttack` (uma vez), `RaiseCompanionAttack` (gasta 1 ponto de habilidade do animal), `LevelUpCompanion` (nível +1, lealdade +5 com teto 100, 1 ponto de habilidade a cada 3 níveis), `LearnCompanionTrick` (1 tentativa por descanso, exige lealdade mínima, d20 rolado por quem chama) e `OrderCompanionTrick` (d100 rolado por quem chama; em combate só truque de combate); eventos `CompanionAttackChosen`, `CompanionAttackRaised`, `CompanionLeveledUp`, `CompanionTrickLearnAttempted`, `CompanionTrickOrdered`. Ramos novos em `decide` e `apply`.
- `GameEngine.companionStrike`: no turno do animal aplica o dano (`Damage`), a condição se o d100 do efeito for ≤ à chance (`AddCondition`) e passa o turno (`NextTurn`); recusa fora do turno, sem combate e contra dono ou aliados.
- ASSUMINDO: o ataque do animal sempre acerta (sem teste de acerto, defesa nem Resistência do alvo); `attackId`, dano, chance, dificuldade e lealdade mínima vêm do pacote (N-32.2) por quem chama, e o motor só guarda; o aprender soma também o bônus de adestramento; ordem de truque de combate não gasta o turno do dono (a ordem só registra o resultado; o efeito do truque, como Proteger e Distrair, fica para o N-32.4/mestre); sem escolha automática do alvo pelo animal.
- Testes: `CompanheiroCombateN32Test` (6) escrito. **Validação:** nada compilado nem executado; conferência estática: ramos novos nos `when` exaustivos de `Command` (`Commands.kt`) e `Event` (`Events.kt`), contas dos testes feitas à mão.

## 2026-10-24 — rules: pacote do companheiro, `companheiros.txt` (N-32.2, não compilado)

- Novo arquivo opcional `companheiros.txt` (`PackageFiles.COMPANIONS`; `PackageData.companions`: `CompanionData`; herança por id; `RulePackage.companions`, `companionAttacks`, `companionAttack`, `companionTrick`, `companionFood`). Linhas por tipo: `corpo`, `ataque` (dano leve/normal/alto = ×0,8/×1/×1,3 do Ataque do animal; efeito `-`, `condicao:<id>`, `elemento:<id>` ou `distancia`; chance d100 base só em condição), `truque` (dificuldade d20 1–20, lealdade mínima 0–100, combate sim/nao) e `comida` (ganho base, preferida sim/nao). Parser recusa: corpo fora de `Companions.BODY_TYPES`, corpo sem exatamente 3 ataques, condição, item ou corpo inexistente, dano/dificuldade/ganho fora da faixa e ids duplicados.
- Sistema próprio: 6 tipos de corpo, 18 ataques, 12 truques (simples 5/10, médio 8/25, difícil 11/40, mestre 14/60; 3 de combate) e 27 comidas por tipo; 13 itens narrativos novos em `itens.txt` (`refeicao-preparada`, `refeicao-de-carne`, `ovo`, `fruta`, `fruta-rara`, `mel`, `doce`, `iguaria`, `nectar`, `cogumelo-raro`, `presa-viva`, `presa-rara`, `peixe-raro`). `BundledContent` passou a ler `companheiros.txt`.
- ASSUMINDO: só dados e leitura (sem cálculo de ataque, chance de efeito, truque nem turno: N-32.3); "preferida" = comida "boa" do tipo (dobra o ganho no motor); "peixe raro" e "fruta rara" viraram itens próprios (`peixe-raro`, `fruta-rara`) em vez de qualidade; sem comida odiada (o animal aceita só o que o tipo lista); ataques com efeito `elemento` e `distancia` ficam sem chance (valem sempre).
- Testes: `CompanheiroPacoteN32Test` (5) escrito. **Validação:** nada compilado nem executado (sem Gradle nem rede); conferência estática: 149 itens com ids únicos, 18 ataques (3 por corpo), 12 truques, comidas apontando para itens existentes.

## 2026-10-24 — engine: companheiro adestrado, estado e lealdade (N-32.1, não compilado)

- `Companion.kt` (novo): `Companions` (tipos de corpo, teto de lealdade 100, fatores por nível do animal 100/75/50/25% e por grau do Adestrador 100% a 200%, `loyaltyGain` com um só arredondamento para cima e mínimo 1), `CompanionState` (entidade, dono, tipo de corpo, lealdade, nível de adestramento, dia do último carinho), comandos `TameCompanion` (lealdade inicial = d20 rolado pelo chamador, 1..20), `FeedCompanion` (ganho base da comida + grau do Adestrador) e `PetCompanion` (+3 base, uma vez por descanso/dia), eventos `CompanionTamed` e `CompanionLoyaltyChanged`. `GameState.companions` (padrão vazio, dados antigos seguem válidos); ramos novos em `decide` (`Commands.kt`) e `apply` (`Events.kt`).
- O animal é uma entidade NPC comum; adestrar define a relação animal→dono em pelo menos 10 (`ALLY_RELATION_MIN`), então `allyIdsInCombat` já o reconhece como aliado.
- ASSUMINDO: o chamador rola o d20 e informa o ganho base da comida (qualidade e dobro da preferida ficam para o módulo do pacote); sem limite de animais por dono neste módulo; sem perda de lealdade; subir de nível do animal (+5 de lealdade), ataque, truques, turno e painel ficam para os próximos módulos.
- Testes: `CompanheiroN32Test` (7) escrito. **Validação:** nada compilado nem executado (sem Gradle nem rede para baixá-lo); conferência estática: ramos novos em todos os `when` exaustivos de `Command` e `Event` (só `Commands.kt` e `Events.kt`; `AutomaticTimeline` tem `else`) e contas dos testes feitas à mão.

## 2026-10-24 — docs: ficha do animal adestrado, decisões do companheiro (N-32, só documentação)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` §15.6 e `PLANO_RPG_SOLO.md` (Andamento da seção 3.10): registram a ficha própria do animal (3 pontos de status em Ataque, Resistência, Velocidade e Destreza; 1 ponto de habilidade a cada 3 níveis; uma só habilidade de ataque; 6 tipos de corpo com 3 opções de ataque cada; o jogador escolhe 1 como único ataque e os pontos de habilidade sobem o nível dela), truques em d20 (+15% por nível de adestramento) e lealdade (d20 automático ao adestrar, 100% = 100% de chance, mais difícil em níveis altos, sobe com comida, carinho e nível) e o nível do Adestrador (1 a 5) que multiplica o ganho de lealdade (×1 a ×2) e dá +3 no d20 da captura por nível.
- Também: painel "Companheiro" na aba Jogar (status, ataque, adestramento, truques, alimentar e carinho); comidas por tipo de corpo (comum +2, boa +5, rara +10; preferida dobra; carinho +3 por descanso); efeitos dos 18 ataques por tipo de corpo (dano leve/normal/alto ×0,8/×1/×1,3; chance de efeito d100 base + 5% por nível da habilidade, teto 90%; condições já existentes); 12 truques sugeridos em 4 níveis de dificuldade e a regra de combate: o animal tem o turno dele e segue a lógica de NPC companheiro (aliado, relação ≥ 10, `allyIdsInCombat`).
- ASSUMINDO: numerado N-32; a ordem de truque de combate não gasta o turno do dono e o animal vira entidade NPC; os 6 tipos de corpo (quadrúpedes, alados, rastejantes, aquáticos, humanoides, insetos e aracnídeos) e as opções de ataque vieram de proposta aceita; valores marcados com 🔶 são propostas aceitas para ajuste. Sem código novo.
- **Validação:** nada compilado nem executado (só documentação).

## 2026-10-24 — rules: nova profissão Adestrador, 5 degraus por porte do animal (N-31, não compilado)

- `habilidades-arvore.txt`: árvore `adestrador` (104ª profissão) com 5 degraus (1, 7, 13, 21, 30; degrau 1 grátis), todos passivos (+N na perícia `adestrador`): pequenos animais, médios, grandes, feras e bestas, criaturas colossais. `personagem-catalogo.json`: entrada editorial `adestrador` (escopo `adestramento`, +1).
- `receitas.txt`: 5 receitas `ade-*` sem entrada, cada uma liberada pelo degrau do seu porte (dificuldade 5, 10, 15, 25 e 35); sem falha garantida a partir do degrau 3, 4 e 5 nos três primeiros portes; feras e colossais sempre arriscam. `itens.txt`: 5 itens narrativos de resultado (`animal-pequeno-adestrado`, `animal-medio-adestrado`, `animal-grande-adestrado`, `fera-adestrada`, `criatura-colossal-adestrada`).
- Testes: `SkillTreeCatalogTest` ajustado (lista de árvores de 5 degraus e total de 104 profissões); sem testes novos.
- ASSUMINDO: o item é a marca do sucesso e o mestre narra o que o animal faz (sem entidade de companheiro, que segue pendente); sem limite por cena; o Treinador continua como profissão de ensino em geral; README e PLANO passam a contar 104 profissões.
- **Validação:** nada compilado nem executado; conferência estática: catálogo com 1368 habilidades de 20 colunas e ids únicos, JSON válido com 104 profissões, receitas apontando para itens e degraus existentes.

## 2026-10-24 — rules: montaria e ave em combate (pendência do N-24, não compilado)

- `habilidades-arvore.txt`: degrau 3 (nível 13) do Tratador de Montarias virou ativo (BUFF em quem usa, 3 PM, 4 rodadas: velocidade +2 e defesa +1, +1 por nível da habilidade) e o do Tratador de Aves Mensageiras também (percepção +2, +1 por nível). Sem código novo.
- ASSUMINDO: montaria e ave são bônus temporários de quem usa, sem entidade própria e sem estado de "montado"; o degrau 3 perde o bônus passivo de +3 na perícia (fica o +2 do degrau 2 entre os níveis 13 e 20); Criador de Animais e Treinador (companheiro) e Tatuador seguem pendentes.
- **Validação:** nada compilado nem executado; sem testes novos, por orientação do proprietário. `SkillTreeCatalogTest` e `OficioN24Test` não dependem do degrau 3 ser passivo (conferido por leitura).

## 2026-10-24 — engine/rules: Inspirar, buff pequeno em aliados (pendência do N-24, não compilado)

- Motor: novo destinatário `SkillEffectRecipient.ALLIES`, comando `ApplyAllySkillEffects` e evento `AllySkillEffectsApplied` (aplica o mesmo efeito temporário a todos os aliados de uma vez; reaplicar troca o efeito anterior da mesma habilidade). `GameState.allyIdsInCombat` define aliado: NPC vivo e de pé na iniciativa cuja relação com o personagem é ≥ `ALLY_RELATION_MIN` (10, simpatia). Recusa fora de combate, fora do turno de quem usa, sem aliado elegível ou com efeito não positivo.
- Catálogo: o parser aceita `ALLIES` só com ação `MODIFIER`, custo positivo, efeitos de combate positivos, sem queda por nível e com duração. Degrau 3 de Artista, Músico, Ator e Dançarino (nível 13) virou ativo (BUFF, 3 PM, 3 rodadas, +1 e +1 por nível da habilidade): precisão, ataque, defesa e velocidade nos aliados.
- App: `SkillTreeActions.activate` valida o aliado antes de gastar PM e envia o efeito; a tela não mudou (sem seletor de alvo, o botão Ativar já serve).
- ASSUMINDO: o usuário não recebe o efeito (só aliados); o motor não tem lados, então o mestre cria o aliado mudando a relação do NPC com o personagem; só em combate; o degrau 3 deixa de dar o bônus passivo +3 na perícia (fica o +2 do degrau 2 entre os níveis 13 e 20; degraus 4 e 5 seguem passivos); Tatuador, montaria e companheiro continuam pendentes.
- **Validação:** nada compilado nem executado; sem testes novos, por orientação do proprietário.

## 2026-10-24 — rules: bônus base por faixa de nível, coluna `escala` (N-12, não compilado)

- `itens.txt` coluna 15 opcional `escala` (`ItemDef.scaleScope`): o item informa o escopo que escala e o nível mínimo define o bônus de qualidade **normal** = ⌈nível ÷ 10⌉ (×5 em `max_pv`); `RulePackage.levelScaleBase` e `equipmentBonus` somam esse valor depois de aplicar a qualidade (ótima 125%, rara 150%, épica 200%, lendária 250%) e a metade da penalidade de peso/tipo. Documentado no cabeçalho de `itens.txt` e em `FORMATO_PACOTE.md`.
- Tabela decidida: peito → PV máximo; elmo → Sorte; luvas → Destreza; botas → Agilidade; calça → defesa; arma → ataque (cajado: Inteligência). Nível 1/10/20/30 = bônus 1/1/2/3 (peito 5/5/10/15 PV).
- ASSUMINDO: itens atuais não mudam (todos declaram efeito e a escala não vale quando o escopo já tem efeito declarado); a escala vale só para itens novos que usem a coluna; encantamentos continuam somando por fora, acima do normal; o arredondamento da qualidade segue o código atual (`scaleByQuality` arredonda ao mais próximo, não para cima), sem alteração.
- **Validação:** nada compilado nem executado; `EscalaNivelTest` (3) escrito.

## 2026-10-24 — rules: rumores, contratos, desarmar e reprogramar por receita (pendência do N-23, não compilado)

- `receitas.txt`: 5 receitas `com-*` (rumores: Estalajadeiro e Taverneiro; contrato: Comerciante, Advogado e Escolta Profissional) e 2 `mec-*` (desarmar engenhoca: Mecânico; reprogramar artefato: Mecânico de Artefatos), sem entrada, liberadas no degrau 2 e sem falha no degrau 4. `itens.txt`: `rumor-ouvido`, `contrato-fechado`, `engenhoca-desarmada`, `artefato-reprogramado` (narrativos). Sem código novo.
- ASSUMINDO: o item é a marca do resultado e o mestre decide o conteúdo (rumor verdadeiro ou falso, termos do contrato, o que o artefato passa a fazer); rumores e contratos não mexem em preço nem em relação de NPC pelo motor; sem limite por cena; outras profissões de Comércio seguem narrativas.
- **Validação:** nada compilado nem executado; `ComercioMecanismosN23Test` (1) escrito; conferência estática: 94 receitas, ids únicos, itens e degraus existentes.

## 2026-10-24 — rules: ações de Furtividade por receita (pendência do N-22, não compilado)

- `receitas.txt`: 4 receitas `fur-*` sem entrada (rastrear: Caçador de Recompensas; colher pistas: Detetive; desarmar armadilha: Perito em Armadilhas; abrir fechadura: Espião), liberadas no degrau 2 e sem falha no degrau 4. `itens.txt`: 4 itens narrativos de resultado (`rastro-seguido`, `pista-colhida`, `armadilha-desarmada`, `fechadura-aberta`). Sem código novo.
- ASSUMINDO: o item é a marca do sucesso na ficha; o mestre vê o item e narra a consequência (o motor não abre portas nem revela segredos sozinho); sem limite por cena; Mensageiro e Carcereiro seguem sem receita; o item pode ficar acumulado no inventário.
- **Validação:** nada compilado nem executado; `FurtividadeN22Test` (1) escrito; conferência estática: 87 receitas, ids únicos, itens e degraus existentes.

## 2026-10-24 — ai/app: gatilho garantido da oportunidade de ofício (não compilado)

- `ContextInput.craftOpportunity` (via `PlayInput` e `PlayController.craftOpportunity`, ligado no `AppModel` quando o pacote ativo tem receitas): enquanto o fato `oficio-oferecido` não existe em `GameState.facts`, fora de combate e sem teste pendente, o contexto traz um lembrete da regra 18; o mestre oferece a chance na narração e grava o fato com `record_fact`, e o lembrete some. `CRAFT_OFFER_FACT` em `Master.kt`; regra 18 do prompt cita o registro.
- ASSUMINDO: o lembrete é pedido, não obrigação: se a cena não couber, o mestre adia e o lembrete volta na jogada seguinte; se o mestre oferecer e não gravar o fato, o lembrete continua; campanhas já salvas sem o fato recebem o lembrete uma vez; sem código no motor.
- **Validação:** nada compilado nem executado; `ReferenceScenesTest` ganhou 1 teste (lembrete liga, some com o fato e não aparece sem a opção).

## 2026-10-24 — app: tela de receitas de ofício na aba Jogar (não compilado)

- `PlayScreen`: `RecipesPanel` (card recolhível "Ofícios e receitas (N)") lista as receitas liberadas ao herói (`AppModel.recipesView`, com nomes de item do pacote) e o botão "Fazer" chama `AppModel.craftRecipe` via `act` (trava de turno). Cobre preparos, forja, consertos, identificação, coleta (`col-*`), pesca/mergulho (`mar-*`) e apresentações (`art-*`). `RecipeView` e 4 strings novas.
- ASSUMINDO: o resultado (sucesso, falha ou recusa, inclusive em combate) aparece como texto no próprio card; sem prévia de chance nem seletor de qualidade; o painel só aparece com pelo menos uma receita liberada e fica habilitado só com a tela livre; sem lista de materiais do inventário no card.
- **Validação:** nada compilado nem executado; sem teste de interface.

## 2026-10-24 — ai: mestre oferece coleta, pesca e ofícios sem o jogador pedir (não compilado)

- `MasterPrompt.SYSTEM`: regra 18, "Oportunidades de ofício": ao menos uma vez por campanha, quando a cena permitir, o mestre mostra na narração uma chance natural de coletar, caçar, pescar, mergulhar ou se apresentar, sem o jogador pedir, e deixa a escolha com ele. Não repete se as cenas recentes já mostraram uma; não resolve a coleta nem concede itens (isso é do motor via receitas `col-*`, `mar-*` e `art-*`); não cita receitas, degraus nem números.
- ASSUMINDO: só instrução de prompt (sem contador no motor): "uma vez" depende do mestre ver as cenas recentes e a linha do tempo, então pode repetir ou atrasar; um gatilho garantido por código exigiria registrar o aviso no estado e fica fora. `ReferenceScenesTest` ajustado para exigir a regra no prompt.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — rules: pesca, mergulho e apresentações (pendência do N-24, não compilado)

- `receitas.txt`: 5 receitas `mar-*` (Pescador, Pescador de Profundidade, Mergulhador, Navegador, Marinheiro de Carga) e 4 `art-apresentar-*` (Artista, Músico, Ator, Dançarino), tipo `criar` sem entrada, liberadas pelo degrau 2 e sem falha no degrau 4. Artes rendem 5 de prata (`moeda:5`). `itens.txt`: `peixe-fresco`, `alga-marinha`, `perola-bruta`. Sem código novo.
- ASSUMINDO: dificuldade 10–20; sem limite por cena ou lugar (o mestre decide); Tatuador não tem receita. **Seguem pendentes:** montaria e companheiro (Animais) e inspirar (Artes), por exigirem entidade/efeito novo no motor.
- **Validação:** nada compilado nem executado; `MarArtesN24Test` (1) escrito.

## 2026-10-24 — rules: coleta de materiais da Exploração (pendência do N-23, não compilado)

- `receitas.txt`: 9 receitas `col-*` (tipo `criar`, sem entrada), uma por profissão de Exploração (Caçador, Guarda-Florestal, Guia de Floresta/Montanha/Deserto, Minerador, Geólogo, Observador do Clima, Cartógrafo de Campo). Liberadas pelo degrau 2 e sem falha no degrau 4; entregam materiais já existentes em `itens.txt` (carne, couro cru, madeira, fibra, ervas, minério, cristal, cogumelo, pedras de terra e vento). Usa `executeRecipe`/`craftRecipe` existentes; sem código novo (o parser e o motor já aceitam `criar` sem entrada).
- ASSUMINDO: dificuldade 10–20; falha não perde nada (não há entrada); sem limite por cena, tempo ou lugar (o mestre decide quando permitir); Pescador e Mar seguem para as ações do N-24; sem tela de receitas.
- **Validação:** nada compilado nem executado; `ColetaN23Test` (1) escrito.

## 2026-10-24 — rules: cura das classes Curandeiro e Médico de Campo (pendência do N-22, não compilado)

- `habilidades-arvore.txt`: o posto 6 (Amparo Avançado) de `curandeiro` e de `medico-de-campo` passa a `OUT_OF_COMBAT` com efeito `cura` (Curandeiro: custo 6 PM, cura 10 + 2/nível; Médico de Campo: custo 5 PM, cura 8 + 2/nível). Usa o fluxo existente de `SkillTreeActions.activate` (cura o herói ou um aliado vivo, só fora de combate); sem código novo.
- ASSUMINDO: valores de custo e cura escolhidos entre os degraus 2 e 4 de Cuidados (N-22); o bônus de combate `resistencia_dano` do posto 6 dessas duas classes deixa de existir (postos 3, 4 e 7 mantêm); posto 7 (culminância) e demais classes inalterados; sem tela nova.
- **Validação:** nada compilado nem executado; `CuraClassesN22Test` (1) escrito.

## 2026-10-24 — Fase 20 concluída em código: revisão geral (N-28, não compilada)

- **Suíte completa:** não executada. `./gradlew --offline :engine:test :rules:test :ai:test` falhou ao baixar o Gradle (sem rede), então nada compilou nem rodou. Testes novos da Fase 19 e 20 seguem não executados: `DegrausN19Test` 2, `OficioN20Test` 13, `OficioN21Test` 6, `OficioN22Test` 5, `OficioN23Test` 5, `OficioN24Test` 2, `CuraPercentualN26Test` 4, `AmeacaN27Test` 4.
- **Conferência estática (script local):** 1363 habilidades com 20 colunas, ids e nomes únicos e pré-requisitos válidos; todas as receitas apontam para itens e degraus existentes; todo `item_<id>_percentual` aponta para item existente; arquivos de dados terminam como antes. Ajuste encontrado e feito no N-24: `SkillTreeCatalogTest` (culminância ofensiva) e `CriacaoDeFichaENpcTest` (teto de PV do N-27). Nenhuma regressão achada por leitura.
- ASSUMINDO: o N-25 (Joias e Construção) segue ⛔ bloqueado por gemas e acampamento e fica fora da Fase 20; fórmulas e testes antigos que dependam dos valores anteriores só serão confirmados no CI.
- **Pendente fora do ambiente:** `./gradlew test :app:assembleDebug` e correção do que falhar (inclui ktlint).

## 2026-10-24 — rules/ai: categorias de ameaça e teto de PV sem pacote (N-27, não compilado)

- `ThreatCategory` (capanga ×0,5, comum ×1, elite ×1,5, chefe ×2, chefe final ×3, arredondando para cima, mínimo 1); `NpcProposal.threat`/`level`; `NpcFactory.create` com pacote multiplica o PV (recurso de morte do manifesto) da ficha derivada por 5 × Resistência. `request_npc` da IA aceita `threat` e `level` (1–99; padrão comum e nível 1).
- Sem pacote: recurso negativo é recusado (antes `Resource` lançava exceção na construção) e o `pv` é recortado a 5 × (nível + 2) × multiplicador, com aviso.
- ASSUMINDO: sem pacote o recurso limitado é o de id `pv`; o teto não vale para NPCs já salvos; PM e PA não são escalados pela categoria; sem escala automática por nível do jogador (a IA informa `level`); `CriacaoDeFichaENpcTest` ajustado (nível 5 para o NPC de 22 PV).
- **Validação:** nada compilado nem executado; `AmeacaN27Test` (4) escrito.

## 2026-10-24 — rules/app: itens de cura em percentual (N-26, não compilado)

- `criacao.txt`: propriedade opcional `item_<id>_percentual` (1–100, validada) ao lado de `quantidade`; `RulePackage.healAmount` cura o maior entre o valor fixo e esse % do máximo do recurso (arredondado para cima). `InventoryActions.useItem` usa `healAmount`. Valores: bandagem 10%, poções de cura 20/30/50/100% (menor, comum, maior, suprema), pergaminhos de cura 40/70%; PM e PA seguem fixos.
- ASSUMINDO: o percentual também escala pela qualidade do item (limitado a 100); a cura de `OUT_OF_COMBAT` das profissões e a cura de poderes não mudam; sem efeito para pacotes importados que não declaram `percentual`.
- **Validação:** nada compilado nem executado; `CuraPercentualN26Test` (4) escrito; documentado em `FORMATO_PACOTE.md`.

## 2026-10-24 — rules: Onda 3, Mar, Animais, Artes e Identificação (N-24, não compilado)

- `habilidades-arvore.txt`: 21 profissões (5 de Mar e rios, 4 de Animais e montarias, 5 de Artes e performance, 7 de Identificação e relíquias) com 5 degraus (1, 7, 13, 21, 30; degrau 1 gratuito; posições de pergaminho 1–5), todas passivas (+N na perícia do ofício).
- `receitas.txt`: 7 receitas `ide-*` (identificar relíquia, reconhecer falsificação, decifrar mapa por tradução e por criptoanálise, examinar achado, restaurar e reparar relíquia) liberadas por degrau; `itens.txt`: 7 itens narrativos de relíquias e mapas.
- ASSUMINDO: Mar, Animais e Artes ficam só com os degraus passivos (pescar/coletar, mergulho, montaria e companheiro, apresentação que rende prata, inspirar e tatuagem não têm ação nem receita); as receitas de identificação mantêm o item na falha; relíquia desconhecida, falsa e quebrada são concedidas pelo mestre; sem tela.
- **Validação:** nada compilado nem executado; `OficioN24Test` (2) escrito; `SkillTreeCatalogTest` ajustado (72 árvores com 5 degraus).

## 2026-10-24 — rules/ai/app: Onda 2, Exploração, Comércio e Mecanismos (N-23, não compilado)

- `habilidades-arvore.txt`: 23 profissões (9 de Exploração, 10 de Comércio e diplomacia, 4 de Mecanismos) com 5 degraus (1, 7, 13, 21, 30; degrau 1 gratuito; posições de pergaminho 1–5). Caçador, Guarda-Florestal e Escolta Profissional mantêm as habilidades de combate antigas dos postos 1, 3, 4, 5 e 7; as demais são passivas (+N na perícia do ofício).
- `RulePackage`: `hazardReductionPercent` (maior degrau de Exploração: 3 = 10%, 4 = 20%, 5 = 30%), `hazardDamage(…, reducePercent)` (mínimo 1) usado por `CheckMenu.rollDamage`; `tradePercent` (Comerciante e Contador: degraus 2 a 5 = 5/10/15/20%), `discountedPrice` e `boostedSale` usados em `InventoryActions.trade` (desconto na compra, bônus na venda).
- `receitas.txt`: 7 receitas `mec-*` (engrenagens, molas, consertar engenhoca, armadilha de mola, engenhoca de fumaça, relógio de bolso, restaurar artefato) liberadas por degrau; `itens.txt`: 8 itens de Mecanismos.
- ASSUMINDO: degraus 2–5 de Exploração e Comércio são passivos; só Comerciante e Contador mexem em preço (as demais de Comércio seguem narrativas); o bônus de perigo vale só para perigos por perfil (`hazard`), não para dano direto; **coleta de materiais (ervas, minério, caça) fica para depois** (sem ação nem receita de coleta); desarmar e reprogramar (Mecanismos) e rumores/contratos (Comércio) não têm ação; itens de Mecanismos são narrativos; sem tela.
- **Validação:** nada compilado nem executado; `OficioN23Test` (5) escrito; `SkillTreeCatalogTest` ajustado (51 árvores com 5 degraus; a culminância ofensiva agora é checada em qualquer degrau de profissão).

## 2026-10-24 — rules/app: Onda 1b, Preparos, Cuidados e Furtividade (N-22, não compilado)

- `habilidades-arvore.txt`: 16 profissões (7 de Preparos, 3 de Cuidados, 6 de Furtividade e investigação) com 5 degraus (1, 7, 13, 21, 30). Nas de Cuidados os degraus 1, 2, 4 e 5 são `OUT_OF_COMBAT` com efeito `cura` (custo em PM, cura crescente; o degrau 1 é gratuito); nas 4 de combate (Toxinas, Caçador de Recompensas, Espião, Perito em Armadilhas) ficaram as habilidades de combate antigas dos postos 1, 3, 4, 5 e 7.
- O carregador exige que `OUT_OF_COMBAT` declare custo e recurso e só aceite efeito `cura`. App: `SkillTreeActions.activate` cura o herói ou um aliado vivo fora de combate (gasta PM, cura `valueAt(nível)` no recurso de saúde do pacote).
- `receitas.txt`: 17 receitas de preparo liberadas por degrau; `itens.txt`: 10 itens de material e preparo. Afanar: `RulePackage.canPilfer` (degrau 1 de Ladino, Assassino, Agente das Sombras ou Espião) e `InventoryActions/AppModel.pilfer` sobre `attemptPilfer`.
- ASSUMINDO: refeições, cerveja, perfume e veneno de lâmina são itens narrativos (sem buff nem condição até haver ações em `criacao.txt`); Furtividade sem receitas (rastrear, pistas, desarmar armadilha e abrir fechaduras ficam para depois); afanar usa Destreza e Agilidade base da ficha; **cura das classes Curandeiro e Médico de Campo não foi feita** (as cadeias de classe seguem de combate; só as profissões de Cuidados curam); sem tela.
- **Validação:** nada compilado nem executado; `OficioN22Test` (5) escrito; `SkillTreeCatalogTest` ajustado (28 árvores com 5 degraus).

## 2026-10-24 — rules/app: Onda 1a, Forja e armas e Vestuário e couro (N-21, não compilado)

- `habilidades-arvore.txt`: as 12 profissões (Ferreiro, Armeiro, Armeiro de Armaduras, Perito em Arcos, Fabricante de Arcos e de Flechas, Alfaiate, Tecelão, Curtidor, Sapateiro, Cordoeiro, Preparador de Peles) passam de 7 para 5 degraus (níveis 1, 7, 13, 21, 30; degrau 1 gratuito; posições de pergaminho 1–5).
- `receitas.txt`: colunas `habilidade` (degrau que libera) e `garantida_por` (degrau sem falha); 28 receitas novas; o parser recusa receita que cria equipamento acima do limite de nível do degrau (1/5/10/20/30, `CRAFT_TIER_LEVEL_CAP`). `itens.txt`: materiais (`fibra-vegetal`, `tecido`, `corda`, `flecha`) e 7 peças quebradas.
- `RulePackage`: `recipeBlock`, `craftTierBonus` (+5% por degrau aprendido além do primeiro), `availableRecipes`, `smithEnchantBlock`, `enchantMaterial`. App: `craftRecipe`, `availableRecipes` e `smithEnchantItem` (Ferreiro degrau 5 encanta peito/arma gastando 1 pedra do elemento, sem preço em moedas).
- ASSUMINDO: degraus 2–5 são passivos (+N na perícia do ofício, como antes) e liberam receitas; `OUT_OF_COMBAT` segue sem uso (entra com cura/furto no N-22); sem tela de receitas; mapa pedra→elemento (gelo=água, raio=ferro, sombra=trevas); itens quebrados são `narrativo` e a IA/mestre os concede; a "resistência ao fogo" do Ferreiro fica fora até haver resistência elemental.
- **Validação:** nada compilado nem executado; `OficioN21Test` (6) escrito; `SkillTreeCatalogTest` ajustado (5 degraus nas 12 árvores).

## 2026-10-24 — engine/rules: mecânicas comuns de ofício (N-20, não compilado)

- `CraftKind` (criar, preparar, consertar, identificar), `CraftChance` e `GameEngine.attemptCraft`/`attemptPilfer` (+ `CampaignSession`): d100 no log; sucesso consome as entradas e entrega as saídas; falha perde metade das entradas fora de `manter_na_falha`; afanar usa a chance de acerto (Destreza × Agilidade) e, ao falhar, derruba em 10 a relação do alvo com o ladrão. Nada vale em combate.
- Pacote: `receitas.txt` (`RecipeDef`, validação de ids, tipo, nível e dificuldade; herança por id), `RulePackage.recipe/recipes/craftChance/executeRecipe` (a IA nunca inventa receita), 4 receitas de exemplo e carregamento no app (`BundledContent`).
- ASSUMINDO: chance = 50% + 5% × Habilidade + 2% × Sorte + 5% × degrau acima do primeiro − dificuldade (5–95%; 100% se a habilidade garante); falha de conserto mantém a peça e perde metade dos materiais; sem durabilidade nem item "quebrado" no catálogo (receitas de conserto/identificação só quando o pacote declarar os ids); sem tela, proposta da IA nem habilidade `OUT_OF_COMBAT` ligada (entram com as ondas N-21 a N-24).
- **Validação:** nada compilado nem executado; `OficioN20Test` (engine 8, rules 5) escrito.

## 2026-10-24 — engine/rules/app: degraus de habilidade (N-19, não compilado)

- `TIER_LEVELS` 1, 7, 13, 21, 30 pelo nível do personagem; degrau 1 de classe e profissão gratuito e concedido na criação (153 linhas do catálogo); `SkillActionKind.OUT_OF_COMBAT`; concessão automática pula o degrau 1 que ainda não cumpre requisitos.
- ASSUMINDO: níveis das cadeias atuais mantidos até os degraus por profissão (N-21 em diante); nenhuma habilidade usa a nova ação ainda.
- **Validação:** nada compilado nem executado; `DegrausN19Test` (2) escrito e `SkillTreeCatalogTest` ajustado.

## 2026-10-24 — Fase 18 concluída em código: elementos e encantamentos (N-16 a N-18, não compilada)

- **Suíte completa:** não executada. `./gradlew --offline :engine:test :rules:test :ai:test` falhou ao baixar o Gradle (sem rede), então nada compilou nem rodou; os 12 testes novos da fase (`ElementosN16Test` 7, `EncantamentoN17Test` 3, `DiasPergaminhoN18Test` 4, `PergaminhoElementoN18Test` 2) seguem não executados.
- **Conferência estática:** corrigida a ordem de imports em `LocalActions.kt` (ktlint) e removido um import sem uso em `SkillTreeCatalog.kt`; `when` sobre eventos têm `else`; ids com `@elemento` passam por `ItemRef`. Nenhuma regressão encontrada por leitura.
- **Pendente fora do ambiente:** `./gradlew test :app:assembleDebug` e correção do que falhar.

## 2026-10-24 — engine/rules/ai/app: pergaminhos de elemento e contador de dias (N-18, não compilado)

- Motor: `GameState.day`, `AdvanceDays`, `ApplyScrollEnchant` e `scrollEnchants` (arma ou peito equipado, um por peça, vence ao passar os dias); descanso local avança 1 dia. 7 pergaminhos `pergaminho-elemento-*` (1 dia; ação `encantar`), `scrollBlock` recusa peça encantada ou com elemento próprio; o golpe usa o elemento do pergaminho (arma e peito).
- ASSUMINDO: sem comando de dias para a IA; preços e a falta de material/item mágico/criação são propostas; o peito com pergaminho também reduz 50% das condições.
- **Validação:** nada compilado nem executado; `DiasPergaminhoN18Test` (4) e `PergaminhoElementoN18Test` (2) escritos.

## 2026-10-24 — rules/engine/app: encantamento do ferreiro (N-17, não compilado)

- Encantamento permanente no id (`base#qualidade@elemento`), só peito e arma, um por peça, sem elemento próprio; preço ⌈5 × item × qualidade⌉; `enchantItem` (ferreiro NPC) troca a peça pela encantada. Arma encantada dá elemento ao golpe; peito encantado define o elemento do defensor (Luz/Trevas ignoram o outro do par) e reduz em 50% o dano das condições (`damageFactorPercent`).
- ASSUMINDO: sem tela de ferreiro nem desencantar; peça só desequipada e fora de combate; peça com elemento próprio não aceita encantamento.
- **Validação:** nada compilado nem executado; `EncantamentoN17Test` (3) e +1 em `ElementosN16Test` escritos.

## 2026-10-24 — engine/rules/app: elementos e condições (N-16, não compilado)

- `ElementChart`: ciclo água>fogo>ferro>vento>terra>água (+50%; o oposto zera o ataque puro), Luz/Trevas +50% nos demais e ×2 entre si, magia combinada +50% (soma com vantagem; 50% contra o elemento que vence um dos dois). `ConditionChance`: base + 2% por Sorte, teto 90%, itens/buffs até 100%. `elementPercent` no ataque unificado e `applyConditionByChance` (d100 no log).
- `itens.txt` colunas 12–14 (`elemento|condicao|chance`, só arma), `espada-de-brasa` e `adaga-envenenada`; catálogo de habilidades aceita `ferro`/`luz`/`trevas`; golpe do app usa elemento da habilidade ou da arma contra a afinidade do alvo e aplica a condição da arma.
- ASSUMINDO: `METAL`=ferro, `YANG`=Luz, `YIN`=Trevas; alvo sem afinidade = sem elemento (NPC até o N-17); percentual antes da resistência fixa; nenhuma habilidade declara elemento ainda; custo da magia combinada pendente.
- **Validação:** nada compilado nem executado; `ElementosN16Test` (6 testes) escrito.

## 2026-10-24 — engine/app: Sorte de equipamento e teto de crítico (N-15, não compilado)

- A Sorte de atributo segue com teto 6 (70%); a Sorte de itens (`luckBonus`) passa do teto, +10% por ponto, até 100% (limite do d10 mínimo 1); `unifiedAttackBetween(luckItemBonus)` e `CombatActions` separam as duas partes.
- ASSUMINDO: só equipamento conta como Sorte de item (encantamentos entram no N-17); buffs e traços ficam sob o teto; Sorte de item negativa conta 0.
- **Validação:** nada compilado nem executado; 2 testes novos em `UnifiedCombatTest`.

## 2026-10-24 — rules/app: escudo e arma de duas mãos (N-14, não compilado)

- Coluna 11 `maos` em `itens.txt`; arma de duas mãos bloqueia o escudo nos dois sentidos (`handsConflict`) e rende ataque ×1,5; escudos dão Resistência (`resistencia:+1;max_pv:+5`); novos `montante` e `arco-longo`.
- ASSUMINDO: escudo +1 em qualquer nível até os itens de nível alto; ×1,5 só em `ataque`; bloqueio só ao equipar.
- **Validação:** nada compilado nem executado; `EscudoDuasMaosN14Test` (4 testes) escrito.

## 2026-10-24 — rules/app: peso, classe e penalidade (N-13, não compilado)

- Peso de equipamento (`peso`, coluna 9) e tipo de arma (`tipo`, coluna 10) em `itens.txt`; `armorWeight` (13 pesadas, 18 médias, 19 leves) e `weaponTypes` nas 50 classes de `personagem-catalogo.json`; peça ou arma fora da classe do herói vale metade dos bônus positivos (arredonda para cima, depois da qualidade; penalidades da peça inteiras); vantagens `mestre-das-armas` e `mestre-dos-equipamentos` anulam a penalidade; combate, `max_<recurso>` e inventário usam o perfil do herói. Corrigido o validador do `PackageParser`, que ainda recusava efeitos de item de atributo e `max_<recurso>`.
- ASSUMINDO: pesos dos itens e tipos de arma por classe são proposta; várias classes = união; NPC sem penalidade; escudo sem peso até o N-14; `max_<recurso>` calculado com o perfil do momento ao equipar e desequipar.
- **Validação:** nada compilado nem executado; `PesoClassePenalidadeN13Test` (12 testes) escrito.

## 2026-10-24 — docs: decisões de N-12 a N-27 (só documentação, nada compilado)

- `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md`: nova seção 15 e anotações ✅ nos itens ❓ e nos módulos N-12, N-13, N-14, N-16, N-17, N-18, N-19, N-26 e N-27. Decisões: bônus base de item = ⌈nível ÷ 10⌉ (peito vira PV máximo, 5 × o bônus); Mestre das Armas (qualquer arma sem penalidade) e Mestre dos Equipamentos (sem penalidade de classe), obtidos na criação pela tabela de equilíbrio existente; peça de peso errado vale metade dos bônus positivos; duas mãos bloqueia o escudo e dá ataque ×1,5; sete elementos com ciclo de +50%, Luz e Trevas (+50% em todos, recebem +50%, dobro entre si), ataque puro sem dano contra o oposto, magia combinada soma +50% e perde o bônus (e 50% do dano) contra o elemento que vence o seu; chance de condição = fixa + 2% por Sorte (teto 90%, 100% com itens/buffs); encantamento permanente do ferreiro, um por peça (−50% condição, −100% elemento), preço 5 × item × qualidade; pergaminho de elemento dura 1 dia (1 descanso completo = 1 dia); degraus pelo nível do personagem e criação com classe + profissão; cura em percentual do PV máximo; categorias de ameaça ×0,5 a ×3 e teto de PV sem pacote.
- Pendência de N-02/N-03 fechada: os 10 pontos são os mesmos (10 de status + 2 de habilidade).
- ASSUMINDO: peito de Luz/Trevas ignora o outro do par; custo da magia combinada ainda por definir (N-16); ritmo do equilíbrio de vantagens depois de 3 = 5 segue `equilibrio_apos|dobrar` até confirmação.
- **Validação:** nada compilado nem executado; nenhuma mudança de código.

## 2026-10-24 — engine/rules/app: nível mínimo e qualidades (N-12, não compilado)

- Qualidades normal 100 / ótima 125 / rara 150 / épica 200 / lendária 250 (épica e lendária só a partir do nível 10; `boa` vira alias de ótima); coluna 8 `nivel` em `itens.txt` e `EquipItem.minLevel` (motor recusa abaixo do nível); 4 itens de nível 5. `NivelMinimoQualidadeN12Test` (4), `EquipamentoNivelMinimoTest` (1), `ItensQualidadeTest` ajustado.
- ASSUMINDO: bônus base por faixa de nível ainda em aberto; trava só ao equipar.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/rules/app: slots e efeitos de equipamento (N-11, não compilado)

- Slots `elmo`, `luvas`, `botas`, `calca`, `peito` (+ `mao`, `outra-mao`); efeitos de item aceitam atributos e `max_<recurso>`; `EquipItem`/`UnequipItem`/`EquipmentChanged` com `maxDelta` (padrão vazio, log antigo abre) ajustam o máximo do recurso sem matar. Itens do sistema próprio redistribuídos e +5 itens (luvas, botas, 2 calças, cajado). `EquipamentoMaxRecursoTest` (3), `SlotsEquipamentoN11Test` (3).
- ASSUMINDO: bônus numéricos são proposta (N-12 recalibra); `inteligencia` de arma não recalcula PM.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — rules: traços e requisitos (N-10, não compilado)

- 93 requisitos `habilidade>=N` de `tracos.txt` migraram para o atributo do tema: Inteligência (magia, espíritos, conhecimento, informática; 53), Agilidade (movimento, furtividade, esquiva; 21), Destreza (precisão, furto, tiro; 18) e Sorte (`sorte-do-mar`); Habilidade segue em ofício/técnica. Arquétipos e `exemplos.txt` reajustados para continuarem alcançando seus traços e cardápios (máx. 10 pontos). `TracosRequisitosN10Test` (5); `EquilibrioEOpcoesTest` ajustado.
- ASSUMINDO: mapeamento por tema e limiares N mantidos são proposta; fichas salvas não são revalidadas.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/app: combate único (N-09, não compilado)

- Golpe do jogador, multiataque, habilidades e turno de NPC usam só o pipeline unificado (`CombatActions.strike`); redução de dano (`UnifiedAttackSpec.damageReduction`) e postura defensiva no pipeline; `GameEngine.attack` marcado `@Deprecated`. `CombateUnicoTest` (3).
- ASSUMINDO: bônus de `dano` soma ao ataque; perfil de dano 1d6 não é mais lido pelo ataque; remoção do caminho antigo no N-28.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/rules/ai/app: condições percentuais (N-08, não compilado)

- Dano por rodada em % do máximo do recurso (arredondado para cima, mínimo 1): `ConditionState.damagePercent`/`tickDamage`, `AddCondition`/`ConditionAdded`/`Inflict`, coluna `N%` em `condicoes.txt`; `sangramento` 10%, `envenenado` 5%, `queimando` 15%. Testes novos: `CondicoesPercentuaisTest` (5) e `CondicoesPercentuaisPacoteTest` (1).
- ASSUMINDO: percentuais confirmados por "Siga"; dano fixo antigo segue válido.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/rules/app: crítico e Sorte (N-07, não compilado)

- Crítico em d10 (base 10%), limite = 10 − min(Sorte, 6) − bônus `crit`, mínimo 1; teto de 70% pelos atributos, 100% com itens. `combate_atributo_sorte|sorte`; `unifiedAttackBetween(luckStat)`. `UnifiedCombatTest` ajustado (+2).
- ASSUMINDO: Sorte só afeta o crítico; d10 vale para todos os pacotes.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/rules/app: acerto, esquiva e iniciativa (N-06, não compilado)

- Teste de acerto automático (50% + 5% × (Destreza − Agilidade), 5–95%, d100) antes do crítico; erro = sem dano. `combate_atributo_acerto|destreza`, `combate_atributo_esquiva|agilidade`, iniciativa por `agilidade`. `ClashRecorded`/`ClashReport` com campos de acerto (padrão nulo; log antigo abre). `UnifiedCombatTest` +3.
- ASSUMINDO: escala e limites propostos por mim; caminho antigo de `Combat.kt` fica para o N-09.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/rules: dano e defesa (N-05, não compilado)

- `UnifiedCombat.damageOf`: dano = ataque − 0,65 × defesa, arredondado para cima, mínimo 0; defesa rolada nunca negativa (`ClashResult.defenseTotal`); `combate_atributo_defesa|resistencia` no sistema próprio. `UnifiedCombatTest` atualizado (+1).
- ASSUMINDO: caminho antigo de `Combat.kt` inalterado até o N-09; sem mudança de equipamento.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/rules/app: progressão sem teto (N-04, não compilado)

- `custo_nivel_fator|5`: curva de XP 5 × N além da tabela (`EngineConfig.levelCostFactor`, `levelFor`, `levelCost`); `maxQuestXp` por fórmula; `combate_xp_vitoria_percentual|8` e `EngineConfig.battleXp` (XP por inimigo, escala com nível e diferença); `CombatActions.awardCombatVictoryXp` usa o novo cálculo. `FORMATO_PACOTE.md` atualizado.
- Teste novo: `ProgressaoSemTetoTest` (5), escrito e não executado.
- ASSUMINDO: modulação ±20% por nível de diferença (0 abaixo de −5); percentual 8%; pacotes sem as chaves mantêm o comportamento antigo.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — rules: recursos PV, PM e PA (N-03, não compilado)

- `recursos.txt` do sistema próprio: PV = 5 × Resistência (mín. 1), PM = 2 × Inteligência, PA = 1 + Habilidade. Nova coluna opcional `minimo` em `recursos.txt` (`ResourceDef.min`, parser, `resourcesFor`); `FORMATO_PACOTE.md` atualizado. Condições sem `recurso` herdam o recurso de `morte` do manifesto (`pv` × `hp` sem divergência).
- Teste novo: `RecursosN03Test` (5), escrito e não executado.
- ASSUMINDO: PM e PA fechados aqui (sem confirmação); fichas salvas não são recalculadas; defesa e iniciativa seguem em Habilidade até N-05/N-06; ❓ custo de PM da árvore com PM menor (N-19).
- **Validação:** nada compilado nem executado.

## 2026-10-24 — engine/rules/app: distribuição inicial de 10 pontos de status + 2 de habilidade (N-02, não compilado)

- `AllocateInitialBuild` passa a exigir exatamente 10 pontos de status entre os atributos e 2 pontos de habilidade (antes: 12 livres entre os dois). Novos `EngineConfig.initialStatPoints`/`initialAbilityPoints` (padrão 10 e 2, lidos de `pontos_iniciais_status`/`pontos_iniciais_habilidade` em `criacao.txt`); atributo ausente em ficha antiga usa o padrão do pacote. A tela da árvore de habilidades pede só os pontos de status e concede os 2 de habilidade; textos atualizados.
- Testes: +2 em `AtributosSemTetoTest` e verificação da config em `SeteAtributosTest`, escritos e não executados.
- ASSUMINDO: o atributo Sorte ainda não influencia o crítico (N-07). ❓ Conferir se os 10 pontos da criação da ficha e os 10 da distribuição inicial não se somam.
- **Validação:** nada compilado nem executado.

## 2026-10-24 — rules/engine: sete atributos sem teto (N-02, não compilado)

- `atributos.txt` do sistema próprio passa a ter Poder, Habilidade, Resistência, **Inteligência, Agilidade, Destreza e Sorte**, todos com mínimo 0, padrão 0 e `max` = `-` (sem teto). `PackageParser` aceita `-` (vira `StatDef.NO_CAP`, `StatDef.uncapped`) e continua aceitando teto numérico; trecho de regra e tela de evolução dizem "sem teto". `AllocateStatPoints` usa o padrão do pacote para atributo que a ficha antiga não tem (fichas antigas abrem com os novos em 0).
- Testes: `SeteAtributosTest` (6) e `AtributosSemTetoTest` (3), escritos e não executados.
- ASSUMINDO: o id `sorte` do atributo coincide com o do teste `sorte` e do escopo do traço Sortudo (sem conflito no parser; o N-07 decide a ligação). Fórmulas de recursos, atributos de combate e requisitos de traços **não mudaram** (N-03, N-05/N-06 e N-10). A distribuição inicial do motor segue em "12 pontos" (❓ no documento de refinamentos).
- **Validação:** nada compilado nem executado (último CI verde: #40097446606, sem este módulo).

## 2026-10-24 — CI: Android CI #40097446606 verde (ktlint, JVM, E2E, migrações Room e APK de debug)

- Commit `40c38b1`: `Check Kotlin style` passou; `./gradlew test` passou com 650 testes JVM (`ai` 203, `engine` 255, `rules` 192); `:app:connectedDebugAndroidTest` rodou 9 testes no emulador API 35 (Pixel 2); `:data:connectedDebugAndroidTest` rodou 6 testes de migração Room; `:app:assembleDebug` e o upload de `rpgsolo-debug-apk` passaram (APK de debug, 11.365.245 bytes, SHA-256 `dd870c22…8fad`).
- E2E corrigido: textos de prontidão alinhados a "Preparar sua mesa", espera da mensagem assíncrona de ficha criada, navegação pelo menu "Mais" no layout estreito, matchers sem ambiguidade, Android Test Orchestrator com `clearPackageData` e tela de jogo com campo de ação e **Enviar** acessíveis em telas pequenas.
- Docs: novo `docs/relatorios/ci-android-2026-10-24.md`; `docs/VALIDACAO_CI.md` e `README.md` atualizados com o estado verde e as pendências (tornar os testes de `data` bloqueantes no workflow, commitar o schema Room v10, validação visual em aparelho).
- **Validação:** run #40097446606 no GitHub Actions (link em `docs/VALIDACAO_CI.md`).

## 2026-10-23 — docs: decisões da fundação dos atributos e do combate (N-01, só documentação)

- Novo `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` com o plano em 28 módulos (N-01 a N-28, fases 15 a 20) e a regra de fechamento por módulo. Decisões do N-01: Habilidade passa a PA e bônus em testes de ofício/perícia, iniciativa vai para a Agilidade; 10 pontos de status + 2 de habilidade/profissão na criação (+1 e +1 por nível); curva de XP sem teto (subir para o nível N custa 5 × N); XP de missão e de vitória em batalha balanceado por nível (hoje a vitória paga 5 fixo e o teto de missão para na última faixa da tabela); ligação `pv` × `hp` no N-03; testes novos autorizados nesta rodada.
- ASSUMINDO: percentuais de XP de batalha (≈ 8% do custo do nível) e faixas de missão são proposta, a fechar no N-04. Nenhum código, dado ou formato de pacote alterado.
- **Validação:** nada compilado nem executado.

## 2026-10-22 — rules/app: custo de mana nas habilidades da árvore (M-10, não compilado)

- 677 habilidades ativas custam PM (= classificação 1–7); ataques especiais passam a gastar o recurso (`CombatActions.attack` com `spendResource/spendCost`, `SkillTreeActions`, `AppModel`); carregador do catálogo aceita custo nesses ataques. +2 testes em `SkillTreeCatalogTest`.
- **Validação:** nada compilado nem executado.

## 2026-10-22 — rules/app: catálogo de itens, qualidade e renascimento no pacote (M-09.1–3, M-04.8, não compilado)

- +47 itens no sistema próprio (poções, antídoto, anti-sono, pergaminhos, pedras elementais, materiais, itens de renascimento) e 3 condições (`sono`, `paralisado`, `medo`). Ações `remover-condicao`/`aplicar-condicao`; qualidade normal/boa/rara/lendária no id (`base#qualidade`) com percentual por pacote; `itens_renascimento`/`renascimento_percentual` lidos pelo pacote. `ItensQualidadeTest` (6 testes).
- Sem crafting, sem loot e sem chamada de `UseRebirthItem` ainda. **Validação:** nada compilado nem executado.

## 2026-10-22 — engine: item de renascimento e fim de campanha (M-04.7, não compilado)

- `UseRebirthItem`, `rebirthItems`/`rebirthPercent` no `EngineConfig`, `AssumeNpc` recusado com item de renascimento, `EndCampaign`/`CampaignEnded`/`GameState.endedReason` (campanha encerrada recusa comandos), linha do tempo e `RebirthAndEndTest` (4 testes).
- Sem leitura no pacote (`rules`) nem chamada em `ai`/`app`. **Validação:** nada compilado nem executado.

## 2026-10-22 — engine: assumir NPC e ficha do NPC com campos ocultos (M-04.6, não compilado)

- `AssumeNpc`/`NpcAssumed` (troca de PLAYER após a morte do herói, corpo mantém o inventário), `GainXp` só para PLAYER, `GameState.npcSheet` → `NpcSheetView` (campos `null` até assumir), entrada na linha do tempo e `AssumeNpcTest` (8 testes). Corrigida a declaração duplicada de `Entity.shield` em `Model.kt`.
- ASSUMINDO: o herói morto vira `NPC` para sobrar um único `PLAYER`. Sem chamada em `ai`/`app`; faltam item de renascimento, teste de recuperação de itens, campanha encerrada, nível dos aliados e prompt do mestre.
- **Validação:** nada compilado nem executado (o ambiente não alcança os repositórios Maven).

## 2026-10-22 — Q-03.3: jogada completa com IA falsa (teste escrito, não executado)

- Novos `FakeAiPlayTest` e `AiTestSupport` (`ScriptedTransport`, `openTab`, `openAiReply`) em `androidTest`; `FakeAiConfigTest` passou a usar `openTab` (a aba Config fica em "Mais" no layout estreito). Sem código de produção alterado. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — Q-03.2: provedor de IA falso no app (teste escrito, não executado)

- Novo `AiTestHooks` (`app`, só injeção; nulo em produção) usado por `AppModel.applySettings/testConnection`; novo `FakeAiConfigTest` (androidTest). A jogada completa com campanha e rolagem segue pendente em Q-03. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — I-07.2: cenas de referência do mestre ampliadas (testes escritos, não executados)

- `ReferenceScenesTest`: +3 testes (prompt do mestre com regras e pedidos do esquema; `request_quest` válido; recusas de `request_quest`). Exceção autorizada à regra de não criar testes; não executados. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — I-09.2: retrato por foto do aparelho (não validado)

- `AppModel.setPortrait/clearPortrait/portraitFile` (foto reduzida a 512 px, JPEG em `filesDir/portraits/`, só a referência em `Ficha.portraitRef`), seção "Retrato" em `CharactersScreen` e 8 strings `char_portrait_*`. Sem geração por IA, por decisão do proprietário. ASSUMINDO: foto fora do JSON exportado. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-07: layout adaptativo para tablet e paisagem (não validado)

- `RpgSoloApp.kt`: `BoxWithConstraints`; largura ≥ 600 dp usa `NavigationRail` com as 6 abas e conteúdo limitado a 840 dp; abaixo disso, barra inferior de 4 destinos. Sem dependência nova. ASSUMINDO: sem layout de dois painéis nas telas internas. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-06.2: barra de navegação com 4 destinos e "Mais" (não validado)

- `RpgSoloApp.kt`: `Tab.inBar`; barra com Jogar, Heróis, Campanhas e "Mais" (menu com Regras, Sistemas e Config); `strings.xml` ganhou `tab_more`; `NavigationAccessibilityTest` ajustado. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — Fechamento da Fase 12 (suíte não executada)

- Todos os submódulos MU-01 a MU-08 estão concluídos em código. `./gradlew --offline :engine:test :rules:test :ai:test` falhou ao baixar o Gradle (sem rede); nada compilou nem rodou. Conferência estática dos imports e das referências de MU-05.5 feita, sem achados. **Pendente fora do ambiente:** `./gradlew test :app:assembleDebug` e ajuste dos testes antigos que dependiam do empate 0/1 ponto (M-06.5). Fase 12 permanece 🟡. Progresso geral: sem recálculo (≈79% mantido).

## 2026-10-22 — MU-05.5: pedido de missão pela IA (`request_quest`) (não validado)

- `Proposals.kt`: `request_quest {title, objectives, xpReward, masterySkill, spell}` (`QuestRequest`), exclusivo; `TurnRunner.attemptQuest` e `QuestContext` (`PowerMenu.kt`); `GameLoop.questExecutor` liga a `AppModel.addQuest`, que valida título, objetivos, XP e ids no pacote; recusa volta à IA. Fecha a lacuna 3 da Fase 12. ASSUMINDO: missão registrada sem aprovação do jogador; não entra no log (desfazer não a remove). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-02.8: escudo no painel do jogador (não validado)

- `PanelState.shield`, `Pill` em `StatusPanel` e string `status_shield`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-02.7: poder de escudo no pacote (não validado)

- `poderes.txt` aceita efeito `escudo`; `PowerEffect.Shield`; `activatePower` usa `GrantShield`; `FORMATO_PACOTE.md` atualizado. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-02.6: escudo no motor (não validado)

- `engine`: `Entity.shield`, `GrantShield`, `ShieldChanged`, `Damage.bypassShield`; `unifiedAttack` ignora o escudo quando ignora a defesa. Compatível com logs antigos. Sem fonte nem interface. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-07.6: pedido de magia pela IA (não validado)

- `request_power` aceita `magia:<id>`; `TurnRunner.attemptPower` usa `PowerMenu.buildSpell`; `PowerMenu.describe` lista magias aprendidas; `GameLoop` as fornece. Fecha a lacuna 7 da Fase 12. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-07.5: conjuração de magia na aba Jogar (não validado)

- `AppModel.powersView/activatePower`: magias aprendidas com efeito aparecem no painel de poderes e usam `PowerMenu.buildSpell`. Sem mudança de interface; `request_spell` pendente. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-07.4: efeito de conjuração de magia (não validado)

- `criacao.txt`: `magia_<id>_poder` (validada); `RulePackage.spellPowerId`; `PowerMenu.buildSpell` reutiliza o poder ligado. Sem tela nem pedido da IA ainda. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-02.5: recarga (CD) do ignorar defesa (não validado)

- `engine`: `GameState.ignoreCooldowns`, evento `IgnoreDefenseCooldownStarted`, bloqueio em `unifiedAttack` e decremento em `EffectsRoundEnded`; compatível com logs antigos. Escudo segue pendente. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-05.4: recompensas de perícia e magia da missão (não validado)

- `AppModel.addQuest` aceita `masterySkillId`/`spellRewardId` (validados no pacote); concluir a missão chama `awardQuestMastery`/`awardQuestSpell`; `CampaignsScreen` ganhou 2 campos opcionais; 4 strings novas. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-06.8: `excecoes.txt` no pacote de regras (não validado)

- `rules`: `ExceptionDef`, parser, herança e `RulePackage.exceptionRegistry()`; `app`: `AppModel` e `CombatActions.unifiedAttack` passam o registro ao motor e aos menus. `FORMATO_PACOTE.md` atualizado. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-06.7: `habilidades.txt` no pacote de regras (não validado)

- `PackageModel.kt`/`PackageParser.kt`/`PackageRegistry.kt`/`RulePackage.kt`: `AbilityDef`, parser com validação, herança por id e `abilitySpec(id)`; `FORMATO_PACOTE.md` atualizado. Lacuna 2 da Fase 12 fechada só para habilidades. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-03.4: pedido de ataque da IA (`request_attack`) (não validado)

- `Proposals.kt`: `request_attack {target}` (`AttackRequest`), exclusivo; `TurnRunner.attemptAttack` valida combate, turno do jogador e alvo NPC vivo e delega a `AttackContext` (`GameLoop.attackExecutor` → `CombatActions.unifiedAttack` em `AppModel`); recusa desfaz e volta à IA. Fecha a lacuna 1 da Fase 12. ASSUMINDO: só o herói usa o pedido. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-06.6: duração de habilidade até o fim da cena (não validado)

- `Abilities.kt`/`Events.kt`: `AbilitySpec.untilSceneEnd` e `ActiveEffect.untilSceneEnd` (padrão false); sem rolar rodadas, recusa combinar com `untilCombatEnd`; `PlayerMovedToPlace` remove os efeitos marcados. ASSUMINDO: cena = lugar atual. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-06.5: duração de habilidade até o fim do combate (não validado)

- `Abilities.kt`/`Events.kt`: `AbilitySpec.untilCombatEnd` e `ActiveEffect.untilCombatEnd` (padrão false, compatível com dados antigos); exige combate ativo, sem rolar rodadas; `CombatEnded` remove os efeitos marcados. Duração por cena segue pendente. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-03.3b: imunidade de exceções mecânicas na aplicação de condições (não validado)

- `CheckMenu` e `PowerMenu` ganharam o parâmetro opcional `exceptions: ExceptionRegistry?` (padrão nulo); `isImmune` e a validação de poderes de condição o consultam. Sem fonte de exceções ainda, o comportamento do app não muda. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — MU-05.1b: XP da missão na tela de campanhas (não validado)

- `AppModel.addQuest` aceita `xpReward` (0–100000) e `CampaignsScreen` ganhou o campo "XP ao concluir"; 2 strings novas. O teto por nível continua no motor. Proposta do mestre com o valor segue pendente. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — DC-01.5: tabelas em DOCX (não validado)

- `SourceDocx.kt`: `extract` percorre tabelas e parágrafos na ordem do documento; cada linha de tabela vira `célula | célula`. Texto fora de tabelas e títulos inalterados. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — DC-05.5: consentimento Q-09 na tela de importação (não validado)

- `SourcesSection.kt`: sem consentimento na campanha ativa, "Importar" abre o diálogo de privacidade (autorizar e importar, importar sem autorizar, cancelar); 3 strings novas. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — I-09.1: história do personagem por IA na biblioteca de fichas (não validado)

- `AppModel.generateLore/approveLore`, seção "História" em `CharactersScreen` e 13 strings `char_lore_*`. A história só é gravada (`approvedLore`) após aprovação; o rascunho é editável e não altera a ficha. Retrato por IA segue pendente (sem provedor de imagem). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — C-02: sugestões de substituição na importação de fichas (não validado)

- `CharacterCreationCatalog.suggestReplacements` (similaridade por id/nome) e `AppModel.importFicha` listando as sugestões junto ao aviso de referências inválidas. Só sugere; nada é alterado automaticamente. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — C-04: aviso das fórmulas de recursos mantido (não validado)

- Sem alteração de código ou dados. Decisão registrada no plano: o aviso de rascunho de PA/PM/PV permanece até o proprietário aprovar as fórmulas; passos para fechar documentados. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-03.2: chips de causa no cartão de rolagem (não validado)

- `PlayController.kt`: `RollView.chips` e `rollChips(spec)` (vantagem, desvantagem, dados explosivos, bônus/penalidade do `CheckSpec`). `DiceUi.kt`: chips (`Pill`) exibidos só após revelar a rolagem. Origem do bônus (traço/condição) não rastreada; fica pendente. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — M-01.9: bônus de três desvantagens do sistema próprio (não validado)

- `tracos.txt`: `rato-de-tela` e `cabeca-em-livros` (atletismo -1) e `mimado` (sobrevivencia -2) passaram a declarar bônus. Só a parte numérica incondicional foi declarada; o restante das descrições segue narrativo. Varredura dos demais traços: sem outro candidato incondicional com escopo suportado. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — M-06: conferência e correção de texto em `MECANICA_DADOS.md` (não validado)

- `docs/MECANICA_DADOS.md`: removida a frase que dizia que a tabela empírica ainda seria recalculada (ela já consta em "Tabela empírica (M-06.5)"). `tools/simulate_checks.py` roda localmente e reproduz as probabilidades exatas da tabela. Sem alteração de código. M-06 segue concluído no código; falta testes e CI. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado (apenas o simulador Python).

## 2026-10-22 — Q-01: ícone adaptativo e assinatura opcional do release (não validado)

- `app/build.gradle.kts`: `signingConfig` de `release` criado apenas com `RELEASE_KEYSTORE_PATH`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS` e `RELEASE_KEY_PASSWORD` presentes. Ícone adaptativo vetorial (`mipmap-anydpi-v26`, `drawable/ic_launcher_foreground.xml`, `values/ic_launcher_colors.xml`) ligado em `AndroidManifest.xml` (`icon`/`roundIcon`). R8 e `versionName` por tag já existiam. Pendente: `assembleRelease` com keystore real e teste do app minificado. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-08: relatório de diagnóstico local (não validado)

- Novo `Diagnostics.kt`: handler de falhas não tratadas (instalado em `MainActivity`) grava a última falha com classe e frames, sem mensagem; `buildReport` monta o relatório (versão do app, Android, aparelho, falha) e `chooser` o compartilha via `FileProvider`. `file_paths.xml` ganhou `cache-path` `diagnostics/`. `ConfigScreen.kt`: cartão "Diagnóstico" com exportar e limpar; 14 strings `diag_*`. Sem prompt, texto de jogada, dado de campanha ou chave. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: fechamento da camada de interface (não validado)

- U-02 marcado como concluído para as telas Compose. Sem alteração de código nesta entrega. `CharacterCreationCatalog`, `*Actions` e `AppModel` permanecem em português (camada sem `Context`, mensagens misturadas com `reason` do `engine` e prompts da IA); migração só se a decisão 5 (idioma) optar por tradução, com provedor de strings ou códigos de erro. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: CharactersScreen concluída (não validado)

- `CharactersScreen.kt` e `strings.xml`: importação por JSON, biblioteca, fichas arquivadas, `TraitRow`, `CatalogPicker` e `CatalogMultiPicker` movidos para recursos (`char_*`; 45 strings). `traitTag` agora recebe `Context`; mensagens de callbacks usam `context.getString`. Nenhum texto fixo de UI resta na tela. Restam em U-02 `CharacterCreationCatalog` e `*Actions`/`AppModel` (inclui a checagem por texto da mensagem de importação). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: CharactersScreen, strings de edição, atributos, traços e revisão (não validado)

- `CharactersScreen.kt` e `strings.xml`: edição de escolhas, nome/conceito, atributos, perícias, pontos, balanço vantagens x desvantagens, cardápio do arquétipo, filtros de traços, revisão final e botão de criar movidos para recursos (`char_*`; 52 strings). Descrições de TalkBack dos botões `+`/`−` e mensagens definidas em lambdas não composables usam `LocalContext`/`getString`. Restam em `CharactersScreen` importação por JSON, biblioteca, `TraitRow`, `CatalogPicker` e `CursePicker`; depois `CharacterCreationCatalog` e `*Actions`/`AppModel`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: CharactersScreen, strings da origem e do catálogo (não validado)

- `CharactersScreen.kt` e `strings.xml`: seleção de sistema, ponto de partida, raça/sub-raça/híbrido/classes/afinidade/profissão/Malditos e resumo das escolhas movidos para recursos (`char_*`; 37 strings; a mensagem de falha de `io` é lida antes da corrotina). Restam em `CharactersScreen` edição de escolhas, nome/atributos/perícias/traços, revisão final, importação, biblioteca, `TraitRow`, `CatalogPicker` e `CursePicker`; depois `CharacterCreationCatalog` e `*Actions`/`AppModel`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: PlayScreen concluída (combate, status e chat) (não validado)

- `PlayScreen.kt` e `strings.xml`: `CombatControls`, `StatusPanel`, `ChatItem`, `Thinking`, `BottomBar` e a prévia de narração movidos para recursos (`combat_*`, `status_*`, `chat_*`; 29 strings com argumentos posicionais). Nenhum texto fixo de UI resta na tela; mensagens vindas de `AppModel` ficam para a etapa `*Actions`/`AppModel`. Restam em U-02 `CharactersScreen`, `CharacterCreationCatalog` e `*Actions`/`AppModel`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings das ações rápidas e dos testes avançados (não validado)

- `PlayScreen.kt` e `strings.xml`: `QuickActions` e `AdvancedCheckControls` (disputa e teste de grupo) movidos para recursos (`quick_*`, `advcheck_*`; contagem de sucessos com `%1$d`). Restam em `PlayScreen` combate, painel de status e chat; depois `CharactersScreen`, `CharacterCreationCatalog` e `*Actions`/`AppModel`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings de poderes, traços, condições, sobrevivência, mapa e adaptações (não validado)

- `PlayScreen.kt` e `strings.xml`: `PowersPanel`, `TraitUsesPanel`, `ConditionRemovalPanel`, `SurvivalPanel`, `PlacesPanel` e `AdditionalRulesPanel` movidos para recursos (`powers_*`, `traituses_*`, `conditions_*`, `survival_*`, `places_*`, `rules_*`). Restam em `PlayScreen` ações rápidas, testes avançados, combate, status e chat; depois `CharactersScreen`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings dos painéis de sorte, inventário e loja (não validado)

- `PlayScreen.kt` e `strings.xml`: `LuckPanel`, `InventoryPanel` e `ShopPanel` movidos para recursos (`luck_*`, `inventory_*`, `shop_*`; valores numéricos com `%s`). Restam em `PlayScreen` poderes, traços, condições, sobrevivência, mapa, adaptações, ações rápidas, testes avançados, combate e chat. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings da tela Jogar, preparação (não validado)

- `PlayScreen.kt` e `strings.xml`: tela de preparação em três etapas e tela de falha do mestre movidas para recursos (`play_*`). Restam em `PlayScreen` os painéis (sorte, inventário, loja, poderes, traços, condições, sobrevivência, mapa, adaptações, ações rápidas, testes avançados, combate) e o conteúdo do chat; depois `CharactersScreen`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: CampaignsScreen concluída (não validado)

- `CampaignsScreen.kt` e `strings.xml`: status e mensagens de erro dos seletores de backup e PDF movidos para recursos (`backup_*`, `pdf_*`, `error_*`). Nenhum texto fixo resta na tela. U-02 pendente: `PlayScreen`, `CharactersScreen`; `CharacterCreationCatalog` e `*Actions`/`AppModel` por último (sem Context). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings de capítulos, missões, linha do tempo e diário (não validado)

- `CampaignsScreen.kt` e `strings.xml`: seções de capítulos, relações, evolução, pontos de evolução/status, slots de magia, missões, linha do tempo e diário movidas para recursos (`chapter*`, `quest*`, `timeline_*`, `advancement_*`, `statpoints_*` etc.); descrições de TalkBack dos botões `+1` também. Resta em `CampaignsScreen` apenas o texto dos seletores de arquivo (status de backup/PDF). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings de renomear/apagar, PDF e backup (não validado)

- `CampaignsScreen.kt` e `strings.xml`: rótulos, diálogo de exclusão, seção de PDF e de backup local movidos para recursos (`campaign_*`, `pdf_*`, `backup_*`); `campaign_cancel` já existia e foi reaproveitada. Restam em `CampaignsScreen` os textos dentro dos seletores de arquivo (status de exportação/restauração), capítulos, relações, evolução, missões, linha do tempo e diário. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings de campanhas salvas e modo de fidelidade (não validado)

- `CampaignsScreen.kt` e `strings.xml`: aviso de gravação pendente, lista de campanhas (estado, datas, Continuar/Pausar/Concluir/Reativar) e modo de fidelidade das fontes movidos para recursos (`campaign_*`). Restam em `CampaignsScreen` backup, PDF, capítulos, relações, evolução, missões, linha do tempo e diário; `PlayScreen`, `CharactersScreen`. `CharacterCreationCatalog` e `*Actions` ficam por último (camada sem Context). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings da árvore de habilidades (não validado)

- `SkillTreePanel.kt` e `strings.xml`: textos do painel, mensagens de resultado e rótulos de elemento/categoria movidos para recursos (`skilltree_*`); `elementLabelUi` e `categoryLabel` passaram a `@Composable`. Restam `CharactersScreen`, `CampaignsScreen`, `PlayScreen`, `CharacterCreationCatalog` e mensagens em `*Actions`/`AppModel`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings do painel de maldições (não validado)

- `CursePanel.kt` e `strings.xml`: textos do painel de maldições lançadas movidos para recursos (`curse_*`); mensagens de resultado usam `context.getString`. Restam `CharactersScreen`, `CampaignsScreen`, `PlayScreen`, `SkillTreePanel`, `CharacterCreationCatalog` e mensagens em `*Actions`/`AppModel`. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings de documentos-fonte e fatos (não validado)

- `SourcesSection.kt` e `strings.xml`: textos da importação, biblioteca, progresso, estados de fato e formulário de fatos saíram do código para recursos (`sources_*`, `fact*`). Restam telas com texto fixo (`CharactersScreen`, `CampaignsScreen`, `PlayScreen` etc.). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-01: contraste e áreas de toque (não validado)

- `Theme.kt`: `outline` do tema escuro de `0xFF6B5E51` para `0xFF8A7B6C` (contraste de borda 2,84:1 → 4,35:1 sobre a superfície). Demais pares de texto dos dois temas já passavam (≥ 5,4:1, cálculo local). Áreas de toque: sem `clickable` customizado; nenhuma mudança. Pendente: revisão com TalkBack em aparelho. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-01: descrições de TalkBack no interruptor e no controle de fonte (não validado)

- `CampaignsScreen.kt`, `ConfigScreen.kt`, `strings.xml`: `Switch` de consentimento de fontes e `Slider` de tamanho da fonte com `contentDescription`. Pendente em U-01: áreas de toque, contraste e revisão completa com TalkBack. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — I-02: memória longa concluída (documentação sincronizada)

- I-02.3 (memórias da cena no contexto do mestre) e I-02.4 (`MemoryRollup`, `MemoryRolledUp`) já estavam em código; plano atualizado, pausa removida. Sem mudança de código. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** conferência estática; nenhum teste, build ou CI executado. Falta teste do resumo rolante.

## 2026-10-22 — U-05: transição automática para Jogar (não validado)

- `RpgSoloApp.kt`: ao concluir as três etapas do primeiro uso, o app abre a aba Jogar. Presets de provedor continuam pendentes (I-03). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-06: ícones vetoriais na barra de navegação (não validado)

- `RpgSoloApp.kt`: `Tab.icon` passa de emoji a `ImageVector` (ícones do `material-icons-core`); descrição de TalkBack preservada; import de `sp` removido. Redução para 4 destinos fica pendente de decisão. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado; disponibilidade de `material-icons-core` via `material3` a confirmar no build.

## 2026-10-22 — M-05 e I-05 conferidos

- M-05: `envenenado`, `queimando`, `sangramento` e a remoção por teste (coluna 8 de `condicoes.txt`, `removeConditionByCheck`, painel na aba Jogar) já estavam em código e documentados; sem alteração.
- I-05: o contexto da IA já recebe só o desfecho em palavras (`outcomeWord`), sem números de dado; marcado como concluído. Sem mudança de código. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** conferência estática; nenhum teste, build ou CI executado.

## 2026-10-22 — I-08: fila única por endpoint conferida

- `ProviderFactory.buildRoles` já compartilha um `RateLimiter` entre os três papéis; item marcado como concluído. Sem mudança de código. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** conferência estática; nenhum teste, build ou CI executado.

## 2026-10-22 — U-09: aba Sistemas separa disponíveis de "em breve" (não validado)

- `SystemsScreen.kt` e `strings.xml`: seção "Em breve" para edições do catálogo sem pacote; oculta quando vazia (caso atual). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-03: pular animação, reduzir movimento e histórico de rolagens (não validado)

- `DiceUi.kt`: botão "Pular animação"; com a escala de animação do sistema em 0 a rolagem é revelada sem animar; novo `RollHistory`. `PlayScreen.kt` exibe o histórico (só rolagens reveladas, últimas 10).
- Pendente: chips de causa da mudança do teste (exigem dados novos em `RollView`). Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-04: estado de rolagem revelada conferido

- Conferido em `PlayController`, `PlayScreen` e `DiceUi`: o estado "revelada" fica no controlador; só a animação em curso é local. Sem mudança de código. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** conferência estática; nenhum teste, build ou CI executado.

## 2026-10-22 — Q-06: CI com setup-java v5 e runner fixo (conferido)

- O workflow já usava `actions/setup-java@v5` e `ubuntu-24.04`; item marcado como concluído no plano. Sem mudança de código. Progresso geral: sem recálculo (≈79% mantido).
- **Validação:** conferência estática; nenhum teste, build ou CI executado.

## 2026-10-22 — Fase 14: interface de fontes, progresso e fatos (não validada)

- `app/SourcesSection.kt` (aba Campanhas): importação por seletor de arquivos (TXT, MD, DOCX, PDF; limite 5 MiB) com indexação imediata, biblioteca com progresso e erros, vincular/desvincular/remover e fatos canônicos manuais (marcar sabido/remover).
- `app/AppModel.kt`: `importAndIndexSource`, `setSourceLinked`, `removeSource`, `refreshSourceContext`, `addManualFact`, `setFactKnown`, `removeFact`; `resumeSourcePipelines` reutiliza `indexSource`. O contexto documental é reaplicado ao vincular, indexar, alterar fatos, trocar de campanha e conceder o consentimento Q-09.
- Fase 14 concluída em código. **Suíte completa:** `./gradlew` não executa (ambiente offline, sem download do Gradle); nenhum teste, build ou lint rodou. Conferência estática apenas. Progresso geral: sem recálculo (≈79% anterior mantido).

## 2026-10-22 — DC-03: retomada automática da pipeline de indexação (não validado)

- `app/AppModel.kt`: `resumeSourcePipelines()` roda após `startup()` em IO; fontes `READY` sem estado ou em IMPORTED/CHUNKED são relidas do armazenamento privado (hash e tamanho conferidos) e indexadas em lotes, com progresso gravado no Room. INDEXED e FAILED não são repetidos; bytes ausentes ou fonte inválida resultam em FAILED. Falhas da retomada não bloqueiam a abertura do app.
- Sem tela de progresso; a importação não dispara a pipeline fora da retomada. Progresso geral: sem recálculo (≈79% anterior mantido).
- **Validação:** nenhum teste, build, lint ou CI executado (ambiente offline).

## 2026-10-22 — Q-09: consentimento de privacidade das fontes (não validado)

- `app/AppModel.kt`: `sourcePrivacyConsent`/`setSourcePrivacyConsent` (consentimento por campanha em SharedPreferences); `configureCampaignSourceContext` aceita `privacyConfirmed` ou o consentimento salvo; revogar limpa o contexto documental e apagar a campanha remove o consentimento.
- `CampaignsScreen.kt` e `strings.xml`: chave com diálogo de confirmação explicando o que é enviado ao provedor.
- Consentimento local, fora do log e do backup. Progresso geral: sem recálculo (estimativa ≈79% anterior mantida).
- **Validação:** nenhum teste, build, lint ou CI executado (ambiente offline).

## 2026-10-22 — preparação de testes Q-03, Q-04 e Q-07 (escritos, não executados)

- **Q-04:** `data/src/androidTest/.../RpgDatabaseMigrationTest.kt` (migração 2→3 validada, banco v2 reaberto na versão atual pelo builder do app, cópia antes de migrar, cadeia contínua) e configuração do módulo `data` para testes instrumentados (`room-testing`, esquemas como assets). Só os esquemas 2 e 3 estão versionados; o teste contra o esquema da versão atual é ignorado até `10.json` ser commitado.
- **CI:** novo artefato `rpgsolo-room-schemas` com os esquemas gerados pelo build; os testes instrumentados de `data` rodam no mesmo emulador do E2E, de forma informativa (resultado em `data-androidtest-result.txt`).
- **Q-03:** `RollCardRevealTest` e `NavigationAccessibilityTest` em `app/src/androidTest`.
- **Q-07:** auditoria mostrou que a bateria já existia (`ContinuityBatteryTest`); adicionado `CampaignStarterTest.ambientacaoNaoAlteraAFichaDoJogador`.
- **Validação:** nenhum teste novo foi compilado ou executado (sem Gradle/SDK nesta etapa); ktlint dos arquivos novos não foi conferido.

## 2026-10-22 — Android CI após correções de ktlint e compilação (E2E e APK em andamento)

- Android CI #296, branch `main`, commit `9c84a6c` ("Resolve navigation accessibility label before semantics"), disparado por push. Às 20:40 do registro (cerca de 39 min após o disparo) o run seguia "Em processamento", com o job `test-and-build` em execução. O link do run não foi informado.
- **Passaram:** `Check Kotlin style` (32s), `Run JVM and Android tests` (1min52s) e `Upload JVM test reports` (3s). Antes, a execução #39829094817 (commit `cee31e7`) tinha falhado no ktlint e ignorado as etapas seguintes.
- **Em execução no momento do registro:** `Run Android E2E on emulator` (cerca de 7 minutos). **Pendentes:** `Upload E2E diagnostics`, `Build debug APK` e `Upload debug APK`. O resultado final ainda não é conhecido; o APK não deve ser tratado como pronto até essas etapas concluírem.
- Código: reformatação ktlint (ordem de imports, vírgulas finais, quebras de linha) em `ai`, `engine`, `data`, `app` e testes, e correção de compilação em `app/ui/RpgSoloApp.kt` (o rótulo de navegação passa a ser calculado fora do bloco `semantics`). A comparação dos dois zips não mostrou outras alterações de comportamento.
- Fonte do resultado: acompanhamento do job pelo app do GitHub (captura de tela); o log completo não foi anexado ao repositório.

## 2026-10-22 — GitHub Actions #39829094817 (falhou no ktlint)

- Disparo manual em `feature/dc-05-1-source-context-app`, commit `cee31e7`. `Check Kotlin style` falhou com 1.331 violações reportadas em 26 arquivos de `ai/src/main/kotlin`.
- As etapas de testes JVM/Android, E2E e build/upload do APK foram ignoradas pela falha anterior. Não há resultado de testes nesta execução.
- Run: https://github.com/Kyra2214/RPG-solo/actions/runs/39829094817. Log completo sem ANSI e com caminho do runner normalizado: `docs/ci/android-ci-39829094817-ktlint-failure.log`.
- **Ação:** nenhuma correção de código ou repetição automática foi feita.

## 2026-10-22 — atualização documental do estado do projeto

- `README.md`, `PLANO_RPG_SOLO.md` e `docs/VALIDACAO_CI.md` sincronizados com as entregas até DC-01.4, Room v10, lacunas de interface/backup e estado não validado da branch.
- Corrigido também o status M-02 do catálogo para refletir as declarações de efeitos já registradas no plano e no histórico.
- **Validação:** alteração apenas documental; nenhum teste, build ou CI executado.

## 2026-10-22 — DC-01.4: persistência dos documentos originais (não validado)

- `data/SourceDocumentStore.kt`: gravação atômica em armazenamento privado e leitura com conferência de tamanho e SHA-256.
- `app/AppModel.kt`: importação liga o documento original à entrada da biblioteca, evita duplicação por hash e expõe leitura verificada.
- Os bytes não entram no Room nem no log/backup da campanha; arquivos com importação inválida mantêm somente o estado `FAILED` e o motivo.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — DC-04: persistência de fatos canônicos (não validado)

- `data`: tabela `fatos_canonicos` e migração Room explícita 9→10; os fatos são removidos junto com a campanha e copiados ao duplicá-la.
- `RoomEventStore`: valida os fatos e grava o conjunto da campanha atomicamente.
- `app/AppModel.kt`: APIs de leitura/gravação; fatos salvos entram no contexto narrativo apenas após `privacyConfirmed = true`.
- **Validação:** nenhum teste, build, lint ou CI executado; schema Room v10 não gerado nesta etapa.

## 2026-10-22 — DC-03: persistência do progresso de indexação (não validado)

- `engine/SourceIndex.kt`: adicionadas funções de serialização para `PipelineState`.
- `data`: tabela `fonte_pipeline` e migração explícita 8→9; estados por fonte são lidos e gravados no Room.
- `app/AppModel.kt`: APIs para persistir/restaurar estados e reconstruir o índice por campanha a partir de trechos já indexados.
- Os bytes originais ainda não persistem; estado anterior à geração dos trechos requer nova importação. Sem tela de progresso.
- **Validação:** nenhum teste, build, lint ou CI executado; schema Room v9 não gerado nesta etapa.

## 2026-10-22 — DC-02: persistência da biblioteca de fontes (não validado)

- `data`: novas tabelas Room `fontes` e `fonte_vinculos`, com migração explícita 7→8; apagar campanha remove só vínculos por FK e duplicar campanha copia seus vínculos.
- `RoomEventStore` e `AppModel`: API de leitura e gravação transacional da `SourceLibrary`.
- Os bytes originais, `PipelineState` e `CanonFacts` permanecem fora deste módulo; o schema exportado v8 depende da geração do Room em build.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — DC-05.2: seletor de modo das fontes (não validado)

- `app/CampaignsScreen.kt`: adicionadas opções de História fiel, História alternativa e Universo inspirado na aba Campanhas.
- `app/AppModel.kt`: o modo selecionado é enviado como `SetSourceMode` ao motor e registrado no log da campanha.
- `PLANO_RPG_SOLO.md`: atualizado DC-05.2; importação, biblioteca, persistência e consentimento de privacidade continuam pendentes.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — DC-05.1: integração de contexto de fontes (não validado)

- `app/AppModel.kt`: conectado o callback de contexto do `PlayController` ao `SourceIndex`, fatos canônicos e modo da campanha.
- O contexto é por campanha, permanece em memória e é opt-in: sem `privacyConfirmed = true`, nenhum trecho é disponibilizado à IA remota; há API para revogar a configuração.
- `PLANO_RPG_SOLO.md`: atualizado o status de DC-05.1 e mantidas como pendentes a persistência, a interface e a tela de consentimento Q-09.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings do cartão de conflito (não validado)

- `app/ClashCard.kt`: rótulos de defesa ignorada, acerto crítico e dano final passaram a usar recursos Android.
- `app/src/main/res/values/strings.xml`: adicionados placeholders para o dano final exibido.
- `PLANO_RPG_SOLO.md`: registrada a sétima fatia; as demais telas ainda precisam ser migradas.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings da criação de campanha (não validado)

- `app/NewCampaignSection.kt`: título, formulário auxiliar, campos, estados do fluxo, avisos e ações passaram a usar recursos Android.
- `app/src/main/res/values/strings.xml`: adicionados placeholders para edição, personagem, notas, termos ignorados e mensagens de erro.
- `PLANO_RPG_SOLO.md`: registrada a sexta fatia; as demais telas ainda precisam ser migradas.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings da aba Regras (não validado)

- `app/RulesScreen.kt`: estado vazio, busca, tutorial e aviso de regras incertas passaram a usar recursos Android.
- `app/src/main/res/values/strings.xml`: adicionados placeholders para títulos numerados e notas de regras.
- `PLANO_RPG_SOLO.md`: registrada a quinta fatia; as demais telas ainda precisam ser migradas.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings da aba Sistemas (não validado)

- `app/SystemsScreen.kt`: títulos, instruções, importação e metadados exibidos dos pacotes passaram a usar recursos Android.
- `app/src/main/res/values/strings.xml`: adicionados textos estáticos e placeholders de origem, licença e estado do pacote.
- `PLANO_RPG_SOLO.md`: registrada a quarta fatia; as demais telas ainda precisam ser migradas.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings restantes da Configuração (não validado)

- `app/ConfigScreen.kt`: preferências de geração, limites de conteúdo, preços, estimativas e privacidade agora usam `strings.xml`, incluindo placeholders e rótulos das opções.
- `app/src/main/res/values/strings.xml`: catálogo ampliado para cobrir todos os textos visíveis da tela de Configuração.
- `PLANO_RPG_SOLO.md`: registrada a terceira fatia; as demais telas do app ainda precisam ser migradas.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings do provedor e acessibilidade (não validado)

- `app/ConfigScreen.kt`: títulos, campos, avisos, ações do provedor e o bloco de acessibilidade passaram a usar recursos Android.
- `app/src/main/res/values/strings.xml`: adicionados os textos correspondentes, incluindo placeholders para estado de conexão, chave mascarada e escala de fonte.
- `PLANO_RPG_SOLO.md`: U-02 atualizado como segunda fatia; preferências narrativas, limites de conteúdo, preços e privacidade permanecem para etapas posteriores.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-02: strings da navegação principal (não validado)

- `app/src/main/res/values/strings.xml`: criado o primeiro catálogo de textos do app.
- `app/RpgSoloApp.kt`: rótulos e títulos das seis abas, marca do app, carregamento, erro de inicialização e descrição semântica da navegação passaram a usar recursos Android, preservando o comportamento existente.
- `PLANO_RPG_SOLO.md`: registrada a primeira fatia de U-02; os demais textos fixos permanecem pendentes.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — U-01: rótulos acessíveis na criação e navegação (não validado)

- `app/CharactersScreen.kt`: botões `+` e `−` de atributos e perícias agora expõem a ação e o nome do campo ao TalkBack.
- `app/RpgSoloApp.kt`: ícones emoji das seis abas passaram a expor o rótulo textual da aba como descrição semântica.
- `PLANO_RPG_SOLO.md`: registrada a primeira entrega de U-01; revisão completa de acessibilidade permanece pendente.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — I-06: ciclo de cena e turnos no prompt do mestre (não validado)

- `ai/Master.kt`: adicionada a regra 17 ao prompt fixo para limitar cada resposta a um momento significativo, terminar com pergunta direta ao jogador e impedir que a IA escolha ações do jogador ou antecipe a resolução de turnos de NPC.
- `PLANO_RPG_SOLO.md`: I-06 marcado como implementado em código, mantendo a validação executada pendente.
- **Validação:** nenhum teste, build, lint ou CI executado.

## 2026-10-22 — F3: sorte/destino no sistema próprio (não validado)

- `rules/src/main/resources/packages/sistema-proprio/criacao.txt`: o pacote próprio agora declara 1 PA como recurso global de sorte/destino, com custo 1 e as formas `REROLL+ADD_FACE`.
- `PLANO_RPG_SOLO.md` e `README.md`: documentado que a configuração global habilita o painel de sorte para testes sem configuração específica.
- **Validação:** nenhum teste, build, lint ou CI executado, conforme a instrução de implementar código sem disparar validações.

## 2026-10-22 — Combate unificado, privacidade narrativa e memória longa

- `engine/MemoryRollup.kt`, `engine/Commands.kt` e `engine/Events.kt`: resumo rolante de memórias persistido por evento quando o limite é excedido (`bbc4261`).
- `engine/Commands.kt`: pistas públicas e intermediárias de relógios recusam números, IDs, nomes internos e metadados mecânicos (`21704f1`).
- `ai/PlayController.kt`, `app/PlayScreen.kt` e `app/ClashCard.kt`: confrontos persistidos são listados na tela sem recalcular valores (`f4cf65f`); os cartões exibem nomes públicos em vez de IDs internos (`4ee497c`).
- `app/CombatActions.kt`, `app/AppModel.kt` e `app/PlayScreen.kt`: ataque manual usa `unifiedAttackBetween`, grava `ClashRecorded` e avança o turno (`ec598af`).
- `app/CombatActions.kt`: turnos de NPC usam o mesmo pipeline unificado do jogador (`f01c173`); a ação escolhe atacante/alvo, mas o motor decide rolagens, defesa e dano.
- `engine/AutomaticTimeline.kt`: projeção pública neutraliza dano, XP, nível, quantidades de itens, recuperação numérica e dano periódico de condições.
- **Validação:** nenhuma alteração acima foi compilada, testada, submetida ao CI ou validada em aparelho, conforme a instrução de seguir code-first sem disparar testes/CI.
- **Auditoria de documentação:** a leitura de `engine/Commands.kt` confirmou que `NextTurn` já emite `EffectsRoundEnded` ao fechar a rodada; a lacuna correspondente foi removida do plano sem duplicar implementação.

## 2026-10-22 — Contexto privado, timeline e consistência de relógios

- `ai/PowerMenu.kt`: valida o par efeito/alvo novamente no ponto de execução, além das validações do parser e da herança de pacotes.
- `engine/AutomaticTimeline.kt`: registra progressão, recursos recuperados, usos limitados, falhas de sobrevivência e mudanças de relação em linguagem neutra; oculta IDs internos de itens, espaços, condições, magias, habilidades e perícias. Promessas de NPC ficam fora da timeline até o log conseguir provar que o personagem presenciou o evento.
- `engine/AutomaticTimeline.kt`: remove ramificações duplicadas de XP, nível, alocação, magias e habilidades; registra pistas públicas quando o relógio cruza a metade ou conclui.
- `ai/GameLoop.kt`, `ai/Master.kt` e `engine/AutomaticTimeline.kt`: se um único avanço cruza a metade e completa o relógio, preserva a pista intermediária junto da conclusão; consequências não são repetidas em narrações posteriores.
- `engine/Npc.kt` e `app/ui/AppModel.kt`: o contexto privado do mestre inclui motivações e relações de NPCs, ordenadas de forma determinística; não são expostas na interface do jogador.
- `ai/GameLoop.kt`, `ai/CheckMenu.kt` e `ai/Master.kt`: rolagens com tabela respeitam imunidades a condições declaradas no pacote; uma nota mecânica privada acompanha a jogada e permanece disponível ao regenerar a narração.
- **Pendente:** avanço automático de agendas por outros eventos narrativos e ligação das agendas a gatilhos narrativos persistidos.
- **Validação:** nenhum teste, build, lint ou CI executado; tudo permanece adiado para a etapa final.

## 2026-10-22 — Continuação code-first: testes avançados e sorte global (não validado)

- `ai/PowerMenu.kt`: adicionada validação defensiva no ponto de execução para recusar poder cuja combinação efeito/alvo seja incompatível com o motor, mesmo se uma definição incoerente escapar das validações do pacote. Não foram executados testes/build/CI.

- `ai/GameLoop.kt`: testes opostos preservam as alternativas descartadas pela política de fortuna de ambos os participantes; testes em grupo preservam as alternativas descartadas de todos os integrantes no registro da jogada, para que a interface possa exibi-las sem alterar o resultado escolhido.
- `ai/CheckMenu.kt`: a sorte/destino pós-rolagem pode ser configurada uma única vez no pacote com `recurso_sorte`, `custo_sorte` e `formas_sorte`; configurações específicas de um teste têm prioridade.
- `rules/PackageParser.kt` e `rules/RulePackage.kt`: validação do conjunto de propriedades globais, custo positivo, formas permitidas e existência do recurso, inclusive após herança de pacotes.
- `docs/PLANO_DESENVOLVIMENTO.md`: corrigida a descrição do fluxo narrativo de testes opostos/em grupo e registrada a configuração global de sorte.
- `docs/pacotes/FORMATO_PACOTE.md`: documentadas as três chaves globais e a prioridade da configuração específica por teste.
- `engine/AutomaticTimeline.kt`: a timeline passa a registrar alterações de inventário/equipamento, evolução da árvore e distribuição inicial. Eventos de aplicação/remoção de maldições ficam fora da projeção para não revelar efeitos ocultos. Os registros de combate agora incluem o dano efetivamente causado quando o personagem participa do confronto. Também registra o dano periódico de condições que afetam o personagem.
- `engine/Commands.kt`: uma viagem válida agora avança os relógios com `moveTick` no mesmo lote de eventos; relógios já completos são ignorados e o preenchimento é limitado ao total de casas. `app/ui/AppModel.kt` delega o deslocamento ao motor sem enviar avanços duplicados.
- `ai/Master.kt`: o contexto instrui o mestre a narrar a consequência de um relógio apenas quando ele conclui desde a última narração, sem repeti-la em turnos posteriores. O `GameLoop` identifica também a transição exata que cruza a metade, para não repetir a pista intermediária em cada avanço.
- `rules/PackageParser.kt` e `rules/RulePackage.kt`: alvos de poderes são compatíveis com o motor: cura e remoção de condição só em si mesmo; dano e aplicação de condição só em inimigo. A validação ocorre também após herança. O formato de pacote documenta as mesmas restrições.
- **Validação:** nenhum teste, build, lint ou CI executado, conforme a prioridade definida. Não declarar compilação ou integração confirmadas até a etapa final.

## 2026-10-22 — Árvores de habilidades e maldições: revisão de conteúdo (ainda não validada)

- `rules/.../habilidades-arvore.txt`: revisadas as seis primeiras habilidades das 50 árvores de classe e das 103 árvores de profissão; as culminâncias das 103 profissões receberam nomes e efeitos por ofício. Foram adicionados testes de catálogo para distinção de nomes, categorias, afinidade e progressão.
- `rules/.../maldicoes-lancadas.txt`: catálogo de maldições lançadas ampliado de 20 para 100 registros, separado dos conceitos/vertentes da Classe de Malditos. Cada registro declara efeitos positivos e negativos, resistência, duração e remoção.
- `engine/SkillTrees.kt` e `app/CombatBonuses.kt`: regra mecânica para penalidade social crescente de classes híbridas, com teste unitário.
- **Estado de validação:** alterações e testes foram escritos na branch `feature/arvores-habilidades`, mas ainda não há confirmação de que a suíte final, o build Android e a CI passaram para o conjunto mais recente. Nenhum resultado verde é declarado aqui. A auditoria de integração de efeitos, persistência e fluxo real das maldições continua pendente.

# Changelog

## 2026-10-21 — I-02.3: memórias narrativas por cena

- O `ContextAssembler` agora inclui memórias registradas somente para os NPCs presentes em `sceneNpcIds`, identificando o personagem pelo nome e mantendo notas fora da cena fora do prompt.
- Adicionado teste de regressão para inclusão da memória do NPC presente e exclusão de memória/identidade do NPC ausente.
- Removidos marcadores de conflito que haviam sido publicados acidentalmente durante o rebase da fase 14.
- Validação JVM: `:engine:test :rules:test :ai:test` aprovado.

## 2026-10-21 — Fase 14 concluída em código (DC-08)

- Testes novos: `SourceImportTest`, `SourceLibraryTest`, `SourcePipelineTest`, `CanonFactsTest` (`engine`) e `SourceFidelityTest` (`ai`). Correção: aspas sem escape no prompt de `RuleDetector`; o prompt agora pede limite `passivo`.
- Suíte completa **não executada**: o Gradle não conseguiu baixar a distribuição (sem rede). Lacunas da fase listadas no `PLANO_RPG_SOLO.md` (DC-08). Progresso ≈ 79%.

## 2026-10-21 — Fase 14: modos da fonte, resumo por título e regras detectadas (DC-05.2, DC-06, DC-07)

- `engine`: `SourceMode`, `SetSourceMode`, `SourceModeSet` e `GameState.sourceMode` (persistidos no log).
- `ai`: `SourceContext.modeInstruction`, `TitleSummarizer` e `RuleDetector`.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: DC-08 (testes de importação e fidelidade) e fim da Fase 14, com suíte completa. Progresso geral ≈ 75%.

## 2026-10-21 — Fase 14: pipeline, fatos canônicos e contexto por cena (DC-03, DC-04, DC-05.1)

- `engine`: `SourceIndex.kt` (`SourceChunker`, `SourceIndex`, `SourcePipeline`, `PipelineState`) e `CanonFacts.kt` (`CanonFact`, `CanonFacts`, `FactStatus`).
- `ai`: `SourceContext.build`; `ContextInput`, `PlayInput` e `PlayController` ganharam `sourceContext`.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: DC-05.2, DC-06 e DC-07. Progresso geral ≈ 75%.

## 2026-10-21 — engine: DOCX, PDF com texto e biblioteca de fontes (DC-01.2, DC-01.3, DC-02)

- `engine`: `DocxText` (`SourceDocx.kt`), `PdfText` (`SourcePdf.kt`) e `SourceLibrary`/`SourceEntry`/`SourceLink`/`SourceStatus` (`SourceLibrary.kt`); `SourceFormat` ganhou `DOCX` e `PDF` e `SourceImporter` os despacha.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: DC-03, DC-04 e DC-05.1. Progresso geral ≈ 75%.

## 2026-10-21 — engine: importação de TXT e Markdown (DC-01.1)

- `engine`: `SourceImporter`, `ImportedSource`, `SourceSection`, `SourceFormat` e `SourceImportResult` (`SourceImport.kt`): decodificação UTF-8/ISO-8859-1, normalização, seções por título Markdown, SHA-256 e limite de 5 MiB.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: DC-01.2 (DOCX). Fase 14 ≈ 7% interna; progresso geral segue ≈ 75%.

## 2026-10-21 — Fase 13 concluída em código (CP-05)

- `engine`: novos `CampaignPersistenceTest` (6) e `AdventureBookTest` (8): isolamento, reabertura, acentos, textos longos, falha de gravação e recuperação, livro e arquivo completo sem segredos.
- **Escritos, não executados** (sem acesso aos repositórios de dependências). Pendentes: teste instrumentado das migrações do Room, conferência visual do PDF e suíte completa. Progresso ≈ 75%.

## 2026-10-21 — app: salvar e compartilhar PDF (CP-04.3)

- `app`: seção "Exportar em PDF" (salvar pelo seletor de arquivos e compartilhar pelo sistema), `PdfShare`, `FileProvider` no manifesto e `res/xml/file_paths.xml`. Módulo CP-04 concluído.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-05 (testes da Fase 13).

## 2026-10-21 — engine/app: Arquivo completo em PDF (CP-04.2)

- `engine`: `CampaignArchiveBuilder` (personagem, inventário, descobertas, lugares e relações em palavras, sem segredos do mestre). `app`: `AppModel.campaignArchive()` e `campaignArchivePdf()`.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-04.3.

## 2026-10-21 — engine/app: Livro da aventura em PDF (CP-04.1)

- `engine`: `AdventureBookBuilder`, `AdventureBook` e `BookSection` (capítulos, missões e diário, sem segredos). `app`: `BookPdf.render` (PDF A4 via `PdfDocument`), `AppModel.adventureBook()` e `adventureBookPdf()`.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-04.2.

## 2026-10-21 — ai/app: briefing de retomada (CP-03)

- `ai`: `ResumeBriefing` (missões ativas, resumos de capítulos e linha do tempo recente, com aviso de que o estado oficial prevalece); `ContextInput`, `PlayInput` e `PlayController` ganharam `resumeBriefing`; o `GameLoop` envia só na primeira jogada depois de abrir a campanha. `app`: `AppModel` fornece o briefing.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-04.1.

## 2026-10-21 — data/app: tela de campanhas (CP-02)

- `data`: `campanhas` com `estado`, `criadaEm` e `atualizadaEm` (banco v7, migração 6→7); `EstadoCampanha`, `CampanhaInfo`, `RoomEventStore.infos/setState/touch`.
- `app`: lista com estado, datas e eventos; Continuar, Pausar, Concluir e Reativar; confirmação antes de apagar.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-03.

## 2026-10-21 — data: cópia de segurança antes de migrar (CP-01.4)

- `data`: `RpgDatabase.VERSION` e `backupBeforeMigration` (arquivo `.antes-v<versão>.bak` antes de abrir um banco mais antigo; sem destruição de dados em nenhum caminho).
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-02. Fecha CP-01.

## 2026-10-21 — engine/app: gravação confirmada e recuperação (CP-01.3)

- `engine`: `CampaignSession.saveError`, `isSaved` e `recover()`; falha de gravação fica registrada e relançada.
- `app`: `AppModel.saveError`, `retrySave()` e `saveMetadata()` que não derruba a jogada; aviso e botão "Tentar gravar de novo" na aba Campanhas.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-01.4.

## 2026-10-21 — data/app: tabela da linha do tempo (CP-01.2)

- `data`: nova tabela `linha_tempo` (banco v6, migração 5→6 copia o JSON antigo), `LinhaTempoDao`, `RoomEventStore.timeline/saveTimeline`; `delete` e `duplicate` cobrem a tabela. `app`: `AppModel` grava e restaura a linha do tempo pela tabela (JSON como reserva) e o import de backup a preenche.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: CP-01.3.

## 2026-10-21 — Fase 13: auditoria da persistência (CP-01.1)

- Documentação apenas, sem código. Auditoria da Fase 3: capítulos, missões e snapshots já têm tabelas (Room v5) e o quadro de fases foi corrigido. Lacunas listadas: estado/datas da campanha, `schemas/4.json` e `5.json`, gravação de log e metadados em transações separadas, sem testes do módulo `data`. Próximo: CP-01.2.

## 2026-10-21 — Fase 12 concluída em código (MU-08.4)

- Suíte completa **não executada**: sem acesso aos repositórios de dependências, o Gradle não resolveu plugins nem JUnit. Conferência estática dos testes novos feita; lacunas da fase listadas no `PLANO_RPG_SOLO.md` (3.7, MU-08.4). Progresso ≈ 71%.

## 2026-10-21 — ai: testes de paridade jogador × IA (MU-08.3)

- `ai`: novo `AiParityTest` (7 testes): em combate a IA não impõe `damage`, `heal`, `spend_resource`, `add_condition` nem `remove_condition` sobre jogador ou NPC (recusa idêntica, estado e PV intactos); fora da ordem do combate vale a regra anterior; narração pura segue permitida.
- **Escritos, não executados** (execução no MU-08.4). Próximo: MU-08.4 (suíte completa da fase e documentação das lacunas).

## 2026-10-21 — engine: testes de combate (MU-08.2)

- `engine`: novo `UnifiedCombatTest` (20 testes) para confronto ataque × defesa, crítico, ignorar defesa, registro no log com replay, recusas, paridade jogador × NPC, agregação de fontes e exceções mecânicas.
- **Escritos, não executados** (execução no MU-08.4). Próximo: MU-08.3 (paridade jogador × IA).

## 2026-10-21 — engine: testes de cálculo e rolagens (MU-08.1)

- `engine`: novo `EffectiveValueTest` (12 testes) cobrindo valor efetivo → expressão (3–12 e extensão), rolagens reproduzíveis, agregador de modificadores, duração rolada, fim de rodada e consumo de usos.
- **Escritos, não executados** (execução na suíte completa do MU-08.4). Próximo: MU-08.2 (testes de combate).

## 2026-10-21 — engine/rules/app: aquisição de magia (MU-07.3)

- `engine`: `LearnSpell`/`SpellLearned` (exige slot livre), `Quest.spellRewardId`/`spellGranted`, `QuestLog.markSpellGranted`, `awardQuestSpell` em `GameEngine` e `CampaignSession`; linha do tempo registra.
- `rules`: `magia_<id>_preco` e `magia_<id>_nome` em `criacao.txt` (`SpellDef`, `RulePackage.spells`), com validação (exige `moeda_item` e `niveis_slot_magia`); `FORMATO_PACOTE.md` atualizado.
- `app`: `InventoryActions.buySpell` (moedas + `LearnSpell` atômicos), `spellShopView` e seção "Magias" no `ShopPanel`.
- Módulo MU-07 concluído. ASSUMINDO: magia = id, nome e preço, sem efeito de conjuração; nenhum pacote embutido declara magias; sem tela para definir a magia da missão. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-08.1 (testes; a Fase 12 fecha com a suíte completa).

## 2026-10-21 — engine/rules/app: slots de magia (MU-07.2)

- `engine`: `EngineConfig.spellSlotLevels` e `spellSlotsFor`, `Entity.spells`, `SpellSlots` e `GameEngine.spellSlots`. `rules`: `niveis_slot_magia` em `criacao.txt` (validado, repassado por `engineConfig()`); `FORMATO_PACOTE.md` atualizado. `app`: `spellSlotsView` e seção "Slots de magia" na aba Campanhas.
- ASSUMINDO: 1 slot por nível-chave; slot = capacidade de magias aprendidas; nenhum pacote embutido declara níveis. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-07.3.

## 2026-10-21 — rules/app: ouro e prata (MU-07.1)

- `rules`: `moeda_ouro` e `prata_por_ouro` (2–1000, padrão 10) em `criacao.txt`, com validação; `RulePackage.goldItemId`, `silverPerGold` e `planPayment` (`CoinPayment`: prata primeiro, ouro no que faltar, troco em prata). Sistema próprio: item `moeda` renomeado para "Prata" e novo item `ouro` (1 ouro = 100 prata); preços inalterados (em prata).
- `app`: compra paga pelo plano de moedas (atômica); `convertCoins` (ouro↔prata); `ShopView` com ouro/prata e saldo total; botões de troca no `ShopPanel`. Venda segue em prata.
- ASSUMINDO: pacotes sem `moeda_ouro` não mudam; sem moedas iniciais nem loja de NPC. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-07.2.

## 2026-10-21 — engine: desvantagem opcional da habilidade (MU-06.4)

- `engine`: `AbilitySpec.drawbacks` (opcional, valores negativos; só existe quando o autor declara). `activateAbility` grava a desvantagem como efeito `habilidade:<id>:desvantagem`, com a mesma duração dos bônus; reativar substitui ambos.
- Módulo MU-06 concluído. ASSUMINDO: desvantagem expira junto com os bônus; sem pacote/interface. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-07.1.

## 2026-10-21 — engine: habilidade com gatilho, bônus e duração (MU-06.3)

- `engine` (`Abilities.kt`): `AbilityTrigger`, `AbilitySpec`, `ActiveEffect`, `ActiveEffectsProvider`, `GameEngine`/`CampaignSession.activateAbility`; comandos `LearnAbility`, `ActivateAbility`, `EndEffectsRound`; eventos `AbilityLearned`, `AbilityActivated`, `EffectsRoundEnded`; `Entity.abilities`, `GameState.activeEffects`; `GameEngine.rollDuration` (1d6 de rodadas no log). Linha do tempo registra.
- ASSUMINDO: duração fixa opcional, senão 1d6; reativar substitui; `EndEffectsRound` ainda não é chamado pelo ciclo de combate; sem arquivo de pacote nem interface (lacuna para MU-08.4). **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-06.4.

## 2026-10-21 — engine/rules: nível 2 da perícia por missão (MU-06.2)

- `engine`: `Entity.masteredSkills`, `MasterSkill`/`SkillMastered` (vagas = níveis-chave alcançados − perícias liberadas), `EngineConfig.masteryLevels`, `Quest.masterySkillId`/`masteryGranted`, `QuestLog.markMasteryGranted`, `GameEngine`/`CampaignSession.awardQuestMastery`; linha do tempo registra.
- `rules`: `niveis_chave_pericia` em `criacao.txt` (validado; repassado por `engineConfig()`); coluna opcional 10 `nivel2` em `tracos.txt` (`level2Bonuses`, só perícias) aplicada por `SkillModifierProvider`; `FORMATO_PACOTE.md` atualizado.
- ASSUMINDO: nenhum pacote embutido declara níveis-chave (sem eles nunca há vaga); sem interface/proposta para definir a perícia liberada pela missão. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-06.3.

## 2026-10-21 — rules: perícia com bônus condicional (MU-06.1)

- `rules`: coluna opcional 9 `condicional` em `tracos.txt` (`escopo:+N@item;...`) → `TraitDef.conditionalBonuses` (`ConditionalBonus`); validação: só perícias, item existente, valor ≠ 0. Novo `SkillModifierProvider` (`ModifierProvider`): com a perícia > 0 e o item equipado, devolve o modificador de origem `pericia:<id>:<item>` para qualquer entidade. `FORMATO_PACOTE.md` atualizado.
- ASSUMINDO: efeito só no confronto unificado; nenhuma perícia embutida declara ainda; sem mudança no d20 nem nos testes por pontos. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-06.2.

## 2026-10-21 — rules/app: status do sistema próprio sem teto (MU-05.3)

- `rules`: `criacao.txt` do sistema próprio declara `pontos_status_por_nivel|1` e `pontos_habilidade_por_nivel|1`.
- `app`: `AppModel.statPointsState`/`increaseStatPoint` (comando `AllocateStatPoints`) e seção "Pontos de status e habilidade" na aba Campanhas: +1 por atributo, sem teto; pontos de habilidade só são exibidos até MU-06.
- Módulo MU-05 concluído. ASSUMINDO: status = atributos existentes (`poder`, `habilidade`, `resistencia`); `pontos_por_nivel` legado mantido. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-06.1.

## 2026-10-21 — engine/rules: dois pontos por nível (MU-05.2)

- `engine`: reservas separadas `Entity.statPoints` e `Entity.abilityPoints` (padrão 0); `EngineConfig.statPointsPerLevel`/`abilityPointsPerLevel` (0–100); `LevelGained` ganhou `statPointsGranted`/`abilityPointsGranted` (padrão 0, logs antigos válidos); `AllocateStatPoints` + `StatPointsAllocated` (1 ponto = +1 em atributo existente, sem teto; recusa por saldo, valor ≤ 0, atributo ausente ou entidade morta); `validateState` recusa saldos negativos; linha do tempo automática registra o investimento.
- `rules`: chaves opcionais `pontos_status_por_nivel` e `pontos_habilidade_por_nivel` em `criacao.txt` (validadas no parser; repassadas por `engineConfig()`); `FORMATO_PACOTE.md` atualizado.
- `app`: `progressionHistory` mostra os pontos de status/habilidade concedidos e o investimento em status.
- ASSUMINDO: `pontos_por_nivel`/`AllocateAdvancement` intactos; nenhum pacote embutido declara as novas chaves (MU-05.3); gasto de habilidade em MU-06.3. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-05.3.

## 2026-10-21 — engine/app: XP por missão (MU-05.1)

- `engine`: `Quest.xpReward` e `Quest.rewarded` (padrões 0/false: dados antigos carregam), `QuestLog.markRewarded`, `EngineConfig.maxQuestXp(nível)` (teto = XP para subir um nível), `GameEngine.awardQuestXp` (missão DONE, valor positivo, dentro do teto, uma vez, via `GainXp`, que já concede nível e pontos). `CampaignSession` ganhou `awardQuestXp`, `unifiedAttack` e `unifiedAttackBetween` (persistem o log).
- `app`: `AppModel.finishQuest` concede o XP ao concluir e devolve o motivo se for recusado.
- ASSUMINDO: teto de um nível por missão. Pendente: campo de recompensa na criação da missão e proposta do mestre. XP por monstro (Fase 2.3) inalterado. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-05.2.

## 2026-10-21 — app: cartão do confronto (MU-04.2)

- `app`: novo `ui/ClashCard.kt`, composable `ClashCard(report: ClashReport)`. Apresenta o detalhamento do motor (origens, dados, crítico, ignorar defesa, defesa e dano final), sem recalcular, sem animação, com `contentDescription` para leitor de tela. Usa `AppCard`, `Pill` e `Muted`.
- Ainda não ligado à `PlayScreen`: depende de o controlador chamar `unifiedAttackBetween` e de fontes reais de modificadores. Strings fixas em português (U-03 as move para `strings.xml`).
- Módulo MU-04 concluído. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-05.1.

## 2026-10-21 — engine: detalhamento do confronto (MU-04.1)

- `engine`: novo `ClashReport.kt` (`ClashReport.of(ClashRecorded)`, `lines()`), gerado só do evento persistido. `ClashRecorded` ganhou campos opcionais (dados do ataque e da defesa, limiar e rolagem de crítico e de ignorar defesa, garantias); logs sem eles continuam válidos. `unifiedAttack` preenche os novos campos.
- ASSUMINDO: texto em português fixo no motor por ora (localização em U-03). **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-04.2 (cartão na interface).

## 2026-10-21 — engine: exceções mecânicas explícitas (MU-03.3)

- `engine` (`MechanicalExceptions.kt`): `MechanicalException` (id, entidade, descrição, modificadores, imunidades) e `ExceptionRegistry` (`validate`, `modifiers` como `ModifierProvider`, `isImmune`). Exceção sem descrição, sem efeito, repetida ou com entidade inexistente é inválida. Bônus entram com origem `excecao:<id>:<n>` e rótulo com a descrição, visíveis no detalhamento. A IA não cria exceções.
- Módulo MU-03 concluído. Pendente: carregar exceções do pacote e ligar `isImmune` à aplicação de condições. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-04.1.

## 2026-10-21 — engine: mesmo serviço para todos os combatentes (MU-03.2)

- `engine`: `ModifierProvider` (fonte de modificadores por entidade e característica), `CombatantValues.breakdown/modifiers` (stat-base + fontes agregadas, sem ramificar por `EntityKind`) e `GameEngine.unifiedAttackBetween(attackerId, targetId, providers, …)`, que monta ataque, defesa, bônus de crítico e de ignorar defesa a partir do estado e chama `unifiedAttack`.
- ASSUMINDO: stats de combate `attack` e `defense` por padrão. Sem fontes reais ainda (traços, perícias, itens, condições): MU-05/MU-06. Pedido de ataque da IA ligado ao controlador ainda pendente.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-03.3.

## 2026-10-21 — ai: IA só com intenção durante o combate (MU-03.1)

- `ai` (`TurnRunner`): proposta `damage`, `heal`, `spend_resource`, `add_condition` ou `remove_condition` sobre entidade em `combat.order` (jogador ou NPC) é recusada e a IA tenta de novo. Prompt do mestre (`ProposalSchema`) instrui a propor só ação e alvo e a nunca informar números de dano, rolagem ou acerto.
- ASSUMINDO: fora de combate o comportamento de NPCs não muda. O pedido de ataque da IA ligado ao `unifiedAttack` vem com o MU-03.2.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-03.2.

## 2026-10-21 — engine: confronto unificado no estado e no histórico (MU-02.4)

- `engine`: `GameEngine.unifiedAttack(UnifiedAttackSpec)` com a validação do `attack` (combate ativo, turno, vivos, alvo no combate, recurso), rolagens gravadas como `RollRecorded` (mantém a reprodução determinística), evento novo `ClashRecorded` (valores, expressões, totais, crítico, defesa ignorada, dano, origens aplicadas e rejeitadas), `AttackResolved` e dano pelo comando `Damage`, com rollback em recusa. `UnifiedAttackSpec`/`UnifiedAttackResult` em `UnifiedCombat.kt`; `DiceExpression.toString` legível.
- `GameEngine.attack` (d20) não foi alterado. Os breakdowns ainda são entregues por quem chama; a montagem a partir do estado fica para MU-03/05/06.
- Módulo MU-02 concluído. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-03.1.

## 2026-10-21 — engine: ignorar defesa (MU-02.3)

- `engine` (`UnifiedCombat.kt`): `IgnoreDefenseSpec`, `IgnoreCheck`, `checkIgnore` e parâmetro `ignoreDefense` em `resolve`. d10 silencioso, acerta em 10, bônus `ignoreDef` reduzem o limiar (mín. 1), `guaranteed` = 100% sem rolagem. Sucesso: dano = valor efetivo cheio × 150% (configurável), sem dado e sem defesa. `ClashResult.defenseRoll` agora é nulo quando a defesa é ignorada.
- ASSUMINDO: ignorar defesa tem prioridade sobre crítico; 150% do dano cheio; escudo ainda inexistente; recarga só informada (aplicação no MU-06.3).
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-02.4.

## 2026-10-21 — engine: crítico (MU-02.2)

- `engine` (`UnifiedCombat.kt`): `CritCheck` e `UnifiedCombat.checkCrit`. d6 silencioso; crítico quando o dado ≥ limiar (base 6, reduzido 1 por ponto de bônus `crit`, mínimo 1). Chance garantida (`guaranteedCrit`) pula a rolagem. No crítico o ataque vale o valor efetivo cheio, sem dado; a defesa rolada ainda é subtraída (ASSUMINDO). `resolve` ganhou `critModifiers`, `guaranteedCrit` e `canCrit`; `ClashResult` ganhou `crit` e `attackRoll` nulo no crítico.
- Sem mudança em `GameEngine.attack` nem em outros módulos. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-02.3 (ignorar defesa).

## 2026-10-21 — engine: confronto ataque × defesa (MU-02.1)

- `engine`: novo `UnifiedCombat.kt` com `UnifiedCombat.resolve(ataque, defesa, dice)` e `ClashResult`. Ataque e defesa usam a mesma conversão valor → expressão e o mesmo `Dice`; `damageOf = max(0, ataque − defesa)`, defesa subtraída uma única vez, sem dano mínimo.
- `GameEngine.attack` (d20) e demais regras existentes não foram alteradas; a ligação com PV, estado e histórico fica para MU-02.4 e MU-03.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-02.2 (crítico).

## 2026-10-21 — engine: duração rolada e expiração (MU-01.4)

- `engine` (`Modifiers.kt`): `EffectDuration.rollRounds` (1d6 de rodadas pelo `Dice` determinístico, sem animação), `withRolledDuration` (grava o sorteio em `Modifier.remainingRounds`), `endOfRound` (desconta 1 e remove os expirados; sem limite permanece) e `consumeUse` (efeitos por usos). `RolledDuration` guarda o valor e a rolagem para o histórico.
- ASSUMINDO: expira ao fim da última rodada sorteada. Durações por combate e por cena, o registro em evento e a chamada de `endOfRound` no ciclo de jogo ficam para o MU-02.4 e seguintes.
- Módulo MU-01 concluído (01.1 a 01.4). **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-02.1.

## 2026-10-21 — engine: agregador de modificadores (MU-01.3)

- `engine`: novo `Modifiers.kt` com `Modifier` (ID estável da origem, característica, valor, rodadas/usos restantes, `stackable`), `ModifierAggregator.aggregate(target, base, modifiers)` e `EffectiveBreakdown` (base, aplicados, rejeitados com motivo EXPIRED ou DUPLICATE_SOURCE, valor e expressão). Fontes diferentes acumulam, valores iguais de fontes independentes acumulam, mesma origem não repete.
- ASSUMINDO: a primeira duplicata da lista é a mantida. Ainda sem ligação às fontes existentes (traços, itens, condições).
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-01.4.

## 2026-10-21 — engine: extensão fora de 3–12 (MU-01.2)

- `EffectiveValue.expressionFor` agora é total (não devolve mais `null`): valor ≥ 6 → dados = valor ÷ 6, bônus = resto; valor < 6 → 1d6 − (6 − valor). Reproduz a regra original em 3–12. Constante `MAX_DICE = 100`.
- ASSUMINDO: valores < 3 e > 12 seguem a extensão acima; acima de 600 o excedente vira bônus. Queda de média entre 11 (1d6+5) e 12 (2d6) mantida e documentada.
- **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-01.3.

## 2026-10-21 — engine: valor efetivo → expressão de dados (MU-01.1)

- `engine`: novo `EffectiveValue.kt` com `DiceExpression(count, sides, modifier)` e `EffectiveValue.expressionFor(valor)`: 3–11 → 1d6 + (valor − 6); 12 → 2d6. Fora de 3–12 devolve `null` (MU-01.2 define a extensão). `DiceExpression.roll(dice)` usa o `Dice` determinístico existente.
- Nenhum comportamento existente alterado. **Escrito, não compilado nem executado; sem testes novos.** Próximo: MU-01.2.

## 2026-10-21 — plano: fases 12 a 14 (somente documentação)

- `PLANO_RPG_SOLO.md`: Fase 12 (motor unificado, progressão e combate; MU-01 a MU-08, com submódulos .1, .2…), Fase 13 (campanhas persistentes e PDF; CP-01 a CP-05) e Fase 14 (biblioteca de fontes e RPG baseado em documentos; DC-01 a DC-08). Progresso geral recalculado: ≈ 68% (9,5 de 14), só porque o denominador cresceu.
- ASSUMINDO: itens marcados na seção 3.7 aguardam confirmação. I-02.3 e I-02.4 pausados.
- Nenhum código alterado.
- Fase 12: habilidades podem garantir 100% de chance de ignorar defesa e de crítico (rolagem pulada); entra no MU-02.3.

## 2026-10-21 — ai: proposta `record_memory` (I-02.2)

- `ai`: tipo `record_memory(subjectId, text)` em `ProposalSchema.TYPES`, no prompt do mestre e no `ProposalParser` (mapeia para `NoteMemory`); limites e existência do assunto são validados pelo motor.
- ASSUMINDO: segue as regras das demais propostas (lote `proposals`); sem exibição ao jogador.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: I-02.3 (notas no contexto do mestre).

## 2026-10-21 — engine: notas de memória por NPC e lugar (I-02.1)

- `engine`: comando `NoteMemory(subjectId, text)`, evento `MemoryNoted` e `GameState.memories` (`MEMORY_NOTES_PER_SUBJECT = 5`, `MEMORY_NOTE_MAX_CHARS = 200`). Recusa nota vazia, acima do limite, repetida em seguida ou de assunto que não seja NPC/lugar existente. Ao passar de 5 notas, a mais antiga cai.
- ASSUMINDO: sem proposta da IA, contexto do mestre nem interface ainda; estado antigo sem `memories` carrega com mapa vazio (valor padrão).
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: I-02.2 (`ai`: pedido `record_memory`).

## 2026-10-21 — engine: NPC apresentado na linha do tempo (I-01.2)

- `engine`: `automaticTimeline` registra "Você conheceu <nome>." (categoria `pessoa`) em `EntityCreated` de NPC; só o nome, sem ficha, dossiê ou segredos. O id do NPC já aparece no contexto do mestre.
- ASSUMINDO: todo NPC criado no log foi apresentado na narração; NPCs de combate também geram a entrada.
- **Escrito, não compilado nem executado; sem testes novos.** I-01 concluído no código; falta testes e CI. Próxima: I-02 (memória longa).

## 2026-10-21 — ai: pedido de NPC isolado (I-01.1)

- `ai`: campo `request_npc` (mesmos campos de `create_npc` + `id` opcional em slug `[a-z0-9-]{2,40}`) em `ProposalParser`/`ProposalSchema`; `TurnOutput.requestNpc`. Exclusivo com `create_npc`, `proposals`, testes/rolagens, `request_power` e `request_combat`. `TurnRunner` usa o `id` informado (recusa se já existir ou for o do jogador) ou gera o id automático; a ficha passa por `NpcFactory` e é criada pelo motor.
- ASSUMINDO: não inicia combate; `id` opcional também vale em `create_npc`; sem exibição própria do NPC ao jogador ainda.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: I-01.2 (id do NPC no contexto do mestre e registro no diário).

## 2026-10-21 — dados: tabela empírica e correção de `pointFaces` (M-06.5)

- `docs/MECANICA_DADOS.md`: tabela empírica recalculada por `tools/simulate_checks.py --runs 100000` e anexada.
- `engine`: `pointFaces` corrigido para `max(pontos,0)+1` (limitado a lados−1), alinhando o código à regra documentada e ao simulador (antes 0 e 1 ponto davam 1 face); `pointsAtDieMax` usa as faces efetivas.
- ASSUMINDO: testes antigos que supunham o empate 0/1 ponto precisarão de ajuste na validação.
- **Escrito, não compilado nem executado; sem testes novos.** M-06 concluído no código; falta testes e CI.

## 2026-10-21 — ai/app: chance no cartão de rolagem (M-06.4)

- `ai`: `RollView.chance` preenchido por `CheckSpec.successChance()` (nulo em rolagem simples, vantagem/desvantagem e dados explosivos). `app`: `RollCard` mostra "chance de sucesso: N%".
- ASSUMINDO: a chance aparece no cartão do chat; sem prévia antes de escolher a dificuldade.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-06.5 (recalcular a tabela empírica).

## 2026-10-21 — engine: chance calculada pelo motor (M-06.3)

- `engine`: `CheckSpec.successChance()` (`CheckOdds.kt`) devolve a chance exata de sucesso pleno enumerando a soma dos dados e usando `resolveCheck`. `MECANICA_DADOS.md` atualizado.
- ASSUMINDO: ignora vantagem/desvantagem e dados explosivos; até 10 dados; sem exibição na interface ainda.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-06.4 (exibir a chance na interface).

## 2026-10-21 — dados: crítico e complicação em testes de pontos (M-06.2)

- Decisão: o sistema próprio não declara crítico nem complicação em `pericia`/`esquiva`; faixas em `POINTS` seguem suportadas pelo motor sobre a face natural, com `crit_falha` prioritário. `MECANICA_DADOS.md` e `FORMATO_PACOTE.md` atualizados.
- ASSUMINDO: sem código novo; decisão reversível por conteúdo.
- **Sem alteração de código; nada compilado nem executado.** Próxima: M-06.3 (calculadora de chance pelo motor).

## 2026-10-21 — engine: "já no máximo neste dado" (M-06.1)

- `engine`: `pointsAtDieMax(points, sides)`; `resolvedPool` acrescenta " · já no máximo neste dado" ao rótulo do teste de pontos quando `pontos >= lados - 1`. `MECANICA_DADOS.md` atualizado.
- ASSUMINDO: só o rótulo muda (chance e rolagem intactas); vale para qualquer dado.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-06.2 (crítico e complicação no d6 por pontos).

## 2026-10-21 — condições: remoção por teste (M-05)

- Conferido: `envenenado` (1 PV × 3 rodadas) e `queimando` (2 PV × 2 rodadas) já existem no sistema próprio com duração e dano por rodada.
- `rules`: coluna 8 opcional `remocao` em `condicoes.txt` (`teste[:dificuldade]`; `ConditionDef.removalCheck/removalDifficulty`, validada no parser) e `RulePackage.conditionRemovalSpec`. Sistema próprio: `sangramento` (`pericia:facil`), `envenenado` (`pericia:media`), `queimando` (`esquiva:facil`).
- `engine`: `removeConditionByCheck` (`ConditionRemoval.kt`; sucesso remove, parcial/falha mantém; atômico). `ai`: `PlayController.removeConditionByCheck` (trava de turno, chat, desfazer). `app`: `AppModel.removableConditions/removeCondition` e `ConditionRemovalPanel` na aba Jogar.
- ASSUMINDO: só o jogador tenta (a IA continua com `remove_condition` direto); não consome turno de combate; testes e dificuldades escolhidos para o sistema próprio são de conteúdo, ajustáveis.
- **Escrito, não compilado nem executado; sem testes novos.** M-05 concluído no código; falta testes e CI. Próxima: M-06 (ajustes de dados).

## 2026-10-21 — ai/app: fluxo do teste de sobrevivência (M-04.5)

- `PlayController.survivalTest` (trava de turno, chat, desfazer reverte, sem IA) chama `session.survivalTest`; `AppModel.survivalLimit/survivalFailures/survivalTest` e `SurvivalPanel` na aba Jogar (só com herói caído e pacote que declara o teste; via `act`, desabilitado em `busy`/pendências).
- ASSUMINDO: só o jogador dispara (a IA não pede); sem consumo de turno de combate; nenhum pacote embutido declara o teste, então o painel não aparece.
- **Escrito, não compilado nem executado; sem testes novos.** M-04 concluído no código para o escopo atual; falta testes e CI. Próxima: M-05 (condições).

## 2026-10-21 — rules: teste de sobrevivência no pacote (M-04.4)

- `manifest.txt`: chaves opcionais `teste_sobrevivencia`, `dificuldade_sobrevivencia` e `falhas_morte` (1–10) em `Manifest.survivalCheck/survivalDifficulty/survivalFailures`; `PackageParser` valida (teste e dificuldade existentes; `DOWNED` exige `teste_sobrevivencia`); herança do pai em `PackageRegistry.merge`. `RulePackage.survivalSpec(stats, skills)` e `survivalFailureLimit` (padrão 3) alimentam `survivalTest`. `FORMATO_PACOTE.md` atualizado.
- ASSUMINDO: nenhum pacote embutido declara (sistema próprio segue `PERMADEATH`); sem chamada em fluxo de jogo ainda (M-04.5).
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-04.5 (fluxo de jogo e interface do teste de sobrevivência).

## 2026-10-21 — engine: testes de sobrevivência (M-04.3)

- `Entity.deathFailures`, `SurvivalFailed`, `FailSurvival` (morre ao atingir o limite) e `survivalTest` em `GameEngine`/`CampaignSession` (`Survival.kt`): sucesso pleno reergue com 1 no recurso de morte, parcial mantém, falha acumula; atômico.
- ASSUMINDO: teste e limite vêm de quem chama (pacote em M-04.4); sem chamada em fluxo de jogo ainda.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-04.4 (pacote declara teste e limite).

## 2026-10-21 — rules: modo de morte no pacote (M-04.2)

- `manifest.txt`: chave opcional `modo_morte` (`PERMADEATH`/`DOWNED`) em `Manifest.deathMode`; `PackageParser` recusa valor inválido e `DOWNED` sem `morte`; `PackageRegistry.merge` herda do pai; `RulePackage.engineConfig()` repassa ao motor (padrão `PERMADEATH`). `FORMATO_PACOTE.md` atualizado.
- ASSUMINDO: nenhum pacote embutido declara o modo (sistema próprio segue `PERMADEATH`); sem escolha por campanha ainda.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-04.3 (escolha na campanha e testes de sobrevivência).

## 2026-10-21 — engine: estado caído (M-04.1)

- `DeathMode { PERMADEATH, DOWNED }` em `EngineConfig.deathMode` (padrão `PERMADEATH`); `Entity.downed`; eventos `EntityDowned`/`EntityRecovered`; `EngineConfig.zeroEvent` decide queda ou morte em `Damage` e no tick de condições. `Heal` acima de 0 recupera; `Revive` limpa; caído não ataca nem ativa poder; linha do tempo automática registra queda e recuperação.
- ASSUMINDO: só PLAYER cai (NPC morre direto); dano > 0 em caído mata; combate não encerra por jogador caído; outros comandos ainda não bloqueiam caído; ninguém escolhe o modo ainda (M-04.2).
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-04.2 (modo por pacote/campanha e testes de sobrevivência).

## 2026-10-21 — rules/ai: perfis de perigo (M-03.6)

- Novo `perigos.txt` (`id|nome|descricao|percentual`): `HazardDef`, `PackageFiles.HAZARDS`, validação 1–100, herança por id, carga em `BundledContent`. Sistema próprio: `leve` 15%, `medio` 30%, `grave` 60%, `mortal` 100% da vida máxima.
- `RulePackage.hazardDamage`; `RollEntry.hazard` (face de `request_roll` com `"hazard":"<id>"` no lugar de `damage`); `CheckMenu.validateRoll`/`rollDamage`/`describe`; `GameLoop` aplica o dano calculado; prompt do mestre e `Master.rollNote` atualizados. `FORMATO_PACOTE.md` atualizado.
- ASSUMINDO: percentual da vida máxima do jogador; só em `request_roll`; NPCs fora; `hazard` e `damage` > 0 não combinam.
- **Escrito, não compilado nem executado; sem testes novos.** M-03 concluído no código. Próxima: M-04 (morte e recuperação).

## 2026-10-21 — app: interface de compra e venda (M-03.5)

- `AppModel.shopView()` (`ShopView`, `ShopEntry`) e `InventoryView.price`; `ShopPanel` na aba Jogar com saldo e "Comprar" (desabilitado sem saldo ou em `busy`); botão "Vender (preço)" no inventário para item com preço e não equipado. Tudo via `act` (trava de turno).
- ASSUMINDO: 1 unidade por toque; loja lista todo item com preço do pacote; painel oculto sem moeda.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-03.6 (perfis de perigo).

## 2026-10-21 — app/rules: compra e venda (M-03.4)

- `RulePackage.currencyItemId` (lê `moeda_item`); `InventoryActions.buyItem/sellItem` com troca atômica de moeda por item via `executeAll`; `AppModel.buyItem/sellItem` delegam. Recusas: saldo ou estoque insuficiente, item equipado (regra do motor), item sem preço, a própria moeda, pacote sem moeda, quantidade fora de 1–999, combate.
- ASSUMINDO: preço único (venda = compra); sem loja/estoque de NPC; sem pedido da IA; sem mudança no motor.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-03.5 (interface de compra e venda).

## 2026-10-21 — rules: dinheiro e preços (M-03.3)

- `ItemDef.price` (coluna 7 opcional de `itens.txt`, 1–1000000) com validação no `PackageParser` e em `RulePackage`; `moeda_item` em `criacao.txt` aponta o item de moeda (narrativo, sem preço; obrigatório se houver preço). `max` de colunas de `itens.txt` passou de 6 para 7. `FORMATO_PACOTE.md` atualizado.
- Sistema próprio: item `moeda` e preço em 18 itens.
- ASSUMINDO: saldo = quantidade de `moeda` no inventário; preço único; sem moedas iniciais; sem mudança no motor.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-03.4 (compra e venda).

## 2026-10-21 — rules: consumíveis (M-03.2)

- `itens.txt`: +4 consumíveis (poção de cura maior, bandagem, poção de mana, tônico de vigor). `criacao.txt`: `item_<id>_acao|recurso|quantidade` (cura pv+10, pv+2, pm+5, pa+2). Só dados; sem mudança de código.
- ASSUMINDO: só a ação `cura` (única suportada por `InventoryActions` e pelo validador); sem preço.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-03.3 (dinheiro, compra e venda).

## 2026-10-21 — rules: armas e armaduras (M-03.1)

- `itens.txt` do sistema próprio: +11 equipamentos (adaga, clava, lança, machado, arco curto, espada longa, escudo pesado, roupa reforçada, armadura de couro, cota de malha, capacete) com efeitos `ataque`/`dano`/`defesa`; slots `mao`, `outra-mao`, `corpo`, `cabeca`. Só dados; sem mudança de código.
- ASSUMINDO: peso como `ataque:-1` (machado, cota de malha); combate segue com o perfil `golpe`; sem preço, consumíveis ou perfis de perigo (M-03.2 em diante).
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-03.2 (consumíveis).

## 2026-10-21 — app: origem dos bônus do catálogo (M-02.8)

- `CharactersScreen`: o seletor e o "Resumo das escolhas" mostram cada efeito do catálogo como `Efeito aplicado nos testes (origem: <nome da escolha>): <escopo ±N>`; o resumo passou a listá-los. Removido o texto desatualizado "ainda não aplicado pelo motor" (os efeitos já entram nos testes e no combate via `selectedCatalogBonuses`).
- Rótulo de benefício descrito ajustado para "sem efeito automático, salvo o efeito listado abaixo".
- **Escrito, não compilado nem executado; sem testes novos.** M-02 concluído no código; falta CI.

## 2026-10-21 — rules: efeitos tipados nas maldições (M-02.7)

- `personagem-catalogo.json`: os 4 benefícios `+1 em Intimidação` (`maldicao-010`, `-040`, `-070`, `-100`) declaram `mechanicalEffects` (`intimidacao` +1). Só dados; sem mudança de código.
- ASSUMINDO: só benefício numérico incondicional em perícia existente. Ficam narrativos: bônus em Destreza/Força (não são escopos do pacote), condicionais (escuro, luz, caminhar, alvo), resistências, dano e perícias sem escopo (Enganação, Prestidigitação); desvantagens sem número.
- **Escrito, não compilado nem executado; sem testes novos.** M-02 concluído no código para o escopo atual; falta CI.

## 2026-10-21 — rules: efeitos tipados nos híbridos (M-02.6)

- `personagem-catalogo.json`: os 20 híbridos declaram `mechanicalEffects` com +1 em uma perícia, sempre aplicável. Só dados; sem mudança de código.
- ASSUMINDO: perícia = a da segunda raça de `raceIds` (a raça de origem da primeira já vem da escolha de raça); soma aos demais bônus. Maldições seguem sem efeitos (têm `benefits`/`drawbacks` próprios, a tratar em M-02.7).
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-02.7 (maldições).

## 2026-10-21 — rules: efeitos tipados nas profissões (M-02.5)

- `personagem-catalogo.json`: as 103 profissões declaram `mechanicalEffects` com +1 em uma perícia, sempre aplicável. Só dados; sem mudança de código. Texto de `Perito em Arcos` ajustado (já não diz "sem bônus automático").
- ASSUMINDO: perícia escolhida pelo ofício, sem escopo de combate (nenhum `pontaria`); soma aos bônus de raça, sub-raça e classe. Maldições e híbridos seguem sem efeitos.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-02.6 (híbridos).

## 2026-10-21 — rules: efeitos tipados nas classes (M-02.4)

- `personagem-catalogo.json`: as 50 classes declaram `mechanicalEffects` com +1 em uma perícia, sempre aplicável. Só dados; sem mudança de código.
- ASSUMINDO: só escopos de perícia (nenhum de combate), para não deslocar o equilíbrio de ataque/dano/defesa antes de M-06; perícia escolhida pelo resumo da classe; soma aos bônus de raça e sub-raça. Profissões, maldições e híbridos seguem sem efeitos.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-02.5 (profissões).

## 2026-10-21 — rules: efeitos tipados nas sub-raças (M-02.3)

- `personagem-catalogo.json`: as 50 sub-raças declaram `mechanicalEffects` com +1 em uma perícia, sempre aplicável. Só dados; sem mudança de código.
- ASSUMINDO: o catálogo não infere herança, então o bônus da sub-raça soma ao da raça escolhida; a perícia foi escolhida pelo lore e é distinta da da raça-base, exceto onde indicado no lore. Classes, profissões, maldições e híbridos seguem sem efeitos.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-02.4 (classes).

## 2026-10-21 — rules: efeitos tipados nas demais raças (M-02.2)

- `personagem-catalogo.json`: as 45 raças restantes declaram `mechanicalEffects` com +1 em uma perícia, sempre aplicável (50 de 50 raças). Só dados; sem mudança de código.
- ASSUMINDO: perícia escolhida pelo lore de cada raça; sem efeito negativo nem condicional. Sub-raças, classes, profissões, maldições e híbridos seguem sem efeitos.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-02.3 (sub-raças).

## 2026-10-21 — rules: primeiros efeitos tipados de raças (M-02.1)

- `personagem-catalogo.json`: `mechanicalEffects` declarado em 5 raças base, +1 em uma perícia cada, sempre aplicável — `humano` (persuasao), `elfo` (percepcao), `anao` (oficios), `orcano` (lideranca), `pequenino` (sobrevivencia). Escopos e origem já suportados pelo contrato de R2.1; sem mudança de código.
- Campos `status` e `effectsPolicy` do catálogo atualizados.
- ASSUMINDO: perícia escolhida pelo lore da raça; sub-raças, classes, profissões, maldições e híbridos seguem sem efeitos.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-02.2 (demais raças).

## 2026-10-21 — app: marcação mecânico/narrativo na ficha salva (M-01.8)

- `CharactersScreen`: a lista de traços da biblioteca usa `traitTag` (`[efeito: escopo ±N]` ou `[narrativo]`); poderes e perícias sem etiqueta.
- ASSUMINDO: usa o pacote da ficha; traço fora do pacote mostra só o id.
- **Escrito, não compilado nem executado; sem testes novos.** M-01 concluído no código para o escopo atual.

## 2026-10-21 — rules/ai: imunidade a condições por traço (M-01.7)

- Escopo `imune_<condição>` em `tracos.txt`; `RulePackage.isImmuneToCondition`; `PowerMenu.build` e `TurnRunner` recusam condição a imune.
- Correção M-01.6: `PackageParser` aceitava só os escopos antigos; agora aceita `max_<recurso>` e `imune_<condição>`.
- `FORMATO_PACOTE.md` atualizado. ASSUMINDO: não remove condição existente; nenhum traço embutido declara `imune_*`.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-01.8 marcação mecânico/narrativo na ficha salva.

## 2026-10-21 — rules: bônus de recurso máximo por traço (M-01.6)

- Escopo `max_<recurso>` em `tracos.txt`; `resourcesFor(stats, traitIds)` soma ao máximo derivado (criação e adaptação); validação do escopo contra os recursos do pacote; `FORMATO_PACOTE.md` atualizado.
- ASSUMINDO: não retroativo; nenhum traço embutido declara `max_*`.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-01.7 imunidades.

## 2026-10-21 — ai/app: gasto de uso de traço na jogada (M-01.5)

- `PlayController.spendTraitUse`: `SpendTraitUse` do herói com limite do pacote, sob `turnGate`, linha no chat e entrada desfazível.
- `AppModel.traitUsesView/spendTraitUse` e `TraitUsesPanel` (aba Jogar): lista traços com `usos`, mostra usos gastos/limite e botão desabilitado se esgotado ou ocupado.
- ASSUMINDO: só o jogador gasta o uso (sem `request` da IA); o gasto não aplica efeito; nenhum traço embutido declara `usos`.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-01.6 imunidades/bônus de recurso máximo.

## 2026-10-21 — rules/ai: usos de traços no pacote e reset no descanso (M-01.4)

- `tracos.txt`: coluna opcional 8 `usos` (1–99); `TraitDef.uses`; validação no `PackageParser`; `FORMATO_PACOTE.md` atualizado. Pacotes antigos seguem válidos.
- `LocalActions.rest`: `ResetTraitUses` do herói no mesmo lote do descanso.
- ASSUMINDO: nenhum traço embutido declara `usos` ainda.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: ligar o gasto de uso à jogada (M-01.5).

## 2026-10-21 — engine: usos limitados de traços (M-01.3)

- `GameState.traitUses`; eventos `TraitUseSpent` e `TraitUsesReset`; comandos `SpendTraitUse(entityId, traitId, limit)` e `ResetTraitUses(entityId, traitIds)`. Recusas: entidade inexistente ou morta, traço ausente, limite inválido, usos esgotados, nada a devolver.
- ASSUMINDO: limite informado por quem chama; sem tipo de período no motor; logs, backups e snapshots antigos continuam válidos.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-01.4 declarar usos no pacote e ligar o reset ao descanso.

## 2026-10-21 — app: marcação de traços mecânicos x narrativos (M-01.2)

- `CharactersScreen.TraitRow`: etiqueta `efeito: <escopo ±N>` quando o traço tem bônus declarado; etiqueta "narrativo" para vantagens/desvantagens sem bônus. Poderes e perícias não recebem a etiqueta.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: usos por cena/descanso (M-01.3).

## 2026-10-21 — rules: primeiros efeitos tipados de traços (M-01.1)

- `sistema-proprio/tracos.txt`: bônus declarados em `desatento` (percepcao -1), `peso-da-armadura` (furtividade -1), `grande-demais` (furtividade -2), `preguicoso` (atletismo -1) e `olhos-de-brasa` (percepcao +1). Só efeitos numéricos explícitos e sempre aplicáveis; escopos já suportados.
- ASSUMINDO: traços com condição (cena, alvo, 1d6, "uma vez por cena") continuam narrativos até haver suporte no motor.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: suporte a usos por cena/gatilhos (M-01.2).

## 2026-10-21 — app: ataque, defesa, fuga e bônus extraídos do `AppModel` (R-03.3)

- Novo `CombatBonuses.kt`: `approvedRuleBonus/Effects/Summary`, `conditionDiceShift`, `combatTraitIds`, `selectedCatalogBonus/Summary` (efeitos do catálogo recebidos por função).
- `CombatActions` agora também tem `resolveNpcTurn`, `attack`, `defend` e `fleeCombat`, movidos sem mudar cálculo nem mensagens.
- `AppModel` delega com as mesmas assinaturas; helpers privados viraram delegação a `CombatBonuses`.
- **Escrito, não compilado nem executado; sem testes novos.** R-03 concluído no código; falta CI.

## 2026-10-21 — app: `CombatActions` extraído do `AppModel` (R-03.2)

- Novo `CombatActions.kt`: `startCombat`, `nextCombatTurn`, `endCombat`, visões da ordem e dos alvos, `isPlayerCombatTurn`, `canDefend` e `awardCombatVictoryXp`, movidos sem mudar regras nem mensagens. Recebe sessão, pacote, herói, o conjunto de NPCs preparados (compartilhado) e funções de salvar/avisar.
- `AppModel` delega com as mesmas assinaturas; ataque, defesa, fuga e turno de NPC permanecem nele.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: R-03.3 ataque/defesa/fuga/turno de NPC.

## 2026-10-21 — app: `InventoryActions` extraído do `AppModel` (R-03.1)

- Novo `InventoryActions.kt`: `inventoryView`, `useItem`, `equipItem`, `unequipItem` movidos sem mudar regras nem mensagens. Sem estado próprio; recebe sessão, pacote ativo, herói, `isPlayerCombatTurn` e `onChanged` por funções.
- `AppModel` delega com as mesmas assinaturas públicas; `PlayScreen` não muda.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: R-03.2 `CombatActions`.

## 2026-10-21 — engine/data: snapshots do estado (F4 · M-08.3)

- `engine`: `GameState`, `Promise` e `CombatState` `@Serializable`; `Codec.encode/decodeState`; `EventStore.snapshot/saveSnapshot` com padrão neutro; `GameEngine.restore(..., snapshot)` aplica só a cauda do log; `CampaignSession.save` grava snapshot a cada 200 eventos (`SNAPSHOT_INTERVAL`, falha ignorada) e `open` o usa se válido (senão reproduz o log inteiro).
- `data`: Room versão 5, tabela `snapshots` (`SnapshotRow`, `SnapshotDao`), migração 4→5; `truncate` apaga snapshots maiores que o novo tamanho; `delete` os remove.
- ASSUMINDO: log inteiro continua carregado; dados seguem derivados do log; `data/schemas/5.json` gerado pelo build.
- **Escrito, não compilado nem executado; sem testes novos.** M-08 concluído no código; falta CI.

## 2026-10-21 — app: capítulos e missões nas tabelas (F4 · M-08.2)

- `AppModel.saveMetadata`: capítulos, missões e metadados gravados na mesma transação (`store.inTransaction`); o JSON legado segue sendo gravado.
- `AppModel.restoreMetadata`: lê `capitulos`/`missoes`; só usa o JSON de `campanha_meta` se as tabelas estiverem vazias. Restauração de backup preenche as tabelas.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: M-08.3 snapshots do estado.

## 2026-10-21 — data: tabelas de capítulos e missões (F4 · M-08.1)

- Room versão 4: `capitulos` e `missoes` (`CapituloRow`, `MissaoRow`, `CapituloDao`, `MissaoDao`), com FK `CASCADE` para `campanhas`. Migração 3→4 cria as tabelas e copia capítulos e missões dos JSONs de `campanha_meta` (ilegível é ignorado).
- `RoomEventStore`: `chapters/saveChapters/quests/saveQuests` (substituição transacional); `delete` transacional apaga também as novas tabelas; `duplicate` as copia. `Codec`: encode/decode de `Chapter` e `Quest`.
- ASSUMINDO: `campanha_meta` mantém os JSONs legados e a linha do tempo; `data/schemas/4.json` é gerado pelo build.
- **Escrito, não compilado nem executado; sem testes novos.** Próximas: M-08.2 ligar o `AppModel`, M-08.3 snapshots.

## 2026-10-21 — engine/ai: consequência contextual de relógio (F4 · M-07.6)

- `Clock/CreateClock/ClockCreated.consequence` (padrão vazio, máx. 500 caracteres; dados antigos válidos). `create_clock` da IA aceita `consequence`; o esquema do prompt a descreve como nota privada.
- `Master.kt`: relógio completo no contexto traz a consequência planejada. Nunca vai à linha do tempo, ao descanso nem ao jogador.
- ASSUMINDO: escrita na criação do relógio; o motor não a executa sozinho.
- **Escrito, não compilado nem executado; sem testes novos.** M-07 concluído no código; falta CI.

## 2026-10-21 — engine/ai: pista pública intermediária de relógio (F4 · M-07.5)

- `Clock/CreateClock/ClockCreated.midClue` (padrão vazio, máx. 500 caracteres; dados antigos válidos) e `Clock.midpoint`. `create_clock` da IA aceita `midClue`; o esquema do prompt o explica.
- `automaticTimeline` publica `midClue` ao preenchimento cruzar a metade (sem duplicar; a pista final prevalece ao completar). `LocalActions.rest` mostra a pista de quem cruzou a metade ou completou. `Master.kt` entrega a pista ao mestre quando o relógio avançou fora da IA e passou da metade.
- ASSUMINDO: pista definida na criação do relógio; uma revelação por travessia da metade.
- **Escrito, não compilado nem executado; sem testes novos.** Pendente em M-07: consequências contextuais.

## 2026-10-21 — ai: sinal de relógios avançados fora da IA (F4 · M-07.4)

- `ContextInput.advancedClockIds`; `GameLoop` guarda `narratedUpTo` (fim do log na última narração) e marca no contexto os relógios com `ClockAdvanced` desde então. `Master.kt` acrescenta ao relógio “AVANÇOU desde sua última fala: mostre um sinal sutil no mundo, sem números nem o nome do relógio”.
- ASSUMINDO: sessão reaberta não marca nada na primeira jogada; refazer e desfazer podem perder a marcação.
- **Escrito, não compilado nem executado; sem testes novos.** Pendente em M-07: pista pública por eventos, consequências contextuais.

## 2026-10-21 — engine/ai/app: relógios que avançam ao fechar capítulo (F4 · M-07.3)

- `Clock/CreateClock/ClockCreated.chapterTick` (padrão 0, validado entre 0 e o número de casas; dados antigos válidos); `create_clock` da IA aceita `chapterTick`; novo `GameState.chapterClocks()`.
- `AppModel.closeChapter`: após o resumo ser gravado, relógios com `chapterTick` avançam pelo motor. Falha do resumo não avança nada.
- **Escrito, não compilado nem executado; sem testes novos.** Pendente em M-07: pista pública por eventos, consequências contextuais.

## 2026-10-21 — engine/ai/app: relógios que avançam na viagem (F4 · M-07.2)

- `Clock/CreateClock/ClockCreated.moveTick` (padrão 0, validado entre 0 e o número de casas; dados antigos válidos); `create_clock` da IA aceita `moveTick`; novo `GameState.travelClocks()`.
- `AppModel.moveToPlace` usa `executeAll`: deslocamento e avanço dos relógios com `moveTick` no mesmo lote atômico; viagem recusada não altera relógios. Pista pública de relógio completo segue a regra existente (linha do tempo e contexto do mestre).
- **Escrito, não compilado nem executado; sem testes novos.** Pendente em M-07: avanço por capítulo, pista pública por eventos, consequências contextuais.

## 2026-10-21 — engine/ai: relógios que avançam no descanso (F4 · M-07.1)

- `Clock.restTick`, `CreateClock.restTick` e `ClockCreated.restTick` (padrão 0; validado entre 0 e o número de casas; logs e backups antigos continuam válidos). `create_clock` da IA aceita `restTick` opcional e o esquema do prompt o explica.
- `LocalActions.rest`: relógios com `restTick` avançam pelo motor no mesmo lote do descanso (desfazer reverte); a resposta mostra só a pista pública de quem completar, nunca nome ou contagem. Descanso com tudo recuperado ainda passa o tempo se houver relógio a avançar.
- **Escrito, não compilado nem executado; sem testes novos.** Pendente em M-07: outros marcos de tempo, pista pública por eventos, consequências contextuais.

## 2026-10-21 — app: painel de sorte na aba Jogar (F3.4)

- `PlayScreen`: novo `LuckPanel` (repetir rolagem / somar uma face), mostrado quando `PlayController.luckOffer()` existe; desabilitado durante `busy`, rolagem ou pedido pendentes. A oferta só é consultada com a tela livre, pois usa a trava de turno. `AppModel.resourceName` dá o nome do recurso.
- Nenhum pacote embutido declara `recurso_sorte` ainda; o painel só aparece em pacotes que declararem.
- **Escrito, não compilado nem executado; sem testes novos.** F3 fecha no código; falta executar o CI.

## 2026-10-21 — ai: sorte pós-rolagem no controlador (F3.3)

- `CheckMenu.luckOption` (+ `LuckOption`); `GameLoop.luckOffer/spendLuck`; `PlayController.luckOffer/spendLuck`; `TurnRecord.luckUsed`. Vale só para o último teste direto da jogada (sem pedido do mestre, disputa, grupo ou revelação), um uso por teste, com recurso suficiente; volta ao ponto após a rolagem, chama `CampaignSession.spendLuck` e narra de novo. O chat troca rolagens e narração da jogada; a rolagem anterior aparece como não escolhida. Falha ao narrar desfaz a jogada; desfazer reverte o gasto.
- ASSUMINDO: sorte não se aplica a testes pedidos pelo mestre, pois efeitos já gravados após o dado (revelações) dependeriam do resultado antigo.
- **Escrito, não compilado nem executado; sem testes novos.** Próxima: F3.4 interface.

## 2026-10-21 — rules: sorte pós-rolagem no pacote (F3.2)

- `testes.txt`: colunas opcionais `recurso_sorte|custo_sorte|formas_sorte` (`REROLL`, `ADD_FACE` ou `REROLL+ADD_FACE`); `CheckDef.luckResource/luckCost/luckKinds`. Validação no `PackageParser` e em `RulePackage` (pós-herança): recurso existente, custo positivo, campos declarados juntos. Pacotes antigos seguem válidos.
- Nenhum pacote embutido declara sorte ainda. **Escrito, não compilado nem executado; sem testes novos.** Próximas: F3.3 controlador, F3.4 interface.

## 2026-10-21 — engine: sorte pós-rolagem (F3.1)

- `AdvancedChecks.kt`: `LuckKind` (`REROLL`, `ADD_FACE`), `LuckResult` e `CampaignSession.spendLuck(entityId, resource, cost, previous, kind)`. Gasta o recurso e repete a rolagem ou soma uma face (dificuldade 1 ponto mais favorável sobre o mesmo dado) numa operação atômica; recusa desfaz tudo. Sem novos tipos de evento.
- ASSUMINDO: um uso por teste é controlado por quem chama; custo e recurso virão do pacote em etapa futura.
- **Escrito, não compilado nem executado; sem testes novos (regra da etapa).** Próximas: F3.2 pacote, F3.3 controlador, F3.4 interface.

## 2026-10-21 — ai/app: poderes pedidos pela IA (F2.5)

- `Proposals.kt`: novo `request_power` (`PowerRequest`), exclusivo (sem proposals, `create_npc`, combate ou testes); instruções do esquema atualizadas.
- `TurnRunner`: valida por `PowerMenu.build` e resolve pelo motor (`activatePower`); poder inválido ou recusa do motor volta à IA (uma correção) sem alterar estado. `PowerUse` em `TurnOutcome.Applied` e `TurnRecord`.
- `PowerMenu.describe` lista os poderes da ficha no contexto do mestre; `GameLoop(powerMenu)` ligado em `AppModel`. `PlayController` mostra custo, rolagem e efeito no chat (helper compartilhado com o uso manual); desfazer reverte tudo.
- ASSUMINDO: a fala do jogador é o consentimento; só poderes do personagem do jogador; narração gerada antes do resultado.
- **Escrito, não compilado nem executado. Testes novos não criados (regra da etapa).** F2 conclui o fluxo pacote → motor → controlador → tela → IA; falta executar o CI.

## 2026-10-21 — app: poderes na aba Jogar (F2.4)

- `AppModel`: `PowerView`, `powersView()`, `powerTargets()`, `activatePower()` (usa `PowerMenu` e `PlayController.activatePower`). ASSUMINDO: poder não consome turno de combate.
- `PlayScreen`: novo `PowersPanel` (custo, seleção de alvo para poderes ofensivos, botão usar), desabilitado durante `busy`/rolagem pendente.
- **Escrito, não compilado nem executado.** Pendente na F2: uso de poderes pela IA.

## 2026-10-21 — ai: tradução e fluxo de poderes (F2.3)

- Novo `PowerMenu`: lista poderes pelo traço da ficha e traduz `PowerDef` em `PowerSpec` (cura, dano por perfil, condição, remoção; teste do pacote com ajustes de condições/traços). Recusa poder/alvo inválidos.
- `PlayController.activatePower`: exclusivo por turno, mostra rolagem/resultado no chat, desfaz como ação local; recusa do motor não altera estado.
- Novo `PowerActivationFlowTest`.
- **Escrito, não compilado nem executado.** Pendente na F2: `AppModel` + botão na aba Jogar, uso pela IA.

## 2026-10-21 — engine: ativação de poderes (F2.2)

- Novo `PowerActivation.kt` (`PowerSpec`, `PowerEffect`, `PowerResult`), `GameEngine.activatePower` e `CampaignSession.activatePower`: valida usuário/alvo, gasta recurso, rola teste opcional (custo gasto mesmo se falhar), aplica cura/dano rolado/condição/remoção; recusa desfaz tudo. Sem novos tipos de evento.
- Novo `PowerActivationTest`.
- **Escrito, não compilado nem executado.** Próximas etapas da F2: `PowerDef` → `PowerSpec`, `PlayController`, interface, IA.

## 2026-10-21 — Etapa 0 concluída no código (fechamento de fase)

- R-01, R-02 e R-05 completos no código. Tentativa de rodar `engine`/`rules`/`ai` fora do Android: o Gradle não resolveu plugins (Maven Central e Google inacessíveis no ambiente), então **a suíte não foi executada**. Falta o CI.
- Verificação estática da fase: ver itens do plano; nenhuma API nova fora do escopo.

## 2026-10-21 — ai: teste de cancelamento (R-05, fechamento)

- Novo `CancelTokenTest`: espera de retry/fila encerrada por `cancel()`, token já cancelado, `RetryingProvider` sem nova tentativa após cancelar, `JavaNetTransport.post` com conexão pendurada em `ServerSocket` local lançando `CancellationException`.
- **Escrito, não compilado nem executado.** Etapa 0 sem pendências de código; falta o CI.

## 2026-10-21 — app: inicialização assíncrona (R-02, fechamento)

- `AppModel`: abertura do banco, restauração de pacotes/fichas/campanha/metadados e provedor movidas para `startup()` em `Dispatchers.IO`; `session` passa a `lateinit`; novos `ready`/`startupError`; `onCleared` cancela o escopo.
- `RpgSoloApp`: tela “Abrindo sua campanha…” (ou erro) até `ready`.
- **Escrito, não compilado nem executado.**

## 2026-10-21 — app: nova campanha em IO (R-02, continuação)

- `NewCampaignSection`: “Começar” grava a campanha e troca a sessão em `Dispatchers.IO` dentro de `exclusiveTurn`, com botão desabilitado e `Problem` em caso de exceção.
- **Escrito, não compilado nem executado.** Pendente em R-02: `init` do `AppModel`.

## 2026-10-21 — app: regras adicionais via `act` (R-02, continuação)

- `AdditionalRulesPanel`: aprovar/descartar regra adicional roda em IO com trava de turno e respeita `busy`.
- **Escrito, não compilado nem executado.** Pendente: `init` do `AppModel`, `NewCampaignSection`, Config.

## 2026-10-21 — app: Campanhas inteira em IO (R-02, continuação)

- Missões (adicionar, concluir objetivo, encerrar), acontecimentos manuais, aumento de avanço e geração do texto de backup passam a rodar em `Dispatchers.IO` com botões desabilitados.
- **Escrito, não compilado nem executado.** Pendente: `init` do `AppModel` e ações das abas Jogar/Config.

## 2026-10-21 — app: ações de UI em IO (R-02, ações de tela)

- Campanhas (renomear, duplicar, apagar, reabrir, restaurar backup por texto/arquivo, abrir capítulo), biblioteca/criação de fichas e importação de pacote ZIP rodam em `Dispatchers.IO`, com botões desabilitados durante a ação; ações que tocam a sessão usam `exclusiveTurn`.
- Novo `AppModel.draftFichaId()` (sem banco) para o rascunho na composição; `newFichaId()` só em IO.
- **Escrito, não compilado nem executado.** Pendente: `init` do `AppModel`, progressão, diário.

## 2026-10-21 — ai: leituras seguras e teste concorrente (R-01, fechamento)

- `PlayController`: `chat` sincronizada, também monitor de `entries`/`revealedRolls`; `lines`, `isRollRevealed`, `revealRoll`, `suggestions`, desfazer/refazer e `truncateLastTurn` atômicos. `GameLoop.history` sincronizada.
- Novo `ConcurrentTurnsTest`: duas jogadas concorrentes serializadas (log, chat e desfazer coerentes); leituras da interface não bloqueiam durante turno em andamento.
- **Escrito, não compilado nem executado.**

## 2026-10-21 — rules: poderes tipados no pacote (F2.1)

- Novo `poderes.txt` opcional: `PowerDef`, `PowerTarget`, `PowerEffectKind`; leitura e validação em `PackageParser` (traço do tipo poder, recurso, teste, perfil de dano, condição, cura exige recurso de saúde), repetição na validação pós-herança de `RulePackage`, mesclagem por id em `PackageRegistry`, carga em `BundledContent`.
- Sistema próprio: `regeneracao-ativa` (cura 2 PV por 1 PM) e `rajada-arcana` (dano `golpe` por 1 PM, com teste `ataque`). Valores em rascunho.
- **Escrito, não compilado nem executado.** Ainda não há ativação no motor/controlador/interface (próximas etapas da F2).

## 2026-10-21 — docs: F1 reconciliada com o código

- O código já continha o fluxo F1 (pedido `request_check` com `opposed`/`group`, validação, pendência, resolução e nota privada). Plano, README e `FORMATO_PACOTE.md` estavam desatualizados e foram corrigidos.
- Nenhum código alterado. **Não executado.** ASSUMINDO: sem sintaxe de pacote própria para disputa/grupo.

## 2026-10-21 — fim da Etapa 0 (verificação local)

- Suíte completa **não executada**: o ambiente está offline e o Gradle Wrapper não consegue baixar a distribuição, então não há compilação nem testes.
- Verificação estática feita: chaves e parênteses balanceados nos 13 arquivos alterados, nenhum `session.engine.events`/`eventCount` restante em `ai` e `app`, nenhum `allowMainThreadQueries` ou `database.runInTransaction` restante, imports não usados removidos.
- Pendências de código que seguem abertas: teste concorrente no `ai` (R-01), caminhos síncronos do `AppModel` (R-02), teste de cancelamento (R-05).
- Próximo passo: rodar o CI e preencher a tabela de `docs/VALIDACAO_CI.md`.

## 2026-10-21 — docs: estado de validação

- `docs/VALIDACAO_CI.md` reescrito no topo: bloqueio de runner marcado como resolvido, últimos resultados conhecidos e tabela por módulo (pendente) para colar o resultado do próximo CI. README e plano alinhados.
- Nenhum código alterado.

## 2026-10-21 — ai/app: transporte cancelável (R-05)

- Novo `CancelToken`: `JavaNetTransport.post/postStream` fecham a conexão ao cancelar; `ThreadSleeper` (retry e fila de limite) espera de forma cancelável. Cancelamento lança `CancellationException`, não repetida pelo retry.
- App: `cancellableIo`/`cancellableIoSuspend` ligam o cancelamento da coroutine ao token (narração, teste de conexão, resumo de capítulo, nova campanha).
- Config avisa ao digitar `http://`; `testConnection` só aceita `https://` (alinhado a `ProviderConfig`).
- **Escrito, não compilado nem executado.**

## 2026-10-21 — data/app: fim do `allowMainThreadQueries` (R-02, parcial)

- `RpgDatabase.build` não permite mais consultas na thread principal.
- `RoomEventStore`: toda operação passa por `io { }`; novo `inTransaction { }`. Chamadas da thread principal são executadas na thread `rpgsolo-db` e a principal espera (sem crash, mas ainda bloqueia).
- `AppModel.importBackup` usa `store.inTransaction`.
- **Escrito, não compilado nem executado.** Pendente: mover os caminhos síncronos do `AppModel` e das telas para `Dispatchers.IO`.

## 2026-10-21 — app: painéis exclusivos e fora da thread principal (R-01)

- `PlayController.exclusiveTurn`/`busy` e `AppModel.exclusiveTurn` expõem a `TurnGate` da narração.
- Inventário, lugares, testes avançados e combate (iniciar, atacar, defender, passar, fugir, turno inimigo, encerrar, adicionar NPC) rodam por `act(...)` em `Dispatchers.IO` sob a trava de turno e ficam desabilitados enquanto `busy`.
- `AppModel.progressionHistory` usa `session.events`.
- **Escrito, não compilado nem executado.** Pendente: teste concorrente no `ai`, leituras de `chat`/`entries` sem trava.

## 2026-10-21 — ai: exclusividade de turno (R-01)

- Novo `TurnGate` (trava reentrante). `GameLoop` expõe `turnGate`; `play`, `resolvePending`, `undo` e `regenerate` rodam exclusivos, assim como `submit`, `rollPending`, `undo`, `regenerate`, `resolveOpposed` e `resolveGroup` do `PlayController`.
- `TurnRunner` e `PlayController` passam a usar `session.events`/`session.eventCount`.
- **Escrito, não compilado nem executado.** Pendente: `app` (`busy` nos painéis, `session.engine` restante em `AppModel`) e teste concorrente no `ai`.

## 2026-10-21 — engine: sessão segura entre threads

- `CampaignSession` passa a usar uma trava reentrante em todas as operações; novos `atomically { }`, `events` e `eventCount`. `InMemoryEventStore` sincronizado.
- `checkOpposed`, `checkGroup`, `checkWithFortune` e `checkWithFortuneSpendingResource` ficam atômicos; `ChapterBook` lê o log por `session.events`.
- Novo `SessionConcurrencyTest`. **Escrito, não executado:** rodar o CI.
- Pendente: `ai` e `app` ainda usam `session.engine` diretamente e fazem `mark()`/`rollbackTo()` sem exclusividade de turno (ver R-01 no plano).

## 2026-10-21 — conteúdo só de criação própria

- Removidos os links de consulta (`catalog/fontes.txt`, `SourceLibrary`) e as seções "Consultar regras oficiais" das abas Heróis e Sistemas.
- Removido o pacote `packages-import/3det-victory/` e o teste que dependia dele.
- Removidos `DocumentIndex`, `PdfTextExtractor` e a consulta a PDF em `RuleLookupChain`/`CharacterDraft.hints`; `RuleSource.PDF_DO_USUARIO` deixou de existir.
- Catálogo embutido reduzido ao sistema próprio (`sistemas.txt` e `compat.txt`); testes de catálogo, campanha e interpretação passaram a usar sistemas fictícios de teste.
- Removidos `docs/PLANO_DESENVOLVIMENTO.md`, `PLANO_MELHORIAS_RPG_SOLO.md` e os documentos de pesquisa de sistemas de terceiros em `docs/catalogo/`; o novo `PLANO_RPG_SOLO.md` os substitui.
- **Não compilado nem testado nesta etapa:** rodar o CI antes de seguir.

## 2026-10-19 — CI verde

- Repositório tornado público para habilitar o uso gratuito dos runners padrão do GitHub Actions.
- Execução oficial [Android CI #39260875061](https://github.com/Kyra2214/RPG-solo/actions/runs/39260875061) concluída com sucesso no commit `85b58ce`.
- `./gradlew test` passou para `engine`, `rules`, `ai`, `data` e `app`; os testes unitários Android sem fontes foram reconhecidos como `NO-SOURCE`/`UP-TO-DATE`.
- `./gradlew :app:assembleDebug` passou e o artefato `rpgsolo-debug-apk` foi publicado pelo workflow.

## 2026-10-20 — testes avançados e validação de pacotes

- `PlayController` expõe fluxos para resolver testes opostos e em grupo com persistência, condições e traços de cada participante, rolagens individuais, desfecho calculado pelo motor e suporte a desfazer. As rolagens descartadas por vantagem/desvantagem são preservadas e exibidas; o resultado fica oculto até todas as rolagens da disputa serem reveladas. A aba Jogar permite configurar e executar os dois formatos.
- Adicionados testes de regressão para disputa/grupo no controlador e referências inválidas nos pacotes. Corrigidos os fixtures da validação de escopo para modificar o campo real de bônus do traço, tanto no parser isolado quanto após herança. **Os testes não foram executados**; o código ainda não está validado.
- `PackageParser` valida referências de atributos em recursos, requisitos de traços, escopos de bônus, atributos/perícias/recursos usados por testes, efeitos de condições e referências em exemplos e arquétipos. `PackageValidator` repete a validação de escopos após resolver herança. Também valida, inclusive após herança, escopos de efeitos de equipamento; o parser verifica se o recurso de morte e o recurso de dano periódico das condições existem no pacote. Foram adicionados testes de regressão para esses três casos e para escopo de equipamento desconhecido; continuam escritos, mas não executados.
- A aba Jogar permite resolver uma disputa entre o herói e um NPC vivo, além de teste de grupo com seleção de participantes e limiar explícito de sucessos. Pendências: sintaxe de pacote e pedidos narrativos para ambas as modalidades; poderes ativáveis e subsistema geral de sorte/destino.

# Changelog — implementação code-first

## 2026-10-19 — correções de consistência nas fases 1–4

### Motor de regras e continuidade
- Testes opostos comparam primeiro o nível do resultado (falha/sucesso/crítico) e depois a margem; empate absoluto favorece o defensor. Margens de testes com dificuldades ou escalas diferentes deixam de ser comparadas isoladamente.
- Adicionada a API `CampaignSession.checkWithFortuneSpendingResource`: paga um recurso validado da ficha antes de aplicar vantagem/desvantagem e restaura o log se a rolagem falhar. A integração posterior adicionou configuração do recurso/custo/modo pago por pacote e opção explícita na interface.
- O fluxo principal de jogada agora respeita `fortuneMode` declarado no teste do pacote, persistindo as duas rolagens e usando apenas o resultado selecionado. Foi adicionado teste de regressão para essa integração; ainda não executado.
- A rolagem descartada de vantagem/desvantagem agora também é preservada no registro da jogada e exibida no chat como 'Não escolhido'; apenas o resultado escolhido é enviado ao contexto de narração. A resolução avançada e a persistência de gasto de recurso foram tornadas transacionais no nível da sessão. Foram adicionados testes para vantagem/desvantagem em testes opostos e de grupo; testes não executados.
- Testes opostos deixam de comparar margens de escalas incompatíveis: a margem só desempata quando comparação, quantidade/lados de dados e dificuldade coincidem; caso contrário, empate na qualidade favorece o defensor. Foi adicionada regressão para dificuldades diferentes; não executada.
- Corrigida uma lacuna na implementação: testes opostos e de grupo agora aplicam a política de vantagem/desvantagem de cada participante por meio da mesma resolução atômica usada nos testes individuais, persistindo todas as rolagens necessárias; consequências continuam usando apenas os resultados escolhidos. Não executado.
- Implementados dados explosivos determinísticos: uma face máxima gera outro dado, com teto defensivo de 100 explosões por dado. O pacote pode ativar a regra por teste na coluna opcional `explodir`; o padrão permanece desligado para preservar pacotes antigos. Testes de dados e parsing adicionados, não executados. O motor preserva a soma natural dos dados iniciais para faixas críticas; os dados extras contam no total de sucesso, sem deslocar a faixa crítica.
- O pacote pode declarar recurso, custo e modo pago (`ADVANTAGE`/`DISADVANTAGE`) por teste, separado da política automática; isso evita conceder vantagem automaticamente e ainda cobrar por ela. O jogo oferece dois caminhos explícitos no teste pendente: rolar normalmente ou gastar o recurso configurado; o custo só é aplicado na segunda opção, junto com as rolagens em uma operação persistida. A validação do pacote verifica recurso, custo e política compatível. Testes de parsing adicionados, não executados. Também foram adicionados testes de fluxo pelo controlador para conferir opção paga, gasto do recurso, exibição das duas rolagens e que a rolagem normal não consome recurso; não executados.
- Progressão agora aceita `recompensa_recurso|recurso=quantidade,...` em `criacao.txt`: ao ganhar um ou mais níveis, o motor restaura por nível os recursos configurados, respeita o máximo e registra a alteração no log canônico. O validador rejeita ids/custos inválidos ou recursos ausentes. Testes de motor e pacote escritos, não executados.
- Falha na validação/resolução do teste agora retorna `PlayResult.Failed` e tenta restaurar a marca anterior, em vez de escapar como exceção do fluxo de jogada. Foi adicionado teste para especificação inválida; não executado.
- `EngineConfig` recusa pré-requisitos de perícia que referenciem atributos inexistentes, em vez de aceitar configuração mecanicamente inconsistente.
- A linha do tempo só revela uma pista pública na transição de relógio incompleto para completo; eventos posteriores que repetem o estado cheio não duplicam a pista. Nomes internos e contagem continuam privados. Foi acrescentada uma regressão para o evento repetido; os testes continuam sem execução.

### Estado da validação
- Foram atualizados testes unitários para os casos afetados, mas eles **não foram executados**. Nenhum build, teste, lint ou CI foi iniciado; validação geral segue adiada conforme a orientação code-first.

## 2026-10-19 — integração de narração progressiva

### IA e fluxo de jogada
- Transporte HTTP ganhou leitura linha a linha para respostas SSE, com implementação real no transporte de produção e fallback compatível com transportes simples.
- Provedores OpenAI-compatible e Anthropic implementam leitura incremental de conteúdo.
- O streaming foi propagado pelos decorators de retry, limite de requisições e contagem de uso.
- O loop extrai somente o campo `narrative` do JSON transmitido, sem exibir propostas ou outros campos estruturados durante a geração.
- A tela Jogar mostra a narração parcial enquanto o pedido está em andamento e limpa o rascunho ao terminar ou falhar.
- Retentativas de streaming não são repetidas depois do primeiro fragmento, evitando concatenar duas respostas parciais.

### Mecânica e acessibilidade
- Alguns bônus e penalidades numéricos já explícitos em traços do pacote próprio agora são aplicados pelo motor (reflexos, frágil, mão pesada, graça feérica e desconfiança/emoção confusa).
- Combate aplica resistência/vulnerabilidade numérica a dano antes de registrar o dano resolvido; Pele dura, Frágil, Tenacidade anã e Corpo de pedra usam o escopo explícito `resistencia_dano` do pacote.
- Dados animados têm descrições semânticas para TalkBack, sem revelar o resultado antes de o jogador revelar a rolagem.
- Consumível de cura não é gasto se o recurso-alvo já estiver cheio; a validação acontece antes do lote atômico de cura/consumo/avanço de turno.
- Testes por pontos agora diferenciam 0 e 1 ponto: zero conserva a face mínima de sucesso; cada ponto positivo acrescenta uma face até o teto do dado. Tutorial, fórmula e simulador foram alinhados; a tabela empírica deve ser recalculada na validação posterior.
- Se um endpoint rejeitar explicitamente o modo SSE (HTTP 400/422), o provedor tenta a resposta completa padrão para preservar compatibilidade.
- Preferência de escala global da fonte (85%–140%) adicionada à Configuração, persistida no aparelho e aplicada à interface via densidade Compose.
- A tela inicial vazia agora orienta explicitamente a sequência de primeiro uso: configurar/testar IA, criar ou escolher herói e criar campanha.

### Estado da validação
- Essas alterações são somente de código e leitura estática. **Não foram executados builds, testes, lint ou CI**. Compatibilidade SSE por provedor, Compose e comportamento em aparelho continuam pendentes de validação do usuário.


## 2026-10-18 — consolidação da branch `implementation/code-first-combat`

### Motor e combate
- Ataques usam a comparação, dados, atributos e perfil de dano declarados pelo pacote.
- Bônus de condição afetam o modificador/limiar de acerto; não alteram indevidamente a quantidade de dados.
- Equipamento, traços e adaptações aprovadas participam dos bônus de ataque, dano e defesa.
- Adicionadas ações Defender e Fugir, seleção explícita de adversários e limpeza de postura defensiva.
- Dano por condição e avanço de turnos permanecem no motor; turno pula combatente morto por condição ao fechar a rodada.
- Relógios de agenda e chave direcional de relações têm modelos explícitos no módulo engine.

### Estado, continuidade e segurança
- Restauração de backup valida o estado reconstruído antes de criar a campanha.
- Validação adicional cobre condições, mapa, dossiês de NPC, relações e relógios.
- Limites de tamanho/quantidade adicionados a dossiês de NPC e adaptações.
- Importação de pacotes restaura arquivos privados, respeita dependências e limita ZIP a 20 MiB.
- Preferências da campanha e lista de adversários preparados são persistidas por campanha.

### Interface
- Biblioteca de fichas permite consultar detalhes, duplicar, arquivar/restaurar, excluir fichas arquivadas e copiar JSON.
- Campanhas exibem linha do tempo, missões encerradas, histórico de XP/níveis e atitudes de NPCs em palavras.
- Combate mostra ações disponíveis de acordo com o pacote.
- Erros de jogada aparecem na interface; a entrada é preservada quando uma ação é recusada para permitir nova tentativa.
- Preferências de narração e limites de conteúdo ficam salvos localmente.
- A tela inicial vazia oferece atalhos para configurar a IA e iniciar uma campanha.
- O diário de campanha mostra histórico de XP/nível derivado do log e atitudes registradas dos NPCs em palavras.
- O avanço de rodada ignora um combatente que morre por efeito de condição no momento em que o turno dele começaria.
- O prompt do mestre orienta avanço parcimonioso de relógios de agenda sem expor nomes ou contagens.
- Condições temporizadas exigem recurso de referência válido, para que a duração sempre possa expirar corretamente.

### Validação desta etapa
- **Não foram executados builds, testes, lint ou CI**, conforme a orientação de concluir o código antes das validações.
- A implementação ainda não deve ser considerada validada até compilar e passar pela bateria de verificações em aparelho/emulador.

---

## 2026-10-18 — próxima etapa

- Configuração: botão **Testar conexão** faz uma chamada mínima ao provedor informado, fora da thread principal, sem salvar a configuração nem alterar a campanha.
- Equilíbrio: adicionado `tools/simulate_checks.py` com semente reproduzível e `docs/MECANICA_DADOS.md` com a tabela de chances por pontos e dificuldade.
- Jogar: atalhos locais para **Ver ficha**, **Inventário** e **Descansar**, sem chamada de IA e bloqueados enquanto há uma rolagem pendente.
- Rolagens: o cartão agora identifica “faces de sucesso” nos testes por pontos, descreve o alvo do d20 e mostra a margem como “passou por” ou “faltou”.
- Motor: testes de luta completa cobrem ataque, dano, morte, encerramento automático do combate e condição com dano por rodada e expiração.
- Combate: a aba Jogar agora mostra alvos vivos e permite atacar somente no turno do jogador; iniciativa, defesa, rolagem e dano continuam sob controle do motor, com autosave.
- Combate: no turno inimigo, a interface exige **Resolver turno inimigo**; o ataque contra o jogador é calculado e salvo antes de avançar a iniciativa.
- Equipamentos: itens equipáveis agora são filtrados pelo catálogo do pacote, aparecem com slot e quantidade, e podem ser equipados ou desequipados pela interface usando comandos validados.

## 2026-10-18

- Interface: o formato visual dos dados agora acompanha o tipo do dado (d4, d6, d8, d10, d12 e d20), sem alterar o resultado decidido pelo motor.
- Jogar: o estado de rolagens já reveladas saiu da composição da tela e passou ao `PlayController`, preservando a narração ao trocar de aba ou recriar a Activity; o comportamento ganhou teste unitário.

## 2026-10-17 (links de consulta)

- Sistema próprio + links de consulta: 3DeT e D&D passam a ser **só link** na criação de personagem (aba Heróis e aba Sistemas). Removidos da tela: selo e texto de licença, origem do pacote e a frase sobre inserir PDFs. `fontes.txt` ficou só com 3DeT e D&D (Fate e Pathfinder saíram).
- Plano: Fase 4B reduzida a links; importação de PDF e trabalho de licença (4A.5, 4B.2, 4B.3) cancelados. `DocumentIndex`/`PdfTextExtractor` seguem no módulo `rules` sem uso no app.

## 2026-10-17

- Backup local: adicionar exportação e restauração de JSON por seletor de arquivos Android (SAF), mantendo a alternativa de copiar e colar texto. A chave da API não integra o backup.
- Campanhas: permitir continuar uma campanha salva pela lista da aba Campanhas. A troca reabre o log de eventos e restaura capítulos, missões e o contexto de personagem/pacote em vez de reutilizar o da campanha anterior.
- Room: persistir player, ficha, sistema, edição e pacote em `campanha_meta`; adicionar migração versionada 2 → 3 sem apagar os dados existentes.
- Fase 2 — perícias: validar ids, níveis de 0 a 5, pré-requisitos e orçamento total do pacote; impedir que uma campanha inicie com uma ficha inválida. A decisão sobre a diferença mecânica entre 0 e 1 ponto continua pendente.
- Documentação: substituir estados históricos obsoletos do README e manter este changelog para registrar as entregas.
- Verificação: `./gradlew test :app:assembleDebug` e os testes do módulo de regras passaram com JDK 17 e Android SDK 35. A execução em aparelho/emulador e os testes instrumentados de migração Room continuam necessários.

## 2026-10-19 — fechamento code-first: motor, continuidade e cache

- Motor/regras: adicionada API para rolagens com vantagem/desvantagem, testes opostos e testes em grupo; `testes.txt` agora aceita a coluna final opcional `NORMAL`, `ADVANTAGE` ou `DISADVANTAGE`, propagada por `RulePackage` até o motor. As versões de `CampaignSession` salvam as rolagens no log persistente; rolagens compostas fazem rollback se uma especificação falhar no meio.
- Progressão: o pacote pode declarar `pontos_por_nivel`; o motor concede saldo ao subir de nível e valida aumentos de atributo/perícia contra os limites gravados na campanha. A aba Campanhas permite gastar um ponto por clique. O motor valida pré-requisitos de perícia, teto e saldo sem aplicar alterações parciais. O sistema próprio agora concede 1 ponto por nível.
- Continuidade: criada linha do tempo derivada dos eventos persistidos para descobertas, locais conhecidos, deslocamentos, XP/nível, condições do jogador e eventos de combate com participação do jogador. Segredos de NPC e alterações ocultas não são exibidos. Relógios aceitam `publicClue` opcional, revelada apenas quando o relógio completa, sem nome interno ou contagem.
- Interface: a aba Campanhas passou a mostrar a linha do tempo automática, separada das anotações manuais. A seção de evolução permite gastar pontos em atributos/perícias com rótulos TalkBack específicos. A tela Jogar agora mantém o assistente de primeiro uso até existir provedor, ficha disponível e campanha ativa.
- IA: a API oficial Anthropic recebe `cache_control: ephemeral` no prefixo de sistema por padrão; endpoints compatíveis não presumem suporte. O cache ainda precisa ser validado em rede real e pode ser desativado pela configuração.
- Uso/cache: a resposta Anthropic captura tokens lidos/criados no cache também via SSE; `UsageTracker` separa as contagens, permite estimar custos com tarifas de cache distintas e Config mostra as métricas. A confirmação de cache-hit real ainda depende de chamada de rede. A Config agora permite ativar/desativar o cache e persiste a escolha com as configurações do provedor. Também aceita preços por milhão para entrada/saída e tarifas de cache separadas, exibindo custo estimado na moeda informada.
- Testes: adicionados casos unitários para essas APIs e para evitar exposição de dados privados na linha do tempo. **Não foram executados**, conforme a etapa code-first.


## 2026-10-22 — auditoria incremental de árvores e maldições

- Híbridos: a penalidade social passou a contar somente as classes desbloqueadas pelo nível do personagem (1/10/20/30), e não todas as classes selecionadas antecipadamente.
- Motor: o comando de loadout agora rejeita combinações com mais de quatro classes; testes unitários cobrem desbloqueio por nível e limite de classes.
- Maldições: documentação corrigida para refletir os 100 registros aplicados atuais e separar catálogo/conceitos da Classe de Malditos.
- CI confirmado verde no commit `7c2aecb3145e768ffee372ba94d61045a64ac385` (testes JVM/Android e APK debug; E2E ignorado). As correções posteriores estão aguardando a própria execução de CI; não considerar o resultado anterior como validação desses novos commits.
- Pendências: auditoria ponta a ponta de todos os escopos/efeitos, persistência de maldições após salvar e reabrir campanha e validação de cada habilidade de rank 7.


- Motor de maldições: adicionados testes para resistência pendente/sucesso/falha, expiração após rodadas completas, remoção com motivo e reconstrução do estado pelo log de eventos. A validação CI desses testes novos ainda está pendente.
 

- Classe de Malditos: substituídos os 42 nomes genéricos das habilidades culminantes de rank 7 por nomes temáticos específicos das famílias/vertentes; adicionado teste contra regressão para placeholders.
 

- Classe de Malditos: além dos nomes próprios, as 42 culminâncias agora diferenciam mecânicas por família (multiataque, controle direcionado ou golpe culminante), e vertentes mágicas declaram vínculo com afinidade. Adicionados testes para esses contratos; balanceamento de jogo segue pendente.
 

- Motor: efeitos ativos direcionados de habilidades agora exigem combate ativo, participação de usuário/alvo na iniciativa e turno do usuário; teste de motor cobre as recusas e o replay.


- Auditoria de habilidades ofensivas: 42 habilidades iniciais de Classe de Malditos agora executam `POWER_STRIKE` real com efeito de dano tipado; o rank 7 físico do arqueiro deixou de exigir afinidade elemental. Testes de regressão passaram no workflow #243 (commit `5205b037ebd45ed71d633f40c0ce08ba3c7ecc2d`): testes JVM/Android e build do APK debug verdes; E2E ignorado.
