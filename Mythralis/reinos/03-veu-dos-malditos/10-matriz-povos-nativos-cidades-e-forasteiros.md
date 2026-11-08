# Véu dos Malditos — matriz de povos nativos, lugares e forasteiros (doc 10)

**Estado:** rascunho de R3-1.6 (D-73 aceita pela continuação) · **Data:** 2026-11-05
**Etiqueta:** tudo **[PROPOSTO P-01 a P-08]**. Cânone de apoio: M-05 (D-24: 11 nativos e 17 forasteiros dos Malditos), doc 01 (partes 1 a 3), docs 03 a 05, `referencia/dados-brutos/personagem-catalogo.json` (IDs), contrato de fronteira com Aeltharyn.
**Regras seguidas:** povo em **ID do catálogo**; nenhuma quantidade de gente (R3-3.2); nenhuma rota ou tempo (R3-3.1); nenhum povo é cidadão, vilão ou leal por natureza (W-01, regra 4); sub-raça só do catálogo; mestiço pela regra D-64 (não alocado); forasteiro não cria rota nem vínculo com outro reino; único NPC novo: 5 heróis lendários falecidos (§6.1).
**Campos adiados:** conferência final de IDs e classes por NPC (R3-2.3); competências (R3-4.2).

## 1. Os onze povos nativos **[PROPOSTO P-01]**

Nativo = povo de referência do reino, não cidadania nem lealdade (M-05 §1). Lugares vêm da parte 1 do doc 01.

| Nº | Povo | ID do catálogo | Lugares onde está | Identidade no reino (uma linha) |
|---|---|---|---|---|
| 1 | Nascido do Crepúsculo | `tiefling` | Morrovéu, Selovale, Candeia-Morta | presente na corte, no arquivo de selos e nas vigílias |
| 2 | Corvídeo | `kenku` | Fozbreu, Borda-Mansa, Fio-Longo | voz do porto, da orla e dos relatos |
| 3 | Ursino Sombrio | `bugbear` | Morrovéu, Selovale | guarda da corte e cópia de selos |
| 4 | Escamado | `lizardino` | Lamaluz, Fozbreu | povo de charco e de barra |
| 5 | Ratoide | `homem-rato` | Lamaluz, Fozbreu, Beira-Sal | canais, porto e poços |
| 6 | Umbral | `sombrio` | Morrovéu, Borda-Mansa, Candeia-Morta | corte, orla e despedidas |
| 7 | Onírico | `onirico` | Fio-Longo | repouso e sonho |
| 8 | Noctívago | `vampirico` | Candeia-Morta | noite e vigília |
| 9 | Metamorfo Lupino | `licantropo` | Beira-Sal | guias e bandos do Ermo |
| 10 | Moldável | `metamorfo` | Lamaluz, Selovale, Fio-Longo | canais, selos e hospedagem |
| 11 | Aracnídeo | `aracnideo` | Beira-Sal, Borda-Mansa | poços e orla |

### 1.1 O que nenhum povo é
Nenhum povo é maldito, mau, cidadão ou leal por natureza. **"Maldito" é nome do reino, não sentença** (W-01). Necromancia é ofício do Lord, não traço de povo. Onírico, Noctívago, Lupino e Aracnídeo aparecem em **um ou dois** lugares por desenho do doc 01, não por serem "estranhos".

## 2. Sub-raças do catálogo **[PROPOSTO P-02]**

Só IDs que existem no JSON. Onde cada uma aparece é ASSUMINDO pelo nome; a troca é livre.

| Povo | Sub-raça (ID) | Onde aparece |
|---|---|---|
| Nascido do Crepúsculo | `crepuscular-de-chifres` | Morrovéu, Selovale |
| Nascido do Crepúsculo | `crepuscular-de-cauda` | Selovale, Candeia-Morta |
| Corvídeo | `corvideo-dos-telhados` | Fozbreu, Fio-Longo |
| Corvídeo | `corvideo-das-ruinas` | Borda-Mansa |
| Ursino Sombrio | `ursino-da-sombra` | Morrovéu |
| Ursino Sombrio | `ursino-das-colinas` | Selovale |
| Escamado | `escamado-dos-mangues` | Lamaluz, Fozbreu |
| Escamado | `escamado-do-solar` | Lamaluz |
| Ratoide | `ratoide-dos-esgotos` | Lamaluz |
| Ratoide | `ratoide-dos-mercados` | Fozbreu, Beira-Sal |

