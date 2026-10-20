# Plano de implementação — Raças, sub-raças, classes e profissões do RPG Solo

**Estado:** planejamento iniciado; não declara catálogo, interface ou mecânicas implementados.  
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

### R2 — Esquema de dados e catálogo
- [x] Criar primeiro catálogo editorial versionado em `rules/src/main/resources/packages/sistema-proprio/personagem-catalogo.json`.
- [x] Cadastrar 50 raças, 50 sub-raças/variantes, 50 classes e 103 profissões com IDs estáveis, resumo e lore/descrição originais.
- [x] Referenciar cada sub-raça à sua raça-base por `parentRaceId`.
- [x] Deixar `mechanicalEffects` vazio em todas as opções; nenhum bônus foi inventado ou concedido por lore.
- [ ] Definir e implementar carregador/validador de IDs, referências e esquema.
- [ ] Integrar o catálogo ao carregamento real do pacote e à ficha.
- [ ] Completar campos editoriais de competências e traços depois da auditoria das mecânicas existentes.

Este primeiro lote é conteúdo versionado, ainda não conectado ao runtime. As entradas não concedem efeitos mecânicos até que regras válidas sejam explicitamente definidas e suportadas pelo motor.

### R3 — Efeitos combinados
- Formalizar os efeitos tipados que o motor realmente consegue executar.
- Definir escopos para bônus raciais e profissionais.
- Combinar efeitos de fontes diferentes sem duplicação nem aplicação fora do escopo.
- Sinalizar efeitos não suportados; não fingir que funcionam.

### R4 — Interface
- Implementar seleção de raça/sub-raça com lore visível.
- Implementar seleção de classe com descrição e habilidades visíveis.
- Implementar seleção de profissão independente, com lore, competências e efeitos visíveis.
- Adicionar especialização profissional apenas quando o esquema estiver definido.
- Exibir resumo de bônus e sua origem antes da confirmação.

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
- Guardar IDs escolhidos, versão/origem necessárias e estado da ficha.
- Preservar compatibilidade com fichas antigas sem essas escolhas.
- Garantir que backups e restaurações preservem as escolhas e os efeitos derivados sem duplicá-los.

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
- [ ] Implementar interface de criação e persistência.
- [ ] Implementar geração opcional de lore e retrato com IA, com limites rígidos contra alterações mecânicas.
- [ ] Expandir e balancear o catálogo.

**Estado atual:** catálogo editorial inicial criado em `rules/src/main/resources/packages/sistema-proprio/personagem-catalogo.json` (50 raças, 50 sub-raças/variantes, 50 classes e 103 profissões). O catálogo ainda não é carregado pelo runtime; efeitos mecânicos estão vazios e nenhuma integração, build ou teste foi declarado.

**Próxima ação:** auditar `CharacterDraft`, `PackageData`/`RulePackage`, persistência e telas de criação; depois implementar carregamento/validação do catálogo e integrar os IDs à ficha sem criar um segundo sistema de personagens.
