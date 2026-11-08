# Azeroth — Eventos-tipo e cadeias causais (doc 12, R5-5.2)

**Estado:** R5-5.2, D-162 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, lugar, facção, relógio, rota ou fato novo**. Dados em `12-eventos.csv` (10 colunas, molde do reino 4).
**Fonte:** docs 01, 04, 05, 06, 07 e 08; `12-relogios.csv`, `12-fichas.csv`. Os 10 relógios dão os gatilhos; as dependências DA-01 a DA-24 e as leis L-01 a L-10 dão os elos. Coluna `recursos`: termos qualitativos do doc 08.

## Regras

- Cada evento é uma **possibilidade causal**, não um fato nem uma obrigação. Só acontece com o **gatilho observável** da coluna (os dos relógios vêm de `12-relogios.csv`). Pistas e escolhas vêm antes das consequências graves.
- **Encadeamento: ASSUMINDO.** Cada elo repete algo já documentado (quem paga, ao completar, lei, dependência, conflito CFA, oportunidade OA); a ordem entre eles é leitura editorial.
- Só entram **rotas entre dois lugares de Azeroth** que existem no doc 07; toda duração segue `desconhecida`. Ligações "Pendente" (por exemplo Campoalvo ↔ Alvamar) não entram em nenhum evento.
- Nenhum evento envolve Aeltharyn, o Véu, as Coroas de Pedra ou o Vale Palancar, nem As Costas e As Fugas do Véu além do lado de cá. A precedência entre as coroas e as lacunas L-01 a L-10 seguem abertas.
- 12 eventos: 10 que completam um relógio (CA-01 a CA-10) e 2 que só fornecem gatilho (`colheita-fraca-na-ceifa`, `chegado-sem-registro-na-porta`).

## Eventos

### `event.medida-dita-em-voz-alta` — O governante do Trigal diz em voz alta quem ajusta a medida em ano ruim
- **Gatilho:** novo preço contestado observado; novo pedido de abertura observado
- **Relógio:** `clock.medida-em-ano-ruim`
- **Cadeia:** colheita fraca e pedido de abertura de celeiro recusado > o preço do pão é contestado em dia de pesagem (CFA-01) > Tomlin intermedeia no Salão das Medidas e o Conselho dos Celeiros reconhece a medida em sessão > o Cais diz que o excedente apodrece sem o mar e o Vento diz que o aviso chega tarde > o governante do Trigal, com o Conselho ouvido, precisa dizer quem ajusta a medida; a precedência não é decidida aqui (L-01 segue aberta).
- **Lugares:** `city.campoalvo`
- **Recursos (termos do doc 08):** grão e canais; celeiro comum; medida
- **Pessoas e facções:** `npc.odile-medida-cheia`, `npc.tomlin-horta-larga`, `faction.conselho-dos-celeiros`
- **Rotas:** —
- **Fonte:** doc 06 CA-01, CFA-01; doc 04 §2 e §10; doc 05 §1 (L-01); doc 08 DA-01, DA-02; encadeamento: ASSUMINDO

### `event.taxa-unica-da-barcaca` — Uma barcaça deixa de pagar duas taxas, ou a comporta se fecha
- **Gatilho:** nova barcaça cobrada duas vezes observada; nova comporta fechada sem aviso observada
- **Relógio:** `clock.passagem-dos-rios`
- **Cadeia:** a mesma barcaça é cobrada pelas duas coroas ao passar de um rio ao outro (CFA-02) > Garvane diz que a passagem não pode fechar e a Casa dos Barqueiros ameaça as comportas (L-02, aviso às duas coroas) > o interior vê o caminho do mar em risco (DA-03) > o apelo vai ao governante do Trigal e à Mesa, que arbitra só o ponto concreto > o governante do Trigal ou a Mesa precisa dizer quem taxa a barcaça; quem taxa o quê no geral não é decidido aqui.
- **Lugares:** `city.vaudouro`, `city.campoalvo`
- **Recursos (termos do doc 08):** comportas; passagem entre os rios; taxa
- **Pessoas e facções:** `npc.garvane-remo-firme`, `faction.casa-dos-barqueiros`, `faction.alfandega-das-mares`, `faction.conselho-dos-celeiros`, `npc.odile-medida-cheia`
- **Rotas:** `route.campoalvo-vaudouro`
- **Fonte:** doc 06 CA-02, CFA-02; doc 04 §3, §8 e §10; doc 05 §1 (L-02); doc 08 DA-03 a DA-05; doc 07 §2; encadeamento: ASSUMINDO

