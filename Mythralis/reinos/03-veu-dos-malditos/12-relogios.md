# 12 — Relógios: gatilhos, trava, custo e inação (R3-5.1)

**Estado:** rascunho de R3-5.1 para aprovação do autor (D-87) · **Data:** 2026-11-06
**Etiqueta:** **[PROPOSTO]**; **não cria relógio, NPC, lugar ou fato novo**: reúne os 8 relógios CV-01 a CV-08 de `06-modelo-operacional-e-aventuras.md` (D-74) com gatilhos observáveis, custo por segmento e efeito da inação. Dados em `12-relogios.csv` (12 colunas, mesmo molde do reino 2).
**ASSUMINDO:** "o que trava", "quem paga" e "efeito da inação" seguem como no doc 06 (leitura editorial das fichas); os **gatilhos** são só o "efeito da inação" reescrito como lista; a **decisão no segmento 5** é o guardião do pacto do lugar (doc 04 §4) ou o Conselho dos Pactuários (Morrovéu) e dos Selos (Selovale), já documentados.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação só cria condição (R3-3.4).
- Todo avanço exige **causa observável** (um gatilho da tabela), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho já existe na cena. Sem gatilho, nada muda.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Nenhum relógio decide as lacunas das leis (L-01, L-07, L-09 e demais) nem o que a Provação, a necromancia ou um pacto antigo concedem. Nenhum relógio envolve Aeltharyn, as Coroas ou as Marcas das Feras.
- Retorno de relógio (trava efetiva) tira **1 segmento** por vez, com custo real.

## 2. Custo por segmento (vale para os 8)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, vigia, material | a dependência da economia do lugar (doc 05 §2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o órgão da coluna "decisão no segmento 5" precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | muda estado, não resolve a trama |

## 3. Os oito relógios

### `clock.desafiantes` — CV-01 A lista parada e o Conselho dividido (2/6)
| Campo | Texto |
|---|---|
| O que move | nova inscrição que não chega ao registro; Pactuários tomando partido em público; recusa de registrar |
| Gatilhos observáveis de avanço | nova inscrição travada; nova tomada pública de partido observada |
| O que trava (ASSUMINDO) | toda inscrição lançada no Arquivo dos Reis que Passaram; sessão do Conselho que ouve os dois lados; Presidente que convoca |
| Quem paga (ASSUMINDO) | Cerrane (preside e não vota); o Arquivo; a Guarda do Rei, que serve ao rei e não à cidade |
| Decisão pública no segmento 5 (ASSUMINDO) | Conselho dos Pactuários (Morrovéu), presidido por Cerrane, que não vota |
| Ao completar (segmento 6) | o Conselho é obrigado a votar em público se chama o rei a responder (doc 04 §3); **o resultado não é decidido aqui** e a Provação segue só pela regra |
| Efeito da inação (ASSUMINDO) | só avança com nova inscrição travada ou nova tomada pública de partido observada |
| Entidades | `city.morroveu`, `npc.morvan-voz-baixa`, `npc.orsk-cicatriz-de-bando`, `npc.cerrane-heranca-firme`, `faction.guarda-do-rei`, `faction.defensores-da-paz`, `faction.memoria-das-provacoes` |

### `clock.partida` — CV-02 Partida e acusação em Lamaluz (3/6)
| Campo | Texto |
|---|---|
| O que move | nova família que pede guia; acusação formal contra quem guia; cheia ou seca fora de época |
| Gatilhos observáveis de avanço | nova acusação; nova família que parte observada |
| O que trava (ASSUMINDO) | acordo entre Pactuários dos Canais e Pilotos sobre o alcance de L-09; caso ouvido antes de acusado; canais navegáveis |
| Quem paga (ASSUMINDO) | Tazk e os pilotos; famílias de pacto antigo; os Pactuários dos Canais |
| Decisão pública no segmento 5 (ASSUMINDO) | Pactuários dos Canais (Lamaluz) |
| Ao completar (segmento 6) | os Pactuários dos Canais abrem **julgamento formal** de quem guia; a pena segue `desconhecida` (L-09) e o efeito fica **dentro de Lamaluz** |
| Efeito da inação (ASSUMINDO) | só avança com nova acusação ou nova família que parte observada |
| Entidades | `city.lamaluz`, `npc.tazk-pedra-ao-sol`, `faction.pilotos-dos-canais`, `faction.guardioes-das-lamparinas` |

