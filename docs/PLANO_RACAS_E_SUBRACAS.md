# Plano de implementação — Raças, sub-raças, classes e profissões do RPG Solo

**Estado atual (20/10/2026):** catálogo editorial, seleção de raça/sub-raça/híbrido/classe/profissão/maldições, validação de escolhas, persistência editorial, importação/exportação e edição das escolhas parcialmente implementados. Efeitos mecânicos do catálogo, história e retrato por IA ainda pendentes. Build/testes/CI não executados.  
**Branch:** `feature/racas-subracas`  
**Inspiração:** D&D como referência criativa de fantasia, sem copiar lore, ilustrações ou blocos de regras protegidos. O RPG Solo terá conteúdo original e motor próprio.

## 1. Objetivo

A criação de personagem combina escolhas independentes: **raça**, **sub-raça** (quando existir), **classe**, **profissão** e, quando fizer sentido, **especialização profissional**. São três pilares distintos — raça, classe e profissão — e o jogador pode escolher cada um separadamente. Não deve ser obrigado a selecionar uma profissão relacionada à classe, nem uma classe relacionada à raça. Cada escolha tem identidade, lore/descrição e traços próprios. Os efeitos mecânicos válidos são acumulados pela ficha e resolvidos pelo motor.

Exemplo de design com três escolhas:
- Raça **Elfo**: concede Precisão +1.
- Classe **Arqueiro**: define habilidades e técnicas de combate à distância.
- Profissão **Perito em Arcos**: representa conhecimento técnico de arcos e bestas e pode conceder bônus específicos de fabricação, manutenção ou uso, conforme as regras definidas.

Outro exemplo, combinando opções sem vínculo obrigatório entre elas:
- Raça **Anão**.
- Classe **Arqueiro**.
- Profissão **Especialista em Joias**: lore explicando o trabalho com gemas e metais preciosos, técnicas de lapidação e montagem; essa profissão pode permitir criar ou aprimorar armas e equipamentos com joias, com bônus definidos e limitados pelo sistema.

Os nomes e bônus são exemplos de design, ainda não regras implementadas ou balanceadas. O motor deverá aplicar apenas os efeitos definidos para cada opção e mostrar de qual escolha cada bônus veio.

## 2. Conceitos separados

- **Raça:** identidade de povo/linhagem, lore, traços raciais comuns.
- **Sub-raça:** variante ligada a uma raça-base, com lore própria e traços adicionais ou substituições declaradas.
- **Classe:** papel ou treinamento de aventura — por exemplo, Arqueiro, Guerreiro, Mago ou Explorador — com habilidades e regras próprias.
- **Profissão:** ofício, ocupação ou conhecimento especializado — por exemplo, Especialista em Joias, Ferreiro, Alquimista ou Perito em Arcos — com lore, competências e efeitos próprios, especialmente em fabricação, manutenção, comércio ou conhecimento técnico.
- **Especialização profissional:** aprofundamento opcional de uma profissão, caso seja necessário; não é um quarto pilar obrigatório e não deve ser confundida com a classe.
- **Traço:** característica descritiva que pode ter efeito mecânico tipado ou ser apenas narrativa.
- **Efeito mecânico:** modificação estruturada, validada pelo motor e com escopo explícito (por exemplo, bônus de dano apenas com arcos e bestas).
- **Lore:** conteúdo narrativo original. A lore não é executável e não deve ser confundida com regra.

Raça, classe e profissão são seleções independentes. Um anão pode ser Arqueiro e Especialista em Joias; um elfo pode ser Guerreiro e Ferreiro. Só haverá restrições quando forem explicitamente definidas pelo pacote de regras ativo — nunca por uma associação presumida no código.

## 3. Metas do catálogo

Metas editoriais mínimas para o catálogo, não contagens já implementadas:

| Coleção | Meta |
|---|---:|
| Raças (somando núcleo e adicionais) | 50+ |
| Sub-raças e variantes | 50+ |
| Classes | 50+ |
| Profissões | 50+ |
| Especializações profissionais | Expansível; não substitui a meta de profissões |
| Catálogo potencial | Pelo menos 50 opções em cada categoria principal; expansível |

O catálogo não terá limite fixo. As metas são independentes: 50+ raças, 50+ sub-raças/variantes, 50+ classes e 50+ profissões. Não contar uma sub-raça como se fosse uma raça-base nem uma especialização como se fosse uma profissão. Adicionar entradas deve ser uma operação de dados, não uma mudança de código de interface por opção. As opções precisam ter diferenças reais de lore, competências e/ou efeitos; não inflar o catálogo com nomes duplicados ou variações cosméticas.

## 4. Dados e identificadores

Cada raça deve ter `id` estável, `name`, `summary`, `lore`, traços, tags, proveniência e versão. Uma sub-raça também exige `parentRaceId`, lore própria e definição explícita de traços herdados, adicionais ou substituídos.

