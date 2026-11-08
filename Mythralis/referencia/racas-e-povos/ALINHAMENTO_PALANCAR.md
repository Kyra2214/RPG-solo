# Alinhamento: povos do Vale Palancar × catálogo do RPG Solo

**Estado:** R4-2.3, D-114 aceita pela continuação (07/11/2026) · **Data:** 2026-11-07 · **Etiqueta:** [PROPOSTO]; não é cânone novo, só confere IDs e atribui, por pessoa, raça, sub-raça, classe e profissão do catálogo.
Conferência entre `reinos/04-vale-palancar/` (doc 10 e `12-registro-mestre.csv`) e `dados-brutos/personagem-catalogo.json`, no molde de `ALINHAMENTO_VEU.md`.

## Resultado

- **12 de 12** nativos existem como raça no catálogo, com o nome de exibição igual ao dos documentos.
- **6 sub-raças** usadas (doc 10 §2): todas existem, com a raça-mãe correta.
- **25 forasteiros** (doc 10 §5): todos existem como raça, com o mesmo nome do catálogo. **Mestiços:** não são alocados (D-64).
- **25 pessoas** (registro mestre): 0 ID de raça, sub-raça, classe ou profissão fora do catálogo; toda sub-raça atribuída aparece no lugar da pessoa (doc 10 §2).

## 1. Os 12 nativos

| Nº | Povo no doc | ID no catálogo | Nome no catálogo | Confere |
|---|---|---|---|---|
| 1 | Orcano | `orcano` | Orcano | sim |
| 2 | Felídeo Errante | `tabaxi` | Felídeo Errante | sim |
| 3 | Centauro | `centauro` | Centauro | sim |
| 4 | Goblin | `goblin` | Goblin | sim |
| 5 | Goblin de Legião | `hobgoblin` | Goblin de Legião | sim |
| 6 | Vulpino | `homem-raposa` | Vulpino | sim |
| 7 | Leporino | `homem-coelho` | Leporino | sim |
| 8 | Ursino | `homem-urso` | Ursino | sim |
| 9 | Lupino | `homem-lobo` | Lupino | sim |
| 10 | Porcino | `homem-javali` | Porcino | sim |
| 11 | Cervino | `homem-cervo` | Cervino | sim |
| 12 | Insetoide | `insetoide` | Insetoide | sim |

### 1.1 Sub-raças em uso

| Sub-raça (ID) | Raça-mãe | Nome no catálogo | Onde aparece (doc 10 §2) |
|---|---|---|---|
| `orcano-das-estepes` | `orcano` | Orcano das Estepes | Terreiro-Maior, Vazante, Gramal |
| `felideo-das-dunas` | `tabaxi` | Felídeo das Dunas | Vazante, Encosta-Aberta |
| `centauro-das-pradarias` | `centauro` | Centauro das Pradarias | Terreiro-Maior, Vazante, Encosta-Aberta |
| `goblin-das-sucatas` | `goblin` | Goblin das Sucatas | Rasovau, Praia-Pardo, Alvasal |
| `legionario-dos-estandartes` | `hobgoblin` | Legionário dos Estandartes | Alvasal, Vazante |
| `homem-raposa-dos-vales` | `homem-raposa` | Vulpino dos Vales | Rasovau |

Sem sub-raça no catálogo (não se inventa): Leporino, Ursino, Lupino, Porcino, Cervino, Insetoide. Não usadas: `goblin-dos-pantanos` e `legionario-das-cidadelas` (doc 10 §2). **Linhagens próprias (P-3, D-110):** as sub-raças `orcano-das-estepes`, `felideo-das-dunas` e `centauro-das-pradarias` são as do Vale, distintas das de Aeltharyn (`orcano-das-matas`, `felideo-das-selvas`, `centauro-das-florestas`), **sem parentesco**.

### 1.2 Mestiços (D-64)

Não alocados a reino, lugar ou facção; os 20 exemplos do catálogo não são conferidos por reino.

### 1.3 Forasteiros ({len(for_)})

| ID | Nome no doc | Nome no catálogo | Confere |
|---|---|---|---|
| `humano` | Humano | Humano | sim |
| `elfo` | Elfo | Elfo | sim |
| `anao` | Anão | Anão | sim |
| `pequenino` | Pequenino | Pequenino | sim |
| `draconato` | Draconato | Draconato | sim |
| `goliath` | Altaneiro | Altaneiro | sim |
| `kenku` | Corvídeo | Corvídeo | sim |
| `satiro` | Silvano Festivo | Silvano Festivo | sim |
| `minotauro` | Labiríntico | Labiríntico | sim |
| `kobold` | Escamado Miúdo | Escamado Miúdo | sim |
| `homem-rato` | Ratoide | Ratoide | sim |
| `feerico` | Feérico | Feérico | sim |
| `sombrio` | Umbral | Umbral | sim |
| `plantae` | Verdejante | Verdejante | sim |
| `licantropo` | Metamorfo Lupino | Metamorfo Lupino | sim |
| `gigante` | Giganteano | Giganteano | sim |
| `felino-das-nuvens` | Felino das Nuvens | Felino das Nuvens | sim |
| `tiefling` | Nascido do Crepúsculo | Nascido do Crepúsculo | sim |
| `bugbear` | Ursino Sombrio | Ursino Sombrio | sim |
| `elemental-terra` | Nascido da Rocha | Nascido da Rocha | sim |
| `elemental-fogo` | Nascido da Brasa | Nascido da Brasa | sim |
| `elemental-ar` | Nascido do Vento | Nascido do Vento | sim |
| `vampirico` | Noctívago | Noctívago | sim |
| `metamorfo` | Moldável | Moldável | sim |
| `aracnideo` | Aracnídeo | Aracnídeo | sim |