### `clock.fardos` — CV-03 Fardos sem papel em Fozbreu (2/6)
| Campo | Texto |
|---|---|
| O que move | carga que chega sem papel que o pacto reconheça; visitantes de passagem sem registro; armazém sem espaço |
| Gatilhos observáveis de avanço | novo fardo sem papel; novo visitante sem registro observado |
| O que trava (ASSUMINDO) | regra provisória de papel na Casa da Barra; registro de cada fardo; conferência da Torre das Marés |
| Quem paga (ASSUMINDO) | o porto; os Pactuários da Foz; quem recebe o que desce o rio |
| Decisão pública no segmento 5 (ASSUMINDO) | Pactuários da Foz (Fozbreu) |
| Ao completar (segmento 6) | a Casa da Barra suspende a recepção de carga sem papel até haver regra provisória; **o que a Descida traz continua `desconhecido`** |
| Efeito da inação (ASSUMINDO) | só avança com novo fardo sem papel ou novo visitante sem registro observado |
| Entidades | `city.fozbreu`, `npc.rusk-rede-da-barra`, `faction.defensores-da-paz`, `faction.pilotos-dos-canais` |

### `clock.acertos-orais` — CV-04 Acertos orais em Beira-Sal (2/6)
| Campo | Texto |
|---|---|
| O que move | guia que cobra por pacto oral sem prova; caravana que contesta a conta; sinal de poço em pior estado, **visto na cena** |
| Gatilhos observáveis de avanço | nova cobrança contestada; novo sinal de poço observado |
| O que trava (ASSUMINDO) | acerto feito no Pátio dos Acertos com testemunha; vistoria dos poços; Pátio neutro |
| Quem paga (ASSUMINDO) | os Guias de Sal; as caravanas; a Casa dos Poços |
| Decisão pública no segmento 5 (ASSUMINDO) | Pactuários dos Poços (Beira-Sal) |
| Ao completar (segmento 6) | o Pátio dos Acertos passa a exigir testemunha de ambos os lados e **suspende acertos orais sem prova**; o valor de um pacto oral já feito segue `desconhecido` (L-05) |
| Efeito da inação (ASSUMINDO) | só avança com nova cobrança contestada ou novo sinal de poço observado |
| Entidades | `city.beira-sal`, `npc.vurga-sede-vencida`, `npc.teshi-no-de-poco`, `faction.guias-de-sal`, `faction.casa-dos-pocos` |

### `clock.pacto-que-vence` — CV-05 O pacto antigo que vence em Selovale (2/6)
| Campo | Texto |
|---|---|
| O que move | alguém invoca uma cláusula esquecida; nova leitura que muda o texto; Pactuários discordando em público |
| Gatilhos observáveis de avanço | nova invocação de cláusula; nova divergência observada entre versões |
| O que trava (ASSUMINDO) | cópia conferida no Arquivo dos Pactos Velhos; sessão do Conselho dos Selos; cláusula registrada (L-08) |
| Quem paga (ASSUMINDO) | o Conselho dos Selos; quem depende do pacto; Morrovéu, pela Memória das Provações |
| Decisão pública no segmento 5 (ASSUMINDO) | Conselho dos Selos (Selovale) |
| Ao completar (segmento 6) | o Conselho dos Selos é obrigado a abrir **sessão pública de parecer** sobre o texto; **o parecer não está decidido aqui** nem é executado por ele (doc 04 §3); o que o fim do pacto concede segue `desconhecido` |
| Efeito da inação (ASSUMINDO) | só avança com nova invocação de cláusula ou nova divergência observada entre versões |
| Entidades | `city.selovale`, `npc.yeshe-forma-emprestada`, `faction.memoria-das-provacoes` |

