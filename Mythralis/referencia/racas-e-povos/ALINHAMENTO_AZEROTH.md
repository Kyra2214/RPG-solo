# Alinhamento: povos de Azeroth × catálogo do RPG Solo

**Estado:** R5-2.3, D-151 aceita pela continuação (07/11/2026) · **Data:** 2026-11-07 · **Etiqueta:** [PROPOSTO]; não é cânone novo, só confere IDs e atribui, por pessoa, raça, sub-raça, classe e profissão do catálogo.
Conferência entre `reinos/05-azeroth/` (doc 10 e `12-registro-mestre.csv`) e `dados-brutos/personagem-catalogo.json`, no molde de `ALINHAMENTO_PALANCAR.md`.

## Resultado

- **9 de 9** nativos existem como raça no catálogo, com o nome de exibição igual ao dos documentos.
- **8 sub-raças** usadas (doc 10 §2): todas existem, com a raça-mãe correta.
- **25 forasteiros** (doc 10 §5): todos existem como raça, com o mesmo nome do catálogo. **Mestiços:** não são alocados (D-64).
- **38 pessoas** (registro mestre): 0 ID de raça, sub-raça, classe ou profissão fora do catálogo; toda sub-raça atribuída aparece no lugar da pessoa (doc 10 §2).

## 1. Os 9 nativos

| Nº | Povo no doc | ID no catálogo | Nome no catálogo | Confere |
|---|---|---|---|---|
| 1 | Humano | `humano` | Humano | sim |
| 2 | Pequenino | `pequenino` | Pequenino | sim |
| 3 | Luminar | `aasimar` | Luminar | sim |
| 4 | Tartarino | `tortle` | Tartarino | sim |
| 5 | Abissal Costeiro | `tritao` | Abissal Costeiro | sim |
| 6 | Nascido da Maré | `elemental-agua` | Nascido da Maré | sim |
| 7 | Nascido do Vento | `elemental-ar` | Nascido do Vento | sim |
| 8 | Sereiano | `sereiano` | Sereiano | sim |
| 9 | Estelar | `ancestral-estelar` | Estelar | sim |

### 1.1 Sub-raças em uso

| Sub-raça (ID) | Raça-mãe | Nome no catálogo | Onde aparece (doc 10 §2) |
|---|---|---|---|
| `humano-das-cidades` | `humano` | Humano das Cidades | Campoalvo, Vaudouro, Alvamar, Concharia, Torreclara, Estrelado, Meiovau |
| `humano-das-fronteiras` | `humano` | Humano das Fronteiras | Torrefronte, Enseada-Vigia, Pouso-das-Brisas |
| `pequenino-viajante` | `pequenino` | Pequenino Viajante | Campoalvo, Vaudouro, Pouso-das-Brisas, Meiovau |
| `luminar-do-alvorecer` | `aasimar` | Luminar do Alvorecer | Campoalvo, Torrefronte, Meiovau |
| `luminar-do-zenite` | `aasimar` | Luminar do Zênite | Torreclara, Estrelado |
| `tartarino-dos-recifes` | `tortle` | Tartarino dos Recifes | Concharia |
| `tartarino-das-ilhas` | `tortle` | Tartarino das Ilhas | Alvamar, Enseada-Vigia |
| `abissal-das-profundezas` | `tritao` | Abissal das Profundezas | Alvamar, Enseada-Vigia |

Sem sub-raça no catálogo (não se inventa): Nascido da Maré, Nascido do Vento, Sereiano e Estelar. **Linhagens próprias (P-4):** as sub-raças de Humano, Pequenino e Abissal Costeiro são as de Azeroth, sem parentesco com as de Aeltharyn (doc 10 §2.1).

### 1.2 Mestiços (D-64)

Não alocados a reino, lugar ou facção; os 20 exemplos do catálogo não são conferidos por reino.

### 1.3 Forasteiros (25)

