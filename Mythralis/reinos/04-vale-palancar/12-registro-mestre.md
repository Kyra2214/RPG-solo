# 12 — Registro mestre de entidades do reino 4 (R4-2.1, versão 1)

**Estado:** R4-2.1 e R4-2.2 (D-112 e D-113 aceitas pela continuação, 07/11/2026) · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não é cânone novo**: só organiza o que já está nos docs 01, 04 e 10 (D-105 a D-111). Onde um dado não existe, a célula traz `—`; nada foi inventado. As 13 pessoas sem ficha (6 da tríade, 6 heróis lendários e Lumbo Pedra-de-Baixo) **ganharam ID agora**, pelo nome já escrito nos docs 01 e 04, sem criar pessoa nova.
**Uso:** fonte única de IDs do reino 4. Antes de criar NPC, lugar ou facção, procure aqui. **Um ID nunca é reutilizado.** Relógios (`clock.*`) estão no doc 06 e entram em `12-relogios` no R4-5.1; mistérios, conflitos e oportunidades usam rótulos MPL, CFP e OP até o R4-5.5.
**Arquivo irmão:** `12-registro-mestre.csv` (formato de D-21: `tipo,id,nome,situacao,id_catalogo,cidade,papel,povo,vivo,extra,ficha_doc,povo_id`). **ASSUMINDO:** `id_catalogo` fica vazio (o catálogo não tem figuras nomeadas); `cidade` guarda o ID do lugar principal (os oito `city.*` por compatibilidade, D-102); `povo_id` é a raça-base do catálogo; sub-raça, classe e profissão por NPC: `referencia/racas-e-povos/ALINHAMENTO_PALANCAR.md` (R4-2.3); Lumbo Pedra-de-Baixo fica em Rasovau porque lidera o grupo de baixo do vau (doc 04 §9); tipo `facção` com cedilha, como nos reinos 2 e 3.
**Arquivos irmãos (R4-2.2):** `12-facoes.csv` (14 facções) e `12-assentamentos.csv` (os 8 lugares), no molde do reino 3 (§6).
**Fases seguintes:** catálogo (R4-2.3), conferência cruzada (R4-2.4).

## 1. Resumo

| Tipo | Quantidade |
|---|---:|
| Lugares principais (`city.*`) | 8 (4 fixos, 4 sazonais) |
| Facções | 14 |
| Assentamentos | 0 (D-102: sem assentamentos próprios) |
| Pessoas (NPCs) distintas | 25 |

| Situação das pessoas | Qtd |
|---|---:|
| proposto, ficha completa (D-107) | 12 |
| proposto, herói lendário (D-107) | 6 |
| proposto, só tríade (sem ficha) | 6 |
| proposto, só líder de facção (sem ficha) | 1 |

19 vivos e 6 falecidos (lendas). Pessoas por povo (ID do catálogo):

| Povo | ID | Qtd |
|---|---|---|
| Orcano | `orcano` | 3 |
| Porcino | `homem-javali` | 3 |
| Lupino | `homem-lobo` | 3 |
| Goblin | `goblin` | 3 |
| Centauro | `centauro` | 2 |
| Vulpino | `homem-raposa` | 2 |
| Insetoide | `insetoide` | 2 |
| Leporino | `homem-coelho` | 2 |
| Cervino | `homem-cervo` | 2 |
| Goblin de Legião | `hobgoblin` | 1 |
| Ursino | `homem-urso` | 1 |
| Felídeo Errante | `tabaxi` | 1 |

Os 12 nativos têm pelo menos uma pessoa. Nenhum forasteiro e nenhum mestiço (D-64).

## 2. Lugares principais

| ID | Nome | Tipo e regime | Ref. |
|---|---|---|---|
| `city.tabuleiro-velho` | Tabuleiro-Velho — O Celeiro do Planalto | fortaleza-celeiro de planalto | regime: fixo | VP-01 |
| `city.rasovau` | Rasovau — O Vau das Trocas | vau e mercado de curral no curso do Argente | regime: fixo | VP-02 |
| `city.praia-pardo` | Praia-Pardo — O Pouso da Descida | pouso fluvial onde o Argente entra nas planícies | regime: fixo | VP-03 |
| `city.alvasal` | Alvasal — O Pouso do Ermo | pouso de caravana na borda norte do Ermo de Sal | regime: fixo | VP-04 |
| `city.terreiro-maior` | Terreiro-Maior — O Ponto da Assembleia | terreiro sazonal de reunião dos clãs | regime: sazonal | VP-05 |
| `city.vazante` | Vazante — O Acampamento da Marcha | acampamento móvel de migração e defesa | regime: sazonal | VP-06 |
| `city.encosta-aberta` | Encosta-Aberta — O Pouso das Elevações | pouso sazonal de vigia no pé das elevações ocidentais | regime: sazonal | VP-07 |
| `city.gramal` | Gramal — O Território dos Rebanhos | território sazonal de pasto e caça dos clãs | regime: sazonal | VP-08 |

