# M-05 — Povos e migrações de Mythralis

**Estado:** **aprovado pelo autor em 2026-10-26** (D-24; todas as propostas M5-01 a M5-04 e os dois encaixes da §9 viram canônicos) · **Data:** 2026-10-25
**Base:** `referencia/racas-e-povos/MATRIZ_POVOS_POR_REINO.md` (coluna Florestas, canônica), `mundo/M-04-geografia-mestra.md` (anel dos reinos, aprovado) e o doc 10 de Aeltharyn.
**Etiqueta:** **[CANÔNICO]** vem do reino 1 ou do M-04; **[PROPOSTO M5-nn]** é lore novo e só vale depois de aprovado.

## 1. Regras

1. A coluna **Florestas não muda**: 11 nativos e o abissal costeiro (`tritao`) como aliado. Nenhuma raça nova entra em Aeltharyn por este documento.
2. **Raça não é cidadania, cultura nem lealdade.** Estar na coluna “nativo” de um reino diz de onde o povo de referência vem, não que todo membro seja leal a ele.
3. Cada reino recebe **nativos de referência**, não todos os 50 povos. Uma raça pode ser nativa de dois reinos quando a linhagem difere (como os gnomos dos vales em Aeltharyn e os gnomos das rochas nas Montanhas).
4. Só vizinhança e papel: nenhuma população, cidade ou história de reino 2 a 5 nasce aqui.
5. O reino 5 (Pacto Humano) continua sendo o último a ser escrito; a coluna Humanos só reserva quem é de lá.

## 2. Legenda

- **N** nativo: povo de referência do reino.
- **A** aliado: presente por pacto, sem ser nativo.
- **F** forasteiro: origem fora do reino, como residente, mercador, refugiado ou diáspora.
- **F\*** já canônico no doc 01 §2.4 como residente em Aeltharyn (humanos, anões, goblins e ratoides), ainda sem registro formal; ver M5-04.
- **—** sem papel definido.

## 3. [PROPOSTO M5-01] Quem é nativo de onde

A lógica segue o relevo e a vizinhança do M-04:

- **Florestas** (11): Elfo, Orcano, Pequenino, Gnomo, Felídeo Errante, Silvano Festivo, Centauro, Corvário, Feérico, Micélioide, Verdejante.
- **Montanhas** (12): Anão, Gnomo, Draconato, Altaneiro, Labiríntico, Escamado Miúdo, Nascido da Rocha, Nascido da Brasa, Cristalino, Construto Desperto, Giganteano, Felino das Nuvens.
- **Malditos** (11): Nascido do Crepúsculo, Corvídeo, Ursino Sombrio, Escamado, Ratoide, Umbral, Onírico, Noctívago, Metamorfo Lupino, Moldável, Aracnídeo.
- **Feras** (12): Orcano, Felídeo Errante, Centauro, Goblin, Goblin de Legião, Vulpino, Leporino, Ursino, Lupino, Porcino, Cervino, Insetoide.
- **Humanos** (9): Humano, Pequenino, Luminar, Tartarino, Abissal Costeiro, Nascido da Maré, Nascido do Vento, Sereiano, Estelar.

Nativos em dois reinos (linhagens diferentes): Orcano, Pequenino, Gnomo, Felídeo Errante, Centauro.

**Por quê:** as Montanhas ficam com os povos da pedra, do fogo e das grandes alturas; o Véu, com os pactuados, os noturnos e os de neblina; as Marcas, com os povos-animais e os de migração; os Humanos, com os povos de planície, rio e costa. Aeltharyn conserva seus onze.

## 4. [PROPOSTO M5-02] A matriz completa

| ID | Raça-base | Florestas | Montanhas | Malditos | Feras | Humanos |
|---|---|---|---|---|---|---|
| `humano` | Humano | F* | F | F | F | N |
| `elfo` | Elfo | N | F | — | F | F |
| `anao` | Anão | F* | N | — | F | F |
| `orcano` | Orcano | N | F | F | N | F |
| `pequenino` | Pequenino | N | F | F | F | N |
| `gnomo` | Gnomo | N | N | — | — | F |
| `draconato` | Draconato | — | N | F | F | F |
| `tiefling` | Nascido do Crepúsculo | — | F | N | F | F |
| `aasimar` | Luminar | — | F | — | — | N |
| `goliath` | Altaneiro | — | N | — | F | F |
| `tabaxi` | Felídeo Errante | N | — | F | N | F |
| `kenku` | Corvídeo | — | — | N | F | F |
| `tortle` | Tartarino | — | — | F | — | N |
| `tritao` | Abissal Costeiro | A | — | — | — | N |
| `satiro` | Silvano Festivo | N | — | — | F | F |
| `centauro` | Centauro | N | — | — | N | F |
| `minotauro` | Labiríntico | — | N | F | F | — |
| `goblin` | Goblin | F* | F | F | N | F |
| `hobgoblin` | Goblin de Legião | — | F | — | N | F |
| `bugbear` | Ursino Sombrio | — | F | N | F | — |
| `kobold` | Escamado Miúdo | — | N | F | F | — |
| `lizardino` | Escamado | — | — | N | — | F |
| `homem-rato` | Ratoide | F* | F | N | F | F |
| `homem-corvo` | Corvário | N | — | F | — | F |
| `homem-raposa` | Vulpino | — | — | F | N | F |
| `homem-coelho` | Leporino | — | — | — | N | F |
| `homem-urso` | Ursino | — | F | — | N | — |
| `homem-lobo` | Lupino | — | F | F | N | — |
| `homem-javali` | Porcino | — | F | — | N | F |
| `homem-cervo` | Cervino | — | — | — | N | — |
| `elemental-terra` | Nascido da Rocha | — | N | — | F | F |
| `elemental-fogo` | Nascido da Brasa | — | N | F | F | — |
| `elemental-agua` | Nascido da Maré | — | — | — | — | N |
| `elemental-ar` | Nascido do Vento | — | F | — | F | N |
| `feerico` | Feérico | N | — | F | F | — |
| `sombrio` | Umbral | — | F | N | F | — |
| `onirico` | Onírico | — | — | N | — | F |
| `cristalino` | Cristalino | — | N | — | — | F |
| `fungal` | Micélioide | N | — | F | — | — |
| `plantae` | Verdejante | N | — | F | F | — |
| `construto` | Construto Desperto | — | N | — | — | F |
| `vampirico` | Noctívago | — | — | N | F | F |
| `licantropo` | Metamorfo Lupino | — | — | N | F | — |
| `metamorfo` | Moldável | — | F | N | F | F |
| `gigante` | Giganteano | — | N | — | F | — |
| `sereiano` | Sereiano | — | — | — | — | N |
| `aracnideo` | Aracnídeo | — | — | N | F | — |
| `insetoide` | Insetoide | — | — | F | N | — |
| `felino-das-nuvens` | Felino das Nuvens | — | N | — | F | — |
| `ancestral-estelar` | Estelar | — | F | — | — | N |

