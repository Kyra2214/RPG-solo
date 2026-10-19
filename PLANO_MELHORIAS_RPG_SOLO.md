# PLANO DE MELHORIAS — RPG Solo

Análise completa do projeto `RPG-solo-ficha-para-ia.zip` (módulos `engine`, `rules`, `ai`, `data`, `app`), feita por leitura do código, dos pacotes de regras e do `PLANO_DESENVOLVIMENTO.md`. **Nada no projeto foi alterado para este documento.**

> Limite da análise: não consegui compilar nem rodar o app (sem Gradle/SDK e sem rede). O que está dito sobre o comportamento das telas vem da leitura do código e precisa ser confirmado rodando no Android Studio. Itens assim estão marcados com **(verificar)**.

> **Atualização de implementação — 18/10/2026:** este arquivo registra o diagnóstico original, não o estado atual completo. A branch `implementation/code-first-combat` já implementa várias melhorias descritas abaixo: Room/autosave e backup Android, chave protegida, importação de pacotes, combate integrado ao app, equipamento, condições com duração, biblioteca de fichas, mapa, capítulos, missões, linha do tempo, preferências de geração e ações de primeiro uso. Consulte `CHANGELOG.md` e `docs/PLANO_DESENVOLVIMENTO.md` para o estado da branch. Builds, testes, lint e CI foram deliberadamente adiados a pedido do usuário; os itens de validação continuam abertos até serem executados.

> **Atualização code-first — 19/10/2026:** streaming SSE foi conectado ao transporte, aos provedores OpenAI-compatible/Anthropic, ao loop de jogo e à prévia progressiva da narração na tela. A tela também ganhou descrições semânticas dos dados para TalkBack. Alguns bônus/penalidades numéricos explícitos em `tracos.txt` agora entram na mecânica. Isso não equivale a implementar todos os 513 traços: usos por cena, gatilhos ficcionais, imunidades e efeitos que dependem do contexto ainda precisam de modelo mecânico próprio. O streaming e as alterações Compose **não foram compilados nem executados**.

Legenda de esforço: **P** = até 1 dia · **M** = 2 a 5 dias · **G** = mais de 1 semana.
Legenda de prioridade: 🔴 bloqueia ou corrompe · 🟠 muda a experiência de jogo · 🟡 melhora · 🟢 polimento.

---

## 1. Resumo executivo

O desenho é sólido: o motor decide tudo, a IA só narra e propõe, o log de eventos permite desfazer e reproduzir, e o formato de pacote de regras é flexível (já aguentou o d6 por pontos e os dados reais sem mudar o resto). A maior parte do risco está **fora do motor**:

1. **O app Android nunca rodou** (`app` e `data` nunca foram compilados; o README admite). Antes de qualquer melhoria, é preciso compilar, rodar e corrigir.
2. **Nada é salvo de verdade.** A campanha é uma demonstração em memória, o Room não está ligado e a chave de API some ao fechar. Girar a tela provavelmente zera tudo (item 3.1).
3. **O combate existe só no motor.** Nenhum código do app ou da IA inicia combate, rola iniciativa ou aplica ataque; a IA é proibida de propor combate. O sistema próprio nem tem tabela de dano nem equipamento.
4. **Quase todos os traços são só texto.** Dos 513 registros de `tracos.txt`, só 18 têm bônus que o motor aplica. O resto (vantagens, desvantagens, poderes) é descrição que a IA lê, sem efeito mecânico garantido.
5. **A interface de dados não acompanhou a mecânica nova.** Só o d6 é desenhado com pontos; d4, d8, d10, d12 e d20 aparecem como número num quadrado, e o cartão mostra "dificuldade N" mesmo nos testes de pontos.

Ordem sugerida: **(A) fazer rodar e salvar → (B) combate e equipamento → (C) efeitos reais dos traços e condições → (D) dados e interface → (E) conteúdo e acabamento.**

---

## 2. O que já está bom (manter)

- Separação em camadas (ficha / sistema / ambientação / regra adicional / campanha) e motor independente de sistema.
- Log de eventos append-only, desfazer com os mesmos dados (`Dice` com seed por posição), backup em texto no motor.
- IA sem poder de rolar dado, criar entidade, iniciar combate ou mudar vida diretamente; resposta inválida desfaz tudo.
- Segredos de NPC só vão à IA quando a cena exige; vida enviada em palavras, nunca em números.
- Economia de IA: classificador barato, ações locais sem IA (ver ficha, inventário, descansar), modelo premium só em crítico.
- Cobertura de testes alta nos módulos puros (relato do README: rules 94, ai 93, engine 89 de 90 na última verificação).
- CI do GitHub já existe (`.github/workflows/android-ci.yml`): roda testes e gera APK de debug.

