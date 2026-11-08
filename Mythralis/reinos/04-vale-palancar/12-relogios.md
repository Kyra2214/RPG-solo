# 12 — Relógios: gatilhos, trava, custo e inação (R4-5.1)

**Estado:** R4-5.1, D-124 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria relógio, NPC, lugar ou fato novo**: reúne os 8 relógios CP-01 a CP-08 de `06-modelo-operacional-e-aventuras.md` (D-111) com gatilhos observáveis, custo por segmento e efeito da inação. Dados em `12-relogios.csv` (12 colunas, mesmo molde dos reinos 2 e 3).
**ASSUMINDO:** "o que trava", "quem paga" e "efeito da inação" seguem como no doc 06; os **gatilhos** são só o "efeito da inação" reescrito como lista; a **decisão no segmento 5** é o Trato do lugar, a Assembleia de Clãs ou o Capitão de Marcha, conforme o "ao completar" do doc 06 e as competências do doc 04 §3 e §6.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação e a migração só criam condição (R4-3.4).
- Todo avanço exige **causa observável** (um gatilho da tabela), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho já existe na cena. Sem gatilho, nada muda.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Nenhum relógio decide as lacunas das leis (L-01 a L-10), a regra de escolha do Capitão, o direito à reserva, a posse das pedras, quem cobra o desembarque, a preferência na cisterna nem quem tem razão sobre o capim. **Nenhum relógio envolve Aeltharyn, as Coroas, o Véu ou o Pacto Humano.**
- Retorno de relógio (trava efetiva) tira **1 segmento** por vez, com custo real.

## 2. Custo por segmento (vale para os 8)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, vigia, material | a dependência da economia do lugar (doc 05 §2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o órgão da coluna "decisão no segmento 5" precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | muda estado, não resolve a trama |

## 3. Os oito relógios

### `clock.reserva-em-ano-ruim` — CP-01 A reserva sem regra (2/6)
| Campo | Texto |
|---|---|
| O que move | clã de fora pedindo abrigo e reserva no celeiro; guardiões recusando a entrega; pasto fraco observado |
| Gatilhos observáveis de avanço | novo pedido de reserva observado; nova recusa de entrega observada |
| O que trava (ASSUMINDO) | entrega em ordem pelo Trato do Celeiro; doação de Pocha a quem o celeiro não atende; acordo na Assembleia sobre a ordem de entrega |
| Quem paga (ASSUMINDO) | os clãs de fora; a Mesa da Partilha; os guardiões, acusados de egoísmo |
| Decisão pública no segmento 5 (ASSUMINDO) | Assembleia de Clãs (Terreiro-Maior), com o Trato do Celeiro ouvido |
| Ao completar (segmento 6) | a Assembleia é chamada a dizer o que o celeiro deve a quem chega em ano ruim; **o direito à reserva não é decidido aqui** (L-07 segue aberta) |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido de reserva ou nova recusa observados |
| Entidades | `city.tabuleiro-velho`, `npc.moura-mel-guardado`, `npc.pocha-ninhada-farta`, `faction.guardioes-da-reserva`, `faction.mesa-da-partilha` |
| Fonte | doc 01 parte 1 §2.1 e parte 2 (N-04, N-05); doc 04 §9 e §10; doc 05 §1 (L-07); MPL-01, MPL-02 |

### `clock.dupla-cobranca` — CP-02 Dois grupos, um vau (3/6)
| Campo | Texto |
|---|---|
| O que move | quem atravessa pagando duas vezes ou nenhuma; cobrança de um grupo contestada pelo outro em público |
| Gatilhos observáveis de avanço | nova travessia contestada observada |
| O que trava (ASSUMINDO) | recados de Quira entre os dois grupos; acerto na Tenda dos Recados; apelo do Trato do Vau à Assembleia |
| Quem paga (ASSUMINDO) | quem atravessa; os rebanhos parados na margem; os dois grupos, que perdem travessia |
| Decisão pública no segmento 5 (ASSUMINDO) | Assembleia de Clãs (Terreiro-Maior), por apelo do Trato do Vau (doc 04 §6); um dos dois grupos pode dizer a quem se paga antes |
| Ao completar (segmento 6) | um dos grupos ou a Assembleia precisa dizer em voz alta a quem se paga; **a quem pertencem as pedras não é decidido aqui** (L-04 segue aberta) |
| Efeito da inação (ASSUMINDO) | só avança com nova travessia contestada observada |
| Entidades | `city.rasovau`, `npc.zinu-troca-justa`, `npc.quira-recado-certo`, `faction.pedras-de-cima`, `faction.pedras-de-baixo` |
| Fonte | doc 01 parte 1 §2.2 e parte 2 (N-08, N-09); doc 04 §7 e §9; doc 05 §1 (L-04); MPL-03 |

