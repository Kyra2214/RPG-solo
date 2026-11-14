# Criação de personagem por abas: plano e estado

**Estado:** este documento **substitui** o antigo `PLANO_CRIACAO_GUIADA_E_CARTOES.md` (removido em 13/11/2026). Primeira versão do código escrita em 13/11/2026 a partir dos protótipos aprovados pelo autor. **Não foi compilado nem testado** (sem Gradle no ambiente de escrita). Rodar `./gradlew :app:ktlintCheck :app:testDebugUnitTest :app:assembleDebug` antes de aceitar.

## O que mudou

| Arquivo | Papel |
|---|---|
| `app/.../ui/CriacaoAbas.kt` | Tela: abas, blocos, grades, painéis e os sete passos. |
| `app/.../ui/CriacaoDados.kt` | Leitura de reinos, cidades e povos nativos; taxonomia; nomes curtos; prévia das árvores. |
| `rules/.../packages/sistema-proprio/taxonomia-criacao.json` | Cópia de `docs/catalogo/taxonomia-proposta.json` (categoria e subgrupo por id de classe e profissão). |
| `app/src/main/res/values/strings_criacao_abas.xml` | Textos novos (`wiz_*`). |
| `app/.../ui/CharactersScreen.kt` | Alternância "Por abas" × "Modo avançado"; o fluxo de gravação é o mesmo do formulário. |
| `app/.../ui/CharacterCreationCatalog.kt` | `CharacterChoices` com `originKind`, `originKingdomId`, `originCity`. |
| `app/.../ui/AppModel.kt` | Linha "Origem narrativa do herói" no contexto da IA. |
| `app/.../ui/CriacaoRascunho.kt` | Rascunho salvo da criação (CG-4.8). |
| `app/.../ui/CriacaoSorteioNomes.kt` | Sorteio de nomes por reino (CG-4.5). |
| `app/src/test/.../CriacaoDadosTest.kt` | Testes dos dados (reinos, povos, sub-raças, taxonomia, nomes curtos, árvores, origem). |

## Fluxo

1. **Raça:** cinco reinos (nome oficial no botão) mostram só os povos nativos (grade de 4 colunas, nome completo). Escolhe-se o povo, a sub-raça (opcional, filtrada pela linhagem local) e a **cidade de nascimento**. A aba **Outros** tem **Forasteiro** (qualquer raça, reino e cidade onde vive) e **Mestiço** (os 20 híbridos do catálogo; cidade de um dos pais; sem reino próprio).
2. **Classe:** 6 categorias, subgrupos e blocos de 5 colunas com nome curto; painel com armadura e armas do catálogo.
3. **Profissão:** 10 categorias, blocos de 4 colunas; "Continuar sem profissão" é permitido.
4. **Status:** distribui os pontos pelo `CharacterDraft` (trava no limite do pacote); a prévia de vida, magia e ações vem de `RulePackage.resourcesFor`.
5. **Habilidades:** prévia das árvores de raça, sub-raça (ou mestiço), classe e profissão, com nível mínimo de cada habilidade. Os 2 pontos iniciais (`initialAbilityPoints` do pacote) evoluem as **habilidades gratuitas** de origem: contador de pontos e botões Evoluir/Desfazer no painel da habilidade (CG-4.11).
6. **Vantagens:** lista do pacote com bloqueio visual de quem o rascunho não aceita, motivo do bloqueio no painel e o equilíbrio vantagens × desvantagens vindo do motor.
7. **Informações:** nome, sexo, idade, aparência, personalidade e história. Aparência, personalidade e história juntas viram a lore aprovada da ficha (`approveLore`, de 40 a 4000 caracteres, ou ficam em branco). O botão **Criar ficha** só habilita sem pendências e lista o que falta.

## Decisões

**Tomadas pelo autor (14/11/2026):**
- **Forasteiro:** a ficha guarda o reino e a cidade onde **vive hoje** (já é assim no código).
- **Habilidades:** os 2 pontos iniciais são gastos **na criação**, para **evoluir as habilidades gratuitas** de origem (raça, sub-raça, classe, profissão). Cada evolução custa 1 ponto (`SkillTreeProgression.upgradeCost`). Comprar habilidades novas continua dependendo do nível mínimo e fica para a campanha.
- **Mestiço:** aceita **qualquer duas raças**, e os bônus das duas raças **somam com teto de +3 por escopo** (CG-3.6 do plano antigo). As 20 combinações nomeadas do catálogo continuam como atalhos.
- **Código de cartões do plano antigo:** **removido** (`ui/cartoes/Cartao.kt`, `CartaoTest.kt` e `docs/CONTRATO_CARTAO.md`); a tela nova lê `CharacterCatalogEntry` direto.