Cada classe deve ter:
- `id` estável e nome de exibição;
- resumo e descrição/lore própria;
- requisitos e compatibilidades opcionais, definidos por dados;
- competências e traços concedidos;
- efeitos mecânicos tipados, com escopo de aplicação;
- proveniência e versão.

Cada profissão deve ter seu próprio ID e catálogo, sem depender da classe escolhida. Uma especialização profissional, se usada, deve referenciar uma profissão válida e definir o que acrescenta ou substitui.

Os traços devem separar `description` de `mechanicalEffects`. Efeitos estruturados devem expressar tipo, valor, escopo, requisitos, gatilhos, limites e duração quando aplicável. Exemplo conceitual: `damage_bonus +1` com escopo `weapon_group: bows_and_crossbows`; não um bônus universal de dano. O esquema concreto será alinhado às capacidades reais do motor após a auditoria.

## 5. Combinação dos efeitos

A ficha deve guardar separadamente as escolhas e suas fontes:
1. traços da raça;
2. traços da sub-raça;
3. traços da classe;
4. traços da profissão;
5. traços da especialização profissional, se houver;
6. vantagens, perícias, poderes e regras adicionais do pacote ativo.

O motor calcula os efeitos aplicáveis a cada teste ou ação a partir dessas fontes. Não se deve somar cegamente efeitos que não se aplicam à ação atual. Bônus limitados a armas, situações, usos ou gatilhos só funcionam dentro do escopo declarado.

Regras de segurança:
- IDs únicos e referências válidas;
- sem duplicação acidental de traços herdados;
- substituições explícitas por ID;
- efeitos desconhecidos/incompatíveis são sinalizados e não aplicados silenciosamente;
- alterações de raça/profissão recalculam efeitos derivados, sem apagar escolhas independentes sem confirmação;
- a ficha e os logs permitem explicar a origem de cada bônus;
- nenhuma mecânica de um sistema externo é apresentada como regra oficial do pacote próprio.

## 6. Lore e fluxo de criação

### Geração narrativa e retrato com IA

Após definir e validar a ficha-base, o jogador poderá pedir à IA para criar a lore do personagem e uma imagem original para salvar. Isso é uma etapa narrativa opcional da criação, não um mecanismo de criação de regras.

Modos previstos:
- **Desenvolver uma ideia do jogador:** o jogador fornece um conceito, passado ou premissa; a IA desenvolve uma história coerente com a ficha e a campanha.
- **Criar do zero:** o jogador autoriza a IA a propor uma história original compatível com as escolhas já confirmadas.

Contexto permitido para a geração: raça/sub-raça e sua lore canônica, classe e sua descrição, profissão e sua lore, vantagens e desvantagens escolhidas, aparência fornecida pelo jogador, ideia inicial e ambientação da campanha. A IA deve respeitar os dados canônicos e não substituir a lore oficial dos catálogos.

A imagem do personagem será gerada separadamente da lore, com base na ficha e na descrição narrativa aprovada. O jogador poderá salvar o retrato e consultar a história na ficha. A lore e a imagem poderão ser regeneradas independentemente, sem alterar escolhas mecânicas já confirmadas.

### Limites rígidos da IA

- A IA narrativa **não cria, concede, remove, altera nem balanceia bônus ou regras**.
- A IA só pode referenciar vantagens e desvantagens válidas e já escolhidas; não pode adicioná-las por conta própria.
- A IA não inventa atributos, perícias, poderes, habilidades, proficiências, requisitos ou efeitos de itens.
- Uma informação narrativa — por exemplo, ter crescido numa guilda, conhecer um ferreiro ou ter encontrado uma joia — não concede automaticamente competência, equipamento, vantagem ou bônus mecânico.
- Imagens podem representar apenas equipamentos e características coerentes com a ficha e com as escolhas aprovadas; a imagem não cria itens nem altera o inventário.
- A saída narrativa fica separada dos dados mecânicos estruturados. Somente o motor de regras pode calcular e aplicar efeitos válidos.
- A IA poderá sugerir ganchos narrativos, rivais, relações, segredos e objetivos pessoais, mas esses elementos só passam a integrar o estado persistente da campanha por meio dos fluxos de jogo autorizados.

### Ficha-base e modificadores derivados

A ficha-base registra separadamente as escolhas confirmadas de:
1. raça ou sub-raça;
2. classe;
3. profissão;
4. vantagens;
5. desvantagens.

Essa ficha-base não deve ser confundida com modificadores derivados de equipamentos, buffs, debuffs ou outros efeitos temporários. Esses elementos pertencem às suas próprias fontes e camadas; o motor determina quando se aplicam, seu escopo, duração, acúmulo e remoção. A geração de lore e imagem não modifica nenhuma dessas camadas.


