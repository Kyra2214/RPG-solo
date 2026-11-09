# Plano de integração do motor de Mythralis no RPG Solo

> **Anexo do plano único.** Estado, ordem e pendências ficam em `PLANO_RPG_SOLO.md`, seção 9; este arquivo guarda só a especificação e as decisões deste tema.

**Estado:** plano escrito e decisões D-I2 e D-I5 a D-I7 mais o local do botão fechadas pelo autor em 08/11/2026. **Andamento: MI-01 a MI-07 concluídos; MI-08 parcial; MI-09 a MI-22, MI-26 e MI-27 escritos em 08 e 09/11/2026 e MI-25 (documentação) em 09/11/2026; MI-23 e MI-24 ⏸ pendentes (24 marcos de código escritos e MI-25 documentado, de 27; Python passa; o erro de compilação em `Dinamica.kt:98` foi corrigido no código); a Fase 21 ainda não foi aceita. Próximo: nenhum marco de código; restam as pendências MI-23 e MI-24 (ver "Pendências da Fase 25"). Testes e compilação ficam para o fim da Fase 22 (regra do projeto); `:world:test` e `ktlintCheck` verdes seguem como pré-requisito para aceitar a Fase 21. A partir do MI-09 o `app` e o `rules` declaram `project(":world")`.** Base: motor Python F0–F9 aceito (D-180), `Mythralis/plano/PLANO_MOTOR_MYTHRALIS.md` §6, §10, §17 e `Mythralis/plano/INTEGRACAO_FUTURA_RPG_SOLO.md`. Este arquivo é o plano da integração; `PLANO_RPG_SOLO.md` (§3.11) aponta para ele.

Legenda: ✅ feito · 🟡 parcial · ⬜ pendente. Esforço: **P** até 1 dia · **M** 2 a 5 dias · **G** mais de 1 semana. 🔶 = valor ajustável. ASSUMINDO = suposição minha, revisável.

## 1. Objetivo

1. Levar o simulador de Mythralis (5 reinos, 46 cidades, eras livres) para dentro do app, **offline**, em Kotlin.
2. Criar a tela **Simulação de tempo**: o jogador escolhe quanto tempo passa (de 1 dia a 1 era) e a velocidade (1x, 5x, 10x); o mundo evolui e o resultado entra na campanha.
3. Ligar o mundo à campanha: dia do mundo, Ecos como rumores, NPCs vivos, mundo anterior e ramificação (MP-07, MP-08, WD).

Princípios herdados: o **código decide, a IA narra**; molde estático + deltas; determinismo por semente (I-02); simulador não escolhe pelo jogador; nada de terceiros.

## 2. Pontos de partida (fatos do código)

| Item | Estado |
|---|---|
| Motor Python | 2.985 linhas em `motor/` (stdlib), 62 testes, lote 500 anos × 20 sementes sem falha; ~2 a 6 min por rodada de 500 anos em 1 núcleo |
| Saída de uma rodada de 500 anos | cerca de 240 mil deltas e 58 MB de `cronica.jsonl`, ainda mais no `notificacoes.jsonl`; inviável guardar inteira no aparelho |
| Dados | molde em `reinos/` + `dinamica/*.txt` (13 arquivos) + catálogo `personagem-catalogo.json` |
| RNG | `random.Random(int(sha256("semente|motor|dia")[:16], 16))`: Mersenne Twister do Python; réplica exata em Kotlin exige portar a semeadura e os métodos usados |
| Hash | `sha256` encadeado sobre JSON com chaves ordenadas e separadores `,` `:`; réplica exige o mesmo formato de número e texto |
| App | módulos `app`, `engine`, `rules`, `ai`, `data` (Room v10); 6 abas; `GameState.day`; `Clock`, `Place`, `NpcDossier`, `CanonFacts`, pipeline DC (`SourceContext`) já existem |
| Tempo no app | `GameState.day` corre por cena, viagem e descanso; hoje não há ligação com um calendário de mundo |

## 3. Decisões de arquitetura (propostas, ASSUMINDO)

| # | Decisão | Proposta |
|---|---|---|
| D-I1 | Como ter o motor no app | **Portar para Kotlin puro**, em novo módulo Gradle `world` (sem Android), ao lado de `engine`. Alternativa descartada por padrão: embutir Python (peso do APK e fronteira de erro) |
| D-I2 | Igualdade com o Python | ✅ **decidido pelo autor em 08/11/2026: resultado idêntico** (mesmos hashes) para as mesmas sementes: portar MT19937 com `init_by_array`, `random`, `randrange`, `choice` e o que mais o motor usar, e o JSON canônico. Os arquivos de conformidade (entrada → hash esperado) saem do Python e os dois motores passam igual (MP §17). Se ficar caro demais, fallback: motor Kotlin estatisticamente equivalente, validado pelas métricas da §14, sem hash igual |
| D-I3 | O que guardar no aparelho | **Snapshot do estado** a cada bloco de 10 anos (retomada) + **crônica filtrada** (importância 2 ou mais, e nível 1 e 2) + último hash e contagem de deltas. A crônica completa não é guardada; o hash encadeado é calculado em fluxo. 🔶 janela de retenção do detalhe (ex.: último século completo) |
| D-I4 | Onde mora o mundo | Molde estático em `rules/src/main/resources/mundos/mythralis/` (texto com `|`, já decidido em WD); estado e crônica do mundo em Room (v11), com vínculo campanha → mundo (MP-07); o log da campanha **não** recebe os 240 mil deltas, só eventos de mudança que a campanha toca |
| D-I5 | Herói e simulação | ✅ **decidido pelo autor em 08/11/2026: o herói envelhece e pode morrer** pela tela de tempo. O herói é uma entidade da campanha (não do molde): roda por um motor próprio `heroi`, com gerador aleatório separado, usando as faixas etárias e a longevidade do povo (`dinamica/faixas_etarias.txt`, `longevidade.txt`, `classes_longevidade.txt`); **não entra no estado nem nos hashes do mundo**, para preservar a igualdade com o Python (D-I2). A morte aciona o legado do M-04 (permadeath: item de renascimento, assumir NPC, recuperar itens ou fim da campanha) |
| D-I6 | "1 era" | ✅ **decidido pelo autor em 08/11/2026: teto de 120 anos.** As eras são livres (D-M13): "1 era" = até o próximo gatilho de fim de era, parando no teto de 120 anos (o limite da Era I) se o gatilho não ocorrer; a tela mostra o teto antes de começar e avisa quando para por ele. O teto pode ser revisto após a medida de desempenho (MI-23) |
| D-I7 | Velocidade | É a velocidade de **exibição**, nunca altera o resultado (I-02). 🔶 1x = 1 ano de mundo por segundo; 5x e 10x multiplicam. O cálculo roda em blocos à frente da exibição; se o aparelho for mais lento, a exibição espera o cálculo |
| D-I8 | Notificações | Seguem D-M11: registrar e continuar; a tela tem a opção **parar em evento de nível 1** (equivalente ao `--interativo`) |

## 4. Tela "Simulação de tempo"

**Onde:** ✅ decidido pelo autor em 08/11/2026: painel "Passar o tempo" na aba Jogar; abre tela cheia.

**Escolha do período (um toque):** 1 dia · 1 semana · 1 lua (30 dias) · 1 estação · 1 ano · 10 anos · 1 século · **1 era (até o fim da era)** · campo livre "N dias/anos". O texto mostra a data do mundo de partida e a de chegada prevista.

**Velocidade:** 1x · 5x · 10x (D-I7). Para períodos de até 1 estação, o resultado aparece de uma vez.

**Durante a simulação:**
- calendário correndo (ano na contagem de cada reino, estação, lua);
- feed de eventos de nível 1 ao vivo (reis, guerras, pactos, quedas de coroa, fim de era); nível 2 em resumo por década; nível 3 só em contagem;
- barra de progresso por bloco de 10 anos, **pausar**, **retomar**, **cancelar** (volta ao último snapshot aceito) e **parar no próximo marco** (D-I8);
- aviso quando o aparelho estiver mais lento que a velocidade escolhida.

