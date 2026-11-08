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

**Decisão sobre empilhamento (08/11/2026):** manter o acúmulo por identidade já aprovado e não acrescentar um teto global de bônus do catálogo. A fórmula existente de `POINTS` já limita o resultado efetivo por dado: `min(max(pontos, 0) + 1, lados − 1)`. Assim, a face 1 sempre falha, e bônus além da saturação do dado não aumentam a probabilidade daquele teste. Um teto global independente do dado achataria parte da escala de dificuldade sem uma meta de probabilidade que o justifique.

Uma enumeração das escolhas válidas atuais (uma raça com sub-raça compatível ou um híbrido, até quatro classes distintas e uma profissão) encontrou máximo de **+6** de efeitos do catálogo no mesmo escopo. Um exemplo é `humano` + `humano-das-cidades`, classes `mago`, `cronista`, `cartografo` e `guardiao-de-reliquias`, e profissão `arquiteto` em `conhecimento`. No pacote ativo, `pericia` e `esquiva` usam `POINTS`: o teste padrão é d6; conforme a dificuldade, os dados são d4/d6/d8/d10. O máximo do motor por dado é, respectivamente, 3/4, 5/6, 7/8 e 9/10 faces de sucesso (75%, 83,3%, 87,5% e 90%), sempre preservando uma falha. O +6 do catálogo, sozinho e partindo de zero pontos, atinge o teto de d4/d6/d8; no d10 dá 7/10 faces, e pontos próprios da ficha podem levá-lo ao teto. Se futuramente houver uma meta de chance mais baixa, deve-se ajustar a dificuldade ou a regra de bônus com um objetivo declarado, não criar um limite arbitrário por origem.

## Teste de regressão e validação

Foi acrescentado a `CharacterCreationCatalogTest` um teste para garantir que os dois escopos profissionais incompatíveis permaneçam removidos e que o híbrido Sangue Lunar continue com `atletismo +1` e texto coerente. Em `PointCheckPackageTest`, um teste confirma que seis bônus externos com IDs distintos continuam somando `+6`, enquanto o d6 por pontos mantém só as faces 2–6 como sucesso.

- Verificação estática: JSON válido; 274 entradas auditadas; 272 efeitos declarados após as duas remoções; asserts das três correções passaram.
- `./gradlew :rules:test` — **passou** com JDK 17.
- `:app:ktlintMainSourceSetCheck`, `:app:ktlintTestSourceSetCheck` e `:rules:ktlintTestSourceSetCheck` — **passaram** nos arquivos afetados.
- `./gradlew ktlintCheck` — **bloqueado por violações preexistentes** de indentação em `engine/src/main/kotlin/dev/rpgsolo/engine/AeltharynWorld.kt`; o plano principal já registra esse problema, que é alheio às alterações desta rodada.
- `./gradlew :app:testDebugUnitTest` — **não iniciou**: Android SDK ausente (`ANDROID_HOME`/`sdk.dir` não configurado). O teste de regressão do app foi escrito, mas depende da execução no CI ou em ambiente com Android SDK.
- Testes em aparelho/emulador — **pulados a pedido**; não foram considerados aprovados. O push foi feito em branch fora dos gatilhos do workflow e nenhuma execução de CI/E2E foi iniciada.
- `git diff --check` — sem erros de whitespace após correção.

## Estado

A revisão de intenção, balanceamento e política de empilhamento foi concluída para as cinco categorias acima, sem adicionar teto global além da saturação por dado já existente no motor. Continuam pendentes o teste unitário do app (SDK ausente), a revisão de lore e retratos e a cobertura de falhas parciais de backup. Testes em aparelho/emulador foram pulados a pedido e não contam como validação. Não houve alteração das cópias históricas do catálogo usadas como referência de mundo.
