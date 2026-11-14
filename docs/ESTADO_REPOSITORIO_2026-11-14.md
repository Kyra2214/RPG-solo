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
| Suíte Python Mythralis | 62 testes passaram no job Python do CI `43653473002`. |
| `ktlintCheck` | Passou localmente e no CI `43653473002`. |
| `validar_mundo.py` | Passou: 0 erros e 20 avisos; 5 reinos, 46 cidades, 233 NPCs, 64 facções e 40 relógios. |
| `exportar_molde.py --verificar` | Passou com o hash atualizado `dfd2e53678a41edb`. |
| Testes Kotlin `:world:test` | Passaram no CI `43653473002`, junto com a suíte completa e WD-01. O teste local não iniciou porque não há JDK 17 configurado neste ambiente. |
| Compilação Android e APK | O CI `43653473002` compilou o app, concluiu os testes principais e gerou/uploadou `rpgsolo-debug-apk`. O APK é de debug; o run só ficou vermelho por um teste instrumentado. |
| E2E no emulador | No run `43653473002`, 12 testes executaram e 11 passaram. O único restante foi `sortearNome_preencheONome`: o nome do modelo mutável `CharacterDraft` não acionava de modo observável a recomposição do campo/aviso. A correção local tornou o nome um estado Compose explícito, sincronizado com o rascunho e a validação; `ktlintCheck` passou e aguarda novo CI. Os ajustes anteriores reduziram as falhas de cinco para uma. |

Este relatório registra o run `43653473002` no commit `eeff075`; a alteração de estado Compose descrita acima está local e ainda não foi enviada. O APK debug desse run foi gerado, mas o E2E ainda não está totalmente verde. O APK entregue será o artifact do run final, após validar a correção; não é um pacote release assinado para a Play Store.
