# Azeroth — modelo operacional, relógios e oportunidades (doc 06)

**Estado:** R5-1.7, D-148 aceita pela continuação (07/11/2026) · **Data:** 2026-11-07
**Etiqueta:** tudo é **[PROPOSTO CA-01 a CA-10, CFA-01 a CFA-10, OA-01 a OA-10]**; só vira cânone com a aprovação (D-148). Cânone de apoio: doc 01 (partes 1 a 4), doc 03, doc 04, doc 05, doc 10; molde: `reinos/04-.../06-...` e `reinos/03-.../06-...`.
**Regras seguidas:** relógio nunca avança por calendário; avanço exige causa observável, nexo e localidade; nenhum relógio explica o Mistério C, a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G, nem dá causa a mistério do Véu, das Coroas ou do Vale; nenhum envolve outro reino; nenhum número de população, estoque, preço ou tempo (D-63). O doc 11 do plano é a ata de fechamento (R5-8.2); R5-1.7 não cria arquivo com esse número.
**ASSUMINDO:** \"o que trava\", \"quem paga\" e \"decisão no segmento 5\" são leitura editorial derivada só do que as fichas e os docs 01, 04 e 05 já dizem; o **número de relógios** (dez, um por lugar) e o **progresso inicial** são escolha de partida e o autor pode trocá-los. \"Segmentos\" são a escala de 6 do molde, não medida de tempo.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação e a safra só criam condição (calendário de Azeroth: R5-3.4).
- Todo avanço exige **causa observável** (algo que aconteceu na cena), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho observável já existe na cena.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Mistérios (doc 01 parte 4) não têm relógio; evidência se acumula na narração.
- Nenhum relógio decide a precedência entre as coroas, as lacunas intencionais das leis (L-01 a L-10), nem o que o doc 04 deixou em disputa (prazo e sustento do chegado, quem lê o registro, quem taxa a passagem, de quem são os faróis e os sinais).
- A **Mesa de Meiovau** só arbitra se as partes a levarem, sem execução forçada e sem decidir a precedência (doc 04 §3); onde um relógio cita a Mesa, ela decide só o ponto concreto escrito.
- Nenhum relógio envolve Aeltharyn, o Véu, as Coroas ou o Vale. As Costas e As Fugas do Véu ficam **do lado de Azeroth**, sem causa, número ou rota (contrato §6 e §7).

## 2. Custo por segmento (vale para os 10)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, vigia, material | a dependência da economia do lugar (doc 05 §2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o governante, o conselho ou a Mesa (conforme o relógio) precisa decidir; nenhuma saída é grátis |
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

## 4. Conflitos com causa concreta

Cada problema atual da parte 1 do doc 01, com causa **observável**, quem perde e saída possível com custo. Cobrem preço do grão, taxa e passagem, registro de limite, luz e sinal, obra, previsão e acolhida; as três rivalidades entre coroas (Trigal × Cais, Cais × Vento, Vento × Trigal) aparecem como pano de fundo, sem solução.

