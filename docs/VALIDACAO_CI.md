# Validação CI — RPG Solo

## Execução mais recente

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
