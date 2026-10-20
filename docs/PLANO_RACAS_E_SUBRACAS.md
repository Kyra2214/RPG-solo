# Plano de implementação — Raças e sub-raças do RPG Solo

**Estado:** planejamento iniciado; ainda não declara catálogo, interface ou mecânicas implementados.  
**Branch de trabalho:** `feature/racas-subracas`  
**Referência criativa:** Dungeons & Dragons (D&D), sem copiar texto de livros, nomes de cenários protegidos ou blocos de regras proprietários. O conteúdo narrativo do RPG Solo será original; D&D serve como inspiração de fantasia e como referência de categorias, não como texto a reproduzir.

## 1. Objetivo

Permitir que a criação de personagem ofereça raças e sub-raças por catálogo de dados expansível. Cada opção precisa ser identificável, explicada e, quando o pacote de regras ativo suportar, produzir efeitos mecânicos reais na ficha. A raça não pode ser apenas um rótulo visual.

**Requisito de experiência:** ao selecionar uma raça, a tela mostra sua lore. Ao selecionar uma sub-raça, mostra também a lore específica dela, sem perder a descrição da raça-base. A lore deve ser consultável novamente depois da seleção e durante a visualização/edição da ficha.

## 2. Escopo e metas de catálogo

As quantidades abaixo são metas editoriais, não conteúdo já existente no aplicativo:

| Coleção | Meta inicial |
|---|---:|
| Raças principais de fantasia | 10–15 |
| Raças adicionais de fantasia | 30–50 |
| Variantes, sub-raças e opções avançadas | Expansível |
| Catálogo total possível | 50+ opções de raça, sem contar todas as variantes |

O sistema não terá limite fixo de raças ou sub-raças. Novas opções devem poder ser adicionadas aos arquivos de dados sem reescrever a lógica da interface.

## 3. Vocabulário e hierarquia

- **Raça:** opção principal de personagem, com identidade, lore e traços comuns.
- **Sub-raça:** variante ligada a uma raça-base; herda sua identidade e seus traços compartilhados e pode acrescentar lore e traços próprios.
- **Variante/opção avançada:** extensão opcional, quando um pacote ou conteúdo futuro precisar representar diferenças que não se encaixam como sub-raça.
- **Traço:** descrição de uma característica narrativa ou regra associada.
- **Efeito mecânico:** efeito tipado e validável pelo motor. Texto descritivo, sozinho, não pode alterar números ou resultados.
- **Lore:** conteúdo narrativo original sobre origem, cultura, aparência, costumes, visão de mundo e diversidade interna. Não é regra mecânica.

Evitar pressupor que toda raça precisa de sub-raças. Uma raça pode ter zero, uma ou várias.

## 4. Modelo de dados proposto

Cada registro de raça deve incluir, no mínimo:

- `id`: identificador estável, único e independente do nome traduzido.
- `name`: nome de exibição.
- `summary`: resumo curto para a lista.
- `lore`: texto original completo para o painel de detalhes.
- `traits`: traços compartilhados, com IDs estáveis.
- `subraces`: referências/IDs das sub-raças disponíveis.
- `tags`: classificação para busca e filtros.
- `source`: origem editorial e nota de licença/proveniência.
- `version`: versão do conteúdo.

Cada sub-raça deve incluir:

- `id` único e estável;
- `parentRaceId` obrigatório;
- `name`, `summary` e `lore` próprios;
- `traits` adicionais ou substituições explícitas, sem duplicar silenciosamente os traços herdados;
- `source` e `version`.

Os traços devem separar claramente texto narrativo e mecânica:

- `id`, `name`, `description`;
- `mechanicalEffects` tipados e opcionais;
- requisitos, limites, gatilhos e usos, quando aplicáveis;
- indicação explícita quando o traço é apenas narrativo ou ainda não tem implementação mecânica.

Não usar efeitos livres em texto para executar regras. Efeitos desconhecidos ou incompatíveis com o pacote ativo devem ser sinalizados e não aplicados silenciosamente.

## 5. Regras de herança e validação

1. Uma sub-raça referencia exatamente uma raça-base existente.
2. A ficha registra os IDs escolhidos, não só os nomes exibidos.
3. Os traços compartilhados da raça-base e os traços próprios da sub-raça são combinados por uma regra explícita e determinística.
4. Se uma sub-raça substituir um traço-base, a substituição deve ser declarada por ID; não pode gerar duplicação ou acumular efeitos por acidente.
5. IDs duplicados, referências quebradas, ciclos, efeitos inválidos e sub-raças sem raça-base devem reprovar a validação do catálogo.
6. Alterar nome ou lore não deve quebrar fichas salvas; IDs existentes não devem ser reutilizados para outra opção.
7. Se o pacote de regras ativo não der suporte a um efeito, a interface deve informar a limitação e manter o efeito sem aplicação mecânica — nunca inventar uma regra oficial.
8. O catálogo de fantasia é independente de sistema. O pacote ativo decide como traços mecânicos são validados e aplicados.

## 6. Fluxo de criação de personagem

1. Usuário abre a etapa **Raça**.
2. A lista mostra nome, resumo e filtros; catálogo vazio ou inválido apresenta erro claro.
3. Ao selecionar uma raça, abrir/atualizar o painel com nome e lore completa.
4. Se houver sub-raças, listar apenas as vinculadas à raça selecionada.
5. Ao selecionar uma sub-raça, exibir a lore dela e manter acessível a lore da raça-base.
6. Antes de confirmar, mostrar resumo da escolha e os traços herdados/adicionais, diferenciando descrição narrativa de efeito mecânico reconhecido.
7. Validar compatibilidade com o pacote ativo e só então salvar os IDs na ficha.
8. Na ficha salva, permitir consultar raça, sub-raça, lore e traços sem reiniciar a criação.
9. Se o jogador mudar a raça depois, recalcular os traços derivados sem apagar atributos ou escolhas independentes; pedir confirmação se a mudança invalidar opções já selecionadas.

