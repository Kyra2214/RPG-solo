# Azeroth — Relógios: gatilhos, trava, custo e inação (doc 12, R5-5.1)

**Estado:** R5-5.1, D-161 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria relógio, NPC, lugar ou fato novo**: reúne os 10 relógios CA-01 a CA-10 de `06-modelo-operacional-e-aventuras.md` (D-148) com gatilhos observáveis, o que os move e trava, custo por segmento e efeito da inação. Dados em `12-relogios.csv` (12 colunas, mesmo molde dos reinos 2, 3 e 4).
**ASSUMINDO:** "o que trava", "quem paga" e "efeito da inação" seguem como no doc 06; a **decisão no segmento 5** é o governante, o conselho ou a Mesa, conforme o "ao completar" do doc 06 e as competências do doc 04 §4.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação e a safra só criam condição (R5-3.4).
- Todo avanço exige **causa observável** (um gatilho da tabela), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho já existe na cena. Sem gatilho, nada muda.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Nenhum relógio decide a precedência entre as coroas, as lacunas das leis (L-01 a L-10) nem o que o doc 04 deixou em disputa. **Nenhum relógio envolve Aeltharyn, as Coroas de Pedra, o Véu ou o Vale Palancar.**
- Retorno de relógio (trava efetiva) tira **1 segmento** por vez, com custo real.

## 2. Custo por segmento (vale para os 10)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, vigia, material | a dependência da economia do lugar (doc 05 §2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o órgão da coluna "decisão no segmento 5" precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | muda estado, não resolve a trama |

## 3. Os dez relógios

### `clock.medida-em-ano-ruim` — CA-01 A medida do pão (2/6)
| Campo | Texto |
|---|---|
| O que move | colheita fraca observada; pedido de abertura de celeiro recusado; preço do pão contestado em dia de pesagem |
| Gatilhos observáveis | novo preço contestado observado; novo pedido de abertura observado |
| O que trava (ASSUMINDO) | sessão do Conselho dos Celeiros que reconhece a medida; acordo no Salão das Medidas; Tomlin intermediando |
| Quem paga (ASSUMINDO) | quem compra o grão; o Conselho dos Celeiros, acusado de esconder; o Trigal, que perde precedência na praça |
| Decisão no segmento 5 | governante do Trigal, com o Conselho dos Celeiros ouvido |
| Ao completar | o governante do Trigal precisa dizer em voz alta quem ajusta a medida em ano ruim; **a precedência não é decidida aqui** e L-01 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com novo preço contestado ou novo pedido de abertura observados |
| Entidades | `city.campoalvo`, `npc.odile-medida-cheia`, `npc.tomlin-horta-larga`, `faction.conselho-dos-celeiros` |
| Fonte | doc 01 parte 1 §2.1 e parte 2 (N-01, N-04); doc 04 §2 e §10; doc 05 §1 (L-01); MA-01 |

### `clock.passagem-dos-rios` — CA-02 Duas taxas, uma barcaça (3/6)
| Campo | Texto |
|---|---|
| O que move | barcaça cobrada por duas coroas; Casa dos Barqueiros lucrando com a dúvida; comporta fechada sem aviso a uma das coroas |
| Gatilhos observáveis | nova barcaça cobrada duas vezes observada; nova comporta fechada sem aviso observada |
| O que trava (ASSUMINDO) | aviso às duas coroas antes de fechar comporta (L-02); apelo ao governante do Trigal; Garvane liberando barcaça sem taxa |
| Quem paga (ASSUMINDO) | quem passa; os barqueiros; o comércio entre Trigal e Cais |
| Decisão no segmento 5 | Mesa de Meiovau, a pedido das coroas (doc 04 §3) |
| Ao completar | a Mesa precisa dizer a quem se paga a passagem entre os rios; **quem taxa o que passa não é decidido aqui** e L-02 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com nova cobrança dupla ou nova comporta fechada sem aviso observadas |
| Entidades | `city.vaudouro`, `npc.garvane-remo-firme`, `faction.casa-dos-barqueiros`, `faction.alfandega-das-mares`, `faction.conselho-dos-celeiros` |
| Fonte | doc 01 parte 1 §2.2 e parte 2 (N-10); doc 04 §3 e §8; doc 05 §1 (L-02); MA-02 |