### `clock.desembarque` — CP-03 O desembarque cobrado de dois jeitos (2/6)
| Campo | Texto |
|---|---|
| O que move | carga que chega com papel que o Vale não conhece; Receptores e Balança cobrando critérios diferentes na mesma rampa |
| Gatilhos observáveis de avanço | nova carga contestada observada |
| O que trava (ASSUMINDO) | guarda de carga no Barracão até o acerto; Vrissk aceitando carga sem conferir de onde vem; pacto de passagem do desembarque |
| Quem paga (ASSUMINDO) | quem desce e espera; os clãs vizinhos que protegem o pouso; o Trato do Desembarque |
| Decisão pública no segmento 5 (ASSUMINDO) | Trato do Desembarque (Praia-Pardo), guardião do pouso: Vrissk |
| Ao completar (segmento 6) | o Trato do Desembarque precisa dizer quem cobra e o que se reconhece como papel; **o que vale para o Vale não é decidido aqui** (L-05 segue aberta) e a Descida não ganha causa, número nem fim rio acima |
| Efeito da inação (ASSUMINDO) | só avança com nova carga contestada observada |
| Entidades | `city.praia-pardo`, `npc.vrissk-colonia-quieta`, `faction.receptores-do-pouso`, `faction.balanca-do-pouso` |
| Fonte | doc 01 parte 1 §2.3 e parte 2 (N-10); doc 04 §7 e §9; doc 05 §1 (L-05) e §3; MPL-04 |

### `clock.cisterna` — CP-04 A cisterna única (2/6)
| Campo | Texto |
|---|---|
| O que move | duas caravanas na cisterna ao mesmo tempo; recusa de água observada; queixa de caravana ou de clã sobre preferência |
| Gatilhos observáveis de avanço | nova espera de caravana observada; nova recusa de água observada |
| O que trava (ASSUMINDO) | turnos de vigia de Gruzk; atendimento em ordem pelo Trato do Poço; pacto de preferência na cisterna (nunca posto, doc 04 §10, item 4) |
| Quem paga (ASSUMINDO) | as caravanas que esperam; os clãs do pouso; os Vigias da Cisterna |
| Decisão pública no segmento 5 (ASSUMINDO) | Trato do Poço (Alvasal), chefe: Aruun |
| Ao completar (segmento 6) | o Trato do Poço precisa dizer quem tem preferência; **a regra de preferência não é decidida aqui** (L-06 segue aberta) e nada se afirma sobre o Ermo de Sal ou sobre Beira-Sal |
| Efeito da inação (ASSUMINDO) | só avança com nova espera ou recusa observada |
| Entidades | `city.alvasal`, `npc.gruzk-estandarte-baixo`, `faction.vigias-da-cisterna` |
| Fonte | doc 01 parte 1 §2.4 e parte 2 (N-03); doc 04 §9 e §10 (item 4); doc 05 §1 (L-06) e §4; MPL-05 |

### `clock.sucessao-do-capitao` — CP-05 O mandato que acaba (3/6)
| Campo | Texto |
|---|---|
| O que move | nova divergência pública entre Clãs da Prova e Clãs da Aclamação; Kharos adiando o anúncio; clã recusando a rodada |
| Gatilhos observáveis de avanço | nova divergência pública entre prova e aclamação observada; novo adiamento do anúncio observado |
| O que trava (ASSUMINDO) | ordem da reunião por Yorna; hospedagem neutra de Cassan na Ramada; rodada em que todos são ouvidos (L-01) |
| Quem paga (ASSUMINDO) | a Assembleia, que perde unidade; a Marcha, que fica sem rumo se o mandato acabar sem sucessor |
| Decisão pública no segmento 5 (ASSUMINDO) | Assembleia de Clãs (Terreiro-Maior), ordem da reunião por Yorna |
| Ao completar (segmento 6) | a Assembleia precisa declarar, em voz alta no Fogo do Meio, se vale prova ou aclamação; **a escolha não é decidida aqui** e o interregno (doc 04 §5) fica como saída possível, não como efeito automático |
| Efeito da inação (ASSUMINDO) | só avança com nova divergência pública ou novo adiamento observados |
| Entidades | `city.terreiro-maior`, `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`, `npc.kharos-casco-de-marcha`, `faction.clas-da-prova`, `faction.clas-da-aclamacao` |
| Fonte | doc 01 parte 1 §2.5 e parte 2 (N-01, N-02, N-12); doc 04 §5 e §10 (item 1); doc 05 §1 (L-01, L-03); MPL-06, MPL-08 |

