# RPG Solo

Aplicativo Android nativo em Kotlin e Jetpack Compose para jogar RPG solo com um mestre de IA configurável. As regras e o estado são validados pelo motor local; a IA narra e propõe mudanças, mas não decide os resultados dos dados.

- Plano único (estado, lacunas e roteiro): [`PLANO_RPG_SOLO.md`](PLANO_RPG_SOLO.md)
- Histórico das entregas recentes: [`CHANGELOG.md`](CHANGELOG.md)
- Formato dos pacotes de regras: [`docs/pacotes/FORMATO_PACOTE.md`](docs/pacotes/FORMATO_PACOTE.md)
- Mecânica e equilíbrio dos dados: [`docs/MECANICA_DADOS.md`](docs/MECANICA_DADOS.md)
- Plano de integração do mundo Mythralis (Fases 21 a 25, MI-01 a MI-27): [`docs/PLANO_INTEGRACAO_MYTHRALIS.md`](docs/PLANO_INTEGRACAO_MYTHRALIS.md)
- Mundo e motor de simulação Mythralis (projeto e implementação independentes, com integração ao app): [`Mythralis/README.md`](Mythralis/README.md)

## Estado atual

O estado validado mais recente está no commit `21b90e1` de `main`. O Android CI terminou com sucesso, incluindo KtLint, suíte de testes, motor Python e testes E2E no emulador. A correção do import que impedia a compilação de Mythralis foi integrada; os módulos apontados pelo KtLint foram formatados; a string com percentuais literais foi corrigida; e o schema Room 11 foi incluído para validação das migrações.

O APK produzido pelo CI é de **debug**, destinado a testes, não uma versão release assinada para publicação na Play Store. A validação E2E em emulador não substitui a revisão visual e funcional em aparelhos físicos.

