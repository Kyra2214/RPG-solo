# RPG Solo

Aplicativo Android nativo em Kotlin e Jetpack Compose para jogar RPG solo com um mestre de IA configurável. As regras e o estado são validados pelo motor local; a IA narra e propõe mudanças, mas não decide os resultados dos dados.

- Plano de desenvolvimento: [`docs/PLANO_DESENVOLVIMENTO.md`](docs/PLANO_DESENVOLVIMENTO.md)
- Auditoria e melhorias sugeridas: [`PLANO_MELHORIAS_RPG_SOLO.md`](PLANO_MELHORIAS_RPG_SOLO.md)
- Histórico das entregas recentes: [`CHANGELOG.md`](CHANGELOG.md)
- Formato dos pacotes de regras: [`docs/pacotes/FORMATO_PACOTE.md`](docs/pacotes/FORMATO_PACOTE.md)

## Estado atual

Em 17/10/2026, `./gradlew test :app:assembleDebug` passou neste ambiente com JDK 17 e Android SDK 35. O APK de depuração é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

Esse resultado confirma a compilação e os testes Gradle, mas não substitui a validação visual em emulador/aparelho nem testes instrumentados do Room. O APK ainda não foi validado em dispositivo físico.

## Funcionalidades implementadas

- Seis áreas Compose: Jogar, Heróis, Regras, Campanhas, Sistemas e Configuração.
- Motor determinístico em Kotlin puro, com comandos validados, log de eventos, rolagens reproduzíveis, rollback e backup de campanha.
- Pacote próprio de regras carregado de recursos, criação de ficha, arquétipos, opções de traços e orçamento de perícias validado no pacote (0–5 pontos por perícia).
- Camada de IA com provedores configuráveis, classificação de intenção, narração, propostas validadas, sugestões e ações locais sem chamada de IA.
- Persistência local via Room para campanhas, eventos, fichas e metadados; a sessão principal é mantida em `ViewModel`. A lista de Campanhas permite reabrir e alternar entre campanhas salvas.
- Chave do provedor cifrada com AES/GCM usando Android Keystore; a chave não é incluída no backup.
- Backup JSON local do log de eventos e da configuração do motor, com salvar/restaurar pelo seletor de arquivos Android (SAF) ou copiar/colar texto. Restaurar cria uma nova campanha. Capítulos, missões e linha do tempo ainda não fazem parte do arquivo exportado.

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

Abra a pasta do repositório no Android Studio para executar o app em emulador ou dispositivo.

## Lacunas conhecidas

A interface e a migração Room ainda precisam de testes em aparelho/emulador e testes instrumentados, respectivamente. O app também ainda não oferece importação de pacotes de regras ou PDFs do usuário. A fase de mecânica está em andamento: falta resolução completa de ataques, equipamento e condições com duração; o efeito diferencial do primeiro ponto de perícia (0 versus 1) permanece uma decisão de regra pendente. O pacote próprio contém decisões de regra marcadas como rascunho/incertas; consulte o tutorial e as notas do pacote antes de tratá-las como definitivas.

## Princípios de conteúdo e licença

Não embuta texto de sistemas de terceiros sem licença apropriada. Sistemas sem pacote instalado podem exigir conteúdo importado pelo usuário ou uma adaptação explicitamente identificada. A mecânica efetivamente jogada é sempre a do pacote escolhido e do motor, não uma regra improvisada pela IA.
