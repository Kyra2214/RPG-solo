# RPG Solo

Aplicativo Android nativo em Kotlin e Jetpack Compose para jogar RPG solo com um mestre de IA configurável. As regras e o estado são validados pelo motor local; a IA narra e propõe mudanças, mas não decide os resultados dos dados.

- Plano de desenvolvimento: [`docs/PLANO_DESENVOLVIMENTO.md`](docs/PLANO_DESENVOLVIMENTO.md)
- Auditoria e melhorias sugeridas: [`PLANO_MELHORIAS_RPG_SOLO.md`](PLANO_MELHORIAS_RPG_SOLO.md)
- Histórico das entregas recentes: [`CHANGELOG.md`](CHANGELOG.md)
- Formato dos pacotes de regras: [`docs/pacotes/FORMATO_PACOTE.md`](docs/pacotes/FORMATO_PACOTE.md)
- Mecânica e equilíbrio dos dados: [`docs/MECANICA_DADOS.md`](docs/MECANICA_DADOS.md)

## Estado atual

A última validação documentada antes da branch atual foi em 17/10/2026, com JDK 17 e Android SDK 35. Desde então, a branch `implementation/code-first-combat` recebeu alterações de código. **Builds, testes, lint e CI não foram executados nesta etapa**, conforme a orientação de consolidar a implementação antes da validação. Portanto, o estado atual da branch ainda não está validado e não deve ser tratado como APK pronto para uso.

## Funcionalidades implementadas no código

- Seis áreas Compose: Jogar, Heróis, Regras, Campanhas, Sistemas e Configuração, com atalhos de primeiro uso.
- Motor determinístico em Kotlin puro, com comandos/eventos, rolagens reproduzíveis, rollback, estado de conhecimento, mapa, relações, relógios e validação de backups.
- Pacote de regras com criação de fichas, perícias de 0–5 pontos, perfis de dano, itens/equipamento, bônus mecânicos e condições com duração/efeitos por rodada.
- Combate integrado à interface: iniciativa configurada pelo pacote, ataque, dano, defesa, fuga, turnos de NPC e XP; a IA não escolhe os números mecânicos.
- Camada de IA com provedores configuráveis, teste de conexão, classificação de intenção, contexto, propostas validadas, sugestões e ações locais sem chamada de IA.
- Persistência via Room para campanhas, eventos, fichas e metadados; sessão principal mantida em `ViewModel`.
- Chave do provedor protegida pelo Android Keystore e excluída do backup.
- Backup JSON local com eventos/configuração do motor e metadados de capítulos, missões e linha do tempo, por seletor de arquivos Android (SAF) ou copiar/colar texto.
- Importação ZIP de pacotes de regras pela interface, com validação, limite de tamanho e restauração de pacotes importados ao reabrir o app.
- Biblioteca de fichas com consulta, duplicação, arquivamento/restauração, exclusão de arquivadas e cópia do JSON.

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

- Esta branch ainda precisa de compilação, testes unitários, validação visual em aparelho/emulador e teste instrumentado das migrações Room.
- O streaming progressivo agora está ligado do transporte SSE até a interface; permanece sem validação de execução nesta etapa. Provedores compatíveis podem variar no suporte a SSE e JSON mode.
- Permanecem melhorias de mecânica fora do fluxo principal: rolagens opostas/em grupo, vantagem/desvantagem, pontos de sorte, recompensas de evolução escolhidas pelo jogador e poderes/magias com efeitos tipados. Os bônus numéricos de traços só estão implementados para os efeitos explícitos já registrados no pacote; descrições condicionais ainda não são gatilhos automáticos.
- A escala global da fonte e descrições TalkBack dos dados foram implementadas no código; revisão completa de TalkBack, contraste e layout em aparelho ainda falta. Também faltam relatório de diagnóstico exportável e configuração comprovada de um provedor gratuito padrão.
- O pacote próprio contém fórmulas e regras marcadas como rascunho/incertas; consulte o tutorial e as notas do pacote antes de tratá-las como definitivas.

## Princípios de conteúdo e licença

Não embuta texto de sistemas de terceiros sem licença apropriada. Sistemas sem pacote instalado podem exigir conteúdo importado pelo usuário ou uma adaptação explicitamente identificada. A mecânica efetivamente jogada é sempre a do pacote escolhido e do motor, não uma regra improvisada pela IA.
