# Véu dos Malditos — modelo operacional, relógios e oportunidades (doc 06)

**Estado:** rascunho de R3-1.7 (D-74 aceita pela continuação) e R3-6.4 (§9, D-95) · **Data:** 2026-11-05
**Etiqueta:** tudo é **[PROPOSTO CV-01 a CV-08, CF-01 a CF-08, OV-01 a OV-08]**; só vira cânone com a aprovação (D-74). Cânone de apoio: doc 01 (partes 1 a 4), doc 03, doc 04, doc 05; molde: `reinos/02-.../06-...` e `reinos/01-.../12-relogios.md`.
**Regras seguidas:** relógio nunca avança por calendário; avanço exige causa observável, nexo e localidade; nenhum relógio explica a Neblina Longa, o Mistério E ou o Mistério D de Aeltharyn; nenhum liga o Véu a evento de Aeltharyn ou das Coroas; nenhum número de população, estoque, preço ou tempo (D-63). O doc 11 do plano é a ata de fechamento (R3-8.2); R3-1.7 não cria arquivo com esse número.
**ASSUMINDO:** "o que trava", "quem paga" e "efeito da inação" são leitura editorial derivada só do que as fichas já dizem; o **número de relógios** (oito, um por lugar) e o **progresso inicial** são escolha de partida e o autor pode trocá-los.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação só cria condição (calendário do Véu: R3-3.4).
- Todo avanço exige **causa observável** (algo que aconteceu na cena), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho observável já existe na cena.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Mistérios (doc 01 parte 4) não têm relógio; evidência se acumula na narração.
- Nenhum relógio decide as lacunas intencionais das leis (L-01, L-07, L-09 e demais) nem o que a Provação, a necromancia ou um pacto antigo concedem.

## 2. Custo por segmento (vale para os 8)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, vigia, material | a dependência da economia do lugar (doc 05 §2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o Pactuário-mor do lugar, o Conselho dos Pactuários ou o Conselho dos Selos precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | muda estado, não resolve a trama |

## 3. Os oito relógios

### `clock.desafiantes` — CV-01 A lista parada e o Conselho dividido (2/6)
| Campo | Texto |
|---|---|
| O que move | nova inscrição que não chega ao registro; Pactuários tomando partido em público; recusa de registrar |
| O que trava (ASSUMINDO) | toda inscrição lançada no Arquivo dos Reis que Passaram; sessão do Conselho que ouve os dois lados; Presidente que convoca |
| Quem paga (ASSUMINDO) | Cerrane (preside e não vota); o Arquivo; a Guarda do Rei, que serve ao rei e não à cidade |
| Ao completar | o Conselho é obrigado a votar em público se chama o rei a responder (doc 04 §3); **o resultado não é decidido aqui** e a Provação segue só pela regra |
| Efeito da inação (ASSUMINDO) | só avança com nova inscrição travada ou nova tomada pública de partido observada |
| Entidades | `city.morroveu`, `npc.morvan-voz-baixa`, `npc.orsk-cicatriz-de-bando`, `npc.cerrane-heranca-firme`, `faction.guarda-do-rei`, `faction.defensores-da-paz`, `faction.memoria-das-provacoes` |
| Fonte | doc 01 parte 1 §2.1 e parte 2; doc 04 §2 a §5 e §10 item 1; MV-01 |

### `clock.partida` — CV-02 Partida e acusação em Lamaluz (3/6)
| Campo | Texto |
|---|---|
| O que move | nova família que pede guia; acusação formal contra quem guia; cheia ou seca fora de época |
| O que trava (ASSUMINDO) | acordo entre Pactuários dos Canais e Pilotos sobre o alcance de L-09; caso ouvido antes de acusado; canais navegáveis |
| Quem paga (ASSUMINDO) | Tazk e os pilotos; famílias de pacto antigo; os Pactuários dos Canais |
| Ao completar | os Pactuários dos Canais abrem **julgamento formal** de quem guia; a pena segue `desconhecida` (L-09) e o efeito fica **dentro de Lamaluz** |
| Efeito da inação (ASSUMINDO) | só avança com nova acusação ou nova família que parte observada |
| Entidades | `city.lamaluz`, `npc.tazk-pedra-ao-sol`, `faction.pilotos-dos-canais`, `faction.guardioes-das-lamparinas` |
| Fonte | doc 01 parte 1 §2.2; doc 04 §6 e §10 item 2; doc 05 §1 (L-09) e §4; MV-03 |

