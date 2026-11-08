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

## Decisão desta etapa

A auditoria de modelos, dados, carregador, validador e telas foi concluída. A próxima pendência deve ser uma revisão de conteúdo/contrato das competências e traços de classes e profissões, começando por definir quais campos são obrigatórios e quais efeitos já existentes são válidos no motor. Nenhum bônus novo deve ser adicionado antes dessa definição.

## Validação

- Inspeção estática do código Kotlin e do JSON canônico.
- Contagens reproduzidas com Python 3 e comparação de IDs/referências.
- Nenhum build ou teste foi executado nesta etapa; a validação JVM anterior está registrada em `docs/VALIDACAO_CI.md`.