**Ao terminar:** resumo do período (marcos, reinos caídos, pactos novos, NPCs principais mortos e empossados), botão **Aplicar à campanha** (avança o dia do mundo e grava os efeitos) ou **Descartar**. Nada é gravado na campanha antes de aplicar.

**Estados de erro:** `FALHA` de invariante ou de bloco → para, mostra o último bloco bom e permite descartar; sem espaço no aparelho; app fechado no meio (retoma do último snapshot).

## 5. Fases e submódulos

Ordem rígida; um submódulo por vez; testes completos só no fim de cada fase (regra do projeto).

### Fase 21 — Núcleo do mundo em Kotlin (módulo `world`)

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| ✅ MI-01 | Módulo `world` no Gradle (Kotlin puro) e contrato de dados: `exportar_molde.py` gera os recursos do molde e de `dinamica/` para `rules/.../mundos/mythralis/` | arquivos gerados com hash do molde igual ao `validacao-mundo.txt` | M |
| ✅ MI-02 | Carregador e validador do molde em Kotlin (porta de `carregador.py`, `validador.py`, `validar_mundo.py`) | 0 erro e os mesmos 20 avisos do Python (o plano dizia 19; o `saidas/validacao-mundo.txt` atual registra 20, conferido no MI-01) | M |
| ✅ MI-03 | RNG idêntico ao Python (MT19937, semeadura por inteiro grande, métodos usados) e JSON canônico + sha256 (D-I2) | vetores de teste gerados pelo Python batem bit a bit | M |
| ✅ MI-04 | Calendário, estado, crônica e hash encadeado (`calendario`, `estado`, `cronica`, `importancia`) | hash do estado inicial e de 1 ano idêntico ao Python | M |
| ✅ MI-05 | Dinâmica (`dinamica/*.txt` → configuração) e motores de pessoas: demografia, sucessão, nomes, relógios, eventos | 10 anos de uma semente com hash igual ao Python | G |
| ✅ MI-06 | Motores de mundo: economia, ecos, mistérios, política, fronteira, diplomacia, guerra, povos | 100 anos de 3 sementes com hash igual | G |
| ✅ MI-07 | Simulador em blocos, invariantes I-01 a I-28, check de saúde, níveis (`niveis.py`), retomada | 500 anos de 3 sementes com hash igual e invariantes sem violação | G |
| ✅ MI-08 | Testes de conformidade: arquivos de entrada → hash esperado gerados pelo Python, rodados no CI do Kotlin | conjunto roda no `./gradlew test`; lote de 20 sementes comparado nas métricas da §14 | M |

**Fase 21 fecha quando:** o `world` reproduz o Python e o CI roda os testes de conformidade. **Escrita e entregue em 08/11/2026 (MI-08); a prova (suíte verde no CI) depende da primeira execução do Gradle, que ainda não ocorreu.**

### Fase 22 — Molde, persistência e vínculo com a campanha

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| 🟡 MI-09 | `WorldTemplate` (MP-08): molde carregado no app, escolha do mundo na criação da campanha (anterior, pré-configurado, próprio) | campanha nova escolhe Mythralis | M |
| 🟡 MI-10 | Room v11 com migração explícita: mundo, snapshots por bloco, crônica filtrada, vínculo campanha → mundo (D-I3, D-I4) | migração v10 → v11 sem perda; sem `fallbackToDestructiveMigration` | M |
| 🟡 MI-11 | Retomada e limpeza: continuar do último snapshot, janela de retenção, tamanho em disco | app encerrado no meio retoma do bloco anterior | M |
| 🟡 MI-12 | Ramificação de mundo (MP-07): cânone compartilhado, linha do tempo alternativa e cópia independente | duas campanhas no mesmo molde não se alteram | M |

### Fase 23 — Tela de Simulação de tempo

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| 🟡 MI-13 | `TimeSimulationController` no `app`: período, execução em `Dispatchers.Default` por blocos, pausa, retomada, cancelamento, parada em marco | controlador roda 1 era sem travar a UI; cancelar volta ao snapshot | M |
| 🟡 MI-14 | Controle de velocidade 1x, 5x e 10x (D-I7) com exibição desacoplada do cálculo | mesma semente e período dão o mesmo resultado em 1x e 10x | M |
| 🟡 MI-15 | Tela: seletor de período, calendário, progresso, botões e estados de erro (§4) | fluxo completo no aparelho, TalkBack e contraste revisados | M |
| 🟡 MI-16 | Feed de eventos por nível e resumo final (marcos, reinos, pactos, NPCs) | resumo bate com `niveis.md` do Python para a mesma semente | M |
| 🟡 MI-17 | **Aplicar à campanha**: avança `GameState.day` e grava os efeitos como eventos de mudança (um lote desfazível; o envelhecimento e a morte do herói entram com MI-26 e MI-27) | desfazer reverte tudo | M |

### Fase 24 — Mundo dentro da campanha

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| 🟡 MI-18 | Instanciação sob demanda: cidade, NPC e facção só entram no log da campanha quando a cena toca neles | cena numa cidade cria só o necessário | G |
| 🟡 MI-19 | Ecos viram rumores e reconhecimento (MP-07): notícia viaja pelo grafo, com versão exata, exagerada ou falsa | rumor chega a 1 passo e não a 4, como no exemplo do MP-07 | G |
| 🟡 MI-20 | Contexto do mestre: resumos por cena pelo pipeline DC; a IA só recebe o relevante à cena (critério 12) | contexto de uma cena não passa do orçamento de tokens | M |
| 🟡 MI-21 | Cânone: `CanonFact` ORIGINAL, DIVERGENCE e AI_CREATED ligados aos deltas; a IA não contradiz o cânone | contradição proposta pela IA é recusada | M |
| 🟡 MI-22 | NPCs recorrentes e vida fora de cena (MP-03, `NPCS_RECORRENTES_MEMORIA_ENTRE_CAMPANHAS.md`) | NPC principal morto na simulação aparece morto na campanha | G |
| 🟡 MI-26 | Idade do herói: campo de idade na ficha e na criação (hoje o engine não guarda idade), data de nascimento no dia do mundo, e motor `heroi` (envelhecimento, doença da idade e morte natural pela longevidade do povo, gerador próprio) | herói de 30 anos simulado por 1 século morre de idade em semente fixa e o resultado se repete; hashes do mundo inalterados | G |
| 🟡 MI-27 | Morte do herói na simulação: a tela interrompe no dia da morte, mostra o resumo e aciona o legado do M-04 (item de renascimento, assumir NPC, recuperar itens, fim da campanha); a simulação do mundo pode continuar sem o herói | morte no meio de 1 era para a tela, oferece as opções do M-04 e desfazer volta ao snapshot | M |

### Fase 25 — Validação e fechamento

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| ⏸ MI-23 | Desempenho em aparelho: tempo e memória de 1 era, 1 século e 500 anos em aparelho de entrada | metas medidas e registradas; teto da D-I6 ajustado | M |
| ⏸ MI-24 | Suíte completa, E2E da tela e lote de conformidade no CI; APK de debug | CI verde com os resultados no log | M |
| 🟡 MI-25 | Documentação final, README, CHANGELOG, `PLANO_RPG_SOLO.md` e `INTEGRACAO_FUTURA_RPG_SOLO.md` atualizados | projeto integrado e zip completo | P |

### Andamento