1. Selecionar raça; exibir imediatamente resumo e lore completa.
2. Se houver sub-raças, listar apenas as ligadas à raça selecionada. Ao selecionar uma, exibir sua lore específica e manter a lore-base acessível.
3. Selecionar classe; exibir descrição, lore e habilidades/efeitos.
4. Selecionar profissão de forma independente da raça e da classe; exibir lore, competências e efeitos profissionais.
5. Selecionar especialização profissional apenas quando disponível e compatível.
6. Mostrar uma revisão final: escolhas, lore consultável, traços herdados e próprios, efeitos reconhecidos, escopos e avisos de compatibilidade.
7. Confirmar e gravar IDs estáveis na ficha.
8. Na ficha salva, permitir consultar as lores e a origem dos bônus.
9. Se uma escolha for alterada, recalcular os efeitos derivados e pedir confirmação quando outra escolha se tornar incompatível.

A lore canônica vem do catálogo de dados e funciona offline; a IA pode usá-la na narração, mas não a substitui nem altera a mecânica.

## 7. Ordem editorial do catálogo

### Raças
Construir um catálogo de pelo menos 50 raças-base, com lore original, e pelo menos 50 sub-raças/variantes com identidade própria. As diferenças entre variantes devem ser significativas — culturais, geográficas, mágicas ou biológicas — e não apenas cosméticas para atingir a meta.

### Classes
Planejar pelo menos 50 classes com funções e mecânicas distintas, cobrindo combate corpo a corpo, armas à distância, defesa, furtividade, exploração, conhecimento, suporte e magia quando suportada pelo pacote. Classes como Arqueiro definem o papel e as habilidades de aventura. O catálogo deve ser desenvolvido por grupos e balanceado, evitando opções duplicadas com nomes diferentes.

### Profissões
Planejar pelo menos 50 profissões distintas, em catálogo separado de classes e sem acoplamento obrigatório a raça ou classe: Ferreiro, Especialista em Joias, Alquimista, Perito em Arcos e outras. Cada profissão terá lore própria, competências e efeitos contextualizados; o catálogo deve abranger ofícios, artes, ciência, comércio, exploração e outros conhecimentos, sem criar opções meramente repetidas para alcançar a meta. Por exemplo, Especialista em Joias pode criar ou aprimorar equipamentos com gemas, se essas mecânicas existirem no motor.

### Especializações profissionais
Tratar como camada opcional de refinamento de uma profissão. Não transformar a profissão em classe nem limitar a profissão pelo papel de combate.

## 8. Licença e originalidade

D&D é inspiração, não autorização para copiar. Não embutir textos de lore, ilustrações, tabelas ou blocos de regras protegidos. Escrever conteúdo original, registrar proveniência e identificar conteúdo importado pelo usuário, sem redistribuí-lo como nativo.

## 9. Fases de implementação

### R1 — Auditoria e compatibilidade
- Localizar modelos da ficha, `CharacterDraft`, pacotes de regras, criação de personagem, persistência e backups.
- Identificar os atributos, perícias, armas, bônus tipados e validações já suportados.
- Confirmar como a ficha guarda traços e como o motor aplica efeitos.
- Reutilizar o fluxo existente; não criar um segundo sistema de personagens.

### R2.1 — Contrato de efeitos tipados (em andamento)

- [x] Criar modelo explícito `scope`, `bonus` e `source` no catálogo de criação.
- [x] Ler efeitos apenas de campos `mechanicalEffects` declarados; nunca inferir bônus a partir de lore/descrição.
- [x] Validar a estrutura dos efeitos, os limites numéricos e a compatibilidade dos escopos com o pacote ativo.
- [x] Restringir os escopos aceitos aos que o resolvedor reconhece: atributos, testes, perícias e escopos de combate; IDs de vantagens/poderes não são tratados como escopos executáveis por si só.
- [x] Exibir os efeitos declarados e sua origem sem afirmar que já estão aplicados.
- [x] Criar `selectedEffects(choices)` para retornar somente efeitos explicitamente declarados nas escolhas, preservando ID estável da entrada e origem (entrada/benefício/custo).
- [x] Manter efeitos de fontes diferentes separados, sem deduplicação silenciosa; o catálogo não decide a política de acúmulo.
- [x] Integrar efeitos externos tipados à resolução do teste de fuga sem copiar os bônus para `Ficha.traits`.
- [x] Aplicar efeitos de combate declarados nos escopos `ataque`, `dano`, `defesa` e `resistencia_dano` aos cálculos correspondentes do personagem jogador, sem transferi-los aos NPCs.
- [x] Preservar a origem nos objetos `ExternalBonus` durante a resolução de testes; efeitos externos são somados separadamente dos `TraitBonus` canônicos.
- [ ] Exibir a origem de cada efeito aplicado no registro/descrição da rolagem e no combate.
- [ ] Integrar todos os caminhos de testes e ações, não apenas o teste de fuga atualmente ajustado pelo `AppModel`.
- [ ] Garantir que efeitos de raça, sub-raça, híbrido, classe, profissão e maldição não sejam aplicados em duplicidade quando compartilham escopo.
- [ ] Definir tratamento de efeitos contextuais (arma, gatilho, duração, condição) antes de habilitar qualquer efeito desse tipo.
- [ ] Adicionar testes de integração depois da conclusão do código, em etapa separada de validação.

