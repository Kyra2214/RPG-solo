# Revisão de intenção e balanceamento do catálogo — 2026-11-08

## Escopo e critério

Revisão do catálogo ativo em `rules/src/main/resources/packages/sistema-proprio/personagem-catalogo.json`, cobrindo raças, sub-raças, híbridos, classes e profissões. Foram comparados nome, resumo, lore, competências/traços disponíveis, escopo e valor de cada `mechanicalEffect` com as regras próprias e com a política de acumulação do projeto.

A perícia é resolvida por pontos: no teste padrão, cada ponto acrescenta uma face de sucesso. Efeitos de declarações com `effectId` distintos acumulam; somente a mesma declaração é deduplicada. A revisão não presume tetos ou metas de probabilidade ausentes dos documentos do sistema. Os JSON sob `Mythralis/referencia/dados-brutos` e o snapshot de mundo correspondente foram deixados intactos: são snapshots de referência, enquanto o runtime carrega o recurso canônico acima.

## Cobertura

| Categoria | Entradas revisadas | Efeitos revisados antes das correções | Efeitos após as correções |
|---|---:|---:|---:|
| Raças | 50 | 50 | 50 |
| Sub-raças | 50 | 50 | 50 |
| Híbridos | 20 | 20 | 20 |
| Classes | 50 | 50 | 50 |
| Profissões | 104 | 104 | 102 |
| **Total** | **274** | **274** | **272** |

A revisão confirmou que os efeitos restantes usam escopos existentes. A auditoria estrutural anterior registrou 17 escopos válidos e nenhum escopo inválido.

## Correções aplicadas

1. **Mergulhador (`professions/mergulhador`)** — removido `pilotagem +1`. O resumo e as competências descrevem exploração submersa, orientação, passagens e vedações. No pacote, Pilotagem é condução de veículos; não há gatilho ou escopo de mergulho que restrinja o bônus a essa atividade. O efeito permanece vazio até que exista uma regra compatível.
2. **Tratador de Aves Mensageiras (`professions/tratador-de-aves-mensageiras`)** — removido `montaria +1`. A entrada descreve cuidado de aves já treinadas e organização de mensagens; não descreve cavalgar ou usar aves como montarias. O efeito permanece vazio até haver escopo/gatilho suportado, sem conversão especulativa para Adestramento.
3. **Sangue Lunar (`hybrids/humano-licantropo`)** — mantido `atletismo +1` e reescritos resumo e lore para explicitar que a ascendência não provoca transformação automática, mas concede o bônus tipado declarado. Isso elimina a contradição interna sem mudar o efeito mecânico.

## Resultado de balanceamento

Nas raças, sub-raças, híbridos e classes não foi demonstrado erro individual de escopo ou valor que justificasse alteração de dados. Também não foi recomendado redistribuir os demais efeitos profissionais: sem benchmarks de frequência, teto ou probabilidade-alvo, uma mudança numérica seria especulativa.

**Risco sistêmico a decidir:** seleções de fontes distintas podem acumular bônus no mesmo escopo (raça ou híbrido, até quatro classes e profissão, conforme a validação atual). Como cada ponto de perícia acrescenta uma face de sucesso, combinações podem elevar significativamente um teste. Os documentos do pacote não estabelecem limite específico de empilhamento por perícia ou um objetivo quantitativo de equilíbrio. Esta revisão registra o risco, mas não altera a política de acumulação previamente aprovada nem inventa um teto. A decisão deve preceder qualquer rebalanceamento amplo.

## Teste de regressão e validação

Foi acrescentado a `CharacterCreationCatalogTest` um teste para garantir que os dois escopos profissionais incompatíveis permaneçam removidos e que o híbrido Sangue Lunar continue com `atletismo +1` e texto coerente.

- Verificação estática: JSON válido; 274 entradas auditadas; 272 efeitos declarados após as duas remoções; asserts das três correções passaram.
- `./gradlew :rules:test` — **passou** com JDK 17.
- `./gradlew :app:testDebugUnitTest` — **bloqueado antes da execução dos testes** neste Sandbox: Android SDK ausente (`ANDROID_HOME`/`sdk.dir` não configurado). A tentativa foi repetida em 2026-11-08 e o Gradle reportou `SDK location not found`; nenhum teste instrumentado ou emulador foi iniciado. O teste de regressão do app continua pendente em ambiente com Android SDK.
- `git diff --check` — sem erros de whitespace após correção.

## Registro das validações sequenciais — 2026-11-08

1. `./gradlew :app:testDebugUnitTest --no-daemon` — a primeira tentativa foi **bloqueada** pela ausência do Android SDK. Em 2026-11-08, após instalar a plataforma API 35 e os build-tools 34.0.0 e configurar JDK 17/`ANDROID_HOME`, o teste executou: **8 testes, 7 passaram e 1 falhou**. `CharacterCreationCatalogTest.efeitosMecanicosDeTodasAsCategoriasUsamEscoposDoPacote` falhou porque `Adestrador` declara o escopo `adestramento`, que não existe no pacote `sistema-proprio`. Nenhum teste instrumentado ou emulador foi iniciado. Durante a configuração, o Gradle instalou automaticamente `platform-tools`; não foram instalados emulator ou imagens de sistema.
2. `./gradlew test --no-daemon` — a primeira tentativa foi **bloqueada** por falta de JDK 17; uma repetição após instalar o JDK 17 encontrou depois a ausência do Android SDK. Com JDK 17 e SDK API 35 configurados, a suíte foi executada em 2026-11-08. A execução padrão falhou em `:app:testDebugUnitTest`; com `--continue`, foram coletados três alvos com falha: `:app:testDebugUnitTest` e `:app:testReleaseUnitTest` (cada um com 8 testes, 7 passaram e 1 falhou pela asserção do escopo `adestramento` de Adestrador) e `:world:compileKotlin` (erro de sintaxe `Dinamica.kt:98: Unclosed comment`, seguido de erros de referências dependentes). Nenhum teste instrumentado ou emulador foi iniciado.
3. `./gradlew ktlintCheck --no-daemon` — **falhou**; a execução padrão parou no primeiro módulo (`app`). A mesma validação foi repetida com `--continue` para coletar todos os resultados e encontrou quatro falhas de tarefas: `app` (`CombatBonuses.kt`, formatação em 213 e 301–302), `engine` (`AeltharynWorld.kt`, indentação inesperada entre as linhas 353 e 1049), `world` principal (`Dinamica.kt:98`, comentário não fechado) e testes de `world` (`Conformidade.kt`, `LoteConformidadeTest.kt`, `MotoresConformidadeTest.kt`, `RngJsonConformidadeTest.kt` e `SimuladorConformidadeTest.kt`, regras de formatação). Nenhum arquivo de código foi alterado nesta etapa.

## Estado

A lacuna de revisão de intenção e balanceamento foi concluída para as cinco categorias acima. Continuam separadas como decisões ou etapas futuras: política/teto de empilhamento, revisão de lore e retratos, cobertura de falhas parciais de backup e validação Android/em aparelho. Não houve alteração das cópias históricas do catálogo usadas como referência de mundo.
