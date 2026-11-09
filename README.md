# RPG Solo

Aplicativo Android nativo em Kotlin e Jetpack Compose para jogar RPG solo com um mestre de IA configurável. As regras e o estado são validados pelo motor local; a IA narra e propõe mudanças, mas não decide os resultados dos dados.

- Plano único (estado, lacunas e roteiro): [`PLANO_RPG_SOLO.md`](PLANO_RPG_SOLO.md)
- Histórico das entregas recentes: [`CHANGELOG.md`](CHANGELOG.md)
- Formato dos pacotes de regras: [`docs/pacotes/FORMATO_PACOTE.md`](docs/pacotes/FORMATO_PACOTE.md)
- Mecânica e equilíbrio dos dados: [`docs/MECANICA_DADOS.md`](docs/MECANICA_DADOS.md)
- Plano de integração do mundo Mythralis (Fases 21 a 25, MI-01 a MI-27): [`docs/PLANO_INTEGRACAO_MYTHRALIS.md`](docs/PLANO_INTEGRACAO_MYTHRALIS.md)
- Mundo e motor de simulação (projeto de escrita, independente do código do app): [`Mythralis/README.md`](Mythralis/README.md)

## Estado atual

O último CI totalmente verde é o [Android CI #40163269121](https://github.com/Kyra2214/RPG-solo/actions/runs/40163269121), em 25/10/2026, no commit `036d445` (`main`): ktlint, 894 testes JVM (`ai` 212, `engine` 390, `rules` 292), 9 testes E2E no emulador, 6 testes instrumentados de `data` (migrações Room) e build/upload do APK de debug (`app-debug.apk`, 11.656.051 bytes) passaram. Relatório em [`docs/relatorios/ci-android-2026-10-25-n54.md`](docs/relatorios/ci-android-2026-10-25-n54.md); histórico e pendências em [`docs/VALIDACAO_CI.md`](docs/VALIDACAO_CI.md). O APK é de **debug**, não uma versão release. A validação anterior (#40097446606, `40c38b1`, 24/10/2026, 650 testes JVM) está em [`docs/relatorios/ci-android-2026-10-24.md`](docs/relatorios/ci-android-2026-10-24.md). Em 08/11/2026, a suíte Python do motor passou (62 testes, reexecutada em 136,9 s). A falha de compilação de `:world:test` em `Dinamica.kt:98` (comentário não fechado), registrada antes, está corrigida no código atual (verificação estática: nenhum comentário de bloco desbalanceado em `world/`). A execução Gradle de `:world:test` e do `ktlintCheck` depois da correção ainda não tem registro neste repositório, e `./gradlew test` completo depende de Android SDK. Não há CI verde após `036d445`; detalhes em [`docs/PLANO_INTEGRACAO_MYTHRALIS.md`](docs/PLANO_INTEGRACAO_MYTHRALIS.md) e [`CHANGELOG.md`](CHANGELOG.md).

## Funcionalidades implementadas no código

- Seis áreas Compose: Jogar, Heróis, Regras, Campanhas, Sistemas e Configuração, com orientação de primeiro uso em três etapas.
- Motor determinístico em Kotlin puro, com comandos/eventos, rolagens reproduzíveis, rollback, estado de conhecimento, mapa, relações, relógios com pista pública opcional ao completar e pista intermediária ao passar da metade e validação de backups.
- Pacote de regras com criação de fichas, perícias de 0–5 pontos, perfis de dano e de perigo, itens/equipamento, bônus mecânicos e condições com duração/efeitos por rodada.
- Combate integrado à interface: iniciativa configurada pelo pacote, ataque, dano, defesa, fuga, turnos de NPC e XP; a IA não escolhe os números mecânicos.
- Progressão básica por pacote: pontos por nível, alocação validada de atributos/perícias (incluindo pré-requisitos) e histórico de XP/nível; o sistema próprio concede 1 ponto de evolução, 1 de status (sem teto) e 1 de habilidade por nível.
- Primeiro uso transacional: a tela Jogar acompanha provedor, ficha disponível e campanha ativa e não abre o chat antes das três etapas estarem prontas.
- Camada de IA com provedores configuráveis, teste de conexão, classificação de intenção, contexto, propostas validadas, sugestões e ações locais sem chamada de IA.
- Cache `ephemeral` do prefixo system implementado para a API oficial Anthropic; Config permite ativar/desativar o cache, mostra contagens de tokens lidos/criados e aceita preços por milhão com tarifas separadas para estimar custo na moeda informada. O uso real ainda não foi validado em rede. A linha do tempo automática derivada do log também foi adicionada à aba Campanhas, incluindo acontecimentos de combate com participação do jogador. O sistema próprio declara sorte/destino global: 1 PA permite repetir a rolagem ou somar uma face após o resultado, quando o teste não define uma política específica.
- Persistência via Room v10 para campanhas, eventos, fichas, metadados, biblioteca de fontes, progresso de pipeline e fatos canônicos; documentos originais ficam em armazenamento privado do app, com verificação de hash.
- Chave do provedor protegida pelo Android Keystore e excluída do backup.
- Diagnóstico local: a Config exporta um relatório de texto (versão, Android, aparelho e última falha não tratada, sem mensagem da exceção, prompts, dados de campanha ou chave) pelo compartilhamento do sistema.
- Backup JSON local com eventos/configuração do motor e metadados de capítulos, missões e linha do tempo, por seletor de arquivos Android (SAF) ou texto. Fatos canônicos e bytes originais de fontes não entram no backup atual.
- Importação ZIP de pacotes de regras pela interface, com validação, limite de tamanho e restauração de pacotes importados ao reabrir o app.
- Biblioteca de fichas com consulta, duplicação, arquivamento/restauração, exclusão de arquivadas e cópia do JSON.
- Acessibilidade inicial: escala global de fonte ajustável (85%–140%) e descrições TalkBack para os dados, mantendo os resultados ocultos até serem revelados.

## Módulos

- `app` — telas Compose, navegação, configuração e integração do aplicativo.
- `engine` — estado canônico, comandos/eventos, dados, capítulos, diário e backup.
- `rules` — catálogo, pacotes de regras, validação de fichas e criação de campanha.
- `world` — porte Kotlin do motor de simulação do Mythralis, com testes de conformidade contra o Python; ligado ao `app` só pela escolha do mundo na nova campanha (MI-09; ver a seção Mythralis).
- `ai` — provedores, loop de jogo, contexto e parsing das propostas.
- `data` — banco Room, adaptador de eventos e armazenamento privado dos documentos-fonte.

## Como compilar e testar

Requisitos: JDK 17, Android SDK com API 35 e licenças do SDK aceitas.

```bash
./gradlew test :app:assembleDebug
```

Os testes dos módulos Kotlin puros também podem ser executados separadamente:

```bash
./gradlew :engine:test :rules:test :ai:test
```

A simulação das chances dos testes por pontos pode ser reproduzida sem Gradle:

```bash
python3 tools/simulate_checks.py --runs 100000
```

Abra a pasta do repositório no Android Studio para executar o app em emulador ou dispositivo.

## Mythralis (mundo, motor e integração em andamento)

A pasta [`Mythralis/`](Mythralis/README.md) guarda o mundo vivo do jogo (5 reinos, 46 cidades, povos, NPCs, facções) e o motor de simulação de referência em Python (`Mythralis/motor/`, fases F0 a F9 concluídas, 62 testes, lote de 500 anos × 20 sementes sem falha). A pasta é independente do código do app; a integração com o app **está em andamento, mas incompleta**.

**O que já existe no repositório:**

- Módulo Gradle `:world` (Kotlin puro, JVM 17, pacote `dev.rpgsolo.world`, incluído em `settings.gradle.kts` e usado por `rules` e `app`): carregador e validador do molde, RNG idêntico ao do Python, JSON canônico e sha256, calendário, estado, crônica, dinâmica, motores de pessoas e de mundo, simulador em blocos, invariantes, níveis e retomada por snapshot (MI-01 a MI-07).
- Molde, dinâmica e `subracas-em-prosa.txt` exportados para `rules/src/main/resources/mundos/mythralis/`.
- Testes de conformidade Kotlin × Python (MI-08), alimentados por vetores gerados pelo Python em `world/src/test/resources/conformidade/`, incluindo o lote de 20 sementes × 500 anos.

**Ligação com o app (MI-09 a MI-22, MI-26 e MI-27, escritas e ainda não compiladas):** o Room v11 (migração 10 → 11) tem as tabelas de mundo, snapshots, crônica filtrada e vínculo campanha → mundo, e a campanha criada com Mythralis ganha seu mundo no banco. Antes, no MI-09: `app` e `rules` declaram `project(":world")`; a nova campanha oferece a lista "Mundo da campanha" (mundo próprio, Aeltharyn, **Mythralis** e "mundo anterior", este indisponível por enquanto) e a campanha criada com Mythralis grava o vínculo com o molde (`mundo.molde_hash`).

**Retomada e limpeza (MI-11, escrita):** `RetomadaMundo` continua do último snapshot e `RetencaoMundo` aplica a janela de retenção (último século de crônica e 5 snapshots) e mede o espaço em disco; ainda não chamadas pela interface (MI-13 e MI-15).

**Ramificação (MI-12, escrita):** `RamificacaoMundo` oferece cânone compartilhado, linha do tempo alternativa e cópia independente; ainda não ligada à tela de nova campanha.

**Controlador (MI-13, escrito):** `TimeSimulationController` roda a simulação por blocos fora da thread principal, com pausa, retomada, cancelamento e parada em marco; ainda sem tela.

**Velocidade (MI-14, escrita):** `ExibicaoSimulacao` mostra o período em 1x, 5x ou 10x sem tocar no cálculo; ainda sem tela.

**Tela (MI-15, escrita):** botão "Passar o tempo" na aba Jogar abre a tela cheia com período, velocidade, progresso, pausa, cancelamento e erros; sem feed de eventos (MI-16) nem "Aplicar à campanha" (MI-17).

**Feed e resumo (MI-16, escrito):** a tela mostra marcos de nível 1 ao vivo, resumo por década e, ao concluir, o resumo do período; `ResumoNiveis.gerar` porta o `niveis.md` do Python.

**Aplicar (MI-17, escrito):** o período simulado só entra na campanha ao tocar em "Aplicar à campanha" (avança o dia e grava fatos num lote), e "Desfazer" reverte campanha e mundo juntos.

**Instanciação (MI-18, escrita):** `AppModel.instantiateCity` leva à campanha só o necessário de uma cidade do mundo (local, até 5 NPCs vivos e até 3 facções), sem repetir o que já existe; ainda sem gatilho na cena.

**Rumores (MI-19, escrito):** os Ecos do mundo viram fatos `mundo.rumor.*` na cidade que a cena toca, com versão exata, exagerada ou falsa; o reconhecimento do herói fica para o MI-26.

**Contexto do mestre (MI-20, escrito):** a IA recebe, só da cidade em que a cena está, o resumo do molde, os presentes vivos, os rumores e as facções, dentro de 600 tokens e cortável pelo orçamento da jogada; resumos secretos não entram.

**Cânone (MI-21, escrito):** vida/morte de NPCs e governante da cidade viram fatos de cânone ligados aos deltas e gravados como `CanonFact` ORIGINAL; a IA não pode contradizê-los nem gravar chaves `canon.*`/`mundo.*` (a proposta é recusada e ela corrige).

**Vida fora de cena (MI-22, escrito):** o NPC que já está na campanha e morreu na simulação aparece morto ao aplicar o período ou ao tocar a cidade (comando `MarkNpcDead`).

**Idade do herói (MI-26, escrito):** o herói tem nascimento no dia do mundo e envelhece, adoece e morre de velhice ao aplicar um período, por motor próprio que não altera os hashes do mundo. A faixa de idade (jovem, adulto, veterano) é escolhida na criação da ficha e vale uma porcentagem da longevidade do povo.

**Pendências da integração (Fase 25):** MI-23 (medir desempenho em aparelho) e MI-24 (suíte completa, E2E e CI) estão pendentes e não foram executados; nada da integração Mythralis foi compilado ou testado.

**Morte do herói na simulação (MI-27, escrito):** se o herói morre de velhice dentro do período, a simulação para no dia da morte, o resumo mostra idade e causa e a tela oferece as opções do M-04 (renascer com item, assumir NPC, encerrar a campanha); desfazer volta ao ponto anterior enquanto nenhuma opção foi escolhida. Recuperar itens do corpo (LG-04) ainda não existe.

**O que ainda não existe:** estado do mundo na campanha (cidades, NPCs e facções só entram na MI-18), tela **Simulação de tempo** (1 dia a 1 era, teto de 120 anos, velocidades 1x/5x/10x), ligação com a campanha e envelhecimento/morte do herói (D-I5) são as Fases 22 a 25 (MI-09 a MI-27).

**Validação:** a suíte Python passa (62 testes). A Fase 21 só será aceita quando `:world:test` passar no CI do GitHub; o `ktlintCheck` também precisa passar (houve erros de indentação em `engine/AeltharynWorld.kt`). O MI-09 foi escrito antes dessa validação, por decisão do autor (testes só no fim da fase); compilação e testes pendentes. Plano: [`docs/PLANO_INTEGRACAO_MYTHRALIS.md`](docs/PLANO_INTEGRACAO_MYTHRALIS.md) (resultado Kotlin igual ao Python, D-I2).

## Lacunas conhecidas

- Sistema de atributos/combate/ofícios (Fases 15–20, N-01 a N-28) e extensões N-32 a N-36 (companheiro e criadouro, gemas e Joias, Construção, utilidades fora de combate, Tatuador): em código, conforme o `CHANGELOG.md` de 24 e 25/10. O CI #40163269121 validou o commit `036d445`; não confirmei quais extensões ele inclui. Mecânica de dados: a regra de pontos dos testes (cada ponto soma uma face, contando o zero) foi alinhada no código e nos testes em 08/11/2026 e **não foi compilada nem executada**. Validação em aparelho físico ainda falta.

- Fase 14 parcialmente implementada e não validada: DC-01 a DC-05 têm lógica no `engine`/`data`/`app`, incluindo Room v10 e bytes de fonte no armazenamento privado; DC-06/DC-07 têm helpers de resumo e propostas de regras. A interface de importação, biblioteca, progresso e fatos está na aba Campanhas, ainda sem validação em aparelho. A pipeline de indexação é retomada automaticamente ao abrir o app. O consentimento Q-09 por campanha (envio de trechos de fontes à IA) já existe na aba Campanhas. PDF não tem OCR e há limites de extração; DOCX não processa tabelas estruturadas.
- Fase 12 (motor unificado) concluída em código, com testes escritos mas nunca executados (`EffectiveValueTest`, `UnifiedCombatTest`, `AiParityTest`); lacunas listadas no plano (3.7, MU-08.4).
- Compilação, testes JVM, E2E no emulador e testes de migração Room passaram no CI #40163269121 (`036d445`). Ainda faltam validação visual em aparelho físico e commitar o schema Room v10 gerado pelo build (`data/schemas` só tem `2.json` e `3.json`; o artefato `rpgsolo-room-schemas` traz os gerados).
- O streaming progressivo agora está ligado do transporte SSE até a interface; permanece sem validação de execução nesta etapa. Provedores compatíveis podem variar no suporte a SSE e JSON mode.
- Permanecem melhorias de mecânica fora do fluxo principal: a API do motor e os fluxos do `PlayController` para rolagens opostas/em grupo foram adicionados, com resultados exibidos no chat e suporte a desfazer; vantagem/desvantagem pode ser declarada em `testes.txt` (`NORMAL`, `ADVANTAGE`, `DISADVANTAGE`). Há controles na aba Jogar para disputar um teste entre o herói e um NPC vivo. A interface também oferece teste de grupo com participantes e limiar de sucessos selecionados. O mestre também pode pedir disputa ou teste de grupo por `request_check` (com `opposed`/`group`), validado pelo motor e resolvido como pedido pendente; não há sintaxe de pacote própria para essas modalidades (usam os testes de `testes.txt`). A progressão básica por pontos por nível e gasto em atributo/perícia está implementada no motor e na aba Campanhas; poderes ativáveis com efeitos tipados (`poderes.txt`) existem no código, na aba Jogar e por `request_power` da IA (sem execução); sorte/destino gasta depois de rolar (repetir rolagem ou somar face, declarada globalmente em `criacao.txt` ou por teste em `testes.txt`) existe no código e na aba Jogar, e o sistema próprio já oferece ambas as formas por 1 PA; recompensas adicionais e magias adicionais seguem pendentes. Bônus numéricos de traços, incluindo resistência/vulnerabilidade a dano, só são aplicados quando o pacote declara um efeito explícito; descrições condicionais ainda não são gatilhos automáticos.
- A escala global da fonte e descrições TalkBack dos dados foram implementadas no código; revisão completa de TalkBack, contraste e layout em aparelho ainda falta. Também faltam relatório de diagnóstico exportável e configuração comprovada de um provedor gratuito padrão.
- Catálogo de personagem: as 50 raças, 50 sub-raças, 50 classes, 104 profissões e 20 híbridos declaram +1 em uma perícia; 4 das 100 maldições declaram +1 em Intimidação. M-02 está concluído em código, mas ainda sem validação por testes/CI; outros efeitos de maldição permanecem narrativos/condicionais.
- As fórmulas de PA, PM e PV do pacote próprio foram aprovadas (22/10/2026); a morte é definitiva (`modo_morte|PERMADEATH`). Regras que ainda constem em `incerto.txt` seguem como não definitivas.

## Princípios de conteúdo e licença

O app embute apenas conteúdo de criação própria: o sistema próprio, o catálogo de estilos e o catálogo de personagem. Não há links, textos nem pacotes de sistemas de terceiros. A importação ZIP aceita pacotes no formato do app, e o conteúdo importado é responsabilidade de quem o importa. A mecânica efetivamente jogada é sempre a do pacote escolhido e do motor, não uma regra improvisada pela IA.
