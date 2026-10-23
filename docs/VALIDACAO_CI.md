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

## Estado atual (2026-10-22)

O bloqueio de runner descrito no histórico abaixo foi resolvido em 2026-10-19, quando o repositório passou a ser público e os runners padrão do GitHub Actions voltaram a iniciar. Registros anteriores a esta revisão:

- 2026-10-19: execução [Android CI #39260875061](https://github.com/Kyra2214/RPG-solo/actions/runs/39260875061), commit `85b58ce`, concluída com sucesso (`./gradlew test` e `./gradlew :app:assembleDebug`; artefato `rpgsolo-debug-apk`), conforme o CHANGELOG.
- Branch `feature/racas-subracas`: execução #169 (commit `44d8215`) com sucesso; as execuções #146 a #168 falharam ou foram canceladas por erros de compilação e de teste, depois corrigidos. A observação sobre a execução #170 permanece apenas como registro histórico; não comprova o estado da branch atual.
- Depois disso, a branch de trabalho `feature/dc-05-1-source-context-app` recebeu remoção de conteúdo de terceiros, R-01/R-02/R-05 e as entregas DC-01.1–DC-05.2. O código está até `1477dad` (DC-01.4): Room v10 para fontes/pipeline/fatos, bytes de documentos no armazenamento privado e APIs no `AppModel`.
- Em 2026-10-22 foi disparada manualmente a execução [Android CI #39829094817](https://github.com/Kyra2214/RPG-solo/actions/runs/39829094817) na branch, commit `cee31e7` (documentação apenas após `1477dad`). O job falhou em `Check Kotlin style` antes de chegar a compilação/testes. O log relatou 1.331 violações ktlint em 26 arquivos de `ai/src/main/kotlin`; o relatório filtrado/sem ANSI está em [`docs/ci/android-ci-39829094817-ktlint-failure.log`](ci/android-ci-39829094817-ktlint-failure.log).
- `Run JVM and Android tests`, E2E no emulador e build/upload do APK ficaram **skipped** após a falha de estilo. O upload de relatórios foi executado pelo `if: always()`, mas não representa testes aprovados. O schema Room v10 também não foi exportado por build.
- Em 2026-10-22, depois de correções de estilo ktlint (ordem de imports, vírgulas finais, quebras de linha em `ai`, `engine`, `data`, `app` e testes) e da correção de compilação em `app/ui/RpgSoloApp.kt`, o job `test-and-…` do Android CI no commit `9c84a6c` (Android CI #296, `main`) registrou: `Check Kotlin style` **passou** (32s); `Run JVM and Android tests` **passou** (1min52s); `Upload JVM test reports` **passou** (3s); `Run Android E2E on emulator` **em execução** (cerca de 7 minutos no momento do registro); `Upload E2E diagnostics`, `Build debug APK` e `Upload debug APK` **pendentes**. Fonte: acompanhamento do job pelo app do GitHub (captura de tela), sem o log completo anexado.

**Conclusão atual: validação parcial.** No commit `9c84a6c`, o estilo (ktlint) e os testes JVM e Android passaram. O E2E no emulador estava em execução e o build/upload do APK ainda não tinham rodado; sem essas etapas verdes, o código **não** deve ser tratado como APK pronto. Pendências: o run #296 ainda estava em processamento às 20:40 (22/10/2026); falta o resultado final do E2E, do build e do upload do APK; o schema Room v10 também só é conferido pelo build. A etapa de testes é agregada (`./gradlew test`): não há resultado individual por módulo nem contagem de testes copiada para este arquivo.

| Etapa / módulo | Resultado | Commit | Data | Link |
|---|---|---|---|---|
| ktlint | **Passou** (32s) | `9c84a6c` | 2026-10-22 | Android CI #296 (link a preencher) |
| engine, rules, ai, data e app (`Run JVM and Android tests`, agregado) | **Passou** (1min52s) | `9c84a6c` | 2026-10-22 | Android CI #296 (link a preencher) |
| E2E no emulador | **Em execução** no momento do registro; resultado final pendente | `9c84a6c` | 2026-10-22 | Android CI #296 (link a preencher) |
| Build/upload do APK de debug | **Pendente** (depende de o job chegar a essas etapas) | `9c84a6c` | 2026-10-22 | Android CI #296 (link a preencher) |
| ktlint (execução anterior) | Falhou — 1.331 violações em 26 arquivos do módulo `ai`; testes, E2E e APK ignorados | `cee31e7` | 2026-10-22 | [run #39829094817](https://github.com/Kyra2214/RPG-solo/actions/runs/39829094817) · [log](ci/android-ci-39829094817-ktlint-failure.log) |

## Testes preparados e ainda não executados (2026-10-22)

`RpgDatabaseMigrationTest` (módulo `data`), `RollCardRevealTest`, `NavigationAccessibilityTest` e `CampaignStarterTest.ambientacaoNaoAlteraAFichaDoJogador` foram escritos sem Gradle/SDK e não constam de nenhuma execução do CI. Os testes instrumentados de `data` rodam no emulador do E2E sem bloquear o job; o resultado fica em `data-androidtest-result.txt` e em `data/build/reports/androidTests/connected` (artefato `rpgsolo-e2e-diagnostics`). Os esquemas Room gerados pelo build ficam no artefato `rpgsolo-room-schemas` e precisam ser commitados em `data/schemas`.

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

## CI #39707524769 — diagnóstico e correção

A execução do commit `38503b5` chegou ao runner, mas falhou na compilação Kotlin do app (`:app:compileDebugKotlin` e `:app:compileReleaseKotlin`). O log apontou helpers `approvedRuleEffects`/`approvedRuleBonusSummary` duplicados em `AppModel`, a falta do import de `SpellSlots` e código de loja de magias referenciado no painel de inventário fora de escopo. Os três problemas foram corrigidos no hotfix seguinte. O E2E não iniciou porque a compilação falhou antes da etapa do emulador.
