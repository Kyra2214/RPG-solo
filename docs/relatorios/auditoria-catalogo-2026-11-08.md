# Auditoria estática do catálogo de personagem — 2026-11-08

## Escopo

Esta etapa cobre somente a auditoria dos modelos, dados, carregamento, validação e telas atuais do catálogo de raças, sub-raças, classes e profissões. Não altera código, não cria novas regras e não gera lore ou retratos.

## Evidências encontradas

- O catálogo é carregado por `CharacterCreationCatalog` a partir de `packages/sistema-proprio/personagem-catalogo.json`.
- As escolhas canônicas ficam em `Ficha.editorialChoicesJson`, com migração/fallback legado no `AppModel`.
- A tela `CharactersScreen` já separa raça, sub-raça, classe principal, classes adicionais, profissão, híbrido e Classe de Malditos; a sub-raça é filtrada pela raça-pai.
- `validateChoices` confere IDs, vínculo entre sub-raça e raça, duplicidade de classes, limite de quatro classes e independência da profissão.
- `validationErrors` confere a estrutura do JSON, IDs únicos, referências de `parentRaceId`, campos obrigatórios e escopos de efeitos.
- Os efeitos selecionados passam por `selectedEffects`, `AppModel.selectedCatalogBonuses` e `CombatBonuses`; a origem é preservada como `ExternalBonus` e não é copiada silenciosamente para `Ficha.traits`.

## Contagem do arquivo canônico

| Categoria | Entradas | IDs únicos | Resumo/lore ausentes | Efeitos mecânicos não vazios |
|---|---:|---:|---:|---:|
| Raças | 50 | 50 | 0 | 50 |
| Sub-raças | 50 | 50 | 0 | 50 |
| Híbridos | 20 | 20 | 0 | 20 |
| Classes | 50 | 50 | 0 | 50 |
| Profissões | 104 | 104 | 0 | 104 |

Todas as sub-raças auditadas têm `parentRaceId` válido. Não foram encontrados IDs ausentes ou duplicados nas categorias verificadas.

## Lacunas confirmadas

1. `competencies` está vazio nas 50 classes e nas 104 profissões.
2. `traits` está vazio nas 50 classes e nas 104 profissões.
3. A documentação do anexo ainda descreve parte dos efeitos mecânicos como pendente, mas o JSON atual já declara um efeito por entrada. Essa divergência precisa ser resolvida por uma revisão posterior de conteúdo e escopo, não por inferência automática.
4. A auditoria foi estática; a execução Android, a edição de escolhas de fichas já criadas e a cobertura de falhas parciais de backup continuam sem validação nesta etapa.

## Decisão e execução da etapa seguinte

A auditoria de modelos, dados, carregador, validador e telas foi concluída. Foi aprovada a regra de que `competencies` registra capacidades práticas/narrativas e `traits` registra características próprias, sem conceder bônus automaticamente. O JSON foi preenchido para as 50 classes e 104 profissões: exatamente 2 competências e 2 traços originais por entrada, 616 frases no total, todas únicas. Nenhum bônus novo foi adicionado e os efeitos mecânicos continuam em campo separado.

## Validação

- Inspeção estática do código Kotlin e do JSON canônico.
- Contagens reproduzidas com Python 3 e comparação de IDs/referências.
- `./gradlew :rules:test` passou com JDK 17 em 8 segundos.
- A suíte Android completa continua dependente do Android SDK/CI, conforme `docs/VALIDACAO_CI.md`.

## Política A — equivalência e acumulação

Foi aprovada a política A: IDs distintos acumulam; somente a mesma declaração, identificada pelo mesmo `effectId`, é deduplicada. O resolvedor `selectedCatalogBonuses` deixou de escolher o maior efeito por escopo e passou a usar a deduplicação por identidade. Foram adicionados testes para acumulação de IDs distintos e remoção de duplicata com o mesmo ID.

## Auditoria de escopos mecânicos

Os `mechanicalEffects` usam 17 escopos: `adestramento`, `atletismo`, `conhecimento`, `cuidados`, `furtividade`, `intimidacao`, `lideranca`, `montaria`, `ocultismo`, `oficios`, `olhar-atento`, `percepcao`, `persuasao`, `pilotagem`, `pontaria`, `sistemas` e `sobrevivencia`. Todos existem como perícia ou escopo de combate no pacote próprio; a auditoria encontrou 0 inválidos.

Foi adicionado teste automático em `CharacterCreationCatalogTest` para validar os efeitos de todas as categorias. `:app:testDebugUnitTest` não pôde iniciar neste sandbox porque o Android SDK não está configurado (`ANDROID_HOME`/`sdk.dir` ausente). O bloqueio é ambiental e está registrado, não uma falha do teste.

## Lacuna seguinte concluída: intenção e balanceamento — 2026-11-08

Foram revisadas 274 entradas e seus 274 efeitos declarados nas categorias de raças, sub-raças, híbridos, classes e profissões. Duas declarações profissionais sem aderência ao escopo descrito foram removidas; a descrição de `humano-licantropo` foi corrigida para corresponder ao `atletismo +1` mantido. O catálogo ativo passa a ter 272 efeitos em 274 entradas. A regra final mantém o acúmulo por `effectId` distinto, sem teto global adicional: os testes `POINTS` saturam em `lados − 1` faces e preservam uma falha. A combinação de escolhas atual foi enumerada; o máximo por escopo é +6.

O relatório detalhado, com justificativas e validações, está em [`revisao-balanceamento-catalogo-2026-11-08.md`](revisao-balanceamento-catalogo-2026-11-08.md). `:rules:test` passou com JDK 17, incluindo regressão de seis fontes externas no d6; `:app:testDebugUnitTest` não iniciou por ausência de Android SDK e segue pendente para CI/ambiente Android.