| ID | Lugar | Causa concreta (observável) | Quem perde | Saída possível e custo | Relógio |
|---|---|---|---|---|---|
| CFA-01 | Campoalvo | em dia de pesagem, compradores contestam o preço do pão e a Coroa do Trigal responde que o reino come por causa dela (preço e precedência) | compradores; o Conselho dos Celeiros | preço fixado em sessão com compradores ouvidos; custo de precedência | CA-01 |
| CFA-02 | Vaudouro | a mesma barcaça é cobrada por duas coroas ao passar de um rio ao outro (taxa de passagem) | quem passa; os barqueiros | taxa única acertada na Mesa; custo de renda de uma coroa | CA-02 |
| CFA-03 | Torrefronte | um chegado é registrado e a guarnição e a vigia leem o registro de modos diferentes (responsabilidade de limite) | os chegados; a guarnição | leitura combinada diante de testemunhas; custo de autoridade | CA-03 |
| CFA-04 | Alvamar | a Alfândega contesta se a taxa serve ao porto ou à Coroa quando um navio descarrega fora do molhe (arrecadação) | a Alfândega; os barcos pequenos | regra de descarga e de taxa pactuada; custo de renda | CA-04 |
| CFA-05 | Enseada-Vigia | o Cais ordena aos faroleiros e o Vento pede os sinais da costa, deixando a luz entre duas disputas (comando da luz) | faroleiros; pilotos | luz mantida pela Irmandade com as duas coroas avisadas; custo de comando | CA-05 |
| CFA-06 | Concharia | o Cais quer abrir mais um canal e os Guardas do Santuário dizem que isso mata o jardim (obra) | o jardim; os Construtores de Concha | obra adiada ou traçado discutido com os dois lados; custo de prazo ou de jardim | CA-06 |
| CFA-07 | Torreclara | um código muda sem aviso e as outras coroas temem depender de quem pode calar uma torre (aviso) | a Casa dos Sinais; as coroas que dependem do aviso | aviso combinado a cada mudança; custo de sigilo | CA-07 |
| CFA-08 | Estrelado | Estelares e Luminares dão previsões diferentes e as coroas pedem a que lhes serve (previsão) | os Leitores do Céu; a Vigília da Cúpula | previsões citadas lado a lado; custo de autoridade | CA-08 |
| CFA-09 | Pouso-das-Brisas | Trigal e Cais pedem sinais próprios e o Posto recusa por serem da linha do Vento (sinal) | o Posto dos Pipeiros; as coroas sem sinal | horário ou fio dividido por acordo; custo de controle | CA-09 |
| CFA-10 | Meiovau | a Mesa precisa decidir prazo e sustento de quem chega e as três coroas discordam (acolhida) | os chegados; o Albergue | prazo e sustento por caso; custo de dureza ou de sustento | CA-10 |

## 5. Oportunidades de aventura (uma por lugar)

Oportunidade = ponto de entrada, não trama. Nenhuma exige herói predestinado; todas podem ser ignoradas, resolvidas por NPC ou piorar por falta de ação. Quem pede é pessoa já nomeada nos docs 01 e 04.

| ID | Lugar | Gancho | Quem pede | Em jogo | Ligação |
|---|---|---|---|---|---|
| OA-01 | Campoalvo | conferir a medida de um celeiro na Casa das Pesagens diante de uma testemunha do Conselho | Helion Vigília-do-Celeiro | confiança do Conselho; preço do pão | CA-01, MA-01 |
| OA-02 | Vaudouro | escoltar uma barcaça de um rio ao outro sem pagar duas vezes nem fechar a comporta | Garvane Remo-Firme | a passagem; a Casa dos Barqueiros | CA-02, MA-02 |
| OA-03 | Torrefronte | reler com Solenne as páginas do Livro diante de uma testemunha da vigia, sem tomar o lado de nenhuma coroa | Solenne Vigília-Longa | confiança da guarnição; o registro | CA-03, MA-03 |
| OA-04 | Alvamar | acompanhar uma ronda da Guarda da Corrente no molhe e anotar o que entra, sem acusar ninguém | Dorgan Escudo-Lento | a corrente; a Alfândega | CA-04, MA-04 |
| OA-05 | Enseada-Vigia | guiar um navio pela barra à noite sem entregar a luz a nenhuma coroa | Nerella Canto-da-Barra | a barra; a confiança dos pilotos | CA-05, MA-05 |
| OA-06 | Concharia | levar Orrume e Corali ao Canal Estreito para olhar o coral antes de qualquer obra | Corali Fundo-de-Coral | o jardim; o canal | CA-06, MA-06 |
| OA-07 | Torreclara | entregar um aviso de torre às outras coroas com o código limpo, sem abrir o código-mestre | Casimar Código-Limpo | confiança da Casa dos Sinais; o aviso | CA-07, MA-07 |
| OA-08 | Estrelado | ouvir as duas leituras do mesmo céu e levar ambas à Mesa sem escolher uma | Eloh Cúpula-Acesa | a Mesa dos Astros; a previsão | CA-08, MA-08 |
| OA-09 | Pouso-das-Brisas | soltar uma pipa de teste e anotar quem responde na linha, sem dar sinal a coroa alguma | Hollis Torre-do-Meio | a linha de torres; a confiança do Posto | CA-09, MA-09 |
| OA-10 | Meiovau | levar um chegado ao registro e ao Albergue sem tomar o lado da Mesa Estreita nem do Albergue | Tolla Mesa-Posta | o Costume da Porta; o chegado | CA-10, MA-10 |