---

## 3. Diagnóstico e melhorias — Fundação (prioridade máxima)

### 3.1 Estado do app se perde (🔴, M)
**Achado:** `RpgSoloApp.kt` cria `AppModel` com `remember { AppModel() }`. Ao girar a tela, trocar tema do sistema ou o Android matar o processo, a Activity é recriada e o `AppModel` nasce de novo: sessão demo, chave de API, capítulos e missões voltam ao zero. **(verificar)**
**Melhoria:**
- Mover o estado para `ViewModel` (com `SavedStateHandle` onde fizer sentido) ou injetar o `AppModel` pela `Application`.
- Decidir DI de uma vez (o plano 1.1.2 previa Hilt ou Koin e foi pulado). Para um app deste tamanho, Koin ou injeção manual numa classe `AppContainer` basta.
- Trocar `remember` por `rememberSaveable` onde o estado é só de tela (campos de texto, aba selecionada).

### 3.2 Persistência real (🔴, G)
**Achado:** o módulo `data` tem Room (`CampanhaRow`, `EventoRow`, `FichaRow`) e `RoomEventStore`, mas o app só usa `InMemoryEventStore`. Fichas, capítulos, missões, dossiês de NPC, mapa de lugares e resumos não têm tabela.
**Melhoria:**
- Ligar `RoomEventStore` ao `CampaignSession` do app; reabrir a campanha ao iniciar.
- Tabelas novas: `capitulos`, `missoes`, `npc_dossie`, `lugares`, `resumos`, `preferencias`.
- **Snapshots** do estado a cada N eventos para campanhas longas (hoje reabrir reproduz o log inteiro; o teste de 4.500 eventos passa em menos de 5 s no PC, mas o celular é mais lento). **(verificar no aparelho)**
- Migrações versionadas e teste instrumentado do Room.
- Lista de campanhas (várias abertas, continuar, renomear, apagar, duplicar).

### 3.3 Chave de API segura (🔴, P)
**Achado:** a chave só vive na memória do processo (`AppModel`, comentário no próprio arquivo; tela de Config avisa que é apagada ao fechar).
**Melhoria:** `EncryptedSharedPreferences` (ou Android Keystore) com botão "esquecer chave"; nunca incluir a chave no backup exportado.

### 3.4 Compilar e rodar de verdade (🔴, M)
- Rodar `./gradlew test :app:assembleDebug` e corrigir o que aparecer (o `app` e o `data` nunca passaram por compilador).
- Confirmar `PersistenceTest` e `Backup` com o serializador JSON real (a única falha conhecida é artefato do ambiente).
- Rodar o que mudou na última etapa: `./gradlew :engine:test :rules:test` com os testes novos de dados (`PointCheckTest`, `PointCheckPackageTest`, `SistemaProprioDadosTest`).
- Primeira execução em aparelho real: medir tempo de abertura, consumo de bateria e dados (item 11.2.2 do plano, ainda aberto).

### 3.5 Tutorial da mecânica de dados (🟢, concluído no código; validar)
**Estado atual:** `tutorial.txt` do sistema próprio já descreve perícias por pontos, dados por dificuldade, ataque d20, dano, iniciativa, equipamento, condições e progressão. O achado acima é histórico e não deve ser tratado como pendência de implementação. Ainda é necessário conferir a apresentação no app depois da compilação.

---

## 4. Mecânica

### 4.1 Combate completo (🟠, G)
**Achado:**
- O motor tem `StartCombat`, `NextTurn`, `EndCombat`, iniciativa rolada (`rollInitiative`) e `CombatState`, mas **nenhum código em `ai` ou `app` os chama**.
- `Proposals.kt` proíbe a IA de propor `start_combat`, `end_combat`, `next_turn` e `create_entity`. Resultado prático: não há como aparecer um inimigo com ficha, e o painel mostra "em combate" que nunca é ativado.
- O sistema próprio **não tem `dano.txt`** (o parser aceita `DamageDef`), nem armas, nem armaduras.