### `event.livro-relido-com-testemunha` — Guarnição e vigia leem juntas o mesmo registro
- **Gatilho:** novo registro contestado observado; nova abertura do limite contestada observada
- **Relógio:** `clock.livro-lido-de-dois-modos`
- **Cadeia:** um chegado é registrado no Livro dos Chegados (L-03) > guarnição e vigia leem o registro de modos diferentes (CFA-03) > Solenne relê as páginas diante de uma testemunha da vigia e o Vento reivindica a torre > o suprimento vindo de dentro atrasa e o apoio de Campoalvo pesa (DA-06, DA-08; valor desconhecido) > o comandante do Trigal precisa dizer, diante das duas partes, de que modo o registro é lido; as páginas sem explicação e a ordem original da vigia seguem desconhecidas.
- **Lugares:** `city.torrefronte`, `city.campoalvo`
- **Recursos (termos do doc 08):** suprimento de dentro do reino; registro de chegados
- **Pessoas e facções:** `npc.solenne-vigilia-longa`, `faction.guarnicao-do-limite`, `faction.posto-dos-pipeiros`
- **Rotas:** `route.campoalvo-torrefronte`
- **Fonte:** doc 06 CA-03, CFA-03; doc 04 §9 e §10; doc 05 §1 (L-03); doc 08 DA-06 a DA-08; doc 07 §2; encadeamento: ASSUMINDO

### `event.regra-de-descarga-no-molhe` — A Alfândega e a Guarda da Corrente pactuam o que entra e quem paga
- **Gatilho:** nova descarga fora do molhe observada; nova contestação pública da taxa observada
- **Relógio:** `clock.taxa-do-molhe`
- **Cadeia:** um navio descarrega fora do molhe (CFA-04) > a Alfândega discute se a taxa serve ao porto ou à Coroa > Dorgan registra o que entra pela boca da baía sem acusar ninguém (DA-11) > Corlan responde às contas da Alfândega e Rilla é cortejada por ela > o governante do Cais precisa dizer qual regra de descarga e de taxa vale; os navios fora do molhe seguem rumor, não ligação (MA-04).
- **Lugares:** `city.alvamar`
- **Recursos (termos do doc 08):** molhe; corrente; taxa do porto; rota e maré
- **Pessoas e facções:** `npc.corlan-cais-primeiro`, `npc.dorgan-escudo-lento`, `npc.rilla-espuma-fina`, `faction.alfandega-das-mares`, `faction.guarda-da-corrente`
- **Rotas:** —
- **Fonte:** doc 06 CA-04, CFA-04; doc 04 §9 e §10; doc 05 §1 (L-04); doc 08 DA-09 a DA-11; encadeamento: ASSUMINDO

### `event.luz-com-duas-coroas-avisadas` — A luz da barra é mantida pela Irmandade com as duas coroas avisadas
- **Gatilho:** nova ordem contestada observada; novo pedido de sinal observado
- **Relógio:** `clock.luz-entre-coroas`
- **Cadeia:** o Cais ordena aos faroleiros e o Vento pede os sinais da costa (CFA-05) > a luz fica entre duas disputas e uma noite sem luz é perigo (DA-12) > Nerella e a Irmandade mantêm a luz e avisam a barra a quem chega (L-05) > o Cais e o Vento são avisados de cada mudança e cobram a autoridade > o Cais ou o Vento precisa dizer de quem é a luz naquela noite; de quem são os faróis e os sinais da costa não é decidido aqui.
- **Lugares:** `city.enseada-vigia`, `city.alvamar`
- **Recursos (termos do doc 08):** farol; luz; barra
- **Pessoas e facções:** `npc.nerella-canto-da-barra`, `faction.irmandade-dos-faroleiros`, `faction.pilotos-da-enseada`, `faction.casa-dos-sinais`
- **Rotas:** `route.alvamar-enseada-vigia`
- **Fonte:** doc 06 CA-05, CFA-05; doc 04 §9 e §10; doc 05 §1 (L-05); doc 08 DA-12, DA-13; doc 07 §2; encadeamento: ASSUMINDO