A lore exibida é conteúdo do catálogo e não depende de chamada à IA. A IA poderá narrar a partir dela depois, mas não deve reescrevê-la como se fosse a fonte canônica.

## 7. Catálogo editorial: ordem sugerida

### Etapa A — núcleo de fantasia (meta: 10–15 raças)

Começar pelas opções reconhecíveis de fantasia, por exemplo: humanos, elfos, anões, halflings, gnomos, orcs, meio-orcs, draconatos, tieflings, seres feéricos e opções de linhagem elemental ou celestial. A lista final será revisada para evitar duplicações conceituais e para manter o conteúdo original.

### Etapa B — sub-raças e variantes

Criar variantes quando elas representarem diferenças culturais, geográficas, mágicas ou biológicas relevantes. Cada uma terá lore própria e uma relação explícita com a raça-base. Não criar variantes só para inflar a contagem.

### Etapa C — catálogo expandido (meta: 30–50 raças adicionais)

Adicionar opções menos comuns, povos híbridos e linhagens fantásticas originais, incluindo opções sombrias, feéricas, extraplanares, aquáticas, subterrâneas e artificiais. O catálogo deve aceitar novas famílias sem alteração de código.

### Etapa D — opções avançadas

Deixar extensões para variantes de cenário, linhagens, heranças e pacotes comunitários. Marcar claramente conteúdo original, adaptação e conteúdo importado pelo usuário.

Esses grupos são metas de planejamento; ainda não significam que os registros, lores ou efeitos estejam cadastrados.

## 8. Licença e originalidade

D&D é inspiração, não autorização para copiar. Não embutir lore textual de livros, ilustrações, tabelas ou blocos de regras protegidos. Escrever lore própria, usar nomes e conceitos com avaliação adequada de direitos e registrar proveniência de cada entrada. Quando uma opção for importada pelo usuário, manter a origem identificada e não redistribuir esse material como conteúdo nativo do app.

## 9. Fases de implementação

### Fase R1 — auditoria do código existente
- Localizar os modelos de ficha, `CharacterDraft`, pacote de regras e telas de criação.
- Identificar onde conceitos de raça já são armazenados ou apresentados.
- Confirmar persistência, edição de ficha e compatibilidade com backups.
- Reutilizar estruturas existentes; não criar um segundo sistema de personagens paralelo.

### Fase R2 — esquema e catálogo
- Definir modelos Kotlin e formato de dados versionado.
- Implementar carregamento, IDs estáveis, referências de sub-raça e validação.
- Criar o núcleo inicial de raças com lore original.
- Manter o catálogo separado dos pacotes de regras e do cenário da campanha.

### Fase R3 — traços e efeitos mecânicos
- Separar lore/descrição de efeitos tipados.
- Integrar os efeitos aceitos ao validador e ao motor, sem lógica específica de uma raça espalhada pela interface.
- Declarar efeitos não suportados sem fingir que foram aplicados.
- Definir herança e substituição de traços da raça-base pela sub-raça.

### Fase R4 — interface de criação
- Adicionar seleção pesquisável de raça e sub-raça.
- Exibir lore da raça assim que ela for selecionada.
- Exibir lore específica da sub-raça assim que ela for selecionada.
- Mostrar traços herdados, traços próprios e efeitos mecânicos separadamente.
- Permitir rever lore e escolhas na ficha final.

### Fase R5 — persistência e compatibilidade
- Salvar IDs de raça/sub-raça na ficha e nos backups.
- Garantir que fichas antigas sem raça continuem abrindo.
- Definir comportamento para opção removida/descontinuada: preservar o registro histórico e apresentar aviso, sem apagar a ficha.
- Verificar edição da ficha e troca de pacote sem perda de dados.

### Fase R6 — expansão editorial
- Aumentar o catálogo por lotes, com lore original para cada raça e sub-raça.
- Revisar coerência, diversidade interna, duplicações e balanceamento.
- Registrar versão, proveniência e estado de implementação dos efeitos.

## 10. Critérios de conclusão

- Catálogo sem limite fixo, carregado de dados.
- Cada raça e sub-raça tem ID único, resumo e lore própria.
- A lore aparece ao selecionar cada opção e pode ser consultada na ficha salva.
- Sub-raças herdam corretamente os traços-base e acrescentam/substituem apenas o que declaram.
- A ficha salva e restaurada preserva as escolhas por ID.
- Efeitos mecânicos reconhecidos são aplicados pelo motor; descrições não suportadas ficam explicitamente identificadas.
- Conteúdo inválido gera erro claro, sem corromper a criação.
- O catálogo pode crescer para 50+ raças sem mudança estrutural da interface.
- Testes/builds e validação em aparelho serão executados numa etapa posterior, conforme a regra de trabalho atual. Este plano não declara essas verificações aprovadas.

## 11. Estado inicial

- [x] Criar branch dedicada para o trabalho de raças/sub-raças.
- [ ] Auditar os modelos e a tela de criação existentes.
- [ ] Confirmar o esquema de dados e integração com o pacote ativo.
- [ ] Implementar catálogo e validação.
- [ ] Implementar seleção e exibição de lore.
- [ ] Integrar efeitos mecânicos tipados.
- [ ] Persistir escolhas e garantir compatibilidade com fichas antigas.
- [ ] Expandir o catálogo para as metas editoriais.

**Próxima ação de código:** concluir a auditoria dos modelos e telas existentes antes de alterar a ficha, para integrar o catálogo ao fluxo atual em vez de duplicar funcionalidades.
