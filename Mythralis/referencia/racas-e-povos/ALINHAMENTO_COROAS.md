# Alinhamento: povos das Coroas de Pedra × catálogo do RPG Solo

**Estado:** rascunho de R2-2.3 para aprovação do autor · **Data:** 2026-11-04 · **Etiqueta:** [PROPOSTO]; não é cânone novo, só confere IDs e atribui, por pessoa, raça, sub-raça, classe e profissão do catálogo.
Conferência entre `reinos/02-coroas-de-pedra-reino-das-montanhas/` (docs 10 e 12) e `dados-brutos/personagem-catalogo.json`, no molde de `ALINHAMENTO_AELTHARYN.md`.

## Resultado

- **12 de 12** nativos existem como raça no catálogo; o nome de exibição do catálogo é o mesmo dos documentos, com **uma exceção**: o doc chama de "Gnomo (das rochas)" o ID `gnomo`, ver ponto aberto 1.
- **13 sub-raças** usadas (doc 10 §2): todas existem, cada uma com a raça-mãe correta.
- **17 forasteiros** (doc 10 §5): todos existem, com o mesmo nome do catálogo. **Mestiços:** não são alocados (D-64); os 20 exemplos do catálogo não são conferidos por reino.
- **45 pessoas** (registro mestre): 0 ID de raça, sub-raça, classe ou profissão fora do catálogo; toda sub-raça atribuída aparece na cidade da pessoa (doc 10 §2).

## 1. Os 12 nativos

| Nº | Povo no doc | ID no catálogo | Tipo | Nome no catálogo | Confere |
|---|---|---|---|---|---|
| 1 | Anão | `anao` | races | Anão | sim |
| 2 | Gnomo (das rochas) | `gnomo` | races | Gnomo | sim, com ressalva (ponto 1) |
| 3 | Draconato | `draconato` | races | Draconato | sim |
| 4 | Altaneiro | `goliath` | races | Altaneiro | sim |
| 5 | Labiríntico | `minotauro` | races | Labiríntico | sim |
| 6 | Escamado Miúdo | `kobold` | races | Escamado Miúdo | sim |
| 7 | Nascido da Rocha | `elemental-terra` | races | Nascido da Rocha | sim |
| 8 | Nascido da Brasa | `elemental-fogo` | races | Nascido da Brasa | sim |
| 9 | Cristalino | `cristalino` | races | Cristalino | sim |
| 10 | Construto Desperto | `construto` | races | Construto Desperto | sim |
| 11 | Giganteano | `gigante` | races | Giganteano | sim |
| 12 | Felino das Nuvens | `felino-das-nuvens` | races | Felino das Nuvens | sim |

### 1.1 Sub-raças em uso

| ID | Nome no catálogo | Raça-mãe | Cidades (doc 10 §2) |
|---|---|---|---|
| `anao-das-forjas` | Anão das Forjas | `anao` | brasalume |
| `anao-das-montanhas` | Anão das Montanhas | `anao` | cumieira, pedra-ata, altavento |
| `anao-dos-abismos` | Anão dos Abismos | `anao` | rodaviva, portao-cinza |
| `gnomo-das-engrenagens` | Gnomo das Engrenagens | `gnomo` | rodaviva, barcaalta |
| `draconato-cinzento` | Draconato Cinzento | `draconato` | portao-cinza, brasalume |
| `draconato-de-cobre` | Draconato de Cobre | `draconato` | pedra-ata |
| `altaneiro-das-neves` | Altaneiro das Neves | `goliath` | altavento |
| `altaneiro-dos-penhascos` | Altaneiro dos Penhascos | `goliath` | altavento |
| `labirintico-dos-arquivos` | Labiríntico dos Arquivos | `minotauro` | sinuosa |
| `labirintico-das-fortalezas` | Labiríntico das Fortalezas | `minotauro` | sinuosa |
| `escamado-das-minas` | Escamado das Minas | `kobold` | sinuosa, portao-cinza |
| `escamado-das-ruinas` | Escamado das Ruínas | `kobold` | geodalia |

`gnomo-dos-vales` existe no catálogo e fica **reservado a Aeltharyn**; nenhuma pessoa das Coroas o usa.

### 1.2 Mestiços (D-64)

Nenhum mestiço é alocado às Coroas de Pedra. Qualquer combinação de duas raças ou sub-raças do catálogo pode nascer na cidade de um dos pais; o jogador escolhe. Regra e exemplos: `hibridos.md`. A conferência de nomes dos 8 exemplos que envolviam povos das Coroas deixou de ser necessária.

### 1.3 Forasteiros (17)

