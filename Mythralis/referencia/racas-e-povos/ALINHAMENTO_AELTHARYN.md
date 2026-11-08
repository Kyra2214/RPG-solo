# Alinhamento: povos de Aeltharyn × catálogo do RPG Solo

Conferência feita em 25/10/2026 entre o doc `10-matriz-povos-nativos-cidades-e-forasteiros.md`, o catálogo de código de Aeltharyn (`referencia-tecnica/AeltharynWorld.kt`, 16 povos e linhagens) e `dados-brutos/personagem-catalogo.json`.

## Resultado

Os **16 IDs** usados no catálogo de Aeltharyn existem no catálogo de personagem (raça, sub-raça ou linhagem com o mesmo ID). Os 11 povos nativos de referência do doc 10 também casam.

| Nº | Povo em Aeltharyn | ID no catálogo | Tipo | Nome no catálogo | Confere |
|---|---|---|---|---|---|
| 1 | Elfos | `elfo` | races | Elfo | sim |
| 1a | Elfos dos Bosques | `elfo-dos-bosques` | subraces | Elfo dos Bosques | sim |
| 1b | Elfos das Estrelas | `elfo-das-estrelas` | subraces | Elfo das Estrelas | sim |
| 1c | Elfos do Crepúsculo | `elfo-do-crepusculo` | subraces | Elfo do Crepúsculo | sim |
| 2 | Silvanos Festivos | `satiro` | races | Silvano Festivo | sim |
| 2a | Silvanos dos Festivais | `silvano-dos-festivais` | subraces | Silvano dos Festivais | sim |
| 3 | Feéricos | `feerico` | races | Feérico | sim |
| 4 | Verdejantes | `plantae` | races | Verdejante | sim |
| 5 | Micélioides | `fungal` | races | Micélioide | sim |
| 6 | Centauros das Florestas | `centauro-das-florestas` | subraces | Centauro das Florestas | sim |
| 7 | Felídeos das Selvas | `felideo-das-selvas` | subraces | Felídeo das Selvas | sim |
| 8 | Orcanos das Matas | `orcano-das-matas` | subraces | Orcano das Matas | sim |
| 9 | Corvários | `homem-corvo-das-torres` | subraces | Corvário das Torres | sim |
| 10 | Pequeninos (ribeirinhos) | `pequenino-ribeirinho` | subraces | Pequenino Ribeirinho | sim |
| 11 | Gnomos dos Vales | `gnomo-dos-vales` | subraces | Gnomo dos Vales | sim |
| — | Abissais das Enseadas (aliado costeiro, não é o 12º povo) | `abissal-das-enseadas` | subraces | Abissal das Enseadas | sim |

## Decisões aplicadas (M1.4, 25/10/2026)

1. **Corvários (D-01).** O povo-corvo de Aeltharyn é `homem-corvo` / `homem-corvo-das-torres`, nome de exibição **Corvário**. "Corvídeo" (`kenku`) não é povo de Aeltharyn. Nos documentos do reino as menções foram trocadas; "corvídeos" como aves, na lista de fauna do doc 01, ficou como estava.
2. **Nomes do catálogo (D-03).** O nome de exibição do catálogo é o padrão; o apelido do texto fica só como sinônimo.

| ID | Nome padrão (catálogo) | Sinônimo registrado |
|---|---|---|
| `satiro` | Silvano Festivo | sátiro |
| `tabaxi` | Felídeo Errante (a linhagem de Aeltharyn é `felideo-das-selvas`, Felídeo das Selvas) | tabaxi |
| `plantae` | Verdejante | plantae (só como ID) |
| `fungal` | Micélioide | fungal (só como ID) |
| `tritao` | Abissal Costeiro | tritão |
| `homem-corvo` | Corvário | corvídeo (apenas como ave; não é povo) |

3. **Nome do mundo (D-02).** O mundo é **Mythralis**; Aeltharyn é o reino das florestas.

## Pontos que continuam abertos

1. **Contagem:** "onze povos nativos" é contagem cultural e territorial, não onze raças-base. Contando só raças-base, os nativos são: elfo, sátiro, feérico, plantae, fungal, centauro, tabaxi, orcano, homem-corvo, pequenino, gnomo (11); as demais entradas são sub-raças ou linhagens dessas.
2. **Mestiços (D-64):** não são alocados a Aeltharyn nem a nenhum reino; podem nascer na cidade de qualquer um dos pais, em qualquer combinação do catálogo, e o jogador escolhe. Os 20 do catálogo são só exemplos (`hibridos.md`).
