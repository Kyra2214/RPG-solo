# RPG Solo

Aplicativo Android nativo em Kotlin e Jetpack Compose para jogar RPG solo com um mestre de IA configurável. As regras e o estado são validados pelo motor local; a IA narra e propõe mudanças, mas não decide os resultados dos dados.

- Plano de desenvolvimento: [`docs/PLANO_DESENVOLVIMENTO.md`](docs/PLANO_DESENVOLVIMENTO.md)
- Auditoria e melhorias sugeridas: [`PLANO_MELHORIAS_RPG_SOLO.md`](PLANO_MELHORIAS_RPG_SOLO.md)
- Histórico das entregas recentes: [`CHANGELOG.md`](CHANGELOG.md)
- Formato dos pacotes de regras: [`docs/pacotes/FORMATO_PACOTE.md`](docs/pacotes/FORMATO_PACOTE.md)
- Mecânica e equilíbrio dos dados: [`docs/MECANICA_DADOS.md`](docs/MECANICA_DADOS.md)

## Estado atual

A execução #169 do Android CI, no commit `44d82157f2cbd554944ea71a64c95c87cc12accc`, passou nos testes JVM e na montagem/upload do APK de depuração. O PR #2 foi mesclado à `main` no commit `62a3e8a96befd950981d8caa19239abf03aabe34`. O teste instrumentado E2E foi tentado no emulador, mas falhou ao iniciar os testes (o log registrou “Starting 0 tests”); como essa etapa é não bloqueante, ela não tornou o CI vermelho. Portanto, o fluxo JVM e a geração do APK estão validados nessa execução, mas a jornada E2E em emulador continua pendente. Consulte [Validação CI](docs/VALIDACAO_CI.md) para detalhes.

## Funcionalidades implementadas no código

- Seis áreas Compose: Jogar, Heróis, Regras, Campanhas, Sistemas e Configuração, com orientação de primeiro uso em três etapas.
- Motor determinístico em Kotlin puro, com comandos/eventos, rolagens reproduzíveis, rollback, estado de conhecimento, mapa, relações, relógios com pista pública opcional ao completar e validação de backups.
- Pacote de regras com criação de fichas, perícias de 0–5 pontos, perfis de dano, itens/equipamento, bônus mecânicos e condições com duração/efeitos por rodada.
- Combate integrado à interface: iniciativa configurada pelo pacote, ataque, dano, defesa, fuga, turnos de NPC e XP; a IA não escolhe os números mecânicos.
- Progressão básica por pacote: pontos por nível, alocação validada de atributos/perícias (incluindo pré-requisitos) e histórico de XP/nível; o sistema próprio concede 1 ponto por nível.
- Primeiro uso transacional: a tela Jogar acompanha provedor, ficha disponível e campanha ativa e não abre o chat antes das três etapas estarem prontas.
- Camada de IA com provedores configuráveis, teste de conexão, classificação de intenção, contexto, propostas validadas, sugestões e ações locais sem chamada de IA.
- Cache `ephemeral` do prefixo system implementado para a API oficial Anthropic; Config permite ativar/desativar o cache, mostra contagens de tokens lidos/criados e aceita preços por milhão com tarifas separadas para estimar custo na moeda informada. O uso real ainda não foi validado em rede. A linha do tempo automática derivada do log também foi adicionada à aba Campanhas, incluindo acontecimentos de combate com participação do jogador.
- Persistência via Room para campanhas, eventos, fichas e metadados; sessão principal mantida em `ViewModel`.
- Chave do provedor protegida pelo Android Keystore e excluída do backup.
- Backup JSON local com eventos/configuração do motor e metadados de capítulos, missões e linha do tempo, por seletor de arquivos Android (SAF) ou copiar/colar texto.
- Importação ZIP de pacotes de regras pela interface, com validação, limite de tamanho e restauração de pacotes importados ao reabrir o app.
- Biblioteca de fichas com consulta, duplicação, arquivamento/restauração, exclusão de arquivadas e cópia do JSON.
- Acessibilidade inicial: escala global de fonte ajustável (85%–140%) e descrições TalkBack para os dados, mantendo os resultados ocultos até serem revelados.

## Módulos

- `app` — telas Compose, navegação, configuração e integração do aplicativo.
- `engine` — estado canônico, comandos/eventos, dados, capítulos, diário e backup.
- `rules` — catálogo, pacotes de regras, validação de fichas e criação de campanha.
- `ai` — provedores, loop de jogo, contexto e parsing das propostas.
- `data` — banco Room e adaptador de armazenamento de eventos.

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

- O teste instrumentado E2E precisa de diagnóstico: a última tentativa falhou em `:app:connectedDebugAndroidTest` e reportou “Starting 0 tests”. Veja [Validação CI](docs/VALIDACAO_CI.md).
- A validação visual em aparelho/emulador e os testes instrumentados das migrações Room ainda precisam ser concluídos.
- O streaming progressivo está ligado do transporte SSE até a interface; permanece sem validação real em rede. Provedores compatíveis podem variar no suporte a SSE e JSON mode.
- Permanecem melhorias de mecânica fora do fluxo principal: a API do motor e os fluxos do `PlayController` para rolagens opostas/em grupo foram adicionados, com resultados exibidos no chat e suporte a desfazer; vantagem/desvantagem pode ser declarada em `testes.txt` (`NORMAL`, `ADVANTAGE`, `DISADVANTAGE`). Há controles na aba Jogar para disputar um teste entre o herói e um NPC vivo. A interface também oferece teste de grupo com participantes e limiar de sucessos selecionados. Nenhuma dessas modalidades tem sintaxe de pacote nem pedido pendente narrativo. A progressão básica por pontos por nível e gasto em atributo/perícia está implementada no motor e na aba Campanhas; pontos de sorte/destino, recompensas adicionais e poderes/magias com efeitos tipados seguem pendentes. Bônus numéricos de traços, incluindo resistência/vulnerabilidade a dano, só são aplicados quando o pacote declara um efeito explícito; descrições condicionais ainda não são gatilhos automáticos.
- A escala global da fonte e descrições TalkBack dos dados foram implementadas no código; revisão completa de TalkBack, contraste e layout em aparelho ainda falta. Também faltam relatório de diagnóstico exportável e configuração comprovada de um provedor gratuito padrão.
- O pacote próprio contém fórmulas e regras marcadas como rascunho/incertas; consulte o tutorial e as notas do pacote antes de tratá-las como definitivas.

## Princípios de conteúdo e licença

Não embuta texto de sistemas de terceiros sem licença apropriada. Sistemas sem pacote instalado podem exigir conteúdo importado pelo usuário ou uma adaptação explicitamente identificada. A mecânica efetivamente jogada é sempre a do pacote escolhido e do motor, não uma regra improvisada pela IA.