**Limite atual:** o motor já resolve bônus de traços do pacote via `TraitBonus`, mas as escolhas do catálogo editorial ainda são persistidas separadamente e não são convertidas em traços canônicos. Por isso, os efeitos declarados no catálogo continuam inativos. Não habilitar bônus até a integração abaixo estar completa.

### R2 — Esquema de dados e catálogo
- [x] Criar primeiro catálogo editorial versionado em `rules/src/main/resources/packages/sistema-proprio/personagem-catalogo.json`.
- [x] Cadastrar 50 raças, 50 sub-raças/variantes, 50 classes e 103 profissões com IDs estáveis, resumo e lore/descrição originais.
- [x] Referenciar cada sub-raça à sua raça-base por `parentRaceId`.
- [x] Deixar `mechanicalEffects` vazio em todas as opções; nenhum bônus foi inventado ou concedido por lore.
- [x] Implementar carregador e validação estrutural de IDs, referências e esquema; falhas do catálogo são exibidas na interface.
- [x] Carregar o catálogo versionado na criação e persistir escolhas editoriais por ID da ficha; a integração mecânica com a ficha/motor permanece pendente.
- [ ] Completar campos editoriais de competências e traços depois da auditoria das mecânicas existentes.

Este primeiro lote é conteúdo versionado e conectado parcialmente ao runtime editorial da criação. As entradas não concedem efeitos mecânicos até que regras válidas sejam explicitamente definidas e suportadas pelo motor.

### R3 — Efeitos combinados
- Formalizar os efeitos tipados que o motor realmente consegue executar.
- Definir escopos para bônus raciais e profissionais.
- Combinar efeitos de fontes diferentes sem duplicação nem aplicação fora do escopo.
- Sinalizar efeitos não suportados; não fingir que funcionam.

### R4 — Interface
- [x] Implementar seleção de raça/sub-raça com lore visível.
- [x] Implementar seleção de classe com descrição/lore visível; lista de habilidades mecânicas do catálogo ainda pendente.
- [x] Implementar seleção independente de profissão com lore visível; competências e efeitos mecânicos tipados ainda pendentes.
- Adicionar especialização profissional apenas quando o esquema estiver definido.
- [x] Exibir revisão das escolhas e separar explicitamente os traços mecânicos atuais das descrições editoriais; origem/escopo de efeitos raciais e profissionais aguarda implementação mecânica.

### R4A — Criação narrativa assistida por IA
- Definir entrada estruturada e controlada para a IA com dados confirmados da ficha e contexto da campanha.
- Oferecer modo de desenvolver a ideia do jogador e modo de criar lore do zero.
- Gerar lore original coerente com raça/sub-raça, classe, profissão, vantagens e desvantagens selecionadas.
- Gerar retrato original em uma etapa separada, usando a ficha e a descrição aprovada.
- Permitir salvar/consultar lore e imagem na ficha e regenerá-las separadamente.
- Validar que a resposta narrativa não altere dados mecânicos, escolhas, inventário ou estado da campanha.
- Manter a funcionalidade de consulta da lore canônica offline; geração por IA/imagem depende dos serviços disponíveis e não deve ser apresentada como recurso offline.

- Implementar seleção de raça/sub-raça com lore visível.
- Implementar seleção de classe com descrição e habilidades visíveis.
- Implementar seleção de profissão independente, com lore, competências e efeitos visíveis.
- Adicionar especialização profissional apenas quando o esquema estiver definido.
- Exibir resumo de bônus e sua origem antes da confirmação.

### R5 — Persistência
- [x] Guardar IDs escolhidos por ficha e incluir escolhas editoriais em backups e exportação/importação individual versionada; unificação no JSON canônico da ficha ainda pendente.
- [x] Preservar compatibilidade com fichas/backups antigos sem essas escolhas.
- [x] Backups/restaurações preservam as escolhas editoriais; efeitos derivados ainda não existem para essas opções e não são duplicados.

### R6 — Expansão editorial
- Atingir pelo menos 50 raças, 50 sub-raças/variantes, 50 classes e 50 profissões, ampliando depois conforme necessário.
- Criar lore original para cada opção.
- Revisar coerência, balanceamento, sobreposições e compatibilidade.
- Versionar conteúdo e documentar efeitos pendentes.

