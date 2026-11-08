# Vale Palancar — modelo operacional, relógios e oportunidades (doc 06)

**Estado:** R4-1.7, D-111 aceita pela continuação (07/11/2026); §9 (R4-6.4), D-132 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** tudo é **[PROPOSTO CP-01 a CP-08, CFP-01 a CFP-08, OP-01 a OP-08]**; só vira cânone com a aprovação (D-111). Cânone de apoio: doc 01 (partes 1 a 4), doc 03, doc 04, doc 05, doc 10; molde: `reinos/03-.../06-...` e `reinos/01-.../12-relogios.md`.
**Regras seguidas:** relógio nunca avança por calendário; avanço exige causa observável, nexo e localidade; nenhum relógio explica a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G de Aeltharyn, nem dá causa a mistério do Véu ou à Descida; nenhum liga o Vale a evento de Aeltharyn, das Coroas ou do Véu; nenhum número de população, estoque, preço ou tempo (D-63). O doc 11 do plano é a ata de fechamento (R4-8.2); R4-1.7 não cria arquivo com esse número.
**ASSUMINDO:** "o que trava", "quem paga" e "efeito da inação" são leitura editorial derivada só do que as fichas e os docs 01 e 04 já dizem; o **número de relógios** (oito, um por lugar) e o **progresso inicial** são escolha de partida e o autor pode trocá-los. "Segmentos" são a escala de 6 do molde, não medida de tempo.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação e a migração só criam condição (calendário do Vale: R4-3.4).
- Todo avanço exige **causa observável** (algo que aconteceu na cena), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho observável já existe na cena.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Mistérios (doc 01 parte 4) não têm relógio; evidência se acumula na narração.
- Nenhum relógio decide as lacunas intencionais das leis (L-01 a L-10), a regra de escolha do Capitão (prova ou aclamação), o direito à reserva, a quem pertencem as pedras do vau, quem cobra o desembarque, quem tem preferência na cisterna, nem quem tem razão sobre o capim.
- Nenhum relógio envolve Aeltharyn, as Coroas, o Véu ou o Pacto Humano. Onde uma situação toca a Descida, a Rota do Sal ou as trilhas, o relógio fica **do lado do Vale** (contrato §5 e §6).

## 2. Custo por segmento (vale para os 8)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, vigia, material | a dependência da economia do lugar (doc 05 §2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o Trato do lugar, a Assembleia ou o Capitão (conforme o relógio) precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | muda estado, não resolve a trama |

## 3. Os oito relógios

### `clock.reserva-em-ano-ruim` — CP-01 A reserva sem regra (2/6)
| Campo | Texto |
|---|---|
| O que move | clã de fora pedindo abrigo e reserva no celeiro; guardiões recusando a entrega; pasto fraco observado |
| O que trava (ASSUMINDO) | entrega em ordem pelo Trato do Celeiro; doação de Pocha a quem o celeiro não atende; acordo na Assembleia sobre a ordem de entrega |
| Quem paga (ASSUMINDO) | os clãs de fora; a Mesa da Partilha; os guardiões, acusados de egoísmo |
| Ao completar | a Assembleia é chamada a dizer o que o celeiro deve a quem chega em ano ruim; **o direito à reserva não é decidido aqui** (L-07 segue aberta) |
| Efeito da inação (ASSUMINDO) | só avança com novo pedido de reserva ou nova recusa observados |
| Entidades | `city.tabuleiro-velho`, `npc.moura-mel-guardado`, `npc.pocha-ninhada-farta`, `faction.guardioes-da-reserva`, `faction.mesa-da-partilha` |
| Fonte | doc 01 parte 1 §2.1 e parte 2 (N-04, N-05); doc 04 §9 e §10; doc 05 §1 (L-07); MPL-01, MPL-02 |

