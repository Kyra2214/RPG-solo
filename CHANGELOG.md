## 2026-10-22 — Contexto privado de NPCs

- `engine/AutomaticTimeline.kt`: XP, nível alcançado, magia/habilidade aprendida, domínio de perícia e alocação de pontos agora geram registros legíveis na timeline. Promessas de NPC permanecem fora da timeline automática porque o evento não comprova que o jogador presenciou a promessa.

- `ai/PowerMenu.kt`: valida novamente o par efeito/alvo no ponto de execução, como defesa em profundidade caso uma definição incoerente escape da validação do pacote.

- `engine/AutomaticTimeline.kt`: promessas continuam fora da timeline automática porque o log atual não prova que o jogador presenciou a promessa; evita vazar compromissos de NPCs fora de cena. Mudanças de relação com o personagem aparecem em linguagem neutra.
- `engine/AutomaticTimeline.kt`: registra mudanças de relação envolvendo o personagem em linguagem neutra, sem expor valores numéricos ou motivos privados.
- `engine/AutomaticTimeline.kt`: inclui falhas de sobrevivência e gasto/recuperação de usos limitados na timeline, sem expor IDs internos de traços.

- `engine/Npc.kt` e `app/ui/AppModel.kt`: os contextos privados de NPC incluem motivações e relações junto de personalidade e objetivos; as relações são ordenadas por ID para manter a composição determinística. Esses dados seguem restritos ao contexto do mestre e não são expostos na interface do jogador.
- `ai/GameLoop.kt` e `ai/CheckMenu.kt`: rolagens com tabela agora respeitam imunidades a condições declaradas no pacote. A condição imune é ignorada e uma nota mecânica privada informa o mestre; a nota fica no `PlayInput` para não se perder ao regenerar a narração. `ai/Master.kt` injeta essas notas no contexto obrigatório e instrui explicitamente a narração a obedecê-las, sem mostrar termos técnicos ao jogador.
- **Pendente:** avanço automático das agendas por outras passagens de tempo e geração contextual de pistas.
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
