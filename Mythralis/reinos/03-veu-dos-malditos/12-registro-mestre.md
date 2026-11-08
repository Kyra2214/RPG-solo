# 12 — Registro mestre de entidades do reino 3 (R3-2.1, versão 1)

**Estado:** rascunho de R3-2.1 para aprovação do autor (D-75) · **Data:** 2026-11-05
**Etiqueta:** **[PROPOSTO]**; **não é cânone novo**: só organiza o que já está nos docs 01, 03, 04, 05 e 10 (D-68 a D-74). Onde um dado não existe, a célula traz `—`; nada foi inventado. Os 18 NPCs sem ficha (tríade e heróis lendários) **ganharam ID agora**, pelo nome já escrito nos docs 01 e 10, sem criar pessoa nova.
**Uso:** fonte única de IDs do reino 3. Antes de criar NPC, lugar ou facção, procure aqui. **Um ID nunca é reutilizado.** Relógios (`clock.*`) ficam em `06` e entram em `12-relogios` no R3-5.1; mistérios, conflitos e oportunidades usam rótulos MV, CF e OV até o R3-5.5.
**Arquivo irmão:** `12-registro-mestre.csv` (formato de D-21: `tipo,id,nome,situacao,id_catalogo,cidade,papel,povo,vivo,extra,ficha_doc,povo_id`). **ASSUMINDO:** `id_catalogo` fica vazio (o catálogo não tem figuras nomeadas) e `cidade` guarda o ID do lugar principal (os oito `city.*` por compatibilidade, D-66). Raça, sub-raça, classe e profissão por NPC: R3-2.3.
**Arquivos irmãos (R3-2.2):** `12-facoes.csv` (11 facções: id, nome, liderança, objetivo, recursos, métodos, tensão, aliados, rivais, presença) e `12-assentamentos.csv` (os 8 lugares com cidade-mãe, liderança, obrigação, interesse e tensão), no molde do reino 2 (§7).
**Fases seguintes:** catálogo (R3-2.3), conferência cruzada (R3-2.4).

## 1. Resumo

| Tipo | Quantidade |
|---|---:|
| Lugares principais (`city.*`) | 8 |
| Facções | 11 |
| Assentamentos | 0 (D-66: sem assentamentos próprios) |
| Pessoas (NPCs) distintas | 30 |

| Situação das pessoas | Qtd |
|---|---:|
| proposto, ficha completa (D-70) | 12 |
| proposto, só tríade, sem ficha | 7 |
| proposto, herói lendário falecido (D-70) | 6 |
| proposto, herói lendário falecido (D-73) | 5 |

19 vivos e 11 falecidos (lendas). Pessoas por povo (ID do catálogo):

| Povo | ID | Qtd |
|---|---|---:|
| Ratoide | `homem-rato` | 4 |
| Corvídeo | `kenku` | 4 |
| Nascido do Crepúsculo | `tiefling` | 3 |
| Ursino Sombrio | `bugbear` | 3 |
| Moldável | `metamorfo` | 3 |
| Aracnídeo | `aracnideo` | 3 |
| Escamado | `lizardino` | 2 |
| Umbral | `sombrio` | 2 |
| Onírico | `onirico` | 2 |
| Noctívago | `vampirico` | 2 |
| Metamorfo Lupino | `licantropo` | 1 |
| Humano (forasteiro, ASSUMINDO, D-70) | `humano` | 1 |

Os 11 nativos têm pelo menos uma pessoa. Nenhum mestiço (D-64).

## 2. Lugares principais

| ID | Nome | Tipo | Ref. |
|---|---|---|---|
| `city.morroveu` | Morrovéu — A Corte do Rei Vencedor | cidade-fortaleza num morro entre charcos; capital do rei | V-01 |
| `city.lamaluz` | Lamaluz — A Cidade das Lamparinas | cidade de charco sobre estacas e passarelas | V-02 |
| `city.fozbreu` | Fozbreu — O Porto da Foz | porto na foz do Argente, sobre o Mar Austral | V-03 |
| `city.beira-sal` | Beira-Sal — O Posto das Caravanas | posto de caravanas na borda do Ermo de Sal | V-04 |
| `city.selovale` | Selovale — O Vale dos Pactos Velhos | vale de pactos antigos, com cidade-arquivo | V-05 |
| `city.borda-mansa` | Borda-Mansa — O Posto da Orla Norte | posto de orla com vigia de neblina | V-06 |
| `city.fio-longo` | Fio-Longo — A Casa dos Sonhos | vila-hospedaria de repouso e sonhos | V-07 |
| `city.candeia-morta` | Candeia-Morta — A Cidade das Vigílias | cidade noturna de vigílias e despedidas | V-08 |