## 6. Como um mistério ou relógio muda

- Mistério: **evidência cumulativa** (pelo menos duas provas independentes) antes de a narração fixar uma hipótese; a hipótese fixada obedece aos vetos do doc 01 parte 4.
- Relógio: **1 segmento por causa observável**, com a escala da §2; ao completar, vira mudança de estado, não solução.
- Retorno de relógio (trava efetiva) tira **1 segmento** por vez, com custo real (§2).
- Nenhum desses movimentos reescreve uma ficha, uma lei, um acordo ou uma regra aprovada.

## 7. Conferências

- 10 relógios; 10 conflitos; 10 oportunidades (1 por lugar); 10 mistérios (doc 01 parte 4).
- 0 NPC, lugar, facção ou rota novo; 0 número, tempo ou distância; 0 mestiço; 0 ID de relógio igual a ID dos reinos 1 a 4.
- Termos de fronteira (Mistério C, Neblina Longa, Mistérios D e E, Conflito G, foz do Argente, Thalanor) **só em vetos** (doc 01 parte 4); nenhum relógio os toca.
- Todo relógio, conflito e oportunidade cita só entidades dos docs 01, 03, 04 e 10.

## 8. Decisão pendente

**D-148:** aprovar os dez mistérios (doc 01 parte 4), os dez relógios e o progresso inicial, os dez conflitos e as dez oportunidades. Opções: **A.** aprovar tudo; **B.** trocar mistério, relógio, progresso inicial ou o número de relógios (diga qual). **Recomendação: A.**

## 9. Contrato anti-invenção (R5-6.4)

**Estado:** R5-6.4, D-169 aceita pela continuação · **Etiqueta:** **[PROPOSTO]**. Vale para qualquer proposta da IA sobre Azeroth. Herda a lista de verificação dos contratos dos reinos 1 a 4 (`reinos/01-.../06-...md` §11 e §11.1; `reinos/02-.../06-...md` §9; `reinos/03-.../06-...md` §9; `reinos/04-.../06-...md` §9), sem alterá-los, e se apoia em `14-camadas-de-informacao.md`, `13-misterios-limites.md`, `12-relogios.md`, doc 07 e `plano/CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md` (§6 e §7). A separação em fixo, livre, registro e proibido é leitura editorial (ASSUMINDO).

### 9.1 Verificação antes de aceitar uma proposta

- Os lugares e entidades existem (registro mestre) ou foram criados como conteúdo novo permitido (§9.4)?
- A cronologia é compatível (doc 01 parte 3 e parte 5: Contagem das Chaves; sem ano, era ou evento de Aeltharyn, das Coroas, do Véu ou do Vale como fato de Azeroth)?
- O agente poderia saber disso (`12-conhecimento.csv`, camada do fato)?
- O agente tem meios e autoridade (`12-competencias.csv`; o governante de uma coroa não governa outra, a Mesa arbitra o ponto concreto e não decide precedência, o presidente da Mesa não vota: doc 04)?
- A rota e o tempo são conhecidos? Toda duração do reino é `desconhecida` (doc 07).
- A proposta transforma hipótese em fato sem evidência? (mistério: pelo menos duas provas independentes, §6)
- A consequência contradiz estado persistente, lei (L-01 a L-10), acordo ou decisão anterior?
- O segredo é revelado sem caminho de descoberta cumprido?
- A proposta trata um povo ou raça como leal, vilão ou "o governo" por natureza? (contrato de fronteira §6 item 10)