## 10. Critérios de conclusão

- A ficha-base registra separadamente raça/sub-raça, classe, profissão, vantagens e desvantagens.
- Lore gerada por IA é opcional, coerente com os dados confirmados e armazenada separadamente das regras mecânicas.
- O jogador pode fornecer uma premissa ou autorizar a criação de lore do zero.
- A imagem gerada representa o personagem e pode ser salva sem alterar ficha, inventário ou regras.
- A IA narrativa não concede bônus, vantagens, desvantagens, atributos, habilidades, itens ou outros efeitos mecânicos.
- Equipamentos, buffs, debuffs e demais modificadores são calculados separadamente pelo motor de regras.

- Raças, sub-raças, classes, profissões e especializações profissionais são dados expansíveis, sem limite codificado; metas mínimas: 50+ em cada uma das quatro categorias principais (raças, sub-raças/variantes, classes e profissões).
- Toda raça/sub-raça tem lore própria e a interface a mostra ao selecionar.
- Toda classe tem descrição e lista explícita de habilidades/efeitos; toda profissão tem lore, competências e efeitos próprios.
- A ficha conserva as escolhas por IDs estáveis e permite rever as lores.
- Efeitos raciais, de classe e profissionais são acumulados corretamente, respeitando escopo e regras do pacote.
- A combinação Anão + Arqueiro + Especialista em Joias é válida sem vínculo predefinido; cada escolha contribui apenas com seus efeitos próprios.
- Alterar uma escolha recalcula efeitos derivados sem apagar dados independentes silenciosamente.
- Catálogo inválido produz erro claro e não corrompe a ficha.
- Build/testes/aparelho só serão declarados validados após execução explícita; esta etapa é planejamento, não aprovação de validação.

## 11. Estado

- [x] Criar branch dedicada.
- [x] Registrar plano inicial de raças/sub-raças.
- [x] Ampliar plano para incluir profissão/classe e composição de efeitos.
- [ ] Auditar modelos e telas atuais.
- [ ] Confirmar esquema compatível com o motor.
- [ ] Separar formalmente os modelos de classe e profissão.
- [x] Criar o primeiro lote editorial: 50 raças, 50 sub-raças/variantes, 50 classes e 103 profissões.
- [x] Registrar lore/descrições originais e IDs estáveis para o primeiro lote.
- [ ] Implementar carregador, validação do catálogo e integração ao runtime.
- [ ] Integrar efeitos tipados à ficha e às ações.
- [x] Implementar interface inicial de criação e persistência editorial; integração mecânica e edição unificada continuam pendentes.
- [ ] Implementar geração opcional de lore e retrato com IA, com limites rígidos contra alterações mecânicas.
- [ ] Expandir e balancear o catálogo.

**Estado atual:** catálogo editorial versionado com 50 raças, 50 sub-raças/variantes, 50 classes, 103 profissões, 100 maldições e 20 híbridos. A UI lê o catálogo, permite seleção/validação e persiste metadados de escolhas; a aplicação de efeitos mecânicos ainda não foi integrada. Importação/exportação e backups preservam as escolhas. Build, testes e CI não foram executados.

**Próxima ação:** concluir o contrato de persistência canônica e auditar o motor de bônus tipados antes de definir como converter traços raciais/profissionais em efeitos executáveis, sem duplicar os traços já escolhidos na ficha.

## 10. Maldições, benefícios e custos narrativos

O catálogo editorial inicial contém 100 maldições em `curses`, cada uma com ID estável, nome, resumo, lore, benefício, desvantagem, consequências narrativas e versão. As maldições são independentes de raça/sub-raça, classe e profissão e podem coexistir com vantagens e desvantagens da ficha.

Princípios obrigatórios:
- Uma maldição pode conceder um benefício e impor um custo ao mesmo tempo.
- Benefícios podem ser numéricos ou condicionais; custos podem ser mecânicos, comportamentais ou sociais.
- Uma consequência narrativa não se transforma automaticamente em penalidade numérica. Ex.: má reputação entre elfos não concede penalidade de Persuasão sem uma regra mecânica explícita.
- A maldição da espada pode conceder +1 de dano com armas e impor compulsão de tentar obter armas raras; a compulsão deve gerar escolhas e conflitos, não retirar o controle do jogador de forma arbitrária.
- Os efeitos descritos no primeiro lote são conteúdo editorial para revisão e balanceamento. `mechanicalEffects` permanece vazio, portanto os bônus descritos ainda não são aplicados pelo motor.
- A IA pode narrar consequências coerentes, mas não inventar, modificar ou ativar regras mecânicas ausentes do catálogo.
- Equipamentos, buffs e debuffs temporários continuam em camada separada.
- Futuras implementações devem tipar efeitos positivos e negativos, definir gatilhos, escopo, duração, resistência, acúmulo e condições de remoção; não inferir esses dados de texto livre.