## 3. Facções

| ID | Nome | Ref. |
|---|---|---|
| `faction.guardas-da-marcha` | Guardas da Marcha | F-01 (doc 04 §9) |
| `faction.clas-da-prova` | Clãs da Prova | F-02 (doc 04 §9) |
| `faction.clas-da-aclamacao` | Clãs da Aclamação | F-03 (doc 04 §9) |
| `faction.guardioes-da-reserva` | Guardiões da Reserva | F-04 (doc 04 §9) |
| `faction.mesa-da-partilha` | Mesa da Partilha | F-05 (doc 04 §9) |
| `faction.pedras-de-cima` | Pedras de Cima | F-06 (doc 04 §9) |
| `faction.pedras-de-baixo` | Pedras de Baixo | F-07 (doc 04 §9) |
| `faction.receptores-do-pouso` | Receptores do Pouso | F-08 (doc 04 §9) |
| `faction.balanca-do-pouso` | Balança do Pouso | F-09 (doc 04 §9) |
| `faction.vigias-da-cisterna` | Vigias da Cisterna | F-10 (doc 04 §9) |
| `faction.guias-da-encosta` | Guias da Encosta | F-11 (doc 04 §9) |
| `faction.vigias-das-trilhas` | Vigias das Trilhas | F-12 (doc 04 §9) |
| `faction.clas-do-gado` | Clãs do Gado | F-13 (doc 04 §9) |
| `faction.clas-da-caca` | Clãs da Caça | F-14 (doc 04 §9) |

## 4. Pessoas

| ID | Nome | Lugar | Povo | Povo (ID) | Vivo | Situação |
|---|---|---|---|---|---|---|
| `npc.kharos-casco-de-marcha` | Kharos Casco-de-Marcha | `city.vazante` | Centauro | `centauro` | sim | proposto, ficha completa (D-107) |
| `npc.yorna-palavra-firme` | Yorna Palavra-Firme | `city.terreiro-maior` | Orcano | `orcano` | sim | proposto, ficha completa (D-107) |
| `npc.gruzk-estandarte-baixo` | Gruzk Estandarte-Baixo | `city.alvasal` | Goblin de Legião | `hobgoblin` | sim | proposto, ficha completa (D-107) |
| `npc.moura-mel-guardado` | Moura Mel-Guardado | `city.tabuleiro-velho` | Ursino | `homem-urso` | sim | proposto, ficha completa (D-107) |
| `npc.pocha-ninhada-farta` | Pocha Ninhada-Farta | `city.tabuleiro-velho` | Porcino | `homem-javali` | sim | proposto, ficha completa (D-107) |
| `npc.tessun-salto-calado` | Tessun Salto-Calado | `city.encosta-aberta` | Felídeo Errante | `tabaxi` | sim | proposto, ficha completa (D-107) |
| `npc.rukhan-retorno-certo` | Rukhan Retorno-Certo | `city.vazante` | Lupino | `homem-lobo` | sim | proposto, ficha completa (D-107) |
| `npc.zinu-troca-justa` | Zinu Troca-Justa | `city.rasovau` | Goblin | `goblin` | sim | proposto, ficha completa (D-107) |
| `npc.quira-recado-certo` | Quira Recado-Certo | `city.rasovau` | Vulpino | `homem-raposa` | sim | proposto, ficha completa (D-107) |
| `npc.vrissk-colonia-quieta` | Vrissk Colônia-Quieta | `city.praia-pardo` | Insetoide | `insetoide` | sim | proposto, ficha completa (D-107) |
| `npc.tibbe-salto-curto` | Tibbe Salto-Curto | `city.gramal` | Leporino | `homem-coelho` | sim | proposto, ficha completa (D-107) |
| `npc.cassan-galhada-velha` | Cassan Galhada-Velha | `city.terreiro-maior` | Cervino | `homem-cervo` | sim | proposto, ficha completa (D-107) |
| `npc.bruga-colheita-salva` | Bruga Colheita-Salva | `city.tabuleiro-velho` | Porcino | `homem-javali` | não | proposto, herói lendário (D-107) |
| `npc.jessik-trato-primeiro` | Jessik Trato-Primeiro | `city.rasovau` | Vulpino | `homem-raposa` | não | proposto, herói lendário (D-107) |
| `npc.nokk-peso-alheio` | Nokk Peso-Alheio | `city.praia-pardo` | Goblin | `goblin` | não | proposto, herói lendário (D-107) |
| `npc.grebb-balanca-justa` | Grebb Balança-Justa | `city.praia-pardo` | Goblin | `goblin` | sim | proposto, só tríade (sem ficha) |
| `npc.urrek-bando-parado` | Urrek Bando-Parado | `city.alvasal` | Lupino | `homem-lobo` | não | proposto, herói lendário (D-107) |
| `npc.aruun-poco-compartilhado` | Aruun Poço-Compartilhado | `city.alvasal` | Lupino | `homem-lobo` | sim | proposto, só tríade (sem ficha) |
| `npc.hukk-pedra-de-divisa` | Hukk Pedra-de-Divisa | `city.terreiro-maior` | Orcano | `orcano` | não | proposto, herói lendário (D-107) |
| `npc.bolgra-forja-de-campo` | Bolgra Forja-de-Campo | `city.vazante` | Orcano | `orcano` | sim | proposto, só tríade (sem ficha) |
| `npc.sendro-clareira-vigia` | Sendro Clareira-Vigia | `city.encosta-aberta` | Cervino | `homem-cervo` | sim | proposto, só tríade (sem ficha) |
| `npc.cadrek-cerca-aberta` | Cadrek Cerca-Aberta | `city.encosta-aberta` | Centauro | `centauro` | sim | proposto, só tríade (sem ficha) |
| `npc.zzirr-favo-dividido` | Zzirr Favo-Dividido | `city.gramal` | Insetoide | `insetoide` | não | proposto, herói lendário (D-107) |
| `npc.ghorla-fartura-dividida` | Ghorla Fartura-Dividida | `city.gramal` | Porcino | `homem-javali` | sim | proposto, só tríade (sem ficha) |
| `npc.lumbo-pedra-de-baixo` | Lumbo Pedra-de-Baixo | `city.rasovau` | Leporino | `homem-coelho` | sim | proposto, só líder de facção (sem ficha) |

