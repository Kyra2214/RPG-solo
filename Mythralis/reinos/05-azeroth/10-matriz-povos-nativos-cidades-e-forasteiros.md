# Azeroth — matriz de povos nativos, lugares e forasteiros (doc 10)

**Estado:** R5-1.6, D-147 aceita pela continuação (07/11/2026) · **Data:** 2026-11-07
**Etiqueta:** tudo **[PROPOSTO AP-01 a AP-08]**. Cânone de apoio: M-05 (§3 e §4: 9 nativos e 25 forasteiros dos Humanos), doc 01 (partes 1 a 3), docs 03 a 05, `referencia/dados-brutos/personagem-catalogo.json` (IDs), contrato de fronteira (P-1 e P-6 decididos em D-141; P-4 aceito; P-3 tratado no §2.1).
**Regras seguidas:** povo em **ID do catálogo**; nenhuma quantidade de gente (R5-3.2); nenhuma rota ou tempo (R5-3.1); nenhum povo é cidadão, vilão ou leal por natureza (W-01); \"Humano\" é povo, não cidadania nem governo (contrato §6, item 10); sub-raça só do catálogo; mestiço pela regra D-64 (não alocado); forasteiro não cria rota nem vínculo com outro reino; **nenhum NPC novo**.
**Campos adiados:** conferência final de IDs e classes por NPC (R5-2.3); competências (R5-4.2).

## 1. Os nove povos nativos **[PROPOSTO AP-01]**

Nativo = povo de referência do reino, não cidadania nem lealdade (M-05 §1). Lugares vêm da parte 1 do doc 01.

| Nº | Povo | ID do catálogo | Lugares onde está | Identidade no reino (uma linha) |
|---|---|---|---|---|
| 1 | Humano | `humano` | os dez lugares | medida, porta, remo e mesa |
| 2 | Pequenino | `pequenino` | Campoalvo, Vaudouro, Pouso-das-Brisas, Meiovau | horta, barcaça, viagem e mesa |
| 3 | Luminar | `aasimar` | Campoalvo, Torrefronte, Torreclara, Estrelado, Meiovau | celeiro, vigília, cúpula e albergue |
| 4 | Tartarino | `tortle` | Alvamar, Enseada-Vigia, Concharia | casco, estaleiro e escudo de cais |
| 5 | Abissal Costeiro | `tritao` | Alvamar, Enseada-Vigia | licença de cais e concha |
| 6 | Nascido da Maré | `elemental-agua` | Vaudouro, Alvamar, Concharia, Meiovau | praia rasa e espuma |
| 7 | Nascido do Vento | `elemental-ar` | Torreclara, Estrelado, Pouso-das-Brisas, Meiovau | sopro, sinal e torre |
| 8 | Sereiano | `sereiano` | Alvamar, Enseada-Vigia, Concharia | canto de barra e fundo de coral |
| 9 | Estelar | `ancestral-estelar` | Torreclara, Estrelado | rota, céu e noite aberta |

### 1.1 O que nenhum povo é
Nenhum povo é cidadão, vilão, governante ou leal por natureza. **\"Humano\" não quer dizer \"de Azeroth\"**: humano nasce em qualquer reino (contrato A2) e é só um dos nove povos daqui. Coroa não é raça: os três governantes são de três povos (Humano, Abissal Costeiro, Nascido do Vento; doc 01 parte 2). Estelar como nativo segue a decisão assumida em D-141 (matriz intacta); Nascido do Vento, Luminar e Tartarino não são \"o povo do Vento\", \"da Guarda\" ou \"do Cais\" por natureza, só aparecem onde o doc 01 os põe.

## 2. Sub-raças do catálogo **[PROPOSTO AP-02]**

Só IDs que existem no JSON. Onde cada uma aparece é ASSUMINDO pelo nome; a troca é livre. **O nome de uma sub-raça não cria relevo nem lore no reino.**