**MI-01 ✅ (08/11/2026; exportação rodada e conferida, Kotlin escrito e não compilado).**
- `Mythralis/motor/exportar_molde.py` copia os 120 arquivos do molde (`reinos/*/dados`, `mundo`, `referencia/dados-brutos`) e os 13 de `dinamica/*.txt` para `rules/src/main/resources/mundos/mythralis/` (1,9 MB), byte a byte, espelhando os caminhos de `Mythralis/` (o hash inclui o caminho relativo). Gera `manifesto.txt` (esquema 1: hash do molde, hash da dinâmica, contagens e, por arquivo, tipo, caminho, tamanho e sha256, na ordem do hash). Sem data no manifesto: rodar de novo gera o mesmo arquivo. `--verificar` confere o destino contra o manifesto, contra o carregador Python e contra o `saidas/validacao-mundo.txt`.
- **Resultado:** hash do molde `f11f1c120acc99b1…` (completo no manifesto), igual ao `validacao-mundo.txt`; adulterar um arquivo exportado é detectado.
- Módulo Gradle `world` (Kotlin puro, JVM 17, `include(":world")`) com `MoldeManifesto`/`ArquivoMolde`, o contrato de leitura do manifesto. Não depende de `rules` nem de Android: o carregador do MI-02 receberá os bytes por uma fonte injetável.
- `.gitattributes` com `-text` na pasta exportada, para que conversão de fim de linha não quebre o hash.
- ASSUMINDO: o hash do molde cobre as 9 `.md` de `mundo/` e o catálogo JSON, por isso eles vão no app (poucos KB); a dinâmica tem hash próprio (`dinamica_sha256`), fora do hash do molde, como no Python.

**MI-02 ✅ (08/11/2026; Kotlin escrito e não compilado; paridade com o Python ainda não medida).**
- Porta para `world` (pacote `dev.rpgsolo.world`): `TabelaMolde` (`ler_tabela`, `_split`, `toks`), `CarregadorMolde` (`carregar`; lê pelo manifesto e confere o hash do molde, sha256 encadeado com `MessageDigest`, contra o manifesto), `MundoMolde` e `CatalogoPersonagem`, `CalendarioReino` (só a validade; datas em MI-04), `DinamicaMinima` (só o que o W-09 lê; a configuração completa é MI-05) e `ValidadorMundo` (W-00 a W-09, mesmos textos e mesma ordem; `relatorio` gera o texto do `validacao-mundo.txt`).
- Fontes de bytes injetáveis (`MoldeFonte`): classpath (recursos do app), mapa em memória (testes). O módulo continua sem dependência de `rules` e de Android; usa só `kotlinx-serialization-json` para o catálogo.
- **Mudança no exportador (MI-01):** a mensagem do W-05 distingue "aparece só em prosa dos reinos" de "não aparece em nenhum reino", e a prosa dos reinos (`reinos/*/*.md`, vários MB) não vai para o app. O exportador passou a gravar `derivados/subracas-em-prosa.txt` (49 das 50 sub-raças do catálogo citadas na prosa, 1 KB), listado no manifesto como tipo `derivado`; fora do hash do molde. Assim o relatório Kotlin pode sair idêntico ao do Python. O hash do molde não mudou.
- **Pronto quando (0 erro e os mesmos 20 avisos):** a conferência automática é a MI-08 (`./gradlew test` compara `relatorio` com `Mythralis/saidas/validacao-mundo.txt`). Nesta etapa não há Gradle nem teste, por regra do projeto.
- ASSUMINDO: `\w` do Python vira `[\p{L}\p{N}_]` na busca de sub-raças (os IDs são ASCII); `int()` do Python vira `trim().toInt()` (valores do molde são números simples).

**MI-03 ✅ (08/11/2026; Kotlin escrito e não compilado; vetores gerados e conferidos só no lado Python).**
- `world/.../PythonRandom.kt`: réplica do `random.Random(int)` do CPython 3.12: semeadura `init_by_array` com o inteiro em palavras de 32 bits (little-endian, `BigInteger`), MT19937, `random()`, `getrandbits(k≤62)`, `randrange(n)`, `randrange(a, b)`, `randint(a, b)` e `choice(lista)` (rejeição por `bit_length`, como `_randbelow_with_getrandbits`). `sementeDoDia`/`doDia` reproduzem `int(sha256(\"semente|motor|dia\")[:16], 16)` do `simulador.py`. São os únicos métodos que o motor Python usa (`random`, `randrange`, `randint`, `choice`).
- `world/.../JsonCanonico.kt`: `Hash.sha256`, `Hash.encadear` e `JsonCanonico.canon` (igual a `json.dumps(ensure_ascii=False, sort_keys=True, separators=(',', ':'))`: escapes do Python, `\u00xx` minúsculo, chaves por ponto de código Unicode e não por unidade UTF-16). Sem ponto flutuante (o estado só tem inteiros; Double/Float lançam erro).
- `Mythralis/motor/exportar_vetores.py` (novo) gera `world/src/test/resources/conformidade/vetores-rng-json.txt` (276 vetores: RNG de 10 sementes até 64 bits, sementes por dia, JSON canônico e cadeia de hash); executado, determinístico (mesmo md5 em duas execuções).
- **Pronto quando (bate bit a bit):** a conferência automática é a MI-08 (teste Kotlin lê o arquivo de vetores). Por regra do projeto não foi criado teste nem rodado Gradle agora.
- ASSUMINDO: nenhum float no estado nem na crônica (conferência por busca em `motores/`, `estado.py`, `cronica.py`); chaves de mapa só String/Int/Long; texto sem substitutos soltos.

**MI-04 ✅ (08/11/2026; Kotlin escrito e não compilado; vetores gerados e conferidos só no lado Python).**
- `CalendarioReino.kt` (ampliado): `data`, `diaDoAno`, `anoBase`, `estacaoIdx`, `lua` e `construir` (módulo de piso, como o `%` do Python). `Importancia.kt`: regras de `importancia.py`. `Estado.kt`: `Estado.doMolde`, `aplicar` (campo, criação, era, registro, nascimento de Ecos), `estadoAnteriorDe`, `comoMapa` e `hash` (sem `proximo_dia`); a dinâmica entra pela interface `DinamicaEstado` (implementada em MI-05). `Cronica.kt`: cadeia de hash em fluxo (`CronicaSaida`, `guardar=false` não mantém os deltas em memória, D-I3), `criar`, `anexar`, `abrir` com conferência da cadeia. `Valores.kt`: igualdade profunda, cópia e leitura de JSON só com inteiros.
- `exportar_vetores.py` agora também gera `world/src/test/resources/conformidade/vetores-estado.txt` (147 vetores): datas de 5 reinos × 24 dias (inclui negativos), hash do estado inicial sem dinâmica (619 entidades) e com dinâmica (1.893), e 24 deltas em sequência com a importância e o hash do estado após cada um.
- **Pendente do "pronto quando":** hash do estado inicial **com dinâmica** e de **1 ano** só podem ser conferidos depois do MI-05 (a dinâmica e os motores anuais ainda não existem em Kotlin); o vetor `com_dinamica` já está gerado. A conferência automática dos demais é o MI-08.
- ASSUMINDO: `1 == True` do Python não ocorre no estado (a igualdade do Kotlin separa Boolean de número); não há float; `_lugar` e a ordem das entidades usam ordem por ponto de código, como o `sorted` do Python.

**MI-05 ✅ (08/11/2026; Kotlin escrito e não compilado; vetores gerados e conferidos só no lado Python).**
- `Dinamica.kt`: porta de `dinamica.py` (todas as tabelas de `dinamica/*.txt`, classe de longevidade, idade e dia de nascimento por faixa, `mortePm`, `eIdoso`) e `Dinamica.hash` (mesmo `hash_dinamica` do Python: nome + sha256 do texto de cada arquivo; **não** é o `dinamica_sha256` do manifesto, que usa outro cálculo). Implementa `DinamicaEstado`, então `Estado.doMolde` já aceita a dinâmica completa.
- `Motor.kt` (interface `Motor`, `Ctx`, `DeltaRascunho`, `causa`), `Nomes.kt` (`nomes.py`), `MotoresPessoas.kt` (`Demografia`, `Sucessao`, `Relogios`, `Eventos`, portas literais dos quatro motores) e `Simulador.kt` (núcleo: laço por dia, frequências, semente por motor e dia, emissão de deltas com importância e id de notificação, cabeçalho da crônica). Blocos, invariantes, check de saúde e retomada ficam no MI-07; o registro completo das notificações, no MI-16.
- `exportar_vetores.py` gera também `vetores-motores-pessoas.txt` (3 sementes × 10 anos, um ponto por ano: deltas, notificações, hash da crônica e do estado, rodando no Python só estes quatro motores). Cobre criação de NPC simulado, vacância, interino, sucessão (herdeiro, vice, candidato) e cadeia de evento. Determinístico (md5 igual em duas execuções).
- **Pendente:** a comparação Kotlin × Python (10 anos, 3 sementes, e o hash do estado inicial com dinâmica de `vetores-estado.txt`) roda no MI-08; nada foi compilado nem executado em Kotlin.
- ASSUMINDO: `str.split()`/`isupper()`/`capitalize()` do Python equivalem aos do Kotlin para o texto do molde; formatos numéricos com `Locale.ROOT`; o motor `politica` fica para o MI-06 (não está na lista do MI-05).