### `clock.livro-lido-de-dois-modos` — CA-03 O registro lido de dois modos (2/6)
| Campo | Texto |
|---|---|
| O que move | chegado registrado com leitura contestada entre guarnição e vigia; Solenne abrindo o limite em nome do Livro |
| Gatilhos observáveis | novo registro contestado observado; nova abertura do limite contestada observada |
| O que trava (ASSUMINDO) | revisão das páginas por Solenne com testemunha; acordo entre a guarnição e o Posto dos Pipeiros |
| Quem paga (ASSUMINDO) | os chegados; a guarnição, acusada de arbítrio; o Posto dos Pipeiros |
| Decisão no segmento 5 | Mesa de Meiovau, a pedido das duas coroas (doc 04 §3) |
| Ao completar | a Mesa precisa dizer quem lê o registro do Livro; **quem responde por quem chega não é decidido aqui** e L-03 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com novo registro contestado observado |
| Entidades | `city.torrefronte`, `npc.solenne-vigilia-longa`, `faction.guarnicao-do-limite`, `faction.posto-dos-pipeiros` |
| Fonte | doc 01 parte 1 §2.3 e parte 2 (N-07); doc 04 §3 e §9; doc 05 §1 (L-03); MA-03 |

### `clock.taxa-do-molhe` — CA-04 A taxa do molhe (3/6)
| Campo | Texto |
|---|---|
| O que move | nova descarga fora do molhe observada; Alfândega contestando a taxa em público; corrente frouxa observada |
| Gatilhos observáveis | nova descarga fora do molhe observada; nova contestação pública da taxa observada |
| O que trava (ASSUMINDO) | Guarda da Corrente registrando cada entrada; acordo entre a Alfândega e o governante do Cais; Rilla calando ou falando do navio |
| Quem paga (ASSUMINDO) | a Alfândega das Marés; os barcos pequenos; o porto, que perde a confiança dos pilotos |
| Decisão no segmento 5 | governante do Cais, com a Alfândega das Marés ouvida |
| Ao completar | o governante do Cais e a Alfândega precisam dizer em voz alta a quem serve a taxa; **a regra não é decidida aqui**, L-04 segue aberta e nada se afirma sobre quem descarrega fora do molhe |
| Efeito da inação (ASSUMINDO) | só avança com nova descarga fora do molhe ou nova contestação da taxa observadas |
| Entidades | `city.alvamar`, `npc.corlan-cais-primeiro`, `npc.dorgan-escudo-lento`, `npc.rilla-espuma-fina`, `faction.alfandega-das-mares`, `faction.guarda-da-corrente` |
| Fonte | doc 01 parte 1 §2.4 e parte 2 (N-02, N-05, N-08); doc 04 §2 e §10; doc 05 §1 (L-04); MA-04 |

### `clock.luz-entre-coroas` — CA-05 A luz entre duas coroas (2/6)
| Campo | Texto |
|---|---|
| O que move | ordem do Cais aos faroleiros contestada; pedido do Vento por sinais da costa; pilotos sem luz em noite de barra |
| Gatilhos observáveis | nova ordem contestada observada; novo pedido de sinal observado |
| O que trava (ASSUMINDO) | Irmandade dos Faroleiros mantendo a luz acesa; Pilotos da Enseada guiando sem tomar partido; Nerella |
| Quem paga (ASSUMINDO) | os faroleiros; os pilotos; quem chega à barra |
| Decisão no segmento 5 | Mesa de Meiovau, a pedido do Cais e do Vento |
| Ao completar | a Mesa precisa dizer a quem respondem os faroleiros e de quem são os sinais da costa; **de quem são os faróis não é decidido aqui** e L-05 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com nova ordem contestada ou novo pedido de sinal observados |
| Entidades | `city.enseada-vigia`, `npc.nerella-canto-da-barra`, `faction.irmandade-dos-faroleiros`, `faction.pilotos-da-enseada`, `faction.casa-dos-sinais` |
| Fonte | doc 01 parte 1 §2.5 e parte 2 (N-09); doc 04 §3 e §11; doc 05 §1 (L-05) e §3; MA-05 |

