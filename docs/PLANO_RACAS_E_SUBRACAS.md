# Plano de implementação — Raças, sub-raças e profissões do RPG Solo

**Estado:** planejamento iniciado; não declara catálogo, interface ou mecânicas implementados.  
**Branch:** `feature/racas-subracas`  
**Inspiração:** D&D como referência criativa de fantasia, sem copiar lore, ilustrações ou blocos de regras protegidos. O RPG Solo terá conteúdo original e motor próprio.

## 1. Objetivo

A criação de personagem combina escolhas independentes: **raça**, **sub-raça** (quando existir), **classe/profissão** e **especialidade** (quando o desenho final a incluir). Cada escolha tem identidade, lore/descrição e traços próprios. Os efeitos mecânicos válidos são acumulados pela ficha e resolvidos pelo motor.

Exemplo de design:
- Raça **Elfo** concede Precisão +1.
- Profissão **Arqueiro** concede Dano +1 ao usar arcos e bestas.
- Resultado: o personagem recebe o benefício racial de precisão e o bônus profissional de dano com armas de projéteis. Os bônus permanecem identificados pela origem, em vez de serem fundidos num modificador sem explicação.

Os valores acima são exemplos propostos, ainda não regras implementadas ou balanceadas. A semântica de “Precisão” e “Dano” deverá corresponder a atributos/efeitos que o motor realmente suporte.

## 2. Conceitos separados

- **Raça:** identidade de povo/linhagem, lore, traços raciais comuns.
- **Sub-raça:** variante ligada a uma raça-base, com lore própria e traços adicionais ou substituições declaradas.
- **Classe/profissão:** treinamento, ocupação ou papel de aventura que concede competências e efeitos independentes da raça.
- **Especialidade:** foco mais estreito dentro da profissão, se a ficha e o pacote de regras ativo suportarem essa camada. Não presumir que seja obrigatória em todos os sistemas.
- **Traço:** característica descritiva que pode ter efeito mecânico tipado ou ser apenas narrativa.
- **Efeito mecânico:** modificação estruturada, validada pelo motor e com escopo explícito (por exemplo, bônus de dano apenas com arcos e bestas).
- **Lore:** conteúdo narrativo original. A lore não é executável e não deve ser confundida com regra.

Raça e profissão não devem se excluir mutuamente nem ter listas rigidamente acopladas: um elfo pode ser arqueiro, guerreiro, curandeiro ou escolher outra profissão compatível com as regras do pacote ativo.

## 3. Metas do catálogo

Metas editoriais, não contagens já implementadas:

| Coleção | Meta |
|---|---:|
| Raças principais de fantasia | 10–15 |
| Raças adicionais de fantasia | 30–50 |
| Sub-raças e variantes | Expansível |
| Profissões/classes iniciais | Definir após mapear as regras comuns do motor |
| Especialidades | Expansível, opcional por pacote |
| Catálogo potencial | 50+ raças, mais variantes, profissões e especialidades |

O catálogo não terá limite fixo. Adicionar entradas deve ser uma operação de dados, não uma mudança de código de interface por opção.

## 4. Dados e identificadores

Cada raça deve ter `id` estável, `name`, `summary`, `lore`, traços, tags, proveniência e versão. Uma sub-raça também exige `parentRaceId`, lore própria e definição explícita de traços herdados, adicionais ou substituídos.

Cada profissão/classe deve ter:
- `id` estável e nome de exibição;
- resumo e descrição/lore própria;
- requisitos e compatibilidades opcionais, definidos por dados;
- competências e traços concedidos;
- efeitos mecânicos tipados, com escopo de aplicação;
- proveniência e versão.

Cada especialidade, se usada, deve referenciar uma profissão/classe válida e definir o que acrescenta ou substitui.

Os traços devem separar `description` de `mechanicalEffects`. Efeitos estruturados devem expressar tipo, valor, escopo, requisitos, gatilhos, limites e duração quando aplicável. Exemplo conceitual: `damage_bonus +1` com escopo `weapon_group: bows_and_crossbows`; não um bônus universal de dano. O esquema concreto será alinhado às capacidades reais do motor após a auditoria.

## 5. Combinação dos efeitos

A ficha deve guardar separadamente as escolhas e suas fontes:
1. traços da raça;
2. traços da sub-raça;
3. traços da profissão/classe;
4. traços da especialidade, se houver;
5. vantagens, perícias, poderes e regras adicionais do pacote ativo.

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

1. Selecionar raça; exibir imediatamente resumo e lore completa.
2. Se houver sub-raças, listar apenas as ligadas à raça selecionada. Ao selecionar uma, exibir sua lore específica e manter a lore-base acessível.
3. Selecionar profissão/classe; exibir descrição, lore e competências/efeitos.
4. Selecionar especialidade apenas quando disponível e compatível.
5. Mostrar uma revisão final: escolhas, lore consultável, traços herdados e próprios, efeitos reconhecidos, escopos e avisos de compatibilidade.
6. Confirmar e gravar IDs estáveis na ficha.
7. Na ficha salva, permitir consultar as lores e a origem dos bônus.
8. Se uma escolha for alterada, recalcular os efeitos derivados e pedir confirmação quando outra escolha se tornar incompatível.