**MI-06 ✅ (08/11/2026; Kotlin escrito e não compilado; vetores gerados só no lado Python).**
- `MotoresMundo.kt`: portas literais de `politica`, `economia`, `ecos`, `misterios`, `diplomacia`, `fronteira`, `guerra` e `povos` (mesmas regras, ordem de sorteios, causas e textos), mais `Motores.pessoas`/`Motores.todos` (equivalente a `motores.criar`, 12 motores). `Diplomacia.agente` é compartilhado com a guerra, como `Diplomacia._agente`.
- `exportar_vetores.py` gera também `world/src/test/resources/conformidade/vetores-motores-mundo.txt` (33 linhas): 3 sementes (1, 7, 42) × 100 anos com os 12 motores, um ponto a cada bloco de 10 anos (deltas, notificações, hash da crônica e do estado, entidades, regime, eras). Determinístico (md5 igual em duas execuções); os demais arquivos de vetores não mudaram.
- **Pendente:** a comparação Kotlin × Python (100 anos, 3 sementes) roda no MI-08; nada foi compilado nem executado em Kotlin.
- ASSUMINDO: divisão inteira com piso (`floorDiv`) onde o Python usa `//` com possíveis negativos; `sorted` de pares `(lugar, rota)` por ponto de código (`Ordem.PARES`); `Motores.todos` mantém a ordem de criação (a ordem de execução é alfabética pelo simulador).

**MI-07 ✅ (08/11/2026; Kotlin escrito e não compilado; vetores gerados só no lado Python).**
- `Invariantes.kt` (porta de `invariantes.py`): `checarDelta` (F-00, I-03, I-05 a I-11, I-13, I-14, I-23 a I-27), `checarEstado` (I-04, I-15) e `checarBloco` (I-12, I-16 a I-18, I-20, I-28), com os mesmos textos. `Niveis.kt`: `Niveis` (`niveis.py`) e `ReinosMapa` (`Reinos` de `relatorio.py`). Os relatórios `niveis.md`/`relatorio.md` não entram (não afetam hash; ficam para a tela, Fase 23).
- `Simulador.kt` ampliado: blocos de `passo` anos (`avancarAte`/`rodar` devolvem `ResultadoRodada`), check de saúde por bloco (`BlocoSaude` = linha de `saude.txt`; FALHA interrompe), `relatorioInvariantes()` (texto de `invariantes.txt`) e retomada por `Snapshot` (`paraJson`/`deJson`; hash e contagem da crônica, estado via `Estado.doSnapshot`, `Cronica.continuar`). Persistência do snapshot é do MI-10; aqui só o gancho `aoFecharBloco`.
- `exportar_vetores.py --simulador` gera `vetores-simulador.txt`: 3 sementes × 500 anos, uma linha `BLOCO` por bloco de 10 anos (resultado, alertas, falhas, hash da crônica), `PONTO` a cada 50 anos e `INV` (sha256 de `invariantes.txt` e resultado final).
- **Pendente:** a comparação Kotlin × Python (500 anos, 3 sementes, invariantes sem violação) roda no MI-08; nada foi compilado nem executado em Kotlin. Registro completo das notificações (`notificacoes.jsonl`) segue no MI-16.
- ASSUMINDO: o id de delta existente em I-05/I-13 vale `d%06d` com número de 1 ao total da crônica (equivale ao conjunto de ids do Python, sem guardá-lo, D-I3); I-21 confere o id `nNNNNNN` contra o contador; I-01 (hash do molde/dinâmica a cada bloco) entra pelo parâmetro `conferirMolde` (o app liga no MI-09; o molde em memória é imutável); na retomada o mapa de reinos de entidades criadas vem do estado (NPCs simulados e Ecos) e `enraizados` começa vazio, como no Python; `rodar(Double)` arredonda par (`Math.rint`), igual ao `round` do Python.

**MI-08 🟡 (08/11/2026; suíte escrita; `vetores-lote.txt` gerado com 20 sementes; validação local parcial, Fase 21 ainda não aceita).** `LOTE_CACHE=<pasta>` no gerador guarda cada semente pronta e permite retomar após interrupção.
- `world/src/test/kotlin/dev/rpgsolo/world/`: `Conformidade.kt` (apoio: molde pelo classpath, leitura dos vetores, fábrica de `Simulador`) e seis classes JUnit 4, uma por conjunto de vetores do Python: `RngJsonConformidadeTest` (RNG, semente por dia, JSON canônico, sha256 e cadeia; MI-03), `EstadoConformidadeTest` (calendários, hash do estado inicial **com e sem dinâmica** e sequência de 24 deltas com importância e hash; MI-04), `MotoresConformidadeTest` (4 motores de pessoas, 3 sementes × 10 anos, e 12 motores, 3 sementes × 100 anos; MI-05 e MI-06), `SimuladorConformidadeTest` (500 anos × 3 sementes: linhas de saúde por bloco, pontos a cada 50 anos, sha256 de `invariantes.txt`, resultado e nenhuma violação; mais retomada por `Snapshot` no ano 50 igual à rodada contínua e ao ponto do ano 100 do Python; MI-07), `ValidadorConformidadeTest` (0 erro e 20 avisos; relatório byte a byte igual a `Mythralis/saidas/validacao-mundo.txt`; MI-02) e `LoteConformidadeTest`.
- **Lote:** `exportar_vetores.py --lote` gera `vetores-lote.txt` (sementes 1 a 20 × 500 anos, passo 10, 12 motores; 20 linhas `LOTE`: resultado, deltas, notificações, hash da crônica e do estado, entidades e as métricas da seção 14 derivadas dos deltas por `lote.estatisticas`: fim da Era I, primeiro incidente, primeira guerra, reinos caídos, mistérios resolvidos, relógios estourados e no 1º século, pactos formados e encerrados). O teste Kotlin roda as 20 sementes com a crônica em fluxo (`guardarDeltas = false`) e compara todos esses campos. As métricas de década (nível 1 por reino, décadas vazias, faixa 3 a 40) não são recalculadas em Kotlin: derivam só dos deltas, que já ficam presos pelo hash da crônica de cada semente.
- `world/build.gradle.kts`: os testes enxergam `rules/src/main/resources` (molde exportado) como recursos e usam heap de 1 GB. Nenhum código de produção foi alterado.
- **Resultado Python local:** `python3 -m unittest discover -s Mythralis/motor/testes -v` passou (62 testes em 97,743 s; dois `ResourceWarning` em `test_f8.py`).
- **Histórico das tentativas iniciais (antes das correções):** `./gradlew --no-daemon test` não chegou aos testes do app porque falta Android SDK (`ANDROID_HOME`/`sdk.dir`). Com JDK 17, `./gradlew --no-daemon :world:test` falhou na compilação em `Dinamica.kt:98` (comentário não fechado), causando referências não resolvidas subsequentes em `Invariantes.kt`, `MotoresMundo.kt` e `MotoresPessoas.kt`. `./gradlew --no-daemon ktlintCheck :world:test` parou antes dos testes por erros de indentação em `engine/AeltharynWorld.kt`; esse arquivo não foi alterado pelo ZIP MI-08. Nenhum CI GitHub foi executado.
- **Situação atual (08/11/2026):** o comentário não fechado de `Dinamica.kt` está corrigido (varredura estática sem comentário de bloco desbalanceado em `world/`); a suíte Python foi reexecutada (62 testes OK, 136,9 s). **Pendência de aceite:** registrar `:world:test` verde, tratar o ktlint (`engine/AeltharynWorld.kt`) e rodar o CI; só então fechar a Fase 21 (o MI-09 já foi escrito antes disso, por decisão do autor). **Fronteira com o app:** até o MI-08 `app/build.gradle.kts` não tinha `project(":world")`; a dependência entrou no MI-09 (abaixo). Tempo estimado do lote: 20 × 500 anos em Kotlin (Python: ≈ 2 min por semente).
- ASSUMINDO: os arquivos de vetores já gerados (MI-03 a MI-07) valem como contrato; o teste do validador lê `../Mythralis/saidas/validacao-mundo.txt` (diretório de trabalho do Gradle é o do módulo `world`, o CI faz checkout do repositório inteiro); `'resolvido' == true` do Kotlin equivale ao valor verdadeiro do Python (o campo é booleano).