| ID | Nome no catálogo | Confere |
|---|---|---|
| `humano` | Humano | sim |
| `elfo` | Elfo | sim |
| `orcano` | Orcano | sim |
| `pequenino` | Pequenino | sim |
| `tiefling` | Nascido do Crepúsculo | sim |
| `aasimar` | Luminar | sim |
| `goblin` | Goblin | sim |
| `hobgoblin` | Goblin de Legião | sim |
| `bugbear` | Ursino Sombrio | sim |
| `homem-rato` | Ratoide | sim |
| `homem-urso` | Ursino | sim |
| `homem-lobo` | Lupino | sim |
| `homem-javali` | Porcino | sim |
| `elemental-ar` | Nascido do Vento | sim |
| `sombrio` | Umbral | sim |
| `metamorfo` | Moldável | sim |
| `ancestral-estelar` | Estelar | sim |

## 2. As 45 pessoas

**ASSUMINDO:** classe e profissão são leitura editorial do papel já escrito (cargo, ofício, lenda); só dizem a que ficha do catálogo a pessoa se parece. As competências, os limites e as capacidades delas são do R2-4.2. Sub-raça só onde a pessoa vive numa cidade em que a sub-raça aparece (doc 10 §2); `—` onde não há sub-raça aplicável.

| ID | Nome | Raça | Sub-raça | Classe | Profissão |
|---|---|---|---|---|---|
| `npc.aurea-faceta-longa` | Aurea Faceta-Longa | `cristalino` | — | `artifice` | `lapidador` |
| `npc.aurel-faceta-calma` | Aurel Faceta-Calma | `cristalino` | — | `xama` | `especialista-em-joias` |
| `npc.brakka-luz-no-portao` | Brakka Luz-no-Portão | `draconato` | `draconato-cinzento` | `sentinela` | `guia-de-montanha` |
| `npc.brundar-veio-alto` | Brundar Veio-Alto | `anao` | `anao-das-forjas` | `comandante` | `minerador` |
| `npc.brunhild-degrau-fundo` | Brunhild Degrau-Fundo | `anao` | `anao-das-montanhas` | `comandante` | `pedreiro` |
| `npc.ciri-faceta-primeira` | Ciri Faceta-Primeira | `cristalino` | — | `bardo` | `musico` |
| `npc.dois-fusos` | Dois-Fusos | `construto` | — | `artifice` | `mecanico` |
| `npc.dorn-primeira-descida` | Dorn Primeira-Descida | `anao` | — | `explorador` | `navegador` |
| `npc.embra-cinza-viva` | Embra Cinza-Viva | `elemental-fogo` | — | `batedor` | `ferreiro` |
| `npc.fenwick-mola-solta` | Fenwick Mola-Solta | `gnomo` | `gnomo-das-engrenagens` | `artifice` | `inventor` |
| `npc.gorrak-pedra-grande` | Gorrak Pedra-Grande | `gigante` | — | `comandante` | `criador-de-animais` |
| `npc.gundrik-muralha-lenta` | Gundrik Muralha-Lenta | `anao` | `anao-das-forjas` | `comandante` | `armeiro` |
| `npc.haldra-tinta-funda` | Haldra Tinta-Funda | `anao` | `anao-das-montanhas` | `cronista` | `escriba` |
| `npc.hrodgar-primeiro-fogo` | Hrodgar Primeiro-Fogo | `anao` | `anao-das-forjas` | `artifice` | `ferreiro` |
| `npc.ilmeth-curva-segura` | Ilmeth Curva-Segura | `minotauro` | `labirintico-dos-arquivos` | `cartografo` | `cartografo-de-campo` |
| `npc.iridel-luz-partida` | Iridel Luz-Partida | `cristalino` | — | `guardiao-de-reliquias` | `montador-de-gemas` |
| `npc.iskra-pena-longa` | Iskra Pena-Longa | `goliath` | `altaneiro-das-neves` | `batedor` | `mensageiro` |
| `npc.karna-brasa-mansa` | Karna Brasa-Mansa | `elemental-fogo` | — | `artifice` | `ferreiro` |
| `npc.maro-tinta-velha` | Maro Tinta-Velha | `draconato` | `draconato-de-cobre` | `cronista` | `bibliotecario` |
| `npc.maur-camada-velha` | Maur Camada-Velha | `elemental-terra` | — | `guardiao` | `pedreiro` |
| `npc.miraq-salto-de-nevoa` | Miraq Salto-de-Névoa | `felino-das-nuvens` | — | `explorador` | `guia-de-montanha` |
| `npc.nael-curva-antiga` | Nael Curva-Antiga | `minotauro` | `labirintico-das-fortalezas` | `explorador` | `guia-de-montanha` |
| `npc.naru-sombra-de-neve` | Naru Sombra-de-Neve | `felino-das-nuvens` | — | `batedor` | `cacador` |
| `npc.orla-fio-de-mapa` | Orla Fio-de-Mapa | `minotauro` | `labirintico-dos-arquivos` | `cartografo` | `cartografo-de-campo` |
| `npc.pelma-gota-medida` | Pelma Gota-Medida | `gnomo` | — | `guardiao` | `mecanico` |
| `npc.pimm-veio-pequeno` | Pimm Veio-Pequeno | `gnomo` | — | `artifice` | `mecanico` |
| `npc.pip-vapor-curto` | Pip Vapor-Curto | `kobold` | `escamado-das-minas` | `ladino` | `comerciante` |
| `npc.primeira-peca` | Primeira-Peça | `construto` | — | `cronista` | `relojoeiro` |
| `npc.quinze-pinos` | Quinze-Pinos | `construto` | — | `artifice` | `mecanico` |
| `npc.seis-eixos` | Seis-Eixos | `construto` | — | `marinheiro` | `marinheiro-de-carga` |
| `npc.sevro-selo-primeiro` | Sevro Selo-Primeiro | `anao` | `anao-das-montanhas` | `diplomata` | `advogado` |
| `npc.sharra-selo-de-voto` | Sharra Selo-de-Voto | `draconato` | `draconato-de-cobre` | `diplomata` | `estalajadeiro` |
| `npc.skit-fenda-quente` | Skit Fenda-Quente | `kobold` | `escamado-das-minas` | `ladino` | `estalajadeiro` |
| `npc.thaurak-escama-fria` | Thaurak Escama-Fria | `draconato` | `draconato-cinzento` | `comandante` | `escolta-profissional` |
| `npc.throm-juramento-alto` | Throm Juramento-Alto | `gigante` | — | `guardiao` | `arquiteto` |
| `npc.tik-fenda-funda` | Tik Fenda-Funda | `kobold` | `escamado-das-minas` | `explorador` | `cartografo-de-campo` |
| `npc.tobiah-mola-mestra` | Tobiah Mola-Mestra | `gnomo` | `gnomo-das-engrenagens` | `artifice` | `relojoeiro` |
| `npc.torgrim-corda-dupla` | Torgrim Corda-Dupla | `anao` | — | `comandante` | `cordoeiro` |
| `npc.tuck-lente-torta` | Tuck Lente-Torta | `gnomo` | — | `artifice` | `lapidador` |
| `npc.varkhan-escama-vigia` | Varkhan Escama-Vigia | `draconato` | `draconato-cinzento` | `sentinela` | `escolta-profissional` |
| `npc.velka-selo-firme` | Velka Selo-Firme | `anao` | `anao-das-montanhas` | `diplomata` | `diplomata-de-corte` |
| `npc.vireo-vento-rapido` | Vireo Vento-Rápido | `goliath` | `altaneiro-dos-penhascos` | `batedor` | `mensageiro` |
| `npc.ysmira-primeiro-degelo` | Ysmira Primeiro-Degelo | `gnomo` | — | `explorador` | `geologo` |
| `npc.zeka-faisca-mansa` | Zeka Faísca-Mansa | `elemental-fogo` | — | `sobrevivente` | `minerador` |
| `npc.zhae-garra-de-nevoa` | Zhae Garra-de-Névoa | `felino-das-nuvens` | — | `sentinela` | `guia-de-montanha` |