### `clock.fardos` — CV-03 Fardos sem papel em Fozbreu (2/6)
| Campo | Texto |
|---|---|
| O que move | carga que chega sem papel que o pacto reconheça; visitantes de passagem sem registro; armazém sem espaço |
| O que trava (ASSUMINDO) | regra provisória de papel na Casa da Barra; registro de cada fardo; conferência da Torre das Marés |
| Quem paga (ASSUMINDO) | o porto; os Pactuários da Foz; quem recebe o que desce o rio |
| Ao completar | a Casa da Barra suspende a recepção de carga sem papel até haver regra provisória; **o que a Descida traz continua `desconhecido`** |
| Efeito da inação (ASSUMINDO) | só avança com novo fardo sem papel ou novo visitante sem registro observado |
| Entidades | `city.fozbreu`, `npc.rusk-rede-da-barra`, `faction.defensores-da-paz`, `faction.pilotos-dos-canais` |
| Fonte | doc 01 parte 1 §2.3; doc 04 §10 item 3; doc 05 §1 (L-04) e §3; MV-04 |

### `clock.acertos-orais` — CV-04 Acertos orais em Beira-Sal (2/6)
| Campo | Texto |
|---|---|
| O que move | guia que cobra por pacto oral sem prova; caravana que contesta a conta; sinal de poço em pior estado, **visto na cena** |
| O que trava (ASSUMINDO) | acerto feito no Pátio dos Acertos com testemunha; vistoria dos poços; Pátio neutro |
| Quem paga (ASSUMINDO) | os Guias de Sal; as caravanas; a Casa dos Poços |
| Ao completar | o Pátio dos Acertos passa a exigir testemunha de ambos os lados e **suspende acertos orais sem prova**; o valor de um pacto oral já feito segue `desconhecido` (L-05) |
| Efeito da inação (ASSUMINDO) | só avança com nova cobrança contestada ou novo sinal de poço observado |
| Entidades | `city.beira-sal`, `npc.vurga-sede-vencida`, `npc.teshi-no-de-poco`, `faction.guias-de-sal`, `faction.casa-dos-pocos` |
| Fonte | doc 01 parte 1 §2.4; doc 04 §10 item 4; doc 05 §1 (L-05); MV-05 |

### `clock.pacto-que-vence` — CV-05 O pacto antigo que vence em Selovale (2/6)
| Campo | Texto |
|---|---|
| O que move | alguém invoca uma cláusula esquecida; nova leitura que muda o texto; Pactuários discordando em público |
| O que trava (ASSUMINDO) | cópia conferida no Arquivo dos Pactos Velhos; sessão do Conselho dos Selos; cláusula registrada (L-08) |
| Quem paga (ASSUMINDO) | o Conselho dos Selos; quem depende do pacto; Morrovéu, pela Memória das Provações |
| Ao completar | o Conselho dos Selos é obrigado a abrir **sessão pública de parecer** sobre o texto; **o parecer não está decidido aqui** nem é executado por ele (doc 04 §3); o que o fim do pacto concede segue `desconhecido` |
| Efeito da inação (ASSUMINDO) | só avança com nova invocação de cláusula ou nova divergência observada entre versões |
| Entidades | `city.selovale`, `npc.yeshe-forma-emprestada`, `faction.memoria-das-provacoes` |
| Fonte | doc 01 parte 1 §2.5; doc 04 §3 e §10 item 5; doc 05 §1 (L-08); MV-06 |

### `clock.orla-sem-guia` — CV-06 Chegadas sem guia em Borda-Mansa (1/6)
| Campo | Texto |
|---|---|
| O que move | mais chegadas sem guia e sem registro; alguém que passa com ajuda de dentro, **observado** |
| O que trava (ASSUMINDO) | lista no Pátio dos Sem Guia; reforço da Torre das Fogueiras; abrigo sem perguntar mais que o pacto permite |
| Quem paga (ASSUMINDO) | os Vigias da Orla; a Casa do Pacto de Hospitalidade; quem chega |
| Ao completar | os Pactuários da Vigia debatem em audiência se **registrar quem chega fere o abrigo** (L-03); **o resultado não é decidido aqui** |
| Efeito da inação (ASSUMINDO) | só avança com nova chegada sem guia ou nova passagem sem registro observada |
| Entidades | `city.borda-mansa`, `npc.krell-eco-da-vigia`, `faction.vigias-da-orla` |
| Fonte | doc 01 parte 1 §2.6; doc 05 §1 (L-03); MV-07 |