**Melhoria (proposta de fluxo):**
1. A IA pode **pedir** combate com um campo novo (`request_combat` com os inimigos da cena, no mesmo estilo de `request_check`); o **motor** valida, cria as entidades inimigas a partir de modelos do pacote (ou do `NpcFactory`, hoje sem uso no app) e rola a iniciativa.
2. Turno do jogador: botões **Atacar · Defender · Usar item · Habilidade · Fugir**. Ataque = teste de d20 contra a defesa; dano pela tabela do pacote.
3. Turno dos inimigos: o **motor** resolve (ataque do NPC contra o jogador, esquiva do jogador em d6 por pontos), e a IA só narra o resultado já decidido.
4. Painel de combate: ordem de iniciativa em palavras ("você, depois o arqueiro"), rodada, condições visíveis. Sem barras de vida de NPC (regra 10.1.5 do plano); usar "ileso / ferido / gravemente ferido".
5. Fim: XP (`GainXp`), saque (itens), fuga e rendição como desfechos válidos.

### 4.2 Equipamento, itens e economia (🟠, G)
**Achado:** item é só `itemId` + quantidade (`ItemChanged`). Não há catálogo de itens, equipar/desequipar, dano de arma, defesa de armadura, peso/carga, dinheiro, loja nem criação (crafting). A IA pode dar `add_item` com qualquer id.
**Melhoria:**
- `itens.txt` no pacote: id, nome, tipo, efeitos (bônus de teste, dano, defesa, cura), uso único, valor.
- Slots de equipamento (mão, corpo, acessório) e comando `Equip`/`Unequip` no motor.
- Dinheiro como recurso do pacote; compra e venda com validação do motor.
- IA só concede itens que existem no catálogo **ou** itens "narrativos" sem estatística (marcados como tal), nunca com números inventados.
- Tela de inventário (ver 5.4).

### 4.3 Perícias com pontos por perícia (🟠, M)
**Achado:** hoje a perícia é um **traço sim/não** com bônus fixo (`atletismo:+2`), e o teste `esquiva`/`pericia` usa o atributo Habilidade como pontos. O jogo que você descreveu ("2 pontos em esquiva", "percepção", "flerte", "escalar", "procurar", "caçar") pede **pontos por perícia** de 0 a 5.
**Melhoria:**
- Modelo: `Ficha.skills: Map<String, Int>` (0 a 5) e `pericias.txt` com id, nome e atributo-base opcional.
- Ponto de partida do teste de pontos = pontos da perícia (talento "mestre em…" soma +2, como já funciona via bônus de traço).
- Criação de ficha: distribuir pontos de perícia (custo e teto no `criacao.txt`).
- Lista de perícias com os nomes que você citou, mais as já existentes (atletismo, furtividade, percepção…), sem duplicar.

### 4.4 Efeitos mecânicos reais dos traços (🟠, G)
**Achado:** dos 513 registros de `tracos.txt`, **18 têm bônus aplicado pelo motor**. As 219 vantagens, 197 desvantagens e 6 poderes são, na prática, texto que a IA interpreta. `criacao.txt` (regra 6) afirma "o motor aplica as penalidades" das desvantagens, o que hoje **não acontece**; o próprio arquivo de plano admite que "o motor valida custo e requisitos, mas ainda não aplica bônus".
**Estado atualizado:** o formato de bônus numérico por escopo já existe e alguns traços explícitos foram ligados (por exemplo, penalidades de furtividade/ofícios e bônus de percepção/atletismo). Isso é parcial; não representa gatilhos condicionais, contadores de uso, imunidades ou efeitos de recursos. **Próxima implementação:** ampliar o modelo de efeitos tipados sem inferir mecânica a partir de texto livre.

**Melhoria original:** coluna de **efeitos tipados** em `tracos.txt`, começando pelos mais comuns:
- bônus ou penalidade em pontos num teste (já existe);
- bônus de recurso máximo (+PV, +PM);
- uso limitado ("1 vez por cena", "1 vez por descanso") com contador no estado;
- gatilho de desvantagem (o motor aplica a penalidade quando o mestre marca o gatilho);
- repetir uma rolagem (gasta PA ou "sorte");
- imunidade/resistência a uma condição.

Fazer primeiro as ~40 vantagens e desvantagens dos arquétipos mais usados e deixar o resto como texto, marcado visualmente como "narrativo" na ficha.