**Estado real:** catálogo de 100 maldições adicionado ao JSON no commit `bbdf336b6acb62cda2532dbd34af158018753399`. O runtime, a interface de seleção, a validação e a aplicação mecânica ainda não foram implementados nem validados.

## 11. Refatoração completa da aba de criação de personagem

A entrega não se limita à aba de maldições: o objetivo é refazer o fluxo de criação de personagem de ponta a ponta, integrando as escolhas em uma única ficha e reaproveitando o modelo de personagem já existente no projeto. Antes de alterar a interface, auditar os componentes e serviços atuais para evitar um segundo sistema de fichas.

### Fluxo proposto
1. **Identidade:** nome e demais campos de identidade já suportados pela aplicação.
2. **Raça e linhagem:** raça-base, sub-raça/variante ou híbrido permitido pelo catálogo; condições/origens sobrenaturais são escolhas separadas.
3. **Classe:** escolha independente, sujeita apenas a requisitos reais definidos nos dados.
4. **Profissão:** escolha independente e, quando suportada, especialização vinculada à profissão.
5. **Vantagens e desvantagens:** selecionar entradas válidas do catálogo, sem fabricar opções ou efeitos.
6. **Maldições:** selecionar maldições do catálogo; cada uma pode combinar benefício e custo narrativo/mecânico explícitos.
7. **Revisão:** exibir ficha-base, escolhas e efeitos separados por fonte, explicar de onde cada efeito vem e mostrar problemas de validação antes de salvar.
8. **História e retrato por IA (opcionais):** enviar à IA apenas as escolhas confirmadas e a lore canônica relevante; permitir revisar/salvar a história e salvar o retrato gerado. A IA não pode alterar a ficha nem inventar regras.
9. **Salvar e editar:** persistir a ficha no formato usado pelo projeto, preservar IDs estáveis e permitir reabrir/editar sem perder escolhas.

### Regras de integridade
- Auditar o fluxo atual, modelo/draft, persistência, navegação, catálogos e serviços de IA antes de codificar a refatoração.
- Reutilizar os modelos e contratos existentes; não criar uma segunda entidade de personagem nem duplicar a lógica de persistência.
- Raça/sub-raça/híbrido, classe, profissão, vantagens, desvantagens e maldições devem ser campos distintos. Equipamentos, buffs e debuffs temporários continuam em camadas separadas.
- Efeitos mecânicos devem ser estruturados e executados somente se suportados pelo motor. Texto de lore ou descrição nunca se converte automaticamente em regra.
- Validar IDs e referências, compatibilidades explícitas, duplicatas, limites e requisitos definidos nos dados; não presumir restrições por temática.
- Mostrar benefícios e custos de cada maldição antes da confirmação. Efeitos sociais/comportamentais podem ser narrativos; não inventar penalidades numéricas.
- Geração de história/retrato é opcional e não bloqueia criação manual.
- Não declarar a refatoração concluída até que interface, integração, persistência e fluxos relevantes tenham sido implementados. A prioridade inicial é código e integração, não CI, salvo pedido explícito posterior.

### Estado
Este escopo foi registrado como plano. A interface e o runtime ainda precisam ser auditados para localizar a aba atual e determinar os arquivos concretos a modificar.

### Implementação inicial da refatoração (20/10/2026)

Foi implementada uma primeira fatia vertical do fluxo de criação:
- Novo leitor do catálogo editorial em `app/src/main/java/dev/rpgsolo/ui/CharacterCreationCatalog.kt`, com entradas de raça, sub-raça, classe, profissão e maldição.
- Aba Heróis agora mostra seletores pesquisáveis para raça, sub-raça/variante, classe, profissão e seleção múltipla de maldições.
- A ficha salva os IDs escolhidos em metadados locais associados ao ID da ficha; a biblioteca mostra os nomes selecionados.
- Duplicar ficha copia esses metadados e excluir ficha remove os metadados correspondentes.
- A montagem do contexto da ficha para a IA inclui nomes, lore e descrições de benefício/custo do catálogo, com instrução explícita de não tratar esses textos como efeitos mecânicos executáveis.
- Nenhum bônus ou penalidade do catálogo é aplicado automaticamente pelo motor.

**Limitações ainda abertas (não declarar refatoração completa):**
- Os metadados da criação ainda são guardados em SharedPreferences separado do JSON canônico da ficha; exportação/importação individual da ficha e migração dos metadados ainda precisam ser unificadas. Backups de campanha já carregam/restauram essas escolhas.
- Híbridos/ascendências mistas agora têm seletor, 20 combinações catalogadas e validação de referências; efeitos mecânicos continuam desativados.
- Vantagens e desvantagens continuam usando o sistema de traços do pacote atual; a integração com o novo catálogo precisa ser definida sem duplicar regras.
- Efeitos mecânicos tipados das maldições, revisão de compatibilidade, história gerada e retrato por IA ainda não foram implementados.
- Não foi executado build, teste ou CI nesta etapa; a validação de compilação permanece pendente.