### `clock.dupla-cobranca` — CP-02 Dois grupos, um vau (3/6)
| Campo | Texto |
|---|---|
| O que move | quem atravessa pagando duas vezes ou nenhuma; cobrança de um grupo contestada pelo outro em público |
| O que trava (ASSUMINDO) | recados de Quira entre os dois grupos; acerto na Tenda dos Recados; apelo do Trato do Vau à Assembleia |
| Quem paga (ASSUMINDO) | quem atravessa; os rebanhos parados na margem; os dois grupos, que perdem travessia |
| Ao completar | um dos grupos ou a Assembleia precisa dizer em voz alta a quem se paga; **a quem pertencem as pedras não é decidido aqui** (L-04 segue aberta) |
| Efeito da inação (ASSUMINDO) | só avança com nova travessia contestada observada |
| Entidades | `city.rasovau`, `npc.zinu-troca-justa`, `npc.quira-recado-certo`, `faction.pedras-de-cima`, `faction.pedras-de-baixo` |
| Fonte | doc 01 parte 1 §2.2 e parte 2 (N-08, N-09); doc 04 §7 e §9; doc 05 §1 (L-04); MPL-03 |

### `clock.desembarque` — CP-03 O desembarque cobrado de dois jeitos (2/6)
| Campo | Texto |
|---|---|
| O que move | carga que chega com papel que o Vale não conhece; Receptores e Balança cobrando critérios diferentes na mesma rampa |
| O que trava (ASSUMINDO) | guarda de carga no Barracão até o acerto; Vrissk aceitando carga sem conferir de onde vem; pacto de passagem do desembarque |
| Quem paga (ASSUMINDO) | quem desce e espera; os clãs vizinhos que protegem o pouso; o Trato do Desembarque |
| Ao completar | o Trato do Desembarque precisa dizer quem cobra e o que se reconhece como papel; **o que vale para o Vale não é decidido aqui** (L-05 segue aberta) e a Descida não ganha causa, número nem fim rio acima |
| Efeito da inação (ASSUMINDO) | só avança com nova carga contestada observada |
| Entidades | `city.praia-pardo`, `npc.vrissk-colonia-quieta`, `faction.receptores-do-pouso`, `faction.balanca-do-pouso` |
| Fonte | doc 01 parte 1 §2.3 e parte 2 (N-10); doc 04 §7 e §9; doc 05 §1 (L-05) e §3; MPL-04 |

### `clock.cisterna` — CP-04 A cisterna única (2/6)
| Campo | Texto |
|---|---|
| O que move | duas caravanas na cisterna ao mesmo tempo; recusa de água observada; queixa de caravana ou de clã sobre preferência |
| O que trava (ASSUMINDO) | turnos de vigia de Gruzk; atendimento em ordem pelo Trato do Poço; pacto de preferência na cisterna (nunca posto, doc 04 §10, item 4) |
| Quem paga (ASSUMINDO) | as caravanas que esperam; os clãs do pouso; os Vigias da Cisterna |
| Ao completar | o Trato do Poço precisa dizer quem tem preferência; **a regra de preferência não é decidida aqui** (L-06 segue aberta) e nada se afirma sobre o Ermo de Sal ou sobre Beira-Sal |
| Efeito da inação (ASSUMINDO) | só avança com nova espera ou recusa observada |
| Entidades | `city.alvasal`, `npc.gruzk-estandarte-baixo`, `faction.vigias-da-cisterna` |
| Fonte | doc 01 parte 1 §2.4 e parte 2 (N-03); doc 04 §9 e §10 (item 4); doc 05 §1 (L-06) e §4; MPL-05 |

### `clock.sucessao-do-capitao` — CP-05 O mandato que acaba (3/6)
| Campo | Texto |
|---|---|
| O que move | nova divergência pública entre Clãs da Prova e Clãs da Aclamação; Kharos adiando o anúncio; clã recusando a rodada |
| O que trava (ASSUMINDO) | ordem da reunião por Yorna; hospedagem neutra de Cassan na Ramada; rodada em que todos são ouvidos (L-01) |
| Quem paga (ASSUMINDO) | a Assembleia, que perde unidade; a Marcha, que fica sem rumo se o mandato acabar sem sucessor |
| Ao completar | a Assembleia precisa declarar, em voz alta no Fogo do Meio, se vale prova ou aclamação; **a escolha não é decidida aqui** e o interregno (doc 04 §5) fica como saída possível, não como efeito automático |
| Efeito da inação (ASSUMINDO) | só avança com nova divergência pública ou novo adiamento observados |
| Entidades | `city.terreiro-maior`, `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`, `npc.kharos-casco-de-marcha`, `faction.clas-da-prova`, `faction.clas-da-aclamacao` |
| Fonte | doc 01 parte 1 §2.5 e parte 2 (N-01, N-02, N-12); doc 04 §5 e §10 (item 1); doc 05 §1 (L-01, L-03); MPL-06, MPL-08 |