### 4.5 Condições com duração e efeito por rodada (🟠, M)
**Achado:** as 7 condições (caído, atordoado, exausto, sangramento, visão turva, amaldiçoado, abençoado) só afetam teste (Ganho/Perda por atributo). Não têm duração, não causam dano por rodada, não expiram sozinhas e não empilham.
**Melhoria:** `condicoes.txt` com duração (rodadas, cenas, até tratar), dano ou cura por rodada, e remoção por teste (`cuidados` estanca sangramento). Adicionar **envenenado** e **queimando** (você citou envenenado). O motor avança as durações ao `NextTurn` e ao descanso.

### 4.6 Regra de dados: ajustes finos (🟡, P a M)
Sobre o que acabou de ser implementado (`POINTS`, dado trocado pela dificuldade):
- **Resolvido no código (19/10):** zero pontos mantém a chance mínima de uma face; cada ponto positivo acrescenta uma face até o teto do dado. A fórmula e o simulador foram alinhados; a tabela empírica será recalculada durante a validação.
- **Teto no d4:** 3 faces. Um talento +2 em teste fácil pode não mudar nada; mostrar isso na tela ("já no máximo neste dado").
- **Sem parcial nem crítico no d6 por pontos.** Opções: face máxima natural = sucesso crítico; face 1 natural = falha com complicação.
- **Vantagem/desvantagem de rolagem** (rolar 2 dados e ficar com o melhor/pior) como alternativa ao Ganho/Perda por pontos, para o mestre usar em situações.
- **Testes opostos** (jogador contra NPC) e **testes em grupo**.
- **Pontos de sorte/destino** (usando PA): repetir uma rolagem ou somar uma face.
- **Calculadora de chance** antes de rolar ("chance de sucesso: 50%") calculada pelo motor, nunca pela IA.
- Conferir se as faixas de dificuldade (8/12/15/18 no d20 e d4 a d10 nas perícias) estão equilibradas; fazer uma **simulação** (script Kotlin ou Python) com a taxa de sucesso por pontos × dificuldade e anexar a tabela ao `docs/`.
- Corrigir `tutorial.txt` (item 3.5).

### 4.7 Dano e letalidade padronizados (🟡, M)
**Achado:** a rolagem de letalidade (d6: 1 menos letal, 6 mais letal) é montada **pela IA** na hora (`request_roll`, com `damage` por face); o motor só limita à vida máxima.
**Melhoria:** **perfis de perigo** no pacote (leve, médio, grave, mortal) com dano base por face e teto por nível do personagem. A IA escolhe o perfil, o motor fornece os números. Isso reduz inconsistência entre cenas e protege o equilíbrio.

### 4.8 Morte, recuperação e permadeath (🟡, M)
**Achado:** PV chegando a 0 mata na hora (`deathResource`).
**Melhoria:** modo configurável: **caído** em 0 PV com testes de sobrevivência (3 sucessos/3 falhas em d6 por pontos), morte definitiva opcional, e **legado** (o próximo herói herda algo). Conversa bem com `Revive` que já exige justificativa.

### 4.9 Magia e recursos PA/PM (🟡, G)
**Achado:** PA e PM existem como recursos, mas nada os consome além de `SpendResource` genérico; não há lista de magias, custos, alcance ou efeitos. As fórmulas de PA, PM e PV estão marcadas como **rascunho** em `incerto.txt`.
**Melhoria:** `poderes.txt` (magias e habilidades com custo, alvo, teste e efeito tipado), uso por PA/PM e recarga no descanso. Fechar as fórmulas de recursos e tirar o aviso de rascunho.

### 4.10 Progressão (🟡, M)
**Achado:** o motor sabe subir de nível por XP (`EngineConfig.levelThresholds`, `GainXp`), mas o sistema próprio não tem `progressao.txt`, nada chama `levelFor` no app e não existe tela de evolução.
**Melhoria:** tabela de XP, recompensa por nível (pontos de atributo/perícia, nova vantagem), tela "subir de nível" com validação do motor, histórico da ficha (item 5.2.2 do plano).

### 4.11 NPCs, relações e relógios (🟡, G)
**Achado:** `NpcFactory`, `NpcSheet` e `DossierBook` existem no motor/regras, mas `NpcFactory` e `NpcSheet` não são usados em `ai`/`app`; relações e relógios existem como comandos e no contexto da IA, sem tela e sem persistência.
**Melhoria:** gerar NPCs relevantes por modelo (item 8.1.4 do plano), persistir dossiês, mostrar relações **em palavras** e relógios como "ameaça crescendo" no diário, instruir o mestre no prompt sobre quando avançar relógios (pendente no README).