Se não puder ser confirmado, a IA reduz a precisão, mantém a incerteza ou pede aprofundamento. Não preenche lacuna com número exato nem fato definitivo para a cena fluir.

### 9.2 Quatro classes

| Classe | O que é | O que a IA faz |
|---|---|---|
| **Fixo** | Tudo o que já tem ID ou fonte: os 10 lugares (todos fixos), as 15 facções, as 38 pessoas do registro (12 principais, 18 sem ficha de principal, 8 heróis lendários **falecidos**), os 10 relógios (CA-01 a CA-10), os 10 mistérios (MA-01 a MA-10) e o "a verdade não pode ser", as 10 leis (L-01 a L-10), os 12 eventos-tipo, as 30 sementes, as três coroas rivais (Trigal, Cais e Vento) e a Mesa de Meiovau (três chaves, sem voto do presidente), os acordos e a sucessão por coroa, o Costume da Porta, as ligações do doc 07 **sem duração**, os recursos, o calendário (Contagem das Chaves), os 44 fatos e a camada de cada um, os resumos e as 7 cenas-teste, e as lacunas (`desconhecido`) | Usa; não altera nem contradiz. Lacuna continua lacuna |
| **Livre** | Detalhe de cena sem consequência persistente: poeira, cheiro, objeto comum, figurante anônimo sem ID, fala dentro do tom (`12-voz.csv`) | Inventa sem registro, se não contradisser um fixo, não prometer recompensa e não criar dever, dívida ou mudança de estado |
| **Exige registro** | O que muda o mundo ou cria entidade: morte ou ferimento de pessoa com ID, troca de governante ou de herdeiro, acordo feito, renovado ou quebrado, relógio que avança, NPC, facção, organização ou lugar recorrente novo, consequência de decisão do jogador, fato restrito ou secreto revelado | Pode **propor**; só vale depois de registrado com estado anterior, novo estado, momento, causa, agentes, quem sabe e grau de certeza (`reinos/01-.../02-arquitetura-mundo-persistente.md` §2) |
| **Proibido** | §9.3 | Não faz; reduz a precisão, mantém a incerteza ou pede aprofundamento |

### 9.3 Proibido (nenhuma proposta, nenhum registro da IA)