### Implementação de híbridos/ascendências mistas (20/10/2026)

- [x] Adicionar categoria `hybrids` separada de `subraces` no catálogo.
- [x] Cadastrar 20 combinações explícitas de duas raças existentes, cada uma com IDs de ascendência e lore original.
- [x] Adicionar seletor de híbrido à criação; escolher híbrido limpa raça/sub-raça e escolher raça limpa híbrido, evitando duas origens conflitantes na ficha-base.
- [x] Persistir o ID do híbrido junto aos demais metadados editoriais e incluí-lo no contexto narrativo da IA.
- [x] Validar IDs duplicados, referências de sub-raças e referências de ambos os pais dos híbridos.
- [x] Exibir lore e descrições de benefício/custo no resumo quando existirem.
- [x] Incluir escolhas editoriais nos backups de campanha e restaurá-las quando o backup é importado.
- [x] Criar exportação individual versionada com ficha e escolhas editoriais e importar esse formato.
- [x] Aceitar também JSONs antigos que contêm apenas a ficha.
- [x] Implementar edição das escolhas de uma ficha já criada sem recriar a ficha.
- [x] Implementar edição das escolhas editoriais de fichas já salvas sem recriar a ficha; a edição não altera a mecânica.
- [ ] Implementar efeitos mecânicos tipados; as combinações adicionadas permanecem narrativas e não concedem bônus automaticamente.

**Limite desta etapa:** as 20 combinações são opções editoriais explícitas, não uma regra para gerar todas as combinações possíveis. Nenhum efeito mecânico foi criado nem aplicado. Build, testes e CI continuam pendentes por instrução de priorizar implementação.


### Persistência das escolhas em backup (20/10/2026)

- [x] Adicionar campo opcional `characterChoices` aos metadados do backup, mantendo compatibilidade com backups antigos por valor padrão vazio.
- [x] Exportar as escolhas das fichas associadas aos IDs estáveis, para as fichas incluídas no backup.
- [x] Restaurar as escolhas ao importar um backup, sem converter lore ou descrições em efeitos mecânicos.
- [x] Definir formato versionado de exportação/importação individual, mantendo compatibilidade de leitura com JSONs antigos.

Build, testes e CI não foram executados.


### Exportação/importação individual da ficha (20/10/2026)

- [x] Exportar um envelope versionado com a ficha mecânica e os metadados de criação associados.
- [x] Importar envelopes versionados e restaurar escolhas de raça/sub-raça/híbrido, classe, profissão e maldições.
- [x] Manter importação compatível com JSONs antigos que contêm somente o objeto da ficha.
- [x] Evitar sobrescrever silenciosamente uma ficha existente: IDs em conflito recebem novo ID e indicação de cópia importada.
- [ ] Implementar edição das escolhas de uma ficha já criada sem recriar a ficha.

Build, testes e CI não foram executados.


### Edição de escolhas em fichas existentes (20/10/2026)

- [x] Adicionar ação para carregar raça/sub-raça/híbrido, classe, profissão e maldições de uma ficha salva.
- [x] Salvar as alterações de escolhas no ID original da ficha, sem recriar atributos, traços ou recursos.
- [x] Desabilitar a criação de outra ficha enquanto o modo de edição está ativo e oferecer cancelamento.
- [ ] Implementar edição unificada dos campos mecânicos e editoriais na mesma tela, com validação final do pacote.

Build, testes e CI continuam sem execução nesta sequência.


### Revisão final antes de salvar (20/10/2026)

- [x] Adicionar revisão com nome, raça/sub-raça ou híbrido, classe, profissão e maldições.
- [x] Mostrar atributos e traços mecânicos do pacote em bloco separado das escolhas editoriais.
- [x] Informar explicitamente que as escolhas editoriais não concedem bônus automaticamente.
- [ ] Ampliar a revisão para apresentar origem e escopo de cada efeito mecânico tipado quando esses efeitos forem implementados.


### Validação de escolhas antes de salvar (20/10/2026)

- [x] Validar IDs de raça, sub-raça, híbrido, classe, profissão e maldições contra as categorias corretas.
- [x] Validar que a sub-raça pertença à raça escolhida e impedir combinação simultânea de raça/sub-raça com híbrido.
- [x] Detectar IDs repetidos de maldições e impedir salvar escolhas inválidas pela interface.
- [x] Exibir os problemas de validação ao jogador sem inferir ou aplicar efeitos mecânicos.
- [x] Validar escolhas antigas durante a importação e apresentar um aviso/fluxo de reparo quando referências não existem mais; sugestões automáticas de substituição continuam pendentes.