---

## 5. Interface

### 5.1 Dados e cartão de rolagem (🟠, M)
**Achado (`DiceUi.kt`):** só o d6 tem desenho (pontos); qualquer outro dado é o número num quadrado arredondado. O cartão mostra sempre "dificuldade N", o que está errado para o teste de pontos (agora o motor converte para "d8 · acerta com 6–8").
**Estado atualizado:** os seis tipos de dados têm formas geométricas e o cartão identifica faces de sucesso/margem; rolagens reveladas ficam no controlador. Descrições semânticas dos dados foram adicionadas para TalkBack, preservando resultado oculto até a revelação. O restante da seção continua como acabamento pendente.

**Melhoria original:**
- Formas por dado: d4 triângulo, d8 losango, d10 e d12 polígonos, d20 hexágono com facetas, d6 como já está.
- Para testes de pontos: **faixa de faces** com as que acertam destacadas ("acerta com 6, 7 ou 8") e a face sorteada marcada.
- Para d20: "d20 + 3 contra 12", com a **margem** do resultado ("passou por 2", "faltou 1") e o limiar de sucesso parcial e crítico visíveis.
- Chips que explicam por que o teste mudou: "Perda (visão turva)", "+2 mestre em esquiva", "dado d8 (difícil)".
- Háptico ao assentar; botão "pular animação"; respeitar "reduzir movimento" do sistema; som opcional.
- **Histórico de rolagens** da sessão (lista compacta).
- **(verificar)** `revealed` no chat é `remember` simples (não persiste ao trocar de aba ou girar): ao voltar, as rolagens antigas podem reaparecer como "não reveladas" e esconder a narração seguinte. Guardar "rolagem já revelada" no estado do controlador, não na tela.

### 5.2 Aba Jogar (🟠, M)
- **Streaming da narração:** `AiProvider.stream` existe, mas a tela mostra a resposta toda de uma vez depois de esperar. Mostrar o texto chegando.
- **Painel rápido expansível:** hoje só PV/PM/PA, condições e missão. Adicionar "ver ficha", "inventário", "diário" como painéis inferiores (as ações locais já existem em `LocalActions`, falta o botão).
- Botões de atalho: **ver ficha · inventário · descansar** (sem IA, sem custo).
- **Cena atual:** local, NPCs presentes (nome e atitude em palavras), relógios visíveis como pistas.
- Copiar/compartilhar texto, buscar no histórico, tamanho da fonte, modo leitura.
- Tratar erro de rede com **tentar de novo** no próprio balão; indicador de offline.
- Teclado: ajustar `imePadding` e rolagem para a última mensagem ao abrir o teclado.

### 5.3 Primeiro uso e navegação (🟠, M)
**Estado atualizado:** a tela "A mesa ainda está vazia" agora apresenta explicitamente os três primeiros passos — configurar/testar provedor, criar ou escolher herói e criar campanha — com botões diretos. Isso melhora a descoberta, mas ainda não é um assistente transacional que guarde o progresso de cada etapa.
**Melhoria restante:**
- Assistente completo com presets de endpoint/modelo, estado de conclusão por etapa e transição automática até Jogar.
- Reduzir a barra para 4 destinos (Jogar, Herói, Diário/Campanhas, Mais) e mover Regras, Sistemas e Config para "Mais".
- Ícones vetoriais no lugar de emoji; transições entre telas.

### 5.4 Heróis, inventário e ficha (🟠, M)
**Achado:** `CharactersScreen` cria e valida ficha, mas a biblioteca é em memória; não há ficha para **ler** durante o jogo, nem inventário, nem evolução, nem retrato.
**Melhoria:**
- Ficha de leitura com atributos, perícias (com pontos), vantagens/desvantagens com ícone de "mecânico" ou "narrativo", e recursos.
- Inventário e equipamento (depende de 4.2).
- Duplicar, arquivar, exportar ficha .
- Retrato opcional (imagem local) e notas livres.

### 5.5 Campanhas, diário e mapa (🟡, M)
- Várias campanhas com continuar e apagar; **backup e restauração** pela interface (o `Backup.kt` do motor já existe, falta a tela e o seletor de arquivo SAF).
- Diário em abas: pistas, pessoas, lugares, linha do tempo.
- **Mapa de lugares:** `PlaceMap` existe no motor e não é usado no app. Mostrar locais descobertos e rota textual.
- Objetivos de missão (o modelo tem; a tela só lista o título).
- Resumo de capítulo **automático** ao atingir um tamanho, em vez de depender de abrir e fechar capítulo à mão.