## 3. Sinônimos e nomes

| ID | Nome padrão (catálogo) | Sinônimo registrado |
|---|---|---|
| `gnomo` | Gnomo | "Gnomo (das rochas)" (linhagem regional editorial; D-36) |
| `goliath` | Altaneiro | goliath (só como ID) |
| `minotauro` | Labiríntico | minotauro (só como ID) |
| `kobold` | Escamado Miúdo | kobold (só como ID) |
| `gigante` | Giganteano | gigante (só como ID) |
| `elemental-terra` | Nascido da Rocha | elemental da terra (só como ID) |
| `elemental-fogo` | Nascido da Brasa | elemental do fogo (só como ID) |

## 4. Pontos que continuam abertos

1. **Gnomo (das rochas):** o catálogo não tem `gnomo-das-rochas`. Tratado como linhagem regional do ID `gnomo`, sem sub-raça (D-36, opção A). Se o autor escolher a opção B, os 6 gnomos nomeados passam a `gnomo-das-engrenagens`; quatro deles (Pelma Gota-Medida, Pimm Veio-Pequeno, Ysmira Primeiro-Degelo e Tuck Lente-Torta) vivem em Veiaclara ou Geodália, onde o doc 10 §2 não põe essa sub-raça, e a distribuição do doc 10 §2 teria de ser revista.
2. **Contagem:** "doze povos nativos" é contagem cultural: todos os 12 são raças-base do catálogo (nenhum é só sub-raça), diferente de Aeltharyn.
3. **Classe e profissão são aproximações:** nenhuma pessoa tem classe de combate por obrigação; a fidelidade a cada ID é do R2-4.2.
4. **Nenhum personagem do catálogo nomeado:** o catálogo não tem figuras; `id_catalogo` do registro mestre segue vazio.

## 5. Decisão pendente

**D-39:** aprovar o alinhamento e as 45 atribuições de classe e profissão (opção A) ou trocar as que o autor preferir (opção B, diga quais). **Recomendação: A.**
