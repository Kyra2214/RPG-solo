# Relatório de testes e build Android — 24/10/2026

## Resultado executivo

O workflow **Android CI** concluiu com sucesso em 24 de outubro de 2026. Todas as etapas obrigatórias foram aprovadas, incluindo verificação de estilo Kotlin, testes JVM, testes instrumentados Android/E2E no emulador, montagem do APK debug e publicação do artefato.

- **Repositório:** [Kyra2214/RPG-solo](https://github.com/Kyra2214/RPG-solo)
- **Branch:** `main`
- **Commit validado:** `40c38b1e26a0330b8de67447a83adb49ca2dbda9` (`Keep play chat controls accessible on small screens`)
- **Execução do GitHub Actions:** [Android CI #40097446606](https://github.com/Kyra2214/RPG-solo/actions/runs/40097446606)
- **Início da execução:** 24/10/2026 às 10:16:44 (UTC−03:00)
- **Conclusão:** sucesso; job `test-and-build` concluído em aproximadamente 5 min 46 s.

## Mudança coberta

A alteração validada reorganiza a tela de jogo para que inventário, loja, poderes e outros painéis fiquem na área rolável junto ao histórico, mantendo o campo de ação e o botão **Enviar** acessíveis no rodapé. O teste instrumentado `jogadaCompletaComRolagemEnarracao` também passou a exigir que **Enviar** esteja visível e habilitado antes de tocar nele. Isso cobre a regressão que impedia a jogada de alcançar o transporte falso quando os painéis ocupavam a tela.

## Resultados por etapa

| Etapa | Resultado | Evidência / detalhe |
|---|---|---|
| `Check Kotlin style` | **Aprovada** | `./gradlew ktlintCheck --stacktrace` |
| `Run JVM and Android tests` | **Aprovada** | `./gradlew test --continue --stacktrace` |
| Suíte JVM — `ai` | **203 passaram** | 23 relatórios JUnit; 0 falhas, 0 erros, 0 ignorados |
| Suíte JVM — `engine` | **255 passaram** | 28 relatórios JUnit; 0 falhas, 0 erros, 0 ignorados |
| Suíte JVM — `rules` | **192 passaram** | 17 relatórios JUnit; 0 falhas, 0 erros, 0 ignorados |
| **Total JVM** | **650 passaram** | 68 relatórios JUnit; 0 falhas, 0 erros, 0 ignorados |
| E2E Android do app | **9 testes concluídos** | `:app:connectedDebugAndroidTest`, emulador Android API 35 (`x86_64`, perfil Pixel 2); 0 falhas e 0 ignorados indicados no progresso do runner; tarefa Gradle aprovada |
| Testes instrumentados de `data` | **6 testes concluídos** | `:data:connectedDebugAndroidTest`; o log registra início e conclusão dos 6 testes e a tarefa Gradle termina com sucesso |
| `Build debug APK` | **Aprovada** | `./gradlew :app:assembleDebug --stacktrace` |
| `Upload debug APK` | **Aprovada** | Artefato `rpgsolo-debug-apk` publicado pelo Actions |

Os demais passos do job (checkout, configuração do JDK 17/Gradle, publicação dos relatórios/schema, configuração do KVM e conclusão do job) também terminaram com sucesso.

## APK gerado

- **Arquivo:** `app-debug.apk` (variante debug; não é uma versão release para publicação na Play Store)
- **Tamanho baixado:** 11.365.245 bytes
- **SHA-256:** `dd870c22fbc4b90f03e06070efaaac7a55bec7875adf0c0ac16cb04057d68fad`
- **Artefato no CI:** [`rpgsolo-debug-apk`](https://github.com/Kyra2214/RPG-solo/actions/runs/40097446606/artifacts/12561751358)
- **Validação local:** reconhecido como pacote Android e verificado com `unzip -t` sem erros.

O workflow configura retenção de 14 dias para o artefato do APK e para os relatórios JVM/E2E. O SHA-256 acima identifica exatamente o APK obtido nesta execução.

## Observações

O log do emulador registrou avisos de console/gráficos durante a execução; eles não impediram a conclusão das suítes ou do job. O GitHub Actions também emitiu um aviso de descontinuação do runtime Node.js 20 para ações de terceiros; não foi uma falha deste build.

## Artefatos e reprodutibilidade

Os XMLs JUnit somados neste relatório foram baixados do artefato `rpgsolo-jvm-test-reports` da execução indicada. Os diagnósticos instrumentados foram publicados como `rpgsolo-e2e-diagnostics`. Para reproduzir as etapas principais a partir do checkout do commit validado:

```bash
./gradlew ktlintCheck --stacktrace
./gradlew test --continue --stacktrace
./gradlew :app:connectedDebugAndroidTest --stacktrace
./gradlew :data:connectedDebugAndroidTest --stacktrace
./gradlew :app:assembleDebug --stacktrace
```

Os testes instrumentados requerem emulador Android configurado; no CI foi usado Android API 35, arquitetura `x86_64`, perfil Pixel 2, com animações desabilitadas.