### `clock.campo-da-marcha` — CP-06 Dois campos, um rumo (2/6)
| Campo | Texto |
|---|---|
| O que move | batedores trazendo notícias diferentes dos dois campos; clã sem pasto na estação reclamando ao Capitão |
| O que trava (ASSUMINDO) | conselho de Rukhan; consulta aos Tratos dos campos; reserva do Tabuleiro-Velho como retaguarda (lugar-mãe) |
| Quem paga (ASSUMINDO) | o clã que fica sem pasto; os Guardas da Marcha; os clãs que adiam a partida |
| Ao completar | o Capitão precisa escolher o campo diante dos clãs; **qualquer escolha custa a algum clã** e a ordem que "parte um clã" segue como lacuna (L-03) |
| Efeito da inação (ASSUMINDO) | só avança com nova queixa de clã ou novo relato de batedor observados |
| Entidades | `city.vazante`, `npc.kharos-casco-de-marcha`, `npc.rukhan-retorno-certo`, `faction.guardas-da-marcha` |
| Fonte | doc 01 parte 1 §2.6 e parte 2 (N-01, N-07); doc 04 §3 e §9; doc 05 §1 (L-03); MPL-07 |

### `clock.guia-das-trilhas` — CP-07 Quem guia o rebanho (2/6)
| Campo | Texto |
|---|---|
| O que move | rebanho esperando guia no pouso; disputa pública sobre o que sobra no pouso; Vigias das Trilhas recusando a espera |
| O que trava (ASSUMINDO) | Tessun guiando sem tomar partido; pastagem de espera no Cerca de Espera; acordo do Trato da Encosta |
| Quem paga (ASSUMINDO) | os rebanhos parados; os Guias da Encosta; quem espera a passagem |
| Ao completar | o Trato da Encosta precisa dizer quem guia e quem fica com o que sobra; **não decide o que há do outro lado** (L-09) e não toca o Conflito G |
| Efeito da inação (ASSUMINDO) | só avança com novo rebanho em espera ou nova recusa observados |
| Entidades | `city.encosta-aberta`, `npc.tessun-salto-calado`, `faction.guias-da-encosta`, `faction.vigias-das-trilhas` |
| Fonte | doc 01 parte 1 §2.7 e parte 2 (N-06); doc 04 §9 e §10 (item 5); doc 05 §1 (L-09) e §5; MPL-09 |

### `clock.capim-disputado` — CP-08 Gado e caça no mesmo campo (2/6)
| Campo | Texto |
|---|---|
| O que move | marcas e mourões contestados; acusação pública de estrago do pasto; estação de pasto fraco observada |
| O que trava (ASSUMINDO) | divisão do campo por Tibbe; batida combinada fora da parição; acerto entre Clãs do Gado e da Caça na Tenda de Marcas |
| Quem paga (ASSUMINDO) | os dois lados, que perdem parte do campo; Ghorla, que sustenta a caça sem acordo |
| Ao completar | o Trato dos Rebanhos precisa dividir o campo diante dos dois lados; **quem tem razão sobre o capim não é decidido aqui** (L-08) e nenhuma raça é culpada |
| Efeito da inação (ASSUMINDO) | só avança com nova contestação de marca ou acusação observadas |
| Entidades | `city.gramal`, `npc.tibbe-salto-curto`, `npc.ghorla-fartura-dividida`, `faction.clas-do-gado`, `faction.clas-da-caca` |
| Fonte | doc 01 parte 1 §2.8 e parte 2 (N-11); doc 04 §9 e §10 (item 6); doc 05 §1 (L-08); MPL-10 |

