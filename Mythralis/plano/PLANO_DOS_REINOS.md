# Plano dos reinos

**Projeto:** Mythralis (independente do código do RPG Solo).
**Base:** `MUNDO_PRECONFIGURADO.md` (requisitos do autor) e o que o Reino das Florestas já provou na prática.

## 1. Os cinco reinos e a ordem de trabalho

| # | Reino (nome provisório) | Tema | Estado | Pasta |
|---|---|---|---|---|
| 1 | **Aeltharyn — Crônicas do Primeiro Verde** (reino 1 do mundo de **Mythralis**) | Reino das Florestas, monarquia élfica antiga, sob o Rei dos Elfos (Aerendyl Vaelorian), capital Elarion | **Fechado editorialmente** (sete fases); em revisão do autor | `reinos/01-aeltharyn-reino-das-florestas/` |
| 2 | Coroas de Pedra | Montanhas | **Fechado editorialmente** (oito fases); fechamento 3 aprovado em 05/11/2026 (D-62) | `reinos/02-coroas-de-pedra-reino-das-montanhas/`; plano em `REFINAMENTO_COROAS_DE_PEDRA.md` |
| 3 | Véu dos Malditos | Malditos | **Fechado editorialmente** (oito fases); fechamento 4 aprovado pelo autor em 07/11/2026 (D-100, D-101); doc 11 em `reinos/03-veu-dos-malditos/` | `REFINAMENTO_VEU_DOS_MALDITOS.md` (pasta proposta `reinos/03-veu-dos-malditos/`, D-66) |
| 4 | **Vale Palancar** (antes "Marcas das Feras") | Feras | **Fechado editorialmente** (oito fases, 37/37); fechamento 5 aprovado pelo autor em 07/11/2026 (D-137, D-138); doc 11 em `reinos/04-vale-palancar/` | `REFINAMENTO_VALE_PALANCAR.md` (pasta `reinos/04-vale-palancar/`) |
| 5 | **Azeroth** (antes "Pacto Humano") | Humanos (último a ser desenvolvido) | **Liberado em 07/11/2026 (D-138)**; plano de 37 módulos criado; Fases 0, 1 e 2 concluídas (R5-2.4 escrito) e Fase 3 concluída e R5-4.1 a R5-4.4 escritos, Fase 4 concluída e R5-5.1 a R5-5.5 escritos, Fase 5 concluída e R5-6.1 a R5-6.4 escritos, Fase 6 concluída e R5-7.1 a R5-7.3 escritos, Fases 7 e 8 concluídas, doc 11 escrito (37/37, 100%); aguarda a aprovação do autor (D-174); nome decidido (**Azeroth**, D-139); escala 10 lugares (ASSUMINDO), governo de várias coroas rivais (autor) e Estelar nativo (ASSUMINDO), D-141 | `REFINAMENTO_PACTO_HUMANO.md` |

**Regra de produção (do autor):** não desenvolver o reino seguinte até revisar e concluir o ecossistema do anterior. Os quatro nomes futuros são rótulos provisórios, sem história nem geografia canônica.

## 2. Escala mínima por reino

Do documento do autor, confirmada pelo que Aeltharyn entregou:

- 10 cidades distintas, cada uma com função coerente com a geografia (porto com água navegável, mineradora com minério, fortaleza em posição estratégica).
- 8 a 12 NPCs principais, com objetivos, relações e limitações: governantes, herdeiros, **pelo menos 2 heróis e 2 generais**, conselheiros, diplomatas, líderes religiosos e chefes de facção.
- Geografia própria (biomas, rios, montanhas, costas, ruínas, perigos), rotas e recursos.
- Povos de origem e diáspora, sem exclusividade racial nem estereótipo.
- Facções e conflitos com causa concreta (recursos, sucessão, fronteiras, crenças, comércio, rivalidades pessoais); nem todo conflito é guerra.
- Estado inicial canônico, data de início e relógios de campanha.

Aeltharyn **excede** o mínimo: 10 cidades, 20 assentamentos, 16 povos e linhagens, 51 figuras (37 vivas no início), 7 relógios e 14 rotas com tempo definido (números do catálogo técnico, ver `referencia-tecnica/`). Isso é o padrão de ambição, não o mínimo exigido para os outros reinos.

**Refinamento do reino 1:** fases, módulos e submódulos em `REFINAMENTO_AELTHARYN.md`. **Plano do reino 2:** `REFINAMENTO_COROAS_DE_PEDRA.md`.

