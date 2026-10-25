# RPG Solo

Aplicativo Android nativo em Kotlin e Jetpack Compose para jogar RPG solo com um mestre de IA configurável. As regras e o estado são validados pelo motor local; a IA narra e propõe mudanças, mas não decide os resultados dos dados.

- Plano único (estado, lacunas e roteiro): [`PLANO_RPG_SOLO.md`](PLANO_RPG_SOLO.md)
- Histórico das entregas recentes: [`CHANGELOG.md`](CHANGELOG.md)
- Formato dos pacotes de regras: [`docs/pacotes/FORMATO_PACOTE.md`](docs/pacotes/FORMATO_PACOTE.md)
- Mecânica e equilíbrio dos dados: [`docs/MECANICA_DADOS.md`](docs/MECANICA_DADOS.md)

## Estado atual

O último CI totalmente verde é o [Android CI #40097446606](https://github.com/Kyra2214/RPG-solo/actions/runs/40097446606), em 24/10/2026, no commit `40c38b1`: ktlint, 650 testes JVM (`ai` 203, `engine` 255, `rules` 192), 9 testes E2E no emulador (API 35), 6 testes instrumentados de `data` (migrações Room) e build/upload do APK de debug passaram. Relatório em [`docs/relatorios/ci-android-2026-10-24.md`](docs/relatorios/ci-android-2026-10-24.md); histórico e pendências em [`docs/VALIDACAO_CI.md`](docs/VALIDACAO_CI.md). O APK é de **debug**, não uma versão release. Antes disso, o CI #39829094817 (`cee31e7`, 22/10/2026) havia falhado em `Check Kotlin style` ([log](docs/ci/android-ci-39829094817-ktlint-failure.log)).

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

## Lacunas conhecidas

- Sistema de atributos/combate/ofícios (Fases 15–20, N-01 a N-28, exceto N-25 bloqueado por gemas e acampamento): concluído em código, **nunca compilado nem testado** (sem rede nas sessões). Profissões de Exploração, Comércio, Mecanismos, Mar, Animais, Artes e Identificação têm 5 degraus; montaria, companheiro e inspirar seguem pendentes. Próximo passo: `./gradlew test :app:assembleDebug` e corrigir o que falhar.

- Fase 14 parcialmente implementada e não validada: DC-01 a DC-05 têm lógica no `engine`/`data`/`app`, incluindo Room v10 e bytes de fonte no armazenamento privado; DC-06/DC-07 têm helpers de resumo e propostas de regras. A interface de importação, biblioteca, progresso e fatos está na aba Campanhas, ainda sem validação em aparelho. A pipeline de indexação é retomada automaticamente ao abrir o app. O consentimento Q-09 por campanha (envio de trechos de fontes à IA) já existe na aba Campanhas. PDF não tem OCR e há limites de extração; DOCX não processa tabelas estruturadas.
- Fase 12 (motor unificado) concluída em código, com testes escritos mas nunca executados (`EffectiveValueTest`, `UnifiedCombatTest`, `AiParityTest`); lacunas listadas no plano (3.7, MU-08.4).
- Compilação, testes JVM, E2E no emulador e testes de migração Room passaram no CI #40097446606 (`40c38b1`). Ainda faltam validação visual em aparelho físico e commitar o schema Room v10 gerado pelo build (`data/schemas` só tem `2.json` e `3.json`; o artefato `rpgsolo-room-schemas` traz os gerados).
- O streaming progressivo agora está ligado do transporte SSE até a interface; permanece sem validação de execução nesta etapa. Provedores compatíveis podem variar no suporte a SSE e JSON mode.
- Permanecem melhorias de mecânica fora do fluxo principal: a API do motor e os fluxos do `PlayController` para rolagens opostas/em grupo foram adicionados, com resultados exibidos no chat e suporte a desfazer; vantagem/desvantagem pode ser declarada em `testes.txt` (`NORMAL`, `ADVANTAGE`, `DISADVANTAGE`). Há controles na aba Jogar para disputar um teste entre o herói e um NPC vivo. A interface também oferece teste de grupo com participantes e limiar de sucessos selecionados. O mestre também pode pedir disputa ou teste de grupo por `request_check` (com `opposed`/`group`), validado pelo motor e resolvido como pedido pendente; não há sintaxe de pacote própria para essas modalidades (usam os testes de `testes.txt`). A progressão básica por pontos por nível e gasto em atributo/perícia está implementada no motor e na aba Campanhas; poderes ativáveis com efeitos tipados (`poderes.txt`) existem no código, na aba Jogar e por `request_power` da IA (sem execução); sorte/destino gasta depois de rolar (repetir rolagem ou somar face, declarada globalmente em `criacao.txt` ou por teste em `testes.txt`) existe no código e na aba Jogar, e o sistema próprio já oferece ambas as formas por 1 PA; recompensas adicionais e magias adicionais seguem pendentes. Bônus numéricos de traços, incluindo resistência/vulnerabilidade a dano, só são aplicados quando o pacote declara um efeito explícito; descrições condicionais ainda não são gatilhos automáticos.
- A escala global da fonte e descrições TalkBack dos dados foram implementadas no código; revisão completa de TalkBack, contraste e layout em aparelho ainda falta. Também faltam relatório de diagnóstico exportável e configuração comprovada de um provedor gratuito padrão.
- Catálogo de personagem: as 50 raças, 50 sub-raças, 50 classes, 104 profissões e 20 híbridos declaram +1 em uma perícia; 4 das 100 maldições declaram +1 em Intimidação. M-02 está concluído em código, mas ainda sem validação por testes/CI; outros efeitos de maldição permanecem narrativos/condicionais.
- As fórmulas de PA, PM e PV do pacote próprio foram aprovadas (22/10/2026); a morte é definitiva (`modo_morte|PERMADEATH`). Regras que ainda constem em `incerto.txt` seguem como não definitivas.

## Princípios de conteúdo e licença

O app embute apenas conteúdo de criação própria: o sistema próprio, o catálogo de estilos e o catálogo de personagem. Não há links, textos nem pacotes de sistemas de terceiros. A importação ZIP aceita pacotes no formato do app, e o conteúdo importado é responsabilidade de quem o importa. A mecânica efetivamente jogada é sempre a do pacote escolhido e do motor, não uma regra improvisada pela IA.
