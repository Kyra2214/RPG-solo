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
3. O manifesto de recursos de Mythralis e o relatório Python foram regenerados a partir da fonte oficial. O hash atual do molde é `dfd2e53678a41edb75d5c1501d021d7a8a441976539c55ba707973b281ea6731`.
4. Os vetores Python→Kotlin de RNG/JSON, estado, motores, simulador de 500 anos e o lote de 20 sementes estão sendo alinhados com a fonte Python atual. O arquivo do lote será incorporado em um push subsequente assim que a geração terminar.

## Evidências e pendências

| Verificação | Estado em 14/11/2026 |
|---|---|
| Suíte Python Mythralis | 62 testes passaram no job Python do CI `43645457039`, após a correção da contagem. |
| `ktlintCheck` | Passou localmente após o formatter. |
| `validar_mundo.py` | Passou: 0 erros e 20 avisos; 5 reinos, 46 cidades, 233 NPCs, 64 facções e 40 relógios. |
| `exportar_molde.py --verificar` | Passou com o hash atualizado `dfd2e53678a41edb`. |
| Testes Kotlin `:world:test` | Ainda aguardam validação em CI com os vetores regenerados. O Gradle local não encontrou JDK 17 e não iniciou os testes; isso é uma limitação do ambiente local, não um resultado de teste. |
| Compilação Android, E2E e APK | Ainda pendentes de nova execução com runner Android. A execução `43645457039` foi falha: apontou os dados/vetores antigos e o import ausente, que foram corrigidos localmente depois do run. |

O último CI verde anterior, no commit `21b90e1`, é histórico e não valida estas alterações mais recentes. O APK do workflow é de **debug**, para testes; não é um pacote release assinado para a Play Store. O próximo push atualizará os vetores finais, disparará nova execução do workflow e permitirá baixar o APK somente após a geração do artifact.