### Contexto completo e seguro para a IA (20/10/2026)

- [x] Carregar todas as descrições de benefícios e custos do catálogo, não apenas a primeira entrada de cada lista.
- [x] Ao selecionar um híbrido, incluir também as duas raças de ascendência e suas descrições canônicas no contexto narrativo.
- [x] Reforçar no contexto enviado ao mestre IA que lore, benefícios editoriais e custos editoriais não concedem bônus, penalidades, poderes, resistências ou alterações de atributos.
- [x] Manter os efeitos válidos ancorados na ficha mecânica e no pacote de regras.
- [ ] Implementar geração de história pessoal opcional e persistência do texto aprovado sem alterar escolhas ou mecânicas.
- [ ] Implementar geração opcional de retrato e armazenamento da referência da imagem.


### Recuperação de referências antigas na importação (20/10/2026)

- [x] Validar as escolhas importadas contra o catálogo atual.
- [x] Preservar as escolhas antigas mesmo quando uma referência não existe mais, para evitar perda silenciosa de dados.
- [x] Mostrar aviso com os problemas encontrados e direcionar para “Editar escolhas” na biblioteca.
- [ ] Criar um assistente de migração com sugestões de substituição quando IDs do catálogo forem removidos ou renomeados.


### Exibição completa das maldições selecionadas (20/10/2026)

- [x] Exibir lore e todas as descrições editoriais de benefícios/custos das maldições selecionadas, não somente o resumo.
- [x] Repetir a indicação explícita de que descrições de benefício/custo não equivalem a bônus ou penalidades automáticos.
- [x] No resumo final das escolhas, listar todas as descrições disponíveis; manter compatibilidade com campos editoriais antigos de benefício/custo único.

### Contrato inicial de efeitos tipados do catálogo (20/10/2026)

- [x] Criar modelo de dado `CharacterCatalogEffect(scope, bonus)` e ler declarações `mechanicalEffects` do catálogo sem inferir valores a partir de texto.
- [x] Validar efeitos declarados como objetos com escopo sintaticamente válido e bônus inteiro entre -100 e 100; detectar escopos repetidos na mesma lista.
- [x] Validar os escopos dos efeitos selecionados contra os atributos, testes, traços e escopos de combate suportados pelo pacote ativo; isso bloqueia escolhas incompatíveis sem executar os efeitos.
- [x] Validar a estrutura de efeitos tanto na entrada principal quanto em descrições de benefícios/custos quando a propriedade existe.
- [x] Preservar a origem do efeito tipado (entrada principal, benefício ou custo) e mostrá-la na interface, identificando explicitamente que ainda não é aplicado pelo motor.
- [ ] Integrar efeitos à ficha canônica e ao motor com origem/escopo rastreável, regras de acumulação definidas e prevenção de duplicação com `TraitBonus` já existente.
- [ ] Definir catálogo de escopos por pacote/sistema e validação semântica contra atributos, perícias e testes realmente existentes.
- [ ] Só habilitar execução de efeitos após migração e testes de compatibilidade; o catálogo atual continua com listas de efeitos vazias e nenhum bônus novo é concedido.

A integração inicial já consulta efeitos explícitos no teste de fuga e nos cálculos de combate do jogador. O catálogo atual ainda declara listas de efeitos vazias; portanto, esta integração não concede bônus novos por si só. A origem ainda não é exibida no log de rolagem/combate. Build, testes e CI não foram executados.

### Validação estrutural do catálogo (20/10/2026)

- [x] Validar a versão do esquema, o identificador do catálogo e o pacote de destino.
- [x] Verificar que todas as categorias obrigatórias são listas e que cada entrada tem ID, nome, resumo, lore, `traits` e `mechanicalEffects` com tipos esperados.
- [x] Validar a estrutura das listas editoriais de benefícios/custos quando presentes.
- [x] Detectar itens JSON inválidos e diferenças entre entradas no documento bruto e opções efetivamente carregadas, em vez de ignorá-los silenciosamente.
- [x] Manter validação de IDs duplicados e referências de sub-raças/híbridos.
- [x] Exibir falhas pela validação existente da criação, bloqueando o salvamento de escolhas enquanto o catálogo for inválido.

A validação é estrutural: não interpreta descrições como regras, não executa efeitos e não altera as escolhas salvas. Build, testes e CI não foram executados.

### Exibição de lore e custos no seletor (20/10/2026)

- [x] Ao selecionar uma opção do catálogo, exibir resumo, lore e todas as descrições de benefícios/custos disponíveis.
- [x] Rotular explicitamente benefícios e custos como descrições editoriais, sem aplicação mecânica automática.