## 3. Facções

| ID | Nome | Ref. |
|---|---|---|
| `faction.guarda-do-rei` | Guarda do Rei | F-01 (doc 04 §9) |
| `faction.defensores-da-paz` | Defensores da Paz | F-02 (doc 04 §9) |
| `faction.memoria-das-provacoes` | Memória das Provações | F-03 (doc 04 §9) |
| `faction.pilotos-dos-canais` | Pilotos dos Canais | F-04 (doc 04 §9) |
| `faction.guardioes-das-lamparinas` | Guardiões das Lamparinas | F-05 (doc 04 §9) |
| `faction.guias-de-sal` | Guias de Sal | F-06 (doc 04 §9) |
| `faction.casa-dos-pocos` | Casa dos Poços | F-07 (doc 04 §9) |
| `faction.vigias-da-orla` | Vigias da Orla | F-08 (doc 04 §9) |
| `faction.casa-dos-fios` | Casa dos Fios | F-09 (doc 04 §9) |
| `faction.irmandade-das-despedidas` | Irmandade das Despedidas | F-10 (doc 04 §9) |
| `faction.turnos-da-vigilia` | Turnos da Vigília | F-11 (doc 04 §9) |

## 4. Pessoas

| ID | Nome | Lugar | Povo | Povo (ID) | Vivo | Situação |
|---|---|---|---|---|---|---|
| `npc.morvan-voz-baixa` | Morvan Voz-Baixa | `city.morroveu` | Humano | `humano` | sim | proposto, ficha completa (D-70) |
| `npc.orsk-cicatriz-de-bando` | Orsk Cicatriz-de-Bando | `city.morroveu` | Ursino Sombrio | `bugbear` | sim | proposto, ficha completa (D-70) |
| `npc.cerrane-heranca-firme` | Cerrane Herança-Firme | `city.morroveu` | Nascido do Crepúsculo | `tiefling` | sim | proposto, ficha completa (D-70) |
| `npc.ilkar-turno-aberto` | Ilkar Turno-Aberto | `city.candeia-morta` | Noctívago | `vampirico` | sim | proposto, ficha completa (D-70) |
| `npc.tazk-pedra-ao-sol` | Tazk Pedra-ao-Sol | `city.lamaluz` | Escamado | `lizardino` | sim | proposto, ficha completa (D-70) |
| `npc.rusk-rede-da-barra` | Rusk Rede-da-Barra | `city.fozbreu` | Ratoide | `homem-rato` | sim | proposto, ficha completa (D-70) |
| `npc.vurga-sede-vencida` | Vurga Sede-Vencida | `city.beira-sal` | Metamorfo Lupino | `licantropo` | sim | proposto, ficha completa (D-70) |
| `npc.teshi-no-de-poco` | Teshi Nó-de-Poço | `city.beira-sal` | Aracnídeo | `aracnideo` | sim | proposto, ficha completa (D-70) |
| `npc.yeshe-forma-emprestada` | Yeshe Forma-Emprestada | `city.selovale` | Moldável | `metamorfo` | sim | proposto, ficha completa (D-70) |
| `npc.krell-eco-da-vigia` | Krell Eco-da-Vigia | `city.borda-mansa` | Corvídeo | `kenku` | sim | proposto, ficha completa (D-70) |
| `npc.ondra-fio-de-sono` | Ondra Fio-de-Sono | `city.fio-longo` | Onírico | `onirico` | sim | proposto, ficha completa (D-70) |
| `npc.dhun-contorno-quieto` | Dhun Contorno-Quieto | `city.candeia-morta` | Umbral | `sombrio` | sim | proposto, ficha completa (D-70) |
| `npc.ardh-primeiro-perdedor` | Ardh Primeiro-Perdedor | `city.morroveu` | Nascido do Crepúsculo | `tiefling` | não | proposto, herói lendário (D-70) |
| `npc.nuvik-canal-torto` | Nuvik Canal-Torto | `city.lamaluz` | Ratoide | `homem-rato` | sim | proposto, só tríade (sem ficha) |
| `npc.lorra-emprestimo-justo` | Lorra Empréstimo-Justo | `city.lamaluz` | Moldável | `metamorfo` | sim | proposto, só tríade (sem ficha) |
| `npc.kerrak-eco-do-mar` | Kerrak Eco-do-Mar | `city.fozbreu` | Corvídeo | `kenku` | não | proposto, herói lendário (D-70) |
| `npc.ssavi-lodo-firme` | Ssavi Lodo-Firme | `city.fozbreu` | Escamado | `lizardino` | sim | proposto, só tríade (sem ficha) |
| `npc.brisk-poco-cheio` | Brisk Poço-Cheio | `city.beira-sal` | Ratoide | `homem-rato` | sim | proposto, só tríade (sem ficha) |
| `npc.ethra-vale-guardado` | Ethra Vale-Guardado | `city.selovale` | Nascido do Crepúsculo | `tiefling` | não | proposto, herói lendário (D-70) |
| `npc.garrun-toca-funda` | Garrun Toca-Funda | `city.selovale` | Ursino Sombrio | `bugbear` | sim | proposto, só tríade (sem ficha) |
| `npc.piok-fogueira-primeira` | Piok Fogueira-Primeira | `city.borda-mansa` | Corvídeo | `kenku` | não | proposto, herói lendário (D-70) |
| `npc.szia-fio-aberto` | Szia Fio-Aberto | `city.borda-mansa` | Aracnídeo | `aracnideo` | sim | proposto, só tríade (sem ficha) |
| `npc.noor-despertar-primeiro` | Noor Despertar-Primeiro | `city.fio-longo` | Onírico | `onirico` | não | proposto, herói lendário (D-70) |
| `npc.rhek-relato-manso` | Rhek Relato-Manso | `city.fio-longo` | Corvídeo | `kenku` | sim | proposto, só tríade (sem ficha) |
| `npc.sorren-vigilia-primeira` | Sorren Vigília-Primeira | `city.candeia-morta` | Noctívago | `vampirico` | não | proposto, herói lendário (D-70) |
| `npc.mursk-juramento-de-bando` | Mursk Juramento-de-Bando | — | Ursino Sombrio | `bugbear` | não | proposto, herói lendário (D-73) |
| `npc.zibb-rede-primeira` | Zibb Rede-Primeira | — | Ratoide | `homem-rato` | não | proposto, herói lendário (D-73) |
| `npc.aun-silencio-primeiro` | Aun Silêncio-Primeiro | — | Umbral | `sombrio` | não | proposto, herói lendário (D-73) |
| `npc.lish-rosto-primeiro` | Lish Rosto-Primeiro | — | Moldável | `metamorfo` | não | proposto, herói lendário (D-73) |
| `npc.xalu-no-primeiro` | Xalu Nó-Primeiro | — | Aracnídeo | `aracnideo` | não | proposto, herói lendário (D-73) |

