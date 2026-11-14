# Estado do repositório RPG Solo — 14/11/2026

## Escopo do backup

O pacote enviado (`RPG-solo-decisoes-wd.zip`) foi sincronizado no repositório `Kyra2214/RPG-solo`, branch `main`. Este registro resume o conteúdo e separa o que já foi verificado do que ainda depende de uma nova execução do GitHub Actions. A situação do APK é independente do fato de o código já estar versionado: nesta captura ainda não há um APK validado para entrega.

## O que há no repositório

- **`app/`**: aplicativo Android nativo Kotlin/Jetpack Compose, com fluxos de campanhas, criação de personagens, combate, configuração e integração com provedores de IA.
- **`engine/`**: motor local determinístico — estado, comandos/eventos, validações, combate, progressão, fatos, memória, personagens recorrentes, rivais e marcos de campanha.
- **`rules/`**: pacotes e catálogo de regras, validação de fichas e inicialização de campanhas; inclui recursos exportados do mundo Mythralis.
- **`ai/`**: provedores, contexto e parsing das propostas da IA; a IA propõe e narra, enquanto o motor valida e aplica as mudanças.
- **`data/`**: persistência Room, adaptadores e armazenamento privado dos documentos da campanha.
- **`world/`**: implementação Kotlin/JVM do simulador Mythralis e testes de conformidade contra o motor Python.
- **`Mythralis/`**: fonte de referência Python e dados editoriais do mundo. A validação Python informa 5 reinos, 46 cidades, 233 NPCs, 64 facções e 40 relógios.
- **`.github/workflows/android-ci.yml`**: automação de lint, testes Python/Kotlin, testes instrumentados em emulador e geração/publicação do APK debug como artifact `rpgsolo-debug-apk`.

## Correções desta rodada

1. Foi corrigida a expectativa obsoleta de relações no teste `test_f4.py` (o molde atual contém 66 relações, não 64).
2. O `ktlintFormat` foi aplicado; `CombatActions.kt` recebeu o import da extensão existente `spareAllowed` e a propriedade de percentual foi renomeada para camelCase. A regra de elegibilidade não foi alterada.
3. A compilação remota revelou outra colisão JVM entre a propriedade `themeMode` e o método `setThemeMode`; `@JvmName("persistThemeMode")` foi aplicado ao método sem mudar seus usos Kotlin.
4. O manifesto de recursos de Mythralis e o relatório Python foram regenerados a partir da fonte oficial. O hash atual do molde é `dfd2e53678a41edb75d5c1501d021d7a8a441976539c55ba707973b281ea6731`.
5. Todos os vetores Python→Kotlin foram regenerados; o lote de 20 sementes × 500 anos foi concluído em paralelo e gravou 21 linhas em `vetores-lote.txt`.

## Evidências e pendências

| Verificação | Estado em 14/11/2026 |
|---|---|
| Suíte Python Mythralis | 62 testes passaram no job Python do CI `43649388794`. |
| `ktlintCheck` | Passou localmente e no CI `43649388794`. |
| `validar_mundo.py` | Passou: 0 erros e 20 avisos; 5 reinos, 46 cidades, 233 NPCs, 64 facções e 40 relógios. |
| `exportar_molde.py --verificar` | Passou com o hash atualizado `dfd2e53678a41edb`. |
| Testes Kotlin `:world:test` | Passaram no CI `43649388794`, junto com a suíte completa e WD-01. O teste local não iniciou porque não há JDK 17 configurado neste ambiente. |
| Compilação Android e APK | O CI `43649388794` compilou o app, concluiu a suíte e gerou/uploadou `rpgsolo-debug-apk`; o APK desse run foi baixado para inspeção. |
| E2E no emulador | O job `43649388794` falhou ao compilar `CriacaoAbasTest.kt`: import inválido `androidx.compose.ui.test.assertDoesNotExist`. O import foi removido localmente (a interação já oferece esse método, como demonstram outros testes); falta confirmar com novo CI. |

O último CI totalmente verde anterior, no commit `21b90e1`, é histórico. O run `43649388794` gerou um APK debug, mas não concluiu o E2E; este relatório registra esse estado como snapshot. O próximo push contém a correção do teste instrumentado e disparará a validação final. O APK entregue será o artifact do run final, se o E2E passar; não é um pacote release assinado para a Play Store.