| Povo | Sub-raça (ID) | Onde aparece |
|---|---|---|
| Humano | `humano-das-cidades` | Campoalvo, Vaudouro, Alvamar, Concharia, Torreclara, Estrelado, Meiovau |
| Humano | `humano-das-fronteiras` | Torrefronte, Enseada-Vigia, Pouso-das-Brisas |
| Pequenino | `pequenino-viajante` | Campoalvo, Vaudouro, Pouso-das-Brisas, Meiovau |
| Luminar | `luminar-do-alvorecer` | Campoalvo, Torrefronte, Meiovau |
| Luminar | `luminar-do-zenite` | Torreclara, Estrelado |
| Tartarino | `tartarino-dos-recifes` | Concharia |
| Tartarino | `tartarino-das-ilhas` | Alvamar, Enseada-Vigia |
| Abissal Costeiro | `abissal-das-profundezas` | Alvamar, Enseada-Vigia |

Sem sub-raça no catálogo (não se inventa): Nascido da Maré, Nascido do Vento, Sereiano e Estelar. Todas as sub-raças existentes dos cinco povos que as têm estão usadas; nenhuma fica sem uso.

### 2.1 Linhagens próprias de Humano, Pequenino e Abissal Costeiro (P-4)
Aeltharyn nomeou as suas: humanos (`humano-das-cidades` e `humano-das-fronteiras`), pequeninos ribeirinhos (`pequenino-ribeirinho`) e, como aliado, abissais das enseadas (`abissal-das-enseadas`). **Proposta:** Azeroth usa a **outra** sub-raça nos pares de dois (`pequenino-viajante`, `abissal-das-profundezas`); nos humanos, em que Aeltharyn já usa as duas, Azeroth usa as mesmas IDs **como linhagem própria, sem parentesco** e sem nome novo (regra de D-110, contrato P-4). Nenhum NPC de Aeltharyn (`npc.syranna-das-mares` e os demais, contrato A4) é parente de alguém de Azeroth. Atribuição por NPC: R5-2.3. A pergunta de **onde termina o Véu e começa a costa** (P-3) segue aberta; este documento não a afirma.

## 3. Mestiços **[D-64]**

Não são alocados a reino, lugar, facção ou coroa. Nascem no lugar de qualquer um dos pais, em qualquer par de raças ou sub-raças do catálogo; o jogador escolhe. Nenhuma tabela de mestiços aqui.

## 4. Lugares × povos **[PROPOSTO AP-04]**

Povos presentes conforme o doc 01; nenhum lugar tem menos de dois povos nativos.

| Lugar | Nativos | Forasteiros (visitantes avulsos) |
|---|---|---|
| Campoalvo | Humano; Pequenino; Luminar | Elfo; Anão; Gnomo |
| Vaudouro | Humano; Pequenino; Nascido da Maré | Goblin; Ratoide |
| Torrefronte | Humano; Luminar | Elfo; Anão; Escamado |
| Alvamar | Humano; Abissal Costeiro; Sereiano; Nascido da Maré; Tartarino | Elfo; Gnomo |
| Enseada-Vigia | Humano; Sereiano; Tartarino; Abissal Costeiro | Elfo |
| Concharia | Tartarino; Sereiano; Humano; Nascido da Maré | — |
| Torreclara | Nascido do Vento; Estelar; Humano; Luminar | — |
| Estrelado | Estelar; Luminar; Humano; Nascido do Vento | — |
| Pouso-das-Brisas | Nascido do Vento; Humano; Pequenino | Goblin |
| Meiovau | Humano; Pequenino; Luminar; Nascido da Maré; Nascido do Vento | visitantes avulsos e chegados do Véu (§5.1) |

## 5. Forasteiros **[PROPOSTO AP-05]**

Os 25 de M-05 §4 (coluna Humanos = F, lida por script). **Forasteiro** = origem fora de Azeroth: residente avulso, viajante, mercador, estudioso ou chegado; **motivo** é possibilidade, não destino do povo. **Origem e caminho: desconhecidos**; nenhuma linha cria rota, tratado ou comunidade grande, nem representa outro reino (contrato A2 e §6).