### `clock.hospedes` — CV-07 Hóspedes que não partem em Fio-Longo (1/6)
| Campo | Texto |
|---|---|
| O que move | parente que cobra a volta de um hóspede; hóspede que não quer ouvir o parente |
| O que trava (ASSUMINDO) | a Casa ouve o hóspede com o parente presente; regra provisória sobre quem decide ficar ou partir |
| Quem paga (ASSUMINDO) | a Casa dos Fios; os parentes; o próprio hóspede |
| Ao completar | a Casa dos Fios abre **julgamento formal** de um caso de hóspede (Lei do Repouso, L-06), **sem decidir se há quebra**; resultado fica a registrar |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido formal de parente ou nova recusa observada de hóspede |
| Entidades | `city.fio-longo`, `npc.ondra-fio-de-sono`, `faction.casa-dos-fios` |
| Fonte | doc 01 parte 1 §2.7; doc 04 §6; doc 05 §1 (L-06); MV-08 |

### `clock.despedidas` — CV-08 A divisão das Despedidas em Candeia-Morta (2/6)
| Campo | Texto |
|---|---|
| O que move | Pactuários que divergem sobre o que o pacto pede de um rei necromante; pedido da Corte que o pacto pode proibir; nome do Muro que mudou de lugar (se observado) |
| O que trava (ASSUMINDO) | reunião dos Pactuários das Despedidas com mediação de Dhun; Casa das Despedidas aberta; turnos que não fecham |
| Quem paga (ASSUMINDO) | a Irmandade das Despedidas; os Turnos da Vigília; a cidade, frágil de dia |
| Ao completar | a divisão é dita em voz alta na Casa das Despedidas e os Pactuários-mor precisam responder o que **L-07 pede de um rei necromante**; **a resposta não é decidida aqui** e a lei segue aberta |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido da Corte observado ou nova divergência pública |
| Entidades | `city.candeia-morta`, `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `faction.irmandade-das-despedidas`, `faction.turnos-da-vigilia` |
| Fonte | doc 01 parte 1 §2.8 e parte 2; doc 04 §9; doc 05 §1 (L-07); MV-02, MV-09 |

**Conferência:** 8 relógios, progresso inicial de 1/6 a 3/6 (nenhum acima de 3), 0 relógio a menos de 1 segmento do fim. Nenhum relógio envolve Aeltharyn, as Coroas ou as Marcas das Feras; nenhum tem NPC, lugar ou facção fora dos docs 01 e 04.

## 4. Conflitos com causa concreta

Cada problema atual da parte 1 do doc 01, com causa **observável**, quem perde e saída possível com custo.

| ID | Lugar | Causa concreta (observável) | Quem perde | Saída possível e custo | Relógio |
|---|---|---|---|---|---|
| CF-01 | Morrovéu | a lista de desafiantes não anda e o Conselho se divide | Conselho; Guarda do Rei | registro aberto de toda inscrição; custo político | CV-01 |
| CF-02 | Lamaluz | famílias querem partir e o pacto manda ficar; quem guia é acusado | pilotos; famílias | acordo sobre o alcance de L-09; custo de pacto | CV-02 |
| CF-03 | Fozbreu | carga chega sem papel que o pacto reconheça | o porto; quem recebe a carga | regra provisória de papel; custo de atraso | CV-03 |
| CF-04 | Beira-Sal | guias cobram por pactos orais sem prova; caravanas contestam | guias; caravanas | acerto com testemunha; custo de atrito | CV-04 |
| CF-05 | Selovale | um pacto antigo vence e ninguém sabe o que concede | Conselho dos Selos; Morrovéu | parecer público; custo de autoridade | CV-05 |
| CF-06 | Borda-Mansa | gente de longe chega sem guia e sem registro | Vigias da Orla; quem chega | lista no Pátio dos Sem Guia; custo do abrigo | CV-06 |
| CF-07 | Fio-Longo | hóspedes não querem partir e parentes cobram | a Casa; os parentes | audiência com os dois lados; custo de confiança | CV-07 |
| CF-08 | Candeia-Morta | Pactuários divergem sobre o que o pacto pede de um rei necromante | a Irmandade; os Turnos | reunião mediada; custo de silêncio quebrado | CV-08 |

## 5. Oportunidades de aventura (uma por lugar)

Oportunidade = ponto de entrada, não trama. Nenhuma exige herói predestinado; todas podem ser ignoradas, resolvidas por NPC ou piorar por falta de ação.

| ID | Lugar | Gancho | Quem pede | Em jogo | Ligação |
|---|---|---|---|---|---|
| OV-01 | Morrovéu | conferir no Arquivo se há inscrição que nunca chegou à lista | Cerrane Herança-Firme | confiança do Conselho; olhar da Guarda | CV-01, MV-01 |
| OV-02 | Lamaluz | guiar uma família sem cair em acusação, ou testemunhar o caso | Tazk Pedra-ao-Sol | pacto; reputação com os pilotos | CV-02, MV-03 |
| OV-03 | Fozbreu | conferir um fardo sem dono contra os registros do armazém | Rusk Rede-da-Barra | regra da barra; confiança do porto | CV-03, MV-04 |
| OV-04 | Beira-Sal | escoltar uma caravana e testemunhar um acerto oral | Vurga Sede-Vencida | acerto honesto; água do posto | CV-04, MV-05 |
| OV-05 | Selovale | achar a cópia mais antiga de um texto contestado | Yeshe Forma-Emprestada | versão do pacto; autoridade do Conselho | CV-05, MV-06 |
| OV-06 | Borda-Mansa | vigiar uma noite de chegadas e registrar quem vem | Krell Eco-da-Vigia | abrigo; segurança do posto | CV-06, MV-07 |
| OV-07 | Fio-Longo | ouvir um hóspede e seus parentes sem tomar partido | Ondra Fio-de-Sono | confiança da Casa; vontade do hóspede | CV-07, MV-08 |
| OV-08 | Candeia-Morta | acompanhar uma vigília e conferir uma anotação do Muro dos Nomes | Dhun Contorno-Quieto | pacto dos mortos; silêncio da cidade | CV-08, MV-02, MV-09 |

## 6. Como um mistério ou relógio muda

- Mistério: **evidência cumulativa** (pelo menos duas provas independentes) antes de a narração fixar uma hipótese; a hipótese fixada obedece aos vetos do doc 01 parte 4.
- Relógio: **1 segmento por causa observável**, com a escala da §2; ao completar, vira mudança de estado, não solução.
- Retorno de relógio (trava efetiva) tira **1 segmento** por vez, com custo real (§2).
- Nenhum desses movimentos reescreve uma ficha, uma lei ou um pacto aprovado.

## 7. Conferências

- 8 relógios; 8 conflitos; 8 oportunidades (1 por lugar); 9 mistérios (doc 01 parte 4).
- 0 NPC, lugar, facção ou rota novo; 0 número, tempo ou distância; 0 mestiço; 0 ID de relógio igual a ID dos reinos 1 e 2.
- Termos de fronteira (Neblina Longa, Mistérios D e E, Brumafunda, Bosque dos Sussurros, Vigília Longa, Sela dos Retornos) **só em vetos** (doc 01 parte 4); nenhum relógio os toca.
- Contrato anti-invenção (§9): fixo, livre, registro e proibido definidos; só vetos citam Neblina Longa, Mistérios D e E e Brumafunda.

## 8. Decisão pendente

**D-74:** aprovar os nove mistérios (doc 01 parte 4), os oito relógios e o progresso inicial, os oito conflitos, as oito oportunidades e os limites da necromancia do Lord (doc 01 parte 4 §3). Opções: **A.** aprovar tudo; **B.** trocar mistério, relógio, progresso inicial ou o número de relógios (diga qual). **Recomendação: A.**

## 9. Contrato anti-invenção (R3-6.4)

**Estado:** rascunho de R3-6.4 para aprovação do autor (D-95) · **Etiqueta:** **[PROPOSTO]**. Vale para qualquer proposta da IA sobre o Véu dos Malditos. Herda a lista de verificação e o contrato de Aeltharyn (`reinos/01-.../06-...md` §11 e §11.1) e o das Coroas (`reinos/02-.../06-...md` §9), sem alterá-los, e se apoia em `14-camadas-de-informacao.md`, `13-misterios-limites.md`, `12-relogios.md`, doc 07 e `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_VEU.md`. A separação em fixo, livre, registro e proibido é leitura editorial (ASSUMINDO).

### 9.1 Verificação antes de aceitar uma proposta

- Os lugares e entidades existem (registro mestre) ou foram criados como conteúdo novo permitido (§9.4)?
- A cronologia é compatível (doc 01 parte 3 e parte 5: Contagem das Provações; sem ano de Aeltharyn como fato do Véu)?
- O agente poderia saber disso (`12-conhecimento.csv`, camada do fato)?
- O agente tem meios e autoridade (competência e limite em `12-competencias.csv`; o Conselho não depõe o rei, a Provação é a única remoção)?
- A rota e o tempo são conhecidos? Toda duração do reino é `desconhecida`.
- A proposta transforma hipótese em fato sem evidência? (mistério: ≥ 2 provas independentes, §6)
- A consequência contradiz estado persistente, lei (L-01 a L-09), pacto ou decisão anterior?
- O segredo é revelado sem caminho de descoberta cumprido?
- A proposta trata um povo como mau, perigoso ou leal por natureza, ou liga "Maldito" a sentença? (contrato de fronteira §3 item 5)

Se não puder ser confirmado, a IA reduz a precisão, mantém a incerteza ou pede aprofundamento. Não preenche lacuna com número exato nem fato definitivo para a cena fluir.

### 9.2 Quatro classes

| Classe | O que é | O que a IA faz |
|---|---|---|
| **Fixo** | Tudo o que já tem ID ou fonte: os 8 lugares (V-01 a V-08), as 11 facções, as 30 pessoas do registro (12 principais, 7 da tríade, 11 heróis lendários **mortos**), os 8 relógios, os 9 mistérios (MV-01 a MV-09) e o "a verdade não pode ser", as 9 leis (L-01 a L-09), os 12 eventos-tipo, as 24 sementes, os 11 nativos do catálogo, o Lord Morvan Voz-Baixa como único rei, o Conselho dos Pactuários (oito assentos, presidente sem voto), os 8 pactos, a Provação das Armas, as ligações do doc 07 **sem duração**, os recursos, o calendário, os 41 fatos e a camada de cada um, e as lacunas (`desconhecido`) | Usa; não altera nem contradiz. Lacuna continua lacuna |
| **Livre** | Detalhe de cena sem consequência persistente: neblina, cheiro, objeto comum, figurante anônimo sem ID, fala dentro do tom (`12-voz.csv`) | Inventa sem registro, se não contradisser um fixo, não prometer recompensa e não criar dever, dívida ou mudança de estado |
| **Exige registro** | O que muda o mundo ou cria entidade: morte ou ferimento de pessoa com ID, vitória na Provação ou troca de rei, pacto ou assento suspenso, relógio que avança, NPC, organização ou lugar recorrente novo, consequência de decisão do jogador, fato restrito ou secreto revelado | Pode **propor**; só vale depois de registrado com estado anterior, novo estado, momento, causa, agentes, quem sabe e grau de certeza (doc 02 §2) |
| **Proibido** | §9.3 | Não faz; reduz a precisão, mantém a incerteza ou pede aprofundamento |

### 9.3 Proibido (nenhuma proposta, nenhum registro da IA)

- criar um segundo rei, nomear herdeiro, dizer que o Lord caiu ou que a Provação foi vencida, abolida ou suspensa; escolher quem vigia a Provação, se a necromancia é permitida nela ou o que a guarda faz se o rei cai (doc 04 §5 item 5: `desconhecido`); depor o rei pelo Conselho;
- nomear o titular do assento de Morrovéu (doc 04 §2.1: `desconhecido`); criar membro, cargo ou delegado fora das regras do doc 04 §2.1;
- criar um 9º lugar, um 12º povo nativo ou ressuscitar um dos 11 heróis lendários;
- declarar guerra, tratar secessão (romper o pacto de um lugar) como resultado conhecido (doc 04 §5: `desconhecido`) ou contradizer a Lei do Pacto Vivo (L-02);
- resolver um mistério ou escolher uma das hipóteses abertas; dar ao custo da estabilização do Lord um nome ou um método (MV-01 a MV-09 e doc 01 parte 4 §3);
- fazer de um povo, de uma raça ou de "Maldito" um traço de maldade, perigo ou lealdade; alocar mestiço a lugar, facção ou reino (D-64);
- inventar número de população, estoque, preço, distância ou duração que o texto deixa desconhecido;
- conceder efeito mecânico por nome, título ou lore;
- revelar fato restrito ou secreto sem o caminho de descoberta cumprido, ou mudar a camada de um fato;
- avançar relógio sem gatilho observável (`12-relogios.csv`) ou por calendário; usar o Dia de Pacto como vencimento do pacto antigo de Selovale;
- explicar a Neblina Longa, o Mistério E ou o Mistério D; criar rota, tempo, passagem ou distância entre o Véu e Aeltharyn ou as Coroas; fazer qualquer pessoa ou lugar de Aeltharyn pertencer ao Véu ou agir a mando dele; dar ano de Aeltharyn a evento do Véu; criar fluxo de refugiados vindo de Aeltharyn ou das Coroas; descrever como Aeltharyn trata quem chega pelas Fugas do Véu (contrato §3);
- afirmar algo sobre as Marcas das Feras ou o Pacto Humano além de M-04 e M-05;
- escrever conteúdo dos reinos 4 e 5.

### 9.4 Casos pedidos

| Pedido | Regra |
|---|---|
| **Novo rei ou governante** | Proibido criar. O Véu tem um só rei, Morvan Voz-Baixa. Troca de rei só pela Provação (L-01), cujo desfecho, desafiante e método são `desconhecidos`; a IA não a conduz. Outra pessoa na coroa exige decisão do autor |
| **Nova cidade ou lugar** | Proibido. Casa, poço, posto ou taverna sem ID é detalhe livre enquanto não tiver função recorrente. Para virar entidade, entra como **proposta** ao autor com cidade-mãe (D-66 só prevê oito lugares; a regra vale para qualquer proposta), liderança, obrigação, povo do catálogo e fonte "proposto"; só vira ID depois de aprovada e registrada no registro mestre |
| **Guerra** | A IA não declara. Escalada local (disputa de turno, cobrança contestada, patrulha reforçada) pode ser proposta como oportunidade condicionada a gatilho observável, com custos; romper um pacto não é guerra automática |
| **NPC ou organização nova** | Figurante sem ID: livre. Com papel recorrente: proposta com `npc.` ou `faction.` novo, lugar, facção-mãe (ou "instituição"), povo do catálogo e fonte "proposto"; nunca com segredo, relação ou conhecimento que mude um mistério |
| **Morte ou ferimento de pessoa com ID** | Exige registro com causa e agentes; a IA não mata para dar peso à cena |
| **Rota ou tempo de viagem** | Proibido inventar: toda duração é `desconhecida` (doc 07). Pode dizer que o tempo é desconhecido |
| **Rumor sobre Aeltharyn (orla norte)** | Só em tom de rumor, como na cena 6 (`15-cenas-de-teste.md`); sem rota, sem tempo, sem explicação da Neblina Longa |

### 9.5 Teste rápido (4 perguntas, em ordem)

1. Já existe ID ou fonte? **Fixo**: usar como está.
2. Está na lista proibida? **Não fazer.**
3. Muda estado, cria entidade ou revela restrito ou secreto? **Propor e registrar** antes de tratar como fato.
4. Nenhuma das anteriores e sem consequência? **Livre.**

Em dúvida entre duas classes, vale a mais restritiva.

### 9.6 Decisão pendente

**D-95:** aprovar este contrato. Opções: **A.** aprovar (recomendada); **B.** mover item entre classes ou trocar caso (diga qual). **Aguardando o autor.**

### 9.7 Testes de fim de F6 (locais)

18 CSV íntegros (463 linhas); registro mestre 49 entidades; 57 resumos, 41 fatos, 6 cenas, 8 relógios, 9 mistérios, 12 eventos e 24 sementes conferem com os números da §9.2; L-01 a L-09 existem; 0 referência de arquivo ausente na seção; reinos 1 e 2, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos ao ZIP de entrada; validador no reino 2: 0 erro, 40 avisos, 7 exceções, relatório idêntico ao existente. 0 falha real, 0 regressão.
