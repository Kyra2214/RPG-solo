# Auditoria técnica integral e independente — 14/11/2026

## Atualização posterior — CI Android #43655330273

A análise abaixo foi realizada antes da execução automatizada e registrava corretamente que nada havia sido executado naquele momento. Depois, o commit `f801b4f5` passou no [Android CI #43655330273](https://github.com/Kyra2214/RPG-solo/actions/runs/43655330273): ktlint, suíte Gradle completa, `:world:test`, validação de autoria, E2E do app, migrações/testes instrumentados de `data`, build e upload do APK debug. As pendências de execução de testes cobertas por esse workflow estão resolvidas; as lacunas de integração sem teste dedicado, validação visual física, release assinado e itens sem teste escrito continuam pendentes.

Base: `RPG-solo-LG-06-atualizado.zip` (projeto `RPG-solo-main`, 6 módulos Gradle, 1.350 arquivos, 349 `.kt`, 992 `@Test`).
Método: **somente análise estática**. Nada foi compilado, executado, testado nem baixado. Nenhum resultado de execução foi inventado.
Hierarquia aplicada: instruções do usuário > prompt de auditoria > Roadmap (critério) > especificações > código (evidência) > changelog.

## 1. Situação geral

O projeto é coerente e o código existe em volume real (engine 12,5 mil linhas, app 17,1 mil, ai 6,5 mil, rules 4,7 mil, world 5,4 mil, data 1,7 mil). Não há `TODO`, `FIXME`, `NotImplementedError` nem stubs vazios. Todas as importações internas resolvem, não há tipos redeclarados, nem strings duplicadas, e as 881 referências `R.string.*` têm definição em `strings.xml`.

O problema principal está na **integração**: vários módulos dos blocos MP/LG/WD existem no `engine`, mas nenhum ponto do app, da IA ou das regras os aciona. O roadmap os descreve como concluídos em código; o código prova apenas que a lógica isolada existe.

Nada do que segue foi validado em execução. Todo "Confirmado" significa: implementação identificada e compatível com o requisito por inspeção. **FUNCIONAMENTO NÃO VALIDADO EM EXECUÇÃO.**

## 2. Percentual real

Universo: 157 requisitos enumerados no Roadmap e anexos (R 5, M 10, I 9, U 9, Q 9, C 4, N 33, MU 8, CP 5, DC 8, MI 27, LG 6, RV 6, MP 8, WD 10). O item CG (criação por abas) tem plano próprio (`docs/CRIACAO_POR_ABAS.md`) e ficou fora da contagem.

| Categoria | Qtde |
|---|---|
| Confirmado | 118 |
| Parcial | 35 |
| Pendente | 3 (Q-08, MI-23, MI-24) |
| Impossível de confirmar | 1 (M-09) |

**Percentual real: 118 / 157 = 75,2 %** (confirmados sobre o total, sem meio ponto por parcial). O ≈79 % anterior não tinha recálculo.

Profundidade da inspeção, para ser honesto sobre os limites:
- **Profunda (código lido e chamadas rastreadas):** blocos LG, RV, MP, WD, MI-10 (Room), Q-04, README/CI/gradle/recursos e todos os cruzamentos automáticos do projeto.
- **Rastreabilidade (marcador do roadmap + rastro do ID em código, testes e changelog + existência dos arquivos citados):** R, M, I, U, Q, C, N, MU, CP, DC, MI-01 a MI-22. Esses itens não foram relidos linha a linha; um "Confirmado" ali depende do roadmap e do rastro, não de revisão funcional.

## 3. Divergências (19)

Classificação: ALTO 5 · MÉDIO 9 · BAIXO 5 · CRÍTICO 0. Nenhuma foi confirmada como erro de compilação; as marcadas "suspeita" dependem de execução.

| # | Gravidade | Módulo / requisito | Evidência no código | Correção necessária | Situação |
|---|---|---|---|---|---|
| 1 | ALTO | MP-02 / Q-04 — Room 13 | `RpgDatabase.VERSION = 13` e migração 12→13 existem; `data/schemas/.../13.json` não existe (só 2, 3, 11, 12). `RpgDatabaseMigrationTest` (linha 135) exige o arquivo da versão atual e `runMigrationsAndValidate` precisa dele. | Rodar `:data:kspDebugKotlin` e commitar o `13.json` gerado. Não foi escrito à mão para não fabricar `identityHash`. | **Pendente** (exige build) |
| 2 | ALTO | MP-06 — Alcance e Procurado | `IssueWanted`, `EscalateWanted`, `RenewWanted`, `RespondWanted`, `RegisterRelic`, `TransferRelic`, `Wanted.reach`, `Wanted.agents`: nenhuma chamada em `app`, `ai`, `rules` ou `data`. `GameState.wanted` nunca é preenchido; `Wanted.masterContext` lê sempre vazio. | Ligar emissão (IA via proposta validada e/ou fluxo do app) e a tela de respostas do jogador. | **Pendente** |
| 3 | ALTO | MP-07 — Fama e rumores | `StartRumor`, `SpreadRumors`, `ChangeFame`, `Fame.spread` e `Fame.effects` sem acionamento. Só `InheritChronicle` é usado (`CampaignStarter`). A fama herdada nunca muda nem produz efeito. | Acionar propagação por dia e aplicar `Fame.effects` (reconhecimento, preço, relação inicial). | **Pendente** |
| 4 | ALTO | MP-05 — Relação em eixos | `RelationAxes`/`RelationProfile`: 0 consumidores. O roadmap afirma que o MP-04 consome o perfil; `Reencounter` e `reencounterContextForMaster` não o usam. Além disso `RelationAxes.profile` lê `GameState.relations`/marcos pelo id da entidade da campanha atual; um recorrente de campanha antiga não é entidade do estado novo, então a ligação exige adaptador a partir de `RecurringCharacter.milestones`. | Criar o adaptador e injetar `RelationAxes.context` no reencontro. Mudança de projeto, não correção local. | **Pendente** |
| 5 | ALTO | LG-05 — Panteão entre campanhas | Requisito: tabela `legados`, interruptor "trazer lendas", até 3 legados como rumores, NPC que pergunta "você conheceu?", estágios de rumor 0–3. Código: nenhuma tabela `legados`, interruptor, nem estágios. Existe o panteão de leitura (RV-03/LG-06) sobre `personagem_recorrente`. O roadmap diz que o MP-02 "inclui LG-05", mas o MP-02 só persiste recorrentes. | Implementar interruptor, rumores de lendas e estágios, ou reescrever o requisito por decisão do autor. | **Pendente** |
| 6 | MÉDIO | MP-01 — Marcos | `RecordMilestone`, `LearnMilestone`, `ReviseMilestone` sem acionamento fora do motor; `MilestoneContext.forNpc` sem consumidor. Só nascem marcos automáticos (morte de herói, derrota viva); HUMILHAÇÃO, TRAIÇÃO, AJUDA etc. nunca são gerados. | Via de entrada para a IA/app e injeção de `MilestoneContext` no contexto do mestre. | **Pendente** |
| 7 | MÉDIO | MP-08 — Mundo próprio | `CustomWorldProposer` e `CustomWorld.install`: sem chamadas na UI ou no fluxo de nova campanha. O roadmap reconhece a ligação como adiada ao MP-07, que não a entregou. | Ligar à tela "Criar o meu mundo". | **Pendente** |
| 8 | MÉDIO | LG-02 — Memória do herói | `LegacyMemory.pendingPins` sem consumidor; a emissão automática dos `NoteMemory` fixados, prometida para MP-02/LG-05, não ocorre. | Emitir as notas fixadas ao carregar vínculos. | **Pendente** |
| 9 | MÉDIO | WD-01 — Validador de mundo | `ValidadorAutoria` (`:world`) sem consumidor e sem teste. O plano descreve "validador em `rules`, com testes no CI; erro de autoria quebra o build". | Chamar o validador num teste e corrigir a descrição. | **Fechado em 14/11/2026** (consumidor `ValidacaoAutoria` + tarefa `:world:validarAutoria` em `check` e no CI; descrição corrigida; teste só na conclusão da fase) |
| 10 | MÉDIO | LG-02 a LG-06, RV-01 a RV-06, MP-01 a MP-08, WD-10 — testes | Único teste do bloco: `LegadoLg01Test`. Nenhum teste referencia `Memorial`, `Pantheon`, `Rivals`, `Defeat*`, `Milestone*`, `Fame`, `Wanted`, `Reencounter`, `OffscreenRules`, `CustomWorldValidator`. O adiamento é decisão registrada, mas a fase foi declarada sem submódulo pendente. | Escrever os testes no fechamento da fase, com compilador disponível. Não foram escritos aqui: um teste sem compilar pode quebrar o `./gradlew test` inteiro. | **Pendente** |
| 11 | MÉDIO | README — estado | Afirmava Room v12 (código: 13) e CI verde no commit `21b90e1`, sem registro em `docs/VALIDACAO_CI.md` (último verde registrado: `036d445`, 25/10) e sem cobrir os blocos de 14/11. | Reescrever com a evidência disponível. | **Corrigido** (documentação) |
| 12 | MÉDIO | Roadmap §9 linhas 5 e 6 | Dizia "só LG-01 está em código" e "nada implementado", contradizendo §3.2 e o código. | Atualizar o estado. | **Corrigido** (documentação) |
| 13 | BAIXO | Roadmap §1 / README | "Room versão 10"; README: "tudo escrito e ainda não compilado". | Atualizar. | **Corrigido** (documentação) |
| 14 | BAIXO | Progresso | Percentual ≈79 % mantido sem recálculo; "bloco concluído" sem distinguir integração. | Recalcular (75,2 %). | **Corrigido** (documentação) |
| 15 | BAIXO | WD — conteúdo | `relatorio-validador` aponta V-06: NPCs e cidades com fonte "proposto" (aguardam o autor), p.ex. 95 de 95 NPCs de Aeltharyn. Pendências editoriais: 1 general de Aeltharyn (WD-03), migrantes sem cidade (WD-08), rota duplicada em Coroas de Pedra (WD-09). Contagens do roadmap conferem (10/10/8/8/10 cidades; 17/13/10/6/9 = 55 rotas; 64 facções). | Decisão do autor. | **Pendente** |
| 16 | BAIXO | `strings.xml` | 6 strings sem uso: `char_skills_title`, `statpoints_ability_note`, `skilltree_initial_title`, `char_points`, `char_skills_help`, `skilltree_initial_complete`. | Remover quando confirmado em build com Lint. | **Pendente** |
| 17 | MÉDIO | R-04 — conteúdo de terceiros | Marcado ✅ "resolvido no código", mas a decisão 9 do §5 deixa a reescrita do histórico Git pendente do proprietário. | Executar `git filter-repo` (fora do repositório). | **Pendente** (marcado Parcial) |
| 18 | BAIXO | MP-04 — Reencontro | Assunções documentadas: "marcos novos = 0" e nível do NPC = nível do herói. Orçamento de aparições nunca sobe com marcos. | Contador por aparição e função de escala do pacote. | **Fechado em 14/11/2026** (contador `newMilestones` e `ReencounterLevel`, escala no motor) |
| 19 | MÉDIO (suspeita) | Q-04 — esquema 12 | `12.json` tem `identityHash` idêntico ao de `11.json` (`ae1743ec…`), embora tenham 17 e 16 entidades. Indica arquivo copiado e editado à mão, não gerado pelo build. Não confirmado; a regeneração do build o substitui. | Regenerar junto com o `13.json`. | **Pendente** |

## 4. Verificações estáticas que passaram

- Importações `dev.rpgsolo.*`: 0 sem resolução real (7 falsos positivos conferidos à mão: `fun interface`, extensões, `R` gerado).
- Dependências entre módulos coerentes: `app`→todos; `data`→`engine`,`world`; `ai`→`engine`,`rules`; `rules`→`engine`,`world`(api); `engine` e `world` sem dependência de Android.
- Aliases do `libs.versions.toml` usados nos `build.gradle.kts` existem.
- Cadeia de migrações Room 1→13 contínua; entidade `PersonagemRecorrenteRow` e SQL da migração 12→13 coincidem.
- CI: caminhos referenciados existem (`Mythralis/motor/testes`, upload de `data/schemas`). O E2E é gate (sem `continue-on-error`).
- LG-06 conferido: `Memorial.kt`, `MemorialPanel.kt`, `AppModel.memorialCards`, item `memorial` na aba Jogar, strings `memorial_*` com formatos compatíveis com os argumentos.
- Blocos RV-01 a RV-06, LG-01, LG-03, LG-04, MP-02, MP-03 e WD-10 estão ligados ao fluxo (`AppModel` → contexto do mestre / `PlayScreen`).

## 5. Correções realizadas

Somente documentação: README, Roadmap (cabeçalho, §1, §3.2, §9, nova §10), Changelog, `docs/PROGRESSO_EXECUCAO.md` e este relatório. **Nenhum código-fonte foi alterado.**

Motivo: as 5 divergências ALTAS e a maior parte das MÉDIAS são lacunas de ligação que exigem decisão de projeto (adaptador de recorrentes, ponto de emissão pela IA, tela) ou geração por build. Implementá-las sem poder compilar arriscaria quebrar o CI, e o prompt proíbe funcionalidade nova fora do roadmap. Foram documentadas como pendência.

## 6. Situação arquitetural

Separação de módulos preservada; sem dependência circular; `AppModel.kt` continua concentrando integração (cerca de 2,9 mil linhas; R-03 parcial). Sem refatoração feita.

## 7. Limitações da análise estática

- Sem compilação: erros de tipo, de Compose e de KtLint não foram detectáveis.
- Os resultados de CI citados nos docs não puderam ser verificados.
- O rastreamento de chamadas foi por busca textual de símbolos; chamadas por reflexão ou geradas não aparecem (não há indício de uso delas).
- Itens fora do bloco LG/RV/MP/WD foram classificados por rastreabilidade, não por leitura integral.

## 8. Validação prática que cabe ao usuário

1. `./gradlew ktlintCheck test :app:assembleDebug` e conferir `13.json` gerado.
2. `./gradlew :data:connectedDebugAndroidTest` (migrações 2→13).
3. Escrever e rodar os testes dos blocos LG-02..06, RV, MP, WD-10.
4. Decidir as pendências 2 a 9.