| ID | Povo | Onde aparece | Motivos possíveis (ASSUMINDO) |
|---|---|---|---|
| `elfo` | Elfo | Campoalvo, Torrefronte, Alvamar, Enseada-Vigia | troca, visita, notícia de mar |
| `anao` | Anão | Campoalvo, Torrefronte | ofício de pedra, acerto de carga |
| `gnomo` | Gnomo | Campoalvo, Alvamar | oficina, estudo |
| `goblin` | Goblin | Vaudouro, Pouso-das-Brisas | troca, recados |
| `homem-rato` | Ratoide | Vaudouro | troca, carga |
| `lizardino` | Escamado | Torrefronte, Meiovau (chegado, §5.1) | passagem; chegada |
| `tiefling` | Nascido do Crepúsculo | Meiovau (chegado, §5.1) | chegada |
| `vampirico` | Noctívago | Meiovau (chegado, §5.1) | chegada |
| `orcano` | Orcano | sem lugar fixo | passagem avulsa |
| `draconato` | Draconato | sem lugar fixo | passagem avulsa |
| `goliath` | Altaneiro | sem lugar fixo | passagem avulsa |
| `tabaxi` | Felídeo Errante | sem lugar fixo | passagem avulsa |
| `kenku` | Corvídeo | sem lugar fixo | passagem avulsa |
| `satiro` | Silvano Festivo | sem lugar fixo | passagem avulsa |
| `centauro` | Centauro | sem lugar fixo | passagem avulsa |
| `hobgoblin` | Goblin de Legião | sem lugar fixo | passagem avulsa |
| `homem-corvo` | Corvário | sem lugar fixo | passagem avulsa |
| `homem-raposa` | Vulpino | sem lugar fixo | passagem avulsa |
| `homem-coelho` | Leporino | sem lugar fixo | passagem avulsa |
| `homem-javali` | Porcino | sem lugar fixo | passagem avulsa |
| `elemental-terra` | Nascido da Rocha | sem lugar fixo | passagem avulsa |
| `onirico` | Onírico | sem lugar fixo | passagem avulsa |
| `cristalino` | Cristalino | sem lugar fixo | passagem avulsa |
| `construto` | Construto Desperto | sem lugar fixo | passagem avulsa |
| `metamorfo` | Moldável | sem lugar fixo | passagem avulsa |

8 com lugar, 17 sem lugar fixo (só viajantes avulsos, sem residir e sem rota). Nomes de povo de mesma raiz **não se confundem**: Corvídeo (`kenku`) não é Corvário (`homem-corvo`); Escamado (`lizardino`) não é Escamado Miúdo (`kobold`, sem papel aqui); Goblin de Legião não é Goblin.

### 5.1 Chegados do Véu (As Fugas do Véu, doc 05 §4)
`tiefling`, `lizardino` e `vampirico` são os três povos F que M-05 §5 liga às Fugas. Em Meiovau são **chegados**: acolhidos pelo Costume da Porta (L-10), **não alocados a lugar, facção ou coroa** e sem culpa por origem. Motivo, número, começo e rota: não afirmados (contrato §6).

### 5.2 Fluxos × matriz (P-2 do plano, conferido por script)
**As Costas:** `humano`, `tritao`, `sereiano` e `tortle` são N na coluna Humanos; `elfo` aparece só como visitante, sem ser parte do fluxo. **As Fugas do Véu:** `tiefling`, `lizardino` e `vampirico` são F. **Fluxos 1 a 3** (Descida, Rota do Sal, Corredores Largos): não chegam a Azeroth; `anao`, `gnomo` e `construto` aparecem só como visitantes avulsos sem ligação com a Descida, e `goblin` e `homem-rato` sem ligação com a Rota do Sal; a matriz não muda.

## 6. Papéis por povo e lacunas **[PROPOSTO AP-06]**

Papel = herói, guardião ou benfeitor já nomeado (doc 01 parte 2, §3). `L` = herói lendário falecido; `V` = herói vivo. Governantes e herdeiros (N-01 a N-06) ficam fora da tríade e são listados à parte.