### 5.6 Regras e Sistemas (🟡, M)
- **Cola rápida** da mecânica (tabela de dado × dificuldade, faces de sucesso por pontos) gerada do próprio pacote.
- **Glossário** (condições, termos de combate).
- Importar pacote por arquivo (10.5.2). **PDF do usuário e licenças foram descartados**: só links de consulta (3DeT e D&D).
- Ocultar da aba Sistemas o que o usuário não precisa ver hoje (edições do catálogo sem pacote) ou separar em "disponíveis" e "em breve".

### 5.7 Configurações (🟡, M)
- **Testar conexão** e mostrar o erro claro (sem internet, chave inválida, limite).
- Preferências de geração (item 10.6.2, ainda aberto): tamanho da narração, tom, nível de detalhe de violência/conteúdo (**linhas e véus**), idioma.
- Mostrar custo estimado (não só tokens) e limite diário opcional.
- Tela de backup e de privacidade com o aviso de que o texto da jogada vai ao provedor.

### 5.8 Acessibilidade e idioma (🟢, M)
- Strings fixas em português dentro do código: mover para `strings.xml` (facilita revisão e eventual tradução).
- **Parcial no código:** dados têm descrição semântica para TalkBack, sem revelar o resultado antes da hora; a escala global da fonte (85%–140%) já é configurável e persistida. Ainda falta revisar cada botão, áreas de toque, contraste nos dois temas e a experiência completa com TalkBack.
- Suporte a tablet/paisagem continua pendente (hoje a interface é majoritariamente coluna única).

---

## 6. IA e mestre

| Tema | Achado | Melhoria | Prior. | Esf. |
|---|---|---|---|---|
| Memória longa | Orçamento de 2000 tokens por pedido; o resumo depende do jogador fechar capítulos | Resumo rolante automático + "o que importa lembrar" por NPC e por lugar | 🟠 | M |
| Criação de entidades | IA proibida de criar entidade; sem isso não há inimigo nem NPC com ficha | Fluxo `request_npc`/`request_combat` validado pelo motor (ver 4.1) | 🟠 | M |
| Prompt do mestre | Regras 1 a 9 cobrem testes e rolagens; faltam regras de ritmo, de cena e de combate por turnos | Acrescentar guia de ritmo, de pergunta ao jogador ("o que você faz?") e de combate com a ordem de turnos | 🟡 | P |
| Dados na narração | O prompt manda não citar números; com dados variáveis, a IA pode descrever "um dado alto" de forma errada | Enviar ao mestre só o desfecho em palavras ("acertou por pouco", "falha feia") calculado pela margem | 🟡 | P |
| Segurança de conteúdo | Existem limites no catálogo (`4A.4.3`), mas sem configuração na tela | Linhas e véus configuráveis na Config e injetados no prompt | 🟡 | M |
| Qualidade | Testes usam transporte falso; ninguém mede a qualidade da narração | Conjunto de **cenas de referência** gravadas (entrada + saída esperada de estrutura) para pegar regressão de prompt | 🟡 | M |
| Provedores | Dois provedores sobre HTTP; primeiro provedor gratuito ainda não definido (6.1.2) | Testar limites reais de 2 ou 3 provedores gratuitos e deixar presets; opção de modelo local no aparelho (futuro) | 🟡 | M |
| Cache | Cache do bloco fixo (6.4.4) pendente | Marcar o prompt fixo como cacheável nos provedores que suportam | 🟢 | P |
| Fila | Cada papel (barato, narrador, premium) tem fila própria de 20/min | Fila única por endpoint | 🟢 | P |

---

## 7. Qualidade, segurança e entrega