**Conferência:** 8 relógios, progresso inicial de 2/6 a 3/6 (nenhum acima de 3), 0 relógio a menos de 1 segmento do fim. Nenhum relógio envolve Aeltharyn, as Coroas, o Véu ou o Pacto Humano; nenhum tem NPC, lugar ou facção fora dos docs 01 e 04.

## 4. Conflitos com causa concreta

Cada problema atual da parte 1 do doc 01, com causa **observável**, quem perde e saída possível com custo. As seis causas pedidas pelo plano estão cobertas: pasto (CFP-06), água (CFP-01, CFP-04), passagem (CFP-02, CFP-07), sucessão de chefia (CFP-05), caça (CFP-08) e tributo de caravana (CFP-03).

| ID | Lugar | Causa concreta (observável) | Quem perde | Saída possível e custo | Relógio |
|---|---|---|---|---|---|
| CFP-01 | Tabuleiro-Velho | em ano de pasto fraco, clãs de fora pedem abrigo e reserva e os guardiões dizem que a reserva é de quem a juntou (água e reserva) | clãs de fora; guardiões, acusados | ordem de entrega acordada na Assembleia; custo de reserva | CP-01 |
| CFP-02 | Rasovau | dois grupos cobram pela mesma passagem do vau (passagem) | quem atravessa; os rebanhos | recado e acerto entre os grupos; custo de preferência | CP-02 |
| CFP-03 | Praia-Pardo | a carga chega com papel que as Coroas reconhecem e o Vale não, e dois critérios de cobrança disputam a rampa (tributo de caravana) | quem desce; o Trato | regra provisória de papel e de cobrança; custo de atraso | CP-03 |
| CFP-04 | Alvasal | a cisterna não serve a todas as caravanas ao mesmo tempo (água) | caravanas; clãs do pouso | atendimento em ordem pactuado; custo de espera | CP-04 |
| CFP-05 | Terreiro-Maior | o mandato do Capitão está perto do fim e a Assembleia se divide entre prova e aclamação (sucessão de chefia) | a Assembleia; a Marcha | rodada em que todos são ouvidos; custo de unidade | CP-05 |
| CFP-06 | Vazante | a marcha precisa escolher entre dois campos e cada escolha deixa um clã sem pasto (pasto) | o clã sem pasto; os Guardas | escolha diante dos clãs com reserva de retaguarda; custo de um clã | CP-06 |
| CFP-07 | Encosta-Aberta | dois clãs disputam quem guia o rebanho e quem fica com o que sobra no pouso (passagem) | rebanhos; os dois grupos | guia combinado e pastagem de espera partilhada; custo de preferência | CP-07 |
| CFP-08 | Gramal | clãs de gado e de caça dizem que o mesmo campo é deles (caça e pasto) | os dois lados | divisão do campo por estação; custo de parte do campo | CP-08 |

## 5. Oportunidades de aventura (uma por lugar)

Oportunidade = ponto de entrada, não trama. Nenhuma exige herói predestinado; todas podem ser ignoradas, resolvidas por NPC ou piorar por falta de ação.

| ID | Lugar | Gancho | Quem pede | Em jogo | Ligação |
|---|---|---|---|---|---|
| OP-01 | Tabuleiro-Velho | conferir no Campo dos Marcos um marco que nenhum clã reconhece | Moura Mel-Guardado | confiança dos guardiões; olhar da Mesa da Partilha | CP-01, MPL-01, MPL-02 |
| OP-02 | Rasovau | levar um recado entre os dois grupos sem ser tomado por espião | Quira Recado-Certo | confiança de um dos grupos; a travessia | CP-02, MPL-03 |
| OP-03 | Praia-Pardo | acompanhar o acerto de uma carga com papel desconhecido sem perguntar de onde vem | Vrissk Colônia-Quieta | regra da rampa; confiança do pouso | CP-03, MPL-04 |
| OP-04 | Alvasal | escoltar duas caravanas até a cisterna sem que briguem por água | Gruzk Estandarte-Baixo | água do pouso; ordem dos turnos | CP-04, MPL-05 |
| OP-05 | Terreiro-Maior | ouvir um clã que não quer entrar na rodada e levar a palavra dele ao Fogo do Meio | Yorna Palavra-Firme | unidade da Assembleia; voz do clã | CP-05, MPL-06, MPL-08 |
| OP-06 | Vazante | bater um campo com Rukhan e voltar com o que viu, sem decidir por ele | Rukhan Retorno-Certo | rumo da marcha; pasto de um clã | CP-06, MPL-07 |
| OP-07 | Encosta-Aberta | guiar um rebanho até o pé das elevações sem tomar o lado de nenhum dos clãs | Tessun Salto-Calado | guia do rebanho; confiança das trilhas | CP-07, MPL-09 |
| OP-08 | Gramal | testemunhar a marca de um rebanho e o mourão contestado, ouvindo os dois lados | Tibbe Salto-Curto | divisão do campo; pasto da estação | CP-08, MPL-10 |