**MI-09 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Molde carregado no app e escolha do mundo na criação da campanha.
- `world/WorldTemplate.kt` (novo): `OrigemMundo` (ANTERIOR, PRECONFIGURADO, PROPRIO), `ReinoResumo`, `WorldTemplate` (id, nome, descrição, reinos, contagens de cidades, assentamentos, NPCs, NPCs principais e facções, hash do molde e da dinâmica) e `WorldTemplates.mythralis` (carga única do molde do classpath, com o `CarregadorMolde` do MI-02; falha com mensagem se houver erro de carga).
- `rules/WorldOptions.kt` (novo): as três vias do MP-08 como `WorldOption`: **mundo próprio** (`id = null`, ambientação livre como antes), **pré-configurado** (Aeltharyn do catálogo do `engine` e **Mythralis** do molde) e **mundo anterior** (listado, mas indisponível até a persistência de mundos, MI-10 e MI-12). Se o molde não carregar, a opção Mythralis aparece indisponível com o motivo.
- `rules/CampaignStarter.kt`: aceita `worldId = "world.mythralis"`; recusa a criação se o molde não carregar; usa "Mythralis" como nome da ambientação e grava o **vínculo campanha → mundo** como fatos da campanha (`mundo.id`, `mundo.nome`, `mundo.origem`, `mundo.molde_hash`, `mundo.dinamica_hash`) por `RecordFact`; o aviso ao jogador diz que cidades, NPCs e facções ainda não entram na campanha (MI-18). Aeltharyn não mudou.
- `app/NewCampaignSection.kt`: o chip único de Aeltharyn virou a lista "Mundo da campanha" (carregada em `Dispatchers.IO`) com resumo da opção escolhida e o motivo das indisponíveis. Gradle: `rules` declara `api(project(":world"))` e `app` declara `implementation(project(":world"))`.
- **Limites desta etapa:** o vínculo vive nos fatos do log da campanha (o Room v11 com tabela própria é o MI-10); nenhum estado de mundo, snapshot ou simulação é criado ainda; a IA não recebe o conteúdo de Mythralis (MI-20).
- ASSUMINDO: o id do molde é `world.mythralis` (o `mundo.mythralis` do `reino.txt` fica como está) e `RecordFact` basta como vínculo provisório, a ser lido pelo MI-10 para migrar para a tabela.

**MI-10 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Room v11 com migração explícita 10 → 11.
- **Tabelas novas** (`Entities.kt`): `mundos` (id, molde e hashes, semente, progresso: `proximoDia`, `totalDeltas`, `hashCronica`, `ultimoBloco`), `mundo_snapshots` (PK mundo + bloco; `snapshotJson` canônico do `Snapshot`, `bytes`), `mundo_cronica` (PK mundo + `seq`; dia, nível, importância, tipo, entidade e a linha jsonl com o hash encadeado) e `campanha_mundo` (vínculo; apagar a campanha apaga só o vínculo; o mundo só sai sem campanhas, `RESTRICT`, por causa do cânone compartilhado do MP-07).
- **Migração** (`RpgDatabase.MIGRATIONS`, `VERSION = 11`): só `CREATE TABLE/INDEX IF NOT EXISTS`; nenhuma tabela existente é alterada, então campanhas e logs ficam intactos; segue sem `fallbackToDestructiveMigration` e com a cópia `.antes-v11.bak` já existente (CP-01.4). O arquivo `data/schemas/.../11.json` **ainda não existe**: é gerado pelo build do KSP e deve ser commitado depois.
- **`mundos` é inserido com ABORT e alterado com UPDATE**, nunca REPLACE: um REPLACE apagaria em cascata snapshots, crônica e vínculos.
- **Contrato** (`world/MundoStore.kt`): `MundoRegistro`, `SnapshotInfo`, `SnapshotGuardado`, `CronicaGuardada`, `VinculoMundo`, `FiltroCronica` e a interface `MundoStore` (criar mundo, vincular, `guardarBloco` numa transação, último snapshot, crônica filtrada, `voltarAoBloco` para cancelar, `bytesEmDisco`). Implementação: `RoomMundoStore` em `data` (módulo `data` agora depende de `:world`).
- **Ligação:** `AppModel.activate` chama `linkWorld`: se a campanha nasceu com os fatos `mundo.*` do MI-09, cria o mundo `mundo-<campanha>` (semente da campanha) e o vínculo no Room. Um mundo por campanha; compartilhar é o MI-12.
- **Filtro da crônica (D-I3):** 🔶 ASSUMINDO guardar só o delta de nível 1 ou 2 **e** importância 2 ou mais (`FiltroCronica`, limites ajustáveis); o texto do plano ("importância 2 ou mais, e nível 1 e 2") admite as duas leituras.
- **Limites desta etapa:** nada grava snapshot ainda (quem chama `guardarBloco` é o controlador do MI-13); sem migração dos fatos `mundo.*` de campanhas criadas com o build do MI-09 (só existiram em desenvolvimento); sem retenção nem limpeza de mundos órfãos (MI-11); sem teste de migração instrumentado (fim da Fase 22).

**MI-11 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Retomada, janela de retenção e tamanho em disco.
- `world/RetencaoMundo.kt` (novo): `PoliticaRetencao` (🔶 ASSUMINDO: crônica filtrada do último século, `100 × 365` dias contados de `proximoDia`, e os 5 últimos snapshots; ambos ajustáveis), `RetencaoMundo.limpar` (poda snapshots antigos e crônica fora da janela; devolve `ResultadoLimpeza` com bytes antes e depois) e `RetencaoMundo.limparOrfaos` (apaga só mundos sem campanha). `RetomadaMundo.retomar` devolve o `PontoRetomada` (último snapshot, ou molde se não houver) e realinha por `voltarAoBloco` se o progresso do mundo ou a crônica estiverem à frente do snapshot. Cada bloco é gravado numa transação (MI-10), então app encerrado no meio do bloco retoma do bloco anterior.
- `MundoStore` ganhou `espaco` (`EspacoMundo`: snapshots, linhas e bytes de cada um), `podarSnapshots`, `podarCronica` e `apagarMundo` (recusa se há campanha, `false`). Implementação em `RoomMundoStore` com novas consultas nos DAOs (`contar`, `apagarAntigos`, `apagarAntesDoDia`); sem mudança de esquema (Room continua v11, sem nova migração).
- **Limites desta etapa:** nada chama `retomar` nem `limpar` ainda (o `TimeSimulationController` é o MI-13); sem tela de espaço em disco (MI-15); sem teste (fim da Fase 22); o último snapshot nunca é podado, então o cancelamento (`voltarAoBloco`) continua possível.