- **Testes de interface:** nenhum teste de Compose. Começar com 3 testes de tela (Jogar com rolagem, criação de ficha, nova campanha) e testes de captura de tela (screenshot) do cartão de dados.
- **Testes instrumentados** do Room (migrações) e do carregamento de recursos (`BundledContent`).
- **Lint e estilo:** adicionar `ktlint` ou `detekt` ao CI.
- **Release:** regras de R8/ProGuard para `kotlinx.serialization` e Room; ícone, nome e versão; assinatura do APK; `versionName` ligado a uma tag.
- **Logs e diagnóstico:** relatório de erro local (sem prompt, sem chave) exportável pelo jogador.
- **Privacidade:** `allowBackup="false"` já está certo; garantir que o backup exportado nunca leve a chave.
- **Documentação:** o README tem uma lista longa de status que se repete e parte dela está desatualizada (por exemplo, diz que o app tem "6 abas, telas vazias"). Criar `CHANGELOG.md`, `docs/MECANICA_DADOS.md` (a regra do d6 por pontos, dados reais e dificuldade) e deixar o README só com "como abrir, módulos e estado atual".
- **Equilíbrio:** um simulador de combate e de testes (item 4.6) rodando no CI para avisar quando uma mudança de pacote quebra o equilíbrio.

---

## 8. Roteiro sugerido

### Etapa 0 — Fazer rodar (1 semana)
3.4 compilar e corrigir · 3.5 corrigir `tutorial.txt` · 3.1 estado sobrevive à rotação · 3.3 chave segura.
**Pronto quando:** o app abre no aparelho, permite configurar a IA, jogar uma cena com teste e girar a tela sem perder nada.

### Etapa 1 — Salvar de verdade (1 a 2 semanas)
3.2 Room ligado · lista de campanhas · backup e restauração pela tela (5.5) · resumo automático (seção 6).
**Pronto quando:** fechar o app no meio da cena e reabrir mantém campanha, ficha, capítulos e missões.

### Etapa 2 — Mecânica de jogo (3 a 5 semanas)
4.3 pontos por perícia · 4.1 combate · 4.2 equipamento · 4.5 condições com duração · 4.4 efeitos tipados (primeiro lote) · 4.7 perfis de perigo.
**Pronto quando:** uma luta completa (iniciativa, ataques, dano, condição, fuga ou vitória, XP) roda sem a IA decidir nenhum número.

### Etapa 3 — Dados e interface de jogo (2 a 3 semanas)
5.1 dados e cartão · 5.2 aba Jogar (streaming, atalhos, cena) · 5.3 primeiro uso · 5.4 ficha e inventário · 4.6 ajustes de dados e simulação.
**Pronto quando:** um jogador novo configura, cria herói e joga sem ajuda, e entende o que cada rolagem significa.

### Etapa 4 — Profundidade (contínua)
4.8 morte e legado · 4.9 magias · 4.10 progressão · 4.11 NPCs e relógios · 5.5 mapa · 5.6 importar pacote · 5.7 preferências de geração.

### Etapa 5 — Acabamento
5.8 acessibilidade e idioma · seção 7 release · seção 6 provedores e qualidade de narração.

---

## 9. Primeiras 10 tarefas (ganhos rápidos)

1. Compilar e rodar `./gradlew test :app:assembleDebug`; corrigir erros.
2. Atualizar `tutorial.txt` com a regra de dados nova.
3. Mover o `AppModel` para `ViewModel`.
4. Guardar "rolagem revelada" no controlador, não na tela.
5. Guardar a chave com `EncryptedSharedPreferences`.
6. Desenhar d4, d8, d10, d12 e d20 e mostrar "acerta com X–Y" no teste de pontos.
7. Botões de atalho ver ficha, inventário e descansar (já existem como ações locais).
8. Botão **testar conexão** na Config.
9. Script de simulação da taxa de sucesso por pontos × dificuldade, com a tabela em `docs/`.
10. Ligar `RoomEventStore` e reabrir a última campanha ao iniciar.

---

## 10. Decisões que preciso de você

1. **Primeiro ponto de perícia:** decisão aplicada no código: vale uma face adicional em relação a zero pontos. A validação deve confirmar se o equilíbrio em jogo fica bom.
2. **Combate:** quer por turnos com botões (Atacar, Defender, Usar item…) ou mais narrativo, com a IA conduzindo e o motor só validando?
3. **Morte:** permadeath, "caído com testes de sobrevivência" ou configurável por campanha?
4. **Perícias:** manter os traços atuais (sim/não com +2) ou migrar para pontos por perícia (0 a 5), como você descreveu?
5. **Primeiro provedor gratuito** para os presets (precisa de teste de limites reais).
6. **Magias:** haverá lista de magias do sistema próprio, ou os poderes continuam só como vantagens descritivas?
7. **Escopo de plataformas:** só celular, ou tablet e paisagem também?
8. **Idioma:** só português, ou preparar para traduzir?