## 6. Como um mistério ou relógio muda

- Mistério: **evidência cumulativa** (pelo menos duas provas independentes) antes de a narração fixar uma hipótese; a hipótese fixada obedece aos vetos do doc 01 parte 4.
- Relógio: **1 segmento por causa observável**, com a escala da §2; ao completar, vira mudança de estado, não solução.
- Retorno de relógio (trava efetiva) tira **1 segmento** por vez, com custo real (§2).
- Nenhum desses movimentos reescreve uma ficha, uma lei, um pacto ou um Trato aprovado.

## 7. Conferências

- 8 relógios; 8 conflitos; 8 oportunidades (1 por lugar); 10 mistérios (doc 01 parte 4).
- 0 NPC, lugar, facção ou rota novo; 0 número, tempo ou distância; 0 mestiço; 0 ID de relógio igual a ID dos reinos 1, 2 e 3.
- Termos de fronteira (Neblina Longa, Mistérios D e E, Conflito G, mistérios e fardos do Véu, Brasalume) **só em vetos** (doc 01 parte 4); nenhum relógio os toca.
- Todo relógio, conflito e oportunidade cita só entidades dos docs 01, 03, 04 e 10.

## 8. Decisão pendente

**D-111:** aprovar os dez mistérios (doc 01 parte 4), os oito relógios e o progresso inicial, os oito conflitos e as oito oportunidades. Opções: **A.** aprovar tudo; **B.** trocar mistério, relógio, progresso inicial ou o número de relógios (diga qual). **Recomendação: A.**

## 9. Contrato anti-invenção (R4-6.4)

**Estado:** R4-6.4, D-132 aceita pela continuação · **Etiqueta:** **[PROPOSTO]**. Vale para qualquer proposta da IA sobre o Vale Palancar. Herda a lista de verificação e o contrato de Aeltharyn (`reinos/01-.../06-...md` §11 e §11.1), o das Coroas (`reinos/02-.../06-...md` §9) e o do Véu (`reinos/03-.../06-...md` §9), sem alterá-los, e se apoia em `14-camadas-de-informacao.md`, `13-misterios-limites.md`, `12-relogios.md`, doc 07 e `plano/CONTRATO_DE_FRONTEIRA_PALANCAR.md`. A separação em fixo, livre, registro e proibido é leitura editorial (ASSUMINDO).

### 9.1 Verificação antes de aceitar uma proposta

- Os lugares e entidades existem (registro mestre) ou foram criados como conteúdo novo permitido (§9.4)?
- A cronologia é compatível (doc 01 parte 3 e parte 5: Contagem dos Marcos; sem ano, era ou evento de Aeltharyn, das Coroas ou do Véu como fato do Vale)?
- O agente poderia saber disso (`12-conhecimento.csv`, camada do fato)?
- O agente tem meios e autoridade (competência e limite em `12-competencias.csv`; o Capitão não faz pacto nem cobra passagem de lugar fixo, a Assembleia não conduz marcha, o Trato não desfaz pacto: doc 04 §3)?
- A rota e o tempo são conhecidos? Toda duração do reino é `desconhecida`.
- A proposta transforma hipótese em fato sem evidência? (mistério: pelo menos duas provas independentes, §6)
- A consequência contradiz estado persistente, lei (L-01 a L-10), pacto ou decisão anterior?
- O segredo é revelado sem caminho de descoberta cumprido?
- A proposta trata um povo como selvagem, perigoso, vilão ou leal por natureza, ou usa "Fera" como insulto ou sentença? (contrato §5 item 7)