**Trabalho de código decorrente:**
1. ✅ **Evolução na criação (CG-4.11, 14/11/2026, não compilado):** `CharacterChoices.initialSkillUpgrades` (id → evoluções, no JSON da ficha); `EstadoCriacao.evolucoes` valida contra as gratuitas das árvores escolhidas e o saldo vem de `pkg.engineConfig().initialAbilityPoints`; `AppModel.applyInitialSkillUpgrades` roda em `activate`, depois de conceder as gratuitas: chama `grantInitialAbilityPoints` (uma vez) e `SkillTreeActions.unlockOrUpgrade` por evolução (1 ponto cada, validado pelo motor). ASSUMINDO: aplicação ao iniciar a campanha, pois só ali existe a entidade; falha vira `configError`, sem desfazer a ficha. Pendente: teste da aplicação (fica para o fim da fase).
2. ✅ **Mestiço livre (CG-4.12, 14/11/2026, não compilado):** `CharacterChoices.mixedRaceIds` (exatamente duas raças distintas; exclui raça, sub-raça e híbrido nomeado; validado em `validateChoices`). A aba Outros → Mestiço ganhou a grade "Mestiço livre" (toca em duas raças; a cidade de nascimento vem das duas). `CharacterCreationCatalog.selectedEffects` soma os bônus das duas raças por escopo com teto de +3 (`mixedRaceEffects`, `MIXED_SCOPE_CAP`; penalidades passam inteiras), no mesmo ponto em que `selectedCatalogBonuses` aplica os bônus de raça. As árvores das duas raças entram no loadout de habilidades e no contexto da IA. ASSUMINDO: o teto vale para a soma dos bônus positivos de cada escopo; acima dele vira um único efeito `mestico:<escopo>` de +3. Os 20 híbridos nomeados seguem como atalhos.
3. Atualizar testes (`CriacaoDadosTest` e os de catálogo) e o checklist de aceite.

## Entregas posteriores

- ✅ **CG-4.3 (14/11/2026, não compilado):** pílula "N de 7 etapas prontas" no topo das abas (`CriacaoPorAbas`, callback `aoProgresso`) e diálogo de confirmação ao trocar para o Modo avançado com progresso nas abas (`CharactersScreen`; as escolhas das abas se perdem). Textos `wiz_progress` e `wiz_leave_*`.

- ✅ **CG-4.6 (14/11/2026, não compilado):** o resumo da aba Informações mostra a origem e os bônus do catálogo somados por escopo (`selectedEffects`, já com o teto +3 do mestiço livre); some se as escolhas têm erro. Texto `wiz_review_bonuses`.

- ✅ **CG-4.1 (14/11/2026, não compilado):** a regra de conclusão dos sete passos saiu da tela para `CriacaoPassos.kt` (Kotlin puro: `ProgressoCriacao`, `concluidos`, `proximo`, `anterior`, `primeiroPendente`), pronta para teste unitário no fim da fase. A tela usa `concluidos` e ganhou o atalho \"Ir para a primeira etapa pendente\" (`wiz_go_pending`). O estado de seleção (`EstadoCriacao`) continua em Compose.

- ✅ **CG-1.4 (14/11/2026, não compilado), versão enxuta:** campo \"Buscar pelo nome\" nas abas Classe e Profissão (`PassoGrupos`; `CriacaoBusca.normalizar`, sem acento nem maiúsculas); com texto, mostra os resultados em uma grade só, sem categorias. Raças (reinos e povos) ficam sem busca; busca entre abas não foi feita.

- ✅ **CG-2.5 (14/11/2026, não compilado):** o painel de detalhe (`PainelEntrada`, usado em raça, sub-raça, mestiço, classe e profissão) ganhou \"Ver detalhes\": lore completa, demais benefícios e desvantagens e os bônus do catálogo somados por escopo. No mestiço livre, mostra a prévia combinada das duas raças já com o teto de +3 (`mixedRaceEffects`).

