# Vale Palancar — matriz de povos nativos, lugares e forasteiros (doc 10)

**Estado:** R4-1.6, D-110 aceita pela continuação (07/11/2026) · **Data:** 2026-11-07
**Etiqueta:** tudo **[PROPOSTO PV-01 a PV-08]**. Cânone de apoio: M-05 (D-24: 12 nativos e 25 forasteiros das Feras), doc 01 (partes 1 a 3), docs 03 a 05, `referencia/dados-brutos/personagem-catalogo.json` (IDs), contrato de fronteira (P-2 resolvido em D-103; P-3 tratado no §2.1).
**Regras seguidas:** povo em **ID do catálogo**; nenhuma quantidade de gente (R4-3.2); nenhuma rota ou tempo (R4-3.1); nenhum povo é cidadão, vilão ou leal por natureza (W-01); \"Fera\" e \"Palancar\" nunca são sentença; goblins, inclusive os de legião, não são \"o inimigo\" por raça; sub-raça só do catálogo; mestiço pela regra D-64 (não alocado); forasteiro não cria rota nem vínculo com outro reino; **nenhum NPC novo**.
**Campos adiados:** conferência final de IDs e classes por NPC (R4-2.3); competências (R4-4.2).

## 1. Os doze povos nativos **[PROPOSTO PV-01]**

Nativo = povo de referência do reino, não cidadania nem lealdade (M-05 §1). Lugares vêm da parte 1 do doc 01.

| Nº | Povo | ID do catálogo | Lugares onde está | Identidade no reino (uma linha) |
|---|---|---|---|---|
| 1 | Orcano | `orcano` | Terreiro-Maior, Vazante, Gramal | assembleia, marcha e rebanhos |
| 2 | Felídeo Errante | `tabaxi` | Vazante, Encosta-Aberta | trilha e marcha |
| 3 | Centauro | `centauro` | Terreiro-Maior, Vazante, Encosta-Aberta | marcha, assembleia e trilhas |
| 4 | Goblin | `goblin` | Rasovau, Praia-Pardo, Alvasal | vau, pouso e cisterna |
| 5 | Goblin de Legião | `hobgoblin` | Alvasal, Vazante | vigias de turno e ordem de acampamento |
| 6 | Vulpino | `homem-raposa` | Rasovau | troca e recados |
| 7 | Leporino | `homem-coelho` | Rasovau, Gramal | vau e rebanhos |
| 8 | Ursino | `homem-urso` | Tabuleiro-Velho, Gramal | celeiro e rebanhos |
| 9 | Lupino | `homem-lobo` | Alvasal, Terreiro-Maior, Vazante | cisterna, assembleia e marcha |
| 10 | Porcino | `homem-javali` | Tabuleiro-Velho, Gramal | celeiro e rebanhos |
| 11 | Cervino | `homem-cervo` | Tabuleiro-Velho, Terreiro-Maior, Encosta-Aberta | celeiro, assembleia e trilhas |
| 12 | Insetoide | `insetoide` | Praia-Pardo, Gramal | pouso e pasto |

### 1.1 O que nenhum povo é
Nenhum povo é selvagem, perigoso, vilão, cidadão ou leal por natureza. **\"Fera\" (nome de trabalho antigo) e \"Palancar\" são nomes de reino, não sentença** (W-01, contrato §5, item 7). Clã é forma de organização, não traço de raça. Goblin e Goblin de Legião aparecem em vigias, trocas e pousos por desenho do doc 01, **não** como ameaça. Nenhum dos doze é só chefe, só guarda ou só mercador.

## 2. Sub-raças do catálogo **[PROPOSTO PV-02]**