### `clock.canal-e-jardim` — CA-06 O canal e o jardim (2/6)
| Campo | Texto |
|---|---|
| O que move | pedido do Cais para abrir o canal; recusa dos Guardas do Santuário; coral novo observado no traçado |
| Gatilhos observáveis | novo pedido de obra observado; nova recusa de obra observada |
| O que trava (ASSUMINDO) | acordo entre Construtores de Concha e Guardas do Santuário; obra adiada; Alfândega pedindo prazo |
| Quem paga (ASSUMINDO) | o jardim; os Construtores de Concha; os Guardas do Santuário |
| Decisão no segmento 5 | governante do Cais, com os dois lados ouvidos |
| Ao completar | o governante do Cais precisa dizer se o canal abre diante dos dois lados; **se o canal fere o santuário não é decidido aqui** e L-06 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido de obra ou nova recusa observados |
| Entidades | `city.concharia`, `faction.construtores-de-concha`, `faction.guardas-do-santuario`, `faction.alfandega-das-mares` |
| Fonte | doc 01 parte 1 §2.6; doc 04 §9 e §10; doc 05 §1 (L-06); MA-06 |

### `clock.codigo-sem-aviso` — CA-07 O código sem aviso (3/6)
| Campo | Texto |
|---|---|
| O que move | nova mudança de código sem aviso observada; Trigal e Cais exigindo os códigos; torre calada em hora de aviso |
| Gatilhos observáveis | nova mudança de código sem aviso observada; nova exigência de código observada |
| O que trava (ASSUMINDO) | Casimar registrando cada mudança; aviso pela Casa dos Sinais; Brevo dividindo o código-mestre |
| Quem paga (ASSUMINDO) | a Casa dos Sinais; o Posto dos Pipeiros; as coroas que dependem do aviso |
| Decisão no segmento 5 | governante do Vento, com a Casa dos Sinais ouvida |
| Ao completar | o governante do Vento precisa dizer quem é avisado quando um código muda; **não decide se outras coroas têm sinais próprios** e L-07 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com nova mudança sem aviso ou nova exigência de código observadas |
| Entidades | `city.torreclara`, `npc.brevo-rumo-alto`, `npc.astrel-noite-clara`, `faction.casa-dos-sinais`, `faction.posto-dos-pipeiros` |
| Fonte | doc 01 parte 1 §2.7 e parte 2 (N-03, N-06); doc 04 §2 e §10; doc 05 §1 (L-07); MA-07 |

### `clock.leitura-do-ceu` — CA-08 Duas leituras do mesmo céu (2/6)
| Campo | Texto |
|---|---|
| O que move | duas previsões diferentes entregues a coroas diferentes; Estelares e Luminares discordando em público |
| Gatilhos observáveis | nova previsão divergente observada; nova discordância pública observada |
| O que trava (ASSUMINDO) | calendário de rota mantido; sessão conjunta no Pátio das Rotas; Eloh e Ixra citando as duas |
| Quem paga (ASSUMINDO) | os Leitores do Céu; a Vigília da Cúpula; as coroas que pedem previsão |
| Decisão no segmento 5 | Mesa de Meiovau, a pedido das coroas |
| Ao completar | a Mesa precisa dizer qual leitura as coroas citam; **quem interpreta o céu não é decidido aqui** e L-08 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com nova previsão divergente observada |
| Entidades | `city.estrelado`, `npc.astrel-noite-clara`, `faction.leitores-do-ceu`, `faction.vigilia-da-cupula` |
| Fonte | doc 01 parte 1 §2.8; doc 04 §3 e §11; doc 05 §1 (L-08); MA-08 |