**MI-12 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Ramificação de mundo (MP-07).
- `world/RamificacaoMundo.kt` (novo): `ModoMundo` e `RamificacaoMundo` com os três modos. **Cânone compartilhado** vincula a campanha ao mesmo mundo; **linha do tempo alternativa** cria `mundo-<campanha>` como cópia do estado de um bloco do mundo de origem (padrão: o último) e diverge; **cópia independente** cria `mundo-<campanha>` direto do molde, sem estado. Os dois últimos criam mundo novo, então duas campanhas neles não se alteram; o mundo de origem só é lido.
- `MundoStore.ramificar` (cópia de um snapshot e da crônica anterior a ele, numa transação) implementada em `RoomMundoStore` com `copiar` nos DAOs de snapshot e crônica. Sem mudança de esquema.
- ASSUMINDO: o modo não tem coluna própria (evita migração 12); quem chama grava `mundo.modo` nos fatos da campanha. A ramificação copia só o snapshot escolhido (mesmo número de bloco): dentro da ramificação só se volta a esse bloco ou ao molde.
- **Limites desta etapa:** a escolha na criação da campanha ("mundo anterior" em `WorldOptions`) e a gravação de `mundo.modo` ainda não usam esta API (AppModel/MI-15); sem teste (fim da Fase 22).

**MI-13 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** `TimeSimulationController` no `app` (`ui/TimeSimulationController.kt`).
- `iniciar(mundoId, dias, pararEmMarco)` retoma por `RetomadaMundo`, monta o `Simulador` pela `SimuladorFabrica` (injetada: quem monta os motores é o `app`) e roda em `Dispatchers.Default` (injetável) **um bloco por vez**: cada chamada de `avancarAte` termina na fronteira de bloco do calendário, o que não muda a crônica (I-22). A cada bloco sem falha grava snapshot e crônica filtrada (`guardarBloco`, `FiltroCronica` com o nível de `Niveis`) e publica `EstadoSimulacao` (`Parado`, `Executando`, `Pausado`, `Concluido`, `Cancelado`, `Erro`) com `ProgressoSimulacao`.
- `pausar`, `retomar`, `cancelar` e a parada em marco agem só na fronteira do bloco (latência máxima de um bloco de 10 anos); `retomar` continua o mesmo simulador sem reconstruí-lo. FALHA de invariante interrompe e não grava o bloco com falha.
- ASSUMINDO: **cancelar e `descartar()` voltam ao bloco em que a execução começou** (nada do período fica no mundo antes de "aplicar", MI-17), e **marco = bloco com delta de nível 1** (D-I8, `pararEmMarco`).
- **Limites desta etapa:** nenhuma tela usa o controlador (MI-15) e o `app` ainda não tem a `SimuladorFabrica` concreta (carregar molde, dinâmica e motores); velocidade 1x/5x/10x é o MI-14; sem teste (fim da Fase 22). Se `kotlinx-coroutines` não vier transitivamente no `app`, declarar a dependência no build.

**MI-14 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Velocidade de exibição desacoplada do cálculo (`app/ui/SimulationPlayback.kt`).
- `VelocidadeSimulacao` (1x, 5x, 10x; 1x = 1 ano de mundo por segundo, D-I7) e `ExibicaoSimulacao`: sem relógio nem thread, a tela chama `atualizar(agora, calculadoAte, concluido)` a cada quadro e recebe um `QuadroExibicao` (dia exibido, `esperandoCalculo`, `aparelhoLento`, `terminou`). O dia exibido nunca passa do dia já calculado (`ProgressoSimulacao.diaAtual`). Trocar a velocidade no meio não dá salto; pausar e retomar acompanham a pausa do controlador.
- **O `TimeSimulationController` não foi alterado e não recebe a velocidade**: por construção, mesma semente e mesmo período dão o mesmo resultado em 1x e 10x (I-02); só a exibição muda.
- ASSUMINDO: período curto = até 91 dias (uma estação): nada é animado e o resultado aparece de uma vez ao concluir; aviso de aparelho lento após 2 s esperando o cálculo.
- **Limites desta etapa:** sem tela (MI-15); a medida real de desempenho é o MI-23; sem teste (fim da Fase 22).

**MI-15 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Tela cheia "Passar o tempo" (§4).
- `ui/TimeSimulationScreen.kt`: períodos de um toque (1 dia a 1 era) e campo livre "N dias/anos" (`ui/SimulationPeriods.kt`: `PeriodoSimulacao`, `Periodos.livre` com erro de formato e teto de 120 anos), texto da data de partida e da prevista (ano base, estação e lua), velocidade 1x/5x/10x (`ExibicaoSimulacao`), "parar no próximo marco", progresso por bloco, **pausar**, **retomar**, **cancelar**, **descartar** e estados de erro (FALHA de invariante, exceção); o aviso de aparelho lento usa o `QuadroExibicao`. Acessibilidade: descrição falada do progresso em porcentagem e região viva para o estado (TalkBack); cores só do tema Material (contraste do tema).
- `ui/MythralisRuntime.kt`: a `SimuladorFabrica` concreta (molde, dinâmica e `Motores.todos` do classpath, carregados uma vez). `AppModel.activeWorld()` e `worldStoreForSimulation()`; `PlayScreen` ganha o botão "Passar o tempo" no topo da aba Jogar quando a campanha tem mundo.
- `TimeSimulationController.descartar()` também vale depois de `Erro` (volta ao bloco de partida).
- ASSUMINDO: o molde não traz o fim da era, então "1 era" = 120 anos (o teto); o painel é um botão no topo da aba Jogar que abre a tela cheia; o calendário mostra a contagem base (ano, estação, lua), sem o ano de cada reino; textos fixos em português no código (sem `strings.xml` ainda).
- **Limites desta etapa:** sem feed de eventos nem resumo (MI-16) e sem "Aplicar à campanha" (MI-17): ao concluir só há "Descartar"; o ano local de cada reino fica para o MI-16; sem teste de interface nem revisão real com TalkBack/contraste (MI-23 e fim da Fase 22); sair da tela no meio cancela e espera o fim do bloco.

**MI-16 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Feed de eventos por nível e resumo final.
- `world/ResumoNiveis.kt` (novo): porta de `gerar_niveis` e de `texto` de `relatorio.py`. `ResumoNiveis.gerar(mundo, deltas, cabecalho)` devolve o texto de `niveis.md` (nível 1 por marco, nível 2 por década e reino, nível 3 só contado, total por cidade), com `evento` (F9.1), `citados`, `contar` (mesma ordem de `Counter.most_common`) e `PyTexto` (`str`/`repr` do Python). A conferência byte a byte com `niveis.md` do Python, pedida no critério do marco, fica para a validação da fase (nenhum teste foi criado nem executado).
- `world/FeedPeriodo.kt` (novo): `AcumuladorFeed` junta, bloco a bloco, os marcos de nível 1 (importância 3+, entidade diferente de `mundo`), o resumo de nível 2 por década e reino e a contagem do nível 3; `ResumoPeriodo` traz marcos, reinos caídos, pactos novos e desfeitos, NPCs mortos e empossados. `Niveis.aprender` (novo) ensina reino e papel das entidades criadas durante a execução.
- `TimeSimulationController.feed` (StateFlow) é atualizado a cada bloco gravado e zerado ao cancelar ou descartar; `TimeSimulationScreen` mostra os marcos até o dia exibido (nunca à frente da exibição), o resumo por década e, ao concluir, o resumo do período.
- ASSUMINDO: "pacto novo" = `alianca_de_povos` de entidade `pacto.*`; "reino caído" = `queda_de_coroa`; "NPC principal" = NPC de nível 1 ou 2; "empossado" = agente de mudança de `titular`, `governante` ou `lideranca` de nível 1 ou 2, sem repetir o NPC; o ano mostrado é o ano base (sem o ano local de cada reino).
- **Limites desta etapa:** o nível de cada delta usa o estado ao fim do bloco (como no MI-13); sem "Aplicar à campanha" (MI-17); sem teste de conformidade com o Python (fim da Fase 22).