### `clock.campo-da-marcha` — CP-06 Dois campos, um rumo (2/6)
| Campo | Texto |
|---|---|
| O que move | batedores trazendo notícias diferentes dos dois campos; clã sem pasto na estação reclamando ao Capitão |
| Gatilhos observáveis de avanço | nova queixa de clã sem pasto observada; novo relato de batedor observado |
| O que trava (ASSUMINDO) | conselho de Rukhan; consulta aos Tratos dos campos; reserva do Tabuleiro-Velho como retaguarda (lugar-mãe) |
| Quem paga (ASSUMINDO) | o clã que fica sem pasto; os Guardas da Marcha; os clãs que adiam a partida |
| Decisão pública no segmento 5 (ASSUMINDO) | Capitão de Marcha (Vazante): Kharos, diante dos clãs |
| Ao completar (segmento 6) | o Capitão precisa escolher o campo diante dos clãs; **qualquer escolha custa a algum clã** e a ordem que "parte um clã" segue como lacuna (L-03) |
| Efeito da inação (ASSUMINDO) | só avança com nova queixa de clã ou novo relato de batedor observados |
| Entidades | `city.vazante`, `npc.kharos-casco-de-marcha`, `npc.rukhan-retorno-certo`, `faction.guardas-da-marcha` |
| Fonte | doc 01 parte 1 §2.6 e parte 2 (N-01, N-07); doc 04 §3 e §9; doc 05 §1 (L-03); MPL-07 |

### `clock.guia-das-trilhas` — CP-07 Quem guia o rebanho (2/6)
| Campo | Texto |
|---|---|
| O que move | rebanho esperando guia no pouso; disputa pública sobre o que sobra no pouso; Vigias das Trilhas recusando a espera |
| Gatilhos observáveis de avanço | novo rebanho em espera observado; nova recusa de espera observada |
| O que trava (ASSUMINDO) | Tessun guiando sem tomar partido; pastagem de espera no Cerca de Espera; acordo do Trato da Encosta |
| Quem paga (ASSUMINDO) | os rebanhos parados; os Guias da Encosta; quem espera a passagem |
| Decisão pública no segmento 5 (ASSUMINDO) | Trato da Encosta (Encosta-Aberta), chefe `desconhecido` (doc 04 §8) |
| Ao completar (segmento 6) | o Trato da Encosta precisa dizer quem guia e quem fica com o que sobra; **não decide o que há do outro lado** (L-09) e não toca o Conflito G |
| Efeito da inação (ASSUMINDO) | só avança com novo rebanho em espera ou nova recusa observados |
| Entidades | `city.encosta-aberta`, `npc.tessun-salto-calado`, `faction.guias-da-encosta`, `faction.vigias-das-trilhas` |
| Fonte | doc 01 parte 1 §2.7 e parte 2 (N-06); doc 04 §9 e §10 (item 5); doc 05 §1 (L-09) e §5; MPL-09 |

### `clock.capim-disputado` — CP-08 Gado e caça no mesmo campo (2/6)
| Campo | Texto |
|---|---|
| O que move | marcas e mourões contestados; acusação pública de estrago do pasto; estação de pasto fraco observada |
| Gatilhos observáveis de avanço | nova contestação de marca observada; nova acusação de estrago observada |
| O que trava (ASSUMINDO) | divisão do campo por Tibbe; batida combinada fora da parição; acerto entre Clãs do Gado e da Caça na Tenda de Marcas |
| Quem paga (ASSUMINDO) | os dois lados, que perdem parte do campo; Ghorla, que sustenta a caça sem acordo |
| Decisão pública no segmento 5 (ASSUMINDO) | Trato dos Rebanhos (Gramal), chefe: Tibbe |
| Ao completar (segmento 6) | o Trato dos Rebanhos precisa dividir o campo diante dos dois lados; **quem tem razão sobre o capim não é decidido aqui** (L-08) e nenhuma raça é culpada |
| Efeito da inação (ASSUMINDO) | só avança com nova contestação de marca ou acusação observadas |
| Entidades | `city.gramal`, `npc.tibbe-salto-curto`, `npc.ghorla-fartura-dividida`, `faction.clas-do-gado`, `faction.clas-da-caca` |
| Fonte | doc 01 parte 1 §2.8 e parte 2 (N-11); doc 04 §9 e §10 (item 6); doc 05 §1 (L-08); MPL-10 |

## 4. Conferências

| Verificação | Resultado |
|---|---|
| Relógios | **8** (CP-01 a CP-08), iguais ao doc 06 (nome, progresso, entidades) |
| Progresso inicial | 2/6 em 6 relógios e 3/6 em 2; nenhum a menos de 1 segmento do fim |
| Relógios com "o que move" e "o que trava" | 8 de 8 |
| Gatilhos observáveis (2 por relógio, exceto 1 em 2 relógios) | 8 de 8 preenchidos; nenhum é data ou estação |
| Decisão no segmento 5 | 8 de 8 com órgão já documentado (Trato, Assembleia ou Capitão) |
| Campo vazio | 0 |
| Entidade fora do registro mestre | 0 |
| Relógio que envolve Aeltharyn, as Coroas, o Véu ou o Pacto Humano | 0 |
| Relógio que resolve mistério ou lacuna de lei | 0 |
| Número, preço ou tempo | 0 |

## 5. Decisão pendente

**D-124:** aprovar gatilhos, custo por segmento, decisor no segmento 5 e efeito da inação dos 8 relógios. Opções: **A.** aprovar; **B.** trocar gatilho, decisor no segmento 5 ou progresso de algum relógio (diga qual). **Recomendação: A.**