- decidir a precedência entre as coroas (L-01 a L-10) ou dar a uma coroa o direito exclusivo sobre Vaudouro, Meiovau, os faróis, as torres ou o canal de Concharia; criar uma quarta coroa, um rei ou um segundo governante por coroa; dar voto ao presidente da Mesa ou fazer a Mesa abrir sem as três chaves;
- nomear o resultado de uma sucessão, dizer como um governante foi reconhecido ou se algum herdeiro foi revogado (`desconhecido`, doc 04 §2.2);
- definir crime grave contra pessoa, exílio ou pena de morte (doc 04: `desconhecido`);
- criar um 11º lugar ou ressuscitar um dos 8 heróis lendários; fazer feito de herói lendário virar fato;
- declarar guerra, aliança, tratado, bloqueio ou crise de Azeroth com outro reino (contrato §6 item 5a); nomear inimigo do reino; fazer de qualquer povo "o inimigo", "o governo" ou "leal" por raça;
- resolver um mistério ou escolher uma das hipóteses abertas (MA-01 a MA-10 e doc 01 parte 4);
- alocar mestiço a lugar, facção ou reino (D-64);
- inventar número de população, estoque, preço, tributo, distância, duração, safra, maré ou estação que o texto deixa desconhecido;
- conceder efeito mecânico por nome, título ou lore;
- revelar fato restrito ou secreto sem o caminho de descoberta cumprido, ou mudar a camada de um fato;
- avançar relógio sem gatilho observável (`12-relogios.csv`) ou por calendário; usar estação como prazo (doc 01 parte 5);
- criar rota, tempo, passagem ou distância entre Azeroth e Aeltharyn, o Véu, as Coroas ou o Vale, além de As Costas (por mar, só o lado de cá) e das Fugas do Véu (só a chegada em Meiovau); criar fluxo de Azeroth para outro reino (contrato §6 itens 5 e 14);
- explicar o Mistério C, o Mistério D, o Mistério E, a Neblina Longa ou o Conflito G; dar motivo, número ou começo às Fugas do Véu; afirmar a fronteira com o Véu ou quem governa a foz do Argente (P-3);
- dar a Lei de Proteção de Refugiados de Aeltharyn a Azeroth; fazer NPC de outro reino agir a mando de Azeroth ou vice-versa; dar parentesco entre linhagens de Azeroth e de Aeltharyn (P-4);
- dar a evento de Azeroth ano de Aeltharyn ou sincronizar eras (contrato §6 item 12);
- usar nome do registro mestre dos reinos 1 a 4 ou o termo "Pacto" para sugerir ligação com pactos de outros reinos;
- alterar população, cargo, idade ou ID de entidade dos reinos 1 a 4.

### 9.4 Casos pedidos

| Pedido | Regra |
|---|---|
| **Novo chefe, governante ou herdeiro** | Proibido criar. Cada coroa tem um governante e um herdeiro reconhecido; troca só pelo costume do doc 04 §2.2, cujo desfecho e momento são `desconhecidos`; a IA não o conduz. Chefe de facção com papel recorrente é proposta ao autor, nunca fato |
| **Novo lugar** | Proibido. Barraca, cais, torre ou marco sem ID é detalhe livre enquanto não tiver função recorrente. Para virar entidade, entra como **proposta** ao autor com lugar-mãe, coroa, povo do catálogo e fonte "proposto"; só vira ID depois de aprovada e registrada no registro mestre |
| **Guerra** | A IA não declara. Escalada local (taxa contestada, comporta fechada, torre calada, canal aberto) pode ser proposta como oportunidade condicionada a gatilho observável, com custos; acordo quebrado não é guerra automática |
| **NPC, facção ou organização nova** | Figurante sem ID: livre. Com papel recorrente: proposta com `npc.` ou `faction.` novo, lugar, facção-mãe (ou "instituição"), povo do catálogo e fonte "proposto"; nunca com segredo, relação ou conhecimento que mude um mistério; nome conferido contra os reinos 1 a 4 |
| **Morte ou ferimento de pessoa com ID** | Exige registro com causa e agentes; a IA não mata para dar peso à cena |
| **Rota ou tempo de viagem** | Proibido inventar: toda duração é `desconhecida` (doc 07). Pode dizer que o tempo é desconhecido |
| **As Costas e As Fugas do Véu** | Só o lado de cá, em termos qualitativos, como nas cenas do porto e da assembleia (`15-cenas-de-teste.md`); sem causa, sem número, sem origem, sem rota |

### 9.5 Teste rápido (4 perguntas, em ordem)

1. Já existe ID ou fonte? **Fixo**: usar como está.
2. Está na lista proibida? **Não fazer.**
3. Muda estado, cria entidade ou revela restrito ou secreto? **Propor e registrar** antes de tratar como fato.
4. Nenhuma das anteriores e sem consequência? **Livre.**

Em dúvida entre duas classes, vale a mais restritiva.

### 9.6 Decisão pendente

**D-169:** aprovar este contrato. Opções: **A.** aprovar (recomendada); **B.** mover item entre classes ou trocar caso (diga qual). **Aceita pela continuação (07/11/2026); reversível.**