A lore canônica vem do catálogo de dados e funciona offline; a IA pode usá-la na narração, mas não a substitui nem altera a mecânica.

## 7. Ordem editorial do catálogo

### Raças
Começar por um núcleo de 10–15 povos reconhecíveis de fantasia, com lore original; depois ampliar para 30–50 raças adicionais. Sub-raças serão criadas quando diferenças culturais, geográficas, mágicas ou biológicas forem significativas, não só para inflar números.

### Profissões/classes
Depois da auditoria do motor, elaborar catálogo inicial que cubra funções distintas — combate corpo a corpo, armas à distância, defesa, furtividade, exploração, conhecimento, suporte e magia quando suportada. Os nomes finais, requisitos e efeitos serão definidos como conteúdo original e balanceados no sistema próprio.

### Especialidades
Tratar como camada opcional de refinamento, não duplicar automaticamente a profissão. Exemplo ilustrativo: Arqueiro pode ter especialidades futuras, mas isso não deve ser implementado antes de definir o esquema e o equilíbrio.

## 8. Licença e originalidade

D&D é inspiração, não autorização para copiar. Não embutir textos de lore, ilustrações, tabelas ou blocos de regras protegidos. Escrever conteúdo original, registrar proveniência e identificar conteúdo importado pelo usuário, sem redistribuí-lo como nativo.

## 9. Fases de implementação

### R1 — Auditoria e compatibilidade
- Localizar modelos da ficha, `CharacterDraft`, pacotes de regras, criação de personagem, persistência e backups.
- Identificar os atributos, perícias, armas, bônus tipados e validações já suportados.
- Confirmar como a ficha guarda traços e como o motor aplica efeitos.
- Reutilizar o fluxo existente; não criar um segundo sistema de personagens.

### R2 — Esquema de dados e catálogo
- Definir formatos versionados para raça, sub-raça, profissão/classe e especialidade.
- Criar carregador e validador de IDs e referências.
- Manter catálogo separado dos pacotes de regras e da ambientação.
- Cadastrar primeiro um conjunto pequeno de exemplos originais, incluindo Elfo e Arqueiro, para validar o modelo.

### R3 — Efeitos combinados
- Formalizar os efeitos tipados que o motor realmente consegue executar.
- Definir escopos para bônus raciais e profissionais.
- Combinar efeitos de fontes diferentes sem duplicação nem aplicação fora do escopo.
- Sinalizar efeitos não suportados; não fingir que funcionam.

### R4 — Interface
- Implementar seleção de raça/sub-raça com lore visível.
- Implementar seleção de profissão/classe com descrição e efeitos visíveis.
- Adicionar especialidade apenas quando o esquema estiver definido.
- Exibir resumo de bônus e sua origem antes da confirmação.

### R5 — Persistência
- Guardar IDs escolhidos, versão/origem necessárias e estado da ficha.
- Preservar compatibilidade com fichas antigas sem essas escolhas.
- Garantir que backups e restaurações preservem as escolhas e os efeitos derivados sem duplicá-los.

### R6 — Expansão editorial
- Aumentar gradualmente raças, sub-raças e profissões.
- Criar lore original para cada opção.
- Revisar coerência, balanceamento, sobreposições e compatibilidade.
- Versionar conteúdo e documentar efeitos pendentes.

## 10. Critérios de conclusão

- Raças, sub-raças, profissões e especialidades são dados expansíveis, sem limite codificado.
- Toda raça/sub-raça tem lore própria e a interface a mostra ao selecionar.
- Toda profissão/classe tem descrição e lista explícita de competências/efeitos.
- A ficha conserva as escolhas por IDs estáveis e permite rever as lores.
- Efeitos raciais e profissionais são acumulados corretamente, respeitando escopo e regras do pacote.
- O exemplo Elfo + Arqueiro diferencia Precisão +1 racial e Dano +1 com arcos/bestas profissional, depois de o motor suportar formalmente esses efeitos.
- Alterar uma escolha recalcula efeitos derivados sem apagar dados independentes silenciosamente.
- Catálogo inválido produz erro claro e não corrompe a ficha.
- Build/testes/aparelho só serão declarados validados após execução explícita; esta etapa é planejamento, não aprovação de validação.

## 11. Estado

- [x] Criar branch dedicada.
- [x] Registrar plano inicial de raças/sub-raças.
- [x] Ampliar plano para incluir profissão/classe e composição de efeitos.
- [ ] Auditar modelos e telas atuais.
- [ ] Confirmar esquema compatível com o motor.
- [ ] Implementar catálogo, lore e validação.
- [ ] Integrar efeitos tipados à ficha e às ações.
- [ ] Implementar interface de criação e persistência.
- [ ] Expandir e balancear o catálogo.

**Próxima ação:** auditar o código existente antes de implementar os modelos e os bônus, para encaixar raça e profissão na ficha atual e garantir que o motor aplique cada efeito no lugar correto.