### Contagem por reino

| Reino | Nativos (N) | Aliados (A) | Forasteiros (F) |
|---|---|---|---|
| Florestas | 11 | 1 | 4 |
| Montanhas | 12 | 0 | 17 |
| Malditos | 11 | 0 | 17 |
| Feras | 12 | 0 | 25 |
| Humanos | 9 | 0 | 25 |

Cinquenta raças-base, todas com pelo menos um reino de origem. A coluna Florestas mostra os 38 sem papel registrado; as quatro marcadas F\* são as que o doc 01 §2.4 já cita como residentes.

## 5. [PROPOSTO M5-03] Os cinco fluxos de migração

Fluxos recorrentes que explicam por que povos vivem longe de casa. Nenhum tem data, número ou causa ligada aos mistérios de Aeltharyn.

1. **A Descida do Argente.** Anões, kobolds, gnomos e construtos descem das Coroas de Pedra pelo Rio Argente para comerciar nas Marcas e no Véu.
2. **A Rota do Sal.** Pelo Ermo de Sal passam mercadores, goblins e ratoides entre as Marcas e o Véu, sempre em caravana e com guias.
3. **Os Corredores Largos.** Centauros, felídeos, cervinos e lupinos das Marcas cruzam as elevações ocidentais para Aeltharyn, os mesmos corredores do conflito G.
4. **As Costas.** Pelo Mar de Âmbar e o Mar Austral, tritões, sereianos, tartarinos e marinheiros humanos ligam Thalanor à costa meridional humana.
5. **As Fugas do Véu.** Famílias de pactos antigos, lagartos de charco e noturnos saem do Véu para as planícies humanas ou para a orla do pântano. Chegam como refugiados, nem sempre bem-vindos, e a Lei de Proteção de Refugiados de Aeltharyn decide o tratamento.

**Nota (D-103, 07/11/2026):** a Descida do Argente leva anões, kobolds, gnomos e construtos "às Marcas e ao Véu", mas a matriz (§4) dá "—" a gnomo e a construto desperto nas Feras e no Véu (e a anão no Véu). A matriz **não muda**: nesses reinos, esses povos aparecem **só como visitantes de passagem** onde a Descida chega, sem residir nem ter lugar fixo, até o autor decidir o contrário.

## 6. [PROPOSTO M5-04] Registrar os quatro residentes de Aeltharyn

O doc 01 §2.4 já diz que humanos, anões, goblins e ratoides vivem no reino como viajantes, refugiados ou residentes, sem serem hostis por natureza. Proposta: marcar os quatro como F na coluna Florestas, sem criar povo nativo, cidade ou facção. É só registro e não altera nenhum texto.

## 7. O que este documento não decide

- Sub-raças de cada reino (entram quando o reino for escrito). **Mestiços não são alocados a reino nenhum** (D-64): nascem na cidade de qualquer um dos pais, sem lista fechada; ver `referencia/racas-e-povos/hibridos.md`.
- Cultura, religião e língua de qualquer povo fora de Aeltharyn.
- Quantidade de gente de cada povo.
- Relações políticas entre povos.

## 8. Efeito sobre os outros documentos

- **Matriz de povos:** a tabela antiga fica como histórico; esta é a vigente depois da aprovação.
- **Validador:** a regra V-12 (povo existe no catálogo) e V-36 (cidade com pelo menos dois povos) passam a ter a matriz como referência por reino.
- **Reino 2:** os 12 nativos das Montanhas são o ponto de partida das cidades das Coroas de Pedra.
- **Aeltharyn:** nenhum documento muda.

## 9. Decisões para o autor

1. **M5-01:** aprovar a distribuição de nativos (principalmente os casos duplos: orcano, pequenino, gnomo, tabaxi, centauro).
2. **M5-02:** aprovar os forasteiros F de cada reino.
3. **M5-03:** aprovar os cinco fluxos.
4. **M5-04:** registrar os quatro residentes de Aeltharyn.
5. **Estelar** (`ancestral-estelar`) como nativo dos Humanos e **Felino das Nuvens** como nativo das Montanhas: são os dois encaixes menos óbvios; confirme ou diga onde preferem.