### `event.canal-ou-jardim` — O Cais adia a obra do canal ou o jardim perde espaço
- **Gatilho:** novo pedido de obra observado; nova recusa de obra observada
- **Relógio:** `clock.canal-e-jardim`
- **Cadeia:** o Cais quer abrir mais um canal e os Guardas do Santuário dizem que isso mata o jardim (CFA-06) > os Construtores de Concha pedem canal maior para navios maiores e a madeira de fora pesa no estaleiro (DA-14) > Orrume e Corali olham o coral no Canal Estreito antes de qualquer obra (OA-06) > a Coroa do Cais, de quem o estaleiro depende, cobra o prazo (DA-16) > a Coroa do Cais precisa dizer se a obra anda ou se adia; se um canal maior fere o santuário não é decidido aqui (L-06 segue aberta).
- **Lugares:** `city.concharia`, `city.alvamar`
- **Recursos (termos do doc 08):** casco e vela; madeira de fora; canal; santuário e recifes
- **Pessoas e facções:** `faction.construtores-de-concha`, `faction.guardas-do-santuario`, `faction.alfandega-das-mares`, `npc.corali-fundo-de-coral`, `npc.orrume-abrigo-de-casco`
- **Rotas:** `route.alvamar-concharia`
- **Fonte:** doc 06 CA-06, CFA-06; doc 04 §9 e §10; doc 05 §1 (L-06); doc 08 DA-14 a DA-16; doc 07 §2; encadeamento: ASSUMINDO

### `event.aviso-de-codigo-combinado` — As outras coroas passam a ser avisadas quando um código muda
- **Gatilho:** nova mudança de código sem aviso observada; nova exigência de código observada
- **Relógio:** `clock.codigo-sem-aviso`
- **Cadeia:** um código de torre muda sem aviso (CFA-07) > as outras coroas temem depender de quem pode calar uma torre (DA-17) > a linha de torres entre Torreclara e Pouso-das-Brisas sente a mudança primeiro > Casimar entrega o aviso com o código limpo, sem abrir o código-mestre (OA-07) > o governante do Vento precisa dizer quem é avisado a cada mudança; o código-mestre e quem o guarda além dele seguem desconhecidos.
- **Lugares:** `city.torreclara`, `city.pouso-das-brisas`
- **Recursos (termos do doc 08):** aviso e hora certa; códigos de torre; linha de torres
- **Pessoas e facções:** `npc.brevo-rumo-alto`, `npc.astrel-noite-clara`, `faction.casa-dos-sinais`, `faction.posto-dos-pipeiros`
- **Rotas:** `route.torreclara-pouso-das-brisas`
- **Fonte:** doc 06 CA-07, CFA-07; doc 04 §9 e §10; doc 05 §1 (L-07); doc 08 DA-17, DA-18, DA-21; doc 07 §2; encadeamento: ASSUMINDO

### `event.duas-leituras-do-ceu-na-mesa` — A Mesa dos Astros recebe as duas leituras do mesmo céu
- **Gatilho:** nova previsão divergente observada; nova discordância pública observada
- **Relógio:** `clock.leitura-do-ceu`
- **Cadeia:** Estelares e Luminares dão previsões diferentes (CFA-08) > cada coroa pede a previsão que lhe serve > Eloh leva as duas leituras à Mesa dos Astros sem escolher uma (OA-08) > a Coroa do Vento, que sustenta o arquivo, vê a autoridade em jogo (DA-19, DA-20) > a Mesa dos Astros, sob o governante do Vento, precisa dizer que previsão sai; quem interpreta o céu não é decidido aqui (L-08 segue aberta).
- **Lugares:** `city.estrelado`, `city.torreclara`
- **Recursos (termos do doc 08):** arquivo celeste; calendário de rota; previsão
- **Pessoas e facções:** `npc.astrel-noite-clara`, `faction.leitores-do-ceu`, `faction.vigilia-da-cupula`, `npc.brevo-rumo-alto`
- **Rotas:** `route.torreclara-estrelado`
- **Fonte:** doc 06 CA-08, CFA-08; doc 04 §9 e §10; doc 05 §1 (L-08); doc 08 DA-19, DA-20; doc 07 §2; encadeamento: ASSUMINDO

### `event.sinal-dividido-na-linha` — Um horário ou fio da linha de torres é dividido por acordo
- **Gatilho:** novo pedido de sinal próprio observado; nova recusa observada
- **Relógio:** `clock.sinais-proprios`
- **Cadeia:** o Trigal e o Cais pedem sinais próprios (CFA-09) > o Posto dos Pipeiros recusa por serem da linha do Vento > a linha precisa de vento bom e de torres inteiras (DA-21) > Hollis solta uma pipa de teste e anota quem responde, sem dar sinal a coroa alguma (OA-09) > o governante do Vento precisa dizer se a linha divide horário ou fio; de quem são os sinais não é decidido aqui.
- **Lugares:** `city.pouso-das-brisas`, `city.torreclara`
- **Recursos (termos do doc 08):** linha de torres; sinal e pouso
- **Pessoas e facções:** `npc.brevo-rumo-alto`, `faction.posto-dos-pipeiros`, `faction.casa-dos-sinais`, `npc.casimar-codigo-limpo`
- **Rotas:** `route.torreclara-pouso-das-brisas`
- **Fonte:** doc 06 CA-09, CFA-09; doc 04 §9 e §10; doc 05 §1; doc 08 DA-21, DA-22; doc 07 §2; encadeamento: ASSUMINDO