### `clock.sinais-proprios` — CA-09 O sinal que cada coroa quer (2/6)
| Campo | Texto |
|---|---|
| O que move | pedido de Trigal ou Cais por sinal próprio; recusa do Posto dos Pipeiros; linha de torres calada para uma coroa |
| Gatilhos observáveis | novo pedido de sinal próprio observado; nova recusa observada |
| O que trava (ASSUMINDO) | Casa dos Sinais atendendo o pedido; Hollis ensinando o fio das pipas; acordo sobre horário de aviso |
| Quem paga (ASSUMINDO) | o Posto dos Pipeiros; as coroas pequenas de sinal; quem depende do aviso |
| Decisão no segmento 5 | governante do Vento, com a Mesa de Meiovau se uma coroa apelar |
| Ao completar | o governante do Vento precisa dizer diante das coroas se outra coroa pode ter sinal próprio; **não é decidido aqui** e L-07 segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido de sinal próprio ou nova recusa observados |
| Entidades | `city.pouso-das-brisas`, `npc.brevo-rumo-alto`, `faction.posto-dos-pipeiros`, `faction.casa-dos-sinais` |
| Fonte | doc 01 parte 1 §2.9; doc 04 §10 e §11; doc 05 §1 (L-07); MA-09 |

### `clock.acolhida-de-meiovau` — CA-10 Prazo e sustento do chegado (3/6)
| Campo | Texto |
|---|---|
| O que move | chegado novo sem registro; Mesa Estreita pedindo prazo antes do sustento; Albergue pedindo acolhida larga |
| Gatilhos observáveis | novo chegado contestado observado; novo pedido de prazo ou sustento observado |
| O que trava (ASSUMINDO) | Costume da Porta (L-10); Tolla ordenando a audiência; doação do Albergue |
| Quem paga (ASSUMINDO) | os chegados; o Albergue dos Chegados; a Mesa Estreita, acusada de dureza |
| Decisão no segmento 5 | Mesa de Meiovau, com as três chaves (L-09) |
| Ao completar | a Mesa precisa dizer prazo e sustento de um chegado; **os pontos em aberto de L-10 não são decididos aqui** e nenhum chegado é culpado por origem |
| Efeito da inação (ASSUMINDO) | só avança com novo chegado contestado ou novo pedido de prazo observados |
| Entidades | `city.meiovau`, `npc.tolla-mesa-posta`, `npc.auriven-porta-acesa`, `faction.albergue-dos-chegados`, `faction.mesa-estreita` |
| Fonte | doc 01 parte 1 §2.10 e parte 2 (N-11, N-12); doc 04 §7 e §10; doc 05 §1 (L-09, L-10) e §4; MA-10 |
**Conferência:** 10 relógios, progresso inicial de 2/6 a 3/6 (nenhum acima de 3), 0 relógio a menos de 1 segmento do fim. Nenhum relógio envolve Aeltharyn, o Véu, as Coroas, o Vale ou o Mistério C; nenhum tem NPC, lugar ou facção fora dos docs 01, 03 e 04.

## 4. Conferências

| Verificação | Resultado |
|---|---|
| Relógios no CSV × doc 06 | 10 de 10 |
| Relógio sem "o que o move" ou sem "o que trava" | 0 |
| Relógio sem gatilho observável, custo ou efeito da inação | 0 |
| Progresso inicial | 2/6 ou 3/6 (nenhum acima de 3) |
| Entidade fora do registro mestre | 0 |
| Relógio que envolve outro reino ou avança por calendário | 0 |
| Campo vazio | 0 |
| ID de relógio igual a ID dos reinos 1 a 4 | 0 |

## 5. Decisão pendente

**D-161:** aprovar os relógios com gatilhos, trava, custo e inação. Opções: **A.** aprovar; **B.** trocar gatilho, trava ou progresso inicial de um relógio (diga qual). **Recomendação: A.**