Se não puder ser confirmado, a IA reduz a precisão, mantém a incerteza ou pede aprofundamento. Não preenche lacuna com número exato nem fato definitivo para a cena fluir.

### 9.2 Quatro classes

| Classe | O que é | O que a IA faz |
|---|---|---|
| **Fixo** | Tudo o que já tem ID ou fonte: os 8 lugares (VP-01 a VP-08; quatro fixos e quatro sazonais), as 14 facções, as 25 pessoas do registro (12 principais, 7 sem ficha, 6 heróis lendários **falecidos**), os 8 relógios (CP-01 a CP-08), os 10 mistérios (MPL-01 a MPL-10) e o "a verdade não pode ser", as 10 leis (L-01 a L-10), os 12 eventos-tipo, as 24 sementes, os 12 nativos do catálogo, os 25 forasteiros, as 4 eras locais, a Assembleia de Clãs (oito assentos, um por Trato; Yorna sem voto), o Capitão de Marcha (Kharos Casco-de-Marcha, mandato limitado e perto do fim), os pactos de passagem e de pasto, as ligações do doc 07 **sem duração**, os recursos, o calendário, os 42 fatos e a camada de cada um, e as lacunas (`desconhecido`) | Usa; não altera nem contradiz. Lacuna continua lacuna |
| **Livre** | Detalhe de cena sem consequência persistente: poeira, capim, cheiro, objeto comum, figurante anônimo sem ID, fala dentro do tom (`12-voz.csv`) | Inventa sem registro, se não contradisser um fixo, não prometer recompensa e não criar dever, dívida ou mudança de estado |
| **Exige registro** | O que muda o mundo ou cria entidade: morte ou ferimento de pessoa com ID, escolha ou remoção do Capitão, pacto feito, renovado ou quebrado, Trato que muda de mãos, relógio que avança, NPC, clã, organização ou lugar recorrente novo, consequência de decisão do jogador, fato restrito ou secreto revelado | Pode **propor**; só vale depois de registrado com estado anterior, novo estado, momento, causa, agentes, quem sabe e grau de certeza (`reinos/01-.../02-arquitetura-mundo-persistente.md` §2) |
| **Proibido** | §9.3 | Não faz; reduz a precisão, mantém a incerteza ou pede aprofundamento |

### 9.3 Proibido (nenhuma proposta, nenhum registro da IA)

- nomear o sucessor de Kharos, dizer como ele foi escolhido, quantos Capitães houve ou qual é o limite do mandato; decidir entre prova e aclamação; resolver o interregno; dar ao Capitão poder de pacto ou de cobrança em lugar fixo; dar voz ou voto a Yorna; criar um segundo Capitão ou uma segunda Assembleia;
- decidir quem é o dono legítimo do Trato do Vau (Rasovau) ou do Trato da Encosta (Encosta-Aberta), ou quem cobra o desembarque (Praia-Pardo), a preferência na cisterna (Alvasal) ou o direito à reserva (Tabuleiro-Velho): todos `desconhecidos` (doc 04 §10);
- definir crime grave contra pessoa, exílio ou pena de morte (doc 04 §6: `desconhecido`);
- criar um 9º lugar, um 13º povo nativo ou ressuscitar um dos 6 heróis lendários; fazer feito de herói lendário virar fato;
- declarar guerra, aliança, tratado ou crise do Vale com outro reino; nomear inimigo do Vale (doc 01 parte 3 §5: `desconhecido`); fazer de goblins, Goblins de Legião ou qualquer povo "o inimigo" por raça;
- resolver um mistério ou escolher uma das hipóteses abertas (MPL-01 a MPL-10 e doc 01 parte 4);
- fazer de um povo, de uma raça ou de "Fera" um traço de selvageria, perigo, vilania ou lealdade; alocar mestiço a lugar, facção ou reino (D-64);
- inventar número de população, estoque, preço, tributo, distância, duração, limite de mandato ou de estações que o texto deixa desconhecido;
- conceder efeito mecânico por nome, título ou lore;
- revelar fato restrito ou secreto sem o caminho de descoberta cumprido, ou mudar a camada de um fato;
- avançar relógio sem gatilho observável (`12-relogios.csv`) ou por calendário; usar a agenda sazonal como prazo (doc 01 parte 5);
- decidir o Conflito G, explicar a Neblina Longa, o Mistério D ou o Mistério E; criar rota, tempo, passagem ou distância entre o Vale e Aeltharyn, as Coroas ou o Véu (inclusive Ermo de Sal ↔ Vale); fazer o Vale ir além do pé das elevações (L-09);
- fazer centauros, orcanos ou felídeos de Aeltharyn, ou `npc.oranna-passo-largo` e os demais de A4 e A5, agirem a mando do Vale, ou vice-versa; dar parentesco entre as linhagens do Vale e as de Aeltharyn (P-3);
- dar causa, número ou fim à Descida do Argente; dizer de onde vem o combustível ou o alimento de Brasalume; dar ao Véu causa vinda do Vale; criar fluxo de refugiados entre o Vale e outro reino;
- dar ano de Aeltharyn a evento do Vale ou sincronizar as eras locais com as de outro reino;
- usar nome do registro mestre dos reinos 1 a 3 (inclusive "Calar" e o prefixo "Passo-");
- afirmar algo sobre o Pacto Humano além de M-04 e M-05; escrever conteúdo do reino 5;
- alterar população, cargo, idade ou ID de entidade dos reinos 1, 2 e 3.