**MI-17 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Aplicar à campanha (`app/ui/AplicacaoSimulacao.kt`).
- `AplicacaoSimulacao.aplicar(session, diaInicio, diaFim, feed)` monta **um lote só** e o executa por `session.executeAll` (recusa tudo se um comando falhar): `AdvanceDays` em passos de até 365 dias (limite do comando), `RecordFact` de `mundo.dia`, `mundo.ultima_simulacao` (período, marcos, reinos caídos, pactos novos, NPCs principais mortos e empossados) e `mundo.marcos` (os 10 marcos mais recentes). Devolve `LoteAplicado` (marca do log antes do lote) ou `Recusado(motivo)`; `desfazer` usa `session.rollbackTo(marca)`.
- Tela: ao concluir, **Aplicar à campanha** e **Descartar**; depois de aplicar, o estado `Aplicado` mostra **Desfazer**, que reverte o lote da campanha e devolve o mundo ao bloco de partida (`controller.descartar()`), então campanha e mundo voltam juntos. `AppModel.applySimulation`/`undoSimulation` fazem a ponte; a gravação roda fora da thread principal.
- ASSUMINDO: nesta etapa os efeitos entram como fatos; cidades, NPCs e facções só entram na campanha sob demanda (MI-18) e o envelhecimento e a morte do herói ficam no MI-26 e MI-27.
- **Limites desta etapa:** o "desfazer jogada" do jogo (`GameLoop.undo`) volta o log à marca de uma jogada anterior e pode levar junto o lote aplicado sem devolver o mundo (divergência a tratar com o MI-18); o dia do mundo só vira dia da campanha pelo lote (sem conversão de calendário além dos dias); sem teste (fim da Fase 22).

**MI-18 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Instanciação sob demanda.
- `world/InstanciacaoCena.kt` (novo): `InstanciacaoCena.cena(molde, estado, cidadeId)` lê o molde e o estado do último snapshot do mundo (quem está vivo, onde mora, quem governa hoje) e devolve só o necessário: o **local**, até 5 **NPCs** vivos (o governante atual, os principais e os de papel de nível 2) e até 3 **facções** com presença na cidade (as lideradas pelos NPCs escolhidos primeiro).
- `app/ui/InstanciacaoCampanha.kt` (novo): transforma a cena num lote (`AddPlace`, `ConnectPlaces` a partir do local atual, `DiscoverPlace`, `CreateEntity` dos NPCs e `RecordFact` `mundo.faccao.<id>`) e o executa por `executeAll`. **Idempotente**: o que já existe na campanha é pulado, então tocar a mesma cidade de novo não cria nada, e um lote recusado não muda nada. `AppModel.instantiateCity(cityId)` é a entrada.
- ASSUMINDO: "o necessário" como acima; facções não são entidades do motor (entram como fatos); NPCs entram sem ficha mecânica (`rulesValidated = false`), com o conceito montado de cargo, personalidade e objetivo do molde.
- **Limites desta etapa:** nada chama `instantiateCity` ainda: o gatilho "a cena toca a cidade" (a IA propor o local, o jogador viajar) e o contexto do mestre ficam para o MI-19 e o MI-20; a ficha mecânica dos NPCs e a memória entre campanhas ficam para o MI-22; sem teste (fim da Fase 22).

**MI-19 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Ecos viram rumores.
- `world/RumoresEcos.kt` (novo): `RumoresEcos.doLugar(molde, estado, lugarId)` lê o estado `eco.<delta>` que o motor de Ecos já mantém (`difusao.lugares`, `passo`, `certeza`) e devolve os `Rumor` que já chegaram ao lugar: **chega se e só se o lugar está na lista de quem sabe**, então um rumor que parou em 1 passo do grafo de rotas abertas chega ao vizinho e não a 4 passos. Nada é gravado no mundo.
- `VersaoRumor`: `confirmado` = **exata**, `alegado` = **exagerada**, `rumor` = **falsa** (a certeza do Eco desce um degrau a cada 2 passos, `eco_passos_por_degrau`).
- `InstanciacaoCena` passa a incluir os rumores da cidade (`CenaInstanciavel.rumores`) e `InstanciacaoCampanha` os grava como fatos `mundo.rumor.<eco>@<cidade>` (`[versão exata] tipo: nome`), idempotentes como o resto do lote.
- ASSUMINDO: o mapa de certeza para versão acima (a falsa é para o mestre tratar como não confiável); pistas de mistério (`restrito`) e fatos `secreto` não viram boato; janela de 1 ano e até 5 rumores por cidade, os mais recentes primeiro; a versão é a do Eco inteiro (o estado não guarda o passo em que cada lugar soube).
- **Limites desta etapa:** o **reconhecimento** (NPC reconhecer o herói por feitos) depende de o herói existir no mundo (MI-26) e fica de fora; a distorção do texto fica a cargo do mestre (MI-20 passa a versão à IA); o texto do rumor é `tipo: nome da entidade` (sem a frase do delta); sem teste do exemplo do MP-07 (fim da Fase 22).