## 5. Conferências

- **47 entidades** (8 lugares, 14 facções, 25 pessoas); **0 ID duplicado**; **0 ID de lugar, facção ou pessoa do reino 4 citado nos docs 01, 03, 04, 05, 06 e 10 sem registro** (a única citação fora do registro é `npc.oranna-passo-largo`, de Aeltharyn, numa proibição).
- **0 colisão** de ID com os registros mestres dos reinos 1, 2 e 3; 12 `povo_id` conferidos no `personagem-catalogo.json` (0 fora do catálogo).
- Os 12 principais e os 6 líderes de facção sem ficha estão registrados; os 6 heróis lendários não têm ficha nem feito (parte 2 do doc 01 §3.1).
- Nenhum documento de Aeltharyn, das Coroas ou do Véu alterado.

## 6. Facções e lugares como dados (R4-2.2)

- `12-facoes.csv`: 14 linhas, 0 campo vazio. Liderança, objetivo, métodos, aliados e rivais vêm do doc 04 §9; presença por lugar, do doc 04 §9.2; `tensao`, do doc 03 (o problema do lugar onde a facção atua). **`recursos`** é `desconhecido` (o doc 04 não lista recursos de facção; não se inventa).
- Aliados e rivais em IDs de facção: 0 assimetria, 0 aliado que seja rival, 0 ID inexistente. 2 `rivais` `desconhecido`, com o motivo do doc 04: `faction.guardas-da-marcha` (não toma lado na sucessão) e `faction.vigias-da-cisterna` (as caravanas não formam facção).
- `12-assentamentos.csv`: 8 linhas (os oito lugares), 0 campo vazio. `cidade_mae` é `—` nos quatro fixos e o lugar-mãe do doc 03 nos sazonais. `lideranca_id` é o chefe do Trato (doc 04 §8); em Rasovau e Encosta-Aberta é `desconhecido`, porque o Trato é o que está em disputa.

## 7. Decisão pendente

**D-112** (registro) **e D-113** (facções e lugares como dados): aprovar o registro (47 entidades) e os 13 IDs novos de pessoas sem ficha. Opções: **A.** aprovar; **B.** trocar o slug de algum ID (diga qual). **Recomendação: A.**