## 2. As 25 pessoas

**ASSUMINDO:** classe e profissão são leitura editorial do papel já escrito (cargo, ofício, lenda); só dizem a que ficha do catálogo a pessoa se parece. Onde não há ofício escrito, a profissão é `—` (não se inventa). Sub-raça só onde a pessoa vive num lugar em que o doc 10 §2 a coloca; os povos sem sub-raça no catálogo ficam `—`.

| ID | Nome | Raça | Sub-raça | Classe | Profissão |
|---|---|---|---|---|---|
| `npc.kharos-casco-de-marcha` | Kharos Casco-de-Marcha | `centauro` | `centauro-das-pradarias` | `comandante` | — |
| `npc.yorna-palavra-firme` | Yorna Palavra-Firme | `orcano` | `orcano-das-estepes` | `guardiao-de-reliquias` | `mestre-de-cerimonias` |
| `npc.gruzk-estandarte-baixo` | Gruzk Estandarte-Baixo | `hobgoblin` | `legionario-dos-estandartes` | `comandante` | — |
| `npc.moura-mel-guardado` | Moura Mel-Guardado | `homem-urso` | — | `guardiao` | — |
| `npc.pocha-ninhada-farta` | Pocha Ninhada-Farta | `homem-javali` | — | `sobrevivente` | — |
| `npc.tessun-salto-calado` | Tessun Salto-Calado | `tabaxi` | `felideo-das-dunas` | `batedor` | `guia-de-montanha` |
| `npc.rukhan-retorno-certo` | Rukhan Retorno-Certo | `homem-lobo` | — | `batedor` | — |
| `npc.zinu-troca-justa` | Zinu Troca-Justa | `goblin` | `goblin-das-sucatas` | `guardiao` | `comerciante` |
| `npc.quira-recado-certo` | Quira Recado-Certo | `homem-raposa` | `homem-raposa-dos-vales` | `diplomata` | `mensageiro` |
| `npc.vrissk-colonia-quieta` | Vrissk Colônia-Quieta | `insetoide` | — | `guardiao` | `avaliador-de-materiais` |
| `npc.tibbe-salto-curto` | Tibbe Salto-Curto | `homem-coelho` | — | `domador` | `criador-de-animais` |
| `npc.cassan-galhada-velha` | Cassan Galhada-Velha | `homem-cervo` | — | `diplomata` | `estalajadeiro` |
| `npc.bruga-colheita-salva` | Bruga Colheita-Salva | `homem-javali` | — | `guardiao` | — |
| `npc.jessik-trato-primeiro` | Jessik Trato-Primeiro | `homem-raposa` | `homem-raposa-dos-vales` | `diplomata` | — |
| `npc.nokk-peso-alheio` | Nokk Peso-Alheio | `goblin` | `goblin-das-sucatas` | `guardiao-do-codigo` | — |
| `npc.grebb-balanca-justa` | Grebb Balança-Justa | `goblin` | `goblin-das-sucatas` | `guardiao-do-codigo` | `comerciante` |
| `npc.urrek-bando-parado` | Urrek Bando-Parado | `homem-lobo` | — | `sobrevivente` | — |
| `npc.aruun-poco-compartilhado` | Aruun Poço-Compartilhado | `homem-lobo` | — | `guardiao` | — |
| `npc.hukk-pedra-de-divisa` | Hukk Pedra-de-Divisa | `orcano` | `orcano-das-estepes` | `diplomata` | — |
| `npc.bolgra-forja-de-campo` | Bolgra Forja-de-Campo | `orcano` | `orcano-das-estepes` | `artifice` | `ferreiro` |
| `npc.sendro-clareira-vigia` | Sendro Clareira-Vigia | `homem-cervo` | — | `sentinela` | — |
| `npc.cadrek-cerca-aberta` | Cadrek Cerca-Aberta | `centauro` | `centauro-das-pradarias` | `guardiao` | — |
| `npc.zzirr-favo-dividido` | Zzirr Favo-Dividido | `insetoide` | — | `guardiao` | — |
| `npc.ghorla-fartura-dividida` | Ghorla Fartura-Dividida | `homem-javali` | — | `sobrevivente` | `cacador` |
| `npc.lumbo-pedra-de-baixo` | Lumbo Pedra-de-Baixo | `homem-coelho` | — | `guardiao` | — |

## 3. Sinônimos e nomes

- **Felídeo Errante (`tabaxi`), Orcano (`orcano`) e Centauro (`centauro`):** mesmos IDs de raça de Aeltharyn; a diferença entre os reinos está só na sub-raça (§1.1).
- **Goblin (`goblin`) × Goblin de Legião (`hobgoblin`):** dois povos; nenhum é "o inimigo" por raça (W-01).
- **Lupino (`homem-lobo`) × Metamorfo Lupino (`licantropo`):** dois povos; o Metamorfo Lupino é só forasteiro em Alvasal.
- **Vulpino (`homem-raposa`), Leporino (`homem-coelho`), Ursino (`homem-urso`), Porcino (`homem-javali`), Cervino (`homem-cervo`):** nomes de exibição do catálogo, iguais aos dos documentos.

## 4. Pontos que continuam abertos

1. **Classe e profissão são aproximações:** a fidelidade a cada ID é do R4-4.2.
2. **Capitão de Marcha:** não há classe "capitão" no catálogo; Kharos é `comandante` (leitura editorial).
3. **Nenhum personagem do catálogo nomeado:** `id_catalogo` do registro mestre segue vazio.

## 5. Decisão pendente

**D-114:** aprovar a atribuição de raça, sub-raça, classe e profissão às 25 pessoas. Opções: **A.** aprovar; **B.** trocar atribuições (diga quais). **Recomendação: A.**