## 3. Camada de mundo (acima dos reinos)

Coisas que não pertencem a um reino só e que os reinos 2 a 5 vão precisar. **Nada disso está escrito ainda.** Sugestão de ordem: M-01, M-04 e M-05 antes de abrir o reino 2 (não violam a regra de ordem, porque não são um reino).

| Id | Documento | Para quê | Estado |
|---|---|---|---|
| M-01 | Bíblia do mundo: nome oficial, gênero, tom, história de origem, eras globais | Hoje só existe a era local de Aeltharyn | ◐ Mínimo em `mundo/M-01-biblia-do-mundo-minima.md` (W-01 aguarda D-28); origem e eras globais ficam fora |
| M-02 | Calendário e tempo | Aeltharyn usa a Contagem das Seivas (ano 742); definir calendário comum e tabela de conversão entre reinos | ◐ Aeltharyn definido (doc 01, seção 35, M2.4); ponte mínima em `mundo/M-02-calendario-e-conversao.md` (W-02 aguarda D-29) |
| M-03 | Magia e sobrenatural | Regras gerais: o que a magia faz e não faz, custo, fontes | ☐ |
| M-04 | Geografia mestra | Mapa dos continentes, mares, cordilheiras, rios, ilhas, vulcões, desertos e onde cada reino fica; fronteiras | ☑ `mundo/M-04-geografia-mestra.md` (G-01 a G-10), aprovado em 25/10/2026 |
| M-05 | Povos e migrações | Distribuição das 50 raças pelos 5 reinos, diásporas, forasteiros. Rascunho antigo em `referencia/racas-e-povos/MATRIZ_POVOS_POR_REINO.md`; matriz completa em `mundo/M-05-povos-e-migracoes.md` | ☑ Aprovado em 26/10/2026 (D-24) |
| M-06 | Facções transnacionais | Guildas e ordens de aventureiros, ordens religiosas, escolas de magia, mercenários, redes comerciais, sociedades secretas, protetores de ruínas | ☐ |
| M-07 | Rotas e economia entre reinos | Moeda, comércio, tratados, corredores entre reinos | ☐ |
| M-08 | Religião e cosmologia | Panteão ou crenças comuns; só se a campanha precisar | ☐ |
| M-09 | Linha do tempo e conflitos globais | Tratados, guerras, disputas entre reinos, ameaças mundiais | ☐ |
| M-10 | Idiomas e nomes | Convenções de nomes por povo e reino, para não repetir | ◐ Mínimo em `mundo/M-10-nomes-minimo.md` (W-03 aguarda D-30); idiomas ficam fora |
| M-11 | Figuras de nível mundial | Heróis, generais e NPCs que atravessam reinos | ☐ |
| M-12 | Memória do mundo | Formato dos marcos e eventos persistentes (ver `NPCS_RECORRENTES_MEMORIA_ENTRE_CAMPANHAS.md`) | ☐ |

## 4. Como cada reino é entregue

Todo reino segue `MODELO_DE_REINO.md` (as 11 peças de Aeltharyn, fichas padrão, IDs e portas de conclusão). Um reino só é considerado fechado quando passa todas as portas.

## 5. Campanhas e cânone (resumo)

Três modos, vindos do documento do autor: **cânone compartilhado** (campanhas alteram o mesmo mundo), **linha do tempo alternativa** (mesmo cenário-base, mudanças isoladas) e **cópia independente**. Esse eixo é separado da *fidelidade* à história (fiel, alternativa, inspirada) que o RPG Solo já tem. Como isso vira código está em `INTEGRACAO_FUTURA_RPG_SOLO.md`.

## 6. Decisões em aberto

1. **Aeltharyn:** padronizar "Corvídeos" × "Corvários" (ver `ALINHAMENTO_AELTHARYN.md`).
2. **Mundo:** nome oficial do mundo todo (Aeltharyn é o nome provisório do mundo no documento do autor, mas aqui também nomeia o primeiro reino).
3. **Camada de mundo:** confirmar a ordem sugerida (M-01, M-04, M-05 primeiro).
4. **Distribuição das raças** pelos reinos 2 a 5 (a matriz está em branco).
5. **Concorrência no cânone compartilhado** (duas campanhas abertas sobre o mesmo cânone): decidir só na implementação.
6. **Boss do cenário de teste** e demais tramas de campanha: fora deste projeto; ficam no plano do RPG Solo.