### `clock.orla-sem-guia` — CV-06 Chegadas sem guia em Borda-Mansa (1/6)
| Campo | Texto |
|---|---|
| O que move | mais chegadas sem guia e sem registro; alguém que passa com ajuda de dentro, **observado** |
| Gatilhos observáveis de avanço | nova chegada sem guia; nova passagem sem registro observada |
| O que trava (ASSUMINDO) | lista no Pátio dos Sem Guia; reforço da Torre das Fogueiras; abrigo sem perguntar mais que o pacto permite |
| Quem paga (ASSUMINDO) | os Vigias da Orla; a Casa do Pacto de Hospitalidade; quem chega |
| Decisão pública no segmento 5 (ASSUMINDO) | Pactuários da Vigia (Borda-Mansa) |
| Ao completar (segmento 6) | os Pactuários da Vigia debatem em audiência se **registrar quem chega fere o abrigo** (L-03); **o resultado não é decidido aqui** |
| Efeito da inação (ASSUMINDO) | só avança com nova chegada sem guia ou nova passagem sem registro observada |
| Entidades | `city.borda-mansa`, `npc.krell-eco-da-vigia`, `faction.vigias-da-orla` |

### `clock.hospedes` — CV-07 Hóspedes que não partem em Fio-Longo (1/6)
| Campo | Texto |
|---|---|
| O que move | parente que cobra a volta de um hóspede; hóspede que não quer ouvir o parente |
| Gatilhos observáveis de avanço | novo pedido formal de parente; nova recusa observada de hóspede |
| O que trava (ASSUMINDO) | a Casa ouve o hóspede com o parente presente; regra provisória sobre quem decide ficar ou partir |
| Quem paga (ASSUMINDO) | a Casa dos Fios; os parentes; o próprio hóspede |
| Decisão pública no segmento 5 (ASSUMINDO) | Casa dos Fios (Fio-Longo), guardiã do Pacto do Repouso |
| Ao completar (segmento 6) | a Casa dos Fios abre **julgamento formal** de um caso de hóspede (Lei do Repouso, L-06), **sem decidir se há quebra**; resultado fica a registrar |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido formal de parente ou nova recusa observada de hóspede |
| Entidades | `city.fio-longo`, `npc.ondra-fio-de-sono`, `faction.casa-dos-fios` |

### `clock.despedidas` — CV-08 A divisão das Despedidas em Candeia-Morta (2/6)
| Campo | Texto |
|---|---|
| O que move | Pactuários que divergem sobre o que o pacto pede de um rei necromante; pedido da Corte que o pacto pode proibir; nome do Muro que mudou de lugar (se observado) |
| Gatilhos observáveis de avanço | novo pedido da Corte observado; nova divergência pública |
| O que trava (ASSUMINDO) | reunião dos Pactuários das Despedidas com mediação de Dhun; Casa das Despedidas aberta; turnos que não fecham |
| Quem paga (ASSUMINDO) | a Irmandade das Despedidas; os Turnos da Vigília; a cidade, frágil de dia |
| Decisão pública no segmento 5 (ASSUMINDO) | Pactuários das Despedidas (Candeia-Morta) |
| Ao completar (segmento 6) | a divisão é dita em voz alta na Casa das Despedidas e os Pactuários-mor precisam responder o que **L-07 pede de um rei necromante**; **a resposta não é decidida aqui** e a lei segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido da Corte observado ou nova divergência pública |
| Entidades | `city.candeia-morta`, `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `faction.irmandade-das-despedidas`, `faction.turnos-da-vigilia` |

## 4. Conferências

| Verificação | Resultado |
|---|---|
| Relógios | 8 (progresso inicial 1/6 a 3/6, nenhum acima de 3) |
| Cada relógio com "o que move" e "o que trava" | 8 de 8 |
| Campo vazio | 0 |
| Entidade fora do registro mestre | 0 |
| Gatilhos observáveis por relógio | 2 em cada (reescrita do efeito da inação) |
| Relógio ligado a principal | 12 de 12 principais em ao menos um relógio (R3-4.1) |
| Fronteira (Aeltharyn, Coroas, Marcas das Feras) | 0 relógio |

## 5. Decisão pendente

**D-87:** aprovar gatilhos, decisão no segmento 5 e custos dos oito relógios. Opções: **A.** aprovar; **B.** trocar gatilho, quem decide no segmento 5 ou o progresso de algum relógio (diga qual). **Recomendação: A.**