| ID | Nome no doc | Nome no catálogo | Confere |
|---|---|---|---|
| `elfo` | Elfo | Elfo | sim |
| `anao` | Anão | Anão | sim |
| `gnomo` | Gnomo | Gnomo | sim |
| `goblin` | Goblin | Goblin | sim |
| `homem-rato` | Ratoide | Ratoide | sim |
| `lizardino` | Escamado | Escamado | sim |
| `tiefling` | Nascido do Crepúsculo | Nascido do Crepúsculo | sim |
| `vampirico` | Noctívago | Noctívago | sim |
| `orcano` | Orcano | Orcano | sim |
| `draconato` | Draconato | Draconato | sim |
| `goliath` | Altaneiro | Altaneiro | sim |
| `tabaxi` | Felídeo Errante | Felídeo Errante | sim |
| `kenku` | Corvídeo | Corvídeo | sim |
| `satiro` | Silvano Festivo | Silvano Festivo | sim |
| `centauro` | Centauro | Centauro | sim |
| `hobgoblin` | Goblin de Legião | Goblin de Legião | sim |
| `homem-corvo` | Corvário | Corvário | sim |
| `homem-raposa` | Vulpino | Vulpino | sim |
| `homem-coelho` | Leporino | Leporino | sim |
| `homem-javali` | Porcino | Porcino | sim |
| `elemental-terra` | Nascido da Rocha | Nascido da Rocha | sim |
| `onirico` | Onírico | Onírico | sim |
| `cristalino` | Cristalino | Cristalino | sim |
| `construto` | Construto Desperto | Construto Desperto | sim |
| `metamorfo` | Moldável | Moldável | sim |

## 2. As 38 pessoas

**ASSUMINDO:** classe e profissão são leitura editorial do papel já escrito (cargo, ofício, órgão que lidera); só dizem a que ficha do catálogo a pessoa se parece. Onde não há ofício escrito, a profissão é `—` (não se inventa). Os 8 heróis lendários não têm feito escrito (doc 01, parte 2), então classe e profissão ficam `—`. Sub-raça só onde a pessoa vive num lugar em que o doc 10 §2 a coloca; os povos sem sub-raça no catálogo ficam `—`.