### `event.prazo-e-sustento-do-chegado` — A Mesa de Meiovau diz prazo e sustento de um chegado
- **Gatilho:** novo chegado contestado observado; novo pedido de prazo ou sustento observado
- **Relógio:** `clock.acolhida-de-meiovau`
- **Cadeia:** um chegado novo sem registro bate à porta > a Mesa Estreita pede prazo antes do sustento e o Albergue pede acolhida larga (CFA-10) > Tolla ordena a audiência pelo Costume da Porta (L-10) > Auriven sustenta o albergue com doação > a Mesa de Meiovau, com as três chaves (L-09), precisa dizer prazo e sustento de um chegado; os pontos em aberto de L-10 não são decididos aqui e ninguém é culpado por origem.
- **Lugares:** `city.meiovau`
- **Recursos (termos do doc 08):** acolhida; doação; as três chaves
- **Pessoas e facções:** `npc.tolla-mesa-posta`, `npc.auriven-porta-acesa`, `faction.albergue-dos-chegados`, `faction.mesa-estreita`
- **Rotas:** —
- **Fonte:** doc 06 CA-10, CFA-10; doc 04 §3, §7 e §10; doc 05 §1 (L-09, L-10) e §4; doc 08 DA-23, DA-24; encadeamento: ASSUMINDO

### `event.colheita-fraca-na-ceifa` — Colheita fraca na Ceifa pesa sobre o celeiro e o apoio ao limite
- **Gatilho:** colheita fraca observada; novo pedido de abertura observado
- **Relógio:** `clock.medida-em-ano-ruim`
- **Cadeia:** colheita fraca observada na Ceifa > o Conselho dos Celeiros segura o excedente (DA-01) > o apoio de Campoalvo a Torrefronte pesa mais (DA-08; o que entrega é desconhecido) > Solenne sente o suprimento atrasar > só fornece gatilho: novo pedido de abertura de celeiro ou novo preço contestado pode então avançar o relógio; sem ele nada muda.
- **Lugares:** `city.campoalvo`, `city.torrefronte`
- **Recursos (termos do doc 08):** grão e canais; suprimento de dentro do reino
- **Pessoas e facções:** `npc.odile-medida-cheia`, `npc.tomlin-horta-larga`, `faction.conselho-dos-celeiros`, `npc.solenne-vigilia-longa`
- **Rotas:** `route.campoalvo-torrefronte`
- **Fonte:** doc 06 CA-01 (gatilho); doc 01 parte 5 §4; doc 08 DA-01, DA-08; doc 07 §2; encadeamento: ASSUMINDO

### `event.chegado-sem-registro-na-porta` — Um chegado sem registro bate à porta do Albergue
- **Gatilho:** novo chegado contestado observado
- **Relógio:** `clock.acolhida-de-meiovau`
- **Cadeia:** um chegado novo sem registro bate à porta do Albergue dos Chegados > Auriven o hospeda sem perguntar o motivo > a Mesa Estreita pede registro e prazo antes de qualquer sustento > só fornece gatilho: novo chegado contestado observado pode então avançar CA-10; sem contestação nada muda e a Mesa não decide.
- **Lugares:** `city.meiovau`
- **Recursos (termos do doc 08):** acolhida; doação
- **Pessoas e facções:** `npc.tolla-mesa-posta`, `npc.auriven-porta-acesa`, `faction.albergue-dos-chegados`, `faction.mesa-estreita`
- **Rotas:** —
- **Fonte:** doc 06 CA-10 (gatilho); doc 04 §7 e §10; doc 05 §4; encadeamento: ASSUMINDO

## Conferências

| Verificação | Resultado |
|---|---|
| Eventos no CSV | 12 (10 completam relógio, 2 só dão gatilho) |
| Relógio de cada evento existe em `12-relogios.csv` | 12 de 12 |
| Lugar, pessoa ou facção fora do registro mestre | 0 |
| Rota fora do doc 07 | 0 (só 6 ligações documentadas entre dois lugares usadas; 0 "Pendente") |
| Evento que envolve outro reino | 0 |
| Campo vazio | 0 |
| ID de evento igual a ID dos reinos 1 a 4 | 0 |

## Decisão pendente

**D-162:** aprovar os 12 eventos-tipo e as cadeias. Opções: **A.** aprovar; **B.** trocar elo, gatilho ou evento (diga qual). **Recomendação: A.**