Sem sub-raça no catálogo (não se inventa): Umbral, Onírico, Noctívago, Metamorfo Lupino, Moldável, Aracnídeo. Sub-raça de cada NPC: R3-2.3.

## 3. Mestiços **[D-64]**

Não são alocados a reino, lugar ou facção. Nascem no lugar de qualquer um dos pais, em qualquer par de raças ou sub-raças do catálogo; o jogador escolhe. Nenhuma tabela de mestiços aqui.

## 4. Lugares × povos **[PROPOSTO P-04]**

Povos presentes conforme o doc 01; nenhum lugar tem menos de dois povos.

| Lugar | Nativos | Forasteiros |
|---|---|---|
| Morrovéu | Crepúsculo; Umbral; Ursino Sombrio | Humano |
| Lamaluz | Escamado; Ratoide; Moldável | Escamado Miúdo; Pequenino |
| Fozbreu | Escamado; Ratoide; Corvídeo | Tartarino; Goblin; Humano (e visitantes da Descida, doc 05 §3) |
| Beira-Sal | Ratoide; Metamorfo Lupino; Aracnídeo | Goblin; Vulpino; Humano |
| Selovale | Crepúsculo; Moldável; Ursino Sombrio | Draconato; Feérico; Humano |
| Borda-Mansa | Corvídeo; Umbral; Aracnídeo | Micélioide; Felídeo Errante; Corvário |
| Fio-Longo | Onírico; Moldável; Corvídeo | Feérico; Pequenino; Humano |
| Candeia-Morta | Noctívago; Umbral; Crepúsculo | Humano; Nascido da Brasa |

## 5. Forasteiros **[PROPOSTO P-05]**

Os 17 de M-05 §4 (coluna Malditos = F). **Forasteiro** = origem fora do Véu: residente, viajante, mercador, refugiado ou estudioso; **motivo** é possibilidade, não destino do povo. **Origem e caminho: desconhecidos**; nenhuma linha cria rota, tratado ou comunidade grande, nem representa outro reino (contrato F14).

| ID | Povo | Onde aparece | Motivos possíveis |
|---|---|---|---|
| `humano` | Humano | Morrovéu, Fozbreu, Beira-Sal, Selovale, Fio-Longo, Candeia-Morta | comércio, ofício, desafio à coroa, família mista |
| `pequenino` | Pequenino | Lamaluz, Fio-Longo | hospedagem, comércio |
| `kobold` | Escamado Miúdo | Lamaluz | charco, ofício, visitante da Descida |
| `tortle` | Tartarino | Fozbreu | porto, pesca de costa |
| `goblin` | Goblin | Fozbreu, Beira-Sal | cais, caravana |
| `homem-raposa` | Vulpino | Beira-Sal | caravana, acerto |
| `draconato` | Draconato | Selovale | arquivo, juramento |
| `feerico` | Feérico | Selovale, Fio-Longo | selo, sonho |
| `fungal` | Micélioide | Borda-Mansa | vigia, estudo da névoa |
| `tabaxi` | Felídeo Errante | Borda-Mansa | viagem, vigia |
| `homem-corvo` | Corvário | Borda-Mansa | viagem; **não é Corvídeo** (D-01 aberta) |
| `elemental-fogo` | Nascido da Brasa | Candeia-Morta | luz, forja de vigia |
| `orcano` | Orcano | sem lugar fixo | passagem avulsa (ASSUMINDO) |
| `minotauro` | Labiríntico | sem lugar fixo | passagem avulsa (ASSUMINDO) |
| `homem-lobo` | Lupino | sem lugar fixo | passagem avulsa; **não é Metamorfo Lupino** |
| `plantae` | Verdejante | sem lugar fixo | passagem avulsa (ASSUMINDO) |
| `insetoide` | Insetoide | sem lugar fixo | passagem avulsa (ASSUMINDO) |

12 com lugar, 5 sem lugar fixo (só viajantes avulsos, sem residir e sem rota). **Anões, gnomos e construtos** não têm linha: são só visitantes de passagem em Fozbreu (doc 01 §3, D-68).

## 6. Papéis por povo e lacunas **[PROPOSTO P-06]**

