# Alinhamento: povos do Véu dos Malditos × catálogo do RPG Solo

**Estado:** rascunho de R3-2.3 para aprovação do autor (D-77) · **Data:** 2026-11-05 · **Etiqueta:** [PROPOSTO]; não é cânone novo, só confere IDs e atribui, por pessoa, raça, sub-raça, classe e profissão do catálogo.
Conferência entre `reinos/03-veu-dos-malditos/` (doc 10 e `12-registro-mestre.csv`) e `dados-brutos/personagem-catalogo.json`, no molde de `ALINHAMENTO_COROAS.md`.

## Resultado


- **11 de 11** nativos existem como raça no catálogo, com o nome de exibição igual ao dos documentos.
- **10 sub-raças** usadas (doc 10 §2): todas existem, com a raça-mãe correta.
- **17 forasteiros** (doc 10 §5): todos existem como raça, com o mesmo nome do catálogo. **Mestiços:** não são alocados (D-64).
- **30 pessoas** (registro mestre): 0 ID de raça, sub-raça, classe ou profissão fora do catálogo; toda sub-raça atribuída aparece no lugar da pessoa (doc 10 §2).

## 1. Os 11 nativos

| Nº | Povo no doc | ID no catálogo | Nome no catálogo | Confere |
|---|---|---|---|---|
| 1 | Nascido do Crepúsculo | `tiefling` | Nascido do Crepúsculo | sim |
| 2 | Corvídeo | `kenku` | Corvídeo | sim |
| 3 | Ursino Sombrio | `bugbear` | Ursino Sombrio | sim |
| 4 | Escamado | `lizardino` | Escamado | sim |
| 5 | Ratoide | `homem-rato` | Ratoide | sim |
| 6 | Umbral | `sombrio` | Umbral | sim |
| 7 | Onírico | `onirico` | Onírico | sim |
| 8 | Noctívago | `vampirico` | Noctívago | sim |
| 9 | Metamorfo Lupino | `licantropo` | Metamorfo Lupino | sim |
| 10 | Moldável | `metamorfo` | Moldável | sim |
| 11 | Aracnídeo | `aracnideo` | Aracnídeo | sim |

### 1.1 Sub-raças em uso

| Sub-raça (ID) | Raça-mãe | Nome no catálogo | Onde aparece (doc 10 §2) |
|---|---|---|---|
| `crepuscular-de-chifres` | `tiefling` | Crepuscular de Chifres | Morrovéu, Selovale |
| `crepuscular-de-cauda` | `tiefling` | Crepuscular de Cauda | Selovale, Candeia-Morta |
| `corvideo-dos-telhados` | `kenku` | Corvídeo dos Telhados | Fozbreu, Fio-Longo |
| `corvideo-das-ruinas` | `kenku` | Corvídeo das Ruínas | Borda-Mansa |
| `ursino-da-sombra` | `bugbear` | Ursino da Sombra | Morrovéu |
| `ursino-das-colinas` | `bugbear` | Ursino das Colinas | Selovale |
| `escamado-dos-mangues` | `lizardino` | Escamado dos Mangues | Lamaluz, Fozbreu |
| `escamado-do-solar` | `lizardino` | Escamado do Solar | Lamaluz |
| `ratoide-dos-esgotos` | `homem-rato` | Ratoide dos Esgotos | Lamaluz |
| `ratoide-dos-mercados` | `homem-rato` | Ratoide dos Mercados | Fozbreu, Beira-Sal |

Sem sub-raça no catálogo (não se inventa): Umbral, Onírico, Noctívago, Metamorfo Lupino, Moldável, Aracnídeo.

### 1.2 Mestiços (D-64)

Não alocados a reino, lugar ou facção; os 20 exemplos do catálogo não são conferidos por reino.

### 1.3 Forasteiros (17)

| ID | Nome no doc | Nome no catálogo | Confere |
|---|---|---|---|
| `humano` | Humano | Humano | sim |
| `pequenino` | Pequenino | Pequenino | sim |
| `kobold` | Escamado Miúdo | Escamado Miúdo | sim |
| `tortle` | Tartarino | Tartarino | sim |
| `goblin` | Goblin | Goblin | sim |
| `homem-raposa` | Vulpino | Vulpino | sim |
| `draconato` | Draconato | Draconato | sim |
| `feerico` | Feérico | Feérico | sim |
| `fungal` | Micélioide | Micélioide | sim |
| `tabaxi` | Felídeo Errante | Felídeo Errante | sim |
| `homem-corvo` | Corvário | Corvário | sim |
| `elemental-fogo` | Nascido da Brasa | Nascido da Brasa | sim |
| `orcano` | Orcano | Orcano | sim |
| `minotauro` | Labiríntico | Labiríntico | sim |
| `homem-lobo` | Lupino | Lupino | sim |
| `plantae` | Verdejante | Verdejante | sim |
| `insetoide` | Insetoide | Insetoide | sim |

## 2. As 30 pessoas

**ASSUMINDO:** classe e profissão são leitura editorial do papel já escrito (cargo, ofício, lenda); só dizem a que ficha do catálogo a pessoa se parece. Onde não há ofício escrito, a profissão é `—` (não se inventa). Sub-raça só onde a pessoa vive num lugar em que o doc 10 §2 a coloca; heróis lendários sem lugar (os cinco do doc 10 §6.1) ficam sem sub-raça. Competências e limites: R3-4.2.