**MI-20 🟡 (08/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Contexto do mestre.
- `world/ContextoMestre.kt` (novo): `ContextoMestre.cena(molde, cena, orcamentoTokens = 600)` monta, das entidades que `InstanciacaoCena` já escolheu, o texto de referência da cena: resumo e vizinhança da cidade (tabela `resumo` do molde, `camada_max` = `local`), lista de presentes vivos, rumores com a explicação de cada versão, resumo de cada NPC e de cada facção. Blocos em ordem de prioridade; o que não couber no orçamento é pulado, e o texto final nunca passa dele (estimativa do `TokenEstimator`, ~4 caracteres por token). Cabeçalho: referência, não instrução; o estado oficial e o motor prevalecem; a lista de vivos prevalece sobre o resumo.
- `ai`: `ContextInput.mundoContext` (seção **cortável** pelo orçamento do mestre, ao contrário de `worldContext`), `PlayInput.mundoContext` e o parâmetro `mundoContext` do `PlayController`. `app`: `AppModel.mundoContextForScene` usa o lugar atual da campanha, o último snapshot do mundo e guarda o texto até mudar mundo, bloco ou lugar.
- ASSUMINDO: "pipeline DC" = seleção por trechos pequenos com orçamento, mas escolhida pelas entidades da cena (o molde já tem um resumo por entidade; sem busca por texto); resumos `secreto`/`restrito` nunca entram; NPC sem resumo entra pelo conceito; só há contexto quando o lugar atual é uma cidade do molde.
- **Limites desta etapa:** a IA ainda não é impedida de contradizer o mundo (MI-21); sem consentimento próprio (o texto é do molde, não de documento do usuário); sem teste do orçamento (fim da Fase 22).

**MI-21 🟡 (09/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Cânone do mundo.
- `world/CanonMundo.kt` (novo): `CanonMundo.fatos(molde, estado, cena, cronica)` lê o último snapshot e a crônica e devolve `FatoCanonMundo` (chave, valor, texto, `deltaId`): `canon.<npc>.vivo` (`sim`/`nao`) para os NPCs da cidade e `canon.<cidade>.governante`, cada um apontando para o delta que o estabeleceu (ou `molde`). `recusa` (fato da IA) e `recusaNpc` (criar NPC morto no mundo) devolvem o motivo da recusa. O cânone entra no contexto do mestre (`ContextoMestre`, bloco logo após os presentes).
- `ai`: `CanonGuard` (`recusaFato`, `recusaNpc`) e parâmetro `canonGuard` do `TurnRunner`: `record_fact` que contradiz o cânone, ou grava chave `canon.*`/`mundo.*`, e `create_npc` com o id de um NPC morto no mundo viram correção pedida à IA (a proposta inteira é recusada).
- `app`: `CanonCampanha.sincronizar` grava cada fato como `CanonFact` ORIGINAL (`sourceId` = `mundo:<mundo>`, `chunkId` = delta) a cada atualização do contexto; `CanonCampanha.divergir` marca DIVERGENCE. `AppModel` guarda o cânone da cena atual e o entrega ao guarda.
- ASSUMINDO: só vida/morte de NPCs da cidade e governante; contradição por chave com valor diferente (sem análise de texto livre); a crônica lida é a de até 500 linhas (o vínculo ao delta pode faltar em mundos longos e cai em `molde`); DIVERGENCE e AI_CREATED não são gravados sozinhos nesta etapa.
- **Limites desta etapa:** contradição em texto livre da narração não é detectada; sem teste (fim da Fase 22).

**MI-22 🟡 (09/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Vida fora de cena.
- `engine`: comando `MarkNpcDead(entityId, cause)` (só NPC vivo; gera `EntityDied`, sem passar por dano, pois os NPCs do MI-18 não têm recursos).
- `app/VidaForaDeCena.kt` (novo): compara os NPCs `npc.*` vivos da campanha com o último snapshot do mundo e gera `MarkNpcDead` e o fato `mundo.npc.<id>.morte`. Entra no mesmo lote de **Aplicar à campanha** (`AplicacaoSimulacao.aplicar(…, estadoMundo)`, desfazer reverte junto) e em `AppModel.instantiateCity`; idempotente.
- ASSUMINDO: só a morte de NPCs já instanciados; a memória entre campanhas (MP-03) e outras mudanças fora de cena (cidade, cargo) ficam fora desta etapa.
- **Limites desta etapa:** o dossiê e as relações do NPC morto não são atualizados; sem teste (fim da Fase 22).

**MI-26 🟡 (09/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Idade do herói.
- `world/MotorHeroi.kt` (novo): `MotorHeroi.simular(din, EstadoHeroi, semente, diaInicio, diaFim)` envelhece o herói, o adoece pela idade e o mata de velhice com as tabelas e fórmulas de longevidade do motor de demografia (`mortePm`, `eIdoso`, `doenca_idosa_pm`, `recuperacao_pm`, `fator_mortalidade_doente`). Gerador próprio (`PythonRandom`), uma avaliação por marco de ano do mundo; o sorteio de cada ano depende só da semente e do ano, então aplicar o período em partes dá o mesmo resultado. **Não entra no estado nem nos hashes do mundo** (nenhum arquivo do motor do mundo mudou).
- `app/IdadeHeroi.kt` (novo): nascimento no dia do mundo, classe e saúde como fatos `heroi.nasc_dia`, `heroi.longevidade`, `heroi.saude` e a morte como `heroi.morte` (`<dia>:<causa>`). `AppModel.setHeroAge(anos, classe)` fixa a idade; `AppModel.applySimulation` roda o motor no mesmo lote de Aplicar (desfazer reverte). `MythralisRuntime.dinamica()` fornece as tabelas.
- ASSUMINDO: fatos em vez de campo novo na entidade (o motor do jogo não guarda idade); a classe é passada por quem chama; o herói só é simulado se tem `heroi.nasc_dia`; a morte só é registrada, sem matar a entidade (parada da tela e legado são o MI-27).
- **Idade na criação (09/11/2026, decisão do autor; escrito, não compilado):** a faixa (jovem 20%, adulto 40%, veterano 70% da `vida_max` da classe de longevidade do povo) é escolhida na criação da ficha (`CharacterChoices.ageBand`, `FaixaIdade`, seletor em `CharactersScreen`, 5 strings `char_age_*`) e acompanha o herói. `AppModel.activate` chama `applyHeroAgeFromFicha` depois de `linkWorld`: classe = `Dinamica.classeDe(raça, sub-raça)`, anos = percentual da `vida_max`, e `setHeroAge` fixa a idade no dia do mundo. 🔶 ASSUMINDO: faixa vazia = adulto; sem mundo, sem raça ou povo sem idade (`sem_idade`) não define idade; a edição das escolhas de ficha existente carrega a faixa.
- **Limites desta etapa:** a faixa não aparece na ficha da biblioteca nem no resumo da criação; sem teste de "herói de 30 anos em 1 século" (fim da Fase 22).


**MI-27 🟡 (09/11/2026; escrito, não compilado nem testado; testes só no fim da Fase 22).** Morte do herói na simulação.
- `engine`: comando `MarkHeroDead(entityId, cause)` (só PLAYER vivo; emite `EntityDied` sem autor, então o legado do LG-01 nasce como `UNKNOWN`, sem vingança). `app/IdadeHeroi.kt`: `previsaoDeMorte` (mesma conta do Aplicar: `MotorHeroi.simular` com a semente do mundo), `resumoDaMorte`, e `comandos` passa a matar o herói no lote de Aplicar além de gravar `heroi.morte`. `TimeSimulationController` ganhou o parâmetro `morteDoHeroi`: se o herói morre dentro do período, o período termina no dia da morte (`Concluido`/`Aplicado` carregam `MorteHeroi`). `AppModel.heroDeathIn/heroDeathSummary`. `TimeSimulationScreen`: ao parar, o resumo do período avisa e, depois de aplicar, o cartão "O herói morreu" mostra idade e causa e oferece as opções do M-04 já existentes (renascer com item, assumir NPC, encerrar a campanha; o motor valida).
- ASSUMINDO: a morte do herói é decidida antes da execução (o motor `heroi` é independente do mundo e determinístico por semente e ano), então o mundo é simulado só até o dia da morte; o dia da morte é sempre um marco de ano (múltiplo de 365); desfazer volta ao snapshot enquanto nenhuma opção do legado foi escolhida (depois disso o botão Desfazer some, porque assumir NPC troca o `playerId` do app); a simulação seguinte roda sem o herói (`heroi.morte` presente).
- **Limites desta etapa:** recuperar itens do corpo (LG-04) não existe, então não há botão para isso; renascer ou assumir NPC não reinicia a idade (o herói revivido ou o NPC assumido não é simulado); sem teste do exemplo "morte no meio de 1 era" (fim da Fase 22).

**Pendências da Fase 25 (09/11/2026, decididas pelo autor: documentar e seguir).**
- ⏸ **MI-23 (desempenho em aparelho):** não medido. Exige aparelho Android de entrada (tempo e pico de memória de 1 era, 1 século e 500 anos); o ambiente de desenvolvimento não tem Android nem aparelho. Nenhum número foi registrado. O teto de 120 anos da D-I6 segue como decidido, sem ajuste. Para fechar: medir no aparelho e registrar aqui.
- ⏸ **MI-24 (suíte completa, E2E e CI):** não executado. Depende de Gradle com rede e Android SDK, ausentes no ambiente. Continuam sem registro verde `:world:test`, `ktlintCheck` (com a indentação apontada em `engine/AeltharynWorld.kt`), a suíte do `app` e o E2E da tela. Para fechar: rodar `./gradlew ktlintCheck test :app:assembleDebug` e o CI, corrigir o que falhar e registrar o resultado.
- 🟡 **MI-25 (documentação):** README, CHANGELOG, `PLANO_RPG_SOLO.md` e `INTEGRACAO_FUTURA_RPG_SOLO.md` atualizados com o estado real (código escrito, nada validado). Passa a ✅ quando MI-23 e MI-24 fecharem. As Fases 21 a 25 seguem sem aceite.

## 6. Riscos

| Risco | Tratamento |
|---|---|
| Hash diferente por detalhe de número ou texto no JSON | MI-03 e MI-04 com vetores do Python antes de portar os motores |
| Aparelho lento para 500 anos | blocos com retomada (MI-11), exibição desacoplada (D-I7), medida em MI-23 |
| Crônica grande demais | D-I3: só guardar o filtrado e o hash |
| Migração do Room | teste instrumentado de migração em MI-10 |
| Herói morrer no meio de uma era | MI-27: a tela para no dia da morte, o legado do M-04 decide o que segue; sem avançar em silêncio |
| A IA contradizer o mundo | MI-20 e MI-21: só contexto filtrado e `CanonFact` |
| Mundo e campanha divergirem | MI-12 e MI-17: ramificação explícita e aplicação desfazível |

## 7. Decisões em aberto para o autor

1. ~~D-I2~~ ✅ decidido: resultado idêntico ao Python.
2. ~~D-I5~~ ✅ decidido: o herói envelhece e pode morrer.
3. ~~D-I6~~ ✅ decidido: teto de 120 anos.
4. ~~D-I7~~ ✅ decidido: 1x = 1 ano de mundo por segundo.
5. ~~Onde fica o botão~~ ✅ decidido: painel "Passar o tempo" na aba Jogar.

**Sem decisão em aberto para o autor.** Próximo passo: rodar `./gradlew ktlintCheck :world:test` (prova da Fase 21); só então MI-09.