### 9.4 Casos pedidos

| Pedido | Regra |
|---|---|
| **Novo chefe, Capitão ou governante** | Proibido criar. O Vale tem um Capitão (Kharos) e uma Assembleia; troca só pelo costume do doc 04 §5, cujo desfecho, método e momento são `desconhecidos`; a IA não a conduz. Chefe de Trato com papel recorrente é proposta ao autor, nunca fato |
| **Novo lugar** | Proibido. Tenda, poço, pouso ou marco sem ID é detalhe livre enquanto não tiver função recorrente. Para virar entidade, entra como **proposta** ao autor com lugar-mãe (D-102 prevê oito lugares; a regra vale para qualquer proposta), Trato, povo do catálogo e fonte "proposto"; só vira ID depois de aprovada e registrada no registro mestre |
| **Guerra** | A IA não declara. Escalada local (disputa de pasto, cobrança contestada, vigia reforçada) pode ser proposta como oportunidade condicionada a gatilho observável, com custos; pacto quebrado não é guerra automática (perde-se a voz no Trato, L-02) |
| **NPC, clã ou organização nova** | Figurante sem ID: livre. Com papel recorrente: proposta com `npc.` ou `faction.` novo, lugar, facção-mãe (ou "instituição"), povo do catálogo e fonte "proposto"; nunca com segredo, relação ou conhecimento que mude um mistério; nome conferido contra os reinos 1 a 3 |
| **Morte ou ferimento de pessoa com ID** | Exige registro com causa e agentes; a IA não mata para dar peso à cena |
| **Rota ou tempo de viagem** | Proibido inventar: toda duração é `desconhecida` (doc 07). Pode dizer que o tempo é desconhecido |
| **Rumor sobre Aeltharyn (elevações ocidentais)** | Só em tom de rumor, como na cena g (`15-cenas-de-teste.md`); sem rota, sem tempo, sem decidir o Conflito G |
| **Descida do Argente e Rota do Sal** | Só o lado do Vale, em termos qualitativos, como na cena f; sem causa, sem número, sem origem de Brasalume, sem rota até Beira-Sal |

### 9.5 Teste rápido (4 perguntas, em ordem)

1. Já existe ID ou fonte? **Fixo**: usar como está.
2. Está na lista proibida? **Não fazer.**
3. Muda estado, cria entidade ou revela restrito ou secreto? **Propor e registrar** antes de tratar como fato.
4. Nenhuma das anteriores e sem consequência? **Livre.**

Em dúvida entre duas classes, vale a mais restritiva.

### 9.6 Decisão pendente

**D-132:** aprovar este contrato. Opções: **A.** aprovar (recomendada); **B.** mover item entre classes ou trocar caso (diga qual). **Aceita pela continuação (07/11/2026); reversível.**
