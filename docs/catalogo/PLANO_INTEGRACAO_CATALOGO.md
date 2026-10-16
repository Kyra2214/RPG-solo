# PLANO_INTEGRACAO_CATALOGO.md

## Arquitetura existente (auditada)
App Android sem servidor, módulos `app`, `engine`, `data`, `ai`, `rules`. O `engine` é independente de sistema (testado). O módulo `rules` estava vazio e passou a conter o catálogo.

## Decisões
1. **Catálogo como dados, não código.** Três arquivos de texto (`estilos.txt`, `sistemas.txt`, `compat.txt`). Adicionar um estilo ou sistema = adicionar uma linha, sem recompilar a lógica.
2. **Famílias e edições separadas.** Cada edição é um `SystemEdition` com sua família; nada é compartilhado entre famílias (3D&T e Tormenta20 são famílias distintas).
3. **Compatibilidade conservadora.** Combinação sem regra = `UNVERIFIED`; o jogador vê avisos antes de começar.
4. **Carga atômica.** `CatalogHolder` só troca o catálogo se a nova carga for válida. Arquivo ruim não corrompe nada.
5. **Dificuldade fechada.** Só 4 parâmetros documentados. Não existe parâmetro que mude atributo de NPC.
6. **Limites de conteúdo independentes** de gênero, dificuldade e regras.
7. **Privacidade de NPC (seção 11 do documento).** O documento pede proteção no backend. **Decisão já tomada com o dono do projeto:** o app é local e sem servidor, então os NPCs ficam no mesmo banco, sem tela de ficha e com contexto enxuto para a IA. Esta divergência fica registrada aqui.

## Arquivos criados ou alterados
- `rules/src/main/kotlin/.../Catalog.kt`, `CatalogParser.kt`, `CampaignConfig.kt`
- `rules/src/main/resources/catalog/estilos.txt` (153 entradas), `sistemas.txt` (37 entradas), `compat.txt` (3 regras)
- `rules/src/test/kotlin/.../CatalogTest.kt` (22 testes)
- `docs/catalogo/*.md` (5 documentos) e `docs/PLANO_DESENVOLVIMENTO.md` (nova Fase 4A)
- `rules/build.gradle.kts` (removido `RulesModule.kt` vazio)

## Testes
- Executados com kotlinc 2.0.21 e mini-JUnit local: **22 do catálogo + 28 do motor passaram**. Confirmar com `./gradlew :rules:test :engine:test` (JUnit real).
- No Android, os arquivos do catálogo devem ser lidos de `assets` e passados como texto ao `CatalogLoader.load`.

## O que NÃO foi feito (não declarar como pronto)
- Geração de configuração a partir de **descrição livre** (precisa da camada de IA, Fase 6).
- **Esquemas de ficha** por sistema e validação de pré-requisitos (Fase 4, depois da pesquisa de cada sistema).
- **Ficha reutilizável** entre campanhas sem vazar estado (Fase 5).
- Pesquisa de criação de personagem de todos os sistemas exceto D&D.
- Leitura das fontes pendentes e dos textos das licenças.

## Próximas etapas
1. Escolher o primeiro sistema real e pesquisá-lo a fundo (criação, opções, cálculos, licença).
2. Montar o pacote de regras dele a partir de fonte com licença verificada.
3. Implementar o esquema de ficha e a validação de pré-requisitos (Fase 4/5).
4. Conectar a descrição livre à configuração (Fase 6).
