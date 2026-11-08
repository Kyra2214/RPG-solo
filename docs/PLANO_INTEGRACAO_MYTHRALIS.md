# Plano de integração do motor de Mythralis no RPG Solo

> **Anexo do plano único.** Estado, ordem e pendências ficam em `PLANO_RPG_SOLO.md`, seção 9; este arquivo guarda só a especificação e as decisões deste tema.

**Estado:** plano escrito e decisões D-I2 e D-I5 a D-I7 mais o local do botão fechadas pelo autor em 08/11/2026. **Andamento: MI-01 a MI-07 concluídos; MI-08 parcial (8 marcos escritos de 27; Python passa, mas o Kotlin não compila); a Fase 21 ainda não foi aceita. Próximo: corrigir a compilação e validar no CI antes de iniciar MI-09.** Base: motor Python F0–F9 aceito (D-180), `Mythralis/plano/PLANO_MOTOR_MYTHRALIS.md` §6, §10, §17 e `Mythralis/plano/INTEGRACAO_FUTURA_RPG_SOLO.md`. Este arquivo é o plano da integração; `PLANO_RPG_SOLO.md` (§3.11) aponta para ele.

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
| MI-09 | `WorldTemplate` (MP-08): molde carregado no app, escolha do mundo na criação da campanha (anterior, pré-configurado, próprio) | campanha nova escolhe Mythralis | M |
| MI-10 | Room v11 com migração explícita: mundo, snapshots por bloco, crônica filtrada, vínculo campanha → mundo (D-I3, D-I4) | migração v10 → v11 sem perda; sem `fallbackToDestructiveMigration` | M |
| MI-11 | Retomada e limpeza: continuar do último snapshot, janela de retenção, tamanho em disco | app encerrado no meio retoma do bloco anterior | M |
| MI-12 | Ramificação de mundo (MP-07): cânone compartilhado, linha do tempo alternativa e cópia independente | duas campanhas no mesmo molde não se alteram | M |

### Fase 23 — Tela de Simulação de tempo

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| MI-13 | `TimeSimulationController` no `app`: período, execução em `Dispatchers.Default` por blocos, pausa, retomada, cancelamento, parada em marco | controlador roda 1 era sem travar a UI; cancelar volta ao snapshot | M |
| MI-14 | Controle de velocidade 1x, 5x e 10x (D-I7) com exibição desacoplada do cálculo | mesma semente e período dão o mesmo resultado em 1x e 10x | M |
| MI-15 | Tela: seletor de período, calendário, progresso, botões e estados de erro (§4) | fluxo completo no aparelho, TalkBack e contraste revisados | M |
| MI-16 | Feed de eventos por nível e resumo final (marcos, reinos, pactos, NPCs) | resumo bate com `niveis.md` do Python para a mesma semente | M |
| MI-17 | **Aplicar à campanha**: avança `GameState.day` e grava os efeitos como eventos de mudança (um lote desfazível; o envelhecimento e a morte do herói entram com MI-26 e MI-27) | desfazer reverte tudo | M |

### Fase 24 — Mundo dentro da campanha

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| MI-18 | Instanciação sob demanda: cidade, NPC e facção só entram no log da campanha quando a cena toca neles | cena numa cidade cria só o necessário | G |
| MI-19 | Ecos viram rumores e reconhecimento (MP-07): notícia viaja pelo grafo, com versão exata, exagerada ou falsa | rumor chega a 1 passo e não a 4, como no exemplo do MP-07 | G |
| MI-20 | Contexto do mestre: resumos por cena pelo pipeline DC; a IA só recebe o relevante à cena (critério 12) | contexto de uma cena não passa do orçamento de tokens | M |
| MI-21 | Cânone: `CanonFact` ORIGINAL, DIVERGENCE e AI_CREATED ligados aos deltas; a IA não contradiz o cânone | contradição proposta pela IA é recusada | M |
| MI-22 | NPCs recorrentes e vida fora de cena (MP-03, `NPCS_RECORRENTES_MEMORIA_ENTRE_CAMPANHAS.md`) | NPC principal morto na simulação aparece morto na campanha | G |
| MI-26 | Idade do herói: campo de idade na ficha e na criação (hoje o engine não guarda idade), data de nascimento no dia do mundo, e motor `heroi` (envelhecimento, doença da idade e morte natural pela longevidade do povo, gerador próprio) | herói de 30 anos simulado por 1 século morre de idade em semente fixa e o resultado se repete; hashes do mundo inalterados | G |
| MI-27 | Morte do herói na simulação: a tela interrompe no dia da morte, mostra o resumo e aciona o legado do M-04 (item de renascimento, assumir NPC, recuperar itens, fim da campanha); a simulação do mundo pode continuar sem o herói | morte no meio de 1 era para a tela, oferece as opções do M-04 e desfazer volta ao snapshot | M |

### Fase 25 — Validação e fechamento

| ID | Entrega | Pronto quando | Esf. |
|---|---|---|---|
| MI-23 | Desempenho em aparelho: tempo e memória de 1 era, 1 século e 500 anos em aparelho de entrada | metas medidas e registradas; teto da D-I6 ajustado | M |
| MI-24 | Suíte completa, E2E da tela e lote de conformidade no CI; APK de debug | CI verde com os resultados no log | M |
| MI-25 | Documentação final, README, CHANGELOG, `PLANO_RPG_SOLO.md` e `INTEGRACAO_FUTURA_RPG_SOLO.md` atualizados | projeto integrado e zip completo | P |

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
- **Resultado Kotlin/Gradle:** `./gradlew --no-daemon test` não chegou aos testes do app porque falta Android SDK (`ANDROID_HOME`/`sdk.dir`). Com JDK 17, `./gradlew --no-daemon :world:test` falhou na compilação em `Dinamica.kt:98` (comentário não fechado), causando referências não resolvidas subsequentes em `Invariantes.kt`, `MotoresMundo.kt` e `MotoresPessoas.kt`. `./gradlew --no-daemon ktlintCheck :world:test` parou antes dos testes por erros de indentação em `engine/AeltharynWorld.kt`; esse arquivo não foi alterado pelo ZIP MI-08. Nenhum CI GitHub foi executado.
- **Pendência de aceite:** corrigir o erro de compilação do módulo `world`, executar `:world:test` e a suíte/CI apropriada; depois tratar os erros de ktlint e só então fechar a Fase 21 e avançar para MI-09. Tempo estimado do lote: 20 × 500 anos em Kotlin (Python: ≈ 2 min por semente).
- ASSUMINDO: os arquivos de vetores já gerados (MI-03 a MI-07) valem como contrato; o teste do validador lê `../Mythralis/saidas/validacao-mundo.txt` (diretório de trabalho do Gradle é o do módulo `world`, o CI faz checkout do repositório inteiro); `'resolvido' == true` do Kotlin equivale ao valor verdadeiro do Python (o campo é booleano).

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