- ✅ **CG-1.7 (14/11/2026, não compilado):** o bloco da grade (`Bloco`) fala o nome e o selo (nível mínimo ou \"gratuita\") em vez do glifo, tem papel de botão e mantém o estado selecionado para o TalkBack. Falta a conferência em aparelho.

- ✅ **CG-2.6 (14/11/2026, não compilado):** abaixo da escolha de raça, o cartão \"Comparar com outra raça\" mostra lado a lado (em sequência) bônus do catálogo, benefícios e desvantagens da raça atual e da escolhida (`ComparadorRacas`; `EstadoCriacao.comparaId`). Só compara raças, não sub-raças.

- ✅ **CG-3.5 (14/11/2026, não compilado):** na aba Classe, o cartão \"Classes adicionais (híbrido)\" permite até 3 classes além da principal (`EstadoCriacao.classesExtras` → `CharacterChoices.additionalClassIds`, validado por `validateChoices`); despertam nos níveis 10, 20 e 30 pelo fluxo que já existia. As árvores das extras entram na aba Habilidades e no resumo. Classes amaldiçoadas seguem só no Modo avançado.
- ✅ **CG-4.5 (14/11/2026, não compilado):** botão "Sortear nome" na aba Informações (`SorteioNomes.sortear`, em `CriacaoSorteioNomes.kt`). O tom vem do reino de origem da escolha (`originKingdomId`), não do povo, porque o cânone diz que raça não determina o nome (M-10, W-01); sem reino, usa o tom geral. Evita os primeiros nomes reservados dos reinos 1 a 3 e o nome já digitado. ASSUMINDO: sílabas e epítetos próprios; sem regra por sexo. Teste unitário fica para o fim da fase.

- ✅ **CG-4.8 (14/11/2026, não compilado):** rascunho salvo e retomável (D-CG-09) em tabela nova `criacao_rascunho` (Room v12, migração 11→12). `CriacaoRascunho.kt` serializa o passo, as escolhas das abas e o `CharacterDraft`; a tela grava com atraso de 0,8 s e restaura ao abrir; o rascunho é apagado ao criar a ficha. ASSUMINDO: um só rascunho; descartado se o pacote de regras mudou; `12.json` do schema escrito à mão (o build regrava o hash).

- ✅ **CG-4.9 (14/11/2026, não compilado):** mensagem curta por aba sob as etapas (`PassoCriacao.mensagem`, `wiz_msg_*`): instrução enquanto pendente e confirmação quando pronto; Informações segue com a lista de pendências.

- ✅ **CG-4.10 (14/11/2026, escrito e não executado):** `CriacaoPassosTest`, `SorteioNomesTest` (JVM) e `CriacaoAbasTest` (Compose, instrumentado). Pendente: testes do rascunho salvo (4.8), das mensagens (4.9), da aplicação das evoluções (4.11) e do mestiço livre (4.12), além de rodar tudo no CI.

- ✅ **CG-10.1 (14/11/2026, não compilado):** conquistas locais (5 marcos) listadas na aba Campanhas.
- ✅ **CG-6.3 (14/11/2026, não compilado):** tema do app (sistema/claro/escuro) em Config.
- ✅ **CG-6.2 (14/11/2026, não compilado):** modo de jogo por campanha (`ModoJogo.kt`, `AppModel.gameMode`), instrução ao mestre e seleção na aba Campanhas.
- ✅ **CG-8.1 (14/11/2026, não compilado):** chips de aparência e "Sortear aparência" na aba Informações (`CriacaoAparencia.kt`).
- ✅ **CG-7.1 (14/11/2026, não compilado):** título "Crônicas salvas" na lista de campanhas salvas (D-CG-02).
- ✅ **CG-6.1 (14/11/2026, não compilado):** cartões de mundo na revisão da nova campanha (`CartaoMundo`).
- ✅ **CG-5.4 (14/11/2026, não compilado):** na revisão da nova campanha, heróis prontos criam a ficha, a selecionam e escolhem Mythralis.
- ✅ **CG-5.3 (14/11/2026, não compilado):** botão "Jogo rápido com este herói": herói + nome sorteado + status em rodízio + uma vantagem válida, direto na aba Informações.
- ✅ **CG-5.2 (14/11/2026, não compilado):** cartão "Heróis prontos" na aba Raça; "Usar este herói" preenche raça, origem, classe, profissão, idade e sexo (`aplicarHeroi`), sem nome. Jogo rápido segue pendente.
- ✅ **CG-1.8 (14/11/2026, não compilado):** `draft.report()` lembrado por `est.tick`/id do rascunho e gravação do rascunho por `snapshotFlow` fora da composição. Sem medição em aparelho.

- ✅ **CG-3.4 (14/11/2026, não compilado):** no Modo avançado, "Classe de Malditos" fica atrás de um botão desligado por padrão, com aviso (D-CG-06); as abas seguem sem esses conceitos. **CG-3.6 (resumo combinado de bônus)** já estava coberto pelo CG-4.6 (revisão por escopo) e pelo mestiço livre com teto +3 (CG-4.12): sem código novo.

- ✅ **CG-3.9 (14/11/2026, não compilado):** painel da classe com "Arma principal" (primeiro tipo de `weaponTypes`) e "Também usa" (demais). Só apresentação; ordem auditada no CG-3.7.

- ✅ **CG-3.7 (14/11/2026, não compilado):** auditoria editorial da ordem de `weaponTypes`: 20 das 50 classes tiveram a arma principal (primeiro tipo) ajustada ao conceito; o conjunto de tipos não mudou, então nada muda na mecânica (N-13). 
- ✅ **CG-2.8 (14/11/2026):** auditoria de raças: integridade ok; achados (lore = summary, sem benefits/drawbacks) em `docs/relatorios/auditoria-racas-2026-11-14.md`, dependem de texto do autor.

- **Idade (CG-4.4):** o item do plano antigo (faixas por longevidade) foi **substituído** pela decisão de 12/11/2026: idade em anos e sexo na aba Informações, limitada à longevidade do povo no início da campanha (MI-26).

## O que veio do plano antigo

Conferido no projeto em 13/11/2026.

**Já aplicado (de forma própria desta tela):** decisões do CG-0.1 registradas; taxonomia de classes e profissões como recurso `taxonomia-criacao.json`, revisão editorial das armas feita (CG-3.7), demais campos ainda **rascunho** (CG-0.7); assistente em passos (parte do CG-4); painel de detalhe, chips e paginador simples (versão enxuta de CG-1.3, 1.4 e 1.5); cartões de raça, sub-raça e mestiço (CG-2.1, 2.3 e 2.4); classes e profissões por categoria, subgrupo e bloco (CG-3.1 e 3.2); navegação categoria → subgrupo → blocos para classes e profissões (CG-1.10).

**Substituído pela criação por abas:**
- Ordem dos passos da D-CG-03 (Modo → Mundo → Herói → Povo → Classe → Profissão → Idade → Nome → Aparência) → Raça → Classe → Profissão → Status → Habilidades → Vantagens → Informações. Modo, mundo e herói ficam na criação de campanha, fora desta tela.
- Categorias de raças da taxonomia e a categoria "Mestiços" → cinco reinos mais a aba Outros (Forasteiro e Mestiço).
- Carrossel de cartões como navegação (CG-1.1) → grades de blocos. O contrato de cartão (CG-0.2) foi removido do projeto.
- Passos separados de Idade, Nome e Aparência → uma aba Informações; aparência em texto livre.

**Pendente e compatível com as abas** (nada disto foi aplicado):
- *Cartões e catálogo:* texto editorial de raças (lore, benefícios e desvantagens; auditoria 2.8 feita), revisão editorial da taxonomia (D-CG-11) **feita em 14/11/2026**, sem mudança de agrupamento.
- *Visual:* kit visual sem imagem **feito** (0.4 e 0.5: `CriacaoTokens` em `Theme.kt` e medalhão do glifo no `Bloco`) e **imagens por IA com cache** (módulo M12, plano em `docs/PLANO_IMAGENS.md`; o bloco já tem lugar para imagem, e o glifo serve de fallback).
- *Fora da criação:* heróis prontos (M5, 6 por mundo: **dados e leitor feitos (CG-5.1) e cartões na aba Raça feitos (CG-5.2) em 14/11/2026**: `herois-prontos.json`, `HeroisProntos.kt`, `HeroisProntosCartao` e `EstadoCriacao.aplicarHeroi`; Jogo rápido da criação feito no CG-5.3: `CriacaoJogoRapido.kt`; ligado à nova campanha no CG-5.4: `AppModel.criarFichaDeHeroi` e seção "Jogo rápido" em `NewCampaignSection`; M5 concluído) e Jogo rápido (M5), cartões de mundo (**feitos no CG-6.1**, 14/11/2026), modo de jogo História × Criativo (**feito no CG-6.2**, 14/11/2026) e temas (M6, pendentes), navegação (M7, D-CG-01 e D-CG-02: abas mantidas, **Criar** já é a aba Heróis; **Crônicas** nos salvos **feito no CG-7.1**, 14/11/2026), aparência por chips e sorteio (**feitos no CG-8.1**, 14/11/2026) e retrato por IA (M8, pendente), voz e Modo ao vivo (M9, adiado até o CG-9.1), conquistas locais (M10), qualidade transversal (M11).

**Sem plano antigo para estas partes** (criadas só aqui): abas de Status, Habilidades e Vantagens; reinos, cidades de nascimento e forasteiros.