O histórico de validação anterior permanece disponível em [Android CI #40163269121](https://github.com/Kyra2214/RPG-solo/actions/runs/40163269121) e nos documentos de [validação do CI](docs/VALIDACAO_CI.md) e [relatórios](docs/relatorios/). Esses registros históricos não devem ser interpretados como o estado mais recente do projeto.
## Funcionalidades implementadas no código

- Seis áreas Compose: Jogar, Heróis, Regras, Campanhas, Sistemas e Configuração, com orientação de primeiro uso em três etapas.
- Motor determinístico em Kotlin puro, com comandos/eventos, rolagens reproduzíveis, rollback, estado de conhecimento, mapa, relações, relógios com pista pública opcional ao completar e pista intermediária ao passar da metade e validação de backups.
- Pacote de regras com criação de fichas, perícias de 0–5 pontos, perfis de dano e de perigo, itens/equipamento, bônus mecânicos e condições com duração/efeitos por rodada.
- Combate integrado à interface: iniciativa configurada pelo pacote, ataque, dano, defesa, fuga, turnos de NPC e XP; a IA não escolhe os números mecânicos.
- Progressão básica por pacote: pontos por nível, alocação validada de atributos/perícias (incluindo pré-requisitos) e histórico de XP/nível; o sistema próprio concede 1 ponto de evolução, 1 de status (sem teto) e 1 de habilidade por nível.
- Primeiro uso transacional: a tela Jogar acompanha provedor, ficha disponível e campanha ativa e não abre o chat antes das três etapas estarem prontas.
- Camada de IA com provedores configuráveis, teste de conexão, classificação de intenção, contexto, propostas validadas, sugestões e ações locais sem chamada de IA.
- Cache `ephemeral` do prefixo system implementado para a API oficial Anthropic; Config permite ativar/desativar o cache, mostra contagens de tokens lidos/criados e aceita preços por milhão com tarifas separadas para estimar custo na moeda informada. O uso real ainda não foi validado em rede. A linha do tempo automática derivada do log também foi adicionada à aba Campanhas, incluindo acontecimentos de combate com participação do jogador. O sistema próprio declara sorte/destino global: 1 PA permite repetir a rolagem ou somar uma face após o resultado, quando o teste não define uma política específica.
- Persistência via Room v11 para campanhas, eventos, fichas, metadados, biblioteca de fontes, progresso de pipeline e fatos canônicos; documentos originais ficam em armazenamento privado do app, com verificação de hash.
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

## Mythralis (mundo vivo e motor de simulação)

Mythralis é um motor de simulação de mundo vivo, independente do RPG Solo. Ele pode operar sem o aplicativo, assim como o RPG Solo pode operar sem Mythralis. Quando integrados, os dois motores se comunicam pelo contrato de integração: Mythralis mantém a simulação e o estado persistente do mundo; RPG Solo mantém as regras e a experiência de jogo. A IA atua como narradora e interface, não como autoridade unilateral sobre o cânone.

A pasta [`Mythralis/`](Mythralis/README.md) contém o mundo (cinco reinos, cidades, povos, NPCs e facções) e o motor de referência em Python. O módulo Gradle `:world` é a implementação Kotlin/JVM da simulação, com motores especializados coordenados pelo simulador, estado, dinâmica, crônica, invariantes, snapshots e testes de conformidade com o Python.

**Estado da implementação:** o módulo `:world` compila no projeto integrado. A correção de compilação de Mythralis foi incorporada ao commit `21b90e1`. O schema Room 11 foi incluído para validar as migrações e a persistência relacionada ao mundo.

**Validação mais recente:** o Android CI no commit `21b90e1` passou em KtLint, suíte de testes, motor Python e E2E no emulador. Portanto, as antigas afirmações deste README de que `:world:test`, o lint e a integração ainda não tinham sido validados não representam mais o estado atual.

A validação em emulador comprova os fluxos cobertos pela suíte automatizada, mas não equivale a testes exploratórios de todos os recursos nem à validação visual em aparelhos físicos. Desempenho em dispositivos reais, revisão visual e demais lacunas listadas no plano continuam sendo acompanhados por etapa.

Consulte o [plano de integração Mythralis](docs/PLANO_INTEGRACAO_MYTHRALIS.md) para o contrato, decisões de arquitetura, escopo das fases e pendências detalhadas.
## Lacunas conhecidas

- Sistema de atributos/combate/ofícios (Fases 15–20, N-01 a N-28) e extensões N-32 a N-36 (companheiro e criadouro, gemas e Joias, Construção, utilidades fora de combate, Tatuador): em código, conforme o `CHANGELOG.md` de 24 e 25/10. O CI mais recente, no commit `21b90e1`, passou na suíte de testes e no E2E em emulador. Validação visual e exploratória em aparelho físico ainda falta.

- Fase 14 parcialmente implementada: DC-01 a DC-05 têm lógica no `engine`/`data`/`app`, com persistência Room v11 e bytes de fonte no armazenamento privado; DC-06/DC-07 têm helpers de resumo e propostas de regras. A interface de importação, biblioteca, progresso e fatos está na aba Campanhas, ainda sem validação em aparelho. A pipeline de indexação é retomada automaticamente ao abrir o app. O consentimento Q-09 por campanha (envio de trechos de fontes à IA) já existe na aba Campanhas. PDF não tem OCR e há limites de extração; DOCX não processa tabelas estruturadas.
- Fase 12 (motor unificado) concluída em código; a suíte automatizada do CI mais recente passou; lacunas listadas no plano (3.7, MU-08.4).
- Compilação, testes, KtLint, motor Python e E2E no emulador passaram no CI mais recente, no commit `21b90e1`. O schema Room 11 foi incluído para validar as migrações. Ainda falta validação visual e exploratória em aparelhos físicos.
- O streaming progressivo agora está ligado do transporte SSE até a interface; permanece sem validação de execução nesta etapa. Provedores compatíveis podem variar no suporte a SSE e JSON mode.
- Permanecem melhorias de mecânica fora do fluxo principal: a API do motor e os fluxos do `PlayController` para rolagens opostas/em grupo foram adicionados, com resultados exibidos no chat e suporte a desfazer; vantagem/desvantagem pode ser declarada em `testes.txt` (`NORMAL`, `ADVANTAGE`, `DISADVANTAGE`). Há controles na aba Jogar para disputar um teste entre o herói e um NPC vivo. A interface também oferece teste de grupo com participantes e limiar de sucessos selecionados. O mestre também pode pedir disputa ou teste de grupo por `request_check` (com `opposed`/`group`), validado pelo motor e resolvido como pedido pendente; não há sintaxe de pacote própria para essas modalidades (usam os testes de `testes.txt`). A progressão básica por pontos por nível e gasto em atributo/perícia está implementada no motor e na aba Campanhas; poderes ativáveis com efeitos tipados (`poderes.txt`) existem no código, na aba Jogar e por `request_power` da IA (sem execução); sorte/destino gasta depois de rolar (repetir rolagem ou somar face, declarada globalmente em `criacao.txt` ou por teste em `testes.txt`) existe no código e na aba Jogar, e o sistema próprio já oferece ambas as formas por 1 PA; recompensas adicionais e magias adicionais seguem pendentes. Bônus numéricos de traços, incluindo resistência/vulnerabilidade a dano, só são aplicados quando o pacote declara um efeito explícito; descrições condicionais ainda não são gatilhos automáticos.
- A escala global da fonte e descrições TalkBack dos dados foram implementadas no código; revisão completa de TalkBack, contraste e layout em aparelho ainda falta. Também faltam relatório de diagnóstico exportável e configuração comprovada de um provedor gratuito padrão.
- Catálogo de personagem: as 50 raças, 50 sub-raças, 50 classes, 104 profissões e 20 híbridos declaram +1 em uma perícia; 4 das 100 maldições declaram +1 em Intimidação. M-02 está concluído em código, mas ainda sem validação por testes/CI; outros efeitos de maldição permanecem narrativos/condicionais.
- As fórmulas de PA, PM e PV do pacote próprio foram aprovadas (22/10/2026); a morte é definitiva (`modo_morte|PERMADEATH`). Regras que ainda constem em `incerto.txt` seguem como não definitivas.

## Princípios de conteúdo e licença

O app embute apenas conteúdo de criação própria: o sistema próprio, o catálogo de estilos e o catálogo de personagem. Não há links, textos nem pacotes de sistemas de terceiros. A importação ZIP aceita pacotes no formato do app, e o conteúdo importado é responsabilidade de quem o importa. A mecânica efetivamente jogada é sempre a do pacote escolhido e do motor, não uma regra improvisada pela IA.