| ID | Nome | Raça | Sub-raça | Classe | Profissão |
|---|---|---|---|---|---|
| `npc.odile-medida-cheia` | Odile Medida-Cheia | `humano` | `humano-das-cidades` | `estrategista` | `contador` |
| `npc.corlan-cais-primeiro` | Corlan Cais-Primeiro | `tritao` | `abissal-das-profundezas` | `diplomata` | — |
| `npc.brevo-rumo-alto` | Brevo Rumo-Alto | `elemental-ar` | — | `estrategista` | — |
| `npc.tomlin-horta-larga` | Tomlin Horta-Larga | `pequenino` | `pequenino-viajante` | `diplomata` | — |
| `npc.rilla-espuma-fina` | Rilla Espuma-Fina | `elemental-agua` | — | `diplomata` | — |
| `npc.astrel-noite-clara` | Astrel Noite-Clara | `ancestral-estelar` | — | `diplomata` | `astronomo` |
| `npc.solenne-vigilia-longa` | Solenne Vigília-Longa | `aasimar` | `luminar-do-alvorecer` | `comandante` | — |
| `npc.dorgan-escudo-lento` | Dorgan Escudo-Lento | `tortle` | `tartarino-das-ilhas` | `comandante` | — |
| `npc.nerella-canto-da-barra` | Nerella Canto-da-Barra | `sereiano` | — | `marinheiro` | `navegador` |
| `npc.garvane-remo-firme` | Garvane Remo-Firme | `humano` | `humano-das-cidades` | `marinheiro` | `marinheiro-de-carga` |
| `npc.tolla-mesa-posta` | Tolla Mesa-Posta | `pequenino` | `pequenino-viajante` | `guardiao-do-codigo` | `mediador` |
| `npc.auriven-porta-acesa` | Auriven Porta-Acesa | `aasimar` | `luminar-do-alvorecer` | `guardiao` | `estalajadeiro` |
| `npc.wendra-aterro-primeiro` | Wendra Aterro-Primeiro | `humano` | `humano-das-cidades` | — | — |
| `npc.helion-vigilia-do-celeiro` | Helion Vigília-do-Celeiro | `aasimar` | `luminar-do-alvorecer` | `guardiao` | — |
| `npc.dodda-prato-cheio` | Dodda Prato-Cheio | `pequenino` | `pequenino-viajante` | `diplomata` | — |
| `npc.ebbo-praia-rasa` | Ebbo Praia-Rasa | `elemental-agua` | — | `guardiao` | — |
| `npc.larkin-barcaca-cheia` | Larkin Barcaça-Cheia | `pequenino` | `pequenino-viajante` | `diplomata` | `marinheiro-de-carga` |
| `npc.halvard-limite-firme` | Halvard Limite-Firme | `humano` | `humano-das-fronteiras` | — | — |
| `npc.sabeth-patio-aberto` | Sabeth Pátio-Aberto | `humano` | `humano-das-fronteiras` | `diplomata` | — |
| `npc.sundar-licenca-de-cais` | Sundar Licença-de-Cais | `tritao` | `abissal-das-profundezas` | — | — |
| `npc.marnu-espuma-dada` | Marnu Espuma-Dada | `elemental-agua` | — | `diplomata` | — |
| `npc.perrin-farol-aceso` | Perrin Farol-Aceso | `humano` | `humano-das-fronteiras` | `sentinela` | — |
| `npc.ressan-concha-aberta` | Ressan Concha-Aberta | `tritao` | `abissal-das-profundezas` | `diplomata` | — |
| `npc.mossa-casco-primeiro` | Mossa Casco-Primeiro | `tortle` | `tartarino-dos-recifes` | — | — |
| `npc.orrume-abrigo-de-casco` | Orrume Abrigo-de-Casco | `tortle` | `tartarino-dos-recifes` | `artifice` | `construtor-de-barcos` |
| `npc.corali-fundo-de-coral` | Corali Fundo-de-Coral | `sereiano` | — | `guardiao` | — |
| `npc.raffe-sopro-primeiro` | Raffe Sopro-Primeiro | `elemental-ar` | — | — | — |
| `npc.casimar-codigo-limpo` | Casimar Código-Limpo | `humano` | `humano-das-cidades` | `guardiao-do-codigo` | — |
| `npc.clarel-lume-de-torre` | Clarel Lume-de-Torre | `aasimar` | `luminar-do-zenite` | `diplomata` | — |
| `npc.orbane-rota-perdida` | Orbane Rota-Perdida | `ancestral-estelar` | — | — | — |
| `npc.eloh-cupula-acesa` | Eloh Cúpula-Acesa | `aasimar` | `luminar-do-zenite` | `cronista` | `arquivista` |
| `npc.ixra-noite-aberta` | Ixra Noite-Aberta | `ancestral-estelar` | — | `oraculo` | `astronomo` |
| `npc.zefir-pipa-caida` | Zefir Pipa-Caída | `elemental-ar` | — | — | — |
| `npc.hollis-torre-do-meio` | Hollis Torre-do-Meio | `humano` | `humano-das-fronteiras` | `sentinela` | — |
| `npc.poppel-mesa-de-viajante` | Poppel Mesa-de-Viajante | `pequenino` | `pequenino-viajante` | `diplomata` | `estalajadeiro` |
| `npc.ostrel-chave-primeira` | Ostrel Chave-Primeira | `humano` | `humano-das-cidades` | — | — |
| `npc.pelmo-balanca-de-porto` | Pelmo Balança-de-Porto | `humano` | `humano-das-cidades` | `guardiao-do-codigo` | `contador` |
| `npc.ruvane-chave-contada` | Ruvane Chave-Contada | `humano` | `humano-das-cidades` | `guardiao-do-codigo` | `contador` |

## 3. Sinônimos e nomes

- **Abissal Costeiro (`tritao`):** nome de exibição do catálogo; em Azeroth é nativo e usa `abissal-das-profundezas` (Aeltharyn o tem só como aliado, com `abissal-das-enseadas`).
- **Luminar (`aasimar`), Tartarino (`tortle`), Nascido da Maré (`elemental-agua`), Nascido do Vento (`elemental-ar`), Estelar (`ancestral-estelar`):** nomes de exibição do catálogo, iguais aos dos documentos.
- **Corvídeo (`kenku`) × Corvário (`homem-corvo`):** dois forasteiros distintos (doc 10 §5).

## 4. Pontos que continuam abertos

1. **Classe e profissão são aproximações:** a fidelidade a cada ID é do R5-4.2.
2. **Heróis lendários** sem classe nem profissão até haver feito escrito.
3. **Nenhum personagem do catálogo nomeado:** `id_catalogo` do registro mestre segue vazio.