| Povo | Herói | Guardião | Benfeitor | Governante ou herdeiro |
|---|---|---|---|---|
| Humano | Wendra Aterro-Primeiro (L); Halvard Limite-Firme (L); Ostrel Chave-Primeira (L); Garvane Remo-Firme (V) | Perrin Farol-Aceso; Casimar Código-Limpo; Hollis Torre-do-Meio | Sabeth Pátio-Aberto | Odile Medida-Cheia |
| Pequenino | nenhum nomeado | Tolla Mesa-Posta | Dodda Prato-Cheio; Larkin Barcaça-Cheia; Poppel Mesa-de-Viajante | Tomlin Horta-Larga (herdeiro) |
| Luminar | nenhum nomeado | Helion Vigília-do-Celeiro; Solenne Vigília-Longa; Eloh Cúpula-Acesa | Clarel Lume-de-Torre; Auriven Porta-Acesa | nenhum |
| Tartarino | Mossa Casco-Primeiro (L) | Dorgan Escudo-Lento; Orrume Abrigo-de-Casco | nenhum nomeado | nenhum |
| Abissal Costeiro | Sundar Licença-de-Cais (L) | nenhum nomeado | Ressan Concha-Aberta | Corlan Cais-Primeiro |
| Nascido da Maré | nenhum nomeado | Ebbo Praia-Rasa | Marnu Espuma-Dada | Rilla Espuma-Fina (herdeira) |
| Nascido do Vento | Raffe Sopro-Primeiro (L); Zefir Pipa-Caída (L) | nenhum nomeado | nenhum nomeado | Brevo Rumo-Alto |
| Sereiano | Nerella Canto-da-Barra (V) | nenhum nomeado | Corali Fundo-de-Coral | nenhum |
| Estelar | Orbane Rota-Perdida (L) | nenhum nomeado | Ixra Noite-Aberta | Astrel Noite-Clara (herdeiro) |

Lacunas **ficam abertas** (a tríade por lugar já está cumprida em 10 de 10): 3 povos sem herói (Pequenino, Luminar, Nascido da Maré), 4 sem guardião (Abissal Costeiro, Nascido do Vento, Sereiano, Estelar) e 2 sem benfeitor (Tartarino, Nascido do Vento). Nenhum NPC é criado aqui para fechá-las (como D-110, opção C).

## 7. Representação e o que este documento não decide **[PROPOSTO AP-07]**

- Nenhum povo é só vilão, só herói ou só funcionário; 6 dos 9 têm herói (vivo ou lendário), 5 têm guardião e 7 têm benfeitor (§6). Raça não determina cargo (doc 01, parte 2 §4).
- **Cultura, religião e língua** por povo: não decididas (M-05 §7); nomes: M-10 §11 a §15.
- **Quantidade de gente e proporção entre povos:** R5-3.2; nada de \"maioria\" de um povo sem decisão. **Relação entre povos nos lugares:** R5-4.3.
- **Povos de Aeltharyn** (Humano, Pequenino e Abissal Costeiro em linhagem própria): nenhuma cidade ou NPC de Aeltharyn alterado; nada liga um povo de Azeroth ao Mistério C, à Neblina Longa, ao Conflito G ou a desaparecimento algum.
- **Povos do Véu** (Nascido do Crepúsculo, Escamado, Noctívago): só chegados ou visitantes avulsos; nada além do que M-05 §5 já diz.
- **Povos das Coroas e do Vale:** só visitantes avulsos sem rota; nenhum fluxo parte de Azeroth para o Véu, as Coroas ou o Vale.

## 8. Conferências **[PROPOSTO AP-08]**

| Item | Resultado |
|---|---|
| Nativos | 9 de 9, todos em ID do catálogo, todos em ao menos 2 lugares |
| Forasteiros | 25 de 25 (8 com lugar, 17 sem lugar fixo); 9 + 25 + 16 sem papel = 50 |
| Sub-raças | 8 usadas, todas existem no catálogo com a raça-mãe correta; 4 povos sem sub-raça no catálogo |
| P-4 | linhagens de Humano, Pequenino e Abissal Costeiro próprias, sem parentesco com Aeltharyn |
| Matriz × fluxo (M-05 §5) | Costas: humano, tritão, sereiano e tartarino N; Fugas: tiefling, lizardino e vampírico F; fluxos 1 a 3 fora |
| Lugares com ≥ 2 nativos | 10 de 10 |
| Povos dos lugares × doc 01 | idênticos nos 10 lugares |
| NPCs novos | 0 |
| Mestiço alocado | 0 |
| Número, rota ou tempo | 0 |

## 9. Decisão pendente

**D-147:** matriz de povos, sub-raças, linhagens próprias (P-4), 25 forasteiros (17 sem lugar fixo), três chegados do Véu e lacunas de papéis abertas. **ASSUMINDO:** onde cada sub-raça aparece, os motivos dos forasteiros, `humano-das-fronteiras` em Enseada-Vigia e a escolha da outra sub-raça em cada par compartilhado com Aeltharyn.