Papel = herói, guardião ou benfeitor/liderança já nomeado (doc 01 parte 2, doc 04). `L` = herói lendário falecido.

| Povo | Herói | Guardião | Benfeitor ou liderança |
|---|---|---|---|
| Nascido do Crepúsculo | Ardh Primeiro-Perdedor (L); Ethra Vale-Guardado (L) | nenhum nomeado | Cerrane Herança-Firme |
| Corvídeo | Kerrak Eco-do-Mar (L); Piok Fogueira-Primeira (L) | Krell Eco-da-Vigia | Rhek Relato-Manso |
| Ursino Sombrio | Mursk Juramento-de-Bando (L, §6.1) | Orsk Cicatriz-de-Bando | Garrun Toca-Funda |
| Escamado | Tazk Pedra-ao-Sol | nenhum nomeado | Ssavi Lodo-Firme |
| Ratoide | Zibb Rede-Primeira (L, §6.1) | Nuvik Canal-Torto; Rusk Rede-da-Barra | Brisk Poço-Cheio |
| Umbral | Aun Silêncio-Primeiro (L, §6.1) | nenhum nomeado | Dhun Contorno-Quieto |
| Onírico | Noor Despertar-Primeiro (L) | Ondra Fio-de-Sono | nenhum nomeado |
| Noctívago | Sorren Vigília-Primeira (L) | Ilkar Turno-Aberto | nenhum nomeado |
| Metamorfo Lupino | Vurga Sede-Vencida | nenhum nomeado | nenhum nomeado |
| Moldável | Lish Rosto-Primeiro (L, §6.1) | Yeshe Forma-Emprestada | Lorra Empréstimo-Justo |
| Aracnídeo | Xalu Nó-Primeiro (L, §6.1) | Teshi Nó-de-Poço | Szia Fio-Aberto |

Lacunas de guardião e benfeitor **ficam abertas** (D-70 só exige tríade por lugar).

### 6.1 Os cinco heróis lendários novos (falecidos) **[PROPOSTO]**
Preenchem o herói dos cinco povos que não tinham. **Figuras de lenda, sem feito, era ou lugar fixados** (`desconhecidos`); lenda não é fato.
- **Mursk Juramento-de-Bando** (`npc.mursk-juramento-de-bando`), Ursino Sombrio.
- **Zibb Rede-Primeira** (`npc.zibb-rede-primeira`), Ratoide.
- **Aun Silêncio-Primeiro** (`npc.aun-silencio-primeiro`), Umbral.
- **Lish Rosto-Primeiro** (`npc.lish-rosto-primeiro`), Moldável.
- **Xalu Nó-Primeiro** (`npc.xalu-no-primeiro`), Aracnídeo.

## 7. Representação e o que este documento não decide **[PROPOSTO P-07]**

- Nenhum povo é só vilão, só herói ou só funcionário; os 11 têm herói (vivo ou lendário), 7 têm guardião e 8 têm benfeitor ou liderança (§6).
- **Cultura, religião e língua** por povo: não decididas (M-05 §7); nomes: M-10.
- **Quantidade de gente e proporção entre povos:** R3-3.2. **Relação entre povos nos lugares:** R3-4.3.
- **Povos de Aeltharyn** (Elfo, Anão, Gnomo, Pequenino, Goblin, Orcano e outros): só forasteiros avulsos; nenhuma cidade ou NPC de Aeltharyn alterado; **nada liga um povo do Véu a Brumafunda, ao Bosque dos Sussurros ou a desaparecimento algum**.
- **Povos das Coroas** (Anão, Gnomo, Construto Desperto): só como visitantes em Fozbreu.

## 8. Conferências **[PROPOSTO P-08]**

| Item | Resultado |
|---|---|
| Nativos | 11 de 11, todos em ID do catálogo, todos em ao menos 1 lugar |
| Forasteiros | 17 de 17 (12 com lugar, 5 sem lugar fixo) |
| Sub-raças | 10, todas existem no catálogo |
| Lugares com ≥ 2 povos | 8 de 8 |
| Heróis lendários novos | 5 (0 colisão de nome ou ID com os reinos 1 e 2) |
| Mestiço alocado | 0 |
| Número, rota ou tempo | 0 |

## 9. Decisão pendente

**D-73:** matriz de povos, sub-raças, forasteiros (5 sem lugar fixo) e 5 heróis lendários novos.