## 5. Conferências

- **49 entidades** (8 lugares, 11 facções, 30 pessoas); **0 ID duplicado**; **0 ID de lugar, facção ou pessoa citado nos docs 01, 03, 04, 05, 06 e 10 sem registro**.
- **0 colisão** de ID com os registros mestres dos reinos 1 e 2; 12 `povo_id` conferidos no `personagem-catalogo.json` (0 fora do catálogo).
- Nenhum documento de Aeltharyn ou das Coroas alterado.

## 6. Facções e lugares como dados (R3-2.2)

- `12-facoes.csv`: 11 linhas, 0 campo vazio. Liderança, objetivo, métodos, aliados e rivais vêm do doc 04 §9; presença por lugar, do doc 04 §9.2. **`recursos`** é `desconhecido` (o doc 04 não lista recursos de facção; não se inventa). **`tensao`** é a tensão do lugar-sede da facção (doc 03, ASSUMINDO: leitura editorial). **`presenca_assentamentos`** é `—` (D-66: sem assentamentos próprios).
- Aliados e rivais em IDs de facção: 0 assimetria, 0 aliado que seja rival, 0 ID inexistente. 3 `rivais` `desconhecido`, com o motivo do doc 04: `faction.vigias-da-orla` (o doc 01 não põe a orla contra ninguém), `faction.casa-dos-fios` (parentes de hóspedes não formam facção), `faction.turnos-da-vigilia` (a divisão do Pacto da Vigília ainda não separou lados).
- `12-assentamentos.csv`: 8 linhas (os oito lugares), 0 campo vazio. **`cidade_mae` é `—` nos oito** (D-66; o elo comum é a coroa e o pacto, doc 03). `lideranca_id` é o Pactuário-mor de cada lugar (Morvan em Morrovéu); todos existem no registro.

## 7. Decisão pendente

**D-75** (registro) **e D-76** (facções e lugares como dados): aprovar o registro (49 entidades) e os 18 IDs novos de NPCs sem ficha. Opções: **A.** aprovar; **B.** trocar o slug de algum ID (diga qual). **Recomendação: A.**