| ID | Nome | Raça | Sub-raça | Classe | Profissão |
|---|---|---|---|---|---|
| `npc.morvan-voz-baixa` | Morvan Voz-Baixa | `humano` | — | `invocador` | `escrivao-judicial` |
| `npc.orsk-cicatriz-de-bando` | Orsk Cicatriz-de-Bando | `bugbear` | `ursino-da-sombra` | `comandante` | `escolta-profissional` |
| `npc.cerrane-heranca-firme` | Cerrane Herança-Firme | `tiefling` | `crepuscular-de-chifres` | `diplomata` | `mediador` |
| `npc.ilkar-turno-aberto` | Ilkar Turno-Aberto | `vampirico` | — | `comandante` | `escolta-profissional` |
| `npc.tazk-pedra-ao-sol` | Tazk Pedra-ao-Sol | `lizardino` | `escamado-dos-mangues` | `explorador` | `navegador` |
| `npc.rusk-rede-da-barra` | Rusk Rede-da-Barra | `homem-rato` | `ratoide-dos-mercados` | `guardiao-do-codigo` | `navegador` |
| `npc.vurga-sede-vencida` | Vurga Sede-Vencida | `licantropo` | — | `explorador` | `guia-de-deserto` |
| `npc.teshi-no-de-poco` | Teshi Nó-de-Poço | `aracnideo` | — | `guardiao-do-codigo` | `avaliador-de-materiais` |
| `npc.yeshe-forma-emprestada` | Yeshe Forma-Emprestada | `metamorfo` | — | `cronista` | `arquivista` |
| `npc.krell-eco-da-vigia` | Krell Eco-da-Vigia | `kenku` | `corvideo-das-ruinas` | `sentinela` | `observador-do-clima` |
| `npc.ondra-fio-de-sono` | Ondra Fio-de-Sono | `onirico` | — | `tecelao-de-sonhos` | `estalajadeiro` |
| `npc.dhun-contorno-quieto` | Dhun Contorno-Quieto | `sombrio` | — | `sacerdote` | `mestre-de-cerimonias` |
| `npc.ardh-primeiro-perdedor` | Ardh Primeiro-Perdedor | `tiefling` | `crepuscular-de-chifres` | `duelista` | — |
| `npc.nuvik-canal-torto` | Nuvik Canal-Torto | `homem-rato` | `ratoide-dos-esgotos` | `guardiao-do-codigo` | — |
| `npc.lorra-emprestimo-justo` | Lorra Empréstimo-Justo | `metamorfo` | — | `diplomata` | `comerciante` |
| `npc.kerrak-eco-do-mar` | Kerrak Eco-do-Mar | `kenku` | `corvideo-dos-telhados` | `marinheiro` | `navegador` |
| `npc.ssavi-lodo-firme` | Ssavi Lodo-Firme | `lizardino` | `escamado-dos-mangues` | `marinheiro` | `construtor-de-barcos` |
| `npc.brisk-poco-cheio` | Brisk Poço-Cheio | `homem-rato` | `ratoide-dos-mercados` | `sobrevivente` | `comerciante` |
| `npc.ethra-vale-guardado` | Ethra Vale-Guardado | `tiefling` | `crepuscular-de-cauda` | `guardiao-runico` | — |
| `npc.garrun-toca-funda` | Garrun Toca-Funda | `bugbear` | `ursino-das-colinas` | `guardiao` | `escriba` |
| `npc.piok-fogueira-primeira` | Piok Fogueira-Primeira | `kenku` | `corvideo-das-ruinas` | `sentinela` | — |
| `npc.szia-fio-aberto` | Szia Fio-Aberto | `aracnideo` | — | `curandeiro` | `tecelao` |
| `npc.noor-despertar-primeiro` | Noor Despertar-Primeiro | `onirico` | — | `tecelao-de-sonhos` | — |
| `npc.rhek-relato-manso` | Rhek Relato-Manso | `kenku` | `corvideo-dos-telhados` | `bardo` | `musico` |
| `npc.sorren-vigilia-primeira` | Sorren Vigília-Primeira | `vampirico` | — | `guardiao` | — |
| `npc.mursk-juramento-de-bando` | Mursk Juramento-de-Bando | `bugbear` | — | `guardiao-do-codigo` | — |
| `npc.zibb-rede-primeira` | Zibb Rede-Primeira | `homem-rato` | — | `explorador` | — |
| `npc.aun-silencio-primeiro` | Aun Silêncio-Primeiro | `sombrio` | — | `agente-das-sombras` | — |
| `npc.lish-rosto-primeiro` | Lish Rosto-Primeiro | `metamorfo` | — | `ilusionista` | — |
| `npc.xalu-no-primeiro` | Xalu Nó-Primeiro | `aracnideo` | — | `artifice` | — |

## 3. Sinônimos e nomes

- **Corvídeo (`kenku`) × Corvário (`homem-corvo`):** dois povos; nenhum texto os funde (D-01 aberta).
- **Metamorfo Lupino (`licantropo`) × Lupino (`homem-lobo`):** dois povos; o Lupino é só forasteiro avulso.
- **Nascido do Crepúsculo (`tiefling`), Ursino Sombrio (`bugbear`), Ratoide (`homem-rato`), Umbral (`sombrio`), Moldável (`metamorfo`):** nomes de exibição do catálogo, iguais aos dos documentos.

## 4. Pontos que continuam abertos

1. **Classe do Lord:** necromante não existe no catálogo; Morvan é `invocador` com `escrivao-judicial` de apoio (D-66, leitura editorial). O catálogo não muda.
2. **Humano do Lord:** `humano` como povo de Morvan é ASSUMINDO (D-70).
3. **Classe e profissão são aproximações:** a fidelidade a cada ID é do R3-4.2.
4. **Nenhum personagem do catálogo nomeado:** `id_catalogo` do registro mestre segue vazio.

## 5. Decisão pendente

**D-77:** aprovar a atribuição de raça, sub-raça, classe e profissão às 30 pessoas. Opções: **A.** aprovar; **B.** trocar atribuições (diga quais). **Recomendação: A.**