Só IDs que existem no JSON. Onde cada uma aparece é ASSUMINDO pelo nome; a troca é livre. **O nome de uma sub-raça não cria relevo nem lore no reino** (\"das Dunas\", \"dos Vales\" são rótulos do catálogo).

| Povo | Sub-raça (ID) | Onde aparece |
|---|---|---|
| Orcano | `orcano-das-estepes` | Terreiro-Maior, Vazante, Gramal |
| Felídeo Errante | `felideo-das-dunas` | Vazante, Encosta-Aberta |
| Centauro | `centauro-das-pradarias` | Terreiro-Maior, Vazante, Encosta-Aberta |
| Goblin | `goblin-das-sucatas` | Rasovau, Praia-Pardo, Alvasal |
| Goblin de Legião | `legionario-dos-estandartes` | Alvasal, Vazante |
| Vulpino | `homem-raposa-dos-vales` | Rasovau |

Sem sub-raça no catálogo (não se inventa): Leporino, Ursino, Lupino, Porcino, Cervino, Insetoide. **Não usadas** por falta de lugar que as sustente: `goblin-dos-pantanos` (o reino não tem pântano em M-04) e `legionario-das-cidadelas` (o reino não tem cidadela). Sub-raça de cada NPC: R4-2.3.

### 2.1 Linhagens próprias de Orcano, Felídeo Errante e Centauro (P-3)
Estes três povos são nativos **também** de Aeltharyn, em linhagens diferentes (M-05 §3). O catálogo tem duas sub-raças de cada um, e Aeltharyn já usa uma (`orcano-das-matas`, `felideo-das-selvas`, `centauro-das-florestas`). **Proposta:** o Vale usa a **outra** de cada par (`orcano-das-estepes`, `felideo-das-dunas`, `centauro-das-pradarias`), **sem parentesco** com as de Aeltharyn e sem nome novo. Nenhum NPC de Aeltharyn (`npc.oranna-passo-largo` e os demais, contrato A4 e A5) é parente de alguém do Vale. Se o autor preferir outro nome de linhagem, vale D-110, opção B.

## 3. Mestiços **[D-64]**

Não são alocados a reino, lugar ou facção. Nascem no lugar de qualquer um dos pais, em qualquer par de raças ou sub-raças do catálogo; o jogador escolhe. Nenhuma tabela de mestiços aqui.

## 4. Lugares × povos **[PROPOSTO PV-04]**

Povos presentes conforme o doc 01; nenhum lugar tem menos de dois povos nativos.

| Lugar | Nativos | Forasteiros |
|---|---|---|
| Tabuleiro-Velho | Ursino; Porcino; Cervino | Anão; Giganteano |
| Rasovau | Vulpino; Leporino; Goblin | Humano; Pequenino; Corvídeo |
| Praia-Pardo | Goblin; Insetoide | Anão; Escamado Miúdo (e visitantes da Descida: Gnomo e Construto Desperto, doc 05 §3) |
| Alvasal | Lupino; Goblin de Legião; Goblin | Ratoide; Metamorfo Lupino; Umbral |
| Terreiro-Maior | Orcano; Centauro; Cervino; Lupino | Elfo; Humano (visitantes individuais, sem representar reino) |
| Vazante | Centauro; Felídeo Errante; Lupino; Orcano; Goblin de Legião | Silvano Festivo; Feérico |
| Encosta-Aberta | Centauro; Felídeo Errante; Cervino | Labiríntico; Draconato; Altaneiro |
| Gramal | Leporino; Porcino; Ursino; Insetoide; Orcano | Felino das Nuvens; Verdejante |

## 5. Forasteiros **[PROPOSTO PV-05]**

Os 25 de M-05 §4 (coluna Feras = F). **Forasteiro** = origem fora do Vale: residente avulso, viajante, mercador ou estudioso; **motivo** é possibilidade, não destino do povo. **Origem e caminho: desconhecidos**; nenhuma linha cria rota, tratado ou comunidade grande, nem representa outro reino (contrato A9).

| ID | Povo | Onde aparece | Motivos possíveis (ASSUMINDO) |
|---|---|---|---|
| `humano` | Humano | Rasovau, Terreiro-Maior | troca, hospedagem, visita à Assembleia |
| `elfo` | Elfo | Terreiro-Maior | visita à Assembleia |
| `anao` | Anão | Tabuleiro-Velho, Praia-Pardo | ofício de pedra, acerto de carga |
| `pequenino` | Pequenino | Rasovau | troca, recados |
| `draconato` | Draconato | Encosta-Aberta | passagem, vigia |
| `goliath` | Altaneiro | Encosta-Aberta | passagem, vigia |
| `kenku` | Corvídeo | Rasovau | recados, troca |
| `satiro` | Silvano Festivo | Vazante | acompanha a marcha |
| `minotauro` | Labiríntico | Encosta-Aberta | passagem, guia |
| `kobold` | Escamado Miúdo | Praia-Pardo | acerto de carga, visitante da Descida |
| `homem-rato` | Ratoide | Alvasal | caravana |
| `feerico` | Feérico | Vazante | acompanha a marcha |
| `sombrio` | Umbral | Alvasal | caravana |
| `plantae` | Verdejante | Gramal | pasto |
| `licantropo` | Metamorfo Lupino | Alvasal | caravana; **não é Lupino** |
| `gigante` | Giganteano | Tabuleiro-Velho | ofício, guarda de muro |
| `felino-das-nuvens` | Felino das Nuvens | Gramal | caça |
| `tiefling` | Nascido do Crepúsculo | sem lugar fixo | passagem avulsa |
| `bugbear` | Ursino Sombrio | sem lugar fixo | passagem avulsa; **não é Ursino** |
| `elemental-terra` | Nascido da Rocha | sem lugar fixo | passagem avulsa |
| `elemental-fogo` | Nascido da Brasa | sem lugar fixo | passagem avulsa |
| `elemental-ar` | Nascido do Vento | sem lugar fixo | passagem avulsa |
| `vampirico` | Noctívago | sem lugar fixo | passagem avulsa |
| `metamorfo` | Moldável | sem lugar fixo | passagem avulsa |
| `aracnideo` | Aracnídeo | sem lugar fixo | passagem avulsa |

17 com lugar, 8 sem lugar fixo (só viajantes avulsos, sem residir e sem rota). **Gnomos e construtos** não têm linha: a matriz lhes dá \"—\" e eles aparecem só como visitantes de passagem em Praia-Pardo (D-103). **Elfo e Humano** em Terreiro-Maior são visitantes individuais: nenhum NPC de Aeltharyn ou do Pacto Humano aparece.

## 6. Papéis por povo e lacunas **[PROPOSTO PV-06]**

Papel = herói, guardião ou benfeitor/liderança já nomeado (doc 01 parte 2, doc 04). `L` = herói lendário falecido; `V` = herói vivo.

| Povo | Herói | Guardião | Benfeitor ou liderança |
|---|---|---|---|
| Orcano | Hukk Pedra-de-Divisa (L) | Yorna Palavra-Firme | Bolgra Forja-de-Campo |
| Felídeo Errante | Tessun Salto-Calado (V) | nenhum nomeado | nenhum nomeado |
| Centauro | nenhum nomeado | Kharos Casco-de-Marcha | Cadrek Cerca-Aberta |
| Goblin | Nokk Peso-Alheio (L) | Zinu Troca-Justa | Grebb Balança-Justa |
| Goblin de Legião | nenhum nomeado | Gruzk Estandarte-Baixo | nenhum nomeado |
| Vulpino | Jessik Trato-Primeiro (L) | nenhum nomeado | Quira Recado-Certo |
| Leporino | nenhum nomeado | Tibbe Salto-Curto | Lumbo Pedra-de-Baixo (liderança) |
| Ursino | nenhum nomeado | Moura Mel-Guardado | nenhum nomeado |
| Lupino | Urrek Bando-Parado (L); Rukhan Retorno-Certo (V) | nenhum nomeado | Aruun Poço-Compartilhado |
| Porcino | Bruga Colheita-Salva (L) | nenhum nomeado | Pocha Ninhada-Farta; Ghorla Fartura-Dividida |
| Cervino | nenhum nomeado | Sendro Clareira-Vigia | Cassan Galhada-Velha |
| Insetoide | Zzirr Favo-Dividido (L) | Vrissk Colônia-Quieta | nenhum nomeado |

Lacunas **ficam abertas** (D-107 só exige tríade por lugar, já cumprida em 8 de 8): 5 povos sem herói (Centauro, Goblin de Legião, Leporino, Ursino, Cervino), 4 sem guardião (Felídeo Errante, Vulpino, Lupino, Porcino) e 4 sem benfeitor ou liderança (Felídeo Errante, Goblin de Legião, Ursino, Insetoide). Nenhum NPC é criado aqui para fechá-las (D-110, opção C).

## 7. Representação e o que este documento não decide **[PROPOSTO PV-07]**

- Nenhum povo é só vilão, só herói ou só funcionário; 7 dos 12 têm herói (vivo ou lendário), 8 têm guardião e 8 têm benfeitor ou liderança (§6). Raça não determina cargo (doc 01, parte 2 §4).
- **Cultura, religião e língua** por povo: não decididas (M-05 §7); nomes: M-10 §9.
- **Quantidade de gente e proporção entre povos:** R4-3.2; nada de \"maioria\" de um povo sem decisão. **Relação entre povos nos lugares:** R4-4.3.
- **Povos de Aeltharyn** (Orcano, Felídeo Errante e Centauro em linhagem diferente; Elfo e Humano como visitantes): nenhuma cidade ou NPC de Aeltharyn alterado; **nada liga um povo do Vale ao Conflito G, à Neblina Longa ou a desaparecimento algum**.
- **Povos das Coroas** (Anão, Escamado Miúdo; Gnomo e Construto como visitantes) e **do Véu** (Ratoide, Umbral, Metamorfo Lupino, Nascido do Crepúsculo, Ursino Sombrio e outros): só forasteiros ou viajantes avulsos; nada liga um povo do Véu à Rota do Sal além do que M-05 §5 já diz.
- **Humanos e Pacto Humano:** Humano é forasteiro avulso; o reino 5 não é descrito.

## 8. Conferências **[PROPOSTO PV-08]**

| Item | Resultado |
|---|---|
| Nativos | 12 de 12, todos em ID do catálogo, todos em ao menos 1 lugar |
| Forasteiros | 25 de 25 (17 com lugar, 8 sem lugar fixo); gnomo e construto só visitantes (D-103) |
| Sub-raças | 6 usadas, todas existem no catálogo com a raça-mãe correta; 2 existentes e não usadas, com motivo |
| P-3 | linhagens do Vale distintas das de Aeltharyn (`orcano-das-estepes`, `felideo-das-dunas`, `centauro-das-pradarias`) |
| Matriz × fluxo (M-05 §5) | Descida: anão e kobold F, gnomo e construto visitantes; Rota do Sal: goblin N, ratoide F; Corredores Largos: centauro, felídeo, cervino e lupino N |
| Lugares com ≥ 2 nativos | 8 de 8 |
| NPCs novos | 0 |
| Mestiço alocado | 0 |
| Número, rota ou tempo | 0 |

## 9. Decisão pendente

**D-110:** matriz de povos, sub-raças, linhagens do Vale (P-3), 25 forasteiros (8 sem lugar fixo) e lacunas de papéis abertas. **ASSUMINDO:** onde cada sub-raça aparece, os motivos dos forasteiros e a escolha da outra sub-raça de cada par compartilhado com Aeltharyn.
