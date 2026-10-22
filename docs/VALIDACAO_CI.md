# Validação CI — RPG Solo

## Subfase I-02.3 — memórias por cena (2026-10-21)

O contexto do mestre passou a incluir `GameState.memories` somente para entidades listadas em `sceneNpcIds`. Memórias de NPCs ausentes não são enviadas, evitando vazamento de informação entre cenas. O teste foi adicionado em `AgendaProposalTest.contextoIncluiApenasMemoriasDosNpcsDaCena`.

Validação após a limpeza dos conflitos publicados no rebase:

```text
./gradlew :engine:test :rules:test :ai:test
BUILD SUCCESSFUL in 20s
14 actionable tasks: 14 executed
```

O CI Android da fase 14 continua em andamento no [workflow #39707201903](https://github.com/Kyra2214/RPG-solo/actions/runs/39707201903). O commit desta subfase será publicado e validado em uma execução própria.

## Fase 14 — validação local (2026-10-21)

O conteúdo de `RPG-solo-fase14.zip` foi aplicado ao repositório e validado com Gradle. O ambiente Sandbox possui JDK 21, mas não possui JDK 17 nem Android SDK; por isso o `./gradlew test` literal não pôde iniciar por falta de toolchain 17 e, em uma cópia temporária usando JDK 21, parou ao resolver `:app:testDebugUnitTest` por ausência do SDK Android.

Após as correções de compilação e comportamento abaixo, a suíte JVM pura foi executada em cópia temporária com JDK 21:

```text
./gradlew :engine:test :rules:test :ai:test
BUILD SUCCESSFUL in 14s
14 actionable tasks: 14 executed
```

Foram aprovados os módulos `engine`, `rules` e `ai`; o relatório do Gradle registrou 222 testes no engine e 193 testes no `ai` nas execuções finais. O único aviso foi a depreciação do construtor `java.net.URL` em `ai/Http.kt`; não houve falha de teste na execução verde.

Correções realizadas durante a validação: tipagem de `ResetTraitUses`, regra de faces dos testes por pontos, filtragem de NPC privado na timeline, classificação de relações do arquivo completo, sanitização de marcadores de trechos de fonte e imports das extensões de sorte/sobrevivência/remoção de condição.

O teste Android completo e o E2E permanecem dependentes do GitHub Actions, pois o Sandbox não tem Android SDK. Eles não devem ser marcados como aprovados até uma execução verde do workflow no commit publicado.

## Estado atual (2026-10-21)

O bloqueio de runner descrito no histórico abaixo foi resolvido em 2026-10-19, quando o repositório passou a ser público e os runners padrão do GitHub Actions voltaram a iniciar. Registros anteriores a esta revisão:

- 2026-10-19: execução [Android CI #39260875061](https://github.com/Kyra2214/RPG-solo/actions/runs/39260875061), commit `85b58ce`, concluída com sucesso (`./gradlew test` e `./gradlew :app:assembleDebug`; artefato `rpgsolo-debug-apk`), conforme o CHANGELOG.
- Branch `feature/racas-subracas`: execução #169 (commit `44d8215`) com sucesso; as execuções #146 a #168 falharam ou foram canceladas por erros de compilação e de teste, depois corrigidos. A #170 (merge em `main`, commit `62a3e8a`) estava em andamento quando o plano foi escrito; seu resultado **não está registrado**.
- Depois disso, a branch recebeu remoção de conteúdo de terceiros, R-01 (trava de sessão e exclusividade de turno), R-02 (Room fora da thread principal) e R-05 (transporte cancelável). **Nada disso foi compilado nem testado**; não há resultado de CI para esses commits.

**Conclusão atual: o código da branch não está validado.** (2026-10-21: nova tentativa local de `gradle :engine:test :rules:test :ai:test` falhou ao resolver plugins por falta de acesso a Maven Central/Google; nada executado.) Para fechar, rodar o CI e colar abaixo, por módulo (`engine`, `rules`, `ai`, `data`, `app`), o resultado, o commit, a data e o link da execução; o E2E em emulador roda com `continue-on-error` e deve ser registrado à parte.

| Módulo | Resultado | Commit | Data | Link |
|---|---|---|---|---|
| engine | pendente | | | |
| rules | pendente | | | |
| ai | pendente | | | |
| data | pendente | | | |
| app | pendente | | | |
| E2E | pendente | | | |

## Histórico: período sem runner (2026-10-18 e 2026-10-19, resolvido)

As seções abaixo descrevem o período em que os jobs não iniciavam e são mantidas só como registro.

## Execução autorizada

- Workflow: [Android CI](https://github.com/Kyra2214/RPG-solo/actions/runs/39524086483).
- Execução: **#169**, concluída em 21/10/2026.
- Branch validada: `feature/racas-subracas`.
- Commit testado: `44d82157f2cbd554944ea71a64c95c87cc12accc`.
- Resultado geral do job `test-and-build`: **success**.
- JDK: 17 (Temurin); runner: `ubuntu-latest`.
- PR #2 foi mesclado em `main` pelo commit `62a3e8a96befd950981d8caa19239abf03aabe34`: https://github.com/Kyra2214/RPG-solo/pull/2.

## Resultado por etapa

| Etapa | Resultado | Observações |
|---|---|---|
| Checkout e configuração do JDK/Gradle | Aprovado | Ambiente configurado. |
| Testes JVM e testes Gradle dos módulos | **Aprovado** | `./gradlew test --continue --stacktrace` terminou com sucesso. |
| Upload dos relatórios JVM | Aprovado | Artefato de relatórios configurado para retenção de 14 dias. |
| Teste instrumentado E2E no emulador | **Não validado** | A tentativa executou `connectedDebugAndroidTest`, mas o Gradle falhou; o log informa “Starting 0 tests”. A etapa usa `continue-on-error: true`, então não bloqueia o CI principal. |
| Montagem do APK de depuração | **Aprovado** | `./gradlew :app:assembleDebug --stacktrace` terminou com `BUILD SUCCESSFUL`. |
| Upload do APK | Aprovado | Artefato `rpgsolo-debug-apk`, com `app-debug.apk`, retenção de 14 dias. |

## Como interpretar o status verde

O status verde desta execução confirma a suíte Gradle de testes JVM e a montagem/upload do APK. **Não significa que o E2E passou**: a execução instrumentada falhou e foi deliberadamente marcada como não bloqueante no workflow. A falha E2E precisa de investigação separada antes de considerar a jornada em emulador validada.

## Reproduzir localmente

Requisitos: JDK 17, Android SDK e licenças aceitas.

```bash
./gradlew test --continue --stacktrace
./gradlew :app:assembleDebug --stacktrace
```

Para tentar os testes instrumentados em emulador:

```bash
./gradlew :app:connectedDebugAndroidTest --stacktrace
```

O E2E requer um emulador compatível e pode falhar independentemente da suíte JVM. Ao registrar uma nova validação, anotar link da execução, commit, resultado de cada etapa e artefatos publicados. Não registrar como aprovado um teste que não executou ou cuja etapa falhou.

## Histórico anterior

As execuções antigas sem runner e sem etapas, descritas em versões anteriores deste documento, eram inconclusivas. A execução #169 conseguiu iniciar o runner e concluiu com sucesso as etapas bloqueantes de testes JVM e montagem do APK; o E2E permanece pendente conforme descrito acima.
